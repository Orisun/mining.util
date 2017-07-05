# coding=utf-8
__author__='orisun'

import re
import os

def touchDir(outDir):
    '''
    创建目录，如果目录已存在则清空目录。当有同名的文件时该文件会被删除
    '''
    if os.path.exists(outDir):
        if os.path.isdir(outDir):
            files=os.listdir(outDir)
            for file in files:
                if os.path.exists(outDir+"/"+file):
                    os.remove(outDir+"/"+file)
        else:
            os.remove(outDir)
            os.makedirs(outDir)
    else:
        os.makedirs(outDir)


def divideData(infile, outDir, col, mod, separator='\s+'):
    '''
    对数据进行切分。
    infile的每行按separator分隔成多列，第col列必须是数字(col从0开始编号)，
    这个数字对mod取模得到数字n，该行记录输出到outDir的第n个文件里。
    '''
    digital_pat = re.compile('\d+')
    touchDir(outDir)
    with open(infile, 'r') as f_in:
        f_outs = []
        for i in range(mod):
            f_out = open(outDir + "/" + str(i), 'w')
            f_outs.append(f_out)

        while True:
            line = f_in.readline()
            if line:
                arr = re.split(separator, line.strip('\n'))
                if len(arr) > col:
                    tail = arr[col][-4:]
                    if digital_pat.match(tail):
                        idx = int(tail) % mod
                        f_outs[idx].writelines(line)
            else:
                break

        for i in range(mod):
            f_outs[i].close()


def mergeFile(outFile,fileList):
    '''
    把多个文件合并成一个文件
    '''
    f_out=open(outFile,'w')
    for i in range(len(fileList)):
        f_in=open(fileList[i],'r')
        while True:
            line=f_in.readline()
            if line:
                f_out.writelines(line)
            else:
                break
        f_in.close()
    f_out.close()


def get_last_n_lines(logfile, n):
    '''
    读取文件的最后n行（要求换行符必须是\n）
    '''
    blk_size_max = 4096
    n_lines = []
    with open(logfile, 'rb') as fp:
        fp.seek(0, os.SEEK_END)     # 定位到文件尾部
        cur_pos = fp.tell()
        while cur_pos > 0 and len(n_lines) < n:
            blk_size = min(blk_size_max, cur_pos)
            fp.seek(cur_pos - blk_size, os.SEEK_SET)
            blk_data = fp.read(blk_size)
            assert len(blk_data) == blk_size
            lines = blk_data.split('\n')

            # adjust cur_pos
            if len(lines) > 1 and len(lines[0]) > 0:
                n_lines[0:0] = lines[1:]
                cur_pos -= (blk_size - len(lines[0]))
            else:
                n_lines[0:0] = lines
                cur_pos -= blk_size
            fp.seek(cur_pos, os.SEEK_SET)

    if len(n_lines) > 0 and len(n_lines[-1]) == 0:
        del n_lines[-1]
    return n_lines[-n:]