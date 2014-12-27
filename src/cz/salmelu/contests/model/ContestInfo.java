package cz.salmelu.contests.model;

import java.io.Serializable;

public class ContestInfo implements Serializable {
	
	private static final long serialVersionUID = -7858873191564696402L;
	private final int id;
	private String name;
	private int contestants = 0;
	private int teams = 0;
	
	protected ContestInfo(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	protected void setTeams(int size) {
		teams = size;
	}
	
	protected void setContestants(int size) {
		contestants = size;
	}
	
	public int getTeams() {
		return teams;
	}
	
	public int getContestants() {
		return contestants;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
}
