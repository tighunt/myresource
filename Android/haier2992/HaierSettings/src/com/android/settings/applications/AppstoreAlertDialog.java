package com.android.settings.applications;

import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.android.settings.ApplicationSettings;
import com.android.settings.R;

/**
 * AppstoreAlertDialog
 * 
 * @author 张海生
 * @date 2012-03-01
 * @since 1.0
 */
public class AppstoreAlertDialog extends Dialog {

	private ApplicationSettings mApplicationSettings;

	private Button ok_btn;
	private Button cancel_btn;

	public AppstoreAlertDialog(ApplicationSettings mApplicationSettings) {
		super(mApplicationSettings);
		this.mApplicationSettings = mApplicationSettings;
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.appstore_app_alert);

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
	}

	/**
	 * init compontent
	 */
	private void findViews() {
		ok_btn = (Button) findViewById(R.id.appstore_ok_btn);
		cancel_btn = (Button) findViewById(R.id.appstore_cancel_btn);

		ok_btn.setFocusable(true);
		ok_btn.requestFocus();
		ok_btn.setFocusableInTouchMode(true);

		ok_btn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				mApplicationSettings.setNonMarketAppsAllowed(true);
				dismiss();
			}
		});

		cancel_btn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				mApplicationSettings.setNonMarketAppsAllowed(false);
				mApplicationSettings.mAppManageViewHolder.mCheckBox.setChecked(false);
				dismiss();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mApplicationSettings.setNonMarketAppsAllowed(false);
			mApplicationSettings.mAppManageViewHolder.mCheckBox.setChecked(false);
			dismiss();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return false;
	}

}
