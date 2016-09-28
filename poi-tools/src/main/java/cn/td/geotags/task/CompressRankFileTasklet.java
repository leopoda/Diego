package cn.td.geotags.task;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import static java.util.stream.Collectors.toList;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import cn.td.geotags.util.Contants;
import cn.td.geotags.util.PoiUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CompressRankFileTasklet implements Tasklet  {
	@Autowired
	private TaskConfig taskConfig;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		Map<String, Object> params = chunkContext.getStepContext().getJobParameters();
		String outputType = (String) params.get(Contants.PARAM_REQ_TYPE);
		long jobId = chunkContext.getStepContext().getStepExecution().getJobExecutionId();

		String types = (String) params.get(Contants.PARAM_TYPES);
		String filePathTemplate = taskConfig.getOutputFilePathTemplate(jobId, outputType);
		String zipPath = taskConfig.getCompressedOutputFilePath(jobId, outputType);

		FileSystemResource zipFile = new FileSystemResource(zipPath);
		if (zipFile.exists()) {
			zipFile.getFile().delete();
		}
		ZipOutputStream zipOut = new ZipOutputStream(zipFile.getOutputStream());

		List<String> poiTypeCode = Arrays.asList(types.split("\\|")).stream().collect(toList());
		poiTypeCode.add(Contants.POI_RANK_TYPE_CODE_BIND_ALL);
		poiTypeCode.stream()
			  .map(o -> PoiUtil.getName(o))
			  .forEach(o -> {
					try {
						FileSystemResource file = new FileSystemResource(String.format(filePathTemplate, o));
						InputStream input = file.getInputStream();
						zipOut.putNextEntry(new ZipEntry(file.getFilename()));

						int temp = 0;
						while ((temp = input.read()) != -1) {
							zipOut.write(temp);
						}

						input.close();
						if (file.exists()) {
							file.getFile().delete();
						}
					} catch (Exception e) {
						log.error("compress multi files failed", e);
					}
			  });
		
		// 删除中间文件前压入zip包, 可用于数据稽核
		String tmpFilePath = taskConfig.getOutputFilePath(jobId, outputType);
		FileSystemResource tmpFile = new FileSystemResource(tmpFilePath);
		if (tmpFile.exists()) {
			InputStream is = tmpFile.getInputStream();
			zipOut.putNextEntry(new ZipEntry(tmpFile.getFilename().replace(Contants.REQ_RANK, Contants.REQ_POI))); // 更改名字, 避免歧义
			int buf = 0;
			while ((buf = is.read()) != -1) {
				zipOut.write(buf);
			}
			is.close();
			tmpFile.getFile().delete();
		}
		
		zipOut.close();
		return RepeatStatus.FINISHED;
	}
}
