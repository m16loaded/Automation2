package com.cellrox.infra.log.waiter;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.cellrox.infra.log.interfaces.ILogListener;


public class ADBReturnMessageTask extends AbsWaitForReportTask {

	private Process mProccess;

	public ADBReturnMessageTask(String logCommand, boolean returnMessage, String filter, String searchParams, int group, int startingLine) {
		super(logCommand, returnMessage, filter, searchParams);
		this.startingLine = startingLine;
		this.group = group;
	}

	//	public ADBReturnMessageTask(String logCommand, String filter, String searchParams,int group,int startingLine) {
	//		this.mFilter = filter;
	//		this.mSearchParams = searchParams;
	//		this.logCommand = logCommand;
	//		this.group = group;
	//		this.startingLine = startingLine;
	//	}

	@Override
	public void run() {
		try {
			int lineNumber = 0;
			//			ProcessBuilder ps = new ProcessBuilder("adb", "shell", logCommand + " | grep -F '" + mFilter + "'");
			ProcessBuilder ps = new ProcessBuilder("adb", "shell", logCommand);
			ps.redirectErrorStream(true);
			mProccess = ps.start();
			BufferedReader in = new BufferedReader(new InputStreamReader(mProccess.getInputStream()));
			String line;
			System.out.println("Starting at line: " + startingLine);
			while (!(Thread.currentThread().isInterrupted()) & (line = in.readLine()) != null) {
				if (lineNumber < startingLine) {
					lineNumber++;
					continue;
				}
				if (stringContainsAllItems(line)) {
					System.out.println("Found String in Log : " + line);
					mProccess.destroy();
					notifyListener(findText.getCounter());
				}
				lineNumber++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setListener(ILogListener listener) {
		this.mLogListener = listener;
	}

	@Override
	public void notifyListener() {
		mLogListener.onNotify();
	}

	public void stop() {
		mProccess.destroy();
	}

	@Override
	public void notifyListener(String report) {
		mLogListener.onNotify(report);
	}

}
