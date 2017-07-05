# coding:utf-8
__author__='orisun'

import sys
import random


def reservoir(iterator, k):
    '''蓄水池抽样法。从迭代器iterator中抽取k个元素
    '''
    rect = []
    num = 0
    for i in xrange(k):
        try:
            ele = iterator.next()
            num += 1
            rect.append(ele)
        except StopIteration:
            return rect
    for ele in iterator:
        num += 1
        rnd = random.randint(1, num + 1)
        if rnd < k:
            rect[rnd] = ele
    return rect


def extrct_by_group(expect_ratio, data_list, batch=5):
    '''分层抽样
       参考 http://www.cnblogs.com/zhangchaoyang/articles/4842281.html
    '''
    length = len(expect_ratio)
    if length == 0:
        raise StopIteration
    assert length == len(data_list)
    if batch > length:
        batch = length
    be_extracted = set()  # 已经被抽取的元素，用于排重
    extract_num = [0 for i in xrange(length)]  # 当前各组抽取的个数
    extract_rario = [0.0 for i in xrange(length)]  # 当前各位抽取的比例
    extract_total = 0  # 当前已抽取元素的总数
    empty_group = set()  # 已经被抽空的组
    while len(empty_group) < length:
        need_ratio = {i: expect_ratio[i] - extract_rario[i]
                      for i in xrange(length)}
        sorted_need_ratio = sorted(
            need_ratio.items(), lambda x, y: cmp(x[1], y[1]), reverse=True)
        for j in xrange(batch):  # 每次抽取batch个
            group_idx = sorted_need_ratio[j][0]
            group_deque = data_list[group_idx]
            if len(group_deque) > 0:
                data = group_deque.popleft()
                if data not in be_extracted:
                    be_extracted.add(data)
                    yield data
            else:
                '''
                # 当第1个列表抽空时把当前的抽取比例打印出来，验证一下extract_rario是否和expect_ratio相等
                print "sample ratio:", extract_rario
                print "expect ratio:", expect_ratio
                raise StopIteration
                '''
                empty_group.add(group_idx)
            extract_num[group_idx] += 1
        extract_total += batch
        extract_rario = [1.0 * extract_num[i] /
                         extract_total for i in xrange(length)]

if __name__ == '__main__':
    weight = [10, 9, 8, 7, 6, 5, 4, 3, 2, 1]
    num = 1000
    total_weight = sum(weight)
    expect_ratio = [1.0 * weight[i] /
                    total_weight for i in xrange(10)]  # 权重归一化从而得到期望抽取的比例
    for r in expect_ratio:
        print r
    print '=' * 10
    l0 = deque(['0_' + str(i) for i in xrange(num)])
    l1 = deque(['1_' + str(i) for i in xrange(num)])
    l2 = deque(['2_' + str(i) for i in xrange(num)])
    l3 = deque(['3_' + str(i) for i in xrange(num)])
    l4 = deque(['4_' + str(i) for i in xrange(num)])
    l5 = deque(['5_' + str(i) for i in xrange(num)])
    l6 = deque(['6_' + str(i) for i in xrange(num)])
    l7 = deque(['7_' + str(i) for i in xrange(num)])
    l8 = deque(['8_' + str(i) for i in xrange(num)])
    l9 = deque(['9_' + str(i) for i in xrange(num)])
    li = [l0, l1, l2, l3, l4, l5, l6, l7, l8, l9]
    actual_count = [0 for i in xrange(10)]
    i = 0
    for ele in extrct_by_group(expect_ratio, li):
        if i >= num:
            break
        index = int(ele.split('_')[0])
        actual_count[index] += 1
        i += 1
    for i in xrange(10):
        print 1.0 * actual_count[i] / num