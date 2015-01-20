package com.rtk.tv.fragment;

import com.rtk.tv.R;
import com.rtk.tv.RtkTvView;
import com.rtk.tv.TvManagerHelper;
import com.rtk.tv.data.ChannelInfo;
import com.rtk.tv.data.ProgramInfo;
import com.rtk.tv.utils.Keymap;
import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AudioModeSwitchFragment extends BaseFragment {
	private static final long DELAY_DISMISS = 3000;
	private TvManagerHelper mTvManager;
	private Handler mHandler;
	private TextView mTextView;
	private final Runnable mRunDismiss = new Runnable() {	
		@Override
		public void run() {
		    getFragmentManager().popBackStack(RtkTvView.STACK_LITE, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		}
	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mTvManager = TvManagerHelper.getInstance(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHandler = new Handler();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_fragment_text_upper_right,
				container, false);
		mTextView = (TextView) v.findViewById(R.id.text_upper_right);
		return v;
	}

	@Override
	public void onStart() {
		super.onStart();
		updateView();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		// Post dismiss
		mHandler.postDelayed(mRunDismiss , DELAY_DISMISS);
	}

	@Override
	public void onPause() {
		super.onPause();
		mHandler.removeCallbacks(mRunDismiss);
	}

	private void updateView() {
		ChannelInfo info = mTvManager.getCurrentChannelInfo();
		if(info == null)
			mTextView.setText("");
		else{
			ProgramInfo program = info.findLatestProgram(mTvManager.currentTvTimeMillis());
			if(program!=null)
				mTextView.setText(program.getAudioLanguage());
		}	        
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case Keymap.KEYCODE_SWITCH_AUDIO:
			return true;
		default:
			return false;
		}
	}

}
