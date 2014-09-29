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

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.logan.R;
import com.logan.util.TimeUtil;
import com.logan.util.UIHelper;
import com.logan.weibo.bean.Status;
import com.logan.weibo.bean.User;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
/**
 * 新浪微博正文
 * @author Logan <a href="https://github.com/Logan676/JustSharePro"/>
 *   
 * @version 1.0 
 *  
 */
public class StatusDetailActivity extends Activity {
	
	private final String TAG = "StatusDetail";
	ImageView back = null;
	ImageView avatar = null;
	TextView name = null;
	TextView text = null;
	ImageView image = null;
	TextView source = null;
	TextView created_at = null;
	TextView comments = null;
	TextView rt = null;
	ImageView verified = null;
	LinearLayout ll_btn = null;
	LinearLayout ll_lyt = null;
	TextView rt_text = null;
	ImageView rt_Image = null;
	Status mStatus = null;
	String httpMethod = "GET";
	String position = null;
	String num = null;

	
	// Universal Image Loader for Android 第三方框架组件
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weibo_statusdetail);
		
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.loading)
		.showImageForEmptyUri(R.drawable.icon)
		.cacheInMemory()
		.cacheOnDisc()
		.displayer(new RoundedBitmapDisplayer(5))
		.build();
		Intent intent = getIntent();
		mStatus = (Status) intent.getSerializableExtra("detail");
		initViews();
		setData2Views(mStatus);
		
	}
	
	private void initViews() {
		back = (ImageView) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		avatar = (ImageView) findViewById(R.id.status_profile_image);
		name = (TextView) findViewById(R.id.status_screen_name);
		text = (TextView) findViewById(R.id.status_text);
		image = (ImageView) findViewById(R.id.status_microBlogImage);
		source = (TextView) findViewById(R.id.status_from);
		created_at = (TextView) findViewById(R.id.status_created_at);
		verified = (ImageView) findViewById(R.id.status_vipImage);
		comments = (TextView) findViewById(R.id.status_commentsCount);
		rt = (TextView) findViewById(R.id.status_repostsCount);
		ll_lyt = (LinearLayout) findViewById(R.id.status_retweeted_status_ll);
		rt_Image = (ImageView) findViewById(R.id.status_retweeted_status_microBlogImage);
		rt_text = (TextView) findViewById(R.id.status_retweeted_status_text);
	}

	private void setData2Views(Status status) {
		Log.v(TAG, status.toString());
		User user = status.getUser();
		Status retweetedStatus = null;
		if (status.getRetweetedStatus() != null) retweetedStatus = status.getRetweetedStatus();
		String profile_url = user.getProfileImageUrl();
		String name_value = user.getName();
		String text_value = status.getText();
		String image_url = status.getBmiddlePic();
		String source_value = status.getSource().getName();
		Boolean verified_value = user.isVerified();
		int statuses_value = status.getCommentsCount();
		int followers_value = status.getRepostsCount();
		String created_at_value = TimeUtil.converTime(new Date(status.getCreatedAt()).getTime() / 1000);
		String rt_text_value = "";
		if (retweetedStatus != null) {
			rt_text_value = retweetedStatus.getText();
			Log.v(TAG, "rt_text:  " + rt_text_value);
		} else {
			// do nothing
		}
		String rt_Image_url = "";
		if (retweetedStatus != null) {
			rt_Image_url = retweetedStatus.getBmiddlePic();
		} else {
			// do nothing
		}
		if (!profile_url.equals("")) imageLoader.displayImage(profile_url, avatar, options);
		name.setText(name_value);
		text.setText(text_value);
		if (!image_url.equals("")) {
			image.setVisibility(View.VISIBLE);
			imageLoader.displayImage(image_url, image, options);
			image.setTag(status.getOriginalPic());
			image.setOnClickListener(imageClickListener);
		} else image.setVisibility(View.GONE);
		source.setText(source_value);
		created_at.setText(created_at_value);
		comments.setText(followers_value + "");
		rt.setText(statuses_value + "");
		if (verified_value) verified.setVisibility(View.VISIBLE); 
		else verified.setVisibility(View.GONE);

		if (!rt_Image_url.equals("")) {
			rt_Image.setOnClickListener(imageClickListener);
			rt_Image.setTag(retweetedStatus.getOriginalPic());
			rt_Image.setVisibility(View.VISIBLE);
			imageLoader.displayImage(rt_Image_url, rt_Image, options);
		} else rt_Image.setVisibility(View.GONE);

		if (!rt_text_value.equals("")) {
			rt_text.setVisibility(View.VISIBLE);
			rt_text.setText(rt_text_value);
		} else rt_text.setVisibility(View.GONE);
		if (!rt_Image_url.equals("") || !rt_text_value.equals("")) ll_lyt.setVisibility(View.VISIBLE);
		else ll_lyt.setVisibility(View.GONE);

	}
	private View.OnClickListener imageClickListener = new View.OnClickListener(){
		public void onClick(View v) {
			UIHelper.showImageZoomDialog(v.getContext(), (String)v.getTag());
		}
	};
}