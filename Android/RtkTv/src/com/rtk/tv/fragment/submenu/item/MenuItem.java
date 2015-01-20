package com.rtk.tv.fragment.submenu.item;

import com.rtk.tv.R;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class MenuItem implements KeyEvent.Callback {
	
	public interface OnValueChangeListener{
		public void onValueChanged(MenuItem item, int value);
	}
	
	public static final int ITEM_TYPE_TEXT_ONLY = 0;
	public static final int ITEM_TYPE_SPINNER = 2;
	public static final int ITEM_TYPE_SEEK_BAR = 3;
	public static final int ITEM_TYPE_DIGIT = 4;
	
	// Basic properties
	public int title = 0;
	public String titleString = "";
	public final int type;
	
	// Callbacks
	private OnClickListener mClickListener;
	private OnValueChangeListener mValueChangeListener;
	
	// Data and status
	private boolean enable;
	
	private View mView;
	private TextView mTextValue;
	private TextView mTextTitle;
	
	public static MenuItem createTextItem(int title) {
		MenuItem item = new MenuItem(title, ITEM_TYPE_TEXT_ONLY);
		return item;
	}
    
    public static MenuItem createTextItem(String title) {
        MenuItem item = new MenuItem(title, ITEM_TYPE_TEXT_ONLY);
        return item;
    }
	
	public static SpinnerMenuItem createSpinnerItem(int title) {
		return new SpinnerMenuItem(title);
	}
    
    public static SpinnerMenuItem createSpinnerItem(String title) {
        return new SpinnerMenuItem(title);
    }
	
	public static SeekBarMenuItem createSeekBarItem(int title) {
		return new SeekBarMenuItem(title);
	}
	
	public static DigitMenuItem createDigitItem(int title) {
		return new DigitMenuItem(title);
	}
	
	protected MenuItem(int title, int type) {
		this.title = title;
		this.type = type;
		this.enable = true;
	}
    
    protected MenuItem(String title, int type) {
        this.titleString = title;
        this.type = type;
        this.enable = true;
    }
	
	public MenuItem setEnable(boolean enable) {
		this.enable = enable;
		return this;
	}
	
	public MenuItem setOnClickListener(View.OnClickListener listener) {
		mClickListener = listener;
		return this;
	}
	
	public MenuItem setOnValueChangeListener(OnValueChangeListener listener) {
		mValueChangeListener = listener;
		return this;
	}
	
	public boolean isEnable() {
		return enable;
	}
	
	public void selectNext() {
		
	}

	public void selectPrev() {
		
	}
	
	public void performClick() {
		if (mClickListener != null) {
			mClickListener.onClick(mView);
		}
	}
	
	protected void notifyValueChange(int value) {
		if (mValueChangeListener != null) {
			mValueChangeListener.onValueChanged(this, value);
		}
	}
	
	public View getView(View convertView, ViewGroup parent) {
		// Unbind previous view
		onUnbindView();

		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.layout_item_setting, parent, false);
		} else if (convertView.getTag() instanceof MenuItem) {
			((MenuItem)convertView.getTag()).onUnbindView();
		}
		convertView.setTag(this);
		mView = convertView;
		
		// Setup title
		mTextTitle = (TextView) convertView.findViewById(R.id.text_title);
		if (title > 0)
		    mTextTitle.setText(title);
		else 
		    mTextTitle.setText(titleString);

		// Bind view control
		mTextValue = (TextView) convertView.findViewById(R.id.text_value);
		ViewGroup container = (ViewGroup) convertView.findViewById(R.id.container_value_control);
		mTextValue.setVisibility(View.INVISIBLE);
		
		Integer t = (Integer) container.getTag();
		boolean inflate = t == null || t != type;
		if (inflate) {
			container.removeAllViews();
		}
		container.setTag(type);
		
		onBindView(inflater, container, inflate);
		return convertView;
	}
	
	protected void onBindView(LayoutInflater inflater, ViewGroup container, boolean inflate) {
		
	}
	
	protected void onUnbindView() {
		
	}
	
	protected void setTextValue(CharSequence text) {
		if (mTextValue != null) {
			if (mTextValue.getVisibility() != View.VISIBLE) {
				mTextValue.setVisibility(View.VISIBLE);
			}
			mTextValue.setText(text);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		return false;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return false;
	}

	@Override
	public boolean onKeyMultiple(int keyCode, int count, KeyEvent event) {
		return false;
	}

}
