# coding:utf8
__author__='orisun'

import json
import time
from collections import deque
import datetime
import logging
from functools import wraps
import subprocess as sub
import traceback
from logger import LoggerFactory
logger = LoggerFactory.getLogger()
import numpy as np
from numpy import linalg

time_format = '%Y-%m-%d %H:%M:%S'


safe_int = lambda x: int(x) if x.strip().isdigit() else 0


def safe_str(x):
    ''' return unicode'''
    x = x.strip().lower()
    return x.decode('utf8', 'ignore') if x != 'null' else ''

def dedupe(items, key=None):
    seen = set()
    for item in items:
        val = item if key is None else key(item)
        if val not in seen:
            yield item
            seen.add(val)

SEC_HOUR = 60 * 60
SEC_DAY = SEC_HOUR * 24
SEC_WEEK = SEC_DAY * 7
SEC_MON = SEC_DAY * 30
SEC_YEAR = SEC_MON * 12


def to_time_int(time_str):
    ''' convert datetime(str) to timestamp(int),
    e.g, 2015-09-09 11:31:52 -> 1441769512
    '''
    if time_str.startswith('0'):
        return 0

    time_int = 0
    try:
        dt = datetime.datetime.strptime(time_str, time_format)
        time_int = int(time.mktime(dt.timetuple()))
    except Exception as e:
        logger.warn('invalid time str (%s), error: (%s)' % (time_str, e))
        time_int = 0

    return time_int


def nowStr():
    '''打印当前时刻
    '''
    now = time.localtime()
    return "{year}-{month:02d}-{day:02d} {hour:02d}:{minute:02d}:{second:02d}".format(year=now.tm_year, month=now.tm_mon, day=now.tm_mday, hour=now.tm_hour, minute=now.tm_min, second=now.tm_sec)


def debugger(prefix=''):
    def decorator(func):
        @wraps(func)
        def wrapper(*args, **kwargs):
            logger.debug('begin: (%s.%s)' % (prefix, func.__name__,))
            t_start = time.time()
            res = func(*args, **kwargs)
            cost = int(time.time() - t_start)
            logger.debug('finished: (%s.%s), cost: <%d>secs' %
                         (prefix, func.__name__, cost))
            return res
        return wrapper
    return decorator


def run_shell(cmd_line):
    try:
        proc = sub.Popen(cmd_line, shell=True,
                         stdout=sub.PIPE, stderr=sub.PIPE)
        out, err = proc.communicate()
        if proc.returncode != 0:
            logger.error('cmd:(%s); returncode:(%s); out:(%s); err:(%s)' % (
                cmd_line, proc.returncode, out, err))
            return False
    except KeyboardInterrupt, SystemExit:
        raise
    except:
        trace = json.dumps(traceback.format_exc())
        logger.error('cmd:(%s); traceback: (%s)' % (cmd_line, trace))
        return False
    return True

def cosSim(vec1,vec2):
    len1=len(vec1)
    len2=len(vec2)
    assert len1==len2
    num = sum(vec1[i] * vec2[i]
              for i in xrange(len1))
    denom = linalg.norm(vec1) * linalg.norm(vec2)
    return num / denom

if __name__ == '__main__':
    print to_time_int('2015-09-15 12:19:42')
    print cosSim([1,2,3],[4,5,6])
