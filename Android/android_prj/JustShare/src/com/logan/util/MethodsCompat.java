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

import java.io.File;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.graphics.Paint;
import android.os.Build;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;

/**
 * Android各版本的兼容方法
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-8-6
 */
public class MethodsCompat {
	
    @TargetApi(5)
    public static void overridePendingTransition(Activity activity, int enter_anim, int exit_anim) {
       	activity.overridePendingTransition(enter_anim, exit_anim);
    }

    @TargetApi(7)
    public static Bitmap getThumbnail(ContentResolver cr, long origId, int kind, Options options) {
       	return MediaStore.Images.Thumbnails.getThumbnail(cr,origId,kind, options);
    }
    
    @TargetApi(8)
    public static File getExternalCacheDir(Context context) {

//	    // return context.getExternalCacheDir(); API level 8
//
//	    // e.g. "<sdcard>/Android/data/<package_name>/cache/"
//	    final File extCacheDir = new File(Environment.getExternalStorageDirectory(),
//	        "/Android/data/" + context.getApplicationInfo().packageName + "/cache/");
//	    extCacheDir.mkdirs();
//	    return extCacheDir;

        return context.getExternalCacheDir();
    }

    @TargetApi(11)
    public static void recreate(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            activity.recreate();
        }
    }

    @TargetApi(11)
    public static void setLayerType(View view, int layerType, Paint paint) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            view.setLayerType(layerType, paint);
        }
    }

    @TargetApi(14)
    public static void setUiOptions(Window window, int uiOptions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            window.setUiOptions(uiOptions);
        }
    }
        
}