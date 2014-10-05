package com.realtek.camera;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Note: This is a singleton controller for screen recording instead of an android service.
 * @author Jason Lin
 *
 */
public class ScreenRecordService {

	private static final String TAG = "ScreenRecordService";
	private static final boolean VERBOSE = true;
	
	private static final int NOTIFICATION_ID = 0;
	
	public static final String ACTION_SCREENRECORD_START = "com.realtek.screenrecord.START";
	public static final String ACTION_SCREENRECORD_STOP = "com.realtek.screenrecord.STOP";
	public static final String ACTION_SCREENRECORD_DISMISS = "com.realtek.screenrecord.DISMISS";
	
	private static ScreenRecordService sInstance;
	
	public static ScreenRecordService getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new ScreenRecordService(context);
		}
		
		return sInstance;
	}
	
	public interface ScreenRecordListener {
		public void onStartRecord(File outFile, int width, int height, int timeSec, long startTime, long endTime, boolean hasStopped);
		public void onStopRecord(File outFile, int width, int height, int timeSec, long startTime, long endTime);
	}
	
	private Context mContext;
	
	private RecordingThread mRecordingThread;
	
	//
	private final Handler mHandlerCallback;
	private final List<ScreenRecordListener> mListeners = new ArrayList<ScreenRecordService.ScreenRecordListener>();
	
	//
	private boolean mNotification = false;
	
	private ScreenRecordService(Context context) {
		mContext = context.getApplicationContext();
		mHandlerCallback = new Handler(Looper.getMainLooper());
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_SCREENRECORD_START);
		filter.addAction(ACTION_SCREENRECORD_STOP);
		filter.addAction(ACTION_SCREENRECORD_DISMISS);
		mContext.registerReceiver(mBroadcastReceiver, filter);
	}
	
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_SCREENRECORD_STOP.equals(action)) {
				stopRecording();
			} else if (ACTION_SCREENRECORD_START.equals(action)) {
				File outFile = getMediaOutputFile("REC_");
		    	startRecording(outFile, 1280, 720, -1);
			} else if (ACTION_SCREENRECORD_DISMISS.equals(action)) {
				setNotificationEnabled(false);
			}
		}
	};
	
	public synchronized void startRecording(File outFile, int width, int height, int timeSec) {
		if (mRecordingThread != null) {
			// Already started.
			return;
		}
		
		// Create record thread
		mRecordingThread = new RecordingThread();
		
		// Setup context
		mRecordingThread.mFileOut = outFile;
		mRecordingThread.mWidth = width;
		mRecordingThread.mHeight = height;
		mRecordingThread.mDuration = timeSec;
		
		// Start thread
		mRecordingThread.start();
	}
	
	public synchronized void stopRecording() {
		if (mRecordingThread == null) {
			// Already stopped
			return;
		}
		
		mRecordingThread.stopRecord();
	}
	
	public synchronized boolean isRecording() {
		return mRecordingThread != null;
	}
	
	public synchronized void registerCallback(ScreenRecordListener listener, boolean notify) {
		mListeners.add(listener);
		if (notify) {
			if (mRecordingThread != null) {
				listener.onStartRecord(
						mRecordingThread.mFileOut,
						mRecordingThread.mWidth,
				        mRecordingThread.mHeight,
				        mRecordingThread.mDuration,
				        mRecordingThread.mStartTime,
				        mRecordingThread.mEndTime,
				        mRecordingThread.hasStopped());
			} else {
				listener.onStopRecord(null, 0, 0, 0, 0, 0);
			}
		}
	}
	
	public synchronized void unregisterCallback(ScreenRecordListener listener) {
		mListeners.remove(listener);
	}
	
	private synchronized void notifyRecordFinished(RecordingThread recordingThread) {
		if (mRecordingThread != recordingThread) {
			return;
		}
		
		mRecordingThread = null;
		
		// invoke callback
		mHandlerCallback.post(new CallbackStop(recordingThread));
	}
	
	private class CallbackStart implements Runnable {
		
		protected final RecordingThread recordingThread;
		private final boolean hasStopped;
		
		public CallbackStart(RecordingThread thread) {
			recordingThread = thread;
			hasStopped = thread.hasStopped();
		}

		@Override
        public void run() {
	        for (ScreenRecordListener l : mListeners) {
	        	l.onStartRecord(
        			recordingThread.mFileOut,
        			recordingThread.mWidth,
        			recordingThread.mHeight,
        			recordingThread.mDuration,
        			recordingThread.mStartTime,
        			recordingThread.mEndTime,
        			hasStopped);
	        }
        }
		
	}
	
	private class CallbackStop extends CallbackStart {
		
		public CallbackStop(RecordingThread thread) {
			super(thread);
		}

		@Override
        public void run() {
	        for (ScreenRecordListener l : mListeners) {
	        	l.onStopRecord(
        			recordingThread.mFileOut,
        			recordingThread.mWidth,
        			recordingThread.mHeight,
        			recordingThread.mDuration,
        			recordingThread.mStartTime,
        			recordingThread.mEndTime);
	        }
			String text = mContext.getString(R.string.msg_format_screenrecord_finish,
			        recordingThread.mFileOut.getAbsolutePath());
	        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
        }
		
	}
	
	private class RecordingThread extends Thread {
		
		// Task context
		private File mFileOut;
		private int mWidth;
		private int mHeight;
		private int mDuration;
		
		private long mStartTime;
		private long mEndTime;

		// lock protected status
		private Process mProcess;
		private boolean mHasStopped = false;
		
		@Override
        public void run() {
	        super.run();
	        Runtime runtime = Runtime.getRuntime();
	        String response = null;
	        String error = null;
	        char[] buffer = new char[128];
	        
	        // Create command
	        String cmd;
	        if (mDuration > 0) {
	        	cmd = String.format(Locale.US, "screenrecord --size %dx%d --time-limit %d %s",
	        			mWidth, mHeight, mDuration, mFileOut.getAbsolutePath());
	        } else {
	        	cmd = String.format(Locale.US, "screenrecord --size %dx%d %s",
	        			mWidth, mHeight, mFileOut.getAbsolutePath());
	        }
	        
	        // Prepare to start
	        mStartTime = System.currentTimeMillis();
	        
			// invoke onStart callback
			mHandlerCallback.post(new CallbackStart(mRecordingThread));
			
			// Show on going Notification
			if (mNotification) {
				showOnGoingNotification(mContext, this);
			}
			
			// Start of record process
	        if (VERBOSE) {
	        	Log.v(TAG, String.format("Start: command=%s", cmd));
	        }
	        try {
	        	startCommand(runtime, cmd);
	        	// Read std out
	        	StringBuilder sb = new StringBuilder();
	        	InputStream is = mProcess.getInputStream();
	            InputStreamReader isr = new InputStreamReader(is);
	            for(int r = isr.read(buffer); r >=0; r = isr.read(buffer)) {
	            	sb.append(buffer, 0, r);
	            }
	            response = sb.toString();
	            try {isr.close();} catch (Exception e) {}
	            
	            // Read error out
	            sb = new StringBuilder();
	            is = mProcess.getErrorStream();
	            isr = new InputStreamReader(is);
	            for(int r = isr.read(buffer); r >=0; r = isr.read(buffer)) {
	            	sb.append(buffer, 0, r);
	            }
	            error = sb.toString();
	            try {isr.close();} catch (Exception e) {}
	            
	            
            } catch (Exception e) {
	            e.printStackTrace();
	            error = e.getMessage();
            }
	        
	        // End of record process
	        mEndTime = System.currentTimeMillis();
	        if (VERBOSE) {
	        	Log.v(TAG, "Finished: " + response);
	        }
	        
	        // Log error
	        if (!TextUtils.isEmpty(error)) {
	        	Log.e(TAG, error);
	        }
	        
	        // Notify onStop callback
	        notifyRecordFinished(this);
	        
	        // Update notification
	        if (mNotification) {
	        	showReadyNotification(mContext);
	        }
        }
		
		public synchronized boolean hasStopped() {
			return mHasStopped;
		}
		
		public synchronized void stopRecord() {
			mHasStopped = true;
			if (mProcess != null) {
				try {
					if (mNotification) {
						showProgressNotification(mContext);
					}
	                int pid = getProcessId(mProcess);
	                android.os.Process.sendSignal(pid, 2);// Send Ctrl+C
	                mProcess = null;
                } catch (Exception e) {
	                e.printStackTrace();
                }
			}
		}
		
		public synchronized void startCommand(Runtime runtime, String cmd) throws Exception {
			if (mHasStopped) {
				throw new Exception("Operation canceled");
			}
			mProcess = runtime.exec(cmd);
		}
	}
	
	public synchronized boolean isNotificationEnabled() {
		return mNotification;
	}
	
	public synchronized void setNotificationEnabled(boolean enabled) {
		if (mNotification == enabled) {
			return;
		}
		mNotification = enabled;
		
		if (mNotification) {
			// show notification based on current status
			if (mRecordingThread != null) {
				// Activate
                if (mRecordingThread.hasStopped()) {
                	// Stopping
                	showProgressNotification(mContext);
                } else {
                	// Recording
                	showOnGoingNotification(mContext, mRecordingThread);
                }
			} else {
				// Idle
				showReadyNotification(mContext);
			}
			
		} else {
			// Dismiss notification
			NotificationManager nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
			nm.cancel(NOTIFICATION_ID);
		}
	}
	
	private static void showOnGoingNotification(Context context, RecordingThread thread) {
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		PendingIntent pi = PendingIntent.getBroadcastAsUser(context, 0, new Intent(ACTION_SCREENRECORD_STOP), 0, UserHandle.CURRENT);
		Notification n = new Notification.Builder(context)
		.setContentTitle(context.getString(R.string.title_screenrecord))
		.setSmallIcon(R.drawable.ic_stat_screenrecord)
		.setAutoCancel(false)
		.setOngoing(true)
		.setUsesChronometer(true)
		.setWhen(thread.mStartTime)
		.setContentText(thread.mFileOut.getAbsolutePath())
		.addAction(R.drawable.ic_stat_av_stop, context.getString(R.string.stop), pi)
		.build();
		nm.notify(NOTIFICATION_ID, n);
	}
	
	private static void showReadyNotification(Context context) {
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		PendingIntent pi = PendingIntent.getBroadcastAsUser(context, 0, new Intent(ACTION_SCREENRECORD_START), 0, UserHandle.CURRENT);
		PendingIntent di = PendingIntent.getBroadcastAsUser(context, 0, new Intent(ACTION_SCREENRECORD_DISMISS), 0, UserHandle.CURRENT);
    	Notification n = new Notification.Builder(context)
    	.setContentTitle(context.getString(R.string.title_screenrecord))
    	.setSmallIcon(R.drawable.ic_stat_screenrecord)
    	.setAutoCancel(false)
    	.setOngoing(true)
    	.addAction(R.drawable.ic_stat_av_play, context.getString(R.string.start), pi)
    	.addAction(R.drawable.ic_stat_dismiss, context.getString(R.string.dismiss), di)
    	.build();
    	nm.notify(NOTIFICATION_ID, n);
	}
	
	private static void showProgressNotification(Context context) {
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    	Notification n = new Notification.Builder(context)
    	.setContentTitle(context.getString(R.string.title_screenrecord))
    	.setSmallIcon(R.drawable.ic_stat_screenrecord)
    	.setAutoCancel(false)
    	.setOngoing(true)
    	.setProgress(0, 0, true)
    	.build();
    	nm.notify(NOTIFICATION_ID, n);
	}
	
	private static int getProcessId(Process p) throws Exception {
		int pid = 0;
	    Field f = p.getClass().getDeclaredField("pid");
	    f.setAccessible(true);
	    pid = f.getInt(p);
		return pid;
	}
	
	private static final SimpleDateFormat FILE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);
	
	private static File getMediaOutputFile(String prefix) {
		// Get out directory
		File outDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
		outDir = new File(outDir, "screenrecord");
    	if (!outDir.exists()) {
    		outDir.mkdirs();
    	}
    	
    	// create file name
    	String fileName = FILE_FORMAT.format(Calendar.getInstance().getTime());
    	
    	// Create target file
    	File file = new File(outDir, prefix + fileName + ".mp4");
    	int copy = 0;
    	while(file.exists()) {
    		file = new File(outDir, String.format("%s%s (%d).mp4", prefix, fileName, ++copy));
    	}
    	
    	return file;
	}
}
