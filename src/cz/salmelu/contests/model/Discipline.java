package cz.salmelu.contests.model;

import java.io.Serializable;

public class Discipline implements Serializable {
	
	private static final long serialVersionUID = 6205234603449373935L;
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
