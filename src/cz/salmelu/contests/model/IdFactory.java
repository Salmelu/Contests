package cz.salmelu.contests.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class IdFactory implements Serializable {
	
	/**  */
	private static final long serialVersionUID = -4655236718997653511L;

	private Map<String, Integer> ids;
	
	private static IdFactory instance = null;
	
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
	
	private IdFactory() {
		ids = new HashMap<>();
	}
	
	protected static IdFactory getInstance() {
		if(instance == null) {
			instance = new IdFactory();
		}
		return instance;
	}
	
	protected static void loadFactory(IdFactory f) {
		if(f != null) {
			instance = f;
		}
	}
}
