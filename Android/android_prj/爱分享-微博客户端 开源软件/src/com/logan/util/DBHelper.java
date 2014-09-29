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
package com.logan.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * 数据库类
 * @author Logan <a href="https://github.com/Logan676/JustSharePro"/>
 *   
 * @version 1.0 
 *  
 */
public class DBHelper extends SQLiteOpenHelper {

	private static final String ACCOUNT_DATABASE_NAME = "account.db";
	private static final int ACCOUNT_DATABASE_VERSION = 4;

	public DBHelper(Context context) {
		super(context, ACCOUNT_DATABASE_NAME, null, ACCOUNT_DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS account"
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, id VARCHAR,name VARCHAR,"
				+ "url VARCHAR,token VARCHAR, expires_in VARCHAR, plf VARCHAR,openid VARCHAR,openkey VARCHAR)");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS account");
		onCreate(db);

	}

}
