
package com.android.settings.update;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

import org.dtools.ini.BasicIniFile;
import org.dtools.ini.FormatException;
import org.dtools.ini.IniFile;
import org.dtools.ini.IniFileReader;
import org.dtools.ini.IniSection;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.net.ethernet.EthernetDevInfo;
import android.net.ethernet.EthernetManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.settings.R;
import com.android.settings.update.UpdateService.ServiceBinder;
import com.android.settings.util.Tools;

public class SystemInformation extends Activity {

    private Button exit_btn;

    private TextView system_infor;

    private final static int CFG_RECOVERY = 1; /* 恢复升级 */

    private final static int CFG_BACKUP = 2; /* 备份升级 */

    private String infor = "";

    private final static String CFG_FILE_PATH = "/tvcustomer/Customer/haier_deviceinfo.ini"; // modify bu CWJ

    public static String mUpgradeCode = ""; /* code，对应ini文件的 code */

    private String mMachineID = ""; /* 设备编号, 对应ini文件的 ID */

    public static String mUrl = ""; /* 升级服务器地址, 对应ini文件的 url */

    // 可用内存
    private String freeMemory;

    // 总内存
    private String totalMemory;

    private EthernetManager mEthernetManager;

    // private Updater mUpdater = null;

    /**
     * 数据的初始化
     */
    private void initData() {

        infor += getString(R.string.system_version) + "：" + Tools.currentSystemVersion() + "_"
                + mUpgradeCode;// + "\n\n";
        system_infor.setText(infor);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        freeMemory = getIntent().getStringExtra("freeMemory");
        totalMemory = getIntent().getStringExtra("totalMemory");

        setContentView(R.layout.system_infor);

        mEthernetManager = (EthernetManager) getSystemService(Context.ETHERNET_SERVICE);
        checkVersionini();
        findViews();

        registerListeners();

    }

    private void findViews() {

        system_infor = (TextView) findViewById(R.id.system_info);
        exit_btn = (Button) findViewById(R.id.back);

        // exit_btn.setBackgroundResource(R.drawable.left_bg);
        exit_btn.setText(getString(R.string.exit));

        initData();
    }

    private void registerListeners() {
        exit_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public synchronized boolean checkVersionini() {
        if (!readConfig(CFG_FILE_PATH)) {
            backkupCfg(CFG_RECOVERY);
            return false;
        } else {
            backkupCfg(CFG_BACKUP);
        }
        return true;
    }

    private void backkupCfg(int flag) {
        final String backup = "/tvcustomer/Customer/haier_deviceinfo.ini";
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
            Log.d("###################mUpgradeCode", section.getItem("code").getValue());
            mUpgradeCode =section.getItem("code").getValue();
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
     * 获取无线网卡MAC地址
     * 
     * @return
     */
    private String getWirelessMacAddress() {

        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        System.out.println("有线MAC:" + info.getMacAddress());
        return info.getMacAddress();

    }

    /**
     * 获取有线网卡地址
     * 
     * @return
     */
    private String getWireMacAddress() {
        mEthernetManager = (EthernetManager) getSystemService(Context.ETHERNET_SERVICE);
        System.out.println("有线MAC:" + mEthernetManager.getMacAddress());
        return mEthernetManager.getMacAddress();
    }

    /**
     * 获取IP地址
     * 
     * @return
     */
    private String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
                    .hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
                        .hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("WifiPreference IpAddress", ex.toString());
        }
        return null;
    }

    /**
     * 获取当前系统的时间
     */
    private String getSystemTime() {
        Date now = Calendar.getInstance().getTime();
        return DateFormat.getTimeFormat(this).format(now);
    }

}
