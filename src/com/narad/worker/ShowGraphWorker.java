package com.narad.worker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.narad.dataaccess.DataAccess;
import com.narad.dataaccess.NaradRelationTypes;
import com.tinkerpop.blueprints.Vertex;

public class ShowGraphWorker implements INaradWorker {
	private static final Logger logger = LoggerFactory.getLogger(ShowGraphWorker.class);

	@Override
	public String getName() {
		return "ShowGraphWorker";
	}

	/**
	 * Find the first matching node for given email. Return null if not found.
	 * 
	 * @param email
	 * @return
	 */
	public List<Map<String, Object>> showGraph(int depth) {
		logger.info("Starting graph traversal in show graph");
		Vertex referenceVertex = DataAccess.getInstance().getReferenceNode();
		// TODO - remove use of neo4j.Node here
		Node referenceNode = (Node) DataAccess.getInstance().getRawVertex(referenceVertex);
		Traverser traverser = referenceNode.traverse(Order.BREADTH_FIRST, StopEvaluator.END_OF_GRAPH,
				ReturnableEvaluator.ALL_BUT_START_NODE, NaradRelationTypes.PERSON_REFERENCE, Direction.OUTGOING);

		List<Map<String, Object>> allNodesList = new ArrayList<Map<String, Object>>();
		for (Node node : traverser) {
			int currDepth = traverser.currentPosition().depth();
			if (depth > 0 && currDepth > depth) {
				break;
			}
			Map<String, Object> nodeMap = new HashMap<String, Object>();
			nodeMap.put("id", node.getId());
			nodeMap.put("depth", currDepth);
			Iterable<Relationship> relationships = node.getRelationships(Direction.OUTGOING);
			TreeSet<Long> relationSet = new TreeSet<Long>();
			for (Relationship relationship : relationships) {
				relationSet.add(relationship.getEndNode().getId());
			}
			nodeMap.put("relations", relationSet);
			allNodesList.add(nodeMap);
		}
		logger.info("Show graph traversal complete");
		return allNodesList;
	}

	public Object giveGraphStats() {
		Map<String, Object> graphStats = new HashMap<String, Object>();
		List<Map<String, Object>> executeQuery = DataAccess.getInstance().executeQuery(
				"START n=node(*) MATCH (n)-->(x) RETURN count(distinct(x))");
		graphStats.put("nodes", executeQuery);

		List<Map<String, Object>> executeQuery2 = DataAccess.getInstance().executeQuery(
				"START n=node(*) MATCH (n)-[r]->() RETURN type(r), count(*)");
		graphStats.put("vertices", executeQuery2);
		return graphStats;
	}

}
