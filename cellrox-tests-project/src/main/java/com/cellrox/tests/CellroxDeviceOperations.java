package com.cellrox.tests;

import java.io.File;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsystem.extensions.analyzers.compare.CompareValues;
import jsystem.framework.TestProperties;
import jsystem.framework.analyzer.AnalyzerException;
import jsystem.framework.report.Reporter;
import jsystem.framework.report.Reporter.ReportAttribute;
import jsystem.framework.report.Summary;
import jsystem.framework.scenario.UseProvider;

import org.junit.Test;
import org.python.core.exceptions;
import org.topq.uiautomator.ObjInfo;
import org.topq.uiautomator.Selector;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;
import com.cellrox.infra.CellRoxDevice;
import com.cellrox.infra.StaticUtils;
import com.cellrox.infra.enums.DeviceNumber;
import com.cellrox.infra.enums.Direction;
import com.cellrox.infra.enums.ElementAttributes;
import com.cellrox.infra.enums.LogcatHandler;
import com.cellrox.infra.enums.Persona;
import com.cellrox.infra.enums.Size;
import com.cellrox.infra.enums.State;
import com.cellrox.infra.log.LogParser;
import com.cellrox.infra.object.LogParserExpression;

public class CellroxDeviceOperations extends TestCase {

	private static final String LOGCAT_TIME_FORMAT = "MM-dd hh:mm:ss.SSS";
	Selector selector;
	private String button, text, value, selectorClass;
	private LogParserExpression[] expression;
	private boolean waitForNewWindow = false, updateVersion = false;
	private String serverHost, version, adminToken;
	private File imgFile;
	private String serverUrl, deviceId;
	private int index, indexOfSelector;
	private String expectedLine, expectedNumber;
	private String appName;
	private long interval;
	private String timeout = "10000";
	private boolean isExists;
	private Direction dir;
	int instance = 0, numberOfTimes = 1;
	private boolean exceptionThrower = true;
	// all the following string are for general function that use father,son
	private String fatherClass, fatherDescription, fatherText, fatherIndex, childClass, childDescription, childText, childIndex, childClassName;
	private String grandfatherClass, grandfatherIndex;
	private String cliCommand;
	private boolean regularExpression = false;
	private String appFullPath;
	private State onOff;
	private String wifiNetwork, wifiPassword, propertyName, expectedValue, packageName, x, y;
	private String applyUpdateLocation, phoneNumber;
	private String messageContent = "Hello from automation.";
	private Size size = Size.Smaller;
	private String logsLocation = System.getProperty("user.home") + "/LOGS_FROM_ADB";
	private LogcatHandler logType = LogcatHandler.PRIV;
	private String user, password;
	private boolean vellamoResultShow = false;
	private boolean  needForClearTheText = false;
	private boolean screenStatus;
	private boolean elementAttributeStatus;
	private ElementAttributes elementAttributes = ElementAttributes.ENABLED;
	private String localFilename;
	private String remoteFilepath;
	private long expectedDurationHostCorp;
	private long expectedDurationHostPriv;
	private int expectedNumber2;
	private int platformNew;
	private String PackageName;
	private String Location;

	/**
	 * Validate that the automation servers are alive. If the automation servers
	 * are answering ping, if not it will try
	 * */
	@Test
	@TestProperties(name = "Before Test Configure Connect Validation.", paramsInclude = { "currentDevice" })
	public void beforeTestConfigureConnectValidation() throws Exception {
		report.report("Validate that the automation servers are alive.");
		try {
			devicesMannager.getDevice(currentDevice).getPersona(Persona.PRIV).ping();
			devicesMannager.getDevice(currentDevice).getPersona(Persona.CORP).ping();
		} catch (Exception e) {
			report.report("Error with the automation servers, about to configure and connect.");
			devicesMannager.getDevice(currentDevice).configureDeviceForAutomation(true);
			devicesMannager.getDevice(currentDevice).connectToServers();
		}
	}

	/**
	 * Function open recent applications and removes all of the list
	 * */
	@Test
	@TestProperties(name = "Close all applications", paramsInclude = "currentDevice,persona")
	public void closeAllApplications() throws Exception {
		closeAllApplicationsFunction();
	}

	/**
	 * This test validate that both of the persona is exists on the running
	 * devices. The test was build for 1-2 devices
	 * */
	@Test
	@TestProperties
	public void validateThatDevicesIsreadyForAutomation() {

		if (devicesMannager.getDevice(DeviceNumber.PRIMARY).validateThatDevicesIsreadyForAutomation()) {
			devicesReadyForAutomation = false;
			return;
		}

		if (devicesMannager.getNumberOfDevices() > 1) {
			if (devicesMannager.getDevice(DeviceNumber.SECONDARY).validateThatDevicesIsreadyForAutomation()) {

				devicesReadyForAutomation = false;
			}
		}
	}
	@Test    //added by Igor 6.11
    @TestProperties(name = "Get Full Text of Text Contains And Replace + compare report \"${text}\" on ${persona}", paramsInclude = { "currentDevice,text,persona,expectedNumber2" })
    public void getTextByTextContainsTrim() throws Exception {
        String msg = devicesMannager.getDevice(currentDevice).getPersona(persona).getText(new Selector().setTextContains(text));
        //msg = msg.replace("You have not uploaded your score yet. Share it with the Vellamo community to see how ", "").replace(" compares to other popular devices.Upload it now?","").trim();
        msg = msg.replace("You have not uploaded your score yet. Share it with the Vellamo community to see how ", "").replace(" compares to other popular devices.Upload it now?",""); //.trim();
        msg=msg.substring(0, 4);
        report.report(msg,ReportAttribute.BOLD);
        int foo = Integer.parseInt(msg);
    
        
        
       // int expected= expectedNumber2;
        
//        switch(expectedNumber2){
//        case 1:expectedNumber2=2750;
//           if(foo<2750){
//        	   Summary.getInstance().setProperty("Vellamo_Results", Summary.getInstance().getProperty("Vellamo_Results") + msg + "is too low: "+(2750-foo)+" points difference"+ "\\");
//        	   report.report(msg+"is too low: "+(2750-foo)+" points difference",Reporter.FAIL);
//        	   break;
//           }
//        case 2:expectedNumber2=1400;
//        if(foo<1400){
//     	      Summary.getInstance().setProperty("Vellamo_Results", Summary.getInstance().getProperty("Vellamo_Results") + msg + "is too low: "+(1400-foo)+" points difference"+ "\\");
//     	      report.report(msg+"is too low: "+(2750-foo)+" points difference",Reporter.FAIL);
//       	      break;
//        }
//        case 3:expectedNumber2=1500;
//        if(foo<1500){
//     	   Summary.getInstance().setProperty("Vellamo_Results", Summary.getInstance().getProperty("Vellamo_Results") + msg + "is too low: "+(1500-foo)+" points difference"+ "\\");
//     	   report.report(msg+"is too low: "+(2750-foo)+" points difference",Reporter.FAIL);
//     	   break;
//        }
//        case 4:expectedNumber2=2444;
//        if(foo<2444){
//     	   Summary.getInstance().setProperty("Vellamo_Results", Summary.getInstance().getProperty("Vellamo_Results") + msg + "is too low: "+(2444-foo)+" points difference"+ "\\");
//     	  report.report(msg+"is too low: "+(2750-foo)+" points difference",Reporter.FAIL);
//       	  break;
//        }
//        case 5:expectedNumber2=1391;
//        if(foo<1391){
//     	   Summary.getInstance().setProperty("Vellamo_Results", Summary.getInstance().getProperty("Vellamo_Results") + msg + "is too low: "+(1391-foo)+" points difference"+ "\\");
//     	  report.report(msg+"is too low: "+(2750-foo)+" points difference",Reporter.FAIL);
//     	 break;
//        }
//        case 6:expectedNumber2=891;
//        if(foo<891){
//     	   Summary.getInstance().setProperty("Vellamo_Results", Summary.getInstance().getProperty("Vellamo_Results") + msg + "is too low: "+(891-foo)+" points difference"+ "\\");
//     	  report.report(msg+"is too low: "+(2750-foo)+" points difference",Reporter.FAIL);
//     	 break;
//        }
        
        if(foo<expectedNumber2){
        	Summary.getInstance().setProperty("Vellamo_Results", Summary.getInstance().getProperty("Vellamo_Results") + msg +" - " + " is too low: "+(expectedNumber2-foo)+" points difference! "+ "\\");
        	report.report(msg+"is too low: "+(expectedNumber2-foo)+" points difference",Reporter.FAIL);
        	
        }
        else{
        Summary.getInstance().setProperty("Vellamo_Results", Summary.getInstance().getProperty("Vellamo_Results") + msg + "\\");
        }
        
        }
        
      
     //added by Igor 6.11
	@Test    //added by Igor 10.11
    @TestProperties(name = "Get Full Text of Text Contains And Replace - Vellamo 3 Browser +compare\"${text}\" on ${persona}", paramsInclude = { "currentDevice,text,persona,expectedNumber" })
    public void getTextByTextContainsTrimBrowser() throws Exception {
        String msg = devicesMannager.getDevice(currentDevice).getPersona(persona).getText(new Selector().setTextContains(text));
        //msg = msg.replace("You have not uploaded your score yet. Share it with the Vellamo community to see how ", "").replace(" compares to other popular devices.Upload it now?","").trim();
        msg = msg.replace("You have not uploaded your score yet. Share it with the Vellamo community to see how ", "").replace(" compares to other popular devices.Upload it now?",""); //.trim();
        msg=msg.substring(0, 4);
        report.report(msg,ReportAttribute.BOLD);
        
        int foo = Integer.parseInt(msg);
        if(foo<2750){
        	Summary.getInstance().setProperty("Vellamo_Results", Summary.getInstance().getProperty("Vellamo_Results") + msg + "is too low: "+(2750-foo)+" points difference"+ "\\");
        	report.report(msg+"is too low: "+(2750-foo)+" points difference",Reporter.FAIL);
        }
        Summary.getInstance().setProperty("Vellamo_Results", Summary.getInstance().getProperty("Vellamo_Results") + msg + "\\");
    }  //added by Igor 10.11
	
	@Test    //added by Igor 14.12
    @TestProperties(name = "Get Full Text of Text Contains And Replace - SpeedTest\"${text}\" on ${persona}", paramsInclude = { "currentDevice,text,persona,expectedNumber" })
    public void getTextByTextContainsTrimBold() throws Exception {
        String msg = devicesMannager.getDevice(currentDevice).getPersona(persona).getText(new Selector().setTextContains(text));
        report.report(msg,ReportAttribute.BOLD);
        
        Summary.getInstance().setProperty("SpeedTest download speed", Summary.getInstance().getProperty("SpeedTest download speed") + msg + "\\");
    }  //added by Igor 14.12
	
	
	@Test    //added by Igor 10.11
    @TestProperties(name = "Get Full Text of Text Contains And Replace - Vellamo 3 Multicore + compare \"${text}\" on ${persona}", paramsInclude = { "currentDevice,text,persona" })
    public void getTextByTextContainsTrimMutlicore() throws Exception {
        String msg = devicesMannager.getDevice(currentDevice).getPersona(persona).getText(new Selector().setTextContains(text));
        //msg = msg.replace("You have not uploaded your score yet. Share it with the Vellamo community to see how ", "").replace(" compares to other popular devices.Upload it now?","").trim();
        msg = msg.replace("You have not uploaded your score yet. Share it with the Vellamo community to see how ", "").replace(" compares to other popular devices.Upload it now?",""); //.trim();
        msg=msg.substring(0, 4);
        report.report(msg,ReportAttribute.BOLD);
        //Summary.getInstance().setProperty("Vellamo_Results", Summary.getInstance().getProperty("Vellamo_Results") + msg + "\\");
        int foo = Integer.parseInt(msg);
        if(foo<1400){
        	Summary.getInstance().setProperty("Vellamo_Results", Summary.getInstance().getProperty("Vellamo_Results") + msg + "is too low: "+(2750-foo)+" points difference"+ "\\");
        	report.report(msg+"is too low: "+(1400-foo)+" points difference",Reporter.FAIL);
        }
        Summary.getInstance().setProperty("Vellamo_Results", Summary.getInstance().getProperty("Vellamo_Results") + msg + "\\");
    }  //added by Igor 10.11
	
	@Test    //added by Igor 10.11
    @TestProperties(name = "Get Full Text of Text Contains And Replace - Vellamo 3 Metal  + compare \"${text}\" on ${persona}", paramsInclude = { "currentDevice,text,persona" })
    public void getTextByTextContainsTrimMetal() throws Exception {
        String msg = devicesMannager.getDevice(currentDevice).getPersona(persona).getText(new Selector().setTextContains(text));
        //msg = msg.replace("You have not uploaded your score yet. Share it with the Vellamo community to see how ", "").replace(" compares to other popular devices.Upload it now?","").trim();
        msg = msg.replace("You have not uploaded your score yet. Share it with the Vellamo community to see how ", "").replace(" compares to other popular devices.Upload it now?",""); //.trim();
        msg=msg.substring(0, 4);
        report.report(msg,ReportAttribute.BOLD);
       // Summary.getInstance().setProperty("Vellamo_Results", Summary.getInstance().getProperty("Vellamo_Results") + msg + "\\");
        int foo = Integer.parseInt(msg);
        if(foo<1500){
        	Summary.getInstance().setProperty("Vellamo_Results", Summary.getInstance().getProperty("Vellamo_Results") + msg + "is too low: "+(2750-foo)+" points difference"+ "\\");
        	report.report(msg+"is too low: "+(1500-foo)+" points difference",Reporter.FAIL );
        }
        Summary.getInstance().setProperty("Vellamo_Results", Summary.getInstance().getProperty("Vellamo_Results") + msg + "\\");
    }  //added by Igor 10.11

	@Test
	@TestProperties(name = "Validate Personas Boot Time", paramsInclude = { "currentDevice,deviceEncrypted,deviceEncryptedPriv,expectedDurationHostCorp,expectedDurationHostPriv" })
	public void getLodingTime() throws Exception {
		devicesMannager.getDevice(currentDevice).getbootingDeviceTime(10 * 60 * 1000, deviceEncrypted, deviceEncryptedPriv, expectedDurationHostCorp,
				expectedDurationHostPriv, Persona.PRIV, Persona.CORP);
	}

	public long getExpectedDurationHostCorp() {
		return expectedDurationHostCorp;
	}

	public void setExpectedDurationHostCorp(long expectedDurationHostCorp) {
		this.expectedDurationHostCorp = expectedDurationHostCorp;
	}

	public long getExpectedDurationHostPriv() {
		return expectedDurationHostPriv;
	}

	public void setExpectedDurationHostPriv(long expectedDurationHostPriv) {
		this.expectedDurationHostPriv = expectedDurationHostPriv;
	}

	/**
	 * This function validate that the insert state is the correct one with the
	 * screen
	 * */
	@Test
	@TestProperties(name = "Validate the Screen is ${onOff}", paramsInclude = "currentDevice,persona,onOff")
	public void validateScreenIsOn() throws RemoteException {

		boolean isOn = devicesMannager.getDevice(currentDevice).getPersona(persona).isScreenOn();

		if (onOff == State.OFF) {
			// device should be off
			if (isOn) {
				report.report("The device screen is on and suppose to be off.", Reporter.FAIL);
			} else {
				report.report("The device screen is off");
			}
		} else {
			// device should be on
			if (isOn) {
				report.report("The device screen is on");

			} else {
				report.report("The device screen is off and suppose to be on.", Reporter.FAIL);
			}
		}
	}

	/**
	 * This function will pull a file from the device to a local file<br>
	 * The function will fail if the file does not exists
	 * */
	@Test
	@TestProperties(name = "Pull File from Device", paramsInclude = "currentDevice,remoteFilepath, localFilename")
	public void pullFileFromDevice() throws RemoteException {
		try {
			devicesMannager.getDevice(currentDevice).pullFileFromDevice(remoteFilepath, localFilename);
		} catch (Exception e) {
			report.report(e.getMessage(), report.FAIL);
		}
	}

	public String getLocalFilename() {
		return localFilename;
	}

	public void setLocalFilename(String localFilename) {
		this.localFilename = localFilename;
	}

	public String getRemoteFilepath() {
		return remoteFilepath;
	}

	public void setRemoteFilepath(String remoteFilepath) {
		this.remoteFilepath = remoteFilepath;
	}

	/**
	 * send to the agent DB the command and validate the returned answer.
	 * */
	@Test
	@TestProperties(name = "Validate Agent DB of the Command : ${text}", paramsInclude = "text,expectedLine,timeout,currentDevice")
	public void validateAgentDB() throws NumberFormatException, Exception {
		devicesMannager.getDevice(currentDevice).validateInAgent(text, expectedLine, Integer.valueOf(timeout));
	}

	/**
	 * This function validate that the ui object is exist in the screen by
	 * description and if not the function fails.
	 * */
	@Test
	@TestProperties(name = "Validate UiObject Exist By Description ${text}", paramsInclude = "currentDevice,persona,text")
	public void validateUiObjectExistByDescription() throws Exception {
		if (devicesMannager.getDevice(currentDevice).getPersona(persona).exist(new Selector().setDescription(text))) {
			report.report("The uiobject is found.");
		} else {
			report.report("Couldn't find the uiobject with the desc : " + text + ".", Reporter.FAIL);
		}
	}

	@Test
	@TestProperties(name = "pressPower", paramsInclude = { "currentDevice" })
	public void pressPower() throws Exception {
		devicesMannager.getDevice(currentDevice).getPersona(Persona.PRIV).excuteCommand("input keyevent 26");
		devicesMannager.getDevice(currentDevice).getPersona(Persona.CORP).excuteCommand("input keyevent 26");
	}

	/**
	 * This function validate that the ui object is exist in the screen by text
	 * and if not the function fails.
	 * */
	@Test
	@TestProperties(name = "Validate UiObject Exist By Text ${text}", paramsInclude = "currentDevice,persona,text")
	public void validateUiObjectExistByText() throws Exception {
		if (devicesMannager.getDevice(currentDevice).getPersona(persona).exist(new Selector().setText(text))) {
			report.report("The uiobject is found.");
		} else {
			report.report("Couldn't find the uiobject with the text : " + text + ".", Reporter.FAIL);
		}
	}

	/**
	 * This function : 1. Execute command 2. validate that the command output
	 * text is exists on the screen
	 * */
	@Test
	@TestProperties(name = "Validate Cli Command Output Exists On The Screen ${text}", paramsInclude = "currentDevice,persona,text")
	public void validateCliCommandOutputExistsOnTheScreen() throws Exception {
		devicesMannager.getDevice(currentDevice).validateCliCommandOutputExistsOnTheScreen(text, persona);
	}
	
	@Test   //added by Igor 16.11  (to CellroxDeviceOperations)
	@TestProperties(name = "Execute Command : ${text} local shell : ${currentDevice}", paramsInclude = { "currentDevice,text" })
	public void executeLocalCommandCli() throws Exception {
		devicesMannager.getDevice(currentDevice).executeCommandLocalCli(text);
	}  //added by Igor16.11
	
	
	
	
	
//	@Test //added by Igor 19.11
//	@TestProperties(name = "Validate Cli Local Command Output Exists On The Screen ${text}", paramsInclude = "currentDevice,persona,text")
//	public void validateCliLocalCommandOutputExistsOnTheScreen() throws Exception {
//		devicesMannager.getDevice(currentDevice).validateCliLocalCommandOutputExistsOnTheScreen(text, persona);
//	} //added by 19.11

	@Test
	@TestProperties(name = "Execute Command : ${text} in adb shell on : ${currentDevice} as root", paramsInclude = { "currentDevice,text" })
	public void executeCommandRoot() throws Exception {
		devicesMannager.getDevice(currentDevice).executeCommandAdbShellRoot(text);
	}

	@Test
	@TestProperties(name = "Execute Command : ${text} in adb shell on : ${currentDevice}", paramsInclude = { "currentDevice,text" })
	public void executeCommand() throws Exception {
		devicesMannager.getDevice(currentDevice).executeCommandAdbShell(text);
	}

	@Test
	@TestProperties(name = "Execute Command : ${text} in adb on : ${currentDevice}", paramsInclude = { "currentDevice,text" })
	public void executeCommandCli() throws Exception {
		devicesMannager.getDevice(currentDevice).executeCommandAdb(text);
	}
//	@Test   //added by Igor 16.11
//	@TestProperties(name = "Execute Command : ${text} local shell : ${currentDevice}", paramsInclude = { "currentDevice,text" })
//	public void executeLocalCommandCli() throws Exception {
//		//executeCommandLocalCli(text);
//		//executeCommandLocalCli(text);
//		devicesMannager.getDevice(currentDevice).executeCommandLocalCli(text);
//	}  //added by Igor16.11

	/**
	 * The function do the same action as the script get_logs_adb return .zip
	 * file of the logs
	 * */
	@Test
	@TestProperties(name = "Get the logs", paramsInclude = { "logsLocation,logType,currentDevice" })
	public void getTheLogs() throws Exception {
		devicesMannager.getDevice(currentDevice).getTheLogs(logType, logsLocation);
	}

	@Test
	@TestProperties(name = "Kill All Automation Processes on ${currentDevice}", paramsInclude = { "currentDevice" })
	public void killAllAutomationProcesses() throws Exception {
		devicesMannager.getDevice(currentDevice).killAllAutomaionProcesses();
	}

	@Test
	@TestProperties(name = "Verify ${currentDevice} Device Connected to ADB", paramsInclude = { "currentDevice" })
	public void isTheDeviceConnected() throws Exception {
		devicesMannager.getDevice(currentDevice).isDeviceConnected();
	}

	@Test
	@TestProperties(name = "Verify Uiautomator Server Status on ${currentDevice} Device (Configure if Necessary)",paramsInclude={"currentDevice"})
	public void isDeviceConnectedToServer() throws Exception {
		try {
			if (devicesMannager.getDevice(currentDevice).getPersona(Persona.PRIV).ping() == null){
				throw new Exception();
			}
			if (devicesMannager.getDevice(currentDevice).getPersona(Persona.CORP).ping() == null){
				throw new Exception();
			}
		} catch (Exception e) {
			report.report("UiAutomator server is down on the given device: "+devicesMannager.getDevice(currentDevice).getDeviceSerial()+" Configuring device...",ReportAttribute.BOLD);
			devicesMannager.getDevice(currentDevice).configureDeviceForAutomation(true);
		}
		report.report("UiAutomator server is up and running on the given device: "+devicesMannager.getDevice(currentDevice).getDeviceSerial(),ReportAttribute.BOLD);

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

	/**
	 * This is a general function for finding the father and than find the child
	 * and enter text to it. If you are not need some of the data, than do not
	 * enter the data
	 * */
	@Test
	@TestProperties(name = "enter text on the child from father ${persona} . father : \"${fatherClass}\",\"${fatherText}\",\"${fatherIndex}\" "
			+ " , child : \"${childClass}\",\"${childText}\" ,\"${chilsIndex}\".", paramsInclude = { "currentDevice,persona,fatherClass,fatherText,fatherIndex,childClass,childText,childIndex,value,needForClearTheText" })
	public void enterTextChildFromFather() throws Exception {

		Selector father = new Selector();
		Selector child = new Selector();

		// father
		if (fatherClass != null)
			father.setClassName(fatherClass);
		if (fatherText != null)
			father.setText(fatherText);
		if (fatherIndex != null)
			father.setIndex(Integer.valueOf(fatherIndex));

		// child
		if (childClass != null)
			child.setClassName(childClass);
		if (childText != null)
			child.setText(childText);
		if (childIndex != null)
			child.setIndex(Integer.valueOf(childIndex));

		String fatherInstance = devicesMannager.getDevice(currentDevice).getPersona(persona).getUiObject(father);
		report.report("The father id : " + fatherInstance);
		String objectId = devicesMannager.getDevice(currentDevice).getPersona(persona).getChild(fatherInstance, child);
		report.report("The child id : " + objectId);

		if (needForClearTheText) {
			devicesMannager.getDevice(currentDevice).getPersona(persona).clearTextField(objectId);
		}
		devicesMannager.getDevice(currentDevice).getPersona(persona).setText(objectId, value);

	}

	@Test
	@TestProperties(name = "open application by Text \"${text}\" on ${persona}", paramsInclude = { "currentDevice,text,persona" })
	public void openApp() throws UiObjectNotFoundException {
		if (!devicesMannager.getDevice(currentDevice).getPersona(persona).openApp(text)) {
			report.report("Couldn't open the application : " + text, Reporter.FAIL);
		}
	}
	@Test //vv
	@TestProperties(name = "Open apps 2 \"${text}\" on ${persona}", paramsInclude = { "currentDevice,text,persona" })
	public void openApp2() throws UiObjectNotFoundException {
		
//		if (!devicesMannager.getDevice(currentDevice).getPersona(persona).openApp2(text)) {
//			report.report("Couldn't open the application : " + text, Reporter.FAIL);
//		}
	//	UiObject AppsTab = new UiObject(new UiSelector().className("android.widget.TextView").description("Apps"));
//		String objectId = devicesMannager
//				.getDevice(currentDevice)
//				.getPersona(persona)
//				.childByText(new Selector().setScrollable(true).setClassName("android.widget.TextView").setDescription("Apps"));
//		
//		 
//        if(AppsTab.exists())
//            AppsTab.click();
//	}
		
		
        try {
        devicesMannager.getDevice(currentDevice).getPersona(persona).openApp(text);
        //if (!devicesMannager.getDevice(currentDevice).getPersona(persona).openApp("Music")) {
//        UiObject Settings = new UiObject(new UiSelector().text("Settings"));
//        Settings.click();
        }
        catch (Exception eE) {
            if (exceptionThrower) {
                report.report("Could not find UiObject " + eE.getMessage(), Reporter.FAIL);
            } else {
                report.report("Could not find UiObject " + eE.getMessage());
            }
            report.report("Couldn't open the application : " + text, Reporter.FAIL);
       
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
	}
	@Test
	@TestProperties(name = "push ${localLocation} to ${remotefileLocation}", paramsInclude = { "currentDevice,localLocation,remotefileLocation" })
	public void pushToDevice() throws Exception {

		report.report("About to push file to device");
		report.report(localLocation.getAbsolutePath());
		report.report(remotefileLocation);
		devicesMannager.getDevice(currentDevice).pushFileToDevice(localLocation.getAbsolutePath(), remotefileLocation);
		report.report("Finish to push file to device");
		runProperties.setRunProperty("adb.push.file.location", remotefileLocation);
	}

	/**
	 * This function push the wanted app to the location of the applications in
	 * the wanted persona
	 * */
	@Test
	@TestProperties(name = "push ${localLocation} to ${persona}", paramsInclude = { "currentDevice,localLocation,persona" })
	public void pushApplicationTo() throws Exception {
		String location;
		if (persona == Persona.PRIV)
			location = "/data/containers/priv/data/app/";
		else
			location = "/data/containers/corp/data/app/";
		devicesMannager.getDevice(currentDevice).pushApplication(localLocation.getAbsolutePath(), location);
	}

	@Test
	@TestProperties(name = "Configure Device", paramsInclude = { "currentDevice" })
	public void configureDevice() throws Exception {
		devicesMannager.getDevice(currentDevice).configureDeviceForAutomation(true);
	}

	@Test
	@TestProperties(name = "Configure Device Priv", paramsInclude = { "currentDevice" })
	public void configureDevicePriv() throws Exception {
		devicesMannager.getDevice(currentDevice).configureDeviceForPriv(true);
	}

	@Test
	@TestProperties(name = "Wait Until Device is Online", paramsInclude = { "currentDevice" })
	public void validateDeviceIsOnline() throws Exception {
		devicesMannager.getDevice(currentDevice).validateDeviceIsOnline(false, false, Persona.PRIV, Persona.CORP);
	}

	/**
	 * This test is validating that the device can run as root, if not it will
	 * reboot.
	 * */
	@Test
	@TestProperties(paramsInclude = { "deviceEncrypted, deviceEncryptedPriv" })
	public void validateDeviceCanBeRoot() throws Exception {
		devicesMannager.getDevice(currentDevice).validateDeviceCanBeRoot(deviceEncrypted, deviceEncryptedPriv, Persona.PRIV, Persona.CORP);
	}

	@Test
	@TestProperties(name = "Connect to Servers", paramsInclude = { "currentDevice" })
	public void connectToServers() throws Exception {
		devicesMannager.getDevice(currentDevice).connectToServers();
	}

	@Test
	@TestProperties(name = "Connect to Server priv", paramsInclude = { "currentDevice" })
	public void connectToServerPriv() throws Exception {
		devicesMannager.getDevice(currentDevice).connectToServerPriv();
	}

	@Test
	@TestProperties(name = "Click on ${x},${y} on ${persona}", paramsInclude = { "currentDevice,x,y,persona" })
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

	@Test //vv1
	@TestProperties(name = "Scroll and Click on UiObject by Text \"${text}\" on ${persona}", paramsInclude = { "currentDevice,text,persona" })
	public void scrollAndClick() throws Exception {

		try {
			String objectId = devicesMannager.getDevice(currentDevice).getPersona(persona)
					.childByText(new Selector().setScrollable(true), new Selector().setText(text), text, true);
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(objectId);
		} catch (Exception e) {
			report.report("Could not find UiObject " + e.getMessage(), Reporter.FAIL);
		}
	}

	@Test
	@TestProperties(name = "Scroll and Click on UiObject by Text \"${text}\" from the father with the class \"${fatherClass}\" and Index \"${fatherIndex}\"  on ${persona}", paramsInclude = { "currentDevice,text,persona,fatherIndex,fatherClass" })
	public void scrollAndClickByClass() throws Exception {

		try {
			String objectId = devicesMannager
					.getDevice(currentDevice)
					.getPersona(persona)
					.childByText(new Selector().setScrollable(true).setClassName(fatherClass).setIndex(Integer.valueOf(fatherIndex)),
							new Selector().setText(text), text, true);
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(objectId);
		} catch (Exception e) {
			report.report("Could not find UiObject " + e.getMessage(), Reporter.FAIL);
		}
	}
	
//	//getPersona(persona).swipe(middleX, middleY, oInfo.getBounds().getLeft() + 3, middleY, 20);
//	@Test //added by Igor 04.03
//	@TestProperties(name = "Scroll only ${Location} on ${persona}", paramsInclude = { "currentDevice,persona,Location" })
//	public void scrollOnly() throws Exception {
//
//		try {
//			int startX = 0;
//			int startY = 0;
//			int endX = 0;
//			int endY = 0;
//	if(Location == "UP"){
//		startX = 500;
//		startY = 1600;
//		endX = 500;
//		endY = 400;
//		
//		
//	}
//
//			
//			
//			
//			
//			devicesMannager.getDevice(currentDevice).getPersona(persona).swipe(startX, startY, endX, endY, 20);
//		} catch (Exception e) {
//			report.report("Could not find UiObject " + e.getMessage(), Reporter.FAIL);
//		}
//	}

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

	/**
	 * Print the text from ui object's text
	 * */
	@Test
	@TestProperties(name = "Get Full Text of Text Contains \"${text}\" on ${persona}", paramsInclude = { "currentDevice,text,persona" })
	public void getTextByTextContains() throws Exception {
		String msg = devicesMannager.getDevice(currentDevice).getPersona(persona).getText(new Selector().setTextContains(text));
		report.report(msg);
	}

	/**
	 * Print the text from ui object's text
	 * */
	@Test
	@TestProperties(name = "Get Full Text of UiObject \"${text}/${childClassName}\" index ${instance}", paramsInclude = { "currentDevice,text,persona,childClassName,instance,index" })
	public void getTextTextViewFather() throws Exception {

		String object = devicesMannager.getDevice(currentDevice).getPersona(persona)
				.childByInstance(new Selector().setClassName(text).setIndex(index), new Selector().setClassName(childClassName), instance);
		String msg = devicesMannager.getDevice(currentDevice).getPersona(persona).getText(object);
		report.report(msg);
	}

	/**
	 * Waiting until the uiobject will come to the screen
	 * */
	@Test
	@TestProperties(name = "Wait for UiObject by Text \"${text}\" on ${persona}", paramsInclude = { "currentDevice,text,persona,timeout,interval" })
	public void waitByTextContains() throws Exception {
		final long start = System.currentTimeMillis();
		while (!devicesMannager.getDevice(currentDevice).getPersona(persona).exist(new Selector().setTextContains(text))) {
			if (System.currentTimeMillis() - start > Integer.valueOf(timeout)) {
				report.report("Could not find UiObject with text " + text + " after " + Integer.valueOf(timeout) / 1000 + " sec.", Reporter.FAIL);
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
				report.report("Could not find UiObject with text " + text + " after " + Integer.valueOf(timeout) / 1000 + " sec.", Reporter.FAIL);
				break;
			}
			Thread.sleep(interval);
		}
	}

	@Test
	@TestProperties(name = "Wait for UiObject by Class \"${text}\" on ${persona}", paramsInclude = { "currentDevice,text,persona,timeout,interval,index,packageName" })
	public void waitByClass() throws Exception {
		final long start = System.currentTimeMillis();
		while (!devicesMannager.getDevice(currentDevice).getPersona(persona)
				.exist(new Selector().setClassName(text).setIndex(index).setPackageName(packageName))) {
			if (System.currentTimeMillis() - start > Integer.valueOf(timeout)) {
				report.report("Could not find UiObject with text " + text + " after " + Integer.valueOf(timeout) / 1000 + " sec.", Reporter.FAIL);
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

	/**
	 * Swipe by Class Name to the Given Direction.
	 * */
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
	@TestProperties(name = "Enter Text \"${value}\" on UiObject by Class Name \"${text}\" on ${persona}", paramsInclude = { "needForClearTheText,currentDevice,text,value,persona,index" })
	public void enterTextByClassName() throws Exception {
		Selector s = new Selector();
		s.setClassName(text);
		s.setIndex(index);
		if (needForClearTheText) {
			devicesMannager.getDevice(currentDevice).getPersona(persona).clearTextField(s);

		}
		isPass = devicesMannager.getDevice(currentDevice).getPersona(persona).setText(s, value);
	}

	@Test
	@TestProperties(name = "Wake Up", paramsInclude = { "currentDevice" })
	public void wakeUp() throws Exception {
		try {
			devicesMannager.getDevice(currentDevice).getPersona(devicesMannager.getDevice(currentDevice).getForegroundPersona()).wakeUp();
		} catch (Exception e) {
			report.report("Error waking up " + devicesMannager.getDevice(currentDevice).getForegroundPersona(), report.FAIL);
		}
	}

	/**
	 * This test return the status of the screen.<br>
	 * This test return boolean parameter : ScreenStatus.<br>
	 * The parameter can be used later in the scenario as &{ScreenStatus).
	 * */
	@Test
	@TestProperties(name = "Get Screen Status", paramsInclude = { "currentDevice" }, returnParam = { "screenStatus" })
	public void getTheScreenStatus() throws Exception {
		screenStatus = devicesMannager.getDevice(currentDevice).getPersona(devicesMannager.getDevice(currentDevice).getForegroundPersona()).isScreenOn();
	}

	/**
	 * This test return the status of the element attribute.<br>
	 * This test return boolean parameter : elementAttributeStatus.<br>
	 * The parameter can be used later in the scenario as
	 * &{ElementAttributeStatus).
	 * */
	@Test
	@TestProperties(name = "Get The Element Attribute ${elementAttributes} of${text} ,on ${persona}", paramsInclude = { "currentDevice,persona,text,elementAttributes" }, returnParam = { "elementAttributeStatus" })
	public void getTheElementAttribute() throws Exception {

		ObjInfo objInfo = devicesMannager.getDevice(currentDevice).getPersona(persona).objInfo(new Selector().setText(text));

		switch (elementAttributes) {
		case ENABLED:
			elementAttributeStatus = objInfo.isEnabled();
			break;

		case CLICKABLE:
			elementAttributeStatus = objInfo.isClickable();
			break;

		default:
			break;
		}

	}

	@Test
	@TestProperties(name = "Enter Text \"${value}\" on UiObject by : Class : \"${childClassName}\", Text \"${childText}\", Description : \"${childDescription}\", Index : \"${childIndex}\" on ${persona}", paramsInclude = { "needForClearTheText,currentDevice,childClassName,childDescription,childIndex,childText,value,persona" })
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
		if (needForClearTheText) {
			devicesMannager.getDevice(currentDevice).getPersona(persona).clearTextField(s);

		}
		
		isPass = devicesMannager.getDevice(currentDevice).getPersona(persona).setText(s, value);
	}

	@Test
	@TestProperties(name = "Enter Text \"${value}\" on UiObject by Text \"${text}\" on ${persona}", paramsInclude = { "needForClearTheText,currentDevice,text,value,persona" })
	public void enterTextByText() throws Exception {
		Selector s = new Selector();
		s.setText(text);
		if (needForClearTheText) {
			devicesMannager.getDevice(currentDevice).getPersona(persona).clearTextField(s);

		}
		isPass = devicesMannager.getDevice(currentDevice).getPersona(persona).setText(s, value);
	}

	@Test
	@TestProperties(name = "Reboot Device", paramsInclude = { "deviceEncrypted,deviceEncryptedPriv" })
	public void rebootDevice() throws Exception {
		devicesMannager.getDevice(currentDevice).rebootDevice(deviceEncrypted, deviceEncryptedPriv, Persona.PRIV, Persona.CORP);
	}

	@Test
	@TestProperties(name = "Reboot Device - Check The Times", paramsInclude = { "currentDevice,timeout, deviceEncrypted,deviceEncryptedPriv" })
	public void rebootDeviceTimout() throws Exception {
		devicesMannager.getDevice(currentDevice).rebootDevice(Integer.valueOf(timeout), deviceEncrypted, deviceEncryptedPriv, Persona.PRIV, Persona.CORP);
	}

	@Test
	@TestProperties(name = "Reboot Recovery", paramsInclude = { "currentDevice,updateVersion, deviceEncrypted, deviceEncryptedPriv" })
	public void rebootRecovery() throws Exception {

		if (updateVersion) {
			String version = runProperties.getRunProperty("adb.push.file.location");
			report.report("New Version File " + version);
			devicesMannager.getDevice(currentDevice).executeHostShellCommand("echo 'boot-recovery ' > /cache/recovery/command");
			devicesMannager.getDevice(currentDevice).executeHostShellCommand("echo '--update_package=" + version + "'>> /cache/recovery/command");
		}
		boolean isUp = devicesMannager.getDevice(currentDevice).rebootRecoveryDevice(deviceEncrypted, deviceEncryptedPriv, Persona.PRIV, Persona.CORP);
		// here i check if the p
		if (!isUp) {
			devicesMannager.getDevice(currentDevice).rebootDevice(deviceEncrypted, deviceEncryptedPriv, Persona.PRIV, Persona.CORP);
		}
		Thread.sleep(2000);
	}
	
	@Test
	@TestProperties(name = "Reboot Recovery Shamu", paramsInclude = { "currentDevice,updateVersion, deviceEncrypted, deviceEncryptedPriv" })
	public void rebootRecoveryShamu() throws Exception { //added by Igor

		if (updateVersion) {
			String version = runProperties.getRunProperty("adb.push.file.location");
			report.report("New Version File " + version);
//			devicesMannager.getDevice(currentDevice).executeHostShellCommand("echo 'boot-recovery ' > /data/media/ota");
//			devicesMannager.getDevice(currentDevice).executeHostShellCommand("echo '--update_package=" + version + "'>> /data/media/ota");
			devicesMannager.getDevice(currentDevice).executeCommandLocalCli("/home/topq/git/automation/uiautomator-client-master/cellrox-update-Igor.sh /home/topq/main_jenkins/workspace/Automation_Nightly_Hammerhead_Lollipop_SHAMU/artifacts/unit-tests.tgz");
			//devicesMannager.getDevice(currentDevice).executeHostShellCommand("/home/topq/git/automation/uiautomator-client-master/cellrox-update-Igor.sh /home/topq/main_jenkins/workspace/Automation_Nightly_Hammerhead_Lollipop_ALT/artifacts/unit-tests.tgz");
		}
		boolean isUp = devicesMannager.getDevice(currentDevice).rebootRecoveryDevice(deviceEncrypted, deviceEncryptedPriv, Persona.PRIV, Persona.CORP);
		// here i check if the p
		if (!isUp) {
			devicesMannager.getDevice(currentDevice).rebootDevice(deviceEncrypted, deviceEncryptedPriv, Persona.PRIV, Persona.CORP);
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

	/**
	 * The test pass if the wanted persona is the one that is on the screen
	 * */
	@Test
	@TestProperties(name = "Validate that ${persona} is on Foreground ", paramsInclude = { "currentDevice,persona" })
	public void validateWantedPersona() throws Exception {
		devicesMannager.getDevice(currentDevice).validateWantedPersona(persona);
	}

	/**
	 * This function is mainly used to click on the currect wifi network
	 * 
	 * @throws Exception
	 */
	@Test
	@TestProperties(name = "Click On Son Text ${childText} By Father Class ${fatherClass}", paramsInclude = { "currentDevice,persona,fatherClass,childText" })
	public void clickOnSonTextByFatherClass() throws Exception {
		// childText = "cellrox-tplink";
		// fatherClass="android.widget.LinearLayout";
		// persona = Persona.PRIV;
		// devicesMannager.getDevice(currentDevice).connectToServers();
		try {
			String id = devicesMannager.getDevice(currentDevice).getPersona(persona)
					.childByText(new Selector().setScrollable(true), new Selector().setClassName(fatherClass), childText, true);
			devicesMannager.getDevice(currentDevice).getPersona(persona).waitForExists(id, 20000);
			devicesMannager.getDevice(currentDevice).clickOnSelectorByUi(id, persona);
		} catch (Exception e) {
			report.report("Couldn't find " + childText + Reporter.FAIL);
			return;
		}
	}

	/**
	 * The function finds the location of the wanted ui object and click on it
	 * in the middle of it location
	 * */
	@Test
	@TestProperties(name = "Click Text \"${text}\" , Class \"${childClassName}\"  By Ui Location on ${persona}", paramsInclude = { "currentDevice,persona,text,childClassName,dir" })
	public void clickTextAndClassByUiLocation() throws Exception {
		if (dir == null) {
			dir = Direction.MIDDLE;
		}
		devicesMannager.getDevice(currentDevice).clickOnSelectorByUi(new Selector().setText(text).setClassName(childClassName), persona, dir);
	}

	/**
	 * The function finds the location of the wanted ui object and click on it
	 * */
	@Test
	@TestProperties(name = "Click Index \"${index}\" , Class \"${selectorClass}\" , Index Of Selector : ${indexOfSelector} By Ui Location on ${persona}", paramsInclude = { "currentDevice,persona,index,selectorClass,dir,indexOfSelector" })
	public void clickIndexAndClassByUiLocation() throws Exception {
		devicesMannager.getDevice(currentDevice).clickOnSelectorByUi(new Selector().setIndex(index).setClassName(selectorClass), persona, dir, indexOfSelector);
	}

	@Test
	@TestProperties(name = "Open the Wi-Fi List on ${persona}", paramsInclude = { "persona,currentDevice" })
	public void openTheWifiList() throws Exception {
		report.report("About to open the wi-fi list");
		devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("home");

		devicesMannager.getDevice(currentDevice).getPersona(persona).openApp("Settings");
		devicesMannager.getDevice(currentDevice).getPersona(persona).waitForExists(new Selector().setText("WIRELESS & NETWORKS"), 10000);

		String id = devicesMannager.getDevice(currentDevice).getPersona(persona)
				.childByInstance(new Selector().setScrollable(true), new Selector().setClassName("android.widget.LinearLayout"), 1);
		devicesMannager.getDevice(currentDevice).clickOnSelectorByUi(id, persona);

		Thread.sleep(1000);
	}

	/**
	 * This function switch the network connection on/off
	 * 
	 * @param wifiNetwoek
	 * */
	@Test
	@TestProperties(name = "Switch network \"${wifiNetwork}\" connection ${onOff} on ${persona}", paramsInclude = { "currentDevice,persona,onOff,wifiNetwork,wifiPassword" })
	public void switchTheNetworkConnection() throws Exception {

		report.report("About to switch the the net work connectivity to : " + onOff + " on : " + wifiNetwork);
		devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("home");

		devicesMannager.getDevice(currentDevice).getPersona(persona).openApp("Settings");
		devicesMannager.getDevice(currentDevice).getPersona(persona).waitForExists(new Selector().setText("WIRELESS & NETWORKS"), 10000);

		String id = devicesMannager.getDevice(currentDevice).getPersona(persona)
				.childByInstance(new Selector().setScrollable(true), new Selector().setClassName("android.widget.LinearLayout"), 1);
		devicesMannager.getDevice(currentDevice).clickOnSelectorByUi(id, persona);
		Thread.sleep(1000);
		if (devicesMannager.getDevice(currentDevice).getPersona(persona).exist(new Selector().setText("Settings"))) {
			report.report("Hey, the bug with the click on the wifi, I'll try one more time");
			devicesMannager.getDevice(currentDevice).clickOnSelectorByUi(id, persona);
		}

		Thread.sleep(1000);
		try {
			id = devicesMannager.getDevice(currentDevice).getPersona(persona)
					.childByText(new Selector().setScrollable(true), new Selector().setClassName("android.widget.LinearLayout"), wifiNetwork, true);
			// if the WiFi is disconnected it takes time to find the requested
			// network
			devicesMannager.getDevice(currentDevice).getPersona(persona).waitForExists(id, 20000);
			devicesMannager.getDevice(currentDevice).clickOnSelectorByUi(id, persona);
		} catch (Exception e) {
			report.report("Couldn't find " + wifiNetwork + Reporter.FAIL);
			return;
		}

		try {
			if (onOff == State.OFF) {
				devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("Forget"));
			} else {
				try {

					if (!((devicesMannager.getDevice(currentDevice).getPersona(persona).exist(new Selector().setText("Connect")))
							&& (devicesMannager.getDevice(currentDevice).getPersona(persona).exist(new Selector().setText("Forget"))) && (devicesMannager
							.getDevice(currentDevice).getPersona(persona).exist(new Selector().setText("Cancel"))))) {

						devicesMannager.getDevice(currentDevice).getPersona(persona)
								.setText(new Selector().setClassName("android.widget.EditText").setIndex(1), wifiPassword);
						devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");
					}

					devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("Connect"));
				} catch (Exception e) {
					devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("Connect"));
				}
			}
		} catch (Exception e) {
			// the phone is in the correct condition
			try {
				devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("Cancel"));
			} catch (Exception e2) {
			}
		}
		devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("home");
		// sleep to get the system properties to update
		sleep(3000);
	}

	/**
	 * Set the text to 'android.widget.EditText'
	 * */
	@Test
	@TestProperties(name = "Set text \"${text}\"  on ${persona}", paramsInclude = { "currentDevice,persona,text,needForClearTheText" })
	public void setText() throws UiObjectNotFoundException {
		if (needForClearTheText) {
			sleep(400);
			devicesMannager.getDevice(currentDevice).getPersona(persona).clearTextField(new Selector().setClassName("android.widget.EditText"));
			sleep(400);
			devicesMannager.getDevice(currentDevice).getPersona(persona).setText(new Selector().setClassName("android.widget.EditText"), "");
			sleep(400);
		}
		devicesMannager.getDevice(currentDevice).getPersona(persona).setText(new Selector().setClassName("android.widget.EditText"), text);
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
		devicesMannager.getDevice(currentDevice).getPersona(persona).waitForExists(new Selector().setDescription("WIRELESS & NETWORKS"), 10000);

		String fatherInstance = devicesMannager.getDevice(currentDevice).getPersona(persona)
				.getUiObject(new Selector().setClassName("android.widget.LinearLayout").setIndex(1));

		try {
			String objectId = devicesMannager.getDevice(currentDevice).getPersona(persona).getChild(fatherInstance, new Selector().setText(onOff.getValue()));
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(objectId);
		} catch (Exception e) {/*
								 * in this case we are on the correct condition
								 * of the wifi connection
								 */
		}

		devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("home");

	}
//<<<<<<< Updated upstream
	
		@Test   //added by Igor 14.01
		@TestProperties(name = "Switch the wifi (LP) : ${onOff} Text on ${persona}", paramsInclude = { "currentDevice,persona,onOff" })
		public void switchTheWiFiLP() throws Exception {
	
			report.report("About to switch the the Wi-Fi to : " + onOff);
	

			
			String cliCommand="monkey -p com.android.settings -c android.intent.category.LAUNCHER 1" ;
			String text = "Events injected: 1" ;
			devicesMannager.getDevice(currentDevice).validateExpressionCliCommand(cliCommand, text, false, persona);
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");
			devicesMannager.getDevice(currentDevice).validateExpressionCliCommand(cliCommand, text, false, persona);

			devicesMannager.getDevice(currentDevice).getPersona(persona).waitForExists(new Selector().setDescription("Wireless & Networks"), 10000);
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(Integer.valueOf(600), Integer.valueOf(500));

		
			
			Selector s = new Selector(); 
			if (onOff == State.ON){
				s.setText("Off");
				devicesMannager.getDevice(currentDevice).getPersona(persona).click(s);
			}
			else{
				s.setText("On");
				devicesMannager.getDevice(currentDevice).getPersona(persona).click(s);
			}
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("home");
		}

	
		  //added by Igor 14.01
//=======
	@Test   //added by Igor 14.01
	@TestProperties(name = "Switch the wifi (LP) : ${onOff} Text on ${persona}", paramsInclude = { "currentDevice,persona,onOff" })
	public void switchTheWiFiLP2() throws Exception {

		report.report("About to switch the the Wi-Fi to : " + onOff);

//		if (onOff == State.ON)
//			onOff = State.OFF;
//		else
//			onOff = State.ON;

		devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("home");
		devicesMannager.getDevice(currentDevice).getPersona(persona).openApp("Settings");
		
		devicesMannager.getDevice(currentDevice).getPersona(persona).waitForExists(new Selector().setDescription("WIRELESS & NETWORKS"), 10000);
		devicesMannager.getDevice(currentDevice).getPersona(persona).waitForExists(new Selector().setDescription("Wireless & Networks"), 10000);
//		String fatherInstance = devicesMannager.getDevice(currentDevice).getPersona(persona)
//				.getUiObject(new Selector().setClassName("android.widget.LinearLayout").setIndex(1));
//		devicesMannager.getDevice(currentDevice).getPersona(persona).click("Wi-Fi");
//		try {
//			String objectId = devicesMannager.getDevice(currentDevice).getPersona(persona)
//					.childByText(new Selector().setScrollable(true), new Selector().setText("Wi-Fi"), "Wi-Fi", true);
//			devicesMannager.getDevice(currentDevice).getPersona(persona).click(objectId);
//		devicesMannager.getDevice(currentDevice).getPersona(persona)
//				.getUiObject(new Selector().setClassName("android.widget.LinearLayout").setIndex(0));
		
					
		try {
		Selector s = new Selector();
		devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("Wi?Fi"));
//		s.setText("Wi-Fi");
//				devicesMannager.getDevice(currentDevice).getPersona(persona).click(s);
	
		
		
		if (onOff == State.ON){
			//devicesMannager.getDevice(currentDevice).getPersona(persona).click("Off");
			s.setText("Off");
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(s);
		}
		else{
			s.setText("On");
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(s);
		}
		}

//		String fatherInstance = devicesMannager.getDevice(currentDevice).getPersona(persona)
//				.getUiObject(new Selector().setClassName("android.widget.LinearLayout").setIndex(1));
		
		 catch (Exception e) {
			//report.report("Could not find UiObject " + e.getMessage(), Reporter.FAIL);
		}

//		try {
//			String objectId = devicesMannager.getDevice(currentDevice).getPersona(persona).getChild(fatherInstance, new Selector().setText(onOff.getValue()));
//			devicesMannager.getDevice(currentDevice).getPersona(persona).click(objectId);
//		} catch (Exception e) {/*
//								 * in this case we are on the correct condition
//								 * of the wifi connection
//								 */
//		}
//		Selector s = new Selector();
//		s.setText(text);
//		try {
//			if (waitForNewWindow) {
//				devicesMannager.getDevice(currentDevice).getPersona(persona).clickAndWaitForNewWindow(s, 10000);
//			} else {
//				devicesMannager.getDevice(currentDevice).getPersona(persona).click(s);
//			}

		devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("home");

	}  //added by Igor 14.01
//>>>>>>> Stashed changes

	/**
	 * This test is a full test from ping dns 1. Open the application 2. For
	 * maximum 3 times : 3. Run the ping test 4. Looks for the test : 0% packet
	 * loss 5. Fail the test wasn't found in the 3 runs, if it found the test
	 * will pass 6. Press home
	 * */
	@Test
	@TestProperties(name = "Ping Dns to : \"${text}\" Text on ${persona}", paramsInclude = { "currentDevice,persona,text,timeout" })
	public void pingDnsTest() throws Exception {
		// TODO to check that this test is working
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

			ObjInfo[] objInfoArray = devicesMannager.getDevice(currentDevice).getPersona(persona)
					.objInfoOfAllInstances(new Selector().setClassName("android.widget.TextView").setIndex(0).setPackageName("com.ulfdittmer.android.ping"));
			textFromTheRun = objInfoArray[2].getText();

			while (!textFromTheRun.contains("% packet loss")) {
				if (System.currentTimeMillis() - start > Integer.valueOf(timeout)) {
					report.report("Could not find UiObject with text " + text + " after " + Integer.valueOf(timeout) / 1000 + " sec.");
				}
				Thread.sleep(interval);
				objInfoArray = devicesMannager
						.getDevice(currentDevice)
						.getPersona(persona)
						.objInfoOfAllInstances(new Selector().setClassName("android.widget.TextView").setIndex(0).setPackageName("com.ulfdittmer.android.ping"));
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
			devicesMannager.getDevice(currentDevice).getPersona(persona)
					.click(new Selector().setClassName("android.widget.ImageButton").setIndex(0).setPackageName("com.ulfdittmer.android.ping"));
		}

		if (passTheTest) {
			devicesMannager.getDevice(currentDevice).getPersona(persona)
					.click(new Selector().setClassName("android.widget.ImageButton").setIndex(0).setPackageName("com.ulfdittmer.android.ping"));
			report.report("Passed the ping & dns test.");
		} else {
			report.report("Couldn't pass after " + numberOfTries + " tries.", Reporter.FAIL);
		}

		devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("home");
	}

	@Test
	@TestProperties(name = "Print all the network data from ${persona}", paramsInclude = { "currentDevice,persona" })
	public void printNetworkData() throws Exception {
		report.startLevel("Click Here for Device Net Configurations (" + persona + ")");
		try {
			report.report("********netcfg********\n" + devicesMannager.getDevice(currentDevice).getPersona(persona).excuteCommand("netcfg"));
			report.report("********ip route********\n" + devicesMannager.getDevice(currentDevice).getPersona(persona).excuteCommand("ip route"));
			report.report("********ip rule********\n" + devicesMannager.getDevice(currentDevice).getPersona(persona).excuteCommand("ip rule"));
			report.report("********ip route list table main********\n"
					+ devicesMannager.getDevice(currentDevice).getPersona(persona).excuteCommand("ip route list table main"));
			report.report("********properties********\n" + ", net.mobile : "
					+ devicesMannager.getDevice(currentDevice).getPersona(persona).getProp("net.mobile") + ", net.wifi : "
					+ devicesMannager.getDevice(currentDevice).getPersona(persona).getProp("net.wifi") + ", network.wifi : "
					+ devicesMannager.getDevice(currentDevice).getPersona(persona).getProp("network.wifi"));
		} catch (Exception e) {
			report.report("Timeout exception fron the server, this is all the data that you will get.");
		}
		report.stopLevel();
	}

	@Test
	@TestProperties(name = "Swipe Down Notification Bar on ${persona}", paramsInclude = { "currentDevice,persona" })
	public void openNotificationBar() throws Exception {

		if (!devicesMannager.getDevice(currentDevice).getPersona(persona).openNotification()) {

			sleep(1000);
			report.report("Error, couldn't swipe down notifications bar - first try.");
			// retry
			if (!devicesMannager.getDevice(currentDevice).getPersona(persona).openNotification()) {
				report.report("Error, couldn't swipe down notifications bar for the seconed try.", Reporter.FAIL);
			}
		}
	}

	@Test
	@TestProperties(name = "Validate UiObject with Description \"${text}\" Exists", paramsInclude = { "currentDevice,persona,text" })
	public void validateUiExistsByDesc() throws Exception {
		isExists = devicesMannager.getDevice(currentDevice).getPersona(persona).exist(new Selector().setDescription(text));
		runProperties.setRunProperty("isExists", String.valueOf(isExists));
	}

	@Test
	@TestProperties(name = "Validate that the uiautomator is up", paramsInclude = { "currentDevice" })
	public void validateUiautomatorUp() throws Exception {
		devicesMannager.getDevice(currentDevice).validateUiautomatorIsUP();
	}

	/**
	 * This test : 1. get the return result from a class 2. validate that the
	 * expectedLine is exist in regex pattern 3. validate that the number is
	 * smaller that the first group of the pattern
	 * */
	@Test
	@TestProperties(name = "Validate Expression is ${size} with Class \"${text}\" than ${expectedLine}", paramsInclude = { "currentDevice,persona,text,index,expectedLine,expectedNumber,size,vellamoResultShow" })
	public void validateExpressionIsSmallerByClass() throws Exception {
		report.report("About to validate expression is " + size.toString() + " than : " + expectedNumber);
		String res = devicesMannager.getDevice(currentDevice).getPersona(persona).getText((new Selector().setClassName(text).setIndex(index)));
		report.report("The return result : " + res);
		Pattern pattern = Pattern.compile(expectedLine);
		Matcher matcher = pattern.matcher(res);

		if (matcher.find()) {
			report.report("Find : " + expectedLine + " in : " + res);
			String number = matcher.group(1);
			boolean isTrue;
			if (size == Size.Smaller) {
				if (Double.valueOf(number) < Double.valueOf(expectedNumber)) {
					report.report("The value " + number + " is Smaller than : " + expectedNumber + " as Excpected");
				} else {
					report.report("The value " + number + " isn't Smaller than " + expectedNumber, Reporter.FAIL);
				}
			}
			if (vellamoResultShow) {
				Summary.getInstance().setProperty("Vellamo_Results", Summary.getInstance().getProperty("Vellamo_Results") + number + "\\");
			}
		} else
			report.report("Couldnt find : " + res + " in : " + res, Reporter.FAIL);
	}

	/**
	 * This test : 1. get the return result from a class of the child from the
	 * father 2. validate that the expectedLine is exist in regex pattern 3.
	 * validate that the number is smaller that the first group of the pattern
	 * */
	@Test
	@TestProperties(name = "Validate Expression is ${size} with Class \"${text}\" than ${expectedLine} from the father", paramsInclude = { "currentDevice,persona,text,index,expectedLine,expectedNumber,fatherClass,fatherIndex,size" })
	public void validateExpressionIsBiggerByClassAndFather() throws Exception {

		Selector father = new Selector().setClassName(fatherClass).setIndex(Integer.valueOf(fatherIndex));

		String fatherInstance = devicesMannager.getDevice(currentDevice).getPersona(persona).getUiObject(father);
		report.report("The father id : " + fatherInstance);

		report.report("bout to validate expression is smaller than : " + expectedNumber);
		String objectId = devicesMannager.getDevice(currentDevice).getPersona(persona)
				.getChild(fatherInstance, new Selector().setClassName(text).setIndex(index));

		String res = devicesMannager.getDevice(currentDevice).getPersona(persona).getText(objectId);
		report.report("The return result : " + res);
		Pattern pattern = Pattern.compile(expectedLine);
		Matcher matcher = pattern.matcher(res);

		if (matcher.find()) {
			report.report("Find : " + expectedLine + " in : " + res);
			String number = matcher.group(1);
			boolean isBigger;
			if (size == Size.Bigger)
				isBigger = Double.valueOf(number) > Double.valueOf(expectedNumber);
			else
				isBigger = Double.valueOf(number) < Double.valueOf(expectedNumber);

			if (isBigger)
				report.report("The value is smaller than : " + res);
			else
				report.report("The value isn't smaller than : " + res, Reporter.FAIL);
		} else
			report.report("Couldnt find : " + expectedLine + " in : " + res, Reporter.FAIL);
	}

	// /**
	// *
	// *
	// * */
	// @Test
	// @TestProperties(name = "",paramsInclude = {"currentDevice,presona"})
	// public void setTheAlarmClockTwoMinutesForward() throws Exception {
	//
	// persona = Persona.PRIV;
	// devicesMannager.getDevice(currentDevice).connectToServers();
	//
	// devicesMannager.getDevice(currentDevice).switchPersona(persona);
	// devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("home");
	//
	// devicesMannager.getDevice(currentDevice).getPersona(persona).openApp("Clock");
	//
	// devicesMannager.getDevice(currentDevice).getPersona(persona).click(new
	// Selector().setDescription("Alarm"));
	//
	// devicesMannager.getDevice(currentDevice).getPersona(persona).click(new
	// Selector().setDescription("Add alarm"));
	//
	// String time = devicesMannager.getDevice(currentDevice).getTheTime();
	//
	// //click on the hour
	// devicesMannager.getDevice(currentDevice).getPersona(persona).click(new
	// Selector().setClassName("android.widget.Button").setIndex(0).setText("12"));
	// //hours
	// if(Integer.valueOf(time.split(":")[0]) > 13) {
	// devicesMannager.getDevice(currentDevice).getPersona(persona).click(new
	// Selector().setText("PM"));
	// }
	//
	// int val = Integer.valueOf(time.split(":")[0])%12;
	// if (val == 0) {
	// val = 12;
	// }
	//
	// devicesMannager.getDevice(currentDevice).getPersona(persona).click(new
	// Selector().setText(String.valueOf(val)));
	//
	// ///click on the min
	// devicesMannager.getDevice(currentDevice).getPersona(persona).click(new
	// Selector().setClassName("android.widget.Button").setIndex(0).setText("00"));
	// //mins
	// devicesMannager.getDevice(currentDevice).getPersona(persona).click(new
	// Selector().setText(String.valueOf((((Integer.valueOf(time.split(":")[1]))%5)+1)*5)));
	//
	// }

	/**
	 * This test : 1. get the return result from a class of the child from the
	 * father 2. validate that the expectedLine is exist in regex pattern 3.
	 * validate that the number is smaller that the first group of the pattern
	 * */
	@Test
	@TestProperties(name = "Validate Expression is ${size} with Class \"${childClass}\" than ${expectedLine} from the father", paramsInclude = { "currentDevice,persona,childClass,childIndex,expectedLine,expectedNumber,fatherClass,fatherIndex,size,grandfatherClass,grandfatherIndex" })
	public void validateExpressionIsBiggerByClassAndFatherAndGrandfather() throws Exception {

		// getting the father id
		Selector grandfather = new Selector().setClassName(grandfatherClass).setIndex(Integer.valueOf(grandfatherIndex));
		String grandfatherInstance = devicesMannager.getDevice(currentDevice).getPersona(persona).getUiObject(grandfather);

		report.report("The grandfather id : " + grandfatherInstance);

		Selector father = new Selector().setClassName(fatherClass).setIndex(Integer.valueOf(fatherIndex));

		String fatherInstance = devicesMannager.getDevice(currentDevice).getPersona(persona).getChild(grandfatherInstance, father);

		// String fatherInstance =
		// devicesMannager.getDevice(currentDevice).getPersona(persona).getUiObject(father);
		report.report("The father id : " + fatherInstance);

		report.report("bout to validate expression is smaller than : " + expectedNumber);
		String objectId = devicesMannager.getDevice(currentDevice).getPersona(persona)
				.getChild(fatherInstance, new Selector().setClassName(childClass).setIndex(Integer.valueOf(childIndex)));

		String res = devicesMannager.getDevice(currentDevice).getPersona(persona).getText(objectId);
		report.report("The return result : " + res);
		Pattern pattern = Pattern.compile(expectedLine);
		Matcher matcher = pattern.matcher(res);

		if (matcher.find()) {
			report.report("Find : " + expectedLine + " in : " + res);
			String number = matcher.group(1);
			switch (size) {
			case Bigger:
				if (Double.valueOf(number) > Double.valueOf(expectedNumber)) {
					report.report("The value " + res + " is Bigger than " + expectedNumber + " as Expected");
				} else {
					report.report("The value " + res + " isn't bigger than " + expectedNumber, Reporter.FAIL);
				}
				break;
			case Smaller:
				if (Double.valueOf(number) < Double.valueOf(expectedNumber)) {
					report.report("The value " + res + " is Smaller than " + expectedNumber + " as Expected");
				} else {
					report.report("The value " + res + " isn't Smaller than " + expectedNumber, Reporter.FAIL);
				}
				break;
			case Equals:
				if (Double.valueOf(number).equals(Double.valueOf(expectedNumber))) {
					report.report("The value " + res + " is Equals to " + expectedNumber + " as Expected");
				} else {
					report.report("The value " + res + " isn't Equals to " + expectedNumber, Reporter.FAIL);
				}
				break;

			}
		} else
			report.report("Couldnt find : " + expectedLine + " in : " + res, Reporter.FAIL);
	}

	@Test
	@TestProperties(name = "Start Logs of Test", paramsInclude = { "currentDevice" })
	public void startLogging() throws Exception {
		devicesMannager.getDevice(currentDevice).initLogs();
	}

	@Test
	@TestProperties(name = "Sleep for ${timeout} milliseconeds", paramsInclude = { "currentDevice,timeout" })
	public void sleep() throws Exception {
		sleep(Integer.valueOf(timeout));
	}

	@Test
	@TestProperties(name = "Set Device as Root", paramsInclude = { "currentDevice" })
	public void setDeviceAsRoot() throws Exception {
		devicesMannager.getDevice(currentDevice).setDeviceAsRoot();
	}

	/**
	 * switch persona enter the password and enter validate that the screen is
	 * on
	 * */
	@Test
	@TestProperties(name = "Enter Password for ${persona}", paramsInclude = { "currentDevice,persona,value" })
	public void enterPassword() throws Exception {
		// TODO to check the text contains with the value
		devicesMannager.getDevice(currentDevice).switchPersona(persona);
		devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("1"));
		devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("1"));
		devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("1"));
		devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("1"));
		devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setDescription("Enter"));
		devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("home");
		devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");
		if (!devicesMannager.getDevice(currentDevice).getPersona(persona).exist(new Selector().setDescription("Apps"))) {
			report.report("The screen is off.", Reporter.FAIL);
		}

	}

	@Test
	@TestProperties(name = "Swipe And Login for ${persona}", paramsInclude = { "currentDevice,persona" })
	public void swipeAndLogin() throws Exception {
		// wake up
		try {
			devicesMannager.getDevice(currentDevice).getPersona(devicesMannager.getDevice(currentDevice).getForegroundPersona()).wakeUp();
		} catch (Exception e) {/* do nothing */
		}
		// switch persona
		devicesMannager.getDevice(currentDevice).switchPersona(persona);
		// unlock by swipe
		devicesMannager.getDevice(currentDevice).unlockBySwipe(persona);
	}
	
	@Test
	@TestProperties(name = "Secondary Swipe And Login for ${persona}", paramsInclude = { "currentDevice,persona" }) //added by Igor 13.01
	public void swipeAndLoginSecondary() throws Exception {
		// wake up
		try {
			devicesMannager.getDevice(currentDevice).getPersona(devicesMannager.getDevice(currentDevice).getForegroundPersona()).wakeUp();
		} catch (Exception e) {/* do nothing */
		}
		// switch persona
		devicesMannager.getDevice(currentDevice).switchPersona(persona);
		// unlock by swipe
		devicesMannager.getDevice(currentDevice).unlockBySwipeSecondary(persona);
	}                                                                                                                        //added by Igor 13.01

	/**
	 * This function scroll to the wanted text to find and click on it. For
	 * example you can find allot of use inside of the settings
	 * */
	@Test
	@TestProperties(name = "Click On \"${text}\" On : ${persona}", paramsInclude = { "currentDevice,persona,text" })
	public void clickOnScrollable() throws Exception {
		String id = devicesMannager.getDevice(currentDevice).getPersona(persona)
				.childByText(new Selector().setScrollable(true), new Selector().setText(text), text, true);
		devicesMannager.getDevice(currentDevice).getPersona(persona).click(id);
	}

	@Test
	@TestProperties(name = "Factory Data Reset", paramsInclude = { "currentDevice,persona,deviceEncrypted,deviceEncryptedPriv" })
	public void factoryDataReset() throws Exception {

		// devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("home");
		report.report("Factory Data Reset");
		devicesMannager.getDevice(currentDevice).getPersona(persona).openApp("Settings");
		String id = devicesMannager.getDevice(currentDevice).getPersona(persona)
				.childByText(new Selector().setScrollable(true), new Selector().setText("Backup & reset"), "Backup & reset", true);
		devicesMannager.getDevice(currentDevice).getPersona(persona).click(id);
		devicesMannager.getDevice(currentDevice).getPersona(persona).waitForExists(new Selector().setText("Factory data reset"), 10 * 1000);
		devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("Factory data reset"));
		devicesMannager.getDevice(currentDevice).getPersona(persona).waitForExists(new Selector().setText("Reset phone"), 10 * 1000);
		if (devicesMannager.getDevice(currentDevice).getPersona(persona).exist(new Selector().setText("Reset phone"))) {
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("Reset phone"));
		} else {
			if (devicesMannager.getDevice(currentDevice).getPersona(persona).waitForExists(new Selector().setText("Reset tablet"), 10 * 1000)) {
				devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("Reset tablet"));
			}
		}

		try {
			devicesMannager.getDevice(currentDevice).getPersona(persona).waitForExists(new Selector().setText("Confirm your PIN"), 10 * 1000);
			boolean pin = devicesMannager.getDevice(currentDevice).getPersona(persona).exist(new Selector().setText("Confirm your PIN"));
			if (pin) {
				report.report("Entering the pin for reset.");
				devicesMannager.getDevice(currentDevice).getPersona(persona).setText(new Selector().setClassName("android.widget.EditText"), "1111");
				devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("Next"));
			}
		} catch (Exception e) {
			report.report("No pin insertion.");
		}

		report.report("Erase everything");
		devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("Erase everything"));
		sleep(2000);
		devicesMannager.getDevice(currentDevice).validateDeviceIsOnline(deviceEncrypted, deviceEncryptedPriv, Persona.PRIV, Persona.CORP);
		devicesMannager.getDevice(currentDevice).setDataAfterReboot();
	}

	/**
	 * this function is passing the email password in the first time wizard if
	 * exist.
	 * */
	@Test
	@TestProperties(name = "Try To Enter User Password First Time Wizard : ${user} , ${password}", paramsInclude = { "currentDevice,persona,user,password" })
	public void tryToEnterEmailPasswordFirstTimeWizard() throws Exception {
		try {
			timeout = "20000";
			final long start = System.currentTimeMillis();
			while (!devicesMannager.getDevice(currentDevice).getPersona(persona).exist(new Selector().setText("Yes"))) {
				if (System.currentTimeMillis() - start > Integer.valueOf(timeout)) {
					report.report("Couldn't find the text 'Yes'.");
					return;
				}
				Thread.sleep(1500);
			}
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("Yes"));
			devicesMannager.getDevice(currentDevice).getPersona(persona).setText(new Selector().setText("Email"), user);
			devicesMannager.getDevice(currentDevice).getPersona(persona).setText(new Selector().setClassName("android.widget.EditText").setIndex(1), password);
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setClassName("android.widget.Button").setIndex(2));
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("OK"));
		} catch (Exception e) {

		}
	}

	@Test
	@TestProperties(name = "Try To Enter this Phone belongs to First Time Wizard : ${uaer} , ${password}", paramsInclude = { "currentDevice,persona,user,password" })
	public void tryToEnterThisPhoneBelongsToFirstTimeWizard() throws Exception {
		try {
			timeout = "20000";
			final long start = System.currentTimeMillis();
			while (!devicesMannager.getDevice(currentDevice).getPersona(persona).exist(new Selector().setText("This phone belongs to..."))) {
				if (System.currentTimeMillis() - start > Integer.valueOf(timeout)) {
					report.report("Couldn't find the text 'This phone belongs to...'.");
					return;
				}
				Thread.sleep(1500);
			}

			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setDescription("Next"));
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setDescription("Next"));

		} catch (Exception e) {
		}
	}

	/**
	 * The function get a command and do it in priv and corp, after it the test
	 * will verify that there is the same regular expression in both of the
	 * personas and check the the wanted groups (etc (\s*),(\d+:\d+),(\S*),(\w*)
	 * and much more) inside of the expression is equal.
	 * */
	@Test
	@TestProperties(name = "Compare Results Corp And Priv of ${cliCommand} with the expression : \"${expectedLine}\"", paramsInclude = { "currentDevice,cliCommand,expectedLine" })
	public void compareResultsCorpAndPriv() throws Exception {
		devicesMannager.getDevice(currentDevice).compareResultsCorpAndPriv(cliCommand, expectedLine);
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
//	@Test //added by Igor 27.01
//	@TestProperties(name = "Wait For \"${expectedLine}\" in LOGMUX", paramsInclude = { "currentDevice,expectedLine,timeout,interval" })
//	public void waitforLineInLogcatLP() throws Exception {
//		devicesMannager.getDevice(currentDevice).waitForLineInTomcat2(expectedLine, Integer.valueOf(timeout), interval);
//	}

	@Test
	@TestProperties(name = "delete file ${remotefileLocation} from Device", paramsInclude = { "currentDevice,remotefileLocation" })
	public void deleteFile() throws Exception {
		devicesMannager.getDevice(currentDevice).deleteFile(remotefileLocation);
	}

	@Test
	@TestProperties(name = "Clean Device DB after Mock Tucki", paramsInclude = { "currentDevice" })
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
	 * This test is validating the data after bringing the data itself For
	 * Example: <br>
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
		devicesMannager.getDevice(currentDevice).getLogsOfRun(logParser, true, true);
	}

	/**
	 * find the expression to find after cli command in the adb shell. Can be
	 * done with regular expression and with regular string. This command
	 * include adb before!!!!!
	 * 
	 * @param cliCommand
	 *            - the wanted cli command after the adb shell
	 * @param expression
	 *            - the wanted text/expression to find
	 * @param RegularExpression
	 *            - is this expression is a regular expression
	 * */
	@Test
	@TestProperties(name = "Validate  expression in the cli command : \"${cliCommand}\" , with the text : \"${text}\" , for ${numberOfTimes} tries", paramsInclude = { "currentDevice,cliCommand,text,regularExpression,numberOfTimes" })
	public void validateExpressionCliCommand() throws Exception {

		devicesMannager.getDevice(currentDevice).validateExpressionCliCommand(cliCommand, text, regularExpression, numberOfTimes);
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
	@Test //Val
	@TestProperties(name = "Validate  expression in the cli command : \"${cliCommand}\" , with the text : \"${text}\" , with persona : ${persona}", paramsInclude = { "currentDevice,cliCommand,text,regularExpression,persona" })
	public void validateExpressionCliCommandPersona() throws Exception {
		devicesMannager.getDevice(currentDevice).validateExpressionCliCommand(cliCommand, text, regularExpression, persona);
	}

	/**
	 * For validating more than one string use the fill the text with ;;
	 * seperator example text = "a;;b;;c"
	 * */
	@Test
	@TestProperties(name = "Validate  expression in the cli cell command : \"${cliCommand}\" , with the text : \"${text}\" , with persona : ${persona}", paramsInclude = { "currentDevice,cliCommand,text,regularExpression,persona,numberOfTimes" })
	public void validateExpressionCliCommandCellPersona() throws Exception {
		devicesMannager.getDevice(currentDevice).validateExpressionCliCommandCell(cliCommand, regularExpression, persona, numberOfTimes, text.split(";;"));
	}

	@Test
	@TestProperties(name = "download the application: \"${appFullPath}\"", paramsInclude = { "currentDevice,appFullPath" })
	public void pushApplication() throws Exception {
		devicesMannager.getDevice(currentDevice).pushApplication(appFullPath, "/data/containers/corp/data/app/");
		devicesMannager.getDevice(currentDevice).pushApplication(appFullPath, "/data/containers/priv/data/app/");
	}

	/**
	 * This test install the wanted application from the Play store persona -
	 * the wanted persona to work over it text - the name for the click appName
	 * - the name for the search
	 * */
	@Test
	@TestProperties(name = "install the application: \"${text}\" from google Store, on persona : ${persona}", paramsInclude = { "currentDevice,appName,text,persona" })
	public void installApplication() throws Exception {

		try {

			report.startLevel("Install the application: " + text);
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("home");
			devicesMannager.getDevice(currentDevice).getPersona(persona).openApp("Play Store");
			long start = System.currentTimeMillis();
			while (!devicesMannager.getDevice(currentDevice).getPersona(persona).exist(new Selector().setText("EDITORS' CHOICE"))) {
				if (System.currentTimeMillis() - start > Integer.valueOf(10 * 1000)) {
					report.report("Could not find UiObject with text EDITORS' CHOICE after " + Integer.valueOf(10 * 1000) / 1000 + " sec.");
					break;
				}
				Thread.sleep(1500);
			}
			if (devicesMannager.getDevice(currentDevice).getPersona(persona).exist(new Selector().setText("Accept"))) {
				devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("Accept"));
			}
			try {
				devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setDescription("Search Google Play"));
				devicesMannager.getDevice(currentDevice).getPersona(persona).setText(new Selector().setTextContains("Search Google Play"), appName);
				devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("enter");
				devicesMannager.getDevice(currentDevice).getPersona(persona).waitForExists(new Selector().setTextContains(text), 10 * 1000);
				devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setTextContains(text));
			} catch (Exception e) {
				report.report("First time of open the app store didn't succeed, about to try for the seconed time.");
				closeAppStoreAndOpenFromBegining(currentDevice, persona, appName, text);
			}
			// check if the application already exist

			boolean isAppExist = false;
			try {
				isAppExist = devicesMannager.getDevice(currentDevice).getPersona(persona).waitForExists(new Selector().setText("UNINSTALL"), 2 * 1000);
			} catch (Exception e) {/* nothing really the application not exist */
			}
			if (isAppExist) {
				devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");
				devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");
				devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("home");
				devicesMannager.getDevice(currentDevice).getPersona(persona).openApp(text);
			}

			else {
				devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("INSTALL"));
				devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("ACCEPT"));

				start = System.currentTimeMillis();
				while (!devicesMannager.getDevice(currentDevice).getPersona(persona).exist(new Selector().setText("UNINSTALL"))) {
					if (System.currentTimeMillis() - start > Integer.valueOf(60 * 1000)) {
						report.report("Could not find UiObject with text UNINSTALL after " + Integer.valueOf(10 * 1000) / 1000 + " sec.", Reporter.FAIL);
						break;
					}
					Thread.sleep(1500);
				}
				devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("OPEN"));
			}
		} catch (Exception e) {
			report.report("Error" + e.getMessage(), Reporter.FAIL);
		} finally {
			report.stopLevel();
		}

	}
	
	@Test //play    //added by Igor 04.02 
	@TestProperties(name = "install the application in LP: \"${text}\" from google Store, on persona : ${persona}", paramsInclude = { "currentDevice,appName,text,persona" })
	public void installApplicationLP() throws Exception {

		try {

			report.startLevel("Install the application: " + text);
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("home");
			
			//devicesMannager.getDevice(currentDevice).getPersona(persona).openApp("Play Store");
			
			String cliCommand ="monkey -p com.android.vending -c android.intent.category.LAUNCHER 1";
			String text = "Events injected: 1" ;
			devicesMannager.getDevice(currentDevice).validateExpressionCliCommand(cliCommand, text, false, persona);
			
			long start = System.currentTimeMillis();
			while (!devicesMannager.getDevice(currentDevice).getPersona(persona).exist(new Selector().setText("EDITORS' CHOICE"))) {
				if (System.currentTimeMillis() - start > Integer.valueOf(10 * 1000)) {
					report.report("Could not find UiObject with text EDITORS' CHOICE after " + Integer.valueOf(10 * 1000) / 1000 + " sec.");
					break;
				}
				Thread.sleep(1500);
			}
			if (devicesMannager.getDevice(currentDevice).getPersona(persona).exist(new Selector().setText("Accept"))) {
				devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("Accept"));
			}
			try {
				devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setDescription("Search Google Play"));
				devicesMannager.getDevice(currentDevice).getPersona(persona).setText(new Selector().setTextContains("Search Google Play"), appName);
				devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("enter");
//				devicesMannager.getDevice(currentDevice).getPersona(persona).waitForExists(new Selector().setTextContains(text), 10 * 1000);
//				devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setTextContains(text));
				devicesMannager.getDevice(currentDevice).getPersona(persona).waitForExists(new Selector().setDescription(text), 10 * 1000);
	     		devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setDescription(text));
				
				//attempt to use clickByTextContains
//				Selector s = new Selector();
//				s.setTextContains(text);
//				try {
//					if (waitForNewWindow) {
//						devicesMannager.getDevice(currentDevice).getPersona(persona).clickAndWaitForNewWindow(s, 10000);
//					} else {
//						devicesMannager.getDevice(currentDevice).getPersona(persona).click(s);
//					}
//				} catch (Exception e) {
//					report.report("Could not find UiObject " + e.getMessage(), Reporter.FAIL);
//				}
				
				//attempt to click by Desc
				
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
			} catch (Exception e) {
				report.report("First time of open the app store didn't succeed, about to try for the seconed time.");
				closeAppStoreAndOpenFromBegining(currentDevice, persona, appName, text);
			}
			// check if the application already exist

			boolean isAppExist = false;
			try {
				isAppExist = devicesMannager.getDevice(currentDevice).getPersona(persona).waitForExists(new Selector().setText("UNINSTALL"), 2 * 1000);
			} catch (Exception e) {/* nothing really the application not exist */
			}
			if (isAppExist) {
				devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");
				devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");
				devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("home");
				devicesMannager.getDevice(currentDevice).getPersona(persona).openApp(text);
			}

			else {
				devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("INSTALL"));
				devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("ACCEPT"));

				start = System.currentTimeMillis();
				while (!devicesMannager.getDevice(currentDevice).getPersona(persona).exist(new Selector().setText("UNINSTALL"))) {
					if (System.currentTimeMillis() - start > Integer.valueOf(60 * 1000)) {
						report.report("Could not find UiObject with text UNINSTALL after " + Integer.valueOf(10 * 1000) / 1000 + " sec.", Reporter.FAIL);
						break;
					}
					Thread.sleep(1500);
				}
				devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("OPEN"));
			}
		} catch (Exception e) {
			report.report("Error" + e.getMessage(), Reporter.FAIL);
		} finally {
			report.stopLevel();
		}

	}
	
	@Test //play    //added by Igor 16.03 
	@TestProperties(name = "find the application in Play in LP: \"${appName}\" from google Store, on persona : ${persona}", paramsInclude = { "currentDevice,appName,text,persona" })
	public void FindApplicationLP() throws Exception {

		try {

			report.startLevel("Install the application: " + text);
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("home");
			
			//devicesMannager.getDevice(currentDevice).getPersona(persona).openApp("Play Store");
			
			String cliCommand ="monkey -p com.android.vending -c android.intent.category.LAUNCHER 1";
			String text = "Events injected: 1" ;
			devicesMannager.getDevice(currentDevice).validateExpressionCliCommand(cliCommand, text, false, persona);
			
			long start = System.currentTimeMillis();
			while (!devicesMannager.getDevice(currentDevice).getPersona(persona).exist(new Selector().setText("EDITORS' CHOICE"))) {
				if (System.currentTimeMillis() - start > Integer.valueOf(10 * 1000)) {
					report.report("Could not find UiObject with text EDITORS' CHOICE after " + Integer.valueOf(10 * 1000) / 1000 + " sec.");
					break;
				}
				Thread.sleep(1500);
			}
			if (devicesMannager.getDevice(currentDevice).getPersona(persona).exist(new Selector().setText("Accept"))) {
				devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("Accept"));
			}
			try {
				devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setDescription("Search Google Play"));
				devicesMannager.getDevice(currentDevice).getPersona(persona).setText(new Selector().setTextContains("Search Google Play"), appName);
				devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("enter");
			
			} finally {
				report.stopLevel();
			}
			}
		finally {
			report.stopLevel();
		}
			
		}
	


	/**
	 * This function was written to reciver from situations of fallings of the
	 * app store
	 * 
	 * @throws Exception
	 * */
	private void closeAppStoreAndOpenFromBegining(DeviceNumber currentDevice, Persona persona, String appName, String text) throws Exception {

		devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("home");

		closeAllApplications();

		devicesMannager.getDevice(currentDevice).getPersona(persona).openApp("Play Store");
		long start = System.currentTimeMillis();
		while (!devicesMannager.getDevice(currentDevice).getPersona(persona).exist(new Selector().setText("EDITORS' CHOICE"))) {
			if (System.currentTimeMillis() - start > Integer.valueOf(10 * 1000)) {
				report.report("Could not find UiObject with text EDITORS' CHOICE after " + Integer.valueOf(10 * 1000) / 1000 + " sec.");
				break;
			}
			Thread.sleep(1500);
		}
		if (devicesMannager.getDevice(currentDevice).getPersona(persona).exist(new Selector().setText("Accept"))) {
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("Accept"));
		}
		try {
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setDescription("Search Google Play"));
			devicesMannager.getDevice(currentDevice).getPersona(persona).setText(new Selector().setTextContains("Search Google Play"), appName);
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("enter");
			devicesMannager.getDevice(currentDevice).getPersona(persona).waitForExists(new Selector().setTextContains(text), 10 * 1000);
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setTextContains(text));
		} catch (Exception e) {
			report.report("Tried to open the play store for the seconed time and still didn't get it", Reporter.FAIL);
		}

	}

	/**
	 * This test uninstall the wanted application from the Play store persona -
	 * the wanted persona to work over it text - the name for the click appName
	 * - the name for the search
	 * */
	@Test
	@TestProperties(name = "uninstall the application: \"${text}\" from google Store, on persona : ${persona}", paramsInclude = { "currentDevice,appName,text,persona" })
	public void uninstallApplication() throws Exception {
		try {
			report.startLevel("Uninstall the application: " + text);
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
					report.report("Could not find UiObject with text EDITORS' CHOICE after " + Integer.valueOf(10 * 1000) / 1000 + " sec.");
					break;
				}
				Thread.sleep(1500);
			}
			try {
				devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setDescription("Search Google Play"));
				devicesMannager.getDevice(currentDevice).getPersona(persona).setText(new Selector().setTextContains("Search Google Play"), appName);
				devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("enter");
				devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setTextContains(text));
			} catch (Exception e) {
				report.report("First time of open the app store didn't succeed, about to try for the seconed time.");
				closeAppStoreAndOpenFromBegining(currentDevice, persona, appName, text);
			}
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("UNINSTALL"));
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("OK"));

			start = System.currentTimeMillis();
			while (!devicesMannager.getDevice(currentDevice).getPersona(persona).exist(new Selector().setText("INSTALL"))) {
				if (System.currentTimeMillis() - start > Integer.valueOf(60 * 1000)) {
					report.report("Could not find UiObject with text UNINSTALL after " + Integer.valueOf(10 * 1000) / 1000 + " sec.", Reporter.FAIL);
					break;
				}
				Thread.sleep(1500);
			}

			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("home");
		} catch (Exception e) {
			report.report("Error" + e.getMessage(), Reporter.FAIL);
		} finally {
			report.stopLevel();
		}
	}

	/**
	 * This test is making a phone call to the wanted phone number.
	 * */
	@Test
	@TestProperties(name = "Call to : \"${phoneNumber}\" : ${persona}", paramsInclude = { "currentDevice,phoneNumber,persona" })
	public void callToAnotherPhone() throws Exception {
		try {
			report.startLevel("Calling to : " + phoneNumber);
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("Phone"));
			devicesMannager.getDevice(currentDevice).getPersona(persona)
					.click(new Selector().setClassName("android.widget.ImageButton").setPackageName("com.android.dialer").setIndex(1));
			devicesMannager.getDevice(currentDevice).getPersona(persona).setText(new Selector().setClassName("android.widget.EditText"), phoneNumber);
			// call
			report.report("Dailing...");
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setDescription("dial"));
			sleep(11000);
			// hangup
			report.report("hangup.");
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setDescription("End"));
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");
		} finally {
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("home");
			report.stopLevel();
		}

	}

	/**
	 * This test is making a phone call to the wanted phone number and answer.
	 * 
	 * */
	@Test
	@TestProperties(name = "Call to : \"${phoneNumber}\" : ${persona} and answer, the caller : ${currentDevice} .", paramsInclude = { "currentDevice,phoneNumber,persona" })
	public void callToAnotherPhoneAndAnswer() throws Exception {
		try {

			try {
				devicesMannager.getDevice(currentDevice).getPersona(persona).wakeUp();
			} catch (Exception e) {
			}
			devicesMannager.getDevice(DeviceNumber.PRIMARY).switchPersona(persona);
			devicesMannager.getDevice(DeviceNumber.PRIMARY).getPersona(persona).pressKey("home");
			devicesMannager.getDevice(DeviceNumber.SECONDARY).switchPersona(persona);
			devicesMannager.getDevice(DeviceNumber.SECONDARY).getPersona(persona).pressKey("home");

			report.startLevel("Calling to : " + phoneNumber);
			devicesMannager.getDevice(currentDevice).getPersona(persona).openApp("Phone");
			Thread.sleep(400);
			devicesMannager.getDevice(currentDevice).getPersona(persona)
					.click(new Selector().setClassName("android.widget.ImageButton").setPackageName("com.android.dialer").setIndex(1));
			Thread.sleep(400);
			devicesMannager.getDevice(currentDevice).getPersona(persona).setText(new Selector().setClassName("android.widget.EditText"), phoneNumber);
			// call
			report.report("Dailing...");
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setDescription("dial"));

			DeviceNumber secDevice;
			if (currentDevice == DeviceNumber.SECONDARY) {
				secDevice = DeviceNumber.PRIMARY;
			} else {
				secDevice = DeviceNumber.SECONDARY;
			}
			report.report("Wait for incoming call.");
			if (devicesMannager.getDevice(secDevice).getPersona(persona).waitForExists(new Selector().setText("Incoming call"), 60 * 1000)) {
				devicesMannager.getDevice(secDevice).getPersona(persona).pressKeyCode(5);
				Thread.sleep(7000);
			} else {
				report.report("There was no incoming call on " + devicesMannager.getDevice(secDevice).getDeviceSerial() + " (" + secDevice + ")", report.FAIL);
			}
			report.report("hangup");
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setDescription("End"));
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("home");

		} finally {
			report.stopLevel();
		}

	}


	
	@Test //added by Igor 29.01
	@TestProperties(name = "Make a call KK2LP : \"${phoneNumber}\" : ${persona} and answer, the caller is KK .", paramsInclude = { "phoneNumber,persona" })
	public void KK2LP() throws Exception {
		try {

			try {
				devicesMannager.getDevice(currentDevice).getPersona(persona).wakeUp();
			} catch (Exception e) {
			}
			DeviceNumber secDevice;
			currentDevice = DeviceNumber.SECONDARY;			
    		secDevice = DeviceNumber.PRIMARY;
			devicesMannager.getDevice(DeviceNumber.PRIMARY).switchPersona(persona);
			devicesMannager.getDevice(DeviceNumber.PRIMARY).getPersona(persona).pressKey("home");
			devicesMannager.getDevice(DeviceNumber.SECONDARY).switchPersona(persona);
			devicesMannager.getDevice(DeviceNumber.SECONDARY).getPersona(persona).pressKey("home");

			report.startLevel("Calling to : " + phoneNumber);
			devicesMannager.getDevice(currentDevice).getPersona(persona).openApp("Phone");
			Thread.sleep(400);
			devicesMannager.getDevice(currentDevice).getPersona(persona)
					.click(new Selector().setClassName("android.widget.ImageButton").setPackageName("com.android.dialer").setIndex(1));
			Thread.sleep(400);
			devicesMannager.getDevice(currentDevice).getPersona(persona).setText(new Selector().setClassName("android.widget.EditText"), phoneNumber);
			// call
			report.report("Dailing...");
//			devicesMannager.getDevice(currentDevice).getPersona(Persona.PRIV).excuteCommand("input keyevent 26"); //power button
//     		devicesMannager.getDevice(currentDevice).getPersona(Persona.CORP).excuteCommand("input keyevent 26"); //power button
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setDescription("dial"));
			//devicesMannager.getDevice(currentDevice).validateExpressionCliCommand(cliCommand, text, regularExpression, persona); 
			report.report("Wait for incoming call.");

		//	devicesMannager.getDevice(currentDevice).validateExpressionCliCommand("logmux -T 500", "RINGING", false, 4);
				Thread.sleep(12000);
			//	report.report("BEFORE CLICK");
				for(int i=0;i<8;i++){
				devicesMannager.getDevice(secDevice).getPersona(persona).click(Integer.valueOf(570), Integer.valueOf(86));
				
				Thread.sleep(500);
				devicesMannager.getDevice(secDevice).getPersona(persona).click(Integer.valueOf(550), Integer.valueOf(100));
				
				Thread.sleep(500);
				devicesMannager.getDevice(secDevice).getPersona(persona).click(Integer.valueOf(550), Integer.valueOf(100));
				
				}
				
			   if (devicesMannager.getDevice(secDevice).getPersona(persona).waitForExists(new Selector().setText("Incoming call"), 60 * 1000)) {
				   devicesMannager.getDevice(secDevice).getPersona(persona).pressKeyCode(5);
				   Thread.sleep(5000);
			 } 
			
			else {
				report.report("There was no incoming call on " + devicesMannager.getDevice(secDevice).getDeviceSerial() + " (" + secDevice + ")", report.FAIL);
			}
			report.report("hangup");
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setDescription("End"));
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("home");

		} finally {
			report.stopLevel();
		}

	}

	
	@Test //added by Igor 29.01
	@TestProperties(name = "Make a call LP2KK : \"${phoneNumber}\" : ${persona} and answer, the caller LP .", paramsInclude = { "phoneNumber,persona" })
	public void LP2KK() throws Exception {
		try {

			try {
				devicesMannager.getDevice(currentDevice).getPersona(persona).wakeUp();
			} catch (Exception e) {
			}
			DeviceNumber secDevice;
			currentDevice = DeviceNumber.PRIMARY;			
    		secDevice = DeviceNumber.SECONDARY;
			devicesMannager.getDevice(DeviceNumber.PRIMARY).switchPersona(persona);
			devicesMannager.getDevice(DeviceNumber.PRIMARY).getPersona(persona).pressKey("home");
			devicesMannager.getDevice(DeviceNumber.SECONDARY).switchPersona(persona);
			devicesMannager.getDevice(DeviceNumber.SECONDARY).getPersona(persona).pressKey("home");

			report.startLevel("Calling to : " + phoneNumber);
//			devicesMannager.getDevice(currentDevice).getPersona(persona).openApp("Phone");
//			Thread.sleep(400);
			//com.android.dialer
			//com.android.service.telecom
			//com.android.service.phone
			String cliCommand="monkey -p com.android.dialer -c android.intent.category.LAUNCHER 1" ;
			String text = "Events injected: 1" ;
			devicesMannager.getDevice(currentDevice).validateExpressionCliCommand(cliCommand, text, false, persona);
		//	devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setClassName("android.widget.ImageButton").setPackageName("com.android.dialer").setIndex(1));  //working
			Thread.sleep(400);
			//devicesMannager.getDevice(currentDevice).getPersona(persona).setText(new Selector().setClassName("android.widget.EditText"), phoneNumber);
			// call
			report.report("Dailing...");

			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setDescription("dial pad"));
			devicesMannager.getDevice(currentDevice).getPersona(persona).setText(new Selector().setClassName("android.widget.EditText"), phoneNumber);
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setDescription("dial"));
			//devicesMannager.getDevice(currentDevice).validateExpressionCliCommand(cliCommand, text, regularExpression, persona); 
			report.report("Wait for incoming call.");
			


			   if (devicesMannager.getDevice(secDevice).getPersona(persona).waitForExists(new Selector().setText("Incoming call"), 60 * 1000)) {
				   devicesMannager.getDevice(secDevice).getPersona(persona).pressKeyCode(5);
				   report.report("pressed answer");
				   Thread.sleep(7000);
			 } 
			
			else {
				report.report("There was no incoming call on " + devicesMannager.getDevice(secDevice).getDeviceSerial() + " (" + secDevice + ")", report.FAIL);
			}
			report.report("hangup");
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setDescription("End"));
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("home");

		} finally {
			report.stopLevel();
		}

	}
	/**
	 * This test is validate the missed call from the wanted number and clear
	 * the call log. Please insert the number including "-".
	 * */
	
	// : ${persona}
	@Test //added by Igor 29.01  //Will later add a uni test with platform definition	
	@TestProperties(name = "Make a call KK2LP Uni : \"${phoneNumber}\"  and answer,on ${persona} using Nexus :\"${platformNew}\" the caller is KK .", paramsInclude = { "phoneNumber,persona,platformNew" })
	public void KK2LP_Uni() throws Exception {
		try {

			try {
				devicesMannager.getDevice(currentDevice).getPersona(persona).wakeUp();
			} catch (Exception e) {
			}
			DeviceNumber secDevice;
			currentDevice = DeviceNumber.SECONDARY;			
    		secDevice = DeviceNumber.PRIMARY;
			devicesMannager.getDevice(DeviceNumber.PRIMARY).switchPersona(persona);
			devicesMannager.getDevice(DeviceNumber.PRIMARY).getPersona(persona).pressKey("home");
			devicesMannager.getDevice(DeviceNumber.SECONDARY).switchPersona(persona);
			devicesMannager.getDevice(DeviceNumber.SECONDARY).getPersona(persona).pressKey("home");

			report.startLevel("Calling to : " + phoneNumber);
			devicesMannager.getDevice(currentDevice).getPersona(persona).openApp("Phone");
			Thread.sleep(400);
			devicesMannager.getDevice(currentDevice).getPersona(persona)
					.click(new Selector().setClassName("android.widget.ImageButton").setPackageName("com.android.dialer").setIndex(1));
			Thread.sleep(400);
			devicesMannager.getDevice(currentDevice).getPersona(persona).setText(new Selector().setClassName("android.widget.EditText"), phoneNumber);
		
			report.report("Dailing...");

			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setDescription("dial"));

			report.report("Wait for incoming call.");

		
				Thread.sleep(12000);
			report.report("BEFORE CONDITION");
				if(platformNew==5){
					
				for(int i=0;i<8;i++){
					report.report("Nexus 5");
				devicesMannager.getDevice(secDevice).getPersona(persona).click(Integer.valueOf(570), Integer.valueOf(86));
				
				Thread.sleep(500);
				devicesMannager.getDevice(secDevice).getPersona(persona).click(Integer.valueOf(550), Integer.valueOf(100));
				
				Thread.sleep(500);
				devicesMannager.getDevice(secDevice).getPersona(persona).click(Integer.valueOf(550), Integer.valueOf(100));
				}
				}
				
				else if(platformNew==6) {
					
					for(int i=0;i<8;i++){
						report.report("Nexus 6");
					devicesMannager.getDevice(secDevice).getPersona(persona).click(Integer.valueOf(570), Integer.valueOf(150));
					
					Thread.sleep(500);
					devicesMannager.getDevice(secDevice).getPersona(persona).click(Integer.valueOf(600), Integer.valueOf(160));
					
					Thread.sleep(500);
					devicesMannager.getDevice(secDevice).getPersona(persona).click(Integer.valueOf(660), Integer.valueOf(170));
					}
				}
				
				
//				for(int i=0;i<8;i++){
//					devicesMannager.getDevice(secDevice).getPersona(persona).click(Integer.valueOf(570), Integer.valueOf(150));
//					
//					Thread.sleep(500);
//					devicesMannager.getDevice(secDevice).getPersona(persona).click(Integer.valueOf(600), Integer.valueOf(160));
//					
//					Thread.sleep(500);
//					devicesMannager.getDevice(secDevice).getPersona(persona).click(Integer.valueOf(660), Integer.valueOf(170));
//					}
				
			   if (devicesMannager.getDevice(secDevice).getPersona(persona).waitForExists(new Selector().setText("Incoming call"), 60 * 1000)) {
				   devicesMannager.getDevice(secDevice).getPersona(persona).pressKeyCode(5);
				   Thread.sleep(5000);
			 } 
			
			else {
				report.report("There was no incoming call on " + devicesMannager.getDevice(secDevice).getDeviceSerial() + " (" + secDevice + ")", report.FAIL);
			}
			report.report("hangup");
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setDescription("End"));
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("home");

		} finally {
			report.stopLevel();
		}

	}

	
	@Test //added by Igor 29.01
	@TestProperties(name = "Make a call LP2KK Shamu : \"${phoneNumber}\" : ${persona} and answer, the caller LP .", paramsInclude = { "phoneNumber,persona" })
	public void LP2KK_shamu() throws Exception {
		try {

			try {
				devicesMannager.getDevice(currentDevice).getPersona(persona).wakeUp();
			} catch (Exception e) {
			}
			DeviceNumber secDevice;
			currentDevice = DeviceNumber.PRIMARY;			
    		secDevice = DeviceNumber.SECONDARY;
			devicesMannager.getDevice(DeviceNumber.PRIMARY).switchPersona(persona);
			devicesMannager.getDevice(DeviceNumber.PRIMARY).getPersona(persona).pressKey("home");
			devicesMannager.getDevice(DeviceNumber.SECONDARY).switchPersona(persona);
			devicesMannager.getDevice(DeviceNumber.SECONDARY).getPersona(persona).pressKey("home");

			report.startLevel("Calling to : " + phoneNumber);
			//devicesMannager.getDevice(currentDevice).getPersona(persona).openApp("Phone");
			Thread.sleep(400);
			//com.android.dialer
			//com.android.service.telecom
			//com.android.service.phone
			String cliCommand="monkey -p com.android.dialer -c android.intent.category.LAUNCHER 1" ;
			String text = "Events injected: 1" ;
			devicesMannager.getDevice(currentDevice).validateExpressionCliCommand(cliCommand, text, false, persona);
		//	devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setClassName("android.widget.ImageButton").setPackageName("com.android.dialer").setIndex(1));  //working
			Thread.sleep(400);
			//devicesMannager.getDevice(currentDevice).getPersona(persona).setText(new Selector().setClassName("android.widget.EditText"), phoneNumber);
			// call
			report.report("Dailing...");

			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setDescription("dial pad"));
			devicesMannager.getDevice(currentDevice).getPersona(persona).setText(new Selector().setClassName("android.widget.EditText"), phoneNumber);
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setDescription("dial"));
			//devicesMannager.getDevice(currentDevice).validateExpressionCliCommand(cliCommand, text, regularExpression, persona); 
			report.report("Wait for incoming call.");
			


			   if (devicesMannager.getDevice(secDevice).getPersona(persona).waitForExists(new Selector().setText("Incoming call"), 60 * 1000)) {
				   devicesMannager.getDevice(secDevice).getPersona(persona).pressKeyCode(5);
				   report.report("pressed answer");
				   Thread.sleep(7000);
			 } 
			
			else {
				report.report("There was no incoming call on " + devicesMannager.getDevice(secDevice).getDeviceSerial() + " (" + secDevice + ")", report.FAIL);
			}
			report.report("hangup");
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setDescription("End"));
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("home");

		} finally {
			report.stopLevel();
		}

	}
	
	
	@Test //added by Igor 04.02
	@TestProperties(name = "Open application by Package Name in  :${PackageName} : ${persona}  .", paramsInclude = { "PackageName,persona" })
	public void openAppByPackageName() throws Exception {
		try {

			try {
				
				currentDevice = DeviceNumber.PRIMARY;			
	    		
				devicesMannager.getDevice(DeviceNumber.PRIMARY).switchPersona(persona);
				devicesMannager.getDevice(DeviceNumber.PRIMARY).getPersona(persona).pressKey("home");
				
				report.startLevel("Oppening" + PackageName);
				
				Thread.sleep(400);
				String cliCommand="monkey -p " + packageName +" -c android.intent.category.LAUNCHER 1" ;
				String text = "Events injected: 1" ;
				devicesMannager.getDevice(currentDevice).validateExpressionCliCommand(cliCommand, text, false, persona);
				
			} catch (Exception e) {
				report.report("Oppening" + PackageName + "failed");
			}
			



			report.report("Wait for incoming call.");
			
		}finally {
				report.stopLevel();
			}

		
	}
	/**
	 * This test is validate the missed call from the wanted number and clear
	 * the call log. Please insert the number including "-".
	 * */
	@Test
	@TestProperties(name = "Validate missed call from : \"${phoneNumber}\" : ${persona}", paramsInclude = { "currentDevice,phoneNumber,persona" })
	public void validateMissedCall() throws Exception {

		// phoneNumber = "052-542-7444";
		try {
			report.startLevel("Validate income call from : " + phoneNumber);
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("Phone"));
			devicesMannager.getDevice(currentDevice).getPersona(persona)
					.click(new Selector().setClassName("android.widget.ImageButton").setPackageName("com.android.dialer").setIndex(0));

			devicesMannager.getDevice(currentDevice).clickOnSelectorByUi(new Selector().setText("Missed"), persona);

			if (!devicesMannager.getDevice(currentDevice).getPersona(persona).exist(new Selector().setText(phoneNumber))) {
				report.report("Couldn't find the incoming call from : " + phoneNumber, Reporter.FAIL);
			} else {
				report.report("Find the incoming call from : " + phoneNumber);
			}

			// clear call log
			String fatherInstance = devicesMannager.getDevice(currentDevice).getPersona(persona)
					.getUiObject(new Selector().setClassName("android.view.View").setIndex(0));
			report.report("The father id : " + fatherInstance);
			String objectId = devicesMannager.getDevice(currentDevice).getPersona(persona)
					.getChild(fatherInstance, new Selector().setClassName("android.widget.ImageButton").setIndex(0));
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(objectId);
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("Clear call log"));
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("OK"));

			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");

		} finally {
			report.stopLevel();
		}

	}
	
	
	
	/**
	 * This test is validate the missed call from the wanted number and clear
	 * the call log. Please insert the number including "-".
	 * */
	@Test
	@TestProperties(name = "Validate missed calls only from : \"${phoneNumber}\" : ${persona}", paramsInclude = { "currentDevice,phoneNumber,persona" })//changed by Igor
	public void validateMissedCallOnly() throws Exception {

		// phoneNumber = "052-542-7444";
		try {
			report.startLevel("Validate income call from : " + phoneNumber);
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("Phone"));
			devicesMannager.getDevice(currentDevice).getPersona(persona)
					.click(new Selector().setClassName("android.widget.ImageButton").setPackageName("com.android.dialer").setIndex(0));

			devicesMannager.getDevice(currentDevice).clickOnSelectorByUi(new Selector().setText("Missed"), persona);

			if (!devicesMannager.getDevice(currentDevice).getPersona(persona).exist(new Selector().setText(phoneNumber))) {
				report.report("Couldn't find the incoming call from : " + phoneNumber, Reporter.FAIL);
			} else {
				report.report("Find the incoming call from : " + phoneNumber);
			}

			// clear call log
			/*String fatherInstance = devicesMannager.getDevice(currentDevice).getPersona(persona)
					.getUiObject(new Selector().setClassName("android.view.View").setIndex(0));
			report.report("The father id : " + fatherInstance);
			String objectId = devicesMannager.getDevice(currentDevice).getPersona(persona)
					.getChild(fatherInstance, new Selector().setClassName("android.widget.ImageButton").setIndex(0));
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(objectId);
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("Clear call log"));
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("OK"));

			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");*/

		} finally {
			report.stopLevel();
		}

	}
	
	
	/**
	 * This test is validate the missed call from the wanted number and clear
	 * the call log. Please insert the number including "-".
	 * */
	@Test
	@TestProperties(name = "Remove only : \"${phoneNumber}\" : ${persona}", paramsInclude = { "currentDevice,phoneNumber,persona" }) //changed by Igor
	public void removeMissedCall() throws Exception {

		// phoneNumber = "052-542-7444";
		try {
			report.startLevel("Validate income call from : " + phoneNumber);
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("Phone"));
			devicesMannager.getDevice(currentDevice).getPersona(persona)
					.click(new Selector().setClassName("android.widget.ImageButton").setPackageName("com.android.dialer").setIndex(0));

			devicesMannager.getDevice(currentDevice).clickOnSelectorByUi(new Selector().setText("Missed"), persona);

			/*if (!devicesMannager.getDevice(currentDevice).getPersona(persona).exist(new Selector().setText(phoneNumber))) {
				report.report("Couldn't find the incoming call from : " + phoneNumber, Reporter.FAIL);
			} else {
				report.report("Find the incoming call from : " + phoneNumber);
			}*/

			// clear call log
			String fatherInstance = devicesMannager.getDevice(currentDevice).getPersona(persona)
					.getUiObject(new Selector().setClassName("android.view.View").setIndex(0));
			report.report("The father id : " + fatherInstance);
			String objectId = devicesMannager.getDevice(currentDevice).getPersona(persona)
					.getChild(fatherInstance, new Selector().setClassName("android.widget.ImageButton").setIndex(0));
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(objectId);
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("Clear call log"));
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("OK"));

			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");

		} finally {
			report.stopLevel();
		}

	}

	/**
	 * The test send sms to the wanted number.
	 * */
	@Test
	@TestProperties(name = "Send sms to : \"${phoneNumber}\" : ${persona}", paramsInclude = { "currentDevice,phoneNumber,persona,messageContent" })
	public void sendSms() throws Exception {

		try {
			report.startLevel("Send sms to : " + phoneNumber);
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("Messaging"));
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setDescription("New message"));
			devicesMannager.getDevice(currentDevice).getPersona(persona).setText(new Selector().setText("To"), phoneNumber);
			devicesMannager.getDevice(currentDevice).getPersona(persona).setText(new Selector().setText("Type message"), messageContent);
			// sending the sms
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setDescription("Send"));
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");
		} finally {
			report.stopLevel();
		}
	}

	/**
	 * The test validate that the sms recived and delete the message. Please
	 * insert the number including "-".
	 * */
	@Test
	@TestProperties(name = "Validate sms recieved from : \"${phoneNumber}\" : ${persona}", paramsInclude = { "currentDevice,phoneNumber,persona" })
	public void validateSmsRecieved() throws Exception {

		try {
			report.startLevel("Validate recieved sms from : " + phoneNumber);
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("Messaging"));
			devicesMannager.getDevice(currentDevice).clickOnSelectorByUi(new Selector().setTextContains(phoneNumber), persona);

			if (!devicesMannager.getDevice(currentDevice).getPersona(persona)
					.exist(new Selector().setClassName("android.widget.QuickContactBadge").setIndex(0))) {
				report.report("Couldn't find the recieved sms from : " + phoneNumber, Reporter.FAIL);
			} else {
				report.report("Find the recieved sms from : " + phoneNumber);
			}
			// delete this thread
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setClassName("android.widget.ImageButton").setIndex(2));
			devicesMannager.getDevice(currentDevice).clickOnSelectorByUi(new Selector().setText("Delete thread"), persona);
			devicesMannager.getDevice(currentDevice).getPersona(persona).click(new Selector().setText("Delete"));

			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("back");

		} finally {
			report.stopLevel();
		}
	}

	/**
	 * The test is set the device upTime
	 * */
	@Test
	@TestProperties(name = "Get Up Time", paramsInclude = { "" })
	public void getUptime() throws Exception {
		for (CellRoxDevice device : devicesMannager.getCellroxDevicesList()) {
			device.setUpTime(device.getCurrentUpTime());
		}
	}

	/**
	 * Set the up time and the processes id
	 * */
	@Test
	@TestProperties(name = "Init All Validation Crashes Data", paramsInclude = { "" })
	public void initAllValidationCrashesData() throws Exception {
		for (CellRoxDevice device : devicesMannager.getCellroxDevicesList()) {
			device.initAllTheCrashesValidationData();
		}
	}

	/**
	 * The test validate that the device have 2 personas.
	 * */
	@Test
	@TestProperties(name = "Validate doa", paramsInclude = { "currentDevice,deviceEncrypted,deviceEncryptedPriv" })
	public void validateDoa() throws Exception {
		try {
			devicesMannager.getDevice(currentDevice).validateDoaCrash();

		} catch (Exception e) {

			sleep(20 * 1000);
			report.report("Device : " + devicesMannager.getDevice(currentDevice).getDeviceSerial());
			// last_kmsg
			devicesMannager.getDevice(currentDevice).printLastKmsg();
			devicesMannager.getDevice(currentDevice).rebootDevice(deviceEncrypted, deviceEncryptedPriv, Persona.PRIV, Persona.CORP);
			report.report("There is an error, the device is offline or had unwanted reboot. Going to reboot.");
			// sleep
			devicesMannager.getDevice(currentDevice).validateDeviceIsOnline(System.currentTimeMillis(), 5 * 60 * 1000, deviceEncrypted, deviceEncryptedPriv,
					Persona.PRIV, Persona.CORP);
			// configure
			devicesMannager.getDevice(currentDevice).configureDeviceForAutomation(true);
			// connect
			devicesMannager.getDevice(currentDevice).connectToServers();
			// to wake up and type password
			devicesMannager.getDevice(currentDevice).getPersona(Persona.CORP).wakeUp();
			devicesMannager.getDevice(currentDevice).switchPersona(Persona.CORP);
			devicesMannager.getDevice(currentDevice).getPersona(Persona.CORP).click(new Selector().setText("1"));
			devicesMannager.getDevice(currentDevice).getPersona(Persona.CORP).click(new Selector().setText("1"));
			devicesMannager.getDevice(currentDevice).getPersona(Persona.CORP).click(new Selector().setText("1"));
			devicesMannager.getDevice(currentDevice).getPersona(Persona.CORP).click(new Selector().setText("1"));
			devicesMannager.getDevice(currentDevice).getPersona(Persona.CORP).click(new Selector().setDescription("Enter"));
			devicesMannager.getDevice(currentDevice).getPersona(Persona.PRIV).wakeUp();
			devicesMannager.getDevice(currentDevice).switchPersona(Persona.PRIV);
			devicesMannager.getDevice(currentDevice).unlockBySwipe(Persona.PRIV);
			try {
				devicesMannager.getDevice(currentDevice).validateDoaCrash();
			} catch (Exception e1) {
				doaCrach++;
				Summary.getInstance().setProperty("Doa_Crash", String.valueOf(doaCrach));
				report.report("Doa Crash", Reporter.FAIL);
			}
		}

	}

	/**
	 * Check all the cj=heckboxes.
	 * */
	@Test
	@TestProperties(name = "Check or Unchek All CheckBoxes ${onOff}", paramsInclude = { "currentDevice,persona,onOff" })
	public void checkUnchekAllCheckBoxes() throws Exception {
		devicesMannager.getDevice(currentDevice).checkUncheckAllCheckBoxes(persona, onOff);
	}

	private void closeAllApplicationsFunction() throws Exception {
		try {
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("home");
		} catch (Exception e) {
		}
		Thread.sleep(500);
		try {
			devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("recent");
		} catch (Exception e) {
			try {
				devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("recent");
			} catch (Exception e1) {
				report.report("Recent apps button wasn't opened.", Reporter.FAIL);
			}
		}
		Thread.sleep(800);
		for (int i = 0; i < 20; i++) {
			try {
				if (devicesMannager.getDevice(currentDevice).getPersona(persona).exist(new Selector().setDescription("Apps"))) {
					break;
				}
				devicesMannager.getDevice(currentDevice).getPersona(persona)
						.swipe(new Selector().setClassName("android.widget.RelativeLayout"), Direction.LEFT.getDir(), 20);
				Thread.sleep(300);
			} catch (Exception e) {
				try {
					devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("home");
					break;
				} catch (Exception e1) {
					break;
				}
			}
		}
		devicesMannager.getDevice(currentDevice).getPersona(persona).pressKey("home");
	}

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

	public String getAppFullPath() {
		return appFullPath;
	}

	public void setAppFullPath(String appFullPath) {
		this.appFullPath = appFullPath;
	}

	public State getOnOff() {
		return onOff;
	}

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

	public String getY() {
		return y;
	}

	public void setY(String y) {
		this.y = y;
	}

	public String getX() {
		return x;
	}

	public void setX(String x) {
		this.x = x;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getExpectedNumber() {
		return expectedNumber;
	}

	public void setExpectedNumber(String expectedNumber) {
		this.expectedNumber = expectedNumber;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public boolean isDeviceEncrypted() {
		return deviceEncrypted;
	}

	public void setDeviceEncrypted(boolean isDeviceEncrypted) {
		this.deviceEncrypted = isDeviceEncrypted;
	}

	public String getApplyUpdateLocation() {
		return applyUpdateLocation;
	}

	public void setApplyUpdateLocation(String applyUpdateLocation) {
		this.applyUpdateLocation = applyUpdateLocation;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}

	public Size getSize() {
		return size;
	}

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

	public int getNumberOfTimes() {
		return numberOfTimes;
	}

	public void setNumberOfTimes(int numberOfTimes) {
		this.numberOfTimes = numberOfTimes;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isVellamoResultShow() {
		return vellamoResultShow;
	}

	public void setVellamoResultShow(boolean vellamoResultShow) {
		this.vellamoResultShow = vellamoResultShow;
	}

	public boolean getNeedForClearTheText() {
		return needForClearTheText;
	}

	public void setNeedForClearTheText(boolean needForClearTheText) {
		this.needForClearTheText = needForClearTheText;
	}

	public String getSelectorClass() {
		return selectorClass;
	}

	public void setSelectorClass(String selectorClass) {
		this.selectorClass = selectorClass;
	}

	public int getIndexOfSelector() {
		return indexOfSelector;
	}

	public void setIndexOfSelector(int indexOfSelector) {
		this.indexOfSelector = indexOfSelector;
	}

	public String getGrandfatherClass() {
		return grandfatherClass;
	}

	public void setGrandfatherClass(String grandfatherClass) {
		this.grandfatherClass = grandfatherClass;
	}

	public String getGrandfatherIndex() {
		return grandfatherIndex;
	}

	public void setGrandfatherIndex(String grandfatherIndex) {
		this.grandfatherIndex = grandfatherIndex;
	}

	public void setScreenStatus(boolean screenStatus) {
		this.screenStatus = screenStatus;
	}

	public boolean isScreenStatus() {
		return screenStatus;
	}

	public boolean isElementAttributeStatus() {
		return elementAttributeStatus;
	}

	public void setElementAttributeStatus(boolean elementAttributeStatus) {
		this.elementAttributeStatus = elementAttributeStatus;
	}

	public ElementAttributes getElementAttributes() {
		return elementAttributes;
	}

	public void setElementAttributes(ElementAttributes elementAttributes) {
		this.elementAttributes = elementAttributes;
	}

	public int getExpectedNumber2() {
		return expectedNumber2;
	}

	public void setExpectedNumber2(int expectedNumber2) {
		this.expectedNumber2 = expectedNumber2;
	}
	public int getPlatformNew() { //added by Igor 09.02
		return platformNew;
	}
	public void setPlatformNew(int platformNew) {  //added by Igor 09.02
		this.platformNew = platformNew;
	}
	public String getLocation() { //added by Igor 04.03
		return Location;
	}
	public void setLocation(String Location) {  //added by Igor 04.03
		this.Location = Location;
	
	}

}
