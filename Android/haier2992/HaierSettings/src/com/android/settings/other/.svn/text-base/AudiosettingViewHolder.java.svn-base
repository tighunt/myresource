
package com.android.settings.other;

import android.content.res.Configuration;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import com.android.settings.R;

/**
 * 用户账号界面的初始化
 * 
 * @author 曹美娟
 * @date 2012-1-4 下午07:06:32
 * @since 1.0
 */
public class AudiosettingViewHolder {

    private AudioSettings mAudioSettingActivity;

    // 开机音乐
    public RelativeLayout mPoweronMusic;

    // 按键声音
    public RelativeLayout mKeyAudio;
    
    // 开机音乐是否为开
    public CheckBox mPoweron_music;
    
    // 按键声音是否为开
    public CheckBox mKeyAudioon;
  
    public AudiosettingViewHolder(AudioSettings audioSettingActivity) {
        this.mAudioSettingActivity = audioSettingActivity;
        findViews();
    }

    private void findViews() {
    	mPoweronMusic = (RelativeLayout) mAudioSettingActivity
                .findViewById(R.id.other_audiosetting_poweronmusic);
    	mKeyAudio = (RelativeLayout) mAudioSettingActivity.findViewById(R.id.other_audiosetting_keyaudio);
    	 Configuration config = mAudioSettingActivity.getResources().getConfiguration();
 	    if(config.locale.toString().equals("en_US"))	//zhf
 	    {
 	    	mKeyAudioon = (CheckBox) mAudioSettingActivity.findViewById(R.id.audiosetting_keyaudio_set_en);
 	    	mKeyAudioon.setVisibility(View.VISIBLE);
 	    	mPoweron_music = (CheckBox) mAudioSettingActivity.findViewById(R.id.audiosetting_poweronmuisic_set_en);
 	    	mPoweron_music.setVisibility(View.VISIBLE);
 	    }
 	    else
 	    {
 	    	mKeyAudioon = (CheckBox) mAudioSettingActivity.findViewById(R.id.audiosetting_keyaudio_set);
 	    	mKeyAudioon.setVisibility(View.VISIBLE);
 	    	mPoweron_music = (CheckBox) mAudioSettingActivity.findViewById(R.id.audiosetting_poweronmuisic_set);
 	    	mPoweron_music.setVisibility(View.VISIBLE);
 	    }
    	
    	
 //   	mKeyAudioon= (CheckBox) mAudioSettingActivity.findViewById(R.id.audiosetting_keyaudio_set);
  //  	mPoweron_music = (CheckBox) mAudioSettingActivity.findViewById(R.id.audiosetting_poweronmuisic_set);
    }

}
