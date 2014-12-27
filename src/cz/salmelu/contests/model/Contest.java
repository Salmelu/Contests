package cz.salmelu.contests.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Contest implements Serializable {

	/**  */
	private static final long serialVersionUID = -2941146427528620926L;
	private ContestInfo infos;
	private Map<TeamCategory, Map<Integer, Team>> teams;
	private Map<Category, Map<Integer, Contestant>> contestants;
	private Map<Integer, Category> categories;
	private Map<Integer, Discipline> disciplines;
	private Map<Integer, TeamCategory> teamCategories;
	private ScoreMode mode;
	
	private final int id;
	private String name;
	
	public Contest() {
		this("");
	}
	
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
	
	public int getId() {
		return id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setScoreMode(ScoreMode mode) {
		this.mode = mode;
	}
	
	public ScoreMode getScoreMode() {
		return mode;
	}
	
	public Map<TeamCategory, Map<Integer, Team>> getAllTeams() {
		return teams;
	}
	
	public Map<Integer, Team> getTeams(TeamCategory tc) {
		if(teams.containsKey(tc)) {
			return teams.get(tc);
		}
		return null;
	}
	
	public Map<Category, Map<Integer, Contestant>> getAllContestants() {
		return contestants;
	}
	
	public Map<Integer, Contestant> getContestants(Category c) {
		if(contestants.containsKey(c)) {
			return contestants.get(c);
		}
		return null;
	}
	
	public Map<Integer, Category> getCategories() {
		return categories;
	}
	
	public Map<Integer, Discipline> getDisciplines() {
		return disciplines;
	}
	
	public Map<Integer, TeamCategory> getTeamCategories() {
		return teamCategories;
	}
	
	public void addTeamCategory(TeamCategory tc) {
		teamCategories.put(tc.getId(), tc);
	}
	
	public void addCategory(Category c) {
		categories.put(c.getId(), c);
	}
	
	public void addDiscipline(Discipline d) {
		disciplines.put(d.getId(), d);
	}
	
	public void addTeam(TeamCategory tc, Team t) {
		if(!teams.containsKey(tc)) {
			teams.put(tc, new TreeMap<>());
		}
		teams.get(tc).put(t.getId(), t);
	}
	
	public void addContestant(Contestant c) {
		if(!contestants.containsKey(c.getCategory())) {
			contestants.put(c.getCategory(), new TreeMap<>());
		}
		contestants.get(c.getCategory()).put(c.getId(), c);
	}
	
	private void updateInfo() {
		int count = 0;
		for(Entry<Category, Map<Integer, Contestant>> e : contestants.entrySet()) {
			if(e.getValue() != null) count += e.getValue().size();
		}
		infos.setContestants(count);
		infos.setTeams(teams.size());
	}
	
	public ContestInfo getContestInfo() {
		updateInfo();
		return infos;
	}

}
