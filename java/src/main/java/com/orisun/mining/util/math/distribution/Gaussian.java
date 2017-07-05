package com.orisun.mining.util.math.distribution;

/**
 * 高斯分布，或正态分布
 * 
 * @author orisun
 * @date 2016年12月16日
 */
public class Gaussian implements Distribution {

	/** 均值 **/
	final private double mu;
	/** 标准差 **/
	final private double sigma;
	final private double xMin;
	final private double xMax;
	final private double spanX;
	final private double yMin;
	final private double yMax;
	final private double ySpan;

	/**
	 * 概率密度函数
	 * 
	 * @param x
	 * @return
	 */
	public double pdf(double x) {
		return 1.0 / (Math.sqrt(2 * Math.PI) * sigma) * Math.exp(-Math.pow(x - mu, 2.0) / (2 * sigma * sigma));
	}

	public Gaussian(double mu, double sigma) {
		this.mu = mu;
		this.sigma = sigma;
		// 理论上x的取值范围是[-inf,inf]，但实际上绝大部分的x都位于[-4*sigma,4*sigma]之间
		this.xMin = -4 * this.sigma;
		this.xMax = 4 * this.sigma;
		this.spanX = this.xMax - this.xMin;
		this.yMin = 0;
		this.yMax = 1.0 / Math.sqrt(2 * Math.PI);
		this.ySpan = this.yMax - this.yMin;
	}

	/**
	 * Acceptance-Rejection Method抽样法算法步骤如下：<br>
	 * <ol>
	 * <li>设概率密度函数为f(x)，f(x)的定义域为[x_min,x_max]，值域为[y_min,y_max]
	 * <li>独立生成2个服从均匀分布的随机变量，X~Uni(x_min,x_max)，Y~Uni(y_min,y_max)
	 * <li>如果Y<=f(X)，则返回X；否则回到第1步
	 * </ol>
	 */
	@Override
	public double drawOnePoint() {
		while (true) {
			double X = xMin + spanX * Math.random();
			double Y = yMin + ySpan * Math.random();
			if (Y < pdf(X)) {
				return X;
			}
		}
	}

	@Override
	public double getExpection() {
		return mu;
	}

	@Override
	public double getVariance() {
		return sigma;
	}

}
