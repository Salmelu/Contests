package cz.salmelu.contests.net;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Used for sending category field updates to server 
 * @author salmelu
 */
public class PacketCategory implements Serializable, Packet {

	private static final long serialVersionUID = 4650829858344324359L;
	/** Parent contest id; -1 is invalid */
	public int conId = -1;
	/** Unique id of the category; -1 is invalid, 0 is new category request */
	public int id = -1;
	/** New name of the category - it should always be set to the previous name or the new one */
	public String name = null;
	/** List of associated disciplines */
	public List<Integer> disciplines = null;
	
	/**
	 * An empty constructor, does nothing
	 */
	public PacketCategory() {
		disciplines = new ArrayList<>();
	}
}
