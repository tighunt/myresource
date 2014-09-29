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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.logan.R;
import com.logan.weibo.bean.BaseActivity;
import com.logan.weibo.bean.QStatus;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class FansListAdapter  extends BaseAdapter{
        private LayoutInflater mInflater;
        private List<QStatus> list;
        private ViewHolder holder;
        private Context ctx; 
        // Universal Image Loader for Android 第三方框架组件
        protected ImageLoader imageLoader = ImageLoader.getInstance();
        private DisplayImageOptions options;
        
        public FansListAdapter(Context context, List<QStatus> list) {
                super();
                this.ctx = context;
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

        private final int OPENID = 211;
        private final int ISIDOL = 212;
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                final QStatus mQstatus = list.get(position);
                if (convertView == null) {
                        holder = new ViewHolder();
                        convertView = mInflater.inflate(R.layout.user_fanslist_item, null);
                        holder.head = (ImageView) convertView.findViewById(R.id.user_fanlist_item_avatar);
                        holder.nick = (TextView) convertView.findViewById(R.id.user_fanlist_item_nick);
                        holder.followBtn = (Button) convertView.findViewById(R.id.following);
                        holder.followBtn.setTag(R.string.openid, mQstatus.getId());
                        holder.followBtn.setTag(R.string.isidol, mQstatus.getIsidol());
                        convertView.setTag(holder);
                } else {
                        holder = (ViewHolder) convertView.getTag();
                }
                holder.nick.setText(mQstatus.getNick());
                if (!mQstatus.getHead().equals("") && mQstatus.getHead() != null) {
                        imageLoader.displayImage(mQstatus.getHead(), holder.head, options);
                }
                if(mQstatus.getIsidol()) holder.followBtn.setText("取消关注"); 
                else 
                        holder.followBtn.setText("关注");
                
                holder.followBtn.setOnClickListener(new OnClickListener() {
                        
                        @Override
                        public void onClick(View v) {
                                if (!(Boolean) v.getTag(R.string.isidol)) {
                                        //不是关注对象的时候
                                        BaseActivity.addIdol(BaseActivity.getInstance(), (String) v.getTag(R.string.openid));
                                        mQstatus.setIsidol(true);
                                        holder.followBtn.setText("取消关注"); 
                                        Toast.makeText(ctx, "已关注！", Toast.LENGTH_SHORT).show();
                                } else {
                                        //是关注对象的时候
                                        BaseActivity.delIdol(BaseActivity.getInstance(), (String) v.getTag(R.string.openid));
                                        mQstatus.setIsidol(false);
                                        holder.followBtn.setText("关注");
                                        Toast.makeText(ctx, "已取消！", Toast.LENGTH_SHORT).show();
                                }
                                notifyDataSetChanged();
                        }
                });
                return convertView;
        }
        
        static class ViewHolder {
                ImageView head;                 // 头像
                TextView nick;                  // 用户名
                Button followBtn;
        }
}