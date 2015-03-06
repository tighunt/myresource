package com.android.settings.datetime;

import java.util.Calendar;

import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.settings.DateTimeSettings;
import com.android.settings.R;
import com.android.settings.util.DateUtil;
import com.android.settings.util.InvalidDateFormatException;

/**
 * 自定义Dialog用于用户对日期格式的选择
 * 
 * @author 杜聪甲(ducj@biaoqi.com.cn)
 * @date 2011-11-5 上午09:51:36
 * @since 1.0
 */
public class DateSettingDialog extends Dialog {

	private DateTimeSettings mDateTimeSettingActivity;

	private Handler updateDateHandler;

	// 设置
	private Button setting_btn;

	// 取消
	private Button cancle_btn;

	private EditText year_et;

	private EditText month_et;

	private EditText day_et;

	private final int YEAR_OF_MONTH_MAX = 12;

	private final int YEAR_OF_MONTH_MIN = 1;

	private final int DAY_MAX = 31;

	private final int DAY_MIN = 0;

	public DateSettingDialog(DateTimeSettings dateTimeSettingActivity, Handler handler) {
		super(dateTimeSettingActivity);
		this.mDateTimeSettingActivity = dateTimeSettingActivity;
		this.updateDateHandler = handler;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.date_setting);

		// 实例化新的窗口
		Window w = getWindow();

		// 获取默认显示数据
		Display display = w.getWindowManager().getDefaultDisplay();

		// 获取窗口的背景图片
		Resources resources = mDateTimeSettingActivity.getResources();
		Drawable drawable = resources.getDrawable(R.drawable.dialog_bg);
		// 设置窗口的背景图片
		w.setBackgroundDrawable(drawable);

		// 窗口的标题为空
		w.setTitle(null);

		// 定义窗口的宽和高
		int width = (int) (display.getWidth() * 0.5);
		int height = (int) (display.getHeight() * 0.6);// /[2011-1-13yanhd] change dialog height

		// 设置窗口的大小
		w.setLayout(width, height);

		// 设置窗口的显示位置
		w.setGravity(Gravity.CENTER);

		// 设置窗口的属性
		WindowManager.LayoutParams wl = w.getAttributes();
		w.setAttributes(wl);

		findViews();

		registerListeners();
	}

	private void findViews() {

		final Calendar c = Calendar.getInstance();

		Log.d("DateSettingDialog", "  c = " + c);
		year_et = (EditText) findViewById(R.id.year_et);
		month_et = (EditText) findViewById(R.id.month_et);
		day_et = (EditText) findViewById(R.id.day_et);

		year_et.setText(c.get(Calendar.YEAR) + "");
		month_et.setText(DateUtil.getMonth(null, "m"));
		day_et.setText(c.get(Calendar.DAY_OF_MONTH) + "");

		setting_btn = (Button) findViewById(R.id.setting_date_ok);
		cancle_btn = (Button) findViewById(R.id.cannel_date_setting);
	}

	private void registerListeners() {

		setting_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				String yearString = year_et.getText().toString();
				String monthString = month_et.getText().toString();
				String dayString = day_et.getText().toString();

				if ("".equals(yearString) || yearString.length() != 4) {
					if (!"".equals(yearString)) {
						year_et.setText("");
					}
					year_et.setHint(mDateTimeSettingActivity.getString(R.string.correct_year));
					year_et.requestFocus();
					return;
				}
				if (yearString != null && !"".equals(yearString)) {
					if (Integer.parseInt(yearString) > 2038 || Integer.parseInt(yearString) < 1970) {
						if (!"".equals(yearString)) {
							year_et.setText("");
						}
						year_et.setHint(mDateTimeSettingActivity.getString(R.string.scope_year));
						year_et.requestFocus();
						return;
					}
				}

				/**
				 * if ("".equals(monthString)) { month_et.setHint(mDateTimeSettingActivity
				 * .getString(R.string.correct_month)); month_et.requestFocus(); return; } if
				 * (monthString != null && !"".equals(monthString)) { int month =
				 * Integer.parseInt(monthString); if (month < YEAR_OF_MONTH_MIN || month >
				 * YEAR_OF_MONTH_MAX) { month_et.setText("");
				 * month_et.setHint(mDateTimeSettingActivity .getString(R.string.correct_month));
				 * month_et.requestFocus(); return; } }
				 */

				if ("".equals(dayString)) {
					day_et.setHint(mDateTimeSettingActivity.getString(R.string.correct_day));
					day_et.requestFocus();
					return;
				}
				if (dayString.length() != 0) {
					int day = Integer.parseInt(dayString);
					if (day < DAY_MIN || day > DAY_MAX) {
						day_et.setText("");
						day_et.setHint(mDateTimeSettingActivity.getString(R.string.correct_day));
						day_et.requestFocus();
						return;
					}
				}

				String dateString = yearString + monthString + dayString;
				try {
					DateUtil.validate(dateString);
				} catch (InvalidDateFormatException e) {
					if (!"".equals(dayString)) {
						day_et.setText("");
					}
					day_et.setHint(mDateTimeSettingActivity.getString(R.string.correct_day));
					day_et.requestFocus();
					return;
				}

				Message msg = new Message();
				Bundle mBundle = new Bundle();
				mBundle.putString("year", yearString);
				mBundle.putString("month", Integer.toString(Integer.parseInt(monthString) - 1));
				mBundle.putString("day", dayString);
				msg.setData(mBundle);
				Log.v("Date",
						"DateSettingDialog.registerListeners().setting_btn.setOnClickListener.onClick......");
				updateDateHandler.sendMessage(msg);
				dismiss();
			}
		});

		cancle_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		year_et.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					year_et.selectAll();
				}
			}

		});

		month_et.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					month_et.selectAll();
				}
			}

		});

		day_et.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					day_et.selectAll();
				}
			}

		});

		year_et.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String yearString = year_et.getText().toString();
				if ("".equals(yearString)) {
					// if (!"".equals(yearString)) {|| yearString.length() != 4
					// year_et.setText("");
					// }
					year_et.setHint(mDateTimeSettingActivity.getString(R.string.correct_year));
					year_et.requestFocus();
					return;
				}
				if (yearString != null && !"".equals(yearString) && yearString.length() == 4) {

					if (Integer.parseInt(yearString) > 2038 || Integer.parseInt(yearString) < 1970) {
						if (!"".equals(yearString)) {
							year_et.setText("");
						}
						year_et.setHint(mDateTimeSettingActivity.getString(R.string.scope_year));
						year_et.requestFocus();
					} else {
						month_et.requestFocus();
					}
					return;
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		month_et.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence text, int start, int before, int count) {

				String monthString = text.toString().trim();
				String yearStrng = year_et.getText().toString().trim();
				if ("".equals(yearStrng)) {
					if (!"".equals(monthString)) {
						month_et.setText("");
					}
					year_et.setHint(mDateTimeSettingActivity.getString(R.string.correct_year));
					year_et.requestFocus();
					return;
				} else {
					if ("".equals(monthString)){
						month_et.setHint(mDateTimeSettingActivity
								.getString(R.string.correct_month));
						return;
					}
					// add by ducj
					if (Integer.parseInt(yearStrng) == 2038) {
						if (!monthString.equals("") && Integer.parseInt(monthString) > 1) {
							month_et.setText("");
							month_et.setHint(mDateTimeSettingActivity
									.getString(R.string.correct_month));
							Toast.makeText(mDateTimeSettingActivity,
									R.string.system_support_max_date, Toast.LENGTH_LONG).show();
							return;
						}
					}
				}

				// if ("".equals(monthString)) {
				// month_et.setHint(mDateTimeSettingActivity.getString(R.string.correct_month));
				// month_et.requestFocus();
				// return;
				// }
				if (monthString.length() != 0) {
					int month = Integer.parseInt(monthString);
					if (month < YEAR_OF_MONTH_MIN || month > YEAR_OF_MONTH_MAX) {
						month_et.setText("");
						month_et.setHint(mDateTimeSettingActivity.getString(R.string.correct_month));
						month_et.requestFocus();
						return;
					}
				}
				if (Integer.parseInt(monthString) > 1 || monthString.length() == 2) {
					day_et.requestFocus();
					return;
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		day_et.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence text, int start, int before, int count) {

				String dayString = text.toString().trim();
				// if ("".equals(dayString)) {
				// day_et.setHint(mDateTimeSettingActivity.getString(R.string.correct_day));
				// day_et.requestFocus();
				// return;
				// }
				// if (dayString.length() != 0) {
				// int day = Integer.parseInt(dayString);
				// if (day < DAY_MIN || day > DAY_MAX) {
				// day_et.setText("");
				// day_et.setHint(mDateTimeSettingActivity.getString(R.string.correct_day));
				// day_et.requestFocus();
				// return;
				// }
				// }
				String monthString = month_et.getText().toString().trim();
				String yearString = year_et.getText().toString().trim();
				if (yearString.length() != 4) {

					if (!"".equals(dayString)) {
						day_et.setText("");
						day_et.setHint(mDateTimeSettingActivity.getString(R.string.correct_day));
					}
					if (!"".equals(yearString)) {
						year_et.setText("");
					}
					year_et.setHint(mDateTimeSettingActivity.getString(R.string.correct_year));
					year_et.requestFocus();
					return;
				}
				if ("".equals(monthString)) {
					if (!"".equals(dayString)) {
						day_et.setText("");
					}
					month_et.setHint(mDateTimeSettingActivity.getString(R.string.correct_month));
					month_et.requestFocus();
					return;
				}
				if (dayString.length() != 0) {
					/*
					 * int month = Integer.parseInt(dayString);
					 * 
					 * if (31 < month) { day_et.setText(""); day_et.setHint(mDateTimeSettingActivity
					 * .getString(R.string.correct_day)); }
					 */
					// add by ducj
					if (Integer.parseInt(dayString) == 0) {
						day_et.setText("");
						day_et.setHint(mDateTimeSettingActivity.getString(R.string.correct_day));
						return;
					}
					// add by ducj
					if (Integer.parseInt(yearString) == 2038 && Integer.parseInt(monthString) == 1) {
						if (!dayString.equals("") && Integer.parseInt(dayString) > 18) {
							day_et.setText("");
							day_et.setHint(mDateTimeSettingActivity.getString(R.string.correct_day));
							Toast.makeText(mDateTimeSettingActivity,
									R.string.system_support_max_date, Toast.LENGTH_LONG).show();
							return;
						}
					}
					// yangf 2012-01-18
					String dateString = yearString + monthString + dayString;
					try {
						DateUtil.validate(dateString);
					} catch (InvalidDateFormatException e) {
						if (!"".equals(dateString)) {
							day_et.setText("");
						}
						day_et.setHint(mDateTimeSettingActivity.getString(R.string.correct_day));
						return;
					}
					
					if(dayString.length() == 2) {
						setting_btn.requestFocus();
						return;
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

	}

}
