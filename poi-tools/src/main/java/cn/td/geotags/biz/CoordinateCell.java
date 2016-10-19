package cn.td.geotags.biz;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import static java.util.stream.Collectors.minBy;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import static java.util.Comparator.comparingInt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.td.geotags.domain.Coordinate;
import cn.td.geotags.domain.PoiInfo;
import cn.td.geotags.service.CoordService;
//import cn.td.geotags.util.Contants;
import cn.td.geotags.util.ParserUtil;

@Component
public class CoordinateCell {
	@Autowired
	private CoordService coordService;
	
	private static final int CELL_POI_TYPE = 120302;
	
//	@Deprecated
//	public void calc(long radius, String inFile, String outFile) throws IOException {
//		calc(radius, Contants.PARAM_COORD_SYS_GPS, inFile, outFile);
//	}
	
	public void calc(long radius, String coordsys, String inFile, String outFile, Map<String, Object> additional) throws IOException {
		PrintStream strm = new PrintStream(outFile);
		try (Stream<String> lines = Files.lines(Paths.get(inFile))) {
			lines.map(ParserUtil::parseAsCoordinate)
				.parallel()
				.map(c -> calc(c, radius, coordsys, additional))
				.forEach(s -> strm.println(s));
		} finally {
			strm.close();
		}
	}
	
	private String calc(Coordinate coord, long radius, String coordsys, Map<String, Object> additional) {
		return this.calc(coord, c -> coordService.getAroundPoi(coord, String.valueOf(CELL_POI_TYPE), radius, coordsys, additional));
	}
	
	private String calc(Coordinate coord, Function<Coordinate, List<PoiInfo>> f) {
		List<PoiInfo> cells = f.apply(coord);
		Optional<PoiInfo> p = cells.stream().collect(minBy(comparingInt(PoiInfo::getDistance)));
		
		String addr;
		if (p.isPresent()) {
			addr = String.join("\t", String.valueOf(coord.getLng()),
							  		 String.valueOf(coord.getLat()),
							  		 String.valueOf(p.get().getAmapCenter().getLng()),
							  		 String.valueOf(p.get().getAmapCenter().getLat()),
							  		 String.valueOf(p.get().getDistance()),
							  		 p.get().getLocation() == null ? "" : p.get().getLocation(),
							  		 p.get().getProvince() == null ? "" : p.get().getProvince(),
							  		 p.get().getCity() == null ? "" : p.get().getCity(),
							  		 p.get().getDistrict() == null ? "" :  p.get().getDistrict(),
							  		 p.get().getAddress() == null ? "" : p.get().getAddress(),
							  		 p.get().getName() == null ? "" : p.get().getName());
		} else {
			addr = String.join("\t", String.valueOf(coord.getLng()), String.valueOf(coord.getLat()), "", "", "", "", "", "", "", "", "");
		}
		
		return addr;
	}
}
