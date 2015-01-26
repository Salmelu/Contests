package cz.salmelu.contests.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Custom Logger allowing to log a message to a PrintStream.<br>
 * Allows for logging to different outputs by registering each output to the Logger instance.<br>
 * The logging action is synchronized and therefore is thread-safe.<br>
 * Uses Singleton design pattern.
 * @author salmelu
 */
public class Logger {
	/** List of PrintStreams for logging */
	private List<LoggerOutput> loggers;
	/** Implements singleton pattern */
	private static Logger instance;
	
	/**
	 * Constructs a new Logger instance and initializes its' output list.
	 */
	private Logger() {
		loggers = new ArrayList<>();
	}
	
	/**
	 * Gets an instance of Logger.
	 * @return an instance of Logger, if it doesn't exist, creates a new one
	 */
	public static Logger getInstance() {
		if(instance == null) {
			instance = new Logger();
		}
		return instance;
	}
	
	/**
	 * Adds a file output to logger with Warning severity.
	 * @param f File to be used
	 * @throws FileNotFoundException if the stream couldn't be created
	 */
	public void openOutput(File f) throws FileNotFoundException {
		openOutput(f, LoggerSeverity.WARNING);
	}
	
	/**
	 * Adds a file output to logger.
	 * @param f File to be used
	 * @param s Minimal severity required to log the message
	 * @throws FileNotFoundException if the stream couldn't be created
	 */
	public void openOutput(File f, LoggerSeverity s) throws FileNotFoundException {
		PrintStream out = new PrintStream(f);
		this.loggers.add(new LoggerOutput(out, s));
	}
	
	/**
	 * Adds an existing PrintStream to the Logger with Warning severity.
	 * @param ps PrintStream opened
	 */
	public void openOutput(PrintStream ps) {
		openOutput(ps, LoggerSeverity.WARNING);
	}

	/**
	 * Adds an existing PrintStream to the Logger.
	 * @param ps PrintStream opened
	 * @param s Minimal severity required to log the message
	 */
	public void openOutput(PrintStream ps, LoggerSeverity s) {
		this.loggers.add(new LoggerOutput(ps, s));
	}
	
	/**
	 * Closes all PrintStreams associated with this Logger.
	 */
	public void closeOutputs() {
		for(LoggerOutput lo : loggers) {
			lo.close();
			loggers.remove(lo);
		}
	}
	
	/**
	 * Logs a message to all output streams, not dependant on the severity.
	 * @param message Message to be logged
	 */
	public void logAlways(String message) {
		for(LoggerOutput lo : loggers) {
			lo.log(message, null);
		}
	}
	
	/**
	 * Logs a message to all output streams allowing message with Info severity.
	 * @param message Message to be logged
	 */
	public void log(String message) {
		log(message, LoggerSeverity.INFO);
	}
	
	/**
	 * Logs a message to all output streams allowing messages with severity s.
	 * @param message Message to be logged
	 * @param s Severity of the message
	 */
	public void log(String message, LoggerSeverity s) {
		for(LoggerOutput lo : loggers) {
			lo.checkedLog(message, s);
		}
	}
}

/**
 * Class representing each pair PrintStream and {@link LoggerSeverity}.
 * @author salmelu
 */
class LoggerOutput {
	/** Stream associated with this output */
	private PrintStream logger;
	/** Minimal severity of this output */
	private LoggerSeverity sev;
	/** Formatter used for time formatting */
	private static SimpleDateFormat formatter;
	
	static {
		formatter = new SimpleDateFormat("hh:mm:ss");
	}
	
	/**
	 * Links a new output.
	 * @param logger Printstream used to print to the output
	 * @param s Minimal severity of the message required to be logged
	 */
	LoggerOutput(PrintStream logger, LoggerSeverity s) {
		this.logger = logger;
		this.sev = s;
	}
	
	/**
	 * Closes the PrintStream.
	 */
	public void close() {
		logger.close();
	}
	
	/**
	 * Logs a message to the stream.<br>
	 * The method is synchronized and therefore multiple threads cannot write to the same stream simultaneously.
	 * @param message Message to be logged
	 * @param s Severity of the message
	 */
	public synchronized void log(String message, LoggerSeverity s) {
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
	
	/**
	 * Logs a message to the stream if the severity of the message
	 *  is greater of equal to the severity of this stream.
	 * @param message Message to be logged
	 * @param s Severity of the message
	 */
	public void checkedLog(String message, LoggerSeverity s) {
		if(s.isGreaterOrEqual(sev)) log(message, s);
	}
	
	/**
	 * Changes the output's severity.
	 * @param s new Severity
	 */
	public void setSeverity(LoggerSeverity s) {
		this.sev = s;
	}
	
	/**
	 * Gets the output's severity.
	 * @return severity
	 */
	public LoggerSeverity getSeverity() {
		return sev;
	}
}