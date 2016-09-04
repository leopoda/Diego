package cn.td.geotags.biz;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.td.geotags.domain.CoordAddress;
import cn.td.geotags.domain.GatherPoint;
import cn.td.geotags.service.CoordService;
import cn.td.geotags.util.Contants;
import cn.td.geotags.util.ParserUtil;

@Component
public class GatherPointTown {
	@Autowired
	CoordService coordService;

	@Deprecated
	public void calc(String filePath, String outFile) throws IOException {
		calc(Contants.PARAM_COORD_SYS_GPS, filePath, outFile);
	}

	public void calc(String coordsys, String filePath, String outFile) throws IOException {
		PrintStream strm = new PrintStream(outFile);
		try (Stream<String> line = Files.lines(Paths.get(filePath))) {
			line.map(ParserUtil::parseAsGatherPoint)
				.parallel()
				.flatMap(o -> o.stream())
				.filter(o -> o.getCoordinate().isValid())
				.map(x -> ImmutablePair.of(x, x.getCoordinate()))
				.map(x -> ImmutablePair.of(x.left, coordService.getCoordAddress(x.right, coordsys)))
				.filter(p -> p.right != null)
				.map(x -> {	GatherPoint gp = x.left;
							CoordAddress ca = x.right;
							String addr = String.join("\t", gp.getTdid(),
															gp.isWorkday() ? "Y" : "N",
															String.valueOf(gp.getHour()),
															String.valueOf(gp.getCount()),
															ca.asFlatText());
							return addr;})
				.forEach(o -> strm.println(o));
		} finally {
			strm.close();
		}
	}
}
