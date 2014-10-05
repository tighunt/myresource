package com.realtek.dmr;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.os.Handler;
import android.os.Message;
import java.util.Date;


import android.view.View;

public class QuickMenu extends PopupWindow
{
	String TAG = "QuickMenu";
	private Context mContext = null;
	private RelativeLayout mlayout = null;
	private Resources mResourceMgr = null; 
	private ListView mContent = null;
	private LayoutInflater mInflater = null;
	private int mHeight,mWidth;
	private Handler mCheckTimerHandler = null;
	private long mLastControlTime = 0l;
	private int mTimeOut = 6000;
	private int isActivityPause = 0;
	private WindowManager windowManager = null;
	
	QuickMenu(Context context,ListAdapter listviewAdapter, int mWidth, int mHeight, String type) {
		super(context);
		if(type.equals("DMR_VIDEO")) {
			mContext = context;
			windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
			mInflater = LayoutInflater.from(context);
			mlayout = (RelativeLayout) mInflater.inflate(R.layout.video_play_quickmenu, null);
			setContentView(mlayout);
			mContent = (ListView) (mlayout.findViewById(R.id.quick_list));
			mContent.setAdapter(listviewAdapter);
			setFocusable(true);
			setOutsideTouchable(true);
			if(mWidth != -99) {
				this.mWidth = mWidth;
				setWidth(mWidth);
			}
			
			if(mHeight != -99) {
				this.mHeight = mHeight;
				setHeight(mHeight);
			}
			
			mCheckTimerHandler = new Handler(){
				@Override  
		        public void handleMessage(Message msg)  
		        { 
		        	switch (msg.what)  
		            {  
		              case 0: 
		            	  if(isShowing())
		            	  {
		            		  dismiss();
		            	  }
						  break;   
		             default:
		            	 break;
		            }  	          
		          super.handleMessage(msg);  
		        }  
		    };
		}
	}
	QuickMenu(Context context,ListAdapter listviewAdapter)
	{
		super(context);
	    mContext = context;
		windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		mHeight = listviewAdapter.getCount()*60 + 73;
		mWidth  = 868;

		setHeight(mHeight);
		setWidth(mWidth);
		
		mInflater = LayoutInflater.from(mContext);
		mlayout = (RelativeLayout) mInflater.inflate(R.layout.quick, null);
	    
		mContent = (ListView)mlayout.findViewById(R.id.quick_list);
		mContent.setAdapter(listviewAdapter);
	    
		setFocusable(true);
		setContentView(mlayout);
		
		mCheckTimerHandler = new Handler(){
			@Override  
	        public void handleMessage(Message msg)  
	        { 
	        	switch (msg.what)  
	            {  
	              case 0: 
	            	  if(isShowing())
	            	  {
	            		  dismiss();
	            	  }
					  break;   
	             default:
	            	 break;
	            }  	          
	          super.handleMessage(msg);  
	        }  
	    };
	    
	    
		
	}
	public void markOperation()
	{
		mLastControlTime = (new Date(System.currentTimeMillis())).getTime();	
	}
	public void setIsActivityPause(int isPause)
	{
		isActivityPause = isPause;
	}
	public void setQuickMenuTimeOut(int timeOut)
	{
		mTimeOut = timeOut;	
	}

	public ListView getListView()
	{
		return mContent;
	}
	
	void showQuickMenu(int x,int y)
	{
		setFocusable(true);
		setOutsideTouchable(true);
		showAtLocation(mlayout, Gravity.LEFT| Gravity.BOTTOM, x, y);
	}
	
	void showAtRTop(int x,int y,int height)
	{
		if(height>0)
			setHeight(height);
		setFocusable(true);
		setOutsideTouchable(true);
		showAtLocation(mlayout, Gravity.RIGHT| Gravity.TOP, x, y);
	}
	public void mozart_specShow(View anchor) {
		int xPos, yPos;
		int[] location = new int[2];
		anchor.getLocationOnScreen(location);
		Rect anchorRect = new Rect(location[0], location[1], location[0]+ anchor.getWidth(), location[1] + anchor.getHeight());
		//root.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		mlayout.setLayoutParams(new LayoutParams(mWidth,LayoutParams.WRAP_CONTENT));
		mlayout.measure(mWidth, LayoutParams.WRAP_CONTENT);
		int rootHeight = mlayout.getMeasuredHeight();
		int rootWidth = mlayout.getMeasuredWidth();
		int screenWidth = windowManager.getDefaultDisplay().getWidth();
		int screenHeight = windowManager.getDefaultDisplay().getHeight();
		Log.v(TAG, "rootHeight  := " + rootHeight);
		Log.v(TAG, "rootWidth := " + rootWidth);
		if ((anchorRect.left + rootWidth) > screenWidth) {
            xPos = anchorRect.left - (rootWidth - anchor.getWidth());
            Log.v(TAG, "anchorRect.left := " + anchorRect.left + "||anchor.getWidth() := " + anchor.getWidth()
            		+ "xPos := " + xPos);
        } else {
            if (anchor.getWidth() > rootWidth) {
                xPos = anchorRect.centerX() - (rootWidth / 2);
            } else {
                xPos = anchorRect.left;
            }
        }
		
		int dyTop = anchorRect.top;
        int dyBottom = screenHeight - anchorRect.bottom;

        boolean onTop = (dyTop > dyBottom) ? true : false;

        if (onTop) {
            if (rootHeight > dyTop) {
            	Log.e("NotAble SHOW", "NotAble SHOW");
                return ;
            } else {
                yPos = anchorRect.top - rootHeight;
            }
        } else {
            yPos = anchorRect.bottom;
	
            if (rootHeight > dyBottom) {
            	Log.e("NotAble SHOW", "NotAble SHOW");
                return ;
            }
            
            yPos += 10;
        }
        
        showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
	}
	public  void setTimeout(){
		mLastControlTime = (new Date(System.currentTimeMillis())).getTime();
		new Thread(new Runnable() {
    		public void run() {
    			long curtime = 0;
    			while(true)
    			{
    				if(isShowing() == false || isActivityPause == 1)
    					break;
    				curtime = (new Date(System.currentTimeMillis())).getTime();
    				if(curtime - mLastControlTime > mTimeOut)
	    			{
	    				Message msg = new Message();
	    				msg.what = 0;
	    				mCheckTimerHandler.sendMessage(msg);
	    			}
    				 
    				try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}	
    			}
    		}
    	}).start();
		
	}
	public int getHeight()
	{
		return mHeight;
	}
	public int getWidth()
	{
		return mWidth;
	}

	public void setNameResource(int resID)
	{
		mResourceMgr.getString(resID);
	}

	public void AddOnItemClickListener(OnItemClickListener quickmenuItemClickListener)
	{
		mContent.setOnItemClickListener(quickmenuItemClickListener);
	}
	public void AddOnItemSelectedListener(OnItemSelectedListener quickmenuItemSelectedListener)
	{
		mContent.setOnItemSelectedListener(quickmenuItemSelectedListener);
	}
	public void AddOnKeyClickListener(OnKeyListener quickmenuKeyClickListener)
	{
		mContent.setOnKeyListener(quickmenuKeyClickListener);
	}
	
}

