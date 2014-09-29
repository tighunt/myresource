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
import com.logan.weibo.bean.QSource;
import com.logan.weibo.bean.QStatus;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class RetweetListAdapter extends BaseAdapter{
	private LayoutInflater mInflater;
	private List<QStatus> list;
	private ViewHolder holder;

	// Universal Image Loader for Android 第三方框架组件
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	
	public RetweetListAdapter(Context context, List<QStatus> list) {
		super();
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
			convertView = mInflater.inflate(R.layout.weibo_retweetlist_item, null);
			holder.nick = (TextView) convertView.findViewById(R.id.item_screen_name);
			holder.head = (ImageView) convertView.findViewById(R.id.item_profile_image);

			holder.origText = (TextView) convertView.findViewById(R.id.item_text);
			holder.from = (TextView) convertView.findViewById(R.id.item_from);

			holder.mcount = (TextView) convertView.findViewById(R.id.item_tweet_statuses_count);
			holder.count = (TextView) convertView.findViewById(R.id.item_tweet_followers_count);
			holder.timeStamp = (TextView) convertView.findViewById(R.id.item_created_at);

			// ---------------转发微博---------------------
			holder.source_text = (TextView) convertView.findViewById(R.id.item_retweeted_status_text);
			holder.source_ll = convertView.findViewById(R.id.item_retweeted_status_ll);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.nick.setText(mQstatus.getNick());
		holder.origText.setText(mQstatus.getOrigText());

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

		// ------------------------转发微博----------------------------------
		QSource mQSource = mQstatus.getSource();
		if(mQSource == null) mQstatus.setIsVisible(false); else{
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
		TextView from;			// 平台来源
		//ImageView isVip;
		TextView mcount;
		TextView count;
		TextView timeStamp;
		// --------转发微博-----------------
		TextView source_text;	// 转发微博文字
		View source_ll;			// 转发微博父容器
	}
}
