package cz.salmelu.contests.model;

import java.io.Serializable;

public class Contest implements Serializable {

	/**  */
	private static final long serialVersionUID = -2941146427528620926L;
	private final int id;
	
	public Contest(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

}
