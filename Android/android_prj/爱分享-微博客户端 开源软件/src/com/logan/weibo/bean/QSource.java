/**   
 * Copyright (c) 2013 by Logan.	
 *   
 * 爱分享-微博客户端，是一款运行在android手机上的微博应用，代码和文档已托管在GitHub上，欢迎爱好者加入
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

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
  
/**   
 * @author Logan 
 * @HomePage https://github.com/Logan676/JustSharePro
 * @DevQQGroup 232482105
 * @time 2013-1-23 下午2:17:41  
 * @version 1.0 
 *  
 */

public class QSource extends JSONObject implements Serializable{

	private static final long serialVersionUID = 1L;
	private static String source_nick ="";
	private static String source_text = "";// 微博文字信息，包含URL、昵称、是否认证的信息
	private static String source_image = "";
	private QSource (){}
	
	public String getSource_nick() {
		return source_nick;
	}

	public void setSource_nick(String source_nick) {
		this.source_nick = source_nick;
	}

	public String getSource_text() {
		return source_text;
	}

	public void setSource_text(String source_text) {
		this.source_text = source_text;
	}

	public String getSource_image() {
		return source_image;
	}

	public void setSource_image(String source_image) {
		this.source_image = source_image;
	}

	public static QSource getQSource(JSONObject json){
		QSource mQsource = new QSource();
		if (!json.isNull("source")) {
			JSONObject source;
			try {
				source = json.getJSONObject("source");
				if (!source.isNull("image")) {
					JSONArray images = source.getJSONArray("image");
					if (images != null && images.length() > 0) {
						mQsource.source_image = images.optString(0) + "/120";
						//Log.v(TAG, "source_image_url:  " + source_image);
					}
				}
				if (!source.isNull("nick")) {
					mQsource.source_nick = source.getString("nick");
					//Log.v(TAG, "source_nick:  " + source_nick);

					if (!source.isNull("origtext")) {
						mQsource.source_text = source_nick + ":  " + source.getString("origtext");
						//Log.v(TAG, "source_origtext:  " + source_text);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
			//Log.v(TAG, "sourcel:  " + source);

		return mQsource;
		}
		
		else return null;
		
	}
}
  