package cz.salmelu.contests.model;

import java.io.Serializable;
import java.util.List;

public class Team implements Serializable {
	
	private static final long serialVersionUID = 3663526489659093306L;
	private final int id;
	private String name;
	private TeamCategory cat;
	private List<TeamContestant> contestants;
	private double teamBonus;
	private transient int order;
	
	public Team() {
		this(null, 0);
	}
	
	public Team(String name) {
		this(name, 0);
	}
	
	public Team(String name, double teamBonus) {
		this.id = IdFactory.getInstance().getNewId(this);
		this.name = name;
		this.teamBonus = teamBonus;
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
	
	public void setCategory(TeamCategory tc) {
		this.cat = tc;
	}
	
	public TeamCategory getCategory() {
		return cat;
	}
 
	public void setBonus(double bonus) {
		this.teamBonus = bonus;
	}
	
	public double getBonus() {
		return teamBonus;
	}
	
	public double getTotalScore() {
		// FIXME fill a method to count it
		return 0;
	}
	
	public List<TeamContestant> getContestants() {
		return contestants;
	}
	
	public int getOrder() {
		return order;
	}
	
	protected void setOrder(int order) {
		this.order = order;
	}
}
