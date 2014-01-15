package org.topq.uiautomator.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import org.topq.uiautomator.AutomatorService;
import org.topq.uiautomator.NotImplementedException;
import org.topq.uiautomator.ObjInfo;
import org.topq.uiautomator.Point;
import org.topq.uiautomator.Selector;

import com.android.uiautomator.core.UiObjectNotFoundException;
import com.googlecode.jsonrpc4j.JsonRpcError;
import com.googlecode.jsonrpc4j.JsonRpcErrors;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.googlecode.jsonrpc4j.ProxyUtil;

public class DeviceClient {

	JsonRpcHttpClient client;
	AutomatorService deviceService;

	private DeviceClient(String serverUrl) throws MalformedURLException {
		URL serverURL = new URL(serverUrl);
		JsonRpcHttpClient client = new JsonRpcHttpClient(serverURL);
		deviceService = ProxyUtil.createClientProxy(
				getClass().getClassLoader(), AutomatorService.class, client);
		
	}
	
	public static AutomatorService getUiAutomatorClient(String serverUrl) throws MalformedURLException{
		return new DeviceClient(serverUrl+"/jsonrpc/0").getDeviceService();
		
	}

	public AutomatorService getDeviceService() {
		return deviceService;
	}

	

}
