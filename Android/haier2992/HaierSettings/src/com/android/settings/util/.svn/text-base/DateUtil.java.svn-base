package com.android.settings.util;

import java.util.Calendar;
import java.util.regex.Pattern;


//import org.apache.commons.lang.time.DateUtils;
public class DateUtil {
	//正确日期格式
	private static String[] dateFormat={"YYYYMMDD","YYYY MM DD","YYYY/MM/DD"};
	public static String dateFString(){
		String s="";
		for(String ss:dateFormat){
			s+=";"+ss;
		}
		return s;
	}
	
	public static void validate(String date) throws InvalidDateFormatException{
		Pattern p = Pattern.compile("^((\\d{2}(([02468][048])|([13579][26]))[\\/\\/\\s]?((((0?[13578])|(1[02]))[\\/\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\/\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\/\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\/\\/\\s]?((((0?[13578])|(1[02]))[\\/\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\/\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\/\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))");
		if(!p.matcher(date).matches()){
			throw new InvalidDateFormatException("提供的日期格式有错误！正确格式:"+dateFString());
		}
	}
	/**获取指定日期对应的星期 1..7
	 * @param date
	 * @return
	 * @throws InvalidDateFormatException
	 */
	public static String getWeek(String date) throws InvalidDateFormatException{
		validate(date);
		//将日期中的分隔符全部替换
		Pattern p=Pattern.compile("[\\/\\/\\s]?");
		String s=p.matcher(date).replaceAll("");
		
		int year=Integer.valueOf(s.substring(0,4));
		int month=Integer.valueOf(s.substring(4,6));
		int day=Integer.valueOf(s.substring(6,8));
		java.util.Calendar c=Calendar.getInstance();
		c.set(year, month-1, day);
		int week=c.get(Calendar.DAY_OF_WEEK)-1;
		switch(week){
			case 0:week=7;
			default:;
		}
		return week+"";
	}
	
	/**获取任意起点的周末 eg:本周二..下周一
	 * @param day 任意周的起始日 可选项： 1..7
	 * @return
	 * @throws InvalidDateFormatException
	 */
	public static String getLastDayOfWeek(String day) throws InvalidDateFormatException{
		int week=-1;
		int iday=-1;
		try{
			iday=Integer.parseInt(day);
		}catch(NumberFormatException ex){
			throw new InvalidDateFormatException("提供的星期只能是 1..7 的数字!");
		}
		for(int i=0;i<6;i++){
			iday++;
			if(iday>7){
				iday=1;
			}
		}
		week=iday;
		return week+"";
	}
	
	
	/**还没有实现
	 * @param day  任意起始日 可选项： 1..月末日（28,29,30,31）
	 * @param date 指定日期：格式，YYYYMMDD","YYYY MM DD","YYYY/MM/DD
	 * @return     返回任意起点的月头day,在指定日期所在月的月月末日 eg:本月1号..本月月末，本月2号..下月1号
	 * @throws InvalidDateFormatException
	 */
	public static String getLastDayOfMonth(String day,String date) throws InvalidDateFormatException{
		int week=-1;
		int iday=-1;
		try{
			iday=Integer.parseInt(day);
		}catch(NumberFormatException ex){
			throw new InvalidDateFormatException("提供的星期只能是 1..7 的数字!");
		}
		for(int i=0;i<6;i++){
			iday++;
			if(iday>7){
				iday=1;
			}
		}
		week=iday;
		return week+"";
	}
	
	
	/**
	 * @param connector
	 * @return 返回当前日期的字符串 格式如下：yyyy[connector]mm[connector]dd
	 * @throws InvalidDateFormatException
	 */
	public static String getDay2Str(Calendar calendar,String connector) throws InvalidDateFormatException{
		Calendar cal=calendar;
		if(cal==null)
			cal=Calendar.getInstance();
		int year=cal.get(Calendar.YEAR);
		int month=cal.get(Calendar.MONTH)+1;
		int day=cal.get(Calendar.DAY_OF_MONTH);
		
		String sYear=Integer.toString(year);
		String sMonth="";
		String sDay="";
		if(month<10){
			sMonth="0"+Integer.toString(month);
		}else{
			sMonth=Integer.toString(month);
		}
		if(day<10){
			sDay="0"+Integer.toString(day);
		}else{
			sDay=Integer.toString(day);
		}
		StringBuffer buffer=new StringBuffer();
		
		buffer.append(sYear).append(connector).append(sMonth).append(connector).append(sDay);
		
		return buffer.toString();
	}
	
	/**
	 * @param date 截止日期 "YYYYMMDD","YYYY MM DD","YYYY/MM/DD"
	 * @return 指定日期之前的一个星期的区间
	 * @throws InvalidDateFormatException
	 */
	public static String[] getAreaWeek(String date)throws InvalidDateFormatException{
		validate(date);
		Pattern p=Pattern.compile("[\\/\\/\\s]?");
		String s=p.matcher(date).replaceAll("");
		Calendar cal=Calendar.getInstance();
		cal.set(Integer.parseInt(s.substring(0,4)),Integer.parseInt(s.substring(4,6))-1,Integer.parseInt(s.substring(6)));
		cal.add(Calendar.DAY_OF_MONTH,-1);
		String eArea=getDay2Str(cal,"");
		cal.add(Calendar.DAY_OF_MONTH, -6);
		String sArea=getDay2Str(cal,"");
		String[] area={sArea,eArea};
		return area;
	}
	
	/**
	 * @param date 截止日期 "YYYYMMDD","YYYY MM DD","YYYY/MM/DD" 
	 * @return 指定日期之前的一个月的区间
	 * @throws InvalidDateFormatException
	 */
	public static String[] getAreaMonth(String date)throws InvalidDateFormatException{
		validate(date);
		Pattern p=Pattern.compile("[\\/\\/\\s]?");
		String s=p.matcher(date).replaceAll("");
		Calendar cal=Calendar.getInstance();
		cal.set(Integer.parseInt(s.substring(0,4)),Integer.parseInt(s.substring(4,6))-1,Integer.parseInt(s.substring(6)));
		cal.add(Calendar.DAY_OF_MONTH,-1);
		String eArea=getDay2Str(cal,"");
		cal.add(Calendar.DAY_OF_MONTH, -6);
		String sArea=eArea.substring(0,6)+"01";
		String[] area={sArea,eArea};
		return area;
	}
	
	/**
	 * @param date
	 * @param style
	 * @return
	 * @throws InvalidDateFormatException
	 */
	public static String DateFormat(String date,String style) throws InvalidDateFormatException{
		validate(date);
		
		Pattern p=Pattern.compile("[\\/\\/\\s]?");
		String s=p.matcher(date).replaceAll("");
		
		String[] fo=style.split("%");
		
		String year="";
		
		String month="";
		
		String day="";
		
		String con="";
		
		if(fo[0].equals("Y")){
			year=s.substring(0,4);
		}else if(fo[0].equals("y")){
			year=s.substring(2,4);
		}
		month=s.substring(4,6);
		day=s.substring(6);
		con=style.substring(2,3);
		return year+con+month+con+day;
	}
	
	
	/**获取月份
	 * @param cal 日期，null 表示当前日期
	 * @param style m月<10左边不补0
	 *              mm月<10 则左边补0
	 */
	public static String getMonth(Calendar calendar,String style){
		Calendar cal=calendar;
		if(cal==null)
			cal=Calendar.getInstance();
		int month=cal.get(Calendar.MONTH)+1;
		String monthS = Integer.toString(month);
		if("m".equals(style)){
			return monthS;
		}else if("mm".equalsIgnoreCase(style)){
			if(month<10){
				return "0"+monthS;
			}
			return monthS;
		}else{
			return null;
		}
	}
	public static void main(String args[]){
		try {
			/*String s=getWeek("2011/12/18");
			System.out.println(s);*/
			
			/*System.out.println(getLastDayOfWeek("7"));*/
			
			/*System.out.println(getDay2Str(null,"-"));*/
			
			/*String[] area=getAreaWeek("20110207");
			for(String a:area){
				System.out.println(a);
			}*/
			
			
			String[] area=getAreaMonth("20110501");
			for(String a:area){
				System.out.println(a);
			}
		} catch (InvalidDateFormatException e) {			
			e.printStackTrace();
		}
	}
}
