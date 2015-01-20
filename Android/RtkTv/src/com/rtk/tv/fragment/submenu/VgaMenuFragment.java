package com.rtk.tv.fragment.submenu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.rtk.tv.utils.FragmentUtils;
import com.rtk.tv.R;
import com.rtk.tv.TvManagerHelper;
import com.rtk.tv.fragment.submenu.item.MenuItem;
import com.rtk.tv.fragment.submenu.item.MenuItem.OnValueChangeListener;
import com.rtk.tv.fragment.submenu.item.SeekBarMenuItem;
import java.util.List;

public class VgaMenuFragment extends BaseMenuFragment implements OnClickListener {
	public SeekBarMenuItem PvrMenuFragment;
	public SeekBarMenuItem vGAVerticalPosition;
	public SeekBarMenuItem vGAHorizonPosition;
	@Override
	public void onCreateMenuItems(List<MenuItem> items) 
	{
		final TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
		items.add
		(
			MenuItem.createTextItem(R.string.STRING_VGA_AUTOADJUST)
			.setOnClickListener
			(
				new OnClickListener() 
				{
					@Override
					public void onClick(View v) 
					{
						//AutoAdjustMenuFragment.show(getBaseFragment(), AutoAdjustMenuFragment.class, null, R.string.STRING_VGA_AUTOADJUST, R.string.STRING_VGA_AUTOADJUST);
					}
				}
			)
		);

		// TODO: VGA Horizon Position 	
		vGAHorizonPosition = MenuItem.createSeekBarItem(R.string.STRING_VGA_HORIZON_POS)
	   .setBoundary(TvManagerHelper.VGA_HPOSITION_MIN, TvManagerHelper.VGA_HPOSITION_MAX)
	   .setCurrentProgress(tm.getVgaHPosition());
		vGAHorizonPosition.setOnValueChangeListener
		(
			new OnValueChangeListener() 
			{		
				@Override
				public void onValueChanged(MenuItem item, int value) 
				{
					value = value + 50;
					//tm.setVgaHPosition((char)value);
				}
			}
		);
		items.add(vGAHorizonPosition);	
		// TODO: VGA Vertical Position 
		vGAVerticalPosition = MenuItem.createSeekBarItem(R.string.STRING_VGA_VERTICAL_POS)
		.setBoundary(TvManagerHelper.VGA_VPOSITION_MIN, TvManagerHelper.VGA_VPOSITION_MAX)
		.setCurrentProgress(tm.getVgaVPosition());
		vGAVerticalPosition.setOnValueChangeListener
		(
		 	new OnValueChangeListener() 
		 	{		 
				 @Override
				 public void onValueChanged(MenuItem item, int value) 
				 {
				 	 value = value + 50;
					 tm.setVgaVPosition((char)value);
				 }
		 	}
		);
		items.add(vGAVerticalPosition);  
		
		// TODO: VGA Clock 
		items.add(
			MenuItem.createSeekBarItem(R.string.STRING_VGA_CLOCK)
		   .setBoundary(TvManagerHelper.VGA_CLOCK_MIN, TvManagerHelper.VGA_CLOCK_MAX)
		   .setCurrentProgress(tm.getVgaClock())
		   .setOnValueChangeListener(new OnValueChangeListener() {
			
				@Override
				public void onValueChanged(MenuItem item, int value) {
					value = value + 50;
					tm.setVgaClock((char)value);		
				}
			})
			.setEnable(true
					/*(tm.getCurSourceType() == TvManager.SOURCE_VGA1 || tm.getCurSourceType() == TvManager.SOURCE_VGA2)? true : false*/)
		);
			
		// TODO: VGA Phase 
		items.add(
			MenuItem.createSeekBarItem(R.string.STRING_VGA_PHASE)
		   .setBoundary(TvManagerHelper.VGA_PHASE_MIN, TvManagerHelper.VGA_PHASE_MAX)
		   .setCurrentProgress(tm.getVgaPhase())
		   .setOnValueChangeListener(new OnValueChangeListener() {
			
				@Override
				public void onValueChanged(MenuItem item, int value) {
					tm.setVgaPhase((char)value);
				}
			})
			.setEnable(true
					/*(tm.getCurSourceType() == TvManager.SOURCE_VGA1 || tm.getCurSourceType() == TvManager.SOURCE_VGA2)? true : false*/)
		);	
		vGAHorizonPosition.setCurrentProgress(tm.getVgaHPosition());
		vGAVerticalPosition.setCurrentProgress(tm.getVgaVPosition());
	}

	@Override
	public int getTitle() {
		return R.string.STRING_VGA;
	}

	@Override
	protected View onInflateLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_fragment_base_menu_dialog, container, false);
		View buttonConfirm = v.findViewById(R.id.button_close);
		buttonConfirm.setOnClickListener(this);
		return v;
	}

	@Override
	public void onClick(View v) {
		FragmentUtils.popBackSubFragment(this);
	}
}


