package cz.salmelu.contests.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import cz.salmelu.contests.model.Contest;
import cz.salmelu.contests.net.Packet;
import cz.salmelu.contests.net.ServerInputException;
import cz.salmelu.contests.net.ServerProcessException;

public class PacketProcesser {
	
	private DataHolder dh;
	
	public PacketProcesser(DataHolder dh) {
		this.dh = dh;
	}

	public boolean processPacket(Packet p, ObjectInputStream in, ObjectOutputStream out) throws IOException {
		if(p == null) {
			writeInputError(out, "Invalid packet received.");
			return false;
		}
		switch(p) {
		case GET_CONTEST:
			if(getContest(in, out))
				return true;
		}
		return false;
	}
	
	private boolean getContest(ObjectInputStream in, ObjectOutputStream out) {
		int contestId;
		try {
			contestId = in.readInt();
		}
		catch (IOException e) {
			writeInputError(out, "Invalid server input, int expected.");
			return false;
		}
		Contest cs = dh.getContest(contestId);
		if(cs == null) {
			writeServerError(out, "No contest with id: " + contestId + " was found.");
			return false;
		}
		try {
			out.writeBoolean(true);
			out.writeObject(cs);
			return true;
		}
		catch(IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private void writeInputError(ObjectOutputStream out, String message) {
		try {
			out.writeBoolean(false);
			out.writeObject(new ServerInputException(message));
		}
		catch (IOException e1) {}
	}
	
	private void writeServerError(ObjectOutputStream out, String message) {
		try {
			out.writeBoolean(false);
			out.writeObject(new ServerProcessException(message));
		}
		catch (IOException e1) {}
	}
	
}
