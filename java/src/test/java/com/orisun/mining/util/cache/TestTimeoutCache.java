package com.orisun.mining.util.cache;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TestTimeoutCache {

	@Test
	public void testTimeout() throws InterruptedException, IOException {
		TimeoutCache<Integer, String> cache = new TimeoutCache<Integer, String>();
		cache.put(1, "aaaa", 3, TimeUnit.SECONDS);
		cache.put(2, "bbbb", 6, TimeUnit.SECONDS);

		// 1尚未过期，还在缓存中
		Thread.sleep(1000 * 2);
		String value = cache.get(1);
		Assert.assertEquals("aaaa", value);
		value = "OOPS!";
		Assert.assertEquals("aaaa", cache.get(1));
		Assert.assertEquals("bbbb", cache.get(2));

		// 1已过期，从缓存中取出来的是null。2尚未过期，还在缓存中
		Thread.sleep(1000 * 2);
		Assert.assertNull(cache.get(1));
		Assert.assertEquals("bbbb", cache.get(2));

		// 2的寿命重置为6秒
		cache.put(2, "cccc", 6, TimeUnit.SECONDS);
		Thread.sleep(1000 * 2);
		Assert.assertNull(cache.get(1));
		Assert.assertEquals("cccc", cache.get(2));
	}

	@Test
	public void testSafe() throws IOException {
		TimeoutCache<Integer, List<Integer>> cache = new TimeoutCache<Integer, List<Integer>>();
		List<Integer> list = new ArrayList<Integer>();
		list.add(51);
		list.add(58);
		list.add(360);
		cache.put(1, list, 3, TimeUnit.SECONDS);

		List<Integer> out = cache.get(1);
		// 从缓存中把value取出来之后，再修改value，不会改变TimeoutCache中的值
		out.set(0, 91);
		System.out.println(out);
		System.out.println(cache.get(1));
	}

	@Test
	public void concurrentTest() throws InterruptedException {
		long begin = System.currentTimeMillis();
		final int threadNum = 100;
		final int key = 1;
		final int originalValue = 0;
		final TimeoutCache<Integer, Integer> cache = new TimeoutCache<Integer, Integer>();
		cache.put(key, originalValue, 5, TimeUnit.HOURS);
		Thread[] threads = new Thread[threadNum];
		for (int i = 0; i < threadNum; i++) {
			Thread thread = new Thread() {
				@Override
				public void run() {
					for (int j = 0; j < 1000; j++) {
						int value = cache.get(key);
						value++;
						cache.put(key, value, 5, TimeUnit.HOURS);
						value = cache.get(key);
						value--;
						cache.put(key, value, 5, TimeUnit.HOURS);
					}
				}
			};
			threads[i] = thread;
		}
		for (int i = 0; i < threadNum; i++) {
			threads[i].start();
		}
		for (int i = 0; i < threadNum; i++) {
			threads[i].join();
		}
		System.out.println(cache.get(key));
		long end = System.currentTimeMillis();
		System.out.println("Time elapsed " + (end - begin) / 1000 + " seconds");
		Assert.assertTrue(originalValue == cache.get(key));
	}
}
