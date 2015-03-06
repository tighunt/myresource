package com.android.settings.applications;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.settings.R;
import com.android.settings.util.Tools;

/**
 * Show a application information
 * 
 * @author ducj
 * @date 2011-11-3 10:32:38 am
 * @since 1.0
 */
public class AppDetailInforActivity extends Activity {
	// private static final String TAG = "AppDetailInforActivity";
	// force stop
	final static int FORCE_STOP = 0;
	// uninstall
	final static int UNINSTALL = 1;
	// clear data
	final static int CLEAR_DATA = 2;
	// clear data success
	private static final int OP_SUCCESSFUL = 3;
	// clear data failure
	private static final int OP_FAILED = 4;
	// uninstall donw
	private final int UNINSTALL_COMPLETE = 5;
	// uninstall success
	public final static int SUCCEEDED = 1;
	// uninstall failure
	public final static int FAILED = 0;
	// check the app is or not running
	public  boolean isRunning = false;
	// check the app has data
	public boolean isHasData = false;
	// private boolean uninstallSuccess = false;

	// check the state
	// private int state = -1;

	protected AppInforViewHolder mAppInforViewHolder;
	private AppDetailInforListener mAppDetailInforListener;

	private AppInfor mAppInfor;
	private ClearUserDataObserver mClearDataObserver;
	// package name
	private String packageName;
	private ActivityManager mActivityManager;

	@SuppressWarnings("unused")
	private volatile int mResultCode = -1;

	// zhoujf 2012-8-24 屏蔽  SDCARED 
	//private PackageInfo mPackageInfo;
	//private PackageManager mPackageManager;
	// 用于记录当前应用程序存放的位置: 1表示此应用程序安装在SD卡中;2表示此应用程序在TV内存中
	//private int mMoveFlags;
	//private PackageMoveObserver mPackageMoveObserver;
	//private long freeStorage = 0;
	//private final int MOVE_APPLICATION = 3; // move app flag,to control focus
	//private boolean mMoveFlag = false;
	//private final int MSG_SDCARD_NOT_EXIST = 101; // sdcard is not exist
	//private final int MSG_APPLICATION_MOVE_FAIL = 11; // app move fail
	//private final int MSG_SDCARD_FREE_SIZE_ERROR = 12;
	//private final int MSG_APP_MOVE_FLAG = 13;
	//private final int MSG_CHECK_APP_POSITION = 14; // check app position
	
	public  final int MSG_NO_STOP = 15;
	public final int MSG_NO_CLEAR_DATA = 16;

	

	public  Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			// If the activity is gone, don't process any more messages.
			if (isFinishing()) {
				return;
			}
			switch (msg.what) {
			case CLEAR_DATA:
				processClearMsg(msg);
				break;
			case UNINSTALL_COMPLETE:

				mResultCode = msg.arg1;
				final String packageName = (String) msg.obj;
				Log.d("zjf", "packageName ++" + packageName);

				// Update the status text
				final int statusText;
				switch (msg.arg1) {
				case PackageManager.DELETE_SUCCEEDED:
					statusText = R.string.uninstall_done;
					mAppDetailInforListener.setInvisible();
					break;
				default:
					statusText = R.string.uninstall_failure;
					break;
				}
				mAppInforViewHolder.uninstall.setText(statusText);

				// Hide the progress bar; Show the ok button
				mAppInforViewHolder.mProgressBar.setVisibility(View.INVISIBLE);
				break;
		    /* zhoujf 2012-8-24 屏蔽  SDCARED
			case MSG_APPLICATION_MOVE_FAIL:

				Toast.makeText(AppDetailInforActivity.this, getResources().getString(R.string.app_move_fail),
						Toast.LENGTH_LONG).show();

				dismissDialog(MSG_APP_MOVE_FLAG);

				break;
			case MSG_SDCARD_NOT_EXIST:
				Toast.makeText(AppDetailInforActivity.this, getResources().getString(R.string.sdcard_not_exist),
						Toast.LENGTH_LONG).show();
				break;
			case MSG_CHECK_APP_POSITION:

				checkAppLocation();

				break;
			
			case MSG_APP_MOVE_FLAG:

				mAppInforViewHolder.mMoveApp.setText(getResources().getString(R.string.app_moving));

				showDialog(MSG_APP_MOVE_FLAG);

				break;
			 
			case MSG_SDCARD_FREE_SIZE_ERROR:
				Toast.makeText(AppDetailInforActivity.this, getResources().getString(R.string.sdcard_free_size_error),
						Toast.LENGTH_LONG).show();
				break;
			*/
			case MSG_NO_STOP:
				Toast.makeText(AppDetailInforActivity.this, getResources().getString(R.string.msg_no_stop),
						Toast.LENGTH_SHORT).show();
				break;
			case MSG_NO_CLEAR_DATA:
				Toast.makeText(AppDetailInforActivity.this, getResources().getString(R.string.msg_no_clear_data),
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mAppInfor = (AppInfor) getIntent().getExtras().getParcelable("app_infor");

		setContentView(R.layout.application_information);

		findViews();

		registerListeners();
	}

	/**
	 * the component initialized
	 */
	private void findViews() {

		mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

		mAppInforViewHolder = new AppInforViewHolder(AppDetailInforActivity.this);
		
		initData();

		packageName = mAppInfor.getPackageName();
		Log.d("zjf", "findViews().packageName:" + packageName);

		isRunning = isRunning(packageName);
		Log.d("zjf", "findViews().isRunning:" + isRunning);

		isHasData = getData();
		Log.d("zjf", "findViews().isHasData:" + isHasData);

		// zhoujf 2012-8-24 屏蔽  SDCARED
		//mPackageManager = this.getPackageManager();
		//mHandler.sendEmptyMessage(MSG_CHECK_APP_POSITION);
	}
	
	/**
	 * initialized data
	 */
	@SuppressWarnings("deprecation")
	private void initData() {

	    Log.d("**********initData*************", "here is initData ");
	    
        Log.d("------mAppInfor getAppName 2----------", "  " + mAppInfor.getAppName());
        Log.d("------mAppInfor getAppSize 2----------", "  " + mAppInfor.getAppSize());
        Log.d("------mAppInfor getDataSize 2----------", "  " + mAppInfor.getDataSize());
        Log.d("------mAppInfor getCacheSize 2----------", "  " + mAppInfor.getCacheSize());
        Log.d("------mAppInfor getPackageName 2----------", "  " + mAppInfor.getPackageName());
        Log.d("------mAppInfor getTotalSize 2----------", "  " + mAppInfor.getTotalSize());
        Log.d("------mAppInfor getVersionName 2----------", "  " + mAppInfor.getVersionName());
        Log.d("------mAppInfor getVersionCode 2 ----------", "  " + mAppInfor.getVersionCode());
        
		mAppInforViewHolder.app_name_tv.setText(mAppInfor.getAppName());
		mAppInforViewHolder.app_version.setText(mAppInfor.getVersionName());
		mAppInforViewHolder.app_total.setText(formateFileSize(mAppInfor.getTotalSize()));
		mAppInforViewHolder.app_size.setText(formateFileSize(mAppInfor.getAppSize()));
		mAppInforViewHolder.app_data.setText(formateFileSize(mAppInfor.getDataSize()));
		mAppInforViewHolder.app_cache.setText(formateFileSize(mAppInfor.getCacheSize()));

		byte[] bitmap = mAppInfor.getBitmap();
		Bitmap bm = Tools.byte2Bitmap(bitmap);
		BitmapDrawable bd = new BitmapDrawable(bm);
		mAppInforViewHolder.app_icon_iv.setBackgroundDrawable(bd);
	}

	/** 
	 * zhoujf 2012-8-24 屏蔽  SDCARED
	 * 
	private void checkAppLocation() {
		try {
			mPackageInfo = mPackageManager.getPackageInfo(mAppInfor.getPackageName(), 0);
			mMoveFlags = (mPackageInfo.applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0 ? PackageManager.MOVE_INTERNAL
					: PackageManager.MOVE_EXTERNAL_MEDIA;
			if (mMoveFlags == 1) {
				mAppInforViewHolder.mMoveApp.setText(getResources().getString(R.string.app_move_to_tv));
			} 
			else if (mMoveFlags == 2) {
				mAppInforViewHolder.mMoveApp.setText(getResources().getString(R.string.app_move_to_sdcard));
			}

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	*/

	/**
	 * 
	 * initialized
     */
	private void registerListeners() {

		mAppDetailInforListener = new AppDetailInforListener(this, mAppInforViewHolder);
		
		/* zhoujf 2012-8-24 屏蔽  SDCARED
		mAppInforViewHolder.mMoveApp.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				mAppDetailInforListener.changeViewSelected(3); // 2021-04-21
				
				if (mPackageMoveObserver == null) {
					mPackageMoveObserver = new PackageMoveObserver();
				}

				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

					// 移至sdcard
					if (mMoveFlags == 2) {

						freeStorage = getSdcardFreeSizes();

						if (freeStorage < mAppInfor.getTotalSize()) {

							Log.d("zjf", "SDCard_剩余空间小于此程序的大小");

							mHandler.sendEmptyMessage(MSG_SDCARD_FREE_SIZE_ERROR);
						} else {
							mMoveFlag = true;
							mHandler.sendEmptyMessage(MSG_APP_MOVE_FLAG);
							if (mPackageManager != null) {
								mPackageManager.movePackage(mAppInfor.getPackageName(), mPackageMoveObserver,
										mMoveFlags);
							}
						}
					}
					// 移至内存
					else {
						mMoveFlag = true;
						mHandler.sendEmptyMessage(MSG_APP_MOVE_FLAG);
						if (mPackageManager != null) {
							mPackageManager.movePackage(mAppInfor.getPackageName(), mPackageMoveObserver, mMoveFlags);
						}
					}

				} else {
					mHandler.sendEmptyMessage(MSG_SDCARD_NOT_EXIST);
				}

			}
		});
		*/
	}
	

	/**
	 * zhoujf 2012-8-24 屏蔽  SDCARED
	 * 
	class PackageMoveObserver extends IPackageMoveObserver.Stub {

		@SuppressWarnings("deprecation")
		@Override
		public void packageMoved(String packageName, int returnCode) throws RemoteException {

			mMoveFlag = false;

			if (returnCode == PackageManager.MOVE_SUCCEEDED) {

				mHandler.sendEmptyMessage(MSG_CHECK_APP_POSITION);

				dismissDialog(MSG_APP_MOVE_FLAG);
			} else {
				mHandler.sendEmptyMessage(MSG_APPLICATION_MOVE_FAIL);
			}
		}
	}
	*/

	/**
	 * zhoujf 2012-8-24 屏蔽  SDCARED
	 * 获取已使用的内部存储空间大小

	private long getSdcardFreeSizes() {

		Log.d("zjf", "存在SDCard_取SDCard剩余大小......");

		StatFs mSDCardFileStats = new StatFs(Environment.getExternalStorageDirectory().toString());

		mSDCardFileStats.restat(Environment.getExternalStorageDirectory().toString());

		Log.d("zjf", "存在SDCard Path:" + Environment.getExternalStorageDirectory().toString());

		try {

			freeStorage = (long) mSDCardFileStats.getAvailableBlocks() * mSDCardFileStats.getBlockSize();

		} catch (IllegalArgumentException e) {

			Log.d("zjf", "取SDCard剩余大小出错了......:" + e.getLocalizedMessage());

		}

		return freeStorage;
	}
	*/

	@Override
	protected Dialog onCreateDialog(int id) {

		Log.d("zjf", "AppDetailInforActivity.onCreateDialog******id:" + id);

		switch (id) {
		case FORCE_STOP:

			Log.d("zjf", "AppDetailInforActivity.onCreateDialog.FORCE_STOP******");

		

				// forece stop
				return new AlertDialog.Builder(AppDetailInforActivity.this).setTitle(R.string.force_stop)
						.setMessage(R.string.force_close_tips)
						.setPositiveButton(R.string.ok, new AlertDialog.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {

								Log.d("zjf", "AppDetailInforActivity.onCreateDialog.FORCE_STOP.onClick.OK******");

								forceStopPackage(packageName);
								isRunning = false;
								// focusChange();

							}
						}).setNegativeButton(R.string.cancle, new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {

								Log.d("zjf", "AppDetailInforActivity.onCreateDialog.FORCE_STOP.onClick.cancle******");

								dialog.dismiss();
							}
						}).show();

			

		case UNINSTALL:

			Log.d("zjf", "AppDetailInforActivity.onCreateDialog.UNINSTALL******");

			// uninstall
			return new AlertDialog.Builder(AppDetailInforActivity.this).setTitle(R.string.uninstall_app)
					.setMessage(R.string.uninstall_app_tips)
					.setPositiveButton(R.string.ok, new AlertDialog.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							Log.d("zjf", "AppDetailInforActivity.onCreateDialog.UNINSTALL.onClick.OK******");

							uninstallPkg(packageName);
							mAppInforViewHolder.uninstall.setText(getString(R.string.uninstalling));
							mAppInforViewHolder.mProgressBar.setVisibility(View.VISIBLE);
							mAppInforViewHolder.uninstall.setVisibility(View.VISIBLE);
						}
					}).setNegativeButton(R.string.cancle, new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							Log.d("zjf", "AppDetailInforActivity.onCreateDialog.UNINSTALL.onClick.cancle******");
							dialog.dismiss();
						}
					}).show();

		case CLEAR_DATA:

			Log.d("zjf", "AppDetailInforActivity.onCreateDialog.CLEAR_DATA******");

				// clear data
				return new AlertDialog.Builder(AppDetailInforActivity.this).setTitle(R.string.delete)
						.setMessage(R.string.delete_tips)
						.setPositiveButton(R.string.ok, new AlertDialog.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {

								Log.d("zjf", "AppDetailInforActivity.onCreateDialog.CLEAR_DATA.onClick.OK******");

								initiateClearUserData();
								isHasData=false;

							}
						}).setNegativeButton(R.string.cancle, new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								Log.d("zjf", "AppDetailInforActivity.onCreateDialog.CLEAR_DATA.onClick.cancle******");
								dialog.dismiss();
							}
						}).show(); 

		}

		/**
		 * zhoujf 2012-8-24 屏蔽 SDCARED case MSG_APP_MOVE_FLAG: {
		 * 
		 * Log.d("zjf", "AppDetailInforActivity.onCreateDialog.MSG_APP_MOVE_FLAG******");
		 * 
		 * ProgressDialog dialog = new ProgressDialog(this);
		 * dialog.setMessage(getResources().getString(R.string.app_moving) + "......"); //
		 * dialog.setIndeterminate(true); dialog.setCancelable(false); return dialog; }
		 */
		return null;

	}

	/**
	 * check current application is running
	 * 
	 * @return
	 */
	private boolean isRunning(String packageName) {

		boolean isStarted = false;

		try {
			int intGetTastCounter = 1000;

			mActivityManager.getRunningTasks(intGetTastCounter);
			List<ActivityManager.RunningAppProcessInfo> mRunningApp = mActivityManager.getRunningAppProcesses();
			Log.d("zjf", "RunningTaskInfo size:" + mRunningApp.size());
			for (ActivityManager.RunningAppProcessInfo amApp : mRunningApp) {
				if (amApp.processName.compareTo(packageName) == 0) {
					isStarted = true;
					break;
				}
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return isStarted;
	}

	/**
	 * whether current application has data
	 * 
	 * @return
	 */
	private boolean getData() {
		return mAppInfor.getDataSize() > 0;
	}

	/**
	 * after clear data
	 * 
	 * @param msg
	 */
	private void processClearMsg(Message msg) {
		int result = msg.arg1;
		if (result == OP_SUCCESSFUL) {
			Log.d("zjf", "Cleared user data for package : " + packageName);
			mAppInforViewHolder.app_data.setText("0KB");
			isHasData = false;
			mAppInforViewHolder.app_cache.setText("0KB");
		}
		forceStopPackage(packageName);
	}

	/**
	 * force current application to stop
	 */
	private void forceStopPackage(String pkgName) {
		mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		mActivityManager.forceStopPackage(pkgName);
	}

	/*
	 * Private method to initiate clearing user data when the user clicks the clear data button for a system package
	 */
	private void initiateClearUserData() {
		Log.d("zjf", "Clearing user data for package : " + packageName);
		if (mClearDataObserver == null) {
			mClearDataObserver = new ClearUserDataObserver();
		}
		mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		boolean res = mActivityManager.clearApplicationUserData(packageName, mClearDataObserver);
		if (!res) {
			// Clearing data failed for some obscure reason.
			Log.d("zjf", "Could not clear application user data for package:" + packageName);
			// TODO:提示用户不能清除数据
		} else {
			Log.d("zjf", "succeed clear application user data for package:" + packageName);
			isHasData = false;
		}
	}

	/**
	 * install package
	 * 
	 * @param pkgName
	 */
	private void uninstallPkg(String pkgName) {
		PackageDeleteObserver observer = new PackageDeleteObserver();
		getPackageManager().deletePackage(pkgName, observer, 0);
		Log.d("zjf", "uninstallPkg");
	}

	/**
	 * 系统函数，字符串转换 long -String (kb)
	 * 
	 * @param size
	 * @return
	 */
	private String formateFileSize(long size) {
		if (size > 0) {
			return Formatter.formatFileSize(AppDetailInforActivity.this, size);
		} else {
			return "0KB";
		}
	}

	class PackageDeleteObserver extends IPackageDeleteObserver.Stub {

		public void packageDeleted(String packageName, int returnCode) throws RemoteException {
			Message msg = mHandler.obtainMessage(UNINSTALL_COMPLETE);
			msg.arg1 = returnCode;
			msg.obj = packageName;
			mHandler.sendMessage(msg);
		}

	}

	class ClearUserDataObserver extends IPackageDataObserver.Stub {
		public void onRemoveCompleted(final String packageName, final boolean succeeded) {
			final Message msg = mHandler.obtainMessage(CLEAR_DATA);
			msg.arg1 = succeeded ? OP_SUCCESSFUL : OP_FAILED;
			mHandler.sendMessage(msg);
		}
	}

	public void setFocus(Button btn) {
		btn.setFocusable(true);
		btn.setFocusableInTouchMode(true);
		btn.requestFocus();
		btn.setBackgroundResource(R.drawable.left_bg);
	}

	public void disableFocus(Button btn) {
		btn.setBackgroundResource(R.drawable.one_px);
	}

}
