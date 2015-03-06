/*
 * Copyright (C) 
 *
 * 
 * 
 * 
 *
 * 
 *
 * 
 * 
 * 
 * 
 * 
 */

package com.realtek.dmr;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observer;

import com.realtek.dmr.util.DLNAFileInfo;

import android.app.Activity;
import android.app.Application;
import android.content.res.XmlResourceParser;
import android.media.MediaPlayer;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
//import android.util.Log;

public class MediaApplication extends Application {
	public String tag = "MediaApplication";
	public static final boolean DEBUG = false;
	public static final String internalStorage = "Internal Memory";
	private  int screenWidth;// px
	private  int screenHeight;// px
	private  float scale;// densityDpi/160

	private  boolean is4k2k = false;
	private  boolean isx4k2k = false;
	private ArrayList<DLNAFileInfo> fileList =null;
	private ArrayList<DLNAFileInfo> playList =null;
	private int fileDirnum =0;//it is only for audio,do not use for other

	private MediaPlayer mPlayer = null;
	
	//private MimeTypes mMimeTypes = null;
	
	public String mBookMarkName = null;
	//public BookMark mBookMark = null;
	
	public boolean isFromVideoPlayer = false;
	public String mStopedFileName = null;
	
	public String subRootPath = null;
	public String mediaServerName = null;
	
	public static final int MAXFILENUM = 4096;
	private List<Activity> activityList = new LinkedList<Activity>(); 
	@Override
	public void onCreate() {
		super.onCreate();
		DisplayMetrics dm = new DisplayMetrics();
		dm = getResources().getDisplayMetrics();
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
		scale = dm.density;
		if(DEBUG)  Log.e("", "scale =" + scale);
		if (screenWidth >= 3840 || screenHeight >= 3840)
			is4k2k = true;
		if (scale >= 3)
			isx4k2k = true;
		fileList = new ArrayList<DLNAFileInfo>();
	}

	public  int getScreenWidth() {
		return screenWidth;
	}

	public  int getScreenHeight() {
		return screenHeight;
	}

	public  float getScale() {
		return scale;
	}

	public  boolean isIs4k2k() {
		return is4k2k;
	}

	public  boolean isIsx4k2k() {
		return isx4k2k;
	}
	
	public ArrayList<DLNAFileInfo> getFileList() {
		return fileList;
	}
	

	public int getFileDirnum() {
		return fileDirnum;
	}
	
	public String getSubRootPath() {
		return subRootPath;
	}
	
	public void setSubRootPath(String path) {
		subRootPath = path;
	}
	
	public String getMediaServerName() {
		return mediaServerName;
	}
	
	public void setMediaServerName(String name) {
		mediaServerName = name;
	}

	public void setFileDirnum(int fileDirnum) {
		this.fileDirnum = fileDirnum;
	}
	
	
	public MediaPlayer getMediaPlayer()
	{
		if (mPlayer == null) {
			mPlayer = new MediaPlayer();
			Log.i(tag, "Create MediaPlayer!");
		}
		
		return mPlayer;
	}
	
	public void releaseMediaPlayer()
	{
		if (mPlayer != null)
		{
			mPlayer.release();
			mPlayer = null;
			System.gc();
		}
	}
	
	public void setMediaPlayerNull() {
		if(mPlayer != null) {
			mPlayer = null;
			System.gc();
		}
	}
	


	
	public boolean getIsFromVideoPlayer()
	{
		return isFromVideoPlayer;
	}
	
	public void setIsFromVideoPlayer(boolean isVideoPlayer)
	{
		this.isFromVideoPlayer = isVideoPlayer;
	}
	
	public String getStopedFileName()
	{
		return mStopedFileName;
	}
	
	public void setStopedFileName(String FileName)
	{
		this.mStopedFileName = FileName;
	}

	
	public ArrayList<DLNAFileInfo> getPlayList() {
		return playList;
	}

	public void setPlayList(ArrayList<DLNAFileInfo> playList) {
		this.playList = playList;
	}
	
    public void addActivity(Activity activity) {  
        activityList.add(activity);  
    }  
   
    public void exit() {  
        for (Activity activity : activityList) { 
        	if(activity!=null)
        		activity.finish();  
        }
        System.exit(0);
    } 
}