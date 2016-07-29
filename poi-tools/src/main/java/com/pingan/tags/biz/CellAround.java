package com.pingan.tags.biz;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.minBy;

import static java.util.Comparator.comparingInt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pingan.tags.domain.Coordinate;
import com.pingan.tags.domain.PoiInfo;
import com.pingan.tags.domain.PoiType;
import com.pingan.tags.service.CoordService;

@Component
public class CellAround {
	@Autowired
	private CoordService coordService;
	
	private static final int CELL_POI_TYPE = 120302;
	
	public String doCalc(Coordinate coord) {
		List<PoiType> poiTypes = new ArrayList<>();
		PoiType poiType = new PoiType();

		poiType.setType(String.valueOf(CELL_POI_TYPE));
		poiTypes.add(poiType);
		
		List<PoiInfo> cells = coordService.getAroundPoi(coord, poiTypes);
		
		Optional<PoiInfo> p = cells.stream().collect(minBy(comparingInt(PoiInfo::getDistance)));
		
		String addr;
		if (p.isPresent()) {
			addr = String.join("\t", String.valueOf(coord.getLng()),
							  		 String.valueOf(coord.getLat()),
							  		 p.get().getProvince() == null ? "" : p.get().getProvince(),
							  		 p.get().getCity() == null ? "" : p.get().getCity(),
							  		 p.get().getDistrict() == null ? "" :  p.get().getDistrict(),
							  		 p.get().getAddress() == null ? "" : p.get().getAddress(),
							  		 p.get().getName() == null ? "" : p.get().getName());
		} else {
			addr = String.join("\t", String.valueOf(coord.getLng()), String.valueOf(coord.getLat()), "", "", "", "", "");
		}
		
		return addr;
	}
}
