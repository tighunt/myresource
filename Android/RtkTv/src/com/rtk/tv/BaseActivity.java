package com.rtk.tv;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import com.rtk.tv.fragment.BaseFragment;
import com.rtk.tv.fragment.BaseFragment.OnFragmentTransactionListener;
import com.rtk.tv.fragment.MainMenuFragment;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseActivity extends Activity implements OnFragmentTransactionListener{
	public static final String ACTION_QUICK_SETUP = "com.realtek.tv.action.QUICK_SETUP";

	public static final String TAG_LITE = "lite";// info bar, channel input,
													// epg, source list, channel
													// list, source auto swich,
													// subtitle, audio mode
													// list,pvr control
	public static final String TAG_DIALOG = "dialog";
	public static final String TAG_NO_DISPLAY = "no_display";
	public static final String STACK_MENU = "menu";
	public static final String STACK_QUICK_SETUP = "quick";
	public static final String STACK_LITE = "lite";
	
	private List<BaseFragment> mFragments = new ArrayList<BaseFragment>();
	private Fragment currentFragment = null;
	
	public static final int HIDE_CURRENT_FRAGMENT = 0;
	//private static final int HIDE_CURRENT_FRAGMENT = 0;
	
	public static final int fragmentTimeout = 10000;
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// Intercept key events for attached fragments
		int count = mFragments.size();
		for (int i = count - 1; i >= 0; i--) {
			BaseFragment f = mFragments.get(i);
			if (!f.isDetached() && event.dispatch(f, null, f)) {
				resetHideFragment();
				return true;
			}
		}
		return super.dispatchKeyEvent(event);
	}

	public void resetHideFragment(){
		getMessageHandler().removeMessages(HIDE_CURRENT_FRAGMENT);
		getMessageHandler().sendEmptyMessageDelayed(HIDE_CURRENT_FRAGMENT,fragmentTimeout);
	}
	
	private Handler MessageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case HIDE_CURRENT_FRAGMENT:
				hideCurrentFragment();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
	public Handler getMessageHandler() {
		return MessageHandler;
	}
	public Fragment getCurrentFragment() {
		return currentFragment;
	}
	public void setCurrentFragment(Fragment currentFragment) {
		this.currentFragment = currentFragment;
	}
	
	public boolean hideCurrentFragment() {
		FragmentManager fm = getFragmentManager();
		if (currentFragment != null) {
			if(currentFragment instanceof MainMenuFragment){
				fm.popBackStack(STACK_MENU,
					FragmentManager.POP_BACK_STACK_INCLUSIVE);
				fm.beginTransaction().remove(currentFragment).commit();
				currentFragment = null;
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void onFragmentResume(BaseFragment fragment) {
		mFragments.add(fragment);
	}

	@Override
	public void onFragmentPause(BaseFragment fragment) {
		mFragments.remove(fragment);
	}
}
