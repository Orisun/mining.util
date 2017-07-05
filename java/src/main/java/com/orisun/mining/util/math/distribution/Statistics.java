package com.orisun.mining.util.math.distribution;

import com.orisun.mining.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @Description:计算样本的一些统计量
 * @Author orisun
 * @Date 2016年8月16日
 */
public class Statistics {

	/**
	 * 计算Gini系数
	 * 
	 * @param list
	 *            每个元素都不能为负
	 * @return
	 */
	public double getGini(List<Double> list) {
		double gini = 0.0;
		if (list != null && list.size() > 0) {
			int n = list.size();
			// 从小到大排序
			Collections.sort(list);
			// 从第0份到第i份的累加和
			double[] accumulate = new double[n];
			int i = 0;
			for (Double ele : list) {
				//
				assert ele >= 0;
				accumulate[i] = ele;
				if (i > 0) {
					accumulate[i] += accumulate[i - 1];
				}
				i++;
			}
			// 如果总和为0，说明每个元素都是0，绝对均匀，则Gini系数为1
			if (accumulate[n - 1] == 0) {
				return 1.0;
			}
			// Gini系数的计算公式
			for (i = 0; i < n - 1; i++) {
				gini += accumulate[i] / accumulate[n - 1];
			}
			gini = 1 - (2 * gini + 1) / n;
		}
		return gini;
	}

	/**
	 * 计算样本均值
	 * 
	 * @param list
	 * @return
	 */
	public double getMean(List<Double> list) {
		double mean = 0.0;
		if (list != null && list.size() > 0) {
			int n = list.size();
			double sum = 0.0;
			for (Double ele : list) {
				sum += ele;
			}
			mean = sum / n;
		}
		return mean;
	}

	/**
	 * 同时计算样本均值和方差
	 * 
	 * @param list
	 * @return pair.first为均值，pair.second为方差
	 */
	public Pair<Double, Double> getMeanAndVariance(List<Double> list) {
		if (list == null || list.size() == 0) {
			return Pair.of(0.0, 0.0);
		}
		int n = list.size();
		double sum = 0;
		double sumSquare = 0;
		for (double ele : list) {
			sum += ele;
			sumSquare += ele * ele;
		}
		double mean = sum / n;
		double variance = sumSquare / n - mean * mean;
		return Pair.of(mean, variance);
	}

	/**
	 * 计算n分位点
	 * 
	 * @param list
	 * @param n
	 *            要分成多少份
	 * @return i/n分位点，i=[1,2,...,n]
	 */
	public List<Double> getQuantile(List<Double> list, int n) {
		if (list == null || list.size() == 0 || n <= 0) {
			return null;
		}
		int dimention = list.size();
		// 样本数目不能少于份数
		if (dimention < n) {
			return null;
		}
		List<Double> rect = new ArrayList<Double>(n);

		List<Double> data = new LinkedList<Double>();
		for (double ele : list) {
			data.add(ele);
		}
		Collections.sort(data);
		double interval = 1.0 * dimention / n;
		for (int i = 1; i <= n; i++) {
			int index = Math.min((int) Math.round(i * interval), dimention - 1);
			rect.add(data.get(index));
		}
		return rect;
	}
}
