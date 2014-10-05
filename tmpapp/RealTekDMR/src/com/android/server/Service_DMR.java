package com.android.server;




import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.os.IBinder;


public class Service_DMR extends Service {
	public RtkDMRService servicedmr = null;
    public Context mContext;
    public static Context FromSetting; 
    public static SharedPreferences SettingPre;
    public static String DMRname = "haier_dmr";
	private int isOn = 0;
	private boolean DMROpenFlag = false; 
	private DMRSwitchReceiver DmrReceiver = new DMRSwitchReceiver();
    public void onCreate() {	
		mContext =this.getApplicationContext();
		IntentFilter dmrfilter = new IntentFilter();
		dmrfilter.addAction("com.android.settings.dmr.on");
		dmrfilter.addAction("com.android.settings.dmr.off");
		dmrfilter.addAction("com.android.settings.dmr.name");
		dmrfilter.addAction("com.rtk.dmr.Miracast.stopDMR.broadcast");
		dmrfilter.addAction("com.rtk.dmr.Miracast.openDMR.broadcast");
		dmrfilter.addAction("com.rtk.dmr.systemupdate.stopDMR.broadcast");
		dmrfilter.addAction("com.rtk.dmr.systemupdate.openDMR.broadcast");
		mContext.registerReceiver(DmrReceiver, dmrfilter);
		super.onCreate();
    }
	public int onStartCommand(Intent intent, int flags, int startId)
	{
	    Log.e("Service_DMR","Start Service !!!!!");
	    servicedmr = new RtkDMRService(mContext);
	    ReceiverReg();
	    try {
			FromSetting = mContext.createPackageContext("com.android.settings", Context.CONTEXT_IGNORE_SECURITY);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    SettingPre = FromSetting.getSharedPreferences("DMRSetShareData", Context.MODE_WORLD_READABLE|Context.MODE_MULTI_PROCESS);
	    servicedmr.MaxVolume = SettingPre.getInt("DMRMaxVolume", 30);
	    isOn = SettingPre.getInt("DMRSwitch", 0);
	    Log.e("","back for DMR_setting");
	    if(isOn == 1){
	       String DMRNAME = SettingPre.getString("DMRName", null);
	       DMROpenFlag = true;
	       if(DMRNAME != null)
	        {
	        	DMRname = DMRNAME;
	        	RtkDMRService.DMRname = DMRname;
	        }
				servicedmr.RTK_DLNA_DMR_init(DMRname);
	    }
		return startId;
	}
	class DMRSwitchReceiver extends BroadcastReceiver{
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals("com.android.settings.dmr.on")||
					action.equals("com.rtk.dmr.Miracast.openDMR.broadcast")||
					action.equals("com.rtk.dmr.systemupdate.openDMR.broadcast")){
				Log.e("DMRSwitchReceiver","start DMR !!");
				if(DMROpenFlag == false)
				{
					DMROpenFlag = true;
					String DMR_DEVICENAME = intent.getStringExtra("DMRName");
					System.out.println("DMR_DEVICENAME="+DMR_DEVICENAME);
					if(DMR_DEVICENAME != null)
					{
						DMRname = DMR_DEVICENAME;
						RtkDMRService.DMRname = DMRname;
					}
					servicedmr.RTK_DLNA_DMR_init(DMRname);
				}else{
					Log.e("DMRSwitchReceiver","DMR has been opened !!");
				}
		      }
			else if(action.equals("com.android.settings.dmr.off")||
					action.equals("com.rtk.dmr.Miracast.stopDMR.broadcast")||
					action.equals("com.rtk.dmr.systemupdate.stopDMR.broadcast")){
				Log.e("DMRSwitchReceiver","stop DMR !!");
				if(DMROpenFlag == true)
				{
					DMROpenFlag = false;
					servicedmr.RTK_DLNA_DMR_deinit();
				}else{
					Log.e("DMRSwitchReceiver","DMR has been closed !!");
				}
			  }
			else if(action.equals("com.android.settings.dmr.name")){
				Log.e("DMRSwitchReceiver","Change DMR Name!!");
				isOn = intent.getIntExtra("DMRSwitch", 0);
				if(isOn == 1)
				{
				String  strDevName = intent.getStringExtra("DMRName");
				System.out.println("strDevName="+strDevName);
				if(strDevName != null)
				{
					if(!strDevName.equals(DMRname)){
						if(DMROpenFlag)
							servicedmr.RTK_DLNA_DMR_deinit();
						DMROpenFlag = true;
						DMRname = strDevName;
						RtkDMRService.DMRname = DMRname;
						servicedmr.RTK_DLNA_DMR_init(DMRname);	
					}
				}
			  }
			}
			}
		}
	public void onDestroy()
	{
		if(DMROpenFlag == true)
			servicedmr.RTK_DLNA_DMR_deinit();
		UnReceiverReg();
		if(servicedmr != null)
			servicedmr = null;
		if(DmrReceiver != null)
			unregisterReceiver(DmrReceiver);
		super.onDestroy();
	}
	public void ReceiverReg()
	{
		IntentFilter filter1 = new IntentFilter("com.rtk.dmr.position.broadcast");
        registerReceiver(servicedmr.seekreceiver, filter1);
        IntentFilter filter2 = new IntentFilter("com.rtk.dmr.stop.broadcast");
        filter2.addAction("com.rtk.dmr.finish.broadcast");
        registerReceiver(servicedmr.stopreceiver, filter2);
        IntentFilter filter3 = new IntentFilter("com.rtk.dmr.notifyup.broadcast");
        filter3.addAction("com.rtk.dmr.notifyup.broadcast.photo");
        filter3.addAction("com.rtk.dmr.notifyup.broadcast.music");
        registerReceiver(servicedmr.notifyReceiver, filter3);
        IntentFilter filter4 = new IntentFilter("com.android.settings.dmr.volumeset");
        registerReceiver(servicedmr.setVolReceiver, filter4);
	}
	public void UnReceiverReg()
	{
		if(servicedmr.seekreceiver != null)
			unregisterReceiver(servicedmr.seekreceiver);
		if(servicedmr.stopreceiver != null)
			unregisterReceiver(servicedmr.stopreceiver);
		if(servicedmr.notifyReceiver != null)
			unregisterReceiver(servicedmr.notifyReceiver);
		if(servicedmr.setVolReceiver != null)
			unregisterReceiver(servicedmr.setVolReceiver);
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
}