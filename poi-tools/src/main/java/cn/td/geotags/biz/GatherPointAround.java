package cn.td.geotags.biz;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import static java.util.stream.Collectors.toList;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.td.geotags.domain.GatherPoint;
import cn.td.geotags.domain.PoiInfo;
import cn.td.geotags.service.CoordService;
//import cn.td.geotags.util.Contants;
import cn.td.geotags.util.ParserUtil;

@Component
public class GatherPointAround {
	@Autowired
	CoordService coordService;

//	@Deprecated
//	public void calc(String poiTypes, long radius, String filePath, String outFile) throws IOException {
//		calc(poiTypes, radius, Contants.PARAM_COORD_SYS_GPS, filePath, outFile);
//	}

	public void calc(String poiTypes, long radius, String coordsys, String filePath, String outFile, Map<String, Object> additional) throws IOException {
		PrintStream strm = new PrintStream(outFile);
		try (Stream<String> line = Files.lines(Paths.get(filePath))) {
			line.map(ParserUtil::parseAsGatherPoint)
				.parallel()
				.flatMap(o -> o.stream())
				.filter(o -> o.getCoordinate().isValid())
				.map(x -> ImmutablePair.of(x, coordService.getAroundPoi(x.getCoordinate(), poiTypes, radius, coordsys, additional)))
				.filter(p -> p.right != null)
				.map(o -> bind(o.right, o.left))
				.flatMap(o -> o.stream())
				.map(o -> String.join("\t", o.left.asFlatText(), o.right.asFlatText()))
				.forEach(o -> strm.println(o));
		} finally {
			strm.close();
		}
	}

	private List<ImmutablePair<GatherPoint, PoiInfo>> bind(List<PoiInfo> poiInfoList, GatherPoint gp) {
		return poiInfoList.stream().map(poiInfo -> ImmutablePair.of(gp, poiInfo)).collect(toList());
	}
}
