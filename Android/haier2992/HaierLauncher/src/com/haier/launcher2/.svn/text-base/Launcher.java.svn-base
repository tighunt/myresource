
/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haier.launcher2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ComponentCallbacks2;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.AudioSystem;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.speech.RecognizerIntent;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.IWindowManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Advanceable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView;

import com.haier.launcher.R;
import com.haier.launcher2.DropTarget.DragObject;
import android.app.TvManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.widget.RtkAtvViewEx;
import android.media.AudioManager;
import android.media.MediaPlayer;
//import com.mstar.tv.service.aidl.EN_INPUT_SOURCE_TYPE;
//import com.mstar.tv.service.aidl.EN_MEMBER_SERVICE_TYPE;
//import com.mstar.tv.service.skin.ChannelSkin;
//import com.mstar.tv.service.skin.CommonSkin;
//import com.mstar.tv.service.skin.PictureSkin;
//zgzs
/*import com.tvos.common.TvManager;
import com.tvos.common.TvManager.EnumScreenMuteType;
import com.tvos.common.TvPlayer;
import com.tvos.common.exception.TvCommonException;
import com.tvos.common.vo.TvOsType.EnumScalerWindow;
import com.tvos.common.vo.VideoWindowType;*/


/**
 * Default launcher application.
 */
public final class Launcher extends Activity
        implements View.OnClickListener, OnLongClickListener, LauncherModel.Callbacks,
                   AllAppsView.Watcher, View.OnTouchListener {
    static final String TAG = "Launcher";
    static final boolean LOGD = false;

    static final boolean PROFILE_STARTUP = false;
    static final boolean DEBUG_WIDGETS = false;

    private static final int MENU_GROUP_WALLPAPER = 1;
    // Add by weijh 2012/2/20 增加菜单项
    private static final int MENU_GROUP_ADD_SHORTCUTINFO = MENU_GROUP_WALLPAPER + 1;
    private static final int MENU_GROUP_DEL_SHORTCUTINFO = MENU_GROUP_ADD_SHORTCUTINFO + 1;
    private static final int MENU_GROUP_MANAGER = MENU_GROUP_DEL_SHORTCUTINFO + 1;
    private static final int MENU_GROUP_SYSTEM_SETTINGS = MENU_GROUP_MANAGER + 1;
    
    private static final int MENU_WALLPAPER_SETTINGS = Menu.FIRST + 1;
    private static final int MENU_MANAGE_APPS = MENU_WALLPAPER_SETTINGS + 1;
    private static final int MENU_SYSTEM_SETTINGS = MENU_MANAGE_APPS + 1;
    private static final int MENU_HELP = MENU_SYSTEM_SETTINGS + 1;
    
    // Add by weijh 2012/2/20 增加菜单项
    private static final int MENU_ADD_SHORTCUTINFO = MENU_HELP + 1;
    private static final int MENU_DEL_SHORTCUTINFO = MENU_ADD_SHORTCUTINFO + 1;

    private static final int REQUEST_CREATE_SHORTCUT = 1;
    private static final int REQUEST_CREATE_APPWIDGET = 5;
    private static final int REQUEST_PICK_APPLICATION = 6;
    private static final int REQUEST_PICK_SHORTCUT = 7;
    private static final int REQUEST_PICK_APPWIDGET = 9;
    private static final int REQUEST_PICK_WALLPAPER = 10;

    static final String EXTRA_SHORTCUT_DUPLICATE = "duplicate";

    static final int SCREEN_COUNT = 3;//5 emirguo 20120204
    static final int DEFAULT_SCREEN = 1;//2 emirguo 20120204
    static final int MENU_ADD_SCREEN = 0;
    //static final String ConfigPath = "/var/Launcher.txt";
//zgzs
//    static final String ConfigPath = "/var/tmp/Launcher.txt";

    static final int DIALOG_CREATE_SHORTCUT = 1;
    static final int DIALOG_RENAME_FOLDER = 2;

    private static final String PREFERENCES = "launcher.preferences";

    // Type: int
    private static final String RUNTIME_STATE_CURRENT_SCREEN = "launcher.current_screen";
    // Type: int
    private static final String RUNTIME_STATE = "launcher.state";
    // Type: int
    private static final String RUNTIME_STATE_PENDING_ADD_CONTAINER = "launcher.add_container";
    // Type: int
    private static final String RUNTIME_STATE_PENDING_ADD_SCREEN = "launcher.add_screen";
    // Type: int
    private static final String RUNTIME_STATE_PENDING_ADD_CELL_X = "launcher.add_cell_x";
    // Type: int
    private static final String RUNTIME_STATE_PENDING_ADD_CELL_Y = "launcher.add_cell_y";
    // Type: boolean
    private static final String RUNTIME_STATE_PENDING_FOLDER_RENAME = "launcher.rename_folder";
    // Type: long
    private static final String RUNTIME_STATE_PENDING_FOLDER_RENAME_ID = "launcher.rename_folder_id";

    private static final String TOOLBAR_ICON_METADATA_NAME = "com.android.launcher.toolbar_icon";

    /** The different states that Launcher can be in. */
    private enum State { WORKSPACE, APPS_CUSTOMIZE, APPS_CUSTOMIZE_SPRING_LOADED };
    private static State mState = State.WORKSPACE;
    private AnimatorSet mStateAnimation;
    private AnimatorSet mDividerAnimator;

    static final int APPWIDGET_HOST_ID = 1024;
    private static final int EXIT_SPRINGLOADED_MODE_SHORT_TIMEOUT = 300;
    private static final int EXIT_SPRINGLOADED_MODE_LONG_TIMEOUT = 600;
    private static final int SHOW_CLING_DURATION = 550;
    private static final int DISMISS_CLING_DURATION = 250;

    private static final Object sLock = new Object();
    private static int sScreen = DEFAULT_SCREEN;

    private final BroadcastReceiver mCloseSystemDialogsReceiver
            = new CloseSystemDialogsIntentReceiver();
    private final ContentObserver mWidgetObserver = new AppWidgetResetObserver();

    
    //for Haier custom's define [hunter.zheng] 2012.2.3
    private static final boolean ENABLE_SHOW_CLING=false;//force disable cling show
    private static final boolean ENABLE_SHOW_SEARCHBAR=false;//force hide search bar in workspace
    
    
    private LayoutInflater mInflater;

    private Workspace mWorkspace;
    private View mQsbDivider;
    private View mDockDivider;
    private DragLayer mDragLayer;
    private DragController mDragController;
    private View mView;

    private AppWidgetManager mAppWidgetManager;
    private LauncherAppWidgetHost mAppWidgetHost;

    private ItemInfo mPendingAddInfo = new ItemInfo();
    private int[] mTmpAddItemCellCoordinates = new int[2];

    private FolderInfo mFolderInfo;

    private Hotseat mHotseat;
    
	// hidden allAppsButton *** add by zhoujf 2012-02-20***
//     private View mAllAppsButton;
    
    private boolean PowerOn = true; //emirguo add for run tv when poweron,20120224
    private boolean FirstSetTvWindow = true;
    private boolean m_bOnPauseByBackgroundActivity = false;
    //zgzs
//    private static VideoWindowType videoWindowType;
//    private static PictureSkin pictureSkin;
 //   private static CommonSkin commonSkin;
//    private ChannelSkin channelSkin;
    static boolean m_tvIsSmallWindows = false;
	// *** add by zhoujf 2012-02-21***
    private boolean mDelayChangeTvWindow = false;
    static boolean mShowTvBg = false;
    private static ImageView image_view;
    private static SurfaceView tv_SurfaceView;
    static View tv_DefaultScreen;
    private static boolean mBroadcastWaittingForChangeWindow;
    private static boolean mBroadcastSetBigWindow;
    public static boolean m_tvNeedDelayChangeSource = true;
    private boolean m_tvWaittingForChange = false;
    private static boolean m_tvNeedChangeWindow = true;
    private boolean m_tvNeedChangeSource = false;
    private boolean tv_bShowTvWindow;
    private static Thread onPauseChangeTvSourceToStoragThread;
    private static Thread onResumeChangeInputSourceThread;
    
    public static MyFunDialog mMyFunDialog;
	private View mMyFunView;
	private Button mEnterMyFun;
	private Button mCancleMyFun;
	private TextView mTextFindUsb;
	private TextView mTextMyFun;
	
	private int mCount = 0;
	private Timer mTimer;
	private MyTimerTask task;
	private final int START_COUNT = 200001;  
    
    /*记录最后一次点击allapps图标的时间*/
    private long mAllAppsLastClickTime = 0;
    
    //emirguo add for keyaudio
    public static MediaPlayer mMediaPlayer;
    public static boolean mDTMFToneEnabled; //按键操作音	
	// *** add by zhoujf 2012-03-27*** 定义我们自己添加的ImageView
	public ImageView dot_1;
	public ImageView dot_2;
	public ImageView dot_3;
	
	public FrameLayout mcontent;
	
	private static final String STR_STATUS_NONE = "0";
	private static final String STR_STATUS_SUSPENDING = "1";
	private static final String STR_STATUS_WAKEUP = "2";
	private static final String STR_STATUS_FIRST_POWERON = "3";

    private SearchDropTargetBar mSearchDropTargetBar;
    private AppsCustomizeTabHost mAppsCustomizeTabHost;
    private AppsCustomizePagedView mAppsCustomizeContent;
    private boolean mAutoAdvanceRunning = false;

    private Bundle mSavedState;

    private SpannableStringBuilder mDefaultKeySsb = null;

    private boolean mWorkspaceLoading = true;

    private boolean mPaused = true;
    private boolean mRestoring;
    private boolean mWaitingForResult;
    private boolean mOnResumeNeedsLoad;

    private Bundle mSavedInstanceState;

    private LauncherModel mModel;
    private IconCache mIconCache;
    private boolean mUserPresent = true;
    private boolean mVisible = false;
    private boolean mAttached = false;

    private static LocaleConfiguration sLocaleConfiguration = null;

    private static HashMap<Long, FolderInfo> sFolders = new HashMap<Long, FolderInfo>();

    private Intent mAppMarketIntent = null;

    // Related to the auto-advancing of widgets
    private final int ADVANCE_MSG = 1;
    private final int TV_WINDOW_MSG = 2;
    private final int mAdvanceInterval = 20000;
    private final int mAdvanceStagger = 250;
    private long mAutoAdvanceSentTime;
    private long mAutoAdvanceTimeLeft = -1;
    private HashMap<View, AppWidgetProviderInfo> mWidgetsToAdvance =
        new HashMap<View, AppWidgetProviderInfo>();

    // Determines how long to wait after a rotation before restoring the screen orientation to
    // match the sensor state.
    private final int mRestoreScreenOrientationDelay = 500;

    // External icons saved in case of resource changes, orientation, etc.
    private static Drawable.ConstantState[] sGlobalSearchIcon = new Drawable.ConstantState[2];
    private static Drawable.ConstantState[] sVoiceSearchIcon = new Drawable.ConstantState[2];
    private static Drawable.ConstantState[] sAppMarketIcon = new Drawable.ConstantState[2];

    static final ArrayList<String> sDumpLogs = new ArrayList<String>();

    private BubbleTextView mWaitingForResume;
//zgzs
	private TvManager mTvManager;
	MediaPlayer mediaPlayer;
	/*********************************************************
	 * WBL add for tv window.
	 ********************************************************/
	private static RtkAtvViewEx mRtkAtvView;
	private boolean bRtkAtvViewOpen = false;
	private boolean bGotoTvServer = false;
	/********************************************************/
	
	private boolean is4k2k = false;
	private int mCurLiveSource = -1;

    private Runnable mBuildLayersRunnable = new Runnable() {
        public void run() {
            if (mWorkspace != null) {
                mWorkspace.buildPageHardwareLayers();
            }
        }
    };

    private static ArrayList<PendingAddArguments> sPendingAddList
            = new ArrayList<PendingAddArguments>();

    private static class PendingAddArguments {
        int requestCode;
        Intent intent;
        long container;
        int screen;
        int cellX;
        int cellY;
    }
    
	@SuppressWarnings("unused")
	private void deleteLauncherDB() {

		Log.i("zhoujf", "Launcher.deleteLauncherDB()......");
		List<Integer> containers = LauncherProvider.queryContainers();
		int count = 0;
		if (containers != null) {
			Log.i("zhoujf", "Launcher.deleteLauncherDB().containers != null");
			for (int i = 0; i < containers.size(); i++) {
				if (containers.get(i).intValue() > 0) {
					count++;
				}
			}
			if (count == 1) {
				String path = "/data/data/com.haier.launcher/databases/launcher.db";
				File file = new File(path);
				Log.i("zhoujf", file + "");
				if (!file.exists()) {
					Log.i("zhoujf", "Launcher.deleteLauncherDB()_!file.exists()");
					return;
				} else {
					Log.i("zhoujf", "Launcher.deleteLauncherDB()_delete:" + file.delete());
				}
			}
		} else {
			Log.i("zhoujf", "Launcher.deleteLauncherDB()_containers == null");
		}
	}
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("here1");
     //  Log.d("shangbaorun","shangbaorun-===============test=========shangbaorun====0nCreate");
       
//zgzs 2013-5-18
       
        mTvManager = (TvManager) this.getSystemService("tv");
        mediaPlayer=new MediaPlayer();
        
      //  mTvManager.setLauncherCancelTimer(false);
        
       /* mTvManager.setSourceAndDisplayWindow(mTvManager.getCurLiveSource(), 111,140,512,288);
        mTvManager.setVideoAreaOn(111,140,512,288,1);*/
        
        Log.i("zhoujf", "Launcher.onCreate......");
        //deleteLauncherDB();

        LauncherApplication app = ((LauncherApplication)getApplication());
        mModel = app.setLauncher(this);
        mIconCache = app.getIconCache();
        mDragController = new DragController(this);
        mInflater = getLayoutInflater();


        mAppWidgetManager = AppWidgetManager.getInstance(this);
        mAppWidgetHost = new LauncherAppWidgetHost(this, APPWIDGET_HOST_ID);
        mAppWidgetHost.startListening();

        if (PROFILE_STARTUP) {
            android.os.Debug.startMethodTracing(
                    Environment.getExternalStorageDirectory() + "/launcher");
        }
//zgzs        
        /*readLauncherConfig();
        if (PowerOn){
        	writeLauncherConfig();
        }*/

        checkForLocaleChange();
        setContentView(R.layout.launcher);
        setupViews();
       
        showFirstRunWorkspaceCling();
        registerContentObservers();

        lockAllApps();

        mSavedState = savedInstanceState;
        restoreState(mSavedState);

        // Update customization drawer _after_ restoring the states
        if (mAppsCustomizeContent != null) {
            mAppsCustomizeContent.onPackagesUpdated();
        }

        if (PROFILE_STARTUP) {
            android.os.Debug.stopMethodTracing();
        }

        if (!mRestoring) {
            mModel.startLoader(this, true);
        }

        if (!mModel.isAllAppsLoaded()) {
            ViewGroup appsCustomizeContentParent = (ViewGroup) mAppsCustomizeContent.getParent();
            mInflater.inflate(R.layout.apps_customize_progressbar, appsCustomizeContentParent);
        }

        // For handling default keys
        mDefaultKeySsb = new SpannableStringBuilder();
        Selection.setSelection(mDefaultKeySsb, 0);

        IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(mCloseSystemDialogsReceiver, filter);

        boolean searchVisible = false;
        boolean voiceVisible = false;
        // If we have a saved version of these external icons, we load them up immediately
        int coi = getCurrentOrientationIndexForGlobalIcons();
        if (sGlobalSearchIcon[coi] == null || sVoiceSearchIcon[coi] == null ||
                sAppMarketIcon[coi] == null) {
            updateAppMarketIcon();
            searchVisible = updateGlobalSearchIcon();
            voiceVisible = updateVoiceSearchIcon(searchVisible);
        }
        if (sGlobalSearchIcon[coi] != null) {
             updateGlobalSearchIcon(sGlobalSearchIcon[coi]);
             searchVisible = true;
        }
        if (sVoiceSearchIcon[coi] != null) {
            updateVoiceSearchIcon(sVoiceSearchIcon[coi]);
            voiceVisible = true;
        }
        if (sAppMarketIcon[coi] != null) {
            updateAppMarketIcon(sAppMarketIcon[coi]);
        }
        mSearchDropTargetBar.onSearchPackagesChanged(searchVisible, voiceVisible);

        // On large interfaces, we want the screen to auto-rotate based on the current orientation
        if (LauncherApplication.isScreenLarge() || Build.TYPE.contentEquals("eng")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
        
        IntentFilter HotKeyFilter = new IntentFilter();
        HotKeyFilter.addAction("com.haier.launcher.HOTKEY.TV");
//zgzs        HotKeyFilter.addAction("com.mstar.tv.service.TIME_EVENT_LAST_MINUTE_WARN");
//        HotKeyFilter.addAction("com.mstar.tv.service.TIME_EVENT_DESTROY_COUNT_DOWN");
 //       HotKeyFilter.addAction("com.mstar.tv.service.TIME_EVENT_LAST_MINUTE_UPDATE");
        HotKeyFilter.addAction("TVCounterActivity.notShow");
        Log.d(TAG, "cxj test HotkeyReceiver");
        this.registerReceiver(HotKeyReceiver, HotKeyFilter);		
//    	pictureSkin = new PictureSkin(this);
 //       pictureSkin.connect(null);
        
 //       commonSkin = new CommonSkin(this);
  //      commonSkin.connect(null);
        
//        channelSkin = new ChannelSkin(this);
//        channelSkin.connect(null);
    	
    	setupTvViews();
    	initRtkAtvView();
    	findMyfunView();//for myfun dialog by hushuai 20140107
        System.out.println("here3");
    	mcontent = (FrameLayout) findViewById(R.id.drag_layer);
    	mcontent.setVisibility(View.INVISIBLE);
    	updateWallpaperVisibility(false);
    }
    
    private BroadcastReceiver HotKeyReceiver  = new BroadcastReceiver() { 

        @Override 
        public void onReceive(Context context, Intent intent) { 
        	Log.i(TAG, " cxj ============get TV hotkey==============" + intent.getAction());
        	if(intent.getAction().equals("com.haier.launcher.HOTKEY.TV")){
	        	m_tvNeedChangeSource = false;
	        	m_tvNeedDelayChangeSource = false;
	        	m_tvNeedChangeWindow = true;
	        	mShowTvBg = true;
        	}else{//for TVCounterActivity
        		if(intent.getAction().equals("TVCounterActivity.notShow")){
        			isCounterShowing = false;//TVCounterActivity is pause
        		}
        		else if(!isCounterShowing){
        			isCounterShowing = true; 
//zgzs        			Log.d(TAG, "com.mstar.tvsetting.hotkey.intent.action.TVCounterActivity");
        			Intent tvCounterIntent = new Intent("com.hrtvbic.tvsetting.hotkey.intent.action.TVCounterActivity");
        			// tvCounterIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        			tvCounterIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        			context.startActivity(tvCounterIntent);
        			Intent ipause = new Intent("hrtvbic.tvsetting.ui.pausemainmenu");
        			Log.i("BroadcastRev","--------------send-------------");
        			context.sendBroadcast(ipause);
        		}
        	}
        } 
    };
    /**for myfun dialog ,so regesit BroadcastReceiver*/
    private final BroadcastReceiver mTabletReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			
			String action = intent.getAction();
			
			if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
				Log.i("hzk","mount");

				if (!getCurrentAppPackage().equals("com.hrtvbic.usb.S6A801")) {
					mCancleMyFun.requestFocus();
					mMyFunDialog.show();				
					mCount = 0;
					mTimer = new Timer(true);
					if (task != null) {
						task.cancel();
					}
					task = new MyTimerTask();
					mTimer.schedule(task, 0, 1000);
					if (!mCancleMyFun.isFocused()) {
								Intent myIntent = new Intent("com.haier.myfun.focus");
								context.sendBroadcast(myIntent);
							}
						}

			} else if (action.equals(Intent.ACTION_MEDIA_EJECT) || action.equals(Intent.ACTION_MEDIA_REMOVED)
					|| action.equals(Intent.ACTION_MEDIA_BAD_REMOVAL)) {				
				Log.i("hzk", "remove");
				if(mMyFunDialog.isShowing())
					mMyFunDialog.dismiss();
			}
		}
	};
//zgzs
 /*   private boolean readLauncherConfig(){
		try {
			FileInputStream inStream = new FileInputStream(ConfigPath);
			int length = inStream.available(); 
    	    byte [] buffer = new byte[length];
			if((inStream.read(buffer))!=-1) {
				PowerOn = false;//读到了数据
						}
		    inStream.close();
		} catch (FileNotFoundException e) {
		    //e.printStackTrace();
		    return false;
		} catch (IOException e){
		    return false;
		}
		return true;
    }
    
    private boolean writeLauncherConfig(){
		try {
			FileOutputStream outStream = new FileOutputStream(ConfigPath);
			outStream.write("true".getBytes());
			outStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e){
			return false;
		}
		return true;
    }*/
    
    private void checkForLocaleChange() {
        if (sLocaleConfiguration == null) {
            new AsyncTask<Void, Void, LocaleConfiguration>() {
                @Override
                protected LocaleConfiguration doInBackground(Void... unused) {
                    LocaleConfiguration localeConfiguration = new LocaleConfiguration();
                    readConfiguration(Launcher.this, localeConfiguration);
                    return localeConfiguration;
                }

                @Override
                protected void onPostExecute(LocaleConfiguration result) {
                    sLocaleConfiguration = result;
                    checkForLocaleChange();  // recursive, but now with a locale configuration
                }
            }.execute();
            return;
        }

        final Configuration configuration = getResources().getConfiguration();

        final String previousLocale = sLocaleConfiguration.locale;
        final String locale = configuration.locale.toString();

        final int previousMcc = sLocaleConfiguration.mcc;
        final int mcc = configuration.mcc;

        final int previousMnc = sLocaleConfiguration.mnc;
        final int mnc = configuration.mnc;

        boolean localeChanged = !locale.equals(previousLocale) || mcc != previousMcc || mnc != previousMnc;

        if (localeChanged) {
            sLocaleConfiguration.locale = locale;
            sLocaleConfiguration.mcc = mcc;
            sLocaleConfiguration.mnc = mnc;

            mIconCache.flush();

            final LocaleConfiguration localeConfiguration = sLocaleConfiguration;
            new Thread("WriteLocaleConfiguration") {
                @Override
                public void run() {
                    writeConfiguration(Launcher.this, localeConfiguration);
                }
            }.start();
        }
    }

    private static class LocaleConfiguration {
        public String locale;
        public int mcc = -1;
        public int mnc = -1;
    }

    private static void readConfiguration(Context context, LocaleConfiguration configuration) {
        DataInputStream in = null;
        try {
            in = new DataInputStream(context.openFileInput(PREFERENCES));
            configuration.locale = in.readUTF();
            configuration.mcc = in.readInt();
            configuration.mnc = in.readInt();
        } catch (FileNotFoundException e) {
            // Ignore
        } catch (IOException e) {
            // Ignore
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }

    private static void writeConfiguration(Context context, LocaleConfiguration configuration) {
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(context.openFileOutput(PREFERENCES, MODE_PRIVATE));
            out.writeUTF(configuration.locale);
            out.writeInt(configuration.mcc);
            out.writeInt(configuration.mnc);
            out.flush();
        } catch (FileNotFoundException e) {
            // Ignore
        } catch (IOException e) {
            //noinspection ResultOfMethodCallIgnored
            context.getFileStreamPath(PREFERENCES).delete();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }

    public DragLayer getDragLayer() {
        return mDragLayer;
    }

    static int getScreen() {
        synchronized (sLock) {
            return sScreen;
        }
    }

    static void setScreen(int screen) {
        synchronized (sLock) {
            sScreen = screen;
        }
    }

    /**
     * Returns whether we should delay spring loaded mode -- for shortcuts and widgets that have
     * a configuration step, this allows the proper animations to run after other transitions.
     */
    private boolean completeAdd(PendingAddArguments args) {
        boolean result = false;
        switch (args.requestCode) {
            case REQUEST_PICK_APPLICATION:
                completeAddApplication(args.intent, args.container, args.screen, args.cellX,
                        args.cellY);
                break;
            case REQUEST_PICK_SHORTCUT:
                processShortcut(args.intent);
                break;
            case REQUEST_CREATE_SHORTCUT:
                completeAddShortcut(args.intent, args.container, args.screen, args.cellX,
                        args.cellY);
                result = true;
                break;
            case REQUEST_PICK_APPWIDGET:
                addAppWidgetFromPick(args.intent);
                break;
            case REQUEST_CREATE_APPWIDGET:
                int appWidgetId = args.intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
                completeAddAppWidget(appWidgetId, args.container, args.screen);
                result = true;
                break;
            case REQUEST_PICK_WALLPAPER:
                // We just wanted the activity result here so we can clear mWaitingForResult
                break;
        }
        // In any situation where we have a multi-step drop, we should reset the add info only after
        // we complete the drop
        resetAddInfo();
        return result;
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, final Intent data) {
        boolean delayExitSpringLoadedMode = false;
        mWaitingForResult = false;

        // The pattern used here is that a user PICKs a specific application,
        // which, depending on the target, might need to CREATE the actual target.

        // For example, the user would PICK_SHORTCUT for "Music playlist", and we
        // launch over to the Music app to actually CREATE_SHORTCUT.
        if (resultCode == RESULT_OK && mPendingAddInfo.container != ItemInfo.NO_ID) {
            final PendingAddArguments args = new PendingAddArguments();
            args.requestCode = requestCode;
            args.intent = data;
            args.container = mPendingAddInfo.container;
            args.screen = mPendingAddInfo.screen;
            args.cellX = mPendingAddInfo.cellX;
            args.cellY = mPendingAddInfo.cellY;

            // If the loader is still running, defer the add until it is done.
            if (isWorkspaceLocked()) {
                sPendingAddList.add(args);
            } else {
                delayExitSpringLoadedMode = completeAdd(args);
            }
        } else if ((requestCode == REQUEST_PICK_APPWIDGET ||
                requestCode == REQUEST_CREATE_APPWIDGET) && resultCode == RESULT_CANCELED) {
            if (data != null) {
                // Clean up the appWidgetId if we canceled
                int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
                if (appWidgetId != -1) {
                    mAppWidgetHost.deleteAppWidgetId(appWidgetId);
                }
            }
        } else if ((requestCode == REQUEST_PICK_WALLPAPER) && (resultCode == RESULT_OK)){
        	if (mBroadcastWaittingForChangeWindow){
        		mBroadcastSetBigWindow = false;
        		Log.i(TAG, "========REQUEST_PICK_WALLPAPER==========");
        	}
        }

        // Exit spring loaded mode if necessary after cancelling the configuration of a widget
        exitSpringLoadedDragModeDelayed((resultCode != RESULT_CANCELED), delayExitSpringLoadedMode);
    }

    @Override
    protected void onResume() {
    	Log.d(TAG, "cxj test enter onResume");
        super.onResume();
      //  Log.d("shangbaorun","shangbaorun-===============test=========shangbaorun====0nCreate");
		mTvManager.setLauncherCancelTimer(false);
        
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE); 
		List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
		ComponentName componentInfo = taskInfo.get(0).topActivity;
		String packageName = componentInfo.getPackageName();
		String className = componentInfo.getClassName();
		Log.d(TAG,"packageName = "+packageName);
		Log.d(TAG,"className = "+className);

		String packageName_mTvManager = mTvManager.getCurAPK();
		Log.d(TAG,"packageName_mTvManager = "+ packageName_mTvManager);
		if(!(className.equals("com.bestv.ctv.activity.CTVDetailActivity")&&packageName_mTvManager.equals("com.bestv.ctv"))){
		Intent homeKeyIntent = new Intent();
		homeKeyIntent.setAction("com.haier.home.broadcast");
		sendBroadcast(homeKeyIntent);
        
        final String str_status = SystemProperties.get("rtk.str.suspending", STR_STATUS_FIRST_POWERON);

        Log.i(TAG, "==========onResume start===========str_status:" + str_status);
       
        //if (STR_STATUS_NONE.equals(str_status)) {
        	
//        	Log.d(TAG, "---- mstar.str.suspending == 0, onResume connect pictureSkin ----");
        	
/*            if(pictureSkin == null){
            	pictureSkin = new PictureSkin(this);
                pictureSkin.connect(null);
            }*/
        //}

        if (m_tvWaittingForChange){
        	m_tvNeedChangeSource = false;
        	m_tvNeedChangeWindow = false;
        }
        m_tvNeedDelayChangeSource = true;
        
        //setKeyAudio();
        
		if(PowerOn == true && STR_STATUS_FIRST_POWERON.equals(str_status)) {

            //mHandler.sendEmptyMessageDelayed(4444,50);
			PowerOn = false;
			SystemProperties.set("rtk.str.suspending", STR_STATUS_NONE);
			//Log.e(TAG,"mTvManager.getCurSourceType() = "+mTvManager.getCurSourceType());
	        mTvManager.setSource((int)mTvManager.getCurSourceType());
	    
			startTVRootActivity();
	        
			
            mPaused = false;
	        if (mRestoring || mOnResumeNeedsLoad) {
	            mWorkspaceLoading = true;
	            mModel.startLoader(this, true);
	            mRestoring = false;
	            mOnResumeNeedsLoad = false;
	        }
	        if (mWaitingForResume != null) {
	            mWaitingForResume.setStayPressed(false);
	        }
	        // When we resume Launcher, a different Activity might be responsible for the app
	        // market intent, so refresh the icon
	        updateAppMarketIcon();
	        mAppsCustomizeTabHost.onResume();
		} else {
			if(!STR_STATUS_NONE.equals(str_status)) {
				SystemProperties.set("rtk.str.suspending", STR_STATUS_NONE);
			}
			if(PowerOn) {
				PowerOn = false;
			}
			if (STR_STATUS_NONE.equals(str_status)) {
				
//zgzs				Log.d(TAG, "---- PowerOn=false --- mstar.str.suspending == 0");
	            mcontent.setVisibility(View.VISIBLE);
	            updateWallpaperVisibility(true);

				// send Broadcast
				//Intent intent = new Intent();
				//intent.setAction("com.haier.launcher.onresume");
				//this.sendBroadcast(intent);

				if (FirstSetTvWindow) {
					mDragLayer.setVisibility(View.VISIBLE);
					FirstSetTvWindow = false;
					m_bOnPauseByBackgroundActivity = true;
				}

				mPaused = false;
				if (mRestoring || mOnResumeNeedsLoad) {
					mWorkspaceLoading = true;
					mModel.startLoader(this, true);
					mRestoring = false;
					mOnResumeNeedsLoad = false;
				}
				if (mWaitingForResume != null) {
					mWaitingForResume.setStayPressed(false);
				}

				// When we resume Launcher, a different Activity might be responsible for the app
				// market intent, so refresh the icon
				updateAppMarketIcon();
				mAppsCustomizeTabHost.onResume();

				mHandler.postDelayed(tv_OnResumeChangeSourceRunnable, 500);
				if (mState != State.WORKSPACE) {
					showTvWindow();
				} else if (mWorkspace.getCurrentPage() == DEFAULT_SCREEN && mState == State.WORKSPACE) {
					if (mDelayChangeTvWindow) {
						setupTvSmallWindowBg(1000, true);
					} else {
						showTvWindow();
					}
				}
			}
			else if (STR_STATUS_WAKEUP.equals(str_status)) {
				
//zgzs			Log.d("zjf", "---- PowerOn=false --- mstar.str.suspending == 2");
//				Log.i("zjf", "---- PowerOn=false --- mstar.str.suspending == 2.time:" + System.currentTimeMillis() / 1000);
				
				//以防STR后睡眠时间没有设置为关				
				Intent bootIntent = new Intent();
				bootIntent.setAction("android.intent.action.STR_STATUS_WAKEUP");
				sendBroadcast(bootIntent);
				
				m_tvNeedDelayChangeSource = false;
				
				try {
					int isAutoDateTime = Settings.System.getInt(getContentResolver(), Settings.System.AUTO_TIME);
					
//zgzs					Log.d("zjf", "---- PowerOn=false --- mstar.str.suspending == 2---isAutoDateTime:" + isAutoDateTime);

					if (isAutoDateTime == 1) {
						Settings.System.putInt(getContentResolver(), Settings.System.AUTO_TIME, 0);
						Settings.System.putInt(getContentResolver(), Settings.System.AUTO_TIME, 1);
					}
				} catch (SettingNotFoundException e) {
//					Log.d("zjf", "---- PowerOn=false --- mstar.str.suspending == 2---have error:" + e.getLocalizedMessage());
					e.printStackTrace();
				}
	//zgzs			
				/*ClearTVManager_RestorSTR();
				SetPropertyForSTR("0");*/
				
				//设置一个STR临时变量，用于控制STR待机后My Fun是否提示。此处设置不显示。
				writeStringToFile("MY_STR_STATE", Global.STR_STATE_PATH);
				
				boolean content = isFileExist(Global.STR_STATE_PATH);
//zgzs				Log.i("zjf", "---- PowerOn=false --- mstar.str.suspending == 2---content:" + content);
				
//		    	pictureSkin = new PictureSkin(this);
//		        pictureSkin.connect(null);
		        
//		        commonSkin = new CommonSkin(this);
//		        commonSkin.connect(null);
		        
//		        channelSkin = new ChannelSkin(this);
//		        channelSkin.connect(null);
				startTVRootActivity();
	            updateWallpaperVisibility(false);
			}
		
			initRtkAtvApp();
			
		}

        if (!mWorkspaceLoading) {
            final ViewTreeObserver observer = mWorkspace.getViewTreeObserver();
            final Workspace workspace = mWorkspace;
            // We want to let Launcher draw itself at least once before we force it to build
            // layers on all the workspace pages, so that transitioning to Launcher from other
            // apps is nice and speedy. Usually the first call to preDraw doesn't correspond to
            // a true draw so we wait until the second preDraw call to be safe
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                boolean mFirstTime = true;
                public boolean onPreDraw() {
                    if (mFirstTime) {
                        mFirstTime = false;
                    } else {
                        workspace.post(mBuildLayersRunnable);
                        observer.removeOnPreDrawListener(this);
                    }
                    return true;
                }
            });
        }
        clearTypedText();
		}
    }
    
	/**
	 * 把字符串写入文本中
	 * 
	 * @param fileName
	 *            生成的文件绝对路径
	 * @param content
	 *            文件要保存的内容
	 * @param enc
	 *            文件编码
	 * @return
	 */
	public static boolean writeStringToFile(String content,String path) {
		Log.d(TAG, "Launcher.writeStringToFile.content:" + content);
		File file = new File(path);
		try {
			if (file.isFile()) {
				file.deleteOnExit();
				file = new File(file.getAbsolutePath());
			}
			OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
			os.write(content);
			os.close();
		} catch (Exception e) {
			//e.printStackTrace();
			Log.d(TAG, "Launcher.writeStringToFile.Exception:" + e.getLocalizedMessage());
			return false;
		}
		return true;
	}
	
	public boolean isFileExist(String prth) {
		File file = new File(prth);
		return file.exists();
	}
    
/*	private void ClearTVManager_RestorSTR() {
		try {
			TvManager tvmanager = TvManager.getListenerHandle();
			tvmanager.finalizeAllManager();
		} catch (Throwable e1) {
			e1.printStackTrace();
		}
	}*/

/*	private void SetPropertyForSTR(String value) {
		IWindowManager winService = IWindowManager.Stub
				.asInterface(ServiceManager.checkService(Context.WINDOW_SERVICE));
		if (winService == null) {
			Log.w(TAG, "Unable to find IWindowManger interface.");
		} else {
			try {
				winService.setSystemProperties("mstar.str.suspending", value);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}*/
    
    public static boolean isInWorkSpace(){
    	return (mState == State.WORKSPACE)?true:false;
    }
    
	private int queryCurInputSrc() {
		int value = 0;
		Cursor cursor = getContentResolver().query(Uri.parse("content://hrtvbic.tv.usersetting/systemsetting"), null,
				null, null, null);
		if (cursor.moveToFirst()) {
			value = cursor.getInt(cursor.getColumnIndex("enInputSourceType"));
		}
		cursor.close();
		return value;
	}

	// *** add by zhoujf 2012-02-15***
	private void changeInputSourceThread() throws InterruptedException {
		
		Log.i(TAG, "Into changeInputSourceThread()");
		
		if (onResumeChangeInputSourceThread != null && onResumeChangeInputSourceThread.isAlive()){
			Log.i(TAG, "=====onResumeChangeInputSourceThread is live , wait,  time="+System.currentTimeMillis());
			onResumeChangeInputSourceThread.join();
			Log.i(TAG, "=====onResumeChangeInputSourceThread is live , join end, will return. time="+System.currentTimeMillis());
			
			return;
		}
		
		mDelayChangeTvWindow = false;
		m_tvIsSmallWindows = false;
		setupTvSmallWindow(true);
		//"changeinputsource"
		onResumeChangeInputSourceThread = new Thread() {
			
			@Override
			public void run() {

				Log.i(TAG, "Into onResumeChangeInputSourceThread,  time="+System.currentTimeMillis());
				
//zgzs				EN_INPUT_SOURCE_TYPE currentInputSourceTemp = commonSkin.GetCurrentInputSource();
//				Log.i(TAG,"*****Launcher.changeInputSourceThread()*****:currentInputSourceTemp:"+ currentInputSourceTemp);
				
				
		//		mDelayChangeTvWindow = false;
		//		m_tvIsSmallWindows = false;
		//		setupTvSmallWindow(true);
				
				
/*				if (currentInputSourceTemp.equals(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_STORAGE)) {
					//setupTvSmallWindow(false);
					
					mDelayChangeTvWindow = true;
					Log.i(TAG,"*****Launcher.changeInputSourceThread()*****Storage.........");
					
				    //int i = commonSkin.getInputSrcFromDB();
				    int i = queryCurInputSrc();
				    Log.i(TAG, "========i="+i);
					
				    // ATV
					if (i == 1) {
						Log.i(TAG,"*****Launcher.changeInputSourceThread()*****ATV.........");
						commonSkin.SetInputSource(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_ATV);
						startATV();
					}else{
						Log.i(TAG,"*****Launcher.changeInputSourceThread()*****else.........");
						commonSkin.SetInputSource(EN_INPUT_SOURCE_TYPE.values()[i]);
					}
					
					m_tvIsSmallWindows = false;
					setupTvSmallWindow(true);
				} else {
					
					Log.i(TAG,"*****Launcher.changeInputSourceThread()*****Storage.........");
					
					if (mBroadcastWaittingForChangeWindow){
						m_tvIsSmallWindows = true;
						mBroadcastSetBigWindow = false;
					}else {
						mBroadcastWaittingForChangeWindow = false;
						setupTvSmallWindow(true);
					}
				}
				mHandler.removeMessages(TV_WINDOW_MSG);
		        Message msg = mHandler.obtainMessage(TV_WINDOW_MSG);
				mHandler.sendMessage(msg);
				Log.i(TAG, "Out onResumeChangeInputSourceThread,  time="+System.currentTimeMillis());*/
			}
		};
		
		while ((onPauseChangeTvSourceToStoragThread != null) && (onPauseChangeTvSourceToStoragThread.isAlive())){
			Log.i(TAG, "====onPauseChangeTvSourceToStoragThread is live ,OnResume wait, time="+System.currentTimeMillis());
			onPauseChangeTvSourceToStoragThread.join();
			Log.i(TAG, "====onPauseChangeTvSourceToStoragThread is live ,OnResume Join end, time="+System.currentTimeMillis());
		}
		
		onResumeChangeInputSourceThread.start();
	}   

    /**
     * 启动ATV
     */
	private void startATV() {
		/*
		int u32Number = 0;
		ChannelManager cm = TvManager.getChannelManager();
		try {
			u32Number = cm.getCurrChannelNumber();
			if (u32Number > max_atv_count || u32Number < 0) {
				u32Number = 0;
			}

		} catch (TvCommonException e) {
			e.printStackTrace();
		}

		try {
			cm.selectProgram(u32Number, (short) 0, 0x00);//
		} catch (TvCommonException e) {
			e.printStackTrace();
		}
		
		channelSkin.programSel(channelSkin.getCurrentChannelNumber(),EN_MEMBER_SERVICE_TYPE.E_SERVICETYPE_ATV);*/
	}
	
	protected void onStop(){
		super.onStop();
		Log.i(TAG, "==========onStop=========");
	   // Log.d("shangbaorun","shangbaorun-===============test=========shangbaorun====onStop");
	   // mTvManager.setLauncherReStartTimer(true);
		setupTvBigWindow(true);
		CloseTvWindow(false);
		KillTVWindowOnStop();
		
		/* [2012-05-09] add by weijh 启动其它应用后把播放器释放掉  */
		try {
			if (null != mMediaPlayer) {
				mMediaPlayer.reset();
				mMediaPlayer.release();
				mMediaPlayer = null;
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
			mMediaPlayer = null;
		}
	}
	
	private boolean isCounterShowing = false;
    @Override
	protected void onPause() {
		super.onPause();
		
		CloseTvWindow(true);
		final String str_status = SystemProperties.get("rtk.str.suspending", STR_STATUS_NONE);
		Log.i(TAG, "==========onPause start===========STR_STATUS:" + str_status);
		mPaused = true;
		mDragController.cancelDrag();
		Log.i(TAG, "==========onPause===== isCounterShowing = " + isCounterShowing);
		Log.i(TAG, "==========onPause===== mShowTvBg = " + mShowTvBg);
		Log.i(TAG, "==========onPause===== m_tvNeedDelayChangeSource = " + m_tvNeedDelayChangeSource);
		Log.i(TAG, "==========onPause===== FirstSetTvWindow = " + FirstSetTvWindow);
		Log.i(TAG, "==========onPause===== m_tvNeedChangeWindow = " + m_tvNeedChangeWindow);
		
		if (STR_STATUS_SUSPENDING.equals(str_status) || STR_STATUS_WAKEUP.equals(str_status)) {
			
//zgzs			Log.d(TAG, "---- mstar.str.suspending == 1/2, onPasue do nothing ----");
			mcontent.setVisibility(View.INVISIBLE);
			updateWallpaperVisibility(false);
			
		} else if (STR_STATUS_NONE.equals(str_status)) {

//			Log.d(TAG, "---- mstar.str.suspending == 0, onPasue normal ----");
			
			if(!isCounterShowing){
				
				if (mShowTvBg){
					ShowDefaultTvScreen();
					mShowTvBg = false;
				}
				
	            mHandler.removeCallbacks(tv_OnResumeChangeSourceRunnable);
	            
				/*if (m_tvNeedDelayChangeSource && !FirstSetTvWindow){
					Log.i(TAG, "==========onPause=====  1");
					if (!m_tvWaittingForChange){
						m_tvNeedChangeSource = true;
						m_tvNeedChangeWindow = true;
						if (m_bOnPauseByBackgroundActivity){
							mHandler.postDelayed(m_tvDelayChangeSourceRunnale, 300);
							m_bOnPauseByBackgroundActivity = false;
						}else {
							mHandler.postDelayed(m_tvDelayChangeSourceRunnale, 100);
						}
						m_tvWaittingForChange = true;
					}
				}else {*/
					Log.i(TAG, "==========onPause=====  2");
					mBroadcastWaittingForChangeWindow = false;
					m_tvNeedDelayChangeSource = true;
					if (!FirstSetTvWindow){
						if (m_tvNeedChangeWindow){
							setupTvBigWindow(true);
						}else {
							m_tvNeedChangeWindow = true;
						}
					}
				//}
			}
		}
		Log.i(TAG, "==========onPause=====  end");
	}

	@Override
    public Object onRetainNonConfigurationInstance() {
        // Flag the loader to stop early before switching
        mModel.stopLoader();
        if (mAppsCustomizeContent != null) {
            mAppsCustomizeContent.surrender();
        }
        return Boolean.TRUE;
    }

    // We can't hide the IME if it was forced open.  So don't bother
    /*
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            final InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            inputManager.hideSoftInputFromWindow(lp.token, 0, new android.os.ResultReceiver(new
                        android.os.Handler()) {
                        protected void onReceiveResult(int resultCode, Bundle resultData) {
                            Log.d(TAG, "ResultReceiver got resultCode=" + resultCode);
                        }
                    });
            Log.d(TAG, "called hideSoftInputFromWindow from onWindowFocusChanged");
        }
    }
    */

    private boolean acceptFilter() {
        final InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        return !inputManager.isFullscreenMode();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	
    	System.out.println("$$$$$$_Launcher.onKeyDown.keyCode:" + keyCode);
    	System.out.println("$$$$$$_Launcher.onKeyDown.event.getAction():" + event.getAction());
    	System.out.println("$$$$$$_Launcher.onKeyDown.mWorkspace.getCurrentPage():" + mWorkspace.getCurrentPage());
    	
    	if (keyCode == KeyEvent.KEYCODE_1) {
    		tv_DefaultScreen.getVisibility();
    		Log.d(TAG, "tv_DefaultScreen.getVisibility() = " + tv_DefaultScreen.getVisibility());
    		Log.d(TAG, "tv_SurfaceView.getVisibility() = " + tv_SurfaceView.getVisibility());
    		Log.d(TAG, "m_tvIsSmallWindows = " + m_tvIsSmallWindows);
    		Log.d(TAG, "mShowTvBg = " + mShowTvBg);
    		Log.d(TAG, "tv_bShowTvWindow = " + tv_bShowTvWindow);
    		
    		/*Log.d(TAG, "videoWindowType.height = " + videoWindowType.height);
    		Log.d(TAG, "videoWindowType.width = " + videoWindowType.width);
    		Log.d(TAG, "videoWindowType.x = " + videoWindowType.x);
    		Log.d(TAG, "videoWindowType.y = " + videoWindowType.y);*/
    		
    		Log.d(TAG, "focus = " + getCurrentFocus());
    		Log.d(TAG, "mBroadcastWaittingForChangeWindow = " + mBroadcastWaittingForChangeWindow);	
    	}
    	
    	if ((keyCode == KeyEvent.KEYCODE_BACK) && (mWorkspace.getCurrentPage() == 1)
    			&& (mWorkspace.getOpenFolder() == null) && (mState == State.WORKSPACE)){
        	return true;
        }
    	if (mWorkspace.mIsMoving){
    		return true;
    	}
    	if ((keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) && (mWorkspace.getCurrentPage() == 0) && (mState == State.WORKSPACE)
    			&& (((CellLayout)mWorkspace.getChildAt(0)).getChildrenLayout().getChildCount() == 0)){
    		FocusHelper.MoveToDefaultScreenWhenFirstScreenIsEmpty(mWorkspace);
    		playTone();
        	return true;
        }
    	if (keyCode == KeyEvent.KEYCODE_TV_INPUT){
    		if (m_tvIsSmallWindows){
				GotoTVServer();
    		}
			return true;
			//zgzs
 //   	}else if (keyCode == KeyEvent.KEYCODE_HAIER_USB){
    	}else if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK || keyCode == KeyEvent.KEYCODE_HAIER_USB ){	
    		if (m_tvIsSmallWindows){
	    		tv_DefaultScreen.setVisibility(View.VISIBLE);
				m_tvNeedDelayChangeSource = false;
				Intent intent = new Intent("com.hrtvbic.usb.S6A801.intent.action.ui.main.MainActivity");
				startActivity(intent);
    		}
			return true;
    	}
    	
        final int uniChar = event.getUnicodeChar();
        final boolean handled = super.onKeyDown(keyCode, event);
        final boolean isKeyNotWhitespace = uniChar > 0 && !Character.isWhitespace(uniChar);
        if (!handled && acceptFilter() && isKeyNotWhitespace) {
            boolean gotKey = TextKeyListener.getInstance().onKeyDown(mWorkspace, mDefaultKeySsb,
                    keyCode, event);
            if (gotKey && mDefaultKeySsb != null && mDefaultKeySsb.length() > 0) {
                // something usable has been typed - start a search
                // the typed text will be retrieved and cleared by
                // showSearchDialog()
                // If there are multiple keystrokes before the search dialog takes focus,
                // onSearchRequested() will be called for every keystroke,
                // but it is idempotent, so it's fine.
                //return onSearchRequested();
            }
        }
        
        
        /* 处理鼠标点开文件夹，再用遥控去操作时，文件夹没有关闭的问题 */
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ||
        		keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_DPAD_UP) {
        	View v = getCurrentFocus();
        	if (v instanceof Folder) {
        		closeFolder();
        		return true;
        	}
        	/*if(mWorkspace.getCurrentPage() == 0||mWorkspace.getCurrentPage() == 2){
        		
            	if(mTvManager.getPanelOn())
            	{
            	Log.d(TAG, "Panel is currently off, turn it back to on!!");
            	mTvManager.setPanelOn(false);
            	}
            	} */    	
        }
        
        
//		if (keyCode == KeyEvent.KEYCODE_ENTER) {
//			
//			System.out.println("$$$$$$_Launcher.onKeyDown.keyCode == KeyEvent.KEYCODE_ENTER ......");
//			
//			startWeatherComponent(Launcher.this);
//		}
		
		if (((keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_PAGE_UP) && mWorkspace.getCurrentPage() == 2) || 
			(keyCode == KeyEvent.KEYCODE_PAGE_DOWN && mWorkspace.getCurrentPage() == 0)) {

			System.out
					.println("$$$$$$_Launcher.keyCode == KeyEvent.KEYCODE_DPAD_LEFT && mWorkspace.getCurrentPage() == 2......");

			Cursor cursor = LauncherProvider.queryForLauncher();

			System.out.println("$$$$$$_Launcher.onKeyDown_cursor:" + cursor);
			System.out.println("$$$$$$_Launcher.onKeyDown_cursor.moveToNext():" + cursor.moveToNext());

			if (!cursor.moveToNext()) {
				ComponentName comp = new ComponentName("com.haier.launcher", "com.haier.launcher2.Launcher");
				Intent intent = new Intent();
				intent.setComponent(comp);
				intent.setAction("android.intent.action.MAIN");
				startActivity(intent);
			}
		}

        // Eat the long press event so the keyboard doesn't come up.
        if (keyCode == KeyEvent.KEYCODE_MENU && event.isLongPress()) {
            return true;
        }
        
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
        	return true;
        }else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP){
        	return true;
        }else if ((keyCode == KeyEvent.KEYCODE_PAGE_UP)){
        	CellLayout layout = (CellLayout) mWorkspace.getChildAt(mWorkspace.getCurrentPage());
        	View v = FocusHelper.findCurrFocusViews(layout,(ViewGroup)layout.getChildAt(0));
        	if ((v != null) && (v instanceof AppWidgetHostView)){
        		AppWidgetHostView appWidgetHostView = (AppWidgetHostView)v;
        		if (appWidgetHostView.getFocusedChild() != null){
        			FocusHelper.PageUpWhenFocusWidgetChildren(mWorkspace,(CellLayout)mWorkspace.getChildAt(mWorkspace.getCurrentPage()),mWorkspace.getCurrentPage());
        			playTone();
        			return true;
        		}
        	}
        }else if ((keyCode == KeyEvent.KEYCODE_PAGE_DOWN)){
        	CellLayout layout = (CellLayout) mWorkspace.getChildAt(mWorkspace.getCurrentPage());
        	View v = FocusHelper.findCurrFocusViews(layout,(ViewGroup)layout.getChildAt(0));
        	if ((v != null) && (v instanceof AppWidgetHostView)){
        		AppWidgetHostView appWidgetHostView = (AppWidgetHostView)v;
        		if (appWidgetHostView.getFocusedChild() != null){
        			FocusHelper.PageDownWhenFocusWidgetChildren(mWorkspace,(CellLayout)mWorkspace.getChildAt(mWorkspace.getCurrentPage()),mWorkspace.getCurrentPage());
        			playTone();
        			return true;
        		}
        	}
        }
        playTone();
        return handled;
    }

	public static void startWeatherComponent(Context context) {

		System.out.println("$$$$$$_Launcher.startWeatherComponent......");

		ComponentName comp = new ComponentName("com.haier.weather.activity",
				"com.haier.weather.activity.LoadingActivity");
		Intent intent = new Intent();
		intent.setComponent(comp);
		intent.setAction("android.intent.action.MAIN");
		Launcher.mstarTvChangeSource("com.haier.weather.activity");
		context.startActivity(intent);
	}

    private String getTypedText() {
        return mDefaultKeySsb.toString();
    }

    private void clearTypedText() {
        mDefaultKeySsb.clear();
        mDefaultKeySsb.clearSpans();
        Selection.setSelection(mDefaultKeySsb, 0);
    }

    /**
     * Given the integer (ordinal) value of a State enum instance, convert it to a variable of type
     * State
     */
    private static State intToState(int stateOrdinal) {
        State state = State.WORKSPACE;
        final State[] stateValues = State.values();
        for (int i = 0; i < stateValues.length; i++) {
            if (stateValues[i].ordinal() == stateOrdinal) {
                state = stateValues[i];
                break;
            }
        }
        return state;
    }

    /**
     * Restores the previous state, if it exists.
     *
     * @param savedState The previous state.
     */
    private void restoreState(Bundle savedState) {
        if (savedState == null) {
            return;
        }

        State state = intToState(savedState.getInt(RUNTIME_STATE, State.WORKSPACE.ordinal()));
        if (state == State.APPS_CUSTOMIZE) {
            showAllApps(false);
        }

        final int currentScreen = savedState.getInt(RUNTIME_STATE_CURRENT_SCREEN, -1);
        if (currentScreen > -1) {
            mWorkspace.setCurrentPage(currentScreen);
        }

        final long pendingAddContainer = savedState.getLong(RUNTIME_STATE_PENDING_ADD_CONTAINER, -1);
        final int pendingAddScreen = savedState.getInt(RUNTIME_STATE_PENDING_ADD_SCREEN, -1);

        if (pendingAddContainer != ItemInfo.NO_ID && pendingAddScreen > -1) {
            mPendingAddInfo.container = pendingAddContainer;
            mPendingAddInfo.screen = pendingAddScreen;
            mPendingAddInfo.cellX = savedState.getInt(RUNTIME_STATE_PENDING_ADD_CELL_X);
            mPendingAddInfo.cellY = savedState.getInt(RUNTIME_STATE_PENDING_ADD_CELL_Y);
            mRestoring = true;
        }

        boolean renameFolder = savedState.getBoolean(RUNTIME_STATE_PENDING_FOLDER_RENAME, false);
        if (renameFolder) {
            long id = savedState.getLong(RUNTIME_STATE_PENDING_FOLDER_RENAME_ID);
            mFolderInfo = mModel.getFolderById(this, sFolders, id);
            mRestoring = true;
        }


        // Restore the AppsCustomize tab
        if (mAppsCustomizeTabHost != null) {
            String curTab = savedState.getString("apps_customize_currentTab");
            if (curTab != null) {
                // We set this directly so that there is no delay before the tab is set
                mAppsCustomizeContent.setContentType(
                        mAppsCustomizeTabHost.getContentTypeForTabTag(curTab));
                mAppsCustomizeTabHost.setCurrentTabByTag(curTab);
                mAppsCustomizeContent.loadAssociatedPages(
                        mAppsCustomizeContent.getCurrentPage());
            }

            int currentIndex = savedState.getInt("apps_customize_currentIndex");
            mAppsCustomizeContent.restorePageForIndex(currentIndex);
        }
    }
    
    private void setupTvViews() {
    	
    	CellLayout child1 = (CellLayout) mWorkspace.getChildAt(DEFAULT_SCREEN);
    	View preview = mInflater.inflate(R.layout.preview, null);
    	child1.addView(preview);
    	
    	image_view = (ImageView)findViewById(R.id.image_view);
    	tv_SurfaceView = (SurfaceView)findViewById(R.id.Surface_view);
	//	image_view.setBackgroundColor(Color.BLACK);
    	
		tv_SurfaceView.getHolder().addCallback(callback);
		preview.setVisibility(View.INVISIBLE);
		
		tv_SurfaceView.setVisibility(View.INVISIBLE);
		//m_tvIsSmallWindows = false;
			        
		View tv_bg = mInflater.inflate(R.layout.tv_small_window_bg, null);
		child1.addView(tv_bg);
		
        //tv_bg.setVisibility(View.GONE);
		tv_DefaultScreen = mInflater.inflate(R.layout.firstin, null);
		child1.addView(tv_DefaultScreen);
//		tv_DefaultScreen.setVisibility(View.GONE);
		tv_DefaultScreen.setVisibility(View.VISIBLE);
		m_tvWaittingForChange = false;
		m_tvNeedChangeSource = false;
		m_tvNeedDelayChangeSource = true;
		m_tvNeedChangeWindow = true;
		
		mBroadcastSetBigWindow = true;
		
    }    
    private Callback callback = new Callback() {		
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			Log.d("DemoSurfaceView", "surfaceDestroyed");			
		}
		
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			
			Log.d("DemoSurfaceView", "surfaceCreated");
			
			if(holder.getSurface().isValid() == false){
          		return;
			}
			
//		play();
		//zgzs
/*mTvManager.setSourceAndDisplayWindow(mTvManager.getCurLiveSource(),52,232,358,200);
mTvManager.setVideoAreaOn(52,232,358,200,1);*/
		
		/*	try
	        {
				TvPlayer mtvplayer = TvManager.getPlayerManager();
				mtvplayer.setDisplay(holder);
	        }
	        catch (TvCommonException e)
	        {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
	        }

			//set window size 
			videoWindowType = new VideoWindowType();*/
			
		}
		
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// TODO Auto-generated method stub
			
		}
	};
	
	private Runnable tv_sWindowRunnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (tv_bShowTvWindow) {
				if (mWorkspace.getCurrentPage() == DEFAULT_SCREEN && mState == State.WORKSPACE) {
					showTvWindow();
				}
			} else {
				showTvBg(true,false);
			}
			mHandler.removeCallbacks(tv_sWindowRunnable);
		}
	};
		
	private Runnable m_tvDelayChangeSourceRunnale = new Runnable() {
		
		@Override
		public void run() {

			if (m_tvWaittingForChange){
				if (m_tvNeedChangeWindow){
					Log.i(TAG,"Comehere m_tvNeedChangeWindow!!!!");
					if (m_tvNeedChangeSource){
						setupTvBigWindow(false);
					} else {
						setupTvBigWindow(true);
					}
					mBroadcastWaittingForChangeWindow = false;
				}else {
					m_tvNeedChangeWindow = true;
				}
				if (m_tvNeedChangeSource){
					m_tvNeedChangeSource = false;
					try {
						LauncherChangeTvSourceToStorage();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				m_tvWaittingForChange = false;
				mHandler.removeCallbacks(m_tvDelayChangeSourceRunnale);
			}
		}
	};
	
    
	private Runnable tv_OnResumeChangeSourceRunnable = new Runnable() {
		
		@Override
		public void run() {
			try {
 				changeInputSourceThread();
 			} catch (InterruptedException e) {
 				e.printStackTrace();
 				HideDefaultTvScreen();
 			}
			mHandler.removeCallbacks(tv_OnResumeChangeSourceRunnable);
		}
	};

	
	private void setupTvSmallWindow(boolean bScale) {

		if (!m_tvIsSmallWindows) {
			Log.i(TAG, "=============set small window==============");
			
			/*if (videoWindowType == null) {
				videoWindowType = new VideoWindowType();
			}

			videoWindowType.height = getResources().getInteger(R.integer.videoWindowType_height);
			videoWindowType.width = getResources().getInteger(R.integer.videoWindowType_width);
			videoWindowType.x = getResources().getInteger(R.integer.videoWindowType_x);
			videoWindowType.y = getResources().getInteger(R.integer.videoWindowType_y);*/

			setTvManage(bScale);
			
			m_tvIsSmallWindows = true;
		}

	}
	/**********************************************************************
	 * WBL add for TV small window.
	 *********************************************************************/
	public void initRtkAtvView() {
		mRtkAtvView = (RtkAtvViewEx) findViewById(R.id.atv_surfaceView);
		mRtkAtvView.setAtvPath();
	}
	
	public void startTVRootActivity() {
		ComponentName comp = new ComponentName("hrtvbic.tvsetting.ui", "hrtvbic.tvsetting.ui.RootActivity");
		Intent intent = new Intent();
		intent.setComponent(comp);
		intent.setAction("android.intent.action.MAIN");
		startActivitySafely(intent, "HaierTv");
	}
	
	public void startMyFunRootActivity() {
		ComponentName comp = new ComponentName("com.hrtvbic.usb.S6A801", "com.hrtvbic.usb.S6A801.ui.main.MainActivity");
		Intent intent = new Intent();
		intent.setComponent(comp);
		intent.setAction("android.intent.action.MAIN");
		startActivitySafely(intent, "MyFun");
	}

	public void GotoTVServer() {
		int mAspectRatio = 0;
		long mCurSourceType = mTvManager.getCurSourceType();
		
		mShowTvBg = true;
		tv_DefaultScreen.setVisibility(View.GONE);
		m_tvNeedDelayChangeSource = false;
		bGotoTvServer = true;
		setupTvBigWindow(false);
		
		mCurLiveSource = mTvManager.getCurLiveSource();
		if(mCurSourceType == TvManager.SOURCE_PLAYBACK
				||mCurSourceType == TvManager.SOURCE_OSD) {
			//mTvManager.setSource(mCurLiveSource);
		} else {
			mAspectRatio = mTvManager.getAspectRatio(mCurLiveSource);
			mTvManager.setAspectRatio(mAspectRatio);
		}
		startTVRootActivity();
	}
	
	public void KillTVWindowOnStop() {
		int mAspectRatio = 0;
		long mCurSourceType = mTvManager.getCurSourceType();
		
		mShowTvBg = true;
		tv_DefaultScreen.setVisibility(View.GONE);
		m_tvNeedDelayChangeSource = false;
		bGotoTvServer = true;
		setupTvBigWindow(false);
		
		mCurLiveSource = mTvManager.getCurLiveSource();
		if(mCurSourceType == TvManager.SOURCE_PLAYBACK
				||mCurSourceType == TvManager.SOURCE_OSD) {
			//mTvManager.setSource(mCurLiveSource);
		} else {
			mAspectRatio = mTvManager.getAspectRatio(mCurLiveSource);
			mTvManager.setAspectRatio(mAspectRatio);
		}
	}
	
	public void GoToTVNormalWindow(boolean bNormal,boolean bInWorkSpace) {
		if(bNormal) {
			if(bGotoTvServer) {
				bGotoTvServer = false;
			} else if(!bInWorkSpace){
				//mTvManager.setSource(TvManager.SOURCE_OSD);
				//mTvManager.setSource(TvManager.SOURCE_PLAYBACK);
			}
		} else {
			mTvManager.setDisplayWindow(75, 251, 504, 284);
		}
	}
	
	public void CloseTvWindow(boolean bInWorkSpace) {
		if(!bRtkAtvViewOpen) {
			Log.i(TAG, "========TvWindow is closed,don't re-close it.=======");
			return;
		}
		Log.i(TAG, "=============CloseTvWindow==============");
		bRtkAtvViewOpen = false;
		if(bInWorkSpace) {
			mRtkAtvView.setVisibility(View.GONE);
		}
		GoToTVNormalWindow(true,bInWorkSpace);
	}
	
	public void OpenTvWindow() {
		if( mTvManager.getCurSourceType() == TvManager.SOURCE_PLAYBACK
		|| mTvManager.getCurSourceType() == TvManager.SOURCE_OSD) {
			return;
		}
		if(bRtkAtvViewOpen) {
			Log.i(TAG, "========TvWindow is on,don't re-open it.=======");
			return;
		}
		Log.i(TAG, "=============OpenTvWindow==============");
		bRtkAtvViewOpen = true;
		GoToTVNormalWindow(false,false);
		mRtkAtvView.setVisibility(View.VISIBLE);
		mRtkAtvView.setAtvPath();
	}
	/**********************************************************************
	 *********************************************************************/

	public void setupTvBigWindow(boolean bScale) {
		Log.i(TAG, "=============setupTvBigWindow ============== m_tvIsSmallWindows = " + m_tvIsSmallWindows);
		if (m_tvIsSmallWindows) {
			Log.i(TAG, "=============set big window==============");
			//zgzs
			/*if (videoWindowType == null) {
				videoWindowType = new VideoWindowType();
			}
			videoWindowType.height = 0xffff;
			videoWindowType.width = 0xffff;
			videoWindowType.x = 0xffff;
			videoWindowType.y = 0xffff;*/
			
			m_tvIsSmallWindows = false;
		}
	}
	
	public static void setupTvBigWindowByArgOne(Context context,boolean isSetLanguageStart,boolean isSetLanguageEnd,boolean isSetWallPaperEnd) {
		Log.i(TAG, "========setupTvBigWindowByArgOne==========");
		if (isSetLanguageEnd){
			mBroadcastSetBigWindow = true;
			mBroadcastWaittingForChangeWindow = true;
		}else if (isSetWallPaperEnd){
			if (mBroadcastWaittingForChangeWindow){
				Log.i(TAG, "============ set window ============");
				if(mBroadcastSetBigWindow){
					Log.i(TAG, "============ set big window ============");
					//zgzs
					/*videoWindowType.height = 0xffff;
					videoWindowType.width = 0xffff;
					videoWindowType.x = 0xffff;
					videoWindowType.y = 0xffff;*/
				}else {
					Log.i(TAG, "============ set small window ============");
					/*videoWindowType.height = context.getResources().getInteger(R.integer.videoWindowType_height);
					videoWindowType.width = context.getResources().getInteger(R.integer.videoWindowType_width);
					videoWindowType.x = context.getResources().getInteger(R.integer.videoWindowType_x);
					videoWindowType.y = context.getResources().getInteger(R.integer.videoWindowType_y);*/
				}

				/*try {
					TvManager.getPictureManager().selectWindow(EnumScalerWindow.E_MAIN_WINDOW);
					TvManager.getPictureManager().setDisplayWindow(videoWindowType);
					TvManager.getPictureManager().scaleWindow();
				} catch (TvCommonException e) {
					e.printStackTrace();
				}*/
				
				mBroadcastWaittingForChangeWindow = false;
				mBroadcastSetBigWindow = true;
			}
		}
		//以下代码可以保证设置壁纸的时候不闪TV画面
		else {
			Log.i(TAG, "============ isSetLanguageEnd and isSetWallPaperEnd is false ============");
			
			/*if (videoWindowType == null) {
				videoWindowType = new VideoWindowType();
			}
			if (pictureSkin == null){
				pictureSkin = new PictureSkin(null);
		        pictureSkin.connect(null);
			}
			videoWindowType.height = 1;
			videoWindowType.width = 1;
			videoWindowType.x = 0;
			videoWindowType.y = 0;
			
			try {
				TvManager.getPictureManager().selectWindow(EnumScalerWindow.E_MAIN_WINDOW);
				TvManager.getPictureManager().setDisplayWindow(videoWindowType);
				TvManager.getPictureManager().scaleWindow();
				
				Log.i(TAG, "==========set black bg===========");
				TvManager.setVideoMute(true, EnumScreenMuteType.E_BLACK, 0, TvManager.getCurrentInputSource());
			} catch (TvCommonException e) {
				e.printStackTrace();
			}*/
			
			mBroadcastWaittingForChangeWindow = true;
		}
	}
	
	private static void setTvManage(boolean bScale) {
		
		/*Log.i(TAG, "setTvManage.videoWindowType.height:" + videoWindowType.height);
		Log.i(TAG, "setTvManage.videoWindowType.width:" + videoWindowType.width);
		Log.i(TAG, "setTvManage.videoWindowType.x:" + videoWindowType.x);
		Log.i(TAG, "setTvManage.videoWindowType.y:" + videoWindowType.y);
		
		try {
			TvManager.getPictureManager().selectWindow(EnumScalerWindow.E_MAIN_WINDOW);
		    TvManager.getPictureManager().setDisplayWindow(videoWindowType);
			if (bScale) {
				Log.i(TAG, "========scaleWindow==========");
				TvManager.getPictureManager().scaleWindow();
			}
		} catch (TvCommonException e) {
			e.printStackTrace();
		}*/
	}
	
	public static void SetStorage(){
		Log.i(TAG, "=========SetStorage=========");
		try {
			LauncherChangeTvSourceToStorage();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void setupTvSmallWindowBg(int timer, boolean showTvFlag){
//		2.啟動計時器：
		tv_bShowTvWindow = showTvFlag;
		mHandler.postDelayed(tv_sWindowRunnable, timer);
//		3.停止計時器：
//		handler.removeCallbacks(runnable);
	}

	public void ShowDefaultTvScreen(){
		//tv_DefaultScreen.setVisibility(View.VISIBLE);
		setupTvSmallWindowBg(20, false);
	}
	
	public void HideDefaultTvScreen(){
		tv_DefaultScreen.setVisibility(View.GONE);
		if (mWorkspace.getCurrentPage() == DEFAULT_SCREEN && mState == State.WORKSPACE) {
			showTvWindow();
		}
	}
	
	public void showTvBg(boolean flag,boolean bInWorkSpace){
		Log.d(TAG, "--------------showTvBg---------- flag = " + flag);
		/*CellLayout child1 = (CellLayout) mWorkspace.getChildAt(DEFAULT_SCREEN);
		View tv_bg = child1.findViewById(R.id.tv_bg);
		tv_bg.setVisibility(View.VISIBLE);
		View tv_window = child1.findViewById(R.id.preview);
		tv_window.setVisibility(View.GONE);
		//child1.invalidate();
		tv_SurfaceView.setBackgroundColor(Color.BLACK);
		if (flag){
			tv_SurfaceView.setVisibility(View.GONE);
			image_view.setVisibility(View.VISIBLE);
		}*/
		if(flag) {
			CloseTvWindow(bInWorkSpace);
		}
	}
	
	public void showTvWindow(){
		Log.d(TAG, "--------------showTvWindow----------");
		if (PowerOn) {
			Log.d(TAG, "--------------showTvWindow:: !PowerOn return ----------");
			return;
		}
		if (!m_tvIsSmallWindows){
			Log.d(TAG, "--------------showTvWindow:: !m_tvIsSmallWindows return ----------");
			return;
		}
		//CellLayout child1 = (CellLayout) mWorkspace.getChildAt(DEFAULT_SCREEN);
		//View tv_window = child1.findViewById(R.id.preview);
		//tv_window.setVisibility(View.VISIBLE);
		
		//View tv_bg = child1.findViewById(R.id.tv_bg);
		//tv_bg.setVisibility(View.GONE);
		//child1.invalidate();
		//tv_SurfaceView.setBackgroundColor(Color.TRANSPARENT);
		//tv_SurfaceView.setVisibility(View.VISIBLE);
		//image_view.setVisibility(View.GONE);
		OpenTvWindow();
	}

	/**
     * Finds all the views we need and configure them properly.
     */
    private void setupViews() {
        final DragController dragController = mDragController;

        mDragLayer = (DragLayer) findViewById(R.id.drag_layer);
    	mDragLayer.setVisibility(View.INVISIBLE);
        mWorkspace = (Workspace) mDragLayer.findViewById(R.id.workspace);
        mQsbDivider = (ImageView) findViewById(R.id.qsb_divider);
        mDockDivider = (ImageView) findViewById(R.id.dock_divider);
        
    	dot_1 = (ImageView) findViewById(R.id.dot_1);
    	dot_2 = (ImageView) findViewById(R.id.dot_2);
    	dot_3 = (ImageView) findViewById(R.id.dot_3);
    	dot_2.setBackgroundResource(R.drawable.tips_red);

        // Setup the drag layer
        mDragLayer.setup(this, dragController);

        // Setup the hotseat
        mHotseat = (Hotseat) findViewById(R.id.hotseat);
        if (mHotseat != null) {
            mHotseat.setup(this);
        }

        // Setup the workspace
        mWorkspace.setHapticFeedbackEnabled(false);
        mWorkspace.setOnLongClickListener(this);
        mWorkspace.setup(dragController);
        dragController.addDragListener(mWorkspace);

        // Get the search/delete bar
        mSearchDropTargetBar = (SearchDropTargetBar) mDragLayer.findViewById(R.id.qsb_bar);

        // Setup AppsCustomize
        mAppsCustomizeTabHost = (AppsCustomizeTabHost)
                findViewById(R.id.apps_customize_pane);
        mAppsCustomizeContent = (AppsCustomizePagedView)
                mAppsCustomizeTabHost.findViewById(R.id.apps_customize_pane_content);
        mAppsCustomizeContent.setup(this, dragController);
        
        // Get the all apps button
        /* hidden allAppsButton 
        mAllAppsButton = findViewById(R.id.all_apps_button);
        if (mAllAppsButton != null) {
            mAllAppsButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
                        onTouchDownAllAppsButton(v);
                    }
                    return false;
                }
            });
        }
        */
        
        // Setup the drag controller (drop targets have to be added in reverse order in priority)
        dragController.setDragScoller(mWorkspace);
        dragController.setScrollView(mDragLayer);
        dragController.setMoveTarget(mWorkspace);
        dragController.addDropTarget(mWorkspace);
        if (mSearchDropTargetBar != null) {
            mSearchDropTargetBar.setup(this, dragController);
        }
        
        FocusHelper.setLauncher(this);
    }

    /**
     * Creates a view representing a shortcut.
     *
     * @param info The data structure describing the shortcut.
     *
     * @return A View inflated from R.layout.application.
     */
    View createShortcut(ShortcutInfo info) {
        return createShortcut(R.layout.application,
                (ViewGroup) mWorkspace.getChildAt(mWorkspace.getCurrentPage()), info);
    }

    /**
     * Creates a view representing a shortcut inflated from the specified resource.
     *
     * @param layoutResId The id of the XML layout used to create the shortcut.
     * @param parent The group the shortcut belongs to.
     * @param info The data structure describing the shortcut.
     *
     * @return A View inflated from layoutResId.
     */
    View createShortcut(int layoutResId, ViewGroup parent, ShortcutInfo info) {
        BubbleTextView favorite = (BubbleTextView) mInflater.inflate(layoutResId, parent, false);
        favorite.applyFromShortcutInfo(info, mIconCache);
        favorite.setOnClickListener(this);
        return favorite;
    }

    /**
     * Add an application shortcut to the workspace.
     *
     * @param data The intent describing the application.
     * @param cellInfo The position on screen where to create the shortcut.
     */
    void completeAddApplication(Intent data, long container, int screen, int cellX, int cellY) {
        final int[] cellXY = mTmpAddItemCellCoordinates;
        final CellLayout layout = getCellLayout(container, screen);

        // First we check if we already know the exact location where we want to add this item.
        if (cellX >= 0 && cellY >= 0) {
            cellXY[0] = cellX;
            cellXY[1] = cellY;
        } else if (!layout.findCellForSpan(cellXY, 1, 1)) {
            showOutOfSpaceMessage();
            return;
        }

        final ShortcutInfo info = mModel.getShortcutInfo(getPackageManager(), data, this);

        if (info != null) {
            info.setActivity(data.getComponent(), Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            info.container = ItemInfo.NO_ID;
            mWorkspace.addApplicationShortcut(info, layout, container, screen, cellXY[0], cellXY[1],
                    isWorkspaceLocked(), cellX, cellY);
        } else {
            Log.e(TAG, "Couldn't find ActivityInfo for selected application: " + data);
        }
    }

    /**
     * Add a shortcut to the workspace.
     *
     * @param data The intent describing the shortcut.
     * @param cellInfo The position on screen where to create the shortcut.
     */
    private void completeAddShortcut(Intent data, long container, int screen, int cellX,
            int cellY) {
        int[] cellXY = mTmpAddItemCellCoordinates;
        int[] touchXY = mPendingAddInfo.dropPos;
        CellLayout layout = getCellLayout(container, screen);

        boolean foundCellSpan = false;

        ShortcutInfo info = mModel.infoFromShortcutIntent(this, data, null);
        if (info == null) {
            return;
        }
        final View view = createShortcut(info);

        // First we check if we already know the exact location where we want to add this item.
        if (cellX >= 0 && cellY >= 0) {
            cellXY[0] = cellX;
            cellXY[1] = cellY;
            foundCellSpan = true;

            // If appropriate, either create a folder or add to an existing folder
            if (mWorkspace.createUserFolderIfNecessary(view, container, layout, cellXY,
                    true, null,null)) {
                return;
            }
            DragObject dragObject = new DragObject();
            dragObject.dragInfo = info;
            if (mWorkspace.addToExistingFolderIfNecessary(view, layout, cellXY, dragObject, true)) {
                return;
            }
        } else if (touchXY != null) {
            // when dragging and dropping, just find the closest free spot
            int[] result = layout.findNearestVacantArea(touchXY[0], touchXY[1], 1, 1, cellXY);
            foundCellSpan = (result != null);
        } else {
            foundCellSpan = layout.findCellForSpan(cellXY, 1, 1);
        }

        if (!foundCellSpan) {
            showOutOfSpaceMessage();
            return;
        }

        Log.i("zhoujf", "Launcher.completeAddShortcut(...) begin......");
        LauncherModel.addItemToDatabase(this, info, container, screen, cellXY[0], cellXY[1], false);
        Log.i("zhoujf", "Launcher.completeAddShortcut(...) end......");

        if (!mRestoring) {
            mWorkspace.addInScreen(view, container, screen, cellXY[0], cellXY[1], 1, 1,
                    isWorkspaceLocked());
        }
    }

	private void completeAddShortcutFromAppsPage(Intent data, long container,
			int screen) {
		boolean foundCellSpan = false;
		for (int i = screen; i < 3; i++) {
			CellLayout layout = getCellLayout(container, i);
			ShortcutInfo info = mModel.getShortcutInfo(getPackageManager(),
					data, this);
			if (info == null) {
				return;
			} else {
				info.setActivity(data.getComponent(), Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				info.container = ItemInfo.NO_ID;
			}
			final View view = createShortcut(info);

			int[] cellXY = mTmpAddItemCellCoordinates;
			foundCellSpan = layout.findCellForSpan(cellXY, 1, 1);

			if (!foundCellSpan) {
				continue;
			} else {
				Log.i("zhoujf", "Launcher.completeAddShortcutFromAppsPage(...) begin......");
				LauncherModel.addItemToDatabase(this, info, container, i, cellXY[0], cellXY[1], false);
				Log.i("zhoujf", "Launcher.completeAddShortcutFromAppsPage(...) end......");

				if (!mRestoring) {
					mWorkspace.addInScreen(view, container, i, cellXY[0],
							cellXY[1], 1, 1, isWorkspaceLocked());
				}
				break;
			}

		}
		if (!foundCellSpan) {
			showOutOfSpaceMessage();
			return;
		}

	}

    private void completeDelShortcutFromHome(View v) {
    	Object tag = v.getTag();
    	ShortcutInfo info = (ShortcutInfo)tag;
    	LauncherModel.deleteItemFromDatabase(this, info);
    	mWorkspace.delInScreen(v,info.screen);
    }
    
    int[] getSpanForWidget(ComponentName component, int minWidth, int minHeight, int[] spanXY) {
        if (spanXY == null) {
            spanXY = new int[2];
        }

        Rect padding = AppWidgetHostView.getDefaultPaddingForWidget(this, component, null);
        // We want to account for the extra amount of padding that we are adding to the widget
        // to ensure that it gets the full amount of space that it has requested
        int requiredWidth = minWidth + padding.left + padding.right;
        int requiredHeight = minHeight + padding.top + padding.bottom;
        return CellLayout.rectToCell(getResources(), requiredWidth, requiredHeight, null);
    }

    int[] getSpanForWidget(AppWidgetProviderInfo info, int[] spanXY) {
        return getSpanForWidget(info.provider, info.minWidth, info.minHeight, spanXY);
    }

    int[] getMinResizeSpanForWidget(AppWidgetProviderInfo info, int[] spanXY) {
        return getSpanForWidget(info.provider, info.minResizeWidth, info.minResizeHeight, spanXY);
    }

    int[] getSpanForWidget(PendingAddWidgetInfo info, int[] spanXY) {
        return getSpanForWidget(info.componentName, info.minWidth, info.minHeight, spanXY);
    }

    /**
     * Add a widget to the workspace.
     *
     * @param appWidgetId The app widget id
     * @param cellInfo The position on screen where to create the widget.
     */
    private void completeAddAppWidget(final int appWidgetId, long container, int screen) {
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);

        // Calculate the grid spans needed to fit this widget
        CellLayout layout = getCellLayout(container, screen);

        int[] spanXY = getSpanForWidget(appWidgetInfo, null);

        // Try finding open space on Launcher screen
        // We have saved the position to which the widget was dragged-- this really only matters
        // if we are placing widgets on a "spring-loaded" screen
        int[] cellXY = mTmpAddItemCellCoordinates;
        int[] touchXY = mPendingAddInfo.dropPos;
        boolean foundCellSpan = false;
        if (mPendingAddInfo.cellX >= 0 && mPendingAddInfo.cellY >= 0) {
            cellXY[0] = mPendingAddInfo.cellX;
            cellXY[1] = mPendingAddInfo.cellY;
            foundCellSpan = true;
        } else if (touchXY != null) {
            // when dragging and dropping, just find the closest free spot
            int[] result = layout.findNearestVacantArea(
                    touchXY[0], touchXY[1], spanXY[0], spanXY[1], cellXY);
            foundCellSpan = (result != null);
        } else {
            foundCellSpan = layout.findCellForSpan(cellXY, spanXY[0], spanXY[1]);
        }

        if (!foundCellSpan) {
            if (appWidgetId != -1) {
                // Deleting an app widget ID is a void call but writes to disk before returning
                // to the caller...
                new Thread("deleteAppWidgetId") {
                    public void run() {
                        mAppWidgetHost.deleteAppWidgetId(appWidgetId);
                    }
                }.start();
            }
            showOutOfSpaceMessage();
            return;
        }

        // Build Launcher-specific widget info and save to database
        LauncherAppWidgetInfo launcherInfo = new LauncherAppWidgetInfo(appWidgetId);
        launcherInfo.spanX = spanXY[0];
        launcherInfo.spanY = spanXY[1];

		Log.i("zhoujf", "Launcher.completeAddAppWidget(...) begin......");
		LauncherModel.addItemToDatabase(this, launcherInfo, container, screen, cellXY[0], cellXY[1], false);
		Log.i("zhoujf", "Launcher.completeAddAppWidget(...) end......");

        if (!mRestoring) {
            // Perform actual inflation because we're live
            launcherInfo.hostView = mAppWidgetHost.createView(this, appWidgetId, appWidgetInfo);

            launcherInfo.hostView.setAppWidget(appWidgetId, appWidgetInfo);
            launcherInfo.hostView.setTag(launcherInfo);

            mWorkspace.addInScreen(launcherInfo.hostView, container, screen, cellXY[0], cellXY[1],
                    launcherInfo.spanX, launcherInfo.spanY, isWorkspaceLocked());

            addWidgetToAutoAdvanceIfNeeded(launcherInfo.hostView, appWidgetInfo);
        }
    }
    
    
    /**
     * Add a widget to the workspace. add by weijh, 用于遥控器在AllApps界面直接添加快捷图标
     *
     * @param appWidgetId The app widget id
     * @param cellInfo The position on screen where to create the widget.
     */
    private void completeAddAppWidgetFromAppsPage(final int appWidgetId, long container, int screen) {

		AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager
				.getAppWidgetInfo(appWidgetId);
		boolean foundCellSpan = false;
		for (int i = screen; i < 3; i++) {
			// Calculate the grid spans needed to fit this widget
			CellLayout layout = getCellLayout(container, i);

			int[] spanXY = getSpanForWidget(appWidgetInfo, null);

			// Try finding open space on Launcher screen
			// We have saved the position to which the widget was dragged-- this
			// really only matters
			// if we are placing widgets on a "spring-loaded" screen
			int[] cellXY = mTmpAddItemCellCoordinates;

			foundCellSpan = layout
					.findCellForSpan(cellXY, spanXY[0], spanXY[1]);

			if (!foundCellSpan) {
				continue;
			} else {
				// Toast.makeText(getApplicationContext(), "已添加到" + i + "页",
				// 2000)
				// .show();
				// Build Launcher-specific widget info and save to database
				LauncherAppWidgetInfo launcherInfo = new LauncherAppWidgetInfo(
						appWidgetId);
				launcherInfo.spanX = spanXY[0];
				launcherInfo.spanY = spanXY[1];

				Log.i("zhoujf", "Launcher.completeAddAppWidgetFromAppsPage(...) begin......");
				LauncherModel.addItemToDatabase(this, launcherInfo, container, i, cellXY[0], cellXY[1], false);
				Log.i("zhoujf", "Launcher.completeAddAppWidgetFromAppsPage(...) end......");

				if (!mRestoring) {
					// Perform actual inflation because we're live
					launcherInfo.hostView = mAppWidgetHost.createView(this,
							appWidgetId, appWidgetInfo);

					launcherInfo.hostView.setAppWidget(appWidgetId,
							appWidgetInfo);
					launcherInfo.hostView.setTag(launcherInfo);

					mWorkspace.addInScreen(launcherInfo.hostView, container, i,
							cellXY[0], cellXY[1], launcherInfo.spanX,
							launcherInfo.spanY, isWorkspaceLocked());

					addWidgetToAutoAdvanceIfNeeded(launcherInfo.hostView,
							appWidgetInfo);
					break;
				}
			}

		}
		if (!foundCellSpan) {

			if (appWidgetId != -1) {
				// Deleting an app widget ID is a void call but writes to
				// disk
				// before returning
				// to the caller...
				new Thread("deleteAppWidgetId") {
					public void run() {
						mAppWidgetHost.deleteAppWidgetId(appWidgetId);
					}
				}.start();
			}
			showOutOfSpaceMessage();
			return;
		}

	}
    
    private void completeDelAppWidgetFromHome(View v) {
        LauncherAppWidgetInfo appWidgetInfo = (LauncherAppWidgetInfo)v.getTag();
        LauncherModel.deleteItemFromDatabase(this, appWidgetInfo);
        mWorkspace.delInScreen(v,appWidgetInfo.screen);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                mUserPresent = false;
                mDragLayer.clearAllResizeFrames();
                updateRunning();

                // Reset AllApps to its initial state only if we are not in the middle of
                // processing a multi-step drop
                if (mAppsCustomizeTabHost != null && mPendingAddInfo.container == ItemInfo.NO_ID) {
                    mAppsCustomizeTabHost.reset();
                    showWorkspace(false);
                }
            } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
                mUserPresent = true;
                updateRunning();
            }
        }
    };

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        // Listen for broadcasts related to user-presence
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(mReceiver, filter);

        mAttached = true;
        mVisible = true;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mVisible = false;
        mDragLayer.clearAllResizeFrames();

        if (mAttached) {
            unregisterReceiver(mReceiver);
            mAttached = false;
        }
        updateRunning();
    }

    public void onWindowVisibilityChanged(int visibility) {
        mVisible = visibility == View.VISIBLE;
        updateRunning();
    }

    private void sendAdvanceMessage(long delay) {
        mHandler.removeMessages(ADVANCE_MSG);
        Message msg = mHandler.obtainMessage(ADVANCE_MSG);
        mHandler.sendMessageDelayed(msg, delay);
        mAutoAdvanceSentTime = System.currentTimeMillis();
    }

    private void updateRunning() {
        boolean autoAdvanceRunning = mVisible && mUserPresent && !mWidgetsToAdvance.isEmpty();
        if (autoAdvanceRunning != mAutoAdvanceRunning) {
            mAutoAdvanceRunning = autoAdvanceRunning;
            if (autoAdvanceRunning) {
                long delay = mAutoAdvanceTimeLeft == -1 ? mAdvanceInterval : mAutoAdvanceTimeLeft;
                sendAdvanceMessage(delay);
            } else {
                if (!mWidgetsToAdvance.isEmpty()) {
                    mAutoAdvanceTimeLeft = Math.max(0, mAdvanceInterval -
                            (System.currentTimeMillis() - mAutoAdvanceSentTime));
                }
                mHandler.removeMessages(ADVANCE_MSG);
                mHandler.removeMessages(0); // Remove messages sent using postDelayed()
            }
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == ADVANCE_MSG) {
                int i = 0;
                for (View key: mWidgetsToAdvance.keySet()) {
                    final View v = key.findViewById(mWidgetsToAdvance.get(key).autoAdvanceViewId);
                    final int delay = mAdvanceStagger * i;
                    if (v instanceof Advanceable) {
                       postDelayed(new Runnable() {
                           public void run() {
                               ((Advanceable) v).advance();
                           }
                       }, delay);
                    }
                    i++;
                }
                sendAdvanceMessage(mAdvanceInterval);
            }else if (msg.what == TV_WINDOW_MSG){
            	Log.i("Launcher", "========TV_WINDOW_MSG========");
            	HideDefaultTvScreen();
            }
            else if(msg.what == 4444){
            	startTVRootActivity();
            }
        }
    };

    void addWidgetToAutoAdvanceIfNeeded(View hostView, AppWidgetProviderInfo appWidgetInfo) {
        if (appWidgetInfo == null || appWidgetInfo.autoAdvanceViewId == -1) return;
        View v = hostView.findViewById(appWidgetInfo.autoAdvanceViewId);
        if (v instanceof Advanceable) {
            mWidgetsToAdvance.put(hostView, appWidgetInfo);
            ((Advanceable) v).fyiWillBeAdvancedByHostKThx();
            updateRunning();
        }
    }

    void removeWidgetToAutoAdvance(View hostView) {
        if (mWidgetsToAdvance.containsKey(hostView)) {
            mWidgetsToAdvance.remove(hostView);
            updateRunning();
        }
    }

    public void removeAppWidget(LauncherAppWidgetInfo launcherInfo) {
        removeWidgetToAutoAdvance(launcherInfo.hostView);
        launcherInfo.hostView = null;
    }

    void showOutOfSpaceMessage() {
        Toast.makeText(this, getString(R.string.out_of_space), Toast.LENGTH_SHORT).show();
    }

    public LauncherAppWidgetHost getAppWidgetHost() {
        return mAppWidgetHost;
    }

    public LauncherModel getModel() {
        return mModel;
    }

    void closeSystemDialogs() {
        getWindow().closeAllPanels();

        /**
         * We should remove this code when we remove all the dialog code.
        try {
            dismissDialog(DIALOG_CREATE_SHORTCUT);
            // Unlock the workspace if the dialog was showing
        } catch (Exception e) {
            // An exception is thrown if the dialog is not visible, which is fine
        }

        try {
            dismissDialog(DIALOG_RENAME_FOLDER);
            // Unlock the workspace if the dialog was showing
        } catch (Exception e) {
            // An exception is thrown if the dialog is not visible, which is fine
        }
         */

        // Whatever we were doing is hereby canceled.
        mWaitingForResult = false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        
        // Close the menu
        if (Intent.ACTION_MAIN.equals(intent.getAction())) {
        	
            // also will cancel mWaitingForResult.
            closeSystemDialogs();

            boolean alreadyOnHome = ((intent.getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
                        != Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);

            Folder openFolder = mWorkspace.getOpenFolder();
            // In all these cases, only animate if we're already on home
            mWorkspace.exitWidgetResizeMode();
            
            if (alreadyOnHome && mState == State.WORKSPACE && !mWorkspace.isTouchActive() &&
                    openFolder == null) {
            	
                mWorkspace.moveToDefaultScreen(true);
            }

            closeFolder();
            exitSpringLoadedDragMode();
            showWorkspace(alreadyOnHome);

            final View v = getWindow().peekDecorView();
            if (v != null && v.getWindowToken() != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }

            // Reset AllApps to its initial state
            if (!alreadyOnHome && mAppsCustomizeTabHost != null) {
                mAppsCustomizeTabHost.reset();
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Do not call super here
        mSavedInstanceState = savedInstanceState;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(RUNTIME_STATE_CURRENT_SCREEN, mWorkspace.getCurrentPage());
        super.onSaveInstanceState(outState);

        outState.putInt(RUNTIME_STATE, mState.ordinal());
        // We close any open folder since it will not be re-opened, and we need to make sure
        // this state is reflected.
        closeFolder();

        if (mPendingAddInfo.container != ItemInfo.NO_ID && mPendingAddInfo.screen > -1 &&
                mWaitingForResult) {
            outState.putLong(RUNTIME_STATE_PENDING_ADD_CONTAINER, mPendingAddInfo.container);
            outState.putInt(RUNTIME_STATE_PENDING_ADD_SCREEN, mPendingAddInfo.screen);
            outState.putInt(RUNTIME_STATE_PENDING_ADD_CELL_X, mPendingAddInfo.cellX);
            outState.putInt(RUNTIME_STATE_PENDING_ADD_CELL_Y, mPendingAddInfo.cellY);
        }

        if (mFolderInfo != null && mWaitingForResult) {
            outState.putBoolean(RUNTIME_STATE_PENDING_FOLDER_RENAME, true);
            outState.putLong(RUNTIME_STATE_PENDING_FOLDER_RENAME_ID, mFolderInfo.id);
        }

        // Save the current AppsCustomize tab
        if (mAppsCustomizeTabHost != null) {
            String currentTabTag = mAppsCustomizeTabHost.getCurrentTabTag();
            if (currentTabTag != null) {
                outState.putString("apps_customize_currentTab", currentTabTag);
            }
            int currentIndex = mAppsCustomizeContent.getSaveInstanceStateIndex();
            outState.putInt("apps_customize_currentIndex", currentIndex);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Remove all pending runnables
        CloseTvWindow(false);
        KillTVWindowOnStop();
        mHandler.removeMessages(ADVANCE_MSG);
        mHandler.removeMessages(0);
        mWorkspace.removeCallbacks(mBuildLayersRunnable);
        tv_SurfaceView.getHolder().removeCallback(callback);
        this.unregisterReceiver(HotKeyReceiver);
        mHandler.removeCallbacks(m_tvDelayChangeSourceRunnale);
        mHandler.removeCallbacks(tv_OnResumeChangeSourceRunnable);
        mHandler.removeCallbacks(tv_sWindowRunnable);

        // Stop callbacks from LauncherModel
        LauncherApplication app = ((LauncherApplication) getApplication());
        mModel.stopLoader();
        app.setLauncher(null);

        try {
            mAppWidgetHost.stopListening();
        } catch (NullPointerException ex) {
            Log.w(TAG, "problem while stopping AppWidgetHost during Launcher destruction", ex);
        }
        mAppWidgetHost = null;

        mWidgetsToAdvance.clear();

        TextKeyListener.getInstance().release();


        unbindWorkspaceAndHotseatItems();

        getContentResolver().unregisterContentObserver(mWidgetObserver);
        unregisterReceiver(mCloseSystemDialogsReceiver);

        ((ViewGroup) mWorkspace.getParent()).removeAllViews();
        mWorkspace.removeAllViews();
        mWorkspace = null;
        mDragController = null;

        ValueAnimator.clearAllAnimations();
    }

    public DragController getDragController() {
        return mDragController;
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if (requestCode >= 0) mWaitingForResult = true;
        super.startActivityForResult(intent, requestCode);
    }

    /**
     * Indicates that we want global search for this activity by setting the globalSearch
     * argument for {@link #startSearch} to true.
     */
    @Override
    public void startSearch(String initialQuery, boolean selectInitialQuery,
            Bundle appSearchData, boolean globalSearch) {

        showWorkspace(true);

        if (initialQuery == null) {
            // Use any text typed in the launcher as the initial query
            initialQuery = getTypedText();
        }
        if (appSearchData == null) {
            appSearchData = new Bundle();
//            appSearchData.putString(Search.SOURCE, "launcher-search");
        }
        Rect sourceBounds = mSearchDropTargetBar.getSearchBarBounds();

        final SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchManager.startSearch(initialQuery, selectInitialQuery, getComponentName(),
            appSearchData, globalSearch, sourceBounds);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isWorkspaceLocked()) {
            return false;
        }

        super.onCreateOptionsMenu(menu);

//        Intent manageApps = new Intent(Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS);
//        manageApps.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//        Intent settings = new Intent(android.provider.Settings.ACTION_SETTINGS);
//        settings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//        String helpUrl = getString(R.string.help_url);
//        Intent help = new Intent(Intent.ACTION_VIEW, Uri.parse(helpUrl));
//        help.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        
        menu.add(MENU_GROUP_ADD_SHORTCUTINFO, MENU_ADD_SHORTCUTINFO, Menu.NONE, R.string.menu_add_shortcutinfo);   
        menu.add(MENU_GROUP_DEL_SHORTCUTINFO, MENU_DEL_SHORTCUTINFO, Menu.NONE, R.string.menu_del_shortcutinfo);
        
        menu.add(MENU_GROUP_WALLPAPER, MENU_WALLPAPER_SETTINGS, 0, R.string.menu_wallpaper)
            .setIcon(android.R.drawable.ic_menu_gallery)
            .setAlphabeticShortcut('W');
        menu.add(MENU_GROUP_MANAGER, MENU_MANAGE_APPS, 0, R.string.menu_manage_apps)
            .setIcon(android.R.drawable.ic_menu_manage)
//            .setIntent(manageApps)
            .setAlphabeticShortcut('M');
        menu.add(MENU_GROUP_SYSTEM_SETTINGS, MENU_SYSTEM_SETTINGS, 0, R.string.menu_settings)
            .setIcon(android.R.drawable.ic_menu_preferences)
//            .setIntent(settings)
            .setAlphabeticShortcut('P');
//        if (!helpUrl.isEmpty()) {
//            menu.add(0, MENU_HELP, 0, R.string.menu_help)
//                .setIcon(android.R.drawable.ic_menu_help)
//                .setIntent(help)
//                .setAlphabeticShortcut('H');
//        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		if (mAppsCustomizeTabHost.isTransitioning()) {
			return false;
		}
		boolean getFocus = false;
		View v = getCurrentFocus();
		mView = v;
		if ((mWorkspace.getOpenFolder() == null)
				&& ((mView instanceof PagedViewIcon)
						|| (mView instanceof PagedViewWidget)
						|| (mView instanceof BubbleTextView) || (mView instanceof AppWidgetHostView))) {
			getFocus = true;
			// if ((mView instanceof BubbleTextView)
			// && (mWorkspace.getCurrentPage() > 0)) {
			// getFocus = false;
			// // ShortcutInfo info = (ShortcutInfo) mView.getTag();
			// // if ((info.intent.getComponent()!= null) &&
			// //
			// (info.intent.getComponent().getPackageName().equals("com.haier.showallapps"))
			// // && (info.screen == 1) && (info.cellX == 3) && (info.cellY ==
			// // 1)){
			// // getFocus = false;
			// // }
			// } else if ((mView instanceof AppWidgetHostView)
			// && (mWorkspace.getCurrentPage() > 0)) {
			// getFocus = false;
			// // LauncherAppWidgetInfo info = (LauncherAppWidgetInfo)
			// // mView.getTag();
			// // AppWidgetProviderInfo provider =
			// // mAppWidgetManager.getAppWidgetInfo(info.appWidgetId);
			// // String ClassName = provider.provider.getClassName();
			// // Log.i(TAG, "ClassName="+ClassName);
			// // if ((mWorkspace.getCurrentPage() == DEFAULT_SCREEN)
			// // &&
			// // (ClassName.equals("com.haier.widgets.news.NewsWidgetProvider")
			// // ||
			// //
			// ClassName.equals("com.haier.nscreen.ui.movescreen.MoveScreenWidget")
			// // ||
			// // ClassName.equals("com.haier.showallapps.HaierTvEmptyWidget")))
			// // {
			// // getFocus = false;
			// // } else if ((mWorkspace.getCurrentPage() == 2)
			// // &&
			// //
			// (ClassName.equals("com.haier.widgets.news.BestvWidgetProvider")
			// // ||
			// // ClassName.equals("com.haiertv.homewgt.cloudservice.AppWidget")
			// // ||
			// // ClassName.equals("com.hrtvbic.weibo.activity.HuatibangWidget")
			// // || ClassName.equals("com.haier.cloudsearch.widget.Widget")
			// // ||
			// // ClassName.equals("com.hrtvbic.syncshow.widget.widgetProvider")
			// // || ClassName.equals("com.haiersoft.activity.widget"))){
			// // getFocus = false;
			// // }
			// }
			if (mWorkspace.getCurrentPage() == DEFAULT_SCREEN) {
				if ((mView instanceof BubbleTextView)) {

					ShortcutInfo info = (ShortcutInfo) mView.getTag();
					if ((info.intent.getComponent() != null)
							&& (info.intent.getComponent().getPackageName()
									.equals("com.haier.showallapps"))
							&& (info.cellX == 3) && (info.cellY == 1)) {
						getFocus = false;
					}
				} else if ((mView instanceof AppWidgetHostView)) {

					LauncherAppWidgetInfo info = (LauncherAppWidgetInfo) mView
							.getTag();
					AppWidgetProviderInfo provider = mAppWidgetManager
							.getAppWidgetInfo(info.appWidgetId);
					String ClassName = provider.provider.getClassName();
					Log.i(TAG, "ClassName=" + ClassName);
					if ((ClassName
							.equals("com.haier.homecloudlive.RecommendAPPProvider")
							&& info.cellX == 0 && info.cellY == 3)
							|| (ClassName
									.equals("com.haier.widgets.news.BestvWidgetProvider")
									&& info.cellX == 6 && info.cellY == 1)
							|| (ClassName
									.equals("com.haier.showallapps.HaierTvEmptyWidget")
									&& info.cellX == 0 && info.cellY == 1)
							|| (ClassName
									.equals("com.haier.cloudsearch.widget.Widget")
									&& info.cellX == 3 && info.cellY == 4)) {
						getFocus = false;
					}
				}
			}

		}
		boolean allAppsVisible = (mAppsCustomizeTabHost.getVisibility() == View.VISIBLE);
		menu.setGroupVisible(MENU_GROUP_WALLPAPER, !allAppsVisible);

		// 此处需根据当前选中apk或者widget是否已创建快捷方法来进一步判断要显示哪个菜单项
		menu.setGroupVisible(MENU_GROUP_ADD_SHORTCUTINFO, allAppsVisible
				&& getFocus);
		menu.setGroupVisible(MENU_GROUP_DEL_SHORTCUTINFO, !allAppsVisible
				&& getFocus);
		menu.setGroupVisible(MENU_GROUP_MANAGER, true);
		menu.setGroupVisible(MENU_GROUP_SYSTEM_SETTINGS, true);

		return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_WALLPAPER_SETTINGS:
        	Log.d(TAG, "cxj test onOptionsItemSelected, MENU_WALLPAPER_SETTINGS");
        	mShowTvBg = false;
        	m_tvNeedDelayChangeSource = false;
        	m_tvNeedChangeWindow = false;
//        	tv_DefaultScreen.setVisibility(View.VISIBLE);
            startWallpaper();
            return true;
            
        case MENU_MANAGE_APPS:
        	Log.d(TAG, "cxj test onOptionsItemSelected, MENU_MANAGE_APPS");
        	mShowTvBg = true;
        	m_tvNeedDelayChangeSource = false;
        	tv_DefaultScreen.setVisibility(View.VISIBLE);
        	Intent manageApps = new Intent(Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS);
        	manageApps.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                  | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        	startActivity(manageApps);
        	return true;
        	
        case MENU_SYSTEM_SETTINGS:
        	Log.d(TAG, "cxj test onOptionsItemSelected, MENU_SYSTEM_SETTINGS");
        	mShowTvBg = true;
        	m_tvNeedDelayChangeSource = false;
        	tv_DefaultScreen.setVisibility(View.VISIBLE);
        	Intent settings = new Intent(android.provider.Settings.ACTION_SETTINGS);
            settings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(settings);
        	return true;
            
        case MENU_ADD_SHORTCUTINFO:
			Log.d("Launcher", "cxj onOptionsItemSelected, MENU_ADD_SHORTCUTINFO");
			if (mView instanceof PagedViewIcon) {
				ApplicationInfo info = (ApplicationInfo) mView.getTag();
				completeAddShortcutFromAppsPage(info.intent,
						LauncherSettings.Favorites.CONTAINER_DESKTOP,
						MENU_ADD_SCREEN);
			} else if (mView instanceof PagedViewWidget) {
				// add by weijh 添加widget的快捷方式
				Object tag = mView.getTag();
				if (tag instanceof PendingAddItemInfo) {
					final PendingAddItemInfo info = (PendingAddItemInfo) tag;
					if (info.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET) {
						int appWidgetId = getAppWidgetHost()
								.allocateAppWidgetId();
						AppWidgetManager.getInstance(this).bindAppWidgetId(
								appWidgetId, info.componentName);
						completeAddAppWidgetFromAppsPage(appWidgetId,
								LauncherSettings.Favorites.CONTAINER_DESKTOP,
								MENU_ADD_SCREEN);

						// Exit spring loaded mode if necessary after adding the
						// widget
						exitSpringLoadedDragModeDelayed(true, false);
					} else if (info.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT) {
						boolean foundCellSpan = false;
						for (int i = 0; i < 3; i++) {
							CellLayout layout = getCellLayout(
									LauncherSettings.Favorites.CONTAINER_DESKTOP,
									i);
							int[] cellXY = mTmpAddItemCellCoordinates;
							foundCellSpan = layout
									.findCellForSpan(cellXY, 1, 1);
							if (!foundCellSpan) {
								continue;
								// showOutOfSpaceMessage();
								// return true;
							} else {

								// Toast.makeText(getApplicationContext(),
								// "已添加到" + i + "页", 2000).show();
								processShortcutFromDrop(
										info.componentName,
										LauncherSettings.Favorites.CONTAINER_DESKTOP,
										i, cellXY, null);
								break;
							}
						}
						if (!foundCellSpan) {
							showOutOfSpaceMessage();
							return true;
						}

					}
				}
			} else {

			}
        	 
        	return true;
        	
        case MENU_DEL_SHORTCUTINFO:
        	Log.d("Launcher", "cxj onOptionsItemSelected, MENU_DEL_SHORTCUTINFO" );
        	if (mView instanceof BubbleTextView) {
        		completeDelShortcutFromHome(mView);
       	 	} else if (mView instanceof AppWidgetHostView){
       	 		completeDelAppWidgetFromHome(mView);
       	 	} else {
       	 		
       	 	}
        	return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSearchRequested() {
        startSearch(null, false, null, true);
        // Use a custom animation for launching search
        overridePendingTransition(R.anim.fade_in_fast, R.anim.fade_out_fast);
        return true;
    }

    public boolean isWorkspaceLocked() {
        return mWorkspaceLoading || mWaitingForResult;
    }

    private void resetAddInfo() {
        mPendingAddInfo.container = ItemInfo.NO_ID;
        mPendingAddInfo.screen = -1;
        mPendingAddInfo.cellX = mPendingAddInfo.cellY = -1;
        mPendingAddInfo.spanX = mPendingAddInfo.spanY = -1;
        mPendingAddInfo.dropPos = null;
    }

    void addAppWidgetFromPick(Intent data) {
        // TODO: catch bad widget exception when sent
        int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        // TODO: Is this log message meaningful?
        if (LOGD) Log.d(TAG, "dumping extras content=" + data.getExtras());
        addAppWidgetImpl(appWidgetId, null);
    }

    void addAppWidgetImpl(int appWidgetId, PendingAddWidgetInfo info) {
        AppWidgetProviderInfo appWidget = mAppWidgetManager.getAppWidgetInfo(appWidgetId);

        if (appWidget.configure != null) {
            // Launch over to configure widget, if needed
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.setComponent(appWidget.configure);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            if (info != null) {
                if (info.mimeType != null && !info.mimeType.isEmpty()) {
                    intent.putExtra(
                            InstallWidgetReceiver.EXTRA_APPWIDGET_CONFIGURATION_DATA_MIME_TYPE,
                            info.mimeType);

                    final String mimeType = info.mimeType;
                    final ClipData clipData = (ClipData) info.configurationData;
                    final ClipDescription clipDesc = clipData.getDescription();
                    for (int i = 0; i < clipDesc.getMimeTypeCount(); ++i) {
                        if (clipDesc.getMimeType(i).equals(mimeType)) {
                            final ClipData.Item item = clipData.getItemAt(i);
                            final CharSequence stringData = item.getText();
                            final Uri uriData = item.getUri();
                            final Intent intentData = item.getIntent();
                            final String key =
                                InstallWidgetReceiver.EXTRA_APPWIDGET_CONFIGURATION_DATA;
                            if (uriData != null) {
                                intent.putExtra(key, uriData);
                            } else if (intentData != null) {
                                intent.putExtra(key, intentData);
                            } else if (stringData != null) {
                                intent.putExtra(key, stringData);
                            }
                            break;
                        }
                    }
                }
            }

            startActivityForResultSafely(intent, REQUEST_CREATE_APPWIDGET);
        } else {
            // Otherwise just add it
            completeAddAppWidget(appWidgetId, info.container, info.screen);

            // Exit spring loaded mode if necessary after adding the widget
            exitSpringLoadedDragModeDelayed(true, false);
        }
    }

    /**
     * Process a shortcut drop.
     *
     * @param componentName The name of the component
     * @param screen The screen where it should be added
     * @param cell The cell it should be added to, optional
     * @param position The location on the screen where it was dropped, optional
     */
    void processShortcutFromDrop(ComponentName componentName, long container, int screen,
            int[] cell, int[] loc) {
        resetAddInfo();
        mPendingAddInfo.container = container;
        mPendingAddInfo.screen = screen;
        mPendingAddInfo.dropPos = loc;

        if (cell != null) {
            mPendingAddInfo.cellX = cell[0];
            mPendingAddInfo.cellY = cell[1];
        }

        Intent createShortcutIntent = new Intent(Intent.ACTION_CREATE_SHORTCUT);
        createShortcutIntent.setComponent(componentName);
        processShortcut(createShortcutIntent);
    }

    /**
     * Process a widget drop.
     *
     * @param info The PendingAppWidgetInfo of the widget being added.
     * @param screen The screen where it should be added
     * @param cell The cell it should be added to, optional
     * @param position The location on the screen where it was dropped, optional
     */
    void addAppWidgetFromDrop(PendingAddWidgetInfo info, long container, int screen,
            int[] cell, int[] loc) {
        resetAddInfo();
        mPendingAddInfo.container = info.container = container;
        mPendingAddInfo.screen = info.screen = screen;
        mPendingAddInfo.dropPos = loc;
        if (cell != null) {
            mPendingAddInfo.cellX = cell[0];
            mPendingAddInfo.cellY = cell[1];
        }

        int appWidgetId = getAppWidgetHost().allocateAppWidgetId();
        AppWidgetManager.getInstance(this).bindAppWidgetId(appWidgetId, info.componentName);
        addAppWidgetImpl(appWidgetId, info);
    }

    void processShortcut(Intent intent) {
        // Handle case where user selected "Applications"
        String applicationName = getResources().getString(R.string.group_applications);
        String shortcutName = intent.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);

        if (applicationName != null && applicationName.equals(shortcutName)) {
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

            Intent pickIntent = new Intent(Intent.ACTION_PICK_ACTIVITY);
            pickIntent.putExtra(Intent.EXTRA_INTENT, mainIntent);
            pickIntent.putExtra(Intent.EXTRA_TITLE, getText(R.string.title_select_application));
            startActivityForResultSafely(pickIntent, REQUEST_PICK_APPLICATION);
        } else {
            startActivityForResultSafely(intent, REQUEST_CREATE_SHORTCUT);
        }
    }

    void processWallpaper(Intent intent) {
        startActivityForResult(intent, REQUEST_PICK_WALLPAPER);
    }

	FolderIcon addFolder(CellLayout layout, long container, final int screen, int cellX, int cellY) {
		Log.i("zhoujf", "addFolder_container:" + container + ",screen:" + screen + ",cellX:" + cellX + ",cellY:"
				+ cellY);

		final FolderInfo folderInfo = new FolderInfo();
		folderInfo.title = getText(R.string.folder_name);

		// Update the model
		Log.i("zhoujf", "Launcher.addFolder(...) begin......");
		LauncherModel.addItemToDatabase(Launcher.this, folderInfo, container, screen, cellX, cellY, false);
		sFolders.put(folderInfo.id, folderInfo);
		Log.i("zhoujf", "Launcher.addFolder(...) end......");

		// Create the view
		FolderIcon newFolder = FolderIcon.fromXml(R.layout.folder_icon, this, layout, folderInfo, mIconCache);
		mWorkspace.addInScreen(newFolder, container, screen, cellX, cellY, 1, 1, isWorkspaceLocked());
		return newFolder;
	}

    void removeFolder(FolderInfo folder) {
        sFolders.remove(folder.id);
    }

    private void startWallpaper() {
        showWorkspace(true);
        final Intent pickWallpaper = new Intent(Intent.ACTION_SET_WALLPAPER);
        Intent chooser = Intent.createChooser(pickWallpaper,
                getText(R.string.chooser_wallpaper));
        // NOTE: Adds a configure option to the chooser if the wallpaper supports it
        //       Removed in Eclair MR1
//        WallpaperManager wm = (WallpaperManager)
//                getSystemService(Context.WALLPAPER_SERVICE);
//        WallpaperInfo wi = wm.getWallpaperInfo();
//        if (wi != null && wi.getSettingsActivity() != null) {
//            LabeledIntent li = new LabeledIntent(getPackageName(),
//                    R.string.configure_wallpaper, 0);
//            li.setClassName(wi.getPackageName(), wi.getSettingsActivity());
//            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { li });
//        }
        startActivityForResult(chooser, REQUEST_PICK_WALLPAPER);
    }

    /**
     * Registers various content observers. The current implementation registers
     * only a favorites observer to keep track of the favorites applications.
     */
    private void registerContentObservers() {
        ContentResolver resolver = getContentResolver();
        resolver.registerContentObserver(LauncherProvider.CONTENT_APPWIDGET_RESET_URI,
                true, mWidgetObserver);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_HOME:
                    return true;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    if (SystemProperties.getInt("debug.launcher2.dumpstate", 0) != 0) {
                        dumpState();
                        return true;
                    }
                    break;
            }
        } else if (event.getAction() == KeyEvent.ACTION_UP) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_HOME:
                    return true;
            }
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onBackPressed() {
        if (mState == State.APPS_CUSTOMIZE) {
            showWorkspace(true);
        } else if (mWorkspace.getOpenFolder() != null) {
            Folder openFolder = mWorkspace.getOpenFolder();
            if (openFolder.isEditingName()) {
                openFolder.dismissEditingName();
            } else {
                closeFolder();
            }
        } else {
            mWorkspace.exitWidgetResizeMode();

            // Back button is a no-op here, but give at least some feedback for the button press
            mWorkspace.showOutlinesTemporarily();
        }
    }

    /**
     * Re-listen when widgets are reset.
     */
    private void onAppWidgetReset() {
        if (mAppWidgetHost != null) {
            mAppWidgetHost.startListening();
        }
    }

    /**
     * Go through the and disconnect any of the callbacks in the drawables and the views or we
     * leak the previous Home screen on orientation change.
     */
    private void unbindWorkspaceAndHotseatItems() {
        if (mModel != null) {
            mModel.unbindWorkspaceItems();
        }
    }
	
	// *** add by zhoujf 2012-02-15*** || PackName.contentEquals("com.haier.mm") 
	public static void mstarTvChangeSource(String packName) {
		
		mShowTvBg = true;
		m_tvNeedChangeWindow = true;
		if (packName != null) {

			Log.i(TAG,"*****_Launcher.mstarTvChangeSource*****_packName:" + packName);

			if (packName.contentEquals("hrtvbic.tvsetting.ui")
			 || packName.contentEquals("hrtvbic.factorymenu.ui")
			 || packName.contentEquals("com.tvos.pip") 
			 || packName.contentEquals("com.hrtvbic.tvsetting.hotkey")
			 || packName.contentEquals("com.android.settings") 
		     || packName.contentEquals("com.haier.ui")
		     || packName.contentEquals("com.haier.launcher")
		     || packName.contentEquals("com.babao.tvju_j3")
		     || packName.contentEquals("com.haier.showallapps")) {
				
				if (packName.contentEquals("hrtvbic.tvsetting.ui") || packName.contentEquals("com.haier.ui")){
					mShowTvBg = false;
				}else {
					//tv_DefaultScreen.setVisibility(View.VISIBLE);
				}
				m_tvNeedDelayChangeSource = false;
			} 
		} 
		//tv_DefaultScreen.setVisibility(View.VISIBLE);
	}
			
    private static void LauncherChangeTvSourceToStorage() throws InterruptedException{
    	
    	Log.d(TAG, "Into LauncherChangeTvSourceToStorage()");
    	
    	if (onPauseChangeTvSourceToStoragThread != null && onPauseChangeTvSourceToStoragThread.isAlive()){
    		Log.i(TAG, "=====onPauseChangeTvSourceToStoragThread is live , wait, time =" + System.currentTimeMillis());
    		onPauseChangeTvSourceToStoragThread.join();
    		Log.i(TAG, "=====onPauseChangeTvSourceToStoragThread is live , join end, will return. time =" + System.currentTimeMillis());
    		
    		return ;
    	}
    	
    	onPauseChangeTvSourceToStoragThread = new Thread(){
    		public void run() {
    			Log.d(TAG, "Into onPauseChangeTvSourceToStoragThread(), time = " + System.currentTimeMillis());
 /* zgzs   			if (commonSkin == null){
    				commonSkin = new CommonSkin(null);
    				commonSkin.connect(null);
    			}
				if (commonSkin.GetCurrentInputSource() != EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_STORAGE){
					Log.i(TAG, "================LauncherChangeTvSourceToStorage================");
					commonSkin.SetInputSource(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_STORAGE);
				}*/
				Log.d(TAG, "Out onPauseChangeTvSourceToStoragThread(), time = " + System.currentTimeMillis());
			}
		};
		
		while ((onResumeChangeInputSourceThread != null) && (onResumeChangeInputSourceThread.isAlive())){
			Log.i(TAG, "=====onResumeChangeInputSourceThread is live ,OnPause wait, curr time =" + System.currentTimeMillis());
			onResumeChangeInputSourceThread.join();
			Log.i(TAG, "=====onResumeChangeInputSourceThread is live ,OnPause join end. time =" + System.currentTimeMillis());
		}
		
		onPauseChangeTvSourceToStoragThread.start();
    }
    
    /**
     * Launches the intent referred by the clicked shortcut.
     *
     * @param v The view representing the clicked shortcut.
     */
	public void onClick(View v) {
		
		System.out.println("-----Launcher.onClick-----v:" + v);
		
		// Make sure that rogue clicks don't get through while allapps is launching, or after the
		// view has detached (it's possible for this to happen if the view is removed mid touch).
		if (v.getWindowToken() == null) {
			System.out.println("-----v.getWindowToken() == null");
			return;
		}

		if (mWorkspace.isSwitchingState()) {
			System.out.println("-----mWorkspace.isSwitchingState()");
			return;
		}
//zgzs 强制添加一条m_tvIsSmallWindows=true试下,
//		不知道这么做有什么问题，但launcher上的apk点击有了回应，apk可访问……
		m_tvIsSmallWindows=true;

		if (!m_tvIsSmallWindows){
			System.out.println("-----!m_tvIsSmallWindows");
			return;
		}
		
		Object tag = v.getTag();
		
		System.out.println("-----Launcher.onClick-----tag:" + tag);
		
		if (tag instanceof ShortcutInfo) {
			
			System.out.println("-----Launcher.onClick.ShortcutInfo-----");
			
			// Open shortcut
			final Intent intent = ((ShortcutInfo) tag).intent;
			
			// into allApps
			if (intent.getComponent() != null){//small bookmark getpackagename will die,emirguo 20120306
				String packageName = intent.getComponent().getPackageName();
				if ("com.haier.showallapps".equals(packageName)) {
					if (mState == State.APPS_CUSTOMIZE) {
						showWorkspace(true);
					} else {
						onClickAllAppsButton(v);
					}
					return ;
				}
			}
			int[] pos = new int[2];
			v.getLocationOnScreen(pos);
			intent.setSourceBounds(new Rect(pos[0], pos[1], pos[0] + v.getWidth(), pos[1] + v.getHeight()));

			// *** add by zhoujf 2012-02-15***
			String packageName = null;
			if (intent.getComponent() != null){
				ShortcutInfo dragInfo = (ShortcutInfo) tag;
				packageName = dragInfo.intent.getComponent().getPackageName();
				String className = dragInfo.intent.getComponent().getClassName();
				Log.i(TAG, "-----Launcher.onClick.ShortcutInfo.packageName:" + packageName);
				Log.i(TAG, "-----Launcher.onClick.ShortcutInfo.className:" + className);
			}
			mstarTvChangeSource(packageName);
			boolean success = startActivitySafely(intent, tag);

			if (success && v instanceof BubbleTextView) {
				mWaitingForResume = (BubbleTextView) v;
				mWaitingForResume.setStayPressed(true);
			}
		} else if (tag instanceof LauncherAppWidgetInfo) {
			
			System.out.println("-----Launcher.onClick.LauncherAppWidgetInfo-----");

			//TODO widget click
			v.requestFocus();		
    		
		} else if (tag instanceof FolderInfo) {
			
			System.out.println("-----Launcher.onClick.FolderInfo-----");
			
			if (v instanceof FolderIcon) {
				FolderIcon fi = (FolderIcon) v;
				handleFolderClick(fi);
			}
		} 
		// *** hidden allAppsButton add by zhoujf 2012-02-20***
		/*
		else if (v == mAllAppsButton) {
			if (mState == State.APPS_CUSTOMIZE) {
				showWorkspace(true);
			} else {
				onClickAllAppsButton(v);
			}
		}
		*/	
		
	}
	
	public long getLastClickAllAppsTime(){
		return mAllAppsLastClickTime;
	}
	
	public void setLastClickAllAppsTime(long tm) {
		mAllAppsLastClickTime = tm;
	}

    public boolean onTouch(View v, MotionEvent event) {
    	
    	System.out.println("#####Launcher.onTouch#####");
    	
        // this is an intercepted event being forwarded from mWorkspace;
        // clicking anywhere on the workspace causes the customization drawer to slide down
        showWorkspace(true);
        return false;
    }

    /**
     * Event handler for the search button
     *
     * @param v The view that was clicked.
     */
    public void onClickSearchButton(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

        onSearchRequested();
    }

    /**
     * Event handler for the voice button
     *
     * @param v The view that was clicked.
     */
    public void onClickVoiceButton(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

        Intent intent = new Intent(RecognizerIntent.ACTION_WEB_SEARCH);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);
    }

    /**
     * Event handler for the "grid" button that appears on the home screen, which
     * enters all apps mode.
     *
     * @param v The view that was clicked.
     */
    public void onClickAllAppsButton(View v) {
    	mAllAppsLastClickTime = System.currentTimeMillis();
        showAllApps(true);
    }

    public void onTouchDownAllAppsButton(View v) {
        // Provide the same haptic feedback that the system offers for virtual keys.
        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
    }

    public void onClickAppMarketButton(View v) {
        if (mAppMarketIntent != null) {
            startActivitySafely(mAppMarketIntent, "app market");
        }
    }

    void startApplicationDetailsActivity(ComponentName componentName) {
        String packageName = componentName.getPackageName();
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", packageName, null));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);
    }

    void startApplicationUninstallActivity(ApplicationInfo appInfo) {
        if ((appInfo.flags & ApplicationInfo.DOWNLOADED_FLAG) == 0) {
            // System applications cannot be installed. For now, show a toast explaining that.
            // We may give them the option of disabling apps this way.
            int messageId = R.string.uninstall_system_app_text;
            Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show();
        } else {
            String packageName = appInfo.componentName.getPackageName();
            String className = appInfo.componentName.getClassName();
            Intent intent = new Intent(
                    Intent.ACTION_DELETE, Uri.fromParts("package", packageName, className));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivity(intent);
        }
    }

    boolean startActivitySafely(Intent intent, Object tag) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Unable to launch. tag=" + tag + " intent=" + intent, e);
        } catch (SecurityException e) {
            Toast.makeText(this, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Launcher does not have the permission to launch " + intent +
                    ". Make sure to create a MAIN intent-filter for the corresponding activity " +
                    "or use the exported attribute for this activity. "
                    + "tag="+ tag + " intent=" + intent, e);
        }
        return false;
    }

    void startActivityForResultSafely(Intent intent, int requestCode) {
        try {
            startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
        } catch (SecurityException e) {
            Toast.makeText(this, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Launcher does not have the permission to launch " + intent +
                    ". Make sure to create a MAIN intent-filter for the corresponding activity " +
                    "or use the exported attribute for this activity.", e);
        }
    }

    private void handleFolderClick(FolderIcon folderIcon) {
        final FolderInfo info = folderIcon.mInfo;
        Folder openFolder = mWorkspace.getFolderForTag(info);

        // If the folder info reports that the associated folder is open, then verify that
        // it is actually opened. There have been a few instances where this gets out of sync.
        if (info.opened && openFolder == null) {
            Log.d(TAG, "Folder info marked as open, but associated folder is not open. Screen: "
                    + info.screen + " (" + info.cellX + ", " + info.cellY + ")");
            info.opened = false;
        }

        if (!info.opened) {
            // Close any open folder
            closeFolder();
            // Open the requested folder
            openFolder(folderIcon);
        } else {
            // Find the open folder...
            int folderScreen;
            if (openFolder != null) {
                folderScreen = mWorkspace.getPageForView(openFolder);
                // .. and close it
                closeFolder(openFolder);
                if (folderScreen != mWorkspace.getCurrentPage()) {
                    // Close any folder open on the current screen
                    closeFolder();
                    // Pull the folder onto this screen
                    openFolder(folderIcon);
                }
            }
        }
    }

    private void growAndFadeOutFolderIcon(FolderIcon fi) {
        if (fi == null) return;
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 0);
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 1.5f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 1.5f);

        FolderInfo info = (FolderInfo) fi.getTag();
        if (info.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
            CellLayout cl = (CellLayout) fi.getParent().getParent();
            CellLayout.LayoutParams lp = (CellLayout.LayoutParams) fi.getLayoutParams();
            cl.setFolderLeaveBehindCell(lp.cellX, lp.cellY);
        }

        ObjectAnimator oa = ObjectAnimator.ofPropertyValuesHolder(fi, alpha, scaleX, scaleY);
        oa.setDuration(getResources().getInteger(R.integer.config_folderAnimDuration));
        oa.start();
    }

    private void shrinkAndFadeInFolderIcon(FolderIcon fi) {
        if (fi == null) return;
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 1.0f);
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 1.0f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 1.0f);

        FolderInfo info = (FolderInfo) fi.getTag();
        CellLayout cl = null;
        if (info.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
            cl = (CellLayout) fi.getParent().getParent();
        }

        final CellLayout layout = cl;
        ObjectAnimator oa = ObjectAnimator.ofPropertyValuesHolder(fi, alpha, scaleX, scaleY);
        oa.setDuration(getResources().getInteger(R.integer.config_folderAnimDuration));
        oa.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (layout != null) {
                    layout.clearFolderLeaveBehind();
                }
            }
        });
        oa.start();
    }

    /**
     * Opens the user folder described by the specified tag. The opening of the folder
     * is animated relative to the specified View. If the View is null, no animation
     * is played.
     *
     * @param folderInfo The FolderInfo describing the folder to open.
     */
    public void openFolder(FolderIcon folderIcon) {
        Folder folder = folderIcon.mFolder;
        FolderInfo info = folder.mInfo;

        growAndFadeOutFolderIcon(folderIcon);
        info.opened = true;

        // Just verify that the folder hasn't already been added to the DragLayer.
        // There was a one-off crash where the folder had a parent already.
        if (folder.getParent() == null) {
            mDragLayer.addView(folder);
            mDragController.addDropTarget((DropTarget) folder);
        } else {
            Log.w(TAG, "Opening folder (" + folder + ") which already has a parent (" +
                    folder.getParent() + ").");
        }
        folder.animateOpen();
    }

    public void closeFolder() {
        Folder folder = mWorkspace.getOpenFolder();
        if (folder != null) {
            if (folder.isEditingName()) {
                folder.dismissEditingName();
            }
            closeFolder(folder);

            // Dismiss the folder cling
            dismissFolderCling(null);
        }
    }

    void closeFolder(Folder folder) {
        folder.getInfo().opened = false;

        ViewGroup parent = (ViewGroup) folder.getParent().getParent();
        if (parent != null) {
            FolderIcon fi = (FolderIcon) mWorkspace.getViewForTag(folder.mInfo);
            shrinkAndFadeInFolderIcon(fi);
        }
        folder.animateClosed();
    }

    public boolean onLongClick(View v) {

		if (mState != State.WORKSPACE) {
			// Toast.makeText(getApplicationContext(),
			// "mState != State.WORKSPACE", 2000).show();
			return false;
		}

		if (isWorkspaceLocked()) {
			// Toast.makeText(getApplicationContext(), "isWorkspaceLocked()",
			// 2000)
			// .show();
			return false;
		}

		if (!(v instanceof CellLayout)) {
			v = (View) v.getParent().getParent();
		}

		resetAddInfo();
		CellLayout.CellInfo longClickCellInfo = (CellLayout.CellInfo) v
				.getTag();
		// This happens when long clicking an item with the dpad/trackball
		if (longClickCellInfo == null) {
			return true;
		}

		// The hotseat touch handling does not go through Workspace, and we
		// always allow long press
		// on hotseat items.
		final View itemUnderLongClick = longClickCellInfo.cell;
		boolean allowLongPress = isHotseatLayout(v)
				|| mWorkspace.allowLongPress();

		if (allowLongPress && !mDragController.isDragging()) {

			if (itemUnderLongClick == null) {
				// User long pressed on empty space
				mWorkspace.performHapticFeedback(
						HapticFeedbackConstants.LONG_PRESS,
						HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING);
				mShowTvBg = true;
				m_tvNeedDelayChangeSource = false;
				tv_DefaultScreen.setVisibility(View.VISIBLE);
				startWallpaper();
			} else {
				if (!(itemUnderLongClick instanceof Folder)) {
					// User long pressed on an item
					/* 只有第一页的图标可以拖动，其它页的都不让拖动 */
					// if (mWorkspace.getCurrentPage() == 0) {
					// mWorkspace.startDrag(longClickCellInfo);
					// }
					if (mWorkspace.getCurrentPage() == DEFAULT_SCREEN) {
						if ((longClickCellInfo.cellX == 3 && longClickCellInfo.cellY == 1)
								| (longClickCellInfo.cellX == 0 && longClickCellInfo.cellY == 0)
								| (longClickCellInfo.cellX == 0 && longClickCellInfo.cellY == 1)
								| (longClickCellInfo.cellX == 0 && longClickCellInfo.cellY == 3)
								| (longClickCellInfo.cellX == 6 && longClickCellInfo.cellY == 1)
								| (longClickCellInfo.cellX == 3 && longClickCellInfo.cellY == 4)) {

							System.out.println();
						}

						else {
							mWorkspace.startDrag(longClickCellInfo);
						}
					} else {
						mWorkspace.startDrag(longClickCellInfo);
					}
					// if (mWorkspace.getCurrentPage() == DEFAULT_SCREEN
					// && (itemUnderLongClick instanceof AppWidgetHostView |
					// (longClickCellInfo.cellX == 3 && longClickCellInfo.cellY
					// == 1))) {
					//
					// } else {
					// mWorkspace.startDrag(longClickCellInfo);
					// }

					// if(itemUnderLongClick instanceof AppWidgetHostView &&
					// mWorkspace.getCurrentPage()==DEFAULT_SCREEN){
					//
					// }

					// else if(itemUnderLongClick instanceof BubbleTextView &&
					// mWorkspace.getCurrentPage()==DEFAULT_SCREEN){
					// //(PagedViewIcon)itemUnderLongClick.
					//
					//
					// }
					// LauncherAppWidgetInfo info = (LauncherAppWidgetInfo)
					// itemUnderLongClick
					// .getTag();
					// AppWidgetProviderInfo provider = mAppWidgetManager
					// .getAppWidgetInfo(info.appWidgetId);
					// String ClassName = provider.provider.getClassName();
					// if (mWorkspace.getCurrentPage() == DEFAULT_SCREEN
					// && ClassName
					// .equals("com.haier.showallapps.HaierTvEmptyWidget")) {
					//
					// } else {
					// mWorkspace.startDrag(longClickCellInfo);
					// }
					// }

				}
			}
		}
		return true;
	}

    boolean isHotseatLayout(View layout) {
        return mHotseat != null && layout != null &&
                (layout instanceof CellLayout) && (layout == mHotseat.getLayout());
    }
    Hotseat getHotseat() {
        return mHotseat;
    }
    SearchDropTargetBar getSearchBar() {
        return mSearchDropTargetBar;
    }

    /**
     * Returns the CellLayout of the specified container at the specified screen.
     */
    CellLayout getCellLayout(long container, int screen) {
        if (container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
            if (mHotseat != null) {
                return mHotseat.getLayout();
            } else {
                return null;
            }
        } else {
            return (CellLayout) mWorkspace.getChildAt(screen);
        }
    }

    Workspace getWorkspace() {
        return mWorkspace;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_CREATE_SHORTCUT:
                return new CreateShortcut().createDialog();
            case DIALOG_RENAME_FOLDER:
                return new RenameFolder().createDialog();
        }

        return super.onCreateDialog(id);
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case DIALOG_CREATE_SHORTCUT:
                break;
            case DIALOG_RENAME_FOLDER:
                if (mFolderInfo != null) {
                    EditText input = (EditText) dialog.findViewById(R.id.folder_name);
                    final CharSequence text = mFolderInfo.title;
                    input.setText(text);
                    input.setSelection(0, text.length());
                }
                break;
        }
    }

    void showRenameDialog(FolderInfo info) {
        mFolderInfo = info;
        mWaitingForResult = true;
        showDialog(DIALOG_RENAME_FOLDER);
    }

    private void showAddDialog() {
        resetAddInfo();
        mPendingAddInfo.container = LauncherSettings.Favorites.CONTAINER_DESKTOP;
        mPendingAddInfo.screen = mWorkspace.getCurrentPage();
        mWaitingForResult = true;
        showDialog(DIALOG_CREATE_SHORTCUT);
    }

    private class RenameFolder {
        private EditText mInput;

        Dialog createDialog() {
            final View layout = View.inflate(Launcher.this, R.layout.rename_folder, null);
            mInput = (EditText) layout.findViewById(R.id.folder_name);

            AlertDialog.Builder builder = new AlertDialog.Builder(Launcher.this);
            builder.setIcon(0);
            builder.setTitle(getString(R.string.rename_folder_title));
            builder.setCancelable(true);
            builder.setOnCancelListener(new Dialog.OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                    cleanup();
                }
            });
            builder.setNegativeButton(getString(R.string.cancel_action),
                new Dialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        cleanup();
                    }
                }
            );
            builder.setPositiveButton(getString(R.string.rename_action),
                new Dialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        changeFolderName();
                    }
                }
            );
            builder.setView(layout);

            final AlertDialog dialog = builder.create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                public void onShow(DialogInterface dialog) {
                    mWaitingForResult = true;
                    mInput.requestFocus();
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.showSoftInput(mInput, 0);
                }
            });

            return dialog;
        }

        private void changeFolderName() {
            final String name = mInput.getText().toString();
            if (!TextUtils.isEmpty(name)) {
                // Make sure we have the right folder info
                mFolderInfo = sFolders.get(mFolderInfo.id);
                mFolderInfo.title = name;
                LauncherModel.updateItemInDatabase(Launcher.this, mFolderInfo);

                if (mWorkspaceLoading) {
                    lockAllApps();
                    mModel.startLoader(Launcher.this, false);
                } else {
                    final FolderIcon folderIcon = (FolderIcon)
                            mWorkspace.getViewForTag(mFolderInfo);
                    if (folderIcon != null) {
                        // TODO: At some point we'll probably want some version of setting
                        // the text for a folder icon.
                        //folderIcon.setText(name);
                        getWorkspace().requestLayout();
                    } else {
                        lockAllApps();
                        mWorkspaceLoading = true;
                        mModel.startLoader(Launcher.this, false);
                    }
                }
            }
            cleanup();
        }

        private void cleanup() {
            dismissDialog(DIALOG_RENAME_FOLDER);
            mWaitingForResult = false;
            mFolderInfo = null;
        }
    }

    // Now a part of LauncherModel.Callbacks. Used to reorder loading steps.
    public boolean isAllAppsVisible() {
        return (mState == State.APPS_CUSTOMIZE);
    }

    // AllAppsView.Watcher
    public void zoomed(float zoom) {
        if (zoom == 1.0f) {
            mWorkspace.setVisibility(View.GONE);
        }
    }

    /**
     * Helper method for the cameraZoomIn/cameraZoomOut animations
     * @param view The view being animated
     * @param state The state that we are moving in or out of (eg. APPS_CUSTOMIZE)
     * @param scaleFactor The scale factor used for the zoom
     */
    private void setPivotsForZoom(View view, float scaleFactor) {
        view.setPivotX(view.getWidth() / 2.0f);
        view.setPivotY(view.getHeight() / 2.0f);
    }

    void updateWallpaperVisibility(boolean visible) {
        int wpflags = visible ? WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER : 0;
        int curflags = getWindow().getAttributes().flags
                & WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER;
        if (wpflags != curflags) {
            getWindow().setFlags(wpflags, WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER);
        }
    }

    /**
     * Things to test when changing the following seven functions.
     *   - Home from workspace
     *          - from center screen
     *          - from other screens
     *   - Home from all apps
     *          - from center screen
     *          - from other screens
     *   - Back from all apps
     *          - from center screen
     *          - from other screens
     *   - Launch app from workspace and quit
     *          - with back
     *          - with home
     *   - Launch app from all apps and quit
     *          - with back
     *          - with home
     *   - Go to a screen that's not the default, then all
     *     apps, and launch and app, and go back
     *          - with back
     *          -with home
     *   - On workspace, long press power and go back
     *          - with back
     *          - with home
     *   - On all apps, long press power and go back
     *          - with back
     *          - with home
     *   - On workspace, power off
     *   - On all apps, power off
     *   - Launch an app and turn off the screen while in that app
     *          - Go back with home key
     *          - Go back with back key  TODO: make this not go to workspace
     *          - From all apps
     *          - From workspace
     *   - Enter and exit car mode (becuase it causes an extra configuration changed)
     *          - From all apps
     *          - From the center workspace
     *          - From another workspace
     */

    /**
     * Zoom the camera out from the workspace to reveal 'toView'.
     * Assumes that the view to show is anchored at either the very top or very bottom
     * of the screen.
     */
    private void showAppsCustomizeHelper(boolean animated, final boolean springLoaded) {
        if (mStateAnimation != null) {
            mStateAnimation.cancel();
            mStateAnimation = null;
        }
        final Resources res = getResources();
        final Launcher instance = this;

        final int duration = res.getInteger(R.integer.config_appsCustomizeZoomInTime);
        final int fadeDuration = res.getInteger(R.integer.config_appsCustomizeFadeInTime);
        final float scale = (float) res.getInteger(R.integer.config_appsCustomizeZoomScaleFactor);
        final View toView = mAppsCustomizeTabHost;
        final int startDelay =
                res.getInteger(R.integer.config_workspaceAppsCustomizeAnimationStagger);

        setPivotsForZoom(toView, scale);

        // Shrink workspaces away if going to AppsCustomize from workspace
        mWorkspace.changeState(Workspace.State.NORMAL, animated);

        if (animated) {
            final ValueAnimator scaleAnim = ValueAnimator.ofFloat(0f, 1f).setDuration(duration);
            scaleAnim.setInterpolator(new Workspace.ZoomOutInterpolator());
            scaleAnim.addUpdateListener(new LauncherAnimatorUpdateListener() {
                public void onAnimationUpdate(float a, float b) {
                    toView.setScaleX(a * scale + b * 1f);
                    toView.setScaleY(a * scale + b * 1f);
                }
            });

            toView.setVisibility(View.VISIBLE);
            toView.setAlpha(0f);
            ValueAnimator alphaAnim = ValueAnimator.ofFloat(0f, 1f).setDuration(fadeDuration);
            alphaAnim.setInterpolator(new DecelerateInterpolator(1.5f));
            alphaAnim.addUpdateListener(new LauncherAnimatorUpdateListener() {
                public void onAnimationUpdate(float a, float b) {
                    // don't need to invalidate because we do so above
                    toView.setAlpha(a * 0f + b * 1f);
                }
            });
            alphaAnim.setStartDelay(startDelay);
            alphaAnim.start();

            scaleAnim.addListener(new AnimatorListenerAdapter() {
                boolean animationCancelled = false;

                @Override
                public void onAnimationStart(Animator animation) {
                	updateWallpaperVisibility(true);
                	
                    // Prepare the position
                    toView.setTranslationX(0.0f);
                    toView.setTranslationY(0.0f);
                    toView.setVisibility(View.VISIBLE);
                    toView.bringToFront();
                }
                @Override
                public void onAnimationEnd(Animator animation) {
                    // If we don't set the final scale values here, if this animation is cancelled
                    // it will have the wrong scale value and subsequent cameraPan animations will
                    // not fix that
                    toView.setScaleX(1.0f);
                    toView.setScaleY(1.0f);
                    if (toView instanceof LauncherTransitionable) {
                        ((LauncherTransitionable) toView).onLauncherTransitionEnd(instance,
                                scaleAnim, false);
                    }

                    if (!springLoaded && !LauncherApplication.isScreenLarge()) {
                        // Hide the workspace scrollbar
                        mWorkspace.hideScrollingIndicator(true);
                        hideDockDivider();
                    }
                    if (!animationCancelled) {
                        updateWallpaperVisibility(false);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    animationCancelled = true;
                }
            });

            // toView should appear right at the end of the workspace shrink animation
            mStateAnimation = new AnimatorSet();
            mStateAnimation.play(scaleAnim).after(startDelay);

            boolean delayAnim = false;
            if (toView instanceof LauncherTransitionable) {
                LauncherTransitionable lt = (LauncherTransitionable) toView;
                delayAnim = lt.onLauncherTransitionStart(instance, mStateAnimation, false);
            }
            // if the anim is delayed, the LauncherTransitionable is responsible for starting it
            if (!delayAnim) {
                // TODO: q-- what if this anim is cancelled before being started? or started after
                // being cancelled?
                mStateAnimation.start();
            }
        } else {
            toView.setTranslationX(0.0f);
            toView.setTranslationY(0.0f);
            toView.setScaleX(1.0f);
            toView.setScaleY(1.0f);
            toView.setVisibility(View.VISIBLE);
            toView.bringToFront();
            if (toView instanceof LauncherTransitionable) {
                ((LauncherTransitionable) toView).onLauncherTransitionStart(instance, null, false);
                ((LauncherTransitionable) toView).onLauncherTransitionEnd(instance, null, false);

                if (!springLoaded && !LauncherApplication.isScreenLarge()) {
                    // Hide the workspace scrollbar
                    mWorkspace.hideScrollingIndicator(true);
                    hideDockDivider();
                }
            }
            updateWallpaperVisibility(false);
        }
    }

    /**
     * Zoom the camera back into the workspace, hiding 'fromView'.
     * This is the opposite of showAppsCustomizeHelper.
     * @param animated If true, the transition will be animated.
     */
    private void hideAppsCustomizeHelper(boolean animated, final boolean springLoaded) {
        if (mStateAnimation != null) {
            mStateAnimation.cancel();
            mStateAnimation = null;
        }
        Resources res = getResources();
        final Launcher instance = this;

        final int duration = res.getInteger(R.integer.config_appsCustomizeZoomOutTime);
        final float scaleFactor = (float)
                res.getInteger(R.integer.config_appsCustomizeZoomScaleFactor);
        final View fromView = mAppsCustomizeTabHost;

        setPivotsForZoom(fromView, scaleFactor);
        updateWallpaperVisibility(true);
        showHotseat(animated);
        if (animated) {
            final float oldScaleX = fromView.getScaleX();
            final float oldScaleY = fromView.getScaleY();

            ValueAnimator scaleAnim = ValueAnimator.ofFloat(0f, 1f).setDuration(duration);
            scaleAnim.setInterpolator(new Workspace.ZoomInInterpolator());
            scaleAnim.addUpdateListener(new LauncherAnimatorUpdateListener() {
                public void onAnimationUpdate(float a, float b) {
                    fromView.setScaleX(a * oldScaleX + b * scaleFactor);
                    fromView.setScaleY(a * oldScaleY + b * scaleFactor);
                }
            });
            final ValueAnimator alphaAnim = ValueAnimator.ofFloat(0f, 1f);
            alphaAnim.setDuration(res.getInteger(R.integer.config_appsCustomizeFadeOutTime));
            alphaAnim.setInterpolator(new AccelerateDecelerateInterpolator());
            alphaAnim.addUpdateListener(new LauncherAnimatorUpdateListener() {
                public void onAnimationUpdate(float a, float b) {
                    fromView.setAlpha(a * 1f + b * 0f);
                }
            });
            if (fromView instanceof LauncherTransitionable) {
                ((LauncherTransitionable) fromView).onLauncherTransitionStart(instance, alphaAnim,
                        true);
            }
            alphaAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    updateWallpaperVisibility(true);
                	
                    fromView.setVisibility(View.GONE);
                    if (fromView instanceof LauncherTransitionable) {
                        ((LauncherTransitionable) fromView).onLauncherTransitionEnd(instance,
                                alphaAnim, true);
                    }
                    mWorkspace.hideScrollingIndicator(false);
                }
            });

            mStateAnimation = new AnimatorSet();
            mStateAnimation.playTogether(scaleAnim, alphaAnim);
            mStateAnimation.start();
        } else {
            fromView.setVisibility(View.GONE);
            if (fromView instanceof LauncherTransitionable) {
                ((LauncherTransitionable) fromView).onLauncherTransitionStart(instance, null, true);
                ((LauncherTransitionable) fromView).onLauncherTransitionEnd(instance, null, true);
            }
            mWorkspace.hideScrollingIndicator(false);
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            mAppsCustomizeTabHost.onTrimMemory();
        }
    }

    void showWorkspace(boolean animated) {
        Resources res = getResources();
        int stagger = res.getInteger(R.integer.config_appsCustomizeWorkspaceAnimationStagger);

        mWorkspace.changeState(Workspace.State.NORMAL, animated, stagger);
        if (mState != State.WORKSPACE) {
            mWorkspace.setVisibility(View.VISIBLE);
            hideAppsCustomizeHelper(animated, false);

            // Show the search bar and hotseat
            mSearchDropTargetBar.showSearchBar(animated);
            // We only need to animate in the dock divider if we're going from spring loaded mode
            showDockDivider(animated && mState == State.APPS_CUSTOMIZE_SPRING_LOADED);

            // Set focus to the AppsCustomize button
    		// hidden allAppsButton *** add by zhoujf 2012-02-20***
            /* 
            if (mAllAppsButton != null) {
                mAllAppsButton.requestFocus();
            }
            */
            
            mWorkspace.getChildAt(mWorkspace.getCurrentPage()).requestFocus();
            HideDefaultTvScreen();
        }

        mWorkspace.flashScrollingIndicator(animated);

        // Change the state *after* we've called all the transition code
        if(mState != State.WORKSPACE) {
            mState = State.WORKSPACE;
            HideDefaultTvScreen();
        }

        // Resume the auto-advance of widgets
        mUserPresent = true;
        updateRunning();

        // send an accessibility event to announce the context change
        getWindow().getDecorView().sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED);
        
        SurfaceView Tv_SurfaceView = (SurfaceView)findViewById(R.id.Surface_view);
    	Tv_SurfaceView.setBackgroundColor(Color.TRANSPARENT);
    	
    }

    void showAllApps(boolean animated) {
        if (mState != State.WORKSPACE) return;

        showAppsCustomizeHelper(animated, false);
        mAppsCustomizeTabHost.requestFocus();

        // Hide the search bar and hotseat
        mSearchDropTargetBar.hideSearchBar(animated);

        // Change the state *after* we've called all the transition code
        mState = State.APPS_CUSTOMIZE;

        // Pause the auto-advance of widgets until we are out of AllApps
        mUserPresent = false;
        updateRunning();
        closeFolder();

    	SurfaceView Tv_SurfaceView = (SurfaceView)findViewById(R.id.Surface_view);
    	if(Tv_SurfaceView !=null){
        	Tv_SurfaceView.setBackgroundColor(Color.BLACK);
    	}
    	  	
    	
        // Send an accessibility event to announce the context change
        getWindow().getDecorView().sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED);
    }

    void enterSpringLoadedDragMode() {
        if (mState == State.APPS_CUSTOMIZE) {
            mWorkspace.changeState(Workspace.State.SPRING_LOADED);
            hideAppsCustomizeHelper(true, true);
            hideDockDivider();
            mState = State.APPS_CUSTOMIZE_SPRING_LOADED;
        }
    }

    void exitSpringLoadedDragModeDelayed(final boolean successfulDrop, boolean extendedDelay) {
        if (mState != State.APPS_CUSTOMIZE_SPRING_LOADED) return;

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (successfulDrop) {
                    // Before we show workspace, hide all apps again because
                    // exitSpringLoadedDragMode made it visible. This is a bit hacky; we should
                    // clean up our state transition functions
                    mAppsCustomizeTabHost.setVisibility(View.GONE);
                    mSearchDropTargetBar.showSearchBar(true);
                    showWorkspace(true);
                } else {
                    exitSpringLoadedDragMode();
                }
            }
        }, (extendedDelay ?
                EXIT_SPRINGLOADED_MODE_LONG_TIMEOUT :
                EXIT_SPRINGLOADED_MODE_SHORT_TIMEOUT));
    }

    void exitSpringLoadedDragMode() {
        if (mState == State.APPS_CUSTOMIZE_SPRING_LOADED) {
            final boolean animated = true;
            final boolean springLoaded = true;
            showAppsCustomizeHelper(animated, springLoaded);
            mState = State.APPS_CUSTOMIZE;
        }
        // Otherwise, we are not in spring loaded mode, so don't do anything.
    }

    void hideDockDivider() {
        if (mQsbDivider != null && mDockDivider != null) {
            mQsbDivider.setVisibility(View.INVISIBLE);
            mDockDivider.setVisibility(View.INVISIBLE);
        }
    }

    void showDockDivider(boolean animated) {
        if (mQsbDivider != null && mDockDivider != null) {
            mQsbDivider.setVisibility(View.VISIBLE);
            mDockDivider.setVisibility(View.VISIBLE);
            if (mDividerAnimator != null) {
                mDividerAnimator.cancel();
                mQsbDivider.setAlpha(1f);
                mDockDivider.setAlpha(1f);
                mDividerAnimator = null;
            }
            if (animated) {
                mDividerAnimator = new AnimatorSet();
                mDividerAnimator.playTogether(ObjectAnimator.ofFloat(mQsbDivider, "alpha", 1f),
                        ObjectAnimator.ofFloat(mDockDivider, "alpha", 1f));
                mDividerAnimator.setDuration(mSearchDropTargetBar.getTransitionInDuration());
                mDividerAnimator.start();
            }
        }
    }

    void lockAllApps() {
        // TODO
    }

    void unlockAllApps() {
        // TODO
    }

    public boolean isAllAppsCustomizeOpen() {
        return mState == State.APPS_CUSTOMIZE;
    }

    /**
     * Shows the hotseat area.
     */
    void showHotseat(boolean animated) {
        if (!LauncherApplication.isScreenLarge()) {
            if (animated) {
                int duration = mSearchDropTargetBar.getTransitionInDuration();
                mHotseat.animate().alpha(1f).setDuration(duration);
            } else {
                mHotseat.setAlpha(1f);
            }
        }
    }

    /**
     * Hides the hotseat area.
     */
    void hideHotseat(boolean animated) {
        if (!LauncherApplication.isScreenLarge()) {
            if (animated) {
                int duration = mSearchDropTargetBar.getTransitionOutDuration();
                mHotseat.animate().alpha(0f).setDuration(duration);
            } else {
                mHotseat.setAlpha(0f);
            }
        }
    }

    /**
     * Add an item from all apps or customize onto the given workspace screen.
     * If layout is null, add to the current screen.
     */
    void addExternalItemToScreen(ItemInfo itemInfo, final CellLayout layout) {
        if (!mWorkspace.addExternalItemToScreen(itemInfo, layout)) {
            showOutOfSpaceMessage();
        }
    }

    /** Maps the current orientation to an index for referencing orientation correct global icons */
    private int getCurrentOrientationIndexForGlobalIcons() {
        // default - 0, landscape - 1
        switch (getResources().getConfiguration().orientation) {
        case Configuration.ORIENTATION_LANDSCAPE:
            return 1;
        default:
            return 0;
        }
    }

    private Drawable getExternalPackageToolbarIcon(ComponentName activityName) {
        try {
            PackageManager packageManager = getPackageManager();
            // Look for the toolbar icon specified in the activity meta-data
            Bundle metaData = packageManager.getActivityInfo(
                    activityName, PackageManager.GET_META_DATA).metaData;
            if (metaData != null) {
                int iconResId = metaData.getInt(TOOLBAR_ICON_METADATA_NAME);
                if (iconResId != 0) {
                    Resources res = packageManager.getResourcesForActivity(activityName);
                    return res.getDrawable(iconResId);
                }
            }
        } catch (NameNotFoundException e) {
            // This can happen if the activity defines an invalid drawable
            Log.w(TAG, "Failed to load toolbar icon; " + activityName.flattenToShortString() +
                    " not found", e);
        } catch (Resources.NotFoundException nfe) {
            // This can happen if the activity defines an invalid drawable
            Log.w(TAG, "Failed to load toolbar icon from " + activityName.flattenToShortString(),
                    nfe);
        }
        return null;
    }

    // if successful in getting icon, return it; otherwise, set button to use default drawable
    private Drawable.ConstantState updateTextButtonWithIconFromExternalActivity(
            int buttonId, ComponentName activityName, int fallbackDrawableId) {
        Drawable toolbarIcon = getExternalPackageToolbarIcon(activityName);
        Resources r = getResources();
        int w = r.getDimensionPixelSize(R.dimen.toolbar_external_icon_width);
        int h = r.getDimensionPixelSize(R.dimen.toolbar_external_icon_height);

        TextView button = (TextView) findViewById(buttonId);
        // If we were unable to find the icon via the meta-data, use a generic one
        if (toolbarIcon == null) {
            toolbarIcon = r.getDrawable(fallbackDrawableId);
            toolbarIcon.setBounds(0, 0, w, h);
            if (button != null) {
                button.setCompoundDrawables(toolbarIcon, null, null, null);
            }
            return null;
        } else {
            toolbarIcon.setBounds(0, 0, w, h);
            if (button != null) {
                button.setCompoundDrawables(toolbarIcon, null, null, null);
            }
            return toolbarIcon.getConstantState();
        }
    }

    // if successful in getting icon, return it; otherwise, set button to use default drawable
    private Drawable.ConstantState updateButtonWithIconFromExternalActivity(
            int buttonId, ComponentName activityName, int fallbackDrawableId) {
        ImageView button = (ImageView) findViewById(buttonId);
        Drawable toolbarIcon = getExternalPackageToolbarIcon(activityName);

        if (button != null) {
            // If we were unable to find the icon via the meta-data, use a
            // generic one
            if (toolbarIcon == null) {
                button.setImageResource(fallbackDrawableId);
            } else {
                button.setImageDrawable(toolbarIcon);
            }
        }

        return toolbarIcon != null ? toolbarIcon.getConstantState() : null;

    }

    private void updateTextButtonWithDrawable(int buttonId, Drawable.ConstantState d) {
        TextView button = (TextView) findViewById(buttonId);
        button.setCompoundDrawables(d.newDrawable(getResources()), null, null, null);
    }

    private void updateButtonWithDrawable(int buttonId, Drawable.ConstantState d) {
        ImageView button = (ImageView) findViewById(buttonId);
        button.setImageDrawable(d.newDrawable(getResources()));
    }

    private void invalidatePressedFocusedStates(View container, View button) {
        if (container instanceof HolographicLinearLayout) {
            HolographicLinearLayout layout = (HolographicLinearLayout) container;
            layout.invalidatePressedFocusedStates();
        } else if (button instanceof HolographicImageView) {
            HolographicImageView view = (HolographicImageView) button;
            view.invalidatePressedFocusedStates();
        }
    }

    private boolean updateGlobalSearchIcon() {
        final View searchButtonContainer = findViewById(R.id.search_button_container);
        final ImageView searchButton = (ImageView) findViewById(R.id.search_button);
        final View searchDivider = findViewById(R.id.search_divider);
        final View voiceButtonContainer = findViewById(R.id.voice_button_container);
        final View voiceButton = findViewById(R.id.voice_button);

        final SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        ComponentName activityName = searchManager.getGlobalSearchActivity();
        //[hunter.zheng] 2012.2.4 force not show search bar
        if (activityName != null && ENABLE_SHOW_SEARCHBAR) {
            int coi = getCurrentOrientationIndexForGlobalIcons();
            sGlobalSearchIcon[coi] = updateButtonWithIconFromExternalActivity(
                    R.id.search_button, activityName, R.drawable.ic_home_search_normal_holo);
            if (searchDivider != null) searchDivider.setVisibility(View.VISIBLE);
            if (searchButtonContainer != null) searchButtonContainer.setVisibility(View.VISIBLE);
            searchButton.setVisibility(View.VISIBLE);
            invalidatePressedFocusedStates(searchButtonContainer, searchButton);
            return true;
        } else {
            // We disable both search and voice search when there is no global search provider
            if (searchDivider != null) searchDivider.setVisibility(View.GONE);
            if (searchButtonContainer != null) searchButtonContainer.setVisibility(View.GONE);
            if (voiceButtonContainer != null) voiceButtonContainer.setVisibility(View.GONE);
            searchButton.setVisibility(View.GONE);
            voiceButton.setVisibility(View.GONE);
            return false;
        }
    }

    private void updateGlobalSearchIcon(Drawable.ConstantState d) {
        final View searchButtonContainer = findViewById(R.id.search_button_container);
        final View searchButton = (ImageView) findViewById(R.id.search_button);
        updateButtonWithDrawable(R.id.search_button, d);
        invalidatePressedFocusedStates(searchButtonContainer, searchButton);
    }

    private boolean updateVoiceSearchIcon(boolean searchVisible) {
        final View searchDivider = findViewById(R.id.search_divider);
        final View voiceButtonContainer = findViewById(R.id.voice_button_container);
        final View voiceButton = findViewById(R.id.voice_button);

        // We only show/update the voice search icon if the search icon is enabled as well
        Intent intent = new Intent(RecognizerIntent.ACTION_WEB_SEARCH);
        ComponentName activityName = intent.resolveActivity(getPackageManager());
        if (searchVisible && activityName != null) {
            int coi = getCurrentOrientationIndexForGlobalIcons();
            sVoiceSearchIcon[coi] = updateButtonWithIconFromExternalActivity(
                    R.id.voice_button, activityName, R.drawable.ic_home_voice_search_holo);
            if (searchDivider != null) searchDivider.setVisibility(View.VISIBLE);
            if (voiceButtonContainer != null) voiceButtonContainer.setVisibility(View.VISIBLE);
            voiceButton.setVisibility(View.VISIBLE);
            invalidatePressedFocusedStates(voiceButtonContainer, voiceButton);
            return true;
        } else {
            if (searchDivider != null) searchDivider.setVisibility(View.GONE);
            if (voiceButtonContainer != null) voiceButtonContainer.setVisibility(View.GONE);
            voiceButton.setVisibility(View.GONE);
            return false;
        }
    }

    private void updateVoiceSearchIcon(Drawable.ConstantState d) {
        final View voiceButtonContainer = findViewById(R.id.voice_button_container);
        final View voiceButton = findViewById(R.id.voice_button);
        updateButtonWithDrawable(R.id.voice_button, d);
        invalidatePressedFocusedStates(voiceButtonContainer, voiceButton);
    }

    /**
     * Sets the app market icon
     */
    private void updateAppMarketIcon() {
        final View marketButton = findViewById(R.id.market_button);
        Intent intent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_MARKET);
        // Find the app market activity by resolving an intent.
        // (If multiple app markets are installed, it will return the ResolverActivity.)
        ComponentName activityName = intent.resolveActivity(getPackageManager());
        if (activityName != null) {
            int coi = getCurrentOrientationIndexForGlobalIcons();
            mAppMarketIntent = intent;
            sAppMarketIcon[coi] = updateTextButtonWithIconFromExternalActivity(
                    R.id.market_button, activityName, R.drawable.ic_launcher_market_holo);
            marketButton.setVisibility(View.GONE);
			marketButton.setEnabled(false);
            
        } else {
            // We should hide and disable the view so that we don't try and restore the visibility
            // of it when we swap between drag & normal states from IconDropTarget subclasses.
            marketButton.setVisibility(View.GONE);
            marketButton.setEnabled(false);
        }
    }

    private void updateAppMarketIcon(Drawable.ConstantState d) {
        updateTextButtonWithDrawable(R.id.market_button, d);
    }

    /**
     * Displays the shortcut creation dialog and launches, if necessary, the
     * appropriate activity.
     */
    private class CreateShortcut implements DialogInterface.OnClickListener,
            DialogInterface.OnCancelListener, DialogInterface.OnDismissListener,
            DialogInterface.OnShowListener {

        private AddAdapter mAdapter;

        Dialog createDialog() {
            mAdapter = new AddAdapter(Launcher.this);

            final AlertDialog.Builder builder = new AlertDialog.Builder(Launcher.this, 
                    AlertDialog.THEME_HOLO_DARK);
            builder.setAdapter(mAdapter, this);

            AlertDialog dialog = builder.create();
            dialog.setOnCancelListener(this);
            dialog.setOnDismissListener(this);
            dialog.setOnShowListener(this);

            return dialog;
        }

        public void onCancel(DialogInterface dialog) {
            mWaitingForResult = false;
            cleanup();
        }

        public void onDismiss(DialogInterface dialog) {
            mWaitingForResult = false;
            cleanup();
        }

        private void cleanup() {
            try {
                dismissDialog(DIALOG_CREATE_SHORTCUT);
            } catch (Exception e) {
                // An exception is thrown if the dialog is not visible, which is fine
            }
        }

        /**
         * Handle the action clicked in the "Add to home" dialog.
         */
        public void onClick(DialogInterface dialog, int which) {
            cleanup();

            AddAdapter.ListItem item = (AddAdapter.ListItem) mAdapter.getItem(which);
            switch (item.actionTag) {
                case AddAdapter.ITEM_APPLICATION: {
                    if (mAppsCustomizeTabHost != null) {
                        mAppsCustomizeTabHost.selectAppsTab();
                    }
                    showAllApps(true);
                    break;
                }
                case AddAdapter.ITEM_APPWIDGET: {
                    if (mAppsCustomizeTabHost != null) {
                        mAppsCustomizeTabHost.selectWidgetsTab();
                    }
                    showAllApps(true);
                    break;
                }
                case AddAdapter.ITEM_WALLPAPER: {
                    startWallpaper();
                    break;
                }
            }
        }

        public void onShow(DialogInterface dialog) {
            mWaitingForResult = true;
        }
    }

    /**
     * Receives notifications when system dialogs are to be closed.
     */
    private class CloseSystemDialogsIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            closeSystemDialogs();
        }
    }

    /**
     * Receives notifications whenever the appwidgets are reset.
     */
    private class AppWidgetResetObserver extends ContentObserver {
        public AppWidgetResetObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange) {
            onAppWidgetReset();
        }
    }

    /**
     * If the activity is currently paused, signal that we need to re-run the loader
     * in onResume.
     *
     * This needs to be called from incoming places where resources might have been loaded
     * while we are paused.  That is becaues the Configuration might be wrong
     * when we're not running, and if it comes back to what it was when we
     * were paused, we are not restarted.
     *
     * Implementation of the method from LauncherModel.Callbacks.
     *
     * @return true if we are currently paused.  The caller might be able to
     * skip some work in that case since we will come back again.
     */
    public boolean setLoadOnResume() {
        if (mPaused) {
            Log.i(TAG, "setLoadOnResume");
            mOnResumeNeedsLoad = true;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Implementation of the method from LauncherModel.Callbacks.
     */
    public int getCurrentWorkspaceScreen() {
        if (mWorkspace != null) {
            return mWorkspace.getCurrentPage();
        } else {
            return SCREEN_COUNT / 2;
        }
    }


    /**
     * Refreshes the shortcuts shown on the workspace.
     *
     * Implementation of the method from LauncherModel.Callbacks.
     */
    public void startBinding() {
        final Workspace workspace = mWorkspace;

        mWorkspace.clearDropTargets();
        int count = workspace.getChildCount();
        for (int i = 0; i < count; i++) {
            // Use removeAllViewsInLayout() to avoid an extra requestLayout() and invalidate().
            final CellLayout layoutParent = (CellLayout) workspace.getChildAt(i);
            layoutParent.removeAllViewsInLayout();
        }
        mWidgetsToAdvance.clear();
        if (mHotseat != null) {
            mHotseat.resetLayout();
        }
    }

    /**
     * Bind the items start-end from the list.
     *
     * Implementation of the method from LauncherModel.Callbacks.
     */
    public void bindItems(ArrayList<ItemInfo> shortcuts, int start, int end) {
        setLoadOnResume();

        final Workspace workspace = mWorkspace;
        for (int i=start; i<end; i++) {
            final ItemInfo item = shortcuts.get(i);

            // Short circuit if we are loading dock items for a configuration which has no dock
            if (item.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT &&
                    mHotseat == null) {
                continue;
            }

            switch (item.itemType) {
                case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
                case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
                    View shortcut = createShortcut((ShortcutInfo)item);
                    workspace.addInScreen(shortcut, item.container, item.screen, item.cellX,
                            item.cellY, 1, 1, false);
                    break;
                case LauncherSettings.Favorites.ITEM_TYPE_FOLDER:
                    FolderIcon newFolder = FolderIcon.fromXml(R.layout.folder_icon, this,
                            (ViewGroup) workspace.getChildAt(workspace.getCurrentPage()),
                            (FolderInfo) item, mIconCache);
                    workspace.addInScreen(newFolder, item.container, item.screen, item.cellX,
                            item.cellY, 1, 1, false);
                    break;
            }
        }
        workspace.requestLayout();
    }

    /**
     * Implementation of the method from LauncherModel.Callbacks.
     */
    public void bindFolders(HashMap<Long, FolderInfo> folders) {
        setLoadOnResume();
        sFolders.clear();
        sFolders.putAll(folders);
    }

    /**
     * Add the views for a widget to the workspace.
     *
     * Implementation of the method from LauncherModel.Callbacks.
     */
    public void bindAppWidget(LauncherAppWidgetInfo item) {
        setLoadOnResume();

        final long start = DEBUG_WIDGETS ? SystemClock.uptimeMillis() : 0;
        if (DEBUG_WIDGETS) {
            Log.d(TAG, "bindAppWidget: " + item);
        }
        final Workspace workspace = mWorkspace;

        final int appWidgetId = item.appWidgetId;
        final AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        if (DEBUG_WIDGETS) {
            Log.d(TAG, "bindAppWidget: id=" + item.appWidgetId + " belongs to component " + appWidgetInfo.provider);
        }

        item.hostView = mAppWidgetHost.createView(this, appWidgetId, appWidgetInfo);

        item.hostView.setAppWidget(appWidgetId, appWidgetInfo);
        item.hostView.setTag(item);

        workspace.addInScreen(item.hostView, item.container, item.screen, item.cellX,
                item.cellY, item.spanX, item.spanY, false);

        addWidgetToAutoAdvanceIfNeeded(item.hostView, appWidgetInfo);

        workspace.requestLayout();

        if (DEBUG_WIDGETS) {
            Log.d(TAG, "bound widget id="+item.appWidgetId+" in "
                    + (SystemClock.uptimeMillis()-start) + "ms");
        }
    }

    /**
     * Callback saying that there aren't any more items to bind.
     *
     * Implementation of the method from LauncherModel.Callbacks.
     */
    public void finishBindingItems() {
        setLoadOnResume();

        if (mSavedState != null) {
            if (!mWorkspace.hasFocus()) {
                mWorkspace.getChildAt(mWorkspace.getCurrentPage()).requestFocus();
            }
            mSavedState = null;
        }

        if (mSavedInstanceState != null) {
            super.onRestoreInstanceState(mSavedInstanceState);
            mSavedInstanceState = null;
        }

        mWorkspaceLoading = false;

        // If we received the result of any pending adds while the loader was running (e.g. the
        // widget configuration forced an orientation change), process them now.
        for (int i = 0; i < sPendingAddList.size(); i++) {
            completeAdd(sPendingAddList.get(i));
        }
        sPendingAddList.clear();

        // Update the market app icon as necessary (the other icons will be managed in response to
        // package changes in bindSearchablesChanged()
        updateAppMarketIcon();

        mWorkspace.post(mBuildLayersRunnable);
    }

    @Override
    public void bindSearchablesChanged() {
        boolean searchVisible = updateGlobalSearchIcon();
        boolean voiceVisible = updateVoiceSearchIcon(searchVisible);
        mSearchDropTargetBar.onSearchPackagesChanged(searchVisible, voiceVisible);
    }

    /**
     * Add the icons for all apps.
     *
     * Implementation of the method from LauncherModel.Callbacks.
     */
    public void bindAllApplications(final ArrayList<ApplicationInfo> apps) {
        // Remove the progress bar entirely; we could also make it GONE
        // but better to remove it since we know it's not going to be used
        View progressBar = mAppsCustomizeTabHost.
            findViewById(R.id.apps_customize_progress_bar);
        if (progressBar != null) {
            ((ViewGroup)progressBar.getParent()).removeView(progressBar);
        }
        // We just post the call to setApps so the user sees the progress bar
        // disappear-- otherwise, it just looks like the progress bar froze
        // which doesn't look great
        mAppsCustomizeTabHost.post(new Runnable() {
            public void run() {
                if (mAppsCustomizeContent != null) {
                    mAppsCustomizeContent.setApps(apps);
                }
            }
        });
    }

    /**
     * A package was installed.
     *
     * Implementation of the method from LauncherModel.Callbacks.
     */
    public void bindAppsAdded(ArrayList<ApplicationInfo> apps) {
        setLoadOnResume();
        removeDialog(DIALOG_CREATE_SHORTCUT);

        if (mAppsCustomizeContent != null) {
            mAppsCustomizeContent.addApps(apps);
        }
    }

    /**
     * A package was updated.
     *
     * Implementation of the method from LauncherModel.Callbacks.
     */
    public void bindAppsUpdated(ArrayList<ApplicationInfo> apps) {
        setLoadOnResume();
        removeDialog(DIALOG_CREATE_SHORTCUT);
        if (mWorkspace != null) {
            mWorkspace.updateShortcuts(apps);
        }

        if (mAppsCustomizeContent != null) {
            mAppsCustomizeContent.updateApps(apps);
        }
    }

    /**
     * A package was uninstalled.
     *
     * Implementation of the method from LauncherModel.Callbacks.
     */
    public void bindAppsRemoved(ArrayList<ApplicationInfo> apps, boolean permanent) {
        removeDialog(DIALOG_CREATE_SHORTCUT);
        if (permanent) {
            mWorkspace.removeItems(apps);
        }

        if (mAppsCustomizeContent != null) {
            mAppsCustomizeContent.removeApps(apps);
        }

        // Notify the drag controller
        mDragController.onAppsRemoved(apps, this);
    }

    /**
     * A number of packages were updated.
     */
    public void bindPackagesUpdated() {
        if (mAppsCustomizeContent != null) {
            mAppsCustomizeContent.onPackagesUpdated();
        }
    }

    private int mapConfigurationOriActivityInfoOri(int configOri) {
        final Display d = getWindowManager().getDefaultDisplay();
        int naturalOri = Configuration.ORIENTATION_LANDSCAPE;
        switch (d.getRotation()) {
        case Surface.ROTATION_0:
        case Surface.ROTATION_180:
            // We are currently in the same basic orientation as the natural orientation
            naturalOri = configOri;
            break;
        case Surface.ROTATION_90:
        case Surface.ROTATION_270:
            // We are currently in the other basic orientation to the natural orientation
            naturalOri = (configOri == Configuration.ORIENTATION_LANDSCAPE) ?
                    Configuration.ORIENTATION_PORTRAIT : Configuration.ORIENTATION_LANDSCAPE;
            break;
        }

        int[] oriMap = {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT,
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
        };
        // Since the map starts at portrait, we need to offset if this device's natural orientation
        // is landscape.
        int indexOffset = 0;
        if (naturalOri == Configuration.ORIENTATION_LANDSCAPE) {
            indexOffset = 1;
        }
        return oriMap[(d.getRotation() + indexOffset) % 4];
    }

    public void lockScreenOrientationOnLargeUI() {
        if (LauncherApplication.isScreenLarge()) {
            setRequestedOrientation(mapConfigurationOriActivityInfoOri(getResources()
                    .getConfiguration().orientation));
        }
    }
    public void unlockScreenOrientationOnLargeUI() {
        if (LauncherApplication.isScreenLarge()) {
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                }
            }, mRestoreScreenOrientationDelay);
        }
    }

    /* Cling related */
    private static final String PREFS_KEY = "com.haier.launcher2.prefs";
    private boolean isClingsEnabled() {
        // disable clings when running in a test harness
        if(ActivityManager.isRunningInTestHarness()) return false;
        if (ENABLE_SHOW_CLING)
        	return true;
        else
        	return false;//[Hunter.zheng] 2012.2.3 force disable clings
    }
    private Cling initCling(int clingId, int[] positionData, boolean animate, int delay) {
        Cling cling = (Cling) findViewById(clingId);
        if (cling != null) {
            cling.init(this, positionData);
            cling.setVisibility(View.VISIBLE);
            cling.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            if (animate) {
                cling.buildLayer();
                cling.setAlpha(0f);
                cling.animate()
                    .alpha(1f)
                    .setInterpolator(new AccelerateInterpolator())
                    .setDuration(SHOW_CLING_DURATION)
                    .setStartDelay(delay)
                    .start();
            } else {
                cling.setAlpha(1f);
            }
        }
        return cling;
    }
    private void dismissCling(final Cling cling, final String flag, int duration) {
        if (cling != null) {
            ObjectAnimator anim = ObjectAnimator.ofFloat(cling, "alpha", 0f);
            anim.setDuration(duration);
            anim.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    cling.setVisibility(View.GONE);
                    cling.cleanup();
                    SharedPreferences prefs =
                        getSharedPreferences("com.haier.launcher2.prefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(flag, true);
                    editor.commit();
                };
            });
            anim.start();
        }
    }
    private void removeCling(int id) {
        final View cling = findViewById(id);
        if (cling != null) {
            final ViewGroup parent = (ViewGroup) cling.getParent();
            parent.post(new Runnable() {
                @Override
                public void run() {
                    parent.removeView(cling);
                }
            });
        }
    }
    public void showFirstRunWorkspaceCling() {
        // Enable the clings only if they have not been dismissed before
        SharedPreferences prefs =
            getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        if (isClingsEnabled() && !prefs.getBoolean(Cling.WORKSPACE_CLING_DISMISSED_KEY, false)) {
            initCling(R.id.workspace_cling, null, false, 0);
        } else {
            removeCling(R.id.workspace_cling);
        }
    }
    public void showFirstRunAllAppsCling(int[] position) {
        // Enable the clings only if they have not been dismissed before
        SharedPreferences prefs =
            getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        if (isClingsEnabled() && !prefs.getBoolean(Cling.ALLAPPS_CLING_DISMISSED_KEY, false)) {
            initCling(R.id.all_apps_cling, position, true, 0);
        } else {
            removeCling(R.id.all_apps_cling);
        }
    }
    public Cling showFirstRunFoldersCling() {
        // Enable the clings only if they have not been dismissed before
        SharedPreferences prefs =
            getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        Cling cling = null;
        if (isClingsEnabled() && !prefs.getBoolean(Cling.FOLDER_CLING_DISMISSED_KEY, false)) {
            cling = initCling(R.id.folder_cling, null, true, 0);
        } else {
            removeCling(R.id.folder_cling);
        }
        return cling;
    }
    public boolean isFolderClingVisible() {
        Cling cling = (Cling) findViewById(R.id.folder_cling);
        if (cling != null) {
            return cling.getVisibility() == View.VISIBLE;
        }
        return false;
    }
    public void dismissWorkspaceCling(View v) {
        Cling cling = (Cling) findViewById(R.id.workspace_cling);
        dismissCling(cling, Cling.WORKSPACE_CLING_DISMISSED_KEY, DISMISS_CLING_DURATION);
    }
    public void dismissAllAppsCling(View v) {
        Cling cling = (Cling) findViewById(R.id.all_apps_cling);
        dismissCling(cling, Cling.ALLAPPS_CLING_DISMISSED_KEY, DISMISS_CLING_DURATION);
    }
    public void dismissFolderCling(View v) {
        Cling cling = (Cling) findViewById(R.id.folder_cling);
        dismissCling(cling, Cling.FOLDER_CLING_DISMISSED_KEY, DISMISS_CLING_DURATION);
    }

    /**
     * Prints out out state for debugging.
     */
    public void dumpState() {
        Log.d(TAG, "BEGIN launcher2 dump state for launcher " + this);
        Log.d(TAG, "mSavedState=" + mSavedState);
        Log.d(TAG, "mWorkspaceLoading=" + mWorkspaceLoading);
        Log.d(TAG, "mRestoring=" + mRestoring);
        Log.d(TAG, "mWaitingForResult=" + mWaitingForResult);
        Log.d(TAG, "mSavedInstanceState=" + mSavedInstanceState);
        Log.d(TAG, "sFolders.size=" + sFolders.size());
        mModel.dumpState();

        if (mAppsCustomizeContent != null) {
            mAppsCustomizeContent.dumpState();
        }
        Log.d(TAG, "END launcher2 dump state");
    }

    @Override
    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        super.dump(prefix, fd, writer, args);
        writer.println(" ");
        writer.println("Debug logs: ");
        for (int i = 0; i < sDumpLogs.size(); i++) {
            writer.println("  " + sDumpLogs.get(i));
        }
    }
    
    public static void mplayerPrepare(){
    	/*
		mMediaPlayer = new MediaPlayer();
		if (mMediaPlayer != null) {
            try {
                String filePath = "/system/media/audio/ui/Effect_Tick.ogg";
                mMediaPlayer.setDataSource(filePath);
                mMediaPlayer.setAudioStreamType(AudioSystem.STREAM_SYSTEM);
                mMediaPlayer.prepare();
                int SOUND_EFFECT_VOLUME_DB = SystemProperties.getInt(
                        "ro.config.sound_fx_volume", -20);
                float volFloat = (float)Math.pow(10, SOUND_EFFECT_VOLUME_DB/20);
                mMediaPlayer.setVolume(volFloat, volFloat);
                mMediaPlayer.setOnErrorListener(new OnErrorListener() {
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        if (mp != null) {
                            try {
                                mp.stop();
                                mp.release();
                            } catch (IllegalStateException ex) {
                                Log.w(TAG, "MediaPlayer IllegalStateException: "+ex);
                            }
                        }
                        return true;
                    }
                });
            } catch (IOException ex) {
                Log.w(TAG, "MediaPlayer IOException: "+ex);
            } catch (IllegalArgumentException ex) {
                Log.w(TAG, "MediaPlayer IllegalArgumentException: "+ex);
            } catch (IllegalStateException ex) {
                Log.w(TAG, "MediaPlayer IllegalStateException: "+ex);
            }
        }*/
    }
    
    public static void playTone() {
    	if (mDTMFToneEnabled){
	    	// TODO 播放按键声音
	    	if (mMediaPlayer == null){
	    		mplayerPrepare();
	    	}
	    	
	    	/* [2012-05-09] add by weijh 增加播放器的异常捕获 */
			try {
				if (null != mMediaPlayer) {
					mMediaPlayer.start();
				}
			} catch (IllegalStateException e) {
				mMediaPlayer = null;
			}
    	}
	}

    private void setKeyAudio(){

    	try {
			//mDTMFToneEnabled = Settings.System.getInt(getContentResolver(), "keyaudio", 1) > 0;
			mDTMFToneEnabled = Settings.System.getInt(getContentResolver(), "keyaudio") > 0;
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			mDTMFToneEnabled = false;
		}
    	Log.i(TAG, "cxj =======mDTMFToneEnabled========"+mDTMFToneEnabled);
    	//获取系统参数“按键操作音”是否开启
    	if (mMediaPlayer == null) {
    		mplayerPrepare();
    	}
	}
    
    private void initRtkAtvApp() {
    	int mtime = 500;
		// init atv area
		mRtkAtvView.setFocusable(false);
		mRtkAtvView.setFocusableInTouchMode(false);
		mRtkAtvView.setVisibility(View.GONE);
		
		//Log.e(TAG,"Old mCurLiveSource = "+mCurLiveSource);
		//Log.e(TAG,"mTvManager.getCurSourceType() = "+mTvManager.getCurSourceType());
		if (mCurLiveSource == -1
				|| mTvManager.getCurSourceType() == TvManager.SOURCE_PLAYBACK
				|| mTvManager.getCurSourceType() == TvManager.SOURCE_OSD) {
			//Log.e(TAG,"mCurLiveSource = "+mCurLiveSource);
			mCurLiveSource = mTvManager.getCurLiveSource();
			mTvManager.setSource(mCurLiveSource);
			mtime = 3000;
		}
		Log.d("mtime","mtime = "+mtime);
		setupTvSmallWindow(false);
		setupTvSmallWindowBg(mtime, true);
	}
    /**myfun dialog modify by hushuai 20140107*/
    private Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case START_COUNT:
				++mCount;
				System.out.println("start count..........." + mCount);
				if (mCount == 10) {
					if (mMyFunDialog != null) {
						mMyFunDialog.dismiss();
					}
					mCount = 0;
					mTimer.cancel();
				}
				break;
			}
		}
	};
	/**myfun dialog TimerTask modify by hushuai 20140107*/
    private class MyTimerTask extends TimerTask {
		@Override
		public void run() {
			System.out.println("mytimertask.............");
			Message message = new Message();
			message.what = START_COUNT;
			myHandler.sendMessage(message);
		}
	}
    /**myfun dialog getCurrentAppPackage modify by hushuai 20140107*/
    private String getCurrentAppPackage() {
		ActivityManager am = (ActivityManager) Launcher.this.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> taskList = am.getRunningTasks(1);
		if (taskList.size() > 0) {
			ComponentName cn = taskList.get(0).topActivity;
			String packageName = cn.getPackageName();
			Log.d("hzk", "statusbar packagename:" + packageName);
			return packageName;
		} else {
			return null;
		}
	}
    /**myfun dialog MyFunDialog modify by hushuai 20140107*/
	public class MyFunDialog extends Dialog {
		public MyFunDialog(Context context) {
			super(context,android.R.style.Theme_Light_NoTitleBar);
		}
	}
	/**myfun dialog isMyFunInstall function modify by hushuai 20140107*/
	private boolean isMyFunInstall() {
		boolean myFunFlag = false;
		List<PackageInfo> packageList = Launcher.this.getPackageManager().getInstalledPackages(0);
		for (int i = 0; i < packageList.size(); i++) {
			if (packageList.get(i).packageName.equals("com.hrtvbic.usb.S6A801")) {
				myFunFlag = true;
			}
		}
		return myFunFlag;
	}
private void findMyfunView(){
		
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
		intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
		intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
		
		intentFilter.addDataScheme("file");
		intentFilter.setPriority(Integer.MAX_VALUE);
		Launcher.this.registerReceiver(mTabletReceiver, intentFilter);
		
		mMyFunView = (View) View.inflate(Launcher.this, R.layout.usb_myfun, null);
		Log.i("hzk", "after View.inflate()");
		mEnterMyFun = (Button) mMyFunView.findViewById(R.id.btn_ok);
		mCancleMyFun = (Button) mMyFunView.findViewById(R.id.btn_cancle);
		mTextFindUsb = (TextView)mMyFunView.findViewById(R.id.find_usb);
		mTextMyFun = (TextView) mMyFunView.findViewById(R.id.enter_myfun);
		
		mEnterMyFun.setTextSize(30);
		mCancleMyFun.setTextSize(28);
		mTextFindUsb.setTextSize(28);
		mTextMyFun.setTextSize(32);

		mEnterMyFun.setText(Launcher.this.getString(R.string.description_ok));
		mCancleMyFun.setText(Launcher.this.getString(R.string.description_cancle));
		mTextFindUsb.setText(Launcher.this.getString(R.string.description_find_usb));
		mTextMyFun.setText(Launcher.this.getString(R.string.description_myfun));
		
		mTextFindUsb.setTextColor(Color.rgb(200, 200, 200));
		mTextMyFun.setTextColor(Color.rgb(255, 255, 255));
		Log.i("hzk", "befor new MyFunDialog()");
		mMyFunDialog = new MyFunDialog(Launcher.this);
		Log.i("hzk", "after new MyFunDialog()");
		mEnterMyFun.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("zjf", "enter  onClick.......");
				if (mMyFunDialog != null) {
					mMyFunDialog.dismiss();
				}
				if (!isMyFunInstall()) {
					Toast.makeText(Launcher.this, Launcher.this.getResources().getString(R.string.myfun_uninstall),
							Toast.LENGTH_LONG).show();
				} else {
					ComponentName comp = new ComponentName("com.hrtvbic.usb.S6A801", "com.hrtvbic.usb.S6A801.ui.main.MainActivity");
					Intent intent = new Intent();
					intent.setComponent(comp);
					intent.setAction("android.intent.action.VIEW");
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					Launcher.this.startActivity(intent);
				}
			}
		});
		Log.i("hzk", "after View.inflate() 1");
		mEnterMyFun.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				mCount = 0;   //hzk add 
				Log.d("zjf", "enter focus........." + hasFocus);
				if (hasFocus) {
					mEnterMyFun.setTextColor(Color.rgb(255, 255, 255));
					mEnterMyFun.setBackgroundResource(R.drawable.pop1_btn_sel);
				} else {
					mEnterMyFun.setTextColor(Color.rgb(200, 200, 200));
					mEnterMyFun.setBackgroundResource(R.drawable.pop1_btn_nor);
				}
			}
		});

		mCancleMyFun.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// add by weijh, 鍙戞柟鎾�鐭ヤ笉杩涘叆myfun
				Intent tvIntent = new Intent("com.mstar.tv.enterUSBorNot");
				tvIntent.putExtra("isEnterUSB", false);
				Launcher.this.sendBroadcast(tvIntent);

				Log.d("zjf", "cancle  onClick.......");
				if (mMyFunDialog != null) {
					mMyFunDialog.dismiss();
				}
			}
		});

		mCancleMyFun.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				mCount = 0;   // hzk add
				Log.d("zjf", "cancle focus........." + hasFocus);
				if (hasFocus) {
					mCancleMyFun.setTextColor(Color.rgb(255, 255, 255));
					mCancleMyFun.setBackgroundResource(R.drawable.pop1_btn_sel);
				} else {
					mCancleMyFun.setTextColor(Color.rgb(200, 200, 200));
					mCancleMyFun.setBackgroundResource(R.drawable.pop1_btn_nor);
				}
			}
		});
		
		
		WindowManager.LayoutParams myFunDialogParam;

		myFunDialogParam = mMyFunDialog.getWindow().getAttributes();
		myFunDialogParam.width = 540;
		myFunDialogParam.height = 330;
		myFunDialogParam.gravity = Gravity.CENTER;
//		myFunDialogParam.type = WindowManager.LayoutParams.TYPE_STATUS_BAR_PANEL;
		myFunDialogParam.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
				| WindowManager.LayoutParams.FLAG_SPLIT_TOUCH | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
				| WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
		myFunDialogParam.setTitle("myFun");
		mMyFunDialog.getWindow().setAttributes(myFunDialogParam);
		mMyFunDialog.getWindow().setFormat(PixelFormat.TRANSPARENT);
		mMyFunDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		mMyFunDialog.setContentView(mMyFunView, new ViewGroup.LayoutParams(450,300));
		mMyFunDialog.getWindow().setBackgroundDrawable(null);
		
	}

    /*
    private void play() {  
        try {  
        	TvManager mTv = (TvManager) getSystemService("tv");
        	RtkAtvViewEx mRtkAtvView = (RtkAtvViewEx) findViewById(R.id.atv_surfaceView);
            mediaPlayer.reset();  
            mediaPlayer  
            .setAudioStreamType(AudioManager.STREAM_MUSIC);  
            //设置需要播放的视频  
            mediaPlayer.setDataSource("tv");  
            //把视频画面输出到SurfaceView  
            mediaPlayer.setDisplay(mRtkAtvView.getHolder());  
            mediaPlayer.prepare();  
            //播放  
            mediaPlayer.start();          
        } catch (Exception e) {  
            // TODO: handle exception  
        }  
    }*/
}
interface LauncherTransitionable {
    // return true if the callee will take care of start the animation by itself
    boolean onLauncherTransitionStart(Launcher l, Animator animation, boolean toWorkspace);
    void onLauncherTransitionEnd(Launcher l, Animator animation, boolean toWorkspace);
}




