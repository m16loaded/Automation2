package com.cellrox.infra.log.writer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.cellrox.infra.log.StopWatch;

/**
 * This class is thread reading a "continues" log<br>
 * Use the <b>reboot</b> boolean from the calling class to stop/start writing.<br>
 * 
 * @author entsec
 * 
 */
public class ADBWriteReportTask extends AbsWriteReportTask {

	private Process mProccess;
	private StringBuilder log;
	private BufferedReader in;

	public ADBWriteReportTask(String logCommand) throws Exception {
		super(logCommand);
		log = new StringBuilder();
		reboot = false;
		in = buildProcess();
	}

	@Override
	public void run() {
		try {
			String line;
			while (!(Thread.currentThread().isInterrupted())) {
				while ((line = in.readLine()) != null) {
					log.append(line + "\n");
				}
			}
		} catch (Exception e) {

		}
	}

	/**
	 * This function builds a process and return its' input stream
	 * 
	 * @return
	 * @throws IOException
	 */
	private BufferedReader buildProcess() throws IOException {
		ProcessBuilder ps = new ProcessBuilder(logCommand.split(" ")); //TODO only adb shell ? 
		ps.redirectErrorStream(true);
		mProccess = ps.start();
		BufferedReader in = new BufferedReader(new InputStreamReader(mProccess.getInputStream()));
		return in;
	}

	/**
	 * This function will end the log writing thread<br>
	 * 
	 * @param resetLog
	 *            - if true the log will be reset
	 * @return
	 */
	public String endLogWriter(boolean resetLog) {
		mProccess.destroy();
		String currentLog = log.toString();
		log = new StringBuilder();
		return currentLog;
	}
	
	public StringBuilder getCurrentLog(){
		return log;
	}

	public boolean isReboot() {
		return reboot;
	}

	public void setReboot(boolean reboot) throws IOException {
		if (reboot) {
			stopLogging();
		} else {
			startLogging();
		}
		this.reboot = reboot;
	}

	/**
	 * Start logging and write message to the log file
	 * 
	 * @throws IOException
	 */
	private void startLogging() throws IOException {
		System.out.println("DEBUG : START LOG AFTER REBOOT");
		StopWatch.stop();
		log.append(String.format("******************Automation Invoked Reboot - Done in %d sec.******************" + "\n", StopWatch.getElapsedTimeSecs()));
		if (mProccess == null) {

			in = buildProcess();
		}
	}

	/**
	 * Stop logging and write message to the log file
	 * 
	 * @throws IOException
	 */
	private void stopLogging() {
		System.out.println("DEBUG : STOP LOG AFTER REBOOT");
		mProccess.destroy();
		mProccess = null;
		StopWatch.start();
		log.append("******************Automation Invoked Reboot******************" + "\n");
	}

}
