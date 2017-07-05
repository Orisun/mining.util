package com.orisun.mining.util.math.distribution;

/**
 * 指数分布
 * 
 * @author orisun
 * @date 2016年12月16日
 */
public class Exponent implements Distribution {

	private double lambda;

	public Exponent(double lambda) {
		assert lambda > 0;
		this.lambda = lambda;
	}

	/**
	 * Inverse Transform Method抽样法步骤如下：<br>
	 * <ol>
	 * <li>生成(0,1)上的服务均匀分布的随机数U~Uni(0,1)
	 * <li>设F(X)为特定分布的累积分布函数，F^{-1}(Y)为其逆函数，则F^{-1}(U)就是来自特定分布的样本
	 * </ol>
	 * 
	 * @return
	 */
	@Override
	public double drawOnePoint() {
		double u = Math.random();// 返回[0,1)上的均匀分布的随机数
		while (u == 0.0) {
			u = Math.random();// 确保u不等于0
		}
		return -1.0 / lambda * Math.log(u);
	}

	@Override
	public double getExpection() {
		return 1.0 / lambda;
	}

	@Override
	public double getVariance() {
		return 1.0 / (lambda * lambda);
	}

}
