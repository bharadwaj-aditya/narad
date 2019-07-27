package com.narad.servlet;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.MapUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.util.Random;
import scala.util.control.Exception;

import com.narad.command.AddNodeCommand;
import com.narad.command.CommandResult;
import com.narad.dataaccess.dao.DaoConstants;
import com.narad.util.NaradDataUtils;
import com.narad.util.NaradFileUtil;
import com.narad.util.NaradMapUtils;

public class RegisterServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3134663403811102423L;
	private static final Logger logger = LoggerFactory.getLogger(RegisterServlet.class);
	private static final String REMOTE_HOST = "remoteHost";

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map parameterMap = req.getParameterMap();
		StringBuilder builder = new StringBuilder();
		builder.append("Request landed at register servlet. Request path: ").append(req.getRequestURI());
		builder.append("Parameters: ").append(parameterMap.toString());
		builder.append(" .Redirecting to default page. ");

		logger.info(builder.toString());

		Map<String, Object> valueMap = new TreeMap<String, Object>();
		for (Object entryObj : parameterMap.entrySet()) {
			Map.Entry<String, Object> entry = (Map.Entry<String, Object>) entryObj;
			Object value = entry.getValue();
			if (value instanceof String[] && ((String[]) value).length > 0) {
				String key = entry.getKey();
				String paramValue = ((String[]) value)[0];
				if (paramValue != null && !paramValue.isEmpty()) {
					valueMap.put(key, paramValue);
				}
			}
		}
		valueMap.put(REMOTE_HOST,req.getRemoteHost());
		valueMap.put("remotePort",req.getRemotePort());
		valueMap.put("remoteUser",req.getRemoteUser());
		
		
		String dumpFolderPath = "D:\\development\\narad\\response_dump\\instacoll";
		new DumpToFile(dumpFolderPath, valueMap).start();
		
		ServletOutputStream outputStream = resp.getOutputStream();
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);

		outputStreamWriter.write("<html><body>");
		try {
			List<String> result = addToNarad(valueMap);
			if (result.isEmpty()) {
				outputStreamWriter.write("Data stored: <br/><br/>");
				for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
					outputStreamWriter.write(entry.getKey() + " : " + entry.getValue() + "<br/>");
				}
				outputStreamWriter.write("<br/><br/>Thanks!");
				outputStreamWriter.write("<br/><br/>If you can, Please fill up for any other people you know.");
			} else {
				outputStreamWriter
						.write("Output saved, please press back and give values for the following for more complete data \n");
				for (String key : result) {
					outputStreamWriter.write(key);
					outputStreamWriter.write("<br/>");
				}
				outputStreamWriter.write("<br/><br/>Thanks for the details.");
				outputStreamWriter.write("<br/><br/>If you can, Please fill up for any other people you know.");
			}
		} catch (RuntimeException e) {
			outputStreamWriter.write("<br/><br/>Please put proper data. Exception: " + e.getMessage());
		}
		outputStreamWriter.write("</body></html>");
		outputStreamWriter.close();
	}
	
	private List<String> addToNarad(Map<String, Object> valueMap) {
		Map<String, Object> hashMap = new HashMap<String, Object>();
		List<String> keyList = new ArrayList<String>(); 
		
		checkAndPopulateString(valueMap, hashMap, keyList, "first_name", "First Name", DaoConstants.FIRST_NAME);
		checkAndPopulateString(valueMap, hashMap, keyList, "last_name", "Last Name", DaoConstants.LAST_NAME);
		checkAndPopulateDate(valueMap, hashMap, keyList, "birth_day", "birth_month", "birth_month", "Birthday",
				DaoConstants.BIRTHDAY);
		checkAndPopulateDate(valueMap, hashMap, keyList, "anni_day", "anni_month", "anni_month", "Anniversary",
				DaoConstants.ANNIVERSARY);
		checkAndPopulateString(valueMap, hashMap, keyList, "industry", "Industry", DaoConstants.INDUSTRY);
		checkAndPopulateString(valueMap, hashMap, keyList, "gender", "Gender", DaoConstants.GENDER);
		checkAndPopulateString(valueMap, hashMap, keyList, "spouse_first", "Spouse First Name", DaoConstants.SPOUSE_FIRST_NAME);
		checkAndPopulateString(valueMap, hashMap, keyList, "spouse_last", "Spouse Last Name", DaoConstants.SPOUSE_LAST_NAME);
		
		List<String> emailIds = checkAndPopulateStringList(valueMap, hashMap, keyList, "email", "Emails", DaoConstants.EMAIL_IDS);
		if(emailIds.isEmpty()) {
			throw new RuntimeException("Email id not found");
		}
		checkAndPopulateStringList(valueMap, hashMap, keyList, "phone", "Phones", DaoConstants.PHONES);
		checkAndPopulateStringList(valueMap, hashMap, keyList, "skill", "Skills", DaoConstants.SKILLS);
	
		List<Map<String,Object>> networkList = new ArrayList<Map<String,Object>>();
		checkAndPopulateNetworkList(valueMap, networkList,"twitter", "Twitter");
		checkAndPopulateNetworkList(valueMap, networkList,"fb", "Facebook");
		checkAndPopulateNetworkList(valueMap, networkList,"linkedin", "Linkedin");
		if(networkList.isEmpty()) {
			keyList.add("Social Networks");
		} else {
			hashMap.put(DaoConstants.NETWORKS, networkList);
		}
		
		checkAndPopulateObjectList(valueMap, hashMap, keyList, "job", 3,
				new String[] { "name", "course", "from", "to" }, DaoConstants.JOBS);
		checkAndPopulateObjectList(valueMap, hashMap, keyList, "inst", 3,
				new String[] { "name", "course", "from", "to" }, DaoConstants.EDUCATION);
		
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("friends", NaradMapUtils.getPropertyStringListFromFlatMap(valueMap, "friend", "_"));
		properties.put(REMOTE_HOST, valueMap.get(REMOTE_HOST));
		properties.put("remotePort", valueMap.get("remotePort"));
		properties.put("remoteuser", valueMap.get("remoteUser"));
		
		AddNodeCommand addNodeCommand = new AddNodeCommand();
		CommandResult addPerson = addNodeCommand.addPerson(emailIds.get(0), hashMap);
		String jsonString = JSONObject.toJSONString(addPerson.getResultAsMap());
		System.out.println(jsonString);
		logger.info("Command result: {}", jsonString);
		return keyList;
	}

	private void checkAndPopulateObjectList(Map<String, Object> valueMap, Map<String, Object> hashmMap,List<String> keyList,
			String propertyPrefix, int numEntries, String[] keys, String naradName) {
		List<Map<String, Object>> networkList = new ArrayList<Map<String,Object>>();
		for (int i = 1; i <= numEntries; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			for (int j = 0; j < keys.length; j++) {
				Object object = valueMap.get(propertyPrefix + "_" + i + "_" + keys[j]);
				if (object != null) {
					map.put(keys[j], object);
				}
			}
			networkList.add(map);
		}
		if(networkList.isEmpty()) {
			keyList.add("Jobs");
		}
	}
	
	private void checkAndPopulateNetworkList(Map<String, Object> valueMap, List<Map<String,Object>> networkList,
			String propertyPrefix, String networkName) {

		List<String> propertyStringListFromFlatMap = NaradMapUtils.getPropertyStringListFromFlatMap(valueMap, propertyPrefix, "_");
		for (String url : propertyStringListFromFlatMap) {
			HashMap<String, Object> network = new HashMap<String, Object>();
			network.put(DaoConstants.NETWORK_ID, networkName);
			network.put(DaoConstants.NETWORK_NAME, networkName);
			network.put(DaoConstants.NETWORK_USER_URL, url);
			networkList.add(network);
		}
	}

	private List<String> checkAndPopulateStringList(Map<String, Object> valueMap, Map<String, Object> hashMap,
			List<String> keyList, String propertyKey, String errorName, String naradConstant) {
		List<String> emailList = NaradMapUtils.getPropertyStringListFromFlatMap(valueMap, propertyKey, "");
		if (!emailList.isEmpty()) {
			hashMap.put(naradConstant, emailList);
		} else {
			keyList.add(errorName);
		}
		return emailList;
	}

	private void checkAndPopulateString(Map<String, Object> valueMap, Map<String, Object> hashMap,
			List<String> keyList, String htmlId, String errorName, String naradName) {
		String val = (String) valueMap.get(htmlId);
		if (val == null) {
			keyList.add(errorName);
		} else {
			hashMap.put(naradName, val);
		}
	}
	
	private void checkAndPopulateInteger(Map<String, Object> valueMap, Map<String, Object> hashMap,
			List<String> keyList, String htmlId, String errorName, String naradName) {
		Integer intVal = MapUtils.getInteger(valueMap, htmlId);
		if (intVal == null) {
			keyList.add(errorName);
		} else {
			hashMap.put(naradName, intVal);
		}
	}
	
	private void checkAndPopulateDate(Map<String, Object> valueMap, Map<String, Object> hashMap, List<String> keyList,
			String htmlDayId, String htmlMonthId, String htmlYearId, String errorName, String naradName) {
		// yyyy-MM-dd'T'HH:mm:ss
		Object year = valueMap.get(htmlYearId);
		Object month = valueMap.get(htmlMonthId);
		Object day = valueMap.get(htmlDayId);
		if (year == null && month == null && day == null) {
			return;
		}
		String dateStr = year + "-" + month + "-" + day + "T00:00:00";
		Date date = NaradDataUtils.getDate(dateStr);
		if (date != null) {
			hashMap.put(naradName, dateStr);
		} else {
			keyList.add(errorName);
		}
	}

	private class DumpToFile extends Thread {
		private String baseFolderPath;
		private Map<String, Object> map;

		public DumpToFile(String baseFolderPath, Map<String, Object> map) {
			super();
			this.baseFolderPath = baseFolderPath;
			this.map = map;
		}
		
		public void writeToFile() {
			JSONObject jsonObject = new JSONObject();
			jsonObject.putAll(map);
			String jsonString = jsonObject.toJSONString();
			
			String filePath = baseFolderPath + File.separator
					+ (map.get(REMOTE_HOST) != null ? map.get(REMOTE_HOST) : "")+ "_" + System.currentTimeMillis()
					+ new Random().nextInt() + ".json";
			logger.info("Writing to file: {} ValueString: {}", new Object[] { jsonString, filePath });
			File createFile = NaradFileUtil.createFile(filePath, false, true);
			NaradFileUtil.writeToFile(createFile, jsonString);
		}

		@Override
		public void run() {
			writeToFile();
		}

	}
}
