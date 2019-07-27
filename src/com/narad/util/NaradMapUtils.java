package com.narad.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NaradMapUtils {
	private static final Logger logger = LoggerFactory.getLogger(NaradMapUtils.class);

	/**
	 * If the map contains a key with a value not of any other type, return the value
	 * 
	 * @param <T>
	 * @param map
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static <T> T getValueFromMap(Map<String, Object> map, String key, T defaultValue) {
		if (map.containsKey(key)) {
			Object object = map.get(key);
			if (object == null || object.getClass().isInstance(defaultValue)) {
				return (T) object;
			}
		}
		return defaultValue;
	}

	/**
	 * Return the value from the map after checking the type
	 * 
	 * @param <T>
	 * @param map
	 * @param key
	 * @param clazz
	 * @return
	 */
	public static <T> T getCheckedValueFromMap(Map map, String key, Class<T> clazz) {
		Object object = map.get(key);
		if (object != null && clazz.isInstance(object)) {
			return (T) object;
		}
		return null;
	}
	
	public static <T> T getCheckedValueFromMap(Map map, String key, Class<T> clazz, T defaultObj) {
		Object object = map.get(key);
		if (object != null && clazz.isInstance(object)) {
			return (T) object;
		}
		return defaultObj;
	}

	/**
	 * Check the date for all parseable types and return
	 * 
	 * @param map
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	public static Date getDateValueFromMap(Map map, String key, Date defaultVal) {
		Object object = map.get(key);
		if (null == object) {
			return defaultVal;
		} else {
			Date date = NaradDataUtils.getDate(object);
			if (date != null) {
				return date;
			}
		}
		return defaultVal;
	}

	public static void putInMapIfNotNull(Map<String, Object> map, String key, Object value) {
		if (key != null && value != null) {
			map.put(key, value);
		}
	}

	public static String mapToJson(Map<String, Object> map) {
		if (map != null) {
			String jsonString = null;
			if (map instanceof JSONObject) {
				jsonString = ((JSONObject) map).toJSONString();
			} else {
				JSONObject jsonObject = new JSONObject();
				jsonObject.putAll(map);
				jsonString = jsonObject.toJSONString();
			}
			return jsonString;
		}
		return null;
	}

	public static Map<String, Object> stringToJson(String jsonPropertyString) {
		try {
			if (jsonPropertyString != null) {
				JSONObject parse = (JSONObject) new JSONParser().parse(jsonPropertyString);
				return parse;
			}
		} catch (org.json.simple.parser.ParseException e) {
			logger.debug("Unable to parse json for string: {} due to : {}", jsonPropertyString, e.getMessage());
		}
		return new JSONObject();
	}

	/**
	 * Convert a structured map to flat map
	 * 
	 * @param flatMap
	 * @param seperator
	 * @param currentObject
	 * @param currentPrefixStr
	 */
	public static void convertStructuredMapToFlatMap(Map<String, Object> flatMap, String seperator,
			Object currentObject, String currentPrefixStr) {
		if (currentObject instanceof Map) {
			Map<String, Object> currentMap = (Map) currentObject;
			for (Map.Entry<String, Object> entry : currentMap.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				convertStructuredMapToFlatMap(flatMap, seperator, value, currentPrefixStr == null ? key
						: currentPrefixStr + seperator + key);
			}
		} else if (currentObject instanceof List) {
			List<Object> currentList = (List) currentObject;
			int i = 0;
			for (Object obj : currentList) {
				convertStructuredMapToFlatMap(flatMap, seperator, obj, currentPrefixStr == null ? i + ""
						: currentPrefixStr + seperator + i);
				i++;
			}
		} else {
			flatMap.put(currentPrefixStr, currentObject);
		}
	}

	/**
	 * Convert a flat map to a structured map. By default, it puts number keys into a list. Ordering of list is not
	 * ensured<br/>
	 * <b>This is a very costly call!! DO NOT USE OFTEN</b>
	 * 
	 * @param flatMap
	 * @param seperator
	 * @return
	 */
	public static Map<String, Object> convertFlatMapToStructuredMap(Map<String, Object> flatMap, String seperator) {
		flatMap = new TreeMap<String, Object>(flatMap);// Ensure sorted so adding in arrays maintains proper order
		Map<String, Object> map = new HashMap<String, Object>();
		for (Map.Entry<String, Object> entry : flatMap.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			String[] split = StringUtils.split(key, seperator);
			Object currentObj = map;
			for (int i = 0; i < split.length; i++) {
				Object nextKeyObj = null;
				if (i < split.length - 1) {
					nextKeyObj = split[i + 1];
					try {
						nextKeyObj = Integer.parseInt((String) nextKeyObj);
					} catch (NumberFormatException e) {
						System.out.println(e.getMessage());
					}
				}

				if (currentObj instanceof Map) {
					Map<String, Object> currentMap = (Map) currentObj;
					if (i == split.length - 1) {
						currentMap.put(split[i], value);
					} else {
						currentObj = currentMap.get(split[i]);
						if (currentObj == null) {
							if (nextKeyObj instanceof Integer) {
								currentObj = new ArrayList<Object>();
							} else {
								currentObj = new HashMap<String, Object>();
							}
							currentMap.put(split[i], currentObj);
						}
					}
				} else if (currentObj instanceof List) {
					List<Object> currentList = (List) currentObj;
					if (i == split.length - 1) {
						currentList.add(value);
					} else {
						Object currentKeyObj = split[i];
						try {
							currentKeyObj = Integer.parseInt((String) currentKeyObj);
						} catch (NumberFormatException e) {
							System.out.println(e.getMessage());
						}
						if (currentKeyObj instanceof Integer) {
							if (currentList.size() <= (Integer) currentKeyObj) {
								if (nextKeyObj instanceof Integer) {
									currentObj = new ArrayList<Object>();
								} else {
									currentObj = new HashMap<String, Object>();
								}
								currentList.add(currentObj);
							} else {
								currentObj = currentList.get((Integer) currentKeyObj);
							}
						}
					}
				} else {
					// Log error,, unknown type data!
					logger.info("Ignoring data of unknown type while converting flat map to structured map");
				}

			}

		}
		return map;
	}
	
	public static void main1(String[] args) {

		/*
		 * HashMap<String, Object> hashMap = new HashMap<String, Object>(); String emailId = "a@b.com";
		 * hashMap.put(DaoConstants.FIRST_NAME, "Satish"); hashMap.put(DaoConstants.LAST_NAME, "Reddy");
		 * hashMap.put(DaoConstants.FULL_NAME, "Satish Reddy"); hashMap.put(DaoConstants.EMAIL_IDS, Arrays.asList(new
		 * String[] { emailId, "a@d.com" })); hashMap.put(DaoConstants.PHONES, Arrays.asList(new String[] { "21",
		 * "9999912345" }));
		 * 
		 * // networks HashMap<String, Object> twitterMap = new HashMap<String, Object>(); twitterMap.put("userId",
		 * "satred"); twitterMap.put("networkId", "twitter"); twitterMap.put("networkName", "Twitter");
		 * hashMap.put(DaoConstants.NETWORKS, Arrays.asList(new Object[] { twitterMap })); // jobs // locations
		 * hashMap.put(DaoConstants.BIRTHDAY, "1980-01-01T00:00:00"); hashMap.put(DaoConstants.ANNIVERSARY,
		 * "2005-01-01T00:00:00"); hashMap.put(DaoConstants.INDUSTRY, "IT"); // education
		 * hashMap.put(DaoConstants.GENDER, "Male"); // spouse name // spouse last name // spouse full name
		 * hashMap.put(DaoConstants.AGE, 35); // hashMap.put(DaoConstants.AGE_RANGE, 35);
		 * hashMap.put(DaoConstants.SKILLS, Arrays.asList(new String[] { "Admin", "Pool", "Table tennis" })); //
		 * Location checkin // properties // client.addPerson(emailId, hashMap); HashMap<String, Object> flatMap = new
		 * HashMap<String, Object>(); convertStructuredMapToFlatMap(flatMap, DaoConstants.SEPERATOR, hashMap, null);
		 * System.out.println(flatMap); Map<String, Object> convertFlatMapToStructuredMap =
		 * convertFlatMapToStructuredMap(flatMap, DaoConstants.SEPERATOR); System.out.println("done");
		 * 
		 * if (hashMap.size() != convertFlatMapToStructuredMap.size()) { System.out.println("boo"); }
		 */

		TreeMap<String, Object> treeMap = new TreeMap<String, Object>();
		treeMap.put("a", "asd");
		treeMap.put("A", "ASD");
		treeMap.put("1", "123");
		treeMap.put("2", "123");
		treeMap.put("10", "123");

		for (Map.Entry<String, Object> entry : treeMap.entrySet()) {
			System.out.println(entry);
		}
	}

	public static Map<String, Object> populateFlatMapForProperty(Map<String, Object> map, String propertyKey,
			List<String> values, String seperator) {
		int i = 0;
		if (values == null) {
			return null;
		}
		for (String value : values) {
			map.put(propertyKey + seperator + i, value);
			i++;
		}
		return map;
	}

	public static List<String> getPropertyStringListFromMap(Map<String, Object> map, String propertyKey) {
		Object object = map.get(propertyKey);
		if (object != null && object instanceof List) {
			return (List) object;
		}
		return null;
	}
	
	public static List<String> getPropertyStringListFromFlatMap(Map<String, Object> map, String propertyKey, String seperator) {
		List<String> propertyValueList = new ArrayList<String>();
		String propertyVal = null;
		for (int i = 0;; i++) {
			propertyVal = (String) map.get(propertyKey + seperator+ i);
			if (propertyVal == null) {
				break;
			}
			propertyValueList.add(propertyVal);
		}
		return propertyValueList;
	}
}
