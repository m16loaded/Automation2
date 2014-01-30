package com.cellrox.infra;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsystem.extensions.analyzers.text.FindText;
import jsystem.extensions.analyzers.text.GetTextCounter;
import jsystem.extensions.analyzers.text.TextNotFound;
import jsystem.framework.report.Reporter;
import jsystem.framework.report.Summary;
import jsystem.framework.report.Reporter.ReportAttribute;
import jsystem.framework.system.SystemObjectImpl;
import jsystem.utils.FileUtils;

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
import com.cellrox.infra.enums.Persona;
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
        // public AutomatorService[] uiClient = new AutomatorService[2];

        List<AutomatorService> uiClient = Collections.synchronizedList(new ArrayList<AutomatorService>());
        private ArrayList<String> pidArray = new ArrayList<String>();
        private boolean afterCrush = false;
        private ExecutorService executor;
        private boolean isrun = true;
        //the otaFileLocation is for the jenkins/the local run to know where is the ota file located and what is it name
        private String otaFileLocation;
        private long defaultCliTimeout = 90000;
        private long upTime;

        
        public CellRoxDevice(int privePort,int corpPort, String otaFileLocation, String serialNumber) throws Exception {
        	
        	this.privePort = privePort;
        	this.corpPort = corpPort;
        	this.otaFileLocation = otaFileLocation;
        	this.deviceSerial = serialNumber;
        		
        	adbController = AdbController.getInstance();
        	device = adbController.waitForDeviceToConnect(serialNumber);
            cli = new AdbConnection("127.0.0.1", getUser(), getPassword(), serialNumber);
            cli.setExitTimeout(60*1000);
            cli.connect();
            
            setDeviceAsRoot();
            isDeviceConnected();
            
            // validate that syslog is enabled
            String status = device.getProperty("persist.service.syslogs.enable");
            // if not enabled enable it...
            if (status == null) {
                    report.report("no persist.service.syslogs.enable"/*, report.WARNING*/);
            } else if (status.equals("0")) {
                    device.executeShellCommand("setprop persist.service.syslogs.enable 1 ");
            }
            upTime = getCurrentUpTime();
        	
        	
        }
        
        
        
        public long getCurrentUpTime() throws Exception {
        	String upTime = "";
        	cli.connect();
        	executeCliCommand("adb -s "+ deviceSerial +" shell");
        	executeCliCommand("uptime");
        	upTime = cli.getTestAgainstObject().toString().replace("uptime", "").replace("shell@mako:/ $", "").replace("root@mako:/ #", "").trim();
        	
        	
        	String upTimeTry = upTime.split(",")[0].replace("up time: ", "").trim().replace(":", "");
        	if (upTimeTry.contains("days")) {
        		upTime = upTime.split(",")[1].replace("up time: ", "").trim().replace(":", "");
        	}
        	else {
        		upTime = upTime.split(",")[0].replace("up time: ", "").trim().replace(":", "");
        	}
        	
//        	Pattern pattern = Pattern.compile(expectedLine);
//    	    Matcher matcher = pattern.matcher(cli.getTestAgainstObject().toString());
//
//    	    if(matcher.find()) {
//    	        	report.report("Find : " + expectedLine + " in : " +res);
//    	        	String number = matcher.group(1);
        	
        	
        	
        	
        	
        	cli.disconnect();
        	
        	return Long.parseLong(upTime);
        }
        /**
         * @throws Exception 
         * 
         * */
        public void addToTheSummarySystemProp() throws Exception {
        	
        	cli.connect();
        	executeCliCommand("adb -s "+getDeviceSerial() +" root");
        	executeCliCommand("adb -s "+getDeviceSerial() +" shell");
        	executeCliCommand("getprop | fgrep ro.build.version.sdk");
        	String propToParse = cli.getTestAgainstObject().toString();
        	propToParse =propToParse.replace("getprop | fgrep ro.build.version.sdk", "");
        	propToParse =propToParse.replace("root@mako:/ #", "").replace("]", "").replace("[", "");
        	propToParse =propToParse.trim();
        	Summary.getInstance().setProperty("Build sdk version", propToParse.split(":")[1]);
        	executeCliCommand("getprop | fgrep ro.build.display.id");
        	propToParse = cli.getTestAgainstObject().toString();
        	propToParse =propToParse.replace("getprop | fgrep ro.build.display.id", "");
        	propToParse =propToParse.replace("root@mako:/ #", "").replace("]", "").replace("[", "");
        	propToParse =propToParse.trim();
        	Summary.getInstance().setProperty("Build display id", propToParse.split(":")[1]);
        	executeCliCommand("getprop | fgrep ro.build.date]");
        	propToParse = cli.getTestAgainstObject().toString();
        	propToParse =propToParse.replace("getprop | fgrep ro.build.date]", "");
        	propToParse =propToParse.replace("root@mako:/ #", "").replace("]", "").replace("[", "");
        	propToParse =propToParse.trim();
        	Summary.getInstance().setProperty("Build date", propToParse.split(":")[1]);
        	
        	cli.disconnect();
        }
        
        
        /**
         *	print out the kmesg 
         * @throws IOException 
         * */
        public void printLastKmsg() throws IOException{
        	
        	try {
        		report.startLevel("click here for kmsg logger");
        		cli.connect();
        		//this is a wanted exception!
        		executeCliCommand("adb -s "+ getDeviceSerial()+" shell cat /proc/last_kmsg" , true , 210 * 1000 , true);
        	
        		cli.disconnect();
        	} catch (Exception e) { }
        	finally {
        		report.report(cli.getTestAgainstObject().toString());
        		report.stopLevel();
        	}
        	
        }
        
        
        
        /**
         * This function kills all the automation running processes:
         * 	1. pkill uiautomator
         * 	2. ps | grep busybox
         * 	3. kill ${process number of the busybox's father}
         * 	4. pkill busybox
         * */
        public void killAllAutomaionProcesses() throws Exception {
        	
        	report.report("Killing all the automation processes.");
        	String expectedLine = "root\\s*(\\d*)\\s*(\\d*)\\s*(\\d*)\\s*(\\d*)\\s*(\\S*)\\s*(\\S*)\\s*S\\s*";
        	cli.connect();
            executeCliCommand("adb -s " + getDeviceSerial() + " root");
            executeCliCommand("adb -s " + getDeviceSerial() + " shell");
        	//kill uiautomator
            executeCliCommand("pkill uiautomator");
          //kill the sh script, of the while
            executeCliCommand("ps | grep busybox");
            String retData = cli.getTestAgainstObject().toString();
            retData = retData.replace("\n", "");
            retData = retData.replace("\r", "");
            String []retDataArr = retData.split("busybox");
            for(int index = 0 ;  index < retDataArr.length ; index++) {
            	Pattern pattern = Pattern.compile(expectedLine);
        	    Matcher matcher = pattern.matcher(retDataArr[index]);
        	    if(matcher.find()) {
        	        	String fatherProcessNumber = matcher.group(2);
        	        	executeCliCommand("kill "+fatherProcessNumber);
        	    }
            }
        	  //kill the busybox
        	executeCliCommand("pkill busybox");
        	report.report("All the processes are down.");
        	cli.disconnect();
        }
        
/*        public CellRoxDevice() throws Exception {
                adbController = AdbController.getInstance();
                // find all devices
                USBDevice[] devices = adbController.waitForDevicesToConnect(1);
                // set the device serial number by its index
                setDeviceSerial(devices[deviceIndex].getSerialNumber());
                device = adbController.waitForDeviceToConnect(getDeviceSerial());
                cli = new AdbConnection("127.0.0.1", getUser(), getPassword());
                cli.setExitTimeout(60*1000);
                cli.connect();

        }*/

  /*      @Override
        public void init() throws Exception {
//                super.init();
//                getDeviceFromAdb();
//                cli = new AdbConnection("127.0.0.1", "topq", "Aa123456");
//                cli.connect();
                // set device as root
                if (deviceAsRootOnInit) {
                        setDeviceAsRoot();
                        isDeviceConnected();
                }
                // validate that syslog is enabled
                String status = device.getProperty("persist.service.syslogs.enable");
                // if not enabled enable it...
                if (status == null) {
                        report.report("no persist.service.syslogs.enable", report.WARNING);
                } else if (status.equals("0")) {
                        device.executeShellCommand("setprop persist.service.syslogs.enable 1 ");
                }
        }
*/
        

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
         *
         * @param privPort
         * @param corpPort
         * @throws Exception
         */
        public void configureDeviceForAutomation(boolean runServer) throws Exception {
                
        		killAllAutomaionProcesses();
        	
                report.startLevel("Configure Device For Automation");
                
                cli.connect();
                executeCliCommand("adb -s " + getDeviceSerial() + " root");
                executeCliCommand("adb -s " + getDeviceSerial() + " shell");
                executeCliCommand("rm /data/containers/priv/data/data/noipin_priv");
                executeCliCommand("rm /data/containers/priv/data/data/noipout_priv");
                executeCliCommand("rm /data/containers/corp/data/data/noipin_corp");
                executeCliCommand("rm /data/containers/corp/data/data/noipout_corp");
                executeCliCommand("mkfifo /data/containers/priv/data/data/noipin_priv");
                executeCliCommand("mkfifo /data/containers/priv/data/data/noipout_priv");
                executeCliCommand("mkfifo /data/containers/corp/data/data/noipin_corp");
                executeCliCommand("mkfifo /data/containers/corp/data/data/noipout_corp");
                Thread.sleep(200);
                cli.switchToPersona(Persona.PRIV);
                /**
                 * For QA mode this line will open a mock server
                 * executeShellCommand(String
                 * .format("busybox nc -l -p %d -e /system/bin/sh&",privPort));
                 */
                Thread.sleep(200);
                if (runServer) {
                        executeCliCommand("uiautomator runtest uiautomator-stub.jar bundle.jar -c com.github.uiautomatorstub.Stub &");
                }
                executeCliCommand("while (true) do busybox nc localhost 9008 < /data/data/noipin_priv > /data/data/noipout_priv;done &");
                cli.switchToHost();
                executeCliCommand("adb -s " + getDeviceSerial() + " root");
                cli.switchToPersona(Persona.CORP);
                /**
                 * For QA mode this line will open a mock server
                 * executeShellCommand(String
                 * .format("busybox nc -l -p %d -e /system/bin/sh&",corpPort));
                 */
                Thread.sleep(200);
                if (runServer) {
                        executeCliCommand("uiautomator runtest uiautomator-stub.jar bundle.jar -c com.github.uiautomatorstub.Stub &");
                }
                executeCliCommand("while (true) do busybox nc localhost 9008 < /data/data/noipin_corp > /data/data/noipout_corp;done &");
                cli.switchToHost();
                executeCliCommand("adb -s " + getDeviceSerial() + " root");
                Thread.sleep(200);
                executeCliCommand(String
                                .format("while (true) do busybox nc -l -p %d > /data/containers/priv/data/data/noipin_priv < /data/containers/priv/data/data/noipout_priv;done &",
                                                getPrivePort()));
                Thread.sleep(200);
                executeCliCommand(String
                                .format("while (true) do busybox nc -l -p %d > /data/containers/corp/data/data/noipin_corp < /data/containers/corp/data/data/noipout_corp;done &",
                                                getCorpPort()));
        
                report.stopLevel();
                cli.disconnect();
        }


        /**
         * Install APK on device
         *
         * @param apkLocation
         * on local file system
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
         * in case of timeout on the connection.
         * @throws AdbCommandRejectedException
         * if adb rejects the command
         * @throws IOException
         */
        public void rebootDevice(boolean isEncrypted, Persona... personas) throws Exception {
                rebootDevice(5 * 60 * 1000, isEncrypted, personas);
        }

        public void rebootDevice(int timeout, boolean isEncrypted, Persona... personas) throws Exception {
                long currentTime = System.currentTimeMillis();
                try {
                        sync();
                        device.reboot();
                        report.report("reboot command was sent");
                        Thread.sleep(5000);
                } catch (Exception e) {
                        // igonore
                }
                
                validateDeviceIsOnline(currentTime, timeout, isEncrypted, personas);
                setDeviceAsRoot();
                upTime = getCurrentUpTime();
        }
        

        /**
         * Reboot Recovery the device, waits until personas are up
         *
         * @throws TimeoutException
         * in case of timeout on the connection.
         * @throws AdbCommandRejectedException
         * if adb rejects the command
         * @throws IOException
         */
        public void rebootRecoveryDevice(boolean isEncrypted, Persona... personas) throws Exception {
                sync();
                device.executeShellCommand("reboot recovery");
                report.report("reboot recovery command was sent");
                Thread.sleep(1000);
                validateDeviceIsOnline(isEncrypted, personas);
//              device = adbController.waitForDeviceToConnect(getDeviceSerial());
                upTime = getCurrentUpTime();
        }

        /**
         * Executes shell command on host
         *
         * @param command
         * @throws Exception
         */
        public void executeHostShellCommand(String shellCommand) throws Exception {
                device.executeShellCommand(shellCommand);
        }

        public void validateDeviceIsOnline(boolean isEncrypted, Persona... personas) throws Exception {
                validateDeviceIsOnline(System.currentTimeMillis(), 10 * 60 * 1000, isEncrypted, personas);
        }

        public void validateDeviceIsOnline(long beginTime, int timeout, boolean isEncrypted, Persona... personas) throws Exception {
                vlidateDeviceIsOffline(personas);
                Thread.sleep(2000);
                device = adbController.waitForDeviceToConnect(getDeviceSerial());
                System.out.println("finsh to wait");
                if(isEncrypted) {
                        validateEncryptedPersonasAreOnline(beginTime, timeout, personas);
                        Thread.sleep(3000);
                        clickOnEncryptedDeviceAfterReboot();
                        validatePersonasAreOnline(beginTime, timeout, personas);
                }
                else {
                        validatePersonasAreOnline(beginTime, timeout, personas);
                }
        }
        
        /**
         * This function insert to the encrypted screen the password "1111" and press enter.
         * This connection is made by the adb connection;
         * */
        public void clickOnEncryptedDeviceAfterReboot() throws Exception {
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
                executeCliCommand("input keyevent 66");//this is the enter
                Thread.sleep(1000);
                cli.disconnect();
        }

        /**
         * validate the device is online by first waiting for the requested
         * persona(s) to shutdown
         *
         * @param personas
         * @throws Exception
         */
        public void vlidateDeviceIsOffline(Persona... personas) throws Exception {
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
                                        } else {
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
                boolean online = false;
                public void validatePersonasAreOnline(long beginTime, int timeout, Persona... personas) throws Exception {
                String result = null;
                int found = 0;
                while (online != true) {

                        if (timeout < System.currentTimeMillis() - beginTime) {
                                report.report("Fail due to timeout in validating the personas are on.", Reporter.FAIL);
                                return;
                        }

                        try {
                                result = device.executeShellCommand("cell list state");
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
                report.report("device is online, its took : " + ((float) (System.currentTimeMillis() - beginTime)) / 1000
                                + " seconds.");
                Thread.sleep(7000);
        }
        
        
        /**
         * validate the requested persona(s) are online with cell list command
         *
         * @param personas
         * @throws Exception
         */
		public void validateEncryptedPersonasAreOnline(long beginTime, int timeout, Persona... personas) throws Exception {
			boolean online = false;
			String result = null;
			while (online != true) {
	
				Thread.sleep(800);
				if (timeout < System.currentTimeMillis() - beginTime) {
					report.report("Fail due to timeout in validating the personas are on.", Reporter.FAIL);
					return;
				}
	
				try {
					result = device.executeShellCommand("cell list state");
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
			report.report("device is online, its took : " + ((float) (System.currentTimeMillis() - beginTime)) / 1000
					+ " seconds.");
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
                if (device.isOnline() && !device.isOffline()) {
                        return true;
                } else {
                        return false;
                }
        }

        /**
         * Push file to the device
         *
         * @param fileLocation
         * file location on the device
         * @param fileName
         * file name on the device
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
         * the remote item(s) to pull
         * @param localPath
         * The local destination. If the entries count is > 1 or if the
         * unique entry is a folder, this should be a folder.
         * @throws SyncException
         * @throws IOException
         * @throws TimeoutException
         *
         * @see FileListingService.FileEntry
         * @see #getNullProgressMonitor()
         */
        public void pullFileFromDevice(String remoteFilepath, String localFilename) throws Exception {
                try {
                        device.pullFileFromDevice(remoteFilepath, localFilename);
                } catch (Exception e) {
                        report.report("Error while pulling the file " + remoteFilepath + " to " + localFilename + "\n" + e,
                                        Reporter.FAIL);
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
                // port forward
//                while (!device.isOnline()) {
//                }
                report.report("Device is online");
                setPortForwarding(privePort, privePort);
                setPortForwarding(corpPort, corpPort);

                // connect client to server
                uiClient.add(Persona.PRIV.ordinal(), DeviceClient.getUiAutomatorClient("http://localhost:" + privePort));
                uiClient.add(Persona.CORP.ordinal(), DeviceClient.getUiAutomatorClient("http://localhost:" + corpPort));

                
                /* executor = Executors.newFixedThreadPool(1);
                Runnable worker = new Runnable() {
                        public void run() {
                                while (isrun) {
                                        try {
                                                for (AutomatorService client : uiClient) {
                                                        client.count(new Selector());
                                                }
                                                Thread.sleep(500);
                                        } catch (InterruptedException e) {
                                                e.printStackTrace();
                                        }
                                }
                        }
                };
                executor.execute(worker);
                report.report("Keep Alive");
                Thread.sleep(2000);
                executor.shutdown();*/

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
                return Persona.valueOf(perona.toUpperCase());
        }

        // public void openNotificationBar(Persona persona) throws Exception{
        // uiClient[persona.ordinal()].
        // }

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
        public void getLogsOfRun(LogParser parser) throws Exception {
                String userHome = System.getProperty("user.home");
                // get the files
                device.pullFileFromDevice("/data/agent/syslogs/system_kmsg.txt", userHome + "/system_kmsg.txt");
                device.pullFileFromDevice("/data/agent/syslogs/system_logcat-radio.txt", userHome + "/system_logcat-radio.txt");
                device.pullFileFromDevice("/data/agent/syslogs/system_logcat.txt", userHome + "/system_logcat.txt");
                File logcat = new File(userHome + "/system_logcat.txt");
                File kmsg = new File(userHome + "/system_kmsg.txt");
                File radioLogcat = new File(userHome + "/system_logcat-radio.txt");
                // set logs to validate
                parser.setLogs(logcat, radioLogcat, kmsg);
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

        /**
         * set root persmission on deivce and that there is no unknown device
         * (?????) with adb root command - try to o it for 3 times.
         *
         * @throws Exception
         */
        public void setDeviceAsRoot() throws Exception {

                String retString = "";
                final int numberOfRootAttempts = 3;

                cli.connect();
                for (int i = 0; i < numberOfRootAttempts; i++) {
                        executeCliCommand("adb -s " + getDeviceSerial() + " root", true);
                        retString = cli.getTestAgainstObject().toString();
                        if (retString.contains("error: device unauthorized.")) {
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
                if (retString.contains("error: device unauthorized.")) {
                        cli.disconnect();
                        throw new Exception("error: device unauthorized, couldn't make adb root.");
                }
                Thread.sleep(2000);
                cli.disconnect();
        }

        public String uploadRomOldServer(String serverHost, String imgFile, String version, String adminToken, String md5sum)
                        throws Exception {
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

        public String remoteUpdateOldServer(String result, String serverUrl, String deviceId, String md5, String fileSize,
                        String version, String adminToken) throws Exception {

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
         * (millis)
         * @param interval
         * (millis)
         * @return
         * @throws Exception
         */
        public boolean waitForLineInTomcat(String line, long timeout, long interval) throws Exception {
                final long start = System.currentTimeMillis();
                // TODO add RegEx support
                while (!getLogcatLastLines(100).contains(line)) {
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
                cli.connect();
                executeCliCommand("adb -s " + getDeviceSerial() + " shell rm " + file);
                cli.analyze(new TextNotFound("No such file or directory"));
                cli.analyze(new TextNotFound("Permission denied"));
                cli.disconnect();
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
         * - the wanted cli command after the adb shell
         * @param expression
         * - the wanted text/expression to find
         * @param isRegularExpression
         * - is this expression is a regular expression
         * */
        public void validateExpressionCliCommand(String cliCommand, String expression, boolean isRegularExpression, boolean isShell)
                        throws Exception {
        	
                cli.connect();
                if(isShell)
                        executeCliCommand("adb -s " + getDeviceSerial() + " shell");
                executeCliCommand(cliCommand);
                FindText findText = new FindText(expression, isRegularExpression);
                cli.analyze(findText);
                cli.disconnect();

        }
        
        public void validateExpressionCliCommand(String cliCommand, String expression, boolean isRegularExpression)
                        throws Exception {
                validateExpressionCliCommand(cliCommand, expression, isRegularExpression, true);
        }

        /**
         * find the expression to find after cli command in the adb shell
         *
         * @param cliCommand
         * - the wanted cli command after the adb shell
         * @param expression
         * - the wanted text/expression to find
         * @param isRegularExpression
         * - is this expression is a regular expression
         * @param persona
         * - the cell console and the wanted persona
         * */
        public void validateExpressionCliCommand(String cliCommand, String expression, boolean isRegularExpression,
                        Persona persona) throws Exception {

                
//                cli.connect();
//                executeCliCommand("adb shell");
//                if (persona == Persona.PRIV) {
//                        executeCliCommand("cell console priv");
//                } else {
//                        executeCliCommand("cell console corp");
//                }
                //executeCliCommand("");
                //executeCliCommand(cliCommand);
                try {
                        report.report(cliCommand);
                        String responce = getPersona(persona).excuteCommand(cliCommand);
                        report.report(responce);
                        if(isRegularExpression) {
                                
                         Pattern pattern = Pattern.compile(expression);
                 Matcher matcher = pattern.matcher(responce);
        
                 if(matcher.find())
                         report.report("Find : " + expression + " in : " +responce);
                 else
                         report.report("Couldnt find : " + expression + " in : " +responce ,Reporter.FAIL);
                        }
                        else {
                                if(!responce.contains(expression))
                                        report.report("Couldnt find : " + expression + " in : " +responce ,Reporter.FAIL);
                                else
                                        report.report("Find : " + expression + " in : " + responce);
                        }
                }
                catch(Exception e) {
                        report.report("Error : " +e.getMessage(),Reporter.FAIL);
                }
                
//                FindText findText = new FindText(expression, isRegularExpression);
//                cli.analyze(findText);
//                cli.switchToHost();
//                cli.disconnect();

        }
        
        public void validateExpressionCliCommandCell(String cliCommand, String expression, boolean isRegularExpression,
                        Persona persona) throws Exception {

                
                cli.connect();
                executeCliCommand("adb -s " + getDeviceSerial() + " shell");
                if (persona == Persona.PRIV) {
                        executeCliCommand("cell console priv");
                        executeCliCommand(" ");
                        executeCliCommand(" ");
                        executeCliCommand(" ");
                        executeCliCommand(" ");
                        executeCliCommand(" ");
                } else {
                        executeCliCommand("cell console corp");
                        executeCliCommand(" ");
                        executeCliCommand(" ");
                        executeCliCommand(" ");
                        executeCliCommand(" ");
                        executeCliCommand(" ");
                }
                executeCliCommand("");
                executeCliCommand(cliCommand);
                
                FindText findText = new FindText(expression, isRegularExpression);
                cli.analyze(findText);
                cli.switchToHost();
                cli.disconnect();

        }

        /**
         * get the path of the application and push it to the priv and the corp
         *
         * @param appFullPath
         * - the application full path on the local cpu
         * */
        public void pushApplicationToDevice(String appFullPath) throws Exception {

                cli.connect();
                report.report("about to do : " + "adb -s " + getDeviceSerial() + " push " + appFullPath + " /data/containers/corp/data/app/");
                executeCliCommand("adb -s " + getDeviceSerial() + " push " + appFullPath + " /data/containers/corp/data/app/");
                report.report("about to do : adb -s " + getDeviceSerial() + " push " + appFullPath + " /data/containers/priv/data/app/");
                executeCliCommand("adb -s " + getDeviceSerial() + " push " + appFullPath + " /data/containers/priv/data/app/");
                cli.disconnect();

        }

        public void pushApplicationToPriv(String appFullPath) throws Exception {

                cli.connect();
                report.report("about to do : adb -s " + getDeviceSerial() + " push " + appFullPath + " /data/containers/priv/data/app/");
                executeCliCommand("adb -s " + getDeviceSerial() + " push " + appFullPath + " /data/containers/priv/data/app/");
                cli.disconnect();

        }
        
        public void pushApplicationToCorp(String appFullPath) throws Exception {

                cli.connect();
                report.report("about to do : " + "adb -s " + getDeviceSerial() + " push " + appFullPath + " /data/containers/corp/data/app/");
                executeCliCommand("adb -s " + getDeviceSerial() + " push " + appFullPath + " /data/containers/corp/data/app/");
                cli.disconnect();

        }
        private void executeCliCommand(String Command) throws Exception {
                executeCliCommand(Command, false);
                Thread.sleep(500);
        }

        private void executeCliCommand(String Command, boolean silent) throws Exception {
        	executeCliCommand(Command, silent , defaultCliTimeout);
        }
        private void executeCliCommand(String Command, boolean silent, long timeout) throws Exception {
        	executeCliCommand(Command, silent , defaultCliTimeout, false);
        }
        
        private void executeCliCommand(String Command, boolean silent, long timeout, boolean ignoreErrors) throws Exception {

                CliCommand cmd = new CliCommand(Command);
                cmd.setSilent(silent);
                cmd.setAddEnter(true);
                cmd.setTimeout(timeout);
                cmd.setIgnoreErrors(ignoreErrors);
                cli.handleCliCommand(Command, cmd);
                // savePID();
        }

        /**
         * The following functions click on the button by the x y cordinate
         * */
        public void clickOnSelectorByUi(ObjInfo info, Persona persona) throws UiObjectNotFoundException {

                int horizon = (info.getVisibleBounds().getRight() + info.getVisibleBounds().getLeft()) / 2;
                int vertical = (info.getVisibleBounds().getTop() + info.getVisibleBounds().getBottom()) / 2;
                report.report("About to click point at : " + horizon + "," + vertical);
                getPersona(persona).click(horizon, vertical);
        }

        public void clickOnSelectorByUi(String id, Persona persona) throws UiObjectNotFoundException {
                ObjInfo obj = getPersona(persona).objInfo(id);
                clickOnSelectorByUi(obj, persona);
        }

        public void clickOnSelectorByUi(Selector s, Persona persona) throws UiObjectNotFoundException {
                String id = getPersona(persona).getUiObject(s);
                clickOnSelectorByUi(id, persona);
        }
        
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
    				report.report("Could not Switch to " + persona, Reporter.FAIL);
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
    		}
    		catch(Exception e) {
    			report.report("Error in unlocking the device.");
    		}
    	}
        
        
        /**
         * This function runs apply update script
         * */
        public void runApplayUpdateScript(String applyUpdateLocation, String otaFileLocation) throws Exception {
                
                cli.connect();
                cli.setExitTimeout(10*60*1000);
                executeCliCommand("adb -s " + getDeviceSerial() + " root");
                executeCliCommand("su -" , false);
                executeCliCommand("");
                Thread.sleep(1500);
//                try {
                        executeCliCommand(applyUpdateLocation + " " + otaFileLocation);
//                }
//                catch(Exception e) {
//                }
                Thread.sleep(100*1000);
                cli.disconnect();
        }
        
        
        @Deprecated
        public void reportToLogcat(String TAG, String msg) {
                device.reportToLogcat(TAG, msg);
        }

        /**
         * This is the System Object "destructor"<br>
         * The close function will close all the process that are open for the
         * automation use.<br>
         *
         * <b>please note: </b>this function only invokes on the end of the
         * automation run and not between tests or building blocks
         *
         */
        @Override
        public void close() {
                // TODO : need to think if we want to stop by PID (means we will need to
                // access each persona) or by process name
                // cli.disconnect();
                isrun = false;
                if (executor != null) {
                        while (!executor.isTerminated()) {
                        }
                }
                stopAllActiveAutomationProecess();

                super.close();
        }

        private void stopAllActiveAutomationProecess() {
                try {
//                        String a = device.executeShellCommand("killall busybox");
//                        device.executeShellCommand("killall uiautomator");
                } catch (Exception e) {
                        report.report("Error in closing automation processes", Reporter.FAIL);
                }
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

        /**
         * @return the otaFileLocation
         */
        public String getOtaFileLocation() {
                return otaFileLocation;
        }

        /**
         * @param otaFileLocation the otaFileLocation to set
         */
        public void setOtaFileLocation(String otaFileLocation) {
                this.otaFileLocation = otaFileLocation;
        }

		/**
		 * @return the upTime
		 */
		public long getUpTime() {
			return upTime;
		}

		/**
		 * @param upTime the upTime to set
		 */
		public void setUpTime(long upTime) {
			this.upTime = upTime;
		}

}