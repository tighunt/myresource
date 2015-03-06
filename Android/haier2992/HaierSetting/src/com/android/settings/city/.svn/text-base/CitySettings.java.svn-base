package com.android.settings.city;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.android.settings.R;

/**
 * 城市选择设置的主界面
 * 
 * @author 曹美娟
 * @date 2012-1-7 上午10:18:09
 * @since 1.0
 */
public class CitySettings extends Activity {

	private CityViewHolder mCityViewHolder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.city_setting);
		findViews();
		init();
		registerListeners();
		
	}

	/**
	 * 组件和数据的初始化
	 */
	private void findViews() {
		mCityViewHolder = new CityViewHolder(this);
	}

	private void init() {
		String curCityName = CityHelp.getCityName(getApplicationContext());
		mCityViewHolder.current_city.setText(curCityName);
	}

	/**
	 * 响应各个控件的监听事件
	 */
	private void registerListeners() {

		// 城市列表
		mCityViewHolder.select_city.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CitySettings.this, ProvinceSelectSettingActivity.class);
				String city = intent.getStringExtra("city");
				intent.putExtra("city", city);
				startActivityForResult(intent, 0);
			}
		});
		
		mCityViewHolder.mCity_list.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CitySettings.this, ProvinceSelectSettingActivity.class);
				String city = intent.getStringExtra("city");
				intent.putExtra("city", city);
				startActivityForResult(intent, 0);
			}
		});

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case RESULT_OK:
			Bundle b = data.getExtras();
			String str = b.getString("city");
			mCityViewHolder.current_city.setText(str);
			break;
		default:
			break;
		}
	}

	public boolean isChinese() {
		Configuration conf = getResources().getConfiguration();
		String locale = conf.locale.getDisplayName(conf.locale);
		if (locale != null && locale.length() > 1) {
			locale = Character.toUpperCase(locale.charAt(0)) + locale.substring(1);
		}
		return locale.equals("English (United States)") ? false : true;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			finish();
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
		case KeyEvent.KEYCODE_DPAD_UP:
			mCityViewHolder.select_city.requestFocus();
			break;
		default:
			
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
