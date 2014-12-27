package cz.salmelu.contests.model;

/**
 * A simple enum for all score modes in team competition
 * @author salmelu
 *
 */
public enum ScoreMode {
	/**	Takes full average of all contestants' (total score + bonus) + team bonus / size */
	AverageFull,
	/** Takes an average of contestants' score (total score + bonus) and adds a full team bonus */
	AddTeamBonus,
	/** Takes an average of contestants' base score, adds all their bonuses and a team bonus */
	AddAllBonuses,
	/** All score is calculated by addition */
	Additive;
	
	ScoreMode() {
		
	}
}
