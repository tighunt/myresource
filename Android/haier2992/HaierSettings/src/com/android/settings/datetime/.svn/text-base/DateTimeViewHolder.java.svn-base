package com.android.settings.datetime;

import android.content.res.Configuration;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.settings.DateTimeSettings;
import com.android.settings.R;

/**
 * 日期和时间设置界面的初始化
 * 
 * @author 杜聪甲(ducj@biaoqi.com.cn)
 * @date 2011-11-4 下午05:22:42
 * @since 1.0
 */
public class DateTimeViewHolder {

	private DateTimeSettings mDateTimeSettingActivity;

	// 自动获取日期和时间
	public RelativeLayout mAutoTimeDate_RL;

	// 日期
	public RelativeLayout mDate_RL;
	public TextView mDate_Text;

	// 时间
	public RelativeLayout mTime_RL;
	public TextView mTime_Text;

	// 时区
	public RelativeLayout mTimeZone_RL;
	public TextView mTimeZone_Text;

	// 时间格式24
	public RelativeLayout mTimeFormat_RL;

	// 日期格式
	public RelativeLayout mDateFormat_RL;

	// 是否自动获取时间和日期
	public CheckBox mAutoBox;

	// 是否是24小时格式
	public CheckBox mTimeBox;

	// 当前日期
	public Button current_date ;

	// 当前时间
	public Button current_time;

	// 当前时区
	public Button current_time_zone;

	// 日期格式
	public Button date_format;

	public DateTimeViewHolder(DateTimeSettings dateTimeSettingActivity) {
		this.mDateTimeSettingActivity = dateTimeSettingActivity;
		findViews();
	}

	private void findViews() {
		mAutoTimeDate_RL = (RelativeLayout) mDateTimeSettingActivity.findViewById(R.id.auto_date_time_rl);
		mDate_RL = (RelativeLayout) mDateTimeSettingActivity.findViewById(R.id.date_rl);
		mDate_Text = (TextView) mDateTimeSettingActivity.findViewById(R.id.date_text);
		mTime_RL = (RelativeLayout) mDateTimeSettingActivity.findViewById(R.id.time_rl);
		mTime_Text = (TextView) mDateTimeSettingActivity.findViewById(R.id.time_text);
		mTimeZone_RL = (RelativeLayout) mDateTimeSettingActivity.findViewById(R.id.timezone_rl);
		mTimeZone_Text = (TextView) mDateTimeSettingActivity.findViewById(R.id.timezone_text);
		mTimeFormat_RL = (RelativeLayout) mDateTimeSettingActivity.findViewById(R.id.date_time_24hour_rl);
		mDateFormat_RL = (RelativeLayout) mDateTimeSettingActivity.findViewById(R.id.date_format_rl);
		
		Configuration config = mDateTimeSettingActivity.getResources().getConfiguration();
		if (config.locale.toString().equals("en_US")) // zhf
		{
			mAutoBox = (CheckBox) mDateTimeSettingActivity.findViewById(R.id.checkbox_auto_date_time_en);
			mAutoBox.setVisibility(View.VISIBLE);
			mTimeBox = (CheckBox) mDateTimeSettingActivity.findViewById(R.id.hour_format_cb_en);
			mTimeBox.setVisibility(View.VISIBLE);
		} else {
			mAutoBox = (CheckBox) mDateTimeSettingActivity.findViewById(R.id.checkbox_auto_date_time);
			mAutoBox.setVisibility(View.VISIBLE);
			mTimeBox = (CheckBox) mDateTimeSettingActivity.findViewById(R.id.hour_format_cb);
			mTimeBox.setVisibility(View.VISIBLE);
		}

		// mAutoBox = (CheckBox) mDateTimeSettingActivity.findViewById(R.id.checkbox_auto_date_time);
		// mTimeBox = (CheckBox) mDateTimeSettingActivity.findViewById(R.id.hour_format_cb);
		current_date = (Button) mDateTimeSettingActivity.findViewById(R.id.date_set);
		current_time = (Button) mDateTimeSettingActivity.findViewById(R.id.time_set);
		current_time_zone = (Button) mDateTimeSettingActivity.findViewById(R.id.timezone_set);
		date_format = (Button) mDateTimeSettingActivity.findViewById(R.id.date_format_set);
	}

}
