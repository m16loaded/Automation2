package com.cellrox.tests;

import java.util.Map;

import jsystem.framework.TestProperties;
import jsystem.framework.report.Reporter;
import jsystem.framework.report.Summary;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.topq.uiautomator.Selector;

import com.cellrox.infra.TestCase;
import com.cellrox.infra.enums.Persona;
import com.cellrox.infra.pages.CellroxAutomationDevicesAbstractPage;
import com.cellrox.infra.pages.CellroxMdmLoginPage;
import com.cellrox.infra.pages.CellroxOwnerAbstractPage;

public class MDMOperation extends TestCase {
	

	 private String mdmPassword = "Automation10", mdmUser = "or.garfunkel@top-q.co.il";
	 private String ownerName = "automation one";
	 final String siteUrl = "https://mdm-stg.cellrox.com/automation/"; 
	 private String policy = "automation-one-app";



//	/**
//	  * This test is needed for the first run of the webDriver
//	  * */
//	 @Test
//	 @TestProperties(name = "Init Web Driver", paramsInclude = {""})
//	 public void initWebDriver() throws Exception {
////		System.setProperty("webdriver.chrome.driver","/home/topq/dev/chromedriver");
////	    driver = new ChromeDriver();
////		 initTheWebDriver();
//		 
//		 System.out.println("dfgdfgdf");
//	 }
	 


	/**
	 * This is step 1 in the activation.
	 * The function :
	 * 	1.	Push the file of unenroll.zip to the /sdcard
	 * 	2.	Execute : echo 'boot-recovery ' > /cache/recovery/command.
	 * 	3.	Execute : echo '--update_package=" + mobileFileLocation + "'>> /cache/recovery/command.
	 * 	4.	Reboot recovery.
	 * 	5.	Wait for the device to return up.
	 * 	6.	Configure device with priv
	 * 	7.	Connect to servers
	 * 	8.	Click on corp persona and check that it isn't activated. 
	 * 
	 * */
	@Test
	@TestProperties(name = "Unactivate Device With unenroll.zip", paramsInclude = {"currentDevice,localLocation"})
	public void unactivateDevice() throws Exception {
		//step 1
//		localLocation = new File("/home/topq/Desktop/unenroll.zip");
		final String mobileFileLocation = "/sdcard/unenroll.zip";
		
//		devicesMannager.getDevice(currentDevice).configureDeviceForAutomation(true);
		/*devicesMannager.getDevice(currentDevice).connectToServers();*/

		//push
		devicesMannager.getDevice(currentDevice).pushFileToDevice(localLocation.getAbsolutePath(), mobileFileLocation);
		//the two lines
		devicesMannager.getDevice(currentDevice).executeHostShellCommand("echo 'boot-recovery ' > /cache/recovery/command");
		devicesMannager.getDevice(currentDevice).executeHostShellCommand("echo '--update_package=" + mobileFileLocation + "'>> /cache/recovery/command");
		//reboot recovery
		devicesMannager.getDevice(currentDevice).executeHostShellCommand("reboot recovery");
		//wait for 
		devicesMannager.getDevice(currentDevice).validateDeviceIsOnline(false, Persona.PRIV);
		//init the data of the devices
		devicesMannager.getDevice(currentDevice).setUpTime(devicesMannager.getDevice(currentDevice).getCurrentUpTime());
		devicesMannager.getDevice(currentDevice).initProcessesForCheck();
		devicesMannager.getDevice(currentDevice).setPsString(devicesMannager.getDevice(currentDevice).getPs());
		//return the server connectivity in Priv
		Thread.sleep(20000);
		devicesMannager.getDevice(currentDevice).configureDeviceForPriv(true);
		devicesMannager.getDevice(currentDevice).connectToServerPriv();
		//to try to switch persona
		devicesMannager.getDevice(currentDevice).getPersona(Persona.PRIV).wakeUp();
		devicesMannager.getDevice(currentDevice).unlockBySwipe(Persona.PRIV);
		devicesMannager.getDevice(currentDevice).getPersona(Persona.PRIV).click(5,5);
		//validate the activation screen is in
		if(!devicesMannager.getDevice(currentDevice).getPersona(Persona.PRIV).waitForExists(new Selector().setText("PERSONA ACTIVATION"), 7*1000)) {
			report.report("The Persona activation screen wan't found." , Reporter.FAIL);
		}
		else {
			report.report("The Persona activation screen wan found.");	
		}
			
		// apply undate / unenroll.zip , adb root
		// to make sure that i dont have a corp persona 
		//Step 1
	}
	
	/**
	 * Login to the mdm
	 * */
	@Test
	@TestProperties(name = "Login To MDM", paramsInclude = {"mdmUser,mdmPassword"})
	public void loginToMDM() throws Exception {
		//step2
//		devicesMannager.getDevice(currentDevice).connectToServers();
		CellroxMdmLoginPage loginPage = new CellroxMdmLoginPage(driver, siteUrl);
		loginPage.login(mdmUser.trim(), mdmPassword.trim());
	}
	
	
	/**
	 * This is step 2 in the activation.
	 * The function will remove the primary device from the MDM and verify it
	 * */
	@Test
	@TestProperties(name = "Remove device from MDM device list", paramsInclude = {"mdmPassword"})
	public void removeDevicefromMdmDeviceList() throws Exception {
		//step2
		
		CellroxAutomationDevicesAbstractPage automationDevicesPage = new CellroxAutomationDevicesAbstractPage(driver, siteUrl);
		
		report.report("getting imei");
		String imei = (String) Summary.getInstance().getProperty("IMEI");
		String macAdr = (String) Summary.getInstance().getProperty("Mac Adress");
		
		
		automationDevicesPage.removeDeviceFromMdm(imei, macAdr, mdmPassword);

		// to check the imei of the device and find the correct device, to click the check box
		// to click on remove, to enter the password , click on remove
		//to check that the phone isn't in the list anymore
	
	}
	
	/**
	 * This is step 3 in the activation.
	 * The function : 
	 * 	1.	Click on the owner and enroll the device
	 * 	2.	Get the activation code from the MDM
	 * 	3.	Activate the phone.
	 * 	4.	Restart the phone.
	 * 	5.	Configure the device for uiautomator.
	 * 	6.	Connect to uiautomator servers.
	 * */
	@Test
	@TestProperties(name = "Enroll Owner", paramsInclude = {"ownerName,currentDevice"})
	public void enrollOwner() throws Exception {
		
		CellroxOwnerAbstractPage ownerPage = new CellroxOwnerAbstractPage(driver, siteUrl);
		
		//click on the owers tab and then click on the correct owner checkbox
		//click on enroll tab and than on enroll
		ownerPage.selectTheCorrectOwnerAndEnroll(ownerName);
		String activationCode = getTheActivationCode(ownerName).replace("-", "");
		
		devicesMannager.getDevice(currentDevice).configureDeviceForPriv(true);
		
		devicesMannager.getDevice(currentDevice).connectToServerPriv();
		
		devicesMannager.getDevice(currentDevice).getPersona(Persona.PRIV).click(new Selector().setText("Next"));
		
		devicesMannager.getDevice(currentDevice).getPersona(Persona.PRIV).setText(new Selector().setClassName("android.widget.EditText").setIndex(0)
				, activationCode.substring(0, 4));
		
		devicesMannager.getDevice(currentDevice).getPersona(Persona.PRIV).setText(new Selector().setClassName("android.widget.EditText").setIndex(2)
				, activationCode.substring(4, 8));
		
		devicesMannager.getDevice(currentDevice).getPersona(Persona.PRIV).setText(new Selector().setClassName("android.widget.EditText").setIndex(4)
				, activationCode.substring(8, 12));
		
		devicesMannager.getDevice(currentDevice).getPersona(Persona.PRIV).setText(new Selector().setClassName("android.widget.EditText").setIndex(3)
				, "p.cellrox.com");
		
		devicesMannager.getDevice(currentDevice).getPersona(Persona.PRIV).pressKey("back");
		Thread.sleep(400);
		devicesMannager.getDevice(currentDevice).getPersona(Persona.PRIV).click(new Selector().setClassName("android.widget.RelativeLayout").setIndex(2));
		boolean isReboot= true;
		isReboot= devicesMannager.getDevice(currentDevice).getPersona(Persona.PRIV).waitForExists(new Selector().setText("Reboot"), 30 * 1000);
		report.report("isReboot = " + isReboot);
		if(!isReboot){
				//try to do this for the seconed time
				report.report("The reboot button isn't exist for the first time.", Reporter.WARNING);
				report.report(devicesMannager.getDevice(currentDevice).getPersona(Persona.PRIV).getText(new Selector().setTextContains("(E2")), Reporter.WARNING);
				devicesMannager.getDevice(currentDevice).getPersona(Persona.PRIV).pressKey("back");
				devicesMannager.getDevice(currentDevice).getPersona(Persona.PRIV).pressKey("back");
				devicesMannager.getDevice(currentDevice).getPersona(Persona.PRIV).click(new Selector().setText("Next"));
				
				devicesMannager.getDevice(currentDevice).getPersona(Persona.PRIV).setText(new Selector().setClassName("android.widget.EditText").setIndex(0)
						, activationCode.substring(0, 4));
				
				devicesMannager.getDevice(currentDevice).getPersona(Persona.PRIV).setText(new Selector().setClassName("android.widget.EditText").setIndex(2)
						, activationCode.substring(4, 8));
				
				devicesMannager.getDevice(currentDevice).getPersona(Persona.PRIV).setText(new Selector().setClassName("android.widget.EditText").setIndex(4)
						, activationCode.substring(8, 12));
				
				devicesMannager.getDevice(currentDevice).getPersona(Persona.PRIV).setText(new Selector().setClassName("android.widget.EditText").setIndex(3)
						, "p.cellrox.com");
				
				devicesMannager.getDevice(currentDevice).getPersona(Persona.PRIV).pressKey("back");
				Thread.sleep(400);
				devicesMannager.getDevice(currentDevice).getPersona(Persona.PRIV).click(new Selector().setClassName("android.widget.RelativeLayout").setIndex(2));
				isReboot= devicesMannager.getDevice(currentDevice).getPersona(Persona.PRIV).waitForExists(new Selector().setText("Reboot"), 30 * 1000);

		}
	
		
		if(isReboot){
			report.report("The device activated");
		}
		else {
			report.report(devicesMannager.getDevice(currentDevice).getPersona(Persona.PRIV).getText(new Selector().setTextContains("(E2")),Reporter.FAIL);
			report.report("The device fail to activate.", Reporter.FAIL);
		}
		
		devicesMannager.getDevice(currentDevice).rebootDevice(true, Persona.PRIV , Persona.CORP);

		devicesMannager.getDevice(currentDevice).configureDeviceForAutomation(true);
		devicesMannager.getDevice(currentDevice).connectToServers();
		
		//to get the https://mdm-qa.cellrox.com/automation/devices.json and to get the activation_code
		//to click add information , to enter to the device, click on the next and than on reboot
		
	}
	
	/**
	 * This is step 4 in the activation.
	 * This function : 
	 * 	1.	Click password in the corp and verify corp is uploaded.
	 * 	2.	Compare the data from the device Cli and the MDM devices
	 * */
	@Test
	@TestProperties(name = "Validate Activated Persona", paramsInclude = {"ownerName,currentDevice"})
	public void validateActivatedPersona() throws Exception {
		
		devicesMannager.getDevice(currentDevice).getPersona(Persona.CORP).wakeUp();
		devicesMannager.getDevice(currentDevice).switchPersona(Persona.CORP);
		devicesMannager.getDevice(currentDevice).getPersona(Persona.CORP).click(new Selector().setText("1"));
		devicesMannager.getDevice(currentDevice).getPersona(Persona.CORP).click(new Selector().setText("1"));
		devicesMannager.getDevice(currentDevice).getPersona(Persona.CORP).click(new Selector().setText("1"));
		devicesMannager.getDevice(currentDevice).getPersona(Persona.CORP).click(new Selector().setText("1"));
		devicesMannager.getDevice(currentDevice).getPersona(Persona.CORP).click(new Selector().setDescription("Enter"));
		devicesMannager.getDevice(currentDevice).getPersona(Persona.CORP).pressKey("home");
		devicesMannager.getDevice(currentDevice).getPersona(Persona.CORP).pressKey("back");
		if(!devicesMannager.getDevice(currentDevice).getPersona(Persona.CORP).exist(new Selector().setDescription("Apps"))) {
			report.report("The screen is off or .",Reporter.FAIL);
		}
		
		devicesMannager.getDevice(currentDevice).switchPersona(Persona.PRIV);
		
		CellroxAutomationDevicesAbstractPage devicesPage = new CellroxAutomationDevicesAbstractPage(driver, siteUrl);
		
		String imei = (String) Summary.getInstance().getProperty("IMEI");
		String macAdr = (String) Summary.getInstance().getProperty("Mac Adress");
		
		//fill the wanted data in the map
		Map<String,String> compareMap  = devicesMannager.getDevice(currentDevice).getMapOfData();
		
		devicesPage.validateDataDeviceDetails(imei, macAdr, compareMap, 10* 60 *1000);
		
	}
	

	
	/**
	 * This is step 5 in the activation.
	 * This function : 
	 * 	1.	change the policy to the wanted policy
	 * 	2.	validate from the MDM that the policy changed
	 * */
	@Test
	@TestProperties(name = "Push the policy : ${policy} and validate it" , paramsInclude = {"policy"})
	public void pushPolicy() throws Exception {
		// step 5
		CellroxAutomationDevicesAbstractPage devicesPage = new CellroxAutomationDevicesAbstractPage(driver, siteUrl);
		
		String imei = (String) Summary.getInstance().getProperty("IMEI");
		String macAdr = (String) Summary.getInstance().getProperty("Mac Adress");
		
		devicesPage.changePolicy(imei, macAdr, policy);
		
	}


	/**
	 * The function gets the owner and return activation code
	 * */
	public String getTheActivationCode(String ownerName) throws ParseException {

		String activationCode = null;
		driver.get(siteUrl + "devices.json");
		WebElement we = driver.findElement(By.cssSelector("html>body>pre"));
		String json = we.getText();

		JSONParser parser = new JSONParser();
		Object obj = parser.parse(json);
		JSONArray jsonArr = (JSONArray) obj;

		for (int i = 0; i < jsonArr.size(); i++) {

			JSONObject jsonObject = (JSONObject) jsonArr.get(i);

			Boolean active =  (Boolean) jsonObject.get("active");
			if (active) {
				continue;
			}

			activationCode = (String) jsonObject.get("activation_code");
			report.report("The activation code : " + activationCode);
			break;
		}

		return activationCode;
	}
	
	public String getMdmUser() {
		return mdmUser;
	}

	public void setMdmUser(String mdmUser) {
		this.mdmUser = mdmUser;
	}

	public String getMdmPassword() {
		return mdmPassword;
	}

	public void setMdmPassword(String mdmPassword) {
		this.mdmPassword = mdmPassword;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	
	public String getPolicy() {
		return policy;
	}

	public void setPolicy(String policy) {
		this.policy = policy;
	}


	
	
	

}
