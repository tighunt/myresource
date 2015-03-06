package com.android.settings.net;

import java.util.List;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.settings.R;

/**
 * custom adapter to show wifi's name and level
 * 
 * @author ducj(ducj@biaoqi.com.cn)
 * @since 1.0 2011-11-29
 */
public class WiFiSignalListAdapter extends BaseAdapter { 

	private final static String TAG = "net";

	private static final int SECURITY_NONE = 0;
	private static final int SECURITY_WEP = 1;
	private static final int SECURITY_PSK = 2;
	private static final int SECURITY_EAP = 3;

	private final int[][] WIFI_SIGNAL_IMG = {
			{ R.drawable.wifi_lock_signal_0, R.drawable.wifi_lock_signal_1,
					R.drawable.wifi_lock_signal_2, R.drawable.wifi_lock_signal_3 },
			{ R.drawable.wifi_signal_0, R.drawable.wifi_signal_1, R.drawable.wifi_signal_2,
					R.drawable.wifi_signal_3 } };

	// security level
	private int security;

	private WiFiSignalListActivity mContext;

	private List<ScanResult> mList;

	ViewHolder viewHolder = null;

	// boolean mIsConnected;, boolean isConnected

	public WiFiSignalListAdapter(WiFiSignalListActivity context, List<ScanResult> list) {
		this.mContext = context;
		this.mList = list;
		// this.mIsConnected = isConnected;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater factory = LayoutInflater.from(mContext);

		View view = null;

		if (convertView == null || convertView.getTag() == null) {
			view = factory.inflate(R.layout.wifi_list_item, null);
			viewHolder = new ViewHolder(view);
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) convertView.getTag();
		}

		ScanResult result = mList.get(position);

		WifiManager mWifiManager = mContext.mWifiManager;
		WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
		System.out.println("mWifiManager.isWifiEnabled()" +mWifiManager.isWifiEnabled());
		if (mWifiManager.isWifiEnabled()) {
			if (wifiInfo != null && wifiInfo.getSSID() != null) {			
				// Log.e(TAG, "WiFiSignalListAdapter.igetView.result.level:" + result.level);&&
				// mIsConnected
        String ssid123 = "\"" + result.SSID + "\""; // change by cwj
				if (ssid123.equals(wifiInfo.getSSID()) && result.level != Constants.MIN_RSSI) {
					viewHolder.isconnect.setText(mContext.getResources().getString(
							R.string.wifi_isconnect));
				} else {
					viewHolder.isconnect.setText("");
				}
			} else {
				// wifiInfo 或 wifiInfo.getSSID() 为空
				viewHolder.isconnect.setText("");
			}
		} else {
			// wifi 不可用。
			viewHolder.isconnect.setText("");
		}

		viewHolder.ssid.setText(result.SSID);
		viewHolder.level.setText(getLevel(result.level) + "");
		try {
			security = getSecurity(result);
		} catch (Exception e) {
			security = SECURITY_NONE;
		}

		setBackground(getLevel(result.level));
		return view;
	}

	/**
	 * Calculates the level of the signal
	 * 
	 * @param level
	 * @return
	 */
	int getLevel(int level) {
		if (level == Integer.MAX_VALUE) {
			return -1;
		}
		return WifiManager.calculateSignalLevel(level, 4);
	}

	/**
	 * set the drawable to show the wifi signal level
	 * 
	 * @param level
	 */
	private void setBackground(int level) {
		if (level == Integer.MAX_VALUE) {
			viewHolder.icon.setImageDrawable(null);
		} else {
			int index = (security != SECURITY_NONE) ? 0 : 1;
			if (level < 0 || 3 < level)
				level = 0;
			viewHolder.icon.setImageResource(WIFI_SIGNAL_IMG[index][level]);
			/*
			 * viewHolder.icon.setImageResource(R.drawable.wifi_signal);
			 * viewHolder.icon.setImageLevel(level); viewHolder.icon.setImageState((security !=
			 * SECURITY_NONE) ? STATE_SECURED : STATE_NONE, true);
			 */
		}
	}

	/**
	 * get the security level
	 * 
	 * @param result
	 * @return
	 */
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

	/**
	 * component init
	 */
	class ViewHolder {
		TextView ssid;

		TextView isconnect;

		TextView level;

		ImageView icon;

		public ViewHolder(View view) {
			this.ssid = (TextView) view.findViewById(R.id.ssid);
			this.isconnect = (TextView) view.findViewById(R.id.isconnect);
			this.level = (TextView) view.findViewById(R.id.level);
			this.icon = (ImageView) view.findViewById(R.id.iv);
		}
	}
}
