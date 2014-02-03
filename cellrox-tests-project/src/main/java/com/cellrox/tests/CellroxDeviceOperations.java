package com.cellrox.tests;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsystem.extensions.analyzers.compare.CompareValues;
import jsystem.framework.TestProperties;
import jsystem.framework.analyzer.AnalyzerException;
import jsystem.framework.report.Reporter;
import jsystem.framework.scenario.UseProvider;
import junit.framework.SystemTestCase4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.topq.uiautomator.ObjInfo;
import org.topq.uiautomator.Selector;

import com.android.uiautomator.core.UiObjectNotFoundException;
import com.cellrox.infra.CellRoxDevice;
import com.cellrox.infra.CellRoxDeviceManager;
import com.cellrox.infra.enums.DeviceNumber;
import com.cellrox.infra.enums.Direction;
import com.cellrox.infra.enums.LogcatHandler;
import com.cellrox.infra.enums.Persona;
import com.cellrox.infra.enums.Size;
import com.cellrox.infra.enums.State;
import com.cellrox.infra.log.LogParser;
import com.cellrox.infra.object.LogParserExpression;



public class CellroxDeviceOperations extends SystemTestCase4 {

	CellRoxDeviceManager devicesMannager;
	private File localLocation;
	private String remotefileLocation;
	Persona persona;
	Selector selector;
	private String button;
	private String text;
	private String value;
	private LogParserExpression[] expression;
	private boolean waitForNewWindow = false;
	private boolean updateVersion = false;
	private String serverHost;
	private String version;
	private String adminToken;
	private File imgFile;
	private String serverUrl;
	private String deviceId;
	private int index;
	private String expectedLine, expectedNumber;
	private String appName;
	private long interval;
	private String timeout = "10000";
	private boolean isExists;
//	private String sqlQuery;
	private Direction dir;
	private String childClassName;
	int instance = 0;
	private boolean exceptionThrower = true;
	// all the following string are for general function that use father,son
	private String fatherClass, fatherDescription, fatherText, fatherIndex, childClass, childDescription, childText, childIndex;
	private String cliCommand;
	private boolean regularExpression = false;
	private String appFullPath;
	private State onOff;
	private String wifiNetwork, wifiPassword;
	private String propertyName;
	private String expectedValue;
	private String x, y;
	private String packageName;
	private boolean deviceEncrypted = true;
	private String applyUpdateLocation;
	private String phoneNumber; 
	private String messageContent = "Hello from automation.";
	private DeviceNumber currentDevice = DeviceNumber.PRIMARY;
	private Size size = Size.Smaller; 
	private String logsLocation = System.getProperty("user.home")+"/LOGS_FROM_ADB";
	private LogcatHandler logType = LogcatHandler.PRIV;
	
	
	@Before
	public void init() throws Exception {
		try {
			report.startLevel("Before");
			devicesMannager = (CellRoxDeviceManager) system.getSystemObject("devicesMannager");
			for (CellRoxDevice device : devicesMannager.getCellroxDevicesList()) {
				long uptime = device.getCurrentUpTime();
				if(device.getUpTime() > uptime) {
					report.report("The uptime is smaller from what it should be.", Reporter.FAIL);
					throw new Exception("Unwanted reboot happened to device : " + device.getDeviceSerial());
				}
				device.setUpTime(uptime);
			}
			report.report("Finish the initing of the test.");
		}
		finally {
			report.stopLevel();
		}
		// devicesMannager.getDevice(currentDevice).configureDeviceForAutomation(true);
		// devicesMannager.getDevice(currentDevice).connectToServers();
	}
	

//	@Test
//	public void pass() {
//		report.report("pass");
//	}
//	@Test
//	public void warnning() {
//		report.report("warnning",Reporter.WARNING);
//	}
//	@Test
//	public void fail() {
//		report.report("fail",Reporter.FAIL);
//	}
	
	
	
	@Test
	public void orConnectivityTest() throws Exception {
		
//		devicesMannager.getDevice(DeviceNumber.SECONDARY).configureDeviceForAutomation(true);
		System.out.println(devicesMannager.getDevice(currentDevice).getDeviceSerial());
//		devicesMannager.getDevice(DeviceNumber.SECONDARY).connectToServers();
		devicesMannager.getDevice(currentDevice).configureDeviceForAutomation(true);
		devicesMannager.getDevice(currentDevice).connectToServers();
		
		report.report("##########################");
		
		devicesMannager.getDevice(DeviceNumber.PRIMARY).getPersona(Persona.PRIV).pressKey("home");
		Thread.sleep(10000);
		devicesMannager.getDevice(DeviceNumber.PRIMARY).getPersona(Persona.PRIV).pressKey("home");
		devicesMannager.getDevice(DeviceNumber.PRIMARY).getPersona(Persona.PRIV).pressKey("home");
		
		
//		devicesMannager.getDevice(DeviceNumber.PRIMARY).getPersona(Persona.PRIV).click(new Selector().setText("Settings"));
//		devicesMannager.getDevice(DeviceNumber.SECONDARY).getPersona(Persona.PRIV).pressKey("home");
//		devicesMannager.getDevice(DeviceNumber.SECONDARY).getPersona(Persona.PRIV).click(new Selector().setText("Settings"));
		

	}

	/**
	 * The function do the same action as the script get_logs_adb
	 * return .zip file of the logs 
	 * */
	@Test
	@TestProperties(name = "Get the logs" , paramsInclude = {"logsLocation,logType,currentDevice"})
	public void getTheLogs() throws Exception {
		devicesMannager.getDevice(currentDevice).getTheLogs(logType, logsLocation);
	}
	
	@Test
	@TestProperties(name = "Kill All Automation Processes on ${currentDevice}", paramsInclude = { "currentDevice" })
	public void killAllAutomationProcesses() throws Exception {
		devicesMannager.getDevice(currentDevice).killAllAutomaionProcesses();
	}

	
	@Test
	@TestProperties(name = "Is The Device Connected on ${currentDevice}", paramsInclude = { "currentDevice" })
	public void isTheDeviceConnected() throws Exception {
		devicesMannager.getDevice(currentDevice).isDeviceConnected();
	}

	/**
	 * This is a general function for finding the father and than find the child
	 * and click on it. If you are not need some of the data, than do not enter
	 * the data
	 * 
	 * @throws UiObjectNotFoundException
	 * */
	@Test
	@TestProperties(name = "click on the child from father ${persona} . father : \"${fatherClass}\",\"${fatherDescription}\",\"${fatherText}\",\"${fatherIndex}\" "
			+ " , child : \"${childClass}\",\"${childDescription}\",\"${childText}\" ,\"${chilsIndex}\".", paramsInclude = { "currentDevice,persona,fatherClass,fatherDescription,fatherText,fatherIndex,childClass,childDescription,childText,childIndex,waitForNewWindow" })
	public void clickChildFromFather() throws UiObjectNotFoundException {

		Selector father = new Selector();
		Selector child = new Selector();

		// father
		if (fatherClass != null) {
			father.setClassName(fatherClass);
		}
		if (fatherDescription != null) {
			father.setDescription(fatherDescription);
		}
		if (fatherText != null) {
			father.setText(fatherText);
		}
		if (fatherIndex != null) {
			father.setIndex(Integer.valueOf(fatherIndex));
		}

		// child
		if (childClass != null) {
			child.setClassName(childClassName);
		}
		if (childDescription != null) {
			child.setDescription(childDescription);
		}
		if (childText != null) {
			child.setText(childText);
		}
		if (childIndex != null) {
			child.setIndex(Integer.valueOf(childIndex));
		}

		String fatherInstance = devicesMannager.getDevice(currentDevice).getPersona(persona).getUiObject(father);
		report.report("The father id : " + fatherInstance);
		String objectId = devicesMannager.getDevice(currentDevice).getPersona(persona).getChild(fatherInstance, child);
		report.report("The child id : " + objectId);

		if (waitForNewWindow) {
			report.report("im clicking and waiting");
			devicesMannager.getDevice(currentDevice).getPersona(persona).clickAndWaitForNewWindow(objectId, 10000);
		} else {
			report.report("im clicking");
			System.out.println(objectId);
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(objectId);
		}

	}

	@Test
	@TestProperties(name = "open application by Text \"${text}\" on ${persona}", paramsInclude = { "currentDevice,text,persona" })
	public void openApp() throws UiObjectNotFoundException {
		devicesMannager.getDevice(currentDevice).getPersona(persona).openApp(text);
	}

	@Test
	@TestProperties(name = "push ${localLocation} to ${remotefileLocation}", paramsInclude = { "currentDevice,localLocation,remotefileLocation" })
	public void pushToDevice() throws Exception {
		
//		currentDevice = DeviceNumber.PRIMARY;
//		localLocation = new File("/home/topq/git/automation/uiautomatorServer-19/bin/bundle.jar");
//		remotefileLocation = "/data/containers/corp/data/local/tmp/bundle.jar";
		
		report.report("About to push file to device");
		report.report(localLocation.getAbsolutePath());
		report.report(remotefileLocation);
		devicesMannager.getDevice(currentDevice).pushFileToDevice(localLocation.getAbsolutePath(), remotefileLocation);
		report.report("Finish to push file to device");
		runProperties.setRunProperty("adb.push.file.location", remotefileLocation);
	}
	
	@Test
	@TestProperties(name = "push ${localLocation} to PRIV", paramsInclude = { "currentDevice,localLocation" })
	public void pushApplicationToPriv() throws Exception {
		devicesMannager.getDevice(currentDevice).pushApplicationToPriv(localLocation.getAbsolutePath());
	}
	
	@Test
	@TestProperties(name = "push ${localLocation} to CORP", paramsInclude = { "currentDevice,localLocation" })
	public void pushApplicationToCorp() throws Exception {
		devicesMannager.getDevice(currentDevice).pushApplicationToCorp(localLocation.getAbsolutePath());
	}

	@Test
	@TestProperties(name = "Configure Device", paramsInclude = {"currentDevice"})
	public void configureDevice() throws Exception {
		devicesMannager.getDevice(currentDevice).configureDeviceForAutomation(true);
	}

	@Test
	@TestProperties(name = "Wait Until Device is Online", paramsInclude = {"currentDevice"})
	public void validateDeviceIsOnline() throws Exception {
		devicesMannager.getDevice(currentDevice).validateDeviceIsOnline(false, Persona.PRIV, Persona.CORP);
	}

	@Test
	@TestProperties(name = "Connect to Servers", paramsInclude = {"currentDevice"})
	public void connectToServers() throws Exception {
		devicesMannager.getDevice(currentDevice).connectToServers();
	}
	
	@Test
	@TestProperties(name = "Click on x,y on ${persona}", paramsInclude = { "currentDevice,x,y,persona" })
	public void clickByCordinate() {
		devicesMannager.getDevice(currentDevice).getPersona(persona).click(Integer.valueOf(x), Integer.valueOf(y));
	}
	
	@Test
	@TestProperties(name = "Click on UiObject by Text \"${text}\" on ${persona}", paramsInclude = { "currentDevice,text,selector,persona,waitForNewWindow,exceptionThrower" })
	public void clickByText() throws Exception {
		Selector s = new Selector();
		s.setText(text);
		try {
			if (waitForNewWindow) {
				devicesMannager.getDevice(currentDevice).getPersona(persona).clickAndWaitForNewWindow(s, 10000);
			} else {
				devicesMannager.getDevice(currentDevice).getPersona(persona).click(s);
			}
		} catch (Exception e) {
			if (exceptionThrower) {
				report.report("Could not find UiObject " + e.getMessage(), Reporter.FAIL);
			} else {
				report.report("Could not find UiObject " + e.getMessage());
			}
		}
	}

	@Test
	@TestProperties(name = "Click on UiObject by Text Contains \"${text}\" on ${persona}", paramsInclude = { "currentDevice,text,selector,persona,waitForNewWindow" })
	public void clickByTextContains() throws Exception {
		Selector s = new Selector();
		s.setTextContains(text);
		try {
			if (waitForNewWindow) {
				devicesMannager.getDevice(currentDevice).getPersona(persona).clickAndWaitForNewWindow(s, 10000);
			} else {
				devicesMannager.getDevice(currentDevice).getPersona(persona).click(s);
			}
		} catch (Exception e) {
			report.report("Could not find UiObject " + e.getMessage(), Reporter.FAIL);
		}
	}

	@Test
	@TestProperties(name = "Scroll and Click on UiObject by Text \"${text}\" on ${persona}", paramsInclude = { "currentDevice,text,persona" })
	public void scrollAndClick() throws Exception {
		Selector s = new Selector();
		s.setText(text);
		try {
			String objectId = devicesMannager.getDevice(currentDevice).getPersona(persona).childByText(new Selector().setScrollable(true),
					new Selector().setText(text), text, true);
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(objectId);
		} catch (Exception e) {
			report.report("Could not find UiObject " + e.getMessage(), Reporter.FAIL);
		}
	}

	@Test
	@TestProperties(name = "Click on UiObject by Description \"${text}\" on ${persona}", paramsInclude = { "currentDevice,text,persona,waitForNewWindow,exceptionThrower" })
	public void clickByDesc() throws Exception {
		Selector s = new Selector();
		s.setDescription(text);
		try {
			if (waitForNewWindow) {
				devicesMannager.getDevice(currentDevice).getPersona(persona).clickAndWaitForNewWindow(s, 10000);
			} else {
				devicesMannager.getDevice(currentDevice).getPersona(persona).click(s);
			}
		} catch (Exception e) {
			if (exceptionThrower) {
				report.report("Could not find UiObject " + e.getMessage(), Reporter.FAIL);
			} else {
				report.report("Could not find UiObject " + e.getMessage());
			}
		}
	}

	@Test
	@TestProperties(name = "Wait for UiObject by Description \"${text}\" on ${persona}", paramsInclude = { "currentDevice,text,persona" })
	public void waitByDesc() throws Exception {
		Selector s = new Selector();
		s.setDescription(text);
		try {
			devicesMannager.getDevice(currentDevice).getPersona(persona).waitForExists(new Selector().setDescription(text), 10000);
		} catch (Exception e) {
			report.report("Could not find UiObject " + e.getMessage(), Reporter.FAIL);
		}
	}

	@Test
	@TestProperties(name = "Get Full Text of Text Contains \"${text}\" on ${persona}", paramsInclude = { "currentDevice,text,persona" })
	public void getTextByTextContains() throws Exception {
		String msg = devicesMannager.getDevice(currentDevice).getPersona(persona).getText(new Selector().setTextContains(text));
		report.report(msg);
	}

	@Test
	@TestProperties(name = "Get Full Text of UiObject \"${text}/${childClassName}\" index ${instance}", paramsInclude = { "currentDevice,text,persona,childClassName,instance,index" })
	public void getTextTextViewFather() throws Exception {

		String object = devicesMannager.getDevice(currentDevice).getPersona(persona).childByInstance(new Selector().setClassName(text).setIndex(index),
				new Selector().setClassName(childClassName), instance);
		String msg = devicesMannager.getDevice(currentDevice).getPersona(persona).getText(object);
		report.report(msg);
	}

	@Test
	@TestProperties(name = "Wait for UiObject by Text \"${text}\" on ${persona}", paramsInclude = { "currentDevice,text,persona,timeout,interval" })
	public void waitByTextContains() throws Exception {
		final long start = System.currentTimeMillis();
		while (!devicesMannager.getDevice(currentDevice).getPersona(persona).exist(new Selector().setTextContains(text))) {
			if (System.currentTimeMillis() - start > Integer.valueOf(timeout)) {
				report.report("Could not find UiObject with text " + text + " after " + Integer.valueOf(timeout) / 1000+ " sec.", Reporter.FAIL);
				break;
			}
			Thread.sleep(interval);
		}
	}

	@Test
	@TestProperties(name = "Wait for UiObject by Text \"${text}\" on ${persona}", paramsInclude = { "currentDevice,text,persona,timeout,interval" })
	public void waitByText() throws Exception {
		final long start = System.currentTimeMillis();
		while (!devicesMannager.getDevice(currentDevice).getPersona(persona).exist(new Selector().setText(text))) {
			if (System.currentTimeMillis() - start > Integer.valueOf(timeout)) {
				report.report("Could not find UiObject with text " + text + " after " + Integer.valueOf(timeout) / 1000+ " sec.", Reporter.FAIL);
				break;
			}
			Thread.sleep(interval);
		}
	}
	
	
	@Test
	@TestProperties(name = "Wait for UiObject by Class \"${text}\" on ${persona}", paramsInclude = { "currentDevice,text,persona,timeout,interval,index,packageName" })
	public void waitByClass() throws Exception {
		final long start = System.currentTimeMillis();
		while (!devicesMannager.getDevice(currentDevice).getPersona(persona).exist(new Selector().setClassName(text).setIndex(index).setPackageName(packageName))) {
			if (System.currentTimeMillis() - start > Integer.valueOf(timeout)) {
				report.report("Could not find UiObject with text " + text + " after " + Integer.valueOf(timeout) / 1000
						+ " sec.", Reporter.FAIL);
				break;
			}
			Thread.sleep(interval);
		}
	}

	@Test
	@TestProperties(name = "Validate Property \"${propertyName}\" equels \"${expectedValue}\"", paramsInclude = { "currentDevice,persona,propertyName,expectedValue" })
	public void validateProperty() throws Exception {

		String actualProperty = devicesMannager.getDevice(currentDevice).getPersona(persona).getProp(propertyName);
		CompareValues compareValues = new CompareValues(expectedValue);
		analyzer.setTestAgainstObject(actualProperty);
		analyzer.analyze(compareValues);
	}

	/**
	 * Unlock device by swipe.<br>
	 * Will press the home key.
	 * 
	 * @throws Exception
	 */
	@Test
	@TestProperties(name = "Unlock Device by Swipe on ${persona}", paramsInclude = { "currentDevice,persona" })
	public void unlockBySwipe() throws Exception {
		devicesMannager.getDevice(currentDevice).unlockBySwipe(persona);
	}


	
	@Test
	@TestProperties(name = " Swipe by Class Name on ${persona}", paramsInclude = { "currentDevice,persona,dir,text" })
	public void swipeByClassName() throws Exception {
		devicesMannager.getDevice(currentDevice).getPersona(persona).swipe(new Selector().setClassName(text), dir.getDir(), 20);
	}

	@Test
	@TestProperties(name = "Click on UiObject by ClassName \"${text}\" on ${persona}", paramsInclude = { "currentDevice,text,selector,persona,waitForNewWindow,index" })
	public void clickByClassName() throws Exception {
		Selector s = new Selector();
		s.setClassName(text);
		s.setIndex(index);
		s.setClickable(true);
		if (waitForNewWindow) {
			isPass = devicesMannager.getDevice(currentDevice).getPersona(persona).clickAndWaitForNewWindow(s, 10000);
		} else {
			try {
				isPass = devicesMannager.getDevice(currentDevice).getPersona(persona).click(s);
			} catch (Exception e) {
				report.report("baaa");
			}
		}
	}

	
	@Test
	@TestProperties(name = "Click on UiObject by Text \"${text}\"and class \"${childClass}\" on ${persona}", paramsInclude = { "currentDevice,childClass,text,selector,persona,waitForNewWindow,index" })
	public void clickByClassNameAndText() throws Exception {
		Selector s = new Selector();
		s.setText(text);
		s.setClassName(childClass);
		s.setIndex(index);
		s.setClickable(true);
		if (waitForNewWindow) {
			isPass = devicesMannager.getDevice(currentDevice).getPersona(persona).clickAndWaitForNewWindow(s, 10000);
		} else {
			try {
				isPass = devicesMannager.getDevice(currentDevice).getPersona(persona).click(s);
			} catch (Exception e) {
				report.report("baaa");
			}
		}
	}
	

	@Test
	@TestProperties(name = "Enter Text \"${value}\" on UiObject by Class Name \"${text}\" on ${persona}", paramsInclude = { "currentDevice,text,value,persona,index" })
	public void enterTextByClassName() throws Exception {
		Selector s = new Selector();
		s.setClassName(text);
		s.setIndex(index);
		isPass = devicesMannager.getDevice(currentDevice).getPersona(persona).setText(s, value);
	}

	
	@Test
	@TestProperties(name = "Wake Up", paramsInclude = {"currentDevice"})
	public void wakeUp() throws Exception {
		try {
			devicesMannager.getDevice(currentDevice).getPersona(devicesMannager.getDevice(currentDevice).getForegroundPersona()).wakeUp();
		}
		catch(Exception e) {
			report.report("Error in the waking up.");
		}
	}
	
	/**
	 * This test is installing new version with ApplyUpdate.sh
	 * 
	 * */
	@Test
	@TestProperties(name = "Install New Version On The Device on ${currentDevice}", paramsInclude = {"deviceEncrypted,applyUpdateLocation,currentDevice"} )
	public void installNewVersion() throws Exception {
//		String applyUpdateLocation = "/home/topq/dev/ota/ApplyUpdate.sh";
//		String otaFileLocation = "/home/topq/dev/ota/kitkat-mako-20140122.121349-ota.zip";
		report.report("Installing new version on the device : " + devicesMannager.getDevice(currentDevice).getDeviceSerial());
		devicesMannager.getDevice(currentDevice).runApplayUpdateScript(applyUpdateLocation, devicesMannager.getDevice(currentDevice).getOtaFileLocation());
		devicesMannager.getDevice(currentDevice).validateDeviceIsOnline(System.currentTimeMillis(), 10*60*1000, deviceEncrypted, Persona.PRIV , Persona.CORP);
	}

	
	@Test
	@TestProperties(name = "Enter Text \"${value}\" on UiObject by : Class : \"${childClassName}\", Text \"${childText}\", Description : \"${childDescription}\", Index : \"${childIndex}\" on ${persona}", paramsInclude = { "currentDevice,childClassName,childDescription,childIndex,childText,value,persona" })
	public void enterTextBy() throws Exception {

		Selector s = new Selector();

		if (childText != null)
			s.setText(childText);
		if (childClassName != null)
			s.setClassName(childClassName);
		if (childIndex != null)
			s.setIndex(Integer.valueOf(childIndex));
		if (childDescription != null)
			s.setDescription(childDescription);

		isPass = devicesMannager.getDevice(currentDevice).getPersona(persona).setText(s, value);
	}

	@Test
	@TestProperties(name = "Enter Text \"${value}\" on UiObject by Text \"${text}\" on ${persona}", paramsInclude = { "currentDevice,text,value,persona" })
	public void enterTextByText() throws Exception {
		Selector s = new Selector();
		s.setText(text);
		isPass = devicesMannager.getDevice(currentDevice).getPersona(persona).setText(s, value);
	}

	@Test
	@TestProperties(name = "Reboot Device", paramsInclude = {"deviceEncrypted"})
	public void rebootDevice() throws Exception {
		devicesMannager.getDevice(currentDevice).rebootDevice(deviceEncrypted, Persona.PRIV, Persona.CORP);
	}
	
	
	@Test
	@TestProperties(name = "Reboot Device - Check The Times", paramsInclude = { "currentDevice,timeout, deviceEncrypted" })
	public void rebootDeviceTimout() throws Exception {
		devicesMannager.getDevice(currentDevice).rebootDevice(Integer.valueOf(timeout), deviceEncrypted, Persona.PRIV, Persona.CORP);
	}

	@Test
	@TestProperties(name = "Reboot Recovery", paramsInclude = { "currentDevice,updateVersion, deviceEncrypted" })
	public void rebootRecovery() throws Exception {
		
		if (updateVersion) {
			String version = runProperties.getRunProperty("adb.push.file.location");
			report.report("New Version File " + version);
			devicesMannager.getDevice(currentDevice).executeHostShellCommand("echo 'boot-recovery ' > /cache/recovery/command");
			devicesMannager.getDevice(currentDevice).executeHostShellCommand("echo '--update_package=" + version + "'>> /cache/recovery/command");
		}
		boolean isUp = devicesMannager.getDevice(currentDevice).rebootRecoveryDevice(deviceEncrypted,Persona.PRIV, Persona.CORP);
		//here i check if the p
		if(!isUp) {
			devicesMannager.getDevice(currentDevice).rebootDevice(deviceEncrypted, Persona.PRIV, Persona.CORP);
		}
		Thread.sleep(2000);
	}

	@Test
	@TestProperties(name = "Press on Button ${button} on ${persona}", paramsInclude = { "currentDevice,button,persona" })
	public void pressBtn() throws Exception {
		try {
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey(button);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@Test
	@TestProperties(name = "Switch Persona to ${persona}", paramsInclude = { "currentDevice,persona" })
	public void switchPersona() throws Exception {
		devicesMannager.getDevice(currentDevice).switchPersona(persona);
	}
	
	
	@Test
	@TestProperties(name = "Click Text \"${text}\" , Class \"${text}\"  By Ui Location on ${persona}", paramsInclude = { "currentDevice,persona,text,childClassName" })
	public void clickTextAndClassByUiLocation() throws Exception {
		
		devicesMannager.getDevice(currentDevice).clickOnSelectorByUi(new Selector().setText(text).setClassName(childClassName), persona);
	}
	
	
	/**
	 * This function switch the network connection on/off
	 * 
	 * @param wifiNetwoek
	 * */
	@Test
	@TestProperties(name = "Switch network \"${wifiNetwork}\" connection ${onOff} on ${persona}", paramsInclude = { "currentDevice,persona,onOff,wifiNetwork,wifiPassword" })
	public void switchTheNetworkConnection() throws Exception {
		
		report.report("About to switch the the net work connectivity to : " + onOff + " on : "+ wifiNetwork);
		devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("home");
		
		devicesMannager.getDevice(currentDevice).getPersona(persona).openApp("Settings");
//		devicesMannager.getDevice(currentDevice).getPersona(persona).waitForExists(new Selector().setText("WIRELESS & NETWORKS"), 10000);

		String id = devicesMannager.getDevice(currentDevice).getPersona(persona).childByInstance(new Selector().setScrollable(true),
				new Selector().setClassName("android.widget.LinearLayout"), 1);
		devicesMannager.getDevice(currentDevice).clickOnSelectorByUi(id, persona);

		id = devicesMannager.getDevice(currentDevice).getPersona(persona).childByText(new Selector().setScrollable(true),
				new Selector().setClassName("android.widget.LinearLayout"), wifiNetwork, true);
		//if the WiFi is disconnected it takes time to find the requested network
		devicesMannager.getDevice(currentDevice).getPersona(persona).waitForExists(id, 20000);
		devicesMannager.getDevice(currentDevice).clickOnSelectorByUi(id, persona);

		try {
			if (onOff == State.OFF) {
				devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("Forget"));
			} else {
				try {
					devicesMannager.getDevice(currentDevice).getPersona(persona).setText(
							new Selector().setClassName("android.widget.EditText").setIndex(1), wifiPassword);
					devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");
					devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("Connect"));
				} catch (Exception e) {
					devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("Connect"));
				}
			}
		} catch (Exception e) {
			// the phone is in the correct condition
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("Cancel"));
		}
		devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("home");
		//sleep to get the system properties to update
		sleep(3000);
	}

	@Test
	@TestProperties(name = "Set text \"${text}\"  on ${persona}", paramsInclude = { "currentDevice,persona,text" })
	public void setText() throws UiObjectNotFoundException {
		devicesMannager.getDevice(currentDevice).getPersona(persona).setText(
				new Selector().setClassName("android.widget.EditText"), text);
	}
	
	/**
	 * This test is switching the wifi to the wanted condition
	 * */
	@Test
	@TestProperties(name = "Switch the wifi : ${onOff} Text on ${persona}", paramsInclude = { "currentDevice,persona,onOff" })
	public void switchTheWiFi() throws Exception {

		report.report("About to switch the the Wi-Fi to : " + onOff);
		
		if (onOff == State.ON)
			onOff = State.OFF;
		else
			onOff = State.ON;
		
		devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("home");
		devicesMannager.getDevice(currentDevice).getPersona(persona).openApp("Settings");
//		devicesMannager.getDevice(currentDevice).getPersona(persona).waitForExists(new Selector().setDescription("WIRELESS & NETWORKS"), 10000);

		String fatherInstance = devicesMannager.getDevice(currentDevice).getPersona(persona).getUiObject(
				new Selector().setClassName("android.widget.LinearLayout").setIndex(1));

		try {
			String objectId = devicesMannager.getDevice(currentDevice).getPersona(persona).getChild(fatherInstance,
					new Selector().setText(onOff.getValue()));
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(objectId);
		} catch (Exception e) {
			// in this case we are on the correct condition of the wifi
			// connection
		}

		devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("home");

	}

	/**
	 * This test is a full test from ping dns 1. Open the application 2. For
	 * maximum 3 times : 3. Run the ping test 4. Looks for the test : 0% packet
	 * loss 5. Fail the test wasn't found in the 3 runs, if it found the test
	 * will pass 6. Press home
	 * */
	@Test
	@TestProperties(name = "Ping Dns to : \"${text}\" Text on ${persona}", paramsInclude = { "currentDevice,persona,text,timeout" })
	public void pingDnsTest() throws Exception {

		final int numberOfTries = 3;
		interval = 100;
		boolean passTheTest = false;
		Selector s;
		String textFromTheRun;

		report.report("Runing dns test.");

		devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("home");

		devicesMannager.getDevice(currentDevice).getPersona(persona).openApp("Ping & DNS");

		try {
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("OK"));
		} catch (Exception e) {
		}

		// each iteration is for run the ping test
		for (int i = 0; i < numberOfTries; i++) {

			s = new Selector().setClassName("android.widget.EditText").setIndex(0);
			devicesMannager.getDevice(currentDevice).getPersona(persona).setText(s, text);
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(s);

			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("Go"));
			final long start = System.currentTimeMillis();

			ObjInfo[] objInfoArray = devicesMannager.getDevice(currentDevice).getPersona(persona).objInfoOfAllInstances(
					new Selector().setClassName("android.widget.TextView").setIndex(0)
							.setPackageName("com.ulfdittmer.android.ping"));
			textFromTheRun = objInfoArray[2].getText();

			while (!textFromTheRun.contains("% packet loss")) {
				if (System.currentTimeMillis() - start > Integer.valueOf(timeout)) {
					report.report("Could not find UiObject with text " + text + " after " + Integer.valueOf(timeout)
							/ 1000 + " sec.");
				}
				Thread.sleep(interval);
				objInfoArray = devicesMannager.getDevice(currentDevice).getPersona(persona).objInfoOfAllInstances(
						new Selector().setClassName("android.widget.TextView").setIndex(0)
								.setPackageName("com.ulfdittmer.android.ping"));
				textFromTheRun = objInfoArray[2].getText();
			}

			if (textFromTheRun.contains("0% packet loss")) {
				passTheTest = true;
				report.report(textFromTheRun);
				report.report("The run took : " + (double) ((System.currentTimeMillis() - start) / 1000) + " seconeds.");
				break;

			} else {
				report.report("Ping Dns number " + i + " failed.");
				report.report(textFromTheRun, Reporter.WARNING);
			}
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(
					new Selector().setClassName("android.widget.ImageButton").setIndex(0)
							.setPackageName("com.ulfdittmer.android.ping"));
		}

		if (passTheTest) {
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(
					new Selector().setClassName("android.widget.ImageButton").setIndex(0)
							.setPackageName("com.ulfdittmer.android.ping"));
			report.report("Passed the ping & dns test.");
		} else {
			report.report("Couldn't pass after " + numberOfTries + " tries.", Reporter.FAIL);
		}

		devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("home");
	}

	@Test
	@TestProperties(name = "Print all the network data from ${persona}", paramsInclude = { "currentDevice,persona" })
	public void printNetworkData() throws Exception {
		report.startLevel("Click Here for Device Net Configurations ("+persona+")");
		report.report("********netcfg********\n" + devicesMannager.getDevice(currentDevice).getPersona(persona).excuteCommand("netcfg"));
		report.report("********ip route********\n" + devicesMannager.getDevice(currentDevice).getPersona(persona).excuteCommand("ip route"));
		report.report("********netstat********\n" + devicesMannager.getDevice(currentDevice).getPersona(persona).excuteCommand("netstat"));
		report.report("********ip rule********\n" + devicesMannager.getDevice(currentDevice).getPersona(persona).excuteCommand("ip rule"));
		report.report("********ip route list table main********\n" + devicesMannager.getDevice(currentDevice).getPersona(persona).excuteCommand("ip route list table main"));
		report.report("********properties********\n" +", net.mobile : " + devicesMannager.getDevice(currentDevice).getPersona(persona).getProp("net.mobile")
				+", net.wifi : "+ devicesMannager.getDevice(currentDevice).getPersona(persona).getProp("net.wifi") + ", network.wifi : " + devicesMannager.getDevice(currentDevice).getPersona(persona).getProp("network.wifi"));
		report.stopLevel();
	}


	@Test
	@TestProperties(name = "Swipe Down Notification Bar on ${persona}", paramsInclude = { "currentDevice,persona" })
	public void openNotificationBar() throws Exception {
		devicesMannager.getDevice(currentDevice).getPersona(persona).openNotification();
	}

	@Test
	@TestProperties(name = "Validate UiObject with Description \"${text}\" Exists", paramsInclude = { "currentDevice,persona,text" })
	public void validateUiExistsByDesc() throws Exception {
		isExists = devicesMannager.getDevice(currentDevice).getPersona(persona).exist(new Selector().setDescription(text));
		runProperties.setRunProperty("isExists", String.valueOf(isExists));
	}
	
	/**
	 * This test :
	 * 1. get the return result from a class
	 * 2. validate that the expectedLine is exist in regex pattern
	 * 3. validate that the number is smaller that the first group of the pattern
	 * */
	@Test
	@TestProperties(name = "Validate Expression is ${size} with Class \"${text}\" than ${expectedLine}", paramsInclude = { "currentDevice,persona,text,index,expectedLine,expectedNumber,size" })
	public void validateExpressionIsSmallerByClass() throws Exception {
		
		report.report("bout to validate expression is smaller than : " +expectedNumber);
		String res = devicesMannager.getDevice(currentDevice).getPersona(persona).getText((new Selector().setClassName(text).setIndex(index)));
		report.report("The return result : "+res);
		Pattern pattern = Pattern.compile(expectedLine);
	    Matcher matcher = pattern.matcher(res);

	    if(matcher.find()) {
	        	report.report("Find : " + expectedLine + " in : " +res);
	        	String number = matcher.group(1);
	        	boolean isTrue;
	        	if(size == Size.Smaller) {
	        		isTrue = Double.valueOf(number) < Double.valueOf(expectedNumber);
	        	}
	        	else {
	        		isTrue = Double.valueOf(number) > Double.valueOf(expectedNumber);
	        	}
	        	
	        	if(isTrue) {
	        		report.report("The value is smaller than : "+res);
	        	}
	        	else {
	        		report.report("The value isn't "+ size.toString() +" than : "+res, Reporter.FAIL);
	        	}
	    }
	    else
	        report.report("Couldnt find : " + res + " in : " +res ,Reporter.FAIL);
	}
	
	
//	/**
//	 * This test :
//	 * 1. get the return result from a class
//	 * 2. validate that the expectedLine is exist in regex pattern
//	 * 3. validate that the number is smaller that the first group of the pattern
//	 * */
//	@Test
//	@TestProperties(name = "Validate Expression is bigger with Class \"${text}\" than ${expectedLine}", paramsInclude = { "currentDevice,persona,text,index,expectedLine,expectedNumber" })
//	public void validateExpressionIsBiggerByClass() throws Exception {
//		
//		report.report("about to validate expression is smaller than : " +expectedNumber);
//		String res = devicesMannager.getDevice(currentDevice).getPersona(persona).getText((new Selector().setClassName(text).setIndex(index)));
//		report.report("The return result : "+res);
//		Pattern pattern = Pattern.compile(expectedLine);
//	    Matcher matcher = pattern.matcher(res);
//
//	    if(matcher.find()) {
//	        	report.report("Find : " + expectedLine + " in : " +res);
//	        	String number = matcher.group(1);
//	        	if(Double.valueOf(number) > Double.valueOf(expectedNumber)) {
//	        		report.report("The value is smaller than : "+res);
//	        	}
//	        	else {
//	        		report.report("The value isn't smaller than : "+res, Reporter.FAIL);
//	        	}
//	    }
//	    else
//	        report.report("Couldnt find : " + res + " in : " +res ,Reporter.FAIL);
//	}
	
	/**
	 * This test :
	 * 1. get the return result from a class of the child from the father
	 * 2. validate that the expectedLine is exist in regex pattern
	 * 3. validate that the number is smaller that the first group of the pattern
	 * */
	@Test
	@TestProperties(name = "Validate Expression is ${size} with Class \"${text}\" than ${expectedLine} from the father", paramsInclude = { "currentDevice,persona,text,index,expectedLine,expectedNumber,fatherClass,fatherIndex,size" })
	public void validateExpressionIsBiggerByClassAndFather() throws Exception {
		
		
		Selector father = new Selector().setClassName(fatherClass).setIndex(Integer.valueOf(fatherIndex));
		
		String fatherInstance = devicesMannager.getDevice(currentDevice).getPersona(persona).getUiObject(father);
		report.report("The father id : " + fatherInstance);
		
		report.report("bout to validate expression is smaller than : " +expectedNumber);
		String objectId = devicesMannager.getDevice(currentDevice).getPersona(persona).getChild(fatherInstance, new Selector().setClassName(text).setIndex(index));
		
		String res = devicesMannager.getDevice(currentDevice).getPersona(persona).getText(objectId);
		report.report("The return result : "+res);
		Pattern pattern = Pattern.compile(expectedLine);
	    Matcher matcher = pattern.matcher(res);

	    if(matcher.find()) {
	        	report.report("Find : " + expectedLine + " in : " +res);
	        	String number = matcher.group(1);
	        	boolean isBigger;
	        	if(size == Size.Bigger) {
	        		isBigger = Double.valueOf(number) > Double.valueOf(expectedNumber);
	        	}
	        	else {
	        		isBigger = Double.valueOf(number) < Double.valueOf(expectedNumber);
	        	}
	        	
	        	if(isBigger) {
	        		report.report("The value is smaller than : "+res);
	        	}
	        	else {
	        		report.report("The value isn't smaller than : "+res, Reporter.FAIL);
	        	}
	    }
	    else
	        report.report("Couldnt find : " + expectedLine + " in : " +res ,Reporter.FAIL);
	}
	

	@Test
	@TestProperties(name = "Start Logs of Test", paramsInclude = {"currentDevice"})
	public void startLogging() throws Exception {
		devicesMannager.getDevice(currentDevice).initLogs();
	}

	@Test
	@TestProperties(name = "sleep", paramsInclude = { "currentDevice,timeout" })
	public void sleep() throws Exception {
		sleep(Integer.valueOf(timeout));
	}

	@Test
	@TestProperties(name = "Set Device as Root", paramsInclude = {"currentDevice"})
	public void setDeviceAsRoot() throws Exception {
		devicesMannager.getDevice(currentDevice).setDeviceAsRoot();
	}

	/**
	 * wake up
	 * switch persona
	 * enter the password and enter
	 * */
	@Test
	@TestProperties(name = "Enter Password for ${persona}", paramsInclude = { "currentDevice,persona,value" })
	public void enterPassword() throws Exception {
				
		report.report("****************000000000000000000000**************");
		devicesMannager.getDevice(currentDevice).switchPersona(persona);
		report.report("****************111111111111111111111**************");
		devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("1"));
		report.report("****************222222222222222222222**************");
		devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("1"));
		report.report("****************333333333333333333333**************");
		devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("1"));
		report.report("****************444444444444444444444**************");
		devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("1"));
		report.report("****************555555555555555555555**************");
		//wake up
		/*try {
			report.report("***************1111111111111111***************");
			Persona p = devicesMannager.getDevice(currentDevice).getForegroundPersona();
			devicesMannager.getDevice(currentDevice).getPersona(p).wakeUp();
		}
		catch(Exception e) {
			report.report("***************222222222222222222***************");
			report.report("Couldn't waking up." , Reporter.WARNING);
		}
		//switch persona
		try {
			report.report("******************33333333333333333333************");
			devicesMannager.getDevice(currentDevice).switchPersona(persona);
		}
		catch(Exception e) {do nothingreport.report("**********444444444444444444444444********************");}

		//1111 and Enter
//		for (char c : value.toCharArray()) {
			report.report("***************5555555555555555555555***************");
			try {
				devicesMannager.getDevice(currentDevice).getPersona(persona).wakeUp();
			}catch(Exception e) {report.report("*****************5.5.5.5.5.5.5.5.5.5*************"); }
			report.report("*****************6666666666666666666*************");
//			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText(String.valueOf(c)));
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("1"));
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("1"));
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("1"));
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("1"));
			
			report.report("***************777777777777777***************");
		
		report.report("************888888888888888888******************");
		devicesMannager.getDevice(currentDevice).getPersona(persona).wakeUp();
		report.report("************999999999999999999******************");
		devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setDescription("Enter"));
		report.report("****************000000000000000000000**************");*/
	}
	
	/**
	 * 
	 * */
	@Test
	@TestProperties(name = "Swipe And Login for ${persona}", paramsInclude = { "currentDevice,persona" })
	public void swipeAndLogin() throws Exception {
		//wake up
		try {
			devicesMannager.getDevice(currentDevice).getPersona(devicesMannager.getDevice(currentDevice).getForegroundPersona()).wakeUp();
		}
		catch(Exception e) {/*do nothing*/}
		//switch persona
		devicesMannager.getDevice(currentDevice).switchPersona(persona);
		//unlock by swipe
		devicesMannager.getDevice(currentDevice).unlockBySwipe(persona);
		
	}
	

	@Test
	@TestProperties(name = "Factory Data Reset", paramsInclude = { "currentDevice,persona,deviceEncrypted" })
	public void factoryDataReset() throws Exception {
		// devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("home");
		devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("Settings"));
		String id = devicesMannager.getDevice(currentDevice).getPersona(persona).childByText(new Selector().setScrollable(true),
				new Selector().setText("Backup & reset"), "Backup & reset", true);
		devicesMannager.getDevice(currentDevice).getPersona(persona).click(id);
		devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("Factory data reset"));
		devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("Reset phone"));
		boolean pin = devicesMannager.getDevice(currentDevice).getPersona(persona).exist(new Selector().setText("Confirm your PIN"));
		if (pin) {
			devicesMannager.getDevice(currentDevice).getPersona(persona).setText(new Selector().setClassName("android.widget.EditText"), "1111");
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("Next"));
		}
		devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("Erase everything"));
		sleep(2000);
		devicesMannager.getDevice(currentDevice).validateDeviceIsOnline(deviceEncrypted, Persona.PRIV, Persona.CORP);
	}

	@Test
	@TestProperties(name = "Rom Update Using Tucki", paramsInclude = { "currentDevice,serverHost,serverUrl,imgFile,version,adminToken,deviceId" })
	public void romUpdateWithTucki() throws Exception {
		// get MD5 of the file
		String md5 = devicesMannager.getDevice(currentDevice).md5sum(imgFile.getAbsolutePath());
		// get file size
		String fileSize = "" + imgFile.length();
		report.report("Take a sit , this will take a while (about 12-15 min.)");
		// upload rom
		String result = devicesMannager.getDevice(currentDevice).uploadRomOldServer(serverHost, imgFile.getAbsolutePath(), version, adminToken, md5);
		// send the remote update command
		String command = devicesMannager.getDevice(currentDevice).remoteUpdateOldServer(result, serverUrl, deviceId, md5, fileSize, version, adminToken);
		runProperties.setRunProperty("download.command", command);
	}

	@Test
	@TestProperties(name = "Wait For \"${expectedLine}\" in Logcat", paramsInclude = { "currentDevice,expectedLine,timeout,interval" })
	public void waitforLineInLogcat() throws Exception {
		devicesMannager.getDevice(currentDevice).waitForLineInTomcat(expectedLine, Integer.valueOf(timeout), interval);
	}

	@Test
	@TestProperties(name = "delete file ${remotefileLocation} from Device", paramsInclude = { "currentDevice,remotefileLocation" })
	public void deleteFile() throws Exception {
		devicesMannager.getDevice(currentDevice).deleteFile(remotefileLocation);
	}

	@Test
	@TestProperties(name = "Clean Device DB after Mock Tucki", paramsInclude = {"currentDevice"})
	public void cleanDBfromTucki() throws Exception {
		String command = runProperties.getRunProperty("download.command");
		if (command == null) {
			// report.report("Cannot find download command",Reporter.FAIL);
			throw new AnalyzerException();
		}
		String sqlQuueryString = "update tasks set synced=0,result='done' where payload='" + command + "';";
		report.report("SQL String = " + sqlQuueryString);
		devicesMannager.getDevice(currentDevice).sendSqlQuery(sqlQuueryString);
		devicesMannager.getDevice(currentDevice).executeHostShellCommand("agent_cli -s");
		devicesMannager.getDevice(currentDevice).sendSqlQuery("select * from tasks where payload='" + command + "';");

	}

	/**
	 * For Example: <br>
	 * - oops\\|kernel panic\\|soft lockup<br>
	 * - out_of_memory<br>
	 * - Timed out waiting for /dev/.coldboot_done<br>
	 * - EGL_BAD_ALLOC<br>
	 * - persona\\.\\*died\\|cellroxservice\\.\\*died\\|FATAL EXCEPTION<br>
	 * - AudioFlinger\\.\\*buffer overflow<br>
	 * - STATE_CRASH_RESET<br>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestProperties(name = "Stop and Validate Logs of Test", paramsInclude = { "currentDevice,expression" })
	public void stopLogAndValidate() throws Exception {
		LogParser logParser = new LogParser(expression);
		devicesMannager.getDevice(currentDevice).getLogs(logParser);
	}

	/**
	 * This test is validating the data after bringing the data itself
	 * For Example: <br>
	 * - oops\\|kernel panic\\|soft lockup<br>
	 * - out_of_memory<br>
	 * - Timed out waiting for /dev/.coldboot_done<br>
	 * - EGL_BAD_ALLOC<br>
	 * - persona\\.\\*died\\|cellroxservice\\.\\*died\\|FATAL EXCEPTION<br>
	 * - AudioFlinger\\.\\*buffer overflow<br>
	 * - STATE_CRASH_RESET<br>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestProperties(name = "Stop and Validate Syslogs", paramsInclude = { "currentDevice,expression" })
	public void stopSysLogAndValidate() throws Exception {
		LogParser logParser = new LogParser(expression);
		devicesMannager.getDevice(currentDevice).getLogsOfRun(logParser);
	}

	/**
	 * find the expression to find after cli command in the adb shell. Can be
	 * done with regular expression and with regular string.
	 * 
	 * @param cliCommand
	 *            - the wanted cli command after the adb shell
	 * @param expression
	 *            - the wanted text/expression to find
	 * @param RegularExpression
	 *            - is this expression is a regular expression
	 * */
	@Test
	@TestProperties(name = "Validate  expression in the cli command : \"${cliCommand}\" , with the text : \"${text}\"", paramsInclude = { "currentDevice,cliCommand,text,regularExpression" })
	public void validateExpressionCliCommand() throws Exception {
		devicesMannager.getDevice(currentDevice).validateExpressionCliCommand(cliCommand, text, regularExpression);
	}
	
	@Test
	@TestProperties(name = "Validate  expression in the cli command : \"${cliCommand}\" , with the text : \"${text}\"", paramsInclude = { "currentDevice,cliCommand,text,regularExpression" })
	public void validateExpressionCpuCliCommand() throws Exception {
		devicesMannager.getDevice(currentDevice).validateExpressionCliCommand(cliCommand, text, regularExpression, false);
	}

	/**
	 * find the expression to find after cli command in the adb shell. Can be
	 * done with regular expression and with regular string.
	 * 
	 * @param cliCommand
	 *            - the wanted cli command after the adb shell
	 * @param expression
	 *            - the wanted text/expression to find
	 * @param RegularExpression
	 *            - is this expression is a regular expression
	 * */
	@Test
	@TestProperties(name = "Validate  expression in the cli command : \"${cliCommand}\" , with the text : \"${text}\" , with persona : ${persona}", paramsInclude = { "currentDevice,cliCommand,text,regularExpression,persona" })
	public void validateExpressionCliCommandPersona() throws Exception {
		devicesMannager.getDevice(currentDevice).validateExpressionCliCommand(cliCommand, text, regularExpression, persona);
	}
	
	@Test
	@TestProperties(name = "Validate  expression in the cli cell command : \"${cliCommand}\" , with the text : \"${text}\" , with persona : ${persona}", paramsInclude = { "currentDevice,cliCommand,text,regularExpression,persona" })
	public void validateExpressionCliCommandCellPersona() throws Exception {
		devicesMannager.getDevice(currentDevice).validateExpressionCliCommandCell(cliCommand, text, regularExpression, persona);
	}

	@Test
	@TestProperties(name = "download the application: \"${appFullPath}\"", paramsInclude = { "currentDevice,appFullPath" })
	public void pushApplication() throws Exception {
		devicesMannager.getDevice(currentDevice).pushApplicationToDevice(appFullPath);
	}
	
	/**
	 * This test install the wanted application from the Play store
	 * persona - the wanted persona to work over it
	 * text - the name for the click
	 * appName - the name for the search
	 * */
	@Test
	@TestProperties(name = "install the application: \"${text}\" from google Store, on persona : ${persona}", paramsInclude = { "currentDevice,appName,text,persona" })
	public void installApplication() throws Exception {
		
		try {
		
			report.startLevel("Install the application: " +text);
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("home");
			devicesMannager.getDevice(currentDevice).getPersona(persona).openApp("Play Store");
			long start = System.currentTimeMillis();
			while (!devicesMannager.getDevice(currentDevice).getPersona(persona).exist(new Selector().setText("EDITORS' CHOICE"))) {
				if (System.currentTimeMillis() - start > Integer.valueOf(10 * 1000)) {
					report.report("Could not find UiObject with text EDITORS' CHOICE after " + Integer.valueOf(10 * 1000) / 1000
							+ " sec.", Reporter.WARNING);
					break;
				}
				Thread.sleep(1500);
			}
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setDescription("Search Google Play"));
			devicesMannager.getDevice(currentDevice).getPersona(persona).setText(new Selector().setTextContains("Search Google Play"), appName);
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("enter");
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setTextContains(text));
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("INSTALL"));
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("ACCEPT"));
					
			start = System.currentTimeMillis();
			while (!devicesMannager.getDevice(currentDevice).getPersona(persona).exist(new Selector().setText("UNINSTALL"))) {
				if (System.currentTimeMillis() - start > Integer.valueOf(60 * 1000)) {
					report.report("Could not find UiObject with text UNINSTALL after " + Integer.valueOf(10 * 1000) / 1000+ " sec.", Reporter.FAIL);
					break;
				}
				Thread.sleep(1500);
			}
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("OPEN"));
		}
		catch(Exception e) {
			report.report("Error" + e.getMessage(),Reporter.FAIL);
		}
		finally{
			report.stopLevel();
		}
		
	}
	
	
	/**
	 * This test uninstall the wanted application from the Play store
	 * persona - the wanted persona to work over it
	 * text - the name for the click
	 * appName - the name for the search
	 * */
	@Test
	@TestProperties(name = "uninstall the application: \"${text}\" from google Store, on persona : ${persona}", paramsInclude = { "currentDevice,appName,text,persona" })
	public void uninstallApplication() throws Exception {		
		try {
			report.startLevel("Uninstall the application: " +text);
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("home");
			devicesMannager.getDevice(currentDevice).getPersona(persona).openApp("Play Store");
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("home");
			devicesMannager.getDevice(currentDevice).getPersona(persona).openApp("Play Store");
			long start = System.currentTimeMillis();
			while (!devicesMannager.getDevice(currentDevice).getPersona(persona).exist(new Selector().setText("EDITORS' CHOICE"))) {
				if (System.currentTimeMillis() - start > Integer.valueOf(10 * 1000)) {
					report.report("Could not find UiObject with text EDITORS' CHOICE after " + Integer.valueOf(10 * 1000) / 1000
							+ " sec.", Reporter.WARNING);
					break;
				}
				Thread.sleep(1500);
			}
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setDescription("Search Google Play"));
			devicesMannager.getDevice(currentDevice).getPersona(persona).setText(new Selector().setTextContains("Search Google Play"), appName);
			
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("enter");
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setTextContains(text));
			
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("UNINSTALL"));
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("OK"));
					
			start = System.currentTimeMillis();
			while (!devicesMannager.getDevice(currentDevice).getPersona(persona).exist(new Selector().setText("INSTALL"))) {
				if (System.currentTimeMillis() - start > Integer.valueOf(60 * 1000)) {
					report.report("Could not find UiObject with text UNINSTALL after " + Integer.valueOf(10 * 1000) / 1000+ " sec.", Reporter.FAIL);
					break;
				}
				Thread.sleep(1500);
			}
			
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("home");
		}
		catch(Exception e) {
			report.report("Error" + e.getMessage(),Reporter.FAIL);
		}
		finally{
			report.stopLevel();
		}
	}
	
	/**
	 * This test is making a phone call to the wanted phone number.  
	 * 
	 * */
	@Test
	@TestProperties(name = "Call to : \"${phoneNumber}\" : ${persona}", paramsInclude = { "currentDevice,phoneNumber,persona" })
	public void callToAnotherPhone() throws Exception {
		
		try{
			report.startLevel("Calling to : "+ phoneNumber);
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("Phone"));
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setClassName("android.widget.ImageButton")
					.setPackageName("com.android.dialer").setIndex(1));
			devicesMannager.getDevice(currentDevice).getPersona(persona).setText(new Selector().setClassName("android.widget.EditText"), phoneNumber);
			//call
			report.report("Dailing...");
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setDescription("dial"));
			sleep(11000);
			//hangup
			report.report("hangup.");
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setDescription("End"));
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");
		}
		finally{
			report.stopLevel();
		}
		
	}
	
	/**
	 * This test is validate the missed call from the wanted number and clear the call log.
	 * Please insert the number including "-".
	 * */
	@Test
	@TestProperties(name = "Validate missed call from : \"${phoneNumber}\" : ${persona}", paramsInclude = { "currentDevice,phoneNumber,persona" })
	public void validateMissedCall() throws Exception {
		
//		phoneNumber = "052-542-7444";
		try{
			report.startLevel("Validate income call from : "+ phoneNumber);
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("Phone"));
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setClassName("android.widget.ImageButton")
					.setPackageName("com.android.dialer").setIndex(0));
			
			devicesMannager.getDevice(currentDevice).clickOnSelectorByUi(new Selector().setText("Missed"), persona);
			
			if(!devicesMannager.getDevice(currentDevice).getPersona(persona).exist(new Selector().setText(phoneNumber))) {
				report.report("Couldn't find the incoming call from : "+ phoneNumber , Reporter.FAIL);
			}
			else {
				report.report("Find the incoming call from : "+ phoneNumber);
			}
	
			//clear call log
			String fatherInstance = devicesMannager.getDevice(currentDevice).getPersona(persona).getUiObject(new Selector().setClassName("android.view.View").setIndex(0));
			report.report("The father id : " + fatherInstance);
			String objectId = devicesMannager.getDevice(currentDevice).getPersona(persona).getChild(fatherInstance, new Selector().setClassName("android.widget.ImageButton").setIndex(0));
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(objectId);
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("Clear call log"));
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("OK"));
			
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");
		
		}
		finally{
			report.stopLevel();
		}
		
	}
	
	
	/**
	 * The test send sms to the wanted number.
	 * */
	@Test
	@TestProperties(name = "Send sms to : \"${phoneNumber}\" : ${persona}", paramsInclude = { "currentDevice,phoneNumber,persona,messageContent" })
	public void sendSms() throws Exception {
		
		try{
			report.startLevel("Send sms to : "+ phoneNumber);
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("Messaging"));
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setDescription("New message"));
			devicesMannager.getDevice(currentDevice).getPersona(persona).setText(new Selector().setText("To"), phoneNumber);
			devicesMannager.getDevice(currentDevice).getPersona(persona).setText(new Selector().setText("Type message"), messageContent);
			//sending the sms
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setDescription("Send"));
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");
		}
		finally{
			report.stopLevel();
		}
	}
	
	
	/**
	 * The test validate that the sms recived and delete the message.
	 * Please insert the number including "-".
	 * */
	@Test
	@TestProperties(name = "Validate sms recieved from : \"${phoneNumber}\" : ${persona}", paramsInclude = { "currentDevice,phoneNumber,persona" })
	public void validateSmsRecieved() throws Exception {
		
		try{
			report.startLevel("Validate recieved sms from : "+ phoneNumber);
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("Messaging"));
			devicesMannager.getDevice(currentDevice).clickOnSelectorByUi(new Selector().setTextContains(phoneNumber), persona);
			
			if(!devicesMannager.getDevice(currentDevice).getPersona(persona).exist(new Selector().setClassName("android.widget.QuickContactBadge").setIndex(0))) {
				report.report("Couldn't find the recieved sms from : "+ phoneNumber , Reporter.FAIL);
			}
			else {
				report.report("Find the recieved sms from : "+ phoneNumber);
			}
			//delete this thread
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setClassName("android.widget.ImageButton").setIndex(2));
			devicesMannager.getDevice(currentDevice).clickOnSelectorByUi(new Selector().setText("Delete thread"), persona);
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("Delete"));
			
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");
			
		}
		finally{
			report.stopLevel();
		}
	}
	
	
	/**
	 * This function runs only in case of fails in the tests and check if
	 * unexpected reboot happened or persona crash or DOA
	 * */
	private void validateDeviceStatus() throws Exception {

		for (CellRoxDevice device : devicesMannager.getCellroxDevicesList()) {

			long knownUpTime = device.getUpTime();
			if (!device.isOnline()) {
				long startTime = System.currentTimeMillis();
				while(!device.isOnline()) {
					Thread.sleep(1000);
					if((System.currentTimeMillis() - startTime) >(5* 60 * 1000)) {
						report.report("Failed to get device after a fail.",Reporter.FAIL);
					}
				}
			}
			//check for DOA
			try {
				device.checkForDoa();
			}
			catch (Exception e) {
				report.report("DOA", Reporter.FAIL);
				knownUpTime = 0;
			}
			
			
			long deviceUpTime = device.getCurrentUpTime();
			if(knownUpTime > deviceUpTime) {
				//this is an indication that crash happaned
				report.report("There is an error, the device is offline, i'm going to do reboot.", Reporter.FAIL);
				// sleep
				sleep(20 * 1000);
				// last_kmsg
				report.report("Device : " + device.getDeviceSerial());
				device.printLastKmsg();
//				device.rebootDevice(deviceEncrypted, Persona.PRIV, Persona.CORP);
				device.validateDeviceIsOnline(System.currentTimeMillis(), 5* 60 *1000 , deviceEncrypted, Persona.PRIV, Persona.CORP);
				// configure
				device.configureDeviceForAutomation(true);
				// connect
				device.connectToServers();
				// to wake up and type password
				device.getPersona(Persona.CORP).wakeUp();
				device.switchPersona(Persona.CORP);
				device.getPersona(Persona.CORP).click(new Selector().setText("1"));
				device.getPersona(Persona.CORP).click(new Selector().setText("1"));
				device.getPersona(Persona.CORP).click(new Selector().setText("1"));
				device.getPersona(Persona.CORP).click(new Selector().setText("1"));
				device.getPersona(Persona.CORP).click(new Selector().setDescription("Enter"));
				device.getPersona(Persona.PRIV).wakeUp();
				device.switchPersona(Persona.PRIV);
				device.unlockBySwipe(Persona.PRIV);
				
			}
			
			
			//TODO to add a check that the persona crashed
			
//			boolean isOnline = device.isOnline();
//			boolean personasUp = device.isPersonasAreOnline(Persona.PRIV, Persona.CORP);
			// validate the device is online and ready
			
			

		}
	}
				
				
//				// this function has a timeout in case the device cannot be
//				// restarted
//				devicesMannager.getDevice(currentDevice).validateDeviceIsOnline(deviceEncrypted ,Persona.PRIV, Persona.CORP);
//				// TODO add crush report
//				report.report("Getting online after Crush", ReportAttribute.BOLD);
//			} catch (Exception e) {
//				report.report("Could not restart after reboot ", Reporter.FAIL);
//			}
//		} else {
//			// validate both personas are up, if not, wait get the run logs and
//			// reboot the device (from host)
//			if (!personasUp) {
//				// TODO add expressions
//				// devicesMannager.getDevice(currentDevice).rebootDevice(Persona.PRIV, Persona.CORP);
//				// LogParserExpression ex = new LogParserExpression();
//				// ex.setColor(Color.RED);
//				// ex.setExpression("CRUSH");
//				// ex.setNiceName("CRUSH");
//				// expression = new LogParserExpression[] { ex };
//				// LogParser logParser = new LogParser(expression);
//				// devicesMannager.getDevice(currentDevice).getLogsOfRun(logParser);
//				report.report("Perona Crush was detected!", ReportAttribute.BOLD);
//
//			}
//		}
		
	

	@After
	public void tearDown() throws Exception {
		try {
			report.startLevel("After");
			if (!isPass()) {
				validateDeviceStatus();
			}
		}
		finally {
			report.stopLevel();
		}
	}
	


	public File getLocalLocation() {
		return localLocation;
	}

	public String getRemotefileLocation() {
		return remotefileLocation;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setLocalLocation(File localLocation) {
		this.localLocation = localLocation;
	}

	public void setRemotefileLocation(String remotefileLocation) {
		this.remotefileLocation = remotefileLocation;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	/*
	 * @UseProvider(provider =
	 * jsystem.extensions.paramproviders.GenericObjectParameterProvider.class)
	 * public void setSelector(Selector selector) { this.selector = selector; }
	 */

	public String getButton() {
		return button;
	}

	public void setButton(String button) {
		this.button = button;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public LogParserExpression[] getExpression() {
		return expression;
	}

	@UseProvider(provider = jsystem.extensions.paramproviders.ObjectArrayParameterProvider.class)
	public void setExpression(LogParserExpression[] expression) {
		this.expression = expression;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isWaitForNewWindow() {
		return waitForNewWindow;
	}

	public void setWaitForNewWindow(boolean waitForNewWindow) {
		this.waitForNewWindow = waitForNewWindow;
	}

	public boolean isUpdateVersion() {
		return updateVersion;
	}

	public void setUpdateVersion(boolean updateVersion) {
		this.updateVersion = updateVersion;
	}

	public String getServerHost() {
		return serverHost;
	}

	public void setServerHost(String serverHost) {
		this.serverHost = serverHost;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getAdminToken() {
		return adminToken;
	}

	public void setAdminToken(String adminToken) {
		this.adminToken = adminToken;
	}

	public File getImgFile() {
		return imgFile;
	}

	public void setImgFile(File imgFile) {
		this.imgFile = imgFile;
	}

	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getExpectedLine() {
		return expectedLine;
	}

	public void setExpectedLine(String expectedLine) {
		this.expectedLine = expectedLine;
	}

	public long getInterval() {
		return interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	public boolean isExists() {
		return isExists;
	}

	public void setExists(boolean isExists) {
		this.isExists = isExists;
	}

	public Direction getDir() {
		return dir;
	}

	public void setDir(Direction dir) {
		this.dir = dir;
	}

	public String getChildClassName() {
		return childClassName;
	}

	public void setChildClassName(String childClassName) {
		this.childClassName = childClassName;
	}

	public int getInstance() {
		return instance;
	}

	public void setInstance(int instance) {
		this.instance = instance;
	}

	public String getWifiNetwork() {
		return wifiNetwork;
	}

	public void setWifiNetwork(String wifiNetwork) {
		this.wifiNetwork = wifiNetwork;
	}

	public String getWifiPassword() {
		return wifiPassword;
	}

	public void setWifiPassword(String wifiPassword) {
		this.wifiPassword = wifiPassword;
	}

	public boolean isExceptionThrower() {
		return exceptionThrower;
	}

	public void setExceptionThrower(boolean exceptionThrower) {
		this.exceptionThrower = exceptionThrower;
	}

	public String getFatherClass() {
		return fatherClass;
	}

	public void setFatherClass(String fatherClass) {
		this.fatherClass = fatherClass;
	}

	public String getFatherDescription() {
		return fatherDescription;
	}

	public void setFatherDescription(String fatherDescription) {
		this.fatherDescription = fatherDescription;
	}

	public String getFatherText() {
		return fatherText;
	}

	public void setFatherText(String fatherText) {
		this.fatherText = fatherText;
	}

	public String getFatherIndex() {
		return fatherIndex;
	}

	public void setFatherIndex(String fatherIndex) {
		this.fatherIndex = fatherIndex;
	}

	public String getChildClass() {
		return childClass;
	}

	public void setChildClass(String childClass) {
		this.childClass = childClass;
	}

	public String getChildDescription() {
		return childDescription;
	}

	public void setChildDescription(String childDescription) {
		this.childDescription = childDescription;
	}

	public String getChildText() {
		return childText;
	}

	public void setChildText(String childText) {
		this.childText = childText;
	}

	public String getChildIndex() {
		return childIndex;
	}

	public void setChildIndex(String childIndex) {
		this.childIndex = childIndex;
	}

	/**
	 * @return the cliCommand
	 */
	public String getCliCommand() {
		return cliCommand;
	}

	/**
	 * @param cliCommand
	 *            the cliCommand to set
	 */
	public void setCliCommand(String cliCommand) {
		this.cliCommand = cliCommand;
	}

	public boolean isRegularExpression() {
		return regularExpression;
	}

	public void setRegularExpression(boolean regularExpression) {
		this.regularExpression = regularExpression;
	}

	/**
	 * @return the appFullPath
	 */
	public String getAppFullPath() {
		return appFullPath;
	}

	/**
	 * @param appFullPath
	 *            the appFullPath to set
	 */
	public void setAppFullPath(String appFullPath) {
		this.appFullPath = appFullPath;
	}

	/**
	 * @return the onOff
	 */
	public State getOnOff() {
		return onOff;
	}

	/**
	 * @param onOff
	 *            the onOff to set
	 */
	public void setOnOff(State onOff) {
		this.onOff = onOff;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	

	public String getExpectedValue() {
		return expectedValue;
	}

	public void setExpectedValue(String expectedValue) {
		this.expectedValue = expectedValue;
	}

	/**
	 * @return the y
	 */
	public String getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(String y) {
		this.y = y;
	}

	/**
	 * @return the x
	 */
	public String getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(String x) {
		this.x = x;
	}

	/**
	 * @return the appName
	 */
	public String getAppName() {
		return appName;
	}

	/**
	 * @param appName the appName to set
	 */
	public void setAppName(String appName) {
		this.appName = appName;
	}
	
	public String getExpectedNumber() {
		return expectedNumber;
	}

	public void setExpectedNumber(String expectedNumber) {
		this.expectedNumber = expectedNumber;
	}

	/**
	 * @return the packageName
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * @param packageName the packageName to set
	 */
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	/**
	 * @return the isDeviceEncrypted
	 */
	public boolean isDeviceEncrypted() {
		return deviceEncrypted;
	}

	/**
	 * @param isDeviceEncrypted the isDeviceEncrypted to set
	 */
	public void setDeviceEncrypted(boolean isDeviceEncrypted) {
		this.deviceEncrypted = isDeviceEncrypted;
	}

	/**
	 * @return the applyUpdateLocation
	 */
	public String getApplyUpdateLocation() {
		return applyUpdateLocation;
	}

	/**
	 * @param applyUpdateLocation the applyUpdateLocation to set
	 */
	public void setApplyUpdateLocation(String applyUpdateLocation) {
		this.applyUpdateLocation = applyUpdateLocation;
	}

	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @param phoneNumber the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * @return the messageContent
	 */
	public String getMessageContent() {
		return messageContent;
	}

	/**
	 * @param messageContent the messageContent to set
	 */
	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}

	public DeviceNumber getCurrentDevice() {
		return currentDevice;
	}

	public void setCurrentDevice(DeviceNumber currentDevice) {
		this.currentDevice = currentDevice;
	}

	/**
	 * @return the size
	 */
	public Size getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(Size size) {
		this.size = size;
	}

	public String getLogsLocation() {
		return logsLocation;
	}

	public void setLogsLocation(String logsLocation) {
		this.logsLocation = logsLocation;
	}

	public LogcatHandler getLogType() {
		return logType;
	}

	public void setLogType(LogcatHandler logType) {
		this.logType = logType;
	}

}
