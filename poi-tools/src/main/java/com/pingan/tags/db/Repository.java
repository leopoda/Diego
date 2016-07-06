package com.pingan.tags.db;

import com.pingan.tags.domain.Address;

public interface Repository {
	Address save(Address a);
	Address findOne(long id);
	Address findByTdId(String tdid);
}
