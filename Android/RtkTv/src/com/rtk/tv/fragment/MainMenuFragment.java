package com.rtk.tv.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.Locale;

import com.rtk.tv.R;
import com.rtk.tv.fragment.submenu.ApplicationMenuFragment;
import com.rtk.tv.fragment.submenu.BaseMenuFragment;
import com.rtk.tv.fragment.submenu.PictureMenuFragment;
import com.rtk.tv.fragment.submenu.PreferenceMenuFragment;
import com.rtk.tv.fragment.submenu.PvrMenuFragment;
import com.rtk.tv.fragment.submenu.SetupMenuFragment;
import com.rtk.tv.fragment.submenu.SoundMenuFragment;

public class MainMenuFragment extends BaseFragment {

	public static final String ARG_TAB_INDEX = "tab_index";

	private Fragment mCurrentFragment;
	
	private int mLastSelectionId = -1;
	private View mSelectedTab;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_fragment_main_menu, container, false);
		ViewGroup tabHost = (ViewGroup) view.findViewById(R.id.container_tabs);
		for (int i = 0; i < tabHost.getChildCount(); i++) {
			View tab = tabHost.getChildAt(i);
			tab.setOnFocusChangeListener(mOnTabFocused);
			tab.setOnKeyListener(mOnTabPressed);
			tab.setClickable(true);
			tab.setFocusableInTouchMode(true);
		}
		// Get focus tab id
		Bundle args = getArguments();
		if (savedInstanceState != null) {
			// With save instance states
			mLastSelectionId = savedInstanceState.getInt("selection", R.id.btn_tab_picture);
		} else if (args != null && mLastSelectionId < 0) {
			// First create with arguments
			int idx = args.getInt(ARG_TAB_INDEX, 0);
			if (idx < tabHost.getChildCount()) {
				mLastSelectionId = tabHost.getChildAt(idx).getId();
			}
		} else if (mLastSelectionId < 0) {
			// First create
			mLastSelectionId = R.id.btn_tab_picture;
		}// else use retained value
		
		// Request focus
		view.findViewById(mLastSelectionId).requestFocus();
		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("selection", mLastSelectionId);
	}

	private View.OnKeyListener mOnTabPressed = new View.OnKeyListener() {
		
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				switch(keyCode) {
				case KeyEvent.KEYCODE_DPAD_UP:
					ListView lvup = (ListView) v.getRootView().findViewById(R.id.list);
					if (lvup != null) {
						lvup.requestFocus();
						int positionup = lvup.getCount() - 1;
						lvup.setSelection(positionup);
					}
					return true;
				case KeyEvent.KEYCODE_DPAD_DOWN:
					ListView lvdn = (ListView) v.getRootView().findViewById(R.id.list);
					if (lvdn != null) {
						lvdn.requestFocus();
						int positiondn = 0;
						lvdn.setSelection(positiondn);
					}
					return true;
				default:
					return false;
				}
			}
			return false;
		}
	};
	
	private View.OnFocusChangeListener mOnTabFocused = new View.OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus && !isDetached()) {
				// unselect previous tab
				if (mSelectedTab != null) {
					mSelectedTab.setSelected(false);
				}
				
				final int id = v.getId();
				mLastSelectionId = id;
				showChildFragment(id);
				
				// select current tab
				mSelectedTab = v;
				v.setSelected(true);
			}
		}
	};
	
	private BaseMenuFragment createChildFragment(int id) {
		BaseMenuFragment fragment;
		switch(id) {
		case R.id.btn_tab_picture:
		default:
			fragment = new PictureMenuFragment();
			break;
		case R.id.btn_tab_sound:
			fragment = new SoundMenuFragment();
			break;
		case R.id.btn_tab_application:
			fragment = new ApplicationMenuFragment();
			break;
		case R.id.btn_tab_preference:
			fragment = new PreferenceMenuFragment();
			break;
		case R.id.btn_tab_setup:
			fragment = new SetupMenuFragment();
			break;
		case R.id.btn_tab_pvr:
			fragment = new PvrMenuFragment();
			break;
		}
		
		Bundle arg = new Bundle();
		arg.putInt(BaseMenuFragment.ARG_PARENT_ID, id);
		fragment.setArguments(arg);
		return fragment;
	}
	
	private void showChildFragment(int id) {
		FragmentManager fm = getChildFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		String tag = getFragmentTag(id);
		
		// Hide previous fragment
		if (mCurrentFragment != null) {
			if (tag.equals(mCurrentFragment.getTag())) {
				return;
			}
			ft.detach(mCurrentFragment);
		}		
		
		// Show fragment
		BaseMenuFragment fragment = (BaseMenuFragment) fm.findFragmentByTag(tag);
		if (fragment == null) {
			fragment = createChildFragment(id);
			
			ft.add(R.id.container_menu, fragment, tag);
		} else {
			ft.attach(fragment);
		}
		ft.commit();
		mCurrentFragment = fragment;
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_MENU:
			getFragmentManager().popBackStack();
			return true;
		default:
			return super.onKeyDown(keyCode, event);
		}
	}

	private static final String getFragmentTag(int id) {
		return String.format(Locale.US, "menu::%d", id);
	}
}
