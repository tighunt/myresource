package com.android.settings.net;

import static android.net.wifi.WifiConfiguration.INVALID_NETWORK_ID;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiSsid; // change by wu
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.android.internal.util.AsyncChannel;
import com.android.settings.R;

/**
 * wifi signal list
 * 
 * @author ducj(ducj@biaoqi.com.cn)
 * @date 2011-11-7 下午10:42:15
 * @since 1.0
 */
public class WiFiSignalListActivity extends Activity {

	private final static String TAG = "WiFiSignalListActivity";
	private List<ScanResult> results;
	private ListView wifiList;
	private WiFiSignalListAdapter mAdapter;
	public ScanResult result;

	static final int FINISH = 0;
	static final int SECURE_CONNECT = 1;
	static final int SECURE_NONE_CONNECT = 2;
	static final int ADD = 3;
	static final int RECEIVER = 4;

	private static final int FAILURE = 5;
	private static final int IN_PROGRESS = 6;

	/**
	 * These values are matched in string arrays -- changes must be kept in sync
	 */
	private TVOSGHotKeyDialog tvosgHotKeyDialog;

	public WifiManager mWifiManager;;

	// configed wifi id
	int mCurrentNetworkId;

	// [2012-2-2 add]
	String selectedSSID;

	private IntentFilter mFilter;
	private BroadcastReceiver mReceiver;

	private Scanner mScanner;

	boolean isAuth;
	boolean isConnected = false;

	private AtomicBoolean mConnected = new AtomicBoolean(false);
	// private boolean mIsPppoeConnected ;//[2012-2-17]

	List<WifiConfiguration> wificonfigurations;

	// Combo scans can take 5-6s to complete - set to 10s.
	private static final int WIFI_RESCAN_INTERVAL_MS = 10 * 1000;

	protected static final int MSG_CHECK_WIFI_IS_CONNECTED = 1001;

	private WifiConfiguration mConfig;

	private int mSecore;
	private String mflag = null;

	private final static int SECURE_WEP = 1;
	private final static int SECURE_WPA = 2;

	ProgressDialog mProgressDialog;

	Dialog dialog;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			Log.d(TAG, "WiFiSignalListActivity.mHandler.msg.what:" + msg.what);
			Log.d(TAG, "WiFiSignalListActivity.mHandler.mSecore:" + mSecore);
			switch (msg.what) {
			case ADD:
				if (mSecore == SECURE_WPA) {
					this.postDelayed(addRunnable, 5 * 1000);
					break;
				} else {
					this.postDelayed(addRunnable, 0);
					break;
				}
			case SECURE_CONNECT:

				if (mProgressDialog != null && mProgressDialog.isShowing()
						&& !isConnected) {
					mProgressDialog.dismiss();
					Toast.makeText(WiFiSignalListActivity.this,
							R.string.wifi_disabled_password_failure,
							Toast.LENGTH_SHORT).show();
				}
				break;
			case SECURE_NONE_CONNECT:

				if (mProgressDialog != null && mProgressDialog.isShowing()
						&& !isConnected) {
					mProgressDialog.dismiss();
					Toast.makeText(WiFiSignalListActivity.this,
							R.string.wifi_saved, Toast.LENGTH_SHORT).show();
				}
				break;
			case FINISH:

				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putParcelable("ssid", result);
				intent.putExtras(bundle);
				WiFiSignalListActivity.this.setResult(RESULT_OK, intent);
				finish();
				break;
			case RECEIVER:// ,isConnected
				int indexFocu = wifiList.getSelectedItemPosition();
				Log.d(TAG, "WiFiSignalListActivity.mHandler.indexFocu:"
						+ indexFocu);
				mAdapter = new WiFiSignalListAdapter(
						WiFiSignalListActivity.this, results);
				wifiList.setDividerHeight(0);
				wifiList.setAdapter(mAdapter);
				if (indexFocu > (results.size() - 1)) {
					wifiList.setSelection(results.size() - 1);
				} else {
					wifiList.setSelection(indexFocu);
				}

				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		results = mWifiManager.getScanResults();

		setContentView(R.layout.wifi_list);

		mFilter = new IntentFilter();
		mFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		mFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		mFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
		mFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		mFilter.addAction(WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION);
		mFilter.addAction(WifiManager.LINK_CONFIGURATION_CHANGED_ACTION);
		mFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		mFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
		// mFilter.addAction(WifiManager.ERROR_ACTION);
		// mFilter.addAction(WifiManager.WIFI_DEVICE_REMOVED_ACTION); // change
		// by cwj

		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

				handleEvent(context, intent);
			}
		};

		mScanner = new Scanner();

		findViews();

		registerListeners();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// mWifiManager.asyncConnect(this, new WifiServiceHandler()); change by
		// cwj
		registerReceiver(mReceiver, mFilter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mScanner.pause();
		unregisterReceiver(mReceiver);
		finish();
	}

	/**
	 * component init , isConnected
	 */
	private void findViews() {

		showProgressDialog();
		wifiList = (ListView) findViewById(R.id.wifi_list_select);
		mAdapter = new WiFiSignalListAdapter(WiFiSignalListActivity.this,
				results);
		wifiList.setDividerHeight(0);
		wifiList.setAdapter(mAdapter);
	}

	// the evenvt
	private void handleEvent(Context context, Intent intent) {

		String action = intent.getAction();

		Log.d("alen", "action++++++++" + action);

		if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {

			updateWifiState(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
					WifiManager.WIFI_STATE_UNKNOWN));
		} else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)
				|| WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION
						.equals(action)
				|| WifiManager.LINK_CONFIGURATION_CHANGED_ACTION.equals(action)) {

			Log.d(TAG, "WiFiSignalListActivity.handleEvent.action:" + action);

			// [2012-2-2]获取信号列表
			updateSignalList("intent");
		} else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {

			DetailedState state = WifiInfo
					.getDetailedStateOf((SupplicantState) intent
							.getParcelableExtra(WifiManager.EXTRA_NEW_STATE));

			Log.d("alen", "WiFiSignalListActivity.handleEvent.state:" + state);

			if (!mConnected.get()) {

				Log.d("alen", "con not connect");
				updateConnectionState(state);
			} else {
				Log.d("alen", "con connect");
				if (mProgressDialog != null && mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
			}
		} else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {

			NetworkInfo info = (NetworkInfo) intent
					.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			mConnected.set(info.isConnected());

			DetailedState state = info.getDetailedState();

			Log.d("alen", "NETWORK_STATE_CHANGED_ACTION++" + state);

			if (state == DetailedState.CONNECTED) {
				isConnected = true;

				if (mProgressDialog != null && mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}

				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}

				if (tvosgHotKeyDialog != null
						&& tvosgHotKeyDialog.wiFiEditDialog != null
						&& tvosgHotKeyDialog.wiFiEditDialog.isShowing()) {
					tvosgHotKeyDialog.wiFiEditDialog.dismiss();
				}
			}

			updateConnectionState(state);
		} else if (WifiManager.RSSI_CHANGED_ACTION.equals(action)) {

			updateConnectionState(null);
		} /*
		 * else if (WifiManager.ERROR_ACTION.equals(action)) {
		 * 
		 * int errorCode = intent.getIntExtra(WifiManager.EXTRA_ERROR_CODE, 0);
		 * switch (errorCode) { case WifiManager.WPS_OVERLAP_ERROR: Log.e(TAG,
		 * "WiFiSignalListActivity.handleEvent.WPS WifiManager.WPS_OVERLAP_ERROR:"
		 * ); Toast.makeText(context, R.string.wifi_wps_overlap_error,
		 * Toast.LENGTH_SHORT).show(); break; } } else if
		 * (WifiManager.WIFI_DEVICE_REMOVED_ACTION.equals(action)) { // change
		 * by cwj
		 * 
		 * if (mProgressDialog != null && mProgressDialog.isShowing()) {
		 * mProgressDialog.dismiss(); } finish(); }
		 */
	}

	private void updateConnectionState(DetailedState state) {

		if (!mWifiManager.isWifiEnabled()) {
			mScanner.pause();
			return;
		}

		if (state == DetailedState.OBTAINING_IPADDR) {
			mScanner.pause();
		} else {
			mScanner.resume();
		}

		if (state == DetailedState.AUTHENTICATING) {
			isAuth = true;
		}

		if (isAuth && state == DetailedState.DISCONNECTED) {
			if (mProgressDialog != null && mProgressDialog.isShowing()) {
				isAuth = false;
				mProgressDialog.dismiss();
				refresh();
			}
		}
	}

	private void updateWifiState(int state) {

		switch (state) {
		case WifiManager.WIFI_STATE_ENABLED:
			mScanner.resume();
			return; // not break, to avoid the call to pause() below
		case WifiManager.WIFI_STATE_ENABLING:
			break;
		case WifiManager.WIFI_STATE_DISABLED:
			break;
		}
		mScanner.pause();
	}

	private class Scanner extends Handler {
		private int mRetry = 0;

		void resume() {
			if (!hasMessages(0)) {
				sendEmptyMessage(0);
			}
		}

		@SuppressWarnings("unused")
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
			Log.d(TAG,
					"WiFiSignalListActivity.Scanner.handleMessage.startScanActive......");
			if (mWifiManager.isWifiEnabled()) {
				if (mWifiManager.startScanActive()) {
					mRetry = 0;
				} else if (++mRetry >= 3) {
					mRetry = 0;
					return;
				}
			}

			sendEmptyMessageDelayed(0, WIFI_RESCAN_INTERVAL_MS);
		}
	}

	// [2012-1-31]wifi callback
	private class WifiServiceHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AsyncChannel.CMD_CHANNEL_HALF_CONNECTED:
				if (msg.arg1 == AsyncChannel.STATUS_SUCCESSFUL) {
					// AsyncChannel in msg.obj
				} else {
					// AsyncChannel set up failure, ignore
				}
				break;
			/*
			 * case WifiManager.CMD_WPS_COMPLETED: WpsResult result =
			 * (WpsResult) msg.obj; if (result == null) break; switch
			 * (result.status) { case FAILURE: dialog = onCreateDialog(FAILURE);
			 * // change by cwj dialog.show(); break; case IN_PROGRESS: dialog =
			 * onCreateDialog(IN_PROGRESS); dialog.show(); break; default:
			 * break; }
			 */
			}
		}
	}

	// /[2012-2-2 yanhd] 获取当前wifi 信号列表
	public void updateSignalList(String flag) {

		Log.e(TAG, "WiFiSignalListActivity.updateSignalList.flag:" + flag);

		// 已配置过的wifi
		List<WifiConfiguration> configLists = mWifiManager
				.getConfiguredNetworks();

		if ("forget".equals(flag) && configLists.size() == 0) {
			mWifiManager.setWifiEnabled(false);
			mWifiManager.setWifiEnabled(true);
		}

		// 扫描出的wifi
		results = mWifiManager.getScanResults();

		if (results == null) {
			Log.e(TAG,
					"WiFiSignalListActivity.updateSignalList.getScanResults() == NULL");
			results = new ArrayList<ScanResult>();
		}

		//add for list apeat by jinfu 2014.4.24
		if (results != null) {
			Set set = new HashSet();
			List newList = new ArrayList<ScanResult>();
			for (Iterator<ScanResult> iter = results.iterator(); iter.hasNext();) {
				Object element = iter.next();
				if (set.add(element))
					newList.add(element);
			}
			results.clear();
			results.addAll(newList);
		}

		for (WifiConfiguration wifiConfiguration : configLists) {

			Log.e("zjf",
					"WiFiSignalListActivity.updateSignalList.wifiConfiguration.SSID:"
							+ wifiConfiguration.SSID);

			if (wifiConfiguration.SSID == null
					|| "".equals(wifiConfiguration.SSID)) {
				continue;
			}

			if (!isExistInScanList(wifiConfiguration, results)) {

				// 去掉字串中的双引号 "mstar_15" ---> mstar_15
				// String ssid = wifiConfiguration.SSID.substring(1,
				// wifiConfiguration.SSID.lastIndexOf("\""));
				final int length = wifiConfiguration.SSID.length();
				WifiSsid wifiSsid = WifiSsid
						.createFromAsciiEncoded(wifiConfiguration.SSID
								.substring(2, length - 1));
				ScanResult result1 = new ScanResult(wifiSsid,
						wifiConfiguration.BSSID, null, -100, 0, // change by wu
						0);
				// ScanResult result = new ScanResult(ssid,
				// wifiConfiguration.BSSID, null, -100, 0);
				results.add(result);
			}
		}

		mHandler.sendEmptyMessage(RECEIVER);
	}

	// [2012-2-2 yanhd] 检查 当前 config 是否在 扫描的类别中存在
	public static boolean isExistInScanList(WifiConfiguration config,
			List<ScanResult> scanlists) {

		if (config == null || scanlists == null)
			return false;

		for (ScanResult scan : scanlists) {

			String scanSSID = "\"" + scan.SSID + "\"";
			int configSecurity = checkWifiConfigurationSecurity(config);
			int scanSecurity = checkScanResultSecurity(scan);
			if (scanSSID.equals(config.SSID) && configSecurity == scanSecurity) {
				return true;
			}
		}
		return false;
	}

	private static int checkScanResultSecurity(ScanResult result) {

		try {
			if (result != null && result.capabilities != null) {
				if (result.capabilities.contains("WEP")) {
					return Constants.SECURITY_WEP;
				} else if (result.capabilities.contains("PSK")) {
					return Constants.SECURITY_PSK;
				} else if (result.capabilities.contains("EAP")) {
					return Constants.SECURITY_EAP;
				}
			}
		} catch (Exception e) {
			Log.e(TAG,
					"WiFiSignalListActivity.checkScanResultSecurity.e.getLocalizedMessage():"
							+ e.getLocalizedMessage());
		}

		return Constants.SECURITY_NONE;
	}

	private static int checkWifiConfigurationSecurity(WifiConfiguration config) {
		if (config.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) {
			return Constants.SECURITY_PSK;
		} else if (config.allowedKeyManagement.get(KeyMgmt.WPA2_PSK)) {
			return Constants.SECURITY_PSK;
		} else if (config.allowedKeyManagement.get(KeyMgmt.WPA_EAP)) {
			return Constants.SECURITY_EAP;
		}
		return Constants.SECURITY_NONE;
	}

	/**
	 * listen the list item click
	 */
	private void registerListeners() {
		wifiList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				result = (ScanResult) mAdapter.getItem(position);

				tvosgHotKeyDialog = new TVOSGHotKeyDialog(
						WiFiSignalListActivity.this, R.style.mainMenu, result,
						mHandler);
				tvosgHotKeyDialog.show();
			}
		});
	}

	/**
	 * [2012-2-2 ] remove the configed information
	 * 
	 * @param networkId
	 */
	public void forget(int networkId) {
		Log.i(TAG, "forget the network");
		mWifiManager.forget(networkId, new WifiManager.ActionListener() {
			public void onSuccess() {
			}

			public void onFailure(int reason) {
				// TODO: Add failure UI // change by wu
			}
		});
		updateSignalList("forget");
	}

	// [2012-2-2 add] 将信号类表中 将指定SSID 删除
	public void removeFromScanList(String ssid, List<ScanResult> scanList) {
		Log.i(TAG, "WiFiSignalListActivity.removeFromScanList.ssid:" + ssid);
		if (null == ssid || null == scanList)
			return;
		for (ScanResult scan : scanList) {
			if (scan.SSID.equals(ssid)) {
				scanList.remove(scan);
				return;
			}
		}
	}

	// /[2012-2-2 yanhd]根据已配置的 networkId 连接网络。
	public boolean connectTheWifiWithID(int networkId) {
		if (INVALID_NETWORK_ID != networkId) {
			mWifiManager.connect(networkId, new WifiManager.ActionListener() {
				public void onSuccess() {
				}

				public void onFailure(int reason) { // change by wu
					// TODO: Add failure UI
				}
			});
			// mWifiManager.connectNetwork(networkId);
			return true;
		}
		return false;
	}

	public boolean isConfiged() {

		List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();
		if (null == configs)
			return false;

		String ssid = "\"" + result.SSID + "\"";
		int resultSecurity = checkScanResultSecurity(result);

		Log.d("tvos", " isConfiged result:::" + result);

		for (int i = 0; i < configs.size(); i++) {

			if (configs.get(i).SSID == null || "".equals(configs.get(i).SSID)) {
				continue;
			}

			int configSecurity = getSecurity(configs.get(i));

			if ((configs.get(i).SSID.equals(ssid) && resultSecurity == configSecurity)
					|| configs.get(i).SSID.equals(ssid)
					&& getLevel(result.level) == 0) {

				mCurrentNetworkId = configs.get(i).networkId;
				selectedSSID = result.SSID; // /保存选中的选中的 信号源的 SSID

				return true;
			}
		}

		return false;
	}

	/**
	 * 在这里可以写一些以后要多次用到的对话框
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case MSG_CHECK_WIFI_IS_CONNECTED: {
			ProgressDialog dialog = new ProgressDialog(this);
			if (mflag != null && "ADD".equals(mflag)) {
				dialog.setMessage(getResources()
						.getString(R.string.wifi_adding));
			} else {
				dialog.setMessage(getResources().getString(
						R.string.wifi_connecting));
			}
			dialog.setIndeterminate(true);
			dialog.setCancelable(false);
			return dialog;
		}
		case FAILURE: {
			Dialog dialog = new AlertDialog.Builder(WiFiSignalListActivity.this)
					.setMessage(R.string.wifi_wps_failed)
					.setTitle(R.string.wps_key_connect)
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.dismiss();
								}
							}).create();
			return dialog;
		}
		case IN_PROGRESS: {
			Dialog dialog = new AlertDialog.Builder(WiFiSignalListActivity.this)
					.setMessage(R.string.wifi_wps_in_progress)
					.setTitle(R.string.wps_key_connect)
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.dismiss();
								}
							}).create();
			return dialog;
		}
		}
		return null;
	}

	// 添加成功与否的判断线程
	Runnable addRunnable = new Runnable() {

		public void run() {

			try {

				Log.d(TAG,
						"WiFiSignalListActivity.addRunnable.TIME:"
								+ System.currentTimeMillis() / 1000);

				List<WifiConfiguration> configList = mWifiManager
						.getConfiguredNetworks();

				Log.e(TAG, "WiFiAddDialog.addRunnable.configs.size():"
						+ configList.size());

				int check = 0;

				if (configList != null && configList.size() > 0) {

					for (int i = 0; i < configList.size(); i++) {
						check++;
						WifiConfiguration config = configList.get(i);

						if (mConfig.SSID.equals(config.SSID)) {
							check = 0;
							// mSecore == SECURE_WEP ||
							if (mSecore == SECURE_WPA) {
								dismissDialog(MSG_CHECK_WIFI_IS_CONNECTED);
							}
							Toast.makeText(WiFiSignalListActivity.this,
									R.string.wifi_add_ok, Toast.LENGTH_SHORT)
									.show();
							break;
						}
					}

					Log.e(TAG, "WiFiAddDialog.addRunnable.check:" + check);
					if (check >= configList.size()) {
						// mSecore == SECURE_WEP ||
						if (mSecore == SECURE_WPA) {
							dismissDialog(MSG_CHECK_WIFI_IS_CONNECTED);
						}
						Toast.makeText(WiFiSignalListActivity.this,
								R.string.wifi_add_error, Toast.LENGTH_SHORT)
								.show();
					}
				}
			} catch (Exception e) {
				Log.e(TAG, "WiFiAddDialog.addRunnable.e.getLocalizedMessage():"
						+ e.getLocalizedMessage());
			}
		}
	};

	public void getConfig(WifiConfiguration config) {
		this.mConfig = config;
	}

	public void getSecore(int mCurrentSecure, String flag) {
		this.mSecore = mCurrentSecure;
		this.mflag = flag;
	}

	/**
	 * show dialog in order to indicate wifi is connecting
	 * 
	 */
	private void showProgressDialog() {
		mProgressDialog = new ProgressDialog(this);
		if (mflag != null && "ADD".equals(mflag)) {
			mProgressDialog.setMessage(getResources().getString(
					R.string.wifi_adding));
		} else {
			mProgressDialog.setMessage(getResources().getString(
					R.string.wifi_connecting));
		}
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setCancelable(false);
	}

	private void refresh() {
		if (mConfig != null && getSecurity(mConfig) != Constants.SECURITY_NONE) {
			if (!isConnected) {
				Toast.makeText(WiFiSignalListActivity.this,
						R.string.wifi_disabled_password_failure,
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * S 通过WifiConfiguration获取网络的的加密方式
	 * 
	 * @param config
	 * @return 当网络的加密方式
	 */
	public int getSecurity(WifiConfiguration config) {
		if (config.allowedKeyManagement.get(KeyMgmt.WPA_PSK)
				|| config.allowedKeyManagement.get(KeyMgmt.WPA2_PSK)) {
			return Constants.SECURITY_PSK;
		}
		if (config.allowedKeyManagement.get(KeyMgmt.WPA_EAP)
				|| config.allowedKeyManagement.get(KeyMgmt.IEEE8021X)) {
			return Constants.SECURITY_EAP;
		}
		return (config.wepKeys[0] != null) ? Constants.SECURITY_WEP
				: Constants.SECURITY_NONE;
	}

	public int checkCurrentWiFi(ScanResult result) {

		if (result.capabilities.contains("WEP")) {
			return Constants.SECURITY_WEP;
		} else if (result.capabilities.contains("PSK")) {
			return Constants.SECURITY_PSK;
		} else if (result.capabilities.contains("EAP")) {
			return Constants.SECURITY_EAP;
		}
		return Constants.SECURITY_NONE;
	}

	/**
	 * Calculates the level of the signal
	 * 
	 * @param level
	 * @return
	 */
	public int getLevel(int level) {
		if (level == Integer.MAX_VALUE) {
			return -1;
		}
		return WifiManager.calculateSignalLevel(level, 4);
	}
}
