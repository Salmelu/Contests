package cz.salmelu.contests.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import cz.salmelu.contests.model.*;
import cz.salmelu.contests.net.CategoryPacket;
import cz.salmelu.contests.net.ContestPacket;
import cz.salmelu.contests.net.DisciplinePacket;
import cz.salmelu.contests.net.Packet;
import cz.salmelu.contests.net.ServerError;
import cz.salmelu.contests.net.TeamCategoryPacket;
import cz.salmelu.contests.net.UpdateScorePacket;
import cz.salmelu.contests.util.Logger;
import cz.salmelu.contests.util.LoggerSeverity;

/**
 * Class dealing with all the packets and the actions done by them
 * @author salmelu
 */
class PacketProcesser {
	
	/** Associated data holder for accessing the server data */
	private DataHolder dh;
	
	/** 
	 * Constructs packet processer, which will use the supplied data holder.
	 * @param dh data holder to be used
	 */
	protected PacketProcesser(DataHolder dh) {
		this.dh = dh;
	}

	/**
	 * Processes a supplied packet. 
	 * @param p processed packet
	 * @param in ObjectInputStream received by the socket
	 * @param out ObjectOutputStream received by the socket
	 * @return true, if the packet was successfully processed, false otherwise
	 */
	protected boolean processPacket(Packet p, ObjectInputStream in, ObjectOutputStream out) {
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
			case CONTEST_EDIT:
				if(editAddContest(in, out))
					return true;
				break;
			case CONTEST_DELETE:
				if(deleteContest(in, out))
					return true;
				break;
			case DISCIPLINE_EDIT:
				if(editAddDiscipline(in, out)) 
					return true;
				break;
			case DISCIPLINE_DELETE:
				if(deleteDiscipline(in, out))
					return true;
				break;
			case TCATEGORY_EDIT:
				if(editAddTeamCategory(in, out))
					return true;
				break;
			case TCATEGORY_DELETE:
				if(deleteTeamCategory(in, out))
					return true;
				break;
			case CATEGORY_EDIT:
				if(editAddCategory(in, out))
					return true;
				break;
			case CATEGORY_DELETE:
				if(deleteCategory(in, out))
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
			case SCORE_UPDATE:
				if(updateScore(in, out))
					return true;
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
	
	/**
	 * Pushes a Map of ContestInfo objects into out stream
	 * @param in ObjectInputStream received by the socket
	 * @param out ObjectOutputStream received by the socket
	 * @return true, if the packet was successfully processed, false otherwise
	 * @throws IOException if the streams are corrupted
	 */
	private boolean getAllNames(ObjectInputStream in, ObjectOutputStream out) throws IOException {
		HashMap<String, ContestInfo> names = new HashMap<>();
		if(!dh.lock()) {
			writeServerError(out, ServerError.UnableToLock);
			return false;
		}
		for(Entry<Integer, Contest> e : dh.getAllContests().entrySet()) {
			names.put(e.getValue().getName(), e.getValue().getContestInfo());
		}
		out.writeBoolean(true);
		out.writeObject(names);
		dh.unlock();
		return true;
	}
	
	/**
	 * Pushes a Contest (with all the data inside it) into the out stream
	 * @param in ObjectInputStream received by the socket
	 * @param out ObjectOutputStream received by the socket
	 * @return true, if the packet was successfully processed, false otherwise
	 * @throws IOException if the streams are corrupted
	 */
	private boolean getContest(ObjectInputStream in, ObjectOutputStream out) throws IOException {
		int contestId;
		contestId = in.readInt();
		if(!dh.lock()) {
			writeServerError(out, ServerError.UnableToLock);
			return false;
		}
		Contest cs = dh.getContest(contestId);
		if(cs == null) {
			writeServerError(out, ServerError.ContestNotFound);
			dh.unlock();
			return false;
		}
		out.writeBoolean(true);
		out.writeObject(cs);
		dh.unlock();
		return true;
	}
	
	/**
	 * Processes an update/edit contest packet
	 * @param in ObjectInputStream received by the socket
	 * @param out ObjectOutputStream received by the socket
	 * @return true, if the packet was successfully processed, false otherwise
	 * @throws ClassNotFoundException when the classes couldn't be loaded
	 * @throws IOException if the streams are corrupted
	 */
	private boolean editAddContest(ObjectInputStream in, ObjectOutputStream out) throws ClassNotFoundException, IOException {
		ContestPacket cp = (ContestPacket) in.readObject();
		if(cp.id == -1 || cp.name == null || cp.name == "") {
			writeServerError(out, ServerError.InvalidInput);
			return false;
		}
		if(cp.id == 0) {
			Contest cs = new Contest(cp.name);
			if(!dh.lock()) {
				writeServerError(out, ServerError.UnableToLock);
				return false;
			}
			dh.addContest(cs);
			dh.unlock();
			out.writeBoolean(true);
			return true;
		}
		else {
			if(!dh.lock()) {
				writeServerError(out, ServerError.UnableToLock);
				return false;
			}
			if(dh.getContest(cp.id) == null) {
				writeServerError(out, ServerError.ContestNotFound);
				return false;
			}
			Contest cs = dh.getContest(cp.id);
			cs.setName(cp.name);
			dh.unlock();
			out.writeBoolean(true);
			return true;
		}
	}
	
	/**
	 * Processes a delete contest request.
	 * @param in ObjectInputStream received by the socket
	 * @param out ObjectOutputStream received by the socket
	 * @return true, if the packet was successfully processed, false otherwise
	 * @throws IOException if the streams are corrupted
	 */
	private boolean deleteContest(ObjectInputStream in, ObjectOutputStream out) throws IOException {
		int conId = in.readInt();
		if(!dh.lock()) {
			writeServerError(out, ServerError.UnableToLock);
			return false;
		}
		if(!dh.deleteContest(conId)) {
			writeServerError(out, ServerError.ContestNotFound);
			dh.unlock();
			return false;
		}
		dh.unlock();
		out.writeBoolean(true);
		return true;
	}

	/**
	 * Processes an update/edit discipline packet
	 * @param in ObjectInputStream received by the socket
	 * @param out ObjectOutputStream received by the socket
	 * @return true, if the packet was successfully processed, false otherwise
	 * @throws ClassNotFoundException when the classes couldn't be loaded
	 * @throws IOException if the streams are corrupted
	 */
	private boolean editAddDiscipline(ObjectInputStream in, ObjectOutputStream out) throws ClassNotFoundException, IOException {
		DisciplinePacket dp = (DisciplinePacket) in.readObject();
		if(dp.id == -1 || dp.name == null || dp.name == "" || dp.conId == -1) {
			writeServerError(out, ServerError.InvalidInput);
			return false;
		}
		if(dp.id == 0) {
			Discipline d = new Discipline(dp.name);
			if(!dh.lock()) {
				writeServerError(out, ServerError.UnableToLock);
				return false;
			}
			if(!dh.addDiscipline(dp.conId, d)) {
				writeServerError(out, ServerError.InvalidDataState);
				dh.unlock();
				return false;
			}
			dh.unlock();
			out.writeBoolean(true);
			return true;
		}
		else {
			if(!dh.lock()) {
				writeServerError(out, ServerError.UnableToLock);
				return false;
			}
			if(dh.getContest(dp.conId) == null) {
				writeServerError(out, ServerError.ContestNotFound);
				return false;
			}
			Contest cs = dh.getContest(dp.conId);
			Discipline d = cs.getDiscipline(dp.id);
			if(d == null) {
				writeServerError(out, ServerError.InvalidDataState);
				return false;
			}
			d.setName(dp.name);
			dh.unlock();
			out.writeBoolean(true);
			return true;
		}
	}
	
	/**
	 * Processes a delete discipline request.
	 * @param in ObjectInputStream received by the socket
	 * @param out ObjectOutputStream received by the socket
	 * @return true, if the packet was successfully processed, false otherwise
	 * @throws IOException if the streams are corrupted
	 */
	private boolean deleteDiscipline(ObjectInputStream in, ObjectOutputStream out) throws IOException {
		int conId = in.readInt();
		int discId = in.readInt();
		if(!dh.lock()) {
			writeServerError(out, ServerError.UnableToLock);
			return false;
		}
		if(!dh.deleteDiscipline(conId, discId)) {
			writeServerError(out, ServerError.InvalidDataState);
			dh.unlock();
			return false;
		}
		dh.unlock();
		out.writeBoolean(true);
		return true;
	}

	/**
	 * Processes an update/edit team category packet
	 * @param in ObjectInputStream received by the socket
	 * @param out ObjectOutputStream received by the socket
	 * @return true, if the packet was successfully processed, false otherwise
	 * @throws ClassNotFoundException when the classes couldn't be loaded
	 * @throws IOException if the streams are corrupted
	 */
	private boolean editAddTeamCategory(ObjectInputStream in, ObjectOutputStream out) throws ClassNotFoundException, IOException {
		TeamCategoryPacket tcp = (TeamCategoryPacket) in.readObject();
		if(tcp.id == -1 || tcp.name == null || tcp.name == "" || tcp.conId == -1 || tcp.sm == null) {
			writeServerError(out, ServerError.InvalidInput);
			return false;
		}
		if(tcp.id == 0) {
			TeamCategory tc = new TeamCategory(tcp.name, tcp.sm);
			if(!dh.lock()) {
				writeServerError(out, ServerError.UnableToLock);
				return false;
			}
			if(!dh.addTeamCategory(tcp.conId, tc)) {
				writeServerError(out, ServerError.InvalidDataState);
				dh.unlock();
				return false;
			}
			dh.unlock();
			out.writeBoolean(true);
			return true;
		}
		else {
			if(!dh.lock()) {
				writeServerError(out, ServerError.UnableToLock);
				return false;
			}
			if(dh.getContest(tcp.conId) == null) {
				writeServerError(out, ServerError.ContestNotFound);
				return false;
			}
			Contest cs = dh.getContest(tcp.conId);
			TeamCategory tc = cs.getTeamCategory(tcp.id);
			if(tc == null) {
				writeServerError(out, ServerError.InvalidDataState);
				return false;
			}
			tc.setName(tcp.name);
			tc.setScoreMode(tcp.sm);
			dh.unlock();
			out.writeBoolean(true);
			return true;
		}
	}
	
	/**
	 * Processes a delete team category request.
	 * @param in ObjectInputStream received by the socket
	 * @param out ObjectOutputStream received by the socket
	 * @return true, if the packet was successfully processed, false otherwise
	 * @throws IOException if the streams are corrupted
	 */
	private boolean deleteTeamCategory(ObjectInputStream in, ObjectOutputStream out) throws IOException {
		int conId = in.readInt();
		int tcId = in.readInt();
		if(!dh.lock()) {
			writeServerError(out, ServerError.UnableToLock);
			return false;
		}
		if(!dh.deleteTeamCategory(conId, tcId)) {
			writeServerError(out, ServerError.InvalidDataState);
			dh.unlock();
			return false;
		}
		dh.unlock();
		out.writeBoolean(true);
		return true;
	}

	/**
	 * Processes an update/edit team category packet
	 * @param in ObjectInputStream received by the socket
	 * @param out ObjectOutputStream received by the socket
	 * @return true, if the packet was successfully processed, false otherwise
	 * @throws ClassNotFoundException when the classes couldn't be loaded
	 * @throws IOException if the streams are corrupted
	 */
	private boolean editAddCategory(ObjectInputStream in, ObjectOutputStream out) throws ClassNotFoundException, IOException {
		CategoryPacket cp = (CategoryPacket) in.readObject();
		if(cp.id == -1 || cp.name == null || cp.name == "" || cp.conId == -1 || cp.disciplines == null) {
			writeServerError(out, ServerError.InvalidInput);
			return false;
		}
		if(cp.id == 0) {
			Category cat = new Category(cp.name);
			if(!dh.lock()) {
				writeServerError(out, ServerError.UnableToLock);
				return false;
			}
			if(dh.getContest(cp.conId) == null) {
				writeServerError(out, ServerError.ContestNotFound);
				return false;
			}
			Contest cs = dh.getContest(cp.conId);
			for(int discId : cp.disciplines) {
				if(!cs.hasDiscipline(discId)) {
					writeServerError(out, ServerError.InvalidDataState);
					dh.unlock();
					return false;
				}
				cat.addDiscipline(cs.getDiscipline(discId));
			}
			if(!dh.addCategory(cp.conId, cat)) {
				writeServerError(out, ServerError.InvalidDataState);
				dh.unlock();
				return false;
			}
			dh.unlock();
			out.writeBoolean(true);
			return true;
		}
		else {
			if(!dh.lock()) {
				writeServerError(out, ServerError.UnableToLock);
				return false;
			}
			if(dh.getContest(cp.conId) == null) {
				writeServerError(out, ServerError.ContestNotFound);
				return false;
			}
			Contest cs = dh.getContest(cp.conId);
			Category cat = cs.getCategory(cp.id);
			if(cat == null) {
				writeServerError(out, ServerError.InvalidDataState);
				return false;
			}
			cat.setName(cp.name);
			ArrayList<Discipline> toAdd = new ArrayList<>();
			for(int discId : cp.disciplines) {
				if(!cs.hasDiscipline(discId)) {
					writeServerError(out, ServerError.InvalidDataState);
					dh.unlock();
					return false;
				}
				if(!cat.hasDiscipline(cs.getDiscipline(discId))) {
					toAdd.add(cs.getDiscipline(discId));
				}
			}
			cat.getDisciplines().addAll(toAdd);
			// A safe remove method
			for(Iterator<Discipline> itd = cat.getDisciplines().iterator(); itd.hasNext(); ) {
				Discipline d = itd.next();
				if(!cp.disciplines.contains(d.getId())) {
					itd.remove();
				}
			}
			dh.unlock();
			out.writeBoolean(true);
			return true;
		}
	}
	
	/**
	 * Processes a delete category request.
	 * @param in ObjectInputStream received by the socket
	 * @param out ObjectOutputStream received by the socket
	 * @return true, if the packet was successfully processed, false otherwise
	 * @throws IOException if the streams are corrupted
	 */
	private boolean deleteCategory(ObjectInputStream in, ObjectOutputStream out) throws IOException {
		int conId = in.readInt();
		int catId = in.readInt();
		if(!dh.lock()) {
			writeServerError(out, ServerError.UnableToLock);
			return false;
		}
		if(!dh.deleteCategory(conId, catId)) {
			writeServerError(out, ServerError.InvalidDataState);
			dh.unlock();
			return false;
		}
		dh.unlock();
		out.writeBoolean(true);
		return true;
	}
	
	// FIXME: usage & make safe
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
	
	// FIXME: usage & make safe
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
	
	/**
	 * Processes an updateScore packet.
	 * @param in ObjectInputStream received by the socket
	 * @param out ObjectOutputStream received by the socket
	 * @return true, if the packet was successfully processed, false otherwise
	 * @throws ClassNotFoundException when the classes couldn't be loaded
	 * @throws IOException if the streams are corrupted
	 */
	private boolean updateScore(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
		int conId = in.readInt();
		int size = in.readInt();
		if(!dh.lock()) {
			writeServerError(out, ServerError.UnableToLock);
			return false;
		}
		Contest con = dh.getContest(conId);
		if(con == null) {
			writeServerError(out, ServerError.ContestNotFound);
			dh.unlock();
			return false;
		}
		for(int i=0; i<size; i++) {
			UpdateScorePacket usp = (UpdateScorePacket) in.readObject();
			if(!dh.updateScore(con, usp.catId, usp.conId, usp.discId, usp.score)) {
				writeServerError(out, ServerError.InvalidDataState);
				dh.clearUpdateScores();
				dh.unlock();
				return false;
			}
		}
		dh.commitUpdateScores();
		dh.unlock();
		out.writeBoolean(true);
		return true;
	}

	/**
	 * Writes a server error to the out stream. 
	 * @param out stream to write the error to
	 * @param message message to be sent to client
	 */
	private void writeServerError(ObjectOutputStream out, ServerError message) {
		try {
			out.writeBoolean(false);
			Logger.getInstance().log("Packet processing error: " + message, LoggerSeverity.VERBOSE);
			out.writeObject(message);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
