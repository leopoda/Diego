package cn.td.geotags.job;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.multipart.MultipartFile;

import cn.td.geotags.task.CellAroundTasklet;
import cn.td.geotags.task.GatherPointTasklet;
import cn.td.geotags.task.PoisAroundTasklet;
import cn.td.geotags.task.TaskConfig;
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
	
	@Autowired
	private JobDao jobDao;
	
	@Autowired
	private TaskConfig taskConfig;
	
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

	public String getInputFilePath(MultipartFile file, String inputType) throws IllegalStateException, IOException {
		String tmpUUID = env.getProperty("file.in.dir") + "/" + UUID.randomUUID();

		File tmpFile = new File(tmpUUID);
		file.transferTo(tmpFile);
		
		String inputFilePath = "";
		if (tmpFile.exists()) {
			String md5 = BigFileMD5.getMD5(tmpFile);
			inputFilePath = env.getProperty("file.in.dir") + "/" + inputType + "-" + md5 + Contants.FILE_EXT_DAT;
			
			File f = new File(inputFilePath);
			if (!f.exists()) {
				tmpFile.renameTo(f);
			} else {
				tmpFile.delete();
			}
		}
		
		if (inputFilePath == null || inputFilePath.equals("")) {
			throw new RuntimeException("arrange input file failed");
		}
		return inputFilePath;
	}

	public FileSystemResource getJobOutput(long jobId) {
		JobExecution jobExecution = jobDao.getJobExecution(jobId);
		if (jobExecution != null) {
			if (jobExecution.getStatus() != BatchStatus.COMPLETED) {
				throw new RuntimeException("this job is not COMPLETED yet");
			}

			JobParameters jobParameters = jobExecution.getJobParameters();
			
			String outputType = jobParameters.getString(Contants.PARAM_REQ_TYPE);
			String result = taskConfig.getOutputFilePath(jobId, outputType);
			
			FileSystemResource  fileSystemResource = new FileSystemResource(result);
			return fileSystemResource;
		} else {
			throw new RuntimeException("invalid job ID");
		}
	}
	
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
