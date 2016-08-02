package com.pingan.tags.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pingan.tags.amap.regeo.Regeo;
import com.pingan.tags.amap.regeo.RegeoCode;
import com.pingan.tags.config.MapConfig;
import com.pingan.tags.config.PoiConfig;
import com.pingan.tags.amap.around.Around;
import com.pingan.tags.amap.regeo.AddressComponent;
import com.pingan.tags.dao.MapRepository;
import com.pingan.tags.domain.AddressData;
import com.pingan.tags.domain.CoordAddress;
import com.pingan.tags.domain.Coordinate;
import com.pingan.tags.domain.PoiInfo;
import com.pingan.tags.domain.PoiType;

import lombok.extern.slf4j.Slf4j;
import lombok.Getter;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;

@Slf4j
@Getter
@Component
public class CoordServiceImpl implements CoordService {
	@Autowired
	MapRepository repo;
	
	@Autowired
	MapConfig mapConfig;

	@Autowired
	PoiConfig poiConfig;
	
	@Override
	public CoordAddress getCoordAddress(Coordinate wjs84Coord) {
		Coordinate anotherCoord = new Coordinate(wjs84Coord);
		AddressData anotherAddr = new AddressData();
		
		CoordAddress ca = new CoordAddress();
		try {
			Coordinate gcj02Coord = repo.getGCJ02Coord(wjs84Coord);
			Regeo regeo = repo.getGEO(gcj02Coord);
			
			RegeoCode rc = regeo.getRegeocode();
			AddressComponent ac = rc.getAddressComponent();
			AddressData addr = new AddressData(
					ac.getCountry(),
					ac.getProvince(),
					ac.getCity(),
					ac.getDistrict(),
					ac.getTownship(),
					rc.getFormatted_address() //,
//					(ac.getBusinessAreas() == null) ? "" : String.format(";", ac.getBusinessAreas().stream().map(b -> b.getName()).collect(toList())) 
					);

			ca.setCoord(wjs84Coord);
			ca.setAddr(addr);
		} catch (Exception e) {
			log.error("failed to retrieve address for coordinate: " + e.toString(), e);
			ca.setCoord(anotherCoord);
			ca.setAddr(anotherAddr);
		}

		return ca;
	}

	@Override
	public List<PoiInfo> getAroundPoi(Coordinate wjs84Coord, List<PoiType> poiTypes) {
		try {
			List<String> types = poiTypes.stream()
										 .map(x -> x.getType())
										 .map(x -> Arrays.asList(x.split("\\|")))
										 .flatMap(x -> x.stream())
										 .distinct()
										 .collect(toList());
			
			String typeStr = String.join("|", types);
			int pageSize = poiConfig.getPoiAroundPageSize();
			int radius = poiConfig.getPoiAroundRadius();
	
			Coordinate gcj02Coord = repo.getGCJ02Coord(wjs84Coord);
			
			Around around = repo.getPoiAround(gcj02Coord, typeStr, radius, pageSize, 1);
			int amount = Integer.parseInt(around.getCount());
			
			int pageCount = amount / pageSize + (amount % pageSize == 0 ? 0 : 1);
			String c = String.join(",", String.valueOf(wjs84Coord.getLng()), String.valueOf(wjs84Coord.getLat()));
			log.info(c + "; page count:" + pageCount + "; poi amount:" + amount);
			
//			System.out.println(around);
			
			List<PoiInfo> p1 = around.getPois()
									 .stream()
									 .map(p -> new PoiInfo(	p.getId(), 
											 				p.getType(), 
											 				p.getTypecode(), 
											 				p.getName(), 
											 				p.getAddress(), 
											 				Integer.parseInt(p.getDistance()),
											 				p.getPname(),
											 				p.getCityname(),
											 				p.getAdname()))
									 .collect(toList());

			List<PoiInfo> p2 = 
			IntStream.rangeClosed(2,  pageCount)
					 .boxed()
					 .parallel() // 经常假死, 需要并行调用高德 api 获取各个页面
					 .map(x -> repo.getPoiAround(gcj02Coord, typeStr, radius, pageSize, x))
					 .map(a -> a.getPois())
					 .flatMap(p -> p.stream())
					 .map(p -> new PoiInfo(	p.getId(), 
							 				p.getType(), 
							 				p.getTypecode(), 
							 				p.getName(), 
							 				p.getAddress(), 
							 				Integer.parseInt(p.getDistance()),
							 				p.getPname(),
							 				p.getCityname(),
							 				p.getAdname()))
					 .collect(toList());
			
			return Stream.of(p1, p2).flatMap(x -> x.stream()).collect(toList());
//			return (p2 != null) ? Stream.of(p1, p2).flatMap(x -> x.stream()).collect(toList()) : p1;
		} catch (Exception e) {
			log.error("get around poi failed", e);
			return new ArrayList<PoiInfo>();
		}
	}
}
