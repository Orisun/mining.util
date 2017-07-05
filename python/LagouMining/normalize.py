# coding=utf-8
__author__='orisun'

import math
import numpy


def mean_var(arr):
    '''计算均值和方差
    '''
    if isinstance(arr, numpy.ndarray) and arr.any():
        narray = arr
    elif isinstance(arr, list) and len(arr) > 0:
        narray = numpy.array(arr)
    else:
        raise ValueError('arg must be none empty numpy.ndarray or list')
    N = narray.shape[0]
    sum1 = narray.sum()
    narray2 = narray * narray
    sum2 = narray2.sum()
    mean = sum1 / N
    var = sum2 / N - mean**2
    return mean, var


def gauss(arr):
    '''
    高斯规一化：减去均值，除以标准差
    '''
    mean, var = mean_var(arr)
    var = 1 if var == 0 else var   # 如果方差是0，要把它变为1，否则会出现除异常
    standardVar = math.sqrt(var)  # 标准差
    rect = []
    for i in range(len(arr)):
        arr[i] = (arr[i] - mean) / standardVar  # 减去均值，除以标准差
    '''
    if (isinstance(arr, numpy.ndarray) and arr.any()) or (isinstance(arr, list) and len(arr) > 0):
        squareSum = 0.0  # 各元素平方和
        total = 0.0  # 各元素之和
        for ele in arr:
            squareSum += ele * ele
            total += ele
        mean = total / len(arr)  # 均值
        var = squareSum / len(arr) - mean * mean  # 方差
        var = 1 if var == 0 else var   # 如果方差是0，要把它变为1，否则会出现除异常
        standardVar = math.sqrt(var)  # 标准差
        rect = []
        for i in range(len(arr)):
            arr[i] = (arr[i] - mean) / standardVar  # 减去均值，除以标准差
    '''


def rmNoise(arr):
    '''
    利用高斯转换去噪.
    有68.26％的平均数落在μ±1标准误之间;
    有95％的平均数落在μ±1.96标准误之间;
    有99％的平均数落在μ±2.58标准误之间.
    '''
    if (isinstance(arr, numpy.ndarray) and arr.any()) or (isinstance(arr, list) and len(arr) > 0):
        squareSum = 0.0  # 各元素平方和
        total = 0.0  # 各元素之和
        for ele in arr:
            squareSum += ele * ele
            total += ele
        mean = total / len(arr)  # 均值
        var = squareSum / len(arr) - mean * mean  # 方差
        var = 1 if var == 0 else var   # 如果方差是0，要把它变为1，否则会出现除异常
        standardVar = math.sqrt(var)  # 标准差
        rect = []
        for i in range(len(arr)):
            data = (arr[i] - mean) / standardVar  # 减去均值，除以标准差
            if data < -1:
                arr[i] = mean - standardVar
            if data > 1:
                arr[i] = mean + standardVar


def linear(arr):
    '''
    线性规一化：y=(x-MinValue)/(MaxValue-MinValue)
    '''
    if (isinstance(arr, numpy.ndarray) and arr.any()) or (isinstance(arr, list) and len(arr) > 0):
        maxValue = max(arr)
        minValue = min(arr)
        if minValue == maxValue:
            for i in range(len(arr)):
                arr[i] = 1

        else:
            for i in range(len(arr)):
                arr[i] = 1.0 * (arr[i] - minValue) / (maxValue - minValue)

if __name__ == '__main__':
    arr = [1, 2, 3, 4, 5, 6, 7, 8, 9]
    print mean_var(arr)
    gauss(arr)
    print arr

    delay_list = list(numpy.random.uniform(0, 6000000, size=300000))
    mean, var = mean_var(delay_list)
    var = 1 if var == 0 else var   # 如果方差是0，要把它变为1，否则会出现除异常
    std_var = math.sqrt(var)  # 标准差
    print(mean, std_var)
