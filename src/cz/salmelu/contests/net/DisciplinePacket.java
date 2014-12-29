package cz.salmelu.contests.net;

import java.io.Serializable;

/**
 * Used for sending discipline field updates to server 
 * @author salmelu
 */
public class DisciplinePacket implements Serializable {

	private static final long serialVersionUID = 4650829858344324359L;
	/** Parent contest id; -1 is invalid */
	public int conId = -1;
	/** Unique id of the contest; -1 is invalid, 0 is new contest request */
	public int id = -1;
	/** New name of the contest - it should always be set to the previous name or the new one */
	public String name = null;
	
	/**
	 * An empty constructor, does nothing
	 */
	public DisciplinePacket() {
		
	}
}
