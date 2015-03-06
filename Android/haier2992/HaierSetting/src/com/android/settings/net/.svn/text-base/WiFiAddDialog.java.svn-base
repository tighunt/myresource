package com.android.settings.net;

import android.app.Dialog;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;  // change by cwj
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.settings.R;

/**
 * add a custom wifi net
 * 
 * @author ducj(ducj@biaoqi.com.cn)
 * @since 1.0 2011-12-2
 */
public class WiFiAddDialog extends Dialog {

	private final static String TAG = "net";

	private WiFiSignalListActivity mWiFiSignalListActivity;

	private int[] SecType = { R.string.none, R.string.secure_wep, R.string.secure_wpa /*
																					 * ,R.string.
																					 * secure_eap
																					 */};

	private final int SECURE_OPEN = 0;
	private final int SECURE_WEP = 1;
	private final int SECURE_WPA = 2;
	// private final int SECURE_EAP = 3;

	// 添加网络名称编辑框.
	private EditText add_net_et;
	// 设置密码编辑框.
	private EditText code_et;
	// 是否显示密码.
	private CheckBox code_show_cb;
	// 安全性选择
	private Button secure_btn;
	// 保存按钮.
	private Button save_btn;
	// 取消保存按钮.
	private Button cancel_btn;
	// 网络名称编辑布局.
	private RelativeLayout wifi_add_r1;
	// 密码编辑布局.
	private RelativeLayout wifi_add_r2;
	// 密码是否显示布局.
	private RelativeLayout wifi_add_r3;
	// 安全性布局
	private RelativeLayout secure_ly;

	protected ImageView sec_left_img;
	protected ImageView sec_right_img;

	// current secure
	private int mCurrentSecure;

	private Handler mHandler;

	protected static final int MSG_CHECK_WIFI_IS_CONNECTED = 1001;

	public WiFiAddDialog(WiFiSignalListActivity wifiSignalListActivity, Handler handler) {
		super(wifiSignalListActivity);
		this.mWiFiSignalListActivity = wifiSignalListActivity;
		this.mHandler = handler;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifi_add);

		// 获取组件.
		findViews();

		setWindowSize(false);

		// 获取组件监听.
		registerListener();
	}

	@SuppressWarnings("deprecation")
	private void setWindowSize(boolean isBig) {
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
		// 定义窗口的宽和高
		int width = (int) (display.getWidth() * 0.5);
		int height;

		if (isBig) {
			wifi_add_r2.setVisibility(View.VISIBLE);
			wifi_add_r3.setVisibility(View.VISIBLE);
			height = (int) (display.getHeight() * 0.6);
		} else {
			wifi_add_r2.setVisibility(View.GONE);
			wifi_add_r3.setVisibility(View.GONE);
			height = (int) (display.getHeight() * 0.42);
		}
		// 设置窗口的大小
		w.setLayout(width, height);
		// 设置窗口的显示位置
		w.setGravity(Gravity.CENTER);
		// 设置窗口的属性
		WindowManager.LayoutParams wl = w.getAttributes();
		w.setAttributes(wl);
	}

	/*
	 * 初始化组件.
	 */
	private void findViews() {

		// ssid
		add_net_et = (EditText) findViewById(R.id.net_et);
		add_net_et.setHint(R.string.please_input_ssid);
		// password
		code_et = (EditText) findViewById(R.id.code_et);

		// show pwd switch
		Configuration config = mWiFiSignalListActivity.getResources().getConfiguration();
		if (config.locale.toString().equals("en_US")) {
			code_show_cb = (CheckBox) findViewById(R.id.code_show_cb_en);
			code_show_cb.setVisibility(View.VISIBLE);
		} else {
			code_show_cb = (CheckBox) findViewById(R.id.code_show_cb);
			code_show_cb.setVisibility(View.VISIBLE);
		}

		// save
		save_btn = (Button) findViewById(R.id.save_net_ok);

		// cancel
		cancel_btn = (Button) findViewById(R.id.save_net_cancel);

		// secure show
		secure_btn = (Button) findViewById(R.id.wifi_sec_type);

		wifi_add_r1 = (RelativeLayout) findViewById(R.id.wifi_add_r1);
		wifi_add_r1.setBackgroundResource(R.drawable.desktop_button);

		wifi_add_r2 = (RelativeLayout) findViewById(R.id.wifi_add_r2);
		wifi_add_r3 = (RelativeLayout) findViewById(R.id.wifi_add_r3);

		secure_ly = (RelativeLayout) findViewById(R.id.wifi_add_secure_ly);

		sec_left_img = (ImageView) findViewById(R.id.secure_left_img);
		sec_right_img = (ImageView) findViewById(R.id.secure_right_img);
		sec_left_img.setVisibility(View.VISIBLE);
		sec_right_img.setVisibility(View.VISIBLE);

		mCurrentSecure = SECURE_OPEN;
		secure_btn.setText(SecType[mCurrentSecure]);
	}

	// /[2012-1-19 update]
	WifiConfiguration getConfig() {

		WifiConfiguration config = new WifiConfiguration();

		String wifiSSID = (String) add_net_et.getText().toString();
		String password = (String) code_et.getText().toString();
		config.SSID = "\"" + wifiSSID + "\"";
		config.hiddenSSID = true;

		switch (mCurrentSecure) {
		case SECURE_OPEN:
			config.allowedKeyManagement.set(KeyMgmt.NONE);
			break;

		case SECURE_WEP:
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
		case SECURE_WPA:
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
		return config;
	}

	/*
	 * 设置组件监听.
	 */
	private void registerListener() {

		// 保存按钮监听.
		save_btn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {

				String wifiSSID = (String) add_net_et.getText().toString();
				String pwd = (String) code_et.getText().toString();

				if (wifiSSID == null || wifiSSID.length() == 0) {
					add_net_et.setText(R.string.please_input_ssid);
					add_net_et.requestFocus();
					return;
				}

				if (mCurrentSecure == SECURE_WEP && pwd.length() < 5) {
					code_et.setText("");
					code_et.setHint(R.string.modify_wep);
					code_et.requestFocus();
					return;
				}

				if (mCurrentSecure == SECURE_WPA && pwd.length() < 8) {
					code_et.setText("");
					code_et.setHint(R.string.modify_wpa);
					code_et.requestFocus();
					return;
				}

				WifiConfiguration wifiConfiguration = getConfig();

				if (wifiConfiguration != null) {

					Log.d(TAG,
							"WiFiAddDialog.registerListener().onClick.mWifiManager.saveNetwork(wc)......");
					mWiFiSignalListActivity.mWifiManager.save(wifiConfiguration,
							new WifiManager.ActionListener() {
								public void onSuccess() {
								}
								public void onFailure(int reason) {             // chaneg by wu 连接网络
									//TODO: Add failure UI
								}
						});
					mWiFiSignalListActivity.getConfig(wifiConfiguration);
					mWiFiSignalListActivity.getSecore(mCurrentSecure, "ADD");

					// mCurrentSecure != SECURE_OPEN
					if (mCurrentSecure == SECURE_WPA) {
						mWiFiSignalListActivity.showDialog(MSG_CHECK_WIFI_IS_CONNECTED);
					}

					mHandler.sendEmptyMessage(mWiFiSignalListActivity.ADD);

				} else {
					Log.e(TAG, "WiFiAddDialog.registerListener().onClick.config if null");
				}

				dismiss();
			}
		});

		// 取消按钮监听.
		cancel_btn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {

				dismiss();
			}
		});

		add_net_et.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					wifi_add_r1.setBackgroundResource(R.drawable.desktop_button);
				} else {
					wifi_add_r1.setBackgroundResource(R.drawable.one_px);
				}
			}
		});

		code_et.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					wifi_add_r2.setBackgroundResource(R.drawable.desktop_button);
				} else {
					wifi_add_r2.setBackgroundResource(R.drawable.one_px);
				}
			}
		});

		code_show_cb.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					wifi_add_r3.setBackgroundResource(R.drawable.desktop_button);
				} else {
					wifi_add_r3.setBackgroundResource(R.drawable.one_px);
				}
			}
		});

		// /secure type foucus
		secure_btn.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					secure_ly.setBackgroundResource(R.drawable.desktop_button);
				} else {
					secure_ly.setBackgroundResource(R.drawable.one_px);
				}
			}
		});

		secure_btn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				secure_btn.requestFocus();
			}
		});

		sec_left_img.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (mCurrentSecure) {
				case SECURE_OPEN:
					break;
				case SECURE_WEP:
					mCurrentSecure = SECURE_OPEN;
					break;
				case SECURE_WPA:
					mCurrentSecure = SECURE_WEP;
					code_et.setHint(R.string.modify_wep);
					break;
				default:
					mCurrentSecure = SECURE_OPEN;
					code_et.setHint("");
					break;
				}
				secure_btn.setText(SecType[mCurrentSecure]);

				setWindowSize((SECURE_OPEN == mCurrentSecure) ? false : true);
			}
		});

		sec_right_img.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (mCurrentSecure) {
				case SECURE_OPEN:
					mCurrentSecure = SECURE_WEP;
					code_et.setHint(R.string.modify_wep);
					break;
				case SECURE_WEP:
					mCurrentSecure = SECURE_WPA;
					code_et.setHint(R.string.modify_wpa);
					break;
				case SECURE_WPA:
					break;
				default:
					mCurrentSecure = SECURE_OPEN;
					break;
				}
				secure_btn.setText(SecType[mCurrentSecure]);

				setWindowSize((SECURE_OPEN == mCurrentSecure) ? false : true);
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

		// 取消按钮焦点监听.
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (secure_btn.isFocused()) {

			if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
				switch (mCurrentSecure) {
				case SECURE_OPEN:
					mCurrentSecure = SECURE_WEP;
					code_et.setHint(R.string.modify_wep);
					break;
				case SECURE_WEP:
					mCurrentSecure = SECURE_WPA;
					code_et.setHint(R.string.modify_wpa);
					break;
				case SECURE_WPA:
					break;
				default:
					mCurrentSecure = SECURE_OPEN;
					code_et.setHint("");
					break;
				}
				secure_btn.setText(SecType[mCurrentSecure]);

				setWindowSize((SECURE_OPEN == mCurrentSecure) ? false : true);
				return true;
			} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
				switch (mCurrentSecure) {
				case SECURE_OPEN:
					break;
				case SECURE_WEP:
					mCurrentSecure = SECURE_OPEN;
					code_et.setHint("");
					break;
				case SECURE_WPA:
					mCurrentSecure = SECURE_WEP;
					code_et.setHint(R.string.modify_wep);
					break;
				default:
					mCurrentSecure = SECURE_OPEN;
					code_et.setHint("");
					break;
				}
				secure_btn.setText(SecType[mCurrentSecure]);

				setWindowSize((SECURE_OPEN == mCurrentSecure) ? false : true);

				return true;
			} else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
				if (SECURE_OPEN == mCurrentSecure) {
					save_btn.requestFocus();
				}
			} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
				save_btn.clearFocus();
			}
		} else if (wifi_add_r2.isShown() && wifi_add_r3.isShown() && code_show_cb.hasFocus()) {

			if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
				save_btn.requestFocus();
			}
		} else if (wifi_add_r2.isShown() && wifi_add_r3.isShown() && save_btn.hasFocus()) {
			if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
				code_show_cb.requestFocus();
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
