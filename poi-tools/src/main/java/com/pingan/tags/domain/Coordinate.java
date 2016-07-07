package com.pingan.tags.domain;

import java.io.IOException;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pingan.tags.amap.around.SearchAround;
import com.pingan.tags.amap.regeo.Regeo;
import com.pingan.tags.util.URLUtil;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import lombok.Getter;
import lombok.Setter;

import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class Coordinate {
	private double lng;
	private double lat;

	private static ObjectMapper MAPPER = new ObjectMapper();
	private static RestTemplate REST = new RestTemplate();
	
	public Optional<Coordinate> toAmapCoordinate() {
		final String key = "2b31af5cfa4784730c76c9fe509dbfa8";
		final String prefix = "http://restapi.amap.com/v3/assistant/coordinate/convert?";
		final String params = String.format("key=%s&locations=%.6f,%.6f&coordsys=gps", key, lng, lat);
		
		final String url = String.format("%s%s", prefix, params);
		
//		log.error("error");
		
		try {
			ResponseEntity<AmapCoordinateResponse> e = REST.getForEntity(url, AmapCoordinateResponse.class);
			return Optional.ofNullable(e.getBody().parseAsCoordinate());
		} catch (Exception e) {
			log.error("坐标转换错误: " + lng + "," + lat, e);
			return Optional.empty();
		}

//		try {
//			String content = URLUtil.doGet(url);
//			return Reponse.parse(content).parseAsCoordinate();
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
	}
	
	public SearchAround searchAround() {
		final String key = "2b31af5cfa4784730c76c9fe509dbfa8";
		final String prefix = "http://restapi.amap.com/v3/place/around?";
		final String params = String.format("key=%s&location=%s,%s&keywords=&types=%s&offset=1&page=1&extensions=all&output=json", key, this.lng, this.lat, "190107");
		
		final String url = String.format("%s%s", prefix, params);
		try {
			String content = URLUtil.doGet(url);
			String newConent = content.replace("[]", "null");
			return MAPPER.readValue(newConent, SearchAround.class);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public Optional<Regeo> regeo() {
		final String key = "2b31af5cfa4784730c76c9fe509dbfa8";
		final String prefix = "http://restapi.amap.com/v3/geocode/regeo?";
		final String params = String.format("key=%s&location=%s,%s&poitype=%s&radius=%s&extensions=base&batch=false&roadlevel=1", key, this.lng, this.lat, "190107", 1000);
		//final String params = String.format("key=%s&location=%s,%s&poitype=%s&radius=%s&extensions=base&batch=false&roadlevel=1", key, 0, 0, "190107", 1000);
		
		final String url = String.format("%s%s", prefix, params);
		
		try {
			String content = URLUtil.doGet(url);
			String newConent = content.replace("[]", "null");
			return Optional.of(MAPPER.readValue(newConent, Regeo.class));
		} catch (IOException e) {
			log.error("不能获取逆地理编码", e);
			return Optional.empty();
		}
	}
}
