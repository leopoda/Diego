package cn.td.geotags.biz;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;
import static java.util.stream.Collectors.toList;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import cn.td.geotags.domain.GatherPoint;
import cn.td.geotags.domain.HourPair;
import cn.td.geotags.domain.PoiInfo;
import cn.td.geotags.domain.RankCondition;
import cn.td.geotags.util.Contants;
import cn.td.geotags.util.ParserUtil;
import cn.td.geotags.util.PoiUtil;
import cn.td.geotags.util.StreamForker;

enum DayType {
	WEEKDAY, WEEKEND, DONTCARE
}

@Component
public class GatherPointAroundRank {
	private static final String outputFile = "d:/datahub/output/test-%s.txt";
	private static List<String> typeCodes = Arrays.asList("050000");

	public static void main(String[] args) throws IOException {
		String fileName = "D:/datahub/output/top-20.txt";
		
		RankCondition condition = new RankCondition();
		condition.setRankInputFile(fileName);
		condition.setRankOutputPrefix(outputFile);
		condition.setPoiTypeCode(typeCodes);
		
		condition.setProvince("all");
		condition.setCity("all");
		condition.setDistrict("all");
		
		HourPair p2 = new HourPair();
		p2.setStartHour(0);
		p2.setEndHour(23);
		
		List<HourPair> hourPairList = new ArrayList<>();
		hourPairList.add(p2);
		
		condition.setHourRange(hourPairList);
		condition.setDayType("d");
		condition.setTopN(100);
		
		GatherPointAroundRank rank = new GatherPointAroundRank();
		rank.calc(condition);
	}

	@SuppressWarnings("unchecked")
	public void calc(RankCondition rankCondition) throws IOException {
		String fileName = rankCondition.getRankInputFile();
		List<String> typeCodes = rankCondition.getPoiTypeCode();
		FileSystemResource resource = new FileSystemResource(fileName);

		try (Stream<String> strm = Files.lines(Paths.get(resource.getURI()))) {
			StreamForker.Results result = IntStream.rangeClosed(1, typeCodes.size())
					 .boxed()
					 .reduce(new StreamForker<String>(strm), 
							(sf, e) -> { int idx = e - 1; return sf.fork(typeCodes.get(idx), calcKPI(Arrays.asList(typeCodes.get(idx)), rankCondition)); },
							(sf1, sf2) -> { return sf2; })
					 .fork(Contants.POI_RANK_TYPE_CODE_BIND_ALL, calcKPI(typeCodes, rankCondition))
					 .getResults();

			List<String> typeCodes2 = typeCodes.stream().collect(toList());
			typeCodes2.add(Contants.POI_RANK_TYPE_CODE_BIND_ALL);
			
			/*
			 * 每个流输出到一个文件
			 */
			List<PrintStream> psList = IntStream.rangeClosed(1, typeCodes2.size())
					 .boxed()
					 .map(o -> String.format(rankCondition.getRankOutputPrefix(), PoiUtil.getName(typeCodes2.get(o - 1))))
					 .map(s -> {
						try {
							return new PrintStream(s);
						} catch (Exception e) {
							return null;
						}
					  })
					 .collect(toList());
			
			IntStream.rangeClosed(1, typeCodes2.size())
					 .boxed()
					 .map(o -> {int idx = o - 1; return ImmutablePair.of(psList.get(idx), (List<String>)result.get(typeCodes2.get(idx)));})
					 .filter(o -> o.left != null && o.right != null)
					 .forEach(p -> {p.right.stream().forEach(s -> p.left.println(s)); p.left.close();});
//					 .forEach(p -> {p.right.stream().forEach(s -> System.out.println(s));});
		}
	}

	private Function<Stream<String>, List<String>> calcKPI(List<String> typeCodeList, RankCondition rankCondition) {
		String province = rankCondition.getProvince();
		String city = rankCondition.getCity();
		String district = rankCondition.getDistrict();
		DayType dt = rankCondition.getDayType().equalsIgnoreCase("w") ? DayType.WEEKDAY : (rankCondition.getDayType().equalsIgnoreCase("e") ? DayType.WEEKEND : DayType.DONTCARE);
		
		return f -> f.map(ParserUtil::parseAsGatherPointPoiInfoPair)
						.filter(isRegionMatched(province, city, district)) 
						.filter(isDayMatched(dt))
						.filter(isHourRangeMatched(rankCondition.getHourRange()))
						.filter(o -> typeCodeList.stream().anyMatch(c -> o.right.getTypecode().contains(c))) 
						.collect(groupingBy(o -> o.right.getId()))
						.entrySet().stream()
						.map(o -> ImmutablePair.of(o.getValue().stream().map(y -> y.left)
																		.map(y -> y.getTdid())
//																		.filter(y -> {
//																			return o.getValue().stream().map(x -> x.left).map(x -> x.getTdid()).filter(s -> s.equals(y)).count() > 4;
//																		 })
																		.distinct()
																		.collect(summingInt(x -> 1))
			   					 				  ,o.getValue().stream().findFirst().get().right))
						.sorted((o1, o2) -> o2.left.intValue() > o1.left.intValue() ? 1 : (o2.left.intValue() == o1.left.intValue() ? 0 : -1))
						.filter(o -> o.left > 0)
						.limit(rankCondition.getTopN())
						.map(t -> {
							int rank = t.left;
							PoiInfo p = t.right;
							return String.join("\t", 
										String.valueOf(rank),
										p.getId(),
										p.getProvince(),
										p.getCity(),
										p.getDistrict(),
										p.getName(),
										p.getType(),
										p.getLocation(),
										p.getAddress()
										);
							})
						.collect(toList());
	}
	
	/*
	 * 按指定的省份, 城市, 地区过滤 POI 数据
	 */
	private Predicate<ImmutablePair<GatherPoint, PoiInfo>> isRegionMatched(String province, String city, String district) {
		return o -> {
			boolean matched = true;
			if (!province.equalsIgnoreCase(Contants.POI_RANK_PROVINCE_DEFAULT_ALL)) {
				matched = o.right.getProvince().contains(province);
			}
			
			if (!city.equalsIgnoreCase(Contants.POI_RANK_CITY_DEFAULT_ALL)) {
				matched = matched && o.right.getCity().contains(city);
			}
			
			if (!district.equalsIgnoreCase(Contants.POI_RANK_DISTRICT_DEFAULT_ALL)) {
				matched = matched && o.right.getDistrict().contains(district);
			}
			return matched;
		};
	}
	
	/*
	 * 按工作日, 非工作日 过滤聚集点输出的 POI 数据
	 */
	private Predicate<ImmutablePair<GatherPoint, PoiInfo>> isDayMatched(DayType day) {
		return o -> {
			if (day == DayType.DONTCARE) {
				return true;
			} else {
				boolean weekday = (day == DayType.WEEKDAY);
				return o.left.isWorkday() == weekday;
			}
		};
	}
	
	/*
	 * 按多个时间段过滤数据, 多个时间段条件为 "或" 的关系
	 */
	private Predicate<ImmutablePair<GatherPoint, PoiInfo>> isHourRangeMatched(List<HourPair> hours) {
		return o -> hours.stream().anyMatch(x -> o.left.getHour() >= x.getStartHour() && o.left.getHour() <= x.getEndHour());
	}
}
