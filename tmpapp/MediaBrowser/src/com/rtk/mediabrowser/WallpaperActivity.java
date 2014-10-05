package com.rtk.mediabrowser;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.realtek.DataProvider.AbstractDataProvider;
import com.realtek.DataProvider.DeviceFileDataPrivider;
import com.realtek.DataProvider.FileFilterType;
import com.realtek.Utils.FileInfo;
import com.realtek.Utils.MimeTypes;
import com.rtk.mediabrowser.GridViewFragment.UiListener;

public class WallpaperActivity extends FragmentActivity implements UiListener
{
    private MediaApplication mMediaApplicationMap = null;
    ArrayList<FileInfo> m_arrListItems = null;
	private Resources m_ResourceMgr = null;
	
	private TextView m_tvExifTitle = null;
	private TextView m_tvExifDate = null;
	private TextView m_tvExifSize = null;
	
	private MimeTypes m_MimeTypes = null;
	private XmlResourceParser m_MimeTypeXml;
	
    private GridViewFragment m_GridViewFragment = null;
       
    //Top mediaPathInfo
    private TextView  m_tvTopMediaPathInfoTextView = null;
    private String m_sRootPath    = null;
    private Path_Info m_PathInfo=null;
    
    //FileIndexInfo
    private TextView m_tvTopFileIndexInfoTextView = null;
    private String   m_sTopFileIndexInfoStr = null;
    
    //Pagedown & Pageup Icon 
    private ImageView m_ivIconPagedown = null;
    private ImageView m_ivIconPageup = null;
    
    private static final String TAG = "WallpaperActivity";
         
    private AbstractDataProvider mDataProvider = null;
    private int mBrowserType = 0;
	private PopupMessage msg_hint_noFile = null;
	
	private PopupWindow  mPop = null;

	private QuickMenu mQuickMenu = null;
	private QuickMenuPhotoAdapter mQuickmenuAdapter = null;
	private enum RepeatStatus {
		OFF, ALL
	}
	private int mRepeatIndex = 0;
	private int mSleepTime = 0;
	private int mIntervalTime = 5;
	private RepeatStatus[] repeats = { RepeatStatus.OFF, RepeatStatus.ALL};	
	
	//loading icon
	static Handler loading_icon_system_handle    = null;
	Animation mAnimLoading = null;
	ImageView mTopLoadingIcon =null;
	
	//preview
	private ImageView mPreview = null;
	
	//handle usb plug in / out
	UsbController mUsbCtrl = null;
	
	private AlertDialog mAlertDialog = null;
	private boolean mIsUsbPlugIn = false;
	
	private boolean mRetvalBackup = false;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        //set topInfo filePath
        //m_sRootPath       = "/mnt/sdcard/";
        //mCurrentPath    = "/mnt/sdcard/photo/";

        m_ResourceMgr=this.getResources();
        
        mMediaApplicationMap=(MediaApplication)getApplication();
        
        if(m_PathInfo !=null)
        {
        	m_PathInfo.cleanLevelInfo();
        }
        else
        {
        	m_PathInfo = new Path_Info();
        }
        msg_hint_noFile = new PopupMessage(this);
        //Intent intent= getIntent();
        //mBrowserType = intent.getIntExtra("browserType", 0);
            
        if(mBrowserType == 0)
        {	
        	m_sRootPath = "/storage/udisk/";
        	
        	File file = new File(m_sRootPath);
        	File[] files = file.listFiles();
        	int fileSize = files.length;
        	files = null;
        	file = null;
        	if(fileSize == 0)
        	//if(getExternalCacheDir() == null)
        	{
        		mAlertDialog = new AlertDialog.Builder(this)
        			.setIcon(android.R.drawable.ic_dialog_alert)
        			.setTitle(R.string.warning_title)
        			.setMessage(R.string.warning_message)
        			.setNegativeButton(android.R.string.cancel, 
        					new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
								}
							})
					.setOnCancelListener(new DialogInterface.OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							if(!mIsUsbPlugIn) {
								finish();
							}
						}
					})
					.show();
        	}
        }	
        m_PathInfo.addLevelInfo(m_sRootPath);
        
        //GetMimeType
        m_MimeTypeXml = m_ResourceMgr.getXml(R.xml.mimetypes);
        m_MimeTypes=Util.GetMimeTypes(m_MimeTypeXml);     
        Log.d(TAG, "FileFilter: m_MimeTypeXml = " + m_MimeTypeXml.toString());
        //Log.d("FileFilter", "mMimeTypeXml:" + m_MimeTypeXml.toString());
        //setGridView
        getDataProvider(m_PathInfo.getLastLevelPath());
        setFileInfoList();
        setContentView(R.layout.activity_wallpaper);
        
        m_GridViewFragment = (GridViewFragment)(getSupportFragmentManager().findFragmentById(R.id.gridview_fragment));       
        mPreview = (ImageView)findViewById(R.id.preview);       
        init();
        
        // createQuickMenu();
        mQuickmenuAdapter = new QuickMenuPhotoAdapter(this);
        mQuickMenu = new QuickMenu(this, mQuickmenuAdapter);
        
        OnItemClickListener quickmenuItemClickListener = new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				switch(position)
				{
					case 0:
					{
						mIntervalTime = mIntervalTime+5;	
						mIntervalTime = mIntervalTime%30;
						TextView OptionText = (TextView)arg1.findViewById(R.id.menu_option);
						OptionText.setText(String.valueOf(mIntervalTime)
		            			+getResources().getString(R.string.timeUnit));
						break;
					}
					case 1:
					{
						mRepeatIndex++;
						mRepeatIndex %= 2;
						TextView OptionText = (TextView)arg1.findViewById(R.id.menu_option);
						OptionText.setText(repeats[mRepeatIndex].name());
						break;
					}
					case 2:
					{
						mSleepTime=mSleepTime+5;	
						mSleepTime=mSleepTime%30;
						TextView OptionText = (TextView)arg1.findViewById(R.id.menu_option);
						if(mSleepTime == 0)
						{
							OptionText.setText("OFF");
						}
		            	else
		            	{
		            		OptionText.setText(
		            			String.valueOf(mSleepTime)
		            			+WallpaperActivity.this.getResources().getString(R.string.timeUnit));
		            	}
						break;
					}
					case 3:
					{
						break;
					}
					case 4:
					{
						break;
					}
					default:
						break;
				}

			}
        };
        
        OnKeyListener quickmenuKeyClickListener = new OnKeyListener(){

        	ListView quickMenuContent = mQuickMenu.getListView();
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == KeyEvent.ACTION_DOWN)
	        	{
					int position = quickMenuContent.getSelectedItemPosition();
		        	if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)
		        	{
		        		// TODO: switch option
		        		switch(position)
		        		{
		        			case 0:
		        			{
		        				mIntervalTime = mIntervalTime+5;	
								mIntervalTime = mIntervalTime%30;
								TextView OptionText = (TextView) (quickMenuContent.getChildAt(position).findViewById(R.id.menu_option));
								OptionText.setText(String.valueOf(mIntervalTime)
				            			+getResources().getString(R.string.timeUnit));
								break;
		        			}
		        			case 1:
		        			{
		        				mRepeatIndex++;
		        				mRepeatIndex %= 2;
								TextView OptionText = (TextView)  (quickMenuContent.getChildAt(position).findViewById(R.id.menu_option));
								OptionText.setText(repeats[mRepeatIndex].name());
								break;
		        			}
		        			case 2:
		        			{
		        				mSleepTime = mSleepTime+5;	
								mSleepTime = mSleepTime%30;
								TextView OptionText = (TextView) (quickMenuContent.getChildAt(position).findViewById(R.id.menu_option));
								if(mSleepTime == 0)
								{
									OptionText.setText("OFF");
								}
				            	else
				            	{
				            		OptionText.setText(
				            			String.valueOf(mSleepTime)
				            			+WallpaperActivity.this.getResources().getString(R.string.timeUnit));
				            	}
								break;
		        			}
		        		}
		        		
		        		return true;
		        	}
		        	else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT)
		        	{
		        		// TODO: switch option
		        		switch(position)
		        		{
		        			case 0:
		        			{
		        				mIntervalTime=mIntervalTime-5 >= 0 ? mIntervalTime - 5 : 30;	
		        				TextView OptionText = (TextView) (quickMenuContent.getChildAt(position).findViewById(R.id.menu_option));
								OptionText.setText(String.valueOf(mIntervalTime)
				            			+getResources().getString(R.string.timeUnit));
								break;
		        			}
		        			case 1:
		        			{
		        				mRepeatIndex=(mRepeatIndex-1) >= 0 ? (mRepeatIndex-1) : 1;
								TextView OptionText = (TextView) (quickMenuContent.getChildAt(position).findViewById(R.id.menu_option));
								OptionText.setText(repeats[mRepeatIndex].name());
								break;
		        			}
		        			case 2:
		        			{
		        				mSleepTime=mSleepTime-5 >= 0 ? mSleepTime - 5 : 30;	
		        				TextView OptionText = (TextView) (quickMenuContent.getChildAt(position).findViewById(R.id.menu_option));
								if(mSleepTime == 0)
								{
									OptionText.setText("OFF");
								}
				            	else
				            	{
				            		OptionText.setText(
				            			String.valueOf(mSleepTime)
				            			+WallpaperActivity.this.getResources().getString(R.string.timeUnit));
				            	}
								break;
		        			}
		        		}
		        		return true;
		        	}
		        	if (keyCode == KeyEvent.KEYCODE_Q)
		        	{
		        		mQuickMenu.dismiss();
		        	}
	        	}
				return false;
			}
		};
		
		OnItemSelectedListener quickmenuItemSelectedListener = new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> arg0, View view,
					int position, long arg3) 
			{
				// TODO Auto-generated method stub
				if(mQuickmenuAdapter.LastSelectedItem_View == null)
					mQuickmenuAdapter.LastSelectedItem_View = view;
				ImageView left_arrow = (ImageView)mQuickmenuAdapter.LastSelectedItem_View.findViewById(R.id.left_arrow);
				ImageView right_arrow = (ImageView)mQuickmenuAdapter.LastSelectedItem_View.findViewById(R.id.right_arrow);
				left_arrow.setVisibility(View.INVISIBLE);
				right_arrow.setVisibility(View.INVISIBLE);
				mQuickmenuAdapter.LastSelectedItem_View = view;
				if(position < 3)
				{
					left_arrow = (ImageView)mQuickmenuAdapter.LastSelectedItem_View.findViewById(R.id.left_arrow);
					right_arrow = (ImageView)mQuickmenuAdapter.LastSelectedItem_View.findViewById(R.id.right_arrow);
					left_arrow.setVisibility(View.VISIBLE);		
					right_arrow.setVisibility(View.VISIBLE);
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) 
			{
				// TODO Auto-generated method stub
				Log.d(TAG, "Quick Menu ListView onNothingSelected");
			}
		};
		mQuickMenu.AddOnItemClickListener(quickmenuItemClickListener);
		mQuickMenu.AddOnItemSelectedListener(quickmenuItemSelectedListener);
		mQuickMenu.AddOnKeyClickListener(quickmenuKeyClickListener);
        
		mUsbCtrl= new UsbController(this);  
        OnUsbCheckListener usbCheckListener = new OnUsbCheckListener(){
			@Override
			public void OnUsbCheck() {
				// TODO Auto-generated method stub
				switch (mUsbCtrl.getDirection())  
                {  
                	case UsbController.PLUG_OUT:
                	{                    
                		if(mUsbCtrl.GetUsbNum() == 0)//deviceNum is 0
                		{
                			Toast.makeText(WallpaperActivity.this
                				  ,m_ResourceMgr.getText(R.string.device_removed_toast)
                				  ,Toast.LENGTH_SHORT).show();
                			finish();
                			return;
                		}
                		if(m_PathInfo.getLastLevelPath() == m_PathInfo.getDeviceLevelPath())
                		{                	 
                			getDataProvider(m_PathInfo.getLastLevelPath());
                			setTopMediaPathInfoTextView();
                			m_GridViewFragment.mGridViewAdapter.count=
                				  mDataProvider.GetSize()>12?12:mDataProvider.GetSize();;
                			m_GridViewFragment.RefreshGridView(0);
                			setFileIndxTextView();
                			setPageDownUpIcon();
                			m_GridViewFragment.directionControl.RefreshPosition();
                			m_GridViewFragment.directionControl.OnItemSelected_dir_ctrl();               		  
                		}
                		else
                		{
                			getDataProvider(m_PathInfo.getLastLevelPath());
                			if(mDataProvider.GetSize() == 0)
                			//if plug out the usb which is playing,go to device level 
                			{       	  	       	
                				m_PathInfo.backToDeviceLevel();
                				getDataProvider(m_PathInfo.getLastLevelPath());
                			  
                				setTopMediaPathInfoTextView();
                				m_GridViewFragment.mGridViewAdapter.count =
                    				  mDataProvider.GetSize() > 12 ? 12 : mDataProvider.GetSize();;
                    			m_GridViewFragment.RefreshGridView(0);
                    			setFileIndxTextView();
                    			setPageDownUpIcon();
                    			m_GridViewFragment.directionControl.RefreshPosition();
                    			m_GridViewFragment.directionControl.OnItemSelected_dir_ctrl();
                			}
                			else
                			//if plug out the usb which is playing,do nothing
                			{
                				break;
                			}               		  
                		}
                		break;
                	}
                	case UsbController.PLUG_IN:
                	{
                		if(mUsbCtrl.GetUsbNum() == 0)//in case of plug in and plug out happens at one time
                		{
                			Toast.makeText(WallpaperActivity.this
                				  ,m_ResourceMgr.getText(R.string.device_removed_toast)
                				  ,Toast.LENGTH_SHORT).show();
                			finish();
                			return;               		  
                		}
                		if(m_PathInfo.getLastLevelPath() == m_PathInfo.getDeviceLevelPath())
                		{
                			mIsUsbPlugIn = true;
                			
                			getDataProvider(m_PathInfo.getLastLevelPath());
                		  
                			setTopMediaPathInfoTextView();
                			m_GridViewFragment.mGridViewAdapter.count =
                				  mDataProvider.GetSize() > 12 ? 12 : mDataProvider.GetSize();;
                			m_GridViewFragment.RefreshGridView(m_PathInfo.getLastLevelFocus());
                			setFileIndxTextView();
                			setPageDownUpIcon();
                			// SetUsbNum(mDataProvider.GetSize());
                			m_GridViewFragment.directionControl.RefreshPosition();
                			m_GridViewFragment.directionControl.OnItemSelected_dir_ctrl();               		  
                		}
                		else
                		{
                			;
                		}
                	}
               }
        	
			}

			@Override
			public void OnUsbCheck(String path,int direction) {
				// TODO Auto-generated method stub
				
			}
        };
        OnUsbLoadingListener usbLoadingListener = new OnUsbLoadingListener ()
        {

			@Override
			public void OnStartLoading() {
				// TODO Auto-generated method stub
				Message msg = new Message();
	    		msg.what = 0;
	    		WallpaperActivity.loading_icon_system_handle.sendMessage(msg);
			}

			@Override
			public void OnStopLoading() {
				// TODO Auto-generated method stub
				Message msg = new Message();
	    		msg.what = 1;
	    		WallpaperActivity.loading_icon_system_handle.sendMessage(msg);
			}

        };
        mUsbCtrl.setOnUsbCheckListener(usbCheckListener);
        mUsbCtrl.setOnUsbLoadingListener(usbLoadingListener);
    }
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.activity_grid_view, menu);
        return true;
    }
    @Override 
    public void onPause()
    {
    	Log.d(TAG, "onPause");
    	super.onPause();
    }
    @Override
    public void onDestroy()
    {
    	Log.d(TAG, "onDestroy");
    	if(mPop != null && mPop.isShowing()) {
    	    mPop.dismiss();
        }
    	mUsbCtrl.UnRegesterBroadcastReceiver();
    	super.onDestroy();
    }
    private void setFileInfoList()
    {
		if(m_arrListItems == null)
		{
			m_arrListItems = mMediaApplicationMap.getPhotoFileInfoList();
		}
		else
			m_arrListItems.clear();

		for (int i = 0; i < mDataProvider.GetSize(); i++) {

			FileInfo finfo = new FileInfo(
					mDataProvider.GetFileTypeAt(i), mDataProvider.GetTitleAt(i), mDataProvider.GetDataAt(i)); 
			m_arrListItems.add(finfo);
		}
    }
    


    @Override
    public void onResume()
    {
    	Log.d(TAG, "onResume");
    	mUsbCtrl.RegesterBroadcastReceiver();
        super.onResume();
    }
 
    @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {
    	boolean retval = false;
    	if(event.getAction() == KeyEvent.ACTION_DOWN)
    	{
	    	switch(event.getKeyCode())
	    	{
			    case KeyEvent.KEYCODE_DPAD_RIGHT:
				{
					m_GridViewFragment.directionControl.RightOperation();
					return true;
				}
				case KeyEvent.KEYCODE_DPAD_LEFT:
				{
					m_GridViewFragment.directionControl.LeftOperation();
					return true;
				}
				case KeyEvent.KEYCODE_DPAD_UP:
				{
					m_GridViewFragment.directionControl.UpOperation();
					return true;
				}
				case KeyEvent.KEYCODE_DPAD_DOWN:
				{
					m_GridViewFragment.directionControl.DownOperation();
					return true;
				}
				case KeyEvent.KEYCODE_PAGE_DOWN:
				{
					m_GridViewFragment.directionControl.PageDownOperation();
					return true; 
				}
				case KeyEvent.KEYCODE_PAGE_UP:
				{
					m_GridViewFragment.directionControl.PageUpOperation();
					return true;    		
				}
				case KeyEvent.KEYCODE_Q:
				{
					if(mQuickMenu.isShowing() == true)
						mQuickMenu.dismiss();
					else
					{
						mQuickMenu.showQuickMenu(14,1266-mQuickMenu.getHeight());	
					}
					return true;
				}
				case KeyEvent.KEYCODE_E:
				{
					finish();
					return true;
				}
				case KeyEvent.KEYCODE_BACK:
				{
					mRetvalBackup = onBackClicked();
					return mRetvalBackup;
				}			
	    	}
    	}
    	if(event.getAction() == KeyEvent.ACTION_UP)
    	{
    		switch(event.getKeyCode())
	    	{
				case KeyEvent.KEYCODE_DPAD_RIGHT:
				case KeyEvent.KEYCODE_DPAD_LEFT:
				case KeyEvent.KEYCODE_DPAD_UP:
				case KeyEvent.KEYCODE_DPAD_DOWN:
				case KeyEvent.KEYCODE_PAGE_DOWN:
				case KeyEvent.KEYCODE_PAGE_UP:
				case KeyEvent.KEYCODE_Q:
				case KeyEvent.KEYCODE_E:
					return true;
				case KeyEvent.KEYCODE_BACK:
					return mRetvalBackup;
				default:
					break;
	    	}
    	}
    	if(retval == false)
    	{	
    		retval =  super.dispatchKeyEvent(event);
    	}
    	return retval;
    }
    
     
    private void init()
    {
        m_tvTopMediaPathInfoTextView = (TextView)findViewById(R.id.topMeidiaPathInfo);
        setTopMediaPathInfoTextView();
   
        //top file index info
        m_tvTopFileIndexInfoTextView= (TextView)findViewById(R.id.TopIndexInfo);
        m_sTopFileIndexInfoStr = Integer.toString(m_PathInfo.getLastLevelFocus()+1, 10)+"/"+mDataProvider.GetSize();
        setFileIndxTextView();
        
        //set pagedown & pageup icon
        m_ivIconPagedown = (ImageView)findViewById(R.id.page_down_icon);
        m_ivIconPageup = (ImageView)findViewById(R.id.page_up_icon);
        setPageDownUpIcon();
 
    	m_tvExifTitle=(TextView)findViewById(R.id.title_exif);
    	m_tvExifDate=(TextView)findViewById(R.id.date_exif);
    	m_tvExifSize=(TextView)findViewById(R.id.size_exif);
    	
    	
    	mTopLoadingIcon=(ImageView)findViewById(R.id.topLoadingIcon);
        
    	mTopLoadingIcon.setVisibility(View.INVISIBLE);
        mAnimLoading = AnimationUtils.loadAnimation(this, R.drawable.anim);
        mTopLoadingIcon.setAnimation(mAnimLoading);
        mTopLoadingIcon.getAnimation().cancel();
        mTopLoadingIcon.setImageResource(R.drawable.blank);
         
        loading_icon_system_handle = new Handler()
        {
        	private synchronized void setLoadingThreadNum(int flag)
        	{
        		if(flag == 0)
        		{
	          		  loading_num++;      	
        		}
        		else if(flag == 1)
        		{
	          		  loading_num--;      	
        		}
        	}
        	private synchronized int getLoadingThreadNum()
        	{
        		return loading_num;
        	}
        	private int loading_num = 0;
        	boolean hasAnimStarted=false;
        	@Override  
            public void handleMessage(Message msg)  
            {        		
            	switch (msg.what)  
                {  
                  case 0:
                  {
                	  setLoadingThreadNum(msg.what);
                	  break;  
                  }
                  case 1:
                  {
                	  setLoadingThreadNum(msg.what);
                	  break;
                  }
                 default:
                	 break;
                }  
            	if(getLoadingThreadNum() > 0)
            	{           	
            		if(hasAnimStarted == false && loading_num == 1)
            		{           	
            			mTopLoadingIcon.getAnimation().reset();
            			mTopLoadingIcon.getAnimation().startNow();
            			mTopLoadingIcon.setVisibility(View.VISIBLE);
            			hasAnimStarted=true;
            		}            		
            		mTopLoadingIcon.setImageResource(R.drawable.others_icons_loading);
            	}
            	else if(getLoadingThreadNum() == 0)
            	{	
            		if(mTopLoadingIcon == null || mTopLoadingIcon.getAnimation() == null)
            		{
            			Log.e(TAG, "Loading Icon is null.");
            			return;
            		}
            		mTopLoadingIcon.getAnimation().cancel();
            		hasAnimStarted=false;           	
            		mTopLoadingIcon.setVisibility(View.INVISIBLE);
            		mTopLoadingIcon.setImageResource(R.drawable.blank);
            	}
            	else
            		Log.e(TAG,"loading icon error");
              super.handleMessage(msg);  
            }         	
        };      
        m_GridViewFragment.directionControl.OnItemSelected_dir_ctrl();//set preview/exif/index at postion 0
    }
    
    private void setPageDownUpIcon() {
    	if(mDataProvider.GetSize()>12)
    	{
    		m_ivIconPagedown.setVisibility(View.VISIBLE);
    		m_ivIconPageup.setVisibility(View.VISIBLE);
    	}
    	else
    	{
    		m_ivIconPagedown.setVisibility(View.INVISIBLE);
    		m_ivIconPageup.setVisibility(View.INVISIBLE);
    	}
	}

	private void setFileIndxTextView()
    {
        Log.d(TAG,"setFileIndxTextView"+m_PathInfo.getLastLevelFocus());
        m_tvTopFileIndexInfoTextView.setVisibility(View.VISIBLE);
        if( m_tvTopFileIndexInfoTextView!=null && mDataProvider != null)
        {    
        	if(mDataProvider.GetSize() <= 0)
        	{
        		m_sTopFileIndexInfoStr = "0/0";
        	}
        	else
        	{	
        		if(m_PathInfo.getLastLevelFocus() + 1 - mDataProvider.getDirnum() < 1)
        		{
        			m_tvTopFileIndexInfoTextView.setVisibility(View.INVISIBLE);
        		}
        		else
        		{
	        		m_sTopFileIndexInfoStr = Integer.toString(
	        			m_PathInfo.getLastLevelFocus() + 1 - mDataProvider.getDirnum()
	        		//		+m_GridViewFragment.directionControl.GetAdapter().movePage*12
	        		//		+m_GridViewFragment.directionControl.GetAdapter().moveLine*4
	        				)
	        				+ "/" + (mDataProvider.GetSize() - mDataProvider.getDirnum());
	        		
	        		m_tvTopFileIndexInfoTextView.setText(m_sTopFileIndexInfoStr);
        		}
        	}	
        }
    }
	
	public int getPathLevel()
    {
        return m_PathInfo.dirLevel;
    }
   
    private void setTopMediaPathInfoTextView()
    {
        if(m_tvTopMediaPathInfoTextView != null && m_PathInfo.getLastLevelPath() != null)
        {
            m_tvTopMediaPathInfoTextView.setText(m_PathInfo.getLastLevelPath());
        }    
    }
      
    private void getDataProvider(String path)
    {
   	
        if(m_PathInfo.getLastLevelPath() != null || m_MimeTypeXml != null ) 
        {   
            mDataProvider = null;
            
            if(mBrowserType == 0)
            {    
            	mDataProvider = new DeviceFileDataPrivider(path, 
            		FileFilterType.DEVICE_FILE_DIR|FileFilterType.DEVICE_FILE_PHOTO|FileFilterType.DEVICE_FILE_DEVICE,
                    -1,0,m_MimeTypes);
            }
            mDataProvider.sortListByType();
            Log.d(TAG,"getDataProvider:size:"+mDataProvider.GetSize());
        }
    }
  
    public AbstractDataProvider getCurrentDataProvider()
    {
        return mDataProvider;
    }

    public void onItemSelected(int position)
    {
        m_PathInfo.setLastLevelFocus(position);
        setFileIndxTextView();
        setPreView(position);
        setExifTextView(position);
    }
    
    private void setExifTextView(int position) {
		// TODO Auto-generated method stub
    	
    	if(-1 == position)//"-1" is used for when "no media available" ,set preview and exif invisible
    	{
    		m_tvExifTitle.setVisibility(View.INVISIBLE);
    		m_tvExifDate.setVisibility(View.INVISIBLE);
    		m_tvExifSize.setVisibility(View.INVISIBLE);
    		return;
    	}
    	if(mDataProvider != null)
    	{
	    	if(mDataProvider.GetFileTypeAt(position) == FileFilterType.DEVICE_FILE_PHOTO)
			{
	    		ExifInterface exif = null;
		    	try {
		    		exif = new ExifInterface(mDataProvider.GetDataAt(position));
		    	} catch (IOException e) {
		    		// TODO Auto-generated catch block
		    		e.printStackTrace();
		    	}
		        String date = exif.getAttribute(ExifInterface.TAG_DATETIME);
		        String length = exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
		        String width = exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
		        if(date == null)
		        {
	    			File _file = new File(mDataProvider.GetDataAt(position));
	    			date = getFileCreateDate(_file);

		        }
		        else
		        {
		        	date = dateFormate(date);
		        }
		        if(length.equals("0") && width.equals("0"))
		        {
		        	//get height and width by not decoding bitmap
		        	BitmapFactory.Options options = new BitmapFactory.Options();  
		            options.inJustDecodeBounds = true;  
		            BitmapFactory.decodeFile(mDataProvider.GetDataAt(position), options); // bitmap is null
		            length = String.valueOf(options.srcHeight==-1?0:options.srcHeight);
		            width = String.valueOf(options.srcWidth==-1?0:options.srcWidth);
		        }
		        
		        m_tvExifTitle.setVisibility(View.VISIBLE);
	    		m_tvExifDate.setVisibility(View.VISIBLE);
	    		m_tvExifSize.setVisibility(View.VISIBLE);
	    		
		        m_tvExifTitle.setText(m_ResourceMgr.getString(R.string.title_exif)+mDataProvider.GetTitleAt(position));
		    	m_tvExifDate.setText(m_ResourceMgr.getString(R.string.date_exif)+date);
		    	m_tvExifSize.setText(m_ResourceMgr.getString(R.string.size_exif)+width+"X"+length);
			}
	    	else if(mDataProvider.GetFileTypeAt(position) == FileFilterType.DEVICE_FILE_DIR)
			{
	    		m_tvExifTitle.setVisibility(View.VISIBLE);
	    		m_tvExifDate.setVisibility(View.INVISIBLE);
	    		m_tvExifSize.setVisibility(View.INVISIBLE);
	    		m_tvExifTitle.setText(m_ResourceMgr.getString(R.string.folder_exif)+mDataProvider.GetTitleAt(position));		    	
			}
	    	else if(mDataProvider.GetFileTypeAt(position) == FileFilterType.DEVICE_FILE_DEVICE)
	    	{
	    		m_tvExifTitle.setVisibility(View.VISIBLE);
	    		m_tvExifDate.setVisibility(View.INVISIBLE);
	    		m_tvExifSize.setVisibility(View.INVISIBLE);
	    		m_tvExifTitle.setText(m_ResourceMgr.getString(R.string.device_exif)+mDataProvider.GetTitleAt(position));		
	    	}
    	}
	}
    public static String getFileCreateDate(File _file) {
        File file = _file;
        Date last_date = new Date(file.lastModified());
        SimpleDateFormat df_des = new SimpleDateFormat("HH:mm EEEE,dd MMMM yyyy");
        String last_modify_date=df_des.format(last_date);
 		return last_modify_date;
    }
	public static String dateFormate(String date) {
		// TODO Auto-generated method stub				
		SimpleDateFormat df_ori_exif = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
		SimpleDateFormat df_des = new SimpleDateFormat("HH:mm EEEE,dd MMMM yyyy");
	    java.util.Date date_parse = null;
	     try {
	    	 date_parse = df_ori_exif.parse(date);
	    	
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	     String formateDate1 = null;
	     formateDate1 = df_des.format(date_parse);
	     return formateDate1;
	}
	private void setPreView(int position) {
		// TODO Auto-generated method stub
    	if(m_GridViewFragment == null)
    	{
    		return;
    	}

    	mPreview.setVisibility(View.VISIBLE);
    	if(-1 == position)//"-1" is used for when "no media available" ,set preview and exif invisible
    	{
    		mPreview.setVisibility(View.INVISIBLE);
    		return;
    	}
    	
    	m_GridViewFragment.loadPreviewImage(mPreview,m_tvExifSize,position);

	}
	public void onItemClicked(int position)
    {
		m_PathInfo.setLastLevelFocus(position);
        if(mDataProvider != null && m_PathInfo.getLastLevelFocus() >= 0 && m_PathInfo.getLastLevelFocus() <mDataProvider.GetSize())
        {
            Log.d(TAG, "onItemClicked(): focus on " + m_PathInfo.getLastLevelFocus());
            if(mDataProvider.GetFileTypeAt(m_PathInfo.getLastLevelFocus()) == FileFilterType.DEVICE_FILE_PHOTO)
            {
                ComponentName componetName = new ComponentName("com.rtk.mediabrowser",
								"com.rtk.mediabrowser.WallpaperPreviewActivity"); 
        		Intent intent = new Intent();
        		Bundle bundle = new Bundle();
        		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        		Log.d(TAG,"onItemClicked photo file:[" + mDataProvider.GetDataAt(position) + "]");

        		int curPos = 0;
        		int totalCnt = 0;
        		int tmpsize = mDataProvider.GetSize();
        		for(int i = 0; i < tmpsize ; i++) 
        		{	
        			
        			if(mDataProvider.GetFileTypeAt(i) == FileFilterType.DEVICE_FILE_PHOTO)
        			{	
        				if(mDataProvider.GetTitleAt(position).compareTo(mDataProvider.GetTitleAt(i)) == 0)
        					curPos = totalCnt;
        				totalCnt++;
        			}
        		}
        		
        		Log.d(TAG,"onItemClicked curPos:"+curPos);
        		Log.d(TAG,"onItemClicked totalCnt:"+totalCnt);

        		bundle.putInt("repeatMode", mRepeatIndex);
        		bundle.putInt("mSleepTime", mSleepTime);
        		bundle.putInt("mIntervalTime", mIntervalTime);
        		
        		bundle.putInt("initPos", curPos);
        		bundle.putInt("totalCnt",totalCnt);
        		bundle.putInt("browserType", mBrowserType);
        		

        		intent.putExtras(bundle);
        		intent.setComponent(componetName);
        		startActivityForResult(intent, 0);
            }
            else if(mDataProvider.GetFileTypeAt(m_PathInfo.getLastLevelFocus()) == FileFilterType.DEVICE_FILE_DIR
            		||mDataProvider.GetFileTypeAt(m_PathInfo.getLastLevelFocus()) == FileFilterType.DEVICE_FILE_DEVICE
            		)
            {
                String path = m_PathInfo.getLastLevelPath() + mDataProvider.GetTitleAt(m_PathInfo.getLastLevelFocus())+"/";
                
                m_PathInfo.addLevelInfo(path);
                getDataProvider(m_PathInfo.getLastLevelPath());
                setFileInfoList();
                setTopMediaPathInfoTextView();
                m_GridViewFragment.mGridViewAdapter.count =
                		mDataProvider.GetSize()>12?12:mDataProvider.GetSize();
                		
                m_PathInfo.setLastLevelFocus(0);
                m_GridViewFragment.RefreshGridView(m_PathInfo.getLastLevelFocus());
                setFileIndxTextView();
                setPageDownUpIcon();
				
                if(0 == m_GridViewFragment.mGridViewAdapter.count )
				{
					msg_hint_noFile.setMessage(m_ResourceMgr.getString(R.string.msg_noFile));
					if(true == MediaBrowserConfig.getRight2Left(getApplicationContext()))
					{
						msg_hint_noFile.setMessageRight();
					}
					msg_hint_noFile.show();	
					msg_hint_noFile.setFocusable(true);
					msg_hint_noFile.update();
					setPreView(-1);
					setExifTextView(-1);
					
					msg_hint_noFile.setOnDismissListener(new OnDismissListener(){

						@Override
						public void onDismiss() {
							// TODO Auto-generated method stub
							onBackClicked();
						}
						
					});
					
					return;
				}							
				m_GridViewFragment.directionControl.RefreshPosition();
				m_GridViewFragment.directionControl.OnItemSelected_dir_ctrl();
            }
        }
        else
        {
        	Log.e(TAG,"onItemClick error!!!");
        }  
    }
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == 0)//plug out usb when ;
		{
			if(resultCode == -10)//exit key
			{
				finish();
				return ;
			}
			else if(resultCode == -1)//
			{
				Toast.makeText(this, m_ResourceMgr.getText(R.string.device_removed_toast),
			    	    Toast.LENGTH_SHORT).show();
				finish();
				return ;
			}
			else if(resultCode == -2)//plug out usb when photo full screen;
			{
				m_GridViewFragment.directionControl.BackToMultiView(resultCode,
						m_GridViewFragment.directionControl.getPositionCurrent_allitems(),
						m_GridViewFragment.directionControl.getPositionCurrent_pageitem());
			}
			else if(resultCode == -5)//set wallpaper success
			{
				Log.d(TAG, "onActivityResult(): resultCode = -5; set wallpaper success!");
				finish();
				return;
			}
			else
			{
				m_GridViewFragment.directionControl.BackToMultiView(resultCode,
						m_GridViewFragment.directionControl.getPositionCurrent_allitems(),
						m_GridViewFragment.directionControl.getPositionCurrent_pageitem());
			}
		}
	}
	
    
    
    public boolean onBackClicked()
    {
        boolean retval = false;
         
        if(mDataProvider == null)
        {
        	Log.d(TAG,"onBackClicked:mDataProvider==null");
        }	
        
        Log.d(TAG,"onBackClicked: m_PathInfo.getLastLevelFocus()=" + m_PathInfo.getLastLevelFocus());
        Log.d(TAG,"onBackClicked: mDataProvider.GetSize()=" + mDataProvider.GetSize());

        if(m_PathInfo.getLastLevel() >0)
        {   
            m_PathInfo.backToLastLevel();
            getDataProvider(m_PathInfo.getLastLevelPath());
            setFileInfoList();
            setTopMediaPathInfoTextView();
            m_GridViewFragment.mGridViewAdapter.count =
            		mDataProvider.GetSize() > 12 ? 12 : mDataProvider.GetSize();
            m_GridViewFragment.RefreshGridView(m_PathInfo.getLastLevelFocus());
            setFileIndxTextView();
            setPageDownUpIcon();
            retval = true;
        }
        else
        {
        	Log.d(TAG, "onBackClicked(): Finish WallpaperActivity");
        	this.finish();
        	return false;
        }
        
        m_GridViewFragment.directionControl.RefreshPosition();
        m_GridViewFragment.directionControl.OnItemSelected_dir_ctrl();
//        getSDInfo();
        
        return retval;
    }
    public boolean onKeyClicked(View view, int keyCode, KeyEvent event,int position,int iconNum,int firstVisibleItem,int lastVisibleItem)
    {
    	return false;
    }
    
    public int  getFocusIndex()
    {   
        return m_PathInfo.getLastLevelFocus();
    }  
	 class QuickMenuPhotoAdapter extends BaseAdapter 
	 {
		 public View LastSelectedItem_View = null;
		 	int[] menu_name = new int[] {
					R.string.quick_menu_photo_intervalTime,
					R.string.quick_menu_repeat,
					R.string.quick_menu_sleep,
					R.string.quick_menu_tvapp,
					R.string.quick_menu_sysset
					};
		 				
			private LayoutInflater mInflater;
			
			class ViewHolder {
				TextView menu_name;
				ImageView left;
				TextView menu_option;
				ImageView right;
			}
			
			public QuickMenuPhotoAdapter(Context context)
	    	{
				mInflater = LayoutInflater.from(context);
	    	}
				        
	        @Override
	        public int getCount() {
	            return menu_name.length;
	        }

	        @Override
	        public View getView(int position, View convertView, ViewGroup parent) {
	        	 ViewHolder holder;
	        	
	        	if (convertView == null) {
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
	            switch(position)
	            {
		            case 0:
		            {
		            	holder.menu_option.setText(
		            			String.valueOf(mIntervalTime)
		            			+ WallpaperActivity.this.getResources().getString(R.string.timeUnit));
		            	break;		            	
		            }
		            case 1:
		            {
		            	holder.menu_option.setText(
		            			repeats[mRepeatIndex].name());
		            	break;
		            }
		            case 2:
		            {
		            	if(mSleepTime == 0)
						{
		            		holder.menu_option.setText("OFF");
						}
		            	else
		            	{
		            		holder.menu_option.setText(
		            			String.valueOf(mSleepTime)
		            			+WallpaperActivity.this.getResources().getString(R.string.timeUnit));
		            	}
		            	break;
		            }
		            case 3:
		            {
		            //	holder.menu_option.setText("QUICK MENU");
		            	break;
		            }
		            case 4:
		            {
		            //	holder.menu_option.setText("QUICK MENU");
		            	break;
		            }
		            default:
		            	break;
	            }

	           
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
	 
	 public void startLoadingIcon() {
		 Message msg = new Message();
		 msg.what = 0;
		 loading_icon_system_handle.sendMessage(msg);
	 }
	 
	 public void stopLoadingIcon() {
		 Message msg = new Message();
		 msg.what = 1;
		 loading_icon_system_handle.sendMessage(msg);
	 }
	 
	 @Override
	 protected void onStop() {
		 super.onStop();
		 if(mAlertDialog != null) {
			 mAlertDialog.dismiss();
			 mAlertDialog = null;
			 Log.d(TAG,"onStop(): AlertDialog dismissed");
		 }
	 }
	    
}
