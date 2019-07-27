package com.narad.dataaccess.dao;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

import com.narad.util.NaradMapUtils;

public class InstitutionDaoInfo implements DaoPropertyStore {

	private String institutionId;
	private String institutionName;
	private InstitutionType institutionType;
	private String title;
	private Integer fromYear;
	private Integer toYear;
	private Map<String, Object> properties;

	private enum InstitutionType {
		SCHOOL, COLLEGE, COMPANY;

		public static InstitutionType getType(String name) {
			if (name == null) {
				return null;
			} else if (name.equals(SCHOOL)) {
				return SCHOOL;
			} else if (name.equals(COLLEGE)) {
				return COLLEGE;
			} else if (name.equals(COMPANY)) {
				return COMPANY;
			}
			return null;
		}
	}

	@Override
	public DaoPropertyStore buildPropertyStore(Map<String, Object> map, String keyPrefix) {
		String keyPrefix2 = keyPrefix == null ? "" : keyPrefix + DaoConstants.SEPERATOR;
		institutionId = MapUtils.getString(map, keyPrefix2 + DaoConstants.INST_ID);
		institutionName = MapUtils.getString(map, keyPrefix2 + DaoConstants.INST_NAME);
		String institutionTypeStr = MapUtils.getString(map, keyPrefix2 + DaoConstants.INST_TYPE);
		institutionType = InstitutionType.getType(institutionTypeStr);
		title = MapUtils.getString(map, keyPrefix2 + DaoConstants.INST_TITLE);
		fromYear = MapUtils.getInteger(map, keyPrefix2 + DaoConstants.INST_FROM_YEAR);
		toYear = MapUtils.getInteger(map, keyPrefix2 + DaoConstants.INST_TO_YEAR);
		if (institutionType != null && institutionId != null) {
			properties = NaradMapUtils.stringToJson(MapUtils.getString(map, keyPrefix2 + DaoConstants.PROPERTIES));
			return this;
		}
		return null;
	}

	@Override
	public Map<String, Object> getStoreAsMap(String keyPrefix) {
		String keyPrefix2 = keyPrefix == null ? "" : keyPrefix + DaoConstants.SEPERATOR;
		HashMap<String, Object> map = new HashMap<String, Object>();
		NaradMapUtils.putInMapIfNotNull(map, keyPrefix2 + DaoConstants.INST_ID, institutionId);
		NaradMapUtils.putInMapIfNotNull(map, keyPrefix2 + DaoConstants.INST_NAME, institutionName);
		NaradMapUtils.putInMapIfNotNull(map, keyPrefix2 + DaoConstants.INST_TYPE, institutionType.name());
		NaradMapUtils.putInMapIfNotNull(map, keyPrefix2 + DaoConstants.INST_TITLE, title);
		NaradMapUtils.putInMapIfNotNull(map, keyPrefix2 + DaoConstants.INST_FROM_YEAR, fromYear);
		NaradMapUtils.putInMapIfNotNull(map, keyPrefix2 + DaoConstants.INST_TO_YEAR, toYear);
		NaradMapUtils.putInMapIfNotNull(map, keyPrefix2 + DaoConstants.PROPERTIES, NaradMapUtils.mapToJson(properties));
		return map;
	}

	public String getInstitutionId() {
		return institutionId;
	}

	public void setInstitutionId(String institutionId) {
		this.institutionId = institutionId;
	}

	public String getInstitutionName() {
		return institutionName;
	}

	public void setInstitutionName(String institutionName) {
		this.institutionName = institutionName;
	}

	public InstitutionType getInstitutionType() {
		return institutionType;
	}

	public void setInstitutionType(InstitutionType institutionType) {
		this.institutionType = institutionType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getFromYear() {
		return fromYear;
	}

	public void setFromYear(Integer fromYear) {
		this.fromYear = fromYear;
	}

	public Integer getToYear() {
		return toYear;
	}

	public void setToYear(Integer toYear) {
		this.toYear = toYear;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

}
