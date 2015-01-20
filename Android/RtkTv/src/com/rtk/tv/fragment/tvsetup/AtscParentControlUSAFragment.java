package com.rtk.tv.fragment.tvsetup;

import java.util.List;

import com.rtk.tv.R;
import com.rtk.tv.TvManagerHelper;
import com.rtk.tv.fragment.submenu.BaseMenuFragment;
import com.rtk.tv.fragment.submenu.item.MenuItem;
import com.rtk.tv.fragment.submenu.item.MenuItem.OnValueChangeListener;
import com.rtk.tv.utils.FragmentUtils;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

public class AtscParentControlUSAFragment extends BaseMenuFragment {
    
    private static final String TAG = "com.rtk.tv-AtscParentControlUSAFragment";
    
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
        
        final String[] usa_mpaa = getResources().getStringArray(R.array.atsc_pl_usa_mpaa);
        
        items.add(
                MenuItem.createTextItem(R.string.STRING_PL_USA_TV)
                .setOnClickListener(new OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                        FragmentUtils.showSubFragment(getBaseFragment(), AtscParentControlUSATVFragment.class);
                        
                    }
                }));
        
        items.add(
                MenuItem.createSpinnerItem(R.string.STRING_PL_USA_MPAA)
                .setSpinnerOptionsByArray(usa_mpaa)
                .setCurrentValue(tm.getPLUSAMPAA())
                .setOnValueChangeListener(new OnValueChangeListener() {
                    
                    @Override
                    public void onValueChanged(MenuItem item, int index) {
                        tm.setPLUSAMPAA(index);
                    }
                }));
    }

    @Override
    public int getTitle() {
        return R.string.STRING_PL_USA;
    }

}
