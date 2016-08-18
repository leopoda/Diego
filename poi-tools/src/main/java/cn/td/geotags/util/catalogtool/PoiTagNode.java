package cn.td.geotags.util.catalogtool;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PoiTagNode {
	private String text;
	private boolean selectable;
	private List<PoiTagLeaf> nodes;
}
