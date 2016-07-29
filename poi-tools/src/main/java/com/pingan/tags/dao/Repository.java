package com.pingan.tags.dao;

import com.pingan.tags.amap.around.Around;
import com.pingan.tags.amap.regeo.Regeo;
import com.pingan.tags.domain.Coordinate;

public interface Repository {
	Coordinate getGCJ02Coord (Coordinate wjs84Coord);
	Regeo getGEO(Coordinate gcj02Coord);
//	Around getPoiAround(Coordinate gcj02Coord);
//	Around getPoiAround(Coordinate gcj02Coord, String poiTypes);
	Around getPoiAround(Coordinate gcj02Coord, String poiTypes, int radius, int pageSize, int pageNum);
}
