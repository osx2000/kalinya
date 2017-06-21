package com.kalinya.oc.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.kalinya.util.PluginUtil;
import com.kalinya.util.StringUtil;
import com.olf.openjvs.OException;
import com.olf.openjvs.Util;
import com.olf.openrisk.application.Debug;
import com.olf.openrisk.application.EnumMessageSeverity;
import com.olf.openrisk.application.Session;
import com.olf.openrisk.internal.OpenRiskException;
import com.olf.openrisk.market.Market;
import com.olf.openrisk.table.Table;
import com.olf.openrisk.utility.Disposable;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationsException;

public class MessageLog implements Disposable {

	private static final int MAX_LOG_FILE_STRING_LENGTH = 496;

	/**
	 * DIR_NODE_PATH is the folder in the database where the log file will be
	 * saved before they are emailed.
	 * <p>
	 * Note that the log files are saved to the daily reports directory; this is
	 * an additional location that supports emailing the log file.
	 * <p>
	 * E.g. DIR_NODE_PATH = "/User/Log Files/"
	 */
	private static String DIR_NODE_PATH = "/User/Log Files/";

	private Session session;
	private Debug debug;
	private String errorLogFile;
	private Class<?> caller;
	private Collection<Exception> exceptions;

	public MessageLog(Class<?> caller) {
		this(null, caller);
	}
	
	public MessageLog(Session session, Class<?> caller) {
		this.session = session;
		if(session != null) {
			this.debug = session.getDebug();
		}
		this.errorLogFile = caller.getSimpleName() + ".log";
		this.caller = caller;
		exceptions = new ArrayList<>();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Caller [" + getCaller().getSimpleName() + "] ");
		sb.append("HasExceptions [" + String.valueOf(hasExceptions()) + "]");
		return sb.toString();
	}
	
	public Class<?> getCaller() {
		return caller;
	}
	
	@Override
	public void dispose() {
		debug = null;
		session = null;
	}
	
	@SuppressWarnings("static-method")
	public void info(String message) {
		String extendedMessage = String.format("INFO: %s", message);
		if(session == null) {
			System.out.println(extendedMessage);
		} else {
			debug.printLine(extendedMessage);
			debug.logError(message, EnumMessageSeverity.Error);
			printToFile("ERROR", message);
		}
	}
	
	/**
	 * Prints each element of the collection
	 * 
	 * @param collection
	 */
	public <E> void info(java.util.Collection<E> collection) {
		for(E e: collection) {
			info(e.toString());
		}
	}
	
	/**
	 * Prints each element of the array
	 * 
	 * @param dbls
	 * @param precision
	 */
	public void info(double[] dbls, int precision) {
		for(double d: dbls) {
			info(StringUtil.formatDouble(d, precision));
		}
	}

	/**
	 * Prints to a log file. There is a limit on the string length so it breaks it down into chunks
	 * @param logStatus
	 * @param message
	 */
	private void printToFile(String logStatus, String message) {
		int numChunks = (int) (message.length()/MAX_LOG_FILE_STRING_LENGTH) + 1;
		int startIndex = 0;
		for(int chunk = 1; chunk <= numChunks; chunk++) {
			int endIndex = Math.min(chunk*MAX_LOG_FILE_STRING_LENGTH, message.length());
			try {
				Util.errorLogMessage(errorLogFile, logStatus, message.substring(startIndex, endIndex));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			startIndex = endIndex;
		}
	}

	/**
	 * Logs an error message to the error log console and a log file and posts
	 * the status to the System Monitor
	 * 
	 * @param message
	 */
	public void error(String message) {
		String extendedMessage = String.format("ERROR: %s", message);
		if(session == null) {
			System.err.println(extendedMessage);
		} else {
			debug.printLine(extendedMessage);
			debug.logError(message, EnumMessageSeverity.Error);
			printToFile("ERROR", message);
		}
	}
	
	/**
	 * Prints each element of the collection
	 * 
	 * @param collection
	 */
	public <E> void error(Collection<E> collection) {
		for(E e: collection) {
			error(e.toString());
		}
	}
	
	/**
	 * Logs an error message to the error log console and a log file and posts
	 * the status to the System Monitor
	 * 
	 * @param message
	 */
	public void warning(String message) {
		String extendedMessage = String.format("WARN: ",  message);
		if(session == null) {
			System.out.println(extendedMessage);
		} else {
			debug.printLine(extendedMessage);
			debug.logError(message, EnumMessageSeverity.Warning);
		}
	}
	
	/**
	 * Prints each element of the collection
	 * 
	 * @param collection
	 */
	public <E> void warning(Collection<E> collection) {
		for(E e: collection) {
			warning(e.toString());
		}
	}

	/**
	 * Saves the log to the database
	 * 
	 * @return
	 */
	public String saveLogToDb() {
		info("Saving log to database");

		// dirNodeDestination is the full path plus log file name in the
		// database
		String dirNodeDestination = DIR_NODE_PATH + errorLogFile;
		info("[" + dirNodeDestination + "]");
		return dirNodeDestination;
	}

	public static String getMemoryUsageString(Session session) {
		long findurMemory = session.getMemorySize();
		long freeMemory = getFreeMemory();
		long totalMemory = getTotalMemory();
		long maxMemory = getMaxMemory();
		int availableProcessors = getAvailableProcessors();
		return String
				.format("Findur Mem : %s, Free Memory : %s, Total Memory %s, Max Memory %s, Num CPUs : %s - %s",
						byteCountToDisplaySize(findurMemory),
						byteCountToDisplaySize(freeMemory),
						byteCountToDisplaySize(totalMemory),
						byteCountToDisplaySize(maxMemory),
						availableProcessors,
						"java version : " + System.getProperty("java.version")
						);
	}

	/**
	 * @return total number of processors or cores available to the JVM
	 */
	public static int getAvailableProcessors() {
		return Runtime.getRuntime().availableProcessors();
	}

	/**
	 * Total amount of free memory available to the JVM
	 * @return total amount of free memory available to the JVM in bytes
	 */
	public static long getFreeMemory() {
		return Runtime.getRuntime().freeMemory();
	}

	/**
	 * Maximum amount of memory the JVM will attempt to use
	 * @return maximum amount of memory the JVM will attempt to use in bytes.
	 *         Long.MAX_VALUE if there is no preset limit
	 */
	public static long getMaxMemory() {
		return Runtime.getRuntime().maxMemory();
	}

	/**
	 * Total memory currently available to the JVM
	 * @return total memory in bytes
	 */
	public static long getTotalMemory() {
		return Runtime.getRuntime().totalMemory();
	}

	/**
	 * The number of bytes in a kilobyte.
	 */
	public static final long ONE_KB = 1024;
	/**
	 * The number of bytes in a megabyte.
	 */
	public static final long ONE_MB = ONE_KB * ONE_KB;
	/**
	 * The number of bytes in a gigabyte.
	 */
	public static final long ONE_GB = ONE_KB * ONE_MB;
	/**
	 * The number of bytes in a terabyte.
	 */
	public static final long ONE_TB = ONE_KB * ONE_GB;
	/**
	 * The number of bytes in a petabyte.
	 */
	public static final long ONE_PB = ONE_KB * ONE_TB;
	/**
	 * The number of bytes in an exabyte.
	 */
	public static final long ONE_EB = ONE_KB * ONE_PB;

	public static String byteCountToDisplaySize(long size) {
		String displaySize;
		int threshold = 10;
		if (size / ONE_EB > threshold) {
			displaySize = String.valueOf(size / ONE_EB) + " EB";
		} else if (size / ONE_PB > threshold) {
			displaySize = String.valueOf(size / ONE_PB) + " PB";
		} else if (size / ONE_TB > threshold) {
			displaySize = String.valueOf(size / ONE_TB) + " TB";
		} else if (size / ONE_GB > threshold) {
			displaySize = String.valueOf(size / ONE_GB) + " GB";
		} else if (size / ONE_MB > threshold) {
			displaySize = String.valueOf(size / ONE_MB) + " MB";
		} else if (size / ONE_KB > threshold) {
			displaySize = String.valueOf(size / ONE_KB) + " KB";
		} else {
			displaySize = String.valueOf(size) + " bytes";
		}

		return displaySize;
	}

	/**
	 * Returns the user's market manager date using the lighter JVS method OCalendar.today()
	 * @param session
	 * @return
	 */
	public static Date getCurrentDate(Session session) {
		Market market = session.getMarket();
		Date currentDate = market.getCurrentDate();
		PluginUtil.dispose(market);
		return currentDate;
	}

	/**
	 * If i is zero or greater than one, returns an 's' to support correct
	 * noun-count grammar in message logging
	 * 
	 * @param i
	 * @return
	 */
	public static String pluralize(int i) {
		return ((i==0||i > 1)? "s":"");
	}

	/**
	 * Logs the exception.getMessage() and the stack 
	 * @param e
	 */
	public void logException(Exception e) {
		getExceptions().add(e);
		if(e instanceof IllegalAnnotationsException) {
			error(((IllegalAnnotationsException) e).getErrors());
		}
		if(e.getMessage() == null && e.getCause() != null) {
			//Instances of MarshalException have the message on the cause
			error(e.getClass() + ": " + e.getCause().getMessage());
		} else {
			error(e.getClass() + ": " + e.getMessage());
		}
		for(StackTraceElement ste: e.getStackTrace()) {
			error(ste.toString());
		}
	}
	
	/**
	 * For each exception in the collection, logs the exception.getMessage()
	 * @param exceptions
	 */
	public void logExceptions(Collection<? extends Exception> exceptions) {
		for(Exception exception: exceptions) {
			error(exception.getMessage());
		}
	}
	
	/**
	 * Logs the error.getMessage() and the stack 
	 * @param e
	 */
	public void logError(Error e) {
		error(e.getClass() + ": " + e.getMessage());
		for(StackTraceElement ste: e.getStackTrace()) {
			error(ste.toString());
		}
	}

	/**
	 * Returns the name of the log file
	 * 
	 * @return
	 */
	public String getErrorLogFile() {
		return errorLogFile;
	}

	public Session getSession() {
		return session;
	}

	/**
	 * Prints the contents of an OC table to the log file
	 * 
	 * @param jvsTable
	 */
	public void printTable(Table table) {
		printTable(session.getTableFactory().toOpenJvs(table));
	}

	/**
	 * Prints the contents of a JVS table to the log file
	 * 
	 * @param jvsTable
	 */
	public void printTable(com.olf.openjvs.Table jvsTable) {
		try {
			jvsTable.printTableAppendToFile(Util.reportGetDirForToday() + "\\" + getErrorLogFile());
		} catch (OException e) {
			throw new OpenRiskException(e);
		}
	}
	

	public Collection<Exception> getExceptions() {
		return exceptions;
	}
	
	public boolean hasExceptions() {
		return getExceptions().size() > 0;
	}

	public void printExceptions() {
		info("[" + getExceptions().size() + "] exception" + MessageLog.pluralize(getExceptions().size()) + " reported");
		for(Exception e: getExceptions()) {
			error(e.getClass() + ": " + e.getMessage());
			for(StackTraceElement ste: e.getStackTrace()) {
				error(ste.toString());
			}
		}
	}
	
	/**
	 * Logs an info message that a process is starting
	 */
	public void start() {
		info("START [" + getCaller().getSimpleName() + "]");
	}
	
	/**
	 * Logs an info message that a process is ending
	 */
	public void end() {
		info("END [" + getCaller().getSimpleName() + "]");
	}
}
