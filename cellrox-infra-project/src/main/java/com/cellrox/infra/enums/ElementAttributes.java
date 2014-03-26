package com.cellrox.infra.enums;

public enum ElementAttributes {
	
	ENABLED("eneabled"), CLICKABLE("clickable");
	
	String attribute;

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	private ElementAttributes(String attribute) {
		this.attribute = attribute;
	}
	
	@Override
	public String toString() {
		return attribute;
	}
}
