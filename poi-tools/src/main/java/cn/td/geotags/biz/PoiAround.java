package cn.td.geotags.biz;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import cn.td.geotags.config.RootConfig;
import cn.td.geotags.domain.Coordinate;
import cn.td.geotags.domain.PoiInfo;
import cn.td.geotags.domain.PoiType;
import cn.td.geotags.service.CoordService;

@Component
public class PoiAround {
	@Autowired
	CoordService coordService;

	public static void main(String[] args) throws IOException {
		if (args.length < 2) {
			System.out.println("Error, at least two input parameters!");
			return;
		}
		String inFile = args[0]; 
		String outFile = args[1]; 

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(RootConfig.class)) {
			
//			String inFile = "D:/datahub/pa_list_home_loc@20160726.dat";
//			String outFile = "D:/datahub/output/pa_list_poi-%s.txt";
			String output = String.format(outFile, sdf.format(System.currentTimeMillis()));
			
			PoiAround poisAround = ctx.getBean(PoiAround.class);
			poisAround.calc(inFile, output);
		}
	}

	private void calc(String inFile, String outFile) throws IOException {
		PrintStream strm = new PrintStream(outFile);
		try (Stream<String> lines = Files.lines(Paths.get(inFile))) {
			lines.map(PoiAround::parseAsCoordinate)
//				 .limit(2)
				 .parallel()
				 .map(c -> this.getPoiInfo(c))
				 .flatMap(s -> s.stream())
				 .map(o -> o.asFlatText())
//				 .forEach(System.out::println);
				 .forEach(o -> strm.println(o));
		} finally {
			strm.close();
		}
	}

	private List<PoiInfo> getPoiInfo(Coordinate coord) {
		List<PoiType> poiTypes = new ArrayList<>();
		return coordService.getAroundPoi(coord, poiTypes);
	}
	
	public void calc(String poiTypes, long radius, String coordinateInputFile, String outFile) throws IOException {
		PrintStream strm = new PrintStream(outFile);
		try (Stream<String> lines = Files.lines(Paths.get(coordinateInputFile))) {
			lines.map(PoiAround::parseAsCoordinate)
				 .limit(2)
				 .parallel()
				 .map(c -> this.getPoiInfo(c, poiTypes, radius))
				 .flatMap(s -> s.stream())
				 .map(o -> o.asFlatText())
				 .forEach(System.out::println);
//				 .forEach(o -> strm.println(o));
		} finally {
			strm.close();
		}
	}
	
	public List<PoiInfo> getPoiInfo(Coordinate coord, String types, long radius) {
		return coordService.getAroundPoi(coord, types, radius);
	}
	
	private static Coordinate parseAsCoordinate(String line) {
		String[] arr = line.split("\t");
		double lng = Double.parseDouble(arr[0]);
		double lat = Double.parseDouble(arr[1]);

		return new Coordinate(lng, lat);
	}
}
