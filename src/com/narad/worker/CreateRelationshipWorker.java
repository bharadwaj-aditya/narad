package com.narad.worker;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.narad.dataaccess.DataAccess;
import com.narad.dataaccess.DataAccessConstants;
import com.narad.dataaccess.NaradRelationTypes;
import com.narad.dataaccess.NodeDao;
import com.narad.dataaccess.RelationshipDao;
import com.narad.dataaccess.dao.DaoConstants;
import com.narad.dataaccess.dao.PersonDao;
import com.narad.dataaccess.dao.PersonRelationDao;
import com.narad.exception.ExceptionErrorCodes;
import com.narad.exception.NaradException;
import com.narad.exception.NaradWorkerException;
import com.tinkerpop.blueprints.Edge;

public class CreateRelationshipWorker implements INaradWorker {
	private static final Logger logger = LoggerFactory.getLogger(CreateRelationshipWorker.class);

	@Override
	public String getName() {
		return "CreateRelationshipWorker";
	}

	// Assuming all nodes have unique emails
	public void createRelationship(String fromEmailId, String toEmailId, String network,
			Map<String, Object> fromProperties, Map<String, Object> toProperties) throws NaradException {

		// Check for already existing relationship
		FindNodeWorker findNodeWorker = new FindNodeWorker();
		NodeDao node1 = findNodeWorker.findNodeByEmail(fromEmailId);

		if (node1 == null) {
			logger.info("Cannot create relationship as a node with fromEmail: {} does not exist. ", fromEmailId);
			throw new NaradWorkerException("Cannot add relationship as node with fromEmail: " + fromEmailId
					+ " does not exist.", null, ExceptionErrorCodes.WORKER_NODE_NOT_FOUND, getName());
		}
		NodeDao node2 = findNodeWorker.findNodeByEmail(toEmailId);
		if (node2 == null) {
			logger.info("Cannot create relationship as a node with toEmail: {} does not exist. ", toEmailId);
			throw new NaradWorkerException("Cannot add relationship as node with toEmail: " + toEmailId
					+ " does not exist.", null, ExceptionErrorCodes.WORKER_NODE_NOT_FOUND, getName());
		}

		logger.debug("Adding relationship from email: {} to email: {} ", fromEmailId, toEmailId);

		RelationshipDao relationshipDao1 = new RelationshipDao(node1, node2, NaradRelationTypes.FRIEND.name());
		relationshipDao1.addProfile(fromEmailId, toEmailId, network, toProperties);
		RelationshipDao relationshipDao2 = new RelationshipDao(node2, node1, NaradRelationTypes.FRIEND.name());
		relationshipDao2.addProfile(toEmailId, fromEmailId, network, fromProperties);

		// creating outward relationship - fromMail to toMail
		createSingleDirectionRelationship(relationshipDao1);
		// creating inward relationship - toMail to FromMail
		createSingleDirectionRelationship(relationshipDao2);

		logger.debug("Added relationship from email: {} to email: {} ", fromEmailId, toEmailId);
	}

	private void createSingleDirectionRelationship(RelationshipDao relationshipDao) {

		// relationshipDao.createRelationShip();
		Object properties = relationshipDao.getProperties();
		Edge edge = DataAccess.getInstance().createEdge(relationshipDao.getFromNode().getNode(),
				relationshipDao.getToNode().getNode(), NaradRelationTypes.FRIEND.name());
		edge.setProperty(DataAccessConstants.PROFILES, properties);

		String[] emailRelations = relationshipDao.getEmailRelations();
		for (int i = 0; i < emailRelations.length; i++) {
			String relationStr = emailRelations[i];
			DataAccess.getInstance().addToRelationIndex(DataAccessConstants.RELATION_INDEX, edge,
					DataAccessConstants.EMAIL_CONCAT, relationStr);
		}
	}
	
	public void createRelationship(PersonDao fromPersonDao, PersonDao toPersonDao,
			Map<String, Object> fromProperties, Map<String, Object> returnProperties) throws NaradException {

		/*logger.debug("Adding relationship from person: {} to person: {} ", fromPersonDao.getEmailIds().get(0),
				toPersonDao.getEmailIds().get(0));*/
		logger.debug("Adding relationship");

		// creating outward relationship - fromMail to toMail
		PersonRelationDao relationDao = new PersonRelationDao(fromProperties);
		Edge edge = DataAccess.getInstance().createEdge(fromPersonDao.getVertex(),
				toPersonDao.getVertex(), DaoConstants.RELATION_TYPE_FRIEND);
		
		// creating inward relationship - toMail to FromMail
		PersonRelationDao returnRelationDao = new PersonRelationDao(returnProperties);
		Edge returnEdge = DataAccess.getInstance().createEdge(toPersonDao.getVertex(),
				fromPersonDao.getVertex(), DaoConstants.RELATION_TYPE_FRIEND);
		
		//Set return edge value
		relationDao.setReturnEdgeId(returnEdge.getId());
		returnRelationDao.setReturnEdgeId(edge.getId());
		
		Map<String, Object> relationDaoAsMap = relationDao.getRelationDaoAsFlatMap();
		for (Map.Entry<String, Object> entry : relationDaoAsMap.entrySet()) {
			edge.setProperty(entry.getKey(), entry.getValue());
		}
		
		Map<String, Object> relationDaoAsMap2 = returnRelationDao.getRelationDaoAsFlatMap();
		for (Map.Entry<String, Object> entry : relationDaoAsMap2.entrySet()) {
			returnEdge.setProperty(entry.getKey(), entry.getValue());
		}

//		logger.debug("Added relationship from person: {} to person: {} ", fromPersonDao.getEmailIds().get(0),
//				toPersonDao.getEmailIds().get(0));
		logger.debug("Added relationship");
	}

}
