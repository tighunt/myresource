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

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.logan.weibo.bean.Account;
/**
 * 数据库管理类
 * @author Logan <a href="https://github.com/Logan676/JustSharePro"/>
 *   
 * @version 1.0 
 *  
 */
public class DBManager {
	private DBHelper helper;
	private SQLiteDatabase mSQLiteDatabase;

	public DBManager(Context context) {
		helper = new DBHelper(context);
		// 因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0,
		// mFactory);
		// 所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
		mSQLiteDatabase = helper.getWritableDatabase();
	}

	/**
	 * add accounts
	 * 
	 * @param accounts
	 */
	public void add(Account account) {
		mSQLiteDatabase.beginTransaction(); // 开始事务
		try {

			mSQLiteDatabase
					.execSQL(
							"INSERT INTO account VALUES(null, ?, ?, ?, ?, ?, ?,?,?)",
							new Object[] { account.getId(), account.getName(),
									account.getUrl(), account.getToken(),
									account.getExpires_in(), account.getPlf(),
									"", "" });

			mSQLiteDatabase.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			mSQLiteDatabase.endTransaction(); // 结束事务
		}
	}

	public void addTencent(Account qAccount) {
		mSQLiteDatabase.beginTransaction(); // 开始事务
		try {

			mSQLiteDatabase.execSQL(
					"INSERT INTO account VALUES(null, ?, ?, ?, ?, ?, ?,?,?)",
					new Object[] { qAccount.getId(), qAccount.getName(),
							qAccount.getUrl(), qAccount.getToken(),
							qAccount.getExpires_in(), qAccount.getPlf(),
							qAccount.getOpenid(), qAccount.getOpenkey() });

			mSQLiteDatabase.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			mSQLiteDatabase.endTransaction(); // 结束事务
		}
	}

	/**
	 * close database
	 */
	public void closeDB() {
		mSQLiteDatabase.close();
	}

	/**
	 * delete one account
	 * 
	 * @param account
	 */
	public void deleteAccount(Account account) {

		mSQLiteDatabase.delete("account", "name= ?",
				new String[] { account.getName() });

	}

	/**
	 * delete accounts
	 * 
	 * @param accounts
	 */
	public void deleteAccounts(ArrayList<Account> accounts) {
		for (Account account : accounts) {
			mSQLiteDatabase.delete("account", "id= ?",
					new String[] { account.getId() });
		}
	}

	/**
	 * query all accounts, return list
	 * 
	 * @return List<Account>
	 */
	public List<Account> getAccounts() {
		ArrayList<Account> accounts = new ArrayList<Account>();
		Cursor c = queryTheCursor();
		while (c.moveToNext()) {
			Account account = new Account();
			// account.set = c.getInt(c.getColumnIndex("_id"));
			account.setId(c.getString(c.getColumnIndex("id")));
			account.setName(c.getString(c.getColumnIndex("name")));
			account.setUrl(c.getString(c.getColumnIndex("url")));
			account.setToken(c.getString(c.getColumnIndex("token")));
			account.setExpires_in(c.getString(c.getColumnIndex("expires_in")));
			account.setPlf(c.getString(c.getColumnIndex("plf")));
			account.setOpenid(c.getString(c.getColumnIndex("openid")));
			account.setOpenkey(c.getString(c.getColumnIndex("openkey")));
			accounts.add(account);
		}
		c.close();
		return accounts;
	}

	/**
	 * query all accounts, return cursor
	 * 
	 * @return Cursor
	 */
	public Cursor queryTheCursor() {
		Cursor c = mSQLiteDatabase.rawQuery("SELECT * FROM account", null);
		return c;
	}

	/**
	 * update account's name
	 * 
	 * @param account
	 */
	public void updateName(Account account) {
		ContentValues cv = new ContentValues();
		cv.put("name", account.getName());
		mSQLiteDatabase.update("account", cv, "id = ?",
				new String[] { account.getId() });// 根据ID，更新用昵称
	}
}
