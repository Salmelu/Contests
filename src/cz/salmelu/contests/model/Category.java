package cz.salmelu.contests.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Category implements Serializable {

	private static final long serialVersionUID = -8950916093636971536L;
	private List<Discipline> discs;
	private final int id;
	private String name;
	
	public Category() {
		this("");
	}
	
	public Category(String name) {
		this.id = IdFactory.getInstance().getNewId(this);
		this.name = name;
		discs = new ArrayList<>();
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

	@Override
	public String toString() {
		return getName();
	}
}
