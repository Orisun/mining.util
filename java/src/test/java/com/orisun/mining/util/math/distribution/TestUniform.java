package com.orisun.mining.util.math.distribution;

import com.orisun.mining.util.Pair;
import com.orisun.mining.util.exception.DmArithmeticException;
import com.orisun.mining.util.math.Vector;
import org.junit.Assert;
import org.junit.Test;

public class TestUniform {

	@Test
	public void testExpection() {
		double ceil = 5;
		double floor = ceil - 10;
		Uniform uniform = new Uniform(floor, ceil);
		double mean = uniform.getExpection();
		Assert.assertTrue(Math.abs(mean) < 1E-5);
	}

	@Test
	public void testVariance() {
		double ceil = 5;
		double floor = ceil - 10;
		Uniform uniform = new Uniform(floor, ceil);
		double variance = uniform.getVariance();
		Assert.assertTrue(Math.abs(variance - 8.33333) < 1E-5);
	}

	@Test
	public void testDrawOnePoint() throws DmArithmeticException {
		double ceil = 5;
		double floor = ceil - 10;
		Uniform uniform = new Uniform(floor, ceil);
		double expection = uniform.getExpection();// 分布的期望
		double variance = uniform.getVariance();// 分布的方差
		double theta = Math.sqrt(variance);// 分布的标准差

		int len = 100;// 样本容量取大一些
		double[] arr = new double[len];
		for (int i = 0; i < len; i++) {
			arr[i] = uniform.drawOnePoint();
			if (arr[i] >= ceil || arr[i] < floor) {
				Assert.assertTrue(false);
			}
		}
		Vector vec = new Vector(arr);
		Pair<Double, Double> mv = vec.meanAndVariance();
		double mean = mv.first;// 样本均值
		double var = mv.second;// 样本方差
		Assert.assertTrue(Math.abs(mean - expection) < theta);
		Assert.assertTrue(Math.abs(var - variance) < theta);
	}

}
