package com.orisun.mining.util.math.function;

import com.orisun.mining.util.math.Vector;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestRosenBrockFunction {

	private static Vector X;

	@BeforeClass
	public static void setup() {
		double[] arr = new double[] { 1, 1 };
		X = new Vector(arr);
	}

	@Test
	public void testGetValue() {
		RosenBrockFunction function = new RosenBrockFunction();
		double y = function.getValue(X);
		Assert.assertTrue(y == 0);
	}

	@Test
	public void testGetGradient() {
		RosenBrockFunction function = new RosenBrockFunction();
		Vector g = function.getGradient(X);
		Assert.assertTrue(g.get(0) == 0);
		Assert.assertTrue(g.get(1) == 0);
	}
}

