package com.orisun.mining.util.math;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 离散化方法
 * 
 * @Author:zhangchaoyang
 * @Since:2014-8-9
 * @Version:
 */
public class Discretize {

	/**
	 * 等宽分箱法
	 * 
	 * @param data
	 *            原始数据
	 * @param piece
	 *            分成多少份
	 * @return 分割点
	 */
	public static List<Double> binConstWidth(List<Double> data, int piece) {
		List<Double> splitPoint = new ArrayList<Double>();
		if (piece >= 2 && data != null && data.size() > 0) {
			Collections.sort(data);
			double min = data.get(0);
			double max = data.get(data.size() - 1);
			double step = (max - min) / piece;

			for (int i = 1; i < piece; i++) {
				double point = min + i * step;
				splitPoint.add(point);
			}
		}
		return splitPoint;
	}

	/**
	 * 等深分箱法
	 * 
	 * @param data
	 *            原始数据
	 * @param piece
	 *            分成多少份
	 * @return 分割点
	 */
	public static List<Double> binConstDepth(List<Double> data, int piece) {
		List<Double> splitPoint = new ArrayList<Double>();
		if (piece >= 2 && data != null && data.size() > 0) {
			Collections.sort(data);
			int len = data.size();
			double step = 1.0 * len / piece;

			for (int i = 1; i < piece; i++) {
				splitPoint.add(data.get((int) (i * step - 1)));
			}
		}
		return splitPoint;
	}

	/**
	 * N个分割点，把实数域分为N+1段，每一段都是前开后闭区间。区间编号从0开始，到N。
	 * 
	 * @param splitPoint
	 *            分箱点。每一个区间都是前开后闭。
	 * @param data
	 *            数据
	 * @return 返回数据data所在的区段编号。
	 */
	public static int getIndexOfBin(final List<Double> splitPoint, double data) {
		// splitPoint已经是从小到大排好序的，查找data所在的区间可以采用折半查找法
		int LEN = splitPoint.size();
		int rect = -1;
		if (data > splitPoint.get(LEN - 1)) {
			rect = LEN;
		} else if (data <= splitPoint.get(0)) {
			rect = 0;
		} else {
			int low = 0;
			int high = LEN - 1;
			while (high >= low) {
				int mid = (low + high) / 2;
				if (data <= splitPoint.get(mid)) {
					rect = mid;
					high = mid - 1;
				} else {
					low = mid + 1;
				}
			}
		}
		return rect;
	}
}
