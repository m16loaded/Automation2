package com.cellrox.infra.object;

import com.cellrox.infra.enums.Color;

public class LogParserExpression {
	String expression;
	String niceName;
	Color color;
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	public String getNiceName() {
		return niceName;
	}
	public void setNiceName(String niceName) {
		this.niceName = niceName;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public LogParserExpression() {
		super();
	}
	public String getHtmlColor(){
		return color.getColor();
	}
	
	

}
