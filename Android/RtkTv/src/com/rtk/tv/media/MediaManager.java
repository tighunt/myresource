
package com.rtk.tv.media;

import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.rtk.tv.R;
import com.rtk.tv.TvManagerHelper;
import com.rtk.tv.data.ChannelInfo;
import com.rtk.tv.data.ProgramInfo;

public class MediaManager {
	
	private static final String TAG = "MediaManager";	
	private static final boolean VERBOSE = true;	
	public static final String EXTRA_LIVE_SOURCE = "com.realtek.tv.extra.LIVE_SOURCE";	
	public static final String EXTRA_CHANNEL_NUMBER = "com.realtek.tv.extra.CHANNEL_NUMBER";
	public static final String EXTRA_CHANNEL_NAME = "com.realtek.tv.extra.CHANNEL_NAME";
	public static final String EXTRA_PROGRAM_NAME = "com.realtek.tv.extra.PROGRAM_NAME";
	public static final String ACTION_ALARM_RECORD_START = "com.realtek.tv.recorder.action.ALARM_RECORD_START";
	public static final String ACTION_ALARM_RECORD_STOP = "com.realtek.tv.recorder.action.ALARM_RECORD_STOP";
	public static final String ACTION_SET_RECORD_STOP = "com.realtek.tv.recorder.action.SET_RECORD_STOP";
	public static final String EXTRA_START_TIME = "com.realtek.tv.recorder.extra.START_TIME";
	public static final String EXTRA_END_TIME = "com.realtek.tv.recorder.extra.END_TIME";
	public static final String EXTRA_REPEAT = "com.realtek.tv.recorder.extra.REPEAT";	
	public static final String SCHEME_ALARM_RECORD = "dvr";
	public static final int STAT_SCHEDULE_ADDED = 0;
	public static final int STAT_SCHEDULE_RECORDING = 1;
	public static final int STAT_SCHEDULE_CANCELED = 2;
	public static final int STAT_SCHEDULE_FAILED = 3;
	public static final int STAT_SCHEDULE_EXPIRED = 4;
	public static final int STAT_SCHEDULE_FINISHED = 5;
	public static final int STAT_SCHEDULE_INTERRUPTED = 6;
	public static final int REPEAT_ONCE = 0;
	public static final int REPEAT_DAILY = 1;
	public static final int REPEAT_WEEKLY = 2;
	private static final String DIR_REC = "REC";
	private static final SimpleDateFormat MEDIA_DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmm", Locale.US);
	private static MediaManager sInstance;

	public static final MediaManager getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new MediaManager(context);
		}
		return sInstance;
	}
	
	public static Bundle createArgumentsFromEpg(ChannelInfo channel, ProgramInfo program, int source) {
		// FIXME: Identify channel by using major,minor number and frequency.
		return createArguments(source, channel.getChannelId(), channel.getDisplayName(), program.getTitle(),
				program.getStartTimeUtcMillis(), program.getEndTimeUtcMillis(), REPEAT_ONCE);
	}

	public static Bundle createArguments(int source, long channelNumber, String channelName, String programName, long startTime, long endTime, int repeats) {
		Bundle args = new Bundle();
		args.putInt(EXTRA_LIVE_SOURCE, source);
		args.putLong(EXTRA_CHANNEL_NUMBER, channelNumber);
		args.putString(EXTRA_CHANNEL_NAME, channelName);
		args.putString(EXTRA_PROGRAM_NAME, programName);
		args.putLong(EXTRA_START_TIME, startTime);
		args.putLong(EXTRA_END_TIME, endTime);
		args.putInt(EXTRA_REPEAT, repeats);
		return args;
	}

	/**
	 * Create the URI used to identify the intents in AlarmManager.
	 * @param source
	 * @param channel
	 * @param start
	 * @param end
	 * @param repeats
	 * @return
	 */
	public static Uri createUri(int source, int channel, long start, long end, long repeats) {
		return Uri.parse(String.format(Locale.US, "%s:///%d/%d/%d-%d/%d", SCHEME_ALARM_RECORD, source, channel, start, end, repeats));
	}

	private final Context mContext;
	private final MediaSQLiteOpenHelper mDatabase;
	
	private Thread mScanningThread;

	private MediaManager(Context context) {
		mContext = context.getApplicationContext();
		mDatabase = new MediaSQLiteOpenHelper(mContext);
	}

	public void startScanMedia() {
		synchronized(mRunScanning) {
			if (mScanningThread == null) {
				mScanningThread = new Thread(mRunScanning, "media_scanner");
				mScanningThread.start();
			}
		}
	}
	
	private final Runnable mRunScanning = new Runnable() {
		
		@Override
		public void run() {
			List<MediaEntry> list = new ArrayList<MediaEntry>();
			listRecordFiles(list);
			for (MediaEntry m : list) {
				m.retrieveMetadata();
			}			
			// Sync. database
			mDatabase.updateMediaDatabase(list);			
			synchronized(this) {
				mScanningThread = null;
			}
		}
	};

	// ======== Database: Recorded Medias ========
	public boolean listRecordFiles(List<MediaEntry> dst) {
		File pvr = TvManagerHelper.getInstance(mContext).getPvrStorage();
		File dir = new File(pvr, DIR_REC);

		if (!dir.exists() || !dir.isDirectory()) {
			return false;
		}
		dst.clear();
		File[] files = dir.listFiles();
		if (files != null) {
			for (File f : files) {
				MediaEntry e = createFileEntry(f);
				if (e != null) {
					mDatabase.retrieveMediaProperties(e);
					dst.add(e);
				}
			}
		}
		return true;
	}

	public boolean setItemLocked(MediaEntry mediaEntry, boolean locked) {
		return mDatabase.setMediaItemLocked(mediaEntry.getFile().getAbsolutePath(), locked);
	}

	public boolean deleteRecordedItem(MediaEntry mediaEntry) {
		if (mediaEntry.isLocked()) {
			return false;
		}
		
		// Delete file
		if(!mediaEntry.getFile().delete()) {
			return false;
		}
		
		// Delete entry in the database
		mDatabase.removeMediaItem(mediaEntry.getPath());
		return true;
	}
	
	private MediaEntry createFileEntry(File f) {
		// NoProgName_20140520_1018_R00.ts
		String fileName = f.getName();

		String[] segs = fileName.split("_");
		final int segCount = segs.length;
		if (segs.length < 4) {
			Log.e(TAG, "Invalid file name: " + fileName);
			return null;
		}

		// File name must end with R00.ts
		if (!segs[segs.length - 1].equals("R00.ts")) {
			return null;
		}

		// Get program name
		String name = segs[0];
		for (int i = 1; i < segCount - 3; i++) {
			name += " " + segs[i];
		}

		// Get date
		Date date = null;
		try {
			date = MEDIA_DATE_FORMAT.parse(segs[segCount - 3] + segs[segCount - 2]);
		} catch (ParseException e) {
			Log.e(TAG, "Invalid date format: " + fileName);
			return null;
		}

		return new MediaEntry(this, f, name, date);
	}

	public void deleteRecordedItems(String[] paths) {
		for (String path : paths) {
			// Delete file
			File f = new File(path);
			if (f.exists()) {
				if (!f.delete()) {
					Log.e(TAG, "Failed to delete file:" + f.getAbsolutePath());
				}
			}

			// Remove entry from database
			mDatabase.removeMediaItem(path);

		}
	}

	// ======== Database: Schedules ========
	public boolean listRecordSchedules(List<ScheduleEntry> dst) {
		dst.clear();
		mDatabase.listRecordSchedules(dst);

		return true;
	}
	
	public int addRecordSchedule(Bundle args, boolean replace) {
		int channelNumber = args.getInt(EXTRA_CHANNEL_NUMBER);
		long startTime = args.getLong(EXTRA_START_TIME);
		long endTime = args.getLong(EXTRA_END_TIME);
		int repeat = args.getInt(EXTRA_REPEAT, REPEAT_ONCE);
		int source = args.getInt(EXTRA_LIVE_SOURCE);
		String programName = args.getString(EXTRA_PROGRAM_NAME);
		
		// Added to database
		int count = mDatabase.addRecordSchedule(replace, source, channelNumber, startTime, endTime, repeat, programName);
		
		// Set alarm
		if (count == 1) {
			AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(ACTION_ALARM_RECORD_START);
			intent.putExtras(args);
			intent.setData(createUri(source, channelNumber, startTime, endTime, repeat));
			PendingIntent operation = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);
			switch(repeat) {
				case REPEAT_DAILY:
					am.setRepeating(AlarmManager.RTC_WAKEUP, startTime, DateUtils.DAY_IN_MILLIS, operation);
					break;
				case REPEAT_WEEKLY:
					am.setRepeating(AlarmManager.RTC_WAKEUP, startTime, DateUtils.WEEK_IN_MILLIS, operation);
					break;
				case REPEAT_ONCE:
				default:
					am.setExact(AlarmManager.RTC_WAKEUP, startTime, operation);
					break;
			}
		}
		return count;
	}

	public void deleteRecordSchedule(Bundle args) {
		int source = args.getInt(EXTRA_LIVE_SOURCE);
		int channelNumber = args.getInt(EXTRA_CHANNEL_NUMBER);
		long startTime = args.getLong(MediaManager.EXTRA_START_TIME);
		long endTime = args.getLong(MediaManager.EXTRA_END_TIME);
		int repeat = args.getInt(EXTRA_REPEAT);
		
		// Remove from database
		int count = mDatabase.removeRecordSchedule(channelNumber, startTime, endTime);
		Log.v(TAG, "Delete record schedule: count=" + count);
		
		// Remove from alarm manager
		Intent intent = new Intent(ACTION_ALARM_RECORD_START);
		intent.putExtras(args);
		intent.setData(createUri(source, channelNumber, startTime, endTime, repeat));
		PendingIntent operation = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);
		AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		am.cancel(operation);
	}

	public void resetAlarms() {
		Log.i(TAG, "Reset recording alarms..");
		List<ScheduleEntry> alarms = new ArrayList<ScheduleEntry>();
		listRecordSchedules(alarms);
		
		final AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		final long now = TvManagerHelper.getInstance(mContext).currentTvTimeMillis();
		
		int count = 0;
		for (ScheduleEntry s : alarms) {
			long start = s.startTime;
			if (start >= now || s.repeat != REPEAT_ONCE) {
				if (s.status != STAT_SCHEDULE_ADDED && s.repeat == REPEAT_ONCE) {
					Log.e(TAG, "Invalid recording status: " + s);
					continue;
				}
				
				// create argument
				Bundle args = createArguments(s.source, s.channelNumber, null, s.programName,
						s.startTime, s.endTime, s.repeat);
				
				// create intents
				Intent intent = new Intent(ACTION_ALARM_RECORD_START);
				intent.putExtras(args);
				intent.setData(createUri(s.source, s.channelNumber, s.startTime, s.endTime, s.repeat));
				PendingIntent operation = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);
				
				// Cancel previous
				am.cancel(operation);
				
				// Set new alarm
				switch(s.repeat) {
					case REPEAT_DAILY:
						am.setRepeating(AlarmManager.RTC_WAKEUP, s.startTime, DateUtils.DAY_IN_MILLIS, operation);
						break;
					case REPEAT_WEEKLY:
						am.setRepeating(AlarmManager.RTC_WAKEUP, s.startTime, DateUtils.WEEK_IN_MILLIS, operation);
						break;
					case REPEAT_ONCE:
					default:
						am.setExact(AlarmManager.RTC_WAKEUP, s.startTime, operation);
						break;
				}
				count++;
			}
		}
		Log.i(TAG, "Reset record alarms: " + count + " alarms set.");
	}

	
	public int getRecordScheduleStatus(Bundle args) {
		int channel = args.getInt(EXTRA_CHANNEL_NUMBER);
		long start = args.getLong(EXTRA_START_TIME);
		long end = args.getLong(EXTRA_END_TIME);
		
		int stat = mDatabase.getRecordScheduleStatus(channel, start, end);
		return stat;
	}

	public void updateRecordScheduleStatus(Bundle args, int status) {
		int channelNumber = args.getInt(EXTRA_CHANNEL_NUMBER);
		long startTime = args.getLong(EXTRA_START_TIME);
		long endTime = args.getLong(EXTRA_END_TIME);
		
		int rows = mDatabase.updateRecordScheduleStatus(channelNumber, startTime, endTime, status);
		if (rows != 1) {
			Log.w(TAG, String.format("updateScheduleStatus: %d rows update with status %d", rows, status));
		}
	}
	
	// ================ Operations from UI interfaces ========
	
	public boolean startRecordCurrentChannel(Bundle args) {
		// Start record
		TvManagerHelper tm = TvManagerHelper.getInstance(mContext);
		boolean b = tm.startRecord();
		
		// Failed to start record
		if (!b) {
			// TODO: handle this
			updateRecordScheduleStatus(args, STAT_SCHEDULE_FAILED);
			Toast.makeText(mContext, R.string.msg_failed_to_start_record, Toast.LENGTH_SHORT).show();
			
		} else {
			updateRecordScheduleStatus(args, STAT_SCHEDULE_RECORDING);
			
			// Succeed: Setup stop alarm
			int source = args.getInt(EXTRA_LIVE_SOURCE);
			int channel = args.getInt(EXTRA_CHANNEL_NUMBER);
			long startTime = args.getLong(EXTRA_START_TIME);
			long endTime = args.getLong(EXTRA_END_TIME);
			int repeats = args.getInt(EXTRA_REPEAT);
			Uri uri = createUri(source, channel, startTime, endTime, repeats);
			if (VERBOSE) {
				Log.v(TAG, "Setup record stop alarm at " + new Date(endTime).toString());
			}
			AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(ACTION_ALARM_RECORD_STOP);
			intent.setData(uri);
			intent.putExtras(args);
			PendingIntent operation = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);
			am.setExact(AlarmManager.RTC_WAKEUP, endTime, operation);
			
			Toast.makeText(mContext, R.string.msg_recording_started, Toast.LENGTH_LONG).show();
		}
		return b;
	}
	
	public void stopRecord() {
		TvManagerHelper tm = TvManagerHelper.getInstance(mContext);		
		if (!tm.isPvrTimeShiftEnabled()) {
			if (tm.isRecording()) {
				boolean b = tm.stopRecord();
				if (!b) {
					Log.e(TAG, " fail to stop Record");
				}
			}
		}
	}

	public ScheduleEntry getNextRecordSchedule() {
		List<ScheduleEntry> list = new ArrayList<ScheduleEntry>();
		listRecordSchedules(list);
		long now = System.currentTimeMillis();
		ScheduleEntry e = null;
		long time = Long.MAX_VALUE;
		for (ScheduleEntry s : list) {
			long start = s.startTime;
			if (start > now && start < time) {
				time = start;
				e = s;
			}
		}
		return e;
	}

}
