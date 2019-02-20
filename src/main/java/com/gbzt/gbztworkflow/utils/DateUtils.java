/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.gbzt.gbztworkflow.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * 日期工具类, 继承org.apache.commons.lang.time.DateUtils类
 * @author ThinkGem
 * @version 2014-4-15
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {
	
	private static String[] parsePatterns = {
		"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM", 
		"yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
		"yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

	/**
	 * 得到当前日期字符串 格式（yyyy-MM-dd）
	 */
	public static String getDate() {
		return getDate("yyyy-MM-dd");
	}
	
	/**
	 * 得到当前日期字符串 格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
	 */
	public static String getDate(String pattern) {
		return DateFormatUtils.format(new Date(), pattern);
	}
	
	/**
	 * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
	 */
	public static String formatDate(Date date, Object... pattern) {
		String formatDate = null;
		if (pattern != null && pattern.length > 0) {
			formatDate = DateFormatUtils.format(date, pattern[0].toString());
		} else {
			formatDate = DateFormatUtils.format(date, "yyyy-MM-dd");
		}
		return formatDate;
	}
	
	/**
	 * 得到日期时间字符串，转换格式（yyyy-MM-dd HH:mm:ss）
	 */
	public static String formatDateTime(Date date) {
		return formatDate(date, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 得到当前时间字符串 格式（HH:mm:ss）
	 */
	public static String getTime() {
		return formatDate(new Date(), "HH:mm:ss");
	}

	/**
	 * 得到当前日期和时间字符串 格式（yyyy-MM-dd HH:mm:ss）
	 */
	public static String getDateTime() {
		return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 得到当前年份字符串 格式（yyyy）
	 */
	public static String getYear() {
		return formatDate(new Date(), "yyyy");
	}

	/**
	 * 得到当前月份字符串 格式（MM）
	 */
	public static String getMonth() {
		return formatDate(new Date(), "MM");
	}

	/**
	 * 得到当天字符串 格式（dd）
	 */
	public static String getDay() {
		return formatDate(new Date(), "dd");
	}

	/**
	 * 得到当前星期字符串 格式（E）星期几
	 */
	public static String getWeek() {
		return formatDate(new Date(), "E");
	}
	
	/**
	 * 日期型字符串转化为日期 格式
	 * { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", 
	 *   "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm",
	 *   "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm" }
	 */
	public static Date parseDate(Object str) {
		if (str == null){
			return null;
		}
		try {
			return parseDate(str.toString(), parsePatterns);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 获取过去的天数
	 * @param date
	 * @return
	 */
	public static long pastDays(Date date) {
		long t = new Date().getTime()-date.getTime();
		return t/(24*60*60*1000);
	}

	/**
	 * 获取过去的小时
	 * @param date
	 * @return
	 */
	public static long pastHour(Date date) {
		long t = new Date().getTime()-date.getTime();
		return t/(60*60*1000);
	}
	
	/**
	 * 获取过去的分钟
	 * @param date
	 * @return
	 */
	public static long pastMinutes(Date date) {
		long t = new Date().getTime()-date.getTime();
		return t/(60*1000);
	}
	
	/**
	 * 转换为时间（天,时:分:秒.毫秒）
	 * @param timeMillis
	 * @return
	 */
    public static String formatDateTime(long timeMillis){
		long day = timeMillis/(24*60*60*1000);
		long hour = (timeMillis/(60*60*1000)-day*24);
		long min = ((timeMillis/(60*1000))-day*24*60-hour*60);
		long s = (timeMillis/1000-day*24*60*60-hour*60*60-min*60);
		long sss = (timeMillis-day*24*60*60*1000-hour*60*60*1000-min*60*1000-s*1000);
		return (day>0?day+",":"")+hour+":"+min+":"+s+"."+sss;
    }
	
	/**
	 * 获取两个日期之间的天数
	 * 
	 * @param before
	 * @param after
	 * @return
	 */
	public static double getDistanceOfTwoDate(Date before, Date after) {
		long beforeTime = before.getTime();
		long afterTime = after.getTime();
		return (afterTime - beforeTime) / (1000 * 60 * 60 * 24);
	}
	
	/**
	 * 返回当前的前一天  2017-09-28
	 * @return yyyy-MM-dd 2017-09-01
	 */
	public static String getLastDay(String date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(parseDate(date));
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		return formatDate(calendar.getTime(), "yyyy-MM-dd");
	}
	
	/**
	 * 获取本周一  2017-09-28
	 * @return 2017-09-25
	 */
	public static String getMonday(){
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return formatDate(calendar.getTime(), "yyyy-MM-dd");
	}
	
	/**
	 * 获取指定日期上周周一
	 * @param date 2017-09-28
	 * @return 2017-09-18
	 */
	public static String getLastMonday(String date){
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.setTime(parseDate(date));
		calendar.add(Calendar.WEEK_OF_YEAR, -1);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return formatDate(calendar.getTime(), "yyyy-MM-dd");
	}
	
	/**
	 * 返回当月第一天  2017-09-28
	 * @return 2017-09-01
	 */
	public static String getMonthFirstDay(){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return formatDate(calendar.getTime(), "yyyy-MM-dd");
	}
	
	/**
	 * 返回传入时间的上个月
	 * @param date 2017-09-28
	 * @return 2017-08-28
	 */
	public static String getLastMonth(String date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(parseDate(date));
		calendar.add(Calendar.MONTH, -1);
		return formatDate(calendar.getTime(), "yyyy-MM-dd");
	}
	
	/**
	 * 返回当年的第一天  2017-09-28 16:21:25
	 * @return 2017-01-01 00:00:00
	 */
	public static String getYearFirstDate(){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return formatDate(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
	}
	
	/**
	 * 返回当年的第一天  2017-09-28
	 * @return 2017-01-01
	 */
	public static String getYearFirstDay(){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return formatDate(calendar.getTime(), "yyyy-MM-dd");
	}
	
	/**
	 * 返回传入日期的上一年
	 * @param date 2016-01-01
	 * @return 2015-01-01
	 */
	public static String getLastYear(String date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(parseDate(date));
		calendar.add(Calendar.YEAR, -1);
		return formatDate(calendar.getTime(), "yyyy-MM-dd");
	}
	/**
	 * 返回昨天
	 * @return
	 */
	public static String getYesterday(){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		return formatDate(calendar.getTime(), "yyyy-MM-dd");
	}
	
	/**
	 * 返回前天
	 * @return
	 */
	public static String getBeforeYesterday(){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, -2);
		return formatDate(calendar.getTime(), "yyyy-MM-dd");
	}
	
	/**
	 * 返回去年的当天
	 * @return
	 */
	public static String getLastYearThisDay(){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, -1);
		return formatDate(calendar.getTime(), "yyyy-MM-dd");
	}
	
	/**
	 * 返回去年第一天
	 * @return
	 */
	public static String getLastYearFirstDay(){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, -1);
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return formatDate(calendar.getTime(), "yyyy-MM-dd");
	}
	
	public static String getEarliestDate(){
		Calendar calendar = Calendar.getInstance();//得到当前时间
		calendar.setTimeInMillis(0);
		return  formatDate(calendar.getTime(), "yyyy-MM-dd");
	}

	public static String getDefultTime(String time){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss 'CST' yyyy", Locale.ENGLISH);
            Date date = dateFormat.parse(time);
            SimpleDateFormat sdf2 = new SimpleDateFormat("yy-MM-dd hh:mm:ss");
            String str=sdf2.format(date);
            return str;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

	/**
	 * @param args
	 * @throws ParseException
	 */
	public static void main(String[] args) throws ParseException {
//		System.out.println(formatDate(parseDate("2010/3/6")));
//		System.out.println(getDate("yyyy年MM月dd日 E"));
//		long time = new Date().getTime()-parseDate("2012-11-19").getTime();
//		System.out.println(time/(24*60*60*1000));
		System.out.println(getLastDay(getDate()));
		System.out.println(getYearFirstDay());
		System.out.println(getLastYear(getDate()));
		System.out.println(getMonthFirstDay());
		System.out.println(getLastMonth(getDate()));
		System.out.println(getMonday());
		System.out.println(getLastMonday(getDate()));
		System.out.println(getEarliestDate());
	}
}
