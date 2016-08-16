package cn.td.geotags.biz;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import cn.td.geotags.config.RootConfig;
import cn.td.geotags.domain.CoordAddress;
import cn.td.geotags.domain.GatherPoint;
import cn.td.geotags.service.CoordService;
import cn.td.geotags.util.GatherPointParser;

@Component
public class GatherPointStatistics {
	@Autowired
	CoordService coordService;
	
	public static void main(String[] args) throws IOException {
		if (args.length < 2) {
			System.out.println("Error, at least two input parameters!");
			return;
		}

		String filePath = args[0];
		String prefix = args[1];
		
//		String filePath = "d:/datahub/近一月到访tdid聚集点信息.dat";
//		String prefix = "d:/datahub/花样年地产-%s.txt";
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
//		PrintStream strm = new PrintStream(String.format(prefix, sdf.format(System.currentTimeMillis())));
		
		try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(RootConfig.class)) {
			GatherPointStatistics gatherPoint = ctx.getBean(GatherPointStatistics.class);
			gatherPoint.calc(filePath, String.format(prefix, sdf.format(System.currentTimeMillis())));
		}
	}
	
	public void calc(String filePath, String outFile) throws IOException {
		PrintStream strm = new PrintStream(outFile);
		try (Stream<String> line = Files.lines(Paths.get(filePath))) {
			line.map(GatherPointParser::parse)
//				.limit(10)
				.parallel()
				.flatMap(o -> o.stream())
				.filter(o -> o.getCoordinate().isValid())
				.map(x -> ImmutablePair.of(x, x.getCoordinate()))
				.map(x -> ImmutablePair.of(x.left, coordService.getCoordAddress(x.right)))
				.filter(p -> p.right != null)
				.map(x -> {	GatherPoint gp = x.left;
							CoordAddress ca = x.right;
							String addr = String.join("\t", gp.getTdid(),
															gp.isWorkday() ? "Y" : "N",
															String.valueOf(gp.getHour()),
															String.valueOf(gp.getCount()),
//															String.valueOf(gp.getCoordinate().getLng()),
//															String.valueOf(gp.getCoordinate().getLat()),
															ca.asFlatText());
							return addr;})
//				.forEach(System.out::println);
				.forEach(o -> strm.println(o));
		} finally {
			strm.close();
		}
	}
}
