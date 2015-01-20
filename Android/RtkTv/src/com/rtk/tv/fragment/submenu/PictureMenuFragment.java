package com.rtk.tv.fragment.submenu;


import android.view.View;
import android.view.View.OnClickListener;


import java.util.List;

import com.rtk.tv.R;
import com.rtk.tv.TvManagerHelper;
import com.rtk.tv.fragment.submenu.item.MenuItem;
import com.rtk.tv.fragment.submenu.item.MenuItem.OnValueChangeListener;
import com.rtk.tv.utils.FragmentUtils;

public class PictureMenuFragment extends BaseMenuFragment {

	@Override
	public void onCreateMenuItems(List<MenuItem> items) {
		final TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
		//Aspect Ratio
		items.add(
			MenuItem.createSpinnerItem(R.string.STRING_ASPECT_RATIO)
			.addSpinnerOption(R.string.STRING_16_9)
			.addSpinnerOption(R.string.STRING_4_3)
			.setCurrentPosition(0)
			.setOnValueChangeListener(new OnValueChangeListener() {
				
				@Override
				public void onValueChanged(MenuItem item, int value) {
					switch (value) {
					case R.string.STRING_4_3:
						tm.setAspectRatio(TvManagerHelper.SCALER_RATIO_4_3);
						break;
					case R.string.STRING_16_9:
					default:
						tm.setAspectRatio(TvManagerHelper.SCALER_RATIO_16_9);
						break;
						
					}
				}
			})
		);
			
		// 3D Settings
		items.add(
			MenuItem.createTextItem(R.string.settings_3d)
			// FIXME: Missing "IsSupport3D" in TvServer
			.setEnable(tm.isSupport3D() && tm.is4K2KMode())
			.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					FragmentUtils.showSubFragment(getBaseFragment(), Tv3DSettingFragment.class);
				}
			})
		);
		
		// Picture Setting
		items.add(
			MenuItem.createTextItem(R.string.STRING_PIC_SETTINGS)
			.setOnClickListener(new OnClickListener() {
			
				@Override
				public void onClick(View v) {
					FragmentUtils.showSubFragment(getBaseFragment(), PictureSettingFragment.class);
				}
			})
		);
		
		
		// Active backlight Control
		items.add(
			MenuItem.createSpinnerItem(R.string.STRING_ACTIVE_BACKLIGHT_CTRL)
			.addSpinnerOption(R.string.STRING_ON)
			.addSpinnerOption(R.string.STRING_OFF)
			.setCurrentPosition( 0)
			.setOnValueChangeListener(new OnValueChangeListener() {
				
				@Override
				public void onValueChanged(MenuItem item, int value) {
					tm.setBacklightControl(value == R.string.STRING_ON);
				}
			})
		);
		
		// VGA Settings
		items.add(
			MenuItem.createTextItem(R.string.STRING_VGA_SETTINGS)
			.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					FragmentUtils.showSubFragment(getBaseFragment(), VgaMenuFragment.class);
				}
			})
			.setEnable(true/*sourceType == TvManager.SOURCE_VGA1 || sourceType == TvManager.SOURCE_VGA2*/)
		);
	}

	@Override
	public int getTitle() {
		return R.string.STRING_PICTURE;
	}

}
