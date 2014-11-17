package com.genius.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.genius.scroll.MyScrollLayout;
import com.genius.scroll.OnViewChangeListener;

public class SwitchViewDemoActivity extends Activity implements
		OnViewChangeListener, OnClickListener {

	private MyScrollLayout mScrollLayout;

	private ImageView[] mImageViews;

	private int mViewCount;

	private int mCurSel;
	
	/** Activity对象 **/
	public static Activity MY_ACTIVITY;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		MY_ACTIVITY = this;
		
		init();
	}

	private void init() {
		mScrollLayout = (MyScrollLayout) findViewById(R.id.ScrollLayout);

		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.llayout);

		mViewCount = mScrollLayout.getChildCount();
		mImageViews = new ImageView[mViewCount];

		for (int i = 0; i < mViewCount; i++) {
			mImageViews[i] = (ImageView) linearLayout.getChildAt(i);
			mImageViews[i].setEnabled(true);
			mImageViews[i].setOnClickListener(this);
			mImageViews[i].setTag(i);
		}

		mScrollLayout.setPageSize(mImageViews.length);
		mCurSel = 0;
		mImageViews[mCurSel].setEnabled(false);

		mScrollLayout.SetOnViewChangeListener(this);
	}

	private void setCurPoint(int index) {
		if (index < 0 || index > mViewCount - 1 || mCurSel == index) {
			return;
		}
		
		mImageViews[mCurSel].setEnabled(true);
		mImageViews[index].setEnabled(false);
		mScrollLayout.setPosition(index);
		mCurSel = index;
	}

	@Override
	public void OnViewChange(int view) {
		setCurPoint(view);
	}

	@Override
	public void onClick(View v) {
		int pos = (Integer) (v.getTag());
		setCurPoint(pos);
		mScrollLayout.snapToScreen(pos);
	}
}