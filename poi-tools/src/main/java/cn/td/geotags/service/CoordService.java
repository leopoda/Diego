package cn.td.geotags.service;

import java.util.List;

import cn.td.geotags.domain.CoordAddress;
import cn.td.geotags.domain.Coordinate;
import cn.td.geotags.domain.PoiInfo;
import cn.td.geotags.domain.PoiType;

public interface CoordService {
//	Coordinate toGcj02Coord(Coordinate wjs84Coord);
	CoordAddress getCoordAddress(Coordinate wjs84Coord);
	List<PoiInfo> getAroundPoi(Coordinate wjs84Coord, List<PoiType> poiTypes);
//	List<PoiType> getConfigPoiTypes();
}
