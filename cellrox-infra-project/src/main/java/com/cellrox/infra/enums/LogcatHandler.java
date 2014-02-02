package com.cellrox.infra.enums;

public enum LogcatHandler {

	PRIV("priv-LogsReady"),HOST("host-LogsReady"),CORP("corp-LogsReady");
	
	String value;



	public void setValue(String value) {
		this.value = value;
	}

	private LogcatHandler(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return value;
	}

}
