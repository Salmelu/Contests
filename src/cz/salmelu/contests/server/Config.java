package cz.salmelu.contests.server;

import java.io.File;

import cz.salmelu.contests.util.ConfigParser;

/**
 * A class with final static fields used for remembering server configuration settings.<br>
 * The meaning of each field is described in the config file.
 * @author salmelu
 */
public class Config {

	/** sets logging level */
	public static final String LOGGING;
	/** sets path to logs folder */
	public static final String LOGGING_PATH;

	/** sets save file */
	public static final String SAVE_FILE;
	/** sets, if the autosaving should be enabled */
	public static final boolean AUTO_SAVE;
	/** sets, if the saving should be done on data change */
	public static final boolean SAVE_ON_CHANGE;
	/** sets the saving interval in case of autosaving */
	public static final int SAVE_INTERVAL;
	
	/** sets the listening port of the server */
	public static final int INET_PORT;

	static {
		ConfigParser cp = new ConfigParser(new File("config/server.conf"));
		LOGGING = cp.getAllowedStringProperty("LOGGING", "verbose-warning",
				"verbose-verbose", "verbose-info", "verbose-warning", "info-info", "info-warning", "warning-warning");
		LOGGING_PATH = cp.getStringProperty("LOGGING_PATH", "log/");
		
		SAVE_FILE = cp.getStringProperty("SAVE_FILE", "");
		AUTO_SAVE = cp.getBooleanProperty("AUTO_SAVE", true);
		SAVE_ON_CHANGE = cp.getBooleanProperty("SAVE_ON_CHANGE", true);
		SAVE_INTERVAL = cp.getIntegerProperty("SAVE_INTERVAL", 300, 15, 3600);
		
		INET_PORT = cp.getIntegerProperty("INET_PORT", 8203, 1, 65535);
	}
	
	private Config() {}

}
