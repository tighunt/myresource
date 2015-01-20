package com.rtk.tv;

import com.rtk.tv.service.ServiceManager;
import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;

public class TvApplication extends Application {
	
	public static final String TAG = "TvApplication";
	
	//private TvDebugBroadcastReceiver mDebugReceiver = new TvDebugBroadcastReceiver();

	// //
	public static ServiceManager mServiceManager = null;
	// //
	@Override
	public void onCreate() {
		super.onCreate();
		mServiceManager = new ServiceManager(this);
		// Make sure we have the correct DPI configuration
		ensureConfiguration(getResources().getConfiguration());
		
		
		// Setup timer on first boot.
		/*if (tm.getIsFirstRunTVAPK()) {
			MediaManager mm = MediaManager.getInstance(this);
			mm.resetAlarms();
		}*/
		
		// Set the flag in TvManagerService to indicate that the TV app has been launched.
		// Launcher would determine whether to start TV or not by this flag.
		//tm.setIsFirstRunTVAPK(false);
		
		//
		//mDebugReceiver.register(this);
	}

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
}
