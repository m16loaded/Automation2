package com.cellrox.infra.log;

import com.cellrox.infra.log.Log.LogTypes;
import com.cellrox.infra.log.interfaces.ILogListener;
import com.cellrox.infra.log.waiter.ADBReturnMessageTask;
import com.cellrox.infra.log.waiter.AbsWaitForReportTask;

public class LogMessageWaiter {

	private boolean mFound = false;
	private String mMsg = null;
	private ILogListener mListener;
	private String logCommand;

	private String logName;
	AbsWaitForReportTask task ;

	public LogMessageWaiter(String logCommand, LogTypes logTypes,String logName) {
		super();
		this.logCommand = logCommand;
		this.logName = logName;
		
		if (logTypes == LogTypes.ADB){
			task = new ADBReturnMessageTask(logCommand, true, "", "", 0, 0);
		}
		
		
		mListener = new ILogListener() {

			@Override
			public void onNotify() {
				mFound = true;
			}

			@Override
			public void onNotify(String report) {
				mFound = true;
				mMsg = report;
			}
		};
	}

//	public LogMessageWaiter() {
//
//		mListener = new ILogcatListener() {
//
//			@Override
//			public void onNotify() {
//				mFound = true;
//			}
//
//			@Override
//			public void onNotify(String report) {
//				mFound = true;
//				mMsg = report;
//			}
//		};
//	}

////	public boolean waitForLineInLog(String filter, long timeout, String messageFilters) throws Exception {
////
////		task = new ADBFindMessageTask(logCommand, false, filter, messageFilters);
////		task.setListener(mListener);
////		Thread t = new Thread(task);
////		t.start();
////
////		final long start = System.currentTimeMillis();
////
////		while (!mFound) {
////
////			if ((System.currentTimeMillis() - start > timeout)) {
////				task.stop();
////				t.interrupt();
////				break;
////			}
////			Thread.sleep(1000);
////
////		}
////		return mFound;
//	}

	public String validateLineInLog(int startingLine, String filter, long timeout, String messageFilters, int group) throws Exception {

		mMsg = null;
		mFound = false;

		task.setmFilter(filter);
		task.setmSearchParams(messageFilters);
		task.setGroup(group);
		task.setStartingLine(startingLine);
		task.setLogName(logName);
		
		
		task.setListener(mListener);
		Thread t = new Thread(task);
		t.start();

		final long start = System.currentTimeMillis();

		while (mMsg == null) {

			if ((System.currentTimeMillis() - start > timeout)) {
				task.stop();
				t.interrupt();
				break;
			}
			Thread.sleep(1000);
		}
		t.interrupt();
		return mMsg;
	}

	public String getmMsg() {
		return mMsg;
	}

	public void setmMsg(String mMsg) {
		this.mMsg = mMsg;
	}

//	public TargetLinuxCliConnection getCliConnection() {
//		return cliConnection;
//	}
//
//	public void setCliConnection(TargetLinuxCliConnection cliConnection) {
//		this.cliConnection = cliConnection;
//		task.setCliConnection(cliConnection);
//	}

}