package com.orisun.mining.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期转换计算工具类
 * 
 * @Author:zhangchaoyang
 * @Since:2014-8-22
 * @Version:1.0
 */
public class DateUtil {

	public static final long SECONDES_OF_DAY = 60 * 60 * 24;
	public static final long MILISECONDES_OF_DAY = 1000 * SECONDES_OF_DAY;

	public static SimpleDateFormat YMDHMS1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat YMDHMS2 = new SimpleDateFormat("yyyyMMddHHmmss");
	public static final SimpleDateFormat HM = new SimpleDateFormat("HH:mm");
	public static SimpleDateFormat YMD1 = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat YMD2 = new SimpleDateFormat("yyyyMMdd");

	/**
	 * 获取昨天的日期，yyyy-MM-dd格式
	 * 
	 * @return
	 */
	public static String getYestodayStr() {
		Calendar rightNow = Calendar.getInstance();
		rightNow.add(Calendar.DAY_OF_YEAR, -1);
		int today_year = rightNow.get(Calendar.YEAR);
		int today_month = rightNow.get(Calendar.MONTH) + 1;
		int yestoday_day = rightNow.get(Calendar.DAY_OF_MONTH);
		String yestoday_str = today_year + "-" + String.format("%02d", today_month) + "-"
				+ String.format("%02d", yestoday_day);
		return yestoday_str;
	}

	/**
	 * 获取今天的日期，yyyy-MM-dd格式
	 * 
	 * @return
	 */
	public static String getTodayStr() {
		Calendar rightNow = Calendar.getInstance();
		int today_year = rightNow.get(Calendar.YEAR);
		int today_month = rightNow.get(Calendar.MONTH) + 1;
		int yestoday_day = rightNow.get(Calendar.DAY_OF_MONTH);
		String today_str = today_year + "-" + String.format("%02d", today_month) + "-"
				+ String.format("%02d", yestoday_day);
		return today_str;
	}

	/**
	 * 获取当前时间，yyyy-MM-dd HH:mm:ss格式
	 * 
	 * @return
	 */
	public static String getNowStr() {
		Date rightNow = new Date();
		return YMDHMS1.format(rightNow);
	}

	/**
	 * 返回当前时间与指定时间之间的秒数.{@code date}-now
	 * 
	 * @param date
	 * @return
	 * @Description:
	 */
	public static int getDistanceFromNow(Date date) {
		long distance = new Date().getTime() - date.getTime();
		return (int) (distance / 1000);// 时差超过24天时distance就超过了Integer.MAX_VALUE，所以必须让distance先除以1000再向int转化
	}

	/**
	 * 获取两个时刻相差的秒数。{@code date1}-{@code date2}
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int getTimeDifference(Date date1, Date date2) {
		long distance = date1.getTime() - date2.getTime();
		return (int) (distance / 1000);// 时差超过24天时distance就超过了Integer.MAX_VALUE，所以必须让distance先除以1000再向int转化
	}

	/**
	 * 获取本周的周日
	 * 
	 * @return
	 */
	public static Calendar getSundayOfThisWeek() {
		Calendar c = Calendar.getInstance();
		int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (day_of_week == 0)
			day_of_week = 7;
		c.add(Calendar.DATE, -day_of_week + 7);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		return c;
	}

	/**
	 * 获取本周的起始时间
	 * 
	 * @return
	 */
	public static Calendar getWeekBeginTime() {
		Calendar calendar = Calendar.getInstance();
		int min = calendar.getActualMinimum(Calendar.DAY_OF_WEEK); // 获取周开始基准
		int current = calendar.get(Calendar.DAY_OF_WEEK); // 获取当天周内天数
		calendar.add(Calendar.DAY_OF_WEEK, min - current); // 当天-基准，获取周开始日期
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return calendar;
	}

	/**
	 * 获取本月的起始时间
	 * 
	 * @return
	 */
	public static Calendar getMonthBeginTime() {
		Calendar calendar = Calendar.getInstance();
		int min = calendar.getActualMinimum(Calendar.DAY_OF_MONTH); // 获取周开始基准
		int current = calendar.get(Calendar.DAY_OF_MONTH); // 获取当天周内天数
		calendar.add(Calendar.DAY_OF_MONTH, min - current); // 当天-基准，获取周开始日期
		calendar.set(Calendar.HOUR_OF_DAY, 00);
		calendar.set(Calendar.MINUTE, 00);
		calendar.set(Calendar.SECOND, 00);
		return calendar;
	}

	/**
	 * 计算两个日期之间隔了几天。计算时只关注两个Calendar的日期，不管时分秒
	 * 
	 * @param earlyDate
	 * @param lateDate
	 * @return
	 */
	public static int dayDiff(Date earlyDate, Date lateDate) {
		Calendar cald1 = Calendar.getInstance();
		cald1.setTime(earlyDate);
		cald1.set(Calendar.HOUR_OF_DAY, 0);
		cald1.set(Calendar.MINUTE, 0);
		cald1.set(Calendar.SECOND, 0);
		cald1.set(Calendar.MILLISECOND, 0);
		Calendar cald2 = Calendar.getInstance();
		cald2.setTime(lateDate);
		cald2.set(Calendar.HOUR_OF_DAY, 0);
		cald2.set(Calendar.MINUTE, 0);
		cald2.set(Calendar.SECOND, 0);
		cald2.set(Calendar.MILLISECOND, 0);
		return (int) ((cald2.getTimeInMillis() - cald1.getTimeInMillis()) / MILISECONDES_OF_DAY);
	}

}
