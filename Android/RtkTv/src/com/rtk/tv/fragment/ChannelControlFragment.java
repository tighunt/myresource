package com.rtk.tv.fragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.Locale;

import com.rtk.tv.R;
import com.rtk.tv.TvManagerHelper;
import com.rtk.tv.data.ChannelInfo;

public class ChannelControlFragment extends BaseFragment {	
	private static final int MAX_DIGIT = 3;
	private static final int MAX_CHANNEL = 999;	
	private static final long DELAY_SMALL = 1000;
	private static final long DELAY_LARGE = 3000;
	private static final long DELAY_DISMISS = 3000;
	private TvManagerHelper mTvManager;
	private Handler mHandler;
	private int mFirstKey = -1;
	private TextView mTextChannel;
	// Input status
	private int mCurrentInput = -1;
	private int mCurrentDigit = 0;
	
	public void setFirstKey(int keyCode) {
		mFirstKey = keyCode;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mTvManager = TvManagerHelper.getInstance(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHandler = new Handler();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_fragment_channel_control,
				container, false);
		mTextChannel = (TextView) v.findViewById(R.id.text_channel_control);
		return v;
	}

	@Override
	public void onStart() {
		super.onStart();
		// Post dismiss
		mHandler.postDelayed(mRunDismiss, DELAY_DISMISS);		
		resetInput();	
		// Handle first key event
		if (mFirstKey > 0) {
			handleKeyCode(mFirstKey);
			mFirstKey = -1;
		}
		// Update view
		if (!isInputMode()) {
			updateChannelView();
		}		
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		removeAllCallback();
	}

	private boolean isInputMode() {
		return mCurrentDigit > 0;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return handleKeyCode(keyCode);
	}

	protected void updateChannelView() {
		if (mTextChannel != null) {
			ChannelInfo ci = mTvManager.getCurrentChannelInfo();
			mTextChannel.setText(ci.formatNumber("%03d"));
		}
	}

	private boolean handleKeyCode(int keyCode) {
		int num = -1;
		switch (keyCode) {
		// Input
		case KeyEvent.KEYCODE_0:num = 0;break;
		case KeyEvent.KEYCODE_1:num = 1;break;
		case KeyEvent.KEYCODE_2:num = 2;break;
		case KeyEvent.KEYCODE_3:num = 3;break;
		case KeyEvent.KEYCODE_4:num = 4;break;
		case KeyEvent.KEYCODE_5:num = 5;break;
		case KeyEvent.KEYCODE_6:num = 6;break;
		case KeyEvent.KEYCODE_7:num = 7;break;
		case KeyEvent.KEYCODE_8:num = 8;break;
		case KeyEvent.KEYCODE_9:num = 9;break;
		// Confirm
		case KeyEvent.KEYCODE_DPAD_CENTER:
			setChannelNumber(mCurrentInput);
			return true;
		default:
			return false;
		}

		// Handle digit keys
		if (mCurrentDigit >= MAX_DIGIT) {
			resetInput();
		}
		
		if (mCurrentDigit <= 0) {
			mCurrentInput = num;
		} else {
			mCurrentInput *= 10;
			mCurrentInput += num;
		}
		mCurrentDigit++;
		
		mTextChannel.setText(formatInputDigit(mCurrentInput, mCurrentDigit));
		
		removeAllCallback();
		if (mCurrentDigit >= MAX_DIGIT) {
			mHandler.postDelayed(mRunConfirmInput, DELAY_SMALL);
		} else {
			mHandler.postDelayed(mRunConfirmInput, DELAY_LARGE);
		}
		
		return true;
	}
	
	private Runnable mRunConfirmInput = new Runnable() {
		
		@Override
		public void run() {
			setChannelNumber(mCurrentInput);
		}
	};
	
	private Runnable mRunUpdateChannel = new Runnable() {
		
		@Override
		public void run() {
			removeAllCallback();
			resetInput();
			updateChannelView();
			mHandler.postDelayed(mRunDismiss, DELAY_DISMISS);
		}
	};
	
	private Runnable mRunDismiss = new Runnable() {
		
		@Override
		public void run() {
			removeAllCallback();
			FragmentManager fm = getFragmentManager();
			if (fm != null) {
				fm.beginTransaction()
				.detach(ChannelControlFragment.this)
				.commit();
			}
		}
	};
	
	private void removeAllCallback() {
		mHandler.removeCallbacks(mRunConfirmInput);
		mHandler.removeCallbacks(mRunDismiss);
	}

	private void setChannelNumber(int channelNumber) {
		removeAllCallback();
		if (channelNumber >= 1 && channelNumber <= MAX_CHANNEL) {
			mTvManager.setChannel(channelNumber);
		} else {
			mRunUpdateChannel.run();
		}
		resetInput();
	}
	
	private void resetInput() {
		mCurrentInput = -1;
		mCurrentDigit = 0;
	}
	
	private static String formatInputDigit(int channel, int digitCount) {
		if (digitCount >= 3) {
			return String.format(Locale.US, "%03d", channel);
		} else if (digitCount >= 2) {
			return String.format(Locale.US, "-%02d", channel);
		} else {
			return String.format(Locale.US, "--%1d", channel);
		}
	}
}
