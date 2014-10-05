
package com.rtk.mediabrowser;

import java.io.File;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PowerDownReceiver extends BroadcastReceiver {
    private final static String TAG = "PowerDownReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals("android.intent.action.ACTION_SHUTDOWN")) {
        	Log.d(TAG, "receive Power Down message!");
        	// fixme here
        	File f = new File("/data/data/com.rtk.mediabrowser/files/VideoBookMark.bin");
    		if (f != null)
    			f.delete();

			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);
        }
    }  
}
