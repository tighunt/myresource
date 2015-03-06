package com.android.settings;

import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.TextView;

import com.android.settings.about.LegalInfoActivity;
import com.android.settings.city.CitySettings;
import com.android.settings.inputmethod.InputMethodAndLanguageSettingsActivity;
import com.android.settings.net.NetSettingActivity;
import com.android.settings.other.OtherSettings;
import com.android.settings.update.SystemBackupUpdate;
import com.android.settings.util.Tools;
import com.tvos.settings.adapter.SetttingsAdapter;

/**
 * <b>系统设置的主界面</b><br>
 * 
 * @author 杜聪甲(ducj@biaoqi.com.cn)
 * @date 2011-10-28 上午09:48:17
 * @since 1.0
 */
public class Settings extends Activity {

	// private final static int DESKTOP_SETTING = 0;

	private final static int APPLYCATION_SETTING = 0;

	private final static int NET_SETTING = 1;

	//private final static int SPLIT_SCREEN = 2;

	// private final static int CLOUND_UPDATE  = 2;

	private final static int CITY_SELECT  = 2;

	private final static int TIME_SETTING = 3;

	private final static int LANGUAGE_SETTING = 4;

	private final static int OTHER = 5;

	private final static int UPDATE_SETTING= 6;

	private final static int LEGAL_INFOR = 7;
	/**20130511 modify by cw
	private final static int USER_ACCOUNT= 6;
	*/
	private int[] setIcons = { R.drawable.app, R.drawable.wifi, R.drawable.city, R.drawable.time, R.drawable.language,
			R.drawable.about, R.drawable.desktop, R.drawable.law };
	/*private int[] setIcons = { R.drawable.app, R.drawable.wifi, R.drawable.cloud, R.drawable.city, R.drawable.time,
			R.drawable.language, R.drawable.about, R.drawable.user, R.drawable.desktop, 
			R.drawable.law };*/
	// private int[] setIcons = { R.drawable.desktop, R.drawable.apply,
	// R.drawable.net, R.drawable.lang, R.drawable.time,
	// R.drawable.desktop, R.drawable.about };

	private ListView mSetting;
	
	private TextView mSystemSettingTxt;
	private TextView mSystemBackTxt;
	private TextView mSystemOkTxt;
	private TextView mSystemChoiceTxt;

	private SetttingsAdapter mSetttingsAdapter;

	private Handler myHandler = new Handler();
	// 可用内存
	private String freeMemory;
	// 总内存
	private String totalMemory;
	
	private int listSelectedIndex = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("Settings", "SettingsActivity===================");
		setContentView(R.layout.system_setting);
		Log.d("Settings", "SettingsActivity*********************");
		System.out.println("软件名称：【系统设置】; 版本号：【1.0.0】 ;发布日期：【2012-04-27】 ;发布人：【zhoujf】;TAG：【6118】");

		// Get device dispiay metrics
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		int width = displayMetrics.widthPixels;
		int height = displayMetrics.heightPixels;

		Log.i("zjf", "width:" + width + " height" + height);

		findViews();

		registerListener();

		// String cmd = "busybox ifconfig"; // 命令

		// // Runtime对象
		//
		// Runtime runtime = Runtime.getRuntime();
		//
		// try {
		// // 执行命令，并且获得Process对象
		//
		// Process process = runtime.exec(cmd);
		//
		// // 获得结果的输入流
		//
		// InputStream input = process.getInputStream();
		//
		// BufferedReader br = new BufferedReader(new InputStreamReader(input));
		//
		// String strLine;
		//
		// while (null != (strLine = br.readLine())) {
		//
		// System.out.println("strLine:"+strLine);
		//
		// if(strLine.contains("inet addr") && strLine.contains("Bcast")){
		// System.out.println("inet addr:" + strLine);
		// System.out.println(strLine.split("Bcas")[0]);
		// System.out.println(strLine.split("Bcas")[0].split(":")[1]);
		// break;
		// }
		//
		// }
		//
		// } catch (IOException e) {
		//
		// e.printStackTrace();
		//
		// }
		
//        IntentFilter HotKeyFilter = new IntentFilter();
//        HotKeyFilter.addAction("com.haier.launcher.HOTKEY.TV");
//    	this.registerReceiver(hotKeyReceiver, HotKeyFilter);
		
		StatFs sf = new StatFs("/cache");
		long blockSize = sf.getBlockSize();
		long blockCount = sf.getBlockCount();
		long availCount = sf.getAvailableBlocks();

		System.out.println("block大小:"+ blockSize+"\nblock数目:"+ blockCount+"\n总大小:"+FormetFileSize(blockSize*blockCount));
		System.out.println("可用的block数目:"+ availCount+"\n可用大小:"+ FormetFileSize(availCount*blockSize));
	}
	
	public String FormetFileSize(long fileS) {// 转换文件大小
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		finish();
		
//		this.unregisterReceiver(hotKeyReceiver);   
	}

	@Override 
	public void onConfigurationChanged(Configuration newConfig) { 
	super.onConfigurationChanged(newConfig); 
		 // 手动国际化主页面
	     mSystemSettingTxt = (TextView) findViewById(R.id.system_setting_name);
	     //mSystemBackTxt  = (TextView) findViewById(R.id.back_tv);
	     //mSystemOkTxt = (TextView) findViewById(R.id.confirm_tv);
	     //mSystemChoiceTxt = (TextView) findViewById(R.id.choice_tv);
	     mSystemSettingTxt.setText(R.string.system_setting);     
	     //mSystemBackTxt.setText(R.string.back);     
	     //mSystemOkTxt.setText(R.string.confirm);     
	     //mSystemChoiceTxt.setText(R.string.choice);
		 if( mSetting != null){			
			mSetttingsAdapter = new SetttingsAdapter(Settings.this, new String[] {
					getString(R.string.application_manage),
					getString(R.string.net_setting),
					// getString(R.string.cloud_update),
					getString(R.string.city),
					getString(R.string.date_time_setting),
					getString(R.string.language_input_setting),
					getString(R.string.other),
					getString(R.string.user_account),
					getString(R.string.system_backup_update),
					getString(R.string.legal_infor) }, setIcons);
			mSetting.setDividerHeight(0);		 
			mSetting.requestFocus();		  
			mSetting.setAdapter(mSetttingsAdapter);	 
            } 
    }

	private void findViews() {

		mSetting = (ListView) findViewById(R.id.system_list_select);

		// mSetting.setSystemUiVisibility(View.STATUS_BAR_HIDDEN);
		// mSetting.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

		mSetttingsAdapter = new SetttingsAdapter(Settings.this, new String[] {
				getString(R.string.application_manage),
				getString(R.string.net_setting),
				// getString(R.string.cloud_update),
				getString(R.string.city),
				getString(R.string.date_time_setting),
				getString(R.string.language_input_setting),
				getString(R.string.other),
				//	getString(R.string.user_account),
				getString(R.string.system_backup_update),
				getString(R.string.legal_infor) }, setIcons);
		mSetting.setDividerHeight(0);
		mSetting.requestFocus();
		mSetting.setAdapter(mSetttingsAdapter);

		freeMemory = Tools.getAvailMemory(Settings.this);
		totalMemory = Tools.getTotalMemory(Settings.this);
	}

	private void registerListener() {
		mSetting.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				System.out.println("position++++++++++" + position);
				listSelectedIndex = position;
				switch (position) {
				case APPLYCATION_SETTING:
					Tools.intentForward(Settings.this, ApplicationSettings.class);
					break;
				case NET_SETTING:
					Tools.intentForward(Settings.this, NetSettingActivity.class);
					break;
				/*case CLOUND_UPDATE :
					Tools.intentForward(Settings.this, CloundSettings.class);
					break;*/
				case CITY_SELECT :	
					Tools.intentForward(Settings.this, CitySettings.class);
					break;
				case TIME_SETTING:
					Tools.intentForward(Settings.this, DateTimeSettings.class);
					break;
				case LANGUAGE_SETTING:
					Tools.intentForward(Settings.this, InputMethodAndLanguageSettingsActivity.class);
					break;
				case OTHER :	
					Tools.intentForward(Settings.this, OtherSettings.class);
					break;
				case UPDATE_SETTING:
					Intent in = new Intent(Settings.this, SystemBackupUpdate.class);
					in.putExtra("freeMemory", freeMemory);
					in.putExtra("totalMemory", totalMemory);
					Settings.this.startActivity(in);
					break;
				case LEGAL_INFOR:
					Tools.intentForward(Settings.this, LegalInfoActivity.class);
					break;
				/*case USER_ACCOUNT:	
					Tools.intentForward(Settings.this, UserAccountSettings.class);
					break;
				 * case D3_SETTING: Tools.intentForward(Settings.this, ThreeSettingActivity.class); break; case
				 * AUDIO_SETTING: Tools.intentForward(Settings.this, AudioSettingActivity.class); break; case
				 * VIDEO_SETTING: Tools.intentForward(Settings.this, VideoSettingActivity.class); break; 
			   *case ABOUT_SETTING: 
				 * Intent intent = new Intent(Settings.this, DeviceInfoSettings.class);
				 * intent.putExtra("freeMemory", freeMemory); intent.putExtra("totalMemory", totalMemory);
				 * Settings.this.startActivity(intent); break;
				 */

				}
			}
		});

		mSetting.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					myHandler.postAtFrontOfQueue(new Runnable() {
						public void run() {
							mSetting.setSelection(0);
						}
					});
				}
			}
		});
		
		mSetting.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				listSelectedIndex = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// listSelectedId = 0;
			}
		});
	}
	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_DOWN:
			if(listSelectedIndex == setIcons.length - 1){
				mSetting.setSelection(0);
			}
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			if(listSelectedIndex == 0){
				mSetting.setSelection(setIcons.length - 1);
			}
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Settings.this.finish();
		}
		return super.onKeyUp(keyCode, event);
	}
	
//    private BroadcastReceiver hotKeyReceiver  = new BroadcastReceiver() { 
//
//        @Override 
//        public void onReceive(Context context, Intent intent) { 
//    		Log.d("TVHot", "TVHotKeyReceiver.onReceive:" + intent.getAction());
//    		
//    		// 接受启动TV的快捷键 的广播，并启动TV
//    		Intent TVIntent = new Intent("mstar.tvsetting.ui.intent.action.RootActivity");
//    		TVIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//    		context.startActivity(TVIntent);
//
//        } 
//    };
	
}
