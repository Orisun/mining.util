package com.orisun.mining.util.zookeeper;

import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class TestRecStrategy {

	private int choose(long seed) {
		List<Double> ratioList = new CopyOnWriteArrayList<Double>();
		ratioList.add(0.5);
		ratioList.add(1.0);
		Map<Integer, Integer> strategyIndex = new ConcurrentHashMap<Integer, Integer>();
		strategyIndex.put(0, 0);
		strategyIndex.put(0, 1);
		Random rnd = new Random(seed);
		double num = rnd.nextDouble();
		int index = ratioList.size() - 1;
		for (; index >= 0; index--) {
			if (num > ratioList.get(index)) {
				break;
			}
		}
		int rect = index + 1;
		if (rect > ratioList.size() - 1) {
			rect = ratioList.size() - 1;
		}
		Integer si = strategyIndex.get(rect);
		if (si == null) {
			si = 0;
		}
		return si;
	}

	@Test
	public void testChoose() {
		int a = 0;
		int b = 0;
		for (int i = 0; i < 1000; i++) {
			Random rnd = new Random();
			long seed = rnd.nextLong();
			if (choose(seed) == 0) {
				a++;
			} else {
				b++;
			}
		}
		System.out.println("number of 0 is " + a + ", number of 1 is " + b);
	}

	List<Double> ratioList = new CopyOnWriteArrayList<Double>();
	Thread th1 = new Thread() {
		@Override
		public void run() {
			for (int i = 0; i < 4; i++) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				List<Double> tmpList = new CopyOnWriteArrayList<Double>();
				for (int j = 0; j < 2 * ratioList.size() + 1; j++) {
					tmpList.add(1.0);
				}
				ratioList = tmpList;
				System.out.println("set size " + ratioList.size());
			}
		}
	};
	Thread th2 = new Thread() {
		@Override
		public void run() {
			for (;;) {
				System.out.println("get size " + ratioList.size());
			}
		}
	};

	@Test
	public void testCopyOnWriteArrayList() throws InterruptedException {
		th2.start();
		th1.start();
		th1.join();
		System.exit(0);
	}
}
