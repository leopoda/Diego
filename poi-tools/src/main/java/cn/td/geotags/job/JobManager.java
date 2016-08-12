package cn.td.geotags.job;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;

import cn.td.geotags.task.CellAroundTasklet;
import cn.td.geotags.task.PoisAroundTasklet;
import cn.td.geotags.task.TownshipTasklet;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JobManager {
	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private JobBuilderFactory jobBuilder;

	@Autowired
	private StepBuilderFactory stepBuilder;

	@Autowired
	private JobRepository jobRepo;

	@Autowired
	private CellAroundTasklet cellAround;
	
	@Autowired
	private TownshipTasklet township;
	
	@Autowired
	private PoisAroundTasklet poisAround;
	
	public JobStatus runCellAroundJob(String jobName, JobParameters params) {
		Function<String, Job> f = j -> jobBuilder.get(j)
				.start(stepBuilder.get("CellAround").tasklet(this.cellAround).build())
				.build();
		
		ImmutablePair<String, JobParameters> p = ImmutablePair.of(jobName, params);
		return runJob(p, f);
	}
	
	public JobStatus runTownshipJob(String jobName, JobParameters params) {
		Function<String, Job> f = j -> jobBuilder.get(j)
				.start(stepBuilder.get("Township").tasklet(this.township).build())
				.build();
		
		ImmutablePair<String, JobParameters> p = ImmutablePair.of(jobName, params);
		return runJob(p, f);
	}
	
	public JobStatus runPoiAroundJob(String jobName, JobParameters params) {
		Function<String, Job> f = j -> jobBuilder.get(j)
				.start(stepBuilder.get("PoiAround").tasklet(this.poisAround).build())
				.build();
		
		ImmutablePair<String, JobParameters> p = ImmutablePair.of(jobName, params);
		return runJob(p, f);
	}

	private JobStatus runJob(ImmutablePair<String, JobParameters> p, Function<String, Job> f) {
		String jobName = p.getLeft();
		JobParameters jobParameters = p.getRight();

		JobStatus jobStatus = new JobStatus();
		JobExecution jobExecution;
		if (!jobRepo.isJobInstanceExists(jobName, jobParameters)) {
			try {
				jobExecution  = jobLauncher.run(f.apply(jobName), jobParameters);
			} catch (Exception e) {
				log.error("submit job failed", e);
				throw new RuntimeException(e);
			}
		} else {
			jobExecution = jobRepo.getLastJobExecution(jobName, jobParameters);
		}

		jobStatus.setJobStatus(jobExecution.getStatus().name());
		jobStatus.setJobName(jobExecution.getJobInstance().getJobName());
		jobStatus.setJobId(jobExecution.getJobId());
		jobStatus.setStartTime(jobExecution.getStartTime() == null ? "" : jobExecution.getStartTime().toString());
		jobStatus.setEndTime(jobExecution.getEndTime() == null ? "" : jobExecution.getStartTime().toString());
		
		Map<String, Object> params = p.getRight()
				.getParameters()
				.entrySet()
				.stream()
				.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().getValue()));
		
		jobStatus.setParams(params);
		return jobStatus;
	}
}
