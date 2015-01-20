
package com.rtk.tv.service;

import com.rtk.tv.data.ChannelInfo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

public class TvService extends Service{
	private TvControl tvControl;
	@Override
	public void onCreate() {
		super.onCreate();
		tvControl = new TvControl(this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	private class ServerStub extends ITvService.Stub {

		@Override
		public ChannelInfo[] getChannels() throws RemoteException {
			return tvControl.getChannels();
		}

		@Override
		public void setChannels(ChannelInfo[] channels) throws RemoteException {
			tvControl.setChannels(channels);		
		}

		@Override
		public ChannelInfo getCurrentChannelInfo() throws RemoteException {
			return tvControl.getCurrentChannelInfo();
		}

		@Override
		public void setCurrentChannelInfo(ChannelInfo info)
				throws RemoteException {
			tvControl.setCurrentChannelInfo(info);
		}

		@Override
		public String getCurInputId() throws RemoteException {
			return tvControl.getCurInputId();
		}

		@Override
		public void setCurInputId(String inputId) throws RemoteException {
			tvControl.setCurInputId(inputId);
		}

		@Override
		public void reset() throws RemoteException {
			tvControl.reset();
		}

		
	}
	
	private final IBinder mBinder = new ServerStub();
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
