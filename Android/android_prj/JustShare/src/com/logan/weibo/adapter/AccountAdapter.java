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

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.logan.R;
import com.logan.util.DBManager;
import com.logan.weibo.bean.Account;
import com.logan.weibo.bean.BaseActivity;
import com.logan.weibo.ui.TimeLineActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.weibo.net.AccessToken;
import com.weibo.net.Oauth2AccessTokenHeader;
import com.weibo.net.Utility;
import com.weibo.net.Weibo;
/**
 * 账号列表适配器
 * @author Logan <a href="https://github.com/Logan676/JustSharePro"/>
 *   
 * @version 1.0 
 *  
 */
public class AccountAdapter extends BaseAdapter {

	
	private final String TAG = "AccountAdapter";

	//private AsyncImageLoader imageLoader = new AsyncImageLoader();
	private ArrayList<Account> mData;
	private ViewHolder holder;
	private Context ctx;
	private ArrayList<Boolean> deleteData ;
	private DBManager mDBManager ;
	
	//Universal Image Loader for Android 第三方框架组件
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	
	public AccountAdapter(Context ctx, ArrayList<Account> data) {
		this.ctx = ctx;
		this.mData = data;
		int size = data.size();
		this.deleteData = new ArrayList<Boolean>(size);
		mDBManager = new DBManager(ctx.getApplicationContext());
		//Log.v(TAG,  "Accounts size is "+ size);
		for (int i = 0; i < size; i++) {
			deleteData.add(i, false);
		}
		
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.loading)
		.showImageForEmptyUri(R.drawable.icon)
		.cacheInMemory()
		.cacheOnDisc()
		.displayer(new RoundedBitmapDisplayer(10))
		.build();
	}

	@Override
	public int getCount() {
		return this.mData != null ? this.mData.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Account mAccount = mData.get(position);
		String profile_image_url = mAccount.getUrl() + "/100";
		// Log.v(TAG, "profile_image_url:  " + profile_image_url);
		String name = mAccount.getName();
		// Log.v(TAG, "name:  " + name);
		String plf = mAccount.getPlf();

		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater mInflater = LayoutInflater.from(ctx);
			convertView = mInflater.inflate(R.layout.account_lv_item, null);
			holder.name = (TextView) convertView.findViewById(R.id.account_tv);
			holder.profile_image = (ImageView) convertView.findViewById(R.id.account_iv);
			holder.plfIcon = (ImageView) convertView.findViewById(R.id.weibo_plf_icon);
			holder.cb = (CheckBox) convertView.findViewById(R.id.cb);
			holder.ll = (LinearLayout) convertView.findViewById(R.id.account_rl);
			//holder.progressBar = (ProgressBar) convertView.findViewById(R.id.tweet_progressbar);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (!profile_image_url.equals("")) {
			//setViewImage(holder.profile_image, profile_image_url);
			imageLoader.displayImage(profile_image_url, holder.profile_image, options);
		} else holder.profile_image.setImageResource(R.drawable.icon);

		if (!name.equals(""))
			holder.name.setText(name);
		else
			holder.name.setText("空值");
		if (plf.equals("sina"))
			holder.plfIcon.setImageResource(R.drawable.sina);
		else if (plf.equals("tencent")) {
			holder.plfIcon.setImageResource(R.drawable.tencent);
		}
		holder.cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Log.v(TAG, "checkBox selected: "+isChecked+" and position is:  "+ position);
				deleteData.set(position, isChecked);
			}
		});
		
		Integer[] mSelector = { android.R.color.transparent,R.drawable.listview_item_selector_bg, R.drawable.listview_item_selector_bg };
		ModifiedLinearLayout mLL = new ModifiedLinearLayout(ctx);
		holder.ll.setBackgroundDrawable(mLL.setbg(mSelector));
		holder.ll.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Log.v(TAG, "relativeLayout "+position +" is clicked! ");
				final String token = mAccount.getToken();
				final String expires_in = mAccount.getExpires_in();
				final String plf = mAccount.getPlf();
				if (plf.equals("sina")) {
					BaseActivity.isSina = true;
					Weibo.getInstance().setupConsumerConfig(BaseActivity.CONSUMER_KEY, BaseActivity.CONSUMER_SECRET);
					Weibo.getInstance().setRedirectUrl(BaseActivity.REDIRECT_URL);
					AccessToken accessToken = new AccessToken(token, BaseActivity.CONSUMER_SECRET);
					accessToken.setExpiresIn(expires_in);
					// 这一句很重要，因为会爆出WeiboException: auth faild! 21301
					Utility.setAuthorization(new Oauth2AccessTokenHeader());
					Weibo.getInstance().setAccessToken(accessToken);
					Log.v(TAG, "sina:Clicked!");
					Log.v(TAG, String.valueOf(new Date(System.currentTimeMillis())));
					Intent i = new Intent();
					i.putExtra("isSina", BaseActivity.isSina);
					i.putExtra("isTencent", BaseActivity.isTencent);
					i.putExtra("currentTag", 0);
					i.setClass(ctx.getApplicationContext(), TimeLineActivity.class);
					ctx.startActivity(i);

				} else if (plf.equals("tencent")) {
					BaseActivity.isTencent = true;
					Log.v(TAG, "tencent:Clicked!");
					Log.v(TAG, String.valueOf(new Date(System.currentTimeMillis())));
					String openid = mAccount.getOpenid();
					String openkey = mAccount.getOpenkey();
					BaseActivity.getInstance().setAccessToken(token);
					BaseActivity.getInstance().setExpiresIn(expires_in);
					BaseActivity.getInstance().setOpenid(openid);
					BaseActivity.getInstance().setOpenkey(openkey);
					Intent i = new Intent();
					i.putExtra("isSina", BaseActivity.isSina);
					i.putExtra("isTencent", BaseActivity.isTencent);
					i.putExtra("currentTag", 0);
					i.setClass(ctx.getApplicationContext(), TimeLineActivity.class);
					ctx.startActivity(i);
				}
			}
		});
		return convertView;
	}

	@Override
	public void notifyDataSetChanged() {
		int size = deleteData.size();
		Boolean isShow = true ;
		for (int i = 0; i < size; i++) {
			Boolean deletable = deleteData.get(i);
			if (deletable) {
				Account account = mData.get(i);
				mDBManager.deleteAccount(account);
				mData.remove(i);
				isShow = false ; 
			}
		}
		if (isShow)Toast.makeText(ctx, "未选中任何选项", Toast.LENGTH_SHORT).show();
		super.notifyDataSetChanged();
	}
	
	static class ViewHolder {
		private ImageView profile_image;
		private TextView name;
		private ImageView plfIcon = null;// 微博平台
		private CheckBox cb = null;
		private LinearLayout ll = null;
		private View progressBar;
	}

    class ModifiedLinearLayout extends View {
        public ModifiedLinearLayout(Context context) {
            super(context);
        }
        // 以下这个方法也可以把你的图片数组传过来，以StateListDrawable来设置图片状态，来表现button的各中状态。未选中，按下，选中效果。
        public StateListDrawable setbg(Integer[] mImageIds) {
            StateListDrawable bg = new StateListDrawable();
            Drawable normal = this.getResources().getDrawable(mImageIds[0]);
            Drawable selected = this.getResources().getDrawable(mImageIds[1]);
            Drawable pressed = this.getResources().getDrawable(mImageIds[2]);
            bg.addState(View.PRESSED_ENABLED_STATE_SET, pressed);
            bg.addState(View.ENABLED_FOCUSED_STATE_SET, selected);
            bg.addState(View.ENABLED_STATE_SET, normal);
            bg.addState(View.FOCUSED_STATE_SET, selected);
            bg.addState(View.EMPTY_STATE_SET, normal);
            return bg;
        }
    }
}
