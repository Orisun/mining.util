# coding:utf-8
__author__='orisun'

import threading


class ReadWriteLock(object):
    '''读写锁。且为读优先，即同时有多个读写线程在等待一个条件时，先处理读后处理写。
       这种锁适用对数据结构进行读的次数比写的次数多的场景。
    '''

    def __init__(self):
        self.__monitor = threading.Lock()
        self.__exclude = threading.Lock()
        self.readers = 0  # 记录当前有几个读锁，当readers>0时，写锁就得等待

    def acquire_read(self):
        with self.__monitor:  # 用于实现acquire_read和release_read互斥，因为这2个方法都要操作公共变量readers
            self.readers += 1
            if self.readers == 1:  # 读锁由无变有时，写锁需要等待
                self.__exclude.acquire()  # 用于跟写操作互斥
            return True

    def release_read(self):
        with self.__monitor:
            self.readers -= 1
            if self.readers == 0:  # 读锁由有变无时，写锁不需等待
                self.__exclude.release()
            return True

    def acquire_write(self):
        return self.__exclude.acquire()

    def release_write(self):
        return self.__exclude.release()


def __test1():
    lock = ReadWriteLock()
    if lock.acquire_read():
        print 'yes'
    if lock.acquire_read():
        print 'yes'
    if lock.acquire_write():
        print 'no'


def __test2():
    lock = ReadWriteLock()
    if lock.acquire_write():
        print 'yes'
    if lock.acquire_write():
        print 'no'


def __test3():
    lock = ReadWriteLock()
    if lock.acquire_write():
        print 'yes'
    if lock.acquire_read():
        print 'no'

if __name__ == '__main__':
    #__test1()
    #__test2()
    __test3()
