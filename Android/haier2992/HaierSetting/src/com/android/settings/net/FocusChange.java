package com.android.settings.net;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.settings.R;

/**
 * 控制遥控器在上下移动过程中焦点的切换
 * 
 * @author 杜聪甲(ducj@biaoqi.com.cn)
 * @date 2011-11-7 下午07:28:03
 * @since 1.0
 */
public class FocusChange {

	public static int mFocusLocal = 0;

	/**
	 * 响应IP设置遥控器向下按
	 */
	public static void wireSettingDropDown(NetSettingViewHolder netSettingViewHolder, int selected) {

		switch (mFocusLocal) {
		case Constants.WIFI_SWITCH:
			if (selected == 1) {
				mFocusLocal = Constants.IS_AUTO_IP_ADDRESS;
			} else {
				mFocusLocal = Constants.SSID;
				netSettingViewHolder.mSSID.requestFocus();
			}
			break;
		case Constants.SSID:
			if (selected == 1) {
				mFocusLocal = Constants.IS_AUTO_IP_ADDRESS;
			} else {
				mFocusLocal = Constants.IS_AUTO_IP_ADDRESS;
				netSettingViewHolder.mAutoIpAddress.requestFocus();
			}
			break;
		case Constants.IS_AUTO_IP_ADDRESS:
			mFocusLocal = Constants.IP_ADDRESS;
			netSettingViewHolder.mIpAddressRelativeLayout.setBackgroundResource(R.drawable.desktop_button);
			netSettingViewHolder.ip_one_ev.requestFocus();
			break;
		case Constants.IP_ADDRESS:
			mFocusLocal = Constants.SUBNET_MASK;
			changeBackground(netSettingViewHolder.mIpAddressRelativeLayout, netSettingViewHolder.mSubnetRelativeLayout);
			netSettingViewHolder.subnet_one_ev.requestFocus();
			break;
		case Constants.SUBNET_MASK:
			mFocusLocal = Constants.DEFAULT_GATEWAY;
			changeBackground(netSettingViewHolder.mSubnetRelativeLayout,
					netSettingViewHolder.mDefaultGatewayRelativeLayout);
			netSettingViewHolder.defult_one_ev.requestFocus();
			break;
		case Constants.DEFAULT_GATEWAY:
			mFocusLocal = Constants.FIRST_DNS;
			changeBackground(netSettingViewHolder.mDefaultGatewayRelativeLayout,
					netSettingViewHolder.mFirstDnsRelativeLayout);
			netSettingViewHolder.first_dns_one_ev.requestFocus();
			break;
		case Constants.FIRST_DNS:
			mFocusLocal = Constants.SECOND_DNS;
			changeBackground(netSettingViewHolder.mFirstDnsRelativeLayout,
					netSettingViewHolder.mSecondDnsRelativeLayout);
			netSettingViewHolder.second_dns_one_ev.requestFocus();
			break;
		case Constants.SECOND_DNS:
			mFocusLocal = Constants.IP_ADDRESS_SAVE;
			netSettingViewHolder.mSecondDnsRelativeLayout.setBackgroundResource(R.drawable.one_px);
			netSettingViewHolder.mIpSaveBtn.requestFocus();
			netSettingViewHolder.mIpSaveBtn.setBackgroundResource(R.drawable.edit_focus);
			break;
		}
	}
	
	/**
	 * 响应IP设置遥控器向上按
	 */
	public static void wireSettingDropUp(NetSettingViewHolder netSettingViewHolder, int selected) {
		switch (mFocusLocal) {
		case Constants.SSID:
			if (selected == 1) {
			} else {
				mFocusLocal = Constants.WIFI_SWITCH;
				netSettingViewHolder.mWifiSwitchCb.requestFocus();
			}
			break;
		case Constants.IS_AUTO_IP_ADDRESS:
			if (selected == 1) {
				mFocusLocal = Constants.IP_ADDRESS;
			} else {
				mFocusLocal = Constants.SSID;
				netSettingViewHolder.mSSID.requestFocus();
			}
			break;
		case Constants.IP_ADDRESS:
			mFocusLocal = Constants.IS_AUTO_IP_ADDRESS;
			netSettingViewHolder.mIpAddressRelativeLayout.setBackgroundResource(R.drawable.one_px);
			netSettingViewHolder.mAutoIpAddress.requestFocus();
			break;
		case Constants.SUBNET_MASK:
			mFocusLocal = Constants.IP_ADDRESS;
			changeBackground(netSettingViewHolder.mSubnetRelativeLayout, netSettingViewHolder.mIpAddressRelativeLayout);
			netSettingViewHolder.ip_one_ev.requestFocus();
			break;
		case Constants.DEFAULT_GATEWAY:
			mFocusLocal = Constants.SUBNET_MASK;
			changeBackground(netSettingViewHolder.mDefaultGatewayRelativeLayout,
					netSettingViewHolder.mSubnetRelativeLayout);
			netSettingViewHolder.subnet_one_ev.requestFocus();
			break;
		case Constants.FIRST_DNS:
			mFocusLocal = Constants.DEFAULT_GATEWAY;
			changeBackground(netSettingViewHolder.mFirstDnsRelativeLayout,
					netSettingViewHolder.mDefaultGatewayRelativeLayout);
			netSettingViewHolder.defult_one_ev.requestFocus();
			break;
		case Constants.SECOND_DNS:
			mFocusLocal = Constants.FIRST_DNS;
			changeBackground(netSettingViewHolder.mSecondDnsRelativeLayout,
					netSettingViewHolder.mFirstDnsRelativeLayout);
			netSettingViewHolder.first_dns_one_ev.requestFocus();
			break;
		case Constants.IP_ADDRESS_SAVE:
			mFocusLocal = Constants.SECOND_DNS;
			netSettingViewHolder.mSecondDnsRelativeLayout.setBackgroundResource(R.drawable.desktop_button);
			netSettingViewHolder.second_dns_one_ev.requestFocus();
			netSettingViewHolder.mIpSaveBtn.setBackgroundResource(R.drawable.edit_normal);
			break;
		}
	}

	/**
	 * 网络状态遥控器上下控制
	 */
	public static void netStateDrop(NetSettingViewHolder netSettingViewHolder) {
		switch (mFocusLocal) {
		case Constants.CONNECT_FORMAT:
			/*
			mFocusLocal = Constants.PPPOE_DIALER;
			changeBackground(netSettingViewHolder.mConnectFormatRelativeLayout,
					netSettingViewHolder.mPPPOERelativeLayout);
			netSettingViewHolder.mNetSetting.clearFocus();
			netSettingViewHolder.mPPPOERelativeLayout.requestFocus();
			*/
			break;
		case Constants.PPPOE_DIALER:
			/*
			mFocusLocal = Constants.CONNECT_FORMAT;
			changeBackground(netSettingViewHolder.mPPPOERelativeLayout,
					netSettingViewHolder.mConnectFormatRelativeLayout);
			netSettingViewHolder.mConnectFormatRelativeLayout.requestFocus();
			netSettingViewHolder.mNetSetting.clearFocus();
			*/
			break;
		}
	}

	/**
	 * 当无线网络是自动获取IP地址时，进行焦点的切换
	 * 
	 * @param netSettingViewHolder
	 */
	public static void wireLessDrop(NetSettingViewHolder netSettingViewHolder) {
		switch (mFocusLocal) {
		case Constants.SSID:
			mFocusLocal = Constants.IS_AUTO_IP_ADDRESS;
			changeBackground(netSettingViewHolder.mSSIDRelativeLayout, netSettingViewHolder.mGetIpRelativeLayout);
			netSettingViewHolder.mAutoIpAddress.requestFocus();
			break;
		case Constants.IS_AUTO_IP_ADDRESS:
			mFocusLocal = Constants.SSID;
			changeBackground(netSettingViewHolder.mGetIpRelativeLayout, netSettingViewHolder.mSSIDRelativeLayout);
			netSettingViewHolder.mSSID.requestFocus();
			break;
		}
	}

	/**
	 * PPPOE设置中遥控器向下移动
	 */
	public static void pppoeSettingDropDown(NetSettingViewHolder netSettingViewHolder) {
		switch (mFocusLocal) {
		case Constants.PPPOE_AUTO_IP:
			mFocusLocal = Constants.PPPOE_USERNAME;
			//changeBackground(netSettingViewHolder.mPPPOEAutoIPRelativeLayout,
			//		netSettingViewHolder.mPPPOEUsernameRelativeLayout);
			netSettingViewHolder.mPPPOEAutoIPRelativeLayout.setBackgroundResource(R.drawable.one_px);
			netSettingViewHolder.mUsernameEditText.requestFocus();
			break;
		case Constants.PPPOE_USERNAME:
			mFocusLocal = Constants.PPPOE_PASSWORD;
			//changeBackground(netSettingViewHolder.mPPPOEUsernameRelativeLayout,
			//		netSettingViewHolder.mPPPOEPasswordRelativeLayout);
			netSettingViewHolder.mPassWordEditText.requestFocus();
			break;
		case Constants.PPPOE_PASSWORD:
			mFocusLocal = Constants.PPPOE_PASSWORD_SHOW;
			//changeBackground(netSettingViewHolder.mPPPOEPasswordRelativeLayout,
			//		netSettingViewHolder.mPasswordShowRelativeLayout);
			netSettingViewHolder.mPasswordShowRelativeLayout.setBackgroundResource(R.drawable.desktop_button);
			netSettingViewHolder.mPasswordShowRelativeLayout.requestFocus();
			break;
		
		case Constants.PPPOE_PASSWORD_SHOW:
			mFocusLocal = Constants.PPPOE_DIALER_ACTION;
			netSettingViewHolder.mPasswordShowRelativeLayout.setBackgroundResource(R.drawable.one_px);
			netSettingViewHolder.mDialerActionBtn.requestFocus();
			break;
		}
	}

	/**
	 * PPPOE设置中遥控器向上移动
	 */
	public static void pppoeSettingDropUp(NetSettingViewHolder netSettingViewHolder) {
		switch (mFocusLocal) {
		case Constants.PPPOE_USERNAME:
			mFocusLocal = Constants.PPPOE_AUTO_IP;
			//changeBackground(netSettingViewHolder.mPPPOEUsernameRelativeLayout,
			//		netSettingViewHolder.mPPPOEAutoIPRelativeLayout);
			netSettingViewHolder.mPPPOEAutoIPRelativeLayout.setBackgroundResource(R.drawable.desktop_button);
			netSettingViewHolder.mPPPOEConnetFormat.requestFocus();
			break;
		case Constants.PPPOE_PASSWORD:
			mFocusLocal = Constants.PPPOE_USERNAME;
			//changeBackground(netSettingViewHolder.mPPPOEPasswordRelativeLayout,
			//		netSettingViewHolder.mPPPOEUsernameRelativeLayout);
			netSettingViewHolder.mUsernameEditText.requestFocus();
			break;
		case Constants.PPPOE_PASSWORD_SHOW:
			mFocusLocal = Constants.PPPOE_PASSWORD;
			//changeBackground(netSettingViewHolder.mPasswordShowRelativeLayout,
			//		netSettingViewHolder.mPPPOEPasswordRelativeLayout);
			netSettingViewHolder.mPasswordShowRelativeLayout.setBackgroundResource(R.drawable.one_px);
			netSettingViewHolder.mPassWordEditText.requestFocus();
			break;
		/*
		case Constants.PPPOE_AUTO_DIALER:
			mFocusLocal = Constants.PPPOE_PASSWORD_SHOW;
			changeBackground(netSettingViewHolder.mAutoDialerRelativeLayout,
					netSettingViewHolder.mPasswordShowRelativeLayout);
			break;
			*/
		case Constants.PPPOE_DIALER_ACTION:
			mFocusLocal = Constants.PPPOE_PASSWORD_SHOW;
			netSettingViewHolder.mPasswordShowRelativeLayout.setBackgroundResource(R.drawable.desktop_button);
			netSettingViewHolder.mPasswordShowRelativeLayout.requestFocus();
		}
	}

	
	//wifi hotspot actoion down
	public static void wifiHotSpotDropDown(NetSettingViewHolder netSettingViewHolder){
		switch (mFocusLocal) {
		case Constants.WIFI_AP_SWITCH:
			mFocusLocal = Constants.WIFI_AP_SETTING;
			netSettingViewHolder.mWifiApSetBtn.requestFocus();
			break;
		case Constants.WIFI_AP_SETTING:
			if (netSettingViewHolder.mWifiApConfigLy.isShown()){
				mFocusLocal = Constants.WIFI_AP_SSID_EDIT;
				netSettingViewHolder.mWifiApSSID.requestFocus();
			}
			break;
		case Constants.WIFI_AP_SSID_EDIT:
			mFocusLocal = Constants.WIFI_AP_SECURE;
			netSettingViewHolder.mWifiApSecButton.requestFocus();
			break;
		case Constants.WIFI_AP_SECURE:
			if (netSettingViewHolder.mWifiApPwdLy.isShown()){
				mFocusLocal = Constants.WIFI_AP_PWD_EDIT;
				netSettingViewHolder.mWifiApPwd.requestFocus();
			}else{
				mFocusLocal = Constants.WIFI_AP_SHOW_SAVE;
				netSettingViewHolder.mWifiApSaveBtn.requestFocus();
			}
			break;
		case Constants.WIFI_AP_PWD_EDIT:
			mFocusLocal = Constants.WIFI_AP_SHOW_PWD;
			netSettingViewHolder.mWifiApShowPwdCb.requestFocus();
			break;
		case Constants.WIFI_AP_SHOW_PWD:
			mFocusLocal = Constants.WIFI_AP_SHOW_SAVE;
			netSettingViewHolder.mWifiApSaveBtn.requestFocus();
			break;
		case Constants.WIFI_AP_SHOW_SAVE:
			break;
			default:
				break;
		}
	}
	
	//wifi hotspot actoion up
	public static void wifiHotSpotDropUp(NetSettingViewHolder netSettingViewHolder){
		switch (mFocusLocal) {
		case Constants.WIFI_AP_SWITCH:
			break;
		case Constants.WIFI_AP_SETTING:
			mFocusLocal = Constants.WIFI_AP_SWITCH;
			netSettingViewHolder.mWifiApSwitchCb.requestFocus();
			break;
		case Constants.WIFI_AP_SSID_EDIT:
			mFocusLocal = Constants.WIFI_AP_SETTING;
			netSettingViewHolder.mWifiApSetBtn.requestFocus();
			break;
		case Constants.WIFI_AP_SECURE:
			mFocusLocal = Constants.WIFI_AP_SSID_EDIT;
			netSettingViewHolder.mWifiApSSID.requestFocus();
			break;
		case Constants.WIFI_AP_PWD_EDIT:
			mFocusLocal = Constants.WIFI_AP_SECURE;
			netSettingViewHolder.mWifiApSecButton.requestFocus();
			break;
		case Constants.WIFI_AP_SHOW_PWD:
			mFocusLocal = Constants.WIFI_AP_PWD_EDIT;
			netSettingViewHolder.mWifiApPwd.requestFocus();
			break;
		case Constants.WIFI_AP_SHOW_SAVE:
			if (netSettingViewHolder.mWifiApPwdLy.isShown()){
				mFocusLocal = Constants.WIFI_AP_SHOW_PWD;
				netSettingViewHolder.mWifiApShowPwdCb.requestFocus();
			}else{
				mFocusLocal = Constants.WIFI_AP_SECURE;
				netSettingViewHolder.mWifiApSecButton.requestFocus();
			}
			
			break;
			default:
				break;
		}
	}
	
	/**
	 * 焦点切换时背景色的改变
	 * 
	 * @param sourc
	 * @param dest
	 */
	public static void changeBackground(RelativeLayout sourc, RelativeLayout dest) {
		sourc.setBackgroundResource(R.drawable.one_px);
		dest.setBackgroundResource(R.drawable.desktop_button);
	}
	
	public static void changeBackground(LinearLayout sourc, LinearLayout dest) {
		sourc.setBackgroundResource(R.drawable.one_px);
		dest.setBackgroundResource(R.drawable.desktop_button);
	}
	
	public static void wireOrWirelessSettingDropDown(NetSettingViewHolder netSettingViewHolder, int selected) {

		switch (mFocusLocal) {
		case Constants.WIFI_SWITCH:
			if (selected == 1) {
				mFocusLocal = Constants.IS_AUTO_IP_ADDRESS;
			} else {
				mFocusLocal = Constants.SSID;
				netSettingViewHolder.mSSID.requestFocus();
			}
			break;
		case Constants.SSID:
			if (selected == 1) {
				mFocusLocal = Constants.IS_AUTO_IP_ADDRESS;
			} else {
				mFocusLocal = Constants.IS_AUTO_IP_ADDRESS;
				netSettingViewHolder.mAutoIpAddress.requestFocus();
			}
			break;
		case Constants.IS_AUTO_IP_ADDRESS:
			mFocusLocal = Constants.IP_ADDRESS_SAVE;
			//netSettingViewHolder.mIpAddressRelativeLayout.setBackgroundResource(R.drawable.desktop_button);
			netSettingViewHolder.mGetIpRelativeLayout.setBackgroundResource(R.drawable.one_px);
			netSettingViewHolder.mIpSaveBtn.requestFocus();
			netSettingViewHolder.mIpSaveBtn.setBackgroundResource(R.drawable.edit_focus);
			break;
		}
	}
	
	public static void wireOrWirelessSettingDropUp(NetSettingViewHolder netSettingViewHolder, int selected) {
		switch (mFocusLocal) {
		case Constants.SSID:
			if (selected == 1) {
			} else {
				mFocusLocal = Constants.WIFI_SWITCH;
				netSettingViewHolder.mWifiSwitchCb.requestFocus();
			}
			break;
		case Constants.IS_AUTO_IP_ADDRESS:
			if (selected == 1) {
				//mFocusLocal = Constants.IP_ADDRESS;
			} else {
				mFocusLocal = Constants.SSID;
				netSettingViewHolder.mSSID.requestFocus();
			}
			break;
		case Constants.IP_ADDRESS_SAVE:
			mFocusLocal = Constants.IS_AUTO_IP_ADDRESS;
			//netSettingViewHolder.mSecondDnsRelativeLayout.setBackgroundResource(R.drawable.desktop_button);
			netSettingViewHolder.mAutoIpAddress.requestFocus();
			netSettingViewHolder.mIpSaveBtn.setBackgroundResource(R.drawable.edit_normal);
			break;
		}
	}
	
}
