package com.realtek.dmr;
 
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.app.TvManager;
import com.realtek.bitmapfun.util.FileFilterType;
import com.realtek.bitmapfun.util.LoadingControl;
import com.realtek.dmr.util.DLNAFileInfo;
public class DmrPhotoPlayerActivity_M extends Activity
{private int test = 0;
	private MediaApplication mMediaApplicationMap = null;
	private ArrayList<DLNAFileInfo> mPhotoList;
	boolean mIsFromAnywhere=false;
	private BroadcastReceiver mTvawReceiver = null;
	IntentFilter mTvawFilter = null;
	private GestureDetector gestureScanner;
	
	private int m_initPos = 0;
	private int m_totalCnt = 0;
	private int m_currentPlayIndex = 0;
	private String[] m_filePathStrArray = null;
	private PictureSurfaceView sv = null;
	private ProgressBar loading;
	private Handler loadingHandler;
	private int screenW, screenH;
	public TvManager mTVService = null;
	
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
	private int filelen = 0;
	private String serverName = null;
	public ArrayList<String> DMSName = new ArrayList<String>();
	MThread getBitmapThread = null;


	
	DMRPhotoBroadcastReceiver bc_receiver = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mTVService = (TvManager)getSystemService("tv");
		mTVService.setSource(TvManager.SCALER_RATIO_SOURCE);
		setContentView(R.layout.photoplayer_gallary_dmr);	
		Intent intent = getIntent();
		m_initPos = intent.getIntExtra("initPos", 0);
		m_totalCnt = intent.getIntExtra("len", 0);
		serverName = intent.getStringExtra("serverName");
		mIsFromAnywhere = intent.getBooleanExtra("isanywhere", false);
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
					for (int i = 0; i < photoListSize; i++) {
						if (mPhotoList.get(i).getFileType() == FileFilterType.DEVICE_FILE_PHOTO) {
							m_filePathStrArray[tmpj] = mPhotoList.get(i).getFilePath();
							tmpj++;
						}
					}
				}
			}
		}
		else 
		{
			m_initPos = intent.getIntExtra("initPos", 0);	    	
			m_filePathStrArray = intent.getStringArrayExtra("filelist");
			m_totalCnt = m_filePathStrArray.length;
			//registerReceiver(mTvawReceiver, mTvawFilter);
		}
		// mRepeatIndex = intent.getIntExtra("repeatIndex", 0);
		// mRepeatMode = mRepeatIndex;

		if (m_initPos < 0 && m_initPos > m_totalCnt - 1) {
			m_initPos = 0;
		}
		
		m_currentPlayIndex = m_initPos;
		
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenW = dm.widthPixels;
		screenH = (dm.heightPixels) / 2;
		
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
		
	}

	

	@Override
	public void onNewIntent(Intent intent) {
		
		super.onNewIntent(intent);
		m_totalCnt = intent.getIntExtra("len", 0);
		m_currentPlayIndex = intent.getIntExtra("initPos", 0);
		m_filePathStrArray = intent.getStringArrayExtra("filelist");
		mTVService = (TvManager)getSystemService("tv");
		mTVService.setSource(TvManager.SCALER_RATIO_SOURCE);
	}
	
	class MThread extends Thread {
		boolean _stopFlag = false;
		public void stopThread() {
			_stopFlag = true;
		}
		@Override
		public void run() {
			String filepath = null;
			String filename = null;
			String randomname = null;
			// TODO Auto-generated method stub
			Message msg = new Message();
			msg.what = 0;
			loadingHandler.sendMessage(msg);
			URL url = null;
			URLConnection  conn = null;
			InputStream is =null;
			try {
				url = new URL(m_filePathStrArray[m_currentPlayIndex]);
				conn = url.openConnection();
		        conn.connect();
		        is = conn.getInputStream();
		        filelen = getFilelength(m_filePathStrArray[m_currentPlayIndex]);
		        System.out.println("finelen = "+filelen);
		        byte[] buffer = new byte[filelen];
		        int hasRead = 0;
		        int length = 0;
		        filepath = Environment.getExternalStorageDirectory().getPath();
		        int urllen = m_filePathStrArray[m_currentPlayIndex].length();
		        int point = m_filePathStrArray[m_currentPlayIndex].lastIndexOf(".");
		        filename = m_filePathStrArray[m_currentPlayIndex].substring(point, urllen);
		        randomname = getRandomString(10);
		        RandomAccessFile currentPart = new RandomAccessFile(filepath+"/"+randomname+filename, "rw");
				while (!_stopFlag && (length < filelen)
					&& (hasRead = is.read(buffer)) != -1)
				{
					currentPart.write(buffer, 0, hasRead);
					length += hasRead;
				}
				currentPart.close();
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(!_stopFlag){
		        Bitmap b = BitmapFactory.decodeFile(filepath+"/"+randomname+filename);
		        
				//bmp[0]  = BitmapmImageWorker.getImage(m_filePathStrArray[m_currentPlayIndex], null,0,false,1);
				if(sv.isCanvasFilled()==false)
				{
					sv.setHasBitmap(b,0);
				}
				else
				{
					sv.setHasBitmap(b,0);
				}
				Message msg2 = new Message();
				msg2.what = 1;
				loadingHandler.sendMessage(msg2);
			}
			File imagefile = new File(filepath+"/"+randomname+filename);
			imagefile.delete();
	}
  }
	@Override
	public void onResume() {
		super.onResume();
		doRegisterReceiver();
		sv = (PictureSurfaceView)findViewById(R.id.picture_focused);
		
		final Bitmap[] bmp = {null};
		synchronized(this)
   	 	{
			if(getBitmapThread != null) {
				getBitmapThread.stopThread();
			}
			getBitmapThread = new MThread();
			getBitmapThread.start();
   	 	}	
		System.out.println("bmpbmpbmp"+"   "+m_filePathStrArray[m_currentPlayIndex]);
   }
	  
	public static String getRandomString(int length) {   
	    String base = "abcdefghijklmnopqrstuvwxyz0123456789";     
	    Random random = new Random();     
	    StringBuffer sb = new StringBuffer();     
	    for (int i = 0; i < length; i++) {     
	        int number = random.nextInt(base.length());     
	        sb.append(base.charAt(number));     
	    }     
	    return sb.toString();     
	 }     
	public int getFilelength(String path) throws IOException
	{
		int fileSize = 0;
		URL url;
		try {
			url = new URL(path);
			HttpURLConnection conn;
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5 * 1000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty(
					"Accept",
					"image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
			conn.setRequestProperty("Accept-Language", "zh-CN");
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty(
					"User-Agent",
					"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
			conn.setRequestProperty("Connection", "Keep-Alive");

			fileSize = conn.getContentLength();
			conn.disconnect();
			return fileSize;
		} catch (ProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		return fileSize;
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
		
	}
	
	@Override
	public void onDestroy() {
		if(getBitmapThread != null) {
			getBitmapThread.stopThread();
		}
		super.onDestroy();
	}


	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) { 

    		case KeyEvent.KEYCODE_DPAD_UP: 
    			sv.y-=3; 
    			break; 

    		case KeyEvent.KEYCODE_DPAD_DOWN: 
    			sv.y+=3; 
    			break; 
    	} 
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		
		return super.onKeyDown(keyCode, event);
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
				if(getBitmapThread != null) {
					getBitmapThread.stopThread();
				}
				Bundle bundle = intent.getExtras();
				String cmd = bundle.getString("cmd");
				if(cmd.equals("Photo")) {
					//do nothing, just like I'm start
				} else {
					//stop onNewIntent
					DmrPhotoPlayerActivity_M.this.finish();
				}
			}else if(intent.getAction().equals("com.android.DMRService.stop")){
				DmrPhotoPlayerActivity_M.this.finish();
			}
		}
		
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

}
