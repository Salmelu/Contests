package cz.salmelu.contests.model;

import java.io.Serializable;

/**
 * Represents a team category, in which the teams compete among each other
 * @author salmelu
 */
public class TeamCategory implements Serializable {

	/** Serialization UID */
	private static final long serialVersionUID = -5132348545396002065L;
	/** Unique id of the category */
	private final int id;
	/** Display name of the category */
	private String name;
	/** Score mode of the category, used for calculating team score */
	private ScoreMode sm;
	
	/**
	 * Constructs a new TeamCategory
	 * @param name display name of the category
	 * @param sm a score mode used for calculating results 
	 */
	public TeamCategory(String name, ScoreMode sm) {
		this.id = IdFactory.getInstance().getNewId(this);
		this.name = name;
		this.sm = sm;
	}
	
	/**
	 * Gets team category id
	 * @return id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Changes the display name of the category
	 * @param name new display name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the display name of the category
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
	 * Changes the score mode of the category
	 * @param sm new score mode
	 */
	public void setScoreMode(ScoreMode sm) {
		this.sm = sm;
	}
	
	/**
	 * Gets the score mode of the category
	 * @return score mode
	 */
	public ScoreMode getScoreMode() {
		return sm;
	}
}
