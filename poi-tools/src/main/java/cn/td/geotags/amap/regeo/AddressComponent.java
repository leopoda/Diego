package cn.td.geotags.amap.regeo;

import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AddressComponent implements Serializable {
	private static final long serialVersionUID = 1L;

	private String country;
	private String province;
	private String city;
	private String citycode;
	private String district;
	private String adcode;
	private String township;
	private String towncode;
	
	private String seaArea;
	
	private Neighborhood neighborhood;
	private Building building;
	private StreetNumber streetNumber;
	
	private List<BusinessArea> businessAreas;
}