package com.android.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferancesTools {
	// 3d设置.
	public static final String D3_SELF_ADAPTER = "d3_self_adapter";
	public static final String D3_MODEL = "d3_model";
	public static final String D3_3D_2D = "d3_3d_to_2d";
	public static final String D3_DEPTH = "d3_depth";
	public static final String D3_VIEW_POINT = "d3_view_point";
	public static final String D3_AUDO_START = "d3_auto_start";
	public static final String D3_SCREEN_VIEW = "d3_screen_view";
	public static final String D3_LR_SWITCH = "d3_lr_switch";

	// 视频存储字段.
	public static final String VIDEO_BRIGHTNESS = "video_brightness";
	public static final String VIDEO_CONTRAST = "video_contrast";
	public static final String VIDEO_COLOR = "video_color";
	public static final String VIDEO_SHARPNESS = "video_sharpness";

	// 音频存储字段.
	public static final String AUDIO_HIG = "audio_high";
	public static final String AUDIO_LOW = "audio_low";
	public static final String AUDIO_BLANS = "audio_balance";
	public static final String AUDIO_SPDIF = "audio_spdif";

	// spdif type:
	public static final int SPDIF_OFF = 0;
	public static final int SPDIF_RAW = 1;
	public static final int SPDIF_PCM = 2;
	
	///[2012-3-6add]
	public static final String PPPOE_USER_NAME = "pppoe_usr";
	public static final String PPPOE_USER_PWD  = "pppoe_pwd";
	public static final String PPPOE_HW_NAME   = "hw_name";
	

	Context context;

	public PreferancesTools(Context context) {
		this.context = context;
	}

	/**
	 * 从SharedPreferences对象中获取int值.
	 * 
	 * @param context
	 *            上下文 .
	 * @param name
	 *            键名.
	 * @param defalut
	 *            默认值.
	 * @return 若键名存在,则返回键值,否则返回默认.
	 */
	public int getIntPref(String name, int defalut) {
		String pkg = context.getPackageName();// 用包名当作文件名
		SharedPreferences prefs = context.getSharedPreferences(pkg, Context.MODE_WORLD_READABLE
				+ Context.MODE_WORLD_WRITEABLE);// 设置读写权限
		return prefs.getInt(name, defalut);
	}

	/**
	 * 设置一个sharedPreferences中的key值对.
	 * 
	 * @param context
	 *            上下文.
	 * @param name
	 *            键名.
	 * @param value
	 *            键值.
	 */
	public void setIntPref(String name, int value) {// name
													// 就是key字符串,value是设置的值
		String pkg = context.getPackageName();// 用包名当作文件名
		SharedPreferences prefs = context.getSharedPreferences(pkg, Context.MODE_WORLD_READABLE
				+ Context.MODE_WORLD_WRITEABLE);
		Editor ed = prefs.edit();
		ed.putInt(name, value);
		ed.commit();
	}

	/**
	 * 从SharedPreferences对象中获取String值.
	 * 
	 * @param context
	 *            上下文 .
	 * @param name
	 *            键名.
	 * @param defalut
	 *            默认值.
	 * @return
	 */
	public String getStringPref(String name, String defalut) {
		String pkg = context.getPackageName();// 用包名当作文件名
		SharedPreferences prefs = context.getSharedPreferences(pkg, Context.MODE_WORLD_READABLE
				+ Context.MODE_WORLD_WRITEABLE);// 设置读写权限
		return prefs.getString(name, defalut);
	}

	/**
	 * 设置一个sharedPreferences中的key值对.
	 * 
	 * @param context
	 *            上下文.
	 * @param name
	 *            键名.
	 * @param value
	 *            键值.
	 */
	public void setStringPref(String name, String value) {// name
		// 就是key字符串,value是设置的值
		String pkg = context.getPackageName();// 用包名当作文件名
		SharedPreferences prefs = context.getSharedPreferences(pkg, Context.MODE_WORLD_READABLE
				+ Context.MODE_WORLD_WRITEABLE);
		Editor ed = prefs.edit();
		ed.putString(name, value);
		ed.commit();
	}
	
	/**
	 * 从SharedPreferences对象中获取String值.
	 * 
	 * @param context
	 *            上下文 .
	 * @param name
	 *            键名.
	 * @param defalut
	 *            默认值.
	 * @return
	 */
	public long getLongPref(String name, Long defalut) {
		String pkg = context.getPackageName();// 用包名当作文件名
		SharedPreferences prefs = context.getSharedPreferences(pkg, Context.MODE_WORLD_READABLE
				+ Context.MODE_WORLD_WRITEABLE);// 设置读写权限
		return prefs.getLong(name, defalut);
	}

	/**
	 * 设置一个sharedPreferences中的key值对.
	 * 
	 * @param context
	 *            上下文.
	 * @param name
	 *            键名.
	 * @param value
	 *            键值.
	 */
	public void setLongPref(String name, Long value) {// name
		// 就是key字符串,value是设置的值
		String pkg = context.getPackageName();// 用包名当作文件名
		SharedPreferences prefs = context.getSharedPreferences(pkg, Context.MODE_WORLD_READABLE
				+ Context.MODE_WORLD_WRITEABLE);
		Editor ed = prefs.edit();
		ed.putLong(name, value);
		ed.commit();
	}
}
