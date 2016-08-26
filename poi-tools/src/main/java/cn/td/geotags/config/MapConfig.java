package cn.td.geotags.config;

import lombok.Setter;

import java.util.Random;

import lombok.Getter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class MapConfig {
	private final static Random rand = new Random();

	private String token;
	private String convertURL;
	private String regeoURL;
	private String aroundURL;

	public String getToken() {
		String[] tokens = token.split(",");
		int r = rand.nextInt(tokens.length);
		String nextToken = tokens[r];
		return nextToken;
	}
}
