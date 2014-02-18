package com.cellrox.infra.pages;

import jsystem.framework.system.SystemObjectImpl;

import org.openqa.selenium.WebDriver;

public abstract class CellRoxAbstractPage extends SystemObjectImpl {

	WebDriver driver;
	
	public CellRoxAbstractPage(WebDriver driver) {
		this.driver = driver;
	}
}
