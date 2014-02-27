package com.github.uiautomatorstub;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import android.os.Build;
import android.os.Environment;
import android.os.RemoteException;
import android.view.KeyEvent;

import com.android.uiautomator.core.UiCollection;
import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.github.uiautomatorstub.watcher.ClickUiObjectWatcher;
import com.github.uiautomatorstub.watcher.PressKeysWatcher;

public class AutomatorServiceImpl implements AutomatorService {

	final static String STORAGE_PATH = "/data/local/tmp/";
	private final HashSet<String> watchers = new HashSet<String>();
	private final ConcurrentHashMap<String, UiObject> uiObjects = new ConcurrentHashMap<String, UiObject>();

	public AutomatorServiceImpl() {
	}

	/**
	 * Walk around to avoid backforward compatibility issue on uiautomator
	 * between api level 16/17.
	 */
	static void setAsHorizontalList(UiScrollable obj) {
		Class noparams[] = {};
		Object nullparmas[] = {};
		try {
			Class.forName("com.android.uiautomator.core.UiScrollable")
					.getDeclaredMethod("setAsHorizontalList", noparams).invoke(obj, nullparmas);
		} catch (NoSuchMethodException e) {
			Log.d(e.getMessage());
		} catch (ClassNotFoundException e) {
			Log.d(e.getMessage());
		} catch (InvocationTargetException e) {
			Log.d(e.getMessage());
		} catch (IllegalAccessException e) {
			Log.d(e.getMessage());
		}
	}

	/**
	 * Walk around to avoid backforward compatibility issue on uiautomator
	 * between api level 16/17.
	 */
	static void setAsVerticalList(UiScrollable obj) {
		Class noparams[] = {};
		Object nullparmas[] = {};
		try {
			Class.forName("com.android.uiautomator.core.UiScrollable").getDeclaredMethod("setAsVerticalList", noparams)
					.invoke(obj, nullparmas);
		} catch (NoSuchMethodException e) {
			Log.d(e.getMessage());
		} catch (ClassNotFoundException e) {
			Log.d(e.getMessage());
		} catch (InvocationTargetException e) {
			Log.d(e.getMessage());
		} catch (IllegalAccessException e) {
			Log.d(e.getMessage());
		}
	}

	/**
	 * It's to test if the service is alive.
	 * 
	 * @return 'pong'
	 */
	@Override
	public String ping() {
		new UiObject(new UiSelector()).exists(); // here we call the method just
//													// for checking if the
//													// UiAutomationService is
//													// ok, else it will throw
//													// IllegalStateException.
		return "pong";
	}

	/**
	 * Get the device info.
	 * 
	 * @return device info.
	 */
	@Override
	public DeviceInfo deviceInfo() {
		return DeviceInfo.getDeviceInfo();
	}

	/**
	 * Perform a click at arbitrary coordinates specified by the user.
	 * 
	 * @param x
	 *            coordinate
	 * @param y
	 *            coordinate
	 * @return true if the click succeeded else false
	 */
	@Override
	public boolean click(int x, int y) {
		return UiDevice.getInstance().click(x, y);
	}

	/**
	 * Performs a swipe from one coordinate to another coordinate. You can
	 * control the smoothness and speed of the swipe by specifying the number of
	 * steps. Each step execution is throttled to 5 milliseconds per step, so
	 * for a 100 steps, the swipe will take around 0.5 seconds to complete.
	 * 
	 * @param startX
	 *            X-axis value for the starting coordinate
	 * @param startY
	 *            Y-axis value for the starting coordinate
	 * @param endX
	 *            X-axis value for the ending coordinate
	 * @param endY
	 *            Y-axis value for the ending coordinate
	 * @param steps
	 *            is the number of steps for the swipe action
	 * @return true if swipe is performed, false if the operation fails or the
	 *         coordinates are invalid
	 * @throws com.github.uiautomatorstub.NotImplementedException
	 * 
	 */
	@Override
	public boolean drag(int startX, int startY, int endX, int endY, int steps) throws NotImplementedException {
		if (Build.VERSION.SDK_INT < 18)
			throw new NotImplementedException();

		return UiDevice.getInstance().drag(startX, startY, endX, endY, steps);
	}

	/**
	 * Performs a swipe from one coordinate to another using the number of steps
	 * to determine smoothness and speed. Each step execution is throttled to
	 * 5ms per step. So for a 100 steps, the swipe will take about 1/2 second to
	 * complete.
	 * 
	 * @param startX
	 *            X-axis value for the starting coordinate
	 * @param startY
	 *            Y-axis value for the starting coordinate
	 * @param endX
	 *            X-axis value for the ending coordinate
	 * @param endY
	 *            Y-axis value for the ending coordinate
	 * @param steps
	 *            is the number of move steps sent to the system
	 * @return false if the operation fails or the coordinates are invalid
	 */
	@Override
	public boolean swipe(int startX, int startY, int endX, int endY, int steps) {
		return UiDevice.getInstance().swipe(startX, startY, endX, endY, steps);
	}

	/**
	 * Helper method used for debugging to dump the current window's layout
	 * hierarchy. The file root location is /data/local/tmp
	 * 
	 * @param compressed
	 *            use compressed layout hierarchy or not using
	 *            setCompressedLayoutHeirarchy method. Ignore the parameter in
	 *            case the API level lt 18.
	 * @param filename
	 *            the filename to be stored.
	 * @return the absolute path name of dumped file.
	 */
	@Override
	public String dumpWindowHierarchy(boolean compressed, String filename) {
		if (Build.VERSION.SDK_INT >= 18)
			UiDevice.getInstance().setCompressedLayoutHeirarchy(compressed);
		File parent = new File(Environment.getDataDirectory(), "local/tmp"); // Environment.getDataDirectory()
																				// return
																				// /data/local/tmp
																				// in
																				// android
																				// 4.3
																				// but
																				// not
																				// expected
																				// /data
		if (!parent.exists())
			parent.mkdirs();
		File dumpFile = new File(parent, filename).getAbsoluteFile();
		UiDevice.getInstance().dumpWindowHierarchy(filename);
		File f = new File(STORAGE_PATH, filename); // It should be this...
		if (f.exists())
			return f.getAbsolutePath();
		else if (dumpFile.exists())
			return dumpFile.getAbsolutePath();
		else
			return null;
	}

	/**
	 * Take a screenshot of current window and store it as PNG The screenshot is
	 * adjusted per screen rotation
	 * 
	 * @param filename
	 *            where the PNG should be written to
	 * @param scale
	 *            scale the screenshot down if needed; 1.0f for original size
	 * @param quality
	 *            quality of the PNG compression; range: 0-100
	 * @return the file name of the screenshot. null if failed.
	 * @throws com.github.uiautomatorstub.NotImplementedException
	 * 
	 */
	@Override
	public String takeScreenshot(String filename, float scale, int quality) throws NotImplementedException {
		if (Build.VERSION.SDK_INT < 17)
			throw new NotImplementedException("takeScreenshot");
		File f = new File(STORAGE_PATH, filename);
		UiDevice.getInstance().takeScreenshot(f, scale, quality);
		if (f.exists())
			return f.getAbsolutePath();
		return null;
	}

	/**
	 * Disables the sensors and freezes the device rotation at its current
	 * rotation state, or enable it.
	 * 
	 * @param freeze
	 *            true to freeze the rotation, false to unfreeze the rotation.
	 * @throws android.os.RemoteException
	 */
	@Override
	public void freezeRotation(boolean freeze) throws RemoteException {
		if (freeze)
			UiDevice.getInstance().freezeRotation();
		else
			UiDevice.getInstance().unfreezeRotation();
	}

	/**
	 * Simulates orienting the device to the left/right/natural and also freezes
	 * rotation by disabling the sensors.
	 * 
	 * @param dir
	 *            Left or l, Right or r, Natural or n, case insensitive
	 * @throws android.os.RemoteException
	 * @throws com.github.uiautomatorstub.NotImplementedException
	 * 
	 */
	@Override
	public void setOrientation(String dir) throws RemoteException, NotImplementedException {
		if (Build.VERSION.SDK_INT < 17)
			throw new NotImplementedException("setOrientation");
		dir = dir.toLowerCase();
		if ("left".equals(dir) || "l".equals(dir))
			UiDevice.getInstance().setOrientationLeft();
		else if ("right".equals(dir) || "r".equals(dir))
			UiDevice.getInstance().setOrientationRight();
		else if ("natural".equals(dir) || "n".equals(dir))
			UiDevice.getInstance().setOrientationNatural();
	}

	/**
	 * Retrieves the text from the last UI traversal event received.
	 * 
	 * @return the text from the last UI traversal event received.
	 */
	@Override
	public String getLastTraversedText() {
		return UiDevice.getInstance().getLastTraversedText();
	}

	/**
	 * Clears the text from the last UI traversal event.
	 */
	@Override
	public void clearLastTraversedText() {
		UiDevice.getInstance().clearLastTraversedText();
	}

	/**
	 * Opens the notification shade.
	 * 
	 * @return true if successful, else return false
	 * @throws com.github.uiautomatorstub.NotImplementedException
	 * 
	 */
	@Override
	public boolean openNotification() throws NotImplementedException {
		if (Build.VERSION.SDK_INT < 18)
			throw new NotImplementedException("openNotification");
		return UiDevice.getInstance().openNotification();
	}

	/**
	 * Opens the Quick Settings shade.
	 * 
	 * @return true if successful, else return false
	 * @throws com.github.uiautomatorstub.NotImplementedException
	 * 
	 */
	@Override
	public boolean openQuickSettings() throws NotImplementedException {
		if (Build.VERSION.SDK_INT < 18)
			throw new NotImplementedException("openQuickSettings");
		return UiDevice.getInstance().openQuickSettings();
	}

	/**
	 * Checks if a specific registered UiWatcher has triggered. See
	 * registerWatcher(String, UiWatcher). If a UiWatcher runs and its
	 * checkForCondition() call returned true, then the UiWatcher is considered
	 * triggered. This is helpful if a watcher is detecting errors from ANR or
	 * crash dialogs and the test needs to know if a UiWatcher has been
	 * triggered.
	 * 
	 * @param watcherName
	 *            the name of registered watcher.
	 * @return true if triggered else false
	 */
	@Override
	public boolean hasWatcherTriggered(String watcherName) {
		return UiDevice.getInstance().hasWatcherTriggered(watcherName);
	}

	/**
	 * Checks if any registered UiWatcher have triggered.
	 * 
	 * @return true if any UiWatcher have triggered else false.
	 */
	@Override
	public boolean hasAnyWatcherTriggered() {
		return UiDevice.getInstance().hasAnyWatcherTriggered();
	}

	/**
	 * Register a ClickUiObjectWatcher
	 * 
	 * @param name
	 *            Watcher name
	 * @param conditions
	 *            If all UiObject in the conditions match, the watcher should be
	 *            triggered.
	 * @param target
	 *            The target UiObject should be clicked if all conditions match.
	 */
	@Override
	public void registerClickUiObjectWatcher(String name, Selector[] conditions, Selector target) {
		synchronized (watchers) {
			if (watchers.contains(name)) {
				UiDevice.getInstance().removeWatcher(name);
				watchers.remove(name);
			}

			UiSelector[] selectors = new UiSelector[conditions.length];
			for (int i = 0; i < conditions.length; i++) {
				selectors[i] = conditions[i].toUiSelector();
			}
			UiDevice.getInstance().registerWatcher(name, new ClickUiObjectWatcher(selectors, target.toUiSelector()));
			watchers.add(name);
		}
	}

	/**
	 * Register a PressKeysWatcher
	 * 
	 * @param name
	 *            Watcher name
	 * @param conditions
	 *            If all UiObject in the conditions match, the watcher should be
	 *            triggered.
	 * @param keys
	 *            All keys will be pressed in sequence.
	 */
	@Override
	public void registerPressKeyskWatcher(String name, Selector[] conditions, String[] keys) {
		synchronized (watchers) {
			if (watchers.contains(name)) {
				UiDevice.getInstance().removeWatcher(name);
				watchers.remove(name);
			}

			UiSelector[] selectors = new UiSelector[conditions.length];
			for (int i = 0; i < conditions.length; i++) {
				selectors[i] = conditions[i].toUiSelector();
			}
			UiDevice.getInstance().registerWatcher(name, new PressKeysWatcher(selectors, keys));
			watchers.add(name);
		}
	}

	/**
	 * Removes a previously registered UiWatcher.
	 * 
	 * @param name
	 *            Watcher name
	 */
	@Override
	public void removeWatcher(String name) {
		synchronized (watchers) {
			if (watchers.contains(name)) {
				UiDevice.getInstance().removeWatcher(name);
				watchers.remove(name);
			}
		}
	}

	/**
	 * Resets a UiWatcher that has been triggered. If a UiWatcher runs and its
	 * checkForCondition() call returned true, then the UiWatcher is considered
	 * triggered.
	 */
	@Override
	public void resetWatcherTriggers() {
		UiDevice.getInstance().resetWatcherTriggers();
	}

	/**
	 * Force to run all watchers.
	 */
	@Override
	public void runWatchers() {
		UiDevice.getInstance().runWatchers();
	}

	/**
	 * Get all registered UiWatchers
	 * 
	 * @return UiWatcher names
	 */
	@Override
	public String[] getWatchers() {
		synchronized (watchers) {
			return watchers.toArray(new String[watchers.size()]);
		}
	}

	/**
	 * Simulates a short press using key name.
	 * 
	 * @param key
	 *            possible key name is home, back, left, right, up, down,
	 *            center, menu, search, enter, delete(or del), recent(recent
	 *            apps), volume_up, volume_down, volume_mute, camera, power
	 * @return true if successful, else return false
	 * @throws android.os.RemoteException
	 */
	@Override
	public boolean pressKey(String key) throws RemoteException {
		boolean result;
		key = key.toLowerCase();
		if ("home".equals(key)) 
			result = UiDevice.getInstance().pressHome();
		else if ("back".equals(key))
			result = UiDevice.getInstance().pressBack();
		else if ("left".equals(key))
			result = UiDevice.getInstance().pressDPadLeft();
		else if ("right".equals(key))
			result = UiDevice.getInstance().pressDPadRight();
		else if ("up".equals(key))
			result = UiDevice.getInstance().pressDPadUp();
		else if ("down".equals(key))
			result = UiDevice.getInstance().pressDPadDown();
		else if ("center".equals(key))
			result = UiDevice.getInstance().pressDPadCenter();
		else if ("menu".equals(key))
			result = UiDevice.getInstance().pressMenu();
		else if ("search".equals(key))
			result = UiDevice.getInstance().pressSearch();
		else if ("enter".equals(key))
			result = UiDevice.getInstance().pressEnter();
		else if ("delete".equals(key) || "del".equals(key))
			result = UiDevice.getInstance().pressDelete();
		else if ("recent".equals(key))
			result = UiDevice.getInstance().pressRecentApps();
		else if ("volume_up".equals(key))
			result = UiDevice.getInstance().pressKeyCode(KeyEvent.KEYCODE_VOLUME_UP);
		else if ("volume_down".equals(key))
			result = UiDevice.getInstance().pressKeyCode(KeyEvent.KEYCODE_VOLUME_DOWN);
		else if ("volume_mute".equals(key))
			result = UiDevice.getInstance().pressKeyCode(KeyEvent.KEYCODE_VOLUME_MUTE);
		else if ("camera".equals(key))
			result = UiDevice.getInstance().pressKeyCode(KeyEvent.KEYCODE_CAMERA);
		else
			result = "power".equals(key) && UiDevice.getInstance().pressKeyCode(KeyEvent.KEYCODE_POWER);

		return result;
	}

	/**
	 * Simulates a short press using a key code. See KeyEvent.
	 * 
	 * @param keyCode
	 *            the key code of the event.
	 * @return true if successful, else return false
	 */
	@Override
	public boolean pressKeyCode(int keyCode) {
		return UiDevice.getInstance().pressKeyCode(keyCode);
	}

	/**
	 * Simulates a short press using a key code. See KeyEvent.
	 * 
	 * @param keyCode
	 *            the key code of the event.
	 * @param metaState
	 *            an integer in which each bit set to 1 represents a pressed
	 *            meta key
	 * @return true if successful, else return false
	 */
	@Override
	public boolean pressKeyCode(int keyCode, int metaState) {
		return UiDevice.getInstance().pressKeyCode(keyCode, metaState);
	}

	/**
	 * This method simulates pressing the power button if the screen is OFF else
	 * it does nothing if the screen is already ON. If the screen was OFF and it
	 * just got turned ON, this method will insert a 500ms delay to allow the
	 * device time to wake up and accept input.
	 * 
	 * @throws android.os.RemoteException
	 */
	@Override
	public void wakeUp() throws RemoteException {
		UiDevice.getInstance().wakeUp();
	}

	/**
	 * This method simply presses the power button if the screen is ON else it
	 * does nothing if the screen is already OFF.
	 * 
	 * @throws android.os.RemoteException
	 */
	@Override
	public void sleep() throws RemoteException {
		UiDevice.getInstance().sleep();
	}

	/**
	 * Checks the power manager if the screen is ON.
	 * 
	 * @return true if the screen is ON else false
	 * @throws android.os.RemoteException
	 */
	@Override
	public boolean isScreenOn() throws RemoteException {
		return UiDevice.getInstance().isScreenOn();
	}

	/**
	 * Waits for the current application to idle.
	 * 
	 * @param timeout
	 *            in milliseconds
	 */
	@Override
	public void waitForIdle(long timeout) {
		UiDevice.getInstance().waitForIdle(timeout);
	}

	/**
	 * Waits for a window content update event to occur. If a package name for
	 * the window is specified, but the current window does not have the same
	 * package name, the function returns immediately.
	 * 
	 * @param packageName
	 *            the specified window package name (can be null). If null, a
	 *            window update from any front-end window will end the wait.
	 * @param timeout
	 *            the timeout for the wait
	 * @return true if a window update occurred, false if timeout has elapsed or
	 *         if the current window does not have the specified package name
	 */
	@Override
	public boolean waitForWindowUpdate(String packageName, long timeout) {
		return UiDevice.getInstance().waitForWindowUpdate(packageName, timeout);
	}

	/**
	 * Clears the existing text contents in an editable field. The UiSelector of
	 * this object must reference a UI element that is editable. When you call
	 * this method, the method first sets focus at the start edge of the field.
	 * The method then simulates a long-press to select the existing text, and
	 * deletes the selected text. If a "Select-All" option is displayed, the
	 * method will automatically attempt to use it to ensure full text
	 * selection. Note that it is possible that not all the text in the field is
	 * selected; for example, if the text contains separators such as spaces,
	 * slashes, at symbol etc. Also, not all editable fields support the
	 * long-press functionality.
	 * 
	 * @param obj
	 *            the selector of the UiObject.
	 * @throws com.android.uiautomator.core.UiObjectNotFoundException
	 * 
	 */
	@Override
	public void clearTextField(Selector obj) throws UiObjectNotFoundException {
		new UiObject(obj.toUiSelector()).clearTextField();
	}

	/**
	 * Reads the text property of the UI element
	 * 
	 * @param obj
	 *            the selector of the UiObject.
	 * @return text value of the current node represented by this UiObject
	 * @throws com.android.uiautomator.core.UiObjectNotFoundException
	 * 
	 */
	@Override
	public String getText(Selector obj) throws UiObjectNotFoundException {
		return new UiObject(obj.toUiSelector()).getText();
	}

	/**
	 * Sets the text in an editable field, after clearing the field's content.
	 * The UiSelector selector of this object must reference a UI element that
	 * is editable. When you call this method, the method first simulates a
	 * click() on editable field to set focus. The method then clears the
	 * field's contents and injects your specified text into the field. If you
	 * want to capture the original contents of the field, call getText() first.
	 * You can then modify the text and use this method to update the field.
	 * 
	 * @param obj
	 *            the selector of the UiObject.
	 * @param text
	 *            string to set
	 * @return true if operation is successful
	 * @throws com.android.uiautomator.core.UiObjectNotFoundException
	 * 
	 */
	@Override
	public boolean setText(Selector obj, String text) throws UiObjectNotFoundException {
		return new UiObject(obj.toUiSelector()).setText(text);
	}

	/**
	 * Performs a click at the center of the visible bounds of the UI element
	 * represented by this UiObject.
	 * 
	 * @param obj
	 *            the target ui object.
	 * @return true id successful else false
	 * @throws com.android.uiautomator.core.UiObjectNotFoundException
	 * 
	 */
	@Override
	public boolean click(Selector obj) throws UiObjectNotFoundException {
		return new UiObject(obj.toUiSelector()).click();
	}

	/**
	 * Clicks the bottom and right corner or top and left corner of the UI
	 * element
	 * 
	 * @param obj
	 *            the target ui object.
	 * @param corner
	 *            "br"/"bottomright" means BottomRight, "tl"/"topleft" means
	 *            TopLeft, "center" means Center.
	 * @return true on success
	 * @throws com.android.uiautomator.core.UiObjectNotFoundException
	 * 
	 */
	@Override
	public boolean click(Selector obj, String corner) throws UiObjectNotFoundException {
		return click(new UiObject(obj.toUiSelector()), corner);
	}

	private boolean click(UiObject obj, String corner) throws UiObjectNotFoundException {
		if (corner == null)
			corner = "center";
		corner = corner.toLowerCase();
		if ("br".equals(corner) || "bottomright".equals(corner))
			return obj.clickBottomRight();
		else if ("tl".equals(corner) || "topleft".equals(corner))
			return obj.clickTopLeft();
		else if ("c".equals(corner) || "center".equals(corner))
			return obj.click();
		return false;
	}

	/**
	 * Performs a click at the center of the visible bounds of the UI element
	 * represented by this UiObject and waits for window transitions. This
	 * method differ from click() only in that this method waits for a a new
	 * window transition as a result of the click. Some examples of a window
	 * transition: - launching a new activity - bringing up a pop-up menu -
	 * bringing up a dialog
	 * 
	 * @param obj
	 *            the target ui object.
	 * @param timeout
	 *            timeout before giving up on waiting for a new window
	 * @return true if the event was triggered, else false
	 * @throws com.android.uiautomator.core.UiObjectNotFoundException
	 * 
	 */
	@Override
	public boolean clickAndWaitForNewWindow(Selector obj, long timeout) throws UiObjectNotFoundException {
		return new UiObject(obj.toUiSelector()).clickAndWaitForNewWindow(timeout);
	}

	/**
	 * Long clicks the center of the visible bounds of the UI element
	 * 
	 * @param obj
	 *            the target ui object.
	 * @return true if operation was successful
	 * @throws com.android.uiautomator.core.UiObjectNotFoundException
	 * 
	 */
	@Override
	public boolean longClick(Selector obj) throws UiObjectNotFoundException {
		return new UiObject(obj.toUiSelector()).longClick();
	}

	/**
	 * Long clicks bottom and right corner of the UI element
	 * 
	 * @param obj
	 *            the target ui object.
	 * @param corner
	 *            "br"/"bottomright" means BottomRight, "tl"/"topleft" means
	 *            TopLeft, "center" means Center.
	 * @return true if operation was successful
	 * @throws com.android.uiautomator.core.UiObjectNotFoundException
	 * 
	 */
	@Override
	public boolean longClick(Selector obj, String corner) throws UiObjectNotFoundException {
		return longClick(new UiObject(obj.toUiSelector()), corner);
	}

	private boolean longClick(UiObject obj, String corner) throws UiObjectNotFoundException {
		if (corner == null)
			corner = "center";

		corner = corner.toLowerCase();
		if ("br".equals(corner) || "bottomright".equals(corner))
			return obj.longClickBottomRight();
		else if ("tl".equals(corner) || "topleft".equals(corner))
			return obj.longClickTopLeft();
		else if ("c".equals(corner) || "center".equals(corner))
			return obj.longClick();

		return false;
	}

	/**
	 * Drags this object to a destination UiObject. The number of steps
	 * specified in your input parameter can influence the drag speed, and
	 * varying speeds may impact the results. Consider evaluating different
	 * speeds when using this method in your tests.
	 * 
	 * @param obj
	 *            the ui object to be dragged.
	 * @param destObj
	 *            the ui object to be dragged to.
	 * @param steps
	 *            usually 40 steps. You can increase or decrease the steps to
	 *            change the speed.
	 * @return true if successful
	 * @throws com.android.uiautomator.core.UiObjectNotFoundException
	 * 
	 * @throws com.github.uiautomatorstub.NotImplementedException
	 * 
	 */
	@Override
	public boolean dragTo(Selector obj, Selector destObj, int steps) throws UiObjectNotFoundException,
			NotImplementedException {
		return dragTo(new UiObject(obj.toUiSelector()), destObj, steps);
	}

	private boolean dragTo(UiObject obj, Selector destObj, int steps) throws UiObjectNotFoundException,
			NotImplementedException {
		if (Build.VERSION.SDK_INT < 18)
			throw new NotImplementedException("dragTo");
		return obj.dragTo(new UiObject(destObj.toUiSelector()), steps);
	}

	/**
	 * Drags this object to arbitrary coordinates. The number of steps specified
	 * in your input parameter can influence the drag speed, and varying speeds
	 * may impact the results. Consider evaluating different speeds when using
	 * this method in your tests.
	 * 
	 * @param obj
	 *            the ui object to be dragged.
	 * @param destX
	 *            the X-axis coordinate of destination.
	 * @param destY
	 *            the Y-axis coordinate of destination.
	 * @param steps
	 *            usually 40 steps. You can increase or decrease the steps to
	 *            change the speed.
	 * @return true if successful
	 * @throws com.android.uiautomator.core.UiObjectNotFoundException
	 * 
	 * @throws com.github.uiautomatorstub.NotImplementedException
	 * 
	 */
	@Override
	public boolean dragTo(Selector obj, int destX, int destY, int steps) throws UiObjectNotFoundException,
			NotImplementedException {
		return dragTo(new UiObject(obj.toUiSelector()), destX, destY, steps);
	}

	private boolean dragTo(UiObject obj, int destX, int destY, int steps) throws UiObjectNotFoundException,
			NotImplementedException {
		if (Build.VERSION.SDK_INT < 18)
			throw new NotImplementedException("dragTo");
		return obj.dragTo(destX, destY, steps);
	}

	/**
	 * Check if view exists. This methods performs a waitForExists(long) with
	 * zero timeout. This basically returns immediately whether the view
	 * represented by this UiObject exists or not.
	 * 
	 * @param obj
	 *            the ui object.
	 * @return true if the view represented by this UiObject does exist
	 */
	@Override
	public boolean exist(Selector obj) {
		return new UiObject(obj.toUiSelector()).exists();
	}

	/**
	 * Get the object info.
	 * 
	 * @param obj
	 *            the target ui object.
	 * @return object info.
	 * @throws com.android.uiautomator.core.UiObjectNotFoundException
	 * 
	 */
	@Override
	public ObjInfo objInfo(Selector obj) throws UiObjectNotFoundException {
		return ObjInfo.getObjInfo(obj.toUiSelector());
	}

	/**
	 * Get the count of the UiObject instances by the selector
	 * 
	 * @param obj
	 *            the selector of the ui object
	 * @return the count of instances.
	 */
	@Override
	public int count(Selector obj) {
		if ((obj.getMask() & Selector.MASK_INSTANCE) > 0) {
			if (new UiObject(obj.toUiSelector()).exists())
				return 1;
			else
				return 0;
		} else {
			UiSelector sel = obj.toUiSelector();
			if (!new UiObject(sel).exists())
				return 0;
			int low = 1;
			int high = 2;
			sel = sel.instance(high - 1);
			while (new UiObject(sel).exists()) {
				low = high;
				high = high * 2;
				sel = sel.instance(high - 1);
			}
			while (high > low + 1) {
				int mid = (low + high) / 2;
				sel = sel.instance(mid - 1);
				if (new UiObject(sel).exists())
					low = mid;
				else
					high = mid;
			}
			return low;
		}
	}

	/**
	 * Get the info of all instance by the selector.
	 * 
	 * @param obj
	 *            the selector of ui object.
	 * @return array of object info.
	 * 
	 */
	@Override
	public ObjInfo[] objInfoOfAllInstances(Selector obj) {
		int total = count(obj);
		ObjInfo objs[] = new ObjInfo[total];
		if ((obj.getMask() & Selector.MASK_INSTANCE) > 0 && total > 0) {
			try {
				objs[0] = objInfo(obj);
			} catch (UiObjectNotFoundException e) {
			}
		} else {
			UiSelector sel = obj.toUiSelector();
			for (int i = 0; i < total; i++) {
				try {
					objs[i] = ObjInfo.getObjInfo(sel.instance(i));
				} catch (UiObjectNotFoundException e) {
				}
			}
		}
		return objs;
	}

	/**
	 * Generates a two-pointer gesture with arbitrary starting and ending
	 * points.
	 * 
	 * @param obj
	 *            the target ui object. ??
	 * @param startPoint1
	 *            start point of pointer 1
	 * @param startPoint2
	 *            start point of pointer 2
	 * @param endPoint1
	 *            end point of pointer 1
	 * @param endPoint2
	 *            end point of pointer 2
	 * @param steps
	 *            the number of steps for the gesture. Steps are injected about
	 *            5 milliseconds apart, so 100 steps may take around 0.5 seconds
	 *            to complete.
	 * @return true if all touch events for this gesture are injected
	 *         successfully, false otherwise
	 * @throws com.android.uiautomator.core.UiObjectNotFoundException
	 * 
	 */
	@Override
	public boolean gesture(Selector obj, Point startPoint1, Point startPoint2, Point endPoint1, Point endPoint2,
			int steps) throws UiObjectNotFoundException, NotImplementedException {
		return gesture(new UiObject(obj.toUiSelector()), startPoint1, startPoint2, endPoint1, endPoint2, steps);
	}

	private boolean gesture(UiObject obj, Point startPoint1, Point startPoint2, Point endPoint1, Point endPoint2,
			int steps) throws UiObjectNotFoundException, NotImplementedException {
		if (Build.VERSION.SDK_INT < 18)
			throw new NotImplementedException("gesture(performTwoPointerGesture)");
		return obj.performTwoPointerGesture(startPoint1.toPoint(), startPoint2.toPoint(), endPoint1.toPoint(),
				endPoint2.toPoint(), steps);
	}

	/**
	 * Performs a two-pointer gesture, where each pointer moves diagonally
	 * toward the other, from the edges to the center of this UiObject .
	 * 
	 * @param obj
	 *            the target ui object.
	 * @param percent
	 *            percentage of the object's diagonal length for the pinch
	 *            gesture
	 * @param steps
	 *            the number of steps for the gesture. Steps are injected about
	 *            5 milliseconds apart, so 100 steps may take around 0.5 seconds
	 *            to complete.
	 * @return true if all touch events for this gesture are injected
	 *         successfully, false otherwise
	 * @throws com.android.uiautomator.core.UiObjectNotFoundException
	 * 
	 * @throws com.github.uiautomatorstub.NotImplementedException
	 * 
	 */
	@Override
	public boolean pinchIn(Selector obj, int percent, int steps) throws UiObjectNotFoundException,
			NotImplementedException {
		return pinchIn(new UiObject(obj.toUiSelector()), percent, steps);
	}

	private boolean pinchIn(UiObject obj, int percent, int steps) throws UiObjectNotFoundException,
			NotImplementedException {
		if (Build.VERSION.SDK_INT < 18)
			throw new NotImplementedException("pinchIn");
		return obj.pinchIn(percent, steps);
	}

	/**
	 * Performs a two-pointer gesture, where each pointer moves diagonally
	 * opposite across the other, from the center out towards the edges of the
	 * this UiObject.
	 * 
	 * @param obj
	 *            the target ui object.
	 * @param percent
	 *            percentage of the object's diagonal length for the pinch
	 *            gesture
	 * @param steps
	 *            the number of steps for the gesture. Steps are injected about
	 *            5 milliseconds apart, so 100 steps may take around 0.5 seconds
	 *            to complete.
	 * @return true if all touch events for this gesture are injected
	 *         successfully, false otherwise
	 * @throws com.android.uiautomator.core.UiObjectNotFoundException
	 * 
	 * @throws com.github.uiautomatorstub.NotImplementedException
	 * 
	 */
	@Override
	public boolean pinchOut(Selector obj, int percent, int steps) throws UiObjectNotFoundException,
			NotImplementedException {
		return pinchOut(new UiObject(obj.toUiSelector()), percent, steps);
	}

	private boolean pinchOut(UiObject obj, int percent, int steps) throws UiObjectNotFoundException,
			NotImplementedException {
		if (Build.VERSION.SDK_INT < 18)
			throw new NotImplementedException("pinchOut");
		return obj.pinchOut(percent, steps);
	}

	/**
	 * Performs the swipe up/down/left/right action on the UiObject
	 * 
	 * @param obj
	 *            the target ui object.
	 * @param dir
	 *            "u"/"up", "d"/"down", "l"/"left", "r"/"right"
	 * @param steps
	 *            indicates the number of injected move steps into the system.
	 *            Steps are injected about 5ms apart. So a 100 steps may take
	 *            about 1/2 second to complete.
	 * @return true of successful
	 * @throws com.android.uiautomator.core.UiObjectNotFoundException
	 * 
	 */
	@Override
	public boolean swipe(Selector obj, String dir, int steps) throws UiObjectNotFoundException {
		return swipe(new UiObject(obj.toUiSelector()), dir, steps);
	}

	private boolean swipe(UiObject item, String dir, int steps) throws UiObjectNotFoundException {
		dir = dir.toLowerCase();
		boolean result = false;
		if ("u".equals(dir) || "up".equals(dir))
			result = item.swipeUp(steps);
		else if ("d".equals(dir) || "down".equals(dir))
			result = item.swipeDown(steps);
		else if ("l".equals(dir) || "left".equals(dir))
			result = item.swipeLeft(steps);
		else if ("r".equals(dir) || "right".equals(dir))
			result = item.swipeRight(steps);
		return result;
	}

	/**
	 * Waits a specified length of time for a view to become visible. This
	 * method waits until the view becomes visible on the display, or until the
	 * timeout has elapsed. You can use this method in situations where the
	 * content that you want to select is not immediately displayed.
	 * 
	 * @param obj
	 *            the target ui object
	 * @param timeout
	 *            time to wait (in milliseconds)
	 * @return true if the view is displayed, else false if timeout elapsed
	 *         while waiting
	 */
	@Override
	public boolean waitForExists(Selector obj, long timeout) {
		return new UiObject(obj.toUiSelector()).waitForExists(timeout);
	}

	/**
	 * Waits a specified length of time for a view to become undetectable. This
	 * method waits until a view is no longer matchable, or until the timeout
	 * has elapsed. A view becomes undetectable when the UiSelector of the
	 * object is unable to find a match because the element has either changed
	 * its state or is no longer displayed. You can use this method when
	 * attempting to wait for some long operation to compete, such as
	 * downloading a large file or connecting to a remote server.
	 * 
	 * @param obj
	 *            the target ui object
	 * @param timeout
	 *            time to wait (in milliseconds)
	 * @return true if the element is gone before timeout elapsed, else false if
	 *         timeout elapsed but a matching element is still found.
	 */
	@Override
	public boolean waitUntilGone(Selector obj, long timeout) {
		return new UiObject(obj.toUiSelector()).waitUntilGone(timeout);
	}

	/**
	 * Performs a backwards fling action with the default number of fling steps
	 * (5). If the swipe direction is set to vertical, then the swipe will be
	 * performed from top to bottom. If the swipe direction is set to
	 * horizontal, then the swipes will be performed from left to right. Make
	 * sure to take into account devices configured with right-to-left languages
	 * like Arabic and Hebrew.
	 * 
	 * @param obj
	 *            the selector of the scrollable object
	 * @param isVertical
	 *            vertical or horizontal
	 * @return true if scrolled, and false if can't scroll anymore
	 * @throws com.android.uiautomator.core.UiObjectNotFoundException
	 * 
	 */
	@Override
	public boolean flingBackward(Selector obj, boolean isVertical) throws UiObjectNotFoundException {
		UiScrollable scrollable = new UiScrollable(obj.toUiSelector());
		if (isVertical)
			setAsVerticalList(scrollable);
		else
			setAsHorizontalList(scrollable);
		return scrollable.flingBackward();
	}

	/**
	 * Performs a forward fling with the default number of fling steps (5). If
	 * the swipe direction is set to vertical, then the swipes will be performed
	 * from bottom to top. If the swipe direction is set to horizontal, then the
	 * swipes will be performed from right to left. Make sure to take into
	 * account devices configured with right-to-left languages like Arabic and
	 * Hebrew.
	 * 
	 * @param obj
	 *            the selector of the scrollable object
	 * @param isVertical
	 *            vertical or horizontal
	 * @return true if scrolled, and false if can't scroll anymore
	 * @throws com.android.uiautomator.core.UiObjectNotFoundException
	 * 
	 */
	@Override
	public boolean flingForward(Selector obj, boolean isVertical) throws UiObjectNotFoundException {
		UiScrollable scrollable = new UiScrollable(obj.toUiSelector());
		if (isVertical)
			setAsVerticalList(scrollable);
		else
			setAsHorizontalList(scrollable);
		return scrollable.flingForward();
	}

	/**
	 * Performs a fling gesture to reach the beginning of a scrollable layout
	 * element. The beginning can be at the top-most edge in the case of
	 * vertical controls, or the left-most edge for horizontal controls. Make
	 * sure to take into account devices configured with right-to-left languages
	 * like Arabic and Hebrew.
	 * 
	 * @param obj
	 *            the selector of the scrollable object
	 * @param isVertical
	 *            vertical or horizontal
	 * @param maxSwipes
	 *            max swipes to achieve beginning.
	 * @return true on scrolled, else false
	 * @throws com.android.uiautomator.core.UiObjectNotFoundException
	 * 
	 */
	@Override
	public boolean flingToBeginning(Selector obj, boolean isVertical, int maxSwipes) throws UiObjectNotFoundException {
		UiScrollable scrollable = new UiScrollable(obj.toUiSelector());
		if (isVertical)
			setAsVerticalList(scrollable);
		else
			setAsHorizontalList(scrollable);
		return scrollable.flingToBeginning(maxSwipes);
	}

	/**
	 * Performs a fling gesture to reach the end of a scrollable layout element.
	 * The end can be at the bottom-most edge in the case of vertical controls,
	 * or the right-most edge for horizontal controls. Make sure to take into
	 * account devices configured with right-to-left languages like Arabic and
	 * Hebrew.
	 * 
	 * @param obj
	 *            the selector of the scrollable object
	 * @param isVertical
	 *            vertical or horizontal
	 * @param maxSwipes
	 *            max swipes to achieve end.
	 * @return true on scrolled, else false
	 * @throws com.android.uiautomator.core.UiObjectNotFoundException
	 * 
	 */
	@Override
	public boolean flingToEnd(Selector obj, boolean isVertical, int maxSwipes) throws UiObjectNotFoundException {
		UiScrollable scrollable = new UiScrollable(obj.toUiSelector());
		if (isVertical)
			setAsVerticalList(scrollable);
		else
			setAsHorizontalList(scrollable);
		return scrollable.flingToEnd(maxSwipes);
	}

	/**
	 * Performs a backward scroll. If the swipe direction is set to vertical,
	 * then the swipes will be performed from top to bottom. If the swipe
	 * direction is set to horizontal, then the swipes will be performed from
	 * left to right. Make sure to take into account devices configured with
	 * right-to-left languages like Arabic and Hebrew.
	 * 
	 * @param obj
	 *            the selector of the scrollable object
	 * @param isVertical
	 *            vertical or horizontal
	 * @param steps
	 *            number of steps. Use this to control the speed of the scroll
	 *            action.
	 * @return true if scrolled, false if can't scroll anymore
	 * @throws com.android.uiautomator.core.UiObjectNotFoundException
	 * 
	 */
	@Override
	public boolean scrollBackward(Selector obj, boolean isVertical, int steps) throws UiObjectNotFoundException {
		UiScrollable scrollable = new UiScrollable(obj.toUiSelector());
		if (isVertical)
			setAsVerticalList(scrollable);
		else
			setAsHorizontalList(scrollable);
		return scrollable.scrollBackward(steps);
	}

	/**
	 * Performs a forward scroll with the default number of scroll steps (55).
	 * If the swipe direction is set to vertical, then the swipes will be
	 * performed from bottom to top. If the swipe direction is set to
	 * horizontal, then the swipes will be performed from right to left. Make
	 * sure to take into account devices configured with right-to-left languages
	 * like Arabic and Hebrew.
	 * 
	 * @param obj
	 *            the selector of the scrollable object
	 * @param isVertical
	 *            vertical or horizontal
	 * @param steps
	 *            number of steps. Use this to control the speed of the scroll
	 *            action.
	 * @return true on scrolled, else false
	 * @throws com.android.uiautomator.core.UiObjectNotFoundException
	 * 
	 */
	@Override
	public boolean scrollForward(Selector obj, boolean isVertical, int steps) throws UiObjectNotFoundException {
		UiScrollable scrollable = new UiScrollable(obj.toUiSelector());
		if (isVertical)
			setAsVerticalList(scrollable);
		else
			setAsHorizontalList(scrollable);
		return scrollable.scrollForward(steps);
	}

	/**
	 * Scrolls to the beginning of a scrollable layout element. The beginning
	 * can be at the top-most edge in the case of vertical controls, or the
	 * left-most edge for horizontal controls. Make sure to take into account
	 * devices configured with right-to-left languages like Arabic and Hebrew.
	 * 
	 * @param obj
	 *            the selector of the scrollable object
	 * @param isVertical
	 *            vertical or horizontal
	 * @param maxSwipes
	 *            max swipes to be performed.
	 * @param steps
	 *            use steps to control the speed, so that it may be a scroll, or
	 *            fling
	 * @return true on scrolled else false
	 * @throws com.android.uiautomator.core.UiObjectNotFoundException
	 * 
	 */
	@Override
	public boolean scrollToBeginning(Selector obj, boolean isVertical, int maxSwipes, int steps)
			throws UiObjectNotFoundException {
		UiScrollable scrollable = new UiScrollable(obj.toUiSelector());
		if (isVertical)
			setAsVerticalList(scrollable);
		else
			setAsHorizontalList(scrollable);
		return scrollable.scrollToBeginning(maxSwipes, steps);
	}

	/**
	 * Scrolls to the end of a scrollable layout element. The end can be at the
	 * bottom-most edge in the case of vertical controls, or the right-most edge
	 * for horizontal controls. Make sure to take into account devices
	 * configured with right-to-left languages like Arabic and Hebrew.
	 * 
	 * @param obj
	 *            the selector of the scrollable object
	 * @param isVertical
	 *            vertical or horizontal
	 * @param maxSwipes
	 *            max swipes to be performed.
	 * @param steps
	 *            use steps to control the speed, so that it may be a scroll, or
	 *            fling
	 * @return true on scrolled, else false
	 * @throws com.android.uiautomator.core.UiObjectNotFoundException
	 * 
	 */
	@Override
	public boolean scrollToEnd(Selector obj, boolean isVertical, int maxSwipes, int steps)
			throws UiObjectNotFoundException {
		UiScrollable scrollable = new UiScrollable(obj.toUiSelector());
		if (isVertical)
			setAsVerticalList(scrollable);
		else
			setAsHorizontalList(scrollable);
		return scrollable.scrollToEnd(maxSwipes, steps);
	}

	/**
	 * Perform a scroll forward action to move through the scrollable layout
	 * element until a visible item that matches the selector is found.
	 * 
	 * @param obj
	 *            the selector of the scrollable object
	 * @param targetObj
	 *            the item matches the selector to be found.
	 * @param isVertical
	 *            vertical or horizontal
	 * @return true on scrolled, else false
	 * @throws com.android.uiautomator.core.UiObjectNotFoundException
	 * 
	 */
	@Override
	public boolean scrollTo(Selector obj, Selector targetObj, boolean isVertical) throws UiObjectNotFoundException {
		UiScrollable scrollable = new UiScrollable(obj.toUiSelector());
		if (isVertical)
			setAsVerticalList(scrollable);
		else
			setAsHorizontalList(scrollable);
		return scrollable.scrollIntoView(targetObj.toUiSelector());
	}

	/**
	 * Name an UiObject and cache it.
	 * 
	 * @param obj
	 *            UiObject
	 * @return the name of the UiObject
	 */
	private String addUiObject(UiObject obj) {
		String key = UUID.randomUUID().toString();
		uiObjects.put(key, obj);
		// schedule the clear timer.
		Timer clearTimer = new Timer();
		clearTimer.schedule(new ClearUiObjectTimerTask(key), 60000);
		return key;
	}

	class ClearUiObjectTimerTask extends TimerTask {
		String name;

		public ClearUiObjectTimerTask(String name) {
			this.name = name;
		}

		public void run() {
			uiObjects.remove(name);
		}
	}

	/**
	 * Searches for child UI element within the constraints of this UiSelector
	 * selector. It looks for any child matching the childPattern argument that
	 * has a child UI element anywhere within its sub hierarchy that has a text
	 * attribute equal to text. The returned UiObject will point at the
	 * childPattern instance that matched the search and not at the identifying
	 * child element that matched the text attribute.
	 * 
	 * @param collection
	 *            Selector of UiCollection or UiScrollable.
	 * @param text
	 *            String of the identifying child contents of of the
	 *            childPattern
	 * @param child
	 *            UiSelector selector of the child pattern to match and return
	 * @return A string ID represent the returned UiObject.
	 */
	@Override
	public String childByText(Selector collection, Selector child, String text) throws UiObjectNotFoundException {
		UiObject obj;
		if (exist(collection) && objInfo(collection).isScrollable()) {
			obj = new UiScrollable(collection.toUiSelector()).getChildByText(child.toUiSelector(), text);
		} else {
			obj = new UiCollection(collection.toUiSelector()).getChildByText(child.toUiSelector(), text);
		}
		return addUiObject(obj);
	}

	@Override
	public String childByText(Selector collection, Selector child, String text, boolean allowScrollSearch)
			throws UiObjectNotFoundException {
		UiObject obj = new UiScrollable(collection.toUiSelector()).getChildByText(child.toUiSelector(), text,
				allowScrollSearch);
		return addUiObject(obj);
	}

	/**
	 * Searches for child UI element within the constraints of this UiSelector
	 * selector. It looks for any child matching the childPattern argument that
	 * has a child UI element anywhere within its sub hierarchy that has
	 * content-description text. The returned UiObject will point at the
	 * childPattern instance that matched the search and not at the identifying
	 * child element that matched the content description.
	 * 
	 * @param collection
	 *            Selector of UiCollection or UiScrollable
	 * @param child
	 *            UiSelector selector of the child pattern to match and return
	 * @param text
	 *            String of the identifying child contents of of the
	 *            childPattern
	 * @return A string ID represent the returned UiObject.
	 */
	@Override
	public String childByDescription(Selector collection, Selector child, String text) throws UiObjectNotFoundException {
		UiObject obj;
		if (exist(collection) && objInfo(collection).isScrollable()) {
			obj = new UiScrollable(collection.toUiSelector()).getChildByDescription(child.toUiSelector(), text);
		} else {
			obj = new UiCollection(collection.toUiSelector()).getChildByDescription(child.toUiSelector(), text);
		}
		return addUiObject(obj);
	}

	@Override
	public String childByDescription(Selector collection, Selector child, String text, boolean allowScrollSearch)
			throws UiObjectNotFoundException {
		UiObject obj = new UiScrollable(collection.toUiSelector()).getChildByDescription(child.toUiSelector(), text,
				allowScrollSearch);
		return addUiObject(obj);
	}

	/**
	 * Searches for child UI element within the constraints of this UiSelector.
	 * It looks for any child matching the childPattern argument that has a
	 * child UI element anywhere within its sub hierarchy that is at the
	 * instance specified. The operation is performed only on the visible items
	 * and no scrolling is performed in this case.
	 * 
	 * @param collection
	 *            Selector of UiCollection or UiScrollable
	 * @param child
	 *            UiSelector selector of the child pattern to match and return
	 * @param instance
	 *            int the desired matched instance of this childPattern
	 * @return A string ID represent the returned UiObject.
	 */
	@Override
	public String childByInstance(Selector collection, Selector child, int instance) throws UiObjectNotFoundException {
		UiObject obj;
		if (exist(collection) && objInfo(collection).isScrollable()) {
			obj = new UiScrollable(collection.toUiSelector()).getChildByInstance(child.toUiSelector(), instance);
		} else {
			obj = new UiCollection(collection.toUiSelector()).getChildByInstance(child.toUiSelector(), instance);
		}
		return addUiObject(obj);
	}

	/**
	 * Creates a new UiObject for a child view that is under the present
	 * UiObject.
	 * 
	 * @param obj
	 *            The ID string represent the parent UiObject.
	 * @param selector
	 *            UiSelector selector of the child pattern to match and return
	 * @return A string ID represent the returned UiObject.
	 */
	@Override
	public String getChild(String obj, Selector selector) throws UiObjectNotFoundException {
		UiObject ui = uiObjects.get(obj);
		if (ui != null) {
			return addUiObject(ui.getChild(selector.toUiSelector()));
		}
		return null;
	}

	/**
	 * Creates a new UiObject for a sibling view or a child of the sibling view,
	 * relative to the present UiObject.
	 * 
	 * @param obj
	 *            The ID string represent the source UiObject.
	 * @param selector
	 *            for a sibling view or children of the sibling view
	 * @return A string ID represent the returned UiObject.
	 */
	@Override
	public String getFromParent(String obj, Selector selector) throws UiObjectNotFoundException {
		UiObject ui = uiObjects.get(obj);
		if (ui != null) {
			return addUiObject(ui.getFromParent(selector.toUiSelector()));
		}
		return null;
	}

	/**
	 * Get a new UiObject from the selector.
	 * 
	 * @param selector
	 *            Selector of the UiObject
	 * @return A string ID represent the returned UiObject.
	 * @throws com.android.uiautomator.core.UiObjectNotFoundException
	 * 
	 */
	@Override
	public String getUiObject(Selector selector) throws UiObjectNotFoundException {
		return addUiObject(new UiObject(selector.toUiSelector()));
	}

	/**
	 * Remove the UiObject from memory.
	 */
	@Override
	public void removeUiObject(String obj) {
		uiObjects.remove(obj);
	}

	/**
	 * Get all named UiObjects.
	 * 
	 * @return all names
	 */
	@Override
	public String[] getUiObjects() {
		Set<String> strings = uiObjects.keySet();
		return strings.toArray(new String[strings.size()]);
	}

	private UiObject getUiObject(String name) throws UiObjectNotFoundException {
		if (uiObjects.containsKey(name)) {
			return uiObjects.get(name);
		} else {
			throw new UiObjectNotFoundException("UiObject " + name + " not found!");
		}
	}

	/**
	 * Clears the existing text contents in an editable field. The UiSelector of
	 * this object must reference a UI element that is editable. When you call
	 * this method, the method first sets focus at the start edge of the field.
	 * The method then simulates a long-press to select the existing text, and
	 * deletes the selected text. If a "Select-All" option is displayed, the
	 * method will automatically attempt to use it to ensure full text
	 * selection. Note that it is possible that not all the text in the field is
	 * selected; for example, if the text contains separators such as spaces,
	 * slashes, at symbol etc. Also, not all editable fields support the
	 * long-press functionality.
	 * 
	 * @param obj
	 *            the id of the UiObject.
	 * @throws com.android.uiautomator.core.UiObjectNotFoundException
	 * 
	 */
	@Override
	public void clearTextField(String obj) throws UiObjectNotFoundException {
		getUiObject(obj).clearTextField();
	}

	/**
	 * Reads the text property of the UI element
	 * 
	 * @param obj
	 *            the id of the UiObject.
	 * @return text value of the current node represented by this UiObject
	 * @throws com.android.uiautomator.core.UiObjectNotFoundException
	 * 
	 */
	@Override
	public String getText(String obj) throws UiObjectNotFoundException {
		return getUiObject(obj).getText();
	}

	/**
	 * Sets the text in an editable field, after clearing the field's content.
	 * The UiSelector selector of this object must reference a UI element that
	 * is editable. When you call this method, the method first simulates a
	 * click() on editable field to set focus. The method then clears the
	 * field's contents and injects your specified text into the field. If you
	 * want to capture the original contents of the field, call getText() first.
	 * You can then modify the text and use this method to update the field.
	 * 
	 * @param obj
	 *            the id of the UiObject.
	 * @param text
	 *            string to set
	 * @return true if operation is successful
	 * @throws com.android.uiautomator.core.UiObjectNotFoundException
	 * 
	 */
	@Override
	public boolean setText(String obj, String text) throws UiObjectNotFoundException {
		return getUiObject(obj).setText(text);
	}

	/**
	 * Performs a click at the center of the visible bounds of the UI element
	 * represented by this UiObject.
	 * 
	 * @param obj
	 *            the id of target ui object.
	 * @return true id successful else false
	 * @throws com.android.uiautomator.core.UiObjectNotFoundException
	 * 
	 */
	@Override
	public boolean click(String obj) throws UiObjectNotFoundException {
		return getUiObject(obj).click();
	}
	

	/**
	 * Clicks the bottom and right corner or top and left corner of the UI
	 * element
	 * 
	 * @param obj
	 *            the id of target ui object.
	 * @param corner
	 *            "br"/"bottomright" means BottomRight, "tl"/"topleft" means
	 *            TopLeft, "center" means Center.
	 * @return true on success
	 * @throws com.android.uiautomator.core.UiObjectNotFoundException
	 * 
	 */
	@Override
	public boolean click(String obj, String corner) throws UiObjectNotFoundException {
		return click(getUiObject(obj), corner);
	}

	/**
	 * Performs a click at the center of the visible bounds of the UI element
	 * represented by this UiObject and waits for window transitions. This
	 * method differ from click() only in that this method waits for a a new
	 * window transition as a result of the click. Some examples of a window
	 * transition: - launching a new activity - bringing up a pop-up menu -
	 * bringing up a dialog
	 * 
	 * @param obj
	 *            the id of target ui object.
	 * @param timeout
	 *            timeout before giving up on waiting for a new window
	 * @return true if the event was triggered, else false
	 * @throws com.android.uiautomator.core.UiObjectNotFoundException
	 * 
	 */
	@Override
	public boolean clickAndWaitForNewWindow(String obj, long timeout) throws UiObjectNotFoundException {
		return getUiObject(obj).clickAndWaitForNewWindow(timeout);
	}

	/**
	 * Long clicks the center of the visible bounds of the UI element
	 * 
	 * @param obj
	 *            the id of target ui object.
	 * @return true if operation was successful
	 * @throws com.android.uiautomator.core.UiObjectNotFoundException
	 * 
	 */
	@Override
	public boolean longClick(String obj) throws UiObjectNotFoundException {
		return getUiObject(obj).longClick();
	}

	/**
	 * Long clicks bottom and right corner of the UI element
	 * 
	 * @param obj
	 *            the id of target ui object.
	 * @param corner
	 *            "br"/"bottomright" means BottomRight, "tl"/"topleft" means
	 *            TopLeft, "center" means Center.
	 * @return true if operation was successful
	 * @throws com.android.uiautomator.core.UiObjectNotFoundException
	 * 
	 */
	@Override
	public boolean longClick(String obj, String corner) throws UiObjectNotFoundException {
		return longClick(getUiObject(obj), corner);
	}

	/**
	 * Drags this object to a destination UiObject. The number of steps
	 * specified in your input parameter can influence the drag speed, and
	 * varying speeds may impact the results. Consider evaluating different
	 * speeds when using this method in your tests.
	 * 
	 * @param obj
	 *            the id of ui object to be dragged.
	 * @param destObj
	 *            the ui object to be dragged to.
	 * @param steps
	 *            usually 40 steps. You can increase or decrease the steps to
	 *            change the speed.
	 * @return true if successful
	 * @throws com.android.uiautomator.core.UiObjectNotFoundException
	 * 
	 * @throws com.github.uiautomatorstub.NotImplementedException
	 * 
	 */
	@Override
	public boolean dragTo(String obj, Selector destObj, int steps) throws UiObjectNotFoundException,
			NotImplementedException {
		return dragTo(getUiObject(obj), destObj, steps);
	}

	/**
	 * Drags this object to arbitrary coordinates. The number of steps specified
	 * in your input parameter can influence the drag speed, and varying speeds
	 * may impact the results. Consider evaluating different speeds when using
	 * this method in your tests.
	 * 
	 * @param obj
	 *            the id of ui object to be dragged.
	 * @param destX
	 *            the X-axis coordinate of destination.
	 * @param destY
	 *            the Y-axis coordinate of destination.
	 * @param steps
	 *            usually 40 steps. You can increase or decrease the steps to
	 *            change the speed.
	 * @return true if successful
	 * @throws com.android.uiautomator.core.UiObjectNotFoundException
	 * 
	 * @throws com.github.uiautomatorstub.NotImplementedException
	 * 
	 */
	@Override
	public boolean dragTo(String obj, int destX, int destY, int steps) throws UiObjectNotFoundException,
			NotImplementedException {
		return dragTo(getUiObject(obj), destX, destY, steps);
	}

	/**
	 * Check if view exists. This methods performs a waitForExists(long) with
	 * zero timeout. This basically returns immediately whether the view
	 * represented by this UiObject exists or not.
	 * 
	 * @param obj
	 *            the id of ui object.
	 * @return true if the view represented by this UiObject does exist
	 */
	@Override
	public boolean exist(String obj) {
		try {
			return getUiObject(obj).exists();
		} catch (UiObjectNotFoundException e) {
			return false;
		}
	}

	/**
	 * Get the object info.
	 * 
	 * @param obj
	 *            the id of target ui object.
	 * @return object info.
	 * @throws com.android.uiautomator.core.UiObjectNotFoundException
	 * 
	 */
	@Override
	public ObjInfo objInfo(String obj) throws UiObjectNotFoundException {
		return ObjInfo.getObjInfo(getUiObject(obj));
	}

	/**
	 * Generates a two-pointer gesture with arbitrary starting and ending
	 * points.
	 * 
	 * @param obj
	 *            the id of target ui object. ??
	 * @param startPoint1
	 *            start point of pointer 1
	 * @param startPoint2
	 *            start point of pointer 2
	 * @param endPoint1
	 *            end point of pointer 1
	 * @param endPoint2
	 *            end point of pointer 2
	 * @param steps
	 *            the number of steps for the gesture. Steps are injected about
	 *            5 milliseconds apart, so 100 steps may take around 0.5 seconds
	 *            to complete.
	 * @return true if all touch events for this gesture are injected
	 *         successfully, false otherwise
	 * @throws com.android.uiautomator.core.UiObjectNotFoundException
	 * 
	 */
	@Override
	public boolean gesture(String obj, Point startPoint1, Point startPoint2, Point endPoint1, Point endPoint2, int steps)
			throws UiObjectNotFoundException, NotImplementedException {
		return gesture(getUiObject(obj), startPoint1, startPoint2, endPoint1, endPoint2, steps);
	}

	/**
	 * Performs a two-pointer gesture, where each pointer moves diagonally
	 * toward the other, from the edges to the center of this UiObject .
	 * 
	 * @param obj
	 *            the id of target ui object.
	 * @param percent
	 *            percentage of the object's diagonal length for the pinch
	 *            gesture
	 * @param steps
	 *            the number of steps for the gesture. Steps are injected about
	 *            5 milliseconds apart, so 100 steps may take around 0.5 seconds
	 *            to complete.
	 * @return true if all touch events for this gesture are injected
	 *         successfully, false otherwise
	 * @throws com.android.uiautomator.core.UiObjectNotFoundException
	 * 
	 * @throws com.github.uiautomatorstub.NotImplementedException
	 * 
	 */
	@Override
	public boolean pinchIn(String obj, int percent, int steps) throws UiObjectNotFoundException,
			NotImplementedException {
		return pinchIn(getUiObject(obj), percent, steps);
	}

	/**
	 * Performs a two-pointer gesture, where each pointer moves diagonally
	 * opposite across the other, from the center out towards the edges of the
	 * this UiObject.
	 * 
	 * @param obj
	 *            the id of target ui object.
	 * @param percent
	 *            percentage of the object's diagonal length for the pinch
	 *            gesture
	 * @param steps
	 *            the number of steps for the gesture. Steps are injected about
	 *            5 milliseconds apart, so 100 steps may take around 0.5 seconds
	 *            to complete.
	 * @return true if all touch events for this gesture are injected
	 *         successfully, false otherwise
	 * @throws com.android.uiautomator.core.UiObjectNotFoundException
	 * 
	 * @throws com.github.uiautomatorstub.NotImplementedException
	 * 
	 */
	@Override
	public boolean pinchOut(String obj, int percent, int steps) throws UiObjectNotFoundException,
			NotImplementedException {
		return pinchOut(getUiObject(obj), percent, steps);
	}

	/**
	 * Performs the swipe up/down/left/right action on the UiObject
	 * 
	 * @param obj
	 *            the id of target ui object.
	 * @param dir
	 *            "u"/"up", "d"/"down", "l"/"left", "r"/"right"
	 * @param steps
	 *            indicates the number of injected move steps into the system.
	 *            Steps are injected about 5ms apart. So a 100 steps may take
	 *            about 1/2 second to complete.
	 * @return true of successful
	 * @throws com.android.uiautomator.core.UiObjectNotFoundException
	 * 
	 */
	@Override
	public boolean swipe(String obj, String dir, int steps) throws UiObjectNotFoundException {
		return swipe(getUiObject(obj), dir, steps);
	}

	/**
	 * Waits a specified length of time for a view to become visible. This
	 * method waits until the view becomes visible on the display, or until the
	 * timeout has elapsed. You can use this method in situations where the
	 * content that you want to select is not immediately displayed.
	 * 
	 * @param obj
	 *            the id of target ui object
	 * @param timeout
	 *            time to wait (in milliseconds)
	 * @return true if the view is displayed, else false if timeout elapsed
	 *         while waiting
	 */
	@Override
	public boolean waitForExists(String obj, long timeout) throws UiObjectNotFoundException {
		return getUiObject(obj).waitForExists(timeout);
	}

	/**
	 * Waits a specified length of time for a view to become undetectable. This
	 * method waits until a view is no longer matchable, or until the timeout
	 * has elapsed. A view becomes undetectable when the UiSelector of the
	 * object is unable to find a match because the element has either changed
	 * its state or is no longer displayed. You can use this method when
	 * attempting to wait for some long operation to compete, such as
	 * downloading a large file or connecting to a remote server.
	 * 
	 * @param obj
	 *            the id of target ui object
	 * @param timeout
	 *            time to wait (in milliseconds)
	 * @return true if the element is gone before timeout elapsed, else false if
	 *         timeout elapsed but a matching element is still found.
	 */
	@Override
	public boolean waitUntilGone(String obj, long timeout) throws UiObjectNotFoundException {
		return getUiObject(obj).waitUntilGone(timeout);
	}

	/**
	 * Click on the app button , scroll to the side, click on the wanted
	 * application
	 * 
	 * @param appName
	 *            application wanted name to open
	 * @return true if the element is clicked and opened new window, false if we
	 *         catch exception
	 */
	public boolean openApp(String appName) throws UiObjectNotFoundException {

		System.out.println("opening application");
		UiDevice.getInstance().pressHome();
		UiObject allAppsButton = new UiObject(new UiSelector().description("Apps"));
		allAppsButton.clickAndWaitForNewWindow();
		UiObject appsTab = new UiObject(new UiSelector().text("Apps"));
		appsTab.click();
		UiScrollable appViews = new UiScrollable(new UiSelector().scrollable(true));
		appViews.setAsHorizontalList();
		try {
			UiObject app = appViews.getChildByText(new UiSelector().className(android.widget.TextView.class.getName()),
					appName);
			app.clickAndWaitForNewWindow();
		} catch (UiObjectNotFoundException e) {
			return false;
		}
		return true;
	}

	/**
	 * return the screen Height
	 * */
	public int getScreenHeight() {
		return UiDevice.getInstance().getDisplayHeight();
	}

	/**
	 * return the screen width
	 * */
	public int getScreenWidth() {
		return UiDevice.getInstance().getDisplayWidth();
	}

	public String getProp(String key) throws Exception {

		Class clazz = null;
		clazz = Class.forName("android.os.SystemProperties");
		Method method = clazz.getDeclaredMethod("get", String.class);
		String prop = (String)method.invoke(null, key);
		return prop;
		
		//		StringBuilder result = new StringBuilder();
//
//		Process proc = Runtime.getRuntime().exec(new String[] { "/system/bin/getprop", key });
//		BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
//		String line;
//		while ((line = reader.readLine()) != null) {
//			result.append(line);
//
//			Log.i("*************************" + line + "*************************************************************");
//		}
//		reader.close();
//		proc.waitFor();
//		Log.i("*********************my prop is: " + result.toString() + "********************");
//		return result.toString();
		// StringBuilder result = new StringBuilder();
		// Process process = Runtime.getRuntime().exec("getprop | grep " + key);
		// DataOutputStream os = new
		// DataOutputStream(process.getOutputStream());
		// DataInputStream osRes = new
		// DataInputStream(process.getInputStream());
		//
		// os.writeBytes("getprop | grep " + key + "\n");
		// os.flush();
		// result.append(osRes.readLine());
		//
		// os.writeBytes("exit\n");
		// os.flush();
		// process.waitFor();
		// return result.toString();

		// StringBuilder result = new StringBuilder();
		// try {
		// String line;
		//
		// Process p = Runtime.getRuntime().exec( "su", "-c",
		// "getprop | grep " + key });
		// BufferedReader bri = new BufferedReader(new
		// InputStreamReader(p.getInputStream()));
		// while ((line = bri.readLine()) != null) {
		// result.append(line);
		// Log.i("*************************" + line
		// + "*************************************************************");
		// }
		// bri.close();
		// p.waitFor();
		// } catch (Exception err) {
		// err.printStackTrace();
		// }
		// return result.toString();
	}
	
	
	
	/**
	 * returns the system answer after excuting the command
	 * @param command - the wanted command to exe
	 * @return system answer
	 * */
	public String excuteCommand(String command) throws Exception {
		
		try {  
			Process p = Runtime.getRuntime().exec(command);  
			BufferedReader in = new BufferedReader(  
			                                new InputStreamReader(p.getInputStream()));  
			String line = null; 
			StringBuilder output = new StringBuilder();
//			p.waitFor();
			output.append("");
			
			while ((line = in.readLine()) != null) {  
				output.append(line);
				output.append("\n");
			}  
			return output.toString();
		} catch (Exception e) {  
				e.printStackTrace();  
				return "Error :" + e.getMessage() ;
		} 
		
		
	}

}
