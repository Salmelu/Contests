package cz.salmelu.contests.net;

import java.io.Serializable;

public class UpdateScorePacket implements Serializable {
	 
	private static final long serialVersionUID = 7503268992906741226L;
	public int catId;
	public int discId;
	public int conId;
	public double score;
	
	public UpdateScorePacket(int catId, int discId, int conId, double score) {
		this.catId = catId;
		this.discId = discId;
		this.conId = conId;
		this.score = score;
	}
}
