package com.orisun.mining.util.similarity;

public class EuclidDist implements SimAlgorithm {

	/**
	 * 欧氏距离
	 */
	@Override
	public double getSim(double[] arr1, double[] arr2, int len) {
		assert arr1.length == len;
		assert arr2.length == len;
		double dist = 0.0;
		for (int i = 0; i < len; i++) {
			dist += (arr1[i] - arr2[i]) * (arr1[i] - arr2[i]);
		}
		dist = Math.sqrt(dist + Double.MIN_VALUE);
		return 1.0 / (dist + 1E-8);
	}

}
