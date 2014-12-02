package cz.salmelu.contests.model;

import java.util.List;

public class Category {

	private List<Discipline> discs;
	private final int id;
	private String name;
	
	public Category() {
		this.id = IdFactory.getInstance().getNewId(this);
	}
	
	public Category(String name) {
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
	
	public List<Discipline> getDisciplines() {
		return discs;
	}
	
	public void addDiscipline(Discipline d) {
		discs.add(d);
	}
	
	public boolean hasDiscipline(Discipline d) {
		return discs.contains(d);
	}

}
