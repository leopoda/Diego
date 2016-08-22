package cn.td.geotags.util;

import cn.td.geotags.domain.Coordinate;

public class CoordinateParser {
	public static Coordinate parse(String line) {
		String[] arr = line.split("\t");
		double lng = Double.parseDouble(arr[0]);
		double lat = Double.parseDouble(arr[1]);

		return new Coordinate(lng, lat);
	}
}
