package cz.salmelu.contests.net;

import java.util.HashMap;
import java.util.Map;

public enum Packet {
	GET_CONTEST((byte) 0x10); // Asking server for a contest
	
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
