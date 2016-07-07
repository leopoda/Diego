package com.pingan.tags.db.jdbc;

import java.util.HashMap;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.pingan.tags.db.Repository;
import com.pingan.tags.domain.Address;

public class JdbcRepository implements Repository {

	private JdbcTemplate jdbcTemplate;
	
	public JdbcRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	@Override
	public Address save(Address a) {
		long id = a.getId();
		Address b = a;
		if (id == 0) {
			id = insertAndReturnId(a);
			//System.out.println("id:" + id);
			b = new Address(
					a.getId(),
					a.getOffset(),
					a.getMonth(),
					a.getHour(),
					a.getLng(),
					a.getLat(),
					a.getSource(),
					a.getCountry(),
					a.getProvince(),
					a.getCity(),
					a.getDistrict(),
					a.getTownship(),
					a.getAddress(),
					a.isWeekend(),
					a.getCount());
			
		} else {
			final String sql =  "update address set tdid = ?, "
					          					 + "month = ?, " 
					          					 + "hour = ?, "
					          					 + "lng = ?, "
					          					 + "lat = ?, "
					          					 + "country = ?, "
					          					 + "province = ?, "
					          					 + "city = ?, "
					          					 + "district = ?, "
					          					 + "township = ?, "
					          					 + "address = ?, "
					          					 + "isWeekend = ?, "
					          					 + "count = ? "
					          					 + "where id = ?";
			jdbcTemplate.update(sql,
					a.getOffset(),
					a.getMonth(),
					a.getHour(),
					a.getLng(),
					a.getLat(),
					a.getCountry(),
					a.getProvince(),
					a.getCity(),
					a.getDistrict(),
					a.getTownship(),
					a.getAddress(),
					a.isWeekend(),
					a.getCount(),
					a.getId());
		}
		return b;
	}

	@Override
	public Address findOne(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Address findByTdId(String tdid) {
		// TODO Auto-generated method stub
		return null;
	}

	private long insertAndReturnId(Address a) {
		SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
		Map<String, Object> args = new HashMap<String, Object>();
		
		args.put("offset", a.getOffset());
		args.put("month", a.getMonth());
		args.put("hour", a.getHour());
		args.put("lng", a.getLng());
		args.put("lat", a.getLat());
		args.put("country", a.getCountry());
		args.put("province", a.getProvince());
		args.put("city", a.getCity());
		args.put("district", a.getDistrict());
		args.put("township", a.getTownship());
		args.put("address", a.getAddress());
		args.put("isWeekend", a.isWeekend());
		args.put("count", a.getCount());
		
		return 
		jdbcInsert.withTableName("address")
				  .usingColumns("offset", "month", "hour", "lng", "lat", "country", "province", "city", "district", "township", "address", "isWeekend", "count")
				  .usingGeneratedKeyColumns("id")
				  .executeAndReturnKey(args)
				  .longValue();
	}
}
