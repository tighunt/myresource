package com.realtek.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

public class LocaleDateInfo {
	static final String TAG = "LocaleDateInfo";
	public static interface Week {
		String getWeekDayByDay(int day);
	}
	
	public static interface Month {
		String getMonthByMonth(int month);
	}
	
	public static class MY_Week implements Week {
		String WeekDays[] = {
				"Isn","Sel","Rab","Kha","Jum","Sab","Aha"	
		};
		
		@Override
		public String getWeekDayByDay(int day) {
			// TODO Auto-generated method stub
			if(day <0 || day > 6) {
				return "";
			}
			return WeekDays[day];
		}
	}
	
	public static class MY_Month implements Month {
		String Months[] = {
			"Jan","Feb","Mac","Apr","Mei","Jun","Jul","Ogo","Sep","Okt","Nov","Dis"
		};
		@Override
		public String getMonthByMonth(int month) {
			// TODO Auto-generated method stub
			if(month < 0 || month > 11) {
				return "";
			}
			return Months[month];
		}
		
	}
	
	public static class VN_Week implements Week {
		String WeekDays[] = {
				"Thứ hai","Thứ ba","Thứ tư","Thứ năm","Thứ sáu","Thứ bảy","Chủ nhật"	
		};
		
		@Override
		public String getWeekDayByDay(int day) {
			// TODO Auto-generated method stub
			if(day <0 || day > 6) {
				return "";
			}
			return WeekDays[day];
		}
	}
	
	public static class VN_Month implements Month {
		String Months[] = {
			"Tháng 1","Tháng 2","Tháng 3","Tháng 4","Tháng 5","Tháng 6","Tháng 7","Tháng 8","Tháng 9","Tháng 10","Tháng 11","Tháng 12"
		};
		@Override
		public String getMonthByMonth(int month) {
			// TODO Auto-generated method stub
			if(month < 0 || month > 11) {
				return "";
			}
			return Months[month];
		}
		
	}
	
	public static class TH_Week implements Week {
		String WeekDays[] = {
				"จันทร์","อังคาร","พุธ","พฤหัสบดี","ศุกร์","เสาร์","อาทิตย์"	
		};
		
		@Override
		public String getWeekDayByDay(int day) {
			// TODO Auto-generated method stub
			if(day <0 || day > 6) {
				return "";
			}
			return WeekDays[day];
		}
	}
	
	public static class TH_Month implements Month {
		String Months[] = {
			"มกราคม","กุมภาพันธ์","มีนาคม","เมษายน","พฤษภาคม","มิถุนายน","กรกฎาคม","สิงหาคม","กันยายน","ตุลาคม","พฤศจิกายน","ธันวาคม"
		};
		@Override
		public String getMonthByMonth(int month) {
			// TODO Auto-generated method stub
			if(month < 0 || month > 11) {
				return "";
			}
			return Months[month];
		}
		
	}
	
	public static class RU_Week implements Week {
		String WeekDays[] = {
				"Пн","Вт","Ср","Чт","Пт","Сб","Вс"
		};
						
		@Override
		public String getWeekDayByDay(int day) {
			if(day <0 || day > 6) {
				return "";
			}
			return WeekDays[day];
		}
	}
		
	public static class RU_Month implements Month {
		String Months[] = {
				"Янв","Фев","Мар","Апр","Май","Июн","Июл","Авг","Сен","Окт","Ноб","Дек"
		};
		@Override
		public String getMonthByMonth(int month) {
			if(month < 0 || month > 11) {
				return "";
			}
			return Months[month];
		}
									
	}
			
	public static class FR_Week implements Week {
		String WeekDays[] = {
			"Lun","Mar","Mer","Jeu","Ven","Sam","Dim"
		};
								
		@Override
		public String getWeekDayByDay(int day) {
			if(day <0 || day > 6) {
				return "";
			}
			return WeekDays[day];
																				}
		}
				
		public static class FR_Month implements Month {
			String Months[] = {
				"Jan","Févr","Mar","Avr","Mai","Jun","Jui","Aoû","Sep","Oct","Nov","Déc"
			};
		@Override
		public String getMonthByMonth(int month) {
			if(month < 0 || month > 11) {
				return "";
			}
			return Months[month];
		}
											
	}
					
	public static class ID_Week implements Week {
		String WeekDays[] = {
			"Snn","Sls","Rbu","Kms","Jmt","Sbt","Mgg"
		};
										
		@Override
		public String getWeekDayByDay(int day) {
			if(day <0 || day > 6) {
				return "";
			}
			return WeekDays[day];
		}
	}
						
	public static class ID_Month implements Month {
		String Months[] = {
			"Jan","Feb","Mar","Apr","Mei","Jun","Jul","Agt","Sep","Okt","Nop","Des"
		};
		@Override
		public String getMonthByMonth(int month) {
			if(month < 0 || month > 11) {
				return "";
			}
			return Months[month];
		}
													
	}
	
	public static class IR_Week implements Week {
		String WeekDays[] = {
			"دوشنبه","سه‌شنبه","چهارشنبه","پنج شنبه","جمعه","شنبه","یکشنبه"
		};
										
		@Override
		public String getWeekDayByDay(int day) {
			if(day <0 || day > 6) {
				return "";
			}
			return WeekDays[day];
		}
	}
						
	public static class IR_Month implements Month {
		String Months[] = {
			"ژانویهٔ","فوریهٔ","مارس","آوریل","مه","ژوئن","ژوئیهٔ","اوت","سپتامبر","اکتبر","نوامبر","دسامبر"
		};
		
		@Override
		public String getMonthByMonth(int month) {
			if(month < 0 || month > 11) {
				return "";
			}
			return Months[month];
		}
													
	}

	public static Week getWeek(Locale locale) {
		if(locale.getCountry().equals("MY")) {
			return new MY_Week();
		} else if(locale.getCountry().equals("VN")) {
			return new VN_Week();
		} else if(locale.getCountry().equals("TH")) {
			return new TH_Week();
		}else if(locale.getCountry().equals("RU")) {
			return new RU_Week();
		}else if(locale.getCountry().equals("FR")) {
			return new FR_Week();
		}else if(locale.getCountry().equals("ID")) {
			return new ID_Week();
		}else if(locale.getCountry().equals("IR")) {
			return new IR_Week();
		}
		return null;
	}
	
	public static Month getMonth(Locale locale) {
		if(locale.getCountry().equals("MY")) {
			return new MY_Month();
		} else if(locale.getCountry().equals("VN")) {
			return new VN_Month();
		} else if (locale.getCountry().equals("TH")) {
			return new TH_Month();
		}else if (locale.getCountry().equals("RU")) {
			return new RU_Month();
		}else if (locale.getCountry().equals("FR")) {
			return new FR_Month();
		}else if (locale.getCountry().equals("ID")) {
			return new ID_Month();
		}else if (locale.getCountry().equals("IR")) {
			return new IR_Month();
		}
		return null;
	}
	
	public static String formatDate(Date date, Locale locale) {
		String ret = "";
		if(date == null)
			return ret;
		LocaleDateInfo.Month months = LocaleDateInfo.getMonth(locale);
		LocaleDateInfo.Week weeks = LocaleDateInfo.getWeek(locale);
		
		if(locale.getCountry().equals("IR")) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int month = cal.get(Calendar.MONTH);	//0-11
			int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);//1-7
			int dayOfWeekIndex = (dayOfWeek + 5) % 7;
			SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", locale);
			SimpleDateFormat dayFormat = new SimpleDateFormat("dd", locale);
			SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", locale);
			String time = timeFormat.format(date);
			String day = dayFormat.format(date);
			String year = yearFormat.format(date);
			String month_str = months.getMonthByMonth(month);
			String week_str = weeks.getWeekDayByDay(dayOfWeekIndex);
			
			ret = year + " " + month_str + " " + day + " " + week_str + time;
			return ret;
		}
		
		if(months != null && weeks != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);	//0-11
			int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
			int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);//1-7
			
			int dayOfWeekIndex = (dayOfWeek + 5) % 7;
			
			
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int minute = cal.get(Calendar.MINUTE);
			
			Log.v(TAG, "" + dayOfWeek);
			
			String concatStr = "";
			if(hour < 10)
				concatStr = concatStr + "0" + String.valueOf(hour);
			else 
				concatStr += String.valueOf(hour);
			
			concatStr += ":";
			
			if(minute < 10)
				concatStr = concatStr + "0" + String.valueOf(minute);
			else
				concatStr += String.valueOf(minute);
			
			concatStr += " ";
			concatStr += weeks.getWeekDayByDay(dayOfWeekIndex);
			concatStr += " ";
			concatStr += dayOfMonth;
			concatStr += " ";
			concatStr += months.getMonthByMonth(month);
			concatStr += " ";
			concatStr += year;
			
			ret = concatStr;
			
		} else {
			final String DATAFORMAT = "HH:mm EEE,dd MMM yyyy";
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(DATAFORMAT);
				ret =  sdf.format(date);
			} catch(Exception e) {
				
			}
		}
		return ret;
	}
}
