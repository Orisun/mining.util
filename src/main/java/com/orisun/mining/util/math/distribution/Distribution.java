package com.orisun.mining.util.math.distribution;

/**
 * 概率分布
 *
 *@Author:zhangchaoyang 
 *@Since:2014-7-9  
 *@Version:
 */
public interface Distribution {

	/**
	 * 从分布中随机抽取一个点
	 * @return
	 */
	public double drawOnePoint();

	/**
	 * 分布的期望
	 * @return
	 */
	public double getExpection();

	/**
	 * 分布的方差
	 * @return
	 */
	public double getVariance();
}
