package com.orisun.mining.util.math.optimization;

import com.orisun.mining.util.exception.ArgumentException;
import com.orisun.mining.util.math.Vector;
import com.orisun.mining.util.math.function.Function;

/**
 * 共轭梯度法<br>
 * 本代码使用的各个变量的含义参见《非线性优化计算方法》袁亚湘著，第一版，第56页。
 *
 *@Author:zhangchaoyang 
 *@Since:2014-7-9  
 *@Version:
 */
public class ConjGrad extends MinOptimization {

	/**
	 * 求使用function取得极小值时的X.默认采用PRB方法求参数beta
	 * 
	 * @param function
	 * @param initX
	 *            给定X的初始值
	 * @return
	 * @throws ArgumentException
	 */
	public Vector getBestX(Function function, Vector initX)
			throws ArgumentException {
		return getBestX(function, initX, BetaWay.PRP);
	}

	/**
	 * 求使用function取得极小值时的X
	 * 
	 * @param function
	 * @param initX
	 *            给定X的初始值
	 * @param betaWay
	 *            采用何种方法求参数beta
	 * @return
	 * @throws ArgumentException
	 */
	public Vector getBestX(Function function, Vector initX, BetaWay betaWay)
			throws ArgumentException {
		int dimention = function.getDimension();
		if (initX.getDimention() != dimention) {
			throw new ArgumentException("X0的维度度不对");
		}
		Vector X = initX;
		Vector g = function.getGradient(initX);
		Vector d = g;

//		int itr = 0;
		while (g.norm2() > eps) {
//			itr++;
			// 计算X_1
			Vector X_1 = OptUtil.getNextX(function, X, d);
			// 计算g_1
			Vector g_1 = function.getGradient(X_1);
			double beta = 0;
			switch (betaWay.ordinal()) {
			case 0:
				beta = getBeta_FletcherReeves(g, g_1, d);
				break;
			case 1:
				beta = getBeta_PRP(g, g_1, d);
				break;
			case 2:
				beta = getBeta_HestenseStiefel(g, g_1, d);
				break;
			// case 3:
			// beta = getBeta_DaiYuan(g, g_1, d);
			// break;
			case 4:
				beta = getBeta_Fletcher(g, g_1, d);
				break;
			case 5:
				beta = getBeta_LiuStorey(g, g_1, d);
				break;
			}
			// 计算d_1
			Vector d_1 = d.multipleBy(beta).sub(g_1);

			// 更新X,g,d
			X = X_1;
			g = g_1;
			d = d_1;
		}
//		System.out.println("共轭梯度法迭代了" + itr + "次");

		return X;
	}

	private static double getBeta_FletcherReeves(Vector g, Vector g_1, Vector d)
			throws ArgumentException {
		return Math.pow(g_1.norm2(), 2) / Math.pow(g.norm2(), 2);
	}

	private static double getBeta_PRP(Vector g, Vector g_1, Vector d)
			throws ArgumentException {
		return g_1.sub(g).toMatrix().transport().dotProduct(g_1.toMatrix())
				.getArr()[0][0]
				/ Math.pow(g.norm2(), 2);
	}

	private static double getBeta_HestenseStiefel(Vector g, Vector g_1, Vector d)
			throws ArgumentException {
		return g_1.sub(g).toMatrix().transport().dotProduct(g_1.toMatrix())
				.getArr()[0][0]
				/ (d.dotProduct(g_1.sub(g)));
	}

	private static double getBeta_Fletcher(Vector g, Vector g_1, Vector d)
			throws ArgumentException {
		return g_1.sub(g).toMatrix().transport().dotProduct(g_1.toMatrix())
				.getArr()[0][0]
				/ (0 - d.dotProduct(g));
	}

	private static double getBeta_LiuStorey(Vector g, Vector g_1, Vector d)
			throws ArgumentException {
		return Math.pow(g_1.norm2(), 2) / (0 - d.dotProduct(g));
	}

	public enum BetaWay {
		FletcherReeves, //
		PRP, //
		HestenseStiefel, //
		// DaiYuan, //
		Fletcher, //
		LiuStorey;
	}
}
