package com.rtk.mediabrowser;

import android.content.Context;

public class MediaBrowserConfig {

	public static boolean HAVE_PREVIEW = false;
	public static boolean HAVE_DLNA = false;
	public static boolean HAVE_PLAYLIST = true;
	public static boolean HAVE_3DMODE = true;
	public static boolean IS_DEMO_BOARD = true;
	public static boolean getRight2Left(Context cnt){
		return false;
	}
	public static float getFontSize(Context cnt) {
		return 0f;
	}
}
	