package com.android.server;



import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.IBinder;


public class Service_DMR extends Service {
	public RtkDMRService servicedmr;
    public Context mContext;
    public String DMRname = "海尔智能电视@";
	private String DMRNAME = null;
    public void onCreate() {	
		mContext = this.getApplicationContext();
		super.onCreate();
    }
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		RtkDMRService.DMRname = DMRname;
		servicedmr = new RtkDMRService(mContext);
		String hostip = servicedmr.getLocalIpAddress("eth0");
		if(hostip == null)
				hostip = servicedmr.getLocalIpAddress("wlan0");
		if(hostip != null)
		{
			int Iplen = hostip.length();
			int Iplastpoint = hostip.lastIndexOf(".");
			DMRNAME = DMRname+(hostip.substring(Iplastpoint+1, Iplen));
		}else
		{
			DMRNAME = DMRname;
		}
		IntentFilter filter1 = new IntentFilter("com.rtk.dmr.position.broadcast");
 	    registerReceiver(servicedmr.seekreceiver, filter1);
		servicedmr.RTK_DLNA_DMR_init(DMRNAME);
		return startId;
	}
	public void onDestroy()
	{
		if(servicedmr.seekreceiver != null)
			this.unregisterReceiver(servicedmr.seekreceiver);
		servicedmr.RTK_DLNA_DMR_deinit();
		super.onDestroy();
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
