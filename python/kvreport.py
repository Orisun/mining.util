# coding:utf-8
__author__='orisun'

import socket
import time

PORT = 22345
HOST = "localhost"
s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)


def report(key, value):
    global HOST
    global PORT

    if key < 0:
        return
    try:
        msg = "\"{'ts':%d,'key':'%d','value':%f}\"" % (
            1000 * time.time(), key, value)
        s.sendto(msg, (HOST, PORT))
    except Error as e:
        raise e

if __name__ == '__main__':
    report(229, 1)
