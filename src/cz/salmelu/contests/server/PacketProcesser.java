package cz.salmelu.contests.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import cz.salmelu.contests.model.*;
import cz.salmelu.contests.net.*;
import cz.salmelu.contests.util.Logger;
import cz.salmelu.contests.util.LoggerSeverity;

/**
 * Processes any obtained packet.<br>
 * It is linked to the {@link DataHolder} which it uses to access and modify the data.<br>
 * Defines a method for each packet returning true, if the packet is processed correctly and false,
 * if it's processed incorrectly. In that case, it also writes a {@link ServerError} to the stream to deliver
 * to the client. 
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
	protected boolean processPacket(PacketOrder p, ObjectInputStream in, ObjectOutputStream out) {
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
			case TEAM_EDIT:
				if(editAddTeam(in, out))
					return true;
				break;
			case TEAM_DELETE:
				if(deleteTeam(in, out))
					return true;
				break;
			case CONTESTANT_EDIT:
				if(editAddContestant(in, out))
					return true;
				break;
			case CONTESTANT_DELETE:
				if(deleteContestant(in, out))
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
		catch (IOException e) {
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
	 * Pushes a {@link java.util.Map} of {@link ContestInfo} objects into out stream.
	 * @param in ObjectInputStream received by the socket
	 * @param out ObjectOutputStream received by the socket
	 * @return true, if the packet was successfully processed, false otherwise
	 * @throws IOException if the streams are corrupted
	 */
	private boolean getAllNames(ObjectInputStream in, ObjectOutputStream out) throws IOException {
		HashMap<String, ContestInfo> names = new HashMap<>();
		for(Entry<Integer, Contest> e : dh.getAllContests().entrySet()) {
			names.put(e.getValue().getName(), e.getValue().getContestInfo());
		}
		out.writeBoolean(true);
		out.writeObject(names);
		return true;
	}
	
	/**
	 * Pushes a {@link Contest} (with all the data inside it) into the out stream.
	 * @param in ObjectInputStream received by the socket
	 * @param out ObjectOutputStream received by the socket
	 * @return true, if the packet was successfully processed, false otherwise
	 * @throws IOException if the streams are corrupted
	 */
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
	
	/**
	 * Processes an update/edit contest packet.
	 * @param in ObjectInputStream received by the socket
	 * @param out ObjectOutputStream received by the socket
	 * @return true, if the packet was successfully processed, false otherwise
	 * @throws ClassNotFoundException when the classes couldn't be loaded
	 * @throws IOException if the streams are corrupted
	 */
	private boolean editAddContest(ObjectInputStream in, ObjectOutputStream out) throws ClassNotFoundException, IOException {
		PacketContest packet = (PacketContest) in.readObject();
		if(packet.id == -1 || packet.name == null || packet.name.equals("")) {
			writeServerError(out, ServerError.InvalidInput);
			return false;
		}
		if(packet.id == 0) {
			Contest cs = new Contest(packet.name);
			dh.addContest(cs);
			out.writeBoolean(true);
			return true;
		}
		else {
			if(dh.getContest(packet.id) == null) {
				writeServerError(out, ServerError.ContestNotFound);
				return false;
			}
			Contest cs = dh.getContest(packet.id);
			cs.setName(packet.name);
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
		if(!dh.deleteContest(conId)) {
			writeServerError(out, ServerError.ContestNotFound);
			return false;
		}
		out.writeBoolean(true);
		return true;
	}

	/**
	 * Processes an update/edit discipline packet.
	 * @param in ObjectInputStream received by the socket
	 * @param out ObjectOutputStream received by the socket
	 * @return true, if the packet was successfully processed, false otherwise
	 * @throws ClassNotFoundException when the classes couldn't be loaded
	 * @throws IOException if the streams are corrupted
	 */
	private boolean editAddDiscipline(ObjectInputStream in, ObjectOutputStream out) throws ClassNotFoundException, IOException {
		PacketDiscipline packet = (PacketDiscipline) in.readObject();
		if(packet.id == -1 || packet.name == null || packet.name.equals("") || packet.conId == -1) {
			writeServerError(out, ServerError.InvalidInput);
			return false;
		}
		if(packet.id == 0) {
			Discipline d = new Discipline(packet.name);
			if(!dh.addDiscipline(packet.conId, d)) {
				writeServerError(out, ServerError.InvalidDataState);
				return false;
			}
			out.writeBoolean(true);
			return true;
		}
		else {
			Contest cs = dh.getContest(packet.conId);
			if(cs == null) {
				writeServerError(out, ServerError.ContestNotFound);
				return false;
			}
			Discipline d = cs.getDiscipline(packet.id);
			if(d == null) {
				writeServerError(out, ServerError.InvalidDataState);
				return false;
			}
			d.setName(packet.name);
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
		if(!dh.deleteDiscipline(conId, discId)) {
			writeServerError(out, ServerError.InvalidDataState);
			return false;
		}
		out.writeBoolean(true);
		return true;
	}

	/**
	 * Processes an update/edit team category packet.
	 * @param in ObjectInputStream received by the socket
	 * @param out ObjectOutputStream received by the socket
	 * @return true, if the packet was successfully processed, false otherwise
	 * @throws ClassNotFoundException when the classes couldn't be loaded
	 * @throws IOException if the streams are corrupted
	 */
	private boolean editAddTeamCategory(ObjectInputStream in, ObjectOutputStream out) throws ClassNotFoundException, IOException {
		PacketTeamCategory packet = (PacketTeamCategory) in.readObject();
		if(packet.id == -1 || packet.name.equals("") || packet.name == null || packet.conId == -1 || packet.sm == null) {
			writeServerError(out, ServerError.InvalidInput);
			return false;
		}
		if(packet.id == 0) {
			TeamCategory tc = new TeamCategory(packet.name, packet.sm);
			if(!dh.addTeamCategory(packet.conId, tc)) {
				writeServerError(out, ServerError.InvalidDataState);
				return false;
			}
			out.writeBoolean(true);
			return true;
		}
		else {
			if(dh.getContest(packet.conId) == null) {
				writeServerError(out, ServerError.ContestNotFound);
				return false;
			}
			Contest cs = dh.getContest(packet.conId);
			TeamCategory tc = cs.getTeamCategory(packet.id);
			if(tc == null) {
				writeServerError(out, ServerError.InvalidDataState);
				return false;
			}
			tc.setName(packet.name);
			tc.setScoreMode(packet.sm);
			out.writeBoolean(true);
			return true;
		}
	}
	
	/**
	 * Processes a delete team category request.<br>
	 * <b>Warning:</b> Removes all the teams in the category!
	 * @param in ObjectInputStream received by the socket
	 * @param out ObjectOutputStream received by the socket
	 * @return true, if the packet was successfully processed, false otherwise
	 * @throws IOException if the streams are corrupted
	 */
	private boolean deleteTeamCategory(ObjectInputStream in, ObjectOutputStream out) throws IOException {
		int conId = in.readInt();
		int tcId = in.readInt();
		if(!dh.deleteTeamCategory(conId, tcId)) {
			writeServerError(out, ServerError.InvalidDataState);
			return false;
		}
		out.writeBoolean(true);
		return true;
	}

	/**
	 * Processes an update/edit team category packet.
	 * @param in ObjectInputStream received by the socket
	 * @param out ObjectOutputStream received by the socket
	 * @return true, if the packet was successfully processed, false otherwise
	 * @throws ClassNotFoundException when the classes couldn't be loaded
	 * @throws IOException if the streams are corrupted
	 */
	private boolean editAddCategory(ObjectInputStream in, ObjectOutputStream out) throws ClassNotFoundException, IOException {
		PacketCategory packet = (PacketCategory) in.readObject();
		if(packet.id == -1 || packet.name == null || packet.name.equals("") || packet.conId == -1 || packet.disciplines == null) {
			writeServerError(out, ServerError.InvalidInput);
			return false;
		}
		Contest cs = dh.getContest(packet.conId);
		if(cs == null) {
			writeServerError(out, ServerError.ContestNotFound);
			return false;
		}		
		if(packet.id == 0) {
			Category cat = new Category(packet.name);
			for(int discId : packet.disciplines) {
				if(!cs.hasDiscipline(discId)) {
					writeServerError(out, ServerError.InvalidDataState);
					return false;
				}
				cat.addDiscipline(cs.getDiscipline(discId));
			}
			if(!dh.addCategory(packet.conId, cat)) {
				writeServerError(out, ServerError.InvalidDataState);
				return false;
			}
			out.writeBoolean(true);
			return true;
		}
		else {
			Category cat = cs.getCategory(packet.id);
			if(cat == null) {
				writeServerError(out, ServerError.InvalidDataState);
				return false;
			}
			cat.setName(packet.name);
			ArrayList<Discipline> toAdd = new ArrayList<>();
			for(int discId : packet.disciplines) {
				if(!cs.hasDiscipline(discId)) {
					writeServerError(out, ServerError.InvalidDataState);
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
				if(!packet.disciplines.contains(d.getId())) {
					itd.remove();
				}
			}
			out.writeBoolean(true);
			return true;
		}
	}
	
	/**
	 * Processes a delete category request.<br>
	 * <b>Warning:</b> Removes all the contestants in the category!
	 * @param in ObjectInputStream received by the socket
	 * @param out ObjectOutputStream received by the socket
	 * @return true, if the packet was successfully processed, false otherwise
	 * @throws IOException if the streams are corrupted
	 */
	private boolean deleteCategory(ObjectInputStream in, ObjectOutputStream out) throws IOException {
		int conId = in.readInt();
		int catId = in.readInt();
		if(!dh.deleteCategory(conId, catId)) {
			writeServerError(out, ServerError.InvalidDataState);
			return false;
		}
		out.writeBoolean(true);
		return true;
	}
	
	/**
	 * Processes an update/edit team packet.
	 * @param in ObjectInputStream received by the socket
	 * @param out ObjectOutputStream received by the socket
	 * @return true, if the packet was successfully processed, false otherwise
	 * @throws ClassNotFoundException when the classes couldn't be loaded
	 * @throws IOException if the streams are corrupted
	 */
	private boolean editAddTeam(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
		PacketTeam packet = (PacketTeam) in.readObject();
		if(packet.id == -1 || packet.name == null || packet.name.equals("") || packet.conId == -1 || packet.tcId == -1) {
			writeServerError(out, ServerError.InvalidInput);
			return false;
		}
		Contest cs = dh.getContest(packet.conId);
		if(cs == null) {
			writeServerError(out, ServerError.ContestNotFound);
			return false;
		}
		if(packet.id == 0) {
			Team t = new Team(packet.name, packet.bonus);
			TeamCategory tc = cs.getTeamCategory(packet.tcId);
			if(tc == null) {
				writeServerError(out, ServerError.InvalidDataState);
				return false;
			}
			if(!dh.addTeam(packet.conId, tc, t)) {
				writeServerError(out, ServerError.InvalidDataState);
				return false;
			}
			out.writeBoolean(true);
			return true;
		}
		else {
			Team t = cs.getTeam(packet.oldTcId, packet.id);
			TeamCategory tc = cs.getTeamCategory(packet.tcId);
			if(t == null || tc == null) {
				writeServerError(out, ServerError.InvalidDataState);
				return false;
			}
			t.setName(packet.name);
			t.setBonus(packet.bonus);
			if(t.getCategory() != tc) {
				cs.changeTeamCategory(t, tc);
			}
			out.writeBoolean(true);
			return true;
		}
	}
	
	/**
	 * Processes a delete team request.
	 * @param in ObjectInputStream received by the socket
	 * @param out ObjectOutputStream received by the socket
	 * @return true, if the packet was successfully processed, false otherwise
	 * @throws IOException if the streams are corrupted
	 */
	private boolean deleteTeam(ObjectInputStream in, ObjectOutputStream out) throws IOException {
		int conId = in.readInt();
		int tcId = in.readInt();
		int teamId = in.readInt();
		if(!dh.deleteTeam(conId, tcId, teamId)) {
			writeServerError(out, ServerError.InvalidDataState);
			return false;
		}
		out.writeBoolean(true);
		return true;
	}
	
	/**
	 * Processes an update/edit contestant packet.
	 * @param in ObjectInputStream received by the socket
	 * @param out ObjectOutputStream received by the socket
	 * @return true, if the packet was successfully processed, false otherwise
	 * @throws ClassNotFoundException when the classes couldn't be loaded
	 * @throws IOException if the streams are corrupted
	 */
	private boolean editAddContestant(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
		PacketContestant packet = (PacketContestant) in.readObject();
		if(packet.id == -1 || packet.fName == null || packet.fName.equals("") || packet.lName == null || packet.lName.equals("") || 
				packet.conId == -1 || packet.catId == -1 || packet.teamId == -1) {
			writeServerError(out, ServerError.InvalidInput);
			return false;
		}
		Contest cs = dh.getContest(packet.conId);
		if(dh.getContest(packet.conId) == null) {
			writeServerError(out, ServerError.ContestNotFound);
			return false;
		}
		if(packet.id == 0) {
			TeamContestant tcs = new TeamContestant(packet.fName, packet.lName);
			tcs.setBonus(packet.bonus);
			Category cat = cs.getCategory(packet.catId);
			if(cat == null) {
				writeServerError(out, ServerError.InvalidDataState);
				return false;
			}
			if(packet.teamId != 0) {
				Team t = cs.getTeam(packet.tcId, packet.teamId);
				if(t == null) {
					writeServerError(out, ServerError.InvalidDataState);
					return false;
				}
				t.addContestant(tcs);
			}
			tcs.setCategory(cat);
			if(!dh.addContestant(packet.conId, cat, tcs)) {
				writeServerError(out, ServerError.InvalidDataState);
				return false;
			}
			out.writeBoolean(true);
			return true;
		}
		else {
			Contestant cst = cs.getContestant(packet.oldCatId, packet.id);
			if(cst == null) {
				writeServerError(out, ServerError.InvalidDataState);
				return false;
			}
			if(cst.getCategory().getId() != packet.catId) {
				Category cat = cs.getCategory(packet.catId);
				cs.changeContestantCategory(cst, cat);
			}
			cst.setFirstName(packet.fName);
			cst.setLastName(packet.lName);
			if(cst instanceof TeamContestant) {
				TeamContestant tcs = (TeamContestant) cst;
				tcs.setBonus(packet.bonus);
				if(packet.teamId == 0 && tcs.getTeam() != null) {
					tcs.getTeam().removeContestant(tcs);
				}
				else if(packet.teamId > 0 && (tcs.getTeam() == null || tcs.getTeam().getId() != packet.teamId)) {
					Team t = cs.getTeam(packet.tcId, packet.teamId);
					if(t != null) {
						if(tcs.getTeam() == null) {
							t.addContestant(tcs);
						}
						else {
							tcs.getTeam().removeContestant(tcs);
							t.addContestant(tcs);
						}
					}
				}
			}
			out.writeBoolean(true);
			return true;
		}
	}
	
	/**
	 * Processes a delete contestant request.
	 * @param in ObjectInputStream received by the socket
	 * @param out ObjectOutputStream received by the socket
	 * @return true, if the packet was successfully processed, false otherwise
	 * @throws IOException if the streams are corrupted
	 */
	private boolean deleteContestant(ObjectInputStream in, ObjectOutputStream out) throws IOException {
		int conId = in.readInt();
		int catId = in.readInt();
		int id = in.readInt();
		if(!dh.deleteContestant(conId, catId, id)) {
			writeServerError(out, ServerError.InvalidDataState);
			return false;
		}
		out.writeBoolean(true);
		return true;
	}
	
	/**
	 * Processes an a packet for updating contestants' scores.
	 * @param in ObjectInputStream received by the socket
	 * @param out ObjectOutputStream received by the socket
	 * @return true, if the packet was successfully processed, false otherwise
	 * @throws ClassNotFoundException when the classes couldn't be loaded
	 * @throws IOException if the streams are corrupted
	 */
	private boolean updateScore(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
		int conId = in.readInt();
		int size = in.readInt();
		Contest con = dh.getContest(conId);
		if(con == null) {
			writeServerError(out, ServerError.ContestNotFound);
			return false;
		}
		for(int i=0; i<size; i++) {
			PacketUpdateScore usp = (PacketUpdateScore) in.readObject();
			if(!dh.updateScore(con, usp.catId, usp.conId, usp.discId, usp.score)) {
				writeServerError(out, ServerError.InvalidDataState);
				dh.clearUpdateScores();
				return false;
			}
		}
		dh.commitUpdateScores();
		out.writeBoolean(true);
		return true;
	}

	/**
	 * Writes a {@link ServerError} to the output stream to be delivered to client. 
	 * @param out stream to write the error to
	 * @param message message to be sent to client
	 */
	private void writeServerError(ObjectOutputStream out, ServerError message) {
		try {
			out.writeBoolean(false);
			Logger.getInstance().log("PacketOrder processing error: " + message, LoggerSeverity.VERBOSE);
			out.writeObject(message);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}