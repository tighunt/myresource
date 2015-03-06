package com.android.settings.net;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.settings.R;

/*
 * [2012-2-22 严海东]yanhd@biaoqi.com.cn
 *  wifi direct setting 
 */

public class WifiDirectSetting implements PeerListListener {

	private final String TAG = "==>WifiDirectSetting";
	// /ui widget
	private NetSettingViewHolder mViewHolder;
	// of activity
	private NetSettingActivity mActivity;
	private CheckBox mCheckBox;
	private TextView mThisDeviceinfoTxt;
	private TextView mNoticeTextView;

	private ListView mDeviceListView;
	private WifiDirectListAdapter mDeviceAdater;

	private boolean mIsDirectEnable;
	private WifiP2pManager mWifiP2pManager;
	private WifiManager mWifiManager;
	private WifiP2pManager.Channel mChannel;
	private WifiP2pDevice mThisDevice;
	private final IntentFilter mIntentFilter = new IntentFilter();
	private WifiP2pDeviceList mPeers = new WifiP2pDeviceList();
	
    private static final int WPS_PBC = 0;
    private static final int WPS_KEYPAD = 1;
    private static final int WPS_DISPLAY = 2;
	private int mWpsSetupIndex = WPS_PBC; //default is pbc
	
	private int mSelectedIndex ;
	
	public WifiDirectSetting(NetSettingViewHolder viewHolder,
			NetSettingActivity activity) {
		this.mViewHolder = viewHolder;
		this.mActivity = activity;

		this.mThisDeviceinfoTxt = viewHolder.mDeivceinfoTxt;
		this.mCheckBox = viewHolder.mdirectCheckbox;
		this.mNoticeTextView = viewHolder.mDiscoverNoticeTxt;

		mIsDirectEnable = false;
		// /init the manager
		mWifiP2pManager = (WifiP2pManager) 
				mActivity.getSystemService(Context.WIFI_P2P_SERVICE);
		if (mWifiP2pManager != null) {
			mChannel = mWifiP2pManager.initialize(mActivity,
					mActivity.getMainLooper(), null);
			if (mChannel == null) {
				// Failure to set up connection
				Log.e(TAG, "Failed to set up connection with wifi p2p service");
				mWifiP2pManager = null;
				mCheckBox.setEnabled(false);
			}
		} else {
			Log.e(TAG, "mWifiP2pManager is null!");
		}

		// /add action
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		mIntentFilter
				.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		mIntentFilter
				.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

		mActivity.registerReceiver(mReceiver, mIntentFilter);

		findViews();
		registerListener();
	}

	private void findViews() {
		mDeviceListView = (ListView) mActivity
				.findViewById(R.id.direct_device_list);
		mDeviceListView.setDividerHeight(0);
	}

	private void registerListener() {
		mDeviceListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Log.d(TAG, "Clicked +i" + position);
				mSelectedIndex = position;
				showDialog();
			}
		});
	}

	///when the device remove;
	public void onWifiDeviceRemove(){
		if (mIsDirectEnable){
			/*
			if (WifiP2pDevice.CONNECTED == mThisDevice.status){
				disconnectDirect();
			}
			*///setDirectEnable
			if (mCheckBox.isClickable()){
				Log.d(TAG,"onWifiDeviceRemove and close wifi direct");
				setDirectEnable(false);
			}
		}
	}
	
	public void initDirectUI() {
		updateDeviceInfo();
		
		///discover device
		if (mWifiP2pManager != null) {
			discoverNotice(0);
			mWifiP2pManager.discoverPeers(mChannel,
					new WifiP2pManager.ActionListener() {
						public void onSuccess() {
							Log.d(TAG, " discover success");
							//discoverNotice(1);
						}

						public void onFailure(int reason) {
							Log.e(TAG, " discover fail " + reason);
							//discoverNotice(2);
						}
					});
		}
	}

	///get current select device of config
	public WifiP2pConfig getConfig(WifiP2pDevice device) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps = new WpsInfo();
        switch (mWpsSetupIndex) {
            case WPS_PBC:
                config.wps.setup = WpsInfo.PBC;
                break;
            case WPS_KEYPAD:
                config.wps.setup = WpsInfo.KEYPAD;
                /*
                config.wps.pin = ((TextView) mView.findViewById(R.id.wps_pin)).
                        getText().toString();
                        */
                break;
            case WPS_DISPLAY:
                config.wps.setup = WpsInfo.DISPLAY;
                break;
            default:
                config.wps.setup = WpsInfo.PBC;
                break;
        }
        return config;
    }
	
	private void discoverNotice(int status){
		String str = "";
		switch(status){
		case 0:
			str =  mActivity.getResources().getString(
					R.string.wifi_direct_search);
			break;
		case 1:
			str =  mActivity.getResources().getString(
					R.string.device_search_success);
			break;
		case 2:
			str =  mActivity.getResources().getString(
					R.string.device_search_failed);
			break;
		default:
				break;
		}
		
		String txt = null;
		if (mIsDirectEnable){
			txt = mActivity.getResources().getString(
					R.string.device_list) + "       " + str;
		}else{
			txt = mActivity.getResources().getString(
					R.string.device_list);
		}
		mNoticeTextView.setText(txt);
	}
	
	// /wifi direct discover
	public void directDeviceDiscover() {
		Log.d(TAG, "directDeviceDiscover");
		
		if (!mIsDirectEnable){
			Toast.makeText(mActivity,
					mActivity.getString(R.string.please_open_direct),
					Toast.LENGTH_LONG).show();
			return;
		}
		
		if (mWifiP2pManager != null) {
			discoverNotice(0);
			mWifiP2pManager.discoverPeers(mChannel,
					new WifiP2pManager.ActionListener() {
						public void onSuccess() {
							Log.d(TAG, " discover success");
							//discoverNotice(1);
						}

						public void onFailure(int reason) {
							Log.e(TAG, " discover fail " + reason);
							//discoverNotice(2);
						}
					});
		}
	}

	///connectr
	public void connectDirect(WifiP2pConfig config) {
		if (null == config) return;
		mWifiP2pManager.connect(mChannel, config, new ActionListener() {
			@Override
			public void onSuccess() {
				Log.d(TAG, "Connect success ");
			}

			@Override
			public void onFailure(int reason) {
				Toast.makeText(mActivity, "Connect failed. Retry.",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	///disconnect
	public void disconnectDirect() {
		if (null == mChannel){
			Log.e(TAG, "mChannel==null");
			return ;
		}
		
		mWifiP2pManager.removeGroup(mChannel, new ActionListener() {
			@Override
			public void onFailure(int reasonCode) {
				Log.e(TAG, "Disconnect failed. Reason :" + reasonCode);
			}
			@Override
			public void onSuccess() {
				Log.d(TAG, "Disconnect success ");
			}

		});
	}

	// /show device info
	private void updateDeviceInfo() {

		String text = "";
		String title;
		if (mThisDevice != null) {
			// /show device name
			if (TextUtils.isEmpty(mThisDevice.deviceName)) {
				title = mActivity.getResources().getString(
						R.string.device_address);

				text += (title + mThisDevice.deviceAddress + "\n");
			} else {
				title = mActivity.getResources()
						.getString(R.string.device_name);
				text += (title + mThisDevice.deviceName + "\n");
			}

			// show connect status
			//if (mThisDevice.status == WifiP2pDevice.CONNECTED) {
				String[] statusArray = mActivity.getResources().getStringArray(
						R.array.wifi_p2p_status);
				title = mActivity.getResources().getString(
						R.string.device_status);
				if (mThisDevice.status < statusArray.length){
					text += (title + statusArray[mThisDevice.status] + "\n");
				}
			//}
		}
		mThisDeviceinfoTxt.setText(text);
	}

	// activity onDestory
	public void onDestoryDirect() {
		mActivity.unregisterReceiver(mReceiver);
	}

	// is wifi enable
	public boolean isWifiDirectEnable() {
		return mIsDirectEnable;
	}

	// /open or close wifi direct
	public boolean setDirectEnable(boolean isAble) {
		if (mWifiP2pManager == null || null == mChannel)
			return false;

		mCheckBox.setEnabled(false);
		if (isAble) {
			Log.d(TAG, "open direct");
			///[2012-3-29add]
			mCheckBox.setChecked(false);
			
						mActivity.mWiFiSetting.openWifi();
			///when open wifi direct and to close wifi hotspot and wifi // change by cwj
			mActivity.mWifiApSet.closeWifiAp();

		} else {
			mActivity.mWiFiSetting.closeWifi();  // change by cwj
			Log.d(TAG, "close direct");
		}
		return true;
	}

	public void closeWifiDirect(){
		setDirectEnable(false);
	}
	
	public boolean checkNetStatus(boolean op) {

		//Log.d(TAG, "mActivity.mWifiManager.isWifiDeviceExist():" + mActivity.mWifiManager.isWifiDeviceExist());   // change by cwj
		Log.d(TAG, "mActivity.mWifiManager.isWifiApEnabled():" + mActivity.mWifiManager.isWifiApEnabled());
		Log.d(TAG, "mActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT):" + mActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT));
		
		// /no dongle plug
		/*if (!mActivity.mWifiManager.isWifiDeviceExist()) {
			Toast.makeText(mActivity, mActivity.getString(R.string.please_insert_dongle), Toast.LENGTH_LONG).show();  //  change by cwj
			return false;
		}

		// 判断此网卡是否具有WiFi Direct功能
		if (op && !mActivity.mWifiManager.isWifiDeviceSupportP2p()) {
			Toast.makeText(mActivity, mActivity.getString(R.string.check_wifi_direct_error), Toast.LENGTH_LONG).show();  // change by cwj
			Log.e(TAG, "the wifi no SupportP2p");
			return false;
		}*/

		// /is wifi pppoe connet
		/*if (mActivity.mPppoeSetting.isPppoeActive()
				&& !mActivity.mPppoeSetting.isWirePPPoE()) {
			Toast.makeText(mActivity,
					mActivity.getString(R.string.please_hangup_pppoe),           // change by cwj
					Toast.LENGTH_LONG).show();
			return false;
		}*/

		return true;
	}

	private boolean checkWifiDirect() {
//		WifiP2pManager p2p = (WifiP2pManager) mActivity.getSystemService(Context.WIFI_P2P_SERVICE);
//
//        if (!mActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT)) {
//        	getPreferenceScreen().removePreference(wifiP2p);
//        } else {
//            mWifiP2pEnabler = new WifiP2pEnabler(mActivity, wifiP2p);
//        }
//        getPreferenceScreen().removePreference(findPreference(KEY_WIFI_P2P_SETTINGS));
        
        return false;
	}

	protected void showDialog() {
		
		AlertDialog.Builder builder = new Builder(mActivity);
		builder.setMessage(mActivity.getResources().getString(
				R.string.wifi_direct_cancel));
		builder.setTitle(mActivity.getResources().getString(
				R.string.wifi_direct));

		WifiP2pDevice device = (WifiP2pDevice)(mPeers.getDeviceList().toArray()[mSelectedIndex]);
		if (WifiP2pDevice.CONNECTED == device.status){
			builder.setPositiveButton(
					mActivity.getResources()
							.getString(R.string.wifi_direct_disconnect),
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							Log.e(TAG, "disconnect ");
							//Collection<WifiP2pDevice> itemsList = mPeers.getDeviceList();
							disconnectDirect();
							dialog.dismiss();
						}
					});
		}else {
			builder.setPositiveButton(
					mActivity.getResources()
							.getString(R.string.wifi_direct_connect),
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							Log.e(TAG, "connect ");
							//Collection<WifiP2pDevice> itemsList = mPeers.getDeviceList();
							WifiP2pDevice device = (WifiP2pDevice)(mPeers.getDeviceList().toArray()[mSelectedIndex]);
							WifiP2pConfig config = getConfig(device);
							connectDirect(config);
							dialog.dismiss();
						}
					});
		}
		
		builder.create().show();
	}

	// /set check box status
	private void handleP2pStateChanged(int state) {
		mCheckBox.setEnabled(true);
		switch (state) {
		case WifiP2pManager.WIFI_P2P_STATE_ENABLED:
			Log.d(TAG, "WIFI_P2P_STATE_ENABLED");
			mIsDirectEnable = true;
			mCheckBox.setChecked(true);
			break;
		case WifiP2pManager.WIFI_P2P_STATE_DISABLED:
			Log.d(TAG, "WIFI_P2P_STATE_DISABLED and Open wifi");
			mIsDirectEnable = false;
			mCheckBox.setChecked(false);
			///open wifi
			//mActivity.mWiFiSetting.openWifi();
			break;
		default:
			mIsDirectEnable = true;
			Log.d(TAG,"Unhandled wifi state " + state);
			break;
		}
	}

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
				Log.d(TAG, "WIFI_P2P_STATE_CHANGED_ACTION");
				// TODO: nothing right now
				handleP2pStateChanged(intent.getIntExtra(
						WifiP2pManager.EXTRA_WIFI_STATE,
						WifiP2pManager.WIFI_P2P_STATE_DISABLED));
			} else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION
					.equals(action)) {
				Log.d(TAG, "WIFI_P2P_PEERS_CHANGED_ACTION");
				if (mWifiP2pManager != null) {
					mWifiP2pManager.requestPeers(mChannel,
							WifiDirectSetting.this);
				}
			} else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION
					.equals(action)) {
				Log.d(TAG, "WIFI_P2P_CONNECTION_CHANGED_ACTION");
				if (mWifiP2pManager == null)
					return;
				NetworkInfo networkInfo = (NetworkInfo) intent
						.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
				if (networkInfo.isConnected()) {
				}
			} else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION
					.equals(action)) {
				Log.d(TAG, "WIFI_P2P_THIS_DEVICE_CHANGED_ACTION");
				mThisDevice = (WifiP2pDevice) intent
						.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
				Log.d(TAG, "mThisDevice.String()" + mThisDevice.toString());
				updateDeviceInfo();
			}
		}
	};

	@Override
	public void onPeersAvailable(WifiP2pDeviceList peers) {

		for (WifiP2pDevice peer : peers.getDeviceList()) {
			Log.d(TAG, "peer=" + peer.toString());
		}
		Log.d(TAG, "mPeers.size=" + mPeers.getDeviceList().size());
		mPeers = peers;
		if(mPeers.getDeviceList().size() > 0)
		    discoverNotice(1);
		else
		    discoverNotice(2);
		mDeviceAdater = new WifiDirectListAdapter(mActivity,
				mPeers.getDeviceList());
		mDeviceListView.setAdapter(mDeviceAdater);
	}

}
