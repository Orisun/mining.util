package com.orisun.mining.util.math.optimization;

import com.orisun.mining.util.exception.ArgumentException;
import com.orisun.mining.util.math.Vector;
import com.orisun.mining.util.math.function.Function;

/**
 * 求极小值的最优化问题
 * 
 * @Author:zhangchaoyang
 * @Since:2014-7-9
 * @Version:
 */
public abstract class MinOptimization {

	protected double eps = 1E-8; // 梯度的2范数小于该值时迭代收敛

	public abstract Vector getBestX(Function function, Vector initX)
			throws ArgumentException;

	public double getEps() {
		return eps;
	}

	public void setEps(double eps) {
		this.eps = eps;
	}

}
