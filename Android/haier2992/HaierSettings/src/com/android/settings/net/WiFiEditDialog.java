package com.android.settings.net;

import android.app.Dialog;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.LinkProperties;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.IpAssignment;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiConfiguration.ProxySettings;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.settings.R;

/**
 * modify password
 * 
 * @author ducj(ducj@biaoqi.com.cn)
 * @since 1.0 2011-12-2
 */
public class WiFiEditDialog extends Dialog {

	private final static String TAG = "net";

	private static final String KEYSTORE_SPACE = "keystore://";

	protected static final int MSG_CHECK_WIFI_IS_CONNECTED = 1001;

	private WiFiSignalListActivity mWiFiSignalListActivity;

	private Handler mHandler;

	// 编辑保存按钮.
	private Button save_btn;
	// 取消保存按钮.
	private Button cancel_btn;
	// 网络名称编辑框.
	private TextView net_name_et;
	// 密码编辑框.
	private EditText code_et;
	// 是否显示密码.
	private CheckBox code_show_cb;
	// 密码编辑布局.
	private RelativeLayout wifi_edit_r2;
	// 密码是否显示布局.
	private RelativeLayout wifi_edit_r3;
	// 提示是否需输入密码
	private TextView mNoticePwd;// [2012-2-11add]
	// /wps layout
	private RelativeLayout mWpsSetLy;
	// /wps switch checkbox
	private CheckBox mWpsSwitchCb;

	private ScanResult mResult;

	// private WifiConfiguration mConfiguration;
	private WifiManager wifiManager;
	
	private WifiManager.WpsListener mWpsListener;
	private int security;
	// ssid
	private String ssid;
	// password
	private String password;

	Configuration config;

	// /[2012-1-19 add]
	// public IpAssignment mIpAssignment = IpAssignment.UNASSIGNED;
	// public ProxySettings mProxySettings = ProxySettings.UNASSIGNED;
	public LinkProperties mLinkProperties = new LinkProperties();

	public WiFiEditDialog(WiFiSignalListActivity wifiSignalListActivity, ScanResult result,
			Handler handler) {
		super(wifiSignalListActivity);
		this.mWiFiSignalListActivity = wifiSignalListActivity;
		this.mResult = result;
		this.mHandler = handler;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifi_edit);

		config = mWiFiSignalListActivity.getResources().getConfiguration();

		wifiManager = mWiFiSignalListActivity.mWifiManager;
		try {
			security = getSecurity(mResult);
		} catch (Exception e) {
			security = Constants.SECURITY_NONE;
		}

		// 获取组件.
		findViews();
		// 获取组件监听.
		registerListener();
	}

	/*
	 * 初始化组件.
	 */
	private void findViews() {

		// save_btn = (Button) findViewById(R.id.save_net_eidt_ok);
		if (config.locale.toString().equals("en_US")) {
			save_btn = (Button) findViewById(R.id.save_net_eidt_ok_en);
			save_btn.setVisibility(View.VISIBLE);
		} else {
			save_btn = (Button) findViewById(R.id.save_net_eidt_ok);
			save_btn.setVisibility(View.VISIBLE);
		}

		cancel_btn = (Button) findViewById(R.id.save_net_edit_cancel);

		net_name_et = (TextView) findViewById(R.id.net_id_et);
		net_name_et.setText(mResult.SSID);
		code_et = (EditText) findViewById(R.id.password_input);
		if (mResult != null) {
			if (security == Constants.SECURITY_WEP) {
				code_et.setHint(R.string.modify_wep);
			} else if (security == Constants.SECURITY_PSK) {
				code_et.setHint(R.string.modify_wpa);
			} else if (security == Constants.SECURITY_EAP) {
				code_et.setHint(R.string.modify);
			}
		}

		// code_show_cb = (CheckBox) findViewById(R.id.code_show_pass_cb);
		if (config.locale.toString().equals("en_US")) {
			code_show_cb = (CheckBox) findViewById(R.id.code_show_pass_cb_en);
			code_show_cb.setVisibility(View.VISIBLE);
		} else {
			code_show_cb = (CheckBox) findViewById(R.id.code_show_pass_cb);
			code_show_cb.setVisibility(View.VISIBLE);
		}

		wifi_edit_r2 = (RelativeLayout) findViewById(R.id.wifi_edit_r2);
		wifi_edit_r3 = (RelativeLayout) findViewById(R.id.wifi_edit_r3);

		// [2012-2-11add]
		mNoticePwd = (TextView) findViewById(R.id.notice_no_password);

		// /wps layout
		mWpsSetLy = (RelativeLayout) findViewById(R.id.wifi_wps_ly);

		// /wps switch checkbox
		// mWpsSwitchCb = (CheckBox) findViewById(R.id.wps_switch);
		if (config.locale.toString().equals("en_US")) {
			mWpsSwitchCb = (CheckBox) findViewById(R.id.wps_switch_en);
			mWpsSwitchCb.setVisibility(View.VISIBLE);
		} else {
			mWpsSwitchCb = (CheckBox) findViewById(R.id.wps_switch);
			mWpsSwitchCb.setVisibility(View.VISIBLE);
		}

		// 实例化新的窗口
		Window w = getWindow();
		// 获取默认显示数据
		Display display = w.getWindowManager().getDefaultDisplay();
		// 获取窗口的背景图片
		Resources resources = mWiFiSignalListActivity.getResources();
		Drawable drawable = resources.getDrawable(R.drawable.dialog_bg);
		// // 设置窗口的背景图片
		w.setBackgroundDrawable(drawable);
		// 窗口的标题为空
		w.setTitle(null);
		// 设置窗口的显示位置
		w.setGravity(Gravity.CENTER);
		// 设置窗口的属性
		WindowManager.LayoutParams wl = w.getAttributes();
		w.setAttributes(wl);
		int height;
		if (Constants.SECURITY_NONE == security) {
			mNoticePwd.setVisibility(View.VISIBLE);
			wifi_edit_r2.setVisibility(View.GONE);
			wifi_edit_r3.setVisibility(View.GONE);
			mWpsSetLy.setVisibility(View.GONE);
			save_btn.requestFocus();
			height = (int) (display.getHeight() * 0.4);
		} else {
			mNoticePwd.setVisibility(View.GONE);
			if (!isContainWps(mResult)) {
				mWpsSetLy.setVisibility(View.GONE);
			}
			code_et.requestFocus();
			
			height = (int) (display.getHeight() * 0.6);
		}

		// /[2012-1-17 修改dialog宽高]
		int width = (int) (display.getWidth() * 0.5);
    

		// 设置窗口的大小
		w.setLayout(width, height);
	}

	/*
	 * 设置组件监听.
	 */
	private void registerListener() {

		// 保存按钮监听.
		save_btn.setOnClickListener(new Button.OnClickListener() {

			@SuppressWarnings({ "deprecation", "static-access" })
			@Override
			public void onClick(View v) {

				// WPS connect
				if (mWpsSwitchCb.isChecked()) {

					String wifiDeviceName = SystemProperties.get("wlan.driver", null);
					Log.d(TAG, "====>wifiDeviceName:" + wifiDeviceName);
					if (!wifiDeviceName.equals("RALINK")) {
						// if(wifiManager.isWifiDeviceSupportWps()){
						Log.d(TAG, "====>startWps");
						// manager.startPbc();
						wifiManager.startWps(getWpsConfig(), mWpsListener);      // change by cwj
						Toast.makeText(mWiFiSignalListActivity, R.string.wps_has_start,
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(mWiFiSignalListActivity,
								R.string.wifi_device_not_support_wps, Toast.LENGTH_SHORT).show();
					}
				} else {
					ssid = net_name_et.getText().toString();
					password = code_et.getText().toString().trim();

					Log.d(TAG, "WiFiEditDialog.registerListener().onClick.mResult:" + mResult.SSID);

					/* || mConfiguration.networkId == -1 */
					String errorString = "";
					if (mResult == null) {
						errorString = mWiFiSignalListActivity.getString(R.string.ssid_error);
					}
					if (security == Constants.SECURITY_WEP && password.length() < 5) {
						if (errorString != null && !"".equals(errorString)) {
							errorString += "\n"
									+ mWiFiSignalListActivity
											.getString(R.string.password_null_error_wep);
						} else {
							errorString = mWiFiSignalListActivity
									.getString(R.string.password_null_error_wep);
						}
						code_et.setText("");
						code_et.setHint(R.string.modify_wep);
					}
					if (security == Constants.SECURITY_PSK && password.length() < 8) {
						if (errorString != null && !"".equals(errorString)) {
							errorString += "\n"
									+ mWiFiSignalListActivity
											.getString(R.string.password_null_error_psk);
						} else {
							errorString = mWiFiSignalListActivity
									.getString(R.string.password_null_error_psk);
						}
						code_et.setText("");
						code_et.setHint(R.string.modify_wpa);
					}
					if (security == Constants.SECURITY_PSK && password.length() > 64) {
						if (errorString != null && !"".equals(errorString)) {
							errorString += "\n"
									+ mWiFiSignalListActivity
											.getString(R.string.password_length_error_psk);
						} else {
							errorString = mWiFiSignalListActivity
									.getString(R.string.password_length_error_psk);
						}
						code_et.setText("");
						code_et.setHint(R.string.modify_wpa);
					}
					if (errorString != null && !"".equals(errorString)) {
						// Toast.makeText(mWiFiSignalListActivity, errorString, Toast.LENGTH_SHORT)
						// .show();
						code_et.requestFocus();
						save_btn.clearFocus();
						return;
					}

					// [2012-1-19 修改 wifi 加入网络功能]
					final WifiConfiguration mConfig = getConfig();

					if (mConfig != null) {

						mWiFiSignalListActivity.isAuth = false;
						mWiFiSignalListActivity.isConnected = false;

						wifiManager.disconnect();
						wifiManager.connect(mConfig, new WifiManager.ActionListener() {
							public void onSuccess() {
							}
							public void onFailure(int reason) {                   // change by wu  连接网络
								//TODO: Add failure UI
							}
						});
						mWiFiSignalListActivity.getConfig(mConfig);
						mWiFiSignalListActivity.mProgressDialog.show();

						if (mWiFiSignalListActivity.getSecurity(mConfig) == Constants.SECURITY_NONE) {
							mHandler.sendEmptyMessageDelayed(
									mWiFiSignalListActivity.SECURE_NONE_CONNECT, 15000);
						} else {
							mHandler.sendEmptyMessageDelayed(
									mWiFiSignalListActivity.SECURE_CONNECT, 15000);
						}
					}
					dismiss();
				}

				if (wifiManager.isWifiEnabled()) {
					Log.d(TAG, "startScanActive");
					wifiManager.startScanActive();
				}
			}
		});

		// /wifi wps switch
		mWpsSwitchCb.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					mWpsSetLy.setBackgroundResource(R.drawable.desktop_button);
				} else {
					mWpsSetLy.setBackgroundResource(R.drawable.one_px);
				}
			}
		});

		// /wifi wps switch click l
		mWpsSwitchCb.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckBox ch = (CheckBox) v;
				Log.d(TAG, "isChecked=" + ch.isChecked());
				Window w = getWindow();
				Display display = w.getWindowManager().getDefaultDisplay();
				int height;
				if (ch.isChecked()) {

					if (config.locale.toString().equals("en_US")) {
						save_btn.setNextFocusUpId(R.id.code_show_pass_cb_en);
					} else {
						save_btn.setNextFocusUpId(R.id.code_show_pass_cb);
					}
					save_btn.requestFocus();
					wifi_edit_r2.setVisibility(View.GONE);
					wifi_edit_r3.setVisibility(View.GONE);
					height = (int) (display.getHeight() * 0.5);
				} else {
					wifi_edit_r2.setVisibility(View.VISIBLE);
					wifi_edit_r3.setVisibility(View.VISIBLE);
					height = (int) (display.getHeight() * 0.6);
				}
				int width = (int) (display.getWidth() * 0.5);
				w.setLayout(width, height);
			}
		});

		// 取消保存按钮监听.
		cancel_btn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		// /onclick
		code_show_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					code_et.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
				} else {
					code_et.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);
				}
			}
		});

		// 是否显示密码check_box布局焦点监听.
		code_show_cb.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					wifi_edit_r3.setBackgroundResource(R.drawable.desktop_button);
				} else {
					wifi_edit_r3.setBackgroundResource(R.drawable.one_px);
				}
			}
		});

		// 密码编辑布局焦点监听.
		code_et.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					wifi_edit_r2.setBackgroundResource(R.drawable.desktop_button);
				} else {
					wifi_edit_r2.setBackgroundResource(R.drawable.one_px);
				}
			}
		});

		// 保存按钮焦点监听.
		save_btn.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					save_btn.setBackgroundResource(R.drawable.left_bg);
				} else {
					save_btn.setBackgroundResource(R.drawable.one_px);
				}
			}
		});

		// 取消保存按钮焦点监听.
		cancel_btn.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					cancel_btn.setBackgroundResource(R.drawable.left_bg);
				} else {
					cancel_btn.setBackgroundResource(R.drawable.one_px);
				}
			}
		});
	}

	private int getSecurity(ScanResult result) {
		return mWiFiSignalListActivity.checkCurrentWiFi(result);
	}

	// /check the dev contain WPS
	private boolean isContainWps(ScanResult result) {
		try {
			Log.e(TAG, "WiFiEditDialog.isContainWps.result.capabilities:" + result.capabilities);
			if (result != null && result.capabilities != null
					&& result.capabilities.contains("WPS")) {
				return true;
			}
		} catch (Exception e) {
			Log.e(TAG,
					"WiFiEditDialog.isContainWps.e.getLocalizedMessage():"
							+ e.getLocalizedMessage());
			return false;
		}

		return false;
	}

	// /[2012-1-19 update]
	WifiConfiguration getConfig() {

		if (null == mResult/* && mConfiguration.networkId != -1 */) {
			Log.e(TAG, "==>NULL");
			return null;
		}

		WifiConfiguration config = new WifiConfiguration();
		if (mResult == null) {
			config.SSID = convertToQuotedString(net_name_et.getText().toString());
			// If the user adds a network manually, assume that it is hidden.
			config.hiddenSSID = true;
		} else {
			config.SSID = convertToQuotedString(mResult.SSID);
		}

		switch (security) {
		case Constants.SECURITY_NONE:
			config.allowedKeyManagement.set(KeyMgmt.NONE);
			break;

		case Constants.SECURITY_WEP:
			config.allowedKeyManagement.set(KeyMgmt.NONE);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
			if (password.length() != 0) {
				int length = password.length();
				// WEP-40, WEP-104, and 256-bit WEP (WEP-232?)
				if ((length == 10 || length == 26 || length == 58)
						&& password.matches("[0-9A-Fa-f]*")) {
					config.wepKeys[0] = password;
				} else {
					config.wepKeys[0] = '"' + password + '"';
				}
			}
			break;

		case Constants.SECURITY_PSK:
			config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
			if (password.length() != 0) {
				if (password.matches("[0-9A-Fa-f]{64}")) {
					config.preSharedKey = password;
				} else {
					config.preSharedKey = '"' + password + '"';
				}
			}
			break;
		default:
			return null;
		}

		// [2012-1-19 add]
		config.proxySettings = ProxySettings.NONE;
		config.ipAssignment = IpAssignment.DHCP;
		config.linkProperties = new LinkProperties(mLinkProperties);

		return config;
	}

	String convertToQuotedString(String string) {
		return "\"" + string + "\"";
	}

	WpsInfo getWpsConfig() {

		WpsInfo config = new WpsInfo();
		/*
		 * switch (mNetworkSetupSpinner.getSelectedItemPosition()) { case WPS_PBC: config.setup =
		 * WpsInfo.PBC; break; case WPS_KEYPAD: config.setup = WpsInfo.KEYPAD; break; case
		 * WPS_DISPLAY: config.setup = WpsInfo.DISPLAY; break; default: config.setup =
		 * WpsInfo.INVALID; Log.e(TAG, "WPS not selected type"); return config; }
		 */
		config.setup = WpsInfo.PBC;
		Log.d(TAG, "mResult.BSSID = " + mResult.BSSID);
		config.BSSID = (mResult != null) ? mResult.BSSID : null;
		//config.proxySettings = ProxySettings.NONE;         // change by cwj
		//config.ipAssignment = IpAssignment.DHCP;           // change by cwj
		mLinkProperties.clear();
		//config.linkProperties = new LinkProperties(mLinkProperties);  // change by cwj
		return config;
	}
}
