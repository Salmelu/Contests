package cz.salmelu.contests.net;

public enum Packet {
	GET_CONTEST((byte) 0x10); // Asking server for a contest
	
	byte order;
	
	private Packet(byte order) {
		this.order = order;
	}
}
