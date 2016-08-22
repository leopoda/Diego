package cn.td.geotags.biz;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.maxBy;
import static java.util.stream.Collectors.minBy;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.averagingInt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.tuple.ImmutablePair;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.td.geotags.config.PoiConfig;
import cn.td.geotags.config.RootConfig;
import cn.td.geotags.domain.Coordinate;
import cn.td.geotags.domain.PoiInfo;
import cn.td.geotags.domain.PoiSummary;
import cn.td.geotags.domain.PoiType;
import cn.td.geotags.service.CoordService;
import cn.td.geotags.util.CoordinateParser;
import cn.td.geotags.util.StreamForker;

@Component
public class PoiAroundStatistics {
	@Autowired
	CoordService coordService;
	
	@Autowired
	PoiConfig poiConfig;

	public static void main(String[] args) throws IOException {
		if (args.length < 2) {
			System.out.println("Error, at least two input parameters!");
			return;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(RootConfig.class)) {
			
			String inFile = args[0]; // "D:/datahub/pa_list_home_loc@20160726.dat"
			String outFile = args[1]; // "D:/datahub/output/pa_list_poi-%s.txt"
			PrintStream strm = new PrintStream(String.format(outFile, sdf.format(System.currentTimeMillis())));
			
			try (Stream<String> lines = Files.lines(Paths.get(inFile))) {
				PoiAroundStatistics poiStats = ctx.getBean(PoiAroundStatistics.class);
				lines.map(CoordinateParser::parse)
//					 .limit(10)
					 .map(c -> poiStats.calculate(c))
					 .flatMap(s -> s.stream())
//					 .forEach(System.out::println);
					 .forEach(o -> strm.println(o));
			} finally {
				strm.close();
			}
		}
	}

	public List<String> calculate(Coordinate coord) {
		
		// 大组分类
		List<ImmutablePair<String, String>> groups = Arrays.asList(
				ImmutablePair.of("01", "聚集点基本特征"),
				ImmutablePair.of("0201", "家庭组成"),
				ImmutablePair.of("0202", "健康情况"),
				ImmutablePair.of("0203", "生活作息"),
				ImmutablePair.of("0204", "消费能力、消费场所"),
				ImmutablePair.of("0205", "工作场所"),
				ImmutablePair.of("0206", "兴趣爱好"),
				ImmutablePair.of("0207", "养车习惯"));
			
		List<PoiType> poiTypes = getConfigPoiTypes().stream()
				.filter(p -> !p.getName().equals("root"))
				.filter(p -> groups.stream().anyMatch(d -> d.left.equals(p.getParentId())))
				.collect(toList());

		// 获取周边 poi
		List<PoiInfo> poiResult = coordService.getAroundPoi(coord, poiTypes);

		// poi 标签分组并计算四类指标	
		StreamForker.Results result = poiTypes.stream()
											  .reduce(new StreamForker<PoiInfo>(poiResult.stream()),
													  (sf, e) -> {return sf.fork(e.getId(), calcKPI(e));},
													  (sf1, sf2) -> {return sf2;})
											  .getResults();
		
		List<PoiSummary> psResult = poiTypes.stream()
											.parallel()
		  									.map(o -> (PoiSummary)result.get(o.getId()))
		  									.collect(toList());
		
		return psResult.stream()
					   .map(asFlatText(coord, groups))
					   .collect(toList());
//					   .forEach(System.out::println);
	}

	private Function<PoiSummary, String> asFlatText(Coordinate coord, List<ImmutablePair<String, String>> groups) {
		return o ->   String.join("\t", String.valueOf(coord.getLng()), 
										String.valueOf(coord.getLat()), 
										groups.stream().filter(x -> x.left.equals(o.getPoiType().getParentId())).map(x -> x.right).findFirst().orElse(""),
										o.getPoiType().getName(),
										String.join("\t", o.getCount() == null ? "" : String.valueOf(o.getCount()), 
														  o.getAvgDistance() == null ? "" : String.format("%.2f", o.getAvgDistance()), 
														  o.getMinDistance() == null ? "" : String.valueOf(o.getMinDistance()), 
														  o.getMaxDistance() == null ? "" : String.valueOf(o.getMaxDistance())));
	}

	private Function<Stream<PoiInfo>, PoiSummary>  calcKPI(PoiType poiType) {
		return strm -> {
			final List<String> names = Arrays.asList("count", "avg", "max", "min");
			PoiSummary ps = new PoiSummary();
			ps.setPoiType(poiType);
	
			StreamForker<PoiInfo> sf = new StreamForker<>(strm);
			StreamForker.Results result = sf.fork(names.get(0), s -> s.filter(hasTypeMatched(poiType.getType()))
								   								 	  .collect(counting()))
											.fork(names.get(1), s -> s.filter(hasTypeMatched(poiType.getType()))
															   		  .collect(averagingInt(PoiInfo::getDistance)))
											.fork(names.get(2), s -> s.filter(hasTypeMatched(poiType.getType()))
																	  .collect(maxBy(comparingInt(PoiInfo::getDistance))))
											.fork(names.get(3), s -> s.filter(hasTypeMatched(poiType.getType()))
																	  .collect(minBy(comparingInt(PoiInfo::getDistance))))
											.getResults();
			
			long count = result.get(names.get(0));
			double avgDistance = result.get(names.get(1));
			Optional<PoiInfo> poiMax = result.get(names.get(2));
			Optional<PoiInfo> poiMin = result.get(names.get(3));
			
			ps.setCount(count > 0 ? count : null);
			ps.setAvgDistance(avgDistance > 0 ? avgDistance : null);
			ps.setMaxDistance(poiMax.isPresent() ? poiMax.get().getDistance() : null);
			ps.setMinDistance(poiMin.isPresent() ? poiMin.get().getDistance() : null);

			return ps;
		};
	}

	private Predicate<PoiInfo> hasTypeMatched(String type) {
		return p -> Arrays.asList(p.getTypecode().split("\\|"))
						  .stream()
						  .anyMatch(s -> type.indexOf(s) != -1);
	}
	
	private List<PoiType> getConfigPoiTypes() {
		ObjectMapper m = new ObjectMapper();
		List<PoiType> list = new ArrayList<>();
		String content = poiConfig.getPoiTypeInfo();
		try {
			list = Arrays.asList(m.readValue(content, PoiType[].class));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return list;
	}
}