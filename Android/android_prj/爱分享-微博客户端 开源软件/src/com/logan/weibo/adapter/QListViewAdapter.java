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
package com.logan.weibo.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
 * 腾讯微博列表适配器
 * @author Logan <a href="https://github.com/Logan676/JustSharePro"/>
 *   
 * @version 1.0 
 *  
 */
public class QListViewAdapter extends BaseAdapter {
	private final String TAG = "QListViewAdapter";
	private Context context;
	private LayoutInflater mInflater;
	private List<QStatus> list;
	private ViewHolder holder;

	// Universal Image Loader for Android 第三方框架组件
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	
	public QListViewAdapter(Context context, List<QStatus> list) {
		super();
		this.context = context;	
		this.list = list;
		this.mInflater = LayoutInflater.from(context);
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.loading)
		.showImageForEmptyUri(R.drawable.icon)
		.cacheInMemory()
		.cacheOnDisc()
		.displayer(new RoundedBitmapDisplayer(5))
		.build();
	}

	@Override
	public int getCount() {
		
		return this.list != null ? this.list.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		QStatus mQstatus = list.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.weibo_listview_item, null);
			holder.nick = (TextView) convertView.findViewById(R.id.item_screen_name);
			holder.head = (ImageView) convertView.findViewById(R.id.item_profile_image);

			holder.origText = (TextView) convertView.findViewById(R.id.item_text);
			holder.image = (ImageView) convertView.findViewById(R.id.item_microBlogImage);
			holder.from = (TextView) convertView.findViewById(R.id.item_from);

			holder.mcount = (TextView) convertView.findViewById(R.id.item_tweet_statuses_count);
			holder.count = (TextView) convertView.findViewById(R.id.item_tweet_followers_count);
			holder.timeStamp = (TextView) convertView.findViewById(R.id.item_created_at);
			holder.isVip = (ImageView) convertView.findViewById(R.id.item_vipImage);

			// ---------------转发微博---------------------
			holder.source_text = (TextView) convertView.findViewById(R.id.item_retweeted_status_text);
			holder.source_image = (ImageView) convertView.findViewById(R.id.item_retweeted_status_microBlogImage);
			holder.source_ll = convertView.findViewById(R.id.item_retweeted_status_ll);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.nick.setText(mQstatus.getNick());
		holder.origText.setText(mQstatus.getOrigText());
		String image_URL = mQstatus.getImage();
		//Log.v(TAG, image_URL);
		if (!image_URL.equals("")) {
			imageLoader.displayImage(mQstatus.getImage(), holder.image, options);
			holder.image.setOnClickListener(imageClickListener);
			holder.image.setTag(image_URL);
			holder.image.setVisibility(View.VISIBLE);
		} else {
			holder.image.setVisibility(View.GONE);
		}
		holder.from.setText(mQstatus.getFrom());

		if (!mQstatus.getHead().equals("") && mQstatus.getHead() != null) {
			imageLoader.displayImage(mQstatus.getHead(), holder.head, options);
		}

		holder.mcount.setText(mQstatus.getMcount());
		holder.count.setText(mQstatus.getCount());
		holder.timeStamp.setVisibility(View.VISIBLE);
		if (!mQstatus.getCreated_at().equals(""))
			holder.timeStamp.setText(mQstatus.getCreated_at());
		else
			holder.timeStamp.setVisibility(View.INVISIBLE);
		if (mQstatus.getIsVip() == 1)
			holder.isVip.setVisibility(View.VISIBLE);
		else
			holder.isVip.setVisibility(View.INVISIBLE);

		// ------------------------转发微博----------------------------------
		QSource mQSource = mQstatus.getSource();
		if(mQSource == null) mQstatus.setIsVisible(false);
		else{
			String rt_image_url =mQSource.getSource_image();
			if (!rt_image_url.equals("")) {
				holder.source_image.setVisibility(View.VISIBLE);
				imageLoader.displayImage(mQSource.getSource_image(), holder.source_image, options);
			} else
				holder.source_image.setVisibility(View.GONE);
			if (!mQSource.getSource_text().equals("")) {
				holder.source_text.setVisibility(View.VISIBLE);
				holder.source_text.setText(mQSource.getSource_text());
			} else
				holder.source_text.setVisibility(View.GONE);
		}
		if (!mQstatus.getIsVisible()) holder.source_ll.setVisibility(View.GONE);
		else
			holder.source_ll.setVisibility(View.VISIBLE);
		

		return convertView;
	}


	static class ViewHolder {
		ImageView head;			// 头像
		TextView nick;			// 用户名
		TextView origText;		// 微博文本
		ImageView image;		// 微博图片
		TextView from;			// 平台来源
		ImageView isVip;
		TextView mcount;
		TextView count;
		TextView timeStamp;
		// --------转发微博-----------------
		TextView source_text;	// 转发微博文字
		ImageView source_image;	// 转发微博图片
		View source_ll;			// 转发微博父容器
	}
	
	private View.OnClickListener faceClickListener = new View.OnClickListener(){
		public void onClick(View v) {
			QStatus mStatus = (QStatus)v.getTag();
			//UIHelper.showUserCenter(v.getContext(), mStatus.getAuthorId(), mStatus.getAuthor());
		}
	};
	
	private View.OnClickListener imageClickListener = new View.OnClickListener(){
		public void onClick(View v) {
			UIHelper.showImageDialog(v.getContext(), (String)v.getTag(), null);
		}
	};
}
