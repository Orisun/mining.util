# coding=utf-8
__author__ = "orisun"

import struct    # 把结构体转成二进制
import numpy as np
import bsddb
from cStringIO import StringIO    # 用操纵文件的方式来操纵字符串。cStringIO.StringIO比StringIO性能好一些
import searchutil


def getCooMatrixElement(coomatrix, row, col):
    '''scipy中的coo_matrix本身不支持按下标查找元素，此函数就是从一个coo_matrix中查找下标为[row,col]的元素，找不到则返回None。
       coomatrix为coo_matrix类型
    '''
    n = coomatrix.shape[0]
    row_index = coomatrix.row
    col_index = coomatrix.col
    # print row_index
    # print col_index
    target = row + 1
    # print 'target=',target
    row_end = searchutil.biggestLessThan(target, row_index)
    # print 'row_end=',row_end
    for i in xrange(row_end, -1, -1):
        if row_index[i] == row:
            if col_index[i] == col:
                return coomatrix.data[i]
        else:
            break
    return None


class DictMatrix():
    '''用dict来实现稀疏矩阵
    '''

    def __init__(self, dft=0.0):
        self._data = {}
        self._dft = dft  # “0元素”的值
        self._nums = 0  # 稀疏矩阵中非0元素的个数

    def __setitem__(self, index, value):
        try:
            i, j = index
        except:
            raise IndexError('invalid index')

        # 为了节省内存，我们把j, value打包成字二进制字符串
        ik = ('i%d' % i)
        ib = struct.pack('if', j, value)  # 格式化：i代替integer，f代表float。pack方法返回字符串
        jk = ('j%d' % j)
        jb = struct.pack('if', i, value)

        try:
            self._data[ik] += ib  # 拼接字符串
        except:
            self._data[ik] = ib
        try:
            self._data[jk] += jb
        except:
            self._data[jk] = jb
        self._nums += 1

    def __getitem__(self, index):
        try:
            i, j = index
        except:
            raise IndexError('invalid index')

        if (isinstance(i, int)):
            ik = ('i%d' % i)
            if not self._data.has_key(ik):
                return self._dft
            # j不是个数字时，输出第i行
            ret = dict(np.fromstring(self._data[ik], dtype='i4,f4'))
            if (isinstance(j, int)):
                return ret.get(j, self._dft)

        if (isinstance(j, int)):
            jk = ('j%d' % j)
            if not self._data.has_key(jk):
                return self._dft
            # i不是个数字时，输出第j列
            ret = dict(np.fromstring(self._data[jk], dtype='i4,f4'))

        return ret

    def __len__(self):
        return self._nums

    def __iter__(self):
        pass

    def read(self, cache):
        '''cache是一个list，其中的每个元素都是个三元组(row,col,value)。
           从磁盘中加载稀疏矩阵时，可以先把部分数据加载到cache中，再从cache放到DictMatrix中。
        '''
        tmpDict = {}
        for row, col, value in cache:
            if value != self._dft:  # 确保添加的是“非0”元素
                ik = ('i%d' % row)
                ib = struct.pack('if', col, value)
                jk = ('j%d' % col)
                jb = struct.pack('if', row, value)

                try:
                    tmpDict[ik].write(ib)
                except:
                    # 考虑到字符串拼接性能不太好，我们直接用StringIO的write()来做拼接
                    tmpDict[ik] = StringIO()
                    tmpDict[ik].write(ib)

                try:
                    tmpDict[jk].write(jb)
                except:
                    tmpDict[jk] = StringIO()
                    tmpDict[jk].write(jb)

                self._nums += 1

        for k, v in tmpDict.items():
            v.seek(0)
            s = v.read()
            try:
                self._data[k] += s
            except:
                self._data[k] = s


if __name__ == '__main__':
    
    from scipy.sparse import coo_matrix, lil_matrix

    arr = lil_matrix((300, 400))
    arr[299, 399] = 1
    arr[250, 390] = 1
    arr[250, 8] = 1
    arr[250, 100] = 1
    arr[299, 390] = 1
    arr[299, 8] = 1
    arr[299, 100] = 1
    arr[50, 390] = 1
    arr[50, 8] = 1
    arr[50, 100] = 1
    arr[0, 399] = 1
    brr = coo_matrix(arr)
    print getCooMatrixElement(brr, 299, 399)
    print getCooMatrixElement(brr, 299, 380)
    print getCooMatrixElement(brr, 250, 8)
    print getCooMatrixElement(brr, 250, 18)
    print getCooMatrixElement(brr, 250, 100)
    print getCooMatrixElement(brr, 250, 90)
    print getCooMatrixElement(brr, 0, 399)
    print getCooMatrixElement(brr, 0, 0)
    print

    arr = np.array([[4, 0, 2], [1, 0, 0], [0, 0, 0]])
    brr = coo_matrix(arr)
    print getCooMatrixElement(brr, 0, 2)
    print

    n = 1246697
    arr = lil_matrix((n, n))
    arr[0, 963628] = 1.0
    arr[0, 963629] = 1.0
    arr[0, 963630] = 1.0
    arr[0, 963631] = 1.0
    arr[1, 963650] = 1.0
    brr = coo_matrix(arr, shape=(n, n))
    import time
    begin = time.time()
    print getCooMatrixElement(brr, 0, 963629)
    end = time.time()
    print 'use time', (end - begin)
    
    matrix = DictMatrix()
    matrix[1, 9] = 58.8
    matrix[1, 16] = 20.0
    matrix[2, 16] = 0.9
    print matrix[1, 'a']  # 输出第1行
    print matrix['a', 16]  # 输出第16列
    print matrix[1, 9]
    print matrix[1, 16]
    print matrix[2, 16]
    print matrix[2, 9]
    print len(matrix)
    matrix.read([(3, 3, 15.5), (9, 3, 100.0)])      #批量添加数据
    print matrix[1, 9]
    print matrix[3, 3]
    print matrix[9, 3]
    print len(matrix)
