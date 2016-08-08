package cn.td.geotags.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
public class GatherPoint {
	private String tdid;
	
	private String month;
//	private String day;
	private int hour;
	
	private Coordinate coordinate;
//	private String source;

	private boolean isWeekend;
	private int count;
	
	private static ObjectMapper MAPPER = new ObjectMapper();

	public GatherPoint(String tdid, boolean isWeekend, String month, int hour, double lng, double lat, /*String source,*/ int count) {
		this.tdid = tdid;
		this.isWeekend = isWeekend;
		this.month = month;
		this.hour = hour;
		this.coordinate = new Coordinate();
		this.coordinate.setLat(lat);
		this.coordinate.setLng(lng);
//		this.source = source;
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
	
	public static List<GatherPoint> parseAsGatherPoints(String line) {
		String tdid = "";
		String pos = "";
		String[] arr = line.split("\t");

		if (arr.length >= 2) {
			tdid = arr[0];
			pos = arr[1];
		}

		int idx = pos.indexOf("|");
		List<GatherPoint> list = new ArrayList<>();

		if (idx >= 0) {
			String first = pos.substring(0, idx);
			String second = pos.substring(idx + 1, pos.length());
			
			if (first != null && first.length() > 0) {
				for (String hc : first.split(";")) {
					int idx2 = hc.indexOf(":");
					String h = hc.substring(0, idx2);
					String c = hc.substring(idx2 + 1, hc.length());
					for (String cos : c.split(",")) {
						String[] co = cos.split("_");
						String lat = co[0];
						String lng = co[1];
						int count = Integer.parseInt(co[2]);
						GatherPoint gp = new GatherPoint(tdid, 
														 false, 
														 "", 
														 Integer.parseInt(h), 
														 Double.parseDouble(String.format("%.5f", Double.parseDouble(lng))), 
														 Double.parseDouble(String.format("%.5f", Double.parseDouble(lat))), 
														 count);
						list.add(gp);
					}
				}
			}

			if (second != null && second.length() > 0) {
				for (String hc : second.split(";")) {
					int idx2 = hc.indexOf(":");
					String h = hc.substring(0, idx2);
					String c = hc.substring(idx2 + 1, hc.length());
					for (String cos : c.split(",")) {
						String[] co = cos.split("_");
						String lat = co[0];
						String lng = co[1];
						int count = Integer.parseInt(co[2]);
						GatherPoint gp = new GatherPoint(tdid, 
														 true, 
														 "", 
														 Integer.parseInt(h), 
														 Double.parseDouble(String.format("%.5f", Double.parseDouble(lng))), 
														 Double.parseDouble(String.format("%.5f", Double.parseDouble(lat))), 
														 count);
						list.add(gp);
					}
				}
			}
		}
		return list;
	}
}
