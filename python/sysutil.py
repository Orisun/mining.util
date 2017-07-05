# coding:utf-8
__author__='orisun'

import subprocess as sub
import os
import sys
import string
import psutil
import re
import time

import utils


def use_memory(pid):
    '''返回进程的使用的(物理内存,虚拟内存)
    '''
    cmd_line = "ps -e -o 'pid,rsz,vsz' | grep {}".format(pid)
    try:
        proc = sub.Popen(cmd_line, shell=True,
                         stdout=sub.PIPE, stderr=sub.PIPE)
        out, err = proc.communicate()
        if proc.returncode != 0:
            print'cmd:({}); returncode:({}); out:({}); err:({})'.format(
                cmd_line, proc.returncode, out, err)
        else:
            return (out.split()[1:])
    except:
        pass


def memory_monitor(pid, outFile=None):
    '''每过1分钟探测一次进程pid使用的内存，将这个信息输出到文件outFile
    '''
    def prob_mem(pid, outFile):
        if outFile is not None:
            if not os.path.exists(outFile):
                os.mknod(outFile)
            elif os.path.isdir(outFile):
                os.remove(outFile)
                os.mknod(outFile)
        while True:
            mem = use_memory(pid)
            if outFile is not None:
                sys.stdout = open(outFile, 'a+')
            print "{time}\t{rs}k\t{vs}k".format(time=utils.nowStr(), rs=mem[0], vs=mem[1])
            try:
                time.sleep(60)
            except KeyboardInterrupt, SystemExit:
                raise
    from multiprocessing import Process
    p = Process(target=prob_mem, args=(pid, outFile,))
    p.start()

if __name__ == '__main__':
    pid = os.getpid()
    mem = use_memory(pid)
    print "pid={pid}, vitual memory={vs:,}k, real memory={rs:,}k".format(pid=pid, rs=int(mem[0]), vs=int(mem[1]))
    memory_monitor(pid, 'mem_use.txt')
    print 'go on'
