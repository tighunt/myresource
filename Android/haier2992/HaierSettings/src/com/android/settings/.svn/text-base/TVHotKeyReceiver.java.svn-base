package com.android.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TVHotKeyReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		Log.d("TVHot", "TVHotKeyReceiver.onReceive:" + intent.getAction());
		
		// 接受启动TV的快捷键 的广播，并启动TV
		Intent TVIntent = new Intent("mstar.tvsetting.ui.intent.action.RootActivity");
		TVIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(TVIntent);

	}
	
}
