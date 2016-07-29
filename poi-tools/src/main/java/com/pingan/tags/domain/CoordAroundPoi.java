package com.pingan.tags.domain;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoordAroundPoi {
	private Coordinate coord;
	private List<PoiType> poiInfo;
}
