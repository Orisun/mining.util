# coding=utf-8
__author__='orisun'

import math

# 置信度到Z值的映射
z_value = {0.95: 1.96,
           0.9: 1.65,
           0.85: 1.44,
           0.8: 1.28,
           0.75: 1.15,
           0.7: 1.04,
           0.65: 0.94}


def wilsonFloor(num, n, confidence=0.9):
    assert n >= 0
    assert num <= 1
    assert confidence >= 0.65
    if n == 0:
        return 0
    # 向下取confidence的5%的整倍数
    confInt = int(confidence * 100)
    confInt = confInt / 5 * 5
    confFloat = 1.0 * confInt / 100
    z = z_value[confFloat]
    z2 = math.pow(z, 2.0)
    return (num + z2 / (2 * n) - z * math.sqrt(num * (1 - num) / n + z2 / (4 * n * n))) / (1 + z2 / n)
