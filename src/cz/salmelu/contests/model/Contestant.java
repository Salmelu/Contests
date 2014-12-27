package cz.salmelu.contests.model;

import java.io.Serializable;
import java.util.Map;

/**
 * Class representing a contestant in the contest
 * @author salmelu
 */
public class Contestant implements Serializable {

	/** Serialization UID */
	private static final long serialVersionUID = -9031088091797646634L;
	/** Unique id of the contestant */
	private final int id;
	/** first name of the contestant */
	private String firstName;
	/** last name of the contestant */
	private String lastName;
	/** category of the contestant */
	private Category cat;
	/** map of scores in all disciplines */
	private Map<Discipline, Double> score;
	/** used for internal ordering */
	//private transient int order;
	
	/**
	 * Creates a new contestant
	 * @param firstName First name of the contestant
	 * @param lastName Last name of the contestant
	 */
	public Contestant(String firstName, String lastName) {
		this(firstName, lastName, null);
	}
	
	/**
	 * Creates a new contestant
	 * @param firstName First name of the contestant
	 * @param lastName Last name of the contestant
	 * @param cat The category of the contestant
	 */
	public Contestant(String firstName, String lastName, Category cat) {
		this.id = IdFactory.getInstance().getNewId(this);
		this.firstName = firstName;
		this.lastName = lastName;
		this.cat = cat;
	}

	/**
	 * Gets contestant's id
	 * @return id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gets contestant's first name
	 * @return first name
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Changes contestant's first name
	 * @param firstName new first name
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Gets contestant's last name
	 * @return last name
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Changes contestant's last name
	 * @param lastName new last name
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	@Override
	public String toString() {
		return getFirstName() + " " + getLastName();
	}

	/**
	 * Gets a map of contestant's scores accessed by disciplines
	 * @return map of scores
	 */
	public Map<Discipline, Double> getAllScores() {
		return score;
	}
	
	/**
	 * Gets contestant's category
	 * @return category
	 */
	public Category getCategory() {
		return cat;
	}
	
	/**
	 * Gets contestant's score in a discipline
	 * @param d contestant's discipline
	 * @return score in discipline d
	 */
	public double getScore(Discipline d) {
		if(!score.containsKey(d)) return 0;
		return score.get(d);
	}
	
	/**
	 * Gets a sum of score in all disciplines 
	 * @return total score of the contestant
	 */
	public double getTotalScore() {
		double total = 0;
		for(Discipline d : cat.getDisciplines()) {
			total += getScore(d);
		}
		return total;
	}

	/**
	 * Changes a score of the contestant
	 * @param d a discipline where the score will be changed
	 * @param score new score
	 */
	public void setScore(Discipline d, double score) {
		if(!cat.hasDiscipline(d)) return;
		this.score.put(d,score);
	}
	
	/* NOT USED NOW
	private int getOrder() {
		return order;
	}
	
	private void setOrder(int order) {
		this.order = order;
	}
	*/
}
