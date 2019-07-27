package com.narad.dataaccess;

import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.RelationshipType;

import com.narad.exception.NaradDataAccessException;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Parameter;
import com.tinkerpop.blueprints.TransactionalGraph.Conclusion;
import com.tinkerpop.blueprints.Vertex;

/**
 *TODO Aditya - add javadocs!
 */
public interface GraphDataSource {

	public String getName();
	
	public void shutdown();

	public Object getRawVertex(Vertex vertex);

	public Object getRawEdge(Edge edge);

	public Vertex createNode(Map<String, Object> properties);

	public void saveNode(Vertex vertex, Map<String, Object> properties);

	public Vertex createNode();

	public Edge createEdge(Vertex outVertex, Vertex inVertex, String label);

	public Vertex getNodeById(Object nodeId);

	public Edge getRelationshipById(Object relationshipId);

	public void beginTransaction();
	
	public void completeTransaction(Conclusion conclusion);

	public Vertex getReferenceNode();

	public void addConnectionToReferenceNode(Vertex vertex, RelationshipType relationType);

	public void addToNodeIndex(String indexName, Vertex node, String indexParameter, Object indexValue);

	public void addToNodeIndex(String indexName, Vertex node, String indexParameter, Object indexValue,
			Map<String, String> indexProperties);

	public void addToRelationIndex(String indexName, Edge relationShip, String indexParameter, Object indexValue);

	public void addToRelationIndex(String indexName, Edge relationShip, String indexParameter, Object indexValue,
			Map<String, String> indexProperties);
	
	public <T extends Element> void createIndex(String indexName, Class<T> indexClass, Parameter[] indexParameters);

	// Throws exception if more than one match - catch and throw data access exception
	public Vertex findOneVertex(String indexName, String indexParameter, Object indexValue)
			throws NaradDataAccessException;

	public Vertex[] findVertices(String indexName, String indexParameter, Object indexValue);

	public Edge findOneRelationship(String indexName, String indexParameter, Object indexValue)
			throws NaradDataAccessException;

	public Edge[] findRelationship(String indexName, String indexParameter, Object indexValue,
			Map<String, String> paramMap);

	public List<Map<String, Object>> executeQuery(String cypherQuery);

	public List<Vertex> shortestPath(Vertex startVertex, Vertex endVertex, RelationshipType relType,
			Direction direction, int maxDepth);

}