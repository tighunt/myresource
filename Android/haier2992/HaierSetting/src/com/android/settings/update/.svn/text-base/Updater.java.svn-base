
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

import com.android.settings.update.UpdateService.HttpThread;
import com.android.settings.update.UpdateService.ServiceBinder;

import android.os.Binder;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

/**
 * 升级包下载, 里面封装了与海尔升级服务器的通讯协议。使用者主要调用checkVersion、downloadPackage、getState
 * 三个函数来下载升级包, 具体用法见UpdateActivity.java里面的Runnable;
 * 
 * @author kevin
 * @date 2012-3-6
 */
public class Updater {

    private HttpThread mHttp = null; /* http 下载线程 */

    private final static String TAG = "Updater";

    private String mData = ""; /* 存储服务器返回的升级信息 */

    private static final String CACHE_PATH = "/cache/";

    /* 升级的当前状态 */
    public final static int STATE_NOTHING = 0; /* 初始状态 */

    public final static int STATE_REQUEST_VER = 1; /* 请求版本信息 */

    public final static int STATE_READ_CFG_ERR = 2; /* 读取配置文件出错 */

    public final static int STATE_READ_MAC_ERR = 3; /* 读取mac出错 */

    public final static int STATE_RECIECE_VER_ERR = 4; /* 请求版本信息出错 */

    public final static int STATE_RECIECE_VER_OK = 5; /* 收到版本信息 */

    public final static int STATE_NO_PACKAGE = 6; /* 没用可用升级包 */

    public final static int STATE_VER_IS_NEWEST = 7; /* 当前已是最新版本 */

    public final static int STATE_DOWNLOADING = 8; /* 正在下载中 */

    public final static int STATE_DOWNLOAD_ERR = 9; /* 下载升级包出错 */

    public final static int STATE_DOWNLOAD_OK = 10; /* 升级包下载完成 */

    public final static int STATE_NOT_ENOUGH_STORAGE = 11; /* 存储空间不足 */
    
    public final static int STATE_UPDATE_ERROR = 12; /* 存储空间不足 */

    /* HTTP下载任务类型 */
    private final static int TASK_CHECK_VERSION = 1; /* 检查版本 */

    private final static int TASK_DOWN_PACKAGE = 2; /* 下载升级包 */

    /* 备份或恢复cfg */
    private final static int CFG_RECOVERY = 1; /* 恢复升级 */

    private final static int CFG_BACKUP = 2; /* 备份升级 */

    private final static String CFG_FILE_PATH = "/tvcustomer/Customer/haier_deviceinfo.ini";

    private final static String VERSION_INFO = "versin_info.txt";

    private final static String OTA_NAME = "update_signed.zip";

    private final static String TMP_SUFFIX = ".tmp";

    public static String mUrl = ""; /* 升级服务器地址, 对应ini文件的 url */

    

    private String mMachineID = ""; /* 设备编号, 对应ini文件的 ID */

   

    // private String mBrand = ""; /* brand，对应ini文件的 brand */
    private String mSystemMac = ""; /* 本地mac地址 */

    public static String mDownloadUrl = ""; /* 服务器返回的文件下载地址 */

    private String mMd5 = ""; /* md5 */


    private ServiceBinder mServiceBinder;

    public Updater(String mac, ServiceBinder binder) {
        mSystemMac = mac;
        mServiceBinder = binder;
        init();
    }


    private void init() {
        mUrl = "";
        mMachineID = "";
        // mBrand = "";
        mDownloadUrl = "";
        mMd5 = "";
//        mServiceBinder.setmState(STATE_NOTHING);
    }

    /**
     * 读取ini配置文件信息
     * 
     * @param path ini file path
     * @return
     */
    private boolean readConfig(String pathFile) {

        Log.d(TAG, "Updater.readConfig, inifile=" + pathFile);
        boolean flag = false;
        File file = new File(pathFile);
        IniFile ini = new BasicIniFile();
        IniFileReader reader = new IniFileReader(ini, file);
        try {
            reader.read();
            IniSection section = ini.getSection("MISC_SOFTWARE_UPGRADE");
            mUrl = section.getItem("url").getValue();
            mMachineID = section.getItem("ID").getValue();
            mServiceBinder.setmUpgradeCode(section.getItem("code").getValue());
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

        // mBrand = section.getItem("brand").getValue();

        /*
         * 测试用参数 //mMachineID = "TEST0000001"; //mSystemMac =
         * "AA:AA:AA:AA:AA:AA"; //mUpgradeCode = "1203100000";
         */

        Log.d(TAG, "Updater.readConfig, url=" + mUrl);
        Log.d(TAG, "Updater.readConfig, ID=" + mMachineID);
        // Log.d(TAG, "Updater.readConfig, brand=" + mBrand);
        Log.d(TAG, "Updater.readConfig, mSystemMac=" + mSystemMac);

        return true;
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

        /*
         * sprintf(cSetID,
         * "0000%c0000%c0000%c0000%c0000%c0000%c0000%c0000%c0000%c0000%c0000%c0000%c"
         * , SystemMac[0],SystemMac[1],SystemMac[2],SystemMac[3],SystemMac[4],
         * SystemMac
         * [5],SystemMac[6],SystemMac[7],SystemMac[8],SystemMac[9],SystemMac
         * [10],SystemMac[11]);
         */

        // "%s     ****$      %s          $1$$     %s    $$$$****$$00$10$      %s            $$$****$****",
        // ServerUrl //MachineCode //cSetID //UpgradeCode);
        // http://update.mocard.tv:8080/MOSP/receive.do?method=getVersion&param=****$DH1QX000000$1$$000000000000000000000000000000000000000000000000000000A00000$$$$****$$00$10$1201221400$$$****$****
        sb = new StringBuilder();
        sb.append(mUrl).append("****$");
        sb.append(mMachineID).append("$1$$");
        sb.append(mac).append("$$$$****$$00$10$");
        sb.append(mServiceBinder.getmUpgradeCode()).append("$$$****$****");

        return sb.toString();
    }

    public synchronized boolean checkVersionini() {
        if (!readConfig(CFG_FILE_PATH)) {
            mServiceBinder.setmState(STATE_READ_CFG_ERR);
            backkupCfg(CFG_RECOVERY);
            return false;
        } else {
            backkupCfg(CFG_BACKUP);
        }
        return true;
    }

    public synchronized boolean checkVersion() {
        if (STATE_REQUEST_VER == mServiceBinder.getmState()
                || STATE_DOWNLOADING == mServiceBinder.getmState()) {
            return false;
        }
        mServiceBinder.setmState(STATE_NOTHING);

        if (!readConfig(CFG_FILE_PATH)) {
            mServiceBinder.setmState(STATE_READ_CFG_ERR);
            backkupCfg(CFG_RECOVERY);
            return false;
        } else {
            backkupCfg(CFG_BACKUP);
        }

        if (mSystemMac.isEmpty()) {
            mServiceBinder.setmState(STATE_READ_MAC_ERR);
            return false;
        }

        if (mHttp != null) {
            if (!mHttp.isFinish()) {
                return true;
            }
            mHttp = null;
        }

        String req = getRequest();
        Log.d(TAG, "req=" + req);
        mHttp = new HttpThread(req, null, TASK_CHECK_VERSION);
        mHttp.start();
        mServiceBinder.setmState(STATE_REQUEST_VER);
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
                    Log.d(TAG, "mLength=" + mLength);
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
                            mServiceBinder.setmState(STATE_NOT_ENOUGH_STORAGE);
                        }
                    } else {
                        mServiceBinder.setmState(STATE_RECIECE_VER_ERR);
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
                mServiceBinder.setmState(STATE_READ_MAC_ERR);
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
                String pathName = mServiceBinder.getmSavePath() + mName;
                if (!pathName.endsWith(TMP_SUFFIX)) {
                    pathName = pathName + TMP_SUFFIX;
                }
                Log.d(TAG, "pathName=" + pathName);
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
                        mServiceBinder.setmState(STATE_RECIECE_VER_OK);
                        break;
                    case -1:
                        mServiceBinder.setmState(STATE_VER_IS_NEWEST);
                        break;
                    default:
                        mServiceBinder.setmState(STATE_NO_PACKAGE);
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
                Log.d(TAG, "package size =" + length);
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

            Log.d(TAG, "checkStorage.size:" + size);

            long cachefreesize = getCacheFreeSize();
            Log.d(TAG, "checkStorage.cachefreesize=" + cachefreesize);

            if (size < cachefreesize) {
                Log.d(TAG, "checkStorage.size < cachefreesize......");
                mServiceBinder.setmSavePath(CACHE_PATH);
                writeStringToFile("cache", "/var/Setttings1.txt");
                return true;
            }

            String path = Environment.getExternalStorageDirectory().getPath();
            long sdcardfreesize = getSdCardFreeSize();
            Log.d(TAG, "checkStorage.sdcardfreesize=" + sdcardfreesize);

            if (null != path && path.length() > 0 && size < sdcardfreesize) {
                Log.d(TAG, "checkStorage. size < sdcardfreesize......");
           
                if (!path.endsWith("/")) {
                    path += "/";
                }
                mServiceBinder.setmSavePath(path);
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

    /**
     * 把字符串写入文本中
     * 
     * @param fileName 生成的文件绝对路径
     * @param content 文件要保存的内容
     * @param enc 文件编码
     * @return
     */
    public synchronized static boolean writeStringToFile(String content, String path) {
        Log.d(TAG, "Updater.writeStringToFile.content:" + content);
        Log.d(TAG, "Updater.writeStringToFile.path:" + path);
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
            // e.printStackTrace();
            Log.d(TAG, "Updater.writeStringToFile.Exception:" + e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    public synchronized void deleteUpgradeTempFile() {

        // 删除cache目录下的升级临时文件
        String cachePath1 = CACHE_PATH + OTA_NAME + TMP_SUFFIX;
        Log.d(TAG, "deleteUpgradeTempFile().cachePath1:" + cachePath1);
        deletefile(cachePath1);

        // 删除cache目录下的升级文件
        String cachePath2 = CACHE_PATH + OTA_NAME;
        Log.d(TAG, "deleteUpgradeTempFile().cachePath2:" + cachePath2);
        deletefile(cachePath2);

        String sdcardPath = Environment.getExternalStorageDirectory().getPath();
        if (sdcardPath != null && sdcardPath.length() > 0) {
            if (!sdcardPath.endsWith("/")) {
                // 删除SDCard目录下的升级临时文件
                String sdcardPath1 = sdcardPath + "/" + OTA_NAME + TMP_SUFFIX;
                Log.d(TAG, "deleteUpgradeTempFile().sdcardPath1:" + sdcardPath1);
                deletefile(sdcardPath1);

                // 删除SDCard目录下的升级文件
                String sdcardPath2 = sdcardPath + "/" + OTA_NAME;
                Log.d(TAG, "deleteUpgradeTempFile().sdcardPath2:" + sdcardPath2);
                deletefile(sdcardPath2);
            }
        }
    }

    /**
     * 解析服务器返回的数据
     * 
     * @param filePath 数据文件的路径
     * @return 0,有地址，可以下载升级包; -1, 当前是最新版本; -2, 没有新升级包
     */
    private int parserReponse(String data) {
        // sscanf(buffer,"****  %s %s     **** %s %s %s %s %s %s **** ****",MachineID,DownloadUrl,FileName,UpdateCode,UpdateFlag,Version,Md5,Description);
        // Server
        // response:****$$1$http://update.mocard.tv:8080/MOSP/version/$$$$$****$1/DH1QX000000/00/1202221545/usb.bin$00$11$1202221545$80d9009ccf697a10bbce57b9c74ebeb0$测试$****$****

        String url = "";
        String path = "";
        String machineID = "";
        String upgradeCode = "";
        String updateFlg = "";
        String version = "";

        Log.d(TAG, "Updater, Updater.parserReponse, data0=" + data);
        data = data.replace('$', ' ');
        Log.d(TAG, "Updater, Updater.parserReponse, data1=" + data);
        data = data.replace('*', ' ');
        Log.d(TAG, "Updater, Updater.parserReponse, data2=" + data);
        String arr[] = data.split(" ");
        for (String s : arr) {
            Log.d(TAG, "Updater, Updater.parserReponse, s=" + s);
        }

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
            // description = arr[22];
        }

        Log.d(TAG, "Updater, Updater.parserReponse, machineID=" + machineID);
        Log.d(TAG, "Updater, Updater.parserReponse, url=" + url);
        Log.d(TAG, "Updater, Updater.parserReponse, path=" + path);
        Log.d(TAG, "Updater, Updater.parserReponse, upgradeCode=" + upgradeCode);
        Log.d(TAG, "Updater, Updater.parserReponse, updateFlg=" + updateFlg);
        Log.d(TAG, "Updater, Updater.parserReponse, version=" + version);
        Log.d(TAG, "Updater, Updater.parserReponse, md5=" + mMd5);
        // Log.d(TAG, "Updater, Updater.parserReponse, description=" +
        // description);

        int ret = 0;
        if (url.isEmpty() || path.isEmpty() || version.isEmpty()) {
            ret = -2;
            Log.d(TAG, "Updater, Updater.parserReponse, There is no new version!");
        } else {
            mDownloadUrl = url + path;
            Log.d(TAG, "mDownloadUrl=" + mDownloadUrl);
        }

        long curl_version = 0;
        long serv_version = 0;
        try {
            curl_version = Long.parseLong(mServiceBinder.getmUpgradeCode());
            serv_version = Long.parseLong(version);
        } catch (NumberFormatException e) {
        }

        if (0 == serv_version) {
            ret = -1;
            Log.d(TAG, "serv_version = 0, It's newest version");
        }

        if (curl_version >= serv_version) {
            ret = -1;
            Log.d(TAG, "curl_version >= serv_versio It's newest version");
        }

        /* 记录新版本号 */
        if (0 == ret) {
            mServiceBinder.setmNewVersion(version);
        }

        return ret;
    }

    private synchronized void deletefile(String deletePath) {

        Log.d(TAG, "deletefile().deletefile:" + deletePath);

        File file = new File(deletePath);
        // 路径为文件且不为空则进行删除

        Log.d(TAG, "deletefile().file.isFile():" + file.isFile());
        Log.d(TAG, "deletefile().file.exists():" + file.exists());

        if (file.isFile() && file.exists()) {
            Log.d(TAG, "deletefile().deletefile:路径为文件且不为空则进行删除***");
            file.delete();
        }
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

        return availCount * blockSize;
    }

}
