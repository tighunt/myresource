package com.android.settings.update;

import java.io.File;

import android.os.Environment;

/**
 * 对是否存在SDCard进行判断
 * 
 * @author 杜聪甲(ducj@biaoqi.com.cn)
 * @since 1.0 2011-11-18
 */
public class CheckSDCard {

	/**
	 * 判断SDCard是否存在
	 * 
	 * @return
	 */
	public static boolean quickHasStorage() {
		System.out.println("Environment.MEDIA_MOUNTED:" + Environment.MEDIA_MOUNTED);
		System.out.println("Environment.getExternalStorageState():" + Environment.getExternalStorageState());
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}

	/**
	 * 判读SDCard是否可写
	 * 
	 * @return
	 */
	private static boolean checkFsWritable() {
		//String directoryName = Environment.getExternalStorageDirectory().toString() + "/mstara3";
		String directoryName = Environment.getExternalStorageDirectory().toString();
		File directory = new File(directoryName);
		if (!directory.isDirectory()) {
			if (!directory.mkdirs()) {
				return false;
			}
		}
		return directory.canWrite();
	}

	/**
	 * 是否能本地存储的判断
	 * 
	 * @return
	 */
	public static boolean hasStorage() {
		return hasStorage(true);
	}

	public static boolean hasStorage(boolean requireWriteAccess) {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			if (requireWriteAccess) {
				boolean writable = checkFsWritable();
				return writable;
			} else {
				return true;
			}
		} else if (!requireWriteAccess && Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}

}
