package cz.salmelu.contests.net;

import java.io.Serializable;

/**
 * Used for sending contestant field updates to server 
 * @author salmelu
 */
public class PacketContestant implements Serializable, Packet {

	private static final long serialVersionUID = 4650829858344324359L;
	/** Parent contest id; -1 is invalid */
	public int conId = -1;
	/** Unique id of the contestant; -1 is invalid, 0 is new contestant request */
	public int id = -1;
	/** New first name of the contestant - it should always be set to the previous name or the new one */
	public String fName = null;
	/** New last name of the contestant - it should always be set to the previous name or the new one */
	public String lName = null;
	/** Category id; -1 is invalid*/
	public int catId = -1;
	/** Old category id; -1 is invalid; used only in update packets to mark old id for lookup*/
	public int oldCatId = -1;
	/** Team id; -1 is invalid; 0 is null */
	public int teamId = -1;
	/** Team category id; -1 is invalid */
	public int tcId = -1;
	/** Team bonus */
	public double bonus = 0;
	
	/**
	 * An empty constructor, does nothing
	 */
	public PacketContestant() {
		
	}
}
