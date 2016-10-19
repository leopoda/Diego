package cn.td.geotags.job;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobState {
	private final static String DATE_FMT_PATTERN = "yyyy-MM-dd HH:mm:ss";
	private final static String DATE_PAR_PATTERN = "yyyy-MM-dd HH:mm:ss.S";

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
	
	@JsonProperty("job_param")
	private Map<String, Object> params;
	
	@JsonProperty("exit_desc")
	private String exitMessage;
	
	public String getStartTime() {
		return formatDateTime(startTime);
	}
	
	public String getEndTime() {
		return formatDateTime(endTime);
	}
	
	private String formatDateTime(String dateTime) {
		final SimpleDateFormat fmt = new SimpleDateFormat(DATE_FMT_PATTERN);
		final SimpleDateFormat par = new SimpleDateFormat(DATE_PAR_PATTERN);

		if (dateTime == null || dateTime.isEmpty()) {
			return "";
		}
		
		try {
			return fmt.format(par.parse(dateTime));
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
}
