package com.narad.worker;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.narad.dataaccess.DataAccessConstants;
import com.narad.dataaccess.NodeDao;
import com.narad.dataaccess.dao.DaoConstants;
import com.narad.dataaccess.dao.PersonDao;
import com.narad.exception.ExceptionErrorCodes;
import com.narad.exception.NaradException;
import com.narad.exception.NaradWorkerException;
import com.tinkerpop.blueprints.Vertex;

public class UpdateNodeWorker implements INaradWorker {

	private static final Logger logger = LoggerFactory.getLogger(UpdateNodeWorker.class);

	@Override
	public String getName() {
		return "UpdateNodeWorker";
	}

	public void updateNodeWithEmail(String emailId, List<Map<String, Object>> profileList) throws NaradException {
		FindNodeWorker worker = new FindNodeWorker();
		NodeDao nodeDao = worker.findNodeByEmail(emailId);
		updateNodeWithEmail(emailId, nodeDao, profileList);
	}

	// Currently creating for only 1 email
	public void updateNodeWithEmail(String emailId, NodeDao nodeDao, List<Map<String, Object>> profileList)
			throws NaradException {
		for (Map<String, Object> profile : profileList) {
			String network = (String) profile.get(DataAccessConstants.NETWORK);
			if (network == null) {
				logger.info("Cannot update node null network profile. Email: {}", emailId);
				throw new NaradWorkerException("Cannot update node with emailId: " + emailId, null,
						ExceptionErrorCodes.DATA_ACCESS_INCOMPLETE_NETWORK_INFORMATION, getName());
			}
			nodeDao.addProfile(emailId, network, profile);
		}

		logger.debug("Updating node for emailId: {} ", emailId);
		Vertex node = nodeDao.getNode();
		Object properties = nodeDao.getProperties();
		node.setProperty(DataAccessConstants.PROFILES, properties);

		logger.debug("Updated node for emailId: {} ", emailId);
	}

	public void updatePerson(PersonDao personDao, Map<String, Object> properties, String source) throws NaradException {
		//TODO - check API to provide for person
		// TODO - check how properties are reset in update
		List<String> emailIds = personDao.getEmailIds();

		Object object = properties.get(DaoConstants.EMAIL_IDS);
		if (object == null || !(object instanceof List)) {
			logger.info("Cannot update person as email ids not not present or not strings. Object: {}", object);
			throw new NaradWorkerException("Cannot update person. ", null,
					ExceptionErrorCodes.DATA_ACCESS_INCOMPLETE_EMAIL_INFORMATION, getName());
		}
		List<String> newEmailIds = (List<String>) object; 	
		for (String newEmailId : newEmailIds) {
			if (emailIds.contains(newEmailId)) {
				// no problem
			} else {
				PersonDao findPersonByEmail = new FindNodeWorker().findPersonByEmail(newEmailId);
				if (findPersonByEmail != null) {
					logger.info("Cannot update person as email already exists in another person. Email: {}", newEmailId);
					throw new NaradWorkerException("Cannot update node with emailId: " + newEmailId, null,
							ExceptionErrorCodes.DATA_ACCESS_INCOMPLETE_EMAIL_INFORMATION, getName());
				}
			}
		}
		
		logger.debug("Updating person");
		//logger.debug("Updating person for emailId: {} ", emailIds.get(0));

		personDao.updatePersonFromMap(properties);
		Vertex vertex = personDao.getVertex();
		Map<String, Object> personAsMap = personDao.getPersonAsFlatMap();
		Set<String> propertyKeys = vertex.getPropertyKeys();
		for (Map.Entry<String, Object> entry : personAsMap.entrySet()) {
			String key = entry.getKey();
			vertex.setProperty(key, entry.getValue());
			propertyKeys.remove(key);
		}
		for (String key : propertyKeys) {// Deleted keys
			vertex.removeProperty(key);
		}
		
		//TODO - check how to delete unused indices
		CreateNodeWorker.addPersonIndexes(vertex, personDao);
		logger.debug("Updated person");
		//logger.debug("Updated person for emailId: {} ", emailIds.get(0));
	}
}
