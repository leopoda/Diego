package cn.td.geotags.amap.regeo;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.td.geotags.domain.Coordinate;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
@ToString
public class CoordinateResponse {
	private String status;
	private String info;
	private String infocode;
	private String locations;
	private static ObjectMapper MAPPER = new ObjectMapper();

	public static CoordinateResponse parse(String content) {
		try {
			return MAPPER.readValue(content, CoordinateResponse.class);
		} catch (IOException e) {
			log.error("parse failed", e);
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
			log.error("invalid coordinate:" + this.toString());
		}
		return c;
	}
}
