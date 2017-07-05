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
public class TestBFGS {

	@Test
	public void testGetBestX() throws ArgumentException {
		MinOptimization minOpt = new BFGS();
		Function function = new RosenBrockFunction();
		Vector initX = new Vector(2);
		initX.set(0, 0);
		initX.set(1, 0);
		Vector min = minOpt.getBestX(function, initX);
		// 在(1,1)处取得全局极小值0
		Assert.assertTrue(Math.abs(min.get(0) - 1) < 1E-11);
		Assert.assertTrue(Math.abs(min.get(1) - 1) < 1E-11);
		Assert.assertTrue(Math.abs(function.getValue(min)) < 1E-10);
	}
}
