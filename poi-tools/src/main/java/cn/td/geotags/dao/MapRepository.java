package cn.td.geotags.dao;

import cn.td.geotags.amap.around.Around;
import cn.td.geotags.amap.regeo.Regeo;
import cn.td.geotags.domain.Coordinate;

public interface MapRepository {
	Coordinate getGCJ02Coord (Coordinate wjs84Coord);
	Regeo getGEO(Coordinate gcj02Coord);
//	Around getPoiAround(Coordinate gcj02Coord);
//	Around getPoiAround(Coordinate gcj02Coord, String poiTypes);
	Around getPoiAround(Coordinate gcj02Coord, String poiTypes, long radius, int pageSize, int pageNum);
}
