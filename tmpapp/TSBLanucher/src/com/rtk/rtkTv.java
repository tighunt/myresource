package com.rtk;

import android.app.KeyguardManager;
import android.app.TvManager;
import android.app.tv.ChannelInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsb.launcher.Launcher;
import com.tsb.launcher.R;

import java.util.Locale;

public class rtkTv {

    private static final boolean SYS_NO_TV = SystemProperties.getBoolean(TvManager.SYSPROP_LAUNCHER_NO_TV, false); 

    /**********************************/
    private LinearLayout mHightlight;
    private TextView mTvInfoTxt;
    private TextView mTvInfoTxt2;
    private TextView mTvCH;
    private ImageView mBtnChUp;
    private ImageView mBtnChDown;
    private ImageView mBtnTvMenu;
    private ImageView mBtnInSelect;
    private RelativeLayout mTvMenuLayout;
    private RelativeLayout mTvLayout;
    private TvManager mTv;
    private Launcher mLauncher;

    private PreviewWindow mPreview;
    /***************************************/

    static final String TAG = "rtkTv";
    private int mCurLiveSource = -1;
    private final int FORCE_BG_MSG = 1;
    private static final long mDlayDuration = 1600;
    private KeyguardManager mKeyguardManager;
    private BroadcastReceiver mBroadcastReceiver;

    private static rtkTv mIntance = new rtkTv();

    private rtkTv() {
    }

    // public static method to return the reference
    public static rtkTv getInstance() {
        return mIntance;
    }
    
    private class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(TvManager.ACTION_SYSTEM_MESSAGE)) {
                int msg = intent.getIntExtra(TvManager.EXTRA_MESSAGE, -1);
                switch(msg) {
                    case TvManager.SYSTEM_MESSAGE_STR_RESUME:
                        if (mLauncher.isResumed()) {
                            mPreview.resetSourceAndDisplayWindow();
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        
    }

    /**
     * Activity.onCreate
     * @param launcher
     */
    public void InitTvLayout(Launcher launcher) {
        // TODO Auto-generated constructor stub
        this.mLauncher = launcher;

        //stephen_chen add for different resolution

    	mHightlight = (LinearLayout) launcher
                .findViewById(R.id.atv_highlight);
        mTvInfoTxt = (TextView) launcher
                .findViewById(R.id.atv_info);
        mTvInfoTxt2 = (TextView) launcher
                .findViewById(R.id.atv_info2);
        mTvCH = (TextView) launcher.findViewById(R.id.ch_text);
        mBtnChUp = (ImageView) launcher
                .findViewById(R.id.fixed_icon_ch_up);
        mBtnChDown = (ImageView) launcher
                .findViewById(R.id.fixed_icon_ch_down);
        mBtnInSelect = (ImageView) launcher
                .findViewById(R.id.fixed_icon_adjustment);
        mBtnTvMenu = (ImageView) launcher
                .findViewById(R.id.fixed_icon_setting);
        mTvMenuLayout = (RelativeLayout) launcher
                .findViewById(R.id.tv_layout);
        mTvLayout = (RelativeLayout) launcher
                .findViewById(R.id.fixed_video_bg);
        mKeyguardManager = (KeyguardManager) launcher.getSystemService(Context.KEYGUARD_SERVICE);
        mTv = (TvManager) launcher.getSystemService(Context.TV_SERVICE);
        initIcons();
        
        // Register Broadcast Receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(TvManager.ACTION_TV_MEDIA_MESSAGE);
        filter.addAction(TvManager.ACTION_SYSTEM_MESSAGE);
        
        mBroadcastReceiver = new MyBroadcastReceiver();
        launcher.registerReceiver(mBroadcastReceiver, filter);
        
        mPreview = new PreviewWindow(mLauncher);

        // For Audio Source Filtering of L4300
	// FIXME
	/*
        boolean isASFEffectsEnabled;
        try {
            isASFEffectsEnabled = Settings.Global.getInt(
                    mTSBLauncherActivity.getContentResolver(),
                    Settings.Global.TSB_ASF_EFFECTS_ENABLED) == 1;
            // Log.d(TAG,"isASFEffectsEnabled=" + isASFEffectsEnabled);
        } catch (SettingNotFoundException e) {
            // Log.d(TAG,"SettingNotFoundException", e);
            isASFEffectsEnabled = true;
            Settings.Global.putInt(mTSBLauncherActivity.getContentResolver(),
                    Settings.Global.TSB_ASF_EFFECTS_ENABLED, 1);
        }
	*/
		boolean isASFEffectsEnabled = false;
		if (isASFEffectsEnabled) {
			mTv.setASFEnable(mTv.getASFEnable());
			mTv.setASFBalance(mTv.getASFBalance());
			mTv.setASFVolume(mTv.getASFVolume());
		}
		

        // Start TV app.
        // Check system properties ro.rtk.launcher.no_tv
        if (SYS_NO_TV) {
            Log.v(TAG, "launcher.no_tv detected");
            mTv.setSource(TvManager.SOURCE_PLAYBACK);
        } else {
            // Bypass setRatio if we're starting TV apk !?
            // Normally it will automatically bypass setRatio for onPause will removes the callback which invokes resumeTv.
            // Therefore, we should make sure mBypassSetRatio is set to false after onPause. Jason
            if (mTv.getIsFirstRunTVAPK()) {
                mPreview.setBypassSetSource(true);
                Intent intent = TvManager.getIntentForTv();

                if (intent.resolveActivity(mLauncher.getPackageManager()) != null) {
                    Log.v(TAG, "Starting TV");
                    launcher.startActivity(intent);
                }
            }
        }

        // Always enable TV layout in onCreate
        // Only Hide/Show layout on workspace transition.
        enableTvLayout(true);
    }

    private void initIcons() {
        mHightlight.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	//Markout by Jason. Do this in onPause
//                enableTvView(false);
                Intent intent = TvManager.getIntentForTv();
                mLauncher.startActivity(intent);
            }
        });
        mHightlight
                .setOnFocusChangeListener(new ImageView.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            mHightlight
                                    .setBackgroundResource(R.drawable.pip_focus);
                            int[] pos = new int[2];
                            v.getLocationOnScreen(pos);
                            mLauncher.sendUnFocusMessage(pos[0], pos[1],
                                    v.getWidth(), v.getHeight(), 1);
                        } else {
                            mHightlight.setBackground(null);
                        }
                    }
                });
        mBtnChUp.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mTv.scaler_ForceBg(true);
                mTv.playNextChannel();
                mTv.scaler_ForceBg(false);
                refreshChannelInfo();
                // Log.d(TAG,"playNextChannel");
            }
        });
        mBtnChUp.setOnFocusChangeListener(new ImageView.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mBtnChUp.setImageResource(R.drawable.fixed_icon_ch_up_focus);
                    int[] pos = new int[2];
                    v.getLocationOnScreen(pos);
                    mLauncher.sendUnFocusMessage(pos[0], pos[1], v.getWidth(),
                            v.getHeight(), 1);
                    // mBtnChUp.setBackground(mTSBLauncherActivity.getResources().getDrawable(R.drawable.fixed_icon_ch_up_focus));
                } else {
                    mBtnChUp.setImageResource(R.drawable.fixed_icon_ch_up);
                    // mBtnChUp.setBackground(mTSBLauncherActivity.getResources().getDrawable(R.drawable.fixed_icon_ch_up));
                }
            }
        });
        mBtnChDown.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mTv.scaler_ForceBg(true);
                mTv.playPrevChannel();
                mTv.scaler_ForceBg(false);
                refreshChannelInfo();
                // Log.d(TAG,"playPrevChannel");
            }
        });
        mBtnChDown
                .setOnFocusChangeListener(new ImageView.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            int[] pos = new int[2];
                            v.getLocationOnScreen(pos);
                            mLauncher.sendUnFocusMessage(pos[0], pos[1],
                                    v.getWidth(), v.getHeight(), 1);
                            mBtnChDown
                                    .setImageResource(R.drawable.fixed_icon_ch_down_focus);
                            // mBtnChDown.setBackground(mTSBLauncherActivity.getResources().getDrawable(R.drawable.fixed_icon_ch_down_focus));
                        } else {
                            mBtnChDown
                                    .setImageResource(R.drawable.fixed_icon_ch_down);
                            // mBtnChDown.setBackground(mTSBLauncherActivity.getResources().getDrawable(R.drawable.fixed_icon_ch_down));
                        }
                    }
                });
        mBtnInSelect.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
				
				if(mCurLiveSource == TvManager.SOURCE_ATV1)
				{
					if(mTv.getIsYppAvailable())
						mCurLiveSource = TvManager.SOURCE_YPP1;
					else
						mCurLiveSource = TvManager.SOURCE_AV1;
				}else if((mCurLiveSource == TvManager.SOURCE_YPP1) || (mCurLiveSource == TvManager.SOURCE_AV1))
					mCurLiveSource = TvManager.SOURCE_AV2;
				else if(mCurLiveSource == TvManager.SOURCE_AV2)
					mCurLiveSource = TvManager.SOURCE_HDMI2;
				else if(mCurLiveSource == TvManager.SOURCE_HDMI2)
					mCurLiveSource = TvManager.SOURCE_HDMI3;
				else if(mCurLiveSource == TvManager.SOURCE_HDMI3)
					mCurLiveSource = TvManager.SOURCE_VGA1;
				else if(mCurLiveSource == TvManager.SOURCE_VGA1)
					mCurLiveSource = TvManager.SOURCE_ATV1;

				//mTv.setSourceAndDisplayWindow(mCurLiveSource, mMarginLeft,mMarginTop, mPreviewWidth, mPreviewHeight);
				syncStatus();
				
				/**
                mTv.bypassSetAspectRatio(true);
                ComponentName componetName = new ComponentName("com.tsb.tv",
                        "com.tsb.tv.Tv_strategy_lite");
                Intent intent = new Intent();
                intent.setComponent(componetName);
                Bundle bundle = new Bundle();
                bundle.putInt("TVMainMenu", 1);
                Rect rect = new Rect();
                rect.left = mMarginLeft;
                rect.top = mMarginTop;
                rect.right = rect.left + mPreviewWidth;
                rect.bottom = rect.top + mPreviewHeight;
                bundle.putParcelable("PlaySourceRect", rect);
                intent.putExtras(bundle);
                mTSBLauncherActivity.startActivity(intent);
                **/
            }
        });
        mBtnInSelect
                .setOnFocusChangeListener(new ImageView.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            int[] pos = new int[2];
                            v.getLocationOnScreen(pos);
                            mLauncher.sendUnFocusMessage(pos[0], pos[1],
                                    v.getWidth(), v.getHeight(), 1);
                            mBtnInSelect
                                    .setImageResource(R.drawable.fixed_icon_adjustment_focus);
                            // mBtnInSelect.setBackground(mTSBLauncherActivity.getResources().getDrawable(R.drawable.fixed_icon_adjustment_focus));
                        } else {
                            mBtnInSelect
                                    .setImageResource(R.drawable.fixed_icon_adjustment);
                            // mBtnInSelect.setBackground(mTSBLauncherActivity.getResources().getDrawable(R.drawable.fixed_icon_adjustment));
                        }
                    }
                });
        mBtnTvMenu.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mPreview.hide();
                ComponentName componetName = new ComponentName("com.tsb.tv",
                        "com.tsb.tv.Tv_strategy");
                Intent intent = new Intent();
                intent.setComponent(componetName);
                Bundle bundle = new Bundle();
                bundle.putInt("TVMainMenu", 3);
                intent.putExtras(bundle);
                mLauncher.startActivity(intent);
            }
        });
        mBtnTvMenu
                .setOnFocusChangeListener(new ImageView.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            int[] pos = new int[2];
                            v.getLocationOnScreen(pos);
                            mLauncher.sendUnFocusMessage(pos[0], pos[1],
                                    v.getWidth(), v.getHeight(), 1);
                            mBtnTvMenu
                                    .setImageResource(R.drawable.fixed_icon_setting_focus);
                            // mBtnTvMenu.setBackground(mTSBLauncherActivity.getResources().getDrawable(R.drawable.fixed_icon_setting_focus));
                        } else {
                            mBtnTvMenu
                                    .setImageResource(R.drawable.fixed_icon_setting);
                            // mBtnTvMenu.setBackground(mTSBLauncherActivity.getResources().getDrawable(R.drawable.fixed_icon_setting));
                        }
                    }
                });
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == FORCE_BG_MSG) {
                mTv.scaler_ForceBg(false);
            }          
        }
    };
    private void setEnableChUpDown(boolean enable) {
        mBtnChUp.setClickable(enable);
        mBtnChUp.setFocusable(enable);
        mBtnChDown.setClickable(enable);
        mBtnChDown.setFocusable(enable);
    }

    private String getSourceString() {
        // SourceOption
        switch (mCurLiveSource) {
        case TvManager.SOURCE_OSD:
            return "OSD";
        case TvManager.SOURCE_ATV1:
        case TvManager.SOURCE_ATV2:
            return "ATV";
        case TvManager.SOURCE_DTV1:
        case TvManager.SOURCE_DTV2:
            return "DTV";
        case TvManager.SOURCE_AV1:
            return "VIDEO1";
        case TvManager.SOURCE_AV2:
            return "VIDEO2";
        case TvManager.SOURCE_AV3:
            return "VIDEO3";
        case TvManager.SOURCE_SV1:
        case TvManager.SOURCE_SV2:
            return "SV";
        case TvManager.SOURCE_YPP1:
        case TvManager.SOURCE_YPP2:
        case TvManager.SOURCE_YPP3:
        case TvManager.SOURCE_YPP4:
            return "Component";
        case TvManager.SOURCE_VGA1:
        case TvManager.SOURCE_VGA2:
            return "PC";
        case TvManager.SOURCE_HDMI1:
        	return "HDMI1";
        case TvManager.SOURCE_HDMI2:
        	return "HDMI2";
        case TvManager.SOURCE_HDMI3:
        	return "HDMI3";
        case TvManager.SOURCE_HDMI4:
        	return "HDMI4";
        case TvManager.SOURCE_HDMI5:
        	return "HDMI5";
        case TvManager.SOURCE_HDMI6:
        	return "HDMI6";
        case TvManager.SOURCE_IDTV1:
        	return "IDTV1";
        case TvManager.SOURCE_SCART1:
        case TvManager.SOURCE_SCART2:
        case TvManager.SOURCE_PLAYBACK:
        case TvManager.SOURCE_MIC:
        case TvManager.SOURCE_AUTO:
        case TvManager.SOURCE_NULL:
        case TvManager.SOURCE_MAX_NUM:
            return "Unknow";
        default:
            break;
        }

        return "";
    }

    private int GetSourceList_Aka(int sourceId) {
        final String key = "SOURCE_Aka_"+sourceId;
        int ret = Settings.Global.getInt(
        		mLauncher.getContentResolver(), key, 0);
        // Log.d("TSBLANUCHER_DEBUG","[GetSourceList_Aka,Global]:key:"+key+","+ret);
        return ret;
    }

    private String getSourceLabel(int index) {
        switch (index) {
        case 0:
            return "";
        case 1:
            return mLauncher.getString(R.string.tsb_amplifier);
        case 2:
            return mLauncher.getString(R.string.tsb_cable);
        case 3:
            return mLauncher.getString(R.string.tsb_dvd);
        case 4:
            return mLauncher.getString(R.string.tsb_game);
        case 5:
            return mLauncher.getString(R.string.tsb_pc);
        case 6:
            return mLauncher.getString(R.string.tsb_recorder);
        case 7:
            return mLauncher.getString(R.string.tsb_satellite);
        case 8:
            return mLauncher.getString(R.string.tsb_vcr);
        default:
            return "";
        }
    }

    private void refreshChannelInfo() {
        String sourceString = getSourceString();
        String sourceLabel = "";
        
        String channelString = "";
        
        // TV Sources
        if (TvManager.isTvSource(mCurLiveSource)) {
            ChannelInfo chInfo = mTv.getCurrentChannel();
            if (null != chInfo) {
                channelString = String.format(Locale.US, "%02d", chInfo.m_1PartChNum);
                if (!TextUtils.isEmpty(chInfo.m_ChName)) {
                    channelString += " " + chInfo.m_ChName;
                }
            }

        // Other sources
        } else {
            // Why this!? Jason
            if(mCurLiveSource == TvManager.SOURCE_YPP1)
                sourceLabel = getSourceLabel(GetSourceList_Aka(TvManager.SOURCE_AV1));
            else
                sourceLabel = getSourceLabel(GetSourceList_Aka(mCurLiveSource));
        }
        
        // FIXME: Use layout-ldrtl or Gravity.START|Gravity.END to support RTL.
        String lang = Locale.getDefault().getLanguage();
        if (lang.equals("iw") || lang.equals("ar") || lang.equals("fa"))
            mTvInfoTxt.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.RIGHT);
        
        // FIXME: Use layout_margin instead of spaces here...
        mTvInfoTxt.setText("     " + sourceString + " " + sourceLabel);
        mTvInfoTxt2.setText(channelString);
    }
    
    private void syncStatus() {
        if (mCurLiveSource == TvManager.SOURCE_ATV1
                || mCurLiveSource == TvManager.SOURCE_ATV2
                || mCurLiveSource == TvManager.SOURCE_DTV1
                || mCurLiveSource == TvManager.SOURCE_DTV2)
            setEnableChUpDown(true);
        else
            setEnableChUpDown(false);
        refreshChannelInfo();
    }
    
    public void syncAVYpp() {
        if(!mLauncher.isOnPause()){
        if((mCurLiveSource == TvManager.SOURCE_YPP1) && (false == mTv.getIsYppAvailable()))  
            mPreview.updateSource(TvManager.SOURCE_ATV1);
        else if((mCurLiveSource == TvManager.SOURCE_AV1) && (true == mTv.getIsYppAvailable()))
            mPreview.updateSource(TvManager.SOURCE_YPP1);
        else
            return;
            }
        
        mCurLiveSource = mTv.getCurLiveSource();
        syncStatus();
    }

    public void onResumeTv() {
        mCurLiveSource = mTv.getCurLiveSource();
        syncStatus();
    }

    public void onPauseTv() {
        // unbind preview
        mPreview.unbindView();
        
        enableTvView(false);
        mPreview.setBypassSetSource(false);
    }
    
    /* From Launcher onResume */
    public void postResumeTv() {
        if (SYS_NO_TV) {
            enableTvView(false);
            enableTvLayout(false);
            return;
        }

        mCurLiveSource = mTv.getCurLiveSource();

        // FIXME
        int iIsScreenlock = 0;
        //int iIsScreenlock = Settings.Global.getInt(mTSBLauncherActivity.getContentResolver(), Settings.Global.TSB_IN_SCREENLOCK_STATE , 0);

        if (iIsScreenlock == 0)
            mTv.scaler_ForceBg(true);

        enableTvView(true);

        // Bind preview to view
        mPreview.bindView(mTvLayout, mHightlight, mCurLiveSource);

        if (iIsScreenlock == 0) {
            // Why do this after a delay ?? Jason
            Message msg = mHandler.obtainMessage(FORCE_BG_MSG);
            mHandler.sendMessageDelayed(msg, mDlayDuration);
        }
    }
    
    /**
     * Hide/Show TV layout.
     * This is called at the start/end of workspace transition
     * @param enable
     */
    public void enableTvLayout(boolean enable) {
        if (SYS_NO_TV) {
            enable = false;
        }

        if(true == enable && 
          2 != ((Launcher) mLauncher).getCurrentWorkspaceScreen())
            enable = false;
        
        int viewType;
        if (true == enable)
            viewType = View.VISIBLE;
        else
            viewType = View.GONE;

		mHightlight.setVisibility(viewType);
		mTvInfoTxt.setVisibility(viewType);
		mTvCH.setVisibility(viewType);
		mBtnChUp.setVisibility(viewType);
		mBtnChDown.setVisibility(viewType);
		mBtnInSelect.setVisibility(viewType);
		mBtnTvMenu.setVisibility(viewType);

    	Log.v("Test", "enableTvLayout: " + enable);
        mTvLayout.setVisibility(viewType);
        
        enableTvView(enable);
    }

    private boolean isEnableVideo() {
        
        if(View.VISIBLE != mTvInfoTxt.getVisibility())
            return false;
        
		if (2 != ((Launcher) mLauncher).getCurrentWorkspaceScreen())
            return false;
        
        boolean showing = mKeyguardManager.inKeyguardRestrictedInputMode();
        if(true == showing)
            return false;
        
        if(true == mLauncher.isOnPause())
            return false;
        
        if (mLauncher.isAllAppsVisible()) {
        	return false;
        }
        
        return true;
    }
    public void enableTvView(boolean enable) {
       Log.v("Test", "enableTvView" + enable);
       if (SYS_NO_TV) {
           mPreview.hide();
           return;
       }
       
        // This is confusing that we check the visibility again by calling isEnableVideo.
        // FIXME: enableTvView(boolean) -> refreshTvScreenVisibility() !? Jason
        if (enable) {
            if (isEnableVideo()) {
                Log.v("Test", "mTv.setVideoAreaOn");
                mPreview.show();
            } else {
                Log.v("Test", "mTv.setVideoAreaOn false");
                mPreview.hide();
            }
        } else {
            mPreview.hide();
        }
    }
/*
    public void initRtkAtvApp() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPostExecute(Void result) {
                enableTvView(true);
            }

            @Override
            protected Void doInBackground(Void... params) {

                if (mCurLiveSource == -1
                        || mTv.getCurSourceType() == TvManager.SOURCE_PLAYBACK
                        || mTv.getCurSourceType() == TvManager.SOURCE_OSD) {
                    mCurLiveSource = mTv.getCurLiveSource();

                    mTv.setSource(mCurLiveSource);
                }
                mTv.setDisplayWindow(mMarginLeft, mMarginTop, mPreviewWidth,
                        mPreviewHeight);
                setChannelInfo();
                // Log.d(TAG,"initRtkAtvAppA source="+Integer.toString(mCurLiveSource));
                return null;
            }
        }.execute();
    }*/

    public void getFocusFromApp() {
        mBtnTvMenu.requestFocus();
    }

}
