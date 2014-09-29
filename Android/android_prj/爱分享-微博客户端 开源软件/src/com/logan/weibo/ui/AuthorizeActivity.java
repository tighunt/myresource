/*
 * Copyright 2011 Sina.
 *
 * Licensed under the Apache License and Weibo License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.open.weibo.com
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.logan.weibo.ui;

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.logan.R; 
import com.logan.weibo.bean.Account;
import com.logan.weibo.bean.BaseActivity;
import com.weibo.net.AccessToken;
import com.weibo.net.DialogError;
import com.weibo.net.WeiboDialogListener;
import com.weibo.net.WeiboException;

/**
 * Sample code for testing weibo APIs.
 * 
 * @author ZhangJie (zhangjie2@staff.sina.com.cn)
 */

public class AuthorizeActivity extends BaseActivity {
	/*
	 * 本页面用来显示新浪微博或者腾讯微博授权页，用户需要输入用户名和明码，进行授权
	 * 代码实现上使用了webview控件，如果你对此不太了解，那就google一下吧
	 */
	private final String TAG = "AuthorizeActivity";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mWeibo.setupConsumerConfig(CONSUMER_KEY, CONSUMER_SECRET);

		// 隐式授权认证方式
		mWeibo.setRedirectUrl(REDIRECT_URL);
		// 此处回调页内容应该替换为与appkey对应的应用回调页
		// 对应的应用回调页可在开发者登陆新浪微博开发平台之后，
		// 进入我的应用--应用详情--应用信息--高级信息--授权设置--应用回调页进行设置和查看，
		// 应用回调页不可为空

		mWeibo.authorize(AuthorizeActivity.this, new AuthDialogListener());

	}

	class AuthDialogListener implements WeiboDialogListener {

		@Override
		public void onComplete(Bundle values) {
			Log.v(TAG, "onComplete");
			token = values.getString("access_token");
			Log.v(TAG, "token:" + token);
			expires_in = values.getString("expires_in");
			Log.v(TAG, "expires_in:" + expires_in);
			accessToken = new AccessToken(token, CONSUMER_SECRET);
			Log.v(TAG, "accessToken:" + accessToken);
			accessToken.setExpiresIn(expires_in);
			mWeibo.setAccessToken(accessToken);

			if (getData())
				addAccount(userName, id, imgUrl, "sina", token, expires_in);
			else
				finish();
			// Editor ed = spf.edit();
			// ed.putString(BaseActivity.TOKEN, token);
			// ed.putString(BaseActivity.EXPIRE, expires_in);
			// ed.commit();

			Intent intent = new Intent();
			intent.setClass(AuthorizeActivity.this, AccountActivity.class);
			startActivity(intent);
			finish();
		}
		/**
		 * 
		 * @param name
		 * @param id
		 * @param url
		 * @param plf
		 * @param token
		 * @param expires_in
		 */
		public void addAccount(String name, String id, String url, String plf,
				String token, String expires_in) {
			Account account = new Account(name, id, url, plf, token, expires_in);
			mDBManager.add(account);
		}

		private String imgUrl = "";
		private String userName = "";
		private String id = "";

		private Boolean getData() {
			String jsonData = null;
			JSONObject jsonObj = null;
			try {
				id = getUID();
				jsonData = getUserInfo();
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (WeiboException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Log.v(TAG, "jsonData:  " + jsonData);

			try {
				if (jsonData != null && !jsonData.equals(""))
					jsonObj = new JSONObject(jsonData);
				else
					return false;
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Log.v(TAG, "jsonObj:  " + jsonObj);
			try {
				imgUrl = jsonObj.getString("profile_image_url");
				Log.v(TAG, "profile_image_url:  " + imgUrl);
				userName = jsonObj.getString("name");
				Log.v(TAG, "userName:  " + userName);
				// Account mAccount = new Account();
				// mAccount.setName(userName);
				// mAccount.setUrl(imgUrl);
				// listData.add(mAccount);
				return true;
			} catch (JSONException e) {
				e.printStackTrace();
				Log.v(TAG, "JSONException");
				return false;
			}
		}

		@Override
		public void onError(DialogError e) {
			Toast.makeText(getApplicationContext(), "授权出错 : " + e.getMessage(),
					Toast.LENGTH_SHORT).show();
			finish();
		}

		@Override
		public void onCancel() {
			Toast.makeText(getApplicationContext(), "用户取消授权！",
					Toast.LENGTH_SHORT).show();
			finish();
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Toast.makeText(getApplicationContext(),
					"Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
					.show();
			finish();
		}

	}

	@Override
	public void onResume() {
		super.onResume();
	}


	@Override
	public int getLayout() {
		return R.layout.account_authorize;
	}

}