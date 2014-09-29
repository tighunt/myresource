package com.markupartist.android.widget;



import com.markupartist.android.example.pulltorefresh.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class PullToRefreshListView extends ListView implements OnScrollListener {

    private static final int TAP_TO_REFRESH = 1;     // 初始状态
    private static final int PULL_TO_REFRESH = 2;    //拉动刷新
    private static final int RELEASE_TO_REFRESH = 3;  //释放刷新
    private static final int REFRESHING = 4;    //正在刷新

    private static final String TAG = "PullToRefreshListView";
    //刷新接口
    private OnRefreshListener mOnRefreshListener;

    //箭头图片
    private static  int REFRESHICON = R.drawable.goicon; 
   
    /**
     * listview 滚动监听器
     */
    private OnScrollListener mOnScrollListener;
    
    //视图索引器
    private LayoutInflater mInflater;
    /**
     * 头部视图  内容  -- start
     */
    private RelativeLayout mRefreshView;
    private TextView mRefreshViewText;
    private ImageView mRefreshViewImage;
    private ProgressBar mRefreshViewProgress;
    private TextView mRefreshViewLastUpdated;
    /**
     * 头部视图  内容  -- end
     */
    //当前listivew 的滚动状态
    private int mCurrentScrollState;
    
    //当前listview 的刷新状态
    private int mRefreshState;

    //动画效果
    //变为向下的箭头
    private RotateAnimation mFlipAnimation;
    //变为逆向的箭头
    private RotateAnimation mReverseFlipAnimation;
    //头视图的高度
    private int mRefreshViewHeight;
    //头视图 原始的 top padding 属性值
    private int mRefreshOriginalTopPadding;
    //
    private int mLastMotionY;
    //是否反弹
    private boolean mBounceHack;

    public PullToRefreshListView(Context context) {
        super(context);
        init(context);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        // Load all of the animations we need in code rather than through XML
    	//初始化动画
    	//
        mFlipAnimation = new RotateAnimation(0, -180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mFlipAnimation.setInterpolator(new LinearInterpolator());
        mFlipAnimation.setDuration(250);
        mFlipAnimation.setFillAfter(true);
        
        
        
        mReverseFlipAnimation = new RotateAnimation(-180, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
        mReverseFlipAnimation.setDuration(250);
        mReverseFlipAnimation.setFillAfter(true);

        mInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

		mRefreshView = (RelativeLayout) mInflater.inflate(R.layout.pull_to_refresh_header, this, false);//(R.layout.pull_to_refresh_header, null);
		  mRefreshViewText =
            (TextView) mRefreshView.findViewById(R.id.pull_to_refresh_text);
        mRefreshViewImage =
            (ImageView) mRefreshView.findViewById(R.id.pull_to_refresh_image);
        mRefreshViewProgress =
            (ProgressBar) mRefreshView.findViewById(R.id.pull_to_refresh_progress);
        mRefreshViewLastUpdated =
            (TextView) mRefreshView.findViewById(R.id.pull_to_refresh_updated_at);

        mRefreshViewImage.setMinimumHeight(50);
        mRefreshView.setOnClickListener(new OnClickRefreshListener());
        mRefreshOriginalTopPadding = mRefreshView.getPaddingTop();

        mRefreshState = TAP_TO_REFRESH;

        addHeaderView(mRefreshView);

        super.setOnScrollListener(this);

        measureView(mRefreshView);
        mRefreshViewHeight = mRefreshView.getMeasuredHeight();  //获取头文件的测量高度
    }

    @Override
    protected void onAttachedToWindow() {
        setSelection(1);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);

        setSelection(1);
    }

    /**
     * Set the listener that will receive notifications every time the list
     * scrolls.
     * 
     * @param l The scroll listener. 
     */
    @Override
    public void setOnScrollListener(AbsListView.OnScrollListener l) {
        mOnScrollListener = l;
    }

    /**
     * Register a callback to be invoked when this list should be refreshed.
     * 
     * @param onRefreshListener The callback to run.
     */
    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    /**
     * Set a text to represent when the list was last updated. 
     * @param lastUpdated Last updated at.
     */
    public void setLastUpdated(CharSequence lastUpdated) {
        if (lastUpdated != null) {
            mRefreshViewLastUpdated.setVisibility(View.VISIBLE);
            mRefreshViewLastUpdated.setText(lastUpdated);
        } else {
            mRefreshViewLastUpdated.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	//当前手指的Y值
        final int y = (int) event.getY();
        
        //Log.i(TAG, "触屏的Y值"+y);
        mBounceHack = false;  //不反弹
        
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
            	//将垂直滚动条设置为可用状态
                if (!isVerticalScrollBarEnabled()) {
                    setVerticalScrollBarEnabled(true);
                }
                
                //如果头部刷新条出现，并且不是正在刷新状态
                if (getFirstVisiblePosition() == 0 && mRefreshState != REFRESHING) {
                    if ((mRefreshView.getBottom() >= mRefreshViewHeight
                            || mRefreshView.getTop() >= 0)
                            && mRefreshState == RELEASE_TO_REFRESH) {   //如果头部视图处于拉离顶部的情况
                        // Initiate the refresh
                        mRefreshState = REFRESHING;  //将标量设置为，正在刷新
                        prepareForRefresh();  //准备刷新
                        onRefresh();   //刷新
                    } else if (mRefreshView.getBottom() < mRefreshViewHeight
                            || mRefreshView.getTop() <= 0) {
                        // Abort refresh and scroll down below the refresh view
                    	// 停止刷新，并且滚动到头部刷新视图的下一个视图
                        resetHeader();
                        setSelection(1);  //定位在第二个列表项
                    }
                }
                break;
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = y;  //跟踪手指的Y值
                break;
            
            case MotionEvent.ACTION_MOVE:
            	//更行头视图的toppadding 属性
                applyHeaderPadding(event);
                break;
        }
        return super.onTouchEvent(event);
    }

    /****
     * 不断的头部的top padding 属性
     * @param ev
     */
    private void applyHeaderPadding(MotionEvent ev) {
        //获取累积的动作数
        int pointerCount = ev.getHistorySize();
       // Log.i(TAG, "获取累积的动作数"+pointerCount);
        for (int p = 0; p < pointerCount; p++) {
            if (mRefreshState == RELEASE_TO_REFRESH) {    //如果是释放将要刷新状态
                if (isVerticalFadingEdgeEnabled()) {   
                    setVerticalScrollBarEnabled(false);
                }
                //历史累积的高度
                int historicalY = (int) ev.getHistoricalY(p);
                //Log.i(TAG, "单个动作getHistoricalY值："+historicalY);
                // Calculate the padding to apply, we divide by 1.7 to
                // simulate a more resistant effect during pull.
                int topPadding = (int) (((historicalY - mLastMotionY)
                        - mRefreshViewHeight) / 1.7);
                
                mRefreshView.setPadding(
                        mRefreshView.getPaddingLeft(),
                        topPadding,
                        mRefreshView.getPaddingRight(),
                        mRefreshView.getPaddingBottom());
            }
        }
    }

    /**
     * Sets the header padding back to original size.
     * 使头部视图的 toppadding 恢复到初始值
     */
    private void resetHeaderPadding() {
        mRefreshView.setPadding(
                mRefreshView.getPaddingLeft(),
                mRefreshOriginalTopPadding,
                mRefreshView.getPaddingRight(),
                mRefreshView.getPaddingBottom());
    }

    /**
     * Resets the header to the original state.  
     *  初始化头部视图 状态
     */
    private void resetHeader() {
        if (mRefreshState != TAP_TO_REFRESH) {
            mRefreshState = TAP_TO_REFRESH; //初始刷新状态
            //使头部视图的 toppadding 恢复到初始值
            resetHeaderPadding();
            // Set refresh view text to the pull label
            //将文字初始化
            mRefreshViewText.setText(R.string.pull_to_refresh_tap_label);
            // Replace refresh drawable with arrow drawable
            //设置初始图片
            mRefreshViewImage.setImageResource(REFRESHICON);
            // Clear the full rotation animation
            // 清除动画
            mRefreshViewImage.clearAnimation();
            // Hide progress bar and arrow.
            //隐藏头视图
            mRefreshViewImage.setVisibility(View.GONE);
            //隐藏进度条
            mRefreshViewProgress.setVisibility(View.GONE);
        }
    }

    
    //测量视图的高度
    private void measureView(View child) {
    	//获取头部视图属性
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0,
                0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        
        //不懂MeasureSpec------------------------------------------------------------------------------------------
        if (lpHeight > 0) {  //如果视图的高度大于0
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);   
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
        //不懂MeasureSpec------------------------------------------------------------------------------------------
    }

    /****
     * 滑动事件
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
        // When the refresh view is completely visible, change the text to say
        // "Release to refresh..." and flip the arrow drawable.
        if (mCurrentScrollState == SCROLL_STATE_TOUCH_SCROLL   //如果是接触滚动状态,并且不是正在刷新的状态
                && mRefreshState != REFRESHING) {
            if (firstVisibleItem == 0) {    //如果显示出来了第一个列表项
            	//显示刷新图片
                mRefreshViewImage.setVisibility(View.VISIBLE);
                if ((mRefreshView.getBottom() >= mRefreshViewHeight + 20
                        || mRefreshView.getTop() >= 0)
                        && mRefreshState != RELEASE_TO_REFRESH) {  //如果下拉了listiview,则显示上拉刷新动画
                    mRefreshViewText.setText(R.string.pull_to_refresh_release_label);
                    mRefreshViewImage.clearAnimation();
                    mRefreshViewImage.startAnimation(mFlipAnimation);
                    mRefreshState = RELEASE_TO_REFRESH;
                    
                    Log.i(TAG, "现在处于下拉状态");
                    
                } else if (mRefreshView.getBottom() < mRefreshViewHeight + 20
                        && mRefreshState != PULL_TO_REFRESH) {    //如果没有到达，下拉刷新距离，则回归原来的状态
                    mRefreshViewText.setText(R.string.pull_to_refresh_pull_label);
                    if (mRefreshState != TAP_TO_REFRESH) {
                        mRefreshViewImage.clearAnimation();
                        mRefreshViewImage.startAnimation(mReverseFlipAnimation);
                        
                        Log.i(TAG, "现在处于回弹状态");
                        
                    }
                    mRefreshState = PULL_TO_REFRESH;
                }
            } else {   
                mRefreshViewImage.setVisibility(View.GONE);  //隐藏刷新图片
                resetHeader();   //初始化，头部
            }
        } else if (mCurrentScrollState == SCROLL_STATE_FLING  //如果是自己滚动状态+ 第一个视图已经显示 + 不是刷新状态
                && firstVisibleItem == 0
                && mRefreshState != REFRESHING) {
            setSelection(1);
            mBounceHack = true;   //状态为回弹
            Log.i(TAG, "现在处于自由滚动到顶部的状态");
        } else if (mBounceHack && mCurrentScrollState == SCROLL_STATE_FLING) {
            setSelection(1);
            Log.i(TAG, "现在处于自由滚动到顶部的状态");
        }

        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(view, firstVisibleItem,
                    visibleItemCount, totalItemCount);
        }
    }

    
    //滚动状态改变
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        mCurrentScrollState = scrollState;

        if (mCurrentScrollState == SCROLL_STATE_IDLE) {   //如果滚动停顿
            mBounceHack = false;
        }

        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    
    
    //准备刷新
    public void prepareForRefresh() {
        resetHeaderPadding();   //初始化，头部文件

        mRefreshViewImage.setVisibility(View.GONE);
        // We need this hack, otherwise it will keep the previous drawable.
        mRefreshViewImage.setImageDrawable(null);
        mRefreshViewProgress.setVisibility(View.VISIBLE);

        // Set refresh view text to the refreshing label
       mRefreshViewText.setText(R.string.pull_to_refresh_refreshing_label);

        mRefreshState = REFRESHING;
    }

    //刷新
    public void onRefresh() {
        Log.d(TAG, "执行刷新");

        if (mOnRefreshListener != null) {
            mOnRefreshListener.onRefresh();
        }
    }

    /**
     * 刷新完成 的回调函数
     * Resets the list to a normal state after a refresh.
     * @param lastUpdated Last updated at.
     */
    public void onRefreshComplete(CharSequence lastUpdated) {
        setLastUpdated(lastUpdated);
        onRefreshComplete(); 
    }

    /**
     *  刷新完成回调函数
     * Resets the list to a normal state after a refresh.
     */
    public void onRefreshComplete() {        
        Log.d(TAG, "onRefreshComplete");

        resetHeader();

        // If refresh view is visible when loading completes, scroll down to
        // the next item.
        if (mRefreshView.getBottom() > 0) {
            invalidateViews();  //重绘视图
            setSelection(1);
        }
    }

    /**
     * Invoked when the refresh view is clicked on. This is mainly used when
     * there's only a few items in the list and it's not possible to drag the
     * list.
     */
    private class OnClickRefreshListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            if (mRefreshState != REFRESHING) {
                //准备刷新
                prepareForRefresh();  
                //刷新   
                onRefresh(); 
            }
        }

    }

    /**
     * 刷新方法接口
     */
    public interface OnRefreshListener {
        
        public void onRefresh();
    }
}