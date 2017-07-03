package com.orisun.mining.util.math.function;

import com.orisun.mining.util.math.Vector;


/**
 * 
 * @Title: 二维RosenBrockFunction
 * @Description: 
 *               在数学优化中,Rosenbrock函数是一个用来测试优化算法性能的非凸函数.二维RosenBrock函数在(1,1)处取得全局最小值
 * @Author:zhangchaoyang
 * @Since:2014-7-4
 * @Version:1.1.0
 */
public class RosenBrockFunction extends Function {

	public RosenBrockFunction() {
		this.setDimension(2);
	}

	/**
	 * 二维的RosenBrock函数表达式：f(x,y)=(1-x)^2+100*(y-x^2)^2
	 */
	@Override
	public double getValue(Vector X) {
		return 100
				* Math.pow(X.getArray()[1] - Math.pow(X.getArray()[0], 2.0),
						2.0) + Math.pow(1 - X.getArray()[0], 2.0);
	}

	@Override
	public Vector getGradient(Vector X) {
		double[] derivative = new double[2];
		derivative[0] = 0 - 400
				* (X.getArray()[1] - Math.pow(X.getArray()[0], 2))
				* X.getArray()[0] - 2 * (1 - X.getArray()[0]);
		derivative[1] = 200 * (X.getArray()[1] - Math.pow(X.getArray()[0], 2));
		return new Vector(derivative);
	}

}
