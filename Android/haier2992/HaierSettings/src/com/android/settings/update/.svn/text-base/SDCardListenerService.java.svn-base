package com.android.settings.update;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.android.settings.R;

/**
 * Listening plug and play of the external storage
 * 
 * @author ducj(ducj@biaoqi.com.cn)
 * @since 1.0 2011-11-23
 */
public class SDCardListenerService extends Service {

	private final static String BROAD_ACTION = "SDCARD_ACTION";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		registerSDCardListener();
	}
	
	public void onDestroy() {
		unregisterReceiver(broadcastReceiver);
	};

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.d("TAG", "====>@@@@sdcard action:::::" + action);
			if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
				// || Intent.ACTION_MEDIA_SCANNER_STARTED.equals(action)
				// || Intent.ACTION_MEDIA_SCANNER_FINISHED.equals(action)
				// SD卡成功挂载
				Intent msgIntent = new Intent();
				msgIntent.setAction(BROAD_ACTION);

				// 要发送的内容
				msgIntent.putExtra("sd", getString(R.string.sdcard_insert));
				msgIntent.putExtra("success", true);

				// 发送 一个无序广播
				sendBroadcast(msgIntent);
			} else if (Intent.ACTION_MEDIA_REMOVED.equals(action)) {
				// || Intent.ACTION_MEDIA_UNMOUNTED.equals(action)
				// || Intent.ACTION_MEDIA_BAD_REMOVAL.equals(action)
				// SD卡挂载失败
				Intent msgIntent = new Intent();
				msgIntent.setAction(BROAD_ACTION);
				// 要发送的内容
				msgIntent.putExtra("sd", getString(R.string.sdcard_remove));
				msgIntent.putExtra("success", false);
				// 发送 一个无序广播
				sendBroadcast(msgIntent);
			}
		}
	};

	// 注册监听
	private void registerSDCardListener() {
		IntentFilter intentFilter = new IntentFilter(
				Intent.ACTION_MEDIA_MOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
		intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
		intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
		intentFilter.addDataScheme("file");
		registerReceiver(broadcastReceiver, intentFilter);
	}
}
