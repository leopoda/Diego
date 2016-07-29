package com.pingan.tags.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class PoiType {
	private String id;
	private String type;

	private String name;
	private String parentId;
}
