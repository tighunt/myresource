package com.android.settings.update;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.android.settings.R;
import com.android.settings.util.Tools;
import com.tvos.settings.adapter.SystemBackAdapter1;

/**
 * <b>系统升级的主界面</b><br>
 * 
 * <pre>
 * 该界面包括：
 *      1：本地升级
 *      2：网络升级
 * </pre>
 * 
 * @author 杜聪甲(ducj@biaoqi.com.cn)
 * @date 2011-10-28 下午09:48:17
 * @since 1.0
 */
public class SystemUpdateActivity extends Activity {

	private final static int NET_UPDATE = 0;

	private final static int LOCAL_UPDATE = 1;

	private ListView mSystemUpdate;

	private SystemBackAdapter1 mSystemBackAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.system_update);

		findViews();
		registerListener();
	}

	/**
	 * 控件的初始化
	 */
	private void findViews() {
		mSystemUpdate = (ListView) findViewById(R.id.update_list_select);
//		mSystemBackAdapter = new SystemBackAdapter1(SystemUpdateActivity.this, new String[] {
//				getResources().getString(R.string.net_update), getResources().getString(R.string.local_update) });
		mSystemBackAdapter = new SystemBackAdapter1(SystemUpdateActivity.this, new String[] {
				getResources().getString(R.string.net_update) });
		mSystemUpdate.setDividerHeight(0);
		mSystemUpdate.setAdapter(mSystemBackAdapter);
	}

	/**
	 * 监听事件的注册
	 */
	private void registerListener() {
		mSystemUpdate.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
				case NET_UPDATE:
					Tools.intentForward(SystemUpdateActivity.this, SystemNetUpdateActivity.class);
					break;
				case LOCAL_UPDATE:
					Tools.intentForward(SystemUpdateActivity.this, SystemLocalUpdateActivity.class);
					break;
				}
			}
		});
	}

}
