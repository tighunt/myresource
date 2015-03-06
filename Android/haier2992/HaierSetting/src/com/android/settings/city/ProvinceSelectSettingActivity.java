package com.android.settings.city;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.android.settings.R;


/**
 * 省份选择主界面
 * 
 * @author 曹美娟
 * @date 2012-1-18 上午12:32:05
 * @since 1.0
 */
public class ProvinceSelectSettingActivity extends Activity {

	private ProvinceSelectSettingViewHolder mProvinceSelectSettingViewHolder;
	private int isSelected = 0;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.city_provinceselect);
		findViews();
		registerListeners();
	}	

	/**
	 * 组件和数据的初始化
	 */
	private void findViews() {

		mProvinceSelectSettingViewHolder = new ProvinceSelectSettingViewHolder(this);
		
		}
	/**
	 * 响应各个控件的监听事件
	 */	
	private void registerListeners() {

		// 城市列表
		mProvinceSelectSettingViewHolder.mBeijing
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();  
				String cityId = "110000";
				String city = getResources().getString(R.string.beijing);
				CityHelp.setCitySettings(getApplicationContext(), cityId, city);
				intent.putExtra("locid",cityId ); 
				intent.putExtra("city",city ); 
				intent.setClass(ProvinceSelectSettingActivity.this, CitySettings.class);
				setResult(RESULT_OK,intent);
				finish();
			}
		});
		mProvinceSelectSettingViewHolder.mShanghai
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(); 
				String cityId = "310000"; 
				String city = getResources().getString(R.string.shanghai);
				CityHelp.setCitySettings(getApplicationContext(), cityId, city);
				intent.putExtra("locid",cityId ); 
				intent.putExtra("city",city ); 
				intent.setClass(ProvinceSelectSettingActivity.this, CitySettings.class);
				setResult(RESULT_OK,intent);
				finish();
			}
		});
		mProvinceSelectSettingViewHolder.mChongqing
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();  
				String cityId = "500000"; 
				String city = getResources().getString(R.string.chongqing);
				CityHelp.setCitySettings(getApplicationContext(), cityId, city);
				intent.putExtra("locid",cityId ); 
				intent.putExtra("city",city ); 
				intent.setClass(ProvinceSelectSettingActivity.this, CitySettings.class);
				setResult(RESULT_OK,intent);
				finish();
			}
		});
		mProvinceSelectSettingViewHolder.mTianjin
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				String cityId = "120000"; 
				String city = getResources().getString(R.string.tianjin);
				CityHelp.setCitySettings(getApplicationContext(), cityId, city);
				intent.putExtra("locid",cityId ); 
				intent.putExtra("city",city ); 
				intent.setClass(ProvinceSelectSettingActivity.this, CitySettings.class);
				setResult(RESULT_OK,intent);
				finish();
			}
		});
		mProvinceSelectSettingViewHolder.mXianggang
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(); 
				String cityId = "810000"; 
				String city = getResources().getString(R.string.xianggang);
				CityHelp.setCitySettings(getApplicationContext(), cityId, city);
				intent.putExtra("locid",cityId ); 
				intent.putExtra("city",city ); 
				intent.setClass(ProvinceSelectSettingActivity.this, CitySettings.class);
				setResult(RESULT_OK,intent);
				finish();
			}
		});
		mProvinceSelectSettingViewHolder.mAomen
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(); 
				String cityId = "820000"; 
				String city = getResources().getString(R.string.aomen);
				CityHelp.setCitySettings(getApplicationContext(), cityId, city);
				intent.putExtra("locid",cityId ); 
				intent.putExtra("city",city ); 
				intent.setClass(ProvinceSelectSettingActivity.this, CitySettings.class);
				setResult(RESULT_OK,intent);
				finish();
			}
		});
		mProvinceSelectSettingViewHolder.mAnhui
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TextView textView = (TextView) v;
				SelectCityByProvinceDialog citySelectDialog = new SelectCityByProvinceDialog(
						ProvinceSelectSettingActivity.this,textView.getText().toString());
				citySelectDialog.show();
			}
		});
		mProvinceSelectSettingViewHolder.mNeimenggu
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TextView textView = (TextView) v;
				SelectCityByProvinceDialog citySelectDialog = new SelectCityByProvinceDialog(
						ProvinceSelectSettingActivity.this,textView.getText().toString());
				citySelectDialog.show();
				
			}
		});
		mProvinceSelectSettingViewHolder.mNingxia
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TextView textView = (TextView) v;
				SelectCityByProvinceDialog citySelectDialog = new SelectCityByProvinceDialog(
						ProvinceSelectSettingActivity.this,textView.getText().toString());
				citySelectDialog.show();

			}
		});
		mProvinceSelectSettingViewHolder.mFujian
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TextView textView = (TextView) v;
				SelectCityByProvinceDialog citySelectDialog = new SelectCityByProvinceDialog(
						ProvinceSelectSettingActivity.this,textView.getText().toString());
				citySelectDialog.show();
				
			}
		});
		mProvinceSelectSettingViewHolder.mQinghai
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TextView textView = (TextView) v;
				SelectCityByProvinceDialog citySelectDialog = new SelectCityByProvinceDialog(
						ProvinceSelectSettingActivity.this,textView.getText().toString());
				citySelectDialog.show();			
			}
		});
		mProvinceSelectSettingViewHolder.mGuangxi
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TextView textView = (TextView) v;
				SelectCityByProvinceDialog citySelectDialog = new SelectCityByProvinceDialog(
						ProvinceSelectSettingActivity.this,textView.getText().toString());
				citySelectDialog.show();				
			}
		});
		mProvinceSelectSettingViewHolder.mGuangdong
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TextView textView = (TextView) v;
				SelectCityByProvinceDialog citySelectDialog = new SelectCityByProvinceDialog(
						ProvinceSelectSettingActivity.this,textView.getText().toString());
				citySelectDialog.show();				
			}
		});
		mProvinceSelectSettingViewHolder.mGuizhou
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TextView textView = (TextView) v;
				SelectCityByProvinceDialog citySelectDialog = new SelectCityByProvinceDialog(
						ProvinceSelectSettingActivity.this,textView.getText().toString());
				citySelectDialog.show();
			}
		});
		mProvinceSelectSettingViewHolder.mGansu
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TextView textView = (TextView) v;
				SelectCityByProvinceDialog citySelectDialog = new SelectCityByProvinceDialog(
						ProvinceSelectSettingActivity.this,textView.getText().toString());
				citySelectDialog.show();				
			}
		});
		mProvinceSelectSettingViewHolder.mShandong
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TextView textView = (TextView) v;
				SelectCityByProvinceDialog citySelectDialog = new SelectCityByProvinceDialog(
						ProvinceSelectSettingActivity.this,textView.getText().toString());
				citySelectDialog.show();				
			}
		});
		mProvinceSelectSettingViewHolder.mShanxi
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TextView textView = (TextView) v;
				SelectCityByProvinceDialog citySelectDialog = new SelectCityByProvinceDialog(
						ProvinceSelectSettingActivity.this,textView.getText().toString());
				citySelectDialog.show();
			}
		});
		mProvinceSelectSettingViewHolder.mShaanxi
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TextView textView = (TextView) v;
				SelectCityByProvinceDialog citySelectDialog = new SelectCityByProvinceDialog(
						ProvinceSelectSettingActivity.this,textView.getText().toString());
				citySelectDialog.show();			
			}
		});
		mProvinceSelectSettingViewHolder.mSichuan
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TextView textView = (TextView) v;
				SelectCityByProvinceDialog citySelectDialog = new SelectCityByProvinceDialog(
						ProvinceSelectSettingActivity.this,textView.getText().toString());
				citySelectDialog.show();			
			}
		});
		mProvinceSelectSettingViewHolder.mHainan
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TextView textView = (TextView) v;
				SelectCityByProvinceDialog citySelectDialog = new SelectCityByProvinceDialog(
						ProvinceSelectSettingActivity.this,textView.getText().toString());
				citySelectDialog.show();				
			}
		});
		mProvinceSelectSettingViewHolder.mHenan
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TextView textView = (TextView) v;
				SelectCityByProvinceDialog citySelectDialog = new SelectCityByProvinceDialog(
						ProvinceSelectSettingActivity.this,textView.getText().toString());
				citySelectDialog.show();				
			}
		});
		mProvinceSelectSettingViewHolder.mHebei
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TextView textView = (TextView) v;
				SelectCityByProvinceDialog citySelectDialog = new SelectCityByProvinceDialog(
						ProvinceSelectSettingActivity.this,textView.getText().toString());
				citySelectDialog.show();			
			}
		});
		mProvinceSelectSettingViewHolder.mHubei
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TextView textView = (TextView) v;
				SelectCityByProvinceDialog citySelectDialog = new SelectCityByProvinceDialog(
						ProvinceSelectSettingActivity.this,textView.getText().toString());
				citySelectDialog.show();				
			}
		});
		mProvinceSelectSettingViewHolder.mHunan
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TextView textView = (TextView) v;
				SelectCityByProvinceDialog citySelectDialog = new SelectCityByProvinceDialog(
						ProvinceSelectSettingActivity.this,textView.getText().toString());
				citySelectDialog.show();				
			}
		});
		mProvinceSelectSettingViewHolder.mHeilongjiang
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TextView textView = (TextView) v;
				SelectCityByProvinceDialog citySelectDialog = new SelectCityByProvinceDialog(
						ProvinceSelectSettingActivity.this,textView.getText().toString());
				citySelectDialog.show();				
			}
		});
		mProvinceSelectSettingViewHolder.mTaiwan
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TextView textView = (TextView) v;
				SelectCityByProvinceDialog citySelectDialog = new SelectCityByProvinceDialog(
						ProvinceSelectSettingActivity.this,textView.getText().toString());
				citySelectDialog.show();
			}
		});
		mProvinceSelectSettingViewHolder.mXizang
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TextView textView = (TextView) v;
				SelectCityByProvinceDialog citySelectDialog = new SelectCityByProvinceDialog(
						ProvinceSelectSettingActivity.this,textView.getText().toString());
				citySelectDialog.show();
				
			}
		});
		mProvinceSelectSettingViewHolder.mXinjiang
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TextView textView = (TextView) v;
				SelectCityByProvinceDialog citySelectDialog = new SelectCityByProvinceDialog(
						ProvinceSelectSettingActivity.this,textView.getText().toString());
				citySelectDialog.show();
				
			}
		});
		mProvinceSelectSettingViewHolder.mJilin
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TextView textView = (TextView) v;
				SelectCityByProvinceDialog citySelectDialog = new SelectCityByProvinceDialog(
						ProvinceSelectSettingActivity.this,textView.getText().toString());
				citySelectDialog.show();
			}
		});
		mProvinceSelectSettingViewHolder.mJiangsu
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TextView textView = (TextView) v;
				SelectCityByProvinceDialog citySelectDialog = new SelectCityByProvinceDialog(
						ProvinceSelectSettingActivity.this,textView.getText().toString());
				citySelectDialog.show();
			}
		});
		mProvinceSelectSettingViewHolder.mJiangxi
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TextView textView = (TextView) v;
				SelectCityByProvinceDialog citySelectDialog = new SelectCityByProvinceDialog(
						ProvinceSelectSettingActivity.this,textView.getText().toString());
				citySelectDialog.show();
			}
		});
		mProvinceSelectSettingViewHolder.mYunnan
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TextView textView = (TextView) v;
				SelectCityByProvinceDialog citySelectDialog = new SelectCityByProvinceDialog(
						ProvinceSelectSettingActivity.this,textView.getText().toString());
				citySelectDialog.show();
			}
		});
		mProvinceSelectSettingViewHolder.mLiaoning
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TextView textView = (TextView) v;
				SelectCityByProvinceDialog citySelectDialog = new SelectCityByProvinceDialog(
						ProvinceSelectSettingActivity.this,textView.getText().toString());
				citySelectDialog.show();
				
			}
		});
		mProvinceSelectSettingViewHolder.mZhejiang
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TextView textView = (TextView) v;
				SelectCityByProvinceDialog citySelectDialog = new SelectCityByProvinceDialog(
						ProvinceSelectSettingActivity.this,textView.getText().toString());
				citySelectDialog.show();
				
			}
		});

	}	
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			//dropDown();
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			//dropUp();
		}else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			//dropRight();
		}else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			//dropLeft();
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		
		return super.onKeyUp(keyCode, event);
	}
	
	/**
	 * 向下滑动
	 */
	private void dropDown() {
		switch (isSelected) {
		case 0:	
		case 1:
		case 2:
			isSelected = 6;
			mProvinceSelectSettingViewHolder.mBeijing
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mShanghai
			        .setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mChongqing
			        .setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mAnhui
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;
		case 6:	
			isSelected =9;
			mProvinceSelectSettingViewHolder.mAnhui
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mFujian
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;
		case 9:	
			isSelected = 11;
			mProvinceSelectSettingViewHolder.mFujian
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mGuangxi
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 11:
			isSelected = 19;
			mProvinceSelectSettingViewHolder.mGuangxi
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mHainan
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 19:
			isSelected = 24;
			mProvinceSelectSettingViewHolder.mHainan
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mHunan
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 24:
			isSelected = 28;
			mProvinceSelectSettingViewHolder.mHunan
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mJilin
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 28:
			isSelected = 32;
			mProvinceSelectSettingViewHolder.mJilin
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mLiaoning
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;
		case 12:
			isSelected = 20;
			mProvinceSelectSettingViewHolder.mGuangdong
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mHenan
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;
		case 20:
		case 21:
		case 22:		
			isSelected = 25;
			mProvinceSelectSettingViewHolder.mHenan
			        .setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mHebei
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mHubei
			        .setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mHeilongjiang
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;
		case 25:
			isSelected = 29;
			mProvinceSelectSettingViewHolder.mHeilongjiang
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mJiangsu
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;
		case 29:
		case 30:	
			isSelected = 32;
			mProvinceSelectSettingViewHolder.mJiangsu
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mJiangxi
			        .setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mLiaoning
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 13:
			isSelected = 21;
			mProvinceSelectSettingViewHolder.mGuizhou
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mHebei
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 14:		
			isSelected = 22;
			mProvinceSelectSettingViewHolder.mGansu
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mHubei
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;
		case 3:
			isSelected = 7;
			mProvinceSelectSettingViewHolder.mTianjin
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mNeimenggu
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;
		case 7:
		case 8:	
			isSelected = 10;
			mProvinceSelectSettingViewHolder.mNeimenggu
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mNingxia
			        .setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mQinghai
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;
		case 10:
			isSelected = 15;
			mProvinceSelectSettingViewHolder.mQinghai
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mShandong
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;
		case 15:
		case 16:
		case 17:
		case 18:	
			isSelected = 23;
			mProvinceSelectSettingViewHolder.mShandong
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mShanxi
			        .setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mShaanxi
			        .setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mSichuan
			        .setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mTaiwan
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;
		case 23:
			isSelected = 26;
			mProvinceSelectSettingViewHolder.mTaiwan
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mXizang
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 26:
		case 27:	
			isSelected = 31;
			mProvinceSelectSettingViewHolder.mXizang
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mXinjiang
			        .setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mYunnan
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 31:
			isSelected = 33;
			mProvinceSelectSettingViewHolder.mYunnan
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mZhejiang
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;
		case 4:
		case 5:
			isSelected = 8;
			mProvinceSelectSettingViewHolder.mXianggang
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mAomen
			        .setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mNingxia
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;

		}
	}

	/**
	 * 向上滑动
	 */
	private void dropUp() {
		switch (isSelected) {
		case 6:
			isSelected = 0;
			mProvinceSelectSettingViewHolder.mAnhui
			        .setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mBeijing
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;
		case 9:
			isSelected = 6;
			mProvinceSelectSettingViewHolder.mFujian
			        .setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mAnhui
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;
		case 11:
		case 12:
		case 13:	
		case 14:
			isSelected = 9;
			mProvinceSelectSettingViewHolder.mGuangxi
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mGuangdong
			        .setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mGuizhou
			        .setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mGansu
			        .setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mFujian
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 19:	
			isSelected = 11;
			mProvinceSelectSettingViewHolder.mHainan
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mGuangxi
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	

		case 24:
			isSelected = 19;
			mProvinceSelectSettingViewHolder.mHunan
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mHainan
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 28:
			isSelected = 24;
			mProvinceSelectSettingViewHolder.mJilin
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mHunan
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 32:
			isSelected = 28;
			mProvinceSelectSettingViewHolder.mLiaoning
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mJilin
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;
		case 21:
			isSelected = 13;
			mProvinceSelectSettingViewHolder.mHebei
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mGuizhou
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;
		case 20:
			isSelected = 12;
			mProvinceSelectSettingViewHolder.mHenan
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mGuangdong
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;
		case 25:
			isSelected = 20;
			mProvinceSelectSettingViewHolder.mHeilongjiang
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mHenan
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;
		case 29:
		case 30:
			isSelected = 25;
			mProvinceSelectSettingViewHolder.mJiangsu
	                 .setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mJiangxi
			        .setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mHeilongjiang
					.setBackgroundResource(R.drawable.focus_bg_small);
	        break;
		case 22:		
			isSelected = 14;
			mProvinceSelectSettingViewHolder.mHubei
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mGansu
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;										
		case 7:
			isSelected = 3;
			mProvinceSelectSettingViewHolder.mNeimenggu
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mTianjin
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;
		case 8:
			isSelected = 4;
			mProvinceSelectSettingViewHolder.mNingxia
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mXianggang
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;
		case 10:
			isSelected = 7;
			mProvinceSelectSettingViewHolder.mQinghai
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mNeimenggu
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;
		case 15:
		case 16:
		case 17:
		case 18:	
			isSelected = 10;
			mProvinceSelectSettingViewHolder.mShandong
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mShanxi
			        .setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mShaanxi
			        .setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mSichuan
			        .setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mQinghai
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;
		case 23:
			isSelected = 15;
			mProvinceSelectSettingViewHolder.mTaiwan
					.setBackgroundResource(R.drawable.one_px);			
			mProvinceSelectSettingViewHolder.mShandong
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;
		case 26:
		case 27:	
			isSelected = 23;
			mProvinceSelectSettingViewHolder.mXizang
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mXinjiang
			        .setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mTaiwan
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 31:	
			isSelected = 26;
			mProvinceSelectSettingViewHolder.mYunnan
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mXizang
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 33:
			isSelected = 31;
			mProvinceSelectSettingViewHolder.mZhejiang
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mYunnan
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;
		}
	}
	/**
	 * 向右滑动
	 */
	private void dropRight() {
		switch (isSelected) {
		case 0:
			isSelected = 1;
			mProvinceSelectSettingViewHolder.mBeijing
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mShanghai
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;
		case 1:
			isSelected = 2;
			mProvinceSelectSettingViewHolder.mShanghai
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mChongqing
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;
		case 2:
			isSelected = 3;
			mProvinceSelectSettingViewHolder.mChongqing
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mTianjin
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;
		case 3:
			isSelected = 4;
			mProvinceSelectSettingViewHolder.mTianjin
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mXianggang
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;
		case 4:
			isSelected = 5;
			mProvinceSelectSettingViewHolder.mXianggang
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mAomen
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;
		case 5:
			isSelected = 6;
			mProvinceSelectSettingViewHolder.mAomen
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mAnhui
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 6:
			isSelected = 7;
			mProvinceSelectSettingViewHolder.mAnhui
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mNeimenggu
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 7:
			isSelected = 8;
			mProvinceSelectSettingViewHolder.mNeimenggu
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mNingxia
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 8:
			isSelected = 9;
			mProvinceSelectSettingViewHolder.mNingxia
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mFujian
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 9:
			isSelected = 10;
			mProvinceSelectSettingViewHolder.mFujian
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mQinghai
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 10:
			isSelected = 11;
			mProvinceSelectSettingViewHolder.mQinghai
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mGuangxi
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 11:
			isSelected = 12;
			mProvinceSelectSettingViewHolder.mGuangxi
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mGuangdong
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 12:
			isSelected = 13;
			mProvinceSelectSettingViewHolder.mGuangdong
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mGuizhou
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 13:
			isSelected = 14;
			mProvinceSelectSettingViewHolder.mGuizhou
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mGansu
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 14:
			isSelected = 15;
			mProvinceSelectSettingViewHolder.mGansu
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mShandong
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 15:
			isSelected = 16;
			mProvinceSelectSettingViewHolder.mShandong
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mShanxi
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 16:
			isSelected = 17;
			mProvinceSelectSettingViewHolder.mShanxi
			        .setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mShaanxi
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;						
		case 17:
			isSelected = 18;
			mProvinceSelectSettingViewHolder.mShaanxi
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mSichuan
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 18:
			isSelected = 19;
			mProvinceSelectSettingViewHolder.mSichuan
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mHainan
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 19:
			isSelected = 20;
			mProvinceSelectSettingViewHolder.mHainan
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mHenan
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 20:
			isSelected = 21;
			mProvinceSelectSettingViewHolder.mHenan
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mHebei
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 21:
			isSelected = 22;
			mProvinceSelectSettingViewHolder.mHebei
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mHubei
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 22:
			isSelected = 23;
			mProvinceSelectSettingViewHolder.mHubei
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mTaiwan
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 23:
			isSelected = 24;
			mProvinceSelectSettingViewHolder.mTaiwan
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mHunan
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 24:
			isSelected = 25;
			mProvinceSelectSettingViewHolder.mHunan
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mHeilongjiang
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 25:
			isSelected = 26;
			mProvinceSelectSettingViewHolder.mHeilongjiang
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mXizang
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 26:
			isSelected = 27;
			mProvinceSelectSettingViewHolder.mXizang
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mXinjiang
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 27:
			isSelected = 28;
			mProvinceSelectSettingViewHolder.mXinjiang
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mJilin
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 28:
			isSelected = 29;
			mProvinceSelectSettingViewHolder.mJilin
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mJiangsu
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 29:
			isSelected = 30;
			mProvinceSelectSettingViewHolder.mJiangsu
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mJiangxi
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 30:
			isSelected = 31;
			mProvinceSelectSettingViewHolder.mJiangxi
			        .setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mYunnan
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;						
		case 31:
			isSelected = 32;
			mProvinceSelectSettingViewHolder.mYunnan
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mLiaoning
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 32:
			isSelected = 33;
			mProvinceSelectSettingViewHolder.mLiaoning
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mZhejiang
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	

		}
	}
	/**
	 * 向左滑动
	 */
	private void dropLeft() {
		switch (isSelected) {
		case 1:
			isSelected = 0;
			mProvinceSelectSettingViewHolder.mShanghai
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mBeijing
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;
		case 2:
			isSelected = 1;
			mProvinceSelectSettingViewHolder.mChongqing
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mShanghai
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;
		case 3:
			isSelected = 2;
			mProvinceSelectSettingViewHolder.mTianjin
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mChongqing
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;
		case 4:
			isSelected = 3;
			mProvinceSelectSettingViewHolder.mXianggang
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mTianjin
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;
		case 5:
			isSelected = 4;
			mProvinceSelectSettingViewHolder.mAomen
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mXianggang
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;
		case 6:
			isSelected = 5;
			mProvinceSelectSettingViewHolder.mAnhui
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mAomen
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 7:
			isSelected = 6;
			mProvinceSelectSettingViewHolder.mNeimenggu
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mAnhui
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 8:
			isSelected = 7;
			mProvinceSelectSettingViewHolder.mNingxia
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mNeimenggu
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 9:
			isSelected = 8;
			mProvinceSelectSettingViewHolder.mFujian
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mNingxia
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 10:
			isSelected = 9;
			mProvinceSelectSettingViewHolder.mQinghai
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mFujian
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 11:
			isSelected = 10;
			mProvinceSelectSettingViewHolder.mGuangxi
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mQinghai
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 12:
			isSelected = 11;
			mProvinceSelectSettingViewHolder.mGuangdong
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mGuangxi
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 13:
			isSelected = 12;
			mProvinceSelectSettingViewHolder.mGuizhou
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mGuangdong
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 14:
			isSelected = 13;
			mProvinceSelectSettingViewHolder.mGansu
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mGuizhou
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 15:
			isSelected = 14;
			mProvinceSelectSettingViewHolder.mShandong
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mGansu
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 16:
			isSelected = 15;
			mProvinceSelectSettingViewHolder.mShanxi
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mShandong
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 17:
			isSelected = 16;
			mProvinceSelectSettingViewHolder.mShaanxi
			        .setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mShanxi
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;						
		case 18:
			isSelected = 17;
			mProvinceSelectSettingViewHolder.mSichuan
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mShaanxi
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 19:
			isSelected = 18;
			mProvinceSelectSettingViewHolder.mHainan
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mSichuan
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 20:
			isSelected = 19;
			mProvinceSelectSettingViewHolder.mHenan
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mHainan
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 21:
			isSelected = 20;
			mProvinceSelectSettingViewHolder.mHebei
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mHenan
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 22:
			isSelected = 21;
			mProvinceSelectSettingViewHolder.mHubei
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mHebei
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 23:
			isSelected = 22;
			mProvinceSelectSettingViewHolder.mTaiwan
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mHubei
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 24:
			isSelected = 23;
			mProvinceSelectSettingViewHolder.mHunan
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mTaiwan
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 25:
			isSelected = 24;
			mProvinceSelectSettingViewHolder.mHeilongjiang
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mHunan
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 26:
			isSelected = 25;
			mProvinceSelectSettingViewHolder.mXizang
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mHeilongjiang
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 27:
			isSelected = 26;
			mProvinceSelectSettingViewHolder.mXinjiang
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mXizang
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 28:
			isSelected = 27;
			mProvinceSelectSettingViewHolder.mJilin
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mXinjiang
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 29:
			isSelected = 28;
			mProvinceSelectSettingViewHolder.mJiangsu
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mJilin
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 30:
			isSelected = 29;
			mProvinceSelectSettingViewHolder.mJiangxi
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mJiangsu
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 31:
			isSelected = 30;
			mProvinceSelectSettingViewHolder.mYunnan
			        .setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mJiangxi
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;						
		case 32:
			isSelected = 31;
			mProvinceSelectSettingViewHolder.mLiaoning
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mYunnan
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		case 33:
			isSelected = 32;
			mProvinceSelectSettingViewHolder.mZhejiang
					.setBackgroundResource(R.drawable.one_px);
			mProvinceSelectSettingViewHolder.mLiaoning
					.setBackgroundResource(R.drawable.focus_bg_small);
			break;	
		}
	}
		
}
