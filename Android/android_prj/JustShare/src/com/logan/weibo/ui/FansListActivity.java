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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.logan.R;
import com.logan.util.UIHelper;
import com.logan.weibo.adapter.FansListAdapter;
import com.logan.weibo.bean.BaseActivity;
import com.logan.weibo.bean.QStatus;
import com.logan.weibo.widget.NewDataToast;
import com.logan.weibo.widget.PullToRefreshListView;
import com.weibo.net.WeiboException;
/**
 * 粉丝列表
 * @author Logan <a href="https://github.com/Logan676/JustSharePro"/>
 *   
 * @version 1.0 
 *  
 */
public class FansListActivity extends BaseActivity{
        
        private final static String TAG = "FansListActivity";
        public final int REFRESH_LIST = 0;
        //private String id = ""; //转播父结点微博id
        private int pageSum = 10;
        public final static int RETWEET_LIST = 1;
        // ----------头部工具栏-----------------------//
        public ImageView tweet = null;
        public ProgressBar mHeadProgress = null;
        public TextView title = null;
        
        // -------中部ListView组件和适配器------------//
        public PullToRefreshListView pullToRefreshListView;
        public View listView_footer;
        public TextView listView_foot_more;
        public ProgressBar listView_foot_progress;
        public static String jsonData;
        
        //----------腾讯微博参数------------------------//
        private int pageSize = 10;
        private int pageFlag = 0;//分页标识（0：第一页，1：向下翻页，2向上翻页）
        private String pageTime = "0";//本页起始时间（第一页：填0，向上翻页：填上一次请求返回的第一条记录时间，向下翻页：填上一次请求返回的最后一条记录时间）
        private String mQData = null;
        private List<QStatus> mQStatusList = null;
        private FansListAdapter mQAdapter =null;
        private Handler mQlistViewHandler;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.user_fanslist);
                initHeader();
                initCenter();
                initTencetData();
                
        }
        private void initHeader(){
                // -------头部工具栏----------------------------------
                tweet = (ImageView) findViewById(R.id.weibo_headbar_tweet);
                title = (TextView) findViewById(R.id.weibo_headbar_title);
                mHeadProgress =  (ProgressBar) findViewById(R.id.weibo_headbar_refreshBtn);
                tweet.setVisibility(View.INVISIBLE);
                title.setText("粉丝列表");
        }
        
        private void initCenter(){
                // --------------中部ListView和适配器---------------------
                listView_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
                listView_foot_more = (TextView)listView_footer.findViewById(R.id.listview_foot_more);
                listView_foot_progress = (ProgressBar)listView_footer.findViewById(R.id.listview_foot_progress);
                pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.listview);
                pullToRefreshListView.addFooterView(listView_footer);//添加底部视图  必须在setAdapter前
                        
        }
        private void initTencetData(){
                mQData = fanslist(getInstance(), "100", "0", "0", "0");
                Log.v(TAG, "start to init Tencent, Sir!");
                //Log.v(TAG, mQData);
                mQStatusList = getQStatusList(mQData);
                mQAdapter = new FansListAdapter(FansListActivity.this, mQStatusList);
                mQlistViewHandler = this.getLvHandler(pullToRefreshListView, mQAdapter, listView_foot_more, listView_foot_progress, pageSize);
                pullToRefreshListView.setAdapter(mQAdapter);
                pullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //点击头部、底部栏无效
                        if(position == 0 || view == listView_footer) return;
                        //跳转到微博详情
                        Intent intent = new Intent();
                        Bundle mBundle = new Bundle();  
                    mBundle.putSerializable("detail",mQStatusList.get(position-1));  
                        intent.putExtras(mBundle);
                                intent.setClass(FansListActivity.this, QStatusDetailActivity.class);
                                startActivity(intent);
                }               
                });
            pullToRefreshListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(AbsListView view, int scrollState) {
                                pullToRefreshListView.onScrollStateChanged(view, scrollState);
                                
                                //数据为空--不用继续下面代码了
                                if(mQStatusList.isEmpty()) return;
                                
                                //判断是否滚动到底部
                                boolean scrollEnd = false;
                                try {
                                        if(view.getPositionForView(listView_footer) == view.getLastVisiblePosition())
                                                scrollEnd = true;
                                } catch (Exception e) {
                                        //Log.v(TAG, "scroll exception");
                                        scrollEnd = false;
                                }
                                
                                //int lvDataState = StringUtils.toInt(pullToRefreshListView.getTag());

                                if(scrollEnd)//&& lvDataState == UIHelper.LISTVIEW_DATA_MORE
                                {
                                        pullToRefreshListView.setTag(UIHelper.LISTVIEW_DATA_LOADING);
                                        listView_foot_more.setText(R.string.loading);
                                        listView_foot_progress.setVisibility(View.VISIBLE);
                                        //页码
                                        int pageFlag = 1;//下翻页
                                        pageTime = mQStatusList.get(mQStatusList.size()-1).getPageTime();
                                        //Log.v(TAG, "pageFlag is: "+pageFlag);
                                        loadTencentLvData(pageFlag, pageTime, mQlistViewHandler, UIHelper.LISTVIEW_ACTION_SCROLL);
                                }
                        }
                        @Override
                        public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
                                //Log.v(TAG, "onRefresh 1");
                                pullToRefreshListView.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                        }
                });
            pullToRefreshListView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
                @Override
                        public void onRefresh() {
                        //Log.v(TAG, "onRefresh 2");
                        loadTencentLvData(0, "0", mQlistViewHandler, UIHelper.LISTVIEW_ACTION_REFRESH);
                }
            }); 
        }

        /**
         * 腾讯 微博接口
         * 获取盛放微博信息的容器
         * 
         * @param jsonStrData
         * @return List<Status>
         */
        public ArrayList<QStatus> getQStatusList(String jsonStrData) {
                jsonData = jsonStrData;
                ArrayList<QStatus> statusList = new ArrayList<QStatus>();
                JSONObject obj = null;
                try {
                        obj = new JSONObject(jsonData);
                } catch (JSONException e2) {

                        e2.printStackTrace();
                }

                JSONObject dataObj = null;
                try {
                        if (!obj.isNull("data")) {dataObj = obj.getJSONObject("data");
                        }
                        else return null;
                } catch (JSONException e1) {
                        e1.printStackTrace();
                        return null;
                }
                JSONArray data = null;
                try {
                        data = dataObj.getJSONArray("info");
                } catch (JSONException e) {
                        e.printStackTrace();
                        return null;
                }
                if (data != null && data.length() > 0) {
                        
                        int lenth = data.length();
                        for (int i = 0; i < lenth; i++) {
                                try {
                                        JSONObject json = data.optJSONObject(i);
                                        QStatus status = QStatus.getQFan(json);
                                        statusList.add(status);
                                } catch (JSONException e) {
                                        e.printStackTrace();
                                        Log.v(TAG, "Load Error:  "+ i);
                                }
                        }
                }
                
                return statusList;
        }
        /**
     * 获取listview的初始化Handler
     * @param lv
     * @param adapter
     * @return
     */
    @SuppressLint("HandlerLeak")
        private Handler getLvHandler(final PullToRefreshListView lv,final BaseAdapter adapter,final TextView more,final ProgressBar progress,final int pageSize){
        
        return new Handler(){
                        @Override
                        public void handleMessage(Message msg) {
                                if(msg.what >= 0){
                                        //listview数据处理
                                        //if(isSina) handleSinaLvData(msg.what, msg.obj, msg.arg2, msg.arg1);
                                        if(isTencent) handleTencentLvData(msg.what, msg.obj, msg.arg2, msg.arg1);
                                        
                                        if(msg.what < pageSize){
                                                lv.setTag(UIHelper.LISTVIEW_DATA_FULL);
                                                adapter.notifyDataSetChanged();
                                                more.setText(R.string.load_full);//已经加载完毕
                                        }else if(msg.what == pageSize){
                                                lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
                                                adapter.notifyDataSetChanged();
                                                more.setText(R.string.load_more);
                                                
                                        }
                                }
                                else if(msg.what == -1){
                                        //有异常--显示加载出错 & 弹出错误消息
                                        lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
                                        more.setText(R.string.load_error);
                                        //((AppException)msg.obj).makeToast(Main.this);
                                }
                                if(adapter.getCount()==0){
                                        lv.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
                                        more.setText(R.string.load_empty);
                                }
                                progress.setVisibility(View.GONE);
                                mHeadProgress.setVisibility(View.GONE);
                                if(msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH){
                                        lv.onRefreshComplete(getString(R.string.pull_to_refresh_update) + new Date().toLocaleString());
                                        lv.setSelection(0);
                                }
                                else if(msg.arg1 == UIHelper.LISTVIEW_ACTION_SCROLL){
                                        lv.onRefreshComplete();
                                        //lv.setSelection(0);
                                }
                        }
                };
    }
    
    /**
     * 获取主页时间线微博信息
     * @param currentTag 模块标识
     * @param pageflag 分页标识（0：第一页，1：向下翻页，2向上翻页）
     * @param pagetime 本页起始时间（第一页：填0，向上翻页：填上一次请求返回的第一条记录时间，向下翻页：填上一次请求返回的最后一条记录时间） 
     * @param handler 处理器
     * @param action 动作标识
     */
        private void loadTencentLvData(final int pageflag,final String pageTime, final Handler handler,final int action){ 
                mHeadProgress.setVisibility(View.VISIBLE);              
                new Thread(){
                        @Override
                        public void run() {                             
                                Message msg = new Message();
                                if(action == UIHelper.LISTVIEW_ACTION_REFRESH || action == UIHelper.LISTVIEW_ACTION_SCROLL) {
                                //Log.v(TAG, "try to load data...");
                                        try {                                   
                                                mQData = fanslist(getInstance(), "100", "0", "0", "0");
                                                //Log.v(TAG, "fetched data:"+mQData);
                                                List<QStatus> newData = getQStatusList(mQData);
                                                msg.what = newData.size();
                                                msg.obj = newData;
                                                Log.v(TAG, "加载粉丝数据size"+newData.size());
                            } catch (Exception e) {
                                Log.v(TAG,"load data exception");
                                e.printStackTrace();
                                msg.what = -1;
                                msg.obj = e;    
                            }
                                msg.arg1 = action;
                                msg.arg2 = UIHelper.LISTVIEW_DATATYPE_NEWS;
                                handler.sendMessage(msg);
                                Log.v(TAG, "we sent data just now, Sir");
                                }
                        }
                }.start();
        }

        
         /**
            * 处理腾讯微博listview数据
            * @param what 数量
            * @param obj 数据
            * @param objtype 数据类型
            * @param actiontype 操作类型
            * @return notice 通知信息
            */
           private void handleTencentLvData(int what, Object obj, int objtype, int actiontype){
                        switch (actiontype) {
                                case UIHelper.LISTVIEW_ACTION_INIT:
                                case UIHelper.LISTVIEW_ACTION_REFRESH:
                                case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG:
                                        int newdata = 0;//新加载数据-只有刷新动作才会使用到
                                        switch (objtype) {
                                                case UIHelper.LISTVIEW_DATATYPE_NEWS:
                                                        @SuppressWarnings("unchecked")
                                                        List<QStatus> newList = (List<QStatus>) obj;
                                                        pageSum = what;
                                                        //Log.v(TAG, "handler starts to handle, Sir");
                                                        if(actiontype == UIHelper.LISTVIEW_ACTION_REFRESH){
                                                                if(mQStatusList.size() > 0){
                                                                        for(QStatus status : newList){
                                                                                boolean b = false;
                                                                                for(QStatus status2 : mQStatusList){
                                                                                        if (status.getId().equals(status2.getId())) {
                                                                                                b = true;
                                                                                                break;
                                                                                        }
                                                                                }
                                                                                if (!b) newdata++;
                                                                        }
                                                                }else{
                                                                        
                                                                        newdata = what;
                                                                }
                                                        }
                                                        mQStatusList.clear();//先清除原有数据
                                                        mQStatusList.addAll(newList);
                                                        break;
                                                
                                        }
                                        if(actiontype == UIHelper.LISTVIEW_ACTION_REFRESH){
                                                //提示新加载数据
                                                if(newdata >0){
                                                        //NewDataToast.makeText(this, getString(R.string.new_data_toast_message, newdata), mApplication.isAppSound()).show();
                                                        NewDataToast.makeText(this, getString(R.string.new_data_toast_message, newdata), true).show();
                                                }else{
                                                        NewDataToast.makeText(this, getString(R.string.new_data_toast_none), false).show();
                                                }
                                        }
                                        break;
                                case UIHelper.LISTVIEW_ACTION_SCROLL:
                                        switch (objtype) {
                                                case UIHelper.LISTVIEW_DATATYPE_NEWS:
                                                        @SuppressWarnings("unchecked")
                                                        List<QStatus> newList = (List<QStatus>) obj;
                                                        pageSum += what;
                                                        if(mQStatusList.size() > 0){
                                                                for(QStatus status : newList){
                                                                        boolean b = false;
                                                                        for(QStatus status2 : mQStatusList){
                                                                                if(status.getId().equals(status2.getId())){
                                                                                        b = true;
                                                                                        break;
                                                                                }
                                                                        }
                                                                        if(!b) mQStatusList.add(status);
                                                                }
                                                        }else{
                                                                mQStatusList.addAll(newList);
                                                        }
                                                        break;
                                        }
                                        break;
                        }
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

        @Override
        public int getLayout() {
                // TODO Auto-generated method stub
                return 0;
        }

}