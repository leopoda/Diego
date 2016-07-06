package com.pingan.tags.amap.around;

import java.util.List;

public class SearchAround {
	private String status;
	private String count;
	private String info;
	private String infocode;
	
	private Suggestion suggestion;
	private List<Poi> pois;
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getInfocode() {
		return infocode;
	}
	public void setInfocode(String infocode) {
		this.infocode = infocode;
	}
	public Suggestion getSuggestion() {
		return suggestion;
	}
	public void setSuggestion(Suggestion suggestion) {
		this.suggestion = suggestion;
	}
	public List<Poi> getPois() {
		return pois;
	}
	public void setPois(List<Poi> pois) {
		this.pois = pois;
	}


}
