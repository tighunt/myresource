package com.android.settings.update;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RecoverySystem;
import android.os.RecoverySystem.ProgressListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.settings.R;
import com.android.settings.util.Tools;

/**
 * 本地升级
 * 
 * @author 杜聪甲(ducj@biaoqi.com.cn)
 * @since 1.0 2011-11-19
 */
public class SystemLocalUpdateActivity extends Activity {

	private final static String TAG = "SystemLocalUpdateActivity===>";
	
	private static final int CHECK_STORAGE = 0;

	private static final int CHECK_NEW_VERSION = 1;

	private static final int CHECK_UPDATE_ERROR = 2;

	private static final int UPDATE_SUCCESS = 3;

	private static final int UPDATE_PROGRESS = 4;

	private static final int NUM_STORAGE_CHECKS = 10;

	//private static final String BROAD_ACTION = "SDCARD_ACTION";

	private Button update_btn;

	private VersionInfor mInfor;
	// 显示提示用户的信息
	private TextView updateInfor_tv;

	private TextView title;
	// 提示用户的信息
	private String updateInfor;
	// 判断是否有存储设备
	private boolean isHasStorage;
	// 检测SDCard的次数
	private int mNumRetries;
	// SDCard目录
	private String directoryName;
	// 用于判断是否有新的版本
	private boolean isHasNewVerison = false;
	// 系统是否正在升级
	private boolean updating = false;

	private File updateFile;

	private ProgressBar mProgressBar;
	// 显示当前进度
	private TextView current_progress;
	// 当前进度
	private int cur_progress;

	// SDCard各种操作的提示信息
	private String msg;
	
	private LinearLayout mLinearLayout;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case CHECK_STORAGE:
				checkStorage();
				break;
			case CHECK_NEW_VERSION:
				updateInfor += getString(R.string.check_new_version) + "\n";
				updateInfor_tv.setText(updateInfor);
				scanUpdateFile();
				break;
			case CHECK_UPDATE_ERROR:
				updateInfor += getString(R.string.check_failure) + "\n";
				update_btn.setEnabled(true);
				update_btn.setText(getString(R.string.exit));
				update_btn.setBackgroundResource(R.drawable.left_bg);
				updateInfor_tv.setText(updateInfor);
				break;
			case UPDATE_SUCCESS:
				updateInfor += getString(R.string.updated);
				updateInfor_tv.setText(updateInfor);
				break;
			case UPDATE_PROGRESS:
				current_progress.setText(cur_progress + "%");
				mProgressBar.setProgress(cur_progress);
				if (cur_progress == 100) {
					updateInfor += getString(R.string.check_success);
					updateInfor_tv.setText(updateInfor);
					updating = false;
				}
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.system_net_update);
		
		directoryName = Environment.getExternalStorageDirectory().toString();
		//directoryName = "/sdcard/";
		System.out.println("directoryName:" + directoryName);
		
		findViews();
		
		sendInitialMessage();
		
		registerListeners();

		IntentFilter intentFilter = new IntentFilter();
		//intentFilter.addAction(BROAD_ACTION);
		
		intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		Log.d(TAG,"registerReceiver to listen SDCARD");
		intentFilter.addDataScheme("file");
		registerReceiver(sdReceiver, intentFilter);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (updating) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		
		finish();
		unregisterReceiver(sdReceiver);
		super.onDestroy();
	}

	/**
	 * 组件的初始化
	 */
	private void findViews() {
		mInfor = new VersionInfor();
		update_btn = (Button) findViewById(R.id.immediate);
		updateInfor_tv = (TextView) findViewById(R.id.net_update_info);
		title = (TextView) findViewById(R.id.updateTitle);
		title.setText(getString(R.string.system_local_update));
		updateInfor = getString(R.string.current_version) + Tools.currentSystemVersion() + "\n";
		updateInfor += getString(R.string.sdcard) + "\n";
		updateInfor_tv.setText(updateInfor);
		update_btn.setEnabled(false);
		update_btn.setBackgroundResource(R.drawable.one_px);
		current_progress = (TextView) findViewById(R.id.current_progress);
		mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
		mLinearLayout = (LinearLayout) findViewById(R.id.show_progress);
	}

	/**
	 * 响应不同的事件
	 */
	private void registerListeners() {

		update_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				System.out.println("setOnClickListener");
				if (isHasNewVerison) {
					System.out.println("updateTVOS");
					mLinearLayout.setVisibility(View.VISIBLE);
					updating = true;
					update_btn.setEnabled(false);
					update_btn.setBackgroundResource(R.drawable.one_px);
					updateInfor += getString(R.string.check_package) + "\n";
					updateInfor_tv.setText(updateInfor);
					new UpdateTOVSThread().start();
				} else {
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
		System.out.println("isHasStorage:" + isHasStorage);
		if (!isHasStorage) {
			if (mNumRetries < NUM_STORAGE_CHECKS) {
				handler.sendEmptyMessageDelayed(CHECK_STORAGE, 200);
			} else {
				updateInfor += getString(R.string.no_sdcard) + "\n";
				updateInfor_tv.setText(updateInfor);
				update_btn.setText(getString(R.string.exit));
				update_btn.setEnabled(true);
				update_btn.setBackgroundResource(R.drawable.left_bg);
			}
		} else {
			// 有SDCArd发送Handler消息提示用户可以进行本地升级
			handler.sendEmptyMessage(CHECK_NEW_VERSION);
		}
	}

	/**
	 * 查询SDcard目录下是否有系统升级的镜像文件
	 */
	private void scanUpdateFile() {

		// File versionFile = new File(directoryName + "/mstara3/", "versioninfor");
		File versionFile = new File(directoryName, "versioninfor");

		String version = Tools.file2String(versionFile, "UTF-8");
		System.out.println("version:" + version);

		if (version != null) {
			getVersionInfor(version);

			if (!Tools.currentSystemVersion().equals(mInfor.getVersion())) {
				// 提示用户有新的版本
				// updateFile = new File(directoryName + "/mstara3/", "update_signed.zip");
				updateFile = new File(directoryName, "update_signed.zip");
				System.out.println(updateFile);
				if (updateFile.exists()) {
					isHasNewVerison = true;
					updateInfor += getString(R.string.new_version) + mInfor.getVersion() + "\n";
					updateInfor_tv.setText(updateInfor);
					update_btn.setEnabled(true);
					update_btn.setText(getString(R.string.update));
					update_btn.setBackgroundResource(R.drawable.left_bg);
				} else {
					isHasNewVerison = false;
					updateInfor += getString(R.string.no_update_file) + "\n";
					updateInfor_tv.setText(updateInfor);
					update_btn.setEnabled(true);
					update_btn.setText(getString(R.string.exit));
					update_btn.setBackgroundResource(R.drawable.left_bg);
				}
			} else {
				// 提示用户当前版本已经是最新的版本
				updateInfor += getString(R.string.latest) + "\n";
				updateInfor_tv.setText(updateInfor);
				update_btn.setEnabled(true);
				update_btn.setText(getString(R.string.exit));
				update_btn.setBackgroundResource(R.drawable.left_bg);
			}
		} else {
			updateInfor += getString(R.string.read_file_error) + "\n";
			updateInfor_tv.setText(updateInfor);
		}

	}

	/**
	 * 解析JSON数据，获取版本的详细信息
	 */
	private void getVersionInfor(String versionString) {
		try {
			JSONObject reqJson = new JSONObject(versionString);
			if (reqJson.getInt("err") == 0) {
				JSONObject jsonObject = reqJson.getJSONObject("bd");

				mInfor.setDs(jsonObject.get("ds").toString());
				mInfor.setVersion(jsonObject.get("ver").toString());
				mInfor.setUds(jsonObject.get("uds").toString());
				mInfor.setUrl(jsonObject.get("url").toString());
				mInfor.setSize(jsonObject.getLong("size"));
				mInfor.setMd(jsonObject.getString("md5"));
				mInfor.setForce(jsonObject.getInt("fd"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 检验更新包的签名
	 */
	private boolean verifyPackage() {

		try {
			RecoverySystem.ProgressListener progressListener = new ProgressListener() {

				@Override
				public void onProgress(int progress) {
					cur_progress = progress;
					handler.sendEmptyMessage(UPDATE_PROGRESS);
				}
			};

			System.out.println("updateFile  " + updateFile);
			System.out.println("updateFile.length():" + updateFile.length());
			RecoverySystem.verifyPackage(updateFile, progressListener, null);
			System.out.println("---------------verify");
			RecoverySystem.installPackage(this, updateFile);
			//RecoverySystem.installPackage(this, new File("/sdcard/update_signed.zip"));
			System.out.println("---------------install");
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 更新TVOS的系统
	 */
	private void updateTVOS() {
		isHasNewVerison = false;
		if (verifyPackage()) {
			// 执行更新系统的操作
			handler.sendEmptyMessage(UPDATE_SUCCESS);
		} else {
			handler.sendEmptyMessage(CHECK_UPDATE_ERROR);
		}
	}

	private BroadcastReceiver sdReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.d(TAG, "sdcard action:::::" + action);
			if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
				String msg = getString(R.string.sdcard_insert);
				scanUpdateFile();
				updateInfor += msg + "\n";
				updateInfor_tv.setText(updateInfor);
			} else if (Intent.ACTION_MEDIA_UNMOUNTED.equals(action)) {
				String msg = getString(R.string.sdcard_remove);
				updateInfor += msg + "\n";
				updateInfor_tv.setText(updateInfor);
				update_btn.setEnabled(true);
				update_btn.setText(getString(R.string.exit));
			}
			
			/*
			if (action.equals(BROAD_ACTION)) {
				msg = intent.getStringExtra("sd");
				boolean success = intent.getBooleanExtra("success", false);
				System.out.println("msg" + msg);
				System.out.println("success" + success);
				updateInfor += msg + "\n";
				updateInfor_tv.setText(updateInfor);
				if (success) {
					scanUpdateFile();
				} else {
					update_btn.setEnabled(true);
					update_btn.setText(getString(R.string.exit));
				}
			}
			*/
		}
	};

	class UpdateTOVSThread extends Thread {
		@Override
		public void run() {
			super.run();
			updateTVOS();
		}
	}
}
