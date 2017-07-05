# coding=utf-8
__author__='orisun'

def goodTuring(arr):
    '''
    goodTuring平滑，数组原先是非负整数，平滑后是正实数
    '''
    cnt_dict = {}  # 统计每个数字出现了多少次
    for ele in arr:
        cnt = 0
        if ele in cnt_dict:
            cnt = cnt_dict[ele]
        cnt_dict[ele] = 1 + cnt
    for i in range(len(arr)):
        arr[i] = 1.0 * (1 + arr[i]) * (cnt_dict[arr[i]] + 1) / cnt_dict[arr[i]]
