package com.rtk.mediabrowser;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Color;
import android.graphics.Typeface;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextPaint;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnHoverListener;
import android.view.View.OnKeyListener;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.realtek.DataProvider.AbstractDataProvider;
import com.realtek.DataProvider.DeviceFileDataPrivider;
import com.realtek.DataProvider.FileFilterType;
import com.realtek.Utils.AsyncInfoLoader;
import com.realtek.Utils.FileInfo;
import com.realtek.Utils.MimeTypes;
import com.realtek.Utils.AsyncInfoLoader.TimeInfoCallback;

import android.app.TvManager;

public class VideoBrowser extends Activity {
	// add by star_he
	private final int LISTNUM = 11;
	private final int LASTLISTID = 10;
	// add by star_he end

	private String TAG = "VideoBrowser";
	private ListView videolist;
	private TextView fileName;
	private TextView topPath;
	private SurfaceView sView;
	private MediaPlayer mPlayer;
	private ProgressBar video_bar;
	private TextView video_hasplayed;
	private TextView video_duration;
	private TextView video_duration_tag;
	private TextView date;
	private ImageView folder_image;
	private TextView mTopFileIndexInfoTextView;
	private ProgressBar loadingIcon;
	private TextView guide_enter;
	private ImageView btn_repeat;
	private ImageView focusView;

	private TextView topDividLine;
	private ContentResolver m_ContentMgr = null;
	private View guide = null;
	private View resume_play_help_bar = null;

	private Animation ad = null;

	private MediaApplication mediaApp = null;
	private Resources resourceMgr = null;

	private SharedPreferences mPerferences = null;

	private final int VIDEO_PLAY = 0;
	private final int PROGRESS_CHANGED = 1;
	private final int UPDATE_TIME = 2;
	private final int HIDE_QUICK_MENU = 3;
	private final int HIDE_POPUP_MESSAGE = 4;
	private final int FOCUS_MSG = 5;
	private final int UNFOCUS_MSG = 6;
	private final int HIDE_SHORT_MSG = 7;
	private final int START_LOADING = 8;
	private final int STOP_LOADING = 9;
	private final int MSG_REFRESH_TIMER = 10;

	private boolean isCreateRTMediaPlayer = false;

	private int FinishVideoBrowser = 1;
	private int UsbRemovedSignal = 2;
	private int StopPlay = 3;

	private int UsbRemoved = 1;

	private int Max = 0;
	private int Minute = 0;
	private int Hour = 0;
	private int Second = 0;

	int fromX, fromY, toX, toY;
	private int animIndex = 0;

	private String hour = null, min = null, day_of_week = null, month = null;

	private boolean isFocused = false;
	private boolean isFirstRun = true;
	public int dirLevel = -1;
	public ArrayList<String> parentPath = new ArrayList<String>();
	private Path_Info curPathInfo;

	private AbstractDataProvider mDataProvider = null;
	private MimeTypes mMimeTypes = null;

	private String rootPath;
	private TimerTask task_getduration = null;
	private TimerTask task_hide_quickmenu = null;
	private TimerTask task_message_time_out = null;
	private Timer timer = null;

	private Handler handler;

	public static int mBrowserType = 0;

	private int currentFocusItem;

	public Activity mContext = this;
	public BookMark mVideoBookMark = null;

	private TvManager mTv = null;

	private boolean isRight2Left = false;
	private boolean isOnResumeState = false;
	/********* Video Recode **********************/
	private int totalLen;
	private String videoPath;
	private String mTopFileIndexInfoStr;
	private static final String DATAFORMAT = "dd MMM yyyy";

	class Video_Index {
		int VideoIndex;
		int positon_in_itemList;
	}

	ArrayList<Video_Index> mVideoIndex = null;
	/******** Page setting *********************/
	ImageView page_up = null;
	ImageView page_down = null;
	int pagenum = 0;
	ArrayList<FileInfo> pageItems = null;

	Drawable videoImgs[] = new Drawable[4];

	FileListAdapter simpleAdapter = null;
	ArrayList<FileInfo> listItems = null;
	int firstVisibleItem;
	int lastVisibleItem;
	int lastnum = 0;
	public static int index = 0;	// 0 ~ LASTLISTID
	private int idx = 0;

	private String devicePath = "";

	private boolean foucsVisiable = false;
	private boolean isFakeCnt = true;

	public static boolean changeIndex = false;
	private int lastIndex = 0;

	private ArrayList<String> folderPath = new ArrayList<String>();
	/******** Quick Menu Setting ***************/
	private QuickMenu quickMenu = null;
	private QuickMenuVideoAdapter quickMenuAdapter = null;

	private final static int SET_REPEAT_MODE = 0;
	private final static int SET_SLEEP_TIMER = 1;
	private final static int SET_TV_APP = 2;
	private final static int SET_SYS_SETTING = 3;

	private int qm_focus_item = 0;

	private int mSleepTimeHour = 0, mSleepTimeMin = 0;

	private int repeatIndex = 0;
	private int playPosition = -1;

	/******** Define key ***********/
	private final int Stop_Key = 47;
	private final int Quick_Key = 45;

	/********** Confirm Message **********************/
	int resumeIndex = -1;

	private ConfirmMessage long_msg = null;
	private ConfirmMessage short_msg = null;

	private ComfirmDailog short_dailog = null;
	private PopupMessage msg_hint = null;
	/*********** Usb Controler ************************/
	private UsbController mUsbCtrl = null;
	static Handler loading_icon_system_handle = null;
	private int mActivityPauseFlag = 0;
	private boolean pageDownFlag = false;
	private boolean pageUpFlag = false;

	private float listTop = 0;
	private float listH = 0;

	int screenHeight = 0;
	int screenWeight = 0;
	AudioManager am;

	public class SurfaceListener implements SurfaceHolder.Callback {

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			if (mPlayer != null)
					mPlayer.setDisplay(sView.getHolder());
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
		}

	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "VideoBrowser onCreate");
		super.onCreate(savedInstanceState);
		am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mediaApp = (MediaApplication) getApplication();
		isRight2Left = MediaBrowserConfig
				.getRight2Left(getApplicationContext());
		if (isRight2Left)
			setContentView(R.layout.video_browser_a);
		else
			setContentView(R.layout.video_browser);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		Intent intent = getIntent();
		mBrowserType = intent.getIntExtra("browserType", 0);
		if (mBrowserType == 0) {
			rootPath = "/storage/udisk/";
		}
		if (mTv == null) {
			mTv = (TvManager) getSystemService("tv");
		}
		m_ContentMgr = getApplicationContext().getContentResolver();
		fromX = 0;
		fromY = 0;

		index = 0;

		resourceMgr = getResources();
		mediaApp = (MediaApplication) getApplication();

		screenHeight = mediaApp.getScreenHeight();
		screenWeight = mediaApp.getScreenWidth();

		mPlayer = mediaApp.getMediaPlayer();

		videolist = (ListView) findViewById(R.id.video_list);
		fileName = (TextView) findViewById(R.id.dir_or_video_name);
		topPath = (TextView) findViewById(R.id.video_path_top);
		sView = (SurfaceView) findViewById(R.id.video);
		video_bar = (ProgressBar) findViewById(R.id.video_progress);
		video_hasplayed = (TextView) findViewById(R.id.video_hasplayed);
		video_duration = (TextView) findViewById(R.id.video_duration);
		video_duration_tag = (TextView) findViewById(R.id.video_duration_tag);
		date = (TextView) findViewById(R.id.date);
		btn_repeat = (ImageView) findViewById(R.id.imgRepeat);
		focusView = (ImageView) findViewById(R.id.focus);

		guide = (View) findViewById(R.id.bg_bottom);
		guide_enter = (TextView) guide.findViewById(R.id.guide_enter);

		resume_play_help_bar = (View) findViewById(R.id.resume_play_help_bar);

		topDividLine = (TextView) findViewById(R.id.top_divid_line);

		page_up = (ImageView) findViewById(R.id.video_list_page_up);
		page_down = (ImageView) findViewById(R.id.video_list_page_down);
		addPageListener();

		folder_image = (ImageView) findViewById(R.id.image_folder);
		mTopFileIndexInfoTextView = (TextView) findViewById(R.id.video_list_index_info);

		loadingIcon = (ProgressBar) findViewById(R.id.loadingIcon);
		loadingIcon.setVisibility(View.INVISIBLE);

		// sView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		sView.getHolder().setKeepScreenOn(true);
		sView.getHolder().addCallback(new SurfaceListener());

		mPerferences = PreferenceManager.getDefaultSharedPreferences(this);

		currentFocusItem = -1;

		mMimeTypes = mediaApp.getMimeTypes();

		curPathInfo = new Path_Info();
		String path = getFilesDir().getPath();
		String fileName = path.concat("/VideoBookMark.bin");
		mVideoBookMark = mediaApp.getBookMark(fileName); // new
															// BookMark(fileName);

		videolist.setOnItemClickListener(itemClickListener);
		videolist.setOnItemSelectedListener(itemSelectedListener);
		videolist.setOnKeyListener(new ListKeyListener());

		initVideoImg();

		setListTouch();

		long_msg = new ConfirmMessage(mContext);
		int wh = (int) (678.0 / 1920.0 * mediaApp.getScreenWidth());
		int hh = (int) (226.0 / 1080.0 * mediaApp.getScreenHeight());
		short_msg = new ConfirmMessage(mContext, wh, hh);
		short_dailog = new ComfirmDailog(mContext, wh, hh);
		msg_hint = new PopupMessage(mContext);

		getDataProvider(rootPath);
		getFirstFileList(rootPath);
		videoListGetFocus();

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case VIDEO_PLAY:
					// Thread mPrepareVideo = new Thread() {
					// @Override
					// public void run() {
					if (videoPath != null) {
						media_play_init(videoPath);
						Log.v(TAG, "the index = " + index);
						simpleAdapter.notifyDataSetChanged(index);
						loadingIcon.setVisibility(View.VISIBLE);
					}
					// }
					// };
					// mPrepareVideo.start();
					break;
				case PROGRESS_CHANGED:
					if (mPlayer != null) {
						int i = mPlayer.getCurrentPosition();
						video_bar.setProgress(i);
						i /= 1000;
						int minute = i / 60;
						int hour = minute / 60;
						int second = i % 60;
						minute %= 60;
						video_hasplayed.setText(String.format("%02d:%02d:%02d",
								hour, minute, second));
					}
					break;
				case UPDATE_TIME:
					int index = msg.getData().getInt("index");
					String time = msg.getData().getString("time");
					updateList(index, time);
					break;
				case HIDE_QUICK_MENU:
					quickMenu.dismiss();
					break;
				case HIDE_POPUP_MESSAGE:
					msg_hint.dismiss();
					break;
				case FOCUS_MSG:
					// remove animation
					animIndex++;
					if (!foucsVisiable) {
						focusView.setImageDrawable(getApplicationContext()
								.getResources().getDrawable(
										R.drawable.list_common_item_focus));
						focusView.setVisibility(View.VISIBLE);
						setFocusLayout(toX, toY);
						foucsVisiable = true;
					} else {
						setFocusLayout(toX, toY);
					}
					break;
				case UNFOCUS_MSG:
					// remove animation
					if (foucsVisiable) {
						focusView.setImageResource(R.drawable.blank);
						focusView.setVisibility(View.INVISIBLE);
						foucsVisiable = false;
					}
					break;
				case HIDE_SHORT_MSG:
					short_dailog.dismiss();
					break;
				case START_LOADING:
					loadingIcon.setVisibility(View.VISIBLE);
					break;
				case STOP_LOADING:
					if (loadingIcon != null) {
						loadingIcon.setVisibility(View.INVISIBLE);
					}
					break;
				case MSG_REFRESH_TIMER: {
					quickMenuAdapter.notifyDataSetChanged();
				}
				default:
					break;
				}
				super.handleMessage(msg);
			}
		};

		loading_icon_system_handle = new Handler() {
			private synchronized void setLoadingThreadNum(int flag) {
				if (flag == 0) {
					loading_num++;
				} else if (flag == 1) {
					loading_num--;
				}
			}

			private synchronized int getLoadingThreadNum() {
				return loading_num;
			}

			private int loading_num = 0;
			boolean hasAnimStarted = false;

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0: {
					setLoadingThreadNum(msg.what);
					break;
				}
				case 1: {
					setLoadingThreadNum(msg.what);
					break;
				}
				default:
					break;
				}
				if (getLoadingThreadNum() > 0) {

					loadingIcon.setVisibility(View.VISIBLE);
				} else if (getLoadingThreadNum() == 0) {
					if (loadingIcon != null) {
						loadingIcon.setVisibility(View.INVISIBLE);

					}
				} else
					Log.e(TAG, "loading icon error");
				super.handleMessage(msg);
			}
		};
		initUsbCtl();

		topDividLine.post(new Runnable() {
			public void run() {
				toX = topDividLine.getLeft();
				listTop = topDividLine.getTop();
				Log.v("adklfjalskdfjlaklistH", "" + listH);
				Log.v("adklfjalskdfjlaklistH", "" + listTop);
			}
		});

		videolist.post(new Runnable() {
			public void run() {
				listH = videolist.getMeasuredHeight() / (float) LISTNUM;
			}
		});
	}

	private void setFocusLayout(int x, int y) {
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) focusView
				.getLayoutParams();
		lp.leftMargin = x;
		lp.topMargin = y;
		focusView.setLayoutParams(lp);
	}

	private void cancelLoadTime() {
		AsyncInfoLoader.opQueue(0, null);
	}

	private void initUsbCtl() {
		initReceiver();
		mUsbCtrl = new UsbController(this);
		OnUsbCheckListener usbCheckListener = new OnUsbCheckListener() {

			@Override
			public void OnUsbCheck() {
				// TODO Auto-generated method stub
				switch (mUsbCtrl.getDirection()) {
				case UsbController.PLUG_OUT: {
					if (mUsbCtrl.GetUsbNum() == 0)// deviceNum is 0
					{
						setResult(UsbRemoved);
						finish();
						return;
					}
					if (parentPath.get(dirLevel) == rootPath) {
						cancelLoadTime();
						simpleAdapter.clearSelected();
						getDataProvider(rootPath);
						showInList();
						if (index == 0)
							onTrickItemSelected(index);
						else
							videolist.setSelection(0);
					} else {
						File file = new File(parentPath.get(dirLevel));

						if (file == null || !file.exists()) {
							setResult(UsbRemoved);
							finish();
							return;
						}
					}
				}
					break;
				case UsbController.PLUG_IN: {
					if (mUsbCtrl.GetUsbNum() == 0)// in case of plug in and plug
													// out happens at one time
					{
						setResult(UsbRemoved);
						finish();
						return;
					}
					if (parentPath.get(dirLevel) == rootPath) {
						cancelLoadTime();
						simpleAdapter.clearSelected();
						getDataProvider(curPathInfo.getLastLevelPath());
						showInList();
						if (currentFocusItem == 0)
							onTrickItemSelected(currentFocusItem);
						else
							videolist.setSelection(0);
					}
				}
					break;
				}
			}

			@Override
			public void OnUsbCheck(String path, int direction) {
				// TODO Auto-generated method stub
				if (mBrowserType != 0) {
					return; // do nothing on dmrMode
				}
				String mpath = "/storage/udisk" + devicePath;
				switch (direction) {
				case UsbController.PLUG_OUT: {
					if (parentPath.get(dirLevel) == rootPath) {
						cancelLoadTime();
						simpleAdapter.clearSelected();
						getDataProvider(rootPath);
						showInList();
						if (index == 0)
							onTrickItemSelected(index);
						else
							videolist.setSelection(0);
					} else if (mpath.equals(path)) {
						setResult(UsbRemoved);
						finish();
						return;
					}

				}
				}
			}

		};
		OnUsbLoadingListener usbLoadingListener = new OnUsbLoadingListener() {

			int loadNum = 0;

			@Override
			public void OnStartLoading() {
				handler.sendEmptyMessage(START_LOADING);
				loadNum++;
			}

			@Override
			public void OnStopLoading() {
				if (loadNum > 1) {
					loadNum--;
					return;
				}
				handler.sendEmptyMessage(STOP_LOADING);
			}

		};
		mUsbCtrl.setOnUsbCheckListener(usbCheckListener);
		mUsbCtrl.setOnUsbLoadingListener(usbLoadingListener);
	}

	private void showPageIcon(boolean last, boolean next) {
		if (last)
			page_up.setVisibility(View.VISIBLE);
		else
			page_up.setVisibility(View.INVISIBLE);
		if (next)
			page_down.setVisibility(View.VISIBLE);
		else
			page_down.setVisibility(View.INVISIBLE);
	}

	public void onTrickItemSelected(int position) {
		currentFocusItem = position + lastnum;
		index = position;
		curPathInfo.setLastLevelFocus(position);
		curPathInfo.setLastFirstVisibleItem(lastnum);
		setFileIndxTextView(currentFocusItem);
		simpleAdapter.notifyFocused(index);

		if (mDataProvider.GetFileTypeAt(currentFocusItem) == FileFilterType.DEVICE_FILE_VIDEO) {
			videoSelected(currentFocusItem);
		} else if (mDataProvider.GetFileTypeAt(currentFocusItem) == FileFilterType.DEVICE_FILE_DIR
				|| mDataProvider.GetFileTypeAt(currentFocusItem) == FileFilterType.DEVICE_FILE_DEVICE) {
			folderSelected(currentFocusItem);
		}
	}
	
	public void onTrickPlayVideo(int position) {
		currentFocusItem = position + lastnum;
		index = position;
		curPathInfo.setLastLevelFocus(position);
		curPathInfo.setLastFirstVisibleItem(lastnum);
		setFileIndxTextView(currentFocusItem);
		simpleAdapter.notifyDataSetChanged(position);
		videoSelected(currentFocusItem);
	}

	public void playNext() {
		Log.v(TAG, "playNext");
		int pos = videolist.getSelectedItemPosition();
		int itemnums = listItems.size();
		while(pos + lastnum < itemnums){
			if (pos + lastnum == itemnums - 1) {
				videolist.setSelection(0);
				simpleAdapter.notifyDataSetChanged();
				lastnum = 0;
				pos = 0;
				firstVisibleItem = 0;
				if (itemnums > LISTNUM)
					lastVisibleItem = LASTLISTID;
				else
					lastVisibleItem = itemnums - 1;
				if (mDataProvider.GetFileTypeAt(0) == FileFilterType.DEVICE_FILE_VIDEO){
					onTrickPlayVideo(0);
					break;
				}
			} else {
				pos++;
				if (itemnums <= LISTNUM || pos < lastVisibleItem) {
					videolist.setSelection(pos);
					simpleAdapter.notifyDataSetChanged();
					if (mDataProvider.GetFileTypeAt(pos) == FileFilterType.DEVICE_FILE_VIDEO){
						onTrickPlayVideo(pos);
						break;
					}
				}else{
					lastnum++;
					videolist.setSelection(LASTLISTID);
					simpleAdapter.notifyDataSetChanged();
					if (mDataProvider.GetFileTypeAt(LASTLISTID + lastnum) == FileFilterType.DEVICE_FILE_VIDEO){
						onTrickPlayVideo(LASTLISTID);
						break;
					}
				}
				
			}
			pos = videolist.getSelectedItemPosition();
		}
		
	}

	private void getDataProvider(String path) {
		if (path != null) {
			if (mBrowserType == 0) {
				mDataProvider = new DeviceFileDataPrivider(path,
						FileFilterType.DEVICE_FILE_DIR
								| FileFilterType.DEVICE_FILE_VIDEO, -1, 0,
						mMimeTypes);
			}
			mDataProvider.sortListByType();
		}
	}

	@Override
	public void onPause() {
		Log.v(TAG, "VideoBrowser onPause");

		super.onPause();
		mActivityPauseFlag = 1;
		handler.removeMessages(VIDEO_PLAY);

		if (timer != null) {
			timer.cancel();
			timer = null;
		}

		if (mPlayer != null) {
			mPlayer.stop();
			mPlayer.reset();
			mPlayer.setOnPreparedListener(null);
			mPlayer.setOnCompletionListener(null);
			mPlayer.setDisplay(null);
			// mPlayer.release();
			// mPlayer = null;
			// System.gc();
		}
		// AsyncInfoLoader.isQuit(0);
	}

	@Override
	public void onStop() {
		Log.v(TAG, "VideoBrowser onStop");
		super.onStop();
	}

	@Override
	public void onStart() {
		Log.v(TAG, "VideoBrowser onStart");
		super.onStart();
	}

	@Override
	public void onResume() {
		Log.v(TAG, "VideoBrowser onResume");

		mActivityPauseFlag = 0;
		mUsbCtrl.RegesterBroadcastReceiver();

		mTv.setAndroidMode(1);

		if (timer == null)
			timer = new Timer(true);
		if(idx != 0) {	
			int newPos = idx - lastnum;
			if (newPos >= 0 && newPos <= LISTNUM) {
				index = newPos;
			} else if (newPos < 0) {
				index = 0;
			}
			idx = 0;
		}
		videolist.setSelection(index);
		onTrickItemSelected(index);

		new Thread(new Runnable() {
			@Override
			public void run() {
				int tmp_index = mPerferences.getInt("repeatIndex", -1);
				if (tmp_index == -1) {
					Editor editor = mPerferences.edit();
					editor.putInt("repeatIndex", repeatIndex);
					editor.commit();
				} else {
					repeatIndex = tmp_index;
				}
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (mActivityPauseFlag != 1) {

					int mins = getSleepTimeValue();
					mSleepTimeHour = mins / 60;
					mSleepTimeMin = mins % 60;
					if (quickMenu != null) {
						if (quickMenu.isShowing()) {
							handler.sendEmptyMessage(MSG_REFRESH_TIMER);
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
		if (quickMenuAdapter == null)
			quickMenuAdapter = new QuickMenuVideoAdapter(this);
		quickMenuAdapter.notifyDataSetChanged();

		check_repeat_model(repeatIndex);

		getInitTimer();
		super.onResume();
	}

	private int getSleepTimeValue() {
		int sethour = Settings.Global.getInt(m_ContentMgr, "SetTimeHour", 0);
		int setmin = Settings.Global.getInt(m_ContentMgr, "SetTimeMinute", 0);
		int setsec = Settings.Global.getInt(m_ContentMgr, "SetTimeSecond", 0);
		int totalmin = Settings.Global.getInt(m_ContentMgr, "TotalMinute", 0);
		Log.d("RTK_DEBUG", "SetTimeHour:" + sethour + ",SetTimeMinute:"
				+ setmin + ",SetTimeSec:" + setsec + ",TotalMinute:" + totalmin);
		Date curDate = new Date(System.currentTimeMillis());
		int curhours = curDate.getHours();
		int curminutes = curDate.getMinutes();
		int curSecs = curDate.getSeconds();
		Date setData = new Date(curDate.getYear(), curDate.getMonth(),
				curDate.getDate(), sethour, setmin, setsec);

		int diftime = 0;
		if (curDate.after(setData) && totalmin != 0) {
			diftime = totalmin
					- ((curhours * 60 + curminutes) - (sethour * 60 + setmin));
		}
		return diftime;
	}

	@Override
	public void onRestart() {
		Log.v(TAG, "VideoBrowser onRestart");
		super.onRestart();

		int pos = videolist.getSelectedItemPosition();
		int position = pos + lastnum;
		if (mDataProvider.GetFileTypeAt(position) == FileFilterType.DEVICE_FILE_VIDEO) {
			if (timer == null)
				timer = new Timer(true);

			resumeIndex = mVideoBookMark.findBookMark(mDataProvider
					.GetTitleAt(position));

			if (resumeIndex >= 0
					&& (mVideoBookMark.bookMarkList.get(resumeIndex).isDivxVODFile != 1
							|| mVideoBookMark.bookMarkList.get(resumeIndex).drmStatus == DivxParser.NAV_DIVX_DRM_AUTHORIZED
							|| mVideoBookMark.bookMarkList.get(resumeIndex).drmStatus == DivxParser.NAV_DIVX_DRM_NONE || mVideoBookMark.bookMarkList
							.get(resumeIndex).drmStatus == DivxParser.NAV_DIVX_DRM_RENTAL_AUTHORIZED))
				PopupMessageShow(
						mContext.getResources().getString(
								R.string.resumePlay_hint), TimerDelay.delay_6s);
		}

		if (quickMenu != null && quickMenu.isShowing())
			hide_quick_menu_delay(TimerDelay.delay_6s);
	}

	@Override
	public void onDestroy() {
		Log.v(TAG, "VideoBrowser onDestroy");

		mediaApp.releaseMediaPlayer();

		super.onDestroy();

		if (curPathInfo != null)
			curPathInfo.cleanLevelInfo();

		AsyncInfoLoader.isQuit(0);
		mUsbCtrl.UnRegesterBroadcastReceiver();
		unregisterReceiver(mPlayReceiver);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {
			if (resultCode == FinishVideoBrowser) {
				this.finish();
			} else if (resultCode == UsbRemovedSignal) {
				setResult(UsbRemoved);
				this.finish();
			} else if (resultCode == StopPlay) {
				for (int i = 0; i < totalLen; i++) {
					if (mDataProvider.GetFileTypeAt(i) == FileFilterType.DEVICE_FILE_VIDEO) {
						if (data.getStringExtra("FileName").compareTo(
								mDataProvider.GetTitleAt(i)) == 0) {
							idx = i;
							break;
						}
					}
				}

			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.e(TAG, "keyCode = " + keyCode);
		switch (keyCode) {
		case 220:// STEREO/DUAL for L4300
		case 82:// MENU
		{
			PopupMessageShow(
					mContext.getResources().getString(
							R.string.toast_not_available),
					R.drawable.message_box_bg2, 100, 315, Gravity.LEFT
							| Gravity.BOTTOM, 18, 18, TimerDelay.delay_4s);
		}
			return true;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			return true;
		case KeyEvent.KEYCODE_BACK: {
			dismissLoading();
			if (mPlayer != null)
				mPlayer.reset();
			if (parentPath.get(dirLevel).equals(rootPath))
				return super.onKeyDown(keyCode, event);
			
			if (curPathInfo.dirLevel > 0
					&& !parentPath.get(dirLevel).equals(rootPath)) {
				parentPath.remove(dirLevel);
				folderPath.remove(dirLevel);
				dirLevel--;
				if (dirLevel == 0)
					devicePath = "";
				simpleAdapter.clearSelected();
				
				videolist.requestFocus();
				// int pos = videolist.getSelectedItemPosition();
				//curPathInfo.backToLastLevel();
				/*
				int focusNum = (curPathInfo.getLastLevelFocus()) % LISTNUM;
				int quotient = (curPathInfo.getLastLevelFocus()) / LISTNUM;
				if (quotient > 1)
					lastnum = (quotient - 1) * LISTNUM + focusNum + 1;
				else if (quotient == 1)
					lastnum = focusNum + 1;
				else
					lastnum = 0;*/
				curPathInfo.backToLastLevel();
				getDataProvider(curPathInfo.getLastLevelPath());
				showInList();
				lastnum = curPathInfo.getLastFirstVisibleItem();
				Log.v(TAG, "onKeyBack lastnum = " + lastnum);
				simpleAdapter.notifyDataSetChanged(curPathInfo.getLastLevelFocus());
				Log.v(TAG, "curPathInfo.getLastLevelFocus() =" + curPathInfo.getLastLevelFocus());
				onTrickItemSelected(curPathInfo.getLastLevelFocus());
				videolist.setSelection(curPathInfo.getLastLevelFocus());
				/*
				if (lastnum > 0) {
					videolist.setSelection(LASTLISTID);
					onTrickItemSelected(LASTLISTID);
				} else {
					videolist.setSelection(curPathInfo.getLastLevelFocus());
					onTrickItemSelected(curPathInfo.getLastLevelFocus());
				}*/
				return true;
			} else if (!isFocused) {
				videolist.requestFocus();
				return true;
			}
		}
			break;
		case 232: // for L4300 KeyEvent.KEYCODE_PLAY
		{
			int position = videolist.getSelectedItemPosition() + lastnum;
			if (mDataProvider.GetFileTypeAt(position) == FileFilterType.DEVICE_FILE_VIDEO)
				playVideo(position);
		}
			break;
		case 231: // for L4300 KeyEvent.KEYCODE_STOP
		case 257: // DISPLAY KEY in REALTECK RCU
		case KeyEvent.KEYCODE_K: // As [Stop] key
		{
			if (isCreateRTMediaPlayer) {
				mVideoBookMark.removeBookMark(0);
				mVideoBookMark.writeBookMark();
				mPlayer.execResetDivxState();
			}
		}
			break;
		// case 111: //for L4300 KeyEvent.KEYCODE_ESCAPE
		// case 178: //INPUT KEY in REALTECK RCU
		case KeyEvent.KEYCODE_ESCAPE:
		case KeyEvent.KEYCODE_J: // As [Exit] key
		{
			Intent intent = new Intent(VideoBrowser.this, MediaBrowser.class);
			startActivity(intent);
			this.finish();
		}
			break;
		case KeyEvent.KEYCODE_B: {
			int pos = index + lastnum;
			videoPath = mDataProvider.GetDataAt(pos);
			Message hMsg = new Message();
			hMsg.what = VIDEO_PLAY;
			handler.sendMessageDelayed(hMsg, TimerDelay.delay_1s);
		}
			break;
		case 227: // for L4300 KeyEvent.KEYCODE_QUICK_MENU:
		case KeyEvent.KEYCODE_Q: {
			if (quickMenu == null) {
				// createQuickMenu()
				quickMenu = new QuickMenu(this, quickMenuAdapter);
				quickMenu.setAnimationStyle(R.style.QuickAnimation);
				OnItemClickListener quickmenuItemClickListener = new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						if (position != 1 && position != 0) {
						setQuickMenuItem(position, true);
						quickMenuAdapter.notifyDataSetChanged();
						}
						hide_quick_menu_delay(TimerDelay.delay_6s);
					}
				};
				OnKeyListener quickmenuKeyListener = new OnKeyListener() {

					@Override
					public boolean onKey(View v, int keyCode, KeyEvent event) {
						// TODO Auto-generated method stub
						if (event.getAction() == KeyEvent.ACTION_DOWN) {
							switch (keyCode) {
							case KeyEvent.KEYCODE_DPAD_RIGHT: {
								if (qm_focus_item != SET_TV_APP
										&& qm_focus_item != SET_SYS_SETTING)
									setQuickMenuItem(qm_focus_item, true);
								quickMenuAdapter.notifyDataSetChanged();
								hide_quick_menu_delay(TimerDelay.delay_6s);
								return true;
							}
							case KeyEvent.KEYCODE_DPAD_LEFT: {
								if (qm_focus_item != SET_TV_APP
										&& qm_focus_item != SET_SYS_SETTING)
									setQuickMenuItem(qm_focus_item, false);
								quickMenuAdapter.notifyDataSetChanged();
								hide_quick_menu_delay(TimerDelay.delay_6s);
								return true;
							}
							case KeyEvent.KEYCODE_Q:
							case 227: // KeyEvent.KEYCODE_QUICK_MENU:
							case KeyEvent.KEYCODE_BACK: {
								quickMenu.dismiss();
							}
								break;
							case 231: // for L4300 KeyEvent.KEYCODE_STOP:
							case KeyEvent.KEYCODE_K: {
								if (isCreateRTMediaPlayer) {
									if (mDataProvider
											.GetFileTypeAt(currentFocusItem) == FileFilterType.DEVICE_FILE_VIDEO) {
										mVideoBookMark
												.removeBookMark(mDataProvider
														.GetTitleAt(currentFocusItem));
										mVideoBookMark.writeBookMark();
									}
									mPlayer.execResetDivxState();
									Toast.makeText(
											getApplicationContext(),
											getResources()
													.getString(
															R.string.msg_resume_remove_hint),
											Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(
											getApplicationContext(),
											getResources()
													.getString(
															R.string.msg_no_resume_point_hint),
											Toast.LENGTH_SHORT).show();
								}
							}
								break;
							case KeyEvent.KEYCODE_MENU: {
								quickMenu.dismiss();
								PopupMessageShow(
										mContext.getResources().getString(
												R.string.toast_not_available),
										R.drawable.message_box_bg2, 100, 300,
										Gravity.LEFT | Gravity.BOTTOM, 18, 18,
										TimerDelay.delay_4s);

								return false;
							}
							case KeyEvent.KEYCODE_DPAD_UP: {
								ListView quickMenuContent = quickMenu
										.getListView();
								int position = quickMenuContent
										.getSelectedItemPosition();
								if (position == 0) {
									quickMenuContent
											.setSelection(quickMenuContent
													.getCount() - 1);
								}
								hide_quick_menu_delay(TimerDelay.delay_6s);
								return false;
							}
							case KeyEvent.KEYCODE_DPAD_DOWN: {
								ListView quickMenuContent = quickMenu
										.getListView();
								int position = quickMenuContent
										.getSelectedItemPosition();
								if (position == quickMenuContent.getCount() - 1) {
									quickMenuContent.setSelection(0);
								}
								hide_quick_menu_delay(TimerDelay.delay_6s);
								return false;
							}
							}
						}
						return false;

					}

				};
				OnItemSelectedListener quickmenuItemSelectedListener = new OnItemSelectedListener() {
					ListView lv = quickMenu.getListView();

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						qm_focus_item = position;
						for (int i = 0; i < lv.getCount(); i++) {
							quickMenuAdapter.setVisibility(i, View.INVISIBLE);
						}
						if (position != SET_TV_APP
								&& position != SET_SYS_SETTING)
							quickMenuAdapter.setVisibility(position,
									View.VISIBLE);
						quickMenuAdapter.notifyDataSetChanged();
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				};

				quickMenu.AddOnItemClickListener(quickmenuItemClickListener);
				quickMenu
						.AddOnItemSelectedListener(quickmenuItemSelectedListener);
				quickMenu.AddOnKeyClickListener(quickmenuKeyListener);
			}

			if (quickMenu.isShowing())
				quickMenu.dismiss();
			else {
				quickMenu.showQuickMenu(14, 14);

				hide_quick_menu_delay(TimerDelay.delay_6s);
			}
		}
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	private OnPreparedListener videoPreparedListener = new OnPreparedListener() {

		@Override
		public void onPrepared(MediaPlayer mp) {
			// TODO Auto-generated method stub

			loadingIcon.setVisibility(View.INVISIBLE);

			if (!isDivxVODFile()) {
				mPlayer.start();

				if (isMutiTitle()) {
					mPlayer.execPlayTitle(1);
				}

				sView.setVisibility(View.VISIBLE);
				folder_image.setVisibility(View.INVISIBLE);

				Max = mPlayer.getDuration();
				video_bar.setMax(Max);
				Max /= 1000;
				Minute = Max / 60;
				Hour = Minute / 60;
				Second = Max % 60;
				Minute %= 60;

				video_duration.setVisibility(View.VISIBLE);
				video_hasplayed.setVisibility(View.VISIBLE);
				video_duration_tag.setVisibility(View.VISIBLE);
				video_bar.setVisibility(View.VISIBLE);

				video_duration.setText(String.format("%02d:%02d:%02d", Hour,
						Minute, Second));
				video_hasplayed.setText("00:00:00");
				video_bar.setProgress(0);

				if (task_getduration != null) {
					task_getduration.cancel();
					task_getduration = null;
				}
				task_getduration = new TimerTask() {
					@Override
					public void run() {
						handler.sendEmptyMessage(PROGRESS_CHANGED);
					}
				};

				if (timer == null)
					timer = new Timer(true);
				timer.schedule(task_getduration, 0, TimerDelay.delay_100ms);
			} else {
				folder_image.setVisibility(View.VISIBLE);
				folder_image.setImageResource(R.drawable.image_mbw_prev_vod);
			}
		}
	};

	private OnCompletionListener videoCompletionListener = new OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer mp) {
			// TODO Auto-generated method stub
			video_hasplayed.setText(String.format("%02d:%02d:%02d", Hour,
					Minute, Second));
			video_bar.setProgress(Max);

			if (task_getduration != null)
				task_getduration.cancel();

			if (mPlayer != null) {
				mPlayer.stop();
				mPlayer.reset();
			}
			playNext();
		}

	};

	private OnInfoListener VideoInfoListener = new OnInfoListener() {

		@Override
		public boolean onInfo(MediaPlayer mp, int what, int extra) {
			return false;
		}

	};

	private OnErrorListener videoErrorListener = new OnErrorListener() {
		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			switch (what) {
			case MediaPlayer.MEDIA_ERROR_SERVER_DIED: {
				Log.e(TAG, "MediaServer died, finish mySelf!");
				mPlayer.reset();
				short_msg.setMessage(VideoBrowser.this.getResources()
						.getString(R.string.Media_Server_Die));
				short_msg.setButtonText(VideoBrowser.this.getResources()
						.getString(R.string.msg_yes));
				short_msg.left.setVisibility(View.INVISIBLE);
				short_msg.right.setVisibility(View.INVISIBLE);
				short_msg.confirm_bt.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						short_msg.dismiss();
						VideoBrowser.this.finish();
					}
				});
				short_msg.show();
			}
				break;
			default: {
				Log.e(TAG, "Get video error: " + what + ", ignore it!");
				mPlayer.reset();
				/*
				 * confirmMsg.setTitle(VideoBrowser.this.getResources().getString
				 * (R.string.msg_hint));
				 * confirmMsg.setMessage(VideoBrowser.this.getResources().
				 * getString(R.string.Video_Play_Error)+" "+what);
				 * confirmMsg.setButtonText
				 * (VideoBrowser.this.getResources().getString
				 * (R.string.msg_yes));
				 * confirmMsg.left.setVisibility(View.INVISIBLE);
				 * confirmMsg.right.setVisibility(View.INVISIBLE);
				 * confirmMsg.confirm_bt.setOnClickListener(new
				 * OnClickListener(){
				 * 
				 * @Override public void onClick(View arg0) { // TODO
				 * Auto-generated method stub confirmMsg.dismiss(); } });
				 * confirmMsg.show();
				 */
			}
				break;
			}
			return true;
		}
	};

	private OnItemSelectedListener itemSelectedListener = new OnItemSelectedListener() {

		public void onItemSelected(AdapterView<?> parent, View v, int position,
				long id) {
			Log.v(TAG,
					"itemSelectedListener, position = " + position
							+ "parent.getFirstVisiblePosition() = "
							+ parent.getFirstVisiblePosition());
			if (mActivityPauseFlag == 1) {
				Log.v(TAG, "VideoBrowser in paused state, return");
				return;
			}

			int pos = position + lastnum;
			index = position;
			setFileIndxTextView(pos);
			simpleAdapter.notifyFocused(index);
			simpleAdapter.notifyDataSetChanged(position);
			
			if (mDataProvider.GetFileTypeAt(pos) == FileFilterType.DEVICE_FILE_VIDEO) {
				videoSelected(pos);
			} else if (mDataProvider.GetFileTypeAt(pos) == FileFilterType.DEVICE_FILE_DIR
					|| mDataProvider.GetFileTypeAt(pos) == FileFilterType.DEVICE_FILE_DEVICE) {
				folderSelected(pos);
				// simpleAdapter.clearSelected();
				// simpleAdapter.notifyDataSetChanged();
				// dismissLoading();
			}
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	};

	private OnItemClickListener itemClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View v,
				final int position, long id) {
			dismissLoading();
			int pos = position + lastnum;
			index = position;

			if (mDataProvider.GetFileTypeAt(pos) == FileFilterType.DEVICE_FILE_VIDEO) {
				lastIndex  = index;
				playVideo(pos);
			} else if (mDataProvider.GetFileTypeAt(pos) == FileFilterType.DEVICE_FILE_DIR
					|| mDataProvider.GetFileTypeAt(pos) == FileFilterType.DEVICE_FILE_DEVICE) {
				String pathTitle = mDataProvider.GetTitleAt(pos);
				String header = parentPath.get(dirLevel);
				if (mDataProvider.GetFileTypeAt(pos) == FileFilterType.DEVICE_FILE_DEVICE) {
					if (isRight2Left)
						devicePath = mDataProvider.GetTitleAt(pos) + "/";
					else
						devicePath = "/" + mDataProvider.GetTitleAt(pos);
					folderPath.add("");
					if (mDataProvider.GetTitleAt(pos).equals(
							MediaApplication.internalStorage)
							&& mBrowserType == 0) {
						header = Environment.getExternalStorageDirectory()
								.getPath();
						pathTitle = "";
					}
				} else {
					String pathTag = "";
					for (int i = 1; i <= dirLevel; i++)
						pathTag += "/";
					if (isRight2Left)
						folderPath.add(mDataProvider.GetTitleAt(pos) + pathTag);
					else
						folderPath.add(pathTag + mDataProvider.GetTitleAt(pos));
				}

				cancelLoadTime();
				parentPath.add(header + pathTitle + "/");
				dirLevel++;
				getDataProvider(parentPath.get(dirLevel));
				curPathInfo.setLastLevelFocus(position);
        		curPathInfo.setLastFirstVisibleItem(lastnum);
        		Log.v(TAG, "onClick lastnum = " + lastnum);
				curPathInfo.addLevelInfo(parentPath.get(dirLevel));

				showInList();
				simpleAdapter.notifyDataSetChanged(0);

				if (mDataProvider.GetSize() == 0) {
					msg_hint.setMessage(mContext.getResources().getString(
							R.string.msg_noFile));
					if (isRight2Left)
						msg_hint.setMessageRight();
					else
						msg_hint.setMessageLeft();
					msg_hint.show();
					msg_hint.setFocusable(true);
					msg_hint.update();

					msg_hint.setOnDismissListener(new OnDismissListener() {

						@Override
						public void onDismiss() {
							dismissLoading();
							if (mPlayer != null)
								mPlayer.reset();
							if (curPathInfo.dirLevel > 0
									&& !parentPath.get(dirLevel).equals(
											rootPath)) {
								parentPath.remove(dirLevel);
								folderPath.remove(dirLevel);
								dirLevel--;
								if (dirLevel == 0)
									devicePath = "";
								curPathInfo.backToLastLevel();
								getDataProvider(curPathInfo.getLastLevelPath());
								showInList();
								lastnum = curPathInfo.getLastFirstVisibleItem();
								Log.v(TAG, "" + lastnum);
								simpleAdapter.notifyDataSetChanged(curPathInfo.getLastLevelFocus());
								Log.v(TAG, "curPathInfo.getLastLevelFocus() =" + curPathInfo.getLastLevelFocus());
								onTrickItemSelected(curPathInfo.getLastLevelFocus());
								videolist.setSelection(curPathInfo.getLastLevelFocus());
							} 
						}

					});
				} else if (mDataProvider.GetSize() > 0) {
					videolist.setSelection(0);
					if (mDataProvider.GetFileTypeAt(0) == FileFilterType.DEVICE_FILE_DIR)
						folderSelected(0);
					else if (mDataProvider.GetFileTypeAt(0) == FileFilterType.DEVICE_FILE_VIDEO)
						videoSelected(0);
				}
			}
		}
	};

	private void videoSelected(int position) {
		handler.removeMessages(VIDEO_PLAY);

		if (task_getduration != null)
			task_getduration.cancel();

		if (mPlayer != null) {
			mPlayer.stop();
			mPlayer.reset();
		}

		fileName.setText(getApplicationContext().getText(R.string.title) + ": "
				+ mDataProvider.GetTitleAt(position));

		guide_enter.setText(R.string.guide_play);

		// sView.setVisibility(INVISIBLE);
		folder_image.setVisibility(View.VISIBLE);
		folder_image.setImageResource(R.drawable.video_list_play);

		video_duration.setVisibility(View.INVISIBLE);
		video_hasplayed.setVisibility(View.INVISIBLE);
		video_duration_tag.setVisibility(View.INVISIBLE);
		video_bar.setVisibility(View.INVISIBLE);

		date.setVisibility(View.VISIBLE);

		get_file_time(position);

		videoPath = mDataProvider.GetDataAt(position);

		if (MediaBrowserConfig.HAVE_PREVIEW == false) {
			return;
		}
		Message hMsg = new Message();
		hMsg.what = VIDEO_PLAY;
		handler.sendMessageDelayed(hMsg, TimerDelay.delay_1s);
	}

	private void folderSelected(int position) {
		handler.removeMessages(VIDEO_PLAY);

		if (task_getduration != null)
			task_getduration.cancel();

		if (mPlayer != null) {
			mPlayer.stop();
			mPlayer.reset();
		}

		guide_enter.setText(R.string.guide_enter);

		// sView.setVisibility(INVISIBLE);
		folder_image.setVisibility(View.VISIBLE);
		video_duration.setVisibility(View.INVISIBLE);
		video_hasplayed.setVisibility(View.INVISIBLE);
		video_duration_tag.setVisibility(View.INVISIBLE);
		date.setVisibility(View.VISIBLE);
		video_bar.setVisibility(View.INVISIBLE);

		loadingIcon.setVisibility(View.INVISIBLE);

		if (mDataProvider.GetFileTypeAt(position) == FileFilterType.DEVICE_FILE_DIR) {
			get_file_time(position);
			folder_image.setImageResource(R.drawable.image_mbw_prev_folder);
			fileName.setText(getApplicationContext().getText(
					R.string.folder_exif)
					+ " " + mDataProvider.GetTitleAt(position));
		} else {
			if (mDataProvider.GetTitleAt(position).equals(
					MediaApplication.internalStorage))
				folder_image
						.setImageResource(R.drawable.video_listlist_internal);
			else
				folder_image.setImageResource(R.drawable.list_common_usb);
			fileName.setText(getApplicationContext().getText(
					R.string.device_exif)
					+ " " + mDataProvider.GetTitleAt(position));
		}
	}

	private void getFirstFileList(String path) {
		/*
		if (mDataProvider.isUsbPlugin()) {
			parentPath.add(path);
			if (path.equals(rootPath))
				folderPath.add("");
			dirLevel++;
			curPathInfo.addLevelInfo(path);
		} else {
			String header = Environment.getExternalStorageDirectory().getPath();
			parentPath.add(header + "/");
			folderPath.add("");
			dirLevel++;
			curPathInfo.addLevelInfo(header + "/");
			getDataProvider(header + "/");
		}
		showInList();
		videolist.setSelection(0);*/
		//old version
		parentPath.add(path);
		if (path.equals(rootPath))
			folderPath.add("");
		dirLevel++;	//0
		curPathInfo.addLevelInfo(path);	//0
		showInList();
		videolist.setSelection(0);
	}

	private void videoListGetFocus() {
		videolist.setAdapter(simpleAdapter);
		videolist.setSelection(curPathInfo.getLastLevelFocus());
		videolist.setSelected(true);
	}

	private void setFileIndxTextView(int pos) {
		/*
		 * if( mTopFileIndexInfoTextView!=null && mDataProvider != null &&
		 * mDataProvider.GetSize() >= 0) { if
		 * (mDataProvider.GetFileTypeAt(curPathInfo.getLastLevelFocus()) ==
		 * FileFilterType.DEVICE_FILE_DIR ||
		 * mDataProvider.GetFileTypeAt(curPathInfo.getLastLevelFocus()) ==
		 * FileFilterType.DEVICE_FILE_DEVICE ) {
		 * mTopFileIndexInfoTextView.setText(""); //Log.d(TAG,
		 * "do not show index for dir"); return; } else if
		 * (mDataProvider.GetFileTypeAt(curPathInfo.getLastLevelFocus()) ==
		 * FileFilterType.DEVICE_FILE_VIDEO) { if(mVideoIndex != null) { int
		 * totalNum = mVideoIndex.size(); for(int i = 0; i < totalNum; i++) {
		 * if(mVideoIndex.get(i).positon_in_itemList ==
		 * curPathInfo.getLastLevelFocus()) mTopFileIndexInfoStr =
		 * Integer.toString(mVideoIndex.get(i).VideoIndex, 10)+"/"+totalNum; } }
		 * } mTopFileIndexInfoTextView.setText(mTopFileIndexInfoStr); }
		 */

		if (MediaApplication.DEBUG)
			Log.d(TAG, "setFileIndxTextView" + curPathInfo.getLastLevelFocus());
		if (mTopFileIndexInfoTextView != null && mDataProvider != null) {
			if (mDataProvider.GetFileTypeAt(pos) == FileFilterType.DEVICE_FILE_DIR
					|| mDataProvider.GetFileTypeAt(pos) == FileFilterType.DEVICE_FILE_DEVICE) {
				mTopFileIndexInfoTextView.setText("");
				if (MediaApplication.DEBUG)
					Log.d(TAG, "do not show index for dir");
				return;
			}
			if (mDataProvider.GetSize() <= 0) {
				mTopFileIndexInfoStr = "0/0";
			} else {
				/*
				 * int dirnum = mDataProvider.getDirnum(); mTopFileIndexInfoStr
				 * = Integer.toString( curPathInfo.getLastLevelFocus() + 1 -
				 * dirnum, 10) + "/" + (mDataProvider.GetSize() - dirnum);
				 */
				int videonum = mDataProvider.GetSize() - mDataProvider.getDirnum();
				int dirnum = mDataProvider.getDirnum();
				mTopFileIndexInfoStr = Integer.toString(pos + 1 - dirnum, 10)
						+ "/" + Integer.toString(videonum, 10);
			}
			mTopFileIndexInfoTextView.setText(mTopFileIndexInfoStr);
		}
	}

	private void showInList() {
		index = 0;
		lastnum = 0;
		if (listItems == null) {
			listItems = mediaApp.getFileListItems();
		}
		listItems.clear();

		totalLen = mDataProvider.GetSize();
		if (totalLen == 0) {
			if (foucsVisiable) {
				handler.removeMessages(UNFOCUS_MSG);
				Message msg = handler.obtainMessage(UNFOCUS_MSG);
				handler.sendMessage(msg);
			}
		}
		if (totalLen >= MediaApplication.MAXFILENUM) {
			PopupMessageShow(
					(String) getApplicationContext().getText(
							R.string.maxfilenum), R.drawable.message_box_bg,
					260, 678, Gravity.CENTER, 0, 0, 5000);
		}
		if (MediaApplication.DEBUG)
			Log.e("VideoBrowser", "showInList: size=" + totalLen);
		if (mBrowserType == 1) {
			for (int i = 0; i < totalLen; i++) {

				FileInfo finfo = new FileInfo(mDataProvider.GetFileTypeAt(i),
						mDataProvider.GetFileAt(i),
						mDataProvider.GetTitleAt(i), mDataProvider.GetDataAt(i));
				listItems.add(finfo);
			}
		} else {
			for (int i = 0; i < totalLen; i++) {
				FileInfo finfo = new FileInfo(mDataProvider.GetFileTypeAt(i),
						mDataProvider.GetFileAt(i),
						mDataProvider.GetTitleAt(i), mDataProvider.GetFileAt(i)
								.getAbsolutePath());
				listItems.add(finfo);
			}
		}
		if (simpleAdapter == null) {
			simpleAdapter = new FileListAdapter(this, listItems, videolist);
			videolist.setAdapter(simpleAdapter);
		} else
			simpleAdapter.notifyDataSetChanged();
		firstVisibleItem = 0;
		if (listItems.size() > LISTNUM)
			lastVisibleItem = LASTLISTID;
		else
			lastVisibleItem = listItems.size() - 1;
		showPageIcon(listItems.size() > LISTNUM, listItems.size() > LISTNUM);
		if (isRight2Left)
			topPath.setText(folderPath.get(dirLevel) + devicePath);
		else
			topPath.setText(devicePath + folderPath.get(dirLevel));
	}

	private void updateList(int index, String time) {
		listItems.get(index).setTime(time);
		simpleAdapter.notifyDataSetChanged();
	}

	private void playVideo(int position) {
		if (msg_hint.isShowing())
			handler.sendEmptyMessage(HIDE_POPUP_MESSAGE);

		playPosition = position;
		resumeIndex = mVideoBookMark.findBookMark(mDataProvider
				.GetTitleAt(playPosition));
		if (resumeIndex < 0) {
			Log.v(TAG, "No bookmark, play directly");
			sendIntent(playPosition, -1);
		} else {
			if (!mTv.isDeviceActivated()
					&& mVideoBookMark.bookMarkList.get(resumeIndex).isDivxVODFile == 1
					&& mVideoBookMark.bookMarkList.get(resumeIndex).drmStatus != DivxParser.NAV_DIVX_DRM_AUTHORIZED
					&& mVideoBookMark.bookMarkList.get(resumeIndex).drmStatus != DivxParser.NAV_DIVX_DRM_NONE) {
				Log.v(TAG,
						"The device is not activated and The File is DIVX VOD file!");
				mVideoBookMark.removeBookMark(resumeIndex);
				mVideoBookMark.writeBookMark();
				sendIntent(playPosition, -1);
			} else {
				Log.v(TAG, "Find bookmark, resume play?");
				Log.e(TAG,
						"mVideoBookMark.bookMarkList.get(resumeIndex).drmStatus = "
								+ mVideoBookMark.bookMarkList.get(resumeIndex).drmStatus);

				if (mVideoBookMark.bookMarkList.get(resumeIndex).drmStatus == DivxParser.NAV_DIVX_DRM_RENTAL_AUTHORIZED) {
					sendIntent(playPosition, resumeIndex);
				} else {
					short_dailog.setMessage(resourceMgr
							.getString(R.string.msg_resumePlay));

					/*
					 * short_msg.confirm_bt.setOnTouchListener(new
					 * View.OnTouchListener(){
					 * 
					 * @Override public boolean onTouch(View v, MotionEvent
					 * event) { switch(event.getAction()) { case
					 * MotionEvent.ACTION_DOWN:
					 * if(short_msg.confirm_text.getText
					 * ().toString().compareTo(mContext
					 * .getResources().getString(R.string.msg_yes))==0)
					 * short_msg
					 * .confirm_text.setText(mContext.getResources().getString
					 * (R.string.msg_no)); else
					 * if(short_msg.confirm_text.getText
					 * ().toString().compareTo(mContext
					 * .getResources().getString(R.string.msg_no))==0)
					 * short_msg.
					 * confirm_text.setText(mContext.getResources().getString
					 * (R.string.msg_yes)); return true; default: break; }
					 * return false; }
					 * 
					 * });
					 */

					short_dailog.confirm_yes
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View arg0) {
									sendIntent(playPosition, resumeIndex);
									short_dailog.dismiss();
									handler.removeMessages(HIDE_SHORT_MSG);
								}

							});

					short_dailog.confirm_yes
							.setOnKeyListener(new OnKeyListener() {

								@Override
								public boolean onKey(View arg0, int keyCode,
										KeyEvent event) {
									if (event.getAction() == KeyEvent.ACTION_DOWN) {
										switch (keyCode) {
										case 232: // for L4300
													// KeyEvent.KEYCODE_PLAY
										{
											sendIntent(playPosition,
													resumeIndex);
											short_dailog.dismiss();
											handler.removeMessages(HIDE_SHORT_MSG);
										}
											break;
										default:
											break;
										}
									}

									return false;
								}

							});

					short_dailog.confirm_no
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View arg0) {
									mVideoBookMark.removeBookMark(resumeIndex);
									mVideoBookMark.writeBookMark();
									sendIntent(playPosition, -1);

									short_dailog.dismiss();
									handler.removeMessages(HIDE_SHORT_MSG);
								}

							});

					short_dailog.confirm_no
							.setOnKeyListener(new OnKeyListener() {

								@Override
								public boolean onKey(View arg0, int keyCode,
										KeyEvent event) {
									if (event.getAction() == KeyEvent.ACTION_DOWN) {
										switch (keyCode) {
										case 232: // for L4300
													// KeyEvent.KEYCODE_PLAY
										{
											mVideoBookMark
													.removeBookMark(resumeIndex);
											mVideoBookMark.writeBookMark();
											sendIntent(playPosition, -1);

											short_dailog.dismiss();
											handler.removeMessages(HIDE_SHORT_MSG);
										}
											break;
										default:
											break;
										}
									}

									return false;
								}

							});

					short_dailog.setOnDismissListener(new OnDismissListener() {

						@Override
						public void onDismiss() {
							guide.setVisibility(View.VISIBLE);
							resume_play_help_bar.setVisibility(View.INVISIBLE);
						}
					});

					if (isRight2Left) {
						short_dailog.setMessageRight();
						short_dailog.message.setWidth(678);
						short_dailog.message.setPadding(30, 1, 150, 1);
					} else {
						int ww;
						ww = (int) (678.0 / 1920.0 * mediaApp.getScreenWidth());
						short_dailog.setMessageLeft();
						short_dailog.message.setWidth(ww);
						int val1, val2, val3, val4;
						val1 = (int) (150.0 / 1920.0 * mediaApp
								.getScreenWidth());
						val2 = 1;
						val3 = (int) (30.0 / 1080.0 * mediaApp
								.getScreenHeight());
						val4 = 1;
						short_dailog.message.setPadding(val1, val2, val3, val4);
					}
					short_dailog.show();

					guide.setVisibility(View.INVISIBLE);
					resume_play_help_bar.setVisibility(View.VISIBLE);

					handler.sendEmptyMessageDelayed(HIDE_SHORT_MSG,
							TimerDelay.delay_60s);
				}
			}
		}
	}

	public class FileListAdapter extends BaseAdapter {
		private List<FileInfo> theItems;
		private AsyncInfoLoader asyncInfoLoader;
		private ListView listView;
		private LayoutInflater mInflater;
		private int selected = -1;
		private int focused = -1;
		private boolean fakeCnt = false;
		private boolean refresh = false;
		private int freshId = 0;

		public FileListAdapter(Context context, List<FileInfo> mData,
				ListView listView) {
			mInflater = LayoutInflater.from(context);
			this.theItems = mData;
			asyncInfoLoader = new AsyncInfoLoader(mTv);
			AsyncInfoLoader.isQuit(-1);
			this.listView = listView;
		}

		public void notifyDataSetChanged(int id) {
			selected = id;
			super.notifyDataSetChanged();
		}

		public void notifyDataSetChanged(boolean refresh, int id) {
			this.refresh = refresh;
			freshId = id;
			super.notifyDataSetChanged();
		}

		public void notifyFocused(int id) {
			focused = id;
			super.notifyDataSetChanged();
		}

		public void clearSelected() {
			selected = -1;
		}

		public void setCnt(int fir) {
			fakeCnt = true;
		}

		public void resetCnt() {
			fakeCnt = false;
		}

		@Override
		public int getCount() {

			if (theItems.size() > LISTNUM)
				return LISTNUM;
			else
				return theItems.size();

		}

		@Override
		public Object getItem(int position) {

			return null;

		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder tag = null;
			int ab_pos = position + lastnum;
			if(ab_pos >= theItems.size())
				return convertView;
			final FileInfo info = theItems.get(ab_pos);
			boolean isSelected = false;
			boolean isFocus = false;
			if (selected + lastnum == ab_pos || info.isPlay()) {
				isSelected = true;
			}
			if (focused + lastnum == ab_pos) {
				isFocus = true;
			}
			if (convertView == null) {
				tag = new ViewHolder();
				if (isRight2Left)
					convertView = mInflater.inflate(
							R.layout.audiocell_reversal, null);
				else
					convertView = mInflater.inflate(R.layout.audiocell, null);
				tag.imageView = (ImageView) convertView
						.findViewById(R.id.ItemImage);
				tag.title = (TextView) convertView.findViewById(R.id.ItemTitle);
				tag.time = (TextView) convertView.findViewById(R.id.ItemTime);
				convertView.setTag(tag);
			} else {
				tag = (ViewHolder) convertView.getTag();
			}

			if (info.getmFileType() == FileFilterType.DEVICE_FILE_DEVICE) {
				if (info.getFileName().equals(MediaApplication.internalStorage))
					tag.imageView.setImageDrawable(videoImgs[3]);
				else
					tag.imageView.setImageDrawable(videoImgs[0]);
			} else if (info.getmFileType() == FileFilterType.DEVICE_FILE_DIR)
				tag.imageView.setImageDrawable(videoImgs[1]);
			else if (info.getmFileType() == FileFilterType.DEVICE_FILE_VIDEO) {
				if (isSelected && theItems.get(ab_pos).getCanPlay() != -1) {
					tag.imageView.setImageDrawable(videoImgs[2]);
				} else
					tag.imageView.setImageDrawable(null);
			}
			if (isFocus) {
				tag.title.setEllipsize(TruncateAt.MARQUEE);
				// tag.title.setTextColor(Color.WHITE);
			} else {
				tag.title.setEllipsize(TruncateAt.END);
				// tag.title.setTextColor(Color.BLACK);
			}
			tag.title.setText(info.getFileName());

			// disabled for the gettotaltime api is not ready
			if (!(theItems.get(ab_pos).getTime() != null && theItems
					.get(ab_pos).getTime().equals("null"))
					&& mBrowserType != 1
					&& info.getmFileType() == FileFilterType.DEVICE_FILE_VIDEO) {
				TextView time = tag.time;
				String url = theItems.get(ab_pos).getPath();

				time.setTag(url);
				if (theItems.get(ab_pos).getTime().length() == 0) {
					Long cachedInt = asyncInfoLoader.loadTime(url, ab_pos,
							new TimeInfoCallback() {
								public void infoLoaded(Long times, String url,
										int pos) {
									try {
										if (times == null
												|| times.intValue() == -1
												|| times.intValue() == 0) {
											// if get total time funs return
											// null,it means this file can not
											// play
											theItems.get(pos).setTime("null");
											return;
										}
										// listItems.get(pos + pagenum
										// *12).setTime(Util.toSecondTime(times.longValue()));
										theItems.get(pos).setTime(
												Util.toSecondTime(times
														.longValue()));
										notifyDataSetChanged(true, pos);
									} catch (Exception e) {
										if (MediaApplication.DEBUG)
											Log.e("reload", "" + e.getMessage());
									}
								}
							});
					if (cachedInt == null) {
						time.setText("--:--:--");
					} else {
						time.setText(Util.toSecondTime(cachedInt.longValue()));
						// listItems.get(position + pagenum
						// *12).setTime(Util.toSecondTime(cachedInt.longValue()));
						theItems.get(ab_pos).setTime(
								Util.toSecondTime(cachedInt.longValue()));
					}
				} else {
					time.setText(theItems.get(ab_pos).getTime());
				}
			} else
				tag.time.setText("--:--:--");
			if (refresh && freshId == ab_pos) {
				refresh = false;
			}
			return convertView;
		}

		public final class ViewHolder {
			ImageView imageView;
			TextView title;
			TextView time;
		}
	}

	class MyView {
		ImageView imageView;
		TextView title;
		TextView time;
	}

	private void initVideoImg() {
		videoImgs[0] = this.getResources().getDrawable(
				R.drawable.list_common_icon_usb);
		videoImgs[1] = this.getResources().getDrawable(
				R.drawable.list_common_icon_folder);
		videoImgs[2] = this.getResources().getDrawable(
				R.drawable.list_common_icon_play);
		videoImgs[3] = this.getResources().getDrawable(
				R.drawable.list_common_icon_internal);
	}

	private class ListKeyListener implements OnKeyListener {
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (foucsVisiable) {
				handler.removeMessages(UNFOCUS_MSG);
				Message msg = handler.obtainMessage(UNFOCUS_MSG);
				handler.sendMessage(msg);
				videolist.setSelector(R.drawable.selector_list_background);
			}
			boolean keycase = false;
			int pos = ((ListView) v).getSelectedItemPosition();
			if (keyCode == KeyEvent.KEYCODE_SHIFT_LEFT
					&& event.getAction() == KeyEvent.ACTION_DOWN && isFakeCnt) {

				simpleAdapter.resetCnt();
				videolist.setSelection(lastVisibleItem);
				isFakeCnt = false;
				simpleAdapter.notifyDataSetChanged();
				// audiolist.setSelection(lastVisibleItem);
				return true;
			}
			int itemnums = listItems.size();
			switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_UP: {
				if (event.getAction() == KeyEvent.ACTION_UP)
	    		{
					keycase = true;
					return true;
				}
				// if(pos == firstVisibleItem)
				// audiolist.setSelectionFromTop(index, 6);
				if (pos == 0) {
					if (itemnums <= LISTNUM) {
						pos = itemnums - 1;
						simpleAdapter.notifyDataSetChanged(pos);
						videolist.setSelection(pos);
						lastVisibleItem = itemnums - 1;
					} else {
						// simpleAdapter.setCnt(itemnums - 12);
						if (lastnum > 0) {
							lastnum--;
							pos = 0;
							simpleAdapter.notifyDataSetChanged(pos);
							videolist.setSelection(0);
							onTrickItemSelected(0);
						} else {
							lastnum = itemnums - LISTNUM;
							pos = LASTLISTID;
							simpleAdapter.notifyDataSetChanged(pos);
							videolist.setSelection(LASTLISTID);
						}
					}
					firstVisibleItem = 0;
					// showPageIcon(itemnums>12,false);
					keycase = true;
					return true;
				} else {
					/*
					 * if(itemnums <= 12 || pos>firstVisibleItem) { keycase =
					 * false; return false; } lastnum --;
					 * simpleAdapter.notifyDataSetChanged();
					 * audiolist.setSelection(0);
					 */
					keycase = false;
					return false;
				}
			}
			case KeyEvent.KEYCODE_DPAD_DOWN:
				if (event.getAction() == KeyEvent.ACTION_UP) {
					keycase = true;
					return true;
				}
				if (pos + lastnum == itemnums - 1) {
					lastnum = 0;
					pos = 0;
					simpleAdapter.notifyDataSetChanged(pos);
					videolist.setSelection(0);
					firstVisibleItem = 0;
					if (itemnums > LISTNUM)
						lastVisibleItem = LASTLISTID;
					else
						lastVisibleItem = itemnums - 1;
					keycase = true;
					return true;
				} else {
					if (itemnums <= LISTNUM || pos < lastVisibleItem) {
						keycase = false;
						return false;
					}
					lastnum++;
					simpleAdapter.notifyDataSetChanged(pos);
					videolist.setSelection(LASTLISTID);
					onTrickItemSelected(LASTLISTID);
					keycase = false;
					return false;
				}
			case KeyEvent.KEYCODE_CHANNEL_DOWN:
			case KeyEvent.KEYCODE_PAGE_DOWN: {
				if (event.getAction() == KeyEvent.ACTION_UP) {
					keycase = true;
					return true;
				}
				if (itemnums == 1) {
					pos = 0;
					simpleAdapter.notifyDataSetChanged(pos);
					videolist.setSelection(0);
					onTrickItemSelected(0);
					keycase = true;
					lastVisibleItem = 0;
					firstVisibleItem = 0;
					return true;
				}
				cancelLoadTime();
				if (pos + lastnum == itemnums - 1) {
					lastnum = 0;
					pos = 0;
					simpleAdapter.notifyDataSetChanged(pos);
					videolist.setSelection(0);
					firstVisibleItem = 0;
					if (itemnums > LISTNUM)
						lastVisibleItem = LASTLISTID;
					else
						lastVisibleItem = itemnums - 1;
					// showPageIcon(false,itemnums>12);
					keycase = true;
					return true;
				} else {
					if (itemnums <= LISTNUM) {
						pos = itemnums - 1;
						simpleAdapter.notifyDataSetChanged(pos);
						videolist.setSelection(pos);
						lastVisibleItem = itemnums - 1;
						firstVisibleItem = 0;
						onTrickItemSelected(LASTLISTID);
					} else if (itemnums - lastVisibleItem - lastnum < LISTNUM + 1
									&& itemnums - lastVisibleItem - lastnum > 1)// left<12
					{
						lastnum += itemnums - lastVisibleItem - lastnum - 1;// move
																			// up
																			// lines
						pos = LASTLISTID;
						simpleAdapter.notifyDataSetChanged(pos);
						videolist.setSelection(LASTLISTID);
						firstVisibleItem = 0;
						lastVisibleItem = LASTLISTID;
	    				onTrickItemSelected(LASTLISTID);
					} else if(itemnums - lastVisibleItem - lastnum == LISTNUM + 1) {// left = 12
						lastnum += LISTNUM;
						pos = LASTLISTID;
						simpleAdapter.notifyDataSetChanged(pos);
						videolist.setSelection(LASTLISTID);
						firstVisibleItem = 0;
						lastVisibleItem = LASTLISTID;	
						onTrickItemSelected(LASTLISTID);
					} else if(itemnums - lastVisibleItem - lastnum == 1) {// left = 0
						lastnum = 0;
						pos = 0;
						simpleAdapter.notifyDataSetChanged(pos);
						videolist.setSelection(pos);
						firstVisibleItem = 0;
						lastVisibleItem = LASTLISTID;
						onTrickItemSelected(LASTLISTID);
					} else {	//left > 12
						lastnum += LISTNUM;
						pos = LASTLISTID;
						simpleAdapter.notifyDataSetChanged(pos);
						videolist.setSelection(pos);
						firstVisibleItem = 0;
						lastVisibleItem = LASTLISTID;
	    				onTrickItemSelected(LASTLISTID);
					}
					keycase = true;
					return true;
				}
			}
			case KeyEvent.KEYCODE_CHANNEL_UP:
			case KeyEvent.KEYCODE_PAGE_UP: {
				if (event.getAction() == KeyEvent.ACTION_UP) {
					keycase = true;
					return true;
				}
				if (itemnums == 1) {
					lastnum = 0;
					pos = 0;
					simpleAdapter.notifyDataSetChanged(pos);
					videolist.setSelection(pos);
					onTrickItemSelected(pos);
					keycase = true;
					return true;
				}
				cancelLoadTime();
				if (lastnum == 0) {
					if (itemnums <= LISTNUM) {
						lastnum = 0;
						pos = itemnums - 1;
						simpleAdapter.notifyDataSetChanged(pos);
						videolist.setSelection(pos);
						onTrickItemSelected(pos);
						firstVisibleItem = 0;
						lastVisibleItem = itemnums - 1;
					} else {
						lastnum = itemnums - LISTNUM;
						pos = 0;
						simpleAdapter.notifyDataSetChanged(pos);
						videolist.setSelection(pos);
						onTrickItemSelected(pos);
						firstVisibleItem = 0;
						lastVisibleItem = LASTLISTID;
					}
					keycase = true;
					return true;
				} else {
					if (lastnum <= LISTNUM) {
						lastnum = 0;
						pos = 0;
						simpleAdapter.notifyDataSetChanged(pos);
						videolist.setSelection(pos);
						onTrickItemSelected(pos);

						firstVisibleItem = 0;
						if (itemnums <= LISTNUM)
							lastVisibleItem = itemnums - 1;
						else
							lastVisibleItem = LASTLISTID;
						keycase = true;
						return true;
					} else {
						lastnum -= LISTNUM;
						pos = 0;
						simpleAdapter.notifyDataSetChanged(pos);
						videolist.setSelection(pos);
						onTrickItemSelected(pos);

						firstVisibleItem = 0;
						lastVisibleItem = LASTLISTID;
						keycase = true;
						return true;
					}
				}
			}
			default:
				return keycase;
			}
		}
	};

	public void handleUsbPlugout() {
		this.finish();
	}

	public void sendIntent(int position, int resume_index) {
		handler.removeMessages(VIDEO_PLAY);

		if (task_getduration != null)
			task_getduration.cancel();

		if (mPlayer != null) {
			mPlayer.stop();
			mPlayer.reset();
			mPlayer.setOnCompletionListener(null);
			mPlayer.setOnPreparedListener(null);
			mPlayer.setOnErrorListener(null);
			mPlayer.setDisplay(null);
		}

		ComponentName componetName = new ComponentName("com.rtk.mediabrowser",
				"com.rtk.mediabrowser.VideoPlayerActivity");
		Intent intent = new Intent();
		Bundle bundle = new Bundle();

		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

		int initPos = 0;
		int totalCnt = 0;

		for (int i = 0; i < totalLen; i++) {
			if (mDataProvider.GetFileTypeAt(i) == FileFilterType.DEVICE_FILE_VIDEO) {
				if (mDataProvider.GetTitleAt(position).compareTo(
						mDataProvider.GetTitleAt(i)) == 0) {
					initPos = totalCnt;
					break;
				}
				totalCnt++;
			}
		}

		bundle.putBoolean("isanywhere", false);
		bundle.putInt("initPos", initPos);
		bundle.putInt("resume_index", resume_index);
		bundle.putInt("mBrowserType", mBrowserType);
		bundle.putString("devicePath", devicePath);
		intent.putExtras(bundle);
		intent.setComponent(componetName);
		startActivityForResult(intent, 0);
	}

	public void get_hour(int time) {
		if (time < 10)
			hour = "0" + String.valueOf(time);
		else
			hour = String.valueOf(time);
	}

	public void get_minute(int time) {
		if (time < 10)
			min = "0" + String.valueOf(time);
		else
			min = String.valueOf(time);
	}

	public void get_week_day(int time) {
		switch (time) {
		case 1:
			day_of_week = "Mon";
			break;
		case 2:
			day_of_week = "Tus";
			break;
		case 3:
			day_of_week = "Wed";
			break;
		case 4:
			day_of_week = "Thu";
			break;
		case 5:
			day_of_week = "Fri";
			break;
		case 6:
			day_of_week = "Sat";
			break;
		case 7:
			day_of_week = "Sun";
			break;
		}
	}

	public void get_month(int time) {
		switch (time) {
		case 1:
			month = "Jan";
			break;
		case 2:
			month = "Fer";
			break;
		case 3:
			month = "Mar";
			break;
		case 4:
			month = "Apr";
			break;
		case 5:
			month = "May";
			break;
		case 6:
			month = "Jun";
			break;
		case 7:
			month = "Jul";
			break;
		case 8:
			month = "Aug";
			break;
		case 9:
			month = "Sep";
			break;
		case 10:
			month = "Oct";
			break;
		case 11:
			month = "Nov";
			break;
		case 12:
			month = "Dec";
			break;
		}
	}

	public void get_file_time(int position) {
		long modifiedTime = mDataProvider.GetFileAt(position).lastModified();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(modifiedTime);

		get_hour(cal.get(Calendar.HOUR_OF_DAY));
		get_minute(cal.get(Calendar.MINUTE));
		get_week_day(cal.get(Calendar.DAY_OF_WEEK));
		get_month(cal.get(Calendar.MONTH));

		date.setText(getApplicationContext().getText(R.string.date) + ": "
				+ hour + ":" + min + " " + day_of_week + ". "
				+ cal.get(Calendar.DAY_OF_MONTH) + " " + month + " "
				+ cal.get(Calendar.YEAR));
	}

	public boolean isDivxVODFile() {
		byte[] inputArray = new byte[] { 0, 0, 0, 0 };
		byte[] outputArray = mPlayer.execSetGetNavProperty(
				DivxParser.NAVPROP_DIVX_VOD_QUERY, inputArray);
		if (outputArray != null) {
			int isVOD = outputArray[0] | ((outputArray[1] & 0xFF) << 8)
					| ((outputArray[2] & 0xFF) << 16)
					| ((outputArray[3] & 0xFF) << 24);
			Log.v(TAG, "VOD: " + isVOD);

			if (isVOD == 1)
				return true;
			else
				return false;
		}

		return false;
	}

	public boolean isMutiTitle() {
		byte[] inputArray = new byte[] { 0, 0, 0, 0 };
		byte[] outputArray = mPlayer.execSetGetNavProperty(
				DivxParser.NAVPROP_DIVX_TITLENUM_QUERY, inputArray);
		if (outputArray != null) {
			int titlenum = outputArray[0] | ((outputArray[1] & 0xFF) << 8)
					| ((outputArray[2] & 0xFF) << 16)
					| ((outputArray[3] & 0xFF) << 24);

			if (titlenum > 1) {
				return true;
			} else if (titlenum == 1) {
				outputArray = mPlayer.execSetGetNavProperty(
						DivxParser.NAVPROP_DIVX_EDITIONNUM_QUERY, inputArray);
				if (outputArray != null) {
					int editionnum = outputArray[0]
							| ((outputArray[1] & 0xFF) << 8)
							| ((outputArray[2] & 0xFF) << 16)
							| ((outputArray[3] & 0xFF) << 24);
					if (editionnum > 1) {
						return true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			}
		}
		return false;
	}

	private void media_play_init(String path) {
		mPlayer.reset();
		mPlayer.setOnPreparedListener(videoPreparedListener);
		mPlayer.setOnCompletionListener(videoCompletionListener);
		mPlayer.setOnErrorListener(videoErrorListener);
		mPlayer.setOnInfoListener(VideoInfoListener);
		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			mPlayer.setPlayerType(6); // Use RTK_MediaPlayer
			mPlayer.setDataSource(path);
			mPlayer.setParameter(1400, 0); // No need load DRM
			mPlayer.setParameter(1500, 0); // No need change LastPlayPath
			isCreateRTMediaPlayer = true;
			mPlayer.prepareAsync();
			mPlayer.setDisplay(sView.getHolder());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			folder_image.setVisibility(View.VISIBLE);
			folder_image.setImageResource(R.drawable.broken_video_icon);
			e.printStackTrace();
			return;
		} catch (IOException e) {
			folder_image.setVisibility(View.VISIBLE);
			folder_image.setImageResource(R.drawable.broken_video_icon);
			e.printStackTrace();
			return;
		}
	}

	private void setQuickMenuItem(int position, boolean isNext) {
		switch (position) {
		case SET_REPEAT_MODE: {
			if (isNext)
				repeatIndex++;
			else {
				repeatIndex += 3;
				repeatIndex--;
			}
			repeatIndex %= 3;

			check_repeat_model(repeatIndex);

			Editor editor = mPerferences.edit();
			editor.putInt("repeatIndex", repeatIndex);
			editor.commit();
			break;
		}
		case SET_SLEEP_TIMER: {
			if (isNext) {
				if (mSleepTimeMin < 50) {
					if (0 == mSleepTimeMin && 12 == mSleepTimeHour) {
						mSleepTimeHour = 0;
					} else {
						mSleepTimeMin = (mSleepTimeMin / 10 + 1) * 10;
					}
				} else {
					mSleepTimeMin = 0;
					mSleepTimeHour++;
				}
			} else {
				if (0 == mSleepTimeMin) {
					if (0 == mSleepTimeHour) {
						mSleepTimeHour = 12;
					} else {
						mSleepTimeHour--;
						mSleepTimeMin = 50;
					}
				} else {
					mSleepTimeMin = ((mSleepTimeMin - 1) / 10) * 10;
				}
			}

			SimpleDateFormat df_ori = new SimpleDateFormat("HH mm");
			SimpleDateFormat df_des = new SimpleDateFormat("HH:mm");
			java.util.Date date_parse = null;
			try {
				date_parse = df_ori.parse(mSleepTimeHour + " " + mSleepTimeMin);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String timeFormat = df_des.format(date_parse);

			TextView OptionText = (TextView) quickMenu.getListView()
					.getChildAt(position).findViewById(R.id.menu_option);
			OptionText.setText(timeFormat);

			Intent itent = new Intent();
			Bundle bundle = new Bundle();
			if (0 == mSleepTimeHour && 0 == mSleepTimeMin) {
				bundle.remove("SetTSBSleepHour");
				bundle.remove("SetTSBSleepMinute");
				bundle.putString("TSBTimerState", "Cancel");
				bundle.putString("CallingTSBTimer", "MediaBrowserCallingTimer");
			} else {
				bundle.putInt("SetTSBSleepHour", mSleepTimeHour);
				bundle.putInt("SetTSBSleepMinute", mSleepTimeMin);
				bundle.putString("TSBTimerState", "Set"); // Set=1, Cancel=0
				bundle.putString("CallingTSBTimer", "MediaBrowserCallingTimer");
			}
			itent.setAction("com.rtk.TSBTimerSettingMESSAGE");
			itent.putExtras((Bundle) bundle);
			sendBroadcast(itent);
			break;
		}
		case SET_TV_APP: {
			if (task_hide_quickmenu != null) {
				task_hide_quickmenu.cancel();
				task_hide_quickmenu = null;
			}

			ComponentName componetName = new ComponentName("com.tsb.tv",
					"com.tsb.tv.Tv_strategy");
			Intent intent = new Intent();
			intent.setComponent(componetName);
			startActivity(intent);
			break;
		}
		case SET_SYS_SETTING: {
			if (task_hide_quickmenu != null) {
				task_hide_quickmenu.cancel();
				task_hide_quickmenu = null;
			}

			ComponentName componetName = new ComponentName(
					"com.android.settings", "com.android.settings.Settings");
			Intent intent = new Intent();
			intent.setComponent(componetName);
			startActivity(intent);
			break;
		}
		default:
			break;
		}
	}

	class QuickMenuVideoAdapter extends BaseAdapter {

		class ViewHolder {
			TextView menu_name;
			ImageView left;
			TextView menu_option;
			ImageView right;
		}

		int[] menu_name = new int[] { R.string.quick_menu_repeat,
				R.string.quick_menu_sleep, R.string.quick_menu_tvapp,
				R.string.quick_menu_sysset };

		int[] visibility = new int[] { View.INVISIBLE, View.INVISIBLE,
				View.INVISIBLE, View.INVISIBLE, };

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

		public void setVisibility(int position, int isVisible) {
			// TODO Auto-generated method stub
			visibility[position] = isVisible;
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
				if (true == MediaBrowserConfig
						.getRight2Left(getApplicationContext()))
					convertView = mInflater.inflate(R.layout.quick_list_row_a,
							null);
				else
					convertView = mInflater.inflate(R.layout.quick_list_row,
							null);
				holder = new ViewHolder();
				holder.menu_name = (TextView) convertView
						.findViewById(R.id.menu_name);
				Typeface type = Typeface
						.createFromFile("/system/fonts/FAUNSGLOBAL3_F_r2.TTF");
				holder.menu_name.setTypeface(type);
				holder.menu_option = (TextView) convertView
						.findViewById(R.id.menu_option);
				holder.left = (ImageView) convertView
						.findViewById(R.id.left_arrow);
				holder.right = (ImageView) convertView
						.findViewById(R.id.right_arrow);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.menu_name.setText(menu_name[position]);

			switch (position) {
			case SET_REPEAT_MODE:
				switch (repeatIndex) {
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
					date_parse = df_ori.parse(mSleepTimeHour + " "
							+ mSleepTimeMin);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				String timeFormat = df_des.format(date_parse);
				holder.menu_option.setText(timeFormat);
				break;
			case SET_TV_APP:
			case SET_SYS_SETTING:
				break;
			default:
				break;
			}

			holder.left.setVisibility(visibility[position]);
			holder.right.setVisibility(visibility[position]);

			return convertView;
		}
	}

	private void check_repeat_model(int repeat) {
		switch (repeat) {
		case 0:
			btn_repeat.setVisibility(View.INVISIBLE);
			break;
		case 1:
			btn_repeat.setImageResource(R.drawable.photo_player_repeat_on);
			btn_repeat.setVisibility(View.VISIBLE);
			break;
		case 2:
			btn_repeat.setImageResource(R.drawable.photo_repeat_one_on);
			btn_repeat.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}

	private void PopupMessageShow(String msg, int dismiss_time) {
		msg_hint.setMessage(msg);
		if (isRight2Left)
			msg_hint.setMessageRight();
		else
			msg_hint.setMessageLeft();
		msg_hint.show();

		if (task_message_time_out != null) {
			task_message_time_out.cancel();
			task_message_time_out = null;
		}

		task_message_time_out = new TimerTask() {

			@Override
			public void run() {
				handler.sendEmptyMessage(HIDE_POPUP_MESSAGE);
			}

		};
		if (timer == null)
			timer = new Timer(true);
		timer.schedule(task_message_time_out, dismiss_time);
	}

	private void PopupMessageShow(String msg, int resid, int height, int width,
			int gravity, int x, int y, int dismiss_time) {
		msg_hint.setMessage(msg);
		msg_hint.setMessageCenterHorizotal();
		msg_hint.show(resid, height, width, gravity, x, y);
		msg_hint.setMessageColor(Color.BLACK);

		if (task_message_time_out != null) {
			task_message_time_out.cancel();
			task_message_time_out = null;
		}

		task_message_time_out = new TimerTask() {

			@Override
			public void run() {
				handler.sendEmptyMessage(HIDE_POPUP_MESSAGE);
			}

		};
		if (timer == null)
			timer = new Timer(true);
		timer.schedule(task_message_time_out, dismiss_time);
	}

	private void addPageListener() {
		page_up.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				int pos = videolist.getSelectedItemPosition();
				int itemnums = listItems.size();
				Log.v("itemnums = ", "" + itemnums);

				if (itemnums == 1) {
					lastnum = 0;
					pos = 0;
					simpleAdapter.notifyDataSetChanged(pos);
					videolist.setSelection(pos);
					onTrickItemSelected(pos);
					return ;
				}
				cancelLoadTime();
				if (lastnum == 0) {
					if (itemnums <= LISTNUM) {
						lastnum = 0;
						pos = itemnums - 1;
						simpleAdapter.notifyDataSetChanged(pos);
						videolist.setSelection(pos);
						onTrickItemSelected(pos);
						firstVisibleItem = 0;
						lastVisibleItem = itemnums - 1;
					} else {
						lastnum = itemnums - LISTNUM;
						pos = 0;
						simpleAdapter.notifyDataSetChanged(pos);
						videolist.setSelection(pos);
						onTrickItemSelected(pos);
						firstVisibleItem = 0;
						lastVisibleItem = LASTLISTID;
					}
				} else {
					if (lastnum <= LISTNUM) {
						lastnum = 0;
						pos = 0;
						simpleAdapter.notifyDataSetChanged(pos);
						videolist.setSelection(pos);
						onTrickItemSelected(pos);

						firstVisibleItem = 0;
						if (itemnums <= LISTNUM)
							lastVisibleItem = itemnums - 1;
						else
							lastVisibleItem = LASTLISTID;
					} else {
						lastnum -= LISTNUM;
						pos = 0;
						simpleAdapter.notifyDataSetChanged(pos);
						videolist.setSelection(pos);
						onTrickItemSelected(pos);
						firstVisibleItem = 0;
						lastVisibleItem = LASTLISTID;
					}
				}
			}
		});
		page_down.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				int pos = videolist.getSelectedItemPosition();
				int itemnums = listItems.size();

				if (itemnums == 1) {
					pos = 0;
					simpleAdapter.notifyDataSetChanged(pos);
					videolist.setSelection(0);
					onTrickItemSelected(0);
					lastVisibleItem = 0;
					firstVisibleItem = 0;
					return ;
				}
				cancelLoadTime();
				if (pos + lastnum == itemnums - 1) {
					lastnum = 0;
					pos = 0;
					simpleAdapter.notifyDataSetChanged(pos);
					videolist.setSelection(0);
					firstVisibleItem = 0;
					if (itemnums > LISTNUM)
						lastVisibleItem = LASTLISTID;
					else
						lastVisibleItem = itemnums - 1;
						return ;
				} else {
					if (itemnums <= LISTNUM) {
						pos = itemnums - 1;
						simpleAdapter.notifyDataSetChanged(pos);
						videolist.setSelection(pos);
						lastVisibleItem = itemnums - 1;
						firstVisibleItem = 0;
						onTrickItemSelected(LASTLISTID);
					} else if (itemnums - lastVisibleItem - lastnum < LISTNUM + 1
							&& itemnums - lastVisibleItem - lastnum > 1)// left<12
					{
						lastnum += itemnums - lastVisibleItem - lastnum - 1;// move
						// up
						// lines
						pos = LASTLISTID;
						simpleAdapter.notifyDataSetChanged(pos);
						videolist.setSelection(LASTLISTID);
						firstVisibleItem = 0;
						lastVisibleItem = LASTLISTID;
						onTrickItemSelected(LASTLISTID);
					}
					else if(itemnums - lastVisibleItem - lastnum == LISTNUM + 1) {// left = 12
						lastnum += LISTNUM;
						pos = LASTLISTID;
						simpleAdapter.notifyDataSetChanged(pos);
						videolist.setSelection(LASTLISTID);
						firstVisibleItem = 0;
						lastVisibleItem = LASTLISTID;	
						onTrickItemSelected(LASTLISTID);
					} else if(itemnums - lastVisibleItem - lastnum == 1) {// left = 0
						lastnum = 0;
						pos = 0;
						simpleAdapter.notifyDataSetChanged(pos);
						videolist.setSelection(pos);
						firstVisibleItem = 0;
						lastVisibleItem = LASTLISTID;
						onTrickItemSelected(LASTLISTID);
					} else {	//left > 12
						lastnum += LISTNUM;
						pos = LASTLISTID;
						simpleAdapter.notifyDataSetChanged(pos);
						videolist.setSelection(pos);
						firstVisibleItem = 0;
						lastVisibleItem = LASTLISTID;
						onTrickItemSelected(LASTLISTID);
						return ;
					}
				}
			}
		});
	}

	private void dismissLoading() {

		loadingIcon.setVisibility(View.INVISIBLE);

	}

	private void setListTouch() {
		videolist.setOnHoverListener(new OnHoverListener() {
			@Override
			public boolean onHover(View v, MotionEvent event) {
				Log.v(TAG, "onHover");
				switch (event.getAction()) {
				case MotionEvent.ACTION_HOVER_ENTER:
					videolist.setSelector(R.drawable.selector_list_background2);
					simpleAdapter.notifyDataSetChanged();
					break;
				case MotionEvent.ACTION_HOVER_MOVE:
					if (!foucsVisiable) {
						videolist
								.setSelector(R.drawable.selector_list_background2);
						simpleAdapter.notifyDataSetChanged();
					}
					transY2Pos((int) event.getRawY());
					break;

				case MotionEvent.ACTION_HOVER_EXIT:
					handler.removeMessages(UNFOCUS_MSG);
					Message msg = handler.obtainMessage(UNFOCUS_MSG);
					handler.sendMessage(msg);
					videolist.setSelector(R.drawable.selector_list_background);
					simpleAdapter.notifyDataSetChanged();
					break;
				}
				return false;
			}
		});
	}

	private Instrumentation in = new Instrumentation();

	private void transY2Pos(int y) {
		int line = (int) ((float) (y - listTop) / listH);
		if (line > LASTLISTID)
			line = LASTLISTID;
		if (line > totalLen - 1)
			line = totalLen - 1;
		toY = (int) (listTop + line * listH);
		toY += 1;
		if (line < 0 && foucsVisiable) {
			handler.removeMessages(UNFOCUS_MSG);
			Message msg = handler.obtainMessage(UNFOCUS_MSG);
			handler.sendMessage(msg);
		} else if (line >= 0) {
			setHoverAction();
		}

		if (videolist.getSelectedItemPosition() < 0) {
			fakeKeyUp();
		}
		videolist.setSelection(line);
	}

	private void setHoverAction() {
		handler.removeMessages(FOCUS_MSG);
		Message msg = handler.obtainMessage(FOCUS_MSG);
		handler.sendMessage(msg);
	}

	// sendKeyDownUpSync not able to work in main thread

	private void fakeKeyUp() {
		new Thread(new Runnable() {
			public void run() {
				in.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_UP);
			}
		}).start();
	}

	private void hide_quick_menu_delay(int delay) {
		if (task_hide_quickmenu != null) {
			task_hide_quickmenu.cancel();
			task_hide_quickmenu = null;
		}
		task_hide_quickmenu = new TimerTask() {

			@Override
			public void run() {
				handler.sendEmptyMessage(HIDE_QUICK_MENU);
			}

		};

		if (timer == null)
			timer = new Timer(true);
		timer.schedule(task_hide_quickmenu, delay);
	}

	private void getInitTimer() {
		new Thread(new Runnable() {
			@Override
			public void run() {

				try {
					int mins = Settings.Global.getInt(getApplicationContext()
							.getContentResolver(), "TotalMinute");
					mSleepTimeHour = mins / 60;
					mSleepTimeMin = mins % 60;
				} catch (SettingNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
	        	String ext = intent.getStringExtra("intentTag");
	        	if(!ext.equalsIgnoreCase("video"))
	        		finish();
	        }
	    };
	    mIntentFilter = new IntentFilter();
		mIntentFilter.addAction("com.rtk.mediabrowser.broadcast");
        registerReceiver(mPlayReceiver, mIntentFilter);
	}
}
