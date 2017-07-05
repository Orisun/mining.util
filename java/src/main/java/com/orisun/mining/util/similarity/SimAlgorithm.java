package com.orisun.mining.util.similarity;

public interface SimAlgorithm {

	/**
	 * 计算距离
	 * @param arr1
	 * @param arr2
	 * @param len
	 * @return
	 */
	public abstract double getSim(double[] arr1, double[] arr2, int len);
}
