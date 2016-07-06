package com.pingan.tags.main;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
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
	
	final static String filePath = "C:/Users/pc/Desktop/tendcloud/FE/平安财险标签/pingan_res_2016-02.2016-02";
	final static String dest = "d:/test.txt";
	
	public static void main(String[] args) throws IOException {
		ApplicationContext ctx = new AnnotationConfigApplicationContext(PinganConfig.class);
		PingAnInsurance app = ctx.getBean(PingAnInsurance.class);
		
		PrintStream strm = new PrintStream(dest);
		Stream<String> lines = Files.lines(Paths.get(filePath));
		
		lines.map(PingAnInsurance::parseAsGatherPoint)
			 .limit(50)
			 //.parallel()
			 .map(GatherPoint::build4AmapQuery)
			 .map(x -> new TwoTuple<GatherPoint, Optional<Regeo>>(x, x.getCoordinate().regeo()))
			 .map(PingAnInsurance::parseAsAddress)
			 //.forEach(x -> app.saveAddress(x));
			 .forEach(System.out::println);
	}

	@Transactional
	private void saveAddress(Address a) {
		repository.save(a);
	}
	
	private static Address parseAsAddress(TwoTuple<GatherPoint, Optional<Regeo>> x) {
		GatherPoint g = x.first;
		Optional<Regeo> r = x.second;

		Address a = new Address();
		a.setTdid(g.getTdid());
		a.setLng(g.getCoordinate().getLng());
		a.setLat(g.getCoordinate().getLat());
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

	private static GatherPoint parseAsGatherPoint(String line) {
		String[] arr = line.split("\t");
		String tdid = arr[0];
		boolean isWeekend = arr[1].equals("119") ? false : true;
		String month = "2016-02";
		int hour = Integer.parseInt(arr[2]);
		double lat = Double.parseDouble(arr[3]);
		double lng = Double.parseDouble(arr[4]);
		int count = Integer.parseInt(arr[5]);
		return new GatherPoint(tdid, isWeekend, month, hour, lng, lat, count);
	}
}
