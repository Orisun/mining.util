# coding=utf-8
__author__ = 'orisun'

import sys

def int2vint(num):
    arr = bytearray()
    while (num & ~0x7F) != 0:
        arr.append((num & 0x7F) | 0x80)
        num = (num & (2**32-1)) >> 7
    arr.append(num & 0x7F)
    return arr


def vint2int(arr):
    b = arr[0]
    num = b & 0x7F
    shift = 7
    i = 1
    while (b & 0x80) != 0:
        b = arr[i]
        num |= (b & 0x7F) << shift
        i += 1
        shift += 7
    if num > sys.maxint:
    	return 0 - (~(num-1) & sys.maxint)
    else:
    	return num

if __name__ == '__main__':
    for i in xrange(-100, 100, 1):
        arr = int2vint(i)
        j = vint2int(arr)
        if i != j:
        	print i,j
