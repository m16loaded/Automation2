package com.cellrox.infra.enums;

public enum DeviceNumber {

	PRIMARY("primary"), SECONDARY("secondary");
	String value;

	private DeviceNumber(String value) {
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

	public DeviceNumber getDeviceNumber(String deviceNumber) {
		if (deviceNumber.toLowerCase().trim().equals("primary")) {
			return PRIMARY;
		} else if (deviceNumber.toLowerCase().trim().equals("secondary")) {
			return SECONDARY;
		}
		return null;
	}
}
