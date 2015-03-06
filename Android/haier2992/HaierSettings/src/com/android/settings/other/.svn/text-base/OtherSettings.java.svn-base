package com.android.settings.other;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.TvManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;

import com.android.settings.R;
//import com.android.settings.intelligenteye.util.Constant;
import com.android.settings.util.Tools;
//import com.tvos.common.TvManager;
//import com.tvos.common.vo.TvOsType;

/**
 * 其它设置的主界面
 * 
 * @author 曹美娟
 * @date 2012-1-2 中午12:27:27
 * @since 1.0
 */
public class OtherSettings extends Activity {

	private final static String NAME = "share_pres";
	// private final static String ONOFF_LIGHT = "light_onoff";
	public final static String STRLIGHT = "light";
	private static final String SLEEPTIMER = "sleeptimer"; // 鐫＄湢鏃堕棿preferences
	private static final String SLEEPTIMER_MODE = "sleeptimer_mode"; // 鐫＄湢鏃堕棿妯″紡
	private static final String ONOFF_IPADMODE = "ipadmode_onoff"; // zhf
	public static final String STRIPADMODE = "ipadmode"; // zhf
	private static final String ONOFF_AUTOUPGRADE = "autoupgrade_onoff";
	public static final String STRAUTOUPGRADE = "autoupgrade";

	public  boolean isAfterPowerOff = false;
	public OtherViewHolder mOtherViewHolder;
	private int isSelected = 0;
	// private int gpioStatus;
	// 商标灯是否为开
//	private boolean isLightOn;
	// ipadmode 是否为开 zhf
	private boolean isIpadmodeon;
	//网络升级开机提示是否为开
	private boolean isAutoUpgrade;
	// EN_SLEEP_TIME_STATE enSleepTimeState;

	private OtherSettingReceiver otherReceiver;
	
	private TvManager mTvManager;
	private Timer downTimer = null;
	private TimerTask timerTask = null;
	
	private Timer intentDownTimer = null;////////////////////////
	private TimerTask intentTimerTask = null;/////////////////////////
	private int leftTimeValue = 60;///////////////////////
	private Intent intent = null;/////////////////////////

	
	   public   Handler handler = new Handler() {
	        public void handleMessage(Message msg) {/*//20130511 modify by cw
	            super.handleMessage(msg);
	            if (msg.what == Constants.CONNECTION_OK) {
	                Bundle bundle = msg.getData();
	                int index = bundle.getInt("Index");
	                switch (index) {
	                }
	            }
	        */
	            Log.d("***********Leewokan********", "----------------Handler----------");
	            super.handleMessage(msg); 
	            if (msg.what == SLEEP_OFF) 
	            {
	            	if(downTimer!=null){
	            		if(timerTask!=null){
	            			timerTask.cancel();
	            	}
	            		
	            	timerTask = new TimerTask() {
	                    @Override
	                    public void run() {
	                    	Log.d("SLEEP_OFF","TimerTask cancel");
	                    }
	                };
	                downTimer.schedule(timerTask, 0);

			if(intentDownTimer!=null){
	            		if(intentTimerTask!=null){
	            			intentTimerTask.cancel();
	            	}
	            		
	            		intentTimerTask = new TimerTask() {
	                    @Override
	                    public void run() {
	                    	Log.d("SLEEP_OFF","intentTimerTask cancel");
	                    }
	                };
	                intentDownTimer.schedule(intentTimerTask, 0);
	            }

	            }
	            }
	            else if(msg.what == SLEEP_5MIN)
	            {
	                goToPowerOff(5* 60 * 1000);
	                intentTransmitData(4* 60 * 1000);
	            }  
	            else if(msg.what == SLEEP_10MIN)
	            {
	                goToPowerOff(10* 60 * 1000);
	                intentTransmitData(9* 60 * 1000);
	            }
	            else if(msg.what == SLEEP_15MIN)
	            {
	                goToPowerOff(15* 60 * 1000);
	                intentTransmitData(14* 60 * 1000);
	            }
	            else if(msg.what == SLEEP_30MIN)
	            {
	                goToPowerOff(30* 60 * 1000);
	                intentTransmitData(29* 60 * 1000);
	            }
	            else if(msg.what == SLEEP_45MIN)
	            {
	                goToPowerOff(45* 60 * 1000);
	                intentTransmitData(44* 60 * 1000);
	            }
	            else if(msg.what == SLEEP_60MIN)
	            {
	                goToPowerOff(60* 60 * 1000);
	                intentTransmitData(59* 60 * 1000);
	            }
	            else if(msg.what == SLEEP_90MIN)
	            {
	                goToPowerOff(90* 60 * 1000);
	                intentTransmitData(89* 60 * 1000);
	            }
	            else if(msg.what == SLEEP_120MIN)
	            {
	                goToPowerOff(120* 60 * 1000);
	                intentTransmitData(119* 60 * 1000);
	            }
	            else if(msg.what == SLEEP_180MIN)
	            {
	                goToPowerOff(180* 60 * 1000);
	                intentTransmitData(179* 60 * 1000);
	            }
	            else if(msg.what == SLEEP_240MIN)
	            {
	                goToPowerOff(240* 60 * 1000);
	                intentTransmitData(239* 60 * 1000);
	            }
	        };
	    };

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.other);

		otherReceiver = new OtherSettingReceiver(this);
		otherReceiver.registerAction("other.setting.eye.receiver");
		otherReceiver.registerAction("TVCounterActivity.notShow"); // 2012-05-04 for sleep countdown

		mTvManager = (TvManager) this.getSystemService("tv");
		findViews();
		intent = new Intent(OtherSettings.this, CountDownTimerActivity.class); //////////////////////////////
		intentDownTimer = new Timer();///////////////////////////////////
        downTimer = new Timer();
//		registerListeners();
	}

	public OtherSettings() {
		super();
	}

	
	
   /* public  TimerTask goToPowerOff() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.d("SleepSettingDialog", "simulation IR power down start ");
                //-->Modify nosignal standby by xhb 20130712
                //simulationIRstand(STANDBYCOMMOND);
                mTvManager.stopRpcServer();
                //<--
            }
        };
        return timerTask;
    }*/
	
	public void intentTransmitData(long time){
		if(intentDownTimer != null){
			if(intentTimerTask != null){
				intentTimerTask.cancel();
			}
		}
		intentTimerTask = new TimerTask() {
            @Override
            public void run() {
            	intent.setAction("com.android.settings.other.CountDownTimerActivity");
            	intent.putExtra("LeftTime", leftTimeValue);
               startActivity(intent);
            }
        };	
        intentDownTimer.schedule(intentTimerTask, time);
	}
    
    public  void goToPowerOff(long time) {
    	if(downTimer!=null){
    		if(timerTask!=null){
    			timerTask.cancel();
    	}
    	timerTask = new TimerTask(){

			@Override
			public void run() {
				// TODO Auto-generated method stub		
				Log.d("SleepSettingDialog", "simulation IR power down start ");
				if(!mTvManager.isTvScanning())
					mTvManager.stopRpcServer();
			}
    		
    	};
    	
    	downTimer.schedule(timerTask, time);
    }
    }

    
	/**
	 * 组件和数据的初始化
	 */
	private void findViews() {
		mOtherViewHolder = new OtherViewHolder(this);
		
		mOtherViewHolder.mOtherSleep.setBackgroundResource(R.drawable.set_button);
		mOtherViewHolder.sleep_mode.requestFocus(); // 2012-05-08 灰掉商标灯和智能眼
		
/*		isLightOn = getLightState();
		if (isLightOn) {
			mOtherViewHolder.mLightOn.setChecked(true);
		} else {
			mOtherViewHolder.mLightOn.setChecked(false);
		}*/
		// ResetLightControl();
		/*/ zhf begin
		isIpadmodeon = getIpadmodeState();
		if (isIpadmodeon) {
			mOtherViewHolder.mIpadmodeOn.setChecked(true);
		} else {
			mOtherViewHolder.mIpadmodeOn.setChecked(false);
		}
		// ResetIpadmodeControl();
		
		*///zhf end
		
		//AutoUpgrade prompt
		isAutoUpgrade = getAutoUpgradeState();
		if (isAutoUpgrade) {
			mOtherViewHolder.mAutoUpgrade.setChecked(true);
		} else {
			mOtherViewHolder.mAutoUpgrade.setChecked(false);
		}
		// intelligente eye
/*		SharedPreferences sf = getSharedPreferences(Constant.INTELLIGENTEYE, Context.MODE_WORLD_READABLE
				+ Context.MODE_WORLD_WRITEABLE);
		int eyeCurrentMode = sf.getInt(Constant.INTELLIGENTEYE_MODE, 0);
		setIntelligenteyeMode(eyeCurrentMode);*/

		// add by cbl at 2012-02-20
		// sleep time
/*		SharedPreferences sf_sleep = getSharedPreferences(SLEEPTIMER, Context.MODE_PRIVATE);
		int sleepCurrentMode = sf_sleep.getInt(SLEEPTIMER_MODE, 0);
  	try {
			TvOsType.EnumSleepTimeState enSleepTimeState = TvManager.getTimerManager().getSleeperState();
			if (enSleepTimeState == TvOsType.EnumSleepTimeState.E_OFF) {
				sleepCurrentMode = SLEEP_OFF;
				sf_sleep.edit().putInt(SLEEPTIMER_MODE, sleepCurrentMode).commit();
			}
		} catch (Exception e) {
		}
		setSleepMode(sleepCurrentMode);
	}
*/
		
		/*
		 * leewokan addd for get sleepCurrentMode
		 * */
		
	/*	SharedPreferences sf_sleep = getSharedPreferences(SLEEPTIMER, Context.MODE_PRIVATE);
        int sleepCurrentMode = sf_sleep.getInt(SLEEPTIMER_MODE, 0);*/
        
        /*Log.d("----------Leewokan----------","isAfterPowerOff = "+ isAfterPowerOff);
        if (isAfterPowerOff) {
            Log.d("----------Leewokan----------","=========isAfterPowerOff is here ==========");
            sleepCurrentMode = SLEEP_OFF;
            sf_sleep.edit().putInt(SLEEPTIMER_MODE, sleepCurrentMode).commit();
        }
        Log.d("----Leewokan----", "sleepCurrentMode  = "+sleepCurrentMode);*/
        int sleepCurrentMode = mTvManager.getTimerAutoSleep();
        Log.d("-----sleepCurrentMode------", "    sleepCurrentMode = "+ sleepCurrentMode);
        setSleepMode(sleepCurrentMode);
        
	/**
	 * 响应各个控件的监听事件
	 */
//	private void registerListeners() {

		// 商标灯是否为开
		/*mOtherViewHolder.mLightOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				isLightOn = isChecked;
				commitCheckBoxValue();
				ResetLightControl();
			}
		});
		mOtherViewHolder.mLightOn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				changeViewSelected(0);
			}
		});
		mOtherViewHolder.mOtherLightcontrol.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				changeViewSelected(0);
			}
		});*/

		// 当前睡眠模式
		mOtherViewHolder.sleep_mode.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SleepSettingDialog sleepSettingDialog = new SleepSettingDialog(OtherSettings.this,handler);
				sleepSettingDialog.show();
				changeViewSelected(0);
			}
		});

		mOtherViewHolder.mOtherSleep.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SleepSettingDialog sleepSettingDialog = new SleepSettingDialog(OtherSettings.this,handler);
				sleepSettingDialog.show();
				changeViewSelected(0);
			}
		});

		// 当前智能眼模式
		/*mOtherViewHolder.intelligenteye_mode.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				IntelligenteyeSelectDialog intelligenteyeSelectDialog = new IntelligenteyeSelectDialog(
						OtherSettings.this);
				intelligenteyeSelectDialog.show();
				changeViewSelected(2);
			}
		});
		mOtherViewHolder.mOtherIntelligenteye.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				IntelligenteyeSelectDialog intelligenteyeSelectDialog = new IntelligenteyeSelectDialog(
						OtherSettings.this);
				intelligenteyeSelectDialog.show();
				changeViewSelected(2);
			}
		});*/

		// 音频设置
		mOtherViewHolder.audio_setting.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Tools.intentForward(OtherSettings.this, AudioSettings.class);
				changeViewSelected(1);
			}
		});
		mOtherViewHolder.mOtherAudiosetting.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Tools.intentForward(OtherSettings.this, AudioSettings.class);
				changeViewSelected(1);
			}
		});

		// 桌面设置
		mOtherViewHolder.desktop_setting.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Tools.intentForward(OtherSettings.this, DesktopSettingActivity.class);
				changeViewSelected(2);
			}
		});
		mOtherViewHolder.mOtherDesktop.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Tools.intentForward(OtherSettings.this, DesktopSettingActivity.class);
				changeViewSelected(2);
			}
		});

		/*/ ipadmode 是否为开 zhf
		mOtherViewHolder.mIpadmodeOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				isIpadmodeon = isChecked;
				commitCheckBoxValue_ipadmode();
				ResetIpadmodeControl();
			}
		});
		mOtherViewHolder.mIpadmodeOn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				changeViewSelected(5);
			}
		});
		mOtherViewHolder.mOtherIpadmode.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				changeViewSelected(5);
			}
		});*/
		mOtherViewHolder.mAutoUpgrade.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				isAutoUpgrade = isChecked;
				commitCheckBoxValue_autoupgrade(isAutoUpgrade);
			}
		});
		mOtherViewHolder.mAutoUpgrade.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				changeViewSelected(3);
			}
		});
		mOtherViewHolder.mOtherAutoUpgrade.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				changeViewSelected(3);
			}
		});
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			dropDown();
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			dropUp();
		} else if (keyCode == KeyEvent.KEYCODE_BACK) { // back
			OtherSettings.this.finish();
		}
		return super.onKeyUp(keyCode, event);
	}

	/**
	 * 商标灯是否为开，默认为关。
	 * 
	 * @return
	 */
	/*private boolean getLightState() {
		return Settings.System.getInt(getContentResolver(), STRLIGHT, 0) > 0;
	}*/

	/*private boolean ResetLightControl() {
		// return isLightOn;
		try {
			if (isLightOn) {
				TvManager.setGpioDeviceStatus(0x83, true);
			} else {
				TvManager.setGpioDeviceStatus(0x83, false);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return isLightOn;
	}*/

	/**
	 * zhf ipadmode是否为开
	 * 
	 * @return
	 */
	/*
	private boolean getIpadmodeState() {
		return Settings.System.getInt(getContentResolver(), STRIPADMODE, 0) > 0;
	}
	*/
	/**
	 * 自动升级提示是否为开
	 * 
	 * @return
	 */
	private boolean getAutoUpgradeState() {
		return Settings.System.getInt(getContentResolver(), STRAUTOUPGRADE, 1) > 0;
	}
	/*
	private boolean ResetIpadmodeControl() {
		// return isLightOn;
		try {
			if (isIpadmodeon) {
				TvManager.setGpioDeviceStatus(31, false); // USB_POWER_CTRL 31
				Toast toast = Toast.makeText(this, R.string.str_other_ipadmode_toast_on, Toast.LENGTH_LONG);

				toast.show();
			} else {
				TvManager.setGpioDeviceStatus(31, true);
				Toast toast = Toast.makeText(this, R.string.str_other_ipadmode_toast_off, Toast.LENGTH_LONG);
				toast.show();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return isIpadmodeon;
	}*/

	/**
	 * 向下滑动
	 */
	private void dropDown() {
		switch (isSelected) {
		case 0:
			isSelected = 1;
			mOtherViewHolder.mOtherSleep.setBackgroundResource(R.drawable.one_px);
			getCurrentFocus().clearFocus();
			mOtherViewHolder.mOtherAudiosetting.setBackgroundResource(R.drawable.set_button);
			mOtherViewHolder.audio_setting.requestFocus();
			break;
		case 1:
			isSelected = 2;
			mOtherViewHolder.mOtherAudiosetting.setBackgroundResource(R.drawable.one_px);
			getCurrentFocus().clearFocus();
			mOtherViewHolder.mOtherDesktop.setBackgroundResource(R.drawable.set_button);
			mOtherViewHolder.desktop_setting.requestFocus();
			break;
	/*	case 2:
			isSelected = 3;
			mOtherViewHolder.mOtherDesktop.setBackgroundResource(R.drawable.one_px);
			getCurrentFocus().clearFocus();
			mOtherViewHolder.mOtherIpadmode.setBackgroundResource(R.drawable.set_button);
			mOtherViewHolder.mIpadmodeOn.requestFocus();
			break;*/
		case 2:
			isSelected = 3;
			mOtherViewHolder.mOtherDesktop.setBackgroundResource(R.drawable.one_px);
			getCurrentFocus().clearFocus();
			mOtherViewHolder.mOtherAutoUpgrade.setBackgroundResource(R.drawable.set_button);
			mOtherViewHolder.mAutoUpgrade.requestFocus();
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
			mOtherViewHolder.mOtherAudiosetting.setBackgroundResource(R.drawable.one_px);
			getCurrentFocus().clearFocus();
			mOtherViewHolder.mOtherSleep.setBackgroundResource(R.drawable.set_button);
			mOtherViewHolder.sleep_mode.requestFocus();
			break;
		case 2:
			isSelected = 1;
			mOtherViewHolder.mOtherDesktop.setBackgroundResource(R.drawable.one_px);
			getCurrentFocus().clearFocus();
			mOtherViewHolder.mOtherAudiosetting.setBackgroundResource(R.drawable.set_button);
			mOtherViewHolder.audio_setting.requestFocus();
			break;
/*		case 3:
			isSelected = 2;
			mOtherViewHolder.mOtherIpadmode.setBackgroundResource(R.drawable.one_px);
			getCurrentFocus().clearFocus();
			mOtherViewHolder.mOtherDesktop.setBackgroundResource(R.drawable.set_button);
			mOtherViewHolder.desktop_setting.requestFocus();
			break;*/
		case 3:
			isSelected = 2;
			mOtherViewHolder.mOtherAutoUpgrade.setBackgroundResource(R.drawable.one_px);
			getCurrentFocus().clearFocus();
			mOtherViewHolder.mOtherDesktop.setBackgroundResource(R.drawable.set_button);
			mOtherViewHolder.desktop_setting.requestFocus();
			break;
		}
	}

	/**
	 * 向下滑动
	 */
	/*private void dropDown() {
		switch (isSelected) {
		case 0:
			isSelected = 1;
			mOtherViewHolder.mOtherLightcontrol.setBackgroundResource(R.drawable.one_px);
			getCurrentFocus().clearFocus();
			mOtherViewHolder.mOtherSleep.setBackgroundResource(R.drawable.set_button);
			mOtherViewHolder.sleep_mode.requestFocus();
			break;
		case 1:
			isSelected = 2;
			mOtherViewHolder.mOtherSleep.setBackgroundResource(R.drawable.one_px);
			getCurrentFocus().clearFocus();
			mOtherViewHolder.mOtherIntelligenteye.setBackgroundResource(R.drawable.set_button);
			mOtherViewHolder.intelligenteye_mode.requestFocus();
			break;
		case 2:
			isSelected = 3;
			mOtherViewHolder.mOtherIntelligenteye.setBackgroundResource(R.drawable.one_px);
			getCurrentFocus().clearFocus();
			mOtherViewHolder.mOtherAudiosetting.setBackgroundResource(R.drawable.set_button);
			mOtherViewHolder.audio_setting.requestFocus();
			break;
		case 3:
			isSelected = 4;
			mOtherViewHolder.mOtherAudiosetting.setBackgroundResource(R.drawable.one_px);
			getCurrentFocus().clearFocus();
			mOtherViewHolder.mOtherDesktop.setBackgroundResource(R.drawable.set_button);
			mOtherViewHolder.desktop_setting.requestFocus();
			break;
		// zhf begin
		case 4:
			isSelected = 5;
			mOtherViewHolder.mOtherDesktop.setBackgroundResource(R.drawable.one_px);
			getCurrentFocus().clearFocus();
			mOtherViewHolder.mOtherIpadmode.setBackgroundResource(R.drawable.set_button);
			mOtherViewHolder.mIpadmodeOn.requestFocus();
			break;
		// zhf end
		}
	} */

	/**
	 * 向上滑动
	 */
	/*private void dropUp() {
		switch (isSelected) {
		case 1:
			isSelected = 0;
			mOtherViewHolder.mOtherSleep.setBackgroundResource(R.drawable.one_px);
			getCurrentFocus().clearFocus();
			mOtherViewHolder.mOtherLightcontrol.setBackgroundResource(R.drawable.set_button);
			mOtherViewHolder.mLightOn.requestFocus();
			break;
		case 2:
			isSelected = 1;
			mOtherViewHolder.mOtherIntelligenteye.setBackgroundResource(R.drawable.one_px);
			getCurrentFocus().clearFocus();
			mOtherViewHolder.mOtherSleep.setBackgroundResource(R.drawable.set_button);
			mOtherViewHolder.sleep_mode.requestFocus();
			break;
		case 3:
			isSelected = 2;
			mOtherViewHolder.mOtherAudiosetting.setBackgroundResource(R.drawable.one_px);
			getCurrentFocus().clearFocus();
			mOtherViewHolder.mOtherIntelligenteye.setBackgroundResource(R.drawable.set_button);
			mOtherViewHolder.intelligenteye_mode.requestFocus();
			break;
		case 4:
			isSelected = 3;
			mOtherViewHolder.mOtherDesktop.setBackgroundResource(R.drawable.one_px);
			getCurrentFocus().clearFocus();
			mOtherViewHolder.mOtherAudiosetting.setBackgroundResource(R.drawable.set_button);
			mOtherViewHolder.audio_setting.requestFocus();
			break;
		// zhf begin
		case 5:
			isSelected = 4;
			mOtherViewHolder.mOtherIpadmode.setBackgroundResource(R.drawable.one_px);
			getCurrentFocus().clearFocus();
			mOtherViewHolder.mOtherDesktop.setBackgroundResource(R.drawable.set_button);
			mOtherViewHolder.desktop_setting.requestFocus();
			break;
		// zhf end
		}
	}*/

	/**
	 * 保存商标灯是否为开
	 */
/*	private void commitCheckBoxValue() {
		
		SharedPreferences preference = getSharedPreferences(NAME, Context.MODE_PRIVATE);
		Editor edit = preference.edit();
		edit.putBoolean(ONOFF_LIGHT, isLightOn);
		edit.commit();

		Settings.System.putInt(getContentResolver(), STRLIGHT, isLightOn ? 1 : 0);
	}*/

	/**
	 * 保存ipadmode是否为开 zhf
	 */
/*	private void commitCheckBoxValue_ipadmode() {
		SharedPreferences preference = getSharedPreferences(NAME, Context.MODE_PRIVATE);

		Editor edit = preference.edit();

		edit.putBoolean(ONOFF_IPADMODE, isIpadmodeon);

		edit.commit();
		Settings.System.putInt(getContentResolver(), STRIPADMODE, isIpadmodeon ? 1 : 0);

	}*/
	/**
	 * 保存autoupgrade是否为开
	 */
	private void commitCheckBoxValue_autoupgrade(boolean isAutoUpgrade) {
		SharedPreferences preference = getSharedPreferences(NAME, Context.MODE_PRIVATE);

		Editor edit = preference.edit();

		edit.putBoolean(ONOFF_AUTOUPGRADE, isAutoUpgrade);

		edit.commit();
		Settings.System.putInt(getContentResolver(), STRAUTOUPGRADE, isAutoUpgrade ? 1 : 0);

	}
	/**
	 * add by Zhanghs at 2012-02-17
	 * 
	 * 设置智能眼模式
	 * 
	 * @param eyeMode
	 */
	/*public void setIntelligenteyeMode(int eyeMode) {
		switch (eyeMode) {
		case Constant.EYE_CLOSE:
			mOtherViewHolder.intelligentyey_mode_return.setText(R.string.intelligent_eye_mode1);
			break;
		case Constant.EYE_OPEN:
			mOtherViewHolder.intelligentyey_mode_return.setText(R.string.intelligent_eye_mode3);
			break;
		case Constant.EYE_DEMO:
			mOtherViewHolder.intelligentyey_mode_return.setText(R.string.intelligent_eye_mode2);
			break;
		default:
			mOtherViewHolder.intelligentyey_mode_return.setText(R.string.intelligent_eye_mode1);
		}
	}*/

	public void setSleepTime(int sleepMode)
	{
		mTvManager.setTimerAutoSleep(sleepMode);
	}
	/**
	 * add by cbl at 2012-02-20
	 * 
	 * 选择睡眠时间后的当前状态显示
	 * 
	 * @param sleepMode
	 */
	private static final int SLEEP_OFF = 0; // 鐫＄湢鏃堕棿涓哄叧
	private static final int SLEEP_5MIN = 1; // 鐫＄湢鏃堕棿涓�鍒嗛挓
	private static final int SLEEP_10MIN = 2; // 鐫＄湢鏃堕棿涓�0鍒嗛挓
	private static final int SLEEP_15MIN = 3; // 鐫＄湢鏃堕棿涓�5鍒嗛挓
	private static final int SLEEP_30MIN = 4; // 鐫＄湢鏃堕棿涓�0鍒嗛挓
	private static final int SLEEP_45MIN = 5; // 鐫＄湢鏃堕棿涓�5鍒嗛挓
	private static final int SLEEP_60MIN = 6; // 鐫＄湢鏃堕棿涓�0鍒嗛挓
	private static final int SLEEP_90MIN = 7; // 鐫＄湢鏃堕棿涓�0鍒嗛挓
	private static final int SLEEP_120MIN = 8; // 鐫＄湢鏃堕棿涓�20鍒嗛挓
	private static final int SLEEP_180MIN = 9; // 鐫＄湢鏃堕棿涓�80鍒嗛挓
	private static final int SLEEP_240MIN = 10; // 鐫＄湢鏃堕棿涓�40鍒嗛挓

	public void setSleepMode(int sleepMode) {
		switch (sleepMode) {
		case SLEEP_OFF:
			//mOtherViewHolder.sleep_mode_return.setText(R.string.sleep_mode_0);
			mOtherViewHolder.sleep_mode.setText(R.string.sleep_mode_0);
			break;
		case SLEEP_5MIN:
			mOtherViewHolder.sleep_mode.setText(R.string.sleep_mode_1);
			break;
		case SLEEP_10MIN:
			mOtherViewHolder.sleep_mode.setText(R.string.sleep_mode_2);
			break;
		case SLEEP_15MIN:
			mOtherViewHolder.sleep_mode.setText(R.string.sleep_mode_3);
			break;
		case SLEEP_30MIN:
			mOtherViewHolder.sleep_mode.setText(R.string.sleep_mode_4);
			break;
		case SLEEP_45MIN:
			mOtherViewHolder.sleep_mode.setText(R.string.sleep_mode_5);
			break;
		case SLEEP_60MIN:
			mOtherViewHolder.sleep_mode.setText(R.string.sleep_mode_6);
			break;
		case SLEEP_90MIN:
			mOtherViewHolder.sleep_mode.setText(R.string.sleep_mode_7);
			break;
		case SLEEP_120MIN:
			mOtherViewHolder.sleep_mode.setText(R.string.sleep_mode_8);
			break;
		case SLEEP_180MIN:
			mOtherViewHolder.sleep_mode.setText(R.string.sleep_mode_9);
			break;
		case SLEEP_240MIN:
			mOtherViewHolder.sleep_mode.setText(R.string.sleep_mode_10);
			break;
		default:
			mOtherViewHolder.sleep_mode.setText(R.string.sleep_mode_0);

		}

	}

	public class OtherSettingReceiver extends BroadcastReceiver {
		private OtherSettingReceiver receiver;
		private Context mContext;

		public OtherSettingReceiver(Context context) {
			this.mContext = context;
			this.receiver = this;
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("other.setting.eye.receiver")) {
				int eyeMode = intent.getIntExtra("eye_mode", 0);
	//			setIntelligenteyeMode(eyeMode);
			} else if (intent.getAction().equals("TVCounterActivity.notShow")) {
				setSleepModeOff(); // zhs 2012-05-05 for sleep countdown
			}
		}

		public void registerAction(String action) {
			IntentFilter filter = new IntentFilter();
			filter.addAction(action);
			mContext.registerReceiver(receiver, filter);
		}
	}

	@Override
	protected void onDestroy() {
		if (null != otherReceiver) {
			unregisterReceiver(otherReceiver);
		}
		super.onDestroy();
	}

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
				mOtherViewHolder.mOtherSleep.setBackgroundResource(R.drawable.one_px);
				mOtherViewHolder.sleep_mode.clearFocus();
				break;
			case 1:
				mOtherViewHolder.mOtherAudiosetting.setBackgroundResource(R.drawable.one_px);
				mOtherViewHolder.audio_setting.clearFocus();
				break;
			case 2:
				mOtherViewHolder.mOtherDesktop.setBackgroundResource(R.drawable.one_px);
				mOtherViewHolder.desktop_setting.clearFocus();
				break;
			case 3:
				mOtherViewHolder.mOtherAutoUpgrade.setBackgroundResource(R.drawable.one_px);
				mOtherViewHolder.mAutoUpgrade.clearFocus();
				break;
		/*	case 6:
				mOtherViewHolder.mOtherAutoUpgrade.setBackgroundResource(R.drawable.one_px);
				mOtherViewHolder.mAutoUpgrade.clearFocus();*/
			default:
				mOtherViewHolder.mOtherSleep.setBackgroundResource(R.drawable.one_px);
				mOtherViewHolder.sleep_mode.clearFocus();
			}
		} else {
			switch (seleted) {
			case 0:
				mOtherViewHolder.mOtherSleep.setBackgroundResource(R.drawable.set_button);
				mOtherViewHolder.sleep_mode.setFocusable(true);
				mOtherViewHolder.sleep_mode.setFocusableInTouchMode(true);
				mOtherViewHolder.sleep_mode.requestFocus();
				break;
			case 1:
				mOtherViewHolder.mOtherAudiosetting.setBackgroundResource(R.drawable.set_button);
				mOtherViewHolder.audio_setting.setFocusable(true);
				mOtherViewHolder.audio_setting.setFocusableInTouchMode(true);
				mOtherViewHolder.audio_setting.requestFocus();
				break;
			case 2:
				mOtherViewHolder.mOtherDesktop.setBackgroundResource(R.drawable.set_button);
				mOtherViewHolder.desktop_setting.setFocusable(true);
				mOtherViewHolder.desktop_setting.setFocusableInTouchMode(true);
				mOtherViewHolder.desktop_setting.requestFocus();
				break;
			case 3:
				mOtherViewHolder.mOtherAutoUpgrade.setBackgroundResource(R.drawable.set_button);
				mOtherViewHolder.mAutoUpgrade.setFocusable(true);
				mOtherViewHolder.mAutoUpgrade.setFocusableInTouchMode(true);
				mOtherViewHolder.mAutoUpgrade.requestFocus();
				break;
		/*	case 6:
				mOtherViewHolder.mOtherAutoUpgrade.setBackgroundResource(R.drawable.set_button);
				mOtherViewHolder.mAutoUpgrade.setFocusable(true);
				mOtherViewHolder.mAutoUpgrade.setFocusableInTouchMode(true);
				mOtherViewHolder.mAutoUpgrade.requestFocus();
				break;*/
			default:
				mOtherViewHolder.mOtherSleep.setBackgroundResource(R.drawable.set_button);
				mOtherViewHolder.sleep_mode.setFocusable(true);
				mOtherViewHolder.sleep_mode.setFocusableInTouchMode(true);
				mOtherViewHolder.sleep_mode.requestFocus();
			}
		}
	}

	public void changeViewSelected(int selected) {
		changeViewSelected(isSelected, false);
		isSelected = selected;
		changeViewSelected(isSelected, true);
	}

	/**
	 * 睡眠计时框取消时，关闭睡眠模式
	 */
	public void setSleepModeOff() {
		try {
			SharedPreferences sf = getSharedPreferences(SLEEPTIMER, Context.MODE_PRIVATE);
			sf.edit().putInt(SLEEPTIMER_MODE, SLEEP_OFF).commit();
			setSleepMode(SLEEP_OFF);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 2012-05-08
	 * 
	 * @param enabled
	 */
	public void setViewEnable(boolean enabled) {
		mOtherViewHolder.mLightOn.setFocusable(enabled);
		mOtherViewHolder.mLightOn.setEnabled(enabled);
		mOtherViewHolder.mOtherLightcontrol.setFocusable(enabled);
		mOtherViewHolder.mOtherLightcontrol.setEnabled(enabled);

		mOtherViewHolder.intelligenteye_mode.setFocusable(enabled);
		mOtherViewHolder.intelligenteye_mode.setEnabled(enabled);
		mOtherViewHolder.mOtherIntelligenteye.setFocusable(enabled);
		mOtherViewHolder.mOtherIntelligenteye.setEnabled(enabled);
	}

}
