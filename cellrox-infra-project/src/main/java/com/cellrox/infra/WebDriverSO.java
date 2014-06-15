package com.cellrox.infra;

import jsystem.framework.system.SystemObjectImpl;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class WebDriverSO extends SystemObjectImpl {

	private String chromWebDriverLocation;
	protected WebDriver driver;

	public void init() throws Exception {
		report.report("init");
		super.init();
		// this.driver = new FirefoxDriver();
		// for chrom use :
		System.setProperty("webdriver.chrome.driver", chromWebDriverLocation);
		this.driver = new ChromeDriver();

	}

	@Override
	public void close() {
		driver.close();
		super.close();
	}

	public WebDriver getDriver() {
		return driver;
	}

	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}

	public String getChromWebDriverLocation() {
		return chromWebDriverLocation;
	}

	public void setChromWebDriverLocation(String chromWebDriverLocation) {
		this.chromWebDriverLocation = chromWebDriverLocation;
	}
}
