package com.narad.worker;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.narad.dataaccess.DataAccess;
import com.narad.dataaccess.DataAccessConstants;
import com.narad.dataaccess.NodeDao;
import com.narad.dataaccess.dao.DaoConstants;
import com.narad.dataaccess.dao.PersonDao;
import com.narad.exception.NaradException;
import com.narad.util.NaradMapUtils;
import com.tinkerpop.blueprints.Vertex;

public class FindNodeWorker implements INaradWorker {
	private static final Logger logger = LoggerFactory.getLogger(FindNodeWorker.class);

	@Override
	public String getName() {
		return "FindNodeWorker";
	}

	/**
	 * Find the first matching node for given email. Return null if not found.
	 * 
	 * @param emailId
	 * @return
	 */
	public NodeDao findNodeByEmail(String emailId) throws NaradException {
		Vertex find = DataAccess.getInstance().findOneVertex(DataAccessConstants.NODE_INDEX,
				DataAccessConstants.NODE_INDEX_PARAM_EMAIL, emailId);
		if (find != null) {
			NodeDao nodeDao = new NodeDao(find);
			return nodeDao;
		} else {
			logger.info("Could not find node with email: {}", emailId);
			return null;
		}
	}

	/**
	 * Find the first matching person for given email. Return null if not found.
	 * 
	 * @param emailId
	 * @return
	 */
	public PersonDao findPersonByEmail(String emailId) throws NaradException {
		if (emailId == null) {
			logger.info("Cannot find node with null emailId");
			return null;
		}

		Vertex find = DataAccess.getInstance().findOneVertex(DataAccessConstants.PERSON_INDEX,
				DataAccessConstants.PERSON_INDEX_PARAM_EMAIL, emailId);
		if (find != null) {
			PersonDao nodeDao = new PersonDao(find);
			return nodeDao;
		} else {
			logger.info("Could not find person with email: {}", emailId);
			return null;
		}
	}

	/**
	 * Search for user based on provided email.<br/>
	 * If email not found, search based on Full Name<br/>
	 * If full name is not found, search based on first name and last name.<br/>
	 * If first name not found, search based on last name.<br/>
	 * If last name not found, search based on first name.<br/>
	 * 
	 * @param properties
	 * @return
	 * @throws NaradException
	 */
	public PersonDao findPersonByProperties(Map<String, Object> properties) throws NaradException {
		// TODO - change data access to allow multi-value queries with OR serperators
		List<String> emailIds = (List) NaradMapUtils.getPropertyStringListFromMap(properties, DaoConstants.EMAIL_IDS);
		if (emailIds != null) {
			for (String emailId : emailIds) {
				PersonDao findPersonByEmail = findPersonByEmail(emailId);
				if (findPersonByEmail != null) {
					return findPersonByEmail;
				}
			}
		}
		// TODO - return search queries with score
		String fullName = NaradMapUtils.getCheckedValueFromMap(properties, DaoConstants.FULL_NAME, String.class);
		String firstName = NaradMapUtils.getCheckedValueFromMap(properties, DaoConstants.FIRST_NAME, String.class);
		String lastName = NaradMapUtils.getCheckedValueFromMap(properties, DaoConstants.LAST_NAME, String.class);
		Vertex vertex = null;
		if (fullName != null && !fullName.isEmpty()) {
			vertex = DataAccess.getInstance().findOneVertex(DataAccessConstants.PERSON_INDEX,
					DataAccessConstants.PERSON_INDEX_PARAM_NAME, "\"" + fullName + "\"");
		}
		// TODO - check this based on mode - greedy mode return first name matches and last name matches

		if (vertex == null && firstName != null && !firstName.isEmpty() && lastName != null && !lastName.isEmpty()) {
			Vertex[] findVertices = DataAccess.getInstance().findVertices(DataAccessConstants.PERSON_INDEX,
					DataAccessConstants.PERSON_INDEX_PARAM_NAME, "\"" + firstName + "\"");
			for (int i = 0; i < findVertices.length; i++) {
				Vertex vertex2 = findVertices[i];
				Object property = vertex2.getProperty(DaoConstants.LAST_NAME);
				if (lastName.equals(property)) {
					vertex = vertex2;
					break;
				}
				// TODO If match not found give probable matches based on maximum string match
			}
			/* vertex = DataAccess.getInstance().findOneVertex(DataAccessConstants.PERSON_INDEX_PARAM_NAME,
			 * DataAccessConstants.PERSON_INDEX_PARAM_NAME, "\"" + lastName + "\""); */
		} else if (vertex == null && firstName != null && !firstName.isEmpty()) {
			// user with no last name
			Vertex[] findVertices = DataAccess.getInstance().findVertices(DataAccessConstants.PERSON_INDEX,
					DataAccessConstants.PERSON_INDEX_PARAM_NAME, "\"" + firstName + "\"");
			for (int i = 0; i < findVertices.length; i++) {
				Vertex vertex2 = findVertices[i];
				Object property = vertex2.getProperty(DaoConstants.LAST_NAME);
				if (property == null) {
					vertex = vertex2;
					break;
				}
				// TODO If match not found give probable matches based on maximum string match
			}
		} else if (vertex == null && lastName != null && !lastName.isEmpty()) {
			// user with no first name
			Vertex[] findVertices = DataAccess.getInstance().findVertices(DataAccessConstants.PERSON_INDEX,
					DataAccessConstants.PERSON_INDEX_PARAM_NAME, "\"" + lastName + "\"");
			for (int i = 0; i < findVertices.length; i++) {
				Vertex vertex2 = findVertices[i];
				Object property = vertex2.getProperty(DaoConstants.FIRST_NAME);
				if (property == null) {
					vertex = vertex2;
					break;
				}
				// TODO If match not found give probable matches based on maximum string match
			}
		}
		if (vertex != null) {
			PersonDao personDao = new PersonDao(vertex);
			return personDao;
		}
		// TODO - check how to do with query
		// List<Map<String, Object>> executeQuery = DataAccess.getInstance().executeQuery(
		// DaoConstants.FIRST_NAME + ":" + firstName + " AND " + DaoConstants.LAST_NAME + ":" + lastName);
		// if (!executeQuery.isEmpty()) {
		// PersonDao personDao = new PersonDao();
		// personDao.populate(executeQuery.get(0));
		// return personDao;
		// }
		logger.info("Could not find matching node based on any property");
		return null;
	}

	/**
	 * Find all matching nodes for email.
	 * 
	 * @param emailId
	 * @return
	 */
	public NodeDao[] findAllNodesByEmail(String emailId) {
		Vertex[] find = DataAccess.getInstance().findVertices(DataAccessConstants.NODE_INDEX,
				DataAccessConstants.NODE_INDEX_PARAM_EMAIL, emailId);
		NodeDao[] foundNodes = new NodeDao[find.length];
		for (int i = 0; i < find.length; i++) {
			foundNodes[i] = new NodeDao(find[i]);
		}
		return foundNodes;
	}

	/**
	 * Search based on properties and return all matching nodes
	 * 
	 * @param properties
	 * @return
	 */
	public NodeDao[] findAllNodes(Map<String, Object> properties) {
		// TODO - implement this!
		return new NodeDao[0];
	}

}
