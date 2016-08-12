package cn.td.geotags.task;

import java.util.Map;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import cn.td.geotags.biz.PoiAround;

public class PoisAroundTasklet implements Tasklet {
	@Autowired
	private PoiAround poiAround;
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		Map<String, Object> params = chunkContext.getStepContext().getJobParameters();
		String inputFileName = (String) params.get("inFile");
		String outputFileName = (String) params.get("outFile");

		String types = (String) params.get("types");
		long radius = Long.parseLong(params.get("radius").toString());
		
		poiAround.calc(types, radius, inputFileName, outputFileName);
		
		return RepeatStatus.FINISHED;
	}
}
