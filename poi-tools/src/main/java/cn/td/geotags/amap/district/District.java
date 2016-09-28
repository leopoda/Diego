package cn.td.geotags.amap.district;

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
public class District {
	/*
	 * 城市编码
	 */
	@JsonProperty("citycode")
	private String cityCode;
	
	/*
	 * 区域编码
	 */
	@JsonProperty("adcode")
	private String adCode;
	
	/*
	 * 行政区名称
	 */
	private String name;
	
	/*
	 * 行政区边界坐标点
	 */
	private String polyine;
	
	/*
	 * 城市中心点
	 */
	private String center;
	
	/*
	 * 行政区划级别
	 */
	private String level;
	
	private List<District> districts;
}
