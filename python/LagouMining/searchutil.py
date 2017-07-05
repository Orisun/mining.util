# coding:utf-8
__author__ = 'orisun'

import math


def biggestLessThan(target, sortedSeq, comparator=cmp):
    '''从一个排好序的序列中找到比target小的最大的那个元素，返回其下标。
       当序列中所有元素都大于target时，返回-1
    '''
    if sortedSeq is None or len(sortedSeq) == 0:
        return -1
    low = 0
    high = len(sortedSeq)
    mid = (low + high) / 2
    # tn = 0
    while low < mid:
        # tn += 1
        if comparator(sortedSeq[mid], target) >= 0:
            high = mid
        else:
            low = mid
        mid = (low + high) / 2
    # print 'try ', tn, 'times '
    return low if comparator(sortedSeq[low], target) < 0 else low - 1


def biggestLessThan_kfold(target, sortedSeq,  comparator=cmp):
    '''从一个排好序的序列中找到比target小的最大的那个元素，返回其下标。
       当序列中所有元素都大于target时，返回-1。
       此方法要求sortedSeq中的元素类型支持减法"-"操作。
       相对于biggestLessThan，biggestLessThan_kfold虽然查找的次数少，但是每次计算ratio的开销大。我们在大数组上试验发现，biggestLessThan_kfold耗时是biggestLessThan的4倍多
    '''
    if sortedSeq is None or len(sortedSeq) == 0:
        return -1

    if target > sortedSeq[-1]:
        return len(sortedSeq) - 1
    size = len(sortedSeq)
    low = 0
    high = size - 1
    min_value = sortedSeq[low]
    max_value = sortedSeq[high]
    if(max_value <= min_value):
        return -1
    # 折半查找不一定是最优的，在假设sortedSeq中的元素服从均匀分布的前提下，先预估一下target可能出现的位置
    ratio = 1.0 * (target - min_value) / (max_value - min_value)
    if ratio >= 1.0:
        ratio = 1 - 1.0 / (2 * size)
    # print 'ratio=%f' % ratio
    mid = int(math.ceil(ratio * high + (1 - ratio) * low))
    # print 'mid=%d' % mid
    tn = 0
    while low < mid:
        tn += 1
        if comparator(sortedSeq[mid], target) >= 0:
            high = mid - 1
            # print 'high = ', high
        else:
            low = mid + 1
            # print 'low = ', low
        min_value = sortedSeq[low]
        max_value = sortedSeq[high]
        if(max_value <= min_value):
            # print 'max_value=%f,min_value=%f' % (max_value, min_value)
            low = high
            break
        ratio = 1.0 * (target - min_value) / (max_value - min_value)
        if ratio >= 1.0:
            ratio = 1 - 1.0 / (2 * size)
        # print 'ratio=%f' % ratio
        mid = int(math.ceil(ratio * high + (1 - ratio) * low))
        # print 'mid=%d' % mid
    # print 'try ', tn, 'times '
    return low if comparator(sortedSeq[low], target) < 0 else low - 1

if __name__ == '__main__':
    import time
    import sys

    print biggestLessThan(0, [0])
    print biggestLessThan_kfold(0, [0])
    print

    print biggestLessThan(0, [0, 0])
    print biggestLessThan_kfold(0, [0, 0])
    print

    print biggestLessThan(1, [0, 0])
    print biggestLessThan_kfold(1, [0, 0])
    print

    print biggestLessThan(1, [0, 0, 1])
    print biggestLessThan_kfold(1, [0, 0, 1])
    print

    print biggestLessThan(1, [0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 8, 12, 34, 1246696])
    print biggestLessThan_kfold(1, [0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 8, 12, 34, 1246696])
    print

    import datetime
    today = datetime.datetime.today()
    arr = [today + datetime.timedelta(days=-2), today,
           today + datetime.timedelta(days=2)]
    yestoday = today + datetime.timedelta(days=-1)
    print biggestLessThan(yestoday, arr, comparator=lambda x, y: time.mktime(x.timetuple()) - time.mktime(y.timetuple()))
    print

    seq = [3, 5, 5, 5, 5, 7, 9]
    for i in xrange(11):
        print i, biggestLessThan(i, seq, comparator=lambda x, y: x - y)
        print i, biggestLessThan_kfold(i, seq,  comparator=lambda x, y: x - y)
        print

    seq = []
    with open("../../data/row.txt") as f_in:
        for line in f_in:
            cont = line.strip()
            if len(cont) > 0:
                seq.append(int(cont))
    print 'begin'
    t1 = time.time()
    a = biggestLessThan(15, seq)
    t2 = time.time()
    print 'find in', a, 'use time', (t2 - t1)
    t3 = time.time()
    b = biggestLessThan_kfold(15, seq)
    t4 = time.time()
    print 'find in', b, 'use time', (t4 - t3)
    print
    t1 = time.time()
    a = biggestLessThan(14, seq)
    t2 = time.time()
    print 'find in', a, 'use time', (t2 - t1)
    t3 = time.time()
    b = biggestLessThan_kfold(14, seq)
    t4 = time.time()
    print 'find in', b, 'use time', (t4 - t3)
    print

    t1 = time.time()
    for i in xrange(1246697):
        a = biggestLessThan(i, seq)
        b = biggestLessThan_kfold(i, seq)
        if a != b:
            print a, b
        assert a == b
    t2 = time.time()
    print 'use time', (t2 - t1)

    t1 = time.time()
    for i in xrange(1246697):
        biggestLessThan(i, seq)
    t2 = time.time()
    print 'use time', (t2 - t1)

    t1 = time.time()
    for i in xrange(1246697):
        biggestLessThan_kfold(i, seq)
    t2 = time.time()
    print 'use time', (t2 - t1)
