package com.rtk.tv.utils;

import android.media.tv.TvContract;

public class Constants {
	public static final String[] CHANNELS_PROJECTION = {
        TvContract.Channels._ID,
        TvContract.Channels.COLUMN_INPUT_ID,
        TvContract.Channels.COLUMN_TYPE,
        TvContract.Channels.COLUMN_SERVICE_TYPE,
        TvContract.Channels.COLUMN_ORIGINAL_NETWORK_ID,
        TvContract.Channels.COLUMN_TRANSPORT_STREAM_ID,
        TvContract.Channels.COLUMN_SERVICE_ID,
        TvContract.Channels.COLUMN_DISPLAY_NUMBER,
        TvContract.Channels.COLUMN_DISPLAY_NAME,
        TvContract.Channels.COLUMN_NETWORK_AFFILIATION,
        TvContract.Channels.COLUMN_DESCRIPTION,
        TvContract.Channels.COLUMN_VIDEO_FORMAT,
        TvContract.Channels.COLUMN_INTERNAL_PROVIDER_DATA,
        TvContract.Channels.COLUMN_VERSION_NUMBER,
    };

    public static final String[] PROGRAMS_PROJECTION = {
        TvContract.Programs._ID,
        TvContract.Programs.COLUMN_CHANNEL_ID,
        TvContract.Programs.COLUMN_TITLE,
        TvContract.Programs.COLUMN_SEASON_NUMBER,
        TvContract.Programs.COLUMN_EPISODE_NUMBER,
        TvContract.Programs.COLUMN_EPISODE_TITLE,
        TvContract.Programs.COLUMN_START_TIME_UTC_MILLIS,
        TvContract.Programs.COLUMN_END_TIME_UTC_MILLIS,
        TvContract.Programs.COLUMN_BROADCAST_GENRE,
        TvContract.Programs.COLUMN_CANONICAL_GENRE,
        TvContract.Programs.COLUMN_SHORT_DESCRIPTION,
        TvContract.Programs.COLUMN_LONG_DESCRIPTION,
        TvContract.Programs.COLUMN_VIDEO_WIDTH,
        TvContract.Programs.COLUMN_VIDEO_HEIGHT,
        TvContract.Programs.COLUMN_AUDIO_LANGUAGE,
        TvContract.Programs.COLUMN_CONTENT_RATING,
        TvContract.Programs.COLUMN_POSTER_ART_URI,
        TvContract.Programs.COLUMN_THUMBNAIL_URI,
        TvContract.Programs.COLUMN_INTERNAL_PROVIDER_DATA,
        TvContract.Programs.COLUMN_VERSION_NUMBER,
    };
    
	// Menu -> Setup -> Country
	public static final int COUNTRY_BAHRAIN = 0;
	public static final int COUNTRY_INDIA = 1;
	public static final int COUNTRY_INDONESIA = 2;
	public static final int COUNTRY_ISRAEL = 3;
	public static final int COUNTRY_KUWAIT = 4;
	public static final int COUNTRY_MALAYSIA = 5;
	public static final int COUNTRY_PHILIPPINES = 6;
	public static final int COUNTRY_QATAR = 7;
	public static final int COUNTRY_SAUDI_ARABIA = 8;
	public static final int COUNTRY_SINGAPORE = 9;
	public static final int COUNTRY_THAILAND = 10;
	public static final int COUNTRY_UNITED_ARAB_EMIRATES = 11;
	public static final int COUNTRY_VIETNAM = 12;
	
	// Menu -> Setup -> Location
	public static final int TV_LOCATION_HOME = 0;
	public static final int TV_LOCATION_STORE = 1;
	
	//ATSC input
    public static final int ATSC_CABLE = 1;
    public static final int ATSC_AIR = 2;
    
    //Menu-> PVR
	public static final int PVR_TIME_SHIFT_SIZE_512M = 512;
	public static final int PVR_TIME_SHIFT_SIZE_1G = 1024;
	public static final int PVR_TIME_SHIFT_SIZE_2G = 2048;
	public static final int PVR_TIME_SHIFT_SIZE_4G = 4096;
	
	//TimeShift Status
    public static final int STAT_TIMESHIFT_DISABLED = -1;
    public static final int STAT_TIMESHIFT_UNKNOWN = 0;
    public static final int STAT_TIMESHIFT_STOP = 1;
    public static final int STAT_TIMESHIFT_PAUSE = 2;
    public static final int STAT_TIMESHIFT_PLAYBACK = 3;
    public static final int STAT_TIMESHIFT_FFWD = 4;
    public static final int STAT_TIMESHIFT_FRWD = 5;
    public static final int STAT_TIMESHIFT_SFWD = 6;
    public static final int STAT_TIMESHIFT_SRWD = 7;
    public static final int STAT_TIMESHIFT_STEPPED = 8;
    
}
