package cn.td.geotags.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class CoordAddress {
	private Coordinate coord;
	private Coordinate amapCoord;
	private AddressData addr;
	
	private final static ObjectMapper m = new ObjectMapper().configure(MapperFeature.USE_ANNOTATIONS, true);

	public CoordAddress() {}
	
	public CoordAddress(Coordinate coord, AddressData addr) {
		this.coord = coord;
		this.addr = addr;
	}

	@Override
	public String toString() {
		try {
			return m.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			log.error("", e);
			return "{'coord' : {'lng' : 0, 'lat' : 0}, 'addr' : {'country' : , 'province' : , 'city' : , 'district' : , 'township' : , 'formattedAddress' :}}";
		}
	}
	
	public String asFlatText() {
		return String.join(	"\t", 
							String.valueOf(coord.getLng()),
							String.valueOf(coord.getLat()),
							String.valueOf(amapCoord.getLng()),
							String.valueOf(amapCoord.getLat()),
							addr.getCountry() == null ? "" : addr.getCountry(),
							addr.getProvince() == null ? "" : addr.getProvince(),
							addr.getCity() == null ? "" : addr.getCity(),
							addr.getDistrict() == null ? "" : addr.getDistrict(),
							addr.getTown() == null ? "" : addr.getTown(),
							addr.getAddress() == null ? "" : addr.getAddress());
	}
}
