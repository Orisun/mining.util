package com.orisun.mining.util.math.optimization;

import com.orisun.mining.util.exception.ArgumentException;
import com.orisun.mining.util.math.Matrix;
import com.orisun.mining.util.math.Vector;
import com.orisun.mining.util.math.function.Function;

/**
 * BFGS一种拟牛顿(Quasi Newton Algorithm)优化算法。<br>
 * 本代码使用的各个变量的含义参见《非线性优化计算方法》袁亚湘著，第一版，第88页。
 *
 *@Author:zhangchaoyang 
 *@Since:2014-7-9  
 *@Version:
 */
public class BFGS extends MinOptimization {

	/**
	 * 求使用function取得极小值时的X
	 * 
	 * @param function
	 * @param initX
	 *            给定X的初始值
	 * @return
	 * @throws ArgumentException
	 */
	public Vector getBestX(Function function, Vector initX)
			throws ArgumentException {
		int dimention = function.getDimension();
		if (initX.getDimention() != dimention) {
			throw new ArgumentException("X0的维度度不对");
		}
		Vector X = initX;
		// 梯度向量g
		Vector g = function.getGradient(initX);
		// 二阶梯度矩阵H(即Hessen矩阵的逆)
		Matrix H = Matrix.identity(dimention);

//		int itr = 0;
		while (g.norm2() > eps) {
//			itr++;
			// 计算X的移动方向
			Vector d = H.dotProduct(g.toMatrix().transport()).multipleBy(-1)
					.getCol(0);
			// 计算X_1
			Vector X_1 = OptUtil.getNextX(function, X, d);
			// 计算g_1
			Vector g_1 = function.getGradient(X_1);
			// 使用BFGS修正公式计算H_1
			Matrix s = X_1.sub(X).toMatrix().transport();
			Matrix y = g_1.sub(g).toMatrix().transport();
			double ys = y.getCol(0).dotProduct(s.getCol(0));
			Matrix part1 = H.dotProduct(y).dotProduct(s.transport())
					.add(s.dotProduct(y.transport()).dotProduct(H))
					.multipleBy(1.0 / ys);
			double factor = 1
					+ y.transport().dotProduct(H).dotProduct(y).getArr()[0][0]
					/ ys;
			Matrix part2 = (s.dotProduct(s.transport()).multipleBy(1.0 / ys))
					.multipleBy(factor);
			Matrix H_1 = H.sub(part1).add(part2);

			// 更新X,g,H
			X = X_1;
			g = g_1;
			H = H_1;
		}
//		System.out.println("BFGS算法迭代了" + itr + "次");
		return X;
	}
}
