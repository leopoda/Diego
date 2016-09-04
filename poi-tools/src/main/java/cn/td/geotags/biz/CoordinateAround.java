package cn.td.geotags.biz;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.td.geotags.domain.Coordinate;
import cn.td.geotags.domain.PoiInfo;
import cn.td.geotags.service.CoordService;
import cn.td.geotags.util.Contants;
import cn.td.geotags.util.ParserUtil;

@Component
public class CoordinateAround {
	@Autowired
	CoordService coordService;

	@Deprecated
	public void calc(String poiTypes, long radius, String coordinateInputFile, String outFile) throws IOException {
		calc(poiTypes, radius, Contants.PARAM_COORD_SYS_GPS, coordinateInputFile, outFile);
	}

	public void calc(String poiTypes, long radius, String coordsys, String coordFile, String outFile) throws IOException {
		PrintStream strm = new PrintStream(outFile);
		try (Stream<String> lines = Files.lines(Paths.get(coordFile))) {
			lines.map(ParserUtil::parseAsCoordinate)
				 .parallel()
				 .map(c -> this.getPoiInfo(c, poiTypes, radius, coordsys))
				 .flatMap(s -> s.stream())
				 .map(o -> o.asFlatText())
				 .forEach(o -> strm.println(o));
		} finally {
			strm.close();
		}
	}

	private List<PoiInfo> getPoiInfo(Coordinate coord, String types, long radius, String coordsys) {
		return coordService.getAroundPoi(coord, types, radius, coordsys);
	}
}
