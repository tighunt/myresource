package com.android.settings.update;

/**
 * 从服务器返回的版本信息
 * 
 * @author 杜聪甲(ducj@biaoqi.com.cn)
 * @since 1.0 2011-11-18
 */
public class VersionInfor {

	// 发生错误时有效,描述信息
	private String ds;
	// 版本号
	private String version;
	// 升级功能描述
	private String uds;
	// 升级包的地址
	private String url;
	// 升级包的大小
	private long size;
	// 升级包的签名
	private String md;//###########
	// 是否强制升级
	private int force;

	public int getForce() {
		return force;
	}

	public void setForce(int force) {
		this.force = force;
	}

	public String getDs() {
		return ds;
	}

	public void setDs(String ds) {
		this.ds = ds;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getUds() {
		return uds;
	}

	public void setUds(String uds) {
		this.uds = uds;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getMd() {
		return md;
	}

	public void setMd(String md) {
		this.md = md;
	}

}
