package cn.td.geotags.dao;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.td.geotags.amap.around.Around;
import cn.td.geotags.amap.regeo.CoordinateResponse;
import cn.td.geotags.amap.regeo.Regeo;
import cn.td.geotags.config.MapConfig;
import cn.td.geotags.domain.Coordinate;
import cn.td.geotags.util.URLUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AmapRepository implements MapRepository {
	@Autowired
	MapConfig mapConfig;
 
	private final static RestTemplate REST = new RestTemplate();
	private final static ObjectMapper MAPPER = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
			.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true)
			.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
			.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false)
			;

	@Cacheable(value="coordCache", key="'wjs84-' + #p0.lng + ',' + #p0.lat", unless="#result == null")
	public Coordinate getGCJ02Coord(Coordinate c) {
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(mapConfig.getConvertURL());
		builder.queryParam("key", mapConfig.getToken());
		builder.queryParam("locations", String.format("%.6f,%.6f", c.getLng(), c.getLat()));
		builder.queryParam("coordsys", "gps");
		
		String url = builder.build().toUriString();
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
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(mapConfig.getRegeoURL());
			builder.queryParam("key", mapConfig.getToken());
			builder.queryParam("location", String.format("%s,%s", c.getLng(), c.getLat()));
			
			String url = builder.build().toUriString();
			try {
				String content = URLUtil.doGet(url);
				String newConent = content.replace("[]", "null");//.replace("[null]", "null");
				return MAPPER.readValue(newConent, Regeo.class);
			} catch (IOException e) {
				log.error("failed to get geo location", e);
			}
		}
		return null;
	}

	@Override
	@Cacheable(value="poiAroundCache", key="'poi-around-gcj02-' + #p0.lng + ',' + #p0.lat + ';' + #p1 + ';' + #p2 + ';' + #p3 + ';' + #p4", unless="#result == null")
	public Around getPoiAround(Coordinate coord, String poiTypes, long radius, int pageSize, int pageNum) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(mapConfig.getAroundURL());
		builder.queryParam("key", mapConfig.getToken());
		builder.queryParam("location", String.format("%s,%s", coord.getLng(), coord.getLat()));
		builder.queryParam("types", poiTypes);
		builder.queryParam("radius", radius);
		builder.queryParam("offset", pageSize);
		builder.queryParam("page", pageNum);
		
		String url = builder.build().toUriString();
//		System.out.println(url);
		
		try {
			String content = URLUtil.doGet(url);
			String newConent = content.replace("[]", "null");
//			log.debug(content);
			Around around = MAPPER.readValue(newConent, Around.class);
//			System.out.println(around);
			return around;
		} catch (IOException e) {
			log.error("Unable to get poi from amap", e);
//			throw new RuntimeException(e);
			return null;
		}
	}
}
