
package com.android.settings.update;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

import com.android.settings.R;

public class UpdateService extends Service {

    private  String mUpgradeCode = ""; /* code，对应ini文件的 code */
    private String mNewVersion = "";
    
    private static String mSavePath = ""; /* 本地存储路径 */

    private final static int TASK_DOWN_PACKAGE = 2; /* 下载升级包 */

    private  int mState = Updater.STATE_NOTHING;

    private final  String OTA_NAME = "update_signed.zip";

    private final static String TMP_SUFFIX = ".tmp";

    private HttpThread mHttp = null; /* http 下载线程 */

    private String mMd5 = ""; /* md5 */

    private final static String TAG = "UpdateService";

    private ServiceBinder sBinder;

    private final static int DOWNLOADING = 1000000001;

    private Notification notification;

    private NotificationManager notificationManager;

    private PendingIntent contentIntent;

    private RemoteViews mRemoteViews;

    private int percent=-1;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            System.out.println("mHandler msg " + msg.what);
            
            if(msg.what == 100) {
                showNotification(R.drawable.one_px, R.string.upgrade_package_download_finished, msg.what);
            }
            if (msg.what <=100) {
                mRemoteViews.setTextViewText(R.id.task_percent, msg.what + "%");
                mRemoteViews.setProgressBar(R.id.task_progressbar, 100, msg.what, false);
                notification.contentView = mRemoteViews;
                notification.contentIntent = contentIntent;
                notificationManager.notify(DOWNLOADING, notification);
            }
            
          
             
        };
    };


    @Override
    public IBinder onBind(Intent intent) {
        return sBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sBinder = new ServiceBinder();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("UpdateService-downUrl:" + Updater.mUrl);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        showNotification(R.drawable.one_px, R.string.update_packages_download, 0);
        Message msg = mHandler.obtainMessage();
        msg.what = 0;
        mHandler.sendMessage(msg);
        startUpdate();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * start download the update package
     */
    private void startUpdate() {

        downloadPackage();
    }

    /**
     * notification的显示
     * 
     * @param drawbale
     * @param titleId
     * @int percent
     */
    private void showNotification(int drawbale, int titleId, int percent) {
        notification = new Notification(drawbale, getString(titleId),
                System.currentTimeMillis());
        mRemoteViews = new RemoteViews(getApplication().getPackageName(),
                R.layout.download_progress);
        Intent intent = new Intent();
        if (percent == 100) {
            intent.setClass(this, SystemNetUpdateActivity.class);
            System.out.println("showNotification 100");
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        }
        contentIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

    }

    // interface for service and activity.

    public class ServiceBinder extends Binder implements IService {

        @Override
        public int getDownPos() {
            if (null != mHttp) {
                return mHttp.getDownPos();
            } else {
                return 0;
            }
        }

        @Override
        public int getLength() {
            if (null != mHttp) {
                return mHttp.getLength();
            } else {
                return 0;
            }
        }

        @Override
        public int getPackageSize() {
            if (null != mHttp) {
                return mHttp.getPackageSize();
            } else {
                return 0;
            }
        }

        @Override
        public HttpThread getHttpThread() {
            return null;
        }

        @Override
        public int isFinished() {
            return 0;
        }

        @Override
        public int getmState() {
            return mState;

        }

        @Override
        public void setmState(int a) {
            mState = a;
        }

        @Override
        public void setmSavePath(String _mSavePath) {
                mSavePath=_mSavePath;
            
        }

        @Override
        public String getmSavePath() {
            return mSavePath+OTA_NAME;
        }

        @Override
        public void setmUpgradeCode(String _mUpgradeCode) {
            mUpgradeCode=_mUpgradeCode;
        }

        @Override
        public String getmUpgradeCode() {
            return mUpgradeCode;
        }

        @Override
        public void setmNewVersion(String _mNewVersion) {
            mNewVersion=_mNewVersion;
        }

        @Override
        public String getmNewVersion() {
            return mNewVersion;
        }

    }


    public class HttpThread extends Thread {
        private int mPos = 0; /* 当前下载位置 */

        private int mLength = 0; /* 数据总长度 */

        private int mPackageSize = 0;

        private int mTaskType = TASK_DOWN_PACKAGE; /* 任务类型 */

        private boolean mFinish = false;

        private String mUrl = ""; /* 网络地址 */

        HttpURLConnection conn = null;

        public HttpThread(String url) {
            mPos = 0;
            mLength = 0;
            mFinish = false;
            mUrl = url;
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
                        Log.d("Updater", "mPos=========" + mPos);
                      
                        if (mLength > 0) {
                             int progress = (int) (mPos * 100.0 / mLength);
                             if(percent!=progress){
                                 percent=progress;
                                 Message msg = mHandler.obtainMessage();
                                 msg.what = progress;
                                 mHandler.sendMessage(msg);
                             }
                           
                        }
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

            synchronized (this) {
                Log.d("Updater", "synchronized...");
                mFinish = true;
                if (mTaskType == TASK_DOWN_PACKAGE) {
                    Log.d("Updater", "mPos======="+mPos+"mLength======="+mLength );
                    if (mLength > 0 && mPos == mLength) {
                        if (checkFile(file)) {
                            mState = Updater.STATE_DOWNLOAD_OK;
                        } else {
                            mState = Updater.STATE_DOWNLOAD_ERR;
                        }
                    } else {
                        mState = Updater.STATE_DOWNLOAD_ERR;
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
                mState = Updater.STATE_READ_MAC_ERR;
            }
        }

        /**
         * 创建文件
         * 
         * @return null, 创建失败; File object 创建成功
         */
        private File createFile() throws IOException {
            Log.d(TAG, "Updater.mSavePath=========" +mSavePath);
            File file = null;
            String pathName = mSavePath + OTA_NAME;
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

            return file;
        }

        /**
         * 校验文件
         * 
         * @param file
         * @return true, md5校验通过 ; false, md5校验没通过
         */
        private boolean checkFile(File file) {
            String pathName = mSavePath + OTA_NAME;
            String md5 = MD5.md5sum(pathName + TMP_SUFFIX);
            if (TASK_DOWN_PACKAGE == mTaskType) {
                Log.d(TAG, "checkFile, md5=" + (md5 != null ? md5 : ""));
                Log.d(TAG, "checkFile, MD5=" + (mMd5 != null ? mMd5 : ""));
                if (!mMd5.isEmpty() && !mMd5.equalsIgnoreCase(md5)) {
                    // Log.d(TAG, "checkFile, md5 != mMd5");
                    return false;
                }
            }

            Log.d(TAG, "checkFile, name=" + pathName);
            File newFile = new File(pathName);
            file.renameTo(newFile);
            Log.d(TAG, "checkFile, return true");
            return true;
        }


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

    public synchronized void downloadPackage() {
        if (mState != Updater.STATE_RECIECE_VER_OK) {
            return;
        }

        mHttp = new HttpThread(Updater.mDownloadUrl);
        mHttp.start();
        mState = Updater.STATE_DOWNLOADING;
    }

}
