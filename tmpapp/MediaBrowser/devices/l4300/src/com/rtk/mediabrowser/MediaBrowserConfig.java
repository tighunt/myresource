package com.rtk.mediabrowser;
import android.content.Context;
import android.util.Log;

public class MediaBrowserConfig {

	public static boolean HAVE_PREVIEW = true;
	public static boolean HAVE_DLNA = false;
	public static boolean HAVE_PLAYLIST = false;
	public static boolean HAVE_3DMODE = false;
	public static boolean IS_DEMO_BOARD = false;
	public static boolean getRight2Left(Context cnt){
		String 	language= cnt.getResources().getConfiguration().locale.getLanguage();
		if(language.equals("ar")||
				language.equals("iw")||
				language.equals("fa"))
			return true;
		return false;
	}
	
	public static float getFontSize(Context cnt) {
            float fontScale = cnt.getResources().getConfiguration().fontScale;
        return fontScale;
    }
}
	