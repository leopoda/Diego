package com.pingan.tags.main;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pingan.tags.amap.regeo.Regeo;
import com.pingan.tags.amap.regeo.RegeoCode;
import com.pingan.tags.amap.regeo.AddressComponent;
import com.pingan.tags.dao.Repository;
import com.pingan.tags.domain.Coordinate;
import com.pingan.tags.domain.GatherPoint;

import org.apache.commons.lang3.tuple.ImmutablePair;
import lombok.extern.slf4j.Slf4j;
import lombok.Getter;

@Slf4j
@Getter
@Component
public class GeoCrawler {
	@Autowired
	Repository repo;

	
//	public static void main(String[] args) throws IOException {
//		ApplicationContext ctx = new AnnotationConfigApplicationContext(RootConfig.class);
//		GeoCrawler app = ctx.getBean(GeoCrawler.class);
//		app.doCrawl(filePath, prefix);
//	}

	public void doCrawl(String inputFilePath, String outputFilePrefix) {
		try (Stream<String> lines = Files.lines(Paths.get(inputFilePath))) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); 
			PrintStream strm = new PrintStream(String.format(outputFilePrefix, sdf.format(System.currentTimeMillis())));

			lines.parallel()
				 .map(GeoCrawler::parseAsCoordinate)
//			 	 .limit(10)
			 	 .filter(c -> c.isValid() == true)
			 	 .map(c -> ImmutablePair.of(c, Optional.ofNullable(repo.getGCJ02Coord(c))))
			 	 .filter(p -> p.right.isPresent() == true)
			 	 .map(p -> ImmutablePair.of(p.left, Optional.ofNullable(repo.getGEO(p.right.isPresent() ? p.right.get() : null))))
			 	 .map(t -> parseAsAddress(t.left, t.right))
//			 	 .forEach(System.out::println);
			 	 .forEach(x -> strm.println(x));

			strm.close();
		} catch (Exception e) {
			log.error("", e);
		}
	}

	public String parseAsAddress(Coordinate c, Optional<Regeo> r) {
		String lng = String.valueOf(c.getLng());
		String lat = String.valueOf(c.getLat());
		
		String country = "";
		String province = "";
		String city = "";
		String district = "";
		String township = "";
		String formattedAddress = "";
		
		if (r.isPresent() && r.get().getInfo().equals("OK")) {
			RegeoCode rc = r.get().getRegeocode();
			AddressComponent ac = rc.getAddressComponent();

			formattedAddress = rc.getFormatted_address();
			country = ac.getCountry();
			province = ac.getProvince();
			city = ac.getCity();
			district = ac.getDistrict();
			township = ac.getTownship();
		}

		return String.join(	"\t", 
							lng, 
							lat, 
							country == null ? "" : country, 
							province == null ? "" : province, 
							city == null ? "" : city, 
							district == null ? "" : district, 
							township == null ? "" : township, 
							formattedAddress == null ? "" : formattedAddress);
	}

	public static Coordinate parseAsCoordinate(String line) {
		String[] arr = line.split("\t");
		double lng = Double.parseDouble(arr[0]);
		double lat = Double.parseDouble(arr[1]);

		return new Coordinate(lng, lat);
	}
	
	public List<GatherPoint> parseAsGatherPoint(String line) {
		String tdid = "";
		String pos = "";
		String[] arr = line.split("\t");

		if (arr.length >= 2) {
			tdid = arr[0];
			pos = arr[1];
		}

		int idx = pos.indexOf("|");
		List<GatherPoint> list = new ArrayList<>();

		if (idx >= 0) {
			String first = pos.substring(0, idx);
			String second = pos.substring(idx + 1, pos.length());
			
			if (first != null && first.length() > 0) {
				for (String hc : first.split(";")) {
					int idx2 = hc.indexOf(":");
					String h = hc.substring(0, idx2);
					String c = hc.substring(idx2 + 1, hc.length());
					for (String cos : c.split(",")) {
						String[] co = cos.split("_");
						String lat = co[0];
						String lng = co[1];
						int count = Integer.parseInt(co[2]);
						GatherPoint gp = new GatherPoint(tdid, 
														 false, 
														 "", 
														 Integer.parseInt(h), 
														 Double.parseDouble(lng), 
														 Double.parseDouble(lat), 
														 count);
						list.add(gp);
					}
				}
			}

			if (second != null && second.length() > 0) {
				for (String hc : second.split(";")) {
					int idx2 = hc.indexOf(":");
					String h = hc.substring(0, idx2);
					String c = hc.substring(idx2 + 1, hc.length());
					for (String cos : c.split(",")) {
						String[] co = cos.split("_");
						String lat = co[0];
						String lng = co[1];
						int count = Integer.parseInt(co[2]);
						GatherPoint gp = new GatherPoint(tdid, 
														 true, 
														 "", 
														 Integer.parseInt(h), 
														 Double.parseDouble(lng), 
														 Double.parseDouble(lat), 
														 count);
						list.add(gp);
					}
				}
			}
		}
		return list;
	}
}
