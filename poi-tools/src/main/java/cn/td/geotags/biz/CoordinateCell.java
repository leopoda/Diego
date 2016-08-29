package cn.td.geotags.biz;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.minBy;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

import static java.util.Comparator.comparingInt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import cn.td.geotags.config.RootConfig;
import cn.td.geotags.domain.Coordinate;
import cn.td.geotags.domain.PoiInfo;
import cn.td.geotags.domain.PoiType;
import cn.td.geotags.service.CoordService;
import cn.td.geotags.util.Contants;
import cn.td.geotags.util.CoordinateParser;

@Component
public class CoordinateCell {
	@Autowired
	private CoordService coordService;
	
	private static final int CELL_POI_TYPE = 120302;
	
	public static void main(String[] args) throws IOException {
		if (args.length < 2) {
			System.out.println("Error, at least two input parameters!");
			return;
		}

		String filePath = args[0];
		String prefix = args[1];
		
//		String filePath = "D:/datahub/pa_list_home_loc@20160726.dat";
//		String prefix = "D:/datahub/pa_list_cell-%s.txt";
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		PrintStream strm = new PrintStream(String.format(prefix, sdf.format(System.currentTimeMillis())));

		try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(RootConfig.class)) {
			try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
				CoordinateCell cellAround = ctx.getBean(CoordinateCell.class);
				lines.map(CoordinateParser::parse)
//						 .limit(10)
						// .filter(x -> x.isValid())
						.parallel()
						.map(c -> cellAround.calc(c))
//						.forEach(System.out::println);
						.forEach(s -> strm.println(s));
			} finally {
				strm.close();
			}
		}
	}
	
	public void calc(long radius, String inFile, String outFile) throws IOException {
		calc(radius, Contants.PARAM_COORD_SYS_GPS, inFile, outFile);
	}
	
	public void calc(long radius, String coordsys, String inFile, String outFile) throws IOException {
		PrintStream strm = new PrintStream(outFile);
		try (Stream<String> lines = Files.lines(Paths.get(inFile))) {
			lines.map(CoordinateParser::parse)
				.parallel()
				.map(c -> calc(c, radius, coordsys))
				.forEach(s -> strm.println(s));
		} finally {
			strm.close();
		}
	}
	
	private String calc(Coordinate coord, long radius, String coordsys) {
		return this.calc(coord, c -> coordService.getAroundPoi(coord, String.valueOf(CELL_POI_TYPE), radius, coordsys));
	}
	
	private String calc(Coordinate coord) {
		List<PoiType> poiTypes = new ArrayList<>();
		PoiType poiType = new PoiType();

		poiType.setType(String.valueOf(CELL_POI_TYPE));
		poiTypes.add(poiType);
		
		return calc(coord, c -> coordService.getAroundPoi(c, poiTypes));
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