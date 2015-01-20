package com.rtk.tv.fragment;

import com.rtk.tv.RtkTvView;

import android.app.Activity;
import android.app.Fragment;
import android.view.KeyEvent;

public class BaseFragment extends Fragment implements KeyEvent.Callback {

	public interface OnFragmentTransactionListener {
		public void onFragmentResume(BaseFragment fragment);

		public void onFragmentPause(BaseFragment fragment);
	}

	private OnFragmentTransactionListener mListener;
	private Activity mActivity;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = getActivity();
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mActivity = null;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (mActivity instanceof OnFragmentTransactionListener) {
			mListener = (OnFragmentTransactionListener) mActivity;
			mListener.onFragmentResume(this);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mListener != null) {
			mListener.onFragmentPause(this);
		}
		mListener = null;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Intercept all key events except for the basic navigation keys.
		switch (keyCode) {
		// Navigation (Handled by ViewRootImpl)
		case KeyEvent.KEYCODE_DPAD_LEFT:
		case KeyEvent.KEYCODE_DPAD_RIGHT:
		case KeyEvent.KEYCODE_DPAD_UP:
		case KeyEvent.KEYCODE_DPAD_DOWN:
		case KeyEvent.KEYCODE_TAB:
		// Enter (Handled by views in the view hierarchy)
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_ENTER:
			resetHideFragment();
		// Back (Handled by views in the view hierarchy)
		case KeyEvent.KEYCODE_BACK:
			return false;
		default:
			return true;
		}
	}
	

	
	private void resetHideFragment(){
		if (mActivity instanceof RtkTvView) {
			((RtkTvView) mActivity).resetHideFragment();
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return false;
	}

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		return false;
	}

	@Override
	public boolean onKeyMultiple(int keyCode, int count, KeyEvent event) {
		return false;
	}

}
