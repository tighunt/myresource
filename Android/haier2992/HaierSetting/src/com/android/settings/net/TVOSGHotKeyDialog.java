package com.android.settings.net;

import android.app.Dialog;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.settings.R;

/**
 * TVOS调出全局的热键菜单.
 * 
 * @author fancs
 */
public class TVOSGHotKeyDialog extends Dialog {
	private static final String TAG = "net";

	private WiFiSignalListActivity mWiFiSignalListActivity;

	private boolean keyResponse = false;

	// -------------控件---------
	private RelativeLayout menuBgRelativeLayout;

	// 页面上的五个图片
	private ImageView menuFoucsImage;
	private ImageView upImage;
	private ImageView rightImage;
	private ImageView downImage;
	private ImageView leftImage;

	// 页面上的四个文字
	private TextView upText;
	private TextView rightText;
	private TextView downText;
	private TextView leftText;

	// 动画效果
	private Animation bigToSmall = null;
	private Animation fadeIn = null;
	private Animation rotateFadeIn = null;

	// ----------动画控制
	private int moveUpDistance = 0;
	private int moveRightDistance = 0;
	private int moveDownDistance = 0;
	private int moveLeftDistance = 0;

	private ScanResult mScanResult;

	String ssid;

	int direction = 0;

	private Handler mHandler;

	private boolean isConfiged = false;
	private boolean isconnected = false;
	// signal level
	private int level = 0;

	protected WiFiEditDialog wiFiEditDialog;

	public TVOSGHotKeyDialog(WiFiSignalListActivity activity, int sytle, ScanResult result,
			Handler handler) {
		super(activity, sytle);
		this.mWiFiSignalListActivity = activity;
		this.mScanResult = result;
		this.mHandler = handler;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tvos_hotkey_dialog);

		// context = getContext();
		// 实例化新的窗口
		Window window = getWindow();
		// 窗口的标题为空
		window.setTitle(null);

		// 调试时不可以使用，权限限制
		// window.requestFeature(Window.FEATURE_NO_TITLE);
		// window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG);
		window.setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
				WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

		// 设置窗口的属性 窗口的宽和高
		final WindowManager.LayoutParams params = window.getAttributes();
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		window.setAttributes(params);

		window.setGravity(Gravity.CENTER);

		window.setFlags(0, WindowManager.LayoutParams.FLAG_DIM_BEHIND);

		// 初始化个控件
		findView();

		// 初始化动画
		initAnimation();

		registerListener();
	}

	/**
	 * 初始化个控件
	 */
	private void findView() {

		if (mScanResult != null) {
			level = mWiFiSignalListActivity.getLevel(mScanResult.level);
		}
		isConfiged = mWiFiSignalListActivity.isConfiged();

		Log.d("tvos", "isConfiged  ::::::::" + isConfiged);

		isconnected = isconnected();

		menuBgRelativeLayout = (RelativeLayout) findViewById(R.id.menuBgRelativeLayout);

		menuFoucsImage = (ImageView) findViewById(R.id.menuFocusImage);
		rightImage = (ImageView) findViewById(R.id.rightImage);
		leftImage = (ImageView) findViewById(R.id.leftImage);
		upImage = (ImageView) findViewById(R.id.upImage);
		downImage = (ImageView) findViewById(R.id.downImage);

		upText = (TextView) findViewById(R.id.upText);
		rightText = (TextView) findViewById(R.id.rightText);
		leftText = (TextView) findViewById(R.id.leftText);
		downText = (TextView) findViewById(R.id.downText);

		bigToSmall = AnimationUtils.loadAnimation(mWiFiSignalListActivity,
				R.anim.tvos_hotkey_big_to_small);

		fadeIn = AnimationUtils.loadAnimation(mWiFiSignalListActivity, R.anim.tvos_hotkey_fade_in);

		rotateFadeIn = AnimationUtils.loadAnimation(mWiFiSignalListActivity,
				R.anim.tvos_hotkey_since_rotate_and_fade_in);
	}

	private void registerListener() {

		rightImage.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				currentToRightAnimation();
			}
		});

		leftImage.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				currentToLeftAnimation();
			}
		});

		upText.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				currentToUpAnimation();
			}
		});

		downImage.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				currentToDownAnimation();
			}
		});

		rightText.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				currentToRightAnimation();
			}
		});

		leftText.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				currentToLeftAnimation();
			}
		});

		upText.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				currentToUpAnimation();
			}
		});

		downText.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				currentToDownAnimation();
			}
		});
	}

	private boolean isconnected() {

		WifiManager mWifiManager = mWiFiSignalListActivity.mWifiManager;
		WifiInfo wifiInfo = mWifiManager.getConnectionInfo();

		if (mWifiManager.isWifiEnabled()) {
			if (null != wifiInfo && null != wifiInfo.getSSID()) {
				if (mWiFiSignalListActivity.result.SSID.equals(wifiInfo.getSSID())) {
					isconnected = true;
				} else {
					isconnected = false;
				}
			} else {
				// wifiInfo 或 wifiInfo.getSSID() 为空
				isconnected = false;
			}
		} else {
			// wifi 不可用。
			isconnected = false;
		}
		return isconnected;
	}

	/**
	 * 初始化动画
	 */
	protected void initAnimation() {
		keyResponse = false;
		menuBgRelativeLayout.startAnimation(rotateFadeIn);
		rotateFadeIn.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				setComponetVisible();
				// 启动圆盘焦点动画
				Message msg = menuFoucsImageFlickerHandler.obtainMessage();
				msg.what = 0;
				menuFoucsImageFlickerHandler.sendMessage(msg);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
	}

	/**
	 * 根据状态设置组件的可见，以及四个按钮的名称
	 */
	protected void setComponetVisible() {

		menuFoucsImage.setVisibility(View.VISIBLE);

		leftText.setVisibility(View.VISIBLE);
		leftImage.setVisibility(View.VISIBLE);

		// if (level <= 0) {
		// rightText.setVisibility(View.INVISIBLE);
		// rightImage.setVisibility(View.INVISIBLE);
		// } else {
		rightText.setVisibility(View.VISIBLE);
		rightImage.setVisibility(View.VISIBLE);
		// }

		if (isConfiged) {
			upText.setVisibility(View.VISIBLE);
			upImage.setVisibility(View.VISIBLE);
			downText.setVisibility(View.VISIBLE);
			downImage.setVisibility(View.VISIBLE);
		} else {
			upText.setVisibility(View.INVISIBLE);
			upImage.setVisibility(View.INVISIBLE);
			downText.setVisibility(View.INVISIBLE);
			downImage.setVisibility(View.INVISIBLE);
		}

		leftText.setText(mWiFiSignalListActivity.getString(R.string.add) + "\n"
				+ mWiFiSignalListActivity.getString(R.string.wifi_net));
		upText.setText(mWiFiSignalListActivity.getString(R.string.forget));
		rightText.setText(mWiFiSignalListActivity.getString(R.string.modigy_password));
		downText.setText(mWiFiSignalListActivity.getString(R.string.choice));

	}

	Handler menuFoucsImageFlickerHandler = new Handler() {
		int i = 0;

		@Override
		public void handleMessage(final Message msg) {
			Log.i(TAG, "handleMessage thread:" + Thread.currentThread().getId() + " msg.what =="
					+ msg.what);
			if (msg.what == 0) {// 处理圆盘焦点动画，重复画3次
				i++;
				menuFoucsImage.startAnimation(fadeIn);
				fadeIn.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationEnd(Animation animation) {
						if (i > 0) {
							keyResponse = true;
						} else {
							Message msg = menuFoucsImageFlickerHandler.obtainMessage();
							msg.what = 0;
							menuFoucsImageFlickerHandler.sendMessage(msg);
						}
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationStart(Animation animation) {
					}
				});
			}

		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (true) {
			Log.d(TAG,
					"onKeyDown keyResponse=" + keyResponse + " keyCode=" + keyCode
							+ " event.getAction()=" + event.getAction() + " event.getCharacters()="
							+ event.getCharacters());
		}
		// 初始化动画完成后才响应
		if (!keyResponse) {
			return false;
		}
		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			currentToLeftAnimation();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			// if (level > 0) {
			currentToRightAnimation();
			// }
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			if (isConfiged) {
				currentToUpAnimation();
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			if (isConfiged) {
				currentToDownAnimation();
			}
			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			dismiss();
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 向左的动画 "添加网络"
	 */
	protected void currentToLeftAnimation() {
		if (moveLeftDistance == 0) {
			moveLeftDistance = (leftImage.getLeft() - rightImage.getRight()) / 2
					- (leftText.getLeft() - menuFoucsImage.getLeft()) / 2;
		}

		TranslateAnimation anim = new TranslateAnimation(0.0f, moveLeftDistance, 0.0f, 0.0f);
		anim.setInterpolator(new DecelerateInterpolator());
		anim.setDuration(300);
		anim.setFillBefore(true);
		// anim.setFillAfter(true);
		keyResponse = false;
		menuFoucsImage.startAnimation(anim);
		anim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				keyResponse = true;
				leftText.startAnimation(bigToSmall);
				bigToSmall.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
					}
				});
				WiFiAddDialog wiFiAddDialog = new WiFiAddDialog(mWiFiSignalListActivity, mHandler);
				wiFiAddDialog.show();
				dismiss();
			}
		});
	}

	/**
	 * 向右的动画
	 */
	protected void currentToRightAnimation() {
		if (moveRightDistance == 0) {
			moveRightDistance = (rightImage.getRight() - leftImage.getLeft()) / 2
					- (rightText.getRight() - menuFoucsImage.getRight()) / 2;
		}

		TranslateAnimation anim = new TranslateAnimation(0.0f, moveRightDistance, 0.0f, 0.0f);
		anim.setInterpolator(new DecelerateInterpolator());
		anim.setDuration(300);
		anim.setFillBefore(true);
		// anim.setFillAfter(true);
		keyResponse = false;
		menuFoucsImage.startAnimation(anim);
		anim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				keyResponse = true;
				rightText.startAnimation(bigToSmall);
				bigToSmall.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
					}
				});

				if (level > 0) {
					wiFiEditDialog = new WiFiEditDialog(mWiFiSignalListActivity, mScanResult,
							mHandler);
					wiFiEditDialog.show();
				} else {
					Toast.makeText(mWiFiSignalListActivity, R.string.wifi_out_of_range,
							Toast.LENGTH_SHORT).show();
				}
				dismiss();
			}
		});
	}

	/**
	 * 向上的动画 "忘记"
	 */
	protected void currentToUpAnimation() {
		if (moveUpDistance == 0) {
			moveUpDistance = (upImage.getTop() - downImage.getBottom()) / 2
					- (upText.getBottom() - menuFoucsImage.getTop()) / 2;
		}

		TranslateAnimation anim = new TranslateAnimation(0.0f, 0.0f, 0.0f, moveUpDistance);
		anim.setInterpolator(new DecelerateInterpolator());
		anim.setDuration(300);
		anim.setFillBefore(true);
		keyResponse = false;
		menuFoucsImage.startAnimation(anim);
		anim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				keyResponse = true;
				upText.startAnimation(bigToSmall);
				bigToSmall.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {

					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation animation) {

					}
				});
				mWiFiSignalListActivity.forget(mWiFiSignalListActivity.mCurrentNetworkId);
				dismiss();
			}
		});
	}

	/**
	 * 向下的动画 "选择"
	 */
	protected void currentToDownAnimation() {
		if (moveDownDistance == 0) {
			moveDownDistance = (downImage.getBottom() - upImage.getTop()) / 2
					- (downText.getTop() - menuFoucsImage.getBottom()) / 2;
		}

		TranslateAnimation anim = new TranslateAnimation(0.0f, 0.0f, 0.0f, moveDownDistance);
		anim.setInterpolator(new DecelerateInterpolator());
		anim.setDuration(300);
		anim.setFillBefore(true);
		// anim.setFillAfter(true);
		keyResponse = false;
		menuFoucsImage.startAnimation(anim);
		anim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				keyResponse = true;
				downText.startAnimation(bigToSmall);
				bigToSmall.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
					}
				});
				direction = 4;
				// ssid = mScanResult.SSID;
				// [2012-2-2yanhd]增加网络连接功能。
				mWiFiSignalListActivity
						.connectTheWifiWithID(mWiFiSignalListActivity.mCurrentNetworkId);
				dismiss();
				mHandler.sendEmptyMessage(mWiFiSignalListActivity.FINISH);
			}
		});
	}
}
