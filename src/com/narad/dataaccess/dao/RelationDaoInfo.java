package com.narad.dataaccess.dao;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

import com.narad.util.NaradMapUtils;

public class RelationDaoInfo implements DaoPropertyStore {

	private String type;
	private String name;
	private String subType;
	private Integer distance;
	private Integer weight;
	private Map<String, Object> properties;

	public RelationDaoInfo() {
		super();
		properties = new HashMap<String, Object>();
	}

	@Override
	public RelationDaoInfo buildPropertyStore(Map<String, Object> map, String keyPrefix) {
		String keyPrefix2 = keyPrefix == null ? "" : keyPrefix + DaoConstants.SEPERATOR;
		type = MapUtils.getString(map, keyPrefix2 + DaoConstants.REL_TYPE);
		name = MapUtils.getString(map, keyPrefix2 + DaoConstants.REL_NAME);
		subType = MapUtils.getString(map, keyPrefix2 + DaoConstants.REL_SUB_TYPE);
		distance = MapUtils.getInteger(map, keyPrefix2 + DaoConstants.REL_DISTANCE);
		weight = MapUtils.getInteger(map, keyPrefix2 + DaoConstants.REL_WEIGHT);
		if (weight != null) {
			this.weight = weight;
		}
		if (type != null && name != null) {
			properties = NaradMapUtils.stringToJson(MapUtils.getString(map, keyPrefix2 + DaoConstants.PROPERTIES));
			return this;
		}
		return null;
	}

	@Override
	public Map<String, Object> getStoreAsMap(String keyPrefix) {
		String keyPrefix2 = keyPrefix == null ? "" : keyPrefix + DaoConstants.SEPERATOR;
		HashMap<String, Object> map = new HashMap<String, Object>();
		NaradMapUtils.putInMapIfNotNull(map, keyPrefix2 + DaoConstants.REL_TYPE, type);
		NaradMapUtils.putInMapIfNotNull(map, keyPrefix2 + DaoConstants.REL_NAME, name);
		NaradMapUtils.putInMapIfNotNull(map, keyPrefix2 + DaoConstants.REL_SUB_TYPE, subType);
		NaradMapUtils.putInMapIfNotNull(map, keyPrefix2 + DaoConstants.REL_DISTANCE, distance);
		NaradMapUtils.putInMapIfNotNull(map, keyPrefix2 + DaoConstants.REL_WEIGHT, weight);
		NaradMapUtils.putInMapIfNotNull(map, keyPrefix2 + DaoConstants.PROPERTIES, NaradMapUtils.mapToJson(properties));
		return map;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}

	public Integer getDistance() {
		return distance;
	}

	public void setDistance(Integer level) {
		this.distance = level;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

}
