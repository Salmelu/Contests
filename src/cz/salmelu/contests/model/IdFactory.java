package cz.salmelu.contests.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * A factory taking care of object ids, being the only way to assign a new id to objects.
 * Class is package private, therefore it cannot be modified and used outside the model objects
 * Registers the class automatically when getNewId() is first called
 * Implemented as a singleton
 * @author salmelu
 */
class IdFactory implements Serializable {
	
	/** Serialization UID */
	private static final long serialVersionUID = -4655236718997653511L;
	/** Holds ids that will be assigned when getNewId() is called, is accessed by class name */
	private Map<String, Integer> ids;
	/** Implementing Singleton pattern */
	private static IdFactory instance = null;
	
	/**
	 * Gets a new unique id for an object
	 * @param o the current object (required for object type)
	 * @return a new unique id for the object
	 */
	protected <T> int getNewId(T o) {
		String cName = o.getClass().getName();
		int ret;
		
		if(!ids.containsKey(cName)) {
			ret = 1;
		}
		else {
			ret = ids.get(cName);
		}
		ids.put(cName, ret+1);
		return ret;
	}
	
	/**
	 * Private constructor initializing the field
	 */
	private IdFactory() {
		ids = new HashMap<>();
	}
	
	/**
	 * Gets (and initializes, if needed) an instance of IdFactory
	 * @return an instance of IdFactory
	 */
	protected static IdFactory getInstance() {
		if(instance == null) {
			instance = new IdFactory();
		}
		return instance;
	}
	
	/** 
	 * Allows replacing the remembered instance by a new one
	 * Used for loading purposes
	 * @param f replacing IdFactory
	 */
	protected static void loadFactory(IdFactory f) {
		if(f != null) {
			instance = f;
		}
	}
}
