package com.rtk.mediabrowser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.R.color;
import android.graphics.Color;
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

import com.realtek.DataProvider.AbstractDataProvider;
import com.realtek.DataProvider.DeviceFileDataPrivider;
import com.realtek.DataProvider.FileFilterType;
import com.realtek.Utils.FileInfo;
import com.realtek.Utils.FileUtils;
import com.realtek.Utils.MimeTypes;
import com.realtek.Utils.SpectrumView;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.Metadata;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.TvManager;
import android.app.tv.SpectrumDataInfo;
import android.content.ActivityNotFoundException;
import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.text.TextPaint;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnHoverListener;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class MusicActivity extends Activity {

	private String TAG = "MusicActivity";

	private Toast toast;
	private Thread barThd = null;
	private boolean finishThd = false;
	private int index;
	private int firstindex;
	private ArrayList<FileInfo> musicList;
	private ArrayList<String> anywhereList;

	private int sta_play = 0;
	private int sta_pause = 1;
	private int sta_stop = 3;

	private ProgressBar musicBar;
	private TextView time_now;
	private TextView musicNumber;
	private TextView music_title;
	private TextView music_artist;
	private String sTitle;
	private String sArtist;
	private ImageButton btn_repeat;
	private ImageButton gui_last;
	private ImageButton gui_next;
	private ImageButton gui_play;
	private ImageButton gui_rw;
	private ImageButton gui_fw;
	private ImageButton gui_repeat;

	private int repeatIndex = 0;
	private RepeatStatus[] repeats = { RepeatStatus.OFF, RepeatStatus.ALL,
			RepeatStatus.ONE };
	private long lasttime = 0;
	private long banner_timeout = 10000;
	private long seek_timeout = 3000;
	private long gui_timeout = 6000;
	private long quick_timeout = 6000;
	private int spectrum_timedelay = 100;
	private boolean isBanner = true;
	private boolean showGui = false;
	private RelativeLayout lay_banner = null;
	private RelativeLayout lay_gui = null;
	private PopupWindow popup;
	private View popview = null;
	private int mSleepTimeHour = 0, mSleepTimeMin = 0;
	private ImageView imgSta;;
	private SharedPreferences mPerferences = null;
	private SpectrumView spectrum = null;
	private TvManager mTv = null;
	private MediaApplication map = null;
	private QuickMenu quickmenu = null;
	private QuickMenuAdapter quickmenuAdapter = null;
	private boolean firstCreate = true;
	private Intent m_intent = null;
	private MediaPlayer mMediaPlayer = null;
	private boolean fromAnywhere = false;
	private boolean fromUri = false;
    private Uri mUri;
	private int len = 0;
	private int playPosition = 0;
	private int prePosition = 0;
	UsbController mUsbCtrl = null;
	private long m_duration;
	private int[] forwardDarwable = { R.drawable.video_player_forward_2x,
			R.drawable.video_player_forward_3x,
			R.drawable.video_player_forward_4x,
			R.drawable.video_player_forward_5x,
			R.drawable.video_player_forward_4x,
			R.drawable.video_player_forward_3x };
	private int[] backDarwable = { R.drawable.video_player_backward_2x,
			R.drawable.video_player_backward_3x,
			R.drawable.video_player_backward_4x,
			R.drawable.video_player_backward_5x,
			R.drawable.video_player_backward_4x,
			R.drawable.video_player_backward_3x };
	private ServiceReceiver receiver = new ServiceReceiver();

	private static final float banner_h = 75f;
	private static final long bannerAnimTime = 200;
	private static final long id3_timeout = 2000;
	public Metadata metadata = null;
	private int direction = 1;
	private String path = "";
	private String devicePath = "";
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

	private void initQuickMenu() {
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
					ComponentName componetName = new ComponentName(
							"com.tsb.tv", "com.tsb.tv.Tv_strategy_lite");
					Intent intent = new Intent();
					intent.setComponent(componetName);
					Bundle bundle = new Bundle();
					bundle.putInt("TVMainMenu", 5);
					intent.putExtras(bundle);
					onbackPlay = true;
					try {
						startActivity(intent);
					} catch (ActivityNotFoundException e) {
						Log.e("Error",
								"ActivityNotFoundException: com.tsb.tv.Tv_strategy_lite");
						Toast.makeText(getApplicationContext(),
								"Can not find the TV App!", Toast.LENGTH_SHORT)
								.show();
					}
					quickmenu.dismiss();
					break;
				}
				case 1: {
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
					execAction(SET_REPEAT, repeats[repeatIndex].name());
					break;
				}
				case 2: {
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
				case 3: {
					try {
						ComponentName componetName = new ComponentName(
								"com.tsb.tv", "com.tsb.tv.Tv_strategy");
						Intent intent = new Intent();
						intent.setComponent(componetName);
						startActivity(intent);
						handler.removeMessages(MSG_QUICK_HIDE);
					} catch (ActivityNotFoundException e) {
						Log.e("Error",
								"ActivityNotFoundException: com.tsb.tv.Tv_strategy");
						Toast.makeText(getApplicationContext(),
								"Can not find the TV App!", Toast.LENGTH_SHORT)
								.show();
					}
					break;
				}
				case 4: {
					try {
						ComponentName componetName = new ComponentName(
								"com.android.settings",
								"com.android.settings.Settings");
						Intent intent = new Intent();
						intent.setComponent(componetName);
						startActivity(intent);
						handler.removeMessages(MSG_QUICK_HIDE);
					} catch (ActivityNotFoundException e) {
						Log.e("Error",
								"ActivityNotFoundException: com.android.settings.Settings");
						Toast.makeText(getApplicationContext(),
								"Can not find the Setting App!",
								Toast.LENGTH_SHORT).show();
					}
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
					ListView lv = quickmenu.getListView();
					if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
						// TODO: switch option
						switch (lv.getSelectedItemPosition()) {
						case 1: {
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
							execAction(SET_REPEAT, repeats[repeatIndex].name());
							break;
						}
						case 2: {
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
						switch (lv.getSelectedItemPosition()) {
						case 1: {
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
							execAction(SET_REPEAT, repeats[repeatIndex].name());
							break;
						}
						case 2: {
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
									MusicActivity.this);
						msg_notavaible.show_msg_notavailable();
						quickMsgAutoQuit();
					}
				}
				return false;

			}

		};
		OnItemSelectedListener quickmenuItemSelectedListener = new OnItemSelectedListener() {
			ListView lv = quickmenu.getListView();

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				quickAutoQuit();
				for (int i = 0; i < lv.getCount(); i++) {
					quickmenuAdapter.setVisibility(i, View.INVISIBLE);
				}
				quickmenuAdapter.setVisibility(position, View.VISIBLE);
				quickmenuAdapter.notifyDataSetChanged();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		};

		quickmenu.AddOnItemClickListener(quickmenuItemClickListener);
		quickmenu.AddOnItemSelectedListener(quickmenuItemSelectedListener);
		quickmenu.AddOnKeyClickListener(quickmenuKeyListener);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		useStrictMode();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.musicplayer);
		View v = (View) findViewById(R.id.main);
		m_ContentMgr = getApplicationContext().getContentResolver();
		v.setOnHoverListener(new OnHoverListener() {
			@Override
			public boolean onHover(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_HOVER_MOVE:
					showGui();
					break;
				}
				return false;
			}
		});
		map = (MediaApplication) getApplication();
		is4k = map.isIs4k2k();
		mMimeTypeXml = getResources().getXml(R.xml.mimetypes);
		mMimeTypes = Util.GetMimeTypes(mMimeTypeXml);
		// GetMimeType
		createPlayer();
		initUsbCtl();
		initGui();
		getAsyncQueryHandler();
		obj = new Object();
		musicBar = (ProgressBar) findViewById(R.id.playBar);
		time_now = (TextView) findViewById(R.id.timeNow);
		music_title = (TextView) findViewById(R.id.musicTitle);
		music_artist = (TextView) findViewById(R.id.musicArtist);
		btn_repeat = (ImageButton) findViewById(R.id.btn_repeat);

		musicNumber = (TextView) findViewById(R.id.musicNumber);
		musicNumber.setText("0/0");
		spectrum = (SpectrumView) findViewById(R.id.spectrum);
		mPerferences = PreferenceManager.getDefaultSharedPreferences(this);
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
					execAction(SET_REPEAT, repeats[repeatIndex].name());
				}
				handler.sendEmptyMessage(MSG_SET_REPEAT);
			}
		}).start();
		setRepeatIcon(repeats[repeatIndex]);
		lay_banner = (RelativeLayout) findViewById(R.id.lay_banner);
		lay_gui = (RelativeLayout) findViewById(R.id.lay_gui);
		handlerTask.postDelayed(task_play2_runnable, banner_timeout);
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		popview = layoutInflater.inflate(R.layout.quick_menu, null);
		popview.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP) {
					if (keyCode == KeyEvent.KEYCODE_Q || keyCode == 227) {
						popup.dismiss();
						return true;
					}
				}
				return false;
			}
		});
		setRepeatIcon(repeats[repeatIndex]);
		imgSta = (ImageView) findViewById(R.id.imgSta);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		initQuickMenu();
		initWorker();	
		homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS); 
		homeRecevier = new InnerRecevier();
        mUri = getIntent().getData();
        if (mUri != null) {
            fromUri = true;
            playUriAudio();
            return;
        }
		if (getIntent().getBooleanExtra("isanywhere", false)) {
			fromAnywhere = true;
			len = getIntent().getIntExtra("len", 0);
			if (len < 1) {
				finish();
			}
		}
		if (getIntent().getIntExtra("initPos", -1) == -1) {
			popMsg("playlist is null");
			finish();
		} else {
			firstCreate = true;
			m_intent = getIntent();
		}
	}

	private void showNotAvail() {
		if (msg_notavaible == null)
			msg_notavaible = new Message_not_avaible(MusicActivity.this);
		msg_notavaible.show_msg_notavailable();
		quickMsgAutoQuit();
	}

	protected void onNewIntent(Intent intent) {

		super.onNewIntent(intent);
        mUri = getIntent().getData();
        if (mUri != null) {
            fromUri = true;
            playUriAudio();
            return;
        }
		if (getIntent().getBooleanExtra("isanywhere", false)) {
			fromAnywhere = true;
			len = getIntent().getIntExtra("len", 0);
			if (len < 1) {
				finish();
			}
		}
		if (getIntent().getIntExtra("initPos", -1) == -1) {
			popMsg("playlist is null");
			finish();
		} else {
			startFromIntent(intent);
		}
	}

	private int initPos = 0;

	private void startFromIntent(Intent intent) {
		isBackground = false;
		initPos = intent.getIntExtra("initPos", 0);
		String[] attr = intent.getStringArrayExtra("filelist");
		mBrowserType = intent.getIntExtra("browserType", 0);
		if (fromAnywhere) {
			anywhereList = new ArrayList<String>();
			for (String str : attr) {
				anywhereList.add(str);
			}
			firstindex = 0;
		} else {
			devicePath = intent.getStringExtra("devicePath");
			if (musicList == null) {
				musicList = map.getFileListItems();
				firstindex = map.getFileDirnum();
				if (musicList.size() == 0) {
					path = intent.getStringExtra("currPath");
					getDataProvider(path);
					int total = mDataProvider.GetSize();
					firstindex = mDataProvider.getDirnum();
					if (mBrowserType == 1) {
						for (int i = 0; i < total; i++) {

							FileInfo finfo = new FileInfo(
									mDataProvider.GetFileTypeAt(i),
									mDataProvider.GetFileAt(i),
									mDataProvider.GetTitleAt(i),
									mDataProvider.GetTitleAt(i));
							musicList.add(finfo);
						}
					} else {
						for (int i = 0; i < total; i++) {
							FileInfo finfo = new FileInfo(
									mDataProvider.GetFileTypeAt(i),
									mDataProvider.GetFileAt(i),
									mDataProvider.GetTitleAt(i), mDataProvider
											.GetFileAt(i).getAbsolutePath());
							musicList.add(finfo);
						}
					}
				}
			}
		}

		index = initPos;
		setMusicInfo();
		musicBar.setProgress(0);
		execAction(PLAY_ACTION, "");
	}

	public void popMsg(String msg) {
		if (toast == null) {
			toast = Toast.makeText(getApplicationContext(), msg,
					Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
		} else
			toast.setText(msg);
		toast.show();
	}

	private boolean is4k = false;
	private final int MSG_TOTAL_TIME = 1;
	private final int MSG_PROCESS_TIME = 2;
	private final int MSG_RESET = 3;
	private final int MSG_INFO_UI = 4;
	private final int MSG_HACK_TIME = 5;
	private final int MSG_RESET_FWD = 6;
	private final int MSG_BANNER = 7;
	private final int MSG_SPECTRUM = 8;
	private final int MSG_RETURN = 9;
	private final int MSG_PLAY = 10;
	private final int MSG_PAUSE = 11;
	private final int UPDATE_SPECTRUM = 12;
	private final int MSG_NEXT = 13;
	private final int MSG_LAST = 14;
	private final int MSG_PLAY_NEXT = 15;
	private final int MSG_GUI_SHOW = 16;
	private final int MSG_GUI_HIDE = 17;
	private final int MSG_SEEK_OUT = 18;
	private final int MSG_SET_REPEAT = 19;
	private final int MSG_QUICK_HIDE = 20;
	private final int MSG_QUICKMSG_HIDE = 21;
	private final int MSG_REFRESH_TIMER = 22;
	private final int MSG_FORWARD = 23;
	private long mMediaId = -1;
    private AsyncQueryHandler mAsyncQueryHandler;
    private void getAsyncQueryHandler(){
    	mAsyncQueryHandler= new AsyncQueryHandler(getContentResolver()) {
        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            if (cursor != null && cursor.moveToFirst()) {

                int titleIdx = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int artistIdx = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                int idIdx = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                int displaynameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);

                if (idIdx >=0) {
                    mMediaId = cursor.getLong(idIdx);
                }
                
                if (titleIdx >= 0) {
                    String title = cursor.getString(titleIdx);
					music_title.setText(title);
                    if (artistIdx >= 0) {
                        String artist = cursor.getString(artistIdx);
                        music_artist.setText(artist);
                    }
                } else if (displaynameIdx >= 0) {
                    String name = cursor.getString(displaynameIdx);
                    music_title.setText(name);
                } else {
                    // Couldn't find anything to display, what to do now?
                    Log.w(TAG, "Cursor had no names for us");
                }
            } else {
                Log.w(TAG, "empty cursor");
            }

            if (cursor != null) {
                cursor.close();
            }
        }
    };
    }
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_TOTAL_TIME:
				if (mMediaPlayer != null) {
					musicBar.setMax(mMediaPlayer.getDuration());
					m_duration = mMediaPlayer.getDuration();
					time_now.setText("00:00:00 / " + Util.toTime(m_duration));
					int dirnum = map.getFileDirnum();
					String str = "";
					if (fromAnywhere) {
						str = Integer.toString(index + 1, 10) + "/" + len;
					} else if(fromUri)
					{
						str = "1/1";
					}else
						str = Integer.toString(index + 1 - dirnum, 10) + "/"
								+ (musicList.size() - dirnum);
					musicNumber.setText(str);
					imgSta.setImageDrawable(getApplicationContext()
							.getResources().getDrawable(
									is4k ? R.drawable.au_play
											: R.drawable.au_play));
					gui_play.setImageDrawable(getApplicationContext()
							.getResources().getDrawable(
									is4k ? R.drawable.gui_pause
											: R.drawable.gui_pause));
				}
				break;
			case MSG_PROCESS_TIME: {
				if (mMediaPlayer == null)
					break;
				int timePosition = mMediaPlayer.getCurrentPosition();
				if (hacktick > 0
						&& (timePosition <= hacktick
								|| timePosition == prePosition || timePosition
								- hacktick > 1000)) {
					timePosition = hacktick;
				} else {
					hacktick = 0;
					handler.removeMessages(MSG_SEEK_OUT);
				}
				if (hacktick == 0) {
					if (seeking && timePosition > 1000)
						break;
					else {
						seeking = false;
						handler.removeMessages(MSG_SEEK_OUT);
					}
				}
				musicBar.setProgress(timePosition); //
				time_now.setText(Util.toTime(timePosition) + " / "
						+ Util.toTime(m_duration));
			}
				break;
			case MSG_RESET: {
				music_title.setText("");
				music_artist.setText("");
				String str = "";
				if (fromAnywhere) {
					str = Integer.toString(index + 1, 10) + "/" + len;
				} else if(fromUri)
				{
					str = "1/1";
				}else
					str = Integer.toString(index + 1 - map.getFileDirnum(), 10)
							+ "/" + (musicList.size() - map.getFileDirnum());
				musicNumber.setText(str);
				setMusicInfo();
				musicBar.setProgress(0);
			}
				break;
			case MSG_INFO_UI: {
				if (sTitle == null || sTitle.length() == 0) {
					if (fromAnywhere) {
						music_title.setText("Unknown");
					} else if(fromUri)
					{
						music_title.setText("Unknown");
					}
					else {
						String str = FileUtils.removeExtension(musicList.get(
								index).getFileName());
						music_title.setText(str);
					}
				} else
					music_title.setText(sTitle);
				if (sArtist == null || sArtist.length() == 0)
					music_artist.setText("Unknown");
				else if(fromUri)
				{
					music_title.setText("Unknown");
				}
				else
					music_artist.setText("" + sArtist);
			}
				break;
			case MSG_HACK_TIME: {
				musicBar.setProgress(hacktick); //
				time_now.setText(Util.toTime(hacktick) + " / "
						+ Util.toTime(m_duration));

			}
				break;
			case MSG_RESET_FWD: {
				handlerTask.removeCallbacks(task_play_runnable);
				handlerTask.removeCallbacks(task_play2_runnable);
				handlerTask.postDelayed(task_play2_runnable, 4000);
			}
				break;
			case MSG_BANNER: {
				if (!isBanner) {
					isBanner = true;
					lay_banner.setVisibility(View.VISIBLE);
					animateShowBanner();
				}

			}
				break;

			case MSG_SPECTRUM: {
				if (isBanner) {
					isBanner = false;
					animateHideBanner();
				}

			}
				break;
			case MSG_RETURN:

				break;
			case MSG_PLAY:
				imgSta.setImageDrawable(getApplicationContext().getResources()
						.getDrawable(
								is4k ? R.drawable.au_play : R.drawable.au_play));
				gui_play.setImageDrawable(getApplicationContext()
						.getResources().getDrawable(
								is4k ? R.drawable.gui_pause
										: R.drawable.gui_pause));
				break;
			case MSG_PAUSE:
				imgSta.setImageDrawable(getApplicationContext().getResources()
						.getDrawable(
								is4k ? R.drawable.au_pause
										: R.drawable.au_pause));
				gui_play.setImageDrawable(getApplicationContext()
						.getResources().getDrawable(
								is4k ? R.drawable.gui_play
										: R.drawable.gui_play));
				break;
			case UPDATE_SPECTRUM:
				long[] fft = null;
				fft = msg.getData().getLongArray("fft");
				;
				spectrum.updateSpectrum(fft);
				break;
			case MSG_NEXT:
				imgSta.setImageDrawable(getApplicationContext().getResources()
						.getDrawable(
								is4k ? R.drawable.player_common_forward
										: R.drawable.player_common_forward));
				break;
			case MSG_LAST:
				imgSta.setImageDrawable(getApplicationContext().getResources()
						.getDrawable(
								is4k ? R.drawable.player_common_backward
										: R.drawable.player_common_backward));
				break;
			case MSG_PLAY_NEXT:
				if (fromAnywhere) {
					if (index == anywhereList.size() - 1)
						finish();
				}else if(fromUri)
				{
					finish();
				} else {
					int i;
					if (direction > 0) {
						for (i = index + 1; i < musicList.size(); i++) {
							if (musicList.get(i).getCanPlay() != -1)
								break;
						}
						if (i < musicList.size()) {
							index = i - 1;
							next();
						} else {
							for (i = firstindex; i < index; i++) {
								if (musicList.get(i).getCanPlay() != -1)
									break;
							}
							if (i < index) {
								if (i == firstindex) {
									i = musicList.size();
								}
								index = i - 1;
								next();
							} else {
								popMsg(getResources().getString(
										R.string.novalidfile));
								finish();
							}
						}
					} else {
						for (i = index - 1; i >= firstindex; i--) {
							if (musicList.get(i).getCanPlay() != -1)
								break;
						}
						if (i >= firstindex) {
							index = i + 1;
							previous();
						} else {
							for (i = musicList.size() - 1; i > index; i--) {
								if (musicList.get(i).getCanPlay() != -1)
									break;
							}
							if (i > index) {
								if (i == musicList.size() - 1) {
									index = firstindex;
								} else
									index = i + 1;
								previous();
							} else {
								popMsg(getResources().getString(
										R.string.novalidfile));
								finish();
							}
						}
					}
				}
				break;
			case MSG_GUI_SHOW: {
				if (!showGui) {
					showGui = true;
					lay_gui.setVisibility(View.VISIBLE);
					gui_play.requestFocus();
				}

			}
				break;

			case MSG_GUI_HIDE: {
				if (showGui) {
					showGui = false;
					lay_gui.setVisibility(View.GONE);
				}

			}
				break;
			case MSG_SEEK_OUT: {
				hacktick = 0;
				seeking = false;
			}
				break;
			case MSG_SET_REPEAT: {
				setRepeatIcon(repeats[repeatIndex]);
			}
				break;
			case MSG_QUICK_HIDE: {
				quickmenu.dismiss();
			}
				break;
			case MSG_QUICKMSG_HIDE: {
				msg_notavaible.dismiss();
			}
				break;
			case MSG_REFRESH_TIMER: {
				quickmenuAdapter.notifyDataSetChanged();
			}
				break;
			case MSG_FORWARD:
				gui_play.setImageDrawable(getApplicationContext()
						.getResources().getDrawable(
								is4k ? R.drawable.gui_play
										: R.drawable.gui_play));
				break;
			}
		}
	};

	private void setMusicInfo() {

		Thread infoThd = new Thread(new Runnable() {
			@Override
			public void run() {
				int tmpindex = index;
				String filePath = "";
				if (fromAnywhere) {
					filePath = anywhereList.get(tmpindex);
				}else if(fromUri)
				{
					//todo
					return;
				}
				else
					filePath = musicList.get(tmpindex).getPath();
				if (mBrowserType == 0
						&& (filePath.endsWith(".mp3") || filePath
								.endsWith(".MP3")))
					processMp3(filePath, index);
				else if (mBrowserType == 0
						&& (filePath.endsWith(".wma") || filePath
								.endsWith(".WMA")))
					processWma(filePath, index);
				else {
					sTitle = null;
					sArtist = null;
					handler.sendEmptyMessage(MSG_INFO_UI);
				}
			}
		});
		infoThd.start();
	}

	private void processMp3(String path, int id) {
		spectrum_timedelay = 100;
		int tmpindex = id;
		sTitle = null;
		sArtist = null;
		Message msg = handler.obtainMessage(MSG_INFO_UI);
		handler.removeMessages(MSG_INFO_UI);
		handler.sendMessageDelayed(msg, id3_timeout);
		File sourceFile = new File(path);
		try {
			MP3File mp3file = new MP3File(sourceFile, 14, true);
			AbstractID3v2Tag tag = mp3file.getID3v2Tag();
			if (tag != null) {
				sTitle = tag.getFirst(FieldKey.TITLE);
				sArtist = tag.getFirst(FieldKey.ARTIST);

				if (index == tmpindex) {
					handler.sendEmptyMessage(MSG_INFO_UI);
				}
			}
		} catch (Exception e) {
			if (MediaApplication.DEBUG)
				Log.e("000", "no tags   " + e.getMessage());
			handler.sendEmptyMessage(MSG_INFO_UI);
		}
	}

	private void processWma(String path, int id) {
		spectrum_timedelay = 300;
		int tmpindex = id;
		sTitle = null;
		sArtist = null;
		File sourceFile = null;

		sourceFile = new File(path);
		AudioFile f;
		try {
			f = AudioFileIO.read(sourceFile);

			if (f.getTag() instanceof AsfTag) {
				AsfTag tag = (AsfTag) f.getTag();
				sArtist = tag.getFirst(FieldKey.ARTIST);
				sTitle = tag.getFirst(FieldKey.TITLE);
			}
		} catch (CannotReadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(MSG_INFO_UI);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(MSG_INFO_UI);
		} catch (TagException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(MSG_INFO_UI);
		} catch (ReadOnlyFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(MSG_INFO_UI);
		} catch (InvalidAudioFrameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(MSG_INFO_UI);
		}
		if (tmpindex == index) {
			handler.sendEmptyMessage(MSG_INFO_UI);
		}
	}

	public void onPause() {
		super.onPause();
		mActivityPauseFlag = 1;
		if (!onbackPlay) {
			try{
			if (receiver != null) {
				unregisterReceiver(receiver);
				receiver = null;
			}
			if (mMediaPlayer != null) {
				mMediaPlayer.reset();
				status = sta_stop;
				mTv.disableSpectrumData();
				if (mMediaPlayer.isPlaying()) {
					playPosition = mMediaPlayer.getCurrentPosition();
				}
				if (fromAnywhere) {
					AudioBrowser.reset = true;
				}
			}
			handlerTask.removeCallbacks(spectrumTask_runnable);
			}catch(Exception e){e.printStackTrace();}
		}
		try{
	        if (homeRecevier != null) { 
	            unregisterReceiver(homeRecevier); 
	        } 
			}catch(Exception e){}
	}

	public void onStop() {
		super.onStop();
		if (onbackPlay) {
			onbackPlay = false;
			if (receiver != null) {
				unregisterReceiver(receiver);
				receiver = null;
			}
			if (status == sta_play && mMediaPlayer != null) {
				if (mMediaPlayer.isPlaying()) {
					playPosition = mMediaPlayer.getCurrentPosition();
					mMediaPlayer.pause();
					status = sta_pause;
					mTv.disableSpectrumData();
				}
				if (fromAnywhere) {
					AudioBrowser.reset = true;
				}
			}
			handlerTask.removeCallbacks(spectrumTask_runnable);
		}
	}

	public void onResume() {
		super.onResume();
		
		if (mTv == null) {
			mTv = (TvManager) getSystemService("tv");
		}
		String CurApkPackageName = mTv.getCurAPK();
		Log.v(TAG, "onResume CurApkPackageName =" +CurApkPackageName);
		if(CurApkPackageName !=null && CurApkPackageName.length()>0 && !CurApkPackageName.equalsIgnoreCase("com.rtk.mediabrowser")){
			finish();
			return;
		}
		mActivityPauseFlag = 0;
		isBackground = false;
		homePressed = false;
		if (onbackPlay)
			onbackPlay = false;
		else {
			IntentFilter filter = new IntentFilter(
					"com.rtk.mediabrowser.PlayService");
			registerReceiver(receiver, filter);
			if (mTv == null) {
				mTv = (TvManager) getSystemService("tv");
			}
			if(mMediaPlayer == null)
				createPlayer();
			if (mMediaPlayer != null) {
				if (status == sta_pause) {
					play();
				}
				if (status == sta_stop) {
					play();
				}
			}
			handlerTask.removeCallbacks(spectrumTask_runnable);
			handlerTask.postDelayed(spectrumTask_runnable, 0);
			if (!fromUri&&firstCreate) {
				startFromIntent(m_intent);
				firstCreate = false;
			}
		}
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
		if (barThd == null) {
			barThd = new Thread(new Runnable() {
				@Override
				public void run() {
					while (!finishThd) {
						if (mMediaPlayer == null) {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							continue;
						}
						try {
							if (status == sta_play &&mMediaPlayer !=null && mMediaPlayer.isPlaying()) {
								handler.sendEmptyMessage(MSG_PROCESS_TIME);
							}
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			});
			barThd.start();
		}
        if (homeRecevier != null) { 
            registerReceiver(homeRecevier, homeFilter); 
        }
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
	protected void onDestroy() {
		super.onDestroy();
		isBanner = true;
		finishThd = true;
		try {
			// lrcThd.join(); // wait to finish
			if (barThd != null)
				barThd.join();
			barThd = null;
			if (worker != null)
				worker.join();
			worker = null;
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		try {
			if (mMediaPlayer != null) {
				mMediaPlayer.reset();
				mMediaPlayer = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		mUsbCtrl.UnRegesterBroadcastReceiver();
		unregisterReceiver(mPlayReceiver);
		delTimer();
	}

	private void delTimer() {
		handlerTask.removeCallbacks(spectrumTask_runnable);
		handlerTask.removeCallbacks(task_play_runnable);
		handlerTask.removeCallbacks(task_play2_runnable);
		handlerTask.removeCallbacks(task_gui_runnable);
		handlerTask.removeCallbacks(task_gui2_runnable);
	}

	private void onInfo() {
		handlerTask.removeCallbacks(task_play_runnable);
		handlerTask.removeCallbacks(task_play2_runnable);
		if (isBanner == false) {
			banner_timeout = 6000;
			handlerTask.postDelayed(task_play_runnable, 0);
			handlerTask.postDelayed(task_play2_runnable, banner_timeout);
		} else {
			handlerTask.postDelayed(task_play2_runnable, 0);
		}
	}

	private void onGui() {
		handlerTask.removeCallbacks(task_gui_runnable);
		handlerTask.removeCallbacks(task_gui2_runnable);
		if (showGui == false) {
			handlerTask.postDelayed(task_gui_runnable, 0);
			handlerTask.postDelayed(task_gui2_runnable, gui_timeout);
		} else {
			handlerTask.postDelayed(task_gui2_runnable, 0);
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent m) {

		switch (m.getKeyCode()) {
		case 82:// MENU
		case 220:// STEREO/DUAL for L4300
			showNotAvail();
			return true;
		}

		if (m.getAction() == KeyEvent.ACTION_UP) {
			switch (m.getKeyCode()) {
			case KeyEvent.KEYCODE_INFO:
			case KeyEvent.KEYCODE_I: // [i+]
				onInfo();
				return true;
			case 251: // the key in the left of sleep key
			case KeyEvent.KEYCODE_G: // [tsb clickable stick]
				onGui();
				return true;
			case 235: // for L4300 KeyEvent.KEYCODE_NEXT:
			case KeyEvent.KEYCODE_N: // >>|
				if (forwardStatus != ForwardStatus.NONE) {
					cancelForward();
				}
				onNext();
				return true;
			case 234: // for L4300 KeyEvent.KEYCODE_PREVIOUS:
			case KeyEvent.KEYCODE_P: // |<<
				if (forwardStatus != ForwardStatus.NONE) {
					cancelForward();
				}
				onLast();
				return true;
			case 229: // for L4300 KeyEvent.KEYCODE_ZOOM
			case KeyEvent.KEYCODE_F: // >>]
				onForward();
				return true;
			case 228: // for L4300 KeyEvent.KEYCODE_HOLD:
			case 256: // VIDEO KEY in realtek RCU
			case KeyEvent.KEYCODE_R: // [<<
				onRewind();
				return true;
			case 227: // for L4300 KeyEvent.KEYCODE_QUICK_MENU:
			case KeyEvent.KEYCODE_Q: // [QUICk]
				onQuick();
				return true;
			case 232: // for L4300 KeyEvent.KEYCODE_PLAY:
				if (forwardStatus != ForwardStatus.NONE) {
					onOk();
					return true;
				} else
					onlyPlay();
				return true;
			case 233: // for L4300 KeyEvent.KEYCODE_PAUSE:
			case KeyEvent.KEYCODE_S: // [Play/pause]
				if (forwardStatus != ForwardStatus.NONE) {
					onOk();
				}
				onPlay();
				return true;
			case KeyEvent.KEYCODE_ESCAPE:
			case KeyEvent.KEYCODE_E: // [exit]
			{
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
				delTimer();
				finish();
			}
				return true;
			case KeyEvent.KEYCODE_ENTER: // [QUICk]
				if (forwardStatus != ForwardStatus.NONE) {
					onOk();
					return true;
				} else
					break;
			case KeyEvent.KEYCODE_X:
				if (!fromAnywhere && !fromUri) {
						AudioBrowser.index = index;
					if (mMediaPlayer != null) {
						mMediaPlayer.reset();
						mMediaPlayer = null;
						mTv.disableSpectrumData();
					}
				}
				delTimer();
				finish();
				break;
			case 231: // for L4300 KeyEvent.KEYCODE_STOP:
				if (!fromAnywhere && !fromUri) {
						AudioBrowser.index = index;
					if (mMediaPlayer != null) {
						mMediaPlayer.reset();
						mMediaPlayer = null;
						mTv.disableSpectrumData();
					}
				}
				delTimer();
				finish();
				break;
			case KeyEvent.KEYCODE_BACK:
				if (!fromAnywhere && !fromUri) {
						AudioBrowser.index = index;
					if (mMediaPlayer != null) {
						mMediaPlayer.reset();
						mMediaPlayer = null;
						mTv.disableSpectrumData();
					}
				}
				delTimer();
				finish();
				break;
			}

		}
		return super.dispatchKeyEvent(m);
	}

	public void handleUsbPlugout() {
		Toast.makeText(
				getApplicationContext(),
				getApplicationContext().getResources().getString(
						R.string.device_removed), Toast.LENGTH_SHORT).show();
		this.finish();
	}

	private void onForward() {
		if (metadata != null && !metadata.getBoolean(Metadata.SEEK_AVAILABLE)) {
			Toast.makeText(getApplicationContext(),
					this.getResources().getString(R.string.msg_seek_forbidden),
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (forwardStatus != ForwardStatus.FORWARD) {
			forwardIndex = 0;
			forwardStatus = ForwardStatus.FORWARD;
		} else {
			forwardIndex++;
			forwardIndex %= 6;
		}
		execAction(SET_FORWARD, "");
		showBannel(false);
		imgSta.setImageDrawable(getApplicationContext().getResources()
				.getDrawable(
						is4k ? forwardDarwable[forwardIndex]
								: forwardDarwable[forwardIndex]));
	}

	private void onRewind() {
		if (metadata != null && !metadata.getBoolean(Metadata.SEEK_AVAILABLE)) {
			Toast.makeText(getApplicationContext(),
					this.getResources().getString(R.string.msg_seek_forbidden),
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (forwardStatus != ForwardStatus.REWIND) {
			forwardIndex = 0;
			forwardStatus = ForwardStatus.REWIND;
		} else {
			forwardIndex++;
			forwardIndex %= 6;
		}
		execAction(SET_FORWARD, "");
		showBannel(false);
		imgSta.setImageDrawable(getApplicationContext().getResources()
				.getDrawable(
						is4k ? backDarwable[forwardIndex]
								: backDarwable[forwardIndex]));
	}

	private void onOk() {
		execAction(STOP_FORWARD, "");
		handlerTask.removeCallbacks(task_play_runnable);
		handlerTask.removeCallbacks(task_play2_runnable);
		banner_timeout = 6000;
		handlerTask.postDelayed(task_play2_runnable, banner_timeout);
		imgSta.setImageDrawable(getApplicationContext().getResources()
				.getDrawable(is4k ? R.drawable.au_play : R.drawable.au_play));
		gui_play.setImageDrawable(getApplicationContext()
				.getResources()
				.getDrawable(is4k ? R.drawable.gui_pause : R.drawable.gui_pause));
	}

	private void cancelForward() {
		execAction(CANCEL_FORWARD, "");
		handlerTask.removeCallbacks(task_play_runnable);
		handlerTask.removeCallbacks(task_play2_runnable);
		banner_timeout = 6000;
		handlerTask.postDelayed(task_play2_runnable, banner_timeout);
		imgSta.setImageDrawable(getApplicationContext().getResources()
				.getDrawable(is4k ? R.drawable.au_play : R.drawable.au_play));
		gui_play.setImageDrawable(getApplicationContext()
				.getResources()
				.getDrawable(is4k ? R.drawable.gui_pause : R.drawable.gui_pause));
	}

	private void onLast() {
		if(fromUri){
			//do nothing
			return;
		}
		String act = "";
		if (System.currentTimeMillis() - lasttime <= 2000) {
			if (repeatStatus == RepeatStatus.ONE) {
				act = GO_START;
				seeking = true;
				handler.removeMessages(MSG_SEEK_OUT);
				Message msg = handler.obtainMessage(MSG_SEEK_OUT);
				handler.sendMessageDelayed(msg, seek_timeout);
			} else {
				if (repeatStatus == RepeatStatus.OFF && index == firstindex) {
					lasttime = System.currentTimeMillis();
					return;
				} else {
					act = PREVIOUS_ACTION;
					seeking = false;
				}
			}

		} else {
			act = GO_START;
			seeking = true;
			handler.removeMessages(MSG_SEEK_OUT);
			Message msg = handler.obtainMessage(MSG_SEEK_OUT);
			handler.sendMessageDelayed(msg, seek_timeout);
		}
		execAction(act, "");
		lasttime = System.currentTimeMillis();

		showBannel(true);
	}

	private void onNext() {
		if (fromAnywhere) {
			if (repeatStatus == RepeatStatus.OFF && index == len - 1) {
				if (receiver != null) {
					unregisterReceiver(receiver);
					receiver = null;
				}
				finish();
				return;
			}
		}else if(fromUri){
			//do nothing
			return;
		}
		else {
			if (repeatStatus == RepeatStatus.OFF
					&& index == musicList.size() - 1) {
					AudioBrowser.index = index;
				if (receiver != null) {
					unregisterReceiver(receiver);
					receiver = null;
				}
				finish();
				return;
			}
		}
		String act = NEXT_ACTION;
		if (repeatStatus == RepeatStatus.ONE)
			act = GO_START;
		execAction(act, "");
		showBannel(true);
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

	public class ServiceReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (!fromAnywhere)
				return;
			if (intent.getStringExtra("action").equals("BACK")
					|| intent.getStringExtra("action").equals("FINISH")) {
				if (receiver != null) {
					unregisterReceiver(receiver);
					receiver = null;
				}
				finish();
			}
			if (intent.getStringExtra("action").equals("PAUSE")) {
				pauseOne();
			}
			if (intent.getStringExtra("action").equals("STOP")) {
				stop();
			}
			if (intent.getStringExtra("action").equals("PLAY")) {
				play();
			}
		}
	}

	private void setRepeatIcon(RepeatStatus tag) {
		switch (tag) {
		case OFF:
			if (btn_repeat != null)
				btn_repeat.setImageDrawable(this.getResources().getDrawable(
						is4k ? R.drawable.photo_player_repeat_off
								: R.drawable.photo_player_repeat_off));
			break;
		case ALL:
			if (btn_repeat != null)
				btn_repeat.setImageDrawable(this.getResources().getDrawable(
						is4k ? R.drawable.photo_player_repeat_on
								: R.drawable.photo_player_repeat_on));
			break;
		case ONE:
			if (btn_repeat != null)
				btn_repeat.setImageDrawable(this.getResources().getDrawable(
						is4k ? R.drawable.photo_repeat_one_on
								: R.drawable.photo_repeat_one_on));
			break;
		default:
			break;
		}
	}

	private void onPlay() {
		if (MediaApplication.DEBUG)
			Log.e("Service Action ", "Pause/start");
		execAction(PAUSE_ACTION, "");
		showBannel(true);
	}

	private void onlyPlay() {
		if (MediaApplication.DEBUG)
			Log.e("Service Action ", "only Play");
		execAction(PLAY_ONLY_ACTION, "");
		showBannel(true);
	}

	private void showBannel(boolean miss) {
		handlerTask.removeCallbacks(task_play_runnable);
		handlerTask.removeCallbacks(task_play2_runnable);
		handlerTask.postDelayed(task_play_runnable, 0);
		if (miss) {
			banner_timeout = 6000;
			handlerTask.postDelayed(task_play2_runnable, banner_timeout);
		}
	}

	private synchronized void getSpectrum() {
		if (status == sta_play) {
			SpectrumDataInfo data = mTv.getSpectrumInfo();
			Message msg = new Message();
			msg.what = UPDATE_SPECTRUM;
			Bundle bundle = new Bundle();
			bundle.putLongArray("fft", data.specData);
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}

	private synchronized void get0Spectrum() {
		for (int i = 0; i < 64; i++)
			tmpData[i] = -90;
		Message msg = new Message();
		msg.what = UPDATE_SPECTRUM;
		Bundle bundle = new Bundle();
		bundle.putLongArray("fft", tmpData);
		msg.setData(bundle);
		handler.sendMessage(msg);
	}

	private long[] tmpData = new long[64];

	class QuickMenuAdapter extends BaseAdapter {
		int[] menu_name = new int[] { R.string.txtSound,
				R.string.quick_menu_repeat, R.string.quick_menu_sleep,
				R.string.quick_menu_tvapp, R.string.quick_menu_sysset };
		int[] visibility = new int[] { View.INVISIBLE, View.INVISIBLE,
				View.INVISIBLE, View.INVISIBLE, View.INVISIBLE,

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
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.menu_name.setText(menu_name[position]);
			switch (position) {
			case 0: {
				holder.menu_option.setText("");
				break;
			}
			case 1: {
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
			case 2: {
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
			case 3: {
				holder.menu_option.setText("");
				break;
			}
			case 4: {
				holder.menu_option.setText("");
				break;
			}
			default:
				break;
			}

			if (position == 1 || position == 2) {
				if (holder.left == null) {
					holder.left = (ImageView) convertView
							.findViewById(R.id.left_arrow);
					holder.right = (ImageView) convertView
							.findViewById(R.id.right_arrow);
				}
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

	private final String PLAY_ACTION = "com.realtek.rtkmusicplayer.PLAY_ACTION";
	private final String PLAY_ONLY_ACTION = "com.realtek.rtkmusicplayer.PLAY_ONLY_ACTION";
	private final String PAUSE_ACTION = "com.realtek.rtkmusicplayer.PAUSE_ACTION";
	private final String PAUSEONE_ACTION = "com.realtek.rtkmusicplayer.PAUSEONE_ACTION";
	private final String NEXT_ACTION = "com.realtek.rtkmusicplayer.NEXT_ACTION";
	private final String PREVIOUS_ACTION = "com.realtek.rtkmusicplayer.PREVIOUS_ACTION";
	private final String STOP_ACTION = "com.realtek.rtkmusicplayer.STOP_ACTION";
	private final String SET_REPEAT = "com.realtek.rtkmusicplayer.SET_REPEAT";
	private final String SET_FORWARD = "com.realtek.rtkmusicplayer.SET_FORWARD";
	private final String STOP_FORWARD = "com.realtek.rtkmusicplayer.STOP_FORWARD";
	private final String GO_START = "com.realtek.rtkmusicplayer.GO_START";
	private final String CANCEL_FORWARD = "com.realtek.rtkmusicplayer.CANCEL_FORWARD";

	private boolean isBackground = false;
	private ForwardStatus forwardStatus = ForwardStatus.NONE;
	private int[] forwardSpeed = { 2, 8, 16, 32, 16, 8 };
	private int forwardIndex = 0;
	private RepeatStatus repeatStatus = RepeatStatus.OFF;
	private Thread worker = null;
	private Object obj = null;
	private boolean canForward = false;
	private int hacktick = 0;
	private int status = -1;

	private void createPlayer() {
		mMediaPlayer = map.getMediaPlayer();
		if(mMediaPlayer ==null)
			return;
		mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				if (fromAnywhere) {
					if (anywhereList == null || len == 0) {
						goBack();
						return;
					}
					if (repeatStatus != RepeatStatus.ONE) {
						index++;
					}
					if (index == len) {
						if (repeatStatus == RepeatStatus.OFF) {
							index--;
							goBack();
							return;
						} else {
							index = firstindex;
						}
					}
					if (!isBackground)
						handler.sendEmptyMessage(MSG_RESET);
					status = sta_stop;
					play();
				} else if(fromUri){
					finish();
					return;
				}
				else {
					if (musicList == null || musicList.size() == 0) {
						goBack();
						return;
					}
					if (repeatStatus != RepeatStatus.ONE) {
						index++;
					}
					if (index == musicList.size()) {
						if (repeatStatus == RepeatStatus.OFF) {
							index--;
							goBack();
							return;
						} else {
							index = firstindex;
						}
					}
					if (!isBackground)
						handler.sendEmptyMessage(MSG_RESET);
					status = sta_stop;
					// mTv.disableSpectrumData();
					play();
				}
			}
		});
		mMediaPlayer.setOnErrorListener(mp_error);
		mMediaPlayer.setOnInfoListener(infoListener);
		mMediaPlayer.setOnSeekCompleteListener(new OnSeekCompleteListener() {
			@Override
			public void onSeekComplete(MediaPlayer mp) {
				mp.start();
				status = sta_play;
			}
		});
	}

	private void startInit() {
		clearSeekTag();
		Thread thd = new Thread() {
			public void run() {
				init();
			}
		};
		thd.start();
	}

	private OnErrorListener mp_error = new OnErrorListener() {
		public boolean onError(MediaPlayer mp, int what, int extra) {
			if (what == 1 || what == 0x30000000) {
				mp.reset();
				if(fromUri){
					finish();
					return true;
				}
				if (!fromAnywhere)
					musicList.get(tmpIndex).setCanPlay(-1);
				handler.sendEmptyMessage(MSG_PLAY_NEXT);
			}
			return true;
		}
	};
	private int tmpIndex = 0;

	private synchronized void init() {
		synchronized (obj) {
			canForward = false;
		}
		tmpIndex = index;
		if(mMediaPlayer ==null)
			return;
		mMediaPlayer.reset();
		try {

			String playPath = "";
			if (fromAnywhere) {
				playPath = anywhereList.get(index);
			} else {
				if (musicList.get(index).getCanPlay() == -1) {
					handler.sendEmptyMessage(MSG_PLAY_NEXT);
					return;
				}
				playPath = musicList.get(index).getPath();
			}

			// This is Very important Audio only to TvServer

			if (MediaApplication.DEBUG)
				Log.d(TAG, "set path audio only:" + playPath);
			Map<String, String> config;
			config = new HashMap<String, String>();
			config.put("FLOWTYPE", "PLAYBACK_TYPE_AUDIO_ONLY");
			mMediaPlayer.setPlayerType(6); // use RTK_MediaPlayer
			mMediaPlayer.setDataSource(playPath, config);
			if (MediaApplication.DEBUG)
				Log.e(TAG, "method.invoke done");

			mMediaPlayer.setOnPreparedListener(audioPreparedListener);
			mMediaPlayer.prepareAsync();
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (IllegalStateException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
			if (!fromAnywhere)
				musicList.get(tmpIndex).setCanPlay(-1);
			handler.sendEmptyMessage(MSG_PLAY_NEXT);
		} catch (Exception e) // add by kelly catch exception 20120820
		{
			if (MediaApplication.DEBUG)
				Log.e(TAG, "kellykelly exception error: " + e);
			e.printStackTrace();

		}
	}

	private String getRealPath(Uri fileUrl) {
		String fileName = null;
		if (fileUrl != null) {
			if (fileUrl.getScheme().toString().compareTo("content") == 0) {
				Cursor cursor = getApplicationContext().getContentResolver()
						.query(fileUrl, null, null, null, null);
				if (cursor != null && cursor.moveToFirst()) {
					int column_index = cursor
							.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					fileName = cursor.getString(column_index);
					if (!fileName.startsWith("/mnt")) {

						fileName = "/mnt" + fileName;
					}
					cursor.close();
				}
			} else if (fileUrl.getScheme().compareTo("file") == 0) {
				fileName = fileUrl.toString();
				fileName = fileUrl.toString().replace("file://", "");
				if (!fileName.startsWith("/mnt")) {
					fileName += "/mnt";
				}
			}
		}
		return fileName;
	}
	
	private synchronized void playUriAudio() {
		String scheme = mUri.getScheme();
        if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
            if (mUri.getAuthority() == MediaStore.AUTHORITY) {
                // try to get title and artist from the media content provider
                mAsyncQueryHandler.startQuery(0, null, mUri, new String [] {
                        MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST},
                        null, null, null);
            } else {
                // Try to get the display name from another content provider.
                // Don't specifically ask for the display name though, since the
                // provider might not actually support that column.
                mAsyncQueryHandler.startQuery(0, null, mUri, null, null, null, null);
            }
        } else if (scheme.equals("file")) {
            // check if this file is in the media database (clicking on a download
            // in the download manager might follow this path
            String path = mUri.getPath();
            mAsyncQueryHandler.startQuery(0, null,  MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String [] {MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST},
                    MediaStore.Audio.Media.DATA + "=?", new String [] {path}, null);
        }
		synchronized (obj) {
			canForward = false;
		}
		mMediaPlayer.reset();
		try {
			Map<String, String> config;
			config = new HashMap<String, String>();
			config.put("FLOWTYPE", "PLAYBACK_TYPE_AUDIO_ONLY");
			mMediaPlayer.setPlayerType(6); // use RTK_MediaPlayer
			mMediaPlayer.setDataSource(getApplicationContext(),mUri, config);
			if (MediaApplication.DEBUG)
				Log.e(TAG, "method.invoke done");

			mMediaPlayer.setOnPreparedListener(audioPreparedListener);
			mMediaPlayer.prepareAsync();
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (IllegalStateException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (Exception e) // add by kelly catch exception 20120820
		{
			if (MediaApplication.DEBUG)
				Log.e(TAG, "kellykelly exception error: " + e);
			e.printStackTrace();

		}
	}
	// play the music
	public void play() {
		metadata = null;
		startInit();
	}

	private void goBack() {
		if (!fromAnywhere && !fromUri) {
				AudioBrowser.index = index;
			if (mMediaPlayer != null) {
				mMediaPlayer.reset();
				mMediaPlayer = null;
				mTv.disableSpectrumData();
			}
		}
		delTimer();
		finish();
	}

	private void execAction(String action, String repeat) {

		if (action.equals(PLAY_ACTION)) {
			play();
		} else if (action.equals(PAUSE_ACTION)) {
			pause();
		} else if (action.equals(PLAY_ONLY_ACTION)) {
			playOnly();
		} else if (action.equals(PAUSEONE_ACTION)) {
			pauseOne();
		} else if (action.equals(NEXT_ACTION)) {
			next();
		} else if (action.equals(PREVIOUS_ACTION)) {
			previous();
		} else if (action.equals(STOP_ACTION)) {
			stop();
		} else if (action.equals(SET_REPEAT)) {
			repeatStatus = RepeatStatus.valueOf(repeat);
		} else if (action.equals(SET_FORWARD)) {
			if (status == sta_play &&mMediaPlayer !=null) {
				try {
					mMediaPlayer.pause();
					status = sta_pause;
					handler.sendEmptyMessage(MSG_FORWARD);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			handler.removeMessages(MSG_SEEK_OUT);
			if (hacktick <= 0) {
				if(mMediaPlayer !=null)
					try{
						hacktick = mMediaPlayer.getCurrentPosition();
					}catch(Exception e){}
				prePosition = hacktick;
			}

			synchronized (obj) {
				canForward = true;
			}
		} else if (action.equals(STOP_FORWARD)) {
			synchronized (obj) {
				canForward = false;
			}
			forwardStatus = ForwardStatus.NONE;
			if (!isBackground) {
				handler.sendEmptyMessage(MSG_RESET_FWD);
			}
			if (status == sta_pause) {
				try {
					handler.removeMessages(MSG_SEEK_OUT);
					Message msg = handler.obtainMessage(MSG_SEEK_OUT);
					handler.sendMessageDelayed(msg, seek_timeout);
					if(mMediaPlayer !=null){
						try{
							mMediaPlayer.seekTo(hacktick);
							//doRequestAF();
							mMediaPlayer.start();
					//hacktick = 0;
							status = sta_play;
					}catch(Exception e){}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else if (action.equals(GO_START)) {
			goStart();
		} else if (action.equals(CANCEL_FORWARD)) {
			synchronized (obj) {
				canForward = false;
			}
			forwardStatus = ForwardStatus.NONE;
			if (!isBackground) {
				// handler
				// .sendEmptyMessage(MSG_HACK_TIME);
				handler.sendEmptyMessage(MSG_RESET_FWD);
			}
			if (status == sta_pause) {
				try {
					handler.removeMessages(MSG_SEEK_OUT);
					if(mMediaPlayer !=null)
					mMediaPlayer.start();
					hacktick = 0;
					status = sta_play;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void pause() {
		if (status == sta_play) {
			if(mMediaPlayer !=null)
			mMediaPlayer.pause();
			status = sta_pause;
			if (!isBackground)
				handler.sendEmptyMessage(MSG_PAUSE);
		} else {
			if(mMediaPlayer !=null)
			mMediaPlayer.start();
			status = sta_play;
			if (!isBackground)
				handler.sendEmptyMessage(MSG_PLAY);
		}
	}

	public void pauseOne() {
		if (status == sta_play) {
			if(mMediaPlayer !=null)
			mMediaPlayer.pause();
			status = sta_pause;
		}
	}

	public void playOnly() {
		if (status == sta_pause) {
			if(mMediaPlayer !=null)
			mMediaPlayer.start();
			status = sta_play;
			if (!isBackground)
				handler.sendEmptyMessage(MSG_PLAY);
		}
	}

	public void stop() {
		if (mMediaPlayer != null) {
			mMediaPlayer = null;
			mTv.disableSpectrumData();
		}
	}

	public void previous() {
		if(fromUri)
			return;
		direction = -1;
		if (!isBackground)
			handler.sendEmptyMessage(MSG_LAST);
		int pretag = status;
		if (index == firstindex) {
			if (fromAnywhere) {
				index = len - 1;
			} else
				index = musicList.size() - 1;
		} else {
			index--;
		}
		if (!isBackground)
			handler.sendEmptyMessage(MSG_RESET);
		startInit();
		if (forwardStatus != ForwardStatus.NONE) {
			synchronized (obj) {
				canForward = false;
			}
			forwardStatus = ForwardStatus.NONE;
			if (!isBackground) {
				handler.sendEmptyMessage(MSG_RESET_FWD);
				handler.sendEmptyMessage(MSG_PLAY);
			}
		} else {
			if (pretag == sta_pause) {
				if(mMediaPlayer !=null)
				mMediaPlayer.pause();
				status = sta_pause;
			}
		}
	}

	//
	public void next() {
		if(fromUri)
			return;
		direction = 1;
		if (!isBackground)
			handler.sendEmptyMessage(MSG_NEXT);
		int pretag = status;
		if (fromAnywhere) {
			if (index == len - 1) {
				index = firstindex;
			} else {
				index++;
			}
		} else {
			if (index == musicList.size() - 1) {
				index = firstindex;
			} else {
				index++;
			}
		}
		if (!isBackground)
			handler.sendEmptyMessage(MSG_RESET);
		startInit();
		if (forwardStatus != ForwardStatus.NONE) {
			synchronized (obj) {
				canForward = false;
			}
			forwardStatus = ForwardStatus.NONE;
			if (!isBackground) {
				handler.sendEmptyMessage(MSG_RESET_FWD);
				handler.sendEmptyMessage(MSG_PLAY);
			}
		} else {
			if (pretag == sta_pause) {
				if(mMediaPlayer !=null)
				mMediaPlayer.pause();
				status = sta_pause;
			}
		}
	}

	public void goStart() {
		if (status == sta_play) {
			if(mMediaPlayer !=null)
			mMediaPlayer.seekTo(0);
		}
	}

	private void initWorker() {
		worker = new Thread(new Runnable() {
			@Override
			public void run() {
				while (!finishThd) {
					if (canForward) {
						int sign = 1;
						if (forwardStatus == ForwardStatus.REWIND)
							sign = -1;
						int position = forwardSpeed[forwardIndex] * 1000 * sign;
						if (MediaApplication.DEBUG)
							Log.e("ggggg", "seek :" + position);
						addFakeSeek(position);
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		worker.start();
	}

	public void addFakeSeek(int tick) {

		hacktick += tick;
		if (hacktick < 0) {
			hacktick = 0;
			synchronized (obj) {
				canForward = false;
			}
			try {
				if(mMediaPlayer !=null)
				mMediaPlayer.seekTo(hacktick);
			} catch (Exception e) {
				e.printStackTrace();
			}
			forwardStatus = ForwardStatus.NONE;
			if (!isBackground) {
				handler.sendEmptyMessage(MSG_RESET_FWD);
				handler.sendEmptyMessage(MSG_PLAY);
			}
		}
		if (hacktick >= m_duration) {
			hacktick = 0;
			synchronized (obj) {
				canForward = false;
			}
			if (repeatStatus == RepeatStatus.ONE) {
				try {
					if(mMediaPlayer !=null)
					mMediaPlayer.seekTo(hacktick);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				if(fromUri)
				{
					finish();
					return;
				}
				index++;
				int total = len;
				if (!fromAnywhere)
					total = musicList.size();
				if (index == total) {
					if (repeatStatus == RepeatStatus.OFF) {
						index--;
						try {
							if(mMediaPlayer !=null)
							mMediaPlayer.seekTo((int) (m_duration - 10));
						} catch (Exception e) {
							e.printStackTrace();
						}
						return;
					}
					index = firstindex;
				}
				if (!isBackground)
					handler.sendEmptyMessage(MSG_RESET);
				hacktick = 0;
				play();
			}
			forwardStatus = ForwardStatus.NONE;
			if (!isBackground) {
				handler.sendEmptyMessage(MSG_RESET_FWD);
				handler.sendEmptyMessage(MSG_PLAY);
			}
		}
		if (!isBackground) {
			handler.sendEmptyMessage(MSG_HACK_TIME);
		}
		if (MediaApplication.DEBUG)
			Log.e("ggggg", "position = " + hacktick);
	}

	public enum RepeatStatus {
		OFF, ALL, ONE
	}

	public enum ForwardStatus {
		REWIND, FORWARD, NONE
	}

	private AbstractDataProvider mDataProvider = null;
	private int mBrowserType;
	private XmlResourceParser mMimeTypeXml;
	private MimeTypes mMimeTypes = null;

	private void getDataProvider(String path) {
		if (path != null) {
			if (mBrowserType == 0) {
				mDataProvider = new DeviceFileDataPrivider(path,
						FileFilterType.DEVICE_FILE_DIR
								| FileFilterType.DEVICE_FILE_AUDIO, -1, 0,
						mMimeTypes);
			}
			mDataProvider.sortListByType();
			firstindex = mDataProvider.getDirnum();
		}
	}

	private void initUsbCtl() {
		initReceiver();
		mUsbCtrl = new UsbController(this);
		mUsbCtrl.RegesterBroadcastReceiver();
		OnUsbCheckListener usbCheckListener = new OnUsbCheckListener() {
			@Override
			public void OnUsbCheck() {
				// TODO Auto-generated method stub
				if (fromAnywhere || mBrowserType != 0) {
					return; // do nothing on dmrMode
				}
				switch (mUsbCtrl.getDirection()) {
				case UsbController.PLUG_OUT: {
					if (mUsbCtrl.GetUsbNum() == 0)// deviceNum is 0
					{
						setResult(1);
						beforeFinish();
						finish();
						return;
					} else {
						File file = new File(musicList.get(0).getPath());
						if (file == null || !file.exists()) {
							setResult(1);
							beforeFinish();
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
			public void OnUsbCheck(String path, int direction) {
				// TODO Auto-generated method stub
				if (fromAnywhere || mBrowserType != 0) {
					return; // do nothing on dmrMode
				}
				String mpath = "/storage/udisk" + devicePath;
				Log.e(TAG, mpath + "0");
				switch (direction) {
				case UsbController.PLUG_OUT: {
					Log.e(TAG, mpath);
					if (mpath.equals(path)) {
						beforeFinish();
						finish();
						return;
					}

				}
				}
			}
		};
		mUsbCtrl.setOnUsbCheckListener(usbCheckListener);

	}

	private void beforeFinish() {
		isBanner = true;
		finishThd = true;
		try {
			// lrcThd.join(); // wait to finish
			if (barThd != null)
				barThd.join();
			barThd = null;
			if (worker != null)
				worker.join();
			worker = null;
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		if (mMediaPlayer != null) {
			mMediaPlayer.reset();
			mMediaPlayer = null;
		}
		delTimer();
	}

	private void initGui() {
		gui_repeat = (ImageButton) findViewById(R.id.gui_repeat);
		gui_repeat.setOnClickListener(new OnClickListener() {
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
				execAction(SET_REPEAT, repeats[repeatIndex].name());
				showBannel(false);
				showGui();
			}
		});

		gui_fw = (ImageButton) findViewById(R.id.gui_fw);
		gui_fw.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onForward();
				showGui();
			}
		});

		gui_rw = (ImageButton) findViewById(R.id.gui_rw);
		gui_rw.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onRewind();
				showGui();
			}
		});

		gui_play = (ImageButton) findViewById(R.id.gui_play);
		gui_play.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (forwardStatus != ForwardStatus.NONE) {
					onOk();
				} else {
					onPlay();
					showGui();
				}
			}
		});

		gui_last = (ImageButton) findViewById(R.id.gui_last);
		gui_last.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (forwardStatus != ForwardStatus.NONE) {
					cancelForward();
				}
				onLast();
				showGui();
			}
		});

		gui_next = (ImageButton) findViewById(R.id.gui_next);
		gui_next.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (forwardStatus != ForwardStatus.NONE) {
					cancelForward();
				}
				onNext();
				showGui();
			}
		});
		gui_play.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					showGui();
				}
			}
		});
		gui_fw.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					showGui();
				}
			}
		});
		gui_rw.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					showGui();
				}
			}
		});
		gui_next.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					showGui();
				}
			}
		});
		gui_last.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					showGui();
				}
			}
		});
		gui_repeat.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					showGui();
				}
			}
		});
	}

	private void showGui() {
		handlerTask.removeCallbacks(task_gui_runnable);
		handlerTask.removeCallbacks(task_gui2_runnable);
		handlerTask.postDelayed(task_gui_runnable, 0);
		handlerTask.postDelayed(task_gui2_runnable, gui_timeout);
	}

	private OnPreparedListener audioPreparedListener = new OnPreparedListener() {

		@Override
		public void onPrepared(MediaPlayer mp) {
			String runningActivity = getRunningActivityName();
			Log.v(TAG, "runningActivity =" +runningActivity);
			if(runningActivity !=null && runningActivity.length()>0 && !runningActivity.equalsIgnoreCase("com.rtk.mediabrowser.MusicActivity"))
				return;
			String CurApkPackageName = mTv.getCurAPK();
			Log.v(TAG, "CurApkPackageName =" +CurApkPackageName);
			if(CurApkPackageName !=null && CurApkPackageName.length()>0 && !CurApkPackageName.equalsIgnoreCase("com.rtk.mediabrowser"))
				return;
			if(mMediaPlayer == null || homePressed)//back key cuase mp relase flow
				return;
			direction = 1;
			if (playPosition > 0)
				mMediaPlayer.seekTo(playPosition);
			mMediaPlayer.start();
			playPosition = 0;
			mTv.enableSpectrumData();
			metadata = mMediaPlayer.getMetadata(false, true);
			status = sta_play;
			if (!isBackground)
				handler.sendEmptyMessage(MSG_TOTAL_TIME);
		}
	};

	private void animateShowBanner() {
		lay_banner.clearAnimation();
		TranslateAnimation TransAnim;
		TransAnim = new TranslateAnimation(0.0f, 0.0f, banner_h, 0.0f);
		TransAnim.setDuration(bannerAnimTime);
		lay_banner.startAnimation(TransAnim);
	}

	private void animateHideBanner() {
		lay_banner.clearAnimation();
		TranslateAnimation TransAnim;
		TransAnim = new TranslateAnimation(0.0f, 0.0f, 0.0f, banner_h);
		TransAnim.setDuration(bannerAnimTime);
		TransAnim.setAnimationListener(new hiderBannerListener());
		lay_banner.startAnimation(TransAnim);
	}

	private class hiderBannerListener implements AnimationListener {
		public void onAnimationEnd(Animation animation) {
			// TODO Auto-generated method stub
			lay_banner.clearAnimation();
			lay_banner.setVisibility(View.GONE);
		}

		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub
		}

		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub
		}

	}

	private Handler handlerTask = new Handler();

	private Runnable task_play_runnable = new Runnable() {

		public void run() {
			handler.sendEmptyMessage(MSG_BANNER);
		}

	};

	private Runnable task_play2_runnable = new Runnable() {

		public void run() {
			handler.sendEmptyMessage(MSG_SPECTRUM);
		}

	};

	private Runnable task_gui_runnable = new Runnable() {

		public void run() {
			handler.sendEmptyMessage(MSG_GUI_SHOW);
		}

	};

	private Runnable task_gui2_runnable = new Runnable() {

		public void run() {
			handler.sendEmptyMessage(MSG_GUI_HIDE);
		}

	};

	private Runnable spectrumTask_runnable = new Runnable() {

		public void run() {
			if (forwardStatus != ForwardStatus.NONE) {
				get0Spectrum();
			}
			if (mMediaPlayer != null && mMediaPlayer.isPlaying())
				getSpectrum();
			handlerTask.postDelayed(this, spectrum_timedelay);
		}

	};

	private boolean onbackPlay = false;// while load sound setting,shold keep on
										// playing
	private boolean seeking = false;// since seek action is async and not
									// accurate

	private void clearSeekTag() {
		hacktick = 0;
		seeking = false;
		handler.removeMessages(MSG_SEEK_OUT);
	}

	private void quickAutoQuit() {
		handler.removeMessages(MSG_QUICK_HIDE);
		Message msg = handler.obtainMessage(MSG_QUICK_HIDE);
		handler.sendMessageDelayed(msg, quick_timeout);
	}

	private Message_not_avaible msg_notavaible = null;

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

	private OnInfoListener infoListener = new OnInfoListener() {

		@Override
		public boolean onInfo(MediaPlayer mp, int what, int extra) {
			Log.e(TAG, "infoListener" + "---what = " + what + "----");
			switch (what) {
			case 0x10000003: // UNKNOWN_FORMAT /* FOR AUDIO */
			{
				mp.reset();
				if(fromUri){
					finish();
					break;
				}
				if (!fromAnywhere)
					musicList.get(tmpIndex).setCanPlay(-1);
				handler.sendEmptyMessage(MSG_PLAY_NEXT);
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
				finishThd = true;
				finish();
			}
		};
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction("com.rtk.mediabrowser.broadcast");
		registerReceiver(mPlayReceiver, mIntentFilter);
	}

	private String getRunningActivityName() {
		ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity
				.getClassName();
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

	private void onHomePressed() {
		if (mMediaPlayer != null) {
			try {
				mMediaPlayer.reset();
				status = -1;
				mMediaPlayer = null;
				homePressed = true;
			} catch (Exception e) {
			}
		}
	}
}
