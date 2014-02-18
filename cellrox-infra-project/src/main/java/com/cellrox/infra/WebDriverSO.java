package com.cellrox.infra;

import jsystem.framework.system.SystemObjectImpl;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;



public class WebDriverSO extends SystemObjectImpl {

	
	protected WebDriver driver;
//	protected WebDriverSystemObject seleniumSystemObject;
	
	
	public void init() throws Exception {
		report.report("init");
		super.init();
		System.setProperty("webdriver.chrome.driver","/home/topq/dev/chromedriver");
		this.driver = new FirefoxDriver();//ChromeDriver();
//		System.setProperty("webdriver.chrome.driver","/home/topq/dev/chromedriver");
//		this.driver = new ChromeDriver();
	}
	
/*	public WebDriverSO() throws Exception {
		report.report("WebDriverSO");
		super.init();
		System.setProperty("webdriver.chrome.driver","/home/topq/dev/chromedriver");
		this.driver = new ChromeDriver();
	}
*/	
	public WebDriver getDriver() {
		return driver;
	}
	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}
//	public WebDriverSystemObject getSeleniumSystemObject() {
//		return seleniumSystemObject;
//	}
//	public void setSeleniumSystemObject(WebDriverSystemObject seleniumSystemObject) {
//		this.seleniumSystemObject = seleniumSystemObject;
//	}
}
