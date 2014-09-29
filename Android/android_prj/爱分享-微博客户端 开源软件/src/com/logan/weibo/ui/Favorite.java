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
package com.logan.weibo.ui;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
/**
 * 收藏微博
 * @author Logan <a href="https://github.com/Logan676/JustSharePro"/>
 *   
 * @version 1.0 
 *  
 */
public class Favorite extends Activity {
	String httpMethod = "POST";
	String url;
	String jsonData;
	String sign = null;
	JSONObject jsonObj = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		SharedPreferences pres = this.getSharedPreferences("parameters",
				Context.MODE_PRIVATE);
		String sina_access_token = pres.getString("sina_access_token", "");
		String sina_access_secret = pres.getString("sina_access_secret", "");
		Intent intent = getIntent();
		String weiBoID = intent.getStringExtra("weiBoID");

		// http://api.t.sina.com.cn/favorites/create.json
		url = "http://api.t.sina.com.cn/favorites/create.json";
//		OauthUtils.getInstance().initSinaData();
//		jsonData = new Weibo().addfavorite(url, httpMethod, sina_access_token,
//				sina_access_secret, weiBoID, null);
		try {
			jsonObj = new JSONObject(jsonData);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			if (jsonObj.getString("id").equals(weiBoID)) {
				sign = "收藏成功！";
			} else {
				sign = "收藏失败！";
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Toast.makeText(Favorite.this, sign, Toast.LENGTH_SHORT).show();
		finish();

	}
}
