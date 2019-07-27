package com.narad.service.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;

import com.narad.command.CommandResult;
import com.narad.command.ShowGraphCommand;

@Path("/debug/")
public class JsonRestDebugService {

	private static final String SHOW_GRAPH = "showGraph";
	private static final String SHOW_GRAPH_STATS = "showGraphStats";

	public JsonRestDebugService() {
		super();
	}

	@GET
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.APPLICATION_FORM_URLENCODED })
	@Produces({ MediaType.APPLICATION_JSON })
	@Path(SHOW_GRAPH)
	public String showGraph(@PathParam("depth") int depth) {
		ShowGraphCommand showGraphCommand = new ShowGraphCommand();
		CommandResult showGraphCmdResult = showGraphCommand.showGraph(depth);
		JSONObject jsonObject = new JSONObject();
		jsonObject.putAll(showGraphCmdResult.getResultAsMap());
		return jsonObject.toJSONString();
	}

	@GET
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.APPLICATION_FORM_URLENCODED })
	@Produces({ MediaType.APPLICATION_JSON })
	@Path(SHOW_GRAPH_STATS)
	public String showGraphStats() {
		ShowGraphCommand showGraphCommand = new ShowGraphCommand();
		CommandResult showGraphCmdResult = showGraphCommand.showGraphStats();
		JSONObject jsonObject = new JSONObject();
		jsonObject.putAll(showGraphCmdResult.getResultAsMap());
		return jsonObject.toJSONString();
	}

}
