
package com.android.settings.update;

import com.android.settings.update.UpdateService.HttpThread;

public interface IService {
    /**
     * 获取当前下载位置
     * 
     * @return
     */
    public int getDownPos();

    /**
     * 获取数据总长度
     * 
     * @return
     */
    public int getLength();

    /**
     * 获取待下载的升级包的长度. 注意: 仅在getState() == STATE_RECIECE_VER_OK时调用有效
     * 
     * @return 返回待下载升级包的长度
     */
    public int getPackageSize();

    public HttpThread getHttpThread();

    public int isFinished();

    public int getmState();

    public void setmState(int a);

    public void setmSavePath(String _mSavePath);

    public String getmSavePath();

    public void setmUpgradeCode(String _mUpgradeCode);

    public String getmUpgradeCode();

    public void setmNewVersion(String _mNewVersion);

    public String getmNewVersion();

}
