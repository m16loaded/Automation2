package com.cellrox.infra.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class CellroxOwnerAbstractPage extends CellRoxAbstractPage {

	protected String siteUrl; 
	
	@FindBy(xpath = "//*[@value='ENROLL']")
	WebElement enrollBtn;
	
	public CellroxOwnerAbstractPage(WebDriver driver, String siteUrl) {
		super(driver);
		this.siteUrl = siteUrl;
		driver.get(siteUrl+"owners");

	}
	
	
	public void selectTheCorrectOwnerAndEnroll(String ownerName) throws InterruptedException {
		
		Thread.sleep(3000);
		List<WebElement> weList = driver.findElements(By.xpath("//*[@class='ngCellText ng-scope col1 colt1']/span")); 
		List<WebElement> weList2 = driver.findElements(By.cssSelector(".ngSelectionCheckbox")); 
		
		for(int i=0; i<weList.size() ; i++) {
			WebElement webElement = weList.get(i);
			String imeiElement = "";
			imeiElement = webElement.getText();
			if(imeiElement.trim().equals(ownerName)) {
				//the wanted element is founded
				weList2.get(i).click();
				report.report("Clicking the owner.");
				break;
			}
		}
		
		Thread.sleep(3000);
		PageFactory.initElements(driver, this);
		enrollBtn.click();
		Thread.sleep(2000);
		driver.findElements(By.cssSelector(".btn")).get(driver.findElements(By.cssSelector(".btn")).size()-1).submit();
		Thread.sleep(5000);
		
	}

}
