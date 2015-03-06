
package com.android.settings.update;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.dtools.ini.BasicIniFile;
import org.dtools.ini.FormatException;
import org.dtools.ini.IniFile;
import org.dtools.ini.IniFileReader;
import org.dtools.ini.IniSection;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.android.settings.R;
import com.android.settings.other.OtherSettings;

public class UpgradeReceiver extends BroadcastReceiver {

    /* 要接收的intent源 */
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    
    private static final String CACHE_PATH = "/cache/";
    private final static String OTA_NAME = "update_signed.zip";
    private final static String TMP_SUFFIX = ".tmp";
    private final static int TASK_CHECK_VERSION = 1; /* 检查版本 */
    private String mNewVersion = "";
    private String mMd5 = "";
    private String mData = ""; /* 存储服务器返回的升级信息 */
    
    private String mSavePath = ""; /* 本地存储路径 */
    
    private HttpThread mHttp = null; 
    
    private  String mUpgradeCode = ""; /* code，对应ini文件的 code */
    
    private String mMachineID = ""; /* 设备编号, 对应ini文件的 ID */
    
    private final static int CFG_RECOVERY = 1; /* 恢复升级 */
    
    private final static String CFG_FILE_PATH = "/tvcustomer/Customer/haier_deviceinfo.ini";
    
    private String mDownloadUrl = ""; /* 服务器返回的文件下载地址 */
    
    private String mUrl = ""; /* 网络地址 */
    
    private final static int CFG_BACKUP = 2; /* 备份升级 */

    public static MyFunDialog mMyFunDialog;

    private View mUpgradeView;
    private String mSystemMac = ""; /* 本地mac地址 */

    private Button mEnterUpgrade;

    private Button mCancleUpgrade;

    private TextView mTextFindUpgrade;

    private TextView mTextUpgrade;

    private int state = Updater.STATE_NOTHING;

    public static final int CHECK_UPDATE_VERSION_OK = 0x100001;

    public static final int CHECK_UPDATE_VERSION_TIME = 0x100001;

    private Context mContext;

    public Handler mHandler = new Handler();

    public Handler mHandlerTime = new Handler();

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(ACTION)) {

            this.mContext = context;

            Log.d("Updater",
                    "UpgradeReceiver.onReceive:start completed recevice......"
                            + System.currentTimeMillis() / 1000);
            
            //在此设置"首选安装位置"为设备内部存储
			Settings.Secure.putInt(context.getContentResolver(), "install_app_location_type", 1);

            // 初始化组件
            setupView(context);

            // 检测升级
            int isAutoUpgrade = Settings.System.getInt(context.getContentResolver(),
                    OtherSettings.STRAUTOUPGRADE, 1);
            
            NetInfo net = new NetInfo(context);
            net.getNetInfo();
            mSystemMac = net.getMac();
            Log.e("Updater", "____________isAutoUpgrade....." + isAutoUpgrade+"mSystemMac=="+mSystemMac);
            if (isAutoUpgrade == 1) {
                checkUpdateVersion();
            }

        }

    }

    private Runnable mRunableTime = new Runnable() {

        @Override
        public void run() {

            Log.d("Updater",
                    "UpgradeReceiver.mRunableTime.run()......" + System.currentTimeMillis() / 1000);

            String state = SystemNetUpdateActivity.readFileByLines("/cache/Setttings2.txt");
            Log.d("Updater", "UpgradeReceiver.mRunableTime.run().state=" + state);

            if (state != null && "OK".equals(state)) {
                Log.d("Updater", "UpgradeReceiver.mRunableTime.run_用户已经手动检测过了......");
                Updater.writeStringToFile("null", "/cache/Setttings2.txt");
            } else {
                NetInfo net = new NetInfo(mContext);
                net.getNetInfo();
                Log.d("Updater", "ip=" + net.getIpAddr());
                Log.d("Updater", "name=" + net.getNetName());
                Log.d("Updater", "mac=" + net.getMac());
                mSystemMac=net.getMac();
                checkVersion();

                mHandler = new Handler();
                mHandler.postDelayed(mRunable, 2000);
            }

        }
    };

    public synchronized boolean checkVersion() {

        if (!readConfig(CFG_FILE_PATH)) {
            
           state=Updater.STATE_READ_CFG_ERR;
            backkupCfg(CFG_RECOVERY);
            return false;
        } else {
            backkupCfg(CFG_BACKUP);
        }

        if (mSystemMac.isEmpty()) {
            state=Updater.STATE_READ_MAC_ERR;
            return false;
        }


        String req = getRequest();
        mHttp = new HttpThread(req, null, TASK_CHECK_VERSION);
        mHttp.start();
        state=Updater.STATE_REQUEST_VER;
        return true;
    }
    
    public class HttpThread extends Thread {
        private int mPos = 0; /* 当前下载位置 */

        private int mLength = 0; /* 数据总长度 */

        private int mPackageSize = 0;

        private int mTaskType = TASK_CHECK_VERSION; /* 任务类型 */

        private boolean mFinish = false;

        private String mUrl = ""; /* 网络地址 */

        private String mName = ""; /* 本址存储地址 */

        HttpURLConnection conn = null;

        public HttpThread(String url, String name, int type) {
            mPos = 0;
            mLength = 0;
            mTaskType = type;
            mFinish = false;
            mUrl = url;
            mName = name;
        }

        public synchronized int getDownPos() {
            return mPos;
        }

        public synchronized int getLength() {
            return mLength;
        }

        public synchronized int getPackageSize() {
            return mPackageSize;
        }

        public synchronized boolean isFinish() {
            return mFinish;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            download();
        }

        private void download() {

            Log.d("Updater", "download.SystemNetUpdateActivity.check:"
                    + SystemNetUpdateActivity.check);

            File file = null;
            InputStream input = null;
            OutputStream output = null;
            // HttpURLConnection conn = null;

            try {
                URL url = new URL(mUrl);
                if (conn != null) {
                    conn.disconnect();
                }
                conn = (HttpURLConnection) url.openConnection();

                Log.d("Updater", "download.SystemNetUpdateActivity.check is false.....");

                conn.setConnectTimeout(15 * 1000); // 15 seconds
                conn.setReadTimeout(180 * 1000); // 180 seconds
                conn.connect();

                input = conn.getInputStream();
                synchronized (this) {
                    mLength = conn.getContentLength();
                }

                file = createFile();
                if (null != file) {
                    output = new FileOutputStream(file);
                }

                int len = 0;
                final int BUFSIZE = 4096;
                byte[] buffer = new byte[BUFSIZE];
                while (len != -1) {
                    len = input.read(buffer, 0, BUFSIZE);
                    if (-1 == len) {
                        break;
                    }
                    /* 数据写入文件 */
                    if (null != output) {
                        output.write(buffer, 0, len);
                    }

                    /* 更新下载进度 */
                    synchronized (this) {
                        mPos = mPos + len;
                        if (mData.length() < 512) {
                            mData += new String(buffer, 0, len);
                        }
                    }

                    if (SystemNetUpdateActivity.check) {
                        SystemNetUpdateActivity.check = false;
                        Log.d("Updater", "download.SystemNetUpdateActivity.check:"
                                + SystemNetUpdateActivity.check);
                        break;
                    }
                }

            } catch (FileNotFoundException e) {
                Log.d("Updater", "download.FileNotFoundException=" + e.getLocalizedMessage());
                e.printStackTrace();
            } catch (MalformedURLException e) {
                Log.d("Updater", "download.MalformedURLException=" + e.getLocalizedMessage());
                e.printStackTrace();
            } catch (IOException e) {
                Log.d("Updater", "download.IOException=" + e.getLocalizedMessage());
                e.printStackTrace();
            } finally {
                finallyMethod(input, output, null);
            } // finally

            /* 取升级包的长度 */
            httpGetPackageSize();

            synchronized (this) {
                Log.d("Updater", "synchronized...");
                mFinish = true;
                if (mTaskType == TASK_CHECK_VERSION) {

                    Log.d("Updater", "mTaskType == TASK_CHECK_VERSION...");

                    if (mLength > 0 && mPos == mLength) {

                        Log.d("Updater", "mLength > 0 && mPos == mLength...");
                        Log.d("Updater", "mLength > 0 && mPos == mLength.mPos:" + mPos);
                        Log.d("Updater", "mLength > 0 && mPos == mLength.mLength:" + mLength);

                        // 删除升级包的临时文件和升级文件
                        deleteUpgradeTempFile();

                        if (checkStorage(mPackageSize)) {
                            // mState = STATE_RECIECE_VER_OK;
                            // httpGetPackageSize里面会改变mState的状态,这里设置会有问题
                        } else {
                            Log.d("Updater", "mState = STATE_NOT_ENOUGH_STORAGE...");
                            state=Updater.STATE_NOT_ENOUGH_STORAGE;
                        }
                    } else {
                        state=Updater.STATE_RECIECE_VER_ERR;
                    }
                }
            }

        }// download

        public void finallyMethod(InputStream input, OutputStream output, String flag) {
            try {
                if (null != input) {
                    input.close();
                }
            } catch (Throwable t) {
                Log.d("Updater", "input...");
            }

            try {
                if (null != conn) {
                    conn.disconnect();
                    conn = null;
                }
            } catch (Throwable t) {
                Log.d("Updater", "conn...");
            }

            try {
                if (null != output) {
                    output.flush();
                    output.close();
                }
            } catch (Throwable t) {
                Log.d("Updater", "output...");
            }
            if ("NetUpdateActivity".equals(flag)) {
                state=Updater.STATE_READ_MAC_ERR;
            }
        }

        /**
         * 创建文件
         * 
         * @return null, 创建失败; File object 创建成功
         */
        private File createFile() throws IOException {

            File file = null;

            /* 创建文件 */
            if (null != mName && !mName.isEmpty()) {
                String pathName = mSavePath + mName;
                if (!pathName.endsWith(TMP_SUFFIX)) {
                    pathName = pathName + TMP_SUFFIX;
                }
                file = new File(pathName);
                if (file.exists()) {
                    file.delete();
                }

                /* 创建目录 */
                int idx = pathName.lastIndexOf('/');
                File dir = new File(pathName.substring(0, idx));
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                /* 创建文件 */
                file.createNewFile();

            }

            return file;
        }

        /**
         * 尝试性连接, 只取升级包大小给UI用, 不下载数据
         * 
         * @return
         */
        private int httpGetPackageSize() {

            synchronized (this) {
                mPackageSize = 0;
                if (mTaskType != TASK_CHECK_VERSION)
                    return 0;

                /* 0,有地址，可以下载升级包; -1, 当前是最新版本; -2, 没有新升级包 */
                int ret = parserReponse(mData);
                switch (ret) {
                    case 0:
                        state=Updater.STATE_RECIECE_VER_OK;
                        break;
                    case -1:
                        state=Updater.STATE_VER_IS_NEWEST;
                        break;
                    default:
                        state=Updater.STATE_NO_PACKAGE;
                        break;
                }
                if (ret != 0) {
                    return 0;
                }
            }

            int length = 0;
            HttpURLConnection conn = null;
            try {
                URL url = null;
                synchronized (this) {
                    url = new URL(mDownloadUrl);
                }
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.connect();
                length = conn.getContentLength();
                synchronized (this) {
                    mPackageSize = length;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (null != conn) {
                        conn.disconnect();
                        conn = null;
                    }
                } catch (Throwable t) {
                }
            }

            return length;
        }

        private synchronized boolean checkStorage(long size) {


            long cachefreesize = getCacheFreeSize();

            if (size < cachefreesize) {
                mSavePath=CACHE_PATH;
                writeStringToFile("cache", "/var/Setttings1.txt");
                return true;
            }

            String path = Environment.getExternalStorageDirectory().getPath();
            long sdcardfreesize = getSdCardFreeSize();

            if (null != path && path.length() > 0 && size < sdcardfreesize) {
           
                if (!path.endsWith("/")) {
                    path += "/";
                }
                mSavePath=path;
                writeStringToFile("sdcard", "/var/Setttings1.txt");
                return true;
            }

            return false;
        }

    }
    
    synchronized static private long getCacheFreeSize() {

        StatFs sf = new StatFs("/cache");

        /* weijh 保留6M空间用于写recovery信息 */
        final int tmpsize = 1024 * 1024 * 6;

        long blockSize = sf.getBlockSize();
        long availCount = sf.getAvailableBlocks();
        long size = availCount * blockSize;
        if (size > tmpsize) {
            size -= tmpsize;
        } else {
            size = 0;
        }

        return size;
    }
    
    private String getRequest() {
        StringBuilder sb = new StringBuilder();
        sb.append("0000").append(mSystemMac.charAt(0));
        sb.append("0000").append(mSystemMac.charAt(1));
        sb.append("0000").append(mSystemMac.charAt(3));
        sb.append("0000").append(mSystemMac.charAt(4));
        sb.append("0000").append(mSystemMac.charAt(6));
        sb.append("0000").append(mSystemMac.charAt(7));
        sb.append("0000").append(mSystemMac.charAt(9));
        sb.append("0000").append(mSystemMac.charAt(10));
        sb.append("0000").append(mSystemMac.charAt(12));
        sb.append("0000").append(mSystemMac.charAt(13));
        sb.append("0000").append(mSystemMac.charAt(15));
        sb.append("0000").append(mSystemMac.charAt(16));
        String mac = sb.toString();
        sb = new StringBuilder();
        sb.append(mUrl).append("****$");
        sb.append(mMachineID).append("$1$$");
        sb.append(mac).append("$$$$****$$00$10$");
        sb.append(mUpgradeCode).append("$$$****$****");

        return sb.toString();
    }
    
    private void backkupCfg(int flag) {
        final String backup = "/tvcustomer/CustomerBackup/haier_deviceinfo.ini";
        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            byte[] buffer = new byte[1024];

            if (CFG_RECOVERY == flag) {
                fis = new FileInputStream(backup);
                fos = new FileOutputStream(CFG_FILE_PATH);
            } else {
                fis = new FileInputStream(CFG_FILE_PATH);
                fos = new FileOutputStream(backup);
            }

            for (int len = 0; true;) {
                len = fis.read(buffer);
                if (len > 0) {
                    fos.write(buffer, 0, len);
                } else {
                    break;
                }
            }
        } catch (Exception e) {
        } finally {
            try {
                if (null != fis) {
                    fis.close();
                    fis = null;
                }
            } catch (Throwable t) {
            }

            try {
                if (null != fos) {
                    fos.close();
                    fos = null;
                }
            } catch (Throwable t) {
            }
        }
    }
    
    /**
     * 读取ini配置文件信息
     * 
     * @param path ini file path
     * @return
     */
    private boolean readConfig(String pathFile) {

        boolean flag = false;
        File file = new File(pathFile);
        IniFile ini = new BasicIniFile();
        IniFileReader reader = new IniFileReader(ini, file);
        try {
            reader.read();
            IniSection section = ini.getSection("MISC_SOFTWARE_UPGRADE");
            mUrl = section.getItem("url").getValue();
            mMachineID = section.getItem("ID").getValue();
            mUpgradeCode=section.getItem("code").getValue();
            flag = true;
        } catch (FormatException e) {
            // exception thrown because the INI file was in an unexpected format
            e.printStackTrace();
        } catch (IOException e) {
            // exception thrown as an input\output exception occured
            e.printStackTrace();
        }
        if (!flag) {
            return false;
        }
        return true;
    }

    /**
     * 检测TV系统是否有新的版本
     * 
     * @return
     */
    private void checkUpdateVersion() {
        mHandlerTime = new Handler();
        mHandlerTime.postDelayed(mRunableTime, 3 * 60 * 1000);

    }

    private Runnable mRunable = new Runnable() {

        @Override
        public void run() {

            Log.d("Updater", "UpgradeReceiver.mRunable......");

            Log.d("Updater", "Runable, state=" + state);

            if (state == Updater.STATE_RECIECE_VER_ERR || state == Updater.STATE_NO_PACKAGE
                    || state == Updater.STATE_NO_PACKAGE || state == Updater.STATE_VER_IS_NEWEST) {

                Log.d("Updater", "UpgradeReceiver.onReceive: no new version......");

            } else if (state == Updater.STATE_RECIECE_VER_OK) {

                Log.d("Updater", "UpgradeReceiver.onReceive:check new version......");

                // 展示对话框
                showDialog();
            }
        }
    };

    private void showDialog() {

        Log.d("Updater", "UpgradeReceiver.showDialog()......");

        mMyFunDialog.show();

        mCancleUpgrade.requestFocus();
        mCancleUpgrade.setTextColor(Color.rgb(255, 255, 255));
        mCancleUpgrade.setBackgroundResource(R.drawable.pop1_btn_sel);

        // 注册操作监听
        registerListener(mContext);
    }

    private void registerListener(final Context context) {

        mEnterUpgrade.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.d("Updater", "enter  onClick.......");

                Intent intent = new Intent();
                intent.setClass(context, SystemBackupUpdate.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

                if (mMyFunDialog != null) {
                    mMyFunDialog.dismiss();
                }
            }
        });

        mEnterUpgrade.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                Log.d("Updater", "enter focus........." + hasFocus);

                if (hasFocus) {
                    mEnterUpgrade.setTextColor(Color.rgb(255, 255, 255));
                    mEnterUpgrade.setBackgroundResource(R.drawable.pop1_btn_sel);
                } else {
                    mEnterUpgrade.setTextColor(Color.rgb(200, 200, 200));
                    mEnterUpgrade.setBackgroundResource(R.drawable.pop1_btn_nor);
                }
            }
        });

        mCancleUpgrade.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.d("Updater", "cancle  onClick.......");

                if (mMyFunDialog != null) {
                    mMyFunDialog.dismiss();
                }
            }
        });

        mCancleUpgrade.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                Log.d("Updater", "cancle focus........." + hasFocus);

                if (hasFocus) {
                    mCancleUpgrade.setTextColor(Color.rgb(255, 255, 255));
                    mCancleUpgrade.setBackgroundResource(R.drawable.pop1_btn_sel);
                } else {
                    mCancleUpgrade.setTextColor(Color.rgb(200, 200, 200));
                    mCancleUpgrade.setBackgroundResource(R.drawable.pop1_btn_nor);
                }
            }
        });

    }

    private void setupView(Context context) {

        Log.d("Updater", "UpgradeReceiver.setupView......");

        mMyFunDialog = new MyFunDialog(context);

        mUpgradeView = (View) View.inflate(context, R.layout.upgrade, null);

        mEnterUpgrade = (Button) mUpgradeView.findViewById(R.id.btn_ok);
        mCancleUpgrade = (Button) mUpgradeView.findViewById(R.id.btn_cancle);
        mTextFindUpgrade = (TextView) mUpgradeView.findViewById(R.id.find_upgrade);
        mTextUpgrade = (TextView) mUpgradeView.findViewById(R.id.enter_upgrade);

        mEnterUpgrade.setText(context.getString(R.string.description_ok));
        mCancleUpgrade.setText(context.getString(R.string.description_cancle));
        mTextFindUpgrade.setText(context.getString(R.string.description_find_upgrade));
        mTextUpgrade.setText(context.getString(R.string.description_upgrade));

        mEnterUpgrade.setTextSize(30);
        mCancleUpgrade.setTextSize(30);
        mTextFindUpgrade.setTextSize(30);
        mTextUpgrade.setTextSize(34);

        mTextFindUpgrade.setTextColor(Color.rgb(150, 150, 150));
        mTextUpgrade.setTextColor(Color.rgb(255, 255, 255));

        WindowManager.LayoutParams myFunDialogParam;

        myFunDialogParam = mMyFunDialog.getWindow().getAttributes();
        myFunDialogParam.width = 860;
        myFunDialogParam.height = 400;
        myFunDialogParam.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        myFunDialogParam.type = WindowManager.LayoutParams.TYPE_STATUS_BAR_PANEL;
        myFunDialogParam.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH
                | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
        myFunDialogParam.setTitle("myFun");
        mMyFunDialog.getWindow().setAttributes(myFunDialogParam);
        mMyFunDialog.getWindow().setFormat(PixelFormat.TRANSPARENT);

        mMyFunDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mMyFunDialog.setContentView(mUpgradeView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mMyFunDialog.getWindow().setBackgroundDrawable(null);
    }
    
    public synchronized void deleteUpgradeTempFile() {

        //删除cache目录下的升级临时文件
        String cachePath1 = CACHE_PATH + OTA_NAME + TMP_SUFFIX;
        deletefile(cachePath1);
        
        //删除cache目录下的升级文件
        String cachePath2 = CACHE_PATH + OTA_NAME;
        deletefile(cachePath2);
        
        String sdcardPath = Environment.getExternalStorageDirectory().getPath();
        if (sdcardPath != null && sdcardPath.length() > 0){
            if (!sdcardPath.endsWith("/")) {
                //删除SDCard目录下的升级临时文件
                String sdcardPath1 = sdcardPath +  "/" + OTA_NAME + TMP_SUFFIX;
                deletefile(sdcardPath1);
                
                //删除SDCard目录下的升级文件
                String sdcardPath2  = sdcardPath + "/" + OTA_NAME;
                deletefile(sdcardPath2);
            }
        }
    }
    
    /**
     * 解析服务器返回的数据
     * 
     * @param filePath
     *            数据文件的路径
     * @return 0,有地址，可以下载升级包; -1, 当前是最新版本; -2, 没有新升级包
     */
    private int parserReponse(String data) {

        String url = "";
        String path = "";
        String machineID = "";
        String upgradeCode = "";
        String updateFlg = "";
        String version = "";

        data = data.replace('$', ' ');
        data = data.replace('*', ' ');
        String arr[] = data.split(" ");

        if (arr.length > 6) {
            machineID = arr[6];
        }
        if (arr.length > 7) {
            url = arr[7];
        }
        if (arr.length > 17) {
            path = arr[17];
        }
        if (arr.length > 18) {
            upgradeCode = arr[18];
        }
        if (arr.length > 19) {
            updateFlg = arr[19];
        }
        if (arr.length > 20) {
            version = arr[20];
        }
        if (arr.length > 21) {
            mMd5 = arr[21];
        }
        if (arr.length > 22) {
            //description = arr[22];
        }


        int ret = 0;
        if (url.isEmpty() || path.isEmpty() || version.isEmpty()) {
            ret = -2;
        } else {
            mDownloadUrl = url + path;
        }

        long curl_version = 0;
        long serv_version = 0;
        try
        {
            curl_version = Long.parseLong(mUpgradeCode);
            serv_version = Long.parseLong(version);
        }
        catch (NumberFormatException e)
        {
        }

        if (0 == serv_version) {
            ret = -1; 
        }
        
        if (curl_version >= serv_version) {
            ret = -1;
        }
        /* 记录新版本号 */
        if (0 == ret) {
            mNewVersion = version;
        }
        return ret;
    }
    
    public synchronized static boolean writeStringToFile(String content,String path) {
        File file = new File(path);
        try {
            if (file.isFile()) {
                file.deleteOnExit();
                file = new File(file.getAbsolutePath());
            }
            OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
            os.write(content);
            os.close();
        } catch (Exception e) {
            //e.printStackTrace();
            return false;
        }
        return true;
    }

    synchronized static private long getSdCardFreeSize() {
        File file = Environment.getExternalStorageDirectory();
        if (null == file) {
            return 0;
        }
        
        StatFs sf = new StatFs(file.getPath());
        if (null == sf) {
            return 0;
        }
        if (!CheckSDCard.hasStorage()) {
            return 0;
        }
        
        long blockSize = sf.getBlockSize();
        long availCount = sf.getAvailableBlocks();

        return availCount*blockSize;
    }


    public class MyFunDialog extends Dialog {
        public MyFunDialog(Context context) {
            super(context, com.android.internal.R.style.Theme_Light_NoTitleBar);
        }
    }
    
    private synchronized void deletefile(String deletePath) {
        File file = new File(deletePath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
        }
    }

}
