package cn.td.geotags.dao;

import java.util.List;

import cn.td.geotags.amap.around.Around;
import cn.td.geotags.amap.district.District;
import cn.td.geotags.amap.regeo.Regeo;
import cn.td.geotags.domain.Coordinate;

public interface MapRepository {
	Coordinate getGCJ02Coord (Coordinate wgs84Coord);
	Coordinate getGCJ02Coord(String coordsys, Coordinate src);
	Regeo getGEO(Coordinate gcj02Coord);
	Around getPoiAround(Coordinate gcj02Coord, String poiTypes, long radius, int pageSize, int pageNum);
	List<District> getDistrict(String provinceCode);
}
