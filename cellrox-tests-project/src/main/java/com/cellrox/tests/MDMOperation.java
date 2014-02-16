package com.cellrox.tests;

import jsystem.framework.TestProperties;
import junit.framework.SystemTestCase4;

import org.jsystem.webdriver_so.WebDriverSystemObject;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

import com.cellrox.infra.pages.CellRoxMgmLoginPage;
import com.cellrox.infra.pages.CellRoxMgmMainPage;

public class MDMOperation extends SystemTestCase4 {
	
	 protected WebDriver driver;
	 protected WebDriverSystemObject seleniumSystemObject;
	 
	 

	@Before
	public void init() throws Exception {
		
//		System.setProperty("webdriver.chrome.driver","/home/topq/dev/chromedriver");
		seleniumSystemObject = (WebDriverSystemObject) system.getSystemObject("webDriverSystemObject");
        driver=seleniumSystemObject.getDriver();
        
		//TODO cant stay like this, should get inside of a SO
//		System.setProperty("webdriver.chrome.driver","/home/topq/dev/chromedriver");

		
	}
	
	
	@Test
	@TestProperties()
	public void connectToMdm() throws Exception {
		//driver = new ChromeDriver();
		
		CellRoxMgmLoginPage loginPage = new CellRoxMgmLoginPage(driver);
		CellRoxMgmMainPage mainPage= loginPage.login("or.garfunkel@top-q.co.il", "Automation10");
		mainPage.navigateToSite();
		
		/*driver.get("https://mdm-qa.cellrox.com");
		driver.findElement(By.id("username")).sendKeys("or.garfunkel@top-q.co.il");
		driver.findElement(By.id("password")).sendKeys("Automation10");
		driver.findElement(By.className("btn")).click();
		driver.navigate().to("https://mdm-qa.cellrox.com/automation");*/
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1");
		Thread.sleep(12000);
//		System.out.println("Strip here");
	}
	

}
