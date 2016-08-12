package cn.td.geotags.job;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class JobError {
	private int code;
	
	@JsonProperty("msg")
	private String message;

	public JobError(int code, String message) {
		this.code = code;
		this.message = message;
	}
}