
package com.rtk.tv.fragment.tvsetup;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import com.rtk.tv.R;
import com.rtk.tv.TvManagerHelper;
import com.rtk.tv.fragment.submenu.BaseMenuFragment;
import com.rtk.tv.fragment.submenu.item.MenuItem;
import com.rtk.tv.fragment.submenu.item.MenuItem.OnValueChangeListener;
import com.rtk.tv.utils.FragmentUtils;

public class AtscCCFragment extends BaseMenuFragment{
    
    private static final String TAG = "com.rtk.tv-AtscCCFragment";
    
    private static final int[] VALUES_CC_MODE = {
        TvManagerHelper.CC_Mode_on, 
        TvManagerHelper.CC_Mode_off, 
        TvManagerHelper.CC_on_Mute
    };
    
    private static final int[] VALUES_CC_BASIC_SELECTION = {
        TvManagerHelper.CC_basic_CC1, 
        TvManagerHelper.CC_basic_CC2, 
        TvManagerHelper.CC_basic_CC3,
        TvManagerHelper.CC_basic_CC4,
        TvManagerHelper.CC_basic_Text1,
        TvManagerHelper.CC_basic_Text2,
        TvManagerHelper.CC_basic_Text3,
        TvManagerHelper.CC_basic_Text4
    };
    
    private ArrayList<Integer> VALUES_CC_SERVICE_LIST = new ArrayList<Integer>();
    private ArrayList<String> string_CC_SERVICE_LIST = new ArrayList<String>();
    
    private void prepareServiceList(TvManagerHelper tm)
    {
        String tmp = tm.getAtscCCServiceList();
        int arrayLen = (tmp.length() < TvManagerHelper.CC_SERVICE_TOTAL)? tmp.length():TvManagerHelper.CC_SERVICE_TOTAL;
        VALUES_CC_SERVICE_LIST.add(0);
        string_CC_SERVICE_LIST.add(getResources().getString(R.string.string_cc_service_none));
        for(int i=0;i<arrayLen;i++)
        {
            if(tmp.charAt(i)=='1')
            {
                VALUES_CC_SERVICE_LIST.add(i);
                string_CC_SERVICE_LIST.add(getResources().getString(R.string.string_cc_service)+i);
            }
        }
    }
    
	@Override
	protected View onInflateLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.dialog_list, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
	    Log.d(TAG, "onViewCreated");
		super.onViewCreated(view, savedInstanceState);
		Button buttonOk = (Button) view.findViewById(R.id.button_ok);
		buttonOk.setVisibility(View.GONE);
		view.findViewById(R.id.button_neutral).setVisibility(View.GONE);
		view.findViewById(R.id.button_cancel).setVisibility(View.GONE);
	}

	@Override
	public void onCreateMenuItems(List<MenuItem> items) {
	    Log.d(TAG, "onCreateMenuItems");
	    final TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
	    final String[] cc_mode = getResources().getStringArray(R.array.atsc_cc_mode);
	    final String[] cc_basic_selection = getResources().getStringArray(R.array.atsc_cc_basic_selection);	    
	    prepareServiceList(tm);
	    final String[] cc_advanced_selection = (String[])(string_CC_SERVICE_LIST.toArray(new String[0]));
	    
        items.add(
                MenuItem.createSpinnerItem(R.string.string_cc_mode)
                .setSpinnerOptionsByArray(cc_mode)
                .setCurrentValue(indexOf(VALUES_CC_MODE, tm.getAtscCCMode()))
                .setOnValueChangeListener(new OnValueChangeListener() {
                    
                    @Override
                    public void onValueChanged(MenuItem item, int index) {
                        tm.setAtscCCMode(VALUES_CC_MODE[index]);
                    }
                }));
        
        items.add(
                MenuItem.createSpinnerItem(R.string.string_basic_selection)
                .setSpinnerOptionsByArray(cc_basic_selection)
                .setCurrentValue(indexOf(VALUES_CC_BASIC_SELECTION, tm.getAtscCCBasicSelection()))
                .setOnValueChangeListener(new OnValueChangeListener() {
                    
                    @Override
                    public void onValueChanged(MenuItem item, int index) {
                        tm.setAtscCCBasicSelection(VALUES_CC_BASIC_SELECTION[index]);
                    }
                }));
        
        //service
        Log.d(TAG, "current service:" + tm.getAtscCCAdvancedSelection());
        int curService = tm.getAtscCCAdvancedSelection();
        int curServiceSelectVal = (VALUES_CC_SERVICE_LIST.indexOf(curService)==-1)?0:VALUES_CC_SERVICE_LIST.indexOf(curService);
        
        items.add(
                MenuItem.createSpinnerItem(R.string.string_advanced_selection)
                .setSpinnerOptionsByArray(cc_advanced_selection)
                .setCurrentValue(curServiceSelectVal)
                .setOnValueChangeListener(new OnValueChangeListener() {
                    
                    @Override
                    public void onValueChanged(MenuItem item, int index) {
                        tm.setAtscCCAdvancedSelection(VALUES_CC_SERVICE_LIST.get(index));
                    }
                })); 
        items.add(
                MenuItem.createTextItem(R.string.string_option)
                .setOnClickListener(new OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                        FragmentUtils.showSubFragment(getBaseFragment(), AtscCCOptionFragment.class);
                        
                    }
                })                    
            );

	}

	@Override
	public int getTitle() {
		return R.string.string_closed_caption;
	}

}
