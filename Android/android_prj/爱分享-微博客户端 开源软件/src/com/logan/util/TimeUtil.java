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
package com.logan.util;

import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * 微博发表时间处理类
 * @author Logan <a href="https://github.com/Logan676/JustSharePro"/>
 *   
 * @version 1.0 
 *  
 */
public class TimeUtil {

	public static String converTime(long timestamp) {
		long currentSeconds = System.currentTimeMillis() / 1000;
		long interval = currentSeconds - timestamp;// 与现在时间相差秒数
		String timeStr = null;
		if (interval > 24 * 60 * 60) {// 1天以上
			timeStr = interval / (24 * 60 * 60) + "天前";
		} else if (interval > 60 * 60) {// 1小时-24小时
			timeStr = interval / (60 * 60) + "小时前";
		} else if (interval > 60) {// 1分钟-59分钟
			timeStr = interval / 60 + "分钟前";
		} else {// 1秒钟-59秒钟
			timeStr = "刚刚";
		}
		return timeStr;
	}

	public static String getStandardTime(long timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm");
		Date date = new Date(timestamp * 1000);
		sdf.format(date);
		return sdf.format(date);
	}
}
