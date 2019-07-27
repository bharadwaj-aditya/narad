package com.narad.worker;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.narad.dataaccess.DataAccess;
import com.narad.dataaccess.DataAccessConstants;
import com.narad.dataaccess.RelationshipDao;
import com.narad.dataaccess.dao.DaoConstants;
import com.narad.dataaccess.dao.PersonDao;
import com.narad.dataaccess.dao.PersonRelationDao;
import com.narad.exception.NaradException;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

public class FindRelationWorker implements INaradWorker {
	private static final Logger logger = LoggerFactory.getLogger(FindRelationWorker.class);

	@Override
	public String getName() {
		return "FindRelationWorker";
	}

	/**
	 * Find the matching relation for given emails. Return null if not found.
	 * 
	 * @param fromEmailId
	 * @param toEmailId
	 * @return
	 */
	public RelationshipDao findRelationByEmail(String fromEmailId, String toEmailId) throws NaradException {
		Edge find = DataAccess.getInstance().findOneRelationship(DataAccessConstants.RELATION_INDEX,
				DataAccessConstants.EMAIL_CONCAT, RelationshipDao.buildEmailRelationStr(fromEmailId, toEmailId));
		if (find != null) {
			logger.debug("Relation found between emailIds: {} and {}", fromEmailId, toEmailId);
			RelationshipDao relationshipDao = new RelationshipDao(find);
			return relationshipDao;
		} else {
			logger.debug("No relation found between emailIds: {} and {}", fromEmailId, toEmailId);
			return null;
		}
	}

	/**
	 * Find the matching relation for given emails. Return null if not found.
	 * 
	 * @param fromEmailId
	 * @param toEmailId
	 * @return
	 */
	public PersonRelationDao findRelationBetweenPeople(PersonDao fromPersonDao, PersonDao toPersonDao) throws NaradException {

		Iterable<Edge> edges = fromPersonDao.getVertex().getEdges(Direction.OUT,
				new String[] { DaoConstants.RELATION_TYPE_FRIEND });
		Iterator<Edge> iterator = edges.iterator();

		while (iterator.hasNext()) {
			Edge edge = iterator.next();
			Vertex vertex = edge.getVertex(Direction.IN);
			if (vertex.equals(toPersonDao.getVertex())) {
				PersonRelationDao relationDao = new PersonRelationDao(edge);
				logger.debug("Relation found between emailIds: {} and {}", fromPersonDao, toPersonDao);
				return relationDao;
			}
		}
		logger.debug("No relation found between emailIds: {} and {}", fromPersonDao, toPersonDao);
		return null;
	}
}
