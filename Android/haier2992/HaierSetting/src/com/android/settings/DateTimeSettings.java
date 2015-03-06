package com.android.settings;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.content.res.XmlResourceParser;

import com.android.settings.datetime.DateFormatSelectDialog;
import com.android.settings.datetime.DateSettingDialog;
import com.android.settings.datetime.DateTimeViewHolder;
import com.android.settings.datetime.TimeSettingDialog;
import com.android.settings.datetime.ZoneListSelectDialog;

/**
 * 日期和时间设置的主界面
 * 
 * @author 杜聪甲(ducj@biaoqi.com.cn)
 * @date 2011-11-4 下午08:20:17
 * @since 1.0
 */
public class DateTimeSettings extends Activity {

	private final static String TAG = "DateTimeSettings";
	private final static String NAME = "share_pres";

	private final static String LAST_AUTO_DATE = "ls_time";

	private final static String AUTO_DATE_TIME = "is_auto";

	private final static String TIME_FORMAT = "24 hour";

	private static final String HOURS_12 = "12";

	private static final String HOURS_24 = "24";
	
	private static final String XMLTAG_TIMEZONE = "timezone";
	
	private final static String DATEFORMAT_INDEX = "date_format_index";

	private DateTimeViewHolder mDateTimeViewHolder;

	private int isSelected = 0;

	// 是否自动获取时间日期
	private boolean isAutoDateTime;

	// 是否是24小时制
	private boolean is24Hour;

	// private Calendar mDummyDate;

	private String dateFormat;

	private String LastAutodate;
	
	// 日期格式
    public String[] dateFormatStrings = {
            "MM-dd-yyyy", "dd-MM-yyyy", "yyyy-MM-dd"
    };

	// 更新日期格式
	private Handler updateDateAndTimeHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			dateFormat = data.getString("dateFormat");
			Log.d("Leewokan", "  dateFormat =  " + dateFormat);
			Settings.System.putString(getContentResolver(), Settings.System.DATE_FORMAT, dateFormat);
			Log.v("Date", "DateTimeSettings.updateDateAndTimeHandler......");
			updateTimeAndDateDisplay();
		};
	};

	// 更新日期
	private Handler updateDateHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			String year = data.getString("year");
			String month = data.getString("month");
			String day = data.getString("day");
			
			Log.v("Date", "DateTimeSettings.updateDateHandler....：date :.." + year  + ":" + month  + ":"+ day );
			onDateSet(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
		};
	};

	// 更新时间
	private Handler updateTimeHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			String hour = data.getString("hour");
			String minute = data.getString("minute");
			Log.v("Date", "DateTimeSettings.updateTimeHandler....：date :.." + hour  + ":" + minute);
			Log.v("Date", "DateTimeSettings.updateTimeHandler......");
			onTimeSet(Integer.parseInt(hour), Integer.parseInt(minute));
		};
	};

	// 更新时区
	private Handler updateZoneTimeHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				Log.v("Date", "DateTimeSettings.updateZoneTimeHandler......");
				updateTimeAndDateDisplay();
			}
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "  -----------onCreate()---------");
		setContentView(R.layout.date_time_setting);

		// 组件和数据的初始化
		findViews();

		// 初始化系统的日期格式，默认为：MM-dd-yyyy
		initSysDateFormat();

		Log.v("Date", "DateTimeSettings.onCreate......");
		// 更新时间日期UI显示
		updateTimeAndDateDisplay();

		// 响应各个控件的监听事件
		registerListeners();
	}

	/**
	 * 组件和数据的初始化
	 */
	private void findViews() {
		// mDummyDate = Calendar.getInstance();
		
		mDateTimeViewHolder = new DateTimeViewHolder(this);
		
		isAutoDateTime = getAutoState();
		Log.v("Date", "DateTimeSettings.findViews().isAutoDateTime:" + isAutoDateTime);

		if (isAutoDateTime) {
			mDateTimeViewHolder.mAutoBox.setChecked(true);
			mDateTimeViewHolder.mDate_Text.setTextColor(Color.argb(204, 128, 128, 128));
			mDateTimeViewHolder.current_date.setTextColor(Color.argb(204, 128, 128, 128));
			mDateTimeViewHolder.mTime_Text.setTextColor(Color.argb(204, 128, 128, 128));
			mDateTimeViewHolder.current_time.setTextColor(Color.argb(204, 128, 128, 128));
			mDateTimeViewHolder.mTimeZone_Text.setTextColor(Color.argb(204, 128, 128, 128));
			mDateTimeViewHolder.current_time_zone.setTextColor(Color.argb(204, 128, 128, 128));
		} else {
			mDateTimeViewHolder.mAutoBox.setChecked(false);
		}
		
		is24Hour = is24Hour();
		Log.v("Date", "DateTimeSettings.findViews().is24Hour:" + is24Hour);
		
		if (is24Hour) {
			mDateTimeViewHolder.mTimeBox.setChecked(true);
		} else {
			mDateTimeViewHolder.mTimeBox.setChecked(false);
		}

	}

	/**
	 * 响应各个控件的监听事件
	 */
	private void registerListeners() {

		Log.d(TAG, "  -----------registerListeners()---------");
		// 是否自动获取日期和时间
		mDateTimeViewHolder.mAutoBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				Log.v("Date", "DateTimeSettings.registerListeners().isAutoDateTime:" + isAutoDateTime);
				
				
				isAutoDateTime = isChecked;
				
				Log.v("Date", "DateTimeSettings.registerListeners().isChecked:" + isChecked);
				
				if (isAutoDateTime) {
					
					Log.v("Date", "DateTimeSettings.registerListeners().isAutoDateTime is true......");
					
					mDateTimeViewHolder.mDate_Text.setTextColor(Color.argb(204, 128, 128, 128));
					mDateTimeViewHolder.current_date.setTextColor(Color.argb(204, 128, 128, 128));
					mDateTimeViewHolder.mTime_Text.setTextColor(Color.argb(204, 128, 128, 128));
					mDateTimeViewHolder.current_time.setTextColor(Color.argb(204, 128, 128, 128));
					mDateTimeViewHolder.mTimeZone_Text.setTextColor(Color.argb(204, 128, 128, 128));
					mDateTimeViewHolder.current_time_zone.setTextColor(Color.argb(204, 128, 128, 128));
				} else {
					
					Log.v("Date", "DateTimeSettings.registerListeners().isAutoDateTime is flase......");
					
					mDateTimeViewHolder.mDate_Text.setTextColor(Color.argb(255, 255, 255, 255));
					mDateTimeViewHolder.current_date.setTextColor(Color.argb(255, 255, 255, 255));
					mDateTimeViewHolder.mTime_Text.setTextColor(Color.argb(255, 255, 255, 255));
					mDateTimeViewHolder.current_time.setTextColor(Color.argb(255, 255, 255, 255));
					mDateTimeViewHolder.mTimeZone_Text.setTextColor(Color.argb(255, 255, 255, 255));
					mDateTimeViewHolder.current_time_zone.setTextColor(Color.argb(255, 255, 255, 255));
				}
				commitCheckBoxValue();
			}
		});
		mDateTimeViewHolder.mAutoBox.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				changeViewSelected(0);
			}
		});
		mDateTimeViewHolder.mAutoTimeDate_RL.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				changeViewSelected(0);
			}
		});
		// 当前日期
		mDateTimeViewHolder.current_date.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isAutoDateTime) {
					DateSettingDialog dateSettingDialog = new DateSettingDialog(DateTimeSettings.this,
							updateDateHandler);
					dateSettingDialog.show();
					changeViewSelected(1);
				} else {
					changeViewSelectedWhenAutoDate(1);
				}
			}
		});
		mDateTimeViewHolder.mDate_RL.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isAutoDateTime) {
					DateSettingDialog dateSettingDialog = new DateSettingDialog(DateTimeSettings.this,
							updateDateHandler);
					dateSettingDialog.show();
					changeViewSelected(1);
				} else {
					changeViewSelectedWhenAutoDate(1);
				}
			}
		});
		// 当前时间
		mDateTimeViewHolder.current_time.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isAutoDateTime) {
					TimeSettingDialog timeSettingDialog = new TimeSettingDialog(DateTimeSettings.this,
							updateTimeHandler, is24Hour);
					timeSettingDialog.show();
					changeViewSelected(2);
				} else {
					changeViewSelectedWhenAutoDate(2);
				}
			}
		});
		mDateTimeViewHolder.mTime_RL.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isAutoDateTime) {
					TimeSettingDialog timeSettingDialog = new TimeSettingDialog(DateTimeSettings.this,
							updateTimeHandler, is24Hour);
					timeSettingDialog.show();
					changeViewSelected(2);
				} else {
					changeViewSelectedWhenAutoDate(2);
				}
			}
		});

		// 当前时区
		mDateTimeViewHolder.current_time_zone.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isAutoDateTime) {
					ZoneListSelectDialog zoneListSelectDialog = new ZoneListSelectDialog(DateTimeSettings.this,
							updateZoneTimeHandler);
					zoneListSelectDialog.show();
					changeViewSelected(3);
				} else {
					changeViewSelectedWhenAutoDate(3);
				}
			}
		});
		mDateTimeViewHolder.mTimeZone_RL.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isAutoDateTime) {
					ZoneListSelectDialog zoneListSelectDialog = new ZoneListSelectDialog(DateTimeSettings.this,
							updateZoneTimeHandler);
					zoneListSelectDialog.show();
					changeViewSelected(3);
				} else {
					changeViewSelectedWhenAutoDate(3);
				}
			}
		});
		// 是否是24小时
		mDateTimeViewHolder.mTimeBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				is24Hour = isChecked;
				Log.v("Date", "DateTimeSettings.mTimeBox.setOnCheckedChangeListener.onCheckedChanged......");
				commitCheckBox();
			}
		});
		mDateTimeViewHolder.mTimeBox.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				changeViewSelected(4);
			}
		});
		mDateTimeViewHolder.mTimeFormat_RL.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				changeViewSelected(4);
			}
		});
		// 日期格式选择
		mDateTimeViewHolder.date_format.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DateFormatSelectDialog dateFormatSelectDialog = new DateFormatSelectDialog(DateTimeSettings.this,
						updateDateAndTimeHandler);
				dateFormatSelectDialog.show();
				changeViewSelected(5);
			}
		});
		mDateTimeViewHolder.mDateFormat_RL.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DateFormatSelectDialog dateFormatSelectDialog = new DateFormatSelectDialog(DateTimeSettings.this,
						updateDateAndTimeHandler);
				dateFormatSelectDialog.show();
				changeViewSelected(5);
			}
		});
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {

		isAutoDateTime = getAutoState();

		if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			dropDown();
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			dropUp();
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	protected void onResume() {
		super.onResume();

		Log.d(TAG, "  -----------onResume()---------");
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_TIME_TICK);
		filter.addAction(Intent.ACTION_TIME_CHANGED);
		filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
		registerReceiver(mIntentReceiver, filter, null, null);

		 //updateTimeAndDateDisplay();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mIntentReceiver != null)
			unregisterReceiver(mIntentReceiver);
	}



	/**
	 * 是否自动获取时间
	 * 
	 * @return
	 */
	private boolean getAutoState() {
		try {
			return Settings.Global.getInt(getContentResolver(), Settings.Global.AUTO_TIME) > 0;
		} catch (SettingNotFoundException snfe) {
			Log.v("Date", "DateTimeSettings.getAutoState().Exception:" + snfe.getLocalizedMessage());
			return true;
		}
	}

	/**
	 * 获取时期格式
	 * 
	 * @return
	 */
	private String getDateFormat() {
		return Settings.System.getString(getContentResolver(), Settings.System.DATE_FORMAT);
	}


	private String getStringData(String key) {
		SharedPreferences preference = getSharedPreferences(NAME, Context.MODE_PRIVATE);
		return preference.getString(key, "");
	}

	
	/**
	 * 获取日期格式索引
	 * 
	 * @return index  0:"MM-dd-yyyy" 1:"dd-MM-yyyy" 2:"yyyy-MM-dd"
	 */
	private int getDateFormatIndex() {
		SharedPreferences preference = getSharedPreferences(NAME, Context.MODE_PRIVATE);
		return preference.getInt(DATEFORMAT_INDEX, 0);
	}
	
	/**
	 *  初始化系统的日期格式，默认为：MM-dd-yyyy
	 */
	public void initSysDateFormat(){
		String dateFormatStr = dateFormatStrings[getDateFormatIndex()];
		Log.d("Leewokan","  dateFormatStr = " + dateFormatStr);
		if(getDateFormat() == null || !dateFormatStr.equals(getDateFormat())) {
			Settings.System.putString(getContentResolver(), Settings.System.DATE_FORMAT, dateFormatStr);
		}
	}

	/**
	 * 是否是24小时格式
	 * 
	 * @return
	 */
	private boolean is24Hour() {
		return DateFormat.is24HourFormat(this);
	}

	/**
	 * 日期更新
	 * 
	 * @param year
	 * @param month
	 * @param day
	 */
	private void onDateSet(int year, int month, int day) {
		
		Log.v("Date", "DateTimeSettings.onDateSet......");

		Calendar c = Calendar.getInstance();

		Log.v("Date", "DateTimeSettings.onDateSet.11:" + c.getTimeInMillis());

		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);
		// c.set(year, month, day);
		long when = c.getTimeInMillis();
		
		Log.v("Date", "DateTimeSettings.onDateSet.when:" + when);
		Log.v("Date", "DateTimeSettings.onDateSet.when / 1000:" + when / 1000);
		Log.v("Date", "DateTimeSettings.onDateSet.Integer.MAX_VALUE:" + Integer.MAX_VALUE);
		Log.v("Date", "DateTimeSettings.onDateSet.isAutoDateTime:" + isAutoDateTime);

		if (when / 1000 < Integer.MAX_VALUE) {
			Log.v("Date", "DateTimeSettings.onDateSet.调用对时了......");
			SystemClock.setCurrentTimeMillis(when);
		}

		Log.v("Date", "DateTimeSettings.onDateSet.22:" + c.getTimeInMillis());
		updateTimeAndDateDisplay();
	}

	/**
	 * 时间更新
	 * 
	 * @param hourOfDay
	 * @param minute
	 */
	public void onTimeSet(int hourOfDay, int minute) {
		Calendar c = Calendar.getInstance();

		c.set(Calendar.HOUR_OF_DAY, hourOfDay);
		c.set(Calendar.MINUTE, minute);
		long when = c.getTimeInMillis();

		Log.v("Date", "DateTimeSettings.onTimeSet.isAutoDateTime:" + isAutoDateTime);

		if (when / 1000 < Integer.MAX_VALUE) {
			Log.v("Date", "DateTimeSettings.onDateSet.调用对时了......");
			SystemClock.setCurrentTimeMillis(when);
		}
		
		Log.v("Date", "DateTimeSettings.onTimeSet......");
		updateTimeAndDateDisplay();
	}

	/**
	 * 向下滑动
	 */
	private void dropDown() {
		switch (isSelected) {
		case 0:
			if (isAutoDateTime) {
				isSelected = 4;
				mDateTimeViewHolder.mAutoTimeDate_RL.setBackgroundResource(R.drawable.one_px);
				mDateTimeViewHolder.mTimeFormat_RL.setBackgroundResource(R.drawable.set_button);
				mDateTimeViewHolder.mTimeBox.requestFocus();
				System.out.println("mDateTimeViewHolder.mTimeBox hasFocus +" + mDateTimeViewHolder.mTimeBox.hasFocus());
			} else {
				isSelected = 1;
				mDateTimeViewHolder.mAutoTimeDate_RL.setBackgroundResource(R.drawable.one_px);
				mDateTimeViewHolder.mDate_RL.setBackgroundResource(R.drawable.set_button);
			}
			break;
		case 1:
			isSelected = 2;
			mDateTimeViewHolder.mDate_RL.setBackgroundResource(R.drawable.one_px);
			mDateTimeViewHolder.mTime_RL.setBackgroundResource(R.drawable.set_button);
			break;
		case 2:
			isSelected = 3;
			mDateTimeViewHolder.mTime_RL.setBackgroundResource(R.drawable.one_px);
			mDateTimeViewHolder.mTimeZone_RL.setBackgroundResource(R.drawable.set_button);
			break;
		case 3:
			isSelected = 4;
			mDateTimeViewHolder.mTimeZone_RL.setBackgroundResource(R.drawable.one_px);
			mDateTimeViewHolder.mTimeFormat_RL.setBackgroundResource(R.drawable.set_button);
			break;
		case 4:
			isSelected = 5;
			mDateTimeViewHolder.mTimeFormat_RL.setBackgroundResource(R.drawable.one_px);
			mDateTimeViewHolder.mDateFormat_RL.setBackgroundResource(R.drawable.set_button);
			break;
		}
	}

	/**
	 * 向上滑动
	 */
	private void dropUp() {
		switch (isSelected) {
		case 1:
			isSelected = 0;
			mDateTimeViewHolder.mDate_RL.setBackgroundResource(R.drawable.one_px);
			mDateTimeViewHolder.mAutoTimeDate_RL.setBackgroundResource(R.drawable.set_button);
			break;
		case 2:
			isSelected = 1;
			mDateTimeViewHolder.mTime_RL.setBackgroundResource(R.drawable.one_px);
			mDateTimeViewHolder.mDate_RL.setBackgroundResource(R.drawable.set_button);
			break;
		case 3:
			isSelected = 2;
			mDateTimeViewHolder.mTimeZone_RL.setBackgroundResource(R.drawable.one_px);
			mDateTimeViewHolder.mTime_RL.setBackgroundResource(R.drawable.set_button);
			break;
		case 4:
			if (isAutoDateTime) {
				isSelected = 0;
				mDateTimeViewHolder.mTimeFormat_RL.setBackgroundResource(R.drawable.one_px);
				mDateTimeViewHolder.mAutoTimeDate_RL.setBackgroundResource(R.drawable.set_button);
				mDateTimeViewHolder.mAutoBox.requestFocus();
				System.out.println("mDateTimeViewHolder.mAutoBox hasFocus +" + mDateTimeViewHolder.mAutoBox.hasFocus());
			} else {
				isSelected = 3;
				mDateTimeViewHolder.mTimeFormat_RL.setBackgroundResource(R.drawable.one_px);
				mDateTimeViewHolder.mTimeZone_RL.setBackgroundResource(R.drawable.set_button);
			}
			break;
		case 5:
			isSelected = 4;
			mDateTimeViewHolder.mDateFormat_RL.setBackgroundResource(R.drawable.one_px);
			mDateTimeViewHolder.mTimeFormat_RL.setBackgroundResource(R.drawable.set_button);
			break;
		}
	}

	/**
	 * 更新时间日期UI显示
	 */
	private void updateTimeAndDateDisplay() {
		
		SharedPreferences preference = getSharedPreferences(NAME, Context.MODE_PRIVATE);
		Log.v("Date", "DateTimeSettings.updateTimeAndDateDisplay()......");  
		java.text.DateFormat shortDateFormat = DateFormat.getDateFormat(this);
		Date now = Calendar.getInstance().getTime();
		
		Log.d("Leewokan", "  now =  " + now);
		String stringDateFormat = shortDateFormat.format(now);
		Log.v("Date", "DateTimeSettings.updateTimeAndDateDisplay().now:  stringDateFormat = " + stringDateFormat);
		

		if (isAutoDateTime) {
		
		Editor edit = preference.edit();
		edit.putString(LAST_AUTO_DATE, stringDateFormat);
		edit.commit();
			}
		//Date dummyDate = mDummyDate.getTime()
		//String lastdatetimeset = preference.getString(LAST_AUTO_DATE, 0);
		String lastdatetimeset = getStringData(LAST_AUTO_DATE);

		Log.d("Leewokan", " lastdatetimeset =   " + lastdatetimeset);
		if (lastdatetimeset == null || "".equals(lastdatetimeset)){
		          mDateTimeViewHolder.current_date.setText(shortDateFormat.format(now));
		          Log.d("Leewokan", " shortDateFormat.format(now) =  " + shortDateFormat.format(now).toString());
			}else{
			      mDateTimeViewHolder.current_date.setText(lastdatetimeset);
			      Log.d("Leewokan", " lastdatetimeset =  " + lastdatetimeset);
				}
			
		
		mDateTimeViewHolder.current_time.setText(DateFormat.getTimeFormat(this).format(now));
		mDateTimeViewHolder.current_time_zone.setText(getTimeZoneID());
		mDateTimeViewHolder.current_date.setText(stringDateFormat);
		
		mDateTimeViewHolder.date_format.setText(stringDateFormat);


		
	}







	/**
	 * 保存是否自动获取日期和时间
	 */
	private void commitCheckBoxValue() {
		
		Log.v("Date", "DateTimeSettings.commitCheckBoxValue()......"+isAutoDateTime);
		

		SharedPreferences preference = getSharedPreferences(NAME, Context.MODE_PRIVATE);
		Editor edit = preference.edit();
		edit.putBoolean(AUTO_DATE_TIME, isAutoDateTime);
		edit.commit();
		
		Settings.Global.putInt(getContentResolver(), Settings.Global.AUTO_TIME, isAutoDateTime ? 1 : 0);
	}

	/**
	 * 保存24小时CheckBox的值
	 */
	private void commitCheckBox() {

		SharedPreferences preference = getSharedPreferences(NAME, Context.MODE_PRIVATE);
		Editor edit = preference.edit();
		edit.putBoolean(TIME_FORMAT, is24Hour);
		edit.commit();

		Settings.System.putString(getContentResolver(), Settings.System.TIME_12_24, is24Hour ? HOURS_24 : HOURS_12);
		
		Log.v("Date", "DateTimeSettings.commitCheckBox()......");
		updateTimeAndDateDisplay();
		
		timeUpdated();
	}

	/**
	 * 时间更新
	 */
	private void timeUpdated() {
		Intent timeChanged = new Intent(Intent.ACTION_TIME_CHANGED);
		sendBroadcast(timeChanged);
	}

	/**
	 * 获取时区
	 * 
	 * @return
	 */
	private String getTimeZoneText() {
		TimeZone tz = java.util.Calendar.getInstance().getTimeZone();
		boolean daylight = tz.inDaylightTime(new Date());
		StringBuilder sb = new StringBuilder();

		sb.append(formatOffset(tz.getRawOffset() + (daylight ? tz.getDSTSavings() : 0))).append(", ")
				.append(tz.getDisplayName(daylight, TimeZone.LONG));

		
		return sb.toString();
	}
	
	private String getTimeZoneID() {
		TimeZone tz = java.util.Calendar.getInstance().getTimeZone();
		Log.d(TAG, "---leewokan----TimeZone = " + tz);
		Log.d(TAG, "---leewokan----TimeZoneText = " + getTimeZoneText());
		boolean daylight = tz.inDaylightTime(new Date());
		StringBuilder sb = new StringBuilder();
		try {
			XmlResourceParser xrp = this.getResources().getXml(R.xml.timezones);
			while (xrp.next() != XmlResourceParser.START_TAG)
				continue;
			xrp.next();
			while (xrp.getEventType() != XmlResourceParser.END_TAG) {
				while (xrp.getEventType() != XmlResourceParser.START_TAG) {
					if (xrp.getEventType() == XmlResourceParser.END_DOCUMENT) {
						return "";
					}
					xrp.next();
				}
				if (xrp.getName().equals(XMLTAG_TIMEZONE)) {
					String id = xrp.getAttributeValue(0);
					String displayName = xrp.nextText();
					if(id.equals(tz.getID())){
						sb.append(formatOffset(tz.getRawOffset() + (daylight ? tz.getDSTSavings() : 0))).append(", ")
				.append(displayName);
						return sb.toString();
					}
				}
				while (xrp.getEventType() != XmlResourceParser.END_TAG) {
					xrp.next();
				}
				xrp.next();
			}
			xrp.close();
		} catch (Exception e) {
			Log.v("TAG", "Ill-formatted timezones.xml file");
		}				
		return "";
	}

	private char[] formatOffset(int off) {
		off = off / 1000 / 60;

		char[] buf = new char[9];
		buf[0] = 'G';
		buf[1] = 'M';
		buf[2] = 'T';

		if (off < 0) {
			buf[3] = '-';
			off = -off;
		} else {
			buf[3] = '+';
		}

		int hours = off / 60;
		int minutes = off % 60;

		buf[4] = (char) ('0' + hours / 10);
		buf[5] = (char) ('0' + hours % 10);

		buf[6] = ':';

		buf[7] = (char) ('0' + minutes / 10);
		buf[8] = (char) ('0' + minutes % 10);

		return buf;
	}

	private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.v("Date", "DateTimeSettings.mIntentReceiver......");
			Log.v("Date", "DateTimeSettings.mIntentReceiver.intent.getAction()" + intent.getAction());
			updateTimeAndDateDisplay();
		}
	};

	/**
	 * 鼠标点击时，切换焦点
	 * 
	 * @param seleted
	 * @param flag
	 */
	public void changeViewSelected(int seleted, boolean flag) {
		if (!flag) {
			switch (seleted) {
			case 0:
				mDateTimeViewHolder.mAutoTimeDate_RL.setBackgroundResource(R.drawable.one_px);
				mDateTimeViewHolder.mAutoBox.clearFocus();
				break;
			case 1:
				mDateTimeViewHolder.mDate_RL.setBackgroundResource(R.drawable.one_px);
				mDateTimeViewHolder.current_date.clearFocus();
				break;
			case 2:
				mDateTimeViewHolder.mTime_RL.setBackgroundResource(R.drawable.one_px);
				mDateTimeViewHolder.current_time.clearFocus();
				break;
			case 3:
				mDateTimeViewHolder.mTimeZone_RL.setBackgroundResource(R.drawable.one_px);
				mDateTimeViewHolder.current_time_zone.clearFocus();
				break;
			case 4:
				mDateTimeViewHolder.mTimeFormat_RL.setBackgroundResource(R.drawable.one_px);
				mDateTimeViewHolder.mTimeBox.clearFocus();
				break;
			case 5:
				mDateTimeViewHolder.mDateFormat_RL.setBackgroundResource(R.drawable.one_px);
				mDateTimeViewHolder.date_format.clearFocus();
				break;
			default:
				mDateTimeViewHolder.mAutoTimeDate_RL.setBackgroundResource(R.drawable.one_px);
				mDateTimeViewHolder.mAutoBox.clearFocus();
			}
		} else {
			switch (seleted) {
			case 0:
				mDateTimeViewHolder.mAutoTimeDate_RL.setBackgroundResource(R.drawable.set_button);
				mDateTimeViewHolder.mAutoBox.setFocusable(true);
				mDateTimeViewHolder.mAutoBox.setFocusableInTouchMode(true);
				mDateTimeViewHolder.mAutoBox.requestFocus();
				break;
			case 1:
				mDateTimeViewHolder.mDate_RL.setBackgroundResource(R.drawable.set_button);
				mDateTimeViewHolder.current_date.setFocusable(true);
				mDateTimeViewHolder.current_date.setFocusableInTouchMode(true);
				mDateTimeViewHolder.current_date.requestFocus();
				break;
			case 2:
				mDateTimeViewHolder.mTime_RL.setBackgroundResource(R.drawable.set_button);
				mDateTimeViewHolder.current_time.setFocusable(true);
				mDateTimeViewHolder.current_time.setFocusableInTouchMode(true);
				mDateTimeViewHolder.current_time.requestFocus();
				break;
			case 3:
				mDateTimeViewHolder.mTimeZone_RL.setBackgroundResource(R.drawable.set_button);
				mDateTimeViewHolder.current_time_zone.setFocusable(true);
				mDateTimeViewHolder.current_time_zone.setFocusableInTouchMode(true);
				mDateTimeViewHolder.current_time_zone.requestFocus();
				break;
			case 4:
				mDateTimeViewHolder.mTimeFormat_RL.setBackgroundResource(R.drawable.set_button);
				mDateTimeViewHolder.mTimeBox.setFocusable(true);
				mDateTimeViewHolder.mTimeBox.setFocusableInTouchMode(true);
				mDateTimeViewHolder.mTimeBox.requestFocus();
				break;
			case 5:
				mDateTimeViewHolder.mDateFormat_RL.setBackgroundResource(R.drawable.set_button);
				mDateTimeViewHolder.date_format.setFocusable(true);
				mDateTimeViewHolder.date_format.setFocusableInTouchMode(true);
				mDateTimeViewHolder.date_format.requestFocus();
				break;
			default:
				mDateTimeViewHolder.mAutoTimeDate_RL.setBackgroundResource(R.drawable.set_button);
				mDateTimeViewHolder.mAutoBox.setFocusable(true);
				mDateTimeViewHolder.mAutoBox.setFocusableInTouchMode(true);
				mDateTimeViewHolder.mAutoBox.requestFocus();
			}
		}
	}

	public void changeViewSelected(int selected) {
		changeViewSelected(isSelected, false);
		isSelected = selected;
		changeViewSelected(isSelected, true);
	}

	public void changeViewSelectedWhenAutoDate(int selected) {
		if (isAutoDateTime) {
			if (isSelected != selected) {
				changeViewSelected(selected, false);
				changeViewSelected(isSelected, true);
			}
		}
	}
}
