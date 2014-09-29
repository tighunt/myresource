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
package com.logan.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.logan.util.StringUtils;

/**
 * 应用程序配置类：用于保存用户相关信息及设置
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class AppConfig{
	
	private final static String APP_CONFIG = "config";
	
	public final static String TEMP_TWEET = "temp_tweet";
	public final static String TEMP_TWEET_IMAGE = "temp_tweet_image";
	public final static String TEMP_MESSAGE = "temp_message";
	public final static String TEMP_COMMENT = "temp_comment";
	public final static String TEMP_POST_TITLE = "temp_post_title";
	public final static String TEMP_POST_CATALOG = "temp_post_catalog";
	public final static String TEMP_POST_CONTENT = "temp_post_content";
	
	public final static String CONF_APP_UNIQUEID = "APP_UNIQUEID";
	public final static String CONF_COOKIE = "cookie";
	public final static String CONF_ACCESSTOKEN = "accessToken";
	public final static String CONF_ACCESSSECRET = "accessSecret";
	public final static String CONF_EXPIRESIN = "expiresIn";
	public final static String CONF_LOAD_IMAGE = "perf_loadimage";
	public final static String CONF_SCROLL = "perf_scroll";
	public final static String CONF_HTTPS_LOGIN = "perf_httpslogin";
	public final static String CONF_VOICE = "perf_voice";
	public final static String CONF_CHECKUP = "perf_checkup";
	
	private Context mContext;
	//private AccessInfo accessInfo = null;
	private static AppConfig appConfig;
	
	public static AppConfig getAppConfig(Context context)
	{
		if(appConfig == null){
			appConfig = new AppConfig();
			appConfig.mContext = context;
		}
		return appConfig;
	}
	
	/**
	 * 获取Preference设置
	 */
	public static SharedPreferences getSharedPreferences(Context context)
	{
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	/**
	 * 是否加载显示文章图片
	 */
	public static boolean isLoadImage(Context context)
	{
		return getSharedPreferences(context)
				.getBoolean(CONF_LOAD_IMAGE, true);
	}	
	
	public String getCookie(){
		return get(CONF_COOKIE);
	}

	public void setAccessToken(String accessToken){
		set(CONF_ACCESSTOKEN, accessToken);
	}
	
	public String getAccessToken(){
		return get(CONF_ACCESSTOKEN);
	}
	
	public void setAccessSecret(String accessSecret){
		set(CONF_ACCESSSECRET, accessSecret);
	}
	
	public String getAccessSecret(){
		return get(CONF_ACCESSSECRET);
	}
	
	public void setExpiresIn(long expiresIn){
		set(CONF_EXPIRESIN, String.valueOf(expiresIn));
	}
	
	public long getExpiresIn(){
		return StringUtils.toLong(get(CONF_EXPIRESIN));
	}
	
	
	
	public String get(String key)
	{
		Properties props = get();
		return (props!=null)?props.getProperty(key):null;
	}
	
	public Properties get() {
		FileInputStream fis = null;
		Properties props = new Properties();
		try{
			//读取files目录下的config
			//fis = activity.openFileInput(APP_CONFIG);
			
			//读取app_config目录下的config
			File dirConf = mContext.getDir(APP_CONFIG, Context.MODE_PRIVATE);
			fis = new FileInputStream(dirConf.getPath() + File.separator + APP_CONFIG);
			
			props.load(fis);
		}catch(Exception e){
		}finally{
			try {
				fis.close();
			} catch (Exception e) {}
		}
		return props;
	}
	
	private void setProps(Properties p) {
		FileOutputStream fos = null;
		try{
			//把config建在files目录下
			//fos = activity.openFileOutput(APP_CONFIG, Context.MODE_PRIVATE);
			
			//把config建在(自定义)app_config的目录下
			File dirConf = mContext.getDir(APP_CONFIG, Context.MODE_PRIVATE);
			File conf = new File(dirConf, APP_CONFIG);
			fos = new FileOutputStream(conf);
			
			p.store(fos, null);
			fos.flush();
		}catch(Exception e){	
			e.printStackTrace();
		}finally{
			try {
				fos.close();
			} catch (Exception e) {}
		}
	}

	public void set(Properties ps)
	{
		Properties props = get();
		props.putAll(ps);
		setProps(props);
	}
	
	public void set(String key,String value)
	{
		Properties props = get();
		props.setProperty(key, value);
		setProps(props);
	}
	
	public void remove(String...key)
	{
		Properties props = get();
		for(String k : key)
			props.remove(k);
		setProps(props);
	}
}
