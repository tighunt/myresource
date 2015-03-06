package com.android.settings;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.android.settings.R;
import com.android.settings.about.LegalInfoActivity;
import com.android.settings.util.Storage;
import com.android.settings.util.Tools;
import com.tvos.settings.adapter.AboutAdapter;

/**
 * <b>系统设置中"关于"菜单</b>
 * 
 * <pre>
 * 主要显示：
 *      1.型号
 *      2.系统版本
 *      3.内存信息
 * </pre>
 * 
 * @author ducj
 * @date 2011-11-3 上午09:50:40
 * @since 1.0
 */
public class DeviceInfoSettings extends Activity {

	private final static int LEGAL_INFO = 4;

	// 关于子菜单以ListView进行显示
	private ListView aboutListView;

	// 关于子菜单的数据
	private AboutAdapter mAboutAdapter;

	private String freeMemory;

	private String totalMemory;

	// 子菜单要显示的内容
	private String[] content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		freeMemory = getIntent().getStringExtra("freeMemory");
		totalMemory = getIntent().getStringExtra("totalMemory");

		if (freeMemory == null || totalMemory == null) {
			freeMemory = Tools.getAvailMemory(DeviceInfoSettings.this);
			totalMemory = Tools.getTotalMemory(DeviceInfoSettings.this);
		}

		setContentView(R.layout.about);
		content = new String[] { getTVModelNumber(), getTVSystemVersion(), getTVMemoryInfo() };

		findViews();
		registerListeners();

	}

	/**
	 * 控件的初始化
	 */
	private void findViews() {
		aboutListView = (ListView) findViewById(R.id.about_list_select);

		mAboutAdapter = new AboutAdapter(this, new String[] { getResources().getString(R.string.model_number),
				getResources().getString(R.string.system_version), getResources().getString(R.string.memory_infor), },
				content);
		// getResources().getString(R.string.legal_infor)

		aboutListView.setDividerHeight(0);
		aboutListView.setAdapter(mAboutAdapter);
	}

	/**
	 * 监听器的注册
	 */
	private void registerListeners() {
		aboutListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				// 法律信息选项,跳转到法律信息Activity
				if (position == LEGAL_INFO) {
					Tools.intentForward(DeviceInfoSettings.this, LegalInfoActivity.class);
				}
			}
		});
	}

	/**
	 * 获取系统的型号
	 * 
	 * @return
	 */
	private String getTVModelNumber() {
		System.out.println("系统的型号" + Build.DISPLAY);
		return Build.DISPLAY;
	}

	/**
	 * 获取系统的版本号
	 * 
	 * @return
	 */
	private String getTVSystemVersion() {
		System.out.println("系统的版本号" + Build.VERSION.RELEASE);
		System.out.println("系统的版本号11" + android.os.Build.VERSION.SDK_INT);
		System.out.println("系统的版本号22" + android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH);
		
//		int   currentapiVersion=android.os.Build.VERSION.SDK_INT;
//		if(currentapiVersion >= android.os.Build.VERSION_CODES.FROYO){
//		         // Do something 
//		}else{
//		         // do something 
//		}

		
		return Build.VERSION.RELEASE;
	}

	/**
	 * 获取系统的内存信息
	 * 
	 * @return
	 */
	private String getTVMemoryInfo() {
		return freeMemory + "/" + totalMemory;
		// return "171.1MB" + "/" + "250.5MB";
	}

	/**
	 * 获取系统的磁盘容量
	 * 
	 * @return
	 */
	private String getTVDiskInfo() {
		long freeSize = new Storage().getDiskStorage().mFreeStrorage;
		long totalSize = new Storage().getDiskStorage().mTotalStorage;
		return Tools.sizeToM(freeSize) + "/" + Tools.sizeToM(totalSize);
	}
}
