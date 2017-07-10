# coding=utf-8
__author__ = 'orisun'

import os
import subprocess as sub
import requests
import base64
import json
import inspect
from apscheduler.schedulers.blocking import BlockingScheduler

#报警阈值设置
gigabyte = 1000 * 1000
avail_mem = 4   #内存还剩4G
avail_cpu = 10  #CPU还剩10%
avail_root_disk = 0.1   #根目录磁盘还剩10%
avail_data_disk = 0.15  #/data目录磁盘还剩15%
main_receiver = ['zhchya@gmail.com']    #报警邮件接收人



def memory_stat():
    mem = {}
    f = open("/proc/meminfo")
    lines = f.readlines()
    f.close()
    for line in lines:
        if len(line) < 2:
            continue
        arr = line.split(':')
        name = arr[0]
        var = arr[1].split()[0]
        mem[name] = long(var)
    return (mem['MemTotal'], mem['MemFree'],mem['Cached'])


def load_stat():
    loadavg = {}
    f = open("/proc/loadavg")
    con = f.read().split()
    f.close()
    return (float(con[0]), float(con[1]), float(con[2]))


def cpu_stat():
    cmd_line = "top -bi -n 1 | grep Cpu"
    try:
        proc = sub.Popen(cmd_line, shell=True,
                         stdout=sub.PIPE, stderr=sub.PIPE)
        out, err = proc.communicate()
        if proc.returncode != 0:
            print'cmd:({}); returncode:({}); out:({}); err:({})'.format(
                cmd_line, proc.returncode, out, err)
        else:
            arr = out.split()
            used = {}
            for ele in arr:
                brr = ele.split('%')
                if len(brr) == 2:
                    used[brr[1]] = float(brr[0])
            return (used['id,'], used['sy,'], used['us,'])
    except:
        print e


def disk_root_stat():
    hd = {}
    disk = os.statvfs("/")
    hd['available'] = disk.f_bsize * disk.f_bavail
    hd['capacity'] = disk.f_bsize * disk.f_blocks
    hd['used'] = disk.f_bsize * disk.f_bfree
    return (hd['capacity'], hd['available'])


def disk_data_stat():
    hd = {}
    disk = os.statvfs("/data")
    hd['available'] = disk.f_bsize * disk.f_bavail
    hd['capacity'] = disk.f_bsize * disk.f_blocks
    hd['used'] = disk.f_bsize * disk.f_bfree
    return (hd['capacity'], hd['available'])


def disk_root_used():
    lines = os.popen("du --max-depth=1 /").read().split("\n")
    used = []
    for line in lines:
        arr = line.split()
        if len(arr) == 2:
            used.append((arr[1], float(arr[0])))
    used.sort(lambda x, y: cmp(y[1], x[1]))
    return "<br>".join(["{}\t{:.2f}G".format(used[i][0], used[i][1] / gigabyte) for i in xrange(0, min(5, len(used)))])


def disk_data_used():
    lines = os.popen("du --max-depth=1 /data").read().split("\n")
    used = []
    for line in lines:
        arr = line.split()
        if len(arr) == 2:
            used.append((arr[1], float(arr[0])))
    used.sort(lambda x, y: cmp(y[1], x[1]))
    return "<br>".join(["{}\t{:.2f}G".format(used[i][0], used[i][1] / gigabyte) for i in xrange(0, min(5, len(used)))])


def send_mail(title, content):
    data_dict = {}
    data_dict['subject'] = u'服务器' + title + u'报警'
    data_dict['recip'] = main_receiver
    data_dict['content'] = content
    url = 'http://inapi.oss.com/v2/send/mail/'
    requests.post(url, data=json.dumps(data_dict))


def check_disk():
    total_disk, res_disk = disk_root_stat()
    res_ratio = 1.0 * res_disk / total_disk
    if res_ratio < avail_root_disk:
        content = u"/ 目录磁盘空间{:.2f}G,仅剩{:.1f}%<br>{}".format(
            1.0 * total_disk / (1024 * gigabyte), 100 * res_ratio, disk_root_used())
        send_mail(u"磁盘", content)
    total_disk, res_disk = disk_data_stat()
    res_ratio = 1.0 * res_disk / total_disk
    if res_ratio < avail_data_disk:
        content = u"/data 目录磁盘空间{:.2f}G,仅剩{:.1f}%<br>{}".format(
            1.0 * total_disk / (1024 * gigabyte), 100 * res_ratio, disk_data_used())
        send_mail(u"磁盘", content)


def check_mem():
    total_mem, res_mem, cache_mem = memory_stat()
    if 1.0 * (res_mem+cache_mem) / gigabyte < avail_mem:
        content = u"总内存{:.2f}G，仅剩{:.2f}G".format(
            1.0 * total_mem / gigabyte, 1.0 * (res_mem+cache_mem) / gigabyte)
        send_mail(u"内存", content)


def check_cpu():
    idle, sysuse, useruse = cpu_stat()
    if idle < avail_cpu:
        load = load_stat()
        content = u"空闲CPU仅剩{:.1f}%<br>{:.1f}%sy\t{:.1f}%us<br>load: {:.2f}(1分钟均值), {:.2f}(5分钟均值), {:.2f}(15分钟均值)".format(
            idle, sysuse, useruse, load[0], load[1], load[2])
        send_mail(u"CPU", content)


if __name__ == '__main__':
    # http://apscheduler.readthedocs.org/en/3.0/modules/schedulers/base.html#apscheduler.schedulers.base.BaseScheduler.add_job
    scheduler = BlockingScheduler()
    # 每隔5分钟检测一次内存
    scheduler.add_job(check_mem, 'interval', minutes=5)
    # 每隔10分钟检测一次CPU
    scheduler.add_job(check_cpu, 'interval', minutes=10)
    # 每隔30分钟检测一次磁盘
    scheduler.add_job(check_disk, 'interval', minutes=30)
    try:
        scheduler.start()
    except (KeyboardInterrupt, SystemExit):
        raise
    except Exception as e:
        print e
