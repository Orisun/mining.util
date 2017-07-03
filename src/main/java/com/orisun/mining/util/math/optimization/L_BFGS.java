package com.orisun.mining.util.math.optimization;

import com.orisun.mining.util.exception.ArgumentException;
import com.orisun.mining.util.math.Matrix;
import com.orisun.mining.util.math.Vector;
import com.orisun.mining.util.math.function.Function;

/**
 * 有限内存(Limited Memory)的BFGS算法。<br>
 * 本代码使用的各个变量的含义参见《非线性优化计算方法》袁亚湘著，第一版，第112页。
 *
 *@Author:zhangchaoyang 
 *@Since:2014-7-9  
 *@Version:
 */
public class L_BFGS extends MinOptimization{

	private int m = 4;// m一般取3到8之间的数，这取决于问题的维数、机器的内存

	/**
	 * 求使用function取得极小值时的X
	 * 
	 * @param function
	 * @param initX
	 *            给定X的初始值
	 * @return
	 * @throws ArgumentException
	 */
	public  Vector getBestX(Function function, Vector initX)
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

		Matrix[] s_arr = new Matrix[m + 1];
		Matrix[] V_arr = new Matrix[m + 1];
		double[] rou_arr = new double[m + 1];

		int itr = 0;
		while (g.norm2() > eps) {
			System.out.println(g.norm2());
			itr++;
			// 计算X的移动方向
			Vector d = g.toMatrix().dotProduct(H).multipleBy(-1).getRow(0);
			// 计算X_1
			Vector X_1 = OptUtil.getNextX(function, X, d);
			// 计算g_1
			Vector g_1 = function.getGradient(X_1);

			// 计算H_0
			Matrix H_0 = null;
			Matrix s = X_1.sub(X).toMatrix().transport();
			Matrix y = g_1.sub(g).toMatrix().transport();
			double ys = y.getCol(0).dotProduct(s.getCol(0));
			if (itr != 1) {
				H_0 = Matrix.identity(dimention).multipleBy(
						ys / Math.pow(y.norm2(), 2));
			} else {
				H_0 = H;
			}

			// 计算H_1
			if (ys == 0) {
				throw new ArithmeticException("分母为0");
			}
			double rou = 1.0 / ys;
			int m_hat = Math.min(itr, m);
			Matrix V = Matrix.identity(dimention).sub(
					y.dotProduct(s.transport()).multipleBy(rou));
			if (itr == m_hat) {
				rou_arr[m_hat - 1] = rou;
				s_arr[m_hat - 1] = s;
				V_arr[m_hat - 1] = V;
			} else {
				if (itr > m_hat + 1) {
					for (int i = 0; i < m_hat; i++) {
						rou_arr[i] = rou_arr[i + 1];
						s_arr[i] = s_arr[i + 1];
						V_arr[i] = V_arr[i + 1];
					}
				}
				rou_arr[m_hat] = rou;
				s_arr[m_hat] = s;
				V_arr[m_hat] = V;
			}

			Matrix[] successiveProduct = new Matrix[m_hat + 1];
			Matrix[] successiveProduct_T = new Matrix[m_hat + 1];
			successiveProduct[0] = V;
			successiveProduct_T[0] = V.transport();
			for (int i = m_hat - 1; i >= 0; i--) {
				successiveProduct[m_hat - i] = successiveProduct[m_hat - i - 1]
						.dotProduct(V_arr[i]);
				successiveProduct_T[m_hat - i] = successiveProduct_T[m_hat - i
						- 1].dotProduct(V_arr[i].transport());
			}
			Matrix part1 = successiveProduct_T[m_hat].dotProduct(H_0)
					.dotProduct(successiveProduct[m_hat]);
			Matrix part2 = Matrix.zero(dimention);
			for (int j = 0; j < m_hat; j++) {
				part2 = part2.add(successiveProduct_T[m_hat - j - 1]
						.dotProduct(s_arr[j]).dotProduct(s_arr[j].transport())
						.dotProduct(successiveProduct[m_hat - j - 1])
						.multipleBy(rou_arr[j]));
			}
			Matrix H_1 = part1.add(part2);

			// 更新X,g,H
			X = X_1;
			g = g_1;
			H = H_1;
		}
		System.out.println("L_BFGS算法迭代了" + itr + "次");

		return X;
	}

	public int getM() {
		return m;
	}

	public void setM(int m) {
		this.m = m;
	}

}
