package com.android.settings;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.android.settings.applications.AppDetailInforActivity;
import com.android.settings.applications.AppInfor;
import com.android.settings.applications.AppManageViewHolder;
import com.android.settings.applications.ApplicationInforAdapter;
import com.android.settings.applications.AppstoreAlertDialog;
import com.android.settings.applications.EraseSDcardAlertDialog;
import com.android.settings.applications.PkgSizeObserver;
import com.android.settings.util.Storage;
import com.android.settings.util.Tools;

/**
 * <b>application manageSSs</b><br>
 * 
 * @author ducj(ducj@biaoqi.com.cn)
 * @date 2011-10-28 上午09:48:17
 * @since 1.0
 */
public class ApplicationSettings extends Activity {
	private static final String TAG = "ApplicationSettings";
	private static final int APP_SETTING = 0;
	private static final int ALL_APP = 1;
	private static final int STORAGE = 2;
	// fix by zhudz 2012-3-6

	// zhoujf 2012-8-24 屏蔽 SDCARED
	// private static final int SDCARD_APP = 2;

	private static final int FORWARD_APP_DETAIL = 4;
	private static final int REFRESH_APP_LIST = 5;
	// private static final int REMOVE_OK = 6;
	private static final int REFRESH_MEMORY = 7;
	public static final int REFRESH_SDCARD_MEMORY = 8;
	public final static int REFRESH_APP_SIZE = 3;
	private static final HandlerThread sWorkerThread = new HandlerThread("launcher-loader");
	static {
		sWorkerThread.start();
	}
	private static final Handler sWorker = new Handler(sWorkerThread.getLooper());

	// current position
	private int isSelected;
	// 设置选择位置
	private int storeSelected;
	private int storageSelected;

	public AppManageViewHolder mAppManageViewHolder;

	private ApplicationInforAdapter mApplicationInforAdapter;
	// application information
	private List<AppInfor> mlistAppInfo = new ArrayList<AppInfor>();
	// deal with the focus question
	private Handler mHandler = new Handler();

	private AppInfor mAppInfor;

	// private List<PackageInfo> packages;
	// set a flag to record the all_app selected count
	private int count = 0;
	// flag the app list selected item
	private int app_selected = 0;

	// zhoujf 2012-8-24 屏蔽 SDCARED
	// private List<AppInfor> mSdcardlistAppInfo = new ArrayList<AppInfor>();
	// private SdcardAppAdapter mSdcardAppAdapter;
	// private AppInfor mSdcardAppInfor;
	// private int mSdcardAppSelect = 0;
	private StatFs mSDCardFileStats;
	private long totalStorage;
	private long freeStorage;
	private long usedStorage;
	// private final int SDCARD_REFRESH_APP_LIST = 10;
	// private final int FORWARD_SDCARDAPP_DETAIL = 11;

	boolean isRegister = false;

	private Handler mAppInforHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case FORWARD_APP_DETAIL:
			    Log.d(TAG, "mAppInforHandler.FORWARD_APP_DETAIL");
				Intent intent = new Intent(ApplicationSettings.this, AppDetailInforActivity.class);
				Bundle bundle = new Bundle();
				bundle.putParcelable("app_infor", mAppInfor);
				intent.putExtras(bundle);
				startActivity(intent);
				break;
			case REFRESH_APP_LIST:
				refreshAPPListView();
				break;
			case REFRESH_MEMORY:
				// 刷新内存数据和进度条
				// mAppManageViewHolder.mUsed.setText(getUsedSize());
				// mAppManageViewHolder.mTotal.setText(getTotalSize());
				int total = getSizeOfM(getTotalSize());
				int cur = getSizeOfM(getUsedSize());
				java.text.NumberFormat nf = java.text.NumberFormat.getPercentInstance();
				nf.setMinimumFractionDigits(1);// 小数点后保留2位
				float percent = (float) cur / (float) total;
				mAppManageViewHolder.mUsed.setText(nf.format(percent));
				mAppManageViewHolder.mProgressBar.setMax(total);
				mAppManageViewHolder.mProgressBar.setProgress(cur);
				break;
			case REFRESH_SDCARD_MEMORY:
				getSdcardSizes();
				if (totalStorage != 0) {
					java.text.NumberFormat nf2 = java.text.NumberFormat.getPercentInstance();
					nf2.setMinimumFractionDigits(1);// 小数点后保留2位
					float sdPercent = (float) usedStorage / (float) totalStorage;
					int totalx = getSizeOfM(getTotalSize());
					int curx = getSizeOfM(getUsedSize());
					float percentx = (float) curx / (float) totalx;
					java.text.NumberFormat nfx = java.text.NumberFormat.getPercentInstance();
					nfx.setMinimumFractionDigits(1);// 小数点后保留2位
					String SDcardUsing = getResources().getString(R.string.sdcard_using) + " " + nf2.format(sdPercent);
					mAppManageViewHolder.sdcard_using.setText(SDcardUsing);
					mAppManageViewHolder.mUsed.setText(nfx.format(percentx));
					mAppManageViewHolder.mProgressBar.setMax(totalx);
					mAppManageViewHolder.mProgressBar.setProgress(curx);
				}
				break;
			case  REFRESH_APP_SIZE:
			    
			/*
			 * zhoujf 2012-8-24 屏蔽 SDCARED case SDCARD_REFRESH_APP_LIST:
			 * mAppManageViewHolder.mSdcardappList.setAdapter(mSdcardAppAdapter); break;
			 * 
			 * case FORWARD_SDCARDAPP_DETAIL: Intent sdIntent = new Intent(ApplicationSettings.this,
			 * AppDetailInforActivity.class); Bundle sdBundle = new Bundle(); sdBundle.putParcelable("app_infor",
			 * mSdcardAppInfor); sdIntent.putExtras(sdBundle); startActivity(sdIntent); break;
			 */
			}
		};
	};

	/**
	 * 返回存储空间的数据，单位是M
	 * 
	 * @param size
	 * @return
	 */
	private int getSizeOfM(String size) {
		double res = 0;
		if (size.contains("G")) {
			res = Double.parseDouble(size.substring(0, size.lastIndexOf("G")));
			res = res * 1024;
		} else if (size.contains("M")) {
			res = Double.parseDouble(size.substring(0, size.lastIndexOf("M")));
		}
		return (int) res;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.application_manage);
		findViews();

		registerListener();

		// 注册APK变化监听
		IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_DATA_CLEARED);
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addDataScheme("package");
		registerReceiver(apkChangerReceiver, filter);

		swapSelected(APP_SETTING);
	}

	@Override
	protected void onPause() {
		sWorkerThread.interrupt();
		super.onPause();
	}

	@Override
	protected void onResume() {
		/**
		 * zhoujf 2012-8-24 屏蔽 SDCARED if(mSdcardlistAppInfo != null){ querySdcardApplication(); if
		 * (mSdcardlistAppInfo.size() <= 0) { mAppManageViewHolder.mSdcardappList.setVisibility(View.GONE);
		 * mAppManageViewHolder.mSdcardAppEmpty.setVisibility(View.VISIBLE); } else {
		 * mAppManageViewHolder.mSdcardappList.setVisibility(View.VISIBLE);
		 * mAppManageViewHolder.mSdcardAppEmpty.setVisibility(View.GONE); } }
		 */
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(apkChangerReceiver);
		super.onDestroy();
	}

	/**
	 * component init
	 */
	private void findViews() {

	    Log.d(TAG, "     ---- this is findViews -----");
		mSDCardFileStats = new StatFs(Environment.getExternalStorageDirectory().toString());

		mAppManageViewHolder = new AppManageViewHolder(this);

		// 设置默认的安装选项  zhoujf 2012-08-29 屏蔽"首选安装位置"
		//mAppManageViewHolder.installLocal_btn.setText(getInitInstallType());

		// 设置存储空间的默认数据
		mAppInforHandler.sendEmptyMessage(REFRESH_MEMORY);
		mAppInforHandler.sendEmptyMessage(REFRESH_SDCARD_MEMORY);
	// 刷新应用列表数据
		Log.d(TAG, "     ---- this is findViews next step  sWorker.post-----");
		
		sWorker.post(queryAppInf_rnb);

	}

	@Override
	protected void onRestart() {
		// 设置存储空间的默认数据
		mAppInforHandler.sendEmptyMessage(REFRESH_MEMORY);
		super.onRestart();
	}

	private void swapSelected(int position) {
		switch (position) {
		// application setting
		case APP_SETTING:
			// mAppManageViewHolder.mSdcardInfo.setVisibility(View.GONE);
			boolean isChecked = isNonMarketAppsAllowed();
			mAppManageViewHolder.mCheckBox.setChecked(isChecked);
			mAppManageViewHolder.mApp_list.setVisibility(View.GONE);
			mAppManageViewHolder.mApp_store.setVisibility(View.VISIBLE);
			mAppManageViewHolder.mStorage.setVisibility(View.GONE);
			// mAppManageViewHolder.mSdcardApp.setVisibility(View.GONE);
			// mAppManageViewHolder.current_position.setText(">>" + getString(R.string.app_setting));
			isSelected = 0;
			count = 0;
			break;
		// all application
		case ALL_APP:
			// mAppManageViewHolder.mSdcardInfo.setVisibility(View.GONE);
			count++;
			if (count == 1) {
				mAppManageViewHolder.mApp_store.setVisibility(View.GONE);
				mAppManageViewHolder.mApp_list.setVisibility(View.VISIBLE);
				mAppManageViewHolder.mStorage.setVisibility(View.GONE);
				// mAppManageViewHolder.mSdcardApp.setVisibility(View.GONE);
				// mAppManageViewHolder.current_position.setText(">>" + getString(R.string.all_app));
				isSelected = 1;
				mAppManageViewHolder.mAllAppList.setAdapter(mApplicationInforAdapter);
			}
			break;
		case STORAGE:
			Log.d("charlie", "swap STORAGE");
			mAppManageViewHolder.mApp_store.setVisibility(View.GONE);
			mAppManageViewHolder.mApp_list.setVisibility(View.GONE);
			mAppManageViewHolder.mStorage.setVisibility(View.VISIBLE);
			mAppManageViewHolder.mAppManage.clearFocus();
			isSelected = 2;
			count = 0;
			break;
		/*
		 * zhoujf 2012-8-24 屏蔽 SDCARED case SDCARD_APP: count = 0;
		 * mAppManageViewHolder.mApp_store.setVisibility(View.GONE);
		 * mAppManageViewHolder.mApp_list.setVisibility(View.GONE);
		 * mAppManageViewHolder.mSdcardApp.setVisibility(View.VISIBLE);
		 * //mAppManageViewHolder.current_position.setText(">>" //+ getString(R.string.sdcard_app)); isSelected = 2;
		 * querySdcardApplication(); if (mSdcardlistAppInfo.size() <= 0) {
		 * mAppManageViewHolder.mSdcardappList.setVisibility(View.GONE); mAppManageViewHolder.mSdcardAppEmpty
		 * .setVisibility(View.VISIBLE); } else { mAppManageViewHolder.mSdcardappList.setVisibility(View.VISIBLE);
		 * mAppManageViewHolder.mSdcardAppEmpty.setVisibility(View.GONE); } if
		 * (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) { getSdcardSizes();
		 * 
		 * String usedString = Formatter.formatShortFileSize(this, usedStorage); String totalString =
		 * Formatter.formatShortFileSize(this, totalStorage);
		 * 
		 * int sdtotal = getSizeOfM(totalString); int sdcur = getSizeOfM(usedString);
		 * 
		 * mAppManageViewHolder.mSdcardStorageBar.setMax(sdtotal);
		 * mAppManageViewHolder.mSdcardStorageBar.setProgress(sdcur);
		 * mAppManageViewHolder.mSdcardAllStorage.setText(totalString);
		 * mAppManageViewHolder.mSdcardUsedStorage.setText(usedString);
		 * mAppManageViewHolder.mSdcardInfo.setVisibility(View.VISIBLE); } break;
		 */
		}
	}

	/**
	 * register listeners
	 */
	private void registerListener() {
		mAppManageViewHolder.mAppManage.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				swapSelected(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		mAppManageViewHolder.mAppManage.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				swapSelected(position);
//				if (position == 2) {
//					mAppManageViewHolder.mAppManage.clearFocus();
//					getCurrentFocus().clearFocus();
//					mAppManageViewHolder.mStorage.requestFocus();
//				}
			}
		});

		// 选中应用程序列表，进行Activity的跳转
		mAppManageViewHolder.mAllAppList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.d(TAG, "mAppManageViewHolder.mAllAppList.setOnItemClickListener app_selected:" + position);
				app_selected = position;
				mAppInfor = mlistAppInfo.get(position);

				/*Log.d(TAG, "----setOnItemClickListener ----" + mAppInfor);
					
			    Log.d("------mAppInfor getAppName 1----------", "  " + mAppInfor.getAppName());
                Log.d("------mAppInfor getAppSize 1----------", "  " + mAppInfor.getAppSize());
                Log.d("------mAppInfor getDataSize 1----------", "  " + mAppInfor.getDataSize());
                Log.d("------mAppInfor getCacheSize 1----------", "  " + mAppInfor.getCacheSize());
                Log.d("------mAppInfor getPackageName 1----------", "  " + mAppInfor.getPackageName());
                Log.d("------mAppInfor getTotalSize 1----------", "  " + mAppInfor.getTotalSize());
                Log.d("------mAppInfor getVersionName 1----------", "  " + mAppInfor.getVersionName());
                Log.d("------mAppInfor getVersionCode 1 ----------", "  " + mAppInfor.getVersionCode());*/
               
				mAppInforHandler.sendEmptyMessage(FORWARD_APP_DETAIL);
			}
		});

		/**
		 * zhoujf 2012-8-24 屏蔽 SDCARED mAppManageViewHolder.mSdcardappList.setOnItemClickListener(new
		 * OnItemClickListener() {
		 * 
		 * @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) { Log.d(TAG,
		 *           "mAppManageViewHolder.mSdcardappList.setOnItemClickListener mSdcardAppSelect:" + position);
		 *           mSdcardAppSelect = position; mSdcardAppInfor = mSdcardlistAppInfo.get(position);
		 *           mAppInforHandler.sendEmptyMessage(FORWARD_SDCARDAPP_DETAIL); } });
		 */
		// 用于判断是否允许安装非AppStore应用
		mAppManageViewHolder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					if (!isNonMarketAppsAllowed()) {
						AppstoreAlertDialog alertDialog = new AppstoreAlertDialog(ApplicationSettings.this);
						alertDialog.show();
					}
				} else {
					setNonMarketAppsAllowed(false);
				}
			}
		});

		mAppManageViewHolder.mAppManage.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					mHandler.postAtFrontOfQueue(new Runnable() {
						@Override
						public void run() {
							mAppManageViewHolder.mAppManage.setSelection(isSelected);
						}
					});
				}
			}
		});

		mAppManageViewHolder.eraseSDcard.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				storageSelected = 1;
				mAppManageViewHolder.eraseSDcard.setBackgroundResource(R.drawable.desktop_button);
				mAppManageViewHolder.mAppManage.clearFocus();
				EraseSDcardAlertDialog alertDialog = new EraseSDcardAlertDialog(ApplicationSettings.this);
				alertDialog.show();
			}
		});
		/* 首选安装位置 2012-04-10 zhoujf 2012-08-29 屏蔽"首选安装位置"
		mAppManageViewHolder.installLocal_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AppInstallLocalDialog dialog = new AppInstallLocalDialog(ApplicationSettings.this);
				dialog.show();
			}
		});

		mAppManageViewHolder.first_install_rtl.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AppInstallLocalDialog dialog = new AppInstallLocalDialog(ApplicationSettings.this);
				dialog.show();
			}
		});
		*/
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (isSelected == 1 && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			mAppManageViewHolder.mAllAppList.setSelection(0);
			mAppManageViewHolder.mAllAppList.requestFocus();
			mAppManageViewHolder.mAppManage.clearFocus();
			return true;
		}

		/**
		 * zhoujf 2012-8-24 屏蔽 SDCARED if (isSelected == 2 && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) { if
		 * (mSdcardlistAppInfo.size() > 0) { mAppManageViewHolder.mSdcardappList.setSelection(0);
		 * mAppManageViewHolder.mSdcardappList.requestFocus(); mAppManageViewHolder.mAppManage.clearFocus(); } return
		 * true; }
		 */

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Log.d(TAG, "onKeyUp isSelected:" + isSelected + " keyCode:" + keyCode + "  storeSelected:" + storeSelected);
		if (isSelected == 0) {// 左边的应用设置选中
			if (storeSelected == 0 && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {// 如果从左边过来就跳到允许安装非APPStore应用的位置
				mAppManageViewHolder.mAppManage.clearFocus();
				mAppManageViewHolder.appstore_rtl.setBackgroundResource(R.drawable.desktop_button);
				getCurrentFocus().clearFocus();
				mAppManageViewHolder.mCheckBox.requestFocus();
				// boolean hasFocus = mAppManageViewHolder.mCheckBox.hasFocus();
				// System.out.println("hasFocus ++" + hasFocus);
				storeSelected++;
			} else if (storeSelected == 1 && keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {// 如果处于第一行按down就跑到第二行
				mAppManageViewHolder.appstore_rtl.setBackgroundResource(R.drawable.one_px);
				//mAppManageViewHolder.first_install_rtl.setBackgroundResource(R.drawable.desktop_button);
				getCurrentFocus().clearFocus();
				//mAppManageViewHolder.installLocal_btn.requestFocus();
				storeSelected++;
			} else if (storeSelected == 2 && keyCode == KeyEvent.KEYCODE_DPAD_UP) {// 如果处于第二行按up就到第一行
				//zhoujf 2012-08-29 屏蔽"首选安装位置"
				//mAppManageViewHolder.first_install_rtl.setBackgroundResource(R.drawable.one_px);
				mAppManageViewHolder.appstore_rtl.setBackgroundResource(R.drawable.desktop_button);
				getCurrentFocus().clearFocus();
				mAppManageViewHolder.mCheckBox.requestFocus();
				storeSelected--;
			}

			// 如果有选择并且按左键就退出设置
			if (storeSelected > 0 && storeSelected < 10
					&& (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_BACK)) {
				mAppManageViewHolder.appstore_rtl.setBackgroundResource(R.drawable.one_px);
				//zhoujf 2012-08-29 屏蔽"首选安装位置"
				//mAppManageViewHolder.first_install_rtl.setBackgroundResource(R.drawable.one_px);
				getCurrentFocus().clearFocus();
				mAppManageViewHolder.mAppManage.requestFocus();
				mAppManageViewHolder.mAppManage.setSelection(0);
				storeSelected = 0;
				return true;
			}

		} else if (isSelected == 1) {
			if (mAppManageViewHolder.mAllAppList.isFocused() && keyCode == KeyEvent.KEYCODE_BACK) {
				mAppManageViewHolder.mAllAppList.clearFocus();
				mAppManageViewHolder.mAppManage.requestFocus();
				return true;
			}
		} else if (isSelected == 2) {
			if (storageSelected == 0 && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
				mAppManageViewHolder.mAppManage.clearFocus();
				getCurrentFocus().clearFocus();
				mAppManageViewHolder.eraseSDcard.setBackgroundResource(R.drawable.desktop_button);
				mAppManageViewHolder.eraseSDcard.requestFocus();
				storageSelected = 1;
			}

			if (storageSelected > 0 && storageSelected < 10
					&& (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_BACK)) {
				mAppManageViewHolder.eraseSDcard.setBackgroundResource(R.drawable.one_px);
				getCurrentFocus().clearFocus();
				mAppManageViewHolder.mAppManage.requestFocus();
				mAppManageViewHolder.mAppManage.setSelection(2);
				storageSelected = 0;
				return true;
			}

		}

		return super.onKeyUp(keyCode, event);
	}

	/**
	 * 获取是否允许安装应用程序
	 * 
	 * @return
	 */
	private boolean isNonMarketAppsAllowed() {
		return android.provider.Settings.Secure.getInt(getContentResolver(),
				android.provider.Settings.Secure.INSTALL_NON_MARKET_APPS, 0) > 0;
	}

	/**
	 * 保存CheckBox的值
	 */
	public void setNonMarketAppsAllowed(boolean enabled) {
		// Change the system setting
		android.provider.Settings.Secure.putInt(getContentResolver(),
				android.provider.Settings.Secure.INSTALL_NON_MARKET_APPS, enabled ? 1 : 0);
	}

	/**
	 * 取得首选安装位置的存储值
	 * 
	 * @return
	 */
	private int getInitInstallType() {
		int type = android.provider.Settings.Secure.getInt(getContentResolver(), "install_app_location_type", 0);
		Log.d(TAG, "getInitInstallType:" + type);
		if (type == 0) {// 缺省
			return R.string.install_type_default;
		} else if (type == 1) {// 内部
			return R.string.install_type_inte;
		}
		if (type == 2) {// 外部
			return R.string.install_type_exte;
		}
		return R.string.install_type_default;
	}

	/**
	 * 设置安装类型自动调整
	 * 
	 * @param add
	 *            true 自动正向循环 false 自动反向转
	 * @return
	 * 
	 *         private int setInstallType(boolean add) { int type =
	 *         android.provider.Settings.Secure.getInt(getContentResolver(), "install_app_location_type", 0); if (add) {
	 *         type++; } else { type--; } if (type > 2) type = 0; if (type < 0) type = 2; // 保存数据
	 *         android.provider.Settings.Secure.putInt(getContentResolver(), "install_app_location_type", type); if
	 *         (type == 0) {// 缺省 return R.string.install_type_default; } else if (type == 1) {// 内部 return
	 *         R.string.install_type_inte; } if (type == 2) {// 外部 return R.string.install_type_exte; }
	 * 
	 *         return R.string.install_type_default; }
	 */

	/**
	 * 获取已使用的内部存储空间大小
	 */
	private String getUsedSize() {
		long usedSize = new Storage().getInternalStorage().mUsedStorage;
		return Formatter.formatFileSize(ApplicationSettings.this, usedSize);
	}

	/**
	 * 获取总的内部存储空间大小
	 */
	private String getTotalSize() {
		long totalSize = new Storage().getInternalStorage().mTotalStorage;
		return Formatter.formatFileSize(ApplicationSettings.this, totalSize);
	}

	/**
	 * 获取所有应用程序的详细信息
	 */
	Runnable queryAppInf_rnb = new Runnable() {

	    
		@Override
		public void run() {
			List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);

			Log.d( "     ---- queryAppInf_rnb packages 11111111111","  packages =  ");
			PackageInfo packageInfo;
			mlistAppInfo.clear();
			for (int i = 0; i < packages.size(); i++) {
				packageInfo = packages.get(i);
				//Log.d("------queryAppInf_rnb  packageInfo 222222222222 = ", " " + packageInfo);
				// 用于显示非系统的应用程序
				if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
					mAppInfor = new AppInfor();
					mAppInfor.setAppName(packageInfo.applicationInfo.loadLabel(getPackageManager()).toString());
					mAppInfor.setPackageName(packageInfo.packageName);
					mAppInfor.setVersionName(packageInfo.versionName);
					mAppInfor.setVersionCode(packageInfo.versionCode);
					mAppInfor.setFirstInstallTime(packageInfo.firstInstallTime);
					mAppInfor.setUid(packageInfo.applicationInfo.uid);
				
					Drawable drawable = packageInfo.applicationInfo.loadIcon(getPackageManager());
					BitmapDrawable bd = (BitmapDrawable) drawable;
					Bitmap bitmap = bd.getBitmap();
					byte[] mapArray = Tools.bitmap2Bytes(bitmap);
					mAppInfor.setIconAppDrawable(bitmap);
					mAppInfor.setBitmap(mapArray);
					//Log.d("-----leewokan packageInfo.packageName -----------"," packageInfo.packageName = " + packageInfo.packageName);
					queryPacakgeSize(packageInfo.packageName);
					mlistAppInfo.add(mAppInfor);
				}
			}
			// 通知主界面更新数据
			mAppInforHandler.sendEmptyMessage(REFRESH_APP_LIST);
		}
	};

	/**
	 * zhoujf 2012-8-24 屏蔽 SDCARED
	 * 
	 * 
	 * private void querySdcardApplication() { mSdcardlistAppInfo.clear(); List<PackageInfo> packages =
	 * getPackageManager().getInstalledPackages(0);
	 * 
	 * PackageInfo packageInfo;
	 * 
	 * for (int i = 0; i < packages.size(); i++) { packageInfo = packages.get(i); // 用于显示非系统的应用程序 if
	 * ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) { if ((packageInfo.applicationInfo.flags
	 * & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) { mSdcardAppInfor = new AppInfor();
	 * mSdcardAppInfor.setAppName(packageInfo.applicationInfo .loadLabel(getPackageManager()).toString());
	 * mSdcardAppInfor.setPackageName(packageInfo.packageName); mSdcardAppInfor.setVersionName(packageInfo.versionName);
	 * mSdcardAppInfor.setVersionCode(packageInfo.versionCode); mSdcardAppInfor.setUid(packageInfo.applicationInfo.uid);
	 * Drawable drawable = packageInfo.applicationInfo .loadIcon(getPackageManager()); BitmapDrawable bd =
	 * (BitmapDrawable) drawable; Bitmap bitmap = bd.getBitmap(); byte[] mapArray = Tools.bitmap2Bytes(bitmap);
	 * mSdcardAppInfor.setIconAppDrawable(bitmap); mSdcardAppInfor.setBitmap(mapArray);
	 * querySdcardPacakgeSize(packageInfo.packageName); mSdcardlistAppInfo.add(mSdcardAppInfor); } } } mSdcardAppAdapter
	 * = new SdcardAppAdapter(ApplicationSettings.this, mSdcardlistAppInfo); mSdcardAppAdapter.notifyDataSetChanged();
	 * // 通知主界面更新数据 mAppInforHandler.sendEmptyMessage(SDCARD_REFRESH_APP_LIST); }
	 */

	private void getSdcardSizes() {
		mSDCardFileStats.restat(Environment.getExternalStorageDirectory().toString());
		String formatPath = Environment.getExternalStorageDirectory().getPath();

		System.out.println("sdcard path:" + Environment.getExternalStorageDirectory().toString());
		try {
			long SdcardSize = 0;
			totalStorage = (long) mSDCardFileStats.getBlockCount() * mSDCardFileStats.getBlockSize();
			freeStorage = (long) mSDCardFileStats.getAvailableBlocks() * mSDCardFileStats.getBlockSize();
			usedStorage = totalStorage - freeStorage;
			SdcardSize = getFileSize(new File(formatPath));
			Log.d("WBL test","Disk totalStorage = " + totalStorage);
			Log.d("WBL test","Disk usedStorage = " + usedStorage);
			Log.d("WBL test","usedStorage = " + SdcardSize);
			totalStorage = totalStorage - usedStorage + SdcardSize;
			usedStorage = SdcardSize;
			Log.d("WBL test","totalStorage = " + totalStorage);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public long getFileSize(File f) throws Exception
	{
		long size = 0;
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++)
		{
			if (flist[i].isDirectory())
			{
				size = size + getFileSize(flist[i]);
			} else
			{
				size = size + flist[i].length();
			}
		}
		return size;
	}

	/**
	 * 通过包名查询应用程序的大小
	 * 
	 * @param pkgName
	 * @throws Exception
	 */
	private void queryPacakgeSize(String pkgName) {
		if (pkgName != null) {
			PackageManager pm = getPackageManager(); // 得到pm对象
			/*Log.d("===========queryPacakgeSize.pkgName ========", "  queryPacakgeSize.pkgName  = " + pkgName);
			Log.d("===========queryPacakgeSize.pm ========", "  queryPacakgeSize.pm  = " + pm);
			Log.d("===========queryPacakgeSize.pm ========", "  qpm.getClass()  = " + pm.getClass()+"this class="+this.getClass());*/
			try {
				Method getPackageSizeInfo = pm.getClass().getDeclaredMethod("getPackageSizeInfo", String.class, int.class,
						IPackageStatsObserver.class);
				//Log.d("===========queryPacakgeSize.getPackageSizeInfo ========1111111111", "  queryPacakgeSize.getPackageSizeInfo  = " + getPackageSizeInfo);
				getPackageSizeInfo.invoke(pm, pkgName, Process.myUid() / 100000,new PkgSizeObserver(mAppInfor, mAppInforHandler));
				//Log.d("===========queryPacakgeSize.getPackageSizeInfo ========2222222222", "  queryPacakgeSize.getPackageSizeInfo  = " + getPackageSizeInfo);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * zhoujf 2012-8-24 屏蔽 SDCARED private void querySdcardPacakgeSize(String pkgName) { if (pkgName != null) {
	 * PackageManager pm = getPackageManager(); // 得到pm对象 try { Method getPackageSizeInfo =
	 * pm.getClass().getDeclaredMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
	 * getPackageSizeInfo.invoke(pm, pkgName, new PkgSizeObserver(mSdcardAppInfor, mAppInforHandler)); } catch
	 * (Exception ex) { ex.printStackTrace(); } } }
	 */

	/**
	 * 刷新listView的内容
	 */
	private void refreshAPPListView() {
		if(mApplicationInforAdapter == null){
			mApplicationInforAdapter = new ApplicationInforAdapter(ApplicationSettings.this, mlistAppInfo);
			mAppManageViewHolder.mAllAppList.setAdapter(mApplicationInforAdapter);
		}
		mApplicationInforAdapter.notifyDataSetChanged();
		mAppManageViewHolder.mAllAppList.setSelection(app_selected);
	}

	private BroadcastReceiver apkChangerReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "apkChangerReceiver....onReceive...Thread ID:" + Thread.currentThread().getId());
			// 刷新应用列表数据
			sWorker.post(queryAppInf_rnb);

			// 修改显示的存储空间size
			mAppInforHandler.sendEmptyMessage(REFRESH_MEMORY);
		}
	};

	public Handler getmAppInforHandler() {
		return mAppInforHandler;
	}

	protected void checkram() {/*//20130511 modify by cw

		// 取最大保留空间
		final int DEFAULT_THRESHOLD_MAX_BYTES = 500 * 1024 * 1024;

		long maxValue = android.provider.Settings.Secure.getInt(ApplicationSettings.this.getContentResolver(),

		android.provider.Settings.Secure.SYS_STORAGE_THRESHOLD_MAX_BYTES, DEFAULT_THRESHOLD_MAX_BYTES);

		// 取最小保留空间
		long value = android.provider.Settings.Secure.getInt(ApplicationSettings.this.getContentResolver(),

		android.provider.Settings.Secure.SYS_STORAGE_THRESHOLD_PERCENTAGE, 10);

		StatFs mDataFileStats = new StatFs("/data");

		long mTotalMemory = ((long) mDataFileStats.getBlockCount() * mDataFileStats.getBlockSize());

		long mixValue = value * mTotalMemory / 100L;

		// 取需要保留的磁盘空间
		long mMemLowThreshold = mixValue < maxValue ? mixValue : maxValue;

		// 剩余磁盘空间
		long mFreeMem = (long) mDataFileStats.getAvailableBlocks() * mDataFileStats.getBlockSize();
		long totalSize = new Storage().getInternalStorage().mTotalStorage;
		int total = getSizeOfM(getTotalSize());
		int surplus = getSizeOfM(Formatter.formatFileSize(ApplicationSettings.this, mFreeMem));

		mAppManageViewHolder.mUsed.setText(Formatter.formatFileSize(ApplicationSettings.this, totalSize - mFreeMem));

		// mAppManageViewHolder.mTotal.setText(getTotalSize());

		mAppManageViewHolder.mProgressBar.setMax(total);
		mAppManageViewHolder.mProgressBar.setProgress(total - surplus);

		// 如果剩余空间小于保留问题就强制回到应用卸载界面
		Log.d("zjf", "checkram():剩余磁盘容量：" + mFreeMem + "==需要保留的磁盘空间：" + mMemLowThreshold);

		if (mFreeMem < mMemLowThreshold) {
			Toast.makeText(ApplicationSettings.this, getResources().getString(R.string.del_more_application), 0).show();
		} else {
			this.finish();
		}

	*/}

	/**
	 * zhoujf 2012-08-29 屏蔽"首选安装位置"

	public void setAppInstallLocalMode(int mode) {
		mAppManageViewHolder.installLocal_btn.setText(getInitInstallType());
	}
	 */
}
