package com.narad.command;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.narad.command.CommandResult.CommandResultType;
import com.narad.dataaccess.NodeDao;
import com.narad.dataaccess.dao.PersonDao;
import com.narad.exception.NaradException;
import com.narad.worker.FindNodeWorker;

public class FindNodeCommand {

	private static final Logger logger = LoggerFactory.getLogger(FindNodeCommand.class);
	private static final String COMMAND_NAME = "findNode";

	public CommandResult findNode(String emailId, Map<String, Object> properties) {
		CommandResult commandResult = new CommandResult(COMMAND_NAME);

		try {
			if (emailId != null && !emailId.isEmpty()) {
				commandResult.setAction("findSingle");
				FindNodeWorker findNodeWorker = new FindNodeWorker();
				NodeDao findNodeByEmail = findNodeWorker.findNodeByEmail(emailId);
				if (findNodeByEmail != null) {
					commandResult.setData(findNodeByEmail.getPropertiesAsMap());
					commandResult.setStatus(CommandResultType.SUCCESS);
				} else {
					commandResult.setData(null);
					commandResult.setDescription("No such user found");
					commandResult.setStatus(CommandResultType.SUCCESS);
				}
			} else if (properties != null && properties.isEmpty()) {
				// TODO - search and return based on properties
				FindNodeWorker findNodeWorker = new FindNodeWorker();
				NodeDao[] findAllNodes = findNodeWorker.findAllNodes(properties);
				commandResult.setAction("findMultiple");
				commandResult.setData(null);
				commandResult.setStatus(CommandResultType.NOT_IMPLEMENTED);
			} else {
				commandResult.setDescription("Cannot execute command with null emailId");
			}
		} catch (NaradException e) {
			logger.info("Exception while executing command: {}", e, COMMAND_NAME);
			commandResult.setDescription(e.getMessage());
		}
		return commandResult;
	}
	
	public CommandResult findPerson(String emailId, Map<String, Object> properties) {
		CommandResult commandResult = new CommandResult(COMMAND_NAME);

		try {
			if (emailId != null && !emailId.isEmpty()) {
				commandResult.setAction("findSingle");
				FindNodeWorker findNodeWorker = new FindNodeWorker();
				PersonDao findPersonByEmail = findNodeWorker.findPersonByEmail(emailId);
				if (findPersonByEmail != null) {
					commandResult.setData(findPersonByEmail.getPersonAsMap());
					commandResult.setStatus(CommandResultType.SUCCESS);
				} else {
					commandResult.setData(null);
					commandResult.setDescription("No such user found");
					commandResult.setStatus(CommandResultType.SUCCESS);
				}
			} else if (properties != null && !properties.isEmpty()) {
				// TODO - search and return based on properties
				commandResult.setAction("findSingleByProperties");
				FindNodeWorker findNodeWorker = new FindNodeWorker();
				PersonDao findPersonByEmail = findNodeWorker.findPersonByProperties(properties);
				if (findPersonByEmail != null) {
					commandResult.setData(findPersonByEmail.getPersonAsMap());
					commandResult.setStatus(CommandResultType.SUCCESS);
				} else {
					commandResult.setData(null);
					commandResult.setDescription("No such user found");
					commandResult.setStatus(CommandResultType.SUCCESS);
				}
				/*FindNodeWorker findNodeWorker = new FindNodeWorker();
				NodeDao[] findAllNodes = findNodeWorker.findAllNodes(properties);
				commandResult.setAction("findMultiple");
				commandResult.setData(null);
				commandResult.setStatus(CommandResultType.NOT_IMPLEMENTED);*/
			} else {
				commandResult.setDescription("Cannot execute command with null emailId");
			}
		} catch (NaradException e) {
			logger.info("Exception while executing command: {}", e, COMMAND_NAME);
			commandResult.setDescription(e.getMessage());
		}
		return commandResult;
	}

}
