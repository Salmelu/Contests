package cz.salmelu.contests.model;

public class TeamCategory {

	private final int id;
	private String name;
	
	public TeamCategory() {
		this.id = IdFactory.getInstance().getNewId(this);
	}
	
	public TeamCategory(String name) {
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

}
