package cn.td.geotags.biz;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.averagingInt;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.td.geotags.domain.Coordinate;
import cn.td.geotags.domain.GatherPoint;
import cn.td.geotags.domain.PoiInfo;
import cn.td.geotags.util.StreamForker;

public class ReachableStatistics {
	final static int TOP_N = 100;
	private final static ObjectMapper om = new ObjectMapper();

	public static void main(String[] args) throws IOException {
		String[] types = new String[] {"餐饮服务", "购物服务", "体育休闲服务", "ALL"};
		Resource res1 = new FileSystemResource("c:/Users/pc/Downloads/poi-104.txt");
		Resource res2 = new FileSystemResource("c:/Users/pc/Downloads/poi-101.txt");
		

		try (Stream<String> lines = Files.lines(Paths.get(res1.getURI()))) {
			StreamForker<String> sf = new StreamForker<>(lines);
			StreamForker.Results result = sf.fork(types[0], getIdAndDistance(types[0]))
											.fork(types[1], getIdAndDistance(types[1]))
											.fork(types[2], getIdAndDistance(types[2]))
											.fork("testaa",  s -> s.map(ReachableStatistics::parse2)
																   .map(BaiduPoi::mock)
																   .collect(toList()))
											.getResults();
			
//			PrintStream ps = new PrintStream("d:/datahub/output/dayawan.txt");
//			List<BaiduPoi> mockSet = result.get("testaa");
//			mockSet.stream().map(asFlatText()).forEach(o -> ps.println(o));
//			System.out.println();
			
			List<ImmutablePair<String, Integer>> resturant = result.get(types[0]);
			List<ImmutablePair<String, Integer>> brand = result.get(types[1]);
			List<ImmutablePair<String, Integer>> entertainment = result.get(types[2]);
			List<ImmutablePair<String, Integer>> all = Stream.of(resturant, brand, entertainment).flatMap(o -> o.stream()).collect(toList());
			
			try (Stream<String> strm = Files.lines(Paths.get(res2.getURI()))) {
				StreamForker<String> sf2 = new StreamForker<>(strm);
				StreamForker.Results result2 = sf2.fork(types[1], getTopN(brand, TOP_N))
												  .fork(types[0], getTopN(resturant, TOP_N))
												  .fork(types[2], getTopN(entertainment, TOP_N))
												  .fork(types[3], getTopN(all, 150))
												  .getResults();
								
				List<ImmutableTriple<Integer, Integer, PoiInfo>> grp0 = result2.get(types[0]);
				grp0.stream()
//					.map(BaiduPoi::parse)
					.map(asFlatText())
					.forEach(System.out::println);
				System.out.println();
				
				List<ImmutableTriple<Integer, Integer, PoiInfo>> grp1 = result2.get(types[1]);
				grp1.stream()
//					.map(BaiduPoi::parse)
					.map(asFlatText())
					.forEach(System.out::println);
				System.out.println();
				
				List<ImmutableTriple<Integer, Integer, PoiInfo>> grp2 = result2.get(types[2]);
				grp2.stream()
//					.map(BaiduPoi::parse)
					.map(asFlatText())
					.forEach(System.out::println);
				System.out.println();
				
//				PrintStream ps2 = new PrintStream("d:/datahub/dayawan2.txt");
				List<ImmutableTriple<Integer, Integer, PoiInfo>> grp3 = result2.get(types[3]);
				grp3.stream()
//					.map(BaiduPoi::parse)
					.map(asFlatText())
					.forEach(System.out::println);
//					.forEach(o -> ps2.println(o));
				System.out.println();
				
				
			}
		}
		
//		BaiduPoi baiduPoi = new BaiduPoi();
//		System.out.println(baiduPoi.geoconv("114.124301,22.588940"));
	}

	private static Function<ImmutableTriple<Integer, Integer, PoiInfo>, String> asFlatText() {
		return t -> {
			int rank = t.left;
			int distance = t.middle;
			PoiInfo p = t.right;
			return String.join("\t", String.valueOf(rank),
						String.valueOf(distance),
						p.getLocation(),
						p.getName());
			};
	}
	
//	private static Function<BaiduPoi, String> asFlatText() {
//		return o -> {
//			try {
//				return om.writeValueAsString(o);
//			} catch (Exception e) {
//				return null;
//			}
//		};
//	}

	private static Function<Stream<String>, ?> getTopN(List<ImmutablePair<String, Integer>> group, int topN) {
		return s -> s.map(ReachableStatistics::parse)
		   .filter(o -> o.right.getCity().equals("深圳市"))
		   .filter(o -> o.left.getHour() > 9 && o.left.getHour() < 16)
		   .filter(o -> group.stream().anyMatch(x -> x.left.equals(o.right.getId())))
		   .collect(groupingBy(o -> o.right.getId()))
		   .entrySet()
		   .stream()
		   .map(o -> ImmutablePair.of(o.getValue().stream().collect(Collectors.summingInt(x -> x.left.getCount()))
//		   .map(o -> ImmutablePair.of(o.getValue().stream().collect(Collectors.averagingInt(x -> x.left.getCount()))
				   					 ,o.getValue().stream().findFirst().get().right))
		   .sorted((o1, o2) -> o2.left.intValue() > o1.left.intValue() ? 1 : (o2.left.intValue() == o1.left.intValue() ? 0 : -1))
		   .limit(topN)
		   .map(p -> ImmutableTriple.of(p.left, // count
				   group.stream().filter(g -> g.left.equals(p.right.getId())).findFirst().get().right, // distance
				   p.right)) // poiInfo
		   .collect(toList());
	}

	private static Function<Stream<String>, ?> getIdAndDistance(String type) {
		return s -> s.filter(line -> line.split("\t")[10].contains(type))
					 .map(line -> ImmutablePair.of(line.split("\t")[12], Integer.parseInt(line.split("\t")[4])))
					 .collect(toList());
	}
	
	private static ImmutablePair<GatherPoint, PoiInfo> parse(String line) {
		String[] fields = line.split("\t");
		
		String mac = fields[0];
		boolean isWorkday = fields[1].equals("Y") ? true : false;
		String month = "";
		int hour = Integer.parseInt(fields[2]);
		int count = Integer.parseInt(fields[3]);
		double lng = Double.parseDouble(fields[4]);
		double lat = Double.parseDouble(fields[5]);
		
		GatherPoint gp = new GatherPoint(mac, isWorkday, month, hour, lng, lat, count);
		
		String id = fields[16];
		String type = fields[14];
		String typecode = fields[15];
		String name = fields[17];
		String address = fields[12];
		int distance = Integer.parseInt(fields[8]);

		String province = fields[9];
		String city = fields[10];
		String district = fields[11];
		String location = fields[13];
		
		Coordinate c1 = new Coordinate(Double.parseDouble(fields[4]), Double.parseDouble(fields[5]));
		Coordinate c2 = new Coordinate(Double.parseDouble(fields[6]), Double.parseDouble(fields[7]));
		
		PoiInfo p = new PoiInfo(id, type, typecode, name, address, distance, province, city, district, location);
		p.setCenter(c1);
		p.setAmapCenter(c2);

		return ImmutablePair.of(gp, p);
	}
	
	private static PoiInfo parse2(String line) {
		String[] fields = line.split("\t");
		int distance = Integer.parseInt(fields[4]);
		String province = fields[5];
		String city = fields[6];
		String district = fields[7];
		String address = fields[8];
		String location = fields[9];
		String type = fields[10];
		String typecode = fields[11];
		String id = fields[12];
		String name = fields[13];
		
		PoiInfo poiInfo = new PoiInfo(id, type, typecode, name, address, distance, province, city, district, location);
		return poiInfo;
	}
}
