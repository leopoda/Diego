package com.pingan.tags.domain;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class AmapCoordinateResponse {
	private String status;
	private String info;
	private String infocode;
	private String locations;
	private static ObjectMapper MAPPER = new ObjectMapper();

	public static AmapCoordinateResponse parse(String content) {
		try {
			return MAPPER.readValue(content, AmapCoordinateResponse.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public Coordinate parseAsCoordinate() {
		Coordinate c = new Coordinate();
		if (Integer.parseInt(status) == 1) {
			String[] arr = locations.split(",");

			c.setLng(Double.parseDouble(arr[0]));
			c.setLat(Double.parseDouble(arr[1]));
		} else {
			log.error("Convert coordinate failed");
		}
		return c;
	}
}
