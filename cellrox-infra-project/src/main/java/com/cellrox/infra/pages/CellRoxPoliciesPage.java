package com.cellrox.infra.pages;

import org.apache.tools.ant.taskdefs.Sleep;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.python.modules.re;
import org.python.modules.thread;

import com.cellrox.infra.object.DataSharingProperty;

public class CellRoxPoliciesPage extends CellRoxAbstractPage {

	private String siteUrl;

	@FindBy(css = ".plus")
	private WebElement addPolicyBtn;

	@FindBy(xpath = "//button[@ng-show='!editing']")
	private WebElement editPoliciesBtn;

	public CellRoxPoliciesPage(WebDriver driver, String siteUrl) {
		super(driver);
		this.setSiteUrl(siteUrl);
		driver.get(siteUrl + "device_policies");
		PageFactory.initElements(driver, this);
	}

	public String getSiteUrl() {
		return siteUrl;
	}

	public void setSiteUrl(String siteUrl) {
		this.siteUrl = siteUrl;
	}

	public void addPolicy(String policyName) throws Exception {
//		addPolicyBtn = (new WebDriverWait(driver, 30))
//				  .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".plus")));
		Thread.sleep(5000);
		addPolicyBtn.click();
		// get new policy name text field
		WebElement newPolicynameField = (new WebDriverWait(driver, 30))
				  .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@id='name']")));
		newPolicynameField.sendKeys(policyName);
		// click OK
		WebElement okBtn = driver.findElement(By.cssSelector(".ok"));
		okBtn.click();
		boolean pass = validateSuccessMessage("successfully created device policy \"" + policyName + "\"");
		if (!pass){
			throw new Exception("Error While Creating New Policy " + policyName);
		}
	}

	/**
	 * This function will delete an existing policy
	 * 
	 * @param policyName
	 * @throws Exception
	 */
	public void deletePolicy(String policyName) throws Exception {
		WebElement policy = clickOnPolicy(policyName);
		Thread.sleep(5000);
		WebElement dropDownMeny =policy.findElement(By.xpath(".//i"));
		dropDownMeny.click();
		WebElement deleteBtn = policy.findElement(By.xpath(".//li[3]"));
		deleteBtn.click();
		WebElement areYouSure = (new WebDriverWait(driver, 30))
				  .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@value='Delete']")));
		areYouSure.click();
		boolean pass = validateSuccessMessage("successfully removed device policy \"" + policyName + "\"");
		if (!pass){
			throw new Exception("Error While Deleting New Policy " + policyName);
		}
	}

	public void editDataSharingPolicy(String policyName, boolean businessContacts, boolean businessCalendar, DataSharingProperty... dataSharingProperties)
			throws Exception {
		clickOnPolicy(policyName);
		// click on edit button
		editPoliciesBtn.click();
		// click on DATA SHARING
		driver.findElement(By.linkText("DATA SHARING")).click();
		// click on "Customize - set the degree of data with finer detail"
		Thread.sleep(5000);
		WebElement customizeFields = driver.findElement(By.cssSelector("#contentSyncCustomeEnable"));
		if (!customizeFields.isSelected())
		{
			customizeFields.click();
		}
		Thread.sleep(1000);
		WebElement contactSync = driver.findElement(By.cssSelector("#contactSync"));
		if (businessContacts) {
			// if the user choose to select the button and the button is not
			// selected then click on the button to select it
			if (!contactSync.isSelected()) {
				contactSync.click();
			}
		} else {
			// if the user choose not to select the button and the button is
			// selected then click on the button to diselect it
			if (contactSync.isSelected()) {
				contactSync.click();
			}
		}
		WebElement calendarSync = driver.findElement(By.cssSelector("#calendarSync"));
		if (businessCalendar) {
			// if the user choose to select the button and the button is not
			// selected then click on the button to select it
			if (!calendarSync.isSelected()) {
				calendarSync.click();
			}
		} else {
			// if the user choose not to select the button and the button is
			// selected then click on the button to diselect it
			if (calendarSync.isSelected()) {
				calendarSync.click();
			}
		}
		//for each property
		for (DataSharingProperty property : dataSharingProperties) {
			WebElement currentStatus = driver.findElement(By.xpath(String.format("//*[contains(text(),'%s')]/../..//label[text()='%s']",
					property.getProperty().getProperty(), property.getStatus())));
			
			if (!currentStatus.getAttribute("class").contains("off")){
				currentStatus.click();
			}
		}
		
		//apply changes
		driver.findElement(By.xpath("//button[@type='submit']")).click();
		boolean pass = validateSuccessMessage("successfully saved device policy \""+policyName+"\"");
		if (!pass){
			throw new Exception("Error While Saving New Policy " + policyName);
		}
		
	}

	private boolean validateSuccessMessage(String message) throws Exception {
		// validate added policy
		Thread.sleep(1000);
		WebElement sucessfulBar = (new WebDriverWait(driver, 30))
				  .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".ng-scope.ng-binding")));
		String sucessBarText = sucessfulBar.getText().trim();
		report.report(sucessBarText.trim()+" == "+message.trim());
		if (!sucessBarText.trim().equals(message.trim())) {
			return false;
		}else{
			return true;
		}
	}

	private WebElement clickOnPolicy(String policyName) throws Exception {
		try {
			Thread.sleep(5000);
			WebElement policy = driver.findElement(By.xpath("//a[contains(@title,'" + policyName + "')]"));
			policy.click();
			return policy;
		} catch (NoSuchElementException e) {
			throw new Exception("Could not Find Policy \"" + policyName + "\"");
		}
	}

}
