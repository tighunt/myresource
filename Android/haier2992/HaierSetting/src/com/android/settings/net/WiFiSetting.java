package com.android.settings.net;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.List;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsResult;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.IpAssignment;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiConfiguration.ProxySettings;
import android.preference.PreferenceActivity;
import android.provider.Settings.System;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.android.internal.util.AsyncChannel;
import com.android.settings.R;
import com.android.settings.util.Tools;

//import com.android.settings.wifi.LinkAddress;
//import com.android.settings.wifi.RouteInfo;

import android.net.DhcpInfo; // change by cwj
//[2012-1-19 ]
import android.net.NetworkUtils;
import android.net.LinkProperties;
import android.net.RouteInfo;
import android.net.LinkAddress;
import android.os.Handler;
import android.os.Message;

/**
 * operate wifi
 * 
 * @author ducj(ducj@biaoqi.com.cn)
 * @since 1.0 2011-11-30
 */
public class WiFiSetting extends PreferenceActivity {

	private final static String TAG = "==>WiFiSetting";
	// [2012-1-31 add]
	private static final int SECURITY_NONE = 0;
	private static final int SECURITY_WEP = 1;
	private static final int SECURITY_PSK = 2;
	private static final int SECURITY_EAP = 3;
	
	
	private String[] ips;            //change by cwj
	// 子网掩码
	private String[] netmasks;        //change by cwj
	// 默认网关
	private String[] defaultWays;     //change by cwj
	// 首选DNS
	private String[] firstdnss;      //change by cwj
	// 备用DNS
	private String[] secdnss;         //change by cwj

	// Combo scans can take 5-6s to complete - set to 10s.
	private static final int WIFI_RESCAN_INTERVAL_MS = 10 * 1000;

	private CheckBox mSwitch;
	private NetSettingViewHolder mNetSettingViewHolder;
	private NetSettingActivity mNetSettingActivity;
	// /static List<ScanResult> results;

	private Scanner mScanner;

	private WifiManager mWifiManager;

	WifiReceiver mReceiverWifi;
	private WifiInfo mWifiInfo;

	private String[] mSettingNames = { System.WIFI_STATIC_IP, System.WIFI_STATIC_GATEWAY,
			System.WIFI_STATIC_NETMASK, System.WIFI_STATIC_DNS1, System.WIFI_STATIC_DNS2 };

	private IntentFilter mFilter;
	private LinkProperties mLinkProperties = new LinkProperties(); // [2012-1-31
																	// add]

	// private ScanResult mResult;
	// private ProgressCategory mAccessPoints;

	public WiFiSetting(NetSettingViewHolder viewHolder, NetSettingActivity activity) {
		this.mNetSettingViewHolder = viewHolder;
		this.mNetSettingActivity = activity;

		// /wifi switch
		mSwitch = mNetSettingViewHolder.mWifiSwitchCb;

		mWifiManager = (WifiManager) mNetSettingActivity.getSystemService(Context.WIFI_SERVICE);
		// //[2012-1-31 ](注册回调)这句话非常重要必须加上，否则在配置wifi时出现空指针异常。
		//mWifiManager.asyncConnect(this, new WifiServiceHandler());

		mScanner = new Scanner();

		mReceiverWifi = new WifiReceiver();
		mWifiInfo = mWifiManager.getConnectionInfo();

		mFilter = new IntentFilter();
		mFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
		// The order matters! We really should not depend on this. :(
		// mFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		mFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		//mFilter.addAction(WifiManager.ERROR_ACTION);       // change by cwj
		mNetSettingActivity.registerReceiver(mReceiverWifi, mFilter);
	}

	public WifiManager getWifiManager() {
		return mWifiManager;
	}
	private class Scanner extends Handler {
		private int mRetry = 0;

		void resume() {
			if (!hasMessages(0)) {
				sendEmptyMessage(0);
			}
		}

		void forceScan() {
			removeMessages(0);
			sendEmptyMessage(0);
		}

		void pause() {
			mRetry = 0;
			removeMessages(0);
		}

		@Override
		public void handleMessage(Message message) {

			if (mWifiManager.isWifiEnabled()) {
				Log.d(TAG, "===>startScanActive");
				if (mWifiManager.startScanActive()) {
					mRetry = 0;
				} else if (++mRetry >= 3) {
					mRetry = 0;
					return;
				}
				sendEmptyMessageDelayed(0, WIFI_RESCAN_INTERVAL_MS);
			}

		}
	}

	// [2012-1-31]wifi callback
	private class WifiServiceHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {

			Log.d(TAG, "handleMessage");

			switch (msg.what) {
			case AsyncChannel.CMD_CHANNEL_HALF_CONNECTED:
				if (msg.arg1 == AsyncChannel.STATUS_SUCCESSFUL) {
					Log.d(TAG, "AsyncChannel.STATUS_SUCCESSFUL");
					// setSSIDValue();// [2012-2-1] show SSID;
					// mWiFiSetting.showWiFiSetting(); // [2012-2-1] show
					// ip;
				} else {
					Log.d(TAG, "Failed to establish AsyncChannel connection");
				}
				break;
			/*case WifiManager.CMD_WPS_COMPLETED:
				WpsResult result = (WpsResult) msg.obj;
				if (result == null)
					break;
				switch (result.status) {
				case FAILURE:
					break;                                        change by cwj
				case IN_PROGRESS:
					break;
				default:

					break;
				}
				break;*/
			// TODO: more connectivity feedback
			default:
				// Ignore
				break;
			}
		}
	}

	public void initWifiUI() {
		
		Log.d(TAG, "WiFiSetting.initWifiUI()......");
		
		refreshWifiUI();
	}

	/**
	 * start scan wifi
	 */
	private void scanWifi() {
		openWifi();
		mWifiManager.startScan();
	}

	/**
	 * open wifi if wifi is closed
	 */
	public void openWifi() {
		
		Log.d(TAG, "WiFiSetting.openWifi().mWifiManager.isWifiEnabled():" + mWifiManager.isWifiEnabled());

		
		if (!mWifiManager.isWifiEnabled()) {
			/*
			 * int wifiState = mNetSettingActivity.mWifiManager.getWifiState(); if (((wifiState ==
			 * WifiManager.WIFI_STATE_DISABLING) || (wifiState == WifiManager.WIFI_STATE_DISABLED)))
			 * {
			 * 
			 * }
			 */
			mWifiManager.setWifiEnabled(true);
		}
		Log.d(TAG, "===>getWifiState()=" + mWifiManager.getWifiState());
	}

	/**
	 * close wifi
	 */
	public void closeWifi() {
		Log.d(TAG, "close wifi");
		int wifiState = mWifiManager.getWifiState();
		if ((wifiState == WifiManager.WIFI_STATE_ENABLING)
				|| (wifiState == WifiManager.WIFI_STATE_ENABLED)) {
			mWifiManager.setWifiEnabled(false);
		}
	}

	// /wifi switch
	public void switchWifiConnect(boolean isWifiConnect) {
		
		Log.d(TAG, "WiFiSetting.switchWifiConnect.isWifiConnect:" + isWifiConnect);

		if (isWifiConnect) {
			// close wifi-direct
			if (mNetSettingActivity.mWifiDirect.isWifiDirectEnable()) {
				Log.d(TAG, "WiFiSetting.switchWifiConnect.isWifiDirectEnable() ture ......");
				mNetSettingActivity.mWifiDirect.setDirectEnable(false);
			}
			mNetSettingActivity.mWiFiSetting.openWifi();
		} else {
			mNetSettingActivity.mWiFiSetting.closeWifi();
			mNetSettingActivity.mWireSetting.openWire();
		}
	}

	// [2012-1-31]
	public WifiConfiguration getCurrentWifiCfg() {
		if (mWifiManager.isWifiEnabled()) {
			WifiInfo currWifiInfo = mWifiManager.getConnectionInfo();

			String ssid = currWifiInfo.getSSID(); /// change by cwj
			int id = currWifiInfo.getNetworkId();
			List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();

			for (WifiConfiguration tmp : configs) {
//				Log.d(TAG, "tmp.SSID =" + tmp.SSID);
//				Log.d(TAG, "ssid =" + ssid);
				if (ssid.equals(tmp.SSID) && id == tmp.networkId) {
					return tmp;
				}
			}
		}
		return null;
	}

	/**
	 * show wifi information 显示ip
	 */
	private void showWiFiSetting() {

		boolean flag = false;
		String ip = null;
		// String mask = null;
		String gateway = null;
		String dns1 = null;
		String dns2 = null;

		String[] ips = null;
		String[] gateways = null;

		WifiConfiguration config = getCurrentWifiCfg();

		if (config != null) {
			LinkProperties linkProperties = config.linkProperties;
			Iterator<LinkAddress> iterator = linkProperties.getLinkAddresses().iterator();
			if (iterator.hasNext()) {
				LinkAddress linkAddress = iterator.next();
				ip = linkAddress.getAddress().getHostAddress();
				if (Tools.matchIP(ip)) {
					ips = Tools.resolutionIP(ip);
					mNetSettingViewHolder.ip_one_ev.setText(ips[0]);
					mNetSettingViewHolder.ip_two_ev.setText(ips[1]);
					mNetSettingViewHolder.ip_three_ev.setText(ips[2]);
					mNetSettingViewHolder.ip_four_ev.setText(ips[3]);
				}
			}

			for (RouteInfo route : linkProperties.getRoutes()) {
				if (route.isDefaultRoute()) {
					gateway = route.getGateway().getHostAddress();
					if (Tools.matchIP(gateway)) {
						gateways = Tools.resolutionIP(gateway);
						mNetSettingViewHolder.defult_one_ev.setText(gateways[0]);
						mNetSettingViewHolder.defult_two_ev.setText(gateways[1]);
						mNetSettingViewHolder.defult_three_ev.setText(gateways[2]);
						mNetSettingViewHolder.defult_four_ev.setText(gateways[3]);
					}
					break;
				}
			}

			Iterator<InetAddress> dnsIterator = linkProperties.getDnses().iterator();
			if (dnsIterator.hasNext()) {
				dns1 = dnsIterator.next().getHostAddress();
				if (Tools.matchIP(dns1)) {
					String[] dns1s = Tools.resolutionIP(dns1);
					mNetSettingViewHolder.first_dns_one_ev.setText(dns1s[0]);
					mNetSettingViewHolder.first_dns_two_ev.setText(dns1s[1]);
					mNetSettingViewHolder.first_dns_three_ev.setText(dns1s[2]);
					mNetSettingViewHolder.first_dns_four_ev.setText(dns1s[3]);
				}
			}
			if (dnsIterator.hasNext()) {
				dns2 = dnsIterator.next().getHostAddress();
				if (Tools.matchIP(dns2)) {
					String[] dns2s = Tools.resolutionIP(dns2);
					mNetSettingViewHolder.second_dns_one_ev.setText(dns2s[0]);
					mNetSettingViewHolder.second_dns_two_ev.setText(dns2s[1]);
					mNetSettingViewHolder.secondt_dns_three_ev.setText(dns2s[2]);
					mNetSettingViewHolder.second_dns_four_ev.setText(dns2s[3]);
				}
			}

			if (null != ips && null != gateways) {
				if (ips[0].equals(gateways[0])) {
					mNetSettingViewHolder.subnet_one_ev.setText("255");
				} else {
					mNetSettingViewHolder.subnet_one_ev.setText("0");
				}
				if (ips[1].equals(gateways[1])) {
					mNetSettingViewHolder.subnet_two_ev.setText("255");
				} else {
					mNetSettingViewHolder.subnet_two_ev.setText("0");
				}
				if (ips[2].equals(gateways[2])) {
					mNetSettingViewHolder.subnet_three_ev.setText("255");
				} else {
					mNetSettingViewHolder.subnet_three_ev.setText("0");
				}

				if (ips[3].equals(gateways[3])) {
					mNetSettingViewHolder.subnet_four_ev.setText("255");
				} else {
					mNetSettingViewHolder.subnet_four_ev.setText("0");
				}
			}

			if (IpAssignment.DHCP == config.ipAssignment) {
				flag = true;
			}

		} else {
			mNetSettingViewHolder.resetIPs();
		}

		Log.d(TAG, "flag=" + flag);
		Configuration configuration = mNetSettingActivity.getResources().getConfiguration();
		if (flag) {
			if (configuration.locale.toString().equals("en_US")) {
				mNetSettingViewHolder.mAutoIpAddress.setBackgroundResource(R.drawable.open_en);
			} else {
				mNetSettingViewHolder.mAutoIpAddress.setBackgroundResource(R.drawable.open);
			}
		} else {
			if (configuration.locale.toString().equals("en_US")) {
				mNetSettingViewHolder.mAutoIpAddress.setBackgroundResource(R.drawable.close_en);
			} else {
				mNetSettingViewHolder.mAutoIpAddress.setBackgroundResource(R.drawable.close);
			}
		}

		mNetSettingViewHolder.setIpInputEnable(!flag);

		setSSIDValue();
	}

	/**
	 * handle wifi connect
	 * 
	 * @param autoa
	 */
	public boolean handleWifiConnect(boolean auto) {
		Log.d(TAG, "handleWifiConnect auto=====>" + auto);
		return setCurrentWifiNet(auto);
	}

	// [2012-2-1add]
	private int getSecurityWithCfg(WifiConfiguration config) {
		if (config.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) {
			return SECURITY_PSK;
		}
		if (config.allowedKeyManagement.get(KeyMgmt.WPA_EAP)
				|| config.allowedKeyManagement.get(KeyMgmt.IEEE8021X)) {
			return SECURITY_EAP;
		}
		return (config.wepKeys[0] != null) ? SECURITY_WEP : SECURITY_NONE;
	}

	// [2012-1-31add]
	private String getSecurity(WifiConfiguration config) {
		if (config.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) {
			return "WPA_PSK";
		}
		if (config.allowedKeyManagement.get(KeyMgmt.WPA_EAP)
				|| config.allowedKeyManagement.get(KeyMgmt.IEEE8021X)) {
			return "WPA_EAP";
		}
		return (config.wepKeys[0] != null) ? "SECURITY_WEP" : "SECURITY_NONE";
	}

	// [2012-1-31add]
	private int getSecurity(ScanResult result) {
		if (result.capabilities.contains("WEP")) {
			return SECURITY_WEP;
		} else if (result.capabilities.contains("PSK")) {
			return SECURITY_PSK;
		} else if (result.capabilities.contains("EAP")) {
			return SECURITY_EAP;
		}
		return SECURITY_NONE;
	}

	// /[2012-1-31 add]
	public WifiConfiguration getConfig(WifiInfo currWifiInfo) {

		if (-1 == currWifiInfo.getNetworkId() || null == currWifiInfo) {
			Log.e(TAG, "reconnect the network");
			return null;
		}

		WifiConfiguration currentCfg = null;
		WifiConfiguration config = new WifiConfiguration();

		List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();
		String ssidStr =  currWifiInfo.getSSID();                          // change by cwj

		Log.d(TAG, "===>configs.size() = " + configs.size());
		for (WifiConfiguration cfg : configs) {
		  Log.d(TAG, "ssidStr" +ssidStr );
			Log.d(TAG, "cfg.SSID" +cfg.SSID );
			if (ssidStr.equals(cfg.SSID)) {
				currentCfg = cfg;
				Log.d(TAG, "===>configs.toString() = " + cfg.toString());
				break;
			}
		}

		if (null == currentCfg) {
			Log.e(TAG, "mResult == null");
			return null;
		}

		int security = getSecurityWithCfg(currentCfg);

		switch (security) {
		case SECURITY_NONE:
			config.allowedKeyManagement.set(KeyMgmt.NONE);
			break;

		case SECURITY_WEP:
			config.allowedKeyManagement.set(KeyMgmt.NONE);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
			break;

		case SECURITY_PSK:
			config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
			break;

		case SECURITY_EAP:
			config.allowedKeyManagement.set(KeyMgmt.WPA_EAP);
			config.allowedKeyManagement.set(KeyMgmt.IEEE8021X);
			config.eap.setValue(getSecurity(config));

			config.phase2.setValue("");
			config.ca_cert.setValue("");
			config.client_cert.setValue("");
			//config.private_key.setValue("");       // change by cwj
			config.identity.setValue("");
			config.anonymous_identity.setValue("");
			break;
		default:
			return null;
		}

		config.networkId = currWifiInfo.getNetworkId();
		config.proxySettings = ProxySettings.NONE;
		return config;
	}

	/*
	 * [2012-1-19 add] 设置当前wifi 状态，DHCP 与 STATIC 切换。
	 */
	public boolean setCurrentWifiNet(boolean auto) {

		// /[2012-2-14 pppoe 优先]
		/*if (mNetSettingActivity.mPppoeSetting.isPppoeActive()) {
			Log.e(TAG, "==>pppoe has connected");
			Toast.makeText(mNetSettingActivity,                                                        //change by cwj
					mNetSettingActivity.getString(R.string.please_insert_dongle), Toast.LENGTH_LONG)
					.show();
			return false;
		}*/

		if (mWifiManager.isWifiEnabled()) {

			WifiInfo currWifiInfo = mWifiManager.getConnectionInfo();
			WifiConfiguration config = getConfig(currWifiInfo);
			if (null == config) {
				Log.e(TAG, "##===>config is null");
				return false;
			}

			if (auto) { // /dhcp
				config.ipAssignment = IpAssignment.DHCP;
				Log.d(TAG, "##saveNetwork(config);");
				mWifiManager.save(config,
						new WifiManager.ActionListener() {
							public void onSuccess() {
							}
							public void onFailure(int reason) {     // change by cwj
								//TODO: Add failure UI
							}
					});
				mWifiManager.reconnect();
			} else {// /static ip
				config.ipAssignment = IpAssignment.STATIC;
				if (configurationStaticIP()) {
					config.linkProperties = new LinkProperties(mLinkProperties);
					Log.d(TAG, "saveNetwork(config);");
					mWifiManager.save(config,
						new WifiManager.ActionListener() {
							public void onSuccess() {
							}
							public void onFailure(int reason) {   // change by cwj
								//TODO: Add failure UI
							}
					});
				} else {
					Log.e(TAG, "input ip address is invalid");
					return false;
				}
			}
		}
		return true;
	}

	// [2012-1-31] config ip address
	private boolean configurationStaticIP() {

		Log.d(TAG, "###===>changeConfigurationStaticIP ");
		mLinkProperties.clear();

		String ipAddr = mNetSettingViewHolder.ip();
		// String netMask = mNetSettingViewHolder.netMask();

		String gateway = mNetSettingViewHolder.defautlWay();
		// String gateway = "192.168.16.1";

		String dns = mNetSettingViewHolder.fistDns();
		// String dns = "202.103.24.68";

		String secondDns = mNetSettingViewHolder.secondDns();

		// /[2012-1-31]when the ip is valid and setup；
		// ip
		InetAddress inetAddr = null;
		try {
			inetAddr = NetworkUtils.numericToInetAddress(ipAddr);
		} catch (IllegalArgumentException e) {
			Log.e(TAG, "无效ip");
			return false;
		}

		mLinkProperties.addLinkAddress(new LinkAddress(inetAddr, 24));

		// gateway
		InetAddress gatewayAddr = null;
		try {
			gatewayAddr = NetworkUtils.numericToInetAddress(gateway);
		} catch (IllegalArgumentException e) {
			Log.e(TAG, "无效gatewayAddr");
			return false;
		}

		mLinkProperties.addRoute(new RouteInfo(gatewayAddr));

		// dns
		InetAddress dnsAddr = null;
		try {
			dnsAddr = NetworkUtils.numericToInetAddress(dns);
		} catch (IllegalArgumentException e) {
			Log.e(TAG, "无效dns");
			return false;
		}

		mLinkProperties.addDns(dnsAddr);

		boolean ret = true;
		InetAddress dnsAddr2 = null;
		try {
			dnsAddr2 = NetworkUtils.numericToInetAddress(secondDns);
		} catch (IllegalArgumentException e) {
			Log.e(TAG, "无效dns2");
			ret = false;
		}

		if (ret) {
			mLinkProperties.addDns(dnsAddr2);
		}

		return true;
	}

	/**
	 * whether auto get ip address
	 * 
	 * @return true :static ip false: auto get ip
	 */
	public boolean isAutoIP() {
		WifiConfiguration config = getCurrentWifiCfg();
    // Log.d(TAG, "===> config.toString() =" + config.ipAssignment.DHCP);
		// Log.d(TAG, "IpAssignment.DHCP = " + IpAssignment.DHCP);
		if (null != config && (IpAssignment.DHCP == config.ipAssignment)) {
			Log.d(TAG, "DHCP");
			return true;
		} else {
			Log.d(TAG, "STATIC IP");
			return false;
		}
	}

	private String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + ((i >> 24) & 0xFF);
			}
	private void showCurrentWifiIP1() {

		WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
		
		if("0x" != wifiInfo.getSSID()){
		DhcpInfo mDhcpInfo = mWifiManager.getDhcpInfo();
		
		
		String ip =intToIp(mDhcpInfo.ipAddress);         
	    String netmask =intToIp(mDhcpInfo.netmask);      
	    String defaultWay = intToIp(mDhcpInfo.gateway);  
	    String firstdns =intToIp(mDhcpInfo.dns1); 
	    String secdns =intToIp(mDhcpInfo.dns2);
	    
	                                                                
			ips = Tools.resolutionIP(ip);                                
			mNetSettingViewHolder.ip_one_state.setText(ips[0]);               
			mNetSettingViewHolder.ip_two_state.setText(ips[1]);               
			mNetSettingViewHolder.ip_three_state.setText(ips[2]);             
			mNetSettingViewHolder.ip_four_state.setText(ips[3]);              
                                                                                                                                                                                                      
			netmasks = Tools.resolutionIP( netmask);                      
			mNetSettingViewHolder.subnet_one_state.setText(netmasks[0]);      
			mNetSettingViewHolder.subnet_two_state.setText(netmasks[1]);      
			mNetSettingViewHolder.subnet_three_state.setText(netmasks[2]);    
			mNetSettingViewHolder.subnet_four_state.setText(netmasks[3]);     
                                                                                                                               
			defaultWays = Tools.resolutionIP(defaultWay);
			mNetSettingViewHolder.defult_one_state.setText(defaultWays[0]);
			mNetSettingViewHolder.defult_two_state.setText(defaultWays[1]);
			mNetSettingViewHolder.defult_three_state.setText(defaultWays[2]);    // change by cwj
			mNetSettingViewHolder.defult_four_state.setText(defaultWays[3]);
		
                                                                                                                                                                                                                                                           
			firstdnss = Tools.resolutionIP(firstdns);                    
			mNetSettingViewHolder.first_dns_one_state.setText(firstdnss[0]);  
			mNetSettingViewHolder.first_dns_two_state.setText(firstdnss[1]);  
			mNetSettingViewHolder.first_dns_three_state.setText(firstdnss[2]);
			mNetSettingViewHolder.first_dns_four_state.setText(firstdnss[3]); 
                                                          
	          
			secdnss = Tools.resolutionIP(secdns);
			mNetSettingViewHolder.second_dns_one_state.setText(secdnss[0]);
			mNetSettingViewHolder.second_dns_two_state.setText(secdnss[1]);
			mNetSettingViewHolder.second_dns_three_state.setText(secdnss[2]);
			mNetSettingViewHolder.second_dns_four_state.setText(secdnss[3]);

																																																         		
		}
		else{
            
			mNetSettingViewHolder.ip_one_state.setText("");                   
			mNetSettingViewHolder.ip_two_state.setText("");                   
			mNetSettingViewHolder.ip_three_state.setText("");                 
			mNetSettingViewHolder.ip_four_state.setText(""); 
			
			mNetSettingViewHolder.subnet_one_state.setText("");               
			mNetSettingViewHolder.subnet_two_state.setText("");               
			mNetSettingViewHolder.subnet_three_state.setText("");             
			mNetSettingViewHolder.subnet_four_state.setText("");
			
			mNetSettingViewHolder.defult_one_state.setText("");
			mNetSettingViewHolder.defult_two_state.setText("");
			mNetSettingViewHolder.defult_three_state.setText("");
			mNetSettingViewHolder.defult_four_state.setText("");
			
			mNetSettingViewHolder.first_dns_one_state.setText("");            
			mNetSettingViewHolder.first_dns_two_state.setText("");            
			mNetSettingViewHolder.first_dns_three_state.setText("");          
			mNetSettingViewHolder.first_dns_four_state.setText(""); 
			
			mNetSettingViewHolder.second_dns_one_state.setText("");
			mNetSettingViewHolder.second_dns_two_state.setText("");
			mNetSettingViewHolder.second_dns_three_state.setText("");
			mNetSettingViewHolder.second_dns_four_state.setText("");
			
			}																																	         		

		
//		String ip = null;
//		// String mask = null;
//		String gateway = null;
//		String dns1 = null;
//		String dns2 = null;
//
//		String[] ips = null;
//		String[] gateways = null;
//		DhcpInfo mDhcpInfo = mWifiManager.getDhcpInfo();
//		WifiConfiguration config = getCurrentWifiCfg();
//		Log.d("ducj", "config  :::::::" + config);
//		if (config != null) {
//			LinkProperties linkProperties = config.linkProperties;
//			Iterator<LinkAddress> iterator = linkProperties.getLinkAddresses().iterator();
//
//			Log.d("ducj", "iterator   :::::::" + iterator.toString());
//
//			if (iterator.hasNext()) {
//				LinkAddress linkAddress = iterator.next();
//				ip = linkAddress.getAddress().getHostAddress();
//
//				Log.d("ducj", "showCurrentWifiIP1  ip +++" + ip);
//				if (Tools.matchIP(ip)) {
//					ips = Tools.resolutionIP(ip);
//					mNetSettingViewHolder.ip_one_state.setText(ips[0]);
//					mNetSettingViewHolder.ip_two_state.setText(ips[1]);
//					mNetSettingViewHolder.ip_three_state.setText(ips[2]);
//					mNetSettingViewHolder.ip_four_state.setText(ips[3]);
//				}
//			}
//
//			for (RouteInfo route : linkProperties.getRoutes()) {
//				if (route.isDefaultRoute()) {
//					gateway = route.getGateway().getHostAddress();
//					Log.d(TAG, "showCurrentWifiIP1  gateway +++" + gateway);
//					if (Tools.matchIP(gateway)) {
//						gateways = Tools.resolutionIP(gateway);
//						mNetSettingViewHolder.defult_one_state.setText(gateways[0]);
//						mNetSettingViewHolder.defult_two_state.setText(gateways[1]);
//						mNetSettingViewHolder.defult_three_state.setText(gateways[2]);
//						mNetSettingViewHolder.defult_four_state.setText(gateways[3]);
//					}
//					break;
//				}
//			}
//
//			Iterator<InetAddress> dnsIterator = linkProperties.getDnses().iterator();
//			if (dnsIterator.hasNext()) {
//				dns1 = dnsIterator.next().getHostAddress();
//				Log.d(TAG, "showCurrentWifiIP1  dns1 +++" + dns1);
//				if (Tools.matchIP(dns1)) {
//					String[] dns1s = Tools.resolutionIP(dns1);
//					mNetSettingViewHolder.first_dns_one_state.setText(dns1s[0]);
//					mNetSettingViewHolder.first_dns_two_state.setText(dns1s[1]);
//					mNetSettingViewHolder.first_dns_three_state.setText(dns1s[2]);
//					mNetSettingViewHolder.first_dns_four_state.setText(dns1s[3]);
//				}
//			}
//
//			if (dnsIterator.hasNext()) {
//				dns2 = dnsIterator.next().getHostAddress();
//				Log.d(TAG, "showCurrentWifiIP1  dns2 +++" + dns2);
//				if (Tools.matchIP(dns2)) {
//					String[] dns2s = Tools.resolutionIP(dns2);
//					mNetSettingViewHolder.second_dns_one_state.setText(dns2s[0]);
//					mNetSettingViewHolder.second_dns_two_state.setText(dns2s[1]);
//					mNetSettingViewHolder.second_dns_three_state.setText(dns2s[2]);
//					mNetSettingViewHolder.second_dns_four_state.setText(dns2s[3]);
//				}
//			}
//
//			if (null != ips && null != gateways) {
//				if (ips[0].equals(gateways[0])) {
//					mNetSettingViewHolder.subnet_one_state.setText("255");
//				} else {
//					mNetSettingViewHolder.subnet_one_state.setText("0");
//				}
//				if (ips[1].equals(gateways[1])) {
//					mNetSettingViewHolder.subnet_two_state.setText("255");
//				} else {
//					mNetSettingViewHolder.subnet_two_state.setText("0");
//				}
//				if (ips[2].equals(gateways[2])) {
//					mNetSettingViewHolder.subnet_three_state.setText("255");
//				} else {
//					mNetSettingViewHolder.subnet_three_state.setText("0");
//				}
//
//				if (ips[3].equals(gateways[3])) {
//					mNetSettingViewHolder.subnet_four_state.setText("255");
//				} else {
//					mNetSettingViewHolder.subnet_four_state.setText("0");
//				}
//			}
//
//			/*
//			 * if (IpAssignment.DHCP == config.ipAssignment){
//			 * mNetSettingViewHolder.setIpInputEnable(false); }else{
//			 * mNetSettingViewHolder.setIpInputEnable(true); }
//			 */
//
//		} else {
//			// mNetSettingViewHolder.resetIPs();
//			mNetSettingViewHolder.resetIPs1();
//		}

		showWifiMac();
		mNetSettingActivity.mWireSetting.showWireMac(null);
	}

	// [2012-4-23yanhd] show wifi mac addr in net state
	public void showWifiMac() {

		Log.v("zjf", "WiFiSetting.showWifiMac......");

		if (null == mNetSettingActivity.mWifiManager)
			return;

		WifiInfo currWifiInfo = mNetSettingActivity.mWifiManager.getConnectionInfo();
		Log.v("zjf", "WiFiSetting.showWifiMac.currWifiInfo:" + currWifiInfo);
		if (null != currWifiInfo) {
			String macAddr = currWifiInfo.getMacAddress();
			// String bssid = currWifiInfo.getBSSID();
			Log.v("zjf", "WiFiSetting.showWifiMac.macAddr:" + macAddr);
			// Log.v("zjf", "WiFiSetting.showWifiMac.bssid:" + bssid);
			if (null != macAddr && !"00:00:00:00:00:00".equals(macAddr)) {
				Log.v("zjf", "WiFiSetting.showWifiMac.null != macAddr......");
				mNetSettingViewHolder.mac_address_state_wifi.setText(macAddr.toUpperCase());
				return;
			}
		}
		Log.v("zjf", "WiFiSetting.showWifiMac.null == currWifiInfo......");
		mNetSettingViewHolder.mac_address_state_wifi.setText("00:00:00:00:00:00");
	}

	/**
	 * check wifi state
	 * 
	 * @return String
	 */
	public void showWiFiState() {
		// [2012-2-8 update]
		mNetSettingViewHolder.resetIPs1();
		if (mWifiManager.isWifiEnabled()) {
			WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
			
//			Log.d(TAG, "wifiInfo =" + wifiInfo);
//			Log.d(TAG, "wifiInfo.getSSID() = " + wifiInfo.getSSID());
			if ("0x" != wifiInfo.getSSID()) {

				mNetSettingViewHolder.wifi_state.setText(mNetSettingActivity
						.getString(R.string.connect_state) + wifiInfo.getSSID());
				Log.d("ducj", "wifiInfo.getIpAddress()+++" + wifiInfo.getIpAddress());
			} else {
				mNetSettingViewHolder.wifi_state.setText(mNetSettingActivity
						.getString(R.string.is_scanning));
			}
		} else {// /wifi 不可用。
			mNetSettingViewHolder.wifi_state.setText(mNetSettingActivity
					.getString(R.string.wifi_state_enable));
		}

		showCurrentWifiIP1();// [2012-3-17 zhs]
	}

	/**
	 * [20120-2-8 update] set ssid' value
	 */
	public void setSSIDValue() {
		Log.d(TAG, "mWifiManager.isWifiEnabled()" + mWifiManager.isWifiEnabled());
		if (mWifiManager.isWifiEnabled()) {
			WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
			Log.d(TAG, "wifiInfo" + wifiInfo);
			Log.d(TAG, "wifiInfo.getSSID()" + wifiInfo.getSSID());
			String mull = "0x";
			if (mull == wifiInfo.getSSID()) {               // change by cwj
				mNetSettingViewHolder.mSSID.setText(mNetSettingActivity
						.getString(R.string.select_wifi_scan));				
			} else {
				String realSSID;
				int lenthSSID = wifiInfo.getSSID().length();
				realSSID = wifiInfo.getSSID().substring(1,lenthSSID - 1);
				mNetSettingViewHolder.mSSID.setText(realSSID);
			}
		} else {// /wifi 不可用。
			mNetSettingViewHolder.mSSID.setText(mNetSettingActivity
					.getString(R.string.wifi_state_enable));
		}
	}

	/**
	 * get network id
	 */
	public int getNetworkId() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
	}

	private void refreshWifiUI() {

		Log.d(TAG, "WiFiSetting.refreshWifiUI()_mWifiManager.isWifiEnabled():" + mWifiManager.isWifiEnabled());

		if (mWifiManager.isWifiEnabled()) {
			showWifiIpUI(true);
			showWiFiSetting();
		} else {
			showWifiIpUI(false);
		}

		int state = mWifiManager.getWifiState();
		if (WifiManager.WIFI_STATE_ENABLING == state || WifiManager.WIFI_STATE_ENABLED == state) {
			mSwitch.setChecked(true);
		} else {
			mSwitch.setChecked(false);
		}
	}

	/**
	 * 是否显示wifi的ip地址等配置信息
	 * 
	 * @param isEnable
	 */
	private void showWifiIpUI(boolean isEnable) {
		
		Log.d(TAG, "WiFiSetting.showWifiIpUI_isEnable:" + mWifiManager.isWifiEnabled());
		
		if (isEnable) {
			mNetSettingViewHolder.mWifiSwitchLy.setVisibility(View.VISIBLE);
			mNetSettingViewHolder.mSSIDRelativeLayout.setVisibility(View.VISIBLE);
			mNetSettingViewHolder.mGetIpRelativeLayout.setVisibility(View.VISIBLE);
			mNetSettingViewHolder.mIpInputLyout.setVisibility(View.VISIBLE);
			mNetSettingViewHolder.mIpSaveBtn.setVisibility(View.VISIBLE);

		} else {
			mNetSettingViewHolder.mWifiSwitchLy.setVisibility(View.VISIBLE);
			mNetSettingViewHolder.mSSIDRelativeLayout.setVisibility(View.GONE);
			mNetSettingViewHolder.mGetIpRelativeLayout.setVisibility(View.GONE);
			mNetSettingViewHolder.mIpInputLyout.setVisibility(View.GONE);
			mNetSettingViewHolder.mIpSaveBtn.setVisibility(View.GONE);
		}
	}

	private void setSwitchChecked(boolean checked) {
		if (checked != mSwitch.isChecked()) {
			mSwitch.setChecked(checked);
		}
	}

	private void handleWifiStateChanged(int state) {
		switch (state) {
		case WifiManager.WIFI_STATE_ENABLING:
			mSwitch.setEnabled(false);
			break;
		case WifiManager.WIFI_STATE_ENABLED:
			setSwitchChecked(true);
			mScanner.resume();
			mSwitch.setEnabled(true);
			break;
		case WifiManager.WIFI_STATE_DISABLING:
			mSwitch.setEnabled(false);
			break;
		case WifiManager.WIFI_STATE_DISABLED:
			setSwitchChecked(false);
			mSwitch.setEnabled(true);
			break;
		default:
			setSwitchChecked(false);
			mSwitch.setEnabled(true);
			break;
		}
	}

	// [2012-2-2 yand update]
	class WifiReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
				handleWifiStateChanged(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
						WifiManager.WIFI_STATE_UNKNOWN));
				// zhs 2012-03-26
				if (mNetSettingActivity.isSelected == Constants.WIRELESS_SETTING) {
					Log.d(TAG, "WiFiSetting.WifiReceiver_WifiManager.WIFI_STATE_CHANGED_ACTION");
					Log.d(TAG, "WiFiSetting.WifiReceiver_mNetSettingActivity.isSelected:" + mNetSettingActivity.isSelected);
					refreshWifiUI();
				}
			} else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {

				if (Constants.NET_STATE == mNetSettingActivity.isSelected) {
					Log.i(TAG, "net status refresh");
					mNetSettingActivity.initCurrentNetState();
				} else if (Constants.WIRELESS_SETTING == mNetSettingActivity.isSelected) {
					Log.i(TAG, "wifi ip refresh");
					refreshWifiUI();
				} else if (Constants.WIRE_SETTING == mNetSettingActivity.isSelected) {
					mNetSettingActivity.mWireSetting.refreshWireUI();
				}
			} /*else if (WifiManager.ERROR_ACTION.equals(action)) {
				int errorCode = intent.getIntExtra(WifiManager.EXTRA_ERROR_CODE, 0);
				switch (errorCode) {
				case WifiManager.WPS_OVERLAP_ERROR:
					Log.e(TAG, "WPS WifiManager.WPS_OVERLAP_ERROR:");
					Toast.makeText(context, R.string.wifi_wps_overlap_error, Toast.LENGTH_SHORT)          // change by cwj
							.show();
					break;
				}
			}*/
		}
	}

	// /判断当前连接是否是 “ssid”
	public boolean isConnected(String ssid) {

		if (null == ssid)
			return false;
		WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
		if (null == wifiInfo)
			return false;

		String str = "\"" + ssid + "\"";
		String current = wifiInfo.getSSID();
		if (null != current && current.equals(str)) {
			return true;
		}

		return false;
	}

	public void onDeviceRemove() {
		mSwitch.setEnabled(true);
		mSwitch.setChecked(mWifiManager.isWifiEnabled());
		if (mNetSettingActivity.isSelected == Constants.WIRELESS_SETTING) {
			Log.d(TAG, "NetSettingActivity.onDeviceRemove()_mNetSettingActivity.isSelected: "
					+ mNetSettingActivity.isSelected);
			initWifiUI();
		}
	}

	public void onPauseWifi() {
		mScanner.pause();
	}

	public void onResumeWifi() {
		mScanner.resume();
	}

	public void onDestoryWifi() {
		mScanner.pause();
		mNetSettingActivity.unregisterReceiver(mReceiverWifi);
	}

}
