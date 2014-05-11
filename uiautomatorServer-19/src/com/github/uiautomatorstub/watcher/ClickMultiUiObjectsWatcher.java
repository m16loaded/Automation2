package com.github.uiautomatorstub.watcher;

import java.io.File;

import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;
import com.github.uiautomatorstub.Log;

public class ClickMultiUiObjectsWatcher extends SelectorWatcher {

	  private UiSelector[] targets = null;

	    public ClickMultiUiObjectsWatcher(UiSelector[] conditions, UiSelector[] targets) {
	        super(conditions);
	        this.targets = targets;
	    }

	@Override
	public void action() {
		if (targets != null) {
            try {
                UiDevice.getInstance().takeScreenshot(new File("/data/local/tmp/wathcer.png"));
                for (UiSelector target : targets){
                    new UiObject(target).click();
                }
            } catch (UiObjectNotFoundException e) {
                Log.d(e.getMessage());
            }
		}

	}

}
