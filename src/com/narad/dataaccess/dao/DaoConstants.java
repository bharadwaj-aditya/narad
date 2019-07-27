package com.narad.dataaccess.dao;


public interface DaoConstants {
	//Property Generic
	public static final String SEPERATOR = ".";
	public static final String PROPERTIES = "properties";
	
	//Relation types
	public static final String RELATION_TYPE_FRIEND = "friend";

	// Person constants
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String FULL_NAME = "fullName";
	public static final String EMAIL_IDS = "emailIds";
	public static final String PHONES = "phones";
	public static final String NETWORKS = "networks";
	public static final String JOBS = "jobs";
	public static final String LOCATIONS = "locations";
	public static final String BIRTHDAY = "birthday";
	public static final String ANNIVERSARY = "anniversary";
	public static final String INDUSTRY = "industry";
	public static final String EDUCATION = "education";
	public static final String GENDER = "gender";
	public static final String SPOUSE_FIRST_NAME = "spouseFirstName";
	public static final String SPOUSE_LAST_NAME = "spouseLastName";
	public static final String SPOUSE_FULL_NAME = "spouseFullName";
	public static final String AGE = "age";
	public static final String AGE_RANGE = "ageRange";
	public static final String SKILLS = "skills";
	public static final String WEBSITES = "websites";
	public static final String LOCATION_CHECKINS = "locationCheckins";
	
	//Location properties
	public static final String LOC_ADDRESS = "address";
	public static final String LOC_CITY = "city";
	public static final String LOC_COUNTRY = "country";
	public static final String LOC_FROM_YEAR = " fromYear";
	public static final String LOC_TO_YEAR = " toYear";
	public static final String LOC_LATTITUDE = " lattitude";
	public static final String LOC_LONGITUDE = " longitude";
	
	//Institution properties
	public static final String INST_ID = "institutionId";
	public static final String INST_NAME = "institutionName";
	public static final String INST_TYPE = "institutionType";
	public static final String INST_TITLE = "title";
	public static final String INST_FROM_YEAR = LOC_FROM_YEAR;
	public static final String INST_TO_YEAR = LOC_TO_YEAR;
	
	//Network properties
	public static final String NETWORK_USER_ID = "userId";
	public static final String NETWORK_USER_URL = "userUrl";
	public static final String NETWORK_ID = "networkId";
	public static final String NETWORK_NAME = "networkName";
	
	//Relation properties
	public static final String RELATION_RETURN_EDGE = "returnEdge";
	public static final String RELATION = "relation";
	public static final String REL_TYPE = "type";
	public static final String REL_NAME = "name";
	public static final String REL_SUB_TYPE = "subType";
	public static final String REL_DISTANCE = "distance";
	public static final String REL_WEIGHT = "weight";

}
