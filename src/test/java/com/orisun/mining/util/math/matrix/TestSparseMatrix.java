package com.orisun.mining.util.math.matrix;

import org.junit.Assert;
import org.junit.Test;
  
public class TestSparseMatrix {
public static final int N = 10000;
	
	@Test
	public void test() {
		SparseMatrix<Integer> matrix1=new SparseMatrix<Integer>();
		matrix1.set(6,19,5);
		matrix1.set(19,6,10);
		Assert.assertTrue(matrix1.get(6, 19) == 5);
		Assert.assertTrue(matrix1.get(19, 6) == 10);
		Assert.assertNull(matrix1.get(19, 7));
		Assert.assertTrue(matrix1.getDimension() == 2);
		
		SparseMatrix<Double> matrix2=new SparseMatrix<Double>();
		matrix2.set(6,19,5.0);
		matrix2.set(19,6,10.0);
		Assert.assertTrue(matrix2.get(6, 19) == 5.0);
		Assert.assertTrue(matrix2.get(19, 6) == 10.0);
		Assert.assertNull(matrix2.get(19, 7));
		Assert.assertTrue(matrix2.getDimension() == 2);
	}
}
