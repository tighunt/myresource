package com.android.settings.net;

import android.content.res.Configuration;
import android.view.View;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.text.TextWatcher;
import android.text.Editable;

import com.android.settings.R;
import com.tvos.settings.adapter.SystemBackAdapter;

/**
 * 网络设置界面中控件的初始化
 * 
 * @author 杜聪甲(ducj@biaoqi.com.cn)
 * @date 2011-11-3 下午07:26:23
 * @since 1.0
 */
public class NetSettingViewHolder {

	private NetSettingActivity mNetSettingActivity;
	// 网络设置的菜单
	protected ListView mNetSetting;
	// 网络设置菜单中数据的初始化
	private SystemBackAdapter mSystemBackAdapter;
	// 当前网络设置中所在的子菜单的名称
	protected TextView mNetCurrentPostion;
	// 网络状态设置
	protected LinearLayout mNetState;
	// 无线和有线网络ip设置
	protected LinearLayout mWirelessSettingIp;
	// /ip input
	protected LinearLayout mIpInputLyout;
	// /wifi switch layout
	protected RelativeLayout mWifiSwitchLy;
	// wifi switch checkbox
	protected CheckBox mWifiSwitchCb;
	// 是否自动获取IP地址
	protected RelativeLayout mGetIpRelativeLayout;
	// SSID设置
	protected RelativeLayout mSSIDRelativeLayout;
	// IP地址
	protected RelativeLayout mIpAddressRelativeLayout;
	// 子网掩码
	protected RelativeLayout mSubnetRelativeLayout;
	// 默认网关
	protected RelativeLayout mDefaultGatewayRelativeLayout;
	// 首选DNS
	protected RelativeLayout mFirstDnsRelativeLayout;
	// 备用DNS
	protected RelativeLayout mSecondDnsRelativeLayout;
	// 判断是否自动获取IP地址
	protected Button mAutoIpAddress;
	// /若没有切换到有线，则提示。
	protected TextView mNoticeWire;
	// PPPOE设置
	protected LinearLayout mPPPOESetting;
	// 网络状态中连接方式左右箭头
	// protected ImageView mConnectFormat_left;
	// protected ImageView mConnectFormat_right;
	// PPPOE 正在拨号
	protected TextView isDialing;
	// PPPOE验证用户名和密码
	// protected RelativeLayout mCheckPWDName;
	// PPPOE 连接成功
	// protected RelativeLayout mConnectSuccess;
	// 连接方式
	protected RelativeLayout mConnectFormatRelativeLayout;
	// PPPOE拨号
	// protected RelativeLayout mPPPOERelativeLayout;
	// PPPOE自动获取IP地址
	protected RelativeLayout mPPPOEAutoIPRelativeLayout;
	// PPPOE设置中自动获取IP地址左右箭头
	protected ImageView mPPPOE_auto_ip_left;
	protected ImageView mPPPOE_auto_ip_right;
	// PPPOE用户名设置
	protected RelativeLayout mPPPOEUsernameRelativeLayout;
	// PPPOE密码设置
	protected RelativeLayout mPPPOEPasswordRelativeLayout;
	// PPPOE密码显示
	protected RelativeLayout mPasswordShowRelativeLayout;
	// 判断是否以明文显示密码
	protected CheckBox mShowPassword;
	// PPPOE自动拨号
	// protected RelativeLayout mAutoDialerRelativeLayout;
	// 拨号
	protected Button mDialerActionBtn;
	// 挂断。
	protected Button mDialerHangupBtn;
	protected TextView mDialerStatusTxt;
	// 判断是否自动拨号
	// protected CheckBox mAutoPPPOEDialer;
	// PPPOE密码输入框
	protected EditText mPassWordEditText;
	// PPPOE用户名输入框
	protected EditText mUsernameEditText;
	// 网络状态连接方式
	protected Button mConnectFormat;
	// PPPOE拨号
	// protected Button mPPPOEDialer;
	// PPPOE连接方式
	protected Button mPPPOEConnetFormat;
	// 无线设置SSID
	protected Button mSSID;
	// wifi-direct
	protected LinearLayout mdirectLlayout;
	// wifi-directwifi_direct_discover
	protected RelativeLayout mdirectSwitchLlayout;
	// wifi-direct checkbox
	protected CheckBox mdirectCheckbox;
	// wifi-driect TextView;
	protected TextView mDeivceinfoTxt;
	// device discover
	protected RelativeLayout mDeviceDisvocerLlayout;
	// /discover actoin button
	protected Button mDiscoverButton;

	protected TextView mDiscoverNoticeTxt;

	// /wifi hotspot
	protected LinearLayout mWifiHotspotLy;
	// /wifi hotspot switch
	protected RelativeLayout mWifiApSwitchLy;
	// /wifi hotspot switch checkbox
	protected CheckBox mWifiApSwitchCb;
	// /show wiif ap config info
	protected TextView mWifiApInfo;
	// /wifi hotspot Setting
	protected Button mWifiApSetBtn;
	// /wifi hotspot config ly
	protected LinearLayout mWifiApConfigLy;
	// /wifi hotspot ssid
	protected EditText mWifiApSSID;
	// //wifi hotspot secure
	protected RelativeLayout mWifiApSecureLy;
	// //wifi hotspot password layout
	protected RelativeLayout mWifiApPwdLy;
	// /secure type show
	protected Button mWifiApSecButton;
	// /wifi hotspot pwd
	protected EditText mWifiApPwd;
	// /wifi hotspot show pwd
	protected CheckBox mWifiApShowPwdCb;
	// /wifi hotspot pwd
	protected Button mWifiApSaveBtn;
	// /show hotspot pwd layout
	protected RelativeLayout mWifiApShowPwdLy;
	// /Location service
	protected LinearLayout mLocationServiceLy;
	// /Location service switch
	protected CheckBox mLocServiceSwitch;
	// /Location service switch layout
	protected RelativeLayout mLocServiceSwitchLy;

	protected ImageView mWifiApSecleft;

	protected ImageView mWifiApSecright;

	// IP地址
	protected EditText ip_one_ev;
	protected EditText ip_two_ev;
	protected EditText ip_three_ev;
	protected EditText ip_four_ev;
	// 子网掩码
	protected EditText subnet_one_ev;
	protected EditText subnet_two_ev;
	protected EditText subnet_three_ev;
	protected EditText subnet_four_ev;
	// 默认网关
	protected EditText defult_one_ev;
	protected EditText defult_two_ev;
	protected EditText defult_three_ev;
	protected EditText defult_four_ev;
	// 首选DNS
	protected EditText first_dns_one_ev;
	protected EditText first_dns_two_ev;
	protected EditText first_dns_three_ev;
	protected EditText first_dns_four_ev;
	// 备用DNS
	protected EditText second_dns_one_ev;
	protected EditText second_dns_two_ev;
	protected EditText secondt_dns_three_ev;
	protected EditText second_dns_four_ev;

	// 网络状态IP地址
	protected TextView ip_one_state;
	protected TextView ip_two_state;
	protected TextView ip_three_state;
	protected TextView ip_four_state;
	// 网络状态子网掩码
	protected TextView subnet_one_state;
	protected TextView subnet_two_state;
	protected TextView subnet_three_state;
	protected TextView subnet_four_state;
	// 网络状态默认网关
	protected TextView defult_one_state;
	protected TextView defult_two_state;
	protected TextView defult_three_state;
	protected TextView defult_four_state;
	// 网络状态首选DNS
	protected TextView first_dns_one_state;
	protected TextView first_dns_two_state;
	protected TextView first_dns_three_state;
	protected TextView first_dns_four_state;
	// /网络状态备用DNS[2012-3-29 yanhd]
	protected TextView second_dns_one_state;
	protected TextView second_dns_two_state;
	protected TextView second_dns_three_state;
	protected TextView second_dns_four_state;

	// 有线网络状态MAC地址
	protected TextView mac_address_state;
	// 无线线网络状态MAC地址
	protected TextView mac_address_state_wifi;

	// 网络状态有线MAC地址/无线MAC地址
	// protected TextView mac_txt_state;

	// 保存ip
	protected Button mIpSaveBtn;

	// 网络状态中有线连接
	protected LinearLayout mWireNetState;
	// 有线连接失败
	protected TextView mWireConFailure;
	// IP正确
	protected RelativeLayout ip_match;
	// 默认网关正确
	protected RelativeLayout default_match;
	// 子网掩码正确
	protected RelativeLayout net_mask_match;
	// DNS正确
	protected RelativeLayout dns_match;
	// 显示有线网卡的状态
	protected TextView wire_net_state;
	// 网络状态中无线连接
	protected LinearLayout mWirelessNetState;
	// 网络状态中PPPOE连接
	protected LinearLayout mPPPOENetState;
	// 网络状态中无线连接
	protected LinearLayout mWiFiNetState;
	protected RelativeLayout check_ssid_pwd_rl;
	protected RelativeLayout wifi_connect_success_rl;
	// show wifi state
	protected TextView wifi_state;

	public NetSettingViewHolder(NetSettingActivity netSettingActivity) {
		this.mNetSettingActivity = netSettingActivity;
		findViews();
	}


/*	TextWatcher tw = new TextWatcher(){
		//@Override
		public void beforeTextChanged(CharSequence s, int start, int count,int after){ 
		}
		//@Override
		public void onTextChanged(CharSequence s, int start, int before, int count){ 
		}
		//@Override
		public void afterTextChanged(Editable s){
			if(s.toString().length() == 3)
			{ 
				if(ip_one_ev.isFocused())
				{ 
					ip_one_ev.clearFocus();
					ip_two_ev.requestFocus();
				}
				else if(ip_two_ev.isFocused())
				{ 
					ip_two_ev.clearFocus(); 
					ip_three_ev.requestFocus();
				}
				else if(ip_three_ev.isFocused())
				{ 
					ip_three_ev.clearFocus();
					ip_four_ev.requestFocus();
				} 
				else if(ip_four_ev.isFocused())
				{ 
					ip_four_ev.clearFocus();
					subnet_one_ev.requestFocus();
				} 
				else if(subnet_one_ev.isFocused())
				{ 
					subnet_one_ev.clearFocus();
					subnet_two_ev.requestFocus();
				}
				else if(subnet_two_ev.isFocused())
				{ 
					subnet_two_ev.clearFocus();
					subnet_three_ev.requestFocus();
				}
				else if(subnet_three_ev.isFocused())
				{ 
					subnet_three_ev.clearFocus();
					subnet_four_ev.requestFocus();
				}
				else if(subnet_four_ev.isFocused())
				{ 
					subnet_four_ev.clearFocus();
					defult_one_ev.requestFocus();
				}
				else if(defult_one_ev.isFocused())
				{ 
					defult_one_ev.clearFocus();
					defult_two_ev.requestFocus();
				}
				else if(defult_two_ev.isFocused())
				{ 
					defult_two_ev.clearFocus();
					defult_three_ev.requestFocus();
				}
				else if(defult_three_ev.isFocused())
				{ 
					defult_three_ev.clearFocus();
					defult_four_ev.requestFocus();
				}
				else if(defult_four_ev.isFocused())
				{ 
					defult_four_ev.clearFocus();
					first_dns_one_ev.requestFocus();
				}
				else if(first_dns_one_ev.isFocused())
				{ 
					first_dns_one_ev.clearFocus();
					first_dns_two_ev.requestFocus();
				}
				else if(first_dns_two_ev.isFocused())
				{ 
					first_dns_two_ev.clearFocus();
					first_dns_three_ev.requestFocus();
				}
				else if(first_dns_three_ev.isFocused())
				{ 
					first_dns_three_ev.clearFocus();
					first_dns_four_ev.requestFocus();
				} 
			}
		}
	}; */
	/**
	 * 控件的初始化
	 */
	private void findViews() {
		mNetSetting = (ListView) mNetSettingActivity.findViewById(R.id.net_list_select);
		mSystemBackAdapter = new SystemBackAdapter(mNetSettingActivity, new String[] {
				mNetSettingActivity.getResources().getString(R.string.net_state),
				mNetSettingActivity.getResources().getString(R.string.wire_setting),
				mNetSettingActivity.getResources().getString(R.string.wireless_setting),
				// delete pppoe 2012-05-23
				// mNetSettingActivity.getResources().getString(R.string.dial_setting),
				mNetSettingActivity.getResources().getString(R.string.wifi_direct),
				mNetSettingActivity.getResources().getString(R.string.wifi_hotspot)
		// , mNetSettingActivity.getResources().getString(R.string.location_service)
				});
		mNetSetting.setDividerHeight(0);
		mNetSetting.setAdapter(mSystemBackAdapter);
		// mNetCurrentPostion = (TextView)
		// mNetSettingActivity.findViewById(R.id.net_current_position);
		mIpInputLyout = (LinearLayout) mNetSettingActivity.findViewById(R.id.wireless_net_ip_ll);
		mWirelessSettingIp = (LinearLayout) mNetSettingActivity
				.findViewById(R.id.wireless_setting_ll);
		mNetState = (LinearLayout) mNetSettingActivity.findViewById(R.id.net_state_ll);
		mPPPOESetting = (LinearLayout) mNetSettingActivity.findViewById(R.id.pppoe_setting_ll);

		// mWifiSwitchCb = (CheckBox) mNetSettingActivity.findViewById(R.id.wifi_switch_checkbox);
		Configuration config = mNetSettingActivity.getResources().getConfiguration();
		if (config.locale.toString().equals("en_US")) {
			mWifiSwitchCb = (CheckBox) mNetSettingActivity
					.findViewById(R.id.wifi_switch_checkbox_en);
			mWifiSwitchCb.setVisibility(View.VISIBLE);
		} else {
			mWifiSwitchCb = (CheckBox) mNetSettingActivity.findViewById(R.id.wifi_switch_checkbox);
			mWifiSwitchCb.setVisibility(View.VISIBLE);
		}

		mWifiSwitchLy = (RelativeLayout) mNetSettingActivity.findViewById(R.id.wifi_switch_rl);

		mSSIDRelativeLayout = (RelativeLayout) mNetSettingActivity.findViewById(R.id.ssid_rl);
		mGetIpRelativeLayout = (RelativeLayout) mNetSettingActivity
				.findViewById(R.id.net_auto_ip_rl);
		mIpAddressRelativeLayout = (RelativeLayout) mNetSettingActivity
				.findViewById(R.id.ip_address_rl);
		mSubnetRelativeLayout = (RelativeLayout) mNetSettingActivity
				.findViewById(R.id.subnet_mask_rl);
		mDefaultGatewayRelativeLayout = (RelativeLayout) mNetSettingActivity
				.findViewById(R.id.default_gateway_rl);
		mFirstDnsRelativeLayout = (RelativeLayout) mNetSettingActivity
				.findViewById(R.id.first_dns_rl);
		mSecondDnsRelativeLayout = (RelativeLayout) mNetSettingActivity
				.findViewById(R.id.second_dns_rl);
		mNoticeWire = (TextView) mNetSettingActivity.findViewById(R.id.notice_wire_status);

		mAutoIpAddress = (Button) mNetSettingActivity.findViewById(R.id.checkbox_auto_ip);

		// mConnectFormat_left = (ImageView) mNetSettingActivity.findViewById(R.id.left_arrowhead);
		// mConnectFormat_right = (ImageView)
		// mNetSettingActivity.findViewById(R.id.right_arrowhead);
		mConnectFormatRelativeLayout = (RelativeLayout) mNetSettingActivity
				.findViewById(R.id.connect_format_rl);
		// mPPPOERelativeLayout = (RelativeLayout)
		// mNetSettingActivity.findViewById(R.id.pppoe_dialer_rl);
		mPPPOEAutoIPRelativeLayout = (RelativeLayout) mNetSettingActivity
				.findViewById(R.id.pppoe_auto_ip_rl);
		mPPPOEUsernameRelativeLayout = (RelativeLayout) mNetSettingActivity
				.findViewById(R.id.username_rl);
		mPPPOE_auto_ip_left = (ImageView) mNetSettingActivity.findViewById(R.id.p_left_arrowhead);
		mPPPOE_auto_ip_right = (ImageView) mNetSettingActivity.findViewById(R.id.p_right_arrowhead);
		mPPPOEPasswordRelativeLayout = (RelativeLayout) mNetSettingActivity
				.findViewById(R.id.password_rl);
		mPasswordShowRelativeLayout = (RelativeLayout) mNetSettingActivity
				.findViewById(R.id.show_password_rl);
		// mAutoDialerRelativeLayout = (RelativeLayout)
		// mNetSettingActivity.findViewById(R.id.auto_dialer_rl);
		mDialerActionBtn = (Button) mNetSettingActivity.findViewById(R.id.dialer_ok); // [2012-2-16add]
		mDialerHangupBtn = (Button) mNetSettingActivity.findViewById(R.id.dialer_hangup); // [2012-2-16add]
		mDialerStatusTxt = (TextView) mNetSettingActivity.findViewById(R.id.dialer_status);// [2012-2-16add]

		// mShowPassword = (CheckBox) mNetSettingActivity.findViewById(R.id.show_password);
		if (config.locale.toString().equals("en_US")) {
			mShowPassword = (CheckBox) mNetSettingActivity.findViewById(R.id.show_password_en);
			mShowPassword.setVisibility(View.VISIBLE);
		} else {
			mShowPassword = (CheckBox) mNetSettingActivity.findViewById(R.id.show_password);
			mShowPassword.setVisibility(View.VISIBLE);
		}

		// mAutoPPPOEDialer = (CheckBox) mNetSettingActivity.findViewById(R.id.auto_dialer_cb);
		mPassWordEditText = (EditText) mNetSettingActivity.findViewById(R.id.password_et);
		mUsernameEditText = (EditText) mNetSettingActivity.findViewById(R.id.username_et);
		mConnectFormat = (Button) mNetSettingActivity.findViewById(R.id.connect_type);
		// mPPPOEDialer = (Button) mNetSettingActivity.findViewById(R.id.pp_connect_type);
		mPPPOEConnetFormat = (Button) mNetSettingActivity.findViewById(R.id.pppoe_auto_ip);
		mSSID = (Button) mNetSettingActivity.findViewById(R.id.wireless_net_name);

		// mConnectSuccess = (RelativeLayout)
		// mNetSettingActivity.findViewById(R.id.connect_success_rl);
		// mCheckPWDName = (RelativeLayout) mNetSettingActivity.findViewById(R.id.check_pwd_name);

		isDialing = (TextView) mNetSettingActivity.findViewById(R.id.isDialer);

		mWireNetState = (LinearLayout) mNetSettingActivity.findViewById(R.id.net_state_wire);
		mWirelessNetState = (LinearLayout) mNetSettingActivity
				.findViewById(R.id.net_state_wireless);
		mPPPOENetState = (LinearLayout) mNetSettingActivity.findViewById(R.id.net_state_pppoe);
		wire_net_state = (TextView) mNetSettingActivity.findViewById(R.id.wire_net_state);

		// [2012-2-22add]
		mdirectLlayout = (LinearLayout) mNetSettingActivity.findViewById(R.id.wifi_direct_layout);

		// Configuration config = mNetSettingActivity.getResources().getConfiguration();
		if (config.locale.toString().equals("en_US")) // zhf
		{
			mdirectCheckbox = (CheckBox) mNetSettingActivity
					.findViewById(R.id.wifi_diect_checkbox_en);
			mdirectCheckbox.setVisibility(View.VISIBLE);
		} else {
			mdirectCheckbox = (CheckBox) mNetSettingActivity.findViewById(R.id.wifi_diect_checkbox);
			mdirectCheckbox.setVisibility(View.VISIBLE);
		}

		// mdirectCheckbox = (CheckBox)mNetSettingActivity.findViewById(R.id.wifi_diect_checkbox);
		mDeivceinfoTxt = (TextView) mNetSettingActivity.findViewById(R.id.wifi_diect_deviceinfo);
		mdirectSwitchLlayout = (RelativeLayout) mNetSettingActivity
				.findViewById(R.id.wifi_direct_switch);
		mDeviceDisvocerLlayout = (RelativeLayout) mNetSettingActivity
				.findViewById(R.id.wifi_direct_discover);

		// mDiscoverButton = (Button)mNetSettingActivity.findViewById(R.id.device_discover_btn);
		if (config.locale.toString().equals("en_US")) {
			mDiscoverButton = (Button) mNetSettingActivity
					.findViewById(R.id.device_discover_btn_en);
			mDiscoverButton.setVisibility(View.VISIBLE);
		} else {
			mDiscoverButton = (Button) mNetSettingActivity.findViewById(R.id.device_discover_btn);
			mDiscoverButton.setVisibility(View.VISIBLE);
		}

		mDiscoverNoticeTxt = (TextView) mNetSettingActivity.findViewById(R.id.wifi_direct_notice);

		// /[2012-3-8]
		mWifiHotspotLy = (LinearLayout) mNetSettingActivity.findViewById(R.id.wifi_hotspot_layout);
		mWifiApSwitchLy = (RelativeLayout) mNetSettingActivity
				.findViewById(R.id.wifi_hotspot_switch);

		if (config.locale.toString().equals("en_US")) // zhf
		{
			mWifiApSwitchCb = (CheckBox) mNetSettingActivity
					.findViewById(R.id.wifi_hotspot_checkbox_en);
			mWifiApSwitchCb.setVisibility(View.VISIBLE);
		} else {
			mWifiApSwitchCb = (CheckBox) mNetSettingActivity
					.findViewById(R.id.wifi_hotspot_checkbox);
			mWifiApSwitchCb.setVisibility(View.VISIBLE);
		}

		// mWifiApSwitchCb = (CheckBox)mNetSettingActivity.findViewById(R.id.wifi_hotspot_checkbox);
		mWifiApSetBtn = (Button) mNetSettingActivity.findViewById(R.id.wifi_hotspot_config);
		mWifiApConfigLy = (LinearLayout) mNetSettingActivity
				.findViewById(R.id.wifi_hotspot_config_layout);
		mWifiApInfo = (TextView) mNetSettingActivity.findViewById(R.id.wifi_hotspot_info);
		mWifiApSSID = (EditText) mNetSettingActivity.findViewById(R.id.wifi_hotspot_ssid);
		mWifiApSecureLy = (RelativeLayout) mNetSettingActivity
				.findViewById(R.id.wifi_hotspot_secure_ly);

		// / android:background="@drawable/one_px"
		mWifiApSecButton = (Button) mNetSettingActivity.findViewById(R.id.secure_type);
		mWifiApPwd = (EditText) mNetSettingActivity.findViewById(R.id.wifi_hotspot_pwd);

		if (config.locale.toString().equals("en_US")) // zhf
		{
			mWifiApShowPwdCb = (CheckBox) mNetSettingActivity
					.findViewById(R.id.hotspot_show_password_en);
			mWifiApShowPwdCb.setVisibility(View.VISIBLE);
		} else {
			mWifiApShowPwdCb = (CheckBox) mNetSettingActivity
					.findViewById(R.id.hotspot_show_password);
			mWifiApShowPwdCb.setVisibility(View.VISIBLE);
		}

		// mWifiApShowPwdCb =
		// (CheckBox)mNetSettingActivity.findViewById(R.id.hotspot_show_password);
		mWifiApPwdLy = (RelativeLayout) mNetSettingActivity.findViewById(R.id.wifiap_password_rl);
		mWifiApShowPwdLy = (RelativeLayout) mNetSettingActivity
				.findViewById(R.id.wifiap_show_password_rl);
		mWifiApSaveBtn = (Button) mNetSettingActivity.findViewById(R.id.hotspot_save_btn);
		mWifiApSecleft = (ImageView) mNetSettingActivity.findViewById(R.id.secure_left_arrowhead);
		mWifiApSecright = (ImageView) mNetSettingActivity.findViewById(R.id.secure_right_arrowhead);

		// /Location service
		mLocationServiceLy = (LinearLayout) mNetSettingActivity
				.findViewById(R.id.location_service_ly);

		if (config.locale.toString().equals("en_US")) // zhf
		{
			mLocServiceSwitch = (CheckBox) mNetSettingActivity
					.findViewById(R.id.net_location_switch_en);
			mLocServiceSwitch.setVisibility(View.VISIBLE);
		} else {
			mLocServiceSwitch = (CheckBox) mNetSettingActivity
					.findViewById(R.id.net_location_switch);
			mLocServiceSwitch.setVisibility(View.VISIBLE);
		}

		// mLocServiceSwitch =(CheckBox)mNetSettingActivity.findViewById(R.id.net_location_switch);
		mLocServiceSwitchLy = (RelativeLayout) mNetSettingActivity
				.findViewById(R.id.net_location_ly);

		ip_one_ev = (EditText) mNetSettingActivity.findViewById(R.id.wireless_ip_one);
		ip_two_ev = (EditText) mNetSettingActivity.findViewById(R.id.wireless_ip_two);
		ip_three_ev = (EditText) mNetSettingActivity.findViewById(R.id.wireless_ip_three);
		ip_four_ev = (EditText) mNetSettingActivity.findViewById(R.id.wireless_ip_four);
		
		subnet_one_ev = (EditText) mNetSettingActivity.findViewById(R.id.wireless_subnet_one);
		subnet_two_ev = (EditText) mNetSettingActivity.findViewById(R.id.wireless_subnet_two);
		subnet_three_ev = (EditText) mNetSettingActivity.findViewById(R.id.wireless_subnet_three);
		subnet_four_ev = (EditText) mNetSettingActivity.findViewById(R.id.wireless_subnet_four);
		
		defult_one_ev = (EditText) mNetSettingActivity.findViewById(R.id.wireless_geteway_one);
		defult_two_ev = (EditText) mNetSettingActivity.findViewById(R.id.wireless_geteway_two);
		defult_three_ev = (EditText) mNetSettingActivity.findViewById(R.id.wireless_geteway_three);
		defult_four_ev = (EditText) mNetSettingActivity.findViewById(R.id.wireless_geteway_four);

		first_dns_one_ev = (EditText) mNetSettingActivity.findViewById(R.id.wireless_first_one);
		first_dns_two_ev = (EditText) mNetSettingActivity.findViewById(R.id.wireless_first_two);
		first_dns_three_ev = (EditText) mNetSettingActivity.findViewById(R.id.wireless_first_three);
		first_dns_four_ev = (EditText) mNetSettingActivity.findViewById(R.id.wireless_first_four);
				    
/*    ip_one_ev.addTextChangedListener(tw);
		ip_two_ev.addTextChangedListener(tw);
		ip_three_ev.addTextChangedListener(tw);
		ip_four_ev.addTextChangedListener(tw);
    
    subnet_one_ev.addTextChangedListener(tw);
		subnet_two_ev.addTextChangedListener(tw);
		subnet_three_ev.addTextChangedListener(tw);
		subnet_four_ev.addTextChangedListener(tw);
		
		defult_one_ev.addTextChangedListener(tw);
		defult_two_ev.addTextChangedListener(tw);
		defult_three_ev.addTextChangedListener(tw);
		defult_four_ev.addTextChangedListener(tw);
		
		first_dns_one_ev.addTextChangedListener(tw);
		first_dns_two_ev.addTextChangedListener(tw);
		first_dns_three_ev.addTextChangedListener(tw);
		first_dns_four_ev.addTextChangedListener(tw);
*/
		second_dns_one_ev = (EditText) mNetSettingActivity.findViewById(R.id.wireless_second_one);
		second_dns_two_ev = (EditText) mNetSettingActivity.findViewById(R.id.wireless_second_two);
		secondt_dns_three_ev = (EditText) mNetSettingActivity.findViewById(R.id.wireless_second_three);
		second_dns_four_ev = (EditText) mNetSettingActivity.findViewById(R.id.wireless_second_four);
		// network state 2012-03-16
		ip_one_state = (TextView) mNetSettingActivity.findViewById(R.id.net_state_ip_one);
		ip_two_state = (TextView) mNetSettingActivity.findViewById(R.id.net_state_ip_two);
		ip_three_state = (TextView) mNetSettingActivity.findViewById(R.id.net_state_ip_three);
		ip_four_state = (TextView) mNetSettingActivity.findViewById(R.id.net_state_ip_four);

		subnet_one_state = (TextView) mNetSettingActivity.findViewById(R.id.net_state_subnet_one);
		subnet_two_state = (TextView) mNetSettingActivity.findViewById(R.id.net_state_subnet_two);
		subnet_three_state = (TextView) mNetSettingActivity
				.findViewById(R.id.net_state_subnet_three);
		subnet_four_state = (TextView) mNetSettingActivity.findViewById(R.id.net_state_subnet_four);

		defult_one_state = (TextView) mNetSettingActivity.findViewById(R.id.net_state_geteway_one);
		defult_two_state = (TextView) mNetSettingActivity.findViewById(R.id.net_state_geteway_two);
		defult_three_state = (TextView) mNetSettingActivity
				.findViewById(R.id.net_state_geteway_three);
		defult_four_state = (TextView) mNetSettingActivity
				.findViewById(R.id.net_state_geteway_four);

		first_dns_one_state = (TextView) mNetSettingActivity.findViewById(R.id.net_state_first_one);
		first_dns_two_state = (TextView) mNetSettingActivity.findViewById(R.id.net_state_first_two);
		first_dns_three_state = (TextView) mNetSettingActivity
				.findViewById(R.id.net_state_first_three);
		first_dns_four_state = (TextView) mNetSettingActivity
				.findViewById(R.id.net_state_first_four);

		second_dns_one_state = (TextView) mNetSettingActivity
				.findViewById(R.id.net_state_second_one);
		second_dns_two_state = (TextView) mNetSettingActivity
				.findViewById(R.id.net_state_second_two);
		second_dns_three_state = (TextView) mNetSettingActivity
				.findViewById(R.id.net_state_second_three);
		second_dns_four_state = (TextView) mNetSettingActivity
				.findViewById(R.id.net_state_second_four);

		mac_address_state_wifi = (TextView) mNetSettingActivity
				.findViewById(R.id.net_state_mac2_address);
		mac_address_state = (TextView) mNetSettingActivity.findViewById(R.id.net_state_mac_address);
		// mac_txt_state = (TextView) mNetSettingActivity.findViewById(R.id.net_state_mac_txt);

		mIpSaveBtn = (Button) mNetSettingActivity.findViewById(R.id.ip_save_btn);

		mWiFiNetState = (LinearLayout) mNetSettingActivity.findViewById(R.id.net_state_wireless);
		wifi_state = (TextView) mNetSettingActivity.findViewById(R.id.wifi_state);
	}

	/**
	 * 当有线和无线设置是自动获取IP地址时，则所有的ip地址置为空
	 */
	public void resetIPs() {
		ip_one_ev.setText("");
		ip_two_ev.setText("");
		ip_three_ev.setText("");
		ip_four_ev.setText("");

		defult_one_ev.setText("");
		defult_two_ev.setText("");
		defult_three_ev.setText("");
		defult_four_ev.setText("");

		subnet_one_ev.setText("");
		subnet_two_ev.setText("");
		subnet_three_ev.setText("");
		subnet_four_ev.setText("");

		first_dns_one_ev.setText("");
		first_dns_two_ev.setText("");
		first_dns_three_ev.setText("");
		first_dns_four_ev.setText("");

		second_dns_one_ev.setText("");
		second_dns_two_ev.setText("");
		secondt_dns_three_ev.setText("");
		second_dns_four_ev.setText("");
	}

	public void resetIPs1() {
		ip_one_state.setText("");
		ip_two_state.setText("");
		ip_three_state.setText("");
		ip_four_state.setText("");

		defult_one_state.setText("");
		defult_two_state.setText("");
		defult_three_state.setText("");
		defult_four_state.setText("");

		subnet_one_state.setText("");
		subnet_two_state.setText("");
		subnet_three_state.setText("");
		subnet_four_state.setText("");

		first_dns_one_state.setText("");
		first_dns_two_state.setText("");
		first_dns_three_state.setText("");
		first_dns_four_state.setText("");

		second_dns_one_state.setText("");
		second_dns_two_state.setText("");
		second_dns_three_state.setText("");
		second_dns_four_state.setText("");

		mac_address_state.setText("");
		mac_address_state_wifi.setText("");
		/*
		 * mac_address_state.setVisibility(View.VISIBLE); mac_txt_state.setVisibility(View.VISIBLE);
		 * mac_address_state.setText("");
		 */

		/*
		 * if (mNetSettingActivity.currentConnectFormat == 0) {
		 * mac_txt_state.setText(R.string.wire_mac_address); } else if
		 * (mNetSettingActivity.currentConnectFormat == 1) {
		 * mac_txt_state.setText(R.string.wireless_mac_address); }
		 */

	}

	// set input enable
	public void setIpInputEnable(boolean isEnable) {
		if (!isEnable) {
			// mAutoIpAddress.requestFocus();
			// mGetIpRelativeLayout.setBackgroundResource(R.drawable.desktop_button);
			clearFocus();
			mIpAddressRelativeLayout.setBackgroundResource(R.drawable.one_px);
			mSubnetRelativeLayout.setBackgroundResource(R.drawable.one_px);
			mDefaultGatewayRelativeLayout.setBackgroundResource(R.drawable.one_px);
			mFirstDnsRelativeLayout.setBackgroundResource(R.drawable.one_px);
			mSecondDnsRelativeLayout.setBackgroundResource(R.drawable.one_px);
			FocusChange.mFocusLocal = Constants.IS_AUTO_IP_ADDRESS;
		}
		ip_one_ev.setEnabled(isEnable);
		ip_two_ev.setEnabled(isEnable);
		ip_three_ev.setEnabled(isEnable);
		ip_four_ev.setEnabled(isEnable);

		defult_one_ev.setEnabled(isEnable);
		defult_two_ev.setEnabled(isEnable);
		defult_three_ev.setEnabled(isEnable);
		defult_four_ev.setEnabled(isEnable);

		subnet_one_ev.setEnabled(isEnable);
		subnet_two_ev.setEnabled(isEnable);
		subnet_three_ev.setEnabled(isEnable);
		subnet_four_ev.setEnabled(isEnable);

		first_dns_one_ev.setEnabled(isEnable);
		first_dns_two_ev.setEnabled(isEnable);
		first_dns_three_ev.setEnabled(isEnable);
		first_dns_four_ev.setEnabled(isEnable);

		second_dns_one_ev.setEnabled(isEnable);
		second_dns_two_ev.setEnabled(isEnable);
		secondt_dns_three_ev.setEnabled(isEnable);
		second_dns_four_ev.setEnabled(isEnable);
	}

	/**
	 * get ip address
	 * 
	 * @return
	 */
	public String ip() {

		return ip_one_ev.getText().toString().trim() + "." + ip_two_ev.getText().toString().trim()
				+ "." + ip_three_ev.getText().toString().trim() + "."
				+ ip_four_ev.getText().toString().trim();
	}

	/**
	 * get net mask
	 */
	public String netMask() {

		return subnet_one_ev.getText().toString().trim() + "."
				+ subnet_two_ev.getText().toString().trim() + "."
				+ subnet_three_ev.getText().toString().trim() + "."
				+ subnet_four_ev.getText().toString().trim();
	}

	/**
	 * get default way
	 * 
	 * @return
	 */
	public String defautlWay() {

		return defult_one_ev.getText().toString().trim() + "."
				+ defult_two_ev.getText().toString().trim() + "."
				+ defult_three_ev.getText().toString().trim() + "."
				+ defult_four_ev.getText().toString().trim();
	}

	/**
	 * get first dns
	 * 
	 * @return
	 */
	public String fistDns() {

		return first_dns_one_ev.getText().toString().trim() + "."
				+ first_dns_two_ev.getText().toString().trim() + "."
				+ first_dns_three_ev.getText().toString().trim() + "."
				+ first_dns_four_ev.getText().toString().trim();
	}

	/**
	 * get second dns
	 * 
	 * @return
	 */
	public String secondDns() {

		return second_dns_one_ev.getText().toString().trim() + "."
				+ second_dns_two_ev.getText().toString().trim() + "."
				+ secondt_dns_three_ev.getText().toString().trim() + "."
				+ second_dns_four_ev.getText().toString().trim();
	}

	/**
	 * when intert the netwire , clear all focus
	 * 
	 */
	private void clearFocus() {
		
		ip_one_ev.clearFocus(); 
		ip_two_ev.clearFocus(); 
		ip_three_ev.clearFocus(); 
		ip_four_ev.clearFocus(); 

		defult_one_ev.clearFocus(); 
		defult_two_ev.clearFocus(); 
		defult_three_ev.clearFocus(); 
		defult_four_ev.clearFocus(); 

		subnet_one_ev.clearFocus(); 
		subnet_two_ev.clearFocus(); 
		subnet_three_ev.clearFocus(); 
		subnet_four_ev.clearFocus(); 

		first_dns_one_ev.clearFocus(); 
		first_dns_two_ev.clearFocus(); 
		first_dns_three_ev.clearFocus(); 
		first_dns_four_ev.clearFocus(); 

		second_dns_one_ev.clearFocus(); 
		second_dns_two_ev.clearFocus(); 
		secondt_dns_three_ev.clearFocus(); 
		second_dns_four_ev.clearFocus(); 
	}
}
