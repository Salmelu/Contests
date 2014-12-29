package cz.salmelu.contests.server;

import java.io.File;

import cz.salmelu.contests.util.ConfigParser;

/**
 * A class with final static fields used for remembering configuration settings
 * @author salmelu
 */
public class Config {
		
	public static final String LOGGING;
	public static final String LOGGING_PATH;

	public static final boolean SAVE_METHOD_FILE;
	public static final String SAVE_FILE;
	public static final boolean AUTO_SAVE;
	public static final boolean SAVE_ON_CHANGE;
	public static final int SAVE_INTERVAL;
	
	public static final int INET_PORT;


	static {
		ConfigParser cp = new ConfigParser(new File("config/server.conf"));
		LOGGING = cp.getAllowedStringProperty("LOGGING", "verbose-warning",
				"verbose-verbose", "verbose-info", "verbose-warning", "info-info", "info-warning", "warning-warning");
		LOGGING_PATH = cp.getStringProperty("LOGGING_PATH", "log/");

		SAVE_METHOD_FILE = cp.getBooleanProperty("SAVE_METHOD_FILE", true);
		SAVE_FILE = cp.getStringProperty("SAVE_FILE", "");
		AUTO_SAVE = cp.getBooleanProperty("AUTO_SAVE", true);
		SAVE_ON_CHANGE = cp.getBooleanProperty("SAVE_ON_CHANGE", true);
		SAVE_INTERVAL = cp.getIntegerProperty("SAVE_INTERVAL", 300, 15, 3600);
		
		INET_PORT = cp.getIntegerProperty("INET_PORT", 8203, 1, 65535);
	}
	
	private Config() {}

}
