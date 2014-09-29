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

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.logan.util.DBManager;
import com.tencent.weibo.api.FriendsAPI;
import com.tencent.weibo.api.StatusesAPI;
import com.tencent.weibo.api.TAPI;
import com.tencent.weibo.api.UserAPI;
import com.tencent.weibo.beans.OAuth;
import com.tencent.weibo.constants.OAuthConstants;
import com.tencent.weibo.oauthv2.OAuthV2;
import com.tencent.weibo.utils.QArrayList;
import com.weibo.net.AccessToken;
import com.weibo.net.AsyncWeiboRunner;
import com.weibo.net.AsyncWeiboRunner.RequestListener;
import com.weibo.net.Utility;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboException;
import com.weibo.net.WeiboParameters;
/**
 * 基类
 * @author Logan <a href="https://github.com/Logan676/JustSharePro"/>
 *   
 * @version 1.0 
 *  
 */
public abstract class BaseActivity extends Activity implements RequestListener {


        // ----------------------公用参数--------------------------------//
        private final String TAG = "BaseActivity";
        public static Boolean isProtected = false;
        public static Boolean isSina = false;// 标识符
        public static Boolean isTencent = false;
        public DBManager mDBManager;
        public String token = "";// 这里token即代表Sina中的token，也代表Tencent中的AccessToken
        public String expires_in = "";
        public String plf = "";
        public final String WHOAMI = "who_am_i";  
        
        // ----------------------新浪API接口所需的参数-----------------------------//
        public static final String TOKEN = "com.logan.weibo.token";
        public static final String EXPIRE = "com.logan.weibo.expire";
        public AccessToken accessToken = null;
        public Weibo mWeibo = Weibo.getInstance();// Sina API 实例
        public static final String CONSUMER_KEY = "4188464852";
        public static final String CONSUMER_SECRET = "cf1e022b33ecee613f1c4ec5104a6586";
        // 此处回调页内容应该替换为与appkey对应的应用回调页
        // 应用回调页不可为空
        public static final String REDIRECT_URL = "https://github.com/Logan676/JustSharePro";
        
        
        // ----------------------腾讯API接口所需的参数-----------------------------//
        public static OAuthV2 oAuth = null;// Tencent API 实例
        public String openid = "";
        public String openkey = "";
        // public static final String TENCENT_REDIRECT_URL = "http://www.myapp.com/downcenter/a/733066?g_f=990935";
        public static final String TENCENT_REDIRECT_URL = "http://www.mumayi.com/android-275488.html?1361782163";
        public static final String CLIENT_ID = "801218195";
        public static final String CLIENT_SECRET = "897a59f506ef91de34e8cec2d0ff90d0";
        public static final String FORMAT = "json";
        // 拉取类型 0x1 原创发表 0x2 转载 0x8 回复 0x10 空回 0x20 提及 0x40 点评
        // 如需拉取多个类型请使用|，如(0x1|0x2)得到3，此时type=3即可，填零表示拉取所有类型
        public final String TYPE = "0";
        // 内容过滤。0-表示所有类型，1-带文本，2-带链接，4-带图片，8-带视频，0x10-带音频
        public final String CONTENTTYPE = "0";
        public StatusesAPI mStatusesAPI = null;
        public static FriendsAPI mFriendAPI = null;
        public UserAPI userAPI;
        public TAPI mTAPI;

        public synchronized static OAuthV2 getInstance() {
                if (oAuth == null) {
                        oAuth = new OAuthV2(CLIENT_ID, CLIENT_SECRET, TENCENT_REDIRECT_URL);
                }
                return oAuth;
        }
        
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                oAuth = BaseActivity.getInstance();
                mDBManager = new DBManager(getApplicationContext());
                userAPI = new UserAPI(OAuthConstants.OAUTH_VERSION_2_A);
                mFriendAPI = new FriendsAPI(OAuthConstants.OAUTH_VERSION_2_A);
                mTAPI = new TAPI(OAuthConstants.OAUTH_VERSION_2_A);
                mStatusesAPI = new StatusesAPI(OAuthConstants.OAUTH_VERSION_2_A);

        }

        @Override
        protected void onDestroy() {
                mDBManager.closeDB();// this mDBManager serves for sina and tencent
                mTAPI.shutdownConnection();// this mTAPI just serves for tencent
                super.onDestroy();
        }

        @Override
        protected void onPause() {
                // TODO Auto-generated method stub
                super.onPause();
        }
        

        @Override
        protected void onRestart() {
                // TODO Auto-generated method stub
                super.onRestart();
        }

        @Override
        protected void onResume() {
                // TODO Auto-generated method stub
                super.onResume();
        }

        @Override
        protected void onStart() {
                // TODO Auto-generated method stub
                super.onStart();
        }

        @Override
        protected void onStop() {
                // TODO Auto-generated method stub

                super.onStop();
        }

        public abstract int getLayout();
        
        
        // ---------- 新浪 API接口 --------------------------//
        /**
         * Sina 
         * 获取当前登录用户及其所关注用户的最新微博
         * 
         * @param since_id 返回ID比since_id大的微博（即比since_id时间晚的微博），默认为0
         * @param pageSize 单页返回的记录条数，最大不超过100，默认为20
         * @return String
         * @throws MalformedURLException
         * @throws IOException
         * @throws WeiboException
         */
        public String getFriendTimeline(String pageIndex, String pageSize) throws MalformedURLException, IOException, WeiboException {
                String url = Weibo.SERVER + "statuses/friends_timeline.json";
                WeiboParameters bundle = new WeiboParameters();
                bundle.add("source", CONSUMER_KEY);
                //bundle.add("since_id", since_id);
                bundle.add("count", pageSize);
                bundle.add("page", pageIndex);
                String rlt;
                try {
                        rlt = mWeibo.request(this, url, bundle, "GET", mWeibo.getAccessToken());
                        return rlt;
                } catch (com.weibo.net.WeiboException e) {
                        e.printStackTrace();
                        return null;
                }
        }

        /**
         * Sina 
         * 返回最新的200条公共微博，返回结果非完全实时
         * 
         * @param pageSize 单页返回的记录条数，最大不超过200，默认为20。
         * @return
         * @throws MalformedURLException
         * @throws IOException
         * @throws WeiboException
         */
        public String getPublicTimeLine(String pageIndex, String pageSize) throws MalformedURLException, IOException, WeiboException {
                String url = Weibo.SERVER + "statuses/public_timeline.json";
                WeiboParameters bundle = new WeiboParameters();
                bundle.add("source", CONSUMER_KEY);
                bundle.add("page", pageIndex);
                bundle.add("count", pageSize);
                String rlt;
                try {
                        rlt = mWeibo.request(this, url, bundle, "GET", mWeibo.getAccessToken());
                        return rlt;
                } catch (com.weibo.net.WeiboException e) {
                        e.printStackTrace();
                        return null;
                }
        }

        /**
         * Sina 
         * 获取登录用户的ID
         * 
         * @return
         * @throws MalformedURLException
         * @throws IOException
         * @throws WeiboException
         */
        public String getUID() throws MalformedURLException, IOException, WeiboException {
                String id = "";
                String url = Weibo.SERVER + "account/get_uid.json";
                WeiboParameters bundle = new WeiboParameters();
                bundle.add("source", CONSUMER_KEY);

                Log.v(TAG, "url:    " + url);
                Log.v(TAG, "CONSUMER_KEY:    " + CONSUMER_KEY);
                Log.v(TAG, "accessToken:    " + accessToken);
                String rlt = null;
                try {
                        rlt = mWeibo.request(this, url, bundle, "GET", Weibo.getInstance().getAccessToken());
                } catch (com.weibo.net.WeiboException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                }

                try {

                        JSONObject js;
                        js = new JSONObject(rlt);
                        id = js.getString("uid");
                        Log.v(TAG, "id:    " + id);
                } catch (JSONException e) {
                        e.printStackTrace();
                }

                return id;
        }

        /**
         * Sina 
         * 根据用户ID获取用户信息
         * 
         * @return
         * @throws MalformedURLException
         * @throws IOException
         * @throws WeiboException
         */
        public String getUserInfo() throws MalformedURLException, IOException, WeiboException {
                String url = Weibo.SERVER + "users/show.json";
                WeiboParameters bundle = new WeiboParameters();
                bundle.add("source", CONSUMER_KEY);
                bundle.add("uid", getUID());
                String rlt;
                try {
                        rlt = mWeibo.request(this, url, bundle, "GET", mWeibo.getAccessToken());
                        return rlt;
                } catch (com.weibo.net.WeiboException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        return null;
                }
        }

        /**
         * Sina 
         * 获取某个用户最新发表的微博列表
         * 
         * @param pageIndex 返回结果的页码，默认为1
         * @param pageSize 单页返回的记录条数，最大不超过100，默认为20。
         * @return
         * @throws MalformedURLException
         * @throws IOException
         * @throws WeiboException
         */
        public String getUserTimeline(String pageIndex, String pageSize) throws MalformedURLException, IOException, WeiboException {
                String url = Weibo.SERVER + "statuses/user_timeline.json";
                WeiboParameters bundle = new WeiboParameters();
                bundle.add("source", CONSUMER_KEY);     //数值为应用的AppKey
                bundle.add("uid", getUID());            //需要查询的用户ID
                bundle.add("count", pageSize);          //单页返回的记录条数，最大不超过100，默认为20
                bundle.add("page", pageIndex);
                String rlt;
                try {
                        rlt = mWeibo.request(this, url, bundle, "GET", mWeibo.getAccessToken());
                        return rlt;
                } catch (com.weibo.net.WeiboException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                return null;
        }
        
        /**
         * Sina
         * 发布一条微博 
         * @param status
         * @param lon
         * @param lat
         * @return
         * @throws MalformedURLException
         * @throws IOException
         * @throws WeiboException
         */
        public String upload2Sina(String status, String lon, String lat) throws MalformedURLException, IOException, WeiboException {
                WeiboParameters bundle = new WeiboParameters();
                bundle.add("source", BaseActivity.CONSUMER_KEY);
                bundle.add("status", status);
                if (!TextUtils.isEmpty(lon)) {
                        bundle.add("lon", lon);
                }
                if (!TextUtils.isEmpty(lat)) {
                        bundle.add("lat", lat);
                }
                String rlt = "";
                String url = Weibo.SERVER + "statuses/update.json";
                AsyncWeiboRunner weiboRunner = new AsyncWeiboRunner(mWeibo);
                weiboRunner.request(BaseActivity.this, url, bundle, Utility.HTTPMETHOD_POST, this);
                return rlt;
        }
        /**
         * 上传一条带图片的微博
         * @param source
         * @param picPath
         * @param status
         * @param lon
         * @param lat
         * @return
         * @throws WeiboException
         */
        public String uploadPic2Sina(String picPath, String status, String lon, String lat) throws WeiboException {
        WeiboParameters bundle = new WeiboParameters();
        bundle.add("source", BaseActivity.CONSUMER_KEY);
        bundle.add("pic", picPath);
        bundle.add("status", status);
        if (!TextUtils.isEmpty(lon)) {
            bundle.add("lon", lon);
        }
        if (!TextUtils.isEmpty(lat)) {
            bundle.add("lat", lat);
        }
        String rlt = "";
        String url = Weibo.SERVER + "statuses/upload.json";
        AsyncWeiboRunner weiboRunner = new AsyncWeiboRunner(mWeibo);
        weiboRunner.request(this, url, bundle, Utility.HTTPMETHOD_POST, this);
        
        return rlt;
    }
        
        
        // ---------腾讯  API接口 -----------------------//
        /**
         * Tencent
         * 获取主页时间线微博信息
         * 
         * @param pageflag 分页标识（0：第一页，1：向下翻页，2向上翻页）
         * @param pagetime 本页起始时间（第一页：填0，向上翻页：填上一次请求返回的第一条记录时间，向下翻页：填上一次请求返回的最后一条记录时间） 
         * @return
         * @throws Exception
     * @see <a href="http://wiki.open.t.qq.com/index.php/%E6%97%B6%E9%97%B4%E7%BA%BF/%E4%B8%BB%E9%A1%B5%E6%97%B6%E9%97%B4%E7%BA%BF">腾讯微博开放平台上关于此条API的文档</a>
         */
        public String getQFriendTimeline(String pageFlag, String pageTime, String pageSize) {
                String rlt = "";
                try {
                        rlt = mStatusesAPI.homeTimeline(oAuth, FORMAT, pageFlag, pageTime, pageSize, TYPE, CONTENTTYPE);
                        //Log.v(TAG, "getQFriendTimeline:" + rlt);
                        return rlt;
                } catch (Exception e1) {
                        e1.printStackTrace();
                        //Log.v(TAG, "getQFriendTimeline2:" + rlt);
                        return null;
                }
        
        }

        /**
         * Tencent
         * 获取用户提及时间线微博信息
         * 
         * @param pageflag 分页标识（0：第一页，1：向下翻页，2向上翻页）
         * @param pagetime 本页起始时间（第一页：填0，向上翻页：填上一次请求返回的第一条记录时间，向下翻页：填上一次请求返回的最后一条记录时间） 
         * @param lastid 和pagetime配合使用（第一页：填0，向上翻页：填上一次请求返回的第一条记录id，向下翻页：填上一次请求返回的最后一条记录id） 
         * @return
         * @throws Exception
     * @see <a href="http://wiki.open.t.qq.com/index.php/%E6%97%B6%E9%97%B4%E7%BA%BF/%E7%94%A8%E6%88%B7%E6%8F%90%E5%8F%8A%E6%97%B6%E9%97%B4%E7%BA%BF">腾讯微博开放平台上关于此条API的文档</a>
                 */
        public String getQMentionsTimeLine(String pageFlag, String pageTime, String lastId, String pageSize) {
                String rlt = "";
                try {
                        rlt = mStatusesAPI.mentionsTimeline(oAuth, FORMAT, pageFlag, pageTime, pageSize, lastId, TYPE, CONTENTTYPE);
                } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                return rlt;
        }

        /**
         * Tencent
         * 获取自己的资料
         * 
         * @param oAuth
         * @return
         */
        public String getQuserInfo(OAuthV2 oAuth) {
                String rlt = "";
                try {
                        rlt = userAPI.info(oAuth, FORMAT);
                } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }

                return rlt;

        }
                
        /**
         * Tencent
         * 获取我发表时间线信息
         * 
         * @param pageflag 分页标识（0：第一页，1：向下翻页，2向上翻页）
         * @param pagetime 本页起始时间（第一页：填0，向上翻页：填上一次请求返回的第一条记录时间，向下翻页：填上一次请求返回的最后一条记录时间） 
         * @param lastid 和pagetime配合使用（第一页：填0，向上翻页：填上一次请求返回的第一条记录id，向下翻页：填上一次请求返回的最后一条记录id） 
         * @return
         * @throws Exception
         * @see <a href="http://wiki.open.t.qq.com/index.php/%E6%97%B6%E9%97%B4%E7%BA%BF/%E6%88%91%E5%8F%91%E8%A1%A8%E6%97%B6%E9%97%B4%E7%BA%BF">腾讯微博开放平台上关于此条API的文档</a>
         */
        public String getQUserTimeLine(String pageFlag, String pageTime, String lastId, String pageSize) {
                String rlt = "";
                try {
                        rlt = mStatusesAPI.broadcastTimeline(oAuth, FORMAT, pageFlag, pageTime, pageSize, lastId, TYPE, CONTENTTYPE);
                } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                return rlt;
        }
        
        /**
         * Tencent
         * 发表一条微博
         * 
         * @param oAuth
         * @param content  微博内容
         * @param clientip 用户IP(以分析用户所在地)
         * @return
         * @throws Exception
         * @see <a href="http://wiki.open.t.qq.com/index.php/%E5%BE%AE%E5%8D%9A%E7%9B%B8%E5%85%B3/%E5%8F%91%E8%A1%A8%E4%B8%80%E6%9D%A1%E5%BE%AE%E5%8D%9A">腾讯微博开放平台上关于此条API的文档</a>
         */
        public String update(OAuthV2 oAuth, String content, String clientip) throws Exception {
                
                String response = mTAPI.add(oAuth, FORMAT, content, clientip);
                return response;
        }
        
        /**
         * 发表一条带图片的微博
         * @param oAuth
         * @param content 微博内容
         * @param clientip 用户IP(以分析用户所在地)
         * @param picpath 可以是本地图片路径 或 网络地址
         * @return 
         * @throws Exception
         * See Also:
         * 腾讯微博开放平台上关于此条API的文档1-本地图片
         * 腾讯微博开放平台上关于此条API的文档2-网络图片
         */
        public String updatePic(OAuthV2 oAuth, String content, String clientip, String picpath) throws Exception {

                String response = mTAPI.addPic(oAuth, FORMAT, content, clientip, picpath);
                return response;
        }
        /**
         * 获取单条微博的转播理由/点评列表
         * @param oAuth
         * @param flag 标识。0－转播列表 1－点评列表 2－点评与转播列表       
         * @param rootid 转发或回复的微博根结点id（源微博id）
         * @param pageflag 分页标识（0：第一页，1：向下翻页，2：向上翻页）
         * @param pagetime 本页起始时间（第一页：填0，向上翻页：填上一次请求返回的第一条记录时间，向下翻页：填上一次请求返回的最后一条记录时间）
         * @param reqnum 每次请求记录的条数（1-100条）
         * @param twitterid 翻页用，第1-100条填0，继续向下翻页，填上一次请求返回的最后一条记录id
         * @return
         */
        public String reList(OAuthV2 oAuth, String flag, String rootid, String pageflag, String pagetime, String reqnum, String twitterid){
                try {
                        return mTAPI.reList(oAuth, FORMAT, flag, rootid, pageflag, pagetime, reqnum, twitterid);
                } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                }
        }
        /**
         * 转播一条微博
         * @param content 微博内容
         * @param clientip
         * @param reid  转播父结点微博id
         * @return
         */
        public String reAdd(OAuth oAuth, String content, String clientip, String reid){
                try {
                        return mTAPI.reAdd(oAuth, FORMAT, content, clientip, reid);
                } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                }
        }
        /**
         * 点评一条微博
         * @param oAuth
         * @param content
         * @param clientip
         * @param reid 点评父结点微博id
         * @return
         */
        public String comment(OAuth oAuth, String content, String clientip, String reid){
                try {
                        return mTAPI.comment(oAuth, FORMAT, content, clientip, reid);
                } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                }
        }
        /**
         * 我的听众列表
         * @param oAuth
         * @param reqnum
         * @param startindex 起始位置（第一页填0，继续向下翻页：填：【reqnum*（page-1）】）
         * @param mode 获取模式，默认为0；mode=0，旧模式，新粉丝在前，只能拉取1000个 ；mode=1，新模式，拉取全量粉丝，老粉丝在前 
         * @param install 过滤安装应用好友（可选） 0-不考虑该参数，1-获取已安装应用好友，2-获取未安装应用好友 
         * @return
         */
        public String fanslist(OAuth oAuth, String reqnum, String startindex, String mode, String install){
                try {
                        return mFriendAPI.fanslist(oAuth, FORMAT, reqnum, startindex, mode, install);
                } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                }
                
        }
        /**
         * 收听某个用户
         * @param oAuth
         * @param fopenids 你需要读取的用户openid列表，用下划线“_”隔开，例如：B624064BA065E01CB73F835017FE96FA_B624064BA065E01CB73F835017FE96FB（可选，最多30个）
         * @return
         */
        public static String addIdol(OAuth oAuth, String fopenids){
                try {
                        //name和fopenids至少选一个，若同时存在则以name值为主
                        return mFriendAPI.add(oAuth, FORMAT, "", fopenids);
                } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                }
        }
        /**
         * 取消收听某个用户
         * @param oAuth
         * @param fopenids 他人的openid（可选）  name和fopenid至少选一个，若同时存在则以name值为主 
         * @return
         */
        public static String delIdol(OAuth oAuth, String fopenids){
                try {
                        //name和fopenids至少选一个，若同时存在则以name值为主
                        return mFriendAPI.del(oAuth, FORMAT, "", fopenids);
                } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                }
        }
        
        /**
    	 * 我收听的人列表
    	 * 
    	 * @param oAuth 标准参数
    	 * @param reqnum  请求个数(1-30)
    	 * @param startindex 起始位置（第一页填0，继续向下翻页：填：【reqnum*（page-1）】）
         * @param install  过滤安装应用好友（可选） <br>
         *                           0-不考虑该参数，1-获取已安装应用好友，2-获取未安装应用好友 
    	 * @return
    	 * @throws Exception
         * @see <a href="http://wiki.open.t.qq.com/index.php/%E5%B8%90%E6%88%B7%E7%9B%B8%E5%85%B3/%E6%88%91%E6%94%B6%E5%90%AC%E7%9A%84%E4%BA%BA%E5%88%97%E8%A1%A8">腾讯微博开放平台上关于此条API的文档</a>
    	 */
    	public static String idollist(OAuth oAuth, String reqnum, String startindex, String install) {
    		try {
                //name和fopenids至少选一个，若同时存在则以name值为主
                return mFriendAPI.idollist(oAuth, FORMAT, reqnum, startindex, install);
    		} catch (Exception e) {
                e.printStackTrace();
                return null;
    		}
    	}
    	
}