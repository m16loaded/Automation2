package com.cellrox.infra.pages;

import java.util.List;
import java.util.Map;

import jsystem.framework.report.Reporter;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CellroxAutomationDevicesAbstractPage extends CellRoxAbstractPage {

	protected String siteUrl; 
	
	@FindBy(xpath = "//*[@value='REMOVE']")
	WebElement removeBtn;
	
	public CellroxAutomationDevicesAbstractPage(WebDriver driver, String siteUrl) {
		super(driver);
		this.siteUrl = siteUrl;
		driver.get(siteUrl+"devices");
		WebDriverWait wait = new WebDriverWait(driver, 30);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@value='REMOVE']")));
		PageFactory.initElements(driver, this);
	}

	/**
	 * The function run the validation of the data over and over within the timeout
	 * */
	public boolean validateDataDeviceDetails(String imei , String macAdr, Map<String,String> compareMap, long timeout) throws InterruptedException {
		
		long startTime = System.currentTimeMillis();
		while((System.currentTimeMillis() - startTime) < timeout) {
			
			if(validateDataDeviceDetails(imei, macAdr, compareMap)) {
				report.report("All the variables is correct comparing the phone to the MDM");
				return true;
			}
			Thread.sleep(1000);
		}
		
		report.report("All the variables isn't correct comparing the phone to the MDM", Reporter.FAIL);
		return true;
		
	}
	
	public boolean clickOnTheDeviceCheckBox(String imei , String macAdr) {
		boolean status = false;
		List<WebElement> weList = driver.findElements(By.xpath("//*[@class='ngCellText ng-scope col4 colt4']/span")); 
		List<WebElement> weList2 = driver.findElements(By.cssSelector(".ngSelectionCheckbox")); 
		
		for(int i=0; i<weList.size() ; i++) {
			WebElement webElement = weList.get(i);
			String imeiElement = "";
			imeiElement = webElement.getText();
			if((imeiElement.trim().equals(imei)) || (imeiElement.trim().equals(macAdr))) {
				//the wanted element is founded
				weList2.get(i).click();
				status =  true;
				break;
			}
		}
		
		return status;
	}
	
	public boolean clickOnTheDeviceImei(String imei , String macAdr) {
		boolean status = false;
		List<WebElement> weList = driver.findElements(By.xpath("//*[@class='ngCellText ng-scope col4 colt4']/span")); 
		for(int i=0; i<weList.size() ; i++) {
			WebElement webElement = weList.get(i);
			String imeiElement = "";
			imeiElement = webElement.getText();
			if((imeiElement.trim().equals(imei)) || (imeiElement.trim().equals(macAdr))) {
				//the wanted element is founded
				weList.get(i).click();
				status =  true;
				break;
				
			}
		}
		
		return status;
	}
	
	
	/**
	 * Validate the mdm data comparing to the first insert map
	 * @throws InterruptedException 
	 * */
	public boolean validateDataDeviceDetails(String imei , String macAdr, Map<String,String> compareMap) throws InterruptedException {
		
		clickOnTheDeviceImei(imei, macAdr);
		
		Thread.sleep(5000);
		
		List<WebElement> weListNames = driver.findElements(By.xpath("/html/body/main/ng-include/div/div/div[2]/div[2]/div[1]/div[2]/dl/dt"));
		List<WebElement> weListData = driver.findElements(By.xpath("/html/body/main/ng-include/div/div/div[2]/div[2]/div[1]/div[2]/dl/dd"));
		
		for (int i =0 ; i < weListNames.size() ; i++ ) { 
			String txt = weListNames.get(i).getText();
			if(compareMap.containsKey(txt.trim())) {
				String strFromMap = compareMap.get(txt.trim());
				String strFromData = weListData.get(i+1).getText();
				if(!(strFromMap.contains(strFromData) ||strFromData.contains(strFromMap))) {
					driver.findElement(By.xpath("//div[1]/div[1]/ul/li[5]/a")).click();
					return false;
				}
			}
		}
		
		//ID/IMEI ROM Version
		weListNames = driver.findElements(By.xpath("//*[@class='content']/dl/div/dt"));
		weListData = driver.findElements(By.xpath("//*[@class='content']/dl/div/dd"));
		
		for (int i =0 ; i < weListNames.size() ; i++ ) { 
			String txt = weListNames.get(i).getText();
			if(compareMap.containsKey(txt.trim())) {
				String strFromMap = compareMap.get(txt.trim());
				String strFromData = weListData.get(i).getText();
				if(!(strFromMap.contains(strFromData) ||strFromData.contains(strFromMap))) {
					driver.findElement(By.xpath("//div[1]/div[1]/ul/li[5]/a")).click();
					return false;
				}
			}
		}
		driver.findElement(By.xpath("//div[1]/div[1]/ul/li[5]/a")).click();
		return true;
	}
	
	
	public boolean removeDeviceFromMdm(String imei , String macAdr, String password) throws InterruptedException {
		
		boolean status = false;
		//first to click on the correct checkbox
		
		if(!clickOnTheDeviceCheckBox(imei , macAdr)){
			report.report("Couldn't find the macAdr : " + macAdr +", or imei : "+imei, Reporter.FAIL);
		}
		
		removeBtn.click();
		
		Thread.sleep(3000);
		//to check this is a popup
		List<WebElement> weList = driver.findElements(By.cssSelector("#password"));
		if(weList.size()>0) {
			driver.findElement(By.cssSelector("#password")).sendKeys(password);
		}		
		
		//to check that im click on the remove
		driver.findElements(By.cssSelector(".btn")).get(driver.findElements(By.cssSelector(".btn")).size()-1).click();
		
		Thread.sleep(3000);
		driver.get(siteUrl+ "devices");
		Thread.sleep(3000);
		weList = driver.findElements(By.xpath("//*[@class='ngCellText ng-scope col4 colt4']/span"));
		for(int i=0; i<weList.size() ; i++) {
			WebElement webElement = weList.get(i);
			String imeiElement = "";
			imeiElement = webElement.getText();
			if((imeiElement.trim().equals(imei)) || (imeiElement.trim().equals(macAdr))) {
				//the wanted element is founded
				report.report("The deleteing of the phone didn't worked, the phone is still in." , Reporter.FAIL);
				status = false;
			}
		}
		
		return status;
		
	}
	
	
	public void changePolicy(String imei , String macAdr, String policy) throws InterruptedException {
		
		Thread.sleep(2000);
		clickOnTheDeviceCheckBox(imei, macAdr);
//		clickOnTheDeviceImei(imei, macAdr);
		Thread.sleep(2000);
		driver.findElement(By.xpath("//*[@value='CHANGE POLICY']")).click();
		Thread.sleep(5000);
		
		WebElement select = driver.findElement(By.id("devicePolicies"));
	    List<WebElement> options = select.findElements(By.tagName("option"));
	    for(WebElement option : options){
	        if(option.getText().equals(policy)) {
	            option.click();
	            break;
	        }
	    }
		
	    Thread.sleep(2000);
		driver.findElement(By.xpath("//*[@value='Apply']")).submit();
		validateMessagesChanges(imei, macAdr, policy, 5 * 60 * 1000);
	}
	
	
	public boolean validateMessagesChanges(String imei , String macAdr, String policy, long timeout) throws InterruptedException {
		
		long startTime = System.currentTimeMillis();
		while((System.currentTimeMillis() - startTime) < timeout) {
			
			if(validateMessagesChanges(imei, macAdr,  policy)) {
				report.report("The messages tab is updated in the phone after : "+timeout+" milliseconeds.");
				return true;
			}
			Thread.sleep(1000);
		}
		report.report("The messages tab isn't updated in the phone after : "+timeout+" milliseconeds.", Reporter.FAIL);
		return true;
		
	}
	
	
	public boolean validateMessagesChanges(String imei, String macAdr, String policy) throws InterruptedException {
		
		clickOnTheDeviceImei(imei, macAdr);
		Thread.sleep(3000);
		
		//click on the history
		driver.findElement(By.xpath("//div[1]/div[1]/ul/li[3]/a")).click();
		Thread.sleep(3000);
		
		String name = driver.findElement(By.xpath("//div[2]/ul/li[1]/ul/li[1]/strong")).getText();
		String status =  driver.findElement(By.xpath("//div[1]/div[2]/ul/li[1]/ul/li[2]")).getText();
		
		if(!name.contains("Set Policy "+policy)){
			driver.findElement(By.xpath("//div[1]/div[1]/ul/li[5]/a")).click();
			return false;
		}
		if(!status.contains("Done")){
			driver.findElement(By.xpath("//div[1]/div[1]/ul/li[5]/a")).click();
			return false;
		}
		
		driver.findElement(By.xpath("//div[1]/div[1]/ul/li[5]/a")).click();
		return true;
	}
	
	
	
	
	

}
