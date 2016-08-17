package cn.td.geotags.web;

import java.util.Arrays;
import java.util.List;

import java.io.IOException;
import static java.util.stream.Collectors.toList;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cn.td.geotags.domain.CoordAddress;
import cn.td.geotags.domain.Coordinate;
import cn.td.geotags.domain.PoiInfo;
import cn.td.geotags.domain.PoiType;
import cn.td.geotags.job.JobError;
import cn.td.geotags.job.JobManager;
import cn.td.geotags.job.JobDao;
import cn.td.geotags.job.JobState;
import cn.td.geotags.job.JobResult;
import cn.td.geotags.service.CoordService;
import cn.td.geotags.util.Contants;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/")
public class CoordController {
	private CoordService coordService;
	
	@Autowired
	private JobManager jobManager;
	
	@Autowired
	private JobDao jobDao;
	
	@Autowired
	public CoordController(CoordService coordService) {
		this.coordService = coordService;
	}
	
	@CrossOrigin
	@RequestMapping(value="/pois", method=RequestMethod.GET, produces="application/json;charset=UTF-8")
	public List<PoiInfo> getAroundPoi(@RequestParam(required=true) String coord, @RequestParam(required=true) String types) {
		Coordinate c = new Coordinate(coord);
		
		List<PoiType> poiTypes = 
		Arrays.asList(types.split("\\|"))
			  .stream()
			  .map(s -> {PoiType p = new PoiType(); p.setType(s); return p;})
			  .collect(toList());

		return coordService.getAroundPoi(c, poiTypes);
	}
	
	@CrossOrigin
	@RequestMapping(value="/township/{coord}", produces="application/json;charset=UTF-8")
	public CoordAddress getCoordAddress(@PathVariable String coord) {
		Coordinate c = new Coordinate(coord);
		
		CoordAddress ca = coordService.getCoordAddress(c);
		return ca;
	}

	@ExceptionHandler({ Exception.class, RuntimeException.class })
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public JobError jobException(Exception e) {
		log.error("service has detected some errors", e);
		return new JobError(1, "service error: " + e.getMessage());
	}

	@CrossOrigin
	@RequestMapping(value="/submit", method=RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public JobState submitJob(
			@RequestParam(value="job", required=true) String jobName,
			@RequestParam(value="file", required=true) MultipartFile file, 
			@RequestParam(value="import", required=true) String contentType,
			@RequestParam(value="coordsys", required=false) String coordsys,
			@RequestParam(value="types", required=false) String poiTypes,
			@RequestParam(value="radius", required=false) Integer radius,
			@RequestParam(value="for", required=true) String reqType) {

		JobState jobState = null;
		String inFile = "";

		if (!file.isEmpty()) {
			try {
				// calc file MD5 to prevent duplicated jobs being submitted
				inFile = jobManager.getInputFilePath(file, contentType);
			} catch (IllegalStateException | IOException e) {
				throw new RuntimeException("identify file for input and output failed", e);
			}
		
			JobParameters jobParameters = new JobParametersBuilder()
					.addString(Contants.PARAM_IN_FILE, inFile)
//					.addString(Contants.PARAM_OUT_FILE, p.right)
					.addString(Contants.PARAM_CONTENT, contentType)
					.addString(Contants.PARAM_REQ_TYPE, reqType)
					.addLong(Contants.PARAM_RADIUS, radius == null ? Contants.DEFAULT_RADIUS : radius)
					.addString(Contants.PARAM_TYPES, poiTypes == null ? "" : poiTypes)
					.toJobParameters();

			if (Contants.TYPE_CO.equals(contentType) && Contants.REQ_GEO.equals(reqType)) {
				jobState = jobManager.runTownshipJob(jobName, jobParameters);
			} else if (Contants.TYPE_CO.equals(contentType) && Contants.REQ_POI.equals(reqType)) {
				jobState = jobManager.runPoiAroundJob(jobName, jobParameters);
			} else if (Contants.TYPE_CO.equals(contentType) && Contants.REQ_CELL.equals(reqType)) {
				jobState = jobManager.runCellAroundJob(jobName, jobParameters);
			} else if (Contants.TYPE_GP.equals(contentType) && Contants.REQ_GEO.equals(reqType)) {
				jobState = jobManager.runGatherPointJob(jobName, jobParameters);
			} else if (Contants.TYPE_GP.equals(contentType) && Contants.REQ_POI.equals(reqType)) {
				throw new RuntimeException("this is expected, this functionality is  still not implemented yet");
			} else if (Contants.TYPE_GP.equals(contentType) && Contants.REQ_CELL.equals(reqType)) {
				throw new RuntimeException("this is expected, this functionality is  still not implemented yet");
			} else {
				throw new RuntimeException("this type of request is not supported yet!");
			}
		}
		return jobState;
	}

	@CrossOrigin
	@RequestMapping(value="/jobs", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public JobResult handleJobQuery(@RequestParam(value = "offset", required = true) int offset,
			@RequestParam(value = "page", required = true) int page,
			@RequestParam(value="for", required=true) String reqType,
			@RequestParam(value="search", required=false) String search) {
		if (reqType.equals(Contants.REQ_GEO)) {
			return jobDao.queryJobs(offset, page, Contants.REQ_GEO, search);
		} else if (reqType.equals(Contants.REQ_POI)) {
			return jobDao.queryJobs(offset, page, Contants.REQ_POI, search);
		} else {
			throw new RuntimeException("this type of request is not supported yet!");
		}
	}
	
	@CrossOrigin
	@RequestMapping(value="/jobresult/{jobId}", method=RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public FileSystemResource handleDownload(@PathVariable long jobId) {
		return jobManager.getJobOutput(jobId);
	}
}
