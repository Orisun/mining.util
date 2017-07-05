#!/usr/bin/env python
# coding:utf8
__author__ = 'orisun'

import sys
import jieba


class Segger(object):

    def __init__(self):
        pass

    def load_userdict(self, dicFile):
        jieba.load_userdict(dicFile)

    def seg(self, text):
        """ @text: unicode (lowercase, no-full-pitches)
        return: list of terms(unicode)
        """
        try:
            return jieba.cut(text)
        except Exception as e:
            sys.stderr.write('Error in segger: text<%s>, error<%s>' %
                             (text.encode('utf8'), e))
            return


if __name__ == '__main__':
    segger = Segger()
    print u'|'.join(segger.seg(u'Python软件开发工程师  '.lower()))
    print u'|'.join(segger.seg(u'&ampJAVA&nbsp;开发工程师'.lower()))
