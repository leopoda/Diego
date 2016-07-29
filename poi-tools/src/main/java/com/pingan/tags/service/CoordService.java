package com.pingan.tags.service;

import java.util.List;

import com.pingan.tags.domain.CoordAddress;
import com.pingan.tags.domain.Coordinate;
import com.pingan.tags.domain.PoiInfo;
import com.pingan.tags.domain.PoiType;

public interface CoordService {
//	Coordinate toGcj02Coord(Coordinate wjs84Coord);
	CoordAddress getCoordAddress(Coordinate wjs84Coord);
	List<PoiInfo> getAroundPoi(Coordinate wjs84Coord, List<PoiType> poiTypes);
//	List<PoiType> getConfigPoiTypes();
}
