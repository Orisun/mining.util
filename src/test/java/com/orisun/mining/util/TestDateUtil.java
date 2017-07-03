package com.orisun.mining.util;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestDateUtil {

	@Test
	public void testDayDiff() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
		Date date1 = sdf.parse("2017-01-09 00:00:00,001");
		Date date2 = sdf.parse("2017-01-09 23:59:59,999");
		Date date3 = sdf.parse("2017-01-08 23:59:59,999");
		Assert.assertTrue(0 == DateUtil.dayDiff(date1, date2));
		Assert.assertTrue(1 == DateUtil.dayDiff(date3, date1));// 即使只隔2毫秒也是相差1天
		Assert.assertTrue(-1 == DateUtil.dayDiff(date1, date3));
	}
}
