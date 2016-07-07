package com.pingan.tags.main;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.pingan.tags.amap.regeo.AddressComponent;
import com.pingan.tags.amap.regeo.Regeo;
import com.pingan.tags.config.PinganConfig;
import com.pingan.tags.db.Repository;
import com.pingan.tags.domain.Address;
import com.pingan.tags.domain.GatherPoint;
import com.pingan.tags.util.TwoTuple;

@Component
public class PingAnInsurance {
	
	@Autowired
	Repository repository;
	
	static LocalDateTime t = LocalDateTime.now();
	
	
	final static String filePath = "D:/datahub/平安-好车主-聚集点数据/hcz_2016-05.txt";
	final static String prefix = "d:/hcz-%s%s%s%s%s%s.txt";
	
	public static void main(String[] args) throws IOException {
		ApplicationContext ctx = new AnnotationConfigApplicationContext(PinganConfig.class);
		PingAnInsurance app = ctx.getBean(PingAnInsurance.class);
		
		PrintStream strm = new PrintStream(String.format(prefix, t.getYear(), t.getMonth(), t.getDayOfMonth(), t.getHour(), t.getMinute(), t.getSecond()));
		Stream<String> lines = Files.lines(Paths.get(filePath));
		
		lines.map(PingAnInsurance::parseAsGatherPoint)
			 //.limit(10)
			 //.parallel()
			 .map(GatherPoint::build4AmapQuery)
			 .map(x -> new TwoTuple<GatherPoint, Optional<Regeo>>(x, x.getCoordinate().regeo()))
			 .map(PingAnInsurance::parseAsAddress)
			 //.forEach(x -> app.saveAddress(x));
			 //.forEach(System.out::println);
			 .forEach(x -> strm.println(x));
	}

	@Transactional
	private void saveAddress(Address a) {
		repository.save(a);
	}
	
	private static Address parseAsAddress(TwoTuple<GatherPoint, Optional<Regeo>> x) {
		GatherPoint g = x.first;
		Optional<Regeo> r = x.second;

		Address a = new Address();
		a.setOffset(g.getOffset());
		a.setLng(g.getCoordinate().getLng());
		a.setLat(g.getCoordinate().getLat());
		a.setSource(g.getSource());
		a.setWeekend(g.isWeekend());
		a.setCount(g.getCount());
		
		a.setMonth(g.getMonth());
		a.setHour(g.getHour());

		r.ifPresent(p -> {
			AddressComponent c = p.getRegeocode().getAddressComponent();
			a.setCountry(c.getCountry());
			a.setProvince(c.getProvince());
			a.setCity(c.getCity());
			a.setDistrict(c.getDistrict());
			a.setTownship(c.getTownship());
			a.setAddress(p.getRegeocode().getFormatted_address());
			
		});
		return a;
	}

	/*
	 * input format: (1747139069,101,22,(22.995071411132812,113.89457702636719),GPS,0.0,1)
	 * the fields definition: offset, dateType, hour, lat, lng, source, accuracy, count
	 */
	private static GatherPoint parseAsGatherPoint(String line) {
		String[] arr = line.replace("(", "").replace(")", "").split(",");
		long offset = Long.parseLong(arr[0]);
		boolean isWeekend = arr[1].equals("101") ? true : false;
		String month = "201605";
		int hour = Integer.parseInt(arr[2]);
		double lat = Double.parseDouble(arr[3]);
		double lng = Double.parseDouble(arr[4]);
		String source = arr[5];
		int count = Integer.parseInt(arr[7]);
		return new GatherPoint(offset, isWeekend, month, hour, lng, lat, source, count);
	}
}
