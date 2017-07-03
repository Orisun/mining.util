package com.orisun.mining.util.math.matrix;

import org.junit.Assert;
import org.junit.Test;
  
public class TestSymSparseMatrix {
	public static final int N = 10000;
	
	@Test
	public void test() {
		SparseSymMatrix<Integer> matrix1=new SparseSymMatrix<Integer>();
		matrix1.set(6,19,5);
		Assert.assertTrue(matrix1.get(6, 19) == 5);
		Assert.assertTrue(matrix1.get(19, 6) == 5);
		Assert.assertNull(matrix1.get(19, 7));
		Assert.assertTrue(matrix1.getDimension() == 1);
		
		SparseSymMatrix<Double> matrix2=new SparseSymMatrix<Double>();
		matrix2.set(6,19,5.0);
		Assert.assertTrue(matrix2.get(6, 19) == 5.0);
		Assert.assertTrue(matrix2.get(19, 6) == 5.0);
		Assert.assertNull(matrix2.get(19, 7));
		Assert.assertTrue(matrix2.getDimension() == 1);
	}
}
