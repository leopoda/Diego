package com.pingan.tags.bmap;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AddressComponent {
	private String country;
	private String province;
	private String city;
	private String district;
	private String street;
	private String street_number;
	private String adcode;
	private int country_code;
	private String direction;
	private String distance;
}
