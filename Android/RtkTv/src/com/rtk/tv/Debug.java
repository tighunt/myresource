package com.rtk.tv;

import android.util.Log;

public class Debug {
    public final static String TAG_APP = "com.rtk.tv-";
    final static boolean DEBUG = true;


    public static int v(String tag, String msg) {
        return Log.v(TAG_APP + tag, msg);
    }

    public static int v(String tag, String msg, Throwable tr) {
        return Log.v(TAG_APP + tag, msg, tr);
    }

    public static int d(String tag, String msg){
        if(DEBUG == true)
            return Log.d(TAG_APP + tag, msg);
        else
            return 0;
    }

    public static int d(String tag, String msg, Throwable tr){
        if(DEBUG == true)
            return Log.d(TAG_APP + tag, msg, tr);
        else
            return 0;
    }

    public static int i(String tag, String msg) {
        return Log.i(TAG_APP + tag, msg);
    }

    public static int i(String tag, String msg, Throwable tr) {
        return Log.i(TAG_APP + tag, msg, tr);
    }
    public static int w(String tag, String msg) {
        return Log.w(TAG_APP + tag, msg);
    }
    public static int w(String tag, String msg, Throwable tr) {
        return Log.w(TAG_APP + tag, msg, tr);
    }

    public static int e(String tag, String msg) {
        return Log.e(TAG_APP + tag, msg);
    }

    public static int e(String tag, String msg, Throwable tr) {
        return Log.e(TAG_APP + tag, msg, tr);
    }
}