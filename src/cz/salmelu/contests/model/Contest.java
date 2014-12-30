package cz.salmelu.contests.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
		teams.put(tc, new HashMap<>());
	}
	
	/**
	 * Checks if the contest contains a team category
	 * @param tcId the id of the contained team category
	 * @return true, if the contest has team category with id tcId
	 */
	public boolean hasTeamCategory(int tcId) {
		return teamCategories.containsKey(tcId);
	}
	
	/**
	 * Checks if the contest contains a team category
	 * @param tc the team category
	 * @return true, if the contest has team category tc
	 */
	public boolean hasTeamCategory(TeamCategory tc) {
		return teamCategories.containsValue(tc);
	}
	
	/**
	 * Removes a team category<br>
	 * <b>Warning:</b> Removes all the teams in the category.
	 * @param tcId id of the removed team category
	 */
	public void removeTeamCategory(int tcId) {
		TeamCategory cat = teamCategories.remove(tcId);
		for(Team t : cat.getAllTeams()) {
			t.removeAllContestants();
		}
		teams.remove(cat);
	}

	/**
	 * Gets a team category in the contest
	 * @param tcId id of the wanted team category
	 * @return the team category, or null, if it doesn't exist
	 */
	public TeamCategory getTeamCategory(int tcId) {
		return teamCategories.get(tcId);
	}
	
	/**
	 * Adds a new category
	 * @param c new category
	 */
	public void addCategory(Category c) {
		categories.put(c.getId(), c);
		contestants.put(c, new HashMap<>());
	}

	/**
	 * Checks if the contest contains a category
	 * @param catId id of the contained category
	 * @return true, if the contest has a category with id catId
	 */
	public boolean hasCategory(int catId) {
		return categories.containsKey(catId);
	}
	
	/**
	 * Checks if the contest contains a category
	 * @param cat the category
	 * @return true, if the contest has category cat
	 */
	public boolean hasCategory(Category cat) {
		return categories.containsValue(cat);
	}
	
	/**
	 * Removes a category. <br>
	 * <b>Warning:</b> Removes all the contestants in the category.
	 * @param catId id of the removed category
	 */
	public void removeCategory(int catId) {
		Category cat = categories.remove(catId);
		for(Contestant cs : contestants.get(cat).values()) {
			if(cs instanceof TeamContestant) {
				TeamContestant tcs = (TeamContestant) cs;
				if(tcs.getTeam() != null) {
					tcs.getTeam().removeContestant(tcs);
				}
			}
		}
		contestants.remove(cat);
	}
	
	/**
	 * Gets a category in the contest
	 * @param catId id of the wanted category
	 * @return the category, or null, if it doesn't exist
	 */
	public Category getCategory(int catId) {
		return categories.get(catId);
	}
	
	/**
	 * Adds a new discipline
	 * @param d new discipline
	 */
	public void addDiscipline(Discipline d) {
		disciplines.put(d.getId(), d);
	}

	/**
	 * Checks if the contest contains a discipline
	 * @param discId id of the contained discipline
	 * @return true, if the contest has a discipline with id discId
	 */
	public boolean hasDiscipline(int discId) {
		return disciplines.containsKey(discId);
	}
	
	/**
	 * Checks if the contest contains a discipline
	 * @param d the discipline
	 * @return true, if the contest has discipline d
	 */
	public boolean hasDiscipline(Discipline d) {
		return disciplines.containsValue(d);
	}
	
	/**
	 * Removes a discipline
	 * @param discId id of the removed discipline
	 */
	public void removeDiscipline(int discId) {
		Discipline d = disciplines.remove(discId);
		for(Category c : categories.values()) {
			if(c.getDisciplines().contains(d)) {
				c.getDisciplines().remove(d);
				for(Contestant cs : contestants.get(c).values()) {
					cs.clearDisciplineScore(d);
				}
			}
		}
	}
	
	/**
	 * Gets a discipline in the contest
	 * @param discId id of the wanted discipline
	 * @return the discipline, or null, if it doesn't exist
	 */
	public Discipline getDiscipline(int discId) {
		return disciplines.get(discId);
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
		t.setCategory(tc);
	}
	
	/**
	 * Checks if the contest contains a team in a category
	 * @param tcId id of the containing team category
	 * @param teamId id of the checked team
	 * @return true, if the contest has a team with id teamId
	 */
	public boolean hasTeam(int tcId, int teamId) {
		return teamCategories.containsKey(tcId) 
				&& teams.get(teamCategories.get(tcId)).containsKey(teamId);
	}
	
	/**
	 * Checks if the contest contains a team
	 * @param t checked team
	 * @return true, if the contest has team t
	 */
	public boolean hasTeam(Team t) {
		return t.getCategory() != null && 
				teamCategories.containsValue(t.getCategory()) && teams.get(t.getCategory()).containsValue(t);
	}
	
	/**
	 * Removes a team from the contest and all its contestants from the team
	 * @param tcId id of containing team category
	 * @param teamId id of the removed team
	 */
	public void removeTeam(int tcId, int teamId) {
		if(teamCategories.containsKey(tcId)) {
			TeamCategory tc = teamCategories.get(tcId);
			Team t = teams.get(tc).get(teamId);
			if(t != null) {
				tc.removeTeam(t);
				teams.get(tc).remove(teamId);
				t.removeAllContestants();
			}
		}
	}
	
	/**
	 * Changes a team's category. Does all the required data transfers
	 * @param t affected team
	 * @param newTc new team category
	 */
	public void changeTeamCategory(Team t, TeamCategory newTc) {
		if(t.getCategory() != null) {
			teams.get(t.getCategory()).remove(t.getId());
		}
		t.setCategory(newTc);
		teams.get(newTc).put(t.getId(), t);
	}
	
	/**
	 * Gets a team by its id and its category id
	 * @param tcId id of the containing category
	 * @param teamId id of the requested team
	 * @return a team with id teamId, or null, if it couldn't be found
	 */
	public Team getTeam(int tcId, int teamId) {
		if(teamCategories.containsKey(tcId)) {
			TeamCategory tc = teamCategories.get(tcId);
			Team t = teams.get(tc).get(teamId);
			return t;
		}
		return null;
	}
	
	/**
	 * Adds a new contestant. If this contest doesn't contain the contestant's category, no contestant is added
	 * @param c new contestant
	 */
	public void addContestant(Category cat, Contestant c) {
		if(!contestants.containsKey(cat)) {
			return;
		}
		contestants.get(cat).put(c.getId(), c);
		c.setCategory(cat);
	}
	
	/**
	 * Checks if the contest contains a contestant in a category
	 * @param catId id of the containing category
	 * @param id id of the checked contestant
	 * @return true, if the contest has a contestant with id id
	 */
	public boolean hasContestant(int catId, int id) {
		return categories.containsKey(catId) 
				&& contestants.get(categories.get(catId)).containsKey(id);
	}
	
	/**
	 * Checks if the contest contains a contestant
	 * @param c checked contestant
	 * @return true, if the contest has contestant c
	 */
	public boolean hasContestant(Contestant c) {
		return c.getCategory() != null && 
				categories.containsValue(c.getCategory()) && contestants.get(c.getCategory()).containsValue(c);
	}
	
	/**
	 * Removes a team from the contest and all its contestants from the team
	 * @param catId id of containing category
	 * @param id id of the removed contestant
	 */
	public void removeContestant(int catId, int id) {
		if(categories.containsKey(catId)) {
			Category cat = categories.get(catId);
			Contestant cs = contestants.get(cat).get(id);
			if(cs != null) {
				contestants.get(cat).remove(id);
				if(cs instanceof TeamContestant) {
					TeamContestant tcs = (TeamContestant) cs;
					if(tcs.getTeam() != null) {
						tcs.getTeam().removeContestant(tcs);
					}
				}	
			}
		}
	}
	
	/**
	 * Changes a contestant's category. Does all the required data transfers
	 * @param cs affected contestant
	 * @param cat new category
	 */
	public void changeContestantCategory(Contestant cs, Category cat) {
		if(cs.getCategory() != null) {
			contestants.get(cs.getCategory()).remove(cs.getId());
		}
		cs.setCategory(cat);
		contestants.get(cat).put(cs.getId(), cs);
	}
	
	/**
	 * Gets a contestant by its id and its category id
	 * @param catId id of the containing category
	 * @param id id of the requested contestant
	 * @return a contestant with id id, or null, if it couldn't be found
	 */
	public Contestant getContestant(int catId, int id) {
		if(categories.containsKey(catId)) {
			Category cat = categories.get(catId);
			Contestant cs = contestants.get(cat).get(id);
			return cs;
		}
		return null;
	}
	
	/**
	 * Updates ContestInfo of this contest with new values
	 */
	private void updateInfo() {
		infos.setName(name);
		int count = 0;
		for(Entry<Category, Map<Integer, Contestant>> e : contestants.entrySet()) {
			if(e.getValue() != null) count += e.getValue().size();
		}
		infos.setContestants(count);
		count = 0;
		for(Entry<TeamCategory, Map<Integer, Team>> e : teams.entrySet()) {
			if(e.getValue() != null) count += e.getValue().size();
		}
		infos.setTeams(count);
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
