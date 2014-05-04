package com.cellrox.tests;

import java.util.List;
import java.util.Map;

import jsystem.framework.TestProperties;
import jsystem.framework.report.Reporter;
import jsystem.framework.report.Summary;

import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.topq.uiautomator.Selector;

import com.cellrox.infra.enums.Persona;
import com.cellrox.infra.pages.CellroxAutomationDevicesAbstractPage;
import com.cellrox.infra.pages.CellroxMdmLoginPage;
import com.cellrox.infra.pages.CellroxOwnerAbstractPage;

public class MDMOperation extends TestCase {
	

	 private String mdmPassword = "Automation10", mdmUser = "or.garfunkel@top-q.co.il";
	 private String ownerName = "automation one";
	 final String siteUrl = "https://mdm-stg.cellrox.com/automation/"; 
	 private String policy = "automation-one-app";




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
	@TestProperties(name = "Unactivate Device With unenroll.zip", paramsInclude = {"currentDevice"})
	  //@TestProperties(name = "Unactivate Device With unenroll.zip", paramsInclude = {"currentDevice,localLocation"})
	public void unactivateDevice() throws Exception {
		//step 1
		  //final String mobileFileLocation = "/sdcard/unenroll.zip";

		//push
		  //devicesMannager.getDevice(currentDevice).pushFileToDevice(localLocation.getAbsolutePath(), mobileFileLocation);
		//the two lines
		  //devicesMannager.getDevice(currentDevice).executeHostShellCommand("echo 'boot-recovery ' > /cache/recovery/command");
		  //devicesMannager.getDevice(currentDevice).executeHostShellCommand("echo '--update_package=" + mobileFileLocation + "'>> /cache/recovery/command");
		//reboot recovery
		  //devicesMannager.getDevice(currentDevice).executeHostShellCommand("reboot recovery");
		
		//Added - unEnroll by commands
		devicesMannager.getDevice(currentDevice).executeHostShellCommand("setprop persist.cellrox.enrolled 0");
		devicesMannager.getDevice(currentDevice).executeHostShellCommand("setprop persist.service.agent.enable 0");
		devicesMannager.getDevice(currentDevice).executeHostShellCommand("pconfig set corp general secondary false");
		devicesMannager.getDevice(currentDevice).executeHostShellCommand("rm /data/agent/agent_cache.conf");
		devicesMannager.getDevice(currentDevice).executeHostShellCommand("rm /data/agent/agent.conf");
		devicesMannager.getDevice(currentDevice).executeHostShellCommand("rm /data/agent/.agent.db");
		devicesMannager.getDevice(currentDevice).executeHostShellCommand("sync");
		devicesMannager.getDevice(currentDevice).executeHostShellCommand("reboot");
		
		//wait for 
		devicesMannager.getDevice(currentDevice).validateDeviceIsOnline(false, false, Persona.PRIV);
		//init the data of the devices
		Thread.sleep(40000);
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
	
	@Test
	@TestProperties(name = "Login To MDM", paramsInclude = {"mdmUser,mdmPassword"})
	public void loginToMDM() throws Exception {
		//step2
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
		
		Thread.sleep(4000);
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
		sleep(500);
		
		devicesMannager.getDevice(currentDevice).getPersona(Persona.PRIV).setText(new Selector().setClassName("android.widget.EditText").setIndex(2)
				, activationCode.substring(4, 8));
		sleep(500);
		
		devicesMannager.getDevice(currentDevice).getPersona(Persona.PRIV).setText(new Selector().setClassName("android.widget.EditText").setIndex(4)
				, activationCode.substring(8, 12));
		sleep(500);
		
		devicesMannager.getDevice(currentDevice).getPersona(Persona.PRIV).setText(new Selector().setClassName("android.widget.EditText").setIndex(3)
				, "p.cellrox.com");
		sleep(500);
		
		devicesMannager.getDevice(currentDevice).getPersona(Persona.PRIV).pressKey("back");
		Thread.sleep(400);
		devicesMannager.getDevice(currentDevice).getPersona(Persona.PRIV).click(new Selector().setClassName("android.widget.RelativeLayout").setIndex(2));
		boolean isReboot= true;
		try {
			isReboot= devicesMannager.getDevice(currentDevice).getPersona(Persona.PRIV).waitForExists(new Selector().setText("Reboot"), 60 * 1000);
		}
		catch(Exception e) {
			report.report("The reboot button isn't exist.",Reporter.FAIL);
			isReboot = false;
		}
		
		if(isReboot){
			report.report("The device activated");
		}
		else {
			report.report(devicesMannager.getDevice(currentDevice).getPersona(Persona.PRIV).getText(new Selector().setTextContains("Error")),Reporter.FAIL);
			report.report("The device fail to activate.", Reporter.FAIL);
		}
		//TODO device encrypted for priv after change
		devicesMannager.getDevice(currentDevice).rebootDevice(true, false, Persona.PRIV , Persona.CORP);

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
		
		//step 4
		//open 
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
		
		//step 4
		//to validate there is two personas inside of the phone and
		//to go to the mdm devices and to click on the device and find the results, max time is 5 min
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
		Thread.sleep(3000);
		devicesPage.changePolicy(imei, macAdr, policy);
		
		//while checking the history there is a need to refresh the page
		//select the wanted device in the device page, click on change policy, choose the wanted policy
		//click apply
		//to click on the device , click on history , check done is in the coosen policy
		//to check that the application installed
		
		//go back to the default policy (without any apps)
		//make sure its done and that there is no application
		
	}


	/**
	 * The function gets the owner and return activation code
	 * @throws InterruptedException 
	 * */
	public String getTheActivationCode(String ownerName) throws ParseException, InterruptedException {

		
		driver.get(siteUrl +"devices");
		Thread.sleep(3000);
		//this list is for all the names
		List<WebElement> weListOwnerNames  = driver.findElements(By.xpath("//div[3]/div/span"));
		List<WebElement> weListIdImeiActivationCode  = driver.findElements(By.xpath("//div[5]/div/span"));
		
		for(int i=0 ; i < weListOwnerNames.size() ; i++) {
			
			if(weListOwnerNames.get(i).getText().trim().equals(ownerName)) {
				//if this is true we find the correct line
				String activationCode =weListIdImeiActivationCode.get(i).getText();
				if(activationCode.contains("Activation token:")) {
					activationCode = activationCode.replace("Activation token:", "").trim();
					report.report("Activation code for : "+ownerName + " found : " + activationCode);
					return activationCode;
				} 
			}
		}
		report.report("Activation code for : "+ownerName + "was not found",Reporter.FAIL);
		return null;

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
