package com.rtk.tv.fragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import com.rtk.tv.R;
import com.rtk.tv.RtkTvView;
import com.rtk.tv.TvManagerHelper;
import com.rtk.tv.utils.Keymap;

public class SubtitleSwitchFragment extends BaseFragment {
	private static final long DELAY_DISMISS = 3000;
	private TvManagerHelper mTvManager;
	private Handler mHandler;
	private TextView mTextView;
	private List<String> mListSub;
	private int mCurrentIndex = -1;	
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
		// Get subtitle list
		mListSub = mTvManager.getCurrentSubtitles();
		if (mTvManager.isSubtitleEnabled()) {
			mCurrentIndex = mTvManager.getCurrentSubtitleIndex();
		} else {
			mCurrentIndex = -1;
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mHandler.postDelayed(mRunDismiss , DELAY_DISMISS);
		updateView();
	}

	@Override
	public void onPause() {
		super.onPause();
		mHandler.removeCallbacks(mRunDismiss);
	}

	private void updateView() {
		if (mListSub.isEmpty()) {
			mTextView.setText(R.string.msg_no_subtitles);
			
		} else if (mCurrentIndex == -1) {
			mTextView.setText(R.string.msg_subtitle_disabled);
			
		} else if (mCurrentIndex >= 0 && mCurrentIndex < mListSub.size()) {
			mTextView.setText(mListSub.get(mCurrentIndex));
			
		}		
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case Keymap.KEYCODE_SWITCH_SUBTITLE:
			performSwitch();
			updateView();
			return true;
		default:
			return false;
		}
	}

	private void performSwitch() {
		int next = mCurrentIndex + 1;
		if (next >= mListSub.size()) {
			mCurrentIndex = -1;
			mTvManager.setSubtitleEnable(false);
		} else {
		    mCurrentIndex = mTvManager.setCurrentSubtitle(next);
			mTvManager.setSubtitleEnable(true);
		}
	}
}
