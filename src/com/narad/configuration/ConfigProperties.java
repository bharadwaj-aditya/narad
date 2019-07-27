package com.narad.configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigProperties {
	private static final Logger logger = LoggerFactory.getLogger(ConfigProperties.class);

	private static final String DEFAULT_CONFIG_PROPERTIES = "configProperties";
	private static final String DEFAULT_CONFIG_PROPERTIES_PATH = "resources/config.xml";
	private String configPropertiesPath;
	private Properties config;

	private static ConfigProperties instance;

	public static synchronized ConfigProperties getInstance() {
		if (instance == null) {
			instance = new ConfigProperties();
		}
		return instance;
	}

	public ConfigProperties(String configPropertiesPath) {
		synchronized (ConfigProperties.class) {
			this.configPropertiesPath = configPropertiesPath;
			logger.info("Direct constructor invocation. Using config file at: "
					+ this.configPropertiesPath);
			instance = this;

			try {
				init();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}

		}
	}

	private ConfigProperties() {
		super();
		configPropertiesPath = DEFAULT_CONFIG_PROPERTIES_PATH;
		String property = System.getProperty(DEFAULT_CONFIG_PROPERTIES);
		if (property != null) {
			configPropertiesPath = property;
		}

		try {
			init();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public void init() throws Exception {
		config = new Properties();
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(configPropertiesPath);
		} catch (FileNotFoundException e) {
			throw e;
		}

		try {
			config.loadFromXML(fileInputStream);
		} catch (InvalidPropertiesFormatException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}
	}

	public Object getProperty(String propertyName) {
		return config.get(propertyName);
	}

}
