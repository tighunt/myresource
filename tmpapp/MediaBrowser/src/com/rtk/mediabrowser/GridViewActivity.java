package com.rtk.mediabrowser;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import com.realtek.DataProvider.AbstractDataProvider;
import com.realtek.DataProvider.DeviceFileDataPrivider;
import com.realtek.DataProvider.FileFilterType;
import com.realtek.Utils.FileInfo;
import com.realtek.Utils.MimeTypes;
import com.realtek.bitmapfun.util.ImageWorker;
import com.realtek.bitmapfun.util.PicSize;

import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.app.Activity;
import android.app.TvManager;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.support.v4.app.FragmentActivity;
import android.text.TextPaint;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow.OnDismissListener;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;

import com.rtk.mediabrowser.GridViewFragment;
import com.rtk.mediabrowser.GridViewFragment.UiListener;

import com.rtk.mediabrowser.UsbController;

public class GridViewActivity extends FragmentActivity implements UiListener
{
    private MediaApplication mMediaApplicationMap = null;
    ArrayList<FileInfo> m_arrListItems = null;
	private Resources m_ResourceMgr = null;
	private ContentResolver m_ContentMgr = null;
	
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
    private String deviceName = null;
    private ArrayList<String> folderPath = new ArrayList<String>();
    
    //FileIndexInfo
    private TextView m_tvTopFileIndexInfoTextView = null;
    private String   m_sTopFileIndexInfoStr = null;
    
    //Pagedown & Pageup Icon 
    private ImageView m_ivIconPagedown = null;
    private ImageView m_ivIconPageup = null;
    
    //Repeat icon
    private ImageView m_repeatIcon = null;
    
    private static final String TAG = "GridViewActivity";
         
    private AbstractDataProvider mDataProvider = null;
    private int mBrowserType = 0;

	
	private PopupWindow  mPop = null;

	private QuickMenu mQuickMenu = null;
	private QuickMenuPhotoAdapter mQuickmenuAdapter = null;
	private Thread mRefreshSleeperTimer = null;
	
	private int mRepeatIndex = 0;
	private int mIntervalIndex = 1;
	private int mSleepTime = 0;
	private int mSleepTimeHour = 0, mSleepTimeMin = 0;
	
	private String[] mIntervalTimeStatus = new String[3];
	
	private String[] repeats = new String[2];

	
	private SharedPreferences mPerferences = null;
	
	Handler quickUIhandler = null;
	private  final static int MSG_SET_REPEAT    = 19;
	private final static int MSG_SET_INTERVAL   = 20;
	private final static int MSG_REFRESH_TIMER  = 21;
	
	//loading icon
	static Handler loading_icon_system_handle    = null;
	Animation mAnimLoading = null;
	ImageView mTopLoadingIcon =null;
	
	//preview
	private ImageView mPreview = null;
	
	//handle usb plug in / out
	UsbController mUsbCtrl = null;
	
	//up/down imgbutton
	private ImageView page_up_icon   = null,
					  page_down_icon = null;
	
	//banner
	private TextView banner_enter = null;
	
	private PopupMessage msg_hint = null;
	private PopupMessage msg_hint_noFile = null;
	
	private Handler mCheckTimerHandler = null;	
	private Handler mSetExifHandler = null;
	private int exifthreadNum = 0;
	private Message_not_avaible msg_notavaible = null;
	long mLastNotAvailableShowTime = 0;
	long mLastDeviceRemovedShowTime = 0;
	
	
	private int mActivityPauseFlag = 0;
	
	private TvManager mTv;
	
	private static final String DATAFORMAT = "dd MMM yyyy"; 
	
	private String devicePath ="";

	public static int mSelectItemIndex = 0;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        //set topInfo filePath
        //mRootPath       = "/mnt/sdcard/";
        //mCurrentPath    = "/mnt/sdcard/photo/";
        mTv = (TvManager)getSystemService("tv");
        m_ResourceMgr = this.getResources();
        m_ContentMgr = getApplicationContext().getContentResolver();
        
        
        
        mIntervalTimeStatus[0] = (String) m_ResourceMgr.getText(R.string.qm_interval_fast);
        mIntervalTimeStatus[1] = (String) m_ResourceMgr.getText(R.string.qm_interval_normal);
        mIntervalTimeStatus[2] = (String) m_ResourceMgr.getText(R.string.qm_interval_slow);
	
    	
    	repeats[0] = (String) m_ResourceMgr.getText(R.string.qm_repeat_off); 
    	repeats[1] = (String) m_ResourceMgr.getText(R.string.qm_repeat_on);


        mPerferences = PreferenceManager
    			.getDefaultSharedPreferences(this);
        
        mMediaApplicationMap=(MediaApplication)getApplication();
        
        if(m_PathInfo !=null)
        {
        	m_PathInfo.cleanLevelInfo();
        }
        else
        {
        	m_PathInfo = new Path_Info();
        }
       
        
        Intent intent = getIntent();
        mBrowserType = intent.getIntExtra("browserType", 0);

        if(mBrowserType == 0)
        {	
        	m_sRootPath = "/storage/udisk/";
        }
        m_PathInfo.addLevelInfo(m_sRootPath);
        
        //GetMimeType
        m_MimeTypeXml = m_ResourceMgr.getXml(R.xml.mimetypes);
        m_MimeTypes = Util.GetMimeTypes(m_MimeTypeXml);     
        
        Log.d("FileFilter", "mMimeTypeXml:" + m_MimeTypeXml.toString());
        
        //setGridView
        getDataProvider(m_PathInfo.getLastLevelPath());
        setFileInfoList();
        
        if(true == MediaBrowserConfig.getRight2Left(getApplicationContext()))
        	setContentView(R.layout.activity_grid_view_a);
        else
        	setContentView(R.layout.activity_grid_view);
        
        m_GridViewFragment = (GridViewFragment)(getSupportFragmentManager().findFragmentById(R.id.gridview_fragment));       
        mPreview = (ImageView)findViewById(R.id.preview);      
        banner_enter = (TextView)findViewById(R.id.guide_enter);
        
        init();
        if(!mDataProvider.isUsbPlugin())
		{
        	onItemClicked(0);
		}
        mCheckTimerHandler = new Handler(){
			@Override  
	        public void handleMessage(Message msg)  
	        { 
	        	switch (msg.what)  
	            {  
	              case 0: 
	            	  if(msg_notavaible.isShowing())
	            		  msg_notavaible.dismiss();
					  break; 
	              case 1:
	            	  if(msg_hint.isShowing())
	            		  msg_hint.dismiss();
					  break; 
	              case 2:
	            	  break;
	            	  
	             default:
	            	 break;
	            }  	          
	          super.handleMessage(msg);  
	        }  
	    };
	    mSetExifHandler = new Handler(){
	    	@Override  
	        public void handleMessage(Message msg)  
	        { 
	        	switch (msg.what)  
	            {  
	              case 0: 
	            	  m_tvExifSize.setText(
	            			  m_ResourceMgr.getString(R.string.size_exif)+msg.arg1+"X"+msg.arg2);
	            	  break;	
	              case 1:
	            	  Log.v(TAG,"max exifthread");
	            	//  Toast.makeText(GridViewActivity.this, "max exifthread 5 ", Toast.LENGTH_SHORT).show();
	            	  break;
	             default:
	            	 break;
	            }  	          
	          super.handleMessage(msg);  
	        }  
	    };
	    
        // createQuickMenu();
        mQuickmenuAdapter = new QuickMenuPhotoAdapter(this);
        mQuickMenu = new QuickMenu(this, mQuickmenuAdapter);
        mQuickMenu.setAnimationStyle(R.style.QuickAnimation);
        quickUIhandler =new Handler(){
          	 public void handleMessage(Message msg)  
               {        		
               	switch (msg.what)  
                  {  
   	             	case MSG_SET_REPEAT:
   	             	{
   	             		checkRepeatMode();
   		        		break;
   	             	}

   	        		case MSG_SET_INTERVAL:
   	        		{
   		                break;
   	        		}
   	        		case MSG_REFRESH_TIMER:
   	        			mQuickmenuAdapter.notifyDataSetChanged();
   	        			break;
   	                default:
   	                 	break;
   	  
                  }
                 super.handleMessage(msg);  
               }
          	
          };
          
        
        
        OnItemClickListener quickmenuItemClickListener = new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				mQuickMenu.markOperation();
				switch(position)
				{
					case 0:
					{ 
						mIntervalIndex++;
						mIntervalIndex %= 3;
							
						TextView OptionText = (TextView)arg1.findViewById(R.id.menu_option);
						OptionText.setText(mIntervalTimeStatus[mIntervalIndex]);
						
						new Thread(new Runnable() {
							@Override
							public void run() {
								Editor editor = mPerferences.edit();
								editor.putInt("intervalIndex_photo", mIntervalIndex);
								editor.commit();
							}
						}).start();
						
						break;
					}
					case 1:
					{
						mRepeatIndex++;
						mRepeatIndex %= 2;
						TextView OptionText = (TextView)arg1.findViewById(R.id.menu_option);
						OptionText.setText(repeats[mRepeatIndex]);
						checkRepeatMode();


						new Thread(new Runnable() {
							@Override
							public void run() {
								Editor editor = mPerferences.edit();
								editor.putInt("repeatIndex_photo", mRepeatIndex);
								editor.commit();
							}
						}).start();
					
						break;
					}
					case 2:
					{
						if(mSleepTimeMin < 50)
						{
        					if(0 == mSleepTimeMin && 12 == mSleepTimeHour)
        					{
        						mSleepTimeHour = 0;
        					}
        					else
        					{
        						mSleepTimeMin = (mSleepTimeMin / 10+1)*10;
        					}
						}
						else
						{
							mSleepTimeMin = 0;
							mSleepTimeHour++;
						}
						
						SimpleDateFormat df_ori = new SimpleDateFormat("HH mm");
		        		SimpleDateFormat df_des = new SimpleDateFormat("HH:mm");
		        	    java.util.Date date_parse = null;
		        	     try {
		        	    	 date_parse = df_ori.parse(mSleepTimeHour+" "+mSleepTimeMin);    	
		        		} catch (ParseException e) {
		        			// TODO Auto-generated catch block
		        			e.printStackTrace();
		        		}
		        
		            	String timeFormat = df_des.format(date_parse);
						
						TextView OptionText = (TextView)arg1.findViewById(R.id.menu_option);
						OptionText.setText(timeFormat);
		            	
						Intent itent = new Intent();
						Bundle bundle = new Bundle();
						if(0 == mSleepTimeHour && 0 == mSleepTimeMin)
        				{
        					bundle.remove("SetTSBSleepHour");
        					bundle.remove("SetTSBSleepMinute");
        					bundle.putString("TSBTimerState","Cancel");
        					bundle.putString("CallingTSBTimer", "MediaBrowserCallingTimer");
        				}
						else
						{
							bundle.putInt("SetTSBSleepHour",mSleepTimeHour);
							bundle.putInt("SetTSBSleepMinute",mSleepTimeMin);
							bundle.putString("TSBTimerState","Set");		//Set=1, Cancel=0
							bundle.putString("CallingTSBTimer", "MediaBrowserCallingTimer");
						}
						itent.setAction("com.rtk.TSBTimerSettingMESSAGE");
						itent.putExtras((Bundle)bundle);
						sendBroadcast(itent);
						break;
					}
					case 3:
					{
						ComponentName componetName = new ComponentName("com.tsb.tv","com.tsb.tv.Tv_strategy");
						Intent intent = new Intent();
						intent.setComponent(componetName);
						startActivity(intent);
						break;
					}
					case 4:
					{
						ComponentName componetName = new ComponentName("com.android.settings","com.android.settings.Settings");
						Intent intent = new Intent();
						intent.setComponent(componetName);
						startActivity(intent);
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
				mQuickMenu.markOperation();
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
								mIntervalIndex++;
								mIntervalIndex %= 3;
								
								TextView OptionText = (TextView)(quickMenuContent.getChildAt(position).findViewById(R.id.menu_option));
								OptionText.setText(mIntervalTimeStatus[mIntervalIndex]);
								new Thread(new Runnable() {
									@Override
									public void run() {
										Editor editor = mPerferences.edit();
										editor.putInt("intervalIndex_photo", mIntervalIndex);
										editor.commit();
									}
								}).start();
								
								break;
		        			}
		        			case 1:
		        			{
		        				mRepeatIndex++;
		        				mRepeatIndex %= 2;
								TextView OptionText = (TextView)  (quickMenuContent.getChildAt(position).findViewById(R.id.menu_option));
								OptionText.setText(repeats[mRepeatIndex]);
								checkRepeatMode();
								new Thread(new Runnable() {
									@Override
									public void run() {
										Editor editor = mPerferences.edit();
										editor.putInt("repeatIndex_photo", mRepeatIndex);
										editor.commit();
									}
								}).start();
								break;
		        			}    
		        			case 2:     
		        			{  
		        				
		        				if(mSleepTimeMin < 50)
								{
		        					if(0 == mSleepTimeMin && 12 == mSleepTimeHour)
		        					{
		        						mSleepTimeHour = 0;
		        					}
		        					else
		        					{
		        						mSleepTimeMin = (mSleepTimeMin / 10+1)*10;
		        					}
								}
								else
								{
									mSleepTimeMin = 0;
									mSleepTimeHour++;
								}
		        				
								SimpleDateFormat df_ori = new SimpleDateFormat("HH mm");
				        		SimpleDateFormat df_des = new SimpleDateFormat("HH:mm");
				        	    java.util.Date date_parse = null;
				        	     try {
				        	    	 date_parse = df_ori.parse(mSleepTimeHour+" "+mSleepTimeMin);    	
				        		} catch (ParseException e) {
				        			// TODO Auto-generated catch block
				        			e.printStackTrace();
				        		}
				        
				            	String timeFormat = df_des.format(date_parse);
								
								TextView OptionText = (TextView)(quickMenuContent.getChildAt(position).findViewById(R.id.menu_option));
								OptionText.setText(timeFormat);
				            	
								Intent itent = new Intent();
								Bundle bundle = new Bundle();
								if(0 == mSleepTimeHour && 0 == mSleepTimeMin)
		        				{
		        					bundle.remove("SetTSBSleepHour");
		        					bundle.remove("SetTSBSleepMinute");
		        					bundle.putString("TSBTimerState","Cancel");
		        					bundle.putString("CallingTSBTimer", "MediaBrowserCallingTimer");
		        				}
								else
								{
									bundle.putInt("SetTSBSleepHour",mSleepTimeHour);
									bundle.putInt("SetTSBSleepMinute",mSleepTimeMin);
									bundle.putString("TSBTimerState","Set");		//Set=1, Cancel=0
									bundle.putString("CallingTSBTimer", "MediaBrowserCallingTimer");
								}
								itent.setAction("com.rtk.TSBTimerSettingMESSAGE");
								itent.putExtras((Bundle)bundle);
								sendBroadcast(itent);
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
								mIntervalIndex = mIntervalIndex-1<0?2:mIntervalIndex-1;
								
								TextView OptionText = (TextView)(quickMenuContent.getChildAt(position).findViewById(R.id.menu_option));
								OptionText.setText(mIntervalTimeStatus[mIntervalIndex]);
								new Thread(new Runnable() {
									@Override
									public void run() {
										Editor editor = mPerferences.edit();
										editor.putInt("intervalIndex_photo", mIntervalIndex);
										editor.commit();
									}
								}).start();
								
								break;
		        			}
		        			case 1:
		        			{
		        				mRepeatIndex=(mRepeatIndex-1) >= 0 ? (mRepeatIndex-1) : 1;
								TextView OptionText = (TextView) (quickMenuContent.getChildAt(position).findViewById(R.id.menu_option));
								OptionText.setText(repeats[mRepeatIndex]);
								checkRepeatMode();
								new Thread(new Runnable() {
									@Override
									public void run() {
										Editor editor = mPerferences.edit();
										editor.putInt("repeatIndex_photo", mRepeatIndex);
										editor.commit();
									}
								}).start();
								break;
		        			}
		        			case 2:
		        			{
		        				if(0 == mSleepTimeMin)
		        				{
		        					if(0 == mSleepTimeHour)
		        					{
		        						mSleepTimeHour = 12;
		        					}
		        					else
		        					{
		        						mSleepTimeHour --;
		        						mSleepTimeMin =50;
		        					}
		        				}
		        				else
		        				{
		        					mSleepTimeMin =( (mSleepTimeMin-1) / 10)*10 ;
		        				}

								SimpleDateFormat df_ori = new SimpleDateFormat("HH mm");
				        		SimpleDateFormat df_des = new SimpleDateFormat("HH:mm");
				        	    java.util.Date date_parse = null;
				        	     try {
				        	    	 date_parse = df_ori.parse(mSleepTimeHour+" "+mSleepTimeMin);    	
				        		} catch (ParseException e) {
				        			// TODO Auto-generated catch block
				        			e.printStackTrace();
				        		}
				        
				            	String timeFormat = df_des.format(date_parse);
								
								TextView OptionText = (TextView)(quickMenuContent.getChildAt(position).findViewById(R.id.menu_option));
								OptionText.setText(timeFormat);
								
								Intent itent = new Intent();
								Bundle bundle = new Bundle();
								
								if(0 == mSleepTimeHour && 0 == mSleepTimeMin)
		        				{
		        					bundle.remove("SetTSBSleepHour");
		        					bundle.remove("SetTSBSleepMinute");
		        					bundle.putString("TSBTimerState","Cancel");
		        					bundle.putString("CallingTSBTimer", "MediaBrowserCallingTimer");
		        				}
								else
								{
									bundle.putInt("SetTSBSleepHour",mSleepTimeHour);
									bundle.putInt("SetTSBSleepMinute",mSleepTimeMin);
									bundle.putString("TSBTimerState","Set");		//Set=1, Cancel=0
									bundle.putString("CallingTSBTimer", "MediaBrowserCallingTimer");
								}
								itent.setAction("com.rtk.TSBTimerSettingMESSAGE");
								itent.putExtras((Bundle)bundle);
								sendBroadcast(itent);
								break;
		        			}
		        		}
		        		return true;
		        	}
		        	else if (keyCode == KeyEvent.KEYCODE_DPAD_UP)
		        	{
		        		if(position == 0)
		        		{
		        			quickMenuContent.setSelection(quickMenuContent.getCount()-1);
		        		}
		        		return false;		
		        	}
		        	else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN)
		        	{
		        		if(position == quickMenuContent.getCount()-1)
		        		{
		        			quickMenuContent.setSelection(0);
		        		}
		        		return false;
		        	}
		        	
		        	
	        	}
				else if (event.getAction() == KeyEvent.ACTION_UP)
				{
					if (keyCode == KeyEvent.KEYCODE_Q || keyCode == 227 //227 presents KEYCODE_QUICK_MENU 
								 || keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_ESCAPE)

		        	{
		        		mQuickMenu.dismiss();
		        	}
					else if(keyCode ==  KeyEvent.KEYCODE_MENU || keyCode == 220)
					{
						mQuickMenu.dismiss();
						if(null == msg_notavaible)
						{
							msg_notavaible = new Message_not_avaible(GridViewActivity.this);
						}
						msg_notavaible.show_msg_notavailable();
						
						mLastNotAvailableShowTime = (new Date(System.currentTimeMillis())).getTime();
						new Thread(new Runnable() {
            	    		public void run() {
            	    			long curtime = 0;
            	    			while(true)
            	    			{
            	    				if(msg_notavaible.isShowing() == false)
            	    					break;
            	    				curtime = (new Date(System.currentTimeMillis())).getTime();
            	    				if(curtime - mLastNotAvailableShowTime > 3000)
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
					
					return false;
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
				mQuickMenu.markOperation();
				for(int i = 0; i < mQuickMenu.getListView().getCount(); i++ )
				{
					mQuickmenuAdapter.setVisibility(i, View.INVISIBLE);
				}
				mQuickmenuAdapter.setVisibility(position, View.VISIBLE);
				mQuickmenuAdapter.notifyDataSetChanged();
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
        
		mUsbCtrl= new UsbController(this,true);  
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
                			setResult(1);
                			finish();
                			return;
                		}
                		if(m_PathInfo.getLastLevelPath() == m_PathInfo.getDeviceLevelPath())
                		{                	 
                			getDataProvider(m_PathInfo.getLastLevelPath());
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
                				/*
                			//if plug out the usb which is playing,return to device level 
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
                			*/
                				//if plug out the usb which is playing,return to home page
                			{
                				setResult(1);
                      			finish();
                      			return;
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
                			Toast.makeText(GridViewActivity.this
                				  ,m_ResourceMgr.getText(R.string.device_removed_toast)
                				  ,Toast.LENGTH_SHORT).show();
                			finish();
                			return;               		  
                		}
                		if(m_PathInfo.getLastLevelPath() == m_PathInfo.getDeviceLevelPath())
                		{
                			getDataProvider(m_PathInfo.getLastLevelPath());
                		  
                			m_GridViewFragment.mGridViewAdapter.count =
                				  mDataProvider.GetSize() > 12 ? 12 : mDataProvider.GetSize();;
                			m_GridViewFragment.RefreshGridView(m_PathInfo.getLastLevelFocus());
                			setFileIndxTextView();
                			setPageDownUpIcon();
                			// SetUsbNum(mDataProvider.GetSize());
                			m_GridViewFragment.directionControl.RefreshPosition();
                			m_GridViewFragment.directionControl.OnItemSelected_dir_ctrl();               		  
                		}
                	}
               }
        	
			}

			@Override
			public void OnUsbCheck(String path,int direction) {
				// TODO Auto-generated method stub
				if(mBrowserType !=0)
				{
					return ; //do nothing on dmrMode
				}
				String mpath = "/mnt/udisk"+devicePath;
				switch (direction)  
                {  
                	case UsbController.PLUG_OUT:
                	{   
                		if(mpath.equals(path)){
                				setResult(1);
		                		finish();
		                		return;
                		}
                	}
               }
			}
        };
        OnUsbLoadingListener usbLoadingListener = new OnUsbLoadingListener ()
        {

			@Override
			public void OnStartLoading() {
				// TODO Auto-generated method stub
				Message msg = new Message();
	    		msg.what = 0;
	    		GridViewActivity.loading_icon_system_handle.sendMessage(msg);
			}

			@Override
			public void OnStopLoading() {
				// TODO Auto-generated method stub
				Message msg = new Message();
	    		msg.what = 1;
	    		GridViewActivity.loading_icon_system_handle.sendMessage(msg);
			}

        };
        mUsbCtrl.setOnUsbCheckListener(usbCheckListener);
        mUsbCtrl.setOnUsbLoadingListener(usbLoadingListener);
        
        page_up_icon = (ImageView)findViewById(R.id.page_up_icon);
        page_down_icon = (ImageView)findViewById(R.id.page_down_icon);
        page_up_icon.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				m_GridViewFragment.directionControl.setPosition(0);
				m_GridViewFragment.directionControl.UpOperation();
				int position = m_GridViewFragment.directionControl.getPositionCurrent_allitems();
				onItemSelected(position+1);
				
			}
        	
        });
        page_down_icon.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				m_GridViewFragment.directionControl.setPosition(8);
				m_GridViewFragment.directionControl.DownOperation();
				int position = m_GridViewFragment.directionControl.getPositionCurrent_allitems();
				onItemSelected(position+1);
			}
        	
        });

        
        msg_hint = new PopupMessage(this);
        msg_hint_noFile = new PopupMessage(this);
        onItemSelected(0);
        if (m_GridViewFragment.getGridView().isInTouchMode()) {
			Log.d(TAG, "In touch mode");
			// FIXME
			//m_GridViewFragment.getGridView().exitTouchMode();	
		}
    }

	private void beforeFinish(){

	}
	private int getSleepTimeValue()
	{
		int sethour = Settings.Global.getInt(m_ContentMgr, "SetTimeHour", 0);
		int setmin = Settings.Global.getInt(m_ContentMgr, "SetTimeMinute", 0);
		int setsec = Settings.Global.getInt(m_ContentMgr, "SetTimeSecond", 0);
		int totalmin = Settings.Global.getInt(m_ContentMgr, "TotalMinute", 0);
		Log.d("RTK_DEBUG", "SetTimeHour:" + sethour + ",SetTimeMinute:" + setmin +",SetTimeSec:" + setsec + ",TotalMinute:" + totalmin);
		Date curDate = new Date(System.currentTimeMillis()) ;
		int curhours =  curDate.getHours();
		int curminutes = curDate.getMinutes();
		int curSecs = curDate.getSeconds();
		Date setData = new Date(curDate.getYear(), curDate.getMonth(), curDate.getDate(), sethour, setmin, setsec);
		
		int diftime = 0;
		if(curDate.after(setData)&&totalmin != 0){
			diftime = totalmin - ((curhours * 60 + curminutes) - (sethour * 60 + setmin));
		}
		return diftime;
	}
	private void PopupMessageShow(String msg, int resid, int height, int width, int gravity, int x, int y, final int dismiss_time)
	{
		if(msg_hint.isShowing() == true)
		{
			msg_hint.dismiss();
		}
		msg_hint.setMessage(msg);
		msg_hint.show(resid, height, width, gravity, x, y);
		
		mLastDeviceRemovedShowTime = (new Date(System.currentTimeMillis())).getTime();
		new Thread(new Runnable() {
    		public void run() {
    			long curtime = 0;
    			while(true)
    			{
    				if(msg_hint.isShowing() == false)
    					break;
    				curtime = (new Date(System.currentTimeMillis())).getTime();
    				if(curtime - mLastDeviceRemovedShowTime > dismiss_time)
	    			{
	    				Message msg = new Message();
	    				msg.what = 1;
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
    	mActivityPauseFlag = 1;
    	if(mQuickMenu.isShowing())
    	{
    		mQuickMenu.setIsActivityPause(mActivityPauseFlag);
    	}
    	mUsbCtrl.UnRegesterBroadcastReceiver();
    	super.onPause();
    }
    @Override
    public void onDestroy()
    {
    	Log.d(TAG, "onDestroy");
    	if(mPop != null && mPop.isShowing()) {
    	    mPop.dismiss();
        }
    	
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
	public void onRestart() {
		Log.d(TAG, "onRestart");
		super.onRestart();
		m_GridViewFragment.RefreshGridView(mSelectItemIndex);
		m_GridViewFragment.directionControl.RefreshPosition();
		m_GridViewFragment.directionControl.OnItemSelected_dir_ctrl();
	}

    @Override
    public void onResume()
    {
    	Log.d(TAG, "onResume");
    	
    	mTv.setAndroidMode(1);
    	mActivityPauseFlag = 0;
    	if(mQuickMenu.isShowing())
    	{
    		mQuickMenu.setIsActivityPause(mActivityPauseFlag);
    		mQuickMenu.setTimeout();
    	}
    	new Thread(new Runnable() {
			@Override
			public void run() {
				Editor editor = mPerferences.edit();
				int tmp_index_repeat   = mPerferences.getInt("repeatIndex_photo", -1);
				int tmp_index_interval = mPerferences.getInt("intervalIndex_photo", -1);
				
				if(tmp_index_repeat == -1)
				{
					editor.putInt("repeatIndex_photo", mRepeatIndex);
					editor.commit();
				}else{
					mRepeatIndex = tmp_index_repeat;
				}
				quickUIhandler.sendEmptyMessage(MSG_SET_REPEAT);
				
				if(tmp_index_interval == -1)
				{
					editor.putInt("intervalIndex_photo", mIntervalIndex);
					editor.commit();
				}else{
					mIntervalIndex = tmp_index_interval;
				}
				quickUIhandler.sendEmptyMessage(MSG_SET_INTERVAL);
				
				int mins = getSleepTimeValue();
				mSleepTimeHour = mins / 60;
				mSleepTimeMin = mins % 60;
				
			}
		}).start();	
    	
    	new Thread(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				while(mActivityPauseFlag != 1)
				{
					int mins = getSleepTimeValue();
					mSleepTimeHour = mins / 60;
					mSleepTimeMin = mins % 60;
					
					if(mQuickMenu.isShowing())
					{
						quickUIhandler.sendEmptyMessage(GridViewActivity.MSG_REFRESH_TIMER);
					}
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
        	
        }).start();
    	mUsbCtrl.RegesterBroadcastReceiver();
    	mQuickmenuAdapter.notifyDataSetChanged();
        super.onResume();
    
    }
 
    @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {
    	if(msg_hint.isShowing())
    		msg_hint.dismiss();
    	if(event.getAction() == KeyEvent.ACTION_UP)
    	{
    		Log.d(TAG, event.toString()+"Action up.");
    		switch(event.getKeyCode())
	    	{
		    	case KeyEvent.KEYCODE_Q:
				case 227:    //227 present KEYCODE_QUICK_MENU
				{
					if(mQuickMenu.isShowing() == true)
						mQuickMenu.dismiss();
					else
					{
						mQuickMenu.showQuickMenu(14,14);
						mQuickMenu.setTimeout();
					}
					super.dispatchKeyEvent(event);
					return true;
				}
				case 82://MENU
				case 220://STEREO/DUAL for L4300
					if(null == msg_notavaible)
					{
						msg_notavaible = new Message_not_avaible(GridViewActivity.this);
					}
					msg_notavaible.show_msg_notavailable();
					
					mLastNotAvailableShowTime = (new Date(System.currentTimeMillis())).getTime();
					new Thread(new Runnable() {
        	    		public void run() {
        	    			long curtime = 0;
        	    			while(true)
        	    			{
        	    				if(msg_notavaible.isShowing() == false)
        	    					break;
        	    				curtime = (new Date(System.currentTimeMillis())).getTime();
        	    				if(curtime - mLastNotAvailableShowTime > 1000)
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
					super.dispatchKeyEvent(event);
					return true;
	    	}
    		return super.dispatchKeyEvent(event);
    	}
    	else if(event.getAction() == KeyEvent.ACTION_DOWN)
    	{
    		Log.d(TAG, event.toString()+"Action down.");
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
				case KeyEvent.KEYCODE_CHANNEL_DOWN:
				{
					m_GridViewFragment.directionControl.PageDownOperation();
					return true; 
				}
				case KeyEvent.KEYCODE_PAGE_UP:
				case KeyEvent.KEYCODE_CHANNEL_UP:
				{
					m_GridViewFragment.directionControl.PageUpOperation();
					return true;    		
				}
				case KeyEvent.KEYCODE_E:
				case KeyEvent.KEYCODE_ESCAPE:
				{
					try {
						ComponentName componetName = new ComponentName(
								"com.rtk.mediabrowser",// another apk name
								"com.rtk.mediabrowser.MediaBrowser" // another apk
																	// activity name
						);
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putInt("browserType", mBrowserType);
						intent.putExtras(bundle);
						intent.setComponent(componetName);
						startActivity(intent);
					} catch (Exception e) {
					}
					finish();
					return true;
				}
				case KeyEvent.KEYCODE_BACK:
				{
					onBackClicked();
					return true;
				}			
	    	}
    	}
    	
    	return super.dispatchKeyEvent(event);
    }
    private synchronized void plusexifthreadnum()
    {    	
    	exifthreadNum++;
    }
    private synchronized void minusexifthreadnum()
    {    	
    	exifthreadNum--;
    }
    
    
     
    private void init()
    {
        m_tvTopMediaPathInfoTextView = (TextView)findViewById(R.id.topMeidiaPathInfo);
//        setTopMediaPathInfoTextView();
   
        //top file index info
        m_tvTopFileIndexInfoTextView= (TextView)findViewById(R.id.TopIndexInfo);
        m_sTopFileIndexInfoStr = Integer.toString(m_PathInfo.getLastLevelFocus()+1, 10)+"/"+mDataProvider.GetSize();
        setFileIndxTextView();
        
        //set pagedown & pageup icon
        m_ivIconPagedown = (ImageView)findViewById(R.id.page_down_icon);
        m_ivIconPageup = (ImageView)findViewById(R.id.page_up_icon);
        setPageDownUpIcon();
        
        //set repeat icon
        m_repeatIcon = (ImageView)findViewById(R.id.photo_browser_repeat);
 
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
        	private int loading_num = 0;
        	boolean hasAnimStarted=false;
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
        		m_tvTopFileIndexInfoTextView.setVisibility(View.INVISIBLE);
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
	private class Message_not_avaible extends PopupWindow
	{
		private Activity context;
		private RelativeLayout rp = null;
		public TextView message = null;
		
		LayoutInflater mInflater=null;
		
		Message_not_avaible(Activity mContext)
		{
			super(mContext);					
			
			this.context=mContext;
			
			mInflater = LayoutInflater.from(context);
		    rp=(RelativeLayout) mInflater.inflate(R.layout.message_not_available, null);
		    message = (TextView)rp.findViewById(R.id.not_available);    
		    setContentView(rp);	
		}
		
		public void show_msg_notavailable()
		{
			TextPaint paint = message.getPaint(); 
			int len = (int) paint.measureText((String) m_ResourceMgr.getText(R.string.toast_not_available))+102;
			message.setText(m_ResourceMgr.getText(R.string.toast_not_available));
			setHeight(72);
			setWidth(len);
			message.setTextColor(Color.BLACK);
			this.setFocusable(true);
			this.setOutsideTouchable(true);
			this.showAtLocation(rp, Gravity.LEFT| Gravity.BOTTOM, 18, 18);
			
		}	
	}
   
    private void setTopMediaPathInfoTextView()
    {
    	int dirLevel = m_PathInfo.getLastLevel();
    	System.out.println("dirLevel :"+dirLevel);
    	System.out.println("folderPath :"+folderPath.size());
    	
    	if(dirLevel <= 0)
    	{
        	m_tvTopMediaPathInfoTextView.setVisibility(View.INVISIBLE);
        	return;
    	}
    	else
    	{
    		m_tvTopMediaPathInfoTextView.setVisibility(View.VISIBLE);
    	}
    	
        if(m_tvTopMediaPathInfoTextView != null && m_PathInfo.getLastLevelPath() != null)
        {
        	
        	String pathTag ="";
			for(int i = 1;i<=dirLevel-1;i++)
				pathTag +="/";
        	
			if(false == MediaBrowserConfig.getRight2Left(getApplicationContext()))
			{
				m_tvTopMediaPathInfoTextView.setText("/"+deviceName+pathTag+folderPath.get(dirLevel-1));
			}
			else
			{
				m_tvTopMediaPathInfoTextView.setText(folderPath.get(dirLevel-1)+pathTag+deviceName+"/");
			}
            
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

	public int getPathLevel()
    {
        return m_PathInfo.dirLevel;
    }

    public void onItemSelected(int position)
    {
        m_PathInfo.setLastLevelFocus(position);
        setFileIndxTextView();
        setExifTextView(position);
        setPreView(position);        
        setBottomBanner(position);
    }
    private void setBottomBanner(int pos)
    {
    	if(mDataProvider.GetFileTypeAt(pos) == FileFilterType.DEVICE_FILE_PHOTO)
    	{
    		banner_enter.setText((String) m_ResourceMgr.getText(R.string.guide_select));
    	}
    	else if((mDataProvider.GetFileTypeAt(pos) == FileFilterType.DEVICE_FILE_DIR)
    			||(mDataProvider.GetFileTypeAt(pos) == FileFilterType.DEVICE_FILE_DEVICE))
		{
    		banner_enter.setText((String) m_ResourceMgr.getText(R.string.guide_enter));
		}
    	
    }
    
    
	@SuppressWarnings("unused")
	private void setExifTextView(int position) {
		// TODO Auto-generated method stub
    	final int tmppos = position;
    	if(-1 == tmppos)//"-1" is used for when "no media available" ,set preview and exif invisible
    	{
    		m_tvExifTitle.setVisibility(View.INVISIBLE);
    		m_tvExifDate.setVisibility(View.INVISIBLE);
    		m_tvExifSize.setVisibility(View.INVISIBLE);
    		return;
    	}
    	
    	if(mDataProvider != null)
    	{
	    	if(mDataProvider.GetFileTypeAt(tmppos) == FileFilterType.DEVICE_FILE_PHOTO)
			{
	    		ExifInterface exif = null;
		    	try {
		    		exif = new ExifInterface(mDataProvider.GetDataAt(tmppos));
		    	} catch (IOException e) {
		    		// TODO Auto-generated catch block
		    		e.printStackTrace();
		    	}
		        String date = exif.getAttribute(ExifInterface.TAG_DATETIME);
		        if(date == null)
		        {
	    			File _file = new File(mDataProvider.GetDataAt(tmppos));
	    			date = getFileCreateDate(_file,DATAFORMAT);
		        }
		        else
		        {
		        	date = dateFormate(date,DATAFORMAT);
		        }
		        
		        PicSize pSize = m_GridViewFragment.getSizeCache(mDataProvider.GetDataAt(tmppos));
		        String length = null;
		        String width = null;
		        if(pSize != null)
		        {
		        	length = String.valueOf(pSize.getHeight());
		        	width = String.valueOf(pSize.getWidth());
		        }
		        else
		        {
		        	length = exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
		        	width = exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
		        	
		        	if(!(length.equals("0") ||width.equals("0")))
		        	{
		        		pSize = new PicSize(Integer.valueOf(length), Integer.valueOf(width)); 
		        		m_GridViewFragment.setSizeCache(mDataProvider.GetDataAt(tmppos),pSize);
		        	}
		        	else if(false)
			        {
			        	new Thread(new Runnable() {
							@Override
							public void run() {
								plusexifthreadnum();
								if(exifthreadNum > 1)
								{
									minusexifthreadnum();
									Message setExifMessage = new Message();
						            setExifMessage.what = 1;
									//mSetExifHandler.sendMessage(setExifMessage);								
									return;
								}
								
								BitmapFactory.Options options = new BitmapFactory.Options();  
								
					            options.inJustDecodeBounds = true;  
					            options.inSampleSize=100;
					            options.inPreferredConfig = Bitmap.Config.ARGB_4444;
					            BitmapFactory.decodeFile(mDataProvider.GetDataAt(tmppos), options); // bitmap is null
					            
					            int decode_length = options.srcHeight==-1?0:options.srcHeight;
					            int decode_width = options.srcWidth==-1?0:options.srcWidth;				   
					            Message setExifMessage = new Message();
					            setExifMessage.what = 0;
					            setExifMessage.arg1 = decode_width;
					            setExifMessage.arg2 = decode_length;
					            
					            mSetExifHandler.sendMessage(setExifMessage);
					            minusexifthreadnum();
							}
						}).start();
			        	//get height and width by not decoding bitmap
			        	
			        }
		        }
		        
		        m_tvExifTitle.setVisibility(View.VISIBLE);
	    		m_tvExifDate.setVisibility(View.VISIBLE);
	    		m_tvExifSize.setVisibility(View.VISIBLE);
	    		
		        m_tvExifTitle.setText(m_ResourceMgr.getString(R.string.title_exif)+mDataProvider.GetTitleAt(tmppos));
		    	m_tvExifDate.setText(m_ResourceMgr.getString(R.string.date_exif)+date);
		    	
		    	String text_size = m_ResourceMgr.getString(R.string.size_exif);
		        if(pSize != null)
		        {
		        	ImageWorker.setSizeText(m_tvExifSize,pSize,text_size);
		        }
		        else
		        {
		        	ImageWorker.setSizeText(m_tvExifSize,new PicSize(0,0),text_size);
		        }
		    	m_tvExifSize.setText(m_ResourceMgr.getString(R.string.size_exif)+width+"X"+length);
			}
	    	else if(mDataProvider.GetFileTypeAt(tmppos) == FileFilterType.DEVICE_FILE_DIR)
			{
	    		m_tvExifTitle.setVisibility(View.VISIBLE);
	    		m_tvExifDate.setVisibility(View.INVISIBLE);
	    		m_tvExifSize.setVisibility(View.INVISIBLE);
	    		m_tvExifTitle.setText(m_ResourceMgr.getString(R.string.folder_exif)+mDataProvider.GetTitleAt(tmppos));		    	
			}
	    	else if(mDataProvider.GetFileTypeAt(tmppos) == FileFilterType.DEVICE_FILE_DEVICE)
	    	{
	    		m_tvExifTitle.setVisibility(View.VISIBLE);
	    		m_tvExifDate.setVisibility(View.INVISIBLE);
	    		m_tvExifSize.setVisibility(View.INVISIBLE);
	    		m_tvExifTitle.setText(m_ResourceMgr.getString(R.string.device_exif)+mDataProvider.GetTitleAt(tmppos));		
	    	}
    	}
	}
    public static String getFileCreateDate(File _file,String formatString) {
        File file = _file;
        Date last_date = new Date(file.lastModified());
        SimpleDateFormat df_des = new SimpleDateFormat(formatString);
        String last_modify_date=df_des.format(last_date);
 		return last_modify_date;
    }
	public static String dateFormate(String date,String formatString) {
		// TODO Auto-generated method stub				
		SimpleDateFormat df_ori_exif = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
		SimpleDateFormat df_des = new SimpleDateFormat(formatString);
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
		if (m_GridViewFragment.getGridView().isInTouchMode()) {
			Log.d(TAG, "In touch mode");
			// FIXME
			//m_GridViewFragment.getGridView().exitTouchMode();
		}
		
		m_PathInfo.setLastLevelFocus(position);
        if(mDataProvider != null && m_PathInfo.getLastLevelFocus() >= 0 && m_PathInfo.getLastLevelFocus() <mDataProvider.GetSize())
        {
        	mSelectItemIndex = m_PathInfo.getLastLevelFocus();
            Log.v(TAG, "focus: " + m_PathInfo.getLastLevelFocus());
            if(mDataProvider.GetFileTypeAt(m_PathInfo.getLastLevelFocus()) == FileFilterType.DEVICE_FILE_PHOTO)
            {
                ComponentName componetName = new ComponentName("com.rtk.mediabrowser",
								"com.rtk.mediabrowser.PhotoPlayerActivity"); 
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

        		bundle.putInt("mSleepTime", mSleepTime);
        		
        		bundle.putString("devicePath", devicePath);
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
            	
                String pathTitle = mDataProvider.GetTitleAt(m_PathInfo.getLastLevelFocus());
        		String header = m_PathInfo.getLastLevelPath();
        		if(mDataProvider.GetFileTypeAt(position) == FileFilterType.DEVICE_FILE_DEVICE)
        		{
        			deviceName = mDataProvider.GetTitleAt(position);
        			devicePath = "/" + deviceName;
        			folderPath.add("");
        			if(mDataProvider.GetTitleAt(position).equals(MediaApplication.internalStorage) && mBrowserType == 0){
						header = Environment.getExternalStorageDirectory().getPath();
						pathTitle = "";
					}
        		}
        		else
        		{
        			folderPath.add(mDataProvider.GetTitleAt(position));
        		}
                
                m_PathInfo.addLevelInfo(header + pathTitle + "/");
                getDataProvider(m_PathInfo.getLastLevelPath());
                
                if(mDataProvider.GetSize() >= MediaApplication.MAXFILENUM)
                {
                	PopupMessageShow((String) m_ResourceMgr.getText(R.string.maxfilenum),R.drawable.message_box_bg,260,678,Gravity.CENTER,0,0,5000);
                }
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
        	Log.v(TAG,"onItemClick error!!!");
        }  
    }
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == 0)
		{
			if(resultCode == -10)//exit key
			{
				finish();
				return ;
			}
			else if(resultCode == -1)//
			{
				setResult(1);
				finish();
				return ;
			}
			else if(resultCode == -2)//plug out usb when photo full screen;
			{
				/*
				m_GridViewFragment.directionControl.BackToMultiView(resultCode,
						m_GridViewFragment.directionControl.getPositionCurrent_allitems(),
						m_GridViewFragment.directionControl.getPositionCurrent_pageitem());
						*/
				setResult(1);
				finish();
				return ;
			}
			/*else{
				m_GridViewFragment.directionControl.BackToMultiView(resultCode,
						m_GridViewFragment.directionControl.getPositionCurrent_allitems(),
						m_GridViewFragment.directionControl.getPositionCurrent_pageitem());
			}*/
		
		}
	}
	
    
    
    public boolean onBackClicked()
    {
    	System.out.println("mUsbCtrl.GetUsbNum()mUsbCtrl.GetUsbNum()++"+mUsbCtrl.GetUsbNum());
    	if(mUsbCtrl.GetUsbNum() == 0)
		{
    		System.out.println("m_PathInfo.getLastLevel()++++++-"+m_PathInfo.getLastLevel());
    		if(m_PathInfo.getLastLevel() ==1)
    		{
    			
    			this.finish();
    			return false;
    		}
		}
    	
        boolean retval = false;
         
        if(mDataProvider == null)
        {
        	Log.d(TAG,"onBackClicked:mDataProvider==null");
        }	
        
        Log.d(TAG,"onBackClicked:" + m_PathInfo.getLastLevelFocus());
        Log.d(TAG,"onBackClicked:" + mDataProvider.GetSize());

            if(m_PathInfo.getLastLevel() >0)
            {   
                m_PathInfo.backToLastLevel();
                getDataProvider(m_PathInfo.getLastLevelPath());
                if(m_PathInfo.getLastLevelPath().equals( m_PathInfo.getDeviceLevelPath()))
            	{
                	if(mDataProvider.GetSize()==1)
                	{
                		this.finish();
                    	return false;
                	}
            	}
                folderPath.remove(m_PathInfo.getLastLevel());
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
            	this.finish();
            	return false;
            }
        
        m_GridViewFragment.directionControl.RefreshPosition();
        m_GridViewFragment.directionControl.OnItemSelected_dir_ctrl();
        
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
		 	int[] menu_name = new int[] {
					R.string.quick_menu_photo_intervalTime,
					R.string.quick_menu_repeat,
					R.string.quick_menu_sleep,
					R.string.quick_menu_tvapp,
					R.string.quick_menu_sysset
					};

			int[] visibility = new int[]{
			 		View.INVISIBLE,
			 		View.INVISIBLE,
			 		View.INVISIBLE,
			 		View.INVISIBLE,
			 		View.INVISIBLE
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

			public void setVisibility(int position,int isVisible)
			{
				visibility[position]=isVisible;				
			}
				        
	        @Override
	        public int getCount() {
	            return menu_name.length;
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
		        	//Typeface type= Typeface.createFromFile("/system/fonts/FAUNSGLOBAL3_F_r2.TTF");
	        		//holder.menu_name.setTypeface(type);
		        	holder.menu_option = (TextView)convertView.findViewById(R.id.menu_option);	
					if(position <= 2){
			        	holder.left = (ImageView)convertView.findViewById(R.id.left_arrow);
			        	holder.right = (ImageView)convertView.findViewById(R.id.right_arrow);
					}
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
		            	holder.menu_option.setText(mIntervalTimeStatus[mIntervalIndex]);
		            	break;		            	
		            }
		            case 1:
		            {
		            	holder.menu_option.setText(repeats[mRepeatIndex]);
		            	break;
		            }
		            case 2:
		            {
		            	SimpleDateFormat df_ori = new SimpleDateFormat("HH mm");
		        		SimpleDateFormat df_des = new SimpleDateFormat("HH:mm");
		        	    java.util.Date date_parse = null;
		        	     try {
		        	    	 date_parse = df_ori.parse(mSleepTimeHour+" "+mSleepTimeMin);    	
		        		} catch (ParseException e) {
		        			// TODO Auto-generated catch block
		        			e.printStackTrace();
		        		}
		        
		            	String timeFormat = df_des.format(date_parse);
		            	holder.menu_option.setText(timeFormat);

		            	break;
		            }
		            case 3:
		            {
						holder.menu_option.setText("");
		            	break;
		            }
		            case 4:
		            {
						holder.menu_option.setText("");
		            	break;
		            }
		            default:
		            	break;
	            }

	           	if(position <= 2)
		        {
		        	holder.left.setVisibility(visibility[position]);
		            holder.right.setVisibility(visibility[position]);
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
	 public void checkRepeatMode()
	 {
		 if(0 == mRepeatIndex)
		 {
			 m_repeatIcon.setVisibility(View.INVISIBLE);
		 }
		 else if(1 == mRepeatIndex)
		 {
			 m_repeatIcon.setVisibility(View.VISIBLE);
		 }
			 
	 }
	 //Added for GridViewFragment.GridViewLoadingControl.startLoading()
	 public void startLoadingIcon() {
		 Message msg = new Message();
		 msg.what = 0;
		 loading_icon_system_handle.sendMessage(msg);
	 }
	 
	 //Added for GridViewFragment.GridViewLoadingControl.stopLoading()
	 public void stopLoadingIcon() {
		 Message msg = new Message();
		 msg.what = 1;
		 loading_icon_system_handle.sendMessage(msg);
	 }

}
