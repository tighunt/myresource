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

package com.rtk.mediabrowser;

import java.util.ArrayList;

import com.realtek.Utils.FileInfo;
import com.realtek.Utils.MimeTypes;

import android.app.Activity;
import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.media.MediaPlayer;
import android.util.DisplayMetrics;
import android.util.Log;
//import android.util.Log;

public class MediaApplication extends Application {
	final String TAG = "MediaApplication";
	public static final boolean DEBUG = false;
	public static final String internalStorage = "Internal Memory";
	private  int screenWidth;// px
	private  int screenHeight;// px
	private  float scale;// densityDpi/160
	
	enum ModifiedConf {
		SW960_SW540_ANYDENSITY;
	}
	
	ModifiedConf m_conf = ModifiedConf.SW960_SW540_ANYDENSITY;

	private  boolean is4k2k = false;
	private  boolean isx4k2k = false;
	private ArrayList<FileInfo> fileListItems =null;
	private int fileDirnum =0;
	
	private ArrayList<FileInfo> photoFileInfoList=null;

	private MediaPlayer mPlayer = null;
	
	private MimeTypes mMimeTypes = null;
	
	public String mBookMarkName = null;
	public BookMark mBookMark = null;
	
	public boolean isFromVideoPlayer = false;
	public String mStopedFileName = null;
	
	
	public static final int MAXFILENUM = 4096;
	
	@Override
	public void onCreate() {
		super.onCreate();
		ensureConfiguration(getResources().getConfiguration());
		
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
		fileListItems = new ArrayList<FileInfo>();
		photoFileInfoList = new ArrayList<FileInfo>();
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
	
	public ArrayList<FileInfo> getFileListItems() {
		return fileListItems;
	}

	public int getFileDirnum() {
		return fileDirnum;
	}

	public void setFileDirnum(int fileDirnum) {
		this.fileDirnum = fileDirnum;
	}
	
	//photo part
	public ArrayList<FileInfo> getPhotoFileInfoList()
	{
		return this.photoFileInfoList;
	}
	
	public MediaPlayer getMediaPlayer()
	{
		if (mPlayer == null)
			mPlayer = new MediaPlayer();
		
		return mPlayer;
	}
	
	public void releaseMediaPlayer()
	{
		if (mPlayer != null)
		{
			mPlayer.reset();
			mPlayer.release();
			mPlayer = null;
			System.gc();
		}
	}
	
	public MimeTypes getMimeTypes()
	{
		if (mMimeTypes == null)
		{
			XmlResourceParser mMimeTypeXml = getResources().getXml(R.xml.mimetypes);
			mMimeTypes = Util.GetMimeTypes(mMimeTypeXml);
		}
		
        return mMimeTypes;
	}
	
	public BookMark getBookMark(String name)
	{
		mBookMarkName = name;
		
		if (mBookMark == null || mBookMarkName == null || mBookMarkName.compareTo(name) != 0)
			mBookMark = new BookMark(mBookMarkName);
		
		return mBookMark;
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
	
	//code from TV
	private void ensureConfiguration(Configuration newConfig) {
		Resources r = getResources();
		DisplayMetrics metrics = r.getDisplayMetrics();
		final int width = metrics.widthPixels;
		final int height = metrics.heightPixels;
		
		Configuration conf = null;
		// 720p
		if (width == 1280 && height == 720) {
			conf = new Configuration();
			conf.densityDpi = DisplayMetrics.DENSITY_TV;
			conf.screenWidthDp = 960;
			conf.screenHeightDp = 540;
			conf.smallestScreenWidthDp = 540;
			metrics.densityDpi = DisplayMetrics.DENSITY_TV;
			metrics.xdpi = DisplayMetrics.DENSITY_TV;
			metrics.ydpi = DisplayMetrics.DENSITY_TV;
			m_conf = ModifiedConf.SW960_SW540_ANYDENSITY;
		// 1080p
		} else if (width == 1920 && height == 1080) {
			conf = new Configuration();
			conf.densityDpi = DisplayMetrics.DENSITY_XHIGH;
			conf.screenWidthDp = 960;
			conf.screenHeightDp = 540;
			conf.smallestScreenWidthDp = 540;
			metrics.densityDpi = DisplayMetrics.DENSITY_XHIGH;
			metrics.xdpi = DisplayMetrics.DENSITY_XHIGH;
			metrics.ydpi = DisplayMetrics.DENSITY_XHIGH;
			m_conf = ModifiedConf.SW960_SW540_ANYDENSITY;
		// 4K
		} else if (width == 3840 && height == 2160) {
			conf = new Configuration();
			conf.densityDpi = DisplayMetrics.DENSITY_XXXHIGH;
			conf.screenWidthDp = 960;
			conf.screenHeightDp = 540;
			conf.smallestScreenWidthDp = 540;
			metrics.densityDpi = DisplayMetrics.DENSITY_XXXHIGH;
			metrics.xdpi = DisplayMetrics.DENSITY_XXXHIGH;
			metrics.ydpi = DisplayMetrics.DENSITY_XXXHIGH;
			m_conf = ModifiedConf.SW960_SW540_ANYDENSITY;
		}
		if (conf != null) {
			int change = newConfig.updateFrom(conf);
			if (change != 0) {
				r.updateConfiguration(newConfig, metrics);
				// Log new configurations
				conf = r.getConfiguration();
				metrics = r.getDisplayMetrics();
				Log.d(TAG, "Configuration updated: conf = " + conf.toString() +
						", metrics = " + metrics.toString());
			}
			
		}
	}
	
	ModifiedConf getModifiedConf() {
		return m_conf;
	}
}