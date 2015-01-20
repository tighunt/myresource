package com.rtk.tv.fragment.tvsetup;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.rtk.tv.R;
import com.rtk.tv.TvManagerHelper;
import com.rtk.tv.data.ChannelInfo;

public class AtvManualTuningSettingFragment extends Fragment {
	
	public static final String ARG_INDEX = "channel_index";
	
	private abstract static class Item implements View.OnKeyListener, OnFocusChangeListener {
		
		public final int id;
		public final int hint;
		
		private final List<String> options = new ArrayList<String>();
		
		private int position = -1;
		
		private View bindView;
		private TextView hintView;
		private boolean mFocused = false;
		
		protected Context mContext;
		protected Configuration mConfigurations;
		//protected ChannelInformation mChanneoInfo;
		
		Item(int id, int hint) {
			this.id = id;
			this.hint = hint;
		}
		
		abstract protected void onSetupOptions(Context context, List<String> options);
		abstract protected void onUpdateView(View view);
		abstract public int valueAt(int position);
		
		public void init(Context context, Configuration configurations) {
			mContext = context.getApplicationContext();
			mConfigurations = configurations;
			onSetupOptions(context, options);
			
			if (options.isEmpty()) {
				position = -1;
			} else if (position < 0 || position >= options.size()) {
				position = 0;
			}
		}
		
		public void bindView(View view, TextView hint) {
			bindView = view;
			hintView = hint;
			view.setTag(this);
			view.setOnKeyListener(this);
			view.setOnFocusChangeListener(this);
			onFocusChange(view, view.isFocused());
		}
		
		public void unbindView() {
			bindView = null;
		}
		
		public int getPosition() {
			return position;
		}
		
		public boolean setPosition(int position, boolean notify) {
			if (position < 0 || position >= options.size()) {
				return false;
			}
			
			this.position = position;
			if (notify) {
				onValueChanged(position);
			}
			return true;
		}
		
		public boolean nextValue(boolean notify) {
			if (options.isEmpty()) {
				position = -1;
				return false;
			}
			
			position++;
			if (position >= options.size()) {
				position = 0;
			}
			if (notify) {
				onValueChanged(position);
			}
			return true;
		}
		
		public boolean prevValue(boolean notify) {
			if (options.isEmpty()) {
				position = -1;
				return false;
			}
			
			position--;
			if (position < 0) {
				position = options.size() - 1;
			}
			if (notify) {
				onValueChanged(position);
			}
			return true;
		}
		
		/*public void updateValue(ChannelInformation channel) {
			mChanneoInfo = channel;
			onUpdateValue(channel);
			updateView();
		}*/
		
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				switch(keyCode) {
				case KeyEvent.KEYCODE_DPAD_UP:
					return nextValue(true);
				case KeyEvent.KEYCODE_DPAD_DOWN:
					return prevValue(true);
				default:
					break;
				}
			}
			return false;
		}

		@Override
		public String toString() {
			if (position >= 0) {
				return options.get(position);
			}
			return "-";
		}
		
		public final void updateView() {
			if (bindView != null) {
				onUpdateView(bindView);
			}
			if (hintView != null && mFocused) {
				onSetupHint(hintView);
			}
		}
		
		protected void onValueChanged(int position) {
			updateView();
		}

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			mFocused = hasFocus;
			if (hintView != null && hasFocus) {
				onSetupHint(hintView);
			}
		}
		
		protected void onSetupHint(TextView tv) {
			hintView.setText(hint);
		}
		
		protected static int indexOf(int[] array, int value) {
			for (int i = 0; i < array.length; i++) {
				if (array[i] == value) {
					return i;
				}
			}
			return -1;
		}		
		public void setTextHint(String str) {
			hintView.setText(str);
		}
	}
	
	private static abstract class TextItem extends Item {
		TextItem(int id, int hint) {
			super(id, hint);
		}
		@Override
		protected void onUpdateView(View view) {
			TextView tv = (TextView) ((ViewGroup) view).getChildAt(1);
			tv.setText(toString());
		}	
	}
	
	private static abstract class ImageItem extends Item {
		ImageItem(int id, int hint) {
			super(id, hint);
		}
		@Override
		protected void onUpdateView(View view) {
			ImageView iv = (ImageView) ((ViewGroup) view).getChildAt(1);
			iv.setImageResource(getImageResourceAt(getPosition()));
		}
		abstract int getImageResourceAt(int position);
		
	}
	// ----------------- Items ----------------------------------------------------------
	private static class ItemPos extends TextItem {

		ItemPos() {
			super(R.id.option_position, R.string.num);
		}

		@Override
		protected void onSetupOptions(Context context, List<String> options) {
			TvManagerHelper tm = TvManagerHelper.getInstance(mContext);
			int count = tm.getAtvScanEntryCount();
			for (int i = 0; i < count; i++) {
				options.add(String.valueOf(i));
			}
		}

		@Override
		public int valueAt(int position) {
			return position;
		}

		/*@Override
		protected void onUpdateValue(ChannelInformation channel) {
			setPosition(channel.channelIndex, false);
		}*/

		@Override
		protected void onValueChanged(int position) {
			super.onValueChanged(position);
			TvManagerHelper tm = TvManagerHelper.getInstance(mContext);
			tm.setChannelByIndex(position);
		}

		@Override
		protected void onUpdateView(View view) {
			TextView tv = (TextView) ((ViewGroup) view).getChildAt(1);
			//tv.setText(mChanneoInfo.formatNumber("%03d", "%d-%d"));
		}

	}
	
	private static class ItemSystem extends TextItem {

		private static final int[] VALUES = {
			0,1,2,3
			/*TvManager.RT_ATV_SOUND_SYSTEM_DK,
			TvManager.RT_ATV_SOUND_SYSTEM_I,
			TvManager.RT_ATV_SOUND_SYSTEM_BG,
			TvManager.RT_ATV_SOUND_SYSTEM_MN*/
		};		
		ItemSystem() {
			super(R.id.option_system, R.string.STRING_SYSTEM);
		}
		@Override
		protected void onSetupOptions(Context context, List<String> options) {
			String[] opt = context.getResources().getStringArray(R.array.manual_tuning_system);
			for (int i = 0; i < opt.length; i++) {
				options.add(opt[i]);
			}
		}
		@Override
		public int valueAt(int position) {
			return VALUES[position];
		}
/*
		@Override
		protected void onUpdateValue(ChannelInformation channel) {
			super.onUpdateValue(channel);
			int value = TvManagerHelper.getInstance(mContext).getCurrentAtvSoundStd();
			setPosition(indexOf(VALUES, value), false);
		}*/

		@Override
		protected void onValueChanged(int position) {
			super.onValueChanged(position);
			int value = VALUES[position];
			TvManagerHelper.getInstance(mContext).setCurrentAtvSoundStd(value);
		}
		
	}
	
	private static class ItemColor extends TextItem {

		private static final int[] VALUES = {
			0,1,2,3,4
			/*
			TvManager.RT_COLOR_STD_AUTO,
			TvManager.RT_COLOR_STD_PAL_60,
			TvManager.RT_COLOR_STD_SECAM,
			TvManager.RT_COLOR_STD_NTSC_443,
			TvManager.RT_COLOR_STD_NTSC*/
		};
		
		String[] hints;
		
		ItemColor() {
			super(R.id.option_color, R.string.STRING_COLOR_SYSTEM);
		}

		@Override
		protected void onSetupOptions(Context context, List<String> options) {
			hints = context.getResources().getStringArray(R.array.manual_tuning_color_system_full);
			String[] opt = context.getResources().getStringArray(R.array.manual_tuning_color_system);
			for (int i = 0; i < opt.length; i++) {
				options.add(opt[i]);
			}
		}

		@Override
		public int valueAt(int position) {
			return VALUES[position];
		}

		@Override
		protected void onValueChanged(int position) {
			super.onValueChanged(position);
			TvManagerHelper tm = TvManagerHelper.getInstance(mContext);
			tm.setCurrentAtvColorStd(VALUES[position]);
		}
		/*
		@Override
		protected void onUpdateValue(ChannelInformation channel) {
			super.onUpdateValue(channel);
			int value = TvManagerHelper.getInstance(mContext).getCurrentAtvColorStd();
			setPosition(indexOf(VALUES, value), false);
		}*/
		
		@Override
		protected void onSetupHint(TextView tv) {
			String h = tv.getResources().getString(hint);
			tv.setText(h + ": " + hints[getPosition()]);
		}
	}
	
	private static class ItemSkip extends ImageItem {

		ItemSkip() {
			super(R.id.option_skip, R.string.STRING_SKIP);
		}

		@Override
		int getImageResourceAt(int position) {
			return position == 1 ? R.drawable.ic_item_atvtune_skip_on
					: R.drawable.ic_item_atvtune_skip_off;
		}

		@Override
		protected void onSetupOptions(Context context, List<String> options) {
			options.add(context.getString(R.string.STRING_SKIP_OFF));
			options.add(context.getString(R.string.STRING_SKIP_ON));
		}

		@Override
		public int valueAt(int position) {
			return position;
		}

		@Override
		protected void onSetupHint(TextView tv) {
			tv.setText(toString());
		}

		@Override
		protected void onValueChanged(int position) {
			super.onValueChanged(position);
			TvManagerHelper tm = TvManagerHelper.getInstance(mContext);
			tm.setCurrentAtvChannelSkipped(position == 1);
		}
		/*
		@Override
		protected void onUpdateValue(ChannelInformation channel) {
			super.onUpdateValue(channel);
			setPosition(channel.skipped? 1 : 0, false);
		}*/
		
		
	}

	private static class ItemSearch extends ImageItem {

		ItemSearch() {
			super(R.id.option_search, R.string.STRING_SEARCH);
		}

		@Override
		int getImageResourceAt(int position) {
			return 0;
		}

		@Override
		protected void onSetupOptions(Context context, List<String> options) {
			
		}

		@Override
		public int valueAt(int position) {
			return 0;
		}

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (event.getAction() != KeyEvent.ACTION_DOWN) {
				return false;
			}
			
			switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_UP: {
				TvManagerHelper tm = TvManagerHelper.getInstance(mContext);
				Item pos = mConfigurations.getItemPosition();
				int idx = pos.getPosition();
				
				String hint = mContext.getString(R.string.STRING_SEARCHING);
				if(tm.isAtvTableScanEnabled()) {
					tm.startAtvTableScanning(idx);
				} else {
					tm.startAtvSeekScanning(true);
					hint += ": " + mContext.getString(R.string.STRING_UP);
				}
				setTextHint(hint);
			}
				return true;
			case KeyEvent.KEYCODE_DPAD_DOWN: {
				TvManagerHelper tm = TvManagerHelper.getInstance(mContext);
				Item pos = mConfigurations.getItemPosition();
				int idx = pos.getPosition();
				
				String hint = mContext.getString(R.string.STRING_SEARCHING);
				if(tm.isAtvTableScanEnabled()) {
					tm.startAtvTableScanning(idx);
				} else {
					tm.startAtvSeekScanning(false);
					hint += ": " + mContext.getString(R.string.STRING_DOWN);
				}
				setTextHint(hint);
			}
				return true;
			default:
				return false;
			}
		}
		
		
	}
	
	private static class ItemFineTune extends TextItem {

		private static final int MAX = 15;
		
		ItemFineTune() {
			super(R.id.option_fine_tune, R.string.STRING_MANUAL_FINE_TUNING);
		}

		@Override
		protected void onSetupOptions(Context context, List<String> options) {
			for (int i = -MAX; i <= MAX; i++ ) {
				options.add(String.valueOf(i));
			}
			setPosition(MAX + 1, false);
		}

		@Override
		public int valueAt(int position) {
			return position - MAX;
		}
		
		@Override
		protected void onValueChanged(int position) {
			super.onValueChanged(position);
			TvManagerHelper tm = TvManagerHelper.getInstance(mContext);
			tm.setCurrentAtvFrequencyOffset(valueAt(getPosition()), true);
		}
		/*
		@Override
		protected void onUpdateValue(ChannelInformation channel) {
			super.onUpdateValue(channel);
			TvManagerHelper tm = TvManagerHelper.getInstance(mContext);
			int offset = tm.getCurrentAtvFrequencyOffset();
			setPosition(offset + MAX, false);
		}*/
	}
	
	private static class ItemSigBooster extends TextItem {

		ItemSigBooster() {
			super(R.id.option_sigbooster, R.string.STRING_SIGNALBOOSTER);
		}

		@Override
		protected void onSetupOptions(Context context, List<String> options) {
			options.add(context.getString(R.string.STRING_OFF));
			options.add(context.getString(R.string.STRING_ON));
		}

		@Override
		public int valueAt(int position) {
			return position;
		}
		
		@Override
		protected void onValueChanged(int position) {
			super.onValueChanged(position);
			TvManagerHelper tm = TvManagerHelper.getInstance(mContext);
			//tm.setAtvChannelBooster(mChanneoInfo.channelIndex, position == 1);
		}
		/*
		@Override
		protected void onUpdateValue(ChannelInformation channel) {
			super.onUpdateValue(channel);
			TvManagerHelper tm = TvManagerHelper.getInstance(mContext);
			boolean booster = tm.isAtvChannelBoosterEnabled(mChanneoInfo.channelIndex);
			setPosition(booster? 1: 0, false);
		}*/
	}
	
	// -----------------------------------------------------------------------------------
	private static class Configuration {
		
		private final Item[] items;
		Configuration() {
			items = new Item[]{
				new ItemSystem(),
				new ItemPos(),
				new ItemColor(),
				new ItemSkip(),
				new ItemSearch(),
				new ItemFineTune(),
				new ItemSigBooster()
			};
		}
		
		void init(Context context) {
			for (Item i : items) {
				i.init(context, this);
			}
		}
		
		void bindView(View root, TextView hint) {
			for (Item i : items) {
				View v = root.findViewById(i.id);
				i.bindView(v, hint);
			}
		}
		
		void unbindView() {
			for (Item i : items) {
				i.unbindView();
			}
		}
		/*
		void updateValue(ChannelInformation channel) {
			for (Item i : items) {
				i.updateValue(channel);
			}
		}*/
		
		Item getItemPosition() {
			return items[0];
		}
	}
	
	// -------------------------------------------------------------------------------------
	private Configuration mConfigs = new Configuration();
	
	private TvManagerHelper mTvManager;
	private BroadcastReceiver mBroadcastReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mTvManager = TvManagerHelper.getInstance(getActivity());		
		mConfigs.init(getActivity());
		/*mBroadcastReceiver = new BroadcastReceiver() {		
			@Override
			public void onReceive(Context context, Intent intent) {
				final String action = intent.getAction();
				if (TvBroadcastDefs.ACTION_TV_DISPLAY_READY.equals(action) ||
					TvBroadcastDefs.ACTION_TV_NO_SIGNAL.equals(action)) {
					// on channel changed
					ChannelInformation channel = mTvManager.getCurrentChannelInformation();
					mConfigs.updateValue(channel);
					
				} else if (TvBroadcastDefs.ACTION_TV_MEDIA_MESSAGE.equals(action)) {
					// on receive tv message
					int msg = intent.getIntExtra(TvBroadcastDefs.EXTRA_TV_MEDIA_MESSAGE, -1);
					if(msg == TvManager.TV_MEDIA_MSG_SCAN_SEEK_COMPLETE || 
						msg == TvManager.TV_MEDIA_MSG_SCAN_MANUAL_COMPLETE) {
						// Scan complete
						ChannelInformation channel = mTvManager.getCurrentChannelInformation();
						mConfigs.updateValue(channel);
					}
				}
			}
		};
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(TvBroadcastDefs.ACTION_TV_DISPLAY_READY);
		intentFilter.addAction(TvBroadcastDefs.ACTION_TV_NO_SIGNAL);
		intentFilter.addAction(TvBroadcastDefs.ACTION_TV_MEDIA_MESSAGE);
		getActivity().registerReceiver(mBroadcastReceiver,  intentFilter);*/
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(mBroadcastReceiver);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_fragment_atv_manual_tuning_setting, container, false);
		TextView textHint = (TextView) view.findViewById(R.id.text_hint);
		mConfigs.bindView(view, textHint);	
		// Get arguments
		Bundle args = getArguments();
		int channelIdx;
		if (args != null) {
			channelIdx = args.getInt(ARG_INDEX, 0);
		} else {
			ChannelInfo info = mTvManager.getCurrentChannelInfo();
			if(info!=null)
				channelIdx = info.getIndex();
		}
		//mConfigs.updateValue(mTvManager.getChannelAt(channelIdx));
		return view;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mConfigs.unbindView();
	}

}
