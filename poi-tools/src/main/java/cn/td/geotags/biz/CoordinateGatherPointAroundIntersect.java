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

import cn.td.geotags.domain.PoiInfo;
import cn.td.geotags.util.ParserUtil;
import cn.td.geotags.util.StreamForker;

public class CoordinateGatherPointAroundIntersect {
	public static void main(String[] args) throws IOException {
		String[] types = new String[] {"购物服务"};
		String f1 = "D:/datahub/input/poi-113_cocopark-福田.txt";
		String f2 = "D:/datahub/input/poi-110_tdidshop.txt";

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
		return s -> s.map(ParserUtil::parseAsGatherPointPoiInfoPair)
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
					 .map(ParserUtil::parsePoiInfo)
					 .filter(o -> o.getCity().contains(city))
					 .filter(o -> o.getType().contains(type))
					 .map(o -> ImmutablePair.of(o.getId(), o.getDistance()))
					 .collect(toList());
	}
}
