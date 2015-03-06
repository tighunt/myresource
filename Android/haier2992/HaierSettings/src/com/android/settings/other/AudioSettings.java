package com.android.settings.other;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;

import com.android.settings.R;
/*import com.tvos.common.TvManager;
import com.tvos.common.TvManager.EnumPowerOnMusicMode;*/
import android.app.TvManager;

/**
 * 音频设置的主界面
 * 
 * @author 曹美娟
 * @date 2012-1-7 下午05:15:27
 * @since 1.0
 */
public class AudioSettings extends Activity {
	private TvManager mTvManager;
	private final static String NAME = "share_pres";
	private final static String ONOFF_POWERONMUSIC = "poweronmusic_onoff";
	private final static String STRPOWERONMUSIC = "poweronmusic";
	private final static String ONOFF_KEYAUDIO = "keyaudio_onoff";
	private final static String STRKEYAUDIO = "keyaudio";
	private AudiosettingViewHolder mAudiosettingViewHolder;
	private int isSelected = 0;
	// 开机音乐是否为开
	private boolean isPoweronmusicOn;
	// 按键声音是否为开
	private boolean isKeyaudioOn;
	
	private Handler handler = null;
	
	private Runnable musicRunnable = new Runnable() {

		@Override
		public void run() {
			savePoweronMusic();
		}
	};
	private Runnable keyRunnable = new Runnable() {
		
		@Override
		public void run() {
			saveKeyAudioOn();
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.other_audiosetting_mix);
		mTvManager = (TvManager) this.getSystemService(TV_SERVICE);
		new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
				handler = new Handler();
				Looper.loop();
			}
		}).start();
		
		findViews();
		registerListeners();
	}

	/**
	 * 组件和数据的初始化
	 */
	private void findViews() {
		// mDummyDate = Calendar.getInstance();
		mAudiosettingViewHolder = new AudiosettingViewHolder(this);
		isPoweronmusicOn = getisPoweronmusicState();
		isKeyaudioOn = getKeyaudioState();
		if (isPoweronmusicOn) {
			mAudiosettingViewHolder.mPoweron_music.setChecked(true);
		} else {
			mAudiosettingViewHolder.mPoweron_music.setChecked(false);
		}
		if (isKeyaudioOn) {
			mAudiosettingViewHolder.mKeyAudioon.setChecked(true);
		} else {
			mAudiosettingViewHolder.mKeyAudioon.setChecked(false);
		}
	}

	/**
	 * 响应各个控件的监听事件
	 */
	private void registerListeners() {

		// 开机音乐是否为开
		mAudiosettingViewHolder.mPoweron_music.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				isPoweronmusicOn = isChecked;
				handler.removeCallbacks(musicRunnable);
				handler.post(musicRunnable);
			}
		});
		mAudiosettingViewHolder.mPoweron_music.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				changeViewSelected(0);
			}
		});
		mAudiosettingViewHolder.mPoweronMusic.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				changeViewSelected(0);
			}
		});

		// 按键声音是否为开
		mAudiosettingViewHolder.mKeyAudioon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				isKeyaudioOn = isChecked;
				handler.removeCallbacks(keyRunnable);
				handler.post(keyRunnable);
			}
		});
		mAudiosettingViewHolder.mKeyAudioon.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				changeViewSelected(1);
			}
		});
		mAudiosettingViewHolder.mKeyAudio.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				changeViewSelected(1);
			}
		});

	}

	private boolean setPoweronMusic() {
		/*try {
			if (isPoweronmusicOn) {
				//TvManager.setEnvironmentPowerOnMusicMode(EnumPowerOnMusicMode.E_POWERON_MUSIC_DEFAULT);
			} else {
				//TvManager.setEnvironmentPowerOnMusicMode(EnumPowerOnMusicMode.E_POWERON_MUSIC_OFF);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}*/
		mTvManager.setOnOffMusic(isPoweronmusicOn);
		return isPoweronmusicOn;
	}

	private boolean setKeyAudioon() {
		try {
			if (isKeyaudioOn) {
				Settings.System.putInt(getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, 1);
				Settings.System.putInt(getContentResolver(), Settings.System.HAPTIC_FEEDBACK_ENABLED, 1);
			} else {
				Settings.System.putInt(getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, 0);
				Settings.System.putInt(getContentResolver(), Settings.System.HAPTIC_FEEDBACK_ENABLED, 0);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return isKeyaudioOn;
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			dropDown();
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			dropUp();
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return super.onKeyUp(keyCode, event);
	}

	/**
	 * 开机音乐是否为开
	 * 
	 * @return
	 */
	private boolean getisPoweronmusicState() {/*//20130511 modify by cw
		try {
			if(TvManager.getEnvironmentPowerOnMusicMode() == EnumPowerOnMusicMode.E_POWERON_MUSIC_DEFAULT){
				return true;
			}else{
				return false;
			}
		} catch (Exception snfe) {
			return false;
		}
		*/
		return	mTvManager.getOnOffMusic();
	}
	/**
	 * 按键声音是否为开
	 * 
	 * @return
	 */
	private boolean getKeyaudioState() {
		try {
			Log.d("charlie", "STRKEYAUDIO: " + Settings.System.getInt(getContentResolver(), STRKEYAUDIO));
			//return Settings.System.getInt(getContentResolver(), STRKEYAUDIO, 1) > 0;
			return Settings.System.getInt(getContentResolver(), STRKEYAUDIO) > 0;
		} catch (SettingNotFoundException snfe) {
			Log.d("charlie", "getKeyaudioState exception:");
			snfe.printStackTrace();
			return false;
		}
	}

	/**
	 * 向下滑动
	 */
	private void dropDown() {
		switch (isSelected) {
		case 0:
			isSelected = 1;
			mAudiosettingViewHolder.mPoweronMusic.setBackgroundResource(R.drawable.one_px);
			getCurrentFocus().clearFocus();
			mAudiosettingViewHolder.mKeyAudio.setBackgroundResource(R.drawable.set_button);
			mAudiosettingViewHolder.mKeyAudioon.requestFocus();
			break;
		}
	}

	/**
	 * 向上滑动
	 */
	private void dropUp() {
		switch (isSelected) {
		case 1:
			isSelected = 0;
			mAudiosettingViewHolder.mKeyAudio.setBackgroundResource(R.drawable.one_px);
			getCurrentFocus().clearFocus();
			mAudiosettingViewHolder.mPoweronMusic.setBackgroundResource(R.drawable.set_button);
			mAudiosettingViewHolder.mPoweron_music.requestFocus();
			break;

		}
	}

	/**
	 * 保存开机音乐是否为开
	 */
	private void commitCheckBoxPoweronmusicValue() {

		SharedPreferences preference = getSharedPreferences(NAME, Context.MODE_PRIVATE);

		Editor edit = preference.edit();

		edit.putBoolean(ONOFF_POWERONMUSIC, isPoweronmusicOn);

		edit.commit();
		Settings.System.putInt(getContentResolver(), STRPOWERONMUSIC, isPoweronmusicOn ? 1 : 0);
	}

	/**
	 * 保存按键声音是否为开
	 */
	private void commitCheckBoxValue() {

		SharedPreferences preference = getSharedPreferences(NAME, Context.MODE_PRIVATE);

		Editor edit = preference.edit();

		edit.putBoolean(ONOFF_KEYAUDIO, isKeyaudioOn);

		edit.commit();
		Settings.System.putInt(getContentResolver(), STRKEYAUDIO, isKeyaudioOn ? 1 : 0);
	}

	/**
	 * 鼠标点击时，切换焦点
	 * 
	 * @param seleted
	 * @param flag
	 */
	public void changeViewSelected(int seleted, boolean flag) {
		if (!flag) {
			switch (seleted) {
			case 0:
				mAudiosettingViewHolder.mPoweronMusic.setBackgroundResource(R.drawable.one_px);
				mAudiosettingViewHolder.mPoweron_music.clearFocus();
				break;
			case 1:
				mAudiosettingViewHolder.mKeyAudio.setBackgroundResource(R.drawable.one_px);
				mAudiosettingViewHolder.mKeyAudioon.clearFocus();
				break;
			default:
				mAudiosettingViewHolder.mPoweronMusic.setBackgroundResource(R.drawable.one_px);
				mAudiosettingViewHolder.mPoweron_music.clearFocus();
			}
		} else {
			switch (seleted) {
			case 0:
				mAudiosettingViewHolder.mPoweronMusic.setBackgroundResource(R.drawable.set_button);
				mAudiosettingViewHolder.mPoweron_music.setFocusable(true);
				mAudiosettingViewHolder.mPoweron_music.setFocusableInTouchMode(true);
				mAudiosettingViewHolder.mPoweron_music.requestFocus();
				break;
			case 1:
				mAudiosettingViewHolder.mKeyAudio.setBackgroundResource(R.drawable.set_button);
				mAudiosettingViewHolder.mKeyAudioon.setFocusable(true);
				mAudiosettingViewHolder.mKeyAudioon.setFocusableInTouchMode(true);
				mAudiosettingViewHolder.mKeyAudioon.requestFocus();
				break;
			default:
				mAudiosettingViewHolder.mPoweronMusic.setBackgroundResource(R.drawable.set_button);
				mAudiosettingViewHolder.mPoweron_music.setFocusable(true);
				mAudiosettingViewHolder.mPoweron_music.setFocusableInTouchMode(true);
				mAudiosettingViewHolder.mPoweron_music.requestFocus();
			}
		}
	}

	public void changeViewSelected(int selected) {
		changeViewSelected(isSelected, false);
		isSelected = selected;
		changeViewSelected(isSelected, true);
	}
	
	
	public void savePoweronMusic() {
		setPoweronMusic();
	//	commitCheckBoxPoweronmusicValue();
	}

	public void saveKeyAudioOn() {
		setKeyAudioon();
		commitCheckBoxValue();
	}

}
