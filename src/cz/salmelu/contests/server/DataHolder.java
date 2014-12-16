package cz.salmelu.contests.server;

import java.util.Map;
import java.util.TreeMap;

import cz.salmelu.contests.model.Contest;
import cz.salmelu.contests.model.TeamCategory;

public class DataHolder {
	
	private Map<Integer, Contest> contests;
	private boolean locked = false;
	
	public DataHolder() {
		this.contests = new TreeMap<>();
	}
	
	public Map<Integer, Contest> getAllContests() {
		return contests;
	}
	
	public void replaceContests(Map<Integer, Contest> contests) {
		this.contests = contests;
	}
	
	public Contest getContest(int id) {
		if(contests.containsKey(id))
			return contests.get(id);
		return null;
	}
	
	public void addContest(Contest cs) {
		this.contests.put(cs.getId(), cs);
	}
	
	public TeamCategory getTeamCategory(int contestId, int tcId) {
		if(!contests.containsKey(contestId))
			return null;
		if(!contests.get(contestId).getTeamCategories().contains(tcId))
			return null;
		return contests.get(contestId).getTeamCategories().get(tcId);
	}
	
	public void addTeamCategory(TeamCategory tc, Contest c) {
		c.addTeamCategory(tc);
	}
	
	public synchronized boolean lock() {
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
	
	public synchronized void unlock() {
		this.locked = false;
		this.notify();
	}
}
