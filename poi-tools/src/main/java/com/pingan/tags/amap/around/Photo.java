package com.pingan.tags.amap.around;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Photo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String title;
	private String url;
}
