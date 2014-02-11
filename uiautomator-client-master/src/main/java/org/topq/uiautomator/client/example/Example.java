package org.topq.uiautomator.client.example;

import java.io.IOException;
import java.io.OutputStream;

import org.topq.uiautomator.AutomatorService;
import org.topq.uiautomator.Selector;
import org.topq.uiautomator.client.DeviceClient;

public class Example {

	static AutomatorService client;
	static boolean isrun = true;

	
	
	public static void main(String[] args) throws Exception {
		

//			String command= "/usr/bin/xterm"; 
//		Runtime rt = Runtime.getRuntime(); 	
//		Process pr = rt.exec(command);
//		pr = rt.exec("adb -s 04e855db9a5c0692 logcat");
//		Process pr =new ProcessBuilder("xterm", "-e", "adb -s 04e855db9a5c0692 logcat").start();

		//rxvt, eterm, aterm, gnome-terminal or konsole
		
//		String[] cmdss1= {"gnome-terminal","-x","adb", "-s", "04e855db9a5c0692" ,"logcat","-v","pidns"};
//	    Process proc = Runtime.getRuntime().exec(cmdss1, null);

		
	    AutomatorService client = DeviceClient.getUiAutomatorClient("http://localhost:3435");
	  
	    client.click(new Selector().setText("Settings"));
	    Thread.sleep(10000);
	    client.pressKey("home");

//			client.wakeUp();
//			client.setText(new Selector().setClassName("android.widget.EditText"), "1");
	    
	}
	
	
	
	

}