package cn.td.geotags.util;

import java.util.ArrayList;
import java.util.List;

import cn.td.geotags.domain.GatherPoint;

public class GatherPointParser {
	public static List<GatherPoint> parse(String line) {
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
}
