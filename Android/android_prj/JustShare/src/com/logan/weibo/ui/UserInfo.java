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

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.logan.R;
import com.logan.weibo.bean.BaseActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.weibo.net.WeiboException;
/**
 * 用户信息
 * @author Logan <a href="https://github.com/Logan676/JustSharePro"/>
 *   
 * @version 1.0 
 *  
 */
public class UserInfo extends BaseActivity {

	private final String TAG = "UserInfo";
	// ----------头部工具栏-----------------------
	private ImageView tweet = null;
	private TextView title = null;

	// ----------底部导航栏------------------------
	private View friendTimeLine;
	private View userTimeLine;
	private View userNews;
	private View userInfo;
	private View more;
	private FooterClickListener listener;
	private ImageView userHead = null;
	private TextView userName = null;
	private TextView genderOfUser = null;
	private TextView locationOfUser = null;
	private TextView descriptionOfUser = null;

	private Button mblogNumBtn = null;
	private Button fansNumBtn = null;
	private Button guanzhuNumBtn = null;

	// -----------------Sina---------------
	private String jsonData;
	private JSONObject jsonObj = null;
	// ---------------Tencent------------------
	private String jsonQData;
	private JSONObject jsonQObj = null;
	private JSONObject mInfo = null;

	private String name = null;
	private String headerImageUrl = null;
	private String gender = null;
	private int sex = 0;// sex : 用户性别，1-男，2-女，0-未填写
	private String location = null;
	private String description = null;
	// followers_count:
	private String followers_count = null;
	// statuses_count:
	private String statuses_count = null;
	// friends_count:
	private String friends_count = null;
	//------------标识符-------------------//
	private Boolean sina =false;
	private Boolean ten =false;
	
	// Universal Image Loader for Android 第三方框架组件
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayout());
		
		initComponents();
		initFooter();
		handleData();
		Intent i = getIntent();
		isSina = i.getBooleanExtra("isSina", false);
		isTencent = i.getBooleanExtra("isTencent", false);
		int currentTag = i.getIntExtra("currentTag", 3);
		setSelectedFooterTab(currentTag);//注意把这个函数放在initFooter函数下面执行
		sina = isSina;
		ten = isTencent;
		Log.v(TAG, "On-isSina= "+isSina);
		Log.v(TAG, "On-isTencent= "+isTencent);
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.loading)
		.showImageForEmptyUri(R.drawable.icon)
		.cacheInMemory()
		.cacheOnDisc()
		.displayer(new RoundedBitmapDisplayer(20))
		.build();
	}

	private void initComponents() {
		tweet = (ImageView) findViewById(R.id.weibo_headbar_tweet);
		tweet.setVisibility(View.INVISIBLE);
		title = (TextView) findViewById(R.id.weibo_headbar_title);
		title.setText("个人资料");
		userHead = (ImageView) findViewById(R.id.userHead);
		userName = (TextView) findViewById(R.id.userName);
		genderOfUser = (TextView) findViewById(R.id.sexOfUser);
		locationOfUser = (TextView) findViewById(R.id.locationOfUser);
		descriptionOfUser = (TextView) findViewById(R.id.introductionTV);
		mblogNumBtn = (Button) findViewById(R.id.mblogNumBtn);
		mblogNumBtn.setOnClickListener(mListener);
		fansNumBtn = (Button) findViewById(R.id.fansNumBtn);
		fansNumBtn.setOnClickListener(mListener);
		guanzhuNumBtn = (Button) findViewById(R.id.guanzhuNumBtn);
		guanzhuNumBtn.setOnClickListener(mListener);

	}
	private OnClickListener mListener = new OnClickListener() {
		Intent i = new Intent();
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.mblogNumBtn:
				
				i.putExtra("isSina", sina);
				i.putExtra("isTencent", ten);
				i.putExtra("currentTag", 1);
				i.setClass(UserInfo.this, TimeLineActivity.class);
				UserInfo.this.startActivity(i);
				break;
				
			case R.id.fansNumBtn:
				
				i.setClass(UserInfo.this, FansListActivity.class);
				UserInfo.this.startActivity(i);
				break;
				
			case R.id.guanzhuNumBtn:
				i.setClass(UserInfo.this, IdolListActivity.class);
				UserInfo.this.startActivity(i);
				break;
			}
			//finish();
		}
	};
	
	private void initFooter(){
		// -----------------底部导航栏---------------------------
		listener = new FooterClickListener();
		friendTimeLine = findViewById(R.id.weibo_menu_friendTimeLine);
		userTimeLine = findViewById(R.id.weibo_menu_userTimeLine);
		userNews = findViewById(R.id.weibo_menu_userNews);
		userInfo = findViewById(R.id.weibo_menu_myInfo);
		more = findViewById(R.id.weibo_menu_more);
		friendTimeLine.setId(0);
		userTimeLine.setId(1);
		userNews.setId(2);
		userInfo.setId(3);
		more.setId(4);
		friendTimeLine.setOnClickListener(listener);
		userTimeLine.setOnClickListener(listener);
		userNews.setOnClickListener(listener);
		userInfo.setOnClickListener(listener);
		more.setOnClickListener(listener);
	}
	private class FooterClickListener implements android.view.View.OnClickListener{

		@Override
		public void onClick(View v) {
			Intent i = new Intent();
			i.putExtra("isSina", sina);
			i.putExtra("isTencent", ten);
			i.putExtra("currentTag", v.getId());
			i.setClass(getApplicationContext(), TimeLineActivity.class);
			startActivity(i);
			finish();
		}
		
	}
	private void handleData() {
		try {
			if (isSina) {
				jsonData = getUserInfo();
				//Log.v(TAG, "isSina:  " + jsonData);
			}
			if (isTencent) {
				jsonQData = getQuserInfo(BaseActivity.getInstance());
				//Log.v(TAG, "isTencent:  " + jsonQData);
			}

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
		if (jsonData != null) {

			try {
				jsonObj = new JSONObject(jsonData);
				bindData();
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
		if (jsonQData != null) {
			try {
				jsonQObj = new JSONObject(jsonQData);
				bindQData();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
	protected void setSelectedFooterTab(int i) {
		//mCurFooterTab = i;
		friendTimeLine.setBackgroundResource(0);
		userTimeLine.setBackgroundResource(0);
		userNews.setBackgroundResource(0);
		userInfo.setBackgroundResource(0);
		more.setBackgroundResource(0);
		if (i == 0) {friendTimeLine.setBackgroundResource(R.drawable.weibo_menu_cp_bg_selected);title.setText("微博主页");}
		if (i == 1) {userTimeLine.setBackgroundResource(R.drawable.weibo_menu_cp_bg_selected);title.setText("我的微博");}
		if (i == 2) {userNews.setBackgroundResource(R.drawable.weibo_menu_cp_bg_selected);title.setText("微博动态");}
		if (i == 3) userInfo.setBackgroundResource(R.drawable.weibo_menu_cp_bg_selected);
		if (i == 4) more.setBackgroundResource(R.drawable.weibo_menu_cp_bg_selected);
	}

	
	private void bindData() {
		try {
			name = jsonObj.getString("name");
			location = jsonObj.getString("location");
			gender = jsonObj.getString("gender");
			headerImageUrl = jsonObj.getString("profile_image_url");
			description = jsonObj.getString("description");
			statuses_count = jsonObj.getString("statuses_count");
			followers_count = jsonObj.getString("followers_count");
			friends_count = jsonObj.getString("friends_count");

		} catch (Exception e) {
			Log.v(TAG, "getDataFromJSON:exception");
		}
		userName.setText(name);
		userName.setTextColor(Color.BLACK);
		userName.setTextSize(20);
		locationOfUser.setText(location);
		locationOfUser.setTextColor(Color.BLACK);
		Log.v(TAG, description);
		descriptionOfUser.setText(description);

		if (gender.equals("m")) genderOfUser.setText("男");
		else if (gender.equals("f")) genderOfUser.setText("女");
		else genderOfUser.setText("未设置"); 
		genderOfUser.setTextColor(Color.BLACK);
		
		
		//setViewImage(userHead, headerImageUrl);
		imageLoader.displayImage(headerImageUrl, userHead);

		String statuses_count_temp = statuses_count + "</font><br><font size='10px' color='#A7A7A7'>微博";
		Spanned localSpanned1 = Html.fromHtml(statuses_count_temp);
		mblogNumBtn.setText(localSpanned1);
		String followers_count_temp = followers_count + "</font><br><font size='10px' color='#A7A7A7'>粉丝";
		Spanned localSpanned2 = Html.fromHtml(followers_count_temp);
		fansNumBtn.setText(localSpanned2);
		String friends_count_temp = friends_count + "</font><br><font size='10px' color='#A7A7A7'>关注";
		Spanned localSpanned3 = Html.fromHtml(friends_count_temp);
		guanzhuNumBtn.setText(localSpanned3);
	}

	private void bindQData() {
		try {
			if (!jsonQObj.isNull("data")) {
				mInfo = jsonQObj.getJSONObject("data");
				name = mInfo.getString("nick");
				location = mInfo.getString("location");
				sex = mInfo.getInt("sex");
				headerImageUrl = mInfo.getString("head") + "/100";
				description = mInfo.getString("introduction");
				statuses_count = mInfo.getString("tweetnum");
				followers_count = mInfo.getString("fansnum");
				friends_count = mInfo.getString("idolnum");
			}
		} catch (Exception e) {
			Log.v(TAG, "getDataFromJSON:exception");
		}
		userName.setText(name);
		userName.setTextColor(Color.BLACK);
		userName.setTextSize(20);
		locationOfUser.setText(location);
		locationOfUser.setTextColor(Color.BLACK);
		Log.v(TAG, description);
		descriptionOfUser.setText(description);
		if (isSina) {
			if (gender.equals("m")) genderOfUser.setText("男");
			else if (gender.equals("f")) genderOfUser.setText("女");
			else genderOfUser.setText("未设置");
		} else if (isTencent) {
			if (sex == 1) genderOfUser.setText("男");
			else if (sex == 2) genderOfUser.setText("女");
			else genderOfUser.setText("未设置");

		}

		genderOfUser.setTextColor(Color.BLACK);

		//setQViewImage(userHead, headerImageUrl);
		imageLoader.displayImage(headerImageUrl, userHead);

		String statuses_count_temp = statuses_count + "</font><br><font size='10px' color='#A7A7A7'>微博";
		Spanned localSpanned1 = Html.fromHtml(statuses_count_temp);
		mblogNumBtn.setText(localSpanned1);
		String followers_count_temp = followers_count + "</font><br><font size='10px' color='#A7A7A7'>粉丝";
		Spanned localSpanned2 = Html.fromHtml(followers_count_temp);
		fansNumBtn.setText(localSpanned2);
		String friends_count_temp = friends_count + "</font><br><font size='10px' color='#A7A7A7'>关注";
		Spanned localSpanned3 = Html.fromHtml(friends_count_temp);
		guanzhuNumBtn.setText(localSpanned3);
	}

	@Override
	public int getLayout() {
		return R.layout.weibo_userinfo;
	}

	@Override
	public void onComplete(String response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onIOException(IOException e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(WeiboException e) {
		// TODO Auto-generated method stub
		
	}
}
