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

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.logan.weibo.bean.Account;
import com.logan.weibo.bean.BaseActivity;
import com.tencent.weibo.oauthv2.OAuthV2;
import com.tencent.weibo.oauthv2.OAuthV2Client;
/**
 * 腾讯微博授权认证类
 * @author Tencent
 *   
 * @version 1.0 
 *  
 */
public class QAuthorizeActivity extends BaseActivity {
	private final String TAG = "QAuthorizeActivity";
	// !!!请根据您的实际情况修改!!! 认证成功后浏览器会被重定向到这个url中 必须与注册时填写的一致
	//private String redirectUri = "http://blog.csdn.net/logan676";
	// !!!请根据您的实际情况修改!!! 换为您为自己的应用申请到的APP KEY
	//private String clientId = "801218195";
	//!!!请根据您的实际情况修改!!! 换为您为自己的应用申请到的APP SECRET
	//private String clientSecret = "897a59f506ef91de34e8cec2d0ff90d0";

	
	private String head = "";
	private String name = "";
	/**
	 * 
	 * @param openid
	 * @param openkey
	 * @param name
	 * @param url
	 * @param accessToken
	 * @param expires_in
	 * @param plf
	 */
	public void addTencentAccount(String openid, String openkey, String name,
			String url, String accessToken, String expires_in, String plf) {
		Account account = new Account(openid, openkey, name, url, accessToken,
				expires_in, plf);
		mDBManager.addTencent(account);
	}

	public Boolean getData() {
		
		try {
			// 调用QWeiboSDK获取用户信息
			String userData = userAPI.info(oAuth, "json");
			JSONObject js = new JSONObject(userData);
			if (!js.isNull("data")) {
				JSONObject info=js.getJSONObject("data");
				name = info.getString("name");
				head = info.getString("head");
				return true;
			} else
				return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.v(TAG, "getData Error");
			return false;
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		oAuth = new OAuthV2(TENCENT_REDIRECT_URL);
		oAuth.setClientId(CLIENT_ID);
		oAuth.setClientSecret(CLIENT_SECRET);
		// 关闭OAuthV2Client中的默认开启的QHttpClient。
		OAuthV2Client.getQHttpClient().shutdownConnection();
		LinearLayout linearLayout = new LinearLayout(this);
		WebView webView = new WebView(this);
		linearLayout.addView(webView, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		setContentView(linearLayout);
		String urlStr = OAuthV2Client.generateImplicitGrantUrl(oAuth);// 使用Implicit
																		// grant方式鉴权时，合成转向授权页面的url

		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(true);
		webView.requestFocus();
		webView.loadUrl(urlStr);
		System.out.println(urlStr.toString());
		Log.i(TAG, "WebView Starting....");
		WebViewClient client = new WebViewClient() {
			/**
			 * 回调方法，当页面开始加载时执行
			 */
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				Log.i(TAG, "WebView onPageStarted...");
				Log.i(TAG, "URL = " + url);
				if (url.indexOf("access_token=") != -1) {
					int start = url.indexOf("access_token=");
					String responseData = url.substring(start);
					// 得到服务器返回的包含access
					// token等的回应包后，解析存储到OAuth类中
					OAuthV2Client
							.parseAccessTokenAndOpenId(responseData, BaseActivity.getInstance());

					view.destroyDrawingCache();
					view.destroy();
					if (getData()) {
						addTencentAccount(oAuth.getOpenid(), oAuth.getOpenkey(), name,head, oAuth.getAccessToken(),oAuth.getExpiresIn(), "tencent");
						Log.i(TAG, "cccessToken:"+ oAuth.getAccessToken());
					} else
						finish();

					Intent intent = new Intent();
					intent.setClass(QAuthorizeActivity.this,
							AccountActivity.class);
					startActivity(intent);
					finish();

				}
				super.onPageStarted(view, url, favicon);

			}

			/*
			 * TODO Android2.2及以上版本才能使用该方法
			 * 目前https://open.t.qq.com中存在http资源会引起sslerror，待网站修正后可去掉该方法
			 */
			@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				if ((null != view.getUrl())
						&& (view.getUrl().startsWith("https://open.t.qq.com"))) {
					handler.proceed();// 接受证书
				} else {
					handler.cancel(); // 默认的处理方式，WebView变成空白页
				}
				// handleMessage(Message msg); 其他处理
			}
		};
		webView.setWebViewClient(client);
	}

	@Override
	public int getLayout() {
		// TODO Auto-generated method stub
		return 0;
	}

}
