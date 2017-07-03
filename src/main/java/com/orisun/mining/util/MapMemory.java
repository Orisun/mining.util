package com.orisun.mining.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 * 验证Java中的Map底层的实现是HashTable，因为Map占用的内存比同等规模的List占更多大内存
 * 
 * @Author:orisun
 * @Since:2015-12-8
 * @Version:1.0
 */
public class MapMemory {

	public static void test(Map<Integer, Integer> map) {
		System.gc();
		long total1 = Runtime.getRuntime().totalMemory(); // byte
		long m1 = Runtime.getRuntime().freeMemory();
		System.out.println("before:" + (total1 - m1));

		Random rnd = new Random();
		int loop = 10000;
		for (int i = 0; i < loop; i++) {
			map.put(rnd.nextInt(), rnd.nextInt());
		}
		long total2 = Runtime.getRuntime().totalMemory();
		long m2 = Runtime.getRuntime().freeMemory();
		System.out.println("after:" + (total2 - m2));
		System.out.println("real use:" + (m1 - m2));
		System.out.println("real use:" + ((total2 - m2) - (total1 - m1)));
		System.out.println("need use:" + loop * 2 * 8);
	}

	public static void main(String[] args) {
		System.out.println("========HashMap========");
		HashMap<Integer, Integer> hashmap = new HashMap<Integer, Integer>();
		test(hashmap);
		System.out.println("========TreeMap========");
		TreeMap<Integer, Integer> treemap = new TreeMap<Integer, Integer>();
		test(treemap);
	}
}
