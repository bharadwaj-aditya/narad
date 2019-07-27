package com.narad.command;

import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.narad.command.CommandResult.CommandResultType;
import com.narad.dataaccess.RelationshipDao;
import com.narad.dataaccess.dao.PersonDao;
import com.narad.dataaccess.dao.PersonRelationDao;
import com.narad.exception.NaradException;
import com.narad.worker.CreateNodeWorker;
import com.narad.worker.CreateRelationshipWorker;
import com.narad.worker.FindNodeWorker;
import com.narad.worker.FindRelationWorker;
import com.narad.worker.TransactionWorker;
import com.narad.worker.UpdateRelationshipWorker;

public class AddRelationCommand {

	private static final Logger logger = LoggerFactory.getLogger(AddRelationCommand.class);
	private static final String COMMAND_NAME = "addRelation";

	public CommandResult addRelation(String fromEmailId, String toEmailId, Map<String, Object> properties) {
		CommandResult commandResult = new CommandResult(COMMAND_NAME);

		if (fromEmailId == null || fromEmailId.isEmpty()) {
			commandResult.setDescription("Cannot execute command with null fromEmailId");
			return commandResult;
		} else if (toEmailId == null || toEmailId.isEmpty()) {
			commandResult.setDescription("Cannot execute command with null toEmailId");
			return commandResult;
		}

		TransactionWorker tx = new TransactionWorker();
		try {
			FindRelationWorker findRelationWorker = new FindRelationWorker();
			RelationshipDao findRelationByEmail = findRelationWorker.findRelationByEmail(fromEmailId, toEmailId);
			tx.beginTransaction();
			if (findRelationByEmail != null) {
				// Update node
				commandResult.setAction("Update");
				UpdateRelationshipWorker updateRelationWorker = new UpdateRelationshipWorker();
				String network = (String) properties.get(NaradCommandConstants.COMMAND_NETWORK);
				Object fromProperties = properties.get(NaradCommandConstants.FROM_PROPERTIES);
				Object toProperties = properties.get(NaradCommandConstants.TO_PROPERTIES);
				if (fromProperties == null) {
					fromProperties = Collections.EMPTY_MAP;
				}
				if (toProperties == null) {
					toProperties = Collections.EMPTY_MAP;
				}
				if (!(fromProperties instanceof Map)) {
					commandResult.setStatus(CommandResultType.FAILED);
					commandResult.setDescription("invalid from properties");
				} else if (!(toProperties instanceof Map)) {
					commandResult.setStatus(CommandResultType.FAILED);
					commandResult.setDescription("invalid to properties");
				} else {
					updateRelationWorker.updateRelationship(fromEmailId, toEmailId, findRelationByEmail, network,
							(Map) fromProperties, (Map) toProperties);
					commandResult.setStatus(CommandResultType.SUCCESS);
				}
			} else {
				commandResult.setAction("Add");
				CreateRelationshipWorker createRelationWorker = new CreateRelationshipWorker();
				String network = (String) properties.get(NaradCommandConstants.COMMAND_NETWORK);
				Object fromProperties = properties.get(NaradCommandConstants.FROM_PROPERTIES);
				Object toProperties = properties.get(NaradCommandConstants.TO_PROPERTIES);
				if (fromProperties == null) {
					fromProperties = Collections.EMPTY_MAP;
				}
				if (toProperties == null) {
					toProperties = Collections.EMPTY_MAP;
				}
				if (!(fromProperties instanceof Map)) {
					commandResult.setStatus(CommandResultType.FAILED);
					commandResult.setDescription("invalid from properties");
				} else if (!(toProperties instanceof Map)) {
					commandResult.setStatus(CommandResultType.FAILED);
					commandResult.setDescription("invalid to properties");
				} else {
					createRelationWorker.createRelationship(fromEmailId, toEmailId, network, (Map) fromProperties,
							(Map) toProperties);
					commandResult.setStatus(CommandResultType.SUCCESS);
				}
			}
			tx.transactionSuccess();
		} catch (Exception e) {
			tx.transactionFail();
			commandResult.setStatus(CommandResultType.FAILED);
			commandResult.setDescription(e.getMessage());
			logger.info("Exception while executing command: {}", e, COMMAND_NAME);
		}

		return commandResult;
	}
	
	public CommandResult addRelationByEmail(String fromEmailId, String toEmailId, Map<String, Object> properties) {
		CommandResult commandResult = new CommandResult(COMMAND_NAME);

		if (fromEmailId == null || fromEmailId.isEmpty()) {
			commandResult.setDescription("Cannot execute command with null fromEmailId");
			return commandResult;
		} else if (toEmailId == null || toEmailId.isEmpty()) {
			commandResult.setDescription("Cannot execute command with null toEmailId");
			return commandResult;
		}
		
		TransactionWorker tx = new TransactionWorker();
		try {
			FindNodeWorker findNodeWorker = new FindNodeWorker();
			PersonDao fromPersonDao = findNodeWorker.findPersonByEmail(fromEmailId);
			if (fromPersonDao == null) {
				commandResult
						.setDescription("Cannot execute command as no person exists with email id: " + fromEmailId);
				return commandResult;
			}
			PersonDao toPersonDao = findNodeWorker.findPersonByEmail(toEmailId);
			if (toPersonDao == null) {
				commandResult.setDescription("Cannot execute command as no person exists with email id: " + toEmailId);
				return commandResult;
			}
			
			addRelationByPersonDao(commandResult, properties, tx, fromPersonDao, toPersonDao);
			tx.transactionSuccess();
		} catch (Exception e) {
			tx.transactionFail();
			commandResult.setStatus(CommandResultType.FAILED);
			commandResult.setDescription(e.getMessage());
			logger.info("Exception while executing command: {}", e, COMMAND_NAME);
		}

		return commandResult;
	}
	
	public CommandResult addRelationByPerson(Map<String, Object> fromUser, Map<String, Object> toUser, Map<String, Object> properties) {
		CommandResult commandResult = new CommandResult(COMMAND_NAME);

		if (fromUser == null || fromUser.isEmpty()) {
			commandResult.setDescription("Cannot execute command with null fromEmailId");
			return commandResult;
		} else if (toUser == null || toUser.isEmpty()) {
			commandResult.setDescription("Cannot execute command with null toEmailId");
			return commandResult;
		}
		
		TransactionWorker tx = new TransactionWorker();
		try {
			FindNodeWorker findNodeWorker = new FindNodeWorker();
			PersonDao fromPersonDao = findNodeWorker.findPersonByProperties(fromUser);
			//TODO - check if adding is greedy
			if (fromPersonDao == null) {
				//TODO - fix this log
				// commandResult
				// .setDescription("Cannot execute command as no person exists with email id: "/* + fromEmailId */);
				// return commandResult;
				logger.info("Could not find user to create relationship adding user.");
				CreateNodeWorker createNodeWorker = new CreateNodeWorker();
				fromPersonDao = createNodeWorker.createPerson(fromUser);
			}
			
			PersonDao toPersonDao = findNodeWorker.findPersonByProperties(toUser);
			if (toPersonDao == null) {
				// TODO - fix this log
				// commandResult
				// .setDescription("Cannot execute command as no person exists with email id: " /* + toEmailId */);
				// return commandResult;
				logger.info("Could not find user to create relationship adding user.");
				CreateNodeWorker createNodeWorker = new CreateNodeWorker();
				toPersonDao = createNodeWorker.createPerson(toUser);
			}
			
			addRelationByPersonDao(commandResult, properties, tx, fromPersonDao, toPersonDao);
			tx.transactionSuccess();
		} catch (Exception e) {
			tx.transactionFail();
			commandResult.setStatus(CommandResultType.FAILED);
			commandResult.setDescription(e.getMessage());
			logger.info("Exception while executing command: {}", COMMAND_NAME, e);
		}

		return commandResult;
	}

	private void addRelationByPersonDao(CommandResult commandResult, Map<String, Object> properties,
			TransactionWorker tx, PersonDao fromPersonDao, PersonDao toPersonDao) throws NaradException {
		
		Object fromProperties = properties.get(NaradCommandConstants.FROM_PROPERTIES);
		Object toProperties = properties.get(NaradCommandConstants.TO_PROPERTIES);
		if (fromProperties == null) {
			fromProperties = Collections.EMPTY_MAP;
		}
		if (toProperties == null) {
			toProperties = Collections.EMPTY_MAP;
		}
		if (!(fromProperties instanceof Map)) {
			commandResult.setStatus(CommandResultType.FAILED);
			commandResult.setDescription("invalid from properties");
			return;
		} else if (!(toProperties instanceof Map)) {
			commandResult.setStatus(CommandResultType.FAILED);
			commandResult.setDescription("invalid to properties");
			return;
		}
		
		FindRelationWorker findRelationWorker = new FindRelationWorker();
		PersonRelationDao findRelation = findRelationWorker.findRelationBetweenPeople(fromPersonDao, toPersonDao);
		tx.beginTransaction();
		if (findRelation != null) {
			// Update relationship
			commandResult.setAction("Update");
			UpdateRelationshipWorker updateRelationWorker = new UpdateRelationshipWorker();
			updateRelationWorker.updateRelationship(fromPersonDao, toPersonDao, findRelation,
					(Map) fromProperties, (Map) toProperties);
			commandResult.setStatus(CommandResultType.SUCCESS);
		} else {
			commandResult.setAction("Add");
			CreateRelationshipWorker createRelationWorker = new CreateRelationshipWorker();
			createRelationWorker.createRelationship(fromPersonDao, toPersonDao, (Map) fromProperties,
					(Map) toProperties);
			commandResult.setStatus(CommandResultType.SUCCESS);
		}
	}

}
