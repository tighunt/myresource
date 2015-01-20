package com.rtk.tv;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class TvPreference {
    
//    private static final String TAG = "TvPreference";
    private static TvPreference sInstance;
    
    private final SharedPreferences mSharedPreferences;
    
    public static TvPreference getInstance(Context vContext) {
        if (sInstance == null) {
            sInstance = new TvPreference(vContext);
        }
        return sInstance;
    }

    private TvPreference(Context vContext) {
        mSharedPreferences = vContext.getSharedPreferences("RTK_PRIVATE", Activity.MODE_PRIVATE);
    }

    public SharedPreferences getPerference() {
        return mSharedPreferences;
    }

    public void cleanAll() {
        SharedPreferences.Editor et = mSharedPreferences.edit();
        et.clear();
        et.commit();
    }
    ////
    public boolean isParentalProtect()
    {
        boolean ret = false;
        if(	GetParentCtrl_OnOff() &&
                (GetPrivate_Password("PARENT_PASSWORD") !=null)
                )
            ret = true;
        return ret;
    }
    ////
    public boolean isFirstTimeTvApk() {
        return mSharedPreferences.getBoolean("RTKTV_FIRST_TIME", true);
    }
    
    public void setFirstTimeTvApk(boolean data) {
        mSharedPreferences.edit()
        .putBoolean("RTKTV_FIRST_TIME", data)
        .commit();
    }
    ////
    ////
    public boolean GetParentCtrl_OnOff()
    {
        if(mSharedPreferences == null)	return false;
        return mSharedPreferences.getBoolean("PARENT_ONOFF",false);
    }
    synchronized public void SetParentCtrl_OnOff(boolean data)
    {
        if(mSharedPreferences == null)	return;
        SharedPreferences.Editor et = mSharedPreferences.edit();
        et.putBoolean("PARENT_ONOFF",data);
        et.commit();
        et = null;
    }
    ////
    public String GetPrivate_Password(String key)
    {
        if(mSharedPreferences == null)	return null;
        return mSharedPreferences.getString(key,null);
    }
    synchronized public void SetPrivate_Password(String key,String data)
    {
        if(mSharedPreferences == null)	return;
        SharedPreferences.Editor et = mSharedPreferences.edit();
        //"PARENT_PASSWORD"
        et.putString(key,data);
        et.commit();
        et = null;
    }
    ////
    public int GetOsd_ExhibitionTime()
    {
        if(mSharedPreferences == null)	return 600000;
        return mSharedPreferences.getInt("osd_exhibition",600000);
    }
    synchronized public void SetOsd_ExhibitionTime(int data)
    {
        if(mSharedPreferences == null)	return;
        SharedPreferences.Editor et = mSharedPreferences.edit();
        et.putInt("osd_exhibition",data);
        et.commit();
        et = null;
    }
    ////
    public int GetSleep_TimeR()
    {
        if(mSharedPreferences == null)	return -1;
        return mSharedPreferences.getInt("rtk_sleep",0);
    }
    synchronized public void SetSleep_TimeR(int data)
    {
        if(mSharedPreferences == null)	return;
        SharedPreferences.Editor et = mSharedPreferences.edit();
        et.putInt("rtk_sleep",data);
        et.commit();
        et = null;
    }
    ////
    public long GetOnTimer_TimeR()
    {
        if(mSharedPreferences == null)	return -1;
        return mSharedPreferences.getLong("rtk_ontimer",0);
    }
    synchronized public void SetOnTimer_TimeR(long data)
    {
        if(mSharedPreferences == null)	return;
        SharedPreferences.Editor et = mSharedPreferences.edit();
        et.putLong("rtk_ontimer",data);
        et.commit();
        et = null;
    }

	public void setNewNit() {
		mSharedPreferences.edit().putBoolean("got_new_nit", true).commit();
	}

	public boolean checkNewNit(boolean consume) {
		boolean b = mSharedPreferences.getBoolean("got_new_nit", false);
		if (b && consume) {
			mSharedPreferences.edit().putBoolean("got_new_nit", false).commit();
		}
		return b;
	}
}

