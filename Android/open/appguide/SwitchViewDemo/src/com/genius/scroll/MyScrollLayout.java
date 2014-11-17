package com.genius.scroll;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.genius.demo.MainActivity;

public class MyScrollLayout extends ViewGroup {

	private static final String TAG = "MyScrollLayout";

	/** 用于判断甩动手势 **/
	private VelocityTracker mVelocityTracker;

	private static final int SNAP_VELOCITY = 600;

	/** 滑动控制器 **/
	private Scroller mScroller;

	private int mCurScreen;

	private int mDefaultScreen = 0;

	private float mLastMotionX;

	/** 当前页码 **/
	private int currentPage;

	/** 总页码 **/
	private int pagesize;

	private Context mContext;// 上下文对象

	private OnViewChangeListener mOnViewChangeListener;// View滚动监听器

	/** 标注是否跳转到主界面了 **/
	private boolean go_main = false;

	private SharedPreferences preferences;

	public MyScrollLayout(Context context) {
		super(context);
		this.mContext = context;
		init(context);
	}

	public MyScrollLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init(context);
	}

	public MyScrollLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		init(context);
	}

	/**
	 * 获取当前页码
	 * 
	 * @param page
	 */
	public void setPosition(int page) {
		if (page > 0) {
			currentPage = page;
		}
	}

	/**
	 * 获取总页数
	 * 
	 * @param size
	 */
	public void setPageSize(int size) {
		pagesize = size;
	}

	private void init(Context context) {
		mCurScreen = mDefaultScreen;

		mScroller = new Scroller(context);
	}

	/**
	 * 调用场景：在view给其孩子设置尺寸和位置时被调用 参数说明： 参数一：view有新的尺寸或位置； 参数二：相对于父view的Left位置；
	 * 参数三：相对于父view的Top位置； 参数四：相对于父view的Right位置； 参数五：相对于父view的Bottom位置.
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (changed) {// 如果有新的尺寸或位置
			int childLeft = 0;
			final int childCount = getChildCount();// 返回子View的总数

			for (int i = 0; i < childCount; i++) {// 遍历子View
				final View childView = getChildAt(i);
				if (childView.getVisibility() != View.GONE) {

					// childView.getMeasuredWidth()對View上的內容進行測量後得到的View內容佔據的寬度，前提是你必須在父佈局的
					// onLayout()方法或者此View的onDraw()方法裡調用measure(0,0);(measure
					// 參數的值你可以自己定義)，否則你得到的結果和getWidth()得到的結果一樣
					final int childWidth = childView.getMeasuredWidth();

					// 参数(相对于父布局的左、上、右、下的位置)
					childView.layout(childLeft, 0, childLeft + childWidth,
							childView.getMeasuredHeight());

					childLeft += childWidth;
				}
			}
		}
	}

	/**
	 * 指明控件可获得的空间以及关于这个空间描述的元数据.
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		// MeasureSpec封装了父布局传递给子布局的布局要求，每个MeasureSpec代表了一组宽度和高度的要求。一个MeasureSpec由大小和模式组成
		final int width = MeasureSpec.getSize(widthMeasureSpec);// 根据提供的测量值提取大小值
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);// 根据提供的测量值(格式)提取模式(有三种模式)
		Log.d(TAG, "onMeasure width:" + width + " widthMode:" + widthMode);

		final int count = getChildCount();// 获取子视图总数

		for (int i = 0; i < count; i++) {
			// 调用子视图
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}

		scrollTo(mCurScreen * width, 0);

	}

	public void snapToDestination() {
		final int screenWidth = getWidth();

		final int destScreen = (getScrollX() + screenWidth / 2) / screenWidth;
		snapToScreen(destScreen);
	}

	public void snapToScreen(int whichScreen) {

		// 得到有效的页面布局
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		if (getScrollX() != (whichScreen * getWidth())) {

			final int delta = whichScreen * getWidth() - getScrollX();

			mScroller.startScroll(getScrollX(), 0, delta, 0,
					Math.abs(delta) * 2);

			mCurScreen = whichScreen;
			invalidate(); // 重绘布局

			if (mOnViewChangeListener != null) {
				mOnViewChangeListener.OnViewChange(mCurScreen);
			}
		}
	}

	/**
	 * View绘制流程draw()中会调用
	 */
	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {// 如果返回true，表示动画还没有结束
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());// 产生平滑的动画效果，根据当前偏移量，每次滚动一点
			postInvalidate();// 此时同样也需要刷新View ，否则效果可能有误差
		} else {// 如果返回false，表示startScroll完成
			Log.i(TAG, "scoller has finished");
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		final int action = event.getAction();
		final float x = event.getX();
		final float y = event.getY();
		Log.d(TAG, "X:" + x + " Y:" + y);

		float oldx = 0;
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			oldx = event.getRawX();
			Log.i("", "onTouchEvent  ACTION_DOWN");

			if (mVelocityTracker == null) {
				mVelocityTracker = VelocityTracker.obtain();
				mVelocityTracker.addMovement(event);
			}

			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}

			mLastMotionX = x;
			break;

		case MotionEvent.ACTION_MOVE:
			int deltaX = (int) (mLastMotionX - x);
			float newX = event.getRawX();

			/**
			 * 最后一页跳转 by 木木
			 */
			if (newX - oldx > 3) {

				if (currentPage == pagesize - 1 && !go_main) {
					go_main = true;

					preferences = mContext.getSharedPreferences("setting", 0);
					SharedPreferences.Editor editor = preferences.edit();
					editor.putBoolean("isOpen", true);
					editor.commit();

					Intent intent = new Intent();
					intent.setClass(mContext, MainActivity.class);
					mContext.startActivity(intent);
				}
			}
			if (IsCanMove(deltaX)) {
				if (mVelocityTracker != null) {
					mVelocityTracker.addMovement(event);
				}

				mLastMotionX = x;

				scrollBy(deltaX, 0);
			}

			break;

		case MotionEvent.ACTION_UP:

			int velocityX = 0;
			if (mVelocityTracker != null) {
				mVelocityTracker.addMovement(event);
				mVelocityTracker.computeCurrentVelocity(1000);
				velocityX = (int) mVelocityTracker.getXVelocity();
			}

			if (velocityX > SNAP_VELOCITY && mCurScreen > 0) {
				// 向左移动
				Log.e(TAG, "snap left");
				snapToScreen(mCurScreen - 1);
			} else if (velocityX < -SNAP_VELOCITY
					&& mCurScreen < getChildCount() - 1) {
				// 向右移动
				Log.e(TAG, "snap right");
				snapToScreen(mCurScreen + 1);
			} else {
				snapToDestination();
			}

			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}

			break;
		}

		return true;
	}

	private boolean IsCanMove(int deltaX) {

		if (getScrollX() <= 0 && deltaX < 0) {
			return false;
		}

		if (getScrollX() >= (getChildCount() - 1) * getWidth() && deltaX > 0) {
			return false;
		}

		return true;
	}

	public void SetOnViewChangeListener(OnViewChangeListener listener) {
		mOnViewChangeListener = listener;
	}

//	/**
//	 * 判断是否开启索引
//	 * @return
//	 */
//	private boolean isOpean(){
//		
//	}
	
}
