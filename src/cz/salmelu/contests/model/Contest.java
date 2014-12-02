package cz.salmelu.contests.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Contest implements Serializable {

	/**  */
	private static final long serialVersionUID = -2941146427528620926L;
	private Map<TeamCategory, List<Team>> teams;
	private Map<Category, List<Contestant>> contestants;
	private List<Category> categories;
	private List<Discipline> disciplines;
	private List<TeamCategory> teamCategories;
	private ScoreMode mode;
	
	private final int id;
	private String name;
	
	public Contest() {
		this.id = IdFactory.getInstance().getNewId(this);
	}
	
	public Contest(String name) {
		this.id = IdFactory.getInstance().getNewId(this);
		this.name = name;
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
	
	public Map<TeamCategory, List<Team>> getAllTeams() {
		return teams;
	}
	
	public List<Team> getTeam(TeamCategory tc) {
		if(teams.containsKey(tc)) {
			return teams.get(tc);
		}
		return null;
	}
	
	public Map<Category, List<Contestant>> getAllContestants() {
		return contestants;
	}
	
	public List<Contestant> getContestants(Category c) {
		if(contestants.containsKey(c)) {
			return contestants.get(c);
		}
		return null;
	}
	
	public List<Category> getCategories() {
		return categories;
	}
	
	public List<Discipline> getDisciplines() {
		return disciplines;
	}
	
	public List<TeamCategory> getTeamCategories() {
		return teamCategories;
	}
	
	public void addTeamCategory(TeamCategory tc) {
		teamCategories.add(tc);
	}
	
	public void addCategory(Category c) {
		categories.add(c);
	}
	
	public void addDiscipline(Discipline d) {
		disciplines.add(d);
	}
	
	public void addTeam(TeamCategory tc, Team t) {
		if(!teams.containsKey(tc)) {
			teams.put(tc, new ArrayList<>());
		}
		teams.get(tc).add(t);
	}
	
	public void addContestant(Category cat, Contestant c) {
		if(!contestants.containsKey(cat)) {
			contestants.put(cat, new ArrayList<>());
		}
		contestants.get(cat).add(c);
	}

}
