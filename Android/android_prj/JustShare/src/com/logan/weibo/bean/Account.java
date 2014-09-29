/**   
 * Copyright (c) 2013 by Logan.	
 *   
 * 爱分享-微博客户端，是一款运行在android手机上的开源应用，代码和文档已托管在GitHub上，欢迎爱好者加入
 * 1.授权认证：Oauth2.0认证流程
 * 2.服务器访问操作流程
 * 3.新浪微博SDK和腾讯微博SDK
 * 4.HMAC加密算法
 * 5.SQLite数据库相关操作
 * 6.字符串处理，表情识别
 * 7.JSON解析，XML解析：超链接解析，时间解析等
 * 8.Android UI：样式文件，布局
 * 9.异步加载图片，异步处理数据，多线程  
 * 10.第三方开源框架和插件
 *    
 */
package com.logan.weibo.bean;

/**
 * 账号实体类
 * @author Logan <a href="https://github.com/Logan676/JustSharePro"/>
 *
 * @version 1.0 
 */
public class Account {

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getOpenkey() {
		return openkey;
	}

	public void setOpenkey(String openkey) {
		this.openkey = openkey;
	}

	// sina
	public Account(String name, String id, String url, String plf,
			String token, String expires_in) {
		this.name = name;
		this.id = id;
		this.url = url;
		this.plf = plf;
		this.token = token;
		this.expires_in = expires_in;
	}

	// tencent
	public Account(String openid, String openkey, String name,
			String url, String accessToken, String expires_in, String plf) {
		super();
		this.name = name;
		this.url = url;
		this.plf = plf;
		this.token = accessToken;
		this.expires_in = expires_in;
		this.openid = openid;
		this.openkey = openkey;
	}

	public Account() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getExpires_in() {
		return expires_in;
	}

	public void setExpires_in(String expires_in) {
		this.expires_in = expires_in;
	}

	public String getPlf() {
		return plf;
	}

	public void setPlf(String plf) {
		this.plf = plf;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private int _id;// SQLite自动生成、维护的列名，自增长
	private String name = "";// 名字-----sina
	private String id = "";// ID号-------sina
	private String url = "";// 头像地址
	private String plf = "";// 微博平台
	private String token = "";// -------sina:token;tencent:accessToken
	private String expires_in = "";
	private String openid = "";// ------tencent
	private String openkey = "";// ------tencent

}
