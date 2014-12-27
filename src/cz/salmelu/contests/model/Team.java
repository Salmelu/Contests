package cz.salmelu.contests.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents each team in the program
 * @author salmelu
 */
public class Team implements Serializable {
	
	/** Serialization UID */
	private static final long serialVersionUID = 3663526489659093306L;
	/** Unique id of the team */
	private final int id;
	/** A display name of the team */
	private String name;
	/** A team category where the team belongs */
	private TeamCategory cat;
	/** A list of all contestants in the team */
	private List<TeamContestant> contestants;
	/** A bonus of this team */
	private double teamBonus;
	// private transient int order; UNUSED
	
	/**
	 * Constructs a new team
	 * @param name display name of the new team
	 */
	public Team(String name) {
		this(name, 0);
	}
	
	/**
	 * Constructs a new team
	 * @param name display name of the new team
	 * @param teamBonus team bonus of the team
	 */
	public Team(String name, double teamBonus) {
		this.id = IdFactory.getInstance().getNewId(this);
		this.name = name;
		this.teamBonus = teamBonus;
		contestants = new ArrayList<>();
	}
	
	/**
	 * Gets team's unique id
	 * @return id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Changes team's display name
	 * @param name new display name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets team's display name
	 * @return display name
	 */
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	/**
	 * Changes/sets team's category
	 * @param tc new team category
	 */
	public void setCategory(TeamCategory tc) {
		this.cat = tc;
	}
	
	/**
	 * Gets team's current category
	 * @return team category of the team 
	 */
	public TeamCategory getCategory() {
		return cat;
	}
 
	/**
	 * Changes team bonus of the team 
	 * @param bonus new bonus
	 */
	public void setBonus(double bonus) {
		this.teamBonus = bonus;
	}
	
	/**
	 * Gets team's bonus
	 * @return bonus
	 */
	public double getBonus() {
		return teamBonus;
	}
	
	/**
	 * Gets team total score, calculated from contestants score, using category score mode 
	 * @return team's total score
	 */
	public double getTotalScore() {
		// FIXME fill a method to count it
		return 0;
	}
	
	/**
	 * Gets a list of all contestants in the team
	 * @return list of team contestants
	 */
	public List<TeamContestant> getContestants() {
		return contestants;
	}
	
	/**
	 * Adds a new contestant to the team
	 * @param tc a new contestant
	 */
	public void addContestant(TeamContestant tc) {
		contestants.add(tc);
	}
	
	/* UNUSED
	public int getOrder() {
		return order;
	}
	
	protected void setOrder(int order) {
		this.order = order;
	}
	*/
}
