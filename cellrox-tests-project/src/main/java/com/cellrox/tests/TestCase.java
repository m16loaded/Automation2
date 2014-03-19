package com.cellrox.tests;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import jsystem.framework.TestProperties;
import jsystem.framework.report.Reporter;
import jsystem.framework.report.Summary;
import junit.framework.SystemTestCase4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.topq.uiautomator.Selector;

import com.cellrox.infra.CellRoxDevice;
import com.cellrox.infra.CellRoxDeviceManager;
import com.cellrox.infra.WebDriverSO;
import com.cellrox.infra.ImageFlowReporter.ImageFlowHtmlReport;
import com.cellrox.infra.enums.DeviceNumber;
import com.cellrox.infra.enums.Persona;
import com.cellrox.infra.log.LogParser;
import com.cellrox.infra.object.LogParserExpression;

public class TestCase extends SystemTestCase4 {
	
	protected boolean devicesReadyForAutomation = true;
	protected CellRoxDeviceManager devicesMannager;    
	protected Persona persona;
	protected File localLocation;
	protected String remotefileLocation;
	protected static int doaCrach = 0, personaCrash = 0 , deviceCrash = 0, connectionCrash = 0;
	protected boolean deviceEncrypted = true;
	protected DeviceNumber currentDevice = DeviceNumber.PRIMARY;
	protected ImageFlowHtmlReport imageFlowHtmlReport;
	protected WebDriver driver;
	protected WebDriverSO driverSo;
	
	@Before
	public void init() throws Exception {
		try {
			report.startLevel("Before");
			imageFlowHtmlReport = new ImageFlowHtmlReport();
			
			if(!devicesReadyForAutomation){
				report.report("Error the device isn't ready for the automation.",Reporter.FAIL);
				throw new Exception("The device isn't ready for the automation.");
			}
			devicesMannager = (CellRoxDeviceManager) system.getSystemObject("devicesMannager");
			
			report.report("Finish the initing of the before test.");
		}
		finally {
			initTheWebDriver();
			report.stopLevel();
		}
		// devicesMannager.getDevice(currentDevice).configureDeviceForAutomation(true);
		// devicesMannager.getDevice(currentDevice).connectToServers();
	}
	
	
//	public void takeScreenShot(String fileName , DeviceNumber deviceNumber, Persona persona, String nameInTheReport) throws Exception {
//		String localLocation = report.getCurrentTestFolder()+ File.separatorChar + fileName;
//		String screenshoot = devicesMannager.getDevice(deviceNumber).getPersona(persona).takeScreenshot(fileName, (float)1.0, 100);
//		devicesMannager.getDevice(deviceNumber).pullFileFromDevice(persona.getTmpLib()+ fileName, localLocation);
//		imageFlowHtmlReport.addTitledImage(nameInTheReport, new File(localLocation));
//	}
//	
//	public void takeScreenShotAndReport(String fileName , DeviceNumber deviceNumber, Persona persona, String nameInTheReport) throws Exception {
//
//		takeScreenShot(fileName, deviceNumber, persona, nameInTheReport);
//		report.report("screen flow", imageFlowHtmlReport.getHtmlReport(), Reporter.PASS, false, true, false, false);
//	}
	
	
	
	
	@After
	public void tearDown() throws Exception {
		try {

			report.startLevel("After");

			
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			Summary.getInstance().setProperty("End_Time", sdf.format(cal.getTime()));
			Summary.getInstance().setProperty("No_Connection", String.valueOf(connectionCrash));
			Summary.getInstance().setProperty("Doa_Crash", String.valueOf(doaCrach));
			Summary.getInstance().setProperty("Device_Crash", String.valueOf(deviceCrash));
			Summary.getInstance().setProperty("Persona_Crash", String.valueOf(personaCrash));
			if (!isPass()) {
//				try{
//					takeScreenShot("FailPriv.jpg", DeviceNumber.PRIMARY, persona.PRIV, "Error - priv screenshot");
//				}
//				catch(Exception e){ }
//				try{
//					takeScreenShot("FailCorp.jpg", DeviceNumber.PRIMARY, persona.CORP, "Error - corp screenshot");
//				}
//				catch(Exception e){ }
				
				report.report("screen flow", imageFlowHtmlReport.getHtmlReport(), Reporter.PASS, false, true, false, false);
				
				if(devicesMannager.getRunStatus().equals("full")) {
					validateDeviceStatus();
				}
			}
			//TODO to remove if i dont find the use
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss");
//			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
//			Calendar cal = Calendar.getInstance();
//			Summary.getInstance().setProperty("End_Time", sdf.format(cal.getTime()));
//			Summary.getInstance().setProperty("No_Connection", String.valueOf(connectionCrash));
//			Summary.getInstance().setProperty("Doa_Crash", String.valueOf(doaCrach));
//			Summary.getInstance().setProperty("Device_Crash", String.valueOf(deviceCrash));
//			Summary.getInstance().setProperty("Persona_Crash", String.valueOf(personaCrash));
		}
		finally {
			report.stopLevel();
		}
	}
	
	
	/**
	 * This function runs only in case of fails in the tests and check if
	 * unexpected reboot happened or persona crash or DOA
	 * */
	private void validateDeviceStatus() throws Exception {

		for (CellRoxDevice device : devicesMannager.getCellroxDevicesList()) {

			boolean crashHappened = false;
			//Step 1 is to check for doa crash
			try {
				device.validateConnectivity();
			}
			catch (Exception e) {
				report.report("Out of connection ", Reporter.FAIL);
				connectionCrash++;
				return;
			}
			
			//Step 2 is to check for device crash by the upTime
			long knownUpTime = device.getUpTime();
			if(knownUpTime > device.getCurrentUpTime()) {
				crashHappened = true;
				report.report("Device_Crash", Reporter.FAIL);
				deviceCrash++;
				sleep(20 * 1000);
				report.report("Device : " + device.getDeviceSerial());
				// last_kmsg
				device.printLastKmsg();
				//here im doning all the thing beside the reboot
				device.validateDeviceIsOnline(System.currentTimeMillis(), 5*60*1000, deviceEncrypted, Persona.PRIV, Persona.CORP);
				device.setDeviceAsRoot();
				device.setUpTime(device.getCurrentUpTime());
                device.setPsString(device.getPs());
			}
			
			//Step 3 is to check for personas crash
			String str2 = device.getPs();
			if(!crashHappened) {
				if(!device.isPsDiff(device.getPsString(), str2)) {
					crashHappened = true;
					report.report("Persona_Crash", Reporter.FAIL);
					personaCrash++;
					sleep(20 * 1000);
					report.report("Device : " + device.getDeviceSerial());
					// last_kmsg
					report.report("About to print the last kmsg.");
					device.printLastKmsg();
					
					//this is the check which persona crashed
					Map<Persona,Integer> mapPerPrOld = new HashMap<Persona, Integer>();
					mapPerPrOld = device.getPersonaProcessIdMap();
					
					Map<Persona,Integer> mapPerPrNew = new HashMap<Persona, Integer>();
					mapPerPrNew = device.getPersonaProcessIdMap();
					
					if(!mapPerPrNew.get(Persona.PRIV).equals(mapPerPrOld.get(Persona.PRIV))) {
						report.report("Error, persona Priv crashed.",Reporter.FAIL);
					}
					if(!mapPerPrNew.get(Persona.CORP).equals(mapPerPrOld.get(Persona.CORP))) {
						report.report("Error, persona CORP crashed.",Reporter.FAIL);
					}
					
					device.rebootDevice(deviceEncrypted, Persona.PRIV, Persona.CORP);
				}
				
			}
			
			//taking care in a cases of persona crash
			if(crashHappened) {
				//check the syslogs
				stopSysLogAndValidateInDevice();
				
				report.report("There is an error, the device is offline or had unwanted reboot. Going to reboot.");
				// sleep
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

		}
	}
	
	/**
	 * This function is validating the data after bringing the data itself from the syslogs files
	 * For Example: <br>
	 * - oops\\|kernel panic\\|soft lockup<br>
	 * - out_of_memory<br>
	 * - Timed out waiting for /dev/.coldboot_done<br>
	 * - EGL_BAD_ALLOC<br>
	 * - persona\\.\\*died\\|cellroxservice\\.\\*died\\|FATAL EXCEPTION<br>
	 * - AudioFlinger\\.\\*buffer overflow<br>
	 * - STATE_CRASH_RESET<br>
	 */
	public void stopSysLogAndValidateInDevice() throws Exception {
		LogParserExpression[] expressions = new LogParserExpression[14];
		
		LogParserExpression expression = new LogParserExpression();
		expression.setExpression("kernel_panic");
		expression.setNiceName("kernel_panic");
		expressions[0] = expression;
		
		expression = new LogParserExpression();
		expression.setExpression("oops");
		expression.setNiceName("oops");
		expressions[1] = expression;
		
		expression = new LogParserExpression();
		expression.setExpression("soft lockup");
		expression.setNiceName("soft lockup");
		expressions[2] = expression;
		
		expression = new LogParserExpression();
		expression.setExpression("out_of_memory");
		expression.setNiceName("out_of_memory");
		expressions[3] = expression;
		
		expression = new LogParserExpression();
		expression.setExpression("Timed out waiting for");
		expression.setNiceName("Timed out waiting for");
		expressions[4] = expression;
		
		expression = new LogParserExpression();
		expression.setExpression(".coldboot_done");
		expression.setNiceName(".coldboot_done");
		expressions[5] = expression;
		
		expression = new LogParserExpression();
		expression.setExpression("EGL_BAD_ALLOC");
		expression.setNiceName("EGL_BAD_ALLOC");
		expressions[6] = expression;
		
		expression = new LogParserExpression();
		expression.setExpression("persona died");
		expression.setNiceName("persona died");
		expressions[7] = expression;
		
		expression = new LogParserExpression();
		expression.setExpression("FATAL EXCEPTION");
		expression.setNiceName("FATAL EXCEPTION");
		expressions[8] = expression;
		
		expression = new LogParserExpression();
		expression.setExpression("AudioFlinger\\.\\*buffer overflow");
		expression.setNiceName("AudioFlinger\\.\\*buffer overflow");
		expressions[9] = expression;
		
		expression = new LogParserExpression();
		expression.setExpression("STATE_CRASH_RESET");
		expression.setNiceName("STATE_CRASH_RESET");
		expressions[10] = expression;
		
		expression = new LogParserExpression();
		expression.setExpression("FATAL_EXEPTION");
		expression.setNiceName("FATAL_EXEPTION");
		expressions[11] = expression;
		
		expression = new LogParserExpression();
		expression.setExpression("FATAL EXEPTION");
		expression.setNiceName("FATAL EXEPTION");
		expressions[12] = expression;
		
		expression = new LogParserExpression();
		expression.setExpression("fatal exception");
		expression.setNiceName("fatal exception");
		expressions[13] = expression;
		
		LogParser logParser = new LogParser(expressions);
		devicesMannager.getDevice(currentDevice).getLogsOfRun(logParser);
	}
	

	public void initTheWebDriver() throws Exception{
		
		driverSo = (WebDriverSO) system.getSystemObject("webDriver");
    	driver= driverSo.getDriver();
//    	System.setProperty("webdriver.chrome.driver","/home/topq/dev/chromedriver");
//    	driver = new ChromeDriver();
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

	public CellRoxDeviceManager getDevicesMannager() {
		return devicesMannager;
	}

	public void setDevicesMannager(CellRoxDeviceManager devicesMannager) {
		this.devicesMannager = devicesMannager;
	}

	public int getDoaCrach() {
		return doaCrach;
	}

	public void setDoaCrach(int doaCrach) {
		this.doaCrach = doaCrach;
	}

	public int getPersonaCrash() {
		return personaCrash;
	}

	public void setPersonaCrash(int personaCrash) {
		this.personaCrash = personaCrash;
	}

	public int getDeviceCrash() {
		return deviceCrash;
	}

	public void setDeviceCrash(int deviceCrash) {
		this.deviceCrash = deviceCrash;
	}

	public int getConnectionCrash() {
		return connectionCrash;
	}

	public void setConnectionCrash(int connectionCrash) {
		this.connectionCrash = connectionCrash;
	}

	public boolean isDeviceEncrypted() {
		return deviceEncrypted;
	}

	public void setDeviceEncrypted(boolean deviceEncrypted) {
		this.deviceEncrypted = deviceEncrypted;
	}

	public DeviceNumber getCurrentDevice() {
		return currentDevice;
	}

	public void setCurrentDevice(DeviceNumber currentDevice) {
		this.currentDevice = currentDevice;
	}
	
	public WebDriver getDriver() {
		return driver;
	}

	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}
	

}
