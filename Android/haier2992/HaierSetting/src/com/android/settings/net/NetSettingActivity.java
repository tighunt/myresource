package com.android.settings.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ethernet.EthernetDevInfo;
import android.net.ethernet.EthernetManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.android.settings.R;

/**
 * <b>网络设置的主界面</b><br>
 * 
 * @author 杜聪甲(ducj@biaoqi.com.cn)
 * @date 2011-10-28 上午09:48:17
 * @since 1.0
 */

public class NetSettingActivity extends Activity {
	private static String TAG = "NetSettingActivity";

//	private static final int WIFI_OPENED = 0;
//	private static final int WIFI_CLOSED = 1;

	final static String WIRE_AUTO_IP_ADDRESS = "wire_auto_ip_address";

	final static String WIRELESS_AUTO_IP_ADDRESS = "wireless_auto_ip_address";

	NetSettingViewHolder mNetSettingViewHolder;

    private NetSettingListeners mNetSettingListeners;

	private ProgressDialog mProgressDialog;

	private int[] state = { R.string.wire_connect, R.string.wireless_connect,
			R.string.pppoe_connect };

	public int mPreKeyCode;// /[2012-1-14 ]记录上次的keyevent 以便在InputIPAddress 光标切换。

	int isSelected;
	// 有线是否自动获取IP地址
	boolean isWireAutoIpAddress;
	// 无线是否自动获取IP地址
	boolean isWirelessAutoIpAddress;
	// 用于判断焦点是否在右边
	boolean focusIsRight = false;
	// 网络状态是否修改连接方式
	boolean isModifyConnectFormat = false;
	// PPPOE是否修改连接方式
	// boolean isPPPOEModifyConnectFormat = false;
	// PPPOE是否是有线连接
	// boolean isWirePPPPOE = true;
	// 用于标记连接状态
	int currentConnectFormat = 0;
	// 用于判断PPPOE是否连接
//	boolean pppoeIsConnect = false; change by cwj

	// wire
	WireSetting mWireSetting;
	WiFiSetting mWiFiSetting;
	// PPPOE连接的相关设置
//	PPPOESetting mPppoeSetting; // change by cwj
	WifiDirectSetting mWifiDirect;// [2012-2-23]
	WifiApSetting mWifiApSet;
	// location service
	LocationSetting mLocalSetting;

	WifiReceiver receiverWifi;

	// /public WifiManager for other
	public WifiManager mWifiManager;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
//			 case WIFI_OPENED:
//			 showProgressDialog(R.string.wifi_is_opening);
//			 break;
//			 case WIFI_CLOSED:
//			 showProgressDialog(R.string.wifi_is_closing);
//			 break;
//			 default:
//			 break;
			}
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.net_setting);
		findViews();
		registerListener();
		// mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		// /dongle plug and remove Listener
		receiverWifi = new WifiReceiver();
		IntentFilter wifiFilter = new IntentFilter();
		wifiFilter.addAction(WifiManager.ACTION_PICK_WIFI_NETWORK); // change by cwj
//		wifiFilter.addAction(WifiManager.ACTION_PICK_WIFI_NETWORK); // change by cwj
		wifiFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		registerReceiver(receiverWifi, wifiFilter);

		Log.v("zjf",
				"NetSettingActivity.onCreate.mNetSettingViewHolder.mDialerStatusTxt.toString():"
						+ mNetSettingViewHolder.mDialerStatusTxt.toString());
		Log.v("zjf", "NetSettingActivity.onCreate.R.string.pppoe_connect_success:"
				+ R.string.pppoe_connect_success);

		if (mNetSettingViewHolder.mDialerStatusTxt.toString()
				.equals(R.string.pppoe_connect_success)) {

			EthernetManager mEthernetManager = (EthernetManager) this
					.getSystemService(Context.CONNECTIVITY_SERVICE); // change by cwj
			Log.v("zjf", "NetSettingActivity.onCreate.mEthernetManager:" + mEthernetManager);
			EthernetDevInfo mEthInfo = mEthernetManager.getSavedConfig();
			Log.v("zjf", "NetSettingActivity.onCreate.mEthInfo:" + mEthInfo);
			if (mEthInfo != null) {
				this.mWireSetting.showWireMac(mEthInfo);
			} else {
				this.mWireSetting.showWireMac(null);
			}
		}
	}

	class WifiReceiver extends BroadcastReceiver {
		public void onReceive(Context c, Intent intent) {

			String act = intent.getAction();

			if (null == act)
				return;

			if (act.equals(WifiManager.ACTION_PICK_WIFI_NETWORK)) { // change by cwj 
				Log.d(TAG, "WIFI_DEVICE_ADDED_ACTION");
			} else if (act.equals(WifiManager.WIFI_STATE_DISABLING)) { // change by cwj
				Log.d(TAG, "WIFI_DEVICE_REMOVED_ACTION");
				mWiFiSetting.onDeviceRemove();
				mWifiDirect.onWifiDeviceRemove();
				mWifiApSet.onWifiApDeviceRemove();
			} else if (act.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
				// updateWifiState(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
				// WifiManager.WIFI_STATE_UNKNOWN));
			}
		}
	}

	@Override
	protected void onResume() {

		Log.d(TAG, "====>onResume");

		mWiFiSetting.onResumeWifi();

		switch (isSelected) {
		case Constants.NET_STATE:
			break;
		case Constants.WIRE_SETTING:
			break;
		case Constants.WIRELESS_SETTING: {
			Log.d(TAG, "NetSettingActivity.onResume_wifi ip Refresh......");
			// mWiFiSetting.showWiFiSetting(); // [2012-2-1] show ip;
			mWiFiSetting.initWifiUI();
		}
			break;
//		case Constants.PPPOE_SETTING:  // change by cwj
//			break;
		default:
			break;
		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {

		Log.d(TAG, "====>onDestroy");
		mWifiDirect.onDestoryDirect();
//		mPppoeSetting.onDestoryPppoe(); // change by cwj
		mWireSetting.onDestoryWire();
		mWiFiSetting.onDestoryWifi();
		mWifiApSet.onDestoryWifiAp();
		unregisterReceiver(receiverWifi);// [2012-2-16]
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		Log.d(TAG, "====>onPause");
		mWiFiSetting.onPauseWifi();
		super.onPause();
	}

	// /[2012-2-16add]
	private void initConnectMode(WifiInfo wifiInfo) {

		if (mWifiManager.isWifiEnabled() && null != wifiInfo && (null != wifiInfo.getBSSID())) {
			currentConnectFormat = 1;
			mNetSettingViewHolder.mConnectFormat.setText(state[currentConnectFormat]);
			Log.d(TAG, "close eth0");
		} else {
			currentConnectFormat = 0;
			mNetSettingViewHolder.mConnectFormat.setText(state[currentConnectFormat]);
		}
	}

	/**
	 * 获取当前网络连接方式
	 */
	public void initCurrentNetState() {

		Log.d(TAG, "initCurrentNetState");
		// /init UI
		mNetSettingViewHolder.mWireNetState.setVisibility(View.GONE);
		mNetSettingViewHolder.mPPPOENetState.setVisibility(View.GONE);
		mNetSettingViewHolder.mWiFiNetState.setVisibility(View.GONE);

		WifiInfo info = mWifiManager.getConnectionInfo();
		initConnectMode(info);

		// delete pppoe 2012-05-23
		/*
		 * if (mPppoeSetting.isPppoeConnected()) {
		 * mNetSettingViewHolder.mPPPOENetState.setVisibility(View.VISIBLE);
		 * mPppoeSetting.showPppoeStatus1(); } else
		 */

		if (mWifiManager.isWifiEnabled() && null != info && (null != info.getBSSID())) {
			mNetSettingViewHolder.mWiFiNetState.setVisibility(View.VISIBLE);
			mWiFiSetting.showWiFiState();
		} else {
			mNetSettingViewHolder.mWireNetState.setVisibility(View.VISIBLE);
			mWireSetting.showWireState();
		}
	}

	// private IntentFilter mFilter;

	/**
	 * 控件的初始化
	 */
	private void findViews() {
		mNetSettingViewHolder = new NetSettingViewHolder(this);
		mWiFiSetting = new WiFiSetting(mNetSettingViewHolder, this);
		mWifiManager = mWiFiSetting.getWifiManager();
		mWireSetting = new WireSetting(mNetSettingViewHolder, this);
//		mPppoeSetting = new PPPOESetting(mNetSettingViewHolder, this); // change by cwj
		mWifiDirect = new WifiDirectSetting(mNetSettingViewHolder, this);
		mWifiApSet = new WifiApSetting(mNetSettingViewHolder, this);
		mLocalSetting = new LocationSetting(mNetSettingViewHolder, this);

		initCurrentNetState();
	}

	/**
	 * 监听器的注册
	 */
	private void registerListener() {
		mNetSettingListeners = new NetSettingListeners(this, mNetSettingViewHolder);

		/*
		 * mNetSettingViewHolder.mConnectFormat_right .setOnTouchListener(new View.OnTouchListener()
		 * {
		 * 
		 * @Override public boolean onTouch(View v, MotionEvent event) { if (currentConnectFormat <
		 * 1) { currentConnectFormat++; mNetSettingViewHolder.mConnectFormat
		 * .setText(state[currentConnectFormat]); wireHide(); pppoeHide(); wifiHide(); } else if
		 * (currentConnectFormat >= 1) { mNetSettingViewHolder.mConnectFormat
		 * .setText(state[currentConnectFormat]); wireHide(); pppoeHide(); wifiHide(); } return
		 * false; } });
		 * 
		 * mNetSettingViewHolder.mConnectFormat_left .setOnTouchListener(new View.OnTouchListener()
		 * {
		 * 
		 * @Override public boolean onTouch(View v, MotionEvent event) { if (currentConnectFormat >
		 * 0) { currentConnectFormat--; mNetSettingViewHolder.mConnectFormat
		 * .setText(state[currentConnectFormat]); wireHide(); pppoeHide(); wifiHide(); } else if
		 * (currentConnectFormat <= 0) { mNetSettingViewHolder.mConnectFormat
		 * .setText(state[currentConnectFormat]); wireHide(); pppoeHide(); wifiHide(); } return
		 * false; } });
		 */
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (!focusIsRight && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			switch (isSelected) {
			case Constants.NET_STATE:
				/* 网络状态只允许查看 2012-03-06 zhanghs */
				// [2012-3-30yanhd] 只显示当前连接
				/*
				 * mNetSettingViewHolder.mConnectFormatRelativeLayout
				 * .setBackgroundResource(R.drawable.desktop_button); FocusChange.mFocusLocal =
				 * Constants.CONNECT_FORMAT; mNetSettingViewHolder.mConnectFormat.requestFocus();
				 * focusIsRight = true;
				 */
				return true;
			case Constants.WIRE_SETTING:
				// delete pppoe 2012-05-23
				/*
				 * if (mPppoeSetting.isPppoeConnected()){
				 * Toast.makeText(this,getString(R.string.please_hangup_pppoe
				 * ),Toast.LENGTH_LONG).show(); return true; }
				 */

				if (mWifiManager.isWifiEnabled()) {
					Toast.makeText(this, getString(R.string.please_switch_wire), Toast.LENGTH_LONG)
							.show();
					return true;
				}

				mNetSettingViewHolder.mGetIpRelativeLayout
						.setBackgroundResource(R.drawable.desktop_button);
				FocusChange.mFocusLocal = Constants.IS_AUTO_IP_ADDRESS;
				mNetSettingViewHolder.mAutoIpAddress.requestFocus();
				focusIsRight = true;
				break;
			case Constants.WIRELESS_SETTING:

				FocusChange.mFocusLocal = Constants.WIFI_SWITCH;
				mNetSettingViewHolder.mWifiSwitchCb.requestFocus();
				focusIsRight = true;
				break;
			case Constants.PPPOE_SETTING:
				mNetSettingViewHolder.mPPPOEAutoIPRelativeLayout
						.setBackgroundResource(R.drawable.desktop_button);
				FocusChange.mFocusLocal = Constants.PPPOE_AUTO_IP;
				mNetSettingViewHolder.mPPPOEConnetFormat.requestFocus();
				focusIsRight = true;
				return true;
			case Constants.WIFI_DIRECT:
				mNetSettingViewHolder.mdirectCheckbox.requestFocus();
				FocusChange.mFocusLocal = Constants.IS_WIFI_DIRECT_OPEN;
				focusIsRight = true;
				return true;
			case Constants.WIFI_HOTSPOT:
				mNetSettingViewHolder.mWifiApSwitchCb.requestFocus();
				FocusChange.mFocusLocal = Constants.WIFI_AP_SWITCH;
				focusIsRight = true;
				return true;
			case Constants.LOCALTON_SERVICE:
				mNetSettingViewHolder.mLocServiceSwitch.requestFocus();
				FocusChange.mFocusLocal = Constants.LOCATION_SERVICE_SW;
				focusIsRight = true;
				return true;
			}
		}

		if (!focusIsRight && keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			View view = getCurrentFocus();
			if (null != view) {
				view.clearFocus();
			}

			if (Constants.WIFI_HOTSPOT == isSelected) {
				mNetSettingViewHolder.mNetSetting.setSelection(0);
				isSelected = Constants.NET_STATE;
			} else if (isSelected < Constants.WIFI_HOTSPOT) {
				mNetSettingViewHolder.mNetSetting.setSelection(++isSelected);
			}
			return true;
		}

		if (!focusIsRight && keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			View view = getCurrentFocus();
			if (null != view) {
				view.clearFocus();
			}

			if (Constants.NET_STATE == isSelected) {
				mNetSettingViewHolder.mNetSetting.setSelection(Constants.LIST_ITEMS - 1);
				isSelected = Constants.WIFI_HOTSPOT;
			} else if (isSelected > Constants.NET_STATE) {
				mNetSettingViewHolder.mNetSetting.setSelection(--isSelected);
			}
			return true;
		}

		// /action down
		if (focusIsRight && keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			switch (isSelected) {
			case Constants.NET_STATE:
				Log.d(TAG, "isSelected KEYCODE_DPAD_DOWN =" + isSelected);
				return true;
			case Constants.WIRE_SETTING:
				// FocusChange.wireSettingDropDown(mNetSettingViewHolder, 1);
				if (isWireAutoIpAddress) {
					FocusChange.wireOrWirelessSettingDropDown(mNetSettingViewHolder, 1);
				} else {
					FocusChange.wireSettingDropDown(mNetSettingViewHolder, 1);
				}
				return true;

			case Constants.WIRELESS_SETTING:
				if (mNetSettingViewHolder.mSSIDRelativeLayout.isShown()) {
					// FocusChange.wireSettingDropDown(mNetSettingViewHolder, 2);
					if (isWirelessAutoIpAddress) {
						FocusChange.wireOrWirelessSettingDropDown(mNetSettingViewHolder, 2);
					} else {
						FocusChange.wireSettingDropDown(mNetSettingViewHolder, 2);
					}
				}
				return true;
			case Constants.PPPOE_SETTING:
				FocusChange.pppoeSettingDropDown(mNetSettingViewHolder);
				return true;
			case Constants.WIFI_HOTSPOT:
				FocusChange.wifiHotSpotDropDown(mNetSettingViewHolder);
				return true;
			}
		}

		// /action up
		if (focusIsRight && keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			switch (isSelected) {
			case Constants.NET_STATE:
				// [2012-2-21]note
				// FocusChange.netStateDrop(mNetSettingViewHolder);
				Log.d(TAG, "isSelected KEYCODE_DPAD_UP =" + isSelected);
				return true;
			case Constants.WIRE_SETTING:
				// FocusChange.wireSettingDropUp(mNetSettingViewHolder, 1);
				if (isWireAutoIpAddress) {
					FocusChange.wireOrWirelessSettingDropUp(mNetSettingViewHolder, 1);
				} else {
					FocusChange.wireSettingDropUp(mNetSettingViewHolder, 1);
				}
				return true;
			case Constants.WIRELESS_SETTING:
				// FocusChange.wireSettingDropUp(mNetSettingViewHolder, 2);
				if (isWirelessAutoIpAddress) {
					FocusChange.wireOrWirelessSettingDropUp(mNetSettingViewHolder, 2);
				} else {
					FocusChange.wireSettingDropUp(mNetSettingViewHolder, 2);
				}
				return true;
			case Constants.PPPOE_SETTING:
				FocusChange.pppoeSettingDropUp(mNetSettingViewHolder);
				return true;
			case Constants.WIFI_HOTSPOT:
				FocusChange.wifiHotSpotDropUp(mNetSettingViewHolder);
				return true;
			}
		}
    if (focusIsRight &&  mNetSettingViewHolder.mIpSaveBtn.isFocused()){
			if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
				 atferSaveIpReturnToLeft();
			}
		}
		if (focusIsRight &&  mNetSettingViewHolder.mAutoIpAddress.isFocused()){
			if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
				atferSaveIpReturnToLeft();				
			}
		}
		if (isModifyConnectFormat) {
			/*
			 * if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			 * 
			 * if (currentConnectFormat < 1) { currentConnectFormat++;
			 * mNetSettingViewHolder.mConnectFormat .setText(state[currentConnectFormat]);
			 * wireHide(); pppoeHide(); wifiHide(); } else if (currentConnectFormat >= 1) {
			 * mNetSettingViewHolder.mConnectFormat .setText(state[currentConnectFormat]);
			 * wireHide(); pppoeHide(); wifiHide(); } } else if (keyCode ==
			 * KeyEvent.KEYCODE_DPAD_LEFT) { if (currentConnectFormat > 0) { currentConnectFormat--;
			 * mNetSettingViewHolder.mConnectFormat .setText(state[currentConnectFormat]);
			 * wireHide(); pppoeHide(); wifiHide(); } else if (currentConnectFormat <= 0) {
			 * mNetSettingViewHolder.mConnectFormat .setText(state[currentConnectFormat]);
			 * wireHide(); pppoeHide(); wifiHide(); } }
			 */
			// 2012-03-24
			if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_LEFT
					|| keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
				if (currentConnectFormat == 0) {
					mNetSettingViewHolder.mConnectFormat.setText(state[++currentConnectFormat]);
					wireHide();
					// delete pppoe 2012-05-23
					// pppoeHide();
					wifiHide();
				} else if (currentConnectFormat == 1) {
					mNetSettingViewHolder.mConnectFormat.setText(state[--currentConnectFormat]);
					wireHide();
					// delete pppoe 2012-05-23
					// pppoeHide();
					wifiHide();
				}
			}
			return true;
		}

		if (mNetSettingViewHolder.mWifiApSecButton.isFocused()) {
			if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
				mWifiApSet.secureOnKeyRight();
				return true;
			} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
				mWifiApSet.secureOnKeyLeft();
				return true;
			}
		}

		if (mNetSettingViewHolder.mDialerActionBtn.isFocused()) {
			if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
				return true;
			}
		}

		if ((keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)
				&& mNetSettingViewHolder.mWifiApSSID.isFocused()) {
			int start = mNetSettingViewHolder.mWifiApSSID.getSelectionStart();
			int end = mNetSettingViewHolder.mWifiApSSID.getSelectionEnd();
			if (mNetSettingViewHolder.mWifiApSSID.length() == end || 0 == start) {
				Log.d(TAG, "start=" + start + " end = " + end);
				return true;
			}
		}

		if ((keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)
				&& mNetSettingViewHolder.mWifiApPwd.isFocused()) {
			int start = mNetSettingViewHolder.mWifiApPwd.getSelectionStart();
			int end = mNetSettingViewHolder.mWifiApPwd.getSelectionEnd();
			if (mNetSettingViewHolder.mWifiApPwd.length() == end || 0 == start) {
				Log.d(TAG, "start=" + start + " end = " + end);
				return true;
			}
		}

		if ((keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)
				&& mNetSettingViewHolder.mUsernameEditText.isFocused()) {
			int start = mNetSettingViewHolder.mUsernameEditText.getSelectionStart();
			int end = mNetSettingViewHolder.mUsernameEditText.getSelectionEnd();
			if (mNetSettingViewHolder.mUsernameEditText.length() == end || 0 == start) {
				Log.d(TAG, "start=" + start + " end = " + end);
				return true;
			}
		}

		if ((keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)
				&& mNetSettingViewHolder.mPassWordEditText.isFocused()) {
			int start = mNetSettingViewHolder.mPassWordEditText.getSelectionStart();
			int end = mNetSettingViewHolder.mPassWordEditText.getSelectionEnd();
			if (mNetSettingViewHolder.mPassWordEditText.length() == end || 0 == start) {
				Log.d(TAG, "start=" + start + " end = " + end);
				return true;
			}
		}

		if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
				&& mNetSettingViewHolder.mPPPOEConnetFormat.isFocused()) {
			return true;
		}

		Log.d(TAG, "default onkey");

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {

		mPreKeyCode = keyCode;// [2012-1-14]

		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {

			if (mNetSettingViewHolder.mNetSetting.hasFocus()) {
				setBackground(R.drawable.one_px);
				focusIsRight = false;
				// /restore save button backgroung
				mNetSettingViewHolder.mIpSaveBtn.setBackgroundResource(R.drawable.edit_normal);
			}
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (focusIsRight) {
				// 2012-03-24 add
				// if (isModifyConnectFormat) {
				// mNetSettingViewHolder.mConnectFormat_left.setVisibility(View.INVISIBLE);
				// mNetSettingViewHolder.mConnectFormat_right.setVisibility(View.INVISIBLE);
				// isModifyConnectFormat = false;
				// }

				if (!isModifyConnectFormat) {
					// [2012-3-28 yanhd]update
					View view = getCurrentFocus();
					if (null != view) {
						view.clearFocus();
					}
					mNetSettingViewHolder.mNetSetting.setSelection(isSelected);
					setBackground(R.drawable.one_px);
					// /restore save button backgroung
					mNetSettingViewHolder.mIpSaveBtn.setBackgroundResource(R.drawable.edit_normal);
					focusIsRight = false;
				}
			} else {
				finish();
			}
		}
		return true;
	}

	/*
	 * [2012-1-16 add] 若有ip，等更新则进行保存。
	 */
	public boolean autoSaveNetInput() {
		switch (isSelected) {			
		case 0: {
			break;
		}
		case 1: {
			return mWireSetting.handle_saveconf(isWireAutoIpAddress);// save config
			// break;
		}
		case 2: {
			return mWiFiSetting.handleWifiConnect(isWirelessAutoIpAddress); // save config
			// break;
		}
		case 3: {
			break;
		}
		default:
			break;
		}
		return false;
	}

	/**
	 * 判断PPPOE拨号是否自动隐藏
	 */
	private void pppoeHide() {
		if (currentConnectFormat == 2) {
			mNetSettingViewHolder.mPPPOENetState.setVisibility(View.VISIBLE);
			mNetSettingViewHolder.isDialing.setText(getString(R.string.pppoe_disconnect));
		} else {
			mNetSettingViewHolder.mPPPOENetState.setVisibility(View.GONE);
		}
	}

	/**
	 * whether the wifi is gone
	 */
	private void wifiHide() {
		if (currentConnectFormat == 1) {
			mNetSettingViewHolder.mWiFiNetState.setVisibility(View.VISIBLE);
			// mWiFiSetting.checkWiFiState();
			mWiFiSetting.showWiFiState();
		} else {
			mNetSettingViewHolder.mWiFiNetState.setVisibility(View.GONE);
		}
	}

	/**
	 * 判断有线连接是否隐藏
	 */
	private void wireHide() {
		if (currentConnectFormat == 0) {
			mNetSettingViewHolder.mWireNetState.setVisibility(View.VISIBLE);
			// mWireSetting.setStateValue();
			mWireSetting.showWireState();
		} else {
			mNetSettingViewHolder.mWireNetState.setVisibility(View.GONE);
		}
	}

	/**
	 * 当焦点从右边移动到左边时，设置右边布局的背景色
	 * 
	 * @param id
	 */
	private void setBackground(int id) {
		mNetSettingViewHolder.mPPPOEAutoIPRelativeLayout.setBackgroundResource(id);
		mNetSettingViewHolder.mPPPOEUsernameRelativeLayout.setBackgroundResource(id);
		mNetSettingViewHolder.mPPPOEPasswordRelativeLayout.setBackgroundResource(id);
		mNetSettingViewHolder.mPasswordShowRelativeLayout.setBackgroundResource(id);
		// /mNetSettingViewHolder.mAutoDialerRelativeLayout
		// .setBackgroundResource(id);
		mNetSettingViewHolder.mConnectFormatRelativeLayout.setBackgroundResource(id);

		mNetSettingViewHolder.mSSIDRelativeLayout.setBackgroundResource(id);
		mNetSettingViewHolder.mGetIpRelativeLayout.setBackgroundResource(id);
		mNetSettingViewHolder.mIpAddressRelativeLayout.setBackgroundResource(id);
		mNetSettingViewHolder.mSubnetRelativeLayout.setBackgroundResource(id);
		mNetSettingViewHolder.mDefaultGatewayRelativeLayout.setBackgroundResource(id);
		mNetSettingViewHolder.mFirstDnsRelativeLayout.setBackgroundResource(id);
		mNetSettingViewHolder.mSecondDnsRelativeLayout.setBackgroundResource(id);
		mNetSettingViewHolder.mdirectLlayout.setBackgroundResource(id);
		mNetSettingViewHolder.mWifiApSwitchLy.setBackgroundResource(id);
	}

	// /[2012-2-23add] is wifi useed
	public boolean isWifiUsed() {
		if (mWifiManager.isWifiEnabled() || mWifiDirect.isWifiDirectEnable()) {
			return true;
		}
		return false;
	}

	// [2012-2-9add] check the "wlan0 ,wlan1, eth0, eth0" is plug
	public boolean isNetInterfaceAvailable(String netif) {
		String netInterfaceStatusFile = "/sys/class/net/" + netif + "/carrier";
		boolean bStatus = isStatusAvailable(netInterfaceStatusFile);
		return bStatus;
	}

	// [2012-2-9add]
	private boolean isStatusAvailable(String statusFile) {
		char st = readStatus(statusFile);
		if (st == '1') {
			return true;
		}
		return false;
	}

	// [2012-2-9add]
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return (char) tempChar;
	}

	// 2012-03-26 add
	public void atferSaveIpReturnToLeft() {
		View view = getCurrentFocus();
//		 when is mouse opetion and getCurrentFocus == null
		if (null != view) {
			view.clearFocus();
		}
		mNetSettingViewHolder.mNetSetting.setFocusable(true);
		mNetSettingViewHolder.mNetSetting.requestFocus();
		mNetSettingViewHolder.mNetSetting.setFocusableInTouchMode(true);
		mNetSettingViewHolder.mNetSetting.setSelection(isSelected);
		setBackground(R.drawable.one_px);
		mNetSettingViewHolder.mIpSaveBtn.setBackgroundResource(R.drawable.edit_text);
		focusIsRight = false;
	}

	private void updateWifiState(int state) {

//		 Log.d("ducj", "state++" + state);
		
//		 switch (state) {
//		 case WifiManager.WIFI_STATE_ENABLING:
//		 mHandler.sendEmptyMessage(WIFI_OPENED);
//		 break;
//		 case WifiManager.WIFI_STATE_ENABLED:
//		 if (mProgressDialog != null && mProgressDialog.isShowing()) {
//		 mProgressDialog.dismiss();
//		 }
//		 break;
//		 case WifiManager.WIFI_STATE_DISABLING:
//		 mHandler.sendEmptyMessage(WIFI_CLOSED);
//		 break;
//		 case WifiManager.WIFI_STATE_DISABLED:
//		 if (mProgressDialog != null && mProgressDialog.isShowing()) {
//		 mProgressDialog.dismiss();
//		 }
//		 break;
//		 case WifiManager.WIFI_STATE_UNKNOWN:
//		 if (mProgressDialog != null && mProgressDialog.isShowing()) {
//		 mProgressDialog.dismiss();
//		 }
//		 break;
//		 default:
//		 break;
//		 }
	}

	/**
	 * show dialog in order to indicate wifi is opening or closing
	 * 
	 * @param
	 */
	private void showProgressDialog(int id) {
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage(getResources().getString(id));
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
	}
}
