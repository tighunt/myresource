/**
 * 
 */
package com.android.settings.sleepmode.receiver;

import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

//import com.android.settings.intelligenteye.util.Constant;
import com.android.settings.other.OtherSettings;

/**
 * @className SleepModeRootReceiver.java
 * @author cbl
 * @date 2012-2-21 
 * @since 1.0
 */
public class SleepModeRootReceiver extends BroadcastReceiver {

	private static final String TAG = "SleepModeRootReceiver";
	private final boolean LOGD = true;
//	private OtherSettings mOtherSetting;
//	private SharedPreferences sf;
	private static final String SLEEPTIMER = "sleeptimer";
	private static final String SLEEPTIMER_MODE = "sleeptimer_mode";
	private static final int SLEEP_OFF = 0; // 睡眠时间为关
	@Override
	public void onReceive(final Context context, Intent intent) {
		// TODO Auto-generated method stub
////		if (LOGD)
////		{	
//			Log.d(TAG, "Sleep Mode **********onReceive()"+intent.getAction());
//			Log.d(TAG, "Sleep Mode **********onReceive()"+intent.getAction());
//			Log.d(TAG, "Sleep Mode **********onReceive()"+intent.getAction());
//			Log.d(TAG, "Sleep Mode **********onReceive()"+intent.getAction());
//			Log.d(TAG, "Sleep Mode **********onReceive()"+intent.getAction());
//			Log.d(TAG, "Sleep Mode **********onReceive()Constant"+Constant.ROOT_RECEIVER_ACTION);
//			Log.d(TAG, "Sleep Mode **********onReceive()Constant"+Constant.ROOT_RECEIVER_ACTION);
//			Log.d(TAG, "Sleep Mode **********onReceive()Constant"+Constant.ROOT_RECEIVER_ACTION);
//			Log.d(TAG, "Sleep Mode **********onReceive()Constant"+Constant.ROOT_RECEIVER_ACTION);
//			Log.d(TAG, "Sleep Mode **********onReceive()Constant"+Constant.ROOT_RECEIVER_ACTION);
//			Log.d(TAG, "Sleep Mode **********onReceive()Constant"+Constant.ROOT_RECEIVER_ACTION);
//
////		}
		Log.d(TAG, intent.getAction());
		//20130511 modify by cw
/*		if (intent.getAction().equals(Constant.ROOT_RECEIVER_ACTION)
				|| intent.getAction().equals(Constant.STR_WAKEUP_RECEIVER_ACTION))
		{

			SharedPreferences sf =  context.getSharedPreferences(SLEEPTIMER, Context.MODE_WORLD_READABLE
					+ Context.MODE_WORLD_WRITEABLE);
			
//			int sleepCurrentMode = sf.getInt(SLEEPTIMER_MODE, 0);
//			Log.d(TAG, "Sleep Mode **********onReceive()**********_sleepCurrentMode:"+sleepCurrentMode);
//			Log.d(TAG, "Sleep Mode **********onReceive()**********_sleepCurrentMode:"+sleepCurrentMode);
//			Log.d(TAG, "Sleep Mode **********onReceive()**********_sleepCurrentMode:"+sleepCurrentMode);
//			Log.d(TAG, "Sleep Mode **********onReceive()**********_sleepCurrentMode:"+sleepCurrentMode);
//			Log.d(TAG, "Sleep Mode **********onReceive()**********_sleepCurrentMode:"+sleepCurrentMode);
//			Log.d(TAG, "Sleep Mode **********onReceive()**********_sleepCurrentMode:"+sleepCurrentMode);
			
			
			sf.edit().putInt(SLEEPTIMER_MODE, SLEEP_OFF).commit();//设置睡眠状态为关
			
//			sleepCurrentMode = sf.getInt(SLEEPTIMER_MODE, 0);
//			Log.d(TAG, "Sleep Mode **********onReceive()**********_sleepCurrentMode_New:"+sleepCurrentMode);
//			Log.d(TAG, "Sleep Mode **********onReceive()**********_sleepCurrentMode_New:"+sleepCurrentMode);
//			Log.d(TAG, "Sleep Mode **********onReceive()**********_sleepCurrentMode_New:"+sleepCurrentMode);
//			Log.d(TAG, "Sleep Mode **********onReceive()**********_sleepCurrentMode_New:"+sleepCurrentMode);
//			Log.d(TAG, "Sleep Mode **********onReceive()**********_sleepCurrentMode_New:"+sleepCurrentMode);
//			Log.d(TAG, "Sleep Mode **********onReceive()**********_sleepCurrentMode_New:"+sleepCurrentMode);
			
		}*/
	}
}
