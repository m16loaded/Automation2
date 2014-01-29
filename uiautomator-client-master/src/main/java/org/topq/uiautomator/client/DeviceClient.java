package org.topq.uiautomator.client;

import java.net.MalformedURLException;
import java.net.URL;

import org.topq.uiautomator.AutomatorService;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.googlecode.jsonrpc4j.ProxyUtil;

public class DeviceClient {

	JsonRpcHttpClient client;
	AutomatorService deviceService;

	private DeviceClient(String serverUrl) throws MalformedURLException {
		URL serverURL = new URL(serverUrl);
		JsonRpcHttpClient client = new JsonRpcHttpClient(serverURL);
		client.setReadTimeoutMillis(30000);
		client.setConnectionTimeoutMillis(60 * 60 * 1000);
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
