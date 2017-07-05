package com.orisun.mining.util.math.matrix;

import org.junit.Assert;
import org.junit.Test;

public class TestSymMatrix {

	public static final int N = 10000;

	@Test
	public void test() {
		SymMatrix<Double> matrix = new SymMatrix<Double>(Double.class, N);
		matrix.set(3, 4, 5.0);
		Assert.assertTrue(matrix.get(4, 3) == 5.0);
		Assert.assertNull(matrix.get(19, 7));
		
		SymMatrix<Integer> matrix2 = new SymMatrix<Integer>(Integer.class, N);
		matrix2.set(3, 4, 5);
		Assert.assertTrue(matrix2.get(4, 3) == 5);
		
		SymMatrix<Float> matrix3 = new SymMatrix<Float>(Float.class, N);
		matrix3.set(3, 4, 5f);
		Assert.assertTrue(matrix3.get(4, 3) == 5f);
	}
}
