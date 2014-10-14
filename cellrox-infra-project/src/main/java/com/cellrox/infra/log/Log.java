package com.cellrox.infra.log;

import jsystem.framework.system.SystemObjectImpl;

public class Log extends SystemObjectImpl {
	
	

	public Log(String logName, String logCommand, LogTypes type) {
		this.logName = logName;
		this.logCommand = logCommand;
		this.type = type;
	}

	public enum LogTypes {
		ADB, Serial, CLI;
	}

	private String path;
	private String logName;
	private String logCommand;
	private LogTypes type;

	public LogTypes getType() {
		return type;
	}

	public void setType(LogTypes type) {
		this.type = type;
	}

	@Override
	public void init() throws Exception {
		super.init();
		if (this.path != null) {
			setLogName(path + logName);
		}
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getLogName() {
		return logName;
	}

	public void setLogName(String logName) {
		this.logName = logName;
	}

	public String getLogCommand() {
		return logCommand;
	}

	public void setLogCommand(String logCommand) {
		this.logCommand = logCommand;
	}

}
