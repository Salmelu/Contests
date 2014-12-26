package cz.salmelu.contests.model;

import java.io.Serializable;
import java.util.Map;

public class Contestant implements Serializable {

	private static final long serialVersionUID = -9031088091797646634L;
	private final int id;
	private String firstName;
	private String lastName;
	private Category cat;
	private Map<Discipline, Double> score;
	private transient int order;
	
	public Contestant() {
		this(null, null, null);
	}
	
	public Contestant(String firstName, String lastName) {
		this(firstName, lastName, null);
	}
	
	public Contestant(String firstName, String lastName, Category cat) {
		this.id = IdFactory.getInstance().getNewId(this);
		this.firstName = firstName;
		this.lastName = lastName;
		this.cat = cat;
	}

	public int getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Map<Discipline, Double> getAllScores() {
		return score;
	}
	
	public Category getCategory() {
		return cat;
	}
	
	public double getScore(Discipline d) {
		if(!score.containsKey(d)) return 0;
		return score.get(d);
	}
	
	public double getTotalScore() {
		double total = 0;
		for(Discipline d : cat.getDisciplines()) {
			total += getScore(d);
		}
		return total;
	}

	public void setScore(Discipline d, double score) {
		if(!cat.hasDiscipline(d)) return;
		this.score.put(d,score);
	}
	
	public int getOrder() {
		return order;
	}
	
	protected void setOrder(int order) {
		this.order = order;
	}
	
	
	
}
