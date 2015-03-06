package com.android.settings.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.settings.R;

/*
 * [2012-3-10 严海东]yanhd@biaoqi.com.cn
 *  wifi hotspot setting
 */
public class WifiApSetting {

	private static final String TAG = "WifiApSetting";

	private int[] SecType = {R.string.wifi_security_open, R.string.wifi_security_wpa, 
		R.string.wifi_security_wpa2};
	private final int SECURE_WPA = 1;
	private final int SECURE_WPA2 = 2;
	private final int SECURE_OPEN = 0;

	private final int MAX_SSID_LENGTH = 32;
	private final int MAX_PWD_LENGTH = 20;

	private int mCurrentSec = 0;

	// /ui widget
	private NetSettingViewHolder mViewHolder;
	// of activity
	private NetSettingActivity mActivity;

	private Button mSecureBtn;
	private CheckBox mCheckBox;
	private TextView mWifiApInfoTxt;
	private EditText mSSIDedit;
	private EditText mPwdEdit;
	private ImageView mSecureLeftImageView;
	private ImageView mSecureRightImageView;

	private WifiConfiguration mWifiApConfig = null;
	private WifiManager mWifiManager;

	private final IntentFilter mIntentFilter;

	public WifiApSetting(NetSettingViewHolder viewHolder, NetSettingActivity activity) {
		this.mViewHolder = viewHolder;
		this.mActivity = activity;

		mIntentFilter = new IntentFilter(WifiManager.WIFI_AP_STATE_CHANGED_ACTION);
		mActivity.registerReceiver(mReceiver, mIntentFilter);
		findViews();
		initWifiAp();
	}

	private void findViews() {
		mSecureBtn = mViewHolder.mWifiApSecButton;
		mCheckBox = mViewHolder.mWifiApSwitchCb;
		mWifiApInfoTxt = mViewHolder.mWifiApInfo;
		mSSIDedit = mViewHolder.mWifiApSSID;
		mPwdEdit = mViewHolder.mWifiApPwd;
		mSecureLeftImageView = mViewHolder.mWifiApSecleft;
		mSecureRightImageView = mViewHolder.mWifiApSecright;
	}

	// /init wifi hotspot status
	public void initWifiAp() {
		mWifiApConfig = mActivity.mWifiManager.getWifiApConfiguration();
		if (null != mWifiApConfig) {
			mCurrentSec = getSecurityTypeIndex(mWifiApConfig);
			Log.e(TAG, "mWifiApConfig.toString()=" + mWifiApConfig.toString());
		} else {
			Log.e(TAG, "mWifiApConfig is null");
		}

		showWifiApInfo();

		// /show ssid and pwd
	//	mSSIDedit.setText(mWifiApConfig.SSID);
		// show password
		if (SECURE_OPEN != getSecurityTypeIndex(mWifiApConfig)) {
			mPwdEdit.setText(mWifiApConfig.preSharedKey);
		} else {
			mPwdEdit.setText("");
		}

		mSecureBtn.setText(SecType[mCurrentSec]);

		setPasswordLayoutVisibale();

		mViewHolder.mWifiApConfigLy.setVisibility(View.GONE);
	}

	// /set password layout status
	public void setPasswordLayoutVisibale() {
		if (SECURE_OPEN != mCurrentSec) {
			mViewHolder.mWifiApPwdLy.setVisibility(View.VISIBLE);
			mViewHolder.mWifiApShowPwdLy.setVisibility(View.VISIBLE);
		} else {
			mViewHolder.mWifiApPwdLy.setVisibility(View.GONE);
			mViewHolder.mWifiApShowPwdLy.setVisibility(View.GONE);
		}
	}

	// /show the wifiap info
	public void showWifiApInfo() {
		// /show ssid and secure
		String text = "";
		String title;
		if (null == mWifiApConfig) {
			Log.e(TAG, "mWifiApConfig ==  null");
			return;
		}
		// /show net ssid
		title = mActivity.getResources().getString(R.string.wifi_net_ssid);
		text += (title + " : " + mWifiApConfig.SSID + "\n");
		// show secure
		String secure = mActivity.getResources().getString(SecType[mCurrentSec]);
		title = mActivity.getResources().getString(R.string.wifi_hotspot_secure);
		text += (title + " : " + secure + "\n");
		mWifiApInfoTxt.setText(text);
	}

/*	public boolean checkWifiNetStatus() {
		// /no dongle plug
		if (!mActivity.mWifiManager.isWifiDeviceExist()) {
			Toast.makeText(mActivity, mActivity.getString(R.string.please_insert_dongle),
					Toast.LENGTH_LONG).show();
			return false;
		}

		// /is wifi pppoe connet
		if (mActivity.mPppoeSetting.isPppoeActive() && !mActivity.mPppoeSetting.isWirePPPoE()) {  // change by cwj
			Toast.makeText(mActivity, mActivity.getString(R.string.please_hangup_pppoe),
					Toast.LENGTH_LONG).show();
			return false;
		}

		return true;
	}*/

	private int getSecurityTypeIndex(WifiConfiguration wifiConfig) {
		if (wifiConfig.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) {
			return SECURE_WPA;
		} else if (wifiConfig.allowedKeyManagement.get(KeyMgmt.WPA2_PSK)) {
			return SECURE_WPA2;
		}
		return SECURE_OPEN;
	}

	// /if secure focus and onkey left
	public void secureOnKeyLeft() {
		switch (mCurrentSec) {
		case SECURE_WPA:
			break;
		case SECURE_WPA2:
			mCurrentSec = SECURE_WPA;
			mPwdEdit.setHint(R.string.modify_wpa);
			break;
		case SECURE_OPEN:
			mCurrentSec = SECURE_WPA2;
			mPwdEdit.setHint(R.string.modify_wpa);
			break;
		default:
			mCurrentSec = SECURE_WPA;
			break;
		}
		mSecureBtn.setText(SecType[mCurrentSec]);
		setPasswordLayoutVisibale();
	}

	// /if secure focus and onkey right
	public void secureOnKeyRight() {
		switch (mCurrentSec) {
		case SECURE_WPA:
			mCurrentSec = SECURE_WPA2;
			mPwdEdit.setHint(R.string.modify_wpa);
			break;
		case SECURE_WPA2:
			mCurrentSec = SECURE_OPEN;
			break;
		case SECURE_OPEN:
			break;
		default:
			mCurrentSec = SECURE_WPA;
			break;
		}
		mSecureBtn.setText(SecType[mCurrentSec]);
		setPasswordLayoutVisibale();
		// backkupCfg(1);
	}

	public WifiConfiguration getApConfig() {

		WifiConfiguration config = new WifiConfiguration();

		/**
		 * TODO: SSID in WifiConfiguration for soft ap is being stored as a raw string without
		 * quotes. This is not the case on the client side. We need to make things consistent and
		 * clean it up
		 */
		config.SSID = mSSIDedit.getText().toString();

		switch (mCurrentSec) {
		case SECURE_OPEN:
			config.allowedKeyManagement.set(KeyMgmt.NONE);
			return config;

		case SECURE_WPA:
			config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
			if (mPwdEdit.length() != 0) {
				String password = mPwdEdit.getText().toString();
				config.preSharedKey = password;
			}
			return config;

		case SECURE_WPA2:
			config.allowedKeyManagement.set(KeyMgmt.WPA2_PSK);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
			if (mPwdEdit.length() != 0) {
				String password = mPwdEdit.getText().toString();
				config.preSharedKey = password;
			}
			return config;
		}
		return null;
	}

	// //save the wifi hotspot config
	public void saveWifiApConfig() {

		// if (SECURE_OPEN != mCurrentSec && mPwdEdit.length() < 8) {
		// // Toast.makeText(mActivity, mActivity.getString(R.string.wifiap_pwd_notice),
		// // Toast.LENGTH_LONG).show();
		// return;
		// }

		if (SECURE_OPEN != mCurrentSec && mPwdEdit.length() < 8) {
			mPwdEdit.requestFocus();
			mPwdEdit.setText("");
			mPwdEdit.setHint(R.string.modify_wpa);
			return;
		}

		if (0 == mSSIDedit.length()) {
			mSSIDedit.setHint(R.string.please_input_ssid);
			return;
		}

		if (mSSIDedit.length() > MAX_SSID_LENGTH || mPwdEdit.length() > MAX_PWD_LENGTH) {
			mSSIDedit.requestFocus();
			mSSIDedit.setText("");
			mSSIDedit.setHint(R.string.please_input_ssid);
			return;
		}

		// WifiConfiguration config = getApConfig();
		mWifiApConfig = getApConfig();
		if (null != mWifiApConfig) {
			if (mActivity.mWifiManager.getWifiApState() == WifiManager.WIFI_AP_STATE_ENABLED) {
				// /restart wifi hotspot
				mActivity.mWifiManager.setWifiApEnabled(null, false);
				mActivity.mWifiManager.setWifiApEnabled(mWifiApConfig, true);
			} else {
				mActivity.mWifiManager.setWifiApConfiguration(mWifiApConfig);
			}
			showWifiApInfo();
			Log.d(TAG, "saveWifiApConfig");
			Toast.makeText(mActivity, mActivity.getString(R.string.wifiap_config_success),
					Toast.LENGTH_LONG).show();
		} else {
			Log.d(TAG, "getApConfig == null;");
		}
	}

	public boolean checkNetStatus(boolean op) {

		/*Log.d(TAG,
				"mActivity.mWifiManager.isWifiDeviceExist():"
						+ mActivity.mWifiManager.isWifiDeviceExist());*/ //  change by cwj
		Log.d(TAG,
				"mActivity.mWifiManager.isWifiApEnabled():"
						+ mActivity.mWifiManager.isWifiApEnabled());

		// /no dongle plug
	/*	if (!mActivity.mWifiManager.isWifiApEnabled()) {
			Toast.makeText(mActivity, mActivity.getString(R.string.please_insert_dongle),  // change by cwj
					Toast.LENGTH_LONG).show();
			return false;
		}*/

		// 判断此网卡是否具有WiFi热点功能
		/*if (op && !mActivity.mWifiManager.isWifiDeviceSupportSoftap()) {
			Toast.makeText(mActivity, mActivity.getString(R.string.check_wifi_hotspot_error),
					Toast.LENGTH_LONG).show();
			Log.e(TAG, "the wifi no SupportSoftap");                                          // change by cwj
			return false;
		}*/

		// /is wifi pppoe connet
		/*if (mActivity.mPppoeSetting.isPppoeActive() && !mActivity.mPppoeSetting.isWirePPPoE()) {
			Toast.makeText(mActivity, mActivity.getString(R.string.please_hangup_pppoe),
					Toast.LENGTH_LONG).show();                                                    // change by cwj
			return false;
		}*/

		return true;
	}

	public void setSoftapEnabled(boolean enable) {

		int apState = mActivity.mWifiManager.getWifiApState();
		Log.d(TAG, "setSoftapEnabled:" + apState);
		if (enable
				&& (apState == WifiManager.WIFI_AP_STATE_ENABLING || apState == WifiManager.WIFI_AP_STATE_ENABLED)) {
			Log.d(TAG, "wifi ap will opened #####################");
			mWifiManager.setWifiEnabled(false);
			return;
		}
		if (enable
				&& (apState == WifiManager.WIFI_AP_STATE_DISABLING || apState == WifiManager.WIFI_AP_STATE_DISABLED)){
				Log.d(TAG, "setSoftapEnabled == enable");
	      	mActivity.mWiFiSetting.closeWifi();
			}

		if (!enable
				&& (apState == WifiManager.WIFI_AP_STATE_DISABLING || apState == WifiManager.WIFI_AP_STATE_DISABLED)) {
			Log.d(TAG, "wifi ap will closed");
			return;
		}

		// /[2012-3-29add]show current switch status
		if (apState == WifiManager.WIFI_AP_STATE_ENABLED) {
			mCheckBox.setChecked(true);
		} else {
			mCheckBox.setChecked(false);
		}

		if (mActivity.mWifiManager.setWifiApEnabled(null, enable)) {
			/* Disable here, enabled on receiving success broadcast */
			mCheckBox.setEnabled(false);
		}
	}

	// /when the device remove;
	public void onWifiApDeviceRemove() {
		if (null != mWifiApConfig && WifiManager.WIFI_AP_STATE_ENABLED == mWifiApConfig.status) {
			if (mCheckBox.isClickable()) {
				Log.d(TAG, "onWifiDeviceRemove and close wifi hotsot");
				setSoftapEnabled(false);
			}
		}
	}

	public void closeWifiAp() {
		Log.d(TAG, "closeWifiAp");
		setSoftapEnabled(false);
	}

	// /wifiap state change
	private void handleWifiApStateChanged(int state) {
		Log.d(TAG, "handleWifiApStateChanged:" + state);

		switch (state) {
		case WifiManager.WIFI_AP_STATE_ENABLING:
			/*mCheckBox.setEnabled(false);*/
			break;
		case WifiManager.WIFI_AP_STATE_ENABLED:
			/**
			 * Summary on enable is handled by tether broadcast notice
			 */
			mCheckBox.setChecked(true);
			/* Doesnt need the airplane check */
			mCheckBox.setEnabled(true);
			break;
		case WifiManager.WIFI_AP_STATE_DISABLING:
			mCheckBox.setEnabled(false);
			break;
		case WifiManager.WIFI_AP_STATE_DISABLED:
			mCheckBox.setEnabled(true);
			mCheckBox.setChecked(false);
			break;
		default:
			mCheckBox.setChecked(false);
			break;
		}
	}

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (WifiManager.WIFI_AP_STATE_CHANGED_ACTION.equals(action)) {
				handleWifiApStateChanged(intent.getIntExtra(WifiManager.EXTRA_WIFI_AP_STATE,
						WifiManager.WIFI_AP_STATE_FAILED));
			}
		}
	};

	public void onDestoryWifiAp() {
		mActivity.unregisterReceiver(mReceiver);
	}
}
