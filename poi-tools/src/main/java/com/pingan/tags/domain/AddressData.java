package com.pingan.tags.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressData {
	private String country;
	private String province;
	private String city;
	private String district;
	private String town;
	private String address;
	
//	private String bizArea;

	public AddressData() {
	}
	
	public AddressData(String country, String province, String city, String district, String township, String address/*, String bizArea*/) {
		this.country = country;
		this.province = province;
		this.city = city;
		this.district = district;
		this.town = township;
		this.address = address;
//		this.bizArea = bizArea;
	}
}