package cz.salmelu.contests.model;

import java.io.Serializable;

public class IdFactory implements Serializable {
	
	/**  */
	private static final long serialVersionUID = -4655236718997653511L;

	private int contestId;
	
	private static IdFactory instance = null;
	
	public int getNewContestId() {
		return contestId++;
	}
	
	private IdFactory() {
		this.contestId = 1;
	}
	
	public static IdFactory getInstance() {
		if(instance == null) {
			instance = new IdFactory();
		}
		return instance;
	}
	
	public static void loadFactory(IdFactory f) {
		if(f != null) {
			instance = f;
		}
	}
	
	public static void reinitializeFactory(int id1) {
		if(instance == null) return;
		instance.contestId = id1;
	}
}
