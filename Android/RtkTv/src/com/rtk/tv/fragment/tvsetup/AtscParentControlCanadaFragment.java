package com.rtk.tv.fragment.tvsetup;

import java.util.List;

import com.rtk.tv.R;
import com.rtk.tv.TvManagerHelper;
import com.rtk.tv.fragment.submenu.BaseMenuFragment;
import com.rtk.tv.fragment.submenu.item.MenuItem;
import com.rtk.tv.fragment.submenu.item.MenuItem.OnValueChangeListener;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AtscParentControlCanadaFragment extends BaseMenuFragment {
    
    private static final String TAG = "com.rtk.tv-AtscParentControlCanadaFragment";
    
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
        
        final String[] canada_english = getResources().getStringArray(R.array.atsc_pl_canada_english);
        final String[] canada_french = getResources().getStringArray(R.array.atsc_pl_canada_french);
        
        items.add(
                MenuItem.createSpinnerItem(R.string.STRING_PL_CANADA_ENGLISH)
                .setSpinnerOptionsByArray(canada_english)
                .setCurrentValue(tm.getPLCanadaEnglish())
                .setOnValueChangeListener(new OnValueChangeListener() {
                    
                    @Override
                    public void onValueChanged(MenuItem item, int index) {
                        tm.setPLCanadaEnglish(index);
                    }
                }));
        
        items.add(
                MenuItem.createSpinnerItem(R.string.STRING_PL_CANADA_FRENCH)
                .setSpinnerOptionsByArray(canada_french)
                .setCurrentValue(tm.getPLCanadaFrench())
                .setOnValueChangeListener(new OnValueChangeListener() {
                    
                    @Override
                    public void onValueChanged(MenuItem item, int index) {
                        tm.setPLCanadaFrench(index);
                    }
                }));
    }

    @Override
    public int getTitle() {
        return R.string.STRING_PL_CANADA;
    }

}
