package com.narad.command;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.narad.command.CommandResult.CommandResultType;
import com.narad.dataaccess.NodeDao;
import com.narad.dataaccess.dao.DaoConstants;
import com.narad.dataaccess.dao.PersonDao;
import com.narad.util.NaradMapUtils;
import com.narad.worker.CreateNodeWorker;
import com.narad.worker.FindNodeWorker;
import com.narad.worker.TransactionWorker;
import com.narad.worker.UpdateNodeWorker;

public class AddNodeCommand {

	private static final Logger logger = LoggerFactory.getLogger(AddNodeCommand.class);
	private static final String COMMAND_NAME = "addNode";

	public CommandResult addNode(String emailId, Map<String, Object> properties) {
		CommandResult commandResult = new CommandResult(COMMAND_NAME);

		if (emailId == null || emailId.isEmpty()) {
			commandResult.setDescription("Cannot execute command with null emailId");
			return commandResult;
		}

		if (properties == null || properties.isEmpty()) {
			commandResult.setDescription("Cannot execute command with null proprties");
			return commandResult;
		}

		TransactionWorker tx = new TransactionWorker();
		try {
			FindNodeWorker findNodeWorker = new FindNodeWorker();
			NodeDao findNodeByEmail = findNodeWorker.findNodeByEmail(emailId);

			tx.beginTransaction();
			if (findNodeByEmail != null) {
				UpdateNodeWorker updateNodeWorker = new UpdateNodeWorker();
				Object profiles = properties.get(NaradCommandConstants.COMMAND_PROFILES);
				updateNodeWorker.updateNodeWithEmail(emailId, findNodeByEmail, (List) profiles);
				commandResult.setAction("Update");
				commandResult.setStatus(CommandResultType.SUCCESS);
			} else {
				CreateNodeWorker createNodeWorker = new CreateNodeWorker();
				Object profiles = properties.get(NaradCommandConstants.COMMAND_PROFILES);
				createNodeWorker.createNodeWithSingleEmail(emailId, (List) profiles);
				commandResult.setAction("Add");
				commandResult.setStatus(CommandResultType.SUCCESS);
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
	
	public CommandResult addPerson(String emailId, Map<String, Object> properties) {
		CommandResult commandResult = new CommandResult(COMMAND_NAME);

		if (properties == null || properties.isEmpty()) {
			commandResult.setDescription("Cannot execute command with null properties");
			return commandResult;
		}
		
		if (emailId == null || emailId.isEmpty()) {
			List<String> emailIds = NaradMapUtils.getPropertyStringListFromMap(properties, DaoConstants.EMAIL_IDS);
			if (emailIds == null || emailIds.isEmpty()) {
				String firstName = NaradMapUtils.getCheckedValueFromMap(properties, DaoConstants.FIRST_NAME,
						String.class);
				String lastName = NaradMapUtils
						.getCheckedValueFromMap(properties, DaoConstants.LAST_NAME, String.class);
				String fullName = NaradMapUtils
						.getCheckedValueFromMap(properties, DaoConstants.FULL_NAME, String.class);
				if (firstName == null && lastName == null && fullName == null) {
					// No identifying parameter
					commandResult.setDescription("Cannot execute command with null emailId and name");
					return commandResult;
				}
			} else {
				emailId = emailIds.get(0);
			}
		}

		TransactionWorker tx = new TransactionWorker();
		try {
			FindNodeWorker findNodeWorker = new FindNodeWorker();
			PersonDao findPerson  = null;
			if (emailId != null) {
				findPerson = findNodeWorker.findPersonByEmail(emailId);
			} else {
				findPerson = findNodeWorker.findPersonByProperties(properties);
			}

			tx.beginTransaction();
			if (findPerson != null) {
				UpdateNodeWorker updateNodeWorker = new UpdateNodeWorker();
				updateNodeWorker.updatePerson(findPerson, properties, null);
				commandResult.setAction("Update");
				commandResult.setStatus(CommandResultType.SUCCESS);
			} else {
				CreateNodeWorker createNodeWorker = new CreateNodeWorker();
				createNodeWorker.createPerson(properties);
				commandResult.setAction("Add");
				commandResult.setStatus(CommandResultType.SUCCESS);
			}
			tx.transactionSuccess();
		} catch (Exception e) {
			tx.transactionFail();
			commandResult.setStatus(CommandResultType.FAILED);
			commandResult.setDescription(e.getMessage());
			logger.info("Exception while executing command: {}", new Object[] { COMMAND_NAME, e });
		}

		return commandResult;
	}

}
