package com.pingan.tags.bmap;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Geocoder {
	private int status;
	private CoordLocationResult result;
}
