package com.pingan.tags.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
public class GatherPoint {
	private String tdid;
	
	private String month;
//	private String day;
	private int hour;
	
	private Coordinate coordinate;
//	private String source;

	private boolean isWeekend;
	private int count;
	
	private static ObjectMapper MAPPER = new ObjectMapper();

	public GatherPoint(String tdid, boolean isWeekend, String month, int hour, double lng, double lat, /*String source,*/ int count) {
		this.tdid = tdid;
		this.isWeekend = isWeekend;
		this.month = month;
		this.hour = hour;
		this.coordinate = new Coordinate();
		this.coordinate.setLat(lat);
		this.coordinate.setLng(lng);
//		this.source = source;
		this.count = count;
	}

	@Override
	public String toString() {
		try {
			return new String(MAPPER.writeValueAsBytes(this));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "";
		}
	}
}
