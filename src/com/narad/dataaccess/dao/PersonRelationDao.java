package com.narad.dataaccess.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.narad.util.NaradMapUtils;
import com.tinkerpop.blueprints.Edge;

public class PersonRelationDao {

	private Edge edge;
	private Object returnEdgeId;
	private String label;
	private List<RelationDaoInfo> relationInfos;

	public PersonRelationDao() {
		super();
		relationInfos = new ArrayList<RelationDaoInfo>();
	}

	public PersonRelationDao(Edge edge) {
		this();
		this.edge = edge;
		this.label = edge.getLabel();
		updateFromFlatMap(new TinkerpopElementMapWrapper(edge));
	}

	public PersonRelationDao(Map<String, Object> map) {
		this();
		updateFromMap(map);
	}

	public void updateFromFlatMap(Map<String, Object> map) {
		this.returnEdgeId = map.get(DaoConstants.RELATION_RETURN_EDGE);
		List<RelationDaoInfo> newRelationInfos = DaoBuilderUtil.getPropertyListFromFlatMap(map, DaoConstants.RELATION,
				RelationDaoInfo.class);
		newRelationInfos.removeAll(relationInfos);
		relationInfos.addAll(newRelationInfos);

	}

	public void updateFromMap(Map<String, Object> map) {
		this.returnEdgeId = map.get(DaoConstants.RELATION_RETURN_EDGE);
		List<RelationDaoInfo> newRelationInfos = DaoBuilderUtil.getPropertyListFromMap(map, DaoConstants.RELATION,
				RelationDaoInfo.class);
		newRelationInfos.removeAll(relationInfos);
		relationInfos.addAll(newRelationInfos);
	}

	public Map<String, Object> getRelationDaoAsFlatMap() {
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		NaradMapUtils.putInMapIfNotNull(hashMap, DaoConstants.RELATION_RETURN_EDGE, returnEdgeId);
		DaoBuilderUtil.populateFlatPropertyMapFromDaos(hashMap, DaoConstants.RELATION, relationInfos);
		return hashMap;
	}

	public Map<String, Object> getRelationDaoAsMap() {
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		NaradMapUtils.putInMapIfNotNull(hashMap, DaoConstants.RELATION_RETURN_EDGE, returnEdgeId);
		DaoBuilderUtil.populatePropertyMapFromDaos(hashMap, DaoConstants.RELATION, relationInfos);
		return hashMap;
	}

	public Edge getEdge() {
		return edge;
	}

	public void setEdge(Edge edge) {
		this.edge = edge;
	}
	
	public Object getReturnEdgeId() {
		return returnEdgeId;
	}

	public void setReturnEdgeId(Object returnEdgeId) {
		this.returnEdgeId = returnEdgeId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<RelationDaoInfo> getRelationInfos() {
		return relationInfos;
	}

}
