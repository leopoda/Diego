package cn.td.geotags.domain;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class RankCondition {
	@JsonProperty("rank_input_file")
	private String rankInputFile;
	
	@JsonProperty("rank_output_prefix")
	private String rankOutputPrefix;
	
	@JsonProperty("poi_type_code")
	private List<String> poiTypeCode;
	private String province;
	private String city;
	private String district;
	
	@JsonProperty("day_type")
	private String dayType;
	
	@JsonProperty("hour_range")
	private List<HourPair> hourRange;
	
	private int topN;
}
