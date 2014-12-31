package cz.salmelu.contests.net;

import java.io.Serializable;

/**
 * Used for sending category field updates to server 
 * @author salmelu
 */
public class PacketTeam implements Serializable, Packet {

	private static final long serialVersionUID = 4650829858344324359L;
	/** Parent contest id; -1 is invalid */
	public int conId = -1;
	/** Unique id of the team; -1 is invalid, 0 is new team request */
	public int id = -1;
	/** New name of the team - it should always be set to the previous name or the new one */
	public String name = null;
	/** Team category id; -1 is invalid*/
	public int tcId = -1;
	/** Old team category id; -1 is invalid; used only in update packets to mark old team id for lookup*/
	public int oldTcId = -1;
	/** Team bonus */
	public double bonus = 0;
	
	/**
	 * An empty constructor, does nothing
	 */
	public PacketTeam() {
	}
}
