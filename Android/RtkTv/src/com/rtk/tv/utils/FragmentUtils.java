
package com.rtk.tv.utils;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentManager.BackStackEntry;
import android.content.Intent;
import android.os.Bundle;

public class FragmentUtils {

	public static void showSubFragment(Fragment parent, Class<? extends Fragment> clazz) {
		showSubFragment(parent, clazz, null);
	}

	public static void showSubFragment(Fragment parent, Class<? extends Fragment> clazz, Bundle args) {
		showSubFragment(parent, clazz.getName(), args);
	}

	public static void showSubFragment(Fragment parent, Class<? extends Fragment> clazz, Bundle args, int requestCode) {
		showSubFragment(parent, clazz.getName(), args, requestCode);
	}
	
	public static void showSubFragment(Fragment parent, String clazz, Bundle args) {
		showSubFragment(parent, clazz, args, 0);
	}	

	public static void showSubFragment(Fragment parent, String clazz, Bundle args, int requestCode) {
		// Instantiate fragment
		Fragment fragment = Fragment.instantiate(parent.getActivity(), clazz, args);
		if (requestCode > 0) {
			fragment.setTargetFragment(fragment, requestCode);
		}

		// Begin transaction
		parent.getFragmentManager().beginTransaction()
		.detach(parent)
		.add(parent.getId(), fragment)
		.addToBackStack(clazz)
		.commit();
	}

	public static void popBackSubFragment(FragmentManager fm, Class<? extends Fragment> clazz) {
		fm.popBackStack(clazz.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}

	public static boolean popBackSubFragment(Fragment subFragment) {
		return popBackSubFragment(subFragment, Activity.RESULT_CANCELED, null);
	}
	
	public static boolean popBackSubFragment(Fragment subFragment, int resultCode, Bundle args) {
		boolean r = subFragment.getFragmentManager().popBackStackImmediate(subFragment.getClass().getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
		if (r && subFragment.getTargetFragment() != null && subFragment.getTargetRequestCode() > 0) {
			Intent intent = new Intent();
			intent.putExtras(args);
			subFragment.getTargetFragment().onActivityResult(subFragment.getTargetRequestCode(), resultCode, intent);
		}
		return r;
	}

	public static void popAllBackStacks(FragmentManager fm) {
		int count = fm.getBackStackEntryCount();
		if (count > 0) {
			BackStackEntry first = fm.getBackStackEntryAt(0);
			fm.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
		}
	}
}
