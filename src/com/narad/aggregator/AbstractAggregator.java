package com.narad.aggregator;

public abstract class AbstractAggregator {
	
	protected abstract String getAggregatorId();
	
	protected abstract AggregatorType getAggregatorType();
	
	protected abstract ProfileType getProfileType();
	
	public enum AggregatorType {
		DATA_CORRECTNESS, GROUP
	}
	
	public enum ProfileType {
		PERSONAL, ORGANIZATION
	}

}
