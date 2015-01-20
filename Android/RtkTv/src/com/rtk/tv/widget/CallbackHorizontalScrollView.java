package com.rtk.tv.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

public class CallbackHorizontalScrollView extends HorizontalScrollView {
	
	public interface OnScrollChangedListener {
		public void onScrollChanged(int l, int t, int oldl, int oldt);
	}

	private OnScrollChangedListener mListener;

	public CallbackHorizontalScrollView(Context context) {
		this(context, null, 0);
	}


	public CallbackHorizontalScrollView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CallbackHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
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


	public static void bindScrollView(final CallbackHorizontalScrollView sv1,
			final CallbackHorizontalScrollView sv2) {
		sv1.setOnScrollChangedListener(new OnScrollChangedListener() {
			
			@Override
			public void onScrollChanged(int l, int t, int oldl, int oldt) {
				OnScrollChangedListener tmp = sv2.mListener;
				sv2.mListener = null;
				sv2.scrollTo(l, t);
				sv2.mListener = tmp;
			}
		});
		sv2.setOnScrollChangedListener(new OnScrollChangedListener() {
			
			@Override
			public void onScrollChanged(int l, int t, int oldl, int oldt) {
				OnScrollChangedListener tmp = sv1.mListener;
				sv1.mListener = null;
				sv1.scrollTo(l, t);
				sv1.mListener = tmp;
			}
		});
	}

}
