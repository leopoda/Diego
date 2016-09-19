package cn.td.geotags.dao;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDate;

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
import cn.td.geotags.util.Contants;
import cn.td.geotags.util.CustomMonitorClient;
import cn.td.geotags.util.URLUtil;
import lombok.extern.slf4j.Slf4j;
//import com.talkingdata.monitor.client.Client;
import cc.d_z.jstats.Counter;

@Slf4j
public class AmapRepository implements MapRepository {
	@Autowired
	private MapConfig mapConfig;
	
	private final static String AMAP_API_REGEO = "regeo";
	private final static String AMAP_API_CONV = "conv";
	private final static String AMAP_API_AROUND = "around";
 
	private final static RestTemplate REST = new RestTemplate();
	private final static ObjectMapper MAPPER = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
			.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true)
			.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
			.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);

	@Deprecated
	@Cacheable(value="coordCache", key="'wjs84-' + #p0.lng + ',' + #p0.lat", unless="#result == null")
	public Coordinate getGCJ02Coord(Coordinate c) {
		return convertCoord("gps", c);
	}
	
	@Cacheable(value="coordCache", key="'coordsys-' + #p0 + '-' + #p1.lng + ',' + #p1.lat", unless="#result == null")
	public Coordinate getGCJ02Coord(String coordsys, Coordinate c) {
		return convertCoord(coordsys, c);
	}

	public Coordinate convertCoord(String coordsys, Coordinate c) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(mapConfig.getConvertURL());
		builder.queryParam("key", mapConfig.getToken());
		builder.queryParam("locations", String.format("%.6f,%.6f", c.getLng(), c.getLat()));
		builder.queryParam("coordsys", coordsys);
		
		String url = builder.build().toUriString();
		try {
			ResponseEntity<CoordinateResponse> e = REST.getForEntity(url, CoordinateResponse.class);
			
			/*
			 * 监控  经纬度转换 的调用次数 
			 */
			CustomMonitorClient.count(currentDayCounterName(AMAP_API_CONV));
			resetCounter(counterName(AMAP_API_CONV), CustomMonitorClient.getCounter(currentDayCounterName(AMAP_API_CONV)).get());

			return e.getBody().parseAsCoordinate();
		} catch (Exception e) {
			log.error("failed to convert to GCJ02: " + c.getLng() + "," + c.getLat(), e);
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
				String newConent = content.replace("[]", "null");
				
				/*
				 * 监控  API 的调用次数, 当前额度为 400万 / (天, token) 
				 */
				CustomMonitorClient.count(currentDayCounterName(AMAP_API_REGEO));
				resetCounter(counterName(AMAP_API_REGEO), CustomMonitorClient.getCounter(currentDayCounterName(AMAP_API_REGEO)).get());
				return MAPPER.readValue(newConent, Regeo.class);
			} catch (IOException e) {
				log.error("failed to get geo location", e);
			}
		}
		return null;
	}

	/*
	 * Compose the counter name for calculating invoking times
	 */
	private String currentDayCounterName(String apiType) {
		LocalDate today = LocalDate.now();
		String date = String.format("%d%02d%02d", today.getYear(), today.getMonthValue(), today.getDayOfMonth());
		String postfix = String.format("%s_%s", apiType, date);
		return Contants.MONITOR_CALL_AMAP_PREFIX + postfix;
	}
	
	private String counterName(String apiType) {
		return Contants.MONITOR_CALL_AMAP_PREFIX + apiType;
	}
	
	/*
	 * Client 这个类只提供累加 counter 的方法, 此方法可以清除掉以前的累加值, 并赋予一个新值。
	 */
	private void resetCounter(String name, long newValue) {
		Counter counter = CustomMonitorClient.getCounter(name);
		if (counter != null) {
			long lastValue = counter.get();
			CustomMonitorClient.count(name, newValue - lastValue);
		} else {
			CustomMonitorClient.count(name, newValue);
		}
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
		
		try {
			String content = URLUtil.doGet(url);
			String newConent = content.replace("[]", "null");
//			log.debug(content);
			Around around = MAPPER.readValue(newConent, Around.class);
//			System.out.println(around);
			
			/*
			 * 监控  API 的调用次数, 当前额度为 40万 / (天, token) 
			 */
			CustomMonitorClient.count(currentDayCounterName(AMAP_API_AROUND));
			resetCounter(counterName(AMAP_API_AROUND), CustomMonitorClient.getCounter(currentDayCounterName(AMAP_API_AROUND)).get());
			
			return around;
		} catch (IOException e) {
			log.error("Unable to get poi from amap", e);
			return null;
		}
	}
}
