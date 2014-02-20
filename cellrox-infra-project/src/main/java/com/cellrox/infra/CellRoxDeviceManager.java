package com.cellrox.infra;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import jsystem.framework.report.Summary;
import jsystem.framework.system.SystemObjectImpl;

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
    
    private AdbController adbController;
    
    public void init() throws Exception {
    	super.init();
    	
    	
    	report.report("Initing CellRoxDeviceManager");
    	if(!isInit) {
    		
    		 Summary.getInstance().clearAllProperties();
    		 
			//adding the start time to summary
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			Summary.getInstance().setProperty("Start_Time", sdf.format(cal.getTime()));
    		
	    	cellroxDevicesList = new CellRoxDevice[numberOfDevices];
	
	    	adbController = AdbController.getInstance();
	    	report.report("Wait for "+numberOfDevices +" to connect.");
	    	USBDevice[] devices = adbController.waitForDevicesToConnect(numberOfDevices);
	    	
	    	//initing each one of the devices
	    	for(int index =0 ; index < devices.length ;index++){
	    		cellroxDevicesList[index] = new CellRoxDevice(privePort + index, corpPort + index, otaFileLocation, devices[index].getSerialNumber(), user, password);
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
	 * @return the corpPort
	 */
	public int getCorpPort() {
		return corpPort;
	}
	/**
	 * @param corpPort the corpPort to set
	 */
	public void setCorpPort(int corpPort) {
		this.corpPort = corpPort;
	}
	/**
	 * @return the privePort
	 */
	public int getPrivePort() {
		return privePort;
	}
	/**
	 * @param privePort the privePort to set
	 */
	public void setPrivePort(int privePort) {
		this.privePort = privePort;
	}

	/**
	 * @return the numberOfDevices
	 */
	public int getNumberOfDevices() {
		return numberOfDevices;
	}

	/**
	 * @param numberOfDevices the numberOfDevices to set
	 */
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
    

}
