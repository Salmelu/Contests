package cz.salmelu.contests.model;

/**
 * Represents a team member. Used for the team bonuses of the members.
 * @author salmelu
 */
public class TeamContestant extends Contestant {

	/** Serialization UID */
	private static final long serialVersionUID = 3976368930647480024L;
	/** Bonus of the contestant */
	private double bonus;
	/** Current contestant's team */
	private Team team = null;
	
	/**
	 * Constructs a new contestant with given name
	 * @param firstName the contestant's first name
	 * @param lastName the contestant's last name
	 */
	public TeamContestant(String firstName, String lastName) {
		super(firstName, lastName);
	}
	
	/**
	 * Constructs a new contestant with given name and category
	 * @param firstName the contestant's first name
	 * @param lastName the contestant's last name
	 * @param cat the contestant's category
	 */
	public TeamContestant(String firstName, String lastName, Category cat) {
		super(firstName, lastName, cat);
	}

	/**
	 * Constructs a new contestant with given name and category, team and his bonus
	 * @param firstName the contestant's first name
	 * @param lastName the contestant's last name
	 * @param cat the contestant's category
	 * @param team the contestant's team
	 * @param bonus the contestant's bonus 
	 */
	public TeamContestant(String firstName, String lastName, Category cat, Team team, double bonus) {
		super(firstName, lastName, cat);
		this.team = team;
		this.bonus = bonus;
	}
	
	/**
	 * Changes contestant's team. Doesn't affect the setting in the team, must be changed there too.
	 * @param t new team
	 */
	public void setTeam(Team t) {
		this.team = t;
	}
	
	/**
	 * Gets contestant's current team
	 * @return team
	 */
	public Team getTeam() {
		return team;
	}
	
	/**
	 * Sets contestant's bonus for team competitions
	 * @param bonus new bonus
	 */
	public void setBonus(double bonus) {
		this.bonus = bonus;
	}
	
	/**
	 * Gets contestant's bonus for team competitions
	 * @return bonus
	 */
	public double getBonus() {
		return bonus;
	}

}
