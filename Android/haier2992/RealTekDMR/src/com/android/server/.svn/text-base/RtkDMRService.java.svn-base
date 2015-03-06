/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.server;

import java.net.InetAddress;
import java.net.Inet4Address;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.TvManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class RtkDMRService {
	private static final String TAG = "DMRServicejava";
	public native void RTK_DLNA_DMR_deinit();
	public native void RTK_DLNA_DMR_init(String DMRname);
	private native void setSeekPosition(int position);
	public  SeekReceiver seekreceiver = new SeekReceiver();
	public native boolean CheckDMRIsBusy();
	private Handler handler;
	private boolean DMRIsQuit = false;
	//DMR CMD
	private final int DMR_RESTART = 0;
	private TvManager mTV;
	private static int CurrentTime;
	public  String UUID = null;
	private String DMRNAME = null;
    Context mContext;	
    public static String DMRname = null;
    
    public RtkDMRService(Context context) {
        mContext = context;
        mTV = (TvManager) mContext.getSystemService("tv");	
        UUID = SetUUID();
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg){
				switch(msg.what){
				case DMR_RESTART:
					Log.i(TAG, "DMRService restart !!!");
					new Thread(){
            					public void run(){
					               try {
					                  Thread.sleep(2000);
								Log.i(TAG, "------delay 2s!!!");
								RTK_DLNA_DMR_deinit();
								String hostip = getLocalIpAddress("eth0");
								if(hostip == null)
									hostip = getLocalIpAddress("wlan0");
								if(hostip != null)
								{
									int Iplen = hostip.length();
									int Iplastpoint = hostip.lastIndexOf(".");
									DMRNAME = DMRname+(hostip.substring(Iplastpoint+1, Iplen));
								}else
								{
									DMRNAME = DMRname;
								}
								Log.i(TAG,DMRNAME);
								RTK_DLNA_DMR_init(DMRNAME);					                 
					               } catch (InterruptedException e) { }
					            }
					         }.start();
					break;
				}
			}
		};
    }
    
    public class SeekReceiver extends BroadcastReceiver{
		public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
			if(action.equals("com.rtk.dmr.position.broadcast"))
		          {
					CurrentTime = intent.getIntExtra("currentTime",0);
					SetMediaPosition();
		          }
			}
		}
    
	public RtkDMRService() 
	{
	     Log.w(TAG, "DMRService in java is constructed!");
	}
	
	static
	{
		 System.loadLibrary("DMR");
	}
	   
	@SuppressLint("NewApi")
	public String getLocalIpAddress(String interfacename){  
		String IPV4 = null;
		InterfaceAddress Addr;
	    List<InterfaceAddress> list;
		InetAddress inetaddres = null; 	
		try{		
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {  
	            NetworkInterface intf = en.nextElement();
	            if(intf.getName().equals(interfacename))
	            {
		            list = intf.getInterfaceAddresses();
		            for(int i=0;i<list.size();i++)
		            {
		            	Addr = list.get(i);
		            	inetaddres = Addr.getAddress();
		            	if(inetaddres instanceof Inet4Address)
		            	{
		            		IPV4 = inetaddres.getHostAddress();
		            		return IPV4;
		            	}
		            }
	            }
	        }
		}catch (SocketException e1) {
			e1.printStackTrace();
		}
			return null;
	}
	
	public void preParse()
	{
		 Log.i(TAG,"preParse in java test");
	}
	    
			
	public void loadMedia(char filename)
	{
		Log.i(TAG,"loadMedia in java test");
	}
		 		 		 	
	public void Play(String filename,int speed,int renderType)
	{
 	   Log.i(TAG,"\nPlay in java test, filename is "+filename+"\n speed is "+speed+" renderType is "+renderType);	
	  if(renderType==1) //Music
	 	{
			Intent Stopintent = new Intent("com.android.DMRService.toplay");
		 	Stopintent.putExtra("cmd", "Audio");
	        mContext.sendBroadcast(Stopintent);
	 		ComponentName componetName = new ComponentName(
	 				"com.realtek.dmr",
	 				"com.realtek.dmr.DmrMusicPlay");
			Log.i(TAG,"create MusicActivity\n");
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
						
			String[] pathList ={filename};
			bundle.putBoolean("fromDMR", true);
			
			bundle.putInt("initPos", 0);
			bundle.putInt("len", 1);
			bundle.putStringArray("filelist", pathList);
			bundle.putBoolean("isanywhere", true);//dmr or tvanywhere
			bundle.putInt("browserType", 1);
			
			intent.putExtras(bundle);
			intent.setComponent(componetName);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Log.i(TAG,"\nPlay in java test Afooo 1111");
			mContext.startActivity(intent);
			Log.i(TAG,"\nPlay in java test Afooo 2222");

		}else if(renderType==2) //Video
		{
			Intent Stopintent = new Intent("com.android.DMRService.toplay");
		 	Stopintent.putExtra("cmd", "Video");
	        mContext.sendBroadcast(Stopintent);
	          
			ComponentName componetName = new ComponentName(
					"com.realtek.dmr",
					"com.realtek.dmr.DmrVideoPlayerActivity");
			Log.i(TAG,"create VideoPlayerActivity\n");
			
			Intent intent = new Intent();
			Bundle bundle = new Bundle();

			String[] pathList ={filename};
			bundle.putInt("initPos", 0);
			bundle.putInt("len", 1);
			bundle.putStringArray("filelist", pathList);
			bundle.putBoolean("isanywhere", true);//dmr or tvanywhere
			bundle.putInt("browserType", 1);
			
			intent.putExtras(bundle);
			intent.setComponent(componetName);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Log.i(TAG,"\nPlay in java test Afooo 1111");
			mContext.startActivity(intent);
			Log.i(TAG,"\nPlay in java test Afooo 2222");

		}else if(renderType==4) //Photo
		{
			Intent Stopintent = new Intent("com.android.DMRService.toplay");
			Stopintent.putExtra("cmd","Photo");
		    mContext.sendBroadcast(Stopintent);
		          
		    ComponentName componetName = new ComponentName(
		    		"com.realtek.dmr",
                    "com.realtek.dmr.DmrPhotoPlayerActivity_M");
			Log.i(TAG,"create PhotoPlayerActivity\n");
			Intent intent = new Intent();
			Bundle bundle = new Bundle();

			String[] pathList ={filename};
			
			bundle.putInt("initPos", 0);
			bundle.putInt("len", 1);
			bundle.putStringArray("filelist", pathList);
			bundle.putBoolean("isanywhere", true);//dmr or tvanywhere
			
			intent.putExtras(bundle);
			intent.setComponent(componetName);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Log.i(TAG,"\nPlay in java test Afooo 1111");
			mContext.startActivity(intent);
			Log.i(TAG,"\nPlay in java test Afooo 2222");
		 }
	 		
    }
 	public void Stop()
 	{
 		DMRIsQuit = true;
 		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(DMRIsQuit) {
					// sendBroadCast
					Intent Stopintent = new Intent("com.android.DMRService.stop");
			 		mContext.sendBroadcast(Stopintent);
			 		Log.e(TAG,"DMR APK is quit !!");
				}
			}
		}, (long)6000);
 		Log.i(TAG,"Stop in java test");
	}
 	public void Pause(boolean Pause){
 		Intent Pauseintent = new Intent("com.android.DMRService.pause");
        mContext.sendBroadcast(Pauseintent);
 		Log.i(TAG,"Pause in java test, Pause is "+Pause);
	}
 	public void Restart()
 	{
		Message message = new Message();
		message.what = DMR_RESTART;
		handler.sendMessage(message);
 		Log.i(TAG,"Java send restart message done!");
	}
 	public void SetPlayMode(int mode)
	{
 		Log.i(TAG,"Java SetPlayMode done!");
	}
 	public void SetVolume(int volume)
 	{
 		mTV.setVolume(volume);
 		Log.i(TAG,"\nSetVolume in java "+volume);
 	}
 	public void GetVolume(int volume)
 	{
 		mTV.getVolume();
 		Log.i(TAG,"\nSetVolume in java "+volume);
 	}
 	public void SetContrast(int contrast)
 	{
 		mTV.setContrast(contrast);
 		Log.i(TAG,"\n SetContrast in java");
 	}
 	public void SetBrightness(int brightness)
 	{
 		//mTV.setBrightnessTsb(brightness);
 		Log.i(TAG,"\n SetBrightness in java");
 	}
 	public void GetBrightness(int brightness)
 	{
 		mTV.getBrightness();
 		Log.i(TAG,"\n SetBrightness in java");
 	}
 	public void QueryForConnection()
 	{
 		DMRIsQuit = false;
 		Log.i(TAG,"\n QueryForConnection in java");
 	}
 	public void SeekMediaPosition(int position)
 	{
 		Intent Seekintent = new Intent("com.rtk.dmr.seek.broadcast");
 		int millisecends = position*1000;
 		Seekintent.putExtra("Seekpos",millisecends);
 		mContext.sendBroadcast(Seekintent);
 		Log.i(TAG,"\n SeekMediaPosition in java "+millisecends);
 	}
 	public int SetMediaPosition()
 	{
 		Log.i(TAG,"\n SetMediaPosition in java ");
 		setSeekPosition(CurrentTime);
 		return 0;
 	}
 	public String SetUUID()
    {
 	   UUIDClass getUuid = new UUIDClass(mContext);
 	   if(getUuid.toString()== null)
 		   return "";
 	   else
 	   {
 		   return getUuid.getDeviceUuid().toString();
 	   }
    }
	
}





