package cz.salmelu.contests.model;

import java.io.IOException;

/**
 * An exception thrown by DataLoader to signalize loading errors
 * @author salmelu
 */
public class LoaderException extends IOException {

	/** Serialization UID */
	private static final long serialVersionUID = -6494215581408558088L;

	/**
	 * Creates a new LoaderException
	 * @param message Message to be passed with the exception
	 */
	public LoaderException(String message) {
		super(message);
	}
}
