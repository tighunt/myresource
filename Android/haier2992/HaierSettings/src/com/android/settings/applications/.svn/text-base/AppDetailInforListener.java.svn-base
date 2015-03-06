package com.android.settings.applications;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.settings.R;

/**
 * 应用细节监听器
 * 
 * @author ducj
 * @since 1.0 2011-11-25
 */
public class AppDetailInforListener {

	private AppDetailInforActivity mActivity;

	private AppInforViewHolder mHolder;

	private int curSelectedIndex = 0;

	public AppDetailInforListener(AppDetailInforActivity activity, AppInforViewHolder holder) {
		this.mActivity = activity;
		this.mHolder = holder;
		registerListeners();
	}

	/**
	 * response to listeners
	 */
	private void registerListeners() {

		Log.d("zjf", "AppDetailInforListener.registerListeners******");

		mHolder.force_stop_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("zjf",
						"AppDetailInforListener.registerListeners.mHolder.force_stop_btn.setOnClickListener.onClick******");
				changeViewSelected(0); // 2012-04-23
				if(mActivity.isRunning)
				mActivity.showDialog(AppDetailInforActivity.FORCE_STOP);
				else
					mActivity.mHandler.sendEmptyMessage(mActivity.MSG_NO_STOP);
			}
		});

		mHolder.uninstall_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("zjf",
						"AppDetailInforListener.registerListeners.mHolder.uninstall_btn.setOnClickListener.onClick******");
				changeViewSelected(1);
				mActivity.showDialog(AppDetailInforActivity.UNINSTALL);
			}
		});

		mHolder.clear_data_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("zjf",
						"AppDetailInforListener.registerListeners.mHolder.clear_data_btn.setOnClickListener.onClick******");
				changeViewSelected(2);
				if(mActivity.isHasData)
				mActivity.showDialog(AppDetailInforActivity.CLEAR_DATA);
				else
					mActivity.mHandler.sendEmptyMessage(mActivity.MSG_NO_CLEAR_DATA);
			}
		});

		mHolder.set_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mActivity.finish();
			}

		});
		OnButtonFocusChangeListener focusChange = new OnButtonFocusChangeListener();
		mHolder.force_stop_btn.setOnFocusChangeListener(focusChange);
		mHolder.uninstall_btn.setOnFocusChangeListener(focusChange);
		mHolder.clear_data_btn.setOnFocusChangeListener(focusChange);
		// zhoujf 2012-8-24 屏蔽  SDCARED 
		//mHolder.mMoveApp.setOnFocusChangeListener(focusChange);
	}

	/**
	 * 当卸载成功时，设置总计，应用程序，数据，缓存等隐藏
	 */
	public void setInvisible() {
		mHolder.totalLayout.setVisibility(View.INVISIBLE);
		mHolder.appLayout.setVisibility(View.INVISIBLE);
		mHolder.dataLayout.setVisibility(View.INVISIBLE);
		mHolder.cacheLayout.setVisibility(View.INVISIBLE);

		mHolder.force_stop_btn.setBackgroundResource(R.drawable.one_px);
		mHolder.clear_data_btn.setBackgroundResource(R.drawable.one_px);
		mHolder.uninstall_btn.setBackgroundResource(R.drawable.one_px);
		//zhoujf 2012-8-24 屏蔽  SDCARED 
		//mHolder.mMoveApp.setBackgroundResource(R.drawable.one_px);

		
		this.mActivity.setFocus(mHolder.force_stop_btn);
		mHolder.force_stop_btn.setText(mActivity.getResources().getString(R.string.ok));
		mHolder.clear_data_btn.setVisibility(View.INVISIBLE);
		mHolder.uninstall_btn.setVisibility(View.INVISIBLE);
		//zhoujf 2012-8-24 屏蔽  SDCARED 
		//mHolder.mMoveApp.setVisibility(View.INVISIBLE);

		// set focus after uninstall
		mHolder.set_btn.setVisibility(View.VISIBLE);
		this.mActivity.setFocus(mHolder.set_btn);

		// mHolder.force_stop_btn.setText(mActivity.getResources().getString(R.string.ok));
		mHolder.force_stop_btn.setVisibility(View.GONE);
		mHolder.clear_data_btn.setVisibility(View.GONE);
		mHolder.uninstall_btn.setVisibility(View.GONE);
		// zhoujf 2012-8-24 屏蔽  SDCARED 
		//mHolder.mMoveApp.setVisibility(View.GONE);

	}

	class OnButtonFocusChangeListener implements View.OnFocusChangeListener {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus) {
				mActivity.setFocus((Button) v);
			} else {
				mActivity.disableFocus((Button) v);
			}
		}
	}

	/**
	 * 鼠标点击时，切换焦点
	 * 
	 * @param seleted
	 * @param flag
	 */
	public void changeViewSelected(int seleted, boolean flag) {
		if (!flag) {
			switch (seleted) {
			case 0:
				mActivity.disableFocus(mHolder.force_stop_btn);
				break;
			case 1:
				mActivity.disableFocus(mHolder.uninstall_btn);
				break;
			case 2:
				mActivity.disableFocus(mHolder.clear_data_btn);
				break;
			/* 转移app 
			 * zhoujf 2012-8-24 屏蔽  SDCARED 
			case 3:
				mActivity.disableFocus(mHolder.mMoveApp);
				break;
			*/
			}
		} else {
			switch (seleted) {
			case 0:
				mActivity.setFocus(mHolder.force_stop_btn);
				break;
			case 1:
				mActivity.setFocus(mHolder.uninstall_btn);
				break;
			case 2:
				mActivity.setFocus(mHolder.clear_data_btn);
				break;
			/* zhoujf 2012-8-24 屏蔽  SDCARED 
			case 3:
				mActivity.setFocus(mHolder.mMoveApp);
				break;
			*/
			}
			
		}
	}

	public void changeViewSelected(int selected) {
		changeViewSelected(curSelectedIndex, false);
		curSelectedIndex = selected;
		changeViewSelected(curSelectedIndex, true);
	}
}
