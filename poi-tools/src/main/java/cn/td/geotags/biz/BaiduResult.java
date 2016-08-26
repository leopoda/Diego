package cn.td.geotags.biz;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BaiduResult {
	private int status;
	private List<BaiduCoordinate> result;
}