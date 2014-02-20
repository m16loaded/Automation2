package com.cellrox.infra.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CellRoxMgmMainPage extends CellRoxAbstractPage {

	@FindBy(xpath = "//li/a[text()='Sites']")
	WebElement siteBtn;
	
	@FindBy(xpath = "//li/a[text()='Dashboard']")
	WebElement dashboardBtn;
	
	@FindBy(xpath = "//li/a[text()='Admin']")
	WebElement adminBtn;
	
	public CellRoxMgmMainPage(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}

	public void navigateToSite() {

		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//li/a[text()='Sites']")));
		String href = siteBtn.getAttribute("href");
		report.report("About to navigate to : " + href);
		driver.get(href);

	}

}
