package com.rtk.tv.fragment.tvsetup;

import java.util.ArrayList;
import java.util.List;

import com.rtk.tv.R;
import com.rtk.tv.TvManagerHelper;
import com.rtk.tv.fragment.submenu.BaseMenuFragment;
import com.rtk.tv.fragment.submenu.item.MenuItem;
import com.rtk.tv.utils.FragmentUtils;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

public class AtscParentControlRRTFragment extends BaseMenuFragment {
    
    private static final String TAG = "com.rtk.tv-AtscParentControlRRTFragment";
    
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
        
        final ArrayList<String> dimensions = tm.getPLRRTDimensions();
        
        for(int i = 0; i < dimensions.size(); i++){
            final int index = i;
            items.add(
                    MenuItem.createTextItem(dimensions.get(i))
                    .setOnClickListener(new OnClickListener() {
                        
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putInt("Dimension", index);
                            bundle.putString("DimensionName", dimensions.get(index));
                            FragmentUtils.showSubFragment(getBaseFragment(), AtscParentControlRRTDimensionFragment.class,bundle);
                            
                        }
                    }));
        }
    }

    @Override
    public int getTitle() {
        return R.string.STRING_PL_RRT_SETTING;
    }

}
