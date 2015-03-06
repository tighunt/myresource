package com.haier.launcher2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LauncherSetStorageReceiver extends BroadcastReceiver{
	
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		Log.i("Launcher", "=========LauncherSetStorageReceiver=========");
		if ("com.haier.launcher.storage".equals(arg1.getAction())) {
				Launcher.SetStorage();
			}
	}
}
