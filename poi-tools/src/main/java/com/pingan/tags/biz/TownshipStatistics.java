package com.pingan.tags.biz;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.pingan.tags.domain.CoordAddress;
import com.pingan.tags.domain.Coordinate;
import com.pingan.tags.service.CoordService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TownshipStatistics {
	@Autowired
	CoordService coordService;
	
//	@Autowired
//	Repository repo;
	
	public void doCalc(String inputFilePath, String outputFilePrefix) {
		try (Stream<String> lines = Files.lines(Paths.get(inputFilePath))) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); 
			PrintStream strm = new PrintStream(String.format(outputFilePrefix, sdf.format(System.currentTimeMillis())));

//			lines.map(line -> parseAsCoordinate(line))
//				 .limit(100)
//				 .filter(c -> c.isValid() == true)
//				 .map(c -> ImmutablePair.of(c, Optional.ofNullable(repo.getGCJ02Coord(c))))
//				 .filter(p -> p.right.isPresent() == true)
//				 .map(p -> ImmutablePair.of(p.left, Optional.ofNullable(repo.getGEO(p.right.orElse(null)))))
//				 .map(t -> parseAsAddress(t.left, t.right))
//				 .forEach(System.out::println);
			
			lines.map(line -> parseAsCoordinate(line))
				 .limit(10)
				 .filter(c -> c.isValid() == true)
				 .map(c -> coordService.getCoordAddress(c))
				 .map(CoordAddress::asFlatText)
				 .forEach(System.out::println);
			
			strm.close();
		} catch (IOException e) {
			log.error("", e);
		}
	}
	
	private  Coordinate parseAsCoordinate(String line) {
		String[] arr = line.split("\t");
		double lng = Double.parseDouble(arr[0]);
		double lat = Double.parseDouble(arr[1]);

		return new Coordinate(lng, lat);
	}
	
//	private String parseAsAddress(Coordinate c, Optional<Regeo> r) {
//		String lng = String.valueOf(c.getLng());
//		String lat = String.valueOf(c.getLat());
//		
//		String country = "";
//		String province = "";
//		String city = "";
//		String district = "";
//		String township = "";
//		String formattedAddress = "";
//		
//		if (r.isPresent() && r.get().getInfo().equals("OK")) {
//			RegeoCode rc = r.get().getRegeocode();
//			AddressComponent ac = rc.getAddressComponent();
//
//			formattedAddress = rc.getFormatted_address();
//			country = ac.getCountry();
//			province = ac.getProvince();
//			city = ac.getCity();
//			district = ac.getDistrict();
//			township = ac.getTownship();
//		}
//
//		return String.join(	"\t", 
//							lng, 
//							lat, 
//							country == null ? "" : country, 
//							province == null ? "" : province, 
//							city == null ? "" : city, 
//							district == null ? "" : district, 
//							township == null ? "" : township, 
//							formattedAddress == null ? "" : formattedAddress);
//	}
}