package com.narad.worker;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.narad.dataaccess.DataAccess;
import com.narad.dataaccess.DataAccessConstants;
import com.narad.dataaccess.RelationshipDao;
import com.narad.dataaccess.RelationshipProfile;
import com.narad.dataaccess.dao.PersonDao;
import com.narad.dataaccess.dao.PersonRelationDao;
import com.narad.exception.NaradException;
import com.tinkerpop.blueprints.Edge;

public class UpdateRelationshipWorker implements INaradWorker {
	private static final Logger logger = LoggerFactory.getLogger(UpdateRelationshipWorker.class);

	@Override
	public String getName() {
		return "UpdateRelationshipWorker";
	}

	public void updateRelationship(String fromEmailId, String toEmailId, String network,
			Map<String, Object> fromProperties, Map<String, Object> toProperties) throws NaradException {
		FindRelationWorker findRelationWorker = new FindRelationWorker();
		RelationshipDao relationshipDao = findRelationWorker.findRelationByEmail(fromEmailId, toEmailId);
		updateRelationship(fromEmailId, toEmailId, relationshipDao, network, fromProperties, toProperties);
	}

	// Assuming all nodes have unique emails
	public void updateRelationship(String fromEmailId, String toEmailId, RelationshipDao relationshipDao,
			String network, Map<String, Object> fromProperties, Map<String, Object> toProperties) throws NaradException {
		logger.debug("Updating relationship between: {} and {} ", fromEmailId, toEmailId);

		updateProfile(fromEmailId, toEmailId, relationshipDao, network, fromProperties);
		updateProfile(toEmailId, fromEmailId, null, network, toProperties);

		logger.debug("Updated relationship between: {} and {} ", fromEmailId, toEmailId);
	}

	private void updateProfile(String fromEmailId, String toEmailId, RelationshipDao relationshipDao, String network,
			Map<String, Object> fromProperties) throws NaradException {
		if (relationshipDao == null) {
			FindRelationWorker findRelationWorker = new FindRelationWorker();
			relationshipDao = findRelationWorker.findRelationByEmail(fromEmailId, toEmailId);
		}
		List<RelationshipProfile> profilesForEmail = relationshipDao.getProfilesForEmail(fromEmailId, toEmailId);
		boolean networkUpdated = false;
		for (RelationshipProfile profile : profilesForEmail) {
			if (profile.getNetwork().equals(network)) {
				// update
				profile.putAll(fromProperties);
				networkUpdated = true;
				break;
			}
		}
		if (!networkUpdated) {
			// new network
			relationshipDao.addProfile(fromEmailId, toEmailId, network, fromProperties);
		}
		Object properties = relationshipDao.getProperties();
		relationshipDao.getRelationship().setProperty(DataAccessConstants.PROFILES, properties);
	}
	
	//This is based on person. Need to disambiguate between the 2 and delete unused method
	public void updateRelationship(PersonDao fromPerson, PersonDao toPerson, PersonRelationDao relation,
			Map<String, Object> fromProperties, Map<String, Object> toProperties) throws NaradException {
		logger.debug("Updating relationship between: {} and {} ", fromPerson, toPerson);
		
		//TODO - index relations!!
		relation.updateFromMap(fromProperties);
		
		PersonRelationDao returnRelation = null;
		Object returnEdgeId = relation.getReturnEdgeId();
		if (returnEdgeId != null) {
			Edge relationshipById = DataAccess.getInstance().getRelationshipById(returnEdgeId);
			if (relationshipById != null) {
				returnRelation = new PersonRelationDao(relationshipById);
			}
		}
		
		if (returnRelation == null) {
			FindRelationWorker worker = new FindRelationWorker();
			returnRelation = worker.findRelationBetweenPeople(toPerson, fromPerson);
		}
		if (returnRelation == null) {
			// THROW EXCEPTION
		} else {
			returnRelation.updateFromMap(toProperties);
		}

		logger.debug("Updated relationship between: {} and {} ", fromPerson, toPerson);
	}


}
