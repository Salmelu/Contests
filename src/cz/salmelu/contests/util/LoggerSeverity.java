package cz.salmelu.contests.util;

/**
 * Sets the severity of logging and debug messages.
 * @author salmelu
 */
public enum LoggerSeverity {
	VERBOSE(1),
	INFO(2),
	WARNING(3),
	ERROR(4);
	
	private int order;
	
	/**
	 * Sets a new severity.
	 * @param order integer value used for comparing gravity of severities
	 */
	LoggerSeverity(int order) {
		this.order = order;
	}
	
	/**
	 * Checks if a severity is at least s.
	 * @param s the severity to be compared to
	 * @return true, if the object on which the method is called, is at least s
	 */
	public boolean isGreaterOrEqual(LoggerSeverity s) {
		return this.order >= s.order;
	}
}