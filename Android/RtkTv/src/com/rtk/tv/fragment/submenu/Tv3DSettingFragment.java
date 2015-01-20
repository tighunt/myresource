package com.rtk.tv.fragment.submenu;

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

public class Tv3DSettingFragment extends BaseMenuFragment implements OnClickListener {

	// Items
	private SpinnerMenuItem mItem3DMode;

	private SpinnerMenuItem mItem3D2D;
	
	private SpinnerMenuItem mItem3DSwaping;
	
	private SeekBarMenuItem mItem3DDeep;

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
		// 3D mode
		mItem3DMode = MenuItem.createSpinnerItem(R.string.settings_3d_mode);
		mItem3DMode.addSpinnerOption(R.string.settings_3d_disabled, TvManagerHelper.MODE_3D_DISABLED);
		mItem3DMode.addSpinnerOption(R.string.settings_3d_auto, TvManagerHelper.MODE_3D_AUTO, true);
		mItem3DMode.addSpinnerOption(R.string.settings_3d_l_r, TvManagerHelper.MODE_3D_LEFT_RIGHT);
		mItem3DMode.addSpinnerOption(R.string.settings_3d_u_d, TvManagerHelper.MODE_3D_UP_DOWN);
		mItem3DMode.addSpinnerOption(R.string.settings_2d_to_3d, TvManagerHelper.MODE_3D_2D_TO_3D);
		mItem3DMode.setOnValueChangeListener(new OnValueChangeListener() {

			@Override
			public void onValueChanged(MenuItem item, int value) {
				tm.set3DMode(value);
				updateSettings();
			}

		});
		items.add(mItem3DMode);
		
		// 3D to 2D
		mItem3D2D = MenuItem.createSpinnerItem(R.string.settings_3d_to_2d);
		mItem3D2D.addSpinnerOption(R.string.STRING_OFF);
		mItem3D2D.addSpinnerOption(R.string.STRING_ON);
		mItem3D2D.setOnValueChangeListener(new OnValueChangeListener() {

			@Override
			public void onValueChanged(MenuItem item, int value) {
				tm.set3Dto2DEnabled(value == R.string.STRING_ON);
				updateSettings();
			}

		});
		items.add(mItem3D2D);
		
		// 3D Left-Right swappping
		mItem3DSwaping = MenuItem.createSpinnerItem(R.string.settings_3d_left_right_swapping);
		mItem3DSwaping.addSpinnerOption(R.string.STRING_OFF);
		mItem3DSwaping.addSpinnerOption(R.string.STRING_ON);
		mItem3DSwaping.setOnValueChangeListener(new OnValueChangeListener() {

			@Override
			public void onValueChanged(MenuItem item, int value) {
				tm.set3DLeftRightSwapped(value == R.string.STRING_ON);
				updateSettings();
			}

		});
		items.add(mItem3DSwaping);
		
		// 3D Depth
		mItem3DDeep = MenuItem.createSeekBarItem(R.string.settings_3d_depth);
		mItem3DDeep.setBoundary(0, TvManagerHelper.MAX_3D_DEPTH);
		mItem3DDeep.setOnValueChangeListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChanged(MenuItem item, int value) {
				tm.set3DDepth(value);
			}
		});
		items.add(mItem3DDeep);
		
		updateSettings();
	}
	
	private void updateSettings() {
		final TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
		int mode3D = tm.get3DMode();
		
		mItem3DMode.setCurrentValue(mode3D);
		mItem3DMode.setEnable(TvManagerHelper.is3DModeOptionEnabled(mode3D));
		
		mItem3D2D.setCurrentValue(tm.is3Dto2DEnabled() ? R.string.STRING_ON : R.string.STRING_OFF);
		mItem3D2D.setEnable(TvManagerHelper.is3Dto2DOptionEnabled(mode3D));
		
		mItem3DSwaping.setCurrentValue(tm.is3DLeftRightSwapped() ? R.string.STRING_ON : R.string.STRING_OFF);
		mItem3DSwaping.setEnable(TvManagerHelper.is3DLeftRightSwappedOptionEnabled(mode3D));
		
		mItem3DDeep.setCurrentProgress(tm.get3DDepth());
		mItem3DDeep.setEnable(TvManagerHelper.is3DDepthOptionEnabled(mode3D));
	}
	
	@Override
	public int getTitle() {
		return R.string.settings_3d;
	}

	@Override
	public void onClick(View v) {
	    FragmentUtils.popBackSubFragment(this);
	}

}
