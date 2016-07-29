package com.pingan.tags.biz;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.pingan.tags.config.RootConfig;
import com.pingan.tags.domain.Coordinate;
import com.pingan.tags.domain.PoiInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {
	
	public static void main(String[] args) throws IOException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(RootConfig.class)) {
			String filePath = "D:/datahub/pa_list_loc@20160715.txt";
			String prefix = "d:/datahub/coord-geo-%s.txt";
			
//			TownshipStatistics coordStreet = ctx.getBean(TownshipStatistics.class);
//			coordStreet.doCalc(filePath, prefix);

//			String fileName = "D:/datahub/光大位置数据.dat";
			String fileName = "d:/datahub/近一月到访tdid聚集点信息.dat";
			GatherPointStatistics gatherPoint = ctx.getBean(GatherPointStatistics.class);
			gatherPoint.doCalc(fileName, String.format("d:/datahub/花样年地产-%s.txt", sdf.format(System.currentTimeMillis())));

			
//			PrintStream strm = new PrintStream("D:/datahub/demo.txt");
//			try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
//				PoiAroundStatistics poiService = ctx.getBean(PoiAroundStatistics.class);
//				lines.map(line -> parseAsCoordinate(line))
//					 .limit(5)
//					 .map(c -> poiService.doCalc(c))
//					 .flatMap(s -> s.stream())
//					 .forEach(System.out::println);
////					 .forEach(o -> strm.println(o));
//			} finally {
//				strm.close();
//			}
			
//			try (Stream<String> lines = Files.lines(Paths.get("D:/datahub/pa_list_home_loc@20160726.dat"))) {
//				PrintStream strm = new PrintStream(String.format("D:/datahub/pa_list_cell-%s.txt", sdf.format(System.currentTimeMillis())));
//				CellAround cellAround = ctx.getBean(CellAround.class);
//				lines.map(line -> parseAsCoordinate(line))
////					 .limit(10)
////					 .filter(x -> x.isValid())
////					 .parallel()
//					 .map(c -> cellAround.doCalc(c))
//					 .forEach(s -> strm.println(s));
//			}
			
		}
	}
	
	private static Coordinate parseAsCoordinate(String line) {
		String[] arr = line.split("\t");
		double lng = Double.parseDouble(arr[0]);
		double lat = Double.parseDouble(arr[1]);

		return new Coordinate(lng, lat);
	}
}