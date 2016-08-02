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
public class Poi implements Serializable {
	private static final long serialVersionUID = 1L;

	private String id;
	private String name;
	private String tag;
	private String type;
	private String typecode;
	private String biz_type;
	private String address;
	private String location;
	private String distance;
	private String tel;
	private String postcode;
	private String pcode;
	private String pname;
	private String citycode;
	private String cityname;
	private String adcode;
	private String adname;
	private String entr_location;
	private String exit_location;
	private String navi_poiid;
	private String gridcode;
	private String alias;
	private String business_area;
	private String parking_type;
	private String indoor_map;
//	private IndoorData indoor_data;

	private String website;
	private String email;
	private String shopid;
	private String match;
	private String recommend;
	private String timestamp;
	private String groupbuy_num;
	private String discount_num;
//	private BizExt biz_ext;
	private String event;
	private String children;
//	private List<Photo> photos;
}
