package cz.salmelu.contests.client;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import cz.salmelu.contests.util.ConfigParser;

public class Config {
	
	public static final int INET_PORT;
	public static final InetAddress INET_ADDR;

	static {
		ConfigParser cp = new ConfigParser(new File("config/client.conf"));

		INET_PORT = cp.getIntegerProperty("INET_PORT", 8203, 1, 65535);
		INET_ADDR = getAddrFromString(cp.getStringProperty("INET_ADDR", "127.0.0.1"));
	}
	
	private Config() {}

	private static InetAddress getAddrFromString(String strAddr) {
		InetAddress addr = null;
		try {
			addr = InetAddress.getByName(strAddr);
		}
		catch(UnknownHostException e) {
			System.err.println("Unable to set parse server internet address from config. "
					+ "Please check your config file and try it again");
			e.printStackTrace();
			try {
				addr = InetAddress.getLocalHost();
				System.err.println(addr);
			} 
			catch (UnknownHostException e1) {
				e1.printStackTrace();
				addr = InetAddress.getLoopbackAddress();
			}
		}
		return addr;
	}
	
}
