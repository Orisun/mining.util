package com.orisun.mining.util.math;  
  
import org.junit.Test;

public class TestSmooth {

	@Test
	public void testLaplasse() {
		int[] arr = new int[] { 3, 0, 2 };
		double[] brr = Smooth.Laplasse(arr, 1);
		for (double ele : brr) {
			System.out.println(ele);
		}
		System.out.println();
	}

	@Test
	public void testGoodTuring() {
		int[] arr = new int[] { 2, 1, 0, 1, 2, 2, 2 };
		double[] brr = Smooth.GoodTuring(arr);
		for (double ele : brr) {
			System.out.println(ele);
		}
		System.out.println();
	}
}
