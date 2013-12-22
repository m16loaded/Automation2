package org.topq.uiautomator;

import com.android.uiautomator.core.UiSelector;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class Selector {

	private String text;
	private String textContains;
	private String textMatches;
	private String textStartsWith;
	private String className;
	private String classNameMatches;
	private String description;
	private String descriptionContains;
	private String descriptionMatches;
	private String descriptionStartsWith;
	private boolean checkable;
	private boolean checked;
	private boolean clickable;
	private boolean longClickable;
	private boolean scrollable;
	private boolean enabled;
	private boolean focusable;
	private boolean focused;
	private boolean selected;
	private String packageName;
	private String packageNameMatches;
	private String resourceId;
	private String resourceIdMatches;
	private int index;
	private int instance;
	private Selector[] childOrSiblingSelector = new Selector[] {};
	private String[] childOrSibling = new String[] {};

	private long mask = 0;

	public static final long MASK_TEXT = 0x01;
	public static final long MASK_TEXTCONTAINS = 0x02;
	public static final long MASK_TEXTMATCHES = 0x04;
	public static final long MASK_TEXTSTARTSWITH = 0x08;
	public static final long MASK_CLASSNAME = 0x10;
	public static final long MASK_CLASSNAMEMATCHES = 0x20;
	public static final long MASK_DESCRIPTION = 0x40;
	public static final long MASK_DESCRIPTIONCONTAINS = 0x80;
	public static final long MASK_DESCRIPTIONMATCHES = 0x0100;
	public static final long MASK_DESCRIPTIONSTARTSWITH = 0x0200;
	public static final long MASK_CHECKABLE = 0x0400;
	public static final long MASK_CHECKED = 0x0800;
	public static final long MASK_CLICKABLE = 0x1000;
	public static final long MASK_LONGCLICKABLE = 0x2000;
	public static final long MASK_SCROLLABLE = 0x4000;
	public static final long MASK_ENABLED = 0x8000;
	public static final long MASK_FOCUSABLE = 0x010000;
	public static final long MASK_FOCUSED = 0x020000;
	public static final long MASK_SELECTED = 0x040000;
	public static final long MASK_PACKAGENAME = 0x080000;
	public static final long MASK_PACKAGENAMEMATCHES = 0x100000;
	public static final long MASK_RESOURCEID = 0x200000;
	public static final long MASK_RESOURCEIDMATCHES = 0x400000;
	public static final long MASK_INDEX = 0x800000;
	public static final long MASK_INSTANCE = 0x01000000;

	public UiSelector toUiSelector() {
		UiSelector s = new UiSelector();
		if ((getMask() & Selector.MASK_CHECKABLE) > 0
				&& android.os.Build.VERSION.SDK_INT >= 18)
			s = s.clickable(this.isClickable());
		if ((getMask() & Selector.MASK_CHECKED) > 0)
			s = s.checked(isChecked());
		if ((getMask() & Selector.MASK_CLASSNAME) > 0
				&& android.os.Build.VERSION.SDK_INT >= 17)
			s = s.className(getClassName());
		if ((getMask() & Selector.MASK_CLASSNAMEMATCHES) > 0
				&& android.os.Build.VERSION.SDK_INT >= 17)
			s = s.classNameMatches(getClassNameMatches());
		if ((getMask() & Selector.MASK_CLICKABLE) > 0)
			s = s.clickable(isClickable());
		if ((getMask() & Selector.MASK_DESCRIPTION) > 0)
			s = s.description(getDescription());
		if ((getMask() & Selector.MASK_DESCRIPTIONCONTAINS) > 0)
			s = s.descriptionContains(getDescriptionContains());
		if ((getMask() & Selector.MASK_DESCRIPTIONMATCHES) > 0
				&& android.os.Build.VERSION.SDK_INT >= 17)
			s = s.descriptionMatches(getDescriptionMatches());
		if ((getMask() & Selector.MASK_DESCRIPTIONSTARTSWITH) > 0)
			s = s.descriptionStartsWith(getDescriptionStartsWith());
		if ((getMask() & Selector.MASK_ENABLED) > 0)
			s = s.enabled(isEnabled());
		if ((getMask() & Selector.MASK_FOCUSABLE) > 0)
			s = s.focusable(isFocusable());
		if ((getMask() & Selector.MASK_FOCUSED) > 0)
			s = s.focused(isFocused());
		if ((getMask() & Selector.MASK_INDEX) > 0)
			s = s.index(getIndex());
		if ((getMask() & Selector.MASK_INSTANCE) > 0)
			s = s.instance(getInstance());
		if ((getMask() & Selector.MASK_LONGCLICKABLE) > 0
				&& android.os.Build.VERSION.SDK_INT >= 17)
			s = s.longClickable(isLongClickable());
		if ((getMask() & Selector.MASK_PACKAGENAME) > 0)
			s = s.packageName(getPackageName());
		if ((getMask() & Selector.MASK_PACKAGENAMEMATCHES) > 0
				&& android.os.Build.VERSION.SDK_INT >= 17)
			s = s.packageNameMatches(getPackageNameMatches());
		if ((getMask() & Selector.MASK_RESOURCEID) > 0
				&& android.os.Build.VERSION.SDK_INT >= 18)
			s = s.resourceId(getResourceId());
		if ((getMask() & Selector.MASK_RESOURCEIDMATCHES) > 0
				&& android.os.Build.VERSION.SDK_INT >= 18)
			s = s.resourceIdMatches(getResourceIdMatches());
		if ((getMask() & Selector.MASK_SCROLLABLE) > 0)
			s = s.scrollable(isScrollable());
		if ((getMask() & Selector.MASK_SELECTED) > 0)
			s = s.selected(isSelected());
		if ((getMask() & Selector.MASK_TEXT) > 0)
			s = s.text(getText());
		if ((getMask() & Selector.MASK_TEXTCONTAINS) > 0)
			s = s.textContains(getTextContains());
		if ((getMask() & Selector.MASK_TEXTSTARTSWITH) > 0)
			s = s.textStartsWith(getTextStartsWith());
		if ((getMask() & Selector.MASK_TEXTMATCHES) > 0
				&& android.os.Build.VERSION.SDK_INT >= 17)
			s = s.textMatches(getTextMatches());

		for (int i = 0; i < this.getChildOrSibling().length
				&& i < this.getChildOrSiblingSelector().length; i++) {
			if (this.getChildOrSibling()[i].toLowerCase().equals("child"))
				s = s.childSelector(getChildOrSiblingSelector()[i]
						.toUiSelector());
			else if (this.getChildOrSibling()[i].toLowerCase()
					.equals("sibling"))
				s = s.fromParent((getChildOrSiblingSelector()[i].toUiSelector()));
		}

		return s;
	}

	@JsonInclude(Include.NON_DEFAULT)
	public String getText() {
		return text;
	}

	@JsonInclude(Include.NON_DEFAULT)
	public String getTextContains() {
		return textContains;
	}

	@JsonInclude(Include.NON_DEFAULT)
	public String getTextMatches() {
		return textMatches;
	}

	@JsonInclude(Include.NON_DEFAULT)
	public String getTextStartsWith() {
		return textStartsWith;
	}

	@JsonInclude(Include.NON_DEFAULT)
	public String getClassName() {
		return className;
	}

	@JsonInclude(Include.NON_DEFAULT)
	public String getClassNameMatches() {
		return classNameMatches;
	}

	@JsonInclude(Include.NON_DEFAULT)
	public String getDescription() {
		return description;
	}

	@JsonInclude(Include.NON_DEFAULT)
	public String getDescriptionContains() {
		return descriptionContains;
	}

	@JsonInclude(Include.NON_DEFAULT)
	public String getDescriptionMatches() {
		return descriptionMatches;
	}

	@JsonInclude(Include.NON_DEFAULT)
	public String getDescriptionStartsWith() {
		return descriptionStartsWith;
	}

	@JsonInclude(Include.NON_DEFAULT)
	public boolean isCheckable() {
		return checkable;
	}

	@JsonInclude(Include.NON_DEFAULT)
	public boolean isChecked() {
		return checked;
	}

	@JsonInclude(Include.NON_DEFAULT)
	public boolean isClickable() {
		return clickable;
	}

	@JsonInclude(Include.NON_DEFAULT)
	public boolean isLongClickable() {
		return longClickable;
	}

	@JsonInclude(Include.NON_DEFAULT)
	public boolean isScrollable() {
		return scrollable;
	}

	@JsonInclude(Include.NON_DEFAULT)
	public boolean isEnabled() {
		return enabled;
	}

	@JsonInclude(Include.NON_DEFAULT)
	public boolean isFocusable() {
		return focusable;
	}

	@JsonInclude(Include.NON_DEFAULT)
	public boolean isFocused() {
		return focused;
	}

	@JsonInclude(Include.NON_DEFAULT)
	public boolean isSelected() {
		return selected;
	}

	@JsonInclude(Include.NON_DEFAULT)
	public String getPackageName() {
		return packageName;
	}

	@JsonInclude(Include.NON_DEFAULT)
	public String getPackageNameMatches() {
		return packageNameMatches;
	}

	@JsonInclude(Include.NON_DEFAULT)
	public String getResourceId() {
		return resourceId;
	}

	@JsonInclude(Include.NON_DEFAULT)
	public String getResourceIdMatches() {
		return resourceIdMatches;
	}

	@JsonInclude(Include.NON_DEFAULT)
	public int getIndex() {
		return index;
	}

	@JsonInclude(Include.NON_DEFAULT)
	public int getInstance() {
		return instance;
	}

	public Selector[] getChildOrSiblingSelector() {
		return childOrSiblingSelector;
	}

	public String[] getChildOrSibling() {
		return childOrSibling;
	}

	public long getMask() {
		return mask;
	}

	public Selector setText(String _text) {
		this.mask += MASK_TEXT;
		this.text = _text;
		return this;
	}

	public Selector setTextContains(String _textContains) {
		mask += MASK_TEXTCONTAINS;
		this.textContains = _textContains;
		return this;
	}

	public Selector setTextMatches(String _textMatches) {
		mask += MASK_TEXTMATCHES;
		this.textMatches = _textMatches;
		return this;
	}

	public Selector setTextStartsWith(String _textStartsWith) {
		mask += MASK_TEXTSTARTSWITH;
		this.textStartsWith = _textStartsWith;
		return this;
	}

	public Selector setClassName(String _className) {
		mask += MASK_CLASSNAME;
		this.className = _className;
		return this;
	}

	public Selector setClassNameMatches(String _classNameMatches) {
		mask += MASK_CLASSNAMEMATCHES;
		this.classNameMatches = _classNameMatches;
		return this;
	}

	public Selector setDescription(String _description) {
		this.mask += MASK_DESCRIPTION;
		this.description = _description;
		return this;
	}

	public Selector setDescriptionContains(String _descriptionContains) {
		mask += MASK_DESCRIPTIONCONTAINS;
		this.descriptionContains = _descriptionContains;
		return this;
	}

	public Selector setDescriptionMatches(String _descriptionMatches) {
		mask += MASK_DESCRIPTIONMATCHES;
		this.descriptionMatches = _descriptionMatches;
		return this;
	}

	public Selector setDescriptionStartsWith(String _descriptionStartsWith) {
		mask += MASK_DESCRIPTIONSTARTSWITH;
		this.descriptionStartsWith = _descriptionStartsWith;
		return this;
	}

	public Selector setCheckable(boolean _checkable) {
		mask += MASK_CHECKABLE;
		this.checkable = _checkable;
		return this;
	}

	public Selector setChecked(boolean _checked) {
		mask += MASK_CHECKED;
		this.checked = _checked;
		return this;
	}

	public Selector setClickable(boolean _clickable) {
		mask += MASK_CLICKABLE;
		this.clickable = _clickable;
		return this;
	}

	public Selector setLongClickable(boolean _longClickable) {
		mask += MASK_LONGCLICKABLE;
		this.longClickable = _longClickable;
		return this;
	}

	public Selector setScrollable(boolean _scrollable) {
		mask += MASK_SCROLLABLE;
		this.scrollable = _scrollable;
		return this;
	}

	public Selector setEnabled(boolean _enabled) {
		mask += MASK_ENABLED;
		this.enabled = _enabled;
		return this;
	}

	public Selector setFocusable(boolean _focusable) {
		mask += MASK_FOCUSABLE;
		this.focusable = _focusable;
		return this;
	}

	public Selector setFocused(boolean _focused) {
		mask += MASK_FOCUSED;
		this.focused = _focused;
		return this;
	}

	public Selector setSelected(boolean _selected) {
		mask += MASK_SELECTED;
		this.selected = _selected;
		return this;
	}

	public Selector setPackageName(String _packageName) {
		mask += MASK_PACKAGENAME;
		this.packageName = _packageName;
		return this;
	}

	public Selector setPackageNameMatches(String _packageNameMatches) {
		mask += MASK_PACKAGENAMEMATCHES;
		this.packageNameMatches = _packageNameMatches;
		return this;
	}

	public Selector setResourceId(String _resourceId) {
		mask += MASK_RESOURCEID;
		this.resourceId = _resourceId;
		return this;
	}

	public Selector setResourceIdMatches(String _resourceIdMatches) {
		mask += MASK_RESOURCEIDMATCHES;
		this.resourceIdMatches = _resourceIdMatches;
		return this;
	}

	public Selector setIndex(int _index) {
		mask += MASK_INDEX;
		this.index = _index;
		return this;
	}

	public Selector setInstance(int _instance) {
		mask += MASK_INSTANCE;
		this.instance = _instance;
		return this;
	}

	public Selector setChildOrSiblingSelector(Selector[] _childOrSiblingSelector) {
		this.childOrSiblingSelector = _childOrSiblingSelector;
		return this;
	}

	public Selector setChildOrSibling(String[] _childOrSibling) {
		this.childOrSibling = _childOrSibling;
		return this;
	}

	public void set_mask(long _mask) {
		this.mask = _mask;
	}

}