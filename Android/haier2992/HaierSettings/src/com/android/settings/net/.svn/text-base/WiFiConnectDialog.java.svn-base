package com.android.settings.net;

import android.app.Dialog;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.settings.R;

public class WiFiConnectDialog extends Dialog {

	private WiFiSignalListActivity mWiFiSignalListActivity;

	// 连接按钮.
	private Button connect_btn;

	// 取消连接按钮.
	private Button cancel_btn;

	// 连接网络的名称.
	private TextView connect_txt;

	// 存放从无线网络列表传过来的网络名称.
	private String net_name;

	private EditText code_et;

	// 密码框布局.
	private RelativeLayout connect_r1;

	// 显示密码框布局.
	private RelativeLayout connect_r2;

	// 是否显示密码选择组件.
	private CheckBox code_show_cb;

	private boolean isHasFocus = false;

	public WiFiConnectDialog(WiFiSignalListActivity wifiSignalListActivity,
			String net_name) {
		super(wifiSignalListActivity);
		this.mWiFiSignalListActivity = wifiSignalListActivity;
		this.net_name = net_name;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifi_connect);

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
		int width = (int) (display.getWidth() * 0.4);
		int height = (int) (display.getHeight() * 0.5);

		// 设置窗口的大小
		w.setLayout(width, height);

		// 设置窗口的显示位置
		w.setGravity(Gravity.CENTER);

		// 设置窗口的属性
		WindowManager.LayoutParams wl = w.getAttributes();
		w.setAttributes(wl);

		findViews();

		registerListener();
	}

	private void findViews() {

		connect_btn = (Button) findViewById(R.id.connect_ok);
		cancel_btn = (Button) findViewById(R.id.connect_cancel);

		connect_txt = (TextView) findViewById(R.id.connect_name);
		connect_txt.setText(mWiFiSignalListActivity
				.getString(R.string.connect_net) + net_name);

		connect_r1 = (RelativeLayout) findViewById(R.id.connect_r1);

		connect_r1.setBackgroundResource(R.drawable.desktop_button);

		connect_r2 = (RelativeLayout) findViewById(R.id.connect_r2);

		//code_show_cb = (CheckBox) findViewById(R.id.code_show_cb_con);
		Configuration config = mWiFiSignalListActivity.getResources().getConfiguration();
		if (config.locale.toString().equals("en_US")){
			code_show_cb = (CheckBox) findViewById(R.id.code_show_cb_con_en);
			code_show_cb.setVisibility(View.VISIBLE);
		} else {
			code_show_cb = (CheckBox) findViewById(R.id.code_show_cb_con);
			code_show_cb.setVisibility(View.VISIBLE);
		}

		code_et = (EditText) findViewById(R.id.code_et_con);
		code_et.requestFocus();
	}

	private void registerListener() {

		// 连接按钮监听.
		connect_btn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();

			}
		});

		// 取消连接按钮监听.
		cancel_btn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();

			}
		});

		code_et.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					connect_r1.setBackgroundResource(R.drawable.desktop_button);
					connect_r2.setBackgroundResource(R.drawable.one_px);
					connect_btn.setBackgroundResource(R.drawable.one_px);
					cancel_btn.setBackgroundResource(R.drawable.one_px);
					isHasFocus = true;
				} else {
					connect_r1.setBackgroundResource(R.drawable.one_px);

				}

			}
		});

		code_show_cb.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					connect_r2.setBackgroundResource(R.drawable.desktop_button);
					connect_r1.setBackgroundResource(R.drawable.one_px);
					connect_btn.setBackgroundResource(R.drawable.one_px);
					cancel_btn.setBackgroundResource(R.drawable.one_px);
					isHasFocus = true;
				} else {
					connect_r2.setBackgroundResource(R.drawable.one_px);
				}
			}
		});

		// 取消按钮焦点监听.
		cancel_btn.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					connect_r1.setBackgroundResource(R.drawable.one_px);
					connect_r2.setBackgroundResource(R.drawable.one_px);
					connect_btn.setBackgroundResource(R.drawable.one_px);
					cancel_btn.setBackgroundResource(R.drawable.left_bg);
					isHasFocus = true;
				} else {
					cancel_btn.setBackgroundResource(R.drawable.one_px);
				}
			}
		});

		connect_btn.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					connect_r1.setBackgroundResource(R.drawable.one_px);
					connect_r2.setBackgroundResource(R.drawable.one_px);
					cancel_btn.setBackgroundResource(R.drawable.one_px);
					connect_btn.setBackgroundResource(R.drawable.left_bg);
					isHasFocus = true;
				} else {
					connect_btn.setBackgroundResource(R.drawable.one_px);
				}
			}
		});

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		Log.i("gong", "isHasFocu:" + isHasFocus);
		Log.i("gong", "keyCode:" + keyCode);
		Log.i("gong", "code_show_cb.hasFocus():" + code_show_cb.hasFocus());
		Log.i("gong", "code_et.hasFocus():" + code_et.hasFocus());
		Log.i("gong", "::::::::::::::::::::::::::::::::::::::::::::;");

		// press向下按钮.
		if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			// switch(currentState){
			// case 0:
			// changeState(connect_r1,R.drawable.one_px,connect_r2,R.drawable.desktop_button);
			// break;
			// case 1:
			// cancel_btn.clearFocus();
			// connect_btn.requestFocus();
			// changeState(connect_r2,R.drawable.one_px,connect_btn,R.drawable.left_bg);
			// break;
			// case 2:
			// //
			// changeState(connect_r2,R.drawable.one_px,cancel_btn,R.drawable.left_bg);
			// break;
			// case 3:
			// break;
			// }
			// currentState++;
			// if(currentState>=2){
			// currentState=2;
			// }

			// 焦点在密码编辑框上时.
			// if (code_et.hasFocus()) {
			// Log.i("gong", "第1次向下按：：：：");
			//
			// code_show_cb.requestFocus();
			// code_et.clearFocus();
			// FocusChange.changeBackground(connect_r1, connect_r2);
			// }

			// 焦点在是否显示密码上.
			// if (code_show_cb.hasFocus()) {
			// Log.i("gong", "第2次向下按：：：：");
			//
			// // code_show_cb.clearFocus();
			// // connect_btn.requestFocus();
			// }
		}

		// press向上按钮.
		if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			// switch(currentState){
			// case 0:
			// //
			// changeState(connect_r1,R.drawable.one_px,connect_r2,R.drawable.desktop_button);
			// break;
			// case 1:
			// changeState(connect_r2,R.drawable.one_px,connect_r1,R.drawable.desktop_button);
			// break;
			// case 2:
			// changeState(cancel_btn,R.drawable.one_px,connect_r2,R.drawable.desktop_button);
			// //
			// changeState(connect_r2,R.drawable.one_px,cancel_btn,R.drawable.left_bg);
			// break;
			// case 3:
			// break;
			// }
			// currentState--;
			// if(currentState<0){
			// currentState=0;
			// }
			// FocusChange.changeBackground(connect_r2, connect_r1);
			// // 焦点在取消按钮上时.
			// if (cancel_btn.hasFocus()) {
			//
			// cancel_btn.clearFocus();
			// code_show_cb.requestFocus();
			// code_et.clearFocus();
			//
			// connect_r2.setBackgroundResource(R.drawable.desktop_button);
			// } else if (connect_btn.hasFocus()) {
			//
			// connect_btn.clearFocus();
			// code_show_cb.requestFocus();
			// code_et.clearFocus();
			//
			// connect_r2.setBackgroundResource(R.drawable.desktop_button);
			//
			// } else if (code_show_cb.hasFocus()) {
			//
			// code_show_cb.clearFocus();
			// FocusChange.changeBackground(connect_r2, connect_r1);
			// }

		}
		return super.onKeyDown(keyCode, event);
	}

}
