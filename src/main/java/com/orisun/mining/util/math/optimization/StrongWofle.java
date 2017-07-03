package com.orisun.mining.util.math.optimization;

import com.orisun.mining.util.exception.ArgumentException;
import com.orisun.mining.util.math.Vector;
import com.orisun.mining.util.math.function.Function;


/**
 * 强Wofle，一维非精确线性搜索方法。在大多数情况下Wofle算法有解。<br>
 * 本代码使用的各个变量的含义参见《非线性优化计算方法》袁亚湘著，第一版，第40页，算法2.5.4，强Wofle搜索。
 *
 *@Author:zhangchaoyang 
 *@Since:2014-7-9  
 *@Version:
 */
public class StrongWofle {

	private static double b1 = 0.1;
	private static double b2 = 0.5;
	private static double tao = 0.1;
	private static double eps = 1E-8;

	/**
	 * 给定X点和优化方向，求步长
	 * 
	 * @param function
	 *            要优化的目标函数
	 * @param X
	 *            当前X的位置
	 * @param direction
	 *            X的前进方向
	 * @throws ArgumentException
	 */
	public static double getStepsize(Function function, Vector X,
			Vector direction) throws ArgumentException {

		if (!(function.getDimension() == X.getDimention() && function
				.getDimension() == direction.getDimention())) {
			throw new ArgumentException("数组长度不对");
		}

		double alpha = 1.0;// 步长
		double alpha1 = 0.0;
		double alpha2 = Double.MAX_VALUE;
		double f;
		double f_g;
		double f1 = function.getValue(X);
//		System.out.print("当前函数值=" + f1 + "\t");
		double f1_g = direction.dotProduct(function.getGradient(X));
		// double f2;
		double f2 = 0.0;
		double f2_g = -1.0;

//		int itr = 0;
		while (true) {
//			itr++;
			f = function.getValue(getNextX(X, direction, alpha));
			// 满足准则1，则转步骤4
			if (norm1(f1, f, alpha, b1, f1_g)) {
				f_g = direction.dotProduct(function.getGradient(getNextX(X,
						direction, alpha)));
				// 满足准则2，则算法终止
				if (norm2(f_g, b2, f1_g)) {
					break;
				}
				// 转步骤6
				else if (f_g < 0) {
					// 转步骤7
					if (alpha2 == Double.MAX_VALUE) {
						double beta = 2 * f_g + f1_g - 3 * (f1 - f)
								/ (alpha1 - alpha);
						double alpha_hat = alpha
								- f_g
								* (alpha - alpha1)
								/ (Math.sqrt(Math.pow(beta - f_g, 2) - f1_g
										* f_g) + beta);
						alpha_hat = Math.min(Math.max(alpha, alpha - alpha1),
								10 * alpha - alpha1);
						alpha1 = alpha;
						f1 = f;
						f1_g = f_g;
						if (Math.abs(alpha_hat - alpha) < eps) {
							break;
						}
						alpha = alpha_hat;
						// 转步骤2，即进行下一次迭代
						continue;
					}
					alpha1 = alpha;
					f1 = f;
					f1_g = f_g;
					// 转步骤5
					if (f2_g > 0) {
						double beta = 2 * f1_g + f2_g - 3 * (f2 - f1)
								/ (alpha2 - alpha1);
						double alpha_hat = alpha1
								- f1_g
								* (alpha2 - alpha1)
								/ (Math.sqrt(Math.pow(beta - f1_g, 2) - f1_g
										* f2_g) - beta);
						alpha_hat = Math.min(
								Math.max(alpha_hat, alpha1 + tao
										* (alpha2 - alpha1)), alpha2 - tao
										* (alpha2 - alpha1));
						if (Math.abs(alpha_hat - alpha) < eps) {
							break;
						}
						alpha = alpha_hat;
						// 转步骤2，即进行下一次迭代
						continue;
					}
					// 转步骤3
					else {
						double beta = (f1 - f2) / ((alpha1 - alpha2) * f1_g);
						double alpha_hat = alpha1 + (alpha2 - alpha1)
								/ (2 * (1 + beta));
						alpha_hat = Math.min(
								Math.max(alpha_hat, alpha1 + tao
										* (alpha2 - alpha1)), alpha2 - tao
										* (alpha2 - alpha1));
						if (Math.abs(alpha_hat - alpha) < eps) {
							break;
						}
						alpha = alpha_hat;
						// 转步骤2，即进行下一次迭代
						continue;
					}
				}
				// 不满足准则2，且不满足f_g<0，则继续往下走，执行步骤5
				alpha2 = alpha;
				f2 = f;
				f2_g = f_g;

				double beta = 2 * f1_g + f2_g - 3 * (f2 - f1)
						/ (alpha2 - alpha1);
				double alpha_hat = alpha1
						- f1_g
						* (alpha2 - alpha1)
						/ (Math.sqrt(Math.pow(beta - f1_g, 2) - f1_g * f2_g) - beta);
				alpha_hat = Math.min(
						Math.max(alpha_hat, alpha1 + tao * (alpha2 - alpha1)),
						alpha2 - tao * (alpha2 - alpha1));
				if (Math.abs(alpha_hat - alpha) < eps) {
					break;
				}
				alpha = alpha_hat;
				// 转步骤2，即进行下一次迭代
				continue;
			}
			// 不满足准则1，则继续往下走，执行步骤3
			alpha2 = alpha;
			f2 = f;
			double beta = (f1 - f2) / ((alpha2 - alpha1) * f1_g);
			double alpha_hat = alpha1 + (alpha2 - alpha1) / (2 * (1 + beta));
			alpha_hat = Math.min(
					Math.max(alpha_hat, alpha1 + tao * (alpha2 - alpha1)),
					alpha2 - tao * (alpha2 - alpha1));
			if (Math.abs(alpha_hat - alpha) < eps) {
				break;
			}
			alpha = alpha_hat;
			// 转步骤2，即进行下一次迭代
			continue;
		}
//		System.out.println("StrongWofle算法迭代了" + itr + "次");

		return alpha;
	}

	/**
	 * 延特定的方向更新X
	 * 
	 * @param X
	 * @param direction
	 * @param step
	 * @return
	 */
	private static Vector getNextX(Vector X, Vector direction, double step) {
		double[] X_new = new double[X.getDimention()];
		for (int i = 0; i < X.getDimention(); i++) {
			X_new[i] = X.getArray()[i] + step * direction.getArray()[i];
		}
		return new Vector(X_new);
	}

	/**
	 * 准则1，即书中的公式(2.5.8)
	 * 
	 * @param oldFunc
	 * @param newFunc
	 * @param alpha
	 * @param b1
	 * @param f1_g
	 * @return
	 */
	private static boolean norm1(double oldFunc, double newFunc, double alpha,
			double b1, double f1_g) {
		return (oldFunc - newFunc) >= 0 - alpha * b1 * f1_g;
	}

	/**
	 * 准则2，即书中的公式(2.5.12)
	 * 
	 * @param f_g
	 * @param b2
	 * @param f1_g
	 * @return
	 */
	private static boolean norm2(double f_g, double b2, double f1_g) {
		return Math.abs(f_g) <= 0 - b2 * f1_g;
	}

	public static double getB1() {
		return b1;
	}

	public static void setB1(double b1) {
		StrongWofle.b1 = b1;
	}

	public static double getB2() {
		return b2;
	}

	public static void setB2(double b2) {
		StrongWofle.b2 = b2;
	}

	public static double getTao() {
		return tao;
	}

	public static void setTao(double tao) {
		StrongWofle.tao = tao;
	}

	public static double getEps() {
		return eps;
	}

	public static void setEps(double eps) {
		StrongWofle.eps = eps;
	}

}
