package com.rtk.tv.fragment.submenu;

import com.rtk.tv.R;
import com.rtk.tv.TvManagerHelper;
import com.rtk.tv.fragment.submenu.item.MenuItem;
import com.rtk.tv.fragment.submenu.item.MenuItem.OnValueChangeListener;

import java.util.List;

public class PreferenceMenuFragment extends BaseMenuFragment {

	@Override
	public void onCreateMenuItems(List<MenuItem> items) {
		final TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
		// Blue Screen
		items.add(
			MenuItem.createSpinnerItem(R.string.STRING_P_BLUE_SCREEN)
			.addSpinnerOption(R.string.STRING_ON)
			.addSpinnerOption(R.string.STRING_OFF)
			.setCurrentPosition(0)
			.setOnValueChangeListener(new OnValueChangeListener() {
				
				@Override
				public void onValueChanged(MenuItem item, int value) {
					tm.setBlueScreenEnable(value == R.string.STRING_ON);
				}
			})
		);
	}

	@Override
	public int getTitle() {
		return R.string.STRING_PREFERENCES;
	}

}
