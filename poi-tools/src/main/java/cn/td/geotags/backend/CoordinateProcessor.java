package cn.td.geotags.backend;

import org.springframework.batch.item.ItemProcessor;
import cn.td.geotags.domain.CoordAddress;
import cn.td.geotags.domain.Coordinate;
import cn.td.geotags.service.CoordService;

public class CoordinateProcessor implements ItemProcessor<Coordinate, CoordAddress> {
	CoordService coordService;
	
	public CoordinateProcessor(CoordService coordService) {
		this.coordService = coordService;
	}

	public CoordAddress process(Coordinate coord) throws Exception {
		return coordService.getCoordAddress(coord);
	}
}
