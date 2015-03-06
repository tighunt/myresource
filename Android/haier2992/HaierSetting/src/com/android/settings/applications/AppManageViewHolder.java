package com.android.settings.applications;

import android.content.res.Configuration;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.settings.ApplicationSettings;
import com.android.settings.R;
import com.tvos.settings.adapter.SystemBackAdapter;

/**
 * 应用管理界面的初始化
 * 
 * @author 杜聪甲(ducj@biaoqi.com.cn)
 * @date 2011-11-3 下午05:54:58
 * @since 1.0
 */
public class AppManageViewHolder {

	private ApplicationSettings mAppManageActivity;

	// 此listView中包括应用设置和应用管理
	public ListView mAppManage;

	// 所有应用的list
	public ListView mAllAppList;

	// 用于显示应用管理和所有应用
	private SystemBackAdapter mSystemBackAdapter;

	// 所有应用程序的列表显示
	// private AppAdapter mAppAdapter;

	// 所有应用列表的LinearLayout
	public LinearLayout mApp_list;

	// 存储管理列表的LinearLayout
	public LinearLayout mStorage;

	// 是否允许安装非Appstore应用及首选安装位置的LinearLayout
	public LinearLayout mApp_store;

	public ProgressBar mProgressBar;

	// 判断是否允许
	public CheckBox mCheckBox;

	// zhoujf 2012-08-29 屏蔽"首选安装位置"
	//public RelativeLayout first_install_rtl;
	//public Button installLocal_btn;
	// 安装位置箭头
	//public ImageView arrowRig_imgView, arrowLef_imgView;
	
	public RelativeLayout appstore_rtl;
	public RelativeLayout eraseSDcard;
	public TextView sdcard_using;

	// 已使用内存空间
	public TextView mUsed;

	// 总的内存空间
	public TextView mTotal;

	// 当前所处的位置
	// public TextView current_position;

	// zhoujf 2012-8-24 屏蔽 SDCARED
	// public ListView mSdcardappList;

	// sdcard app显示布局
	// public LinearLayout mSdcardApp;

	// sdcard没有app提示信息
	// public TextView mSdcardAppEmpty;

	// sdcard 总存储空间
	// public TextView mSdcardAllStorage;

	// sdcard 使用存储空间
	// public TextView mSdcardUsedStorage;

	// public LinearLayout mSdcardInfo;

	// public ProgressBar mSdcardStorageBar;

	public AppManageViewHolder(ApplicationSettings appManageActivity) {
		this.mAppManageActivity = appManageActivity;
		findViews();
	}

	/**
	 * 组件和数据的初始化
	 */
	private void findViews() {

		mApp_list = (LinearLayout) mAppManageActivity.findViewById(R.id.app_ll);

		mApp_store = (LinearLayout) mAppManageActivity.findViewById(R.id.appstore_ll);

		mStorage = (LinearLayout) mAppManageActivity.findViewById(R.id.storage_ll);
		eraseSDcard = (RelativeLayout) mStorage.findViewById(R.id.erase_sdcard);
		sdcard_using = (TextView) mStorage.findViewById(R.id.sdcard_using);

		// 允许安装非Appstore数据
		appstore_rtl = (RelativeLayout) mApp_store.findViewById(R.id.appstore_rl);
		Configuration config = mAppManageActivity.getResources().getConfiguration();
		if (config.locale.toString().equals("en_US")) // zhf
		{
			mCheckBox = (CheckBox) mApp_store.findViewById(R.id.checkbox_app_en);
			mCheckBox.setVisibility(View.VISIBLE);
		} else {
			mCheckBox = (CheckBox) mApp_store.findViewById(R.id.checkbox_app);
			mCheckBox.setVisibility(View.VISIBLE);
		}

		// 首选项元素 zhoujf 2012-08-29 屏蔽"首选安装位置"
		//first_install_rtl = (RelativeLayout) mApp_store.findViewById(R.id.first_install_rl);
		//arrowRig_imgView = (ImageView) mApp_store.findViewById(R.id.right_arrowhead);
		//arrowLef_imgView = (ImageView) mApp_store.findViewById(R.id.left_arrowhead);
		//installLocal_btn = (Button) mApp_store.findViewById(R.id.first_install_bt);

		mAppManage = (ListView) mAppManageActivity.findViewById(R.id.app_list_select);
		mSystemBackAdapter = new SystemBackAdapter(mAppManageActivity, new String[] {
				mAppManageActivity.getResources().getString(R.string.app_setting),
				mAppManageActivity.getResources().getString(R.string.all_app),
				mAppManageActivity.getResources().getString(R.string.storage_manager)
		// zhoujf 2012-8-24 屏蔽 SDCARED
		// mAppManageActivity.getResources().getString(R.string.sdcard_app)
				});
		mAppManage.setDividerHeight(0);
		mAppManage.setAdapter(mSystemBackAdapter);

		mAllAppList = (ListView) mAppManageActivity.findViewById(R.id.app_list);
		mAllAppList.setDividerHeight(0);

		mProgressBar = (ProgressBar) mAppManageActivity.findViewById(R.id.cur_pro);

		mUsed = (TextView) mAppManageActivity.findViewById(R.id.used_tv);
		// mTotal = (TextView) mAppManageActivity.findViewById(R.id.total_tv);
		// current_position = (TextView) mAppManageActivity.findViewById(R.id.current_position);

		// zhoujf 2012-8-24 屏蔽 SDCARED
		// mSdcardappList = (ListView) mAppManageActivity.findViewById(R.id.sdcard_app_list);
		// mSdcardappList.setDividerHeight(0);

		// mSdcardApp = (LinearLayout) mAppManageActivity.findViewById(R.id.sdcard_app_ll);
		// mSdcardAppEmpty = (TextView) mAppManageActivity.findViewById(R.id.sdcard_empty_app);
		// mSdcardAllStorage = (TextView) mAppManageActivity.findViewById(R.id.sdcard_all_storage);
		// mSdcardUsedStorage = (TextView) mAppManageActivity.findViewById(R.id.sdcard_avialiable_storage);

		// mSdcardStorageBar = (ProgressBar) mAppManageActivity.findViewById(R.id.sdcard_storage_bar);
		// mSdcardInfo = (LinearLayout) mAppManageActivity.findViewById(R.id.sdcard_info);
	}
}
