package com.orisun.mining.util;

import org.junit.Assert;
import org.junit.Test;

public class TestTriple {

	@Test
	public void testHashCode() {
		Triple<Integer, Long, Integer> t1 = Triple.of(1, 2L, 9);
		Triple<Integer, Long, Integer> t2 = Triple.of(1, 2L, 9);
		Assert.assertEquals(t1.hashCode(), t2.hashCode());
		Assert.assertEquals(t1, t2);

		/**
		 * Triple中如果包含数组，则即使内容相同hashCode也不相同
		 */
		byte[] arr = new byte[] { 1, 5, 91 };
		byte[] brr = new byte[] { 1, 5, 91 };
		Triple<Integer, Long, byte[]> t3 = Triple.of(1, 2L, arr);
		Triple<Integer, Long, byte[]> t4 = Triple.of(1, 2L, brr);
		Assert.assertNotEquals(t3.hashCode(), t4.hashCode());
		Assert.assertNotEquals(t3, t4);
	}
}
