package cz.salmelu.contests.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import cz.salmelu.contests.model.*;

/**
 * A class used by server to hold all contest data and perform all major changes to the structures.
 * Also implements lock to allow thread-safe data changes
 * @author salmelu
 */
class DataHolder {
	
	/** Holds all the contests */
	private Map<Integer, Contest> contests;
	/** Lock implementation */
	private boolean locked = false;
	/** Used for updating scores, holds the temporary data */
	private Map<Contestant,Map<Discipline, Double>> updateScore;
	
	/** 
	 * Creates a new empty holder
	 */
	protected DataHolder() {
		this.contests = new TreeMap<>();
	}
	
	/**
	 * Gets a map of all contests on the server
	 * @return map of all contests indexed by their id
	 */
	protected Map<Integer, Contest> getAllContests() {
		return contests;
	}
	
	/**
	 * Replaces the map of contests with a new (loaded) one
	 * @param contests map of contests indexed by their id
	 */
	protected void replaceContests(Map<Integer, Contest> contests) {
		this.contests = contests;
	}
	
	/**
	 * Gets a contest by its id
	 * @param id an id of the contest
	 * @return the contest, or null, if it doesn't exist
	 */
	protected Contest getContest(int id) {
		if(contests.containsKey(id))
			return contests.get(id);
		return null;
	}
	
	/**
	 * Adds a new contest to the server
	 * @param cs added contest
	 */
	protected void addContest(Contest cs) {
		this.contests.put(cs.getId(), cs);
	}
	
	/**
	 * Removes a contest from the server
	 * @param id id of the removed contest
	 * @return true, if the contest was removed, false, if it doesn't exist
	 */
	protected boolean deleteContest(int id) {
		if(!contests.containsKey(id))
			return false;
		contests.remove(id);
		return true;
	}
	
	/**
	 * Adds a new discipline to the contest
	 * @param conId id of the affected contest
	 * @param d added discipline
	 * @return true, if the discipline was added, false, if it couldn't be added
	 */
	protected boolean addDiscipline(int conId, Discipline d) {
		Contest cs = getContest(conId);
		if(cs == null) {
			return false;
		}
		cs.addDiscipline(d);
		return true;
	}
	
	/**
	 * Removes a discipline from the contest
	 * @param conId id of the affected contest
	 * @param discId id of the removed discipline
	 * @return true, if something was removed, false, if the discipline or the contest was not found
	 */
	protected boolean deleteDiscipline(int conId, int discId) {
		Contest cs = getContest(conId);
		if(cs == null) {
			return false;
		}
		if(!cs.hasDiscipline(discId)) {
			return false;
		}
		cs.removeDiscipline(discId);
		return true;
	}
	
	/**
	 * Adds a new team category to the contest
	 * @param conId id of the affected contest
	 * @param tc added team category
	 * @return true, if the contest was found and category added, false otherwise
	 */
	protected boolean addTeamCategory(int conId, TeamCategory tc) {
		Contest cs = getContest(conId);
		if(cs == null) {
			return false;
		}
		cs.addTeamCategory(tc);
		return true;
	}
	
	/**
	 * Removes a team category from the contest. <br>
	 * <b>Warning:</b> Removes all the teams in the category!
	 * @param conId id of the affected contest
	 * @param tcId id of the removed team category
	 * @return true, if the category was removed, false, if the category or the contest was not found
	 */
	protected boolean deleteTeamCategory(int conId, int tcId) {
		Contest cs = getContest(conId);
		if(cs == null) {
			return false;
		}
		if(!cs.hasTeamCategory(tcId)) {
			return false;
		}
		cs.removeTeamCategory(tcId);
		return true;
	}
	
	/**
	 * Adds a new category to the contest
	 * @param conId id of the affected contest
	 * @param cat added category
	 * @return true, if the contest was found and category added, false otherwise
	 */
	protected boolean addCategory(int conId, Category cat) {
		Contest cs = getContest(conId);
		if(cs == null) {
			return false;
		}
		cs.addCategory(cat);
		return true;
	}

	/**
	 * Removes a category from the contest <br>
	 * <b>Warning:</b> Removes all the contestants in the category!
	 * @param conId id of the affected contest
	 * @param catId id of the removed category
	 * @return true, if the category was deleted, false, if the category or the contest was not found
	 */
	protected boolean deleteCategory(int conId, int catId) {
		Contest cs = getContest(conId);
		if(cs == null) {
			return false;
		}
		if(!cs.hasCategory(catId)) {
			return false;
		}
		cs.removeCategory(catId);
		return true;
	}
	
	protected Team getTeam(int teamId, int tcId, int contestId) {
		Contest cs = contests.get(contestId);
		if(cs == null)
			return null;
		TeamCategory tc = cs.getTeamCategories().get(tcId);
		if(tc == null)
			return null;
		Team t = cs.getTeams(tc).get(teamId);
		return t;
	}
	
	protected void addTeam(Team t, TeamCategory tc, Contest cs) {
		cs.addTeam(tc, t);
		t.setCategory(tc);
	}
	
	/**
	 * Adds an update score task to the queue. The queue is not processes until a commit action is called.
	 * If there happens to be an invalid update, method clearUpdateScores() should be called to clear the queue.
	 * If every update score action is valid, the queue is processed by commitUpdateScores() action.
	 * @param con contest id
	 * @param catId category id
	 * @param conId contestant id
	 * @param discId discipline id
	 * @param score new score of the contestant
	 * @return true, if the request is valid, false otherwise
	 */
	protected boolean updateScore(Contest con, int catId, int conId, int discId, double score) {
		if(updateScore == null) {
			updateScore = new HashMap<>();
		}
		Category cat = con.getCategories().get(catId);
		if(cat == null) {
			return false;
		}
		Contestant cs = con.getContestants(cat).get(conId);
		Discipline disc = con.getDisciplines().get(discId);
		if(con == null || disc == null) {
			return false;
		}
		if(!updateScore.containsKey(cs)) updateScore.put(cs, new HashMap<>());
		updateScore.get(cs).put(disc, score);
		return true;
	}

	/**
	 * Commits and clears the queue. Does all the changes queued by updateScore() method.
	 */
	protected void commitUpdateScores() {
		if(updateScore == null) return;
		for(Entry<Contestant, Map<Discipline, Double>> e1 : updateScore.entrySet()) {
			for(Entry<Discipline, Double> e2 : updateScore.get(e1.getKey()).entrySet()) {
				e1.getKey().setScore(e2.getKey(), e2.getValue());
			}
		}
		updateScore.clear();
	}
	
	/**
	 * Clears the queue without committing the changes.
	 */
	protected void clearUpdateScores() {
		updateScore.clear();
	}
	
	/**
	 * Locks the thread.
	 * @return true, if locking was successful
	 */
	protected synchronized boolean lock() {
		try {
			while(locked) {
				this.wait();
			}
		}
		catch (InterruptedException e) {
			return false;
		}
		this.locked = true;
		return true;
	}
	
	/**
	 * Unlocks the holder
	 */
	protected synchronized void unlock() {
		this.locked = false;
		this.notify();
	}
}
