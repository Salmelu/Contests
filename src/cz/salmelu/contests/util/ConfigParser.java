package cz.salmelu.contests.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom class used for parsing configuration files.<br>
 * Defines a constructor which parses the file.<br>
 * Also defines methods that take the parsed values and are used by Config classes to fill the fields.
 * @author salmelu
 */
public class ConfigParser {
	/** holds read properties */
	private Map<String, String> properties;
	
	/**
	 * Constructs a parser and tries to parse the given file.<br>
	 *  Saves the parsed data for later uses or requests.
	 * @param fname File to be parsed
	 */
	public ConfigParser(File fname) {
		properties = new HashMap<>();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fname)))){
			String line;
			while((line = reader.readLine()) != null) {
				if(line.startsWith("#")) continue;
				String[] split = line.split("\\s*=\\s*", 2);
				if(split.length != 2 || split[0] == null || split[0].equals("") 
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
	
	/**
	 * Gets an integer property parsed from the file, if it exists.<br>
	 * If the property doesn't exist or is an invalid integer, returns default value.
	 * @param name Name of the parsed property
	 * @param defaultValue Value returned if something fails
	 * @return the read property or default value
	 */
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
	
	/**
	 * Gets an integer property parsed from the file, if it exists.<br>
	 * If the property doesn't exist, it is not a valid integer, 
	 *  or it is not in allowed range returns default value.
	 * @param name Name of the parsed property
	 * @param defaultValue Value returned if something fails
	 * @param min Minimal allowed value
	 * @param max Maximal allowed value
	 * @return the read property or default value
	 */
	public int getIntegerProperty(String name, int defaultValue, int min, int max) {
		int result = getIntegerProperty(name, defaultValue);
		if(result < min || result > max) {
			System.err.println("Error parsing config: Property " + name
					+ " must be within range " + min + "-" + max + ".");
			return defaultValue;
		}
		return result;
	}
	
	/**
	 * Gets a string property parsed from the file, if it exists.<br>
	 * If the property doesn't exist, returns default value.
	 * @param name Name of the parsed property
	 * @param defaultValue Value returned if the property doesn't exist
	 * @return the read property or default value
	 */
	public String getStringProperty(String name, String defaultValue) {
		if(!properties.containsKey(name)) {
			System.err.println("Error parsing config: Property " + name + " was not found.");
			return defaultValue;
		}
		return properties.get(name);
	}
	
	/**
	 * Gets a string property parsed from the file, if it exists.<br>
	 * Checks if the string is in allowed values.<br> 
	 * If the property doesn't exist or is not in allowed values, returns default value.
	 * @param name Name of the parsed property
	 * @param defaultValue Value returned if the property doesn't exist or it is not in allowed values
	 * @param allowed An array or enumeration of allowed values
	 * @return the read property or default value
	 */
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

	/**
	 * Gets a boolean property parsed from the file, if it exists.<br>
	 * If the property doesn't exist, or it is not a valid boolean, returns default value.
	 * @param name Name of the parsed property
	 * @param defaultValue Value returned if the property doesn't exist or it's an invalid boolean
	 * @return the read property or default value
	 */
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
