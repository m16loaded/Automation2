package com.cellrox.infra.log.writer;

import java.io.IOException;

import jsystem.framework.report.JSystemListeners;
import jsystem.framework.report.ListenerstManager;

public abstract class AbsWriteReportTask implements Runnable {

	protected String logCommand;
	protected boolean reboot;
//	protected TargetLinuxCliConnection cliConnection;
	protected String logName;
	protected static JSystemListeners report = ListenerstManager.getInstance();

	
	public abstract String endLogWriter(boolean resetLog);

	public abstract StringBuilder getCurrentLog();

	public abstract boolean isReboot();

	public abstract void setReboot(boolean reboot) throws IOException;
	


	
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

	public AbsWriteReportTask(String logCommand) throws Exception {
		this.logCommand = logCommand;
	}

	
	public String getLogName() {
		return logName;
	}

	public void setLogName(String logName) {
		this.logName = logName;
	}

}
