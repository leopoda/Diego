package cn.td.geotags.amap.district;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DistrictResponse {
	private int status;
	private String info;
	
	@JsonProperty("infocode")
	private int infoCode;
	
	private List<District> districts;
}
