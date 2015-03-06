package com.android.settings.applications;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.android.settings.ApplicationSettings;
import com.android.settings.R;

/**
 * AppInstallLocalDialog
 * 
 * @author zhanghs
 * @date 2012-4-10
 * @since 1.0
 */
public class AppInstallLocalDialog extends Dialog {

	private ApplicationSettings mApplicationSetting;
	private ListView mAppInstallLocalListView;
	private int balanceIndex = 0;
	private List<Map<String, Object>> appInstallLocalList = new ArrayList<Map<String, Object>>();
	private SimpleAdapter appInstallLocalAdapter;
	// 用于标记当前的选中项
	private int select;
	private String[] items;

	public AppInstallLocalDialog(Context context) {
		super(context);
		mApplicationSetting = (ApplicationSettings) context;
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.app_install_local_mode);

		Window w = getWindow();
		Display display = w.getWindowManager().getDefaultDisplay();
		WindowManager.LayoutParams layoutParams = w.getAttributes();
		w.setBackgroundDrawableResource(R.drawable.set_bg);
		layoutParams.width = (int) (display.getWidth() * 0.35);
		layoutParams.height = (int) (display.getHeight() * 0.45);
		w.setAttributes(layoutParams);
		w.setTitle(null);

		findViews();
		registerListeners();
	}

	/**
	 * init compontent
	 */
	private void findViews() {
		mAppInstallLocalListView = (ListView) findViewById(R.id.app_install_local_list);
		mAppInstallLocalListView.setDivider(null);

		items = mApplicationSetting.getResources().getStringArray(R.array.app_install_local);
		select = getInstallMode();
		balanceIndex = select;
		for (int i = 0; i < items.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("nameItem", items[i]);
			if (select == i) {
				map.put("imgItem", R.drawable.selected);
			} else {
				map.put("imgItem", R.drawable.unselected);
			}
			appInstallLocalList.add(map);
		}
		appInstallLocalAdapter = new SimpleAdapter(mApplicationSetting, appInstallLocalList,
				R.layout.date_format_item_list, new String[] { "nameItem", "imgItem" }, new int[] {
						R.id.date_format_item, R.id.date_format_item_iv });

		mAppInstallLocalListView.setAdapter(appInstallLocalAdapter);
		mAppInstallLocalListView.setSelection(select);
	}

	private void registerListeners() {
		mAppInstallLocalListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				changeRadioImg(balanceIndex, false);
				changeRadioImg(position, true);
				balanceIndex = position;

				setInstallLocal(position);
				dismiss();
			}
		});
	}

	/**
	 * 实时改变单选项
	 * 
	 * @param selectedItem
	 *            当前选中项
	 * @param b
	 *            是否点击此项
	 */
	@SuppressWarnings("unchecked")
	private void changeRadioImg(int selectedItem, boolean b) {
		HashMap<String, Object> map = (HashMap<String, Object>) appInstallLocalAdapter.getItem(selectedItem);
		if (b) {
			map.put("imgItem", R.drawable.selected);
		} else {
			map.put("imgItem", R.drawable.unselected);
		}
		appInstallLocalAdapter.notifyDataSetChanged();
	}

	/**
	 * 设置安装位置模式 0：由系统确定；1：设备内部存储；2：可卸载的SD卡
	 * 
	 * @param mode
	 */
	public void setInstallLocal(int mode) {
		setInstallMode(mode);
		//mApplicationSetting.setAppInstallLocalMode(mode);
	}

	public void setInstallMode(int mode) {
		android.provider.Settings.Secure.putInt(mApplicationSetting.getContentResolver(), "install_app_location_type",
				mode);
	}

	public int getInstallMode() {
		return android.provider.Settings.Secure.getInt(mApplicationSetting.getContentResolver(),
				"install_app_location_type", 0);
	}
}
