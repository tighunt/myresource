package com.android.settings.update;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import android.content.Context;
import android.net.ethernet.EthernetDevInfo;
import android.net.ethernet.EthernetManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class NetInfo {
	private String mIpAddr = "";
	private String mNetName = "";
	private String mMacAddr = "";
	private Context mContext = null;

	public NetInfo(Context context) {
		mContext = context;
	}

	public String getIpAddr() {
		return mIpAddr;
	}

	public String getNetName() {
		return mNetName;
	}

	public String getMac() {
		return mMacAddr;
		//return "AA:AA:AA:AA:AA:AA";
	}

	public boolean getNetInfo() {
		// 有线eth0、eth1等，无线wlan0、wlan1等
		boolean r = false;
		r = isNetInterfaceAvailable("eth0");
		if (r) {
			getEthernetInfo();
			return r;
		}

		r = isNetInterfaceAvailable("eth1");
		if (r) {
			getEthernetInfo();
			return r;
		}

		r = isNetInterfaceAvailable("wlan0");
		if (r) {
			getWifiInfo();
			//此时升级时取有线网络的MAC地址
			mMacAddr = getEthernetMAC();
			return r;
		}

		r = isNetInterfaceAvailable("wlan1");
		if (r) {
			getWifiInfo();
			//此时升级时取有线网络的MAC地址
			mMacAddr = getEthernetMAC();
			return r;
		}

		return r;
	}

	/**
	 * 取有线网络的MAC地址
	 * @return
	 */
	private String getEthernetMAC() {
		EthernetManager ethManager = (EthernetManager) mContext.getSystemService(mContext.ETHERNET_SERVICE);
		EthernetDevInfo ethInfo = ethManager.getSavedConfig();
		return ethManager.getMacAddress();
	}

	private boolean getEthernetInfo() {
		EthernetManager ethManager = (EthernetManager) mContext.getSystemService(mContext.ETHERNET_SERVICE);
		EthernetDevInfo ethInfo = ethManager.getSavedConfig();
		mIpAddr = ethInfo.getIpAddress();
		mNetName = ethInfo.getIfName();
		mMacAddr = ethManager.getMacAddress();
		Log.d("Updater", "getEthernetInfo.mIpAddr=" + mIpAddr);
		Log.d("Updater", "getEthernetInfo.mNetName=" + mNetName);
		Log.d("Updater", "getEthernetInfo.mMacAddr=" + mMacAddr);
		return true;
	}

	private boolean getWifiInfo() {
		WifiManager wifiManager = (WifiManager) mContext.getSystemService(mContext.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		if (null != wifiInfo) {
			long addr = wifiInfo.getIpAddress();
			mNetName = wifiInfo.getBSSID();
			mMacAddr = wifiInfo.getMacAddress();
			if (0 != addr) {
				// handle negative values whe first octet > 127
				if (addr < 0) {
					addr += 0x100000000L;
				}
				mIpAddr = String.format("%d.%d.%d.%d", addr & 0xFF, (addr >> 8) & 0xFF, (addr >> 16) & 0xFF,
						(addr >> 24) & 0xFF);
			}
		}

		if (null != wifiInfo && null != mNetName) {
			return true;
		}

		return false;

	}

	private boolean isNetInterfaceAvailable(String netif) {
		String netInterfaceStatusFile = "/sys/class/net/" + netif + "/carrier";
		boolean bStatus = isStatusAvailable(netInterfaceStatusFile);
		Log.d("Updater", "isNetInterfaceAvailable.bStatus=" + bStatus);
		return bStatus;
	}

	private boolean isStatusAvailable(String statusFile) {
		char st = readStatus(statusFile);
		if (st == '1') {
			return true;
		}
		return false;
	}

	private synchronized char readStatus(String filePath) {
		File file = new File(filePath);
		int tempChar = 0;
		if (file.exists()) {
			Reader reader = null;
			try {
				reader = new InputStreamReader(new FileInputStream(file));
				try {
					tempChar = reader.read();
				} catch (IOException e) {
					Log.d("Updater", "readStatus.IOException=" + e.getLocalizedMessage());
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				Log.d("Updater", "readStatus.FileNotFoundException=" + e.getLocalizedMessage());
				e.printStackTrace();
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
					Log.d("Updater", "readStatus.finally IOException=" + e.getLocalizedMessage());
					e.printStackTrace();
				}
			}

		}
		return (char) tempChar;
	}

}