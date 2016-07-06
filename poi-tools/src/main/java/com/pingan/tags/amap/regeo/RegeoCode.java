package com.pingan.tags.amap.regeo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegeoCode {
	private String formatted_address;
	private AddressComponent addressComponent;
}
