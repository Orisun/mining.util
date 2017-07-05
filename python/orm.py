# coding:utf-8
__author__ = 'orisun'

import re
from datetime import datetime

_type_pat = re.compile(r'\<.*\>')


class Typed(object):
    _expcet_type = type(None)
    _default_value = 0

    def __init__(self):
        self._value = self._default_value


class Integer(Typed):
    _expcet_type = int
    _default_value = 0


class Long(Typed):
    _expcet_type = long
    _default_value = 0L


class Float(Typed):
    _expcet_type = float
    _default_value = 0.0


class String(Typed):
    _expcet_type = str
    _default_value = None


class Date(Typed):
    _expcet_type = datetime
    _default_value = None


class PoMeta(type):

    def __new__(cls, clsname, bases, clsdict):
        d = dict(clsdict)
        column_attr = dict()
        for name, value in clsdict.items():
            if isinstance(value, Typed):
                column_attr[name] = type(value)
        d['column_attr'] = column_attr
        return type.__new__(cls, clsname, bases, d)


class PO(object):

    __metaclass__ = PoMeta

    DB = ""  # 数据库名
    TABLE = ""  # 表名
    ID = ""  # 主键名，主键要求必须是数值类型
    PK_AUTO_CREATE = True  # 主键是否是自动生成的

    def __getattribute__(self, *args, **kwargs):
        rect = object.__getattribute__(self, *args, **kwargs)
        if isinstance(rect, Typed) and _type_pat.match(str(rect)):
            return rect._value
        else:
            return rect

    '''赋值时进行2项检查：
       1.不允许给对象动态添加属性
       2.给已知属性赋值时必须符合声明时的类型
    '''
    '''
    def __setattr__(self, name, value):
        if not name in self.column_attr:
            raise ValueError('{} have no attribute \'{}\''.format(
                self.__class__.__name__, name))
        expect_type = self.column_attr[name]._expcet_type
        if not isinstance(value, expect_type):
            raise TypeError('Expcet {}, but is {}'.format(
                expect_type, type(value)))
        else:
            self.__dict__[name] = value
    '''

    def __setattr__(self, name, value):
        '''赋值时进行2项检查：如果该属性是跟BD字段对应的，则必须符合既定的数据类型
        '''
        if name in self.column_attr and value is not None:
            if not isinstance(value, self.column_attr[name]._expcet_type):
                if self.column_attr[name]._expcet_type == long and type(value) == int:
                    value = long(value)
                elif self.column_attr[name]._expcet_type == float and (type(value) == int or type(value) == long):
                    value = float(value)
                elif self.column_attr[name]._expcet_type == int and type(value) == long and value < 2**31 - 1:
                    value = int(value)
                else:
                    raise TypeError('{} expcet {}, but is {}'.format(name,
                                                                     self.column_attr[name]._expcet_type, type(value)))
        self.__dict__[name] = value

    def __str__(self):
        return ', '.join(name + '=' + str(getattr(self, name))
                         for name in self.column_attr)

    def all_columns(self):
        return ','.join(map(str, self.column_attr.keys()))

    def all_columns_without_pk(self):
        return ','.join(map(str, [key for key in self.column_attr.keys() if key != self.ID]))


if __name__ == '__main__':
    class NegativeFeedback(PO):

        DB = "rec"
        TABLE = "negative_feedback"
        ID = "id"

        id = Long()
        userid = Long()
        positionid = Long()
        commit_time = Date()
        unlike_reason = String()

    inst = NegativeFeedback()
    print 'all_columns is: ', inst.all_columns()
    print 'all_columns_without_pk is: ', inst.all_columns_without_pk()
    print '初始化后的值'
    print ','.join(inst.column_attr.keys())
    print ','.join(list(vars(inst).keys()))
    print NegativeFeedback.DB
    print inst.DB
    print inst
    print

    print '赋值后的值'
    setattr(inst, 'userid', 5345234L)  # 等价于inst.userid = 5345234L
    inst.commit_time = datetime.today()
    inst.unlike_reason = 'renxing'
    print inst.ID
    print inst
    print

    print '给对象动态添加属性'
    import traceback
    try:
        inst.not_attr = 342
    except:
        assert False
    else:
        print '赋值成功'
    print

    print '赋值时类型有误'
    try:
        inst.id = 434
    except TypeError, e:
        print e.args[0]
    else:
        assert False
    print

    print '与数据库中的列对应的字段有'
    for attr, value in vars(inst).items():
        if attr not in inst.column_attr:
            print attr, '没有与之对应的数据库字段'
        else:
            print attr, type(value)

    print '验证字段类型'
    print isinstance(NegativeFeedback.userid, Long)
    print isinstance(NegativeFeedback.__dict__['userid'], Long)

    print '给Number属性赋None'
    inst.id = None
    print inst.id
