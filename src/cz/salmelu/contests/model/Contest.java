package cz.salmelu.contests.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Represents each Contest
 * @author salmelu
 */
public class Contest implements Serializable {

	/** Serialization UID */
	private static final long serialVersionUID = -2941146427528620926L;
	/** Information class associated with this contest */
	private ContestInfo infos;
	/** A map of teams sorted by team categories */
	private Map<TeamCategory, Map<Integer, Team>> teams;
	/** A map of contestants sorted by categories */
	private Map<Category, Map<Integer, Contestant>> contestants;
	/** A map of categories sorted by their id */
	private Map<Integer, Category> categories;
	/** A map of disciplines sorted by their id */
	private Map<Integer, Discipline> disciplines;
	/** A map of team categories sorted by their id */
	private Map<Integer, TeamCategory> teamCategories;
	/** A score mode of this contest for team competitions */
	private ScoreMode mode;
	
	/** Unique id of this contest */
	private final int id;
	/** Display name of this contest */
	private String name;
	
	/**
	 * Construct a new contest
	 * @param name Display name of that contest
	 */
	public Contest(String name) {
		this.id = IdFactory.getInstance().getNewId(this);
		this.name = name;
		this.categories = new HashMap<>();
		this.disciplines = new HashMap<>();
		this.teamCategories = new HashMap<>();
		this.contestants = new HashMap<>();
		this.teams = new HashMap<>();
		this.infos = new ContestInfo(id, name);
	}
	
	/**
	 * Gets contest's id
	 * @return id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Changes contest's display name
	 * @param name new name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets contest's display name
	 * @return display name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Changes contest's score mode
	 * @param mode new mode
	 */
	public void setScoreMode(ScoreMode mode) {
		this.mode = mode;
	}
	
	/**
	 * Gets contest's score mode
	 * @return score mode
	 */
	public ScoreMode getScoreMode() {
		return mode;
	}
	
	/**
	 * Gets all teams in the contest
	 * @return a double map of teams sorted by team categories and then by ids
	 */
	public Map<TeamCategory, Map<Integer, Team>> getAllTeams() {
		return teams;
	}
	
	/**
	 * Gets all teams in a certain category
	 * @param tc selected category
	 * @return map of teams in the category ordered by ids, null if this contest doesn't contain tc
	 */
	public Map<Integer, Team> getTeams(TeamCategory tc) {
		if(teams.containsKey(tc)) {
			return teams.get(tc);
		}
		return null;
	}
	
	/**
	 * Gets all contestants in the contest
	 * @return a double map of contestants sorted by categories and then by ids
	 */
	public Map<Category, Map<Integer, Contestant>> getAllContestants() {
		return contestants;
	}
	
	/**
	 * Gets all contestants in a certain category
	 * @param c selected category
	 * @return map of contestants in the category ordered by ids, null if this contest doesn't contain c
	 */
	public Map<Integer, Contestant> getContestants(Category c) {
		if(contestants.containsKey(c)) {
			return contestants.get(c);
		}
		return null;
	}
	
	/**
	 * Gets all categories in the contest
	 * @return map of categories ordered by ids
	 */
	public Map<Integer, Category> getCategories() {
		return categories;
	}
	
	/**
	 * Gets all disciplines in the contest
	 * @return map of disciplines ordered by ids
	 */
	public Map<Integer, Discipline> getDisciplines() {
		return disciplines;
	}
	
	/**
	 * Gets all team categories in the contest
	 * @return map of team categories ordered by ids
	 */
	public Map<Integer, TeamCategory> getTeamCategories() {
		return teamCategories;
	}
	
	/**
	 * Adds a new team category
	 * @param tc new team category
	 */
	public void addTeamCategory(TeamCategory tc) {
		teamCategories.put(tc.getId(), tc);
		teams.put(tc, new TreeMap<>());
	}
	
	/**
	 * Adds a new category
	 * @param c new category
	 */
	public void addCategory(Category c) {
		categories.put(c.getId(), c);
		contestants.put(c, new TreeMap<>());
	}
	
	/**
	 * Adds a new discipline
	 * @param d new discipline
	 */
	public void addDiscipline(Discipline d) {
		disciplines.put(d.getId(), d);
	}
	
	/**
	 * Adds a new team. If this contest doesn't contain the team's category, no team is added
	 * @param tc team category of the team
	 * @param t new team
	 */
	public void addTeam(TeamCategory tc, Team t) {
		if(!teams.containsKey(tc)) {
			return;
		}
		teams.get(tc).put(t.getId(), t);
	}
	
	/**
	 * Adds a new contestant. If this contest doesn't contain the contestant's category, no contestant is added
	 * @param c new contestant
	 */
	public void addContestant(Contestant c) {
		if(!contestants.containsKey(c.getCategory())) {
			return;
		}
		contestants.get(c.getCategory()).put(c.getId(), c);
	}
	
	/**
	 * Updates ContestInfo of this contest with new values
	 */
	private void updateInfo() {
		int count = 0;
		for(Entry<Category, Map<Integer, Contestant>> e : contestants.entrySet()) {
			if(e.getValue() != null) count += e.getValue().size();
		}
		infos.setContestants(count);
		infos.setTeams(teams.size());
	}
	
	/**
	 * Updates and gets a ContestInfo object of the contest
	 * @return ContestInfo of the contest
	 */
	public ContestInfo getContestInfo() {
		updateInfo();
		return infos;
	}

}
