package cn.td.geotags.job;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.multipart.MultipartFile;

import cn.td.geotags.task.CellAroundTasklet;
import cn.td.geotags.task.GatherPointTasklet;
import cn.td.geotags.task.PoisAroundTasklet;
import cn.td.geotags.task.TownshipTasklet;
import cn.td.geotags.util.BigFileMD5;
import cn.td.geotags.util.Contants;

import org.apache.commons.lang3.tuple.ImmutablePair;
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
	
	@Autowired
	private GatherPointTasklet gatherPoint;
	
	@Autowired
	private Environment env;
	
	public JobState runCellAroundJob(String jobName, JobParameters params) {
		Function<String, Job> f = j -> jobBuilder.get(j)
				.start(stepBuilder.get(Contants.BIZ_CELL_AROUND).tasklet(this.cellAround).build())
				.build();
		
		ImmutablePair<String, JobParameters> p = ImmutablePair.of(jobName, params);
		return runJob(p, f);
	}
	
	public JobState runTownshipJob(String jobName, JobParameters params) {
		Function<String, Job> f = j -> jobBuilder.get(j)
				.start(stepBuilder.get(Contants.BIZ_TOWNSHIP).tasklet(this.township).build())
				.build();
		
		ImmutablePair<String, JobParameters> p = ImmutablePair.of(jobName, params);
		return runJob(p, f);
	}
	
	public JobState runPoiAroundJob(String jobName, JobParameters params) {
		Function<String, Job> f = j -> jobBuilder.get(j)
				.start(stepBuilder.get(Contants.BIZ_POI_AROUND).tasklet(this.poisAround).build())
				.build();
		
		ImmutablePair<String, JobParameters> p = ImmutablePair.of(jobName, params);
		return runJob(p, f);
	}

	public JobState runGatherPointJob(String jobName, JobParameters params) {
		Function<String, Job> f = j -> jobBuilder.get(j)
				.start(stepBuilder.get(Contants.BIZ_GP_TO_POI_AROUND).tasklet(this.gatherPoint).build())
				.build();
		
		ImmutablePair<String, JobParameters> p = ImmutablePair.of(jobName, params);
		return runJob(p, f);
	}

	public ImmutablePair<String, String> getInputAndOutputFilePath(MultipartFile file, String inputType, String outputType) throws IllegalStateException, IOException {
		String md5 = BigFileMD5.getMD5((FileInputStream)file.getInputStream());
		String inputFilePath = env.getProperty("file.in.dir") + "/" + inputType + "-" + md5 + Contants.FILE_EXT_DAT;
		String outputFilePath = env.getProperty("file.out.dir") + "/" + outputType + "-" + md5 + Contants.FILE_EXT_CSV;

		File f = new File(inputFilePath);
		if (!f.exists()) {
			file.transferTo(f);
		}
		return ImmutablePair.of(inputFilePath, outputFilePath);
	}

//	public String getOutputFilePath(String outputType) {
//		String test = env.getProperty("file.out.dir") + "/" + outputType + "-" + UUID.randomUUID().toString().replace("-", "") + ".txt";
//		return test;
//	}

	private JobState runJob(ImmutablePair<String, JobParameters> p, Function<String, Job> f) {
		String jobName = p.getLeft();
		JobParameters jobParameters = p.getRight();

		JobState jobState = new JobState();
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

		jobState.setJobStatus(jobExecution.getStatus().name());
		jobState.setJobName(jobExecution.getJobInstance().getJobName());
		jobState.setJobId(jobExecution.getJobId());
		jobState.setStartTime(jobExecution.getStartTime() == null ? "" : jobExecution.getStartTime().toString());
		jobState.setEndTime(jobExecution.getEndTime() == null ? "" : jobExecution.getStartTime().toString());
		
		Map<String, Object> params = p.getRight()
				.getParameters()
				.entrySet()
				.stream()
				.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().getValue()));
		
		jobState.setParams(params);
		return jobState;
	}
}
