package com.pingan.tags.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.io.Serializable;

@Getter
@Setter
@ToString
public class Coordinate implements Serializable {
	private static final long serialVersionUID = 1L;

	private double lng;
	private double lat;
	
	public Coordinate() {}
	
	public Coordinate(double lng, double lat) {
		this.lng = lng;
		this.lat = lat;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Coordinate) {
			Coordinate c = (Coordinate)o;
			return String.valueOf(lng).equals(String.valueOf(c.lng)) && String.valueOf(lat).equals(String.valueOf(c.lat));
		}
		return false;
		
	}
	
	public boolean isValid() {
		return (lng >= 73 && lng <= 135) && (lat >= 4 && lat <= 53);
	}
}
