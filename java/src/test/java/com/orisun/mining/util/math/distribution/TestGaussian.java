package com.orisun.mining.util.math.distribution;

import com.orisun.mining.util.Pair;
import com.orisun.mining.util.exception.DmArithmeticException;
import com.orisun.mining.util.math.Vector;
import org.junit.Assert;
import org.junit.Test;

public class TestGaussian {

	@Test
	public void testDrawOnePoint() throws DmArithmeticException {
		double mu = 0;
		double sigma = 1;
		Gaussian guass = new Gaussian(mu, sigma);
		double expection = guass.getExpection();// 分布的期望
		double variance = guass.getVariance();// 分布的方差

		int len = 100000;// 样本容量取大一些
		double[] arr = new double[len];
		for (int i = 0; i < len; i++) {
			arr[i] = guass.drawOnePoint();
		}
		Vector vec = new Vector(arr);
		Pair<Double, Double> mv = vec.meanAndVariance();
		double mean = mv.first;// 样本均值
		double var = mv.second;// 样本方差
		System.out.println("mean=" + mean + ", variance=" + var);
		Assert.assertTrue(Math.abs(mean - expection) < 0.01);
		Assert.assertTrue(Math.abs(var - variance) < 0.01);
	}
}
