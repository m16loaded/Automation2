package com.cellrox.infra.pages;

import org.openqa.selenium.WebDriver;

public abstract class CellRoxAbstractPage {

	WebDriver driver;
	
	public CellRoxAbstractPage(WebDriver driver) {
		this.driver = driver;
	}
}
