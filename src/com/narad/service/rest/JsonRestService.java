package com.narad.service.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;

import com.narad.command.AddNodeCommand;
import com.narad.command.AddRelationCommand;
import com.narad.command.CommandResult;
import com.narad.command.FindNodeCommand;
import com.narad.command.FindPathCommand;

@Path("/")
public class JsonRestService {

	private static final String RESULT = "result";
	private static final String ACTION = "action";
	private static final String PROPERTIES = "properties";

	private static final String TO_EMAIL = "toEmail";
	private static final String FROM_EMAIL = "fromEmail";
	private static final String EMAIL = "email";

	private static final String FIND_PATH = "findPath";
	private static final String FIND_PERSON = "findPerson";
	private static final String ADD_RELATION = "addRelation";
	private static final String ADD_NODE = "addNode";

	public JsonRestService() {
		super();
	}

	@GET
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.APPLICATION_FORM_URLENCODED })
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/")
	public String serverCheck() {
		JSONObject retJsonObj = new JSONObject();
		retJsonObj.put("ServerStatus", "Running");
		return retJsonObj.toJSONString();
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.APPLICATION_FORM_URLENCODED })
	@Produces({ MediaType.APPLICATION_JSON })
	@Path(ADD_NODE)
	public String addNode(@FormParam(EMAIL) String email, @FormParam(PROPERTIES) JsonRequestMap properties) {
		AddNodeCommand addNodeCommand = new AddNodeCommand();
		CommandResult addNode = addNodeCommand.addNode(email, properties);
		JSONObject retJsonObj = new JSONObject();
		retJsonObj.put(ACTION, ADD_NODE);
		retJsonObj.put(EMAIL, email);
		retJsonObj.put(PROPERTIES, properties);
		retJsonObj.put(RESULT, addNode.getResultAsMap());
		return retJsonObj.toJSONString();
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.APPLICATION_FORM_URLENCODED })
	@Produces({ MediaType.APPLICATION_JSON })
	@Path(ADD_RELATION)
	public String addRelation(@FormParam(FROM_EMAIL) String fromEmail, @FormParam(TO_EMAIL) String toEmail,
			@FormParam(PROPERTIES) JsonRequestMap properties) {
		AddRelationCommand cmd = new AddRelationCommand();
		CommandResult addRelation = cmd.addRelation(fromEmail, toEmail, properties);
		JSONObject retJsonObj = new JSONObject();
		retJsonObj.put(ACTION, ADD_RELATION);
		retJsonObj.put(FROM_EMAIL, fromEmail);
		retJsonObj.put(TO_EMAIL, toEmail);
		retJsonObj.put(PROPERTIES, properties);
		retJsonObj.put(RESULT, addRelation.getResultAsMap());
		return retJsonObj.toJSONString();
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.APPLICATION_FORM_URLENCODED })
	@Produces({ MediaType.APPLICATION_JSON })
	@Path(FIND_PERSON)
	public String findPerson(@FormParam(EMAIL) String email, @FormParam(PROPERTIES) JsonRequestMap properties) {
		FindNodeCommand cmd = new FindNodeCommand();
		CommandResult findNode = cmd.findNode(email, properties);
		JSONObject retJsonObj = new JSONObject();
		retJsonObj.put(ACTION, FIND_PERSON);
		retJsonObj.put(RESULT, findNode.getResultAsMap());
		retJsonObj.put(PROPERTIES, properties);
		return retJsonObj.toJSONString();
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.APPLICATION_FORM_URLENCODED })
	@Produces({ MediaType.APPLICATION_JSON })
	@Path(FIND_PATH)
	public String findPath(@FormParam(FROM_EMAIL) String fromEmail, @FormParam(TO_EMAIL) String toEmail,
			@FormParam(PROPERTIES) JsonRequestMap properties) {
		FindPathCommand cmd = new FindPathCommand();
		CommandResult findPath = cmd.findPath(fromEmail, toEmail, properties);
		JSONObject retJsonObj = new JSONObject();
		retJsonObj.put(ACTION, FIND_PATH);
		retJsonObj.put(RESULT, findPath.getResultAsMap());
		retJsonObj.put(PROPERTIES, properties);
		return retJsonObj.toJSONString();
	}
}
