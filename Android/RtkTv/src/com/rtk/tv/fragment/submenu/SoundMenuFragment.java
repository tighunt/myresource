package com.rtk.tv.fragment.submenu;

import java.util.List;
import com.rtk.tv.R;
import com.rtk.tv.TvManagerHelper;
import com.rtk.tv.fragment.submenu.item.MenuItem;
import com.rtk.tv.fragment.submenu.item.MenuItem.OnValueChangeListener;

public class SoundMenuFragment extends BaseMenuFragment {
	
	/**
	 * Ensure this matches R.array.audio_channel_swap
	 */
	private static final int[] AUDIO_CHANNELS = {
		0,1,2,3,4
		/*TvManager.AUDIO_AO_CHANNEL_OUT_STEREO,
		TvManager.AUDIO_AO_CHANNEL_OUT_R_TO_L,
		TvManager.AUDIO_AO_CHANNEL_OUT_L_TO_R,
		TvManager.AUDIO_AO_CHANNEL_OUT_LR_SWAP,
		TvManager.AUDIO_AO_CHANNEL_OUT_LR_MIXED*/
	};
	
		private final static int AUDIO_DIGITAL_RAW = 1;
		private final static int AUDIO_DIGITAL_LPCM_DUAL_CH = 2;

	@Override
	public void onCreateMenuItems(List<MenuItem> items) {
		final TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
		// Audio Mode
		String[] audioChannels = getResources().getStringArray(R.array.audio_channel_swap);
		items.add(MenuItem.createSpinnerItem(R.string.audio_channel)
				.setSpinnerOptionsByArray(audioChannels, AUDIO_CHANNELS)
				.setCurrentValue(tm.getAudioChannelSwap())
				.setOnValueChangeListener(new OnValueChangeListener() {
					
					@Override
					public void onValueChanged(MenuItem item, int value) {
						tm.setAudioChannelSwap(value);
					}
				}));
		
		// Balance
		items.add(
			MenuItem.createSeekBarItem(R.string.STRING_S_BALANCE)
			.setBoundary(TvManagerHelper.AUDIO_BALANCING_MIN, TvManagerHelper.AUDIO_BALANCING_MAX)
			.setCurrentProgress(tm.getAudioBalancing())
			.setOnValueChangeListener(new OnValueChangeListener() {
				
				@Override
				public void onValueChanged(MenuItem item, int value) {
					tm.setAudioBalancing(value);
				}
			})
		);		
		
		items.add(
			MenuItem.createSpinnerItem(R.string.STRING_SPDIFOUTPUT)
			.addSpinnerOption(R.string.STRING_SPDIFOUTPUT_RAW, AUDIO_DIGITAL_RAW)
			.addSpinnerOption(R.string.STRING_SPDIFOUTPUT_LPCM,AUDIO_DIGITAL_LPCM_DUAL_CH)
			.setCurrentPosition(0)
			.setOnValueChangeListener(new OnValueChangeListener() {			
				@Override
				public void onValueChanged(MenuItem item, int value) {
					tm.setAudioSpdifOutput(value);
				}
			})
		);
	}

	@Override
	public int getTitle() {
		return R.string.STRING_SOUND;
	}

}
