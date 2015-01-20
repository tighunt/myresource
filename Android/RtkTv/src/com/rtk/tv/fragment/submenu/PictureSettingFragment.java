package com.rtk.tv.fragment.submenu;

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

public class PictureSettingFragment extends BaseMenuFragment implements OnClickListener {

	private static final int[] VALUE_MODES = {
		0,1,2,3,4,5,6,7
		/*
		TvManager.PICTURE_MODE_USER,
		TvManager.PICTURE_MODE_VIVID,
		TvManager.PICTURE_MODE_STD,
		TvManager.PICTURE_MODE_GENTLE,
		TvManager.PICTURE_MODE_MOVIE,
		TvManager.PICTURE_MODE_SPORT,
		TvManager.PICTURE_MODE_GAME,
		TvManager.PICTURE_MODE_MAX*/
	};
	
	// Items
	private SpinnerMenuItem mItemMode;

	private SeekBarMenuItem mItemBackLight;

	private SeekBarMenuItem mItemContrast;

	private SeekBarMenuItem mItemBright;

	private SeekBarMenuItem mItemColor;

	private SeekBarMenuItem mItemTint;

	private SeekBarMenuItem mItemSharpness;
	
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
		mItemMode = MenuItem.createSpinnerItem(R.string.STRING_PICTUR_MODE);
		mItemMode.setSpinnerOptionsByArray(r.getStringArray(R.array.setting_pic_modes), VALUE_MODES);
		mItemMode.setCurrentValue(0/*tm.mTvManager.getPictureMode()*/);
		mItemMode.setOnValueChangeListener(new OnValueChangeListener() {

			@Override
			public void onValueChanged(MenuItem item, int value) {
				tm.setPictureMode(value);
				//updatePictureSetting(tm.mTvManager);
			}

		});
		items.add(mItemMode);
		
		// Back light
		mItemBackLight = MenuItem.createSeekBarItem(R.string.STRING_BACKLIGHT).setBoundary(0, 100);
		mItemBackLight.setOnValueChangeListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChanged(MenuItem item, int value) {
				//onChangePictureSetting(tm.mTvManager, (SeekBarMenuItem) item, value);
				//tm.mTvManager.setBacklight(value);
			}
		});
		items.add(mItemBackLight);
		
		// Contrast
		mItemContrast = MenuItem.createSeekBarItem(R.string.STRING_CONTRAST).setBoundary(0, 100);
		mItemContrast.setOnValueChangeListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChanged(MenuItem item, int value) {
				//onChangePictureSetting(tm.mTvManager, (SeekBarMenuItem) item, value);
				//tm.mTvManager.setContrast(value);
			}
		});
		items.add(mItemContrast);
		
		// Brightness
		mItemBright = MenuItem.createSeekBarItem(R.string.STRING_BRIGHTNESS).setBoundary(0, 100);
		mItemBright.setOnValueChangeListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChanged(MenuItem item, int value) {
				//onChangePictureSetting(tm.mTvManager, (SeekBarMenuItem) item, value);
				//tm.mTvManager.setBrightness(value);
			}
		});
		items.add(mItemBright);
		
		// Color
		mItemColor = MenuItem.createSeekBarItem(R.string.STRING_COLOUR).setBoundary(0, 100);
		mItemColor.setOnValueChangeListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChanged(MenuItem item, int value) {
				//onChangePictureSetting(tm.mTvManager, (SeekBarMenuItem) item, value);
				//tm.mTvManager.setSaturation(value);
			}
		});
		items.add(mItemColor);
		
		// Tint
		mItemTint = MenuItem.createSeekBarItem(R.string.STRING_TINT).setBoundary(-50, 50);
		mItemTint.setOnValueChangeListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChanged(MenuItem item, int value) {
				//onChangePictureSetting(tm.mTvManager, (SeekBarMenuItem) item, value);
				//tm.mTvManager.setHue(value);
			}
		});
		items.add(mItemTint);
		
		// Sharpness
		mItemSharpness = MenuItem.createSeekBarItem(R.string.STRING_SHARPNESS).setBoundary(-50, 50);
		mItemSharpness.setOnValueChangeListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChanged(MenuItem item, int value) {
				//onChangePictureSetting(tm.mTvManager, (SeekBarMenuItem) item, value);
				//tm.mTvManager.setSharpness(true, value);
			}
		});
		items.add(mItemSharpness);
		
		//updatePictureSetting(tm.mTvManager);
		
		// Color Temperature
		items.add(MenuItem.createTextItem(R.string.STRING_COLOUR_TEMP)
				.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
					    FragmentUtils.showSubFragment(PictureSettingFragment.this, ColorTemperatureFragment.class);
					}
				}));

		// Noise Reduction
		items.add(MenuItem.createTextItem(R.string.STRING_NOISE_REDUCTION)
				.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
					    FragmentUtils.showSubFragment(PictureSettingFragment.this, NoiseReductionFragment.class);
					}
				}));
	}
	
	/*private void onChangePictureSetting(TvManager tm, SeekBarMenuItem item, int value) {
		if (mItemMode.getSelectedValue() != TvManager.PICTURE_MODE_USER) {
			// Get current values
			int bright = tm.getBrightness();
			int contrast = tm.getContrast();
			int backlight = tm.getBacklight();
			int color = tm.getSaturation();
			int tint = tm.getHue();
			int sharpness = tm.getSharpness();
			
			// Change to user mode
			mItemMode.setCurrentValue(TvManager.PICTURE_MODE_USER, true);
			
			// Current item might be modified.
			// Restore it.
			item.setCurrentProgress(value, false);
			
			// Copy previous values to user setting
			updateItemIfNotEqual(item, mItemBright, bright);
			updateItemIfNotEqual(item, mItemContrast, contrast);
			updateItemIfNotEqual(item, mItemBackLight, backlight);
			updateItemIfNotEqual(item, mItemColor, color);
			updateItemIfNotEqual(item, mItemTint, tint);
			updateItemIfNotEqual(item, mItemSharpness, sharpness);
		}
	}*/
	
	private static void updateItemIfNotEqual(MenuItem base, SeekBarMenuItem item, int value) {
		if (base != item) {
			item.setCurrentProgress(value, true);
		}
	}
	
	/*private void updatePictureSetting(TvManager tm) {
		mItemBright.setCurrentProgress(tm.getBrightness());
		mItemContrast.setCurrentProgress(tm.getContrast());
		mItemBackLight.setCurrentProgress(tm.getBacklight());
		mItemColor.setCurrentProgress(tm.getSaturation());
		mItemTint.setCurrentProgress(tm.getHue());
		mItemSharpness.setCurrentProgress(tm.getSharpness());
	}*/

	@Override
	public int getTitle() {
		return R.string.STRING_PIC_SETTINGS;
	}

	@Override
	public void onClick(View v) {
	    FragmentUtils.popBackSubFragment(this);
	}

}
