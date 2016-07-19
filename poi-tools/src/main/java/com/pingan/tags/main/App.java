package com.pingan.tags.main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.pingan.tags.amap.regeo.AddressComponent;
import com.pingan.tags.amap.regeo.Regeo;
import com.pingan.tags.amap.regeo.RegeoCode;
import com.pingan.tags.config.RootConfig;
import com.pingan.tags.domain.GatherPoint;

import org.apache.commons.lang3.tuple.ImmutablePair;

public class App {
	final static String filePath = "D:/datahub/pa_list_loc@20160715.txt";
	final static String prefix = "d:/datahub/coord-geo-%s.txt";
	
	public static void main(String[] args) throws IOException {
		try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(RootConfig.class)) {
			GeoCrawler crawl = ctx.getBean(GeoCrawler.class);
			crawl.doCrawl(filePath, prefix);
			
//			Stream<String> line = Files.lines(Paths.get("D:/datahub/光大位置数据.dat"));
//			line.map(l -> crawl.parseAsGatherPoint(l))
//				.flatMap(x -> x.stream())
//				.filter(g -> g.getCoordinate().isValid() == true)
//				.map(x -> ImmutablePair.of(x, x.getCoordinate()))
//				.map(x -> ImmutablePair.of(x.left, crawl.getRepo().getGCJ02Coord(x.right)))
//				.map(x -> ImmutablePair.of(x.left, crawl.getRepo().getGEO(x.right)))
//				.filter(p -> p.right != null)
//				.map(x -> {	GatherPoint gp = x.left;
//							Regeo rg = x.right;
//							RegeoCode rc = rg.getRegeocode();
//							AddressComponent ac = rc.getAddressComponent();
//							String addr = String.join("\t", gp.getTdid(),
//															String.valueOf(gp.isWeekend()),
//															String.valueOf(gp.getHour()),
//															String.valueOf(gp.getCoordinate().getLng()),
//															String.valueOf(gp.getCoordinate().getLat()),
//															ac.getCountry(),
//															ac.getProvince() == null ? "" : ac.getProvince(),
//															ac.getCity() == null ? "" : ac.getCity(),
//															ac.getDistrict() == null ? "" : ac.getDistrict(),
//															ac.getTownship() == null ? "" : ac.getTownship(),
//															rc.getFormatted_address() == null ? "" : rc.getFormatted_address());
//							return addr;})
//				
//				.forEach(System.out::println);
		}
	}
	
	
}