package com.rtk.tv.data;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import com.rtk.tv.utils.TvUtil;
import android.database.Cursor;
import android.media.tv.TvContract;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class ChannelInfo implements Parcelable{
	private static String Programs ="PROGRAMS";
	int index;
	long channelId;
	String inputId;
	String type;
	String serviceType;
	String displayNumber;
	String displayName;
	String description;
	int originalNetworkId;
	int transportStreamId;
	int serviceId;
	String videoFormat;
	int searchable;
	int version;	
	ProgramInfo[] programs;
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public long getChannelId() {
		return channelId;
	}
	public void setChannelId(long channelId) {
		this.channelId = channelId;
	}
	public String getInputId() {
		return inputId;
	}
	public void setInputId(String inputId) {
		this.inputId = inputId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDisplayNumber() {
		return displayNumber;
	}
	public void setDisplayNumber(String displayNumber) {
		this.displayNumber = displayNumber;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String diaplayName) {
		this.displayName = diaplayName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public int getOriginalNetworkId() {
		return originalNetworkId;
	}
	public void setOriginalNetworkId(int originalNetworkId) {
		this.originalNetworkId = originalNetworkId;
	}
	public int getTransportStreamId() {
		return transportStreamId;
	}
	public void setTransportStreamId(int transportStreamId) {
		this.transportStreamId = transportStreamId;
	}
	public int getServiceId() {
		return serviceId;
	}
	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}
	public String getVideoFormat() {
		return videoFormat;
	}
	public void setVideoFormat(String videoFormat) {
		this.videoFormat = videoFormat;
	}
	public int getSearchable() {
		return searchable;
	}
	public void setSearchable(int searchable) {
		this.searchable = searchable;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	
	public ProgramInfo[] getPrograms() {
		return programs;
	}
	public void setPrograms(ProgramInfo[] programs) {
		this.programs = programs;
	}
	
	public ProgramInfo getProgramAt(int pos) {
		if(programs== null || programs.length == 0)
			return null;
		if(pos>programs.length-1)
			return null;
		return programs[pos];
	}
	
	public static ChannelInfo[] buildChannels(Cursor cursor){
		if (cursor == null)
			return null;
		ChannelInfo[] channels = new ChannelInfo[cursor.getCount()];
		int index = 0;
		while (cursor.moveToNext()) {
			ChannelInfo info = new ChannelInfo();
			info.setChannelId(cursor.getLong(
					cursor.getColumnIndex(TvContract.Channels._ID)));
			info.setType(cursor.getString(
					cursor.getColumnIndex(TvContract.Channels.COLUMN_TYPE)));
			info.setInputId(cursor.getString(
					cursor.getColumnIndex(TvContract.Channels.COLUMN_INPUT_ID)));
			info.setDisplayNumber(cursor.getString(
					cursor.getColumnIndex(TvContract.Channels.COLUMN_DISPLAY_NUMBER)));
			info.setDisplayName(cursor.getString(
					cursor.getColumnIndex(TvContract.Channels.COLUMN_DISPLAY_NAME)));
			info.setDescription(cursor.getString(
					cursor.getColumnIndex(TvContract.Channels.COLUMN_DESCRIPTION)));
			
			info.setServiceType(cursor.getString(
					cursor.getColumnIndex(TvContract.Channels.COLUMN_SERVICE_TYPE)));
			info.setOriginalNetworkId(cursor.getInt(
					cursor.getColumnIndex(TvContract.Channels.COLUMN_ORIGINAL_NETWORK_ID)));
			info.setTransportStreamId(cursor.getInt(
					cursor.getColumnIndex(TvContract.Channels.COLUMN_TRANSPORT_STREAM_ID)));
			info.setServiceId(cursor.getInt(
					cursor.getColumnIndex(TvContract.Channels.COLUMN_SERVICE_ID)));
			info.setVideoFormat(cursor.getString(
					cursor.getColumnIndex(TvContract.Channels.COLUMN_VIDEO_FORMAT)));
			info.setSearchable(cursor.getInt(
					cursor.getColumnIndex(TvContract.Channels.COLUMN_SEARCHABLE)));
			info.setVersion(cursor.getInt(
					cursor.getColumnIndex(TvContract.Channels.COLUMN_VERSION_NUMBER)));
			info.setIndex(index);
			channels[index++] = info;
		}
		return channels;
	}
	
	public String formatNumber(String format) {
		return formatNumber(format, Locale.getDefault());
	}

	public String formatNumber(String format, Locale locale) {
		return String.format(locale,  format, channelId);
	}
	
	public ProgramInfo findLatestProgram(long time){
		if(programs == null || programs.length == 0)
			return null;
		for(ProgramInfo info :programs){
			if(time>=info.getStartTimeUtcMillis() && time <= info.getEndTimeUtcMillis())
				return info;
		}
		return null;
	}
	
	private Date mBaseDate;
	/**
	 * Get the date of this Programs.
	 * The time will be set to the begin of that day.
	 * @return The date of this Channel or {@code null} if no data available.
	 */
	public Date getBaseTime() {
		if (mBaseDate == null && programs != null && programs.length > 0) {
			ProgramInfo p = programs[0];
			Calendar c = Calendar.getInstance();
			if (p.isCrossDay()) {
				c.setTime(TvUtil.translateTvTime(p.getEndTimeUtcMillis()));
			} else {
				c.setTime(TvUtil.translateTvTime(p.getStartTimeUtcMillis()));
			}
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			mBaseDate = c.getTime();
		}
		return mBaseDate;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		Bundle bundle = new Bundle();
		bundle.putLong(TvContract.Channels._ID,channelId);
		bundle.putString(TvContract.Channels.COLUMN_TYPE,type);
		bundle.putString(TvContract.Channels.COLUMN_INPUT_ID,inputId);
		bundle.putString(TvContract.Channels.COLUMN_DISPLAY_NUMBER,displayNumber);
		bundle.putString(TvContract.Channels.COLUMN_DISPLAY_NAME,displayName);
		bundle.putString(TvContract.Channels.COLUMN_DESCRIPTION,description);
		bundle.putString(TvContract.Channels.COLUMN_SERVICE_TYPE,serviceType);
		bundle.putInt(TvContract.Channels.COLUMN_ORIGINAL_NETWORK_ID,originalNetworkId);
		bundle.putInt(TvContract.Channels.COLUMN_TRANSPORT_STREAM_ID,transportStreamId);
		bundle.putInt(TvContract.Channels.COLUMN_SERVICE_ID,serviceId);
		bundle.putString(TvContract.Channels.COLUMN_VIDEO_FORMAT,videoFormat);
		bundle.putInt(TvContract.Channels.COLUMN_SEARCHABLE,searchable);
		bundle.putInt(TvContract.Channels.COLUMN_VERSION_NUMBER,version);
		bundle.putInt("INDEX",index);
		bundle.putParcelableArray(Programs, programs);
		dest.writeBundle(bundle);
	}
	
	public static final Parcelable.Creator<ChannelInfo> CREATOR = new Parcelable.Creator<ChannelInfo>() {

		@Override
		public ChannelInfo createFromParcel(Parcel source) {
			ChannelInfo info = new ChannelInfo();
			Bundle bundle = new Bundle();
			bundle = source.readBundle();
			
			info.setChannelId(bundle.getLong(TvContract.Channels._ID));
			info.setType(bundle.getString(TvContract.Channels.COLUMN_TYPE));
			info.setInputId(bundle.getString(TvContract.Channels.COLUMN_INPUT_ID));
			info.setDisplayNumber(bundle.getString(TvContract.Channels.COLUMN_DISPLAY_NUMBER));
			info.setDisplayName(bundle.getString(TvContract.Channels.COLUMN_DISPLAY_NAME));
			info.setDescription(bundle.getString(TvContract.Channels.COLUMN_DESCRIPTION));			
			info.setServiceType(bundle.getString(TvContract.Channels.COLUMN_SERVICE_TYPE));
			info.setOriginalNetworkId(bundle.getInt(TvContract.Channels.COLUMN_ORIGINAL_NETWORK_ID));
			info.setTransportStreamId(bundle.getInt(TvContract.Channels.COLUMN_TRANSPORT_STREAM_ID));
			info.setServiceId(bundle.getInt(TvContract.Channels.COLUMN_SERVICE_ID));
			info.setVideoFormat(bundle.getString(TvContract.Channels.COLUMN_VIDEO_FORMAT));
			info.setSearchable(bundle.getInt(TvContract.Channels.COLUMN_SEARCHABLE));
			info.setVersion(bundle.getInt(TvContract.Channels.COLUMN_VERSION_NUMBER));
			
			info.setIndex(bundle.getInt("INDEX"));	
			info.setPrograms((ProgramInfo[])bundle.getParcelableArray(Programs));
			return info;
		}

		@Override
		public ChannelInfo[] newArray(int size) {
			return new ChannelInfo[size];
		}
	};
}
