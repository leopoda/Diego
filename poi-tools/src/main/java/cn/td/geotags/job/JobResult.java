package cn.td.geotags.job;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobResult {
	@JsonProperty("count")
	private int count;

	@JsonProperty("offset")
	private int offset;

	@JsonProperty("page_total")
	private int pageTotal;
	
	@JsonProperty("page_num")
	private int pageNum;

	@JsonProperty("jobs")
	List<JobState> jobList;
}
