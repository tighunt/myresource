package com.rtk.tv.fragment.tvsetup;

import java.util.ArrayList;
import java.util.List;

import com.rtk.tv.R;
import com.rtk.tv.TvManagerHelper;
import com.rtk.tv.fragment.submenu.BaseMenuFragment;
import com.rtk.tv.fragment.submenu.item.MenuItem;
import com.rtk.tv.fragment.submenu.item.MenuItem.OnValueChangeListener;
import com.rtk.tv.fragment.submenu.item.SpinnerMenuItem;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AtscParentControlRRTDimensionFragment extends BaseMenuFragment {
    
    private static final String TAG = "com.rtk.tv-AtscParentControlRRTDimensionFragment";
    private int dimension = 0;
    private ArrayList<String> dimensionItems;
    final TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
    
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
        
        final String[] on_off = getResources().getStringArray(R.array.atsc_pl_off_on);

        dimension = getArguments().getInt("Dimension");
        dimensionItems = tm.getPLRRTDimensionItems(dimension);
        
        for(int i = 1; i < dimensionItems.size(); i++){
            final int index = i;
            items.add(
                    MenuItem.createSpinnerItem(dimensionItems.get(i))
                    .setSpinnerOptionsByArray(on_off)
                    .setCurrentValue(tm.getPLRRTDimensionItemValue(dimension,i))
                    .setOnValueChangeListener(new OnValueChangeListener() {
                        
                        @Override
                        public void onValueChanged(MenuItem item, int value) {
                            setPLRRTDimensionItemEnable(index, value);
                        }
                    }));
        }
    }
    
    public void setPLRRTDimensionItemEnable(int index, int enable)
    {
        tm.setPLRRTDimensionItemValue(dimension, index, enable);
        int listViewIndex = index - 1;
        if (tm.getPLRRTDimensionOrder(dimension) > 0) {
            if (enable > 0 && (index + 1) < dimensionItems.size()) {
                ((SpinnerMenuItem)(this.getListView().getAdapter()
                        .getItem(listViewIndex + 1)))
                        .setCurrentValue(enable, true);
                return;
            }
            if (enable == 0 && (index - 1) > 0) {
                ((SpinnerMenuItem)(this.getListView().getAdapter()
                        .getItem(listViewIndex - 1)))
                        .setCurrentValue(enable, true);
                return;
            }
        }
        else {
        }
    }

    @Override
    public int getTitle() {
        return 0;
    }
    
    @Override
    public String getTitleString() {
        return getArguments().getString("DimensionName");
    }

}
