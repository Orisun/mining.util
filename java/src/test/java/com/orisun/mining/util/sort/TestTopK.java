package com.orisun.mining.util.sort;

import com.orisun.mining.util.sort.TopK.Tuple;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TestTopK {

	@Test
	public void testTopK() {
		Random rnd = new Random();
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (int i = 0; i < 100; i++) {
			int num = rnd.nextInt(100);
			System.out.println(num);
			map.put(i, num);
		}
		System.out.println("========");
		List<Tuple<Integer, Integer>> hotList = TopK.topK(map, 10);
		for (Tuple<Integer, Integer> tuple : hotList) {
			System.out.println(tuple.getValue());
		}
	}
}
