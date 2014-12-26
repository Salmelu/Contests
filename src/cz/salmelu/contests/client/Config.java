package cz.salmelu.contests.client;

import java.io.File;

import cz.salmelu.contests.util.ConfigParser;

public class Config {
	
	public static final int INET_PORT;
	public static final String INET_ADDR;

	static {
		ConfigParser cp = new ConfigParser(new File("config/client.conf"));

		INET_PORT = cp.getIntegerProperty("INET_PORT", 8203, 1, 65535);
		INET_ADDR = cp.getStringProperty("INET_ADDR", "127.0.0.1");
	}
	
	private Config() {}

}
