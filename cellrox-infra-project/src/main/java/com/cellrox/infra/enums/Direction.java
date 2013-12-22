package com.cellrox.infra.enums;

public enum Direction {
	
	UP("up"),DOWN("down"),LEFT("left"),RIGHT("right");
	
	String dir;

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	private Direction(String dir) {
		this.dir = dir;
	}
	
	@Override
	public String toString() {
		return dir;
	}

}
