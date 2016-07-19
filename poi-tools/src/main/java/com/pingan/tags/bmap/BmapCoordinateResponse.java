package com.pingan.tags.bmap;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BmapCoordinateResponse {
	private int status;
	private List<Coord> result;
}
