package com.realtek.dmr;

public class Util{
	
	/**
	 * 
	 * 
	 * @param time
	 * @return
	 */
	
	public static String toTime(long time) {

		time /= 1000;
		long minute = time / 60;
		long second = time % 60;
		long hour = minute / 60;
		minute %= 60;
		return String.format("%02d:%02d:%02d",hour, minute, second);
	}
	
	public static String toSecondTime(long time) {
		long minute = time / 60;
		long second = time % 60;
		long hour = minute / 60;
		minute %= 60;
		return String.format("%02d:%02d:%02d",hour, minute, second);
	}
	
}