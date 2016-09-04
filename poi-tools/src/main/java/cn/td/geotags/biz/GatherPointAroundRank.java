package cn.td.geotags.biz;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.core.io.FileSystemResource;

import cn.td.geotags.domain.Coordinate;
import cn.td.geotags.domain.GatherPoint;
import cn.td.geotags.domain.PoiInfo;
import cn.td.geotags.util.ParserUtil;

public class GatherPointAroundRank {
	public static void main(String[] args) throws IOException {
		String fileName = "c:/Users/pc/Downloads/poi-111.txt";
		FileSystemResource resource = new FileSystemResource(fileName);
		PrintStream ps = new PrintStream("d:/datahub/output/test.txt");
		
		List<String> competitors = Arrays.asList("怡景中心城", 
				"君尚百货", "九方购物中心", "喜荟城", "花园城", "欢乐海岸", "深圳华润万象城", "星河cocoCity",
				"COCO Park", "万科广场", "华强北九方购物中心", "金光华广场", "茂业百货", "益田假日广场", 
				"海雅缤纷城", "海上世界广场", "嘉里建设广场", "皇庭广场","cocopark", "kkmal", "intown", "海岸城");
		
		try (Stream<String> strm = Files.lines(Paths.get(resource.getURI()))) {
			strm //.limit(1000000)
				.parallel()
				.map(ParserUtil::parseAsGatherPointPoiInfoPair)
				.filter(o -> o.right.getCity().contains("深圳"))
				.filter(p -> competitors.stream().anyMatch(o -> p.right.getName().contains(o)))
				.collect(groupingBy(o -> o.right.getId()))
				.entrySet().stream()
				.map(o -> ImmutablePair.of(o.getValue().stream().collect(summingInt(x -> x.left.getCount()))
	   					 				  ,o.getValue().stream().findFirst().get().right))
				.sorted((o1, o2) -> o2.left.intValue() > o1.left.intValue() ? 1 : (o2.left.intValue() == o1.left.intValue() ? 0 : -1))
//				.limit(1000)
				.map(t -> {
					int rank = t.left;
					PoiInfo p = t.right;
					return String.join("\t", 
								String.valueOf(rank),
								p.getLocation(),
								p.getCity(),
								p.getDistrict(),
								p.getName(),
								p.getType(),
								p.getAddress());
					})
//				.forEach(System.out::println);
				.forEach(o -> ps.println(o));
		}
		
	}
}
