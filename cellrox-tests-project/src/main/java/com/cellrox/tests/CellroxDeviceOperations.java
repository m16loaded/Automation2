package com.cellrox.tests;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsystem.extensions.analyzers.compare.CompareValues;
import jsystem.framework.TestProperties;
import jsystem.framework.analyzer.AnalyzerException;
import jsystem.framework.report.Reporter;
import jsystem.framework.report.Reporter.ReportAttribute;
import jsystem.framework.scenario.UseProvider;
import junit.framework.SystemTestCase4;

import org.jsystemtest.mobile.core.ConnectionException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.topq.uiautomator.ObjInfo;
import org.topq.uiautomator.Selector;

import com.android.uiautomator.core.UiObjectNotFoundException;
import com.cellrox.infra.CellRoxDevice;
import com.cellrox.infra.enums.Direction;
import com.cellrox.infra.enums.Persona;
import com.cellrox.infra.enums.State;
import com.cellrox.infra.log.LogParser;
import com.cellrox.infra.object.LogParserExpression;

public class CellroxDeviceOperations extends SystemTestCase4 {

	CellRoxDevice device;
	File localLocation;
	String remotefileLocation;
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
	public String getExpectedNumber() {
		return expectedNumber;
	}

	public void setExpectedNumber(String expectedNumber) {
		this.expectedNumber = expectedNumber;
	}

	private long interval;
	private String timeout = "10000";
	private boolean isExists;
	private String sqlQuery;
	private Direction dir;
	private String childClassName;
	int instance = 0;
	private boolean exceptionThrower = true;
	// all the following string are for general function that use father,son
	private String fatherClass, fatherDescription, fatherText, fatherIndex, childClass, childDescription, childText,
			childIndex;
	private String cliCommand;
	private boolean regularExpression = false;
	private String appFullPath;
	private State onOff;
	private String wifiPassword;
	private String wifiNetwork;
	private String propertyName;
	private String expectedValue;
	private String x, y;

	@Before
	public void init() throws Exception {
		device = (CellRoxDevice) system.getSystemObject("device");
		// device.configureDeviceForAutomation(true);
		// device.connectToServers();
	}

	@Test
	public void orConnectivityTest() throws Exception {
		
/*		device.pushFileToDevice("~/git/automation/uiautomatorServer-19/bin/bundle.jar", "/tmp/");
		device.pushFileToDevice("~/git/automation/uiautomatorServer-19/bin/uiautomator-stub.jar", "/tmp/");*/
		
		device.configureDeviceForAutomation(true);
		device.connectToServers();
		
		

		System.out.println("##########################");
//		
		device.getPersona(Persona.PRIV).click(new Selector().setText("Settings"));
////		device.clickOnSelectorByUi(new Selector().setText("Settings"), persona.PRIV);
		sleep(20000);
		
		device.getPersona(Persona.PRIV).pressKey("home");
//		
	}

	@Test
	public void isTheDeviceConnected() throws Exception {
		device.isDeviceConnected();
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
			+ " , child : \"${childClass}\",\"${childDescription}\",\"${childText}\" ,\"${chilsIndex}\".", paramsInclude = { "persona,fatherClass,fatherDescription,fatherText,fatherIndex,childClass,childDescription,childText,childIndex,waitForNewWindow" })
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

		String fatherInstance = device.getPersona(persona).getUiObject(father);
		report.report("The father id : " + fatherInstance);
		String objectId = device.getPersona(persona).getChild(fatherInstance, child);
		report.report("The child id : " + objectId);

		if (waitForNewWindow) {
			report.report("im clicking and waiting");
			device.getPersona(persona).clickAndWaitForNewWindow(objectId, 10000);
		} else {
			report.report("im clicking");
			System.out.println(objectId);
			device.getPersona(persona).click(objectId);
		}

	}

	@Test
	@TestProperties(name = "open application by Text \"${text}\" on ${persona}", paramsInclude = { "text,persona" })
	public void openApp() throws UiObjectNotFoundException {
		device.getPersona(persona).openApp(text);
	}

	@Test
	@TestProperties(name = "push ${localLocation} to ${remotefileLocation}", paramsInclude = { "localLocation,remotefileLocation" })
	public void pushToDevice() throws Exception {
		device.pushFileToDevice(localLocation.getAbsolutePath(), remotefileLocation);
		runProperties.setRunProperty("adb.push.file.location", remotefileLocation);
	}

	@Test
	@TestProperties(name = "Configure Device", paramsInclude = {})
	public void configureDevice() throws Exception {
		device.configureDeviceForAutomation(true);

	}

	@Test
	@TestProperties(name = "Wait Until Device is Online", paramsInclude = {})
	public void validateDeviceIsOnline() throws Exception {
		device.validateDeviceIsOnline(Persona.PRIV, Persona.CORP);
	}

	@Test
	@TestProperties(name = "Connect to Servers", paramsInclude = {})
	public void connectToServers() throws Exception {
		device.connectToServers();
	}

	
	@Test
	@TestProperties(name = "Click on x,y on ${persona}", paramsInclude = { "x,y,persona" })
	public void clickByCordinate() {
		device.getPersona(persona).click(Integer.valueOf(x), Integer.valueOf(y));
	}
	
	@Test
	@TestProperties(name = "Click on UiObject by Text \"${text}\" on ${persona}", paramsInclude = { "text,selector,persona,waitForNewWindow,exceptionThrower" })
	public void clickByText() throws Exception {
		Selector s = new Selector();
		s.setText(text);
		try {
			if (waitForNewWindow) {
				device.getPersona(persona).clickAndWaitForNewWindow(s, 10000);
			} else {
				device.getPersona(persona).click(s);
			}
		} catch (Exception e) {
			if (exceptionThrower) {
				report.report("Could not find UiObject " + e.getMessage(), report.FAIL);
			} else {
				report.report("Could not find UiObject " + e.getMessage());
			}
		}
	}

	@Test
	@TestProperties(name = "Click on UiObject by Text Contains \"${text}\" on ${persona}", paramsInclude = { "text,selector,persona,waitForNewWindow" })
	public void clickByTextContains() throws Exception {
		Selector s = new Selector();
		s.setTextContains(text);
		try {
			if (waitForNewWindow) {
				device.getPersona(persona).clickAndWaitForNewWindow(s, 10000);
			} else {
				device.getPersona(persona).click(s);
			}
		} catch (Exception e) {
			report.report("Could not find UiObject " + e.getMessage(), report.FAIL);
		}
	}

	@Test
	@TestProperties(name = "Scroll and Click on UiObject by Text \"${text}\" on ${persona}", paramsInclude = { "text,persona" })
	public void scrollAndClick() throws Exception {
		Selector s = new Selector();
		s.setText(text);
		try {
			String objectId = device.getPersona(persona).childByText(new Selector().setScrollable(true),
					new Selector().setText(text), text, true);
			device.getPersona(persona).click(objectId);
		} catch (Exception e) {
			report.report("Could not find UiObject " + e.getMessage(), report.FAIL);
		}
	}

	@Test
	@TestProperties(name = "Click on UiObject by Description \"${text}\" on ${persona}", paramsInclude = { "text,persona,waitForNewWindow,exceptionThrower" })
	public void clickByDesc() throws Exception {
		Selector s = new Selector();
		s.setDescription(text);
		try {
			if (waitForNewWindow) {
				device.getPersona(persona).clickAndWaitForNewWindow(s, 10000);
			} else {
				device.getPersona(persona).click(s);
			}
		} catch (Exception e) {
			if (exceptionThrower) {
				report.report("Could not find UiObject " + e.getMessage(), report.FAIL);
			} else {
				report.report("Could not find UiObject " + e.getMessage());
			}
		}
	}

	@Test
	@TestProperties(name = "Wait for UiObject by Description \"${text}\" on ${persona}", paramsInclude = { "text,persona" })
	public void waitByDesc() throws Exception {
		Selector s = new Selector();
		s.setDescription(text);
		try {
			device.getPersona(persona).waitForExists(new Selector().setDescription(text), 10000);
		} catch (Exception e) {
			report.report("Could not find UiObject " + e.getMessage(), report.FAIL);
		}
	}

	@Test
	@TestProperties(name = "Get Full Text of Text Contains \"${text}\" on ${persona}", paramsInclude = { "text,persona" })
	public void getTextByTextContains() throws Exception {
		String msg = device.getPersona(persona).getText(new Selector().setTextContains(text));
		report.report(msg);
	}

	@Test
	@TestProperties(name = "Get Full Text of UiObject \"${text}/${childClassName}\" index ${instance}", paramsInclude = { "text,persona,childClassName,instance,index" })
	public void getTextTextViewFather() throws Exception {

		String object = device.getPersona(persona).childByInstance(new Selector().setClassName(text).setIndex(index),
				new Selector().setClassName(childClassName), instance);
		String msg = device.getPersona(persona).getText(object);
		report.report(msg);
	}

	@Test
	@TestProperties(name = "Wait for UiObject by Text \"${text}\" on ${persona}", paramsInclude = { "text,persona,timeout,interval" })
	public void waitByTextContains() throws Exception {
		final long start = System.currentTimeMillis();
		while (!device.getPersona(persona).exist(new Selector().setTextContains(text))) {
			if (System.currentTimeMillis() - start > Integer.valueOf(timeout)) {
				report.report("Could not find UiObject with text " + text + " after " + Integer.valueOf(timeout) / 1000
						+ " sec.", report.FAIL);
				break;
			}
			Thread.sleep(interval);
		}
	}

	@Test
	@TestProperties(name = "Wait for UiObject by Text \"${text}\" on ${persona}", paramsInclude = { "text,persona,timeout,interval" })
	public void waitByText() throws Exception {
		final long start = System.currentTimeMillis();
		while (!device.getPersona(persona).exist(new Selector().setText(text))) {
			if (System.currentTimeMillis() - start > Integer.valueOf(timeout)) {
				report.report("Could not find UiObject with text " + text + " after " + Integer.valueOf(timeout) / 1000
						+ " sec.", report.FAIL);
				break;
			}
			Thread.sleep(interval);
		}
	}

	@Test
	@TestProperties(name = "Validate Property \"${propertyName}\" equels \"${expectedValue}\"", paramsInclude = { "persona,propertyName,expectedValue" })
	public void validateProperty() throws Exception {

		String actualProperty = device.getPersona(persona).getProp(propertyName);
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
	@TestProperties(name = "Unlock Device by Swipe on ${persona}", paramsInclude = { "persona" })
	public void unlockBySwipe() throws Exception {

		try {
			ObjInfo oInfo = device.getPersona(persona).objInfo(new Selector().setDescription("Slide area."));
	
			int middleX = (oInfo.getBounds().getLeft() + oInfo.getBounds().getRight()) / 2;
			int middleY = (oInfo.getBounds().getTop() + oInfo.getBounds().getBottom()) / 2;
			device.getPersona(persona).swipe(middleX, middleY, oInfo.getBounds().getLeft() + 3, middleY, 20);
	
			device.getPersona(persona).pressKey("home");
		}
		catch(Exception e) {
			report.report("Error in unlocking the device.");
		}
	}

	@Test
	@TestProperties(name = " Swipe by Class Name on ${persona}", paramsInclude = { "persona,dir,text" })
	public void swipeByClassName() throws Exception {
		device.getPersona(persona).swipe(new Selector().setClassName(text), dir.getDir(), 20);
	}

	@Test
	@TestProperties(name = "Click on UiObject by ClassName \"${text}\" on ${persona}", paramsInclude = { "text,selector,persona,waitForNewWindow,index" })
	public void clickByClassName() throws Exception {
		Selector s = new Selector();
		s.setClassName(text);
		s.setIndex(index);
		s.setClickable(true);
		if (waitForNewWindow) {
			isPass = device.getPersona(persona).clickAndWaitForNewWindow(s, 10000);
		} else {
			try {
				isPass = device.getPersona(persona).click(s);
			} catch (Exception e) {
				report.report("baaa");
			}
		}
	}

	@Test
	@TestProperties(name = "Enter Text \"${value}\" on UiObject by Class Name \"${text}\" on ${persona}", paramsInclude = { "text,value,persona,index" })
	public void enterTextByClassName() throws Exception {
		Selector s = new Selector();
		s.setClassName(text);
		s.setIndex(index);
		isPass = device.getPersona(persona).setText(s, value);
	}

	@Test
	@TestProperties(name = "Wake Up", paramsInclude = {})
	public void wakeUp() throws Exception {
		try {
			device.getPersona(device.getForegroundPersona()).wakeUp();
		}
		catch(Exception e) {
			report.report("Error in the waking up.");
		}
	}

	@Test
	@TestProperties(name = "Enter Text \"${value}\" on UiObject by : Class : \"${childClassName}\", Text \"${childText}\", Description : \"${childDescription}\", Index : \"${childIndex}\" on ${persona}", paramsInclude = { "childClassName,childDescription,childIndex,childText,value,persona" })
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

		isPass = device.getPersona(persona).setText(s, value);
	}

	@Test
	@TestProperties(name = "Enter Text \"${value}\" on UiObject by Text \"${text}\" on ${persona}", paramsInclude = { "text,value,persona" })
	public void enterTextByText() throws Exception {
		Selector s = new Selector();
		s.setText(text);
		isPass = device.getPersona(persona).setText(s, value);
	}

	@Test
	@TestProperties(name = "Reboot Device", paramsInclude = {})
	public void rebootDevice() throws Exception {
		device.rebootDevice(Persona.PRIV, Persona.CORP);
	}

	@Test
	@TestProperties(name = "Reboot Device - Check The Times", paramsInclude = { "timeout" })
	public void rebootDeviceTimout() throws Exception {
		device.rebootDevice(Integer.valueOf(timeout), Persona.PRIV, Persona.CORP);
	}

	@Test
	@TestProperties(name = "Reboot Recovery", paramsInclude = { "updateVersion" })
	public void rebootRecovery() throws Exception {
		if (updateVersion) {
			String version = runProperties.getRunProperty("adb.push.file.location");
			report.report("New Version File " + version);
			device.executeHostShellCommand("echo 'boot-recovery ' > /cache/recovery/command");
			device.executeHostShellCommand("echo '--update_package=" + version + "'>> /cache/recovery/command");
		}
		device.rebootRecoveryDevice(Persona.PRIV, Persona.CORP);
		Thread.sleep(2000);
	}

	@Test
	@TestProperties(name = "Press on Button ${button} on ${persona}", paramsInclude = { "button,persona" })
	public void pressBtn() throws Exception {
		try {
			device.getPersona(persona).pressKey(button);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@Test
	@TestProperties(name = "Switch Persona to ${persona}", paramsInclude = { "persona" })
	public void switchPersona() throws Exception {
		Persona current = device.getForegroundPersona();
		if (current == persona) {
			report.report("Persona " + persona + " is Already in the Foreground");
		} else {
			device.getPersona(current).click(5, 5);
			current = device.getForegroundPersona();
			if (current == persona) {
				report.report("Switch to " + persona);
			} else {
				report.report("Could not Switch to " + persona, report.FAIL);
			}
		}
	}

	/**
	 * This function switch the network connection on/off
	 * 
	 * @param wifiNetwoek
	 * */
	@Test
	@TestProperties(name = "Switch network \"${wifiNetwork}\" connection ${onOff} on ${persona}", paramsInclude = { "persona,onOff,wifiNetwork,wifiPassword" })
	public void switchTheNetworkConnection() throws Exception {

		report.report("About to switch the the net work connectivity to : " + onOff + " on : "+ wifiNetwork);
		
		device.getPersona(persona).pressKey("home");
		device.getPersona(persona).click(new Selector().setText("Settings"));
		device.getPersona(persona).waitForExists(new Selector().setText("WIRELESS & NETWORKS"), 10000);

		String id = device.getPersona(persona).childByInstance(new Selector().setScrollable(true),
				new Selector().setClassName("android.widget.LinearLayout"), 1);
		device.clickOnSelectorByUi(id, persona);

		id = device.getPersona(persona).childByText(new Selector().setScrollable(true),
				new Selector().setClassName("android.widget.LinearLayout"), wifiNetwork, true);
		//if the WiFi is disconnected it takes time to find the requested network
		device.getPersona(persona).waitForExists(id, 20000);
		device.clickOnSelectorByUi(id, persona);

		try {
			if (onOff == State.OFF) {
				device.getPersona(persona).click(new Selector().setText("Forget"));
			} else {
				try {
					device.getPersona(persona).setText(
							new Selector().setClassName("android.widget.EditText").setIndex(1), wifiPassword);
					device.getPersona(persona).pressKey("back");
					device.getPersona(persona).click(new Selector().setText("Connect"));
				} catch (Exception e) {
					device.getPersona(persona).click(new Selector().setText("Connect"));
				}
			}
		} catch (Exception e) {
			// the phone is in the correct condition
			device.getPersona(persona).click(new Selector().setText("Cancel"));
		}
		device.getPersona(persona).pressKey("home");
		//sleep to get the system properties to update
		sleep(3000);
	}

	@Test
	@TestProperties(name = "Set text \"${text}\"  on ${persona}", paramsInclude = { "persona,text" })
	public void setText() throws UiObjectNotFoundException {
		device.getPersona(persona).setText(
				new Selector().setClassName("android.widget.EditText"), text);
	}
	
	/**
	 * This test is switching the wifi to the wanted condition
	 * */
	@Test
	@TestProperties(name = "Switch the wifi : ${onOff} Text on ${persona}", paramsInclude = { "persona,onOff" })
	public void switchTheWiFi() throws Exception {

		report.report("About to switch the the Wi-Fi to : " + onOff);
		
		if (onOff == State.ON)
			onOff = State.OFF;
		else
			onOff = State.ON;

		device.getPersona(persona).pressKey("home");
		device.getPersona(persona).click(new Selector().setText("Settings"));
		device.getPersona(persona).waitForExists(new Selector().setDescription("WIRELESS & NETWORKS"), 10000);

		String fatherInstance = device.getPersona(persona).getUiObject(
				new Selector().setClassName("android.widget.LinearLayout").setIndex(1));

		try {
			String objectId = device.getPersona(persona).getChild(fatherInstance,
					new Selector().setText(onOff.getValue()));
			device.getPersona(persona).click(objectId);
		} catch (Exception e) {
			// in this case we are on the correct condition of the wifi
			// connection
		}

		device.getPersona(persona).pressKey("home");

	}

	/**
	 * This test is a full test from ping dns 1. Open the application 2. For
	 * maximum 3 times : 3. Run the ping test 4. Looks for the test : 0% packet
	 * loss 5. Fail the test wasn't found in the 3 runs, if it found the test
	 * will pass 6. Press home
	 * */
	@Test
	@TestProperties(name = "Ping Dns to : \"${text}\" Text on ${persona}", paramsInclude = { "persona,text,timeout" })
	public void pingDnsTest() throws Exception {

		final int numberOfTries = 3;
		interval = 100;
		boolean passTheTest = false;
		Selector s;
		String textFromTheRun;

		report.report("Runing dns test.");

		device.getPersona(persona).pressKey("home");

		device.getPersona(persona).openApp("Ping & DNS");

		try {
			device.getPersona(persona).click(new Selector().setText("OK"));
		} catch (Exception e) {
		}

		// each iteration is for run the ping test

		for (int i = 0; i < numberOfTries; i++) {

			s = new Selector().setClassName("android.widget.EditText").setIndex(0);
			device.getPersona(persona).setText(s, text);
			device.getPersona(persona).click(s);

			device.getPersona(persona).click(new Selector().setText("Go"));
			final long start = System.currentTimeMillis();

			ObjInfo[] objInfoArray = device.getPersona(persona).objInfoOfAllInstances(
					new Selector().setClassName("android.widget.TextView").setIndex(0)
							.setPackageName("com.ulfdittmer.android.ping"));
			textFromTheRun = objInfoArray[2].getText();

			while (!textFromTheRun.contains("% packet loss")) {
				if (System.currentTimeMillis() - start > Integer.valueOf(timeout)) {
					report.report("Could not find UiObject with text " + text + " after " + Integer.valueOf(timeout)
							/ 1000 + " sec.");
				}
				Thread.sleep(interval);
				objInfoArray = device.getPersona(persona).objInfoOfAllInstances(
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
			device.getPersona(persona).click(
					new Selector().setClassName("android.widget.ImageButton").setIndex(0)
							.setPackageName("com.ulfdittmer.android.ping"));
		}

		if (passTheTest) {
			device.getPersona(persona).click(
					new Selector().setClassName("android.widget.ImageButton").setIndex(0)
							.setPackageName("com.ulfdittmer.android.ping"));
			report.report("Passed the ping & dns test.");
		} else {
			report.report("Couldn't pass after " + numberOfTries + " tries.", Reporter.FAIL);
		}

		device.getPersona(persona).pressKey("home");
	}

	@Test
	@TestProperties(name = "Print all the network data from ${persona}", paramsInclude = { "persona" })
	public void printNetworkData() throws Exception {
		report.startLevel("Click Here for Device Net Configurations ("+persona+")");
		report.report("********netcfg********\n" + device.getPersona(persona).excuteCommand("netcfg"));
		report.report("********iptables********\n" + device.getPersona(persona).excuteCommand("iptables"));
		report.report("********iptables nat********\n" + device.getPersona(persona).excuteCommand("iptables nat"));
		report.report("********ip route********\n" + device.getPersona(persona).excuteCommand("ip route"));
		report.report("********ifconfig********\n" + device.getPersona(persona).excuteCommand("ifconfig"));
		report.report("********netstat********\n" + device.getPersona(persona).excuteCommand("netstat"));
		report.report("********ip rule********\n" + device.getPersona(persona).excuteCommand("ip rule"));
		report.report("********ip route list table main********\n" + device.getPersona(persona).excuteCommand("ip route list table main"));
		//TODO add network properties
		report.stopLevel();
	}


	@Test
	@TestProperties(name = "Swipe Down Notification Bar on ${persona}", paramsInclude = { "persona" })
	public void openNotificationBar() throws Exception {
		device.getPersona(persona).openNotification();
	}

	@Test
	@TestProperties(name = "Validate UiObject with Description \"${text}\" Exists", paramsInclude = { "persona,text" })
	public void validateUiExistsByDesc() throws Exception {
		isExists = device.getPersona(persona).exist(new Selector().setDescription(text));
		runProperties.setRunProperty("isExists", String.valueOf(isExists));
	}
	
	/**
	 * This test :
	 * 1. get the return result from a class
	 * 2. validate that the expectedLine is exist in regex pattern
	 * 3. validate that the number is smaller that the first group of the pattern
	 * */
	@Test
	@TestProperties(name = "Validate Expression is smaller with Class \"${text}\" than ${expectedLine}", paramsInclude = { "persona,text,index,expectedLine,expectedNumber" })
	public void validateExpressionIsSmallerByClass() throws Exception {
		
		report.report("bout to validate expression is smaller than : " +expectedNumber);
		String res = device.getPersona(persona).getText((new Selector().setClassName(text).setIndex(index)));
		report.report("The return result : "+res);
		Pattern pattern = Pattern.compile(expectedLine);
	    Matcher matcher = pattern.matcher(res);

	    if(matcher.find()) {
	        	report.report("Find : " + expectedLine + " in : " +res);
	        	String number = matcher.group(1);
	        	if(Double.valueOf(number) < Double.valueOf(expectedNumber)) {
	        		report.report("The value is smaller than : "+expectedLine);
	        	}
	        	else {
	        		report.report("The value isn't smaller than : "+expectedLine, Reporter.FAIL);
	        	}
	    }
	    else
	        report.report("Couldnt find : " + expectedLine + " in : " +res ,Reporter.FAIL);
	}
	
	
	/**
	 * This test :
	 * 1. get the return result from a class
	 * 2. validate that the expectedLine is exist in regex pattern
	 * 3. validate that the number is smaller that the first group of the pattern
	 * */
	@Test
	@TestProperties(name = "Validate Expression is bigger with Class \"${text}\" than ${expectedLine}", paramsInclude = { "persona,text,index,expectedLine,expectedNumber" })
	public void validateExpressionIsBiggerByClass() throws Exception {
		
		report.report("about to validate expression is smaller than : " +expectedNumber);
		String res = device.getPersona(persona).getText((new Selector().setClassName(text).setIndex(index)));
		report.report("The return result : "+res);
		Pattern pattern = Pattern.compile(expectedLine);
	    Matcher matcher = pattern.matcher(res);

	    if(matcher.find()) {
	        	report.report("Find : " + expectedLine + " in : " +res);
	        	String number = matcher.group(1);
	        	if(Double.valueOf(number) > Double.valueOf(expectedNumber)) {
	        		report.report("The value is smaller than : "+expectedLine);
	        	}
	        	else {
	        		report.report("The value isn't smaller than : "+expectedLine, Reporter.FAIL);
	        	}
	    }
	    else
	        report.report("Couldnt find : " + expectedLine + " in : " +res ,Reporter.FAIL);
	}
	
	/**
	 * This test :
	 * 1. get the return result from a class of the child from the father
	 * 2. validate that the expectedLine is exist in regex pattern
	 * 3. validate that the number is smaller that the first group of the pattern
	 * */
	@Test
	@TestProperties(name = "Validate Expression is bigger with Class \"${text}\" than ${expectedLine} from the father", paramsInclude = { "persona,text,index,expectedLine,expectedNumber,fatherClass,fatherIndex" })
	public void validateExpressionIsBiggerByClassAndFather() throws Exception {
		
		
		Selector father = new Selector().setClassName(fatherClass).setIndex(Integer.valueOf(fatherIndex));
		
		String fatherInstance = device.getPersona(persona).getUiObject(father);
		report.report("The father id : " + fatherInstance);
		
		report.report("bout to validate expression is smaller than : " +expectedNumber);
		String objectId = device.getPersona(persona).getChild(fatherInstance, new Selector().setClassName(text).setIndex(index));
		
		String res = device.getPersona(persona).getText(objectId);
		report.report("The return result : "+res);
		Pattern pattern = Pattern.compile(expectedLine);
	    Matcher matcher = pattern.matcher(res);

	    if(matcher.find()) {
	        	report.report("Find : " + expectedLine + " in : " +res);
	        	String number = matcher.group(1);
	        	if(Double.valueOf(number) > Double.valueOf(expectedNumber)) {
	        		report.report("The value is smaller than : "+expectedLine);
	        	}
	        	else {
	        		report.report("The value isn't smaller than : "+expectedLine, Reporter.FAIL);
	        	}
	    }
	    else
	        report.report("Couldnt find : " + expectedLine + " in : " +res ,Reporter.FAIL);
	}
	

	@Test
	@TestProperties(name = "Start Logs of Test", paramsInclude = {})
	public void startLogging() throws Exception {
		device.initLogs();
	}

	@Test
	@TestProperties(name = "sleep", paramsInclude = { "timeout" })
	public void sleep() throws Exception {
		sleep(Integer.valueOf(timeout));
	}

	@Test
	@TestProperties(name = "Set Device as Root", paramsInclude = {})
	public void setDeviceAsRoot() throws Exception {
		device.setDeviceAsRoot();
	}

	@Test
	@TestProperties(name = "Enter Password for ${persona}", paramsInclude = { "persona,value" })
	public void enterPassword() throws Exception {
		try {
			for (char c : value.toCharArray()) {
				device.getPersona(persona).click(new Selector().setTextContains(String.valueOf(c)));
			}
			device.getPersona(persona).click(new Selector().setDescription("Enter"));
		}
		catch(Exception e) {
			report.report("There was an error in entering the password.");
		}
	}

	@Test
	@TestProperties(name = "Factory Data Reset", paramsInclude = { "persona" })
	public void factoryDataReset() throws Exception {
		// device.getPersona(persona).pressKey("home");
		device.getPersona(persona).click(new Selector().setText("Settings"));
		String id = device.getPersona(persona).childByText(new Selector().setScrollable(true),
				new Selector().setText("Backup & reset"), "Backup & reset", true);
		device.getPersona(persona).click(id);
		device.getPersona(persona).click(new Selector().setText("Factory data reset"));
		device.getPersona(persona).click(new Selector().setText("Reset phone"));
		boolean pin = device.getPersona(persona).exist(new Selector().setText("Confirm your PIN"));
		if (pin) {
			device.getPersona(persona).setText(new Selector().setClassName("android.widget.EditText"), "1111");
			device.getPersona(persona).click(new Selector().setText("Next"));
		}
		device.getPersona(persona).click(new Selector().setText("Erase everything"));
		sleep(2000);
		device.validateDeviceIsOnline(Persona.PRIV, Persona.CORP);
	}

	@Test
	@TestProperties(name = "Rom Update Using Tucki", paramsInclude = { "serverHost,serverUrl,imgFile,version,adminToken,deviceId" })
	public void romUpdateWithTucki() throws Exception {
		// get MD5 of the file
		String md5 = device.md5sum(imgFile.getAbsolutePath());
		// get file size
		String fileSize = "" + imgFile.length();
		report.report("Take a sit , this will take a while (about 12-15 min.)");
		// upload rom
		String result = device.uploadRomOldServer(serverHost, imgFile.getAbsolutePath(), version, adminToken, md5);
		// send the remote update command
		String command = device.remoteUpdateOldServer(result, serverUrl, deviceId, md5, fileSize, version, adminToken);
		runProperties.setRunProperty("download.command", command);
	}

	@Test
	@TestProperties(name = "Wait For \"${expectedLine}\" in Logcat", paramsInclude = { "expectedLine,timeout,interval" })
	public void waitforLineInLogcat() throws Exception {
		device.waitForLineInTomcat(expectedLine, Integer.valueOf(timeout), interval);
	}

	@Test
	@TestProperties(name = "delete file ${remotefileLocation} from Device", paramsInclude = { "remotefileLocation" })
	public void deleteFile() throws Exception {
		device.deleteFile(remotefileLocation);
	}

	@Test
	@TestProperties(name = "Clean Device DB after Mock Tucki", paramsInclude = {})
	public void cleanDBfromTucki() throws Exception {
		String command = runProperties.getRunProperty("download.command");
		if (command == null) {
			// report.report("Cannot find download command",report.FAIL);
			throw new AnalyzerException();
		}
		String sqlQuueryString = "update tasks set synced=0,result='done' where payload='" + command + "';";
		report.report("SQL String = " + sqlQuueryString);
		device.sendSqlQuery(sqlQuueryString);
		device.executeHostShellCommand("agent_cli -s");
		device.sendSqlQuery("select * from tasks where payload='" + command + "';");

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
	@TestProperties(name = "Stop and Validate Logs of Test", paramsInclude = { "expression" })
	public void stopLogAndValidate() throws Exception {
		LogParser logParser = new LogParser(expression);
		device.getLogs(logParser);
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
	@TestProperties(name = "Stop and Validate Syslogs", paramsInclude = { "expression" })
	public void stopSysLogAndValidate() throws Exception {
		LogParser logParser = new LogParser(expression);
		device.getLogsOfRun(logParser);
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
	@TestProperties(name = "Validate  expression in the cli command : \"${cliCommand}\" , with the text : \"${text}\"", paramsInclude = { "cliCommand,text,regularExpression" })
	public void validateExpressionCliCommand() throws Exception {
		device.validateExpressionCliCommand(cliCommand, text, regularExpression);
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
	@TestProperties(name = "Validate  expression in the cli command : \"${cliCommand}\" , with the text : \"${text}\" , with persona : ${persona}", paramsInclude = { "cliCommand,text,regularExpression,persona" })
	public void validateExpressionCliCommandPersona() throws Exception {
		device.validateExpressionCliCommand(cliCommand, text, regularExpression, persona);
	}

	@Test
	@TestProperties(name = "download the application: \"${appFullPath}\"", paramsInclude = { "appFullPath" })
	public void pushApplication() throws Exception {
		device.pushApplicationToDevice(appFullPath);
	}

	private void validateDeviceStatus() throws Exception {
		boolean isOnline = device.isOnline();
		boolean personasUp = device.isPersonasAreOnline(Persona.PRIV, Persona.CORP);
		// validate the device is online and ready
		if (!isOnline) {
			try {
				// this function has a timeout in case the device cannot be
				// restarted
				device.validateDeviceIsOnline(Persona.PRIV, Persona.CORP);
				// TODO add crush report
				report.report("Getting online after Crush", ReportAttribute.BOLD);
			} catch (ConnectionException e) {
				report.report("Could not restart after reboot ", report.FAIL);
			}
		} else {
			// validate both personas are up, if not, wait get the run logs and
			// reboot the device (from host)
			if (!personasUp) {
				// TODO add expressions
				// device.rebootDevice(Persona.PRIV, Persona.CORP);
				// LogParserExpression ex = new LogParserExpression();
				// ex.setColor(Color.RED);
				// ex.setExpression("CRUSH");
				// ex.setNiceName("CRUSH");
				// expression = new LogParserExpression[] { ex };
				// LogParser logParser = new LogParser(expression);
				// device.getLogsOfRun(logParser);
				report.report("Perona Crush was detected!", ReportAttribute.BOLD);

			}
		}

	}

	@After
	public void tearDown() throws Exception {
		if (!isPass()) {
			validateDeviceStatus();
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

	/*
	 * public Selector getSelector() { return selector; }
	 */

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

}
