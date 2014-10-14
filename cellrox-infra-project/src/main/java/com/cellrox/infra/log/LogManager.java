package com.cellrox.infra.log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import jsystem.framework.system.SystemObjectImpl;
import jsystem.utils.FileUtils;

import com.cellrox.infra.log.Log.LogTypes;

/**
 * This class manages all configured logs (from SUT)<br>
 * The class act as an observer for explicit reboot events
 * 
 * @author entsec
 * 
 */
public class LogManager extends SystemObjectImpl {

	private HashMap<String, LogFileWriter> logFileWriters;
	private HashMap<String, LogMessageWaiter> logMessageWaiters;
	private HashMap<String, Integer> logMarkers;
	public ArrayList<Log> logTypes;
	// public TargetLinuxCliConnection cliConnection;
	public boolean isStopped = false;

	@Override
	public void init() throws Exception {
		super.init();
		logFileWriters = new HashMap<String, LogFileWriter>();
		logMessageWaiters = new HashMap<String, LogMessageWaiter>();
		logMarkers = new HashMap<String, Integer>();
	}

	public LogManager() {
		logTypes = new ArrayList<Log>();
		logFileWriters = new HashMap<String, LogFileWriter>();
		logMessageWaiters = new HashMap<String, LogMessageWaiter>();
		logMarkers = new HashMap<String, Integer>();

	}

	public void addLog(Log log) {
		logTypes.add(log);
	}

	public void populateLogManager() throws Exception {
		if (logTypes != null && logTypes.size() > 0) {
			for (Log log : logTypes) {
				// init log writers
				LogFileWriter logFileWriter = new LogFileWriter(log.getLogCommand(), log.getType(), log.getLogName());

				logFileWriters.put(log.getLogName(), logFileWriter);
				// // init log message waiters
				// LogMessageWaiter logMessageWaiter = new
				// LogMessageWaiter(log.getLogCommand(), log.getType(),
				// log.getLogName());
				// logMessageWaiters.put(log.getLogName(), logMessageWaiter);
				// logMarkers.put(log.getLogName(), 0);
			}
		}
	}

	/**
	 * 
	 * @param getter
	 * @param logName
	 * @return
	 * @throws Exception
	 */
	public ArrayList<String> getMessageFromLog(LogMessageGetter getter, String logName) throws Exception {
		String logFullName = getLogFullName(logName);
		LogFileWriter log = logFileWriters.get(logFullName);
		StringBuilder currentLog = log.getCurrentLog();
		getter.setCurrentLog(logMarkers.get(logFullName), currentLog.toString());
		ArrayList<String> results = getter.analyze();
		return results;
	}

	// /**
	// * This function will mark a log for later analysis
	// *
	// * @throws Exception
	// */
	// public void markLogs(String logName) throws Exception {
	// String logFullName = getLogFullName(logName);
	// // get the requested log
	// LogFileWriter log = logFileWriters.get(logFullName);
	// // get log last line number
	// int lastCurrentLogLineNumber = log.getLastLine();
	// report.report("Marking log " + logName + " at line " +
	// lastCurrentLogLineNumber);
	// // save the last current line
	// logMarkers.put(logFullName, lastCurrentLogLineNumber + 1);
	// }

	// /**
	// * This function will wait until:<br>
	// * - A requested string (<b>(can be a regex)</b> will appear in a
	// requested
	// * log<br>
	// * - Timeout<br>
	// * <b>Please note: </b> THIS IS A BLOCKING FUNCTION
	// *
	// * @return true if the string was found
	// * @throws Exception
	// */
	// public boolean waitForLogMessage(String logName, String filter, long
	// timeout, String stringToFind) throws Exception {
	// boolean found = false;
	// report.report("wait for logcat message filterd by " + filter +
	// " and contains: " + stringToFind);
	// LogMessageWaiter logMessageWaiter = logMessageWaiters.get(logName);
	// found = logMessageWaiter.waitForLineInLog(filter, timeout, stringToFind);
	// if (found) {
	// report.report("Message Found on Log " + logName + ": \"" +
	// logMessageWaiter.getmMsg() + "\"");
	// }
	// return found;
	// }

	/**
	 * This function will wait until:<br>
	 * - A requested string will appear in a requested log<br>
	 * - Timeout<br>
	 * <b>Please note: </b> THIS IS A BLOCKING FUNCTION
	 * 
	 * @param logName
	 * 
	 * 
	 * @return The function will return the requested string
	 * @throws Exception
	 */
	public String getAndWaitForLogMessage(String logName, String filter, long timeout, String stringToFind, int group) throws Exception {
		String logFullName = getLogFullName(logName);
		report.report("get log message filtered by " + filter + " and contains: " + stringToFind + " with timeout of " + timeout + " miliseconds");
		LogMessageWaiter logMessageWaiter = logMessageWaiters.get(logFullName);
		return logMessageWaiter.validateLineInLog(logMarkers.get(logFullName), filter, timeout, stringToFind, group);
	}

	/**
	 * This function will start writing all logs
	 * 
	 * @throws Exception
	 */
	public void startWritingAllLogs() throws Exception {
		isStopped = false;
		for (String logKey : logFileWriters.keySet()) {
			logFileWriters.get(logKey).startLogWriter();
		}
	}

	/**
	 * This function will stop writing logs and save them into the test's folder
	 * with the respective log name (from SUT)
	 * @param parser 
	 * 
	 * @throws Exception
	 */
	public void stopWritingAllLogs(LogParser parser) throws Exception {
		if (!isStopped) {
			isStopped = true;

			String localReportFolder = report.getCurrentTestFolder();
			for (String logKey : logFileWriters.keySet()) {
				String logFile = null;
//				if (isLogWithPath(logKey)) {
//					logFile = logKey + ".txt";
//				} else {
					logFile = localReportFolder + "\\" + logKey + ".txt";
//				}
				logFileWriters.get(logKey).stopLogWriter();
				FileUtils.write(logFile, logFileWriters.get(logKey).getLog());
				parser.addLogFile(logKey, new File(logFile));
			}
			parser.validateLogs();
			
		} else {
			System.out.println("Logs are already stopped");
			// report.report("Logs are already stopped");
		}
	}

	/**
	 * This function will notify all logs that a reboot event was sent by the
	 * user
	 * 
	 * @throws Exception
	 */
	public void notifyReboot() throws Exception {
		for (String logKey : logFileWriters.keySet()) {
			logFileWriters.get(logKey).rebootInProcess();
		}
	}

	/**
	 * This function will notify all logs that a reboot event that was sent by
	 * the user completed
	 * 
	 * @throws Exception
	 */
	public void notifyRebootCompleted() throws Exception {
		for (String logKey : logFileWriters.keySet()) {
			logFileWriters.get(logKey).rebootCompleted();
		}
		for (String key : logMarkers.keySet()) {
			logMarkers.put(key, 0);
		}
	}

	// private String stringArrayToString(String[] array, String separator) {
	// return StringUtils.join(array, separator);
	// }

	/**
	 * Return the log name as it is saved in the log map, because if path was
	 * given in SUT it is added to the log name and saved like that in the logs
	 * map. In this way the user can still invoke logManager APIs with the log
	 * name without the path and the full name will be calculated by this
	 * method.
	 * 
	 * @param logName
	 * @return
	 */
	private String getLogFullName(String logName) {
		for (Log log : logTypes) {
			if (true/* CoreBspUtils.isEmpty(log.getPath()) */) {
				if (log.getLogName().equals(logName)) {
					return log.getLogName(); // log name as in sut
				}
			} else {
				if (log.getLogName().substring(log.getPath().length()).equals(logName)) {
					return log.getLogName(); // this is a concatenation of the
												// path and log name from SUT
				}
			}
		}
		return null;
	}

	private boolean isLogWithPath(String logName) {
		for (Log log : logTypes) {
			if (log.getLogName().equals(logName) && log.getPath().isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public void parseLogs(LogParser parser) {
		
			
		
	}
}
