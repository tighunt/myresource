
package com.rtk.tv.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class PvrProgressBar extends ProgressBar {
	
	private static final int[] ATTRS = {
		android.R.attr.thumb,
	};
	
	private Drawable mIndicator;
	
	private int mIndicatorPosition = 50;
	private int mRecordDuration = 10;

	public PvrProgressBar(Context context) {
		this(context, null);
	}

	public PvrProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PvrProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray array = context.obtainStyledAttributes(attrs, ATTRS);
		mIndicator = array.getDrawable(0);
		array.recycle();
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		final int width = getWidth();
		
		// Draw progress
		// Note that we should take care of paddings.
		Drawable progress = getProgressDrawable();
		if (progress instanceof LayerDrawable) {
			int position = getProgress();
			int paddedWidth = width - getPaddingLeft() - getPaddingRight();
			
			// Draw background
			LayerDrawable ld = (LayerDrawable) progress;
			Drawable d = ld.findDrawableByLayerId(android.R.id.background);
			d.setBounds(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
			d.draw(canvas);
			
			// Draw progress
			d = ld.findDrawableByLayerId(android.R.id.progress);
			float endR = (float) position / (float) getMax();
			int endX = (int) (paddedWidth * endR);
			d.setBounds(getPaddingLeft(), getPaddingTop(), endX, getHeight() - getPaddingBottom());
			d.draw(canvas);
			
			
			// Draw the record section.
			d = ld.findDrawableByLayerId(android.R.id.secondaryProgress);
			if (d != null && mRecordDuration >= 0) {
				float startR = (float) (position - mRecordDuration) / (float) getMax();
				int startX = (int) (paddedWidth * startR);
				d.setBounds(startX, getPaddingTop(), endX, getHeight() - getPaddingBottom());
				d.draw(canvas);
			}
		}
		
		// Draw indicator
		// Padding is ignored here.
		if (mIndicator != null && mIndicatorPosition >= 0) {
			float ratio = (float) mIndicatorPosition / (float) getMax();
			int offset = (int) (ratio * width);
			int posX = offset - mIndicator.getIntrinsicWidth() / 2;
			
			// Center vertical
			int posY = (getHeight() - mIndicator.getIntrinsicHeight()) / 2;
			mIndicator.setBounds(posX, posY, posX + mIndicator.getIntrinsicWidth(), posY + mIndicator.getIntrinsicHeight());
			mIndicator.draw(canvas);
		}
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// Use Indicator to resolve the height.
		if (mIndicator != null) {
			int indicatorHeight = mIndicator.getIntrinsicHeight();
			int height = resolveSize(indicatorHeight, heightMeasureSpec);
			setMeasuredDimension(getMeasuredWidth(), height);
		}
	}
	
	public void setIndicatorProgress(int position) {
		mIndicatorPosition = position;
		invalidate();
	}

	public void setRecordDuration(int duration) {
		mRecordDuration = duration;
		invalidate();
	}
}
