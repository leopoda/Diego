package cn.td.geotags.amap.around;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class IndoorData implements Serializable {
	private static final long serialVersionUID = 1L;

	private String cpid;
	private String floor;
	private String truefloor;
	private String cmsid;
}
