package cz.salmelu.contests.net;

import java.io.Serializable;

import cz.salmelu.contests.model.ScoreMode;

/**
 * Used for sending contest field updates to server 
 * @author salmelu
 */
public class ContestPacket implements Serializable {

	private static final long serialVersionUID = 4650829858344324359L;
	/** Unique id of the contest; -1 is invalid, 0 is new contest request */
	public int id = -1;
	/** New name of the contest - it should always be set to the previous name or the new one */
	public String name = null;
	/** New score mode of the contest - must be always set to valid value */
	public ScoreMode sm = null;
	
	/**
	 * An empty constructor, does nothing
	 */
	public ContestPacket() {
		
	}
}
