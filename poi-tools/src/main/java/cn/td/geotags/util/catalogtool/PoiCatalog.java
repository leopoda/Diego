package cn.td.geotags.util.catalogtool;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PoiCatalog {
	private String text;
	private boolean selectable;
	private List<PoiTagNode> nodes;
}
