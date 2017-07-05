# coding:utf-8
__author__='orisun'

'''封装常用的MySQL增删改查操作。目前不支持查询超时设置
'''

import time
import sys
from datetime import datetime

import MySQLdb
from connection import ConnectionPoolFactory, ConnectionPool, Connection
from orm import PO, Integer, Long
from logger import LoggerFactory
logger = LoggerFactory.getLogger()


class BaseDao(object):

    # SQL及脚本关键词，用于防止SQl注入攻击和XSS攻击
    sql_key_words = set(["and", "or", "insert", "select", "delete", "update", "count", "chr", "mid", "truncate",
                         "trunc", "char", "declare", "like", "%", "<", ">", "=", "\"", "'", ")", "(", "script", "alert"])

    def _containSQL(self, sqlstr):
        arr = sqlstr.split()
        for ele in arr:
            if ele in self.sql_key_words:
                return True
        return False

    def __init__(self, entity_cls):
        if entity_cls.__base__ == PO:
            self.entity_cls = entity_cls
            po = self.entity_cls()
            self.all_columns = po.all_columns()
            self.all_columns_without_pk = po.all_columns_without_pk()
            # 读写MySQL时一次传输的数据量如果太大，会报异常：MySQL server has gone away
            # 因为MySQL有一些限制，比如max_allowed_packet(默认为1M)、connect_timeout=120、wait_timeout
            self.batch_limit = 500  # 每次最查询或写入的记录数
        else:
            logger.error("class is not instance of PO")

    def _getConn(self):
        if not self.entity_cls:
            return None
        return ConnectionPoolFactory.getConnectionPool(
                self.entity_cls.DB).getConnection()

    def _data2po(self, data):
        '''由一行DB记录构造一个PO
        '''
        po = self.entity_cls()
        for k in data:
            if k in po.column_attr:
                setattr(po, k, data[k])
        return po

    def getListByPage(self, where, pageno, pagesize, columns="*", order=None):
        '''分页按条件获取指定的列
        '''
        if not self.entity_cls:
            return None
        if columns == None or len(columns) == 0:
            return None
        if columns == "*":
            columns = self.all_columns

        MAX_PAGE_SIZE = self.batch_limit
        loop = (pagesize - 1) / MAX_PAGE_SIZE + 1
        rect = []
        for i in xrange(loop):
            sql = []
            sql.append('select')
            sql.append(columns)
            sql.append('from')
            sql.append(self.entity_cls.TABLE)
            if where and len(where) > 0:
                sql.append('where')
                sql.append(where)
            if order:
                sql.append('order by')
                sql.append(order)
            sql.append('limit')
            sql.append(str(pagesize * (pageno - 1) + i * MAX_PAGE_SIZE))
            sql.append(',')
            new_page_size = min(MAX_PAGE_SIZE, pagesize - i * MAX_PAGE_SIZE)
            sql.append(str(new_page_size))
            sqlstr = ' '.join(sql)
            conn = self._getConn()
            alldata = None
            cursor = conn.cursor()
            try:
                cursor.execute(sqlstr)
                alldata = cursor.fetchall()
            except MySQLdb.Error as e:
                logger.error(
                    'read db failed:{}\n sql={}'.format(e.args[1], sqlstr))
                return rect
            finally:
                cursor.close()
                conn.close()
            if alldata and len(alldata) > 0:
                for data in alldata:
                    rect.append(self._data2po(data))
                if len(alldata) < new_page_size:
                    break
            else:
                break
        return rect

    def get(self, where, columns="*", order=None):
        '''按条件获取指定的列
        '''
        return self.getListByPage(where, 1, sys.maxint, columns=columns, order=None)

    def getById(self, recordid, columns="*"):
        '''按主键获取一行记录
        '''
        if not self.entity_cls:
            return None
        if columns == "*":
            columns = self.all_columns
        sqlstr = 'select ' + columns + ' from ' + \
            self.entity_cls.TABLE + ' where ' + \
            self.entity_cls.ID + '=' + str(recordid)
        conn = self._getConn()
        data = None
        cursor = conn.cursor()
        try:
            cursor.execute(sqlstr)
            data = cursor.fetchone()
        except MySQLdb.Error as e:
            logger.error(
                'read db failed:{}\n sql={}'.format(e.args[1], sqlstr))
            return None
        finally:
            cursor.close()
            conn.close()
        rect = None
        if data:
            rect = self._data2po(data)
        return rect

    def getDistinctValue(self, column, condition=None):
        if not self.entity_cls:
            return None
        if column == None or len(column) == 0:
            return None
        if not condition:
            condition = "1=1"
        sqlstr = 'select distinct(' + column + ') from ' + \
            self.entity_cls.TABLE + ' where ' + condition
        conn = self._getConn()
        cursor = conn.cursor()
        alldata = None
        try:
            cursor.execute(sqlstr)
            alldata = cursor.fetchall()
        except MySQLdb.Error as e:
            logger.error(
                'read db failed:{}\n sql={}'.format(e.args[1], sqlstr))
            return []
        finally:
            cursor.close()
            conn.close()
        rect = []
        is_int = False
        if isinstance(self.entity_cls.__dict__[column], Integer):
            is_int = True
        is_long = False
        if isinstance(self.entity_cls.__dict__[column], Long):
            is_long = True
        if alldata and len(alldata) > 0:
            for data in alldata:
                if is_int:
                    rect.append(int(data[column]))
                elif is_long:
                    rect.append(long(data[column]))
                else:
                    rect.append(data[column])
        return rect

    def getIn(self, value_list, search_column, get_columns="*"):
        '''value_list只支持Number类型，value_list的长度不能超过500
        '''
        if not self.entity_cls:
            return None
        if search_column == None or len(search_column) == 0:
            return None
        if get_columns == None or len(get_columns) == 0:
            return None
        if value_list == None or len(value_list) == 0:
            return None
        if len(value_list) > self.batch_limit:
            logger.error(
                'can not get more than {:d} records at once'.format(self.batch_limit))
            return None
        if get_columns == "*":
            get_columns = self.all_columns
        sql = []
        sql.append('select')
        sql.append(get_columns)
        sql.append('from')
        sql.append(self.entity_cls.TABLE)
        sql.append('where')
        sql.append(search_column)
        sql.append('in')
        sql.append('(')
        sql.append(','.join(map(str, value_list)))
        sql.append(')')
        sqlstr = ' '.join(sql)
        conn = self._getConn()
        cursor = conn.cursor()
        alldata = None
        rect = []
        try:
            cursor.execute(sqlstr)
            alldata = cursor.fetchall()
        except MySQLdb.Error as e:
            logger.error(
                'read db failed:{}\n sql={}'.format(e.args[1], sqlstr))
            return rect
        finally:
            cursor.close()
            conn.close()
        if alldata and len(alldata) > 0:
            for data in alldata:
                rect.append(self._data2po(data))
        return rect

    def getMaxAndMin(self, column):
        '''获取某列的最大和最小值。如果该列既非Integer，亦非Long，或者表为空，则返回(None, None)
        '''
        if column not in self.entity_cls.__dict__:
            return (None, None)
        if not (isinstance(self.entity_cls.__dict__[column], Integer) or isinstance(self.entity_cls.__dict__[column], Long)):
            return (None, None)
        sqlstr = 'select min({key}) as min, max({key}) as max from {table}'.format(
            key=column, table=self.entity_cls.TABLE)
        conn = self._getConn()
        cursor = conn.cursor()
        data = None
        try:
            cursor.execute(sqlstr)
            data = cursor.fetchone()
        except MySQLdb.Error as e:
            logger.error('get max and min on {} failed:{}\n sql={}'.format(
                column, e.args[1], sqlstr))
            return (None, None)
        finally:
            cursor.close()
            conn.close()
        if data['min'] and data['max']:
            if isinstance(self.entity_cls.__dict__[column], Integer):
                return (int(data['min']), int(data['max']))
            elif isinstance(self.entity_cls.__dict__[column], Long):
                return (long(data['min']), long(data['max']))
        else:
            return (None, None)

    def delete(self, where):
        if not self.entity_cls:
            return
        if where == None or len(where) == 0:
            return
        sqlstr = 'delete from ' + \
            self.entity_cls.TABLE + ' where ' + where
        conn = self._getConn()
        cursor = conn.cursor()
        try:
            cursor.execute(sqlstr)
        except MySQLdb.Error as e:
            logger.error(
                'delete from db failed:{}\n sql={}'.format(e.args[1], sqlstr))
        finally:
            cursor.close()
            conn.close()

    def deleteIn(self, id_list):
        '''只要主键在指定列表中的，全部删除
        '''
        if not id_list or len(id_list) == 0:
            return
        where = "{} in ({})".format(self.entity_cls.ID,
                                    ','.join(map(str, id_list)))
        self.delete(where)

    def deleteById(self, id):
        where = '{}={}'.format(self.entity_cls.ID, id)
        self.delete(where)

    def deleteAll(self):
        where = '1=1'
        self.delete(where)

    def count(self, where='1=1'):
        if not self.entity_cls:
            return
        sqlstr = 'select count(*) as count from ' + \
            self.entity_cls.TABLE + ' where ' + where
        conn = self._getConn()
        cursor = conn.cursor()
        rect = 0
        try:
            cursor.execute(sqlstr)
            data = cursor.fetchone()
            if data:
                rect = data['count']
        except MySQLdb.Error as e:
            logger.error(
                'delete from db failed:{}\n sql={}'.format(e.args[1], sqlstr))
            return rect
        finally:
            cursor.close()
            conn.close()
        return rect

    def insert(self, po):
        if not self.entity_cls:
            return
        if not po:
            return
        if not isinstance(po, PO):
            logger.error(
                'Expect instance of PO, but is {}'.format(str(type(value))))
            return
        columns = []
        values = []
        for attr in po.column_attr.keys():
            if po.PK_AUTO_CREATE and attr == po.ID:
                continue
            value = getattr(po, attr)
            if isinstance(value, int) or isinstance(value, float) or isinstance(value, long):
                values.append(str(value))
            elif isinstance(value, str):
                if self._containSQL(value):
                    logger.error('SQL injection: {}'.format(value))
                    return
                values.append("'" + value + "'")
            elif isinstance(value, datetime):
                values.append("'" + str(value)[:19] + "'")
            elif value is None:
                values.append('null')
            else:
                logger.error('type {} is not valid'.format(type(value)))
                return
            columns.append(attr)
        sqlstr = 'insert into ' + self.entity_cls.TABLE + \
            ' (' + ','.join(columns) + ') values (' + ','.join(values) + ')'
        conn = self._getConn()
        cursor = conn.cursor()
        try:
            cursor.execute(sqlstr)
        except MySQLdb.Error as e:
            logger.error(
                'insert db failed:{}\n sql={}'.format(e.args[1], sqlstr))
            return
        finally:
            cursor.close()
            conn.close()

    def batchInsert(self, poList):
        if not self.entity_cls:
            return
        if not poList or len(poList) == 0:
            return
        sql = []
        sql.append('insert into')
        sql.append(self.entity_cls.TABLE)
        sql.append('(')
        po = poList[0]
        if not isinstance(po, PO):
            logger.error(
                'Expect instance of PO, but is {}'.format(str(type(value))))
            return
        if self.entity_cls.PK_AUTO_CREATE:
            sql.append(self.all_columns_without_pk)
        else:
            sql.append(self.all_columns)
        sql.append(') values')
        for po in poList:
            sql.append('(')
            values = []
            for attr in po.column_attr.keys():
                if po.PK_AUTO_CREATE and attr == po.ID:
                    continue
                value = getattr(po, attr)
                if isinstance(value, int) or isinstance(value, float) or isinstance(value, long):
                    values.append(str(value))
                elif isinstance(value, str):
                    values.append("'" + value + "'")
                elif isinstance(value, datetime):
                    values.append("'" + str(value)[:19] + "'")
                elif value is None:
                    values.append('null')
                else:
                    logger.error('type {} is not valid'.format(type(value)))
                    return
            sql.append(','.join(values))
            sql.append('),')
        sqlstr = ' '.join(sql)
        sqlstr = sqlstr[:-1]
        conn = self._getConn()
        cursor = conn.cursor()
        try:
            cursor.execute(sqlstr)
        except MySQLdb.Error as e:
            logger.error(
                'batch insert db failed:{}\n sql={}'.format(e.args[1], sqlstr))
            return
        finally:
            cursor.close()
            conn.close()

    def update(self, po):
        '''要求主键必须唯一
        '''
        if not self.entity_cls:
            return
        if not po:
            return
        if not isinstance(po, PO):
            logger.error(
                'Expect instance of PO, but is {}'.format(str(type(value))))
            return
        sql = []
        sql.append('update')
        sql.append(self.entity_cls.TABLE)
        sql.append('set')
        set_list = []
        for attr, value in vars(po).items():
            if not attr in po.column_attr:
                continue
            if attr != self.entity_cls.ID:
                if isinstance(value, int) or isinstance(value, float) or isinstance(value, long):
                    set_list.append(attr + '=' + str(value))
                elif isinstance(value, str):
                    set_list.append(
                        attr + '="' + str(value) + '"')
                elif isinstance(value, datetime):
                    set_list.append(attr + '="' + str(value)[:19] + '"')
                elif value is None:
                    set_list.append(attr + '="null"')
                else:
                    logger.error('type {} is not valid'.format(type(value)))
                    return
        sql.append(','.join(set_list))
        sql.append('where')
        sql.append(self.entity_cls.ID)
        sql.append('=')
        sql.append(str(po.__dict__[self.entity_cls.ID]))
        sqlstr = ' '.join(sql)
        conn = self._getConn()
        cursor = conn.cursor()
        try:
            cursor.execute(sqlstr)
        except MySQLdb.Error as e:
            logger.error(
                'update table failed:{}\n sql={}'.format(e.args[1], sqlstr))
            return
        finally:
            cursor.close()
            conn.close()

    def _check_type(self, value_dict):
        '''检查value_dict中指定的各字段的值是否和数据库的类型匹配，如果不匹配抛出TypeError异常
        '''
        po = self.entity_cls()
        try:
            for attr, value in value_dict.items():
                po.attr = value
        except TypeError as e:
            raise e

    def update_column(self, record_id, new_value):
        '''根据record_id更新数据。new_value是个dict,里面存放着需要更新的列及其值
        '''
        if not self.entity_cls:
            return
        try:
            self._check_type(new_value)
        except TypeError as e:
            logger.error('check type error:', e)
            return
        sql = []
        sql.append('update')
        sql.append(self.entity_cls.TABLE)
        sql.append('set')
        set_list = []
        for attr, value in new_value.items():
            if isinstance(value, int) or isinstance(value, float) or isinstance(value, long):
                set_list.append(attr + '=' + str(value))
            elif isinstance(value, str):
                set_list.append(
                    attr + '="' + str(value) + '"')
            elif isinstance(value, datetime):
                set_list.append(attr + '="' + str(value)[:19] + '"')
            elif value is None:
                set_list.append(attr + '="null"')
            else:
                logger.error('type {} is not valid'.format(type(value)))
                return
        sql.append(','.join(set_list))
        sql.append('where')
        sql.append(self.entity_cls.ID)
        sql.append('=')
        sql.append(str(record_id))
        sqlstr = ' '.join(sql)
        conn = self._getConn()
        cursor = conn.cursor()
        try:
            cursor.execute(sqlstr)
        except MySQLdb.Error as e:
            logger.error(
                'update table failed:{}\n sql={}'.format(e.args[1], sqlstr))
            return
        finally:
            cursor.close()
            conn.close()


if __name__ == '__main__':
    # 初始化数据库连接
    ConnectionPoolFactory.setConfFile("../../conf/db.conf.offline")

    from orm import PO, Integer, Float, String, Date, Long

    class NegativeFeedback(PO):

        DB = "lagou_rec"
        TABLE = "negative_feedback"
        ID = "id"

        id = Long()
        userid = Long()
        positionid = Long()
        commit_time = Date()
        unlike_reason = String()
    # 创建dao
    dao = BaseDao(NegativeFeedback)

    # 单条插入
    inst = NegativeFeedback()
    inst.userid = 1L
    inst.positionid = 10L
    inst.commit_time = datetime.today()
    dao.insert(inst)

    # 批量插入
    entities = []
    for i in xrange(100):
        inst = NegativeFeedback()
        inst.userid = long(i + 4)
        inst.positionid = long(10 * i)
        inst.commit_time = datetime.today()
        inst.unlike_reason = u'任性'.encode('utf-8')
        entities.append(inst)
    dao.batchInsert(entities)

    # min--max
    a, b = dao.getMaxAndMin()
    print a, b

    # count
    cnt = dao.count("userid<100")
    print cnt

    # 单条查询
    inst = dao.getById(2516L)
    if inst:
        print inst

    # 批量查询
    entities = dao.getListByPage(None, 10, 5)
    for entity in entities:
        print entity

    # 单条删除
    dao.deleteById(2516)

    # 批量删除
    dao.delete("userid<10")

    # 全部删除
    dao.deleteAll()
