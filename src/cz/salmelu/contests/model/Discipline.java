package cz.salmelu.contests.model;

import java.io.Serializable;

/**
 * Represents a discipline in which the contestants compete.<br>
 * It is used by a {@link Category} to represent category disciplines and by the {@link Contestant} 
 * to mark down their score.
 * @author salmelu
 */
public class Discipline implements Serializable {
	
	/** Serialization UID */
	private static final long serialVersionUID = 6205234603449373935L;
	/** Unique id of the discipline */
	private final int id;
	/** Display name of the discipline */
	private String name;
	
	/**
	 * Constructs a new discipline
	 * @param name display name
	 */
	public Discipline(String name) {
		this.id = IdFactory.getInstance().getNewId(this);
		this.name = name;
	}
	
	/**
	 * Gets id of the discipline
	 * @return id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Changes a display name of the discipline
	 * @param name new display name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets a display name of the discipline
	 * @return display name
	 */
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return getName();
	}

}
