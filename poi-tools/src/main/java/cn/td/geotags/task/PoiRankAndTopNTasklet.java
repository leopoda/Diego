package cn.td.geotags.task;

import java.util.Arrays;
import java.util.Map;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.td.geotags.biz.GatherPointAroundRank;
import cn.td.geotags.domain.RankCondition;
import cn.td.geotags.util.Contants;

@Component
public class PoiRankAndTopNTasklet implements Tasklet {
	@Autowired
	private GatherPointAroundRank rank;
	
	@Autowired
	private TaskConfig taskConfig;
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		Map<String, Object> params = chunkContext.getStepContext().getJobParameters();

		String outputType = (String) params.get(Contants.PARAM_REQ_TYPE);
		long jobId = chunkContext.getStepContext().getStepExecution().getJobExecutionId();
		String types = (String) params.get(Contants.PARAM_TYPES);
		String inputFileName = taskConfig.getOutputFilePath(jobId, outputType); // 上一个 task 的输出文件, 是这个 task 的输入文件
		
		String rankString = (String)params.get(Contants.PARAM_RANK_CONDITION);
		if (rankString == null || rankString.isEmpty()) {
			throw new RuntimeException("param error: rankCondition");
		}
		ObjectMapper mapper = new ObjectMapper();
		RankCondition rankCondition = mapper.readValue(rankString, RankCondition.class);
		
		if (rankCondition != null) {
			rankCondition.setRankInputFile(inputFileName);
			rankCondition.setRankOutputPrefix(taskConfig.getOutputFilePathTemplate(jobId, outputType));
			rankCondition.setPoiTypeCode(Arrays.asList(types.split("\\|")));
			rankCondition.setTopN(Contants.PARAM_RANK_TOP_N);
			
			rank.calc(rankCondition);
		}
		return RepeatStatus.FINISHED;
	}
}
