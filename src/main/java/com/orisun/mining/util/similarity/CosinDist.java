package com.orisun.mining.util.similarity;

public class CosinDist implements SimAlgorithm {

	/**
	 * 余弦距离,余弦值和相似度成正比
	 */
	@Override
	public double getSim(double[] arr1, double[] arr2, int len) {
		assert arr1.length == len;
		assert arr2.length == len;
		double cos = 0.0;
		double dotProduct = 0.0;
		double modulus1 = 0.0;
		double modulus2 = 0.0;
		for (int i = 0; i < len; i++) {
			dotProduct += arr1[i] * arr2[i];
			modulus1 += arr1[i] * arr1[i];
			modulus2 += arr2[i] * arr2[i];
		}
		if (dotProduct == 0) {
			return 0;
		}
		cos = dotProduct / Math.sqrt(modulus1) * Math.sqrt(modulus2);
		return cos;
	}

}
