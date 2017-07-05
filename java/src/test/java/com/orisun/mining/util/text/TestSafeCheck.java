package com.orisun.mining.util.text;

import org.junit.Assert;
import org.junit.Test;

public class TestSafeCheck {

	@Test
	public void testSafe() {
		String str = "<<<<<<<<<jhklhk";
		Assert.assertTrue(SafeCheck.safeArg(str));
		str = "<<<<<<<<<jhk>lhk";
		Assert.assertFalse(SafeCheck.safeArg(str));
	}

	@Test
	public void testJsonpSafe() {
		String str = "54tg54y54DFEFE_ff.C";
		Assert.assertTrue(SafeCheck.safeCallBack(str));
		str = "54tg54y54DFEFE_ff.C++";
		Assert.assertFalse(SafeCheck.safeCallBack(str));
	}

}
