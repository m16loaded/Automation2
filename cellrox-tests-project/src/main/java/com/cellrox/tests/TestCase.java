package com.cellrox.tests;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import jsystem.extensions.analyzers.text.FindText;
import jsystem.framework.RunProperties;
import jsystem.framework.TestProperties;
import jsystem.framework.report.Reporter;
import jsystem.framework.report.Summary;
import jsystem.framework.report.Reporter.EnumReportLevel;
import junit.framework.SystemTestCase4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.python.modules.re;
import org.topq.uiautomator.Selector;

import com.cellrox.infra.CellRoxDevice;
import com.cellrox.infra.CellRoxDeviceManager;
import com.cellrox.infra.StaticUtils;
import com.cellrox.infra.WebDriverSO;
import com.cellrox.infra.ImageFlowReporter.ImageFlowHtmlReport;
import com.cellrox.infra.enums.Color;
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
	protected static int doaCrach = 0, personaCrash = 0, deviceCrash = 0, connectionCrash = 0;
	protected boolean deviceEncrypted = true;
	protected boolean deviceEncryptedPriv = false;
	protected DeviceNumber currentDevice = DeviceNumber.PRIMARY;
	protected ImageFlowHtmlReport imageFlowHtmlReport;
	protected WebDriver driver;
	protected WebDriverSO driverSo;

	@Before
	public void init() throws Exception {
		try {
			report.startLevel("Before");
			imageFlowHtmlReport = new ImageFlowHtmlReport();

			if (!devicesReadyForAutomation) {
				report.report("Error the device isn't ready for the automation.", Reporter.FAIL);
				throw new Exception("The device isn't ready for the automation.");
			}
			devicesMannager = (CellRoxDeviceManager) system.getSystemObject("devicesMannager");

			report.report("Finish the initing of the before test.");
		} finally {
			initTheWebDriver();
			report.stopLevel();
		}
	}

	@After
	public void tearDown() throws Exception {
		try {
			// get Oren's magic code
			getPrintkMessage();
			getUnexpectedErrorScreenshot();
			report.startLevel("After");
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			Summary.getInstance().setProperty("End_Time", sdf.format(cal.getTime()));
			Summary.getInstance().setProperty("No_Connection", String.valueOf(connectionCrash));
			Summary.getInstance().setProperty("Doa_Crash", String.valueOf(doaCrach));
			Summary.getInstance().setProperty("Device_Crash", String.valueOf(deviceCrash));
			Summary.getInstance().setProperty("Persona_Crash", String.valueOf(personaCrash));
			if (!isPass()) {
				
				// report.report("screen flow",
				// imageFlowHtmlReport.getHtmlReport(), Reporter.PASS, false,
				// true, false, false);

				if (devicesMannager.getRunStatus().equals("full")) {
					validateDeviceStatus();
				}
			}
		} finally {
			report.stopLevel();
		}
	}

	private void getUnexpectedErrorScreenshot() throws Exception {
		for (CellRoxDevice device : devicesMannager.getCellroxDevicesList()) {
			try {
				getScreenShot(device, Persona.PRIV, Persona.CORP);
			} catch (Exception e) {
				// do nothing
			}
		}

	}

	private void getScreenShot(CellRoxDevice device, Persona... personas) throws Exception {
		for (Persona persona : personas) {
			// get the screenshot from device and save it to current test's
			// folder
			device.pullFileFromDevice("/data/containers/" + persona + "/data/local/tmp/wathcer.png", report.getCurrentTestFolder() + "/" + persona.toString()
					+ "_unexpectedError.png");
			// add a reporter link
			report.addLink("Click Here for Unexpected Error Window Screenshot for Persona " + persona+" Device Serial = "+device.getDeviceSerial(), persona.toString() + "_unexpectedError.png");
			// delete screenshot from device for later use
			device.deleteFile("/data/containers/" + persona + "/data/local/tmp/wathcer.png");
			
		}
	}
	
	private void getPrintkMessage() throws Exception{
		report.startLevel("Validate PrintK");
		String printk = devicesMannager.getDevice(DeviceNumber.PRIMARY).executeHostShellCommand("cat /proc/sys/kernel/printk");
		report.report(printk);
		FindText findText = new FindText("(\\d*)\\s*4\\s*1\\s*7", true,false,2);
		findText.setTestAgainst(printk);
		findText.analyze();
		//if the regex is found it doesn't mean we're Ok, we need to validate that the first number is 7
		if (findText.getStatus()){
			String firstDigit = findText.getCounter();
			if (firstDigit.trim().equals("7")){
				report.report("Print K was 7 4 1 7 , as Excpected");
			}else if (firstDigit.trim().equals("15")){
				report.report("Error! PrintK first digit is 15!",Reporter.FAIL);
			}else {
				report.report("Error! PrintK in not 7 4 1 7!",Reporter.FAIL);
			}
		}
		report.stopLevel();
	}

	/**
	 * <b><h1>THIS BOOLEAN IS FOR DEBUG USE ONLY!!!!!</h1></b>
	 */
	public boolean debugFailureSleep = false;

	public boolean isDebugFailureSleep() {
		return debugFailureSleep;
	}

	public void setDebugFailureSleep(boolean debugFailureSleep) {
		this.debugFailureSleep = debugFailureSleep;
	}

	/**
	 * <b><h1>THIS FUNCTION IS FOR DEBUG USE ONLY!!!!!</h1></b>
	 */
	@Test
	@TestProperties(paramsInclude = { "debugFailureSleep" })
	public void sleepAndFail() throws Exception {

		if (debugFailureSleep) {
			// sleep for a minute
			sleep(60 * 1000);
		}
		// fail the test
		report.report("FAIL", report.FAIL);
	}

	/**
	 * This function runs only in case of fails in the tests and check if
	 * unexpected reboot happened or persona crash or DOA
	 * */
	private void validateDeviceStatus() throws Exception {

		//for (CellRoxDevice device : devicesMannager.getCellroxDevicesList()) {
		CellRoxDevice device = devicesMannager.getDevice(DeviceNumber.PRIMARY);
			boolean deviceCrashDetected = false;
			boolean personaCrashDetected = false;
			// Step 1 is to check for doa crash
			try {
				device.validateConnectivity();
			} catch (Exception e) {
				report.report("Out of connection ", Reporter.FAIL);
				connectionCrash++;
				return;
			}

			// Step 2 is to check for device crash by the upTime
			long knownUpTime = device.getUpTime();
			if (knownUpTime > device.getCurrentUpTime()) {
				report.stopLevel(); // stop After level
				report.report("The known upTime is : " + knownUpTime);
				deviceCrashDetected = true;
				RunProperties.getInstance().setRunProperty("deviceCrash", "1");
				report.startLevel("Device Crash! on "+getMethodName(),EnumReportLevel.MainFrame);
				report.report("Device Crash!", Reporter.FAIL);
				deviceCrash++;
				// TODO: can we know the name of the test? 
				sleep(20 * 1000);
				report.report("Device : " + device.getDeviceSerial());
				// last_kmsg

				// here im doning all the thing beside the reboot
				device.validateDeviceIsOnline(System.currentTimeMillis(), 5 * 60 * 1000, deviceEncrypted, deviceEncryptedPriv, Persona.PRIV, Persona.CORP);
				device.setDeviceAsRoot();
				device.setUpTime(device.getCurrentUpTime());
				device.setPsString(device.getPs());
			}

			// Step 3 is to check for persona crash
			if (!deviceCrashDetected) {
				String str2 = device.getPs(false);
				if (!device.isPsDiff(device.getPsString(), str2)) {
					personaCrashDetected = true;
					RunProperties.getInstance().setRunProperty("personaCrash", "1");
					report.stopLevel(); // stop After
					report.startLevel("Persona Crash! on "+getMethodName(),EnumReportLevel.MainFrame);
					report.report("Persona_Crash", Reporter.FAIL);
//					RunProperties.getInstance().setRunProperty("personaCrash", "1");
					personaCrash++;
					sleep(20 * 1000);
					report.report("Device : " + device.getDeviceSerial());
					// print last_kmsg before reboot
					report.report("About to print the last kmsg.");

					// validating which persona was crashed
					// getting the "old" PIDs
					Map<Persona, Integer> mapPerPrOld = new HashMap<Persona, Integer>();
					mapPerPrOld = device.getPersonaProcessIdMap();

					// getting the "new" PIDs
					Map<Persona, Integer> mapPerPrNew = new HashMap<Persona, Integer>();
					device.getPs(true);
					mapPerPrNew = device.getPersonaProcessIdMap();
					try {
						if (!mapPerPrNew.get(Persona.PRIV).equals(mapPerPrOld.get(Persona.PRIV))) {
							report.report("Error, persona Priv crashed.", Reporter.FAIL);
						}
					} catch (Exception e) {
						report.report("Error, persona Priv crashed.", Reporter.FAIL);
					}
					try {
						if (!mapPerPrNew.get(Persona.CORP).equals(mapPerPrOld.get(Persona.CORP))) {
							report.report("Error, persona CORP crashed.", Reporter.FAIL);
						}
					} catch (Exception e) {
						report.report("Error, persona CORP crashed.", Reporter.FAIL);
					}
					report.report("Please Find Below Logs Before Crash");
					// since last K message will not help up in this case - we'll look in logcat
					stopSysLogAndValidateInDevice();
					report.report("There is an error, the device is offline or had unwanted reboot. Rebooting Device...");
					device.rebootDevice(deviceEncrypted, deviceEncryptedPriv, Persona.PRIV, Persona.CORP);
				}

			}

			// if a crash is been detected - get all logs, validate device is
			// online, configure device and connect to uiautomator server
			// wake up the device and enter corp's password, then, return to
			// Priv
			if (deviceCrashDetected || personaCrashDetected) {
				// Last kmessage
				device.printLastKmsg();
				// check the syslogs
				stopSysLogAndValidateInDevice();
				// sleep
				// device.validateDeviceIsOnline(System.currentTimeMillis(), 5*
				// 60 *1000 , deviceEncrypted, deviceEncryptedPriv,
				// Persona.PRIV, Persona.CORP);
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
				report.stopLevel(); // stop device / persona crash
			}

		//}
	}

	/**
	 * This function is validating the data after bringing the data itself from
	 * the syslogs files For Example: <br>
	 * - oops\\|kernel panic\\|soft lockup<br>
	 * - out_of_memory<br>
	 * - Timed out waiting for /dev/.coldboot_done<br>
	 * - EGL_BAD_ALLOC<br>
	 * - persona\\.\\*died\\|cellroxservice\\.\\*died\\|FATAL EXCEPTION<br>
	 * - AudioFlinger\\.\\*buffer overflow<br>
	 * - STATE_CRASH_RESET<br>
	 */
	public void stopSysLogAndValidateInDevice() throws Exception {

		// checking the kmsg
		
		LogParser logParser = new LogParser();
		logParser.addExpression(Color.RED, "kernel_panic", "kernel_panic", "kmsg");
		logParser.addExpression(Color.RED, "oops", "oops", "kmsg");
		logParser.addExpression(Color.RED, "soft lockup", "soft lockup", "kmsg");
		logParser.addExpression(Color.RED, "out_of_memory", "out_of_memory", "kmsg");
		logParser.addExpression(Color.RED, "Timed out waiting for", "Timed out waiting for", "kmsg");
		logParser.addExpression(Color.RED, ".coldboot_done", ".coldboot_done", "kmsg");
		logParser.addExpression(Color.RED, "EGL_BAD_ALLOC", "EGL_BAD_ALLOC", "kmsg");
		logParser.addExpression(Color.RED, "persona died", "persona died", "kmsg");
		logParser.addExpression(Color.RED, "FATAL EXCEPTION", "FATAL EXCEPTION", "kmsg");
		logParser.addExpression(Color.RED, "AudioFlinger\\.\\*buffer overflow", "AudioFlinger\\.\\*buffer overflow", "kmsg");
		logParser.addExpression(Color.RED, "STATE_CRASH_RESET", "STATE_CRASH_RESET", "kmsg");
	
		devicesMannager.getDevice(currentDevice).getLogsOfRun(logParser, true, false);

		// checking the logcat
		logParser = new LogParser();
		logParser.addExpression(Color.RED, "FATAL_EXEPTION", "FATAL_EXEPTION", "logcat");
		logParser.addExpression(Color.RED, "fatal exception", "fatal exception", "logcat");
		devicesMannager.getDevice(currentDevice).getLogsOfRun(logParser, false, true);

	}

	public void initTheWebDriver() throws Exception {

		driverSo = (WebDriverSO) system.getSystemObject("webDriver");
		driver = driverSo.getDriver();
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

	public boolean isDeviceEncryptedPriv() {
		return deviceEncryptedPriv;
	}

	public void setDeviceEncryptedPriv(boolean deviceEncryptedPriv) {
		this.deviceEncryptedPriv = deviceEncryptedPriv;
	}

}
