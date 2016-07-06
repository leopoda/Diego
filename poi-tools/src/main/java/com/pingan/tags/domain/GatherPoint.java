package com.pingan.tags.domain;

import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
public class GatherPoint {
	private String tdid;
	
	private String month;
	private String day;
	private int hour;
	
	private Coordinate coordinate;

	private boolean isWeekend;
	private int count;
	
	private static ObjectMapper MAPPER = new ObjectMapper();

	public GatherPoint(String tdid, boolean isWeekend, String month, int hour, double lng, double lat, int count) {
		this.tdid = tdid;
		this.isWeekend = isWeekend;
		this.month = month;
		this.hour = hour;
		this.coordinate = new Coordinate();
		this.coordinate.setLat(lat);
		this.coordinate.setLng(lng);
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
	
	public static GatherPoint build4AmapQuery(GatherPoint r) {
		Coordinate o = new Coordinate();
		o.setLng(0);
		o.setLat(0);

		Optional<Coordinate> c = r.getCoordinate().toAmapCoordinate();
		return new GatherPoint(
				r.getTdid(), 
				r.isWeekend(), 
				r.getMonth(), 
				r.getHour(), 
				c.orElse(o).getLng(), 
				c.orElse(o).getLat(),
				r.getCount());
	}
}
