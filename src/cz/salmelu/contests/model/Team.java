package cz.salmelu.contests.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents each team in the program.<br>
 * Contains a list of all contestants in the team to allow a quick and easy iterating through them.
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
	 * Constructs a new team with a given name.
	 * @param name display name of the new team
	 */
	public Team(String name) {
		this(name, 0);
	}
	
	/**
	 * Constructs a new team with a given name and team bonus.
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
	 * Gets team's unique id.
	 * @return id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Changes team's display name.
	 * @param name new display name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets team's display name.
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
	 * Changes or sets team's category.
	 * @param tc new team category
	 */
	public void setCategory(TeamCategory tc) {
		if(cat != null) cat.removeTeam(this);
		cat = tc;
		if(cat != null) tc.addTeam(this);
	}
	
	/**
	 * Gets team's current category.
	 * @return team category of the team 
	 */
	public TeamCategory getCategory() {
		return cat;
	}
 
	/**
	 * Changes team bonus of the team.
	 * @param bonus new bonus
	 */
	public void setBonus(double bonus) {
		this.teamBonus = bonus;
	}
	
	/**
	 * Gets team's bonus.
	 * @return bonus
	 */
	public double getBonus() {
		return teamBonus;
	}
	
	/**
	 * Gets team total score.
	 * It is calculated from contestants' score, using this team's category {@link ScoreMode}.
	 * @return team's total score
	 */
	public double getTotalScore() {
		if(cat == null) return 0;
		double score = 0;
		
		switch(cat.getScoreMode()) {
		case Additive:
			for(TeamContestant tc : contestants) {
				score += tc.getTotalScore() + tc.getBonus();
			}
			score += teamBonus;
			break;
		case AddAllBonuses:
			int bonus = 0;
			for(TeamContestant tc : contestants) {
				score += tc.getTotalScore();
				bonus += tc.getBonus();
			}
			score /= contestants.size();
			score += bonus + teamBonus;
			break;
		case AddTeamBonus:
			for(TeamContestant tc : contestants) {
				score += tc.getTotalScore() + tc.getBonus();
			}
			score /= contestants.size();
			score += teamBonus;
			break;
		case AverageFull:
			for(TeamContestant tc : contestants) {
				score += tc.getTotalScore() + tc.getBonus();
			}
			score += teamBonus;
			score /= contestants.size();
			break;
		}
		return score;
	}
	
	/**
	 * Gets a list of all contestants in the team.
	 * @return list of team contestants
	 */
	public List<TeamContestant> getContestants() {
		return contestants;
	}
	
	/**
	 * Adds a new contestant to the team.
	 * @param tc a new contestant
	 */
	public void addContestant(TeamContestant tc) {
		contestants.add(tc);
		tc.setTeam(this);
	}
	
	/**
	 * Removes a contestant from the team.
	 * @param tc removed contestant
	 */
	public void removeContestant(TeamContestant tc) {
		contestants.remove(tc);
		tc.setTeam(null);
	}
	
	/**
	 * Removes all contestants from the team.<br>
	 * Used when the team is removed from the contest.
	 */
	public void removeAllContestants() {
		for(Iterator<TeamContestant> itc = contestants.iterator(); itc.hasNext(); ) {
			TeamContestant tc = itc.next();
			tc.setTeam(null);
			itc.remove();
		}
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
