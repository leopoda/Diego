package cn.td.geotags.task;

import java.util.Map;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.td.geotags.biz.GatherPointAround;
import cn.td.geotags.util.Contants;

@Component
public class GatherPointAroundTasklet implements Tasklet {
	@Autowired
	private GatherPointAround gatherPointAround;

	@Autowired
	private TaskConfig taskConfig;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		Map<String, Object> params = chunkContext.getStepContext().getJobParameters();
		String inputFileName = (String) params.get(Contants.PARAM_IN_FILE);

		String outputType = (String) params.get(Contants.PARAM_REQ_TYPE);
		long jobId = chunkContext.getStepContext().getStepExecution().getJobExecutionId();

		String types = (String) params.get(Contants.PARAM_TYPES);
		long radius = Long.parseLong(params.get(Contants.PARAM_RADIUS).toString());
		String coordsys = (String) params.get(Contants.PARAM_COORD_SYS);
		
		gatherPointAround.calc(types, radius, coordsys, inputFileName, taskConfig.getOutputFilePath(jobId, outputType));
		return RepeatStatus.FINISHED;
	}
}