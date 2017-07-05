# coding=utf-8
__author__ = 'orisun'

import heapq


class TopkHeap(object):
    '''用小顶堆实现求最大的K个元素。
       如果想求最小的K个元素，可以重载元素的__cmp__方法。
    '''

    def __init__(self, k):
        self.k = k
        self.data = []

    def push(self, elem):
        '''往小顶堆中添加一个元素，如果小于堆顶元素则丢弃
        '''
        if len(self.data) < self.k:
            heapq.heappush(self.data, elem)
        else:
            topk_small = self.data[0]
            if elem > topk_small:
                heapq.heapreplace(self.data, elem)

    def topK(self):
        '''从大到小依次取出最大的K个元素
        '''
        return [x for x in reversed([heapq.heappop(self.data) for x in xrange(len(self.data))])]

    
    def __len__(self):
        '''返回容器中元素的个数
        '''
        return len(self.data)

if __name__ == '__main__':
    class A(object):

        def __init__(self, name, age):
            self.name = name
            self.age = age

        def __cmp__(self, other):
            if isinstance(other, A):
                return cmp(self.age, other.age)  # 将从大到小输出最大的K个元素
                # return -cmp(self.age, other.age)  #将从小到大输出最小的K个元素
            return 0

    heap = TopkHeap(5)
    heap.push(A("", 5))
    heap.push(A("", 6))
    heap.push(A("", 8))
    heap.push(A("", 10))
    heap.push(A("", 11))
    heap.push(A("", 7))
    heap.push(A("", 9))
    for ele in heap.topK():
        print ele.age
