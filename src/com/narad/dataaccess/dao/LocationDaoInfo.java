package com.narad.dataaccess.dao;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

import com.narad.util.NaradMapUtils;

public class LocationDaoInfo implements DaoPropertyStore {

	private String address;
	private String city;
	private String country;
	private Integer fromYear;
	private Integer toYear;
	private Double lattitude;
	private Double longitude;
	private Map<String, Object> properties;

	@Override
	public DaoPropertyStore buildPropertyStore(Map<String, Object> map, String keyPrefix) {
		String keyPrefix2 = keyPrefix == null ? "" : keyPrefix + DaoConstants.SEPERATOR;
		address = MapUtils.getString(map, keyPrefix2 + DaoConstants.LOC_ADDRESS);
		city = MapUtils.getString(map, keyPrefix2 + DaoConstants.LOC_CITY);
		country = MapUtils.getString(map, keyPrefix2 + DaoConstants.LOC_COUNTRY);
		fromYear = MapUtils.getInteger(map, keyPrefix2 + DaoConstants.LOC_FROM_YEAR);
		toYear = MapUtils.getInteger(map, keyPrefix2 + DaoConstants.LOC_TO_YEAR);
		lattitude = MapUtils.getDouble(map, keyPrefix2 + DaoConstants.LOC_LATTITUDE);
		longitude = MapUtils.getDouble(map, keyPrefix2 + DaoConstants.LOC_LONGITUDE);
		if ((lattitude != null && longitude != null) || address != null || city != null || country != null) {
			properties = NaradMapUtils.stringToJson(MapUtils.getString(map, keyPrefix2 + DaoConstants.PROPERTIES));
			return this;
		}
		return null;
	}

	@Override
	public Map<String, Object> getStoreAsMap(String keyPrefix) {
		String keyPrefix2 = keyPrefix == null ? "" : keyPrefix + DaoConstants.SEPERATOR;
		HashMap<String, Object> map = new HashMap<String, Object>();
		NaradMapUtils.putInMapIfNotNull(map, keyPrefix2 + DaoConstants.LOC_ADDRESS, address);
		NaradMapUtils.putInMapIfNotNull(map, keyPrefix2 + DaoConstants.LOC_CITY, city);
		NaradMapUtils.putInMapIfNotNull(map, keyPrefix2 + DaoConstants.LOC_COUNTRY, country);
		NaradMapUtils.putInMapIfNotNull(map, keyPrefix2 + DaoConstants.LOC_FROM_YEAR, fromYear);
		NaradMapUtils.putInMapIfNotNull(map, keyPrefix2 + DaoConstants.LOC_TO_YEAR, toYear);
		NaradMapUtils.putInMapIfNotNull(map, keyPrefix2 + DaoConstants.LOC_LATTITUDE, lattitude);
		NaradMapUtils.putInMapIfNotNull(map, keyPrefix2 + DaoConstants.LOC_LONGITUDE, longitude);
		NaradMapUtils.putInMapIfNotNull(map, keyPrefix2 + DaoConstants.PROPERTIES, NaradMapUtils.mapToJson(properties));
		return map;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Integer getFromYear() {
		return fromYear;
	}

	public void setFromYear(Integer fromYear) {
		this.fromYear = fromYear;
	}

	public Integer getToYear() {
		return toYear;
	}

	public void setToYear(Integer toYear) {
		this.toYear = toYear;
	}

	public Double getLattitude() {
		return lattitude;
	}

	public void setLattitude(Double lattitude) {
		this.lattitude = lattitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}
}
