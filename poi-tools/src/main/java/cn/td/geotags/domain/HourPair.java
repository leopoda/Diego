package cn.td.geotags.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class HourPair {
	@JsonProperty("start_hour")
	private int startHour;
	
	@JsonProperty("end_hour")
	private int endHour;
}
