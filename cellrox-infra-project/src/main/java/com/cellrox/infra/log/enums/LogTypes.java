package com.cellrox.infra.log.enums;

public enum LogTypes {

	LOGCAT("logcat -v time"), KMSG("cat /proc/kmsg");
	String logCommand;

	private LogTypes(String logCommand) {
		this.logCommand = logCommand;
	}

	public String getLogCommand() {
		return logCommand;
	}

	public void setLogCommand(String logCommand) {
		this.logCommand = logCommand;
	}

}
