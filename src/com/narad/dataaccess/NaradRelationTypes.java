package com.narad.dataaccess;

import org.neo4j.graphdb.RelationshipType;

public enum NaradRelationTypes implements RelationshipType {
	PERSON_REFERENCE, FRIEND
}
