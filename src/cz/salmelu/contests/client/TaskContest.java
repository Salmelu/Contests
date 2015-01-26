package cz.salmelu.contests.client;

import cz.salmelu.contests.net.ServerError;
import javafx.concurrent.Task;

/**
 * An abstract parent for various client to server tasks to allow handling server errors.<br>
 * Should be extended by all tasks.
 * @author salmelu
 */
public abstract class TaskContest extends Task<Boolean> {
	private ServerError se = null;
	
	/**
	 * Gets a {@link ServerError} object representing the occured error.
	 * @return a ServerError object, or null, if no error happened on the server
	 */
	public ServerError getServerError() {
		return se;
	}
	
	/**
	 * Updates the object with the new error.
	 * @param se happening server error
	 */
	public void setServerError(ServerError se) {
		this.se = se;
	}
}
