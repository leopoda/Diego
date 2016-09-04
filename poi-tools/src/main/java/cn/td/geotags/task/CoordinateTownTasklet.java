package cn.td.geotags.task;

import java.util.Date;
import java.util.Map;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import cn.td.geotags.biz.CoordinateTown;
import cn.td.geotags.util.Contants;

public class CoordinateTownTasklet implements Tasklet, Monitorable {
	@Autowired
	private CoordinateTown township;

	@Autowired
	private TaskConfig taskConfig;
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		Map<String, Object> params = chunkContext.getStepContext().getJobParameters();
		String inputFile = (String) params.get(Contants.PARAM_IN_FILE);
		
		String outputType = (String) params.get(Contants.PARAM_REQ_TYPE);
		long jobId = chunkContext.getStepContext().getStepExecution().getJobExecutionId();
		long taskId = chunkContext.getStepContext().getStepExecution().getId();
		String coordsys = (String) params.get(Contants.PARAM_COORD_SYS);
		
		/*
		 * 监控: 处理前, 记录下时间戳
		 */
		monitorProcessTimeAt(jobId, 
				taskId, 
				Contants.MONITOR_TASK_STAGE_START,
				new Date());

		monitorFileSize(jobId, taskId, Contants.MONITOR_TASK_STAGE_INPUT_FILE, inputFile);
		township.calc(coordsys, inputFile, taskConfig.getOutputFilePath(jobId, outputType));
		
		/*
		 * 监控: 处理完毕，记录下时间戳
		 */
		monitorProcessTimeAt(jobId, 
				taskId, 
				Contants.MONITOR_TASK_STAGE_END,
				new Date());
		return RepeatStatus.FINISHED;
	}
}
