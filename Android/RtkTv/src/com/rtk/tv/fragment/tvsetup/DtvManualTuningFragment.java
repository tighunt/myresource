package com.rtk.tv.fragment.tvsetup;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

import com.rtk.tv.R;
import com.rtk.tv.TvManagerHelper;
import com.rtk.tv.fragment.submenu.BaseMenuFragment;
import com.rtk.tv.fragment.submenu.item.DigitMenuItem;
import com.rtk.tv.fragment.submenu.item.MenuItem;
import com.rtk.tv.fragment.submenu.item.MenuItem.OnValueChangeListener;
import com.rtk.tv.fragment.submenu.item.SpinnerMenuItem;
import com.rtk.tv.utils.FragmentUtils;

public class DtvManualTuningFragment extends BaseMenuFragment implements OnClickListener {

	private static final boolean DEFAULT_ADVANCED_ENABLED = false;
	private static final int[] OPTIONS_BANDWIDTH = {
		0,1,2
		/*TvManager.TUNING_BANDWIDTH_8MHZ,
		TvManager.TUNING_BANDWIDTH_7MHZ,
		TvManager.TUNING_BANDWIDTH_7MHZ_8MHZ,*/
	};
	private static final int[] OPTIONS_MODULATION = {
		0,1,2,3,4,5,6,7,8,9
		/*TvManager.MOD_ATV,
		TvManager.MOD_OQPSK,
		TvManager.MOD_VSB8,
		TvManager.MOD_VSB16,
		TvManager.MOD_QAM_AUTO,
		TvManager.MOD_QAM4,
		TvManager.MOD_QAM16,
		TvManager.MOD_QAM32,
		TvManager.MOD_QAM64,
		TvManager.MOD_QAM128,
		TvManager.MOD_QAM256,
		TvManager.MOD_QAM512,
		TvManager.MOD_QAM1024,
		TvManager.MOD_QPSK,
		TvManager.MOD_BPSK,
		TvManager.MOD_OFDM,
		TvManager.MOD_ISDB_AUTO,
		TvManager.MOD_DVB_AUTO,
		TvManager.MOD_DTMB_AUTO,
		TvManager.MOD_ABS_AUTO,*/
	};
	
	private static class ChannelFrequency {
		public final int index;		
		protected int frequency = -1;
		protected final TvManagerHelper tm;		
		ChannelFrequency(TvManagerHelper tm, int index) {
			this.index = index;
			this.tm = tm;
		}
		
		@Override
		public String toString() {
			return String.valueOf(getFrequency());
		}
		
		public int getFrequency() {
			if (frequency < 0) {
				frequency = tm.getDtvFrequency(index);
			}
			return frequency;
		}
		
	}	
	private TvManagerHelper tm;	
	private final List<ChannelFrequency> mData = new ArrayList<DtvManualTuningFragment.ChannelFrequency>();	
	private SpinnerMenuItem mItemFreq;
	private SpinnerMenuItem mItemBand;	
	private SpinnerMenuItem mItemSwitch;
	private DigitMenuItem mItemFreqText;
	private SpinnerMenuItem mItemModl;
	private DigitMenuItem mItemSymb;
	private int mLastIndex = -1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tm = TvManagerHelper.getInstance(getActivity());
		mData.clear();
		final int count = tm.getDtvFrequencyTableSize();
		for (int i = 0; i < count; i++) {
			ChannelFrequency item = new ChannelFrequency(tm, i);
			mData.add(item);
		}
		
	}
	
	@Override
	protected View onInflateLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_fragment_dtv_manual_tuning, container, false);
		View buttonConfirm = v.findViewById(R.id.button_confirm);
		buttonConfirm.setOnClickListener(this);
		return v;
	}

	@Override
	public void onCreateMenuItems(List<MenuItem> items) {
		// Frequency option
		mItemFreq = MenuItem.createSpinnerItem(R.string.STRING_FREQUENCY);
		long freq = tm.getDtvCurFrequency();
		int currentPosition = 0;
		for (int i = 0; i < mData.size(); i++) {
			ChannelFrequency c = mData.get(i);
			mItemFreq.addSpinnerOption(c.toString(), c.frequency);
			if(freq == (long)c.getFrequency()){
				currentPosition = i;
			}
		}
		mItemFreq.setEnable(!DEFAULT_ADVANCED_ENABLED);
		items.add(mItemFreq);
		
		mItemFreq.setCurrentPosition(currentPosition, false);
		
		// Bandwidth option
		mItemBand = MenuItem.createSpinnerItem(R.string.STRING_BAND_WIDTH);
		String[] bandwidths = getResources().getStringArray(R.array.band_width);
		mItemBand.setSpinnerOptionsByArray(bandwidths, OPTIONS_BANDWIDTH);
		items.add(mItemBand);
	
		// -------- Advanced Options
		// Advanced option switch
		mItemSwitch = MenuItem.createSpinnerItem(R.string.advanced_options);
		mItemSwitch
			.addSpinnerOption(R.string.STRING_OFF)
			.addSpinnerOption(R.string.STRING_ON)
			.setCurrentValue(DEFAULT_ADVANCED_ENABLED ? R.string.STRING_ON : R.string.STRING_OFF)
			.setOnValueChangeListener(new OnValueChangeListener() {
				
				@Override
				public void onValueChanged(MenuItem item, int value) {
					boolean enable = value == R.string.STRING_ON;
					mItemFreq.setEnable(!enable);
					
					mItemModl.setEnable(enable);
					mItemFreqText.setEnable(enable);
					mItemSymb.setEnable(enable);
				}
			});
		items.add(mItemSwitch);
		
		// Manual Frequency
		mItemFreqText = MenuItem.createDigitItem(R.string.frequency)
				.setMaxDigits(9)
				.setCurrentValue(mItemFreq.getSelectedValue());
		mItemFreqText.setEnable(DEFAULT_ADVANCED_ENABLED);
		items.add(mItemFreqText);
		
		// Modulation
		mItemModl = MenuItem.createSpinnerItem(R.string.modulation);
		String[] modulations = getResources().getStringArray(R.array.tuner_modulations);
		mItemModl.setSpinnerOptionsByArray(modulations, OPTIONS_MODULATION);
		mItemModl.setCurrentValue(0/*TvManager.MOD_QAM_AUTO*/);
		mItemModl.setEnable(DEFAULT_ADVANCED_ENABLED);
		items.add(mItemModl);
		
		// Symbol Rate
		mItemSymb = MenuItem.createDigitItem(R.string.symbol_rate)
				.setMaxDigits(6)
				.setCurrentValue(0);
		mItemSymb.setEnable(DEFAULT_ADVANCED_ENABLED);
		items.add(mItemSymb);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);
		v.requestFocus();
		if (mLastIndex >= 0 && mLastIndex < mData.size()) {
			mItemFreq.setCurrentPosition(mLastIndex, true);
		}
		return v;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mLastIndex = mItemFreq.getCurrentPosition();
	}

	@Override
	public int getTitle() {
		return R.string.STRING_DTV_MANUAL_TUNING;
	}

	@Override
	public void onClick(View v) {	
		boolean advanced = mItemSwitch.getSelectedValue() == R.string.STRING_ON;
		int frequency = advanced ? mItemFreqText.getCurrentValue() : mItemFreq.getSelectedValue();
		int bandwidth = mItemBand.getSelectedValue();
		int modulation = mItemModl.getSelectedValue();
		int symbolRate = mItemSymb.getCurrentValue();
		
		Bundle args = DtvManualTuningResultFragment.buildArguments(0, frequency, bandwidth, advanced, modulation, symbolRate);
		FragmentUtils.popBackSubFragment(this, Activity.RESULT_OK, args);
	}

}
