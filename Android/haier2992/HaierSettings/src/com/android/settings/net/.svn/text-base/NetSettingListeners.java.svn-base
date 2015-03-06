package com.android.settings.net;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.android.settings.R;

/**
 * 网络设置界面各种监听器
 * 
 * @author 杜聪甲(ducj@biaoqi.com.cn)
 * @since 1.0 2011-11-15
 */
public class NetSettingListeners {

	private static final String TAG = "NetSettingListeners==>";

	private static final String PPPOE_IS_AUTO_DIALER = "pppoe_is_auto_dialer";

	private NetSettingViewHolder mNetSettingViewHolder;

	private NetSettingActivity mNetSettingActivity;
		
	private Handler myHandler = new Handler();

	public NetSettingListeners(NetSettingActivity netSettingActivity,
			NetSettingViewHolder netSettingViewHolder) {
		this.mNetSettingActivity = netSettingActivity;
		this.mNetSettingViewHolder = netSettingViewHolder;
		registerListeners();
	}

	private void swapSelected(int position) {
		switch (position) {
		// 网络状态
		case Constants.NET_STATE:

			// mNetSettingViewHolder.mNetCurrentPostion.setText(">>"
			// + mNetSettingActivity.getString(R.string.net_state));

			FocusChange.mFocusLocal = Constants.CONNECT_FORMAT;
			mNetSettingViewHolder.mNetState.setVisibility(View.VISIBLE);
			mNetSettingViewHolder.mWirelessSettingIp.setVisibility(View.GONE);
			mNetSettingViewHolder.mPPPOESetting.setVisibility(View.GONE);
			mNetSettingViewHolder.mdirectLlayout.setVisibility(View.GONE);
			mNetSettingViewHolder.mLocationServiceLy.setVisibility(View.GONE);
			mNetSettingViewHolder.mWifiHotspotLy.setVisibility(View.GONE);
			mNetSettingActivity.initCurrentNetState();
			break;
		// 有线设置
		case Constants.WIRE_SETTING:
			// mNetSettingViewHolder.mNetCurrentPostion.setText(">>"
			// + mNetSettingActivity.getString(R.string.wire_setting));

			mNetSettingActivity.isWireAutoIpAddress = mNetSettingActivity.mWireSetting
					.isWireAutoIP();

			FocusChange.mFocusLocal = Constants.IS_AUTO_IP_ADDRESS;
			// [2012-1-16add]清空字串
			mNetSettingViewHolder.resetIPs();

			mNetSettingViewHolder.mNetState.setVisibility(View.GONE);
			mNetSettingViewHolder.mWirelessSettingIp.setVisibility(View.VISIBLE);
			mNetSettingViewHolder.mPPPOESetting.setVisibility(View.GONE);
			mNetSettingViewHolder.mdirectLlayout.setVisibility(View.GONE);
			mNetSettingViewHolder.mWifiHotspotLy.setVisibility(View.GONE);
			mNetSettingViewHolder.mLocationServiceLy.setVisibility(View.GONE);
			mNetSettingActivity.mWireSetting.initWireUI();

			break;
		// 无线设置
		case Constants.WIRELESS_SETTING:
			// mNetSettingViewHolder.mNetCurrentPostion.setText(">>"
			// + mNetSettingActivity.getString(R.string.wireless_setting));
			mNetSettingActivity.isWirelessAutoIpAddress = mNetSettingActivity.mWiFiSetting
					.isAutoIP();

			FocusChange.mFocusLocal = Constants.WIFI_SWITCH;
			// [2012-1-17add]清空ui字串
			mNetSettingViewHolder.resetIPs();

			mNetSettingViewHolder.mNetState.setVisibility(View.GONE);
			mNetSettingViewHolder.mWirelessSettingIp.setVisibility(View.VISIBLE);
			mNetSettingViewHolder.mPPPOESetting.setVisibility(View.GONE);
			mNetSettingViewHolder.mdirectLlayout.setVisibility(View.GONE);
			mNetSettingViewHolder.mWifiHotspotLy.setVisibility(View.GONE);
			mNetSettingViewHolder.mLocationServiceLy.setVisibility(View.GONE);
			Log.d(TAG, "NetSettingActivity.swapSelected_Constants.WIRELESS_SETTING......");
			mNetSettingActivity.mWiFiSetting.initWifiUI();
			break;
		// PPPOE设置
		// delete pppoe 2012-05-23
		/*
		 * case Constants.PPPOE_SETTING: //count++;
		 * //mNetSettingViewHolder.mNetCurrentPostion.setText(">>" // +
		 * mNetSettingActivity.getString(R.string.dial_setting));
		 * 
		 * FocusChange.mFocusLocal = Constants.PPPOE_AUTO_IP;
		 * 
		 * mNetSettingViewHolder.mNetState.setVisibility(View.GONE);
		 * mNetSettingViewHolder.mWirelessSettingIp.setVisibility(View.GONE);
		 * mNetSettingViewHolder.mPPPOESetting.setVisibility(View.VISIBLE);
		 * mNetSettingViewHolder.mdirectLlayout.setVisibility(View.GONE);
		 * mNetSettingViewHolder.mWifiHotspotLy.setVisibility(View.GONE);
		 * mNetSettingViewHolder.mLocationServiceLy.setVisibility(View.GONE);
		 * mNetSettingActivity.mPppoeSetting.initPPPOEData(); break;
		 */
		case Constants.WIFI_DIRECT:
			System.out.println("===>Constants.WIFI_DIRECT");
			// mNetSettingViewHolder.mNetCurrentPostion.setText(">>"
			// + mNetSettingActivity.getString(R.string.wifi_direct));

			FocusChange.mFocusLocal = Constants.IS_WIFI_DIRECT_OPEN;
			mNetSettingViewHolder.mNetState.setVisibility(View.GONE);
			mNetSettingViewHolder.mWirelessSettingIp.setVisibility(View.GONE);
			mNetSettingViewHolder.mPPPOESetting.setVisibility(View.GONE);
			mNetSettingViewHolder.mdirectLlayout.setVisibility(View.VISIBLE);
			mNetSettingViewHolder.mWifiHotspotLy.setVisibility(View.GONE);
			mNetSettingViewHolder.mLocationServiceLy.setVisibility(View.GONE);
			mNetSettingActivity.mWifiDirect.initDirectUI();
			break;
		case Constants.WIFI_HOTSPOT:
			System.out.println("===>Constants.WIFI_HOTSPOT");
			// mNetSettingViewHolder.mNetCurrentPostion.setText(">>"
			// + mNetSettingActivity.getString(R.string.wifi_hotspot));

			FocusChange.mFocusLocal = Constants.WIFI_AP_SWITCH;
			mNetSettingViewHolder.mNetState.setVisibility(View.GONE);
			mNetSettingViewHolder.mWirelessSettingIp.setVisibility(View.GONE);
			mNetSettingViewHolder.mdirectLlayout.setVisibility(View.GONE);
			mNetSettingViewHolder.mPPPOESetting.setVisibility(View.GONE);
			mNetSettingViewHolder.mWifiHotspotLy.setVisibility(View.VISIBLE);
			mNetSettingViewHolder.mLocationServiceLy.setVisibility(View.GONE);
			mNetSettingActivity.mWifiApSet.initWifiAp();
			break;
		case Constants.LOCALTON_SERVICE:
			System.out.println("===>Constants.LOCALTON_SERVICE");
			// mNetSettingViewHolder.mNetCurrentPostion.setText(">>"
			// + mNetSettingActivity.getString(R.string.location_service));

			FocusChange.mFocusLocal = Constants.LOCATION_SERVICE_SW;
			mNetSettingViewHolder.mNetState.setVisibility(View.GONE);
			mNetSettingViewHolder.mWirelessSettingIp.setVisibility(View.GONE);
			mNetSettingViewHolder.mdirectLlayout.setVisibility(View.GONE);
			mNetSettingViewHolder.mPPPOESetting.setVisibility(View.GONE);
			mNetSettingViewHolder.mWifiHotspotLy.setVisibility(View.GONE);
			mNetSettingViewHolder.mLocationServiceLy.setVisibility(View.VISIBLE);
			mNetSettingActivity.mLocalSetting.initLocationUI();
			break;
		}
	}

	/**
	 * 监听器的注册
	 */
	private void registerListeners() {

		// /左侧列表焦点移动。
		mNetSettingViewHolder.mNetSetting.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mNetSettingActivity.isSelected = position;
				swapSelected(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		mNetSettingViewHolder.mNetSetting.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mNetSettingActivity.isSelected = position;
				swapSelected(position);
			}
		});

		// 网络状态中连接方式
		/*
		 * [2012-4-6 yanhd]update mNetSettingViewHolder.mConnectFormat .setOnClickListener(new
		 * View.OnClickListener() {
		 * 
		 * // /[2012-1-14 add]原来 保存网络设置的地方。
		 * 
		 * @Override public void onClick(View v) { if (!mNetSettingActivity.isModifyConnectFormat) {
		 * mNetSettingViewHolder.mConnectFormat_left .setVisibility(View.VISIBLE);
		 * mNetSettingViewHolder.mConnectFormat_right .setVisibility(View.VISIBLE);
		 * mNetSettingActivity.isModifyConnectFormat = true; } else {
		 * 
		 * mNetSettingViewHolder.mConnectFormat_left .setVisibility(View.INVISIBLE);
		 * mNetSettingViewHolder.mConnectFormat_right .setVisibility(View.INVISIBLE);
		 * mNetSettingActivity.isModifyConnectFormat = false; } } });
		 */

		// 为了使焦点回到当前的选中项
		mNetSettingViewHolder.mNetSetting.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {

				if (hasFocus) {
					myHandler.postAtFrontOfQueue(new Runnable() {
						public void run() {
							mNetSettingViewHolder.mNetSetting
									.setSelection(mNetSettingActivity.isSelected);
						}
					});
					mNetSettingActivity.focusIsRight = false;
				}
			}
		});

		// /[2012-3-12add]wifi switch
		mNetSettingViewHolder.mWifiSwitchCb.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CheckBox ch = (CheckBox) v;
				System.out.println("===>isChecked=" + ch.isChecked());
/*				if (mNetSettingActivity.mWifiManager.isWifiEnabled()) {  // change by cwj
					ch.setChecked(false);
					// /notice please plug the dongle
					Toast.makeText(mNetSettingActivity,
							mNetSettingActivity.getString(R.string.please_insert_dongle),
							Toast.LENGTH_LONG).show();
					return;
				}

				if (mNetSettingActivity.mPppoeSetting.isPppoeActive()) {
       			ch.setChecked(!ch.isChecked());
					// /notice please plug the dongle
					Toast.makeText(mNetSettingActivity,
							mNetSettingActivity.getString(R.string.please_hangup_pppoe), // change by cwj
							Toast.LENGTH_LONG).show();
					return;
				}
*/
				mNetSettingActivity.mWiFiSetting.switchWifiConnect(ch.isChecked());
			}
		});

		// /wifi switch check box focus change listener
		mNetSettingViewHolder.mWifiSwitchCb.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					mNetSettingViewHolder.mWifiSwitchLy
							.setBackgroundResource(R.drawable.desktop_button);
				} else {
					mNetSettingViewHolder.mWifiSwitchLy.setBackgroundResource(R.drawable.one_px);
				}
			}
		});

		// /ssid focus change listener
		mNetSettingViewHolder.mSSID.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					mNetSettingViewHolder.mSSIDRelativeLayout
							.setBackgroundResource(R.drawable.desktop_button);
				} else {
					mNetSettingViewHolder.mSSIDRelativeLayout
							.setBackgroundResource(R.drawable.one_px);
				}
			}
		});

		// /dhcp switch focus change listener
		mNetSettingViewHolder.mAutoIpAddress.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					mNetSettingViewHolder.mGetIpRelativeLayout
							.setBackgroundResource(R.drawable.desktop_button);
				} else {
					mNetSettingViewHolder.mGetIpRelativeLayout
							.setBackgroundResource(R.drawable.one_px);
				}
			}
		});

		// /Local service focus listener
		mNetSettingViewHolder.mLocServiceSwitch
				.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							mNetSettingViewHolder.mLocServiceSwitchLy
									.setBackgroundResource(R.drawable.desktop_button);
						} else {
							mNetSettingViewHolder.mLocServiceSwitchLy
									.setBackgroundResource(R.drawable.one_px);
						}
					}
				});

		// ////Local service click listener
		mNetSettingViewHolder.mLocServiceSwitch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CheckBox ch = (CheckBox) v;
				System.out.println("===>isChecked=" + ch.isChecked());

				mNetSettingActivity.mLocalSetting.setNetLoc(ch.isChecked());
			}
		});

		// [2012-2-15]有线和无线自动获取IP地址
		mNetSettingViewHolder.mAutoIpAddress.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				if (1 == mNetSettingActivity.isSelected) {// /有线
					Log.d(TAG, "isWireAutoIpAddress=" + mNetSettingActivity.isWireAutoIpAddress);
					mNetSettingActivity.isWireAutoIpAddress = !mNetSettingActivity.isWireAutoIpAddress;
					if (mNetSettingActivity.isWireAutoIpAddress) {
						setInisible();
					} else {
						setVisible();       
						// mNetSettingActivity.mWireSetting.showWireSetting();// /显示ip地址，网关等。
					}

				} else {// /无线
					mNetSettingActivity.isWirelessAutoIpAddress = !mNetSettingActivity.isWirelessAutoIpAddress;
					Log.d(TAG, "isWirelessAutoIpAddress="
							+ mNetSettingActivity.isWirelessAutoIpAddress);
					if (mNetSettingActivity.isWirelessAutoIpAddress) {
						setInisible();
					} else {
						setVisible();	        
						// mNetSettingActivity.mWiFiSetting.showWiFiSetting();
					}
				}

			}
		});

		// 无线网络设置中当点击SSID时，会跳转到无线信号列表界面
		mNetSettingViewHolder.mSSID.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mNetSettingActivity.mWifiManager.isWifiEnabled()) {

					if (null != mNetSettingActivity.mWifiManager.getScanResults()) {

						// /[2012-2-17add]若有pppoe连接，则提示先挂断。
//						if (mNetSettingActivity.mPppoeSetting.isPppoeActive()) {
//							Toast.makeText(mNetSettingActivity,
//									mNetSettingActivity.getString(R.string.please_hangup_pppoe), // change by wcj
	//								Toast.LENGTH_LONG).show();
//							return;
//						}

						Intent intent = new Intent(mNetSettingActivity,
								WiFiSignalListActivity.class);
						/*
						 * Bundle bundle = new Bundle(); boolean isconnected =
						 * mNetSettingActivity.mPppoeSetting.isPppoeConnected();
						 * bundle.putBoolean("isPppoeConnected", isconnected); //[2012-2-17add]
						 * intent.putExtras(bundle);
						 */
						mNetSettingActivity.startActivityForResult(intent, 1);
					} else {

						System.out.println("===> ScanList is null");
						Toast.makeText(mNetSettingActivity,
								mNetSettingActivity.getString(R.string.no_single_list),
								Toast.LENGTH_LONG).show();
					}

				} else {
					Toast.makeText(mNetSettingActivity,
							mNetSettingActivity.getString(R.string.please_insert_dongle),
							Toast.LENGTH_LONG).show();
				}
			}
		});
		// PPPOE拨号设置中修改PPPOE连接方式
		/*
		 * mNetSettingViewHolder.mPPPOEConnetFormat .setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { if
		 * (!mNetSettingActivity.isPPPOEModifyConnectFormat) {
		 * mNetSettingViewHolder.mPPPOE_auto_ip_left .setVisibility(View.VISIBLE);
		 * mNetSettingViewHolder.mPPPOE_auto_ip_right .setVisibility(View.VISIBLE);
		 * mNetSettingActivity.isPPPOEModifyConnectFormat = true; } else {
		 * mNetSettingViewHolder.mPPPOE_auto_ip_left .setVisibility(View.INVISIBLE);
		 * mNetSettingViewHolder.mPPPOE_auto_ip_right .setVisibility(View.INVISIBLE);
		 * mNetSettingActivity.isPPPOEModifyConnectFormat = false; } } });
		 */

		// /[2012-2-23add]wifi-direct switch
		mNetSettingViewHolder.mdirectCheckbox.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CheckBox ch = (CheckBox) v;
				System.out.println("===>isChecked=" + ch.isChecked());

				if (!mNetSettingActivity.mWifiDirect.checkNetStatus(ch.isChecked())) {
					System.out.println("===ch.setEnabled(false)");
					ch.setChecked(false);
					return;
				}
				mNetSettingActivity.mWifiDirect.setDirectEnable(ch.isChecked());
			}
		});

		// /check box focus change listener
		mNetSettingViewHolder.mdirectCheckbox.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					mNetSettingViewHolder.mdirectSwitchLlayout
							.setBackgroundResource(R.drawable.desktop_button);
				} else {
					mNetSettingViewHolder.mdirectSwitchLlayout
							.setBackgroundResource(R.drawable.one_px);
				}
			}
		});

		// /[2012-2-23add]wifi-direct discover
		mNetSettingViewHolder.mDiscoverButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mNetSettingActivity.mWifiDirect.directDeviceDiscover();
			}

		});

		// /wifi direct check box focus change listener
		mNetSettingViewHolder.mDiscoverButton.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					mNetSettingViewHolder.mDiscoverButton
							.setBackgroundResource(R.drawable.edit_focus);
				} else {
					mNetSettingViewHolder.mDiscoverButton
							.setBackgroundResource(R.drawable.edit_normal);
				}
			}
		});

		// /[2012-2-23add]wifi hotspotswitch
		mNetSettingViewHolder.mWifiApSwitchCb.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CheckBox ch = (CheckBox) v;
				System.out.println("===>isChecked=" + ch.isChecked());

				if (!mNetSettingActivity.mWifiApSet.checkNetStatus(ch.isChecked())) {
					System.out.println("===ch.setEnabled(false)");
					ch.setChecked(false);
					return;
				}
				mNetSettingActivity.mWifiApSet.setSoftapEnabled(ch.isChecked());
			}
		});

		// /wifi hotspot config button
		mNetSettingViewHolder.mWifiApSwitchCb.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					mNetSettingViewHolder.mWifiApSwitchLy
							.setBackgroundResource(R.drawable.desktop_button);
				} else {
					mNetSettingViewHolder.mWifiApSwitchLy.setBackgroundResource(R.drawable.one_px);
				}
			}
		});

		// /wifi hotspot pwd edittext
		mNetSettingViewHolder.mWifiApPwd.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					((EditText) v).setSelection(((EditText) v).getText().length());
				}
			}
		});
		// /wifi hotspot ssid edittext
		mNetSettingViewHolder.mWifiApSSID.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					((EditText) v).setSelection(((EditText) v).getText().length());
				}
			}
		});

		// /wifi hotspot config button
		mNetSettingViewHolder.mWifiApSetBtn.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					mNetSettingViewHolder.mWifiApSetBtn
							.setBackgroundResource(R.drawable.edit_focus);
				} else {
					mNetSettingViewHolder.mWifiApSetBtn
							.setBackgroundResource(R.drawable.edit_normal);
				}
			}
		});

		// /wifi hotspot config button click
		mNetSettingViewHolder.mWifiApSetBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
					// if (mNetSettingViewHolder.mWifiApConfigLy.isShown()){
/*				if (!mNetSettingActivity.mWifiManager.isWifiDeviceSupportSoftap()) {
					mNetSettingViewHolder.mWifiApConfigLy.setVisibility(View.GONE);
					                                                                                 change by cwj
					Toast.makeText(mNetSettingActivity,
							mNetSettingActivity.getString(R.string.check_wifi_hotspot_error),
							Toast.LENGTH_LONG).show();

				} else {*/
					mNetSettingViewHolder.mWifiApConfigLy.setVisibility(View.VISIBLE);
//				}   change by cwj
			}
		});

		// /wifi hotspot show password checkbox
		mNetSettingViewHolder.mWifiApShowPwdCb
				.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							mNetSettingViewHolder.mWifiApShowPwdLy
									.setBackgroundResource(R.drawable.desktop_button);
						} else {
							mNetSettingViewHolder.mWifiApShowPwdLy
									.setBackgroundResource(R.drawable.one_px);
						}
					}
				});

		// /show password
		mNetSettingViewHolder.mWifiApShowPwdCb.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CheckBox ch = (CheckBox) v;
				if (ch.isChecked()) {
					mNetSettingViewHolder.mWifiApPwd
							.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
				} else {
					mNetSettingViewHolder.mWifiApPwd.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);
				}
			}
		});

		// /wifi hotspot save button
		mNetSettingViewHolder.mWifiApSaveBtn.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					mNetSettingViewHolder.mWifiApSaveBtn
							.setBackgroundResource(R.drawable.edit_focus);
				} else {
					mNetSettingViewHolder.mWifiApSaveBtn
							.setBackgroundResource(R.drawable.edit_normal);
				}
			}
		});

		// /wifi hotspot save button click
		mNetSettingViewHolder.mWifiApSaveBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mNetSettingActivity.mWifiApSet.saveWifiApConfig();
			}
		});

		// wifi hotspot secure change left
		mNetSettingViewHolder.mWifiApSecleft.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mNetSettingActivity.mWifiApSet.secureOnKeyLeft();
			}
		});

		// wifi hotspot secure change right
		mNetSettingViewHolder.mWifiApSecright.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mNetSettingActivity.mWifiApSet.secureOnKeyRight();
			}
		});

		// /secure layout
		mNetSettingViewHolder.mWifiApSecButton
				.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							System.out.println("==>has focus");
							mNetSettingViewHolder.mWifiApSecureLy
									.setBackgroundResource(R.drawable.desktop_button);
							// mNetSettingViewHolder.mWifiApSecleft.setVisibility(View.VISIBLE);
							// mNetSettingViewHolder.mWifiApSecright.setVisibility(View.VISIBLE);
						} else {
							System.out.println("==>leave focus");
							mNetSettingViewHolder.mWifiApSecureLy
									.setBackgroundResource(R.drawable.one_px);
							// mNetSettingViewHolder.mWifiApSecleft.setVisibility(View.GONE);
							// mNetSettingViewHolder.mWifiApSecright.setVisibility(View.GONE);
						}
					}
				});

		// PPPOE拨号设置中显示密码的checkbox
		mNetSettingViewHolder.mShowPassword
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (isChecked) {
							mNetSettingViewHolder.mPassWordEditText
									.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
						} else {
							mNetSettingViewHolder.mPassWordEditText
									.setInputType(InputType.TYPE_CLASS_TEXT
											| InputType.TYPE_TEXT_VARIATION_PASSWORD);
						}
					}
				});
		// PPPOE拨号设置中是否自动拨号的切换
		/*
		 * mNetSettingViewHolder.mAutoPPPOEDialer .setOnCheckedChangeListener(new
		 * CompoundButton.OnCheckedChangeListener() {
		 * 
		 * @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		 * commitCheckBoxValue(PPPOE_IS_AUTO_DIALER, isChecked); isAutoDialer = isChecked; if
		 * (isChecked) {
		 * 
		 * } } });
		 */

		// pppoe user name
		mNetSettingViewHolder.mUsernameEditText
				.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View myview, boolean bgetfocus) {
						if (bgetfocus) {
							((EditText) myview)
									.setSelection(((EditText) myview).getText().length());
						}
					}
				});

		// pppoe password
		mNetSettingViewHolder.mPassWordEditText
				.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View myview, boolean bgetfocus) {
						if (bgetfocus) {
							((EditText) myview)
									.setSelection(((EditText) myview).getText().length());
						}
					}
				});

		// 拨号 焦点监听
		mNetSettingViewHolder.mDialerActionBtn
				.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View myview, boolean bgetfocus) {
						if (bgetfocus) {
							mNetSettingViewHolder.mDialerActionBtn
									.setBackgroundResource(R.drawable.edit_focus);
						} else {
							mNetSettingViewHolder.mDialerActionBtn
									.setBackgroundResource(R.drawable.edit_normal);
						}
					}
				});

		// 挂断焦点监听
		mNetSettingViewHolder.mDialerHangupBtn
				.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View myview, boolean bgetfocus) {
						if (bgetfocus) {
							mNetSettingViewHolder.mDialerHangupBtn
									.setBackgroundResource(R.drawable.edit_focus);
						} else {
							mNetSettingViewHolder.mDialerHangupBtn
									.setBackgroundResource(R.drawable.edit_normal);
						}
					}
				});

		// 拨号
/*		mNetSettingViewHolder.mDialerActionBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mNetSettingActivity.mPppoeSetting.pppoeAction();
			}
		});

		// hangup pppoe
		mNetSettingViewHolder.mDialerHangupBtn.setOnClickListener(new View.OnClickListener() {  // change by cwj
			@Override
			public void onClick(View v) {
				mNetSettingActivity.mPppoeSetting.pppoeHangUp();
			}
		});
*/
		// [2012-1-13]disable 掉 （有线ip设置输入框） 聚焦时的软键盘。
		// IP地址
		mNetSettingViewHolder.ip_one_ev.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View myview, boolean bgetfocus) {
				if (bgetfocus == true && mNetSettingViewHolder.ip_one_ev.isEnabled()) {

					if (KeyEvent.KEYCODE_DPAD_RIGHT == mNetSettingActivity.mPreKeyCode) {
						// [2012-1-14] 将光标移到最后;
						((EditText) myview).setSelection(((EditText) myview).getText().length());
					} else if (KeyEvent.KEYCODE_DPAD_LEFT == mNetSettingActivity.mPreKeyCode) {
						// [2012-1-14] 将光标移到最前面);
						((EditText) myview).setSelection(0);
					}
					((EditText) myview).selectAll();
				}
			}
		});

		mNetSettingViewHolder.ip_two_ev.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View myview, boolean bgetfocus) {
				if (bgetfocus == true && mNetSettingViewHolder.ip_two_ev.isEnabled()) {
					/*
					 * InputMethodManager m = (InputMethodManager)
					 * mNetSettingViewHolder.ip_two_ev.getContext() .getSystemService
					 * (mNetSettingActivity.INPUT_METHOD_SERVICE); m.toggleSoftInput(0,
					 * InputMethodManager.HIDE_NOT_ALWAYS);
					 */
					if (KeyEvent.KEYCODE_DPAD_RIGHT == mNetSettingActivity.mPreKeyCode) {
						// [2012-1-14yanhd] 将光标移到最后;
						((EditText) myview).setSelection(((EditText) myview).getText().length());
					} else if (KeyEvent.KEYCODE_DPAD_LEFT == mNetSettingActivity.mPreKeyCode) {
						// [2012-1-14yanhd] 将光标移到最前面);
						((EditText) myview).setSelection(0);
					}
					((EditText) myview).selectAll();
				}
			}
		});

		mNetSettingViewHolder.ip_three_ev.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View myview, boolean bgetfocus) {
				if (bgetfocus == true && mNetSettingViewHolder.ip_three_ev.isEnabled()) {
					/*
					 * InputMethodManager m = (InputMethodManager)
					 * mNetSettingViewHolder.ip_three_ev.getContext() .getSystemService
					 * (mNetSettingActivity.INPUT_METHOD_SERVICE); m.toggleSoftInput(0,
					 * InputMethodManager.HIDE_NOT_ALWAYS);
					 */
					if (KeyEvent.KEYCODE_DPAD_RIGHT == mNetSettingActivity.mPreKeyCode) {
						// [2012-1-14yanhd] 将光标移到最后;
						((EditText) myview).setSelection(((EditText) myview).getText().length());
					} else if (KeyEvent.KEYCODE_DPAD_LEFT == mNetSettingActivity.mPreKeyCode) {
						// [2012-1-14yanhd] 将光标移到最前面);
						((EditText) myview).setSelection(0);
					}
					((EditText) myview).selectAll();
				}
			}
		});

		mNetSettingViewHolder.ip_four_ev.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View myview, boolean bgetfocus) {
				if (bgetfocus == true && mNetSettingViewHolder.ip_four_ev.isEnabled()) {
					/*
					 * InputMethodManager m = (InputMethodManager)
					 * mNetSettingViewHolder.ip_four_ev.getContext() .getSystemService
					 * (mNetSettingActivity.INPUT_METHOD_SERVICE); m.toggleSoftInput(0,
					 * InputMethodManager.HIDE_NOT_ALWAYS);
					 */
					if (KeyEvent.KEYCODE_DPAD_RIGHT == mNetSettingActivity.mPreKeyCode) {
						// [2012-1-14yanhd] 将光标移到最后;
						((EditText) myview).setSelection(((EditText) myview).getText().length());
					} else if (KeyEvent.KEYCODE_DPAD_LEFT == mNetSettingActivity.mPreKeyCode) {
						// [2012-1-14yanhd] 将光标移到最前面);
						((EditText) myview).setSelection(0);
					}
					((EditText) myview).selectAll();
				}
			}
		});

		// 子网掩码
		mNetSettingViewHolder.subnet_one_ev.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View myview, boolean bgetfocus) {
				if (bgetfocus == true && mNetSettingViewHolder.subnet_one_ev.isEnabled()) {
					/*
					 * InputMethodManager m = (InputMethodManager)
					 * mNetSettingViewHolder.subnet_one_ev.getContext()
					 * .getSystemService(mNetSettingActivity. INPUT_METHOD_SERVICE);
					 * m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
					 */
					if (KeyEvent.KEYCODE_DPAD_RIGHT == mNetSettingActivity.mPreKeyCode) {
						// [2012-1-14yanhd] 将光标移到最后;
						((EditText) myview).setSelection(((EditText) myview).getText().length());
					} else if (KeyEvent.KEYCODE_DPAD_LEFT == mNetSettingActivity.mPreKeyCode) {
						// [2012-1-14yanhd] 将光标移到最前面);
						((EditText) myview).setSelection(0);
					}
					((EditText) myview).selectAll();
				}
			}
		});

		mNetSettingViewHolder.subnet_two_ev.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View myview, boolean bgetfocus) {
				if (bgetfocus == true && mNetSettingViewHolder.subnet_two_ev.isEnabled()) {
					/*
					 * InputMethodManager m = (InputMethodManager)
					 * mNetSettingViewHolder.subnet_two_ev.getContext()
					 * .getSystemService(mNetSettingActivity. INPUT_METHOD_SERVICE);
					 * m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
					 */
					if (KeyEvent.KEYCODE_DPAD_RIGHT == mNetSettingActivity.mPreKeyCode) {
						// [2012-1-14yanhd] 将光标移到最后;
						((EditText) myview).setSelection(((EditText) myview).getText().length());
					} else if (KeyEvent.KEYCODE_DPAD_LEFT == mNetSettingActivity.mPreKeyCode) {
						// [2012-1-14yanhd] 将光标移到最前面);
						((EditText) myview).setSelection(0);
					}
					((EditText) myview).selectAll();
				}
			}
		});

		mNetSettingViewHolder.subnet_three_ev.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View myview, boolean bgetfocus) {
				if (bgetfocus == true && mNetSettingViewHolder.subnet_three_ev.isEnabled()) {
					/*
					 * InputMethodManager m = (InputMethodManager) mNetSettingViewHolder
					 * .subnet_three_ev.getContext() .getSystemService(mNetSettingActivity
					 * .INPUT_METHOD_SERVICE); m.toggleSoftInput(0,
					 * InputMethodManager.HIDE_NOT_ALWAYS);
					 */
					if (KeyEvent.KEYCODE_DPAD_RIGHT == mNetSettingActivity.mPreKeyCode) {
						// [2012-1-14yanhd] 将光标移到最后;
						((EditText) myview).setSelection(((EditText) myview).getText().length());
					} else if (KeyEvent.KEYCODE_DPAD_LEFT == mNetSettingActivity.mPreKeyCode) {
						// [2012-1-14yanhd] 将光标移到最前面);
						((EditText) myview).setSelection(0);
					}
					((EditText) myview).selectAll();
				}
			}
		});

		mNetSettingViewHolder.subnet_four_ev.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View myview, boolean bgetfocus) {
				if (bgetfocus == true && mNetSettingViewHolder.subnet_four_ev.isEnabled()) {
					/*
					 * InputMethodManager m = (InputMethodManager)
					 * mNetSettingViewHolder.subnet_four_ev.getContext()
					 * .getSystemService(mNetSettingActivity. INPUT_METHOD_SERVICE);
					 * m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
					 */
					if (KeyEvent.KEYCODE_DPAD_RIGHT == mNetSettingActivity.mPreKeyCode) {
						// [2012-1-14yanhd] 将光标移到最后;
						((EditText) myview).setSelection(((EditText) myview).getText().length());
					} else if (KeyEvent.KEYCODE_DPAD_LEFT == mNetSettingActivity.mPreKeyCode) {
						// [2012-1-14yanhd] 将光标移到最前面);
						((EditText) myview).setSelection(0);
					}
					((EditText) myview).selectAll();
				}
			}
		});

		// 默认网关
		mNetSettingViewHolder.defult_one_ev.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View myview, boolean bgetfocus) {
				if (bgetfocus == true && mNetSettingViewHolder.defult_one_ev.isEnabled()) {
					/*
					 * InputMethodManager m = (InputMethodManager)
					 * mNetSettingViewHolder.defult_one_ev.getContext()
					 * .getSystemService(mNetSettingActivity. INPUT_METHOD_SERVICE);
					 * m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
					 */
					if (KeyEvent.KEYCODE_DPAD_RIGHT == mNetSettingActivity.mPreKeyCode) {
						// [2012-1-14yanhd] 将光标移到最后;
						((EditText) myview).setSelection(((EditText) myview).getText().length());
					} else if (KeyEvent.KEYCODE_DPAD_LEFT == mNetSettingActivity.mPreKeyCode) {
						// [2012-1-14yanhd] 将光标移到最前面);
						((EditText) myview).setSelection(0);
					}
					((EditText) myview).selectAll();
				}
			}
		});

		mNetSettingViewHolder.defult_two_ev.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View myview, boolean bgetfocus) {
				if (bgetfocus == true && mNetSettingViewHolder.defult_two_ev.isEnabled()) {
					/*
					 * InputMethodManager m = (InputMethodManager)
					 * mNetSettingViewHolder.defult_two_ev.getContext()
					 * .getSystemService(mNetSettingActivity. INPUT_METHOD_SERVICE);
					 * m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
					 */
					if (KeyEvent.KEYCODE_DPAD_RIGHT == mNetSettingActivity.mPreKeyCode) {
						// [2012-1-14yanhd] 将光标移到最后;
						((EditText) myview).setSelection(((EditText) myview).getText().length());
					} else if (KeyEvent.KEYCODE_DPAD_LEFT == mNetSettingActivity.mPreKeyCode) {
						// [2012-1-14yanhd] 将光标移到最前面);
						((EditText) myview).setSelection(0);
					}
					((EditText) myview).selectAll();
				}
			}
		});

		mNetSettingViewHolder.defult_three_ev.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View myview, boolean bgetfocus) {
				if (bgetfocus == true && mNetSettingViewHolder.defult_three_ev.isEnabled()) {
					/*
					 * InputMethodManager m = (InputMethodManager) mNetSettingViewHolder
					 * .defult_three_ev.getContext() .getSystemService(mNetSettingActivity
					 * .INPUT_METHOD_SERVICE); m.toggleSoftInput(0,
					 * InputMethodManager.HIDE_NOT_ALWAYS);
					 */
					if (KeyEvent.KEYCODE_DPAD_RIGHT == mNetSettingActivity.mPreKeyCode) {
						// [2012-1-14yanhd] 将光标移到最后;
						((EditText) myview).setSelection(((EditText) myview).getText().length());
					} else if (KeyEvent.KEYCODE_DPAD_LEFT == mNetSettingActivity.mPreKeyCode) {
						// [2012-1-14yanhd] 将光标移到最前面);
						((EditText) myview).setSelection(0);
					}
					((EditText) myview).selectAll();
				}
			}
		});

		mNetSettingViewHolder.defult_four_ev.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View myview, boolean bgetfocus) {
				if (bgetfocus == true && mNetSettingViewHolder.defult_four_ev.isEnabled()) {
					/*
					 * InputMethodManager m = (InputMethodManager)
					 * mNetSettingViewHolder.defult_four_ev.getContext()
					 * .getSystemService(mNetSettingActivity. INPUT_METHOD_SERVICE);
					 * m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
					 */
					if (KeyEvent.KEYCODE_DPAD_RIGHT == mNetSettingActivity.mPreKeyCode) {
						// [2012-1-14yanhd] 将光标移到最后;
						((EditText) myview).setSelection(((EditText) myview).getText().length());
					} else if (KeyEvent.KEYCODE_DPAD_LEFT == mNetSettingActivity.mPreKeyCode) {
						// [2012-1-14yanhd] 将光标移到最前面);
						((EditText) myview).setSelection(0);
					}
					((EditText) myview).selectAll();
				}

			}
		});

		// 首选DNS
		mNetSettingViewHolder.first_dns_one_ev
				.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View myview, boolean bgetfocus) {
						if (bgetfocus == true && mNetSettingViewHolder.first_dns_one_ev.isEnabled()) {
							/*
							 * InputMethodManager m = (InputMethodManager) mNetSettingViewHolder
							 * .first_dns_one_ev.getContext() .getSystemService(mNetSettingActivity
							 * .INPUT_METHOD_SERVICE); m.toggleSoftInput(0,
							 * InputMethodManager.HIDE_NOT_ALWAYS);
							 */
							if (KeyEvent.KEYCODE_DPAD_RIGHT == mNetSettingActivity.mPreKeyCode) {
								// [2012-1-14yanhd] 将光标移到最后;
								((EditText) myview).setSelection(((EditText) myview).getText()
										.length());
							} else if (KeyEvent.KEYCODE_DPAD_LEFT == mNetSettingActivity.mPreKeyCode) {
								// [2012-1-14yanhd] 将光标移到最前面);
								((EditText) myview).setSelection(0);
							}
							((EditText) myview).selectAll();
						}
					}
				});

		mNetSettingViewHolder.first_dns_two_ev
				.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View myview, boolean bgetfocus) {
						if (bgetfocus == true && mNetSettingViewHolder.first_dns_two_ev.isEnabled()) {
							/*
							 * InputMethodManager m = (InputMethodManager) mNetSettingViewHolder
							 * .first_dns_two_ev.getContext() .getSystemService(mNetSettingActivity
							 * .INPUT_METHOD_SERVICE); m.toggleSoftInput(0,
							 * InputMethodManager.HIDE_NOT_ALWAYS);
							 */
							if (KeyEvent.KEYCODE_DPAD_RIGHT == mNetSettingActivity.mPreKeyCode) {
								// [2012-1-14yanhd] 将光标移到最后;
								((EditText) myview).setSelection(((EditText) myview).getText()
										.length());
							} else if (KeyEvent.KEYCODE_DPAD_LEFT == mNetSettingActivity.mPreKeyCode) {
								// [2012-1-14yanhd] 将光标移到最前面);
								((EditText) myview).setSelection(0);
							}
							((EditText) myview).selectAll();
						}
					}
				});

		mNetSettingViewHolder.first_dns_three_ev
				.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View myview, boolean bgetfocus) {
						if (bgetfocus == true
								&& mNetSettingViewHolder.first_dns_three_ev.isEnabled()) {
							/*
							 * InputMethodManager m = (InputMethodManager) mNetSettingViewHolder
							 * .first_dns_three_ev.getContext() .getSystemService
							 * (mNetSettingActivity.INPUT_METHOD_SERVICE); m.toggleSoftInput(0,
							 * InputMethodManager.HIDE_NOT_ALWAYS);
							 */
							if (KeyEvent.KEYCODE_DPAD_RIGHT == mNetSettingActivity.mPreKeyCode) {
								// [2012-1-14yanhd] 将光标移到最后;
								((EditText) myview).setSelection(((EditText) myview).getText()
										.length());
							} else if (KeyEvent.KEYCODE_DPAD_LEFT == mNetSettingActivity.mPreKeyCode) {
								// [2012-1-14yanhd] 将光标移到最前面);
								((EditText) myview).setSelection(0);
							}
							((EditText) myview).selectAll();
						}
					}
				});

		mNetSettingViewHolder.first_dns_four_ev
				.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View myview, boolean bgetfocus) {
						if (bgetfocus == true
								&& mNetSettingViewHolder.first_dns_four_ev.isEnabled()) {
							/*
							 * InputMethodManager m = (InputMethodManager) mNetSettingViewHolder
							 * .first_dns_four_ev.getContext() .getSystemService(
							 * mNetSettingActivity.INPUT_METHOD_SERVICE); m.toggleSoftInput(0,
							 * InputMethodManager.HIDE_NOT_ALWAYS);
							 */
							if (KeyEvent.KEYCODE_DPAD_RIGHT == mNetSettingActivity.mPreKeyCode) {
								// [2012-1-14yanhd] 将光标移到最后;
								((EditText) myview).setSelection(((EditText) myview).getText()
										.length());
							} else if (KeyEvent.KEYCODE_DPAD_LEFT == mNetSettingActivity.mPreKeyCode) {
								// [2012-1-14yanhd] 将光标移到最前面);
								((EditText) myview).setSelection(0);
							}
							((EditText) myview).selectAll();
						}

					}
				});

		// 备用DNS
		mNetSettingViewHolder.second_dns_one_ev
				.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View myview, boolean bgetfocus) {
						if (bgetfocus == true
								&& mNetSettingViewHolder.second_dns_one_ev.isEnabled()) {
							/*
							 * InputMethodManager m = (InputMethodManager) mNetSettingViewHolder
							 * .second_dns_one_ev.getContext() .getSystemService(
							 * mNetSettingActivity.INPUT_METHOD_SERVICE); m.toggleSoftInput(0,
							 * InputMethodManager.HIDE_NOT_ALWAYS);
							 */
							if (KeyEvent.KEYCODE_DPAD_RIGHT == mNetSettingActivity.mPreKeyCode) {
								// [2012-1-14yanhd] 将光标移到最后;
								((EditText) myview).setSelection(((EditText) myview).getText()
										.length());
							} else if (KeyEvent.KEYCODE_DPAD_LEFT == mNetSettingActivity.mPreKeyCode) {
								// [2012-1-14yanhd] 将光标移到最前面);
								((EditText) myview).setSelection(0);
							}
							((EditText) myview).selectAll();
						}
					}
				});

		mNetSettingViewHolder.second_dns_two_ev
				.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View myview, boolean bgetfocus) {
						if (bgetfocus == true
								&& mNetSettingViewHolder.second_dns_two_ev.isEnabled()) {
							/*
							 * InputMethodManager m = (InputMethodManager) mNetSettingViewHolder
							 * .second_dns_two_ev.getContext() .getSystemService(
							 * mNetSettingActivity.INPUT_METHOD_SERVICE); m.toggleSoftInput(0,
							 * InputMethodManager.HIDE_NOT_ALWAYS);
							 */
							if (KeyEvent.KEYCODE_DPAD_RIGHT == mNetSettingActivity.mPreKeyCode) {
								// [2012-1-14yanhd] 将光标移到最后;
								((EditText) myview).setSelection(((EditText) myview).getText()
										.length());
							} else if (KeyEvent.KEYCODE_DPAD_LEFT == mNetSettingActivity.mPreKeyCode) {
								// [2012-1-14yanhd] 将光标移到最前面);
								((EditText) myview).setSelection(0);
							}
							((EditText) myview).selectAll();
						}
					}
				});

		mNetSettingViewHolder.secondt_dns_three_ev
				.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View myview, boolean bgetfocus) {
						if (bgetfocus == true
								&& mNetSettingViewHolder.secondt_dns_three_ev.isEnabled()) {
							/*
							 * InputMethodManager m = (InputMethodManager) mNetSettingViewHolder
							 * .secondt_dns_three_ev.getContext() .getSystemService
							 * (mNetSettingActivity.INPUT_METHOD_SERVICE); m.toggleSoftInput(0,
							 * InputMethodManager.HIDE_NOT_ALWAYS);
							 */
							if (KeyEvent.KEYCODE_DPAD_RIGHT == mNetSettingActivity.mPreKeyCode) {
								// [2012-1-14yanhd] 将光标移到最后;
								((EditText) myview).setSelection(((EditText) myview).getText()
										.length());
							} else if (KeyEvent.KEYCODE_DPAD_LEFT == mNetSettingActivity.mPreKeyCode) {
								// [2012-1-14yanhd] 将光标移到最前面);
								((EditText) myview).setSelection(0);
							}
							((EditText) myview).selectAll();
						}
					}
				});

		mNetSettingViewHolder.second_dns_four_ev
				.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View myview, boolean bgetfocus) {
						if (bgetfocus == true
								&& mNetSettingViewHolder.second_dns_four_ev.isEnabled()) {
							/*
							 * InputMethodManager m = (InputMethodManager) mNetSettingViewHolder
							 * .second_dns_four_ev.getContext() .getSystemService
							 * (mNetSettingActivity.INPUT_METHOD_SERVICE); m.toggleSoftInput(0,
							 * InputMethodManager.HIDE_NOT_ALWAYS);
							 */
							if (KeyEvent.KEYCODE_DPAD_RIGHT == mNetSettingActivity.mPreKeyCode) {
								// [2012-1-14yanhd] 将光标移到最后;
								((EditText) myview).setSelection(((EditText) myview).getText()
										.length());
							} else if (KeyEvent.KEYCODE_DPAD_LEFT == mNetSettingActivity.mPreKeyCode) {
								// [2012-1-14yanhd] 将光标移到最前面);
								((EditText) myview).setSelection(0);
							}
							((EditText) myview).selectAll();
						}
					}
				});

		mNetSettingViewHolder.mIpSaveBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("===>Save");
				boolean isSuccess = mNetSettingActivity.autoSaveNetInput();
				System.out.println("===>isSuccess:" + isSuccess);
				if (isSuccess) { // 2012-03-26
					mNetSettingActivity.atferSaveIpReturnToLeft();
					Toast.makeText(mNetSettingActivity,
							mNetSettingActivity.getString(R.string.notice_save), Toast.LENGTH_LONG)
							.show();
				}
			}
		});
	}

	/**
	 * 是自动获取IP地址时，不可以设置IP地址
	 */
	private void setInisible() {
		mNetSettingViewHolder.setIpInputEnable(false);

		// mNetSettingViewHolder.mAutoIpAddress.setBackgroundResource(R.drawable.open);
		Configuration configuration = mNetSettingActivity.getResources().getConfiguration();
		if (configuration.locale.toString().equals("en_US")) {
			mNetSettingViewHolder.mAutoIpAddress.setBackgroundResource(R.drawable.open_en);
		} else {
			mNetSettingViewHolder.mAutoIpAddress.setBackgroundResource(R.drawable.open);
		}

	}

	/**
	 * 不是自动获取IP地址时，可以设置IP地址
	 */
	private void setVisible() {
		mNetSettingViewHolder.setIpInputEnable(true);

		// mNetSettingViewHolder.mAutoIpAddress.setBackgroundResource(R.drawable.close);
		Configuration configuration = mNetSettingActivity.getResources().getConfiguration();
		if (configuration.locale.toString().equals("en_US")) {
			mNetSettingViewHolder.mAutoIpAddress.setBackgroundResource(R.drawable.close_en);
		} else {
			mNetSettingViewHolder.mAutoIpAddress.setBackgroundResource(R.drawable.close);
		}
	}
}
