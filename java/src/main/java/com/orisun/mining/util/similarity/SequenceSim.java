package com.orisun.mining.util.similarity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @Description: 序列相似度计算
 * @Author orisun
 * @Date 2015-5-14 下午2:21:21
 */
public class SequenceSim {

	/**
	 * 计算两个序列的最长公共子串。比如"abcd6ef"和"ef6abcd"的最长公共子串是"abcd"
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static <E> List<E> longestCommonSubsequence1(List<E> s1, List<E> s2) {
		int i, j;
		int len1, len2;
		len1 = s1.size();
		len2 = s2.size();
		int maxLen = len1 > len2 ? len1 : len2;
		int[] max = new int[maxLen];
		int[] maxIndex = new int[maxLen];
		int[] c = new int[maxLen]; // 记录对角线上的相等值的个数

		for (i = 0; i < len2; i++) {
			for (j = len1 - 1; j >= 0; j--) {
				if (s2.get(i).equals(s1.get(j))) {
					if ((i == 0) || (j == 0))
						c[j] = 1;
					else
						c[j] = c[j - 1] + 1;
				} else {
					c[j] = 0;
				}

				if (c[j] > max[0]) { // 如果是大于那暂时只有一个是最长的,而且要把后面的清0;
					max[0] = c[j]; // 记录对角线元素的最大值，之后在遍历时用作提取子串的长度
					maxIndex[0] = j; // 记录对角线元素最大值的位置

					for (int k = 1; k < maxLen; k++) {
						max[k] = 0;
						maxIndex[k] = 0;
					}
				} else if (c[j] == max[0]) { // 有多个是相同长度的子串
					for (int k = 1; k < maxLen; k++) {
						if (max[k] == 0) {
							max[k] = c[j];
							maxIndex[k] = j;
							break; // 在后面加一个就要退出循环了
						}

					}
				}
			}
		}
		List<E> result = new ArrayList<E>();
		// 最长公共子串可能不止一个，打印出所有的最长公共子串
		// for (j = 0; j < maxLen; j++) {
		// if (max[j] > 0) {
		// System.out.print("第"+j+"个公共子串：");
		// for (i = maxIndex[j] - max[j] + 1; i <= maxIndex[j]; i++) {
		// result.add(s1.get(i));
		// System.out.print(s1.get(i));
		// }
		// System.out.println();
		// }
		// }
		// 只打印一个最长公共子串
		// System.out.println(maxIndex[0]+"\t"+max[0]);
		if (maxIndex.length > 0) {
			for (i = maxIndex[0] - max[0] + 1; i <= maxIndex[0]; i++) {
				result.add(s1.get(i));
			}
		}
		return result;
	}

	/**
	 * 计算两个序列的最长公共子序列。比如"a1b1c1d"和"22a2b2c2d"的最长公共子序列是"abcd"
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static <E> List<E> longestCommonSubsequence2(List<E> s1, List<E> s2) {
		int[][] num = new int[s1.size() + 1][];
		for (int i = 0; i < s1.size() + 1; i++) {
			num[i] = new int[s2.size() + 1];
			for (int j = 0; j < s2.size() + 1; j++) {
				num[i][j] = 0;
			}
		}
		for (int i = 1; i < s1.size() + 1; i++) {
			for (int j = 1; j < s2.size() + 1; j++) {
				if (s1.get(i - 1).equals(s2.get(j - 1))) {
					num[i][j] = 1 + num[i - 1][j - 1];
				} else {
					num[i][j] = Math.max(num[i - 1][j], num[i][j - 1]);
				}
			}
		}
		int s1position = s1.size(), s2position = s2.size();
		List<E> result = new ArrayList<E>();
		while (s1position > 0 && s2position > 0) {
			if (s1.get(s1position - 1).equals(s2.get(s2position - 1))) {
				result.add(s1.get(s1position - 1));
				s1position--;
				s2position--;
			} else if (num[s1position][s2position - 1] >= num[s1position - 1][s2position]) {
				s2position--;
			} else {
				s1position--;
			}
		}
		Collections.reverse(result);
		return result;
	}

	/**
	 * 最长公共子串
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static String longestCommonSubsequence1(String s1, String s2) {
		List<Character> l1 = new ArrayList<Character>();
		for (int i = 0; i < s1.length(); i++) {
			l1.add(s1.charAt(i));
		}
		List<Character> l2 = new ArrayList<Character>();
		for (int i = 0; i < s2.length(); i++) {
			l2.add(s2.charAt(i));
		}
		List<Character> lcs = longestCommonSubsequence1(l1, l2);
		StringBuilder sb = new StringBuilder();
		for (Character ch : lcs) {
			sb.append(ch);
		}
		return sb.toString();
	}

	/**
	 * 最长公共子序列
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static String longestCommonSubsequence2(String s1, String s2) {
		List<Character> l1 = new ArrayList<Character>();
		for (int i = 0; i < s1.length(); i++) {
			l1.add(s1.charAt(i));
		}
		List<Character> l2 = new ArrayList<Character>();
		for (int i = 0; i < s2.length(); i++) {
			l2.add(s2.charAt(i));
		}
		List<Character> lcs = longestCommonSubsequence2(l1, l2);
		StringBuilder sb = new StringBuilder();
		for (Character ch : lcs) {
			sb.append(ch);
		}
		return sb.toString();
	}

	/**
	 * 找出3个int值的最小者
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	private static int Minimum(int a, int b, int c) {
		int mi = a;
		if (b < mi) {
			mi = b;
		}
		if (c < mi) {
			mi = c;
		}
		return mi;
	}

	/**
	 * 求两个序列的编辑距离
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static <E> int getEditDistance(List<E> s1, List<E> s2) {
		int d[][]; // matrix
		int n; // length of s
		int m; // length of t
		int i; // iterates through s
		int j; // iterates through t
		E s_i; // ith character of s
		E t_j; // jth character of t
		int cost; // cost

		// Step 1

		n = s1.size();
		m = s2.size();
		if (n == 0) {
			return m;
		}
		if (m == 0) {
			return n;
		}
		d = new int[n + 1][m + 1];

		// Step 2

		for (i = 0; i <= n; i++) {
			d[i][0] = i;
		}

		for (j = 0; j <= m; j++) {
			d[0][j] = j;
		}

		// Step 3

		for (i = 1; i <= n; i++) {
			s_i = s1.get(i - 1);
			// Step 4
			for (j = 1; j <= m; j++) {
				t_j = s2.get(j - 1);
				// Step 5
				if (s_i.equals(t_j)) {
					cost = 0;
				} else {
					cost = 1;
				}
				// Step 6
				d[i][j] = Minimum(d[i - 1][j] + 1, d[i][j - 1] + 1,
						d[i - 1][j - 1] + cost);
			}
		}
		// Step 7
		return d[n][m];

	}

	/**
	 * 求两个序列的相似度（先计算距离，再由距离转换成相似度）
	 * 
	 * @param s1
	 * @param s2
	 * @param way
	 * @return
	 */
	public static <E> double getSim(List<E> s1, List<E> s2, SimWay way) {
		int len = Math.min(s1.size(), s2.size());
		double sim = 0.0;
		int commSize = 0;
		switch (way.ordinal()) {
		case 0:
			commSize = longestCommonSubsequence1(s1, s2).size();
			sim = 1.0 * commSize / len;
			break;
		case 1:
			commSize = longestCommonSubsequence2(s1, s2).size();
			sim = 1.0 * commSize / len;
			break;
		case 2:
			sim = 1 - (1.0 * getEditDistance(s1, s2) / len);
			break;
		default:
			sim = 1 - (1.0 * getEditDistance(s1, s2) / len);
			break;
		}
		return sim;
	}

	public static enum SimWay {
		LCS1, // 最找公共子串
		LCS2, // 最长公共子序列
		EDIT;// 编辑距离
	}

	public static void main(String[] args) {
		String str1 = "abcd6ef";
		String str2 = "ef6abcd";
		List<Character> l1 = new ArrayList<Character>();
		List<Character> l2 = new ArrayList<Character>();
		for (char ch : str1.toCharArray()) {
			l1.add(ch);
		}
		for (char ch : str2.toCharArray()) {
			l2.add(ch);
		}
		System.out.println(SequenceSim.longestCommonSubsequence1(l1, l2));
	}
}
