package cn.td.geotags.amap.around;

import lombok.Setter;

import java.io.Serializable;

import lombok.Getter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class BizExt implements Serializable {
	private static final long serialVersionUID = 1L;

	private String rating;
	private String cost;
	private String meal_ordering;
}
