package cn.td.geotags.task;

import java.util.Map;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import cn.td.geotags.biz.Township;

public class TownshipTasklet implements Tasklet {

	@Autowired
	Township township;
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		Map<String, Object> params = chunkContext.getStepContext().getJobParameters();

		String inputFile = (String) params.get("inFile");
		String outputFile = (String) params.get("outFile");
		
		township.calc(inputFile, outputFile);
		return RepeatStatus.FINISHED;
	}

}
