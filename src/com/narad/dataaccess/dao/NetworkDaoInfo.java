package com.narad.dataaccess.dao;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

import com.narad.util.NaradMapUtils;

public class NetworkDaoInfo implements DaoPropertyStore {

	private String userid;
	private String networkId;
	private String networkName;
	private String userUrl;
	private Map<String, Object> properties;

	public NetworkDaoInfo() {
		super();
	}

	@Override
	public DaoPropertyStore buildPropertyStore(Map<String, Object> map, String keyPrefix) {
		String keyPrefix2 = keyPrefix == null ? "" : keyPrefix + DaoConstants.SEPERATOR;
		userid = MapUtils.getString(map, keyPrefix2 + DaoConstants.NETWORK_USER_ID);
		userUrl = MapUtils.getString(map, keyPrefix2 + DaoConstants.NETWORK_USER_URL);
		networkId = MapUtils.getString(map, keyPrefix2 + DaoConstants.NETWORK_ID);
		networkName = MapUtils.getString(map, keyPrefix2 + DaoConstants.NETWORK_NAME);
		if ((userid != null || userUrl != null) && networkId != null) {
			properties = NaradMapUtils.stringToJson(MapUtils.getString(map, keyPrefix2 + DaoConstants.PROPERTIES));
			return this;
		}
		return null;
	}

	@Override
	public Map<String, Object> getStoreAsMap(String keyPrefix) {
		String keyPrefix2 = keyPrefix == null ? "" : keyPrefix + DaoConstants.SEPERATOR;
		HashMap<String, Object> map = new HashMap<String, Object>();
		NaradMapUtils.putInMapIfNotNull(map, keyPrefix2 + DaoConstants.NETWORK_USER_ID, userid);
		NaradMapUtils.putInMapIfNotNull(map, keyPrefix2 + DaoConstants.NETWORK_USER_URL, userUrl);
		NaradMapUtils.putInMapIfNotNull(map, keyPrefix2 + DaoConstants.NETWORK_ID, networkId);
		NaradMapUtils.putInMapIfNotNull(map, keyPrefix2 + DaoConstants.NETWORK_NAME, networkName);
		NaradMapUtils.putInMapIfNotNull(map, keyPrefix2 + DaoConstants.PROPERTIES, NaradMapUtils.mapToJson(properties));
		return map;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userId) {
		this.userid = userId;
	}

	public String getUserUrl() {
		return userUrl;
	}

	public void setUserUrl(String userUrl) {
		this.userUrl = userUrl;
	}

	public String getNetworkId() {
		return networkId;
	}

	public void setNetworkId(String networkId) {
		this.networkId = networkId;
	}

	public String getNetworkName() {
		return networkName;
	}

	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

}
