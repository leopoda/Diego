package com.pingan.tags.bmap;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoordLocationResult {
	private Location location;
	private String formatted_address;
	private String business;
	private AddressComponent addressComponent;
	private List<Poi> pois;
	private String sematic_description;
	private List<PoiRegion> poiRegions;
	private int cityCode;
}