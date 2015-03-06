package com.android.settings.net;

import android.content.ContentResolver;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.CheckBox;


public class LocationSetting {
	private static final String TAG = "LocationSetting==>";
	// /ui widget
	private NetSettingViewHolder mViewHolder;
	// of activity
	private NetSettingActivity mActivity;
	
	private CheckBox mNetLocCheckbox;
	
	public LocationSetting(NetSettingViewHolder viewHolder,
			NetSettingActivity activity) {
		this.mViewHolder = viewHolder;
		this.mActivity = activity;
		
		findViews();
	}
	
	private void findViews() {
		mNetLocCheckbox = mViewHolder.mLocServiceSwitch;
	}
	
	public void initLocationUI(){
		ContentResolver res = mActivity.getContentResolver();
		mNetLocCheckbox.setChecked(Settings.Secure.isLocationProviderEnabled(
                res, LocationManager.NETWORK_PROVIDER));
	}
	
	public boolean setNetLoc(boolean isOpen){
		ContentResolver res = mActivity.getContentResolver();
		Log.d(TAG,"isOpen="+isOpen);
		Settings.Secure.setLocationProviderEnabled(res,
                 LocationManager.NETWORK_PROVIDER,isOpen);
		 return true;
	}
}
