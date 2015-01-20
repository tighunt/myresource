package com.rtk.tv.media;

import android.os.Bundle;
import android.widget.Checkable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScheduleEntry implements Checkable {
	
	private static final String TAG = "ScheduleEntry";

	//
	public final int source;
	public final int channelNumber;
	public final long startTime;
	public final long endTime;
	public final int repeat;
	public final String programName;
	public final int status;
	
	// SQL properties
	private boolean mLocked;
	private long mDuration;
	
	// User status
	private boolean mChecked = false;


	public ScheduleEntry(int source, int channelNumber, long startTime, long endTime, int repeat, String programName, int status) {
		this.source = source;
		this.channelNumber = channelNumber;
		this.startTime = startTime;
		this.endTime = endTime;
		this.repeat = repeat;
		this.programName = programName;
		this.status = status;
	}

	@Override
	public String toString() {
		DateFormat df = SimpleDateFormat.getInstance();
		return String.format("RecordSchedule {" +
				"source=%d, channel=%d, start=%s, end=%s, repeat=%d, status=%d}", 
				source, channelNumber, df.format(new Date(startTime)), df.format(new Date(endTime)), repeat, status);
	}

	public boolean isChecked() {
		return mChecked;
	}

	@Override
	public void setChecked(boolean checked) {
		mChecked = checked;
	}

	@Override
	public void toggle() {
		mChecked = !mChecked;
	}

	public long getDuration() {
		return endTime - startTime;
	}

	public Bundle toBundle() {
		return MediaManager.createArguments(source, channelNumber, null, programName, startTime, endTime, repeat);
	}
}