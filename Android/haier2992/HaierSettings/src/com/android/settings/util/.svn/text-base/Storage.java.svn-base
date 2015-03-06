package com.android.settings.util;

import android.os.Environment;
import android.os.StatFs;

/**
 * 此类用于获取系统的内部存储空间和磁盘容量大小,内存大小
 * 
 * @author ducj
 * @date 2011-11-4 上午11:43:47
 * @since 1.0
 */
public class Storage {

	// 系统的内部存储空间
	private StatFs mDataFileStats;

	// 磁盘容量大小
	private StatFs mSDCardFileStats;

	// 存储空间总的大小
	private long mTotalStorage = 0;

	// 存储空间剩余的大小
	private long mFreeStorage = 0;

	// 存储空间已使用的大小
	private long mUsedStorage = 0;

	/**
	 * 取得内部存储空间的信息
	 * 
	 * @return
	 */
	public StorageInfor getInternalStorage() {
		mDataFileStats = new StatFs("/data");

		mDataFileStats.restat("/data");
		try {
			mTotalStorage = (long) mDataFileStats.getBlockCount() * mDataFileStats.getBlockSize();
			mFreeStorage = (long) mDataFileStats.getAvailableBlocks() * mDataFileStats.getBlockSize();
			mUsedStorage = mTotalStorage - mFreeStorage;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		StorageInfor infor = new StorageInfor(mTotalStorage, mFreeStorage, mUsedStorage);

		return infor;
	}

	/**
	 * 取得磁盘的存储空间的信息
	 * 
	 * @return
	 */
	public StorageInfor getDiskStorage() {

		String path = Environment.getExternalStorageDirectory().toString();

		mSDCardFileStats = new StatFs(path);

		mSDCardFileStats.restat(path);

		try {
			mTotalStorage = (long) mSDCardFileStats.getBlockCount() * mSDCardFileStats.getBlockSize();
			mFreeStorage = (long) mSDCardFileStats.getAvailableBlocks() * mSDCardFileStats.getBlockSize();

			mUsedStorage = mTotalStorage - mFreeStorage;
		} catch (IllegalArgumentException e) {
		}

		StorageInfor infor = new StorageInfor(mTotalStorage, mFreeStorage, mUsedStorage);

		return infor;
	}
}
