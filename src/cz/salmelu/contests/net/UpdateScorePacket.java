package cz.salmelu.contests.net;

import java.io.Serializable;

/**
 * Used for sending change score data to server.
 * All the member fields are public, as this class should only hold some data
 * @author salmelu
 */
public class UpdateScorePacket implements Serializable {
	 
	/** Serialization UID */
	private static final long serialVersionUID = 7503268992906741226L;
	/** Category id */
	public int catId;
	/** Discipline id */
	public int discId;
	/** Contestant id */
	public int conId;
	/** New score */
	public double score;
	
	/**
	 * A simple constructor to make creating the packet easier
	 * @param catId category id
	 * @param discId discipline id
	 * @param conId contestant id
	 * @param score new score
	 */
	public UpdateScorePacket(int catId, int discId, int conId, double score) {
		this.catId = catId;
		this.discId = discId;
		this.conId = conId;
		this.score = score;
	}
}
