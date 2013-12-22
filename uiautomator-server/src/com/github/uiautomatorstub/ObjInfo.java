package com.github.uiautomatorstub;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;

public class ObjInfo {

    public static final ObjInfo getObjInfo(UiObject obj) throws UiObjectNotFoundException {
        return new ObjInfo(obj);
    }

    public static final ObjInfo getObjInfo(UiSelector selector) throws UiObjectNotFoundException {
        return new ObjInfo(new UiObject(selector));
    }

	private ObjInfo(UiObject obj) throws UiObjectNotFoundException {
		this._bounds = Rect.from(obj.getBounds());
		this._checkable = obj.isCheckable();
		this._checked = obj.isChecked();
		this._chileCount = obj.getChildCount();
		this._clickable = obj.isClickable();
		this._contentDescription = obj.getContentDescription();
		this._enabled = obj.isEnabled();
		this._focusable = obj.isFocusable();
		this._focused = obj.isFocused();
		this._longClickable = obj.isLongClickable();
		this._packageName = obj.getPackageName();
		this._scrollable = obj.isScrollable();
		this._selected = obj.isSelected();
		this._text = obj.getText();
		if (android.os.Build.VERSION.SDK_INT >= 17) {
			this._visibleBounds = Rect.from(obj.getVisibleBounds());
		}
		if (android.os.Build.VERSION.SDK_INT >= 18) {
			this._className = obj.getClassName();
		}
	}

	private Rect _bounds;
	private Rect _visibleBounds;
	private int _chileCount;
	private String _className;
	private String _contentDescription;
	private String _packageName;
	private String _text;
	private boolean _checkable;
	private boolean _checked;
	private boolean _clickable;
	private boolean _enabled;
	private boolean _focusable;
	private boolean _focused;
	private boolean _longClickable;
	private boolean _scrollable;
	private boolean _selected;

	public Rect getBounds() {
		return _bounds;
	}

	public void setBounds(Rect bounds) {
		this._bounds = bounds;
	}

	public Rect getVisibleBounds() {
		return _visibleBounds;
	}

	public void setVisibleBounds(Rect visibleBounds) {
		this._visibleBounds = visibleBounds;
	}

	public int getChileCount() {
		return _chileCount;
	}

	public void setChileCount(int chileCount) {
		this._chileCount = chileCount;
	}

	public String getClassName() {
		return _className;
	}

	public void setClassName(String className) {
		this._className = className;
	}

	public String getContentDescription() {
		return _contentDescription;
	}

	public void setContentDescription(String contentDescription) {
		this._contentDescription = contentDescription;
	}

	public String getPackageName() {
		return _packageName;
	}

	public void setPackageName(String packageName) {
		this._packageName = packageName;
	}

	public String getText() {
		return _text;
	}

	public void setText(String text) {
		this._text = text;
	}

	public boolean isCheckable() {
		return _checkable;
	}

	public void setCheckable(boolean checkable) {
		this._checkable = checkable;
	}

	public boolean isChecked() {
		return _checked;
	}

	public void setChecked(boolean checked) {
		this._checked = checked;
	}

	public boolean isClickable() {
		return _clickable;
	}

	public void setClickable(boolean clickable) {
		this._clickable = clickable;
	}

	public boolean isEnabled() {
		return _enabled;
	}

	public void setEnabled(boolean enabled) {
		this._enabled = enabled;
	}

	public boolean isFocusable() {
		return _focusable;
	}

	public void setFocusable(boolean focusable) {
		this._focusable = focusable;
	}

	public boolean isFocused() {
		return _focused;
	}

	public void setFocused(boolean focused) {
		this._focused = focused;
	}

	public boolean isLongClickable() {
		return _longClickable;
	}

	public void setLongClickable(boolean longClickable) {
		this._longClickable = longClickable;
	}

	public boolean isScrollable() {
		return _scrollable;
	}

	public void setScrollable(boolean scrollable) {
		this._scrollable = scrollable;
	}

	public boolean isSelected() {
		return _selected;
	}

	public void setSelected(boolean selected) {
		this._selected = selected;
	}
}
