package com.cellrox.infra;

import java.io.IOException;

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
    
    private AdbController adbController;
    
    public void init() throws Exception {
    	super.init();
    	cellroxDevicesList = new CellRoxDevice[numberOfDevices];

    	adbController = AdbController.getInstance();
    	USBDevice[] devices = adbController.waitForDevicesToConnect(numberOfDevices);
    	
    	//initing each one of the devices
    	for(int index =0 ; index < devices.length ;index++){
    		cellroxDevicesList[index] = new CellRoxDevice(privePort + index, corpPort + index, otaFileLocation, devices[index].getSerialNumber());
    	}
    	
    	//to add to the summary properties
    	
    	getDevice(DeviceNumber.PRIMARY).addToTheSummarySystemProp();
//    	getDevice(DeviceNumber.PRIMARY).printKmsg();
    	
    	
    }
    

    public CellRoxDevice[] getCellroxDevicesList() {
    	return cellroxDevicesList;
    }
    
    public CellRoxDevice getDevice(DeviceNumber devNum) {
    	return cellroxDevicesList[devNum.ordinal()];
    }
    
    
//    public void close(){
//			try {
//				getDevice(DeviceNumber.PRIMARY).printKmsg();
//			} catch (IOException e) { }
//    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
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
    

}
