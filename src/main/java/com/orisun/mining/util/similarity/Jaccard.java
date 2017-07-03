package com.orisun.mining.util.similarity;  

import java.util.List;
  
public class Jaccard {
	/**
	 * 计算两个集合的Jaccard相似度<br>
	 * 注意：调用该方法两个List必须先从大到小排列好
	 * 
	 * @param list1
	 * @param list2
	 * @return
	 */
	public static double getJaccardSim(List<Integer> list1, List<Integer> list2) {
		double sim;
		int len1 = list1.size();
		int len2 = list2.size();
		int idx1 = 0;
		int idx2 = 0;
		int intersect = 0; // 交集大小
		while (idx1 < len1 && idx2 < len2) {
			int num1 = list1.get(idx1);
			int num2 = list2.get(idx2);
			if (num1 == num2) {
				intersect++;
				idx1++;
				idx2++;
			} else if (num1 < num2) {
				idx2++;
			} else if (num1 > num2) {
				idx1++;
			}
		}
		sim = 1.0 * intersect / (len1 + len2 - intersect); // 交集除以并集得到Jaccard相似度
		return sim;
	}

	/**
	 * 判断两个集合的Jaccard相似度是否达到给定的阈值<br>
	 * 注意：调用该方法两个List必须先从大到小排列好
	 * 
	 * @param list1
	 * @param list2
	 * @param thresh
	 *            Jaccard相似度阈值
	 * @return 如果达到阈值，则返回真实的相似度；否则返回-1
	 */
	public static double jaccardSimEnough(List<Integer> list1,
			List<Integer> list2, double thresh) {
		double sim = 0.0;
		int len1 = list1.size();
		int len2 = list2.size();
		int idx1 = 0;
		int idx2 = 0;
		int intersect = 0; // 交集大小
		int diffset = 0;// 差集大小
		double diffsetCeil = (1 - thresh) * (len1 + len2) / (1 + thresh);// Jaccard相似度要达到阈值的话，并集大小有一个上限
		while (idx1 < len1 && idx2 < len2) {
			int num1 = list1.get(idx1);
			int num2 = list2.get(idx2);
			if (num1 == num2) {
				intersect++;
				idx1++;
				idx2++;
			} else if (num1 < num2) {
				idx2++;
				diffset++;
			} else if (num1 > num2) {
				idx1++;
				diffset++;
			}
			if (diffset > diffsetCeil) {
				return -1;
			}
		}
		sim = 1.0 * intersect / (len1 + len2 - intersect); // 交集除以并集得到Jaccard相似度
		if (sim >= thresh) {
			return sim;
		} else {
			return -1;
		}
	}
}
