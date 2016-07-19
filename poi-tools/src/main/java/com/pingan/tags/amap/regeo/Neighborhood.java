package com.pingan.tags.amap.regeo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Neighborhood implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private String type;
}
