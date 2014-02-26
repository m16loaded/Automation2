package com.cellrox.infra;

import jsystem.framework.system.SystemObjectImpl;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;



public class WebDriverSO extends SystemObjectImpl {

	private String chromWebDriverLocation;
	protected WebDriver driver;
	
	public void init() throws Exception {
		report.report("init");
		super.init();
		System.setProperty("webdriver.firefox.driver","/");
//		System.setProperty("webdriver.chrome.driver", chromWebDriverLocation);
		this.driver = new FirefoxDriver();//new ChromeDriver(); //
//		System.setProperty("webdriver.chrome.driver","/home/topq/dev/chromedriver");
//		this.driver = new ChromeDriver();
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
