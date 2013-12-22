package com.cellrox.infra.enums;

public enum Color {
	RED("color=\"red\""), ORANGE("color=\"orange\""), BLUE("color=\"blue\""), GREEN(
			"color=\"green\"");
	String color;

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	private Color(String color) {
		this.color = color;
	}

}
