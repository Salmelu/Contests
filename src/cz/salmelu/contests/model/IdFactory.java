package cz.salmelu.contests.model;

import java.io.Serializable;

public class IdFactory implements Serializable {
	
	/**  */
	private static final long serialVersionUID = -4655236718997653511L;

	private int contestId;
	private int categoryId;
	private int contestantId;
	private int disciplineId;
	private int teamId;
	private int teamCategoryId;
	
	private static IdFactory instance = null;
	
	protected int getNewContestId() {
		return contestId++;
	}
	
	protected int getNewCategoryId() {
		return categoryId++;
	}
	
	protected int getNewContestantId() {
		return contestantId++;
	}
	
	protected int getNewDisciplineId() {
		return disciplineId++;
	}
	
	protected int getNewTeamId() {
		return teamId++;
	}
	
	protected int getNewTeamCategoryId() {
		return teamCategoryId++;
	}
	
	private IdFactory() {
		this.contestId = 1;
		this.categoryId = 1;
		this.contestantId = 1;
		this.teamCategoryId = 1;
		this.teamId = 1;
		this.disciplineId = 1;
	}
	
	protected static IdFactory getInstance() {
		if(instance == null) {
			instance = new IdFactory();
		}
		return instance;
	}
	
	protected static void loadFactory(IdFactory f) {
		if(f != null) {
			instance = f;
		}
	}
	
	protected static void reinitializeFactory(int id1, int id2, int id3, int id4, int id5, int id6) {
		if(instance == null) return;
		instance.contestId = id1;
		instance.categoryId = id2;
		instance.contestantId = id3;
		instance.teamCategoryId = id4;
		instance.teamId = id5;
		instance.disciplineId = id6;
	}
}
