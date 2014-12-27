package cz.salmelu.contests.model;

import java.io.Serializable;

/**
 * Holds some information about the contest to avoid sending whole maps 
 * of contestants, teams, etc. to client and still allow displaying some 
 * contest info
 * @author salmelu
 */
public class ContestInfo implements Serializable {
	
	/** Serialization UID */
	private static final long serialVersionUID = -7858873191564696402L;
	/** Unique id of the represented contest */
	private final int id;
	/** Display name of the contest */
	private String name;
	/** Number of contestants in the contest */
	private int contestants = 0;
	/** Number of teams in the contest */
	private int teams = 0;
	
	/**
	 * Constructs a new Object. Should be only called from Contest class when creating a new contest
	 * @param id unique id of the represented contest
	 * @param name display name of the represented contest
	 */
	protected ContestInfo(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	/**
	 * Updates a number of teams in the contest
	 * @param size new number of teams
	 */
	protected void setTeams(int size) {
		teams = size;
	}

	/**
	 * Updates a number of contestants in the contest
	 * @param size new number of contestants
	 */
	protected void setContestants(int size) {
		contestants = size;
	}
	
	/**
	 * Gets a number of teams in the represented contest
	 * @return number of teams
	 */
	public int getTeams() {
		return teams;
	}
	
	/**
	 * Gets a number of contestants in the represented contest
	 * @return number of contestants
	 */
	public int getContestants() {
		return contestants;
	}
	
	/**
	 * Gets an unique id of the represented contest
	 * @return id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Gets a display name of the represented contest
	 * @return display name
	 */
	public String getName() {
		return name;
	}
}
