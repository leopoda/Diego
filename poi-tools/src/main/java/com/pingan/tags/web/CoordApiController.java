package com.pingan.tags.web;

import java.util.Arrays;
import java.util.List;
import static java.util.stream.Collectors.toList;  

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pingan.tags.domain.CoordAddress;
import com.pingan.tags.domain.Coordinate;
import com.pingan.tags.domain.PoiInfo;
import com.pingan.tags.domain.PoiType;
import com.pingan.tags.service.CoordService;

@RestController
@RequestMapping("/")
public class CoordApiController {
	CoordService coordService;
	
	@Autowired
	public CoordApiController(CoordService coordService) {
		this.coordService = coordService;
	}
	
	@RequestMapping(value="/pois", method=RequestMethod.GET, produces="application/json;charset=UTF-8")
	public List<PoiInfo> getAroundPoi(@RequestParam(required=true) String coord, @RequestParam(required=true) String types) {
		Coordinate c = new Coordinate(coord);
		
		List<PoiType> poiTypes = 
		Arrays.asList(types.split("\\|"))
			  .stream()
			  .map(s -> {PoiType p = new PoiType(); p.setType(s); return p;})
			  .collect(toList());

		return coordService.getAroundPoi(c, poiTypes);
	}
	
	@RequestMapping(value="/township/{coord}", produces="application/json;charset=UTF-8")
	public CoordAddress getCoordAddress(@PathVariable String coord) {
		Coordinate c = new Coordinate(coord);
		
		CoordAddress ca = coordService.getCoordAddress(c);
		return ca;
	}
}
