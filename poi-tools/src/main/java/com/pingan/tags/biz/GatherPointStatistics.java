package com.pingan.tags.biz;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import com.pingan.tags.config.RootConfig;
import com.pingan.tags.domain.CoordAddress;
import com.pingan.tags.domain.GatherPoint;
import com.pingan.tags.service.CoordService;

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
	
	void calc(String filePath, String outFile) throws IOException {
		PrintStream strm = new PrintStream(outFile);
		try (Stream<String> line = Files.lines(Paths.get(filePath))) {
			line.map(x -> parseAsGatherPoint(x))
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
															gp.isWeekend() ? "Y" : "N",
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
	
	
	private List<GatherPoint> parseAsGatherPoint(String line) {
		String tdid = "";
		String pos = "";
		String[] arr = line.split("\t");

		if (arr.length >= 2) {
			tdid = arr[0];
			pos = arr[1];
		}

		int idx = pos.indexOf("|");
		List<GatherPoint> list = new ArrayList<>();

		if (idx >= 0) {
			String first = pos.substring(0, idx);
			String second = pos.substring(idx + 1, pos.length());
			
			if (first != null && first.length() > 0) {
				for (String hc : first.split(";")) {
					int idx2 = hc.indexOf(":");
					String h = hc.substring(0, idx2);
					String c = hc.substring(idx2 + 1, hc.length());
					for (String cos : c.split(",")) {
						String[] co = cos.split("_");
						String lat = co[0];
						String lng = co[1];
						int count = Integer.parseInt(co[2]);
						GatherPoint gp = new GatherPoint(tdid, 
														 false, 
														 "", 
														 Integer.parseInt(h), 
														 Double.parseDouble(String.format("%.5f", Double.parseDouble(lng))), 
														 Double.parseDouble(String.format("%.5f", Double.parseDouble(lat))), 
														 count);
						list.add(gp);
					}
				}
			}

			if (second != null && second.length() > 0) {
				for (String hc : second.split(";")) {
					int idx2 = hc.indexOf(":");
					String h = hc.substring(0, idx2);
					String c = hc.substring(idx2 + 1, hc.length());
					for (String cos : c.split(",")) {
						String[] co = cos.split("_");
						String lat = co[0];
						String lng = co[1];
						int count = Integer.parseInt(co[2]);
						GatherPoint gp = new GatherPoint(tdid, 
														 true, 
														 "", 
														 Integer.parseInt(h), 
														 Double.parseDouble(String.format("%.5f", Double.parseDouble(lng))), 
														 Double.parseDouble(String.format("%.5f", Double.parseDouble(lat))), 
														 count);
						list.add(gp);
					}
				}
			}
		}
		return list;
	}
}
