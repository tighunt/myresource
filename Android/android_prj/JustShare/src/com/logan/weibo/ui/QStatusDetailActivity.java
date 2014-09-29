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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.logan.R;
import com.logan.util.UIHelper;
import com.logan.weibo.bean.QSource;
import com.logan.weibo.bean.QStatus;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
/**
 * 腾讯微博正文
 * @author Logan <a href="https://github.com/Logan676/JustSharePro"/>
 *   
 * @version 1.0 
 *  
 */
public class QStatusDetailActivity extends Activity {
	//private final String TAG = "QStatusDetail";

	//private QAsyncImageLoader imageLoader = new QAsyncImageLoader();
	ImageView back = null;

	ImageView head;// 头像
	TextView nick;// 用户名
	TextView origText;// 微博文本
	ImageView image;// 微博图片
	TextView from;// 平台来源
	ImageView isVip;
	TextView mcount;
	TextView count;
	TextView timeStamp;
	// --------转发微博-----------------
	QSource mQSource = null;
	TextView source_text;// 转发微博文字
	ImageView source_image;// 转发微博图片
	View source_ll;// 转发微博父容器
	Boolean isVisible = true;
	String httpMethod = "GET";
	QStatus mStatus = null;
	View retweetBtn = null;
	View commentBtn = null;

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
		
		initViews();
		Intent intent = getIntent();
		mStatus = (QStatus) intent.getSerializableExtra("detail");
		try {
			setData2Views(mStatus);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	private void initViews() {
		back = (ImageView) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		head = (ImageView) findViewById(R.id.status_profile_image);
		nick = (TextView) findViewById(R.id.status_screen_name);
		origText = (TextView) findViewById(R.id.status_text);
		image = (ImageView) findViewById(R.id.status_microBlogImage);
		from = (TextView) findViewById(R.id.status_from);
		timeStamp = (TextView) findViewById(R.id.status_created_at);
		isVip = (ImageView) findViewById(R.id.status_vipImage);
		mcount = (TextView) findViewById(R.id.status_commentsCount);
		count = (TextView) findViewById(R.id.status_repostsCount);
		// ------------转发微博------------------------
		source_image = (ImageView) findViewById(R.id.status_retweeted_status_microBlogImage);
		source_text = (TextView) findViewById(R.id.status_retweeted_status_text);
		source_ll = findViewById(R.id.status_retweeted_status_ll);
		
		retweetBtn = (View) findViewById(R.id.relativelyt_redirect);
		commentBtn = (View) findViewById(R.id.relativelyt_commment);
		retweetBtn.setOnClickListener(mListener);
		commentBtn.setOnClickListener(mListener);
		
	}

	private View.OnClickListener mListener =new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			String id ="";
			Intent i =new Intent();
			switch (v.getId()) {
			case R.id.relativelyt_redirect:
				id = (String) v.getTag();
				i.putExtra("rt_id", id);
				i.setClass(QStatusDetailActivity.this, RetweetListActivity.class);
				startActivity(i);
				break;
			case R.id.relativelyt_commment:
				id = (String) v.getTag();
				i.putExtra("rt_id", id);
				i.setClass(QStatusDetailActivity.this, CommentListActivity.class);
				startActivity(i);
				break;
			default:
				break;
			}
		}
	};

	private void setData2Views(QStatus status) throws JSONException {
		nick.setText(status.getNick());
		// if (!origText.equals("") && origText != null)
		origText.setText(status.getOrigText());
		// else
		// origText.setText(text);
		if (!status.getImage().equals("")) {
			image.setVisibility(View.VISIBLE); //setViewImage(image, status.getImage());
			image.setTag(status.getMediun_image());
			image.setOnClickListener(imageClickListener);
			imageLoader.displayImage(status.getImage(), image, options);
		} else {
			image.setVisibility(View.GONE);
		}
		from.setText(status.getFrom());

		if(!status.getHead().equals("")) imageLoader.displayImage(status.getHead(), head, options);
		
		mcount.setText(status.getMcount());
		count.setText(status.getCount());
		timeStamp.setVisibility(View.VISIBLE);
		if (!status.getCreated_at().equals(""))
			timeStamp.setText(status.getCreated_at());
		else
			timeStamp.setVisibility(View.INVISIBLE);
		if (status.getIsVip() == 1)
			isVip.setVisibility(View.VISIBLE);
		else
			isVip.setVisibility(View.INVISIBLE);

		// ------------------------转发微博----------------------------------
		mQSource = status.getSource();
		if(mQSource ==null) isVisible = false;
		else 
			{
				if (!mQSource.getSource_image().equals("") ) {
				source_image.setVisibility(View.VISIBLE);
				source_image.setOnClickListener(imageClickListener);
				source_image.setTag(mQSource.getSource_medium_image());
				imageLoader.displayImage(mQSource.getSource_image(), source_image, options);
				} else source_image.setVisibility(View.GONE);
				if (!mQSource.getSource_text().equals("")) {
					source_text.setVisibility(View.VISIBLE);
					source_text.setText(mQSource.getSource_text());
				} else source_text.setVisibility(View.GONE);
			}
		if (!isVisible) source_ll.setVisibility(View.GONE);
		else source_ll.setVisibility(View.VISIBLE);
		retweetBtn.setTag(status.getId());
		commentBtn.setTag(status.getId());
	}
	
	private View.OnClickListener imageClickListener = new View.OnClickListener(){
		public void onClick(View v) {
			UIHelper.showImageZoomDialog(v.getContext(), (String)v.getTag());
		}
	};
	
}
