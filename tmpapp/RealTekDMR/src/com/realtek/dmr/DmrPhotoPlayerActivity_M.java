package com.realtek.dmr;
 
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.android.server.RtkDMRService;
import com.realtek.bitmapfun.util.CommonActivityWithImageWorker;
import com.realtek.bitmapfun.util.DiskLruCache;
import com.realtek.bitmapfun.util.ImageCache;
import com.realtek.bitmapfun.util.ImageFetcher;
import com.realtek.bitmapfun.util.ImageResizer;
import com.realtek.bitmapfun.util.LoadingControl;
import com.realtek.bitmapfun.util.ReturnSizes;
import com.realtek.bitmapfun.util.Utils;
import com.realtek.dmr.util.DLNAFileInfo;
import com.realtek.dmr.util.DensityUtil;
import com.realtek.dmr.util.PopupMessage;

import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.app.TvManager;
import android.app.AlertDialog.Builder;
import android.widget.ProgressBar;
public class DmrPhotoPlayerActivity_M extends Activity
{
	private final static String TAG = "DMRPhotoPlayerActivity_M";
	private int ScreenWidth = 0,ScreenHeight = 0; 
	private Resources m_ResourceMgr = null;
	private TvManager mTv = null;
	private MediaApplication mMediaApplicationMap = null;
	private ArrayList<DLNAFileInfo> mPhotoList;
	boolean mIsFromAnywhere=false;
	private GestureDetector gestureScanner;
	
	private int m_initPos = 0;
	private int m_totalCnt = 0;
	private int m_currentPlayIndex = 0;
	private String[] m_filePathStrArray = null;
	private PictureSurfaceView sv = null;

	private float rate = 1;
	private float oldRate = 1;
	private boolean isFirst = true;
	private boolean canDrag = false;
	private boolean canRecord = true;
	private boolean canSetScaleCenter = true;
	float oldLineDistance = 0f;
	float oldDistanceX = 0f;
	float oldDistanceY = 0f;
	float moveX = 0f,moveY = 0f;
	float startPointX = 0f, startPointY = 0f;
	boolean disableMove = false;
	boolean hasTranslate = false;
	private boolean hasBeenStoped = false;
	Runnable r_showMessageOnComplete = null;
	private final int DELAY_6000 = 6000;
	private String serverName = null;
	public ArrayList<String> DMSName = new ArrayList<String>();
	
	
	ImageCache mCache_small;

	private ImageButton zoom_btn   = null;
	private ImageButton rotate_btn = null;
	private RelativeLayout controlbar_photoplayer = null;
	
	//save the Click order
	private int order = 0;
	
	
	//player zoom mode
	private int mZoomMode = 0;
	private final static int ZOOMMODE_NORMAL = 1;
	private final static int ZOOMMODE_INX2   = 2;
	private final static int ZOOMMODE_INX4   = 4;
	private final static int ZOOMMODE_INX8   = 8;
	private final static int ZOOMMODE_INX16  = 16;
	
	//player rotate mode
	private int mRotateMode = ROTATEMODE_0D;
	private final static int ROTATEMODE_0D = 0;
	private final static int ROTATEMODE_90D = 90;
	private final static int ROTATEMODE_180D = 180;
	private final static int ROTATEMODE_270D = 270;
	
	
	private Handler UIHandler = null;
	private long mBannerShowTime = 6000; 
	private long mLastControlTime = 0l;
	
	//for menu icon(quickmenu)
	
	private QuickMenu mQuickMenu = null;
	private QuickMenuPhotoAdapter mQuickmenuAdapter = null;
	private ImageView btn_menu = null;

	//Activity flag
	private int mActivityPauseFlag = 0;
	private int mActivityDestroyFlag = 0;
	private boolean  mIsFullScrean = false; 
	
	private String[] mIntervalTimeStatus = new String[3];
	private int mIntervalIndex = 1;
	private SharedPreferences mPerferences = null;
	
	DMRPhotoBroadcastReceiver bc_receiver = null;
	boolean isStop = false;
	private ProgressBar loading;
	private Handler loadingHandler;
	
	private boolean isActivityReady = false;
	private boolean receiveInvalidProtol = true;
	private PopupMessage msg_hint = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photoplayer_gallary_dmr);	
		msg_hint = new PopupMessage(this);
		m_ResourceMgr = this.getResources();
		mTv = (TvManager)this.getSystemService("tv");
		zoom_btn   = (ImageButton)findViewById(R.id.info_zoom);
		rotate_btn = (ImageButton)findViewById(R.id.rotate_btn);
		mPerferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		mCache_small = ImageCache.createCache(this,"images");
		Intent intent = getIntent();
		m_initPos = intent.getIntExtra("initPos", 0);
		m_totalCnt = intent.getIntExtra("len", 0);
		serverName = intent.getStringExtra("serverName");
		mIsFromAnywhere = intent.getBooleanExtra("isanywhere", false);
		receiveInvalidProtol = intent.getBooleanExtra("isSupport", true);
		mMediaApplicationMap = (MediaApplication) getApplication();

		if (mIsFromAnywhere == false)
		{
			if (mPhotoList == null) 
			{
				mPhotoList = mMediaApplicationMap.getFileList();
			}
			int photoListSize = mPhotoList.size();
			m_filePathStrArray = new String[photoListSize];
			{
				int tmpj = 0;
				if (m_totalCnt > 0) 
				{
					// get filePathArrayStr
				}
			}
		}
		else 
		{
			if (mPhotoList == null) 
			{
				mPhotoList = mMediaApplicationMap.getFileList();
			}
			m_initPos = intent.getIntExtra("initPos", 0);	    	
			m_filePathStrArray = intent.getStringArrayExtra("filelist");
			m_totalCnt = m_filePathStrArray.length;
		}
		DLNAFileInfo finfo = new DLNAFileInfo(0, null, null,
				16,
				null,
				null,
				0,0,null);
		mPhotoList.add(finfo);

		if (m_initPos < 0 && m_initPos > m_totalCnt - 1) {
			m_initPos = 0;
		}
		
		m_currentPlayIndex = m_initPos;

		controlbar_photoplayer = (RelativeLayout)findViewById(R.id.controlbar_photoplayer);
		controlbar_photoplayer.getBackground().setAlpha(50);
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		ScreenWidth = dm.widthPixels;
		ScreenHeight = dm.heightPixels;
		loading = (ProgressBar)findViewById(R.id.loading);
		loading.setVisibility(View.INVISIBLE);
		loadingHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					loading.setVisibility(View.VISIBLE);
					break;
				case 1:
					loading.setVisibility(View.INVISIBLE);
					break;
				default:
					break;
				}
				super.handleMessage(msg);
			}
		};
		Init_QuickMenu();
	}

	

	private void Init_QuickMenu() {
		// TODO Auto-generated method stub
		
		
		mIntervalTimeStatus[0] = (String) m_ResourceMgr.getText(R.string.qm_interval_fast);
		mIntervalTimeStatus[1] = (String) m_ResourceMgr.getText(R.string.qm_interval_normal);
		mIntervalTimeStatus[2] = (String) m_ResourceMgr.getText(R.string.qm_interval_slow);
		
		mQuickmenuAdapter = new QuickMenuPhotoAdapter(this);
		mQuickMenu = new QuickMenu(this, mQuickmenuAdapter);
		mQuickMenu.setAnimationStyle(R.style.QuickAnimation);
		
		OnItemClickListener quickmenuItemClickListener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				mQuickMenu.markOperation();
				switch (position) {
				case 0: {
					mIntervalIndex++;
					mIntervalIndex %= 3;

					TextView OptionText = (TextView) arg1
							.findViewById(R.id.menu_option);
					OptionText.setText(mIntervalTimeStatus[mIntervalIndex]);

					new Thread(new Runnable() {
						@Override
						public void run() {
							Editor editor = mPerferences.edit();//
							editor.putInt("intervalIndex_photo", mIntervalIndex);
							editor.commit();
						}
					}).start();

					break;
				}
		
				case 1:
				{
					break;
				}
				case 2:
				{
					ComponentName componetName = new ComponentName("com.android.emanualreader",
							"com.android.emanualreader.MainActivity");
					mQuickMenu.dismiss();					
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
								
								TextView OptionText = (TextView)(quickMenuContent.getChildAt(position).findViewById(R.id.menu_option));
								
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

								TextView OptionText = (TextView)(quickMenuContent.getChildAt(position).findViewById(R.id.menu_option));
								
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
					
					return false;
				}
				return false;
			}
		};
		
		r_showMessageOnComplete = new Runnable() {
			public void run() {
				DmrPhotoPlayerActivity_M.this.finish();
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
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) 
			{
				// TODO Auto-generated method stub
			}
		};
		mQuickMenu.AddOnItemClickListener(quickmenuItemClickListener);
		mQuickMenu.AddOnItemSelectedListener(quickmenuItemSelectedListener);
		mQuickMenu.AddOnKeyClickListener(quickmenuKeyClickListener);
		
	}



	@Override
	public void onNewIntent(Intent intent) {	
		super.onNewIntent(intent);
		hasBeenStoped = false;
		m_totalCnt = intent.getIntExtra("len", 0);
		m_currentPlayIndex = intent.getIntExtra("initPos", 0);
		m_filePathStrArray = intent.getStringArrayExtra("filelist");
		
		if (m_initPos < 0 && m_initPos > m_totalCnt - 1) {
			m_initPos = 0;
		}
		if(msg_hint != null) {
			msg_hint.dismiss();
		}
		receiveInvalidProtol = intent.getBooleanExtra("isSupport", true);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
	
		System.out.println("surfaceView touched!!!!!!!!!!!!");
		
		if(event.getPointerCount() == 1)
        {
			if(!disableMove)
			{
				if(mZoomMode == ZOOMMODE_NORMAL)
				{
				
					if(canRecord)
		        	{
		        		startPointX = rotate_translateX(event.getX(),event.getY());
		        		startPointY = rotate_translateY(event.getX(),event.getY());
		        		canRecord = false;
		        	}
					else
		        	{
		        		float distanceX = rotate_translateX(event.getX(),event.getY()) - startPointX;
		        		float distanceY = rotate_translateY(event.getX(),event.getY()) - startPointY;
		        		
		        		float rotate_tmp = -1;
		        		if(mRotateMode ==ROTATEMODE_0D)
		        		{
		        			rotate_tmp = distanceX;
		        		}
		        		else if(mRotateMode ==ROTATEMODE_90D)
		        		{
		        			rotate_tmp = -distanceY;
		        		}
		        		else if(mRotateMode ==ROTATEMODE_180D)
		        		{
		        			rotate_tmp = -distanceX;
		        		}
		        		else if(mRotateMode ==ROTATEMODE_270D)
		        		{
		        			rotate_tmp = distanceY;
		        		}
		        		
		        		if(rotate_tmp > 100 && hasTranslate == false)
		        		//show left pictures(start from left,end to right)
		        		{
			        		hasTranslate = true;
		        		}
		        		else if(rotate_tmp < -100 && hasTranslate == false)
		        		//show right pictures(start from right,end to left)
		        		{
		        			hasTranslate = true;
		        		}
		        	}
					
				}
				else if(mZoomMode > 0) 
				{
					if(canRecord)
		        	{
		        		startPointX = rotate_translateX(event.getX(),event.getY());
		        		startPointY = rotate_translateY(event.getX(),event.getY());
		        		canRecord = false;
		        	}
		        	else
		        	{
		        		float distanceX = rotate_translateX(event.getX(),event.getY()) - startPointX;
		        		float distanceY = rotate_translateY(event.getX(),event.getY()) - startPointY;
		        		moveX = distanceX+oldDistanceX;
		        		moveY = distanceY+oldDistanceY;

		        		if(moveX > ScreenWidth*(oldRate - 1)/2)
		        			moveX = ScreenWidth*(oldRate - 1)/2;
		        		else if(moveX < -ScreenWidth*(oldRate - 1)/2)
		        			moveX =  -ScreenWidth*(oldRate - 1)/2;

		        		if(moveY > ScreenHeight*(oldRate -1)/2)
		        			moveY = ScreenHeight*(oldRate -1)/2;
		        		else if(moveY < -ScreenHeight*(oldRate -1)/2)
		        			moveY =  -ScreenHeight*(oldRate -1)/2;
		        		
		        		sv.setMove(moveX/rate,moveY/rate);
		        		//when surface is scaled, X and Y is scaled too.
		        		//So before use the values, we need to return it to original condition  
		        	}
				}	
			}
        }
		
		if (event.getPointerCount() > 1) {
			disableMove = true;
			if (event.getPointerCount() == 2) {
				float rotate_translateX0 = rotate_translateX(event.getX(0),event.getY(0));
				float rotate_translateY0 = rotate_translateY(event.getX(0),event.getY(0));
				float rotate_translateX1 = rotate_translateX(event.getX(1),event.getY(1));
				float rotate_translateY1 = rotate_translateY(event.getX(1),event.getY(1));
				
				if (isFirst) {

					oldLineDistance = (float) Math.sqrt(Math.pow(rotate_translateX1 - rotate_translateX0, 2)
							+ Math.pow(rotate_translateY1 - rotate_translateY0, 2));
					isFirst = false;
				} else {

					float newLineDistance = (float) Math.sqrt(Math.pow(rotate_translateX1 - rotate_translateX0, 2)
							+ Math.pow(rotate_translateY1 - rotate_translateY0, 2));

					rate = oldRate * newLineDistance / oldLineDistance;
					if(rate >1)
					{
						mZoomMode = ZOOMMODE_INX2;
						if(rate>ZOOMMODE_INX16)
							rate =ZOOMMODE_INX16;
						checkZoomMode(rate);
						sv.setScale(rate);
					}
					else
					{
						rate = 1;
						mZoomMode = ZOOMMODE_NORMAL;
						resetSurfaceView();
					}
				}
				if(canSetScaleCenter)
				{
					canSetScaleCenter = false;
				}
			}
		}
		
        switch (event.getAction())
        {  
	        case MotionEvent.ACTION_DOWN: 
	        	break;
	        case MotionEvent.ACTION_MOVE:
	            break;  
	        case MotionEvent.ACTION_UP:
	        {
	        	
	        	
	        	isFirst = true;
	        	canRecord = true;
				canDrag = true;
				oldRate = rate;
				canSetScaleCenter =true;
				oldDistanceX = moveX;
				oldDistanceY = moveY;
				if(event.getPointerCount() == 1)
		        {
					if(disableMove == true)
					{
						disableMove = false;
					}
		        }
				hasTranslate = false;
				break;
	        }
        }

        return gestureScanner.onTouchEvent(event); 
	}
	private float rotate_translateX(float x,float y)
	{
		switch(mRotateMode)
		{
			case ROTATEMODE_0D:
			{
				return x;
			}
			case ROTATEMODE_90D:
			{
				return y;
			}
			case ROTATEMODE_180D:
			{
				return -x;
			}
			case ROTATEMODE_270D:
			{
				return -y;
			}
		
		}
		return -1f;//error
	}
	private float rotate_translateY(float x,float y)
	{
		switch(mRotateMode)
		{
			case ROTATEMODE_0D:
			{
				return y;
			}
			case ROTATEMODE_90D:
			{
				return -x;
			}
			case ROTATEMODE_180D:
			{
				return -y;
			}
			case ROTATEMODE_270D:
			{
				return x;
			}
		}
		return -1f;//error
	}
	
	

	@Override
	public void onResume() {
		super.onResume();
		if(!receiveInvalidProtol) {
			sendBroadCastNotifyUp();
			if(isActivityReady) {
				msg_hint.setMessage(getResources().getString(R.string.unsupport_file));
				msg_hint.show();
			}
		}
		doRegisterReceiver();
		sv = (PictureSurfaceView)findViewById(R.id.picture_focused);
		sv.setOnTouchListener(new View.OnTouchListener() {  
  
            @Override  
            public boolean onTouch(View v, MotionEvent event) {  
                // TODO Auto-generated method stub  
                return false; 
            }  
        });
		
		//save the Click order
		if(receiveInvalidProtol) {
			playPicture(0);
		}
		
		
		UIHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					resetSurfaceView();
					break;
				case 1:
					setbanner();
				case 2:
					hidebanner();
					break;
				default:
					break;
				}

				super.handleMessage(msg);
			}
			
		};
		
		zoom_btn.setBackgroundResource(R.drawable.dnla_zoom_1_f);
		zoom_btn.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				// TODO Auto-generated method stub
				mLastControlTime = (new Date(System.currentTimeMillis())).getTime();
				int divide = arg0.getWidth()/5;
				if(event.getX()>0 && event.getX()<divide)
				{
					zoom_btn.setBackgroundResource(R.drawable.dnla_zoom_1_f);
					mZoomMode = ZOOMMODE_NORMAL;
					resetSurfaceView();
				}
				else if(event.getX()>divide && event.getX()<divide*2)
				{
					mZoomMode = ZOOMMODE_INX2;
					checkZoomMode(-1);
				}
				else if(event.getX()>divide*2 && event.getX()<divide*3)
				{
					mZoomMode = ZOOMMODE_INX4;
					checkZoomMode(-1);
				}
				else if(event.getX()>divide*3 && event.getX()<divide*4)
				{
					mZoomMode = ZOOMMODE_INX8;
					checkZoomMode(-1);
				}
				else if(event.getX()>divide*4 && event.getX()<divide*5)
				{
					mZoomMode = ZOOMMODE_INX16;
					checkZoomMode(-1);
				}
					
				return false;
			}
		
		});
		rotate_btn.setBackgroundResource(R.drawable.dnla_rotete_icon_n);
		rotate_btn.setOnTouchListener(new OnTouchListener(){
			
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				// TODO Auto-generated method stub
				mLastControlTime = (new Date(System.currentTimeMillis())).getTime();
				rotate_btn.setBackgroundResource(R.drawable.dnla_rotete_icon_f);
				switch (event.getAction()) {   
	                case MotionEvent.ACTION_UP: 
	                {
	                	mRotateMode = mRotateMode + 90 > ROTATEMODE_270D ? ROTATEMODE_0D : mRotateMode + 90;
	                	rotate_btn.setBackgroundResource(R.drawable.dnla_rotete_icon_n);
	                	sv.setRotate(mRotateMode);
	                }

	                break;
				 }
				 return true;
			}
		});
		
		btn_menu = (ImageView) findViewById(R.id.btn_menu);
		btn_menu.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mQuickMenu.isShowing() == true)
				{
					mQuickMenu.dismiss();
				}
				else
				{
					mQuickMenu.showAtRTop(DensityUtil.dip2px(getApplicationContext(), 41),
							DensityUtil.dip2px(getApplicationContext(), 69),360);
					mQuickMenu.setTimeout();
				}
			}
			
		});
		
		controlbar_photoplayer.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				mLastControlTime = (new Date(System.currentTimeMillis())).getTime();
				return true;
			}
			
		});
		
		gestureScanner = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
			@Override
		    public boolean onDoubleTap(MotionEvent e) {
				System.out.println("onDoubleTap");
		        //TODO
				mZoomMode = mZoomMode * 2 > ZOOMMODE_INX16 ? ZOOMMODE_NORMAL : mZoomMode * 2;
				if(mZoomMode == ZOOMMODE_NORMAL)
					resetSurfaceView();
				else
					checkZoomMode(-1);
		        return false;
		    }
			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {   
				setbanner();
		        return false;   
		      } 
			
		});
		
		mLastControlTime = (new Date(System.currentTimeMillis())).getTime();
		new Thread(new Runnable() {
			public void run() {
				long curtime = 0;
				while (true) {
					if (mActivityDestroyFlag == 1)
						break;
					curtime = (new Date(System.currentTimeMillis())).getTime();
					if (!mIsFullScrean) {
						if (curtime - mLastControlTime > mBannerShowTime) {
							Message msg = new Message();
							msg.what = 1;
							UIHandler.sendMessage(msg);
						}
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		
		checkZoomMode(-1);

	};
	
	public void sendBroadCastNotifyUp() {
    	Intent sendToServer = new Intent("com.rtk.dmr.notifyup.broadcast.photo");
    	sendBroadcast(sendToServer);
    }
	
	private void setbanner()
	{
		if(mIsFullScrean)
		{
			controlbar_photoplayer.setVisibility(View.VISIBLE);
			mLastControlTime = (new Date(System.currentTimeMillis())).getTime();
		}
		else
		{
			controlbar_photoplayer.setVisibility(View.INVISIBLE);
		}
		
		mIsFullScrean = !mIsFullScrean;
	}

	private void checkZoomMode(float fRate)
	{
		if(fRate == -1)
		{
			rate = mZoomMode;
			if(mZoomMode == ZOOMMODE_INX2)
			{
				zoom_btn.setBackgroundResource(R.drawable.dnla_zoom_2_f);
			}
			else if(mZoomMode == ZOOMMODE_INX4)
			{
				zoom_btn.setBackgroundResource(R.drawable.dnla_zoom_4_f);
			}
			else if(mZoomMode == ZOOMMODE_INX8)
			{
				zoom_btn.setBackgroundResource(R.drawable.dnla_zoom_8_f);
			}
			else if(mZoomMode == ZOOMMODE_INX16)
			{
				zoom_btn.setBackgroundResource(R.drawable.dnla_zoom_16_f);
			}
			else if(mZoomMode == ZOOMMODE_NORMAL)
			{
				zoom_btn.setBackgroundResource(R.drawable.dnla_zoom_1_f);
			}
			
		}
		else
		{
			rate = fRate;
		}
		sv.setScale(rate);
		
	}
	private byte[] getBytes(InputStream is,ByteArrayOutputStream baos) {
		
		if(is == null)
			return null;
		byte[] bytes = new byte[1024];
		
		
		int len =0;

		try {
			while ((len = is.read(bytes,0,1024)) !=-1) 
			{
			baos.write(bytes, 0, len);
			baos.flush();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally
		{
			bytes = baos.toByteArray();
			
			try {
				baos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		return bytes;
	}
	protected Bitmap decodeBitmapStreamToByte(byte[] byt) {
		// TODO Auto-generated method stub
		
		BitmapFactory.Options opts=new BitmapFactory.Options();
		opts.inPurgeable = true;
		opts.inInputShareable = true; 
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(byt, 0, byt.length,opts);  
		opts.inSampleSize = calculateInSampleSize(opts,ScreenWidth,ScreenHeight);
		
		opts.inJustDecodeBounds = false;
		
		Bitmap b =BitmapFactory.decodeByteArray(byt, 0, byt.length,opts); 
		
		return b;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.srcHeight;
        final int width = options.srcWidth;
        System.out.println("calculateInSampleSize"+height+" "+width);
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger
            // inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down
            // further.
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

	private void playPicture(final int index)
	{
		order++;
		m_currentPlayIndex = index;
		final int tmporder = order;
		final Message msg = new Message();

		Thread getBitmapThread = new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msgLoadingStart = new Message();
				msgLoadingStart.what = 0;
				loadingHandler.sendMessage(msgLoadingStart);
				
				File file = null;
				Bitmap b = null;
				URL url = null;
				
				URLConnection  conn = null;
				InputStream is =null;
				
				String tailStr = null;
				
				if(m_filePathStrArray[m_currentPlayIndex].contains(" "))
    			{
    				tailStr = m_filePathStrArray[m_currentPlayIndex].substring(
    						m_filePathStrArray[m_currentPlayIndex].indexOf(" "));
    			}
				if(tailStr != null&&tailStr.contains("protocolinfo"))
    			{
    				file = downloadBitmapDTCPIP(m_filePathStrArray[m_currentPlayIndex]);	
    			}
				else
				{
					int index =m_filePathStrArray[0].indexOf(" ");

					if(index>0)
					{
						String realpath = m_filePathStrArray[0].substring(0, index);
						m_filePathStrArray[0] =realpath;
					}
					
					file = downloadBitmap(m_filePathStrArray[m_currentPlayIndex]);
				}
				
				if(file == null)
				{
					Message msgLoadingEnd = new Message();
					msgLoadingEnd.what = 1;
					loadingHandler.sendMessage(msgLoadingEnd);
					return;
				}
				
				DLNAFileInfo dif = mPhotoList.get(index);
				
				ExifInterface exif_http = null;
				int ori    = -1;
				int digree = -1; 
				try {
					exif_http = new ExifInterface(file.getPath());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(exif_http == null)
					return;
				
				ori = exif_http.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_UNDEFINED);
				switch (ori) 
				{ 
					case ExifInterface.ORIENTATION_ROTATE_90: 
						digree = 90; 
						break; 
					case ExifInterface.ORIENTATION_ROTATE_180: 
						digree = 180; 
						break; 
					case ExifInterface.ORIENTATION_ROTATE_270: 
						digree = 270; 
						break; 
					default: 
						digree = 0; 
						break; 
				} 
				dif.setOrientionExif(digree);
				
				String date = exif_http.getAttribute(ExifInterface.TAG_DATETIME);
				String height = exif_http.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
				String width = exif_http.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
		        if(date == null)
		        {
		        	String 	language= getApplicationContext()
		        			.getResources().getConfiguration().locale.getLanguage();
		        	if(language.equals("ja"))
		        	{
		        		date = getFileCreateDate(file,
		        				"yyyy"+(String) m_ResourceMgr.getText(R.string.year_menuicon)
		        			   +" MMM dd"+(String) m_ResourceMgr.getText(R.string.day_menuicon)
		        			   +" EEEEEE HH:mm");
		        	}
		        	else
		        		date = getFileCreateDate(file, "HH:mm EEE,dd MMM yyyy");
		        }
		        else
		        {
		        	String 	language= getApplicationContext()
		        			.getResources().getConfiguration().locale.getLanguage();
		        	if(language.equals("ja"))
		        	{
		        		date = dateFormate(date,
		        				"yyyy"+(String) m_ResourceMgr.getText(R.string.year_menuicon)
		        			   +" MMM dd"+(String) m_ResourceMgr.getText(R.string.day_menuicon)
		        			   +" EEEEEE HH:mm");
		        	}
		        	else
		        		date = dateFormate(date,"HH:mm EEE,dd MMM yyyy");
		        }
		        dif.setDateExif(date);
		        
		        
		        Message msg = new Message();
		        msg.what = 2;
			//	UIHandler.sendMessage(msg);
				
				b=ImageResizer.decodeSampledBitmapFromFile(file.toString(), 1080, 1920); 
				if (mPhotoList.get(m_currentPlayIndex).getOrientionExif() > 0) { 
	    			Matrix m = new Matrix(); 
	    			m.postRotate(mPhotoList.get(m_currentPlayIndex).getOrientionExif()); 
	    			b = Bitmap.createBitmap(b, 0, 0, b.getWidth(),b.getHeight(), m, true); 
	    		} 
				
				if(height==null||width==null)
		        {
					height = String.valueOf(ImageResizer.getHeight());
					width = String.valueOf(ImageResizer.getWidth());
		        }
				dif.setHeight(Integer.valueOf(height));
				dif.setWidth(Integer.valueOf(width));
				
				if(sv.isCanvasFilled()==false)
				{
					sv.setHasBitmap(b,0,tmporder);
				}
				else
				{
					sv.setHasBitmap(b,0,tmporder);
				}
				Message msgLoadingEnd = new Message();
				msgLoadingEnd.what = 1;
				loadingHandler.sendMessage(msgLoadingEnd);
				
				msg.what = 0;
				UIHandler.sendMessage(msg);
				sv.setRotate(0);
				mRotateMode = ROTATEMODE_0D;

			}
		});
		getBitmapThread.start();
	}
	public File downloadBitmap(final String urlString)   
    {
    	//Log.d(TAG, "downloadBitmap -n http cache - " + urlString);
		 String path = DiskLruCache.getDiskCacheDir(DmrPhotoPlayerActivity_M.this, "tmp").getAbsolutePath();
        
		File file = new File(path);
        Utils.disableConnectionReuseIfNecessary();
        HttpURLConnection urlConnection = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        boolean sendboardcast = true;
        try {
        	URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(6000);
            urlConnection.setReadTimeout(6000);
            in = new BufferedInputStream(urlConnection.getInputStream(), Utils.IO_BUFFER_SIZE);
            out = new BufferedOutputStream(new FileOutputStream(file),Utils.IO_BUFFER_SIZE);
            
            byte[] buffer = new byte[Utils.IO_BUFFER_SIZE];
            int size = 0;
            while ((size = in.read(buffer)) > 0) 
            {
                out.write(buffer, 0, size);
                if(sendboardcast){
                	sendBroadCastNotifyUp(); 
            	    sendboardcast = false;
            	    Log.d(TAG, "downloadBitmap -> sendBroadCastNotifyUp");
                }
            }
            return file;

        } catch (final IOException e) {
        	if(e.getMessage()!=null){
        		if(e.getMessage().equals("Expected a hex chunk size, but was 200000000")){
		        	try {
						Thread.sleep(3000);
						Log.e(TAG, e.getMessage());
					} catch (InterruptedException io) {
						io.printStackTrace();
					}
        		}
        	}
            Log.e(TAG, "Error in downloadBitmap - " + e);
        } finally {
            if (out != null) {
                try {
                	out.flush();
                    out.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error in downloadBitmap - " + e);
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error in downloadBitmap - " + e);
                }
            } 
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            sendBroadCastNotifyUp();
            Log.d(TAG, "downloadBitmap -> sendBroadCastNotifyUp");
        }
        return null;
    }
	
	
	public String getFileCreateDate(File _file,String formatString) {
        File file = _file;
        Date last_date = new Date(file.lastModified());
        SimpleDateFormat df_des = new SimpleDateFormat(formatString);
        String last_modify_date=df_des.format(last_date);
 		return last_modify_date;
    }
	public String dateFormate(String date,String formatString) {
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
	public File downloadBitmapDTCPIP(String urlString)
    {
    	String path = DiskLruCache.getDiskCacheDir(DmrPhotoPlayerActivity_M.this, "tmp").getAbsolutePath();
        final String tmpUrlString = urlString;
        final String tmpPath = path;
        new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mTv.DownloadImageFile(tmpUrlString,tmpPath);
			}
        	
        }).start();
        
    	File file = new File(path);
    	final Object lock = new Object();
    	Thread checkResultThread = new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				synchronized (lock)
				{
			        
					while(true){
						
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
							break;
						}	
			    		if(mTv.GetDownloadStatus()==1)
			    		{
			    			System.out.println("downloading!!!");
			    			continue;
			    		}
			    		else
			    		{
			    			System.out.println("get upppp!!!!!");
			    			lock.notifyAll();
			    			return;
			    		}
					}
				}
			}
    	});
    	checkResultThread.start();
    	sendBroadCastNotifyUp();
    	Log.d(TAG, "downloadBitmapDTCPIP -> sendBroadCastNotifyUp");
    	synchronized(lock){
            try{
            	lock.wait();
            }catch(InterruptedException e){
               e.printStackTrace();
            }
        }
    	if(mTv.GetDownloadStatus() == 0)
    	{
			return file;
    	}
    	else if(mTv.GetDownloadStatus() == -1)
    	{
    		Log.v(TAG, "DownloadImageFile failed!");
    		return null;
    	}
    	return null;
    }
	public class GridViewLoadingControl implements LoadingControl
    {
    	@Override
    	public void startLoading(int pos) {

    	}
 
    	@Override
    	public void stopLoading(int pos) {

    	}    	
    }
    public  GridViewLoadingControl full_loadingcontrol=new GridViewLoadingControl(); 


	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		doUnRegisterReceiver();
		mActivityPauseFlag = 1;
		if (mQuickMenu.isShowing()) {
			mQuickMenu.setIsActivityPause(mActivityPauseFlag);
		}
       
	}
	
	@Override
	public void onDestroy() {	
		super.onDestroy();
		mActivityDestroyFlag = 1;
		Intent finishtoDMR = new Intent("com.rtk.dmr.finish.broadcast");
		sendBroadcast(finishtoDMR);
	}


	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			AlertDialog.Builder builder = new Builder(DmrPhotoPlayerActivity_M.this);
			builder.setMessage(getResources().getString(R.string.note_exit));
			builder.setTitle(null);
			builder.setPositiveButton(R.string.note_ok, new DialogInterface.OnClickListener() {
				 @Override
				 public void onClick(DialogInterface dialog, int which) {
					loadingHandler.removeCallbacks(r_showMessageOnComplete);
				    dialog.dismiss();
					finish();
				 }
			});
			builder.setNegativeButton(R.string.note_cancel, new DialogInterface.OnClickListener() {
				 @Override
				 public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				 }
			});

			builder.create().show();
		}
		return true;
	}


	private void resetSurfaceView() {
		// TODO Auto-generated method stub
		moveX = 0;
		moveY = 0;
		sv.setMove(moveX, moveY);
		mZoomMode = ZOOMMODE_NORMAL;
		checkZoomMode(-1);
		oldRate = 1;
		oldDistanceX = 0;
		oldDistanceY = 0;
//		disableMove = false;
		
	}
	
	class QuickMenuPhotoAdapter extends BaseAdapter {
		public View LastSelectedItem_View = null;
		int[] menu_name = new int[] { 
				R.string.quick_menu_photo_intervalTime,
				R.string.quick_menu_detail, 
				R.string.quick_menu_help
		};

		private LayoutInflater mInflater;

		class ViewHolder {
			TextView menu_name;
			ImageView left;
			TextView menu_option;
			ImageView right;
		}

		public QuickMenuPhotoAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return menu_name.length;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			
			if(position == 1){
        		DLNAFileInfo info = mPhotoList.get(m_currentPlayIndex);
        		convertView = mInflater.inflate(R.layout.photo_detail_row, null);
        		TextView filename = (TextView)convertView.findViewById(R.id.filename_info);
        		TextView date = (TextView)convertView.findViewById(R.id.date_exif_info);
        		TextView size = (TextView)convertView.findViewById(R.id.size_exif_info);

        		filename.setText(RtkDMRService.getfileTitle());
        		date.setText( mPhotoList.get(m_currentPlayIndex).getDateExif());
        		size.setText(mPhotoList.get(m_currentPlayIndex).getWidth()+" X "+mPhotoList.get(m_currentPlayIndex).getHeight());

        		return convertView;
        	}

			if (convertView == null) {

				convertView = mInflater.inflate(R.layout.quick_list_row,null);

				holder = new ViewHolder();
				holder.menu_name = (TextView) convertView.findViewById(R.id.menu_name);
				holder.menu_option = (TextView) convertView.findViewById(R.id.menu_option);
				holder.left = (ImageView) convertView.findViewById(R.id.left_arrow);
				holder.right = (ImageView) convertView.findViewById(R.id.right_arrow);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.menu_name.setText(menu_name[position]);
			
			switch (position) {
				case 0: {
					holder.menu_option.setText(mIntervalTimeStatus[mIntervalIndex]);
					break;
				}
				case 1: {
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



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
	private class DMRPhotoBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			
			if(intent.getAction().equals("com.android.DMRService.toplay"))
			{
				isStop = false;
				loadingHandler.removeCallbacks(r_showMessageOnComplete);
				Bundle bundle = intent.getExtras();
				String cmd = bundle.getString("cmd");
				if(cmd.equals("Photo")) {
					//do nothing, just like I'm start
				} else {
					//stop onNewIntent
					DmrPhotoPlayerActivity_M.this.finish();
				}
			}
			else if(intent.getAction().equals("com.android.DMRService.stop"))
			{
				if(!hasBeenStoped)
				{
					hasBeenStoped = true;
				order ++;
				isStop = true;
				Message msg = new Message();
				msg.what = 3;
				UIHandler.sendMessage(msg);
				sv.setHasBitmap(null,0,order);
				loadingHandler.postDelayed(r_showMessageOnComplete, DELAY_6000);
			}
		}
		}
		
			}
	
	public void hidebanner(){
		if(mQuickMenu.isShowing()) {
			mQuickMenu.dismiss();
		}
		
		controlbar_photoplayer.setVisibility(View.INVISIBLE);
	} 
	
	public void doRegisterReceiver() {
		bc_receiver = new DMRPhotoBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.android.DMRService.toplay");
		intentFilter.addAction("com.android.DMRService.stop");
		registerReceiver(bc_receiver, intentFilter);
	}
	public void doUnRegisterReceiver(){
		unregisterReceiver(bc_receiver);
	}
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		if(hasFocus) {
			isActivityReady = true;
			if(!receiveInvalidProtol) {
				msg_hint.setMessage(getResources().getString(R.string.unsupport_file));
				msg_hint.show();
			}
		} else {
			isActivityReady = false;
		}
		
	}



	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if(msg_hint != null) {
			msg_hint.dismiss();
		}
	}
	
	

}
