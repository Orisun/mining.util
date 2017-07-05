# coding:utf-8
__author__='orisun'

import sys
import jieba
import jieba.posseg as pseg


class Segger(object):

    def __init__(self, user_dict_file=None):
        if user_dict_file:
            jieba.load_userdict(user_dict_file)
        else:
            pass

    def add_user_dict(self, user_dict_file):
        jieba.load_userdict(user_dict_file)

    def seg(self, text):
        try:
            return jieba.cut(text)
        except Exception as e:
            sys.stderr.write("seg <{}> failed, error: {}".format(
                text.encode("utf8"), e))

    def seg_with_flag(self, text):
        try:
            return pseg.cut(text)
        except Exception as e:
            sys.stderr.write("pos seg <{}> failed, error: {}".format(
                text.encode("utf8"), e))

if __name__ == '__main__':
    segger = Segger("cn.dic")			#把“安居客”、“.net”加入词典
    segger.add_user_dict("html.dic")	#把“&amp”、“&nbsp”加入词典
    print u'|'.join(segger.seg(u'Python软件开发工程师  '.lower()))
    print u'|'.join(segger.seg(u'&ampJAVA&nbsp;开发工程师'.lower()))
    for w in segger.seg_with_flag(u'安居客诚聘c/c++ .NET c#工程师【可实习】'.lower()):
        print w.word, w.flag

'''
输出：
python|软件开发|工程师| | 
&amp|java|&nbsp|;|开发|工程师
安居客 x
诚聘 v
c x
/ x
c++ nz
  x
.net x
  x
c# nz
工程师 n
【 x
可 v
实习 v
】 x
'''