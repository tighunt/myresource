package com.rtk.dmp;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.realtek.DLNA_DMP_1p5.DLNA_DMP_1p5;
import com.realtek.DLNA_DMP_1p5.DLNA_DMP_1p5.DeviceStatusListener;
import com.rtk.dmp.AudioBrowser.QuickMenuAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
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
import android.util.Log;
import android.view.KeyEvent;
import android.media.MediaPlayer.OnCompletionListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.app.TvManager;


@SuppressLint("ShowToast")
public class DmrVideoPlayerActivity extends Activity{
	private final static String TAG = "DmrVideoPlayerActivity";
	private final static String tag = "DmrVideoPlayerActivityTestTag";
	
	
    private String[] filePathArray = null;
	public ArrayList<String> fileTitleArray = new ArrayList<String>();
	public ArrayList<String> DMSName = new ArrayList<String>();
	private String serverName = null;
	
	DeviceStatusListener mDeviceListener = null;
	private DLNA_DMP_1p5 dlna_DMP_1p5 = null;
	
	public int currIndex = 0;
	public int len = 0;
	
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
	private boolean isNeedExecuteOnPause = true;
	private boolean isOnPause = false;
	
	
	private static int ResultCodeFinishVideoBrowser = 1;
	
	private boolean isShowChapterMetaData = false;
	
	private int selected_idx = 0;
	/********   android component parameter initial *******/
	private SurfaceView sView = null;
	protected MediaPlayer mPlayer = null;
	
	private ImageButton play = null;
	private ImageButton slowforward = null;
	private ImageButton slowbackward = null;
	private ImageButton fastforward = null;
	private ImageButton fastbackward = null;
	private ImageButton pictureSize = null;
	private ImageView menu = null;
	private SeekBar time_bar = null;
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
	
	//************MediaPlayer status******************
	private boolean isMediaplayIdle = true;
	private boolean isOnPlay = false;
	private boolean isOnNotNormalPlay = false;
	private boolean needSeek = false;
	private boolean isSeeking = false;
	
	private boolean isSeekFromCommand = false;
	private boolean isSeekFromUser = false;
	
	private boolean isStillSeeking = false;
	private long lastSeekComplete = 0;
	private long save_lastSeekComplete = 0;
	
	
	private boolean isMediaPlayerPrepared = false;
	
	//*********Action Defined End***************************
	AudioManager am = null;
	DMRVideoBroadcastReceiver bc_receiver = null;
	boolean isBannerAbleShown = true;
	boolean isBannerStillShown = false;
	
	boolean isFromSavedInstance = false;
	boolean isFromRestart = false;
	
	boolean isFirstStart = true;
	
	private int ffRate[] = {2, 8, 16, 32};	
	private int fwRate[] = {2, 8, 16, 32};	
	private int sfRate[] = {4, 16};	
	private int swRate[] = {4, 16};
	
	private int ffIndex = -1;
	private int fwIndex = -1;
	private int sfIndex = -1;
	private int swIndex = -1;
	
	
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
	
	
	
	
	//allow interval between nowPos and seekPos
	private final int INTERVALFORNOWPOSANDSEEKPOS = 2000;	//ms
	
	private final int DELAY_UPDATETIME = 300;	//ms
	private final int DELAY_SEEK = 700;	//ms
	private final int DELAY_TOHIDEBANNER = 60000; //ms
	
	private int nowAudioFocus = AudioManager.AUDIOFOCUS_LOSS;	//start not have audio focus
	//long time1, time2;
	
	boolean stopNewIntent = false;
	
	QuickMenu quickMenu = null;
	private QuickMenuAdapter quickMenuAdapter = null;
	
	
	
	class QuickMenuAdapter extends BaseAdapter {
		Context mContext = null;
		int[] menu_name = new int[] {
			R.string.quick_menu_sleep,
			R.string.quick_menu_version,
			R.string.quick_menu_exit
		};
		int[] visibility = new int[]{
 			View.INVISIBLE,
 			View.INVISIBLE,
 			View.INVISIBLE,
	 	};
		
		public QuickMenuAdapter(Context mContext) {
			this.mContext = mContext;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getItem(int arg0) {
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
			return null;
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
			switch (what)
			{
				case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
				{
					Log.e(TAG, "MediaServer died, finish mySelf!");
					
					resetMediaPlayer();
					
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
			Log.e(TAG, "VideoInfoListener" + "---what = "+what+"----");
			switch(what)
			{
			case 0x10000003: //UNKNOWN_FORMAT /* FOR AUDIO */
			{
				if(!isAudio_Error)
				{
					if(isOnPlay)
					{
						mPlayer.pause();
						play.setImageResource(R.drawable.dnla_video_controll_stop_f);
						isOnPlay = false;
					}
					
					
    				//msg_hint.setMessage(DmrVideoPlayerActivity.this.getResources().
			    	getString(R.string.FatalErrorCode_Audio_Unknow_Format);
    				//msg_hint.show();
					
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
				long_msg.setMessage(DmrVideoPlayerActivity.this.getResources().
		    	getString(R.string.FatalErrorCode_Video_Frame_Rate));
		    	long_msg.setButtonText(DmrVideoPlayerActivity.this.getResources().getString(R.string.msg_yes));
				long_msg.left.setVisibility(View.INVISIBLE);
				long_msg.right.setVisibility(View.INVISIBLE);
    			long_msg.confirm_bt.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						long_msg.dismiss();
					}		
				});
				long_msg.show();
			}
			break;
			case 0x20000008: //FATALERR_VIDEO_MPEG2DEC_UNSUPPORTED_RESOLUTION
			{

				long_msg.setMessage(DmrVideoPlayerActivity.this.getResources().
		    	getString(R.string.FatalErrorCode_Video_Resolution));
				long_msg.setButtonText(DmrVideoPlayerActivity.this.getResources().getString(R.string.msg_yes));
				long_msg.left.setVisibility(View.INVISIBLE);
				long_msg.right.setVisibility(View.INVISIBLE);
    			long_msg.confirm_bt.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						long_msg.dismiss();
					}		
				});
				long_msg.show();
			}
			break;
			case 722: /*mPlayer.MEDIA_INFO_FE_PB_RESET_SPEED*/
			{
				/*
				if(mPlayer.getCurrentPosition() < 5000)
				{
					play.setImageResource(R.drawable.au_play);
					isforward = false;
					forward_press = 1;
					isrewind = false;
					rewind_press = 1;
				}
				*/
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
			Log.v(tag, "VideoPlayer onPrepared!");
			isMediaPlayerPrepared = true;
			Intent intent = new Intent();
			intent.setAction("MediaScannerSuspend");
			sendBroadcast(intent);
			//if want to resume
			//	do it here
			//		just seek to the point is OK
			if(isFromSavedInstance) {
				//do Resume
				return ;
			}
			
			if(isFromRestart) {
				//do Resume
				return ;
			}
			
			try {
				firstStart();	//begin to play
			} catch (IllegalStateException e) {
    			e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			
			if(isFirstStart) {
				return ;
			} else {
				handler.sendEmptyMessage(MSG_SET_PLAYANDPAUSE_ICONTOPAUSE);
		    	Message msg = handler.obtainMessage();
				msg.what = MSG_SET_PLAYBACKSTATUS;
				msg.arg1 = PlaybackStatus.STATUS_PLAY;
			}
		}
    };
    
    private OnCompletionListener videoCompletionListener = new OnCompletionListener()
    {
		@Override
		public void onCompletion(MediaPlayer mp) {
			boolean isRemoved = false;
			isMediaPlayerPrepared = true;
			isMediaplayIdle = false;
			ffIndex = fwIndex = sfIndex = swIndex = -1;
			isOnNotNormalPlay = false;
			isOnPlay = false;
			isFirstStart = false;
			if(serverName == null) {				
				return ;
			}
			
			
			if(DMSName.size() != 0){
				for(int i = 0; i < DMSName.size(); i++){
					isRemoved = DMSName.get(i).equals(serverName);
					if(isRemoved){
						break;
					}
				}
				if(isRemoved){
					Log.e(TAG, "This DMS has been closed");
					short_msg.confirm_title.setVisibility(View.INVISIBLE);
    				short_msg.setMessage(getResources().getString(R.string.DMS_was_close));
    				short_msg.setButtonText(getResources().getString(R.string.msg_yes));
        			short_msg.left.setVisibility(View.VISIBLE);
        			short_msg.right.setVisibility(View.VISIBLE);
        			short_msg.confirm_bt.setVisibility(View.VISIBLE);
        			short_msg.setKeyListener(true);
        			short_msg.confirm_bt.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							DMSName.clear();
							short_msg.dismiss();
							isBannerAbleShown = true;
							DmrVideoPlayerActivity.this.finish();
						}
								
					});
        			isBannerAbleShown = false;
        			short_msg.show();
        			return;
				}
			}
			
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
        
        findViews();
        setListeners();
        init();
        reInit();
        
        initHandler();
        
        if(savedInstanceState != null) {
        	isFromSavedInstance = true;
        	//now don't do it, just finish
        	this.finish();
        	
        } else {
        	isFromSavedInstance = false;
        	captureIntent();
        }
        
    	mDeviceListener = new DeviceStatusListener(){
			@Override
			public void deviceAdded(String dmsName) {
				// TODO Auto-generated method stub
			}
			@Override
			public void deviceRemoved(String dmsName) {
				// TODO Auto-generated method st
					DMSName.add(dmsName);
			}	
        	
        };
        
        dlna_DMP_1p5 = new DLNA_DMP_1p5();
        dlna_DMP_1p5.setDeviceStatusListener(mDeviceListener);
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
		if(timer == null)
			timer = new Timer(true);
		
		doRegisterReceiver();
		
		handler.sendEmptyMessage(MSG_SHOWBANNER);
		
	    super.onResume();
    }
	
	@Override
	protected void onPause() {
		Log.v(TAG, "VidePlayerActivity onPause");
		Log.e(TAG, "isNeedExecuteOnPause = "+isNeedExecuteOnPause);
		
		doUnRegisterReceiver();
		
		stopTaskUpdateTime();
		stopTaskWaitBannerToHide();
		
		if(timer != null)
		{
			timer.cancel();
			timer = null;
		}
		
		f_pause();
		
		super.onPause(); 
    }
	
    @Override
	 public void onStop(){
    	Log.v(TAG, "VidePlayerActivity onStop");
    	resetMediaPlayer();
    	
		DmrVideoPlayerActivity.this.finish();
		stopNewIntent = false;
		super.onStop();
    }
    
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}
    
    @Override
	protected void onDestroy() {
    	Log.v(TAG, "VidePlayerActivity onDestroy");
    	releaseMediaPlayer();
    	Log.v(TAG, "VideoPlayerActivity onDestroy end");
		super.onDestroy();
	}
    
    @Override
    protected void onNewIntent(Intent intent) {
    	Log.v(TAG, "VidePlayerActivity onNewIntent");
    	super.onNewIntent(intent);
    	
    	currIndex = intent.getIntExtra("initPos", 0);
    	filePathArray = intent.getStringArrayExtra("filelist");
    	
    	//sendBroadCast to tell nower position 0;
    	
    	startFromIntent();
    	
    }
 
	@SuppressWarnings("static-access")
	@Override    
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i(TAG, "keyCode = "+keyCode);
		
		if(isOnPause)
			return true;
		
		switch (keyCode) {
		case 165: //for L4300 KeyEvent.KEYCODE_INFO:
		case KeyEvent.KEYCODE_M:
		{
			;
		}
		return true;
		case 231:  //for L4300 KeyEvent.KEYCODE_STOP
		case 257:  //DISPLAY KEY in REALTECK RCU
		case KeyEvent.KEYCODE_K:   // As [Stop] key
		{
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
     	case 230:
     	case 254:	// KeyEvent.KEYCODE_EPG:
     	case KeyEvent.KEYCODE_S:
     	{
    		hide_lawrate_metadata_info();
    		hide_picture_size_ui();
    		
     		setSubtitle();
     	}
     	return true;
     	case 220: //for L4300 KeyEvent.KEYCODE_SOUND_MODE
     	case KeyEvent.KEYCODE_PROG_BLUE:
     	case KeyEvent.KEYCODE_A:
     	{
    		hide_lawrate_metadata_info();
    		hide_picture_size_ui();
    		
     		setAudioTrack();
     	}
     	return true;
     	case 232: //for L4300 KeyEvent.KEYCODE_PLAY:
     	case KeyEvent.KEYCODE_PROG_GREEN:
     	case KeyEvent.KEYCODE_P:
     	{	
			if(isforward)
			{
				mPlayer.fastforward(0);
				play.setImageResource(R.drawable.au_play);
				isforward = false;
				forward_press = 1;
			}else if(isrewind)
			{
				mPlayer.fastrewind(0);
				play.setImageResource(R.drawable.au_play);
				isrewind = false;
				rewind_press = 1;
			}
			else
			{
				if(!mPlayer.isPlaying())
				{
					mPlayer.start();
					play.setImageResource(R.drawable.au_play);
				}
			}
			
			bannerShowTimeOutControl(MOVIE_BANNER, TimerDelay.delay_6s);
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
    				handler.removeMessages(HandlerControlerVariable.PROGRESS_CHANGED);
    				isforward = false;
    				forward_press = 1;
    			}else if(isrewind)
    			{
    				mPlayer.fastrewind(0);
    				mPlayer.pause();
    				play.setImageResource(R.drawable.au_pause);
    				handler.removeMessages(HandlerControlerVariable.PROGRESS_CHANGED);
    				isrewind = false;
    				rewind_press = 1;
    			}else
    			{
    				if(mPlayer.isPlaying())
    				{
    					mPlayer.pause();
    					play.setImageResource(R.drawable.au_pause);
    					handler.removeMessages(HandlerControlerVariable.PROGRESS_CHANGED);
    				}else
    				{
    					mPlayer.start();
    					play.setImageResource(R.drawable.au_play);
    					showCurrentInfo();
    				}
    			}
         		bannerShowTimeOutControl(MOVIE_BANNER, TimerDelay.delay_6s);
         		showCurrentInfo();
     		}
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
        map = (MediaApplication)getApplication();
        
        //mPlayer = map.getMediaPlayer();
        
        mTVService = (TvManager)getSystemService("tv");
		toast = new Toast(this);
        getAudioService();
        
        quickMenuAdapter = new QuickMenuAdapter(this);
        quickMenu = new QuickMenu(this, quickMenuAdapter);
	}
	
	private void initHandler()
	{
		 handler = new Handler() {	
	    	@Override
			public void handleMessage(Message msg) {
	    		switch(msg.what){
		    		case MSG_SET_PICTURESIZE_FOCUS: {
		    			pictureSize.requestFocus();
		    		} break;
	    			case MSG_HINT_NOTGETAUDIOFOCUS : {
	    				Toast.makeText(DmrVideoPlayerActivity.this, "Can't get AudioFocus to start", Toast.LENGTH_SHORT).show();
	    			} break;
	        		case MSG_SET_PLAYBACKSTATUS: {
	        			switch(msg.arg1) {
	        				case PlaybackStatus.STATUS_PLAY:
	        					break;
	        				case PlaybackStatus.STATUS_PAUSE:
	        					break;
	        				case PlaybackStatus.STATUS_FF1:
	        					break;
	        				case PlaybackStatus.STATUS_FF2:
	        					break;
	        				case PlaybackStatus.STATUS_FF3:
	        					break;
	        				case PlaybackStatus.STATUS_FF4:
	        					break;
	        				case PlaybackStatus.STATUS_FW1:
	        					break;
	        				case PlaybackStatus.STATUS_FW2:
	        					break;
	        				case PlaybackStatus.STATUS_FW3:
	        					break;
	        				case PlaybackStatus.STATUS_FW4:
	        					break;
	        				case PlaybackStatus.STATUS_SF1:
	        					break;
	        				case PlaybackStatus.STATUS_SF2:
	        					break;
	        				case PlaybackStatus.STATUS_SF3:
	        					break;
	        				case PlaybackStatus.STATUS_SF4:
	        					break;
	        				case PlaybackStatus.STATUS_SW1:
	        					break;
	        				case PlaybackStatus.STATUS_SW2:
	        					break;
	        				case PlaybackStatus.STATUS_SW3:
	        					break;
	        				case PlaybackStatus.STATUS_SW4:
	        					break;
	        				case PlaybackStatus.STATUS_NOTREADY:
	        					break;
	        			}
	        		} break;
	        		case MSG_GETDURATION : {
	        			get_duration();
	        		} break;
	        		case MSG_SET_PLAYANDPAUSE_ICONTOPAUSE : {
	        			play.setImageResource(R.drawable.dnla_video_controll_stop_f);
	        		} break;
	        		case MSG_SET_PLAYANDPAUSE_ICONTOPLAY: {
	        			play.setImageResource(R.drawable.dnla_video_controll_play_f);
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
	        			if(isMediaPlayerPrepared) {
	        				int i = msg.arg1;
	        				time_bar.setProgress(i);
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
	        		case HandlerControlerVariable.PROGRESS_CHANGED:
	        			set_progress_change();
	        			break;
	        		case HandlerControlerVariable.GET_DURATION:
		        			get_duration();
		        		break;
	        		case HandlerControlerVariable.SET_DIVX_METADATA_SUBTITLE_INFO:
		        			set_Subtitle_MetaData_Info();
		        		break;
	        		case HandlerControlerVariable.SET_DIVX_METADATA_AUDIO_INFO:
		        			set_Audio_MetaData_Info();
		            	break;
	        		case HandlerControlerVariable.HIDE_MOVIE_BANNER:
		        			animateHideBanner();
		        		break;
	        		case HandlerControlerVariable.HIDE_LAWRATE_METADATA:
							hide_lawrate_metadata_info();
						break;
	        		case HandlerControlerVariable.HIDE_LEFT_CORN_INFO:
		        			//common_ui.setVisibility(View.INVISIBLE);
		        		break;
	        		case HandlerControlerVariable.DELAY_SHOW_BANNER:
		        			if(!isMediaplayIdle)
							{	
								bannerShowTimeOutControl(MOVIE_BANNER, TimerDelay.delay_4s);
								showCurrentInfo();
							}
		        		break;
	        		case HandlerControlerVariable.MEDIAPLAY_INIT:
		        			media_play_init(filePathArray[currIndex]);
		        		break;
	        		case HandlerControlerVariable.HIDE_PICTURE_SIZE_UI:
		        			//PictureSize.setVisibility(View.INVISIBLE);
		        		break;
	        		case HandlerControlerVariable.MEDIA_PLAYER_START:
		        			if(!mPlayer.isPlaying())
		        				mPlayer.start();
							play.setImageResource(R.drawable.au_play);
		        		break;
	        		case HandlerControlerVariable.MESSAGE_DISMISS:
		        			if(short_msg != null)
		        				short_msg.dismiss();
		        			if(long_msg != null)
		        				long_msg.dismiss();
		        		break;
	        		case HandlerControlerVariable.HIDE_POPUP_MESSAGE:
		        			//msg_hint.dismiss();
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
		if(isMediaPlayerPrepared)
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
			duration.setText(String.format("/%02d:%02d:%02d", Hour, Minute, Second));
			time_bar.setProgress(0);
		}
	}
	
	private void hide_lawrate_metadata_info()
	{
		//common_ui.setVisibility(View.INVISIBLE);
		isShowChapterMetaData = false;
	}
	
	private void hide_picture_size_ui()
	{
		//PictureSize.setVisibility(View.INVISIBLE);
	}
	
	private void hide_all()
	{
		hideController(MOVIE_BANNER);
		hide_lawrate_metadata_info();
		hide_picture_size_ui();
		
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
		if(isMediaPlayerPrepared)
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
				time_bar.setProgress(i);
				
				i /= 1000;
				int minute = i / 60;
				int hour = minute / 60;
				int second = i % 60;
				minute %= 60;
				hasPlay.setText(String.format("%02d:%02d:%02d", hour, minute, second));
			}
		}
	}
	
	private void adjustMetaNamePosition()
	{
		//common_ui.setVisibility(View.INVISIBLE);
	}
	
	private void adjustMetaInfoPosition()
	{
		if (MovieBannerView.getVisibility() == View.INVISIBLE)
		{
			//common_ui.setY(1005);
		}
		else
		{
			//common_ui.setY(780);
		}
	}
	
	private void adjustPicSizePosition()
	{
		if (MovieBannerView.getVisibility() == View.INVISIBLE)
		{
			//PictureSize.setY(982);
		}else
		{
			//PictureSize.setY(780);
		}
	}

	private void set_Subtitle_MetaData_Info()
	{
		if(isMediaPlayerPrepared)
		{
			int[] currSubtitleInfo = mPlayer.getSubtitleInfo(curr_subtitle_stream_num);
			
			if(SPU_ENABLE == 1)
			{	
				//common_ui.setVisibility(View.VISIBLE);
				//common_ui.setText(" "+"Subtitle"+curr_subtitle_stream_num+" ");
			}else if (SPU_ENABLE == 0)
			{
				//common_ui.setVisibility(View.VISIBLE);
				//common_ui.setText(" "+"Subtitle off"+" ");
			}
			
			adjustMetaInfoPosition();
			
			hide_meta_data_delay(TimerDelay.delay_4s);
		}
	}
	
	private void set_Audio_MetaData_Info()
	{
		if(isMediaPlayerPrepared)
		{
			//common_ui.setText(" "+"Audio Tracks: " + curr_audio_stream_num+" ");
			//common_ui.setVisibility(View.VISIBLE);
			
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
			
			hide_meta_data_delay(TimerDelay.delay_4s);
		}
	}
	
	private void captureIntent()
    {
		Intent intent= getIntent();
    	currIndex = intent.getIntExtra("initPos", 0);
    	filePathArray = intent.getStringArrayExtra("filelist");
    	
    	startFromIntent();
    }
    
    private void setAudioSpdifOutput(int mode)
    {
    	mTVService.setAudioSpdifOutput(mode);
    	
    	hideController(MOVIE_BANNER);
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
		if(curr_subtitle_stream_num <= subtitle_num_Stream)
		{
			SPU_ENABLE = 1;
			
			mPlayer.setSubtitleInfo(curr_subtitle_stream_num, SPU_ENABLE, textEncoding, textColor, fontSize);			
		}else
		{
			SPU_ENABLE = 0;
			curr_subtitle_stream_num = subtitle_num_Stream;
			mPlayer.setSubtitleInfo(curr_subtitle_stream_num, SPU_ENABLE, textEncoding, textColor, fontSize);
		}
		
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

	private void showController(int id){
		switch(id)
		{
		case MOVIE_BANNER:
		if(MovieBannerView != null && sView.isShown()&& fileTitleArray != null)
		{
			MovieBannerView.setVisibility(View.VISIBLE);
		}
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
        		if(!isMediaplayIdle)
        		{
        			boolean execError = false;
        			try
        			{
        				handler.sendEmptyMessage(HandlerControlerVariable.PROGRESS_CHANGED);
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
        		}
			}
		};
		if(timer != null)
			timer.schedule(task_getCurrentPositon, 0, TimerDelay.delay_100ms);
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
	
	private void bannerShowTimeOutControl(final int id, int delay)
	{
		if(id == MOVIE_BANNER)
			animateShowBanner();
		else
			showController(id);
		
		if(task_hide_controler != null)
		{
			task_hide_controler.cancel();
			task_hide_controler = null;
		}

		task_hide_controler = new TimerTask(){
			public void run() {
				switch(id)
				{
				case MOVIE_BANNER:
					handler.sendEmptyMessage(HandlerControlerVariable.HIDE_MOVIE_BANNER);
					if(task_getCurrentPositon != null)
					{
						task_getCurrentPositon.cancel();
						task_getCurrentPositon = null;
					}
				break;
				default:
				break;
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

    private void media_play_init(String path)
    {
    	play.requestFocus();
		play.setImageResource(R.drawable.au_pause);
		
		if(task_getCurrentPositon != null)
		{
			task_getCurrentPositon.cancel();
			task_getCurrentPositon = null;
		}
		
		mPlayer.stop();
		mPlayer.reset();
		isMediaplayIdle = true;
		mPlayer.setOnPreparedListener(videoPreparedListener);
		mPlayer.setOnCompletionListener(videoCompletionListener);
		mPlayer.setOnInfoListener(VideoInfoListener);
		mPlayer.setOnErrorListener(ErrorListener);
		try {
			mPlayer.setDataSource(path);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();			
			PopupErrorMessageShow(DmrVideoPlayerActivity.this.getResources().getString(R.string.msg_unsupport), TimerDelay.delay_2s);		
		} catch (IOException e) {
			e.printStackTrace();
			PopupErrorMessageShow(DmrVideoPlayerActivity.this.getResources().getString(R.string.msg_unsupport), TimerDelay.delay_2s);
		}
		
		play.setImageResource(R.drawable.au_play);
		
		try {
			mPlayer.prepareAsync();
		} catch (IllegalStateException e) {
			e.printStackTrace();
			PopupErrorMessageShow(DmrVideoPlayerActivity.this.getResources().getString(R.string.msg_unsupport), TimerDelay.delay_2s);
			return;
		}
		
		mPlayer.setDisplay(sView.getHolder());
		
		isforward = false;
		forward_press = 1;
		isrewind = false;
		rewind_press = 1;
		
		isAudio_Error = false;
		isOnPlay = true;
    }
    
    
    private void setPicSize()
    {
    	switch(selected_idx)
    	{
    	case 0:
    		mTVService.setAspectRatio(TvManager.SCALER_RATIO_PANORAMA);
    		//spinner_bar_value.setText(R.string.picture_super_live);
    		selected_idx++;
    		break;
    	case 1:
    		mTVService.setAspectRatio(TvManager.SCALER_RATIO_POINTTOPOINT);
    		//spinner_bar_value.setText(R.string.picture_dot_by_dot);
    		selected_idx++;
    		break;
    	case 2:
    		mTVService.setAspectRatio(TvManager.SCALER_RATIO_BBY_NORMAL);
    		//spinner_bar_value.setText(R.string.picture_normal);
    		selected_idx++;
    		break;
    	case 3:
    		mTVService.setAspectRatio(TvManager.SCALER_RATIO_BBY_ZOOM);
    		//spinner_bar_value.setText(R.string.picture_zoom);
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
				//f_pause();
				f_playAndPause();
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
				f_seek(seekPos);	//Seek where
			} else if(intent.getAction().equals(WANTPLAY_ACTION)) {
				Bundle bundle = intent.getExtras();
				String cmd = bundle.getString("cmd");
				if(cmd.equals("Video")) {
					//do nothing, just like I'm start
				} else {
					//stop onNewIntent
					stopNewIntent = true;
					resetMediaPlayer();
    		    	isOnPlay = false;
    				DmrVideoPlayerActivity.this.finish();
    				stopNewIntent = false;
				}
			}
		}
		
	} 
	
	public void normalPlay() {
		mPlayer.fastforward(0);
	}
	
	public int commandToPlay() {
		if(isFirstStart) {	//not first start
			firstStart();
			if(isFirstStart) {	//not start
				isOnPlay = false;
				isOnNotNormalPlay = false;
				return 0;
			} else {	//start
				isOnPlay = true;
				isOnNotNormalPlay = false;
				return 1;
			}
		}
		
		if(checkAndRequestAF()) {//start
			if(isOnPlay) {
				if(isOnNotNormalPlay) {
					sfIndex = swIndex = ffIndex = fwIndex = -1;
					normalPlay();
				} else {
					mPlayer.start();
				}
			} else {
				if(isOnNotNormalPlay) {
					sfIndex = swIndex = ffIndex = fwIndex = -1;
					mPlayer.start();
					normalPlay();
				} else {
					mPlayer.start();
				}
			}
			
			isOnNotNormalPlay = false;
			isOnPlay = true;
			
			return 2;
		} else {
			return 3;
		}
	}
	
	public int commandToPause() {
		if(isFirstStart) {	//not first start
			Log.v(tag, "not first start!");
			isOnPlay = false;
			return 0;
		}
		if(!isOnPlay) {
			Log.e(tag, "already onPause");
			return 1;
		}
		
		doAbandonAF();
		sfIndex = swIndex = ffIndex = fwIndex = -1;
		mPlayer.pause();
		isOnPlay = false;
		return 2;
	}
	
	public void f_play() {
		handler.sendEmptyMessage(MSG_SHOWBANNER);
		handler.sendEmptyMessage(MSG_SET_PLAYANDPAUSE_FOCUS);
		int result_commandToPlay = -1; 
		result_commandToPlay = commandToPlay();
		//update UI
		switch(result_commandToPlay) {
			case 0 :{	//not start yet
				handler.sendEmptyMessage(MSG_SET_PLAYANDPAUSE_ICONTOPLAY);
				Message msg = handler.obtainMessage();
				msg.what = MSG_SET_PLAYBACKSTATUS;
				msg.arg1 = PlaybackStatus.STATUS_NOTREADY;
				handler.sendMessage(msg);
			}break;
			case 1 :{	//start
				handler.sendEmptyMessage(MSG_SET_PLAYANDPAUSE_ICONTOPAUSE);
				Message msg = handler.obtainMessage();
				msg.what = MSG_SET_PLAYBACKSTATUS;
				msg.arg1 = PlaybackStatus.STATUS_PLAY;
				handler.sendMessage(msg);
			}break;
			case 2 :{	//start
				handler.sendEmptyMessage(MSG_SET_PLAYANDPAUSE_ICONTOPAUSE);
				Message msg = handler.obtainMessage();
				msg.what = MSG_SET_PLAYBACKSTATUS;
				msg.arg1 = PlaybackStatus.STATUS_PLAY;
				handler.sendMessage(msg);
			}break;
			case 3 :
				break;
			default :
				break;
		}
	}
	
	public void f_pause() {
		handler.sendEmptyMessage(MSG_SHOWBANNER);
		handler.sendEmptyMessage(MSG_SET_PLAYANDPAUSE_FOCUS);
		int result_commandToPause = -1;
		result_commandToPause = commandToPause();
		switch(result_commandToPause) {
			case 0:
				break;
			case 1:
				break;
			case 2: {
				handler.sendEmptyMessage(MSG_SET_PLAYANDPAUSE_ICONTOPLAY);
				Message msg = handler.obtainMessage();
				msg.what = MSG_SET_PLAYBACKSTATUS;
				msg.arg1 = PlaybackStatus.STATUS_PAUSE;
				handler.sendMessage(msg);
			}break;
			case 3:
				break;
			default:
				break;
		}
	}
	
	public void f_playAndPause() {
		handler.sendEmptyMessage(MSG_SHOWBANNER);
		handler.sendEmptyMessage(MSG_SET_PLAYANDPAUSE_FOCUS);
		
		int result_commandToPlay = -1;
		int result_commandToPause = -1;
		
		if(isOnPlay) {
			if(isOnNotNormalPlay) {
				result_commandToPlay = commandToPlay();
			} else {
				result_commandToPause = commandToPause();
			}
		} else {
			result_commandToPlay = commandToPlay();
		}
		
		//update UI
		switch(result_commandToPlay) {
			case 0 :{	//not start yet
				handler.sendEmptyMessage(MSG_SET_PLAYANDPAUSE_ICONTOPLAY);
				Message msg = handler.obtainMessage();
				msg.what = MSG_SET_PLAYBACKSTATUS;
				msg.arg1 = PlaybackStatus.STATUS_NOTREADY;
				handler.sendMessage(msg);
			}break;
			case 1 :{	//start
				handler.sendEmptyMessage(MSG_SET_PLAYANDPAUSE_ICONTOPAUSE);
				Message msg = handler.obtainMessage();
				msg.what = MSG_SET_PLAYBACKSTATUS;
				msg.arg1 = PlaybackStatus.STATUS_PLAY;
				handler.sendMessage(msg);
			}break;
			case 2 :{	//start
				handler.sendEmptyMessage(MSG_SET_PLAYANDPAUSE_ICONTOPAUSE);
				Message msg = handler.obtainMessage();
				msg.what = MSG_SET_PLAYBACKSTATUS;
				msg.arg1 = PlaybackStatus.STATUS_PLAY;
				handler.sendMessage(msg);
			}break;
			case 3 :
				break;
			default :
				break;
		}
		
		switch(result_commandToPause) {
			case 0:
				break;
			case 1:
				break;
			case 2: {
				handler.sendEmptyMessage(MSG_SET_PLAYANDPAUSE_ICONTOPLAY);
				Message msg = handler.obtainMessage();
				msg.what = MSG_SET_PLAYBACKSTATUS;
				msg.arg1 = PlaybackStatus.STATUS_PAUSE;
				handler.sendMessage(msg);
			}break;
			case 3:
				break;
			default:
				break;
		}
	}
	
	public int commandToStop() {
		if(isMediaPlayerPrepared) {
			mPlayer.stop();
			isMediaPlayerPrepared = false;
			swIndex = sfIndex = ffIndex = fwIndex = -1;
			isOnPlay = false;
			isOnNotNormalPlay = false;
			isFirstStart = true;
			return 0;
		} else {
			return 1;
		}
	}
	
	public void f_Stop() {
		int result_commandToStop = -1;
		result_commandToStop = commandToStop();
		switch(result_commandToStop) {
			case 0:
				handler.sendEmptyMessage(MSG_SET_PLAYANDPAUSE_ICONTOPLAY);
				Message msg = handler.obtainMessage();
				msg.what = MSG_SET_PLAYBACKSTATUS;
				msg.arg1 = PlaybackStatus.STATUS_NOTREADY;
				handler.sendMessage(msg);
				break;
			case 1:
				break;
		}
	}
	
	public int commandToSlowForward() {
		if(!isOnPlay) {
			commandToPlay();
			if(isOnPlay) {
				return 0;	//start
			} else {
				return 1;	//do not update UI
			}
		} else { 
			if(checkAndRequestAF()) {
				isOnNotNormalPlay = true;
				sfIndex++;
				swIndex = ffIndex = fwIndex = -1;
				if(sfIndex >= sfRate.length) {
					sfIndex = 0;
				}
				//no api
				//mPlayer.slowforward(sfRate[sfIndex]);
				return 2;	//slowForward
			} else {
				return 3;	//do not update UI
			}
		}
	}
	
	public void f_slowForward() {
		handler.sendEmptyMessage(MSG_SHOWBANNER);
		handler.sendEmptyMessage(MSG_SET_SLOWFORWARD_FOCUS);
		int result_commandToSlowForward = -1;
		result_commandToSlowForward = commandToSlowForward();
		switch(result_commandToSlowForward) {
			case 0: {	
				handler.sendEmptyMessage(MSG_SET_PLAYANDPAUSE_ICONTOPAUSE);
				Message msg = handler.obtainMessage();
				msg.what = MSG_SET_PLAYBACKSTATUS;
				msg.arg1 = PlaybackStatus.STATUS_PLAY;
				handler.sendMessage(msg);
			} break;
			case 1:	
				break;
			case 2: { 
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
			} break;
			case 3:
				break;
			default :
				break;
		}
	}
	
	public int commandToFastForward() {
		if(!isOnPlay) {
			commandToPlay();
			if(isOnPlay) {
				return 0;	//normal play
			} else {
				return 1;	//do not update UI
			}
		} else {
			if (checkAndRequestAF()) {
				isOnNotNormalPlay = true;
				ffIndex++;
				swIndex = sfIndex = fwIndex = -1;
				if (ffIndex >= ffRate.length) {
					ffIndex = 0;
				}
				mPlayer.fastforward(ffRate[ffIndex]);
				return 2;	//fastForward
			} else {
				return 3;	//do not update UI
			}
		} 
	}
	
	public void f_fastForward() {
		handler.sendEmptyMessage(MSG_SHOWBANNER);
		handler.sendEmptyMessage(MSG_SET_FASTFORWARD_FOCUS);
		int result_commandToFastForward = commandToFastForward();
		switch(result_commandToFastForward) {
			case 0: {
				handler.sendEmptyMessage(MSG_SET_PLAYANDPAUSE_ICONTOPAUSE);
				Message msg = handler.obtainMessage();
				msg.what = MSG_SET_PLAYBACKSTATUS;
				msg.arg1 = PlaybackStatus.STATUS_PLAY;
				handler.sendMessage(msg);
			} break;
			case 1:
				break;
			case 2: {
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
			} break;
			case 3:
				break;
			default :
				break;
		}
	}
	
	public int commandToFastBackward() {
		if(!isOnPlay) {
			commandToPlay();
			if(isOnPlay) {
				return 0;	//normal play
			} else {
				return 1;	//still pause
			}
		} else {
			if (checkAndRequestAF()) {
				isOnNotNormalPlay = true;
				fwIndex++;
				swIndex = sfIndex = ffIndex = -1;
				if (fwIndex >= fwRate.length) {
					fwIndex = 0;
				}
				mPlayer.fastrewind(fwRate[fwIndex]);
				return 2;
			} else {
				return 3;
			}
		}
	}
	
	public void f_fastBackward() {
		handler.sendEmptyMessage(MSG_SHOWBANNER);
		handler.sendEmptyMessage(MSG_SET_FASTBACKWARD_FOCUS);
		int result_commandToFastBackward = commandToFastBackward();
		switch(result_commandToFastBackward) {
			case 0 : {
				handler.sendEmptyMessage(MSG_SET_PLAYANDPAUSE_ICONTOPAUSE);
				Message msg = handler.obtainMessage();
				msg.what = MSG_SET_PLAYBACKSTATUS;
				msg.arg1 = PlaybackStatus.STATUS_PLAY;
				handler.sendMessage(msg);
			} break;
			case 1 :
				break;
			case 2 : {
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
			} break;
			case 3 :
				break;
			default :
				break;
		}
	}
	
	public int commandToSlowBackward() {
		if(!isOnPlay) {
			commandToPlay();
			if(isOnPlay) {
				return 0;	//normal play
			} else {
				return 1;	//do not update UI
			}
		} else {
			if(checkAndRequestAF()) {
				isOnNotNormalPlay = true;
				swIndex++;
				fwIndex = sfIndex = ffIndex = -1;
				if (swIndex >= swRate.length) {
					swIndex = 0;
				}
				//no api
				//mPlayer.slowrewind(swRate[swIndex]);	//try to slowBackward
				return 2;	//slowForward
			} else {
				return 3;	//do not update UI
			}
		}
	}
	
	public void f_slowBackward() {
		handler.sendEmptyMessage(MSG_SHOWBANNER);
		handler.sendEmptyMessage(MSG_SET_SLOWBACKWARD_FOCUS);
		int result_commandToSlowBackward = -1; 
		result_commandToSlowBackward = commandToSlowBackward();
		switch(result_commandToSlowBackward) {
			case 0 : {
				handler.sendEmptyMessage(MSG_SET_PLAYANDPAUSE_ICONTOPAUSE);
				Message msg = handler.obtainMessage();
				msg.what = MSG_SET_PLAYBACKSTATUS;
				msg.arg1 = PlaybackStatus.STATUS_PLAY;
				handler.sendMessage(msg);
			} break;
			case 1 :
				break;
			case 2 : {
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
			} break;
			case 3 :
				break;
			default :
				break;
		}
	}
	
	public int commandToSeek(int seekPos) {
		if (seekPos < 0) {
			Log.e(tag, "Error in commandToSeek");
			return 0;
		}
		if(!isOnPlay) {
			commandToPlay();
		}
		
		if(isOnPlay) {
			time_bar.setProgress(seekPos);	//OK go seek
			sendBroadCastPosition(seekPos);
			isSeekFromCommand = true;
			mPlayer.seekTo(seekPos);
			return 1;
		} else {
			int position = time_bar.getProgress();	//no just ... tell now position
			sendBroadCastPosition(position);
			isSeekFromCommand = false;
			return 2;
		}
	}
	
	public void f_seek(int seekPos) {
		handler.sendEmptyMessage(MSG_SHOWBANNER);
		
		commandToSeek(seekPos);
	}
	
	private void animateShowBanner(){
		if(!MovieBannerView.isShown())
		{
			MovieBannerView.setVisibility(View.VISIBLE);
			MovieBannerView.clearAnimation();
			TranslateAnimation TransAnim;
			TransAnim = new TranslateAnimation(0.0f,0.0f,banner_h,0.0f);	
			TransAnim.setDuration(bannerAnimTime);
			MovieBannerView.startAnimation(TransAnim);
		}
	}
	
	private void animateHideBanner(){
		if(MovieBannerView.isShown()) {
			MovieBannerView.clearAnimation();
			TranslateAnimation TransAnim;
			TransAnim = new TranslateAnimation(0.0f,0.0f,0.0f,banner_h);	
			TransAnim.setDuration(bannerAnimTime);
			TransAnim.setAnimationListener(new hiderBannerListener());
			MovieBannerView.startAnimation(TransAnim);
		}
	}
	
	public void showBanner() {
		if(!isBannerAbleShown) {	// because menu exists
			//do message clean operate
			handler.removeMessages(MSG_SHOWBANNER);
			handler.sendEmptyMessage(MSG_HIDEBANNER);
			isBannerStillShown = false;
			return ;
		}
		//when banner is shown , start update time and start calculate to hide banner
		//give a flag isBannerStillShown to indicate that banner is still seen;
		animateShowBanner();
		isBannerStillShown = true;
		//startTaskUpdateTime();
		startTaskWaitBannerToHide();
	}
	
	public void hideBanner() {
		animateHideBanner();
		isBannerStillShown = false;
		//stopTaskUpdateTime();
		stopTaskWaitBannerToHide();
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
				if(!isMediaplayIdle)
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
						if(isSeekFromCommand || isSeekFromUser) {
							long currentMillis = System.currentTimeMillis();
							if(lastSeekComplete == 0) {
								lastSeekComplete = currentMillis;
								return ;
							}
							Log.v(tag, "time minus --------" + Math.abs(currentMillis - lastSeekComplete));
							if(Math.abs(currentMillis - lastSeekComplete) < DELAY_SEEK) {
								return ;
							}
							isSeekFromCommand = isSeekFromUser = false;
							lastSeekComplete = 0;
						} else {
							Log.v(tag, "Get Nower pos i  " + i);
							Message msg = handler.obtainMessage();
							msg.what = MSG_UPDATEPROGRESSBAR;
							msg.arg1 = i;
							Log.v(tag, "Get Nower pos i  " + i);
							handler.sendMessage(msg);
							sendBroadCastPosition(i);
							return ;
						}
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
					f_playAndPause();
				} break;
				case R.id.c_slowforward: {	////////////////////////////////////////////////////////////////////////////////
					f_slowForward();
				} break;
				case R.id.c_slowbackward: {
					f_slowBackward();
				} break;
				case R.id.c_fastforward: {
					f_fastForward();
				} break;
				case R.id.c_fastbackward: {
					f_fastBackward();
				} break;
				case R.id.play_picturesize : {
					//handler.sendEmptyMessage(MSG_SHOWBANNER);
					//handler.sendEmptyMessage(MSG_SET_PICTURESIZE_FOCUS);
				} break;
				case R.id.main : {
					if(MovieBannerView.getVisibility() == View.INVISIBLE) {
						handler.sendEmptyMessage(MSG_SHOWBANNER);
					} else {
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
	
	private OnSeekBarChangeListener seekListen = new OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			if(fromUser) {
				if(!isOnPlay) {
					commandToPlay();
				}
				if(!isOnPlay) {
					return ;
				}
				if(checkAndRequestAF()) {
					
					seekBar.setProgress(progress);
					progress /= 1000;
					int minute = progress / 60;
					int hour = minute / 60;
					int second = progress % 60;
					minute %= 60;
					Log.v(tag, "seeking to progress--- " + progress);
					mPlayer.seekTo(progress);
					hasPlay.setText(String.format("%02d:%02d:%02d", hour, minute, second));
				}
			}
			
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			isSeekFromUser = true;
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			//isSeekFromUser = false;
			int progress = seekBar.getProgress();
			Log.v(tag, "seeking onStopTrackingTouch!++++" + progress);
			mPlayer.seekTo(progress);
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
        nowAudioFocus = result;
		if(result == AudioManager.AUDIOFOCUS_REQUEST_FAILED){
			handler.sendEmptyMessage(HandlerControlerVariable.MSG_HINT_NOTGETAUDIOFOCUS);
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
		if(nowAudioFocus != AudioManager.AUDIOFOCUS_LOSS) {
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
    			commandToPause();
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            	Log.v(tag, "AUDIOFOCUS_GAIN");
            	commandToPlay(); 
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            	Log.v(tag, "AUDIOFOCUS_LOSS");
            	commandToPause();  
            }   
        }  
    };
    
    public void firstStart() {
    	if(isMediaplayIdle) {
    		return ;
    	}
    	if(!isMediaPlayerPrepared && !isMediaplayIdle) {
    		Log.v(tag, "not Prepared");
    		//mPlayer.prepareAsync();
    		return ;
    	}
    	if(isFirstStart && isMediaPlayerPrepared) {
    		if(checkAndRequestAF()) {
				mPlayer.start();
				Log.v(tag, "first start oK!");
				handler.sendEmptyMessage(MSG_GETDURATION);
				startTaskUpdateTime();
				//can get meta info about this media
				//getAudioTrackInfo();
				//getSubtitleInfo();
				SPU_ENABLE = 0;
				isOnPlay = true;
				isOnNotNormalPlay = false;
				isFirstStart = false;
				return;
			}
    	}
    	return ;
    }
    
    
    public void sendBroadCastPosition(int position) {
    	Intent sendToServer = new Intent("com.rtk.dmr.position.broadcast");
		sendToServer.putExtra("currentTime", position);	//Tell DMC
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
    	duration = (TextView)MovieBannerView.findViewById(R.id.time_end);
    	hasPlay = (TextView)MovieBannerView.findViewById(R.id.time_now);
    	playStatus = (ImageView)findViewById(R.id.play_status);
    	
    	menu = (ImageView) findViewById(R.id.main_menu);
    	
    	
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
    	time_bar.setOnSeekBarChangeListener(seekListen);
    	
    	
    	sView.getHolder().setKeepScreenOn(true);
        sView.getHolder().addCallback(new SurfaceListener());
    }
    
    public void initBetweenOP() {
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		setContentView(R.layout.video_player_dmr);
		findViews();
		setListeners();
		initBetweenOP();
		//other
		super.onConfigurationChanged(newConfig);
	}
	
	public void resetMediaPlayer() {
		Log.v(tag, "MediaPlayer reset bb");
		setMediaPlayerListenersNull();
		mPlayer.reset();
		isMediaPlayerPrepared = false;
		isMediaplayIdle = true;	//only in reset
		isOnPlay = false;
		isFirstStart = true;
		Log.v(tag, "MediaPlayer reset ee");
	}
	
	public void releaseMediaPlayer() {
		Log.v(tag, "MediaPlayer release begin");
		mPlayer.release();
		mPlayer = null;
		Log.v(tag, "MediaPlayer release end");
	}
	
	public void startFromIntent() {
		if(mPlayer == null) {
			mPlayer = new MediaPlayer();
		}
		
		resetMediaPlayer();
    	
    	handler.sendEmptyMessage(MSG_SET_PLAYANDPAUSE_ICONTOPLAY);
    	Message msg = handler.obtainMessage();
    	msg.what = MSG_SET_PLAYBACKSTATUS;
    	msg.arg1 = PlaybackStatus.STATUS_NOTREADY;
    	handler.sendMessage(msg);
    	
    	//About progressBAR
    	sendBroadCastPosition(0);
    	time_bar.setProgress(0);
    	isSeeking = false;
    	needSeek = false;
    	hasPlay.setText("00:00:00");
    	
    	getAudioService();
    	
    	new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				setMediaPlayerListeners();
				try {
					mPlayer.setDataSource(filePathArray[currIndex]);
					isMediaplayIdle = false;
					mPlayer.prepareAsync();
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
			}
        }.start();
	}
	
	public void f_onMenu() {	//for menu show and hide
		
	}
	
	public void getAudioService() {
		if(am == null) {
			am = (AudioManager) getSystemService(this.AUDIO_SERVICE);
		}
	}
}


