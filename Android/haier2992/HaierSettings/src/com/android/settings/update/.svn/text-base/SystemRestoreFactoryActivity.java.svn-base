package com.android.settings.update;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.android.settings.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.IpAssignment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.Context;
import android.app.TvManager;  
/**20130511 modify by cw
/*import com.tvos.atv.AtvManager;
import com.tvos.atv.AtvScanManager;
import com.tvos.atv.AtvScanManager.EnumSetProgramCtrl;
import com.tvos.common.TvManager;
import com.tvos.common.TvManager.EnumPowerOnMusicMode;
import com.tvos.common.TvManager.EnumScreenMuteType;
import com.tvos.common.exception.TvCommonException;
import com.tvos.common.vo.TvOsType.EnumInputSource;
import com.tvos.factory.FactoryManager;
import com.tvos.factory.FactoryManager.EnumAcOnPowerOnMode;
*/

/**
 * 恢复出厂设置
 * 
 * @author 严海东 (yanhd@biaoqi.com.cn)
 * @since 1.0 2012-2-10
 */

public class SystemRestoreFactoryActivity extends Activity {

	private final static String TAG = "SystemRestoreFactoryActivity.CPP";
	// /确认按钮。
	private Button mConfirm;
	private Activity mThisActivity;
	//private FactoryManager fm = TvManager.getFactoryManager();
	//-->20130511 modify by cw
	private static SystemRestoreFactoryActivity mDesk = null;
	/* TV Manager JNI */
	private static TvManager mTvManager;  
	private static Context mContext;
	//<--
	enum EnumPowerOnMusicMode{
		E_POWERON_MUSIC_OFF,
		E_POWERON_MUSIC_ON,
		E_POWERON_MUSIC_DEFAULT
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e(TAG, "===>SystemRestoreFactoryActivity");
		setContentView(R.layout.system_restore_factory);
		mTvManager = (TvManager)this.getSystemService("tv");
		mThisActivity = SystemRestoreFactoryActivity.this;
		findViews();
		registerListener();
	}

	private void findViews() {
		mConfirm = (Button) findViewById(R.id.clear_button);
	}

	private void registerListener() {

		mConfirm.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.e(TAG, "===>MASTER_CLEAR");
				
				//20130511 modify by cw				
/*				if (mRestoreTVdataCheckbox.isChecked()){
					System.out.println("===>is checked");
					boolean ret = restoreToDefault();
				}*/
				
				
				AlertDialog.Builder builder = new Builder(mThisActivity);
				builder.setMessage(mThisActivity.getResources().getString(
						R.string.restore_factory_confirm_restore));
				builder.setTitle(mThisActivity.getResources().getString(
						R.string.system_restore_factory));
				
				builder.setPositiveButton(
						mThisActivity.getResources()
								.getString(R.string.ok),
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								Log.e(TAG, "ok");
								boolean ret = restoreToDefault();
								if (ret){
									Log.e(TAG, "===>sucess1");
								}else{
									Log.e(TAG, "===>failed1");
								}
								Toast.makeText(SystemRestoreFactoryActivity.this,SystemRestoreFactoryActivity.this.getResources().getString(R.string.restore_factory_system_reboot),
										Toast.LENGTH_LONG).show();
								// 2012-05-10 系统复位时，复位开机音乐。
								try {
									//mTvManager.setEnvironmentPowerOnMusicMode(EnumPowerOnMusicMode.E_POWERON_MUSIC_OFF);//20130511 modify by cw
								} catch (Exception e) {
		
								}
								Log.e(TAG, "===>MASTER_CLEAR");
								sendBroadcast(new Intent("android.intent.action.MASTER_CLEAR"));
								
								dialog.dismiss();
							}
						});
				
				builder.setNegativeButton(
						mThisActivity.getResources()
								.getString(R.string.cancle),
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Log.e(TAG, "cancel ！");
								dialog.dismiss();
							}
						});
				
				builder.create().show();
/*					
				
				boolean ret = restoreToDefault();
				if (ret){
					Log.e(TAG, "===>sucess2");
				}else{
					Log.e(TAG, "===>failed2");
				}
				
				Toast.makeText(SystemRestoreFactoryActivity.this,SystemRestoreFactoryActivity.this.getResources().getString(R.string.restore_factory_system_reboot),
								Toast.LENGTH_LONG).show();
				sendBroadcast(new Intent("android.intent.action.MASTER_CLEAR"));
				
				Log.e(TAG, "===>ACTION_REBOOT");
				
				
			 Intent reboot = new
				 Intent(Intent.ACTION_REBOOT);
				 reboot.putExtra("nowait", 1);
				 reboot.putExtra("interval", 1);
				 reboot.putExtra("window", 0);
				 sendBroadcast(reboot);*/
				
				
				//sendBroadcast(new Intent(Intent.ACTION_SHUTDOWN));
				 //sendBroadcast(new Intent(Intent.ACTION_REBOOT));
				//sendBroadcast(new Intent("android.intent.action.MASTER_CLEAR"));
				// Intent handling is asynchronous -- assume it will happen
				// soon.
				//finish();
			}
		});
	}

	public boolean restoreToDefault()
	{
		boolean result = false;
/*		File srcFile = new File("/tvdatabase/DatabaseBackup/",
				"user_setting.db");
		File destFile = new File("/tvdatabase/Database/", "user_setting.db");

		result = copyFile(srcFile, destFile);
		Log.e(TAG, "===>result 1 = "+result);
		if (result == false) {
			//ret = false;
			return false;
		}

		srcFile = new File("/tvdatabase/DatabaseBackup/", "factory.db");
		destFile = new File("/tvdatabase/Database/", "factory.db");
		result = copyFile(srcFile, destFile);
		Log.e(TAG, "===>result 2= "+result);
		if (result == false) {
			return false;
		}
		clearChannel();*/
		
		//20140226 modify by cw		
		Toast.makeText(SystemRestoreFactoryActivity.this,SystemRestoreFactoryActivity.this.getResources().getString(R.string.restore_factory_system_reboot),
				Toast.LENGTH_LONG).show();
		try {
			Log.e(TAG, "clearEeprom success=======================!!!!");
			mTvManager.clearEeprom(false);
			//fm.setUartEnv(false);
			//fm.setEnvironmentPowerMode(EnumAcOnPowerOnMode.values()[2]);
			mTvManager.setStartPowerOnMode(false);//power on standby
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sendBroadcast(new Intent("android.intent.action.MASTER_CLEAR"));
//-->20130511 modify by cw
		Log.e(TAG, "===>ACTION_REBOOT");
		Intent reboot = new
				Intent(Intent.ACTION_REBOOT);
		reboot.putExtra("nowait", 1);
		reboot.putExtra("interval", 1);
		reboot.putExtra("window", 0);
		sendBroadcast(reboot);
//
		return true;
	}

	private void clearChannel()
		{
			try {
				
				//mTvManager.setChannelDel(,true); //20130511 modify by cw
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	/**
	 * 
	 * Copy data from a source stream to destFile.
	 * 
	 * Return true if succeed, return false if failed.
	 */

	private boolean copyToFile(InputStream inputStream, File destFile) {

		try {
			if (destFile.exists()) {
				destFile.delete();
			}

			FileOutputStream out = new FileOutputStream(destFile);

			try {
				byte[] buffer = new byte[4096];
				int bytesRead;

				while ((bytesRead = inputStream.read(buffer)) >= 0) {
					Log.d(" out.write(buffer, 0, bytesRead);",
							" out.write(buffer, 0, bytesRead);");
					out.write(buffer, 0, bytesRead);
				}

			} finally {
				out.flush();

				try {
					out.getFD().sync();
				} catch (IOException e) {
				}

				out.close();
			}

			return true;
		} catch (IOException e) {
			Log.d("copyToFile(InputStream inputStream, File destFile)",
					e.getMessage());
			return false;
		}
	}

	// copy a file from srcFile to destFile, return true if succeed, return
	// false if fail

	private boolean copyFile(File srcFile, File destFile) {
		boolean result = false;
		chmodFile(srcFile);//20130511 modify by cw
		chmodFile(destFile);//20130511 modify by cw
		try {
			InputStream in = new FileInputStream(srcFile);
			try {
				result = copyToFile(in, destFile);
			} finally {
				in.close();
			}

		} catch (IOException e) {
			Log.d("copyFile(File srcFile, File destFile)", e.getMessage());
			result = false;
		}

		chmodFile(destFile);
		return result;
	}

	private void chmodFile(File destFile)
	{
		try {
			String command = "chmod 666 " + destFile.getAbsolutePath();
			Log.i("zyl", "command = " + command);

			Runtime runtime = Runtime.getRuntime();
			Process proc = runtime.exec(command);

		} catch (IOException e) {
			Log.i("zyl", "chmod fail!!!!");
			e.printStackTrace();
		}
	}

}
