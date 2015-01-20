package com.rtk.tv.fragment.submenu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;

import com.rtk.tv.R;
import com.rtk.tv.fragment.submenu.item.MenuItem;
import com.rtk.tv.fragment.submenu.item.MenuItem.OnValueChangeListener;
import com.rtk.tv.fragment.submenu.item.SpinnerMenuItem;

import java.util.List;

public class ApplicationMenuFragment extends BaseMenuFragment {

	private static final int MIN_SECS = 60;
	private static final int UPDATE_INTERVAL = 3000;

	/**
	 * <string-array name="sleep_timer_entires">
	 */
	public static final int[] SLEEP_TIMERS = {
		-1,
		5 * MIN_SECS,
		10 * MIN_SECS,
		15 * MIN_SECS,
		30 * MIN_SECS,
		45 * MIN_SECS,
		60 * MIN_SECS,
		90 * MIN_SECS,
		120 * MIN_SECS,
		180 * MIN_SECS,
		240 * MIN_SECS,
	};
	
	public static final int[] ON_TIMERS_MINS = {
		-1,
		5,
		10,
		15,
		30,
		45,
		60,
		90,
		120,
		180,
		240,
	};
	
	private SpinnerMenuItem mItemTimer;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			updateSleepTimer();
			if (isResumed()) {
				sendEmptyMessageDelayed(0, UPDATE_INTERVAL);
			}
		}
		
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}

	@Override
	public void onResume() {
		super.onResume();
		updateSleepTimer();
		mHandler.sendEmptyMessageDelayed(0, UPDATE_INTERVAL);
	}

	@Override
	public void onPause() {
		super.onPause();
		mHandler.removeMessages(0);
	}

	@Override
	public void onCreateMenuItems(List<MenuItem> items) {
		Context context = getActivity();
		// On Timer
		int onTimerMin = 10;/*mTvManager.getOnTimerMinutes();*/
		String onTimerLabel = getTimeMinutesLabel(context, onTimerMin);
		String[] onTimeTitles = new String[ON_TIMERS_MINS.length];
		int onTimerIndex = 0;
		for (int i = 0; i < ON_TIMERS_MINS.length; i++) {
			onTimeTitles[i] = getTimeMinutesLabel(context, ON_TIMERS_MINS[i]);
			if (ON_TIMERS_MINS[i] == onTimerMin) {
				onTimerIndex = i;
			}
		}
		items.add(
			MenuItem.createSpinnerItem(R.string.STRING_A_ON_TIMER)
			.setSpinnerOptionsByArray(onTimeTitles, ON_TIMERS_MINS)
			.setCurrentPosition(onTimerIndex)
			.setTempOption(onTimerLabel)
			.setOnValueChangeListener(new OnValueChangeListener() {
				
				@Override
				public void onValueChanged(MenuItem item, int value) {
					//mTvManager.setOnTimerMinutes(value);
				}
			})
		);
		
		// Sleep Timer
		mItemTimer = MenuItem.createSpinnerItem(R.string.STRING_A_SLEEP_TIMER);
		mItemTimer.setOnValueChangeListener(new OnValueChangeListener() {
				
				@Override
				public void onValueChanged(MenuItem item, int value) {
					// Monkey is not allowed to setup sleep timer
					if (!ActivityManager.isUserAMonkey()) {
						//mTvManager.setSleepTimerSec(value);
					}
				}
			});
		items.add(mItemTimer);
		updateSleepTimer();
		
		// Media Player
		items.add(
			MenuItem.createTextItem(R.string.STRING_A_MEDIAPLAYER)
			.setOnClickListener(new OnClickListener() {
			
				@Override
				public void onClick(View v) {
					Activity a = getActivity();
					Intent intent = a.getPackageManager()
						.getLaunchIntentForPackage("com.rtk.mediabrowser");
					if (intent != null) {
						a.startActivity(intent);
					}
					getFragmentManager().popBackStack();
				}
		}));
		
		
		// System Setting
		items.add(
			MenuItem.createTextItem(R.string.STRING_SYSTEM_SETTINGS)
			.setOnClickListener(new OnClickListener() {
			
				@Override
				public void onClick(View v) {
					Activity a = getActivity();
					Intent intent = new Intent(Settings.ACTION_SETTINGS);
					a.startActivity(intent);
					getFragmentManager().popBackStack();
				}
		}));
	}

	@Override
	public int getTitle() {
		return R.string.STRING_APPLICATION;
	}

	private void updateSleepTimer() {
		if (mItemTimer == null) {
			return;
		}
		
		// Get option title
		String[] timerTitles = getResources().getStringArray(R.array.sleep_timer_entires);
		int timeout = 100 /*mTvManager.getSleepTimerSec()*/;
		int timeoutMin = Math.round(timeout / 60F);
		String timer = getString(R.string.format_time_minutes, timeoutMin);
		int timerIdx = 0;
		for (int i = SLEEP_TIMERS.length - 1; i >= 0; i--) {
			if (timeout > SLEEP_TIMERS[i]) {
				timerIdx = i;
				break;
			} else if (timeout == SLEEP_TIMERS[i]) {
				timer = timerTitles[i]; 
				timerIdx = i;
				break;
			}
		}
		
		mItemTimer
		.setSpinnerOptionsByArray(timerTitles, SLEEP_TIMERS)
		.setCurrentPosition(timerIdx)
		.setTempOption(timer);
		notifyDataSetChanged();
	}
	
	private static final String getTimeMinutesLabel(Context context, int minutes) {
		String label;
		if (minutes > 0) {
			label = context.getString(R.string.format_time_minutes, minutes);
		} else {
			label = context.getString(R.string.STRING_OFF);
		}
		return label;
	}
}
