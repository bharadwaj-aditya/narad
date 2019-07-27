package com.narad.dataaccess;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.neo4j.cypher.ExecutionEngine;
import org.neo4j.cypher.ExecutionResult;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.index.lucene.QueryContext;
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.impl.traversal.TraversalPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.Option;

import com.narad.configuration.ConfigProperties;
import com.narad.exception.NaradDataAccessException;
import com.tinkerpop.blueprints.CloseableIterable;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Index;
import com.tinkerpop.blueprints.Parameter;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.TransactionalGraph.Conclusion;
import com.tinkerpop.blueprints.impls.neo4j.Neo4jEdge;
import com.tinkerpop.blueprints.impls.neo4j.Neo4jGraph;
import com.tinkerpop.blueprints.impls.neo4j.Neo4jVertex;

public class Neo4jDataAccess implements GraphDataSource {

	private static final Logger logger = LoggerFactory.getLogger(Neo4jDataAccess.class);
	private static final String NEO4J_DB_SERVICE = "neo4j";
	private static final String GRAPH_DB_URL = "neo4JGraphDbUrl";
	private String dbUrl;
	private Neo4jGraph graph;

	public Neo4jDataAccess() {
		super();
		init();
	}

	private void init() {
		dbUrl = (String) ConfigProperties.getInstance().getProperty(GRAPH_DB_URL);
		graph = new Neo4jGraph(dbUrl);
	}
	
	public String getName() {
		return NEO4J_DB_SERVICE;
	}
	
	public void shutdown(){
		logger.info("Explicit shutdown called for Neodb at: {}", dbUrl);
		graph.shutdown();
		logger.info("Explicit shutdown complete for Neodb at: {}", dbUrl);
	}
	
	@Override
	public Object getRawVertex(Vertex vertex) {
		return ((Neo4jVertex)vertex).getRawVertex();
	}

	@Override
	public Object getRawEdge(Edge edge) {
		return ((Neo4jEdge)edge).getRawEdge();
	}
	
	@Override
	public Vertex createNode(Map<String, Object> properties) {
		Vertex vertex = graph.addVertex(null);
		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			vertex.setProperty(entry.getKey(), entry.getValue());
		}
		return vertex;
	}

	@Override
	public void saveNode(Vertex vertex, Map<String, Object> properties) {
		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			vertex.setProperty(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public Vertex createNode() {
		Vertex vertex = graph.addVertex(null);
		return vertex;
	}

	@Override
	public Edge createEdge(Vertex outVertex, Vertex inVertex, String label) {
		return graph.addEdge(null, outVertex, inVertex, label);
	}

	@Override
	public Vertex getNodeById(Object nodeId) {
		return graph.getVertex(nodeId);
	}

	@Override
	public Edge getRelationshipById(Object relationshipId) {
		return graph.getEdge(relationshipId);
	}

	@Override
	public void beginTransaction() {
		//Automatically started by Tinkerpop
	}
	
	@Override
	public void completeTransaction(Conclusion conclusion) {
		graph.stopTransaction(conclusion);
	}

	@Override
	public Vertex getReferenceNode() {
		Node referenceNode = graph.getRawGraph().getReferenceNode();
		Vertex vertex = new Neo4jVertex(referenceNode, graph);
		return vertex;
	}

	@Override
	public void addConnectionToReferenceNode(Vertex vertex, RelationshipType relationType) {
		// graphDbService.getReferenceNode().createRelationshipTo(node, relationType);
		Vertex referenceNode = getReferenceNode();
		graph.addEdge(null, vertex, referenceNode, relationType.name());
	}

	@Override
	public void addToNodeIndex(String indexName, Vertex node, String indexParameter, Object indexValue) {
		addToNodeIndex(indexName, node, indexParameter, indexValue, null);
	}

	@Override
	public void addToNodeIndex(String indexName, Vertex node, String indexParameter, Object indexValue,
			Map<String, String> indexProperties) {
		if (indexValue == null) {
			return;
		}
		com.tinkerpop.blueprints.Index<Vertex> vertexIndex = graph.getIndex(indexName, Vertex.class);
		vertexIndex.put(indexParameter, indexValue, node);
	}

	@Override
	public void addToRelationIndex(String indexName, Edge relationShip, String indexParameter, Object indexValue) {
		addToRelationIndex(indexName, relationShip, indexParameter, indexValue, null);
	}

	@Override
	public void addToRelationIndex(String indexName, Edge relationShip, String indexParameter, Object indexValue,
			Map<String, String> indexProperties) {
		if (indexValue == null) {
			return;
		}
		com.tinkerpop.blueprints.Index<Edge> edgeIndex = graph.getIndex(indexName, Edge.class);
		edgeIndex.put(indexParameter, indexValue, relationShip);
	}
	
	@Override
	public <T extends Element> void createIndex(String indexName, Class<T> indexClass, Parameter[] indexParameters) {
		Index<T> index = graph.getIndex(indexName, indexClass);
		if (index == null) {
			graph.createIndex(indexName, indexClass, indexParameters);
		} else {
			//TODO check if index properties are same
		}
	}

	@Override
	public Vertex findOneVertex(String indexName, String indexParameter, Object indexValue)
			throws NaradDataAccessException {
		return findOne(indexName, indexParameter, indexValue, Vertex.class);
	}

	@Override
	public Vertex[] findVertices(String indexName, String indexParameter, Object indexValue) {
		return find(indexName, indexParameter, indexValue, Vertex.class);
	}

	@Override
	public Edge findOneRelationship(String indexName, String indexParameter, Object indexValue)
			throws NaradDataAccessException {
		return findOne(indexName, indexParameter, indexValue, Edge.class);
	}

	@Override
	public Edge[] findRelationship(String indexName, String indexParameter, Object indexValue,
			Map<String, String> paramMap) {
		return find(indexName, indexParameter, indexValue, Edge.class);
	}

	private <T extends Element> T findOne(String indexName, String indexParameter, Object indexValue, Class<T> t) {
		com.tinkerpop.blueprints.Index<T> elementIndex = graph.getIndex(indexName, t);
		if (elementIndex == null) {
			elementIndex = graph.createIndex(indexName, t, new Parameter[0]);
		}
		CloseableIterable<T> queryResult = elementIndex.query(indexParameter, new QueryContext(indexValue).sortByScore().top(1));

		Iterator<T> iterator = queryResult.iterator();
		try {
			while (iterator.hasNext()) {
				T next = iterator.next();
				// throw exception if more than 1??
				return next;
			}
		} finally {
			queryResult.close();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private <T extends Element> T[] find(String indexName, String indexParameter, Object indexValue, Class<T> t) {
		com.tinkerpop.blueprints.Index<T> elementIndex = graph.getIndex(indexName, t);
		if (elementIndex == null) {
			elementIndex = graph.createIndex(indexName, t, new Parameter[0]);
		}
		CloseableIterable<T> queryResult = elementIndex.query(indexParameter, indexValue);

		Iterator<T> iterator = queryResult.iterator();
		List<T> matchingElements = new ArrayList<T>();
		try {
			while (iterator.hasNext()) {
				T next = iterator.next();
				matchingElements.add(next);
			}
		} finally {
			queryResult.close();
		}
		T[] matches = matchingElements.toArray((T[]) Array.newInstance(t, matchingElements.size()));
		return matches;
	}

	@Override
	public List<Map<String, Object>> executeQuery(String cypherQuery) {
		// TODO - cleanup impl
		ExecutionEngine executionEngine = new ExecutionEngine(graph.getRawGraph());
		ExecutionResult result = executionEngine.execute(cypherQuery);
		ArrayList<Map<String, Object>> queryResultList = new ArrayList<Map<String, Object>>();
		for (; result.hasNext();) {
			scala.collection.immutable.Map<String, Object> scalaRow = result.next();
			scala.collection.Iterator<String> iterator = scalaRow.keySet().iterator();

			Map<String, Object> row = new HashMap<String, Object>();
			for (; iterator.hasNext();) {
				String key = iterator.next();
				Option<Object> paramV = scalaRow.get(key);
				Object object = paramV != null ? paramV.get() : null;
				if (object != null) {
					if (object instanceof Node) {
						row.put(key, new Neo4jVertex((Node)object, graph));
					} else if (object instanceof Relationship) {
						row.put(key, new Neo4jEdge((Relationship)object, graph));
					} else if (object instanceof TraversalPath) {
						TraversalPath path = ((TraversalPath) object);
						Iterator<Node> nodes = path.nodes().iterator();
						ArrayList<Vertex> vertexList = new ArrayList<Vertex>();
						while(nodes.hasNext()) {
							vertexList.add(new Neo4jVertex((Node)nodes.next(), graph));
						}
						Iterator<Relationship> relations = path.relationships().iterator();
						ArrayList<Edge> edgeList = new ArrayList<Edge>();
						while(relations.hasNext()) {
							edgeList.add(new Neo4jEdge((Relationship)relations.next(), graph));
						}
						HashMap<String, Object> pathMap = new HashMap<String, Object>();
						pathMap.put("vertices", vertexList);
						pathMap.put("edges", edgeList);
						row.put(key, pathMap);
					}
				}
			}
			queryResultList.add(row);
		}
		return queryResultList;
	}

	@Override
	public List<Vertex> shortestPath(Vertex startNode, Vertex endNode, RelationshipType relType, Direction direction,
			int maxDepth) {
		PathFinder<Path> pathFinder = GraphAlgoFactory.shortestPath(Traversal.expanderForTypes(relType, direction),
				maxDepth);
		Path singlePath = pathFinder.findSinglePath(((Neo4jVertex) startNode).getRawVertex(),
				((Neo4jVertex) endNode).getRawVertex());
		if (singlePath == null) {
			return null;
		}
		List<Vertex> pathNodeList = new ArrayList<Vertex>(singlePath.length());
		for (Node node : singlePath.nodes()) {
			pathNodeList.add(new Neo4jVertex(node, graph));
		}
		return pathNodeList;
	}
}
