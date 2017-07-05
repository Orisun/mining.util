package com.orisun.mining.util.math.optimization;

import com.orisun.mining.util.exception.ArgumentException;
import com.orisun.mining.util.math.Vector;
import com.orisun.mining.util.math.function.Function;


/**
 * 求解最优化问题中，常用到的一些utility
 *
 *@Author:zhangchaoyang 
 *@Since:2014-7-9  
 *@Version:
 */
public class OptUtil {

	public static Vector getNextX(Function function, Vector X, Vector d)
			throws ArgumentException {
		int dimention = function.getDimension();
		double[] X_1 = new double[dimention];
		double stepSize = StrongWofle.getStepsize(function, X, d);
		for (int i = 0; i < dimention; i++) {
			X_1[i] = X.getArray()[i] + stepSize * d.getArray()[i];
		}
		return new Vector(X_1);
	}

}
