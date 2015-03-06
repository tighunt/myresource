package com.android.settings.desktop;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.android.settings.R;
/*import com.android.settings.net.FocusChange;*/

/**
 * 桌面设置主界面
 * 
 * @author 杜聪甲(ducj@biaoqi.com.cn)
 * @date 2011-11-9 上午10:37:05
 * @since 1.0
 */
public class DesktopSettingActivity extends Activity {

	private final static int BACKGROUND_CHANGE = 0;

	private final static int STYTLE_CHANGE = 1;

	private DesktopSettingViewHolder mDesktopSettingViewHolder;

	// 焦点的切换
	private int state = 0;

	// 是否修改背景图片
	private boolean isModifyBackground = false;

	// 是否修改风格
	private boolean isModifyStyle = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.desktop_setting);

		findViews();

		registerListeners();
	}

	private void findViews() {
		mDesktopSettingViewHolder = new DesktopSettingViewHolder(this);

		mDesktopSettingViewHolder.mBackgroundRelativeLayout
				.setBackgroundResource(R.drawable.desktop_button);
		mDesktopSettingViewHolder.mBackgroundRelativeLayout.requestFocus();
	}

	private void registerListeners() {
		mDesktopSettingViewHolder.change_background_btn
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if (state == BACKGROUND_CHANGE) {
							if (!isModifyStyle) {
								if (!isModifyBackground) {
									isModifyBackground = true;
									mDesktopSettingViewHolder.background_left_arrowHead
											.setVisibility(View.VISIBLE);
									mDesktopSettingViewHolder.background_right_arrowHead
											.setVisibility(View.VISIBLE);
								} else {
									isModifyBackground = false;
									mDesktopSettingViewHolder.background_left_arrowHead
											.setVisibility(View.INVISIBLE);
									mDesktopSettingViewHolder.background_right_arrowHead
											.setVisibility(View.INVISIBLE);

								}
							}
						}
					}
				});
		mDesktopSettingViewHolder.change_style_btn
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if (state == STYTLE_CHANGE) {
							if (!isModifyBackground) {
								if (!isModifyStyle) {
									isModifyStyle = true;
									mDesktopSettingViewHolder.style_left_arrowHead
											.setVisibility(View.VISIBLE);
									mDesktopSettingViewHolder.style_right_arrowHead
											.setVisibility(View.VISIBLE);
								} else {
									isModifyStyle = false;
									mDesktopSettingViewHolder.style_left_arrowHead
											.setVisibility(View.INVISIBLE);
									mDesktopSettingViewHolder.style_right_arrowHead
											.setVisibility(View.INVISIBLE);
								}
							}
						}
					}
				});
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN
				|| keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			switch (state) {
			case BACKGROUND_CHANGE:
				state = STYTLE_CHANGE;
/*				FocusChange.changeBackground(//20130511 modify by cw
						mDesktopSettingViewHolder.mBackgroundRelativeLayout,
						mDesktopSettingViewHolder.mStyleRelativeLayout);*/
				break;
			case STYTLE_CHANGE:
				state = BACKGROUND_CHANGE;
/*				FocusChange.changeBackground(//20130511 modify by cw
						mDesktopSettingViewHolder.mStyleRelativeLayout,
						mDesktopSettingViewHolder.mBackgroundRelativeLayout);*/
				break;
			}
		}
		return super.onKeyUp(keyCode, event);
	}
}
