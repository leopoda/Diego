package cn.td.geotags.job;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;

import cn.td.geotags.task.CoordinateCellTasklet;
import cn.td.geotags.task.GatherPointTownTasklet;
import cn.td.geotags.task.PoiRankAndTopNTasklet;
import cn.td.geotags.task.CoordinateAroundTasklet;
import cn.td.geotags.task.TaskConfig;
import cn.td.geotags.task.CoordinateTownTasklet;
import cn.td.geotags.task.GatherPointAroundTasklet;
import cn.td.geotags.task.CompressFileTasklet;
import cn.td.geotags.task.CompressRankFileTasklet;
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
	private CoordinateCellTasklet coordinateCell;
	
	@Autowired
	private CoordinateTownTasklet coordinateTown;
	
	@Autowired
	private CoordinateAroundTasklet coordinateAround;
	
	@Autowired
	private GatherPointTownTasklet gatherPointTown;
	
	@Autowired
	private GatherPointAroundTasklet gatherPointAround;
	
	@Autowired
	private CompressFileTasklet zipTasklet;
	
	@Autowired
	private CompressRankFileTasklet compressMultiFile;
	
	@Autowired
	private PoiRankAndTopNTasklet poiRankAndTopN;
	
	@Autowired
	private Environment env;
	
	@Autowired
	private JobDao jobDao;
	
	@Autowired
	private TaskConfig taskConfig;
	
	public JobState runCoordinateCellJob(String jobName, JobParameters params) {
		Function<String, Job> f = j -> jobBuilder.get(j)
				.start(stepBuilder.get(Contants.BIZ_CELL_NEARBY).tasklet(this.coordinateCell).build())
				.next(stepBuilder.get(Contants.BIZ_COMPRESS_ZIP).tasklet(this.zipTasklet).build())
				.build();
		
		ImmutablePair<String, JobParameters> p = ImmutablePair.of(jobName, params);
		return runJob(p, f);
	}
	
	public JobState runCoordinateTownJob(String jobName, JobParameters params) {
		Function<String, Job> f = j -> jobBuilder.get(j)
				.start(stepBuilder.get(Contants.BIZ_TOWNSHIP).tasklet(this.coordinateTown).build())
				.next(stepBuilder.get(Contants.BIZ_COMPRESS_ZIP).tasklet(this.zipTasklet).build())
				.build();

		ImmutablePair<String, JobParameters> p = ImmutablePair.of(jobName, params);
		return runJob(p, f);
	}
	
	public JobState runCoordinateAroundJob(String jobName, JobParameters params) {
		Function<String, Job> f = j -> jobBuilder.get(j)
				.start(stepBuilder.get(Contants.BIZ_POI_AROUND).tasklet(this.coordinateAround).build())
				.next(stepBuilder.get(Contants.BIZ_COMPRESS_ZIP).tasklet(this.zipTasklet).build())
				.build();
		
		ImmutablePair<String, JobParameters> p = ImmutablePair.of(jobName, params);
		return runJob(p, f);
	}

	public JobState runGatherPointTownJob(String jobName, JobParameters params) {
		Function<String, Job> f = j -> jobBuilder.get(j)
				.start(stepBuilder.get(Contants.BIZ_GP_TO_TOWN).tasklet(this.gatherPointTown).build())
				.next(stepBuilder.get(Contants.BIZ_COMPRESS_ZIP).tasklet(this.zipTasklet).build())
				.build();
		
		ImmutablePair<String, JobParameters> p = ImmutablePair.of(jobName, params);
		return runJob(p, f);
	}

	public JobState runGatherPointAroundJob(String jobName, JobParameters params) {
		Function<String, Job> f = j -> jobBuilder.get(j)
				.start(stepBuilder.get(Contants.BIZ_GP_TO_AROUND).tasklet(this.gatherPointAround).build())
				.next(stepBuilder.get(Contants.BIZ_COMPRESS_ZIP).tasklet(this.zipTasklet).build())
				.build();
		
		ImmutablePair<String, JobParameters> p = ImmutablePair.of(jobName, params);
		return runJob(p, f);
	}
	
	public JobState runPoiRankJob(String jobName, JobParameters params) {
		Function<String, Job> f = j -> jobBuilder.get(j)
				.start(stepBuilder.get(Contants.BIZ_GP_TO_AROUND).tasklet(this.gatherPointAround).build())
				.next(stepBuilder.get("POI Rank").tasklet(this.poiRankAndTopN).build())
				.next(stepBuilder.get(Contants.BIZ_COMPRESS_ZIP).tasklet(this.compressMultiFile).build())
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
			throw new RuntimeException("transfer input file failed");
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
			String result = taskConfig.getCompressedOutputFilePath(jobId, outputType);
			
			FileSystemResource  fileSystemResource = new FileSystemResource(result);
			return fileSystemResource;
		} else {
			throw new RuntimeException("invalid job ID");
		}
	}

	public JobState deleteJob(long jobId) {
		JobState jobState = jobDao.getJobState(jobId);
		String outputType = (String) jobState.getParams().get(Contants.PARAM_REQ_TYPE);
		String outputFilePath = taskConfig.getCompressedOutputFilePath(jobId, outputType);
		
		FileSystemResource resource = new FileSystemResource(outputFilePath);
		if (resource.exists()) {
			resource.getFile().delete();
		}
		jobDao.deleteJob(jobId);
		return jobState;
	}
	
	public JobState updateJobStatus(long jobId, int statusCode) {
		JobExecution jobExecution = jobDao.getJobExecution(jobId);
		JobState jobState = jobDao.getJobState(jobId);

		switch (statusCode) {
		case 0:
			jobExecution.setStatus(BatchStatus.COMPLETED);
			break;
		case 1:
			jobExecution.setStatus(BatchStatus.STARTING);
			break;
		case 2:
			jobExecution.setStatus(BatchStatus.STARTED);
			break;
		case 3:
			jobExecution.setStatus(BatchStatus.STOPPING);
			break;
		case 4:
			jobExecution.setStatus(BatchStatus.STOPPED);
			break;
		case 5:
			jobExecution.setStatus(BatchStatus.FAILED);
			break;
		case 6:
			jobExecution.setStatus(BatchStatus.ABANDONED);
			break;
		case 7:
			jobExecution.setStatus(BatchStatus.UNKNOWN);
			break;
		default:
			throw new RuntimeException("Unknown status code");
		}
		jobDao.updateJob(jobExecution);
		return jobState;
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
