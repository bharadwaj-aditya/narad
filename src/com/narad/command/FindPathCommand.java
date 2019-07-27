package com.narad.command;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.narad.command.CommandResult.CommandResultType;
import com.narad.dataaccess.NodeDao;
import com.narad.dataaccess.dao.PersonDao;
import com.narad.worker.FindNodeWorker;
import com.narad.worker.FindPathWorker;

public class FindPathCommand {

	private static final Logger logger = LoggerFactory.getLogger(FindPathCommand.class);
	private static final String COMMAND_NAME = "findPath";

	public CommandResult findPath(String fromEmailId, String toEmailId, Map<String, Object> properties) {
		CommandResult commandResult = new CommandResult(COMMAND_NAME);

		if (fromEmailId == null || fromEmailId.isEmpty()) {
			commandResult.setDescription("Cannot execute command with null fromEmailId");
			return commandResult;
		} else if (toEmailId == null || toEmailId.isEmpty()) {
			commandResult.setDescription("Cannot execute command with null toEmailId");
			return commandResult;
		}

		try {
			if (properties == null || properties.isEmpty()) {
				commandResult.setAction("findPath");
				FindPathWorker findPathWorker = new FindPathWorker();
				List<NodeDao> findPath;
				findPath = findPathWorker.findPath(fromEmailId, toEmailId);
				JSONArray jsonArray = new JSONArray();
				for (NodeDao nodeDao : findPath) {
					jsonArray.add(nodeDao.getPropertiesAsMap());
				}
				commandResult.setData(jsonArray);
				commandResult.setStatus(CommandResultType.SUCCESS);
			} else {
				// search and return based on properties
				FindNodeWorker findNodeWorker = new FindNodeWorker();
				// NodeDao[] findAllNodes = findNodeWorker.findAllNodes(properties);
				commandResult.setAction("findMultiplePaths");
				commandResult.setData(null);
				commandResult.setStatus(CommandResultType.NOT_IMPLEMENTED);
			}
		} catch (Exception e) {
			commandResult.setData(e.getMessage());
			logger.info("Exception while executing command: {}", e, COMMAND_NAME);
		}
		return commandResult;
	}
	
	public CommandResult findPath(Map<String, Object> fromPersonMap, Map<String,Object> toPersonMap, Map<String, Object> properties) {
		CommandResult commandResult = new CommandResult(COMMAND_NAME);
		if (fromPersonMap == null || fromPersonMap.isEmpty()) {
			commandResult.setDescription("Cannot execute command with null fromEmailId");
			return commandResult;
		} else if (toPersonMap == null || toPersonMap.isEmpty()) {
			commandResult.setDescription("Cannot execute command with null toEmailId");
			return commandResult;
		}
		FindNodeWorker findNodeWorker = new FindNodeWorker();
		try {
			PersonDao fromPerson = findNodeWorker.findPersonByProperties(fromPersonMap);
			PersonDao toPerson = findNodeWorker.findPersonByProperties(toPersonMap);
			FindPathWorker findPathWorker = new FindPathWorker();
			List<Object> findPath = findPathWorker.findPath(fromPerson, toPerson, properties);
			JSONArray data = new JSONArray();
			data.addAll(findPath);
			commandResult.setData(data);
			commandResult.setStatus(CommandResultType.SUCCESS);
		} catch (Exception e) {
			commandResult.setData(e.getMessage());
			logger.info("Exception while executing command: {}", e, COMMAND_NAME);
		}
		return commandResult;
	}

}
