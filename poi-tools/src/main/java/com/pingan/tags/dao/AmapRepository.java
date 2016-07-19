package com.pingan.tags.dao;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pingan.tags.amap.regeo.CoordinateResponse;
import com.pingan.tags.amap.regeo.Regeo;
import com.pingan.tags.config.MapConfig;
import com.pingan.tags.domain.Coordinate;
import com.pingan.tags.util.URLUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AmapRepository implements Repository {
	@Autowired
	MapConfig mapConfig;

	private final static RestTemplate REST = new RestTemplate();
	private final static ObjectMapper MAPPER = new ObjectMapper();

	@Cacheable(value="coordCache", key="'wjs84-' + #p0.lng + ',' + #p0.lat", unless="#result == null")
	public Coordinate getGCJ02Coord(Coordinate c) {
		final String params = String.format("key=%s&locations=%.6f,%.6f&coordsys=gps", mapConfig.getToken(), c.getLng(), c.getLat());
		final String url = String.format("%s%s", mapConfig.getConvertURL(), params);

		try {
			ResponseEntity<CoordinateResponse> e = REST.getForEntity(url, CoordinateResponse.class);
			return e.getBody().parseAsCoordinate();
		} catch (Exception e) {
			log.error("failed to convert for GCJ02: " + c.getLng() + "," + c.getLat(), e);
			return null;
		}
	}

	@Cacheable(value="regeoCache", key="'gcj02-' + #p0.lng + ',' + #p0.lat", unless="#result == null")
	public Regeo getGEO(Coordinate c) {
		if (c != null) {
			final String params = String.format("key=%s&location=%s,%s", mapConfig.getToken(), c.getLng(), c.getLat());
			final String url = String.format("%s%s", mapConfig.getRegeoURL(), params);
			
			try {
				String content = URLUtil.doGet(url);
				String newConent = content.replace("[]", "null");
				return MAPPER.readValue(newConent, Regeo.class);
			} catch (Exception e) {
				log.error("failed to get geo location", e);
			}
		}
		return null;
	}
}
