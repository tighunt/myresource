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
package com.logan.weibo.ui.more;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

import com.logan.R;
/**
 * 设置
 * @author Logan <a href="https://github.com/Logan676/JustSharePro"/>
 *   
 * @version 1.0 
 *  
 */
public class SettingActivity extends PreferenceActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.more_setting);	
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		// TODO Auto-generated method stub
		String key=preference.getKey();
		if(key!=null){
			if(key.equals("setting_cache")){
				Intent intent=new Intent();
				intent.setClass(SettingActivity.this, CacheSettingDialog.class);
				SettingActivity.this.startActivity(intent);
			}
			if(key.equals("setting_remind")){
				Intent intent=new Intent();
				intent.setClass(SettingActivity.this, RemindSettingDialog.class);
				SettingActivity.this.startActivity(intent);
			}
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
	
	
}
