package cn.td.geotags.amap.regeo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class StreetNumber implements Serializable {
	private static final long serialVersionUID = 1L;

	private String street;
	private String number;
	private String location;
	private String direction;
	private String distance;
}
