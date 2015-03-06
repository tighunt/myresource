/**
 * 
 */
package com.android.settings.update;

/**
 * @author ducj
 * @since 1.0 2011-11-23
 */
public interface IDownloadProgressListener {

	/**
	 * 通知上层下载进度.
	 * 
	 */
	void onDownloadSizeChange(int percent);
}