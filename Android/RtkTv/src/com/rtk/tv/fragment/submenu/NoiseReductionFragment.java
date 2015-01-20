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
import com.rtk.tv.utils.FragmentUtils;

public class NoiseReductionFragment extends BaseMenuFragment implements OnClickListener {
	
	private static final int[] VALUE_DRN = {
		0,1,2,3,4
		/*
		TvManager.DNR_OFF,
		TvManager.DNR_LOW,
		TvManager.DNR_MEDIUM,
		TvManager.DNR_HIGH,
		TvManager.DNR_AUTO*/
	};

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
		// DNR
		items.add(MenuItem.createSpinnerItem(R.string.STRING_DNR)
				.setSpinnerOptionsByArray(getResources().getStringArray(R.array.setting_dnr_mode), VALUE_DRN)
				.setCurrentValue(0/*tm.mTvManager.getDNR()*/)
				.setOnValueChangeListener(new OnValueChangeListener() {

					@Override
					public void onValueChanged(MenuItem item, int value) {
						//tm.mTvManager.setDNR(value);
					}

				}));
	
	}
	
	@Override
	public int getTitle() {
		return R.string.STRING_NOISE_REDUCTION;
	}

	@Override
	public void onClick(View v) {
	    FragmentUtils.popBackSubFragment(this);
	}

}
