package com.cellrox.tests;

import java.io.File;

import jsystem.framework.TestProperties;
import jsystem.framework.analyzer.AnalyzerException;
import jsystem.framework.report.Reporter.ReportAttribute;
import jsystem.framework.scenario.UseProvider;
import junit.framework.SystemTestCase4;

import org.jsystemtest.mobile.core.ConnectionException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.topq.uiautomator.Selector;

import com.cellrox.infra.CellRoxDevice;
import com.cellrox.infra.enums.Direction;
import com.cellrox.infra.enums.Persona;
import com.cellrox.infra.log.LogParser;
import com.cellrox.infra.object.LogParserExpression;

public class CellroxDeviceOperations extends SystemTestCase4 {

	CellRoxDevice device;
	File localLocation;
	String remotefileLocation;
	Persona persona;
	Selector selector;
	String button;
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
	private String expectedLine;
	private long interval;
	private long timeout=10000;
	private boolean isExists;
	private String sqlQuery;
	private Direction dir;
	private String childClassName;
	int instance = 0;

	@Before
	public void init() throws Exception {
		device = (CellRoxDevice) system.getSystemObject("device");
		// validateDeviceStatus();
	}

	@Test
	public void ortziExample() throws Exception {
		device.setDeviceAsRoot();
		device.configureDeviceForAutomation(true);
		device.connectToServers();
		device.getPersona(Persona.PRIV).wakeUp();
		device.getPersona(Persona.PRIV).openApp("Voice Search");
		device.getPersona(Persona.PRIV).waitForExists(
				new Selector().setIndex(1).setPackageName("com.google.android.googlequicksearchbox")
						.setClassName("android.widget.TextView"), 10000);
	}

	@Test
	public void testME() throws Exception {
		device.configureDeviceForAutomation(true);
		device.connectToServers();
		device.getPersona(Persona.PRIV).wakeUp();
		device.getPersona(Persona.PRIV).click(new Selector().setText("1"));
		device.getPersona(Persona.PRIV).click(new Selector().setText("1"));
		device.getPersona(Persona.PRIV).click(new Selector().setText("1"));
		device.getPersona(Persona.PRIV).click(new Selector().setText("1"));
		device.getPersona(Persona.PRIV).waitForExists(new Selector().setDescription("Status widget."), 10000);
	}

	@Test
	@TestProperties(name = "push ${localLocation} to ${remotefileLocation}", paramsInclude = { "localLocation,remotefileLocation" })
	public void pushToDevice() throws Exception {
		device.pushFileToDevice(localLocation.getAbsolutePath(),
				remotefileLocation);
		runProperties.setRunProperty("adb.push.file.location",
				remotefileLocation);
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
	@TestProperties(name = "Click on UiObject by Text \"${text}\" on ${persona}", paramsInclude = { "text,selector,persona,waitForNewWindow" })
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
			report.report("Could not find UiObject " + e.getMessage(),
					report.FAIL);
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
			report.report("Could not find UiObject " + e.getMessage(),
					report.FAIL);
		}
	}

	@Test
	@TestProperties(name = "Scroll and Click on UiObject by Text \"${text}\" on ${persona}", paramsInclude = { "text,persona" })
	public void scrollAndClick() throws Exception {
		Selector s = new Selector();
		s.setText(text);
		try {
			String objectId = device.getPersona(persona).childByText(
					new Selector().setScrollable(true),
					new Selector().setText(text), text, true);
			device.getPersona(persona).click(objectId);
		} catch (Exception e) {
			report.report("Could not find UiObject " + e.getMessage(),
					report.FAIL);
		}
	}

	@Test
	@TestProperties(name = "Click on UiObject by Description \"${text}\" on ${persona}", paramsInclude = { "text,persona,waitForNewWindow" })
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
			report.report("Could not find UiObject " + e.getMessage(),
					report.FAIL);
		}
	}

	@Test
	@TestProperties(name = "Wait for UiObject by Description \"${text}\" on ${persona}", paramsInclude = { "text,persona" })
	public void waitByDesc() throws Exception {
		Selector s = new Selector();
		s.setDescription(text);
		try {
			device.getPersona(persona).waitForExists(
					new Selector().setDescription(text), 10000);
		} catch (Exception e) {
			report.report("Could not find UiObject " + e.getMessage(),
					report.FAIL);
		}
	}
	@Test
	@TestProperties(name = "Get Full Text of Text Contains \"${text}\" on ${persona}", paramsInclude = { "text,persona" })
	public void getTextByTextContains() throws Exception{
		String msg = device.getPersona(persona).getText(new Selector().setTextContains(text));
		report.report(msg);
	}
	
	@Test
	@TestProperties(name = "Get Full Text of UiObject \"${text}/${childClassName}\" index ${instance}", paramsInclude = { "text,persona,childClassName,instance,index" })
	public void getTextTextViewFather() throws Exception{
		String object = device.getPersona(persona).childByInstance(new Selector().setClassName(text).setIndex(index), new Selector().setClassName(childClassName), instance);
		String msg = device.getPersona(persona).getText(object);
		report.report(msg);
	}

	@Test
	@TestProperties(name = "Wait for UiObject by Text \"${text}\" on ${persona}", paramsInclude = { "text,persona,timeout,interval" })
	public void waitByText() throws Exception {
		final long start = System.currentTimeMillis();
		while (!device.getPersona(persona).exist(new Selector().setText(text))) {
			if (System.currentTimeMillis() - start > timeout) {
				report.report("Could not find UiObject with text " + text + " after "+timeout/1000+" sec.", report.FAIL);
			}
			Thread.sleep(interval);
		}
		
		/*Selector s = new Selector();
		s.setText(text);
		try {
			device.getPersona(persona).waitForExists(s, timeout);
		} catch (Exception e) {
			report.report("Could not find UiObject " + e.getMessage(),
					report.FAIL);
		}
		device.getPersona(persona).e*/

	}
	
	@Test
	@TestProperties(name = "Unlock Device by Swipe on ${persona}", paramsInclude={"persona"})
	public void unlockBySwipe() throws Exception{
		device.getPersona(persona).swipe(new Selector().setDescription("Slide area."), Direction.LEFT.getDir(), 20);
		device.getPersona(persona).pressKey("back");
	}
	//android.support.v4.view.ViewPager
	@Test
	@TestProperties(name = " Swipe by Class Name on ${persona}", paramsInclude={"persona,dir,text"})
	public void swipeByClassName() throws Exception{
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
			isPass = device.getPersona(persona).clickAndWaitForNewWindow(s,
					10000);
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
		device.getPersona(device.getForegroundPersona()).wakeUp();
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
	@TestProperties(name = "Reboot Recovery", paramsInclude = { "updateVersion" })
	public void rebootRecovery() throws Exception {
		if (updateVersion) {
			String version = runProperties
					.getRunProperty("adb.push.file.location");
			report.report("New Version File " + version);
			device.executeHostShellCommand("echo 'boot-recovery ' > /cache/recovery/command");
			device.executeHostShellCommand("echo '--update_package=" + version
					+ "'>> /cache/recovery/command");
		}
		device.rebootRecoveryDevice(Persona.PRIV, Persona.CORP);
		Thread.sleep(2000);
	}

	@Test
	@TestProperties(name = "Press on Button ${button} on ${persona}", paramsInclude = { "button,persona" })
	public void pressBtn() throws Exception {
		isPass = device.getPersona(persona).pressKey(button);
	}

	@Test
	@TestProperties(name = "Switch Persona to ${persona}", paramsInclude = { "persona" })
	public void switchPersona() throws Exception {
		Persona current = device.getForegroundPersona();
		if (current==persona){
			report.report("Persona "+persona+" is Already in the Foreground");
		}else{
			device.getPersona(current).click(5, 5);
			current = device.getForegroundPersona();
			if (current == persona){
				report.report("Switch to "+persona);
			}else{
				report.report("Could not Switch to "+persona,report.FAIL);
			}
		}
	}
	
	@Test
	@TestProperties(name = "Swipe Down Notification Bar on ${persona}",paramsInclude={"persona"})
	public void openNotificationBar() throws Exception{
		device.getPersona(persona).openNotification();
	}
	
	@Test
	@TestProperties(name = "Validate UiObject with Description \"${text}\" Exists",paramsInclude={"persona,text"})
	public void validateUiExistsByDesc() throws Exception{
		isExists = device.getPersona(persona).exist(new Selector().setDescription(text));
		runProperties.setRunProperty("isExists", String.valueOf(isExists));
	}

	@Test
	@TestProperties(name = "Start Logs of Test", paramsInclude = {})
	public void startLogging() throws Exception {
		device.initLogs();
	}

	@Test
	@TestProperties(name = "sleep", paramsInclude = {})
	public void sleep() throws Exception {
		sleep(10000);
	}

	@Test
	@TestProperties(name = "Set Device as Root", paramsInclude = {})
	public void setDeviceAsRoot() throws Exception {
		device.setDeviceAsRoot();
	}

	@Test
	@TestProperties(name = "Enter Password for ${persona}", paramsInclude = { "persona,value" })
	public void enterPassword() throws Exception {
		for (char c : value.toCharArray()) {
			device.getPersona(persona).click(
					new Selector().setText(String.valueOf(c)));
		}
	}

	@Test
	@TestProperties(name = "Factory Data Reset", paramsInclude = { "persona" })
	public void factoryDataReset() throws Exception {
		// device.getPersona(persona).pressKey("home");
		device.getPersona(persona).click(new Selector().setText("Settings"));
		String id = device.getPersona(persona).childByText(
				new Selector().setScrollable(true),
				new Selector().setText("Backup & reset"), "Backup & reset",
				true);
		device.getPersona(persona).click(id);
		device.getPersona(persona).click(
				new Selector().setText("Factory data reset"));
		device.getPersona(persona).click(new Selector().setText("Reset phone"));
		boolean pin = device.getPersona(persona).exist(
				new Selector().setText("Confirm your PIN"));
		if (pin) {
			device.getPersona(persona).setText(
					new Selector().setClassName("android.widget.EditText"),
					"1111");
			device.getPersona(persona).click(new Selector().setText("Next"));
		}
		device.getPersona(persona).click(
				new Selector().setText("Erase everything"));
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
		String result = device.uploadRomOldServer(serverHost,
				imgFile.getAbsolutePath(), version, adminToken, md5);
		// send the remote update command
		String command = device.remoteUpdateOldServer(result, serverUrl, deviceId, md5,
				fileSize, version, adminToken);
		runProperties.setRunProperty("download.command", command);
	}

	@Test
	@TestProperties(name = "Wait For \"${expectedLine}\" in Logcat", paramsInclude = { "expectedLine,timeout,interval" })
	public void waitforLineInLogcat() throws Exception {
		device.waitForLineInTomcat(expectedLine, timeout,interval);
	}
	
	@Test
	@TestProperties(name = "delete file ${remotefileLocation} from Device",paramsInclude={"remotefileLocation"})
	public void deleteFile () throws Exception{
		device.deleteFile(remotefileLocation);
	}
	
	@Test
	@TestProperties(name = "Clean Device DB after Mock Tucki",paramsInclude={})
	public void cleanDBfromTucki() throws Exception{
		String command = runProperties.getRunProperty("download.command");
		if (command==null){
			report.report("Cannot find download command",report.FAIL);
			throw new AnalyzerException();
		}
		String sqlQuueryString ="update tasks set synced=0,result='done' where payload='"+command+"';"; 
		report.report("SQL String = "+sqlQuueryString);
		device.sendSqlQuery(sqlQuueryString);
		device.executeHostShellCommand("agent_cli -s");
		device.sendSqlQuery("select * from tasks where payload='"+command+"';");
		
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

	private void validateDeviceStatus() throws Exception {
		boolean isOnline = device.isOnline();
		boolean personasUp = device.isPersonasAreOnline(Persona.PRIV,
				Persona.CORP);
		// validate the device is online and ready
		if (!isOnline) {
			try {
				// this function has a timeout in case the device cannot be
				// restarted
				device.validateDeviceIsOnline(Persona.PRIV, Persona.CORP);
				// TODO add crush report
				report.report("Getting online after Crush",
						ReportAttribute.BOLD);
			} catch (ConnectionException e) {
				report.report("Could not restart after reboot ", report.FAIL);
			}
		} else {
			// validate both personas are up, if not, wait get the run logs and
			// reboot the device (from host)
			if (!personasUp) {
				// TODO add expressions
				//device.rebootDevice(Persona.PRIV, Persona.CORP);
				// LogParserExpression ex = new LogParserExpression();
				// ex.setColor(Color.RED);
				// ex.setExpression("CRUSH");
				// ex.setNiceName("CRUSH");
				// expression = new LogParserExpression[] { ex };
				// LogParser logParser = new LogParser(expression);
				// device.getLogsOfRun(logParser);
				report.report("Perona Crush was detected!",
						ReportAttribute.BOLD);

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

	/*public Selector getSelector() {
		return selector;
	}*/

	public void setLocalLocation(File localLocation) {
		this.localLocation = localLocation;
	}

	public void setRemotefileLocation(String remotefileLocation) {
		this.remotefileLocation = remotefileLocation;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	/*@UseProvider(provider = jsystem.extensions.paramproviders.GenericObjectParameterProvider.class)
	public void setSelector(Selector selector) {
		this.selector = selector;
	}*/

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

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
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

}
