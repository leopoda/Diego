package com.pingan.tags.amap.around;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Around implements Serializable {
	private static final long serialVersionUID = 1L;

	private String status;
	private String count;
	private String info;
	private String infocode;
	
//	private Suggestion suggestion;
	private List<Poi> pois;
}
