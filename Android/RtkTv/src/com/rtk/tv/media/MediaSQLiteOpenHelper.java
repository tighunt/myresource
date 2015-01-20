
package com.rtk.tv.media;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

class MediaSQLiteOpenHelper extends SQLiteOpenHelper {

	private static final String TAG = "MediaSQLite";

	private static final String SQL_NAME = "media";

	private static final String TABLE_RECORD_MEDIA = "records";
	
	private static final String TABLE_RECORD_SCHEDULE = "schedules";

	private static final String COLUMN_ID = "_id";

	private static final String COLUMN_PATH = "_path";

	private static final String COLUMN_LOCKED = "_locked";

	private static final String COLUMN_DURATION = "_duration";
	
	private static final String COLUMN_PROGRAM_NAME = "_program";
	
	private static final String COLUMN_SOURCE = "_source";
	
	private static final String COLUMN_CHANNEL_NUMBER = "_channel";
	
	private static final String COLUMN_START_TIME = "_start_time";

	private static final String COLUMN_END_TIME = "_end_time";
	
	private static final String COLUMN_REPEAT = "_repeat";
	
	private static final String COLUMN_STATUS = "_status";
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

	
	public MediaSQLiteOpenHelper(Context context) {
		super(context, SQL_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// Recorded Media Table
		db.execSQL(
				String.format(Locale.US, "create table %s(" +
						"%s integer primary key autoincrement," +
						"%s text," +
						"%s integer," +
						"%s integer);",
						TABLE_RECORD_MEDIA,
						COLUMN_ID,
						COLUMN_PATH,
						COLUMN_LOCKED,
						COLUMN_DURATION));
		
		// Record Schedule Table
		db.execSQL(
				String.format(Locale.US, "create table %s(" +
						"%s integer primary key autoincrement," +
						"%s integer," + // live source
						"%s integer," + // channel number
						"%s text," +    // program name
						"%s integer," + // start time
						"%s integer," + // end time
						"%s integer," + // repeat
						"%s integer);", // status
						TABLE_RECORD_SCHEDULE,
						COLUMN_ID,
						COLUMN_SOURCE,
						COLUMN_CHANNEL_NUMBER,
						COLUMN_PROGRAM_NAME,
						COLUMN_START_TIME,
						COLUMN_END_TIME,
						COLUMN_REPEAT,
						COLUMN_STATUS));
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
	
	boolean retrieveMediaProperties(MediaEntry entry) {
		Cursor cursor = getReadableDatabase().query(TABLE_RECORD_MEDIA, new String[] {
				COLUMN_LOCKED,
				COLUMN_DURATION
			}, COLUMN_PATH + "=?", new String[] {
				entry.getPath()
			}, null, null, null);
		if (cursor.moveToFirst()) {
			entry.updateProperties(
				cursor.getInt(0) > 0,
				cursor.getLong(1)
			);
			return true;
		}
		return false;
	}

	/**
	 * Sync. the items by the list.
	 * Media properties in the list are saved to database.
	 * @param list
	 */
	void updateMediaDatabase(List<MediaEntry> list) {
		Log.i(TAG, "Start update database");
		SQLiteDatabase database = getWritableDatabase();
		for (MediaEntry e : list) {
			String path = e.getPath();
			Cursor cursor = database.query(
					TABLE_RECORD_MEDIA, new String[] {COLUMN_ID},
					COLUMN_PATH + "=?", new String[] {path}, null, null, null);
			
			// Create new values
			ContentValues values = new ContentValues();
			values.put(COLUMN_DURATION, e.getDuration());
			values.put(COLUMN_PATH, path);
			
			if (cursor.moveToFirst()) {
				// Get row id
				long id = cursor.getLong(0);
				
				// Update values
				database.update(TABLE_RECORD_MEDIA, values, COLUMN_ID + "=" + id, null);
			} else {
				database.insert(TABLE_RECORD_MEDIA, null, values);
			}
		}
		database.close();
		Log.i(TAG, "End update database");
	}

	public boolean setMediaItemLocked(String path, boolean locked) {
		Cursor cursor = getReadableDatabase().query(
				TABLE_RECORD_MEDIA, new String[] {COLUMN_ID},
				COLUMN_PATH + "=?", new String[] {path}, null, null, null);
		
		if (cursor.moveToFirst()) {
			ContentValues values = new ContentValues();
			values.put(COLUMN_LOCKED, locked);
			int i = getWritableDatabase().update(TABLE_RECORD_MEDIA, values, COLUMN_PATH + "=?", new String[] {path});
			return i > 0;
		} else {
			ContentValues values = new ContentValues();
			values.put(COLUMN_PATH, path);
			values.put(COLUMN_LOCKED, locked);
			long id = getWritableDatabase().insert(TABLE_RECORD_MEDIA, null, values);
			return id >= 0;
		}
		
	}
	
	public boolean isMediaItemLocked(String path) {
		Cursor cursor = getReadableDatabase().query(TABLE_RECORD_MEDIA, new String[] {
				COLUMN_LOCKED
			}, COLUMN_PATH + "=?", new String[] {
				path
			}, null, null, null);
		if (cursor.moveToFirst()) {
			return cursor.getInt(0) > 0;
		}
		return false;
	}

	public boolean removeMediaItem(String path) {
		int delete = getWritableDatabase().delete(TABLE_RECORD_MEDIA, COLUMN_PATH + "=?", new String[]{path});
		return delete > 0;
	}

	// ======== Record Schedule
	public int addRecordSchedule(boolean replace, int source, int channelNumber, long startTime, long endTime, int repeat, String programName) {
		// Note: program name is optional
		
		String start = getDateTime(startTime);
		String end = getDateTime(endTime);
		
		// Only handle conflicts for one-time schedules.
		if (repeat == MediaManager.REPEAT_ONCE) {
			if (replace) {
				// Delete conflict
				getWritableDatabase().delete(TABLE_RECORD_SCHEDULE,
						COLUMN_START_TIME + "<? AND " + COLUMN_END_TIME +">? AND " + 
						COLUMN_REPEAT + "=?",
						new String[] {end, start, String.valueOf(MediaManager.REPEAT_ONCE)}
						);
				
			} else {
				// Check conflicts
				Cursor cursor = getReadableDatabase().query(TABLE_RECORD_SCHEDULE, new String[] {
						COLUMN_ID
				}, COLUMN_START_TIME + "<? AND " + 
						COLUMN_END_TIME + ">? AND " +
						COLUMN_REPEAT + "=?", new String[] {
						end, start, String.valueOf(MediaManager.REPEAT_ONCE)
				}, null, null, null);
				int count = cursor.getCount();
				if (count > 0) {
					return -count;
				}
			}
		}
		
		
		// Add to database
		ContentValues values = new ContentValues();
		values.put(COLUMN_SOURCE, source);
		values.put(COLUMN_CHANNEL_NUMBER, channelNumber);
		values.put(COLUMN_START_TIME, start);
		values.put(COLUMN_END_TIME, end);
		values.put(COLUMN_REPEAT, repeat);
		values.put(COLUMN_PROGRAM_NAME, programName);
		values.put(COLUMN_STATUS, MediaManager.STAT_SCHEDULE_ADDED);
		getWritableDatabase().insert(TABLE_RECORD_SCHEDULE, null, values);
		return 1;
	}

	public int removeRecordSchedule(int channelIdx, long startTime, long endTime) {
		String start = getDateTime(startTime);
		String end = getDateTime(endTime);
		
		// Delete entry
		int count = getWritableDatabase().delete(TABLE_RECORD_SCHEDULE,
				COLUMN_CHANNEL_NUMBER + "=? AND " +
				COLUMN_START_TIME + "=? AND " + 
				COLUMN_END_TIME +"=?",
				new String[] {String.valueOf(channelIdx), start, end}
		);

		if (count == 0) {
			Log.w(TAG, String.format("removeRecordSchedule: nothing removed," +
					"channel=%d, start=%s, end=%s", channelIdx, start, end));
		}
		return count;
	}

	public int getRecordScheduleStatus(int channel, long startTime, long endTime) {
		String start = getDateTime(startTime);
		String end = getDateTime(endTime);
		
		Cursor cursor = getReadableDatabase().query(TABLE_RECORD_SCHEDULE,
				new String[] {COLUMN_ID, COLUMN_STATUS},
				COLUMN_CHANNEL_NUMBER + "=? AND " +
				COLUMN_START_TIME + "=? AND " + 
				COLUMN_END_TIME +"=?",
				new String[] {String.valueOf(channel), start, end}, null, null, null);
		if (cursor.moveToFirst()) {
			return cursor.getInt(1);
		}
		
		return MediaManager.STAT_SCHEDULE_CANCELED;
	}

	public int updateRecordScheduleStatus(int channelNumber, long startTime, long endTime, int status) {
		String start = getDateTime(startTime);
		String end = getDateTime(endTime);
		
		ContentValues values = new ContentValues();
		values.put(COLUMN_STATUS, status);
		
		int rows = getWritableDatabase().update(TABLE_RECORD_SCHEDULE, values,
				COLUMN_CHANNEL_NUMBER + "=? AND " +
				COLUMN_START_TIME + "=? AND " + 
				COLUMN_END_TIME +"=?",
				new String[] {String.valueOf(channelNumber), start, end});
		
		return rows;
	}

	public void listRecordSchedules(List<ScheduleEntry> dst) {
		Cursor cursor = getReadableDatabase().query(TABLE_RECORD_SCHEDULE,
				new String[] {COLUMN_ID,
				COLUMN_SOURCE,
				COLUMN_CHANNEL_NUMBER,
				COLUMN_START_TIME, COLUMN_END_TIME,
				COLUMN_REPEAT,
				COLUMN_PROGRAM_NAME,
				COLUMN_STATUS},
				null, null, null, null, null);
		
		while (cursor.moveToNext()) {
			try {
				ScheduleEntry s = new ScheduleEntry(
						cursor.getInt(1),
						cursor.getInt(2),
						DATE_FORMAT.parse(cursor.getString(3)).getTime(),
						DATE_FORMAT.parse(cursor.getString(4)).getTime(),
						cursor.getInt(5),
						cursor.getString(6),
						cursor.getInt(7));
				dst.add(s);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	// ======== Utilities ========
	
	private static String getDateTime(long time) {
		Date date = new Date(time);
		return DATE_FORMAT.format(date);
	}

}
