package com.narad.dataaccess;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores properties of relation between 2 email ids
 * 
 * @author Aditya
 */
public class RelationshipProfile extends HashMap<String, Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2627853771754575697L;

	private String fromEmail;
	private String toEmail;
	private String network;
	
	public RelationshipProfile(Map<String, Object> map) {
		putAll(map);
		this.fromEmail = (String)map.get(DataAccessConstants.FROM_EMAIL);
		this.toEmail = (String)map.get(DataAccessConstants.TO_EMAIL);
		this.network = (String)map.get(DataAccessConstants.NETWORK);
	}

	public RelationshipProfile(String fromEmail, String toEmail, String network) {
		super();
		this.fromEmail = fromEmail;
		this.toEmail = toEmail;
		this.network = network;
		put(DataAccessConstants.FROM_EMAIL, fromEmail);
		put(DataAccessConstants.TO_EMAIL, toEmail);
		put(DataAccessConstants.NETWORK, network);
	}

	public String getFromEmail() {
		return fromEmail;
	}

	public String getToEmail() {
		return toEmail;
	}

	public String getNetwork() {
		return network;
	}
}
