package com.android.settings.update;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.settings.R;
import com.android.settings.util.Tools;

/**
 * 系统恢复
 * 
 * @author 杜聪甲(ducj@biaoqi.com.cn)
 * @since 1.0 2011-11-21
 */
public class SystemRestoreActivity extends Activity {

	private static final int CHECK_STORAGE = 0;

	private static final int SYSTEM_RESTORE = 1;

	private static final int RESTORE_SUCCESS = 2;

	private static final int NUM_STORAGE_CHECKS = 10;

	private Button restore_btn;

	private TextView title;

	private TextView infor;

	private String directoryName;
	// 系统恢复的提示信息
	private String restoreInfor;
	// 是否有存储设备
	private boolean isHasStorage = false;
	// 检测SDCard的次数
	private int mNumRetries;
	// 开始恢复
	private boolean startResore = false;
	// 是否正在恢复
	private boolean restoring = false;
	// 重启TV
	private boolean reboot = false;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CHECK_STORAGE:
				checkStorage();
				break;
			case SYSTEM_RESTORE:
				restoreInfor += getString(R.string.start_restore) + "\n";
				infor.setText(restoreInfor);
				restore_btn.setText(getString(R.string.restore_start));
				restore_btn.setEnabled(true);
				restore_btn.setBackgroundResource(R.drawable.left_bg);
				startResore = true;
				break;
			case RESTORE_SUCCESS:
				restoring = false;
				restoreInfor += getString(R.string.restore_success) + "\n";
				infor.setText(restoreInfor);
				restore_btn.setText(getString(R.string.reboot));
				restore_btn.setEnabled(true);
				restore_btn.setBackgroundResource(R.drawable.left_bg);
				reboot = true;
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		directoryName = Environment.getExternalStorageDirectory().toString();
		System.out.println("directoryName" + directoryName);
		setContentView(R.layout.system_net_update);

		findViews();
		sendInitialMessage();
		registerListeners();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (restoring) {
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 组件的初始化
	 */
	private void findViews() {
		title = (TextView) findViewById(R.id.updateTitle);
		title.setText(getString(R.string.system_restore));
		infor = (TextView) findViewById(R.id.net_update_info);
		restoreInfor = getString(R.string.current_version)
				+ Tools.currentSystemVersion() + "\n";
		restoreInfor += getString(R.string.sdcard) + "\n";
		infor.setText(restoreInfor);

		restore_btn = (Button) findViewById(R.id.immediate);
		restore_btn.setEnabled(false);
		restore_btn.setBackgroundResource(R.drawable.one_px);
	}

	/**
	 * 监听器的注册
	 */
	private void registerListeners() {

		restore_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (startResore) {
					restoreSystem();
				} else if (reboot) {
					Intent reboot = new Intent(Intent.ACTION_REBOOT);
					reboot.putExtra("nowait", 1);
					reboot.putExtra("interval", 1);
					reboot.putExtra("window", 0);
					sendBroadcast(reboot);
				} else {
					finish();
				}
			}
		});

	}

	/**
	 * 系统的恢复
	 */
	private void restoreSystem() {

		startResore = false;

		String backUpPath = directoryName + "/mstara3/update_signed.zip/";

		File file = new File(backUpPath);

		if (file.exists()) {
			// 执行恢复操作

			System.out.println("11111111111111");
			restoreInfor += getString(R.string.restoring) + "\n";
			infor.setText(restoreInfor);
			restoring = true;
			restore_btn.setEnabled(false);
			restore_btn.setBackgroundResource(R.drawable.one_px);

			handler.sendEmptyMessage(RESTORE_SUCCESS);
		} else {
			restoreInfor += getString(R.string.no_restore) + "\n";
			infor.setText(restoreInfor);
			restore_btn.setEnabled(true);
			restore_btn.setText(getString(R.string.exit));
			restore_btn.setBackgroundResource(R.drawable.left_bg);
		}

	}

	/**
	 * 发送初始化消息，检测是否存在SDCard
	 */
	private void sendInitialMessage() {
		Message checkStorage = new Message();
		checkStorage.what = CHECK_STORAGE;
		handler.sendMessage(checkStorage);
	}

	/**
	 * 判断外部设备是否存在，
	 */
	private void checkStorage() {
		mNumRetries++;
		//isHasStorage = CheckSDCard.hasStorage();
		isHasStorage = CheckSDCard.quickHasStorage();
		System.out.println("isHasStorage ++++" + isHasStorage);
		if (!isHasStorage) {
			if (mNumRetries < NUM_STORAGE_CHECKS) {
				handler.sendEmptyMessageDelayed(CHECK_STORAGE, 200);
			} else {
				restoreInfor += getString(R.string.no_sdcard) + "\n";
				infor.setText(restoreInfor);
				restore_btn.setText(getString(R.string.exit));
				restore_btn.setEnabled(true);
				restore_btn.setBackgroundResource(R.drawable.left_bg);
			}
		} else {
			// 有SDCArd发送Handler消息提示用户可以开始备份
			handler.sendEmptyMessage(SYSTEM_RESTORE);
		}
	}
}
