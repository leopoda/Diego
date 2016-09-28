package cn.td.geotags.util;

import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PoiUtil {
	private static final String POI_TYPE_RESOURCE_FILE = "/poiCatalog.dat";
	private static final Map<String, String> DICT;
	
	static {
		Resource resource = new ClassPathResource(POI_TYPE_RESOURCE_FILE);
		try (Stream<String> strm = Files.lines(Paths.get(resource.getURI()))) {
			
			DICT = strm.map(s -> {String[] arr = s.split("\t"); return ImmutablePair.of(arr[0], arr[3]);}) // 类别代码, POI 第三类名称
					   .collect(toMap(o -> o.left, o -> o.right));
			
			DICT.putIfAbsent(Contants.POI_RANK_TYPE_CODE_BIND_ALL, "综合");
		} catch (IOException e) {
			log.error("get poi type name failed", e);
			throw new RuntimeException(e);
		}
	}
	
	public static String getName(String id) {
		return DICT.get(id);
	}
}
