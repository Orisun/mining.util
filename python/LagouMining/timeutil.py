# coding=utf-8
__author__='orisun'

import time
import datetime

SECONDS_OF_DAY = 60 * 60 * 24
MINUTES_OF_DAY = 60 * 24


def timeDiff(datetime1, datetime2):
    '''比较两个时间间隔几秒,datetime1-datetime2
    '''
    return SECONDS_OF_DAY * (datetime1 - datetime2).days + (datetime1 - datetime2).seconds


def dayDiff(datetime1, datetime2):
    '''比较两个时间间隔几天,精确到秒,datetime1-datetime2
    '''
    return 1.0 * timeDiff(datetime1, datetime2) / SECONDS_OF_DAY


def dateBefore(datetime1, datetime2):
    '''判断datetime1是否比datetime2早（精确到天），若是返回-1，不是不返回1，同一天返回0
    '''
    date1 = datetime1.timetuple()
    date2 = datetime2.timetuple()
    if date1.tm_year < date2.tm_year:
        return -1
    elif date1.tm_year > date2.tm_year:
        return 1
    else:
        if date1.tm_yday < date2.tm_yday:
            return -1
        elif date1.tm_yday > date2.tm_yday:
            return 1
        else:
            return 0


def nextMondayZero(dt):
    '''下一个周一0点的时间戳(如果当天就是周一，则返回当天0点的时间戳)
    '''
    timestamp = time.mktime(dt.timetuple())
    zeroClockTs = timestamp - (timestamp % 86400) + time.timezone
    dayDiff = (8 - dt.isocalendar()[2]) % 7
    timestamp = zeroClockTs + dayDiff * SECONDS_OF_DAY
    return timestamp


def preSaturdayZero(dt):
    '''上一个周六的0点(如果当天就是周六，则返回当天0点的时间戳)
    '''
    timestamp = time.mktime(dt.timetuple())
    zeroClockTs = timestamp - (timestamp % 86400) + time.timezone
    dayDiff = (dt.isocalendar()[2] + 1) % 7
    timestamp = zeroClockTs - dayDiff * SECONDS_OF_DAY
    return timestamp


def workdayTimeDiff(datetime1, datetime2):
    '''计算两个时间相隔的秒数，datetime1-datetime2，周六周日不计算在内
    '''
    time_tuple1 = datetime1.timetuple()
    time_tuple2 = datetime2.timetuple()
    time1 = datetime1.time()
    time2 = datetime2.time()

    # 两个时间回归到当周的周一
    dt1 = datetime.datetime.strptime(
        "{}-{}".format(time_tuple1.tm_year, time_tuple1.tm_yday - time_tuple1.tm_wday), "%Y-%j")
    dt2 = datetime.datetime.strptime(
        "{}-{}".format(time_tuple2.tm_year, time_tuple2.tm_yday - time_tuple2.tm_wday), "%Y-%j")
    # 周六周日的时间挪到下周一0点
    if time_tuple1.tm_wday > 4:
        time1 = time1.replace(hour=0, minute=0, second=0)
    if time_tuple2.tm_wday > 4:
        time2 = time2.replace(hour=0, minute=0, second=0)
    interval_days = (dt1 - dt2).days * 5 / 7 + \
        min(time_tuple1.tm_wday, 5) - min(time_tuple2.tm_wday, 5)

    interval_seconds = interval_days * SECONDS_OF_DAY + 3600 * \
        (time1.hour - time2.hour) + 60 * (time1.minute -
                                          time2.minute) + (time1.second - time2.second)
    return interval_seconds


def workdayTimeDiff2(datetime1, datetime2):
    '''计算两个时间相隔几个工作日,datetime1-datetime2,精确到天
    '''
    time_tuple1 = datetime1.timetuple()
    time_tuple2 = datetime2.timetuple()
    # 两个时间回归到当周的周一
    dt1 = datetime.datetime.strptime(
        "{}-{}".format(time_tuple1.tm_year, time_tuple1.tm_yday - time_tuple1.tm_wday), "%Y-%j")
    dt2 = datetime.datetime.strptime(
        "{}-{}".format(time_tuple2.tm_year, time_tuple2.tm_yday - time_tuple2.tm_wday), "%Y-%j")
    # 周六周日的时间挪到下周一
    interval_days = (dt1 - dt2).days * 5 / 7 + \
        min(time_tuple1.tm_wday, 5) - min(time_tuple2.tm_wday, 5)
    return interval_days


def is_leap_year(year):
    '''判断是否为闰年
    '''
    # 整百年能被400整除，或者非整百年能被4整除
    if (year % 4 == 0) and (year % 100 != 0) or (year % 400 == 0):
        return True
    else:
        return False

if __name__ == '__main__':
    d1 = datetime.datetime.strptime('2015-12-3 11:11:11', '%Y-%m-%d %H:%M:%S')
    d2 = datetime.datetime.strptime('2015-12-4 10:10:10', '%Y-%m-%d %H:%M:%S')
    d3 = datetime.datetime.strptime('2015-12-5 10:10:10', '%Y-%m-%d %H:%M:%S')
    d4 = datetime.datetime.strptime('2015-12-6 10:10:10', '%Y-%m-%d %H:%M:%S')
    d5 = datetime.datetime.strptime('2015-12-7 10:10:10', '%Y-%m-%d %H:%M:%S')
    d6 = datetime.datetime.strptime('2015-12-8 10:10:10', '%Y-%m-%d %H:%M:%S')
    d7 = datetime.datetime.strptime('2015-11-28 10:10:10', '%Y-%m-%d %H:%M:%S')
    d8 = datetime.datetime.strptime('2015-11-27 10:10:10', '%Y-%m-%d %H:%M:%S')
    d9 = datetime.datetime.strptime('2015-11-26 10:10:10', '%Y-%m-%d %H:%M:%S')
    d10 = datetime.datetime.strptime(
        '2015-11-25 10:10:10', '%Y-%m-%d %H:%M:%S')
    assert 1 == workdayTimeDiff2(d2, d1)
    assert 2 == workdayTimeDiff2(d3, d1)
    assert 2 == workdayTimeDiff2(d4, d1)
    assert 2 == workdayTimeDiff2(d5, d1)
    assert 3 == workdayTimeDiff2(d6, d1)
    assert -3 == workdayTimeDiff2(d7, d1)
    assert -4 == workdayTimeDiff2(d8, d1)
    assert -5 == workdayTimeDiff2(d9, d1)
    assert -6 == workdayTimeDiff2(d10, d1)
    assert 82739 == workdayTimeDiff(d2, d1)
    assert 132529 == workdayTimeDiff(d3, d1)
    assert 132529 == workdayTimeDiff(d4, d1)
    assert 169139 == workdayTimeDiff(d5, d1)
    assert 255539 == workdayTimeDiff(d6, d1)
    assert -299471 == workdayTimeDiff(d7, d1)
    assert -349261 == workdayTimeDiff(d8, d1)
    assert -435661 == workdayTimeDiff(d9, d1)
    assert -522061 == workdayTimeDiff(d10, d1)

    d11 = datetime.datetime.strptime(
        '2015-07-11 04:55:49', '%Y-%m-%d %H:%M:%S')
    d12 = datetime.datetime.strptime(
        '2015-07-11 04:58:48', '%Y-%m-%d %H:%M:%S')
    d13 = datetime.datetime.strptime(
        '2015-07-13 00:00:48', '%Y-%m-%d %H:%M:%S')
    d14 = datetime.datetime.strptime(
        '2015-07-10 23:59:00', '%Y-%m-%d %H:%M:%S')
    assert 0 == workdayTimeDiff(d12, d11)
    assert 48 == workdayTimeDiff(d13, d11)
    assert 108 == workdayTimeDiff(d13, d14)
    assert 172908 == timeDiff(d13, d14)
