package cz.salmelu.contests.model;

public class Discipline {
	
	private final int id;
	private String name;
	
	public Discipline() {
		this.id = IdFactory.getInstance().getNewId(this);
	}
	
	public Discipline(String name) {
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
