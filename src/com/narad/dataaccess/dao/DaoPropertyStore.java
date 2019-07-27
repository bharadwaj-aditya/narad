package com.narad.dataaccess.dao;

import java.util.Map;

import com.tinkerpop.blueprints.Vertex;

public interface DaoPropertyStore {
	
	//public DaoPropertyStore buildPropertyStore(Vertex vertex, String keyPrefix);
	
	public DaoPropertyStore buildPropertyStore(Map<String, Object> map, String keyPrefix);
	
	public Map<String, Object> getStoreAsMap(String keyPrefix);

}
