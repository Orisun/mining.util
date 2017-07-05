package com.orisun.mining.util.correlation;

import com.orisun.mining.util.exception.ArgumentException;

public class Pearson {

	public static double corrcoef(double[] arr, double[] brr)
			throws ArgumentException {
		int len = arr.length;
		if (len != brr.length) {
			throw new ArgumentException("length of two array not equal:"
					+ arr.length + "," + brr.length);
		}
		double coef = 0.0;
		double product_sum = 0.0;
		double arr_sum = 0.0;
		double brr_sum = 0.0;
		double arr_square_sum = 0.0;
		double brr_square_sum = 0.0;
		for (int i = 0; i < len; i++) {
			arr_sum += arr[i];
			brr_sum += brr[i];
			arr_square_sum += Math.pow(arr[i], 2);
			brr_square_sum += Math.pow(brr[i], 2);
			product_sum += arr[i] * brr[i];
		}
		double mean_product = product_sum / len;
		double mean_arr = arr_sum / len;
		double mean_brr = brr_sum / len;
		double mean_arr_square = arr_square_sum / len;
		double mean_brr_square = brr_square_sum / len;
		coef = (mean_product - mean_arr * mean_brr)
				/ (Math.sqrt(mean_arr_square - Math.pow(mean_arr, 2)) * Math
						.sqrt(mean_brr_square - Math.pow(mean_brr, 2)));
		return coef;
	}
}
