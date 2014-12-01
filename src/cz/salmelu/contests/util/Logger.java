package cz.salmelu.contests.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Logger {
	private List<LoggerOutput> loggers;
	private static Logger instance;
	
	private Logger() {
		loggers = new ArrayList<>();
	}
	
	public static Logger getInstance() {
		if(instance == null) {
			instance = new Logger();
		}
		return instance;
	}
	
	public void openOutput(File f) throws FileNotFoundException {
		openOutput(f, LoggerSeverity.WARNING);
	}
	
	public void openOutput(File f, LoggerSeverity s) throws FileNotFoundException {
		PrintStream out = new PrintStream(f);
		this.loggers.add(new LoggerOutput(out, s));
	}
	
	public void openOutput(PrintStream ps) {
		openOutput(ps, LoggerSeverity.WARNING);
	}

	public void openOutput(PrintStream ps, LoggerSeverity s) {
		this.loggers.add(new LoggerOutput(ps, s));
	}
	
	public void closeOutputs() {
		for(LoggerOutput lo : loggers) {
			lo.close();
			loggers.remove(lo);
		}
	}
	
	public void logAlways(String message) {
		for(LoggerOutput lo : loggers) {
			lo.log(message, null);
		}
	}
	
	public void log(String message) {
		log(message, LoggerSeverity.INFO);
	}
	
	public void log(String message, LoggerSeverity s) {
		for(LoggerOutput lo : loggers) {
			lo.checkedLog(message, s);
		}
	}
}

class LoggerOutput {
	private PrintStream logger;
	private LoggerSeverity sev; 
	private static SimpleDateFormat formatter;
	
	static {
		formatter = new SimpleDateFormat("hh:mm:ss");
	}
	
	LoggerOutput(PrintStream logger, LoggerSeverity s) {
		this.logger = logger;
		this.sev = s;
	}
	
	public void close() {
		logger.close();
	}
	
	public void log(String message, LoggerSeverity s) {
		StringBuilder sb = new StringBuilder();
		sb.append(formatter.format(new Date()));
		if(s != null) {
		switch(s) {
			case ERROR:
				sb.append(" - [E] ");
				break;
			case WARNING:
				sb.append(" - [W] ");
				break;
			case INFO:
				sb.append(" - [I] ");
				break;
			case VERBOSE:
				sb.append(" - [V] ");
				break;
			}
		}
		else {
			sb.append(" - [A] ");
		}
		sb.append(message);
		logger.println(sb.toString());
	}
	public void checkedLog(String message, LoggerSeverity s) {
		if(s.isGreaterOrEqual(sev)) log(message, s);
	}
	
	public void setSeverity(LoggerSeverity s) {
		this.sev = s;
	}
	
	public LoggerSeverity getSeverity() {
		return sev;
	}
}