package cz.salmelu.contests.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ConfigParser {
	private Map<String, String> properties;
	
	public ConfigParser(File fname) {
		properties = new HashMap<>();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fname)))){
			String line;
			while((line = reader.readLine()) != null) {
				if(line.startsWith("#")) continue;
				String[] split = line.split("\\s*=\\s*", 2);
				if(split.length != 2 || split[0] == null || split[0] == "" 
						|| split[1] == null || split[1] == "") continue;
				properties.put(split[0], split[1]);
			}
		} catch (FileNotFoundException e) {
			System.err.println("Couldn't find config file. Make sure there is one at config/settings.conf");
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println("Couldn't open config file. Make sure it is readable");
			System.err.println(e.getMessage());
		}
	}
	
	public int getIntegerProperty(String name, int defaultValue) {
		if(!properties.containsKey(name)) {
			System.err.println("Error parsing config: Property " + name + " was not found.");
			return defaultValue;
		}
		try {
			int returnValue = Integer.parseInt(properties.get(name));
			return returnValue;
		}
		catch (NumberFormatException e) {
			System.err.println("Error parsing config: Property " + name + " doesn't have an integer value.");
			return defaultValue;
		}
	}
	
	public int getIntegerProperty(String name, int defaultValue, int min, int max) {
		int result = getIntegerProperty(name, defaultValue);
		if(result < min || result > max) {
			System.err.println("Error parsing config: Property " + name
					+ " must be within range " + min + "-" + max + ".");
			return defaultValue;
		}
		return result;
	}
	
	public String getStringProperty(String name, String defaultValue) {
		if(!properties.containsKey(name)) {
			System.err.println("Error parsing config: Property " + name + " was not found.");
			return defaultValue;
		}
		return properties.get(name);
	}
	
	public String getAllowedStringProperty(String name, String defaultValue, String... allowed) {
		if(!properties.containsKey(name)) {
			System.err.println("Error parsing config: Property " + name + " was not found.");
			return defaultValue;
		}
		String loaded = properties.get(name);
		for(String s : allowed) {
			if(loaded.equalsIgnoreCase(s)) {
				return s;
			}
		}
		System.err.println("Error parsing config: Property " + name + " has invalid value " + loaded + ".");
		return defaultValue;
	}

	public boolean getBooleanProperty(String name, boolean defaultValue) {
		if(!properties.containsKey(name)) {
			System.err.println("Error parsing config: Property " + name + " was not found.");
			return defaultValue;
		}
		if(properties.get(name).equalsIgnoreCase("true")) return true;
		else if(properties.get(name).equalsIgnoreCase("false")) return false;
		System.err.println("Error parsing config: Property " + name + " doesn't have a valid boolean value");
		return defaultValue;
	}
}
