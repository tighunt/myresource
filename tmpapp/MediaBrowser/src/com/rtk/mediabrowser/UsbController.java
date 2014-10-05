package com.rtk.mediabrowser;

import java.util.Timer;
import java.util.TimerTask;

import com.realtek.DataProvider.AbstractDataProvider;
import com.realtek.DataProvider.DeviceFileDataPrivider;
import com.realtek.DataProvider.FileFilterType;
import com.realtek.Utils.MimeTypes;
import com.realtek.bitmapfun.util.LoadingControl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.XmlResourceParser;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class UsbController implements OnUsbCheckListener,OnUsbLoadingListener
{
	private OnUsbCheckListener mUsbCheckLisnter;
	private OnUsbLoadingListener mUsbLoadingListener;
	
	private static final String TAG= "UsbController";
	public final static int PLUG_OUT = 0;
	public final static int PLUG_IN = 1;
	private int mDeviceNum;
	private Context mContext;
	private BroadcastReceiver mReceiver = null;
	private Object usbLock = new Object();
	private Path_Info mPathInfo = null;
	private AbstractDataProvider mDataProvider = null;
	private int mBrowserType = 0;
	private XmlResourceParser mMimeTypeXml = null;
	private MimeTypes mMimeTypes=null;
	private IntentFilter mIntentFilter = null;
	private int m_iPlugDirection = 0;
	
	private UsbLoadingControl m_loadingcontrol=new UsbLoadingControl(); 
	
	public UsbController(Context context)
	{
		mContext = context;
		
		mReceiver = new BroadcastReceiver()
	    {
	        public void onReceive(Context context, Intent intent)
	        {
	        	Uri uri = intent.getData();
	        	String path = uri.getPath();
	        	Log.v(TAG,"USBControl"
	        			+"<BroadcastReceiver> intent.getAction()="+intent.getAction()
	        			+"\ndata: "+intent.getData());
	            if (intent.getAction().equals("android.intent.action.MEDIA_CHECKING"))
	            {
	            	m_loadingcontrol.startLoading();
	            	handleUsbPlugIn();
	            } 
	            else if (intent.getAction().equals("android.intent.action.MEDIA_REMOVED")) //add by starfu 2012/07/23 for bad removed
	            {
	            	m_loadingcontrol.startLoading();
	            	handleUsbPlugOut(path,PLUG_OUT);
	            	handleUsbPlugOut();
	            	Log.i(TAG, intent.getAction());
	            }
	            else if (intent.getAction().equals("android.intent.action.MEDIA_EJECT")) //add by charles_liu 2013/10/28 for unmount
	            {
	            	m_loadingcontrol.startLoading();
	            	handleUsbPlugOut(path,PLUG_OUT);
	            	handleUsbPlugOut();
	            	Log.i(TAG, intent.getAction());
	            }
	        }
	    };
	    mIntentFilter = new IntentFilter();	 
		Init();
	}
	public UsbController(Context context,boolean isPhoto)
	{
		mContext = context;
		
		mReceiver = new BroadcastReceiver()
	    {
	        public void onReceive(Context context, Intent intent)
	        {
	        	Uri uri = intent.getData();
	        	String path = uri.getPath();
	        	Log.v(TAG,"USBControl"
	        			+"<BroadcastReceiver> intent.getAction()="+intent.getAction()
	        			+"\ndata: "+intent.getData());
	            if (intent.getAction().equals("android.intent.action.MEDIA_CHECKING"))
	            {
	            	m_loadingcontrol.startLoading();
	            	handleUsbPlugIn();
	            } 
	            else if (intent.getAction().equals("android.intent.action.MEDIA_REMOVED")) //add by starfu 2012/07/23 for bad removed
	            {
	            	m_loadingcontrol.startLoading();
	            	handleUsbPlugOut(path,PLUG_OUT);
	            	handleUsbPlugOut();
	            	Log.i(TAG, intent.getAction());
	            }
	            else if (intent.getAction().equals("android.intent.action.MEDIA_EJECT"))
	            {
	            	m_loadingcontrol.startLoading();
	            	handleUsbPlugOut(path,PLUG_OUT);
	            	handleUsbPlugOut();
	            	Log.i(TAG, intent.getAction());
	            }
	        }
	    };
	    mIntentFilter = new IntentFilter();	 
		Init();
	}
	private class UsbLoadingControl implements LoadingControl
    {
    	@Override
    	public void startLoading() {
    		// TODO Auto-generated method stub
    		OnStartLoading();
    	}

    	@Override
    	public void stopLoading() {
    		// TODO Auto-generated method stub
    		OnStopLoading();
    	}    	
    }
	public void RegesterBroadcastReceiver()
	{
		mIntentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		mIntentFilter.addAction(Intent.ACTION_MEDIA_CHECKING);
		mIntentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        mIntentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        mIntentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        mIntentFilter.addAction(Intent.ACTION_MEDIA_SHARED);
        mIntentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        mIntentFilter.addDataScheme("file");
        mContext.registerReceiver(mReceiver, mIntentFilter);
	}
	public void UnRegesterBroadcastReceiver()
	{
		mContext.unregisterReceiver(mReceiver); 
	}
	
	private void Init() {
		// TODO Auto-generated method stub
		if(mPathInfo !=null) 
			mPathInfo=null;
        mPathInfo = new Path_Info();
        
        if(mBrowserType == 0)
        {	
        	String mRootPath     ="/storage/udisk/";
        	mPathInfo.addLevelInfo(mRootPath);
        }
        mMimeTypeXml = mContext.getResources().getXml(R.xml.mimetypes);
	    mMimeTypes=Util.GetMimeTypes(mMimeTypeXml);     
        getDataProvider(mPathInfo.getLastLevelPath());
		SetUsbNum(mDataProvider.GetSize()); 	 
	}

	public void SetUsbNum(int num)
	{
		synchronized (usbLock)
		{
			mDeviceNum = num;
			Log.v(TAG,"set usb num.Usb Num is "+mDeviceNum);
		}
	}
	public int GetUsbNum()
	{
		synchronized (usbLock)
		{
			return mDeviceNum ;
		}
	}

	private Handler timerTaskHandler = new Handler()
	{
		@Override  
        public void handleMessage(Message msg)  
        {        
			OnUsbCheck();
			super.handleMessage(msg); 
        }
	};
	private class CheckUsbTimerTask extends TimerTask 
	{
		private final int TIMEOUT =10;
		private int m_iCheckTime = 0;	
		private int m_iDirection = 0;
		CheckUsbTimerTask(int direction)
		{
			this.m_iDirection = direction;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true)
			{				
				getDataProvider(mPathInfo.getDeviceLevelPath());
				int iUsbNumAfterPlug = mDataProvider.GetSize();
				if( iUsbNumAfterPlug != GetUsbNum() || m_iCheckTime >= TIMEOUT)
				{
					m_iCheckTime = 0;
					SetUsbNum(iUsbNumAfterPlug);
					m_iPlugDirection = m_iDirection;
					Message msg = new Message();
					msg.what = 0;					
					timerTaskHandler.sendMessage(msg);
					break;
				}
				else
				{
					m_iCheckTime++;
				}
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}			
		}
	}
	
	@Override
	public void OnUsbCheck() {
		// TODO Auto-generated method stub
		if(mUsbCheckLisnter != null){
			mUsbCheckLisnter.OnUsbCheck();
			
			m_loadingcontrol.stopLoading();
		    
		}
		
	}
	@Override
	public void OnUsbCheck(String path,int direction) {
		// TODO Auto-generated method stub
		if(mUsbCheckLisnter != null){
			mUsbCheckLisnter.OnUsbCheck(path, direction);
			m_loadingcontrol.stopLoading();
		    
		}
		
	}
	@Override
	public void OnStartLoading() {
		// TODO Auto-generated method stub
		if(mUsbLoadingListener != null){
			mUsbLoadingListener.OnStartLoading();
		}
	}
	@Override
	public void OnStopLoading() {
		// TODO Auto-generated method stub
		if(mUsbLoadingListener != null){
			mUsbLoadingListener.OnStopLoading();
		}
	}
	public void setOnUsbCheckListener(OnUsbCheckListener checklsn)
	{
		this.mUsbCheckLisnter = checklsn;
	}
	public void setOnUsbLoadingListener(OnUsbLoadingListener loadlsn)
	{
		this.mUsbLoadingListener = loadlsn;
	}

	private void getDataProvider(String path)
    {  	
        if(mPathInfo.getLastLevelPath() != null || mMimeTypeXml!=null ) 
        {   
            mDataProvider = null;
            
            if(mBrowserType == 0)
            {    
            	mDataProvider = new DeviceFileDataPrivider(path, 
            		FileFilterType.DEVICE_FILE_DIR|FileFilterType.DEVICE_FILE_PHOTO|FileFilterType.DEVICE_FILE_DEVICE,
                    -1,0,mMimeTypes);
            }
            mDataProvider.sortListByType();
        }
    }
	public int getDirection()
	{
		return m_iPlugDirection;
	}
	
	protected synchronized void handleUsbPlugIn()
	{
	   	//Thread check_USB_Thread = new CheckDeviceNumThread(1);
	   	//check_USB_Thread.start();
		CheckUsbTimerTask checkTask = new CheckUsbTimerTask(PLUG_IN);
		Timer checkUsbTimer = new Timer();
		checkUsbTimer.schedule(checkTask, 0);	
	}
	protected synchronized void handleUsbPlugOut()
	{
		CheckUsbTimerTask checkTask = new CheckUsbTimerTask(PLUG_OUT);
		Timer checkUsbTimer = new Timer();
		checkUsbTimer.schedule(checkTask, 0);	
	}
	protected  void handleUsbPlugOut(String path,int direction)
	{
		OnUsbCheck(path,direction);
	}
}
interface OnUsbCheckListener
{
    public void OnUsbCheck();
    public void OnUsbCheck(String path,int direction);
}
interface OnUsbLoadingListener
{
	public void OnStartLoading();
    public void OnStopLoading();
}
