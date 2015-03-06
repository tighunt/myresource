package com.rtk.android.miracast;

import java.io.IOException;
import java.io.File;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.pluses.wifidisplay.WifiDisplayEngine;

public class videoPlayerActivity extends Activity {

    private static final String TAG = "videoPlayerActivity";
        private static boolean useMyPlayer = true;
	private SurfaceView sView = null;
	//public MediaPlayer mPlayer = null;
	public int flag = 0;
	public String url = null;
	private final IntentFilter intentFilter = new IntentFilter();
	private BroadcastReceiver receiver = null;
	private String devname;
	private String solution;
	private String videocodec = "H.264";
	private String ss = "\r\n";
	private Intent intent;
	// private boolean bisReceiveRegisted = false;
	int height = 333;
	int width = 444;
	private int[] AudioInfo = null;
	private int audio_num_stream = 0;
	private int curr_audio_stream_num = 0;
	public TextView textname;
	public TextView textresolution;
	public TextView textvideo;
	public TextView textaudio;
	public TextView texthz;
	public View layout;

	// private AlertDialog isExit;
	// private AlertDialog alertDialog;
        WifiDisplayEngine engine;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,       
                      WindowManager.LayoutParams. FLAG_FULLSCREEN); 
					  
		setContentView(R.layout.video_player);
		File file = new File("/storage/udisk/sda4/wfd.ts");
		if (!file.exists()) {
			Log.e("TEST", "no file");
                        finish();
			return;
		} else if (!(file.canRead()&& file.canWrite())) {
			Log.e("TEST", "file can not read");
                        finish();
			return;
		}
		intentFilter.addAction("MediaPlayerKill");
		//intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		//mPlayer = new MediaPlayer();
                engine = new WifiDisplayEngine();
		sView = (SurfaceView) findViewById(R.id.surfaceView);
		sView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		sView.getHolder().setKeepScreenOn(true);
		sView.getHolder().setFixedSize(1920, 300);
		intent = getIntent();
		String str1 = intent.getStringExtra("URL");
		devname = intent.getStringExtra("device");
		Log.i(TAG, " videoPlayerActivity devname :" + devname);
		if (str1 != null) {
			Log.e(TAG, " videoPlayerActivity ;the intent data is :" + str1);
		}
		if (str1 != null && !str1.isEmpty()) {
			url = str1;
			sView.getHolder().addCallback(new SurfaceLister());
		}
	}

	/** register the BroadcastReceiver with the intent values to be matched */
	@Override
	public void onResume() {
		Log.i(TAG, "videoPlayerActivity->onResume()!");
		super.onResume();
		if (receiver == null) {
			receiver = new VideoPlayerBroadcastReceiver(this);
		Log.i(TAG, "new VideoPlayerBroadcastReceiver(this)");	
		}	
		registerReceiver(receiver, intentFilter);
		Log.i(TAG, "registerReceiver(receiver, intentFilter);");	
	}

	@Override
	protected void onPause() {
		Log.e(TAG, "videoPlayerActivity->onPause()!");
		if (receiver != null) {
			unregisterReceiver(receiver);
			receiver = null;
			Log.e(TAG, "unregisterReceiver(receiver);");
		}
                                       Log.e(TAG,
                                "engine.uninit");
                                       engine.deinit();
		super.onPause();
	}

	@Override
	public void onStop() {
		Log.e(TAG, "videoPlayerActivity->onStop()!");

		if (receiver != null) {
			Log.e(TAG,
					"unregisterReceiver(receiver);");
			unregisterReceiver(receiver);
			receiver = null;
		}
		super.onStop();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.e(TAG,
				"videoPlayerActivity->onDestroy()!  tiny_li");

		if (receiver != null) {
			unregisterReceiver(receiver);
			receiver = null;
		}
		super.onDestroy();
	}

	public class SurfaceLister implements SurfaceHolder.Callback {

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// TODO Auto-generated method stub
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			try {

                                        Log.e(TAG,
                                "engine.setsurface");
                                       engine.setSurface(holder.getSurface());
                                       Log.e(TAG,
                                "engine.initpalyer");
                                       boolean created = engine.initPlayer(url);
                                       /*Log.e(TAG,
                                "engine.start");
                                       // engine.start();
                                         Log.e(TAG,
                                "engine.working");*/

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub

		}

	}

	/*
	 * private OnPreparedListener videoPreparedListener = new
	 * OnPreparedListener(){
	 * 
	 * @Override public void onPrepared(MediaPlayer mp) { // TODO Auto-generated
	 * method stub mPlayer.start();
	 * Log.i(TAG,"player onPrepared "); } };
	 */

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean tmp = false;
		switch (keyCode) {

		case KeyEvent.KEYCODE_BACK:
		case KeyEvent.KEYCODE_ESCAPE: {

			AlertDialog isExit = new AlertDialog.Builder(this)
			//.setCanceledOnTouchOutside(false)
			.setMessage(getString(R.string.connect_exit))
			.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					Intent intent = new Intent("MediaPlayerKill");
					sendBroadcast(intent);
					return;
				}
			})
			.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					return;
				}
			})
		    .create();
		    Window window = isExit.getWindow();       
            window.setGravity(Gravity.BOTTOM);  
			isExit.show();
			isExit.getButton(AlertDialog.BUTTON_POSITIVE).clearFocus();
			isExit.getButton(AlertDialog.BUTTON_NEGATIVE).requestFocus();
			break;
		}

		case KeyEvent.KEYCODE_MENU: {
			//showDialog();
			tmp = true;
			break;
		}
		}
		if (tmp)
			return tmp;
		return super.onKeyDown(keyCode, event);
	}


/*
	private void showDialog() {

		height = mPlayer.getVideoHeight();
		Log.i(TAG, "height= " + height);
		width = mPlayer.getVideoWidth();
		Log.i(TAG, "width= " + width);
		solution = String.valueOf(width) + "X" + String.valueOf(height);
		String mAudio = getAudioTrackInfo();
        String mhz="60Hz";
		AlertDialog.Builder builder;

		Context mContext = videoPlayerActivity.this;
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		String language= this.getResources().getConfiguration().locale.getLanguage();
		if(language.equals("ar")||language.equals("iw")||language.equals("fa"))
			 layout = inflater.inflate(R.layout.toshiba_dialog, null);
		else
		{
			 layout = inflater.inflate(R.layout.dialog, null);
		}

		textname = (TextView) layout.findViewById(R.id.mDev);
		textname.setText(devname);
		textresolution = (TextView) layout.findViewById(R.id.mRes);
		textresolution.setText(solution);
		texthz = (TextView) layout.findViewById(R.id.mHz);
		texthz.setText(mhz);
		textvideo = (TextView) layout.findViewById(R.id.mVid);
		textvideo.setText(videocodec);
		textaudio = (TextView) layout.findViewById(R.id.mAud);
		textaudio.setText(mAudio);

		builder = new AlertDialog.Builder(mContext);
		builder.setView(layout, 0, 0, 0, 0);
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}
*/
	public void shutDown() {
		Intent intent = new Intent("MediaPlayershutdown");
		sendBroadcast(intent);
		finish();
	}
	
}
