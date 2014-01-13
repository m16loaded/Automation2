package com.cellrox.infra.enums;

public enum State {
	
	ON("ON"), OFF("OFF");
	String value;

	private State(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}

}
