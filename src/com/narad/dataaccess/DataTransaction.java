package com.narad.dataaccess;

import org.neo4j.graphdb.Transaction;

/**
 * Abstraction to transaction class in graph
 * @author Aditya
 *
 */
public class DataTransaction {

	private Transaction tx;
	private boolean isCompleted;

	public DataTransaction(Transaction tx) {
		super();
		this.tx = tx;
		isCompleted = false;
	}
	
	public void succes() {
		isCompleted = true;
		tx.success();
	}
	
	public void failure(){
		isCompleted = true;
		tx.failure();
	}
	
	public void finish() {
		if (!isCompleted) {
			tx.failure();
		}
		tx.finish();
	}
	
	//To stop connection leaks
	@Override
	protected void finalize() throws Throwable {
		finish();
	}
	

}
