package com.orisun.mining.util.math;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 数据平滑
 * 
 * @Author:zhangchaoyang
 * @Since:2014-8-13
 * @Version: 1.0
 */
public class Smooth {

	/**
	 * 拉普拉斯加1平滑算法
	 * 
	 * @param arr
	 *            原始计数
	 * @return 平滑之后的概率值
	 */
	public static double[] Laplasse(int[] arr) {
		return Laplasse(arr, 1);
	}

	/**
	 * 拉普拉斯加mu平滑算法
	 * 
	 * @param arr
	 *            原始计数
	 * @param mu
	 * @return 平滑之后的概率值
	 */
	public static double[] Laplasse(int[] arr, int mu) {
		int len = arr.length;
		double[] probability = new double[len];
		int sum = 0;
		for (int anArr : arr) {
			sum += anArr;
		}
		sum += mu;
		for (int i = 0; i < len; i++) {
			probability[i] = (arr[i] + 1.0 * mu / len) / sum;// 算法核心部分
		}
		return probability;
	}

	/**
	 * Good-Turing平滑算法
	 * 
	 * @param arr
	 *            原始计数
	 * @return 平滑之后的概率值
	 */
	public static double[] GoodTuring(int[] arr) {
		int len = arr.length;

		Set<Integer> numberSet = new HashSet<Integer>();// 存储各种互不相同的计数
		Map<Integer, Integer> numberFrequency = new HashMap<Integer, Integer>();// 每种计数出现多少次
		for (int number : arr) {
			Integer freq = numberFrequency.get(number);
			if (freq != null) {
				numberFrequency.put(number, freq + 1);
			} else {
				numberFrequency.put(number, 1);
				numberSet.add(number);
			}
		}

		Map<Integer, Double> newNumberMap = new HashMap<Integer, Double>();// 原始计数到新计数的映射关系
		for (Integer number : numberSet) {
			double newNumber = 1.0 * (number + 1)
					* (numberFrequency.get(number) + 1)
					/ numberFrequency.get(number);// 算法核心部分
			newNumberMap.put(number, newNumber);
		}

		double[] count = new double[len];// Good-Turing变换之后新的计数
		for (int i = 0; i < len; i++) {
			int number = arr[i];
			count[i] = newNumberMap.get(number);
//			System.out.print(count[i] + "\t");
		}
//		System.out.println();

		double[] probability = new double[len];
		double sum = 0;
		for (int i = 0; i < len; i++) {
			sum += count[i];
		}
		for (int i = 0; i < len; i++) {
			probability[i] = count[i] / sum;
		}
		return probability;
	}
}
