package com.orisun.mining.util.similarity;

public class ManhattanDist implements SimAlgorithm {

	/**
	 * 曼哈顿距离
	 */
	public double getSim(double[] arr1, double[] arr2, int len) {
		assert arr1.length == len;
		assert arr2.length == len;
		double dist = 0.0;
		for (int i = 0; i < len; i++) {
			dist += Math.abs(arr1[i] - arr2[i]);
		}
		return 1.0/(dist+1E-8);
	}

}
