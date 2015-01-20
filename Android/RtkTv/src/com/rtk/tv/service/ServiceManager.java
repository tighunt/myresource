
package com.rtk.tv.service;


import com.rtk.tv.data.ChannelInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;


public class ServiceManager{

	public ITvService mService;
	private Context mContext;
	private ServiceConnection mConn;
	private IOnServiceConnectComplete mIOnServiceConnectComplete;

	public ServiceManager(Context context) {
		this.mContext = context;
		initConn();
	}

	private void initConn() {
		mConn = new ServiceConnection() {

			@Override
			public void onServiceDisconnected(ComponentName name) {
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				Log.e("test", "srevice binded");
				mService = ITvService.Stub.asInterface(service);
				if (mService != null) {
					mIOnServiceConnectComplete.onServiceConnectComplete(mService);
				}
			}
		};
	}

	public void connectService() {
		Log.e("test", "start connect service");
		Intent intent = new Intent("com.rtk.tv.service.TvService")
		.setPackage("com.rtk.tv");
		mContext.bindService(intent, mConn, Context.BIND_AUTO_CREATE);
	}
	
	public void disConnectService() {
		mContext.unbindService(mConn);
		mContext.stopService(new Intent("com.rtk.tv.service.TvService")
		.setPackage("com.rtk.tv"));
	}
	
	public void exit() {
		mContext.unbindService(mConn);
	}
	

	public void setOnServiceConnectComplete(
			IOnServiceConnectComplete IServiceConnect) {
		mIOnServiceConnectComplete = IServiceConnect;
	}

	public ChannelInfo[] getChannels(){
		if(mService != null) {
			try {
				return mService.getChannels();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public void setChannels(ChannelInfo[] channels){
		if(mService != null) {
			try {
				mService.setChannels(channels);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}


	public ChannelInfo getCurrentChannelInfo(){
		if(mService != null) {
			try {
				return mService.getCurrentChannelInfo();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}


	public void setCurrentChannelInfo(ChannelInfo info) {
		if(mService != null) {
			try {
				mService.setCurrentChannelInfo(info);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}


	public String getCurInputId() {
		if(mService != null) {
			try {
				return mService.getCurInputId();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}


	public void setCurInputId(String inputId) {
		if(mService != null) {
			try {
				mService.setCurInputId(inputId);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	public void reset() {
		if(mService != null) {
			try {
				mService.reset();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
}
