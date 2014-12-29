package cz.salmelu.contests.net;

import java.util.HashMap;
import java.util.Map;

/**
 * An enumeration of supported packets to be sent to server.
 *  First byte sent to the server should always be one of the packet codes.
 * @author salmelu
 */
public enum Packet {
	ALL_GET_NAMES((byte) 0x00), // Gets a full map of contests of <name, contestinfo>
	CONTEST_GET((byte) 0x10), // Get a contest by Id (contest id)
	CONTEST_EDIT((byte) 0x11), // Edit/add a contest (ContestPacket)
	CONTEST_DELETE((byte) 0x12), // Add a new contest (string name)
	TCATEGORY_GET((byte) 0x20), // Get a team category by Id (category id, contest id)
	TCATEGORY_ADD((byte) 0x21), // Add a team category (string name, scoremode, contest id)
	TCATEGORY_EDIT_NAME((byte) 0x22), // Edit category name (string name, category id, contest id)
	TCATEGORY_EDIT_MODE((byte) 0x23), // Edit category score mode (scoremode, category id, contest id)
	TEAM_GET((byte) 0x30),	// Get a team by Id (team id, category id, contest id)
	TEAM_ADD((byte) 0x31),	// Add a new team (string name, double bonus, category id, contest id)
	TEAM_EDIT_NAME((byte) 0x32),	// Edit a team name (string name, team id, category id, contest id)
	TEAM_EDIT_BONUS((byte) 0x33),	// Edit a team bonus (double bonus, team id, category id, contest id)	
	TEAM_JOIN_CONTESTANT((byte) 0x34),	// Add an existing contestant to team
	TEAM_LEAVE_CONTESTANT((byte) 0x35),	// Remove an existing contestant from a team
	//TEAM_ADD_CONTESTANT((byte) 0x34);
	SCORE_UPDATE((byte) 0x50);  // Updates score (double, size, UpdateScorePackets)
	
	/** Byte value of the packet */
	private byte order;
	/** Map of all the packet used for searching purposes */
	private static Map<Byte, Packet> packetList;
	
	static {
		packetList = new HashMap<>();
		for(Packet p : Packet.values()) {
			packetList.put(p.order, p);
		}
	}
	
	/**
	 * Creates new packet 
	 * @param order the byte value of the packet
	 */
	private Packet(byte order) {
		this.order = order;
	}
	
	/**
	 * Gets a packet with the supplied byte value
	 * @param order byte value received
	 * @return corresponding packet or null if there is no packet for that value
	 */
	public static Packet getPacket(byte order) {
		if(packetList.containsKey(order)) {
			return packetList.get(order);
		}
		return null;
	}
	
	/**
	 * Converts a packet to its byte value
	 * @return byte representing the packet
	 */
	public byte toByte() {
		return order;
	}
}
