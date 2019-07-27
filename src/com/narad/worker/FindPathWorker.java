package com.narad.worker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Direction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.narad.dataaccess.DataAccess;
import com.narad.dataaccess.DataAccessConstants;
import com.narad.dataaccess.NaradRelationTypes;
import com.narad.dataaccess.NodeDao;
import com.narad.dataaccess.dao.PersonDao;
import com.narad.dataaccess.dao.PersonRelationDao;
import com.narad.exception.ExceptionErrorCodes;
import com.narad.exception.NaradException;
import com.narad.exception.NaradWorkerException;
import com.narad.util.NaradMapUtils;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

public class FindPathWorker implements INaradWorker {
	private static final Logger logger = LoggerFactory.getLogger(FindPathWorker.class);

	@Override
	public String getName() {
		return "FindPathWorker";
	}

	/**
	 * Find the first matching shortest path matching the from and to email ids.
	 * 
	 * @param email
	 * @return
	 * @return
	 * @throws NaradWorkerException
	 */
	public List<NodeDao> findPath(String fromEmailId, String toEmailId) throws NaradException {

		Vertex startNode = DataAccess.getInstance().findOneVertex(DataAccessConstants.NODE_INDEX,
				DataAccessConstants.NODE_INDEX_PARAM_EMAIL, fromEmailId);
		Vertex endNode = DataAccess.getInstance().findOneVertex(DataAccessConstants.NODE_INDEX,
				DataAccessConstants.NODE_INDEX_PARAM_EMAIL, toEmailId);
		if (startNode == null) {
			logger.debug("Cannot find path as start node is null for email: {}", fromEmailId);
			throw new NaradWorkerException("email: " + fromEmailId, null, ExceptionErrorCodes.WORKER_NODE_NOT_FOUND,
					"FindPath");
		} else if (endNode == null) {
			logger.debug("Cannot find path as end node is null for email: {}", toEmailId);
			throw new NaradWorkerException("email: " + fromEmailId, null, ExceptionErrorCodes.WORKER_NODE_NOT_FOUND,
					"FindPath");
		}
		List<Vertex> shortestPath = DataAccess.getInstance().shortestPath(startNode, endNode,
				NaradRelationTypes.FRIEND, Direction.OUTGOING, 10);
		if (shortestPath == null) {
			return null;
		}
		List<NodeDao> pathNodesList = new ArrayList<NodeDao>(shortestPath.size());
		for (Vertex node : shortestPath) {
			pathNodesList.add(new NodeDao(node));
		}
		return pathNodesList;
	}
	
	public List<Object> findPath(PersonDao fromPerson, PersonDao toPerson, Map<String, Object> properties) {
		//Construct query from properties
		
		//TODO - use gremlin instead of cypher
		
		if (properties == null || properties.isEmpty()) {
			List<Vertex> shortestPath = DataAccess.getInstance().shortestPath(fromPerson.getVertex(), toPerson.getVertex(),
					NaradRelationTypes.FRIEND, Direction.OUTGOING, 10);
			if (shortestPath == null) {
				return null;
			}
			List<Object> pathNodesList = new ArrayList<Object>(shortestPath.size());
			for (Vertex node : shortestPath) {
				pathNodesList.add(new PersonDao(node));
			}
			return pathNodesList;
		}
		
		//Conditions
		//Return values
		Object fromId = fromPerson.getVertex().getId();
		Object toId = toPerson!=null?toPerson.getVertex().getId():null;
		Object maxDistanceObj = properties.get("maxDistance");

		StringBuilder br = new StringBuilder();
		br.append("START ");
		br.append("a=node(").append(fromId).append(") "); 
		if (toId != null) {
			br.append(", b=node(").append(toId).append(") ");
		}
		int maxDistance = 3;
		if (maxDistanceObj != null && maxDistanceObj instanceof Number && ((Number) maxDistanceObj).intValue() > 0) {
			maxDistance = ((Number) maxDistanceObj).intValue();
		}
		br.append("MATCH p=a-[*..").append(maxDistance).append("]->b");
		
		Map<String, Object> conditionsMap = NaradMapUtils.getCheckedValueFromMap(properties, "conditions",
				Map.class);
		//TODO - add handling where null columns are being checked
		List<String> nullProperties = NaradMapUtils.getPropertyStringListFromMap(properties, "nullProperties");
		int i =0;
		if (conditionsMap != null) {
			int conditionsSize = conditionsMap.size() + (nullProperties != null ? nullProperties.size() : 0);
			br.append(" WHERE ");
			for(Map.Entry<String, Object> entry: conditionsMap.entrySet()) {
				String key = entry.getKey();
				br.append(key).append("=");
				Object value = entry.getValue();
				if (value == null || value instanceof Number || value instanceof Boolean) {
					br.append(value);
				} else if (value instanceof String) {
					br.append("'").append(value).append("'");
				}
				if (i < conditionsSize - 1) {
					br.append(" AND ");
					i++;
				} else {
					br.append(" ");
				}
			}
		}
		
		//TODO - add handling for return - use source node, dest node, path nodes and relationships
		List<String> returnValues = NaradMapUtils.getPropertyStringListFromMap(properties, "returnValues");
		br.append(" RETURN ");
		if (returnValues == null || returnValues.isEmpty()) {
			br.append("p");
		}
		
		String cypherQuery = br.toString();
		List<Map<String,Object>> executeQuery = DataAccess.getInstance().executeQuery(cypherQuery);
		if (executeQuery == null || executeQuery.size() == 0) {
			logger.info("There is not path for the given parameters");
			return null;
		} else {
			List<Object> processedQueryResult = new ArrayList<Object>(); 
			for (Map<String, Object> queryRow : executeQuery) {
				HashMap<String,Object> hashMap = new HashMap<String, Object>();
				for (Map.Entry<String, Object> entry : queryRow.entrySet()) {
					String key = entry.getKey();
					Object value = entry.getValue();
					if(value instanceof Vertex) {
						PersonDao personDao = new PersonDao((Vertex)value);
						hashMap.put(key, personDao.getPersonAsMap());
					} else if (value instanceof Edge) {
						PersonRelationDao personDao = new PersonRelationDao((Edge)value);
						hashMap.put(key, personDao.getRelationDaoAsMap());
					} else if (value instanceof Map && ((Map)value).containsKey("edges") && ((Map)value).containsKey("vertices")) {
						//TODO - simplify this !!!
						Map<String, Object> queryMap = (Map) value;
						List<Vertex> vertexList = (List) queryMap.get("vertices");
						ArrayList<Map<String,Object>> personList = new ArrayList<Map<String,Object>>();
						for (Vertex vertex : vertexList) {
							personList.add(new PersonDao(vertex).getPersonAsMap());
						}
						ArrayList<Map<String,Object>> personRelationList = new ArrayList<Map<String,Object>>();
						List<Edge> edgeList = (List) queryMap.get("edges");
						for (Edge edge : edgeList) {
							personRelationList.add(new PersonRelationDao(edge).getRelationDaoAsMap());
						}
						HashMap<String,Object> hashMap2 = new HashMap<String, Object>();
						hashMap2.put("person", personList);
						hashMap2.put("relation", personRelationList);
						
						hashMap.put(key, hashMap2);
					} else {
						hashMap.put(key, value);
					}
				}
				processedQueryResult.add(hashMap);
			}
			logger.info("Completed finding path");
			return processedQueryResult;
		}
	}

}
