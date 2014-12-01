package cz.salmelu.contests.util;

public enum LoggerSeverity {
	VERBOSE(1),
	INFO(2),
	WARNING(3),
	ERROR(4);
	
	int order;
	
	LoggerSeverity(int order) {
		this.order = order;
	}
	
	public boolean isGreaterOrEqual(LoggerSeverity s) {
		return this.order >= s.order;
	}
}