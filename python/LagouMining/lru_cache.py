# coding:utf-8
__author__='orisun'

class Node(object):
    __slots__ = ['prev', 'next', 'me']

    def __init__(self, prev, me):
        self.prev = prev
        self.me = me
        self.next = None


class LruListCache:
    '''注意：非线程安全
    '''
    def __init__(self, count):
        self.count = count
        self.l = []
        self.lru = {}
        self.tm = 0

    def extend(self, t_list):
        for i in t_list:
            self.lru[item] = self.tm
            self.tm += 1
            self.append(i)

    def appendd(self, item):
        if len(self.l) >= self.count:
            old_key = min(self.lru.keys(), key=lambda k: self.lru[k])
            self.l.remove(old_key)
            self.lru.pop(old_key)
        self.l.append(item)
        self.lru[item] = self.tm
        self.tm += 1

    def pop(self):
        old_key = min(self.lru.keys(), key=lambda k: self.lru[k])
        self.l.remove(old_key)
        return old_key

    def __getitem__(self, item):
        data = self.l[item]
        self.lru[item] = self.tm
        self.tm += 1
        return data


class LruDictCache:
    '''注意：非线程安全
    '''
    def __init__(self, count, pairs=[]):
        self.count = max(count, 1)
        self.d = {}
        self.first = None
        self.last = None
        for key, value in pairs:
            self[key] = value

    def __contains__(self, obj):
        return obj in self.d

    def __getitem__(self, obj):
        a = self.d[obj].me
        self[a[0]] = a[1]
        return a[1]

    def __setitem__(self, obj, val):
        if obj in self.d:
            del self[obj]
        nobj = Node(self.last, (obj, val))
        if self.first is None:
            self.first = nobj
        if self.last:
            self.last.next = nobj
        self.last = nobj
        self.d[obj] = nobj
        if len(self.d) > self.count:
            if self.first == self.last:
                self.first = None
                self.last = None
                return
            a = self.first
            a.next.prev = None
            self.first = a.next
            a.next = None
            del self.d[a.me[0]]
            del a

    def __delitem__(self, obj):
        nobj = self.d[obj]
        if nobj.prev:
            nobj.prev.next = nobj.next
        else:
            self.first = nobj.next
        if nobj.next:
            nobj.next.prev = nobj.prev
        else:
            self.last = nobj.prev
        del self.d[obj]

    def __iter__(self):
        cur = self.first
        while cur != None:
            cur2 = cur.next
            yield cur.me[1]
            cur = cur2

    def iteritems(self):
        cur = self.first
        while cur != None:
            cur2 = cur.next
            yield cur.me
            cur = cur2

    def iterkeys(self):
        return iter(self.d)

    def itervalues(self):
        for i, j in self.iteritems():
            yield j

    def keys(self):
        return self.d.keys()


if __name__ == '__main__':
    queue = LruDictCache(3)
    queue["a"] = 654
    queue["b"] = 43
    queue["c"] = 978697
    queue[('a','b')] = 'hahaha'

    for ele in queue.iteritems():
        print ele

    print queue["a"]
    queue["d"] = 34534
    for ele in queue.iteritems():
        print ele

    print "a" in queue.keys()
    print "a" in queue
    print "b" not in queue.keys()
