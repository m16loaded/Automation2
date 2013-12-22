package org.topq.uiautomator;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;
import com.fasterxml.jackson.annotation.JsonCreator;

public class ObjInfo {

	public static final ObjInfo getObjInfo(UiObject obj)
			throws UiObjectNotFoundException {
		return new ObjInfo(obj);
	}

	public static final ObjInfo getObjInfo(UiSelector selector)
			throws UiObjectNotFoundException {
		return new ObjInfo(new UiObject(selector));
	}

	public ObjInfo() {
		// TODO Auto-generated constructor stub
	}

	public ObjInfo(UiObject obj) throws UiObjectNotFoundException {
		this.bounds = Rect.from(obj.getBounds());
		this.checkable = obj.isCheckable();
		this.checked = obj.isChecked();
		this.chileCount = obj.getChildCount();
		this.clickable = obj.isClickable();
		this.contentDescription = obj.getContentDescription();
		this.enabled = obj.isEnabled();
		this.focusable = obj.isFocusable();
		this.focused = obj.isFocused();
		this.longClickable = obj.isLongClickable();
		this.packageName = obj.getPackageName();
		this.scrollable = obj.isScrollable();
		this.selected = obj.isSelected();
		this.text = obj.getText();
		if (android.os.Build.VERSION.SDK_INT >= 17) {
			this.visibleBounds = Rect.from(obj.getVisibleBounds());
		}
		if (android.os.Build.VERSION.SDK_INT >= 18) {
			this.className = obj.getClassName();
		}
	}

	private Rect bounds;
	private Rect visibleBounds;
	private int chileCount;
	private String className;
	private String contentDescription;
	private String packageName;
	private String text;
	private boolean checkable;
	private boolean checked;
	private boolean clickable;
	private boolean enabled;
	private boolean focusable;
	private boolean focused;
	private boolean longClickable;
	private boolean scrollable;
	private boolean selected;

	public Rect getBounds() {
		return bounds;
	}

	public void setBounds(Rect bounds) {
		this.bounds = bounds;
	}

	public Rect getVisibleBounds() {
		return visibleBounds;
	}

	public void setVisibleBounds(Rect visibleBounds) {
		this.visibleBounds = visibleBounds;
	}

	public int getChileCount() {
		return chileCount;
	}

	public void setChileCount(int chileCount) {
		this.chileCount = chileCount;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getContentDescription() {
		return contentDescription;
	}

	public void setContentDescription(String contentDescription) {
		this.contentDescription = contentDescription;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isCheckable() {
		return checkable;
	}

	public void setCheckable(boolean checkable) {
		this.checkable = checkable;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public boolean isClickable() {
		return clickable;
	}

	public void setClickable(boolean clickable) {
		this.clickable = clickable;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isFocusable() {
		return focusable;
	}

	public void setFocusable(boolean focusable) {
		this.focusable = focusable;
	}

	public boolean isFocused() {
		return focused;
	}

	public void setFocused(boolean focused) {
		this.focused = focused;
	}

	public boolean isLongClickable() {
		return longClickable;
	}

	public void setLongClickable(boolean longClickable) {
		this.longClickable = longClickable;
	}

	public boolean isScrollable() {
		return scrollable;
	}

	public void setScrollable(boolean scrollable) {
		this.scrollable = scrollable;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}