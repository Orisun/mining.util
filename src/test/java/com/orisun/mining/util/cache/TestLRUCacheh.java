//package com.orisun.mining.util.cache;
//
//import com.orisun.mining.util.Pair;
//import org.junit.Assert;
//import org.junit.Test;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class TestLRUCacheh {
//
//	@Test
//	public void test() {
//		LRUCache<String, String> map = new LRUCache<String, String>(3);
//		String v = map.get("A");// A最先插入
//		if (v == null) {
//			map.put("A", "a");
//		}
//		v = map.get("B");
//		if (v == null) {
//			map.put("B", "b");
//		}
//		v = map.get("C");
//		if (v == null) {
//			map.put("C", "c");
//		}
//		map.get("A");// 访问一次A，这样B成为最老的元素
//		v = map.get("D");
//		if (v == null) {
//			map.put("D", "d");// 插入D，B被移除
//		}
//		v = map.get("B");
//		Assert.assertNull(v);
//		v = map.get("A");
//		Assert.assertEquals("a", v);
//		map.clear();
//		Assert.assertTrue(map.isEmpty());
//		v = map.get("B");
//		Assert.assertNull(v);
//		map.put("A", "a");
//		v = map.get("A");
//		Assert.assertEquals("a", v);
//
//		LRUCache<String, List<String>> cache = new LRUCache<String, List<String>>(3);
//		List<String> list = new ArrayList<String>();
//		list.add("A");
//		list.add("B");
//		list.add("C");
//		cache.put("key", list);
//		List<String> list2 = cache.get("key");
//		Assert.assertEquals(list2.get(0), "A");
//		Assert.assertEquals(list2.get(1), "B");
//		Assert.assertEquals(list2.get(2), "C");
//		list2.set(1, "Q");
//		List<String> list3 = cache.get("key");
//		Assert.assertEquals(list3.get(1), "B");
//
//		LRUCache<String, List<Pair<Integer, String>>> cache2 = new LRUCache<String, List<Pair<Integer, String>>>(3);
//		List<Pair<Integer, String>> list8 = new ArrayList<Pair<Integer, String>>();
//		list8.add(Pair.of(9, "342534"));
//		cache2.put("key", list8);
//		System.out.println(cache2.get("key"));
//	}
//}
