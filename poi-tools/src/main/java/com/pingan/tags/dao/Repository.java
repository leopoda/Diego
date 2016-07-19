package com.pingan.tags.dao;

import com.pingan.tags.amap.regeo.Regeo;
import com.pingan.tags.domain.Coordinate;

public interface Repository {
//	Address save(Address a);
//	Address findOne(long id);
//	Address findByTdId(String tdid);
	
	Coordinate getGCJ02Coord (Coordinate coord);
	Regeo getGEO(Coordinate coord);
}
