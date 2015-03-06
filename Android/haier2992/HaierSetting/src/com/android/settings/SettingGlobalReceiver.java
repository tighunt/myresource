package com.android.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
//import android.net.PPPOE_STA;
import android.net.ethernet.EthernetManager;
import android.net.ethernet.EthernetStateTracker;
//import android.net.pppoe.PppoeManager;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * 开机自启动一个服务，用于判断PPPOE是否自动拨号
 * 
 * @author ducj(ducj@biaoqi.com.cn)
 * @since 1.0 2011-11-25
 * 
 * [2012-3-12 yanhd]update
 */
public class SettingGlobalReceiver extends BroadcastReceiver {
	
	private final String TAG = "SettingGlobalReceiver==>" ; 
	@Override
	public void onReceive(Context context, Intent intent) {

		String action = intent.getAction();
		Log.d(TAG,""+action);
		if (action.equals("android.intent.action.BOOT_COMPLETED")) {
//			PppoeManager pppoe = (PppoeManager) context.getSystemService(Context.PPPOE_SERVICE);
//			pppoeAutoDial(pppoe);
		}else if (intent.getAction().equals(
					EthernetManager.ETHERNET_STATE_CHANGED_ACTION)){
			handleEthStateChanged(intent.getIntExtra(
					EthernetManager.EXTRA_ETHERNET_STATE,
					EthernetManager.ETHERNET_STATE_UNKNOWN),context);
		} 
/*		else if (action.equals((WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)){
			
		}else if(action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)){
			Log.d(TAG,"open eth0");
			EthernetManager mEthernetManager = (EthernetManager) context.getSystemService(Context.ETHERNET_SERVICE);
			mEthernetManager.setEnabled(true);
		}
*/		
	}
	
	//[2012-4-5yanhd]
/*	private void pppoeAutoDial(PppoeManager pppoe){
		String hwname = pppoe.PppoeGetInterface();
		String user = pppoe.PppoeGetUser();
		String pwd = pppoe.PppoeGetPW();
		
		Log.d(TAG,"@@@@@@hwname = "+hwname);
		Log.d(TAG,"@@@@@usr = "+user);
		Log.d(TAG,"@@@@@pwd = "+pwd);
		if (null != hwname && null != user && null != pwd){
			Log.d(TAG,"pppoeAutoDial");
			pppoe.PppoeDialup();
		}
	}*/
	
	///[2012-4-5yanhd]
	private void handleEthStateChanged(int ethState,Context context) {
		
		switch (ethState) {
			case EthernetStateTracker.EVENT_HW_CONNECTED:
				break;
			case EthernetStateTracker.EVENT_INTERFACE_CONFIGURATION_SUCCEEDED:
				//规避问题： 有线route配置成功 ， 冲掉当前pppoe route的问题。
				Log.d(TAG,"wire cofig and repppoe");
				
/*				PppoeManager pppoe = (PppoeManager) context.getSystemService(Context.PPPOE_SERVICE);
				if (PPPOE_STA.CONNECTED == pppoe.PppoeGetStatus()){
					pppoeAutoDial(pppoe);
				}*/
				break;
			case EthernetStateTracker.EVENT_HW_DISCONNECTED:
				break;
			case EthernetStateTracker.EVENT_INTERFACE_CONFIGURATION_FAILED:
				break;
				default:
					break;
	   }
	}
}
