package com.narad.service.rest;

import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonRequestMap extends HashMap<String, Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8320568923013169998L;

	public JsonRequestMap() {
		super();
	}

	public JsonRequestMap(String s) {
		super();
		JSONParser jsonParser = new JSONParser();
		try {
			if (s != null) {
				JSONObject parse = (JSONObject) jsonParser.parse(s);
				putAll(parse);
			}
		} catch (ParseException e) {
			put(ServiceConstants.EXCEPTION, e.getMessage());
		}
	}

	public String toString() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.putAll(this);
		return jsonObject.toJSONString();
	}

}
