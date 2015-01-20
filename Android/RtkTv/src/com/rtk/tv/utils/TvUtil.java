package com.rtk.tv.utils;

import java.util.Calendar;
import java.util.Date;

import com.rtk.tv.R;
import com.rtk.tv.TvManagerHelper;

public class TvUtil {
	public static int getVideoResolutionIcon(int resolution) {
		switch(resolution) 
		{
			case TvManagerHelper.RESOLUTION_HD:
				return R.drawable.ic_info_video_hd;
			case TvManagerHelper.RESOLUTION_SD:
				return R.drawable.ic_info_video_sd;
			case TvManagerHelper.RESOLUTION_UNKNOWN:
			default:
				return R.drawable.ic_info_video_hd;
		}
	}
	
	public static int getVideoAspectIcon(int aspect) {
		switch(aspect) {
		case TvManagerHelper.RESOLUTION_UNKNOWN:
		default:
			return R.drawable.ic_info_video_hd;
		}
	}
	
	public static Date translateTvTime(long time) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time * 1000);
		return c.getTime();
	}
	
	public static int getParentRatingIcon(String parentRating) {
		//default
		return R.drawable.ic_info_video_hd;
	}

	public static int getGreneIcon(String grene) {
		//default
		return R.drawable.ic_info_video_hd;
	}
}
