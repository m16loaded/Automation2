package com.cellrox.infra.log;

import com.cellrox.infra.log.Log.LogTypes;
import com.cellrox.infra.log.interfaces.ILogRebootListner;
import com.cellrox.infra.log.writer.ADBWriteReportTask;
import com.cellrox.infra.log.writer.AbsWriteReportTask;


/**
 * This class runs a thread that write the log into a string<br>
 * This is a generic class , any "continues" log can be written<br>
 * For example: "logcat", "cat /dev/kmsg", etc.
 * 
 * @author entsec
 * 
 */
public class LogFileWriter implements ILogRebootListner {

	private AbsWriteReportTask task;
	private Thread t;
	private String logCommand;
	private String log;
//	private TargetLinuxCliConnection  cliConnection;
	private String logName;

	


	public LogFileWriter(String logCommand, LogTypes logTypes,String logName) throws Exception {
		this.logCommand = logCommand;
		this.logName = logName;
		if (logTypes == LogTypes.ADB) {
			task = new ADBWriteReportTask(logCommand);
		}
	
		
	}
//	
//	/**
//	 * This function will return the last line of log
//	 */
//	public int getLastLine() throws Exception{
//		int lastCurrentLogLineNumber = 0;
//		// if serial connection - use WC
//		if (cliConnection!=null){
//			CliCommand cmd = new CliCommand("wc -l "+logName+".log");
//			FindText findText = new FindText("(\\d+)\\s*"+logName+".log",true,false,2);
//			cmd.addAnalyzers(findText);
//			cliConnection.handleCliCommand("wc",cmd);
//			lastCurrentLogLineNumber = Integer.parseInt(findText.getCounter());
//		}else{
//			StringBuilder currentLog = getCurrentLog();
//			// get log last line number
//			lastCurrentLogLineNumber = currentLog.toString().split("\\n").length;
//		}
//		
//		return lastCurrentLogLineNumber;
//	}

	/**
	 * Start writing the log
	 * 
	 * @throws Exception
	 */
	public void startLogWriter() throws Exception {
		t = new Thread(task);
		t.start();
	}

	/**
	 * Stop writing the log, as default clears the log for later tests
	 */
	public void stopLogWriter() {
		stopLogWriter(true);
	}

	/**
	 * Stop writing the log
	 * 
	 * @param resetLog
	 *            - if true clear log
	 */
	public void stopLogWriter(boolean resetLog) {
		log = task.endLogWriter(resetLog);
		if(t != null) {
			t.interrupt();
		}
	}

	/**
	 * This function implements the {@link ILogRebootListner} interface for the
	 * observer pattern
	 */
	@Override
	public void rebootInProcess() throws Exception {
		task.setReboot(true);
	}

	/**
	 * This function implements the <b>ILogRebootListner</b> interface for the
	 * observer pattern
	 */
	@Override
	public void rebootCompleted() throws Exception {
		task.setReboot(false);
	}

	public StringBuilder getCurrentLog() {
		return task.getCurrentLog();
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}
	
//	public TargetLinuxCliConnection getCliConnection() {
//		return cliConnection;
//	}
//
//	public void setCliConnection(TargetLinuxCliConnection cliConnection) {
//		this.cliConnection = cliConnection;
//		task.setCliConnection(cliConnection);
//	}
	
	public String getLogName() {
		return logName;
	}

	public void setLogName(String logName) {
		this.logName = logName;
	}
}