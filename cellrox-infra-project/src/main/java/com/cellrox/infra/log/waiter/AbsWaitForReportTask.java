package com.cellrox.infra.log.waiter;

import jsystem.extensions.analyzers.text.FindText;

import com.cellrox.infra.log.interfaces.ILogListener;
import com.cellrox.infra.log.interfaces.IReportLogNotifier;



public abstract class AbsWaitForReportTask implements Runnable, IReportLogNotifier {

	protected String logCommand;
	protected boolean reboot;
//	protected TargetLinuxCliConnection cliConnection;
	protected String logName;
	protected int group;
	protected int startingLine;

	protected String mFilter;
	protected String mSearchParams;
	protected ILogListener mLogListener;
	protected boolean mReturnMessage = false;
	protected FindText findText;

	protected AbsWaitForReportTask(String logCommand, boolean returnMessage, String filter, String searchParams) {
		this.mReturnMessage = returnMessage;
		this.mFilter = filter;
		this.mSearchParams = searchParams;
		this.logCommand = logCommand;
	}

	protected boolean stringContainsAllItems(String logLine) {
		if (group != -1) {
			findText = new FindText(mFilter + ".*" + mSearchParams, true, false, group);
		} else {
			findText = new FindText(mFilter + ".*" + mSearchParams, true);
		}
		findText.setTestAgainst(logLine);
		findText.analyze();
		return findText.getStatus();

	}

	public String getLogCommand() {
		return logCommand;
	}

	public void setLogCommand(String logCommand) {
		this.logCommand = logCommand;
	}

//	public TargetLinuxCliConnection getCliConnection() {
//		return cliConnection;
//	}
//
//	public void setCliConnection(TargetLinuxCliConnection cliConnection) {
//		this.cliConnection = cliConnection;
//	}

	public AbsWaitForReportTask(String logCommand) throws Exception {
		this.logCommand = logCommand;
	}

	public String getLogName() {
		return logName;
	}

	public void setLogName(String logName) {
		this.logName = logName;
	}

	public String getmFilter() {
		return mFilter;
	}

	public void setmFilter(String mFilter) {
		this.mFilter = mFilter;
	}

	public String getmSearchParams() {
		return mSearchParams;
	}

	public void setmSearchParams(String mSearchParams) {
		this.mSearchParams = mSearchParams;
	}

	public ILogListener getmLogcatListener() {
		return mLogListener;
	}

	public void setmLogcatListener(ILogListener mLogcatListener) {
		this.mLogListener = mLogcatListener;
	}

	public boolean ismReturnMessage() {
		return mReturnMessage;
	}

	public void setmReturnMessage(boolean mReturnMessage) {
		this.mReturnMessage = mReturnMessage;
	}

	public abstract void stop();

	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
	}

	public int getStartingLine() {
		return startingLine;
	}

	public void setStartingLine(int startingLine) {
		this.startingLine = startingLine;
	}

}
