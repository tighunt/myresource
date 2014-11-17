package com.genius.demo;

import java.util.List;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * ViewPgageAdapter适配器
 * @author jgduan 2013-01-18
 *
 */
public class ViewPageAdapter extends PagerAdapter {

	List<View> mViewList;

	public ViewPageAdapter(List<View> viewList) {
		mViewList = viewList;
	}

	@Override
	public int getCount() {
		if (mViewList != null) {
			return mViewList.size();
		}

		return 0;
	}

	@Override
	public Object instantiateItem(View view, int index) {
		((ViewPager) view).addView(mViewList.get(index), 0);

		return mViewList.get(index);
	}

	@Override
	public void destroyItem(View view, int position, Object arg2) {

		((ViewPager) view).removeView(mViewList.get(position));
	}

	@Override
	public void finishUpdate(View arg0) {

	}

	@Override
	public boolean isViewFromObject(View view, Object obj) {
		return (view == obj);

	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {

	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {

	}

}
