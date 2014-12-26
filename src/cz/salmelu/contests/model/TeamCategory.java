package cz.salmelu.contests.model;

import java.io.Serializable;

public class TeamCategory implements Serializable {

	private static final long serialVersionUID = -5132348545396002065L;
	private final int id;
	private String name;
	private ScoreMode sm;
	
	public TeamCategory() {
		this.id = IdFactory.getInstance().getNewId(this);
	}
	
	public TeamCategory(String name, ScoreMode sm) {
		this.id = IdFactory.getInstance().getNewId(this);
		this.name = name;
		this.sm = sm;
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
	
	public void setScoreMode(ScoreMode sm) {
		this.sm = sm;
	}
	
	public ScoreMode getScoreMode() {
		return sm;
	}
}
