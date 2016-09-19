package cn.td.geotags.task;

import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;

import cn.td.geotags.util.Contants;

public class CompressFileTasklet implements Tasklet, Monitorable {
	@Autowired
	private TaskConfig taskConfig;
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		Map<String, Object> params = chunkContext.getStepContext().getJobParameters();
		String outputType = (String) params.get(Contants.PARAM_REQ_TYPE);
		long jobId = chunkContext.getStepContext().getStepExecution().getJobExecutionId();
//		long taskId = chunkContext.getStepContext().getStepExecution().getId();

		String filePath = taskConfig.getOutputFilePath(jobId, outputType);
		String zipPath = taskConfig.getCompressedOutputFilePath(jobId, outputType);

		FileSystemResource file = new FileSystemResource(filePath);
		FileSystemResource zipFile = new FileSystemResource(zipPath);

//		/*
//		 * 监控: 处理前, 记录下时间戳
//		 */
//		monitorProcessTimeAt(jobId, 
//				taskId, 
//				Contants.MONITOR_TASK_STAGE_COMPRESS_BEGIN,
//				new Date());
		
		if (zipFile.exists()) {
			zipFile.getFile().delete();
		}
		
		InputStream input = file.getInputStream();
		ZipOutputStream zipOut = new ZipOutputStream(zipFile.getOutputStream());
		zipOut.putNextEntry(new ZipEntry(file.getFilename()));
		
		int temp = 0;
		while ((temp = input.read()) != -1) {
			zipOut.write(temp);
		}
		
		input.close();
		zipOut.close();
		
		if (file.exists()) {
			file.getFile().delete();
		}
		
//		monitorFileSize(jobId, taskId, Contants.MONITOR_TASK_STAGE_COMPRESSED_FILE, zipPath);
		
//		/*
//		 * 监控: 处理完毕，记录下时间戳
//		 */
//		monitorProcessTimeAt(jobId, 
//				taskId, 
//				Contants.MONITOR_TASK_STAGE_COMPRESS_END,
//				new Date());
		return RepeatStatus.FINISHED;
	}
}
