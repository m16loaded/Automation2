package com.cellrox.infra.enums;

public enum Platform {
	
	nexus4("nexus4"), nexus6("nexus6");
	
	private String value;

	private Platform(String value) {
		this.value = value;
		

}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public Platform getPlatform(String Platform) {
		if (Platform.toLowerCase().trim().equals("nexus4")) {
			return nexus4;
		} else if (Platform.toLowerCase().trim().equals("nexus6")) {
			return nexus6;
		}
		return null;
	}
}
