package cz.salmelu.contests.model;

import java.util.List;

public class Team {

	private final int id;
	private String name;
	private List<TeamContestant> contestants;
	private double teamBonus;
	private transient int order;
	
	public Team() {
		this(null, 0);
	}
	
	public Team(String name) {
		this(name, 0);
	}
	
	public Team(String name, int teamBonus) {
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
 
	public void setBonus(double bonus) {
		this.teamBonus = bonus;
	}
	
	public double getBonus() {
		return teamBonus;
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
