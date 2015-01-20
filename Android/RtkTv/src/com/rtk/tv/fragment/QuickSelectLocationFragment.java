package com.rtk.tv.fragment;


import com.rtk.tv.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;


public class QuickSelectLocationFragment extends BaseFragment implements OnClickListener {
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_fragment_quick_select_location, container, false);
        v.findViewById(R.id.button_store).setOnClickListener(this);
        v.findViewById(R.id.button_home).setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_store:
                
                break;
            case R.id.button_home:
               
                break;
            default:
                break;
        }
        /*
        int source = tm.getInputSource();
        if (TvManager.isAtvSource(source)) {
            FragmentUtils.showSubFragment(this, QuickSelectAtvTableScanFragment.class);
        } else if (TvManager.isDtvSource(source)) {
            FragmentUtils.showSubFragment(this, DtvAutoTuningSettingFragment.class);
        } else {
        	FragmentUtils.showSubFragment(this, AutoTuningFragment.class);
        }*/
    }

}
