package com.android.settings.update;

import java.io.File;

import android.app.Activity;
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
 * 进行系统的备份
 * 
 * @author 杜聪甲(ducj@biaoqi.com.cn)
 * @since 1.0 2011-11-17
 */
public class SystemBackupActivity extends Activity {

	private static final int CHECK_STORAGE = 0;

	private static final int SYSTEM_BACKUP = 1;

	private static final int NUM_STORAGE_CHECKS = 10;

	private Button mBackUp_btn;

	private TextView mTitle;

	private TextView mBackUp;
	// 检测SDCard的次数
	private int mNumRetries;
	// 是否有存储设备
	private boolean isHasStorage = false;
	// 是否可以备份
	private boolean isBackupFile = false;
	// 是否开始备份
	private boolean isStart = false;
	// 备份是否完成
	private boolean backUpDone = false;
	// 显示的提示信息
	private String backupInfor;
	// SDCard的根目录
	private String directoryName;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CHECK_STORAGE:
				checkStorage();
				break;
			case SYSTEM_BACKUP:
				backupInfor += getString(R.string.start_backup) + "\n";
				mBackUp.setText(backupInfor);
				mBackUp_btn.setText(getString(R.string.start));
				mBackUp_btn.setEnabled(true);
				mBackUp_btn.setBackgroundResource(R.drawable.left_bg);
				isBackupFile = true;
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
		registerListeners();
		sendInitialMessage();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!isStart || backUpDone) {
				finish();
				System.out.println("1111111111111111");
			} else {
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 组件的初始化
	 */
	private void findViews() {

		mTitle = (TextView) findViewById(R.id.updateTitle);
		mTitle.setText(getString(R.string.system_backup));
		mBackUp = (TextView) findViewById(R.id.net_update_info);
		backupInfor = getString(R.string.current_version)
				+ Tools.currentSystemVersion() + "\n";
		backupInfor += getString(R.string.sdcard) + "\n";
		mBackUp.setText(backupInfor);

		mBackUp_btn = (Button) findViewById(R.id.immediate);
		mBackUp_btn.setEnabled(false);
		mBackUp_btn.setBackgroundResource(R.drawable.one_px);
	}

	/**
	 * Button的响应事件
	 */
	private void registerListeners() {

		mBackUp_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isBackupFile) {
					startBackUpSystem();
				} else if (!isHasStorage || backUpDone) {
					finish();
				}
			}
		});

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
				backupInfor += getString(R.string.no_sdcard) + "\n";
				mBackUp.setText(backupInfor);
				mBackUp_btn.setText(getString(R.string.exit));
				mBackUp_btn.setEnabled(true);
				mBackUp_btn.setBackgroundResource(R.drawable.left_bg);
			}
		} else {
			// 有SDCArd发送Handler消息提示用户可以开始备份
			handler.sendEmptyMessage(SYSTEM_BACKUP);
		}
	}

	/**
	 * 备份
	 */
	private void backUp() {
		System.out.println("22222222222222");
		// TODO:开始备份，同时更新备份的进度

		// 备份完成后
		backUpDone = true;
		mBackUp_btn.setEnabled(true);
		mBackUp_btn.setText(getString(R.string.exit));
		mBackUp_btn.setBackgroundResource(R.drawable.left_bg);
	}

	/**
	 * 执行系统备份的操作
	 */
	private void startBackUpSystem() {
		String backUpPath = directoryName + "/mstara3";

		isStart = true;
		isBackupFile = false;

		mBackUp_btn.setEnabled(false);
		mBackUp_btn.setBackgroundResource(R.drawable.one_px);

		backupInfor += getString(R.string.backUping) + "\n";
		mBackUp.setText(backupInfor);

		File backUpDirectory = new File(backUpPath);

		if (!backUpDirectory.exists()) {
			System.out.println("1111111111111");
			backUpDirectory.mkdir();
		}
		backUp();
	}
}
