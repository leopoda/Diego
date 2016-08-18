package cn.td.geotags.job;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import java.util.Comparator;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.beans.factory.annotation.Autowired;

import cn.td.geotags.util.Contants;

public class JobDao {
	@Autowired
	private JobInstanceDao jobInstanceDao;
	
	@Autowired
	private JobExecutionDao jobExecutionDao;
	
//	@Autowired
//	private StepExecutionDao stepExecutionDao;
//	
//	@Autowired
//	private ExecutionContextDao executionContextDao;
	
	private JobState getJobState(JobExecution jobExecution) {
		JobState jobState = new JobState();
		JobInstance jobInstance = jobInstanceDao.getJobInstance(jobExecution);
		
		jobState.setJobId(jobExecution.getId());
		jobState.setJobStatus(jobExecution.getStatus().toString());
		jobState.setStartTime(jobExecution.getStartTime() == null ? "" : jobExecution.getStartTime().toString());
		jobState.setEndTime(jobExecution.getEndTime() == null ? "" : jobExecution.getEndTime().toString());
//		jobState.setJobName(jobExecution.getJobInstance() == null ? "" : jobExecution.getJobInstance().getJobName());
		jobState.setJobName(jobInstance == null ? "" : jobInstance.getJobName());

		JobParameters jobParameters = jobExecution.getJobParameters();
		Map<String, Object> params = jobParameters.getParameters()
				.entrySet()
				.stream()
				.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().getValue()));
		
		jobState.setParams(params);
		return jobState;
	}
	
	public JobResult queryJobs(int offset, int pageNum, String reqType, String search) {
		List<ImmutablePair<String, List<JobExecution>>> jobExecutionList = getJobExecution();
		Comparator<JobState> byJobId = (j1, j2) -> Long.compare(j2.getJobId(), j1.getJobId());
		
		JobResult jobResult = new JobResult();
		jobResult.setCount(jobExecutionList.size());
		jobResult.setOffset(offset);
		jobResult.setPageNum(pageNum);

		List<JobState> list = jobExecutionList.stream()
				.map(p -> p.getRight())
				.flatMap(o -> o.stream())
				.map(o -> getJobState(o))
				.filter(o -> o.getParams().get(Contants.PARAM_REQ_TYPE).equals(reqType))
				.filter(o -> (search != null && !search.equals("")) ? o.getJobName().contains(search) : true)
				.collect(toList());
				
		int count = list.size();
		jobResult.setPageTotal(count % offset > 0 ? (1 + count / offset) : (count / offset));
				
		jobResult.setJobList(
				list.stream()
					.sorted(byJobId)
					.skip((pageNum - 1) * offset)
					.limit(offset)
					.collect(toList()));
		
		return jobResult;
	}
	
	public JobExecution getJobExecution(long jobId) {
		return jobExecutionDao.getJobExecution(jobId);
	}
	
	public JobState getJobState(long jobId) {
		JobExecution jobExecution = getJobExecution(jobId);
		if (jobExecution == null) {
			throw new RuntimeException("invalid job id");
		}
		
		return getJobState(jobExecution);
	}

	private List<ImmutablePair<String, List<JobExecution>>> getJobExecution() {
		List<String> jobNames = jobInstanceDao.getJobNames();
		return jobNames.stream()
				.map(jobName -> {
					try {
						return ImmutablePair.of(jobName, jobInstanceDao.getJobInstanceCount(jobName));
					} catch (NoSuchJobException e) {
						return ImmutablePair.of(jobName, 0);
					}
				})
				.filter(p -> p.right > 0)
				.map(p -> ImmutablePair.of(p.left, jobInstanceDao.getJobInstances(p.left, 0, p.right)))
				.map(p -> ImmutablePair.of(p.left, p.right))
				.map(p -> ImmutablePair.of(p.left, jobInstanceToLastJobExecution(p.right)))
				.collect(toList());
	}

	private List<JobExecution> jobInstanceToLastJobExecution(List<JobInstance> jobInstances) {
		return jobInstances.stream()
				.map(o -> jobExecutionDao.getLastJobExecution(o))
				.collect(toList());
	}
}
