package cn.td.geotags.biz;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.groupingBy;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import cn.td.geotags.domain.Coordinate;
import cn.td.geotags.domain.GatherPoint;
import cn.td.geotags.domain.PoiInfo;
import cn.td.geotags.util.StreamForker;

public class CoordinateGatherPointAroundIntersect {
	public static void main(String[] args) throws IOException {
		String[] types = new String[] {"餐饮服务", "购物服务", "体育休闲服务"};
		String f1 = "c:/Users/pc/Downloads/poi-104.txt";
		String f2 = "c:/Users/pc/Downloads/poi-101.txt";

		CoordinateGatherPointAroundIntersect pi = new CoordinateGatherPointAroundIntersect();
		List<List<String>> list = pi.intersect(f1, f2, types, "深圳市", 10);
		list.stream()
			.forEach(o -> o.stream().forEach(System.out::println));
	}

	/*
	 * 经纬度周边 POI 和 位置聚集点周边 POI 取交集
	 */
	public List<List<String>> intersect(String f1, String f2, String[] types, String city, int topN) throws IOException {
		Resource res1 = new FileSystemResource(f1);
		Resource res2 = new FileSystemResource(f2);
		
		try (Stream<String> lines = Files.lines(Paths.get(res1.getURI()))) {
			StreamForker.Results result = IntStream.rangeClosed(1, types.length)
				 .boxed()
				 .reduce(new StreamForker<String>(lines),
						 (sf, e) -> {int idx = e - 1; return sf.fork(types[idx], getIdAndDistance(types[idx], city));},
						 (sf1, sf2) -> {return sf2;})
				 .getResults();
			
			/*
			 * <POI Id, 距离>
			 */
			@SuppressWarnings("unchecked")
			List<List<ImmutablePair<String, Integer>>> idGroups = IntStream.rangeClosed(1, types.length)
				 .boxed()
				 .map(i -> {int idx = i - 1; return (List<ImmutablePair<String, Integer>>)result.get(types[idx]);})
				 .reduce(new ArrayList<>(),
						 (l, e) -> {l.add(e); return l;},
						 (l1, l2) -> {return l2;});
			
			/*
			 * 生成  ALL 分组, 等于前面所有的分组取并集
			 */
			List<ImmutablePair<String, Integer>> groupAll = Stream.of(idGroups).flatMap(l -> l.stream()).flatMap(o -> o.stream()).sorted().collect(toList());
			idGroups.add(groupAll);
			
			List<String> allTypes = new ArrayList<>();
			allTypes.addAll(Stream.of(types).collect(toList()));
			allTypes.add("ALL");
			String[] allTypesArray = allTypes.toArray(new String[allTypes.size()]);
			
			/*
			 * 求交集, 取 top N
			 */
			try (Stream<String> strm = Files.lines(Paths.get(res2.getURI()))) {
				StreamForker.Results result2 = IntStream.rangeClosed(1, allTypesArray.length)
					 .boxed()
					 .reduce(new StreamForker<String>(strm),
							  (sf3, e) -> {int idx = e - 1; return sf3.fork(allTypesArray[idx], getTopN(idGroups.get(idx), city, topN));},
							  (sf4, sf5) -> {return sf5;})
					 .getResults();

				/*
				 * <rank, distance, poiInfo>
				 */
				return IntStream.rangeClosed(1,  allTypesArray.length)
					 .boxed()
					 .map(i -> {int idx = i - 1;
						 		List<ImmutableTriple<Integer, Integer, PoiInfo>> grp = result2.get(allTypesArray[idx]);
					 			return grp.stream().map(asFlatText()).collect(toList());})
					 .collect(toList());
			}
		}
	}

	private Function<ImmutableTriple<Integer, Integer, PoiInfo>, String> asFlatText() {
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

	private Function<Stream<String>, List<ImmutableTriple<Integer, Integer, PoiInfo>>> getTopN(List<ImmutablePair<String, Integer>> group, String city, int topN) {
		return s -> s.map(line -> parse(line))
		   .parallel()
		   .filter(o -> o.right.getCity().contains(city))
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

	private Function<Stream<String>, List<ImmutablePair<String, Integer>>> getIdAndDistance(String type, String city) {
		return s -> s.parallel()
					 .map(line -> parsePoiInfo(line))
					 .filter(o -> o.getCity().contains(city))
					 .filter(o -> o.getType().contains(type))
					 .map(o -> ImmutablePair.of(o.getId(), o.getDistance()))
					 .collect(toList());
	}
	
	private ImmutablePair<GatherPoint, PoiInfo> parse(String line) {
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
	
	private PoiInfo parsePoiInfo(String line) {
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
