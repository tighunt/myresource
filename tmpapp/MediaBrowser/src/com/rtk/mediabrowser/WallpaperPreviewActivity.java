package com.rtk.mediabrowser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import android.app.AlertDialog;
import android.app.TvManager;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.realtek.DataProvider.FileFilterType;
import com.realtek.Utils.FileInfo;

public class WallpaperPreviewActivity extends FragmentActivity
{
	private MediaApplication mMediaApplicationMap = null;
	private ArrayList<FileInfo> mPhotoList;
	private int mPhotoDirNum=0;
	boolean mIsFromAnywhere=false;
	private BroadcastReceiver mTvawReceiver = null;
	IntentFilter mTvawFilter = null;
	
	private Resources m_ResourceMgr = null;
	private static final int NOT_ZOOM_MODE = 0;
	private int mRepeatMode=0;

	private static final String TAG = "WallpaperPreviewActivity";
	private static PictureKit  m_pPictureKit = null;
	
	private  static boolean m_startdecode     = false;
	private  static int     m_decodeImageState  = DecodeImageState.STATE_NOT_DECODED;
	private  static int     m_decodeImageResult = DecodeImageState.STATE_DECODE_DONE;
		
	private int      m_initPos = 0;
	private int      m_currentPlayIndex = 0;
	private int      m_totalCnt = 0;
	private String[] m_filePathStrArray = null;
	
    private Handler m_checkResultHandlerTimer = new Handler();
    private Handler m_slideShowHandlerTimer  = new Handler();
    
    private Handler m_checkResultHandlerTimer_forOnKey = new Handler();
    private Handler m_checkFirstPictureHandler = new Handler();
    
    private long     m_checkResultTime = 100;
    private long     m_slideShowTime   = 5000; //5 second
      
    private DisplayMetrics mDisplayMetrics = new DisplayMetrics();

    boolean mIsFullScrean=false;

	private long mLastControlTime=0;
	
	private int mBrowserType = 0;
    
    private Handler mCheckTimerHandler;
    //private long mBannerShowTime=6000;
    private long mBannerShowTime = 0;
   
    private SurfaceView mPhotoPlaybackView = null;
	
    public static LinkedList<ImagePlaylist> playList = new LinkedList<ImagePlaylist>();
    
    public static final String IMAGE_CACHE_DIR = "images";
      
    private Handler mSetBannerHandler = null;
    private TextView banner_pic_index  = null
    		,banner_pic_name           = null
    		,banner_pic_timeinfo       = null
    		,banner_pic_resolution     = null;
    private ImageView banner_slideshow = null
    		,banner_repeat             = null;
    private ImageView move_left_btm    = null
    		,move_right_btm			   = null
    		,move_top_btm			   = null
    		,move_bottom_btm		   = null;
    private	TextView pic_multiple	   = null; 
    private boolean mIsSlideShowModel  = false;
    private int mIsZoomModel		   = 0;
    
    private QuickMenu mQuickmenu=null;
	private QuickMenuPhotoAdapter mQuickmenuAdapter=null;
	private enum RepeatStatus {
		OFF, ALL
	}
	
	private int mRepeatIndex  = 0;
	private int mSleepTime    = 0;
	private int mIntervalTime = 5;
	private RepeatStatus[] repeats = { RepeatStatus.OFF, RepeatStatus.ALL};
    
    private TvManager mTv;
    
    UsbController mUsbCtrl = null;
    
    private int mDecodeRetryTimes = 0;
    
    private int mActivityPauseFlag = 0;
    private int mActivityDestroyFlag = 0;
    
    private Handler m_setWallpaperHandler = null;
    private static final int LOAD_PHOTO_FINISHED = 0;
	private static final int SET_WALLPAPER_FINISHED = 1;
	private Bitmap mBitmap;
	private boolean mWallpaperSettingSuccess = true;
	private boolean mIsWallpaperSetting = false;
	private WallpaperManager mWallpaperManager = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {   
        Log.d(TAG,"onCreate");
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_wallpaper_preview);
    	
    	m_ResourceMgr = this.getResources();
    	
    	banner_slideshow    = (ImageView)findViewById(R.id.banner_slideshow);
 	    banner_repeat    = (ImageView)findViewById(R.id.banner_repeat);

 	    move_left_btm = (ImageView)findViewById(R.id.move_left_btm);
 	    move_right_btm = (ImageView)findViewById(R.id.move_right_btm);
 	    move_top_btm = (ImageView)findViewById(R.id.move_top_btm);
 	    move_bottom_btm = (ImageView)findViewById(R.id.move_bottom_btm);
 	    pic_multiple = (TextView)findViewById(R.id.pic_multiple);
 	    
 	    mMediaApplicationMap =(MediaApplication)getApplication();
    	//mDataProvider = map.getPhotoDataProvider();
		mPhotoPlaybackView = (SurfaceView) findViewById(R.id.m_photoPlaybackSurfaceView);
          
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
       
        mLastControlTime=(new Date(System.currentTimeMillis())).getTime();

       
        mCheckTimerHandler=new Handler(){
			@Override  
	        public void handleMessage(Message msg)  
	        { 
	        	switch (msg.what)  
	            {  
	              case 0: 
	            	  dofullscrean();
	               	  mLastControlTime=(new Date(System.currentTimeMillis())).getTime();
					  break;  
	             default:
	            	 break;
	            }  	          
	          super.handleMessage(msg);  
	        }  
	    };
	    
	    mSetBannerHandler = new Handler(){
	    	@Override  
	        public void handleMessage(Message msg)  
	        {
	    		int position = msg.arg1;
	        	switch (msg.what)  
	            {  
	              case 0: 
	            	  dosetbannerexif(position);
					  break;  
	             default:
	            	 break;
	            }  
	          
	          super.handleMessage(msg);  
	        }  
	    };
	   	    	    
	    mTv = (TvManager) this.getSystemService("tv");

	    move_top_btm.setImageResource(R.drawable.photo_player_up);
	    Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.photo_player_up)).getBitmap();
	    int bitmapWidth = bitmap.getWidth();
	    int bitmapHeight = bitmap.getHeight();
	    Matrix matrix = new Matrix();
	    matrix.reset();
	    matrix.setRotate(90);
	    Bitmap rightDirection = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true);
	    matrix.reset();
	    matrix.setRotate(180);
	    Bitmap bottomDirection = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true);
	    matrix.reset();
	    matrix.setRotate(-90);
	    Bitmap leftDirection = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true);
	    move_right_btm.setImageBitmap(rightDirection);
	    move_left_btm.setImageBitmap(leftDirection);
	    move_bottom_btm.setImageBitmap(bottomDirection);
	    
	    mTvawFilter = new IntentFilter(); 
    	mTvawFilter.addAction("com.rtk.mediabrowser.PlayService");
    	mTvawReceiver = new BroadcastReceiver()
        {
            public void onReceive(Context context, Intent intent)
            {
            	if (intent.getStringExtra("action").equals("PAUSE"))
                {
                    	m_checkResultHandlerTimer.removeCallbacks(m_checkResultTimerCb);
            			m_slideShowHandlerTimer.removeCallbacks(m_slideShowTimerCb);
            			banner_slideshow.setImageResource(R.drawable.photo_play_off);
                    } 
                    else if (intent.getStringExtra("action").equals("PLAY"))
                    {          	
                    	if(m_totalCnt > 0)
           		     	{	                    	
                    		m_checkResultHandlerTimer.postDelayed(m_checkResultTimerCb, m_checkResultTime);
           		     	}
                    	banner_slideshow.setImageResource(R.drawable.photo_play_on);			
                    }
                    else if((intent.getStringExtra("action").equals("FINISH")))
                    {
                    	finish();
                    	return;
                    }

            }			
        };
	    
	    new Thread(new Runnable() {
    		public void run() {
    			long curtime = 0;
    			while(true)
    			{
    				if(mActivityDestroyFlag == 1)
    					break;
    				curtime = (new Date(System.currentTimeMillis())).getTime();
    				if(!mIsFullScrean)
    				{
	    				if(curtime - mLastControlTime > mBannerShowTime)
	    				{
	    					Message msg = new Message();
	    					msg.what = 0;
	    					mCheckTimerHandler.sendMessage(msg);
	    				}
    				} 
    				try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}	
    			}
    		}
    	}).start();
	    Intent intent = getIntent();
        m_initPos = intent.getIntExtra("initPos", 0);       
        m_totalCnt = intent.getIntExtra("totalCnt", 0);         
        mBrowserType = intent.getIntExtra("browserType", 0);       
        mIsFromAnywhere = intent.getBooleanExtra("isanywhere", false);

        if(mIsFromAnywhere == false)
        {
        	if (mPhotoList == null) {
        		mPhotoList = mMediaApplicationMap.getPhotoFileInfoList();
    		}
        	int photoListSize = mPhotoList.size();
            mPhotoDirNum = photoListSize;
            m_filePathStrArray = new String[photoListSize];
            {
	            int tmpj = 0;
	            if(m_totalCnt >0)
	            {   
	            	//get filePathArrayStr
	            	for(int i=0;i<photoListSize;i++)
	            	{
	            		if(mPhotoList.get(i).getmFileType() == FileFilterType.DEVICE_FILE_PHOTO)
	        			{
	            			m_filePathStrArray[tmpj] = mPhotoList.get(i).getPath();
	            			tmpj++;
	        			}
	            	}
	            }	
	            mPhotoDirNum = m_filePathStrArray.length - tmpj;
            }
        }
        else
        {
        	m_filePathStrArray = intent.getStringArrayExtra("filelist");
        	m_totalCnt = m_filePathStrArray.length;
        	mPhotoDirNum = 0;       	
            registerReceiver(mTvawReceiver, mTvawFilter);
        }
        mRepeatIndex = intent.getIntExtra("mRepeatMode", 0);
        mRepeatMode = mRepeatIndex;
        mIntervalTime = intent.getIntExtra("mIntervalTime", 5);
        m_slideShowTime = mIntervalTime * 1000;
        
        if(m_initPos < 0 && m_initPos > m_totalCnt-1)
        {
            m_initPos = 0;
        }
        
        m_currentPlayIndex = m_initPos;
	    
   
	 // InitQuickMenu
        mQuickmenuAdapter = new QuickMenuPhotoAdapter(this);
        mQuickmenu=new QuickMenu(this, mQuickmenuAdapter);
        OnItemClickListener quickmenuItemClickListener = new OnItemClickListener()
        {
        	@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				switch(position)
				{
					case 0:
					{
						mIntervalTime = mIntervalTime + 5;	
						mIntervalTime = mIntervalTime % 30;
						TextView OptionText = (TextView)arg1.findViewById(R.id.menu_option);
						OptionText.setText(String.valueOf(mIntervalTime)
		            			+ getResources().getString(R.string.timeUnit));
						m_slideShowTime = mIntervalTime * 1000;
						break;
					}
					case 1:
					{
						mRepeatIndex++;
						mRepeatIndex %= 2;
						TextView OptionText = (TextView)arg1.findViewById(R.id.menu_option);
						OptionText.setText(repeats[mRepeatIndex].name());
						mRepeatMode = mRepeatIndex;
						check_repeat_mode();
						break;
					}
					case 2:
					{
						mSleepTime = mSleepTime + 5;	
						mSleepTime = mSleepTime % 30;
						TextView OptionText = (TextView)arg1.findViewById(R.id.menu_option);
						if(mSleepTime == 0)
						{
							OptionText.setText("OFF");
						}
		            	else
		            	{
		            		OptionText.setText(
		            			String.valueOf(mSleepTime)
		            			+ getResources().getString(R.string.timeUnit));
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
        OnItemSelectedListener quickmenuItemSelectedListener = new OnItemSelectedListener()
        {
        	@Override
			public void onItemSelected(AdapterView<?> arg0, View view,
					int position, long arg3) {
				// TODO Auto-generated method stub
				Log.d(TAG, "Quick Menu ListView onItemSelected");
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
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				Log.d(TAG, "Quick Menu ListView onNothingSelected");
			}       	
        };
        OnKeyListener quickmenuKeyClickListener = new OnKeyListener()
        {
        	@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
        		ListView quickMenuContent = mQuickmenu.getListView();
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
		        				mIntervalTime = mIntervalTime + 5;	
								mIntervalTime = mIntervalTime % 30;
								TextView OptionText = (TextView) (quickMenuContent.getChildAt(position).findViewById(R.id.menu_option));
								OptionText.setText(String.valueOf(mIntervalTime)
				            			+getResources().getString(R.string.timeUnit));
								
								m_slideShowTime = mIntervalTime * 1000;
								break;
		        			}
		        			case 1:
		        			{
		        				mRepeatIndex++;
								mRepeatIndex %= 2;
								TextView OptionText = (TextView) (quickMenuContent.getChildAt(position).findViewById(R.id.menu_option));
								OptionText.setText(repeats[mRepeatIndex].name());
								mRepeatMode = mRepeatIndex;
								check_repeat_mode();
								break;
		        			}
		        			case 2:
		        			{
		        				mSleepTime = mSleepTime + 5;	
								mSleepTime = mSleepTime % 30;
								TextView OptionText = (TextView) (quickMenuContent.getChildAt(position).findViewById(R.id.menu_option));
								if(mSleepTime == 0)
								{
									OptionText.setText("OFF");
								}
				            	else
				            	{
				            		OptionText.setText(
				            			String.valueOf(mSleepTime) + getResources().getString(R.string.timeUnit));
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
		        				mIntervalTime = mIntervalTime - 5 >= 0 ? mIntervalTime - 5 : 30;	
								m_slideShowTime = mIntervalTime;
								TextView OptionText = (TextView) (quickMenuContent.getChildAt(position).findViewById(R.id.menu_option));
								OptionText.setText(String.valueOf(mIntervalTime)
				            			+ getResources().getString(R.string.timeUnit));
								break;
		        			}
		        			case 1:
		        			{
		        				mRepeatIndex = (mRepeatIndex-1) >= 0 ? (mRepeatIndex-1) : 1;
		        				TextView OptionText = (TextView) (quickMenuContent.getChildAt(position).findViewById(R.id.menu_option));
								OptionText.setText(repeats[mRepeatIndex].name());
								mRepeatMode = mRepeatIndex;
								check_repeat_mode();
								break;
		        			}
		        			case 2:
		        			{
		        				mSleepTime = mSleepTime - 5 >= 0 ? mSleepTime-5 : 30;	
		        				TextView OptionText = (TextView) (quickMenuContent.getChildAt(position).findViewById(R.id.menu_option));
								if(mSleepTime == 0)
								{
									OptionText.setText("OFF");
								}
				            	else
				            	{
				            		OptionText.setText(
				            			String.valueOf(mSleepTime)
				            			+getResources().getString(R.string.timeUnit));
				            	}
								break;
		        			}
		        		}

		        		return true;
		        	}
		        	if (keyCode == KeyEvent.KEYCODE_Q)
		        	{
		        		mQuickmenu.dismiss();
		        		return true;
		        	}
	        	}
				if (event.getAction() == KeyEvent.ACTION_UP)
				{
					switch(keyCode) {
						case KeyEvent.KEYCODE_DPAD_RIGHT:
						case KeyEvent.KEYCODE_DPAD_LEFT:
						case KeyEvent.KEYCODE_Q:
							return true;
						default:
							return false;
					}
				}
				return false;
			}
        };
        mQuickmenu.AddOnItemClickListener(quickmenuItemClickListener);
        mQuickmenu.AddOnItemSelectedListener(quickmenuItemSelectedListener);
        mQuickmenu.AddOnKeyClickListener(quickmenuKeyClickListener);
       
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
                			Toast.makeText(WallpaperPreviewActivity.this
                  				  , WallpaperPreviewActivity.this.getResources().getText(R.string.device_removed_toast)
                  				  ,Toast.LENGTH_SHORT).show();
                			setResult(-1);
                			finish();
                			return ;
                  	  	}
                		else
                  		{
                			File file=new File(mPhotoList.get(0).getPath());
                			if(file == null || !file.exists())
                			{
                				setResult(-2);
                				finish();
                				return;
                			}
                			else
                			{
                				break;//the usb plug out is not the usb which is playing,do noting
                			}
          		  
                  		}
                	}
                	case UsbController.PLUG_IN:
                	{
                		break;//do nothing when plug in
                	}
               }
        	
			}

			@Override
			public void OnUsbCheck(String path,int direction) {
				// TODO Auto-generated method stub
				
			}
        };       
        mUsbCtrl.setOnUsbCheckListener(usbCheckListener);

		TextView textView = (TextView)findViewById(R.id.textView_setWallpaper);
		textView.setAlpha(0);
		
		m_setWallpaperHandler = new Handler(){
			@Override
			public void handleMessage(Message msg){
				switch(msg.what){
				case LOAD_PHOTO_FINISHED:
					setWallpaper();
					break;
				case SET_WALLPAPER_FINISHED:
					checkWallpaerSettingResult();
					break;
				default:
					break;
				}
				super.handleMessage(msg);
			}
		};
		
		mWallpaperManager = (WallpaperManager) this.getApplicationContext().getSystemService(
                Context.WALLPAPER_SERVICE);
    }
    private void dosetmoveicon()
    {
    	if(mIsZoomModel != NOT_ZOOM_MODE)
    	{
    		if(mIsZoomModel > 0)
    		{
    			move_left_btm.setVisibility(View.VISIBLE);
    			move_right_btm.setVisibility(View.VISIBLE);
    			move_top_btm.setVisibility(View.VISIBLE);
    			move_bottom_btm.setVisibility(View.VISIBLE);
    		}   		
    	}  
    	else
    	{
    		move_left_btm.setVisibility(View.INVISIBLE);
			move_right_btm.setVisibility(View.INVISIBLE);
			move_top_btm.setVisibility(View.INVISIBLE);
			move_bottom_btm.setVisibility(View.INVISIBLE);  		
    	}   	
    	
    	RelativeLayout llayout_banner_photo = (RelativeLayout)findViewById(R.id.banner_photo);
    	llayout_banner_photo.setLayoutParams(llayout_banner_photo.getLayoutParams());
    }

    @Override
    public void onNewIntent(Intent intent)
    {
    	Log.v(TAG, "onNewIntent");
    	m_filePathStrArray = intent.getStringArrayExtra("filelist");
    	m_totalCnt = m_filePathStrArray.length;
    	mPhotoDirNum = 0;

    	mUsbCtrl.RegesterBroadcastReceiver();
    	registerReceiver(mTvawReceiver, mTvawFilter);
    	
    	super.onNewIntent(intent);
    }
    
    private void dosetbannerexif(int position)
    {
    	banner_pic_index      = (TextView)findViewById(R.id.banner_pic_index);
    	banner_pic_name       = (TextView)findViewById(R.id.banner_pic_name);
    	banner_pic_timeinfo   = (TextView)findViewById(R.id.banner_pic_timeinfo);
    	banner_pic_resolution = (TextView)findViewById(R.id.banner_pic_resolution);
    	
    	ExifInterface exif = null;
    	try {
    		exif = new ExifInterface(m_filePathStrArray[position]);   		
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    	String title = null;
    	if(mIsFromAnywhere ==  false)
    	{
    		title = mPhotoList.get(position+mPhotoDirNum).getFileName();//mDataProvider.GetTitleAt(position);
    	}
    	else
    	{
    		title = m_filePathStrArray[position].substring(m_filePathStrArray[position].lastIndexOf("/")+1);
    	}
        String date    = exif.getAttribute(ExifInterface.TAG_DATETIME);
        String length  = exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
        String width   = exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
        if(date==null)
        {
			File _file = new File(m_filePathStrArray[position]);//mDataProvider.GetDataAt(position));
			date = WallpaperActivity.getFileCreateDate(_file);
        }
        else
        {
        	date = WallpaperActivity.dateFormate(date);
        }
        if(length.equals("0") && width.equals("0"))
        {
        	//get height and width by not decoding bitmap
        	BitmapFactory.Options options = new BitmapFactory.Options();  
            options.inJustDecodeBounds = true;  
            BitmapFactory.decodeFile(m_filePathStrArray[position], options); // bitmap is null
            length = String.valueOf(options.srcHeight == -1 ? 0 : options.srcHeight);
            width = String.valueOf(options.srcWidth == -1 ? 0 : options.srcWidth);
        }
        banner_pic_index.setText(String.valueOf(position+1)
        		+ "/"
        		+ (m_filePathStrArray.length-mPhotoDirNum));//mDataProvider.GetSize());
        banner_pic_name.setText(title);
        banner_pic_timeinfo.setText(date);
        banner_pic_resolution.setText(width + "X" + length);
    }   
    

	protected void dofullscrean() 
	{
		// TODO Auto-generated method stub
		
		Log.d(TAG,"dofullscrean(): photo atv view W:["+mPhotoPlaybackView.getWidth()+"]");
		Log.d(TAG,"dofullscrean(): photo atv view H:["+mPhotoPlaybackView.getHeight()+"]");
		
		dosetmoveicon();
		RelativeLayout llayout_banner_photo=(RelativeLayout)findViewById(R.id.banner_photo);
		RelativeLayout.LayoutParams llayoutparams_banner_photo = new RelativeLayout.LayoutParams(
	              LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		llayoutparams_banner_photo.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		llayoutparams_banner_photo.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
	    llayoutparams_banner_photo.width  = mDisplayMetrics.widthPixels;
	    llayoutparams_banner_photo.height = 0;//llayout_params_gallery.height*3/5;
	    llayout_banner_photo.setLayoutParams(llayoutparams_banner_photo);
	          
       	LinearLayout l_atvview=(LinearLayout)findViewById(R.id.RtkAtvView_LinearLayout);
       	RelativeLayout.LayoutParams llayout_params_atvview = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
       	llayout_params_atvview.width=mDisplayMetrics.widthPixels;
        llayout_params_atvview.height=mDisplayMetrics.heightPixels;//-llayout_params_gallery.height;//- rlayout_params_title.height; 
        l_atvview.setLayoutParams(llayout_params_atvview);
 
        SurfaceView sv=(SurfaceView)findViewById(R.id.m_photoPlaybackSurfaceView);
        LinearLayout.LayoutParams   llp   =   (LinearLayout.LayoutParams) sv.getLayoutParams();
        llp.width  = mDisplayMetrics.widthPixels;
        llp.height = mDisplayMetrics.heightPixels;//-llayout_params_gallery.height;//- rlayout_params_title.height;
        mPhotoPlaybackView.getHolder().setFixedSize(llp.width,llp.height); 
        mPhotoPlaybackView.setLayoutParams(llp); 
        
        mIsFullScrean = true;
	}

	@Override
	public void onDestroy()
	{		
		mActivityDestroyFlag = 1;
		super.onDestroy();
	}
    
    @Override
    public void onResume()
    {
        super.onResume();
        Log.d(TAG,"onResume");
        mActivityPauseFlag = 0;
        StartPictureKit();  
        initLayout();
	    
        mPhotoPlaybackView.setFocusable(false);
        mPhotoPlaybackView.setFocusableInTouchMode(false);
			
        // set source
     		
     	if (mMediaApplicationMap.isIs4k2k()) 
     	{
            mTv.setDisplayWindow(0, 0, 3840, 2160);
     	}
     	else 
     	{
     		mTv.setDisplayWindow(0, 0, 1920, 1080);
     	}
		                
        if(m_totalCnt > 0)
        {	
	        m_checkFirstPictureHandler.postDelayed(m_checkFirstDecodeCb, 100);
	        //     DecodePictureKit(m_filePathStrArray[m_currentPlayIndex]);
        }     

		mIsSlideShowModel = false;
		mUsbCtrl.RegesterBroadcastReceiver();
		
		//Show Set_wallpaper button
        Button button = (Button)findViewById(R.id.button_setWallpaper);
        button.setAlpha(1);
        button.setClickable(true);
		button.setFocusable(true);
		button.setFocusableInTouchMode(true);
		button.requestFocus();
   }

    public class SurfaceListener implements SurfaceHolder.Callback{

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
		}
		
	}
    @Override
    public void onPause()
    {
        // TODO Auto-generated method stub
        super.onPause();
        Log.v(TAG, "onPause");
        mActivityPauseFlag = 1;
        m_checkResultHandlerTimer_forOnKey.removeCallbacks(m_checkResultTimerCb_forOnkeyLeft);
        m_checkResultHandlerTimer_forOnKey.removeCallbacks(m_checkResultTimerCb_forOnkeyRight);
        m_checkResultHandlerTimer.removeCallbacks(m_checkResultTimerCb);
        m_checkFirstPictureHandler.removeCallbacks(m_checkFirstDecodeCb); 
        if(m_pPictureKit != null) {
        	m_startdecode = false;
        	m_pPictureKit.stopPictureKit();
        	m_pPictureKit = null;
        }
        mIsSlideShowModel=false;
        check_slideshow_mode();       

        if(mIsFromAnywhere == true)
        {
        	unregisterReceiver(mTvawReceiver);
        }
        mUsbCtrl.UnRegesterBroadcastReceiver();
    }
    
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	Log.d(TAG, "onKeyDown : "+event.toString());
    	mLastControlTime=(new Date(System.currentTimeMillis())).getTime();
						
			if(keyCode==KeyEvent.KEYCODE_M)//82)
			{
				mBannerShowTime = 60000;
					if(mIsFullScrean)
					{
						dosetmoveicon();
						
						RelativeLayout llayout_banner_photo=(RelativeLayout)findViewById(R.id.banner_photo);
						 
						RelativeLayout.LayoutParams llayoutparams_banner_photo = new RelativeLayout.LayoutParams(
					            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						llayoutparams_banner_photo.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
						llayoutparams_banner_photo.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
					    llayoutparams_banner_photo.width=mDisplayMetrics.widthPixels;
					    llayoutparams_banner_photo.height=109;//llayout_params_gallery.height*3/5;
					    llayout_banner_photo.setLayoutParams(llayoutparams_banner_photo);
				     						
				       	LinearLayout l_atvview=(LinearLayout)findViewById(R.id.RtkAtvView_LinearLayout);
				       	RelativeLayout.LayoutParams llayout_params_atvview = new RelativeLayout.LayoutParams(
				                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				       	llayout_params_atvview.width=mDisplayMetrics.widthPixels;
				        llayout_params_atvview.height=mDisplayMetrics.heightPixels-llayoutparams_banner_photo.height;//- rlayout_params_title.height; 
				        l_atvview.setLayoutParams(llayout_params_atvview);

				        SurfaceView sv=(SurfaceView)findViewById(R.id.m_photoPlaybackSurfaceView);
				        LinearLayout.LayoutParams   llp   =   (LinearLayout.LayoutParams) sv.getLayoutParams();
				        llp.width	=	mDisplayMetrics.widthPixels;
				        llp.weight=0;
				        llp.height   =  mDisplayMetrics.heightPixels-llayoutparams_banner_photo.height;//- rlayout_params_title.height;
				      
				        mPhotoPlaybackView.getHolder().setFixedSize(llp.width,llp.height); 
				        mPhotoPlaybackView.setLayoutParams(llp); 
					}					
					else
					{
						dosetmoveicon();
												
						RelativeLayout llayout_banner_photo=(RelativeLayout)findViewById(R.id.banner_photo);

						RelativeLayout.LayoutParams llayoutparams_banner_photo = new RelativeLayout.LayoutParams(
						              LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						llayoutparams_banner_photo.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
						llayoutparams_banner_photo.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
					    llayoutparams_banner_photo.width=0;
					    llayoutparams_banner_photo.height=0;
					    llayout_banner_photo.setLayoutParams(llayoutparams_banner_photo);
						LinearLayout l_atvview=(LinearLayout)findViewById(R.id.RtkAtvView_LinearLayout);
					    RelativeLayout.LayoutParams llayout_params_atvview = new RelativeLayout.LayoutParams(
					                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					 
					    llayout_params_atvview.width=mDisplayMetrics.widthPixels;
					    llayout_params_atvview.height=mDisplayMetrics.heightPixels;//- rlayout_params_title.height; 
					    l_atvview.setLayoutParams(llayout_params_atvview);
				
				        SurfaceView sv=(SurfaceView)findViewById(R.id.m_photoPlaybackSurfaceView);
				        LinearLayout.LayoutParams   llp   =   (LinearLayout.LayoutParams) sv.getLayoutParams();
				        llp.width	=	mDisplayMetrics.widthPixels;
				        llp.weight=0;
				        llp.height   =  mDisplayMetrics.heightPixels;//-llayout_params_gallery.height;//- rlayout_params_title.height;
				      
				        mPhotoPlaybackView.getHolder().setFixedSize(llp.width,llp.height); 
				        mPhotoPlaybackView.setLayoutParams(llp); 	
					}
					mIsFullScrean=!mIsFullScrean;
					
				return true;
			}
			else if(keyCode==KeyEvent.KEYCODE_DPAD_UP)
			{
				if(mIsZoomModel == NOT_ZOOM_MODE)
				{
					mTv.rightRotate();
					
					//pause ,then play,to simulate reseting inteval time
					if(mIsSlideShowModel)
					{
						m_checkResultHandlerTimer.removeCallbacks(m_checkResultTimerCb);
						m_slideShowHandlerTimer.removeCallbacks(m_slideShowTimerCb);
							
						if(m_totalCnt > 0)
						{
							m_checkResultHandlerTimer.postDelayed(m_checkResultTimerCb, m_checkResultTime);
						}
					}
					//end
				}
				else
				{
					mTv.onZoomMoveUp();
				}
				return true;
			}
			else if(keyCode==KeyEvent.KEYCODE_DPAD_DOWN)
			{
				if(mIsZoomModel == NOT_ZOOM_MODE)
				{
					mTv.leftRotate(); 
					
					//pause ,then play,to simulate reseting inteval time
					if(mIsSlideShowModel)
					{
						m_checkResultHandlerTimer.removeCallbacks(m_checkResultTimerCb);
						m_slideShowHandlerTimer.removeCallbacks(m_slideShowTimerCb);
							
						if(m_totalCnt > 0)
						{
							m_checkResultHandlerTimer.postDelayed(m_checkResultTimerCb, m_checkResultTime);
						}
					}
					//end
				}
				else
				{
					mTv.onZoomMoveDown();
				}
				return true;
			}
			else if(keyCode==KeyEvent.KEYCODE_DPAD_LEFT)
			{
				if(mIsZoomModel == NOT_ZOOM_MODE)
				{
					if(m_pPictureKit!=null)
					{
			        
			        	if(mRepeatMode==1)
			        	{	
							if(m_currentPlayIndex-1<0)	
							{
								m_currentPlayIndex=m_totalCnt-1;
								DecodePictureKit(m_filePathStrArray[m_currentPlayIndex]);
							}
							else
							{
								m_currentPlayIndex--;
								DecodePictureKit(m_filePathStrArray[m_currentPlayIndex]);					
							}
							dosetbannerexif(m_currentPlayIndex);
			        	}
			        	else if(mRepeatMode==0)
			        	{
			        		if(m_currentPlayIndex-1 >= 0)	
							{
								m_currentPlayIndex--;
								DecodePictureKit(m_filePathStrArray[m_currentPlayIndex]);					
							}
			        		else
			        		{
			        			setResult(m_currentPlayIndex);
								finish();
								return true;
			        		}
							dosetbannerexif(m_currentPlayIndex);			        		
			        	}
					}
					if(!mIsSlideShowModel)
						m_checkResultHandlerTimer_forOnKey.postDelayed(m_checkResultTimerCb_forOnkeyLeft,200);
					check_slideshow_mode();
				}
				else
				{
					mTv.onZoomMoveLeft();
				}
				return true;
			}
			else if(keyCode==KeyEvent.KEYCODE_DPAD_RIGHT)
			{
				if(mIsZoomModel==NOT_ZOOM_MODE)
				{
					if(m_pPictureKit!=null)
					{						
						if(mRepeatMode==1) 
						{					        
							if(m_currentPlayIndex+1 >= m_totalCnt)	
							{
								m_currentPlayIndex=0;
								DecodePictureKit(m_filePathStrArray[m_currentPlayIndex]);
							}
							else
							{
								m_currentPlayIndex++;
								DecodePictureKit(m_filePathStrArray[m_currentPlayIndex]);					
							}
							dosetbannerexif(m_currentPlayIndex);		
						}
						else if(mRepeatMode == 0)
						{
							
							if(m_currentPlayIndex+1 >= m_totalCnt)	
							{
								setResult(m_currentPlayIndex);
								finish();
								return true;
							}
							else
							{
								m_currentPlayIndex++;
								DecodePictureKit(m_filePathStrArray[m_currentPlayIndex]);					
							}
							dosetbannerexif(m_currentPlayIndex);							
						}
						//check_slideshow_mode();					
					}
					if(!mIsSlideShowModel)
						m_checkResultHandlerTimer_forOnKey.postDelayed(m_checkResultTimerCb_forOnkeyRight,200);
					check_slideshow_mode();
				}
				else
				{
					mTv.onZoomMoveRight();
				}
				return true;
			}
			else if(keyCode==KeyEvent.KEYCODE_1)
			{
				if(mIsZoomModel == NOT_ZOOM_MODE)
				{
					mIsSlideShowModel = !mIsSlideShowModel;	
					check_slideshow_mode();
				}
				return true;
			}
			else if(keyCode==KeyEvent.KEYCODE_2)
			{
				;
				return true;
			}
			else if(keyCode==KeyEvent.KEYCODE_3)
			{
				mRepeatMode=(mRepeatMode+1) % 2;
				check_repeat_mode();
				return true;
			}
			else if(keyCode==KeyEvent.KEYCODE_4)
			{
				;
				return true;
			}
			else if(keyCode==KeyEvent.KEYCODE_Z)
			{
				if(mIsZoomModel+1>4)
				{
					;
				}
				else
				{
					mIsZoomModel++;
					mTv.zoomIn();
					check_zoom_model();
				}	
				return true;
			}
			else if(keyCode==KeyEvent.KEYCODE_X)
			{
				if(mIsZoomModel-1<0)
				{
					;
				}
				else
				{
					mIsZoomModel--;
					mTv.zoomOut();
					check_zoom_model();
				}
				return true;
			}
			else if(keyCode==KeyEvent.KEYCODE_C)
			{
				mTv.leftRotate();
				return true;
			}
			else if(keyCode==KeyEvent.KEYCODE_V)
			{
				mTv.rightRotate();
				return true;
			}
			else if(keyCode==KeyEvent.KEYCODE_B)
			{
				if(mIsZoomModel == NOT_ZOOM_MODE)
				{
					setResult(m_currentPlayIndex);
					finish();	
					return true;
				}
				else
				{
					for( ; 0<mIsZoomModel ; mIsZoomModel-- )
					{
						mTv.zoomOut();
					}
					check_zoom_model();
				}	
				return true;
			}
			else if(keyCode == KeyEvent.KEYCODE_BACK)
			{
				Log.d(TAG,"ESC  "+m_currentPlayIndex);
				setResult(m_currentPlayIndex);
				finish();	
				return true;
			}
			else if(keyCode == KeyEvent.KEYCODE_E)
			{
				setResult(-10);
				finish();	
				return true;
			}
			else if(keyCode == KeyEvent.KEYCODE_Q)
			{
	    		if(mQuickmenu.isShowing() == true)
	    			mQuickmenu.dismiss();
	    		else
	    		{  
	    			mQuickmenu.showQuickMenu(14,1266-mQuickmenu.getHeight());
	    		}
	    		return true;
			}
			else if(keyCode == KeyEvent.KEYCODE_W)
			{
				if(mIsZoomModel != NOT_ZOOM_MODE)
					mTv.onZoomMoveUp();
				return true;
			}
			else if(keyCode == KeyEvent.KEYCODE_S)
			{
				if(mIsZoomModel != NOT_ZOOM_MODE)
					mTv.onZoomMoveDown();
				return true;
			}
			else if(keyCode == KeyEvent.KEYCODE_A)
			{
				if(mIsZoomModel != NOT_ZOOM_MODE)
					mTv.onZoomMoveLeft();
				return true;
			}
			else if(keyCode == KeyEvent.KEYCODE_D)
			{
				if(mIsZoomModel != NOT_ZOOM_MODE)
					mTv.onZoomMoveRight();
				return true;
			}
		
    	return super.onKeyDown(keyCode, event);
    } 
    
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
    	Log.d(TAG, "onKeyUp : " + event.toString());
    	
    	switch(keyCode) {
	    	case KeyEvent.KEYCODE_M:
	    	case KeyEvent.KEYCODE_DPAD_UP:
	    	case KeyEvent.KEYCODE_DPAD_DOWN:
	    	case KeyEvent.KEYCODE_DPAD_LEFT:
	    	case KeyEvent.KEYCODE_DPAD_RIGHT:
	    	case KeyEvent.KEYCODE_1:
	    	case KeyEvent.KEYCODE_2:
	    	case KeyEvent.KEYCODE_3:
	    	case KeyEvent.KEYCODE_4:
	    	case KeyEvent.KEYCODE_Z:
	    	case KeyEvent.KEYCODE_X:
	    	case KeyEvent.KEYCODE_C:
	    	case KeyEvent.KEYCODE_V:
	    	case KeyEvent.KEYCODE_B:
	    	case KeyEvent.KEYCODE_BACK:
	    	case KeyEvent.KEYCODE_E:
	    	case KeyEvent.KEYCODE_Q:
	    	case KeyEvent.KEYCODE_W:
	    	case KeyEvent.KEYCODE_S:
	    	case KeyEvent.KEYCODE_A:
	    	case KeyEvent.KEYCODE_D:
	    		return true;
	    	default:
	    		break;
    	}
    	
    	return super.onKeyUp(keyCode, event);
    }
    
    private void check_slideshow_mode() {
		// TODO Auto-generated method stub
    	if(mIsSlideShowModel == true)
		{
			if(m_totalCnt > 0)
		     {
				 m_checkResultHandlerTimer.postDelayed(m_checkResultTimerCb, m_checkResultTime);
		     }
			banner_slideshow.setImageResource(R.drawable.photo_play_on);			
		}
		else if(mIsSlideShowModel == false)
		{
			m_checkResultHandlerTimer.removeCallbacks(m_checkResultTimerCb);
			m_slideShowHandlerTimer.removeCallbacks(m_slideShowTimerCb);
			banner_slideshow.setImageResource(R.drawable.photo_play_off);
		}
    }
    private void check_repeat_mode()
    {
    	if(mRepeatMode == 1)
    	{
    		banner_repeat.setImageResource(R.drawable.photo_repeat_all_on);
    	}
    	else if(mRepeatMode == 0)
    	{
    		banner_repeat.setImageResource(R.drawable.photo_repeat_all_off);
    	}
    }
    private void check_zoom_model()
    {	
    	if(mIsZoomModel != NOT_ZOOM_MODE)
    	{
    		//if in zoom model,make sure the slide show stops
    		if(mIsSlideShowModel == true)
    		{
	    		if(m_pPictureKit!=null) {
		        }
				m_checkResultHandlerTimer.removeCallbacks(m_checkResultTimerCb);
				banner_slideshow.setImageResource(R.drawable.photo_play_off);
    		}
    		if(mIsZoomModel > 0)
    		{
    			move_left_btm.setVisibility(View.VISIBLE);
    			move_right_btm.setVisibility(View.VISIBLE);
    			move_top_btm.setVisibility(View.VISIBLE);
    			move_bottom_btm.setVisibility(View.VISIBLE);
    		}
    		pic_multiple.setVisibility(View.VISIBLE);
			switch(mIsZoomModel)
			{
    			case 1:
    			{
    				pic_multiple.setText("X2");
    				break;
    			}
    			case 2:
    			{
    				pic_multiple.setText("X4");
    				break;
    			}
    			case 3:
    			{
    				pic_multiple.setText("X8");
    				break;
    			}
    			case 4:
    			{
    				pic_multiple.setText("X16");
    				break;
    			}
    			case -1:
    			{
    				pic_multiple.setText("X1/2");
    				break;
    			}case -2:
    			{
    				pic_multiple.setText("X1/4");
    				break;
    			}case -3:
    			{
    				pic_multiple.setText("X1/8");
    				break;
    			}   			
			}
    	}  
    	else
    	{
    		if(mIsSlideShowModel == true)
    		{	    	
	    		if(m_totalCnt > 0)
			     {
					 m_checkResultHandlerTimer.postDelayed(m_checkResultTimerCb, m_checkResultTime);
			     }
				banner_slideshow.setImageResource(R.drawable.photo_play_on);
    		}
		
    		move_left_btm.setVisibility(View.INVISIBLE);
			move_right_btm.setVisibility(View.INVISIBLE);
			move_top_btm.setVisibility(View.INVISIBLE);
			move_bottom_btm.setVisibility(View.INVISIBLE);
			pic_multiple.setVisibility(View.INVISIBLE);    		
    	}    	
    	RelativeLayout llayout_banner_photo=(RelativeLayout)findViewById(R.id.banner_photo);
    	llayout_banner_photo.setLayoutParams(llayout_banner_photo.getLayoutParams());
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        return true;
    }
    
    public void NextImage()
    {
        if(m_totalCnt<=0)
            return;
        
        if(m_currentPlayIndex < m_totalCnt-1 )
    	{
    	   m_currentPlayIndex++;
    	   
    	   Log.e(TAG,"NextImage m_currentPlayIndex:["+m_currentPlayIndex+"]");
           
           DecodePictureKit(m_filePathStrArray[m_currentPlayIndex]);
        }
        else if(mRepeatMode == 1)
        {
            m_currentPlayIndex= 0;
            Log.e(TAG,"NextImage m_currentPlayIndex:["+m_currentPlayIndex+"]");            
            DecodePictureKit(m_filePathStrArray[m_currentPlayIndex]);
        }
        else if(mRepeatMode == 0)
        {
        	Log.d(TAG,"NEXT  "+m_currentPlayIndex);
        	setResult(m_currentPlayIndex);
			finish();
			return ;
        }      
        m_checkResultHandlerTimer.postDelayed(m_checkResultTimerCb, m_checkResultTime);
        
        //  TSB spec
        Message msg = new Message();
        msg.arg1=m_currentPlayIndex;
        msg.what=0;
        mSetBannerHandler.sendMessage(msg);
    }
  
    public void StartPictureKit()
    {    	
    	new Thread(new Runnable() 
    	{
    		public void run() 
    		{
    			long ret;
    			int timeout = 0;
		    	if(m_pPictureKit == null) 
		    	{
		    		m_pPictureKit = new PictureKit(WallpaperPreviewActivity.this);        	
		        	Log.d(TAG,"New RTK_PictureKit in StartPictureKit");
		    	}
		    	while(m_startdecode == false) 
		    	{		    		
		    		if(m_pPictureKit!=null) 
		    		{		    		
			    		ret = m_pPictureKit.startPictureKit();
			    		if(ret == -1) 
			    		{
			    			Log.e(TAG,"New RTK_PictureKit memory is not enough => fail decode");
			    			break;
			    		} 
			    		else if((ret == 1)||(ret == -2))
			    		{
			    			try {
									Thread.sleep(100);
									Log.d(TAG,"StartPictureKit need to do again :"+ret);
									Log.e(TAG,"startdecode do :"+timeout);
						    } catch (InterruptedException e) {
						       // TODO Auto-generated catch block
						       e.printStackTrace();
						    }
			    			timeout++;
			    			if(timeout > 50) {
			    				Log.e(TAG,"startdecode failed timeout > 5sec ");
			    				break;
			    			}
			    		}
			    		else
			    		{
			    			m_startdecode = true;
			    			Log.d(TAG,"StartPictureKit done :"+ret);
			    		}			    		
			    	} else {
			    		Log.e(TAG,"RTK_PictureKit is null");
			    		break;
			    	}			    	
		    	}
    		}
    		}).start(); 
    }
        
    public void DecodePictureKit(String Url)
    { 	
        m_slideShowHandlerTimer.removeCallbacks(m_slideShowTimerCb);
    	if(m_pPictureKit!=null) {    
    
        	DecoderInfo di = new DecoderInfo();
        	di.decodemode = 7;      
       	
        	if(mBrowserType == 1)
        		di.bUpnpFile = true;
        	else
        		di.bUpnpFile = false;
        	
        	PictureKit.loadPicture(Url, di);
        	Log.d(TAG,"RTK_PictureKit.loadPicture"); 
        	
        	m_decodeImageState  = DecodeImageState.STATE_DECODEING;
        	m_decodeImageResult = DecodeImageState.STATE_DECODE_RESULT_INIT;
    	    	
        	new Thread(new Runnable() {
        		public void run() {   
        		int timeout = 0;	
        		while(m_startdecode == false) 
        		{
        			if(m_pPictureKit!=null) 
        			{
    	    			try {
    							Thread.sleep(100);
    							Log.e(TAG,"startdecode is failed :"+timeout);
    				    } catch (InterruptedException e) {
    				       // TODO Auto-generated catch block
    				       e.printStackTrace();
    				    }
    	    			timeout++;
    	    			if(timeout > 500) {
    	    				Log.e(TAG,"startdecode failed timeout > 5sec ");
    	    				break;
    	    			}  
    	    		} 
        			else 
        			{
    	    			Log.e(TAG,"RTK_PictureKit is null");
    			    	break;
    	    		} 				
        		}     	
        		timeout = 0;		
    			while(m_startdecode) {
    				if(m_pPictureKit!=null) {
    					long ret;
    					try {	
    					        Log.e(TAG,"Picturekit: Get Result......");
    							ret = m_pPictureKit.GetDecodeImageResult();		
    							if(ret==1)  //DEC_IMG_DECODING
    							{
    							    Log.e(TAG,"Picturekit DecodeImage decoding");
    								Thread.sleep(100);
    							}
    							else if(ret == -5||ret == -1)
    							{
    								//if(decodeFirstPicture)
    								if(mDecodeRetryTimes>10)
    								{
    									Log.e(TAG, "DecodeImage failed retry timeout > 10 times" );  
    									Log.e(TAG, "picturekit DecodeImage failed");
    									mDecodeRetryTimes = 0;
    				    				m_decodeImageResult = DecodeImageState.STATE_DECODE_RESULT_FAIL;
    				    				m_decodeImageState  = DecodeImageState.STATE_DECODE_DONE;
    				    				break;
    								}
    								   								
    								Log.e(TAG, "ret is: " + ret + ",DecodeImage failed now retry! ");   
    								mDecodeRetryTimes++;
    								if(m_pPictureKit!=null) {
    						        	m_startdecode = false;
    						        	m_pPictureKit.stopPictureKit();
    						        	m_pPictureKit = null;
    						        }
    					        	StartPictureKit();  
    					        	m_checkFirstPictureHandler.postDelayed(m_checkFirstDecodeCb, 100);
    					        	m_decodeImageState  = DecodeImageState.STATE_DECODE_RETRY;
    								break;
    							}
    							else if(ret < 0 && ret !=-5 && ret != -1) //Decode Fail
    							{   					
    								Thread.sleep(100);	
    								timeout++;
    				    			if(timeout > 20) {
    				    				Log.e(TAG,"DecodeImage failed timeout > 5sec ");
    				    				Log.e(TAG,"Picturekit DecodeImage failed");
    				    				m_decodeImageResult = DecodeImageState.STATE_DECODE_RESULT_FAIL;
    				    				mDecodeRetryTimes = 0;
    				    				m_decodeImageState  = DecodeImageState.STATE_DECODE_DONE;
    				    				break;
    				    			}   				   
    							}
    							else 
    							{	
    							    Log.e(TAG,"Picturekit DecodeImage success");
    								m_decodeImageResult = DecodeImageState.STATE_DECODE_RESULT_SUCCESS;
    								mDecodeRetryTimes = 0;
    								m_decodeImageState  = DecodeImageState.STATE_DECODE_DONE;
    								break;
    							}					 
    					       
    					    } catch (InterruptedException e) {
    					       // TODO Auto-generated catch block
    					       e.printStackTrace();
    					    }
    				}
    				else {
    	    			Log.e(TAG,"RTK_PictureKit is null");
    	    			m_decodeImageResult = DecodeImageState.STATE_DECODE_RESULT_FAIL;
    	    			m_decodeImageState  = DecodeImageState.STATE_DECODE_DONE;
    			    	break;
    	    		}
    			}
        		}
    		}).start();
        	
    	} 
    	else 
    	{
    			Log.e(TAG,"PicDecodeRTK is not ready!!!");
    			m_decodeImageResult = DecodeImageState.STATE_DECODE_RESULT_FAIL;
    	}
  	
    }	
    
    public void StopPictureKit()
    { 	
    	if(m_pPictureKit == null || m_startdecode == false)
    		return;
    	    	
    	m_pPictureKit.stopPictureKit();    	
    }
    private final Runnable m_checkFirstDecodeCb = new Runnable()
    {

		@Override
		public void run() {
			// TODO Auto-generated method stub

            if(mActivityPauseFlag == 1)
            	return;
            if(m_startdecode == false || 
               m_pPictureKit == null    )
            {
            	 Log.e(TAG,"Not Decode!!!");
            	 m_checkFirstPictureHandler.postDelayed(this, 100);
            }
            else //if( m_decodeImageState == DecodeImageState.STATE_DECODEING ) 
            {
            	/*
            	Log.e(TAG,"m_checkFirstDecodeCb: Decoding now ????!");
            	m_checkFirstPictureHandler.postDelayed(this, 100);
            }
            else
            {*/
            	Log.e(TAG,"m_checkFirstDecodeCb:not Decoding");
                m_checkFirstPictureHandler.removeCallbacks(this); 
                //make sure m_decodeImageState is not STATE_DECODEING,then we begin to Decode,
                //or first picture may often fail decoding (mantis 0039273)
                DecodePictureKit(m_filePathStrArray[m_currentPlayIndex]);
            }  		 
		}    
    };
    private final Runnable m_checkResultTimerCb = new Runnable()
    {
        public void run()
        {
            if(m_startdecode == false || 
               m_pPictureKit ==null     ) 
            {
                Log.e(TAG,"Not Decode!!!");
                //m_checkResultHandlerTimer.removeCallbacks(this); //put this in onDestory
                m_checkResultHandlerTimer.postDelayed(this, m_checkResultTime);
            }
            else
            {
                if(m_decodeImageState == DecodeImageState.STATE_DECODEING ) 
                {
                    m_checkResultHandlerTimer.postDelayed(this, m_checkResultTime);
                    Log.e(TAG,"Get Result decodeing!!!");
                } 
                else if(m_decodeImageState == DecodeImageState.STATE_DECODE_DONE ) 
                {
                    Log.e(TAG,"Decode Done!!!");
                    if(m_decodeImageResult ==  DecodeImageState.STATE_DECODE_RESULT_SUCCESS)
                    {
                        Log.e(TAG,"Decode Success!!!");
                        m_checkResultHandlerTimer.removeCallbacks(this);
                        Log.e(TAG,"m_checkResultTimerCb:start m_slideShowTimerCb:!!!");
                        m_slideShowHandlerTimer.postDelayed(m_slideShowTimerCb, m_slideShowTime);
                    }
                    else if(m_decodeImageResult ==  DecodeImageState.STATE_DECODE_RESULT_FAIL)
                    {
                        Log.e(TAG,"Decode Fail!!!");
                        Toast.makeText(WallpaperPreviewActivity.this
	                			   ,m_filePathStrArray[m_currentPlayIndex].substring(m_filePathStrArray[m_currentPlayIndex].lastIndexOf("/")+1)
	                			   +" "+m_ResourceMgr.getString(R.string.decode_fail)
	                			   ,Toast.LENGTH_SHORT).show();
                        m_checkResultHandlerTimer.removeCallbacks(this);
                        Log.e(TAG,"m_checkResultTimerCb:start m_slideShowTimerCb:!!!");
                        m_slideShowHandlerTimer.postDelayed(m_slideShowTimerCb, 0);
                    } 
                    else
                    {
                    	Log.e(TAG,"Decode Success!!!");
                        m_checkResultHandlerTimer.removeCallbacks(this);
                        Log.e(TAG,"m_checkResultTimerCb:start m_slideShowTimerCb:!!!");
                        m_slideShowHandlerTimer.postDelayed(m_slideShowTimerCb, m_slideShowTime);
                    }    
                }
                else if (m_decodeImageState == DecodeImageState.STATE_DECODE_RETRY)
                {
                	m_checkResultHandlerTimer.postDelayed(this, m_checkResultTime);
                    Log.e(TAG,"Get State Retry!!!");
                }
            }            
        }
    };
    
    private final Runnable m_checkResultTimerCb_forOnkeyRight = new Runnable()
    {
        public void run()
        {
        	if(mActivityPauseFlag == 1)
        		return;
            if(m_startdecode == false || 
               m_pPictureKit ==null     ) 
            {
                Log.e(TAG,"OnKey Not Decode!!!");
                m_checkResultHandlerTimer.postDelayed(this, m_checkResultTime);
            }
            else
            {
                if(m_decodeImageState == DecodeImageState.STATE_DECODEING ) 
                {
                    m_checkResultHandlerTimer.postDelayed(this, m_checkResultTime);
                    Log.e(TAG,"OnKey Get Result decodeing!!!");
                } 
                else if(m_decodeImageState == DecodeImageState.STATE_DECODE_RETRY)
                {
                	m_checkResultHandlerTimer.postDelayed(this, m_checkResultTime);
                    Log.e(TAG,"OnKey Get State Retry");
                }
                else if(m_decodeImageState == DecodeImageState.STATE_DECODE_DONE ) 
                {
                    Log.e(TAG,"OnKey Decode Done!!!"+m_decodeImageResult);

	                   if(m_decodeImageResult ==  DecodeImageState.STATE_DECODE_RESULT_FAIL)
	                   {
	                	   Toast.makeText(WallpaperPreviewActivity.this
	                			   ,m_filePathStrArray[m_currentPlayIndex].substring(m_filePathStrArray[m_currentPlayIndex].lastIndexOf("/")+1)
	                			   +" "+m_ResourceMgr.getString(R.string.decode_fail)
	                			   ,Toast.LENGTH_SHORT).show();
	                	   Log.e(TAG,"Decode Failed!!! Go to Next");
	                	   if(mRepeatMode==1) 
							{					        
								if(m_currentPlayIndex+1 >= m_totalCnt)	
								{
									m_currentPlayIndex=0;
									DecodePictureKit(m_filePathStrArray[m_currentPlayIndex]);
								}
								else
								{
									m_currentPlayIndex++;
									DecodePictureKit(m_filePathStrArray[m_currentPlayIndex]);					
								}
								dosetbannerexif(m_currentPlayIndex);		
							}
							else if(mRepeatMode == 0)
							{							
								if(m_currentPlayIndex+1 >= m_totalCnt)	
								{
									Log.d(TAG,"RIGHT  "+m_currentPlayIndex);
									setResult(m_currentPlayIndex);
									finish();
									return ;
								}
								else
								{
									m_currentPlayIndex++;
									DecodePictureKit(m_filePathStrArray[m_currentPlayIndex]);					
								}
								dosetbannerexif(m_currentPlayIndex);							
							}
	                        m_checkResultHandlerTimer.postDelayed(this, m_checkResultTime);

	                    } 
	                    else if(m_decodeImageResult ==  DecodeImageState.STATE_DECODE_RESULT_SUCCESS)
	                    {
	                    	check_slideshow_mode();
	                    	;//thread end
	                    }    
	                    else
	                    {
	                    	m_checkResultHandlerTimer.postDelayed(this, m_checkResultTime);
	                    }

                }
            }            
        }
    };
    private final Runnable m_checkResultTimerCb_forOnkeyLeft = new Runnable()
    {
        public void run()
        {
        	if(mActivityPauseFlag == 1)
        		return;
            if(m_startdecode == false ||
               m_pPictureKit ==null     ) 
            {
                Log.e(TAG,"OnKey Not Decode!!!");
                m_checkResultHandlerTimer.postDelayed(this, m_checkResultTime);
            }
            else
            {
                if(m_decodeImageState == DecodeImageState.STATE_DECODEING ) 
                {
                    m_checkResultHandlerTimer.postDelayed(this, m_checkResultTime);
                    Log.e(TAG,"Get Result decodeing!!!");
                } 
                else if(m_decodeImageState == DecodeImageState.STATE_DECODE_RETRY)
                {
                	m_checkResultHandlerTimer.postDelayed(this, m_checkResultTime);
                    Log.e(TAG,"OnKey Get State Retry");
                }
                else if(m_decodeImageState == DecodeImageState.STATE_DECODE_DONE ) 
                {
                    Log.e(TAG,"Decode Done!!!");
                   
                   if(m_decodeImageResult ==  DecodeImageState.STATE_DECODE_RESULT_FAIL)
                    {
                	   Toast.makeText(WallpaperPreviewActivity.this
                			   ,m_filePathStrArray[m_currentPlayIndex].substring(m_filePathStrArray[m_currentPlayIndex].lastIndexOf("/")+1)
                			   +" "+m_ResourceMgr.getString(R.string.decode_fail)
                			   ,Toast.LENGTH_SHORT).show();
                	   if(mRepeatMode==1)
			        	{	
							if(m_currentPlayIndex-1<0)	
							{
								m_currentPlayIndex=m_totalCnt-1;
								DecodePictureKit(m_filePathStrArray[m_currentPlayIndex]);
							}
							else
							{
								m_currentPlayIndex--;
								DecodePictureKit(m_filePathStrArray[m_currentPlayIndex]);					
							}
							dosetbannerexif(m_currentPlayIndex);
			        	}
			        	else if(mRepeatMode == 0)
			        	{
			        		if(m_currentPlayIndex-1 >= 0)	
							{
								m_currentPlayIndex--;
								DecodePictureKit(m_filePathStrArray[m_currentPlayIndex]);					
							}
			        		else
			        		{
								Log.d(TAG,"LEFT " + m_currentPlayIndex);
								setResult(m_currentPlayIndex);
								finish();
								return;
							}
							dosetbannerexif(m_currentPlayIndex);	
			        	}
                        m_checkResultHandlerTimer.postDelayed(this, m_checkResultTime);
                    } 
                   	else if(m_decodeImageResult == DecodeImageState.STATE_DECODE_RESULT_SUCCESS)
                   	{
                   		check_slideshow_mode();
                   		;//thread end
                   	}    
                   	else//DecodeImageState.STATE_DECODE_RESULT_INIT?
                   	{
                   		m_checkResultHandlerTimer.postDelayed(this, m_checkResultTime);
                   	}  
                   
                }
            }            
        }
    };
    
    private final Runnable m_slideShowTimerCb = new Runnable()
    {
        public void run()
        {
            Log.e(TAG,"m_slideShowTimerCb:!!!");
            
            if(mActivityPauseFlag == 1)
            	return;
            if(m_startdecode == false || 
               m_pPictureKit == null    )
            {
            	 Log.e(TAG,"Not Decode!!!");
            	 m_checkResultHandlerTimer.postDelayed(m_slideShowTimerCb, 100);
            }
            else if( m_decodeImageState == DecodeImageState.STATE_DECODEING ) 
            {
            	Log.e(TAG,"m_slideShowTimerCb: Decoding now !!!");
            	//Log.e(TAG,"m_slideShowTimerCb: Decoding now !!!");
            	m_checkResultHandlerTimer.postDelayed(m_slideShowTimerCb, 100);
                //m_slideShowHandlerTimer.postDelayed(m_slideShowTimerCb, m_slideShowTime);
                // m_checkResultHandlerTimer.removeCallbacks(this); //put this in onDestory
            }
            else
            {
                if(m_decodeImageState != DecodeImageState.STATE_DECODEING ) 
                {
                    Log.e(TAG,"m_slideShowTimerCb:Next Image");
                    m_slideShowHandlerTimer.removeCallbacks(m_slideShowTimerCb); //put this in onDestory
                    NextImage();    
                } 
            }  
        }
    }; 
    
	 private void initLayout() {
		 mIsFullScrean=false;
		 dosetbannerexif(m_currentPlayIndex);
		 check_slideshow_mode();
		 check_repeat_mode();
		 check_zoom_model();
	 
		 RelativeLayout llayout_banner_photo=(RelativeLayout)findViewById(R.id.banner_photo);
		 RelativeLayout.LayoutParams llayoutparams_banner_photo = new RelativeLayout.LayoutParams(
				 LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT
	            );
		 llayoutparams_banner_photo.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		 llayoutparams_banner_photo.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
	     llayoutparams_banner_photo.width=mDisplayMetrics.widthPixels;
	     llayoutparams_banner_photo.height=0;//set the height to 109 after RTKAtvView is finished coding

	     llayout_banner_photo.setLayoutParams(llayoutparams_banner_photo);

	     LinearLayout l_atvview=(LinearLayout)findViewById(R.id.RtkAtvView_LinearLayout);
	     RelativeLayout.LayoutParams llayout_params_atvview = new RelativeLayout.LayoutParams(
	            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	     llayout_params_atvview.width=mDisplayMetrics.widthPixels;
	     llayout_params_atvview.height=mDisplayMetrics.heightPixels;//-llayoutparams_banner_photo.height;//- rlayout_params_title.height; 
	     l_atvview.setLayoutParams(llayout_params_atvview);

	     SurfaceView sv=(SurfaceView)findViewById(R.id.m_photoPlaybackSurfaceView);
	     LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams) sv.getLayoutParams();
	     llp.width	= mDisplayMetrics.widthPixels;
	     llp.height = mDisplayMetrics.heightPixels-llayoutparams_banner_photo.height;//- rlayout_params_title.height;
	     llp.weight = 0;
	     mPhotoPlaybackView.getHolder().setFixedSize(llp.width,llp.height); 
	     mPhotoPlaybackView.setLayoutParams(llp); 
	        
	     banner_slideshow.setFocusable(true);
	     banner_slideshow.setFocusableInTouchMode(true);
		 banner_slideshow.requestFocus();
			
		/*
		 *set banner height to 109 AFTER RTKAtvView height is to screen height
		 *in order to make sure the height of RTKAtvView take effect;
		 *if set banner heitht to 109 BEFORE set RTKAtvView height to screen height
		 *the height of RTKAtvView will become as screen height minus banner height( 720 - 109=611)
		 *why that happens?
		 */
		llayoutparams_banner_photo.height=109;
		llayout_banner_photo.setLayoutParams(llayoutparams_banner_photo);
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
			            			+WallpaperPreviewActivity.this.getResources().getString(R.string.timeUnit));
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
			            			+WallpaperPreviewActivity.this.getResources().getString(R.string.timeUnit));
			            	}
			            	break;
			            }
			            case 3:
			            {
			         //   	holder.menu_option.setText("QUICK MENU");
			            	break;
			            }
			            case 4:
			            {
			         //  	holder.menu_option.setText("QUICK MENU");
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
		 
	public void onSetWallpaperButtonClicked(View view) {
		if(mIsWallpaperSetting) {
			Toast.makeText(this, R.string.click_repeatedly, Toast.LENGTH_SHORT).show();
			return;
		}
		mIsWallpaperSetting = true;
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				String mPhotoFilePath = m_filePathStrArray[m_currentPlayIndex];
				Log.d(TAG, "onSetWallpaperButtonClicked(): Loading bitmap " + mPhotoFilePath + "...");
				mBitmap = loadBitmap(mPhotoFilePath);
				Message msg = new Message();
				msg.what = LOAD_PHOTO_FINISHED;
				m_setWallpaperHandler.sendMessage(msg);
			}
		}).start();
		
		TextView textView = (TextView)findViewById(R.id.textView_setWallpaper);
		textView.setAlpha(1);
		textView.setText(R.string.setting_wallpaper);
		textView.setTextColor(getResources().getColor(android.R.color.background_light));
		textView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
	}
	 
	private Bitmap loadBitmap(String mImagePath) {
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
	    return BitmapFactory.decodeFile(mImagePath, options);
	}
	
	private void setWallpaper() {
		if(mBitmap == null) {
			Log.e(TAG, "setWallpaper(): Loading bitmap is failed:\nThe image data could not be decoded successfully!");
			AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(R.string.error_title)
				.setMessage(R.string.error_message_loading)
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
							//finish();
							//NextImage();
						}
					});
			builder.show();
			
			TextView textView = (TextView)findViewById(R.id.textView_setWallpaper);
			textView.setAlpha(0);
			textView.setText(null);
			
			mIsWallpaperSetting = false;			
			return;
		}
		Log.d(TAG, "setWallpaper(): Loading bitmap success!");
		
		mWallpaperSettingSuccess = true;
		new Thread(new Runnable() {
			@Override
			public void run() {
				Log.d(TAG, "setWallpaper(): Setting wallpaper...");
				try {
					mWallpaperManager.setBitmap(mBitmap);
				} catch(IOException e) {
					Log.e(TAG, "setWallpaper(): Wallpaper setting is failed:\nThis photo cannot be converted to PNG format successfully!", e);
					mWallpaperSettingSuccess = false;
				} finally {
					Message msg = new Message();
					msg.what = SET_WALLPAPER_FINISHED;
					m_setWallpaperHandler.sendMessage(msg);
				}
			}
		}).start();
	}
	
	private void checkWallpaerSettingResult() {
		if(mWallpaperSettingSuccess == false) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(R.string.error_title)
				.setMessage(R.string.error_message_setting)
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
						//finish();
						//NextImage();
					}
				});
			builder.show();
			
			TextView textView = (TextView)findViewById(R.id.textView_setWallpaper);
			textView.setAlpha(0);
			textView.setText(null);
			
			mIsWallpaperSetting = false;			
			return;
		}
		Log.d(TAG, "checkWallpaerSettingResult(): Wallpaper setting: success!");
		
		TextView textView = (TextView)findViewById(R.id.textView_setWallpaper);
		textView.setAlpha(0);
		
		Toast.makeText(this, R.string.success, Toast.LENGTH_SHORT).show();
		mIsWallpaperSetting = false;
		
		setResult(-5);
		finish();
		return;
	}
	
} 