package com.rtk.launcher;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Instrumentation;
import android.app.TvManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ScrollActivity extends Activity {
	private final BroadcastReceiver mApplicationsReceiver = new ApplicationsIntentReceiver();
	private final int MSG_RIGHT = 1100;
	private final int MSG_LEFT = 1101;

	/**********************************/
	private RelativeLayout lay_tv;
	private RelativeLayout lay_media;
	private RelativeLayout lay_browser;
	private RelativeLayout lay_dlna;
	private RelativeLayout lay_myapps;
	private RelativeLayout lay_setting;

	private ImageView btn_atv;
	private ImageView btn_browser;
	private ImageView btn_dlna;
	private ImageView btn_myapp;
	private ImageView btn_setting;
	private ImageView btn_media;
	private TextView txt_time;
	private TextView txt_am;
	private TextView txt_date;
	private TextView txt_week;
	/***************************************/

	private Instrumentation in = new Instrumentation();
	private final int SETTIME = 2009;
	private Timer timer;
	private int mCurLiveSource = -1;

	private boolean isGoTv = false;

	private boolean is4k2k = false;
	
	int mfirst;
	
	int minute = -1;
	HomeApplication  application = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (HomeApplication) getApplication();
		is4k2k = application.isIs4k2k();
			if(application.getDensityDpi() == 160)
				setContentView(R.layout.firstpage_low);
			else
				setContentView(R.layout.firstpage);
		//setContentView(R.layout.firstpage_4k2k_160);
		initArgs();
		bindIcons();
		timer = new Timer(true);
		registerIntentReceivers();
		
		mfirst=0;
		try {
			ComponentName componetName = new ComponentName(
					"com.realtek.tv",// another apk name
					"com.realtek.tv.Tv_strategy" // another apk
													// activity name
			);
			Intent intent = new Intent();
			intent.setComponent(componetName);
			startActivity(intent);
			isGoTv = true;
		} catch (Exception e) {
		}
	}

	private void initArgs() {
		//totalApplications = new ArrayList<ApplicationInfo>();
		//context = getApplicationContext();
	}

	private void bindIcons() {

		txt_time = (TextView) findViewById(R.id.txt_time);
		txt_am = (TextView) findViewById(R.id.txt_am);
		txt_date = (TextView) findViewById(R.id.txt_date);
		txt_week = (TextView) findViewById(R.id.txt_week);

		lay_tv = (RelativeLayout) findViewById(R.id.tv_layout);
		lay_media = (RelativeLayout) findViewById(R.id.media_layout);
		lay_browser = (RelativeLayout) findViewById(R.id.browser_layout);
		lay_dlna = (RelativeLayout) findViewById(R.id.dlna_layout);
		lay_myapps = (RelativeLayout) findViewById(R.id.myapps_layout);
		lay_setting = (RelativeLayout) findViewById(R.id.setting_layout);
		btn_atv = (ImageView) findViewById(R.id.ic_tv);
		btn_atv.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					ComponentName componetName = new ComponentName(
							"com.realtek.tv",// another apk name
							"com.realtek.tv.Tv_strategy" // another apk
															// activity name
					);
					Intent intent = new Intent();
					intent.setComponent(componetName);
					startActivity(intent);
					isGoTv = true;
				} catch (Exception e) {
				}
			}
		});
		btn_atv.setOnFocusChangeListener(new ImageView.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				//
				if (is4k2k) {
					if (hasFocus == true) {
						lay_tv.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.menu_focus_x));
					} else {
						lay_tv.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.menu_unfocus_x));
					}
				} else {
					if (hasFocus == true) {
						lay_tv.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.menu_focus));
					} else {
						lay_tv.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.menu_unfocus));
					}
				}
			}
		});
		
		// media icon start
		btn_media = (ImageView) findViewById(R.id.ic_media);
		btn_media.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					ComponentName componetName = new ComponentName(
							"com.rtk.mediabrowser",// another apk name
							"com.rtk.mediabrowser.MediaBrowser" // another apk
																// activity name
					);
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putInt("browserType", 0);
					intent.putExtras(bundle);
					intent.setComponent(componetName);
					startActivity(intent);
				} catch (Exception e) {
				}
			}
		});
		btn_media
				.setOnFocusChangeListener(new ImageView.OnFocusChangeListener() {
					public void onFocusChange(View v, boolean hasFocus) {
						//
						if (is4k2k) {
							if (hasFocus == true) {
								lay_media.setBackgroundDrawable(getResources()
										.getDrawable(R.drawable.menu_focus_x));
							} else {
								lay_media
										.setBackgroundDrawable(getResources()
												.getDrawable(
														R.drawable.menu_unfocus_x));
							}
						} else {
							if (hasFocus == true) {
								lay_media.setBackgroundDrawable(getResources()
										.getDrawable(R.drawable.menu_focus));
							} else {
								lay_media.setBackgroundDrawable(getResources()
										.getDrawable(R.drawable.menu_unfocus));
							}
						}
					}
				});

		// img_media = (ImageView) convertViewEx.findViewById(R.id.img_media);
		// animation_media =
		// (ImageView)convertViewEx.findViewById(R.id.animation_media);
		// media icon end

		// browser icon start
		btn_browser = (ImageView) findViewById(R.id.ic_browser);
		btn_browser.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					Intent intent = new Intent();
					intent.setAction("android.intent.action.VIEW");
					Uri content_url = Uri.parse("http://www.google.com");
					intent.setData(content_url);
					startActivity(intent);
				} catch (Exception e) {
				}
			}
		});
		btn_browser
				.setOnFocusChangeListener(new ImageView.OnFocusChangeListener() {
					public void onFocusChange(View v, boolean hasFocus) {
						//
						if (is4k2k) {
							if (hasFocus == true) {
								lay_browser
										.setBackgroundDrawable(getResources()
												.getDrawable(
														R.drawable.menu_focus_x));
							} else {
								lay_browser
										.setBackgroundDrawable(getResources()
												.getDrawable(
														R.drawable.menu_unfocus_x));
							}
						} else {
							if (hasFocus == true) {
								lay_browser
										.setBackgroundDrawable(getResources()
												.getDrawable(
														R.drawable.menu_focus));
							} else {
								lay_browser
										.setBackgroundDrawable(getResources()
												.getDrawable(
														R.drawable.menu_unfocus));
							}
						}
					}
				});

		// img_browser = (ImageView)
		// convertViewEx.findViewById(R.id.img_browser);
		// animation_browser = (ImageView) convertViewEx
		// .findViewById(R.id.animation_browser);
		// browser icon end
				
		// dlna icon start
		btn_dlna = (ImageView) findViewById(R.id.ic_dlna);
		btn_dlna.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					ComponentName componetName = new ComponentName(
							"com.rtk.mediabrowser",// another apk name
							"com.rtk.mediabrowser.MediaBrowser" // another apk
																// activity name
					);
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putInt("browserType", 1);
					intent.putExtras(bundle);
					intent.setComponent(componetName);
					startActivity(intent);

				} catch (Exception e) {
				}
			}
		});
		btn_dlna.setOnFocusChangeListener(new ImageView.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				//
				if (is4k2k) {
					if (hasFocus == true) {
						lay_dlna.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.menu_focus_x));
					} else {
						lay_dlna.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.menu_unfocus_x));
					}
				} else {
					if (hasFocus == true) {
						lay_dlna.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.menu_focus));
					} else {
						lay_dlna.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.menu_unfocus));
					}
				}
			}
		});

		// myapp icon start
		btn_myapp = (ImageView) findViewById(R.id.ic_myapps);
		btn_myapp.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					//overridePendingTransition(0, R.anim.scale_to_center);
					Intent intent = new Intent(ScrollActivity.this,
							AllApps.class);
					startActivity(intent);
				} catch (Exception e) {
				}
			}
		});
		btn_myapp
				.setOnFocusChangeListener(new ImageView.OnFocusChangeListener() {
					public void onFocusChange(View v, boolean hasFocus) {
						//
						if (is4k2k) {
							if (hasFocus == true) {
								lay_myapps.setBackgroundDrawable(getResources()
										.getDrawable(R.drawable.menu_focus_x));
							} else {
								lay_myapps
										.setBackgroundDrawable(getResources()
												.getDrawable(
														R.drawable.menu_unfocus_x));
							}
						} else {
							if (hasFocus == true) {
								lay_myapps.setBackgroundDrawable(getResources()
										.getDrawable(R.drawable.menu_focus));
							} else {
								lay_myapps.setBackgroundDrawable(getResources()
										.getDrawable(R.drawable.menu_unfocus));
							}
						}
					}
				});

		// img_myapp = (ImageView) convertViewEx.findViewById(R.id.img_myapp);
		// animation_myapp =
		// (ImageView)convertViewEx.findViewById(R.id.animation_myapp);

		// myapp icon end

		// setting icon start
		btn_setting = (ImageView) findViewById(R.id.ic_setting);
		btn_setting.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					ComponentName componetName = new ComponentName(
							"com.android.settings",// another apk name
							"com.android.settings.Settings" // another apk
															// activity name
					);
					Intent intent = new Intent();
					intent.setComponent(componetName);
					startActivity(intent);
				} catch (Exception e) {
				}
			}
		});
		btn_setting
				.setOnFocusChangeListener(new ImageView.OnFocusChangeListener() {
					public void onFocusChange(View v, boolean hasFocus) {
						//
						if (is4k2k) {
							if (hasFocus == true) {
								lay_setting
										.setBackgroundDrawable(getResources()
												.getDrawable(
														R.drawable.menu_focus_x));
							} else {
								lay_setting
										.setBackgroundDrawable(getResources()
												.getDrawable(
														R.drawable.menu_unfocus_x));
							}
						} else {
							if (hasFocus == true) {
								lay_setting
										.setBackgroundDrawable(getResources()
												.getDrawable(
														R.drawable.menu_focus));
							} else {
								lay_setting
										.setBackgroundDrawable(getResources()
												.getDrawable(
														R.drawable.menu_unfocus));
							}
						}
					}
				});

		// img_setting = (ImageView)
		// convertViewEx.findViewById(R.id.img_setting);
		// animation_myapp =
		// (ImageView)convertViewEx.findViewById(R.id.animation_myapp);

		// setting icon end
	}

	public Handler handlerBtn = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			// TODO Auto-generated method stub
			switch (msg.what) {
			case MSG_RIGHT:
				new Thread(new Runnable() {
					public void run() {
						// TODO Auto-generated method stub
						in.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_RIGHT);
						in.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_LEFT);
					}
				}).start();
				break;
			case MSG_LEFT:
				new Thread(new Runnable() {
					public void run() {
						// TODO Auto-generated method stub
						in.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_LEFT);
						in.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_RIGHT);
					}
				}).start();
				break;
			case SETTIME:

				Calendar calendar = Calendar.getInstance();
				//if (minute != calendar.get(Calendar.MINUTE))
				{
				int year = calendar.get(Calendar.YEAR);
				int month = calendar.get(Calendar.MONTH);
				int day = calendar.get(Calendar.DAY_OF_MONTH);
				int hour = calendar.get(Calendar.HOUR);
				minute = calendar.get(Calendar.MINUTE);
				int week = calendar.get(Calendar.DAY_OF_WEEK);
				String str_am = ((calendar.get(Calendar.AM_PM) == Calendar.PM) ? "pm"
						: "am");
				if (hour == 0) {
					if (str_am.equals("pm")) {
						hour = 12;
					}
				}
				txt_time.setText(String.format("%02d:%02d", hour, minute));
				txt_am.setText(str_am);
				txt_date.setText(String.format("%d/%02d/%02d", year, month + 1,
						day));
				txt_week.setText(getWeekStr(week));
				}
				break;
			}
			super.handleMessage(msg);
		}
	};

	/**
	 * Registers various intent receivers. The current implementation registers
	 * only a wallpaper intent receiver to let other applications change the
	 * wallpaper.
	 */
	private void registerIntentReceivers() {
		IntentFilter filter;
		filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
		filter.addDataScheme("package");
		registerReceiver(mApplicationsReceiver, filter);
	}

	/**
	 * Receives notifications when applications are added/removed.
	 */
	private class ApplicationsIntentReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// loadApplications(false);
			application.setAppchanged(true);
		}
	}

	/**
	 * Creates a new appplications adapter for the grid view and registers it.
	 */

	class AppItem {
		ImageView mAppIcon;
		TextView mAppName;
	}

	// / init Rtk Atv
	private void initRtkAtvApp() {
		// init atv area
		SurfaceView mRtkAtvView = (SurfaceView) findViewById(R.id.atv_surfaceView);
		mRtkAtvView.setFocusable(false);
		mRtkAtvView.setFocusableInTouchMode(false);
		// set source
		
		TvManager mTv = (TvManager) getSystemService("tv");
		if (mCurLiveSource == -1
				|| mTv.getCurSourceType() == TvManager.SOURCE_PLAYBACK
				|| mTv.getCurSourceType() == TvManager.SOURCE_OSD) {
			mCurLiveSource = mTv.getCurLiveSource();
			mTv.setSource(mCurLiveSource);
		}
		
		if (is4k2k) {
				mTv.setDisplayWindow(1010, 260, 1818, 1194);
		} else {
				mTv.setDisplayWindow(504, 130, 910, 598);
		}
		/*
		Thread TvThread = new Thread(new Runnable() {			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				TvManager mTv = (TvManager) getSystemService("tv");
				if (mCurLiveSource == -1
						|| mTv.getCurSourceType() == TvManager.SOURCE_PLAYBACK
						|| mTv.getCurSourceType() == TvManager.SOURCE_OSD) {
					mCurLiveSource = mTv.getCurLiveSource();
					mTv.setSource(mCurLiveSource);
				}
				
				if (is4k2k) {
						mTv.setDisplayWindow(1010, 260, 1818, 1194);
				} else {
						mTv.setDisplayWindow(504, 130, 910, 598);
				}	
			}
		});
		TvThread.start();*/
		
		isGoTv = false;
	}

	public void onResume() {
		super.onResume();
		if (timer == null)
			timer = new Timer(true);
		if (task == null) {
			task = new TimerTask() {
				public void run() {
					Message message = new Message();
					message.what = SETTIME;
					handlerBtn.sendMessage(message);
				}
			};
		}
		
		if(mfirst==1){
			initRtkAtvApp();
		}else{
			mfirst=1;
		}
		
		timer.schedule(task, 0, 1000);
		btn_atv.setFocusable(true);
		btn_atv.requestFocus();
		btn_atv.setFocusableInTouchMode(true);
		//overridePendingTransition(0, R.anim.scale_to_center);
		//overridePendingTransition(0, com.android.internal.R.styleable.WindowAnimation_activityCloseExitAnimation);
	}

	public void onPause() {
		super.onPause();
		// timer.cancel();
		task.cancel();
		task = null;
		// timer = null;
		//overridePendingTransition(R.anim.translate_from_bottom,0);
	}

	public void onBackPressed() {
	}

	TimerTask task = new TimerTask() {
		public void run() {
			Message message = new Message();
			message.what = SETTIME;
			handlerBtn.sendMessage(message);
		}
	};

	String getWeekStr(int week) {
		switch (week) {
		case Calendar.SUNDAY:
			return (this.getResources().getString(R.string.sun));
		case Calendar.MONDAY:
			return (this.getResources().getString(R.string.mon));
		case Calendar.TUESDAY:
			return (this.getResources().getString(R.string.tue));
		case Calendar.WEDNESDAY:
			return (this.getResources().getString(R.string.wed));
		case Calendar.THURSDAY:
			return (this.getResources().getString(R.string.thu));
		case Calendar.FRIDAY:
			return (this.getResources().getString(R.string.fri));
		case Calendar.SATURDAY:
			return (this.getResources().getString(R.string.sat));
		}
		return "";
	}
}
