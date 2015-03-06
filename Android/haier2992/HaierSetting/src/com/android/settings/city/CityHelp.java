package com.android.settings.city;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

public class CityHelp {

	public static final String SELECTED_CITY_ID = "selectedCityId";
	public static final String SELECTED_CITY_NAME = "selectedCityName";

	public static void setCitySettings(Context mContext, String cityId, String cityName) {
		Settings.System.putString(mContext.getContentResolver(), SELECTED_CITY_ID, cityId == null ? "" : cityId);
		Settings.System.putString(mContext.getContentResolver(), SELECTED_CITY_NAME, cityName == null ? "" : cityName);
		mContext.sendBroadcast(new Intent("com.android.settings.city.SELECT_CITY_COMPLETED"));
	}

	public static String getCityId(Context mContext) {
		String cityId = Settings.System.getString(
				mContext.getContentResolver(), SELECTED_CITY_ID);
		return cityId == null ? "" : cityId;
	}

	public static String getCityName(Context mContext) {
		String cityName = Settings.System.getString(
				mContext.getContentResolver(), SELECTED_CITY_NAME);
		return cityName == null ? "" : cityName;
	}
}
