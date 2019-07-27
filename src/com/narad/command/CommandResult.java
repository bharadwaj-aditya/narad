package com.narad.command;

import java.util.HashMap;
import java.util.Map;

public class CommandResult {

	private String command;
	private String action;
	private CommandResultType status;
	private String description;
	private Object data;

	public CommandResult(String command) {
		super();
		this.command = command;
		status = CommandResultType.FAILED;
		//action = "unknown";
		description = "unknown";
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public CommandResultType getStatus() {
		return status;
	}

	public void setStatus(CommandResultType status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Map<String, Object> getResultAsMap() {
		Map<String, Object> propertiesMap = new HashMap<String, Object>();
		propertiesMap.put("command", command);
		if (action != null) {
			propertiesMap.put("action", action);
		}
		propertiesMap.put("status", status.toString());
		propertiesMap.put("description", description);
		propertiesMap.put("data", data);
		return propertiesMap;
	}

	public enum CommandResultType {
		SUCCESS, FAILED, UNKNOWN, NOT_IMPLEMENTED
	}

}
