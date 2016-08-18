package cn.td.geotags.web;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.bind.annotation.RequestPart;
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
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.annotations.ApiIgnore;

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
	private HttpServletResponse response;
	
	@Autowired
	public CoordController(CoordService coordService) {
		this.coordService = coordService;
	}
	
	@ApiIgnore
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
	
	@ApiIgnore
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
	@ApiOperation(tags ="地理标签应用", value = "提交任务")
	@RequestMapping(value="/submit", method=RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public JobState submitJob(
			@ApiParam("*任务名称") @RequestParam(value="job") String jobName,
			@ApiParam("*输入类别 (co - 经纬度, gp - 聚集点)") @RequestParam(value="import") String contentType,
			@ApiParam("*上传文件") @RequestPart(value="file") MultipartFile file, 
			@ApiParam("坐标系 (gps - wjs84坐标, autonavi -  高德、谷歌、腾讯坐标)") @RequestParam(value="coordsys", required=false) String coordsys,
			@ApiParam("POI分类代码  (高德 POI 类型代码，多个以|分隔)") @RequestParam(value="types", required=false) String poiTypes,
			@ApiParam("搜索半径 (默认 500m)") @RequestParam(value="radius", required=false) Integer radius,
			@ApiParam("*输出类别 (geo - 社区街道, poi - 周边 POI, cell - 最近小区)") @RequestParam(value="for") String reqType) {

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
	@ApiOperation(tags ="地理标签应用", value = "批量任务状态查询")
	@RequestMapping(value="/jobs", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public JobResult handleJobQuery(
			@ApiParam(value = "*任务类型 (geo, poi, cell)") @RequestParam(value = "for") String reqType,
			@ApiParam(value = "*请求页码") @RequestParam(value = "page") int page,
			@ApiParam(value = "*每页记录数") @RequestParam(value = "offset") int offset,
			@ApiParam(value = "任务名称(过滤用)") @RequestParam(value="search", required=false) String search) {
		if (reqType.equals(Contants.REQ_GEO)) {
			return jobDao.queryJobs(offset, page, Contants.REQ_GEO, search);
		} else if (reqType.equals(Contants.REQ_POI)) {
			return jobDao.queryJobs(offset, page, Contants.REQ_POI, search);
		} else if (reqType.equals(Contants.REQ_CELL)){
			return jobDao.queryJobs(offset, page, reqType, search);
		} else {
			throw new RuntimeException("this type of request is not supported yet!");
		}
	}

	@CrossOrigin
	@ApiOperation(tags ="地理标签应用", value = "单个任务状态查询")
	@RequestMapping(value="/job/{jobId}", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public JobState singleJobQuery(@ApiParam(value = "*任务 ID") @PathVariable long jobId) {
		return jobDao.getJobState(jobId);
	}

	@CrossOrigin
	@ApiOperation(tags ="地理标签应用", value = "获取任务结果")
	@RequestMapping(value="/jobresult/{jobId}", method=RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public FileSystemResource handleDownload(@ApiParam(value = "*任务 ID") @PathVariable long jobId) {
		FileSystemResource resource = jobManager.getJobOutput(jobId);
		response.setHeader("Content-disposition", String.format("attachment;filename=\"%s\"", resource.getFilename()));
		return resource;
	}
}
