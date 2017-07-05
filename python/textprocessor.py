# coding:utf-8
__author__='orisun'

import re
import string

'''本模块中的所有方法输入和输出都是unicode
'''

backet_patten = re.compile(u"([\(（\[【].*?[\)）\]】])")
puncset = set()


def get_cont_in_backet(text):
    '''返回括号中的内容
    '''
    return backet_patten.findall(text)


def rm_backet(text):
    '''删除括号里的内容
    '''
    new_str, n = backet_patten.subn(u"", text)
    return new_str


def _full2half(char):
    '''全角转半角
    '''
    num = ord(char)
    # 空格特殊处理
    if num == 0x3000:
        return unichr(0x20)
    if num >= 0xFF01 and num <= 0xFF5E:
        return unichr(num - (0xFF01 - 0x21))
    return char


def full2half(text):
    '''全角转半角
    '''
    return ''.join(_full2half(char) for char in text)


def isChinese(char):
    '''
    判断一个unicode是否为汉字
    '''
    if char >= u'\u4e00' and char <= u'\u9fa5':
        return True
    else:
        return False


def isNumber(char):
    '''
    判断一个unicode是否为数字
    '''
    if char >= u'\u0030' and char <= u'\u0039':
        return True
    else:
        return False


def isAlphabet(char):
    '''
    判断一个unicode是否为英文字母
    '''
    if (char >= u'\u0041' and char <= u'\u005a') or (char >= u'\u0061' and char <= u'\u007a'):
        return True
    else:
        return False


def isPuncEN(char):
    '''
    判断是否为英文标点
    '''
    global puncset
    if len(puncset) == 0:
        puncset = set(string.punctuation)
    if char in puncset:
        return True
    return False


def isPuncZH(char):
    '''
    判断是否为中文标点。一个unicode既非数字、英文字母、英文标点，又非汉字，则为中文本标点
    '''
    if not (isChinese(char) or isNumber(char) or isAlphabet(char) or isPuncEN(char)):
        return True
    else:
        return False


def rmPunc(text):
    '''
    去除字符串的中标点符号。注意不会改变原字符串
    '''
    rect = ""
    for uchar in text:
        if isChinese(uchar) or isNumber(uchar) or isAlphabet(uchar):
            rect += uchar
    return rect


def rmPunc(text, exclude):
    '''
    去除字符串的中标点符号(包括空格，但是连接两个单词的空格不去除),在exclude中的符号不去除。注意不会改变原字符串
    '''
    rect = ""
    text = " ".join(text.split())  # 连续的空格用一个空格代替
    for i in range(len(text)):
        uchar = text[i]
        if isChinese(uchar) or isNumber(uchar) or isAlphabet(uchar) or uchar in exclude:
            rect += uchar
        else:
            code = ord(uchar)
            if code == 0x3000 or code == 0x0020:
                if i > 0 and i < len(text) - 1:
                    if (isNumber(text[i - 1]) or isAlphabet(text[i - 1]) or isPuncEN(text[i - 1])) and (isNumber(text[i + 1]) or isAlphabet(text[i + 1]) or isPuncEN(text[i + 1])):
                        rect += ' '
    return rect


def splitByPunc(text):
    '''
    字符串按照标点（包含中文标点和英文标点）进行分割
    '''
    rect = []
    word = ""
    for uchar in text:
        if not isPuncZH(uchar) and not isPuncEN(uchar):
            word += uchar
        else:
            if len(word) > 0:
                rect.append(word)
                word = ""
    if len(word) > 0:
        rect.append(word)
    return rect


def splitByPuncEx(text, exclude):
    '''
    字符串按照标点（包含中文标点和英文标点，但exclude中的标点除外）进行分割
    '''
    rect = []
    word = ""
    for uchar in text:
        if not isPuncZH(uchar) and not isPuncEN(uchar) or uchar in exclude:
            word += uchar
        else:
            if len(word) > 0:
                rect.append(word)
                word = ""
    if len(word) > 0:
        rect.append(word)
    return rect


def extractEnEle(text):
    '''
    抽取一个句子里面的英文元素(英文字母、数字、标点)，比如“你喜欢C++还是Java？”的抽取结果是[C++,Java]
    '''
    rect = []
    word = ""
    for uchar in text:
        if isAlphabet(uchar) or isNumber(uchar) or isPuncEN(uchar):
            word += uchar
        else:
            if len(word) > 0:
                rect.append(word)
                word = ""
    if len(word) > 0:
        rect.append(word)
    return rect
