
package com.android.settings.applications;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 用于记录非系统应用的信息
 * 
 * @author ducj
 * @date 2011-11-10 下午12:08:01
 * @since 1.0
 */
public class AppInfor implements Parcelable {
    // 应用程序的图标
    private Bitmap iconAppDrawable;

    // 应用程序的包名
    private String packageName;

    // 应用程序的名字
    private String appName;

    // 应用程序的版本名字
    private String versionName = "";

    // 应用程序的版本号
    private int versionCode = 0;

    // 缓存大小
    private long cacheSize;

    // 数据大小
    private long dataSize;

    // 应用程序大小
    private long appSize;

    // 应用程序总的大小
    private long totalSize;

    // 图片的字节数组
    private byte[] bitmap;

    // kernel user-ID
    private int uid;
    
    private long firstInstallTime;

    public long getFirstInstallTime() {
		return firstInstallTime;
	}

	public void setFirstInstallTime(long firstInstallTime) {
		this.firstInstallTime = firstInstallTime;
	}

	public AppInfor() {

    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public Bitmap getIconAppDrawable() {
        return iconAppDrawable;
    }

    public void setIconAppDrawable(Bitmap iconAppDrawable) {
        this.iconAppDrawable = iconAppDrawable;
    }

    public byte[] getBitmap() {
        return bitmap;
    }

    public void setBitmap(byte[] bitmap) {
        this.bitmap = bitmap;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public long getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(long cacheSize) {
        this.cacheSize = cacheSize;
    }

    public long getDataSize() {
        return dataSize;
    }

    public void setDataSize(long dataSize) {
        this.dataSize = dataSize;
    }

    public long getAppSize() {
        return appSize;
    }

	public void setAppSize(long appSize) {
        this.appSize = appSize;
    }

    public static final Parcelable.Creator<AppInfor> CREATOR = new Creator<AppInfor>() {
        public AppInfor createFromParcel(Parcel source) {
            AppInfor mAppInfor = new AppInfor();
            mAppInfor.appName = source.readString();
            mAppInfor.packageName = source.readString();
            mAppInfor.versionName = source.readString();
            mAppInfor.totalSize = source.readLong();
            mAppInfor.firstInstallTime = source.readLong();
            mAppInfor.appSize = source.readLong();
            mAppInfor.dataSize = source.readLong();
            mAppInfor.cacheSize = source.readLong();
            mAppInfor.versionCode = source.readInt();
            mAppInfor.uid = source.readInt();
            mAppInfor.bitmap = source.createByteArray();
            return mAppInfor;
        }

        public AppInfor[] newArray(int size) {
            return new AppInfor[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(appName);
        dest.writeString(packageName);
        dest.writeString(versionName);
        dest.writeLong(totalSize);
        dest.writeLong(firstInstallTime);
        dest.writeLong(appSize);
        dest.writeLong(dataSize);
        dest.writeLong(cacheSize);
        dest.writeInt(versionCode);
        dest.writeInt(uid);
        dest.writeByteArray(bitmap);
    }
}
