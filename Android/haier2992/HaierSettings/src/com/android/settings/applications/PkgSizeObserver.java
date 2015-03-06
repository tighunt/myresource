package com.android.settings.applications;

import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageStats;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;

/**
 * 获取每个非系统应用程序的大小
 * 
 * @author 杜聪甲(ducj@biaoqi.com.cn)
 * @since 1.0 2011-11-17
 */
public class PkgSizeObserver extends IPackageStatsObserver.Stub {

	private final static int REFRESH_APP_SIZE = 3;

	private AppInfor mAppInfor;

	private Handler mAppInforHandler;
	// 缓存大小
	private long cacheSize;
	// 数据大小
	private long dataSize;
	// 应用程序大小
	private long appSize;
	// 总大小
	private long totalSize;

	public PkgSizeObserver(AppInfor mAppInfor, Handler handler) {
		this.mAppInfor = mAppInfor;
		this.mAppInforHandler = handler;
	}

	/***
	 * 回调函数，
	 * 
	 * @param pStatus
	 *            ,返回数据封装在PackageStats对象中
	 * @param succeeded
	 *            代表回调成功
	 */
	@Override
	public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
			throws RemoteException {
		appSize = pStats.codeSize;
		dataSize = pStats.dataSize;
		cacheSize = pStats.cacheSize;
		totalSize = pStats.cacheSize + pStats.dataSize + pStats.codeSize;
		
		
		
		Log.d("  onGetStatsCompleted     ", " appSize  =    " + appSize);
		Log.d("  onGetStatsCompleted     ", " dataSize  =    " + dataSize);
		Log.d("  onGetStatsCompleted     ", " cacheSize  =    " + cacheSize);
		Log.d("  onGetStatsCompleted     ", " totalSize  =    " + totalSize);

		mAppInfor.setAppSize(appSize);
		mAppInfor.setDataSize(dataSize);
		mAppInfor.setCacheSize(cacheSize);
		mAppInfor.setTotalSize(totalSize);

		mAppInforHandler.sendEmptyMessage(REFRESH_APP_SIZE);
	}
}
