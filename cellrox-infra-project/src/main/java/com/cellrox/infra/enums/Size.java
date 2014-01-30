package com.cellrox.infra.enums;

public enum Size {
	
	Bigger("Bigger"),Smaller("Smaller");
	
	String size;

	public String getDir() {
		return size;
	}

	public void setSize(String dir) {
		this.size = dir;
	}

	private Size(String size) {
		this.size = size;
	}
	
	@Override
	public String toString() {
		return size;
	}

}