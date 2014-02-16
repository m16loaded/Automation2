package com.cellrox.infra.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CellRoxMgmLoginPage extends CellRoxAbstractPage {

	@FindBy(css = "#username")
	WebElement user;

	@FindBy(css = "#password")
	WebElement password;

	@FindBy(xpath = "//*[@value='Log In']")
	WebElement loginBtn;

	public CellRoxMgmLoginPage(WebDriver driver) throws InterruptedException {
		super(driver);
		driver.get("https://mdm-qa.cellrox.com");
		WebDriverWait wait = new WebDriverWait(driver, 20);
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@value='Log In']")));
		Thread.sleep(8000);
		PageFactory.initElements(driver, this);
	}

	public CellRoxMgmMainPage login(String username, String pass) throws Exception {
		user.sendKeys(username);
		Thread.sleep(5000);
		System.out.println(pass + "   !@#@!#$!$!@%@#@$!~@!$!@##!@");
		password.sendKeys(pass);
		Thread.sleep(5000);
		loginBtn.sendKeys(Keys.ENTER);
		
//		if(this.driver.findElement(By.cssSelector("#password"))!=null) {
//			System.out.println("sec time");
//			Thread.sleep(5000);
//			PageFactory.initElements(driver, this);
//			user.sendKeys(username);
//			Thread.sleep(1500);
//			System.out.println(pass + "   !@#@!#$!$!@%@#@$!~@!$!@##!@");
//			password.sendKeys(pass);
//			Thread.sleep(1500);
//			loginBtn.submit();
//		}
		
		return new CellRoxMgmMainPage(driver);
	}

}
