package cn.td.geotags.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PoiInfo {
	private Coordinate center;
	private Coordinate amapCenter;
	
	private String id;
	private String type;
	private String typecode;
	private String name;
	private String address;
	private int distance; // 离中心点距离, 米
	private String location;
	
	private String province;
	private String city;
	private String district; // 区域名称
	
//	private String bizArea;
	
	public PoiInfo (String id, String type, String typecode, String name, String address, int distance, String province, String city, String district, String location) {
		this.id = id;
		this.type = type;
		this.typecode = typecode;
		this.name = name;
		this.address = address;
		this.distance = distance;
		
		this.province = province;
		this.city = city;
		this.district = district;
		this.location = location;
	}
	
	public String asFlatText() {
		return String.join("\t", 
				String.valueOf(center.getLng()),
				String.valueOf(center.getLat()),
				String.valueOf(amapCenter.getLng()),
				String.valueOf(amapCenter.getLat()),
				String.valueOf(this.distance), 
				this.province, 
				this.city, 
				this.district, 
				this.address,
				this.location, 
				this.type, 
				this.typecode, 
				this.id,
				this.name);
	}
}
