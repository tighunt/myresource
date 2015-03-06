package com.android.settings.applications;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.settings.ApplicationSettings;

public class LowMemoryReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(Intent.ACTION_DEVICE_STORAGE_LOW) || action.equals(Intent.ACTION_DEVICE_STORAGE_FULL)) {

			Log.d("zjf", "LowMemoryReceiver.onReceive.收到【内部存储空间低】的广播了*****action:" + action);

			ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningTaskInfo> taskList = am.getRunningTasks(1);
			if (taskList.size() > 0) {
				ComponentName cn = taskList.get(0).topActivity;
				String packageName = cn.getPackageName();
				Log.d("zjf", "LowMemoryReceiver.onReceive*****packageName:" + packageName);
				if (!packageName.equals("com.android.settings")) {
					Intent mIntent = new Intent();
					mIntent.setClass(context, ApplicationSettings.class);
					mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(mIntent);
				}
			}

		}
	}

}
