package com.pingan.tags.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Address {
	private long id;
	private long offset;

	private String month;
	private int hour;

	private double lng;
	private double lat;
	private String source;
	
	private String country;
	private String province;
	private String city;
	private String district;
	private String township;
	private String address;
	
	private boolean isWeekend;
	private int count;
	
	public Address(long id, 
				   long offset, 
				   String month, 
				   int hour, 
				   double lng, 
				   double lat, 
				   String country,
				   String province, 
				   String city, 
				   String district, 
				   String township, 
				   String address, 
				   boolean isWeekend,
				   int count) {
		this.id = id;
		this.offset = offset;
		
		this.month = month;
		this.hour = hour;
		
		this.lng = lng;
		this.lat = lat;
		this.source = source;
		
		this.country = country;
		this.province = province;
		this.city = city;
		this.district = district;
		this.township = township;
		this.address = address;
		
		this.isWeekend = isWeekend;
		this.count = count;
	}
	
	public Address() {
		
	}
	
	@Override
	public String toString() {
		return String.join("\t", 
				Long.toString(offset), 
				isWeekend ? "Y" : "N",
				month,
				String.format("%02d", hour),
				Double.toString(lng),
				Double.toString(lat),
//				source,
				country, 
				province, 
				city, 
				district, 
				township, 
				address);
	}
}
