package cn.td.geotags.biz;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Stream;
import static java.util.stream.Collectors.toList;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import cn.td.geotags.config.RootConfig;
import cn.td.geotags.domain.GatherPoint;
import cn.td.geotags.domain.PoiInfo;
import cn.td.geotags.service.CoordService;
import cn.td.geotags.util.Contants;
import cn.td.geotags.util.GatherPointParser;

@Component
public class GatherPointAround {
	@Autowired
	CoordService coordService;
	
	public static void main(String[] args) throws IOException {
//		if (args.length < 2) {
//			System.out.println("Error, at least two input parameters!");
//			return;
//		}
//
//		String filePath = args[0];
//		String prefix = args[1];
		
		String filePath = "e:/tendcloud/FE/卓越/大亚湾和intown的MAC数据/dayawan_result.txt";
		String prefix = "d:/datahub/output/dayawan-%s.txt";
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(RootConfig.class)) {
			GatherPointAround gatherPoint = ctx.getBean(GatherPointAround.class);
			gatherPoint.calc("", 500, filePath, String.format(prefix, sdf.format(System.currentTimeMillis())));
		}
	}

	public void calc(String poiTypes, long radius, String filePath, String outFile) throws IOException {
		calc(poiTypes, radius, Contants.PARAM_COORD_SYS_GPS, filePath, outFile);
	}

	public void calc(String poiTypes, long radius, String coordsys, String filePath, String outFile) throws IOException {
		PrintStream strm = new PrintStream(outFile);
		try (Stream<String> line = Files.lines(Paths.get(filePath))) {
			line.map(GatherPointParser::parse)
//				.limit(10)
				.parallel()
				.flatMap(o -> o.stream())
				.filter(o -> o.getCoordinate().isValid())
				.map(x -> ImmutablePair.of(x, coordService.getAroundPoi(x.getCoordinate(), poiTypes, radius, coordsys)))
				.filter(p -> p.right != null)
				.map(o -> bind(o.right, o.left))
				.flatMap(o -> o.stream())
				.map(o -> String.join("\t", o.left.asFlatText(), o.right.asFlatText()))
//				.forEach(System.out::println);
				.forEach(o -> strm.println(o));
		} finally {
			strm.close();
		}
	}

	private List<ImmutablePair<GatherPoint, PoiInfo>> bind(List<PoiInfo> poiInfoList, GatherPoint gp) {
		return poiInfoList.stream().map(poiInfo -> ImmutablePair.of(gp, poiInfo)).collect(toList());
	}
}
