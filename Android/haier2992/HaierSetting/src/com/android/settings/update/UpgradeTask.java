package com.android.settings.update;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

/**
 * 下载任务实现类.
 * 
 * @author wangchao
 * @since 1.0
 * @date 2011-11-18
 */
public class UpgradeTask implements Runnable {

	private final static String TAG = "upgrade";

	private final static String DOWNLOAD_ADDRESS = "url";

	private final static String VERSION = "version";

	private final static String NAME = "share_pres";

	private final static int DOWNLOAD_ERROR = 3;
	/* 已经下载的大小 */
	private long downloadedSize = 0;
	/* 文件总大小 */
	private long totalSize;
	/* 百分比 */
	private int percent;
	// 进度条变化监听器
	private IDownloadProgressListener mDPListener;
	// 更新包存放的本地地址
	private String mLocalPath;
	// 更新的URL的地址
	private String mUpgradeURL;
	// 版本号
	private String mVersion;

	private Context mContext;
	private Handler mHandler;

	public UpgradeTask(Context context, String upgradeURL, String localPath, String version,
			IDownloadProgressListener dpListener, Handler handler) {
		this.mDPListener = dpListener;
		this.mLocalPath = localPath;
		this.mUpgradeURL = upgradeURL;
		this.mVersion = version;
		this.mContext = context;
		this.mHandler = handler;
	}

	/**
	 * 检测升级的版本是否正确，如果不正确则不需要进行断点续传，直接删除.
	 * 
	 * @return
	 */
	protected void prepare() {

		File file = new File(mLocalPath);

		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}

		String versionString = getStringData(VERSION);

		if (versionString.equals(mVersion)) {
			mUpgradeURL = getStringData(DOWNLOAD_ADDRESS);
		} else {
			System.out.println("delete..............");
			file.delete();
		}
	}

	/*
	 * @see com.jrm.core.container.cmps.upgrade.task.BaseUpgradeTask#onDownload()
	 */
	protected boolean download() {

		/* 获取之前已下载的大小 */
		File file = new File(mLocalPath);
		if (file.exists()) {
			downloadedSize = file.length();
		} else {
			downloadedSize = 0;
		}

		System.out.println("mUpgradeURL:" + mUpgradeURL);

		System.out.println("downloadedSize:" + downloadedSize);

		try {
			URL url = new URL(mUpgradeURL);
			HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();

			totalSize = httpConnection.getContentLength();

			System.out.println("totalSize:" + totalSize);

			if (downloadedSize == totalSize && this.mDPListener != null) {
				mDPListener.onDownloadSizeChange(100);
				return true;
			} else if (downloadedSize > totalSize) {// 已下载大小超过总大小，删除之前文件.
				if (!file.delete()) {// 如果之前旧文件没有删除成功则下载失败.
					return false;
				}
			}

			httpConnection.disconnect();

			/* 设置下载参数 */
			httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection
					.setRequestProperty("Accept", "image/gif, " + "image/jpeg, " + "image/pjpeg, " + "image/pjpeg, "
							+ "application/x-shockwave-flash, " + "application/xaml+xml, "
							+ "application/vnd.ms-xpsdocument, " + "application/x-ms-xbap, "
							+ "application/x-ms-application, " + "application/vnd.ms-excel, "
							+ "application/vnd.ms-powerpoint, " + "application/msword, " + "*/*");
			httpConnection.setRequestProperty("Accept-Language", "zh-CN");
			httpConnection.setRequestProperty("Referer", mUpgradeURL);
			httpConnection.setRequestProperty("Charset", "UTF-8");
			httpConnection.setRequestProperty("Range", "bytes=" + downloadedSize + "-");// 设置获取实体数据的范围

			httpConnection.setRequestProperty("Connection", "Keep-Alive");

			/* 准备下载 */
			InputStream inStream = httpConnection.getInputStream();
			byte[] buffer = new byte[1024];

			/* 文件存储 */
			File saveFile = new File(mLocalPath);// task.getLocalUrl()

			RandomAccessFile threadfile = new RandomAccessFile(saveFile, "rwd");
			threadfile.seek(downloadedSize);

			int offset = 0;
			int count = 0;
			int perUnit = (int) totalSize / 1024 / 100;

			try {
				while ((offset = inStream.read(buffer, 0, 1024)) != -1) {

					threadfile.write(buffer, 0, offset);

					count++;
					if (count == perUnit && downloadedSize < totalSize) {
						percent = (int) (downloadedSize * 100 / totalSize);

						if (this.mDPListener != null) {
							mDPListener.onDownloadSizeChange(percent);
						}

						count = 0;
					}
					downloadedSize += offset;
				}
			} finally {
				threadfile.close();
				inStream.close();
			}

			if (downloadedSize == totalSize && this.mDPListener != null) {
				mDPListener.onDownloadSizeChange(100);
			}

			Log.d(TAG, "download finished.");
			return true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			Log.e(TAG, "MSG1:" + e.getMessage());
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "MSG2:" + e.getMessage());
			return false;
		} catch (Throwable th) {
			th.printStackTrace();
			Log.e(TAG, "MSG3:" + th.getMessage());
			return false;
		}
	}

	@Override
	public void run() {
		prepare();
		if (!download()) {
			System.out.println("download failed...");
			mHandler.sendEmptyMessage(DOWNLOAD_ERROR);
		}
	};

	/**
	 * 从SharePreference中获取持久化的数据
	 * 
	 * @param key
	 * @return
	 */
	private String getStringData(String key) {
		SharedPreferences preference = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		return preference.getString(key, "");
	}
}
