package com.android.settings.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.NetworkInfo;
import android.net.DhcpInfo;//       change by cwj
import android.net.ethernet.EthernetDevInfo;
import android.net.ethernet.EthernetManager;
import android.net.ethernet.EthernetStateTracker;
import android.util.Log;
import android.view.View;
import java.io.BufferedReader; // change by cwj
import java.io.FileReader;       // change by cwj
import java.io.IOException;         // change by cwj

import com.android.settings.R;
import com.android.settings.util.Tools;

/**
 * 有线设置
 * 
 * @author 杜聪甲(ducj@biaoqi.com.cn)
 * @since 1.0 2011-11-22
 */
public class WireSetting {

	private final static String TAG = "WireSetting===>";

	private NetSettingViewHolder mNetSettingViewHolder;

	private NetSettingActivity mNetSettingActivity;
	// IP地址
	private String[] ips;
	// 子网掩码
	private String[] netmasks;
	// 默认网关
	private String[] defaultWays;
	// 首选DNS
	private String[] firstdnss;
	// 备用DNS
	private String[] secdnss;
	// 当前有线连接 的状态
	public int state;

	// /is show wire ip
	private boolean mIsWireConfiged = true;;

	private EthernetManager mEthernetManager;
  private DhcpInfo mDhcpInfo;                 
	public WireSetting(NetSettingViewHolder mNetSettingViewHolder,
			NetSettingActivity mNetSettingActivity) {
		super();
		this.mNetSettingViewHolder = mNetSettingViewHolder;
		this.mNetSettingActivity = mNetSettingActivity;

		mEthernetManager = (EthernetManager) mNetSettingActivity
				.getSystemService(Context.ETHERNET_SERVICE);

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(EthernetManager.ETHERNET_STATE_CHANGED_ACTION);
		intentFilter.addAction(EthernetManager.NETWORK_STATE_CHANGED_ACTION);

		mNetSettingActivity.registerReceiver(mEthStateReceiver, intentFilter);
	}

	public EthernetManager getEthManager() {
		return mEthernetManager;
	}

	// [2012-2-1add] 网络状态中的ip显示。change by cwj ,2013.6.28
	public void showWireIP() {
    
		EthernetDevInfo mEthInfo = mEthernetManager.getSavedConfig();
		
		        String mip = mEthInfo.getIpAddress();		
		        String mnetmask = mEthInfo.getNetMask();
		        String mdefaultWay = mEthInfo.getRouteAddr();		       
		        String mfirstdns = mEthInfo.getDnsAddr();	                    	         		           
		    	mNetSettingViewHolder.resetIPs1();
		if (mNetSettingActivity.isNetInterfaceAvailable("eth0")){
			if (mEthInfo.getConnectMode().equals(EthernetDevInfo.ETHERNET_CONN_MODE_DHCP)){
	          mDhcpInfo = mEthernetManager.getDhcpInfo();
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
						mNetSettingViewHolder.defult_three_state.setText(defaultWays[2]); 
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
				    
				    if(null!= mip)				    
				   { 
				    ips = Tools.resolutionIP(mip);                                
						mNetSettingViewHolder.ip_one_state.setText(ips[0]);               
						mNetSettingViewHolder.ip_two_state.setText(ips[1]);               
						mNetSettingViewHolder.ip_three_state.setText(ips[2]);             
						mNetSettingViewHolder.ip_four_state.setText(ips[3]);
					 }
					 else
					 {
					 	mNetSettingViewHolder.ip_one_state.setText("");               
						mNetSettingViewHolder.ip_two_state.setText("");               
						mNetSettingViewHolder.ip_three_state.setText("");             
						mNetSettingViewHolder.ip_four_state.setText("");
					 }
					 if(null != mnetmask)
					 {
				    netmasks = Tools.resolutionIP(mnetmask);                      
						mNetSettingViewHolder.subnet_one_state.setText(netmasks[0]);      
						mNetSettingViewHolder.subnet_two_state.setText(netmasks[1]);      
						mNetSettingViewHolder.subnet_three_state.setText(netmasks[2]);    
						mNetSettingViewHolder.subnet_four_state.setText(netmasks[3]);     
		       }
		       else
		       {
		       	mNetSettingViewHolder.subnet_one_state.setText("");      
						mNetSettingViewHolder.subnet_two_state.setText("");      
						mNetSettingViewHolder.subnet_three_state.setText("");    
						mNetSettingViewHolder.subnet_four_state.setText("");
		       	}                                                                  				                                                            					            
		        
		        if(null != mdefaultWay)
		        {                                                                  				                                                                   		                                                                          				                                             
						defaultWays = Tools.resolutionIP(mdefaultWay);						               
						mNetSettingViewHolder.defult_one_state.setText(defaultWays[0]);   
						mNetSettingViewHolder.defult_two_state.setText(defaultWays[1]);   
						mNetSettingViewHolder.defult_three_state.setText(defaultWays[2]); 
						mNetSettingViewHolder.defult_four_state.setText(defaultWays[3]);  
				    }
				    else
				    {
 				    mNetSettingViewHolder.defult_one_state.setText("");   
						mNetSettingViewHolder.defult_two_state.setText("");   
						mNetSettingViewHolder.defult_three_state.setText(""); 
						mNetSettingViewHolder.defult_four_state.setText("");
				    }
				    if(null != mfirstdns)
				    {                                                      					              			                                                                  		                                                                          					                                             
						firstdnss = Tools.resolutionIP(mfirstdns);                    
						mNetSettingViewHolder.first_dns_one_state.setText(firstdnss[0]);  
						mNetSettingViewHolder.first_dns_two_state.setText(firstdnss[1]);  
						mNetSettingViewHolder.first_dns_three_state.setText(firstdnss[2]); 
						mNetSettingViewHolder.first_dns_four_state.setText(firstdnss[3]);
					  }
					  else
					  {					                    
						 mNetSettingViewHolder.first_dns_one_state.setText("0");  
						 mNetSettingViewHolder.first_dns_two_state.setText("0");  
						 mNetSettingViewHolder.first_dns_three_state.setText("0"); 
						 mNetSettingViewHolder.first_dns_four_state.setText("0");	
					  }
   					mNetSettingViewHolder.second_dns_one_state.setText("0");
				    mNetSettingViewHolder.second_dns_two_state.setText("0");
				    mNetSettingViewHolder.second_dns_three_state.setText("0");
				    mNetSettingViewHolder.second_dns_four_state.setText("0");
				}
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
		 showWireMac(mEthInfo);
	}

private String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + ((i >> 24) & 0xFF);
			}
				
				
	public void showWireIP1() {

		EthernetDevInfo mEthInfo = mEthernetManager.getSavedConfig();
		if (null == mEthInfo)
			return;

		if (mEthernetManager.isConfigured() && mIsWireConfiged) {

			String ip = mEthInfo.getIpAddress();
			String netmask = mEthInfo.getNetMask();
			String defaultWay = mEthInfo.getRouteAddr();
			String firstdns = mEthInfo.getDnsAddr();
			String secdns = mEthInfo.getDnsAddr();

			if (null != ip) {
				ips = Tools.resolutionIP(ip);
				mNetSettingViewHolder.ip_one_state.setText(ips[0]);
				mNetSettingViewHolder.ip_two_state.setText(ips[1]);
				mNetSettingViewHolder.ip_three_state.setText(ips[2]);
				mNetSettingViewHolder.ip_four_state.setText(ips[3]);
			} else {
				mNetSettingViewHolder.ip_one_state.setText("");
				mNetSettingViewHolder.ip_two_state.setText("");
				mNetSettingViewHolder.ip_three_state.setText("");
				mNetSettingViewHolder.ip_four_state.setText("");
			}

			if (null != netmask) {
				netmasks = Tools.resolutionIP(netmask);
				mNetSettingViewHolder.subnet_one_state.setText(netmasks[0]);
				mNetSettingViewHolder.subnet_two_state.setText(netmasks[1]);
				mNetSettingViewHolder.subnet_three_state.setText(netmasks[2]);
				mNetSettingViewHolder.subnet_four_state.setText(netmasks[3]);

			} else {
				mNetSettingViewHolder.subnet_one_state.setText("");
				mNetSettingViewHolder.subnet_two_state.setText("");
				mNetSettingViewHolder.subnet_three_state.setText("");
				mNetSettingViewHolder.subnet_four_state.setText("");

			}

			if (null != defaultWay) {
				defaultWays = Tools.resolutionIP(defaultWay);
				mNetSettingViewHolder.defult_one_state.setText(defaultWays[0]);
				mNetSettingViewHolder.defult_two_state.setText(defaultWays[1]);
				mNetSettingViewHolder.defult_three_state.setText(defaultWays[2]);
				mNetSettingViewHolder.defult_four_state.setText(defaultWays[3]);
			} else {
				mNetSettingViewHolder.defult_one_state.setText("");
				mNetSettingViewHolder.defult_two_state.setText("");
				mNetSettingViewHolder.defult_three_state.setText("");
				mNetSettingViewHolder.defult_four_state.setText("");
			}

			if (null != firstdns) {
				firstdnss = Tools.resolutionIP(firstdns);
				mNetSettingViewHolder.first_dns_one_state.setText(firstdnss[0]);
				mNetSettingViewHolder.first_dns_two_state.setText(firstdnss[1]);
				mNetSettingViewHolder.first_dns_three_state.setText(firstdnss[2]);
				mNetSettingViewHolder.first_dns_four_state.setText(firstdnss[3]);
			} else {
				mNetSettingViewHolder.first_dns_one_state.setText("");
				mNetSettingViewHolder.first_dns_two_state.setText("");
				mNetSettingViewHolder.first_dns_three_state.setText("");
				mNetSettingViewHolder.first_dns_four_state.setText("");
			}

			// [2012-3-29yanhd] second dns
			if (null != secdns) {
				secdnss = Tools.resolutionIP(secdns);
				mNetSettingViewHolder.second_dns_one_state.setText(secdnss[0]);
				mNetSettingViewHolder.second_dns_two_state.setText(secdnss[1]);
				mNetSettingViewHolder.second_dns_three_state.setText(secdnss[2]);
				mNetSettingViewHolder.second_dns_four_state.setText(secdnss[3]);
			} else {
				mNetSettingViewHolder.second_dns_one_state.setText("");
				mNetSettingViewHolder.second_dns_two_state.setText("");
				mNetSettingViewHolder.second_dns_three_state.setText("");
				mNetSettingViewHolder.second_dns_four_state.setText("");
			}
		}

		showWireMac(mEthInfo);
		mNetSettingActivity.mWiFiSetting.showWifiMac();
	}

	// [2012-4-23yanhd ]show wire mac addr in net state
	public void showWireMac(EthernetDevInfo ethInfo) {

		Log.v("zjf", "WireSetting.showWireMac......");

		EthernetDevInfo info = null;

		Log.v("zjf", "WireSetting.showWireMac.mEthernetManager:" + mEthernetManager);
		Log.v("zjf", "WireSetting.showWireMac.ethInfo:" + ethInfo);

		if (mEthernetManager != null && ethInfo == null) {
			info = mEthernetManager.getSavedConfig();
		} else {
			info = ethInfo;
		}

		if (info != null) {
			String macAddr = getWireMacAddress();
			Log.v("zjf", "WireSetting.showWireMac.macAddr:" + macAddr);
			// [2012-3-30 yanhd]
			if (macAddr != null) {
				mNetSettingViewHolder.mac_address_state.setText(macAddr);
				return;
			}
		}
		mNetSettingViewHolder.mac_address_state.setText("");
	}

	/**
	 * 有线设置各个项的显示
	 */
	public void showWireSetting() {
		boolean flag = false;

//		if (mEthernetManager.isConfigured() && mIsWireConfiged) {
			EthernetDevInfo mEthInfo = mEthernetManager.getSavedConfig();
			// int total = mEthernetManager.getTotalInterface();
//			String ifName = mEthInfo.getIfName();
//			Log.d(TAG, "ifName  " + ifName);
            String mip = mEthInfo.getIpAddress();		
		        String mnetmask = mEthInfo.getNetMask();
		        String mdefaultWay = mEthInfo.getRouteAddr();
		        String mfirstdns = mEthInfo.getDnsAddr();
		        Log.d(TAG, "mip********"+ mip);
				      
		    	mNetSettingViewHolder.resetIPs1();
      if (mNetSettingActivity.isNetInterfaceAvailable("eth0")){
      	if (mEthInfo.getConnectMode().equals(EthernetDevInfo.ETHERNET_CONN_MODE_DHCP)){
      	mDhcpInfo = mEthernetManager.getDhcpInfo();		                    	           
		    String ip =intToIp(mDhcpInfo.ipAddress);         	
		    String netmask =intToIp(mDhcpInfo.netmask);      
		    String defaultWay = intToIp(mDhcpInfo.gateway);  
		    String firstdns =intToIp(mDhcpInfo.dns1); 
		    String secDns =intToIp(mDhcpInfo.dns2); 
      	
				ips = Tools.resolutionIP(ip);
				mNetSettingViewHolder.ip_one_ev.setText(ips[0]);
				mNetSettingViewHolder.ip_two_ev.setText(ips[1]);
				mNetSettingViewHolder.ip_three_ev.setText(ips[2]);
				mNetSettingViewHolder.ip_four_ev.setText(ips[3]);
	
				netmasks = Tools.resolutionIP(netmask);
				mNetSettingViewHolder.subnet_one_ev.setText(netmasks[0]);
				mNetSettingViewHolder.subnet_two_ev.setText(netmasks[1]);
				mNetSettingViewHolder.subnet_three_ev.setText(netmasks[2]);
				mNetSettingViewHolder.subnet_four_ev.setText(netmasks[3]);

				defaultWays = Tools.resolutionIP(defaultWay);
				mNetSettingViewHolder.defult_one_ev.setText(defaultWays[0]);
				mNetSettingViewHolder.defult_two_ev.setText(defaultWays[1]);
				mNetSettingViewHolder.defult_three_ev.setText(defaultWays[2]);
				mNetSettingViewHolder.defult_four_ev.setText(defaultWays[3]);



				firstdnss = Tools.resolutionIP(firstdns);
				mNetSettingViewHolder.first_dns_one_ev.setText(firstdnss[0]);
				mNetSettingViewHolder.first_dns_two_ev.setText(firstdnss[1]);
				mNetSettingViewHolder.first_dns_three_ev.setText(firstdnss[2]);
				mNetSettingViewHolder.first_dns_four_ev.setText(firstdnss[3]);


			/* haier */

				secdnss = Tools.resolutionIP(secDns);
				mNetSettingViewHolder.second_dns_one_ev.setText(secdnss[0]);
				mNetSettingViewHolder.second_dns_two_ev.setText(secdnss[1]);
				mNetSettingViewHolder.secondt_dns_three_ev.setText(secdnss[2]);
				mNetSettingViewHolder.second_dns_four_ev.setText(secdnss[3]);

				flag = true;
			}
		  else{
				Log.d(TAG, "mip888888888888"+ mip);
				    
				    
				   if(null!= mip)				    
				   { 
				    ips = Tools.resolutionIP(mip);                                
						mNetSettingViewHolder.ip_one_ev.setText(ips[0]);               
						mNetSettingViewHolder.ip_two_ev.setText(ips[1]);               
						mNetSettingViewHolder.ip_three_ev.setText(ips[2]);             
						mNetSettingViewHolder.ip_four_ev.setText(ips[3]);
					 }
					 else
					 {
					 	mNetSettingViewHolder.ip_one_ev.setText("");               
						mNetSettingViewHolder.ip_two_ev.setText("");               
						mNetSettingViewHolder.ip_three_ev.setText("");             
						mNetSettingViewHolder.ip_four_ev.setText("");
					 }
					 if(null != mnetmask)
					 {
				    netmasks = Tools.resolutionIP(mnetmask);                      
						mNetSettingViewHolder.subnet_one_ev.setText(netmasks[0]);      
						mNetSettingViewHolder.subnet_two_ev.setText(netmasks[1]);      
						mNetSettingViewHolder.subnet_three_ev.setText(netmasks[2]);    
						mNetSettingViewHolder.subnet_four_ev.setText(netmasks[3]);     
		       }
		       else
		       {
		       	mNetSettingViewHolder.subnet_one_ev.setText("");      
						mNetSettingViewHolder.subnet_two_ev.setText("");      
						mNetSettingViewHolder.subnet_three_ev.setText("");    
						mNetSettingViewHolder.subnet_four_ev.setText("");
		       	}                                                                  				                                                            					            
		        
		        if(null != mdefaultWay)
		        {                                                                  				                                                                   		                                                                          				                                             
						defaultWays = Tools.resolutionIP(mdefaultWay);						               
						mNetSettingViewHolder.defult_one_ev.setText(defaultWays[0]);   
						mNetSettingViewHolder.defult_two_ev.setText(defaultWays[1]);   
						mNetSettingViewHolder.defult_three_ev.setText(defaultWays[2]); 
						mNetSettingViewHolder.defult_four_ev.setText(defaultWays[3]);  
				    }
				    else
				    {
 				    mNetSettingViewHolder.defult_one_ev.setText("");   
						mNetSettingViewHolder.defult_two_ev.setText("");   
						mNetSettingViewHolder.defult_three_ev.setText(""); 
						mNetSettingViewHolder.defult_four_ev.setText("");
				    }
				    if(null != mfirstdns)
				    {                                                      					              			                                                                  		                                                                          					                                             
						firstdnss = Tools.resolutionIP(mfirstdns);                    
						mNetSettingViewHolder.first_dns_one_ev.setText(firstdnss[0]);  
						mNetSettingViewHolder.first_dns_two_ev.setText(firstdnss[1]);  
						mNetSettingViewHolder.first_dns_three_ev.setText(firstdnss[2]); 
						mNetSettingViewHolder.first_dns_four_ev.setText(firstdnss[3]);
					  }
					  else
					  {					                    
						 mNetSettingViewHolder.first_dns_one_ev.setText("0");  
						 mNetSettingViewHolder.first_dns_two_ev.setText("0");  
						 mNetSettingViewHolder.first_dns_three_ev.setText("0"); 
						 mNetSettingViewHolder.first_dns_four_ev.setText("0");	
					  }
						
						mNetSettingViewHolder.second_dns_one_ev.setText("0");
				    mNetSettingViewHolder.second_dns_two_ev.setText("0");
				    mNetSettingViewHolder.secondt_dns_three_ev.setText("0");
			    	mNetSettingViewHolder.second_dns_four_ev.setText("0");
				}
			}
		else{
			
			   Log.d(TAG, "55555888888888888"+ mip);
			      mNetSettingViewHolder.ip_one_ev.setText("");                   
						mNetSettingViewHolder.ip_two_ev.setText("");                   
						mNetSettingViewHolder.ip_three_ev.setText("");                 
						mNetSettingViewHolder.ip_four_ev.setText("");
						
						
						mNetSettingViewHolder.subnet_one_ev.setText("");               
						mNetSettingViewHolder.subnet_two_ev.setText("");               
						mNetSettingViewHolder.subnet_three_ev.setText("");             
						mNetSettingViewHolder.subnet_four_ev.setText("");  
						
						mNetSettingViewHolder.defult_one_ev.setText("");               
						mNetSettingViewHolder.defult_two_ev.setText("");               
						mNetSettingViewHolder.defult_three_ev.setText("");             
						mNetSettingViewHolder.defult_four_ev.setText("");
						
						mNetSettingViewHolder.first_dns_one_ev.setText("");            
						mNetSettingViewHolder.first_dns_two_ev.setText("");            
						mNetSettingViewHolder.first_dns_three_ev.setText("");          
						mNetSettingViewHolder.first_dns_four_ev.setText(""); 
						
					  mNetSettingViewHolder.second_dns_one_ev.setText("");
				    mNetSettingViewHolder.second_dns_two_ev.setText("");
				    mNetSettingViewHolder.secondt_dns_three_ev.setText("");
				    mNetSettingViewHolder.second_dns_four_ev.setText("");
		}
		

		Log.d(TAG, "flag=" + flag);
		Configuration config = mNetSettingActivity.getResources().getConfiguration();
		if (flag) {

			if (config.locale.toString().equals("en_US")) {
				mNetSettingViewHolder.mAutoIpAddress.setBackgroundResource(R.drawable.open_en);
			} else {
				mNetSettingViewHolder.mAutoIpAddress.setBackgroundResource(R.drawable.open);
			}
		} else {
			if (config.locale.toString().equals("en_US")) {
				mNetSettingViewHolder.mAutoIpAddress.setBackgroundResource(R.drawable.close_en);
			} else {
				mNetSettingViewHolder.mAutoIpAddress.setBackgroundResource(R.drawable.close);
			}
		}

		mNetSettingViewHolder.setIpInputEnable(!flag);
	}

	// /is wire dhcp
	public boolean isWireAutoIP() {
		EthernetDevInfo mEthInfo = mEthernetManager.getSavedConfig();
		if (null != mEthInfo
				&& mEthInfo.getConnectMode().equals(EthernetDevInfo.ETHERNET_CONN_MODE_DHCP)) {
			return true;
		} else {
			return false;
		}
	}

	// /open wire
	public void openWire() {
		mEthernetManager.setEnabled(true);
	}

	// /save wire ip dhcp or static
	public boolean handle_saveconf(boolean auto) {
		System.out.println("handle_saveconf auto =" + auto);
		// /[2012-2-14 pppoe 优先]
		if (false) {              // change by cwj
			Log.e(TAG, "==>pppoe has connected");
			return false;
		}

		EthernetDevInfo info = new EthernetDevInfo();
		int total = mEthernetManager.getTotalInterface();
		String ifName = info.getIfName();
		System.out.println("===>ifName=" + ifName);
		System.out.println("total  " + total);
		info.setIfName("eth0");
		System.out.println("auto   " + auto);
		if (auto) {
			info.setConnectMode(EthernetDevInfo.ETHERNET_CONN_MODE_DHCP);
			info.setIpAddress(null);
			info.setNetMask(null);
			info.setRouteAddr(null);
			info.setDnsAddr(null);
//			info.setDnsAddr(null); // haier          change by cwj
			
		} else {
			info.setConnectMode(EthernetDevInfo.ETHERNET_CONN_MODE_MANUAL);

			String ip = mNetSettingViewHolder.ip();
			String netMask = mNetSettingViewHolder.netMask();
			String defaultway = mNetSettingViewHolder.defautlWay();
			String firstDns = mNetSettingViewHolder.fistDns();
//			String secDns = mNetSettingViewHolder.secondDns();

			if (Tools.matchIP(ip)) {
				info.setIpAddress(ip);
			} else
				info.setIpAddress(null);

			if (Tools.matchIP(netMask)) {
				info.setNetMask(netMask);
			} else
				info.setNetMask(null);

			if (Tools.matchIP(defaultway)) {
				info.setRouteAddr(defaultway);
			} else
				info.setRouteAddr(null);

			if (Tools.matchIP(firstDns)) {
				info.setDnsAddr(firstDns);
			} else
				info.setDnsAddr(null);

//			/* haier */
//			if (Tools.matchIP(secDns)) {
//				info.setDnsAddr(secDns);     // change by cwj
//			} else
//				info.setDnsAddr(null);    // change by cwj

		}

		mEthernetManager.updateDevInfo(info);
		mEthernetManager.setEnabled(true);
		return true;
	}

	// /[2012-2-22add]
	public void setEthEnabled(final boolean enable) {

		int state = getEthState();
		System.out.println("Show configuration dialog " + enable);
		if (state != EthernetManager.ETHERNET_STATE_ENABLED && enable) {
			if (mEthernetManager.isConfigured() != true) {
				// Now, kick off the setting dialog to get the configurations
			} else {
				mEthernetManager.setEnabled(enable);
			}
		} else {
			mEthernetManager.setEnabled(enable);
		}
	}

	/**
	 * 获取当前有线网卡的状态
	 */
	public int getEthState() {
		return mEthernetManager.getState();
	}

	/**
	 * [2012-2-9 update] show wire in net stauts ui
	 */
	public void showWireState() {
		System.out.println("===>showWireState");
		mNetSettingViewHolder.resetIPs1();
		if (mNetSettingActivity.isNetInterfaceAvailable("eth0")) {
			mNetSettingViewHolder.wire_net_state.setText(mNetSettingActivity
					.getString(R.string.eth_able));
		} else { // /未接网线
			mNetSettingViewHolder.wire_net_state.setText(mNetSettingActivity
					.getString(R.string.eth_enable));
		}

		showWireIP();// [2012-3-17 modified]
		System.out.println("#########**************");    // change by cwj
	}

	/**
	 * 当第一次进入网络设置不是自动获取IP地址时，如果获取的地址不为空显示出系统的IP地址
	 */
	public void showWireIPs() {
		mNetSettingViewHolder.ip_one_ev.setText(ips[0]);
		mNetSettingViewHolder.ip_two_ev.setText(ips[1]);
		mNetSettingViewHolder.ip_three_ev.setText(ips[2]);
		mNetSettingViewHolder.ip_four_ev.setText(ips[3]);

		mNetSettingViewHolder.defult_one_ev.setText(defaultWays[0]);
		mNetSettingViewHolder.defult_two_ev.setText(defaultWays[1]);
		mNetSettingViewHolder.defult_three_ev.setText(defaultWays[2]);
		mNetSettingViewHolder.defult_four_ev.setText(defaultWays[3]);

		mNetSettingViewHolder.subnet_one_ev.setText(netmasks[0]);
		mNetSettingViewHolder.subnet_two_ev.setText(netmasks[1]);
		mNetSettingViewHolder.subnet_three_ev.setText(netmasks[2]);
		mNetSettingViewHolder.subnet_four_ev.setText(netmasks[3]);

		mNetSettingViewHolder.first_dns_one_ev.setText(firstdnss[0]);
		mNetSettingViewHolder.first_dns_two_ev.setText(firstdnss[1]);
		mNetSettingViewHolder.first_dns_three_ev.setText(firstdnss[2]);
		mNetSettingViewHolder.first_dns_four_ev.setText(firstdnss[3]);
	}

	public BroadcastReceiver mEthStateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			Log.d(TAG, "intent.getAction() =" + intent.getAction());

			if (intent.getAction().equals(EthernetManager.ETHERNET_STATE_CHANGED_ACTION)) {
				handleEthStateChanged(intent.getIntExtra(EthernetManager.EXTRA_ETHERNET_STATE,
						EthernetManager.ETHERNET_STATE_UNKNOWN), intent.getIntExtra(
						EthernetManager.EXTRA_PREVIOUS_ETHERNET_STATE,
						EthernetManager.ETHERNET_STATE_UNKNOWN));
			} else if (intent.getAction().equals(EthernetManager.NETWORK_STATE_CHANGED_ACTION)) {
				handleNetworkStateChanged((NetworkInfo) intent
						.getParcelableExtra(EthernetManager.EXTRA_NETWORK_INFO));
			}
		}
	};

	// /init wire ui
	public void initWireUI() {
		refreshWireUI();
	}

	// refresh wire ui
	public void refreshWireUI() {
		if (mNetSettingActivity.mWifiManager.isWifiEnabled()) {    // change by cwj
			showWireIpUI(false);
		} else {

			mNetSettingActivity.mWireSetting.showWireSetting();
			showWireIpUI(true);
		}
	}

	public void showWireIpUI(boolean isEnable) {
		Log.d(TAG, "showWireIpUI");
		if (isEnable) {
			mNetSettingViewHolder.mWifiSwitchLy.setVisibility(View.GONE);
			mNetSettingViewHolder.mSSIDRelativeLayout.setVisibility(View.GONE);
			mNetSettingViewHolder.mGetIpRelativeLayout.setVisibility(View.VISIBLE);
			mNetSettingViewHolder.mIpInputLyout.setVisibility(View.VISIBLE);
			mNetSettingViewHolder.mIpSaveBtn.setVisibility(View.VISIBLE);

		} else {

			mNetSettingViewHolder.mWifiSwitchLy.setVisibility(View.GONE);
			mNetSettingViewHolder.mSSIDRelativeLayout.setVisibility(View.GONE);
			mNetSettingViewHolder.mGetIpRelativeLayout.setVisibility(View.GONE);
			mNetSettingViewHolder.mIpInputLyout.setVisibility(View.GONE);
			mNetSettingViewHolder.mIpSaveBtn.setVisibility(View.GONE);
		}
	}

	public boolean isWireConnected() {
		return mIsWireConfiged;
	}

	private void handleEthStateChanged(int ethState, int previousEthState) {

		state = ethState;

		switch (ethState) {
		case EthernetStateTracker.EVENT_HW_CONNECTED:
			Log.d(TAG, "EVENT_HW_CONNECTED");
			mIsWireConfiged = true;
			mNetSettingActivity.isWireAutoIpAddress = true;
			break;
		case EthernetStateTracker.EVENT_INTERFACE_CONFIGURATION_SUCCEEDED:
			Log.d(TAG, "EVENT_INTERFACE_CONFIGURATION_SUCCEEDED");
			mIsWireConfiged = true;
			mNetSettingActivity.isWireAutoIpAddress = true;
			break;
		case EthernetStateTracker.EVENT_HW_DISCONNECTED:
			Log.d(TAG, "EVENT_HW_DISCONNECTED");
			mIsWireConfiged = false;
			mNetSettingActivity.isWireAutoIpAddress = false;
			break;
		case EthernetStateTracker.EVENT_INTERFACE_CONFIGURATION_FAILED:
			Log.d(TAG, "EVENT_INTERFACE_CONFIGURATION_FAILED");
			mIsWireConfiged = false;
			mNetSettingActivity.isWireAutoIpAddress = false;
			break;
		default:
			break;
		}

		mNetSettingActivity.initCurrentNetState();
		if (Constants.WIRE_SETTING == mNetSettingActivity.isSelected) {
			refreshWireUI();
		}
	}

	private void handleNetworkStateChanged(NetworkInfo networkInfo) {
		Log.d(TAG, "Received network state changed to " + networkInfo);
	}

	public void onDestoryWire() {
		mNetSettingActivity.unregisterReceiver(mEthStateReceiver);
	}

public String getWireMacAddress() {
	try {
		return loadFileAsString("/sys/class/net/eth0/address")                     //change by cwj
				.toUpperCase().substring(0, 17);
	} catch (IOException e) {
		e.printStackTrace();
		return null;
	}
}

public static String loadFileAsString(String filePath)
		throws java.io.IOException {
	StringBuffer fileData = new StringBuffer(1000);
	BufferedReader reader = new BufferedReader(new FileReader(filePath));
	char[] buf = new char[1024];                                                // change by cwj
	int numRead = 0;
	while ((numRead = reader.read(buf)) != -1) {
		String readData = String.valueOf(buf, 0, numRead);
		fileData.append(readData);
	}
	reader.close();
	return fileData.toString();
}
}
