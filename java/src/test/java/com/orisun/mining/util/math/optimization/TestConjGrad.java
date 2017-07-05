package com.orisun.mining.util.math.optimization;

import com.orisun.mining.util.exception.ArgumentException;
import com.orisun.mining.util.math.Vector;
import com.orisun.mining.util.math.function.Function;
import com.orisun.mining.util.math.function.RosenBrockFunction;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * 
 * @Author:zhangchaoyang
 * @Since:2014-7-9
 * @Version:
 */
public class TestConjGrad {

	@Test
	public void testGetBestX_PRP() throws ArgumentException {
		ConjGrad conjGrad = new ConjGrad();
		Function function = new RosenBrockFunction();
		Vector initX = new Vector(2);
		initX.set(0, 0);
		initX.set(1, 0);
		Vector min = conjGrad.getBestX(function, initX, ConjGrad.BetaWay.PRP);
		// 在(1,1)处取得全局极小值0
		Assert.assertTrue(Math.abs(min.get(0) - 1) < 1E-9);// 精度只到达到1E-9，没有BFGS高
		Assert.assertTrue(Math.abs(min.get(1) - 1) < 1E-9);
		Assert.assertTrue(Math.abs(function.getValue(min)) < 1E-10);
	}

	// @Test
	// public void testGetBestX_DaiYuan() throws ArgumentException {
	// ConjGrad conjGrad = new ConjGrad();
	// conjGrad.setEps(1E-4);
	// Function function = new RosenBrockFunction();
	// Vector initX = new Vector(2);
	// initX.set(0, 0);
	// initX.set(1, 0);
	// Vector min = conjGrad.getBestX(function, initX,ConjGrad.BetaWay.DaiYuan);
	// // 在(1,1)处取得全局极小值0
	// Assert.assertTrue(Math.abs(min.get(0) - 1) < 1E-9);
	// Assert.assertTrue(Math.abs(min.get(1) - 1) < 1E-9);
	// Assert.assertTrue(Math.abs(function.getValue(min)) < 1E-10);
	// }

	@Test
	public void testGetBestX_Fletcher() throws ArgumentException {
		ConjGrad conjGrad = new ConjGrad();
		Function function = new RosenBrockFunction();
		Vector initX = new Vector(2);
		initX.set(0, 0);
		initX.set(1, 0);
		Vector min = conjGrad.getBestX(function, initX,
				ConjGrad.BetaWay.Fletcher);
		// 在(1,1)处取得全局极小值0
		Assert.assertTrue(Math.abs(min.get(0) - 1) < 1E-7);// 精度只到达到1E-9，没有BFGS高
		Assert.assertTrue(Math.abs(min.get(1) - 1) < 1E-7);
		Assert.assertTrue(Math.abs(function.getValue(min)) < 1E-10);
	}

	@Test
	public void testGetBestX_FletcherReeves() throws ArgumentException {
		ConjGrad conjGrad = new ConjGrad();
		Function function = new RosenBrockFunction();
		Vector initX = new Vector(2);
		initX.set(0, 0);
		initX.set(1, 0);
		Vector min = conjGrad.getBestX(function, initX,
				ConjGrad.BetaWay.FletcherReeves);
		// 在(1,1)处取得全局极小值0
		Assert.assertTrue(Math.abs(min.get(0) - 1) < 1E-7);// 精度只到达到1E-7，没有BFGS高
		Assert.assertTrue(Math.abs(min.get(1) - 1) < 1E-7);
		Assert.assertTrue(Math.abs(function.getValue(min)) < 1E-10);
	}

	@Test
	public void testGetBestX_HestenseStiefel() throws ArgumentException {
		ConjGrad conjGrad = new ConjGrad();
		Function function = new RosenBrockFunction();
		Vector initX = new Vector(2);
		initX.set(0, 0);
		initX.set(1, 0);
		Vector min = conjGrad.getBestX(function, initX,
				ConjGrad.BetaWay.HestenseStiefel);
		// 在(1,1)处取得全局极小值0
		Assert.assertTrue(Math.abs(min.get(0) - 1) < 1E-9);// 精度只到达到1E-9，没有BFGS高
		Assert.assertTrue(Math.abs(min.get(1) - 1) < 1E-9);
		Assert.assertTrue(Math.abs(function.getValue(min)) < 1E-10);
	}

	@Test
	public void testGetBestX_LiuStorey() throws ArgumentException {
		ConjGrad conjGrad = new ConjGrad();
		Function function = new RosenBrockFunction();
		Vector initX = new Vector(2);
		initX.set(0, 0);
		initX.set(1, 0);
		Vector min = conjGrad.getBestX(function, initX,
				ConjGrad.BetaWay.LiuStorey);
		// 在(1,1)处取得全局极小值0
		Assert.assertTrue(Math.abs(min.get(0) - 1) < 1E-7);// 精度只到达到1E-7，没有BFGS高
		Assert.assertTrue(Math.abs(min.get(1) - 1) < 1E-7);
		Assert.assertTrue(Math.abs(function.getValue(min)) < 1E-10);
	}
}
