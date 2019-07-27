package com.narad.dataaccess;

import java.util.HashMap;
import java.util.Map;

public class NodeProfile extends HashMap<String, Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2627853771754575697L;

	/**
	 * Name of the network that this profile gives information of.
	 */
	private String network;
	private String type;// social,email etc
	private boolean isActive;

	public NodeProfile(Map<String, Object> map) {
		this((String) map.get(DataAccessConstants.NETWORK));
		putAll(map);
	}

	public NodeProfile(String network) {
		super();
		this.network = network;
		type = "social";
		isActive = true;
		put(DataAccessConstants.NETWORK, network);
	}

	public String getNetwork() {
		return network;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

}
