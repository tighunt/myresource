package com.rtk.mediabrowser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Typeface;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.asf.AsfTag;
import org.jaudiotagger.tag.asf.AsfTagCoverField;
import org.jaudiotagger.tag.id3.AbstractID3v2Frame;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.framebody.FrameBodyAPIC;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Instrumentation;
import android.app.TvManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.XmlResourceParser;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnGenericMotionListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnHoverListener;
import android.view.View.OnKeyListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.realtek.DataProvider.AbstractDataProvider;
import com.realtek.DataProvider.DeviceFileDataPrivider;
import com.realtek.DataProvider.FileFilterType;
import com.realtek.Utils.AsyncInfoLoader;
import com.realtek.Utils.AsyncInfoLoader.TimeInfoCallback;
import com.realtek.Utils.FileInfo;
import com.realtek.Utils.FileUtils;
import com.realtek.Utils.MimeTypes;
import com.realtek.Utils.utilCue;
import com.rtk.mediabrowser.MusicActivity.RepeatStatus;
import com.rtk.mediabrowser.Path_Info;

public class AudioBrowser extends Activity implements
OnItemLongClickListener{
	private String TAG = "AudioBrowser";

	private ListView audiolist;
	private TextView topPath;
	private TextView audio_artist;
	private TextView audio_album;
	private ImageView audio_image;
	private ProgressBar audio_bar;
	private TextView guide_enter;
	private TextView audio_hasplayed;
	private TextView audio_duration;
	private TextView audio_duration_tag;
	private TextView mTopFileIndexInfoTextView;
	// add by star_he
	private final int LISTNUM = 11;
	private final int LISTID = 10;
	// add by star_he end

	private final int SHOW_AUDIO_INFO = 0;
	private final int PROGRESS_CHANGED = 1;
	private final int UPDATE_UI_FACE = 2;
	private final int START_LOADING = 3;
	private final int STOP_LOADING = 4;
	private final int UPDATE_TIME = 5;
	private final int UPDATE_UI_BROKEN = 6;
	private final int UPDATE_UI_RESET = 7;
	private final int UPDATE_LIST = 8;
	private final int MSG_SET_REPEAT = 11;
	private final int MSG_QUICK_HIDE = 12;
	private final int MSG_QUICKMSG_HIDE = 13;
	private final int MSG_GO_BACK = 14;
	private final int MSG_HIDE_HINT = 15;
	private final int MSG_REFRESH_TIMER = 16;
	private long quick_timeout = 6000;
	private int Max = 0;
	private int Minute = 0;
	private int Hour = 0;
	private int Second = 0;
	private boolean isFocused = true;
	private boolean isFirstRun = true;
	private Path_Info curPathInfo;
	private int dirLevel = -1;
	private ArrayList<String> parentPath = new ArrayList<String>();
	private AbstractDataProvider mDataProvider = null;
	private MimeTypes mMimeTypes = null;
	private XmlResourceParser mMimeTypeXml;
	private String rootPath;
	private MediaPlayer mPlayer = null;
	private TimerTask task_play = null;
	private TimerTask task_getduration = null;
	private Timer timer = null;
	private Timer timer2 = null;
	private Handler handler;
	private Runnable PlayingRunnable;
	private int mBrowserType = 0;
	private int totalLen;
	private boolean run;
	private String AudioPath;
	private String mTopFileIndexInfoStr;
	String total = null;
	String title = null;
	String album = null;
	String artist = null;
	String date = null;
	FrameBodyAPIC body = null;
	Drawable audioImgs[] = new Drawable[5];// gmaui
	ArrayList<FileInfo> listItems = null;
	ProgressBar loadingIcon = null;
	ImageView page_up = null;
	ImageView page_down = null;
	ImageView img_line = null;
	Animation ad = null;
	// Context mcontext =null;
	int lastnum = 0;
	ArrayList<FileInfo> pageItems = null;
	FileListAdapter simpleAdapter = null;
	public static int index = 0;
	private int lastIndex = 0;
	private int playindex = 0;
	public static boolean changeIndex = false;
	public static boolean reset = false;
	private int repeatIndex = 0;
	private RepeatStatus[] repeats = { RepeatStatus.OFF, RepeatStatus.ALL,
			RepeatStatus.ONE };
	private SharedPreferences mPerferences = null;
	private ImageButton imgRepeat = null;
	int firstVisibleItem;
	int lastVisibleItem;
	private MediaApplication map = null;
	private QuickMenu quickmenu = null;
	private TvManager mTv = null;
	private QuickMenuAdapter quickmenuAdapter = null;
	UsbController mUsbCtrl = null;
	private boolean isFakeCnt = true;
	private int mSleepTimeHour = 0, mSleepTimeMin = 0;
	int[] positions = new int[2];
	private ImageView focusView;
	private boolean foucsVisiable = false;
	int fromX, fromY, toX, toY;
	private String devicePath = "";
	private ArrayList<String> folderPath = new ArrayList<String>();
	private PopupMessage msg_hint = null;
	Message_not_avaible msg_notavaible = null;
	private int UsbRemoved = 1;

	private int mActivityPauseFlag = 0;
	private ContentResolver m_ContentMgr = null;

	private void useStrictMode() {
		if (MediaApplication.DEBUG) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectDiskReads().detectDiskWrites().detectNetwork() //
					.penaltyLog() //
					.build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectLeakedSqlLiteObjects() //
					.penaltyLog() //
					.penaltyDeath().build());
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		useStrictMode();
		super.onCreate(savedInstanceState);
		isRight2Left = MediaBrowserConfig
				.getRight2Left(getApplicationContext());
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (isRight2Left)
			setContentView(R.layout.music_reversal);
		else
			setContentView(R.layout.music);
		map = (MediaApplication) getApplication();
		Intent intent = getIntent();
		initAudioImg();
		initLoading();
		initUsbCtl();
		fromX = 0;
		fromY = 0;
		toX = 135;
		index = 0;
		mBrowserType = intent.getIntExtra("browserType", 0);
		if (mBrowserType == 0)
			rootPath = "/storage/udisk/";
		if (mTv == null) {
			mTv = (TvManager) getSystemService("tv");
		}
		msg_hint = new PopupMessage(this);
		run = false;
		reset = false;
		audiolist = (ListView) findViewById(R.id.audio_list);
		topPath = (TextView) findViewById(R.id.music_path_top);
		audio_artist = (TextView) findViewById(R.id.audio_artist);
		audio_album = (TextView) findViewById(R.id.audio_album);
		audio_image = (ImageView) findViewById(R.id.audio_image);
		audio_bar = (ProgressBar) findViewById(R.id.audio_progress);
		audio_hasplayed = (TextView) findViewById(R.id.audio_hasplayed);
		guide_enter = (TextView) findViewById(R.id.guide_enter);
		audio_duration = (TextView) findViewById(R.id.audio_duration);
		audio_duration_tag = (TextView) findViewById(R.id.audio_duration_tag);
		mTopFileIndexInfoTextView = (TextView) findViewById(R.id.TopIndexInfo_audio);
		page_up = (ImageView) findViewById(R.id.page_up);
		focusView = (ImageView) findViewById(R.id.focus);
		page_down = (ImageView) findViewById(R.id.page_down);
		img_line = (ImageView) findViewById(R.id.audio_list_icon_line);
		imgRepeat = (ImageButton) findViewById(R.id.imgRepeat);
		addPageListener();
		// GetMimeType
		mMimeTypeXml = getResources().getXml(R.xml.mimetypes);
		mMimeTypes = Util.GetMimeTypes(mMimeTypeXml);
		mPerferences = PreferenceManager.getDefaultSharedPreferences(this);
		if (mPlayer == null)
			mPlayer = map.getMediaPlayer();

		if (curPathInfo == null)
			curPathInfo = new Path_Info();

		mPlayer.setOnCompletionListener(audioCompletionListener);
		mPlayer.setOnErrorListener(mp_error);
		mPlayer.setOnInfoListener(infoListener);
		audiolist.setOnItemSelectedListener(itemSelectedListener);
		audiolist.setOnItemClickListener(itemClickListener);
		audiolist.setOnItemLongClickListener(this);
		audiolist.setOnKeyListener(new ListKeyListener());
		setListTouch();
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		popview = layoutInflater.inflate(R.layout.quick_menu, null);
		menu_repeat = (Button) popview.findViewById(R.id.menu_repeat);
		menu_repeat.setOnKeyListener(keyListen);
		menu_repeat.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				repeatIndex++;
				repeatIndex %= 3;
				new Thread(new Runnable() {
					@Override
					public void run() {
						Editor editor = mPerferences.edit();//
						editor.putInt("repeatIndex", repeatIndex);
						editor.commit();
					}
				}).start();
				setRepeatIcon(repeats[repeatIndex]);
			}
		});
		menu_repeat
				.setOnFocusChangeListener(new Button.OnFocusChangeListener() {
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus == true) {
							re_repeat.setBackgroundDrawable(getResources()
									.getDrawable(
											R.drawable.list_common_item_focus));
						} else {
							re_repeat.setBackgroundColor(Color.TRANSPARENT);
						}
					}
				});
		menu_sleep = (Button) popview.findViewById(R.id.menu_sleep);
		menu_sleep.setOnKeyListener(keyListen);
		menu_sleep.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

			}
		});
		menu_sleep.setOnFocusChangeListener(new Button.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus == true) {
					re_sleep.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.list_common_item_focus));
				} else {
					re_sleep.setBackgroundColor(Color.TRANSPARENT);
				}
			}
		});
		tvapp = (Button) popview.findViewById(R.id.btn_tvap);
		tvapp.setOnKeyListener(keyListen);
		tvapp.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
			}
		});
		tvapp.setOnFocusChangeListener(new Button.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus == true) {
					re_tvap.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.list_common_item_focus));
				} else {
					re_tvap.setBackgroundColor(Color.TRANSPARENT);
				}
			}
		});

		syssetting = (Button) popview.findViewById(R.id.btn_setting);
		syssetting.setOnKeyListener(keyListen);
		syssetting.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
			}
		});
		syssetting.setOnFocusChangeListener(new Button.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus == true) {
					re_setting.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.list_common_item_focus));
				} else {
					re_setting.setBackgroundColor(Color.TRANSPARENT);
				}
			}
		});

		sound_bar = (SeekBar) popview.findViewById(R.id.sound_bar);
		sound_bar.setOnKeyListener(keyListen);

		sound_bar = (SeekBar) popview.findViewById(R.id.sound_bar);
		txtSound = (TextView) popview.findViewById(R.id.txtSound);

		re_sound = (RelativeLayout) popview.findViewById(R.id.re_sound);
		re_sound.setVisibility(View.GONE);
		re_repeat = (RelativeLayout) popview.findViewById(R.id.re_repeat);
		re_sleep = (RelativeLayout) popview.findViewById(R.id.re_sleep);
		re_setting = (RelativeLayout) popview.findViewById(R.id.re_setting);
		re_tvap = (RelativeLayout) popview.findViewById(R.id.re_tvap);

		sound_bar.setVisibility(View.GONE);
		txtSound.setVisibility(View.GONE);
		getDataProvider(rootPath);
		getFileList(rootPath);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case SHOW_AUDIO_INFO:
					if (index + lastnum == playindex) {
						playAndSeek = true;
					}
					playindex = index + lastnum;
					task_play.cancel();
					task_play = null;
					loadingIcon.setVisibility(View.VISIBLE);
					if (mPlayer != null) {
						if (MediaApplication.DEBUG)
							Log.e(TAG, "mPlayer != null");
						mPlayer.reset();
					}
					audio_bar.setProgress(0);
					Thread mPrepareMusic = new Thread() {

						@Override
						public void run() {
							startMusic();
						}
					};
					mPrepareMusic.start();
					break;
				case PROGRESS_CHANGED:
					handler.post(PlayingRunnable);
					break;
				case UPDATE_UI_FACE:
					if (body != null) {
						byte[] imageData = body.getImageData();
						if (imageData != null && imageData.length > 0)
							audio_image.setImageBitmap(BitmapFactory
									.decodeByteArray(imageData, 0,
											imageData.length));
					}

					loadingIcon.setVisibility(View.INVISIBLE);
					break;
				case START_LOADING:
					loadingIcon.setVisibility(View.VISIBLE);
					break;
				case STOP_LOADING:
					loadingIcon.setVisibility(View.INVISIBLE);
					break;
				case UPDATE_TIME:
					int index = msg.getData().getInt("index");
					String time = msg.getData().getString("time");
					updateList(index, time);
					break;
				case UPDATE_UI_BROKEN:
					audio_image
							.setImageResource(map.isIs4k2k() ? R.drawable.broken_video_icon
									: R.drawable.broken_video_icon);
					show(false);
					loadingIcon.setVisibility(View.INVISIBLE);
					simpleAdapter.clearSelected();
					simpleAdapter.notifyDataSetChanged();
					break;
				case UPDATE_UI_RESET:
					audio_image.setImageBitmap(null);
					break;
				case UPDATE_LIST:
					in.sendKeyDownUpSync(KeyEvent.KEYCODE_SHIFT_LEFT);
					break;
				case MSG_SET_REPEAT:
					setRepeatIcon(repeats[repeatIndex]);
					break;
				case MSG_QUICK_HIDE: {
					quickmenu.dismiss();
				}
					break;
				case MSG_QUICKMSG_HIDE: {
					msg_notavaible.dismiss();
				}
					break;
				case MSG_GO_BACK: {
					gotoLastLevel();
				}
					break;
				case MSG_HIDE_HINT: {
					if (msg_hint != null) {
						msg_hint.setOnDismissListener(null);
						msg_hint.dismiss();
					}
				}
					break;
				case MSG_REFRESH_TIMER: {
					quickmenuAdapter.notifyDataSetChanged();
				}
					break;
				}
				super.handleMessage(msg);
			}
		};

		PlayingRunnable = new Runnable() {

			@Override
			public void run() {
				if (MediaApplication.DEBUG)
					Log.e("AudioBrowser", "PlayingRunnable");
				// TODO Auto-generated method stub
				try {
					if (run && mPlayer != null && mPlayer.isPlaying()) {
						if (MediaApplication.DEBUG)
							Log.e("AudioBrowser", "getCurrentPosition");
						int i = mPlayer.getCurrentPosition();
						audio_bar.setProgress(i);
						i /= 1000;
						int minute = i / 60;
						int hour = minute / 60;
						int second = i % 60;
						minute %= 60;
						audio_hasplayed.setText(String.format(
								"%02d:%02d:%02d /", hour, minute, second));
					}
				} catch (Exception e) {
				}
			}
		};

		// createQuickMenu()
		quickmenuAdapter = new QuickMenuAdapter(this);
		quickmenu = new QuickMenu(this, quickmenuAdapter);
		quickmenu.setAnimationStyle(R.style.QuickAnimation);
		quickAutoQuit();
		OnItemClickListener quickmenuItemClickListener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				quickAutoQuit();
				switch (position) {
				case 0: {
					repeatIndex++;
					repeatIndex %= 3;
					new Thread(new Runnable() {
						@Override
						public void run() {
							Editor editor = mPerferences.edit();//
							editor.putInt("repeatIndex", repeatIndex);
							editor.commit();
						}
					}).start();
					setRepeatIcon(repeats[repeatIndex]);
					break;
				}
				case 1: {
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

					TextView OptionText = (TextView) arg1
							.findViewById(R.id.menu_option);
					OptionText.setText(timeFormat);

					Intent itent = new Intent();
					Bundle bundle = new Bundle();
					if (0 == mSleepTimeHour && 0 == mSleepTimeMin) {
						bundle.remove("SetTSBSleepHour");
						bundle.remove("SetTSBSleepMinute");
						bundle.putString("TSBTimerState", "Cancel");
						bundle.putString("CallingTSBTimer",
								"MediaBrowserCallingTimer");
					} else {
						bundle.putInt("SetTSBSleepHour", mSleepTimeHour);
						bundle.putInt("SetTSBSleepMinute", mSleepTimeMin);
						bundle.putString("TSBTimerState", "Set"); // Set=1,
																	// Cancel=0
						bundle.putString("CallingTSBTimer",
								"MediaBrowserCallingTimer");
					}
					itent.setAction("com.rtk.TSBTimerSettingMESSAGE");
					itent.putExtras((Bundle) bundle);
					sendBroadcast(itent);
					break;
				}
				case 2: {
					handler.removeMessages(MSG_QUICK_HIDE);
					ComponentName componetName = new ComponentName(
							"com.tsb.tv", "com.tsb.tv.Tv_strategy");
					Intent intent = new Intent();
					intent.setComponent(componetName);
					startActivity(intent);
					break;
				}
				case 3: {
					handler.removeMessages(MSG_QUICK_HIDE);
					ComponentName componetName = new ComponentName(
							"com.android.settings",
							"com.android.settings.Settings");
					Intent intent = new Intent();
					intent.setComponent(componetName);
					startActivity(intent);
					break;
				}
				default:
					break;
				}
				quickmenuAdapter.notifyDataSetChanged();
			}
		};
		OnKeyListener quickmenuKeyListener = new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					quickAutoQuit();
					if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
						// TODO: switch option
						switch (quickmenu.getListView()
								.getSelectedItemPosition()) {
						case 0: {
							repeatIndex++;
							repeatIndex %= 3;
							new Thread(new Runnable() {
								@Override
								public void run() {
									Editor editor = mPerferences.edit();//
									editor.putInt("repeatIndex", repeatIndex);
									editor.commit();
								}
							}).start();
							setRepeatIcon(repeats[repeatIndex]);
							break;
						}
						case 1: {
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

							SimpleDateFormat df_ori = new SimpleDateFormat(
									"HH mm");
							SimpleDateFormat df_des = new SimpleDateFormat(
									"HH:mm");
							java.util.Date date_parse = null;
							try {
								date_parse = df_ori.parse(mSleepTimeHour + " "
										+ mSleepTimeMin);
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							String timeFormat = df_des.format(date_parse);

							TextView OptionText = (TextView) (quickmenu
									.getListView().getChildAt(1)
									.findViewById(R.id.menu_option));
							OptionText.setText(timeFormat);

							Intent itent = new Intent();
							Bundle bundle = new Bundle();
							if (0 == mSleepTimeHour && 0 == mSleepTimeMin) {
								bundle.remove("SetTSBSleepHour");
								bundle.remove("SetTSBSleepMinute");
								bundle.putString("TSBTimerState", "Cancel");
								bundle.putString("CallingTSBTimer",
										"MediaBrowserCallingTimer");
							} else {
								bundle.putInt("SetTSBSleepHour", mSleepTimeHour);
								bundle.putInt("SetTSBSleepMinute",
										mSleepTimeMin);
								bundle.putString("TSBTimerState", "Set"); // Set=1,
																			// Cancel=0
								bundle.putString("CallingTSBTimer",
										"MediaBrowserCallingTimer");
							}
							itent.setAction("com.rtk.TSBTimerSettingMESSAGE");
							itent.putExtras((Bundle) bundle);
							sendBroadcast(itent);
							break;
						}
						default:
							break;
						}
						quickmenuAdapter.notifyDataSetChanged();
						return true;
					} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
						// TODO: switch option
						switch (quickmenu.getListView()
								.getSelectedItemPosition()) {
						case 0: {
							repeatIndex += 3;
							repeatIndex--;
							repeatIndex %= 3;
							new Thread(new Runnable() {
								@Override
								public void run() {
									Editor editor = mPerferences.edit();//
									editor.putInt("repeatIndex", repeatIndex);
									editor.commit();
								}
							}).start();
							setRepeatIcon(repeats[repeatIndex]);
							break;
						}
						case 1: {
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

							SimpleDateFormat df_ori = new SimpleDateFormat(
									"HH mm");
							SimpleDateFormat df_des = new SimpleDateFormat(
									"HH:mm");
							java.util.Date date_parse = null;
							try {
								date_parse = df_ori.parse(mSleepTimeHour + " "
										+ mSleepTimeMin);
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							String timeFormat = df_des.format(date_parse);

							TextView OptionText = (TextView) (quickmenu
									.getListView().getChildAt(1)
									.findViewById(R.id.menu_option));
							OptionText.setText(timeFormat);

							Intent itent = new Intent();
							Bundle bundle = new Bundle();

							if (0 == mSleepTimeHour && 0 == mSleepTimeMin) {
								bundle.remove("SetTSBSleepHour");
								bundle.remove("SetTSBSleepMinute");
								bundle.putString("TSBTimerState", "Cancel");
								bundle.putString("CallingTSBTimer",
										"MediaBrowserCallingTimer");
							} else {
								bundle.putInt("SetTSBSleepHour", mSleepTimeHour);
								bundle.putInt("SetTSBSleepMinute",
										mSleepTimeMin);
								bundle.putString("TSBTimerState", "Set"); // Set=1,
																			// Cancel=0
								bundle.putString("CallingTSBTimer",
										"MediaBrowserCallingTimer");
							}
							itent.setAction("com.rtk.TSBTimerSettingMESSAGE");
							itent.putExtras((Bundle) bundle);
							sendBroadcast(itent);
							break;
						}
						default:
							break;
						}
						quickmenuAdapter.notifyDataSetChanged();
						return true;
					} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
						ListView quickMenuContent = quickmenu.getListView();
						int position = quickMenuContent
								.getSelectedItemPosition();
						if (position == 0) {
							quickMenuContent.setSelection(quickMenuContent
									.getCount() - 1);
						}
						return false;
					} else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
						ListView quickMenuContent = quickmenu.getListView();
						int position = quickMenuContent
								.getSelectedItemPosition();
						if (position == quickMenuContent.getCount() - 1) {
							quickMenuContent.setSelection(0);
						}
						return false;
					}

				} else if (event.getAction() == KeyEvent.ACTION_UP) {
					if (keyCode == KeyEvent.KEYCODE_Q || keyCode == 227
							|| keyCode == KeyEvent.KEYCODE_BACK) {
						quickmenu.dismiss();
					} else if (keyCode == KeyEvent.KEYCODE_MENU
							|| keyCode == 220)// STEREO/DUAL for L4300
					{
						quickmenu.dismiss();
						if (msg_notavaible == null)
							msg_notavaible = new Message_not_avaible(
									AudioBrowser.this);
						msg_notavaible.show_msg_notavailable();
						quickMsgAutoQuit();
					}
				}
				return false;

			}

		};
		OnItemSelectedListener quickmenuItemSelectedListener = new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				quickAutoQuit();
				if (MediaApplication.DEBUG)
					Log.d(TAG, "Quick Menu ListView onItemSelected" + ""
							+ quickmenu.getListView().getCount());

				for (int i = 0; i < quickmenu.getListView().getCount(); i++) {
					quickmenuAdapter.setVisibility(i, View.INVISIBLE);
				}
				quickmenuAdapter.setVisibility(position, View.VISIBLE);
				quickmenuAdapter.notifyDataSetChanged();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				if (MediaApplication.DEBUG)
					Log.d(TAG, "Quick Menu ListView onNothingSelected");
			}
		};

		quickmenu.AddOnItemClickListener(quickmenuItemClickListener);
		quickmenu.AddOnItemSelectedListener(quickmenuItemSelectedListener);
		quickmenu.AddOnKeyClickListener(quickmenuKeyListener);
		m_ContentMgr = getApplicationContext().getContentResolver();
		audiolist.post(new Runnable() {
			public void run() {
				listH = audiolist.getMeasuredHeight() / (float) LISTNUM;
				listTop = img_line.getTop();
			}
		});
		homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS); 
		homeRecevier = new InnerRecevier();
	}

	private void showNotAvail() {
		if (msg_notavaible == null)
			msg_notavaible = new Message_not_avaible(AudioBrowser.this);
		msg_notavaible.show_msg_notavailable();
		quickMsgAutoQuit();
	}

	private int tmpIndex = 0;

	public synchronized void startMusic() {
		try {
			if (MediaApplication.DEBUG)
				Log.e(TAG, "---------" + AudioPath + "-----------");
			tmpIndex = index;
			setMusicInfo();
			if (mPlayer == null) {
				mPlayer = map.getMediaPlayer();
				mPlayer.setOnErrorListener(mp_error);
			}
			// mPlayer.reset();
			mPlayer.setPlayerType(6); // use RTK_MediaPlayer
			Map<String, String> config;
			config = new HashMap<String, String>();
			config.put("FLOWTYPE", "PLAYBACK_TYPE_AUDIO_ONLY");
			mPlayer.setDataSource(AudioPath, config);
			if (MediaApplication.DEBUG)
				Log.e(TAG, "method.invoke done");
			mPlayer.setOnCompletionListener(audioCompletionListener);
			mPlayer.setOnInfoListener(infoListener);
			mPlayer.setOnPreparedListener(audioPreparedListener);
			mPlayer.prepareAsync();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			listItems.get(tmpIndex).setCanPlay(-1);
			handler.sendEmptyMessage(UPDATE_UI_BROKEN);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			if (MediaApplication.DEBUG)
				Log.e(TAG, "method.invoke done");
			e.printStackTrace();
		}
	}

	private void getDataProvider(String path) {
		if (path != null) {
			if (mBrowserType == 0) {
				mDataProvider = new DeviceFileDataPrivider(path,
						FileFilterType.DEVICE_FILE_DIR
								| FileFilterType.DEVICE_FILE_AUDIO
								| FileFilterType.DEVICE_FILE_VDIR, -1, 0,
						mMimeTypes);
			}
			mDataProvider.sortListByType();
			map.setFileDirnum(mDataProvider.getDirnum());
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		if (isFirstRun) {
			fakeKeyLeft();
			isFirstRun = false;
		}
	}

	public void onResume() {
		super.onResume();
		String CurApkPackageName = mTv.getCurAPK();
		Log.v(TAG, "onResume CurApkPackageName =" +CurApkPackageName);
		if(CurApkPackageName !=null && CurApkPackageName.length()>0 && !CurApkPackageName.equalsIgnoreCase("com.rtk.mediabrowser")){
			finish();
			return;
		}
		mActivityPauseFlag = 0;
		homePressed = false;
        if (homeRecevier != null) { 
            registerReceiver(homeRecevier, homeFilter); 
        }
		int newPos = index - lastnum;
		int lastPos = lastIndex - lastnum;
		if (newPos >= 0 && newPos < LISTNUM) {
			index = newPos;
		} else if (newPos < 0) {
			lastnum += newPos;
			index = 0;
		} else if (newPos >= LISTNUM) {
			lastnum += (newPos - LISTID);
			index = LISTID;
		}
		audiolist.setSelection(index);
		if (lastPos == index) {
			onTrickItemSelected(index);
		}
		changeIndex = false;
		new Thread(new Runnable() {
			@Override
			public void run() {
				int tmp_index = mPerferences.getInt("repeatIndex", -1);
				if (tmp_index == -1) {
					Editor editor = mPerferences.edit();//
					editor.putInt("repeatIndex", repeatIndex);
					editor.commit();
				} else {
					repeatIndex = tmp_index;
				}
				handler.sendEmptyMessage(MSG_SET_REPEAT);
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

					if (quickmenu.isShowing()) {
						handler.sendEmptyMessage(MSG_REFRESH_TIMER);
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
		quickmenuAdapter.notifyDataSetChanged();
		getInitTimer();
		if (quickmenu != null && quickmenu.isShowing()) {
			quickAutoQuit();
		}
	}

	@Override
	public void onPause() {
		mActivityPauseFlag = 0;
		if (timer2 != null) {
			timer2.cancel();
			timer2 = null;
		}
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (!goingtoFullPlay && mPlayer != null) {
			mPlayer.reset();
		}
		try{
	        if (homeRecevier != null) { 
	            unregisterReceiver(homeRecevier); 
	        }
			}catch(Exception e){}
		goingtoFullPlay = false;
		super.onPause();
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

	private void beforeFinish() {
		if (timer2 != null) {
			timer2.cancel();
			timer2 = null;
		}
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (mPlayer != null) {
			try {
				mPlayer.reset();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		if (curPathInfo != null)
			curPathInfo.cleanLevelInfo();
		super.onDestroy();
		map.releaseMediaPlayer();
		AsyncInfoLoader.isQuit(0);
		mUsbCtrl.UnRegesterBroadcastReceiver();
		unregisterReceiver(mPlayReceiver);
	}

	@Override
	public void onRestart() {
		super.onRestart();
	}

	private void gotoLastLevel() {
		dismissLoading();
		if (mPlayer != null)
			mPlayer.reset();
		if (parentPath.get(dirLevel).equals(rootPath))
			return;
		isFocused = audiolist.isFocused();
		if (curPathInfo.dirLevel > 0
				&& !parentPath.get(dirLevel).equals(rootPath)) {
			parentPath.remove(dirLevel);
			folderPath.remove(dirLevel);
			dirLevel--;
			if (dirLevel == 0)
				devicePath = "";
			getDataProvider(curPathInfo.curPathArr.get(curPathInfo.dirLevel).path);
			simpleAdapter.clearSelected();
			showInList();
			if (!isFocused)
				audiolist.requestFocus();
			int pos = audiolist.getSelectedItemPosition();
			curPathInfo.backToLastLevel();
			if (pos == curPathInfo.getLastLevelFocus())
				onTrickItemSelected(pos);
			else
				audiolist.setSelection(curPathInfo.getLastLevelFocus());
		} else if (!isFocused) {
			audiolist.requestFocus();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK: {
			dismissLoading();
			if (mPlayer != null)
				mPlayer.reset();
			if (parentPath.get(dirLevel).equals(rootPath))
				return super.onKeyDown(keyCode, event);
			isFocused = audiolist.isFocused();
			if (curPathInfo.dirLevel > 0
					&& !parentPath.get(dirLevel).equals(rootPath)) {
				parentPath.remove(dirLevel);
				folderPath.remove(dirLevel);
				dirLevel--;
				if (dirLevel == 0)
					devicePath = "";
				getDataProvider(curPathInfo.curPathArr
						.get(curPathInfo.dirLevel).path);
				simpleAdapter.clearSelected();
				showInList();
				if (!isFocused)
					audiolist.requestFocus();
				curPathInfo.backToLastLevel();
				int focusNum = (curPathInfo.getLastLevelFocus()) % LISTNUM;
				int quotient = (curPathInfo.getLastLevelFocus()) / LISTNUM;
				if (quotient > 1)
					lastnum = (quotient - 1) * LISTNUM + focusNum + 1;
				else if (quotient == 1)
					lastnum = focusNum + 1;
				else
					lastnum = 0;
				simpleAdapter.notifyDataSetChanged();
				if (lastnum > 0) {
					audiolist.setSelection(LISTID);
					onTrickItemSelected(LISTID);
				} else {
					audiolist.setSelection(curPathInfo.getLastLevelFocus());
					onTrickItemSelected(curPathInfo.getLastLevelFocus());
				}
				return true;
			} else if (!isFocused) {
				audiolist.requestFocus();
				return true;
			}
		}
			break;
		case KeyEvent.KEYCODE_ESCAPE:
			beforeFinish();
			try {
				ComponentName componetName = new ComponentName(
						"com.rtk.mediabrowser",// another apk name
						"com.rtk.mediabrowser.MediaBrowser" // another apk
															// activity name
				);
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putInt("browserType", mBrowserType);
				intent.putExtras(bundle);
				intent.setComponent(componetName);
				startActivity(intent);
			} catch (Exception e) {
			}
			finish();
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onPrepared() {
		Max = mPlayer.getDuration();
		audio_bar.setMax(Max);
		Max /= 1000;
		Minute = Max / 60;
		Hour = Minute / 60;
		Second = Max % 60;
		Minute %= 60;
		audio_duration.post(new Runnable() {
			@Override
			public void run() {
				audio_duration.setText(String.format(" %02d:%02d:%02d", Hour,
						Minute, Second));
				audio_hasplayed.setText("00:00:00 /");
				audio_bar.setProgress(0);
			}
		});

		mPlayer.start();
		if (timer2 == null)
			timer2 = new Timer(true);
		task_getduration = new TimerTask() {

			@Override
			public void run() {
				handler.sendEmptyMessage(PROGRESS_CHANGED);
			}
		};
		timer2.schedule(task_getduration, 0, TimerDelay.delay_1s);
	}

	private OnCompletionListener audioCompletionListener = new OnCompletionListener() {

		@Override
		public void onCompletion(MediaPlayer mp) {
			if (timer != null) {
				timer.cancel();
				timer = null;
			}
			if (timer2 != null) {
				timer2.cancel();
				timer2 = null;
			}
			if (mPlayer != null) {
				mPlayer.reset();
			}

			audio_hasplayed.setText(String.format("%02d:%02d:%02d /", Hour,
					Minute, Second));
			audio_bar.setProgress(Max);
		}

	};

	private OnItemSelectedListener itemSelectedListener = new OnItemSelectedListener() {

		public void onItemSelected(AdapterView<?> parent, View v, int position,
				long id) {
			if (MediaApplication.DEBUG)
				Log.e("AudioBrowser", "itemSelectedListener");
			int pos = position + lastnum;
			index = position;
			curPathInfo.setLastLevelFocus(pos);
			setFileIndxTextView();
			simpleAdapter.notifyFocused(index);
			if (mDataProvider.GetFileTypeAt(pos) == FileFilterType.DEVICE_FILE_AUDIO) {
				if (MediaApplication.DEBUG)
					Log.d(TAG, mDataProvider.GetDataAt(pos));
				guide_enter.setText(R.string.guide_play);
				if (listItems.get(pos).getCanPlay() == -1) {
					audio_image
							.setImageResource(map.isIs4k2k() ? R.drawable.broken_video_icon
									: R.drawable.broken_video_icon);
					show(false);
					resetAudioInfo();
					audiolist.setEnabled(false);
					simpleAdapter.notifyDataSetChanged(-1);
					audiolist.setEnabled(true);
					mPlayer.reset();
					if (timer != null) {
						timer.cancel();
						timer = null;
					}

					if (timer2 != null) {
						timer2.cancel();
						timer2 = null;
					}
					return;
				}
				show(true);
				AudioPath = mDataProvider.GetDataAt(pos);
				audio_image
						.setImageResource(map.isIs4k2k() ? R.drawable.music_list_music_on
								: R.drawable.music_list_music_on);
				audio_bar.setProgress(0);
				if (timer != null) {
					timer.cancel();
					timer = null;
				}

				if (timer2 != null) {
					timer2.cancel();
					timer2 = null;
				}
				run = true;
				timer = new Timer(true);
				task_play = new TimerTask() {
					@Override
					public void run() {
						handler.sendEmptyMessage(SHOW_AUDIO_INFO);
					}
				};
				timer.schedule(task_play, TimerDelay.delay_1s);
			} else if (mDataProvider.GetFileTypeAt(pos) == FileFilterType.DEVICE_FILE_DIR
					|| mDataProvider.GetFileTypeAt(pos) == FileFilterType.DEVICE_FILE_DEVICE) {
				show(false);
				guide_enter.setText(R.string.guide_enter);
				if (map.isIs4k2k()) {
					if (mDataProvider.GetFileTypeAt(pos) == FileFilterType.DEVICE_FILE_DIR) {
						audio_image
								.setImageResource(R.drawable.image_mbw_prev_folder);
					} else {
						if (mDataProvider.GetTitleAt(pos).equals(
								MediaApplication.internalStorage))
							audio_image
									.setImageResource(R.drawable.video_listlist_internal);
						else
							audio_image
									.setImageResource(R.drawable.list_common_usb);
					}
				} else {
					if (mDataProvider.GetFileTypeAt(pos) == FileFilterType.DEVICE_FILE_DIR) {
						audio_image
								.setImageResource(R.drawable.image_mbw_prev_folder);
					} else {
						if (mDataProvider.GetTitleAt(pos).equals(
								MediaApplication.internalStorage))
							audio_image
									.setImageResource(R.drawable.video_listlist_internal);
						else
							audio_image
									.setImageResource(R.drawable.list_common_usb);
					}
				}
				audio_hasplayed.setText(" ");
				audio_duration.setText(" ");
				if (mDataProvider.GetFileTypeAt(pos) == FileFilterType.DEVICE_FILE_DIR)
					audio_artist.setText(getApplicationContext().getText(
							R.string.folder_exif)
							+ mDataProvider.GetTitleAt(pos));
				else
					audio_artist.setText(getApplicationContext().getText(
							R.string.device_exif)
							+ mDataProvider.GetTitleAt(pos));
				audio_album.setText(" ");
				audio_bar.setProgress(0);

				if (run && mPlayer != null) {
					if (timer != null) {
						timer.cancel();
						timer = null;
					}

					if (timer2 != null) {
						timer2.cancel();
						timer2 = null;
					}
					run = false;
					try {
						mPlayer.reset();
					} catch (Exception e) {

					}
				}
				simpleAdapter.clearSelected();
				simpleAdapter.notifyDataSetChanged();
				dismissLoading();

			}
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}

	};

	private void dismissLoading() {
		loadingIcon.setVisibility(View.INVISIBLE);
	}

	private boolean goingtoFullPlay = false;
	public boolean onItemLongClick(AdapterView parent, View view, int position,
			   long id) {
		dismissLoading();
		int pos = position + lastnum;
		index = pos;
		if (mDataProvider.GetFileTypeAt(pos) == FileFilterType.DEVICE_FILE_AUDIO) {
			if (listItems.get(pos).getCanPlay() == -1) {
				Toast.makeText(
						getApplicationContext(),
						getApplicationContext().getResources().getString(
								R.string.unsupport_file),
						Toast.LENGTH_SHORT).show();
				return true;
			}
			final String filePath = listItems.get(pos).getPath();
	        String type = FileUtils.getMimeType(filePath);

	        if (!TextUtils.isEmpty(type) && !TextUtils.equals(type, "*/*")) {
	            Intent intent = new Intent();
	            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            intent.setAction(android.content.Intent.ACTION_VIEW);
	            intent.setDataAndType(Uri.fromFile(new File(filePath)), type);
	            startActivity(intent);
	        } else {
	            // unknown MimeType
	            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getApplicationContext());
	            dialogBuilder.setTitle(R.string.dialog_select_type);

	            CharSequence[] menuItemArray = new CharSequence[] {
	            		getApplicationContext().getString(R.string.dialog_type_text),
	            		getApplicationContext().getString(R.string.dialog_type_audio),
	            		getApplicationContext().getString(R.string.dialog_type_video),
	            		getApplicationContext().getString(R.string.dialog_type_image) };
	            dialogBuilder.setItems(menuItemArray,
	                    new DialogInterface.OnClickListener() {
	                        @Override
	                        public void onClick(DialogInterface dialog, int which) {
	                            String selectType = "*/*";
	                            switch (which) {
	                            case 0:
	                                selectType = "text/plain";
	                                break;
	                            case 1:
	                                selectType = "audio/*";
	                                break;
	                            case 2:
	                                selectType = "video/*";
	                                break;
	                            case 3:
	                                selectType = "image/*";
	                                break;
	                            }
	                            Intent intent = new Intent();
	                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	                            intent.setAction(android.content.Intent.ACTION_VIEW);
	                            intent.setDataAndType(Uri.fromFile(new File(filePath)), selectType);
	                            startActivity(intent);
	                        }
	                    });
	            dialogBuilder.show();
	        }
	    }
		if (mPlayer != null) {
			mPlayer.reset();
		} 
	return true;
    };
	private OnItemClickListener itemClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			dismissLoading();
			int pos = position + lastnum;
			index = pos;
			// add by jessie
			if (MediaBrowserConfig.HAVE_PLAYLIST == true
					&& mDataProvider.GetFileTypeAt(pos) == FileFilterType.DEVICE_FILE_VDIR) {
				// VDir Special Treatment
				long currentTime = System.nanoTime();
				System.out.println("currentTime is: " + currentTime);
				curPathInfo.addLevelInfo(parentPath.get(dirLevel));
				curPathInfo.setLevelFocus(curPathInfo.dirLevel, pos);

				parentPath.add(parentPath.get(dirLevel)
						+ mDataProvider.GetTitleAt(pos));
				// display the files which in Vdir
				dirLevel++;
				getDataProvider(parentPath.get(dirLevel));
				simpleAdapter.clearSelected();
				showInList();
				if (pos == 0)
					onTrickItemSelected(pos);
				else
					audiolist.setSelection(0); // add end
			} else if (mDataProvider.GetFileTypeAt(pos) == FileFilterType.DEVICE_FILE_AUDIO) {
				if (listItems.get(pos).getCanPlay() == -1) {
					Toast.makeText(
							getApplicationContext(),
							getApplicationContext().getResources().getString(
									R.string.unsupport_file),
							Toast.LENGTH_SHORT).show();
					return;
				}
				cancelLoadTime();
				ComponentName componetName = new ComponentName(
						"com.rtk.mediabrowser",
						"com.rtk.mediabrowser.MusicActivity");
				lastIndex = index;
				goingtoFullPlay = true;
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("currPath", parentPath.get(dirLevel));
				bundle.putString("devicePath", devicePath);
				bundle.putInt("initPos", pos);
				bundle.putInt("browserType", mBrowserType);
				intent.putExtras(bundle);
				intent.setComponent(componetName);
				startActivityForResult(intent, 0);
				playPosition = 0;
				if (mPlayer != null) {
					if (mPlayer.isPlaying()) {
						playPosition = mPlayer.getCurrentPosition();
					}
					mPlayer.reset();
				}
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
					if (pathTitle.equals(MediaApplication.internalStorage)
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
				simpleAdapter.clearSelected();
				curPathInfo.addLevelInfo(parentPath.get(dirLevel));
				curPathInfo.setLevelFocus(curPathInfo.dirLevel, pos);
				parentPath.add(header + pathTitle + "/");
				dirLevel++;

				getDataProvider(parentPath.get(dirLevel));
				showInList();
				if (pos == 0) {
					// onTrickItemSelected(pos);
					audiolist.setSelected(false);
					audiolist.setSelected(true);
					audiolist.setSelection(0);
					onTrickItemSelected(pos);
				} else {
					audiolist.setSelection(0);
					audiolist.requestFocusFromTouch();
					audiolist.setSelector(R.drawable.selector_list_background);
				}
			}
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0)// plug out usb when ;
		{
			if (resultCode == 1)//
			{
				setResult(UsbRemoved);
				beforeFinish();
				finish();
				return;
			}
		}
	}

	public void getFileList(String path) {
		parentPath.add(path);
		if (path.equals(rootPath))
			folderPath.add("");
		dirLevel++;
		curPathInfo.addLevelInfo(path);
		showInList();
		audiolist.setSelected(false);
		audiolist.setSelected(true);
		audiolist.requestFocusFromTouch();
		audiolist.setSelector(R.drawable.selector_list_background);
		audiolist.setSelection(0);
	}

	private void setFileIndxTextView() {
		if (MediaApplication.DEBUG)
			Log.d(TAG, "setFileIndxTextView" + curPathInfo.getLastLevelFocus());
		if (mTopFileIndexInfoTextView != null && mDataProvider != null) {
			if (mDataProvider.GetFileTypeAt(curPathInfo.getLastLevelFocus()) == FileFilterType.DEVICE_FILE_DIR
					|| mDataProvider.GetFileTypeAt(curPathInfo
							.getLastLevelFocus()) == FileFilterType.DEVICE_FILE_DEVICE) {
				mTopFileIndexInfoTextView.setText("");
				if (MediaApplication.DEBUG)
					Log.d(TAG, "do not show index for dir");
				return;
			}
			if (mDataProvider.GetSize() <= 0) {
				mTopFileIndexInfoStr = "0/0";
			} else {
				int dirnum = mDataProvider.getDirnum();
				mTopFileIndexInfoStr = Integer.toString(
						curPathInfo.getLastLevelFocus() + 1 - dirnum, 10)
						+ "/" + (mDataProvider.GetSize() - dirnum);
			}
			mTopFileIndexInfoTextView.setText(mTopFileIndexInfoStr);
		}
	}

	private void showInList() {
		lastnum = 0;
		if (listItems == null) {
			listItems = map.getFileListItems();
		}
		listItems.clear();

		totalLen = mDataProvider.GetSize();
		if (totalLen == 0) {
			msg_hint.setMessage(getApplicationContext().getResources()
					.getString(R.string.msg_noFile));
			if (true == MediaBrowserConfig
					.getRight2Left(getApplicationContext())) {
				msg_hint.setMessageRight();
			}
			msg_hint.show();
			msg_hint.setFocusable(true);
			msg_hint.update();
			msg_hint.setOnDismissListener(new PopupWindow.OnDismissListener() {
				@Override
				public void onDismiss() {
					handler.sendEmptyMessage(MSG_GO_BACK);
				}
			});
		}
		if (totalLen >= MediaApplication.MAXFILENUM) {
			PopupMessageShow(
					(String) getApplicationContext().getText(
							R.string.maxfilenum), R.drawable.message_box_bg,
					260, 678, Gravity.CENTER, 0, 0, 5000);
		}

		if (MediaApplication.DEBUG)
			Log.e("AudioBrowser", "showInList: size=" + totalLen);
		if (mBrowserType == 1) {
			for (int i = 0; i < totalLen; i++) {

				FileInfo finfo = new FileInfo(mDataProvider.GetFileTypeAt(i),
						mDataProvider.GetFileAt(i),
						mDataProvider.GetTitleAt(i), mDataProvider.GetDataAt(i));
				listItems.add(finfo);
			}
		} else {
			for (int i = 0; i < totalLen; i++) {
				// add by jessie
				if (mDataProvider.GetFileSubTypeAt(i) == FileFilterType.DEVICE_FILE_AUDIO_TRACK) {
					FileInfo finfo = new FileInfo(
							mDataProvider.GetFileTypeAt(i),
							mDataProvider.GetFileSubTypeAt(i),
							mDataProvider.GetFileAt(i),
							mDataProvider.GetCUEListTitleAt(i), mDataProvider
									.GetFileAt(i).getAbsolutePath(),
							mDataProvider.GetFileTotalTime(i),
							mDataProvider.GetFileStartTime(i),
							mDataProvider.GetFileEndTime(i),
							mDataProvider.GetFileDate(i),
							mDataProvider.GetFileAlbumName(i),
							mDataProvider.GetFilePerformer(i));
					listItems.add(finfo);
				} else {
					FileInfo finfo = new FileInfo(
							mDataProvider.GetFileTypeAt(i),
							mDataProvider.GetFileAt(i),
							mDataProvider.GetTitleAt(i), mDataProvider
									.GetFileAt(i).getAbsolutePath());
					listItems.add(finfo);
				}
			}
		}
		if (simpleAdapter == null) {
			simpleAdapter = new FileListAdapter(this, listItems, audiolist);
			audiolist.setAdapter(simpleAdapter);
		} else
			simpleAdapter.notifyDataSetChanged();
		firstVisibleItem = 0;
		if (listItems.size() > LISTNUM)
			lastVisibleItem = LISTID;
		else
			lastVisibleItem = listItems.size() - 1;
		showPageIcon(listItems.size() > LISTNUM, listItems.size() > LISTNUM);
		if (isRight2Left)
			topPath.setText(folderPath.get(dirLevel) + devicePath);
		else
			topPath.setText(devicePath + folderPath.get(dirLevel));
		audiolist.requestFocusFromTouch();
		audiolist.setSelector(R.drawable.selector_list_background);
	}

	public void handleUsbPlugout() {
		beforeFinish();
		this.finish();
	}

	private void PopupMessageShow(String msg, int resid, int height, int width,
			int gravity, int x, int y, final int dismiss_time) {
		if (msg_hint.isShowing() == true) {
			msg_hint.dismiss();
		}
		msg_hint.setMessage(msg);
		msg_hint.show(resid, height, width, gravity, x, y);
		autoQuithint(dismiss_time);
	}

	private void autoQuithint(final int dismiss_time) {
		handler.removeMessages(MSG_HIDE_HINT);
		Message msg = handler.obtainMessage(MSG_HIDE_HINT);
		handler.sendMessageDelayed(msg, dismiss_time);
	}

	private void initAudioImg() {
		if (map.isIs4k2k()) {
			audioImgs[0] = this.getResources().getDrawable(
					R.drawable.list_common_icon_usb);
			audioImgs[1] = this.getResources().getDrawable(
					R.drawable.list_common_icon_folder);
			audioImgs[2] = this.getResources().getDrawable(
					R.drawable.list_common_icon_play);
			audioImgs[3] = this.getResources().getDrawable(
					R.drawable.list_common_icon_vfolder);
			audioImgs[4] = this.getResources().getDrawable(
					R.drawable.list_common_icon_internal);
		} else {
			audioImgs[0] = this.getResources().getDrawable(
					R.drawable.list_common_icon_usb);
			audioImgs[1] = this.getResources().getDrawable(
					R.drawable.list_common_icon_folder);
			audioImgs[2] = this.getResources().getDrawable(
					R.drawable.list_common_icon_play);
			audioImgs[3] = this.getResources().getDrawable(
					R.drawable.list_common_icon_vfolder);
			audioImgs[4] = this.getResources().getDrawable(
					R.drawable.list_common_icon_internal);
		}
	}

	private void initLoading() {
		loadingIcon = (ProgressBar) findViewById(R.id.loadingIcon);
		loadingIcon.setVisibility(View.INVISIBLE);
	}

	private void show(boolean show) {
		if (show) {
			audio_bar.setVisibility(View.VISIBLE);
			audio_hasplayed.setVisibility(View.VISIBLE);
			audio_duration.setVisibility(View.VISIBLE);
		} else {
			audio_bar.setVisibility(View.INVISIBLE);
			audio_hasplayed.setVisibility(View.INVISIBLE);
			audio_duration.setVisibility(View.INVISIBLE);
			audio_duration_tag.setVisibility(View.INVISIBLE);
		}
	}

	private void resetAudioInfo() {
		audio_artist.setText("");
		audio_album.setText("");
	}

	private void setListTouch() {
		audiolist.setOnHoverListener(new OnHoverListener() {
			@Override
			public boolean onHover(View v, MotionEvent event) {

				switch (event.getAction()) {
				case MotionEvent.ACTION_HOVER_ENTER:
					break;
				case MotionEvent.ACTION_HOVER_MOVE:
					transY2Pos((int) event.getRawY());
					break;

				case MotionEvent.ACTION_HOVER_EXIT:
					break;
				}
				return false;
			}
		});
	}

	private void transY2Pos(int y) {
		int line = (int) ((float) (y - listTop) / listH);
		if (line > LISTID)
			line = LISTID;
		if (line > totalLen - 1)
			line = totalLen - 1;
		audiolist.setSelection(line);
	}

	private class ListKeyListener implements OnKeyListener {
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (msg_hint.isShowing()) {
				autoQuithint(0);
			}
			if (foucsVisiable) {
				audiolist.setSelector(R.drawable.selector_list_background);
				focusView.setImageResource(R.drawable.blank);
				focusView.setVisibility(View.INVISIBLE);
				foucsVisiable = false;
			}
			boolean keycase = false;
			int pos = ((ListView) v).getSelectedItemPosition();
			if (keyCode == KeyEvent.KEYCODE_SHIFT_LEFT
					&& event.getAction() == KeyEvent.ACTION_DOWN && isFakeCnt) {
				audiolist.setSelection(lastVisibleItem);
				isFakeCnt = false;
				simpleAdapter.notifyDataSetChanged();
				return true;
			}
			if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				audiolist.setSelected(false);
				audiolist.setSelected(true);
				audiolist.setSelection(0);
				simpleAdapter.notifyDataSetChanged();
				return true;
			}
			int itemnums = listItems.size();
			switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_UP: {
				if (event.getAction() == KeyEvent.ACTION_UP) {
					keycase = true;
					return true;
				}
				if (pos == 0) {
					if (itemnums <= LISTNUM) {
						audiolist.setSelection(itemnums - 1);
						lastVisibleItem = itemnums - 1;
					} else {
						if (lastnum > 0) {
							lastnum--;
							simpleAdapter.notifyDataSetChanged();
							audiolist.setSelection(0);
							onTrickItemSelected(0);
						} else {
							lastnum = itemnums - LISTNUM;
							simpleAdapter.notifyDataSetChanged();
							audiolist.setSelection(LISTID);
						}
					}
					firstVisibleItem = 0;
					keycase = true;
					return true;
				} else {
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
					simpleAdapter.notifyDataSetChanged();
					audiolist.setSelection(0);
					firstVisibleItem = 0;
					if (itemnums > LISTNUM)
						lastVisibleItem = LISTID;
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
					simpleAdapter.notifyDataSetChanged();
					audiolist.setSelection(LISTID);
					onTrickItemSelected(LISTID);
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
					onTrickItemSelected(0);
					keycase = true;
					return true;
				}
				cancelLoadTime();
				if (pos + lastnum == itemnums - 1) {
					lastnum = 0;
					simpleAdapter.notifyDataSetChanged();
					audiolist.setSelection(0);
					firstVisibleItem = 0;
					if (itemnums > LISTNUM)
						lastVisibleItem = LISTID;
					else
						lastVisibleItem = itemnums - 1;
					keycase = true;
					return true;
				} else {
					if (itemnums <= LISTNUM) {
						audiolist.setSelection(itemnums - 1);
						lastVisibleItem = itemnums - 1;
						firstVisibleItem = 0;
					} else if (itemnums - lastVisibleItem - lastnum < LISTNUM + 1)// left<12
					{
						lastnum += itemnums - lastVisibleItem - lastnum - 1;// move
																			// up
																			// lines
						simpleAdapter.notifyDataSetChanged();
						audiolist.setSelection(LISTID);
						firstVisibleItem = 0;
						lastVisibleItem = LISTID;
						if (pos == LISTID)
							onTrickItemSelected(LISTID);
					} else {
						lastnum += LISTNUM;
						simpleAdapter.notifyDataSetChanged();
						audiolist.setSelection(LISTID);
						firstVisibleItem = 0;
						lastVisibleItem = LISTID;
						if (pos == LISTID)
							onTrickItemSelected(LISTID);
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
					onTrickItemSelected(0);
					keycase = true;
					return true;
				}
				cancelLoadTime();
				if (lastnum + pos == 0) {
					if (itemnums <= LISTNUM) {
						lastnum = 0;
						audiolist.setSelection(itemnums - 1);
						firstVisibleItem = 0;
						lastVisibleItem = itemnums - 1;
					} else {
						lastnum = itemnums - LISTNUM;
						simpleAdapter.notifyDataSetChanged();
						audiolist.setSelection(LISTID);
						firstVisibleItem = 0;
						lastVisibleItem = LISTID;
					}
					keycase = true;
					return true;
				} else {
					if (lastnum <= LISTNUM) {
						lastnum = 0;
						audiolist.setSelection(0);
						firstVisibleItem = 0;
						if (itemnums <= LISTNUM)
							lastVisibleItem = itemnums - 1;
						else
							lastVisibleItem = LISTID;
						simpleAdapter.notifyDataSetChanged();
						if (pos == 0)
							onTrickItemSelected(0);
						keycase = true;
						return true;
					} else {
						lastnum -= LISTNUM;
						audiolist.setSelection(0);
						firstVisibleItem = 0;
						lastVisibleItem = LISTID;
						simpleAdapter.notifyDataSetChanged();
						if (pos == 0)
							onTrickItemSelected(0);
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

	private void updateList(int index, String time) {
		listItems.get(index).setTime(time);
		simpleAdapter.notifyDataSetChanged();
	}

	public void onTrickItemSelected(int position) {
		if (MediaApplication.DEBUG)
			Log.e("AudioBrowser", "itemSelectedListener");
		int pos = position + lastnum;
		index = position;
		curPathInfo.setLastLevelFocus(pos);
		setFileIndxTextView();
		simpleAdapter.notifyFocused(index);
		if (mDataProvider.GetFileTypeAt(pos) == FileFilterType.DEVICE_FILE_AUDIO) {
			if (MediaApplication.DEBUG)
				Log.d(TAG, mDataProvider.GetDataAt(pos));
			guide_enter.setText(R.string.guide_play);
			if (listItems.get(pos).getCanPlay() == -1) {
				audio_image
						.setImageResource(map.isIs4k2k() ? R.drawable.broken_video_icon
								: R.drawable.broken_video_icon);
				return;
			}
			show(true);
			AudioPath = mDataProvider.GetDataAt(pos);
			if (map.isIs4k2k())
				audio_image.setImageResource(R.drawable.music_list_music_on);
			else
				audio_image.setImageResource(R.drawable.music_list_music_on);
			audio_bar.setProgress(0);
			if (timer != null) {
				timer.cancel();
				timer = null;
			}

			if (timer2 != null) {
				timer2.cancel();
				timer2 = null;
			}
			run = true;
			timer = new Timer(true);
			task_play = new TimerTask() {
				@Override
				public void run() {
					handler.sendEmptyMessage(SHOW_AUDIO_INFO);
				}
			};
			timer.schedule(task_play, TimerDelay.delay_1s);
		} else if (mDataProvider.GetFileTypeAt(pos) == FileFilterType.DEVICE_FILE_DIR
				|| mDataProvider.GetFileTypeAt(pos) == FileFilterType.DEVICE_FILE_DEVICE) {
			show(false);
			guide_enter.setText(R.string.guide_enter);
			if (map.isIs4k2k()) {
				if (mDataProvider.GetFileTypeAt(pos) == FileFilterType.DEVICE_FILE_DIR)
					audio_image
							.setImageResource(R.drawable.image_mbw_prev_folder);
				else {
					if (mDataProvider.GetTitleAt(pos).equals(
							MediaApplication.internalStorage))
						audio_image
								.setImageResource(R.drawable.video_listlist_internal);
					else
						audio_image
								.setImageResource(R.drawable.list_common_usb);
				}
			} else {
				if (mDataProvider.GetFileTypeAt(pos) == FileFilterType.DEVICE_FILE_DIR)
					audio_image
							.setImageResource(R.drawable.image_mbw_prev_folder);
				else {
					if (mDataProvider.GetTitleAt(pos).equals(
							MediaApplication.internalStorage))
						audio_image
								.setImageResource(R.drawable.video_listlist_internal);
					else
						audio_image
								.setImageResource(R.drawable.list_common_usb);
				}
			}
			audio_hasplayed.setText(" ");
			audio_duration.setText(" ");
			if (mDataProvider.GetFileTypeAt(pos) == FileFilterType.DEVICE_FILE_DIR)
				audio_artist.setText(getApplicationContext().getText(
						R.string.folder_exif)
						+ mDataProvider.GetTitleAt(pos));
			else
				audio_artist.setText(getApplicationContext().getText(
						R.string.device_exif)
						+ mDataProvider.GetTitleAt(pos));

			audio_album.setText(" ");
			audio_bar.setProgress(0);

			if (run && mPlayer != null) {
				if (timer != null) {
					timer.cancel();
					timer = null;
				}

				if (timer2 != null) {
					timer2.cancel();
					timer2 = null;
				}
				run = false;
				mPlayer.reset();
			}
			simpleAdapter.clearSelected();
			simpleAdapter.notifyDataSetChanged();
			dismissLoading();
			audiolist.requestFocusFromTouch();
			audiolist.setSelector(R.drawable.selector_list_background);
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent m) {
		switch (m.getKeyCode()) {
		case 82:// MENU
		case 220:// STEREO/DUAL for L4300
			showNotAvail();
			return true;
		case 227:
		case KeyEvent.KEYCODE_Q: // [QUICk]
			if (m.getAction() == KeyEvent.ACTION_UP) {
				onQuick();
				return true;
			}
		}
		return super.dispatchKeyEvent(m);
	}

	private void onQuick() {
		if (quickmenu.isShowing() == true) {
			quickmenu.dismiss();
			handler.removeMessages(MSG_QUICK_HIDE);
		} else {
			quickmenu.showQuickMenu(14, 14);
			quickAutoQuit();
		}
	}

	private View popview = null;
	private SeekBar sound_bar;
	private TextView txtSound;
	private Button menu_repeat;
	private Button menu_sleep;
	private Button tvapp;
	private Button syssetting;

	private RelativeLayout re_sound;
	private RelativeLayout re_repeat;
	private RelativeLayout re_sleep;
	private RelativeLayout re_tvap;
	private RelativeLayout re_setting;

	OnKeyListener keyListen = new OnKeyListener() {
		public boolean onKey(View v, int key, KeyEvent event) {
			if (event.getAction() == KeyEvent.ACTION_UP) {
				switch (event.getKeyCode()) {
				case 227:
				case KeyEvent.KEYCODE_Q:
				case KeyEvent.KEYCODE_BACK:
					onQuick();
					break;
				}
			}
			return false;
		}
	};

	private void setRepeatIcon(RepeatStatus tag) {
		switch (tag) {
		case OFF:
			imgRepeat.setVisibility(View.INVISIBLE);
			if (menu_repeat != null)
				menu_repeat.setText(getApplicationContext().getText(
						R.string.qm_repeat_off));
			break;
		case ALL:

			if (imgRepeat != null) {
				imgRepeat.setVisibility(View.VISIBLE);
				imgRepeat.setImageDrawable(this.getResources().getDrawable(
						map.isIs4k2k() ? R.drawable.photo_player_repeat_on
								: R.drawable.photo_player_repeat_on));
			}
			if (menu_repeat != null)
				menu_repeat.setText(getApplicationContext().getText(
						R.string.repeat_all));
			break;

		case ONE:
			if (imgRepeat != null) {
				imgRepeat.setVisibility(View.VISIBLE);
				imgRepeat.setImageDrawable(this.getResources().getDrawable(
						map.isIs4k2k() ? R.drawable.photo_repeat_one_on
								: R.drawable.photo_repeat_one_on));
			}
			if (menu_repeat != null)
				menu_repeat.setText(getApplicationContext().getText(
						R.string.repeat_one));
			break;
		default:
			break;
		}
	}

	public class FileListAdapter extends BaseAdapter {
		private List<FileInfo> theItems;
		private AsyncInfoLoader asyncInfoLoader;
		private ListView listView;
		private LayoutInflater mInflater;
		private int selected = -1;
		private int focused = -1;

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

		public void notifyFocused(int id) {
			focused = id;
			super.notifyDataSetChanged();
		}

		public void clearSelected() {
			selected = -1;
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
			final FileInfo info = theItems.get(ab_pos);
			boolean isSelected = false;
			boolean isFocus = false;
			if (selected == ab_pos || info.isPlay()) {
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
					tag.imageView.setImageDrawable(audioImgs[4]);
				else
					tag.imageView.setImageDrawable(audioImgs[0]);
			} else if (info.getmFileType() == FileFilterType.DEVICE_FILE_DIR)
				tag.imageView.setImageDrawable(audioImgs[1]);
			else if (info.getmFileType() == FileFilterType.DEVICE_FILE_AUDIO) {
				if (isSelected && theItems.get(ab_pos).getCanPlay() != -1) {
					tag.imageView.setImageDrawable(audioImgs[2]);
				} else
					tag.imageView.setImageDrawable(null);
			} else if (info.getmFileType() == FileFilterType.DEVICE_FILE_VDIR) {
				tag.imageView.setImageDrawable(audioImgs[3]);
			}// add jessie end
			if (isFocus) {
				tag.title.setEllipsize(TruncateAt.MARQUEE);
			} else {
				tag.title.setEllipsize(TruncateAt.END);
			}
			tag.title.setText(info.getFileName());

			// disabled for the gettotaltime api is not ready
			if (!(theItems.get(ab_pos).getTime() != null && theItems
					.get(ab_pos).getTime().equals("null"))
					&&mBrowserType != 1
					&& info.getmFileType() == FileFilterType.DEVICE_FILE_AUDIO) {
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
											theItems.get(pos).setTime("null");
											return;
										}
										theItems.get(pos).setTime(
												Util.toSecondTime(times
														.longValue()));
										notifyDataSetChanged();
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
						theItems.get(ab_pos).setTime(
								Util.toSecondTime(cachedInt.longValue()));
					}
				}// add by jessie
				else if (ab_pos == theItems.size() - 1
						&& theItems.get(ab_pos).getmSubType() == FileFilterType.DEVICE_FILE_AUDIO_TRACK) {
					String startTime = theItems.get(ab_pos).getCueStartIndex();
					Long mediaTotalTime = asyncInfoLoader.loadTime(url, ab_pos,
							new TimeInfoCallback() {
								public void infoLoaded(Long times, String url,
										int pos) {
									try {
										if (times == null
												|| times.intValue() == -1
												|| times.intValue() == 0) {
											return;
										}
										theItems.get(pos).setTime(
												Util.toSecondTime(times
														.longValue()));
										notifyDataSetChanged();
									} catch (Exception e) {
										if (MediaApplication.DEBUG)
											Log.e("reload", "" + e.getMessage());
									}
								}
							});
					if (mediaTotalTime == null) {
						time.setText("--:--:--");
					} else {
						String endTime = Util.toSecondTime(mediaTotalTime
								.longValue());
						utilCue utilCue = new utilCue();
						endTime = endTime.substring(3, endTime.length())
								+ ":00";
						String playTime = utilCue.getPlayTime(startTime,
								endTime);
						time.setText(playTime);
						theItems.get(ab_pos).setTime(playTime);
						notifyDataSetChanged();
					}
					// add end
				} else {
					time.setText(theItems.get(ab_pos).getTime());
				}
			} else
				tag.time.setText("--:--:--");
			return convertView;
		}

		public final class ViewHolder {
			ImageView imageView;
			TextView title;
			TextView time;
		}
	}

	class QuickMenuAdapter extends BaseAdapter {
		int[] menu_name = new int[] { R.string.quick_menu_repeat,
				R.string.quick_menu_sleep, R.string.quick_menu_tvapp,
				R.string.quick_menu_sysset };
		int[] visibility = new int[] { View.INVISIBLE, View.INVISIBLE,
				View.INVISIBLE, View.INVISIBLE,

		};

		private LayoutInflater mInflater;

		class ViewHolder {
			TextView menu_name;
			ImageView left;
			TextView menu_option;
			ImageView right;
		}

		public QuickMenuAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		public void setVisibility(int position, int isVisible) {
			visibility[position] = isVisible;
		}

		@Override
		public int getCount() {
			return menu_name.length;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
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
				if (position <= 1) {
					holder.left = (ImageView) convertView
							.findViewById(R.id.left_arrow);
					holder.right = (ImageView) convertView
							.findViewById(R.id.right_arrow);
				}
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.menu_name.setText(menu_name[position]);
			switch (position) {
			case 0: {
				if (repeats[repeatIndex] == RepeatStatus.OFF)
					holder.menu_option.setText(getApplicationContext().getText(
							R.string.qm_repeat_off));
				else if (repeats[repeatIndex] == RepeatStatus.ALL)
					holder.menu_option.setText(getApplicationContext().getText(
							R.string.repeat_all));
				else if (repeats[repeatIndex] == RepeatStatus.ONE)
					holder.menu_option.setText(getApplicationContext().getText(
							R.string.repeat_one));
				break;
			}
			case 1: {
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
			}
			case 2: {
				holder.menu_option.setText("");
				break;
			}
			case 3: {
				holder.menu_option.setText("");
				break;
			}
			default:
				break;
			}

			if (position <= 1) {
				holder.left.setVisibility(visibility[position]);
				holder.right.setVisibility(visibility[position]);
			}

			return convertView;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

	}

	private void cancelLoadTime() {
		AsyncInfoLoader.opQueue(0, null);
	}

	private void initUsbCtl() {
		initReceiver();
		mUsbCtrl = new UsbController(this);
		mUsbCtrl.RegesterBroadcastReceiver();
		OnUsbCheckListener usbCheckListener = new OnUsbCheckListener() {
			@Override
			public void OnUsbCheck() {
				if (mBrowserType != 0) {
					return; // do nothing on dmrMode
				}
				// TODO Auto-generated method stub
				switch (mUsbCtrl.getDirection()) {
				case UsbController.PLUG_OUT: {
					if (mUsbCtrl.GetUsbNum() == 0)// deviceNum is 0
					{
						setResult(UsbRemoved);
						beforeFinish();
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
							audiolist.setSelection(0);
					} else {
						File file = new File(parentPath.get(dirLevel));
						if (file == null || !file.exists()) {
							setResult(UsbRemoved);
							beforeFinish();
							finish();
							return;
						}
					}
					break;
				}
				case UsbController.PLUG_IN: {
					if (mUsbCtrl.GetUsbNum() == 0)// in case of plug in and plug
													// out happens at one time
					{
						setResult(UsbRemoved);
						beforeFinish();
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
							audiolist.setSelection(0);
					}
				}
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
							audiolist.setSelection(0);
					} else if (mpath.equals(path)) {
						setResult(UsbRemoved);
						beforeFinish();
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

	private void setMusicInfo() {

		Thread infoThd = new Thread(new Runnable() {
			@Override
			public void run() {
				resetUi();
				if (mBrowserType == 0
						&& (AudioPath.endsWith(".mp3") || AudioPath
								.endsWith(".MP3")))
					processMp3(AudioPath, index + lastnum);
				else if (mBrowserType == 0
						&& (AudioPath.endsWith(".wma") || AudioPath
								.endsWith(".WMA")))
					processWma(AudioPath, index + lastnum);
				else {
					handler.sendEmptyMessage(UPDATE_UI_FACE);
					artist = "unknow";
					String str = FileUtils.removePathExtension(AudioPath);
					album = str;
					audio_artist.post(new Runnable() {
						@Override
						public void run() {
							audio_artist.setText(getApplicationContext()
									.getText(R.string.artist) + ":" + artist);
							audio_album.setText(getApplicationContext()
									.getText(R.string.title) + ":" + album);
						}
					});
				}
			}
		});
		infoThd.start();
	}

	private void processMp3(String path, int id) {
		int tmpindex = id;
		title = null;
		artist = null;
		body = null;
		File sourceFile = null;

		sourceFile = new File(path);
		MP3File mp3file = null;
		try {
			mp3file = new MP3File(sourceFile, 14, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TagException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ReadOnlyFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAudioFrameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AbstractID3v2Tag tag = null;
		if (mp3file != null)
			tag = mp3file.getID3v2Tag();
		if (tag != null) {
			title = tag.getFirst(FieldKey.TITLE);
			album = tag.getFirst(FieldKey.ALBUM);
			artist = tag.getFirst(FieldKey.ARTIST);
			try {
				AbstractID3v2Frame frame = (AbstractID3v2Frame) tag
						.getFrame("APIC");

				if (frame != null) {
					body = (FrameBodyAPIC) frame.getBody();
				}
			} catch (Exception e) {
			}
		}
		if (tmpindex == index + lastnum) {
			handler.sendEmptyMessage(UPDATE_UI_FACE);
			if (title == null || title.length() < 1) {
				String str = FileUtils.removePathExtension(AudioPath);
				title = str;
			}
			if (artist == null || artist.length() < 1)
				artist = "unknow";
			audio_artist.post(new Runnable() {
				@Override
				public void run() {
					audio_artist.setText(getApplicationContext().getText(
							R.string.artist)
							+ ":" + artist);
					audio_album.setText(getApplicationContext().getText(
							R.string.title)
							+ ":" + title);
				}
			});
		}
	}

	private void processWma(String path, int id) {
		int tmpindex = id;
		title = null;
		artist = null;
		body = null;
		TagField tagField = null;

		int imgLen = 0;
		int end = 0;
		File sourceFile = null;

		sourceFile = new File(path);
		AudioFile f;
		try {
			f = AudioFileIO.read(sourceFile);

			if (f.getTag() instanceof AsfTag) {
				AsfTag tag = (AsfTag) f.getTag();
				artist = tag.getFirst(FieldKey.ARTIST);
				title = tag.getFirst(FieldKey.TITLE);
				if (tag.getFields(FieldKey.COVER_ART).size() > 0) {
					tagField = tag.getFields(FieldKey.COVER_ART).get(0);

					// Should have been loaded as special field to make things
					// easier
					if (tagField instanceof AsfTagCoverField) {
						int count = 5;
						String mimeType = null;
						String name = null;
						int endOfMimeType = 0;
						int endOfName = 0;
						while (count < tagField.getRawContent().length - 1) {
							if (tagField.getRawContent()[count] == 0
									&& tagField.getRawContent()[count + 1] == 0) {
								if (mimeType == null) {
									mimeType = new String(
											tagField.getRawContent(), 5,
											(count) - 5, "UTF-16LE");
									endOfMimeType = count + 2;
								} else if (name == null) {
									name = new String(tagField.getRawContent(),
											endOfMimeType, count
													- endOfMimeType, "UTF-16LE");
									endOfName = count + 2;
									break;
								}
								count += 2;
							}
							count += 2; // keep on two byte word boundary
						}
						imgLen = tagField.getRawContent().length - endOfName;
						end = endOfName;
					}
				}
			}
		} catch (CannotReadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TagException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ReadOnlyFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAudioFrameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (tmpindex == index + lastnum) {
			if (imgLen > 0) {
				try {
					final byte[] imageData = new byte[imgLen];
					final int len = imgLen;

					new ByteArrayInputStream(tagField.getRawContent(), end,
							imgLen).read(imageData);

					audio_image.post(new Runnable() {
						@Override
						public void run() {
							audio_image.setImageBitmap(BitmapFactory
									.decodeByteArray(imageData, 0, len));
							loadingIcon.setVisibility(View.INVISIBLE);
						}
					});
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					handler.sendEmptyMessage(UPDATE_UI_FACE);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					handler.sendEmptyMessage(UPDATE_UI_FACE);
				}
			} else
				handler.sendEmptyMessage(UPDATE_UI_FACE);
			if (title == null || title.length() < 1) {
				String str = FileUtils.removePathExtension(AudioPath);
				title = str;
			}
			if (artist == null || artist.length() < 1)
				artist = "unknow";
			audio_artist.post(new Runnable() {
				@Override
				public void run() {
					audio_artist.setText(getApplicationContext().getText(
							R.string.artist)
							+ ":" + artist);
					audio_album.setText(getApplicationContext().getText(
							R.string.title)
							+ ":" + title);
				}
			});
		}
	}

	private void resetUi() {
		audio_artist.post(new Runnable() {
			@Override
			public void run() {
				audio_artist.setText(getApplicationContext().getText(
						R.string.artist)
						+ ":");
				audio_album.setText(getApplicationContext().getText(
						R.string.title)
						+ ":");
			}
		});
	}

	private OnPreparedListener audioPreparedListener = new OnPreparedListener() {

		@Override
		public void onPrepared(MediaPlayer mp) {
			String runningActivity = getRunningActivityName();
			Log.v(TAG, "runningActivity =" +runningActivity);
			if(runningActivity !=null && runningActivity.length()>0 && !runningActivity.equalsIgnoreCase("com.rtk.mediabrowser.AudioBrowser"))
				return;
			String CurApkPackageName = mTv.getCurAPK();
			Log.v(TAG, "audiobrowser onPrepared CurApkPackageName =" +CurApkPackageName);
			if(CurApkPackageName !=null && CurApkPackageName.length()>0 && !CurApkPackageName.equalsIgnoreCase("com.rtk.mediabrowser"))
				return;
			if(mPlayer == null || homePressed )
				return;			
			Max = mPlayer.getDuration();
			audio_bar.setMax(Max);
			Max /= 1000;
			Minute = Max / 60;
			Hour = Minute / 60;
			Second = Max % 60;
			Minute %= 60;
			audio_duration.post(new Runnable() {
				@Override
				public void run() {
					audio_duration.setText(String.format(" %02d:%02d:%02d",
							Hour, Minute, Second));
					audio_hasplayed.setText("00:00:00 /");
					audio_bar.setProgress(0);
				}
			});
			if (playAndSeek && playPosition > 0) {
				// do not do seek,since ape can not seek before start
				// mPlayer.seekTo(playPosition);
				playPosition = 0;
			}
			playAndSeek = false;
			mPlayer.start();
			if (timer2 == null)
				timer2 = new Timer(true);
			task_getduration = new TimerTask() {

				@Override
				public void run() {
					handler.sendEmptyMessage(PROGRESS_CHANGED);
				}
			};
			timer2.schedule(task_getduration, 0, TimerDelay.delay_1s);
			audiolist.setEnabled(false);
			simpleAdapter.notifyDataSetChanged(index + lastnum);
			audiolist.setEnabled(true);
			audiolist.requestFocusFromTouch();
			audiolist.setSelector(R.drawable.selector_list_background);
			audiolist.setSelection(index);
		}
	};

	private Instrumentation in = new Instrumentation();

	private void fakeKeyLeft() {
		new Thread(new Runnable() {
			public void run() {
				in.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_LEFT);
			}
		}).start();
	}

	private OnErrorListener mp_error = new OnErrorListener() {
		public boolean onError(MediaPlayer mp, int what, int extra) {
			if (what == 1 || what == 0x30000000) {
				mp.reset();
				listItems.get(tmpIndex).setCanPlay(-1);
				handler.sendEmptyMessage(UPDATE_UI_BROKEN);
				resetAudioInfo();
				show(false);
			}
			return true;
		}
	};
	private float listTop = 230.0f;
	private float listH = 69.0f;

	private void addPageListener() {
		page_up.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				int itemnums = listItems.size();
				cancelLoadTime();
				if (lastnum == 0) {
					lastnum = itemnums - LISTNUM;
					simpleAdapter.notifyDataSetChanged();
					onTrickItemSelected(LISTID);
					firstVisibleItem = 0;
					lastVisibleItem = LISTID;
				} else {
					if (lastnum <= LISTNUM) {
						lastnum = 0;
						onTrickItemSelected(0);
						firstVisibleItem = 0;
						lastVisibleItem = LISTID;
					} else {
						lastnum -= LISTNUM;
						onTrickItemSelected(0);
						firstVisibleItem = 0;
						lastVisibleItem = LISTID;
					}
					simpleAdapter.notifyDataSetChanged();
				}
			}
		});
		page_down.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				int itemnums = listItems.size();
				cancelLoadTime();
				if (LISTNUM + lastnum >= itemnums) {
					lastnum = 0;
					simpleAdapter.notifyDataSetChanged();
					onTrickItemSelected(0);
					firstVisibleItem = 0;
					lastVisibleItem = LISTID;
				} else {
					if (itemnums - lastVisibleItem - lastnum < LISTNUM + 1)// left<12
					{
						lastnum += itemnums - lastVisibleItem - lastnum - 1;// move
																			// up
																			// lines
						simpleAdapter.notifyDataSetChanged();
						onTrickItemSelected(LISTID);
						firstVisibleItem = 0;
						lastVisibleItem = LISTID;
					} else {
						lastnum += LISTNUM;
						simpleAdapter.notifyDataSetChanged();
						audiolist.setSelected(false);
						audiolist.setSelected(true);
						audiolist.setSelection(LISTID);
						onTrickItemSelected(LISTID);
						firstVisibleItem = 0;
						lastVisibleItem = LISTID;
					}
				}
			}
		});
	}

	private void quickAutoQuit() {
		handler.removeMessages(MSG_QUICK_HIDE);
		Message msg = handler.obtainMessage(MSG_QUICK_HIDE);
		handler.sendMessageDelayed(msg, quick_timeout);
	}

	private void quickMsgAutoQuit() {
		handler.removeMessages(MSG_QUICKMSG_HIDE);
		Message msg = handler.obtainMessage(MSG_QUICKMSG_HIDE);
		handler.sendMessageDelayed(msg, 1000);
	}

	private class Message_not_avaible extends PopupWindow {
		private Activity context;
		private RelativeLayout rp = null;
		public TextView message = null;

		LayoutInflater mInflater = null;

		Message_not_avaible(Activity mContext) {
			super(mContext);

			this.context = mContext;
			mInflater = LayoutInflater.from(context);
			rp = (RelativeLayout) mInflater.inflate(
					R.layout.message_not_available, null);
			message = (TextView) rp.findViewById(R.id.not_available);
			setContentView(rp);
		}

		public void show_msg_notavailable() {
			TextPaint paint = message.getPaint();
			int len = (int) paint.measureText((String) context.getResources()
					.getText(R.string.toast_not_available)) + 102;
			message.setText(context.getResources().getText(
					R.string.toast_not_available));
			setHeight(72);
			setWidth(len);
			message.setTextColor(Color.BLACK);
			this.setFocusable(true);
			this.setOutsideTouchable(true);
			this.showAtLocation(rp, Gravity.LEFT | Gravity.BOTTOM, 18, 18);

		}
	}

	private void getInitTimer() {
		new Thread(new Runnable() {
			@Override
			public void run() {

				int mins = getSleepTimeValue();
				mSleepTimeHour = mins / 60;
				mSleepTimeMin = mins % 60;

			}
		}).start();
	}

	private boolean isRight2Left = false;
	private int playPosition = 0;
	private boolean playAndSeek = false;

	private OnInfoListener infoListener = new OnInfoListener() {

		@Override
		public boolean onInfo(MediaPlayer mp, int what, int extra) {
			Log.e(TAG, "infoListener" + "---what = " + what + "----");
			switch (what) {
			case 0x10000003: // UNKNOWN_FORMAT /* FOR AUDIO */
			{

				msg_hint.setMessage(getApplicationContext().getResources()
						.getString(R.string.FatalErrorCode_Audio_Unknow_Format));
				if (isRight2Left)
					msg_hint.setMessageRight();
				else
					msg_hint.setMessageLeft();
				msg_hint.show();
				mp.reset();
				listItems.get(tmpIndex).setCanPlay(-1);
				handler.sendEmptyMessage(UPDATE_UI_BROKEN);
				resetAudioInfo();
				show(false);
				autoQuithint(200);
			}
				break;
			default:
				break;
			}
			return false;
		}

	};
	private BroadcastReceiver mPlayReceiver = null;
	private IntentFilter mIntentFilter = null;

	private void initReceiver() {
		mPlayReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				Log.e(TAG, "com.rtk.mediabrowser.broadcast audio");
				String ext = intent.getStringExtra("intentTag");
				if (!ext.equalsIgnoreCase("audio"))
					finish();
			}
		};
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction("com.rtk.mediabrowser.broadcast");
		registerReceiver(mPlayReceiver, mIntentFilter);
	}
	
	 private String getRunningActivityName(){        
	        ActivityManager activityManager=(ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	        String runningActivity=activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
	        return runningActivity;               
	    }
	    private IntentFilter homeFilter;  
	    private InnerRecevier homeRecevier;
	    class InnerRecevier extends BroadcastReceiver { 
	        final String SYSTEM_DIALOG_REASON_KEY = "reason"; 
	        final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions"; 
	        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps"; 
	        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey"; 
	        @Override 
	        public void onReceive(Context context, Intent intent) { 
	            String action = intent.getAction(); 
	            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) { 
	                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY); 
	                if (reason != null) { 
	                    Log.e(TAG, "action:" + action + ",reason:" + reason); 
	                        if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) { 
	                            onHomePressed(); 
	                        } 
	                    } 
	                } 
	            }
	        }
	    private boolean homePressed = false;
	    private void onHomePressed(){
	    	if(	mPlayer !=null)
	    	{
	    		homePressed = true;
	    		mActivityPauseFlag = 1;
	    		try{
	    		mPlayer.reset();
	    		mPlayer = null;
	    		}catch(Exception e){}
	    	}
	    }
}
