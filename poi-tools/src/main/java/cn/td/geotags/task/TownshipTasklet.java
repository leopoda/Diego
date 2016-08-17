package cn.td.geotags.task;

import java.util.Map;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import cn.td.geotags.biz.Township;
import cn.td.geotags.util.Contants;

public class TownshipTasklet implements Tasklet {
	@Autowired
	private Township township;

	@Autowired
	private TaskConfig taskConfig;
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		Map<String, Object> params = chunkContext.getStepContext().getJobParameters();
		String inputFile = (String) params.get(Contants.PARAM_IN_FILE);
		
		String outputType = (String) params.get(Contants.PARAM_REQ_TYPE);
		long jobId = chunkContext.getStepContext().getStepExecution().getJobExecutionId();
		
		township.calc(inputFile, taskConfig.getOutputFilePath(jobId, outputType));
		return RepeatStatus.FINISHED;
	}
}
