/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.server;

import com.realtek.dmr.R;
import com.realtek.sync.CommonSemaphore;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.TvManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class RtkDMRService {
	private static final String TAG = "DMRServicejava";
	public native void RTK_DLNA_DMR_deinit();
	public native void RTK_DLNA_DMR_init(String DMRname);
	public native static byte[] getMediaInfo();
	public native static int getFileType();
	public native static int getfileDate();
	public native static void setfilePlayspeed(String speed);
	public native static String getfileTitle();
	private native void setSeekPosition(int position);
	private native void setStateStop();
	public  SeekReceiver seekreceiver = new SeekReceiver();
	public  StopReceiver stopreceiver = new StopReceiver();
	public	NotifyReceiver notifyReceiver = new NotifyReceiver();
	public	SetVolumeReceiver setVolReceiver = new SetVolumeReceiver();
	private Handler handler;
	public int  PlaylistFileByteSize = 0;
	public int  PlaylistFileCharSize = 0;
	public char[] tempbuffer;
	//DMR CMD
	private final int DMR_RESTART = 0;
	private final int DMR_Start = 1;
	private final int DMR_Play = 2;
	private TvManager mTV;
	private static int CurrentTime;
	private static boolean isplay;
	public static int MaxVolume = 30;
	public  String UUID = null;
    Context mContext;	
    static String DMRname = "haier_dmr";
    public String Error = "error";
    private Toast toast;
    private static boolean playing = false;
    Object obj = new Object();
    volatile boolean flag = false; //no wait! 
    
    String signalType = "";	// dfault , "Photo", "Video", "Music"
    
    RtkDMRService(Context context) {
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
								Thread.sleep(10000);
								RTK_DLNA_DMR_init(DMRname);					                 
					               } catch (InterruptedException e) { }
					            }
					         }.start();
					break;
				case DMR_Start:
					toast = Toast.makeText(mContext, R.string.DMR_start, Toast.LENGTH_SHORT);
		            toast.setGravity(Gravity.CENTER|Gravity.BOTTOM, 0, 0);
					toast.show(); 
		            break;
				case DMR_Play:
					toast.cancel();
					break;
	            }
			}
		};
    }
    
    public class SetVolumeReceiver extends BroadcastReceiver{
		public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
			if(action.equals("com.android.settings.dmr.volumeset"))
			      {  
					MaxVolume = intent.getIntExtra("DMRMaxVolume", 0);
					Log.v(TAG,"---MaxVolume ="+MaxVolume+"---");
		          }
			}
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
    public class StopReceiver extends BroadcastReceiver{
		public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
			if(action.equals("com.rtk.dmr.stop.broadcast")) {
				sleepTime(5000);
				setStateStop();
				playing = false;
		    }
			else if(action.equals("com.rtk.dmr.finish.broadcast")){
				playing = false;
		    }
		}
	}
    public class NotifyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(action.equals("com.rtk.dmr.notifyup.broadcast")) {
				Log.i(TAG, "2---CurrentThreadId = " + Thread.currentThread().getId());
				notifyUp("Video");
			} else if(action.equals("com.rtk.dmr.notifyup.broadcast.photo")) {
				notifyUp("Photo");
			} else if(action.equals("com.rtk.dmr.notifyup.broadcast.music")){
				notifyUp("Music");
			}
		}
    	
    }
	public RtkDMRService() 
	{
	     Log.w(TAG, "DMRService in java is constructed!");
	}
	
	public static int GetFileType(){
		int filetype = getFileType();
		return filetype;
	}
	
	public static byte[] GetMediaInfo(){
		byte[] mediainfo = getMediaInfo();
		return mediainfo;
	}
	
	static
	{
		 System.loadLibrary("DMR");
	}
	private void sleepTime(int delay)
	{
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	   
	public void preParse()
	{
		 Log.i(TAG,"preParse in java test");
	}
	    
			
	public void loadMedia(char filename)
	{
		Log.i(TAG,"loadMedia in java test");
	}
		 		 		 	
	public void Play(String filename,int speed,int renderType,int inittime,boolean isSupport)
	{
	  Log.i(TAG,"\nPlay in java test, filename is "+filename+"\n speed is "+speed+" renderType is "+renderType+"isSupport=" +isSupport);	
	  Message msg = new Message();
	  msg.what = DMR_Play;
	  handler.sendMessage(msg);
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
			bundle.putInt("inittime", inittime);
			bundle.putInt("len", 1);
			bundle.putStringArray("filelist", pathList);
			bundle.putBoolean("isanywhere", true);//dmr or tvanywhere
			bundle.putInt("browserType", 1);
			bundle.putBoolean("isSupport",isSupport);
			if(!playing){
				String EXTRA_FULL_SCREEN = "android.intent.extra.force_fullscreen";
	            intent.putExtra(EXTRA_FULL_SCREEN, true);
			}
			
			intent.putExtras(bundle);
			intent.setComponent(componetName);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Log.i(TAG,"\nPlay in java test Afooo 1111");
			mContext.startActivity(intent);
			// wait Music play action done
			waitToNotify(10000, "Music");
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
			bundle.putInt("PLAYSPEED", speed);
			bundle.putBoolean("isSupport",isSupport);
			if(!playing){
				String EXTRA_FULL_SCREEN = "android.intent.extra.force_fullscreen";
	            intent.putExtra(EXTRA_FULL_SCREEN, true);
			}	
			intent.putExtras(bundle);
			intent.setComponent(componetName);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Log.i(TAG,"\nPlay in java test Afooo 1111");
			mContext.startActivity(intent);
			Log.i(TAG, "1---CurrentThreadId = " + Thread.currentThread().getId());
			waitToNotify(30000, "Video");
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
			bundle.putBoolean("isSupport",isSupport);
			if(!playing){
				String EXTRA_FULL_SCREEN = "android.intent.extra.force_fullscreen";
	            intent.putExtra(EXTRA_FULL_SCREEN, true);
			}
			
			intent.putExtras(bundle);
			intent.setComponent(componetName);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Log.i(TAG,"\nPlay in java test Afooo 1111");
			mContext.startActivity(intent);
			waitToNotify(2000, "Photo");
			Log.i(TAG,"\nPlay in java test Afooo 2222");
		 }   
	  playing = true;
    }
 	public void Stop()
 	{
 		Intent Stopintent = new Intent("com.android.DMRService.stop");
 		mContext.sendBroadcast(Stopintent);
 		sleepTime(500);
 		Log.i(TAG,"Stop in java test");
	}
 	public void Pause(boolean Pause){
 		Intent Pauseintent = new Intent("com.android.DMRService.pause");
 		Pauseintent.putExtra("pause", Pause);
        mContext.sendBroadcast(Pauseintent);
        sleepTime(500);
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
 		int isOn = 1;
 		/*try {
 				isOn = Settings.Global.getInt(mContext.getContentResolver(), Settings.Global.CEC_AV_SYSTEM_LINK);
 				Log.i(TAG,"Check HDMI-CEC !");
 				System.out.println("isOn ="+isOn);
         } catch (SettingNotFoundException e) {
              e.printStackTrace();
         }*/
 		if(isOn == 1)
 		{
 	    if(volume > MaxVolume){
 			AudioManager audioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
 	 		audioManager.setSuggestedStreamVolume(AudioManager.STREAM_MUSIC, MaxVolume, AudioManager.FLAG_SHOW_UI);
 		}else{
	 		AudioManager audioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
	 		audioManager.setSuggestedStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_SHOW_UI);
 		}
 			Log.i(TAG,"set volume done !");
 		}
 		Log.i(TAG,"\nSetVolume in java "+volume);
 	}	
 	public void SetMute(boolean mute)
 	{
 		mTV.setMute(!mTV.getMute());
 		Log.i(TAG,"\nSetMute in java "+ mute);
 	}	
 	public void GetVolume(int volume)
 	{
 		//mTV.getVolume();
 		Log.i(TAG,"\nSetVolume in java "+volume);
 	}
 	public void SetContrast(int contrast)
 	{
 		//mTV.setContrast(contrast);
 		Log.i(TAG,"\n SetContrast in java");
 	}
 	public void SetBrightness(int brightness)
 	{
 		//mTV.setBrightnessTsb(brightness);
 		Log.i(TAG,"\n SetBrightness in java");
 	}
 	public void GetBrightness(int brightness)
 	{
 		//mTV.getBrightness();
 		Log.i(TAG,"\n SetBrightness in java");
 	}
 	public int QueryForConnection()
 	{
 		Message msg = new Message();
		msg.what = DMR_Start;
		handler.sendMessage(msg);
 		int isOn = 0;
 		Log.i(TAG,"\n QueryForConnection in java");
 		System.out.println("isOn ="+isOn);
 		return isOn;
 	}
 	public void SeekMediaPosition(int position)
 	{
 		Log.i(TAG,"SeekMediaPosition enter position = "+position);
 		Intent Seekintent = new Intent("com.rtk.dmr.seek.broadcast");
 		int millisecends = position*1000;
 		Seekintent.putExtra("Seekpos",millisecends);
 		
		mContext.sendBroadcast(Seekintent);
 		Log.i(TAG,"SeekMediaPosition broadcast sent");
 		sleepTime(2000);
 		Log.i(TAG,"SeekMediaPosition leave");		
 	}
 	public int SetMediaPosition()
 	{
 		Log.i(TAG,"SetMediaPosition enter");
 		setSeekPosition(CurrentTime);
 		Log.i(TAG,"SetMediaPosition leave");
 		return 0;
 	}
	public void SetRate(int rate)
	{
		Log.i(TAG,"SetRate rate = "+rate);
		Intent SetRate = new Intent("com.rtk.dmr.setrate.broadcast");
		SetRate.putExtra("Rate",rate);		
		mContext.sendBroadcast(SetRate);
		sleepTime(500);
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
 	public int getSize(String path) throws java.net.MalformedURLException, java.net.ProtocolException
 	{
 		try{
	 		URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5 * 1000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty(
				"Accept",
				"image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
			conn.setRequestProperty("Accept-Language", "zh-CN");
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty(
				"User-Agent",
				"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
			conn.setRequestProperty("Connection", "Keep-Alive");
	
			PlaylistFileByteSize = conn.getContentLength();
			conn.disconnect();
			return PlaylistFileByteSize;
 		}catch(IOException e)
		{
			e.printStackTrace();
		}
 		return 0;
 	}
 	
 	public int getfilelen(String path)
 	{
 		try{
 			return getSize(path);
 		}catch (java.net.MalformedURLException e)
		{
			e.printStackTrace();
		}catch (java.net.ProtocolException e)
		{
			e.printStackTrace();
		}
 		
 		return 0;
 	}
 	public String Download(String path)
 	{
 		try{
 			return DownUtil(path);
 		}catch (java.net.MalformedURLException e)
		{
			e.printStackTrace();
		}catch (java.net.ProtocolException e)
		{
			e.printStackTrace();
		}
 		
 		return Error;
 	}
	public String DownUtil (String path) throws java.net.ProtocolException, java.net.MalformedURLException
 	{
 			
 		String URL = null;
 		try{
 			int length = 0;	
 			URL url = new URL(path);
 		    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
 		    conn.setConnectTimeout(5 * 1000);
 			conn.setRequestMethod("GET");
 			conn.setRequestProperty(
 						"Accept",
 						"image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
 			conn.setRequestProperty("Accept-Language", "zh-CN");
 			conn.setRequestProperty("Charset", "UTF-8");
 			InputStream inStream = conn.getInputStream();
 			inStream.skip(0);
 			System.out.print(inStream);
 			byte[] buffer = new byte[(int)PlaylistFileByteSize];
 		    int hasRead = 0;
 			while ( length < PlaylistFileByteSize
 					&& (hasRead = inStream.read(buffer)) != -1)
 				{
 					length += hasRead;
 				}
 			inStream.close();
 			tempbuffer = getChars(buffer);
 			PlaylistFileCharSize = tempbuffer.length;
 			URL = String.valueOf(tempbuffer);
 			URL = String.copyValueOf(tempbuffer);
 		}catch (IOException e)
		{
			e.printStackTrace();
		}
 		return URL;
 	 }
 		
 	private char[] getChars (byte[] bytes) {	
 	        Charset cs = Charset.forName ("UTF-8");
 	        ByteBuffer bb = ByteBuffer.allocate (bytes.length);
 	        bb.put (bytes);
 	        bb.flip ();
 	        CharBuffer cb = cs.decode (bb);
 	        return cb.array();
 		}

	private void waitToNotify(int delay, String type) {	//delay == 0, means wait till end
		synchronized (obj) {
			signalType = type;
			if(!flag) {
				try {
					flag = true;
					if(delay == 0) {
						obj.wait();
					} else {
						obj.wait(delay);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				flag = false;
			} else {
				Log.i(TAG, "This wait should never happen!");
			}
		}
	}
	
	private void notifyUp(String type) {
		synchronized (obj) {
			if(!type.equals(signalType)) {
				return ;
			}
			if(flag) {
				flag = false;
				obj.notify();
				signalType = "";
			} else {
				Log.i(TAG, "This notify should never happen!");
			}
		}
	}
}





