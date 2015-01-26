package cz.salmelu.contests.net;

import java.util.HashMap;
import java.util.Map;

/**
 * An enumeration of supported packets to be sent to server.<br>
 * First byte sent to the server should always be one of the packet codes.
 * @author salmelu
 */
public enum PacketOrder {
	ALL_GET_NAMES((byte) 0x00, false), // Gets a full map of contests of <name, contestinfo>
	CONTEST_GET((byte) 0x10, false), // Get a contest by Id (contest id)
	CONTEST_EDIT((byte) 0x11), // Edit/add a contest (PacketContest)
	CONTEST_DELETE((byte) 0x12), // Add a new contest (string name)
	DISCIPLINE_DELETE((byte) 0x20), // Deletes a discipline (contest id, disc id)
	DISCIPLINE_EDIT((byte) 0x21), // Edits/Adds a new discipline
	TCATEGORY_DELETE((byte) 0x25), // Deletes a team category (contest id, category id)
	TCATEGORY_EDIT((byte) 0x26), // Edits/Adds a new team category
	CATEGORY_DELETE((byte) 0x30), // Deletes a category (contest id, category id)
	CATEGORY_EDIT((byte) 0x31), // Edits/Adds a new category
	TEAM_DELETE((byte) 0x40), // Deletes a team (contest id, team category id, team id)
	TEAM_EDIT((byte) 0x41), // Edits/Adds a new team
	CONTESTANT_DELETE((byte) 0x50), // Deletes a contestant (contest id, category id, contestant id)
	CONTESTANT_EDIT((byte) 0x51), // Edits/Adds a contestant
	SCORE_UPDATE((byte) 0x60); // Updates score (double, size, UpdateScorePackets)
	
	/** Byte value of the packet */
	private byte order;
	/** True if the order changes server data. Used for saving purposes */
	private boolean changing = true;
	/** Map of all the packet used for searching purposes */
	private static Map<Byte, PacketOrder> packetList;
	
	static {
		packetList = new HashMap<>();
		for(PacketOrder p : PacketOrder.values()) {
			packetList.put(p.order, p);
		}
	}
	
	/**
	 * Creates new changing packet with an associated byte value.
	 * @param order the byte value of the packet
	 */
	private PacketOrder(byte order) {
		this(order, true);
	}
	
	/**
	 * Creates a new packet with an associated byte value.
	 * @param order the byte value of the packet
	 * @param changing marks if the packet changes anything
	 */
	private PacketOrder(byte order, boolean changing) {
		this.order = order;
		this.changing = changing;
	}
	
	/**
	 * Gets a packet by the supplied byte value.
	 * @param order byte value received
	 * @return corresponding packet or null if there is no packet for that value
	 */
	public static PacketOrder getPacket(byte order) {
		if(packetList.containsKey(order)) {
			return packetList.get(order);
		}
		return null;
	}
	
	/**
	 * Converts a packet to its byte value.
	 * @return byte representing the packet
	 */
	public byte toByte() {
		return order;
	}
	
	/**
	 * Checks if this order changes server data structure
	 * @return true, if the order changes server data, false otherwise
	 */
	public boolean changing() {
		return changing;
	}
}
