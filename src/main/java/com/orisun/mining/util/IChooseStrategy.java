package com.orisun.mining.util;

/**
 * 
 * @Author:orisun
 * @Since:2016-4-25
 * @Version:1.0
 */
public interface IChooseStrategy {

	/**
	 * 根据一个随机数种子，返回一个策略编号
	 * 
	 * @param seed
	 * @return
	 */
	public int chooseStrategyBySeed(long seed);

	/**
	 * 根据userid或positionid选择对应的推荐策略
	 * 
	 * @param number
	 *            userid或positionid
	 * @return
	 */
	public int choose(int number);

	/**
	 * 根据既定的比例，随机选择一个策略，返回策略编号
	 * 
	 * @return
	 */
	public int choose();
}
