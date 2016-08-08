package cn.td.geotags.amap.regeo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Regeo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String status;
	private String info;
	private String infocode;
	private RegeoCode regeocode;
}
