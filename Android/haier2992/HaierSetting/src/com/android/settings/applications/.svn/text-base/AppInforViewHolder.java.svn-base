package com.android.settings.applications;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.settings.R;

/**
 * 应用详细信息界面组件的初始化
 * 
 * @author ducj
 * @date 2011-11-3 上午10:46:34
 * @since 1.0
 */
public class AppInforViewHolder {

	private AppDetailInforActivity mAppDetailInforActivity;

	private Context mContext;
	// 应用图标
	protected ImageView app_icon_iv;
	// 程序名称
	protected TextView app_name_tv;
	// 程序版本
	protected TextView app_version;
	// 程序各种数据总和
	protected TextView app_total;
	// 程序大小
	protected TextView app_size;
	// 程序数据大小
	protected TextView app_data;
	// 程序缓存大小
	protected TextView app_cache;
	// 对应用程序的操作
	protected ListView mAppOperation;
	// 确定
	protected Button set_btn;
	// 强行停止
	protected Button force_stop_btn;
	// 卸载
	protected Button uninstall_btn;
	// 清除数据
	protected Button clear_data_btn;

	// 转移app zhoujf 2012-8-24 屏蔽  SDCARED 
	//protected Button mMoveApp;

	// 卸载进度条
	protected ProgressBar mProgressBar;
	// 卸载提示
	protected TextView uninstall;

	protected LinearLayout totalLayout;
	protected LinearLayout appLayout;
	protected LinearLayout dataLayout;
	protected LinearLayout cacheLayout;

	public AppInforViewHolder(AppDetailInforActivity appDetailInforActivity) {
		this.mAppDetailInforActivity = appDetailInforActivity;
		findViews();
	}

	/**
	 * 组件和数据的初始化
	 */
	private void findViews() {

		mAppOperation = (ListView) mAppDetailInforActivity.findViewById(R.id.app_operation_select);

		app_icon_iv = (ImageView) mAppDetailInforActivity.findViewById(R.id.app_icon);
		app_name_tv = (TextView) mAppDetailInforActivity.findViewById(R.id.app_name);
		app_version = (TextView) mAppDetailInforActivity.findViewById(R.id.app_version_tv);
		app_total = (TextView) mAppDetailInforActivity.findViewById(R.id.app_total_size);
		app_size = (TextView) mAppDetailInforActivity.findViewById(R.id.app_size_tv);
		app_data = (TextView) mAppDetailInforActivity.findViewById(R.id.app_data_tv);
		app_cache = (TextView) mAppDetailInforActivity.findViewById(R.id.app_cache_tv);

		set_btn = (Button) mAppDetailInforActivity.findViewById(R.id.set_btn);
		set_btn.setVisibility(View.INVISIBLE);
		force_stop_btn = (Button) mAppDetailInforActivity.findViewById(R.id.force_stop_btn);
		uninstall_btn = (Button) mAppDetailInforActivity.findViewById(R.id.uninstall_btn);
		clear_data_btn = (Button) mAppDetailInforActivity.findViewById(R.id.clear_data_btn);
	     //zhoujf 2012-8-24 屏蔽  SDCARED 
        //mMoveApp = (Button) mAppDetailInforActivity.findViewById(R.id.move_app_btn);
        mProgressBar= (ProgressBar) mAppDetailInforActivity.findViewById(R.id.progress_bar);
        mProgressBar.setIndeterminate(true);

		uninstall = (TextView) mAppDetailInforActivity.findViewById(R.id.uninstall_progress);

		totalLayout = (LinearLayout) mAppDetailInforActivity.findViewById(R.id.total_ll);
		appLayout = (LinearLayout) mAppDetailInforActivity.findViewById(R.id.app_ll);
		dataLayout = (LinearLayout) mAppDetailInforActivity.findViewById(R.id.data_ll);
		cacheLayout = (LinearLayout) mAppDetailInforActivity.findViewById(R.id.cache_ll);
	}
}
