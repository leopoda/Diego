package cn.td.geotags.job;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobState {
	@JsonProperty("job_id")
	private Long jobId;
	
	@JsonProperty("job_name")
	private String jobName;
	
	@JsonProperty("status")
	private String jobStatus;
	
	@JsonProperty("start_time")
	private String startTime;
	
	@JsonProperty("end_time")
	private String endTime;
	
//	@JsonProperty("msg")
//	private String message;
	
	@JsonProperty("job_param")
	Map<String, Object> params;
}
