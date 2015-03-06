package com.android.settings.other;

import com.android.settings.R;

import android.app.Activity;
import android.os.Bundle;


public class DesktopSettingActivity extends Activity  {
	
	private DesktopSettingViewHolder mHolder;
	private DesktopSettingListener mListener;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_desktopsetting_mix);
        
        
        mHolder = new DesktopSettingViewHolder(DesktopSettingActivity.this);
        mListener = new DesktopSettingListener(DesktopSettingActivity.this, mHolder);
        if (mListener == null){
        	
        }
        
    }
	
}
