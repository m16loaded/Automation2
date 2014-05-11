package com.cellrox.infra;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsystem.extensions.analyzers.text.FindText;
import jsystem.extensions.analyzers.text.GetTextCounter;
import jsystem.extensions.analyzers.text.TextNotFound;
import jsystem.framework.report.Reporter;
import jsystem.framework.report.Reporter.ReportAttribute;
import jsystem.framework.report.Summary;
import jsystem.framework.system.SystemObjectImpl;
import jsystem.utils.FileUtils;

import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.jsystemtest.mobile.core.AdbController;
import org.jsystemtest.mobile.core.AdbControllerException;
import org.jsystemtest.mobile.core.device.USBDevice;
import org.topq.uiautomator.AutomatorService;
import org.topq.uiautomator.ObjInfo;
import org.topq.uiautomator.Selector;
import org.topq.uiautomator.client.DeviceClient;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.FileListingService;
import com.android.ddmlib.InstallException;
import com.android.ddmlib.SyncException;
import com.android.ddmlib.TimeoutException;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.aqua.sysobj.conn.CliCommand;
import com.cellrox.infra.enums.Direction;
import com.cellrox.infra.enums.LogcatHandler;
import com.cellrox.infra.enums.Persona;
import com.cellrox.infra.enums.State;
import com.cellrox.infra.log.LogParser;

public class CellRoxDevice extends SystemObjectImpl {

	// init from the SUT
	private int encryptPasseord = 1111;
	private int privePort = 3435;
	private int corpPort = 4321;
	private int deviceIndex = 0;
	private String user = "topq";
	private String password = "Aa123456";
	private boolean deviceAsRootOnInit = false;
	// will not be init from the SUT
	private AdbController adbController;
	private USBDevice device;
	private AdbConnection cli;
	private String deviceSerial;
	List<AutomatorService> uiClient = Collections.synchronizedList(new ArrayList<AutomatorService>());
	private boolean afterCrush = false;
	private ExecutorService executor;
	// the otaFileLocation is for the jenkins/the local run to know where is the
	// ota file located and what is it name
	private String otaFileLocation;
	private long defaultCliTimeout = 90000;
	private String psString;
	private Set<String> processForCheck = new HashSet<String>();
	private long upTime;
	private Map<Persona, Integer> personaProcessIdMap = new HashMap<Persona, Integer>();
	// this boolean is only for showing the summary data in the Jsystem result
	// sender

	boolean online = false;

	public CellRoxDevice(int privePort, int corpPort, String otaFileLocation, String serialNumber, String user, String password, String runStatus)
			throws Exception {

		this.privePort = privePort;
		this.corpPort = corpPort;
		this.otaFileLocation = otaFileLocation;
		this.deviceSerial = serialNumber;
		this.user = user;
		this.password = password;

		adbController = AdbController.getInstance();
		device = adbController.waitForDeviceToConnect(serialNumber);
		cli = new AdbConnection("127.0.0.1", getUser(), getPassword(), serialNumber);
		cli.setExitTimeout(60 * 1000);
		cli.connect();

		setDeviceAsRoot();
		isDeviceConnected();

		// enable the syslogs
		cli.connect();
		executeCliCommand("adb -s " + getDeviceSerial() + " shell");
		executeCliCommand("setprop persist.service.syslogs.enable 1");
		cli.disconnect();

		// here is our checks uptime and the processes, by this we can check the
		// crashes
		if (runStatus.equals("full")) {
			setUpTime(getCurrentUpTime());
			initProcessesForCheck();
			setPsString(getPs());
		}
	}

	public void initAllTheCrashesValidationData() throws Exception {
		setUpTime(getCurrentUpTime());
		initProcessesForCheck();
		setPsString(getPs());
	}

	public void initProcessesForCheck() {
		processForCheck.add("/init");
		processForCheck.add("/sbin/ueventd");
		processForCheck.add("/system/bin/servicemanager");
		processForCheck.add("/system/bin/vold");
		processForCheck.add("/system/bin/netd");
		processForCheck.add("/system/bin/debuggerd");
		processForCheck.add("/system/bin/surfaceflinger");
		processForCheck.add("zygote");
		processForCheck.add("/system/bin/drmserver");
		processForCheck.add("/system/bin/mediaserver.cellrox");
		processForCheck.add("/system/bin/installd");
		processForCheck.add("/system/bin/keystore");
		processForCheck.add("/system/bin/surface_monitor");
		processForCheck.add("/system/bin/sdcard");
		processForCheck.add("/sbin/adbd");
		processForCheck.add("system_server");
		processForCheck.add("com.android.systemui");
		processForCheck.add("android.process.media");
		processForCheck.add("com.android.phasebeam");
		processForCheck.add("com.android.inputmethod.latin");
		processForCheck.add("com.android.phone");
		processForCheck.add("com.cellrox.cellroxservice");
		processForCheck.add("com.android.launcher");
		processForCheck.add("android.process.acore");
		processForCheck.add("com.android.settings");
		processForCheck.add("com.android.printspooler");
		processForCheck.add("com.android.smspush");
		processForCheck.add("com.android.music");
		processForCheck.add("com.android.dialer");
		processForCheck.add("com.android.bluetooth");
		processForCheck.add("com.android.providers.calendar");
		processForCheck.add("com.android.mms");
		processForCheck.add("com.android.onetimeinitializer");
		processForCheck.add("com.android.voicedialer");
		processForCheck.add("com.android.calendar");
		processForCheck.add("com.android.deskclock");
		processForCheck.add("com.android.email");
		processForCheck.add("com.android.exchange");
	}

	/**
	 * This function send command to the agent and check that the expectedLine
	 * returned.<br>
	 * The function will return the command till the timeout.
	 * */
	public void validateInAgent(String cmd, String expectedLine, int timeout) throws Exception {

		boolean isPass = false;

		cli.connect();
		executeCliCommand("adb -s " + getDeviceSerial() + " root");
		executeCliCommand("adb -s " + getDeviceSerial() + " shell");
		executeCliCommand("sqlite3 /data/agent/.agent.db");
		long startTime = System.currentTimeMillis();

		do {
			executeCliCommand(cmd);
			if (expectedLine == null) {
				isPass = true;
			} else if (cli.getTestAgainstObject().toString().contains(expectedLine)) {
				isPass = true;
			}
		} while (((System.currentTimeMillis() - startTime) < timeout) && (!isPass));

		if (!isPass) {
			report.report(expectedLine + " wasn't return from the agent from the server.", Reporter.FAIL);
		}

		cli.disconnect();
	}

	/**
	 * This class was make in order to compare between the data in the mdm and
	 * the phone, the returned map is the map from the phone
	 * 
	 * @throws Exception
	 * */
	public Map<String, String> getMapOfData() throws Exception {
		Map<String, String> compareMap = new HashMap<String, String>();
		String var = "", cmd = "";

		cli.connect();
		executeCliCommand("adb -s " + getDeviceSerial() + " root");
		executeCliCommand("adb -s " + getDeviceSerial() + " shell");

		cmd = "getprop ro.hardware";// Type
		executeCliCommand(cmd);
		var = getPropFromCli(cli.getTestAgainstObject().toString());
		compareMap.put("Model Number", var);
		// ROM Version
		cmd = "getprop ro.build.display.id";
		executeCliCommand(cmd);
		var = getPropFromCli(cli.getTestAgainstObject().toString());
		compareMap.put("ROM Version", var);
		// Android Version
		cmd = "getprop ro.build.version.release";
		executeCliCommand(cmd);
		var = getPropFromCli(cli.getTestAgainstObject().toString());
		compareMap.put("Android Version", var);
		// ID/IMEI
		cmd = "getprop ril.IMEI";
		executeCliCommand(cmd);
		var = getPropFromCli(cli.getTestAgainstObject().toString());
		compareMap.put("ID/IMEI", var);
		// Kernel Version
		cmd = "uname -r";
		executeCliCommand(cmd);
		var = getPropFromCli(cli.getTestAgainstObject().toString());
		compareMap.put("Kernel Version", var);

		return compareMap;
	}

	private String getPropFromCli(String propToParse) {
		String prop = null;
		Pattern p = Pattern.compile("\\[.*\\]:\\s*\\[(.*)\\]");
		Matcher m = p.matcher(propToParse);

		while (m.find()) {
			prop = m.group(1);
		}
		return prop;

	}

	public void executeCommandAdbShellRoot(String cmd) throws Exception {
		cli.connect();
		executeCliCommand("adb -s " + getDeviceSerial() + " root");
		executeCliCommand("adb -s " + getDeviceSerial() + " shell");
		executeCliCommand(cmd);
	}

	public void executeCommandAdbShell(String cmd) throws Exception {
		cli.connect();
		executeCliCommand("adb -s " + getDeviceSerial() + " shell");
		executeCliCommand(cmd);
	}

	public void executeCommandAdb(String cmd) throws Exception {
		cli.connect();
		executeCliCommand("adb -s " + getDeviceSerial() + " " + cmd);
	}

	/**
	 * Return the time as a String
	 * */
	public String getTheTime() throws Exception {

		cli.connect();

		executeCliCommand("adb -s " + getDeviceSerial() + " shell");
		executeCliCommand("date");
		String output = cli.getTestAgainstObject().toString().replace("date", "").trim().split(" ")[4];

		cli.disconnect();

		return output;
	}

	/**
	 * The function get a command and do it in priv and corp, after it the test
	 * will verify that there is the same regular expression in both of the
	 * personas and check the the wanted groups (etc (\s*),(\d+:\d+),(\S*),(\w*)
	 * and much more) inside of the expression is equal.
	 * */
	public void compareResultsCorpAndPriv(String cmd, String regularExpression) throws Exception {

		String[] outputPrivGroupArr = null, outputCorpGroupArr = null;

		String outputPriv = getPersona(Persona.PRIV).excuteCommand(cmd).trim();
		report.report(outputPriv);
		String outputCorp = getPersona(Persona.CORP).excuteCommand(cmd).trim();
		report.report(outputCorp);

		// check the priv
		Pattern pattern = Pattern.compile(regularExpression);
		Matcher matcher = pattern.matcher(outputPriv);
		if (matcher.find()) {
			int numOfGroups = matcher.groupCount();
			outputPrivGroupArr = new String[numOfGroups];
			for (int i = 0; i < numOfGroups; i++) {
				outputPrivGroupArr[i] = matcher.group(i + 1);
			}
		} else {
			report.report("Matcher isn't match : " + regularExpression + " to " + outputPriv + " on priv persona.", Reporter.FAIL);
		}

		// check the corp
		pattern = Pattern.compile(regularExpression);
		matcher = pattern.matcher(outputCorp);
		if (matcher.find()) {
			int numOfGroups = matcher.groupCount();
			outputCorpGroupArr = new String[numOfGroups];
			for (int i = 0; i < numOfGroups; i++) {
				outputCorpGroupArr[i] = matcher.group(i + 1);
			}
		} else {
			report.report("Matcher isn't match : " + regularExpression + " to " + outputCorp + " on corp persona.", Reporter.FAIL);
		}

		// compare the data from the two maches
		if (outputCorpGroupArr.length != outputPrivGroupArr.length) {
			report.report("Error the macher isn't equal in the personas", Reporter.FAIL);
		}
		for (int i = 0; i < outputCorpGroupArr.length; i++) {
			if (!outputCorpGroupArr[i].equals(outputPrivGroupArr[i])) {
				report.report("The data isn't equal : " + outputCorpGroupArr[i] + " to " + outputPrivGroupArr[i], Reporter.FAIL);
			}
		}

		report.report("The data from the persona and the corp is equal.");

	}

	/**
	 * This function use the get logs to returns the logs to the wanted
	 * location.
	 * */
	public void getTheLogs(LogcatHandler loggerType, String logLocation) throws Exception {
		cli.connect();
		executeCliCommand("adb -s " + getDeviceSerial() + " shell rm -R /data/agent/logs/adb");
		executeCliCommand("adb -s " + getDeviceSerial() + " shell mkdir -p /data/agent/logs/adb/ 2>&1 >/dev/null");
		executeCliCommand("adb -s " + getDeviceSerial() + " shell bash /system/etc/logcollector.sh " + loggerType.toString() + " \"adb/\" 2>&1 >/dev/null");
		executeCliCommand("adb -s " + getDeviceSerial() + " pull /data/agent/logs/adb " + logLocation);
		executeCliCommand("date");
		report.report("Logs saved in : " + logLocation);

	}

	/**
	 * This function is for saving the uptime.<br>
	 * The uptime is used for checking if there is a device crash.
	 * */
	public long getCurrentUpTime() throws Exception {
		String upTime = "";
		String retAns = executeHostShellCommand("uptime");

		upTime = retAns.toString().replace("uptime", "").replace("shell@mako:/ $", "").replace("root@mako:/ #", "").trim();

		report.report("Current up time : " + upTime);

		String upTimeTry = upTime.split(",")[0].replace("up time: ", "").trim().replace(":", "");
		if (upTimeTry.contains("days")) {
			upTime = upTime.split(",")[1].replace("up time: ", "").trim().replace(":", "");
		} else {
			upTime = upTime.split(",")[0].replace("up time: ", "").trim().replace(":", "");
		}
		upTime = upTime.replace("time", "").trim();
		return Long.parseLong(upTime);
	}

	/**
	 * This function perform : "adb devices" and "cell list stsate" if the sell
	 * list state isn't returned it will include this as error and throw
	 * exception.
	 * */
	public void validateConnectivity() throws Exception {
		cli.connect();
		executeCliCommand("adb devices");
		if (!cli.getTestAgainstObject().toString().contains(getDeviceSerial())) {
			throw new Exception("no connection, the device" + getDeviceSerial() + " isn't online.");
		}
		cli.disconnect();
	}

	/**
	 * This function perform : "adb devices" and "cell list stsate" if the sell
	 * list state isn't returned it will include this as error and throw
	 * exception.
	 * */
	public void validateDoaCrash() throws Exception {
		cli.connect();
		executeCliCommand("adb -s " + getDeviceSerial() + " shell");
		executeCliCommand("cell list state");
		if (!((cli.getTestAgainstObject().toString().contains("priv (3)")) && (cli.getTestAgainstObject().toString().contains("corp (3)")))) {
			throw new Exception("DOA, the device" + getDeviceSerial() + " isn't online.");
		}
		cli.disconnect();
	}

	public boolean validateThatDevicesIsreadyForAutomation() {
		boolean isReady = true;
		try {
			cli.connect();
			cli.switchToPersona(Persona.CORP);
			cli.switchToHost();
			cli.switchToPersona(Persona.PRIV);
		} catch (Exception e) {
			report.report("The device isn't ready for automation, the runs will stop", Reporter.FAIL);
			return false;
		}
		return isReady;
	}

	/**
	 * This function adding to the summary(Jsystem report) the properties.
	 * */
	public void addToTheSummarySystemProp() throws Exception {
		String prop = null;
		String hardware = null;
		report.startLevel("Get Device Properties");

		cli.connect();
		executeCliCommand("adb -s " + getDeviceSerial() + " root");
		executeCliCommand("adb -s " + getDeviceSerial() + " shell");

		// add build sdk version prop
		executeCliCommand("getprop | fgrep ro.build.version.sdk");
		prop = getPropFromCli(cli.getTestAgainstObject().toString());
		Summary.getInstance().setProperty("Build_sdk_version", prop);// propToParse.split(":")[1].trim());

		// add Build_display_id prop
		executeCliCommand("getprop | fgrep ro.build.display.id");
		prop = getPropFromCli(cli.getTestAgainstObject().toString());
		Summary.getInstance().setProperty("Build_display_id", prop);// propToParse.split(":")[1].trim());

		// add Build_date prop
		executeCliCommand("getprop | fgrep ro.build.date]");
		prop = getPropFromCli(cli.getTestAgainstObject().toString());
		Summary.getInstance().setProperty("Build_date", prop);// propToParse.split(":")[1].trim());

		// add hardware prop
		executeCliCommand("getprop | fgrep ro.hardware]");

		prop = getPropFromCli(cli.getTestAgainstObject().toString());
		Summary.getInstance().setProperty("hardware", prop);

		// add IMEI or MAC address to prop
		if (prop.trim().equalsIgnoreCase("Flo")) {
			// in this case bring the mac address
			executeCliCommand("netcfg | grep wlan0");
			String propToParse = cli.getTestAgainstObject().toString();
			propToParse = propToParse.replace("netcfg | grep wlan0", "");
			Pattern p = Pattern.compile("wlan0\\s*UP\\s*(\\d*\\.\\d*\\.\\d*\\.\\d*.\\d*)\\s*(\\S*)\\s*(.*)");
			Matcher m = p.matcher(propToParse);
			if (m.find()) {
				Summary.getInstance().setProperty("MAC_Address", m.group(1));
			}
		} else {
			// in this case bring the IMIE
			executeCliCommand("getprop | fgrep IMEI]");
			prop = getPropFromCli(cli.getTestAgainstObject().toString());
			Summary.getInstance().setProperty("IMEI", prop);
		}

		Summary.getInstance().setProperty("Vellamo_Results", "");

		cli.disconnect();
		report.stopLevel();
	}

	/**
	 * Validate Cli Command Output Exists On The Screen
	 * 
	 * @throws Exception
	 * */
	public void validateCliCommandOutputExistsOnTheScreen(String command, Persona persona) throws Exception {
		cli.connect();
		executeCliCommand("adb -s " + getDeviceSerial() + " root");
		executeCliCommand("adb -s " + getDeviceSerial() + " shell");
		executeCliCommand(command);
		String output = cli.getTestAgainstObject().toString().replace(command, "").replace("\n", "").replace("root@mako:/ #", "").trim();

		if (getPersona(persona).exist(new Selector().setTextContains(output))) {
			report.report("The uiobject is found with the text : " + output + ".");
		} else {
			report.report("Couldn't find the uiobject with the text : " + output + ".", Reporter.FAIL);
		}
	}

	/**
	 * print out the kmesg
	 * 
	 * @throws IOException
	 * */
	public void printLastKmsg() throws IOException {
		try {
			pullFileFromDevice("/proc/last_kmsg", report.getCurrentTestFolder() + "/last_kmsg.txt");
			report.addLink("Click Here for Last Kmsg Log", "last_kmsg.txt");
			// report.startLevel("click here for kmsg logger");
			// cli.connect();
			// //this is a wanted exception!
			// executeCliCommand("adb -s "+
			// getDeviceSerial()+" shell cat /proc/last_kmsg" , true , 240 *
			// 1000 , true, 2);
			//
			// cli.disconnect();
		} catch (Exception e) {
		}
		// finally {
		// report.report(cli.getTestAgainstObject().toString());
		// report.stopLevel();
		// }

	}

	/**
	 * This function kills all the automation running processes: 1. pkill
	 * uiautomator 2. ps | grep busybox 3. kill ${process number of the
	 * busybox's father} 4. pkill busybox
	 * */
	public void killAllAutomaionProcesses() throws Exception {

		report.report("Killing all the automation processes.");
		cli.connect();
		executeCliCommand("adb -s " + getDeviceSerial() + " root");
		executeCliCommand("adb -s " + getDeviceSerial() + " shell");
		executeCliCommand("pkill -KILL uiautomator");
		executeCliCommand("ps | grep nc");
		String[] retStringArr = cli.getTestAgainstObject().toString().split("\n");
		String expectedLine = "root\\s*(\\d*)\\s*\\d*\\s*\\d*\\s*\\d*\\s*\\S*\\s*\\S*\\s*\\S\\s*(\\S*)\\s*";
		for (String retLine : retStringArr) {
			Pattern pattern = Pattern.compile(expectedLine);
			Matcher matcher = pattern.matcher(retLine);

			if (matcher.find()) {
				if (matcher.group(2).toString().equals("nc")) {
					executeCliCommand("kill -KILL " + Integer.valueOf(matcher.group(1)));
				}
			}
		}
		report.report("All the processes are down.");
		cli.disconnect();
	}

	public void getDeviceFromAdb() throws Exception {
		adbController = AdbController.getInstance();
		// find all devices
		USBDevice[] devices = adbController.waitForDevicesToConnect(1);
		// set the device serial number by its index
		setDeviceSerial(devices[deviceIndex].getSerialNumber());
		device = adbController.waitForDeviceToConnect(getDeviceSerial());
	}

	/**
	 * This function checks that the adb devices return only one device
	 * */
	public void isDeviceConnected() throws Exception {
		cli.connect();
		executeCliCommand("adb devices");
		String devices = cli.getTestAgainstObject().toString();
		if (devices.split("device").length < 4) {
			cli.disconnect();
			throw new Exception("There is no devices connected.");
		}
		cli.disconnect();
	}

	/**
	 * This function will configure a device with all the pipes needed for port
	 * forwarding between the host and the cells
	 */
	public void configureDeviceForAutomation(boolean runServer) throws Exception {
		report.startLevel("Configure Device For Automation");

		killAllAutomaionProcesses();

		cli.connect();
		executeCliCommand("adb -s " + getDeviceSerial() + " root");
		executeCliCommand("adb -s " + getDeviceSerial() + " shell");

		Thread.sleep(200);
		cli.switchToPersona(Persona.PRIV);
		Thread.sleep(200);
		if (runServer) {
			executeCliCommand("uiautomator runtest uiautomator-stub.jar bundle.jar -c com.github.uiautomatorstub.Stub &");
			executeCliCommand("rm /data/local/tmp/local_pipe");
			executeCliCommand("mkfifo /data/local/tmp/local_pipe");
			executeCliCommand("rm /data/unix_soc");
			executeCliCommand("(nc -lkU /data/unix_soc </data/local/tmp/local_pipe | nc localhost 9008  >/data/local/tmp/local_pipe) &");
		}

		cli.switchToHost();
		executeCliCommand("adb -s " + getDeviceSerial() + " root");
		executeCliCommand("rm /data/local/tmp/local_pipe");
		executeCliCommand("mkfifo /data/local/tmp/local_pipe");
		executeCliCommand("nc -lk " + privePort + " < /data/local/tmp/local_pipe  | nc -U /data/containers/priv/data/unix_soc >/data/local/tmp/local_pipe &");

		cli.switchToHost();
		cli.switchToPersona(Persona.CORP);
		Thread.sleep(200);
		if (runServer) {
			executeCliCommand("uiautomator runtest uiautomator-stub.jar bundle.jar -c com.github.uiautomatorstub.Stub &");
			executeCliCommand("rm /data/local/tmp/local_pipe");
			executeCliCommand("mkfifo /data/local/tmp/local_pipe");
			executeCliCommand("rm /data/unix_soc");
			executeCliCommand("(nc -lkU /data/unix_soc </data/local/tmp/local_pipe | nc localhost 9008  >/data/local/tmp/local_pipe) &");
		}

		cli.switchToHost();
		executeCliCommand("adb -s " + getDeviceSerial() + " root");
		executeCliCommand("rm /data/local/tmp/local_pipe");
		executeCliCommand("mkfifo /data/local/tmp/local_pipe");
		executeCliCommand("nc -lk " + corpPort + " < /data/local/tmp/local_pipe  | nc -U /data/containers/corp/data/unix_soc >/data/local/tmp/local_pipe &");

		report.stopLevel();
		cli.disconnect();
	}

	/**
	 * Configure the device only for priv
	 * */
	public void configureDeviceForPriv(boolean runServer) throws Exception {

		killAllAutomaionProcesses();

		report.startLevel("Configure Device For Automation Priv");

		cli.connect();
		executeCliCommand("adb -s " + getDeviceSerial() + " root");
		executeCliCommand("adb -s " + getDeviceSerial() + " shell");

		Thread.sleep(200);
		cli.switchToPersona(Persona.PRIV);
		Thread.sleep(200);
		if (runServer) {
			executeCliCommand("uiautomator runtest uiautomator-stub.jar bundle.jar -c com.github.uiautomatorstub.Stub &");
			executeCliCommand("rm /data/local/tmp/local_pipe");
			executeCliCommand("mkfifo /data/local/tmp/local_pipe");
			executeCliCommand("rm /data/unix_soc");
			executeCliCommand("(nc -lkU /data/unix_soc </data/local/tmp/local_pipe | nc localhost 9008  >/data/local/tmp/local_pipe) &");
		}

		cli.switchToHost();
		executeCliCommand("adb -s " + getDeviceSerial() + " root");
		executeCliCommand("rm /data/local/tmp/local_pipe");
		executeCliCommand("mkfifo /data/local/tmp/local_pipe");
		executeCliCommand("nc -lk " + privePort + " < /data/local/tmp/local_pipe  | nc -U /data/containers/priv/data/unix_soc >/data/local/tmp/local_pipe &");

		report.stopLevel();
		cli.disconnect();
	}

	/**
	 * Install APK on device
	 * 
	 * @param apkLocation
	 *            on local file system
	 * @param reinstall
	 */
	public void installPackage(String apkLocation, boolean reinstall) throws InstallException {
		try {
			device.installPackage(apkLocation, reinstall);
		} catch (InstallException e) {
			report.report(e.getMessage(), Reporter.FAIL);
		}
	}

	/**
	 * Reboot the device, waits until personas are up
	 * 
	 * @throws TimeoutException
	 *             in case of timeout on the connection.
	 * @throws AdbCommandRejectedException
	 *             if adb rejects the command
	 * @throws IOException
	 */
	public void rebootDevice(boolean isEncrypted, boolean isEncryptedPriv, Persona... personas) throws Exception {
		rebootDevice(5 * 60 * 1000, isEncrypted, isEncryptedPriv, personas);
	}

	public void rebootDevice(int timeout, boolean isEncrypted, boolean isEncryptedPriv, Persona... personas) throws Exception {
		long currentTime = System.currentTimeMillis();
		try {
			sync();
			device.reboot();
			report.report("reboot command was sent");
			Thread.sleep(5000);
		} catch (Exception e) {
		}

		validateDeviceIsOnline(currentTime, timeout, isEncrypted, isEncryptedPriv, personas);
		setDeviceAsRoot();
		upTime = getCurrentUpTime();
		setPsString(getPs());
	}

	public void setDataAfterReboot() throws Exception {
		setDeviceAsRoot();
		upTime = getCurrentUpTime();
		setPsString(getPs());
	}

	/**
	 * Reboot Recovery the device, waits until personas are up
	 * 
	 * @throws TimeoutException
	 *             in case of timeout on the connection.
	 * @throws AdbCommandRejectedException
	 *             if adb rejects the command
	 * @throws IOException
	 */
	public boolean rebootRecoveryDevice(boolean isEncrypted, boolean isEncryptedPriv, Persona... personas) throws Exception {
		sync();
		device.executeShellCommand("reboot recovery");
		report.report("reboot recovery command was sent");
		Thread.sleep(60000);
		boolean isUp = validateDeviceIsOnline(isEncrypted, isEncryptedPriv, personas);
		// device = adbController.waitForDeviceToConnect(getDeviceSerial());
		upTime = getCurrentUpTime();
		setPsString(getPs());
		return isUp;
	}

	/**
	 * Executes shell command on host
	 * 
	 * @param command
	 * @throws Exception
	 */
	public String executeHostShellCommand(String shellCommand) throws Exception {
		return device.executeShellCommand(shellCommand);
	}

	public boolean validateDeviceIsOnline(boolean isEncrypted, boolean isEncryptedPriv, Persona... personas) throws Exception {
		return validateDeviceIsOnline(System.currentTimeMillis(), 10 * 60 * 1000, isEncrypted, isEncryptedPriv, personas);
	}

	// TODO to add isEncryptedPriv do something
	public boolean validateDeviceIsOnline(long beginTime, int timeout, boolean isEncrypted, boolean isEncryptedPriv, Persona... personas) throws Exception {
		// validateDeviceIsOffline(personas);
		Thread.sleep(2000);
		device = adbController.waitForDeviceToConnect(getDeviceSerial());
		// if the corp is encrypted we should wait until the "cell list state"
		// is 3 for Priv and 2 for Corp
		// then we should enter the Corp password and validate both personas'
		// state is 3
		if (isEncrypted) {
			validateEncryptedCorpPersonasAreOnline(beginTime, timeout, personas);
			Thread.sleep(3000);
			clickOnEncryptedDeviceAfterReboot();
			return validatePersonasAreOnline(beginTime, timeout, personas);
		} else {
			// if the Corp persona is not encrypted - just wait until both
			// personas' state is 3
			return validatePersonasAreOnline(beginTime, timeout, personas);
		}
	}

	/**
	 * This function insert to the encrypted screen the password "1111" and
	 * press enter. This connection is made by the adb connection;
	 * */
	public void clickOnEncryptedDeviceAfterReboot() throws Exception {
		report.startLevel("Loging to Encrypted Corp");
		cli.connect();
		Thread.sleep(2000);
		executeCliCommand("adb -s " + getDeviceSerial() + " root");
		Thread.sleep(2000);
		executeCliCommand("adb -s " + getDeviceSerial() + " shell");
		Thread.sleep(2000);
		executeCliCommand("cell console corp");
		Thread.sleep(2000);
		executeCliCommand("");
		executeCliCommand("y");
		Thread.sleep(2000);
		executeCliCommand("");
		executeCliCommand("input keyevent 8");
		executeCliCommand("");
		Thread.sleep(1000);
		executeCliCommand("input keyevent 8");
		executeCliCommand("");
		Thread.sleep(1000);
		executeCliCommand("input keyevent 8");
		Thread.sleep(1000);
		executeCliCommand("");
		executeCliCommand("input keyevent 8");
		Thread.sleep(1000);
		executeCliCommand("");
		executeCliCommand("input keyevent 66");// Enter
		Thread.sleep(1000);
		cli.disconnect();
		report.stopLevel();
	}

	/**
	 * This function tries to make the device root, if it can't, perform reboot
	 * and try again
	 * 
	 * @throws Exception
	 * */
	public void validateDeviceCanBeRoot(boolean isEncrypted, boolean isEncryptedPriv, Persona... personas) throws Exception {

		cli.connect();
		Thread.sleep(2000);
		executeCliCommand("adb -s " + getDeviceSerial() + " root");
		String rootAnswer = cli.getTestAgainstObject().toString();
		if (!rootAnswer.contains("adbd cannot run as root in production builds")) {
			report.report("Device : " + this.getDeviceSerial() + " is root now.");
		} else {
			report("adbd cannot run as root in production builds for : " + this.getDeviceSerial() + " , going to reboot.", Reporter.WARNING);
			rebootDevice(isEncrypted, isEncryptedPriv, personas);
			configureDeviceForAutomation(true);
			connectToServers();
			Persona persona = Persona.CORP;
			getPersona(persona).wakeUp();
			switchPersona(persona);
			getPersona(persona).click(new Selector().setText("1"));
			getPersona(persona).click(new Selector().setText("1"));
			getPersona(persona).click(new Selector().setText("1"));
			getPersona(persona).click(new Selector().setText("1"));
			getPersona(persona).click(new Selector().setDescription("Enter"));
			getPersona(persona).pressKey("home");

			switchPersona(Persona.PRIV);
			cli.connect();
			Thread.sleep(2000);
			executeCliCommand("adb -s " + getDeviceSerial() + " root");
			rootAnswer = cli.getTestAgainstObject().toString();
			if (rootAnswer.contains("adbd cannot run as root in production builds")) {
				report.report("Device : " + this.getDeviceSerial() + " is not root for the seconed time- check this, the tests are going to fall.",
						Reporter.FAIL);
			}
		}

	}

	/**
	 * validate the device is online by first waiting for the requested
	 * persona(s) to shutdown
	 * 
	 * @param personas
	 * @throws Exception
	 */
	public void validateDeviceIsOffline(Persona... personas) throws Exception {
		boolean offline = false;
		String result = null;
		int found = 0;
		while (offline != true) {
			try {
				result = device.executeShellCommand("cell list");
				if (result == null) {
					continue;
				}
				for (Persona persona : personas) {
					if (result.contains(persona.toString())) {
						found++;
					}

					if (found == 0) {
						offline = true;
					} else if (found == personas.length) {
						found = 0;
					}
				}
			} catch (AdbControllerException e) {
				offline = true;
			}
		}
		report.report("device is offline");
		Thread.sleep(2000);
	}

	/**
	 * validate the requested persona(s) are online with cell list command
	 * 
	 * @param personas
	 * @throws Exception
	 */
	public boolean validatePersonasAreOnline(long beginTime, int timeout, Persona... personas) throws Exception {
		String result = null;
		int found = 0;
		report.startLevel("Validating Device is Online");
		report.setSilent(true);
		while (online != true) {

			if (timeout < System.currentTimeMillis() - beginTime) {
				report.stopLevel();
				report.report("Fail due to timeout in validating the personas are on.", Reporter.FAIL);
				return false;
			}
			try {
				result = device.executeShellCommand("cell list state");
				report.report(result);
			} catch (AdbControllerException e) {
				continue;
			}
			if (result == null) {
				continue;
			}
			for (Persona persona : personas) {
				if (result.contains(persona.toString() + " (3)")) {
					found++;
				}
			}
			if (found == personas.length) {
				online = true;
			} else {
				found = 0;
			}
		}
		report.setSilent(false);
		report.stopLevel();
		report.report("device is online, its took : " + ((float) (System.currentTimeMillis() - beginTime)) / 1000 + " seconds.");
		Thread.sleep(7000);
		return true;
	}

	/**
	 * validate the requested persona(s) are online with cell list command
	 * 
	 * @param personas
	 * @throws Exception
	 */
	public void validateEncryptedCorpPersonasAreOnline(long beginTime, int timeout, Persona... personas) throws Exception {
		boolean online = false;
		String result = null;
		report.startLevel("Validating Device with Encrypted Corp Persona is Online");
		report.setSilent(true);
		while (online != true) {

			Thread.sleep(800);
			if (timeout < System.currentTimeMillis() - beginTime) {
				report.stopLevel();
				report.report("Fail due to timeout in validating the personas are on.", Reporter.FAIL);
				return;
			}

			try {
				result = device.executeShellCommand("cell list state");
				report.report(result);
			} catch (AdbControllerException e) {
				continue;
			}
			if (result == null) {
				continue;
			}
			if ((result.contains("(2)")) && (result.contains("(3)"))) {
				online = true;
				break;
			}
		}
		report.setSilent(false);
		report.stopLevel();
		report.report("device is online, it took : " + ((float) (System.currentTimeMillis() - beginTime)) / 1000 + " seconds.");
		Thread.sleep(2000);
	}

	/**
	 * return true if the requested persona(s) are on line<br>
	 * return false if one of the personas is down or devices is no available
	 * 
	 * @param personas
	 * @throws Exception
	 */
	public boolean isPersonasAreOnline(Persona... personas) throws Exception {
		String result = null;
		int found = 0;
		try {
			result = device.executeShellCommand("cell list");
		} catch (AdbControllerException e) {
			return false;
		}
		if (result == null) {
			return false;
		}
		for (Persona persona : personas) {
			if (result.contains(persona.toString())) {
				found++;
			}
		}
		if (found == personas.length) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * return <b>true</b> if device is online and ready to work , o/w return
	 * <b>false</b>
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean isOnline() throws Exception {
		if (device.isOnline() && !device.isOffline())
			return true;
		else
			return false;
	}

	/**
	 * Push file to the device
	 * 
	 * @param fileLocation
	 *            file location on the device
	 * @param fileName
	 *            file name on the device
	 * @throws Exception
	 */
	public void pushFileToDevice(String localLocation, String remotefileLocation) throws Exception {
		try {
			device.pushFileToDevice(remotefileLocation, localLocation);
		} catch (Exception e) {
			report.report("Error while pushing the file " + localLocation + " to " + remotefileLocation + e.getMessage(), Reporter.FAIL);
		}
	}

	/**
	 * Pulls file(s) or folder(s).
	 * 
	 * @param remoteFilepath
	 * @param localFilename
	 * @param entries
	 *            the remote item(s) to pull
	 * @param localPath
	 *            The local destination. If the entries count is > 1 or if the
	 *            unique entry is a folder, this should be a folder.
	 * @throws SyncException
	 * @throws IOException
	 * @throws TimeoutException
	 * @see FileListingService.FileEntry
	 * @see #getNullProgressMonitor()
	 */
	public void pullFileFromDevice(String remoteFilepath, String localFilename) throws Exception {
		try {
			device.pullFileFromDevice(remoteFilepath, localFilename);
		} catch (Exception e) {
			throw new Exception("Error while pulling the file " + remoteFilepath + " to " + localFilename + "\n"+e.getMessage());
		}
	}

	/**
	 * Set port forwarding for the requested device
	 * 
	 * @param deviceSerial
	 * @param localPort
	 * @param remotePort
	 * @throws Exception
	 */
	public void setPortForwarding(int localPort, int remotePort) throws Exception {
		device.setPortForwarding(localPort, remotePort);
	}

	public String getPs() throws Exception {
		return getPs(true);
	}

	/**
	 * this function return the current process list (by using "ps") as string<br>
	 * this function will alsp parse the process id of the personas and save
	 * them into an array mapOfProcessLocal<br>
	 * initVars param - if true - add to the personaProcessIdMap the process ids
	 * initVars param - if false - return the map of the processes ids
	 * */
	public String getPs(boolean isInit) throws Exception {
		cli.setExitTimeout(240 * 1000);
		cli.connect();
		executeCliCommand("adb -s " + deviceSerial + " shell");
		executeCliCommand("ps > /data/local/tmp/ps.txt", true, 4 * 60 * 1000);
		pullFileFromDevice("/data/local/tmp/ps.txt", report.getCurrentTestFolder() + "/ps.txt");
		report.addLink("Click Here for PS List", "ps.txt");
		getPsInitPrivCorp(isInit);
		String psList = FileUtils.read(report.getCurrentTestFolder() + "/ps.txt");
		return psList.toString().split("com.android.phone")[0];
	}

	/**
	 * The function : initVars param - if true - add to the personaProcessIdMap
	 * the process ids initVars param - if false - return the map of the
	 * processes ids
	 * */

	public Map<Persona, Integer> getPsInitPrivCorp(boolean initVars) throws Exception {
		int privId = -2, corpId = -2;
		report.startLevel("Getting Personas PID");
		Map<Persona, Integer> mapOfProcessLocal = new HashMap<Persona, Integer>();
		if (initVars) {
			personaProcessIdMap.clear();
		}
		cli.connect();
		executeCliCommand("adb -s " + deviceSerial + " shell");

		executeCliCommand("cell list");
		String[] retAnswerArr = cli.getTestAgainstObject().toString().trim().split("\n");
		for (String retAnswer : retAnswerArr) {
			if (retAnswer.contains("priv")) {
				privId = Integer.valueOf(retAnswer.replace("priv", "").replace("(", "").replace(")", "").trim());
			}
			if (retAnswer.contains("corp")) {
				corpId = Integer.valueOf(retAnswer.replace("corp", "").replace("(", "").replace(")", "").trim());
			}
		}

		executeCliCommand("ps | grep /init");
		String[] retStringArr = cli.getTestAgainstObject().toString().split("\n");
		String expectedLine = "root\\s*(\\d*)\\s*(\\d*)\\s*(\\d*)\\s*(\\d*)\\s*(\\S*)\\s*(\\S*)\\s*S\\s*";
		int counterOfPersonas = 0;
		for (String retLine : retStringArr) {
			Pattern pattern = Pattern.compile(expectedLine);
			Matcher matcher = pattern.matcher(retLine);

			if (matcher.find()) {

				int localId = Integer.valueOf(matcher.group(1));

				if (localId == privId) {
					report.report("Priv PID: " + localId);
					if (initVars) {
						personaProcessIdMap.put(Persona.PRIV, localId);
					} else {
						mapOfProcessLocal.put(Persona.PRIV, localId);
					}
				} else if (localId == corpId) {
					System.out.println("");
					report.report("Corp PID: " + localId);
					if (initVars) {
						personaProcessIdMap.put(Persona.CORP, localId);
					} else {
						mapOfProcessLocal.put(Persona.CORP, localId);
					}
				}
			}
		}
		report.stopLevel();
		return mapOfProcessLocal;
	}

	/**
	 * This function check or uncheck all the checkboxes in the screen
	 * */
	public void checkUncheckAllCheckBoxes(Persona persona, State isCheck) throws UiObjectNotFoundException {

		try {
			String object;
			boolean action = false;
			if (isCheck.equals(State.OFF)) {
				action = true;
			}

			while (true) {
				try {
					object = getPersona(persona).getUiObject(new Selector().setClassName("android.widget.CheckBox").setChecked(action).setEnabled(true));
				} catch (Exception e) {
					break;
				}
				if (object == null) {
					break;
				}
				if (object.isEmpty()) {
					break;
				}
				clickOnSelectorByUi(object, persona);
			}
		} catch (Exception e) {
			System.out.println("Exception nothing really happened, i waited for it.");
		}
	}

	/**
	 * This function checks for known differences unless it have a known
	 * diffrences.
	 * 
	 * @throws IOException
	 * */
	public boolean isPsDiff(String ps1, String ps2) throws IOException {

		String pid1 = null, pid2 = null, pname1 = null, pname2 = null;
		Map<String, String> map1 = new HashMap<String, String>();
		Map<String, String> map2 = new HashMap<String, String>(0);
		String[] strArr = ps1.split("\n");
		String[] strArr2 = ps2.split("\n");
		System.out.println(strArr.length);
		System.out.println(strArr2.length);
		String expression = "\\S*\\s*(\\d*)\\s*\\S*\\s*\\S*\\s*\\S*\\s*\\S*\\s*\\S*\\s*\\S*\\s*(\\S*)\\s*";

		for (int index = 0; index < strArr.length; index++) {
			Pattern pattern = Pattern.compile(expression);
			Matcher matcher = pattern.matcher(strArr[index]);
			if (matcher.find()) {
				pid1 = matcher.group(1);
				pname1 = matcher.group(2);
				if (processForCheck.contains(pname1)) {
					map1.put(pid1, pname1);
				}
			}
			if (index < strArr2.length) {
				matcher = pattern.matcher(strArr2[index]);
				if (matcher.find()) {
					pid2 = matcher.group(1);
					pname2 = matcher.group(2);
					if (processForCheck.contains(pname2)) {
						map2.put(pid2, pname2);
					}
				}
			}
		}

		if (map1.equals(map2)) {
			return true;
		} else {
			report.startLevel("Process Diffrencess");
			report.report("The Following processes could not be found:");
			for (Entry<String, String> entery : map1.entrySet()) {
				if (!map2.containsKey(entery.getKey())) {
					report.report(entery.getKey() + "," + entery.getValue());
				}
			}
			report.report("Map 1 - Before Fail : ");
			printMap(map1);
			report.report("Map 2 - After Fail : ");
			printMap(map2);
			report.stopLevel();
			return false;
		}

	}

	private void printMap(Map<String, String> map) {
		for (String key : map.keySet()) {
			report.report(map.get(key) + " - " + key);
		}
	}

	/**
	 * Open logcat termianl that print it all the logcat
	 * */
	public void openLogcatTerminal() throws IOException {
		String[] cmdss1 = { "gnome-terminal", "-x", "adb", "-s", deviceSerial, "logcat", "-v", "pidns" };
		/* Process proc = */Runtime.getRuntime().exec(cmdss1, null);
	}

	public void connectToServerPriv() throws Exception {

		device = adbController.waitForDeviceToConnect(getDeviceSerial());
		report.report("Device is online");
		setPortForwarding(privePort, privePort);
		// connect client to server
		uiClient.add(Persona.PRIV.ordinal(), DeviceClient.getUiAutomatorClient("http://localhost:" + privePort));
	}

	/**
	 * - ADB forword to priv and corp port as configured in the SUT<br>
	 * - Connect to UIAutomator servers on the two personas<br>
	 * <br>
	 * This function assumes that the servers are already running
	 * 
	 * @throws Exception
	 */
	public void connectToServers() throws Exception {

		device = adbController.waitForDeviceToConnect(getDeviceSerial());
		report.report("Device is online");
		setPortForwarding(privePort, privePort);
		setPortForwarding(corpPort, corpPort);
		// connect client to server
		uiClient.add(Persona.PRIV.ordinal(), DeviceClient.getUiAutomatorClient("http://localhost:" + privePort));
		uiClient.add(Persona.CORP.ordinal(), DeviceClient.getUiAutomatorClient("http://localhost:" + corpPort));
		addWatchers();

	}

	private void addWatchers() throws Exception {
		// addding watcher for unxpected stopped application on priv
		uiClient.get(Persona.PRIV.ordinal()).registerClickUiObjectWatcher("CLICK_UNEXPEXTED_STOP",
				new Selector[] { new Selector().setTextContains("Unfortunately") }, new Selector().setText("OK"));

		// addding watcher for unxpected stopped application on corp
		uiClient.get(Persona.CORP.ordinal()).registerClickUiObjectWatcher("CLICK_UNEXPEXTED_STOP",
				new Selector[] { new Selector().setTextContains("Unfortunately") }, new Selector().setText("OK"));

		// adding launcher selection watcher for priv
		uiClient.get(Persona.PRIV.ordinal()).registerClickMultiUiObjectWatcher("LAUNCHER_SELECTION",
				new Selector[] { new Selector().setText("Select a home app") },
				new Selector[] { new Selector().setTextMatches("Launcher"), new Selector().setText("Always") });

		// adding launcher selection watcher for corp
		uiClient.get(Persona.CORP.ordinal()).registerClickMultiUiObjectWatcher("LAUNCHER_SELECTION",
				new Selector[] { new Selector().setText("Select a home app") },
				new Selector[] { new Selector().setText("Launcher").setIndex(1), new Selector().setText("Always") });

		uiClient.get(Persona.PRIV.ordinal()).runWatchers();
		uiClient.get(Persona.CORP.ordinal()).runWatchers();

	}

	/**
	 * Get the foreground persona
	 * 
	 * @return Persona enum
	 * @throws Exception
	 */
	public Persona getForegroundPersona() throws Exception {
		String output = device.executeShellCommand("cat /proc/dev_ns/ns_tag");
		FindText findText = new FindText("tag:\\s*(.*)", true, false, 2);
		findText.setTestAgainst(output);
		findText.analyze();
		String perona = findText.getCounter();
		return Persona.valueOf(perona.toUpperCase().trim());
	}

	/**
	 * init logs (logcat, radio, kmsg)
	 */
	public void initLogs() throws Exception {
		cli.connect();
		String userHome = System.getProperty("user.home");
		executeCliCommand("adb -s " + getDeviceSerial() + " logcat -c", true);
		executeCliCommand("adb -s " + getDeviceSerial() + " logcat -v time > " + userHome + "/testLogcat.txt &", true);
		executeCliCommand("adb -s " + getDeviceSerial() + " logcat -b radio -v time > " + userHome + "/testRadioLogcat.txt &", true);
		executeCliCommand("adb -s " + getDeviceSerial() + " shell cat /proc/kmsg > " + userHome + "/testKmsg.txt &", true);
		cli.disconnect();
	}

	/**
	 * get logs (logcat , logcat-radio, kmsg)
	 */
	public void getLogs(LogParser parser) throws Exception {
		String userHome = System.getProperty("user.home");
		File logcat = new File(userHome + "/testLogcat.txt");
		File kmsg = new File(userHome + "/testKmsg.txt");
		File radioLogcat = new File(userHome + "/testRadioLogcat.txt");
		// set logs to validate
		parser.setLogs(logcat, radioLogcat, kmsg);
		parser.validateLogs();
		// delete all logs locally
		logcat.delete();
		kmsg.delete();
		radioLogcat.delete();
	}

	/**
	 * The function get logs of the entire run from /data/agent/syslogs
	 * 
	 * @throws Exception
	 */
	public void getLogsOfRun(LogParser parser, boolean kmsgSearch, boolean logcatSearch) throws Exception {
		String userHome = System.getProperty("user.home");
		// get the files

		device.pullFileFromDevice("/data/agent/syslogs/system_kmsg.txt", userHome + "/system_kmsg.txt");
		device.pullFileFromDevice("/data/agent/syslogs/system_logcat-radio.txt", userHome + "/system_logcat-radio.txt");
		device.pullFileFromDevice("/data/agent/syslogs/system_logcat.txt", userHome + "/system_logcat.txt");
		File logcat = new File(userHome + "/system_logcat.txt");
		File kmsg = new File(userHome + "/system_kmsg.txt");
		File radioLogcat = new File(userHome + "/system_logcat-radio.txt");
		// set logs to validate

		if (kmsgSearch && logcatSearch) {
			parser.setLogs(logcat, kmsg);
		} else if (kmsgSearch) {
			parser.setLogs(kmsg);
		} else {
			parser.setLogs(logcat);
		}
		parser.validateLogs();
		// delete all logs locally
		logcat.delete();
		kmsg.delete();
		radioLogcat.delete();
	}

	/**
	 * Get the device sdk version
	 * 
	 * @return the device's sdk version i.e. 16,17,18 etc.
	 * @throws Exception
	 */
	public int getDeviceSdkVersion() throws Exception {
		return device.getSdkVersion();
	}

	public AutomatorService getPersona(Persona persona) {
		return uiClient.get(persona.ordinal());
	}

	public void validateUiautomatorIsUP() throws Exception {
		cli.connect();
		executeCliCommand("adb -s " + getDeviceSerial() + " root");
		executeCliCommand("adb -s " + getDeviceSerial() + " shell");
		executeCliCommand("ps | grep uiautomator");
		String retPs = cli.getTestAgainstObject().toString().replace("ps | grep uiautomator", "");
		if (retPs.split("uiautomator").length != 3) {
			report.report("The uiautomator not connect.");
			report.report("About to configure new device for the automation and to connect to the servers.");
			configureDeviceForAutomation(true);
			connectToServers();
			report.report("Now the uiautomator connect.");
		} else {
			report.report("The uiautomator connect.");
		}

	}

	/**
	 * set root persmission on deivce and that there is no unknown device
	 * (?????) with adb root command - try to o it for 3 times.
	 * 
	 * @throws Exception
	 */
	public void setDeviceAsRoot() throws Exception {

		String retString = "";
		final int numberOfRootAttempts = 5;
		cli.connect();
		for (int i = 0; i < numberOfRootAttempts; i++) {
			executeCliCommand("adb -s " + getDeviceSerial() + " root", true);
			retString = cli.getTestAgainstObject().toString();
			if (retString.contains("error: device unauthorized.") || retString.contains("offline") || retString.contains("device not found") || retString.contains("adbd cannot run as root in production builds")) {
				executeCliCommand("adb -s " + getDeviceSerial() + " kill-server");
			} else {
				if (retString.contains("?????")) {
					cli.disconnect();
					throw new Exception("error: device is ?????, couldn't make adb root.");
				} else {
					break;
				}
			}
		}
		if (retString.contains("error: device unauthorized.") || retString.contains("offline") || retString.contains("device not found") || retString.contains("adbd cannot run as root in production builds")) {
			cli.disconnect();
			throw new Exception("adb error, couldn't make adb root.");
		}
		Thread.sleep(2000);
		cli.disconnect();
	}

	public String uploadRomOldServer(String serverHost, String imgFile, String version, String adminToken, String md5sum) throws Exception {
		cli.connect();
		CliCommand cmd = new CliCommand();
		cmd.setCommand(String
				.format("curl -i -k -H \"Accept: application/json\" -F \"auth_token=%s\" -F \"rom[version]=%s\" -F \"rom[argument_attributes][attachment]=@%s\" -F \"rom[argument_attributes][md5]=%s\" https://%s/roms | tee /tmp/upload.result | tail -1 | sed 's/,/\\\"/g' | sed 's/://g' | awk -F\\\" '{ print $14\"/\"$10}'",
						adminToken, version, imgFile, md5sum, serverHost).replace("\r\n", ""));
		// timeout up to 30 min.
		cmd.setTimeout(1800000);
		cli.handleCliCommand("Uploading image " + imgFile, cmd);
		// validate upload.result file
		String uplodresult = FileUtils.read(new File("/tmp/upload.result"));
		if (!uplodresult.contains("200 OK")) {
			report.report(uplodresult, Reporter.FAIL);
		}
		// get result
		FindText findText = new FindText("(.*/.*/\\d+)", true, false, 2);
		cli.analyze(findText);
		String result = findText.getCounter();
		cli.disconnect();
		return result;
	}

	public String remoteUpdateOldServer(String result, String serverUrl, String deviceId, String md5, String fileSize, String version, String adminToken)
			throws Exception {

		String command = String.format("COMMAND DOWNLOAD %s %s %s %s", version, result, md5, fileSize);
		String description = "Remote Update " + version;
		String date = getDate();
		String remoteUpdateCmd = String
				.format("curl -k -H \"Accept: application/json\" -X POST https://%s/devices/%s/messages.json -d \"auth_token=%s\" -d \"message[due_at]=%s\" -d \"message[payload]=%s\" -d \"message[description]=%s\" 2>&1 | tee post.result | tail -1 | sed 's/,/\\n/g' | sed 's/[{}]/\\n/g'\r\n",
						serverUrl, deviceId, adminToken, date, command, description);
		CliCommand cmd = new CliCommand(remoteUpdateCmd);
		cmd.setTimeout(1800000);
		cli.connect();
		cli.handleCliCommand("Uploading image ", cmd);
		cli.disconnect();
		return command;
	}

	public String getDate() throws Exception {
		cli.connect();
		executeCliCommand("echo -n R;date -u +\"%Y-%m-%dT%H:%M:%SZ\"| awk '{print \"esult: \" $1}'");
		GetTextCounter counter = new GetTextCounter("Result");
		cli.analyze(counter);
		String date = counter.getCounter().trim();
		cli.disconnect();
		return date.trim();
	}

	public String md5sum(String fileName) throws Exception {
		cli.connect();
		CliCommand cmd = new CliCommand(String.format("echo -n R ; md5sum %s | awk '{print \"esult: \" $1}'", fileName));
		cmd.setSuppressEcho(true);
		cmd.addErrors("No such file or directory");
		cli.handleCliCommand("getting image file MD5SUM", cmd);
		GetTextCounter counter = new GetTextCounter("Result");
		cli.analyze(counter);
		String md5sum = counter.getCounter().trim();
		cli.disconnect();
		return md5sum;
	}

	/**
	 * Wait for a specific line in Logcat<br>
	 * This function is limited by timeout
	 * 
	 * @param line
	 * @param timeout
	 *            (millis)
	 * @param interval
	 *            (millis)
	 * @return
	 * @throws Exception
	 */
	public boolean waitForLineInTomcat(String line, long timeout, long interval) throws Exception {
		final long start = System.currentTimeMillis();
		while (!getLogcatLastLines(500).contains(line)) {
			if (System.currentTimeMillis() - start > timeout) {
				report.report("Could not find the line " + line, Reporter.FAIL);
				return false;
			}
			Thread.sleep(interval);
		}
		String log = getLogcatLastLines(50).replace("\n", "<br>").replace(line, "<b>" + line + "</b>");
		report.report("Click Here to See Logcat Result", log, ReportAttribute.HTML);
		return true;
	}

	public void deleteFile(String file) throws Exception {
		report.startLevel("Deleting File " + file);
		cli.connect();
		executeCliCommand("adb -s " + getDeviceSerial() + " shell rm " + file);
		cli.analyze(new TextNotFound("No such file or directory"));
		cli.analyze(new TextNotFound("Permission denied"));
		cli.disconnect();
		report.stopLevel();
	}

	public void sendSqlQuery(String sqlQuery) throws Exception {
		cli.connect();
		executeCliCommand("adb -s " + getDeviceSerial() + " shell");
		executeCliCommand("sqlite3 /data/agent/.agent.db");
		executeCliCommand(sqlQuery);
		executeCliCommand(".exit");
		cli.disconnect();
	}

	/**
	 * Get the last X lines from logcat
	 * 
	 * @param lines
	 * @return
	 * @throws Exception
	 */
	private String getLogcatLastLines(int lines) throws Exception {
		// logcat -v time -d | tail -n 10 | grep corp
		String logcat = device.executeShellCommand(String.format("logcat -v time -d | tail -n %d", lines));
		System.out.println(logcat);
		return logcat;
	}

	/**
	 * find the expression to find after cli command in the adb shell
	 * 
	 * @param cliCommand
	 *            - the wanted cli command after the adb shell
	 * @param expression
	 *            - the wanted text/expression to find
	 * @param isRegularExpression
	 *            - is this expression is a regular expression
	 * */
	public void validateExpressionCliCommand(String cliCommand, String expression, boolean isRegularExpression, boolean isShell, int numberOfTries)
			throws Exception {
		boolean isPass = false;
		cli.connect();

		if (isShell)
			executeCliCommand("adb -s " + getDeviceSerial() + " shell");
		while (numberOfTries > 0 && !isPass) {
			executeCliCommand(cliCommand);
			FindText findText = new FindText(expression, isRegularExpression);
			// cli.analyze(findText, false);
			findText.setTestAgainst(cli.getTestAgainstObject());
			findText.analyze();
			isPass = findText.getStatus();
			numberOfTries--;
			Thread.sleep(1000);
		}

		if (!isPass) {
			report.report("Couldn't find the text : " + expression, Reporter.FAIL);
		}

		cli.disconnect();

	}

	public void validateExpressionCliCommand(String cliCommand, String expression, boolean isRegularExpression, boolean isShell) throws Exception {
		validateExpressionCliCommand(cliCommand, expression, isRegularExpression, true, 1);
	}

	public void validateExpressionCliCommand(String cliCommand, String expression, boolean isRegularExpression) throws Exception {
		validateExpressionCliCommand(cliCommand, expression, isRegularExpression, true, 1);
	}

	public void validateExpressionCliCommand(String cliCommand, String expression, boolean isRegularExpression, int numberOfTries) throws Exception {
		validateExpressionCliCommand(cliCommand, expression, isRegularExpression, true, numberOfTries);
	}

	/**
	 * find the expression to find after cli command in the adb shell
	 * 
	 * @param cliCommand
	 *            - the wanted cli command after the adb shell
	 * @param expression
	 *            - the wanted text/expression to find
	 * @param isRegularExpression
	 *            - is this expression is a regular expression
	 * @param persona
	 *            - the cell console and the wanted persona
	 * */
	public void validateExpressionCliCommand(String cliCommand, String expression, boolean isRegularExpression, Persona persona) throws Exception {

		try {
			report.report(cliCommand);
			String responce = getPersona(persona).excuteCommand(cliCommand);
			report.report(responce);
			if (isRegularExpression) {

				Pattern pattern = Pattern.compile(expression);
				Matcher matcher = pattern.matcher(responce);

				if (matcher.find())
					report.report("Find : " + expression + " in : " + responce);
				else
					report.report("Couldnt find : " + expression + " in : " + responce, Reporter.FAIL);
			} else {
				if (!responce.contains(expression))
					report.report("Couldnt find : " + expression + " in : " + responce, Reporter.FAIL);
				else
					report.report("Find : " + expression + " in : " + responce);
			}
		} catch (Exception e) {
			report.report("Error : " + e.getMessage(), Reporter.FAIL);
		}

	}

	public void validateExpressionCliCommandCell(String cliCommand, boolean isRegularExpression, Persona persona, int numberOfTries, String[] expressions)
			throws Exception {
		boolean isPass = false;

		cli.connect();
		executeCliCommand("adb -s " + getDeviceSerial() + " shell");
		if (persona == Persona.PRIV) {
			executeCliCommand("cell console priv");
		} else {
			executeCliCommand("cell console corp");
		}
		executeCliCommand(" ");
		executeCliCommand(" ");
		executeCliCommand(" ");
		executeCliCommand(" ");
		executeCliCommand("");

		while (numberOfTries > 0 && !isPass) {
			for (String expression : expressions) {
				executeCliCommand(cliCommand);
				FindText findText = new FindText(expression, isRegularExpression);
				findText.setTestAgainst(cli.getTestAgainstObject());
				findText.analyze();
				if (findText.getStatus()) {
					isPass = true;
					break;
				}
			}
			numberOfTries--;
			Thread.sleep(1000);

		}

		if (!isPass) {
			StringBuilder longExpression = new StringBuilder();
			for (String expression : expressions) {
				longExpression.append(expression + ", ");
			}
			report.report("Couldn't find the texts : " + longExpression, Reporter.FAIL);
		}
		cli.disconnect();

	}

	public void pushApplication(String appFullPath, String locationForPushing) throws Exception {
		cli.connect();
		report.report("about to do : adb -s " + getDeviceSerial() + " push " + appFullPath + " " + locationForPushing);
		executeCliCommand("adb -s " + getDeviceSerial() + " push " + appFullPath + " " + locationForPushing);
		cli.disconnect();
	}

	private void executeCliCommand(String Command) throws Exception {
		executeCliCommand(Command, false);
		Thread.sleep(500);
	}

	private void executeCliCommand(String Command, boolean silent) throws Exception {
		executeCliCommand(Command, silent, defaultCliTimeout);
	}

	private void executeCliCommand(String Command, boolean silent, long timeout) throws Exception {
		executeCliCommand(Command, silent, defaultCliTimeout, false, 2);
	}

	private void executeCliCommand(String Command, boolean silent, long timeout, boolean ignoreErrors, int numOfTries) throws Exception {
		CliCommand cmd = new CliCommand(Command);
		cmd.setSilent(silent);
		cmd.setAddEnter(true);
		cmd.setTimeout(timeout);
		cmd.setIgnoreErrors(ignoreErrors);
		cli.handleCliCommand(Command, cmd);
		// this part is for checking if the adb is down, if so it will restart
		// the connection
		if (cli.getTestAgainstObject().toString().contains("error: protocol fault (no status)")) { //TODO if offline etc. status repets - add here the retrys
			if (numOfTries > 1) {
				cli.connect();
				cmd = new CliCommand("adb kill-server");
				cmd.setSilent(silent);
				cmd.setAddEnter(true);
				cmd.setTimeout(timeout);
				cmd.setIgnoreErrors(ignoreErrors);
				cli.handleCliCommand(Command, cmd);
				// making the reconnect
				configureDeviceForAutomation(true);
				connectToServers();
				// doing the command again
				executeCliCommand(Command, silent, timeout, ignoreErrors, 1);
			}
		}
	}

	/**
	 * The following functions click on the button by the x y cordinate
	 * */
	public void clickOnSelectorByUi(ObjInfo info, Persona persona, Direction dir) throws UiObjectNotFoundException {
		int horizon = -9, vertical = -3;
		if (dir == null) {
			horizon = (info.getVisibleBounds().getRight() + info.getVisibleBounds().getLeft()) / 2;
			vertical = (info.getVisibleBounds().getTop() + info.getVisibleBounds().getBottom()) / 2;
		} else if (dir.equals(Direction.MIDDLE)) {
			horizon = (info.getVisibleBounds().getRight() + info.getVisibleBounds().getLeft()) / 2;
			vertical = (info.getVisibleBounds().getTop() + info.getVisibleBounds().getBottom()) / 2;
		} else if (dir.equals(Direction.LEFT)) {
			horizon = info.getVisibleBounds().getLeft();
			vertical = (info.getVisibleBounds().getTop() + info.getVisibleBounds().getBottom()) / 2;
		} else if (dir.equals(Direction.RIGHT)) {
			horizon = info.getVisibleBounds().getRight() - 1;
			vertical = (info.getVisibleBounds().getTop() + info.getVisibleBounds().getBottom()) / 2;
		}
		report.report("About to click point at : " + horizon + "," + vertical);
		getPersona(persona).click(horizon, vertical);
	}

	/**
	 * The next 4 functions are for clicking on ui object by it location. All
	 * function are going to the same last function.
	 * */
	public void clickOnSelectorByUi(String id, Persona persona) throws UiObjectNotFoundException {
		clickOnSelectorByUi(id, persona, Direction.MIDDLE);
	}

	public void clickOnSelectorByUi(String id, Persona persona, Direction dir) throws UiObjectNotFoundException {
		ObjInfo obj = getPersona(persona).objInfo(id);
		clickOnSelectorByUi(obj, persona, dir);
	}

	public void clickOnSelectorByUi(Selector s, Persona persona) throws UiObjectNotFoundException {
		clickOnSelectorByUi(s, persona, Direction.MIDDLE);
	}

	public void clickOnSelectorByUi(Selector s, Persona persona, Direction dir) throws UiObjectNotFoundException {
		String id = getPersona(persona).getUiObject(s);
		clickOnSelectorByUi(id, persona, dir);
	}

	public void clickOnSelectorByUi(Selector s, Persona persona, Direction dir, int indexOfElement) throws UiObjectNotFoundException {

		ObjInfo[] objs = getPersona(persona).objInfoOfAllInstances(s);
		if (indexOfElement >= objs.length) {
			report.report("Error, the element : " + indexOfElement + " isn't exists.", Reporter.FAIL);
		} else {
			clickOnSelectorByUi(objs[indexOfElement], persona, dir);
		}
	}

	public void validateWantedPersona(Persona persona) throws Exception {
		Persona current = getForegroundPersona();
		if (current != persona) {
			report.report("Persona " + persona + " isn't in the Foreground", Reporter.FAIL);
		} else {
			report.report("Persona " + persona + " is in the Foreground");
		}

	}

	/**
	 * The switch persona function is switching to the wanted persona. If there
	 * is an error the function will try to switch one more time.
	 * */
	public void switchPersona(Persona persona) throws Exception {
		Persona current = getForegroundPersona();
		if (current == persona) {
			report.report("Persona " + persona + " is Already in the Foreground");
		} else {
			getPersona(current).click(5, 5);
			current = getForegroundPersona();
			if (current == persona) {
				report.report("Switch to " + persona);

			} else {
				report.report("Could not Switch to " + persona + " for the first time.");
				current = getForegroundPersona();
				if (current == persona) {
					report.report("Persona " + persona + " is in the Foreground");
				} else {
					getPersona(current).click(5, 5);
					current = getForegroundPersona();
					if (current == persona) {
						report.report("Switch to " + persona);
					} else {
						report.report("Could not Switch to " + persona, Reporter.FAIL);
					}
				}
			}
		}
	}

	public void unlockBySwipe(Persona persona) throws Exception {
		try {
			ObjInfo oInfo = getPersona(persona).objInfo(new Selector().setDescription("Slide area."));

			int middleX = (oInfo.getBounds().getLeft() + oInfo.getBounds().getRight()) / 2;
			int middleY = (oInfo.getBounds().getTop() + oInfo.getBounds().getBottom()) / 2;
			getPersona(persona).swipe(middleX, middleY, oInfo.getBounds().getLeft() + 3, middleY, 20);

			getPersona(persona).pressKey("home");
		} catch (Exception e) {
			report.report("Error in unlocking the device.");
		}
	}

	public void answerCall(Persona persona) throws Exception {
		try {
			ObjInfo oInfo = getPersona(persona).objInfo(new Selector().setDescription("Slide area."));

			int middleX = (oInfo.getBounds().getLeft() + oInfo.getBounds().getRight()) / 2;
			int middleY = (oInfo.getBounds().getTop() + oInfo.getBounds().getBottom()) / 2;
			getPersona(persona).swipe(middleX, middleY, oInfo.getBounds().getLeft() + 3, middleY, 20);
		} catch (Exception e) {
			report.report("Error in answer.");
		}

	}

	/**
	 * This function runs apply update script
	 * */
	public void runApplayUpdateScript(String applyUpdateLocation, String otaFileLocation) throws Exception {
		cli.connect();
		cli.setExitTimeout(10 * 60 * 1000);
		executeCliCommand("adb -s " + getDeviceSerial() + " root");
		executeCliCommand("su -", false);
		executeCliCommand("");
		Thread.sleep(1500);
		executeCliCommand(applyUpdateLocation + " " + otaFileLocation);
		Thread.sleep(100 * 1000);
		cli.disconnect();
	}

	/**
	 * This is the System Object "destructor"<br>
	 * The close function will close all the process that are open for the
	 * automation use.<br>
	 * <b>please note: </b>this function only invokes on the end of the
	 * automation run and not between tests or building blocks
	 */
	@Override
	public void close() {
		if (executor != null) {
			while (!executor.isTerminated()) {
			}
		}
		super.close();
	}

	public void sync() throws Exception {
		cli.connect();
		executeCliCommand("sync");
		cli.disconnect();
	}

	public int getPrivePort() {
		return privePort;
	}

	public int getCorpPort() {
		return corpPort;
	}

	public int getDeviceIndex() {
		return deviceIndex;
	}

	public void setPrivePort(int privePort) {
		this.privePort = privePort;
	}

	public void setCorpPort(int corpPort) {
		this.corpPort = corpPort;
	}

	public void setDeviceIndex(int deviceIndex) {
		this.deviceIndex = deviceIndex;
	}

	public String getDeviceSerial() {
		return deviceSerial;
	}

	public void setDeviceSerial(String deviceSerial) {
		this.deviceSerial = deviceSerial;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isDeviceAsRootOnInit() {
		return deviceAsRootOnInit;
	}

	public void setDeviceAsRootOnInit(boolean deviceAsRootOnInit) {
		this.deviceAsRootOnInit = deviceAsRootOnInit;
	}

	public boolean isAfterCrush() {
		return afterCrush;
	}

	public void setAfterCrush(boolean afterCrush) {
		this.afterCrush = afterCrush;
	}

	public int getEncryptPasseord() {
		return encryptPasseord;
	}

	public void setEncryptPasseord(int encryptPasseord) {
		this.encryptPasseord = encryptPasseord;
	}

	public String getOtaFileLocation() {
		return otaFileLocation;
	}

	public void setOtaFileLocation(String otaFileLocation) {
		this.otaFileLocation = otaFileLocation;
	}

	public String getPsString() {
		return psString;
	}

	public void setPsString(String psString) {
		this.psString = psString;
	}

	public long getUpTime() {
		return upTime;
	}

	public void setUpTime(long upTime) {
		this.upTime = upTime;
	}

	public Map<Persona, Integer> getPersonaProcessIdMap() {
		return personaProcessIdMap;
	}

	public void setPersonaProcessIdMap(Map<Persona, Integer> personaProcessIdMap) {
		this.personaProcessIdMap = personaProcessIdMap;
	}

}