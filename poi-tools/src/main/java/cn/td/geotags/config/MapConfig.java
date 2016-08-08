package cn.td.geotags.config;

import lombok.Setter;
import lombok.Getter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class MapConfig {
	private String token;
	private String convertURL;
	private String regeoURL;
	private String aroundURL;
}
