package com.haier.launcher2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class LanguageOrWallpaperSettingReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		Log.i("Launcher", "receiver set language or wallpaper broadcast......");

		if ("com.android.settings.languageOrWallpaperSetting.action".equals(intent.getAction())) {
			Bundle mbundle = intent.getExtras();
			String temp = mbundle.getString("Setting");
			Log.i("Launcher", "===========temp:" + temp);
			if (temp.equals("SettingStart")) {
				Log.i("Launcher", "===========SettingStart===============");
				Launcher.setupTvBigWindowByArgOne(context, true, false, false);
			} else if (temp.equals("SettingEnd")) {
				Log.i("Launcher", "===========SettingEnd===============");
				Launcher.setupTvBigWindowByArgOne(context, false, true, false);
			} else if (temp.equals("WallPaperEnd")) {
				Log.i("Launcher", "===========WallPaperEnd===============");
				Launcher.setupTvBigWindowByArgOne(context, false, false, true);
			} else {
				Log.i("Launcher", "===========WallPaper else===============");
				Launcher.setupTvBigWindowByArgOne(context, false, false, false);
			}
		}
	}
}
