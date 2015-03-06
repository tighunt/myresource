package com.android.settings.other;

import android.content.res.Configuration;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.settings.R;

/**
 * 其它设置界面的初始化
 * 
 * @author 曹美娟
 * @date 2012-1-2 下午02:58:32
 * @since 1.0
 */
public class OtherViewHolder {

	private OtherSettings mOtherSettingActivity;

	// 商标灯
	public RelativeLayout mOtherLightcontrol;

	// 睡眠时间
	public RelativeLayout mOtherSleep;

	// 智能眼
	public RelativeLayout mOtherIntelligenteye;

	// 音频设置
	public RelativeLayout mOtherAudiosetting;

	// 桌面设置
	public RelativeLayout mOtherDesktop;

	// ipad充电模式 zhf
	public RelativeLayout mOtherIpadmode;

	// 开机检测新版本系统软件自动提示
	public RelativeLayout mOtherAutoUpgrade;
	
	// 商标灯是否为开
	public CheckBox mLightOn;

	// 当前睡眠时间模式
	public Button sleep_mode;

	// 当前智能眼模式
	public Button intelligenteye_mode;

	public Button audio_setting;

	public Button desktop_setting;

	// ipad充电模式是否为开
	public CheckBox mIpadmodeOn;

	public CheckBox mAutoUpgrade;
	
	public TextView intelligentyey_mode_return;

	//public TextView sleep_mode_return;

	public OtherViewHolder(OtherSettings otherSettingActivity) {
		this.mOtherSettingActivity = otherSettingActivity;
		findViews();
	}

	private void findViews() {
//		mOtherLightcontrol = (RelativeLayout) mOtherSettingActivity.findViewById(R.id.ohter_lightcontrol);
		mOtherSleep = (RelativeLayout) mOtherSettingActivity.findViewById(R.id.other_sleep);
//		mOtherIntelligenteye = (RelativeLayout) mOtherSettingActivity.findViewById(R.id.other_intelligenteye);
		mOtherAudiosetting = (RelativeLayout) mOtherSettingActivity.findViewById(R.id.other_audiosetting);
		mOtherDesktop = (RelativeLayout) mOtherSettingActivity.findViewById(R.id.other_desktop);
//		mOtherIpadmode = (RelativeLayout) mOtherSettingActivity.findViewById(R.id.other_ipadmode);// zhf
		mOtherAutoUpgrade = (RelativeLayout) mOtherSettingActivity.findViewById(R.id.other_autoupgrade);

		Configuration config = mOtherSettingActivity.getResources().getConfiguration();
//		if (config.locale.toString().equals("en_US")) // zhf
//		{
//			mLightOn = (CheckBox) mOtherSettingActivity.findViewById(R.id.checkbox_lightcontrol_en);
//			mLightOn.setVisibility(View.VISIBLE);
//
//		} else {
//			mLightOn = (CheckBox) mOtherSettingActivity.findViewById(R.id.checkbox_lightcontrol);
//			mLightOn.setVisibility(View.VISIBLE);
//
//		}

		sleep_mode = (Button) mOtherSettingActivity.findViewById(R.id.sleep_set);
//		intelligenteye_mode = (Button) mOtherSettingActivity.findViewById(R.id.intelligenteye_set);
		audio_setting = (Button) mOtherSettingActivity.findViewById(R.id.audiosetting_set);
		desktop_setting = (Button) mOtherSettingActivity.findViewById(R.id.desktop_set);
//		if (config.locale.toString().equals("en_US")) // zhf
//		{
//			mIpadmodeOn = (CheckBox) mOtherSettingActivity.findViewById(R.id.checkbox_ipadmode_en);
//			mIpadmodeOn.setVisibility(View.VISIBLE);
//
//		} else {
//			mIpadmodeOn = (CheckBox) mOtherSettingActivity.findViewById(R.id.checkbox_ipadmode);
//			mIpadmodeOn.setVisibility(View.VISIBLE);
//
//		}
		if (config.locale.toString().equals("en_US"))
		{
			mAutoUpgrade = (CheckBox) mOtherSettingActivity.findViewById(R.id.checkbox_autoupgrade_en);
			mAutoUpgrade.setVisibility(View.VISIBLE);

		} else {
			mAutoUpgrade = (CheckBox) mOtherSettingActivity.findViewById(R.id.checkbox_autoupgrade);
			mAutoUpgrade.setVisibility(View.VISIBLE);

		}

//		intelligentyey_mode_return = (TextView) mOtherSettingActivity.findViewById(R.id.intelligentyey_mode_return);
	//	sleep_mode_return = (TextView) mOtherSettingActivity.findViewById(R.id.sleep_mode_return);
	}

}
