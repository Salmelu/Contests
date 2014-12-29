package cz.salmelu.contests.net;

import java.io.Serializable;

import cz.salmelu.contests.model.ScoreMode;

/**
 * Used for sending team category field updates to server 
 * @author salmelu
 */
public class TeamCategoryPacket implements Serializable {

	private static final long serialVersionUID = 4650829858344324359L;
	/** Parent contest id; -1 is invalid */
	public int conId = -1;
	/** Unique id of the team category; -1 is invalid, 0 is new contest request */
	public int id = -1;
	/** New name of the team category - it should always be set to the previous name or the new one */
	public String name = null;
	/** Score mode of this team category - it should never be null when sent */
	public ScoreMode sm = null;
	
	/**
	 * An empty constructor, does nothing
	 */
	public TeamCategoryPacket() {
		
	}
}
