package com.rtk.tv.fragment.tvsetup;

import java.util.List;

import com.rtk.tv.R;
import com.rtk.tv.TvManagerHelper;
import com.rtk.tv.fragment.submenu.BaseMenuFragment;
import com.rtk.tv.fragment.submenu.item.MenuItem;
import com.rtk.tv.fragment.submenu.item.MenuItem.OnValueChangeListener;
import com.rtk.tv.fragment.submenu.item.SpinnerMenuItem;
import com.rtk.tv.utils.FragmentUtils;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;

public class AtscParentControlFragment extends BaseMenuFragment {
    
    private static final String TAG = "com.rtk.tv-AtscParentControlFragment";

    private MenuItem mItemSystemLock;
    private MenuItem mItemUSA;
    private MenuItem mItemCanada;
    private MenuItem mItemRRT;
    private MenuItem mItemResetRRT;
    private MenuItem mItemClearLock;
    
    @Override
    protected View onInflateLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_list, container, false);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.button_ok).setVisibility(View.GONE);
        view.findViewById(R.id.button_neutral).setVisibility(View.GONE);
        view.findViewById(R.id.button_cancel).setVisibility(View.GONE);
    }
    
    @Override
    public void onCreateMenuItems(List<MenuItem> items) {
        Log.d(TAG, "onCreateMenuItems");
        final TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
        
        final String[] system_lock = getResources().getStringArray(R.array.atsc_pl_off_on);

        items.add(
                MenuItem.createTextItem(R.string.STRING_PL_CHPASSWORD)
                .setOnClickListener(new OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                        FragmentUtils.showSubFragment(getBaseFragment(), AtscParentControlChangePasswordFragment.class);
                        
                    }
                })                    
            );
        
        mItemSystemLock=
                MenuItem.createSpinnerItem(R.string.STRING_PL_SYSTEMLOCK)
                .setSpinnerOptionsByArray(system_lock)
                .setCurrentValue(tm.getPLEnable() > 0 ? 1 : 0)
                .setOnValueChangeListener(new OnValueChangeListener() {
                    
                    @Override
                    public void onValueChanged(MenuItem item, int index) {
                        tm.setPLEnable(index > 0 ? true : false);
                        updateSettings(index > 0);
                    }
                });
        items.add(mItemSystemLock);

        mItemUSA = MenuItem.createTextItem(R.string.STRING_PL_USA)
                .setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                FragmentUtils.showSubFragment(getBaseFragment(), AtscParentControlUSAFragment.class);
                
            }
        });
        items.add(mItemUSA);

        mItemCanada=MenuItem.createTextItem(R.string.STRING_PL_CANADA)
        .setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                FragmentUtils.showSubFragment(getBaseFragment(), AtscParentControlCanadaFragment.class);
                
            }
        });
        items.add(mItemCanada);

        mItemRRT=MenuItem.createTextItem(R.string.STRING_PL_RRT_SETTING)
        .setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                FragmentUtils.showSubFragment(getBaseFragment(), AtscParentControlRRTFragment.class);
                
            }
        });
        items.add(mItemRRT);

        mItemResetRRT=MenuItem.createTextItem(R.string.STRING_PL_RESET_RRT)
        .setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                tm.resetPLRRT();
                
            }
        });
        items.add(mItemResetRRT);

        mItemClearLock=MenuItem.createTextItem(R.string.STRING_PL_CLEAR_LOCK)
        .setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                tm.clearPLLock();
                getListView().setSelection(0);
                ((SpinnerMenuItem)mItemSystemLock).setCurrentValue(tm.getPLEnable() > 0 ? 1 : 0, true);
                ((BaseAdapter)(getListView().getAdapter())).notifyDataSetChanged();
                
            }
        });
        items.add(mItemClearLock);
        
        updateSettings(tm.getPLEnable() > 0);
    }
    
    private void updateSettings(boolean value) {
        mItemUSA.setEnable(value);
        mItemCanada.setEnable(value);
        mItemRRT.setEnable(value);
        final TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
        if (tm.getPLRRTDimensions().size() == 0) 
            mItemRRT.setEnable(false);
        else
            mItemRRT.setEnable(value);
        mItemResetRRT.setEnable(value);
        mItemClearLock.setEnable(value);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) 
    {
        Log.d(TAG, "PL ONKEYDOWN");
        if (super.onKey(v, keyCode, event)) 
            return true;
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                Log.d(TAG, "PL ONKEYDOWN KEYCODE_BACK");
                FragmentUtils.popBackSubFragment(this.getFragmentManager(), AtscParentControlPasswordFragment.class);
                return true;
            default:
                return false;
        }
    };

    @Override
    public int getTitle() {
        return R.string.STRING_PARENTAL_CONTROL;
    }
}
