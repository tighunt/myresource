package com.realtek.dmr;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.android.server.RtkDMRService;
import com.realtek.dmr.util.ConfirmMessage;
import com.realtek.dmr.util.HandlerControlerVariable;
import com.realtek.dmr.util.HandlerControlerVariable.PlaybackStatus;
import com.realtek.dmr.util.PopupMessage;
import com.realtek.dmr.util.TimerDelay;
import com.realtek.sync.CommonSemaphore;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.Metadata;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.media.MediaPlayer.OnCompletionListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.app.TvManager;
import android.app.AlertDialog.Builder;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


@SuppressLint("ShowToast")
public class DmrVideoPlayerActivity extends Activity {
	private final static String TAG = "DmrVideoPlayerActivity";
	private final static String tag = "DmrVideoPlayerActivityTestTag";
	private String[] filePathArray = null;
	public ArrayList<String> fileTitleArray = new ArrayList<String>();
	public ArrayList<String> DMSName = new ArrayList<String>();
	private String serverName = null;
	private boolean receiveInvalidProtol = true;
	
	public int currIndex = 0;
	public int len = 0;
	
	/******** control parameter initial *******/
	private final static int MOVIE_BANNER = 1;
	private final static int SUBTILE_LIST = 3;
	private final static int AUDIO_BANNER = 4;
	private final static int WANNING_MESSAGE = 5;
	private final static int TITLE_BANNER = 6;
	
	private final static int REPEAT_SINGLE = 1;
	private final static int REPEAT_ALL = 2;
	private final static int REPEAT_OFF = 3;
	private int repeat_mode = REPEAT_ALL;

	private final static int AUDIO_DIGITAL_RAW = 0;
	private final static int AUDIO_DIGITAL_LPCM_DUAL_CH = 1;
	private int AUDIO_DIGITAL_OUTPUT_MODE = 0;
	
	public static final int	NAVPROP_INPUT_GET_PLAYBACK_STATUS = 10;
	public static final int NAVPROP_INPUT_GET_NAV_STATE = 11;
	public static final int NAVPROP_INPUT_SET_NAV_STATE = 12;
	/***********flag setting **************************/
	private int rewind_press = 1;
	private int forward_press = 1;
	private boolean isrewind = false;
	private boolean isforward = false;
	
	private boolean isSwitchToNextFile = false;
	
	private boolean isAudio_Error = false;
	private boolean isVideoPlayCompleteInRepeatOFFMode = false;
	private boolean isVideoPlayerActivityOnPause = true;
	private boolean isOnPause = false;
	private boolean hasBeenStoped = false;
	
	private static int ResultCodeFinishVideoBrowser = 1;
	
	private boolean isShowChapterMetaData = false;
	
	private int selected_idx = 0;
	/********   android component parameter initial *******/
	private SurfaceView sView = null;
	protected MediaPlayer mPlayer = null;
	
	private ProgressBar loadingIcon = null;
	private TextView dolby = null;
	private ImageButton play = null;
	private ImageButton slowforward = null;
	private ImageButton slowbackward = null;
	private ImageButton fastforward = null;
	private ImageButton fastbackward = null;
	private ImageButton pictureSize = null;
	private ImageView menu = null;
	private SeekBar time_bar = null;
	DealWithSeek dealWithSeek = null;
	private TextView hasPlay = null;
	private TextView duration = null;
	private ImageView playStatus = null;
	private View mainLayout = null;
	
	private TextView common_ui = null;
	
	private View MovieBannerView = null;

		
	public TextView spinner_bar_value = null;
	
	public Metadata metadata = null;
	public TvManager mTVService = null;
	public Toast toast = null;
	/************Animation********************/
	private float banner_h = 0.0f;
    private final long bannerAnimTime = 200;
	/******** Handler  parameter initial *******/
	public Handler handler = null;
	
	private static int Minute = 0;
    private static int Hour = 0;
    private static int Second = 0;
    
	/******** Timer and TimerTask***************/
    private Timer timer = null;
	
	private TimerTask task_getCurrentPositon = null;
	private TimerTask task_hide_controler = null;
	private TimerTask task_meta_data_show = null;
	private TimerTask task_delay_task = null;
	private TimerTask task_audio_spdif_output_show = null;
	private TimerTask task_picture_size_show = null;
	private TimerTask task_message_time_out = null;
	
	private TimerTask task_updateTime = null;
	private int fakeCurrentTime1 = -1;
	private Runnable fakeRunnable1 = null;
	private TimerTask task_waitBannerToHide = null;
	
	/******** DivxParser *****************/
	public String ButtonNum = "";
	public int mClick_DPAD_DOWN_NUM = 0;
	/************ConfirmMessage*****************/
	private ConfirmMessage long_msg = null;
	private ConfirmMessage short_msg = null;
	
	private PopupMessage msg_hint = null;
	/************MediaApplication***************/
	private MediaApplication map = null;
	private Resources resourceMgr = null;
	
	/************Intent*****************/
	public boolean isAnywhere;
	
	
	
	//***********Action Defined****************************
	
	private final String PLAY_ACTION = "com.realtek.rtkvideoplayer.PLAY_ACTION";	//DMC
	private final String PAUSE_ACTION = "com.android.DMRService.pause";	//DMC
	private final String SLOWFORWARD_ACTION = "com.realtek.rtkvideoplayer.SLOWFORWARD_ACTION";	//DMC
	private final String SLOWBACKWARD_ACTION = "com.realtek.rtkvideoplayer.SLOWBACKWARD_ACTION";	//DMC
	private final String FASTFORWARD_ACTION = "com.realtek.rtkvideoplayer.FASTFORWARD_ACTION";	//DMC
	private final String FASTBACKWARD_ACTION = "com.realtek.rtkvideoplayer.FASTBACKWARD_ACTION";	//DMC
	private final String SEEK_ACTION = "com.rtk.dmr.seek.broadcast";	//DMC
	private final String WANTPLAY_ACTION = "com.android.DMRService.toplay";
	private final String STOP_ACTION = "com.android.DMRService.stop";
	private final String SETRATE = "com.rtk.dmr.setrate.broadcast";
	
	private final String TO_SERVICE_NOTIFY_ACTION = "com.rtk.dmr.notifyup.broadcast";
	//************MediaPlayer status******************
	// not able to be together
	private boolean isMediaplayIdle = true;
	private boolean isMediaPlayerPrepared = false;
	private boolean isMediaPlayerCompleted = false;
	private boolean isMediaInitialized = false;
	private boolean isMediaPlayerStarted = false;
	private boolean isMediaPlayerPaused = false;
	private boolean isMediaPlayerStopped = false;
	private boolean isMediaPlayerError = false;
	
	private boolean isOnNotNormalPlay = false;
	
	private final String SETSTATUS_IDLE = "SETSTATUS_IDLE";
	private final String SETSTATUS_PREPARED = "SETSTATUS_PREPARED";
	private final String SETSTATUS_COMPLETED = "SETSTATUS_COMPLETED";
	private final String SETSTATUS_INITIALIZED = "SETSTATUS_INITIALIZED";
	private final String SETSTATUS_STATED = "SETSTATUS_STATED";
	private final String SETSTATUS_PAUSED = "SETSTATUS_PAUSED";
	private final String SETSTATUS_STOPPED = "SETSTATUS_STOPPED";
	private final String SETSTATUS_ERROR = "SETSTATUS_ERROR";
	
	
	
	//private boolean isOnPlay = false;
	
	private boolean needSeek = false;
	private boolean isSeeking = false;
	
	private boolean isSeekFromCommand = false;
	
	private boolean isStillSeeking = false;
	private long lastSeekComplete = 0;
	private long save_lastSeekComplete = 0;
	
	
	private boolean isStopByDMR = false;
	//*********Action Defined End***************************
	AudioManager am = null;
	DMRVideoBroadcastReceiver bc_receiver = null;
	boolean isBannerAbleShown = true;
	boolean isBannerStillShown = false;
	//boolean isBannerForceShown = false;
	
	boolean isFromSavedInstance = false;
	boolean isFromRestart = false;
	
	//boolean isFirstStart = true;
	
	private int ffRate[] = {2, 8, 16, 32};	
	private int fwRate[] = {2, 8, 16, 32};	
	private int sfRate[] = {4, 16};	
	private int swRate[] = {4, 16};
	
	private int ffIndex = -1;
	private int fwIndex = -1;
	private int sfIndex = -1;
	private int swIndex = -1;
	private int ctr_direction_fw = 0; //0 means raw value, -1, means ctr left , 1 means ctr right
	private int ctr_direction_ff = 0; //0 means raw value, -1, means ctr left , 1 means ctr right
	//Set Subtitle info
	private int[] subtitleInfo = null;
	private int subtitle_num_Stream = 1;
	private int curr_subtitle_stream_num = 0;
	private int curr_textEncoding = 1000;
	private int curr_textColor = 0;
	private int curr_fontSize = 19;
	private int curr_SPU_ENABLE = 0;
	
	//Set Audio info
	private int[] audioInfo = null;
	private int audio_num_stream = 0;
	private int curr_audio_stream_num = 0;
	final String defaultAudioType = "UnKnown";
	String curr_audio_type = defaultAudioType;
	//MSG add to Handler
	private final int MSG_SHOWBANNER = 101;
	private final int MSG_HIDEBANNER = 102;
	private final int MSG_UPDATEPROGRESSBAR = 103;
	private final int MSG_GETDURATION = 104;
	private final int MSG_SET_PLAYANDPAUSE_ICONTOPLAY = 105;
	private final int MSG_SET_PLAYANDPAUSE_ICONTOPAUSE = 1051;
	private final int MSG_SET_PLAYANDPAUSE_FOCUS = 106;
	private final int MSG_SET_PLAYBACKSTATUS = 107;
	private final int MSG_SET_FASTFORWARD_FOCUS = 108;
	private final int MSG_SET_SLOWFORWARD_FOCUS = 109;
	private final int MSG_SET_FASTBACKWARD_FOCUS = 110;
	private final int MSG_SET_SLOWBACKWARD_FOCUS = 111;
	private final int MSG_HINT_NOTGETAUDIOFOCUS = 112;
	private final int MSG_SET_PICTURESIZE_FOCUS = 113;
	private final int MSG_HIDE_MENU = 114;
	private final int MSG_DISMISS_EXITMSG = 115;

	private final int MSG_STARTANIMATION = 116;
	private final int MSG_DISSMISSANIMATION = 117;

	private final int MSG_NOTIFY_DELAY = 1000;
	
	
	
	
	
	//allow interval between nowPos and seekPos
	private final int INTERVALFORNOWPOSANDSEEKPOS = 2000;	//ms
	
	private final int DELAY_UPDATETIME = 300;	//ms
	private final int DELAY_SEEK = 700;	//ms
	private final int DELAY_TOHIDEBANNER = 6000; //ms
	private final int DELAY_6000 = 6000;
	private final int DELAY_2000 = 2000;
	private final int TESTDELAY = 5000;
	
	private final int ORIENTATION_LANDSCAPE = 0;
	private final int ORIENTATION_PORTRAIT = 1;
	
	private int nowAudioFocus = AudioManager.AUDIOFOCUS_LOSS;	//start not have audio focus
	//long time1, time2;
	
	boolean stopNewIntent = false;
	
	
	QuickMenu quickMenu = null;
	private QuickMenuAdapter quickMenuAdapter = null;
	
	final int SUBTITLE_ID = 0;	//On means there has subtitle, off means there is no subtitle, and greyed
	final int AUDIOTRACK_ID = 1;	//No multi audio available , just greyed
	final int DETAILS_ID = 2;
	final int HELP_ID = 3;
	final int ITEMNUM = 4;
	final int MAXTYPE_COUNT = 2;
	final int FIRST_TYPE = 0;
	final int SECOND_TYPE = 1;
	
	int[] menu_name = new int[ITEMNUM];
	
	Runnable r_showMessageOnComplete = null;
	
	boolean loading = false;
	int playSpeed = 256;
	boolean needSendNotify = false;
	boolean isActivityReady = false;
        
long dateMiles = 0;
	String mediaDateStr = null;
	class QuickMenuAdapter extends BaseAdapter {
		private LayoutInflater layoutInflater = null;
		class ViewHolder {
			TextView menu_name;
			ImageView left;
			TextView menu_option;
			ImageView right;
		}
		
		public QuickMenuAdapter(Context mContext) {
			// TODO Auto-generated constructor stub
			layoutInflater = LayoutInflater.from(mContext);
			menu_name[SUBTITLE_ID] = R.string.quick_menu_subtitle;
			menu_name[AUDIOTRACK_ID] = R.string.quick_menu_audio_track;
			menu_name[DETAILS_ID] = R.string.quick_menu_detail;
			menu_name[HELP_ID] = R.string.quick_menu_help;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return menu_name.length;
		}

		@Override
		public Object getItem(int position) {	//not used
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		
		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return MAXTYPE_COUNT;
		}
		
		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			return position == 2? FIRST_TYPE : SECOND_TYPE;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder = null;
			TextView fileNameDataView = null;
			TextView dateDataView = null;
			TextView resolutionDataView = null;
			
			int currentType = getItemViewType(position);
			if(currentType == FIRST_TYPE) {
				convertView = layoutInflater.inflate(R.layout.video_detail, null);
				fileNameDataView = (TextView) convertView.findViewById(R.id.filename_data);
				dateDataView = (TextView)convertView.findViewById(R.id.date_data);
				resolutionDataView = (TextView)convertView.findViewById(R.id.resolution_data);
				//convertView.setBackgroundResource(R.drawable.gray_out);
				convertView.setEnabled(false);
			} else if(currentType == SECOND_TYPE) {
				if(convertView == null) {
					convertView = layoutInflater.inflate(R.layout.video_play_quickmenu_row, null);
					viewHolder = new ViewHolder();
					viewHolder.left = (ImageView) convertView.findViewById(R.id.left_arrow);
					viewHolder.right = (ImageView) convertView.findViewById(R.id.right_arrow);
					viewHolder.menu_name = (TextView)convertView.findViewById(R.id.menu_name);
//					Typeface type= Typeface.createFromFile("/system/fonts/FAUNSGLOBAL3_F_r2.TTF");
//					viewHolder.menu_name.setTypeface(type);
		        	viewHolder.menu_option = (TextView)convertView.findViewById(R.id.menu_option);
		        	convertView.setTag(viewHolder);
				} else {
					viewHolder = (ViewHolder)convertView.getTag();
				}
				viewHolder.menu_name.setText(menu_name[position]);
			}
			
			switch(position) {
				case SUBTITLE_ID: {
					//default subtitle off
					viewHolder.left.setVisibility(View.INVISIBLE);
					viewHolder.right.setVisibility(View.INVISIBLE);
					Log.v(tag, "when getView subtitle_num_Stream = " + subtitle_num_Stream + ", curr_SPU_ENABLE = " + curr_SPU_ENABLE + ", curr_subtitle_stream_num = " + curr_subtitle_stream_num);
					
					if(subtitle_num_Stream <= 0) {
						viewHolder.menu_option.setText("Subtitle Off");
						convertView.setBackgroundResource(R.drawable.gray_out);
						convertView.setEnabled(false);
					} else {
						convertView.setBackgroundResource(0);
						convertView.setEnabled(true);
						if(curr_SPU_ENABLE == 0) {
							viewHolder.menu_option.setText("Subtitle Off");
						}
						if(curr_SPU_ENABLE == 1) {
							viewHolder.menu_option.setText("Subtitle On " + curr_subtitle_stream_num);
						}
					}
				} break;
				case AUDIOTRACK_ID: {
					viewHolder.left.setVisibility(View.INVISIBLE);
					viewHolder.right.setVisibility(View.INVISIBLE);
					if(audio_num_stream < 1) {
						viewHolder.menu_option.setText(defaultAudioType);
						convertView.setBackgroundResource(R.drawable.gray_out);
						convertView.setEnabled(false);
					} else {
						viewHolder.menu_option.setText(curr_audio_type);
						convertView.setEnabled(true);
					}
				} break;
				case DETAILS_ID: {
					if(currIndex >= 0  && currIndex < filePathArray.length) {
						int titleStartIndex = filePathArray[currIndex].lastIndexOf("/");
						if(titleStartIndex == -1) {
							titleStartIndex = filePathArray[currIndex].lastIndexOf("\\");
						}
						String fileName = filePathArray[currIndex].substring(titleStartIndex + 1);
						fileNameDataView.setText(fileName);
					} else {
						fileNameDataView.setText("NotKnown");
					}
					//show date
					dateDataView.setText(mediaDateStr);
					//show date end
					//Resolution View
					int videoWidth = mPlayer.getVideoWidth();
					int videoHeight = mPlayer.getVideoHeight();
					if(videoWidth == 0 || videoHeight == 0) {
						resolutionDataView.setText("NotKnown");
					} else {
						resolutionDataView.setText("" + videoWidth +"" + (char) 10005 + "" + videoHeight);
					}
					
				} break;
				case HELP_ID: {
					// just do nothing will be OK
				} break;
				default : {
					break;
				}
					
			}
			return convertView;
		}
	}
	
	class SurfaceListener implements SurfaceHolder.Callback{
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {	
		}
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			if(mPlayer == null) {
				Log.e(tag, "SurfaceView Create quick than Surface");
				return ;
			}
			mPlayer.setDisplay(holder);
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			
		}
		
	}
	
    private OnErrorListener ErrorListener = new OnErrorListener(){

		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			//if error happens , first resetMediaPlayer;
			if(needSendNotify) {
				sendBroadCastNotifyUp();
			}
			
			resetMediaPlayer();

			handler.sendEmptyMessage(MSG_NOTIFY_DELAY);
			handler.sendEmptyMessage(MSG_DISSMISSANIMATION);
			
			switch (what)
			{
				case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
				{
					Log.e(TAG, "MediaServer died, finish mySelf!");
					
					resetMediaPlayer();
					/*
					long_msg.setMessage(DmrVideoPlayerActivity.this.getResources().
			    	getString(R.string.Media_Server_Die));
					long_msg.setButtonText(DmrVideoPlayerActivity.this.getResources().getString(R.string.msg_yes));
					long_msg.left.setVisibility(View.INVISIBLE);
					long_msg.right.setVisibility(View.INVISIBLE);
					long_msg.confirm_bt.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							long_msg.dismiss();
							Intent intent = new Intent(DmrVideoPlayerActivity.this, RTKDMP.class);
							startActivity(intent);
							DmrVideoPlayerActivity.this.finish();
						}		
					});
					long_msg.show();
					*/
				}
				break;
				case 1:
				{
					Log.e(TAG, "Video resolution not supported");
					resetMediaPlayer();
					
					switch(extra)
					{
					case 1:  //NAV_FAILURE_UNSUPPORTED_VIDEO_CODEC
						//PopupErrorMessageShow(DmrVideoPlayerActivity.this.getResources().getString(R.string.msg_unsupport), TimerDelay.delay_2s);
						break;
					case 0x100: //MEDIA_ERR_FRAME_SIZE
						//PopupErrorMessageShow(DmrVideoPlayerActivity.this.getResources().getString(R.string.FatalErrorCode_Video_Resolution), TimerDelay.delay_2s);
						break;
					case 0x101: //MEDIA_ERR_FRAME_RATE
						break;
					}	
				break;
				}
				default:
				{
					Log.e(TAG, "Get video error: " + what + ", ignore it!");
					resetMediaPlayer();
				}
				break;
			}
   
			return true;
		}
    	
    };
	
	private OnInfoListener VideoInfoListener = new OnInfoListener(){

		@Override
		public boolean onInfo(MediaPlayer mp, int what, int extra) {
			//MEDIA_INFO_BAD_INTERLEAVING = 800
			//MEDIA_INFO_NOT_SEEKABLE = 801
			
			Log.e(TAG, "VideoInfoListener" + "---what = "+what+"----");
			switch(what) {
				case 722: {	//MEDIA_INFO_FE_PB_RESET_SPEED
					Log.v(tag, "MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START");
					handler.removeCallbacks(r_showMessageOnComplete);
					ffIndex = fwIndex = sfIndex = swIndex = -1;
					ctr_direction_ff = ctr_direction_fw = 0;
					setMediaStatus(SETSTATUS_STATED);
					isOnNotNormalPlay = false;
					playStatus.setImageDrawable(resourceMgr.getDrawable(R.drawable.status_play));
					RtkDMRService.setfilePlayspeed("1");
				} break;
			}
			return true;
		}	
		
	};
	
	private OnPreparedListener videoPreparedListener = new OnPreparedListener(){

		@Override
		public void onPrepared(MediaPlayer mp) {
			Log.v(tag, "VideoPlayer onPrepared!");
			selected_idx = 3;
			setPicSize();	
			handler.sendEmptyMessage(MSG_DISSMISSANIMATION);
			ffIndex = fwIndex = sfIndex = swIndex = -1;
			ctr_direction_ff = ctr_direction_fw = 0;
			
			setMediaStatus(SETSTATUS_PREPARED);
			isOnNotNormalPlay = false;
			curr_SPU_ENABLE = 0;
			curr_subtitle_stream_num = 0;
			subtitle_num_Stream = 0;
			curr_audio_stream_num = -1;
			audio_num_stream = 0;
			
			Intent intent = new Intent();
			intent.setAction("MediaScannerSuspend");
			sendBroadcast(intent);
			
			if(checkAndRequestAF()) {
				getAudioTrackInfo();
				mPlayer.start();
				if(playSpeed != 256) {
					mPlayer.setSpeed(playSpeed);
					playSpeed = 256;
				}
				setMediaStatus(SETSTATUS_STATED);
				isOnNotNormalPlay = false;
				Log.v(tag, "start after onPrepared !");
				isStopByDMR = false;
				handler.removeCallbacks(r_showMessageOnComplete);
				handler.sendEmptyMessage(MSG_GETDURATION);
				handler.sendEmptyMessage(MSG_SET_PLAYANDPAUSE_ICONTOPAUSE);
				startTaskUpdateTime();
				//can get meta info about this media
				//getAudioTrackInfo();
				//getSubtitleInfo();
				return;
			}
		}
    };
    
    private OnCompletionListener videoCompletionListener = new OnCompletionListener()
    {
		@Override
		public void onCompletion(MediaPlayer mp) {
			Log.v(tag, "Video play on Completion!");
			Intent stopcmd = new Intent("com.rtk.dmr.stop.broadcast");
		    sendBroadcast(stopcmd);
		    
			setMediaStatus(SETSTATUS_COMPLETED);
			ffIndex = fwIndex = sfIndex = swIndex = -1;
			ctr_direction_ff = ctr_direction_fw = 0;
			isOnNotNormalPlay = false;
			
			curr_SPU_ENABLE = 0;
			curr_subtitle_stream_num = 0;
			subtitle_num_Stream = 0;
			curr_audio_stream_num = -1;
			audio_num_stream = 0;
			resetPlaybackResources();
			handler.removeCallbacks(r_showMessageOnComplete);
			handler.postDelayed(r_showMessageOnComplete, DELAY_6000);	// tell will exit 6s
        	return;
		}
	};
    
    private OnSeekCompleteListener videoSeekCompleteListener = new OnSeekCompleteListener() {
		
		@Override
		public void onSeekComplete(MediaPlayer mp) {
			// TODO Auto-generated method stub
			Log.v(tag, "onSeekComplete");
			lastSeekComplete = System.currentTimeMillis();
		}
	};
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.v(TAG, "VidePlayerActivity onCreate");

        super.onCreate(savedInstanceState);
        //add land and ori
        setContentView(R.layout.video_player_dmr);
        resourceMgr = this.getResources();
        map = (MediaApplication)getApplication();
        //mPlayer = map.getMediaPlayer();
		mTVService = (TvManager)getSystemService("tv");
		toast = new Toast(this);
        getAudioService();
        findViews();
        setListeners();
        init();
        reInit();
        
        initHandler();
        
        doRegisterReceiver();
        	isFromSavedInstance = false;
        	captureIntent();
        
	}
	
	@Override
	protected void onStart() {
    	Log.v(TAG, "VidePlayerActivity onStart");
		super.onStart();
	}
	
	@Override
	protected void onResume() {
		Log.v(TAG, "VidePlayerActivity onResume");
		//when onResume ,we wan't to play, so do play here
		isOnPause = false;
		if(timer == null)
			timer = new Timer();
		
		handler.sendEmptyMessage(MSG_SHOWBANNER);
		
	    super.onResume();
    }
	
	@Override
	protected void onPause() {
		Log.v(TAG, "VidePlayerActivity onPause");
		
		isOnPause = true;
		
		/*if(mPlayer != null) {
			try {
				mPlayer.stop();
				setMediaStatus(SETSTATUS_STOPPED);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/
		
		super.onPause(); 
    }
	
    @Override
	 public void onStop(){
    	Log.v(TAG, "VidePlayerActivity onStop");
    	if(needSendNotify) {
			sendBroadCastNotifyUp();
		}
    	if(loading) {
			dismissLoading();
		}
    	stopTaskUpdateTime();
		stopTaskWaitBannerToHide();
		if(timer != null)
		{
			timer.cancel();
			timer = null;
		}
		doAbandonAF();
    	
    	resetMediaPlayer();
		handler.removeCallbacks(fakeRunnable1);
    	if(msg_hint != null) {
    		msg_hint.dismiss();
    	}
		//DmrVideoPlayerActivity.this.finish();
		stopNewIntent = false;
		super.onStop();
    }
    
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		Log.v(TAG, "VidePlayerActivity onRestart");
		super.onRestart();
	}
    
    @Override
	protected void onDestroy() {
    	Log.v(TAG, "VidePlayerActivity onDestroy");
    	doUnRegisterReceiver();
    	releaseMediaPlayer();
    	System.gc();
    	Intent finishtoDMR = new Intent("com.rtk.dmr.finish.broadcast");
		sendBroadcast(finishtoDMR);
    	Log.v(TAG, "VideoPlayerActivity onDestroy end");
		super.onDestroy();
	}
    
    @Override
    protected void onNewIntent(Intent intent) {
    	Log.v(TAG, "VidePlayerActivity onNewIntent");
    	super.onNewIntent(intent);
    	hasBeenStoped = false;
    	currIndex = intent.getIntExtra("initPos", 0);
    	filePathArray = intent.getStringArrayExtra("filelist");
    	playSpeed = intent.getIntExtra("PLAYSPEED", 0);
    	isMediaPlayerCompleted = false;
    	//sendBroadCast to tell nower position 0;
    	receiveInvalidProtol = intent.getBooleanExtra("isSupport", true);
    	startFromIntent();
    	
    }
    boolean flag1 = false;
	@SuppressWarnings("static-access")
	@Override    
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i(TAG, "keyCode = "+keyCode);
		
		if(isOnPause)	//when activity onPause , not accept
			return true;
		
		switch (keyCode) {
		//for test
		case KeyEvent.KEYCODE_0:
			if(flag1) {
				this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
			} else {
				this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
			}
			flag1 = !flag1;
			break;
		//for test
		case KeyEvent.KEYCODE_BACK: {
			Log.v(tag, "KeyCode Back!!!!!!!!!!!!!!!!&&&&&&&&&&&&&&&&&&&&&");
			if(isMediaPlayerCompleted || isStopByDMR) {
				handler.removeCallbacks(r_showMessageOnComplete);
			}
			
			//msg_hint.dismiss();
			//disable Banner
			hideBanner();
			
			AlertDialog.Builder builder = new Builder(DmrVideoPlayerActivity.this);
			builder.setMessage(getResources().getString(R.string.note_exit));
			builder.setTitle(null);
			builder.setNegativeButton(R.string.note_ok, new DialogInterface.OnClickListener() {
				 @Override
				 public void onClick(DialogInterface dialog, int which) {
				    dialog.dismiss();
					finish();
				 }
			});
			builder.setPositiveButton(R.string.note_cancel, new DialogInterface.OnClickListener() {
				 @Override
				 public void onClick(DialogInterface dialog, int which) {
					if(isMediaPlayerCompleted || isStopByDMR) {
						handler.postDelayed(r_showMessageOnComplete, DELAY_6000);
					}
					dialog.dismiss();
				 }
			});
			
			builder.create().show();
			return true;
		} 
		case 165: //for L4300 KeyEvent.KEYCODE_INFO:
		case KeyEvent.KEYCODE_M:
		{
			if(MovieBannerView.getVisibility() == View.INVISIBLE) {
				Log.v(tag, "to showBanner");
				handler.sendEmptyMessage(MSG_SHOWBANNER);
			} else {
				Log.v(tag, "to hideBanner");
				handler.sendEmptyMessage(MSG_HIDEBANNER);
			}
		}
		return true;
		case 231:  //for L4300 KeyEvent.KEYCODE_STOP
		case 257:  //DISPLAY KEY in REALTECK RCU
		case KeyEvent.KEYCODE_K:   // As [Stop] key
		{
			//this.finish();
		}
		return true;
		case KeyEvent.KEYCODE_ESCAPE: //for L4300 KeyEvent.KEYCODE_ESCAPE
		case KeyEvent.KEYCODE_J:   // As [Exit] key
		{
			setResult(ResultCodeFinishVideoBrowser);
			//this.finish();
		}
		return true;
     	case 228: //for L4300 KeyEvent.KEYCODE_HOLD:
     	case 256: //VIDEO KEY  in realtek RCU
     	case KeyEvent.KEYCODE_R: //rewind
     	{
     		
     	}
     	return true;
     	case 229: //for L4300 KeyEvent.KEYCODE_ZOOM
     	case 255: //AUDIO KEY in realteck RCU
     	case KeyEvent.KEYCODE_F: //forward
     	{
     		
     	}
     	return true;
     	case 230:
     	case 254:	// KeyEvent.KEYCODE_EPG:
     	case KeyEvent.KEYCODE_S:
     	{
    		
     	}
     	return true;
     	case 220: //for L4300 KeyEvent.KEYCODE_SOUND_MODE
     	case KeyEvent.KEYCODE_PROG_BLUE:
     	case KeyEvent.KEYCODE_A:
     	{
    		
     	}
     	return true;
     	case 232: //for L4300 KeyEvent.KEYCODE_PLAY:
     	case KeyEvent.KEYCODE_PROG_GREEN:
     	case KeyEvent.KEYCODE_P:
     	{	
			
     	}
     	return true;
     	case KeyEvent.KEYCODE_C:
     	case 233: // for L4300 KeyEvent.KEYCODE_PAUSE:
     	{
     	}
     	return true;
     	case KeyEvent.KEYCODE_O:
     	{
     	
     	}
     	return true;
     	case 221: //for L4300 KeyEvent.KEYCODE_DISPLAY_MODE
     	{	
   
     		
 			setPicSize();
     		
     		/*if(task_picture_size_show != null)
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
     			timer.schedule(task_picture_size_show, TimerDelay.delay_5s);*/
     	}
     	return true;
     	default:
     		break;
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	
	 
	private void init()
	{
        if(quickMenuAdapter == null) {
        	quickMenuAdapter = new QuickMenuAdapter(this);
		}
        
		quickMenu = new QuickMenu(this, quickMenuAdapter, 868, LayoutParams.WRAP_CONTENT, "DMR_VIDEO");
		//quickMenu.setAnimationStyle(R.style.QuickAnimation);
quickMenu.AddOnItemClickListener(quickmenuItemClickListener);
		
		msg_hint = new PopupMessage(this);
		short_msg = new ConfirmMessage(this);
	}
	
	private void initHandler()
	{
		r_showMessageOnComplete = new Runnable() {
			public void run() {
				//disable Banner
				Log.v(tag, "Play already finish!");
				hideBanner();
				//disable Menu
				if(quickMenu.isShowing()) {
					handler.removeMessages(MSG_HIDE_MENU);
					quickMenu.dismiss();
				}
				//disable transparantDialog
				DmrVideoPlayerActivity.this.finish();

			}
		};
		 handler = new Handler() {	
	    	@Override
			public void handleMessage(Message msg) {
	    		switch(msg.what){
		    		case MSG_STARTANIMATION: {
						if(!isOnPause) {
							startLoading();
						}
					} break;
					case MSG_DISSMISSANIMATION: {
						if(!isOnPause) {
							dismissLoading();
						}
					} break;
					case MSG_DISMISS_EXITMSG : {
						msg_hint.dismiss();
					} break;
					case MSG_HIDE_MENU: {
						quickMenu.dismiss();
					} break;
		    		case MSG_SET_PICTURESIZE_FOCUS: {
		    			pictureSize.requestFocus();
		    		} break;
	    			case MSG_HINT_NOTGETAUDIOFOCUS : {
	    				Toast.makeText(DmrVideoPlayerActivity.this, "Can't get AudioFocus to start", Toast.LENGTH_SHORT).show();
	    			} break;
	        		case MSG_SET_PLAYBACKSTATUS: {
	        			switch(msg.arg1) {
	        				case PlaybackStatus.STATUS_PLAY:
								playStatus.setImageDrawable(getResources().getDrawable(R.drawable.status_play));
	        					break;
	        				case PlaybackStatus.STATUS_PAUSE:
								playStatus.setImageDrawable(getResources().getDrawable(R.drawable.status_pause));
	        					break;
	        				case PlaybackStatus.STATUS_FF1:
								playStatus.setImageDrawable(getResources().getDrawable(R.drawable.status_ff2));
	        					break;
	        				case PlaybackStatus.STATUS_FF2:
								playStatus.setImageDrawable(getResources().getDrawable(R.drawable.status_ff8));
	        					break;
	        				case PlaybackStatus.STATUS_FF3:
								playStatus.setImageDrawable(getResources().getDrawable(R.drawable.status_ff16));
	        					break;
	        				case PlaybackStatus.STATUS_FF4:
								playStatus.setImageDrawable(getResources().getDrawable(R.drawable.status_ff32));
	        					break;
	        				case PlaybackStatus.STATUS_FW1:
								playStatus.setImageDrawable(getResources().getDrawable(R.drawable.status_rew2));
	        					break;
	        				case PlaybackStatus.STATUS_FW2:
								playStatus.setImageDrawable(getResources().getDrawable(R.drawable.status_rew8));
	        					break;
	        				case PlaybackStatus.STATUS_FW3:
								playStatus.setImageDrawable(getResources().getDrawable(R.drawable.status_rew16));
	        					break;
	        				case PlaybackStatus.STATUS_FW4:
								playStatus.setImageDrawable(getResources().getDrawable(R.drawable.status_rew32));
	        					break;
	        				case PlaybackStatus.STATUS_SF1:
	        					playStatus.setImageDrawable(getResources().getDrawable(R.drawable.status_sff));
	        					break;
	        				case PlaybackStatus.STATUS_SF2:
	        					playStatus.setImageDrawable(getResources().getDrawable(R.drawable.status_sff));
	        					break;
	        				case PlaybackStatus.STATUS_SF3:
	        					playStatus.setImageDrawable(getResources().getDrawable(R.drawable.status_sff));
	        					break;
	        				case PlaybackStatus.STATUS_SF4:
	        					playStatus.setImageDrawable(getResources().getDrawable(R.drawable.status_sff));
	        					break;
	        				case PlaybackStatus.STATUS_SW1:
	        					playStatus.setImageDrawable(getResources().getDrawable(R.drawable.status_srew));
	        					break;
	        				case PlaybackStatus.STATUS_SW2:
	        					playStatus.setImageDrawable(getResources().getDrawable(R.drawable.status_srew));
	        					break;
	        				case PlaybackStatus.STATUS_SW3:
	        					playStatus.setImageDrawable(getResources().getDrawable(R.drawable.status_srew));
	        					break;
	        				case PlaybackStatus.STATUS_SW4:
	        					playStatus.setImageDrawable(getResources().getDrawable(R.drawable.status_srew));
	        					break;
	        				case PlaybackStatus.STATUS_NOTREADY:
								playStatus.setImageResource(R.color.blank);
	        					break;
	        			}
	        		} break;
	        		case MSG_GETDURATION : {
	        			get_duration();
	        		} break;
	        		case MSG_SET_PLAYANDPAUSE_ICONTOPAUSE : {
	        			play.setImageResource(R.drawable.v_gui_pause);
	        		} break;
	        		case MSG_SET_PLAYANDPAUSE_ICONTOPLAY: {
	        			play.setImageResource(R.drawable.v_gui_play);
	        		} break;
	        		case MSG_SET_PLAYANDPAUSE_FOCUS: {
	        			Log.v(tag, "play focus");
	        			play.requestFocus();
	        			Log.v(tag, "play focus end");
	        		} break;
	        		case MSG_SET_FASTFORWARD_FOCUS : {
	        			fastforward.requestFocus();
	        		} break;
	        		case MSG_SET_SLOWFORWARD_FOCUS : {
	        			slowforward.requestFocus();
	        		} break;
	        		case MSG_SET_FASTBACKWARD_FOCUS : {
	        			fastbackward.requestFocus();
	        		} break;
	        		case MSG_SET_SLOWBACKWARD_FOCUS : {
	        			slowbackward.requestFocus();
	        		} break;
	        		case MSG_UPDATEPROGRESSBAR: {
	        			if(isMediaPlayerPrepared || isMediaPlayerStarted || isMediaPlayerPaused || isMediaPlayerCompleted) {
	        				int i = msg.arg1;
	        				if(needSendNotify) {
	        					if(i > 0) {
	        						needSendNotify = false;
	        						sendBroadCastNotifyUp();
	        					}
	        				}
							dealWithSeek.setProgress(i);
	        				i /= 1000;
	        				int minute = i / 60;
	        				int hour = minute / 60;
	        				int second = i % 60;
	        				minute %= 60;
	        				hasPlay.setText(String.format("%02d:%02d:%02d", hour, minute, second));
	        			}
	        		} break;
	        		case MSG_SHOWBANNER:	//SHOW AND WAIT BANNER TO LEAVE
	        			showBanner();
	        			break;
	        		case MSG_HIDEBANNER:
	        			hideBanner();
	        			break;
	        		case MSG_NOTIFY_DELAY:
	        			break;
	        		default:
	        			break;
	        		}
	        		super.handleMessage(msg);
	        	}
	 		}; 
	}
	
	private void get_duration()
	{
		if(isMediaPlayerPrepared || isMediaPlayerCompleted || isMediaPlayerStarted || isMediaPlayerPaused )
		{
			Log.v(TAG, "get_duration");
			int max = mPlayer.getDuration();
			time_bar.setMax(max);
			max /= 1000;	
			Minute = max / 60;
			Hour = Minute / 60;
			Second = max % 60;
			Minute %= 60;
			
			hasPlay.setText("00:00:00");
			duration.setText(String.format("%02d:%02d:%02d", Hour, Minute, Second));
			dealWithSeek.setProgress(0);
		}
	}
	
	
	private void captureIntent()
    {
		Intent intent= getIntent();
    	currIndex = intent.getIntExtra("initPos", 0);
    	filePathArray = intent.getStringArrayExtra("filelist");
    	playSpeed = intent.getIntExtra("PLAYSPEED", 0);
    	receiveInvalidProtol = intent.getBooleanExtra("isSupport", true);
    	startFromIntent();
    }
    
    private void setAudioSpdifOutput(int mode)
    {
    	mTVService.setAudioSpdifOutput(mode);
    	
		//common_ui.setVisibility(View.VISIBLE);
    	
    	if(mode == AUDIO_DIGITAL_RAW)
    	{
    		//common_ui.setText(" "+ this.getResources().getString(R.string.raw)+ " ");
    	}else if(mode == AUDIO_DIGITAL_LPCM_DUAL_CH)
    	{	
    		//common_ui.setText(" "+this.getResources().getString(R.string.LPCM)+" ");
    	}
    	
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
	
	private void getSubtitleInfo()	//add by star_he
    {
		if(isMediaplayIdle || isMediaPlayerError || isMediaInitialized) {
			curr_subtitle_stream_num = 0;
			subtitle_num_Stream = 0;
			return ;
		}
		subtitleInfo = mPlayer.getSubtitleInfo();
		if(subtitleInfo == null) {
			curr_subtitle_stream_num = 0;
			subtitle_num_Stream = 0;
			return ;
		}
		
    	subtitle_num_Stream = subtitleInfo[1];
		curr_subtitle_stream_num = subtitleInfo[2];
    }
	
	private void setSubtitle()	//add by star_he
	{	
		if(isMediaplayIdle || isMediaPlayerError || isMediaInitialized) {
			return ;
		}
		if(curr_SPU_ENABLE == 0) {	// tell to set subtitle notEnable
			Log.v(tag, "curr_SP_ENABLE = 0");
			if(subtitle_num_Stream > 0) {
				curr_subtitle_stream_num = 1;
				mPlayer.setSubtitleInfo(curr_subtitle_stream_num, curr_SPU_ENABLE, curr_textEncoding, curr_textColor, curr_fontSize);
				curr_subtitle_stream_num = 0;
			}
			return ;
		}
			
		if(curr_SPU_ENABLE == 1) {
			Log.v(tag, "curr_SP_ENABLE = 1");
			if(subtitle_num_Stream <= 0) {
				curr_subtitle_stream_num = 0;
				return ;
			}
			if(subtitle_num_Stream > 0 && curr_subtitle_stream_num <= 0) {
				curr_subtitle_stream_num = 1;
				mPlayer.setSubtitleInfo(curr_subtitle_stream_num, curr_SPU_ENABLE, curr_textEncoding, curr_textColor, curr_fontSize);
				return ;
			}
 			
			if(subtitle_num_Stream > 0 && curr_subtitle_stream_num > subtitle_num_Stream) {
				curr_SPU_ENABLE = 0;
				curr_subtitle_stream_num = 1;
				mPlayer.setSubtitleInfo(curr_subtitle_stream_num, curr_SPU_ENABLE, curr_textEncoding, curr_textColor, curr_fontSize);
				curr_subtitle_stream_num = 0;
				return ;
			}
			mPlayer.setSubtitleInfo(curr_subtitle_stream_num, curr_SPU_ENABLE, curr_textEncoding, curr_textColor, curr_fontSize);
			return ;
 		}
	}
	
	private void getAudioTrackInfo()	//add by star_he
    {
		if(isMediaplayIdle || isMediaPlayerError || isMediaInitialized) {
			audio_num_stream = 0;
			curr_audio_stream_num = 0;
			curr_audio_type = defaultAudioType;
			return ;
		}
		audioInfo = mPlayer.getAudioTrackInfo(-1);
    	if(audioInfo == null) {
    		Log.e(tag, "get AudioTrack info error");
    		audio_num_stream = 0;
    		curr_audio_stream_num = 0;
    		return ;
		}
    	audio_num_stream = audioInfo[1];
    	curr_audio_stream_num = audioInfo[2];
    	curr_audio_type = Utility.AUDIO_TYPE_TABLE(audioInfo[3]);
    	
    	if(curr_audio_type.equals("Dolby AC3")) {
    		///Update UI
    		dolby.setText("Dolby AC3");
    	} else if(curr_audio_type.equals("Dolby Digital Plus")) {
    		///Update UI
    		dolby.setText("Dolby Digital Plus");
    	} else {
    		dolby.setText(null);
    	}
    }
	
	private void setAudioTrack()	//add by star_he
	{	
		if(isMediaplayIdle || isMediaPlayerError || isMediaInitialized) {
			return ;
		}
		if(audio_num_stream < 1) {
			curr_audio_stream_num = 0;	//Not OK indeed
			return ;
		}
		
		if(audio_num_stream >= 1 && curr_audio_stream_num == 0) {
			curr_audio_stream_num = 1;
			mPlayer.setAudioTrackInfo(curr_audio_stream_num);
			return ;
		}
			
		if(audio_num_stream >= 1 && curr_audio_stream_num > audio_num_stream) 
		{
			curr_audio_stream_num = 1;
			mPlayer.setAudioTrackInfo(curr_audio_stream_num);
 			return ;
 		}
		mPlayer.setAudioTrackInfo(curr_audio_stream_num);
	}
		
	
	public void VideoPlayerFinish() {
		this.finish();
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
	
    private void setPicSize()
    {
    	handler.sendEmptyMessage(MSG_SHOWBANNER);
		handler.sendEmptyMessage(MSG_SET_PICTURESIZE_FOCUS);
    	switch(selected_idx)
    	{
	    	case 0:
	    		mTVService.setAspectRatio(TvManager.SCALER_RATIO_PANORAMA);
	    		
	    		selected_idx++;
	    		break;
	    	case 1:
	    		mTVService.setAspectRatio(TvManager.SCALER_RATIO_BBY_ZOOM);
	    		selected_idx++;
	    		break;
	    	case 2:
	    		mTVService.setAspectRatio(TvManager.SCALER_RATIO_POINTTOPOINT);
				selected_idx++;
	    		break;
	    	case 3:
	    		mTVService.setAspectRatio(TvManager.SCALER_RATIO_BBY_AUTO);	
				selected_idx = 0;
	    		break;
	    	default:
	    		break;
    	}
    }
	
	private void PopupErrorMessageShow(String msg, int dismiss_time)
	{
		/*msg_hint.setMessage(msg);
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
				DmrVideoPlayerActivity.this.finish();
			}
			
		};
		
		if(timer != null)
			timer.schedule(task_message_time_out, dismiss_time);*/
	}
	
	private void PopupMessageShow(String msg, int resid, int height, int width, int gravity, int x, int y, int dismiss_time)
	{
		/*msg_hint.setMessage(msg);
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
			timer.schedule(task_message_time_out, dismiss_time);*/
	}
	
	public void PlayFile()
	{	
		
	}
	
	public byte[] get_nav_state(int propertyID, byte[] inputArray)
	{
		byte[] outputArray = mPlayer.execSetGetNavProperty(NAVPROP_INPUT_GET_NAV_STATE, inputArray);
		if(outputArray == null)
		{
			Log.e(TAG, "execSetGetNavProperty(NAVPROP_INPUT_GET_NAV_STATE) return null!");
		}
		
		return outputArray;
	}
	
	public void set_nav_state(int propertyID, byte[] inputArray)
	{
		if(inputArray != null)
			mPlayer.execSetGetNavProperty(NAVPROP_INPUT_SET_NAV_STATE, inputArray);
	}
	
	public void reInit() {
		
	}
	
	public void doRegisterReceiver() {
		bc_receiver = new DMRVideoBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(PLAY_ACTION);
		intentFilter.addAction(PAUSE_ACTION);
		intentFilter.addAction(SLOWFORWARD_ACTION);
		intentFilter.addAction(SLOWBACKWARD_ACTION);
		intentFilter.addAction(FASTFORWARD_ACTION);
		intentFilter.addAction(FASTBACKWARD_ACTION);
		intentFilter.addAction(SEEK_ACTION);
		intentFilter.addAction(WANTPLAY_ACTION);
		intentFilter.addAction(STOP_ACTION);
		intentFilter.addAction(SETRATE);
		registerReceiver(bc_receiver, intentFilter);
	}
	
	public void doUnRegisterReceiver(){
		unregisterReceiver(bc_receiver);
	}
	
	private class DMRVideoBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals(PLAY_ACTION)) {
				f_play();
			} else if(intent.getAction().equals(PAUSE_ACTION)) {
				boolean pause = intent.getBooleanExtra("pause",false);
				if(pause)
					f_pause();
				else
					f_play();
			} else if(intent.getAction().equals(SLOWFORWARD_ACTION)) {
				f_slowForward();
			} else if(intent.getAction().equals(SLOWBACKWARD_ACTION)) {
				f_slowBackward();
			} else if(intent.getAction().equals(FASTFORWARD_ACTION)) {
				f_fastForward();
			} else if(intent.getAction().equals(FASTBACKWARD_ACTION)) {
				f_fastBackward();
			} else if(intent.getAction().equals(SEEK_ACTION)) {
				int seekPos = intent.getIntExtra("Seekpos", -1);
				Log.v(tag, "Get seek broadcast seekPos = " + seekPos);
				seek_from_dmr(seekPos);	//Seek where
				Log.v(tag, "Leave seek broadcast seekPos = " + seekPos);
			} else if(intent.getAction().equals(WANTPLAY_ACTION)) {
				Bundle bundle = intent.getExtras();
				String cmd = bundle.getString("cmd");
				if(cmd.equals("Video")) {
					//do nothing, just like I'm start
					handler.removeCallbacks(r_showMessageOnComplete);
				} else {
					//stop onNewIntent
					stopNewIntent = true;
					handler.removeCallbacks(r_showMessageOnComplete);
					resetMediaPlayer();
    				DmrVideoPlayerActivity.this.finish();
    				stopNewIntent = false;
				}
			} else if(intent.getAction().equals(STOP_ACTION)) {
				if(!hasBeenStoped)				
				{
					hasBeenStoped = true;
				isStopByDMR = true;
				if(mPlayer != null) {
					try {
						mPlayer.stop();
						setMediaStatus(SETSTATUS_STOPPED);
						doAbandonAF();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				stopTaskUpdateTime();
				resetPlaybackResources();
				handler.removeCallbacks(r_showMessageOnComplete);
				handler.postDelayed(r_showMessageOnComplete, DELAY_6000);
				}
			} else if(intent.getAction().equals(SETRATE)){
				int rate = intent.getIntExtra("Rate", -1);
				try {
					Log.v(tag, "setSpeed happen ! speed = " + rate);
					mPlayer.setSpeed(rate);
					//what if success, update UI
					Message msg = handler.obtainMessage();
					msg.what = MSG_SET_PLAYBACKSTATUS;
					switch(rate) {
						case 256 : {
							msg.arg1 = PlaybackStatus.STATUS_PLAY;
						} break;
						case 256 * 2 : {
							msg.arg1 = PlaybackStatus.STATUS_FF1;
						} break;
						case 256 * 8 : {
							msg.arg1 = PlaybackStatus.STATUS_FF2;
						} break;
						case 256 * 16 : {
							msg.arg1 = PlaybackStatus.STATUS_FF3;
						} break;
						case 256 * 32 : {
							msg.arg1 = PlaybackStatus.STATUS_FF4;
						} break;
						case 256 * -2 : {
							msg.arg1 = PlaybackStatus.STATUS_FW1;
						} break;
						case 256 * -8 : {
							msg.arg1 = PlaybackStatus.STATUS_FW2;
						} break;
						case 256 * -16 : {
							msg.arg1 = PlaybackStatus.STATUS_FW3;
						} break;
						case 256 * -32 : {
							msg.arg1 = PlaybackStatus.STATUS_FW4;
						} break;
						case 256 / 4 : {
							msg.arg1 = PlaybackStatus.STATUS_SF1;
						} break;
						case 256 / 16 : {
							msg.arg1 = PlaybackStatus.STATUS_SF2;
						} break;
						case -256 / 4 : {
							msg.arg1 = PlaybackStatus.STATUS_SW1;
						} break;
						case -256 / 16 : {
							msg.arg1 = PlaybackStatus.STATUS_SW2;
						} break;
					}
					
					handler.sendEmptyMessage(MSG_SET_PLAYANDPAUSE_ICONTOPLAY);
					handler.sendMessage(msg);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	} 
	
	public void normalPlay() {
		mPlayer.fastforward(0);
		ffIndex = fwIndex = sfIndex = swIndex = -1;
		ctr_direction_ff = ctr_direction_fw = 0;
		isOnNotNormalPlay = false;
	}
	
	// 0 : not play OK
	// 1 : play success
	public void commandToPlay() {
		if(checkAndRequestAF()) {//start
			if(isMediaPlayerPrepared || isMediaPlayerPaused || isMediaPlayerCompleted) {
				mPlayer.start();
				setMediaStatus(SETSTATUS_STATED);
				handler.removeCallbacks(r_showMessageOnComplete);
			}
			if(isMediaPlayerStarted && isOnNotNormalPlay) {
				normalPlay();
				setMediaStatus(SETSTATUS_STATED);
			}
			if(isMediaPlayerStarted && !isOnNotNormalPlay) {
				;
			}
		}
	}
	
	public void commandToPause() {
		if(isMediaPlayerStarted && isOnNotNormalPlay) {
			normalPlay();
			mPlayer.pause();
			setMediaStatus(SETSTATUS_PAUSED);
		}
		if(isMediaPlayerStarted && !isOnNotNormalPlay) {
			mPlayer.pause();
			setMediaStatus(SETSTATUS_PAUSED);
		}
		
		if(isMediaPlayerPrepared || isMediaPlayerCompleted) {
			//now not let pause;
			mPlayer.pause();
			setMediaStatus(SETSTATUS_PAUSED);
		}
	}
	
	public void f_play() {
		handler.sendEmptyMessage(MSG_SHOWBANNER);
		handler.sendEmptyMessage(MSG_SET_PLAYANDPAUSE_FOCUS);
		commandToPlay();
		if(isMediaPlayerStarted) {
			handler.sendEmptyMessage(MSG_SET_PLAYANDPAUSE_ICONTOPAUSE);
			Message msg = handler.obtainMessage();
			msg.what = MSG_SET_PLAYBACKSTATUS;
			msg.arg1 = PlaybackStatus.STATUS_PLAY;
			handler.sendMessage(msg);
		}
	}
	
	public void f_pause() {
		handler.sendEmptyMessage(MSG_SHOWBANNER);
		handler.sendEmptyMessage(MSG_SET_PLAYANDPAUSE_FOCUS);
		commandToPause();
		if(isMediaPlayerPaused) {
			handler.sendEmptyMessage(MSG_SET_PLAYANDPAUSE_ICONTOPLAY);
			Message msg = handler.obtainMessage();
			msg.what = MSG_SET_PLAYBACKSTATUS;
			msg.arg1 = PlaybackStatus.STATUS_PAUSE;
			handler.sendMessage(msg);
		}
	}
	
	public void f_playAndPause() {
		handler.sendEmptyMessage(MSG_SHOWBANNER);
		handler.sendEmptyMessage(MSG_SET_PLAYANDPAUSE_FOCUS);
		
		if((isMediaPlayerStarted && isOnNotNormalPlay) || isMediaPlayerPrepared || isMediaPlayerCompleted || isMediaPlayerPaused) {
			commandToPlay();
		} else if(isMediaPlayerStarted && !isOnNotNormalPlay) {
			commandToPause();
		}
		if(isMediaPlayerStarted) {
			handler.sendEmptyMessage(MSG_SET_PLAYANDPAUSE_ICONTOPAUSE);
			Message msg = handler.obtainMessage();
			msg.what = MSG_SET_PLAYBACKSTATUS;
			msg.arg1 = PlaybackStatus.STATUS_PLAY;
			handler.sendMessage(msg);
		}
		if(isMediaPlayerPaused) {
			handler.sendEmptyMessage(MSG_SET_PLAYANDPAUSE_ICONTOPLAY);
			Message msg = handler.obtainMessage();
			msg.what = MSG_SET_PLAYBACKSTATUS;
			msg.arg1 = PlaybackStatus.STATUS_PAUSE;
			handler.sendMessage(msg);
		}
	}
	
	public void commandToStop() {
		if(isMediaPlayerPrepared || isMediaPlayerCompleted || isMediaPlayerPaused || isMediaPlayerStarted || isMediaPlayerStopped) {
			mPlayer.stop();
			setMediaStatus(SETSTATUS_STOPPED);
			swIndex = sfIndex = ffIndex = fwIndex = -1;
			ctr_direction_ff = ctr_direction_fw = 0;
			isOnNotNormalPlay = false;
			
		}
	}
	
	public void f_Stop() {
		commandToStop();
		if(isMediaPlayerStopped) {
			handler.sendEmptyMessage(MSG_SET_PLAYANDPAUSE_ICONTOPLAY);
			Message msg = handler.obtainMessage();
			msg.what = MSG_SET_PLAYBACKSTATUS;
			msg.arg1 = PlaybackStatus.STATUS_NOTREADY;
			handler.sendMessage(msg);
		}
	}
	
	public void commandToSlowForward() {
		if(!isMediaPlayerStarted) {
			f_play();
			return ;
		}
		
		if(isMediaPlayerStarted) {
			isOnNotNormalPlay = true;
			sfIndex++;
			swIndex = ffIndex = fwIndex = -1;
			ctr_direction_ff = ctr_direction_fw = 0;
			if(sfIndex >= sfRate.length) {
				sfIndex = 0;
			}
			mPlayer.slowforward(sfRate[sfIndex]);
		}
	}
	
	public void f_slowForward() {
		handler.sendEmptyMessage(MSG_SHOWBANNER);
		handler.sendEmptyMessage(MSG_SET_SLOWFORWARD_FOCUS);
		commandToSlowForward();
		if(isMediaPlayerStarted && isOnNotNormalPlay) {
			handler.sendEmptyMessage(MSG_SET_PLAYANDPAUSE_ICONTOPLAY);
			Message msg = handler.obtainMessage();
			msg.what = MSG_SET_PLAYBACKSTATUS;
			switch(sfIndex) {
				case 0 :
					msg.arg1 = PlaybackStatus.STATUS_SF1;
					break;
				case 1 :
					msg.arg1 = PlaybackStatus.STATUS_SF2;
					break;
				case 2 :
					msg.arg1 = PlaybackStatus.STATUS_SF3;
					break;
				case 3 :
					msg.arg1 = PlaybackStatus.STATUS_SF4;
					break;
			}
			handler.sendMessage(msg);
		}
	}
	
	public void commandToFastForward() {	// fastForward 2, 8, 16, 32, 16, 8, 2
		if(!isMediaPlayerStarted) {
			f_play();
			return ;
		}
		
		if (isMediaPlayerStarted) {
			isOnNotNormalPlay = true;
			swIndex = sfIndex = fwIndex = -1;
			ctr_direction_fw = 0;
			if(ctr_direction_ff == 0) {
				ctr_direction_ff = 1;
				ffIndex = 0;
			} else if(ffIndex == 0 && ctr_direction_ff == -1){
				ffIndex = 1;
				ctr_direction_ff = 1;
			} else if(ffIndex == ffRate.length - 1 && ctr_direction_ff == 1) {
				ctr_direction_ff = -1;
				ffIndex = ffRate.length - 2;
			} else {
				if(ctr_direction_ff == -1) {
					ffIndex--;
				}
				if(ctr_direction_ff == 1) {
					ffIndex++;
				}
			}
			mPlayer.fastforward(ffRate[ffIndex]);
			
		}
	}
	
	public void f_fastForward() {
		handler.sendEmptyMessage(MSG_SHOWBANNER);
		handler.sendEmptyMessage(MSG_SET_FASTFORWARD_FOCUS);
		commandToFastForward();
		if(isMediaPlayerStarted && isOnNotNormalPlay) {
			handler.sendEmptyMessage(MSG_SET_PLAYANDPAUSE_ICONTOPLAY);
			Message msg = handler.obtainMessage();
			msg.what = MSG_SET_PLAYBACKSTATUS;
			switch (ffIndex) {
				case 0:
					msg.arg1 = PlaybackStatus.STATUS_FF1;
					break;
				case 1:
					msg.arg1 = PlaybackStatus.STATUS_FF2;
					break;
				case 2:
					msg.arg1 = PlaybackStatus.STATUS_FF3;
					break;
				case 3:
					msg.arg1 = PlaybackStatus.STATUS_FF4;
					break;
			}
			handler.sendMessage(msg);
		}
	}
	
	public void commandToFastBackward() {
		if(!isMediaPlayerStarted) {
			f_play();
			return ;
		}
		
		if (isMediaPlayerStarted) {
			isOnNotNormalPlay = true;
			swIndex = sfIndex = ffIndex = -1;
			ctr_direction_ff = 0;
			if(ctr_direction_fw == 0) {
				ctr_direction_fw = 1;
				fwIndex = 0;
			} else if(fwIndex == 0 && ctr_direction_fw == -1){
				fwIndex = 1;
				ctr_direction_fw = 1;
			} else if(fwIndex == fwRate.length - 1 && ctr_direction_fw == 1) {
				ctr_direction_fw = -1;
				fwIndex = fwRate.length - 2;
			} else {
				if(ctr_direction_fw == -1) {
					fwIndex--;
				}
				if(ctr_direction_fw == 1) {
					fwIndex++;
				}
			}
			mPlayer.fastrewind(fwRate[fwIndex]);
			
		} 
		
	}
	
	public void f_fastBackward() {
		handler.sendEmptyMessage(MSG_SHOWBANNER);
		handler.sendEmptyMessage(MSG_SET_FASTBACKWARD_FOCUS);
		commandToFastBackward();
		if(isMediaPlayerStarted && isOnNotNormalPlay) {
			handler.sendEmptyMessage(MSG_SET_PLAYANDPAUSE_ICONTOPLAY);
			Message msg = handler.obtainMessage();
			msg.what = MSG_SET_PLAYBACKSTATUS;
			switch (fwIndex) {
				case 0:
					msg.arg1 = PlaybackStatus.STATUS_FW1;
					break;
				case 1:
					msg.arg1 = PlaybackStatus.STATUS_FW2;
					break;
				case 2:
					msg.arg1 = PlaybackStatus.STATUS_FW3;
					break;
				case 3:
					msg.arg1 = PlaybackStatus.STATUS_FW4;
					break;
			}
			handler.sendMessage(msg);
		}
	}
	
	public void commandToSlowBackward() {
		int result_commandToPlay = -1;
		if(!isMediaPlayerStarted) {
			f_play();
			return ;
		}
		
		if(isMediaPlayerStarted) {
			isOnNotNormalPlay = true;
			swIndex++;
			fwIndex = sfIndex = ffIndex = -1;
			ctr_direction_ff = ctr_direction_fw = 0;
			if (swIndex >= swRate.length) {
				swIndex = 0;
			}
			mPlayer.slowrewind(swRate[swIndex]);	//try to slowBackward
			
		} 
		
	}
	
	public void f_slowBackward() {
		handler.sendEmptyMessage(MSG_SHOWBANNER);
		handler.sendEmptyMessage(MSG_SET_SLOWBACKWARD_FOCUS);
		
		commandToSlowBackward();
		if(isMediaPlayerStarted && isOnNotNormalPlay) {
			handler.sendEmptyMessage(MSG_SET_PLAYANDPAUSE_ICONTOPLAY);
			Message msg = handler.obtainMessage();
			msg.what = MSG_SET_PLAYBACKSTATUS;
			switch (swIndex) {
				case 0:
					msg.arg1 = PlaybackStatus.STATUS_SW1;
					break;
				case 1:
					msg.arg1 = PlaybackStatus.STATUS_SW2;
					break;
				case 2:
					msg.arg1 = PlaybackStatus.STATUS_SW3;
					break;
				case 3:
					msg.arg1 = PlaybackStatus.STATUS_SW4;
					break;
			}
			handler.sendMessage(msg);
		}
	}
	
	public int commandToSeek(int seekPos) {
		if (seekPos < 0) {
			Log.e(tag, "Error in commandToSeek");
			return 0;
		}
		
		Log.v(tag, "isMediaPlayerPrepared: " + isMediaPlayerPrepared);
		Log.v(tag, "isMediaPlayerPaused: " + isMediaPlayerPaused);
		Log.v(tag, "isMediaPlayerStarted: " + isMediaPlayerStarted);
		Log.v(tag, "isMediaPlayerCompleted: " + isMediaPlayerCompleted);
		if(isMediaPlayerPrepared || isMediaPlayerPaused || isMediaPlayerStarted || isMediaPlayerCompleted) {
			Log.v(tag, "sendBroadCastPosition0: " + seekPos);
			boolean result = dealWithSeek.setProgress(seekPos, DealWithSeek.PRI_CHANGECOMMAND);	//OK go seek
			if(result) {
				Log.v(tag, "sendBroadCastPosition1: " + seekPos);
				sendBroadCastPosition(seekPos);
				isSeekFromCommand = true;
				mPlayer.seekTo(seekPos);
			}
			return 1;
		} else {
			Log.v(tag, "sendBroadCastPosition2: " + seekPos);
			//int position = time_bar.getProgress();	//no just ... tell now position
			//sendBroadCastPosition(position);
			//isSeekFromCommand = false;
			return 2;
		}
	}
	
	public void forceSeek(int seekPos) {
		if (seekPos < 0) {
			Log.e(tag, "Error in commandToSeek");
			return;
		}
		
		sendBroadCastPosition(seekPos);
		try {
			Log.e(tag, "forceSeek pos = " + seekPos);
			if (mPlayer != null) {
				if(!isMediaPlayerStarted || (isMediaPlayerStarted && isOnNotNormalPlay)) {
					f_play();
				}
				mPlayer.seekTo(seekPos);
				Log.e(tag, "forceSeek ok pos = " + seekPos);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void f_seek(int seekPos) {
		handler.sendEmptyMessage(MSG_SHOWBANNER);
		commandToSeek(seekPos);
	}
	
	public void seek_from_dmr(final int seekPos) {
		handler.sendEmptyMessage(MSG_SHOWBANNER);
		forceSeek(seekPos);
		if(fakeRunnable1 != null) {
			handler.removeCallbacks(fakeRunnable1);
			fakeRunnable1 = null;
		}
		fakeCurrentTime1 = seekPos;
		fakeRunnable1 = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				fakeCurrentTime1 = -1;
			}
		};
		handler.postDelayed(fakeRunnable1, 10000);
	}
	
	private void noAnimationShowBanner() {
		if(!MovieBannerView.isShown())
		{
			MovieBannerView.bringToFront();
			MovieBannerView.setVisibility(View.VISIBLE);
		}
	}
	
	private void animateShowBanner(){
		if(!MovieBannerView.isShown() && !isOnPause)
		{
			MovieBannerView.bringToFront();
			MovieBannerView.setVisibility(View.VISIBLE);
			MovieBannerView.clearAnimation();
			TranslateAnimation TransAnim;
			TransAnim = new TranslateAnimation(0.0f,0.0f,banner_h,0.0f);	
			TransAnim.setDuration(bannerAnimTime);
			MovieBannerView.startAnimation(TransAnim);
		}
	}
	AnimationListener animationListener = new AnimationListener() {
		
		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onAnimationEnd(Animation animation) {
			// TODO Auto-generated method stub
			MovieBannerView.setVisibility(View.INVISIBLE);
		}
	};
	
	private void noAnimationHideBanner() {
		if(MovieBannerView.isShown()) {
			MovieBannerView.setVisibility(View.INVISIBLE);
		}
	}
	
	private void animateHideBanner(){
		if(MovieBannerView.isShown() && !isOnPause) {
			MovieBannerView.clearAnimation();
			TranslateAnimation TransAnim;
			TransAnim = new TranslateAnimation(0.0f,0.0f,0.0f,banner_h);	
			TransAnim.setDuration(bannerAnimTime);
			TransAnim.setAnimationListener(animationListener);
			MovieBannerView.startAnimation(TransAnim);
		}
	}

	
	public void showBanner() {
		isBannerAbleShown = checkBannerAbleShown();
		Log.v(TAG, "isBannerAbleShown = " + isBannerAbleShown);
		if(!isBannerAbleShown) {	// because menu exists
			//do message clean operate
			isBannerStillShown = false;
			return ;
		}
		//when banner is shown , start update time and start calculate to hide banner
		//give a flag isBannerStillShown to indicate that banner is still seen;
		noAnimationShowBanner();
		isBannerStillShown = true;
		//startTaskUpdateTime();
		startTaskWaitBannerToHide();
	}
	
	public void hideBanner() {
		stopTaskWaitBannerToHide();
		noAnimationHideBanner();
		isBannerStillShown = false;
	}
	
	
	public void startTaskUpdateTime() {
		if(timer == null) {
			Log.e(tag, "Error in startTaskUpdateTime Timer not initialized");
			return ;
		}
		//and so on
		copy_task_updateTime();
		timer.schedule(task_updateTime, 0, DELAY_UPDATETIME);
	}
	
	public void stopTaskUpdateTime() {
		if(timer == null) {
			Log.e(tag, "Error in stopTaskUpdateTime Timer not initialized");
			return ;
		}
		cancel_task_updateTime();
	}
	
	public void cancel_task_updateTime() {
		if(task_updateTime != null) {
			task_updateTime.cancel();
			task_updateTime = null;
		}
		handler.removeMessages(MSG_UPDATEPROGRESSBAR);
	}
	
	public void copy_task_updateTime() {
		if(task_updateTime != null) {
			task_updateTime.cancel();
			task_updateTime = null;
		}
		
		task_updateTime = new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(isMediaPlayerPrepared || isMediaPlayerStarted || isMediaPlayerPaused ||isMediaPlayerCompleted)
				{
					int i = 0;
					boolean execError = false;
					try
					{
						i = mPlayer.getCurrentPosition();
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
						if(i < fakeCurrentTime1 && fakeCurrentTime1 != -1) {
							i = fakeCurrentTime1;
						}
						Log.v(tag, "Get Nower pos i  " + i);
						Message msg = handler.obtainMessage();
						msg.what = MSG_UPDATEPROGRESSBAR;
						msg.arg1 = i;
						Log.v(tag, "Get Nower pos i  " + i);
						handler.sendMessage(msg);
						//check need to send Position?
						sendBroadCastPosition(i);
						return ;

					}		
				}
			}
		};
	}
	
	public void startTaskWaitBannerToHide() {
		if(MovieBannerView.getVisibility() == View.INVISIBLE) {
			Log.e(tag, "Error in startTaskWaitBannerToHide MovieBannerView is not able seen");
			return ;
		}
		if(timer == null) {
			Log.e(tag, "Error in startTaskWaitBannerToHide timer not initialized");
			return ;
		}
		
		copy_task_waitBannerToHide();
		timer.schedule(task_waitBannerToHide, DELAY_TOHIDEBANNER);
	}
	
	public void stopTaskWaitBannerToHide() {
		if(timer == null) {
			Log.e(tag, "Error in stopTaskWaitBannerToHide timer not initialized");
			return ;
		}
		cancel_task_waitBannerToHide();
	}
	
	public void copy_task_waitBannerToHide() {
		if(task_waitBannerToHide != null) {
			task_waitBannerToHide.cancel();
			task_waitBannerToHide = null;
		}
		
		task_waitBannerToHide = new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(MSG_HIDEBANNER);
			}
		};
	}
	
	public void cancel_task_waitBannerToHide() {
		if(task_waitBannerToHide != null) {
			task_waitBannerToHide.cancel();
			task_waitBannerToHide = null;
		}
		handler.removeMessages(MSG_HIDEBANNER);
	}
	
	private OnClickListener btclick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch(v.getId()) {
				case R.id.c_playandpause: {///////////////////////////////////////////////////////////////////////////////////
					handler.sendEmptyMessage(MSG_STARTANIMATION);
					f_playAndPause();
					handler.sendEmptyMessage(MSG_DISSMISSANIMATION);
				} break;
				case R.id.c_slowforward: {	////////////////////////////////////////////////////////////////////////////////
					handler.sendEmptyMessage(MSG_STARTANIMATION);
					f_slowForward();
					handler.sendEmptyMessage(MSG_DISSMISSANIMATION);
				} break;
				case R.id.c_slowbackward: {
					handler.sendEmptyMessage(MSG_STARTANIMATION);
					f_slowBackward();
					handler.sendEmptyMessage(MSG_DISSMISSANIMATION);
				} break;
				case R.id.c_fastforward: {
					handler.sendEmptyMessage(MSG_STARTANIMATION);
					f_fastForward();
					handler.sendEmptyMessage(MSG_DISSMISSANIMATION);
				} break;
				case R.id.c_fastbackward: {
					handler.sendEmptyMessage(MSG_STARTANIMATION);
					f_fastBackward();
					handler.sendEmptyMessage(MSG_DISSMISSANIMATION);
				} break;
				case R.id.play_picturesize : {
					handler.sendEmptyMessage(MSG_STARTANIMATION);
					setPicSize();
					handler.sendEmptyMessage(MSG_DISSMISSANIMATION);
				} break;
				case R.id.main : {
					if(!MovieBannerView.isShown()) {
						Log.v(tag, "to showBanner");
						handler.sendEmptyMessage(MSG_SHOWBANNER);
					} else {
						Log.v(tag, "to hideBanner");
						handler.sendEmptyMessage(MSG_HIDEBANNER);
					}
						
				} break;
				case R.id.main_menu : {
					f_onMenu();
				} break;
				default : 
					break;
			}
		}
	};
	
	public void setMediaPlayerListenersNull() {
		mPlayer.setOnBufferingUpdateListener(null);
		mPlayer.setOnCompletionListener(null);
		mPlayer.setOnErrorListener(null);
		mPlayer.setOnInfoListener(null);
		mPlayer.setOnPreparedListener(null);
		mPlayer.setOnSeekCompleteListener(null);
		mPlayer.setOnTimedTextListener(null);
		mPlayer.setOnVideoSizeChangedListener(null);
	}
	
	public void setMediaPlayerListeners() {
		mPlayer.setOnPreparedListener(videoPreparedListener);
        mPlayer.setOnCompletionListener(videoCompletionListener);
        mPlayer.setOnInfoListener(VideoInfoListener);
        mPlayer.setOnErrorListener(ErrorListener);
        mPlayer.setOnSeekCompleteListener(videoSeekCompleteListener);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}
	
	
	private void doRequestAF(){	//try to obtain AUDIOFOCUS_GAIN, nowAudioFocus
        int result = am.requestAudioFocus(audioFocusChangeListener,  
                AudioManager.STREAM_MUSIC, // Request permanent focus.  
                AudioManager.AUDIOFOCUS_GAIN);  

		if(result == AudioManager.AUDIOFOCUS_REQUEST_FAILED){
			handler.sendEmptyMessage(HandlerControlerVariable.MSG_HINT_NOTGETAUDIOFOCUS);
			nowAudioFocus = AudioManager.AUDIOFOCUS_LOSS;
		} else {
			nowAudioFocus = AudioManager.AUDIOFOCUS_GAIN;
		}
	}
	
	private boolean checkAndRequestAF() {
		if(nowAudioFocus != AudioManager.AUDIOFOCUS_GAIN) {
			doRequestAF();
			if(nowAudioFocus != AudioManager.AUDIOFOCUS_GAIN) {
				return false;
			}
		}
		return true;
	}
	
	private void doAbandonAF(){
		if(nowAudioFocus == AudioManager.AUDIOFOCUS_GAIN) {
			if(am != null) {
				am.abandonAudioFocus(audioFocusChangeListener);
				nowAudioFocus = AudioManager.AUDIOFOCUS_LOSS;
			}
		}
	}
	
	OnAudioFocusChangeListener audioFocusChangeListener = new OnAudioFocusChangeListener() {  
        public void onAudioFocusChange(int focusChange) {  
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {  
            	Log.v(tag, "AUDIOFOCUS_LOSS_TRANSIENT");
            	if(mPlayer != null) {
        			try {
        				mPlayer.stop();
        				setMediaStatus(SETSTATUS_STOPPED);
        			} catch (Exception e) {
        				e.printStackTrace();
        			}
        		}
            	nowAudioFocus = AudioManager.AUDIOFOCUS_LOSS;
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            	Log.v(tag, "AUDIOFOCUS_GAIN");
            	commandToPlay(); 
            	nowAudioFocus = AudioManager.AUDIOFOCUS_GAIN;
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            	Log.v(tag, "AUDIOFOCUS_LOSS");
            	if(mPlayer != null) {
        			try {
        				mPlayer.stop();
        				setMediaStatus(SETSTATUS_STOPPED);
        			} catch (Exception e) {
        				e.printStackTrace();
        			}
        		}
            	nowAudioFocus = AudioManager.AUDIOFOCUS_LOSS;
            }   
        }  
    };
    
    public void sendBroadCastPosition(int position) {
    	Intent sendToServer = new Intent("com.rtk.dmr.position.broadcast");
		sendToServer.putExtra("currentTime", position);	//Tell DMC
		sendBroadcast(sendToServer);
    }
    
    public void sendBroadCastNotifyUp() {
    	Intent sendToServer = new Intent(TO_SERVICE_NOTIFY_ACTION);
    	sendBroadcast(sendToServer);
    }
    
    public void findViews() {
    	sView = (SurfaceView)findViewById(R.id.surfaceView);
    	mainLayout = (View)findViewById(R.id.main);
    	MovieBannerView = (View)findViewById(R.id.movie_banner);
    	banner_h = MovieBannerView.getLayoutParams().height;
    	
    	
    	play = (ImageButton)MovieBannerView.findViewById(R.id.c_playandpause);
    	play.requestFocus();
    	
    	slowforward = (ImageButton)MovieBannerView.findViewById(R.id.c_slowforward);
    	slowbackward = (ImageButton)MovieBannerView.findViewById(R.id.c_slowbackward);
    	fastforward = (ImageButton)MovieBannerView.findViewById(R.id.c_fastforward);
    	fastbackward = (ImageButton)MovieBannerView.findViewById(R.id.c_fastbackward);
    	pictureSize = (ImageButton)MovieBannerView.findViewById(R.id.play_picturesize);
    	time_bar = (SeekBar)MovieBannerView.findViewById(R.id.time_bar);
    	dealWithSeek = new DealWithSeek(time_bar);
    	duration = (TextView)MovieBannerView.findViewById(R.id.time_end);
    	hasPlay = (TextView)MovieBannerView.findViewById(R.id.time_now);
    	playStatus = (ImageView)findViewById(R.id.play_status);
    	loadingIcon = (ProgressBar)findViewById(R.id.loadingIcon);
    	
    	menu = (ImageView) findViewById(R.id.main_menu);
    	dolby = (TextView)findViewById(R.id.dolby);
    	
    	if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
    		long_msg = new ConfirmMessage(DmrVideoPlayerActivity.this);
            short_msg = new ConfirmMessage(DmrVideoPlayerActivity.this,678,226);
           
    	} else {
    		long_msg = new ConfirmMessage(DmrVideoPlayerActivity.this);
            short_msg = new ConfirmMessage(DmrVideoPlayerActivity.this,678,226); 
    	}
    	
    }
    
    public void setListeners() {
    	mainLayout.setOnClickListener(btclick);
    	MovieBannerView.setOnClickListener(btclick);
    	play.setOnClickListener(btclick);
    	slowforward.setOnClickListener(btclick);
    	slowbackward.setOnClickListener(btclick);
    	fastforward.setOnClickListener(btclick);
    	fastbackward.setOnClickListener(btclick);
    	pictureSize.setOnClickListener(btclick);       
    	menu.setOnClickListener(btclick);
    	
    	//time_bar.setOnSeekBarChangeListener(seekListen);
    	
    	sView.getHolder().setKeepScreenOn(true);
        sView.getHolder().addCallback(new SurfaceListener());
    }
    
    public void initBetweenOP() {
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		//UI data reserve
		int timeBarMax = time_bar.getMax();
		String timeEndStr = (String) duration.getText();
		
		Drawable play_status_drawable = playStatus.getDrawable();
		//Drawable play_mode_drawable = play_mode.getDrawable();
		//Drawable picture_size_drawable = pictureSize.getDrawable();
		Drawable playAndPause_drawable = play.getDrawable();
		if(quickMenu != null && quickMenu.isShowing()) {
			quickMenu.dismiss();
		}
		
		setContentView(R.layout.video_player_dmr);
		
		findViews();
		setListeners();
		initBetweenOP();
		//Recover UI data
		//other
		time_bar.setMax(timeBarMax);
		duration.setText(timeEndStr);
		playStatus.setImageDrawable(play_status_drawable);
		//play_mode.setImageDrawable(play_mode_drawable);
		//pictureSize.setImageDrawable(picture_size_drawable);
		play.setImageDrawable(playAndPause_drawable);
		if(loading) {
			startLoading();
		} else {
			dismissLoading();
		}
		super.onConfigurationChanged(newConfig);
	}
	
	public void resetMediaPlayer() {
		Log.v(tag, "MediaPlayer reset bb");

		setMediaPlayerListenersNull();
		mPlayer.reset();
		setMediaStatus(SETSTATUS_IDLE);
		isOnNotNormalPlay = false;
		ffIndex = fwIndex = sfIndex = swIndex = -1;
		ctr_direction_ff = ctr_direction_fw = 0;
		
		Log.v(tag, "MediaPlayer reset ee");
		curr_SPU_ENABLE = 0;
		curr_subtitle_stream_num = 0;
		subtitle_num_Stream = 0;
		curr_audio_stream_num = -1;
		audio_num_stream = 0;
	}
	
	public void releaseMediaPlayer() {
		Log.v(tag, "MediaPlayer release begin");
		mPlayer.release();
		mPlayer = null;
		Log.v(tag, "MediaPlayer release end");
	}
	
	public void startFromIntent() {
		needSendNotify = true;
		System.gc();
		stopTaskUpdateTime();
		isStopByDMR = false;
		
		if(mPlayer == null) {
			mPlayer = new MediaPlayer();
		}
		
		handler.removeCallbacks(fakeRunnable1);
		handler.removeCallbacks(r_showMessageOnComplete);
		handler.sendEmptyMessage(MSG_STARTANIMATION);
		msg_hint.dismiss();
		short_msg.dismiss();
		resetMediaPlayer();
		dealWithSeek.reset();
    	
    	handler.sendEmptyMessage(MSG_SET_PLAYANDPAUSE_ICONTOPLAY);
    	Message msg = handler.obtainMessage();
    	msg.what = MSG_SET_PLAYBACKSTATUS;
    	msg.arg1 = PlaybackStatus.STATUS_NOTREADY;
    	handler.sendMessage(msg);
    	
    	//About progressBAR
    	//sendBroadCastPosition(0);
    	time_bar.setProgress(0);
    	isSeeking = false;
    	needSeek = false;
    	hasPlay.setText("00:00:00");
    	
    	dateMiles = (long)RtkDMRService.getfileDate();
    	mediaDateStr = parseFromDateMiles(dateMiles);
				setMediaPlayerListeners();
				try {
					mPlayer.setPlayerType(6);
			if(!receiveInvalidProtol) {
				handler.sendEmptyMessage(MSG_DISSMISSANIMATION);
				sendBroadCastNotifyUp();
				if(isActivityReady) {
					msg_hint.setMessage(getResources().getString(R.string.unsupport_file));
					msg_hint.show();
				}
				return;
			}
					mPlayer.setDataSource(filePathArray[currIndex]);
					setMediaStatus(SETSTATUS_INITIALIZED);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				mPlayer.prepareAsync();
			}
	
	public void f_onMenu() {	//for menu show and hide
		Log.v(tag, "Menu Click");
		Log.v(tag, "show quickMneu");
		if(!quickMenu.isShowing()) {
			hideBanner();	//later do
			//before show Menu, we want data
			getSubtitleInfo();
			if(curr_subtitle_stream_num < 1 || subtitle_num_Stream < 1) {
				curr_SPU_ENABLE = 0;
			} else {
				curr_SPU_ENABLE = 1;
			}
			getAudioTrackInfo();			
			// where to show Menu;
			quickMenu.mozart_specShow(menu);
		}
	}
	
	OnItemClickListener quickmenuItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			//quickAutoQuit();
			switch(position) {
				case SUBTITLE_ID: {
					getSubtitleInfo();
					if(subtitle_num_Stream <= 0) {
						curr_SPU_ENABLE = 0;
						setSubtitle();
						((BaseAdapter)(parent.getAdapter())).notifyDataSetChanged();
						return ;
					}
										
					if(0 == curr_SPU_ENABLE ) {
						curr_SPU_ENABLE = 1;
						curr_subtitle_stream_num = 1;
						setSubtitle();
						((BaseAdapter)(parent.getAdapter())).notifyDataSetChanged();
						return ;
					}
					if(1 == curr_SPU_ENABLE && curr_subtitle_stream_num >= subtitle_num_Stream) {
						curr_SPU_ENABLE = 0;
						curr_subtitle_stream_num = 0;
						setSubtitle();
						((BaseAdapter)(parent.getAdapter())).notifyDataSetChanged();
						return ;
					}
					curr_subtitle_stream_num ++;
					setSubtitle();
					((BaseAdapter)(parent.getAdapter())).notifyDataSetChanged();
				} break;
				case AUDIOTRACK_ID: {
					getAudioTrackInfo();
					if(audio_num_stream < 1) {
						curr_audio_stream_num = 0;
						return ;
					}
					curr_audio_stream_num ++;
					setAudioTrack();
					((BaseAdapter)(parent.getAdapter())).notifyDataSetChanged();
				} break;
				case DETAILS_ID: {
					
				} break;
				case HELP_ID: {
					ComponentName componetName = new ComponentName("com.android.emanualreader","com.android.emanualreader.MainActivity");
					quickMenu.dismiss();
					Intent intent = new Intent();
					intent.setComponent(componetName);
					startActivity(intent);
				} break;
				default :
					break;
			}
		}
		
	};
		
	private void quickAutoQuit() {
		handler.removeMessages(MSG_HIDE_MENU);
		Message msg = handler.obtainMessage(MSG_HIDE_MENU);
		handler.sendMessageDelayed(msg, DELAY_6000);
	}
	
	public void getAudioService() {
		if(am == null) {
			am = (AudioManager) getSystemService(this.AUDIO_SERVICE);
		}
	}
	
	public int getOrientation() {
		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			return ORIENTATION_LANDSCAPE;
		} else {
			return ORIENTATION_PORTRAIT;
		}
	}
	
	public boolean checkBannerAbleShown() {
//		if(isBannerForceShown) {
//			isBannerForceShown = false;
//			return true;
//		}
		if(quickMenu.isShowing() || msg_hint.isShowing() || short_msg.isShowing()) {
			return false;
		} else {
			return true;
		}
	}
	
	
	class DealWithSeek {
		static final int PRI_CHANGEDEFAULT = 0;
		static final int PRI_CHANGECOMMAND = 1;
		static final int PRI_CHANGEKEYPAD = 2;
		static final int PRI_CHANGETRACK = 3;
		int nowPRI = PRI_CHANGEDEFAULT;
		int requestPRI = PRI_CHANGEDEFAULT;
		final int DELAY_1000 = 1000;
		final int DELAY_2000 = 2000;
		final int DELAY_500 = 500;
		
		final int MSG_RESET_PRI = 100;
		
		Handler dfHandler = null;
		SeekBar seekBar = null;
		
		boolean isFromKeyPad = false;
		boolean needUpdateUI = false;
		void init() {
			dfHandler = new Handler() {

				@Override
				public void handleMessage(Message msg) {
					// TODO Auto-generated method stub
					switch(msg.what) {
						case MSG_RESET_PRI : {
							nowPRI = PRI_CHANGEDEFAULT;
						} break;
					}
					
					super.handleMessage(msg);
				}
				
			};
			seekBar.setEnabled(false);
			seekBar.setClickable(false);
			seekBar.setSelected(false);
			seekBar.setFocusable(false);
//			seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
//			seekBar.setOnKeyListener(onKeyListener);
		}
		
		public DealWithSeek(SeekBar seekBar) {
			// TODO Auto-generated constructor stub
			this.seekBar = seekBar;
			init();
		}
		
		OnKeyListener onKeyListener = new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				switch(keyCode) {
					case KeyEvent.KEYCODE_DPAD_LEFT :
					case KeyEvent.KEYCODE_DPAD_RIGHT: {
						Log.v(tag, "change by left or right keypad!!!");
						handler.sendEmptyMessage(MSG_SHOWBANNER);
						boolean result = requestPRI(PRI_CHANGEKEYPAD);
						if(result) {
							isFromKeyPad = true;
						} else {
							return true;
						}
					}
				}
				return false;
			}
		};
		
		OnSeekBarChangeListener onSeekBarChangeListener = new OnSeekBarChangeListener() {
			boolean needUpdateUI = false;
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(MSG_SHOWBANNER);
				if(!checkAndRequestAF() || seekBar.getMax() <= 0) {
					return ;
				}
				if(isMediaPlayerStarted || isMediaPlayerPaused || isMediaPlayerCompleted || isMediaPlayerPrepared) {
					if(!isMediaPlayerStarted || (isMediaPlayerStarted && isOnNotNormalPlay)) {
						f_play();
					}
					if(!isMediaPlayerStarted) {
						return ;
					}
					int progress = seekBar.getProgress();
					boolean result = requestPRI(PRI_CHANGETRACK);
					if(result && seekBar.getMax() > 0) {
						mPlayer.seekTo(progress);
						setMediaStatus(SETSTATUS_STATED);
						seekBar.setProgress(progress);
	    				progress /= 1000;
	    				int minute = progress / 60;
	    				int hour = minute / 60;
	    				int second = progress % 60;
	    				minute %= 60;
	    				hasPlay.setText(String.format("%02d:%02d:%02d", hour, minute, second));
					}
				}
		}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				stopTaskWaitBannerToHide();
				if(!checkAndRequestAF()) {
					return ;
				}
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				if(!checkAndRequestAF() || seekBar.getMax() <= 0) {
					return ;
				}

				if(fromUser) {
					isFromKeyPad = false;
					if(isMediaPlayerStarted || isMediaPlayerPaused || isMediaPlayerCompleted || isMediaPlayerPrepared) {
						if(!isMediaPlayerStarted || (isMediaPlayerStarted && isOnNotNormalPlay)) {
							f_play();
							stopTaskWaitBannerToHide();
						}
						if(!isMediaPlayerStarted) {
							return ;
						}
						boolean result = requestPRI(PRI_CHANGETRACK);
						if(result) {
							Log.v(tag, "seeking to progress--- " + progress);
							mPlayer.seekTo(progress);
							setMediaStatus(SETSTATUS_STATED);
							
							seekBar.setProgress(progress);
							progress /= 1000;
							int minute = progress / 60;
							int hour = minute / 60;
							int second = progress % 60;
							minute %= 60;
							hasPlay.setText(String.format("%02d:%02d:%02d", hour, minute, second));
							return ;
						}
					}
				}
				
				if(isFromKeyPad) {
					isFromKeyPad = false;
					if(isMediaPlayerStarted || isMediaPlayerPaused || isMediaPlayerCompleted || isMediaPlayerPrepared) {
						if(!isMediaPlayerStarted || (isMediaPlayerStarted && isOnNotNormalPlay)) {
							f_play();
						}
						if(!isMediaPlayerStarted) {
							return ;
						}
						boolean result = requestPRI(PRI_CHANGEKEYPAD);
						if(result) {
							Log.v(tag, "seeking to progress--- " + progress);
							mPlayer.seekTo(progress);
							setMediaStatus(SETSTATUS_STATED);
							seekBar.setProgress(progress);
							progress /= 1000;
							int minute = progress / 60;
							int hour = minute / 60;
							int second = progress % 60;
							minute %= 60;
							hasPlay.setText(String.format("%02d:%02d:%02d", hour, minute, second));
							return ;
						}
					}
				}
			}
		};
		public synchronized boolean setProgress(int progress) {
			// TODO Auto-generated method stub
			requestPRI = PRI_CHANGEDEFAULT;
			if(requestPRI >= nowPRI) {
				seekBar.setProgress(progress);
				return true;
			}
			return false;
		}
		
		public synchronized boolean setProgress(int progress , int PRI) {
			requestPRI = PRI;
			if(requestPRI >= nowPRI) {
				nowPRI = requestPRI;
				seekBar.setProgress(progress);
				switch(requestPRI) {
					case PRI_CHANGEDEFAULT : {
						
					} break;
					case PRI_CHANGECOMMAND : {
						dfHandler.removeMessages(MSG_RESET_PRI);
						dfHandler.sendEmptyMessageDelayed(MSG_RESET_PRI, DELAY_500);
					} break;
					case PRI_CHANGEKEYPAD : {
						dfHandler.removeMessages(MSG_RESET_PRI);
						dfHandler.sendEmptyMessageDelayed(MSG_RESET_PRI, DELAY_500);
					} break;
					case PRI_CHANGETRACK : {
						dfHandler.removeMessages(MSG_RESET_PRI);
						dfHandler.sendEmptyMessageDelayed(MSG_RESET_PRI, DELAY_500);
					} break;
				}
				return true;
			}
			
			return false;
		}
		
		public synchronized boolean requestPRI(int PRI) {
			requestPRI = PRI;
			if(requestPRI >= nowPRI) {
				nowPRI = requestPRI;
				switch(requestPRI) {
					case PRI_CHANGEDEFAULT : {
						
					} break;
					case PRI_CHANGECOMMAND : {
						dfHandler.removeMessages(MSG_RESET_PRI);
						dfHandler.sendEmptyMessageDelayed(MSG_RESET_PRI, DELAY_500);
					} break;
					case PRI_CHANGEKEYPAD : {
						dfHandler.removeMessages(MSG_RESET_PRI);
						dfHandler.sendEmptyMessageDelayed(MSG_RESET_PRI, DELAY_500);
					} break;
					case PRI_CHANGETRACK : {
						dfHandler.removeMessages(MSG_RESET_PRI);
						dfHandler.sendEmptyMessageDelayed(MSG_RESET_PRI, DELAY_500);
					} break;
				}
				return true;
			}
			return false;
		}
		
		void reset() {
			dfHandler.removeMessages(MSG_RESET_PRI);
			nowPRI = PRI_CHANGEDEFAULT;
		}
	}
	
	void resetPlaybackResources() {
		handler.sendEmptyMessage(MSG_SET_PLAYANDPAUSE_ICONTOPLAY);
		Message msg = handler.obtainMessage();
		msg.what = MSG_SET_PLAYBACKSTATUS;
		msg.arg1 = PlaybackStatus.STATUS_NOTREADY;
		handler.sendMessage(msg);
	}
	
	
	void setMediaStatus(String status) {
		isMediaplayIdle = isMediaPlayerPrepared = isMediaPlayerCompleted = isMediaInitialized =	isMediaPlayerStarted = isMediaPlayerPaused = isMediaPlayerStopped = isMediaPlayerError = false;
		if(status.equals(SETSTATUS_IDLE)) {
			isMediaplayIdle = true;
		} else if (status.equals(SETSTATUS_INITIALIZED)) {
			isMediaInitialized = true;
		} else if (status.equals(SETSTATUS_PREPARED)) {
			isMediaPlayerPrepared = true;
		} else if (status.equals(SETSTATUS_STATED)) {
			isMediaPlayerStarted = true;
		} else if (status.endsWith(SETSTATUS_PAUSED)) {
			isMediaPlayerPaused = true;
		} else if (status.equals(SETSTATUS_STOPPED)) {
			isMediaPlayerStopped = true;
		} else if (status.equals(SETSTATUS_COMPLETED)) {
			isMediaPlayerCompleted = true;
		}
	}
	
	public void startLoading() {
		loadingIcon.setVisibility(View.VISIBLE);
		loading = true;
	}

	public void dismissLoading() {
		loadingIcon.setVisibility(View.INVISIBLE);
		loading = false;
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		if(hasFocus) {
			isActivityReady = true;
			if(!receiveInvalidProtol) {
				msg_hint.setMessage(getResources().getString(R.string.unsupport_file));
				msg_hint.show();
			}
		} else {
			isActivityReady = false;
		}
		
	}
	
	String parseFromDateMiles(long dateMiles) {
		if(dateMiles == 0) {
			return "";
		}
		String language = getApplicationContext()
        		.getResources().getConfiguration().locale.getLanguage();
		Date date = new Date(dateMiles);
		String ret = "";
		if(language.equals("ja"))
    	{
			try {
				ret = dateFormate(date,
        				"yyyy"+(String) resourceMgr.getText(R.string.year_menuicon)
        			   +" MMM dd"+(String) resourceMgr.getText(R.string.day_menuicon)
        			   +" EEEEEE");
			} catch (Exception e) {
				// TODO: handle exception
				ret = "";
			}
    	}
    	else
    	{
    		try {
    			ret = dateFormate(date,"EEE,dd MMM yyyy");
    		} catch(Exception e) {
    			ret = "";
    		}
    	}
		return ret;
	}
	
	public String dateFormate(Date date,String formatString) {
		// TODO Auto-generated method stub				
		SimpleDateFormat df_des = new SimpleDateFormat(formatString);
	    String formateDate1 = "";
	    try {
	    	formateDate1 = df_des.format(date);
	    } catch(Exception e) {
	    	
	    }
	    return formateDate1;
	}
	
	void setNavVisibility(boolean visible) {
        int newVis = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        if (!visible) {
            newVis |= View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }
        getWindow().getDecorView().requestFocus();
        // Set the new desired visibility.
        this.getWindow().getDecorView().setSystemUiVisibility(newVis);
    }
}


