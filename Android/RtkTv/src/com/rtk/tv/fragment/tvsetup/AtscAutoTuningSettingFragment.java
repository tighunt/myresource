
package com.rtk.tv.fragment.tvsetup;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import com.rtk.tv.R;
import com.rtk.tv.TvManagerHelper;
import com.rtk.tv.fragment.submenu.BaseMenuFragment;
import com.rtk.tv.fragment.submenu.item.MenuItem;
import com.rtk.tv.fragment.submenu.item.MenuItem.OnValueChangeListener;
import com.rtk.tv.utils.FragmentUtils;

public class AtscAutoTuningSettingFragment extends BaseMenuFragment implements OnClickListener {
    
    private static final String TAG = "com.rtk.tv-AtscAutoTuningSettingFragment";
    
    private static final int[] VALUES_CABLE_SYSTEM = {
    	0,1,2,3
    	/*
        TvManagerHelper.ATSC_STD, 
        TvManagerHelper.ATSC_IRC, 
        TvManagerHelper.ATSC_HRC,
        TvManagerHelper.ATSC_AUTO*/
        };
    
	@Override
	protected View onInflateLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.dialog_list, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
	    Log.d(TAG, "onViewCreated");
		super.onViewCreated(view, savedInstanceState);
		Button buttonOk = (Button) view.findViewById(R.id.button_ok);
		buttonOk.setOnClickListener(this);
		view.findViewById(R.id.button_neutral).setVisibility(View.GONE);
		view.findViewById(R.id.button_cancel).setVisibility(View.GONE);
	}

	@Override
	public void onCreateMenuItems(List<MenuItem> items) {
	    Log.d(TAG, "onCreateMenuItems");
	    final TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
	    final String[] cable_system = getResources().getStringArray(R.array.atsc_cable_system);        
	    
	    Log.d(TAG, "before getAtscInput");
		if(tm.getAtscInput() == tm.ATSC_AIR)
		{
            items.add(
                    MenuItem.createSpinnerItem(R.string.cable_system)
                    .setSpinnerOptionsByArray(cable_system)
                    .setCurrentValue(3)
                    .setEnable(false)

                 );
		}
		else
		{    		
		    int cableTypeIdx = indexOf(VALUES_CABLE_SYSTEM, tm.getAtscCableSystem());
    		items.add(
    				MenuItem.createSpinnerItem(R.string.cable_system)
    				.setSpinnerOptionsByArray(cable_system)
    				.setCurrentValue(cableTypeIdx)
    				.setOnValueChangeListener(new OnValueChangeListener() {    					
    					@Override
    					public void onValueChanged(MenuItem item, int index) {
    						tm.setAtscCableSystem(VALUES_CABLE_SYSTEM[index]);
    					}
    				}));
		}
	}

	@Override
	public int getTitle() {
		return R.string.STRING_AUTO_TUNING;
	}

	@Override
	public void onClick(View view) {
		FragmentUtils.showSubFragment(this, AtscAutoTuningFragment.class);
	}

}
