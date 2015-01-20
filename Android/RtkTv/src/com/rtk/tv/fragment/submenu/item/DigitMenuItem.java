package com.rtk.tv.fragment.submenu.item;

import com.rtk.tv.R;
import com.rtk.tv.widget.EditTextController;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class DigitMenuItem extends MenuItem {

	
	private int mValue = 0;
	
	// View
	private int mMaxDigits = 3;
	private TextView mTextEdit;

	DigitMenuItem(int title) {
		super(title, ITEM_TYPE_DIGIT);
	}
	
	public DigitMenuItem setMaxDigits(int maxDigits) {
		mMaxDigits = maxDigits;
		if (mTextEdit != null) {
			EditTextController.bindNumberEditor(mTextEdit, mMaxDigits);
		}
		return this;
	}
	
	public DigitMenuItem setCurrentValue(int value) {
		return setCurrentValue(value, false);
	}
	
	public DigitMenuItem setCurrentValue(int value, boolean notify) {
		mValue = value;
		if (notify) {
			notifyValueChange(value);
		}
		updateContent();
		return this;
	}

	public int getCurrentValue() {
		return mValue;
	}

	@Override
	protected void onBindView(LayoutInflater inflater, ViewGroup container, boolean inflate) {
		if (inflate) {
			inflater.inflate(R.layout.layout_item_setting_edit, container);
		}
		mTextEdit = (TextView) container.findViewById(R.id.edit_value);
		EditTextController.bindNumberEditor(mTextEdit, mMaxDigits, mOnEdit);
		updateContent();
	}

	@Override
	protected void onUnbindView() {
		super.onUnbindView();
		if (mTextEdit != null) {
			mTextEdit.setOnKeyListener(null);
		}
	}

	private void updateContent() {
		if (mTextEdit == null) {
			return;
		}
		mTextEdit.setText(String.valueOf(mValue));
	}

	private OnEditorActionListener mOnEdit = new OnEditorActionListener() {
		
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			int value = Integer.parseInt(v.getText().toString());
			mValue = value;
			notifyValueChange(value);
			return true;
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mTextEdit == null) {
			return super.onKeyDown(keyCode, event);
		}
		return mTextEdit.dispatchKeyEvent(event);
	}

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		if (mTextEdit == null) {
			return super.onKeyLongPress(keyCode, event);
		}
		return mTextEdit.dispatchKeyEvent(event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (mTextEdit == null) {
			return super.onKeyUp(keyCode, event);
		}
		return mTextEdit.dispatchKeyEvent(event);
	}

	@Override
	public boolean onKeyMultiple(int keyCode, int count, KeyEvent event) {
		if (mTextEdit == null) {
			return super.onKeyMultiple(keyCode, count, event);
		}
		return mTextEdit.dispatchKeyEvent(event);
	}

}
