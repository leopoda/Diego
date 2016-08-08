package cn.td.geotags.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PoiSummary {
	private PoiType poiType;
	private Long count;
	private Double avgDistance;
	private Integer minDistance;
	private Integer maxDistance;
}
