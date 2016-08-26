package cn.td.geotags.biz;

import java.io.IOException;
import java.util.stream.IntStream;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.td.geotags.domain.PoiInfo;
import junit.framework.Assert;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BaiduPoi {
	private final static RestTemplate rest = new RestTemplate();
	private final static ObjectMapper om = new ObjectMapper();

	private double lng;
	private double lat;
	
	private int rank;
	private int distance;

	String name;
	String address;
	
	public String geoconv(String location) {
		UriComponentsBuilder uri = UriComponentsBuilder.fromHttpUrl("http://api.map.baidu.com/geoconv/v1/");
		uri.queryParam("coords", location);
		uri.queryParam("ak", "gyccRf53EAOCOQ2bCMnEdKS0BNnZ7wP1");
		uri.queryParam("from", 3);
		uri.queryParam("to", 5);
		uri.queryParam("output", "json");
		
		String url = uri.build().toString();
//		String tmp = rest.getForObject(url, String.class);
//		String tmp = rest.getForEntity(url, String.class);
		ResponseEntity<String> resp = rest.getForEntity(url, String.class);
		if (resp.getStatusCode() != HttpStatus.OK) {
			return null;
		}
		String tmp = resp.getBody();
//		System.out.println(tmp);
		if (tmp == null) {
			return null;
		}
		BaiduResult br = null;
		try {
			br = om.readValue(tmp.getBytes(), BaiduResult.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
//		if (br.getStatus())
//		System.out.println(br.getStatus());
//		System.out.println(url);
		if (br.getStatus() != 0) {
			return null;
		}
		BaiduCoordinate result = br.getResult().get(0);
		return String.join(",", String.valueOf(result.getX()), String.valueOf(result.getY()));
	}

	public void setLngLat(String lnglat) {
		if (lnglat == null) {
			this.lng = 0;
			this.lat = 0;
		}
		String dest = geoconv(lnglat);
		if (dest != null) {
			String[] arr = dest.split(",");
			this.lng = Double.parseDouble(arr[0]);
			this.lat = Double.parseDouble(arr[1]);
		}
	}
	
	public static BaiduPoi parse(ImmutableTriple<Integer, Integer, PoiInfo> p) {
		return IntStream.rangeClosed(1, 1).boxed().map(o -> {
			BaiduPoi poi = new BaiduPoi();
			poi.setRank(p.getLeft());
			poi.setDistance(p.getMiddle());
			poi.setLngLat(p.getRight().getLocation());
			poi.setName(p.getRight().getName());
			poi.setAddress(p.getRight().getAddress());
			return poi;
		}).findFirst().get();
	}
	
	public static BaiduPoi mock(PoiInfo poiInfo) {
		BaiduPoi poi = new BaiduPoi();	
		poi.setRank(100);
		poi.setDistance(poiInfo.getDistance());
//		Assert.assertNotNull(poiInfo.getLocation());
		poi.setLngLat(poiInfo.getLocation());
		poi.setName(poiInfo.getName());
		poi.setAddress(poiInfo.getAddress());
		return poi;
	}
}
