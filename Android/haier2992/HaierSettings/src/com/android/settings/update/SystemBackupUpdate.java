package com.android.settings.update;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.android.settings.R;
import com.android.settings.util.Tools;
import com.tvos.settings.adapter.SystemBackAdapter1;

/**
 * <b>系统备份升级的主界面</b><br>
 * 
 * <pre>
 * 该界面包括：
 *      1：系统升级
 *     	 	2：系统备份
 *     	 	3：系统还原
 *      4：系统信息
 *      5: 恢复出厂  [2012-2-10]
 * </pre>
 * 
 * @author 杜聪甲(ducj@biaoqi.com.cn)
 * @date 2011-10-28 下午09:48:17
 * @since 1.0
 */
public class SystemBackupUpdate extends Activity {
	private final static int SYSTEM_UPDATE = 0;

//	private final static int SYSTEM_BACKUP = 1;
//
//	private final static int SYSTEM_RESTORE = 2;

//	private final static int SYSTEM_INFOR = 3;
	private final static int SYSTEM_INFOR = 1;// here please notice
	
	private final static int SYSTEM_RESTORE_FACTORY = 2;//[2012-2-10]

	private ListView mSystemBackupUpdate;

	private SystemBackAdapter1 mSystemBackAdapter;
	
	// 可用内存
	private String freeMemory;
	// 总内存
	private String totalMemory;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		freeMemory = getIntent().getStringExtra("freeMemory");
		totalMemory = getIntent().getStringExtra("totalMemory");
		
		setContentView(R.layout.system_backup_update);
		
		//startService(new Intent(this, SDCardListenerService.class));
		findViews();
		registerListener();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//stopService(new Intent(this, SDCardListenerService.class));
	}
	
	private void findViews() {
		mSystemBackupUpdate = (ListView) findViewById(R.id.backup_update_list_select);
		mSystemBackAdapter = new SystemBackAdapter1(
				SystemBackupUpdate.this, new String[] {
						getResources().getString(R.string.system_update),
//						getResources().getString(R.string.system_backup),
						getResources().getString(R.string.system_information),
						getResources().getString(R.string.system_restore_factory)});
		mSystemBackupUpdate.setDividerHeight(0);
		mSystemBackupUpdate.setAdapter(mSystemBackAdapter);
	}

	/**
	 * 监听器的注册
	 */
	private void registerListener() {
		mSystemBackupUpdate.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case SYSTEM_UPDATE:
					Tools.intentForward(SystemBackupUpdate.this,
							SystemUpdateActivity.class);
					break;
//				case SYSTEM_BACKUP:
//					Tools.intentForward(SystemBackupUpdateActivity.this,
//							SystemBackupActivity.class);
//					break;
//				case SYSTEM_RESTORE:
//					Tools.intentForward(SystemBackupUpdateActivity.this,
//							SystemRestoreActivity.class);
//					break;
				case SYSTEM_INFOR:
					Intent intent = new Intent(SystemBackupUpdate.this,
							SystemInformation.class);
					intent.putExtra("freeMemory", freeMemory);
					intent.putExtra("totalMemory", totalMemory);
					SystemBackupUpdate.this.startActivity(intent);
					break;
				case SYSTEM_RESTORE_FACTORY:
					Tools.intentForward(SystemBackupUpdate.this,
							SystemRestoreFactoryActivity.class);
					break;
				}
			}
		});
	}
}
