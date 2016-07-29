package com.pingan.tags.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PoiConfig {
	private String poiTypeInfo;
	private int poiAroundRadius;
	private int poiAroundPageSize;
}
