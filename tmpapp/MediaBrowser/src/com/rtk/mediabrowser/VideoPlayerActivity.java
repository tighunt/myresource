package com.rtk.mediabrowser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import android.graphics.Typeface;
import com.realtek.DataProvider.FileFilterType;
import com.realtek.Utils.FileInfo;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.TrackInfo;
import android.media.Metadata;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.KeyEvent;
import android.media.MediaPlayer.OnCompletionListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnHoverListener;
import android.view.View.OnKeyListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.PopupWindow.OnDismissListener;
import android.app.TvManager;

public class VideoPlayerActivity extends Activity{
	private final static String TAG = "VideoPlayerActivity";
	
	
    private ArrayList<String> filePathArray = new ArrayList<String>();
	public ArrayList<String> fileTitleArray = new ArrayList<String>();
	public int currIndex = 0;
	public int resume_index = -1;
	public static int mBrowserType = 0;
	private String devicePath ="";
	/*********switch chapter subtitle title audio************/
	private int chapter_jump_num = 1;
	private int title_jump_num = 1;
	/******** Set Subtitle info *******/
	private int[] SubtitleInfo = null;
	private int SPU_ENABLE = 0;
	private int subtitle_num_Stream = 1;
	private int curr_subtitle_stream_num = 0;
	private int textEncoding = 1000;
	private int textColor = 0;
	private int fontSize = 19;
	
	/******** Set Audio info *******/
	private int[] AudioInfo = null;
	private int audio_num_stream = 0;
	private int curr_audio_stream_num = 0;
	
	/******** control parameter initial *******/
	private final static int ALL_BANNER = 0;
	private final static int MOVIE_BANNER = 1;
	private final static int QUICK_MENU = 2;
	private final static int SUBTILE_LIST = 3;
	private final static int AUDIO_BANNER = 4;
	private final static int WANNING_MESSAGE = 5;
	private final static int TITLE_BANNER = 6;
	private final static int BUTTON_LIST = 7;
	
	private final static int REPEAT_SINGLE = 1;
	private final static int REPEAT_ALL = 2;
	private final static int REPEAT_OFF = 3;
	private int repeat_mode = REPEAT_ALL;

	private final static int AUDIO_DIGITAL_RAW = 0;
	private final static int AUDIO_DIGITAL_LPCM_DUAL_CH = 1;
	private int AUDIO_DIGITAL_OUTPUT_MODE = 0;
	
	private int chapter_key_press = 0;
	
	/***********flag setting **************************/
	private int rewind_press = 1;
	private int forward_press = 1;
	private boolean isrewind = false;
	private boolean isforward = false;
	
	private boolean isSwitchToNextFile = false;
	
	private boolean isAudio_Error = false;
	private boolean isVideoPlayCompleteInRepeatOFFMode = false;
	private boolean isNeedExecuteOnPause = true;
	private boolean isOnPause = false;
	private boolean isStartPlay = true;
	private int querySubtitleCount = 0;
	
	private static int ResultCodeFinishVideoBrowser = 1;
	private int UsbRemoved = 2;
	private int StopPlay = 3;
	
	private int isDivxVODFile = 0;
	
	private boolean isShowChapterMetaData = false;
	
	private int selected_idx = 0;
	
	public boolean isRight2Left = false;
	
	private float MouseY = 0;
	private float MouseX = 0;
	/********   android component parameter initial *******/
	private SurfaceView sView = null;
	protected MediaPlayer mPlayer = null;
	
	private ImageView play = null;
	private ImageView play_mode = null;
	private ProgressBar time_bar = null;
	private TextView duration = null;
	private TextView filename = null;
	private TextView chapter_info = null;
	private ImageView tpt = null;
	private ImageView dolby = null;
	private TextView file_number = null;
	
	private TextView common_ui = null;
	
	private TextView metaData_name = null;
	private TextView metaData_language_etc = null;
	private TextView lawRateInfo = null;
	
	private View MovieBannerView = null;
	private View ButtonListView = null;
	private View PictureSize = null;
	private View guide_bar = null;
	private View help_bar = null;
	
	private TextView guide_red = null;
	private TextView guide_title = null;
	
	public ImageView bt_skip_backward = null;
	public ImageView bt_rew = null;
	public ImageView bt_play = null;
	public ImageView bt_ff = null;
	public ImageView bt_skip_forward = null;
	public ImageView bt_repeat = null;
	
	public TextView spinner_bar_value = null;
	
	public Metadata metadata = null;
	public TvManager mTVService = null;
	public Toast toast = null;
	
	private SharedPreferences mPerferences =null;
	/******** Quick Menu Setting ***************/
	private final static int SET_PICTURE = 0;
	private final static int SET_SOUND = 1;
	private final static int SET_3D = 2;
	private final static int SET_REPEAT_MODE = 3;
	private final static int SET_SLEEP_TIMER = 4;
	private final static int SET_TV_APP = 5;
	private final static int SET_SYS_SETTING = 6;
	
	private QuickMenu quickMenu = null;
	private QuickMenuVideoAdapter quickMenuAdapter = null;
	
	private int mSleepTimeHour = 0, mSleepTimeMin = 0;
	
	private int qm_focus_item = 0;
	
	private int repeatIndex = 0;
	
	/************Animation********************/
	private static final float banner_y = 1005f;
	private static final float banner_h = 75f;
    private static final long bannerAnimTime = 200;
	/******** Handler  parameter initial *******/
	public Handler handler = null;
	
	private static int Minute = 0;
    private static int Hour = 0;
    private static int Second = 0;
    
	/******** Timer and TimerTask***************/
    private Timer timer = null;
	
	private TimerTask task_getduration = null;
	private TimerTask task_getCurrentPositon = null;
	private TimerTask task_info_show = null;
	private TimerTask task_key_event = null;
	private TimerTask task_hide_controler = null;
	private TimerTask task_hide_button = null;
	private TimerTask task_meta_data_show = null;
	private TimerTask task_playbackquery = null;
	private TimerTask task_delay_task = null;
	private TimerTask task_audio_spdif_output_show = null;
	private TimerTask task_meta_data_query_delay = null;
	private TimerTask task_picture_size_show = null;
	private TimerTask task_message_time_out = null;
	private TimerTask task_hide_quickmenu = null;
	private TimerTask task_hide_dolby = null;
	private TimerTask task_query_subtitle_audio = null;
	
	public TitleBookMark mTitleBookMark = null;
	public BookMark mVideoBookMark = null;
	
	/******** DivxParser *****************/
	public DivxParser Divx= null;
	public String ButtonNum = "";
	public int mClick_DPAD_DOWN_NUM = 0;
	
	/************ConfirmMessage*****************/
	private ConfirmMessage long_msg = null;
	private ConfirmMessage short_msg = null;
	
	private PopupMessage msg_hint = null;
	/************control_button_list************/
	//private control_button_list bt = null;
	/************MediaApplication***************/
	private MediaApplication map = null;
	public  ArrayList<FileInfo> videoList;
	
	/************Intent*****************/
	public boolean isAnywhere;
	
	/***********Usb Controler************************/
	private UsbController mUsbCtrl = null;
	
	/************MediaPlayer status******************/
	private boolean isMediaplayIdle = true;
	
	//long time1, time2;
	
	
	private int mActivityPauseFlag = 0;
	private ContentResolver m_ContentMgr = null;
	public class SurfaceListener implements SurfaceHolder.Callback{

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {	
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			
			/*time2 = System.currentTimeMillis();
	        Log.e(TAG, "Step2: time = " + (time2 - time1));
	        time1 = time2;*/
			
			try {
				mPlayer.reset();
				isMediaplayIdle = true;
				mPlayer.setOnPreparedListener(videoPreparedListener);
		        mPlayer.setOnCompletionListener(videoCompletionListener);
		        mPlayer.setOnInfoListener(VideoInfoListener);
		        mPlayer.setOnErrorListener(ErrorListener);
	        	mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
	        	if(currIndex < filePathArray.size())
	        	{
					mPlayer.setPlayerType(6); // use RTK_MediaPlayer
	        		mPlayer.setDataSource(filePathArray.get(currIndex));
					mPlayer.setParameter(1400, 1); // Need load DRM
					mPlayer.setParameter(1500, 1); // Need change LastPlayPath
					mPlayer.prepareAsync();
					mPlayer.setDisplay(sView.getHolder());
	        	}
				//showController(LOADING);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();			
				PopupErrorMessageShow(VideoPlayerActivity.this.getResources().getString(R.string.msg_unsupport), TimerDelay.delay_2s);
			} catch (IOException e) {
				e.printStackTrace();				
				PopupErrorMessageShow(VideoPlayerActivity.this.getResources().getString(R.string.msg_unsupport), TimerDelay.delay_2s);
			}
			
			int index = currIndex + 1;
			filename.setText(getApplicationContext().getText(R.string.title)+": "+fileTitleArray.get(currIndex));
			file_number.setText(index + "/" + filePathArray.size());
	    	play.setImageResource(R.drawable.au_play);
	    	
			
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
		}
		
	}
	
    private OnErrorListener ErrorListener = new OnErrorListener(){

		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			switch (what)
			{
				case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
				{
					Log.e(TAG, "MediaServer died, finish mySelf!");
					mPlayer.reset();
					mPlayer.setOnPreparedListener(null);
					mPlayer.setOnCompletionListener(null);
					mPlayer.setOnInfoListener(null);
					mPlayer.setOnErrorListener(null);
					isMediaplayIdle = true;
					
					long_msg.setMessage(VideoPlayerActivity.this.getResources().
			    	getString(R.string.Media_Server_Die));
					long_msg.setButtonText(VideoPlayerActivity.this.getResources().getString(R.string.msg_yes));
					long_msg.left.setVisibility(View.INVISIBLE);
					long_msg.right.setVisibility(View.INVISIBLE);
					long_msg.confirm_bt.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							long_msg.dismiss();
							Intent intent = new Intent(VideoPlayerActivity.this, MediaBrowser.class);
							startActivity(intent);
							VideoPlayerActivity.this.finish();
						}		
					});
					long_msg.show();
				}
				break;
				case MediaPlayer.MEDIA_ERROR_UNKNOWN:
				{
					Log.e(TAG, "Video resolution not supported");
					mPlayer.reset();
					mPlayer.setOnPreparedListener(null);
					mPlayer.setOnCompletionListener(null);
					mPlayer.setOnInfoListener(null);
					mPlayer.setOnErrorListener(null);
					isMediaplayIdle = true;
					
					switch(extra)
					{
					case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
					case 0:
					case 5:
					case 1:  //NAV_FAILURE_UNSUPPORTED_VIDEO_CODEC
						PopupErrorMessageShow(VideoPlayerActivity.this.getResources().getString(R.string.msg_unsupport), TimerDelay.delay_2s);
						break;
					case 4:  //NAV_FAILURE_UNSUPPORTED_DIVX_DRM_VERSION
						PopupErrorMessageShow(VideoPlayerActivity.this.getResources().getString(R.string.msg_unauthorised_divx_drm_version), TimerDelay.delay_2s);
						break;
					case 0x100: //MEDIA_ERR_FRAME_SIZE
						PopupErrorMessageShow(VideoPlayerActivity.this.getResources().getString(R.string.FatalErrorCode_Video_Resolution), TimerDelay.delay_2s);
						break;
					case 0x101: //MEDIA_ERR_FRAME_RATE
						break;
					case MediaPlayer.MEDIA_ERROR_MALFORMED:
						PopupErrorMessageShow(VideoPlayerActivity.this.getResources().getString(R.string.FatalErrorCode_Video_Bitstream), TimerDelay.delay_2s);
						break;
					}
					
					/*short_msg.setTitle(VideoPlayerActivity.this.getResources().getString(R.string.msg_hint));
					short_msg.setMessage(VideoPlayerActivity.this.getResources().
			    	getString(R.string.FatalErrorCode_Video_Resolution));
					short_msg.setButtonText(VideoPlayerActivity.this.getResources().getString(R.string.msg_yes));
					short_msg.left.setVisibility(View.INVISIBLE);
					short_msg.right.setVisibility(View.INVISIBLE);
					
					short_msg.setOnDismissListener(new OnDismissListener(){

						@Override
						public void onDismiss() {
							VideoPlayerActivity.this.finish();
						}
						
					});
					
					short_msg.confirm_bt.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View arg0) {
							short_msg.setOnDismissListener(null);
							short_msg.dismiss();
							VideoPlayerActivity.this.finish();
						}		
					});	
					
					
					short_msg.show();*/
				break;
				}
				default:
				{
					Log.e(TAG, "Get video error: " + what + ", ignore it!");
					mPlayer.reset();
					mPlayer.setOnPreparedListener(null);
					mPlayer.setOnCompletionListener(null);
					mPlayer.setOnInfoListener(null);
					mPlayer.setOnErrorListener(null);
					isMediaplayIdle = true;
					
					/*long_msg.setTitle(VideoPlayerActivity.this.getResources().getString(R.string.msg_hint));
					long_msg.setMessage(VideoPlayerActivity.this.getResources().
			    	getString(R.string.Video_Play_Error)+" "+what
			    	+ ". "
			    	+ VideoPlayerActivity.this.getResources().getString(R.string.file_jump));
					long_msg.setButtonText(VideoPlayerActivity.this.getResources().getString(R.string.msg_yes));
					long_msg.setOnDismissListener(new OnDismissListener(){
        				public void onDismiss() {
        					VideoPlayerActivity.this.finish();
        				}
        			});
					long_msg.setKeyListener(true);
					long_msg.confirm_bt.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							if(long_msg.confirm_text.getText().toString().compareTo
							(VideoPlayerActivity.this.getResources().getString(R.string.msg_yes))==0)
							{
								switch_file(KeyEvent.KEYCODE_CHANNEL_UP);
							}else if(long_msg.confirm_text.getText().toString().compareTo
									(VideoPlayerActivity.this.getResources().getString(R.string.msg_no))==0)
							{
								VideoPlayerActivity.this.finish();
							}
							long_msg.setOnDismissListener(null);
							long_msg.dismiss();
						}		
					});
					long_msg.show();*/
				}
				break;
			}
			return true;
		}
    	
    };
	
	private OnInfoListener VideoInfoListener = new OnInfoListener(){

		@Override
		public boolean onInfo(MediaPlayer mp, int what, int extra) {
			Log.e(TAG, "VideoInfoListener" + "---what = "+what+"----");
			switch(what)
			{
			case 0x10000003: //UNKNOWN_FORMAT /* FOR AUDIO */
			{
				if(!isAudio_Error)
				{
					/*if(isStartPlay)
					{
						mPlayer.pause();
						play.setImageResource(R.drawable.au_pause);
	    				if(bt_play.isFocused())
	    					bt_play.setImageResource(R.drawable.player_common_pause_focus_button_list);
	    				else
	    					bt_play.setImageResource(R.drawable.player_common_pause_button_list);
	    				
	    				isStartPlay = false;
					}*/
					
					
    				msg_hint.setMessage(VideoPlayerActivity.this.getResources().
			    	getString(R.string.FatalErrorCode_Audio_Unknow_Format));
    				if(isRight2Left)
    					msg_hint.setMessageRight();
    				else
    					msg_hint.setMessageLeft();
    				msg_hint.show();
					
					if(task_message_time_out != null)
					{
						task_message_time_out.cancel();
						task_message_time_out = null;
					}
					
					task_message_time_out = new TimerTask(){

						@Override
						public void run() {
							handler.sendEmptyMessage(HandlerControlerVariable.HIDE_POPUP_MESSAGE);
							handler.sendEmptyMessage(HandlerControlerVariable.MEDIA_PLAYER_START);
						}
						
					};
					
					if(timer != null)
						timer.schedule(task_message_time_out, TimerDelay.delay_2s);
					
					isAudio_Error = true;
				}
			}
			break;
			case 0x20000003: //FATALERR_VIDEO_MPEG2DEC_UNKNOWN_FRAME_RATE
			case 0x2000000d: //FATALERR_VIDEO_MPEG4DEC_UNKNOWN_FRAME_RATE_CODE
			{
				long_msg.setMessage(VideoPlayerActivity.this.getResources().
		    	getString(R.string.FatalErrorCode_Video_Frame_Rate));
		    	long_msg.setButtonText(VideoPlayerActivity.this.getResources().getString(R.string.msg_yes));
				long_msg.left.setVisibility(View.INVISIBLE);
				long_msg.right.setVisibility(View.INVISIBLE);
    			long_msg.confirm_bt.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						long_msg.dismiss();
					}		
				});
    			if(isRight2Left)
    				long_msg.setMessageRight();
    			else
    				long_msg.setMessageLeft();
				long_msg.show();
			}
			break;
			case 0x20000008: //FATALERR_VIDEO_MPEG2DEC_UNSUPPORTED_RESOLUTION
			{

				long_msg.setMessage(VideoPlayerActivity.this.getResources().
		    	getString(R.string.FatalErrorCode_Video_Resolution));
				long_msg.setButtonText(VideoPlayerActivity.this.getResources().getString(R.string.msg_yes));
				long_msg.left.setVisibility(View.INVISIBLE);
				long_msg.right.setVisibility(View.INVISIBLE);
    			long_msg.confirm_bt.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						long_msg.dismiss();
					}		
				});
    			if(isRight2Left)
    				long_msg.setMessageRight();
    			else
    				long_msg.setMessageLeft();
				long_msg.show();
			}
			break;
			//case 720: /*mPlayer.MEDIA_INFO_FE_PB_ENTER_TRICKPLAY*/
				/*tpt.setVisibility(View.VISIBLE);
			break;
			case 721: /*mPlayer.MEDIA_INFO_FE_PB_LEAVE_TRICKPLAY*/
				/*tpt.setVisibility(View.INVISIBLE);
			break;*/
			case 722: /*mPlayer.MEDIA_INFO_FE_PB_RESET_SPEED*/
			{
				if(mPlayer.getCurrentPosition() < 5000)
				{
					play.setImageResource(R.drawable.au_play);
					isforward = false;
					forward_press = 1;
					isrewind = false;
					rewind_press = 1;
					
					statusBannerShowControl(TimerDelay.delay_6s);
				}
			}
			break;
			default:
			break;
			}
			return false;
		}
		
	};
	
	private OnPreparedListener videoPreparedListener = new OnPreparedListener(){

		@Override
		public void onPrepared(MediaPlayer mp) {
			
			try {
			isMediaplayIdle = false;
			/*time2 = System.currentTimeMillis();
	        Log.e(TAG, "Step4: time = " + (time2 - time1));
	        time1 = time2;*/
			//Log.e(TAG, "###Width = "+mPlayer.getVideoWidth() +"####Height = "+mPlayer.getVideoHeight());
			if(isHasVideo())
			{
					//if(mPlayer.getVideoWidth()==3840 && mPlayer.getVideoHeight() == 2160)
					//{
						{
							Intent intent = new Intent();
							intent.setAction("MediaScannerSuspend");
							sendBroadcast(intent);
						}
						
						isDivxVODFile = isDivxVODFile();
						Log.v(TAG, "#######isDivxVODFile = "+isDivxVODFile);
						if(isDivxVODFile == 1)
						{
							Divx.execSetGetNavProperty(DivxParser.NAVPROP_DIVX_DRM_QUERY); //MediaPlayer start
						}else
						{
							Log.v(TAG, "########resume_index = "+resume_index);
							if(resume_index >= 0)
							{
								Divx.ResumePlay(resume_index);
								resume_index = -1;
							}
							else
							{
								int index = mVideoBookMark.findBookMark(fileTitleArray.get(currIndex));
								if(index < 0)
								{
									Log.v(TAG, "playfile");
									Divx.PlayFile();
								}
								else
								{
									Log.v(TAG, "resume play");
									Divx.ResumePlay(index);
								}
									
							}
						}
						
						{
							Intent intent = new Intent();
							if(isProgressiveMovie())
								intent.setAction("ProgressiveMovie");
							else
								intent.setAction("InterlaceMovie");
							sendBroadcast(intent);
						}
						
						
						if(isHasTrickPlayTrack() == 1)
						{
							tpt.setVisibility(View.VISIBLE);
						}else
						{
							tpt.setVisibility(View.INVISIBLE);
						}
						
					//}else
					//{
					//	Divx.execSetGetNavProperty(Divx.NAVPROP_DIVX_DRM_QUERY); //MediaPlayer start
					//}
					
					/*time2 = System.currentTimeMillis();
			        Log.e(TAG, "Step5: time = " + (time2 - time1));
			        time1 = time2;*/
					
					getAudioTrackInfo();
					getSubtitleInfo();
					querySubtitleInfo6Times();
					SPU_ENABLE = Divx.SPU_ENABLE;
					
					SetDolby();
					
					if(task_delay_task != null)
					{
						task_delay_task.cancel();
						task_delay_task = null;
					}
					task_delay_task= new TimerTask(){
						@Override
						public void run() {
							handler.sendEmptyMessage(HandlerControlerVariable.DELAY_SHOW_BANNER);
						} 
					};
					
					if(timer != null)
						timer.schedule(task_delay_task, 1500);
					
					metadata = mPlayer.getMetadata(false, true);
			}else
			{
				skipUnsupportFile(currIndex);
			}
			} catch (IllegalStateException e) {
    			e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
    };
    
    private OnCompletionListener videoCompletionListener = new OnCompletionListener()
    {
		@Override
		public void onCompletion(MediaPlayer mp) {
			
			isAudio_Error = false;
			
			switch (repeat_mode)
			{
			case REPEAT_SINGLE:
				{
					if(Divx.m_titleNum > 1)
					{
						int jump_to_titleNum = Divx.m_current_title_num + 1;
			     		if(jump_to_titleNum > Divx.m_titleNum)
			     		{
			     			jump_to_titleNum = 1;
			     			if(Divx.drmStatus == DivxParser.NAV_DIVX_DRM_RENTAL_AUTHORIZED)
			     			{
			     				mPlayer.execResetDivxState();
			     				handler.sendEmptyMessage(HandlerControlerVariable.MEDIAPLAY_INIT);
			     			}else
			     			{
			     				switch_title(jump_to_titleNum);
					     		mPlayer.start();
			     			}
			     		}else
			     		{
			     			switch_title(jump_to_titleNum);
				     		mPlayer.start();
			     		}
					}else
					{
						handler.sendEmptyMessage(HandlerControlerVariable.MEDIAPLAY_INIT);
					}
				}
				break;
			case REPEAT_ALL:
				{
					if(Divx.m_titleNum > 1)
					{
						int jump_to_titleNum = Divx.m_current_title_num + 1;
			     		if(jump_to_titleNum <= Divx.m_titleNum)
			     		{
			     			switch_title(jump_to_titleNum);
			     			mPlayer.start();
			     		}else
			     		{
			     			currIndex++;
				     		if(currIndex > filePathArray.size()-1)
				     			currIndex = 0;
				     		if(Divx.drmStatus == DivxParser.NAV_DIVX_DRM_RENTAL_AUTHORIZED)
				     			mPlayer.execResetDivxState();
				     		handler.sendEmptyMessage(HandlerControlerVariable.MEDIAPLAY_INIT);
			     		}
					}else
					{
						currIndex++;
			     		if(currIndex > filePathArray.size() -1)
			     			currIndex = 0;
			     		handler.sendEmptyMessage(HandlerControlerVariable.MEDIAPLAY_INIT);
					}
				}
				break;
			case REPEAT_OFF:
				{
					if(Divx.m_titleNum > 1)
					{
						int jump_to_titleNum = Divx.m_current_title_num + 1;
			     		if(jump_to_titleNum <= Divx.m_titleNum)
			     		{
			     			switch_title(jump_to_titleNum);
			     			mPlayer.start();
			     		}else
			     		{
			     			isVideoPlayCompleteInRepeatOFFMode = true;
			     			if(Divx.drmStatus == DivxParser.NAV_DIVX_DRM_RENTAL_AUTHORIZED)
				     			mPlayer.execResetDivxState();
				     		VideoPlayerFinish();
			     		}
					}else
					{
						isVideoPlayCompleteInRepeatOFFMode = true;
						if(Divx.drmStatus == DivxParser.NAV_DIVX_DRM_RENTAL_AUTHORIZED)
			     			mPlayer.execResetDivxState();
			     		VideoPlayerFinish();
					}
				}
				break;
			}
		}
    };
    
    public void setOnFocusChangeListener(OnFocusChangeListener focus)
	{
		 bt_skip_backward.setOnFocusChangeListener(focus);
	     bt_rew.setOnFocusChangeListener(focus);
	     bt_play.setOnFocusChangeListener(focus);
	     bt_ff.setOnFocusChangeListener(focus);
	     bt_skip_forward.setOnFocusChangeListener(focus);
	     bt_repeat.setOnFocusChangeListener(focus);		
	}
	
	public void setOnClickListener(OnClickListener click)
	{
		bt_skip_backward.setOnClickListener(click);
        bt_rew.setOnClickListener(click);
        bt_play.setOnClickListener(click);
        bt_ff.setOnClickListener(click);
        bt_skip_forward.setOnClickListener(click);
        bt_repeat.setOnClickListener(click);
	}
    
    public OnFocusChangeListener focus = new OnFocusChangeListener(){

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
				if(bt_skip_backward == v)
				{
					if(hasFocus)
						bt_skip_backward.setImageResource(R.drawable.player_common_backward_focus_button_list);
					else
						bt_skip_backward.setImageResource(R.drawable.player_common_backward_button_list);
					return;
				}else if(bt_rew == v)
				{
					if(hasFocus)
						bt_rew.setImageResource(R.drawable.player_common_prev_focus_button_list_x2);
					else
						bt_rew.setImageResource(R.drawable.player_common_prev_button_list_x2);
		     		return;
				}else if(bt_play == v)
				{
					if(!isMediaplayIdle)
					{
						if(mPlayer.isPlaying() && !isforward && !isrewind)
						{
							if(hasFocus)
								bt_play.setImageResource(R.drawable.player_common_pause_focus_button_list);
							else
								bt_play.setImageResource(R.drawable.player_common_pause_button_list);
						}else
						{
							if(hasFocus)
								bt_play.setImageResource(R.drawable.player_common_play_focus_button_list);
							else
								bt_play.setImageResource(R.drawable.player_common_play_button_list);
						}
					}
					return;
				}else if(bt_ff == v)
				{
					if(hasFocus)
						bt_ff.setImageResource(R.drawable.player_common_next_focus_button_list_x2);
					else
						bt_ff.setImageResource(R.drawable.player_common_next_button_list_x2);
					return;
				}else if(bt_skip_forward == v)
				{
					if(hasFocus)
						bt_skip_forward.setImageResource(R.drawable.player_common_forward_focus_button_list);
					else
						bt_skip_forward.setImageResource(R.drawable.player_common_forward_button_list);
					return;
				}else if(bt_repeat == v)
				{
					if(hasFocus)
						bt_repeat.setImageResource(R.drawable.photo_player_repeat_on_focus_button_list);
					else
						bt_repeat.setImageResource(R.drawable.photo_player_repeat_on_button_list);
					return;
				}
				
			}
    	
    };
    
    private OnClickListener click = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(!isMediaplayIdle)
			{
				if(bt_skip_backward == v)
				{
					if(!isforward && !isrewind) //can not switch chapter when is rewinding or forwarding.
		     		{
		     			if(Divx.m_chapterNum > 1)
		     				switch_chapter(KeyEvent.KEYCODE_DPAD_DOWN);
		     			else
		     			{
		     				isSwitchToNextFile = false;
		     				switch_file_by_skip_key(false);
		     			}
		     		}
		     		else
		     		{
		     			/*toast.makeText(getApplicationContext(), 
								this.getResources().getString(R.string.msg_donot_switch_chapter),
		            		    Toast.LENGTH_SHORT).show();*/
		     			media_play_init(filePathArray.get(currIndex), fileTitleArray.get(currIndex));
		     		}
				}else if(bt_rew == v)
				{
     		        if(metadata != null && !metadata.getBoolean(Metadata.SEEK_BACKWARD_AVAILABLE))
     		        {
     			        toast.makeText(getApplicationContext(), 
     					    VideoPlayerActivity.this.getResources().getString(R.string.msg_seek_rewind_forbidden),
            		        Toast.LENGTH_SHORT).show();
     		        }else{
					    if(!mPlayer.isPlaying())
		     			    mPlayer.start();
		     		    rewind();
		     		    bt_play.setImageResource(R.drawable.gui_play);
                    }
				}else if(bt_play == v)
				{
					if(isforward)
					{
						mPlayer.fastforward(0);
						play.setImageResource(R.drawable.au_play);
						bt_play.setImageResource(R.drawable.gui_pause);
						isforward = false;
						forward_press = 1;
					}else if(isrewind)
					{
						mPlayer.fastrewind(0);
						play.setImageResource(R.drawable.au_play);
						bt_play.setImageResource(R.drawable.gui_pause);
						isrewind = false;
						rewind_press = 1;
					}else
					{
						if(mPlayer.isPlaying())
						{
							mPlayer.pause();
							play.setImageResource(R.drawable.au_pause);
							bt_play.setImageResource(R.drawable.gui_play);
							handler.removeMessages(HandlerControlerVariable.PROGRESS_CHANGED);
						}
						else
						{
							mPlayer.start();
							bt_play.setImageResource(R.drawable.gui_pause);
							play.setImageResource(R.drawable.au_play);
						}
					}
				}else if(bt_ff == v)
				{
     		        if(metadata != null && !metadata.getBoolean(Metadata.SEEK_FORWARD_AVAILABLE))
     		        {
     			        toast.makeText(getApplicationContext(), 
     					    VideoPlayerActivity.this.getResources().getString(R.string.msg_seek_forward_forbidden),
            		        Toast.LENGTH_SHORT).show();
     		        }else
     		        {
					    if(!mPlayer.isPlaying())
		     			    mPlayer.start();
					    forward();
					    bt_play.setImageResource(R.drawable.gui_play);
                    }
				}else if(bt_skip_forward == v)
				{
					if(!isforward && !isrewind) //can not switch  when is rewinding or forwarding.
		     		{
		     			if(Divx.m_chapterNum > 1)
		     				switch_chapter(KeyEvent.KEYCODE_DPAD_UP);
		     			else
		     			{
		     				isSwitchToNextFile = true;
		     				switch_file_by_skip_key(true);
		     			}
		     		}
		     		else
		     		{
		     			/*toast.makeText(getApplicationContext(), 
		     					this.getResources().getString(R.string.msg_donot_switch_chapter),
		            		    Toast.LENGTH_SHORT).show();*/
		     			currIndex++;
		     			media_play_init(filePathArray.get(currIndex), fileTitleArray.get(currIndex));
		     		}
				}else if(bt_repeat == v)
				{
					repeatIndex++;
					repeatIndex %= 3;
					setRepeatIcon(repeatIndex);
					
    				Editor editor = mPerferences.edit();
    				editor.putInt("repeatIndex", repeatIndex);
    				editor.commit();
				}
				
				buttonBannerShowControl(TimerDelay.delay_6s);
				statusBannerShowControl(TimerDelay.delay_6s);
				showCurrentInfo();
			}
		}
    };
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.v(TAG, "VidePlayerActivity onCreate");
    	
    	//time1 = System.currentTimeMillis();
    	
        super.onCreate(savedInstanceState);
        isRight2Left = MediaBrowserConfig.getRight2Left(getApplicationContext());
        if(isRight2Left)
        	setContentView(R.layout.video_player_a);
        else
        	setContentView(R.layout.video_player);
        m_ContentMgr = getApplicationContext().getContentResolver();
        init();
	}
	
	@Override
	protected void onStart() {
    	Log.v(TAG, "VidePlayerActivity onStart");
		super.onStart();
	}
	
	@Override
	protected void onResume() {
		Log.v(TAG, "VidePlayerActivity onResume");
		mTVService.setAndroidMode(0);
		
		timer = new Timer(true);
		mActivityPauseFlag = 0;
		mUsbCtrl.RegesterBroadcastReceiver();
		
		play.setImageResource(R.drawable.av_play);
	    
	    IntentFilter filter = new IntentFilter("com.rtk.mediabrowser.PlayService");
		registerReceiver(receiver, filter);

	    showInBackground = false;
	    IntentFilter filter2 = new IntentFilter("com.realtek.TVShowInBackGround");
		registerReceiver(OnEngMenupRecever,filter2);
		new Thread(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(mActivityPauseFlag != 1)
				{

					int mins = getSleepTimeValue();
					mSleepTimeHour = mins / 60;
					mSleepTimeMin = mins % 60;
					
					if(quickMenu != null)
					{
						if(quickMenu.isShowing())
						{
							handler.sendEmptyMessage(HandlerControlerVariable.MSG_REFRESH_TIMER);
						}
					}
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
        	
        }).start();
		if(quickMenuAdapter == null)
			quickMenuAdapter = new QuickMenuVideoAdapter(this);
		quickMenuAdapter.notifyDataSetChanged();
		getInitTimer();
		
		if(quickMenu != null && quickMenu.isShowing())
			hide_quick_menu_delay(TimerDelay.delay_6s);
		
		isOnPause = false;
		
	    super.onResume();
    }
	private int getSleepTimeValue()
	{
		int sethour = Settings.Global.getInt(m_ContentMgr, "SetTimeHour", 0);
		int setmin = Settings.Global.getInt(m_ContentMgr, "SetTimeMinute", 0);
		int setsec = Settings.Global.getInt(m_ContentMgr, "SetTimeSecond", 0);
		int totalmin = Settings.Global.getInt(m_ContentMgr, "TotalMinute", 0);
		Log.d(TAG, "SetTimeHour:" + sethour + ",SetTimeMinute:" + setmin +",SetTimeSec:" + setsec + ",TotalMinute:" + totalmin);
		Date curDate = new Date(System.currentTimeMillis()) ;
		int curhours =  curDate.getHours();
		int curminutes = curDate.getMinutes();
		int curSecs = curDate.getSeconds();
		Date setData = new Date(curDate.getYear(), curDate.getMonth(), curDate.getDate(), sethour, setmin, setsec);
		
		int diftime = 0;
		if(curDate.after(setData)&&totalmin != 0){
			diftime = totalmin - ((curhours * 60 + curminutes) - (sethour * 60 + setmin));
		}
		return diftime;
	}
	
	@Override
	protected void onPause() {
		Log.v(TAG, "VidePlayerActivity onPause");
		Log.e(TAG, "isNeedExecuteOnPause = "+isNeedExecuteOnPause);
		Log.e(TAG, "**********DrmStatus = "+ Divx.drmStatus);
		/*if(handler != null)
		{
			handler.removeMessages(HandlerControlerVariable.DELAY_SHOW_BANNER);
			handler.removeMessages(HandlerControlerVariable.GET_DURATION);
			handler.removeMessages(HandlerControlerVariable.PROGRESS_CHANGED);
			handler.removeMessages(HandlerControlerVariable.SET_DIVX_METADATA_LAWRATE_INFO);
			handler.removeMessages(HandlerControlerVariable.MEDIAPLAY_INIT);
			handler.removeMessages(HandlerControlerVariable.SWITCH_TITLE_QUERY);
			handler.removeMessages(HandlerControlerVariable.SET_DIVX_CHAPTER_INFO);
			handler.removeMessages(HandlerControlerVariable.SET_DIVX_METADATA_SUBTITLE_INFO);
			handler.removeMessages(HandlerControlerVariable.SET_DIVX_METADATA_AUDIO_INFO);
			handler.removeMessages(HandlerControlerVariable.SET_DIVX_METADATA_CHAPTER_INFO);
			handler.removeMessages(HandlerControlerVariable.SET_CHAPTER_NUM);
		}*/
		mActivityPauseFlag = 1;
		if (showInBackground || !isNeedExecuteOnPause)
		{
			isNeedExecuteOnPause = true;
		}
		else //if(isNeedExecuteOnPause)
		{
			isOnPause = true;
			
			if(Divx != null && mVideoBookMark != null)
	    	{
	    		boolean execError = false;
	    		
	    		if((Divx.drmStatus == DivxParser.NAV_DIVX_DRM_NONE
	    		|| Divx.drmStatus == DivxParser.NAV_DIVX_DRM_AUTHORIZED
	    		|| Divx.drmStatus == DivxParser.NAV_DIVX_DRM_RENTAL_AUTHORIZED)
	    		&& !Divx.isConfirmMsgDismissByBackKey
	    		&& !isVideoPlayCompleteInRepeatOFFMode)
	    		{	
	    			try {
	        			Divx.execSetGetNavProperty(DivxParser.NAVPROP_INPUT_GET_NAV_STATE);
	        		} catch (IllegalArgumentException e) {
	        			e.printStackTrace();
	        			execError = true;
	        		} catch (SecurityException e) {
	        			e.printStackTrace();
	        			execError = true;
	        		} catch (IllegalStateException e) {
	        			e.printStackTrace();
	        			execError = true;
	        		}
	        		
	        		if (execError == false && Divx.outputArray != null)
	        		{
	        			mVideoBookMark.addBookMark(fileTitleArray.get(currIndex), curr_subtitle_stream_num, SPU_ENABLE, curr_audio_stream_num, Divx.drmStatus, isDivxVODFile, Divx.outputArray);
	        			mVideoBookMark.writeBookMark();
	        			map.setIsFromVideoPlayer(true);
	        			map.setStopedFileName(fileTitleArray.get(currIndex));
	        		}
	    		}
	    	}
	    	
			if(timer != null)
			{
				timer.cancel();
				timer = null;
			}
			
	    	if(!isMediaplayIdle && !showInBackground)
	    	{
	    		try {
	    			mPlayer.stop();
	    			mPlayer.setOnPreparedListener(null);
		        	mPlayer.setOnCompletionListener(null);
		        	mPlayer.setOnInfoListener(null);
		        	mPlayer.setDisplay(null);
		        	mPlayer.reset();
		        	isMediaplayIdle = true;
	    		} catch (IllegalStateException e) {
        			e.printStackTrace();
	    		}
	        	
	    	}
			play.setImageResource(R.drawable.av_pause);
			
			if(mUsbCtrl != null)
			{
				mUsbCtrl.UnRegesterBroadcastReceiver();
			}
			
			if(receiver != null)
			{
				unregisterReceiver(receiver);
				receiver = null;
			}
			if(OnEngMenupRecever != null)
			{
				unregisterReceiver(OnEngMenupRecever);
			}
			if(toast != null)
			{
				toast.cancel();
			}
			
			Intent intent = new Intent();
			intent.setAction("MediaScannerResume");
			sendBroadcast(intent);
		}
		//else
		//{
		//	isNeedExecuteOnPause = true;
		//}
    	
		super.onPause(); 
    }
	
    @Override
	 public void onStop(){
    	Log.v(TAG, "VidePlayerActivity onStop");
		super.onStop();
    }
    
    @Override
	protected void onDestroy() {
    	Log.v(TAG, "VidePlayerActivity onDestroy");
		super.onDestroy();
		unregisterReceiver(mPlayReceiver);
	}
    
	@SuppressWarnings("static-access")
	@Override    
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i(TAG, "keyCode = "+keyCode);
		
		if(isOnPause)
			return true;
		
		switch (keyCode) {
		case 82:
		{
			
			int val1, val2, val3, val4;
			val1 = (int) (100.0 / 1080.0 * map.getScreenHeight());
			val2 = (int) (315.0 / 1920.0 * map.getScreenWidth());
			val3 = (int) (18.0 / 1920.0 * map.getScreenWidth());
			val4 = (int) (18.0 / 1080.0 * map.getScreenHeight());
			PopupMessageShow(VideoPlayerActivity.this.getResources().getString(R.string.toast_not_available),
 			R.drawable.message_box_bg2, val1, val2, Gravity.LEFT|Gravity.BOTTOM, val3, val4, TimerDelay.delay_4s);
		}
		return true;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
		{
			if(ButtonListView.isShown())
			{
				buttonBannerShowControl(TimerDelay.delay_6s);
			}else
			{
				switch_title_by_key(true);
			}
		}
		break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
		{
			if(ButtonListView.isShown())
			{
				buttonBannerShowControl(TimerDelay.delay_6s);
			}else
			{
				switch_title_by_key(false);
			}
		}
		break;
		case 227: //for L4300 KeyEvent.KEYCODE_QUICK_MENU:
		case KeyEvent.KEYCODE_Q:
		{
			hide_all();
			// Lazy load QuickMenu UI
			if (quickMenu == null)
			{
				// createQuickMenu()
     	        quickMenuAdapter = new QuickMenuVideoAdapter(this);
     	        quickMenu=new QuickMenu(
     					this, quickMenuAdapter);
     	        quickMenu.setAnimationStyle(R.style.QuickAnimation);
     	        OnItemClickListener quickmenuItemClickListener = new OnItemClickListener()
     	        {
     	        	@Override
     				public void onItemClick(AdapterView<?> arg0, View arg1, int position,
     						long arg3) {
     					// TODO Auto-generated method stub
     	        		setQuickMenuItem(position, true);
     					quickMenuAdapter.notifyDataSetChanged();
     				}
     	        };
     	        OnKeyListener quickmenuKeyListener = new OnKeyListener()
     	        {

     	        	@Override
     				public boolean onKey(View v, int keyCode, KeyEvent event) {
     					// TODO Auto-generated method stub
     					if (event.getAction() == KeyEvent.ACTION_DOWN)
     		        	{
     						
     						switch(keyCode)
     						{
	     						case KeyEvent.KEYCODE_DPAD_RIGHT:
	     						{
	     							if(qm_focus_item == SET_REPEAT_MODE || qm_focus_item == SET_SLEEP_TIMER)
	     			        			setQuickMenuItem(qm_focus_item, true);
	     			        		quickMenuAdapter.notifyDataSetChanged();
	     			        		hide_quick_menu_delay(TimerDelay.delay_6s);
	     			        		return true;
	     						}
	     						case KeyEvent.KEYCODE_DPAD_LEFT:
	     						{
	     							if(qm_focus_item == SET_REPEAT_MODE || qm_focus_item == SET_SLEEP_TIMER)
	     			        			setQuickMenuItem(qm_focus_item, false);
	     			        		quickMenuAdapter.notifyDataSetChanged();
	     			        		hide_quick_menu_delay(TimerDelay.delay_6s);
	     			        		return true;
	     						}
	     						case KeyEvent.KEYCODE_DPAD_UP:
	     			        	{
	     			        		ListView quickMenuContent = quickMenu.getListView();
	         						int position = quickMenuContent.getSelectedItemPosition();
	     			        		if(position == 0)
	     			        		{
	     			        			quickMenuContent.setSelection(quickMenuContent.getCount()-1);
	     			        		}
	     			        		hide_quick_menu_delay(TimerDelay.delay_6s);
	     			        		return false;		
	     			        	}
	     						case KeyEvent.KEYCODE_DPAD_DOWN:
	     			        	{
	     			        		ListView quickMenuContent = quickMenu.getListView();
	         						int position = quickMenuContent.getSelectedItemPosition();
	     			        		if(position == quickMenuContent.getCount()-1)
	     			        		{
	     			        			quickMenuContent.setSelection(0);
	     			        		}
	     			        		hide_quick_menu_delay(TimerDelay.delay_6s);
	     			        		return false;
	     			        	}
	     						case KeyEvent.KEYCODE_Q:
	     						case 227:
	     						case KeyEvent.KEYCODE_BACK:
	     						{
	     							quickMenu.dismiss();
	     						}
	     						break;
	     						case 231: //for L4300 KeyEvent.KEYCODE_STOP:
	     						case KeyEvent.KEYCODE_K:
	     						{
	     							VideoPlayerActivity.this.finish();
	     						}
	     						break;
	     						case  KeyEvent.KEYCODE_MENU:
	     						{
	     							quickMenu.dismiss();
	     							PopupMessageShow(VideoPlayerActivity.this.getResources().getString(R.string.toast_not_available),
			     					R.drawable.message_box_bg2, 100, 300, Gravity.LEFT|Gravity.BOTTOM, 18, 18, TimerDelay.delay_4s);
	     						}
	     						return true;
	     						default:
	     						break;
     						}
     		        	}
     					return false;

     				}
     	        };
     	        OnItemSelectedListener quickmenuItemSelectedListener = new OnItemSelectedListener()
     	        {
     	        	ListView lv = quickMenu.getListView();
     	        	@Override
     				public void onItemSelected(AdapterView<?> arg0, View arg1,
     						int position, long arg3) {
     	        		qm_focus_item = position;
     					for(int i = 0; i < lv.getCount(); i++ )
     					{
     						quickMenuAdapter.setVisibility(i, View.INVISIBLE);
     					}
     					if(position == SET_REPEAT_MODE || position == SET_SLEEP_TIMER)
     						quickMenuAdapter.setVisibility(position, View.VISIBLE);
     					quickMenuAdapter.notifyDataSetChanged();
     				}

     				@Override
     				public void onNothingSelected(AdapterView<?> arg0) {
     				}
     	        };
     	        
     	        quickMenu.AddOnItemClickListener(quickmenuItemClickListener);
     	        quickMenu.AddOnItemSelectedListener(quickmenuItemSelectedListener);
     	        quickMenu.AddOnKeyClickListener(quickmenuKeyListener);
			}
			
			if(quickMenu.isShowing() == true)
				quickMenu.dismiss();
    		else
    		{
    			quickMenu.showQuickMenu(14,14);
    			hide_quick_menu_delay(TimerDelay.delay_6s);
    		}
		}
		return true;
		case 165: //for L4300 KeyEvent.KEYCODE_INFO:
		case KeyEvent.KEYCODE_M:
		{
			if(!isMediaplayIdle)
			{
				if(MovieBannerView.isShown())
				{
					animateHideBanner();
					if(task_getCurrentPositon != null)
					{
						task_getCurrentPositon.cancel();
						task_getCurrentPositon = null;
					}
					if(task_hide_controler != null)
					{
						task_hide_controler.cancel();
						task_hide_controler = null;
					}
				}
				else 
				{
					statusBannerShowControl(TimerDelay.delay_60s);
					showCurrentInfo();
				}
			}
		}
		return true;
		case KeyEvent.KEYCODE_BACK:
		{
			Intent intent= new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("FileName", fileTitleArray.get(currIndex));
			intent.putExtras(bundle);
			setResult(StopPlay, intent);
		}
		break;
		case 231:  //for L4300 KeyEvent.KEYCODE_STOP
		case 257:  //DISPLAY KEY in REALTECK RCU
		case KeyEvent.KEYCODE_K:   // As [Stop] key
		{
			Intent intent= new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("FileName", fileTitleArray.get(currIndex));
			intent.putExtras(bundle);
			
			setResult(StopPlay, intent);
			this.finish();
		}
		return true;
		case KeyEvent.KEYCODE_ESCAPE: //for L4300 KeyEvent.KEYCODE_ESCAPE
		case KeyEvent.KEYCODE_J:   // As [Exit] key
		{
			setResult(ResultCodeFinishVideoBrowser);
			this.finish();
		}
		return true;
		case KeyEvent.KEYCODE_I:
		{
			if(MediaBrowserConfig.HAVE_3DMODE)
			{
				FragmentManager fragmentManager = getFragmentManager();
				if (fragmentManager
						.findFragmentById(R.id.mode_3d_setting_fragment_layout) != null)
					fragmentManager.popBackStack(FragmentMode3DSetting.TAG,
							FragmentManager.POP_BACK_STACK_INCLUSIVE);
				else
					fragmentManager
							.beginTransaction()
							.replace(R.id.mode_3d_setting_fragment_layout,
									FragmentMode3DSetting.NewInstance(this))
							.addToBackStack(FragmentMode3DSetting.TAG).commit();
			}
		}
		return true;
		case 251: // the key in the left of sleep key
		case KeyEvent.KEYCODE_B:  //Show button list view
		{
			if(ButtonListView.isShown())
			{
				hideController(BUTTON_LIST);
				hideController(MOVIE_BANNER);
				if(task_getCurrentPositon != null)
				{
					task_getCurrentPositon.cancel();
					task_getCurrentPositon = null;
				}
			}
			else
			{
				hide_lawrate_metadata_info();
				hide_picture_size_ui();
				
				buttonBannerShowControl(TimerDelay.delay_6s);
				bt_skip_backward.requestFocus();
			}
		}
		return true;
		case KeyEvent.KEYCODE_0:
		{
			if(Divx.m_chapterNum > 1)
     		{
				hideController(BUTTON_LIST);
    			hide_lawrate_metadata_info();
    			hide_picture_size_ui();
    			
    			if(ButtonNum != null)
    				ButtonNum = ButtonNum + "0";
     			
    			execChapterJumpTimer();
     		}
		}
		return true;
     	case KeyEvent.KEYCODE_1:
     	{
     		if(Divx.m_chapterNum > 1)
     		{
     			hideController(BUTTON_LIST);
    			hide_lawrate_metadata_info();
    			hide_picture_size_ui();
    			
     			ButtonNum = ButtonNum + "1";
     			execChapterJumpTimer();
     		}
     	}
     	return true;
     	case KeyEvent.KEYCODE_2:
     	{
     		if(Divx.m_chapterNum > 1)
     		{
     			hideController(BUTTON_LIST);
    			hide_lawrate_metadata_info();
    			hide_picture_size_ui();
    			
     			ButtonNum = ButtonNum + "2";
     			execChapterJumpTimer();
     		}
     	}
     	return true;
     	case KeyEvent.KEYCODE_3:
     	{
     		if(Divx.m_chapterNum > 1)
     		{
     			hideController(BUTTON_LIST);
    			hide_lawrate_metadata_info();
    			hide_picture_size_ui();
    			
     			ButtonNum = ButtonNum + "3";
     			execChapterJumpTimer();
     		}
     	}
     	return true;
     	case KeyEvent.KEYCODE_4:
     	{
     		if(Divx.m_chapterNum > 1)
     		{
     			hideController(BUTTON_LIST);
    			hide_lawrate_metadata_info();
    			hide_picture_size_ui();
    			
     			ButtonNum = ButtonNum + "4";
     			execChapterJumpTimer();
     		}
     	}	
     	return true;
     	case KeyEvent.KEYCODE_5:
     	{
     		if(Divx.m_chapterNum > 1)
     		{
     			hideController(BUTTON_LIST);
    			hide_lawrate_metadata_info();
    			hide_picture_size_ui();
    			
     			ButtonNum = ButtonNum + "5";
     			execChapterJumpTimer();
     		}
     	}
     	return true;
     	case KeyEvent.KEYCODE_6:
     	{
     		if(Divx.m_chapterNum > 1)
     		{
     			hideController(BUTTON_LIST);
    			hide_lawrate_metadata_info();
    			hide_picture_size_ui();
    			
     			ButtonNum = ButtonNum + "6";
     			execChapterJumpTimer();
     		}
     	}
     	return true;
     	case KeyEvent.KEYCODE_7:
     	{
     		if(Divx.m_chapterNum > 1)
     		{
     			hideController(BUTTON_LIST);
    			hide_lawrate_metadata_info();
    			hide_picture_size_ui();
    			
     			ButtonNum = ButtonNum + "7";
     			execChapterJumpTimer();
     		}
     	}
     	return true;
     	case KeyEvent.KEYCODE_8:
     	{
     		if(Divx.m_chapterNum > 1)
     		{
     			hideController(BUTTON_LIST);
    			hide_lawrate_metadata_info();
    			hide_picture_size_ui();
    			
     			ButtonNum = ButtonNum + "8";
     			execChapterJumpTimer();
     		}
     	}
     	return true;
     	case KeyEvent.KEYCODE_9:
     	{
     		if(Divx.m_chapterNum > 1)
     		{
     			hideController(BUTTON_LIST);
    			hide_lawrate_metadata_info();
    			hide_picture_size_ui();
    			
     			ButtonNum = ButtonNum + "9";
     			execChapterJumpTimer();
     		}	
     	}
     	return true;
     	case KeyEvent.KEYCODE_DPAD_CENTER:
     	{
     		if(Divx.m_chapterNum > 1 && ButtonNum != "")
     		{
     			if(task_info_show != null)
     			{
					task_info_show.cancel();
					task_info_show = null;
     			}
     			execChapterJump(KeyEvent.KEYCODE_DPAD_CENTER);
     		}
     	}
     	return true;
     	case 228: //for L4300 KeyEvent.KEYCODE_HOLD:
     	case 256: //VIDEO KEY  in realtek RCU
     	case KeyEvent.KEYCODE_R: //rewind
     	{
     		if(metadata != null && !metadata.getBoolean(Metadata.SEEK_BACKWARD_AVAILABLE))
     		{
     			toast.makeText(getApplicationContext(), 
     					this.getResources().getString(R.string.msg_seek_rewind_forbidden),
            		    Toast.LENGTH_SHORT).show();
     		}else
     		{
     			if(!mPlayer.isPlaying())
         			mPlayer.start();
     			
     			if(task_hide_controler != null)
        		{
        			task_hide_controler.cancel();
        			task_hide_controler = null;
        		}
     			
         		showCurrentInfo();
         		animateShowBanner();
         		rewind();
     		}
     	}
     	return true;
     	case 229: //for L4300 KeyEvent.KEYCODE_ZOOM
     	case 255: //AUDIO KEY in realteck RCU
     	case KeyEvent.KEYCODE_F: //forward
     	{
     		if(metadata != null && !metadata.getBoolean(Metadata.SEEK_FORWARD_AVAILABLE))
     		{
     			toast.makeText(getApplicationContext(), 
     					this.getResources().getString(R.string.msg_seek_forward_forbidden),
            		    Toast.LENGTH_SHORT).show();
     		}else
     		{
     			if(!mPlayer.isPlaying())
         			mPlayer.start();
     			
     			if(task_hide_controler != null)
        		{
        			task_hide_controler.cancel();
        			task_hide_controler = null;
        		}

         		showCurrentInfo();
         		animateShowBanner();         		
         		forward();
     		}
     	}
     	return true;
     	case 235: //for L4300 KeyEvent.KEYCODE_NEXT skip+
     	case KeyEvent.KEYCODE_X:
     	{
     		hideController(BUTTON_LIST);
			hide_lawrate_metadata_info();
			hide_picture_size_ui();
			
     		if(!isforward && !isrewind) //can not switch  when is rewinding or forwarding.
     		{
     			if(Divx.m_chapterNum > 1)
     				switch_chapter(KeyEvent.KEYCODE_DPAD_UP);
     			else
     			{
     				isSwitchToNextFile = true;
     				switch_file_by_skip_key(true);
     			}
     		}
     		else
     		{
     			/*toast.makeText(getApplicationContext(), 
     					this.getResources().getString(R.string.msg_donot_switch_chapter),
            		    Toast.LENGTH_SHORT).show();*/
     			currIndex++;
     			media_play_init(filePathArray.get(currIndex), fileTitleArray.get(currIndex));
     		}
     	}
     	return true;
     	case 234: //for L4300 KeyEvent.KEYCODE_PREVIOUS skip-
     	case KeyEvent.KEYCODE_Z:
     	{
     		hideController(BUTTON_LIST);
			hide_lawrate_metadata_info();
			hide_picture_size_ui();
    		
     		if(!isforward && !isrewind) //can not switch chapter when is rewinding or forwarding.
     		{
     			if(Divx.m_chapterNum > 1)
     				switch_chapter(KeyEvent.KEYCODE_DPAD_DOWN);
     			else
     			{
     				isSwitchToNextFile = false;
     				switch_file_by_skip_key(false);
     			}
     		}
     		else
     		{
     			/*toast.makeText(getApplicationContext(), 
						this.getResources().getString(R.string.msg_donot_switch_chapter),
            		    Toast.LENGTH_SHORT).show();*/
     			media_play_init(filePathArray.get(currIndex), fileTitleArray.get(currIndex));
     		}
     	}
     	return true;
     	case KeyEvent.KEYCODE_DPAD_DOWN:
     	case KeyEvent.KEYCODE_U: //switch to next file
     	{
     		hide_all();
     		//bannerShowTimeOutControl(MOVIE_BANNER, TimerDelay.Movie_Banner_Show_4S_Time);
     		switch_file(KeyEvent.KEYCODE_CHANNEL_UP);
     	}
     	return true;
     	
     	case KeyEvent.KEYCODE_DPAD_UP:
     	case KeyEvent.KEYCODE_D: //switch to previous file
     	{
     		hide_all();
     		//bannerShowTimeOutControl(MOVIE_BANNER, TimerDelay.Movie_Banner_Show_4S_Time);
     		switch_file(KeyEvent.KEYCODE_CHANNEL_DOWN);
     	}
     	return true;
     	case 230:
     	case 254:	// KeyEvent.KEYCODE_EPG:
     	case KeyEvent.KEYCODE_S:
     	{
     		hideController(BUTTON_LIST);
    		hide_lawrate_metadata_info();
    		hide_picture_size_ui();
    		
    		if(!isMediaplayIdle)
    			setSubtitle();
     	}
     	return true;
     	case 267: //for rtk_generic  realtek RCU CARD key
     	case KeyEvent.KEYCODE_A:
     	{
     		hideController(BUTTON_LIST);
    		hide_lawrate_metadata_info();
    		hide_picture_size_ui();
    		
    		if(!isMediaplayIdle)
    			setAudioTrack();
     	}
     	return true;
     	case KeyEvent.KEYCODE_PROG_BLUE:
     	{
     		if(MediaBrowserConfig.IS_DEMO_BOARD)
     		{
     			hideController(BUTTON_LIST);
        		hide_lawrate_metadata_info();
        		hide_picture_size_ui();
        		
        		if(!isMediaplayIdle)
        			setAudioTrack();
         		return true;
     		}
     	}
     	break;
     	case KeyEvent.KEYCODE_PROG_GREEN:
     	{
     		if(MediaBrowserConfig.IS_DEMO_BOARD)
     		{
     			if(isforward)
    			{
    				mPlayer.fastforward(0);
    				play.setImageResource(R.drawable.au_play);
    				if(bt_play.isFocused())
    					bt_play.setImageResource(R.drawable.player_common_pause_focus_button_list);
    				else
    					bt_play.setImageResource(R.drawable.player_common_pause_button_list);
    				isforward = false;
    				forward_press = 1;
    			}else if(isrewind)
    			{
    				mPlayer.fastrewind(0);
    				play.setImageResource(R.drawable.au_play);
    				if(bt_play.isFocused())
    					bt_play.setImageResource(R.drawable.player_common_pause_focus_button_list);
    				else
    					bt_play.setImageResource(R.drawable.player_common_pause_button_list);
    				isrewind = false;
    				rewind_press = 1;
    			}
    			else
    			{
    				if(!mPlayer.isPlaying())
    				{
    					mPlayer.start();
    					play.setImageResource(R.drawable.au_play);
    					if(bt_play.isFocused())
    						bt_play.setImageResource(R.drawable.player_common_pause_focus_button_list);
    					else
    						bt_play.setImageResource(R.drawable.player_common_pause_button_list);
    				}
    			}
    			
    			if(common_ui.isShown() || metaData_name.isShown() 
    			|| metaData_language_etc.isShown() || lawRateInfo.isShown())
    			{
    				hide_meta_data_delay(TimerDelay.delay_4s);		
    			}
    			statusBannerShowControl(TimerDelay.delay_6s);
    			showCurrentInfo();
    			return true;
     		}
     	}
     	break;
     	case 232: //for L4300 KeyEvent.KEYCODE_PLAY:
     	case KeyEvent.KEYCODE_P:
     	{	
			if(isforward)
			{
				mPlayer.fastforward(0);
				play.setImageResource(R.drawable.au_play);
				if(bt_play.isFocused())
					bt_play.setImageResource(R.drawable.player_common_pause_focus_button_list);
				else
					bt_play.setImageResource(R.drawable.player_common_pause_button_list);
				isforward = false;
				forward_press = 1;
			}else if(isrewind)
			{
				mPlayer.fastrewind(0);
				play.setImageResource(R.drawable.au_play);
				if(bt_play.isFocused())
					bt_play.setImageResource(R.drawable.player_common_pause_focus_button_list);
				else
					bt_play.setImageResource(R.drawable.player_common_pause_button_list);
				isrewind = false;
				rewind_press = 1;
			}
			else
			{
				if(!mPlayer.isPlaying())
				{
					mPlayer.start();
					play.setImageResource(R.drawable.au_play);
					if(bt_play.isFocused())
						bt_play.setImageResource(R.drawable.player_common_pause_focus_button_list);
					else
						bt_play.setImageResource(R.drawable.player_common_pause_button_list);
				}
			}
			
			if(common_ui.isShown() || metaData_name.isShown() 
			|| metaData_language_etc.isShown() || lawRateInfo.isShown())
			{
				hide_meta_data_delay(TimerDelay.delay_4s);		
			}
			
			statusBannerShowControl(TimerDelay.delay_6s);
			showCurrentInfo();
     	}
     	return true;
     	case KeyEvent.KEYCODE_C:
     	case 233: // for L4300 KeyEvent.KEYCODE_PAUSE:
     	{
     		if(metadata != null && !metadata.getBoolean(Metadata.PAUSE_AVAILABLE))
     		{
     			toast.makeText(getApplicationContext(), 
     					this.getResources().getString(R.string.msg_seek_pause_forbidden),
            		    Toast.LENGTH_SHORT).show();
     		}
     		else
     		{
     			if(isforward)
    			{
    				mPlayer.fastforward(0);
    				mPlayer.pause();
    				play.setImageResource(R.drawable.au_pause);
    				if(bt_play.isFocused())
    					bt_play.setImageResource(R.drawable.player_common_play_focus_button_list);
    				else
    					bt_play.setImageResource(R.drawable.player_common_play_button_list);
    				handler.removeMessages(HandlerControlerVariable.PROGRESS_CHANGED);
    				isforward = false;
    				forward_press = 1;
    				
    				if(common_ui.isShown() || metaData_name.isShown() 
    				|| metaData_language_etc.isShown() || lawRateInfo.isShown())
    				{
    					if(task_meta_data_show != null)
        				{
        					task_meta_data_show.cancel();
        					task_meta_data_show = null;
        				}
    				}
    			}else if(isrewind)
    			{
    				mPlayer.fastrewind(0);
    				mPlayer.pause();
    				play.setImageResource(R.drawable.au_pause);
    				if(bt_play.isFocused())
    					bt_play.setImageResource(R.drawable.player_common_play_focus_button_list);
    				else
    					bt_play.setImageResource(R.drawable.player_common_play_button_list);
    				handler.removeMessages(HandlerControlerVariable.PROGRESS_CHANGED);
    				isrewind = false;
    				rewind_press = 1;
    				
    				if(common_ui.isShown() || metaData_name.isShown() 
    	    		|| metaData_language_etc.isShown() || lawRateInfo.isShown())
    	    		{
    	    			if(task_meta_data_show != null)
    	        		{
    	    				task_meta_data_show.cancel();
    	        			task_meta_data_show = null;
    	        		}
    	    		}
    			}else
    			{
    				if(mPlayer.isPlaying())
    				{
    					mPlayer.pause();
    					play.setImageResource(R.drawable.au_pause);
        				if(bt_play.isFocused())
        					bt_play.setImageResource(R.drawable.player_common_play_focus_button_list);
        				else
        					bt_play.setImageResource(R.drawable.player_common_play_button_list);
    					handler.removeMessages(HandlerControlerVariable.PROGRESS_CHANGED);
    					
    					if(common_ui.isShown() || metaData_name.isShown() 
    		    	    || metaData_language_etc.isShown() || lawRateInfo.isShown())
    					{
    						if(task_meta_data_show != null)
    						{
            					task_meta_data_show.cancel();
            					task_meta_data_show = null;
            				}
    					}
    				}else
    				{
    					mPlayer.start();
    					play.setImageResource(R.drawable.au_play);
    					if(bt_play.isFocused())
    						bt_play.setImageResource(R.drawable.player_common_pause_focus_button_list);
    					else
    						bt_play.setImageResource(R.drawable.player_common_pause_button_list);
    					showCurrentInfo();
    					
    					if(common_ui.isShown() || metaData_name.isShown() 
    					|| metaData_language_etc.isShown() || lawRateInfo.isShown())
    					{
    						hide_meta_data_delay(TimerDelay.delay_4s);
    					}
    					
    				}
    			}
     			statusBannerShowControl(TimerDelay.delay_6s);
         		showCurrentInfo();
     		}
     	}
     	return true;
        case KeyEvent.KEYCODE_PROG_RED:
     	case KeyEvent.KEYCODE_T:
     	{
     		switch_title_by_Redkey(true);
     	}
     	return true;
     	case KeyEvent.KEYCODE_PROG_YELLOW:
     	case KeyEvent.KEYCODE_E:
     	{
     		switch_title_by_Redkey(false);
     	}
     	return true;
     	case KeyEvent.KEYCODE_O:
     	{
     		if(AUDIO_DIGITAL_OUTPUT_MODE == AUDIO_DIGITAL_RAW)
     		{
     			setAudioSpdifOutput(AUDIO_DIGITAL_LPCM_DUAL_CH);
     			AUDIO_DIGITAL_OUTPUT_MODE = AUDIO_DIGITAL_LPCM_DUAL_CH;
     		}else
     		{
     			setAudioSpdifOutput(AUDIO_DIGITAL_RAW);
     			AUDIO_DIGITAL_OUTPUT_MODE = AUDIO_DIGITAL_RAW;
     		}
     	}
     	return true;
     	case 221: //for L4300 KeyEvent.KEYCODE_DISPLAY_MODE
     	{	
     		adjustPicSizePosition();
     		hideController(BUTTON_LIST);
 			hide_lawrate_metadata_info();
     		PictureSize.setVisibility(View.VISIBLE);
     		
 			setPicSize();
     		
     		if(task_picture_size_show != null)
     		{
     			task_picture_size_show.cancel();
     			task_picture_size_show = null;
     		}
     		
     		task_picture_size_show = new TimerTask(){

				@Override
				public void run() {
					handler.sendEmptyMessage(HandlerControlerVariable.HIDE_PICTURE_SIZE_UI);
				}
     			
     		};
     		
     		if(timer != null)
     			timer.schedule(task_picture_size_show, TimerDelay.delay_5s);
     	}
     	return true;
     	default:
     		break;
		}
		
		return super.onKeyDown(keyCode, event);
	}
	 
	private void init()
	{
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
        sView = (SurfaceView)findViewById(R.id.surfaceView);
        
        map = (MediaApplication)getApplication();
        mPlayer = map.getMediaPlayer();
        
        MovieBannerView = (View)findViewById(R.id.movie_banner);
        MovieBannerView.setBackgroundColor(Color.TRANSPARENT);
        MovieBannerView.setVisibility(View.INVISIBLE);
        
        play = (ImageView)MovieBannerView.findViewById(R.id.play);
        play_mode = (ImageView)MovieBannerView.findViewById(R.id.play_mode);
        filename = (TextView)MovieBannerView.findViewById(R.id.filename);
        time_bar = (ProgressBar)MovieBannerView.findViewById(R.id.time_bar);
        duration = (TextView)MovieBannerView.findViewById(R.id.time);
        chapter_info = (TextView)MovieBannerView.findViewById(R.id.chapter);
        tpt = (ImageView)MovieBannerView.findViewById(R.id.TPT);
    	file_number = (TextView)MovieBannerView.findViewById(R.id.file_number);
    	guide_bar = (View)MovieBannerView.findViewById(R.id.vp_guide_bar);
    	
    	guide_bar.setVisibility(View.INVISIBLE);
    	
    	guide_red = (TextView)guide_bar.findViewById(R.id.vp_guide_red);
    	guide_title = (TextView)guide_bar.findViewById(R.id.vp_guide_title);
    	
    	ButtonListView = (View)findViewById(R.id.button_list);
    	ButtonListView.setBackgroundColor(Color.TRANSPARENT);
    	ButtonListView.setVisibility(View.INVISIBLE);
    	
    	help_bar = (View)findViewById(R.id.help_bar);
    	
    	bt_skip_backward = (ImageView)ButtonListView.findViewById(R.id.bt_skip_backward);
        bt_rew = (ImageView)ButtonListView.findViewById(R.id.bt_rew);
        bt_play = (ImageView)ButtonListView.findViewById(R.id.bt_play);
        bt_ff = (ImageView)ButtonListView.findViewById(R.id.bt_ff);
        bt_skip_forward = (ImageView)ButtonListView.findViewById(R.id.bt_skip_forward);
        bt_repeat = (ImageView)ButtonListView.findViewById(R.id.bt_repeat);
        
        PictureSize = (View)findViewById(R.id.set_picture_size);
        spinner_bar_value = (TextView)PictureSize.findViewById(R.id.SPINNER_BAR_VALUE);
        PictureSize.setVisibility(View.INVISIBLE);
        
        setOnFocusChangeListener(focus);
        setOnClickListener(click);
    	
    	metaData_name = (TextView)findViewById(R.id.metaData_name);
    	metaData_language_etc = (TextView)findViewById(R.id.metaData_language_etc);
    	lawRateInfo = (TextView)findViewById(R.id.lawRateInfo);
    	
    	dolby = (ImageView)findViewById(R.id.dolby);
    	
        common_ui = (TextView)findViewById(R.id.subtitle_audio_track);
        common_ui.setVisibility(View.INVISIBLE);
        
        chapter_info.setVisibility(View.INVISIBLE);
        tpt.setVisibility(View.INVISIBLE);
        dolby.setVisibility(View.INVISIBLE);
        
        metaData_name.setVisibility(View.INVISIBLE);	
        metaData_language_etc.setVisibility(View.INVISIBLE);
        lawRateInfo.setVisibility(View.INVISIBLE);
        
        play.setOnClickListener(click);
        
        mTVService = (TvManager)getSystemService("tv");
        
        toast = new Toast(this);
        
        mPerferences = PreferenceManager
				.getDefaultSharedPreferences(this);
        new Thread(new Runnable() {
			@Override
			public void run() {
				int tmp_index = mPerferences.getInt("repeatIndex", -1);
				if(tmp_index == -1)
				{
					Editor editor = mPerferences.edit();
					editor.putInt("repeatIndex", repeatIndex);
					editor.commit();
				}else{
					repeatIndex = tmp_index;
					setRepeatIcon(repeatIndex);
				}
			}
		}).start();
        
        handler = new Handler() {
        	
        	@Override
    		public void handleMessage(Message msg) {
        		switch(msg.what){
        		case HandlerControlerVariable.PROGRESS_CHANGED:
        			set_progress_change();
        		break;
        		case HandlerControlerVariable.GET_DURATION:
        			get_duration();
        		break;
        		case HandlerControlerVariable.SET_DIVX_METADATA_LAWRATE_INFO:
        			set_Title_LawRating_MetaData_Info();
        		break;
        		case HandlerControlerVariable.SET_DIVX_METADATA_SUBTITLE_INFO:
        			set_Subtitle_MetaData_Info();
        		break;
        		case HandlerControlerVariable.SET_DIVX_METADATA_AUDIO_INFO:
        			set_Audio_MetaData_Info();
            	break;
        		case HandlerControlerVariable.SET_DIVX_METADATA_CHAPTER_INFO:
        			 set_Chapter_MetaData_Info();
        		break;
        		case HandlerControlerVariable.HIDE_MOVIE_BANNER:
        			//hideController(MOVIE_BANNER);
        			animateHideBanner();
        		break;
        		case HandlerControlerVariable.HIDE_LAWRATE_METADATA:
					hide_lawrate_metadata_info();
				break;
        		case HandlerControlerVariable.HIDE_METADATA_NAME:
	        		metaData_name.setVisibility(View.INVISIBLE);
				break;
        		case HandlerControlerVariable.HIDE_LEFT_CORN_INFO:
        			common_ui.setVisibility(View.INVISIBLE);
        		break;
        		case HandlerControlerVariable.DELAY_SHOW_BANNER:
        			if(!isMediaplayIdle)
					{	
						/*Divx.m_divx_metadata_type = DivxParser.DIVX_METADATA_TITLE;
						Divx.execSetGetNavProperty(DivxParser.NAVPROP_DIVX_METADATA_QUERY);	
						if(Divx.metaData != null && Divx.ArrayElementNonzeroJudge(Divx.metaData))
						{
							filename.setText("Title: " + Divx.metaData);
						}else
						{
							filename.setText(fileTitleArray.get(currIndex));
						}*/
						
        				statusBannerShowControl(TimerDelay.delay_4s);
						showCurrentInfo();
					}
        		break;
        		case HandlerControlerVariable.MEDIAPLAY_INIT:
        			media_play_init(filePathArray.get(currIndex), fileTitleArray.get(currIndex));
        		break;
        		case HandlerControlerVariable.SET_CHAPTER_NUM:
        			common_ui.setText(" "+"Chapter"+Divx.m_current_chapter_num+" ");
        		break;
        		case HandlerControlerVariable.HIDE_BUTTON_LIST:
        			hideController(BUTTON_LIST);
        		break;
        		case HandlerControlerVariable.SWITCH_TITLE_QUERY:
        			switch_title_query(Divx.m_current_title_num);
        		break;
        		case HandlerControlerVariable.SWITCH_FILE_QUERY:
        			if(isSwitchToNextFile)
        				switch_file_query(KeyEvent.KEYCODE_CHANNEL_UP);
        			else
        				switch_file_query(KeyEvent.KEYCODE_CHANNEL_DOWN);
        		break;
        		case HandlerControlerVariable.SET_DIVX_CHAPTER_NUMBER:
        			if(Divx.m_chapterNum > 1)
					{
        				guide_bar.setVisibility(View.VISIBLE);
        				if(Divx.m_titleNum <= 1)
        				{
        					guide_red.setVisibility(View.INVISIBLE);
        					guide_title.setVisibility(View.INVISIBLE);
        				}
						chapter_info.setVisibility(View.VISIBLE);
						chapter_info.setText("Chapter:"+Divx.m_current_chapter_num+"/"+Divx.m_chapterNum);
					}else
					{
						chapter_info.setVisibility(View.INVISIBLE);
						guide_bar.setVisibility(View.INVISIBLE);
					}
        		break;
        		case HandlerControlerVariable.HIDE_PICTURE_SIZE_UI:
        			PictureSize.setVisibility(View.INVISIBLE);
        		break;
        		case HandlerControlerVariable.MEDIA_PLAYER_START:
        			if(!mPlayer.isPlaying())
        				mPlayer.start();
					play.setImageResource(R.drawable.au_play);
					if(bt_play.isFocused())
						bt_play.setImageResource(R.drawable.player_common_pause_focus_button_list);
					else
						bt_play.setImageResource(R.drawable.player_common_pause_button_list);
        		break;
        		case HandlerControlerVariable.MESSAGE_DISMISS:
        			if(short_msg != null)
        				short_msg.dismiss();
        			if(long_msg != null)
        				long_msg.dismiss();
        		break;
        		case HandlerControlerVariable.HIDE_QUICK_MENU:
        			quickMenu.dismiss();
        		break;
        		case HandlerControlerVariable.HIDE_DOLBY:
        			dolby.setVisibility(View.INVISIBLE);
        		break;
        		case HandlerControlerVariable.HIDE_POPUP_MESSAGE:
        			msg_hint.dismiss();
        		break;
        		case HandlerControlerVariable.MSG_REFRESH_TIMER:
        			quickMenuAdapter.notifyDataSetChanged();
        			
        		break;
        		default:
        		break;
        		}
        		super.handleMessage(msg);
        	}
        };
        
        Intent intent= getIntent();
        isAnywhere = intent.getBooleanExtra("isanywhere", false);
        if (!isAnywhere)
        	captureIntent(intent);
        else if (isAnywhere)
        {
        	captureIntent_TvAnywhere(intent);
        }
        
        sView.getHolder().setKeepScreenOn(true);
        if (currIndex >= 0)
        	sView.getHolder().addCallback(new SurfaceListener());
        
        String path = getFilesDir().getPath();
        String fileName = path.concat("/VideoBookMark.bin");
        mVideoBookMark = map.getBookMark(fileName);
        
        mTitleBookMark = new TitleBookMark();
        
        Divx = new DivxParser(VideoPlayerActivity.this);
        
        long_msg = new ConfirmMessage(VideoPlayerActivity.this);
        int val1, val2;
        val1 = (int) (678.0 / 1920.0 * map.getScreenWidth());
        val2 = (int) (226.0 / 1080.0 * map.getScreenHeight());
        
        short_msg = new ConfirmMessage(VideoPlayerActivity.this,val1,val2);
        
        msg_hint = new PopupMessage(VideoPlayerActivity.this);
        /*bt = new control_button_list(VideoPlayerActivity.this);
        bt.setOnClickListener(click);
        bt.setOnFocusChangeListener(bt.focus);*/
        
        initUsbCtl();
        
        
        View v = (View)findViewById(R.id.main);
        v.setOnHoverListener(new OnHoverListener(){
        	@Override
            public boolean onHover(View v,MotionEvent event){
            	switch(event.getAction()){
            	case MotionEvent.ACTION_HOVER_ENTER:
            	{
            		MouseX = event.getRawX();
       			 	MouseY = event.getRawY();
            	}
            	break;
            	case MotionEvent.ACTION_HOVER_MOVE:
            		 if(event.getRawX() != MouseX || event.getRawY() != MouseY)
            		 {
            			 if(!ButtonListView.isShown())
                 		{
                 			//animateShowBanner();
            				 buttonBannerShowControl(TimerDelay.delay_6s);
             				//showCurrentInfo();
             				bt_play.requestFocus();
             				if(isStartPlay)
             				{
             					bt_play.setImageResource(R.drawable.player_common_pause_focus_button_list);
             					isStartPlay = false;	
             				}else
             				{
             					if(!isMediaplayIdle)
            					{
            						if(mPlayer != null && mPlayer.isPlaying())
            						{
            							bt_play.setImageResource(R.drawable.player_common_pause_focus_button_list);
            						}else
            						{
            							bt_play.setImageResource(R.drawable.player_common_play_focus_button_list);
            						}
            					}
             				}
                 		}
            		 }
            		break;
            	case MotionEvent.ACTION_HOVER_EXIT:
            	{
            		MouseX = event.getRawX();
       			 	MouseY = event.getRawY();
            	}
            	break;
            	}
            	return false;
            }
        });
        
        /*time2 = System.currentTimeMillis();
        Log.e(TAG, "Step1: time = " + (time2 - time1));
        time1 = time2;*/
	}
	
	private void get_duration()
	{
		if(!isMediaplayIdle)
		{
			Log.e(TAG, "get_duration");
			int max = mPlayer.getDuration();
			time_bar.setMax(max);
			max /= 1000;	
			Minute = max / 60;
			Hour = Minute / 60;
			Second = max % 60;
			Minute %= 60;
			
			duration.setText("00:00:00" + String.format("/%02d:%02d:%02d", Hour, Minute, Second));
			time_bar.setProgress(0);
		}
	}
	
	
	private void setRepeatIcon(int repeat)
	{
		switch(repeat)
		{
		case 0:
			repeat_mode = REPEAT_OFF;
			if(play_mode != null)
				play_mode.setImageDrawable(
					this.getResources().getDrawable(R.drawable.photo_repeat_all_off));
			break;
		case 1:
			repeat_mode = REPEAT_ALL;
			if(play_mode != null)
				play_mode.setImageDrawable(
					this.getResources().getDrawable(R.drawable.photo_repeat_all_on));
			break;
		case 2:
			repeat_mode = REPEAT_SINGLE;
			if(play_mode != null)
				play_mode.setImageDrawable(
					this.getResources().getDrawable(R.drawable.photo_player_repeat1_on));
			break;
		default:
			break;
		}
	}
	
	private void hide_lawrate_metadata_info()
	{
		common_ui.setVisibility(View.INVISIBLE);
		metaData_name.setVisibility(View.INVISIBLE);
		metaData_language_etc.setVisibility(View.INVISIBLE);
		lawRateInfo.setVisibility(View.INVISIBLE);
		isShowChapterMetaData = false;
	}
	
	private void hide_picture_size_ui()
	{
		PictureSize.setVisibility(View.INVISIBLE);
	}
	
	private void hide_all()
	{
		hideController(MOVIE_BANNER);
		hideController(BUTTON_LIST);
		hide_lawrate_metadata_info();
		hide_picture_size_ui();
		
		if(task_hide_button != null)
		{
			task_hide_button.cancel();
			task_hide_button = null;
		}
		
		if(task_hide_controler != null)
		{
			task_hide_controler.cancel();
			task_hide_controler = null;
		}
		
		if(task_getCurrentPositon != null)
		{
			task_getCurrentPositon.cancel();
			task_getCurrentPositon = null;
		}
	}
	
	private void set_progress_change()
	{
		if(!isMediaplayIdle)
		{
			int i = 0;
			boolean execError = false;
			try
			{
				i = mPlayer.getCurrentPosition();
				Log.e(TAG, "getCurrentPosition = "+ i);
			} catch (IllegalArgumentException e) {
    			e.printStackTrace();
    			execError = true;
    		} catch (SecurityException e) {
    			e.printStackTrace();
    			execError = true;
    		} catch (IllegalStateException e) {
    			e.printStackTrace();
    			execError = true;
    		}
			if(!execError)
			{
				time_bar.setProgress(i);
				
				i /= 1000;
				int minute = i / 60;
				int hour = minute / 60;
				int second = i % 60;
				minute %= 60;
				
				duration.setText(String.format("%02d:%02d:%02d", hour, minute, second) +
				String.format("/%02d:%02d:%02d", Hour, Minute, Second));
			}
		}
	}
	
	private void adjustMetaNamePosition()
	{
		common_ui.setVisibility(View.INVISIBLE);
		int val;
		if (MovieBannerView.getVisibility() == View.INVISIBLE)
		{
			val = (int) (1005.0 / 1080.0 * map.getScreenHeight());
			metaData_name.setY(val);
		}
		else
		{
			val = (int) (780.0 / 1080.0 * map.getScreenHeight());
			metaData_name.setY(val);
		}
	}
	
	private void adjustMetaInfoPosition()
	{
		int val1, val2;
		int val3;
		if (lawRateInfo.getVisibility() == View.VISIBLE)
		{
			if (dolby.getVisibility() == View.INVISIBLE) {
				val1 = (int) (9.0 / 1080.0 * map.getScreenHeight());
				lawRateInfo.setY(val1);
			
			}else {
				val2 = (int) (99.0 / 1920.0 * map.getScreenWidth());
				lawRateInfo.setY(val2);
			}
		}
		
		if (MovieBannerView.getVisibility() == View.INVISIBLE)
		{
			val3 = (int) (1005.0 / 1080.0 * map.getScreenHeight());
			common_ui.setY(val3);
			val3 = (int) (930.0 / 1080.0 * map.getScreenHeight());
			metaData_name.setY(val3);
			val3 = (int) (1005.0 / 1080.0 * map.getScreenHeight());
			metaData_language_etc.setY(val3);
		}
		else
		{
			val3 = (int) (780.0 / 1080.0 * map.getScreenHeight());
			common_ui.setY(val3);
			val3 = (int) (705.0 / 1080.0 * map.getScreenHeight());
			metaData_name.setY(val3);
			val3 = (int) (780.0 / 1080.0 * map.getScreenHeight());
			metaData_language_etc.setY(val3);
		}
	}
	
	private void adjustPicSizePosition()
	{
		int val3;
		if (MovieBannerView.getVisibility() == View.INVISIBLE)
		{
			val3 = (int) (982.0 / 1080.0 * map.getScreenHeight());
			PictureSize.setY(val3);
		}else
		{
			val3 = (int) (780.0 / 1080.0 * map.getScreenHeight());
			PictureSize.setY(val3);
		}
	}
	
	private void set_Title_LawRating_MetaData_Info()
	{
		if(!isMediaplayIdle)
		{
			try {
				Divx.execSetGetNavProperty(DivxParser.NAVPROP_INPUT_GET_PLAYBACK_STATUS);
			} catch (IllegalStateException e) {
				e.printStackTrace();
				return;
			}catch (Exception e) {
				e.printStackTrace();
				return;
			}
			
			if(Divx.metaData != null && Divx.ArrayElementNonzeroJudge(Divx.metaData))
			{
				String metaData = "";
				
				metaData = new String(Divx.metaData, Charset.forName("UTF-8"));
				
				metaData_name.setVisibility(View.VISIBLE);
				metaData_name.setText(" "+metaData+" ");
			}else
			{
				metaData_name.setVisibility(View.INVISIBLE);
			}
			
			common_ui.setVisibility(View.VISIBLE);
			common_ui.setText(" "+"Title"+"    "+"Title"+Divx.m_current_title_num+" ");
			
			if(Divx.rating != null && Divx.ArrayElementNonzeroJudge(Divx.rating))
			{
				String metaData = "";
				metaData =  new String(Divx.rating);
				lawRateInfo.setVisibility(View.VISIBLE);
				lawRateInfo.setText(" "+metaData+" ");
			}else
			{
				lawRateInfo.setVisibility(View.INVISIBLE);
			}
			
			adjustMetaInfoPosition();
			
			hide_meta_data_delay(TimerDelay.delay_4s);
		}
	}

	private void set_Subtitle_MetaData_Info()
	{
		Log.v(TAG, "set_Subtitle_MetaData_Info start !");
		if(!isMediaplayIdle)
		{
			int[] currSubtitleInfo = mPlayer.getSubtitleInfo(curr_subtitle_stream_num);
			
			if(SPU_ENABLE == 1)
			{
				if(Divx.metaData != null && Divx.ArrayElementNonzeroJudge(Divx.metaData))
				{
					String metaData = "";
					metaData = new String(Divx.metaData);
					metaData_name.setVisibility(View.VISIBLE);
					metaData_name.setText(" "+metaData+" ");
				}else
				{
					metaData_name.setVisibility(View.INVISIBLE);
				}
				
				common_ui.setVisibility(View.VISIBLE);
				common_ui.setText(" "+"Subtitle"+curr_subtitle_stream_num+" ");
				
				String mlanguage = Utility.SI_LANG_TO_ISO639(currSubtitleInfo[3]);
				metaData_language_etc.setVisibility(View.VISIBLE);
				if(mlanguage != null)
				{
					metaData_language_etc.setText(" "+mlanguage+" ");
				}else
				{
					metaData_language_etc.setText("Undefined");
				}
			}else if (SPU_ENABLE == 0)
			{
				metaData_language_etc.setVisibility(View.INVISIBLE);
				metaData_name.setVisibility(View.INVISIBLE);
				common_ui.setVisibility(View.VISIBLE);
				common_ui.setText(" "+"Subtitle off"+" ");
			}
			
			adjustMetaInfoPosition();
			
			hide_meta_data_delay(TimerDelay.delay_4s);
		}
	}
	
	private void set_Audio_MetaData_Info()
	{
		if(!isMediaplayIdle)
		{
			common_ui.setText(" "+"Audio Tracks: " + curr_audio_stream_num+" ");
			common_ui.setVisibility(View.VISIBLE);
			
			int[] currAudioInfo = mPlayer.getAudioTrackInfo(curr_audio_stream_num);
			
			String mLanguage = Utility.SI_LANG_TO_ISO639(currAudioInfo[7]);
			if(mLanguage == null)
				mLanguage = "Undefined";
			
			String mChannel = null;
			if(currAudioInfo[4] > 0)
				mChannel = currAudioInfo[4] + " Channels";
			else
				mChannel = "Stereo";
			
			String mAudioType = Utility.AUDIO_TYPE_TABLE(currAudioInfo[3]);
			if(mAudioType == null)
				mAudioType = "Unknown";
			
			metaData_language_etc.setVisibility(View.VISIBLE);
			metaData_language_etc.setText(" "+mLanguage + ", " + mAudioType + ", " +mChannel+" ");
			
			if(Divx.metaData != null && Divx.ArrayElementNonzeroJudge(Divx.metaData))
			{
				String metaData = "";
				metaData = new String(Divx.metaData);
				metaData_name.setVisibility(View.VISIBLE);
				metaData_name.setText(" "+metaData+" ");
			}else
			{
				String metaData = "";
				metaData_name.setText(metaData);
				metaData_name.setVisibility(View.INVISIBLE);
			}
			
			hide_meta_data_delay(TimerDelay.delay_4s);
		}
	}
	
	private void set_Chapter_MetaData_Info()
	{
		if(!isMediaplayIdle)
		{
			if(Divx.metaData != null && Divx.ArrayElementNonzeroJudge(Divx.metaData))
			{
				Log.e(TAG, "Divx.metaData != null");
				String metaData = "";
				metaData = new String(Divx.metaData);
				Log.e(TAG, "----metaData is"+metaData);
				metaData_name.setVisibility(View.VISIBLE);
				metaData_name.setText(" "+metaData+" ");
				isShowChapterMetaData = true;
				adjustMetaNamePosition();
			}else
			{
				metaData_name.setVisibility(View.INVISIBLE);
				common_ui.setVisibility(View.VISIBLE);
				if(chapter_jump_num >= 1 && chapter_jump_num <= Divx.m_chapterNum)
					common_ui.setText(" "+"Chapter"+chapter_jump_num+" ");
				else if(chapter_jump_num > Divx.m_chapterNum)
				{
					chapter_jump_num =  Divx.m_chapterNum;
					common_ui.setText(" "+"Chapter"+Divx.m_chapterNum+" ");
				}
				else if(chapter_jump_num < 1)
				{
					chapter_jump_num = 1;
					common_ui.setText(" "+"Chapter1"+" ");
				}
				
				if(task_playbackquery != null)
				{
					task_playbackquery.cancel();
					task_playbackquery = null;
				}
				
				task_playbackquery = new TimerTask(){
					@Override
					public void run() {
						if(!isMediaplayIdle)
						{
							Divx.execSetGetNavProperty(DivxParser.NAVPROP_INPUT_GET_PLAYBACK_STATUS);
							if(Divx.m_current_chapter_num != chapter_jump_num)
							{
								handler.sendEmptyMessage(HandlerControlerVariable.SET_CHAPTER_NUM);
							}
						}
					}
				};
				
				if(timer != null)
					timer.schedule(task_playbackquery, TimerDelay.delay_2s);
				
				adjustMetaInfoPosition();
			}
			
			hide_meta_data_delay(TimerDelay.delay_4s);
		}
	}
	
	private void captureIntent(Intent intent)
    {
    	int totalCnt = 0;
    	int j = 0;
    	currIndex = intent.getIntExtra("initPos", 0);
    	resume_index = intent.getIntExtra("resume_index", -1);
    	mBrowserType = intent.getIntExtra("mBrowserType", 0);
    	devicePath = intent.getStringExtra("devicePath");
    	
		if (videoList == null) {
			videoList = map.getFileListItems();
		}
		
		totalCnt = videoList.size();
		for(int i = 0; i < totalCnt; i++)
		{
			if(videoList.get(i).getmFileType() == FileFilterType.DEVICE_FILE_VIDEO)
			{
				filePathArray.add(j, videoList.get(i).getPath());
				fileTitleArray.add(j, videoList.get(i).getFileName());
				j++;
			}
		}
    }
    private void captureIntent_TvAnywhere(Intent intent) {
		// TODO Auto-generated method stub
    	
    	currIndex = intent.getIntExtra("initPos", 0);
    	int len = intent.getIntExtra("len", 1);
    	String [] filelist = intent.getStringArrayExtra("filelist");

    	for(int i = 0; i < len; i++)
    	{
    		filePathArray.add(i,filelist[i]);
    		fileTitleArray.add(i, filelist[i].substring(filelist[i].lastIndexOf("/")+1));
    	}
    	
	}
    
    private void setAudioSpdifOutput(int mode)
    {
    	mTVService.setAudioSpdifOutput(mode);
    	
    	hideController(MOVIE_BANNER);
    	
		metaData_name.setVisibility(View.INVISIBLE);
		metaData_language_etc.setVisibility(View.INVISIBLE);
		lawRateInfo.setVisibility(View.INVISIBLE);
		common_ui.setVisibility(View.VISIBLE);
    	
    	if(mode == AUDIO_DIGITAL_RAW)
    		common_ui.setText(" "+ this.getResources().getString(R.string.raw)+ " ");
    	else if(mode == AUDIO_DIGITAL_LPCM_DUAL_CH)
    		common_ui.setText(" "+this.getResources().getString(R.string.LPCM)+" ");
    	
    	
    	if(task_audio_spdif_output_show != null)
    	{
    		task_audio_spdif_output_show.cancel();
    		task_audio_spdif_output_show = null;
    	}
    	task_audio_spdif_output_show = new TimerTask(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(HandlerControlerVariable.HIDE_LEFT_CORN_INFO);
			}
    		
    	};
    	if(timer != null)
    		timer.schedule(task_audio_spdif_output_show, TimerDelay.delay_5s);
    }
	
	private void getAudioTrackInfo()
    {
    	AudioInfo = mPlayer.getAudioTrackInfo(-1);
    	audio_num_stream = AudioInfo[1];
    	curr_audio_stream_num = AudioInfo[2];
    }
	
	private void setAudioTrack()
	{	
		if(audio_num_stream > 0)
 		{
			isAudio_Error = false;
			
 			if(curr_audio_stream_num < audio_num_stream)
     			curr_audio_stream_num++;	
     		else
     			curr_audio_stream_num = 1;
 			
 			mPlayer.setAudioTrackInfo(curr_audio_stream_num);
 			
 			meta_data_query_delay(DivxParser.DIVX_METADATA_AUDIO, TimerDelay.delay_100ms);
 			
 			SetDolby();
 		}
	}
	
	private void getSubtitleInfo()
    {
    	SubtitleInfo = mPlayer.getSubtitleInfo();
		
    	subtitle_num_Stream = SubtitleInfo[1];
		curr_subtitle_stream_num = SubtitleInfo[2];
		
		if(subtitle_num_Stream > 0)
		{	
			if(SubtitleInfo[2] < 0)
				curr_subtitle_stream_num = 1;
		}
    }
	
	private void setSubtitle()
	{	
		getSubtitleInfo();
		
		if(SPU_ENABLE == 0)
			curr_subtitle_stream_num = 0;
		curr_subtitle_stream_num++;
		if(subtitle_num_Stream > 0 && curr_subtitle_stream_num <= subtitle_num_Stream)
		{
			SPU_ENABLE = 1;
			/*int[] currSubtitleInfo = mPlayer.getSubtitleInfo(curr_subtitle_stream_num);
			String mLanguage = Utility.SI_LANG_TO_ISO639(currSubtitleInfo[3]);
			if(mLanguage == null)
			{
				mLanguage = "Undefined";
			}	
			common_ui.setText(curr_subtitle_stream_num + ", " + mLanguage);*/
			
			mPlayer.setSubtitleInfo(curr_subtitle_stream_num, SPU_ENABLE, textEncoding, textColor, fontSize);			
		}else
		{
			SPU_ENABLE = 0;
			curr_subtitle_stream_num = subtitle_num_Stream;
			mPlayer.setSubtitleInfo(curr_subtitle_stream_num, SPU_ENABLE, textEncoding, textColor, fontSize);
		}
		
		meta_data_query_delay(DivxParser.DIVX_METADATA_SUBTITLE, TimerDelay.delay_100ms);
	}
	
	public void VideoPlayerFinish() {
		this.finish();
	}
	
	private void hide_dolby_delay()
	{
		if(task_hide_dolby != null)
		{
			task_hide_dolby.cancel();
			task_hide_dolby = null;
		}
		
		task_hide_dolby = new TimerTask(){

			@Override
			public void run() {
				handler.sendEmptyMessage(HandlerControlerVariable.HIDE_DOLBY);
			}
			
		};
		if(timer != null)
			timer.schedule(task_hide_dolby, TimerDelay.delay_4s);
	}
	
	private void hide_quick_menu_delay(int delay)
	{
		if(task_hide_quickmenu !=  null)
		{
			task_hide_quickmenu.cancel();
			task_hide_quickmenu = null;
		}
		task_hide_quickmenu = new TimerTask(){

			@Override
			public void run() {
				handler.sendEmptyMessage(HandlerControlerVariable.HIDE_QUICK_MENU);
			}
				
		};
		
		if(timer != null)	
			timer.schedule(task_hide_quickmenu, delay);
	}
	
	private void hide_meta_data_delay(int delay)
	{
		if(task_meta_data_show != null)
		{
			task_meta_data_show.cancel();
			task_meta_data_show = null;
		}
			
		task_meta_data_show = new TimerTask(){

			@Override
			public void run() {
				handler.sendEmptyMessage(HandlerControlerVariable.HIDE_LAWRATE_METADATA);
			}
			
		};
		
		if(timer != null)
			timer.schedule(task_meta_data_show, delay);
	}
	
	private void meta_data_query_delay(final int m_divx_metadata_type, int delay)
	{
		if(task_meta_data_query_delay != null)
		{
			task_meta_data_query_delay.cancel();
			task_meta_data_query_delay = null;
		}
		
		task_meta_data_query_delay = new TimerTask(){

			@Override
			public void run() {
				Divx.m_divx_metadata_type = m_divx_metadata_type;
				Divx.execSetGetNavProperty(DivxParser.NAVPROP_DIVX_METADATA_QUERY);
				switch(m_divx_metadata_type)
				{
				case DivxParser.DIVX_METADATA_TITLE:
					Divx.execSetGetNavProperty(DivxParser.NAVPROP_DIVX_LAWRATE_QUERY);
		    		handler.sendEmptyMessage(HandlerControlerVariable.SET_DIVX_METADATA_LAWRATE_INFO);
					break;
				case DivxParser.DIVX_METADATA_AUDIO:
					handler.sendEmptyMessage(HandlerControlerVariable.SET_DIVX_METADATA_AUDIO_INFO);
					break;
				case DivxParser.DIVX_METADATA_CHAPTER:
					handler.sendEmptyMessage(HandlerControlerVariable.SET_DIVX_METADATA_CHAPTER_INFO);
					break;
				case DivxParser.DIVX_METADATA_SUBTITLE:
					handler.sendEmptyMessage(HandlerControlerVariable.SET_DIVX_METADATA_SUBTITLE_INFO);
					break;
				default:
					break;
				}
			}
			
		};
		if(timer != null)
			timer.schedule(task_meta_data_query_delay, delay);
	}

	private void showController(int id){
		switch(id)
		{
		case MOVIE_BANNER:
		if(MovieBannerView != null && sView.isShown()&& fileTitleArray != null)
		{
			filename.setText(fileTitleArray.get(currIndex));
			MovieBannerView.setVisibility(View.VISIBLE);
			if(Divx.m_chapterNum > 1)
			{
				chapter_info.setVisibility(View.VISIBLE);
				chapter_info.setText("Chapter:"+Divx.m_current_chapter_num+"/"+Divx.m_chapterNum);
			}else
			{
				chapter_info.setVisibility(View.INVISIBLE);
			}
		}
		break;
		case QUICK_MENU:
		{
		}
		break;
		case SUBTILE_LIST:
			common_ui.setVisibility(View.VISIBLE);
		break;
		case AUDIO_BANNER:
			common_ui.setVisibility(View.VISIBLE);
		break;
		case WANNING_MESSAGE:
		break;
		case TITLE_BANNER:
			common_ui.setVisibility(View.VISIBLE);
		break;
		case BUTTON_LIST:
			ButtonListView.setVisibility(View.VISIBLE);
		break;
		default:
		break;
		}
		
		adjustMetaInfoPosition();
	}
	
	private void hideController(int id)
	{
		switch(id)
		{
		case MOVIE_BANNER:
			MovieBannerView.setVisibility(View.INVISIBLE);
			if(task_getCurrentPositon != null)
			{
				task_getCurrentPositon.cancel();
				task_getCurrentPositon = null;
			}
		break;
		case QUICK_MENU:
		break;
		case SUBTILE_LIST:
			common_ui.setVisibility(View.INVISIBLE);
		break;
		case AUDIO_BANNER:
			common_ui.setVisibility(View.INVISIBLE);
		break;
		case WANNING_MESSAGE:
		break;
		case TITLE_BANNER:
			common_ui.setVisibility(View.INVISIBLE);
		break;
		case BUTTON_LIST:
			ButtonListView.setVisibility(View.INVISIBLE);
		break;
		default:
		break;
		}
		
		adjustMetaInfoPosition();
	}
	
	private void showCurrentInfo()   // execute this func when Movie banner is showing
	{
		if(task_getCurrentPositon != null)
		{
			task_getCurrentPositon.cancel();
			task_getCurrentPositon = null;
		}
		
		task_getCurrentPositon = new TimerTask(){
        	
        	@Override
			public void run() {
        		if(!isMediaplayIdle && Divx != null)
        		{
        			boolean execError = false;
        			try
        			{
        				handler.sendEmptyMessage(HandlerControlerVariable.PROGRESS_CHANGED);
        				if(Divx.m_chapterNum > 1)
        					Divx.execSetGetNavProperty(DivxParser.NAVPROP_INPUT_GET_PLAYBACK_STATUS);
        			} catch (IllegalArgumentException e) {
            			e.printStackTrace();
            			execError = true;
            		} catch (SecurityException e) {
            			e.printStackTrace();
            			execError = true;
            		} catch (IllegalStateException e) {
            			e.printStackTrace();
            			execError = true;
            		}
        			if(!execError)
        				handler.sendEmptyMessage(HandlerControlerVariable.SET_DIVX_CHAPTER_NUMBER);
        		}
			}
		};
		if(timer != null)
			timer.schedule(task_getCurrentPositon, 0, TimerDelay.delay_100ms);
	}
	
	private void animateShowBanner(){
		if(!MovieBannerView.isShown())
		{
			MovieBannerView.setVisibility(View.VISIBLE);
			if(isShowChapterMetaData)
				adjustMetaNamePosition();
			else
				adjustMetaInfoPosition();
			adjustPicSizePosition();
			MovieBannerView.clearAnimation();
			TranslateAnimation TransAnim;
			TransAnim = new TranslateAnimation(0.0f,0.0f,banner_h,0.0f);	
			TransAnim.setDuration(bannerAnimTime);
			MovieBannerView.startAnimation(TransAnim);
		}
	}
	
	private void animateHideBanner(){
		MovieBannerView.clearAnimation();
		TranslateAnimation TransAnim;
		TransAnim = new TranslateAnimation(0.0f,0.0f,0.0f,banner_h);	
		TransAnim.setDuration(bannerAnimTime);
		TransAnim.setAnimationListener(new hiderBannerListener());
		MovieBannerView.startAnimation(TransAnim);
	}
	
	private class hiderBannerListener implements AnimationListener{
        public void onAnimationEnd(Animation animation) {
            // TODO Auto-generated method stub
        	MovieBannerView.clearAnimation();
        	MovieBannerView.setVisibility(View.INVISIBLE);
        	if(isShowChapterMetaData)
				adjustMetaNamePosition();
			else
				adjustMetaInfoPosition();
        	adjustPicSizePosition();
        }

        public void onAnimationRepeat(Animation animation) {
            // TODO Auto-generated method stub
        }

        public void onAnimationStart(Animation animation) {
            // TODO Auto-generated method stub
        }
        
    }
	
	private void buttonBannerShowControl(int delay)
	{
		if(!ButtonListView.isShown())
			showController(BUTTON_LIST);
		
		if(task_hide_button != null)
		{
			task_hide_button.cancel();
			task_hide_button = null;
		}
		
		task_hide_button = new TimerTask(){
			public void run() {				
				handler.sendEmptyMessage(HandlerControlerVariable.HIDE_BUTTON_LIST);
			}
		};
		
		if(timer != null)
			timer.schedule(task_hide_button, delay);
	}
	
	private void statusBannerShowControl(int delay)
	{
		animateShowBanner();
		
		if(task_hide_controler != null)
		{
			task_hide_controler.cancel();
			task_hide_controler = null;
		}
		
		task_hide_controler = new TimerTask(){
			public void run() {
				handler.sendEmptyMessage(HandlerControlerVariable.HIDE_MOVIE_BANNER);
				if(task_getCurrentPositon != null)
				{
					task_getCurrentPositon.cancel();
					task_getCurrentPositon = null;
				}
			}
		};
		
		if(timer != null)
			timer.schedule(task_hide_controler, delay);
	}
	
	private void rewind()
    {
		forward_press = 1;
		if(!isMediaplayIdle)
    	{
			switch(rewind_press)
			{
			case 1:
			case 7:
				mPlayer.fastrewind(2);
				play.setImageResource(R.drawable.video_player_backward_2x);
				if(rewind_press == 1)
					rewind_press++;
				else if(rewind_press ==7)
					rewind_press = 2;
				isrewind = true;
				break;
			case 2:
			case 6:
				mPlayer.fastrewind(8);
				play.setImageResource(R.drawable.video_player_backward_3x);
				rewind_press++;
				isrewind = true;
				break;
			case 3:
			case 5:
				mPlayer.fastrewind(16);
				play.setImageResource(R.drawable.video_player_backward_4x);
				rewind_press++;
				isrewind = true;
				break;
			case 4:
				mPlayer.fastrewind(32);
				play.setImageResource(R.drawable.video_player_backward_5x);
				rewind_press++;
				isrewind = true;
				break;
			default:
				break;
			}
    	}
    }
    
    private void forward()
    {
    	rewind_press = 1;
    	if(!isMediaplayIdle)
    	{
    		switch(forward_press)
    		{
    		case 1:
    		case 7:
    			mPlayer.fastforward(2);
    			play.setImageResource(R.drawable.video_player_forward_2x);
    			if(forward_press == 1)
    				forward_press++;
    			else if(forward_press == 7)
    				forward_press = 2;
    			isforward = true;
    			break;
    		case 2:
    		case 6:
    			mPlayer.fastforward(8);
    			play.setImageResource(R.drawable.video_player_forward_3x);
    			forward_press++;
    			isforward = true;
    			break;
    		case 3:
    		case 5:
    			mPlayer.fastforward(16);
    			play.setImageResource(R.drawable.video_player_forward_4x);
    			forward_press++;
    			isforward = true;
    			break;
    		case 4:
    			mPlayer.fastforward(32);
    			play.setImageResource(R.drawable.video_player_forward_5x);
    			forward_press++;
    			isforward = true;
    			break;
    		default:
    			break;
    		}
    	}
    }
	
	@SuppressWarnings("static-access")
	private void execChapterJumpTimer()
    {
		if(Divx.m_chapterNum > 1)
		{
			
			int mJump_chapter_num = Integer.parseInt(ButtonNum);
			if(mJump_chapter_num <= Divx.m_chapterNum && mJump_chapter_num > 0)
			{
				common_ui.setVisibility(View.VISIBLE);
				common_ui.setText(" "+"Chapter" + ButtonNum+" ");
			}else if(mJump_chapter_num <= 0)
			{
				common_ui.setVisibility(View.INVISIBLE);
				toast.makeText(getApplicationContext(), 
						this.getResources().getString(R.string.less_than_chapter_num)
						+" "
						+ ButtonNum
						+ " "
						+ this.getResources().getString(R.string.input_inlegal),
            		    Toast.LENGTH_SHORT).show();
				ButtonNum = "";
			}else if(mJump_chapter_num > Divx.m_chapterNum)
			{
				common_ui.setVisibility(View.INVISIBLE);
				toast.makeText(getApplicationContext(), 
						this.getResources().getString(R.string.more_than_chapter_num)
						+ " "
						+ ButtonNum
						+ " "
						+ this.getResources().getString(R.string.input_inlegal),
						Toast.LENGTH_SHORT).show();
				ButtonNum = "";
			}
	    	
	    	if(task_info_show != null)
	    	{
	    		task_info_show.cancel();
	    		task_info_show = null;
	    	}
			task_info_show = new TimerTask(){
				public void run() {
					toast.cancel();
					execChapterJump(0);
				}
			};
			if(timer != null)
				timer.schedule(task_info_show, TimerDelay.delay_1s);
		}
    }
	
	 private void switch_chapter(final int switch_type)
	 {	
	    chapter_key_press++;
	    if(Divx.m_chapterNum > 1)
	     {
	    	if(task_key_event!=null)
	    	{
	    		task_key_event.cancel();
	    		task_key_event = null;
	    	}

	        task_key_event = new TimerTask(){
	        	public void run() {
	        		execChapterJump(switch_type);
	        		chapter_key_press = 0;
	        	}
	         };
	         
	         if(timer != null)
	        	 timer.schedule(task_key_event, TimerDelay.delay_500ms); 
	     }
	  }
    
    private void execChapterJump(int keyCode)
    {
    	/*isforward = false;
		forward_press = 1;
		isrewind = false;
		rewind_press = 1;*/
    	
    	if(Divx.m_chapterNum > 1)
    	{	
    		handler.sendEmptyMessage(HandlerControlerVariable.MEDIA_PLAYER_START);
    		
        	if(ButtonNum != "")
     		{
     			int mJump_chapter_num = Integer.parseInt(ButtonNum);
     			ButtonNum = "";
     			if(mJump_chapter_num <= Divx.m_chapterNum && mJump_chapter_num > 0)
     			{
     				Divx.m_current_chapter_num = mJump_chapter_num;
     				chapter_jump_num = Divx.m_current_chapter_num;
     				playChapter();
     			}
     			
     			hide_meta_data_delay(TimerDelay.No_Delay);
     			
				// delay to query metainfo
				handler.sendEmptyMessage(HandlerControlerVariable.HIDE_METADATA_NAME);
				
				meta_data_query_delay(DivxParser.DIVX_METADATA_CHAPTER, TimerDelay.delay_100ms);
     		}else if(ButtonNum == "")
     		{
     			Divx.execSetGetNavProperty(DivxParser.NAVPROP_INPUT_GET_PLAYBACK_STATUS);
     			if(chapter_key_press == 2)
     			{
     				switch(repeat_mode)
     				{
     				case REPEAT_SINGLE:
     				{
     					if(keyCode == KeyEvent.KEYCODE_DPAD_UP)
         				{
     						if(Divx.m_chapterNum >= 2 )
     						{
     							if(Divx.m_current_chapter_num < Divx.m_chapterNum -1)
            		     		{
            			     		chapter_jump_num = Divx.m_current_chapter_num + 2;
            			     		Divx.m_current_chapter_num = chapter_jump_num;
            		     		}else if(Divx.m_current_chapter_num == Divx.m_chapterNum -1)
            		     		{
            		     			chapter_jump_num = 1;
            			     		Divx.m_current_chapter_num = 1;
            		     		}else if(Divx.m_current_chapter_num == Divx.m_chapterNum)
            		     		{
            		     			chapter_jump_num = 2;
            			     		Divx.m_current_chapter_num = 2;
            		     		}
     							playChapter();
     						}
     						
         				}
     					else if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN)
         				{
         					if(Divx.m_current_chapter_num == 1 && mClick_DPAD_DOWN_NUM == 0)
        		     		{
        		     			playChapter();
        		     			mClick_DPAD_DOWN_NUM = 1;
        		     			chapter_jump_num = 1;
        		     		}else if(Divx.m_current_chapter_num == 1 && mClick_DPAD_DOWN_NUM == 1)
        		     		{
        		     			playPrevChapter();
        		     			chapter_jump_num = 1;
        		     		}else if(Divx.m_current_chapter_num > 1)
        		     		{
        		     			mClick_DPAD_DOWN_NUM = 0;
        		     			playPrevChapter();
        		     			chapter_jump_num = Divx.m_current_chapter_num - 1;
        		     		}
         				}
     					meta_data_query_delay(DivxParser.DIVX_METADATA_CHAPTER, TimerDelay.delay_100ms);
     				}
     				break;
     				case REPEAT_ALL:
     				{
     					handler.sendEmptyMessage(HandlerControlerVariable.HIDE_LAWRATE_METADATA);
     					if(keyCode == KeyEvent.KEYCODE_DPAD_UP)
         				{
     						if(Divx.m_chapterNum >= 2 )
     						{
     							if(Divx.m_current_chapter_num < Divx.m_chapterNum -1)
            		     		{
            			     		chapter_jump_num = Divx.m_current_chapter_num + 2;
            			     		Divx.m_current_chapter_num = chapter_jump_num;
            			     		playChapter();
            			     		
            			     		meta_data_query_delay(DivxParser.DIVX_METADATA_CHAPTER, TimerDelay.delay_100ms);
            		     		}else if(Divx.m_current_chapter_num == Divx.m_chapterNum -1)
            		     		{
            		     			chapter_jump_num = Divx.m_current_chapter_num + 1;
            			     		Divx.m_current_chapter_num = chapter_jump_num;
            			     		playChapter();
            			     		
            			     		meta_data_query_delay(DivxParser.DIVX_METADATA_CHAPTER, TimerDelay.delay_100ms);
            		     		}else if(Divx.m_current_chapter_num == Divx.m_chapterNum)
            		     		{
            		     			Divx.m_current_title_num++;
            		     			if(Divx.m_current_title_num > Divx.m_titleNum)
            		     			{
            		     				Divx.m_current_title_num = Divx.m_titleNum;
            		     				isSwitchToNextFile = true;
            		     				handler.sendEmptyMessage(HandlerControlerVariable.SWITCH_FILE_QUERY);
            		     			}else
            		     				handler.sendEmptyMessage(HandlerControlerVariable.SWITCH_TITLE_QUERY);
            		     		}
     						}
         				}
     					else if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN)
         				{
     						if(Divx.m_current_chapter_num > 1)
     						{
     							mClick_DPAD_DOWN_NUM = 0;
     							
     							playPrevChapter();
     							chapter_jump_num = Divx.m_current_chapter_num - 1;
     							
     							meta_data_query_delay(DivxParser.DIVX_METADATA_CHAPTER, TimerDelay.delay_100ms);
     						}else if(Divx.m_current_chapter_num == 1 && mClick_DPAD_DOWN_NUM == 0)
        		     		{
        		     			playChapter();
        		     			mClick_DPAD_DOWN_NUM = 1;
        		     			chapter_jump_num = 1;
        		     			
        		     			meta_data_query_delay(DivxParser.DIVX_METADATA_CHAPTER, TimerDelay.delay_100ms);
        		     		}else if(Divx.m_current_chapter_num == 1 && mClick_DPAD_DOWN_NUM == 1)
     						{
     							Divx.m_current_title_num--;
        		     			if(Divx.m_current_title_num < 1)
        		     			{
        		     				Divx.m_current_title_num = 1;
        		     				isSwitchToNextFile = false;
        		     				handler.sendEmptyMessage(HandlerControlerVariable.SWITCH_FILE_QUERY);
        		     			}else
        		     			{
        		     				handler.sendEmptyMessage(HandlerControlerVariable.SWITCH_TITLE_QUERY);
        		     			}
     						}
         				}
     				}
     				break;
     				case REPEAT_OFF:
     				{
     					if(keyCode == KeyEvent.KEYCODE_DPAD_UP)
         				{
     						if(Divx.m_current_chapter_num < Divx.m_chapterNum -1)
        		     		{
        			     		chapter_jump_num = Divx.m_current_chapter_num + 2;
        			     		Divx.m_current_chapter_num = chapter_jump_num;
        			     		playChapter();
        			     		
        			     		meta_data_query_delay(DivxParser.DIVX_METADATA_CHAPTER, TimerDelay.delay_100ms);
        		     		}else if(Divx.m_current_chapter_num == Divx.m_chapterNum -1)
        		     		{
        		     			chapter_jump_num = Divx.m_current_chapter_num + 1;
        			     		Divx.m_current_chapter_num = chapter_jump_num;
        			     		playChapter();
        			     		
        			     		meta_data_query_delay(DivxParser.DIVX_METADATA_CHAPTER, TimerDelay.delay_100ms);
        		     		}
     						//For bug 0049036
        		     		else if(Divx.m_current_chapter_num == Divx.m_chapterNum)
        		     		{
        		     			Divx.m_current_title_num++;
        		     			if(Divx.m_current_title_num > Divx.m_titleNum)
        		     			{
        		     				Divx.m_current_title_num = Divx.m_titleNum;
        		     				isSwitchToNextFile = true;
        		     				
        		     				if(currIndex >= filePathArray.size()-1)
        		     					VideoPlayerFinish();
        		     				else
        		     					handler.sendEmptyMessage(HandlerControlerVariable.SWITCH_FILE_QUERY);
        		     			}else
        		     				handler.sendEmptyMessage(HandlerControlerVariable.SWITCH_TITLE_QUERY);
        		     		}
     						/*else if(Divx.m_current_chapter_num == Divx.m_chapterNum)
        		     		{
        		     			Divx.m_current_title_num++;
        		     			if(Divx.m_current_title_num > Divx.m_titleNum)
        		     			{
            		     			VideoPlayerFinish();
        		     			}
        		     			else
        		     				handler.sendEmptyMessage(HandlerControlerVariable.SWITCH_TITLE_QUERY);
        		     		}*/
         				}
     					else if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN)
         				{
     						if(Divx.m_current_chapter_num > 1)
     						{
     							mClick_DPAD_DOWN_NUM = 0;
     							
     							playPrevChapter();
     							chapter_jump_num = Divx.m_current_chapter_num - 1;
     							
     							meta_data_query_delay(DivxParser.DIVX_METADATA_CHAPTER, TimerDelay.delay_100ms);
     						}else if(Divx.m_current_chapter_num == 1 && mClick_DPAD_DOWN_NUM == 0)
        		     		{
        		     			playChapter();
        		     			chapter_jump_num = 1;
        		     			mClick_DPAD_DOWN_NUM = 1;
        		     			meta_data_query_delay(DivxParser.DIVX_METADATA_CHAPTER, TimerDelay.delay_100ms);
        		     		}else if(Divx.m_current_chapter_num == 1 && mClick_DPAD_DOWN_NUM == 1)
     						{
     							Divx.m_current_title_num--;
        		     			if(Divx.m_current_title_num < 1)
        		     			{
        		     				if(currIndex > 0)
        		     				{
        		     					Divx.m_current_title_num = 1;
            		     				isSwitchToNextFile = false;
            		     				handler.sendEmptyMessage(HandlerControlerVariable.SWITCH_FILE_QUERY);
        		     				}	
        		     			}else
        		     			{
        		     				handler.sendEmptyMessage(HandlerControlerVariable.SWITCH_TITLE_QUERY);
        		     			}
     						}
         				}
     				}
     				break;
     			}
     			}else if(chapter_key_press == 1)
     			{	
     				switch(repeat_mode)
     				{ 
     				case REPEAT_SINGLE:
     				{
     					if(keyCode == KeyEvent.KEYCODE_DPAD_UP)
         				{
     						if(Divx.m_current_chapter_num < Divx.m_chapterNum)
        		     		{
     							playNextChapter();
        			     		chapter_jump_num = Divx.m_current_chapter_num + 1;
        		     		}else if(Divx.m_current_chapter_num == Divx.m_chapterNum)
        		     		{
        		     			Divx.m_current_chapter_num = 1;
        		     			playChapter();
        		     			chapter_jump_num = 1;
        		     		}
         				}else if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN)
         				{
         					int interval = 0;      					
         					Parcel p = mPlayer.getParcelParameter(1600);
         					int mFrameRate = p.readInt();
         					interval= mPlayer.getCurrentPosition() - (Divx.mcurrChapterStartTime*1000 + Divx.mFrameIndex * 1000/mFrameRate);
         					if(interval < 2000)
         					{
         						if(Divx.m_current_chapter_num == 1 && mClick_DPAD_DOWN_NUM == 0)
            		     		{
            		     			playChapter();
            		     			mClick_DPAD_DOWN_NUM = 1;
            		     			chapter_jump_num = 1;
            		     		}else if(Divx.m_current_chapter_num == 1 && mClick_DPAD_DOWN_NUM == 1)
            		     		{
            		     			playPrevChapter();
            		     			chapter_jump_num = 1;
            		     		}else if(Divx.m_current_chapter_num > 1)
            		     		{
            		     			mClick_DPAD_DOWN_NUM = 0;
            		     			playPrevChapter();
            		     			chapter_jump_num = Divx.m_current_chapter_num - 1;
            		     		}
         					}else
         					{
         						playChapter();
             					chapter_jump_num = Divx.m_current_chapter_num;
         					}

         				}
     					
     					meta_data_query_delay(DivxParser.DIVX_METADATA_CHAPTER, TimerDelay.delay_100ms);
     				}
     				break;
     				case REPEAT_ALL:
     				{
     					handler.sendEmptyMessage(HandlerControlerVariable.HIDE_LAWRATE_METADATA);
     					if(keyCode == KeyEvent.KEYCODE_DPAD_UP)
         				{
     						if(Divx.m_current_chapter_num < Divx.m_chapterNum)
        		     		{	
     							playNextChapter();
        			     		Log.e(TAG, "#########Divx.m_current_chapter_num = "+Divx.m_current_chapter_num );
        			     		/*if(Divx.m_current_chapter_num == Divx.m_chapterNum)
        			     			chapter_jump_num = Divx.m_current_chapter_num;
        			     		else*/
        			     		chapter_jump_num = Divx.m_current_chapter_num + 1;
        			     		
        			     		meta_data_query_delay(DivxParser.DIVX_METADATA_CHAPTER, TimerDelay.delay_100ms);
        		     		}
     						else if(Divx.m_current_chapter_num == Divx.m_chapterNum)
        		     		{
        		     			Divx.m_current_title_num++;
        		     			if(Divx.m_current_title_num > Divx.m_titleNum)
        		     			{
        		     				Divx.m_current_title_num = Divx.m_titleNum;
        		     				isSwitchToNextFile = true;
        		     				handler.sendEmptyMessage(HandlerControlerVariable.SWITCH_FILE_QUERY);
        		     			}else
        		     				handler.sendEmptyMessage(HandlerControlerVariable.SWITCH_TITLE_QUERY);
        		     		}
         				}else if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN)
         				{
         					int interval = 0;      					
         					Parcel p = mPlayer.getParcelParameter(1600);
         					int mFrameRate = p.readInt();
         					interval= mPlayer.getCurrentPosition() - (Divx.mcurrChapterStartTime*1000 + Divx.mFrameIndex * 1000/mFrameRate);
         					if(interval <= 2000)
         					{
         						if(Divx.m_current_chapter_num > 1)
         						{
         							mClick_DPAD_DOWN_NUM = 0;
         							
         							playPrevChapter();
         							Log.e(TAG, "mPlayer.execPlayPrevChapter()");
         							chapter_jump_num = Divx.m_current_chapter_num - 1;
         							
         							meta_data_query_delay(DivxParser.DIVX_METADATA_CHAPTER, TimerDelay.delay_100ms);
         						}else if(Divx.m_current_chapter_num == 1 && mClick_DPAD_DOWN_NUM == 0)
            		     		{
            		     			playChapter();
            		     			mClick_DPAD_DOWN_NUM = 1;
            		     			chapter_jump_num = 1;
            		     			
            		     			meta_data_query_delay(DivxParser.DIVX_METADATA_CHAPTER, TimerDelay.delay_100ms);
            		     		}else if(Divx.m_current_chapter_num == 1 && mClick_DPAD_DOWN_NUM == 1)
         						{
         							Divx.m_current_title_num--;
            		     			if(Divx.m_current_title_num < 1)
            		     			{
            		     				Divx.m_current_title_num = 1;
            		     				isSwitchToNextFile = false;
            		     				handler.sendEmptyMessage(HandlerControlerVariable.SWITCH_FILE_QUERY);
            		     			}else
            		     			{
            		     				handler.sendEmptyMessage(HandlerControlerVariable.SWITCH_TITLE_QUERY);
            		     			}
         						}
         					}else
         					{
         						playChapter();
             					chapter_jump_num = Divx.m_current_chapter_num;
             					meta_data_query_delay(DivxParser.DIVX_METADATA_CHAPTER, TimerDelay.delay_100ms);
         					}
         				}
     				}
     				break;                                                                
     				case REPEAT_OFF:
     				{
     					if(keyCode == KeyEvent.KEYCODE_DPAD_UP)
         				{
     						if(Divx.m_current_chapter_num < Divx.m_chapterNum)
        		     		{
     							playNextChapter();
        			     		
        			     		/*if(Divx.m_current_chapter_num == Divx.m_chapterNum)
        			     			chapter_jump_num = Divx.m_current_chapter_num;
        			     		else*/
        			     		chapter_jump_num = Divx.m_current_chapter_num + 1;
        			     		
        			     		meta_data_query_delay(DivxParser.DIVX_METADATA_CHAPTER, TimerDelay.delay_100ms);
        		     		}
     						else if(Divx.m_current_chapter_num == Divx.m_chapterNum)
        		     		{
        		     			Divx.m_current_title_num++;
        		     			if(Divx.m_current_title_num >= Divx.m_titleNum)
        		     			{
        		     				Divx.m_current_title_num = Divx.m_titleNum;
        		     				isSwitchToNextFile = true;
        		     				
        		     				if(currIndex >= filePathArray.size()-1)
        		     					VideoPlayerFinish();
        		     				else
        		     					handler.sendEmptyMessage(HandlerControlerVariable.SWITCH_FILE_QUERY);
        		     			}else
        		     				handler.sendEmptyMessage(HandlerControlerVariable.SWITCH_TITLE_QUERY);
        		     		}
     						//For bug 0049036
     						/*else if(Divx.m_current_chapter_num == Divx.m_chapterNum)
        		     		{
        		     			Divx.m_current_title_num++;
        		     			if(Divx.m_current_title_num > Divx.m_titleNum)
        		     			{
            		     			VideoPlayerFinish();
        		     			}
        		     			else
        		     				handler.sendEmptyMessage(HandlerControlerVariable.SWITCH_TITLE_QUERY);
        		     		}*/
         				}else if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN)
         				{
         					int interval = 0;      					
         					Parcel p = mPlayer.getParcelParameter(1600);
         					int mFrameRate = p.readInt();
         					interval= mPlayer.getCurrentPosition() - (Divx.mcurrChapterStartTime*1000 + Divx.mFrameIndex * 1000/mFrameRate);
         					if(interval < 2000)
         					{
         						if(Divx.m_current_chapter_num > 1)
         						{
         							mClick_DPAD_DOWN_NUM = 0;
         							
         							playPrevChapter();
         							chapter_jump_num = Divx.m_current_chapter_num - 1;
         							
         							meta_data_query_delay(DivxParser.DIVX_METADATA_CHAPTER, TimerDelay.delay_100ms);
         						}else if(Divx.m_current_chapter_num == 1 && mClick_DPAD_DOWN_NUM == 0)
            		     		{
            		     			playChapter();
            		     			chapter_jump_num = 1;
            		     			mClick_DPAD_DOWN_NUM = 1;
            		     			meta_data_query_delay(DivxParser.DIVX_METADATA_CHAPTER, TimerDelay.delay_100ms);
            		     		}else if(Divx.m_current_chapter_num == 1 && mClick_DPAD_DOWN_NUM == 1)
         						{
         							Divx.m_current_title_num--;
            		     			if(Divx.m_current_title_num < 1)
            		     			{
            		     				if(currIndex > 0)
            		     				{
            		     					Divx.m_current_title_num = 1;
                		     				isSwitchToNextFile = false;
                		     				handler.sendEmptyMessage(HandlerControlerVariable.SWITCH_FILE_QUERY);
            		     				}	
            		     			}else
            		     			{
            		     				handler.sendEmptyMessage(HandlerControlerVariable.SWITCH_TITLE_QUERY);
            		     			}
         						}
         					}else
         					{
         						playChapter();
             					chapter_jump_num = Divx.m_current_chapter_num;
             					meta_data_query_delay(DivxParser.DIVX_METADATA_CHAPTER, TimerDelay.delay_100ms);
         					}
         				}
     				}
     				break;
     				}
     			}
     		}
    	}
    }
    
    private void playChapter()
    {
    	if(Divx.chaptertype== 1 && Divx.m_current_title_num >= 1 && Divx.m_current_chapter_num >= 1)
		{
			Log.v(TAG, "[Play Auto Chapter]");
			if(!mPlayer.isPlaying())
     			mPlayer.start();
			mPlayer.execPlayAutoChapter(Divx.m_current_title_num, Divx.m_current_chapter_num);
		} else if (Divx.chaptertype == 0 && Divx.m_current_title_num >= 1 && Divx.m_current_chapter_num >= 1){
			Log.v(TAG, "[Play Author Chapter]");
			if(!mPlayer.isPlaying())
     			mPlayer.start();
			mPlayer.execPlayChapter(Divx.m_current_title_num, Divx.m_current_chapter_num);
		}
    }
    
    private void playNextChapter()
    {
    	if(!mPlayer.isPlaying())
 			mPlayer.start();
    	mPlayer.execPlayNextChapter();
    }
    
    private void playPrevChapter()
    {
    	if(!mPlayer.isPlaying())
 			mPlayer.start();
    	mPlayer.execPlayPrevChapter();
    }
    
    private void switch_title_query(final int jump_to_titleNum)
    {
    	hide_all();
    	help_bar.setVisibility(View.VISIBLE);
    	
    	long_msg.setMessage(VideoPlayerActivity.this.getResources().getString(R.string.title_jump));
    	long_msg.setButtonText(VideoPlayerActivity.this.getResources().getString(R.string.msg_yes));
    	long_msg.setKeyListener(true);
    	
    	long_msg.setOnDismissListener(new OnDismissListener(){

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				help_bar.setVisibility(View.INVISIBLE);
			}
    		
    	});
    	
		long_msg.confirm_bt.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(long_msg.confirm_text.getText().toString().compareTo
				(VideoPlayerActivity.this.getResources().getString(R.string.msg_yes))==0)
				{
					switch_title(jump_to_titleNum);
				}
				else if(long_msg.confirm_text.getText().toString().compareTo
				(VideoPlayerActivity.this.getResources().getString(R.string.msg_no))==0)
				{
				}
				help_bar.setVisibility(View.INVISIBLE);
				long_msg.dismiss();
			}
					
		});
		if(isRight2Left)
			long_msg.setMessageRight();
		else
			long_msg.setMessageLeft();
		long_msg.show();
    }
    
    @SuppressWarnings("static-access")
	private void switch_title_by_key(boolean isNext)
    {
    	if(Divx.m_titleNum > 1 && !isforward && !isrewind) //can not switch title when is rewinding or forwarding.
 		{
 			
     		Divx.execSetGetNavProperty(DivxParser.NAVPROP_INPUT_GET_PLAYBACK_STATUS);
     		Log.e(TAG, "Divx.m_current_title_num = "+Divx.m_current_title_num);
     		if(isNext)
     		{
     			int jump_to_titleNum = Divx.m_current_title_num + 1;
         		if(jump_to_titleNum > Divx.m_titleNum)
         		{
         			jump_to_titleNum = Divx.m_titleNum;
         			return;
         		}
         		
         		handler.sendEmptyMessage(HandlerControlerVariable.HIDE_LAWRATE_METADATA);
     			hideController(MOVIE_BANNER);
     			hideController(BUTTON_LIST);
     			
         		switch_title(jump_to_titleNum);
     		}else
     		{
     			int jump_to_titleNum = Divx.m_current_title_num - 1;
         		if(jump_to_titleNum < 1)
         		{
         			jump_to_titleNum = 1;
         			return;
         		}
         		
         		handler.sendEmptyMessage(HandlerControlerVariable.HIDE_LAWRATE_METADATA);
     			hideController(MOVIE_BANNER);
     			hideController(BUTTON_LIST);
     			
         		switch_title(jump_to_titleNum);
     		}
     		
 		}/*else
 		{
 			toast.makeText(getApplicationContext(), 
 					this.getResources().getString(R.string.msg_donot_switch_title),
        		    Toast.LENGTH_SHORT).show();
 		}*/
    }
    
    private void switch_title_by_Redkey(boolean isNext)
    {
    	if(Divx.m_titleNum > 1 && !isforward && !isrewind) //can not switch title when is rewinding or forwarding.
 		{
 			handler.sendEmptyMessage(HandlerControlerVariable.HIDE_LAWRATE_METADATA);
 			hideController(MOVIE_BANNER);
 			hideController(BUTTON_LIST);
 			
     		Divx.execSetGetNavProperty(DivxParser.NAVPROP_INPUT_GET_PLAYBACK_STATUS);
     		Log.e(TAG, "Divx.m_current_title_num = "+Divx.m_current_title_num);
     		if(isNext)
     		{
     			int jump_to_titleNum = Divx.m_current_title_num + 1;
         		if(jump_to_titleNum > Divx.m_titleNum)
         			jump_to_titleNum = 1;
         		switch_title(jump_to_titleNum);
     		}else
     		{
     			int jump_to_titleNum = Divx.m_current_title_num - 1;
         		if(jump_to_titleNum < 1)
         			jump_to_titleNum = Divx.m_titleNum;
         		switch_title(jump_to_titleNum);
     		}
 		}else
 		{
 			meta_data_query_delay(DivxParser.DIVX_METADATA_TITLE, TimerDelay.No_Delay);
 		}
    }
    
    private void switch_title(int jump_to_titleNum)
    {
    	isforward = false;
		forward_press = 1;
		isrewind = false;
		rewind_press = 1;
		
    	if(Divx.m_titleNum > 1 && jump_to_titleNum >= 1 && jump_to_titleNum <= Divx.m_titleNum)
    	{		
    		isAudio_Error = false;
       		
       		Divx.execSetGetNavProperty(DivxParser.NAVPROP_INPUT_GET_PLAYBACK_STATUS);
    		
       		//BookMarkTitle
    		Divx.execSetGetNavProperty(DivxParser.NAVPROP_INPUT_GET_NAV_STATE);
    		getAudioTrackInfo();
    		getSubtitleInfo();
    		mTitleBookMark.addBookMark(Divx.m_current_title_num, curr_subtitle_stream_num, SPU_ENABLE, curr_audio_stream_num, Divx.outputArray);
    				
    		//For close the subtitle of last title
    		if(SPU_ENABLE == 1)
    			mPlayer.setSubtitleInfo(curr_subtitle_stream_num, 0, textEncoding, textColor, fontSize);
    		
    		int index = mTitleBookMark.findBookMark(jump_to_titleNum);
    		if (index >= 0)
    		{
    			// find TitleBookMark
    			Divx.inputArray = mTitleBookMark.getNavBuffer(index);
   				Divx.execSetGetNavProperty(mPlayer.NAVPROP_INPUT_SET_NAV_STATE);
   				if(Divx.HasEditionTitle)
   	   			{
   					int length = mTitleBookMark.bookMarkLength();
   					final int subtitleTrack = mTitleBookMark.getSubtitleTrack(length-1);
   					final int subtitleOn = mTitleBookMark.isSubtitleOn(length-1);
   					final int audioTrack = mTitleBookMark.getAudioTrack(length-1);
   					
   					
   					if(task_query_subtitle_audio != null)
   		    		{
   		    			task_query_subtitle_audio.cancel();
   		    			task_query_subtitle_audio = null;
   		    		}
   		    		
   		    		task_query_subtitle_audio = new TimerTask()
   		    		{
   						@Override
   						public void run() {
   							getSubtitleInfo();
   							getAudioTrackInfo();
   							
   							if (subtitleTrack > 0 && subtitleOn == 1)
   		   						mPlayer.setSubtitleInfo(subtitleTrack, subtitleOn, textEncoding, textColor, fontSize);
   		   	   				
   		   	   		   		if(audioTrack > 0)
   		   	   		   			mPlayer.setAudioTrackInfo(audioTrack);
   				        	
   				        	Divx.execSetGetNavProperty(DivxParser.NAVPROP_DIVX_CHAPTERTYPE_QUERY); //check chapter type
   						}
   		    			
   		    		};
   		    		
   		    		if(timer != null)
   		    			timer.schedule(task_query_subtitle_audio, TimerDelay.delay_500ms);
   					
   	   			}
   				else
   	   			{
   					final int subtitleTrack = mTitleBookMark.getSubtitleTrack(index);
   					final int subtitleOn = mTitleBookMark.isSubtitleOn(index);
   					final int audioTrack = mTitleBookMark.getAudioTrack(index);
   					Log.e(TAG, "###########subtitleTrack = "+subtitleTrack);
   					
   					if(task_query_subtitle_audio != null)
   		    		{
   		    			task_query_subtitle_audio.cancel();
   		    			task_query_subtitle_audio = null;
   		    		}
   		    		
   		    		task_query_subtitle_audio = new TimerTask()
   		    		{
   						@Override
   						public void run() {
   							getSubtitleInfo();
   							getAudioTrackInfo();
   							
   							if (subtitleTrack > 0 && subtitleOn == 1)
   		   						mPlayer.setSubtitleInfo(subtitleTrack, subtitleOn, textEncoding, textColor, fontSize);
   		   	   				
   		   	   		   		if(audioTrack > 0)
   		   	   		   			mPlayer.setAudioTrackInfo(audioTrack);
   				        	
   				        	Divx.execSetGetNavProperty(DivxParser.NAVPROP_DIVX_CHAPTERTYPE_QUERY); //check chapter type
   						}
   		    			
   		    		};
   		    		
   		    		if(timer != null)
   		    			timer.schedule(task_query_subtitle_audio, TimerDelay.delay_500ms);
   	   			}
   				
   				mTitleBookMark.removeBookMark(index);
    		}
    		else
    		{
    			// not find TitleBookMark
    			mPlayer.execPlayTitle(jump_to_titleNum);
       			if (Divx.HasEditionTitle)
       			{
       				int length = mTitleBookMark.bookMarkLength();
   					final int subtitleTrack = mTitleBookMark.getSubtitleTrack(length-1);
   					final int subtitleOn = mTitleBookMark.isSubtitleOn(length-1);
   					final int audioTrack = mTitleBookMark.getAudioTrack(length-1);
   				
   					if(task_query_subtitle_audio != null)
   		    		{
   		    			task_query_subtitle_audio.cancel();
   		    			task_query_subtitle_audio = null;
   		    		}
   		    		
   		    		task_query_subtitle_audio = new TimerTask()
   		    		{
   						@Override
   						public void run() {
   							getSubtitleInfo();
   							getAudioTrackInfo();
   							
   							if (subtitleTrack > 0 && subtitleOn == 1)
   		   						mPlayer.setSubtitleInfo(subtitleTrack, subtitleOn, textEncoding, textColor, fontSize);
   		   	   				
   		   	   		   		if(audioTrack > 0)
   		   	   		   			mPlayer.setAudioTrackInfo(audioTrack);
   				        	
   				        	Divx.execSetGetNavProperty(DivxParser.NAVPROP_DIVX_CHAPTERTYPE_QUERY); //check chapter type
   						}
   		    			
   		    		};
   		    		
   		    		if(timer != null)
   		    			timer.schedule(task_query_subtitle_audio, TimerDelay.delay_500ms);
       			}
       			else
       			{
       				if(task_query_subtitle_audio != null)
   		    		{
   		    			task_query_subtitle_audio.cancel();
   		    			task_query_subtitle_audio = null;
   		    		}
   		    		
   		    		task_query_subtitle_audio = new TimerTask()
   		    		{
   						@Override
   						public void run() {
   							getSubtitleInfo();
   							getAudioTrackInfo();
   				        	
   				        	Divx.execSetGetNavProperty(DivxParser.NAVPROP_DIVX_CHAPTERTYPE_QUERY); //check chapter type
   						}
   		    			
   		    		};
   		    		
   		    		if(timer != null)
   		    			timer.schedule(task_query_subtitle_audio, TimerDelay.delay_500ms);
       			}
    		}
       		
        	
        	meta_data_query_delay(DivxParser.DIVX_METADATA_TITLE, TimerDelay.delay_100ms);
  
    		if(task_getduration != null)
    		{
    			task_getduration.cancel();
    			task_getduration = null;
    		}

			task_getduration = new TimerTask(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					handler.sendEmptyMessage(HandlerControlerVariable.GET_DURATION);
				}
					
			};
			
			if(timer != null)
				timer.schedule(task_getduration, TimerDelay.delay_500ms);
    	}
    }
    
    private void switch_file_query(final int keyCode)
    {
    	hide_all();
    	help_bar.setVisibility(View.VISIBLE);
    	
    	long_msg.setMessage(VideoPlayerActivity.this.getResources().getString(R.string.title_jump));
    	long_msg.setButtonText(VideoPlayerActivity.this.getResources().getString(R.string.msg_yes));
    	long_msg.setKeyListener(true);
    	
    	long_msg.setOnDismissListener(new OnDismissListener(){

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				help_bar.setVisibility(View.INVISIBLE);
			}
    		
    	});
    	
    	long_msg.confirm_bt.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(long_msg.confirm_text.getText().toString().compareTo
				(VideoPlayerActivity.this.getResources().getString(R.string.msg_yes))==0)
				{
					switch_file(keyCode);
				}
				else if(long_msg.confirm_text.getText().toString().compareTo
				(VideoPlayerActivity.this.getResources().getString(R.string.msg_no))==0)
				{
				}
				
		    	help_bar.setVisibility(View.INVISIBLE);
				long_msg.dismiss();
			}
					
		});
		
		long_msg.show();
    }
    
    //This fuc can be called when there is no chapter in the file
    private void switch_file_by_skip_key(boolean isSwitchToNextFile)
    {
    	isforward = false;
		forward_press = 1;
		isrewind = false;
		rewind_press = 1;
		
		isAudio_Error = false;
		
		if(task_hide_controler != null)
		{
			task_hide_controler.cancel();
			task_hide_controler = null;
		}
		
		if(task_hide_button != null)
		{
			task_hide_button.cancel();
			task_hide_button = null;
		}
		
		switch(repeat_mode)
    	{
    	case REPEAT_SINGLE:
    		if(task_getCurrentPositon != null)
			{
				task_getCurrentPositon.cancel();
				task_getCurrentPositon = null;
			}
			
			mPlayer.stop();
	    	handler.sendEmptyMessage(HandlerControlerVariable.MEDIAPLAY_INIT);
    		break;
		case REPEAT_OFF:
			if(isSwitchToNextFile)
    		{
				currIndex++;
	     		if(currIndex > filePathArray.size()-1)
	     			currIndex = filePathArray.size()-1;
	     		else
	     		{
	     			if(task_getCurrentPositon != null)
	    			{
	    				task_getCurrentPositon.cancel();
	    				task_getCurrentPositon = null;
	    			}
	    			
	    			mPlayer.stop();
	    	    	handler.sendEmptyMessage(HandlerControlerVariable.MEDIAPLAY_INIT);
	     		}
	     			
    		}else
    		{
    			if(mPlayer.getCurrentPosition() <= 2000)
    			{
    				currIndex--;
            		if(currIndex < 0)
            			currIndex = 0;
            		else
            		{
            			if(task_getCurrentPositon != null)
    	    			{
    	    				task_getCurrentPositon.cancel();
    	    				task_getCurrentPositon = null;
    	    			}
    	    			
    	    			mPlayer.stop();
    	    	    	handler.sendEmptyMessage(HandlerControlerVariable.MEDIAPLAY_INIT);
            		}
    			}else
    			{
    				if(task_getCurrentPositon != null)
	    			{
	    				task_getCurrentPositon.cancel();
	    				task_getCurrentPositon = null;
	    			}
	    			
	    			mPlayer.stop();
	    	    	handler.sendEmptyMessage(HandlerControlerVariable.MEDIAPLAY_INIT);
    			}
    		}
			
			break;
		case REPEAT_ALL:
			if(isSwitchToNextFile)
    		{
				currIndex++;
	     		if(currIndex > filePathArray.size()-1)
	     			currIndex = 0;
    		}else
    		{
    			if(mPlayer.getCurrentPosition() <= 2000)
    			{
    				currIndex--;
            		if(currIndex < 0)
            			currIndex = filePathArray.size()-1;
    			}
    		}
			
			if(task_getCurrentPositon != null)
			{
				task_getCurrentPositon.cancel();
				task_getCurrentPositon = null;
			}
			
			mPlayer.stop();
	    	handler.sendEmptyMessage(HandlerControlerVariable.MEDIAPLAY_INIT);
			
			break;
    	}
    }
    
    private void switch_file(final int keyCode)
    {    
    	isforward = false;
		forward_press = 1;
		isrewind = false;
		rewind_press = 1;
		
		isAudio_Error = false;
		
		if(task_hide_controler != null)
		{
			task_hide_controler.cancel();
			task_hide_controler = null;
		}
		
		if(task_hide_button != null)
		{
			task_hide_button.cancel();
			task_hide_button = null;
		}
    	
    	switch(repeat_mode)
    	{
    	case REPEAT_SINGLE:
		case REPEAT_OFF:
			if(keyCode == KeyEvent.KEYCODE_CHANNEL_UP)
			{
				currIndex++;
	     		if(currIndex > filePathArray.size() -1)
	     			currIndex = filePathArray.size() - 1; //can not switch file at repeat off mode when currIndex is (filePathArray.size() -1)
	     		else
	     		{
	     			if(task_getCurrentPositon != null)
	     			{
	     				task_getCurrentPositon.cancel();
	     				task_getCurrentPositon = null;
	     			}
	     			
	     			mPlayer.stop();
	     			handler.sendEmptyMessage(HandlerControlerVariable.MEDIAPLAY_INIT);
	     		}
	     	}else if(keyCode == KeyEvent.KEYCODE_CHANNEL_DOWN)
			{
        		currIndex--;
        		if(currIndex < 0)
        		     currIndex = 0; //can not switch file at repeat off mode when currIndex is 0
        		else
        		{
        			if(task_getCurrentPositon != null)
        			{
        				task_getCurrentPositon.cancel();
        				task_getCurrentPositon = null;
        			}
        			
        			mPlayer.stop();
        		    handler.sendEmptyMessage(HandlerControlerVariable.MEDIAPLAY_INIT);
        		}
        	}
			break;
		case REPEAT_ALL:
			if(keyCode == KeyEvent.KEYCODE_CHANNEL_UP)
			{
				currIndex++;
	     		if(currIndex > filePathArray.size()-1)
	     			currIndex = 0;
			}else if(keyCode == KeyEvent.KEYCODE_CHANNEL_DOWN)
			{
				currIndex--;
        		if(currIndex < 0)
        			currIndex = filePathArray.size()-1;
			}
			
			if(task_getCurrentPositon != null)
			{
				task_getCurrentPositon.cancel();
				task_getCurrentPositon = null;
			}
			
			mPlayer.stop();
	    	handler.sendEmptyMessage(HandlerControlerVariable.MEDIAPLAY_INIT);
			break;
    	}
    }
    
    private void media_play_init(String path, String file_name)
    {
    	play.requestFocus();
		play.setImageResource(R.drawable.au_pause);
		
		if(task_getCurrentPositon != null)
		{
			task_getCurrentPositon.cancel();
			task_getCurrentPositon = null;
		}
		
		mPlayer.reset();
		isMediaplayIdle = true;
		mPlayer.setOnPreparedListener(videoPreparedListener);
		mPlayer.setOnCompletionListener(videoCompletionListener);
		mPlayer.setOnInfoListener(VideoInfoListener);
		mPlayer.setOnErrorListener(ErrorListener);
    	//mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			mPlayer.setPlayerType(6); // use RTK_MediaPlayer
			mPlayer.setDataSource(path);
			mPlayer.setParameter(1400, 1); // Need load DRM
			mPlayer.setParameter(1500, 1); // Need change LastPlayPath 
			mPlayer.prepareAsync();
			mPlayer.setDisplay(sView.getHolder());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
			skipUnsupportFile(currIndex);
			//PopupErrorMessageShow(VideoPlayerActivity.this.getResources().getString(R.string.msg_unsupport), TimerDelay.delay_2s);		
		} catch (IOException e) {
			e.printStackTrace();
			skipUnsupportFile(currIndex);
			//PopupErrorMessageShow(VideoPlayerActivity.this.getResources().getString(R.string.msg_unsupport), TimerDelay.delay_2s);
		}
		
		int index = currIndex + 1;
		filename.setText(getApplicationContext().getText(R.string.title)+": "+file_name);
		play.setImageResource(R.drawable.au_play);
		file_number.setText(index + "/" + filePathArray.size());
		
	    if(Divx != null)
	    {
	    	//resume play initial file just one time
	    	if(Divx.resume_index < 0)
	    		resume_index = -1;
	    	Divx = null;
	    }
		Divx = new DivxParser(VideoPlayerActivity.this);
		
		isforward = false;
		forward_press = 1;
		isrewind = false;
		rewind_press = 1;
		isStartPlay = true;
		isAudio_Error = false;
		querySubtitleCount = 0;
    }
    
    private void skipUnsupportFile(int index)
    {
    	filePathArray.remove(index);	
    	fileTitleArray.remove(index);
    	
    	isforward = false;
		forward_press = 1;
		isrewind = false;
		rewind_press = 1;
		
		isAudio_Error = false;
		
		if(task_hide_controler != null)
		{
			task_hide_controler.cancel();
			task_hide_controler = null;
		}
		
		if(task_hide_button != null)
		{
			task_hide_button.cancel();
			task_hide_button = null;
		}
		
		if(filePathArray.size() <= 0)
		{	
			this.finish();
			return;
		}
		
		switch(repeat_mode)
    	{
    	case REPEAT_SINGLE:
    		if(task_getCurrentPositon != null)
			{
				task_getCurrentPositon.cancel();
				task_getCurrentPositon = null;
			}
			
			mPlayer.stop();
	    	handler.sendEmptyMessage(HandlerControlerVariable.MEDIAPLAY_INIT);
    		break;
		case REPEAT_OFF:
			if(isSwitchToNextFile)
    		{
	     		if(currIndex > filePathArray.size()-1)
	     			currIndex = filePathArray.size()-1;
	     		else
	     		{
	     			if(task_getCurrentPositon != null)
	    			{
	    				task_getCurrentPositon.cancel();
	    				task_getCurrentPositon = null;
	    			}
	    			
	    			mPlayer.stop();
	    	    	handler.sendEmptyMessage(HandlerControlerVariable.MEDIAPLAY_INIT);
	     		}
	     			
    		}else
    		{
    			if(mPlayer.getCurrentPosition() <= 2000)
    			{
            		if(currIndex < 0)
            			currIndex = 0;
            		else
            		{
            			if(task_getCurrentPositon != null)
    	    			{
    	    				task_getCurrentPositon.cancel();
    	    				task_getCurrentPositon = null;
    	    			}
    	    			
    	    			mPlayer.stop();
    	    	    	handler.sendEmptyMessage(HandlerControlerVariable.MEDIAPLAY_INIT);
            		}
    			}else
    			{
    				if(task_getCurrentPositon != null)
	    			{
	    				task_getCurrentPositon.cancel();
	    				task_getCurrentPositon = null;
	    			}
	    			
	    			mPlayer.stop();
	    	    	handler.sendEmptyMessage(HandlerControlerVariable.MEDIAPLAY_INIT);
    			}
    		}
			
			break;
		case REPEAT_ALL:
			if(isSwitchToNextFile)
    		{
	     		if(currIndex > filePathArray.size()-1)
	     			currIndex = 0;
    		}else
    		{
    			if(mPlayer.getCurrentPosition() <= 2000)
    			{
            		if(currIndex < 0)
            			currIndex = filePathArray.size()-1;
    			}
    		}
			
			if(task_getCurrentPositon != null)
			{
				task_getCurrentPositon.cancel();
				task_getCurrentPositon = null;
			}
			
			mPlayer.stop();
	    	handler.sendEmptyMessage(HandlerControlerVariable.MEDIAPLAY_INIT);
			
			break;
    	}
    
    }
    
    private void setPicSize()
    {
    	switch(selected_idx)
    	{
    	case 0:
    		mTVService.setAspectRatio(TvManager.SCALER_RATIO_PANORAMA);
    		spinner_bar_value.setText(R.string.picture_super_live);
    		selected_idx++;
    		break;
    	case 1:
    		mTVService.setAspectRatio(TvManager.SCALER_RATIO_POINTTOPOINT);
    		spinner_bar_value.setText(R.string.picture_dot_by_dot);
    		selected_idx++;
    		break;
    	case 2:
    		mTVService.setAspectRatio(TvManager.SCALER_RATIO_BBY_AUTO);
    		spinner_bar_value.setText(R.string.picture_normal);
    		selected_idx++;
    		break;
    	case 3:
    		mTVService.setAspectRatio(TvManager.SCALER_RATIO_BBY_ZOOM);
    		spinner_bar_value.setText(R.string.picture_zoom);
    		selected_idx = 0;
    		break;
    	default:
    		break;
    	}
    }
    
    private void setQuickMenuItem(int position, boolean isNext)
	{
    	switch(position)
		{
			case SET_PICTURE:
			{
				ComponentName componetName = new ComponentName("com.tsb.tv","com.tsb.tv.LiteActivity");
			    Intent intent = new Intent();
			    intent.setComponent(componetName);
			    //Bundle bundle = new Bundle();
			    //bundle.putInt("TVMainMenu", 4);
			    //bundle.putBoolean("FORMAT_PROGRESSIVE", isProgressiveMovie());
			    intent.putExtra("setting", "picture");
			    isNeedExecuteOnPause = false;
				startActivity(intent);
				quickMenu.dismiss(); 
				break;
			}
			case SET_SOUND:
			{
				ComponentName componetName = new ComponentName("com.tsb.tv","com.tsb.tv.LiteActivity");
			    Intent intent = new Intent();
			    intent.setComponent(componetName);
			    /*Bundle bundle = new Bundle();
			    bundle.putInt("TVMainMenu", 5);
			    bundle.putBoolean("Audio_DRC", isDolby());
			    intent.putExtras(bundle);*/
			    intent.putExtra("setting", "sound");
			    isNeedExecuteOnPause = false;
				startActivity(intent);
				quickMenu.dismiss(); 
				break;
			}
			case SET_3D:
			{
				ComponentName componetName = new ComponentName("com.tsb.tv","com.tsb.tv.LiteActivity");
			    Intent intent = new Intent();
			    intent.setComponent(componetName);
			    intent.putExtra("setting", "3d");
			    isNeedExecuteOnPause = false;
				startActivity(intent);
				quickMenu.dismiss(); 
				break;
			}
			case SET_REPEAT_MODE:
			{
				if(isNext)
					repeatIndex++;
				else
				{	
					repeatIndex +=3;
					repeatIndex--;
				}
				
				repeatIndex %= 3;
				setRepeatIcon(repeatIndex);
				Editor editor = mPerferences.edit();
				editor.putInt("repeatIndex", repeatIndex);
				editor.commit();
				break;
			}
			case SET_SLEEP_TIMER:
			{	
				if(isNext)
				{
					if(mSleepTimeMin < 50)
					{
    					if(0 == mSleepTimeMin && 12 == mSleepTimeHour)
    					{
    						mSleepTimeHour = 0;
    					}
    					else
    					{
    						mSleepTimeMin = (mSleepTimeMin / 10+1)*10;
    					}
					}
					else
					{
						mSleepTimeMin = 0;
						mSleepTimeHour++;
					}
				}else
				{
					if(0 == mSleepTimeMin)
    				{
    					if(0 == mSleepTimeHour)
    					{
    						mSleepTimeHour = 12;
    					}
    					else
    					{
    						mSleepTimeHour --;
    						mSleepTimeMin =50;
    					}
    				}
    				else
    				{
    					mSleepTimeMin =( (mSleepTimeMin-1) / 10)*10 ;
    				}
				}
				
				SimpleDateFormat df_ori = new SimpleDateFormat("HH mm");
        		SimpleDateFormat df_des = new SimpleDateFormat("HH:mm");
        	    java.util.Date date_parse = null;
        	     try {
        	    	 date_parse = df_ori.parse(mSleepTimeHour+" "+mSleepTimeMin);    	
        		} catch (ParseException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        
            	String timeFormat = df_des.format(date_parse);
				
				TextView OptionText = (TextView)quickMenu.getListView().getChildAt(position).findViewById(R.id.menu_option);
				OptionText.setText(timeFormat);
            	
				Intent itent = new Intent();
				Bundle bundle = new Bundle();
				if(0 == mSleepTimeHour && 0 == mSleepTimeMin)
				{
					bundle.remove("SetTSBSleepHour");
					bundle.remove("SetTSBSleepMinute");
					bundle.putString("TSBTimerState","Cancel");
					bundle.putString("CallingTSBTimer", "MediaBrowserCallingTimer");
				}
				else
				{
					bundle.putInt("SetTSBSleepHour",mSleepTimeHour);
					bundle.putInt("SetTSBSleepMinute",mSleepTimeMin);
					bundle.putString("TSBTimerState","Set");		//Set=1, Cancel=0
					bundle.putString("CallingTSBTimer", "MediaBrowserCallingTimer");
				}
				itent.setAction("com.rtk.TSBTimerSettingMESSAGE");
				itent.putExtras((Bundle)bundle);
				sendBroadcast(itent);
				
				break;
			}
			case SET_TV_APP:
			{
				if(task_hide_quickmenu !=  null)
				{
					task_hide_quickmenu.cancel();
					task_hide_quickmenu = null;
				}
				ComponentName componetName = new ComponentName("com.tsb.tv", "com.tsb.tv.Tv_strategy");
				Intent intent= new Intent();
				intent.setComponent(componetName);
				startActivity(intent);
				break;
			}
			case SET_SYS_SETTING:
			{
				if(task_hide_quickmenu !=  null)
				{
					task_hide_quickmenu.cancel();
					task_hide_quickmenu = null;
				}
				ComponentName componetName = new ComponentName("com.android.settings", "com.android.settings.Settings");
				Intent intent= new Intent();
				intent.setComponent(componetName);
				startActivity(intent);
				break;
			}
			default:
				break;
		}
	}
    
    public class QuickMenuVideoAdapter extends BaseAdapter
	{
		
		class ViewHolder {
			TextView menu_name;
			ImageView left;
			TextView menu_option;
			ImageView right;
		}
		
		int[] menu_name = new int[] {
				R.string.quick_menu_picture,
				R.string.quick_menu_sound,
				R.string.quick_menu_3d_settings,
				R.string.quick_menu_repeat,
				R.string.quick_menu_sleep,
				R.string.quick_menu_tvapp,
				R.string.quick_menu_sysset
				};
		
		int[] visibility = new int[]{
	 			View.INVISIBLE,
	 			View.INVISIBLE,
	 			View.INVISIBLE,
	 			View.INVISIBLE,
	 			View.INVISIBLE,
	 			View.INVISIBLE,
	 			View.INVISIBLE,
	 	};
		
		private LayoutInflater mInflater;
		
		public QuickMenuVideoAdapter(Context context) {
			// TODO Auto-generated constructor stub
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return menu_name.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
        	if (convertView == null) {
        		holder = new ViewHolder();
        		if(true == MediaBrowserConfig.getRight2Left(getApplicationContext()))
        			convertView = mInflater.inflate(R.layout.quick_list_row_a, null);
                else
                	convertView = mInflater.inflate(R.layout.quick_list_row, null);
	        	holder.menu_name = (TextView)convertView.findViewById(R.id.menu_name);
	        	Typeface type= Typeface.createFromFile("/system/fonts/FAUNSGLOBAL3_F_r2.TTF");
        		holder.menu_name.setTypeface(type);
	        	holder.menu_option = (TextView)convertView.findViewById(R.id.menu_option);
	        	holder.left = (ImageView)convertView.findViewById(R.id.left_arrow);
        		holder.right = (ImageView)convertView.findViewById(R.id.right_arrow);
	        	convertView.setTag(holder);
        	} 
        	else 
        	{
        		holder = (ViewHolder)convertView.getTag();
        	}
        	
            holder.menu_name.setText(menu_name[position]);
            
            switch(position)
            {
            case SET_REPEAT_MODE:
            	switch(repeatIndex)
            	{
            	case 0:
            		holder.menu_option.setText(R.string.qm_repeat_off);
            		break;
            	case 1:
            		holder.menu_option.setText(R.string.repeat_all);
            		break;
            	case 2:
            		holder.menu_option.setText(R.string.repeat_one);
            		break;
            	default:
            		break;
            	}
            	break;
            case SET_SLEEP_TIMER:
            	SimpleDateFormat df_ori = new SimpleDateFormat("HH mm");
        		SimpleDateFormat df_des = new SimpleDateFormat("HH:mm");
        	    java.util.Date date_parse = null;
        	     try {
        	    	 date_parse = df_ori.parse(mSleepTimeHour+" "+mSleepTimeMin);    	
        		} catch (ParseException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        
            	String timeFormat = df_des.format(date_parse);
            	holder.menu_option.setText(timeFormat);
            	break;
            case SET_PICTURE:
            case SET_SOUND:
            case SET_3D:
            case SET_TV_APP:
            case SET_SYS_SETTING:
            	holder.menu_option.setText("");
            	break;
            default:
            	break;
            }
            
            holder.left.setVisibility(visibility[position]);
            holder.right.setVisibility(visibility[position]);
      
            return convertView;
		}

		public void setVisibility(int position, int inVisible) {
			// TODO Auto-generated method stub
			visibility[position]=inVisible;	
		}
	}
	
	private ServiceReceiver receiver = new ServiceReceiver();
	
	public class ServiceReceiver extends BroadcastReceiver{
	    @Override
	    public void onReceive(Context context, Intent intent) {
	      if(!isAnywhere)  
	    	  return;
	        if(intent.getStringExtra("action").equals("BACK") ||
	        		intent.getStringExtra("action").equals("FINISH"))
	        {
	    		if (receiver != null) {
	    			mUsbCtrl.UnRegesterBroadcastReceiver();
	    			unregisterReceiver(receiver);
	    			receiver = null;
	    		}
	        	finish();
	        }
	        if(intent.getStringExtra("action").equals("PAUSE"))
	        {
	        	pauseOne();
	        }
	        if(intent.getStringExtra("action").equals("STOP"))
	        {
	        	stop(); 
	        }
	        if(intent.getStringExtra("action").equals("PLAY"))
	        {
	        	play();
	        }
	    }
	}

	private boolean showInBackground = false;
	private BroadcastReceiver OnEngMenupRecever = new BroadcastReceiver(){

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals("com.realtek.TVShowInBackGround")){
				showInBackground = true;
			}
		}
		
	};
	
	public void pauseOne() {
		if(mPlayer.isPlaying())
		{
			mPlayer.pause();
			play.setImageResource(R.drawable.au_pause);
			handler.removeMessages(HandlerControlerVariable.PROGRESS_CHANGED);
		}
	}
	
	public void stop() {
		if(!isMediaplayIdle)
		{
			mPlayer.stop();
			mPlayer.setOnPreparedListener(null);
        	mPlayer.setOnCompletionListener(null);
        	mPlayer.setOnInfoListener(null);
			mPlayer.setDisplay(null);
			mPlayer.reset();
			isMediaplayIdle = true;
		}
	}
	
	public void play(){
		if(!isMediaplayIdle && !mPlayer.isPlaying())
		{
			mPlayer.start();
			play.setImageResource(R.drawable.au_play);
		}else if (mPlayer == null)
		{
			media_play_init(filePathArray.get(currIndex), fileTitleArray.get(currIndex));
		}
	}
	
	private void initUsbCtl(){
		initReceiver();
		mUsbCtrl= new UsbController(this); 
    	
        OnUsbCheckListener usbCheckListener = new OnUsbCheckListener(){

			@SuppressWarnings("static-access")
			@Override
			public void OnUsbCheck() {
				// TODO Auto-generated method stub
				if(isAnywhere || mBrowserType != 0)
				{
					return ; //do nothing on dmrMode
				}
				switch (mUsbCtrl.getDirection())  
                {  
            	case UsbController.PLUG_OUT:
            	{   
            		if(mUsbCtrl.GetUsbNum()== 0)//deviceNum is 0
              	  	{
            			setResult(UsbRemoved);
            			finish();
            			return ;
              	  	}
            		else
              		{
            			File file=new File(filePathArray.get(0));
            			if(file == null || !file.exists())
            			{
            				setResult(UsbRemoved);
            				finish();
            				return;
            			}
              		}
            	}
            	default:
            		break;
               }
			}

			@Override
			public void OnUsbCheck(String path,int direction) {
				
				if(isAnywhere || mBrowserType != 0)
				{
					return ; //do nothing on dmrMode
				}
				String mpath = "/storage/udisk"+devicePath;
				Log.e(TAG, mpath+"0");
				if(mpath.equals(path))
				{
					Log.e(TAG, mpath);
					switch (mUsbCtrl.getDirection())  
	                {  
	            	case UsbController.PLUG_OUT:
	            	{   
        				setResult(UsbRemoved);
        				finish();
	            	}
	            	default:
	            		break;
	               }
				}
			}
        };
        mUsbCtrl.setOnUsbCheckListener(usbCheckListener);
	}
	
	public boolean isHasVideo()
	{
		/*byte[] inputArray = new byte[]{0,0,0,0};
		byte[] outputArray = mPlayer.execSetGetNavProperty(DivxParser.NAVPROP_INPUT_GET_VIDEO_STATUS, inputArray);
		if(outputArray != null)
		{
			int has = outputArray[0] | ((outputArray[1] & 0xFF) << 8) | ((outputArray[2] & 0xFF) << 16) | ((outputArray[3] & 0xFF) << 24);
			Log.e(TAG, "###has = "+has);
			if(has > 0)
				return true;
			else
				return false;
		}
		return true;*/
		if(mPlayer != null)
		{
			int count = 0;
			TrackInfo[] track = mPlayer.getTrackInfo();
			int len = track.length;
			for(int i = 0; i < len; i++)
			{
				if(track[i].getTrackType() ==TrackInfo.MEDIA_TRACK_TYPE_VIDEO)
				{
					Log.v(TAG, "###has video");
					return true;
				}
				count++;
			}
			if(count == len)
				return false;
		}
		return false;
	}
	
	public int isDivxVODFile()
	{
		byte[] inputArray = new byte[]{0,0,0,0};
		byte[] outputArray = mPlayer.execSetGetNavProperty(DivxParser.NAVPROP_DIVX_VOD_QUERY, inputArray);
		if(outputArray != null)
		{
			int isVOD = outputArray[0] | ((outputArray[1] & 0xFF) << 8) | ((outputArray[2] & 0xFF) << 16) | ((outputArray[3] & 0xFF) << 24);
			Log.v(TAG, "VOD: " + isVOD);
			
			if (isVOD == 1)
				return 1;
			else
				return 0;
		}
		return 0;
	}
	
	public boolean isProgressiveMovie()
	{
		Parcel p = mPlayer.getParcelParameter(1800);
		boolean is_prog = false;
		if(p.readInt() == 1)
			is_prog = true;
		else
			is_prog = false;
		
		return is_prog;
	}
	
	public int isHasTrickPlayTrack()
	{
		byte[] inputArray = new byte[]{0,0,0,0};
		byte[] outputArray = mPlayer.execSetGetNavProperty(DivxParser.NAVPROP_DIVX_TPT_QUERY, inputArray);
		if(outputArray != null)
		{
			int is_tpt = outputArray[0] | ((outputArray[1] & 0xFF) << 8) | ((outputArray[2] & 0xFF) << 16) | ((outputArray[3] & 0xFF) << 24);
			Log.v(TAG, "VOD: " + is_tpt);
			
			if (is_tpt == 1)
				return 1;
			else
				return 0;
		}
		return 0;
	}
	
	public void SetDolby()
	{
		int[] currAudioInfo = mPlayer.getAudioTrackInfo(curr_audio_stream_num);
			String mAudioType = Utility.AUDIO_TYPE_TABLE(currAudioInfo[3]);
		if(mAudioType.compareTo("Dolby AC3") == 0)
		{
			dolby.setVisibility(View.VISIBLE);
			dolby.setImageResource(R.drawable.dolby);
			hide_dolby_delay();
		}
		else if(mAudioType.compareTo("Dolby Digital Plus") == 0)
		{
			dolby.setVisibility(View.VISIBLE);
			dolby.setImageResource(R.drawable.dolby_plus);
			hide_dolby_delay();
		}
		else
		{
			dolby.setVisibility(View.INVISIBLE);
		}
	}
	
	public boolean isDolby()
	{
		int[] currAudioInfo = mPlayer.getAudioTrackInfo(curr_audio_stream_num);
		String mAudioType = Utility.AUDIO_TYPE_TABLE(currAudioInfo[3]);
		if(mAudioType.compareTo("Dolby AC3") == 0)
		{
			return true;
		}
		else if(mAudioType.compareTo("Dolby Digital Plus") == 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private void PopupErrorMessageShow(String msg, int dismiss_time)
	{
		msg_hint.setMessage(msg);
		if(isRight2Left)
			msg_hint.setMessageRight();
		else
			msg_hint.setMessageLeft();
		msg_hint.show();
		
		if(task_message_time_out != null)
		{
			task_message_time_out.cancel();
			task_message_time_out = null;
		}
		
		task_message_time_out = new TimerTask(){

			@Override
			public void run() {
				handler.sendEmptyMessage(HandlerControlerVariable.HIDE_POPUP_MESSAGE);
				VideoPlayerActivity.this.finish();
			}
			
		};
		
		if(timer != null)
			timer.schedule(task_message_time_out, dismiss_time);
	}
	
	private void PopupMessageShow(String msg, int resid, int height, int width, int gravity, int x, int y, int dismiss_time)
	{
		msg_hint.setMessage(msg);
		msg_hint.setMessageCenterHorizotal();
		msg_hint.show(resid, height, width, gravity, x, y);
		
		if(task_message_time_out != null)
		{
			task_message_time_out.cancel();
			task_message_time_out = null;
		}
		
		task_message_time_out = new TimerTask(){

			@Override
			public void run() {
				handler.sendEmptyMessage(HandlerControlerVariable.HIDE_POPUP_MESSAGE);
			}
			
		};
		
		if(timer != null)
			timer.schedule(task_message_time_out, dismiss_time);
	}
	
	private void getInitTimer(){
		new Thread(new Runnable() {
			@Override
			public void run() {

				try {
					int mins = Settings.Global.getInt(getApplicationContext().getContentResolver(), "TotalMinute");
					mSleepTimeHour = mins / 60;
					mSleepTimeMin = mins % 60;
				} catch (SettingNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	private void querySubtitleInfo6Times() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (querySubtitleCount < 10) {
					querySubtitleCount++;
					try {
						Thread.sleep(10000);
						getSubtitleInfo();
					} catch(IllegalStateException e){
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				querySubtitleCount = 0;
			}
			
		}).start();	
	}
	
	private BroadcastReceiver mPlayReceiver = null;
	private IntentFilter mIntentFilter = null;
	private void initReceiver(){
		mPlayReceiver = new BroadcastReceiver()
	    {
	        public void onReceive(Context context, Intent intent)
	        {
	        	finish();
	        }
	    };
	    mIntentFilter = new IntentFilter();
		mIntentFilter.addAction("com.rtk.mediabrowser.broadcast");
        registerReceiver(mPlayReceiver, mIntentFilter);
	}

}
