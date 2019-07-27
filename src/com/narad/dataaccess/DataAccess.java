package com.narad.dataaccess;

import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.RelationshipType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.narad.exception.NaradDataAccessException;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Parameter;
import com.tinkerpop.blueprints.TransactionalGraph.Conclusion;
import com.tinkerpop.blueprints.Vertex;

public class DataAccess {

	private static final Logger logger = LoggerFactory.getLogger(DataAccess.class);
	// TODO - explore index configuration
	private GraphDataSource graphDataSource;
	private static DataAccess instance;

	public static DataAccess getInstance() {
		if (instance == null) {
			synchronized (DataAccess.class) {
				if (instance == null) {
					instance = new DataAccess();
				}
			}
		}
		return instance;
	}

	private DataAccess() {
		super();
		init();
	}

	private void init() {
		graphDataSource = new Neo4jDataAccess();
		//Build indexes
		//MapUtil.stringMap( IndexManager.PROVIDER, "lucene", "type", "fulltext" )
		Parameter<String, String> luceneIndexParam = new Parameter<String, String>("provider", "lucene");
		Parameter<String, String> fullTextIndexParam = new Parameter<String, String>("type", "fulltext");
		Parameter<String, String> exactTextIndexParam = new Parameter<String, String>("type", "exact");
		Parameter<String, String> lowerCaseTextIndexParam = new Parameter<String, String>("to_lower_case", "true");
		graphDataSource.createIndex(DataAccessConstants.PERSON_INDEX, Vertex.class, new Parameter[] {
			luceneIndexParam, fullTextIndexParam, lowerCaseTextIndexParam });
		// graphDataSource.createIndex(DataAccessConstants.PERSON_INDEX_PARAM_NAME, Vertex.class, new Parameter[] {
		// luceneIndexParam, fullTextIndexParam, lowerCaseTextIndexParam });
		// graphDataSource.createIndex(DataAccessConstants.PERSON_INDEX_PARAM_EMAIL, Vertex.class, new Parameter[] {
		// luceneIndexParam, exactTextIndexParam });
		// graphDataSource.createIndex(DataAccessConstants.PERSON_INDEX_PARAM_PHONE, Vertex.class, new Parameter[] {
		// luceneIndexParam, exactTextIndexParam});
		// graphDataSource.createIndex(DataAccessConstants.PERSON_INDEX_PARAM_WEBSITE, Vertex.class, new Parameter[] {
		// luceneIndexParam, exactTextIndexParam });
		// graphDataSource.createIndex(DataAccessConstants.PERSON_INDEX_PARAM_EDUCATION, Vertex.class, new Parameter[] {
		// luceneIndexParam, fullTextIndexParam, lowerCaseTextIndexParam });
		// graphDataSource.createIndex(DataAccessConstants.PERSON_INDEX_PARAM_JOBS, Vertex.class, new Parameter[] {
		// luceneIndexParam, fullTextIndexParam, lowerCaseTextIndexParam });
		// graphDataSource.createIndex(DataAccessConstants.PERSON_INDEX_PARAM_SKILLS, Vertex.class, new Parameter[] {
		// luceneIndexParam, fullTextIndexParam, lowerCaseTextIndexParam });
		// graphDataSource.createIndex(DataAccessConstants.PERSON_INDEX_PARAM_AGE, Vertex.class, new Parameter[] {
		//	luceneIndexParam });
	}
	
	public void shutdown() {
		synchronized (DataAccess.class) {
			DataAccess currentDataAccess = instance;
			instance = null;
			try {
				graphDataSource.shutdown();
			} catch (Exception e) {
				logger.error("Error while shutting down database", e);
				instance = currentDataAccess;
			}
		}
	}
	
	public static void registerShutdownHook(final GraphDataSource graphDb) {
		// Registers a shutdown hook for the Neo4j instance so that it
		// shuts down nicely when the VM exits (even if you "Ctrl-C" the
		// running example before it's completed)
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				graphDb.shutdown();
			}
		});
	}
	
	public Object getRawVertex(Vertex vertex) {
		return graphDataSource.getRawVertex(vertex);
	}

	public Object getRawEdge(Edge edge) {
		return graphDataSource.getRawEdge(edge);
	}

	public void saveNode(Vertex vertex, Map<String, Object> properties) {
		graphDataSource.saveNode(vertex, properties);
	}

	public Edge createEdge(Vertex outVertex, Vertex inVertex, String label) {
		return graphDataSource.createEdge(outVertex, inVertex, label);
	}

	public void addConnectionToReferenceNode(Vertex vertex, RelationshipType relationType) {
		graphDataSource.addConnectionToReferenceNode(vertex, relationType);
	}

	public void addToNodeIndex(String indexName, Vertex node, String indexParameter, Object indexValue) {
		graphDataSource.addToNodeIndex(indexName, node, indexParameter, indexValue);
	}

	public void addToNodeIndex(String indexName, Vertex node, String indexParameter, Object indexValue,
			Map<String, String> indexProperties) {
		graphDataSource.addToNodeIndex(indexName, node, indexParameter, indexValue, indexProperties);
	}

	public void addToRelationIndex(String indexName, Edge relationShip, String indexParameter, Object indexValue) {
		graphDataSource.addToRelationIndex(indexName, relationShip, indexParameter, indexValue);
	}

	public void addToRelationIndex(String indexName, Edge relationShip, String indexParameter, Object indexValue,
			Map<String, String> indexProperties) {
		graphDataSource.addToRelationIndex(indexName, relationShip, indexParameter, indexValue, indexProperties);
	}

	public Vertex findOneVertex(String indexName, String indexParameter, Object indexValue)
			throws NaradDataAccessException {
		return graphDataSource.findOneVertex(indexName, indexParameter, indexValue);
	}

	public Vertex[] findVertices(String indexName, String indexParameter, Object indexValue) {
		return graphDataSource.findVertices(indexName, indexParameter, indexValue);
	}

	public Vertex createNode(Map<String, Object> properties) {
		return graphDataSource.createNode(properties);
	}

	public Vertex createNode() {
		return graphDataSource.createNode();
	}

	public Vertex getNodeById(Object nodeId) {
		return graphDataSource.getNodeById(nodeId);
	}

	public Edge getRelationshipById(Object relationshipId) {
		return graphDataSource.getRelationshipById(relationshipId);
	}

	public void beginTransaction() {
		graphDataSource.beginTransaction();
	}
	
	public void completeTransaction(Conclusion conclusion) {
		graphDataSource.completeTransaction(conclusion);
	}

	public Vertex getReferenceNode() {
		return graphDataSource.getReferenceNode();
	}

	public Edge findOneRelationship(String indexName, String indexParameter, Object indexValue)
			throws NaradDataAccessException {
		return graphDataSource.findOneRelationship(indexName, indexParameter, indexValue);
	}

	public Edge[] findRelationship(String indexName, String indexParameter, Object indexValue,
			Map<String, String> paramMap) {
		return graphDataSource.findRelationship(indexName, indexParameter, indexValue, paramMap);
	}

	public List<Map<String, Object>> executeQuery(String cypherQuery) {
		return graphDataSource.executeQuery(cypherQuery);
	}

	public List<Vertex> shortestPath(Vertex startNode, Vertex endNode, RelationshipType relType, Direction direction,
			int maxDepth) {
		return graphDataSource.shortestPath(startNode, endNode, relType, direction, maxDepth);
	}
}
