package cn.td.geotags.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import cn.td.geotags.amap.around.Around;
import cn.td.geotags.amap.regeo.AddressComponent;
import cn.td.geotags.amap.regeo.Regeo;
import cn.td.geotags.amap.regeo.RegeoCode;
import cn.td.geotags.config.MapConfig;
import cn.td.geotags.config.PoiConfig;
import cn.td.geotags.dao.MapRepository;
import cn.td.geotags.domain.AddressData;
import cn.td.geotags.domain.CoordAddress;
import cn.td.geotags.domain.Coordinate;
import cn.td.geotags.domain.PoiInfo;
import cn.td.geotags.domain.PoiType;
import lombok.extern.slf4j.Slf4j;
import lombok.Getter;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;

@Slf4j
@Getter
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
		Coordinate gcj02Coord = null;
		try {
			gcj02Coord = repo.getGCJ02Coord(wjs84Coord);
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
			ca.setAmapCoord(gcj02Coord);
			ca.setAddr(addr);
		} catch (Exception e) {
			log.error("failed to retrieve address for coordinate: " + e.toString(), e);
			ca.setCoord(anotherCoord);
			if (gcj02Coord != null) {
				ca.setAmapCoord(gcj02Coord);
			}
			ca.setAddr(anotherAddr);
		}

		return ca;
	}

	public List<PoiInfo> getAroundPoi(Coordinate wjs84Coord, List<PoiType> poiTypes) {
		List<String> types = poiTypes.stream()
				.map(x -> x.getType())
				.map(x -> Arrays.asList(x.split("\\|")))
				.flatMap(x -> x.stream())
				.distinct()
				.collect(toList());

		String typeStr = String.join("|", types);
		
		return this.getAroundPoi(wjs84Coord, typeStr, poiConfig.getPoiAroundRadius());
	}
	
	@Override
	public List<PoiInfo> getAroundPoi(Coordinate wjs84Coord, String types, long radius) {
		try {
//			List<String> types = poiTypes.stream()
//										 .map(x -> x.getType())
//										 .map(x -> Arrays.asList(x.split("\\|")))
//										 .flatMap(x -> x.stream())
//										 .distinct()
//										 .collect(toList());
			
//			String typeStr = String.join("|", types);
			int pageSize = poiConfig.getPoiAroundPageSize();
//			int radius = poiConfig.getPoiAroundRadius();
	
			Coordinate gcj02Coord = repo.getGCJ02Coord(wjs84Coord);
			
			Around around = repo.getPoiAround(gcj02Coord, types, radius, pageSize, 1);
			int amount = Integer.parseInt(around.getCount());
			
			int pageCount = amount / pageSize + (amount % pageSize == 0 ? 0 : 1);
			String c = String.join(",", String.valueOf(wjs84Coord.getLng()), String.valueOf(wjs84Coord.getLat()));
			log.info(c + "; page count:" + pageCount + "; poi amount:" + amount);
			
//			System.out.println(around);
			
			List<PoiInfo> p1 = around.getPois().stream().map(p -> {
													PoiInfo po = new PoiInfo(p.getId(), 
															p.getType(), 
															p.getTypecode(), 
															p.getName(), 
															p.getAddress(),
															Integer.parseInt(p.getDistance()), 
															p.getPname(), 
															p.getCityname(), 
															p.getAdname(),
															p.getLocation());
													po.setCenter(wjs84Coord);
													po.setAmapCenter(gcj02Coord);
													return po;})
										.collect(toList());

			List<PoiInfo> p2 = 
			IntStream.rangeClosed(2,  pageCount)
					 .boxed()
					 .parallel() // 经常假死, 需要并行调用高德 api 获取各个页面
					 .map(x -> repo.getPoiAround(gcj02Coord, types, radius, pageSize, x))
					 .map(a -> a.getPois())
					 .flatMap(p -> p.stream())
					 .map(p -> {
							PoiInfo po = new PoiInfo(p.getId(), 
									p.getType(), 
									p.getTypecode(), 
									p.getName(), 
									p.getAddress(),
									Integer.parseInt(p.getDistance()), 
									p.getPname(), 
									p.getCityname(), 
									p.getAdname(),
									p.getLocation());
							po.setCenter(wjs84Coord);
							po.setAmapCenter(gcj02Coord);
							return po;})
					 .collect(toList());
			
			return Stream.of(p1, p2).flatMap(x -> x.stream()).collect(toList());
//			return (p2 != null) ? Stream.of(p1, p2).flatMap(x -> x.stream()).collect(toList()) : p1;
		} catch (Exception e) {
			log.error("get around poi failed", e);
			return new ArrayList<PoiInfo>();
		}
	}
}
