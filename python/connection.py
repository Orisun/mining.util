# coding:utf-8
__author__='orisun'

import threading
import time
from collections import deque
import datetime
import MySQLdb
import ConfigParser
import traceback
import MySQLdb

from concurrent import ReadWriteLock
from logger import LoggerFactory
logger = LoggerFactory.getLogger()


class ConnectionPoolFactory(object):
    '''管理所有的数据库连接池。
       引入缓存机制，同一个数据库的连接池只会创建一个
    '''
    config = ConfigParser.ConfigParser()
    _cache = {}
    _lock = threading.Lock()

    @classmethod
    def setConfFile(cls, conf_file):
        cls.config.read(conf_file)

    @classmethod
    def getConnectionPool(cls, dbname):
        '''获取一个数据库的连接池。单例模式
        '''
        if not dbname in cls._cache:
            with cls._lock:
                if not dbname in cls._cache:
                    n_failed = 0
                    except_msg = ""
                    # 连接数据库给一次失败的机会
                    while n_failed < 2:
                        try:
                            host = cls.config.get(dbname, 'host')
                            port = cls.config.getint(dbname, 'port')
                            user = cls.config.get(dbname, 'username')
                            passwd = cls.config.get(dbname, 'password')
                            maxconn = cls.config.getint(dbname, 'maxconn')
                            pool = ConnectionPool(host, port, dbname,
                                                  user, passwd, maxconn)
                            cls._cache[dbname] = pool
                        except Exception as e:
                            n_failed += 1
                            except_msg = traceback.format_exc()
                        else:
                            except_msg = ""
                            break
                    if len(except_msg) > 0:
                        raise Exception(
                            'config for db {} failed {}'.format(dbname, except_msg))
        return cls._cache[dbname]


class Connection(object):

    def __init__(self, conn, pool):
        self.conn = conn
        self.pool = pool

    def cursor(self):
        cursor = self.conn.cursor(MySQLdb.cursors.DictCursor)
        return cursor

    def close(self):
        '''close方法并非真的关闭连接，只是将连接归还到连接池中
        '''
        self.pool.returnConnection(self)


class ConnectionPool(object):

    def __init__(self, host, port, dbname, user, passwd, maxconn, initial_conn=5, increment_conn=5):
        self.host = host
        self.port = port
        self.dbname = dbname
        self.user = user
        self.passwd = passwd
        self.maxconn = maxconn  # 最大连接数
        self.initial_conn = initial_conn  # 初始创建多少连接
        self.increment_conn = increment_conn  # 每次发现连接不够用时新建多少连接
        '''deque是线程安全的,用于存放所有创建的DB连接。
           采用后进先出策略的好处是：最后放入池中的连接很大概率上讲应该是可用的。
           采用先进先出策略的好处是：每个连接都均匀地被使用，避免某些连接因长时间空闲而被MySQL Server强制断开。
        '''
        self.connections = deque()
        self.__createNewConnections(initial_conn)
        logger.info('create connection pool for DB {} success'.format(dbname))

    def __createNewConnections(self, num):
        '''新建num个连接，放到容器中，并返回创建的第1个连接
        '''
        firstConn = None
        for x in xrange(num):
            if self.maxconn > 0 and len(self.connections) >= self.maxconn:
                logger.error(
                    'DB connection have reached ceil:{}, can not create more'.format(self.maxconn))
                break
            try:
                connection = self.__createConnection()
                self.connections.append(connection)
                if not firstConn and connection:
                    firstConn = connection
            except MySQLdb.Error as e:
                logger.error('create DB connection for {} failed'.format(
                    self.dbname), e)
                raise e
        logger.info('have create {} DB connections for {}'.format(
            len(self.connections), self.dbname))
        return firstConn

    def __createConnection(self):
        '''新建1个连接
        '''
        conn = MySQLdb.connect(
            host=self.host, port=self.port, db=self.dbname, user=self.user, passwd=self.passwd, charset="utf8", use_unicode=False)
        # 使用MySQLdb时必须打开“自动提交”，否则会对查询结果进行缓冲，即修改一条记录后再查询获取到的可能还是修改之前的值
        conn.autocommit(1)
        return Connection(conn, self)

    def returnConnection(self, connection):
        '''连接用完后归还到池中
        '''
        self.connections.append(connection)

    def getConnection(self):
        '''从容器中获取一个空闲连接
        '''
        while len(self.connections) > 0:
            connection = self.connections.popleft()
            # 先ping一下，确保连接可用
            try:
                connection.conn.ping()
            except MySQLdb.Error as e:
                # 连接不可用，从deque中取下一个
                continue
            else:
                # 连接可用，直接返回
                return connection
        # 如果没有空闲连接，则新建一批连接。新建连接可能会失败(很可能是连接池已满导致的)，允许重试3次
        tryTimes = 0
        while tryTimes < 3:
            newConnection = self.__createNewConnections(self.increment_conn)
            if newConnection:
                return newConnection
            tryTimes += 1
            # 休息一下，等待连接池空闲
            time.sleep(0.2)
        logger.error("can not get connection from pool")
        return None


if __name__ == '__main__':
    DB_REC = "rec"
    DB_MY = "mytable"
    ConnectionPoolFactory.setConfFile("../../conf/db.conf.offline")
    conn3 = ConnectionPoolFactory.getConnectionPool(
        DB_REC).getConnection()
    conn4 = ConnectionPoolFactory.getConnectionPool(DB_MY).getConnection()
    conn1 = ConnectionPoolFactory.getConnectionPool(
        DB_REC).getConnection()
    conn2 = ConnectionPoolFactory.getConnectionPool(DB_MY).getConnection()
    cursor1 = conn1.cursor()
    cursor1.execute('select * from negative_feedback limit 1')
    data = cursor1.fetchone()
    for k, v in data.items():
        print '{}={}'.format(k, v)
    cursor2 = conn2.cursor()
    cursor2.execute('select positionname from position limit 1')
    print cursor2.fetchone()
    conn1.close()
    conn2.close()

    ConnectionPoolFactory.getConnectionPool(DB_REC).refresh()
    ConnectionPoolFactory.getConnectionPool(DB_MY).refresh()

    cursor3 = conn3.cursor()
    cursor3.execute('select * from negative_feedback limit 1')
    data = cursor3.fetchone()
    for k, v in data.items():
        print '{}={}'.format(k, v)
    cursor4 = conn4.cursor()
    cursor4.execute('select positionname from position limit 1')
    print cursor4.fetchone()
    conn3.close()
    conn4.close()
