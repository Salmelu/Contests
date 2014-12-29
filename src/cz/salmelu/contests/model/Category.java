package cz.salmelu.contests.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents contestants' category. Defines a set of disciplines for the contestants
 * @author salmelu
 */
public class Category implements Serializable {

	/** Serialization UID */
	private static final long serialVersionUID = -8950916093636971536L;
	/** List of disciplines in this category */
	private List<Discipline> discs;
	/** Id of this category */
	private final int id;
	/** Display name of this category */
	private String name;
	
	/**
	 * Constructs a new category
	 * @param name Display name of this category
	 */
	public Category(String name) {
		this.id = IdFactory.getInstance().getNewId(this);
		this.name = name;
		discs = new ArrayList<>();
	}
	
	/**
	 * Gets category id
	 * @return id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Changes category display name
	 * @param name new name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets category name
	 * @return display name
	 */
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getName();
	}
	
	/**
	 * Gets a list of disciplines associated with this category
	 * @return list of disciplines
	 */
	public List<Discipline> getDisciplines() {
		return discs;
	}
	
	/**
	 * Adds a new discipline to the category
	 * @param d new discipline
	 */
	public void addDiscipline(Discipline d) {
		discs.add(d);
	}
	
	/**
	 * Checks if the category contains a certain discipline
	 * @param d checked discipline
	 * @return true if the category contains Discipline d, false otherwise
	 */
	public boolean hasDiscipline(Discipline d) {
		return discs.contains(d);
	}
	
	/**
	 * Removes the discipline from the category
	 * @param d removed discipline
	 */
	public void removeDiscipline(Discipline d) {
		discs.remove(d);
	}
}
