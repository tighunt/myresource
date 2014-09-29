package com.freedie.customerizedview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class MyView extends View {

	public MyView(Context context) {
		super(context);
	}

	public MyView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int measuredHeight = measureHeight(heightMeasureSpec);
		int measuredWidth = measureWidth(widthMeasureSpec);
		//Must make this call to setMeasuredDimension
		//or you will cause a runtime exception when the control is laid out
		setMeasuredDimension(measuredWidth, measuredHeight);
	}

	private int measureWidth(int widthMeasureSpec) {
		int specMode = MeasureSpec.getMode(widthMeasureSpec);
		int specSize = MeasureSpec.getSize(widthMeasureSpec);
		int result = 500;
		if(specMode == MeasureSpec.AT_MOST){
			//calculate the ideal size of your control within this maximum size.
			//if your control fills the available  space return the outer bound.
			result = specSize;
		}else if(specMode == MeasureSpec.EXACTLY){
			//if your control can fit within these bounds return that value
			result = specSize;
		}
		return result;
	}

	private int measureHeight(int heightMeasureSpec) {
		int specMode = MeasureSpec.getMode(heightMeasureSpec);
		int specSize = MeasureSpec.getSize(heightMeasureSpec);
		int result = 500;
		if(specMode == MeasureSpec.AT_MOST){
			result = specSize;
		}else if(specMode == MeasureSpec.EXACTLY){
			result = specSize;
		}
		return result;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		//get the size of the control based on the last call to onMeasure()
		int height = getMeasuredHeight();
		int width = getMeasuredWidth();
		
		//Find the center
		int px = width/2;
		int py = height/2;	
		
		//create the new paint brushes
		//note:For efficiency this should be done in the view's constructor
		Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setColor(Color.WHITE);
		
		//define the string
		String displayText = "Hello World!";
		
		//measure the width of the string
		float textWidth = mTextPaint.measureText(displayText);
		
		canvas.drawText(displayText, px - textWidth/2, py, mTextPaint);
		super.onDraw(canvas);
	}

}
