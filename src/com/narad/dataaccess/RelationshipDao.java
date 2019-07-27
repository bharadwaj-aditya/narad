package com.narad.dataaccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;

/**
 * Store properties of relation between 2 nodes. <br/>
 * Note - Between every 2 nodes, there are 2 such relationships, 1 incoming, 1 outgoing.
 * 
 * @author Aditya
 * 
 */
public class RelationshipDao {

	private static final Logger logger = LoggerFactory.getLogger(RelationshipDao.class);

	private Object id;
	private Edge relationship;
	private NodeDao fromNode;
	private NodeDao toNode;
	private String type;
	private Map<String, Map<String, List<RelationshipProfile>>> fromEmailVsToEmailVsRelationProfiles;
	private boolean propertiesLoaded = false;
	private String propertiesString;

	public RelationshipDao() {
		super();
		fromEmailVsToEmailVsRelationProfiles = new HashMap<String, Map<String, List<RelationshipProfile>>>();
		propertiesLoaded = true;
	}

	public RelationshipDao(NodeDao fromNode, NodeDao toNode, String type) {
		this();
		this.fromNode = fromNode;
		this.toNode = toNode;
		this.type = type;
	}

	public RelationshipDao(Edge relationship) {
		this();
		this.relationship = relationship;
		this.type = relationship.getLabel();
		this.fromNode = new NodeDao(relationship.getVertex(Direction.OUT));
		this.toNode = new NodeDao(relationship.getVertex(Direction.IN));
		id = relationship.getId();
		Object relations = relationship.getProperty(DataAccessConstants.PROFILES);
		if (null == relations) {
			// DO something
		} else if (relations instanceof String) {
			propertiesString = (String) relations;
			propertiesLoaded = false;
		} else if (relations instanceof Map) {
			fromEmailVsToEmailVsRelationProfiles = (Map) relations;
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
				String fromEmail = entry.getKey();
				Map<String, Object> innerMap = (Map) entry.getValue();
				for (Map.Entry<String, Object> innerEntry : innerMap.entrySet()) {
					String toEmail = innerEntry.getKey();
					List<Map<String, Object>> profiles = (List) innerEntry.getValue();
					for (Map<String, Object> profile : profiles) {
						String network = (String) profile.get(DataAccessConstants.NETWORK);
						addProfile(fromEmail, toEmail, network, profile);
					}
				}
			}
		} catch (ParseException e) {
			// TODO - exception handling
			logger.error("Error while parsing dao for node with id: {}. Exception: {}", id, e.getMessage());
		} catch (ClassCastException e) {
			logger.error("Error while parsing dao for node with id: {}. Exception: {}", id, e.getMessage());
		}
	}

	public Object getId() {
		return id;
	}

	public Edge getRelationship() {
		return relationship;
	}

	public String getType() {
		return type;
	}

	public NodeDao getFromNode() {
		return fromNode;
	}

	public NodeDao getToNode() {
		return toNode;
	}

	public Map<String, Map<String, List<RelationshipProfile>>> getFromEmailVsToEmailVsRelationProfiles() {
		lazyLoadProperties();
		return fromEmailVsToEmailVsRelationProfiles;
	}

	public String[] getEmailRelations() {
		lazyLoadProperties();
		Set<String> emailRelations = new HashSet<String>();
		for (Map.Entry<String, Map<String, List<RelationshipProfile>>> entry : fromEmailVsToEmailVsRelationProfiles
				.entrySet()) {
			String fromEmail = entry.getKey();
			Map<String, List<RelationshipProfile>> innerMap = entry.getValue();
			for (String toEmail : innerMap.keySet()) {
				emailRelations.add(buildEmailRelationStr(fromEmail, toEmail));
			}
		}
		String[] emailRelationsArr = emailRelations.toArray(new String[emailRelations.size()]);
		return emailRelationsArr;
	}

	public List<RelationshipProfile> getProfilesForEmail(String fromEmail, String toEmail) {
		lazyLoadProperties();
		Map<String, List<RelationshipProfile>> toEmailMap = fromEmailVsToEmailVsRelationProfiles.get(fromEmail);
		if (toEmailMap == null) {
			return null;
		}
		return toEmailMap.get(toEmail);
	}

	public void addProfile(String fromEmail, String toEmail, String network, Map<String, Object> properties) {
		lazyLoadProperties();
		RelationshipProfile profile = new RelationshipProfile(fromEmail, toEmail, network);
		profile.putAll(properties);

		Map<String, List<RelationshipProfile>> toEmailVsProfiles = fromEmailVsToEmailVsRelationProfiles.get(fromEmail);
		if (toEmailVsProfiles == null) {
			toEmailVsProfiles = new HashMap<String, List<RelationshipProfile>>();
			fromEmailVsToEmailVsRelationProfiles.put(fromEmail, toEmailVsProfiles);
		}

		List<RelationshipProfile> profiles = toEmailVsProfiles.get(toEmail);
		if (null == profiles) {
			profiles = new ArrayList<RelationshipProfile>();
			toEmailVsProfiles.put(toEmail, profiles);
		}
		profiles.add(profile);
	}

	public Map<String, Object> getPropertiesAsMap() {
		lazyLoadProperties();
		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put(DataAccessConstants.ID, id);
		properties.put(DataAccessConstants.PROFILES, fromEmailVsToEmailVsRelationProfiles);
		return properties;
	}

	public Object getProperties() {
		if (propertiesLoaded) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.putAll(fromEmailVsToEmailVsRelationProfiles);
			String jsonString = jsonObject.toJSONString();
			return jsonString;
		} else {
			return propertiesString;
		}
	}

	public static String buildEmailRelationStr(String fromEmail, String toEmail) {
		return fromEmail + "," + toEmail;// SET THE SEPERATOR CAREFULLY-AFTER CHECKING INDEX(EG-LUCENE INDEX) PARAMETERS
	}
}
