package com.narad.command;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.narad.command.CommandResult.CommandResultType;
import com.narad.worker.ShowGraphWorker;

public class ShowGraphCommand {

	private static final Logger logger = LoggerFactory.getLogger(ShowGraphCommand.class);
	private static final String COMMAND_NAME = "showGraph";

	public CommandResult showGraph(int depth) {
		CommandResult commandResult = new CommandResult(COMMAND_NAME);
		try {

			ShowGraphWorker showGraphWorker = new ShowGraphWorker();
			List<Map<String, Object>> showGraph = showGraphWorker.showGraph(depth);

			commandResult.setData(showGraph);
			commandResult.setAction("Show");
			commandResult.setStatus(CommandResultType.SUCCESS);
		} catch (Exception e) {
			commandResult.setStatus(CommandResultType.FAILED);
			commandResult.setDescription(e.getMessage());
			logger.info("Exception while executing command: {}", e, COMMAND_NAME);
		}

		return commandResult;
	}
	
	public CommandResult showGraphStats() {
		CommandResult commandResult = new CommandResult(COMMAND_NAME);
		try {

			ShowGraphWorker showGraphWorker = new ShowGraphWorker();
			Object showGraph = showGraphWorker.giveGraphStats();

			commandResult.setData(showGraph);
			commandResult.setAction("Stats");
			commandResult.setStatus(CommandResultType.SUCCESS);
		} catch (Exception e) {
			commandResult.setStatus(CommandResultType.FAILED);
			commandResult.setDescription(e.getMessage());
			logger.info("Exception while executing command: {}", e, COMMAND_NAME);
		}

		return commandResult;
	}


}
