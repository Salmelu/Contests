package cz.salmelu.contests.client;

/**
 * This enum represents all the menu actions.
 * There is one dummy action, MAIN_RELOAD_QUIET, 
 * which is used for reloading the server data 
 * after changing some of the data.
 * @author salmelu
 */
enum MenuAction {
	MAIN_CONTESTS,
	MAIN_RELOAD,
	MAIN_RELOAD_QUIET,
	MAIN_EXIT,
	SHOW_TEAMS,
	SHOW_CONTESTANTS,
	SHOW_ONE_TEAM,
	SCORE_CATEGORY,
	UPDATE_CONTEST,
	UPDATE_DISCIPLINE,
	UPDATE_TCATEGORY,
	UPDATE_CATEGORY,
	UPDATE_TEAM,
	UPDATE_CONTESTANT;
}
