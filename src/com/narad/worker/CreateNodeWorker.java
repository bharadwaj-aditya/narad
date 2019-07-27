package com.narad.worker;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.narad.dataaccess.DataAccess;
import com.narad.dataaccess.DataAccessConstants;
import com.narad.dataaccess.NaradRelationTypes;
import com.narad.dataaccess.NodeDao;
import com.narad.dataaccess.dao.InstitutionDaoInfo;
import com.narad.dataaccess.dao.NetworkDaoInfo;
import com.narad.dataaccess.dao.PersonDao;
import com.narad.exception.ExceptionErrorCodes;
import com.narad.exception.NaradException;
import com.narad.exception.NaradWorkerException;
import com.tinkerpop.blueprints.Vertex;

public class CreateNodeWorker implements INaradWorker {

	private static final Logger logger = LoggerFactory.getLogger(CreateNodeWorker.class);

	@Override
	public String getName() {
		return "CreateNodeWorker";
	}

	// Currently creating for only 1 email
	public void createNodeWithSingleEmail(String emailId, List<Map<String, Object>> profileList) throws NaradException {
		NodeDao nodeDao = new NodeDao();

		for (Map<String, Object> profile : profileList) {
			String network = (String) profile.get(DataAccessConstants.NETWORK);
			if (network == null) {
				logger.info("Cannot add node with null network profile. Email: {}", emailId);
				throw new NaradWorkerException("Cannot add node with emailId: " + emailId, null,
						ExceptionErrorCodes.DATA_ACCESS_INCOMPLETE_NETWORK_INFORMATION, getName());
			}
			nodeDao.addProfile(emailId, network, profile);
		}

		logger.debug("Adding node for emailId: {} ", emailId);
		Vertex createNode = DataAccess.getInstance().createNode();
		Object properties = nodeDao.getProperties();
		createNode.setProperty(DataAccessConstants.PROFILES, properties);

		DataAccess.getInstance().addToNodeIndex(DataAccessConstants.NODE_INDEX, createNode,
				DataAccessConstants.NODE_INDEX_PARAM_EMAIL, emailId);

		// TODO - check if link to reference node required
		DataAccess.getInstance().addConnectionToReferenceNode(createNode, NaradRelationTypes.PERSON_REFERENCE);

		logger.debug("Added node for emailId: {} ", emailId);
	}

	public PersonDao createPerson(Map<String, Object> properties) throws NaradException {
		PersonDao personDao = new PersonDao();
		personDao.populate(properties);
		List<String> emailIds = personDao.getEmailIds();
		if (emailIds.isEmpty()) {
			String fullName = personDao.getFullName();
			String firstName = personDao.getFirstName();
			String lastName = personDao.getLastName();
			//TODO - check other parameters like phones, websites, network url/ids etc
			if ((fullName == null || fullName.isEmpty()) && (firstName == null || firstName.isEmpty())
					&& (lastName == null || lastName.isEmpty())) {
				logger.info("Cannot add node with as email ids are not available. ");
				throw new NaradWorkerException("Cannot add node as email ids are not available. ", null,
						ExceptionErrorCodes.DATA_ACCESS_INCOMPLETE_EMAIL_INFORMATION, getName());
			}
		}

		List<NetworkDaoInfo> socialNetworks = personDao.getNetworks();
		if (socialNetworks.isEmpty()) {
			logger.info("Cannot add node with as networks are not available. ");
			throw new NaradWorkerException("Cannot add node as networks are not available. ", null,
					ExceptionErrorCodes.DATA_ACCESS_INCOMPLETE_NETWORK_INFORMATION, getName());
		}
		
		// TODO - fix this logger
		//logger.debug("Adding node for emailIds: {} ", emailIds.get(0));
		logger.debug("Adding node");
		Vertex vertex = DataAccess.getInstance().createNode();
		Map<String, Object> personAsMap = personDao.getPersonAsFlatMap();
		for (Map.Entry<String, Object> entry : personAsMap.entrySet()) {
			vertex.setProperty(entry.getKey(), entry.getValue());
		}

		addPersonIndexes(vertex, personDao);
		personDao.setVertex(vertex);

		logger.debug("Added node");
		//logger.debug("Added node for emailId: {} ", emailIds.get(0));
		return personDao;
	}
	
	public static void addPersonIndexes(Vertex vertex, PersonDao personDao) {
		List<String> emailIds = personDao.getEmailIds();
		for (String emailId : emailIds) {
			DataAccess.getInstance().addToNodeIndex(DataAccessConstants.PERSON_INDEX, vertex,
					DataAccessConstants.PERSON_INDEX_PARAM_EMAIL, emailId);
		}
		
		List<String> phones = personDao.getPhones();
		for (String phone: phones) {
			DataAccess.getInstance().addToNodeIndex(DataAccessConstants.PERSON_INDEX, vertex,
					DataAccessConstants.PERSON_INDEX_PARAM_PHONE, phone);
		}
		
		List<String> websites = personDao.getWebsites();
		for (String website : websites) {
			DataAccess.getInstance().addToNodeIndex(DataAccessConstants.PERSON_INDEX, vertex,
					DataAccessConstants.PERSON_INDEX_PARAM_WEBSITE, website);
		}
		
		DataAccess.getInstance().addToNodeIndex(DataAccessConstants.PERSON_INDEX, vertex,
				DataAccessConstants.PERSON_INDEX_PARAM_NAME, personDao.getFullName());
		
		/*List<LocationDaoInfo> locations = personDao.getLocations();
		for (LocationDaoInfo location : locations) {
			
		}
		//Similarly location checkins
		*/
		
		List<InstitutionDaoInfo> education = personDao.getEducation();
		for (InstitutionDaoInfo institute : education) {
			DataAccess.getInstance().addToNodeIndex(DataAccessConstants.PERSON_INDEX, vertex,
					DataAccessConstants.PERSON_INDEX_PARAM_EDUCATION, institute.getInstitutionName());
		}
		
		List<InstitutionDaoInfo> jobs = personDao.getJobs();
		for (InstitutionDaoInfo job : education) {
			DataAccess.getInstance().addToNodeIndex(DataAccessConstants.PERSON_INDEX, vertex,
					DataAccessConstants.PERSON_INDEX_PARAM_JOBS, job.getInstitutionName());
		}
		
		List<String> skills = personDao.getSkills();
		for (String skill : skills) {
			DataAccess.getInstance().addToNodeIndex(DataAccessConstants.PERSON_INDEX, vertex,
					DataAccessConstants.PERSON_INDEX_PARAM_SKILLS, skill);
		}
		
		DataAccess.getInstance().addToNodeIndex(DataAccessConstants.PERSON_INDEX_PARAM_AGE, vertex,
				DataAccessConstants.PERSON_INDEX_PARAM_AGE, personDao.getAge());
	}
}
