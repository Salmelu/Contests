package cz.salmelu.contests.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map.Entry;

import cz.salmelu.contests.model.*;
import cz.salmelu.contests.net.Packet;
import cz.salmelu.contests.net.ServerError;

public class PacketProcesser {
	
	private DataHolder dh;
	
	public PacketProcesser(DataHolder dh) {
		this.dh = dh;
	}

	public boolean processPacket(Packet p, ObjectInputStream in, ObjectOutputStream out) throws IOException {
		if(p == null) {
			writeServerError(out, ServerError.InvalidPacket);
			return false;
		}
		try {
			switch(p) {
			case ALL_GET_NAMES:
				if(getAllNames(in, out))
					return true;
				break;
			case CONTEST_GET:
				if(getContest(in, out))
					return true;
				break;
			case CONTEST_ADD:
				if(addContest(in, out))
					return true;
				break;
			case TCATEGORY_GET:
				if(getTeamCategory(in, out))
					return true;
				break;
			case TCATEGORY_ADD:
				if(addTeamCategory(in, out))
					return true;
				break;
			case TCATEGORY_EDIT_NAME:
				if(editTeamCategoryName(in, out))
					return true;
				break;
			case TCATEGORY_EDIT_MODE:
				if(editTeamCategoryMode(in, out))
					return true;
				break;
			case TEAM_GET:
				if(getTeam(in, out))
					return true;
				break;
			case TEAM_ADD:
				if(addTeam(in, out))
					return true;
				break;
			case TEAM_EDIT_BONUS:
				break;
			case TEAM_EDIT_NAME:
				break;
			case TEAM_JOIN_CONTESTANT:
				break;
			case TEAM_LEAVE_CONTESTANT:
				break;
			default:
				break;
			}
		}
		catch(IOException e) {
			writeServerError(out, ServerError.InvalidInput);
			return false;
		}
		catch (ClassNotFoundException e) {
			writeServerError(out, ServerError.InvalidInput);
			return false;
		}
		return false;
	}
	
	private boolean getAllNames(ObjectInputStream in, ObjectOutputStream out) throws IOException {
		HashMap<String, ContestInfo> names = new HashMap<>();
		for(Entry<Integer, Contest> e : dh.getAllContests().entrySet()) {
			names.put(e.getValue().getName(), e.getValue().getContestInfo());
		}
		out.writeBoolean(true);
		out.writeObject(names);
		return true;
	}
	
	private boolean getContest(ObjectInputStream in, ObjectOutputStream out) throws IOException {
		int contestId;
		contestId = in.readInt();
		Contest cs = dh.getContest(contestId);
		if(cs == null) {
			writeServerError(out, ServerError.ContestNotFound);
			return false;
		}
		out.writeBoolean(true);
		out.writeObject(cs);
		return true;
	}
	
	private boolean addContest(ObjectInputStream in, ObjectOutputStream out) throws ClassNotFoundException, IOException {
		String contestName;
		contestName = (String) in.readObject();
		Contest cs = new Contest(contestName);
		dh.addContest(cs);
		out.writeBoolean(true);
		return true;
	}
	
	private boolean getTeamCategory(ObjectInputStream in, ObjectOutputStream out) throws IOException {
		int contestId, tcId;
		contestId = in.readInt();
		tcId = in.readInt();
		TeamCategory tc = dh.getTeamCategory(contestId, tcId);
		if(tc == null) {
			writeServerError(out, ServerError.TeamCategoryNotFound);
			return false;
		}
		out.writeBoolean(true);
		out.writeObject(tc);
		return true;
	}
	
	private boolean addTeamCategory(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
		String tcName;
		ScoreMode sm;
		int contestId;
		
		tcName = (String) in.readObject();
		sm = (ScoreMode) in.readObject();
		contestId = in.readInt();
		
		Contest cs = dh.getContest(contestId);
		if(cs == null) {
			writeServerError(out, ServerError.ContestNotFound);
			return false;
		}
		
		TeamCategory tc = new TeamCategory(tcName, sm);
		dh.addTeamCategory(tc, cs);
		out.writeBoolean(true);
		return true;
	}
	
	private boolean editTeamCategoryName(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
		int contestId, tcId;
		String newName;
		contestId = in.readInt();
		tcId = in.readInt();
		newName = (String) in.readObject();
		TeamCategory tc = dh.getTeamCategory(contestId, tcId);
		if(tc == null) {
			writeServerError(out, ServerError.TeamCategoryNotFound);
			return false;
		}
		if(newName == null) {
			writeServerError(out, ServerError.InvalidInput);
			return false;
		}
		tc.setName(newName);
		out.writeBoolean(true);
		return true;
	}
	
	private boolean editTeamCategoryMode(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
		int contestId, tcId;
		ScoreMode sm;
		contestId = in.readInt();
		tcId = in.readInt();
		sm = (ScoreMode) in.readObject();
		TeamCategory tc = dh.getTeamCategory(tcId, contestId);
		if(tc == null) {
			writeServerError(out, ServerError.TeamCategoryNotFound);
			return false;
		}
		if(sm == null) {
			writeServerError(out, ServerError.InvalidInput);
			return false;
		}
		tc.setScoreMode(sm);
		out.writeBoolean(true);
		return true;
	}
	
	private boolean getTeam(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
		int contestId, tcId, teamId;
		teamId = in.readInt();
		tcId = in.readInt();
		contestId = in.readInt();
		Team t = dh.getTeam(teamId, tcId, contestId);
		if(t == null) {
			writeServerError(out, ServerError.TeamNotFound);
			return false;
		}
		out.writeBoolean(true);
		out.writeObject(t);
		return true;
	}
	
	private boolean addTeam(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
		int contestId, tcId;
		String name;
		double bonus;
		
		name = (String) in.readObject();
		bonus = in.readDouble();
		tcId = in.readInt();
		contestId = in.readInt();
		Contest cs = dh.getContest(contestId);
		if(cs == null) {
			writeServerError(out, ServerError.ContestNotFound);
			return false;
		}
		TeamCategory tc = dh.getTeamCategory(contestId, tcId);
		if(tc == null) {
			writeServerError(out, ServerError.TeamCategoryNotFound);
			return false;
		}
		Team t = new Team(name, bonus);
		dh.addTeam(t, tc, cs);
		out.writeBoolean(true);
		return true;
	}

	private void writeServerError(ObjectOutputStream out, ServerError message) {
		try {
			out.writeBoolean(false);
			out.writeObject(message);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
