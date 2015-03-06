
package com.android.settings.update;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.DecimalFormat;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RecoverySystem;
import android.os.RecoverySystem.ProgressListener;
import android.os.StatFs;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.settings.R;
import com.android.settings.update.UpdateService.ServiceBinder;
import com.android.settings.util.Tools;

/*import com.tvos.common.exception.TvCommonException;
import com.tvos.factory.FactoryManager;
import com.tvos.factory.FactoryManager.EnumAcOnPowerOnMode;*/
import android.app.TvManager;
/**
 * 系统网络升级
 * 
 * @author 杜聪甲(ducj@biaoqi.com.cn)
 * @since 1.0 2011-11-18
 */
public class SystemNetUpdateActivity extends Activity {

    private ServiceBinder mServiceBinder;

    private final static String TAG = "NetUpdateActivity";

    private final static String NAME = "share_pres";
    
    private final static String DOWNLOAD_ADDRESS = "url";

    private final static String VERSION = "version";

    private final static int STATE_UPDATE_SUCCESS = 100;

    private final static int STATE_UPDATE_PROGRESS = 101;

    private final static int STATE_UPDATE_ERROR = 102;

    private final static int GET_VERSION_INFOR = 6;
    
    private final static int DOWNLOADING = 1000000001;


    // private final static String BROAD_ACTION = "SDCARD_ACTION";
    private final static String PERCENT = "percent";

    private Button mBtnUpdate;

    private TextView mtxtUpdateInfo = null;

    private String mTxtInfo = "";

    private TextView title;

    // 更新信息
    private String updateInfor;

    // 系统的版本信息
    private String systemVersion;

    // 新的版本信息
    private String newVersion;

    // 是否有新的版本
    private boolean hasNewVersion = false;

    private VersionInfor mInfor;

    // 更新包的下载路径
    private String downUrl;

    // SDCard各种操作的提示信息
    private String msg;

    private LinearLayout mLayout;

    // 网络升级帮助信息
    private LinearLayout mLayout_help;

    // 进度条
    private ProgressBar mProgressBar;

    // 显示当前进度
    private TextView current_progress;

    // 当前进度
    private int mProgress = 0;

    // 用于判断是否正在更新
    private boolean isUpdating = false;

    private String size;

    private String mac;

    public static boolean check = false;

   // private FactoryManager fm = TvManager.getFactoryManager();

    // private Updater updater = null;

    private Updater mUpdater = null;

    private Handler mHandler = new Handler();

    private Handler mUpdateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case STATE_UPDATE_SUCCESS:
                    System.out.println("********* handleMessage UPDATE_SUCCESS");
                    mtxtUpdateInfo.setText(mTxtInfo + getString(R.string.updated));
                    break;
                case STATE_UPDATE_PROGRESS:
                    current_progress.setText(mProgress + "%");
                    mProgressBar.setProgress(mProgress);
                    if (mProgress == 100) {
                        mtxtUpdateInfo.setText(mTxtInfo + getString(R.string.check_success));
                        mProgressBar.setVisibility(View.INVISIBLE);
                        mLayout.setVisibility(View.INVISIBLE);
                    }
                    break;

                case STATE_UPDATE_ERROR:
                    System.out.println("********* handleMessage CHECK_UPDATE_ERROR");
                    mtxtUpdateInfo.setText(R.string.msg_upgrade_err);
                    mBtnUpdate.setEnabled(true);
                    mBtnUpdate.setText(getString(R.string.exit));
                    mBtnUpdate.setBackgroundResource(R.drawable.button_bg);

                    mProgressBar.setVisibility(View.INVISIBLE);
                    mLayout.setVisibility(View.INVISIBLE);
                    mServiceBinder.setmState(Updater.STATE_UPDATE_ERROR);
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * 升级状态 0:读取版本信息 1:下载 2:升级 -1:退出
     */
    private int step = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.system_net_update);
        check = false;
        Intent intent = new Intent(SystemNetUpdateActivity.this, UpdateService.class);
        SystemNetUpdateActivity.this.bindService(intent, serviceConnection,
                Context.BIND_AUTO_CREATE);

        findViews();
        init();

        NetInfo net = new NetInfo(this);
        net.getNetInfo();
        mac = net.getMac();
        Log.d("Updater", "ip=" + net.getIpAddr());
        Log.d("Updater", "name=" + net.getNetName());
        Log.d("Updater", "mac=" + net.getMac());

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilter.addDataScheme("file");
        registerReceiver(sdReceiver, intentFilter);

        registerListeners();

    }

    ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mServiceBinder = (ServiceBinder) service;
            Log.d("Updater", "mServiceBinder.getmState()=======" + mServiceBinder.getmState());
            mUpdater = new Updater(mac, mServiceBinder);

            
            if (Updater.STATE_DOWNLOADING == mServiceBinder.getmState()) {
                mTxtInfo += mServiceBinder.getmUpgradeCode() + "\n";
                mTxtInfo += getString(R.string.new_version);
                mTxtInfo += mServiceBinder.getmNewVersion() + "\n";
                mtxtUpdateInfo.setText(mTxtInfo);
                mLayout.setVisibility(View.VISIBLE);
                mLayout_help.setVisibility(View.VISIBLE);
                mBtnUpdate.setVisibility(View.VISIBLE);
                mBtnUpdate.setText(getString(R.string.download));
                mBtnUpdate.setEnabled(false);
                mBtnUpdate.setBackgroundResource(R.drawable.one_px);
            } else if (Updater.STATE_DOWNLOAD_OK == mServiceBinder.getmState()) {
                NotificationManager notificationManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.cancel(DOWNLOADING);
                mTxtInfo += mServiceBinder.getmUpgradeCode() + "\n";
                mTxtInfo += getString(R.string.new_version);
                mTxtInfo += mServiceBinder.getmNewVersion() + "\n";
                mLayout.setVisibility(View.VISIBLE);
                mLayout_help.setVisibility(View.VISIBLE);
                current_progress.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                mtxtUpdateInfo.setVisibility(View.VISIBLE);
                mBtnUpdate.setVisibility(View.VISIBLE);
                current_progress.setText("100%");
                mProgressBar.setProgress(100);
                mtxtUpdateInfo.setText(mTxtInfo + getString(R.string.msg_download_ok));

                mBtnUpdate.setText(getString(R.string.update));
                mBtnUpdate.setBackgroundResource(R.drawable.button_bg);
            } else {
                mUpdater.checkVersion();
            }
            mHandler = new Handler();
            mHandler.postDelayed(mRunable, 2000);

        }
    };

    @Override
    public void onBackPressed() {
        check = true;
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        unbindService(serviceConnection);
        super.onDestroy();
        finish();
        unregisterReceiver(sdReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onResume");
        mProgress = getPercentData(PERCENT);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // /if (isUpdating) {
        // return true;
        // }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 组件的初始化
     */
    private void findViews() {

        mInfor = new VersionInfor();

        mtxtUpdateInfo = (TextView) findViewById(R.id.net_update_info);
        mtxtUpdateInfo.setText(getString(R.string.check_new_version));
        title = (TextView) findViewById(R.id.updateTitle);

        mBtnUpdate = (Button) findViewById(R.id.immediate);
        mBtnUpdate.setBackgroundResource(R.drawable.one_px);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        current_progress = (TextView) findViewById(R.id.current_progress);
        mLayout = (LinearLayout) findViewById(R.id.show_progress);
        mLayout_help = (LinearLayout) findViewById(R.id.upgrade_help);
    }

    /**
     * 显示当前版本信息
     */

    private void init() {
        title.setText(getString(R.string.system_net_update));
        mTxtInfo = getString(R.string.current_version);
        mtxtUpdateInfo.setText(getString(R.string.check_new_version));
        mBtnUpdate.setBackgroundResource(R.drawable.one_px);
        mBtnUpdate.setVisibility(View.INVISIBLE);
    }

    /**
     * 注册监听器
     */
    private void registerListeners() {
        mBtnUpdate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                int state = mServiceBinder.getmState();
                Log.d(TAG, "onClick, state=" + state);

                if (Updater.STATE_RECIECE_VER_OK == state) {
                    // mLayout.setVisibility(View.INVISIBLE);
                    mBtnUpdate.setEnabled(false);
                    mBtnUpdate.setBackgroundResource(R.drawable.one_px);

                    Intent intent = new Intent(SystemNetUpdateActivity.this, UpdateService.class);
                    SystemNetUpdateActivity.this.startService(intent);
                    mHandler.postDelayed(mRunable, 2000);
                } else if (Updater.STATE_DOWNLOAD_OK == state) {
                    mTxtInfo += getString(R.string.check_package);
                    mtxtUpdateInfo.setText(mTxtInfo);
                    // isDownloaded = false;
                    mLayout.setVisibility(View.VISIBLE);
                    mLayout_help.setVisibility(View.VISIBLE);
                    current_progress.setText("");
                    mProgressBar.setProgress(0);
                    mProgressBar.setVisibility(View.VISIBLE);

                    mBtnUpdate.setEnabled(false);
                    mBtnUpdate.setText(getString(R.string.update));
                    mBtnUpdate.setBackgroundResource(R.drawable.one_px);

                    new UpdateTOVSThread().start();
                } else {
                    finish();
                }

            }
        });
    }

    /**
     * 检测TV系统是否有新的版本
     * 
     * @return
     */
    private boolean checkUpdateVersion() {

        updateInfor += getString(R.string.check_new_version) + "\n";
        mtxtUpdateInfo.setText(updateInfor);

        String directoryName = "";
        // 获得cache目录可用大小
        long cacheFreeSize = getCacheFreeSize();
        size = mInfor.getSize() + "";

        // nand版本将升级包下载到U盘，EMMC版本将升级包下载到cache分区
        System.out.println("cacheFreeSize:" + cacheFreeSize);
        System.out.println("size:" + size);
        if (cacheFreeSize > Long.parseLong(size)) {
            directoryName = "/cache/versioninfor";
        } else {
            directoryName = Environment.getExternalStorageDirectory().toString() + "/versioninfor";
        }

        String infor = JSONData.getInfor();
        System.out.println("infor:" + infor);

        // 将字符串写入指定文件(当指定的父路径中文件夹不存在时，会最大限度去创建，以保证保存成功！)
        Tools.string2File(infor, directoryName);

        // 获取新的版本号
        newVersion = mInfor.getVersion();
        System.out.println("newVersion:" + newVersion);

        systemVersion = Build.VERSION.INCREMENTAL;// V1.0.0.0
        System.out.println("systemVersion:" + systemVersion);

        // 检查是否有更新的
        hasNewVersion = checkVersion();

        // 通过是否返回url地址来判断是否需要更新
        downUrl = mInfor.getUrl();
        System.out.println("downUrl:" + downUrl);

        size = mInfor.getSize() + "";
        System.out.println("size:" + size);

        if (downUrl != null) {
            commitURLValue(DOWNLOAD_ADDRESS, downUrl);
            commitURLValue(VERSION, newVersion);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 检查是否有更新的
     * 
     * @return
     */
    private boolean checkVersion() {
        // 版本号相同
        if (systemVersion.equals(newVersion)) {
            hasNewVersion = false;
        }
        // 版本号不同
        else {
            // 进行比较
            systemVersion = systemVersion.substring(1);
            newVersion = newVersion.substring(1);
            System.out.println(systemVersion);
            System.out.println(newVersion);

            String systemOne = "";
            String newOne = "";

            if (systemVersion.contains(".")) {
                systemOne = systemVersion.split("\\.")[0];
                System.out.println(systemOne);
            }

            if (newVersion.contains(".")) {
                newOne = newVersion.split("\\.")[0];
                System.out.println(newOne);
            }

            if (Integer.parseInt(newOne) > Integer.parseInt(systemOne)) {
                System.out.println("发现新版本了 one");
                hasNewVersion = true;
            } else {
                System.out.println("继续比较 two");

                String systemTwo = "";
                String newTwo = "";

                systemTwo = systemVersion.split("\\.")[1];
                newTwo = newVersion.split("\\.")[1];

                System.out.println(systemTwo);
                System.out.println(newTwo);

                if (Integer.parseInt(newTwo) > Integer.parseInt(systemTwo)) {
                    System.out.println("发现新版本了 two");
                    hasNewVersion = true;
                } else {
                    System.out.println("继续比较 three");

                    String systemThree = "";
                    String newThree = "";

                    systemThree = systemVersion.split("\\.")[2];
                    newThree = newVersion.split("\\.")[2];

                    System.out.println(systemThree);
                    System.out.println(newThree);

                    if (Integer.parseInt(newThree) > Integer.parseInt(systemThree)) {
                        System.out.println("发现新版本了  three");
                        hasNewVersion = true;
                    } else {
                        System.out.println("继续比较 four");

                        String systemFour = "";
                        String newFour = "";

                        systemFour = systemVersion.split("\\.")[3];
                        newFour = newVersion.split("\\.")[3];

                        System.out.println(systemFour);
                        System.out.println(newFour);

                        if (Integer.parseInt(newFour) > Integer.parseInt(systemFour)) {
                            System.out.println("发现新版本了 four");
                            hasNewVersion = true;
                        } else {
                            // 已经比较完了
                            hasNewVersion = false;
                        }
                    }
                }
            }

            System.out.println("hasNewVersion:" + hasNewVersion);
        }

        return hasNewVersion;
    }

    /**
     * 更新TVOS
     */
    private void updateTVOS() {
        if (vertify()) {
            // 执行更新系统的操作
            System.out.println("********* vertify ok");
            mUpdateHandler.sendEmptyMessage(STATE_UPDATE_SUCCESS);
        } else {
            System.out.println("********* vertify err");
            mUpdateHandler.sendEmptyMessage(STATE_UPDATE_ERROR);
        }
    }

    /**
     * 对下载的更新包进行验证
     */
    private boolean vertify() {

        String pkgName = mServiceBinder.getmSavePath();
        Log.d(TAG, "vertify(), pkgName = " + pkgName);
        File file = new File(pkgName);

        try {

            RecoverySystem.ProgressListener progressListener = new ProgressListener() {

                @Override
                public void onProgress(int progress) {
                    mProgress = progress;
                    mUpdateHandler.sendEmptyMessage(STATE_UPDATE_PROGRESS);
                }
            };

            // 有效性验证
            RecoverySystem.verifyPackage(file, progressListener, null);
            System.out.println("+++++++++++verify+++++++++++");

            // 保存下载百分比数据
            commitPercentValue(PERCENT, 0);

            // 安装
            System.out.println("begin install");
            /*try {//20130511 modify by cw
                fm.setEnvironmentPowerMode(EnumAcOnPowerOnMode.values()[2]);
            } catch (TvCommonException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }*/
            RecoverySystem.installPackage(this, file);
            System.out.println("end install");

            isUpdating = false;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 解析JSON数据，获取版本的详细信息
     */
    private void getVersionInfor() {
        /*
         * try { JSONObject reqJson = new JSONObject(JSONData.getInfor()); if
         * (reqJson.getInt("err") == 0) { JSONObject jsonObject =
         * reqJson.getJSONObject("bd");
         * mInfor.setDs(jsonObject.get("ds").toString());
         * mInfor.setVersion(jsonObject.get("ver").toString());
         * mInfor.setUds(jsonObject.get("uds").toString());
         * mInfor.setUrl(jsonObject.get("url").toString());
         * mInfor.setSize(jsonObject.getLong("size"));
         * mInfor.setMd(jsonObject.getString("md5"));
         * mInfor.setForce(jsonObject.getInt("fd")); } } catch (JSONException e)
         * { e.printStackTrace(); }
         */

        /*
         * if(updater.checkVersion()){//下载版本文件
         * if(updater.getState()==updater.STATE_RECIECE_VER_OK){//检查版本文件是否有效
         * }else if(updater.getState()==updater.STATE_RECIECE_VER_ERR){ } }
         */
        mUpdater.checkVersion();
        Message getVersionInfor = new Message();
        getVersionInfor.what = GET_VERSION_INFOR;
        // handler.sendMessage(getVersionInfor);
    }

    /**
     * 保存是否自动获取IP地址
     */
    private void commitURLValue(String key, String value) {
        SharedPreferences preference = getSharedPreferences(NAME, Context.MODE_PRIVATE);
        Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }


    private BroadcastReceiver sdReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "sdcard action:::::" + action);
            // /[2012-3-28 yanhd]update
            if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
                msg = getString(R.string.sdcard_insert) + "\n";
            } else if (Intent.ACTION_MEDIA_UNMOUNTED.equals(action)) {
                msg = getString(R.string.sdcard_remove) + "\n";
            }
            mtxtUpdateInfo.setText(msg);
        }
    };

    /**
     * 获取下载百分比数据
     * 
     * @param key
     * @return
     */
    private int getPercentData(String key) {
        SharedPreferences preference = getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return preference.getInt(key, 0);
    }

    /**
     * 保存下载百分比数据
     * 
     * @param key
     * @param percent
     */
    private void commitPercentValue(String key, int percent) {
        SharedPreferences preference = getSharedPreferences(NAME, Context.MODE_PRIVATE);
        Editor edit = preference.edit();
        edit.putInt(key, percent);
        edit.commit();
    }

    // 用于获取Json数据的线程
    class GetJsonDataThread extends Thread {

        @Override
        public void run() {
            super.run();
            hasNewVersion = checkUpdateVersion();
            // 有SDCArd发送Handler消息提示用户可以下载更新包
            // handler.sendEmptyMessage(CHECK_NEW_VERSION);
        }
    }

    // 用于更新TVOS数据的线程
    class UpdateTOVSThread extends Thread {
        @Override
        public void run() {
            super.run();
            updateTVOS();
        }
    }

    // 用于更新TVOS数据的线程
    class GetVersionInfor extends Thread {
        @Override
        public void run() {
            super.run();
            // 获取版本的详细信息
            getVersionInfor();
        }
    }

    private long getCacheFreeSize() {

        StatFs sf = new StatFs("/cache");

        long blockSize = sf.getBlockSize();
        long availCount = sf.getAvailableBlocks();
        return availCount * blockSize;
    }

    private String FormetFileSize(long size) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSize = "";
        if (size < 1024) {
            fileSize = df.format((double) size) + "B";
        } else if (size < 1048576) {
            fileSize = df.format((double) size / 1024) + "K";
        } else if (size < 1073741824) {
            fileSize = df.format((double) size / 1048576) + "M";
        } else {
            fileSize = df.format((double) size / 1073741824) + "G";
        }
        return fileSize;
    }

    private void getProgress() {
        int progress = 0;
        int curr = mServiceBinder.getDownPos();
        int total = mServiceBinder.getLength();
        if (total > 0) {
            progress = (int) (curr * 100.0 / total);
        }
        StringBuffer sb = new StringBuffer();
        sb.append(getResources().getString(R.string.msg_downloading));
        sb.append(" (");
        sb.append(FormetFileSize(curr));
        sb.append(" / ");
        sb.append(FormetFileSize(total));
        sb.append(" )...");
        mBtnUpdate.setVisibility(View.VISIBLE);
        mLayout_help.setVisibility(View.VISIBLE);
        mLayout.setVisibility(View.VISIBLE);
        mtxtUpdateInfo.setVisibility(View.VISIBLE);
        current_progress.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mtxtUpdateInfo.setText(mTxtInfo + sb.toString());
        current_progress.setText(progress + "%");
        mProgressBar.setProgress(progress);
    }

    private Runnable mRunable = new Runnable() {

        @Override
        public void run() {
            Log.d("Updater", "check" + check);
            if (check) {
                return;
            }

            boolean bPost = true;
            Log.d("Updater", "Runable.run()");
            int state = mServiceBinder.getmState();
            Log.d("Updater", "Runable.run().state=" + state);

            switch (state) {
                case Updater.STATE_NOTHING: {
                    break;
                }
                case Updater.STATE_REQUEST_VER: {
                    break;
                }
                case Updater.STATE_READ_CFG_ERR: {
                    /* 读取配置文件出错 */
                    bPost = false;
                    mtxtUpdateInfo.setText(R.string.msg_readconfig_err);

                    mBtnUpdate.setText(getString(R.string.exit));
                    mBtnUpdate.setBackgroundResource(R.drawable.button_bg);
                    mBtnUpdate.setVisibility(View.VISIBLE);
                    break;
                }
                case Updater.STATE_READ_MAC_ERR: {
                    /* 读取mac出错 */
                    bPost = false;
                    // mtxtUpdateInfo.setText(R.string.msg_readconfig_err);
                    mtxtUpdateInfo.setText(R.string.msg_net_err);

                    mBtnUpdate.setText(getString(R.string.exit));
                    mBtnUpdate.setBackgroundResource(R.drawable.button_bg);
                    mBtnUpdate.setVisibility(View.VISIBLE);
                    break;
                }
                case Updater.STATE_RECIECE_VER_ERR: {
                    /* 请求版本信息出错 */
                    bPost = false;
                    mtxtUpdateInfo.setText(R.string.msg_net_err);

                    mBtnUpdate.setText(getString(R.string.exit));
                    mBtnUpdate.setBackgroundResource(R.drawable.button_bg);
                    mBtnUpdate.setVisibility(View.VISIBLE);
                    break;
                }
                case Updater.STATE_RECIECE_VER_OK: {
                    /* 收到版本信息 */
                    bPost = false;
                    mTxtInfo += mServiceBinder.getmUpgradeCode() + "\n";
                    mTxtInfo += getString(R.string.new_version);
                    mTxtInfo += mServiceBinder.getmNewVersion() + "\n";

                    mtxtUpdateInfo.setText(mTxtInfo);

                    mBtnUpdate.setText(getString(R.string.download));
                    mBtnUpdate.setBackgroundResource(R.drawable.button_bg);
                    mBtnUpdate.setVisibility(View.VISIBLE);

                    mLayout.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(0);
                    mProgressBar.setVisibility(View.VISIBLE);

                    mLayout_help.setVisibility(View.VISIBLE);

                    Updater.writeStringToFile("OK", "/cache/Setttings2.txt");

                    break;
                }
                case Updater.STATE_NO_PACKAGE: {
                    /* 没用可用升级包 */
                    bPost = false;
                    mtxtUpdateInfo.setText(R.string.msg_version_newest);

                    mBtnUpdate.setText(getString(R.string.exit));
                    mBtnUpdate.setBackgroundResource(R.drawable.button_bg);
                    mBtnUpdate.setVisibility(View.VISIBLE);
                    break;
                }
                case Updater.STATE_VER_IS_NEWEST: {
                    /* 当前已是最新版本 */
                    bPost = false;
                    mtxtUpdateInfo.setText(R.string.msg_version_newest);

                    mBtnUpdate.setText(getString(R.string.exit));
                    mBtnUpdate.setBackgroundResource(R.drawable.button_bg);
                    mBtnUpdate.setVisibility(View.VISIBLE);
                    break;
                }
                case Updater.STATE_DOWNLOADING: {
                    /* 正在下载中 */
                    getProgress();
                    break;
                }
                case Updater.STATE_DOWNLOAD_ERR: {
                    /* 下载出错 */
                    bPost = false;
                    mtxtUpdateInfo.setText(R.string.msg_download_err);

                    mBtnUpdate.setText(getString(R.string.exit));
                    mBtnUpdate.setBackgroundResource(R.drawable.button_bg);
                    break;

                }
                case Updater.STATE_DOWNLOAD_OK: {
                    /* 升级包下载完成 */
                    bPost = false;
                    current_progress.setText("100%");
                    mProgressBar.setProgress(100);
                    mtxtUpdateInfo.setText(mTxtInfo + getString(R.string.msg_download_ok));

                    mBtnUpdate.setText(getString(R.string.update));
                    mBtnUpdate.setBackgroundResource(R.drawable.button_bg);
                    break;
                }
                case Updater.STATE_NOT_ENOUGH_STORAGE: {
                    bPost = false;

                    String storageFlag = readFileByLines("/var/Setttings1.txt");
                    Updater.writeStringToFile("null", "/var/Setttings1.txt");

                    Log.d("Updater", "SystemNetUpdateActivity.storageFlag:" + storageFlag);
                    Log.d("Updater",
                            "SystemNetUpdateActivity.IsSdcard:"
                                    + Environment.getExternalStorageState().equals(
                                            Environment.MEDIA_MOUNTED));

                    if (storageFlag != null && storageFlag.equals("cache")) {
                        mtxtUpdateInfo.setText(R.string.err_not_enough_storage_for_cache);
                    } else if (Environment.getExternalStorageState().equals(
                            Environment.MEDIA_MOUNTED)
                            && storageFlag != null && storageFlag.equals("sdcard")) {
                        mtxtUpdateInfo.setText(R.string.err_not_enough_storage_for_sdcard);
                    } else {
                        mtxtUpdateInfo.setText(R.string.err_not_enough_storage);
                    }

                    mBtnUpdate.setText(getString(R.string.exit));
                    mBtnUpdate.setBackgroundResource(R.drawable.button_bg);
                    break;
                }
            }

            if (bPost) {
                mHandler.postDelayed(mRunable, 2000);
            } else {
                mBtnUpdate.setEnabled(true);
                mBtnUpdate.setBackgroundResource(R.drawable.button_bg);
                mBtnUpdate.requestFocus();
            }
        }
    };

    /**
     * 以行为单位读取文件
     * 
     * @param fileName 文件名
     */
    public static String readFileByLines(String path) {
        Log.d("Updater", "SystemNetUpdateActivity.readFileByLines().path:" + path);
        File file = new File(path);
        BufferedReader reader = null;
        String context = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                Log.d("Updater", "SystemNetUpdateActivity.readFileByLines():" + tempString);
                context = tempString;
            }
            reader.close();
        } catch (IOException e) {
            // e.printStackTrace();
            Log.d("Updater",
                    "SystemNetUpdateActivity.readFileByLines().IOException:"
                            + e.getLocalizedMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return context;
    }
}
