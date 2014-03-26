package com.cellrox.infra.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CellroxMdmLoginPage extends CellRoxAbstractPage {

	protected String siteUrl; 
	
	@FindBy(css = "#username")
	WebElement user;

	@FindBy(css = "#password")
	WebElement password;

	@FindBy(xpath = "//*[@value='Log In']")
	WebElement loginBtn;

	public CellroxMdmLoginPage(WebDriver driver, String siteUrl) throws InterruptedException {
		super(driver);
		this.siteUrl = siteUrl;
		driver.get(siteUrl+"users/sign_in");
		driver.get(siteUrl+"users/sign_in");
		WebDriverWait wait = new WebDriverWait(driver, 30);
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@value='Log In']")));
		Thread.sleep(2000);
		PageFactory.initElements(driver, this);
	}

	public CellRoxMgmMainPage login(String username, String pass) throws Exception {
		
		report.report("Login to MDM.");
		Thread.sleep(1000);
		PageFactory.initElements(driver, this);
		user.clear();
		Thread.sleep(1000);
		user.sendKeys(username);
		Thread.sleep(1000);
		password.clear();
		Thread.sleep(1000);
		password.sendKeys(pass);
		Thread.sleep(1000);
		loginBtn.submit();
		Thread.sleep(2000);
		
		return new CellRoxMgmMainPage(driver);
	}

}
