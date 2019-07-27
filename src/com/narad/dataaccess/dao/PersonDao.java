package com.narad.dataaccess.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

import com.narad.util.NaradDataUtils;
import com.narad.util.NaradMapUtils;
import com.tinkerpop.blueprints.Vertex;

public class PersonDao {

	private Vertex vertex;

	private String firstName;
	private String lastName;
	private String fullName;
	private Date birthday;
	private Date anniversary;
	private String industry;
	private String gender;
	private String spouseFirstName;
	private String spouseLastName;
	private String spouseFullName;
	private Integer age;
	private String ageRange;
	private List<String> emailIds;
	private List<String> phones;
	private List<String> websites;
	private List<String> skills;
	private List<NetworkDaoInfo> networks;
	private List<InstitutionDaoInfo> jobs;
	private List<InstitutionDaoInfo> education;
	private List<LocationDaoInfo> locations;
	private List<LocationDaoInfo> locationCheckins;
	private Map<String, Object> properties;

	// activity stream - updates in node/node views

	public PersonDao() {
		super();
		emailIds = new ArrayList<String>();
		phones = new ArrayList<String>();
		skills = new ArrayList<String>();
		websites = new ArrayList<String>();
		networks = new ArrayList<NetworkDaoInfo>();
		jobs = new ArrayList<InstitutionDaoInfo>();
		education = new ArrayList<InstitutionDaoInfo>();
		locations = new ArrayList<LocationDaoInfo>();
		locationCheckins = new ArrayList<LocationDaoInfo>();
		properties = new HashMap<String, Object>();
	}

	public PersonDao(Vertex vertex) {
		this();
		this.vertex = vertex;
		populateFromFlatData(new TinkerpopElementMapWrapper(vertex));
	}

	public void populate(Map<String, Object> map) {
		updateBasicData(map);
		emailIds = NaradMapUtils.getCheckedValueFromMap(map, DaoConstants.EMAIL_IDS, List.class, emailIds);
		phones = NaradMapUtils.getCheckedValueFromMap(map, DaoConstants.PHONES, List.class, emailIds);
		skills = NaradMapUtils.getCheckedValueFromMap(map, DaoConstants.SKILLS, List.class, emailIds);
		websites = NaradMapUtils.getCheckedValueFromMap(map, DaoConstants.WEBSITES, List.class, emailIds);
		networks = DaoBuilderUtil.getPropertyListFromMap(map, DaoConstants.NETWORKS, NetworkDaoInfo.class);
		jobs = DaoBuilderUtil.getPropertyListFromMap(map, DaoConstants.JOBS, InstitutionDaoInfo.class);
		education = DaoBuilderUtil.getPropertyListFromMap(map, DaoConstants.EDUCATION, InstitutionDaoInfo.class);
		spouseFirstName = NaradMapUtils.getCheckedValueFromMap(map, DaoConstants.SPOUSE_FIRST_NAME, String.class);
		spouseLastName = NaradMapUtils.getCheckedValueFromMap(map, DaoConstants.SPOUSE_LAST_NAME, String.class);
		spouseFullName = NaradMapUtils.getCheckedValueFromMap(map, DaoConstants.SPOUSE_FULL_NAME, String.class);
		locations = DaoBuilderUtil.getPropertyListFromMap(map, DaoConstants.LOCATIONS, LocationDaoInfo.class);
		locationCheckins = DaoBuilderUtil.getPropertyListFromMap(map, DaoConstants.LOCATION_CHECKINS,
				LocationDaoInfo.class);
	}

	public void populateFromFlatData(Map<String, Object> map) {
		updateBasicData(map);
		emailIds = NaradMapUtils.getPropertyStringListFromFlatMap(map, DaoConstants.EMAIL_IDS, DaoConstants.SEPERATOR);
		phones = NaradMapUtils.getPropertyStringListFromFlatMap(map, DaoConstants.PHONES, DaoConstants.SEPERATOR);
		skills = NaradMapUtils.getPropertyStringListFromFlatMap(map, DaoConstants.SKILLS, DaoConstants.SEPERATOR);
		websites = NaradMapUtils.getPropertyStringListFromFlatMap(map, DaoConstants.WEBSITES, DaoConstants.SEPERATOR);
		networks = DaoBuilderUtil.getPropertyListFromFlatMap(map, DaoConstants.NETWORKS, NetworkDaoInfo.class);
		jobs = DaoBuilderUtil.getPropertyListFromFlatMap(map, DaoConstants.JOBS, InstitutionDaoInfo.class);
		education = DaoBuilderUtil.getPropertyListFromFlatMap(map, DaoConstants.EDUCATION, InstitutionDaoInfo.class);
		locations = DaoBuilderUtil.getPropertyListFromFlatMap(map, DaoConstants.LOCATIONS, LocationDaoInfo.class);
		locationCheckins = DaoBuilderUtil.getPropertyListFromFlatMap(map, DaoConstants.LOCATION_CHECKINS,
				LocationDaoInfo.class);
	}

	private void updateBasicData(Map<String, Object> map) {
		firstName = MapUtils.getString(map, DaoConstants.FIRST_NAME, firstName);
		lastName = MapUtils.getString(map, DaoConstants.LAST_NAME, lastName);
		fullName = MapUtils.getString(map, DaoConstants.FULL_NAME, fullName);
		birthday = NaradMapUtils.getDateValueFromMap(map, DaoConstants.BIRTHDAY, birthday);
		anniversary = NaradMapUtils.getDateValueFromMap(map, DaoConstants.ANNIVERSARY, anniversary);
		industry = MapUtils.getString(map, DaoConstants.INDUSTRY, industry);
		gender = MapUtils.getString(map, DaoConstants.GENDER, gender);
		spouseFirstName = MapUtils.getString(map, DaoConstants.SPOUSE_FIRST_NAME, spouseFirstName);
		spouseLastName = MapUtils.getString(map, DaoConstants.SPOUSE_LAST_NAME, spouseLastName);
		spouseFullName = MapUtils.getString(map, DaoConstants.SPOUSE_FULL_NAME, spouseFullName);
		age = MapUtils.getInteger(map, DaoConstants.AGE, age);
		ageRange = MapUtils.getString(map, DaoConstants.AGE_RANGE, ageRange);
		String propertiesString = MapUtils.getString(map, DaoConstants.PROPERTIES);
		Map<String, Object> newProperties = NaradMapUtils.stringToJson(propertiesString);
		properties.putAll(newProperties);
	}

	public void updatePersonFromMap(Map<String, Object> map) {
		updateBasicData(map);
		updateList(emailIds, NaradMapUtils.getPropertyStringListFromMap(map, DaoConstants.EMAIL_IDS));
		updateList(phones, NaradMapUtils.getPropertyStringListFromMap(map, DaoConstants.PHONES));
		updateList(skills, NaradMapUtils.getPropertyStringListFromMap(map, DaoConstants.SKILLS));
		updateList(websites, NaradMapUtils.getPropertyStringListFromMap(map, DaoConstants.WEBSITES));
		updateList(networks, DaoBuilderUtil.getPropertyListFromMap(map, DaoConstants.NETWORKS, NetworkDaoInfo.class));
		updateList(jobs, DaoBuilderUtil.getPropertyListFromMap(map, DaoConstants.JOBS, InstitutionDaoInfo.class));
		updateList(education,
				DaoBuilderUtil.getPropertyListFromMap(map, DaoConstants.EDUCATION, InstitutionDaoInfo.class));
		updateList(locations, DaoBuilderUtil.getPropertyListFromMap(map, DaoConstants.LOCATIONS, LocationDaoInfo.class));
		updateList(locationCheckins,
				DaoBuilderUtil.getPropertyListFromMap(map, DaoConstants.LOCATION_CHECKINS, LocationDaoInfo.class));
	}

	public void updatePersonFromFlatMap(Map<String, Object> map) {
		updateBasicData(map);
		updateList(emailIds, NaradMapUtils.getPropertyStringListFromFlatMap(map, DaoConstants.EMAIL_IDS, null));
		updateList(phones, NaradMapUtils.getPropertyStringListFromFlatMap(map, DaoConstants.PHONES, null));
		updateList(skills, NaradMapUtils.getPropertyStringListFromFlatMap(map, DaoConstants.SKILLS, null));
		updateList(websites, NaradMapUtils.getPropertyStringListFromFlatMap(map, DaoConstants.WEBSITES, null));
		updateList(networks,
				DaoBuilderUtil.getPropertyListFromFlatMap(map, DaoConstants.NETWORKS, NetworkDaoInfo.class));
		updateList(jobs, DaoBuilderUtil.getPropertyListFromFlatMap(map, DaoConstants.JOBS, InstitutionDaoInfo.class));
		updateList(education,
				DaoBuilderUtil.getPropertyListFromFlatMap(map, DaoConstants.EDUCATION, InstitutionDaoInfo.class));
		updateList(locations,
				DaoBuilderUtil.getPropertyListFromFlatMap(map, DaoConstants.LOCATIONS, LocationDaoInfo.class));
		updateList(locationCheckins,
				DaoBuilderUtil.getPropertyListFromFlatMap(map, DaoConstants.LOCATION_CHECKINS, LocationDaoInfo.class));
	}

	private <T> void updateList(List<T> oldList, List<T> newList) {
		if (newList != null) {
			newList.removeAll(oldList);
			oldList.addAll(newList);
		}
	}

	public HashMap<String, Object> getBasicPersonDataAsMap() {
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		NaradMapUtils.putInMapIfNotNull(hashMap, DaoConstants.FIRST_NAME, firstName);
		NaradMapUtils.putInMapIfNotNull(hashMap, DaoConstants.LAST_NAME, lastName);
		NaradMapUtils.putInMapIfNotNull(hashMap, DaoConstants.FULL_NAME, fullName);
		NaradMapUtils.putInMapIfNotNull(hashMap, DaoConstants.BIRTHDAY, NaradDataUtils.getDateString(birthday));
		NaradMapUtils.putInMapIfNotNull(hashMap, DaoConstants.ANNIVERSARY, NaradDataUtils.getDateString(anniversary));
		NaradMapUtils.putInMapIfNotNull(hashMap, DaoConstants.INDUSTRY, industry);
		NaradMapUtils.putInMapIfNotNull(hashMap, DaoConstants.GENDER, gender);
		NaradMapUtils.putInMapIfNotNull(hashMap, DaoConstants.SPOUSE_FIRST_NAME, spouseFirstName);
		NaradMapUtils.putInMapIfNotNull(hashMap, DaoConstants.SPOUSE_LAST_NAME, spouseLastName);
		NaradMapUtils.putInMapIfNotNull(hashMap, DaoConstants.SPOUSE_FULL_NAME, spouseFullName);
		NaradMapUtils.putInMapIfNotNull(hashMap, DaoConstants.AGE, age);
		NaradMapUtils.putInMapIfNotNull(hashMap, DaoConstants.AGE_RANGE, ageRange);
		return hashMap;
	}

	public Map<String, Object> getPersonAsFlatMap() {
		HashMap<String, Object> hashMap = getBasicPersonDataAsMap();
		NaradMapUtils.populateFlatMapForProperty(hashMap, DaoConstants.EMAIL_IDS, emailIds, DaoConstants.SEPERATOR);
		NaradMapUtils.populateFlatMapForProperty(hashMap, DaoConstants.PHONES, phones, DaoConstants.SEPERATOR);
		NaradMapUtils.populateFlatMapForProperty(hashMap, DaoConstants.SKILLS, skills, DaoConstants.SEPERATOR);
		NaradMapUtils.populateFlatMapForProperty(hashMap, DaoConstants.WEBSITES, websites, DaoConstants.SEPERATOR);
		DaoBuilderUtil.populateFlatPropertyMapFromDaos(hashMap, DaoConstants.NETWORKS, networks);
		DaoBuilderUtil.populateFlatPropertyMapFromDaos(hashMap, DaoConstants.JOBS, jobs);
		DaoBuilderUtil.populateFlatPropertyMapFromDaos(hashMap, DaoConstants.EDUCATION, education);
		DaoBuilderUtil.populateFlatPropertyMapFromDaos(hashMap, DaoConstants.LOCATIONS, locations);
		DaoBuilderUtil.populateFlatPropertyMapFromDaos(hashMap, DaoConstants.LOCATION_CHECKINS, locationCheckins);
		NaradMapUtils.putInMapIfNotNull(hashMap, DaoConstants.PROPERTIES, NaradMapUtils.mapToJson(properties));
		return hashMap;
	}

	public Map<String, Object> getPersonAsMap() {
		HashMap<String, Object> hashMap = getBasicPersonDataAsMap();
		hashMap.put(DaoConstants.EMAIL_IDS, emailIds);
		hashMap.put(DaoConstants.PHONES, phones);
		hashMap.put(DaoConstants.SKILLS, skills);
		hashMap.put(DaoConstants.WEBSITES, websites);
		DaoBuilderUtil.populatePropertyMapFromDaos(hashMap, DaoConstants.NETWORKS, networks);
		DaoBuilderUtil.populatePropertyMapFromDaos(hashMap, DaoConstants.JOBS, jobs);
		DaoBuilderUtil.populatePropertyMapFromDaos(hashMap, DaoConstants.EDUCATION, education);
		DaoBuilderUtil.populatePropertyMapFromDaos(hashMap, DaoConstants.LOCATIONS, locations);
		DaoBuilderUtil.populatePropertyMapFromDaos(hashMap, DaoConstants.LOCATION_CHECKINS, locationCheckins);
		NaradMapUtils.putInMapIfNotNull(hashMap, DaoConstants.PROPERTIES, NaradMapUtils.mapToJson(properties));
		return hashMap;
	}

	public Vertex getVertex() {
		return vertex;
	}

	public void setVertex(Vertex vertex) {
		this.vertex = vertex;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public Date getAnniversary() {
		return anniversary;
	}

	public void setAnniversary(Date anniversary) {
		this.anniversary = anniversary;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getSpouseFirstName() {
		return spouseFirstName;
	}

	public void setSpouseFirstName(String spouseFirstName) {
		this.spouseFirstName = spouseFirstName;
	}

	public String getSpouseLastName() {
		return spouseLastName;
	}

	public void setSpouseLastName(String spouseLastName) {
		this.spouseLastName = spouseLastName;
	}

	public String getSpouseFullName() {
		return spouseFullName;
	}

	public void setSpouseFullName(String spouseFullName) {
		this.spouseFullName = spouseFullName;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getAgeRange() {
		return ageRange;
	}

	public void setAgeRange(String ageRange) {
		this.ageRange = ageRange;
	}

	public List<InstitutionDaoInfo> getEducation() {
		return education;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public List<String> getEmailIds() {
		return emailIds;
	}

	public List<String> getPhones() {
		return phones;
	}

	public List<String> getWebsites() {
		return websites;
	}

	public List<String> getSkills() {
		return skills;
	}

	public List<NetworkDaoInfo> getNetworks() {
		return networks;
	}

	public List<InstitutionDaoInfo> getJobs() {
		return jobs;
	}

	public List<LocationDaoInfo> getLocations() {
		return locations;
	}

	public List<LocationDaoInfo> getLocationCheckins() {
		return locationCheckins;
	}
}
