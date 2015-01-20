package com.rtk.tv;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.tv.TvContentRating;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputManager;
import android.media.tv.TvTrackInfo;
import android.media.tv.TvView;
import android.net.Uri;
import android.os.Bundle;
import com.rtk.tv.Debug;
import com.rtk.tv.fragment.AudioModeSwitchFragment;
import com.rtk.tv.fragment.ChannelControlFragment;
import com.rtk.tv.fragment.ChannelListFragment;
import com.rtk.tv.fragment.EpgFragment;
import com.rtk.tv.fragment.InputSourceFragment;
import com.rtk.tv.fragment.MainMenuFragment;
import com.rtk.tv.fragment.PvrControlFragment;
import com.rtk.tv.fragment.QuickSelectLocationFragment;
import com.rtk.tv.fragment.SubtitleSwitchFragment;
import com.rtk.tv.fragment.TvInfoFragment;
import com.rtk.tv.service.IOnServiceConnectComplete;
import com.rtk.tv.service.ITvService;
import com.rtk.tv.service.ServiceManager;
import com.rtk.tv.utils.Keymap;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.GestureDetector;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class RtkTvView extends BaseActivity 
implements IOnServiceConnectComplete{
	private static final long BACK_PRESS_TIMEOUT = 8000;
	private static final String TAG = "RtkTv";
	// for parental control
	public static boolean STATUS_INPUT_PIN_OSD = false;
	private TvView mTvView = null;
	private TvInputManager mTvInputManager = null;
	private TvInputInfo mInfo = null;
	private List<TvInputInfo> infoList;
	private ServiceManager mServiceManager;
	public static final RtkTvView getResumedActivity() {
		if (sInstance != null) {
			return sInstance;
		}
		return null;
	}
	private static RtkTvView sInstance;
	private GestureDetector mGestureDetector;
	long mLastBackPressedTime = -1;
	public Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sInstance = this;

		setContentView(R.layout.activity_tv);
		mGestureDetector = new GestureDetector(this, mGestureListener);

		getFragmentManager()
				.addOnBackStackChangedListener(StackChangedListener);
		mServiceManager = TvApplication.mServiceManager;
		mServiceManager.connectService();
		mServiceManager.setOnServiceConnectComplete(this);
		initTvView();
		handleIntent(getIntent());
	}

	private void initTvView() {
		mTvView = (TvView) findViewById(R.id.tv);

		if (mTvInputManager == null) {
			mTvInputManager = (TvInputManager) getSystemService(Context.TV_INPUT_SERVICE);
		}
		infoList = mTvInputManager.getTvInputList();
		if(infoList!=null && infoList.size()>0)
			mInfo = infoList.get(0);
		mTvView.setCallback(new TvViewTvInputCallback());
		mTvView.setOnUnhandledInputEventListener(new TvViewOnUnhandledInputEventListener());
		mTvInputManager.registerCallback(new TvInputManagerTvInputCallback(),
				new Handler() {
					@Override
					public void handleMessage(Message msg) {
						switch (msg.what) {
						default: {
							Debug.w(TAG, "Unhandled message code: " + msg.what);
							return;
						}
						}
					}
				});
		tm = TvManagerHelper.getInstance(this);
		tm.setServiceManager(mServiceManager);
		tm.setTvView(mTvView);
		tm.reset();
	}
	TvManagerHelper tm;
	
	class TvViewTvInputCallback extends TvView.TvInputCallback {
		public void onConnectionFailed(String inputId) {
			Debug.i(TAG, "onConnectionFailed");
		}

		public void onDisconnected(String inputId) {
			Debug.i(TAG, "onDisconnected");
		}

		public void onVideoSizeChanged(String inputId, int width, int height) {
			Debug.i(TAG, "onVideoSizeChanged");
		}

		// called when we have a new channel
		public void onChannelRetuned(String inputId, Uri channelUri) {
			Debug.i(TAG, "onChannelRetuned: " + inputId + " " + channelUri);
		}

		public void onTracksChanged(String inputId, List<TvTrackInfo> tracks) {
			Debug.i(TAG, "onTracksChanged");
		}

		public void onTrackSelected(String inputId, int type, String trackId) {
			Debug.i(TAG, "onTrackSelected");
		}

		public void onVideoAvailable(String inputId) {
			Debug.i(TAG, "onVideoAvailable");
		}

		public void onVideoUnavailable(String inputId, int reason) {
			Debug.i(TAG, "onVideaUnvailable");
		}

		public void onContentAllowed(String inputId) {
			Debug.i(TAG, "onContentAllowed");
		}

		public void onContentBlocked(String inputId, TvContentRating rating) {
			Debug.i(TAG, "onContentBlocked");
		}

		// SystemApi
		public void onEvent(String inputId, String eventType, Bundle eventArgs) {
			Debug.i(TAG, "onEvent: " + inputId + " " + eventType + " "
					+ eventArgs);
		}
	}

	class TvInputManagerTvInputCallback extends TvInputManager.TvInputCallback {
		public void onInputStateChanged(String inputId, int state) {
			Debug.i(TAG, "onInputStateChanged");
		}

		public void onInputAdded(String inputId) {
			Debug.i(TAG, "onInputAdded");
		}

		public void onInputRemoved(String inputId) {
			Debug.i(TAG, "onInputRemoved");
		}

		public void onInputUpdated(String inputId) {
			Debug.i(TAG, "onInputUpdated");
		}
	}

	class TvViewOnUnhandledInputEventListener implements
			TvView.OnUnhandledInputEventListener {
		@Override
		public boolean onUnhandledInputEvent(InputEvent event) {
			Debug.i(TAG, "onUnhandledInputEvent: " + event);
			return true;
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Start DFB
	}

	@Override
	protected void onStart() {
		super.onStart();
		TvPreference pref = TvPreference.getInstance(this);
		if (pref.isFirstTimeTvApk()) {
			//mark this feature
			/*Fragment f = Fragment.instantiate(this,
					QuickSelectLocationFragment.class.getName());
			getFragmentManager().beginTransaction()
					.add(android.R.id.content, f, STACK_QUICK_SETUP)
					.addToBackStack(STACK_QUICK_SETUP).commit();*/
			pref.setFirstTimeTvApk(false);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onResume() {
		super.onResume();
		Debug.d(TAG, "onResume");
		tm.retune(this);
		checkSignal();
		checkRecordStatus();
		if (TvPreference.getInstance(this).checkNewNit(true)) {
			showNitUpdateDialog();
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mBroadcastReceiver);
		getFragmentManager().removeOnBackStackChangedListener(
				StackChangedListener);
		if (sInstance == this) {
			sInstance = null;
		}
		
		TvApplication.mServiceManager.exit();
		TvApplication.mServiceManager = null;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Debug.d(TAG, "onNewIntent");
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		final String action = intent.getAction();
		Debug.d(TAG, "handleIntent," + action);
		if (ACTION_QUICK_SETUP.equals(action)) {
			// Start quick setup
			// Remove other fragments
			FragmentManager fm = getFragmentManager();
			fm.popBackStack(STACK_MENU,
					FragmentManager.POP_BACK_STACK_INCLUSIVE);
			fm.popBackStack(STACK_QUICK_SETUP,
					FragmentManager.POP_BACK_STACK_INCLUSIVE);
			fm.popBackStack(STACK_LITE,
					FragmentManager.POP_BACK_STACK_INCLUSIVE);
			Fragment f = fm.findFragmentByTag(TAG_LITE);
			if (f != null) {
				fm.beginTransaction().remove(f).commit();
			}
			f = fm.findFragmentByTag(TAG_DIALOG);
			if (f != null) {
				fm.beginTransaction().remove(f).commit();
			}

			f = Fragment.instantiate(this,
					QuickSelectLocationFragment.class.getName());
			fm.beginTransaction()
					.add(android.R.id.content, f, STACK_QUICK_SETUP)
					.addToBackStack(STACK_QUICK_SETUP).commit();

		} else {
			// Open Menu
			int menu = intent.getIntExtra("TVMainMenu", -1);
			if (menu >= 0) {
				showMenuFragment(menu);
			}
		}
	}

	public static interface TvBroadcastListener {
		public boolean onRecieveMsg(String action, int msg);
	}

	final ArrayList<TvBroadcastListener> TvMediaCallbacks = new ArrayList<TvBroadcastListener>();

	public void RemoveTvBroadcatCallback(TvBroadcastListener callback) {
		synchronized (TvMediaCallbacks) {
			TvMediaCallbacks.remove(callback);
		}
	}

	public boolean AddTvBroadcatCallback(TvBroadcastListener callback) {
		boolean ret = false;
		synchronized (TvMediaCallbacks) {
			if (TvMediaCallbacks.contains(callback) == false) {
				TvMediaCallbacks.add(callback);
				ret = true;
			}
		}
		return ret;
	}

	boolean preProcessTvBroadcast(String action, int msgId) {
		Debug.d(TAG, "preProcessTvMediaMsg, not implement, please implement it");
		return true;
	}

	boolean dispatchTvBroadcastProcess(String action, int msgId) {
		Debug.d(TAG, "dispatchTvMediaProcess");
		boolean ret = false;

		for (TvBroadcastListener c : TvMediaCallbacks) {
			if (c instanceof Fragment) {
				if (((Fragment) c).isResumed()) {
					ret = c.onRecieveMsg(action, msgId);
					if (ret)
						break;
				}
			}
		}
		return ret;
	}

	boolean postProcessTvBroadcast(Intent intent, String action) {

		return true;
	}

	/**
	 * Handle TV callbacks
	 */
	private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
		}
	};

	private GestureDetector.OnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {

			int absX = (int) Math.abs(velocityX);
			int absY = (int) Math.abs(velocityY);
			// Horizontal fling: switch source
			if (absX > absY) {
				if (velocityX > 0) {
					 tm.switchToPrevSource();
				} else {
					 tm.switchToNextSoruce();
				}

				// Vertical fling: switch channel
			} else {
				if (velocityY > 0) {
					 tm.switchToPrevChannel();
				} else {
					 tm.switchToNextChannel();
				}
			}
			/*
			 * int curSource= (int)tm.getCurSourceType();
			 * Log.v(TAG,"mGestureListener### curSource  ====="+curSource);
			 * if((curSource==3||curSource==4) && tm.mheg5IsQuietlyTune()){
			 * Debug.d(TAG,"mGestureListener ### can not show info bar=====");
			 * }else{ showLiteFragment(TvInfoFragment.class, false); }
			 */
			return super.onFling(e1, e2, velocityX, velocityY);
		}

	};

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mGestureDetector.onTouchEvent(event)) {
			return true;
		}
		return super.onTouchEvent(event);
	}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case Keymap.KEYCODE_CHANNEL_UP:
            	tm.switchToNextChannel();
                return true;
            case Keymap.KEYCODE_CHANNEL_DOWN:
            	tm.switchToPrevChannel();
                return true;
                // Channel Number
            case Keymap.KEYCODE_0:
            case Keymap.KEYCODE_1:
            case Keymap.KEYCODE_2:
            case Keymap.KEYCODE_3:
            case Keymap.KEYCODE_4:
            case Keymap.KEYCODE_5:
            case Keymap.KEYCODE_6:
            case Keymap.KEYCODE_7:
            case Keymap.KEYCODE_8:
            case Keymap.KEYCODE_9:
                showChannelControl(keyCode);
                return true;
            case Keymap.KEYCODE_MENU:
                showMenuFragment(0);
                return true;
            case Keymap.KEYCODE_INFO:
            	showLiteFragment(TvInfoFragment.class);
                return true;
            case Keymap.KEYCODE_TV_INPUT:
            	showLiteFragment(InputSourceFragment.class);
                return true;
            case Keymap.KEYCODE_CHANNEL:
                showLiteFragment(ChannelListFragment.class);
                return true;
            case Keymap.KEYCODE_BOOKMARK://No favorite
                // Show EPG
            case Keymap.KEYCODE_PROG_GREEN: //test atsc lite epg
                return true;
            case Keymap.KEYCODE_EPG:// KeyEvent.KEYCODE_EPG:
                if (tm.hasProgramInformation()) {
                    showLiteFragment(EpgFragment.class);
                } else {
                    Toast.makeText(this, R.string.msg_no_epg_data_available, Toast.LENGTH_SHORT)
                    .show();
                }
                return true;
            case Keymap.KEYCODE_SWITCH_SUBTITLE:
                showLiteFragment(SubtitleSwitchFragment.class);
                return true;
            case Keymap.KEYCODE_SWITCH_AUDIO:
                showLiteFragment(AudioModeSwitchFragment.class);
                return true;
            case Keymap.KEYCODE_PVR_CTRL:
                if (tm.isPvrSupported()) {
                    showLiteFragment(PvrControlFragment.class);
                } else {
                    Toast.makeText(this, R.string.msg_pvr_not_supported_current_source, Toast.LENGTH_SHORT).show();
                }
                return true;
            case Keymap.KEYCODE_PROG_RED:
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }
	
	private void showChannelControl(int keyCode) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ChannelControlFragment f =
                (ChannelControlFragment) fm.findFragmentByTag("channel_control");
        if (f == null) {
            f =  new ChannelControlFragment();
            ft.add(android.R.id.content, f, "channel_control");
        } else if (f.isDetached()) {
            ft.attach(f);
        }
        f.setFirstKey(keyCode);
        ft.commit();
	}

	private void showMenuFragment(int tab) {
		getMessageHandler().removeMessages(HIDE_CURRENT_FRAGMENT);
        FragmentManager fm = getFragmentManager();
        Fragment f = fm.findFragmentByTag(STACK_MENU);
        if (f != null) {
            fm.popBackStack(STACK_MENU, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {
            // Hide lite fragment
            hideLiteFragment();

            f = new MainMenuFragment();
            Bundle arg = new Bundle();
            arg.putInt(MainMenuFragment.ARG_TAB_INDEX, tab);
            f.setArguments(arg);
            fm.beginTransaction()
            .add(android.R.id.content, f, STACK_MENU)
            .addToBackStack(STACK_MENU)
            .commit();
        }
        setCurrentFragment(f);
        getMessageHandler().sendEmptyMessageDelayed(HIDE_CURRENT_FRAGMENT,fragmentTimeout);
	}	

	/**
	 * Fragments shown by this function are mutual exclusive.
	 * 
	 * @param f
	 */
	public void showLiteFragment(Class<? extends Fragment> f, boolean toggle) {
        FragmentManager fm = getFragmentManager();
        Fragment prev = fm.findFragmentByTag(TAG_LITE);
        boolean add = true;
        FragmentTransaction transaction = fm.beginTransaction();
        if (prev != null) {
            // Pop-up previous fragment
            if (!f.equals(prev.getClass())) {
                //transaction.remove(prev);
                fm.popBackStack(STACK_LITE, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                add = true;

            } else if (toggle) {
                //transaction.remove(prev);
                fm.popBackStack(STACK_LITE, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                add = false;
            } else {
                // Keep it. Do nothing.
                add = false;
            }
        }

        if (add && fm.findFragmentByTag(STACK_MENU) == null) {
            Fragment fragment = Fragment.instantiate(this, f.getName());
            //transaction.add(android.R.id.content, fragment, TAG_LITE);
            /****add lite fragment to stack ****/
            transaction.add(android.R.id.content, fragment, TAG_LITE).addToBackStack(STACK_LITE);
        }
        transaction.commit();
	}

	/**
	 * Fragments shown by this function are mutual exclusive.
	 * 
	 * @param f
	 */
	public void showLiteFragment(Class<? extends Fragment> f) {
		showLiteFragment(f, true);
	}

	public boolean hideLiteFragment() {
		FragmentManager fm = getFragmentManager();
		Fragment f = fm.findFragmentByTag(TAG_LITE);
		if (f != null) {
			fm.popBackStack(STACK_LITE,
					FragmentManager.POP_BACK_STACK_INCLUSIVE);
			// fm.beginTransaction().remove(f).commit();
			return true;
		}
		return false;
	}

	public void setNoDisplayFragmentShown(boolean show,
			Class<? extends Fragment> clazz) {
		FragmentManager fm = getFragmentManager();
		fm.executePendingTransactions();
		Fragment f = fm.findFragmentByTag(TAG_NO_DISPLAY);
		if (show) {
			if (f != null) {
				if (clazz.equals(f.getClass())) {
					return;
				} else {
					fm.beginTransaction().remove(f).commitAllowingStateLoss();
				}
			}

			f = Fragment.instantiate(this, clazz.getName());
			// Add in the background container to ensure it will not block any
			// other fragment.
			fm.beginTransaction()
					.add(R.id.container_background, f, TAG_NO_DISPLAY)
					.commitAllowingStateLoss(); // TODO: need addToBackStack?
		} else {
			if (f != null) {
				fm.beginTransaction().remove(f).commitAllowingStateLoss();
			}
		}
	}

	public void checkSignal() {

	}

	@Override
	public void onBackPressed() {
		if (!getFragmentManager().popBackStackImmediate()) {
			long time = SystemClock.uptimeMillis();
			if (time - mLastBackPressedTime >= BACK_PRESS_TIMEOUT) {
				Toast.makeText(this, R.string.STRING_MSG_EXIT,
						Toast.LENGTH_SHORT).show();
			} else {
				finish();
			}
			mLastBackPressedTime = time;
		}
	}

	private void checkRecordStatus() {

	}

	private void setRecordStatusShown(boolean show) {
		View stat = findViewById(R.id.status_record);
		ViewGroup parent = (ViewGroup) findViewById(android.R.id.content);
		if (show && stat == null) {
			LayoutInflater.from(this).inflate(R.layout.status_record, parent,
					true);
		} else if (!show && stat != null) {
			parent.removeView(stat);
		}
	}

	public void showNitUpdateDialog() {

	}

	public void onCheckLock() {
		Debug.d(TAG, "onCheckLock, Do CheckLock Timer!");
		getMessageHandler().removeCallbacks(TV_CHECK_LOCK_RUNNABLE);
		getMessageHandler().postDelayed(TV_CHECK_LOCK_RUNNABLE, 1000);
	}

	boolean checkLockHappen() {
		boolean ret = false;

		return ret;
	}

	boolean checkNoSignalHappen() {
		boolean ret = false;
		/*
		 * now no signal is always shown, if you want not show at the same time
		 * with other fragment. please implement here
		 */
		Debug.d(TAG, "checkNoSignalHappen, not implement ret:" + ret);
		return ret;
	}

	boolean checkAudioOnlyHappen() {
		boolean ret = false;
		/*
		 * now audio only is always shown, if you want not show at the same time
		 * with other fragment. please implement here
		 */
		Debug.d(TAG, "checkAudioOnlyHappen, not implement ret:" + ret);
		return ret;
	}

	boolean checkServiceNotRunHappen() {
		boolean ret = false;
		// please implement here
		Debug.d(TAG, "checkServiceNotRunHappen, not implement ret:" + ret);
		return ret;
	}

	boolean checkEncryptedChannelHappen() {
		boolean ret = false;
		// please implement here
		Debug.d(TAG, "checkEncryptedChannelHappen, not implement ret:" + ret);
		return ret;
	}

	boolean checkMuteHappen() {
		boolean ret = false;
		// please implement here
		Debug.d(TAG, "checkMuteHappen, not implement ret:" + ret);
		return ret;
	}

	boolean checkNoMenuHandle() {
		if (checkLockHappen())
			return true;
		else if (checkNoSignalHappen())
			return true;
		else if (checkAudioOnlyHappen())
			return true;
		else if (checkServiceNotRunHappen())
			return true;
		else if (checkEncryptedChannelHappen())
			return true;
		else if (checkMuteHappen())
			return true;

		return false;
	}

	protected Runnable TV_NOMENU_RUNNABLE = new Runnable() {
		public void run() {
			if (getFragmentManager().getBackStackEntryCount() <= 0)
				checkNoMenuHandle();
			else {
				Debug.d(TAG, "you can check here using fragment vector");
				// Vector <Fragment> fgvector = getFragmentVector();
				//
			}
		}
	};
	protected Runnable TV_CHECK_LOCK_RUNNABLE = new Runnable() {
		public void run() {
			// no menu, or input password osd exists, need check lock
			if (getFragmentManager().getBackStackEntryCount() <= 0
					|| STATUS_INPUT_PIN_OSD)
				checkLockHappen();
			else {
				Debug.d(TAG, "you can check here using fragment vector");
				// Vector <Fragment> fgvector = getFragmentVector();
				//
			}

		}
	};

	public Vector<Fragment> getFragmentVector() {
		boolean debug_FV = true;
		Vector<Fragment> fragments = new Vector<Fragment>();
		FragmentManager fm = getFragmentManager();
		int cnt = 0;
		Bundle bundle = new Bundle();
		do {
			bundle.putInt("Index", cnt);
			Fragment fg = null;
			try {
				fg = fm.getFragment(bundle, "Index");
			} catch (IllegalStateException e) {
					e.printStackTrace();
			}

			if (fg != null) {
				fragments.add(fg);
				if (debug_FV)
					Debug.d(TAG, "(" + cnt + ")" + "getFragmentVector:"
							+ fg.getClass().getSimpleName());
			} else
				break;
			cnt++;
		} while (true);
		return fragments;
	}

	protected FragmentManager.OnBackStackChangedListener StackChangedListener = new FragmentManager.OnBackStackChangedListener() {
		public void onBackStackChanged() {
			int Laststack = getFragmentManager().getBackStackEntryCount() - 1;
			String name = null;
			if (Laststack >= 0) {
				getMessageHandler().removeCallbacks(TV_NOMENU_RUNNABLE);
				name = getFragmentManager().getBackStackEntryAt(Laststack)
						.getName();
				// Debug.d(TAG,"GET BACKSTACK COUNT:"+Laststack);
				Debug.d(TAG, "onBackStackChanged LAST STACK Name:" + name);
				name = null;
			} else {
				// Laststack < 0
				getMessageHandler().removeCallbacks(TV_NOMENU_RUNNABLE);
				getMessageHandler().postDelayed(TV_NOMENU_RUNNABLE, 1000);
				Debug.d(TAG, "onBackStackChanged, post nomenu runnable");
			}

		}
	};
	@Override
	public void onServiceConnectComplete(ITvService service) {
		// TODO
		Debug.d(TAG, "service binded");
		tm.retune(this);
	}
}
