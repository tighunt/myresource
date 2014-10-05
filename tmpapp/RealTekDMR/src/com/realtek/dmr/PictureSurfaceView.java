package com.realtek.dmr; 


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class PictureSurfaceView extends SurfaceView implements SurfaceHolder.Callback,Runnable{ 

	private static String TAG="PictureSurfaceView";
	private final static int UPDATETIME = 50;
	private static final int FRAME_TYPE_FADE_OUT = 2;
	private static final int FRAME_TYPE_NO_FADE = 0;
	private static final int FRAME_TYPE_FADE_IN = 1;
	
	private static final int VIEW_NORMAL = 0;
	private static final int VIEW_BEGIN = 1;
	private static final int VIEW_END =2;
	
	private int viewMode = 1;
 
	DisplayMetrics dm;
    
	boolean mbLoop = false; 
	Bitmap bmp_show = null;
	Bitmap bmp_new = null;
	int order = -10;
	boolean isCanvasFilled =false;


	SurfaceHolder mSurfaceHolder = null; 
	int miCount = 0; 
	int y =50; 
	private float rate = 1;

	private float moveX = 0f;
	private float moveY = 0f;
	private boolean isFirst = true;
	private boolean canDrag = false;
	
	//player rotate mode
	private int degrees = 0;
	private final static int ROTATEMODE_0D = 0;
	private final static int ROTATEMODE_90D = 90;
	private final static int ROTATEMODE_180D = 180;
	private final static int ROTATEMODE_270D = 270;
	
	private GestureDetector gestureScanner;
	
	//for effect 
	
	// effect last time
	private int duration;
	// update surfaceview time interval
	private int updateTime;
	
	
	///////////for Fade effect
	// Alpha extream value
		protected static final int ALPHA_MIN = 0;
		protected static final int ALPHA_MAX = 255;

		// effect confirm flag
		protected boolean isFadeRunning = false;


		// target alpha
		private int alphaFrom;

		// origen alpha
		private int alphaTo;

		

		// change of alpha per time
		private int alphaStep;

		// current effect time
		private int fadeTime;

		// current alpha
		private int fadeAlpha;
		
		private int frameType;
		
		///////////for ROTATE_SCALE effect
		////////////////////////common
		protected boolean isRotateScaleRunning = false;
		private int effectTime_of_RotateScale;
		private int effectType_of_RotateScale;
		private int direct_of_RotateScale;
		///////////////////////rotate
		
		private float current_rotate_of_RotateScale;
		private int num_rotate;
		private float rotateStep_of_RotateScale;
		//////////////////////scale
		private float current_scale_of_RotateScale;
		private float scaleStep_of_RotateScale;
	
	public PictureSurfaceView(Context context) { 
		super(context); 
		

		mSurfaceHolder = this.getHolder(); 
		
		setLongClickable(true);
		mSurfaceHolder.addCallback(this); 
		this.setFocusable(true); 
		mbLoop = true; 
	} 
	

	
	public PictureSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        updateTime = UPDATETIME;
        mSurfaceHolder = this.getHolder(); 

		mSurfaceHolder.addCallback(this); 
		this.setFocusable(true); 
		mbLoop = true;

		setLongClickable(true);

		gestureScanner = new GestureDetector(context, new GestureDetector.OnGestureListener(){

			@Override
			public boolean onDown(MotionEvent e) {
				// TODO Auto-generated method stub
				System.out.println("onDown");
				return false;
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				// TODO Auto-generated method stub
				System.out.println("onFling");
				return false;
			}

			@Override
			public void onLongPress(MotionEvent e) {
				// TODO Auto-generated method stub
				System.out.println("onLongPress");
				
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2,
					float distanceX, float distanceY) {
				// TODO Auto-generated method stub
				System.out.println("onScroll");
				return true;
			}

			@Override
			public void onShowPress(MotionEvent e) {
				// TODO Auto-generated method stub
				System.out.println("onShowPress");
				
			}

			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				// TODO Auto-generated method stub
				System.out.println("onSingleTapUp");
				return false;
			}
			
		});
		gestureScanner.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
			
			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {
				// TODO Auto-generated method stub
				System.out.println("double + onSingleTapConfirmed");
				return false;
			}
			
			@Override
			public boolean onDoubleTapEvent(MotionEvent e) {
				// TODO Auto-generated method stub
				System.out.println("double + nDoubleTapEvent");
				return false;
			}
			
			@Override
			public boolean onDoubleTap(MotionEvent e) {
				// TODO Auto-generated method stub
				System.out.println("double + onDoubleTap");
				return false;
			}
		});

    }
	
	public void setHasBitmap(Bitmap bmp,int viewMode,int order)
	{
		System.out.println(order);
		this.viewMode = viewMode;
		this.frameType = viewMode;
		this.effectType_of_RotateScale = viewMode;
		if(bmp != null)
			isCanvasFilled = true;
		else
			isCanvasFilled = false;
		if(this.order<order)
		{
			
			if(viewMode == 0)
			{
				this.bmp_show = bmp;
			}
			if(viewMode == 1)
			{
				this.bmp_show = bmp;
			}
			else if(viewMode == 2)
			{
				this.bmp_new = bmp;
			}
			this.order = order;
		}
		else
		{
			Log.v(TAG, "earlier Bitmap abandoned!");
		}
	}
	public boolean isCanvasFilled()
	{
		return isCanvasFilled;
	}
	public void setScale(float rate)
	{
		this.rate = rate;
	}
	
	public void setMove(float x , float y)
	{
		/*
		switch(degrees)
		{
			case ROTATEMODE_0D:
			{
				this.moveX = x;
				this.moveY = y;
				break;
			}
			case ROTATEMODE_90D:
			{
				this.moveX = y;
				this.moveY = -x;
				break;
			}
			case ROTATEMODE_180D:
			{
				this.moveX = -x;
				this.moveY = -y;
				break;
			}
			case ROTATEMODE_270D:
			{
				this.moveX = -y;
				this.moveY = x;
				break;
			}
		
		}*/
		this.moveX = x;
		this.moveY = y;
	}
	
	public void setRotate(int degrees)
	{
		this.degrees = degrees;
	}

	@Override 
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { 
		
		Log.v(TAG, "surfaceChanged");
	} 
	

	@Override 
	public void surfaceCreated(SurfaceHolder holder) { 

		Log.v(TAG, "surfaceCreated");
		frameType = FRAME_TYPE_FADE_OUT;
		mbLoop = true;
		new Thread(this).start(); 
	} 
	

	@Override 
	public void surfaceDestroyed(SurfaceHolder holder) { 

		Log.v(TAG, "surfaceDestroyed");
		mbLoop = false; 
		isCanvasFilled = false;
	} 
	
 
	@Override 
	public void run() { 
		while (mbLoop){ 
			long start = System.currentTimeMillis();
			synchronized( mSurfaceHolder ){ 
				Draw(); 
			} 
			long end = System.currentTimeMillis();
			try {
				if (end - start < updateTime) {
					Thread.sleep(updateTime - (end - start));
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} 
	} 
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.v(TAG,"onTouchEvent");
		super.onTouchEvent(event);
		
		return gestureScanner.onTouchEvent(event);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
/*		if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			 Animation mAlphaAnimation;
		
	        mAlphaAnimation = new AlphaAnimation(0.1f, 1.0f);  
	        mAlphaAnimation.setDuration(3000);  
	        this.startAnimation(mAlphaAnimation);  
		}
*/
		return super.onKeyDown(keyCode, event);
	}
	
	
	
	public void Draw(){ 
		
		
		Canvas canvas = mSurfaceHolder.lockCanvas(); 
		if (mSurfaceHolder==null || canvas == null) { 
			return; 
		} 
		if (miCount < 100) { 
			miCount++; 
		}else { 
			miCount = 0; 
		} 
		Paint mPaint = new Paint(); 
		mPaint.setAntiAlias(true); 
		mPaint.setColor(Color.BLACK); 
		canvas.drawRect(0, 0, 1920, 1080, mPaint); 
		if(bmp_show ==  null)
		{
			//Todo: loading icon function . 
			;
		}
		else
		{
			float wScale = (float)(getWidth())/(float)(bmp_show.getWidth());
			float hScale = (float)(getHeight())/(float)(bmp_show.getHeight());
			float preScale = 0f;
			float translate_for_scaleX = 0f;
			float translate_for_scaleY = 0f;

			
			if(hScale < wScale)
			{
				//use hScale
				preScale = hScale;
				translate_for_scaleX = getWidth() / 2 - bmp_show.getWidth()*preScale / 2;
			}
			else
			{
				//use wScale
				preScale = wScale;
				translate_for_scaleY = getHeight() / 2 - bmp_show.getHeight()*preScale / 2;
			}
			
			Matrix matrix=new Matrix();
			
	        matrix.postScale(preScale, preScale);
	        matrix.postTranslate(translate_for_scaleX, translate_for_scaleY);

	        matrix.postScale(
	        		rate,
	        		rate,
	        		getWidth()/2 - moveX,
	        		getHeight()/2 - moveY
	        		);
	        matrix.postRotate(degrees,getWidth()/2 - moveX,getHeight()/2 - moveY);
    		
	        matrix.postTranslate(moveX, moveY);
	        
		//	canvas.drawBitmap(bmp_show, matrix, mPaint);
			
		//	effect_Fade(canvas,mPaint);
	        canvas.drawBitmap(bmp_show, matrix, mPaint);
			
		}
		mSurfaceHolder.unlockCanvasAndPost(canvas); 
	}
	
	private void effect_RotateScale(Canvas canvas, Matrix matrix, Paint paint) {
		// TODO Auto-generated method stub
		if(viewMode == VIEW_NORMAL)
		{
			canvas.drawBitmap(bmp_show, matrix, paint);//do nothing,just draw
		}
	}
} 
