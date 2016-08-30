package cn.td.geotags.util;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaiduCoordinateUtil {
	private final static RestTemplate rest = new RestTemplate();
	private final static ObjectMapper om = new ObjectMapper();

	public static void main(String[] args) {
		String coord = geoconv("113.15849,27.85652");
		System.out.println(coord);
	}

	public static String geoconv(String location) {
		UriComponentsBuilder uri = UriComponentsBuilder.fromHttpUrl("http://api.map.baidu.com/geoconv/v1/");
		uri.queryParam("coords", location);
		uri.queryParam("ak", "gyccRf53EAOCOQ2bCMnEdKS0BNnZ7wP1");
		uri.queryParam("from", 3);
		uri.queryParam("to", 5);
		uri.queryParam("output", "json");
		
		String url = uri.build().toString();
		ResponseEntity<String> resp = rest.getForEntity(url, String.class);
		if (resp.getStatusCode() != HttpStatus.OK) {
			return null;
		}
		String tmp = resp.getBody();
		if (tmp == null) {
			return null;
		}
		BaiduCoordinateResult br = null;
		try {
			br = om.readValue(tmp.getBytes(), BaiduCoordinateResult.class);
		} catch (IOException e) {
			log.error("convert to baidu coordinate failed");
		}
		if (br == null || br.getStatus() != 0) {
			return null;
		}
		BaiduCoordinate result = br.getResult().get(0);
		return String.join(",", String.valueOf(result.getX()), String.valueOf(result.getY()));
	}

	@Getter
	@Setter
	private static class BaiduCoordinateResult {
		private int status;
		private List<BaiduCoordinate> result;
	}
	
	@Getter
	@Setter
	private static class BaiduCoordinate {
		private double x;
		private double y;
	}
}
