package com.narad.dataaccess.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DaoBuilderUtil {

	public static <T extends DaoPropertyStore> List<T> getPropertyListFromMap(Map<String, Object> map,
			String propertyPrefix, Class<T> t) {
		List<T> propertyValueList = new ArrayList<T>();
		Object object = map.get(propertyPrefix);
		if (object instanceof List) {
			List<Map<String, Object>> listDataMap = (List) object;
			for (Map<String, Object> data : listDataMap) {
				T newInstance;
				try {
					newInstance = t.newInstance();
					DaoPropertyStore buildPropertyStore = newInstance.buildPropertyStore(data, null);
					if (buildPropertyStore != null) {
						propertyValueList.add((T) buildPropertyStore);
					}
				} catch (InstantiationException e) {
					// TODO Handle exception
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Handle exception
					e.printStackTrace();
				}
			}
		}
		return propertyValueList;
	}

	public static <T extends DaoPropertyStore> List<T> getPropertyListFromFlatMap(Map<String, Object> map,
			String propertyPrefix, Class<T> t) {
		List<T> propertyValueList = new ArrayList<T>();
		for (int i = 0;; i++) {
			T newInstance;
			try {
				newInstance = t.newInstance();
				DaoPropertyStore buildPropertyStore = newInstance.buildPropertyStore(map, propertyPrefix
						+ DaoConstants.SEPERATOR + i);
				if (buildPropertyStore == null) {
					break;
				}
				propertyValueList.add((T) buildPropertyStore);
			} catch (InstantiationException e) {
				// TODO Handle exception
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Handle exception
				e.printStackTrace();
			}
		}
		return propertyValueList;
	}

	public static <T extends DaoPropertyStore> List<Map<String, Object>> populatePropertyMapFromDaos(
			Map<String, Object> map, String propertyPrefix, List<T> daoList) {
		List<Map<String, Object>> listDataMap = new ArrayList<Map<String, Object>>();
		for (T dao : daoList) {
			Map<String, Object> storeAsMap = dao.getStoreAsMap(propertyPrefix);
			listDataMap.add(storeAsMap);
		}
		return listDataMap;
	}

	public static <T extends DaoPropertyStore> Map<String, Object> populateFlatPropertyMapFromDaos(
			Map<String, Object> map, String propertyPrefix, List<T> daoList) {
		int i = 0;
		for (T dao : daoList) {
			Map<String, Object> storeAsMap = dao.getStoreAsMap(propertyPrefix + DaoConstants.SEPERATOR + i);
			map.putAll(storeAsMap);
			i++;
		}
		return map;
	}
}
