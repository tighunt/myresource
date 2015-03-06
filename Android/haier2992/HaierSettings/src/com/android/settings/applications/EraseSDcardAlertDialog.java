package com.android.settings.applications;

import java.io.File;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.settings.ApplicationSettings;
import com.android.settings.R;

/**
 * EraseSDcardAlertDialog
 * 
 * @author 刘畅
 * @date 2012-09-19
 * @since 1.0
 */
public class EraseSDcardAlertDialog extends Dialog {

	private static final String TAG = "EraseSDcardAlertDialog";

	private ApplicationSettings mApplicationSettings;
	private String formatPath = Environment.getExternalStorageDirectory().getPath();
	private StorageManager stm = (StorageManager) getContext().getSystemService(Context.STORAGE_SERVICE);

	private TextView alertText;
	private Button ok_btn;
	private Button cancel_btn;
	private ProgressBar progressBar;
	private TextView formatHint;
	
	private boolean isFormatting;

	public EraseSDcardAlertDialog(ApplicationSettings mApplicationSettings) {
		super(mApplicationSettings);
		this.mApplicationSettings = mApplicationSettings;
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.erase_sdcard_alert);

		// set window
		Window w = getWindow();
		w.setBackgroundDrawableResource(R.drawable.set_bg);
		WindowManager.LayoutParams layoutParams = w.getAttributes();
		layoutParams.width = 700;
		layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		layoutParams.x = 100;
		w.setAttributes(layoutParams);
		w.setTitle(null);

		findViews();
		cancel_btn.requestFocus();
	}

	/**
	 * init compontent
	 */
	private void findViews() {
		alertText = (TextView) findViewById(R.id.alert_text);
		ok_btn = (Button) findViewById(R.id.erase_ok_btn);
		cancel_btn = (Button) findViewById(R.id.erase_cancel_btn);
		progressBar = (ProgressBar) findViewById(R.id.format_progress);
		formatHint = (TextView) findViewById(R.id.format_hint);

		ok_btn.setFocusable(true);
		ok_btn.setFocusableInTouchMode(true);

		ok_btn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				isFormatting = true;
				alertText.setVisibility(View.GONE);
				progressBar.setVisibility(View.VISIBLE);
				formatHint.setVisibility(View.VISIBLE);
				ok_btn.setVisibility(View.GONE);
				cancel_btn.setVisibility(View.GONE);

				new Thread() {
					public void run() {
						new Thread() {
							public void run() {
								// unmount
								/*while (stm.getVolumeState(formatPath).equals(Environment.MEDIA_MOUNTED)) {
									//stm.unmountVolume(formatPath, true, false);//20130511 modify by cw
									try {
										sleep(500);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}

								// format
								formatSDcard();

								// mount
								while (stm.getVolumeState(formatPath).equals(Environment.MEDIA_UNMOUNTED)) {
									//stm.mountVolume(formatPath);//20130511 modify by cw
									try {
										sleep(500);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
								*/
								fromatSDcardData();
								mApplicationSettings.getmAppInforHandler().sendEmptyMessage(
										ApplicationSettings.REFRESH_SDCARD_MEMORY);
								dismiss();
							}
						}.start();
					}
				}.start();
			}
		});

		cancel_btn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}
	
	private void deleteFile(File oldPath) {
		if (oldPath.isDirectory()) {
			File[] files = oldPath.listFiles();
			for (File file : files) {
				deleteFile(file);
				file.delete();
			}
		}else{
			oldPath.delete();
		}
	}
	
	private void fromatSDcardData() {
		File file = new File(formatPath);
		deleteFile(file);
	}

	public void formatSDcard() {
		if (stm.getVolumeState(formatPath).equals(Environment.MEDIA_UNMOUNTED)) {
/*			if (stm.formatVolume(formatPath)) {//20130511 modify by cw
				Log.d(TAG, "Success to format " + formatPath);

				if (stm.mountVolume(formatPath)) {
					Log.d(TAG, "Success to mount " + formatPath + " again");
				} else {
					Log.d(TAG, "Fail to mount " + formatPath + " again");
				}
			} else {
				Log.d(TAG, "Fail to format " + formatPath);
			}*/

			formatPath = null;
		} else {
			Log.d(TAG, "Can not format " + formatPath);
			formatPath = null;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(!isFormatting) dismiss();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return false;
	}

}
