package com.pingan.tags.amap.regeo;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressComponent {
	private String country;
	private String province;
	private String city;
	private String citycode;
	private String district;
	private String adcode;
	private String township;
	private String towncode;
	
	private Neighborhood neighborhood;
	private Building building;
	private StreetNumber streetNumber;
	
	private List<BusinessArea> businessAreas;
}