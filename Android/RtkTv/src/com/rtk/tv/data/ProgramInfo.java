package com.rtk.tv.data;

import android.database.Cursor;
import android.media.tv.TvContract;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class ProgramInfo implements Parcelable{
	int index;
	long id;
	String packageName;
	int channelId;
	String title;
	int seasonNumber;
	int episodeNumber;
	String episodeTitle;
	long startTimeUtcMillis;
	long endTimeUtcMillis;
	String broadcastGenre;
	String canonicalGenre;
	String shortDescription;
	String longDescription;
	int videoWidth;
	int videoHeight;
	String audioLanguage;
	String contentRating;
	String posterArtUri;
	String thumbnailUri;
	int versionNumber;

	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public int getChannelId() {
		return channelId;
	}

	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getSeasonNumber() {
		return seasonNumber;
	}

	public void setSeasonNumber(int seasonNumber) {
		this.seasonNumber = seasonNumber;
	}

	public int getEpisodeNumber() {
		return episodeNumber;
	}

	public void setEpisodeNumber(int episodeNumber) {
		this.episodeNumber = episodeNumber;
	}

	public String getEpisodeTitle() {
		return episodeTitle;
	}

	public void setEpisodeTitle(String episodeTitle) {
		this.episodeTitle = episodeTitle;
	}

	public long getStartTimeUtcMillis() {
		return startTimeUtcMillis;
	}

	public void setStartTimeUtcMillis(long startTimeUtcMillis) {
		this.startTimeUtcMillis = startTimeUtcMillis;
	}

	public long getEndTimeUtcMillis() {
		return endTimeUtcMillis;
	}

	public void setEndTimeUtcMillis(long endTimeUtcMillis) {
		this.endTimeUtcMillis = endTimeUtcMillis;
	}

	public String getBroadcastGenre() {
		return broadcastGenre;
	}

	public void setBroadcastGenre(String broadcastGenre) {
		this.broadcastGenre = broadcastGenre;
	}

	public String getCanonicalGenre() {
		return canonicalGenre;
	}

	public void setCanonicalGenre(String canonicalGenre) {
		this.canonicalGenre = canonicalGenre;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getLongDescription() {
		return longDescription;
	}

	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	public int getVideoWidth() {
		return videoWidth;
	}

	public void setVideoWidth(int videoWidth) {
		this.videoWidth = videoWidth;
	}

	public int getVideoHeight() {
		return videoHeight;
	}

	public void setVideoHeight(int videoHeight) {
		this.videoHeight = videoHeight;
	}

	public String getAudioLanguage() {
		return audioLanguage;
	}

	public void setAudioLanguage(String audioLanguage) {
		this.audioLanguage = audioLanguage;
	}

	public String getContentRating() {
		return contentRating;
	}

	public void setContentRating(String contentRating) {
		this.contentRating = contentRating;
	}

	public String getPosterArtUri() {
		return posterArtUri;
	}

	public void setPosterArtUri(String posterArtUri) {
		this.posterArtUri = posterArtUri;
	}

	public String getThumbnailUri() {
		return thumbnailUri;
	}

	public void setThumbnailUri(String thumbnailUri) {
		this.thumbnailUri = thumbnailUri;
	}

	public int getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(int versionNumber) {
		this.versionNumber = versionNumber;
	}

	public static ProgramInfo[] buildPrograms(Cursor cursor) {
		if (cursor == null)
			return null;
		ProgramInfo[] programs = new ProgramInfo[cursor.getCount()];
		int index = 0;
		while (cursor.moveToNext()) {
			ProgramInfo info = new ProgramInfo();
			info.setId(cursor.getLong(cursor
					.getColumnIndex(TvContract.Programs._ID)));
			info.setPackageName(cursor.getString(cursor
					.getColumnIndex(TvContract.Programs.COLUMN_PACKAGE_NAME)));
			info.setChannelId(cursor.getInt(cursor
					.getColumnIndex(TvContract.Programs.COLUMN_CHANNEL_ID)));
			info.setTitle(cursor.getString(cursor
					.getColumnIndex(TvContract.Programs.COLUMN_TITLE)));
			info.setSeasonNumber(cursor.getInt(cursor
					.getColumnIndex(TvContract.Programs.COLUMN_SEASON_NUMBER)));
			info.setEpisodeNumber(cursor.getInt(cursor
					.getColumnIndex(TvContract.Programs.COLUMN_EPISODE_NUMBER)));
			info.setEpisodeTitle(cursor.getString(cursor
					.getColumnIndex(TvContract.Programs.COLUMN_EPISODE_TITLE)));
			info.setStartTimeUtcMillis(cursor.getLong(cursor
					.getColumnIndex(TvContract.Programs.COLUMN_START_TIME_UTC_MILLIS)));
			info.setEndTimeUtcMillis(cursor.getLong(cursor
					.getColumnIndex(TvContract.Programs.COLUMN_END_TIME_UTC_MILLIS)));
			info.setBroadcastGenre(cursor.getString(cursor
					.getColumnIndex(TvContract.Programs.COLUMN_BROADCAST_GENRE)));
			info.setCanonicalGenre(cursor.getString(cursor
					.getColumnIndex(TvContract.Programs.COLUMN_CANONICAL_GENRE)));
			info.setShortDescription(cursor.getString(cursor
					.getColumnIndex(TvContract.Programs.COLUMN_SHORT_DESCRIPTION)));
			info.setLongDescription(cursor.getString(cursor
					.getColumnIndex(TvContract.Programs.COLUMN_LONG_DESCRIPTION)));
			info.setVideoWidth(cursor.getInt(cursor
					.getColumnIndex(TvContract.Programs.COLUMN_VIDEO_WIDTH)));
			info.setVideoHeight(cursor.getInt(cursor
					.getColumnIndex(TvContract.Programs.COLUMN_VIDEO_HEIGHT)));
			info.setAudioLanguage(cursor.getString(cursor
					.getColumnIndex(TvContract.Programs.COLUMN_AUDIO_LANGUAGE)));
			info.setContentRating(cursor.getString(cursor
					.getColumnIndex(TvContract.Programs.COLUMN_CONTENT_RATING)));
			info.setPosterArtUri(cursor.getString(cursor
					.getColumnIndex(TvContract.Programs.COLUMN_POSTER_ART_URI)));
			info.setThumbnailUri(cursor.getString(cursor
					.getColumnIndex(TvContract.Programs.COLUMN_THUMBNAIL_URI)));
			info.setVersionNumber(cursor.getInt(cursor
					.getColumnIndex(TvContract.Programs.COLUMN_VERSION_NUMBER)));
			info.setIndex(index);
			programs[index++] = info;
		}
		return programs;
	}
	
	public boolean isExpired(long now) {
		return now > endTimeUtcMillis;
	}
	
	public boolean isPlaying(long now) {
		return now >= startTimeUtcMillis && now <= endTimeUtcMillis;
	}
	
	public boolean isCrossDay() {
		long s = startTimeUtcMillis / 86400000L;
		long e = endTimeUtcMillis / 86400000L;
		return s != e;
	}
	
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		Bundle bundle = new Bundle();
		bundle.putLong(TvContract.Programs._ID,id);
		bundle.putString(TvContract.Programs.COLUMN_PACKAGE_NAME,packageName);
		bundle.putInt(TvContract.Programs.COLUMN_CHANNEL_ID,channelId);
		bundle.putString(TvContract.Programs.COLUMN_TITLE,title);
		bundle.putInt(TvContract.Programs.COLUMN_SEASON_NUMBER,seasonNumber);
		bundle.putInt(TvContract.Programs.COLUMN_EPISODE_NUMBER,episodeNumber);
		bundle.putString(TvContract.Programs.COLUMN_EPISODE_TITLE,episodeTitle);
		bundle.putLong(TvContract.Programs.COLUMN_START_TIME_UTC_MILLIS,startTimeUtcMillis);
		bundle.putLong(TvContract.Programs.COLUMN_END_TIME_UTC_MILLIS,endTimeUtcMillis);
		bundle.putString(TvContract.Programs.COLUMN_BROADCAST_GENRE,broadcastGenre);
		bundle.putString(TvContract.Programs.COLUMN_CANONICAL_GENRE,canonicalGenre);
		bundle.putString(TvContract.Programs.COLUMN_SHORT_DESCRIPTION,shortDescription);
		bundle.putString(TvContract.Programs.COLUMN_LONG_DESCRIPTION,longDescription);
		bundle.putInt(TvContract.Programs.COLUMN_VIDEO_WIDTH,videoWidth);
		bundle.putInt(TvContract.Programs.COLUMN_VIDEO_HEIGHT,videoHeight);
		bundle.putString(TvContract.Programs.COLUMN_AUDIO_LANGUAGE,audioLanguage);
		bundle.putString(TvContract.Programs.COLUMN_CONTENT_RATING,contentRating);
		bundle.putString(TvContract.Programs.COLUMN_POSTER_ART_URI,posterArtUri);
		bundle.putString(TvContract.Programs.COLUMN_THUMBNAIL_URI,thumbnailUri);
		bundle.putInt(TvContract.Programs.COLUMN_VERSION_NUMBER,versionNumber);
		bundle.putInt("INDEX",index);
		dest.writeBundle(bundle);
	}
	
	public static final Parcelable.Creator<ProgramInfo> CREATOR = new Parcelable.Creator<ProgramInfo>() {

		@Override
		public ProgramInfo createFromParcel(Parcel source) {
			ProgramInfo info = new ProgramInfo();
			Bundle bundle = new Bundle();
			bundle = source.readBundle();			
			info.setId(bundle.getLong(TvContract.Programs._ID));			
			info.setPackageName(bundle.getString(TvContract.Programs.COLUMN_PACKAGE_NAME));
			info.setChannelId(bundle.getInt(TvContract.Programs.COLUMN_CHANNEL_ID));
			info.setTitle(bundle.getString(TvContract.Programs.COLUMN_TITLE));
			info.setSeasonNumber(bundle.getInt(TvContract.Programs.COLUMN_SEASON_NUMBER));
			info.setEpisodeNumber(bundle.getInt(TvContract.Programs.COLUMN_EPISODE_NUMBER));
			info.setEpisodeTitle(bundle.getString(TvContract.Programs.COLUMN_EPISODE_TITLE));
			info.setStartTimeUtcMillis(bundle.getLong(TvContract.Programs.COLUMN_START_TIME_UTC_MILLIS));
			info.setEndTimeUtcMillis(bundle.getLong(TvContract.Programs.COLUMN_END_TIME_UTC_MILLIS));
			info.setBroadcastGenre(bundle.getString(TvContract.Programs.COLUMN_BROADCAST_GENRE));
			info.setCanonicalGenre(bundle.getString(TvContract.Programs.COLUMN_CANONICAL_GENRE));
			info.setShortDescription(bundle.getString(TvContract.Programs.COLUMN_SHORT_DESCRIPTION));
			info.setLongDescription(bundle.getString(TvContract.Programs.COLUMN_LONG_DESCRIPTION));
			info.setVideoWidth(bundle.getInt(TvContract.Programs.COLUMN_VIDEO_WIDTH));
			info.setVideoHeight(bundle.getInt(TvContract.Programs.COLUMN_VIDEO_HEIGHT));
			info.setAudioLanguage(bundle.getString(TvContract.Programs.COLUMN_AUDIO_LANGUAGE));
			info.setContentRating(bundle.getString(TvContract.Programs.COLUMN_CONTENT_RATING));
			info.setPosterArtUri(bundle.getString(TvContract.Programs.COLUMN_POSTER_ART_URI));
			info.setThumbnailUri(bundle.getString(TvContract.Programs.COLUMN_THUMBNAIL_URI));
			info.setVersionNumber(bundle.getInt(TvContract.Programs.COLUMN_VERSION_NUMBER));			
			info.setIndex(bundle.getInt("INDEX"));			
			return info;
		}

		@Override
		public ProgramInfo[] newArray(int size) {
			return new ProgramInfo[size];
		}
	};
}
