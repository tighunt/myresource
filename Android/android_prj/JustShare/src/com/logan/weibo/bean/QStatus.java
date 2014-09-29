
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
package com.logan.weibo.bean;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.logan.util.TimeUtil;

/**
 * 腾讯微博实体类
 * @author Logan <a href="https://github.com/Logan676/JustSharePro"/>
 *   
 * @version 1.0 
 *  
 */
public class QStatus implements Serializable {

        private static final long serialVersionUID = 1L;

        private final static String TAG = "QStatus";

        public String id = "";
        public String head = "";
        private String nick = "";
        private String origText = "";
        // String text = "";// 适用于转发微博
        private String image = "";// 微博图片
        private String mediun_image = "";// 微博图片
        private String large_image = "";// 微博图片
        private String from = "";
        private int isVip = 0;
        private String mcount = "";
        private String count = "";
        private String created_at = "";
        private String pageTime = "";
        private Boolean isidol = false;
        
        // --转发微博---
        private QSource source = null;
        // String source_nick = "";
        private Boolean isVisible = true;


        public String getId() {
                return id;
        }

        public void setId(String id) {
                this.id = id;
        }
        
        public String getPageTime() {
                return pageTime;
        }

        public void setPageTime(String pageTime) {
                this.pageTime = pageTime;
        }
        public String getHead() {
                return head;
        }

        public void setHead(String head) {
                this.head = head;
        }

        public String getNick() {
                return nick;
        }

        public void setNick(String nick) {
                this.nick = nick;
        }

        public String getOrigText() {
                return origText;
        }

        public void setOrigText(String origText) {
                this.origText = origText;
        }

        public String getImage() {
                return image;
        }

        public void setImage(String image) {
                this.image = image;
        }

        public String getMediun_image() {
                return mediun_image;
        }

        public void setMediun_image(String mediun_image) {
                this.mediun_image = mediun_image;
        }

        public String getLarge_image() {
                return large_image;
        }

        public void setLarge_image(String large_image) {
                this.large_image = large_image;
        }
        
        public String getFrom() {
                return from;
        }

        public void setFrom(String from) {
                this.from = from;
        }

        public int getIsVip() {
                return isVip;
        }

        public void setIsVip(int isVip) {
                this.isVip = isVip;
        }

        public String getMcount() {
                return mcount;
        }

        public void setMcount(String mcount) {
                this.mcount = mcount;
        }

        public String getCount() {
                return count;
        }

        public void setCount(String count) {
                this.count = count;
        }

        public String getCreated_at() {
                return created_at;
        }

        public void setCreated_at(String created_at) {
                this.created_at = created_at;
        }

        public QSource getSource() {
                return source;
        }
        
        public Boolean getIsidol() {
                return isidol;
        }

        public void setIsidol(Boolean isidol) {
                this.isidol = isidol;
        }

        public void setSource(QSource source) {
                this.source = source;
        }

        

        private QStatus()  {
                //TODO sth
        }

        public static QStatus getQStatus(JSONObject json) throws JSONException {
                QStatus mQStatus = new QStatus();
                mQStatus.head = json.getString("head") + "/100";
                mQStatus.id = json.getString("id");
                mQStatus.nick = json.getString("nick");
                mQStatus.origText = json.getString("origtext");
                
                JSONArray imageArray = json.optJSONArray("image");// 如果此微博有图片内容，就显示出来
                if (imageArray != null && imageArray.length() > 0) {
                                mQStatus.image = imageArray.optString(0) + "/160";
                                mQStatus.mediun_image = imageArray.optString(0) + "/460";
                                mQStatus.large_image = imageArray.optString(0) + "/2000";
                }
                // /120 /160 /460 /2000返回相应大小的图片
                        
                mQStatus.from = json.getString("from");// 不是超链接的数据，纯文本
                mQStatus.isVip = json.getInt("isvip");
                mQStatus.mcount = json.getString("mcount");
                mQStatus.count = json.getString("count");
                mQStatus.pageTime = json.getString("timestamp");
                mQStatus.created_at = TimeUtil.converTime(Long.parseLong(json.getString("timestamp")));

                // --------------获取转发微博的信息----包括图片、文字信息，不包含视频、音频信息------------------------
                
                mQStatus.source = QSource.getQSource(json);
                if(mQStatus.source == null) mQStatus.isVisible = false;
                

//              Log.v(TAG, "id:  " + mQStatus.id);
//              Log.v(TAG, "head:  " + mQStatus.head);
//              Log.v(TAG, "nick: " + mQStatus.nick);
//              Log.v(TAG, "origText:  " + mQStatus.origText);
//              Log.v(TAG, "image:  " +mQStatus.image);
//              Log.v(TAG, "from:  " +mQStatus.from);
//              Log.v(TAG, "isVip:  " + mQStatus.isVip);
//              Log.v(TAG, "mcount:  " + mQStatus.mcount);
//              Log.v(TAG, "count:  " +mQStatus.count);
                
                return mQStatus;
        }
        
        public static QStatus getQFan(JSONObject json) throws JSONException {
                QStatus mQStatus = new QStatus();
                mQStatus.head = json.getString("head") + "/100";
                mQStatus.id = json.getString("openid");
                mQStatus.nick = json.getString("nick");
                mQStatus.isidol = json.getBoolean("isidol");
                return mQStatus;
        }
        public Boolean getIsVisible() {
                return isVisible;
        }

        public void setIsVisible(Boolean isVisible) {
                this.isVisible = isVisible;
        }

        @Override
        public String toString(){
                
                return "id:"+id+", nick:"+nick+", text:"+origText+", from:"+from+", created_at:"+created_at; 
        }
        
}