package cz.salmelu.contests.model;

public class TeamContestant extends Contestant {

	private static final long serialVersionUID = 3976368930647480024L;
	private double bonus;
	private Team team = null;
	
	public TeamContestant() {
		super();
	}
	
	public TeamContestant(String firstName, String lastName) {
		super(firstName, lastName);
	}
	
	public TeamContestant(String firstName, String lastName, Category cat) {
		super(firstName, lastName, cat);
	}
	
	public TeamContestant(String firstName, String lastName, Category cat, Team team, double bonus) {
		super(firstName, lastName, cat);
		this.team = team;
		this.bonus = bonus;
	}
	
	public void setTeam(Team t) {
		this.team = t;
	}
	
	public Team getTeam() {
		return team;
	}
	
	public void setBonus(double bonus) {
		this.bonus = bonus;
	}
	
	public double getBonus() {
		return bonus;
	}

}
