package com.android.settings.net;

/**
 * 网络设置中的状态
 * 
 * @author 杜聪甲(ducj@biaoqi.com.cn)
 * @date 2011-11-7 下午04:36:31
 * @since 1.0
 */
public class Constants {

	// 网络状态
	public final static int NET_STATE = 0;

	// 有线设置
	public final static int WIRE_SETTING = 1;

	// 无线设置
	public final static int WIRELESS_SETTING = 2;

	// Wifi direct setting
	public final static int WIFI_DIRECT = 3;

	// /Wifi hotspot
	public final static int WIFI_HOTSPOT = 4;

	// PPPOE设置
	public final static int PPPOE_SETTING = 5;

	// /location
	public final static int LOCALTON_SERVICE = 6;

	// 左侧 类表 items 数量。[ 2012-2-8 add]
	public final static int LIST_ITEMS = 5;// 7; zhs 2012-04-10

	// 有线自动获取IP地址
	public final static int WIRE_AUTO_IP_ADDRESS = 8;

	// IP地址
	public final static int IP_ADDRESS = 9;

	// 子网掩码
	public final static int SUBNET_MASK = 10;

	// 默认网关
	public final static int DEFAULT_GATEWAY = 11;

	// 首选DNS
	public final static int FIRST_DNS = 12;

	// 备用DNS
	public final static int SECOND_DNS = 13;

	// 是否自动获取IP地址
	public final static int IS_AUTO_IP_ADDRESS = 14;

	// wifi switch
	public final static int WIFI_SWITCH = 15;

	// SSID
	public final static int SSID = 16;

	// 连接方式
	public final static int CONNECT_FORMAT = 17;

	// PPPOE拨号
	public final static int PPPOE_DIALER = 18;

	// PPPOE设置自动获取IP地址
	public final static int PPPOE_AUTO_IP = 19;

	// PPPOE用户名
	public final static int PPPOE_USERNAME = 20;

	// PPPOE密码
	public final static int PPPOE_PASSWORD = 21;

	// PPPOE密码显示
	public final static int PPPOE_PASSWORD_SHOW = 22;

	// PPPOE自动拨号
	public final static int PPPOE_AUTO_DIALER = 23;

	// PPPOE 拨号
	public final static int PPPOE_DIALER_ACTION = 24;

	// is wifi direct open
	public final static int IS_WIFI_DIRECT_OPEN = 25;

	// //wifi direct discover
	public final static int WIFI_DIRECT_DISCOVER = 26;

	// /ip address save
	public final static int IP_ADDRESS_SAVE = 27;

	// /wifi hotspot sitch
	public final static int WIFI_AP_SWITCH = 28;

	// /wifi hotspot setting
	public final static int WIFI_AP_SETTING = 29;

	// /wifi hotspot ssid edit
	public final static int WIFI_AP_SSID_EDIT = 30;

	// /wifi hotspot secure select
	public final static int WIFI_AP_SECURE = 31;

	// /wifi hotspot password edit
	public final static int WIFI_AP_PWD_EDIT = 32;

	// /wifi hotspot show password checkbox
	public final static int WIFI_AP_SHOW_PWD = 33;

	// /wifi hotspot save config button
	public final static int WIFI_AP_SHOW_SAVE = 34;

	// /location service switch
	public final static int LOCATION_SERVICE_SW = 35;

	public static final int SECURITY_NONE = 0;
	public static final int SECURITY_WEP = 1;
	public static final int SECURITY_PSK = 2;
	public static final int SECURITY_EAP = 3;
	
    /** Anything worse than or equal to this will show 0 bars. */
	public static final int MIN_RSSI = -100;

}
