package com.cellrox.infra;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.Reporter;
import jsystem.framework.report.Summary;
import jsystem.framework.system.SystemObjectImpl;
import junit.framework.TestListener;

import org.jsystemtest.mobile.core.AdbController;
import org.jsystemtest.mobile.core.device.USBDevice;

import com.cellrox.infra.enums.DeviceNumber;

public class CellRoxDeviceManager extends SystemObjectImpl {
	
    private int privePort = 3435;
    private int corpPort = 4321;
    private String otaFileLocation;
    private CellRoxDevice[] cellroxDevicesList;
    private int numberOfDevices = 2;
    private static boolean isInit = false;
    private String user, password;
    //the runStatus String is for the wqanting for the run etc : full/debug
    private String runStatus = "debug";
    
    private AdbController adbController;
    
    public void init() throws Exception {
    	super.init();
    	    	
    	
    	report.report("Initing CellRoxDeviceManager");
    	if(!isInit) {
    		//init test report
    		Summary.getInstance().clearAllProperties();
    		 
			//adding the start time to summary
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss");
    		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			Summary.getInstance().setProperty("Start_Time", sdf.format(cal.getTime()));
    		
	    	cellroxDevicesList = new CellRoxDevice[numberOfDevices];
	
	    	adbController = AdbController.getInstance();
	    	report.report("Wait for "+numberOfDevices +" to connect.");
	    	USBDevice[] devices = adbController.waitForDevicesToConnect(numberOfDevices);
	    	
	    	//initing each one of the devices
	    	for(int index =0 ; index < devices.length ;index++){
	    		cellroxDevicesList[index] = new CellRoxDevice(privePort + index, corpPort + index, otaFileLocation, devices[index].getSerialNumber(), user, password, runStatus);
	    	}
	    	
	    	//to add to the summary properties
		    getDevice(DeviceNumber.PRIMARY).addToTheSummarySystemProp();
		   
		    isInit = true;
    	}
    	
    }
    

    public CellRoxDevice[] getCellroxDevicesList() {
    	return cellroxDevicesList;
    }
    
    public CellRoxDevice getDevice(DeviceNumber devNum) {
    	return cellroxDevicesList[devNum.ordinal()];
    }
    
    
    

	public String getOtaFileLocation() {
		return otaFileLocation;
	}

	public void setOtaFileLocation(String otaFileLocation) {
		this.otaFileLocation = otaFileLocation;
	}

	public int getCorpPort() {
		return corpPort;
	}

	public void setCorpPort(int corpPort) {
		this.corpPort = corpPort;
	}

	public int getPrivePort() {
		return privePort;
	}
 
	public void setPrivePort(int privePort) {
		this.privePort = privePort;
	}

	public int getNumberOfDevices() {
		return numberOfDevices;
	}

	public void setNumberOfDevices(int numberOfDevices) {
		this.numberOfDevices = numberOfDevices;
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

	public String getRunStatus() {
		return runStatus;
	}

	public void setRunStatus(String runStatus) {
		this.runStatus = runStatus;
	}
    

}
