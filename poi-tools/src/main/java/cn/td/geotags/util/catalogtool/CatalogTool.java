package cn.td.geotags.util.catalogtool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CatalogTool {
	private final static ObjectMapper OM = new ObjectMapper();
	
	public static void main(String[] args) throws IOException {
		new CatalogTool().amapPoiCatalogFlatTextFileToJson();
	}

	public void amapPoiCatalogFlatTextFileToJson() throws JsonProcessingException, IOException {
		Resource resource = new ClassPathResource("/poiCatalog.dat");
		try (Stream<String> lines = Files.lines(Paths.get(resource.getURI()))) {
			
			Map<String, Map<String, List<PoiTag>>> catalog = 
			lines.map(PoiTag::parse)
				 .collect(groupingBy(PoiTag::getFirstPartOfCode, groupingBy(PoiTag::getSecondPartOfCode)));
			
			System.out.println(getJson(catalog));
		}
	}

	private String getJson(Map<String, Map<String, List<PoiTag>>> catalog) throws JsonProcessingException {
		List<PoiCatalog> pa = 
		catalog.entrySet().stream()
			   .map(o -> o.getKey())
			   .distinct()
			   .sorted()
			   .map(o -> ImmutablePair.of(o, getTag1(catalog, o)))
			   .map(o -> {PoiCatalog pc = new PoiCatalog(); pc.setText(o.right); pc.setNodes(getTag2(catalog, o.left)); return pc;})
			   .collect(toList());
		
		return OM.writeValueAsString(pa);
	}

	private String getTag1(Map<String, Map<String, List<PoiTag>>> catalog, String partCode) {
		String tag1 = catalog.entrySet().stream()
				.filter(o -> o.getKey().equals(partCode))
				.map(o -> o.getValue())
				.flatMap(o -> o.entrySet().stream())
				.flatMap(o -> o.getValue().stream())
				.map(o -> o.getTag1())
				.distinct()
				.findFirst().get();
		
		return tag1;
	}

	private List<PoiTagNode> getTag2(Map<String, Map<String, List<PoiTag>>> catalog, String parentPartCode) {
		List<PoiTagNode> p = catalog.entrySet().stream()
				.filter(o -> o.getKey().equals(parentPartCode))
				.map(o -> o.getValue())
				.flatMap(o -> o.entrySet().stream())
				.sorted((o1, o2) -> o1.getKey().compareTo(o2.getKey()))
				.flatMap(o -> o.getValue().stream())
				.map(o -> o.getTag2() + "\t" + o.getSecondPartOfCode())
				.distinct()
				.map(o -> {String[] arr = o.split("\t"); PoiTagNode pn = new PoiTagNode(); pn.setText(arr[0]); pn.setNodes(getTag3(catalog, parentPartCode, arr[1])); return pn;})
				.collect(toList());
		
		return p;
	}
	
	private List<PoiTagLeaf> getTag3(Map<String, Map<String, List<PoiTag>>> catalog, String parentParentPartCode, String parentPartCode) {
		return catalog.entrySet().stream()
		   .filter(o -> o.getKey().equals(parentParentPartCode))
		   .map(o -> o.getValue())
		   .flatMap(o -> o.entrySet().stream())
		   .sorted((o1, o2) -> o1.getKey().compareTo(o2.getKey()))
		   .filter(o -> o.getKey().equals(parentPartCode))
		   .flatMap(o -> o.getValue().stream())
		   .sorted((o1, o2) -> o1.getCode().compareTo(o2.getCode()))
		   .map(o -> {PoiTagLeaf pl = new PoiTagLeaf(); pl.setCode(o.getCode()); pl.setText(o.getTag3()); return pl;})
		   .collect(toList());
	}
}
