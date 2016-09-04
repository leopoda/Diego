package cn.td.geotags.service;

import java.util.List;

import cn.td.geotags.domain.CoordAddress;
import cn.td.geotags.domain.Coordinate;
import cn.td.geotags.domain.PoiInfo;
import cn.td.geotags.domain.PoiType;

public interface CoordService {
	CoordAddress getCoordAddress(Coordinate wjs84Coord);
	CoordAddress getCoordAddress(Coordinate coord, String coordsys);
	List<PoiInfo> getAroundPoi(Coordinate wjs84Coord, List<PoiType> poiTypes);
	List<PoiInfo> getAroundPoi(Coordinate wjs84Coord, String types, long radius);
	List<PoiInfo> getAroundPoi(Coordinate coord, String types, long radius, String coordsys);
}
