package com.orisun.mining.util.math;

import org.junit.Test;

public class TestWilson {

	@Test
	public void test() {
		double value = 0.9;
		for (int n = 1; n < 100; n += 10) {
			for (double confidence = 0.5; confidence < 1.0; confidence += 0.03) {
				System.out.println("value=" + value + ", n=" + n + ", confidence=" + confidence + ", wilson floor is "
						+ Wilson.wilsonFloor(value, n, confidence));
			}
		}
	}
}
