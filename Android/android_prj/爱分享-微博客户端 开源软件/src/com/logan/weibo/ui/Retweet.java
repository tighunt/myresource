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
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.logan.R;
/**
 * 转发
 * @author Logan <a href="https://github.com/Logan676/JustSharePro"/>
 *   
 * @version 1.0 
 *  
 */
public class Retweet extends Activity {
	private String httpMethod = "GET";
	private String url;
	private String jsonData;
	private JSONObject jsonObj = null;
	private SharedPreferences pres = null;
	private Button redirectBtn;
	private ImageButton backButton;
	// 要转发的微博的内容
	private String text = null;
	private EditText editText = null;
	private View delword_ll = null;
	SharedPreferences preferences = null;
	private final int WEIBO_MAX_LENGTH = 140;
	private TextView mTextNum = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weibo_retweet);
		mTextNum = (TextView) findViewById(R.id.redirec_text_limit);
		delword_ll = findViewById(R.id.delword_ll);
		delword_ll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				editText.setText("");
			}
		});
		backButton = (ImageButton) findViewById(R.id.writeBackBtn);
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// preferences = getSharedPreferences("redirectBlogText",
				// MODE_PRIVATE);
				// if (preferences != null)
				// editText.setText(preferences.getString("text", null));
				// else
				// editText.setText(null);
				finish();
			}
		});
		redirectBtn = (Button) findViewById(R.id.redirectBtn);
		redirectBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					// Editor ed = preferences.edit();
					// ed.clear();
					// ed.commit();

					// 为发送微博准备必要的数据
					text = getData();
					// 发送微博
					zhuanFaWeiBo(text);
					finish();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		editText = (EditText) findViewById(R.id.microBlog_ed);
		editText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String mText = editText.getText().toString();
				int len = mText.length();
				if (len <= WEIBO_MAX_LENGTH) {
					len = WEIBO_MAX_LENGTH - len;
					mTextNum.setTextColor(Color.GRAY);
					if (!redirectBtn.isEnabled())
						redirectBtn.setEnabled(true);
				} else {
					len = len - WEIBO_MAX_LENGTH;

					mTextNum.setTextColor(Color.RED);
					if (redirectBtn.isEnabled())
						redirectBtn.setEnabled(false);
				}
				mTextNum.setText(String.valueOf(len));
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	private String getData() throws JSONException {
		Intent intent = getIntent();
		String weiBoID = intent.getStringExtra("weiBoID");
		pres = getSharedPreferences("parameters", Context.MODE_PRIVATE);
		String sina_access_token = pres.getString("sina_access_token", "");
		String sina_access_secret = pres.getString("sina_access_secret", "");
		url = "http://api.t.sina.com.cn/statuses/show/:" + weiBoID + ".json";
		// url = "http://api.t.sina.com.cn/statuses/retweet/:" + weiBoID +
		// ".json";
		/*
		 * 作用：根据Id获取一条微博内容 URL:https://api.weibo.com/2/statuses/show.json
		 * 支持格式:JSON HTTP请求方式:GET 是否需要登录:是 访问级别：普通接口 频次限制：是 参数： access_token
		 * 需要获取的微博id
		 * 旧版本的URL：http://api.t.sina.com.cn/statuses/retweet/:id.format
		 */
//		jsonData = new Weibo().getOneWeiBo(url, httpMethod, sina_access_token,
//				sina_access_secret, null, null);

		try {
			jsonObj = new JSONObject(jsonData);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		text = jsonObj.getString("text");
		return text;
	}

	/**
	 * 转发微博
	 * 
	 * @param status
	 */
	private void zhuanFaWeiBo(String text) {
		httpMethod = "POST";
		String sign = null;

		SharedPreferences pres = this.getSharedPreferences("parameters",
				Context.MODE_PRIVATE);
		String sina_access_token = pres.getString("sina_access_token", "");
		String sina_access_secret = pres.getString("sina_access_secret", "");

		url = "http://api.t.sina.com.cn/statuses/update.json";
//		OauthUtils.getInstance().initSinaData();
//		jsonData = new Weibo().submitOneWeibo(url, httpMethod,
//				sina_access_token, sina_access_secret, text, null, null, null);

		if ("-1".equals(jsonData)) {
			sign = "发送失败！";
		} else {
			try {
				jsonObj = new JSONObject(jsonData);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if ("400".equals(jsonData)) {
				sign = "重复发送！";
			} else
				try {
					if (jsonObj.getString("text").equals(text)) {

						sign = "sina发送成功！";
					} else {
						sign = "发送失败！";
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
		}
		Toast.makeText(Retweet.this, sign, Toast.LENGTH_SHORT).show();
	}

	// @Override
	// public void onBackPressed() {
	// String text = editText.getText().toString();
	// // 保存到本地，以备下次使用
	// preferences = getSharedPreferences("myBlogText", MODE_PRIVATE);
	// // 通过preferences得到它的编辑器对象edit
	// Editor edit = preferences.edit();
	// edit.putString("text", text);
	// edit.commit();
	// finish();
	// super.onBackPressed();
	// }
}
