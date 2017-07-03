package com.orisun.mining.util.math;

import com.orisun.mining.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 一些零散琐碎的数学计算方法
 * 
 * @Author:zhangchaoyang
 * @Since:2014-8-11
 * @Version:
 */
public class CommonMath {
	/**
	 * 给一个样本（数组中是各种情况的计数），计算它的熵
	 * 
	 * @param arr
	 * @return
	 */
	public static double getEntropy(int[] arr) {
		double entropy = 0.0;
		int sum = 0;
		for (int i = 0; i < arr.length; i++) {
			entropy -= arr[i] * Math.log(arr[i] + Double.MIN_VALUE)
					/ Math.log(2);
			sum += arr[i];
		}
		entropy += sum * Math.log(sum + Double.MIN_VALUE) / Math.log(2);
		entropy /= sum;
		return entropy;
	}

	/**
	 * 给一个样本数组及样本的算术和，计算它的熵
	 * 
	 * @param arr
	 * @param sum
	 * @return
	 */
	public static double getEntropy(int[] arr, int sum) {
		double entropy = 0.0;
		for (int i = 0; i < arr.length; i++) {
			entropy -= arr[i] * Math.log(arr[i] + Double.MIN_VALUE)
					/ Math.log(2);
		}
		entropy += sum * Math.log(sum + Double.MIN_VALUE) / Math.log(2);
		entropy /= sum;
		return entropy;
	}

	/**
	 * 给定一个集合，计算两两之间的组合
	 * 
	 * @param list
	 * @return
	 */
	public static <T> List<Pair<T, T>> getCombine(List<T> list) {
		List<Pair<T, T>> rect = Collections.emptyList();
		if (list != null && list.size() > 0) {
			rect = new ArrayList<Pair<T, T>>();
			int len = list.size();
			for (int i = 0; i < len - 1; i++) {
				for (int j = i + 1; j < len; j++) {
					rect.add(Pair.of(list.get(i), list.get(j)));
				}
			}
		}
		return rect;
	}

}
