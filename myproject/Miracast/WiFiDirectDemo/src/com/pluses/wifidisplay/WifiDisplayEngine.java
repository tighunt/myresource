package com.pluses.wifidisplay;

import android.view.Surface;
import java.io.RandomAccessFile;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileOutputStream;
import android.util.Log;
import java.lang.ref.WeakReference;

public class WifiDisplayEngine {

        private static RandomAccessFile  file = null;
        private static FileOutputStream fout = null;
	private long mNativeContext; // accessed by native methods
	public WifiDisplayEngine(){
		init();
                native_setup(new WeakReference<WifiDisplayEngine>(this));
	}
	public void init(){
		nativeInit();
	}
	
	public void deinit(){
		nativeDeinit();
	}
	
	public boolean initPlayer(String filename){
		return nativeInitPlayer(filename);
	}
	
	public void start(){
		nativeStart();
	}
	
	public void stop(){
		nativeStop();
	}
	
	public void pause(){
                if(fout!= null)
                   try {
			fout.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		nativePause();
	}
	
	public void setSurface(Surface surface){
		nativeSetSurface(surface);
	}

	private static void postEventFromNative(Object engine_ref,
		int what, int arg1, int arg2, Object obj)	
	{		
		WifiDisplayEngine engine = (WifiDisplayEngine)((WeakReference)engine_ref).get();		
		if (engine == null) 
		{			
			return;		
		}

		Log.e("TEST","get message id = " + what);
	}
    /** Native methods, implemented in jni folder */
    public  native void nativeInit();
    public  native void nativeDeinit();
    public  native boolean nativeInitPlayer(String filename);
    public  native void nativeStart();
    public static native void nativeStop();
    public static native void nativePause();
    public static native void nativeSetSurface(Surface surface);
    private static native final void native_init();
    private native final void native_setup(Object mediaplayer_this);
    /*public static native boolean createStreamingMediaPlayer(String filename);
    public static native void setPlayingStreamingMediaPlayer(boolean isPlaying);
    public static native void shutdown();
    public static native void setSurface(Surface surface);
    public static native void rewindStreamingMediaPlayer();*/

    /** Load jni .so on initialization */
    static {
         System.loadLibrary("native-media-jni");
         native_init();
    }
}
