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
package com.logan.weibo.ui.more;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.logan.R;

/**
 * 账号管理
 * 
 * @author Logan <a href="https://github.com/Logan676/JustSharePro"/>
 * 
 * @version 1.0
 * 
 */
public class AccountAcitvity extends Activity {

	private RelativeLayout addCount = null;
	private RelativeLayout managerCount = null;
	private ImageView back = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more_account);

		addCount = (RelativeLayout) findViewById(R.id.addcount);
		addCount.setOnClickListener(new AddCountListener());

		managerCount = (RelativeLayout) findViewById(R.id.managerCount);
		managerCount.setOnClickListener(new ManagerCount());

		back = (ImageView) findViewById(R.id.back);
		back.setOnClickListener(new BackListener());

	}

	class AddCountListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			System.out.println("addcount");
		}

	}

	class ManagerCount implements OnClickListener {

		@Override
		public void onClick(View v) {

			System.out.println("managercount");
		}

	}

	class BackListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			finish();
		}

	}

}
