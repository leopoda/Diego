package com.pingan.tags.amap.regeo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BusinessArea implements Serializable {
	private static final long serialVersionUID = 1L;

	private String id;
	private String name;
	private String location;
}
