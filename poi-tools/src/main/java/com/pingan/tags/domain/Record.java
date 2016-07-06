package com.pingan.tags.domain;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Record {
	private String tdid;
	private Map<Integer, List<Coordinate>> workday;
	private Map<Integer, List<Coordinate>> weekend;

	private static ObjectMapper MAPPER = new ObjectMapper();	
	
	@Override
	public String toString() {
		try {
			return new String(MAPPER.writeValueAsBytes(this));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public static boolean isValid(Record r) {
		if (r.workday != null) {
			for (Integer i : r.workday.keySet()) {
				List<Coordinate> list = r.workday.get(i);
				for (Coordinate c : list) {
					if (c.getLat() > c.getLng())
						return false;
				}
			}
		}

		if (r.weekend != null) {
			for (Integer i : r.weekend.keySet()) {
				List<Coordinate> list = r.weekend.get(i);
				for (Coordinate c : list) {
					if (c.getLat() > c.getLng())
						return false;
				}
			}
		}
		
		return true;
	}
}
