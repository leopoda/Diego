package com.pingan.tags.amap.around;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Suggestion implements Serializable {
	private static final long serialVersionUID = 1L;

	private String keywords;
	private String cities;
}
