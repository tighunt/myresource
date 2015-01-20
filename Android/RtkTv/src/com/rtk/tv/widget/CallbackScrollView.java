package com.rtk.tv.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ScrollView;

public class CallbackScrollView extends ScrollView {
	
	public interface OnScrollChangedListener {
		public void onScrollChanged(int l, int t, int oldl, int oldt);
	}

	private OnScrollChangedListener mListener;

	public CallbackScrollView(Context context) {
		super(context);
	}


	public CallbackScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CallbackScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}


	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (mListener != null) {
			mListener.onScrollChanged(l, t, oldl, oldt);
		}
	}


	public void setOnScrollChangedListener(OnScrollChangedListener listener) {
		mListener = listener;
	}


	public static void bindListView(final CallbackScrollView sv, final ListView lv) {
		final AbsListView.OnScrollListener listScroll = new AbsListView.OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				OnScrollChangedListener tmp = sv.mListener;
				sv.mListener = null;
				sv.scrollTo(lv.getScrollX(), lv.getScrollY());
				sv.mListener = tmp;
			}
		};
		lv.setOnScrollListener(listScroll);
		sv.setOnScrollChangedListener(new OnScrollChangedListener() {
			
			@Override
			public void onScrollChanged(int l, int t, int oldl, int oldt) {
				lv.setOnScrollListener(null);
				lv.scrollTo(l, t);
				lv.setOnScrollListener(listScroll);
			}
		});
	}

}
