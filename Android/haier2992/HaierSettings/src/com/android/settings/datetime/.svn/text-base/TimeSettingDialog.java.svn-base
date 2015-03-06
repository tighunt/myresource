package com.android.settings.datetime;

import java.util.Calendar;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
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
import android.widget.ImageView;

import com.android.settings.DateTimeSettings;
import com.android.settings.R;

/**
 * 自定义Dialog用于用户对日期格式的选择
 * 
 * @author 杜聪甲(ducj@biaoqi.com.cn)
 * @date 2011-11-5 上午09:51:36
 * @since 1.0
 */
public class TimeSettingDialog extends Dialog {

	private DateTimeSettings mDateTimeSettingActivity;

	private Handler updateDateHandler;

	// 设置
	private Button setting_btn;

	// 取消
	private Button cancle_btn;

	private EditText hour_et;

	private EditText minute_et;

	private boolean is24Hour = false;
	private View morning,afternoon;
	private ImageView iv1,iv2;
	private static boolean isPM=false;

	public TimeSettingDialog(DateTimeSettings dateTimeSettingActivity,
			Handler handler, boolean is24Hour) {
		super(dateTimeSettingActivity);
		this.mDateTimeSettingActivity = dateTimeSettingActivity;
		this.updateDateHandler = handler;
		this.is24Hour = is24Hour;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.time_setting);

		// 实例化新的窗口
		Window w = getWindow();

		// 获取默认显示数据
		Display display = w.getWindowManager().getDefaultDisplay();

		// 获取窗口的背景图片
		Resources resources = mDateTimeSettingActivity.getResources();
		Drawable drawable = resources.getDrawable(R.drawable.dialog_bg);
		// // 设置窗口的背景图片
		w.setBackgroundDrawable(drawable);

		// 窗口的标题为空
		w.setTitle(null);

		// 定义窗口的宽和高
		int width = (int) (display.getWidth() * 0.5);
		int height = (int) (display.getHeight() * 0.6);///[2011-1-13yanhd] change dialog height

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
		hour_et = (EditText) findViewById(R.id.hour_et);
	
		minute_et = (EditText) findViewById(R.id.minute_et);
		minute_et.setText(c.get(Calendar.MINUTE) + "");	
		setting_btn = (Button) findViewById(R.id.setting_time_ok);
		cancle_btn = (Button) findViewById(R.id.cannel_time_setting);
		
		morning = (View) findViewById(R.id.morning);
		afternoon = (View) findViewById(R.id.afternoon);
		iv1 = (ImageView) findViewById(R.id.iv1);
		iv2 = (ImageView) findViewById(R.id.iv2);
		
		if (is24Hour) {
			morning.setVisibility(View.GONE);
			afternoon.setVisibility(View.GONE);
			hour_et.setText(c.get(Calendar.HOUR_OF_DAY) + "");
		} else {
			if ((0 < c.get(Calendar.HOUR_OF_DAY)) && (12 > c.get(Calendar.HOUR_OF_DAY))) {
				morning.setBackgroundResource(R.drawable.set_button);
				morning.requestFocus();
				iv1.setBackgroundResource(R.drawable.selected);
				iv2.setBackgroundResource(R.drawable.unselected);
				hour_et.setText(c.get(Calendar.HOUR_OF_DAY) + "");
			}

			else if (0 == c.get(Calendar.HOUR_OF_DAY)) {
				morning.setBackgroundResource(R.drawable.set_button);
				morning.requestFocus();
				iv1.setBackgroundResource(R.drawable.selected);
				iv2.setBackgroundResource(R.drawable.unselected);
				hour_et.setText(c.get(Calendar.HOUR_OF_DAY) + 12 + "");

			} else {
				afternoon.setBackgroundResource(R.drawable.set_button);
				afternoon.requestFocus();
				iv2.setBackgroundResource(R.drawable.selected);
				iv1.setBackgroundResource(R.drawable.unselected);

				if (12 == c.get(Calendar.HOUR_OF_DAY))
					hour_et.setText(c.get(Calendar.HOUR_OF_DAY) + "");
				else {
					hour_et.setText(c.get(Calendar.HOUR_OF_DAY) - 12 + "");
				}
			}
		}
		Log.i("==================",""+c.get(Calendar.HOUR_OF_DAY));
		
	}

	private void registerListeners() {
		
		morning.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					morning.setBackgroundResource(R.drawable.set_button);
				} else {
					morning.setBackgroundResource(R.drawable.one_px);
				}
			}
			
		});	
		
		morning.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				morning.setBackgroundResource(R.drawable.set_button);
				iv1.setBackgroundResource(R.drawable.selected);
				afternoon.setBackgroundResource(R.drawable.one_px);
				iv2.setBackgroundResource(R.drawable.unselected);
				isPM = false;
			}
		});
		
		afternoon.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					afternoon.setBackgroundResource(R.drawable.set_button);
				} else {
					afternoon.setBackgroundResource(R.drawable.one_px);
				}
			}
		});
	     
		afternoon.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				afternoon.setBackgroundResource(R.drawable.set_button);
				iv2.setBackgroundResource(R.drawable.selected);

				morning.setBackgroundResource(R.drawable.one_px);
				iv1.setBackgroundResource(R.drawable.unselected);
				isPM = true;
			}
		});
		
		setting_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String hour = hour_et.getText().toString();
				String minute = minute_et.getText().toString();
				// yangf 12-01-18 增加校验
				if ("".equals(hour)) {
					hour_et.setHint(mDateTimeSettingActivity.getString(R.string.correct_hour));
					hour_et.requestFocus();
					return;
				} else {
					if (is24Hour) {
						if (23 < Integer.parseInt(hour)) {
							hourHint();
							hour_et.requestFocus();
							return;
						}
					} else {
						if (12 < Integer.parseInt(hour) || 0 == Integer.parseInt(hour)) {
							hourHint();
							hour_et.requestFocus();
							return;
						}
					}
				}
				
				if ("".equals(minute)) {
					minute_et.setHint(mDateTimeSettingActivity.getString(R.string.correct_minute));
					minute_et.requestFocus();
					return;
				} else {
					if (59 < Integer.parseInt(minute)) {
						minuteHint();
						minute_et.requestFocus();
						return;
					} else {
						if (!is24Hour) {
							if (12 == Integer.parseInt(hour)) {
								hour = String.valueOf(0);
							}
							if (isPM) {
								hour = String.valueOf(Integer.parseInt(hour) + 12);
							}
						}
					}
				}
				Message msg = new Message();
				Bundle mBundle = new Bundle();
				mBundle.putString("hour", hour);
				mBundle.putString("minute", minute);
				msg.setData(mBundle);
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
		
		minute_et.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					minute_et.selectAll();
				}
			}
		});
		

		minute_et.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence text, int start, int before, int count) {
				String minuteString = text.toString().trim();
				if (minuteString.length() != 0) {
					int day = Integer.parseInt(minuteString);
					if (59 < day) {
						minuteHint();
						return;
					}

					if (minuteString.length() == 2) {
						setting_btn.requestFocus();
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
		
		hour_et.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					hour_et.selectAll();
				}
			}
			
		});
		
		hour_et.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence text, int start, int before, int count) {

				String hourString = text.toString().trim();
				if (hourString.length() != 0) {
					int hour = Integer.parseInt(hourString);
					if (is24Hour) {
						if (23 < hour) {
							hourHint();
							return;
						}
					} else if (12 < hour || 1 > hour) {
						hourHint();
						return;
					}
					
					if (hourString.length() == 2) {
						minute_et.requestFocus();
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}
	
	private void hourHint() {
		hour_et.setText("");
		hour_et.setHint(mDateTimeSettingActivity
				.getString(R.string.correct_hour));
	}

	private void minuteHint() {
		minute_et.setText("");
		minute_et.setHint(mDateTimeSettingActivity
				.getString(R.string.correct_minute));
	}
}
