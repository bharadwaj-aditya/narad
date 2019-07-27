package com.narad.configuration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.narad.dataaccess.DataAccess;

public class NaradLifecycleListener implements ServletContextListener {
	private static final Logger logger = LoggerFactory.getLogger(NaradLifecycleListener.class);
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		logger.info("Destroying narad");
		DataAccess.getInstance().shutdown();
		logger.info("Destroyed narad");
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		logger.info("Initializing narad");
		ConfigProperties.getInstance();
		DataAccess.getInstance();
		logger.info("Initialized narad");
	}

}
