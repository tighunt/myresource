package com.rtk.mediabrowser;


import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import com.rtk.mediabrowser.MediaApplication.ModifiedConf;

import android.app.Activity;
import android.app.TvManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
//import android.content.ComponentName;


public class MediaBrowser extends Activity {

	private final String TAG = "MediaBrowser";
	//UI control
	float moveBase = 0.0f;//dp
	boolean testDivxUIflag = false;
	//UI control 
	
	
	public static TvManager mTV;
	public int mBrowserType = 0;
	 
    QuickMenu quickmenu = null;
    QuickMenuAdapter qkAdapter = null;	
    
    private MediaApplication map = null;
    public BookMark mVideoBookMark = null;
    
    public Activity mContext = this;
    
    /*************************************//////////ImageButton    
    private ImageButton mImageButton[];
    
    private int mImageButtonFSrc[]={
    		R.drawable.select_icon_photo_focus,
    		R.drawable.select_icon_music_focus,
    		R.drawable.select_icon_video_focus,
    		R.drawable.select_icon_setting_focus};
    private int mImageButtonUFSrc[]={
    		R.drawable.select_icon_photo_unfocus,
    		R.drawable.select_icon_music_unfocus,
    		R.drawable.select_icon_video_unfocus,
    		R.drawable.select_icon_setting_unfocus};
    
    private int mIndex,mIndex_old, maxIndex;
    private ImageView mFocus;
    float m_curX,m_nextX;
    
    private int mBackKeyClickNum = 0;
    
    int mQuickMenuIndex = 0;
    
    private Timer timer = null;
    private TimerTask task_back_key_click_delay = null;
    static private int DelayTime = 150;
    
    private final int HIDE_POPUP_MESSAGE = 0;
    private Handler handler;
    
    private PopupMessage msg_hint = null;
    
    private boolean isRight2Left = false;
    
    private int UsbRemoved = 1;
    
    private int screenHeight = 0;
    private int screenWeight = 0;
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        
        mTV = (TvManager) this.getSystemService("tv");
        //mTV.setSource(TvManager.SOURCE_OSD);
        mTV.setSource(TvManager.SOURCE_PLAYBACK);
        
        map = (MediaApplication)getApplication();
        screenHeight = map.getScreenHeight();
        screenWeight = map.getScreenWidth();
        
        String path = getFilesDir().getPath();
        String fileName = path.concat("/VideoBookMark.bin");
        mVideoBookMark = map.getBookMark(fileName); 
        
        Intent intent= getIntent();
        mBrowserType = intent.getIntExtra("browserType", 0);
        
        isRight2Left = MediaBrowserConfig.getRight2Left(getApplicationContext());
        
        if(mTV.isOpenDivxPlusFlag() || testDivxUIflag){
        	if(isRight2Left)
            	setContentView(R.layout.media_browser_a);
            else
            	setContentView(R.layout.media_browser);
        	maxIndex = 3;
        	if(map.getModifiedConf() == ModifiedConf.SW960_SW540_ANYDENSITY){
        		moveBase = 150.0f;	//dp
        	}
        }else{
        	setContentView(R.layout.media_browser_nodivx);
        	maxIndex = 2;
        	if(map.getModifiedConf() == ModifiedConf.SW960_SW540_ANYDENSITY){
        		moveBase = 150.0f;	//dp
        	}
        }
        
        msg_hint = new PopupMessage(mContext);
        
		mIndex=0;
		mIndex_old=0;
		m_curX = 0.0f;
        mImageButton = new ImageButton[4];
        
        mFocus = (ImageView)findViewById(R.id.mb_focus);        
        
        mImageButton[0] = (ImageButton)findViewById(R.id.mb_photo_img);              
        mImageButton[1] = (ImageButton)findViewById(R.id.mb_music_img);       
        mImageButton[2] = (ImageButton)findViewById(R.id.mb_videos_img);
        
        mImageButton[0].setFocusable(true); 
        mImageButton[1].setFocusable(false);
        mImageButton[2].setFocusable(false);
        
        
        ButtonListener listener = new ButtonListener();
        mImageButton[0].setTag(1);
        mImageButton[0].setOnClickListener(listener);
        mImageButton[1].setTag(2);
        mImageButton[1].setOnClickListener(listener);
        mImageButton[2].setTag(3);
        mImageButton[2].setOnClickListener(listener);
        
        if(mTV.isOpenDivxPlusFlag() || testDivxUIflag){
	        mImageButton[3] = (ImageButton)findViewById(R.id.mb_setting_img);
	        mImageButton[3].setFocusable(false);
	        mImageButton[3].setTag(4);
	        mImageButton[3].setOnClickListener(listener);
        }
        
        
        timer = new Timer(true);
        
        handler = new Handler(){
        	@Override
    		public void handleMessage(Message msg) {
        		switch(msg.what){
        		case HIDE_POPUP_MESSAGE:
        			if(msg_hint != null && msg_hint.isShowing())
        				msg_hint.dismiss();
        		break;
        		}
        	}
        };
        
        
       // createQuickMenu();
        qkAdapter = new QuickMenuAdapter(MediaBrowser.this);
        quickmenu=new QuickMenu(
				MediaBrowser.this, qkAdapter);
        quickmenu.setAnimationStyle(R.style.QuickAnimation);
        OnItemClickListener quickmenuItemClickListener = new OnItemClickListener()
        {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				
				if (position == 0)
				{
					ComponentName componetName = new ComponentName("com.tsb.tv", "com.tsb.tv.Tv_strategy");
					Intent intent= new Intent();
					intent.setComponent(componetName);
					startActivity(intent);
				}
				else
				{
					ComponentName componetName = new ComponentName("com.android.settings", "com.android.settings.Settings");
					Intent intent= new Intent();
					intent.setComponent(componetName);
					startActivity(intent);
				}
			}
        	
        };
        
        OnKeyListener quickmenuKeyListener = new OnKeyListener()
        {
        	@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN)
	        	{
					if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER)
					{
						if (mQuickMenuIndex == 0)
						{
							ComponentName componetName = new ComponentName("com.tsb.tv", "com.tsb.tv.Tv_strategy");
							Intent intent= new Intent();
							intent.setComponent(componetName);
							startActivity(intent);
						}
						else
						{
							ComponentName componetName = new ComponentName("com.android.settings", "com.android.settings.Settings");
							Intent intent= new Intent();
							intent.setComponent(componetName);
							startActivity(intent);
						}
						
						return true;
					}
		        	if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)
		        		return true;
		        	else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT)
		        		return true;	
		        	else if (keyCode == KeyEvent.KEYCODE_DPAD_UP)
		        	{
		        		ListView quickMenuContent = quickmenu.getListView();
		        		int position = quickMenuContent.getSelectedItemPosition();
		        		if(position == 0)
		        		{
		        			quickMenuContent.setSelection(quickMenuContent.getCount()-1);
		        		}
		        		return false;		
		        	}
		        	else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN)
		        	{
		        		ListView quickMenuContent = quickmenu.getListView();
		        		int position = quickMenuContent.getSelectedItemPosition();
		        		if(position == quickMenuContent.getCount()-1)
		        		{
		        			quickMenuContent.setSelection(0);
		        		}
		        		return false;
		        	}
	        	}
				else if (event.getAction() == KeyEvent.ACTION_UP)
				{
					if (keyCode == KeyEvent.KEYCODE_Q||keyCode == 227||keyCode == KeyEvent.KEYCODE_BACK)
		        		quickmenu.dismiss();
					else if(keyCode ==  KeyEvent.KEYCODE_MENU)
					{
						Toast.makeText(MediaBrowser.this
                				  , MediaBrowser.this.getResources().getText(R.string.toast_not_available)
                				  ,Toast.LENGTH_SHORT).show();
					}
				}
				return false;
			}
        	
        };
        OnItemSelectedListener quickmenuItemSelectedListener = new OnItemSelectedListener()
        {

        	@Override
			public void onItemSelected(AdapterView<?> arg0, View view,
					int position, long arg3) {
        		mQuickMenuIndex = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				Log.d(TAG, "Quick Menu ListView onNothingSelected");
			}
        	
        };
        quickmenu.AddOnItemSelectedListener(quickmenuItemSelectedListener);
        quickmenu.AddOnKeyClickListener(quickmenuKeyListener);
        quickmenu.AddOnItemClickListener(quickmenuItemClickListener);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == 0)
		 {
			 if(resultCode == UsbRemoved)
			 {
				if(msg_hint.isShowing())
					msg_hint.dismiss();
				msg_hint.setMessage(mContext.getResources().getString(R.string.device_removed_toast));
				if(isRight2Left)
					msg_hint.setMessageRight();
				else
					msg_hint.setMessageLeft();
     			msg_hint.show();
     			handler.sendEmptyMessageDelayed(HIDE_POPUP_MESSAGE, TimerDelay.delay_4s);
			 }
		 }
	}
	
	@Override
	protected void onDestroy() {
    	Log.v(TAG, "onDestroy");
    	
    	if(map.getStopedFileName() != null)
		{
			  mVideoBookMark.removeBookMark(map.getStopedFileName());
			  map.setStopedFileName(null);
		}
    	
		super.onDestroy();
	}
	
	class QuickMenuAdapter extends BaseAdapter 
    {
		
		public View LastSelectedItem_View = null;
		int[] menu_name = new int[] {R.string.quick_menu_tvapp, R.string.quick_menu_sysset};
        String[] menu_option = new String[] {"", ""};
        
        int[] visibility = new int[]{
	 			View.INVISIBLE,
	 			View.INVISIBLE,
	 	};
        
		private LayoutInflater mInflater;
		
		class ViewHolder {
			TextView menu_name;
			ImageView left;
			TextView menu_option;
			ImageView right;
		}
		
		
		public QuickMenuAdapter(Context context)
    	{
			mInflater = LayoutInflater.from(context);
    	}
		
		
		public void setVisibility(int position,int isVisible)
		{
			visibility[position]=isVisible;				
		}
        
        @Override
        public int getCount() {
            return 2;
        }

        public int getViewTypeCount() {
        	return 1;
        }
        
        public int getItemViewType(int position) {
        	return 0;
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	 ViewHolder holder;
        	
        	if (convertView == null) {
        		
        		if(true == MediaBrowserConfig.getRight2Left(getApplicationContext()))
        			convertView = mInflater.inflate(R.layout.quick_list_row_a, null);
                else
                	convertView = mInflater.inflate(R.layout.quick_list_row, null);
        		
	        	holder = new ViewHolder();
	        	holder.menu_name = (TextView)convertView.findViewById(R.id.menu_name);
	        	holder.menu_option = (TextView)convertView.findViewById(R.id.menu_option);
	        	holder.left = (ImageView)convertView.findViewById(R.id.left_arrow);
	        	holder.right = (ImageView)convertView.findViewById(R.id.right_arrow);
	        	convertView.setTag(holder);
        	} 
        	else 
        	{
        		holder = (ViewHolder)convertView.getTag();
        	}
        	
            holder.menu_name.setText(menu_name[position]);
            holder.menu_option.setText(menu_option[position]);

           	holder.left.setVisibility(visibility[position]);
           	holder.right.setVisibility(visibility[position]);
            
        	return convertView;
        }
        
        @Override
        public Object getItem(int position) {
        	return null;
        }
        	
        @Override
        public long getItemId(int position) {
        	return 0;
        }
    }
	
	public class ButtonListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			ComponentName componetName;
			int tag = (Integer) v.getTag();
			/*if (tag != 4)
			{ 
				if(mBrowserType == 0)
			     {
					 File file = new File("/storage/udisk/");
					 File[] files = file.listFiles(); 
					 int fileSize =files.length;
				 
					 files = null;
					 file=null;
				 
					 if(fileSize == 0)
					 {
						 Toast.makeText(getApplicationContext(), "Please insert external disk!",
	            		     Toast.LENGTH_SHORT).show();
						 return;
					 }
			     }
			}*/

			switch (tag){
				case 1:
					componetName = new ComponentName("com.rtk.mediabrowser",
	                "com.rtk.mediabrowser.GridViewActivity");
					m_nextX = 0.0f;
					mIndex = 0;
					break;
				case 2:
					componetName = new ComponentName("com.rtk.mediabrowser",
					"com.rtk.mediabrowser.AudioBrowser");
					m_nextX = dip2px(getApplicationContext(), moveBase);
					mIndex =1;
					break;
				case 3:
					componetName = new ComponentName("com.rtk.mediabrowser",
					"com.rtk.mediabrowser.VideoBrowser");
					m_nextX = dip2px(getApplicationContext(), moveBase) * 2;
					mIndex = 2;
					break;
				case 4:
					componetName = new ComponentName("com.rtk.mediabrowser",
					"com.rtk.mediabrowser.DivxSetup");
					m_nextX = dip2px(getApplicationContext(), moveBase) * 3;
					mIndex =3;
					break;
				default:
					return;
			}
			
			TranslateAnimation animation_focus = new TranslateAnimation(
                    m_curX,  m_nextX,
                    0.0f,  0.0f);
			m_curX = m_nextX;
    		animation_focus.setDuration(150);
    		animation_focus.setFillAfter(true);
    		mFocus.startAnimation(animation_focus);
    		Log.v(TAG, "ButtonListener mIndex = "+mIndex+" tag = "+tag);
			Intent intent= new Intent();
			Bundle bundle = new Bundle();
			bundle.putInt("browserType", mBrowserType);
			intent.putExtras(bundle);
			intent.setComponent(componetName);
			startActivityForResult(intent, 0);
		}	
	}
	@Override
	public boolean onKeyUp(int keyCode,KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_Q || keyCode == 227)
    	{
    		
    		if(quickmenu.isShowing() == true)
    			quickmenu.dismiss();
    		else
    		{
    			int x = (int) (0.013 * screenHeight);
    			int y = (int) (0.007 * screenWeight);
    			
    			quickmenu.showQuickMenu(x, y);
    			quickmenu.setTimeout();
    		}
    	}
		return super.onKeyUp(keyCode, event);
	}
		
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stubGridViewActivity
    	if (event.getAction() == KeyEvent.ACTION_DOWN)
    	{
    		
        	if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)
        	{
        		mIndex_old = mIndex;
        		if (mIndex == maxIndex){
        			mIndex = 0;
        			m_nextX = 0.0f;
        		}       		
        		else{
        			mIndex++;
        			m_nextX += dip2px(this,moveBase);
        		}   		       		
        		
        		mImageButton[mIndex].setFocusable(true);
        		mImageButton[mIndex_old].setFocusable(false);
        		mImageButton[mIndex_old].setImageResource(mImageButtonUFSrc[mIndex_old]);
        		TranslateAnimation animation_focus = new TranslateAnimation(
                        m_curX,  m_nextX,
                        0.0f,  0.0f);
        		m_curX = m_nextX;
        		animation_focus.setDuration(DelayTime);
        		animation_focus.setFillAfter(true);
        		mFocus.startAnimation(animation_focus);
        		
        		 new Handler().postDelayed(new Runnable(){  
        		     public void run() {  
        		     //execute the task  
        		     mImageButton[mIndex].setImageResource(mImageButtonFSrc[mIndex]);
        		     }  
        		  }, DelayTime); 

        		return true;
        	}
        	else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT)
        	{
        		mIndex_old = mIndex;
        		if (mIndex == 0){
        			mIndex = maxIndex;
        			m_nextX = dip2px(this, moveBase * maxIndex);   			
        		}
        		else{
        			mIndex--;
        			m_nextX -= dip2px(this,moveBase); 
        		}

        		mImageButton[mIndex_old].setFocusable(false);
        		mImageButton[mIndex].setFocusable(true);
        		mImageButton[mIndex_old].setImageResource(mImageButtonUFSrc[mIndex_old]);
        		
        		TranslateAnimation animation_focus = new TranslateAnimation(
                        m_curX,  m_nextX,
                        0.0f,  0.0f);
        		m_curX = m_nextX;
        		animation_focus.setDuration(DelayTime);
        		animation_focus.setFillAfter(true);
        		mFocus.startAnimation(animation_focus);
        		
        		 new Handler().postDelayed(new Runnable(){  
        		     public void run() {  
        		     //execute the task  
        		     mImageButton[mIndex].setImageResource(mImageButtonFSrc[mIndex]);
        		     }  
        		  }, DelayTime);

        		return true;
        	}
        	
			else if(keyCode == KeyEvent.KEYCODE_MEDIA_STOP) // As [Stop] key
			{
				if(map.getStopedFileName() != null)
			   {
				  mVideoBookMark.removeBookMark(map.getStopedFileName());
				  map.setStopedFileName(null);
				  Toast.makeText(getApplicationContext(), 
		          getResources().getString(R.string.msg_resume_remove_hint),
		          Toast.LENGTH_SHORT).show();
			   }else
			   {
				   Toast.makeText(getApplicationContext(), 
	     		   getResources().getString(R.string.msg_no_resume_point_hint),
	     		   Toast.LENGTH_SHORT).show();
			   }
			}
			else if(keyCode == KeyEvent.KEYCODE_ESCAPE)
			{
				// do nothing, L4300 required.
				return true;
			}
			else if(keyCode == KeyEvent.KEYCODE_BACK)
			{
				mBackKeyClickNum++;
				if(mBackKeyClickNum == 1)
				{
					if(msg_hint.isShowing())
						msg_hint.dismiss();
					msg_hint.setMessage(mContext.getResources().getString(R.string.msg_exit));
					if(isRight2Left)
						msg_hint.setMessageRight();
					else
						msg_hint.setMessageLeft();
        			msg_hint.show();
        			handler.sendEmptyMessageDelayed(HIDE_POPUP_MESSAGE, TimerDelay.delay_4s);
					
					if(task_back_key_click_delay != null)
					{
						task_back_key_click_delay.cancel();
						task_back_key_click_delay = null;
					}
					task_back_key_click_delay = new TimerTask(){

						@Override
						public void run() {
							mBackKeyClickNum = 0;
							Log.e(TAG, "mBackKeyClickNum = 0");
						}
						
					};
					timer.schedule(task_back_key_click_delay, TimerDelay.delay_8s);
					
					return true;
				}else if(mBackKeyClickNum == 2)
				{
                    msg_hint.dismiss();
					if(timer!=null)
					{
						timer.cancel();
						timer = null;
					}
					goToLauncher();
					this.finish();
				}
			}
    	}	
		return super.onKeyDown(keyCode, event);
	}	
	
	public float dip2px(Context context, float dipValue)
	{
		Log.v(TAG, "density = " + context.getResources().getDisplayMetrics().density);
		float m = context.getResources().getDisplayMetrics().density ;
		return dipValue * m + 0.5f;
	}
	 
	public float px2dip(Context context, float pxValue)
	{
		Log.v(TAG, "density = " + context.getResources().getDisplayMetrics().density);
		float m = context.getResources().getDisplayMetrics().density ;
		return pxValue / m  + 0.5f;
	}
	
	private void goToLauncher(){
		Intent it =new Intent();
		it.setAction("android.intent.action.MAIN");
		it.addCategory("android.intent.category.HOME");
		startActivity(it);
	}
	
	
	@Override
	public void onRestart() {
		Log.v(TAG, "onRestart");
		Log.v(TAG, "#####mIndex = "+mIndex);
		mImageButton[mIndex].setFocusable(true);
		mImageButton[mIndex].requestFocus();
		//mImageButton[mIndex_old].setFocusable(false);
		//mImageButton[mIndex_old].setImageResource(mImageButtonUFSrc[mIndex_old]);
		super.onRestart();
	}
	
	@Override
	public void onResume() {
		mTV.setAndroidMode(1);
		super.onResume();
	}
}
