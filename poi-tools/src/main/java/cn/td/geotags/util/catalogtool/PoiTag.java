package cn.td.geotags.util.catalogtool;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.springframework.util.Assert;

@Getter
@Setter
@ToString
public class PoiTag {
	private String code;
	private String tag1;
	private String tag2;
	private String tag3;
	
	public static PoiTag parse(String line) {
		String[] arr = line.split("\t");
		PoiTag entity = new PoiTag();
		
		entity.setCode(arr[0]);
		entity.setTag1(arr[1]);
		entity.setTag2(arr[2]);
		entity.setTag3(arr[3]);
		return entity;
	}
	
	private String getPartOfCode(int num) {
		Assert.isTrue(num > 0 && num <= code.length());
		return code.substring(0, num);
	}
	
	public String getFirstPartOfCode() {
		return getPartOfCode(2);
	}
	
	public String getSecondPartOfCode() {
		return getPartOfCode(4);
	}
}
