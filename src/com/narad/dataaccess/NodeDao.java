package com.narad.dataaccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Vertex;

/**
 * Stores properties of a node
 * @author Aditya
 *
 */
/**
 * @author Aditya
 * 
 */
public class NodeDao {

	private static final Logger logger = LoggerFactory.getLogger(NodeDao.class);

	private Object id;
	private Vertex node;
	private Map<String, List<NodeProfile>> emailProfiles;
	private boolean propertiesLoaded = false;
	private String propertiesString;

	public NodeDao() {
		super();
		emailProfiles = new HashMap<String, List<NodeProfile>>();
		propertiesLoaded = true;
	}

	public NodeDao(Vertex node) {
		this();
		this.node = node;
		id = node.getId();
		Object emailsObj = node.getProperty(DataAccessConstants.PROFILES);
		if (null == emailsObj) {
			// DO something
		} else if (emailsObj instanceof String) {
			// Postpone for lazyloading
			propertiesString = (String) emailsObj;
			propertiesLoaded = false;
			// loadProperties(emailsObj);
		} else if (emailsObj instanceof Map) {
			emailProfiles = (Map) emailsObj;
		} else {
			// Throw error?
		}
	}

	private void lazyLoadProperties() {
		if (propertiesLoaded) {
			return;
		}
		propertiesLoaded = true;
		try {
			JSONObject parse = (JSONObject) new JSONParser().parse(propertiesString);

			for (Object entryObj : parse.entrySet()) {
				Map.Entry<String, Object> entry = (Map.Entry<String, Object>) entryObj;
				String email = entry.getKey();
				List<Map<String, Object>> profilesList = (List) entry.getValue();
				for (Map<String, Object> profileMap : profilesList) {
					String network = (String) profileMap.get(DataAccessConstants.NETWORK);
					addProfile(email, network, profileMap);
				}
			}
		} catch (ParseException e) {
			// TODO - exception handling
			logger.error("Error while parsing dao for node with id: {} Exception: {}", id, e.getMessage());
		} catch (ClassCastException e) {
			logger.error("Error while parsing dao for node with id: {} Exception: {}", id, e.getMessage());
		}
	}

	public Object getId() {
		return id;
	}

	public Vertex getNode() {
		return node;
	}

	public Map<String, List<NodeProfile>> getEmailProfiles() {
		lazyLoadProperties();
		return emailProfiles;
	}

	public List<NodeProfile> getProfilesForEmail(String email) {
		lazyLoadProperties();
		return emailProfiles.get(email);
	}

	public void addProfile(String email, String network, Map<String, Object> properties) {
		lazyLoadProperties();
		NodeProfile profile = new NodeProfile(network);
		profile.putAll(properties);

		List<NodeProfile> list = emailProfiles.get(email);
		if (null == list) {
			list = new ArrayList<NodeProfile>();
			emailProfiles.put(email, list);
		}
		list.add(profile);
	}

	public Map<String, Object> getPropertiesAsMap() {
		lazyLoadProperties();
		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put(DataAccessConstants.ID, id);
		properties.put(DataAccessConstants.PROFILES, emailProfiles);
		return properties;
	}

	public Object getProperties() {
		if (propertiesLoaded) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.putAll(emailProfiles);
			String jsonString = jsonObject.toJSONString();
			return jsonString;
		} else {
			return propertiesString;
		}
	}

	public Object[] getPropertiesList() {
		lazyLoadProperties();
		Object[] array = emailProfiles.values().toArray();
		return array;
	}

}
