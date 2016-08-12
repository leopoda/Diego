package cn.td.geotags.web;

import java.util.Arrays;
import java.util.List;
import static java.util.stream.Collectors.toList;

import org.springframework.http.HttpStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import cn.td.geotags.domain.CoordAddress;
import cn.td.geotags.domain.Coordinate;
import cn.td.geotags.domain.PoiInfo;
import cn.td.geotags.domain.PoiType;
import cn.td.geotags.job.JobError;
import cn.td.geotags.job.JobManager;
import cn.td.geotags.job.JobStatus;
import cn.td.geotags.service.CoordService;

@RestController
@RequestMapping("/")
public class CoordController {
	CoordService coordService;
	
	private final static long DEFAULT_RADIUS_LENGTH = 500L;
	
	@Autowired
	JobManager jobManager;
	
	@Autowired
	public CoordController(CoordService coordService) {
		this.coordService = coordService;
	}
	
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
	
	@RequestMapping(value="/township/{coord}", produces="application/json;charset=UTF-8")
	public CoordAddress getCoordAddress(@PathVariable String coord) {
		Coordinate c = new Coordinate(coord);
		
		CoordAddress ca = coordService.getCoordAddress(c);
		return ca;
	}
	
	@RequestMapping(value="/runjob/cellaround", method=RequestMethod.GET)
	public JobStatus handleCellAroundJob(@RequestParam(value = "job", required = true) String jobName,
			@RequestParam(value="r", required = false) Long radius) {
		JobParameters jobParameters = new JobParametersBuilder()
				.addString("inFile", "D:/datahub/pa_list_home_loc@20160726.dat")
				.addString("outFile", "D:/datahub/cellAroundTest.txt")
				.addLong("radius", radius == null ? DEFAULT_RADIUS_LENGTH : radius)
				.toJobParameters();
		
		return jobManager.runCellAroundJob(jobName, jobParameters);
	}
	
	@RequestMapping(value="/runjob/township/{jobName}", method=RequestMethod.GET)
	public JobStatus handleTownshipJob(@PathVariable String jobName) {
		JobParameters jobParameters = new JobParametersBuilder()
				.addString("inFile", "D:/datahub/pa_list_home_loc@20160726.dat")
				.addString("outFile", "D:/datahub/output/township.txt")
				.toJobParameters();
		
		return jobManager.runTownshipJob(jobName, jobParameters);
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public JobError jobException(Exception e) {
		
		return  new JobError(1, "Service failed, please contact the administrator.");
	}

}
