package com.android.settings.net;


/*
 * @ autor yanhd
 * @ date 2012-1-6
 * @ function 仅仅用于保存当前网络地址如 ：有线ip，netmask，无线ip，pppoe 账户和密码等。
 */
public class CurretNetStatusBak {

	public enum NetType {
		EN_WIRE, EN_WIFI, EN_PPPOE
	}

	// //保存当前，有线ip设置。
	public String mWireIP;
	public String mWireMask;
	public String mWireRoute;
	public String mWireDNS;
	public String mWireDNSbak;

	// //保存当前无线设置。
	public String mWifiIP;
	public String mWifiMask;
	public String mWifiRoute;
	public String mWifiDNS;
	public String mWifiDNSbak;

	// /PPPoE用户，密码
	public String mPPPoEusr;
	public String mPPPoEpwd;

	// /清空数据。
	public void clearDate(NetType type) {
		switch (type) {
		case EN_WIRE: {
			mWireIP = "";
			mWireMask = "";
			mWireRoute = "";
			mWireDNS = "";
			mWireDNSbak = "";
			break;
		}
		case EN_WIFI: {
			mWifiIP = "";
			mWifiMask = "";
			mWifiRoute = "";
			mWifiDNS = "";
			mWifiDNSbak = "";
			break;
		}
		case EN_PPPOE: {
			mPPPoEusr = "";
			mPPPoEpwd = "";
			break;
		}
		default:
			break;
		}
	}

	// /比较是否有静态ip输入更新。
	public boolean isStaticIpUpdate(String ip, String mask, String route,
			String dns1, String dns2) {

		String nullIp = "...";
		if (nullIp.equals(ip))
			ip = "0.0.0.0";
		if (nullIp.equals(mask))
			mask = "0.0.0.0";
		if (nullIp.equals(route))
			route = "0.0.0.0";
		if (nullIp.equals(dns1))
			dns1 = "0.0.0.0";

		if (null == mWireIP)
			mWireIP = "0.0.0.0";
		if (null == mWireMask)
			mWireMask = "0.0.0.0";
		if (null == mWireRoute)
			mWireRoute = "0.0.0.0";
		if (null == mWireDNS)
			mWireDNS = "0.0.0.0";

		if (!mWireIP.equals(ip))
			return true;
		if (!mWireMask.equals(mask))
			return true;
		if (!mWireRoute.equals(route))
			return true;
		if (!mWireDNS.equals(dns1))
			return true;
		// if (null == mWireDNSbak || !mWireDNSbak.equals(dns2))return true;
		// 暂未使用

		return false;
	}

	private Object String(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	// 比较是否有用户名和密码更新
	public boolean isPPPoEUpdate(String usr, String pwd) {

		if (!mPPPoEusr.equals(usr))
			return true;
		if (!mPPPoEpwd.equals(pwd))
			return true;

		return false;
	}
}
