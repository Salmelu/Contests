package cz.salmelu.contests.net;

import java.util.HashMap;
import java.util.Map;

public enum Packet {
	CONTEST_GET((byte) 0x10), // Get a contest by Id (contest id)
	CONTEST_ADD((byte) 0x11), // Add a new contest (string name)
	TCATEGORY_GET((byte) 0x20), // Get a team category by Id (category id, contest id)
	TCATEGORY_ADD((byte) 0x21), // Add a team category (string name, scoremode, contest id)
	TCATEGORY_EDIT_NAME((byte) 0x22), // Edit category name (string name, category id, contest id)
	TCATEGORY_EDIT_MODE((byte) 0x23), // Edit category score mode (scoremode, category id, contest id)
	TEAM_GET((byte) 0x30),	// Get a team by Id (team id, category id, contest id)
	TEAM_ADD((byte) 0x31),	// Add a new team (string name, double bonus, category id, contest id)
	TEAM_EDIT_NAME((byte) 0x32),	// Edit a team name (string name, team id, category id, contest id)
	TEAM_EDIT_BONUS((byte) 0x33),	// Edit a team bonus (double bonus, team id, category id, contest id)	
	TEAM_JOIN_CONTESTANT((byte) 0x34),	// Add an existing contestant to team
	TEAM_LEAVE_CONTESTANT((byte) 0x35);	// Remove an existing contestant from a team
	//TEAM_ADD_CONTESTANT((byte) 0x34);
	
	private byte order;
	private static Map<Byte, Packet> packetList;
	
	static {
		packetList = new HashMap<>();
		for(Packet p : Packet.values()) {
			packetList.put(p.order, p);
		}
	}
	
	private Packet(byte order) {
		this.order = order;
	}
	
	public static Packet getPacket(byte order) {
		if(packetList.containsKey(order)) {
			return packetList.get(order);
		}
		return null;
	}
}
