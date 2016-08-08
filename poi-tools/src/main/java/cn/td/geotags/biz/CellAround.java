package cn.td.geotags.biz;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.minBy;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

import static java.util.Comparator.comparingInt;

import org.springframework.batch.core.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import cn.td.geotags.config.RootConfig;
import cn.td.geotags.domain.Coordinate;
import cn.td.geotags.domain.PoiInfo;
import cn.td.geotags.domain.PoiType;
import cn.td.geotags.service.CoordService;

@Component
public class CellAround {
	@Autowired
	private CoordService coordService;
	
	private static final int CELL_POI_TYPE = 120302;
	
	public static void main(String[] args) throws IOException {
//		if (args.length < 2) {
//			System.out.println("Error, at least two input parameters!");
//			return;
//		}
//
//		String filePath = args[0];
//		String prefix = args[1];
		
		String filePath = "D:/datahub/pa_list_home_loc@20160726.dat";
		String prefix = "D:/datahub/pa_list_cell-%s.txt";
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		PrintStream strm = new PrintStream(String.format(prefix, sdf.format(System.currentTimeMillis())));

		try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(RootConfig.class)) {
			Job j = ctx.getBean(Job.class);
			
			try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
				CellAround cellAround = ctx.getBean(CellAround.class);
				lines.map(line -> parseAsCoordinate(line))
						 .limit(10)
						// .filter(x -> x.isValid())
						.parallel()
						.map(c -> cellAround.calc(c))
						.forEach(System.out::println);
//						.forEach(s -> strm.println(s));
			} finally {
				strm.close();
			}
		}
	}
	
	
	public String calc(Coordinate coord) {
		List<PoiType> poiTypes = new ArrayList<>();
		PoiType poiType = new PoiType();

		poiType.setType(String.valueOf(CELL_POI_TYPE));
		poiTypes.add(poiType);
		
		List<PoiInfo> cells = coordService.getAroundPoi(coord, poiTypes);
		
		Optional<PoiInfo> p = cells.stream().collect(minBy(comparingInt(PoiInfo::getDistance)));
		
		String addr;
		if (p.isPresent()) {
			addr = String.join("\t", String.valueOf(coord.getLng()),
							  		 String.valueOf(coord.getLat()),
							  		 p.get().getProvince() == null ? "" : p.get().getProvince(),
							  		 p.get().getCity() == null ? "" : p.get().getCity(),
							  		 p.get().getDistrict() == null ? "" :  p.get().getDistrict(),
							  		 p.get().getAddress() == null ? "" : p.get().getAddress(),
							  		 p.get().getName() == null ? "" : p.get().getName(),
							  		 p.get().getLocation() == null ? "" : p.get().getLocation());
		} else {
			addr = String.join("\t", String.valueOf(coord.getLng()), String.valueOf(coord.getLat()), "", "", "", "", "", "");
		}
		
		return addr;
	}
	
	private static Coordinate parseAsCoordinate(String line) {
		String[] arr = line.split("\t");
		double lng = Double.parseDouble(arr[0]);
		double lat = Double.parseDouble(arr[1]);

		return new Coordinate(lng, lat);
	}
}
