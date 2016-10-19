package cn.td.geotags.task;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import cn.td.geotags.biz.CoordinateAround;
import cn.td.geotags.util.Contants;
import cn.td.geotags.util.GatherPointCounter;

public class CoordinateAroundTasklet implements Tasklet {
	@Autowired
	private CoordinateAround poiAround;
	
	@Autowired
	private TaskConfig taskConfig;

	@Autowired
	private Environment env;
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		Map<String, Object> params = chunkContext.getStepContext().getJobParameters();
		String inputFileName = (String) params.get(Contants.PARAM_IN_FILE);

		String outputType = (String) params.get(Contants.PARAM_REQ_TYPE);
		long jobId = chunkContext.getStepContext().getStepExecution().getJobExecutionId();
		String jobName = chunkContext.getStepContext().getJobName();

		String types = (String) params.get(Contants.PARAM_TYPES);
		long radius = Long.parseLong(params.get(Contants.PARAM_RADIUS).toString());
		String coordsys = (String) params.get(Contants.PARAM_COORD_SYS);

		long num = GatherPointCounter.countGeoPoint(inputFileName, 1); // 0 - gp, 1 - co
		long threshold = Long.parseLong(env.getProperty("gp.count.threshold"));
		
		if (threshold > 0 && num > threshold) {
			throw new RuntimeException("提交的文件超过资源使用限定的阈值.");
		}
		
		Map<String, Object> additional = new ConcurrentHashMap<>();
		additional.putIfAbsent(Contants.ADDITIONAL_KEY_JOB, jobName + "-" + jobId);
		poiAround.calc(types, radius, coordsys, inputFileName, taskConfig.getOutputFilePath(jobId, outputType), additional);
		return RepeatStatus.FINISHED;
	}
}
