package com.narad.dataaccess;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.GraphDatabaseAPI;
import org.neo4j.server.WrappingNeoServerBootstrapper;

public class LocalServerRunner {

	private GraphDatabaseService service;

	/*
	 * Requires
	 * com.sun.jersey.jersey-core-1.4.0.jar
com.sun.jersey.jersey-server-1.4.0.jar
jetty-6.1.12.jar
jetty-util-6.1.12.jar
server-api-1.7.2.jar
	 */
	public LocalServerRunner(GraphDatabaseService service) {
		super();
		this.service = service;
	}
	
	public void runServer() {
		WrappingNeoServerBootstrapper srv = new WrappingNeoServerBootstrapper((GraphDatabaseAPI) service);
		srv.start();
		registerShutdownHook(service);
	}
	
	public static void registerShutdownHook(final GraphDatabaseService graphDb) {
		// Registers a shutdown hook for the Neo4j instance so that it
		// shuts down nicely when the VM exits (even if you "Ctrl-C" the
		// running example before it's completed)
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				graphDb.shutdown();
			}
		});
	}
	
	

}
