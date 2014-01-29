package org.topq.uiautomator.client.example;

import java.net.MalformedURLException;
import java.rmi.RemoteException;

import org.topq.uiautomator.AutomatorService;
import org.topq.uiautomator.Selector;
import org.topq.uiautomator.client.DeviceClient;

import com.android.uiautomator.core.UiObjectNotFoundException;

public class Example {

	static AutomatorService client;
	static boolean isrun = true;

	public static void main(String[] args) throws Exception {
		try {
			client = DeviceClient.getUiAutomatorClient("http://localhost:4321");
			
			client.wakeUp();
//			client.setText(new Selector().setClassName("android.widget.EditText"), "1");
			

			/*
			 * ScheduledExecutorService scheduledExecutorService =
			 * Executors.newScheduledThreadPool(1);
			 * 
			 * 
			 * ScheduledFuture scheduledFuture =
			 * scheduledExecutorService.schedule(new Callable() { public Object
			 * call() throws Exception { client.ping(); return "Called!"; } },
			 * 500, TimeUnit.MILLISECONDS);
			 */

//			ExecutorService executor = Executors.newFixedThreadPool(1);
//			Runnable worker = new Runnable() {
//
//				public void run() {
//				while (isrun) {
//						client.ping();
//						try {
//							Thread.sleep(500);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
//
//				}
//			};
//			executor.execute(worker);
//			executor.shutdown();
//			isrun = false;
//			while (!executor.isTerminated()) {
//			}

			// create new selector for app drawer
			Selector selector = new Selector();
			selector.setDescription("Apps");
			selector.setClassName("android.widget.TextView");
			// click to open app drawer
			client.click(selector);
			client.pressKey("home");
			client.click(selector);
			System.out.println("about to sleep");
			Thread.sleep(10000);
			System.out.println("done sleeping");
			client.pressKey("home");
			// press home
			client.pressKey("home");
		} catch (MalformedURLException e) {
			System.out.println("Error while trying to connect to server");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			System.out.println("Could not send command");
		} catch (UiObjectNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Could not find object");
		}
	}

}
