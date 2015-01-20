package com.rtk.tv.fragment.submenu;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import java.util.List;

import com.rtk.tv.R;
import com.rtk.tv.TvManagerHelper;
import com.rtk.tv.fragment.submenu.item.MenuItem;
import com.rtk.tv.fragment.submenu.item.MenuItem.OnValueChangeListener;
import com.rtk.tv.fragment.submenu.item.SeekBarMenuItem;
import com.rtk.tv.fragment.submenu.item.SpinnerMenuItem;
import com.rtk.tv.utils.FragmentUtils;

public class ColorTemperatureFragment extends BaseMenuFragment implements OnClickListener {
	
	private static final int[] VALUE_COLOR_TEMP = {
		0,1,2,3,4,5
		/*
		TvManager.SLR_COLORTEMP_USER,
		TvManager.SLR_COLORTEMP_NORMAL,
		TvManager.SLR_COLORTEMP_WARMER,
		TvManager.SLR_COLORTEMP_WARM,
		TvManager.SLR_COLORTEMP_COOL,
		TvManager.SLR_COLORTEMP_COOLER*/
	};
	
	// Items
	private SpinnerMenuItem mItemMode;
	private SeekBarMenuItem mItemRed;
	private SeekBarMenuItem mItemGreen;
	private SeekBarMenuItem mItemBlue;
	
	@Override
	protected View onInflateLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_fragment_base_menu_dialog, container, false);
		View buttonConfirm = v.findViewById(R.id.button_close);
		buttonConfirm.setOnClickListener(this);
		return v;
	}
	
	@Override
	public void onCreateMenuItems(List<MenuItem> items) {
		final TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
		Resources r = getResources();
		// Picture Mode
		mItemMode = MenuItem.createSpinnerItem(R.string.STRING_COLOUR_TEMP);
		mItemMode.setSpinnerOptionsByArray(r.getStringArray(R.array.setting_pic_color_temp), VALUE_COLOR_TEMP);
		mItemMode.setOnValueChangeListener(new OnValueChangeListener() {

			@Override
			public void onValueChanged(MenuItem item, int value) {
				//tm.setColorTempMode(value);
				//updateColorSetting(tm);
			}

		});
		//mItemMode.setCurrentValue(tm.getColorTempMode());
		items.add(mItemMode);
		
		// Red Level
		mItemRed = MenuItem.createSeekBarItem(R.string.STRING_RED_LEVEL).setBoundary(0, 999);
		mItemRed.setOnValueChangeListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChanged(MenuItem item, int value) {
				//tm.setColorTempROffset(value);
			}
		});
		items.add(mItemRed);
		
		// Green Level
		mItemGreen = MenuItem.createSeekBarItem(R.string.STRING_GREEN_LEVEL).setBoundary(0, 999);
		mItemGreen.setOnValueChangeListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChanged(MenuItem item, int value) {
				//tm.setColorTempGOffset(value);
			}
		});
		items.add(mItemGreen);
		
		// Blue Level
		mItemBlue = MenuItem.createSeekBarItem(R.string.STRING_BLUE_LEVEL).setBoundary(0, 999);
		mItemBlue.setOnValueChangeListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChanged(MenuItem item, int value) {
				//tm.setColorTempBOffset(value);
			}
		});
		items.add(mItemBlue);
		//updateColorSetting(tm);
	}
	
	/*private void updateColorSetting(TvManager tm) {
		mItemRed.setCurrentProgress(tm.getColorTempROffset());
		mItemGreen.setCurrentProgress(tm.getColorTempGOffset());
		mItemBlue.setCurrentProgress(tm.getColorTempBOffset());
	}*/

	@Override
	public int getTitle() {
		return R.string.STRING_COLOUR_TEMP;
	}

	@Override
	public void onClick(View v) {
	    FragmentUtils.popBackSubFragment(this);
	}

}
