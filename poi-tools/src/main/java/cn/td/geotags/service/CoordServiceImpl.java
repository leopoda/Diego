package cn.td.geotags.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import java.util.stream.Stream;
import java.util.stream.IntStream;
import static java.util.stream.Collectors.toList;
import org.springframework.beans.factory.annotation.Autowired;

import cn.td.geotags.amap.around.Around;
import cn.td.geotags.amap.regeo.Regeo;
import cn.td.geotags.amap.regeo.RegeoCode;
import cn.td.geotags.amap.regeo.AddressComponent;

import cn.td.geotags.config.MapConfig;
import cn.td.geotags.config.PoiConfig;
import cn.td.geotags.dao.MapRepository;
import cn.td.geotags.domain.AddressData;
import cn.td.geotags.domain.CoordAddress;
import cn.td.geotags.domain.Coordinate;
import cn.td.geotags.domain.PoiInfo;
import cn.td.geotags.domain.PoiType;
import cn.td.geotags.util.Contants;

import lombok.extern.slf4j.Slf4j;
import lombok.Getter;

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
	@Deprecated
	public CoordAddress getCoordAddress(Coordinate wjs84Coord) {
		return getCoordAddress(wjs84Coord, Contants.PARAM_COORD_SYS_GPS);
	}
	
	@Override
	public CoordAddress getCoordAddress(Coordinate coord, String coordsys) {
		Coordinate anotherCoord = new Coordinate(coord);
		AddressData anotherAddr = new AddressData();
		
		CoordAddress ca = new CoordAddress();
		Coordinate gcj02Coord = null;
		try {
			gcj02Coord = repo.getGCJ02Coord(coordsys, coord);
			Regeo regeo = repo.getGEO(gcj02Coord);
			
			RegeoCode rc = regeo.getRegeocode();
			AddressComponent ac = rc.getAddressComponent();
			AddressData addr = new AddressData(
					ac.getCountry(),
					ac.getProvince(),
					ac.getCity(),
					ac.getDistrict(),
					ac.getTownship(),
					rc.getFormatted_address());

			ca.setCoord(coord);
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
		return getAroundPoi(wjs84Coord, types, radius, Contants.PARAM_COORD_SYS_GPS, null);
	}
	
	@Override
	public List<PoiInfo> getAroundPoi(Coordinate coord, String types, long radius, String coordsys, Map<String, Object> additional) {
		try {
			int pageSize = poiConfig.getPoiAroundPageSize();
			Coordinate gcj02Coord = repo.getGCJ02Coord(coordsys, coord);
			
			if (gcj02Coord == null) {
				throw new RuntimeException("unable to get GCJ02 coordinate");
			}
			
			Around around = repo.getPoiAround(gcj02Coord, types, radius, pageSize, 1, additional); // 加入调用次数监控
			
			int amount = 0;
			try {
				amount = Integer.parseInt(around.getCount());
			} catch (Exception ex) {
				log.error("get poi count failed, use default value 0");
			}	
						
			int pageCount = amount / pageSize + (amount % pageSize == 0 ? 0 : 1);
			String c = String.join(",", String.valueOf(coord.getLng()), String.valueOf(coord.getLat()));
			log.debug(c + "; page count:" + pageCount + "; poi amount:" + amount);
			
			List<PoiInfo> p1 =
			IntStream.rangeClosed(1, 1)
					 .boxed()
					 .map(o -> around)
					 .filter(o -> o != null)
					 .map(o -> o.getPois())
					 .filter(o -> o != null)
					 .flatMap(o -> o.stream())
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
							po.setCenter(coord);
							po.setAmapCenter(gcj02Coord);
							return po;})
					 .collect(toList());

			List<PoiInfo> p2 = 
			IntStream.rangeClosed(2,  pageCount)
					 .boxed()
					 .parallel() // 并行调用高德 api 获取各个页面
					 .map(x -> repo.getPoiAround(gcj02Coord, types, radius, pageSize, x, additional)) // 加入调用次数监控
					 .filter(x -> x != null)
					 .map(a -> a.getPois())
					 .filter(o -> o != null)
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
							po.setCenter(coord);
							po.setAmapCenter(gcj02Coord);
							return po;})
					 .collect(toList());
			
			return Stream.of(p1, p2).flatMap(x -> x.stream()).collect(toList());
		} catch (Exception e) {
			log.error("get around poi failed", e);
			return new ArrayList<PoiInfo>();
		}
	}
}
