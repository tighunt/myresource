package com.rtk.tv.media;

import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;
import android.widget.Checkable;

import java.io.File;
import java.util.Date;

public class MediaEntry implements Checkable {
	
	private static final String TAG = "MediaEntry";

	//
	private final MediaManager mManager;
	private final File mFile;
	private final String mName;
	private final Date mDate;
	
	// SQL properties
	private boolean mLocked;
	private long mDuration;
	
	// User status
	private boolean mChecked = false;

	MediaEntry(MediaManager manager, File file, String name, Date date) {
		mManager = manager;
		mFile = file;
		mName = name;
		mDate = date;
	}

	void updateProperties(boolean locked, long duration) {
		mLocked = locked;
		mDuration = duration;
	}
	
	public String getName() {
		return mName;
	}

	public Date getDate() {
		return mDate;
	}

	public File getFile() {
		return mFile;
	}

	public String getPath() {
		return mFile.getAbsolutePath();
	}

	@Override
	public String toString() {
		return mName;
	}

	public Uri getUri() {
		return Uri.fromFile(mFile);
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

	public boolean setLocked(boolean locked) {
		boolean success = mManager.setItemLocked(this, locked);
		if (success) {
			mLocked = locked;
		} else {
			Log.e(TAG, "Failed to lock/unlock file: " + mFile.getAbsolutePath());
		}
		return success;
	}

	public boolean isLocked() {
		return mLocked;
	}

	public boolean toggleLock() {
		return setLocked(!isLocked());
	}

	public boolean delete() {
		return mManager.deleteRecordedItem(this);
	}

	public long getDuration() {
		return mDuration;
	}

	void retrieveMetadata() {
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		try {
			retriever.setDataSource(getPath());
			String durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
			mDuration = Long.parseLong(durationStr);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}