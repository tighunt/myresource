package com.realtek.dmr;

import com.android.server.Service_DMR;

import android.content.BroadcastReceiver; 
import android.content.Context; 
import android.content.Intent; 
import android.util.Log;

 
public class BootCompletedReceiver extends BroadcastReceiver { 
  @Override 
  public void onReceive(Context context, Intent intent) { 
    if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) 
    { 
    	Log.e("LOG", "start com.android.server.Service_DMR");
      Intent newIntent = new Intent(context,Service_DMR.class);
      context.startService(newIntent);      
    }       
  } 
} 
