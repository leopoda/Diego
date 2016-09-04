package cn.td.geotags.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;

import cn.td.geotags.domain.Coordinate;
import cn.td.geotags.domain.GatherPoint;
import cn.td.geotags.domain.PoiInfo;

public class ParserUtil {
	public static Coordinate parseAsCoordinate(String line) {
		String[] arr = line.split("\t");
		double lng = Double.parseDouble(arr[0]);
		double lat = Double.parseDouble(arr[1]);

		return new Coordinate(lng, lat);
	}
	
	public static List<GatherPoint> parseAsGatherPoint(String line) {
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
		}
		return list;
	}
	
	public static PoiInfo parsePoiInfo(String line) {
		String[] fields = line.split("\t");
		int distance = Integer.parseInt(fields[4]);
		String province = fields[5];
		String city = fields[6];
		String district = fields[7];
		String address = fields[8];
		String location = fields[9];
		String type = fields[10];
		String typecode = fields[11];
		String id = fields[12];
		String name = fields[13];
		
		PoiInfo poiInfo = new PoiInfo(id, type, typecode, name, address, distance, province, city, district, location);
		return poiInfo;
	}
	
	public static ImmutablePair<GatherPoint, PoiInfo> parseAsGatherPointPoiInfoPair(String line) {
		String[] fields = line.split("\t");
		
		String mac = fields[0];
		boolean isWorkday = fields[1].equals("Y") ? true : false;
		String month = "";
		int hour = Integer.parseInt(fields[2]);
		int count = Integer.parseInt(fields[3]);
		double lng = Double.parseDouble(fields[4]);
		double lat = Double.parseDouble(fields[5]);
		
		GatherPoint gp = new GatherPoint(mac, isWorkday, month, hour, lng, lat, count);
		
		String id = fields[16];
		String type = fields[14];
		String typecode = fields[15];
		String name = fields[17];
		String address = fields[12];
		int distance = Integer.parseInt(fields[8]);

		String province = fields[9];
		String city = fields[10];
		String district = fields[11];
		String location = fields[13];
		
		Coordinate c1 = new Coordinate(Double.parseDouble(fields[4]), Double.parseDouble(fields[5]));
		Coordinate c2 = new Coordinate(Double.parseDouble(fields[6]), Double.parseDouble(fields[7]));
		
		PoiInfo p = new PoiInfo(id, type, typecode, name, address, distance, province, city, district, location);
		p.setCenter(c1);
		p.setAmapCenter(c2);

		return ImmutablePair.of(gp, p);
	}
}
