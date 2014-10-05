package com.rtk.mediabrowser;

import android.content.Context;

public class MediaBrowserConfig {

	public static boolean HAVE_PREVIEW = true;
	public static boolean HAVE_DLNA = false;
	public static boolean HAVE_PLAYLIST = false;
	public static boolean HAVE_3DMODE = false;
	public static boolean IS_DEMO_BOARD = false;
	public static boolean getRight2Left(Context cnt){
		return false;
	}
	public static float getFontSize(Context cnt) {
		return 0f;	
	}
}
	