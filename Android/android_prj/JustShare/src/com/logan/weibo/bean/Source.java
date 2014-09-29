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
 * 微博发布平台
 * @author Logan <a href="https://github.com/Logan676/JustSharePro"/>
 *   
 * @version 1.0 
 *  
 */
public class Source implements java.io.Serializable{

	private static final long serialVersionUID = -8972443458374235866L;
    private String url;               
    private String relationShip;      
    private String name;              
	public Source(String str) {
		super();
		String[] source = str.split("\"",5);
        url = source[1];
        relationShip = source[3];
        name = source[4].replace(">", "").replace("</a", "");
	}
    
	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}


	public String getRelationship() {
		return relationShip;
	}


	public void setRelationship(String relationShip) {
		this.relationShip = relationShip;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

    
	@Override
	public String toString() {
		return "Source [url=" + url + ", relationShip=" + relationShip
				+ ", name=" + name + "]";
	}


}
