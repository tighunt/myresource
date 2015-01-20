package com.rtk.tv.fragment.submenu;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import com.rtk.tv.R;
import com.rtk.tv.TvManagerHelper;
import com.rtk.tv.fragment.submenu.item.MenuItem;
import com.rtk.tv.fragment.submenu.item.MenuItem.OnValueChangeListener;
import com.rtk.tv.fragment.tvsetup.AtscAutoTuningSettingFragment;
import com.rtk.tv.fragment.tvsetup.AtscCCFragment;
import com.rtk.tv.fragment.tvsetup.AtscParentControlPasswordFragment;
import com.rtk.tv.fragment.tvsetup.AtvManualTuningFragment;
import com.rtk.tv.fragment.tvsetup.ConfirmDialogFragment;
import com.rtk.tv.fragment.tvsetup.DtvManualTuningResultFragment;
import com.rtk.tv.fragment.tvsetup.ResetTvFragment;
import com.rtk.tv.fragment.tvsetup.SystemInfoFragment;
import com.rtk.tv.utils.Constants;
import com.rtk.tv.utils.FragmentUtils;
import com.rtk.tv.utils.TvConfig;

import java.util.List;

public class SetupMenuFragment extends BaseMenuFragment {
    
    private static final String TAG = "com.rtk.tv-SetupMenuFragment";

	private static final int[] VALUES_COUNTRY = {
			Constants.COUNTRY_BAHRAIN,
			Constants.COUNTRY_INDIA,
			Constants.COUNTRY_INDONESIA,
			Constants.COUNTRY_ISRAEL,
			Constants.COUNTRY_KUWAIT,
			Constants.COUNTRY_MALAYSIA,
			Constants.COUNTRY_PHILIPPINES,
			Constants.COUNTRY_QATAR,
			Constants.COUNTRY_SAUDI_ARABIA,
			Constants.COUNTRY_SINGAPORE,
			Constants.COUNTRY_THAILAND,
			Constants.COUNTRY_UNITED_ARAB_EMIRATES,
			Constants.COUNTRY_VIETNAM };
	
	private static final int[] VALUES_TV_LOCATION = {
		Constants.TV_LOCATION_HOME, Constants.TV_LOCATION_STORE };
	
	private static final int[] VALUES_TV_INPUT = {
		Constants.ATSC_AIR, Constants.ATSC_CABLE };
	
	@Override
	public void onCreateMenuItems(List<MenuItem> items) {	
		final TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
		if(TvConfig.isATSC)
		{
		    // Air/Cable: [Air, Cable]
	        final String[] inputArray = getResources().getStringArray(R.array.atsc_input);
	        int inputIdx = indexOf(VALUES_TV_INPUT, tm.getAtscInput());
	        items.add(
	            MenuItem.createSpinnerItem(R.string.air_cable)
	            .setSpinnerOptionsByArray(inputArray)
	            .setCurrentPosition(inputIdx)
	            .setOnValueChangeListener(new OnValueChangeListener() {
                
                @Override
                public void onValueChanged(MenuItem item, int value) {
                    tm.setAtscInput(VALUES_TV_INPUT[value]);
                }
            })
	        );
	        
	        // Auto Tuning
            items.add(
                MenuItem.createTextItem(R.string.STRING_AUTO_TUNING)
                .setOnClickListener(new OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                            ConfirmDialogFragment.show(getBaseFragment(), AtscAutoTuningSettingFragment.class,
                                    null, R.string.STRING_AUTO_TUNING, R.string.STRING_QUICKSETUP_INFO);
                        
                    }
                })
            );
            
            // Closed Caption
            items.add(
                MenuItem.createTextItem(R.string.string_closed_caption)
                .setOnClickListener(new OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                        FragmentUtils.showSubFragment(getBaseFragment(), AtscCCFragment.class);
                        
                    }
                })                    
            );
            
            // Parent Control
            items.add(
                MenuItem.createTextItem(R.string.STRING_PARENTAL_CONTROL)
                .setOnClickListener(new OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                        FragmentUtils.showSubFragment(getBaseFragment(), AtscParentControlPasswordFragment.class);
                    }
                })                    
            );
		    
		}
		else
		{		
    		// Auto Tuning
    		items.add(
    			MenuItem.createTextItem(R.string.STRING_AUTO_TUNING)
    			.setOnClickListener(new OnClickListener() {
    				
    				@Override
    				public void onClick(View v) {
    					
    				}
    			})
    			.setEnable(true)
    		);
    
    		// Manual Tuning ATV
    		items.add(
    			MenuItem.createTextItem(R.string.STRING_ATV_MANUAL_TUNING)
    			.setOnClickListener(new OnClickListener() {
    				
    				@Override
    				public void onClick(View v) {
    				    FragmentUtils.showSubFragment(getBaseFragment(), AtvManualTuningFragment.class);
    				}
    			})
    			.setEnable(true)
    		);
    
    		// Manual Tuning DTV
    		items.add(
    			MenuItem.createTextItem(R.string.dtv_channel_manager)
    			.setOnClickListener(new OnClickListener() {
    				
    				@Override
    				public void onClick(View v) {
    					FragmentUtils.showSubFragment(getBaseFragment(), DtvManualTuningResultFragment.class);
    				}
    			})
    			.setEnable(true)
    		);
		}

		// Quick Setup 
		items.add(
			MenuItem.createTextItem(R.string.STRING_QUICK_SETUP)
			.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
				}
			})
		);
	
		// Location: [Home, Store]
		final String[] locations = getResources().getStringArray(R.array.setup_option_location);
		int locationIdx = 0;/*indexOf(VALUES_TV_LOCATION, tm.getTvLocation());*/
		items.add(
			MenuItem.createSpinnerItem(R.string.STRING_LOCATION)
			.setSpinnerOptionsByArray(locations)
			.setCurrentPosition(locationIdx)
			.setOnValueChangeListener(new OnValueChangeListener() {
				
				@Override
				public void onValueChanged(MenuItem item, int value) {
					tm.setTvLocation(VALUES_TV_LOCATION[value]);
				}
			})
		);	

		// System Information
		items.add(
			MenuItem.createTextItem(R.string.STRING_SYSTEM_INFO)
			.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					FragmentUtils.showSubFragment(getBaseFragment(), SystemInfoFragment.class);
				}
			})
		);
		
		// Check Update
		items.add(
			MenuItem.createTextItem(R.string.settings_check_update)
			.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//Intent intent = new Intent(getActivity(), UpdateActivity.class);
					//startActivity(intent);
				}
			})
		);

		// RTK CI
		items.add(
			MenuItem.createTextItem(R.string.settings_rtk_ci)
			.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Activity a = getActivity();
					Intent intent = a.getPackageManager()
						.getLaunchIntentForPackage("com.rtk.ci");
					if (intent != null) {
						a.startActivity(intent);
					}
					getFragmentManager().popBackStack();
				}
			})
		);

		
		// Reset
		items.add(
			MenuItem.createTextItem(R.string.STRING_RESET_TV)
			.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
				    FragmentUtils.showSubFragment(getBaseFragment(), ResetTvFragment.class);
				}
			})
		);
	}

	@Override
	public int getTitle() {
		return R.string.STRING_SETUP;
	}

}
