package cz.salmelu.contests.server;

import java.io.File;

import cz.salmelu.contests.util.ConfigParser;

public class Config {
		
	public static final boolean VERBOSE;

	public static final boolean SAVE_METHOD_FILE;
	public static final String SAVE_FILE;
	public static final boolean AUTO_SAVE;
	public static final boolean SAVE_ON_CHANGE;
	public static final int SAVE_INTERVAL;
	
	public static final int INET_PORT;

	static {
		ConfigParser cp = new ConfigParser(new File("config/server.conf"));
		VERBOSE = cp.getBooleanProperty("VERBOSE", false);

		SAVE_METHOD_FILE = cp.getBooleanProperty("SAVE_METHOD_FILE", true);
		SAVE_FILE = cp.getStringProperty("SAVE_FILE", "");
		AUTO_SAVE = cp.getBooleanProperty("AUTO_SAVE", true);
		SAVE_ON_CHANGE = cp.getBooleanProperty("SAVE_ON_CHANGE", true);
		SAVE_INTERVAL = cp.getIntegerProperty("SAVE_INTERVAL", 300, 15, 3600);
		
		INET_PORT = cp.getIntegerProperty("INET_PORT", 8203, 1, 65535);
	}
	
	private Config() {}

}
