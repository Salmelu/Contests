package cz.salmelu.contests.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import cz.salmelu.contests.model.*;

class DataHolder {
	
	private Map<Integer, Contest> contests;
	private boolean locked = false;
	private Map<Contestant,Map<Discipline, Double>> updateScore;
	
	protected DataHolder() {
		this.contests = new TreeMap<>();
	}
	
	protected Map<Integer, Contest> getAllContests() {
		return contests;
	}
	
	protected void replaceContests(Map<Integer, Contest> contests) {
		this.contests = contests;
	}
	
	protected Contest getContest(int id) {
		if(contests.containsKey(id))
			return contests.get(id);
		return null;
	}
	
	protected void addContest(Contest cs) {
		this.contests.put(cs.getId(), cs);
	}
	
	protected TeamCategory getTeamCategory(int tcId, int contestId) {
		Contest cs = contests.get(contestId);
		if(cs == null)
			return null;
		return contests.get(contestId).getTeamCategories().get(tcId);
	}
	
	protected void addTeamCategory(TeamCategory tc, Contest c) {
		c.addTeamCategory(tc);
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

	protected void commitUpdateScores() {
		if(updateScore == null) return;
		for(Entry<Contestant, Map<Discipline, Double>> e1 : updateScore.entrySet()) {
			for(Entry<Discipline, Double> e2 : updateScore.get(e1.getKey()).entrySet()) {
				e1.getKey().setScore(e2.getKey(), e2.getValue());
			}
		}
		updateScore.clear();
	}
	
	protected void clearUpdateScores() {
		updateScore.clear();
	}
	
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
	
	protected synchronized void unlock() {
		this.locked = false;
		this.notify();
	}
}
