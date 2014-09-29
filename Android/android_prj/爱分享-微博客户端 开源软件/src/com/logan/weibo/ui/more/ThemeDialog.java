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
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.logan.R;
/**
 * 主题
 * 
 * @author Logan <a href="https://github.com/Logan676/JustSharePro"/>
 *   
 * @version 1.0 
 *  
 */
public class ThemeDialog extends Activity{

	private RadioGroup radioGroup=null;
	private RadioButton theme1=null;
	private RadioButton theme2=null;
	private RadioButton theme3=null;
	private Button button=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more_theme);
		radioGroup=(RadioGroup)findViewById(R.id.radioGroupTheme);
		theme1=(RadioButton)findViewById(R.id.radioThemeOne);
		theme2=(RadioButton)findViewById(R.id.radioThemeTwo);
		theme3=(RadioButton)findViewById(R.id.radioThemeThree);
		
		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(theme1.getId()==checkedId){
					System.out.println("theme1");
				}
				else if(theme2.getId()==checkedId){
					System.out.println("theme2");
				}
				else if(theme3.getId()==checkedId){
					System.out.println("theme3");
				}
			}
		});
		
		button=(Button)findViewById(R.id.surebutton);
		button.setOnClickListener(new ButtonListener());

	}

	class ButtonListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			finish();
		}

	}
}
